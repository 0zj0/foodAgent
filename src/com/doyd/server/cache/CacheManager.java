package com.doyd.server.cache;

import com.doyd.server.cache.memory.MemoryServer;
import com.doyd.server.cache.redis.RedisServer;

public class CacheManager {
	private static CacheManager manager = new CacheManager();
	// redis缓存
	private ICache redisCache = new RedisServer();
	
	private ICache memoryCache = new MemoryServer();
	
	/**
	 * 获取redis缓存管理器
	 * @return
	 */
	public static ICache getRedis(){
		return manager.redisCache;
	}
	
	public static ICache getMemory(){
		return manager.memoryCache;
	}
}
