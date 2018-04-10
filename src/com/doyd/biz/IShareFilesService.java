package com.doyd.biz;

import org.json.JSONObject;

import com.doyd.core.action.common.Page;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;

public interface IShareFilesService {
	
	/**
	 * 分页查询共享文件
	 * @Title: queryShareFilesByPage
	 * @param page
	 * @param openGId
	 * @param wuId
	 * @param key
	 * @param type
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午2:35:31
	 */
	public ApiMessage queryShareFilesByPage(Page page, String openGId, int wuId, String key, int type, ReqCode reqCode);
	
	/**
	 * 新建共享文件
	 * @Title: createShareFiles
	 * @param weixinUser
	 * @param openGId
	 * @param remark
	 * @param file
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 上午9:58:40
	 */
	public ApiMessage createShareFiles(WeixinUser weixinUser, String openGId, String remark, JSONObject file, ReqCode reqCode);
	
	/**
	 * 删除/批量删除共享文件
	 * @Title: deleteShareFiles
	 * @param weixinUser
	 * @param openGId
	 * @param shareIds
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午1:48:09
	 */
	public ApiMessage deleteShareFiles(WeixinUser weixinUser, String openGId, int[] shareIds, ReqCode reqCode);
	
	/**
	 * 修改共享文件（备注）
	 * @Title: updateShareFiles
	 * @param weixinUser
	 * @param shareId
	 * @param remark
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午2:03:51
	 */
	public ApiMessage updateShareFiles(WeixinUser weixinUser, int shareId, String remark, ReqCode reqCode);
	
	/**
	 * 查询共享文件数量
	 * @Title: getShareFilesCnt
	 * @param weixinUser
	 * @param openGId
	 * @param type
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午3:42:32
	 */
	public ApiMessage getShareFilesCnt(WeixinUser weixinUser, String openGId, int type, ReqCode reqCode);
	/**
	 * 获得共享文件
	 * 
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @param key 搜索关键字
	 * @param page 页码
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-29 上午10:27:10
	 */
	public ApiMessage getShareFiles(String openId, String openGId, String key, int page);
}
