package com.doyd.module.applet.entrance.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.doyd.dao.IElectionLogDao;
import com.doyd.dao.IElectionLordDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.model.ElectionLord;
import com.doyd.model.WeixinGroup;
import com.doyd.model.WeixinUser;
import com.doyd.module.applet.Enums.AppletOutEnum;
import com.doyd.module.applet.entrance.IEntranceService;
import com.doyd.msg.ApiMessage;
import org.doyd.utils.StringUtil;

/**
 * @author ylb
 * @version 创建时间：2017-12-14 下午4:55:48
 */
@Component
public class VoteEntranceService implements IEntranceService{
	
	@Autowired
	private IElectionLordDao electionLordDao;
	@Autowired
	private IUserGroupDao userGroupDao;
	@Autowired
	private IElectionLogDao electionLogDao;
	@Autowired
	private IWeixinGroupDao weixinGroupDao;

	@Override
	public ApiMessage entrance(WeixinUser wu, Map<String, Object> resultMap, ApiMessage am, String info1, String info2) {
		if(StringUtil.isEmpty(info1) || StringUtil.isEmpty(info2)){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		//获取选举信息
		ElectionLord electionLord = electionLordDao.getElectionLord(info1, info2, 2);
		//不存在选举信息或选举不在进行中则进入首页
		if(electionLord == null){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		//用户不存在群身份或已离开群
		if(!userGroupDao.userInGroup(wu.getWuId(), electionLord.getGroupId())){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		//选举不在进行中，返回结果页面
		if(electionLord.getState() != 1){
			resultMap.put("id", electionLord.getElectionId());
			resultMap.put("pageType", AppletOutEnum.VoteResult);
			return am.setInfo(resultMap);
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(electionLord.getGroupId());
		if(wg == null){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		if(electionLord.getCandidateWuId() == wu.getWuId() 
				|| electionLord.getParticipant() == wu.getWuId()){
			resultMap.put("id", electionLord.getElectionId());
			resultMap.put("pageType", AppletOutEnum.VoteResult);
			return am.setInfo(resultMap);
		}
		//判断用户是否存在投票信息
		boolean exist = electionLogDao.exist(electionLord.getElectionId(), wu.getWuId());
		AppletOutEnum aoe = exist ? AppletOutEnum.VoteResult : AppletOutEnum.Vote;
		resultMap.put("pageType", aoe);
		resultMap.put("id", electionLord.getElectionId());
		return am.setInfo(resultMap);
	}

}
