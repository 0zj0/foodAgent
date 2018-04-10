package com.doyd.model;

import java.io.Serializable;

public class ElectionLord implements Serializable {
	private static final long serialVersionUID = 1L;
	private int electionId; // 主键
	private int groupId; // 群Id
	private int candidateWuId; // 候选人Id
	private int electionType; // 选举类型 1、纠正 2、投票
	private long initiateTime; // 发起时间
	private int participant; // 参与人，当前班主任
	private int participateCnt; // 参与人数
	private int agreeCnt; // 同意人员数
	private int state; // 选举状态1:进行中，2：成功，3：失败，4：失效
	private String ctime; // 时间

	public int getElectionId() {
		return electionId;
	}
	public void setElectionId(int electionId) {
		this.electionId = electionId;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getCandidateWuId() {
		return candidateWuId;
	}
	public void setCandidateWuId(int candidateWuId) {
		this.candidateWuId = candidateWuId;
	}
	public int getElectionType() {
		return electionType;
	}
	public void setElectionType(int electionType) {
		this.electionType = electionType;
	}
	public long getInitiateTime() {
		return initiateTime;
	}
	public void setInitiateTime(long initiateTime) {
		this.initiateTime = initiateTime;
	}
	public int getParticipant() {
		return participant;
	}
	public void setParticipant(int participant) {
		this.participant = participant;
	}
	public int getParticipateCnt() {
		return participateCnt;
	}
	public void setParticipateCnt(int participateCnt) {
		this.participateCnt = participateCnt;
	}
	public int getAgreeCnt() {
		return agreeCnt;
	}
	public void setAgreeCnt(int agreeCnt) {
		this.agreeCnt = agreeCnt;
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
