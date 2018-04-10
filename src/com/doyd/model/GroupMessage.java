package com.doyd.model;

import java.io.Serializable;

public class GroupMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private int msgId; // 主键
	private int groupId;
	private int wuId;
	private int msgType; // 消息类型
	private String title; // 标题
	private String content; // 内容
	private int id; // 触发者微信Id
	private String pages; // 详情页面
	private int isRead; // 是否阅读，1：新添加 2、已推送  3、已阅读
	private String ctime; // 添加时间

	public int getMsgId() {
		return msgId;
	}
	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getWuId() {
		return wuId;
	}
	public void setWuId(int wuId) {
		this.wuId = wuId;
	}
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPages() {
		return pages;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
	public int getIsRead() {
		return isRead;
	}
	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
}
