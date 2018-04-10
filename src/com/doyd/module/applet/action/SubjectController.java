package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.ISubjectService;
import com.doyd.biz.IWeixinUserService;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;

/**
 * @author ylb
 * @version 创建时间：2018-2-7 上午10:14:58
 */
@Controller
@RequestMapping(value = {"applet"})
public class SubjectController {
	
	@Autowired
	private ISubjectService subjectService;
	@Autowired
	private IWeixinUserService weixinUserService;

	@RequestMapping(value="/homework/subject")
	@ResponseBody
	public ApiMessage getSubject(HttpServletRequest request){
		ReqCode reqCode = ReqCode.GetSubjectList;
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		WeixinUser wu = weixinUserService.getUser(openId);
		if(wu == null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		return subjectService.getSubjectList(openGId, wu.getWuId(), reqCode);
	}
}
