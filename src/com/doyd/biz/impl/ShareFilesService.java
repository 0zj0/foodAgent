package com.doyd.biz.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.doyd.biz.IFilesService;
import com.doyd.biz.IShareFilesService;
import com.doyd.core.action.common.Page;
import com.doyd.core.biz.impl.MyTransSupport;
import com.doyd.dao.IShareFilesDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.model.Files;
import com.doyd.model.ShareFiles;
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
public class ShareFilesService extends MyTransSupport implements IShareFilesService {
	@Autowired
	private IShareFilesDao shareFilesDao;
	@Autowired
	private IFilesService filesService;
	@Autowired
	private IWeixinGroupDao weixinGroupDao;
	@Autowired
	private IUserGroupDao userGroupDao;
	@Autowired
	private IWeixinUserDao weixinUserDao;
	
	private String STORAGETABLE = "shareFiles";
	private String FIELD1 = "newShare";
	private String FIELD2 = "totalShare";

	@Override
	public ApiMessage queryShareFilesByPage(Page page, String openGId,
			int wuId, String key, int type, ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		
		UserGroup userGroup = userGroupDao.getUserGroup(wuId, group.getGroupId());
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		List<Map<String, Object>> shareFilesList = shareFilesDao.queryShareFilesByPage(page, userGroup.getGroupId(), wuId, key, type);
		if(shareFilesList!=null && shareFilesList.size()>0){
			for (Map<String, Object> shareFiles : shareFilesList) {
				int id = StringUtil.parseIntByObj(shareFiles.get("shareId"));
				String storageTable = (String) shareFiles.get("storageTable");
				List<Map<String, Object>> fileList = filesService.getFiles(storageTable, id, STORAGETABLE);
				shareFiles.put("file", (fileList!=null && fileList.size()>0) ? fileList.get(0) : null);
				shareFiles.remove("storageTable");
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", shareFilesList);
		map.put("totalPage", page.getTotalPage());
		return new ApiMessage(reqCode, ReqState.Success).setInfo(map);
	}

	@Override
	public ApiMessage createShareFiles(WeixinUser weixinUser, String openGId,
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
			
			ShareFiles shareFiles = new ShareFiles();
			shareFiles.setRemark(remark);
			shareFiles.setUpDate(StringUtil.parseInt(DateUtil.today().replaceAll("-", "")));
			shareFiles.setFileCnt(1);
			shareFiles.setGroupId(userGroup.getGroupId());
			shareFiles.setUgId(userGroup.getUgId());
			shareFiles.setWuId(weixinUser.getWuId());
			//TODO 现在都存入files表里头，以后要备份的时候怎么搞？
			shareFiles.setStorageTable("files");
			List<Files> fileList = new ArrayList<Files>();
			String fileAddr = file.getString("fileAddr");
			String fileName = file.getString("fileName");
			Files files = FileUtil.createFile(userGroup.getGroupId(), fileAddr, fileName, STORAGETABLE);
			fileList.add(files);
			boolean r = true;
			TransactionStatus status = getTxStatus();
			try{
				r = r && shareFilesDao.addShareFiles(shareFiles);
				r = r && filesService.batchAddFiles(fileList, shareFiles.getShareId());
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
	public ApiMessage deleteShareFiles(WeixinUser weixinUser, String openGId,
			int[] shareIds, ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), group.getGroupId());
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		List<ShareFiles> shareFilesList = shareFilesDao.getShareFilesList(userGroup.isDirector(), weixinUser.getWuId(), shareIds);
		if(shareFilesList==null || shareFilesList.size()!=shareIds.length){
			return new ApiMessage(reqCode, ReqState.ExistNoPowerItem);
		}
		Map<String, List<Integer>> fileMap = new HashMap<String, List<Integer>>();
		for (ShareFiles shareFiles : shareFilesList) {
			List<Integer> shareIdList = fileMap.get(shareFiles.getStorageTable());
			if(shareIdList==null){
				shareIdList = new ArrayList<Integer>();
			}
			shareIdList.add(shareFiles.getShareId());
			fileMap.put(shareFiles.getStorageTable(), shareIdList);
		}
		boolean r = true;
		TransactionStatus status = getTxStatus();
		try{
			r = r && shareFilesDao.deleteShareFiles(shareIds);
			for(String storageTable: fileMap.keySet()){
				r = r && filesService.batchDeleteFiles(storageTable, STORAGETABLE, fileMap.get(storageTable));
			}
			r = r && weixinGroupDao.downCnt(group.getGroupId(), FIELD2, shareIds.length);
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
	public ApiMessage updateShareFiles(WeixinUser weixinUser, int shareId,
			String remark, ReqCode reqCode) {
		ShareFiles shareFiles = shareFilesDao.getShareFilesById(shareId);
		if(shareFiles==null){
			return new ApiMessage(reqCode, ReqState.NoExistShareFiles);
		}
		UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), shareFiles.getGroupId());
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		//是老师但不是他上传的共享文件
		if (userGroup.isTeacher() && userGroup.getWuId()!=shareFiles.getWuId()) {
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		shareFiles.setRemark(remark);
		if(!shareFilesDao.updateShareFiles(shareFiles)){
			return new ApiMessage(reqCode, ReqState.Failure);
		}
		return new ApiMessage(reqCode, ReqState.Success);
	}

	@Override
	public ApiMessage getShareFilesCnt(WeixinUser weixinUser, String openGId,
			int type, ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), group.getGroupId());
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		int shareFilesCnt = shareFilesDao.getShareFilesCnt(userGroup.getGroupId(), weixinUser.getWuId(), type);
		return new ApiMessage(reqCode, ReqState.Success).setInfo(shareFilesCnt);
	}

	@Override
	public ApiMessage getShareFiles(String openId, String openGId, String key,
			int page) {
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.Share, ReqState.NoExistUser);
		}
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(ReqCode.Share, ReqState.NoExistGroup);
		}
		if(!userGroupDao.existUserGroup(wu.getWuId(), group.getGroupId(), 0)){
			return new ApiMessage(ReqCode.Share, ReqState.NoPower);
		}
		List<Map<String, Object>> shareFilesList = shareFilesDao.queryShareFilesByPage(page, group.getGroupId(), key);
		return new ApiMessage(ReqCode.Share, ReqState.Success).setInfo(shareFilesList);
	}
	
}
