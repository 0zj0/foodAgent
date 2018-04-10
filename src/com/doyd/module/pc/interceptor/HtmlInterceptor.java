package com.doyd.module.pc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.doyd.cache.redis.OauthRedis;
import com.doyd.core.action.common.ControllerContext;
import com.doyd.module.pc.model.Oauth;
import com.doyd.module.pc.ticket.SessionKeyTicketServer;

/**
 * @author ylb
 * @version 创建时间：2018-2-8 上午10:03:16
 */
public class HtmlInterceptor implements HandlerInterceptor{
	
	@Autowired
	private SessionKeyTicketServer sessionKeyTicketServer;
	@Autowired
	private OauthRedis oauthRedis;

	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		String basepath = ControllerContext.getBasePath(request);
		String sessionKey = sessionKeyTicketServer.getUser(request, response);
		String openId = oauthRedis.getOpenId(sessionKey);
		Oauth oauth = oauthRedis.get(openId);
		if(oauth==null){
			//request.getRequestDispatcher("login.html").forward(request, response);
			response.sendRedirect(basepath + "login.html");
			return false;
		}
		return true;
	}

}
