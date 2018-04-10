package com.doyd.module.applet.Enums;
/**
 * @author ylb
 * @version 创建时间：2017-12-14 下午3:15:17
 */
public enum AppletEntranceEnum {
	Correct("correct", "纠正"),
	Vote("vote", "投票"),
	Work("work", "作业"),
	ClassNotify("classNotify", "班务通知"),
	Transfer("transfer", "主动转让群"),
	PC("pc", "PC端二维码链接"),
	Other("other", "其他入口");
	
	private AppletEntranceEnum(String type, String remark){
		this.type = type;
		this.remark = remark;
	}
	private String type;
	private String remark;
	public String getType() {
		return type;
	}
	public String getRemark() {
		return remark;
	}
	public static AppletEntranceEnum getEnum(String type){
		for (AppletEntranceEnum aee : AppletEntranceEnum.values()) {
			if(aee.getType().equals(type)){
				return aee;
			}
		}
		return AppletEntranceEnum.Other;
	}
}
