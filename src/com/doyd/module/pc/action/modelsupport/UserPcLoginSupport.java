package com.doyd.module.pc.action.modelsupport;

import javax.servlet.http.HttpServletRequest;

import org.doyd.ip.IP;
import org.doyd.ip.IpUtil;
import org.doyd.utils.DateUtil;
import org.doyd.utils.StringUtil;
import org.springframework.stereotype.Component;

import com.doyd.core.action.common.ControllerContext;
import com.doyd.core.action.common.MsgContext;
import com.doyd.core.action.modelsupport.IModelSupport;
import com.doyd.model.UserPcLogin;

@Component
public class UserPcLoginSupport implements IModelSupport<UserPcLogin> {
	
	@Override
	public UserPcLogin get(HttpServletRequest request) {
		UserPcLogin record = new UserPcLogin();
		String ip = ControllerContext.getIp(request);
		IP IP = IpUtil.getIp(ip);
		String city = IP.getCity();
		String province = IP.getProvince();
		String userAgent = request.getHeader("user-agent");
		Long timestamp = System.currentTimeMillis();
		record.setIp(ip);
		record.setCity(city);
		record.setProvince(province);
		record.setUserAgent(userAgent);
		record.setLoginDate(StringUtil.parseInt(DateUtil.today().replaceAll("-", "")));
		record.setHeartTime(timestamp);
		record.setLogin(timestamp);
		return record;
	}

	@Override
	public MsgContext check(UserPcLogin record, String desc, int type) {
		return null;
	}
	
}
