package com.doyd.dao;

import java.util.List;
import java.util.Map;

import com.doyd.model.GroupMessage;

public interface IGroupMessageDao {
	/**
	 * 获得用户的消息
	 * 
	 * @param wuId 用户Id
	 * @return List<Map<String, Object>
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-21 上午11:29:57
	 */
	public List<Map<String, Object>> getGroupMessageList(int wuId);
	public List<Map<String, Object>> getGroupMessageList(int wuId, int groupId);
	/**
	 * 修改推送状态
	 * 
	 * @param wuId
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-25 下午4:41:18
	 */
	public boolean push(int wuId);
	/**
	 * 获得消息
	 * 
	 * @param msgId 消息Id
	 * @return GroupMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-27 下午8:29:27
	 */
	public GroupMessage getGroupMessage(int msgId);
	/**
	 * 修改消息（阅读）
	 * 
	 * @param msgId 消息Id
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-27 下午8:47:18
	 */
	public boolean readMsg(int msgId);
	/**
	 * 添加用户消息
	 * 
	 * @param gmList
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-3 下午3:47:25
	 */
	public boolean addGroupMessage(List<GroupMessage> gmList);
	/**
	 * 添加用户消息
	 * 
	 * @param gm
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-11 下午2:47:18
	 */
	public boolean addGroupMessage(GroupMessage gm);
}
