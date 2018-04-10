package com.doyd.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.doyd.biz.IFilesService;
import com.doyd.biz.IHomeworkService;
import com.doyd.core.action.common.Page;
import com.doyd.core.biz.impl.MyTransSupport;
import com.doyd.dao.IHomeworkDao;
import com.doyd.dao.IReadWorkDao;
import com.doyd.dao.ISubjectDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.model.Files;
import com.doyd.model.Homework;
import com.doyd.model.ReadWork;
import com.doyd.model.Subject;
import com.doyd.model.UserGroup;
import com.doyd.model.WeixinGroup;
import com.doyd.model.WeixinUser;
import com.doyd.module.pc.utils.FileUtil;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import org.doyd.utils.DateUtil;
import org.doyd.utils.StringUtil;

@Service
public class HomeworkService extends MyTransSupport implements IHomeworkService {
	@Autowired
	private IHomeworkDao homeworkDao;
	@Autowired
	private IFilesService filesService;
	@Autowired
	private IWeixinGroupDao weixinGroupDao;
	@Autowired
	private IUserGroupDao userGroupDao;
	@Autowired
	private IReadWorkDao readWorkDao;
	@Autowired
	private IWeixinUserDao weixinUserDao;
	@Autowired
	private ISubjectDao subjectDao;
	
	private String STORAGETABLE = "homework";
	private String FIELD1 = "newWork";
	private String FIELD2 = "totalWork"; 
	private String JOINFIELD = "workId";
	private String READTABLE = "read_work";
	
	private static String subj = "语文，数学，英语，物理，化学，生物，政治，地理，历史，音乐，美术，体育";

