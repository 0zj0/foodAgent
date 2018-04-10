package com.doyd.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.doyd.cache.CacheKey;
import com.doyd.server.cache.CacheManager;
import org.doyd.httpclient.HttpClientUtil;
import org.doyd.utils.StringUtil;
import org.doyd.utils.WeiXinSign;


public class ApiMessageUtils {

	
	
	
	/**
	 * 向远处服务器发送消息
	 * @author wjs
	 * @date 2017-9-13 
	 *
	 * @param msn
	 * @param message
	 */
	public static boolean sendRemoteMsg(String url ,String msn,boolean isPrint ,String msgId,String message){
		//
		String token = ApiSign.buildToken(msn);
		//签名
		Map<String, Object> params  = new HashMap<String, Object>();
		params.put("token", token);
		params.put("timestamp", System.currentTimeMillis());
		params.put("nonce", StringUtil.getRandomString(8));
		String secret = ApiSign.generateSecret(token);
		if(isPrint){
			params.put("msgType", "printMsg");
			params.put("msgId", msgId);
		}
		String sign = WeiXinSign.sign(params, secret);
		params.put("sign", sign);
		//
		 url  = addParams(url, params);
		//
		String result;
		try {
			result = HttpClientUtil.postXml(url, message,"utf-8");
			if(StringUtil.isNotEmpty(result)){
				try {
					JSONObject json  = new JSONObject(result);
					return json.getInt("state") ==0;
				   } catch (JSONException e) {
				}
			}
		} catch (Exception e1) {
		}
		
		return false;
	}
	
	public static boolean rmRemoteMsg(String msn,String msgId){
		//
		String url  = String.valueOf(CacheManager.getRedis().get(CacheKey.getApiMsgPath(msn)));
		if(StringUtil.isEmpty(url) || StringUtil.isEmpty(msgId)){
			return false;
		}
		String token = ApiSign.buildToken(msn);
		//签名
		Map<String, Object> params  = new HashMap<String, Object>();
		params.put("token", token);
		params.put("timestamp", System.currentTimeMillis());
		params.put("nonce", StringUtil.getRandomString(8));
		String secret = ApiSign.generateSecret(token);
		params.put("msgId", msgId);
		params.put("msgType", "rmMsg");
		String sign = WeiXinSign.sign(params, secret);
		params.put("sign", sign);
		//
	     url  = addParams(url, params);
		//
		String result = HttpClientUtil.post(url, null);
		if(StringUtil.isNotEmpty(result)){
			try {
				JSONObject json  = new JSONObject(result);
				return json.getInt("state") ==0;
			   } catch (JSONException e) {
			}
		}
		return false;
	}
	
	private static String addParams(String url,Map<String, Object> params){
		if(StringUtil.isEmpty(url)){
			return null;
		}
		if(params == null || params.size() ==0){
			return url;
		}
		for(Entry<String, Object> entry : params.entrySet()){
			 url += (url.indexOf("?")>0?"&":"?")+entry.getKey()+"="+entry.getValue();
		}
		return url;
	}
}
