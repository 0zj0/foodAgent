package com.doyd.server.task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public abstract class Task implements Job,ITask {
	private JobExecutionContext context = null;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		this.context = context;
		run();
	}
	
	@Override
	public boolean isSingleton() {
		return false;
	}

	public JobExecutionContext getContext() {
		return context;
	}
}
