package com.doyd.model;

import java.io.Serializable;

public class Leave implements Serializable {
	private static final long serialVersionUID = 1L;
	private int leaveId; // 主键
	private int groupId; // 群Id
	private int childId; // 孩子
	private int leaveWuId; // 请假人
	private String reason; // 理由
	private String startTime; // 开始时间
	private String endTime; // 结束时间
	private int auditWuId; // 审核人
	private String auditTime; // 审核时间
	private String auditResult; // 审核理由
	private int auditState; // 审核状态1，未审核，2：审核中（已经查看记录），3：审核成功，4：审核失败
	private String ctime; // 添加或者修改时间

	public int getLeaveId() {
		return leaveId;
	}
	public void setLeaveId(int leaveId) {
		this.leaveId = leaveId;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getChildId() {
		return childId;
	}
	public void setChildId(int childId) {
		this.childId = childId;
	}
	public int getLeaveWuId() {
		return leaveWuId;
	}
	public void setLeaveWuId(int leaveWuId) {
		this.leaveWuId = leaveWuId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getAuditWuId() {
		return auditWuId;
	}
	public void setAuditWuId(int auditWuId) {
		this.auditWuId = auditWuId;
	}
	public String getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(String auditTime) {
		this.auditTime = auditTime;
	}
	public String getAuditResult() {
		return auditResult;
	}
	public void setAuditResult(String auditResult) {
		this.auditResult = auditResult;
	}
	public int getAuditState() {
		return auditState;
	}
	public void setAuditState(int auditState) {
		this.auditState = auditState;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
}
