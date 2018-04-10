package com.doyd.cache;


public class CacheKey {
	public static final String NAME_PREFIX = "JZQZS_";
	
	public static String getApiMsgPath(String id){
		return NAME_PREFIX+"API_MSG_PATH_"+id;
	}
	
	public static String getApiMsgKey(String key){
		return NAME_PREFIX+"API_MSG_"+key;
	}
	
	public static String getOauthKey(String openId){
		return NAME_PREFIX+"OAUTH_KEY_"+openId;
	}
	
	public static String getOpenId(String sessionKey){
		return NAME_PREFIX+"OPENID_"+sessionKey;
	}
	
	/**
	 * 获取redis的地址
	 * @author wjs
	 * @date 2017-7-5 
	 *
	 * @param key
	 * @return
	 */
	public static String getWebsocketSessionRedis(String key){
		return NAME_PREFIX+"WSR_URL"+key;
	}
	
	public static String getWebSocketKey(String key){
		return NAME_PREFIX+"WS_"+key;
	}
	
	public static String getAppletAccountKey(String appletId){
		return NAME_PREFIX+"applet_account_"+(appletId==null?"":appletId);
	}
	
	public static String getReflushMemoryTimestampKey(){
		 return NAME_PREFIX + "reflush_memory_timestamp";
	}
	public static String getaccessKey(String access) {
		return NAME_PREFIX+"access_"+(access==null?"":access);
	}
	
	public static String getApplet(String appId){
		return NAME_PREFIX + "applet_"+appId;
	}

}
