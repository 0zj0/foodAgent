package com.doyd.dao;

import java.util.List;
import java.util.Map;

import com.doyd.model.UserGroup;

public interface IUserGroupDao {
	/**
	 * 获得用户群信息
	 * 
	 * @param wuId 用户Id
	 * @param groupId 群Id
	 * @return UserGroup
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-14 下午2:13:46
	 */
	public UserGroup getUserGroup(int wuId, int groupId);
	/**
	 * 判断用户是否在群中
	 * 
	 * @param wuId 用户Id
	 * @param groupId 群Id
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-19 下午4:55:39
	 */
	public boolean userInGroup(int wuId, int groupId);
	/**
	 * 判断用户是否在群中
	 * 
	 * @param openId 用户openId
	 * @param groupId 群Id
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-19 下午4:55:39
	 */
	public boolean userInGroup(String openId, int groupId);
	/**
	 * 根据群Id获得群成员
	 * 
	 * @param groupId 群Id
	 * @return List<Map<String, Object>>
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-18 上午10:49:23
	 */
	public List<Map<String, Object>> getUserGroupList(int groupId, String key);
	/**
	 * 添加用户群关系
	 * 
	 * @param ug
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-20 下午7:12:27
	 */
	public boolean addUserGroup(UserGroup ug);
	/**
	 * 批量添加用户群关系
	 * 
	 * @param ugList
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-11 上午10:55:24
	 */
	public boolean batchAddUserGroup(List<UserGroup> ugList);
	/**
	 * 批量修改用户群关系
	 * 
	 * @param ugList
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-11 下午2:04:02
	 */
	public boolean batchUpdateUserGroup(List<UserGroup> ugList);
	/**
	 * 获得用户的群列表
	 * 
	 * @param wuId 用户Id
	 * @return List<Map<String, Object>>
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-21 上午10:19:27
	 */
	public List<Map<String, Object>> getUserGroupListByWuId(int wuId, String openGId);
	/**
	 * 修改用户群关系
	 * 
	 * @param ug
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-21 下午6:58:56
	 */
	public boolean update(UserGroup ug);
	/**
	 * 修改用户群的身份
	 * 
	 * @param ug
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-12 下午5:24:38
	 */
	public boolean updateIdentity(UserGroup ug, boolean director, boolean teacher, boolean patriarch);
	/**
	 * 判断是否存在用户关系
	 * 
	 * @param wuId 用户Id
	 * @param groupId 群Id
	 * @param childId 孩子Id
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-25 下午2:23:36
	 */
	public boolean existUserGroup(int wuId, int groupId, int childId);
	
	/**
	 * 获取用户群关系
	 * @Title: getUserGroup
	 * @param ugId
	 * @return UserGroup
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-20 下午5:57:00
	 */
	public UserGroup getUserGroup(int ugId);
	
	/**
	 * 剔除群成员
	 * @Title: deleteUser
	 * @param userGroup
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-21 下午7:41:05
	 */
	public boolean deleteUser(UserGroup userGroup);
	
	/**
	 * 批量删除群成员
	 * @Title: batchDeleteUser
	 * @param ugIds
	 * @param groupId
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-25 上午9:49:24
	 */
	public boolean batchDeleteUser(int[] ugIds, int groupId);
	
	/**
	 * 修改群成员
	 * @Title: updateUser
	 * @param userGroup
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-25 上午11:22:34
	 */
	public boolean updateUser(UserGroup userGroup);
	
	/**
	 * 获得群成员数量
	 * @Title: getUserCnt
	 * @param groupId
	 * @param type
	 * @return int
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-25 下午3:17:18
	 */
	public int getUserCnt(int groupId, int type);
	
