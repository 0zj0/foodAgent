package com.doyd.dao;

import java.util.List;
import java.util.Map;

import com.doyd.core.action.common.Page;
import com.doyd.model.ClassNotify;

public interface IClassNotifyDao {
	/**
	 * 获得班务通知
	 * 
	 * @param notifyId 班务通知Id
	 * @return ClassNotify
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-15 上午9:36:43
	 */
	public ClassNotify getClassNotify(int notifyId);
	/**
	 * 获得班务通知
	 * 
	 * @param notifyId 班务通知Id
	 * @return Map<String,Object>
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-29 下午2:24:46
	 */
	public Map<String, Object> getClassNotifyForMap(int notifyId);
	
	/**
	 * 分页查询班务通知
	 * @Title: queryClassNotifyByPage
	 * @param page
	 * @param groupId
	 * @param type
	 * @param wuId
	 * @param key
	 * @param beginDate
	 * @param endDate
	 * @return List<Map<String,Object>>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 上午11:03:43
	 */
	public List<Map<String, Object>> queryClassNotifyByPage(Page page, int groupId, int type, int wuId, String key, String beginDate, String endDate);
	/**
	 * 分页查询班务通知
	 * 
	 * @param page 页码信息
	 * @param groupId 群Id
	 * @param wuId 用户Id
	 * @param key 0，全部，1、我发布的、2，其他人发布的
	 * @return List<Map<String,Object>>
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-6 下午3:52:27
	 */
	public List<Map<String, Object>> queryClassNotifyByPage(int page, int groupId, int wuId, int key);
	
	/**
	 * 添加班务通知
	 * @Title: addClassNotify
	 * @param notify
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 上午11:49:18
	 */
	public boolean addClassNotify(ClassNotify notify);
	
	/**
	 * 删除/批量删除班务通知
	 * @Title: deleteClassNotify
	 * @param notifyIds
	 * @param groupId
	 * @param flag
	 * @param wuId
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午2:47:44
	 */
	public boolean deleteClassNotify(int[] notifyIds, int groupId, boolean flag, int wuId);
	
	/**
	 * 查询班务通知数量
	 * @Title: getClassNotifyCnt
	 * @param groupId
	 * @param wuId
	 * @param type
	 * @return int
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午3:48:23
	 */
	public int getClassNotifyCnt(int groupId, int wuId, int type);
	
	/**
	 * 
	 * @Title: getClassNotifyList
	 * @param flag
	 * @param wuId
	 * @param notifyIds
	 * @return List<ClassNotify>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午5:18:29
	 */
	public List<ClassNotify> getClassNotifyList(boolean flag, int wuId, int[] notifyIds);
	/**
	 * 添加阅读数量
	 * 
	 * @param notifyId 班务通知Id
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-6 下午7:19:28
	 */
	public boolean readNotify(int notifyId);
}
