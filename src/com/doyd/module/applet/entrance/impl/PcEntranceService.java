package com.doyd.module.applet.entrance.impl;

import java.util.Map;

import org.doyd.utils.StringUtil;
import org.doyd.utils.Tools;
import org.springframework.stereotype.Component;

import com.doyd.cache.memory.SysCache;
import com.doyd.core.CoreVars;
import com.doyd.model.WeixinUser;
import com.doyd.module.applet.Enums.AppletOutEnum;
import com.doyd.module.applet.entrance.IEntranceService;
import com.doyd.msg.ApiMessage;

/**
 * @author ylb
 * @version 创建时间：2017-12-15 上午9:43:41
 */
@Component
public class PcEntranceService implements IEntranceService {

	@Override
	public ApiMessage entrance(WeixinUser wu, Map<String, Object> resultMap,
			ApiMessage am, String info1, String info2) {
		//sessionKey为空或时间戳为空，或者时间戳格式不正确
		if(StringUtil.isEmpty(info1) || StringUtil.isEmpty(info2)
				|| !info2.matches(CoreVars.RULES_INT)){
			resultMap.put("pageType", AppletOutEnum.PCError);
			resultMap.put("message", "链接错误");
			return am.setInfo(resultMap);
		}
		long timestamp = StringUtil.parseLong(info2);
		//时间戳 小于 当前时间的半个小时前
		if(timestamp < System.currentTimeMillis() - 30 * 60 * 1000){
			resultMap.put("pageType", AppletOutEnum.PCError);
			resultMap.put("message", "二维码失效");
			return am.setInfo(resultMap);
		}
		String secret = SysCache.getCache().getAppConfig().getPcLoginSecret();
		//sessionKey非法，返回错误
		if(Tools.verify(info1, secret, info2)){
			resultMap.put("pageType", AppletOutEnum.PCError);
			resultMap.put("message", "链接错误");
			return am.setInfo(resultMap);
		}
		//不是老师并且不是班主任
		if(!wu.isTeacher() && !wu.isDirector()){
			resultMap.put("pageType", AppletOutEnum.PCError);
			resultMap.put("message", "您没有权限进行此操作");
			return am.setInfo(resultMap);
		}
		resultMap.put("pageType", AppletOutEnum.PC);
		resultMap.put("code", info1);
		return am.setInfo(resultMap);
	}

}
