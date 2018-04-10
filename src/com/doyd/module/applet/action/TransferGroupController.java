package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.ITransferGroupService;
import com.doyd.msg.ApiMessage;
import org.doyd.utils.StringUtil;

/**
 * @author ylb
 * @version 创建时间：2017-12-26 下午5:33:10
 */
@Controller
@RequestMapping(value = {"applet"})
public class TransferGroupController {

	@Autowired
	private ITransferGroupService transferGroupService;
	/**
	 * 添加转让信息
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 下午7:38:56
	 */
	@RequestMapping(value="/transfer/add")
	@ResponseBody
	public ApiMessage addTransfer(HttpServletRequest request){
		String acceptOpenId = request.getParameter("acceptOpenId");
		String openGId = request.getParameter("openGId");
		String openId = (String) request.getAttribute("openId");
		return transferGroupService.addTransfer(openId, openGId, acceptOpenId);
	}
	/**
	 * 获得选举信息
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 下午5:58:22
	 */
	@RequestMapping(value="/transfer/show")
	@ResponseBody
	public ApiMessage showTransfer(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		int transferId = StringUtil.parseInt(request.getParameter("transferId"));
		return transferGroupService.showTransfer(openId, transferId);
	}
	/**
	 * 接收转让
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 下午7:59:51
	 */
	@RequestMapping(value="/transfer/accept")
	@ResponseBody
	public ApiMessage accept(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		int transferId = StringUtil.parseInt(request.getParameter("transferId"));
		String realName = request.getParameter("realName");
		return transferGroupService.accept(openId, transferId, realName);
	}
}
