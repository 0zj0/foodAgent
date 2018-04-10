package com.doyd.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.doyd.utils.DateUtil;
import org.doyd.utils.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.doyd.biz.IClassNotifyService;
import com.doyd.biz.IFilesService;
import com.doyd.core.action.common.Page;
import com.doyd.core.biz.impl.MyTransSupport;
import com.doyd.dao.IClassNotifyDao;
import com.doyd.dao.IReadClassDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.model.ClassNotify;
import com.doyd.model.Files;
import com.doyd.model.ReadClass;
import com.doyd.model.UserGroup;
import com.doyd.model.WeixinGroup;
import com.doyd.model.WeixinUser;
import com.doyd.module.pc.utils.FileUtil;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;

@Service
public class ClassNotifyService extends MyTransSupport implements IClassNotifyService {
	@Autowired
	private IClassNotifyDao classNotifyDao;
	@Autowired
	private IFilesService filesService;
	@Autowired
	private IWeixinGroupDao weixinGroupDao;
	@Autowired
	private IUserGroupDao userGroupDao;
	@Autowired
	private IReadClassDao readClassDao;
	@Autowired
	private IWeixinUserDao weixinUserDao;
	
	private String STORAGETABLE = "class_notify";
	private String FIELD1 = "newNotify";
	private String FIELD2 = "totalNotify";
	private String JOINFIELD = "notifyId";
	private String READTABLE = "read_class";

