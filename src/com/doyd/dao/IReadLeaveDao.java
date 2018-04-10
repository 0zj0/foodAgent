package com.doyd.dao;

import com.doyd.model.ReadLeave;

/**
 * @author ylb
 * @version 创建时间：2018-3-7 下午7:20:45
 */
public interface IReadLeaveDao {
	/**
	 * 判断用户是否阅读过这条记录
	 * 
	 * @param leaveId 请假审批Id
	 * @param wuId 用户Id
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-3-7 下午7:29:04
	 */
	public boolean isRead(int leaveId, int wuId);
	/**
	 * 添加阅读记录
	 * 
	 * @param rl
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-3-7 下午7:29:51
	 */
	public boolean addReadLeave(ReadLeave rl);
}
