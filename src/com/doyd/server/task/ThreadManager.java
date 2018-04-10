package com.doyd.server.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadManager {
	private int threadCnt = 50;
	private ThreadPoolExecutor exe = null;
	public ThreadManager(){
		exe = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCnt);
	}
	
	public ThreadManager(int threadCnt){
		this.threadCnt = threadCnt;
		exe = (ThreadPoolExecutor) Executors.newFixedThreadPool(this.threadCnt);
	}
	
	public void addTask(Runnable task){
		if(exe==null){
			return;
		}
		exe.execute(task);
		/*
		while(getTaskSize(exe)>threadCnt*4){
			sleep(10);
		}
		*/
	}
	
	public void shutdown(){
		while(getTaskSize(exe)>0){
			sleep(200);
		}
		if(exe!=null){
			exe.shutdownNow();
			exe = null;
		}
	}
	
	public static int getTaskSize(ThreadPoolExecutor exe){
		return exe==null?0:(exe.getActiveCount()+exe.getQueue().size());
	}

	public static void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
}
