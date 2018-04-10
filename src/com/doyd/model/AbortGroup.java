package com.doyd.model;

import java.io.Serializable;

public class AbortGroup implements Serializable {
	private static final long serialVersionUID = 1L;
	private int abortId; // 主键
	private int groupId; // 群Id
	private int ugId; // 群关系
	private int wuId; // 用户Id
	private String identities; // 身份数组，director,teacher,patriarch
	private int abortType; // 退出类型
	private int operatorId; // 操作人Id
	private String ctime; // 操作时间

	public int getAbortId() {
		return abortId;
	}
	public void setAbortId(int abortId) {
		this.abortId = abortId;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getUgId() {
		return ugId;
	}
	public void setUgId(int ugId) {
		this.ugId = ugId;
	}
	public int getWuId() {
		return wuId;
	}
	public void setWuId(int wuId) {
		this.wuId = wuId;
	}
	public String getIdentities() {
		return identities;
	}
	public void setIdentities(String identities) {
		this.identities = identities;
	}
	public int getAbortType() {
		return abortType;
	}
	public void setAbortType(int abortType) {
		this.abortType = abortType;
	}
	public int getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
}
