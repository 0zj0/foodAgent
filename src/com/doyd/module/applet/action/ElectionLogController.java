package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IElectionLogService;
import com.doyd.msg.ApiMessage;
import org.doyd.utils.StringUtil;

/**
 * @author ylb
 * @version 创建时间：2017-12-26 下午1:49:51
 */
@Controller
@RequestMapping(value = {"applet"})
public class ElectionLogController {
	
	@Autowired
	private IElectionLogService electionLogService;

	@RequestMapping(value="/election/log/add")
	@ResponseBody
	public ApiMessage addLog(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		int electionId = StringUtil.parseInt(request.getParameter("electionId"));
		int result = StringUtil.parseInt(request.getParameter("result"));
		return electionLogService.addElectionLog(openId, electionId, result);
	}
}
