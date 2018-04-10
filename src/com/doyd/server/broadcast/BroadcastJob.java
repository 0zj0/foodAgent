package com.doyd.server.broadcast;

import java.util.List;

import com.doyd.server.task.Task;

/**
 * 广播任务
 * @author zdh
 *
 * @param <T>
 */
public class BroadcastJob<T> extends Task{
	@SuppressWarnings("rawtypes")
	private List<IConsumer> list;
	private T t;
	public BroadcastJob(){
		this(null, null);
	}
	
	@SuppressWarnings("rawtypes")
	public BroadcastJob(List<IConsumer> list, T t) {
		this.list = list;
		this.t = t;
	}

	@Override
	public void run() {
		if(list!=null){
			for(IConsumer<T> c: list){
				c.run(t);
			}
		}
	}

}
