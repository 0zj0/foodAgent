package com.doyd.cache.redis;

import org.springframework.stereotype.Component;

import com.doyd.cache.CacheKey;
import com.doyd.cache.IModelCache;
import com.doyd.server.cache.CacheManager;

@Component
public class ReflushTimestampCache implements IModelCache {
	
	public void init(){
		CacheManager.getRedis().remove(CacheKey.getReflushMemoryTimestampKey());
	}
	
	public void setReflushMemoryTimestamp(){
		long timestamp = System.currentTimeMillis();
		CacheManager.getRedis().set(CacheKey.getReflushMemoryTimestampKey(), timestamp);
	}
	
	public long getReflushMemoryTimestamp(){
		Long t = (Long) CacheManager.getRedis()
				.get(CacheKey.getReflushMemoryTimestampKey());
		return t==null?0:t.longValue();
	}

}
