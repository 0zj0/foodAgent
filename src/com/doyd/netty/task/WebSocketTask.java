package com.doyd.netty.task;

import com.doyd.core.action.common.ControllerContext;
import com.doyd.netty.cache.WebSocketCache;
import com.doyd.server.task.Task;

/**
 * @author ylb
 * @version 创建时间：2018-3-23 下午7:07:29
 */
public class WebSocketTask extends Task {

	private static boolean isRun = false;
	
	@Override
	public void run() {
		if(isRun){
			return;
		}
		isRun = true;
		WebSocketCache webSocketCache = (WebSocketCache) ControllerContext.getWac().getBean("webSocketCache");
		if(webSocketCache != null){
			webSocketCache.send();
		}
		isRun = false;
	}

}
