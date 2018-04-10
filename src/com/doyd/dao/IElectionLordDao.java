package com.doyd.dao;

import java.util.List;

import com.doyd.model.ElectionLord;

public interface IElectionLordDao {
	/**
	 * 获得选举信息
	 * 
	 * @param electionId 选举Id
	 * @return ElectionLord
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-14 下午3:51:40
	 */
	public ElectionLord getElectionLord(int electionId);
	/**
	 * 根据openId和openGId获得纠正信息
	 * 
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @param type 1、纠正  2、投票
	 * @return ElectionLord
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-18 下午1:39:28
	 */
	public ElectionLord getElectionLord(String openId, String openGId, int type);
	/**
	 * 获得用户再群中的纠正信息
	 * 
	 * @param wuId 当前用户Id
	 * @param groupId 群Id
	 * @return List<ElectionLord>
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-25 上午11:14:04
	 */
	public List<ElectionLord> getElectionLordList(int wuId, int groupId);
	/**
	 * 添加选举信息
	 * 
	 * @param el
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 上午10:15:21
	 */
	public boolean addElectionLord(ElectionLord el);
	/**
	 * 修改参与人数和同意人数
	 * 
	 * @param electionId 选举Id
	 * @param result 投票结果
	 * @param 选举结果   0：未有结果  2：成功，3：失败
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-11 下午5:46:14
	 */
	public boolean update(int electionId, int result, int state);
}
