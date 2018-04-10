package com.doyd.core.action.ticket;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.doyd.utils.EncryptUtils;
import org.doyd.utils.StringUtil;

import com.doyd.cache.memory.SysCache;

public abstract class TicketBaseServer<T> {
	private final String SECRET = "ZE#@)!$";
	public TicketBaseServer(){
	}
	
	protected abstract String getTicketPrefix();
	
	protected String getTicketName(){
		return getTicketPrefix()+"_ticket";
	}
	
	protected String getUserTicketName(){
		return getTicketPrefix()+"_ticket_uname";
	}
	
	protected String getTimestampName(){
		return getTicketPrefix()+"_ticket_timestamp";
	}
	
	protected TicketSession getTicketSession(HttpServletRequest request){
		Cookie[] cookies = request.getCookies();
		if(cookies==null||cookies.length<=0){
			return null;
		}
		TicketSession ticket = new TicketSession();
		for(Cookie cookie: cookies){
			if(getUserTicketName().equals(cookie.getName())){
				ticket.setName(StringUtil.trim(cookie.getValue()));
			}else if(getTicketName().equals(cookie.getName())){
				ticket.setTicket(StringUtil.trim(cookie.getValue()));
			}else if(getTimestampName().equals(cookie.getName())){
				ticket.setTicketTimestamp(StringUtil.parseLong(cookie.getValue()));
			}
		}
		return ticket;
	}
	
	protected abstract T getUser(HttpServletRequest request, HttpServletResponse response, TicketSession ticket, String userName);
	
	public T getUser(HttpServletRequest request, HttpServletResponse response){
		TicketSession ticket = getTicketSession(request);
		if(ticket==null){
			return null;
		}
		T t = getUser(request, response, ticket, ticket.getName());
		return t;
	}
	
	protected boolean checkTicket(HttpServletRequest request, HttpServletResponse response, TicketSession ticket, String userName, String password){
		if(validTicket(ticket, password)){
			if(addTicket(request, response, userName, password)){
				return true;
			}
		}
		deleteTicket1(request, response, password);
		return false;
	}
	
	protected boolean addTicket(HttpServletRequest request, HttpServletResponse response, String suName, String password){
		if(StringUtil.isEmpty(suName) || StringUtil.isEmpty(password)){
			return false;
		}
		String contextPath = request.getContextPath();
		TicketSession ticket = createTicket(suName, password, System.currentTimeMillis());
		if(true){
			Cookie suNameCookie = new Cookie(getUserTicketName(), ticket.getName());
			suNameCookie.setPath(contextPath);
			suNameCookie.setMaxAge(-1);
			response.addCookie(suNameCookie);
		}
		if(true){
			Cookie cookie = new Cookie(getTicketName(), ticket.getTicket());
			cookie.setPath(contextPath);
			cookie.setMaxAge(-1);
			response.addCookie(cookie);
		}
		if(true){
			Cookie cookie = new Cookie(getTimestampName(), ticket.getTicketTimestamp()+"");
			cookie.setPath(contextPath);
			cookie.setMaxAge(-1);
			response.addCookie(cookie);
		}
		
		return true;
	}
	
	public abstract boolean addTicket(HttpServletRequest request, HttpServletResponse response, T user);
	
	public abstract void deleteTicket(HttpServletRequest request, HttpServletResponse response, T user);
	
	protected void deleteTicket1(HttpServletRequest request,
			HttpServletResponse response, String password) {
		if(StringUtil.isEmpty(password)){
			return;
		}
		Cookie[] cookies = request.getCookies();
		if(cookies==null||cookies.length<=0){
			return;
		}
		String contextPath = request.getContextPath();
		TicketSession ticket = getTicketSession(request);
		if(!validTicket(ticket, password)){
			return;
		}
		for(Cookie cookie: cookies){
			if(cookie.getName().equals(getUserTicketName())){
				cookie.setMaxAge(0);
				cookie.setPath(contextPath);
				response.addCookie(cookie);
			}
			if(cookie.getName().equals(getTicketName())){
				cookie.setMaxAge(0);
				cookie.setPath(contextPath);
				response.addCookie(cookie);
			}
			if(cookie.getName().equals(getTimestampName())){
				cookie.setMaxAge(0);
				cookie.setPath(contextPath);
				response.addCookie(cookie);
			}
		}
	}
	
	/**
	 * 获取cookie超时时间s
	 * @return
	 */
	protected int getCookieTimeout() {
		return SysCache.getCache().getAppConfig().getCookieTimeout();
	}
	
	
	protected boolean validTicket(TicketSession ticket, String password){
		if(StringUtil.isEmpty(ticket.getName())
				 || StringUtil.isEmpty(ticket.getTicket())
				 || StringUtil.isEmpty(password) 
		){
			return false;
		}
		//强制过期2017-06-06 15:00
		if(ticket.getTicketTimestamp()<=1496732542031l){
			return false;
		}
		long cookieTimeout = getCookieTimeout();
		if(cookieTimeout>0){
			long t = System.currentTimeMillis()-ticket.getTicketTimestamp();
			if(t<0 || t>cookieTimeout*1000){
				return false;
			}
		}
		TicketSession newTicket = createTicket(ticket.getName(), password, ticket.getTicketTimestamp());
		if(!ticket.getTicket().equals(newTicket.getTicket())){
			return false;
		}
		return true;
	}
	
	protected TicketSession createTicket(String userName, String password, long ticketTimestamp){
		TicketSession ticket = new TicketSession();
		ticket.setName(userName);
		ticket.setTicketTimestamp(ticketTimestamp);
		String tickPassword = EncryptUtils.MD5(password+ticketTimestamp+SECRET);
		String ticketKey = EncryptUtils.MD5(userName+ticketTimestamp+SECRET);
		String ticketValue = EncryptUtils.MD5(password+ticketTimestamp+SECRET);
		String sign = EncryptUtils.MD5(ticket.getName()+tickPassword
				+ticketKey+ticketValue+ticket.getTicketTimestamp()+SECRET);
		ticket.setTicket(ticketKey+ticketValue+tickPassword+sign);
		return ticket;
	}
}
