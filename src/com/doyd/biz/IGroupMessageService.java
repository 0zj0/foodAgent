package com.doyd.biz;

import com.doyd.msg.ApiMessage;

public interface IGroupMessageService {
	/**
	 * 阅读消息
	 * 
	 * @param openId 用户openId
	 * @param msgId 消息Id
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-27 下午8:20:13
	 */
	public ApiMessage readMes(String openId, int msgId, int result);
	/**
	 * 获得消息列表
	 * 
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-25 下午4:56:17
	 */
	public ApiMessage getMessage(String openId, String openGId);
}
