package com.doyd.server.cache.redis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.doyd.server.cache.ICache;
import com.doyd.server.cache.SerializeUtil;
import org.doyd.utils.StringUtil;

public class RedisServer implements ICache {
	private static Logger logger = Logger.getLogger(RedisServer.class);
	private static String HOST = "";
	private static int PORT = 6379;
	private static String PASSWORD = "";
	private static JedisPool pool = null;
	
	public RedisServer(){
	}
	
	private void init(){
		JedisPoolConfig config = new JedisPoolConfig();
		//控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
		//如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
		config.setMaxTotal(1000);
		//控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
		config.setMaxIdle(20);
		//表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
		config.setMaxWaitMillis(10000);
		//在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
		config.setTestOnBorrow(true);
		pool = new JedisPool(config, HOST, PORT, 10000, PASSWORD);
	}
	
	/**
	 * port=0时，表示不修改默认端口
	 */
	public void setHost(String host, int port, String password){
		if(host==null || host.length()<=0){
			return;
		}
		if(host.equals(HOST) && (port==PORT || port==0) && ((password==null&&PASSWORD==null) 
				|| (password!=null && password.equals(PASSWORD)))
		){
			return;
		}
		if(pool!=null){
			pool.close();
		}
		HOST = host;
		PORT = port>0?port:PORT;
		PASSWORD = password;
		init();
	}
	
	public Jedis getJedis(){
		if(pool==null){
			init();
		}
		return pool.getResource();
	}
	
	private byte[] getKey(String key){
		return key.getBytes();
	}
	
	@Override
	public Object get(String key) {
		Jedis jedis = null;
		try{
			jedis = getJedis();
			byte[] value = jedis.get(getKey(key));
			if(value==null || value.length==0){
				return null;
			}
			Object o = SerializeUtil.unserialize(value);
			if(o==null && value!=null && value.length>0){
				try{
					o = new String(value);
				}catch (Exception e) {
					o = null;
				}
			}
			return o;
		} catch (Exception e) {
			if(key!=null && key.endsWith("_reflush_memory_timestamp")){
				return null;
			}
			logger.error("获取redis缓存失败，key："+key);
			logger.error(e);
			System.err.println("获取redis缓存失败，key："+key+",\r\nexception:"+e);
		} finally {
			if(jedis!=null){
				jedis.close();
			}
		}
		return null;
	}

