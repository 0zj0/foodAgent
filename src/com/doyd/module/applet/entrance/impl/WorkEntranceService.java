package com.doyd.module.applet.entrance.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.doyd.dao.IHomeworkDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.model.Homework;
import com.doyd.model.UserGroup;
import com.doyd.model.WeixinUser;
import com.doyd.module.applet.Enums.AppletOutEnum;
import com.doyd.module.applet.entrance.IEntranceService;
import com.doyd.msg.ApiMessage;
import org.doyd.utils.StringUtil;

/**
 * @author ylb
 * @version 创建时间：2017-12-14 下午5:58:27
 */
@Component
public class WorkEntranceService implements IEntranceService{
	
	@Autowired
	private IHomeworkDao homeworkDao;
	@Autowired
	private IUserGroupDao userGroupDao;

	@Override
	public ApiMessage entrance(WeixinUser wu, Map<String, Object> resultMap,
			ApiMessage am, String info1, String info2) {
		//获得作业Id
		int id = StringUtil.parseInt(info1);
		if(id == 0){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		//获得作业
		Homework homework = homeworkDao.getHomework(id);
		//作业不存在
		if(homework == null){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		UserGroup userGroup = userGroupDao.getUserGroup(wu.getWuId(), homework.getGroupId());
		//用户不存在群身份或已离开群
		if(userGroup == null){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		//如果拥有班主任身份或者（拥有老师身份并且作业是自己布置的）
		if(userGroup.isDirector() || 
				(userGroup.isTeacher() && homework.getWuId() == wu.getWuId())){
			resultMap.put("pageType", AppletOutEnum.WorkShow);
			resultMap.put("id", id);
			return am.setInfo(resultMap);
		//存在家长身份
		}else if(userGroup.isPatriarch()){
			resultMap.put("pageType", AppletOutEnum.Work);
			resultMap.put("id", id);
			return am.setInfo(resultMap);
		}
		//不存在身份，或者拥有老师身份但作业不是自己布置的
		resultMap.put("pageType", AppletOutEnum.Home);
		return am.setInfo(resultMap);
	}

}
