package com.doyd.module.pc.ticket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.doyd.cache.memory.SysCache;
import com.doyd.core.action.ticket.TicketBaseServer;
import com.doyd.core.action.ticket.TicketSession;

@Component
public class PcLoginSignTicketServer extends TicketBaseServer<String> {

	private String defaultPassword = "DOYD_PC_LOGIN_SIGN";
	
	@Override
	protected String getUser(HttpServletRequest request, HttpServletResponse response, TicketSession ticket, String sign) {
		if(checkTicket(request, response, ticket, sign, defaultPassword)){
			return sign;
		}
		return null;
	}

	@Override
	public String getTicketPrefix() {
		return "pc_login_sign";
	}

	@Override
	public boolean addTicket(HttpServletRequest request, HttpServletResponse response, String sign) {
		return addTicket(request, response, sign, defaultPassword);
	}
	
	public int getCookieTimeout() {
		return SysCache.getCache().getAppConfig().getCookieTimeout();
	}

	@Override
	public void deleteTicket(HttpServletRequest request,
			HttpServletResponse response, String sign) {
		deleteTicket1(request, response, defaultPassword);
	}
}
