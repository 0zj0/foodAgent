package com.doyd.biz.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.doyd.biz.IGroupMessageService;
import com.doyd.core.biz.impl.MyTransSupport;
import com.doyd.dao.IGroupMessageDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.model.GroupMessage;
import com.doyd.model.UserGroup;
import com.doyd.model.WeixinGroup;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import org.doyd.utils.StringUtil;

@Service
public class GroupMessageService extends MyTransSupport implements IGroupMessageService {
	@Autowired
	private IGroupMessageDao groupMessageDao;
	@Autowired
	private IWeixinUserDao weixinUserDao;
	@Autowired
	private IUserGroupDao userGroupDao;
	@Autowired
	private IWeixinGroupDao weixinGroupDao;

	@Override
	public ApiMessage readMes(String openId, int msgId, int result) {
		if(StringUtil.isEmpty(openId) || msgId <= 0){
			return new ApiMessage(ReqCode.MsgRead, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.MsgRead, ReqState.NoExistUser);
		}
		GroupMessage gm = groupMessageDao.getGroupMessage(msgId);
		if(gm == null){
			return new ApiMessage(ReqCode.MsgRead, ReqState.NoExistMsg);
		}
		if(gm.getWuId() != wu.getWuId()){
			return new ApiMessage(ReqCode.MsgRead, ReqState.NoPower);
		}
		if(gm.getIsRead() == 3){
			return new ApiMessage(ReqCode.MsgRead, ReqState.Success);
		}
		UserGroup ug = null;
		if(gm.getGroupId() > 0){
			ug = userGroupDao.getUserGroup(wu.getWuId(), gm.getGroupId());
			if(ug == null){
				return new ApiMessage(ReqCode.MsgRead, ReqState.UserNotInGroup);
			}
		}
		UserGroup tmpUg = null;
		if(gm.getMsgType() == 7){
			tmpUg = userGroupDao.getUserGroup(gm.getId(), gm.getGroupId());
			if(tmpUg == null){
				tmpUg = new UserGroup();
				tmpUg.setWuId(gm.getId());
				tmpUg.setGroupId(gm.getGroupId());
				tmpUg.setTeacher(true);
				tmpUg.setPhone(wu.getPhone());
			}
		}
		TransactionStatus txStatus = getTxStatus();
		boolean r = false;
		try {
			r = groupMessageDao.readMsg(msgId);
			//科任老师申请进入
			if(r && gm.getMsgType() == 7){
				//如果不存在用户关系，则添加
				if(tmpUg.getUgId() <= 0){
					r = userGroupDao.addUserGroup(tmpUg);
				//存在用户关系，则添加老师身份
				}else{
					r = userGroupDao.addIdentity(tmpUg.getWuId(), tmpUg.getGroupId(), false, true);
				}
			}
		} catch (Exception e) {
			r = false;
		}finally{
			commit(txStatus, r);
		}
		ReqState reqState = r ? ReqState.Success : ReqState.Failure;
		return new ApiMessage(ReqCode.MsgRead, reqState);
	}

	@Override
	public ApiMessage getMessage(String openId, String openGId) {
		if(StringUtil.isEmpty(openId)){
			return new ApiMessage(ReqCode.GetMessage, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.GetMessage, ReqState.NoExistUser);
		}
		int groupId = 0;
		if(StringUtil.isNotEmpty(openGId)){
			WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
			if(wg == null){
				return new ApiMessage(ReqCode.GetMessage, ReqState.NoExistGroup);
			}
			if(!userGroupDao.existUserGroup(wu.getWuId(), wg.getGroupId(), 0)){
				return new ApiMessage(ReqCode.GetMessage, ReqState.UserNotInGroup);
			}
			groupId = wg.getGroupId();
		}
		List<Map<String, Object>> gmList = groupMessageDao.getGroupMessageList(wu.getWuId(), groupId);
		return new ApiMessage(ReqCode.GetMessage, ReqState.Success).setInfo(gmList);
	}
	
}
