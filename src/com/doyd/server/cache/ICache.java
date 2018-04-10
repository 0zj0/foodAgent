package com.doyd.server.cache;

import java.util.List;
import java.util.Map;

public interface ICache {
	/**
	 * 设置服务器
	 * @param host
	 * @param port
	 * @param password
	 * @param databases 缓存库索引
	 */
	public void setHost(String host, int port, String password);
	/**
	 * 获取缓存
	 * @param key
	 * @return
	 */
	public Object get(String key);
	
	/**
	 * 设置缓存
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(String key, Object value);
	
	/**
	 * 设置缓存
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(String key, int value);
	
	/**
	 * 删除缓存
	 * @param key
	 * @return
	 */
	public boolean remove(String key);
	
	/**
	 * 根据正则表达式删除缓存
	 * @param pattern
	 * @return
	 */
	public boolean removeByPattern(String pattern);
	
	/**
	 * 设置缓存失效实现，单位秒
	 * @param key
	 * @param seconds 单位秒
	 * @return
	 */
	public boolean expire(String key, int seconds);
	
	/**
	 * 添加缓存集合map
	 * @param key
	 * @param memberScoreMap
	 * @return
	 */
	public boolean zadd(String key, Map<String, Double> memberScoreMap);
	
	/**
	 * 添加缓存集合
	 * 
	 * @param key
	 * @param member ID
	 * @param score 成绩
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2016-12-26 上午9:55:56
	 */
	public boolean zadd(String key, String member, double score);
	
	/**
	 * 获取成员排名，-1时，表示没有获取到排名，排名从1开始，从小到大排序
	 * @param key
	 * @param member
	 * @return
	 */
	public int zrank(String key, String member);
	
	/**
	 * 获取成员排名，-1时，表示没有获取到排名，排名从1开始，从大到小排序
	 * @param key
	 * @param member
	 * @return
	 */
	public int zrevrank(String key, String member);
	/**
	 * 获取成员成绩，0表示没有获取到成绩或者没有成绩
	 * @param key
	 * @param member
	 * @return
	 */
	public long zscore(String key, String member);
	
	/**
	 * 获取缓存集合的元素
	 * 序号位置从0开始，以-1作为排序的集合的最后一个元素，-2倒数第二元素等
	 * @param key
	 * @param start 开始位置
	 * @param stop 停止位置
	 * @return List<String>
	 * @author 创建人：ylb
	 * @date 创建时间：2016-12-26 上午10:33:48
	 */
	public List<String> zrange(String key, long start, long stop);
	
	/**
	 * 获取缓存集合的元素
	 * 
	 * @param key
	 * @param min 开始Id null:代表最小值     >3:大于3    3:大于等于3 
	 * @param max 结束Id null:代表最大值     <3:小于3    3:小于等于3
	 * @return List<String>
	 * @author 创建人：ylb
	 * @date 创建时间：2016-12-26 上午10:33:48
	 */
	public List<String> zrangeByScore(String key, String min, String max);
	
	public int getNum(String key);
	
	public int incr(String key);
	
	public int decr(String key);
	
	public long lpush(String key, Object ... objs);
	
	public List<Object> lrange(String key, int start, int stop);
}
