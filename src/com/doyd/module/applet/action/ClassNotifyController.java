package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IClassNotifyService;
import com.doyd.biz.IWeixinUserService;
import com.doyd.model.WeixinUser;
import com.doyd.module.pc.utils.ParamUtil;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import org.doyd.utils.StringUtil;

/**
 * @author ylb
 * @version 创建时间：2018-1-6 下午1:51:22
 */
@Controller
@RequestMapping(value = {"applet"})
public class ClassNotifyController {

	@Autowired
	private IClassNotifyService classNotifyService;
	@Autowired
	private IWeixinUserService weixinUserService;
	/**
	 * 获得班务通知
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-6 下午3:57:14
	 */
	@RequestMapping(value="/classnotify")
	@ResponseBody
	public ApiMessage classnotify(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		String identity = request.getParameter("identity");
		int key = StringUtil.parseInt(request.getParameter("key"));
		int page = StringUtil.parseInt(request.getParameter("page"), 1);
		return classNotifyService.queryClassNotifyByPage(page, openId, openGId, identity, key);
	}
	/**
	 * 获得班务通知详情
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-6 下午4:43:56
	 */
	@RequestMapping(value="/classnotify/show")
	@ResponseBody
	public ApiMessage showClassnotify(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		int notifyId = StringUtil.parseInt(request.getParameter("notifyId"));
		return classNotifyService.showClassNotify(openId, notifyId);
	}
	/**
	 * 添加班务
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-6 下午6:18:39
	 */
	@RequestMapping(value="/classnotify/add")
	@ResponseBody
	public ApiMessage addClassnotify(HttpServletRequest request){
		ReqCode reqCode = ReqCode.ClassnotifyAdd;
		String openId = (String) request.getAttribute("openId");
		String content = request.getParameter("content");
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
		String title = request.getParameter("title");
		WeixinUser wu = weixinUserService.getUser(openId);
		if(wu == null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		if(StringUtil.isEmpty(openGId) || StringUtil.isEmpty(title) ||
				StringUtil.isEmpty(content)){
			return new ApiMessage(reqCode, ReqState.ApiParamError);
		}
		return classNotifyService.createClassNotify(wu, openGId, title, content, files, reqCode);
	}
	/**
	 * 删除班务通知
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-6 下午7:17:48
	 */
	@RequestMapping(value="/classnotify/delete")
	@ResponseBody
	public ApiMessage deleteClassnotify(HttpServletRequest request){
		ReqCode reqCode = ReqCode.ClassnotifyDelete;
		String openId = (String) request.getAttribute("openId");
		WeixinUser wu = weixinUserService.getUser(openId);
		if(wu == null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String openGId = request.getParameter("openGId");
		String ids = request.getParameter("notifyId");
		int[] notifyIds = null;
		if(StringUtil.isNotEmpty(ids)){
			notifyIds = ParamUtil.parseIntArray(ids.split(","));
		}
		if(StringUtil.isEmpty(openGId) || notifyIds==null || notifyIds.length<=0){
			return new ApiMessage(reqCode, ReqState.ApiParamError);
		}
		return classNotifyService.deleteClassNotify(wu, openGId, notifyIds, reqCode);
	}
}
