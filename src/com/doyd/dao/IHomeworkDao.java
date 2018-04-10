package com.doyd.dao;

import java.util.List;
import java.util.Map;

import com.doyd.core.action.common.Page;
import com.doyd.model.Homework;

public interface IHomeworkDao {
	/**
	 * 获得家庭作业
	 * 
	 * @param workId 作业Id
	 * @return Homework
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-14 下午6:12:28
	 */
	public Homework getHomework(int workId);
	
	/**
	 * 分页查询作业
	 * @Title: queryWorkByPage
	 * @param page
	 * @param groupId
	 * @param type
	 * @param flag 是否为班主任
	 * @param wuId
	 * @param key
	 * @param beginDate
	 * @param endDate
	 * @return List<Map<String,Object>>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 上午11:03:43
	 */
	public List<Map<String, Object>> queryWorkByPage(Page page, int groupId, int type, boolean flag, int wuId, String key, String beginDate, String endDate);
	public List<Map<String, Object>> queryWorkByPage(int page, int groupId, boolean isDirector, int wuId, int key);
	
	/**
	 * 添加作业
	 * @Title: addWork
	 * @param work
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 上午11:49:18
	 */
	public boolean addWork(Homework work);
	
	/**
	 * 删除/批量删除作业
	 * @Title: deleteWork
	 * @param workIds
	 * @param groupId
	 * @param flag
	 * @param wuId
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午2:47:44
	 */
	public boolean deleteWork(int[] workIds, int groupId, boolean flag, int wuId);
	
	/**
	 * 查询作业数量
	 * @Title: getWorkCnt
	 * @param groupId
	 * @param wuId
	 * @param type
	 * @return int
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午3:48:23
	 */
	public int getWorkCnt(int groupId, int wuId, int type);
	
	/**
	 * 
	 * @Title: getHomeworkList
	 * @param flag
	 * @param wuId
	 * @param workIds
	 * @return List<Homework>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午5:05:46
	 */
	public List<Homework> getHomeworkList(boolean flag, int wuId, int[] workIds);
	/**
	 * 添加作业阅读数
	 * 
	 * @param workId 作业Id
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-7 上午9:29:33
	 */
	public boolean readHomework(int workId);
	/**
	 * 获得作业和班务通知
	 * 
	 * @param groupId 群Id
	 * @param wuId 用户Id
	 * @param page 页码信息
	 * @return List<Map<String,Object>>
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-7 下午1:47:50
	 */
	public List<Map<String, Object>> getHomework_notify(int groupId, int wuId, int page);
}
