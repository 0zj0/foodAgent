package com.doyd.model;

import java.io.Serializable;

public class WeixinUser implements Serializable {
	private static final long serialVersionUID = 1L;
	private int wuId; // 主键
	private String openId; // openId
	private String unionId; // 联合绑定Id
	private String nickName; // 用户昵称
	private int gender; // 0：未知、1：男、2：女
	private String country; // 国家
	private String province; // 省份
	private String city; // 城市
	private String avatarUrl; // 头像
	private long timestamp; // 登陆的时间戳
	private String phone; // 手机号码
	private String realName; // 真实姓名
	private boolean patriarch; // 是否为家长
	private boolean teacher; // 是否为老师
	private boolean director; // 是否为班主任
	private String source; // 来源
	private int regDate; // 注册日期(20171109)
	private int loginDate; // 最后登录日期(20171109)
	private String ctime; // 记录时间
	
	public int getWuId() {
		return wuId;
	}
	public void setWuId(int wuId) {
		this.wuId = wuId;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getUnionId() {
		return unionId;
	}
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
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
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
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
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public int getRegDate() {
		return regDate;
	}
	public void setRegDate(int regDate) {
		this.regDate = regDate;
	}
	public int getLoginDate() {
		return loginDate;
	}
	public void setLoginDate(int loginDate) {
		this.loginDate = loginDate;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
	
}
