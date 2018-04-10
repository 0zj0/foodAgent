/**
 * 
 */
package com.doyd.module.pc.model;

import java.io.Serializable;

import com.doyd.model.WeixinUser;

/**
 * @author Administrator
 *
 */
public class Oauth implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String sessionKey;//会话key
	private String loginSecret;// 登录密钥
	private String ip;//请求ip地址
	private String userAgent;//请求userAgent
	private WeixinUser weixinUser; //用户信息
	public String getSessionKey() {
		return sessionKey;
	}
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	public String getLoginSecret() {
		return loginSecret;
	}
	public void setLoginSecret(String loginSecret) {
		this.loginSecret = loginSecret;
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
	public WeixinUser getWeixinUser() {
		return weixinUser;
	}
	public void setWeixinUser(WeixinUser weixinUser) {
		this.weixinUser = weixinUser;
	}

}
