package com.doyd.module.pc.socket;

import org.doyd.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.doyd.cache.redis.WebsocketSessionRedis;
import com.doyd.netty.cache.WebSocketCache;
import com.doyd.netty.model.SocketMessage;

public class LoginSocketHandler extends TextWebSocketHandler{
	
	@Autowired
	private WebsocketSessionRedis websocketSessionRedis;
	@Autowired
	private WebSocketCache webSocketCache;
	
    //接收文本消息，并发送出去
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    	try{
    		String sessionKey = (String)session.getAttributes().get("sessionKey");
			SocketMessage socketMessage = webSocketCache.get(sessionKey);
			if(socketMessage != null){
				session.sendMessage(new TextMessage(socketMessage.getMessage().getBytes("utf-8")));
				//清除redis缓存
				webSocketCache.remove(sessionKey);
			}else{
				super.handleTextMessage(session, message);
			}
    	}catch (Exception e) {
		}
    	
    }
    //连接建立后处理
    @Override
	public void afterConnectionEstablished(WebSocketSession session)
			throws Exception {
    	String sessionKey = (String)session.getAttributes().get("sessionKey");
    	String url = (String) session.getAttributes().get("url");
    	url = StringUtil.isEmpty(url) ? null : url+"msg/sendMessage";
		websocketSessionRedis.set(sessionKey, session, url);
    }
    
    //抛出异常时处理
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if(session.isOpen()){
            session.close();
        }
        String sessionKey = (String)session.getAttributes().get("sessionKey");
        websocketSessionRedis.deleteByKey(sessionKey);
    }
    //连接关闭后处理
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    	String sessionKey = (String)session.getAttributes().get("sessionKey");
    	websocketSessionRedis.deleteByKey(sessionKey);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    
}
