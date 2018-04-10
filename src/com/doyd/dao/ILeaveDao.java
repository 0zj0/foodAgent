package com.doyd.dao;

import java.util.List;
import java.util.Map;

import com.doyd.core.action.common.Page;
import com.doyd.model.Leave;

public interface ILeaveDao {
	/**
	 * 获得请假审批
	 * 
	 * @param wuId 用户Id
	 * @param groupId 群Id
	 * @param childId 孩子Id，可以为0
	 * @param type 1、班主任 2、老师  3、家长
	 * @param state 审核状态0，全部 1，未审核，2：审核中（已经查看记录），3：审核成功，4：审核失败
	 * @param page
	 * @return List<Map<String,Object>>
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-28 上午9:37:25
	 */
	public List<Map<String, Object>> getLeaveList(int wuId, int groupId, int childId, int type, int state, int page);
	/**
	 * 添加请假审批
	 * 
	 * @param leave
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-29 上午9:29:55
	 */
	public boolean addLeave(Leave leave);
	/**
	 * 获得请假审批
	 * @Title: getLeaveById
	 * @param leaveId
	 * @return Leave
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午3:36:21
	 */
	public Leave getLeaveById(int leaveId);
	
	/**
	 * 分页查询请假审批
	 * @Title: queryLeaveByPage
	 * @param page
	 * @param groupId
	 * @param type
	 * @param key
	 * @return List<Map<String,Object>>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 上午11:03:43
	 */
	public List<Map<String, Object>> queryLeaveByPage(Page page, int groupId, int type, String key);
	
	/**
	 * 审核请假审批
	 * @Title: updateLeave
	 * @param leave
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 上午11:05:01
	 */
	public boolean updateLeave(Leave leave);
	
	/**
	 * 查询请假审批数量
	 * @Title: getLeaveCnt
	 * @param groupId
	 * @param type
	 * @return int
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午3:48:23
	 */
	public int getLeaveCnt(int groupId, int type);
	/**
	 * 阅读请假审批
	 * 
	 * @param leaveId 请假审批Id
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-7 下午4:44:38
	 */
	public boolean readLeave(int leaveId);
	
}
