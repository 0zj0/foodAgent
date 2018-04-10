package com.doyd.module.applet.Enums;
/**
 * @author ylb
 * @version 创建时间：2017-12-14 上午11:12:23
 */
public enum AppletOutEnum {
	FirstEnter("FirstEnter", "第一次进入"),
	Authorized("Authorized", "授权"),
	CreateIdentity("CreateIdentity", "创建身份"),
	Home("Home", "首页"),
	ToVote("ToVote", "去投票页面"),
	Correct("Correct", "纠正"),
	CorrectError("CorrectError", "纠正身份错误"),
	Vote("Vote", "投票"),
	VoteResult("VoteResult", "投票结果"),
	Work("Work", "家长作业"),
	WorkShow("WorkShow", "老师或班主任作业"),
	ClassNotify("ClassNotify", "家长班务通知"),
	ClassNotifyShow("ClassNotifyShow", "老师或班主任班务通知"),
	Transfer("Transfer", "主动转让群"),
	PC("PC", "PC端二维码扫码后"),
	PCError("PCError", "PC端二维码扫码错误提示")
	;
	
	private AppletOutEnum(String code, String remark){
		this.code = code;
		this.remark = remark;
	}
	
	private String code;
	private String remark;
	
	public String getCode() {
		return code;
	}
	public String getRemark() {
		return remark;
	}
	
	@Override
	public String toString(){
		return this.code;
	}
	
}
