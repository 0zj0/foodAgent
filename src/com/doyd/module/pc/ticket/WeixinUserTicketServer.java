package com.doyd.module.pc.ticket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.doyd.biz.IWeixinUserService;
import com.doyd.cache.memory.SysCache;
import com.doyd.core.action.ticket.TicketBaseServer;
import com.doyd.core.action.ticket.TicketSession;
import com.doyd.model.WeixinUser;

@Component
public class WeixinUserTicketServer extends TicketBaseServer<WeixinUser> {
	@Autowired
	private IWeixinUserService weixinUserService;

	private String defaultPassword = "DOYD_WEIXIN_USER";
	
	@Override
	protected WeixinUser getUser(HttpServletRequest request, HttpServletResponse response, TicketSession ticket, String openId) {
		WeixinUser user = weixinUserService.getUser(openId);
		if(user!=null && checkTicket(request, response, ticket, openId, defaultPassword)){
			return user;
		}
		return null;
	}

	@Override
	public String getTicketPrefix() {
		return "weixin_user";
	}

	@Override
	public boolean addTicket(HttpServletRequest request, HttpServletResponse response, WeixinUser user) {
		return addTicket(request, response, user.getOpenId(), defaultPassword);
	}
	
	public int getCookieTimeout() {
		return SysCache.getCache().getAppConfig().getCookieTimeout();
	}

	@Override
	public void deleteTicket(HttpServletRequest request,
			HttpServletResponse response, WeixinUser user) {
		deleteTicket1(request, response, defaultPassword);
	}
}
