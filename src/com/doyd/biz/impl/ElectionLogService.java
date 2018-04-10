package com.doyd.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.doyd.biz.IElectionLogService;
import com.doyd.core.biz.impl.MyTransSupport;
import com.doyd.dao.IAbortGroupDao;
import com.doyd.dao.IElectionLogDao;
import com.doyd.dao.IElectionLordDao;
import com.doyd.dao.IGroupMessageDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.model.AbortGroup;
import com.doyd.model.ElectionLog;
import com.doyd.model.ElectionLord;
import com.doyd.model.GroupMessage;
import com.doyd.model.UserGroup;
import com.doyd.model.WeixinGroup;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import org.doyd.utils.StringUtil;

@Service
public class ElectionLogService extends MyTransSupport implements IElectionLogService {
	@Autowired
	private IElectionLogDao electionLogDao;
	@Autowired
	private IWeixinUserDao weixinUserDao;
	@Autowired
	private IElectionLordDao electionLordDao;
	@Autowired
	private IWeixinGroupDao weixinGroupDao;
	@Autowired
	private IUserGroupDao userGroupDao;
	@Autowired
	private IGroupMessageDao groupMessageDao;
	@Autowired
	private IAbortGroupDao abortGroupDao;

	@Override
	public ApiMessage addElectionLog(String openId, int electionId, int result) {
		//判断参数
		if(StringUtil.isEmpty(openId) || electionId <= 0 || (result!=1 && result!=2)){
			return new ApiMessage(ReqCode.CreateElectionLog, ReqState.ApiParamError);
		}
		//获得用户信息
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.CreateElectionLog, ReqState.NoExistUser);
		}
		//获得选举信息
		ElectionLord el = electionLordDao.getElectionLord(electionId);
		if(el == null){
			return new ApiMessage(ReqCode.CreateElectionLog, ReqState.ApiParamError);
		}
		if(el.getState() != 1){
			return new ApiMessage(ReqCode.CreateElectionLog, ReqState.ElectionEnd);
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(el.getGroupId());
		if(wg == null){
			return new ApiMessage(ReqCode.CreateElectionLog, ReqState.NoExistGroup);
		}
		//如果群主不是参与人，选举过时
		if(wg.getDirectorId() != el.getParticipant()){
			return new ApiMessage(ReqCode.CreateElectionLog, ReqState.ElectionOutdate);
		}
		//判断用户是否参与过选举
		if(electionLogDao.exist(electionId, wu.getWuId())){
			return new ApiMessage(ReqCode.CreateElectionLog, ReqState.RepeatVote);
		}
		ElectionLog log = new ElectionLog();
		log.setElectionId(electionId);
		log.setWuId(wu.getWuId());
		log.setAgree(result == 1 ? 1 : 0);
		//判断候选人群关系
		boolean existUg = false;
		//已经存在老师身份
		boolean existTeacherIdentity = false;
		UserGroup candidateUg = userGroupDao.getUserGroup(el.getCandidateWuId(), wg.getGroupId());
		if(candidateUg != null){
			//如果候选人已经是班主任，返回投票超时
			if(candidateUg.isDirector()){
				return new ApiMessage(ReqCode.CreateElectionLog, ReqState.ElectionOutdate);
			}
			existTeacherIdentity = candidateUg.isTeacher();
			existUg = true;
			candidateUg.setDirector(true);
			candidateUg.setTeacher(false);
		}
		//获得参与者用户身份数量
		UserGroup ug = userGroupDao.getUserGroup(el.getParticipant(), wg.getGroupId());
		if(ug == null){
			return new ApiMessage(ReqCode.CreateElectionLog, ReqState.UserNotInGroup);
		}
		int identityCnt = 0;
		identityCnt = ug.isDirector() ? ++identityCnt : identityCnt;
		identityCnt = ug.isTeacher() ? ++identityCnt : identityCnt;
		identityCnt = ug.isPatriarch() ? ++identityCnt : identityCnt;
		
		int state = 0;
		//选举结果消息
		GroupMessage gm = null;
		//投票通过通知原班主任
		GroupMessage voteGm = null;
		//纠正
		if(el.getElectionType() == 1){
			//当前用户不是参与人
			if(wu.getWuId() != el.getParticipant()){
				return new ApiMessage(ReqCode.CreateElectionLog, ReqState.NoPower);
			}
			gm = new GroupMessage();
			gm.setWuId(el.getCandidateWuId());
			if(result == 1){
				gm.setGroupId(wg.getGroupId());
				gm.setTitle("纠正申请已通过");
				if(existTeacherIdentity){
					gm.setContent("你已成为" + wg.getGroupName() + "家长群助手班主任，拥有最高权限，并为您合并了老师身份");
				}else{
					gm.setContent("你已成为" + wg.getGroupName() + "家长群助手班主任");
				}
				gm.setMsgType(5);
				state = 2;
			}else{
				gm.setTitle("纠正申请未通过");
				gm.setGroupId(0);
				gm.setContent("您的申请已被对方拒绝，您还可以通过群成员投票申请成为班主任");
				gm.setMsgType(6);
				state = 3;
			}
		}else{
			if(wg.getPeopleCnt() >= 9){
				// 结束投票人数  =  群人数的1/3，* 2/3
				int endCnt = ceil(wg.getPeopleCnt(), 3) * 2 / 3;
				//投票参与人数
				int participateCnt = el.getParticipateCnt()+1;
				//投票同意人数
				int agreeCnt = el.getAgreeCnt()+(result == 1 ? 1 : 0);
				//同意人数大于等于结束投票人数
				if(endCnt <= agreeCnt){
					gm = new GroupMessage();
					gm.setWuId(el.getCandidateWuId());
					gm.setTitle("投票申请已通过");
					gm.setGroupId(wg.getGroupId());
					if(existTeacherIdentity){
						gm.setContent("你已成为" + wg.getGroupName() + "家长群助手班主任，拥有最高权限，并为您合并了老师身份");
					}else{
						gm.setContent("你已成为" + wg.getGroupName() + "家长群助手班主任");
					}
					gm.setMsgType(3);
					state = 2;
					voteGm = new GroupMessage();
					voteGm.setWuId(el.getParticipant());
					voteGm.setTitle("群投结果");
					voteGm.setGroupId(0);
					voteGm.setContent(wg.getGroupName() + "发起了群投，你在" + wg.getGroupName() + "群助手的班主任身份已经被转移");
					voteGm.setMsgType(3);
				//不同意人数大于等于结束投票人数
				}else if(endCnt <= participateCnt - agreeCnt){
					gm = new GroupMessage();
					gm.setWuId(el.getCandidateWuId());
					gm.setTitle("投票申请未通过");
					gm.setGroupId(0);
					gm.setContent("您的投票申请未通过");
					gm.setMsgType(4);
					state = 3;
				}
			}
		}
		ug = new UserGroup();
		ug.setWuId(el.getCandidateWuId());
		ug.setGroupId(wg.getGroupId());
		ug.setDirector(true);
		ug.setPhone(wu.getPhone());
		TransactionStatus txStatus = getTxStatus();
		boolean r = false;
		try {
			//添加选举信息参与人数
			r = electionLordDao.update(electionId, result, state);
			//添加用户消息
			if(r && gm != null){
				r = groupMessageDao.addGroupMessage(gm);
			}
			//添加用户消息
			if(r && voteGm != null){
				r = groupMessageDao.addGroupMessage(voteGm);
			}
			//添加选举记录
			r = r && electionLogDao.addLog(log);
			if(r && state == 2){
				if(existUg){
					//添加班主任身份
					r = userGroupDao.updateIdentity(candidateUg, true, false, false);
					//r = userGroupDao.addIdentity(el.getCandidateWuId(), wg.getGroupId(), true, false);
				}else{
					//添加关系
					r = userGroupDao.addUserGroup(ug);
				}
				r = r && weixinGroupDao.updateDirectorId(wg.getGroupId(), el.getCandidateWuId());
				if(identityCnt > 1){
					//解绑参与人的关系
					r = r && userGroupDao.unbindUserGroup(el.getParticipant(), wg.getGroupId(), 1, 0);
				}else{
					r = r && userGroupDao.deleteUserGroup(el.getParticipant(), wg.getGroupId(), 0);
				}
				AbortGroup ag = new AbortGroup();
				ag.setGroupId(el.getGroupId());
				ag.setWuId(el.getParticipant());
				ag.setAbortType(el.getElectionType());
				ag.setUgId(ug.getUgId());
				ag.setIdentities("director");
				ag.setOperatorId(wu.getWuId());
				r = r && abortGroupDao.addAbortGroup(ag);
			}
		} catch (Exception e) {
			r = false;
		}finally{
			commit(txStatus, r);
		}
		ReqState reqState = r ? ReqState.Success : ReqState.Failure;
		return new ApiMessage(ReqCode.CreateElectionLog, reqState);
	}
	private int ceil(int num, int multiple){
		int number = num / multiple;
		if(num%multiple != 0){
			number++;
		}
		return number;
	}
}
