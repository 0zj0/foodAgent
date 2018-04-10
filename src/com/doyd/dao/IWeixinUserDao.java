package com.doyd.dao;

import java.util.List;
import java.util.Map;

import com.doyd.core.action.common.Page;
import com.doyd.model.WeixinUser;

public interface IWeixinUserDao {
	/**
	 * 添加用户信息
	 * 
	 * @param wu
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-13 下午4:34:34
	 */
	public boolean addWeixinUser(WeixinUser wu);
	/**
	 * 授权后修改信息
	 * 
	 * @param wu
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-13 下午4:35:11
	 */
	public boolean updateWeixinUser(WeixinUser wu);
	/**
	 * 登录后修改登录信息
	 * 
	 * @param wuId 用户Id
	 * @param timestamp 时间戳
	 * @param loginDate 登录日期
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-13 下午4:49:40
	 */
	public boolean login(int wuId, long timestamp, int loginDate);
	
	/**
	 * 获得用户信息
	 * @Title: getUser
	 * @param openId
	 * @return WeixinUser
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-14 上午10:09:07
	 */
	public WeixinUser getUser(String openId);
	/**
	 * 根据unionId获得用户信息
	 * 
	 * @param unionId
	 * @return WeixinUser
	 * @author 创建人：ylb
	 * @date 创建时间：2018-3-13 下午2:45:37
	 */
	public WeixinUser getUserByUnionId(String unionId);
	public WeixinUser getUserById(int wuId);
	
	/**
	 * 查询群内成员列表
	 * @Title: getWeixinUserList
	 * @param page
	 * @param groupId
	 * @param type 查询类型；0：所有；1：家长；2：老师； 3：班主任
	 * @param key 搜索条件：可搜索称呼、昵称、手机号
	 * @param wuId
	 * @return List<Map<String, Object>>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-15 下午1:58:14
	 */
	public List<Map<String, Object>> getWeixinUserList(Page page, int groupId, int type, String key, int wuId);
	
	/**
	 * 用户添加身份
	 * 
	 * @param wu 用户信息
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-20 下午5:43:12
	 */
	public boolean addIdentity(WeixinUser wu);
	/**
	 * 修改用户信息
	 * 
	 * @param wuId 用户Id
	 * @param realName 真实姓名
	 * @param phone 电话
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-27 下午4:19:44
	 */
	public boolean updateUser(int wuId, String realName, String phone);
	/**
	 * 根据群Id获得用户列表
	 * 
	 * @param groupId 群Id
	 * @return List<WeixinUser>
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-8 上午11:43:52
	 */
	public List<WeixinUser> getWeixinUserList(int groupId);
}
