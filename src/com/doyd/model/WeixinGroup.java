package com.doyd.model;

import java.io.Serializable;

public class WeixinGroup implements Serializable {
	private static final long serialVersionUID = 1L;
	private int groupId; // 主键
	private String groupPic; // 群合成图片
	private int picCnt; // 群图片用多少个用户头像合成的
	private String openGId; // 微信群返回的id
	private String groupName; // 群名称
	private int directorId; // 群主Id(wuId)
	private String city; // 群城市
	private int peopleCnt; // 群人数
	private int totalWork; // 总的作业数量
	private int totalNotify; // 总的班务通知数量
	private int totalShare; // 总的共享文件
	private int totalLeave; // 总的请假通知
	private int totalAchievement; // 总的成绩单数量
	private int state; // 状态
	private String ctime; // 添加或者修改时间

	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public String getGroupPic() {
		return groupPic;
	}
	public void setGroupPic(String groupPic) {
		this.groupPic = groupPic;
	}
	public int getPicCnt() {
		return picCnt;
	}
	public void setPicCnt(int picCnt) {
		this.picCnt = picCnt;
	}
	public String getOpenGId() {
		return openGId;
	}
	public void setOpenGId(String openGId) {
		this.openGId = openGId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public int getDirectorId() {
		return directorId;
	}
	public void setDirectorId(int directorId) {
		this.directorId = directorId;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getPeopleCnt() {
		return peopleCnt;
	}
	public void setPeopleCnt(int peopleCnt) {
		this.peopleCnt = peopleCnt;
	}
	public int getTotalWork() {
		return totalWork;
	}
	public void setTotalWork(int totalWork) {
		this.totalWork = totalWork;
	}
	public int getTotalNotify() {
		return totalNotify;
	}
	public void setTotalNotify(int totalNotify) {
		this.totalNotify = totalNotify;
	}
	public int getTotalShare() {
		return totalShare;
	}
	public void setTotalShare(int totalShare) {
		this.totalShare = totalShare;
	}
	public int getTotalLeave() {
		return totalLeave;
	}
	public void setTotalLeave(int totalLeave) {
		this.totalLeave = totalLeave;
	}
	public int getTotalAchievement() {
		return totalAchievement;
	}
	public void setTotalAchievement(int totalAchievement) {
		this.totalAchievement = totalAchievement;
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
