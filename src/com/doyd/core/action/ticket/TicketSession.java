package com.doyd.core.action.ticket;

public class TicketSession {
	private String name = null;
	private String ticket = null;
	private long ticketTimestamp = 0l;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public long getTicketTimestamp() {
		return ticketTimestamp;
	}
	public void setTicketTimestamp(long ticketTimestamp) {
		this.ticketTimestamp = ticketTimestamp;
	}
}
