package com.doyd.biz.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.doyd.biz.ILeaveService;
import com.doyd.core.CoreVars;
import com.doyd.core.action.common.Page;
import com.doyd.core.biz.impl.MyTransSupport;
import com.doyd.dao.ILeaveDao;
import com.doyd.dao.IReadLeaveDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.model.Leave;
import com.doyd.model.ReadLeave;
import com.doyd.model.UserGroup;
import com.doyd.model.WeixinGroup;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import org.doyd.utils.StringUtil;

@Service
public class LeaveService extends MyTransSupport implements ILeaveService {
	@Autowired
	private ILeaveDao leaveDao;
	@Autowired
	private IWeixinGroupDao weixinGroupDao;
	@Autowired
	private IUserGroupDao userGroupDao;
	@Autowired
	private IWeixinUserDao weixinUserDao;
	@Autowired
	private IReadLeaveDao readLeaveDao;
	
	private String FIELD1 = "newLeave";
	private String FIELD2 = "totalLeave";

	@Override
	public ApiMessage queryLeaveByPage(Page page, String openGId, int wuId,
			int type, String key, ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		
		UserGroup userGroup = userGroupDao.getUserGroup(wuId, group.getGroupId());
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		if(userGroup.isTeacher()){
			//老师将审批所有的
			userGroupDao.clearCnt(wuId, group.getGroupId(), FIELD1);
		}
		List<Map<String, Object>> leaveList = leaveDao.queryLeaveByPage(page, userGroup.getGroupId(), type, key);
		if(userGroup.isTeacher() && !userGroup.isDirector()){
			for (Map<String, Object> leave : leaveList) {
				if(StringUtil.parseIntByObj(leave.get("auditState")) == 1){
					leave.put("auditState", 2);//待审批，老师看到的是审批中
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", leaveList);
		map.put("totalPage", page.getTotalPage());
		return new ApiMessage(reqCode, ReqState.Success).setInfo(map);
	}

	@Override
	public ApiMessage auditLeave(WeixinUser weixinUser, int leaveId,
			int auditState, String auditResult, ReqCode reqCode) {
		Leave leave = leaveDao.getLeaveById(leaveId);
		if(leave==null){
			return new ApiMessage(reqCode, ReqState.NoExistLeave);
		}
		UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), leave.getGroupId());
		if(userGroup==null || !userGroup.isDirector()){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		leave.setAuditResult(auditResult);
		leave.setAuditState(auditState);
		leave.setAuditWuId(weixinUser.getWuId());
		TransactionStatus txStatus = getTxStatus();
		boolean r = false;
		try {
			r = leaveDao.updateLeave(leave) 
					&& userGroupDao.downCnt(weixinUser.getWuId(), leave.getGroupId(), FIELD1);
		} catch (Exception e) {
			r = false;
		}finally{
			commit(txStatus, r);
		}
		if(!r){
			return new ApiMessage(reqCode, ReqState.Failure);
		}
		return new ApiMessage(reqCode, ReqState.Success);
	}

	@Override
	public ApiMessage getLeaveCnt(WeixinUser weixinUser, String openGId,
			int type, ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		UserGroup userGroup = userGroupDao.getUserGroup(weixinUser.getWuId(), group.getGroupId());
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		int leaveCnt = leaveDao.getLeaveCnt(userGroup.getGroupId(), type);
		return new ApiMessage(reqCode, ReqState.Success).setInfo(leaveCnt);
	}
	
	public static final String LEAVE_DATA = "\\d{4}\\/(0[1-9]|1[0-2])\\/(0[1-9]|[12][0-9]|3[0-1])( " +CoreVars.RULES_TIME + ")?";
	

	@Override
	public ApiMessage getLeaveList(String openId, String identity, int ugId,
			int state, int page) {
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(identity)
				|| ugId <= 0 || state < 0 || state > 4){
			return new ApiMessage(ReqCode.Leave, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.Leave, ReqState.NoExistUser);
		}
		UserGroup ug = userGroupDao.getUserGroup(ugId);
		if(ug == null){
			return new ApiMessage(ReqCode.Leave, ReqState.ApiParamError);
		}
		if(ug.getWuId() != wu.getWuId()){
			return new ApiMessage(ReqCode.Leave, ReqState.NoPower);
		}
		int type = "director".equals(identity) ? 1 : "teacher".equals(identity) ? 2 : 3;
		List<Map<String, Object>> leaveList = leaveDao.getLeaveList(wu.getWuId(), ug.getGroupId(), ug.getChildId(), type, state, page);
		return new ApiMessage(ReqCode.Leave, ReqState.Success).setInfo(leaveList);
	}

	@Override
	public ApiMessage addLeave(String openId, int ugId, String reason,
			String startTime, String endTime) {
		//判断参数
		if(StringUtil.isEmpty(openId) || ugId <= 0 || StringUtil.isEmpty(reason) 
				|| StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime)){
			return new ApiMessage(ReqCode.CreateLeave, ReqState.ApiParamError);
		}
		//判断开始时间和结束时间格式
		if(!startTime.matches(LEAVE_DATA) || !endTime.matches(LEAVE_DATA)){
			return new ApiMessage(ReqCode.CreateLeave, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.CreateLeave, ReqState.NoExistUser);
		}
		UserGroup ug = userGroupDao.getUserGroup(ugId);
		if(ug == null){
			return new ApiMessage(ReqCode.CreateLeave, ReqState.ApiParamError);
		}
		if(!ug.isPatriarch() || ug.getChildId() <= 0){
			return new ApiMessage(ReqCode.CreateLeave, ReqState.NoPower);
		}
		Leave leave = new Leave();
		leave.setGroupId(ug.getGroupId());
		leave.setChildId(ug.getChildId());
		leave.setLeaveWuId(wu.getWuId());
		leave.setReason(reason);
		leave.setStartTime(startTime);
		leave.setEndTime(endTime);
		TransactionStatus txStatus = getTxStatus();
		boolean r = false;
		try {
			r = leaveDao.addLeave(leave) 
					&& userGroupDao.upCnt(ug.getGroupId(), FIELD1, 2) 
					&& weixinGroupDao.upCnt(ug.getGroupId(), FIELD2);
		} catch (Exception e) {
			r = false;
		}finally{
			commit(txStatus, r);
		}
		if(r){
			return new ApiMessage(ReqCode.CreateLeave, ReqState.Success).setInfo(leave.getLeaveId());
		}
		return new ApiMessage(ReqCode.CreateLeave, ReqState.Failure);
	}

