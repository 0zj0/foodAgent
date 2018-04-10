package com.doyd.model;

import java.io.Serializable;

public class ElectionLog implements Serializable {
	private static final long serialVersionUID = 1L;
	private int recordId; // 主键
	private int electionId; // 选举Id
	private int wuId; // 用户Id
	private int agree; // 是否同意：0，否，1：是
	private String ctime; // 参加时间

	public int getRecordId() {
		return recordId;
	}
	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}
	public int getElectionId() {
		return electionId;
	}
	public void setElectionId(int electionId) {
		this.electionId = electionId;
	}
	public int getWuId() {
		return wuId;
	}
	public void setWuId(int wuId) {
		this.wuId = wuId;
	}
	public int getAgree() {
		return agree;
	}
	public void setAgree(int agree) {
		this.agree = agree;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
}
