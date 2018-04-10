package com.doyd.module.pc.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.doyd.utils.StringUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.doyd.core.action.common.ControllerContext;

public class LoginHandshakeInterceptor implements HandshakeInterceptor{

	@Override
	public void afterHandshake(ServerHttpRequest arg0, ServerHttpResponse arg1,
			WebSocketHandler arg2, Exception arg3) {
		
	}

	@Override
	public boolean beforeHandshake(ServerHttpRequest arg0,
			ServerHttpResponse res, WebSocketHandler handle,
			Map<String, Object> arg3) throws Exception {
		HttpServletRequest request = ((ServletServerHttpRequest) arg0).getServletRequest();
		String sessionKey = request.getParameter("sessionKey");
		String ip = ControllerContext.getIp(request);
		String userAgent = request.getHeader("user-agent");
		ServletServerHttpRequest req = (ServletServerHttpRequest) arg0;
		String url =  ControllerContext.getIPBasePath(req.getServletRequest());
		if(StringUtil.isNotEmpty(url)){
			arg3.put("url", url);
		}
		arg3.put("sessionKey", sessionKey);
		arg3.put("ip", ip);
		arg3.put("userAgent", userAgent);
		return true;
	}
}
