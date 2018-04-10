package com.doyd.dao;

import java.util.List;

import com.doyd.model.Children;

public interface IChildrenDao {
	/**
	 * 判断用户下是否存在这个孩子
	 * 
	 * @param wuId 用户Id
	 * @param childName 孩子姓名
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-20 下午3:55:48
	 */
	public boolean existChildren(int wuId, String childName);
	/**
	 * 添加孩子
	 * 
	 * @param child
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-20 下午4:41:54
	 */
	public boolean addChildren(Children child);
	/**
	 * 获得用户下的孩子列表
	 * 
	 * @param wuId 用户Id
	 * @return List<Children>
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-20 下午5:00:49
	 */
	public List<Children> getChildrenList(int wuId);
	/**
	 * 获得孩子信息
	 * 
	 * @param childId 孩子Id
	 * @return Children
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-21 下午7:27:21
	 */
	public Children getChildren(int childId);
	/**
	 * 修改孩子名称
	 * 
	 * @param childId 孩子Id
	 * @param childName 孩子名称
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-21 下午7:31:19
	 */
	public boolean updateChildName(int childId, String childName, String relation);
}
