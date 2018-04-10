package com.doyd.biz.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.doyd.biz.ITransferGroupService;
import com.doyd.core.biz.impl.MyTransSupport;
import com.doyd.dao.IAbortGroupDao;
import com.doyd.dao.IGroupMessageDao;
import com.doyd.dao.ITransferGroupDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.model.AbortGroup;
import com.doyd.model.GroupMessage;
import com.doyd.model.TransferGroup;
import com.doyd.model.UserGroup;
import com.doyd.model.WeixinGroup;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import org.doyd.utils.StringUtil;

@Service
public class TransferGroupService extends MyTransSupport implements ITransferGroupService {
	@Autowired
	private ITransferGroupDao transferGroupDao;
	@Autowired
	private IWeixinUserDao weixinUserDao;
	@Autowired
	private IWeixinGroupDao weixinGroupDao;
	@Autowired
	private IUserGroupDao userGroupDao;
	@Autowired
	private IAbortGroupDao abortGroupDao;
	@Autowired
	private IGroupMessageDao groupMessageDao;

	@Override
	public ApiMessage addTransfer(String openId, String openGId,
			String acceptOpenId) {
		//判断参数信息
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(openGId) || StringUtil.isEmpty(acceptOpenId)){
			return new ApiMessage(ReqCode.CreateTransfer, ReqState.ApiParamError);
		}
		if(openId.equals(acceptOpenId)){
			return new ApiMessage(ReqCode.CreateTransfer, ReqState.NoTransferSelf);
		}
		//获得用户信息
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.CreateTransfer, ReqState.NoExistUser);
		}
		//获得群信息
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return new ApiMessage(ReqCode.CreateTransfer, ReqState.NoExistGroup);
		}
		//班群群主不是当前用户
		if(wg.getDirectorId() != wu.getWuId()){
			return new ApiMessage(ReqCode.CreateTransfer, ReqState.NoPower);
		}
		//获得接收者用户信息
		WeixinUser acceptWu = weixinUserDao.getUser(acceptOpenId);
		if(acceptWu == null){
			return new ApiMessage(ReqCode.CreateTransfer, ReqState.NoExistUser);
		}
		//判断接收者是否在群中
		if(!userGroupDao.userInGroup(acceptWu.getWuId(), wg.getGroupId())){
			return new ApiMessage(ReqCode.CreateTransfer, ReqState.UserNotInGroup);
		}
		TransferGroup tg = new TransferGroup();
		tg.setGroupId(wg.getGroupId());
		tg.setTransferWuId(wu.getWuId());
		tg.setAcceptWuId(acceptWu.getWuId());
		TransactionStatus txStatus = getTxStatus();
		GroupMessage gm = new GroupMessage();
		gm.setGroupId(wg.getGroupId());
		gm.setWuId(acceptWu.getWuId());
		gm.setMsgType(9);
		gm.setTitle("您有一条转让信息");
		gm.setContent(wu.getNickName()+"将班主任身份转让给你");
		boolean r = false;
		try{
			r = transferGroupDao.addTransferGroup(tg);
			if(r){
				gm.setId(tg.getTransferId());
				r = groupMessageDao.addGroupMessage(gm);
			}
		}catch(Exception e){
			r = false;
		}finally{
			commit(txStatus, r);
		}
		if(r){
			return new ApiMessage(ReqCode.CreateTransfer, ReqState.Success).setInfo(tg.getTransferId());
		}
		return new ApiMessage(ReqCode.CreateTransfer, ReqState.Failure);
	}

	@Override
	public ApiMessage showTransfer(String openId, int transferId) {
		//判断参数信息
		if(StringUtil.isEmpty(openId) || transferId <= 0){
			return new ApiMessage(ReqCode.TransferShow, ReqState.ApiParamError);
		}
		//获得当前用户信息
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.TransferShow, ReqState.NoExistUser);
		}
		//获得转移信息
		TransferGroup tg = transferGroupDao.getTransferGroup(transferId);
		if(tg == null){
			return new ApiMessage(ReqCode.TransferShow, ReqState.ApiParamError);
		}
		//接收者不是当前用户
		if(tg.getAcceptWuId() != wu.getWuId()){
			return new ApiMessage(ReqCode.TransferShow, ReqState.NoPower);
		}
		//获得微信群信息
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(tg.getGroupId());
		if(wg == null){
			return new ApiMessage(ReqCode.TransferShow, ReqState.NoExistGroup);
		}
		//获得转移者信息
		WeixinUser transferWu = weixinUserDao.getUserById(tg.getTransferWuId());
		if(transferWu == null){
			return new ApiMessage(ReqCode.TransferShow, ReqState.Failure);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("groupName", wg.getGroupName());
		resultMap.put("transferName", transferWu.getNickName());
		resultMap.put("transferPic", transferWu.getAvatarUrl());
		resultMap.put("existIdentity", StringUtil.isNotEmpty(wu.getRealName()));
		return new ApiMessage(ReqCode.TransferShow, ReqState.Success).setInfo(resultMap);
	}

	@Override
	public ApiMessage accept(String openId, int transferId, String realName) {
		//判断参数信息
		if(StringUtil.isEmpty(openId) || transferId <= 0){
			return new ApiMessage(ReqCode.AcceptTransfer, ReqState.ApiParamError);
		}
		//获得当前用户信息
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.AcceptTransfer, ReqState.NoExistUser);
		}
		//用户不存在真实姓名，页面没有传入真实姓名
		if(StringUtil.isEmpty(wu.getRealName()) && StringUtil.isEmpty(realName)){
			return new ApiMessage(ReqCode.AcceptTransfer, ReqState.ApiParamError);
		}
		if(StringUtil.isEmpty(wu.getRealName())){
			wu.setRealName(realName);
		}
		//获得转让信息
		TransferGroup tg = transferGroupDao.getTransferGroup(transferId);
		if(tg == null){
			return new ApiMessage(ReqCode.AcceptTransfer, ReqState.ApiParamError);
		}
		//转让结束
		if(tg.getTransferState() != 1){
			return new ApiMessage(ReqCode.AcceptTransfer, ReqState.TransferEnd);
		}
		//获得群信息
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(tg.getGroupId());
		if(wg == null){
			return new ApiMessage(ReqCode.AcceptTransfer, ReqState.NoExistGroup);
		}
		//判断群主（班主任）是否是转让者
		if(wg.getDirectorId() != tg.getTransferWuId()){
			return new ApiMessage(ReqCode.AcceptTransfer, ReqState.TransferOutdate);
		}
		//判断接收用户是否在群内
		if(!userGroupDao.userInGroup(wu.getWuId(), wg.getGroupId())){
			return new ApiMessage(ReqCode.AcceptTransfer, ReqState.UserNotInGroup);
		}
		//获得转让人的群关系
		UserGroup ug = userGroupDao.getUserGroup(tg.getTransferWuId(), wg.getGroupId());
		if(ug == null){
			return new ApiMessage(ReqCode.AcceptTransfer, ReqState.UserNotInGroup);
		}
		//转让人没有班主任身份
		if(!ug.isDirector()){
			return new ApiMessage(ReqCode.AcceptTransfer, ReqState.TransferOutdate);
		}
		int identityCnt = 0;
		identityCnt = ug.isDirector() ? ++identityCnt : identityCnt;
		identityCnt = ug.isTeacher() ? ++identityCnt : identityCnt;
		identityCnt = ug.isPatriarch() ? ++identityCnt : identityCnt;
		
		//退群记录----主动转移为主动退群
		AbortGroup ag = new AbortGroup();
		ag.setGroupId(wg.getGroupId());
		ag.setUgId(ug.getUgId());
		ag.setWuId(tg.getTransferWuId());
		ag.setIdentities("identity");
		ag.setOperatorId(tg.getTransferWuId());
		ag.setAbortType(1);
		TransactionStatus txStatus = getTxStatus();
		boolean r = false;
		try {
			//修改微信群的群主信息
			r = weixinGroupDao.updateDirectorId(wg.getGroupId(), wu.getWuId())
					//添加当前用户（接收人）身份
					&& userGroupDao.addIdentity(wu.getWuId(), wg.getGroupId(), true, false);
			//如果存在多个身份就解绑班主任身份，否则删除群关系
			if(identityCnt > 1){
				//解绑转让人的班主任身份
				r = r && userGroupDao.unbindUserGroup(tg.getTransferWuId(), wg.getGroupId(), 1, 0);
			}else{
				//删除转让人的群关系
				r = r && userGroupDao.deleteUserGroup(tg.getTransferWuId(), wg.getGroupId(), 0);
			}
			//添加推群记录
			r = r && abortGroupDao.addAbortGroup(ag);
			//如果当前用户不存在班主任身份，则添加班主任身份
			if(r && !wu.isDirector()){
				wu.setDirector(true);
				r = weixinUserDao.addIdentity(wu);
			}
			
		} catch (Exception e) {
			r = false;
		}finally{
			commit(txStatus, r);
		}
		ReqState reqState = r ? ReqState.Success : ReqState.Failure;
		return new ApiMessage(ReqCode.AcceptTransfer, reqState);
	}
	
}
