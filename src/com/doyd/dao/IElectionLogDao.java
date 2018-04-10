package com.doyd.dao;

import com.doyd.model.ElectionLog;


public interface IElectionLogDao {
	/**
	 * 判断用户是否投票
	 * 
	 * @param electionId 选举Id
	 * @param wuId 用户Id
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-14 下午5:05:47
	 */
	public boolean exist(int electionId, int wuId);
	/**
	 * 获得投票记录
	 * 
	 * @param electionId
	 * @param wuId
	 * @return ElectionLog
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-17 上午10:19:44
	 */
	public ElectionLog getElectionLog(int electionId, int wuId);
	/**
	 * 添加选举记录
	 * 
	 * @param log
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 下午3:20:27
	 */
	public boolean addLog(ElectionLog log);
}