	@Override
	public ApiMessage queryClassNotifyByPage(Page page, String openGId,
			int type, int wuId, String key, String beginDate, String endDate,
			ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		
		UserGroup userGroup = userGroupDao.getUserGroup(wuId, group.getGroupId());
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		List<Map<String, Object>> notifyList = classNotifyDao.queryClassNotifyByPage(page, userGroup.getGroupId(), type, wuId, key, beginDate, endDate);
		if(notifyList!=null && notifyList.size()>0){
			for (Map<String, Object> notify : notifyList) {
				List<Map<String, Object>> fileList = null;
				if(notify.get("storageTable")!=null) {
					int id = StringUtil.parseIntByObj(notify.get("notifyId"));
					String storageTable = (String) notify.get("storageTable");
					fileList = filesService.getFiles(storageTable, id, STORAGETABLE);
				}
				notify.put("files", fileList);
				notify.remove("storageTable");
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", notifyList);
		map.put("totalPage", page.getTotalPage());
		return new ApiMessage(reqCode, ReqState.Success).setInfo(map);
	}
	
	@Override
	public ApiMessage queryClassNotifyByPage(int page, String openId, String openGId, String identity, int key){
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(openGId) || page <= 0 
				|| StringUtil.isEmpty(identity) || key < 0 || key > 2){
			return new ApiMessage(ReqCode.Classnotify, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.Classnotify, ReqState.NoExistUser);
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return new ApiMessage(ReqCode.Classnotify, ReqState.NoExistGroup);
		}
		//用户群关系
		UserGroup ug = userGroupDao.getUserGroup(wu.getWuId(), wg.getGroupId());
		if(ug == null){
			return new ApiMessage(ReqCode.Classnotify, ReqState.UserNotInGroup);
		}
		//如果页面传入班主任但用户群内不存在班主任--等
		if(!(("director".equals(identity) && ug.isDirector()) 
				|| ("teacher".equals(identity) && ug.isTeacher()))){
			return new ApiMessage(ReqCode.Classnotify, ReqState.NoPower);
		}
		List<Map<String, Object>> cnList = classNotifyDao.queryClassNotifyByPage(page, wg.getGroupId(), wu.getWuId(), key);
		return new ApiMessage(ReqCode.Classnotify, ReqState.Success).setInfo(cnList);
	}

	@Override
	public ApiMessage showClassNotify(int notifyId, WeixinUser weixinUser,
			ReqCode reqCode) {
		Map<String, Object> notify = classNotifyDao.getClassNotifyForMap(notifyId);
		if(notify==null){
			return new ApiMessage(reqCode, ReqState.NoExistNotify);
		}
		int groupId = StringUtil.parseIntByObj(notify.get("groupId"));
		UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), groupId);
		if(userGroup==null){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		//不是班主任且不是老师
		if(!userGroup.isDirector() && !userGroup.isTeacher()){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		readNotify(weixinUser, userGroup, notify);
		String storageTable = (String) notify.get("storageTable");
		//附件
		List<Map<String, Object>> fileList = StringUtil.isNotEmpty(storageTable)?filesService.getFiles(storageTable, notifyId, STORAGETABLE):null;
		
		//已读家长列表
		List<Map<String, Object>> readUserList = readClassDao.getReadUserList(userGroup.getGroupId(), notifyId);
		if(readUserList==null){
			readUserList = new ArrayList<Map<String,Object>>();
		}
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
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("content", notify.get("content"));
		map.put("ctime", notify.get("ctime"));
		map.put("files", fileList);
		map.put("name", notify.get("realName"));
		map.put("notReadCnt", noReadUserList.size());
		map.put("notReadUserList", noReadUserList);
		map.put("readCnt", readUserList.size());
		map.put("readUserList", readUserList);
		map.put("title", notify.get("title"));
		return new ApiMessage(reqCode, ReqState.Success).setInfo(map);
	}
	
	@Override
	public ApiMessage showClassNotify(String openId, int notifyId){
		if(StringUtil.isEmpty(openId) || notifyId <= 0){
			return new ApiMessage(ReqCode.ClassnotifyShow, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.ClassnotifyShow, ReqState.NoExistUser);
		}
		Map<String, Object> notify = classNotifyDao.getClassNotifyForMap(notifyId);
		if(notify==null){
			return new ApiMessage(ReqCode.ClassnotifyShow, ReqState.NoExistNotify);
		}
		int groupId = StringUtil.parseIntByObj(notify.get("groupId"));
		UserGroup ug = userGroupDao.getUserGroup(wu.getWuId(), groupId);
		//不是班主任且不是老师
		if(ug==null){
			return new ApiMessage(ReqCode.ClassnotifyShow, ReqState.NoPower);
		}
		readNotify(wu, ug, notify);
		String storageTable = (String) notify.get("storageTable");
		//附件
		List<Map<String, Object>> fileList = filesService.getFiles(storageTable, notifyId, STORAGETABLE);

		String content = (String) notify.get("content");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("content", StringUtil.isEmpty(content) ? null : content.replaceAll("&nbsp;", " ").replaceAll("<br/>", "\\\n"));
		map.put("existPower", StringUtil.parseIntByObj(notify.get("wuId")) == wu.getWuId() || ug.isDirector());
		map.put("ctime", notify.get("ctime"));
		map.put("files", fileList);
		map.put("readCnt", notify.get("readCnt"));
		map.put("realName", notify.get("realName"));
		map.put("title", notify.get("title"));
		return new ApiMessage(ReqCode.ClassnotifyShow, ReqState.Success).setInfo(map);
	}
	
	private void readNotify(WeixinUser wu, UserGroup ug, Map<String, Object> notify){
		int notifyId = StringUtil.parseIntByObj(notify.get("notifyId"));
		int groupId = StringUtil.parseIntByObj(notify.get("groupId"));
		//如果存在阅读记录
		if(!readClassDao.isRead(notifyId, wu.getWuId())){
			String ctime = (String) notify.get("ctime");
			Date now = new Date();
			ReadClass rc = new ReadClass();
			rc.setGroupId(groupId);
			rc.setNotifyId(notifyId);
			rc.setWuId(wu.getWuId());
			//   （当前时间戳 - 作业发布毫秒数）/ 1000毫秒（等于秒） / 60秒 （等于分钟）
			int interval = (int) ((now.getTime() - DateUtil.convertToDate(ctime).getTime()) / 1000 / 60);
			rc.setInterval(interval);
			int readDate = StringUtil.parseInt(DateUtil.convertToString(now, "yyyyMMdd"));
			rc.setReadDate(readDate);
			rc.setReadHour(DateUtil.getHour());
			TransactionStatus txStatus = getTxStatus();
			boolean r = false;
			try {
				//添加阅读记录
				r = readClassDao.addReadClass(rc);
				if(r && ug.getNewNotify() > 0){
					//减少气泡
					r = userGroupDao.downCnt(wu.getWuId(), groupId, "newNotify");
				}
				//只有家长阅读才添加阅读数
				if(r && !ug.isDirector() && !ug.isTeacher()){
					//添加班务阅读数量
					r = classNotifyDao.readNotify(notifyId);
				}
			} catch (Exception e) {
				r = false;
			}finally{
				commit(txStatus, r);
			}
		}
	}

	@Override
	public ApiMessage createClassNotify(WeixinUser weixinUser, String openGId,
			String title, String content, JSONArray files, ReqCode reqCode) {
		try{
			
			WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
			if(group==null){
				return new ApiMessage(reqCode, ReqState.NoExistGroup);
			}
			
			UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), group.getGroupId());
			if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
				return new ApiMessage(reqCode, ReqState.NoPower);
			}
			
