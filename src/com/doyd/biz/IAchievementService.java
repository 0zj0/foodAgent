package com.doyd.biz;

import org.json.JSONObject;

import com.doyd.core.action.common.Page;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;

public interface IAchievementService {
	
	/**
	 * 分页查询成绩单
	 * @Title: queryAchievementByPage
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
	public ApiMessage queryAchievementByPage(Page page, String openGId, int wuId, String key, int type, ReqCode reqCode);
	/**
	 * 分页查询成绩单
	 * 
	 * @param page 页码
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @param key 搜索条件
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-3 下午5:06:38
	 */
	public ApiMessage queryAchievementByPage(int page, String openId, String openGId, String key);
	
	/**
	 * 新建成绩单
	 * @Title: createAchievement
	 * @param weixinUser
	 * @param openGId
	 * @param remark
	 * @param file
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 上午9:58:40
	 */
	public ApiMessage createAchievement(WeixinUser weixinUser, String openGId, String remark, JSONObject file, ReqCode reqCode);
	
	/**
	 * 删除/批量删除成绩单
	 * @Title: deleteAchievement
	 * @param weixinUser
	 * @param openGId
	 * @param scoreIds
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午1:48:09
	 */
	public ApiMessage deleteAchievement(WeixinUser weixinUser, String openGId, int[] scoreIds, ReqCode reqCode);
	
	/**
	 * 修改成绩单（备注）
	 * @Title: updateAchievement
	 * @param weixinUser
	 * @param scoreId
	 * @param remark
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午2:03:51
	 */
	public ApiMessage updateAchievement(WeixinUser weixinUser, int scoreId, String remark, ReqCode reqCode);
	
	/**
	 * 查询成绩单数量
	 * @Title: getAchievementCnt
	 * @param weixinUser
	 * @param openGId
	 * @param type
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午3:42:32
	 */
	public ApiMessage getAchievementCnt(WeixinUser weixinUser, String openGId, int type, ReqCode reqCode);
	
}
