package com.doyd.biz;

import com.doyd.msg.ApiMessage;

public interface ITransferGroupService {
	/**
	 * 添加转移信息
	 * 
	 * @param openId 当前用户openId
	 * @param openGId 为您群Id
	 * @param acceptOpenId 接受者openId
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 下午6:34:42
	 */
	public ApiMessage addTransfer(String openId, String openGId, String acceptOpenId);
	/**
	 * 获得选举信息
	 * 
	 * @param openId 用户openId
	 * @param transferId 转移Id
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 下午5:36:53
	 */
	public ApiMessage showTransfer(String openId, int transferId);
	/**
	 * 接收转让
	 * 
	 * @param openId 用户openId
	 * @param transferId 转让Id
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 下午8:00:33
	 */
	public ApiMessage accept(String openId, int transferId, String realName);
}
