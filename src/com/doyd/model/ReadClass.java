package com.doyd.model;

import java.io.Serializable;

public class ReadClass implements Serializable {
	private static final long serialVersionUID = 1L;
	private int readId; // 主键
	private int groupId; // 群Id
	private int notifyId; // 班务通知Id
	private int wuId; // 阅读人
	private int interval; // 发布到阅读间隔时间
	private int readDate; // 阅读日期
	private int readHour; // 阅读所在区间小时
	private String ctime; // 添加或者修改时间

	public int getReadId() {
		return readId;
	}
	public void setReadId(int readId) {
		this.readId = readId;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getNotifyId() {
		return notifyId;
	}
	public void setNotifyId(int notifyId) {
		this.notifyId = notifyId;
	}
	public int getWuId() {
		return wuId;
	}
	public void setWuId(int wuId) {
		this.wuId = wuId;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public int getReadDate() {
		return readDate;
	}
	public void setReadDate(int readDate) {
		this.readDate = readDate;
	}
	public int getReadHour() {
		return readHour;
	}
	public void setReadHour(int readHour) {
		this.readHour = readHour;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
}
