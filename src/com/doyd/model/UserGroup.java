package com.doyd.model;

import java.io.Serializable;

public class UserGroup implements Serializable {
	private static final long serialVersionUID = 1L;
	private int ugId; // 主键
	private int wuId; // 用户Id
	private int groupId; // 群Id
	private int childId; // 孩子Id(可能为空)
	private boolean patriarch; // 是否为家长
	private boolean teacher; // 是否为老师
	private boolean director; // 是否为班主任
	private String subjects; // 所教科目[]为数组
	private String aliasName; // 群名称
	private String phone; // 联系电话
	private int newWork; // 最新作业数量
	private int newNotify; // 最新班务通知
	private int newShare; // 最新共享文件数量
	private int newLeave; // 最新请假通知
	private int newAchievement; // 最新成绩单数量
	private int state; // 用户与群关系状态
	private String ctime; // 创建或者修改时间

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
	public boolean isPatriarch() {
		return patriarch;
	}
	public void setPatriarch(boolean patriarch) {
		this.patriarch = patriarch;
	}
	public boolean isTeacher() {
		return teacher;
	}
	public void setTeacher(boolean teacher) {
		this.teacher = teacher;
	}
	public boolean isDirector() {
		return director;
	}
	public void setDirector(boolean director) {
		this.director = director;
	}
	public String getSubjects() {
		return subjects;
	}
	public void setSubjects(String subjects) {
		this.subjects = subjects;
	}
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getNewWork() {
		return newWork;
	}
	public void setNewWork(int newWork) {
		this.newWork = newWork;
	}
	public int getNewNotify() {
		return newNotify;
	}
	public void setNewNotify(int newNotify) {
		this.newNotify = newNotify;
	}
	public int getNewShare() {
		return newShare;
	}
	public void setNewShare(int newShare) {
		this.newShare = newShare;
	}
	public int getNewLeave() {
		return newLeave;
	}
	public void setNewLeave(int newLeave) {
		this.newLeave = newLeave;
	}
	public int getNewAchievement() {
		return newAchievement;
	}
	public void setNewAchievement(int newAchievement) {
		this.newAchievement = newAchievement;
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
