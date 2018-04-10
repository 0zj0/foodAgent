package com.doyd.biz;

import com.doyd.model.UserPcLogin;

public interface IUserPcLoginService {
	
	/**
	 * 添加登陆记录
	 * @Title: addRecord
	 * @param record
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-15 下午5:03:18
	 */
	public boolean addRecord(UserPcLogin record);
	
	/**
	 * 更新心跳时间
	 * @Title: updateHeartTime
	 * @param record
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-15 下午5:05:15
	 */
	public boolean updateHeartTime(UserPcLogin record);
	
	/**
	 * 登出
	 * @Title: logout
	 * @param record
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-15 下午5:05:26
	 */
	public boolean logout(UserPcLogin record);
	
	/**
	 * 获取用户最新的登陆记录
	 * @Title: getLatestRecord
	 * @param wuId
	 * @return UserPcLogin
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-15 下午7:52:37
	 */
	public UserPcLogin getLatestRecord(int wuId);
	
}
