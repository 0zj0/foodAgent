/**
 * 
 */
package com.doyd.module.pc.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.ILeaveService;
import com.doyd.biz.IWeixinUserService;
import com.doyd.core.action.common.Page;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import org.doyd.utils.StringUtil;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = {"pc"})
public class PCLeaveController {
	
	@Autowired
	private ILeaveService leaveService;
	@Autowired
	private IWeixinUserService weixinUserService;
	
	/**
	 * 分页查询请假审批
	 * @Title: queryLeave
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午2:11:27
	 */
	@RequestMapping(value="/leave/query/page",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage queryLeave(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.GetLeaveList;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		//群id
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		if(StringUtil.isEmpty(openGId)){
			return new ApiMessage(reqCode, ReqState.LackOpenGId);
		}
		
		//搜索条件：可根据请假人搜索
		String key = StringUtil.trim(request.getParameter("key"));
		int type = StringUtil.parseInt(request.getParameter("type"));
		Page page = new Page(request);
		return leaveService.queryLeaveByPage(page, openGId, weixinUser.getWuId(), type, key, reqCode);
	}
	
	/**
	 * 审核请假审批
	 * @Title: auditShare
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午8:46:12
	 */
	@RequestMapping(value="/leave/audit",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage auditShare(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.AuditLeave;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		int leaveId = StringUtil.parseInt(request.getParameter("leaveId"));
		if(leaveId<=0){
			return new ApiMessage(reqCode, ReqState.LackLeaveId);
		}
		int auditState = StringUtil.parseInt(request.getParameter("auditState"));
		if(auditState!=3 && auditState!=4){
			return new ApiMessage(reqCode, ReqState.WrongAuditState);
		}
		String auditResult = StringUtil.trim(request.getParameter("auditResult"));
		return leaveService.auditLeave(weixinUser, leaveId, auditState, auditResult, reqCode);
	}
	
	/**
	 * 查询请假审批数量
	 * @Title: getLeaveCnt
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午4:02:09
	 */
	@RequestMapping(value="/leave/getCnt",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage getLeaveCnt(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.GetLeaveCnt;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		if(StringUtil.isEmpty(openGId)){
			return new ApiMessage(reqCode, ReqState.ApiParamError);
		}
		int type = StringUtil.parseInt(request.getParameter("type"));
		return leaveService.getLeaveCnt(weixinUser, openGId, type, reqCode);
	}

}
