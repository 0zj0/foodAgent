package com.doyd.model;

import java.io.Serializable;

public class UserPcLogin implements Serializable {
	private static final long serialVersionUID = 1L;
	private int loginId; // 主键
	private int wuId; // 用户Id
	private int loginDate; // 登陆日期
	private String ip; // 登陆ip
	private String province; // 省份
	private String city; // 城市
	private String userAgent; // 浏览器信息
	private int duration; // 登陆时长
	private String loginSecret; // 当前用户登陆秘钥
	private long heartTime; // 上次心跳时间
	private long login; // 登陆日期
	private long quitOut; // 退出登陆时间

	public int getLoginId() {
		return loginId;
	}
	public void setLoginId(int loginId) {
		this.loginId = loginId;
	}
	public int getWuId() {
		return wuId;
	}
	public void setWuId(int wuId) {
		this.wuId = wuId;
	}
	public int getLoginDate() {
		return loginDate;
	}
	public void setLoginDate(int loginDate) {
		this.loginDate = loginDate;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getLoginSecret() {
		return loginSecret;
	}
	public void setLoginSecret(String loginSecret) {
		this.loginSecret = loginSecret;
	}
	public long getHeartTime() {
		return heartTime;
	}
	public void setHeartTime(long heartTime) {
		this.heartTime = heartTime;
	}
	public long getLogin() {
		return login;
	}
	public void setLogin(long login) {
		this.login = login;
	}
	public long getQuitOut() {
		return quitOut;
	}
	public void setQuitOut(long quitOut) {
		this.quitOut = quitOut;
	}
}
