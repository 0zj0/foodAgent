package com.doyd.biz;

import com.doyd.core.action.common.Page;
import com.doyd.model.WeixinGroup;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;

public interface IWeixinGroupService {
	/**
	 * 获得微信群信息
	 * 
	 * @param openGId 微信群Id
	 * @return WeixinGroup
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-14 下午2:21:48
	 */
	public WeixinGroup getWeixinGroup(String openGId);
	/**
	 * 根据用户信息查询微信群列表
	 * @Title: queryWeixinGroup
	 * @param weixinUser
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-14 下午3:43:08
	 */
	public ApiMessage queryWeixinGroup(WeixinUser weixinUser, ReqCode reqCode);
	
	/**
	 * 分页查询与用户有关的群信息
	 * @Title: queryWeixinGroupByPage
	 * @param page
	 * @param weixinUser
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-15 上午11:29:03
	 */
	public ApiMessage queryWeixinGroupByPage(Page page, WeixinUser weixinUser, ReqCode reqCode);
	/**
	 * 获得openGId
	 * 
	 * @param openId 用户openId
	 * @param code 微信用户登录凭证（有效期五分钟）
	 * @param gEncryptedData 微信群encryptedData
	 * @param gIv 微信群iv
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-4 上午9:54:10
	 */
	public ApiMessage getOpenGId(String appId, String openId, String code, String gEncryptedData, String gIv);
	/**
	 * 添加群图片
	 * 
	 * @param openGId 微信群Id
	 * @return String
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-7 下午7:56:47
	 */
	public String addGroupPic(String openGId, String baseUrl);
	/**
	 * 修改群名称
	 * 
	 * @param openId
	 * @param openGId
	 * @param groupName
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-12 下午3:21:19
	 */
	public ApiMessage updateGroupName(String openId, String openGId, String groupName);
}