	@Override
	public boolean set(String key, Object value) {
		if(value==null){
			return remove(key);
		}
		Jedis jedis = null;
		try{
			jedis = getJedis();
			byte[] v = SerializeUtil.serialize(value);
			String status = jedis.set(getKey(key), v);
			if("OK".equalsIgnoreCase(status)){
				return true;
			}else{
				System.err.println("修改redis缓存失败，key："+key+",value:"+value+",status:"+status);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("修改redis缓存失败，key："+key+",value:"+value);
			logger.error(e);
			System.err.println("修改redis缓存失败，key："+key+",value:"+value+",\r\nexception:"+e);
		} finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return false;
	}
	
	public boolean set(String key, int value) {
		Jedis jedis = null;
		try{
			jedis = getJedis();
			String status = jedis.set(key, value+"");
			if("OK".equalsIgnoreCase(status)){
				return true;
			}else{
				System.err.println("修改redis缓存失败，key："+key+",value:"+value+",status:"+status);
			}
		} catch (Exception e) {
			logger.error("修改redis缓存失败，key："+key+",value:"+value);
			logger.error(e);
			System.err.println("修改redis缓存失败，key："+key+",value:"+value+",\r\nexception:"+e);
		} finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return false;
	}

	@Override
	public boolean remove(String key) {
		Jedis jedis = null;
		try{
			jedis = getJedis();
			Long status = jedis.del(key);
			return status!=null && status.longValue()>0;
		} catch (Exception e) {
			logger.error("删除redis缓存失败，key："+key);
			logger.error(e);
			System.err.println("删除redis缓存失败，key："+key+",\r\nexception:"+e);
		} finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return false;
	}
	
	public boolean removeByPattern(String pattern){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			Set<String> set = jedis.keys(pattern +"*");
			if(set!=null && set.size()>0){
				Long status = jedis.del(set.toArray(new String[]{}));
				if(status==null || status.longValue()<=0){
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			logger.error("删除redis缓存失败，key："+pattern);
			logger.error(e);
			System.err.println("删除redis缓存失败，key："+pattern+",\r\nexception:"+e);
		} finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return false;
	}
	
	public List<Object> getByPattern(String pattern){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			Set<String> set = jedis.keys(pattern +"*");
			if(set!=null && set.size()>0){
				List<Object> list = new ArrayList<Object>();
				Iterator<String> it = set.iterator();  
				while (it.hasNext()) {  
					String key = it.next();
					list.add(jedis.get(key));
				} 
				return list;
			}
			return null;
		} catch (Exception e) {
			logger.error("删除redis缓存失败，key："+pattern);
			logger.error(e);
			System.err.println("删除redis缓存失败，key："+pattern+",\r\nexception:"+e);
		} finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return null;
	}
	
	public boolean expire(String key, int seconds){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			Long status = jedis.expire(key, seconds);
			return status!=null && status.longValue()>0;
		} catch (Exception e) {
			logger.error("设置redis缓存失效时间失败，key："+key+",seconds:"+seconds);
			logger.error(e);
			System.err.println("设置redis缓存失效时间失败，key："+key+",seconds:"+seconds+",\r\nexception:"+e);
		} finally {
			if(jedis!=null){
				jedis.close();
			}
		}
		return false;
	}
	
	public boolean zadd(String key, Map<String, Double> memberScoreMap){
		if(memberScoreMap==null || memberScoreMap.size()<=0){
			return true;
		}
		Jedis jedis = null;
		try{
			jedis = getJedis();
			Long status = jedis.zadd(key, memberScoreMap);
			return status!=null && status.longValue()>0;
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			sb.append("添加redis集合成员失败，key:").append(key).append(",member-score-map:\r\n");
			for(Map.Entry<String, Double> entry: memberScoreMap.entrySet()){
				sb.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			logger.error(sb.toString());
			logger.error(e);
			System.err.println(sb.toString()+"\r\nexception:"+e);
		} finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return false;
	}
	public boolean zadd(String key, String member, double score){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			Long status = jedis.zadd(key, score, ""+member);
			return status!=null && status.longValue()>0;
		} catch (Exception e) {
			logger.error("添加redis集合成员失败，key："+key+",score:"+score+",member:"+member);
			logger.error(e);
			System.err.println("添加redis集合成员失败，key："+key+",score:"+score+",member:"+member+",\r\nexception:"+e);
		} finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return false;
	}
	
