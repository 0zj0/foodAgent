package com.doyd.server.init;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.doyd.initialization.InitWeixinSdk;
import org.springframework.web.context.WebApplicationContext;

import com.doyd.cache.memory.SysCache;
import com.doyd.cache.memory.TaskManagerCache;
import com.doyd.cache.redis.ReflushTimestampCache;
import com.doyd.core.action.common.ControllerContext;
import com.doyd.netty.task.WebSocketTask;
import com.doyd.server.task.ITaskServer;

public class InitCacheServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
		initAll();
		/**
		 * 当其中一个项目重启时，通知所有项目刷新缓存
		 */
		ReflushTimestampCache cache = (ReflushTimestampCache) ControllerContext.getWac().getBean("reflushTimestampCache");
		if(cache==null){
			return;
		}
		cache.setReflushMemoryTimestamp();
	}
	
	public static void initAll(){
		initMemory();
		initRedis();
		initTask();
	}
	
	public static void initRedis(){
		InitWeixinSdk.init(true);
		WebApplicationContext wc = ControllerContext.getWac();
		if(wc==null){
			return;
		}	
	}
	
	public static void initMemory(){
		SysCache.getCache().init();
		WebApplicationContext wc = ControllerContext.getWac();
		if(wc==null){
			return;
		}
	}
	
	public static void initTask(){
		ITaskServer taskServer = TaskManagerCache.getTaskManager().getTaskServer();
		if(taskServer!=null){
			// websocket每秒发送一次消息
			if(!taskServer.contains("WebSocketTask")){
				taskServer.addTask(WebSocketTask.class, "WebSocketTask", 1);
			}
			taskServer.start();
		}
	}
	
	
	@Override
	public void destroy() {
		System.out.println("系统关闭");
		//关闭所有任务
		TaskManagerCache.shutdown();
	}
}
