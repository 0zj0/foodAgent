package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IGroupMessageService;
import com.doyd.msg.ApiMessage;
import org.doyd.utils.StringUtil;

/**
 * @author ylb
 * @version 创建时间：2017-12-27 下午8:06:39
 */
@Controller
@RequestMapping(value = {"applet"})
public class GroupMessageController {

	@Autowired
	private IGroupMessageService groupMessageService;
	/**
	 * 阅读消息
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-27 下午8:51:36
	 */
	@RequestMapping(value="/message/read")
	@ResponseBody
	public ApiMessage readMsg(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		int msgId = StringUtil.parseInt(request.getParameter("msgId"));
		int result = StringUtil.parseInt(request.getParameter("result"));
		return groupMessageService.readMes(openId, msgId, result);
	}
	
	@RequestMapping(value="/message")
	@ResponseBody
	public ApiMessage message(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		return groupMessageService.getMessage(openId, openGId);
	}
}