	@Override
	public int zrank(String key, String member){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			Long status = jedis.zrank(key, ""+member);//redis排名从0开始
			return status==null?-1:(status.intValue()+1);
		} catch (Exception e) {
			logger.error("获取redis集合成员排名失败，key："+key+",member:"+member);
			logger.error(e);
			System.err.println("获取redis集合成员排名失败，key："+key+",member:"+member+",\r\nexception:"+e);
		} finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return -1;
	}
	
	@Override
	public int zrevrank(String key, String member){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			Long status = jedis.zrevrank(key, ""+member);//redis排名从0开始
			return status==null?-1:(status.intValue()+1);
		} catch (Exception e) {
			logger.error("获取redis集合成员排名失败，key："+key+",member:"+member);
			logger.error(e);
			System.err.println("获取redis集合成员排名失败，key："+key+",member:"+member+",\r\nexception:"+e);
		} finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return -1;
	}
	
	public long zscore(String key, String member){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			Double status = jedis.zscore(key, member);
			return status==null?0:status.longValue();
		} catch (Exception e) {
			logger.error("获取redis集合成绩失败，key："+key+",member:"+member);
			logger.error(e);
			System.err.println("获取redis集合排名失败，key："+key+",member:"+member+",\r\nexception:"+e);
		} finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return -1;
	}
	
	public List<String> zrange(String key, long start, long stop){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			Set<String> set = jedis.zrange(key, start, stop);
			if(set==null || set.size()<1){
				return null;
			}
			List<String> members = new ArrayList<String>();
			members.addAll(set);
			return members;
		} catch (Exception e) {
			logger.error("获取redis集合成员失败，key："+key);
			logger.error(e);
			System.err.println("获取redis集合成员失败，key："+key+",start:"+start+",stop:"+stop+",\r\nexception:"+e);
		} finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return null;
	}
	public List<String> zrangeByScore(String key, String min, String max){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			// min 开始Id -inf:代表最小值     (3:大于3    3:大于等于3 
			// max 结束Id +inf:代表最大值     (3:小于3    3:小于等于3
			min = StringUtil.isEmpty(min)?"-inf":min.replace(">", "(");
			max = StringUtil.isEmpty(max)?"+inf":max.replace("<", "(");
			Set<String> set = jedis.zrangeByScore(key, min, max);
			List<String> memberList = new ArrayList<String>();
			memberList.addAll(set);
			return memberList;
		} catch (Exception e) {
			logger.error("获取redis集合失败，key："+key);
			logger.error(e);
			System.err.println("获取redis集合失败，key："+key+",min:"+min+",max:"+max+",\r\nexception:"+e);
		} finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return null;
	}
	
	public int getNum(String key){
		Jedis jedis = null;
		try{
			jedis = getJedis();
			String o = jedis.get(key);
			if(o==null){
				return 0;
			}
			int v = StringUtil.parseInt(o);
			return v;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获取redis数值失败，key："+key);
			logger.error(e);
			System.err.println("获取redis数值失败，key："+key+",\r\nexception:"+e);
		} finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return 0;
	}

	@Override
	public int incr(String key) {
		Jedis jedis = null;
		try{
			jedis = getJedis();
			Long c = jedis.incr(key);
			return c==null?0:c.intValue();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("增加redis计数失败，key："+key);
			logger.error(e);
			System.err.println("增加redis计数失败，key："+key+",\r\nexception:"+e);
		} finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return 0;
	}
	
	@Override
	public int decr(String key) {
		Jedis jedis = null;
		try{
			jedis = getJedis();
			Long c = jedis.decr(key);
			return c==null?0:c.intValue();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("减少redis计数失败，key："+key);
			logger.error(e);
			System.err.println("减少redis计数失败，key："+key+",\r\nexception:"+e);
		} finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return 0;
	}
	@Override
	public long lpush(String key, Object ... objs) {
		Jedis jedis = null;
		try{
			jedis = getJedis();
			byte [][] bytes = null;
			if(objs != null && objs.length > 0){
				bytes = new byte[objs.length][];
				for(int i=0;i<objs.length;i++){
					bytes[i] = SerializeUtil.serialize(objs[i]);
				}
			}
			Long status = jedis.lpush(getKey(key), bytes);
			return status;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		} finally {
			if(jedis!=null){
				jedis.close();
			}
		}
		return 0;
	}

	@Override
	public List<Object> lrange(String key, int start, int stop) {
		Jedis jedis = null;
		try{
			jedis = getJedis();
			List<Object> list = new ArrayList<Object>();
			List<byte[]> result = jedis.lrange(getKey(key), start, stop);
			if(result != null && result.size() > 0){
				for(int i=0;i<result.size();i++){
					list.add(SerializeUtil.unserialize(result.get(i)));
				}
			}
			System.out.println("list:"+list);
			return list;
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if(jedis!=null){
				jedis.close();
			}
		}
		return null;
	}
	
}
