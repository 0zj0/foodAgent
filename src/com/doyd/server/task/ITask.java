package com.doyd.server.task;

public interface ITask extends Runnable {
	
	public boolean isSingleton(); 
	
	public void run();
	
}