	@Override
	public ApiMessage readLeave(String openId, int leaveId) {
		if(StringUtil.isEmpty(openId) || leaveId <= 0){
			return new ApiMessage(ReqCode.ReadLeave, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.ReadLeave, ReqState.NoExistUser);
		}
		Leave leave = leaveDao.getLeaveById(leaveId);
		if(leave == null){
			return new ApiMessage(ReqCode.ReadLeave, ReqState.NoExistLeave);
		}
		UserGroup ug = userGroupDao.getUserGroup(wu.getWuId(), leave.getGroupId());
		if(ug == null){
			return new ApiMessage(ReqCode.ReadLeave, ReqState.NoPower);
		}
		if(!ug.isTeacher() && !ug.isDirector()){
			return new ApiMessage(ReqCode.ReadLeave, ReqState.NoPower);
		}
		if(ug.isTeacher() && !ug.isDirector()){
			if(!readLeaveDao.isRead(leaveId, wu.getWuId())){
				ReadLeave rl = new ReadLeave();
				rl.setGroupid(leave.getGroupId());
				rl.setLeaveId(leaveId);
				rl.setWuId(wu.getWuId());
				if(readLeaveDao.addReadLeave(rl)){
					if(ug.getNewLeave() > 0){
						userGroupDao.downCnt(wu.getWuId(), leave.getGroupId(), FIELD1);
					}
				}else{
					return new ApiMessage(ReqCode.ReadLeave, ReqState.Failure);
				}
			}
			return new ApiMessage(ReqCode.ReadLeave, ReqState.Success);
		}
		//如果不是未阅读状态，返回
		if(leave.getAuditState() != 1){
			return new ApiMessage(ReqCode.ReadLeave, ReqState.Success);
		}
		if(leaveDao.readLeave(leaveId)){
			return new ApiMessage(ReqCode.ReadLeave, ReqState.Success);
		}
		return new ApiMessage(ReqCode.ReadLeave, ReqState.Failure);
	}
	
}
