package com.doyd.model;

import java.io.Serializable;

public class Files implements Serializable {
	private static final long serialVersionUID = 1L;
	private int fileId; // 主键
	private int groupId; // 群Id
	private String fileType; // 文件类型
	private String fileName; // 文件名称
	private String fileFormat; // 文件格式
	private String storageTable; // 存储对应表的信息
	private int id; // 对应存储的Id
	private int fileSize; // 文件大小
	private String fileAddr; // 文件地址
	private int state; // 文件状态
	private int licitState; // 合法状态
	private int cDate; // 上传日期
	private String ctime; // 添加或者修改时间

	public int getFileId() {
		return fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileFormat() {
		return fileFormat;
	}
	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}
	public String getStorageTable() {
		return storageTable;
	}
	public void setStorageTable(String storageTable) {
		this.storageTable = storageTable;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileAddr() {
		return fileAddr;
	}
	public void setFileAddr(String fileAddr) {
		this.fileAddr = fileAddr;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getLicitState() {
		return licitState;
	}
	public void setLicitState(int licitState) {
		this.licitState = licitState;
	}
	public int getCDate() {
		return cDate;
	}
	public void setCDate(int cDate) {
		this.cDate = cDate;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
}
