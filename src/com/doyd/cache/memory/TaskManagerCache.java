package com.doyd.cache.memory;

import com.doyd.core.action.common.ControllerContext;
import com.doyd.server.broadcast.Publisher;
import com.doyd.server.task.TaskManager;
import com.doyd.server.task.ThreadManager;

public class TaskManagerCache {
	/**
	 * websocket
	 */
	private static ThreadManager smTaskManager = new ThreadManager();
	
	/**
	 * 普通任务
	 */
	private static ThreadManager threadManager = new ThreadManager();
	
	/**
	 * quartz任务
	 */
	private static TaskManager taskManager;

	public static ThreadManager getSmTaskManager() {
		return smTaskManager;
	}

	public static TaskManager getTaskManager() {
		if(taskManager==null){
			taskManager = (TaskManager) ControllerContext.getWac().getBean("taskManager");
		}
		return taskManager;
	}
	
	public static ThreadManager getThreadManager() {
		return threadManager;
	}

	/**
	 * 关闭所有任务
	 */
	public static void shutdown(){
		if(smTaskManager!=null){
			smTaskManager.shutdown();
		}
		if(threadManager!=null){
			threadManager.shutdown();
		}
		TaskManager manager = getTaskManager();
		if(manager!=null){
			manager.stop();
		}
		Publisher.getTaskManager().shutdown();
	}
}