			ClassNotify classNotify = new ClassNotify();
			classNotify.setTitle(title);
			classNotify.setFileCnt(files==null?0:files.length());
			classNotify.setGroupId(userGroup.getGroupId());
			classNotify.setContent(content);
			classNotify.setUgId(userGroup.getUgId());
			classNotify.setWuId(weixinUser.getWuId());
			//现在都存入files表里头，以后要备份的时候怎么搞？TODO
			classNotify.setStorageTable("files");
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
				r = r && classNotifyDao.addClassNotify(classNotify);
				r = r && filesService.batchAddFiles(fileList, classNotify.getNotifyId());
				r = r && userGroupDao.upCnt(group.getGroupId(), FIELD1, 1);
				r = r && weixinGroupDao.upCnt(group.getGroupId(), FIELD2);
			}catch (Exception e) {
				e.printStackTrace();
				r = false;
			}finally{
				commit(status, r);
			}
			if(!r){
				return new ApiMessage(reqCode, ReqState.Failure);
			}
			return new ApiMessage(reqCode, ReqState.Success).setInfo(classNotify.getNotifyId());
		
		}catch (Exception e) {
			e.printStackTrace();
			return new ApiMessage(reqCode, ReqState.Failure);
		}
	}

	@Override
	public ApiMessage deleteClassNotify(WeixinUser weixinUser, String openGId, int[] notifyIds,
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
		List<ClassNotify> notifyList = classNotifyDao.getClassNotifyList(userGroup.isDirector(), weixinUser.getWuId(), notifyIds);
		if(notifyList==null || notifyList.size()!=notifyIds.length){
			return new ApiMessage(reqCode, ReqState.ExistNoPowerItem);
		}
		Map<String, List<Integer>> fileMap = new HashMap<String, List<Integer>>();
		for (ClassNotify notify : notifyList) {
			if(notify.getStorageTable()==null) continue;
			List<Integer> notifyIdList = fileMap.get(notify.getStorageTable());
			if(notifyIdList==null){
				notifyIdList = new ArrayList<Integer>();
			}
			notifyIdList.add(notify.getNotifyId());
			fileMap.put(notify.getStorageTable(), notifyIdList);
		}
		boolean r = true;
		TransactionStatus status = getTxStatus();
		try{
			r = r && classNotifyDao.deleteClassNotify(notifyIds, userGroup.getGroupId(), userGroup.isDirector(), weixinUser.getWuId());
			for(String storageTable: fileMap.keySet()){
				r = r && filesService.batchDeleteFiles(storageTable, STORAGETABLE, fileMap.get(storageTable));
			}
			for(int notifyId : notifyIds){
				r = r && userGroupDao.downCnt(userGroup.getGroupId(), FIELD1, READTABLE, JOINFIELD, notifyId);
			}
			r = r && weixinGroupDao.downCnt(group.getGroupId(), FIELD2, notifyIds.length);
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
	public ApiMessage getClassNotifyCnt(WeixinUser weixinUser, String openGId,
			int type, ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), group.getGroupId());
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		int notifyCnt = classNotifyDao.getClassNotifyCnt(userGroup.getGroupId(), weixinUser.getWuId(), type);
		return new ApiMessage(reqCode, ReqState.Success).setInfo(notifyCnt);
	}
	
}
