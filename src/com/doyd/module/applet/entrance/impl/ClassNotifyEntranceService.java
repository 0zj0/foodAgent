package com.doyd.module.applet.entrance.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.doyd.dao.IClassNotifyDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.model.ClassNotify;
import com.doyd.model.UserGroup;
import com.doyd.model.WeixinUser;
import com.doyd.module.applet.Enums.AppletOutEnum;
import com.doyd.module.applet.entrance.IEntranceService;
import com.doyd.msg.ApiMessage;
import org.doyd.utils.StringUtil;

/**
 * @author ylb
 * @version 创建时间：2017-12-14 下午6:24:17
 */
@Component
public class ClassNotifyEntranceService implements IEntranceService {
	
	@Autowired
	private IClassNotifyDao classNotifyDao;
	@Autowired
	private IUserGroupDao userGroupDao;

	@Override
	public ApiMessage entrance(WeixinUser wu, Map<String, Object> resultMap,
			ApiMessage am, String info1, String info2) {
		//获得班务通知Id
		int id = StringUtil.parseInt(info1);
		if(id <= 0){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		//获取班务通知
		ClassNotify classNotify = classNotifyDao.getClassNotify(id);
		//班务通知不存在，返回首页
		if(classNotify == null){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		UserGroup userGroup = userGroupDao.getUserGroup(wu.getWuId(), classNotify.getGroupId());
		//用户不存在群身份或已离开群
		if(userGroup == null){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		resultMap.put("pageType", AppletOutEnum.ClassNotify);
		resultMap.put("id", id);
		return am.setInfo(resultMap);
	}

}
