package com.doyd.model;

import java.io.Serializable;

public class Applet implements Serializable {
	private static final long serialVersionUID = 1L;
	private int appletId; // 主键
	private String appletName; // 小程序名称
	private String appId; // 小程序appId
	private String ghId; // 小程序ghId
	private int appletType; // 小程序类型 1、家长群助手  2、赚班费
	private int state; // 1、启用 2、禁用
	private String ctime; // 创建时间

	public int getAppletId() {
		return appletId;
	}
	public void setAppletId(int appletId) {
		this.appletId = appletId;
	}
	public String getAppletName() {
		return appletName;
	}
	public void setAppletName(String appletName) {
		this.appletName = appletName;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getGhId() {
		return ghId;
	}
	public void setGhId(String ghId) {
		this.ghId = ghId;
	}
	public int getAppletType() {
		return appletType;
	}
	public void setAppletType(int appletType) {
		this.appletType = appletType;
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
