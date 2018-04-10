package com.doyd.server.task;

import java.util.List;


public interface ITaskServer {
	public Task getTask(String name);
	
	public List<Task> getAll();
	
	public boolean contains(String name);
	
	public void addTask(Class<? extends Task> clazz, String name, int seconds);
	
	public void start();
	
	public void stop();
	
	public void pauseAll();
	
	public void pauseTask(String name);
	
	public void resumeAll();
	
	public void resumeTask(String name);
}
