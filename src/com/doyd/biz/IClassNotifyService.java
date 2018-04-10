package com.doyd.biz;

import org.json.JSONArray;

import com.doyd.core.action.common.Page;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;

public interface IClassNotifyService {
	
	/**
	 * 分页查询班务通知
	 * @Title: queryClassNotifyByPage
	 * @param page
	 * @param openGId
	 * @param type
	 * @param wuId
	 * @param key
	 * @param beginDate
	 * @param endDate
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午2:35:31
	 */
	public ApiMessage queryClassNotifyByPage(Page page, String openGId, int type, int wuId, String key, String beginDate, String endDate, ReqCode reqCode);
	/**
	 * 分页查询班务通知
	 * 
	 * @param page 页码信息
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @param identity 访问身份
	 * @param key 0，全部，1、我发布的、2，其他人发布的
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-6 下午3:43:22
	 */
	public ApiMessage queryClassNotifyByPage(int page, String openId, String openGId, String identity, int key);
	
	/**
	 * 查看班务通知详情
	 * @Title: showClassNotify
	 * @param notifyId
	 * @param weixinUser
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午3:44:34
	 */
	public ApiMessage showClassNotify(int notifyId, WeixinUser weixinUser, ReqCode reqCode);
	/**
	 * 获得班务通知详情
	 * 
	 * @param openId 用户openId
	 * @param notifyId 通知Id
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-6 下午4:23:58
	 */
	public ApiMessage showClassNotify(String openId, int notifyId);
	
	/**
	 * 新建班务通知
	 * @Title: createClassNotify
	 * @param weixinUser
	 * @param openGId
	 * @param title
	 * @param content
	 * @param files
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 上午9:58:40
	 */
	public ApiMessage createClassNotify(WeixinUser weixinUser, String openGId, String title, String content, JSONArray files, ReqCode reqCode);
	
	/**
	 * 删除/批量删除班务通知
	 * @Title: deleteClassNotify
	 * @param weixinUser
	 * @param openGId
	 * @param notifyIds
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午1:48:09
	 */
	public ApiMessage deleteClassNotify(WeixinUser weixinUser, String openGId, int[] notifyIds, ReqCode reqCode);
	
	/**
	 * 查询班务通知数量
	 * @Title: getClassNotifyCnt
	 * @param weixinUser
	 * @param openGId
	 * @param type
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午3:42:32
	 */
	public ApiMessage getClassNotifyCnt(WeixinUser weixinUser, String openGId, int type, ReqCode reqCode);
	
}
