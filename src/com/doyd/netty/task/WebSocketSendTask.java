package com.doyd.netty.task;

import org.doyd.httpclient.HttpClientUtil;
import org.doyd.utils.StringUtil;

import com.doyd.core.action.common.ControllerContext;
import com.doyd.netty.cache.WebSocketCache;
import com.doyd.netty.model.SocketMessage;
import com.doyd.server.task.Task;

/**
 * @author ylb
 * @version 创建时间：2018-3-24 上午9:34:00
 */
public class WebSocketSendTask extends Task{
	
	private SocketMessage sm;
	
	public WebSocketSendTask(SocketMessage sm){
		this.sm = sm; 
	}

	@Override
	public void run() {
		String result = HttpClientUtil.postJson(sm.getUrl(), sm.getMessage());
		if(StringUtil.isNotEmpty(result)){
			WebSocketCache webSocketCache = (WebSocketCache) ControllerContext.getWac().getBean("webSocketCache");
			webSocketCache.remove(sm.getSessionKey());
		}
	}

}
