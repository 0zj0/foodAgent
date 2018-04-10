package com.doyd.biz;

import com.doyd.msg.ApiMessage;

public interface IElectionLordService {
	/**
	 * 发起纠正页----获得班主任信息
	 * 
	 * @param openId 用户openId
	 * @param openGId 群Id
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 上午9:38:40
	 */
	public ApiMessage election(String openId, String openGId);
	/**
	 * 添加选举信息
	 * 
	 * @param openId 用户openId
	 * @param openGId 群Id
	 * @param electionType 选举类型correct、vote
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 上午10:05:50
	 */
	public ApiMessage addElection(String openId, String openGId, String electionType);
	/**
	 * 获得选举信息
	 * 
	 * @param openId 用户openId
	 * @param electionId 选举信息Id
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 上午10:51:47
	 */
	public ApiMessage showElection(String openId, int electionId);
}
