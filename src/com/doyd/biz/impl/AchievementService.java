package com.doyd.biz.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.doyd.biz.IAchievementService;
import com.doyd.biz.IFilesService;
import com.doyd.core.action.common.Page;
import com.doyd.core.biz.impl.MyTransSupport;
import com.doyd.dao.IAchievementDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.model.Achievement;
import com.doyd.model.Files;
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
public class AchievementService extends MyTransSupport implements IAchievementService {
	@Autowired
	private IAchievementDao achievementDao;
	@Autowired
	private IFilesService filesService;
	@Autowired
	private IWeixinGroupDao weixinGroupDao;
	@Autowired
	private IUserGroupDao userGroupDao;
	@Autowired
	private IWeixinUserDao weixinUserDao;
	
	private String STORAGETABLE = "achievement";
	private String FIELD1 = "newAchievement";
	private String FIELD2 = "totalAchievement";

	@Override
	public ApiMessage queryAchievementByPage(Page page, String openGId,
			int wuId, String key, int type, ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		
		UserGroup userGroup = userGroupDao.getUserGroup(wuId, group.getGroupId());
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		List<Map<String, Object>> achievementList = achievementDao.queryAchievementByPage(page, userGroup.getGroupId(), wuId, key, type);
		if(achievementList!=null && achievementList.size()>0){
			for (Map<String, Object> achievement : achievementList) {
				int id = StringUtil.parseIntByObj(achievement.get("scoreId"));
				String storageTable = (String) achievement.get("storageTable");
				List<Map<String, Object>> fileList = filesService.getFiles(storageTable, id, STORAGETABLE);
				achievement.put("file", (fileList!=null && fileList.size()>0) ? fileList.get(0) : null);
				achievement.remove("storageTable");
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", achievementList);
		map.put("totalPage", page.getTotalPage());
		return new ApiMessage(reqCode, ReqState.Success).setInfo(map);
	}
	
	@Override
	public ApiMessage queryAchievementByPage(int page, String openId, String openGId, String key){
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(openGId) || page <= 0){
			return new ApiMessage(ReqCode.Achievement, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.Achievement, ReqState.NoExistUser);
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return new ApiMessage(ReqCode.Achievement, ReqState.NoExistGroup);
		}
		if(!userGroupDao.existUserGroup(wu.getWuId(), wg.getGroupId(), 0)){
			return new ApiMessage(ReqCode.Achievement, ReqState.UserNotInGroup);
		}
		List<Map<String, Object>> achList = achievementDao.queryAchievementByPage(page, wg.getGroupId(), key);
		return new ApiMessage(ReqCode.Achievement, ReqState.Success).setInfo(achList);
	}

	@Override
	public ApiMessage createAchievement(WeixinUser weixinUser, String openGId,
			String remark, JSONObject file, ReqCode reqCode) {
		try{
			
			WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
			if(group==null){
				return new ApiMessage(reqCode, ReqState.NoExistGroup);
			}
			
			UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), group.getGroupId());
			if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
				return new ApiMessage(reqCode, ReqState.NoPower);
			}
			
			Achievement achievement = new Achievement();
			achievement.setRemark(remark);
			achievement.setUpDate(StringUtil.parseInt(DateUtil.today().replaceAll("-", "")));
			achievement.setFileCnt(1);
			achievement.setGroupId(userGroup.getGroupId());
			achievement.setUgId(userGroup.getUgId());
			achievement.setWuId(weixinUser.getWuId());
			//TODO 现在都存入files表里头，以后要备份的时候怎么搞？
			achievement.setStorageTable("files");
			List<Files> fileList = new ArrayList<Files>();
			String fileAddr = file.getString("fileAddr");
			String fileName = file.getString("fileName");
			Files files = FileUtil.createFile(userGroup.getGroupId(), fileAddr, fileName, STORAGETABLE);
			fileList.add(files);
			boolean r = true;
			TransactionStatus status = getTxStatus();
			try{
				r = r && achievementDao.addAchievement(achievement);
				r = r && filesService.batchAddFiles(fileList, achievement.getScoreId());
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
			return new ApiMessage(reqCode, ReqState.Success);
		
		}catch (Exception e) {
			e.printStackTrace();
			return new ApiMessage(reqCode, ReqState.Failure);
		}
	}

	@Override
	public ApiMessage deleteAchievement(WeixinUser weixinUser, String openGId, int[] scoreIds,
			ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), group.getGroupId());
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		List<Achievement> achievementList = achievementDao.getAchievementList(userGroup.isDirector(), weixinUser.getWuId(), scoreIds);
		if(achievementList==null || achievementList.size()!=scoreIds.length){
			return new ApiMessage(reqCode, ReqState.ExistNoPowerItem);
		}
		Map<String, List<Integer>> fileMap = new HashMap<String, List<Integer>>();
		for (Achievement achievement : achievementList) {
			List<Integer> scoreIdList = fileMap.get(achievement.getStorageTable());
			if(scoreIdList==null){
				scoreIdList = new ArrayList<Integer>();
			}
			scoreIdList.add(achievement.getScoreId());
			fileMap.put(achievement.getStorageTable(), scoreIdList);
		}
		boolean r = true;
		TransactionStatus status = getTxStatus();
		try{
			r = r && achievementDao.deleteAchievement(scoreIds);
			for(String storageTable: fileMap.keySet()){
				r = r && filesService.batchDeleteFiles(storageTable, STORAGETABLE, fileMap.get(storageTable));
			}
			r = r && weixinGroupDao.downCnt(group.getGroupId(), FIELD2, scoreIds.length);
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
	public ApiMessage updateAchievement(WeixinUser weixinUser, int scoreId,
			String remark, ReqCode reqCode) {
		Achievement achievement = achievementDao.getAchievementById(scoreId);
		if(achievement==null){
			return new ApiMessage(reqCode, ReqState.NoExistAchievement);
		}
		UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), achievement.getGroupId());
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		//是老师但不是他上传的成绩单
		if (userGroup.isTeacher() && userGroup.getWuId()!=achievement.getWuId()) {
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		achievement.setRemark(remark);
		if(!achievementDao.updateAchievement(achievement)){
			return new ApiMessage(reqCode, ReqState.Failure);
		}
		return new ApiMessage(reqCode, ReqState.Success);
	}

	@Override
	public ApiMessage getAchievementCnt(WeixinUser weixinUser, String openGId,
			int type, ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), group.getGroupId());
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		int achievementCnt = achievementDao.getAchievementCnt(userGroup.getGroupId(), weixinUser.getWuId(), type);
		return new ApiMessage(reqCode, ReqState.Success).setInfo(achievementCnt);
	}
	
}