	@Override
	public ApiMessage queryWorkByPage(Page page, String openGId, int type, int wuId, String key, String beginDate, String endDate, ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		
		UserGroup userGroup = userGroupDao.getUserGroup(wuId, group.getGroupId());
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		List<Map<String, Object>> workList = homeworkDao.queryWorkByPage(page, userGroup.getGroupId(), type, userGroup.isDirector(), wuId, key, beginDate, endDate);
		if(workList!=null && workList.size()>0){
			for (Map<String, Object> work : workList) {
				List<Map<String, Object>> fileList = null;
				if(work.get("storageTable")!=null) {
					int id = StringUtil.parseIntByObj(work.get("workId"));
					String storageTable = (String) work.get("storageTable");
					fileList = filesService.getFiles(storageTable, id, STORAGETABLE);
				}
				work.put("files", fileList);
				work.remove("storageTable");
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", workList);
		map.put("totalPage", page.getTotalPage());
		return new ApiMessage(reqCode, ReqState.Success).setInfo(map);
	}
	
	@Override
	public ApiMessage queryWorkByPage(int page, String openId, String openGId, String identity, int key, ReqCode reqCode){
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(openGId) || page <= 0 
				|| StringUtil.isEmpty(identity) || key < 0 || key > 2){
			return new ApiMessage(reqCode, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		//用户群关系
		UserGroup ug = userGroupDao.getUserGroup(wu.getWuId(), wg.getGroupId());
		if(ug == null){
			return new ApiMessage(reqCode, ReqState.UserNotInGroup);
		}
		//如果页面传入班主任但用户群内不存在班主任--等
		if(!(("director".equals(identity) && ug.isDirector()) 
				|| ("teacher".equals(identity) && ug.isTeacher()))){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		List<Map<String, Object>> hwList = homeworkDao.queryWorkByPage(page, wg.getGroupId(), "director".equals(identity), wu.getWuId(), key);
		if (hwList != null && hwList.size() > 0) {
			for (Map<String, Object> map :
					hwList) {
				String content = (String) map.get("work");
				if(StringUtil.isNotEmpty(content)){
					map.put("work", StringUtil.isEmpty(content) ? null : content.replaceAll("&nbsp;", " ").replaceAll("<br/>", "\\\n"));
				}
			}
		}
		return new ApiMessage(reqCode, ReqState.Success).setInfo(hwList); 
	}

	@Override
	public ApiMessage showWork(int workId, WeixinUser weixinUser, ReqCode reqCode) {
		Homework work = homeworkDao.getHomework(workId);
		if(work==null){
			return new ApiMessage(reqCode, ReqState.NoExistWork);
		}
		UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), work.getGroupId());
		if(userGroup==null){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		//不是班主任且不是老师
		if(!userGroup.isDirector() && !userGroup.isTeacher()){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		//是老师但不是自己
		if(userGroup.isTeacher() && userGroup.getWuId()!=work.getWuId()){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		//附件
		List<Map<String, Object>> fileList = StringUtil.isNotEmpty(work.getStorageTable())?filesService.getFiles(work.getStorageTable(), work.getWorkId(), STORAGETABLE):null;
		
		//已读家长列表
		List<Map<String, Object>> readUserList = readWorkDao.getReadUserList(userGroup.getGroupId(), workId);
		if(readUserList==null){
			readUserList = new ArrayList<Map<String,Object>>();
		}
		readWork(weixinUser, userGroup, work);
		//群内所有家长
		List<Map<String, Object>> allUserList = userGroupDao.getAllUserList(userGroup.getGroupId());
		//未读家长列表
		List<Map<String, Object>> noReadUserList = new ArrayList<Map<String,Object>>();
		if(allUserList!=null && allUserList.size()>0){
			for (Map<String, Object> user : allUserList) {
				if(!readUserList.contains(user)){
					noReadUserList.add(user);
				}
			}
		}
		WeixinUser wu = weixinUserDao.getUserById(work.getWuId());
		String name = wu != null ? wu.getRealName() : "";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("costTime", work.getCostTime());
		map.put("ctime", work.getCtime());
		map.put("files", fileList);
		map.put("name", name);
		map.put("notReadCnt", noReadUserList.size());
		map.put("notReadUserList", noReadUserList);
		map.put("readCnt", readUserList.size());
		map.put("readUserList", readUserList);
		map.put("subject", work.getSubject());
		map.put("submitTime", work.getSubmitTime());
		map.put("work", work.getWork());
		return new ApiMessage(reqCode, ReqState.Success).setInfo(map);
	}
	
	@Override
	public ApiMessage showWork(String openId, int workId){
		if(StringUtil.isEmpty(openId) || workId <= 0){
			return new ApiMessage(ReqCode.ShowWork, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.ShowWork, ReqState.NoExistUser);
		}
		Homework work = homeworkDao.getHomework(workId);
		if(work == null){
			return new ApiMessage(ReqCode.ShowWork, ReqState.NoExistWork);
		}
		UserGroup ug = userGroupDao.getUserGroup(wu.getWuId(), work.getGroupId());
		//用户不存在群关系或者，不是班主任和老师，或者老师身份作业不是自己的
		if(ug == null){
			return new ApiMessage(ReqCode.ShowWork, ReqState.NoPower);
		}
		readWork(wu, ug, work);
		//附件
		List<Map<String, Object>> fileList = StringUtil.isNotEmpty(work.getStorageTable())?filesService.getFiles(work.getStorageTable(), work.getWorkId(), STORAGETABLE):null;
		
		WeixinUser user = weixinUserDao.getUserById(work.getWuId());
		String realName = user != null ? user.getRealName() : "";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("costTime", work.getCostTime());
		map.put("existPower", work.getWuId() == wu.getWuId() || ug.isDirector());
		map.put("ctime", work.getCtime());
		map.put("files", fileList);
		map.put("readCnt", work.getReadCnt());
		map.put("realName", realName);
		map.put("subject", work.getSubject());
		map.put("submitTime", work.getSubmitTime());
		map.put("work", StringUtil.isEmpty(work.getWork()) ? null : work.getWork().replaceAll("&nbsp;", " ").replaceAll("<br/>", "\\\n"));
		return new ApiMessage(ReqCode.ShowWork, ReqState.Success).setInfo(map);
	}
	
	private void readWork(WeixinUser wu, UserGroup ug, Homework work){ 
		//如果不存在阅读记录
		if(!readWorkDao.isRead(work.getWorkId(), wu.getWuId())){
			Date now = new Date();
			ReadWork rw = new ReadWork();
			rw.setGroupId(work.getGroupId());
			rw.setWorkId(work.getWorkId());
			rw.setWuId(wu.getWuId());
			//   （当前时间戳 - 作业发布毫秒数）/ 1000毫秒（等于秒） / 60秒 （等于分钟）
			int interval = (int) ((now.getTime() - DateUtil.convertToDate(work.getCtime()).getTime()) / 1000 / 60);
			rw.setInterval(interval);
			int readDate = StringUtil.parseInt(DateUtil.convertToString(now, "yyyyMMdd"));
			rw.setReadDate(readDate);
			rw.setReadHour(DateUtil.getHour());
			TransactionStatus txStatus = getTxStatus();
			boolean r = false;
			try {
				//添加阅读记录
				r = readWorkDao.addReadWork(rw);
				if(r && ug.isPatriarch() && ug.getNewWork() > 0){
					//减少气泡
					r = userGroupDao.downCnt(wu.getWuId(), work.getGroupId(), "newWork");
				}
				//只有家长阅读才增加阅读人数
				if(r && !ug.isTeacher() && !ug.isDirector()){
					//添加作业阅读人数
					r = homeworkDao.readHomework(work.getWorkId());
				}
			} catch (Exception e) {
				r = false;
			}finally{
				commit(txStatus, r);
			}
		}
	}
	
	@Override
	public ApiMessage createWork(WeixinUser weixinUser, String openGId,
			String subject, String submitTime, String work, int costTime,
			JSONArray files, ReqCode reqCode) {
		try{
		
			WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
			if(group==null){
				return new ApiMessage(reqCode, ReqState.NoExistGroup);
			}
			
			UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), group.getGroupId());
			if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
				return new ApiMessage(reqCode, ReqState.NoPower);
			}
			
			Subject sub = null;
			if(!subj.contains(subject) && !subjectDao.existSubject(weixinUser.getWuId(), subject)){
				sub = new Subject();
				sub.setWuId(weixinUser.getWuId());
				sub.setSubject(subject);
			}
			
			Homework homework = new Homework();
			homework.setCostTime(costTime);
			homework.setFileCnt(files==null?0:files.length());
			homework.setGroupId(userGroup.getGroupId());
			homework.setSubject(subject);
			homework.setSubmitTime(submitTime);
			homework.setUgId(userGroup.getUgId());
			homework.setWork(work);
			homework.setWuId(weixinUser.getWuId());
			//TODO 现在都存入files表里头，以后要备份的时候怎么搞？
			homework.setStorageTable("files");
			List<Files> fileList = new ArrayList<Files>();
			if(files!=null && files.length()>0){
				for (int i = 0; i < files.length(); i++) {
					JSONObject fileObj = files.getJSONObject(i);
					String fileAddr = fileObj.getString("fileAddr");
					String fileName = fileObj.getString("fileName");
					Files file = FileUtil.createFile(userGroup.getGroupId(), fileAddr, fileName, STORAGETABLE);
					fileList.add(file);
				}
			}
			boolean r = true;
			TransactionStatus status = getTxStatus();
			try{
				r = r && homeworkDao.addWork(homework);
				r = r && filesService.batchAddFiles(fileList, homework.getWorkId());
				r = r && userGroupDao.upCnt(group.getGroupId(), FIELD1, 3);
				r = r && weixinGroupDao.upCnt(group.getGroupId(), FIELD2);
				if(r && sub != null){
					r = subjectDao.addSubject(sub);
				}
			}catch (Exception e) {
				e.printStackTrace();
				r = false;
			}finally{
				commit(status, r);
			}
			if(!r){
				return new ApiMessage(reqCode, ReqState.Failure);
			}
			return new ApiMessage(reqCode, ReqState.Success).setInfo(homework.getWorkId());
		
		}catch (Exception e) {
			e.printStackTrace();
			return new ApiMessage(reqCode, ReqState.Failure);
		}
	}
	
	@Override
	public ApiMessage deleteWork(WeixinUser weixinUser, String openGId, int[] workIds,
			ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		//操作者群关系
		UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), group.getGroupId());
		//跟此群无关系或者不是班主任且不是老师无权删除
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		List<Homework> workList = homeworkDao.getHomeworkList(userGroup.isDirector(), weixinUser.getWuId(), workIds);
		if(workList==null || workList.size()!=workIds.length){
			return new ApiMessage(reqCode, ReqState.ExistNoPowerItem);
		}
		Map<String, List<Integer>> fileMap = new HashMap<String, List<Integer>>();
		for (Homework work : workList) {
			if(work.getStorageTable()==null) continue;
			List<Integer> workIdList = fileMap.get(work.getStorageTable());
			if(workIdList==null){
				workIdList = new ArrayList<Integer>();
			}
			workIdList.add(work.getWorkId());
			fileMap.put(work.getStorageTable(), workIdList);
		}
		boolean r = true;
		TransactionStatus status = getTxStatus();
		try{
			r = r && homeworkDao.deleteWork(workIds, userGroup.getGroupId(), userGroup.isDirector(), weixinUser.getWuId());
			for(String storageTable: fileMap.keySet()){
				r = r && filesService.batchDeleteFiles(storageTable, STORAGETABLE, fileMap.get(storageTable));
			}
			for(int workId : workIds){
				r = r && userGroupDao.downCnt(userGroup.getGroupId(), FIELD1, READTABLE, JOINFIELD, workId);
			}
			r = r && weixinGroupDao.downCnt(group.getGroupId(), FIELD2, workIds.length);
		}catch (Exception e) {
			e.printStackTrace();
			r = false;
		}finally{
			commit(status, r);
		}
		if(!r){
			return new ApiMessage(reqCode, ReqState.Failure);
		}
		return new ApiMessage(reqCode, ReqState.Success);
	}
	
