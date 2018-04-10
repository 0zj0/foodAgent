package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IElectionLordService;
import com.doyd.msg.ApiMessage;
import org.doyd.utils.StringUtil;

/**
 * @author ylb
 * @version 创建时间：2017-12-26 上午9:34:42
 */
@Controller
@RequestMapping(value = {"applet"})
public class ElectionLordController {

	@Autowired
	private IElectionLordService electionLordService;
	/**
	 * 发起纠正页----获得班主任信息
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 上午9:41:01
	 */
	@RequestMapping(value="/election")
	@ResponseBody
	public ApiMessage election(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		return electionLordService.election(openId, openGId);
	}
	/**
	 * 添加选举信息
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 上午10:30:09
	 */
	@RequestMapping(value="/election/add")
	@ResponseBody
	public ApiMessage addElection(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		String electionType = request.getParameter("electionType");
		return electionLordService.addElection(openId, openGId, electionType);
	}
	/**
	 * 获得选举信息
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-26 下午1:48:54
	 */
	@RequestMapping(value="/election/show")
	@ResponseBody
	public ApiMessage shoeElection(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		int electionId = StringUtil.parseInt(request.getParameter("electionId"));
		return electionLordService.showElection(openId, electionId);
	}
}
