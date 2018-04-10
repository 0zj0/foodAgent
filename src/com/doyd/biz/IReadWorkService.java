package com.doyd.biz;

import com.doyd.msg.ApiMessage;

public interface IReadWorkService {
	/**
	 * 获得阅读记录
	 * 
	 * @param openId 用户openId
	 * @param workId 作业Id
	 * @param key 1、已读 2、未读
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-5 下午9:00:53
	 */
	public ApiMessage getReadHomework(String openId, int workId, int key);
}
