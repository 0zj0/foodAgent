package com.doyd.model;

import java.io.Serializable;

public class DownRecord implements Serializable {
	private static final long serialVersionUID = 1L;
	private int recordId; // 主键
	private int fileId; // 文件Id
	private int wuId; // 用户Id
	private String ip; // 登陆ip
	private String province; // 省份
	private String city; // 城市
	private String userAgent; // 浏览器信息
	private int downDate; // 下载日期
	private int downHour; // 下载24小时区间
	private int state; // 状态：1成功，2：失败
	private String remark; // 下载备注
	private String ctime; // 下载时间

	public int getRecordId() {
		return recordId;
	}
	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}
	public int getFileId() {
		return fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	public int getWuId() {
		return wuId;
	}
	public void setWuId(int wuId) {
		this.wuId = wuId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public int getDownDate() {
		return downDate;
	}
	public void setDownDate(int downDate) {
		this.downDate = downDate;
	}
	public int getDownHour() {
		return downHour;
	}
	public void setDownHour(int downHour) {
		this.downHour = downHour;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
}
