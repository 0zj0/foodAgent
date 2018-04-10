package com.doyd.dao;

import com.doyd.model.WxUserOauth;

public interface IWxUserOauthDao {
	/**
	 * 获得信息
	 * 
	 * @param appId
	 * @param openId
	 * @return WxUserOauth
	 * @author 创建人：ylb
	 * @date 创建时间：2018-3-13 上午10:45:58
	 */
	public WxUserOauth getWxUserOauth(String appId, String openId);
	/**
	 * 添加信息
	 * 
	 * @param wuo
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-3-13 上午10:55:55
	 */
	public boolean addWxUserOauth(WxUserOauth wuo);
	/**
	 * 修改关联信息的unionId
	 * 
	 * @param wuoId
	 * @param unionId
	 * @return booelan
	 * @author 创建人：ylb
	 * @date 创建时间：2018-3-13 下午2:58:45
	 */
	public boolean updateWxUserOauthForUnionId(int wuoId, String unionId);
}
