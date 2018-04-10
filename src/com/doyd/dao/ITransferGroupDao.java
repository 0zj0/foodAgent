package com.doyd.dao;

import com.doyd.model.TransferGroup;

public interface ITransferGroupDao {
	/**
	 * 获得转让信息
	 * 
	 * @param transferId 转让Id
	 * @return TransferGroup
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-15 上午10:21:15
	 */
	public TransferGroup getTransferGroup(int transferId);
	/**
	 * 获得转让信息
	 * 
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @return TransferGroup
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-18 下午3:08:05
	 */
	public TransferGroup getTransferGroup(String openId, String openGId);
	/**
	 * 添加转让信息
	 * 
	 * @param tg
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 下午7:35:27
	 */
	public boolean addTransferGroup(TransferGroup tg);
}
