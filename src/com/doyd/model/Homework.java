package com.doyd.model;

import java.io.Serializable;

public class Homework implements Serializable {
	private static final long serialVersionUID = 1L;
	private int workId; // 主键
	private int groupId; // 群Id
	private int ugId; // 上传人关系
	private int wuId; // 用户Id
	private String subject; // 作业科目
	private String work; // 作业内容
	private int fileCnt; // 文件数量
	private String storageTable; // 存储对应表的信息
	private String submitTime; // 提交作业时间
	private int costTime; // 要求用时
	private int state; // 家庭作业状态
	private int readCnt; // 阅读人数
	private String ctime; // 提交时间

	public int getWorkId() {
		return workId;
	}
	public void setWorkId(int workId) {
		this.workId = workId;
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
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public int getFileCnt() {
		return fileCnt;
	}
	public void setFileCnt(int fileCnt) {
		this.fileCnt = fileCnt;
	}
	public String getStorageTable() {
		return storageTable;
	}
	public void setStorageTable(String storageTable) {
		this.storageTable = storageTable;
	}
	public String getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}
	public int getCostTime() {
		return costTime;
	}
	public void setCostTime(int costTime) {
		this.costTime = costTime;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getReadCnt() {
		return readCnt;
	}
	public void setReadCnt(int readCnt) {
		this.readCnt = readCnt;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
}
