package com.doyd.model;

import java.io.Serializable;

public class ClassNotify implements Serializable {
	private static final long serialVersionUID = 1L;
	private int notifyId; // 主键
	private int groupId; // 群Id
	private int ugId; // 上传人关系
	private int wuId; // 用户Id
	private String title; // 班务标题
	private String content; // 班务内容
	private int fileCnt; // 文件数量
	private String storageTable; // 存储对应表的信息
	private int readCnt; // 阅读人数
	private int state; // 班务状态
	private String ctime; // 提交时间

	public int getNotifyId() {
		return notifyId;
	}
	public void setNotifyId(int notifyId) {
		this.notifyId = notifyId;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public int getReadCnt() {
		return readCnt;
	}
	public void setReadCnt(int readCnt) {
		this.readCnt = readCnt;
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
