package com.doyd.model;

import java.io.Serializable;

public class SocketRedis  implements Serializable{

	/**
	 * @author wjs
	 * @date 2017-7-6 
	 *
	 */
	private static final long serialVersionUID = 2806300524922108003L;
	
	/**
	 * 是否在线
	 */
	private boolean online = false;
	
	/**
	 * 该key下模板服务器地址
	 */
	private String url;
	
	
	/**
	 * 服务器类型
	 */
	private String serverType = "tomcat";
	
	/**
	 * 客户端sn
	 */
	private String msn;
	
	private String ip;
	private String userAgent;
	

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMsn() {
		return msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	

	
	

}
