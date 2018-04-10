package com.doyd.dao;

import java.util.List;
import java.util.Map;

import com.doyd.core.action.common.Page;
import com.doyd.model.Achievement;

public interface IAchievementDao {
	
	/**
	 * 获得成绩单
	 * @Title: getAchievementById
	 * @param scoreId
	 * @return Achievement
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午3:36:21
	 */
	public Achievement getAchievementById(int scoreId);
	
	/**
	 * 分页查询成绩单
	 * @Title: queryAchievementByPage
	 * @param page
	 * @param groupId
	 * @param wuId
	 * @param key
	 * @param type
	 * @return List<Map<String,Object>>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 上午11:03:43
	 */
	public List<Map<String, Object>> queryAchievementByPage(Page page, int groupId, int wuId, String key, int type);
	/**
	 * 分页查询成绩单
	 * 
	 * @param page 页码信息
	 * @param groupId 群Id
	 * @param key
	 * @return List<Map<String,Object>>
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-3 下午5:18:12
	 */
	public List<Map<String, Object>> queryAchievementByPage(int page, int groupId, String key);
	
	/**
	 * 添加成绩单
	 * @Title: addAchievement
	 * @param achievement
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 上午11:49:18
	 */
	public boolean addAchievement(Achievement achievement);
	
	/**
	 * 删除/批量删除成绩单
	 * @Title: deleteAchievement
	 * @param scoreIds
	 * @param groupId
	 * @param flag
	 * @param wuId
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午2:47:44
	 */
	public boolean deleteAchievement(int[] scoreIds, int groupId, boolean flag, int wuId);
	
	/**
	 * 删除/批量删除成绩单
	 * @Title: deleteAchievement
	 * @param scoreIds
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午4:27:40
	 */
	public boolean deleteAchievement(int[] scoreIds);
	
	
	/**
	 * 修改成绩单（备注）
	 * @Title: updateAchievement
	 * @param achievement
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 上午11:05:01
	 */
	public boolean updateAchievement(Achievement achievement);
	
	/**
	 * 查询成绩单数量
	 * @Title: getAchievementCnt
	 * @param groupId
	 * @param wuId
	 * @param type
	 * @return int
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午3:48:23
	 */
	public int getAchievementCnt(int groupId, int wuId, int type);
	
	/**
	 * 批量查询成绩单
	 * @Title: getAchievementList
	 * @param scoreIds
	 * @return List<Achievement>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午3:47:24
	 */
	public List<Achievement> getAchievementList(int[] scoreIds);
	
	/**
	 * 
	 * @Title: getAchievementList
	 * @param flag
	 * @param wuId
	 * @param scoreIds
	 * @return List<Achievement>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午4:04:03
	 */
	public List<Achievement> getAchievementList(boolean flag, int wuId, int[] scoreIds);
}
