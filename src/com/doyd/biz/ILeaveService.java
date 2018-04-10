package com.doyd.biz;

import com.doyd.core.action.common.Page;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;

public interface ILeaveService {
	/**
	 * 获得请假审批
	 * 
	 * @param openId 用户openId
	 * @param identity 身份director,teacher,patriarch
	 * @param ugId 用户群序号
	 * @param state 审核状态0，全部 1，未审核，2：审核中（已经查看记录），3：审核成功，4：审核失败
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-28 上午9:19:53
	 */
	public ApiMessage getLeaveList(String openId, String identity, int ugId, int state, int page);
	/**
	 * 添加请假审批
	 * 
	 * @param openId 用户openId
	 * @param ugId 用户群关系Id
	 * @param reason 请假理由
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-29 上午9:20:15
	 */
	public ApiMessage addLeave(String openId, int ugId, String reason, String startTime, String endTime);
	/**
	 * 分页查询请假审批
	 * @Title: queryLeaveByPage
	 * @param page
	 * @param openGId
	 * @param wuId
	 * @param type
	 * @param key
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午2:35:31
	 */
	public ApiMessage queryLeaveByPage(Page page, String openGId, int wuId, int type, String key, ReqCode reqCode);
	
	/**
	 * 审核请假审批
	 * @Title: auditLeave
	 * @param weixinUser
	 * @param leaveId
	 * @param auditState
	 * @param auditResult
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午2:03:51
	 */
	public ApiMessage auditLeave(WeixinUser weixinUser, int leaveId, int auditState, String auditResult, ReqCode reqCode);
	
	/**
	 * 查询请假审批数量
	 * @Title: getLeaveCnt
	 * @param weixinUser
	 * @param openGId
	 * @param type
	 * @param reqCode
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午3:42:32
	 */
	public ApiMessage getLeaveCnt(WeixinUser weixinUser, String openGId, int type, ReqCode reqCode);
	/**
	 * 班主任阅读请假审批
	 * 
	 * @param openId 用户openId
	 * @param leaveId 请假Id
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-7 下午4:35:04
	 */
	public ApiMessage readLeave(String openId, int leaveId);
}
