package com.doyd.dao;

import java.util.List;
import java.util.Map;

import com.doyd.model.ReadClass;

public interface IReadClassDao {
	
	/**
	 * 查询已读家长列表
	 * @Title: getReadUserList
	 * @param groupId
	 * @param notifyId
	 * @return List<Map<String,Object>>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午4:52:41
	 */
	public List<Map<String, Object>> getReadUserList(int groupId, int notifyId);
	/**
	 * 判断用户是否已经阅读
	 * 
	 * @param notifyId 通知Id
	 * @param wuId 用户Id
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-6 下午5:41:56
	 */
	public boolean isRead(int notifyId, int wuId);
	/**
	 * 添加班务阅读记录
	 * 
	 * @param rc
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-6 下午5:42:48
	 */
	public boolean addReadClass(ReadClass rc);
}