	/**
	 * 查询所有家长列表
	 * @Title: getAllUserList
	 * @param groupId
	 * @return List<Map<String,Object>>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午4:54:23
	 */
	public List<Map<String, Object>> getAllUserList(int groupId);
	
	
	/**
	 * 获得用户在群内，的关系个数
	 * 
	 * @param wuId 用户Id
	 * @param groupId 群Id
	 * @return int
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-25 下午5:53:08
	 */
	public int getUserGroupCnt(int wuId, int groupId);
	/**
	 * 解绑群关系
	 * 
	 * @param wuId 用户Id
	 * @param groupId 群Id
	 * @param type 解绑类型  1、班主任  2、老师   3、家长
	 * @param childId 孩子Id
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-25 下午5:46:03
	 */
	public boolean unbindUserGroup(int wuId, int groupId, int type, int childId);
	/**
	 * 删除群关系----存在多个孩子群关系，并解绑孩子群关系时
	 * 
	 * @param wuId 用户Id
	 * @param groupId 群Id
	 * @param childId 孩子Id
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-25 下午7:18:01
	 */
	public boolean deleteUserGroup(int wuId, int groupId, int childId);
	/**
	 * 添加身份，老师身份，班主任身份
	 * 
	 * @param wuId 用户Id
	 * @param groupId 群Id
	 * @param director 班主任
	 * @param teacher 老师
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 下午3:57:22
	 */
	public boolean addIdentity(int wuId, int groupId, boolean director, boolean teacher);
	/**
	 * 修改任教科目
	 * 
	 * @param wuId 用户Id
	 * @param groupId 群Id
	 * @param subject 任教科目
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-27 下午4:47:20
	 */
	public boolean updateSubject(int wuId, int groupId, String subject);
	/**
	 * 判断openId数组中存在群内的个数
	 * 
	 * @param groupId 群Id
	 * @param type 类型 1老师    2、家长
	 * @param openIds openId数组
	 * @return int
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-2 下午3:37:53
	 */
	public List<UserGroup> getUserGroupList(int groupId, int type, String[] openIds);
	/**
	 * 删除用户群关系
	 * 
	 * @param groupId 群Id
	 * @param wuIdList 用户Id集合
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-3 上午11:11:10
	 */
	public boolean deleteUserGroup(int groupId, List<Integer> wuIdList);
	/**
	 * 修改群关系（解除某身份）
	 * 
	 * @param groupId 群Id
	 * @param wuIdList 用户Id集合
	 * @param type 1、老师  2、家长
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-3 上午11:16:30
	 */
	public boolean updateUserGroup(int groupId, List<Integer> wuIdList, int type);
	/**
	 * 删除用户群Id
	 * 
	 * @param ugIdList 用户群关系Id集合
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-3 上午11:32:10
	 */
	public boolean deleteUserGroup(int groupId, Object[] ugIdList);
	/**
	 * 根据群Id获得用户群关系
	 * 
	 * @param groupId 群Id
	 * @return List<UserGroup>
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-3 下午4:47:19
	 */
	public List<UserGroup> getUserGroupList(int groupId);
	
	/**
	 * 增加字段数量
	 * @Title: upCnt
	 * @param groupId
	 * @param field
	 * @param type 1、所有 2、班主任和老师 4、家长  
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2018-1-11 上午11:53:03
	 */
	public boolean upCnt(int groupId, String field, int type);
	
	/**
	 * （批量）删除时减少字段数量
	 * @Title: downCnt
	 * @param groupId
	 * @param field 要修改的字段
	 * @param readTable 阅读记录表名
	 * @param joinField 条件字段名
	 * @param id 条件字段值
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2018-3-12 下午9:35:34
	 */
	public boolean downCnt(int groupId, String field, String readTable, String joinField, int id);
	
	/**
	 * 阅读时减少字段数量
	 * @Title: downCnt
	 * @param wuId
	 * @param groupId
	 * @param field
	 * @return Boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2018-1-11 上午11:53:50
	 */
	public Boolean downCnt(int wuId, int groupId, String field);
	
	/**
	 * 减少字段数量
	 * @Title: downCnt
	 * @param wuId
	 * @param groupId
	 * @param field
	 * @param cnt
	 * @return Boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2018-1-11 上午11:54:25
	 */
	public Boolean downCnt(int wuId, int groupId, String field, int cnt);
	
	/**
	 * 清空字段数量
	 * @Title: clearCnt
	 * @param wuId
	 * @param groupId
	 * @param field
	 * @return Boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2018-3-7 下午5:45:09
	 */
	public Boolean clearCnt(int wuId, int groupId, String field);
	
	/**
	 * 查询群成员
	 * @Title: getUserGroupList
	 * @param ugIds
	 * @param groupId
	 * @return List<UserGroup>
	 * @author 创建人：王伟
	 * @date 创建时间：2018-1-15 下午2:43:34
	 */
	public List<UserGroup> getUserGroupList(int[] ugIds, int groupId);
	/**
	 * 修改电话为空的关联的电话
	 * 
	 * @param wuId 用户Id
	 * @param phone 电话
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-3-10 上午10:59:12
	 */
	public boolean updatePhone(int wuId, String phone);
	/**
	 * 修改表newUse字段，全部改为0
	 * 
	 * @param openId
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-3-14 上午11:17:33
	 */
	public boolean updateNewUse(String openId);
	/**
	 * 修改表newUse字段，指定的改为1
	 * 
	 * @param openId
	 * @param openGId
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-3-14 上午11:23:58
	 */
	public boolean updateNewUse(String openId, String openGId);
}
