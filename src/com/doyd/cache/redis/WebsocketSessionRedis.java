package com.doyd.cache.redis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.doyd.cache.CacheKey;
import com.doyd.cache.IModelCache;
import com.doyd.model.SocketRedis;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.netty.cache.WebSocketCache;
import com.doyd.server.cache.CacheManager;
import com.doyd.util.ApiMessageUtils;
import org.doyd.httpclient.HttpClientUtil;
import org.doyd.utils.StringUtil;
import org.doyd.utils.Tools;

@Component
public class WebsocketSessionRedis implements IModelCache {
	
	private static Logger logger =Logger.getLogger(WebsocketSessionRedis.class);
	/**
	 * 根据页面参数获取session
	 */
	private  static Map<String, WebSocketSession> sessions = new ConcurrentHashMap<String, WebSocketSession>();
	@Autowired
	private WebSocketCache webSocketCache;
	
	/**
	 * 存储WebSocketSession
	 * @author wjs
	 * @date 2017-7-6 
	 *
	 * @param key
	 * @param session
	 * @param url
	 * @return 
	 * 	true:存储成功
	 */
	public  boolean set(String key,WebSocketSession session,String url){
		//判断是否合法
		if(StringUtil.isEmpty(key) || session == null ){
			return false;
		}
		//内存缓存中存在相同key的websocket，需要删除
		boolean isContain = sessions.containsKey(key);
		sessions.remove(key);
		//存入缓存内存中
		session.getAttributes().put("sessionKey", key);
		
		sessions.put(key, session);
		
		//把websocket的相关信息存于redis里面
		SocketRedis redis = new SocketRedis();
		redis.setOnline(true);
		redis.setUrl(url);
		redis.setIp((String) session.getAttributes().get("ip"));
		redis.setUserAgent((String) session.getAttributes().get("userAgent"));
		//
		CacheManager.getRedis().set(CacheKey.getWebsocketSessionRedis(key),redis);
		//一天的有效期
		CacheManager.getRedis().expire(CacheKey.getWebsocketSessionRedis(key), 86400);
	
		// 否则为心跳包自动更新websocket信息，不打印在线数量信息。
		if(!isContain){
			logger.info("当前在线ws数量："+sessions.size());			
		}
		return true;
	}
	
	
	public String getSocketRedisMsn(String key){
		SocketRedis redis = (SocketRedis) CacheManager.getRedis().get(CacheKey.getWebsocketSessionRedis(key));
		if(redis == null){
			return null;
		}
		return redis.getMsn();
	}
	
	public SocketRedis getSocketRedis(String key){
		return (SocketRedis) CacheManager.getRedis().get(CacheKey.getWebsocketSessionRedis(key));
	}
	
	
	
	
	/**
	 * 删除websocket内存
	 * @author wjs
	 * @date 2017-7-5 
	 *
	 * @param sessionId
	 */
	public void deleteByKey(String key){
		if(StringUtil.isEmpty(key)){
			return;
		}
		sessions.remove(key);
		//删除redis中的数据
		logger.info("当前在线数量："+sessions.size());
	}
	
	
	/**
	 * 获取websocketSession
	 * @author wjs
	 * @date 2017-7-5 
	 * @param key
	 * @return
	 */
	public WebSocketSession get(String key){
		if(StringUtil.isEmpty(key)){
			return null;
		}
		WebSocketSession session = sessions.get(key);
		if(session != null){
			return session;
		}
		
		return sessions.get(key);
	}
	
	/**
	 * 发送消息
	 * @author wjs
	 * @date 2017-7-5 
	 * @param key
	 * @param message
	 * @return
	 */
	public boolean sendMsg(String key,ApiMessage message){
		if(StringUtil.isEmpty(key) || message == null){
			return false;
		}
		WebSocketSession session =get(key);
		if(session == null){
			//如果session为空，来源是本地调用，需要获取redis地址，远程调用
			return sendApiMsg(key, message);
		}else{
			//发送消息
			return sendMsg(session, message);
			
		}
	}
	
