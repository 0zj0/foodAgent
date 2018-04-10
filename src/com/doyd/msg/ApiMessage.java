package com.doyd.msg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;


public class ApiMessage implements Serializable{
	/**
	 * @author wjs
	 * @date 2017-8-22 
	 */
	private static final long serialVersionUID = 654134761254994365L;
	private int reqCode;
	private int state;
	private String message;
	private Object info;
	
	//需要保存队列的话，需要存储数据
	private boolean needReply = false;
	private String id;
	private long timestamp;//当前时间戳
	private long startTime ;//存入时间
	private String key;
	
	public ApiMessage(){
		this.timestamp = System.currentTimeMillis();
		this.startTime = timestamp;
		
	}
	public ApiMessage(ReqCode reqCode, ReqState reqState){
		this.reqCode = reqCode.getCode();
		this.state = reqState.getCode();
		this.message = reqState.getRemark();
		this.timestamp = System.currentTimeMillis();
		this.startTime = timestamp;
	}
	
	public long getStartTime() {
		return startTime;
	}
	public boolean isNeedReply() {
		return needReply;
	}
	public void setNeedReply(boolean needReply) {
		this.needReply = needReply;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public void setReqCode(int reqCode) {
		this.reqCode = reqCode;
	}
	public void setState(int state) {
		this.state = state;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getReqCode() {
		return reqCode;
	}
	public void setReqCode(ReqCode reqCode) {
		this.reqCode = reqCode.getCode();
	}
	public int getState() {
		return state;
	}
	public void setState(ReqState reqState) {
		this.state = reqState.getCode();
		this.message = reqState.getRemark();
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(ReqState reqState) {
		this.state = reqState.getCode();
		this.message = reqState.getRemark();
	}
	public Object getInfo() {
		return info;
	}
	public ApiMessage setInfo(Object info) {
		this.info = info;
		return this;
	}
	
	
	
	public JSONObject toJson() {
		try{
	        JSONObject json = new JSONObject();
	        //状态码
	        json.put("state", this.state);
	        json.put("reqCode", this.reqCode);
	        json.put("id", id);
	        //错误信息
	        if(this.message != null){
	        	json.put("message", this.message);
	        }
	        //业务数据
	        if(this.info != null){
	        	if (this.info instanceof String || this.info instanceof Number || this.info instanceof Boolean){
	        		json.put("info", this.info);
	        	}else{
	        		ObjectMapper objectMapper = new ObjectMapper();
	        		JSONObject infoJson = new JSONObject(objectMapper.writeValueAsString(this.info));
	        		json.put("info", infoJson);	        		
	        	}
	        }
	        return json;
	    }catch (Exception e){
	        e.printStackTrace();
	        return new JSONObject();
	    }
	}
	
	
	
	
	@Override
	public String toString() {
		try{
			Map<String, Object> resultMap = new HashMap<String,Object>();
	        //状态码
	        resultMap.put("state", this.state);
	        resultMap.put("reqCode", this.reqCode);
	        //错误信息
	        if(this.message != null){
	            resultMap.put("message", this.message);
	        }
	        //业务数据
	        if(this.info != null){
	            resultMap.put("info", this.info);
	        }
	        ObjectMapper objectMapper = new ObjectMapper();
	        return objectMapper.writeValueAsString(resultMap);
	        //return toJson().toString();
	    }catch (Exception e){
	        e.printStackTrace();
	        return "error";
	    }
	}
}
