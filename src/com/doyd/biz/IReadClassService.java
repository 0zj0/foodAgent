package com.doyd.biz;

import com.doyd.msg.ApiMessage;

public interface IReadClassService {
	/**
	 * 获得阅读记录
	 * 
	 * @param openId 用户openId
	 * @param notifyId 通知Id
	 * @param key 1、已读 2、未读
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-5 下午9:00:53
	 */
	public ApiMessage getReadClassnotify(String openId, int notifyId, int key);
}
