package com.doyd.netty.cache;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import com.doyd.cache.CacheKey;
import com.doyd.cache.memory.TaskManagerCache;
import com.doyd.netty.model.SocketMessage;
import com.doyd.netty.task.WebSocketSendTask;
import com.doyd.server.cache.CacheManager;

/**
 * @author ylb
 * @version 创建时间：2018-3-23 下午7:06:35
 */
@Component
public class WebSocketCache {
	/**
	 * 存储
	 */
	private static Queue<String> socketMessageQueue = new ConcurrentLinkedQueue<>();
	
	public SocketMessage get(String sessionKey){
		return (SocketMessage) CacheManager.getRedis().get(CacheKey.getWebSocketKey(sessionKey));
	}
	
	public void set(String sessionKey, String message, String url, int sendType){
		SocketMessage sm = new SocketMessage();
		sm.setMessage(message);
		sm.setUrl(url);
		sm.setSendType(sendType);
		sm.setSessionKey(sessionKey);
		//将消息存入redis中
		CacheManager.getRedis().set(CacheKey.getWebSocketKey(sessionKey), sm);
		//设置消息存储时间30秒，
		CacheManager.getRedis().expire(sessionKey, 10);
		//将消息加入队列中
		socketMessageQueue.add(sessionKey);
	}
	
	public void remove(String sessionKey){
		CacheManager.getRedis().remove(CacheKey.getWebSocketKey(sessionKey));
	}
	
	public void send(){
		if(socketMessageQueue.size() <= 0){
			return;
		}
		Iterator<String> in = socketMessageQueue.iterator();
		while(in.hasNext()) {
			String sessionKey = in.next();
			//获得reids中的消息
			SocketMessage sm = this.get(sessionKey);
			if(sm == null){
				socketMessageQueue.remove(sessionKey);
				continue;
			}
			TaskManagerCache.getSmTaskManager().addTask(new WebSocketSendTask(sm));
		}
	}
	
}
