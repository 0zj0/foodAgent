package com.doyd.biz.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doyd.biz.IReadClassService;
import com.doyd.core.biz.impl.MyTransSupport;
import com.doyd.dao.IClassNotifyDao;
import com.doyd.dao.IReadClassDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.model.ClassNotify;
import com.doyd.model.UserGroup;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import org.doyd.utils.StringUtil;

@Service
public class ReadClassService extends MyTransSupport implements IReadClassService {
	@Autowired
	private IReadClassDao readClassDao;
	@Autowired
	private IWeixinUserDao weixinUserDao;
	@Autowired
	private IClassNotifyDao classNotifyDao;
	@Autowired
	private IUserGroupDao userGroupDao;

	@Override
	public ApiMessage getReadClassnotify(String openId, int notifyId, int key) {
		if(StringUtil.isEmpty(openId) || notifyId <= 0 
				|| (key != 1 && key != 2)){
			return new ApiMessage(ReqCode.ClassnotifyRead, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.ClassnotifyRead, ReqState.NoExistUser);
		}
		ClassNotify cn = classNotifyDao.getClassNotify(notifyId);
		if(cn == null){
			return new ApiMessage(ReqCode.ClassnotifyRead, ReqState.NoExistNotify);
		}
		UserGroup ug = userGroupDao.getUserGroup(wu.getWuId(), cn.getGroupId());
		//用户不存在群关系或者，不是班主任和老师
		if(ug == null || (!ug.isDirector() && !ug.isTeacher())){
			return new ApiMessage(ReqCode.ClassnotifyRead, ReqState.NoPower);
		}
		//获得群内所有只有家长身份的用户
		List<Map<String, Object>> allUserList = userGroupDao.getAllUserList(cn.getGroupId());
		//获得用户总数
		int totalCnt = allUserList != null ? allUserList.size() : 0;
		//获得阅读用户
		List<Map<String, Object>> readUserList = readClassDao.getReadUserList(cn.getGroupId(), cn.getNotifyId());
		if(readUserList==null){
			readUserList = new ArrayList<Map<String,Object>>();
		}
		//获得阅读用户数
		int readCnt = readUserList.size();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("totalCnt", totalCnt);
		resultMap.put("readCnt", readCnt);
		if(key == 2){
			//未读家长列表
			List<Map<String, Object>> noReadUserList = new ArrayList<Map<String,Object>>();
			if(allUserList!=null && allUserList.size()>0){
				for (Map<String, Object> user : allUserList) {
					if(!readUserList.contains(user)){
						noReadUserList.add(user);
					}
				}
			}
			resultMap.put("userList", noReadUserList);
		}else{
			resultMap.put("userList", readUserList);
		}
		return new ApiMessage(ReqCode.ClassnotifyRead, ReqState.Success).setInfo(resultMap);
	}
	
}
