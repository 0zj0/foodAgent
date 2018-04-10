package com.doyd.server.broadcast;

public interface IConsumer<T> {
	/**
	 * 消息处理接口
	 * @param t 消息
	 */
	public void run(T t);
}
