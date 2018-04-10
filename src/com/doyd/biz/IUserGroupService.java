package com.doyd.biz;

import com.doyd.model.UserGroup;
import com.doyd.msg.ApiMessage;

public interface IUserGroupService {
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
	 * 获得群成员列表
	 * 
	 * @param openGId 微信群Id
	 * @param key 搜索条件
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-18 上午10:40:39
	 */
	public ApiMessage getGroupUser(String openGId, String openId, String key);
	/**
	 * 获得用户身份信息
	 * 
	 * @param openId 用户openId
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-20 上午9:48:24
	 */
	public ApiMessage identity(String openId, String openGId);
	/**
	 * 添加身份
	 * 
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @param identities 身份数组
	 * @param childName 孩子名称
	 * @param education 学历
	 * @param grade 年级
	 * @param realName 真实姓名
	 * @param relation 和孩子关系
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-20 上午11:47:37
	 */
	public ApiMessage addIdentity(String baseUrl, String openId, String openGId, String groupName, String[] identities
			, String childName, int education, int grade, String realName, String relation);
	/**
	 * 获得用户首页信息
	 * 
	 * @param openId 用户openId
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-20 下午7:23:47
	 */
	public ApiMessage home(String openId, String openGId);
	/**
	 * 添加群关系
	 * 
	 * @param openId 用户openId
	 * @param openGIds 用户群数组
	 * @param identity 身份
	 * @param childId 孩子信息
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-25 上午9:43:20
	 */
	public ApiMessage addUserGroup(String baseUrl, String openId, String openGId, String groupName, String identity, int childId);
	/**
	 * 批量添加群名称
	 * 
	 * @param openId 用户群Id
	 * @param openGIds 微信群Id数组
	 * @param childId 孩子Id
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-11 上午9:54:11
	 */
	public ApiMessage batchAddUserGroup(String baseUrl, String openId, String identity, String[] openGIds, int childId);
	/**
	 * 解绑群关系
	 * 
	 * @param openId 用户openId
	 * @param openGId 绑定群Id
	 * @param identity 身份
	 * @param childId 孩子信息
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-25 下午4:31:10
	 */
	public ApiMessage unbindUserGroup(String openId, String openGId, String identity, int childId);
	
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
	 * 获得任教科目
	 * 
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-27 下午5:38:00
	 */
	public ApiMessage getSubjects(String openId, String openGId);
	/**
	 * 修改任教科目
	 * 
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @param subjects 任教科目
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-27 下午4:36:13
	 */
	public ApiMessage updateSubjects(String openId, String openGId, String[] subjects);
	/**
	 * 修改成员电话
	 * 
	 * @param openId 当前用户openId
	 * @param openGId 微信群Id
	 * @param phone 电话
	 * @param pOpenId 被修改者的OpenId
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-2 上午9:54:28
	 */
	public ApiMessage updatePhone(String openId, String openGId, String phone, String pOpenId);
	/**
	 * 删除群成员
	 * 
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @param teacher 老师的openId
	 * @param patriarch 家长的openId
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-2 下午3:24:01
	 */
	public ApiMessage deleteUg(String openId, String openGId, String[] teacher, String[] patriarch);
	/**
	 * 发起完善通知
	 * 
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-3 下午4:05:43
	 */
	public ApiMessage addPerfect(String openId, String openGId);
	/**
	 * 获得用户群信息
	 * 
	 * @param openId
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-7 下午3:17:55
	 */
	public ApiMessage getUserGroup(String openId, String identity, int childId);
	/**
	 * 修改群为最新使用
	 * 
	 * @param openId
	 * @param openGId void
	 * @author 创建人：ylb
	 * @date 创建时间：2018-3-14 上午10:02:06
	 */
	public void newUse(String openId, String openGId);
}
