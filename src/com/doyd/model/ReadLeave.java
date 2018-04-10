package com.doyd.model;

import java.io.Serializable;

/**
 * @author ylb
 * @version 创建时间：2018-3-7 下午7:18:38
 */
public class ReadLeave implements Serializable {
	private static final long serialVersionUID = 1L;
	private int readId; // id
	private int groupid; // 群Id
	private int leaveId; // 请假审批Id
	private int wuId; // 用户Id
	private String ctime; // 添加时间
	public int getReadId() {
		return readId;
	}
	public void setReadId(int readId) {
		this.readId = readId;
	}
	public int getGroupid() {
		return groupid;
	}
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}
	public int getLeaveId() {
		return leaveId;
	}
	public void setLeaveId(int leaveId) {
		this.leaveId = leaveId;
	}
	public int getWuId() {
		return wuId;
	}
	public void setWuId(int wuId) {
		this.wuId = wuId;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
}
