package com.doyd.biz.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.doyd.sdk.WeixinApi;
import org.doyd.utils.StringUtil;
import org.doyd.weixin.entity.user.WeixinAppletUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.doyd.biz.IAppletService;
import com.doyd.biz.IWeixinGroupService;
import com.doyd.core.action.common.ControllerContext;
import com.doyd.core.action.common.Page;
import com.doyd.core.biz.impl.MyTransSupport;
import com.doyd.dao.IGroupMessageDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.model.Applet;
import com.doyd.model.GroupMessage;
import com.doyd.model.Pic;
import com.doyd.model.UserGroup;
import com.doyd.model.WeixinGroup;
import com.doyd.model.WeixinUser;
import com.doyd.module.applet.utils.WeixinUserModelUtil;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import com.doyd.util.FileUploadUtil;
import com.doyd.util.PapersException;

@Service
public class WeixinGroupService extends MyTransSupport implements IWeixinGroupService {
	@Autowired
	private IWeixinGroupDao weixinGroupDao;
	@Autowired
	private IWeixinUserDao weixinUserDao;
	@Autowired
	private IUserGroupDao userGroupDao;
	@Autowired
	private IGroupMessageDao groupMessageDao;
	@Autowired
	private IAppletService appletService;

	@Override
	public ApiMessage queryWeixinGroup(WeixinUser weixinUser, ReqCode reqCode) {
		if(weixinUser==null) return new ApiMessage(reqCode, ReqState.NoExistUser);
		List<Map<String, Object>> weixinGroupList = weixinGroupDao.getWeixinGroupList(weixinUser.getWuId());
		return new ApiMessage(reqCode, ReqState.Success).setInfo(weixinGroupList);
	}
	public WeixinGroup getWeixinGroup(String openGId) {
		return weixinGroupDao.getWeixinGroup(openGId);
	}
	@Override
	public ApiMessage queryWeixinGroupByPage(Page page, WeixinUser weixinUser,
			ReqCode reqCode) {
		if(weixinUser==null) return new ApiMessage(reqCode, ReqState.NoExistUser);
		List<Map<String, Object>> weixinGroupList = weixinGroupDao.getWeixinGroupByPage(page, weixinUser.getWuId());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", weixinGroupList);
		map.put("totalPage", page.getTotalPage());
		return new ApiMessage(reqCode, ReqState.Success).setInfo(map);
	}
	@Override
	public ApiMessage getOpenGId(String appId, String openId, String code,
			String gEncryptedData, String gIv) {
		if(StringUtil.isEmpty(appId) || StringUtil.isEmpty(openId) || StringUtil.isEmpty(code) 
				|| StringUtil.isEmpty(gEncryptedData) || StringUtil.isEmpty(gIv)){
			return new ApiMessage(ReqCode.GetOpenGId, ReqState.ApiParamError);
		}
		Applet applet = appletService.getApplet(appId);
		if(applet == null){
			return new ApiMessage(ReqCode.GetOpenGId, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.GetOpenGId, ReqState.NoExistUser);
		}
        //获取微信用户相关信息
    	WeixinAppletUser wau = WeixinApi.getAppletUserService().getAppletUser(appId, null, code);
    	if(wau == null || StringUtil.isEmpty(wau.getOpenid())){
            return new ApiMessage(ReqCode.GetOpenGId, ReqState.ApiParamError);
    	}
    	if(!openId.equals(wau.getOpenid())){
    		return new ApiMessage(ReqCode.GetOpenGId, ReqState.ApiParamError);
    	}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String openGId = WeixinUserModelUtil.getOpenGId(wau.getSession_key(), gEncryptedData, gIv);
		//是否存在群名称
		boolean existName = false;
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		resultMap.put("groupName", null);
		if(wg != null && StringUtil.isNotEmpty(wg.getGroupName())){
			existName = true;
			resultMap.put("groupName", wg.getGroupName());
		}
		resultMap.put("openGId", openGId);
		resultMap.put("existName", existName);
		return new ApiMessage(ReqCode.GetOpenGId, ReqState.Success).setInfo(resultMap);
	}
	
