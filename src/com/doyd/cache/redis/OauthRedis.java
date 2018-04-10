package com.doyd.cache.redis;

import org.springframework.stereotype.Component;

import com.doyd.cache.CacheKey;
import com.doyd.cache.IModelCache;
import com.doyd.cache.memory.SysCache;
import com.doyd.module.pc.model.Oauth;
import com.doyd.server.cache.CacheManager;
import org.doyd.utils.StringUtil;

@Component
public class OauthRedis implements IModelCache {
	
	public boolean set(String openId, Oauth oauth){
		if(StringUtil.isEmpty(openId) || oauth == null){
			return false;
		}
		CacheManager.getRedis().set(CacheKey.getOauthKey(openId), oauth);
		//设置失效时间为30分钟
		CacheManager.getRedis().expire(CacheKey.getOauthKey(openId), SysCache.getCache().getAppConfig().getCookieTimeout());
		return true;
	}
	
	public Oauth get(String openId){
		if(StringUtil.isEmpty(openId)){
			return null;
		}
		return (Oauth) CacheManager.getRedis().get(CacheKey.getOauthKey(openId));
	}
	
	public boolean setOpenId(String sessionKey, String openId){
		if(StringUtil.isEmpty(sessionKey) || StringUtil.isEmpty(openId)){
			return false;
		}
		CacheManager.getRedis().set(CacheKey.getOpenId(sessionKey), openId);
		//设置失效时间为30分钟
		CacheManager.getRedis().expire(CacheKey.getOpenId(sessionKey), SysCache.getCache().getAppConfig().getCookieTimeout());
		return true;
	}
	
	public String getOpenId(String sessionKey){
		if(StringUtil.isEmpty(sessionKey)){
			return null;
		}
		return (String) CacheManager.getRedis().get(CacheKey.getOpenId(sessionKey));
	}
	
	public void removeOauth(String sessionKey){
		CacheManager.getRedis().remove(CacheKey.getOauthKey(CacheKey.getOpenId(sessionKey)));
		CacheManager.getRedis().remove(CacheKey.getOpenId(sessionKey));
	}
	
	@Override
	public void init() {

	}
	

}
