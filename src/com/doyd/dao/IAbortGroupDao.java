package com.doyd.dao;

import java.util.List;

import com.doyd.model.AbortGroup;

public interface IAbortGroupDao {
	/**
	 * 批量添加退群记录
	 * 
	 * @param agList 退群记录
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-3 下午2:59:19
	 */
	public boolean addAbortGroup(List<AbortGroup> agList);
	public boolean addAbortGroup(AbortGroup ag);
	/**
	 * 获得退群信息
	 * 
	 * @param groupId 群Id
	 * @param wuId 用户Id
	 * @return AbortGroup
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-11 下午2:41:21
	 */
	public AbortGroup getAbortGroup(int groupId, int wuId);
}
