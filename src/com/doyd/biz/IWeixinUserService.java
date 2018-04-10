package com.doyd.biz;

import com.doyd.core.action.common.Page;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;

public interface IWeixinUserService {
	/**
	 * 小程序入口
	 * 
	 * @param code
	 * @param openGId 微信群Id
	 * @param t 入口类型
	 * @param info1 信息1
	 * @param info2 信息2
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-14 下午4:11:26
	 */
	public ApiMessage entrance(String appId, String code, String gEncryptedData, String gIv, String t, String info1, String info2);
	/**
	 * 小程序确认授权
	 * 
	 * @param code
	 * @param encryptedData
	 * @param iv
	 * @param source
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-19 下午6:20:07
	 */
	public ApiMessage init(String appId, String code, String encryptedData, String iv, String source);
	
	public WeixinUser getUser(String openId);
	
	/**
	 * 查询群内成员列表
	 * @Title: queryWeixinUserList
	 * @param page
	 * @param openGId
	 * @param type 查询类型；0：所有；1：家长；2：老师； 3：班主任
	 * @param key 搜索条件：可搜索称呼、昵称、手机号
	 * @param wuId
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-15 下午1:54:09
	 */
	public ApiMessage queryWeixinUserList(Page page, String openGId, int type, String key, int wuId, ReqCode reqCode);
	
	/**
	 * 删除群成员
	 * @Title: deleteUser
	 * @param ugId
	 * @param type
	 * @param wuId
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-21 下午7:35:01
	 */
	public ApiMessage deleteUser(int ugId, int type, int wuId, ReqCode reqCode);
	
	/**
	 * 批量删除群成员
	 * @Title: batchDeleteUser
	 * @param openGId
	 * @param ugIds
	 * @param wuId
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-25 上午9:46:49
	 */
	public ApiMessage batchDeleteUser(String openGId, int[] ugIds, int wuId, ReqCode reqCode);

	/**
	 * 修改群成员
	 * @Title: updateUser
	 * @param ugId
	 * @param phone
	 * @param wuId
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-25 上午11:13:59
	 */
	public ApiMessage updateUser(int ugId, String phone, int wuId, ReqCode reqCode);
	
	/**
	 * 查询群成员数量
	 * @Title: getGroupUserCnt
	 * @param openGId
	 * @param type
	 * @param wuId
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-25 下午3:13:50
	 */
	public ApiMessage getGroupUserCnt(String openGId, int type, int wuId, ReqCode reqCode);
	/**
	 * 获得用户信息
	 * 
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-27 下午1:55:44
	 */
	public ApiMessage getUser(String openId, String openGId);
	/**
	 * 修改用户信息
	 * 
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @param phone 电话
	 * @param realName 真实姓名
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-27 下午3:21:26
	 */
	public ApiMessage updateUser(String openId, String openGId, String phone, String realName);
}
