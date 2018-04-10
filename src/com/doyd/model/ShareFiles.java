package com.doyd.model;

import java.io.Serializable;

public class ShareFiles implements Serializable {
	private static final long serialVersionUID = 1L;
	private int shareId; // 主键
	private int groupId; // 群Id
	private int ugId; // 用户和群的关系
	private int wuId; // 用户Id
	private int upDate; // 上传日期
	private int fileCnt; // 文档数
	private String storageTable; // 存储对应表的信息
	private String remark; // 备注
	private int state; // 状态 1 可用 2、删除
	private String ctime; // 添加或者修改时间

	public int getShareId() {
		return shareId;
	}
	public void setShareId(int shareId) {
		this.shareId = shareId;
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
	public int getUpDate() {
		return upDate;
	}
	public void setUpDate(int upDate) {
		this.upDate = upDate;
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
