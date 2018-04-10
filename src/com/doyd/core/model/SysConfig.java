package com.doyd.core.model;

import java.io.Serializable;

public class SysConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	private int configId; // 系统配置ID
	private String key; // 配置关键字
	private String name; // 字段说明
	private String valueType; // 字段值类型，枚举：int, boolean, string
	private int intValue; // 整型值
	private boolean boolValue; // boolean型值
	private String textValue; // 字符串值
	private String rule; // 字段正则表达式
	private String ctime; // 创建时间

	public int getConfigId() {
		return configId;
	}
	public void setConfigId(int configId) {
		this.configId = configId;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValueType() {
		return valueType;
	}
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	public int getIntValue() {
		return intValue;
	}
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
	public boolean isBoolValue() {
		return boolValue;
	}
	public void setBoolValue(boolean boolValue) {
		this.boolValue = boolValue;
	}
	public String getTextValue() {
		return textValue;
	}
	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
}
