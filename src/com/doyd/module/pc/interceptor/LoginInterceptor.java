package com.doyd.module.pc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.doyd.biz.IUserPcLoginService;
import com.doyd.cache.redis.OauthRedis;
import com.doyd.core.CoreVars;
import com.doyd.core.action.common.ControllerContext;
import com.doyd.module.pc.model.Oauth;
import com.doyd.module.pc.ticket.SessionKeyTicketServer;
import com.doyd.module.pc.ticket.WeixinUserTicketServer;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;

public class LoginInterceptor implements HandlerInterceptor {
	
	@Autowired
	private WeixinUserTicketServer weixinUserTicketServer;
	@Autowired
	private SessionKeyTicketServer sessionKeyTicketServer;
	@Autowired
	private IUserPcLoginService userPcLoginService;
	@Autowired
	private OauthRedis oauthRedis;

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object)
			throws Exception {
		String uri = request.getServletPath();
		if (uri != null && uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		if (uri.matches(CoreVars.ANONYMOUS_ACCESS_PATH)) {
			return true;
		}
		String sessionKey = sessionKeyTicketServer.getUser(request, response);
		String openId = oauthRedis.getOpenId(sessionKey);
		Oauth oauth = oauthRedis.get(openId);
		/*WeixinUser weixinUser = weixinUserTicketServer.getUser(request, response);
		if(weixinUser == null){
			ApiMessage msg = new ApiMessage(ReqCode.Login, ReqState.SessionTimeOut);
			ControllerContext.print(response, msg.toString(), "text/html;charset=utf-8");
			return false;
		}*/
		if(oauth==null){
			ApiMessage msg = new ApiMessage(ReqCode.Login, ReqState.SessionTimeOut);
			ControllerContext.print(response, msg.toString(), "text/html;charset=utf-8");
			return false;
		}
		//覆盖之前的cookie
		sessionKeyTicketServer.addTicket(request, response, sessionKey);
		oauthRedis.setOpenId(sessionKey, openId);
		oauthRedis.set(openId, oauth);
		/*weixinUserTicketServer.addTicket(request, response, weixinUser);
		if(weixinUser.getLoginState()!=2){
			ApiMessage msg = new ApiMessage(ReqCode.Login, ReqState.WaitForLogin);
			ControllerContext.print(response, msg.toString(), "text/html;charset=utf-8");
			return false;
		}*/
		request.setAttribute("weixinUser", oauth.getWeixinUser());
		return true;
	}

}
