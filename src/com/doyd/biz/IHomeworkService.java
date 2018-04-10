package com.doyd.biz;

import org.json.JSONArray;

import com.doyd.core.action.common.Page;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;

public interface IHomeworkService {
	
	/**
	 * 分页查询作业
	 * @Title: queryWorkByPage
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
	public ApiMessage queryWorkByPage(Page page, String openGId, int type, int wuId, String key, String beginDate, String endDate, ReqCode reqCode);
	/**
	 * 查询家庭作业
	 * 
	 * @param page 页码
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @param identity 身份director,teacher,patriarch
	 * @param key 搜索关键字
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-29 下午4:52:30
	 */
	public ApiMessage queryWorkByPage(int page, String openId, String openGId, String identity, int key, ReqCode reqCode);
	
	/**
	 * 查看作业详情
	 * @Title: showWork
	 * @param workId
	 * @param weixinUser
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午3:44:34
	 */
	public ApiMessage showWork(int workId, WeixinUser weixinUser, ReqCode reqCode);
	/**
	 * 获得作业详情
	 * 
	 * @param openId 用户openId
	 * @param workId 作业Id
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-5 下午6:12:02
	 */
	public ApiMessage showWork(String openId, int workId);
	
	/**
	 * 新建作业
	 * @Title: createWork
	 * @param weixinUser
	 * @param openGId
	 * @param subject
	 * @param submitTime
	 * @param work
	 * @param costTime
	 * @param files
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 上午9:58:40
	 */
	public ApiMessage createWork(WeixinUser weixinUser, String openGId, String subject, String submitTime, String work, int costTime, JSONArray files, ReqCode reqCode);
	
	/**
	 * 删除/批量删除作业
	 * @Title: deleteWork
	 * @param weixinUser
	 * @param openGId
	 * @param workIds
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午1:48:09
	 */
	public ApiMessage deleteWork(WeixinUser weixinUser, String openGId, int[] workIds, ReqCode reqCode);
	
	/**
	 * 查询作业数量
	 * @Title: getWorkCnt
	 * @param weixinUser
	 * @param openGId
	 * @param type
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午3:42:32
	 */
	public ApiMessage getWorkCnt(WeixinUser weixinUser, String openGId, int type, ReqCode reqCode);
	
	/**
	 * 获得作业和班务
	 * 
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @param page 页码信息
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-7 上午10:15:18
	 */
	public ApiMessage homework_notify(String openId, String openGId, int page);
}
