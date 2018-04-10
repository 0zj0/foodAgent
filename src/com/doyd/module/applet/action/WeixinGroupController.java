package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.doyd.biz.IWeixinGroupService;
import com.doyd.msg.ApiMessage;

/**
 * @author ylb
 * @version 创建时间：2018-1-4 上午9:51:12
 */
@Controller
@RequestMapping(value = {"applet"})
public class WeixinGroupController {

	@Autowired
	private IWeixinGroupService weixinGroupService;
	
	@RequestMapping(value="/group/openGId")
	@ResponseBody
	public ApiMessage getOpenGId(HttpServletRequest request){
		String appId = request.getParameter("appId");
		String openId = (String) request.getAttribute("openId");
		String code = request.getParameter("code");
		String gEncryptedData = request.getParameter("gEncryptedData");
		String gIv = request.getParameter("gIV");
		return weixinGroupService.getOpenGId(appId, openId, code, gEncryptedData, gIv);
	}
	/**
	 * 添加/修改群名称
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-12 下午3:19:59
	 */
	@RequestMapping(value="/group/update/name")
	@ResponseBody
	public ApiMessage updateGroupName(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		String groupName = request.getParameter("groupName");
		return weixinGroupService.updateGroupName(openId, openGId, groupName);
	}
}
