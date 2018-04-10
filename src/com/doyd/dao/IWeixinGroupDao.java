package com.doyd.dao;

import java.util.List;
import java.util.Map;

import com.doyd.core.action.common.Page;
import com.doyd.model.WeixinGroup;

public interface IWeixinGroupDao {
	/**
	 * 获得微信群信息
	 * 
	 * @param openGId 微信群Id
	 * @return WeixinGroup
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-14 下午2:21:48
	 */
	public WeixinGroup getWeixinGroup(String openGId);
	/**
	 * 获得微信群信息
	 * 
	 * @param groupId 群Id
	 * @return WeixinGroup
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-14 下午4:18:06
	 */
	public WeixinGroup getWeixinGroup(int groupId);
	
	/**
	 * 查询群主拥有的所有群列表
	 * @Title: getWeixinGroupList
	 * @param wuId
	 * @return List<WeixinGroup>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-14 下午3:47:45
	 */
	public List<Map<String, Object>> getWeixinGroupList(int wuId);
	
	/**
	 * 分页查询群主拥有的所有群列表信息，包括一些统计信息
	 * @Title: getWeixinGroupByPage
	 * @param page
	 * @param wuId
	 * @return List<Map<String,Object>>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-15 上午10:21:49
	 */
	public List<Map<String, Object>> getWeixinGroupByPage(Page page, int wuId);
	/**
	 * 添加微信群
	 * 
	 * @param wg 微信群信息
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-20 下午5:48:47
	 */
	public boolean addWeixinGroup(WeixinGroup wg);
	/**
	 * 修改群班主任Id
	 * 
	 * @param groupId 群Id
	 * @param wuId 用户Id
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-20 下午6:21:24
	 */
	public boolean updateDirectorId(int groupId, int wuId);
	/**
	 * 解绑用户群班主任
	 * 
	 * @param groupId 群Id
	 * @param wuId 用户Id
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-25 下午6:09:36
	 */
	public boolean unbindDirector(int groupId, int wuId);
	
	/**
	 * 获得用户群信息
	 * 
	 * @param wuId
	 * @return List<Map<String,Object>>
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-7 下午3:23:42
	 */
	public List<Map<String, Object>> getUserGroupList(int wuId, int type, int childId);
	/**
	 * 修改群人数
	 * 
	 * @param openGIdList 微信群Id集合
	 * @param type  1、添加   2、减去
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-11 下午1:34:42
	 */
	public boolean updatePeopleCnt(String openGId, int type, int cnt);
	public boolean updatePeopleCnt(List<String> openGIdList, int type);
	/**
	 * 修改群信息
	 * 
	 * @param wg
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-11 下午8:33:07
	 */
	public boolean update(WeixinGroup wg);
	
	/**
	 * 增加字段数量
	 * @Title: upCnt
	 * @param groupId
	 * @param field
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2018-1-11 上午11:53:03
	 */
	public boolean upCnt(int groupId, String field);
	
	/**
	 * 减少字段数量
	 * @Title: downCnt
	 * @param groupId
	 * @param field
	 * @return Boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2018-1-11 上午11:53:50
	 */
	public Boolean downCnt(int groupId, String field);
	
	/**
	 * 减少字段数量
	 * @Title: downCnt
	 * @param groupId
	 * @param field
	 * @param cnt
	 * @return Boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2018-1-11 上午11:54:25
	 */
	public Boolean downCnt(int groupId, String field, int cnt);
	/**
	 * 修改群名称
	 * 
	 * @param groupId 群Id
	 * @param groupName 群名称
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-12 下午3:48:44
	 */
	public boolean updateGroupName(int groupId, String groupName);
}
