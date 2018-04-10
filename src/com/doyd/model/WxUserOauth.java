package com.doyd.model;

import java.io.Serializable;

public class WxUserOauth implements Serializable {
	private static final long serialVersionUID = 1L;
	private int wuoId; // 主键
	private String unionId; // 联合绑定Id
	private String appId; // 小程序appId
	private String openId; // 用户对小程序唯一的openId
	private int state; // 1、启用 2、禁用
	private String ctime; // 创建时间

	public int getWuoId() {
		return wuoId;
	}
	public void setWuoId(int wuoId) {
		this.wuoId = wuoId;
	}
	public String getUnionId() {
		return unionId;
	}
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
}
