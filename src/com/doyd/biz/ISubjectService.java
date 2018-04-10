package com.doyd.biz;

import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;

public interface ISubjectService {
	
	/**
	 * 查询学科/任教科目
	 * @Title: getSubjectList
	 * @param openGId
	 * @param wuId
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午5:57:22
	 */
	public ApiMessage getSubjectList(String openGId, int wuId, ReqCode reqCode);
	
	/**
	 * 添加自定义学科
	 * @Title: addSubject
	 * @param wuId
	 * @param subject
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午6:21:39
	 */
	public ApiMessage addSubject(int wuId, String subject, ReqCode reqCode);
}
