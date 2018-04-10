package com.doyd.model;

import java.io.Serializable;

public class AppletContact implements Serializable {
	private static final long serialVersionUID = 1L;
	private int acId; // 主键
	private int jzqzsId; // 家长群小程序Id
	private int classfeeId; // 赚班费小程序Id
	private int state; // 1、启用 2、禁用
	private String ctime; // 创建时间

	public int getAcId() {
		return acId;
	}
	public void setAcId(int acId) {
		this.acId = acId;
	}
	public int getJzqzsId() {
		return jzqzsId;
	}
	public void setJzqzsId(int jzqzsId) {
		this.jzqzsId = jzqzsId;
	}
	public int getClassfeeId() {
		return classfeeId;
	}
	public void setClassfeeId(int classfeeId) {
		this.classfeeId = classfeeId;
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
