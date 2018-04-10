package com.doyd.server.task.quartz;

import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

import com.doyd.server.task.ITaskServer;
import com.doyd.server.task.Task;

@Component
public class QuartzTaskServer implements ITaskServer {
	public static void main(String[] args) {
		QuartzTaskServer server = new QuartzTaskServer();
		//master.addTask(SmsJob.class, "simpleTrigger1", 2);
		server.start();
		//master.pauseAll();
		//master.resumeAll();
		//master.stop();
	}
	
	private Scheduler getScheduler() throws SchedulerException{
		return StdSchedulerFactory.getDefaultScheduler();
	}

	@Override
	public Task getTask(String name) {
		try{
			Scheduler scheduler = getScheduler();
			JobDetail detail = scheduler.getJobDetail(getJobKey(name));
			return null;
		}catch (Exception e) {
			
		}
		return null;
	}

	@Override
	public List<Task> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(String name) {
		try{
			Scheduler scheduler = getScheduler();
			return scheduler.checkExists(getJobKey(name));
		}catch (Exception e) {
			
		}
		return false;
	}

	public void addTask(Class<? extends Task> clazz, String name, int seconds) {
		try {
			Class<? extends Job> cls = (Class<? extends Task>) clazz;
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			JobDataMap dataMap = new JobDataMap();
			//JobBuilder.newJob().
			JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(getJobKey(name)).build();
			long ctime = System.currentTimeMillis();
			Trigger trigger = TriggerBuilder
					.newTrigger()
					.withIdentity(name)
					.startAt(new Date(ctime))
					.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(seconds).repeatForever())
					.build();
			// 设置作业启动时间,马上启动
			// 作业和触发器注册到调度器中,并建立Trigger和JobDetail的关联
			scheduler.scheduleJob(jobDetail, trigger);
			// 启动调度器
			// scheduler.start();
			// scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		try {
			Scheduler scheduler = getScheduler();
			if(!scheduler.isStarted()){
				scheduler.start();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void stop() {
		try {
			Scheduler scheduler = getScheduler();
			if(!scheduler.isShutdown()){
				scheduler.shutdown();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void pauseAll() {
		try {
			Scheduler scheduler = getScheduler();
			scheduler.pauseAll();
		} catch (Exception e) {
		}
	}

	@Override
	public void pauseTask(String name) {
		try {
			Scheduler scheduler = getScheduler();
			scheduler.pauseJob(getJobKey(name));
		} catch (Exception e) {
		}
	}

	@Override
	public void resumeAll() {
		try {
			Scheduler scheduler = getScheduler();
			scheduler.resumeAll();
		} catch (Exception e) {
		}
	}

	@Override
	public void resumeTask(String name) {
		try {
			Scheduler scheduler = getScheduler();
			scheduler.resumeJob(getJobKey(name));
		} catch (Exception e) {
		}
	}
	
	private JobKey getJobKey(String name){
		return new JobKey(name + "_jobdetail");
	}
}
