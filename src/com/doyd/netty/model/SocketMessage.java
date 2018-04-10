package com.doyd.netty.model;

import java.io.Serializable;

/**
 * @author ylb
 * @version 创建时间：2018-3-23 下午7:07:12
 */
public class SocketMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String sessionKey;
	private String message;
	private String url;
	private int sendType; // 消息发送类型 1、小程序发送至pc端   2、pc端发送消息 
	
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getSendType() {
		return sendType;
	}
	public void setSendType(int sendType) {
		this.sendType = sendType;
	}
}
