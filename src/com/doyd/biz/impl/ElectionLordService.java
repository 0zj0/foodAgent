package com.doyd.biz.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doyd.biz.IElectionLordService;
import com.doyd.dao.IElectionLogDao;
import com.doyd.dao.IElectionLordDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.model.ElectionLog;
import com.doyd.model.ElectionLord;
import com.doyd.model.WeixinGroup;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import org.doyd.utils.StringUtil;

@Service
public class ElectionLordService implements IElectionLordService {
	@Autowired
	private IElectionLordDao electionLordDao;
	@Autowired
	private IWeixinUserDao weixinUserDao;
	@Autowired
	private IWeixinGroupDao weixinGroupDao;
	@Autowired
	private IUserGroupDao userGroupDao;
	@Autowired
	private IElectionLogDao electionLogDao;

	@Override
	public ApiMessage election(String openId, String openGId) {
		//参数异常
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(openGId)){
			return new ApiMessage(ReqCode.Election, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.Election, ReqState.NoExistUser);
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return new ApiMessage(ReqCode.Election, ReqState.NoExistGroup);
		}
		//判断群内是否存在班主任
		if(wg.getDirectorId() <= 0){
			return new ApiMessage(ReqCode.Election, ReqState.GroupNoExistDirector);
		}
		//获得班主任信息
		WeixinUser directorWu = weixinUserDao.getUserById(wg.getDirectorId());
		if(directorWu == null){
			return new ApiMessage(ReqCode.Election, ReqState.GroupNoExistDirector);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("existCorrect", false);
		//获得用户在群内的选举列表
		List<ElectionLord> elList = electionLordDao.getElectionLordList(wu.getWuId(), wg.getGroupId());
		if(elList != null && elList.size() > 0){
			for (ElectionLord el : elList) {
				//如果选举信息中存在纠正信息
				if(el.getElectionType() == 1 && el.getState() != 2){
					resultMap.put("existCorrect", true);
					resultMap.put("correctResult", el.getState());
				}else if(el.getElectionType() == 2 && el.getState() == 2){
					resultMap.put("existCorrect", false);
				}
			}
		}
		resultMap.put("existIdentity", false);
		//判断用户是否存在身份
		if(wu.isDirector()) resultMap.put("existIdentity", true);
		if(wu.isTeacher()) resultMap.put("existIdentity", true);
		if(wu.isPatriarch()) resultMap.put("existIdentity", true);
		resultMap.put("directorName", directorWu.getNickName());
		resultMap.put("directorPic", directorWu.getAvatarUrl());
		resultMap.put("groupName", wg.getGroupName());
		return new ApiMessage(ReqCode.Election, ReqState.Success).setInfo(resultMap);
	}