	/**
	 * 发送远程服务器消息
	 * @Depiction：TODO
	 * @param key
	 * @param message
	 * @return boolean
	 * @author 王思敏
	 * @date 2017-8-4 下午3:07:52
	 */
	public boolean sendApiMsg(String key,ApiMessage message){
		
		Object obj = CacheManager.getRedis().get(CacheKey.getWebsocketSessionRedis(key));
		if(obj != null && obj instanceof SocketRedis){
			try {
				SocketRedis redis  = (SocketRedis) obj;
				redis = redis == null ? new SocketRedis():redis;
				if(StringUtil.isNotEmpty(redis.getUrl())){
					boolean flag = false;
					int i  =0 ;
					//多次发送待打印列表
					do {
						if(redis.getUrl().contains("netty")){
							//向 netty 发送消息
							flag = ApiMessageUtils.sendRemoteMsg(redis.getUrl(),redis.getMsn(), message.isNeedReply() , message.getId(), message.toString());
						}else{
							flag =  sendApiMsg(key, redis.getUrl(), message);
						}
						i++;
						
					} while (i < 3 && !flag);
					if(!flag){
						String url = redis.getUrl();
						//追加参数
						url = StringUtil.addParameter(url, "key", key);
						String time  = System.currentTimeMillis()+"";
						url = StringUtil.addParameter(url, "time", time);
						url = StringUtil.addParameter(url, "tag", Tools.getVerify(time, key));
						webSocketCache.set(key, message.toString(), url, 2);
					}
					return flag;
				}
			} catch (Exception e) {
			}
		}
		return false;
	}
	
	/**
	 * 向远程服务器发送消息
	 * @author wjs
	 * @date 2017-8-24 
	 *
	 * @param key
	 * @param url
	 * @param message
	 * @return
	 */
	private  boolean sendApiMsg(String key,String url,ApiMessage message){
			//追加参数
			url = StringUtil.addParameter(url, "key", key);
			String time  = System.currentTimeMillis()+"";
			url = StringUtil.addParameter(url, "time", time);
			url = StringUtil.addParameter(url, "tag", Tools.getVerify(time, key));
			String result  = HttpClientUtil.postJson(url, message.toJson());
			if(StringUtil.isNotEmpty(result)){
				try {
					JSONObject json = new JSONObject(result);
					return json.getBoolean("state");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		return false;
	}
	
	/**
	 * 发送消息
	 * @author wjs
	 * @date 2017-7-7 
	 *
	 * @param session
	 * @param message
	 * @return
	 */
	public boolean sendMsg(WebSocketSession session,ApiMessage message){
		if(session == null || !session.isOpen()){
			return false;
		}
		try {
			String sessionKey = (String) session.getAttributes().get("sessionKey");
			boolean flag = sessions.containsKey(sessionKey);
			if(!flag || ReqCode.WebSocketHeart.getCode()!= message.getReqCode()){
				if(!flag){
					sessionKey = sessionKey+"("+session.getAttributes().get("msn")+")";
				}
			}
			TextMessage mes = new TextMessage((message.toString()).getBytes("utf-8"));
			session.sendMessage(mes);
			return true;
		} catch (Exception e) {
			
		}
		return false;
	}
	
	/**
	 * 发送远程服务器过来的消息
	 * @author wjs
	 * @date 2017-7-5 
	 *
	 * @param key
	 * @param message
	 * @return
	 */
	public boolean sendRemoteMsg(String key,String message){
		if(StringUtil.isEmpty(key) || StringUtil.isEmpty(message)){
			return false;
		}
		WebSocketSession session = get(key);
		if(session != null){
			if(session.isOpen()){
				try {
					logger.info("向"+key+"发送消息："+message);
					TextMessage mes = new TextMessage((message).getBytes("utf-8"));
					session.sendMessage(mes);
					return true;
				} catch (Exception e) {
				}
			}
		}
		return false;
	}
	
	@Override
	public void init() {

	}
	

}
