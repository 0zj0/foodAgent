package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IReadClassService;
import com.doyd.msg.ApiMessage;
import org.doyd.utils.StringUtil;

/**
 * @author ylb
 * @version 创建时间：2018-1-6 下午5:00:54
 */
@Controller
@RequestMapping(value = {"applet"})
public class ReadClassController {

	@Autowired
	private IReadClassService readClassService;
	/**
	 * 获得班务阅读记录
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-6 下午5:02:30
	 */
	@RequestMapping(value="/classnotify/read")
	@ResponseBody
	public ApiMessage getReadClassnotify(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		int notifyId = StringUtil.parseInt(request.getParameter("notifyId"));
		int key = StringUtil.parseInt(request.getParameter("key"));
		return readClassService.getReadClassnotify(openId, notifyId, key);
	}
}
