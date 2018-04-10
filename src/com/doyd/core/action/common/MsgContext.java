package com.doyd.core.action.common;

import org.json.JSONArray;
import org.json.JSONObject;

public class MsgContext {
	private boolean state;
	private String message;
	private String desc;
	private int id;
	private Object info;
	private String name;
	
	public MsgContext(){
	}
	
	public MsgContext(boolean state){
		this.state = state;
	}
	
	public MsgContext(boolean state, String msg){
		this.state = state;
		this.message = (msg!=null?msg:"")+(state?"成功":"失败");
	}
	public MsgContext(boolean state, int id,String msg){
		this.state = state;
		this.id = id;
		this.message = (msg!=null?msg:"")+(state?"成功":"失败");
	}
	public MsgContext(boolean state, String msg, String desc){
		this.state = state;
		this.message = (msg!=null?msg:"")+(state?"成功":"失败")+(desc!=null?("，"+desc):"");
		this.desc = desc;
	}
	
//	public MsgContext(boolean state, Object info){
//		this.state = state;
//		this.message = (msg!=null?msg:"")+(state?"成功":"失败");
//		this.info = info;
//	}
	
	public boolean isState() {
		return state;
	}
	public MsgContext setState(boolean state) {
		this.state = state;
		return this;
	}
	public String getMessage() {
		return message;
	}
	public MsgContext setMessage(String message) {
		this.message = message;
		return this;
	}
	public String getDesc() {
		return desc;
	}
	public Object getInfo() {
		return info;
	}
	public MsgContext setInfo(Object info) {
		this.info = info;
		return this;
	}
	public int getId() {
		return id;
	}
	public MsgContext setId(int id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public MsgContext setName(String name) {
		this.name = name;
		return this;
	}

	public String toString(){
		String message = this.message==null?null:this.message.toString();
		String info = this.info==null?null:this.info.toString();
		JSONObject json = new JSONObject();
		try{
			json.put("state", state);
			if(id!=0){
				json.put("id", id);
			}
			json.put("message", message);
			if(info!=null){
				json.put("info", info);
				try{
					if(info.startsWith("[")){
						JSONArray infoJson = new JSONArray(info);
						json.put("info", infoJson);
					}else if(info.startsWith("{")){
						JSONObject infoJson = new JSONObject(info);
						json.put("info", infoJson);
					}else{
						json.put("info", info);
					}
				}catch (Exception e) {
					json.put("info", info);
				}
			}
		}catch (Exception e) {
		}
		return json.toString();
		/*
		StringBuffer json = new StringBuffer();
		json.append("{\"state\":").append(state).append(",");
		if(id!=0){
			json.append("\"id\":").append(id).append(",");
		}
		json.append("\"message\":\"").append(message).append("\",");
		if(info!=null && ((info.startsWith("{") && info.endsWith("}")) 
				||( info.startsWith("[") && info.endsWith("]"))) ){
			json.append("\"info\":").append(info);
		}else{
			if(info!=null){
//				//当提示登陆超时时，需要特殊处理，登陆超时时message为登陆超时标志Vars.NO_LOGIN_TAG+登陆地址
//				if(message.startsWith(Vars.NO_LOGIN_TAG)){
//					String url = info.replaceFirst(Vars.NO_LOGIN_TAG, ""); 
//					json.append("\"message\":\"").append(Vars.NO_LOGIN_TAG).append("\"").append(",");
//					json.append("\"info\":\"").append(url).append("\"");
//				}else{
					info = info.replace("\"", "\\\"");
					json.append("\"info\":\"").append(info).append("\"");
//				}
			}else{
				json.append("\"info\":null");
			}
		}
		json.append("}");
		return json.toString();
		 */
	}
}
