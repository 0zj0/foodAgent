package com.doyd.biz;

import com.doyd.msg.ApiMessage;

public interface IChildrenService {
	/**
	 * 添加孩子
	 * 
	 * @param openId 用户openId
	 * @param openGId 微信群Id
	 * @param childName 孩子姓名
	 * @param relation 和孩子关系
	 * @param education 学历，教育
	 * @param grade 年级
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-21 下午5:13:59
	 */
	public ApiMessage addChild(String baseUrl, String openId, String openGId, String groupName, String childName
			, String relation, int education, int grade);
	/**
	 * 添加孩子--多个群
	 * 
	 * @param baseUrl
	 * @param openId 用户openId
	 * @param childName 孩子姓名
	 * @param relation 和孩子关系
	 * @param education 学历，教育
	 * @param grade 年级
	 * @param openGIds 微信群Id数组
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-13 上午11:01:18
	 */
	public ApiMessage addChild(String baseUrl, String openId, String childName, String relation
			, int education, int grade, String[] openGIds);
	/**
	 * 修改孩子信息
	 * 
	 * @param openId 用户openId
	 * @param childId 孩子Id
	 * @param childName 孩子名称
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-21 下午7:20:17
	 */
	public ApiMessage updateChild(String openId, int childId, String childName, String relation);
}