	@Override
	public String addGroupPic(String openGId, String baseUrl) {
		if(StringUtil.isEmpty(openGId)){
			return null;
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return null;
		}
		String oldFileName = null;
		if(StringUtil.isNotEmpty(wg.getGroupPic())){
			oldFileName = wg.getGroupPic().substring(wg.getGroupPic().lastIndexOf("/")+1);
		}
		//群图片用大于9个生成的，则返回
		if(wg.getPicCnt() > 9){
			return wg.getGroupPic();
		}
		List<WeixinUser> wuList = weixinUserDao.getWeixinUserList(wg.getGroupId());
		if(wuList == null || wuList.size() <= 0){
			return null;
		}
		if(wuList.size() < wg.getPicCnt()){
			return wg.getGroupPic();
		}
		int startX = 0;
		int startY = 0;
		int size = 0;
		int gap = 0;
		if(wuList.size() == 1){
			startX = 30;
			startY = 30;
			size = 240;
		}else if(wuList.size() == 2){
			startX = 20;
			startY = 85;
			size = 125;
			gap = 135;
		}else if(wuList.size() <= 4){
			startX = 20;
			startY = 20;
			size = 125;
			gap = 135;
		}else if(wuList.size() <= 6){
			startX = 10;
			startY = 57;
			size = 90;
			gap = 95;
		}else{
			startX = 10;
			startY = 10;
			size = 90;
			gap = 95;
		}
		List<Pic> picList = new ArrayList<Pic>();
		int cX = startX;
		int cY = startY;
		for (int i = 0; (i < wuList.size() && i < 9); i++) {
			if(StringUtil.isNotEmpty(wuList.get(i).getAvatarUrl())){
				picList.add(new Pic(wuList.get(i).getAvatarUrl(), cX, cY, size, size));
			}
			cX += gap;
			if(wuList.size() <= 4){
				if(i == 1){
					cX = startX;
					cY += gap;
				}
			}else if(wuList.size() <= 6){
				if(i == 2){
					cX = startX;
					cY += gap;
				}
			}else{
				if(i == 2 || i == 5){
					cX = startX;
					cY += gap;
				}
			}
		}
		try {
			String basePath = ControllerContext.getBaseFilePath();
			BufferedImage image = ImageIO.read(new File(basePath + "/image/whith.jpg"));
			Graphics2D g = image.createGraphics();
			for (Pic pic : picList) {
				URL url = new URL(pic.getPicUrl());
				BufferedImage img = ImageIO.read(url);
				g.drawImage(img, pic.getX(), pic.getY(), pic.getWidth(), pic.getHeight(), null);
			}
			String fileName = (wg.getOpenGId()+StringUtil.getRandomString(2));
			String filePath = basePath + "/image/groupPic/" + fileName + ".jpg";
			File picFile = new File(filePath);
			ImageIO.write(image, "jpg", picFile);
			String fileUrl = baseUrl + "image/groupPic/" + fileName + ".jpg";
			if(wuList.size() >= 9){
				fileUrl = FileUploadUtil.uploadToFileServer(picFile, false, false, null);
				if(picFile.exists()){
					picFile.delete();
				}
			}
			wg.setGroupPic(fileUrl);
			wg.setPicCnt(wuList.size());
			weixinGroupDao.update(wg);
			if(StringUtil.isNotEmpty(oldFileName)){
				File file = new File(basePath+"/image/groupPic/"+oldFileName);
				if(file.exists()){
					file.delete();
				}
			}
			return fileUrl;
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		} catch (PapersException e) {
		}
		return null;
	}
	
	@Override
	public ApiMessage updateGroupName(String openId, String openGId,
			String groupName) {
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(openGId)
				|| StringUtil.isEmpty(groupName)){
			return new ApiMessage(ReqCode.UpdateGroupName, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.UpdateGroupName, ReqState.NoExistUser);
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return new ApiMessage(ReqCode.UpdateGroupName, ReqState.NoExistGroup);
		}
		UserGroup ug = userGroupDao.getUserGroup(wu.getWuId(), wg.getGroupId());
		if(ug == null){
			return new ApiMessage(ReqCode.UpdateGroupName, ReqState.UserNotInGroup);
		}
		//如果不是班主任，并且班群名称存在
		if(!ug.isDirector() && StringUtil.isNotEmpty(wg.getGroupName())){
			return new ApiMessage(ReqCode.UpdateGroupName, ReqState.ExistGroupName);
		}
		List<GroupMessage> gmList = new ArrayList<GroupMessage>();
		List<WeixinUser> wuList = weixinUserDao.getWeixinUserList(wg.getGroupId());
		if(ug.isDirector() && wuList != null && wuList.size() > 0){
			for (WeixinUser tmp : wuList) {
				GroupMessage gm = new GroupMessage();
				gm.setGroupId(wg.getGroupId());
				gm.setWuId(tmp.getWuId());
				gm.setTitle("群名称修改");
				gm.setContent("班主任修改群名称为“"+groupName+"”");
				gm.setMsgType(2);
			}
		}
		TransactionStatus txStatus = getTxStatus();
		boolean r = false;
		try {
			r = weixinGroupDao.updateGroupName(wg.getGroupId(), groupName) 
					&& groupMessageDao.addGroupMessage(gmList);
		} catch (Exception e) {
			r = false;
		}finally{
			commit(txStatus, r);
		}
		if(r){
			return new ApiMessage(ReqCode.UpdateGroupName, ReqState.Success);
		}
		return new ApiMessage(ReqCode.UpdateGroupName, ReqState.Failure);
	}
}
