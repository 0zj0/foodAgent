package com.doyd.module.pc.ticket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.doyd.cache.memory.SysCache;
import com.doyd.core.action.ticket.TicketBaseServer;
import com.doyd.core.action.ticket.TicketSession;

@Component
public class SessionKeyTicketServer extends TicketBaseServer<String> {

	private String defaultPassword = "DOYD_PC_SESSION_KEY";
	
	@Override
	protected String getUser(HttpServletRequest request, HttpServletResponse response, TicketSession ticket, String sessionKey) {
		if(checkTicket(request, response, ticket, sessionKey, defaultPassword)){
			return sessionKey;
		}
		return null;
	}

	@Override
	public String getTicketPrefix() {
		return "pc_session_key";
	}

	@Override
	public boolean addTicket(HttpServletRequest request, HttpServletResponse response, String sessionKey) {
		return addTicket(request, response, sessionKey, defaultPassword);
	}
	
	public int getCookieTimeout() {
		return SysCache.getCache().getAppConfig().getCookieTimeout();
	}

	@Override
	public void deleteTicket(HttpServletRequest request,
			HttpServletResponse response, String sessionKey) {
		deleteTicket1(request, response, defaultPassword);
	}
}