	@Override
	public ApiMessage getWorkCnt(WeixinUser weixinUser, String openGId,
			int type, ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), group.getGroupId());
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		int workCnt = homeworkDao.getWorkCnt(userGroup.getGroupId(), weixinUser.getWuId(), type);
		return new ApiMessage(reqCode, ReqState.Success).setInfo(workCnt);
	}

	@Override
	public ApiMessage homework_notify(String openId, String openGId, int page) {
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(openGId) || page <= 0){
			return new ApiMessage(ReqCode.HomeworkNotify, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.HomeworkNotify, ReqState.NoExistUser);
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return new ApiMessage(ReqCode.HomeworkNotify, ReqState.NoExistGroup);
		}
		UserGroup ug = userGroupDao.getUserGroup(wu.getWuId(), wg.getGroupId());
		//没有用户群关系，或者没有家长身份
		if(ug == null || !ug.isPatriarch()){
			return new ApiMessage(ReqCode.HomeworkNotify, ReqState.NoPower);
		}
		List<Map<String, Object>> hcList = homeworkDao.getHomework_notify(wg.getGroupId(), wu.getWuId(), page);
		if(hcList != null && hcList.size() > 0){
			for (Map<String, Object> map : hcList) {
				String subject = (String) map.get("subject");
				map.remove("subject");
				String subjects = (String) map.get("subjects");
				map.remove("subjects");
				Boolean director = (Boolean) map.get("director");
				map.remove("director");
				Boolean teacher = (Boolean) map.get("teacher");
				map.remove("teacher");
				String submitTime = (String) map.get("submitTime");
				map.remove("submitTime");
				int msgType = StringUtil.parseIntByObj(map.get("msgType"));
				//编辑作业标题
				if(msgType == 1){
					int month = StringUtil.parseInt(submitTime.substring(submitTime.indexOf("-")+1, submitTime.lastIndexOf("-")));
					int day = StringUtil.parseInt(submitTime.substring(submitTime.lastIndexOf("-")+1));
					String title = new StringBuilder("家长好！").append(subject)
							.append("作业已布置，请督促您的孩子于").append(month).append("月")
							.append(day).append("日")
							.append("前完成。").toString();
					map.put("title", title);
				}
				String label = null;
				if(director != null && director){
					label = "班主任";
				}else if(teacher != null && teacher){
					subjects = StringUtil.isEmpty(subjects) ? (String)map.get("realName") : subjects;
					label = subjects+"老师";
				}
				map.put("label", label);
			}
		}
		return new ApiMessage(ReqCode.HomeworkNotify, ReqState.Success).setInfo(hcList);
	}
	
}
