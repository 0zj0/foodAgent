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

import com.doyd.biz.ISubjectService;
import com.doyd.biz.IWeixinUserService;
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
public class PCSubjectController {
	
	@Autowired
	private ISubjectService subjectService;
	@Autowired
	private IWeixinUserService weixinUserService;
	
	@RequestMapping(value="/subject/query",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage querySubject(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.GetSubjectList;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		//群id
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		if(StringUtil.isEmpty(openGId)){
			return new ApiMessage(reqCode, ReqState.LackOpenGId);
		}
		return subjectService.getSubjectList(openGId, weixinUser.getWuId(), reqCode);
	}
	
	@RequestMapping(value="/subject/create",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage createSubject(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.CreateSubject;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		if(!weixinUser.isDirector() && !weixinUser.isTeacher()){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		//学科/科目
		String subject = StringUtil.trim(request.getParameter("subject"));
		if(StringUtil.isEmpty(subject)){
			return new ApiMessage(reqCode, ReqState.EmptySubject);
		}
		if("语文，数学，英语，物理，化学，生物，政治，地理，历史，音乐，美术，体育".contains(subject)){
			return new ApiMessage(reqCode, ReqState.ExistSubject);
		}
		return subjectService.addSubject(weixinUser.getWuId(), subject, reqCode);
	}

}
