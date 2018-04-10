package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IReadWorkService;
import com.doyd.msg.ApiMessage;
import org.doyd.utils.StringUtil;

/**
 * @author ylb
 * @version 创建时间：2018-1-5 下午8:59:35
 */
@Controller
@RequestMapping(value = {"applet"})
public class ReadWorkController {
	
	@Autowired
	private IReadWorkService readWorkService;

	/**
	 * 获得作业阅读记录
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-5 下午8:58:44
	 */
	@RequestMapping(value="/homework/read")
	@ResponseBody
	public ApiMessage getReadHomework(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		int workId = StringUtil.parseInt(request.getParameter("workId"));
		int key = StringUtil.parseInt(request.getParameter("key")); // 1、已读 2、未读
		return readWorkService.getReadHomework(openId, workId, key);
	}
}
