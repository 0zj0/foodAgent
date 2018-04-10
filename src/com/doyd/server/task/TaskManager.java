package com.doyd.server.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskManager {
	@Autowired
	private ITaskServer quartzTaskMaster;
	
	private static TaskManager manager = new TaskManager();
	
	public static TaskManager getInstance(){
		return manager;
	}
	
	public void init(){
		//
	}
	
	public ITaskServer getTaskServer(){
		return quartzTaskMaster;
	}
	
	public void start(){
		quartzTaskMaster.start();
	}
	
	public void stop(){
		quartzTaskMaster.stop();
	}
}
