package com.doyd.module.applet.entrance.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.doyd.dao.IElectionLordDao;
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
 * @version 创建时间：2017-12-14 下午4:46:23
 */
@Component
public class CorrectEntranceService implements IEntranceService {
	
	@Autowired
	private IElectionLordDao electionLordDao;
	@Autowired
	private IWeixinGroupDao weixinGroupDao;

	@Override
	public ApiMessage entrance(WeixinUser wu, Map<String, Object> resultMap, ApiMessage am, String info1, String info2) {
		if(StringUtil.isEmpty(info1) || StringUtil.isEmpty(info2)){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		//获取选举信息
		ElectionLord electionLord = electionLordDao.getElectionLord(info1, info2, 1);
		//不存在选举信息或选举不在进行中则进入首页
		if(electionLord == null){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		//当前用户不是选举信息的参与人并且不是候选人
		if(electionLord.getParticipant() != wu.getWuId() 
				&& electionLord.getCandidateWuId() != wu.getWuId()){
			resultMap.put("pageType", AppletOutEnum.CorrectError);
			resultMap.put("id", electionLord.getElectionId());
			return am.setInfo(resultMap);
		}
		//如果是发起人进入--纠正状态为正在进行或者失效失败则，进入去投票页面
		if(electionLord.getCandidateWuId() == wu.getWuId() && electionLord.getState() != 2){
			resultMap.put("pageType", AppletOutEnum.ToVote);
			return am.setInfo(resultMap);
		}else if(electionLord.getCandidateWuId() == wu.getWuId() && electionLord.getState() == 2){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		//如果是参与人---并且状态不在进行中
		if(electionLord.getState() != 1){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		WeixinGroup weixinGroup = weixinGroupDao.getWeixinGroup(electionLord.getGroupId());
		//微信群不存在
		if(weixinGroup == null){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		//当前用户不是班主任
		if(weixinGroup.getDirectorId() != wu.getWuId()){
			resultMap.put("pageType", AppletOutEnum.CorrectError);
			return am.setInfo(resultMap);
		}
		resultMap.put("pageType", AppletOutEnum.Correct);
		resultMap.put("id", electionLord.getElectionId());
		return am.setInfo(resultMap);
	}

}