	@Override
	public ApiMessage addElection(String openId, String openGId,
			String electionType) {
		//参数异常
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(openGId) 
				|| (!"correct".equals(electionType) && !"vote".equals(electionType))){
			return new ApiMessage(ReqCode.CreateElection, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.CreateElection, ReqState.NoExistUser);
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return new ApiMessage(ReqCode.CreateElection, ReqState.NoExistGroup);
		}
		//判断群内是否存在班主任
		if(wg.getDirectorId() <= 0){
			return new ApiMessage(ReqCode.CreateElection, ReqState.GroupNoExistDirector);
		}
		int electionType2 = "correct".equals(electionType) ? 1 : 2;
		//获得选举列表
		List<ElectionLord> elList = electionLordDao.getElectionLordList(wu.getWuId(), wg.getGroupId());
		if(elList != null && elList.size() > 0){
			boolean correct = false;
			boolean vote = false;
			for (int i=0;i<elList.size();i++) {
				ElectionLord el = elList.get(i);
				//判断是否存在纠正信息，并且纠正未通过
				if(el.getElectionType() == 1 && el.getState() != 2){
					correct = true;
				//判断是否存在正在进行的投票信息
				}else if(el.getElectionType() == 2 && el.getState() == 1){
					vote = true;
				//如果有通过了的选举，则刷新
				}else if(el.getState() == 2){
					correct = false;
					vote = false;
				}
			}
			//如果添加类型时纠正，判断是否存在未通过的纠正信息
			if(electionType2 == 1 && correct){
				return new ApiMessage(ReqCode.CreateElection, ReqState.UserExistElection);
			}
			//如果添加类型为投票，判断是否存在正在进行的投票
			if(electionType2 == 2 && vote){
				return new ApiMessage(ReqCode.CreateElection, ReqState.UserExistVote);
			}
		}
		ElectionLord el = new ElectionLord();
		el.setGroupId(wg.getGroupId());
		el.setCandidateWuId(wu.getWuId());
		el.setElectionType(electionType2);
		el.setParticipant(wg.getDirectorId());
		boolean r = electionLordDao.addElectionLord(el);
		if(r){
			return new ApiMessage(ReqCode.CreateElection, ReqState.Success).setInfo(el.getElectionId());
		}else{
			return new ApiMessage(ReqCode.CreateElection, ReqState.Failure);
		}
	}

	@Override
	public ApiMessage showElection(String openId, int electionId) {
		//参数异常
		if(StringUtil.isEmpty(openId) || electionId <= 0){
			return new ApiMessage(ReqCode.ElectionShow, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.ElectionShow, ReqState.NoExistUser);
		}
		//获得选举信息
		ElectionLord el = electionLordDao.getElectionLord(electionId);
		if(el == null){
			return new ApiMessage(ReqCode.ElectionShow, ReqState.ApiParamError);
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(el.getGroupId());
		if(wg == null){
			return new ApiMessage(ReqCode.ElectionShow, ReqState.Failure);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("userLacking", wg.getPeopleCnt() < 9);
		//  需要同意成员数  =  群人数的1/3，* 2/3 - 已同意人数
		int needAgreeCnt = ceil(wg.getPeopleCnt(), 3) * 2 / 3 - el.getAgreeCnt();
		resultMap.put("needAgreeCnt", needAgreeCnt);
		//如果是纠正信息
		if(el.getElectionType() == 1){
			WeixinUser cWu = weixinUserDao.getUserById(el.getCandidateWuId());
			if(cWu == null){
				return new ApiMessage(ReqCode.ElectionShow, ReqState.Failure);
			}
			resultMap.put("candidateName", cWu.getNickName());
			resultMap.put("candidatePic", cWu.getAvatarUrl());
			resultMap.put("participantName", wu.getNickName());
			resultMap.put("participantPic", wu.getAvatarUrl());
		//如果是投票
		}else{
			//如果当前用户时参与人
			if(el.getParticipant() == wu.getWuId()){
				//如果参与人不是班主任班主任
				if(wu.getWuId() != wg.getDirectorId()){
					return new ApiMessage(ReqCode.ElectionShow, ReqState.NoPower);
				}
				resultMap.put("participantName", wu.getNickName());
				resultMap.put("participantPic", wu.getAvatarUrl());
				WeixinUser cWu = weixinUserDao.getUserById(el.getCandidateWuId());
				if(cWu == null){
					return new ApiMessage(ReqCode.ElectionShow, ReqState.Failure);
				}
				resultMap.put("candidateName", cWu.getNickName());
				resultMap.put("candidatePic", cWu.getAvatarUrl());
				resultMap.put("result", 2);
			//如果当前用户时候选人
			}else if(el.getCandidateWuId() == wu.getWuId()){
				resultMap.put("candidateName", wu.getNickName());
				resultMap.put("candidatePic", wu.getAvatarUrl());
				WeixinUser pWu = weixinUserDao.getUserById(el.getParticipant());
				if(pWu == null){
					return new ApiMessage(ReqCode.ElectionShow, ReqState.Failure);
				}
				resultMap.put("participantName", pWu.getNickName());
				resultMap.put("participantPic", pWu.getAvatarUrl());
				resultMap.put("result", 2);
			//如果当前用户是，群内普通成员
			}else{
				//如果当前用户不是群成员
				if(!userGroupDao.userInGroup(wu.getWuId(), wg.getGroupId())){
					return new ApiMessage(ReqCode.ElectionShow, ReqState.NoPower);
				}
				WeixinUser cWu = weixinUserDao.getUserById(el.getCandidateWuId());
				if(cWu == null){
					return new ApiMessage(ReqCode.ElectionShow, ReqState.Failure);
				}
				WeixinUser pWu = weixinUserDao.getUserById(el.getParticipant());
				if(pWu == null){
					return new ApiMessage(ReqCode.ElectionShow, ReqState.Failure);
				}
				resultMap.put("candidateName", cWu.getNickName());
				resultMap.put("candidatePic", cWu.getAvatarUrl());
				resultMap.put("participantName", pWu.getNickName());
				resultMap.put("participantPic", pWu.getAvatarUrl());
				ElectionLog log = electionLogDao.getElectionLog(el.getElectionId(), wu.getWuId());
				if(log != null){
					resultMap.put("existLog", true);
					resultMap.put("result", log.getAgree());
				}else{
					resultMap.put("existLog", false);
				}
			}
		}
		resultMap.put("groupName", wg.getGroupName());
		resultMap.put("participateCnt", el.getParticipateCnt());
		resultMap.put("agreeCnt", el.getAgreeCnt());
		resultMap.put("electionType", el.getElectionType() == 1 ? "correct" : "vote");
		resultMap.put("state", el.getState());
		return new ApiMessage(ReqCode.ElectionShow, ReqState.Success).setInfo(resultMap);
	}
	private int ceil(int num, int multiple){
		int number = num / multiple;
		if(num%multiple != 0){
			number++;
		}
		return number;
	}

}
