package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.ILeaveService;
import com.doyd.biz.IWeixinUserService;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import org.doyd.utils.StringUtil;

/**
 * @author ylb
 * @version 创建时间：2017-12-27 下午8:52:47
 */
@Controller
@RequestMapping(value = {"applet"})
public class LeaveController {

	@Autowired
	private ILeaveService leaveService;
	@Autowired
	private IWeixinUserService weixinUserService;
	/**
	 * 获得请假审批
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-28 上午9:21:36
	 */
	@RequestMapping(value="/leave")
	@ResponseBody
	public ApiMessage leave(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String identity = request.getParameter("identity");
		int ugId = StringUtil.parseInt(request.getParameter("ugId"));
		int state = StringUtil.parseInt(request.getParameter("state"));
		int page = StringUtil.parseInt(request.getParameter("page"), 1);
		return leaveService.getLeaveList(openId, identity, ugId, state, page);
	}
	/**
	 * 添加请假审批
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-29 上午9:17:06
	 */
	@RequestMapping(value="/leave/add")
	@ResponseBody
	public ApiMessage addLeave(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		int ugId = StringUtil.parseInt(request.getParameter("ugId"));
		String reason = request.getParameter("reason");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		return leaveService.addLeave(openId, ugId, reason, startTime, endTime);
	}
	/**
	 * 班主任阅读请假审批
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-7 下午4:25:10
	 */
	@RequestMapping(value="/leave/read")
	@ResponseBody
	public ApiMessage readLeave(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		int leaveId = StringUtil.parseInt(request.getParameter("leaveId"));
		return leaveService.readLeave(openId, leaveId);
	}
	/**
	 * 
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-7 下午4:25:27
	 */
	@RequestMapping(value="/leave/update")
	@ResponseBody
	public ApiMessage updateLeave(HttpServletRequest request){
		ReqCode reqCode = ReqCode.UpdateLeave;
		String openId = (String) request.getAttribute("openId");
		WeixinUser weixinUser = weixinUserService.getUser(openId);
		if(weixinUser == null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		int leaveId = StringUtil.parseInt(request.getParameter("leaveId"));
		int auditState = StringUtil.parseInt(request.getParameter("auditState"));
		if(leaveId<=0 || (auditState != 3 && auditState != 4)){
			return new ApiMessage(reqCode, ReqState.ApiParamError);
		}
		String auditResult = request.getParameter("auditResult");
		return leaveService.auditLeave(weixinUser, leaveId, auditState, auditResult, reqCode);
	}
}
