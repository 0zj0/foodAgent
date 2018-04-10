package com.doyd.biz;

import com.doyd.msg.ApiMessage;

public interface IElectionLogService {
	/**
	 * 添加选举记录
	 * 
	 * @param openId 用户openId
	 * @param electionId 选举信息
	 * @param result
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 下午1:52:10
	 */
	public ApiMessage addElectionLog(String openId, int electionId, int result);
}
