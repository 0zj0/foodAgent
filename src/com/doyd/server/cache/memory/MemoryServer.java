package com.doyd.server.cache.memory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.doyd.utils.StringUtil;

import com.doyd.server.cache.ICache;

public class MemoryServer implements ICache {
	private ConcurrentMap<String, Object> map = new ConcurrentHashMap<String, Object>();
	
	public Map<String, Object> getMap() {
		return map;
	}
	
	public void setHost(String host, int port, String password){
		
	}

	public Object get(String key) {
		return map.get(key);
	}

	public boolean set(String key, Object value) {
		if(StringUtil.isEmpty(key)){
			return false;
		}
		map.put(key, value);
		return true;
	}
	
	public boolean set(String key, int value){
		return set(key, value);
	}

	public boolean remove(String key) {
		map.remove(key);
		return true;
	}

	public boolean removeByPattern(String pattern) {
		if(StringUtil.isEmpty(pattern)){
			return false;
		}
		Set<String> keySet = map.keySet();
		for(String key: keySet){
			if(key.matches(pattern)){
				map.remove(key);
			}
		}
		return true;
	}

	public int getNum(String key) {
		return 0;
	}

	public int incr(String key) {
		return 0;
	}

	public int decr(String key) {
		return 0;
	}

	public boolean expire(String key, int seconds) {
		
		return false;
	}

	@Override
	public boolean zadd(String key, Map<String, Double> memberScoreMap) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zadd(String key, String member, double score) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int zrank(String key, String member) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int zrevrank(String key, String member) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long zscore(String key, String member) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> zrange(String key, long start, long stop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> zrangeByScore(String key, String min, String max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long lpush(String key, Object... objs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Object> lrange(String key, int start, int stop) {
		// TODO Auto-generated method stub
		return null;
	}

}
