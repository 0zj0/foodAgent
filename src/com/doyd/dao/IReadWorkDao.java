package com.doyd.dao;

import java.util.List;
import java.util.Map;

import com.doyd.model.ReadWork;

public interface IReadWorkDao {
	
	/**
	 * 查询已读家长列表
	 * @Title: getReadUserList
	 * @param groupId
	 * @param workId
	 * @return List<Map<String,Object>>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午4:52:41
	 */
	public List<Map<String, Object>> getReadUserList(int groupId, int workId);
	/**
	 * 获得用户是否阅读过作业
	 * 
	 * @param workId 作业Id
	 * @param wuId 用户Id
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-6 上午10:13:01
	 */
	public boolean isRead(int workId, int wuId);
	/**
	 * 添加作业阅读记录
	 * 
	 * @param rw
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-6 上午10:15:38
	 */
	public boolean addReadWork(ReadWork rw);
	
}
