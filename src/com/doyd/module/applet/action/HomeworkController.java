package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IHomeworkService;
import com.doyd.biz.IWeixinUserService;
import com.doyd.model.WeixinUser;
import com.doyd.module.pc.utils.ParamUtil;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;

import org.doyd.utils.DateUtil;
import org.doyd.utils.StringUtil;

/**
 * @author ylb
 * @version 创建时间：2017-12-29 下午4:43:15
 */
@Controller
@RequestMapping(value = {"applet"})
public class HomeworkController {

	@Autowired
	private IHomeworkService homeworkService;
	@Autowired
	private IWeixinUserService weixinUserService;
	/**
	 * 获得家庭作业
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-29 下午4:44:09
	 */
	@RequestMapping(value="/homework")
	@ResponseBody
	public ApiMessage homework(HttpServletRequest request){
		ReqCode reqCode = ReqCode.Homework;
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		String identity = request.getParameter("identity");
		int key = StringUtil.parseInt(request.getParameter("key"));
		int page = StringUtil.parseInt(request.getParameter("page"), 1);
		return homeworkService.queryWorkByPage(page, openId, openGId, identity, key, reqCode);
	}
	/**
	 * 获得家庭作业详情
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-5 下午5:54:22
	 */
	@RequestMapping(value="/homework/show")
	@ResponseBody
	public ApiMessage showHomework(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		int workId = StringUtil.parseInt(request.getParameter("workId"));
		return homeworkService.showWork(openId, workId);
	}
	/**
	 * 新建作业
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-6 下午1:34:49
	 */
	@RequestMapping(value="/homework/add")
	@ResponseBody
	public ApiMessage addHomework(HttpServletRequest request){
		ReqCode reqCode = ReqCode.HomeworkAdd;
		String openId = (String) request.getAttribute("openId");
		int costTime = StringUtil.parseInt(request.getParameter("costTime"));
		String f = request.getParameter("files");
		JSONArray files = null;
		try{
			if(StringUtil.isNotEmpty(f)){
				files = new JSONArray(f);
			}
		}catch (Exception e) {
			return new ApiMessage(reqCode, ReqState.ApiParamError);
		}
		String openGId = request.getParameter("openGId");
		String subject = StringUtil.trim(request.getParameter("subject"));
		String submitTime = request.getParameter("submitTime");
		String work = request.getParameter("work");
		WeixinUser wu = weixinUserService.getUser(openId);
		if(wu == null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		if(StringUtil.isEmpty(openGId) || StringUtil.isEmpty(subject) ||
				StringUtil.isEmpty(submitTime) || StringUtil.isEmpty(work)){
			return new ApiMessage(reqCode, ReqState.ApiParamError);
		}
		if(!submitTime.matches("\\d{4}\\-(0[1-9]|1[0-2])\\-(0[1-9]|[12][0-9]|3[0-1])")){
			return new ApiMessage(reqCode, ReqState.WrongTime);
		}
		if(DateUtil.before(submitTime.substring(0,10) + " 00:00:00", DateUtil.today() + " 00:00:00")){
			return new ApiMessage(reqCode, ReqState.SubmitTimeBeforeToday);
		}
		return homeworkService.createWork(wu, openGId, subject, submitTime, work, costTime, files, reqCode);
	}
	/**
	 * 删除作业
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-6 下午1:42:24
	 */
	@RequestMapping(value="/homework/delete")
	@ResponseBody
	public ApiMessage deleteHomework(HttpServletRequest request){
		ReqCode reqCode = ReqCode.HomeworkDelete;
		String openId = (String) request.getAttribute("openId");
		WeixinUser wu = weixinUserService.getUser(openId);
		if(wu == null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String openGId = request.getParameter("openGId");
		String ids = request.getParameter("workId");
		int[] workId = null;
		if(StringUtil.isNotEmpty(ids)){
			workId = ParamUtil.parseIntArray(ids.split(","));
		}
		if(StringUtil.isEmpty(openGId) || workId==null || workId.length<=0){
			return new ApiMessage(reqCode, ReqState.ApiParamError);
		}
		return homeworkService.deleteWork(wu, openGId, workId, reqCode);
	}
	
	
	
	/**
	 * 获得作业和班务
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-7 上午10:13:54
	 */
	@RequestMapping(value="/homework_notify")
	@ResponseBody
	public ApiMessage homework_notify(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		int page = StringUtil.parseInt(request.getParameter("page"), 1);
		return homeworkService.homework_notify(openId, openGId, page);
	}
}
