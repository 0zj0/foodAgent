package com.doyd.model;

import java.io.Serializable;

public class Subject implements Serializable {
	private static final long serialVersionUID = 1L;
	private int subjectId; // 主键
	private int wuId; // 用户Id
	private String subject; // 科目
	private int state; // 科目状态
	private String ctime; // 创建或者修改时间

	public int getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}
	public int getWuId() {
		return wuId;
	}
	public void setWuId(int wuId) {
		this.wuId = wuId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
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
