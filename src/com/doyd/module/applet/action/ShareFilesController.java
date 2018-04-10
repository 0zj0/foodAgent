package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import com.doyd.biz.IShareFilesService;
import com.doyd.biz.IWeixinUserService;
import com.doyd.model.WeixinUser;
import com.doyd.module.pc.utils.ParamUtil;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import org.doyd.utils.StringUtil;


import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author ylb
 * @version 创建时间：2017-12-29 上午10:12:13
 */
@Controller
@RequestMapping(value = {"applet"})
public class ShareFilesController {

	@Autowired
	private IShareFilesService shareFilesService;
	@Autowired
	private IWeixinUserService weixinUserService;
	/**
	 * 获得共享文件
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-29 上午10:14:27
	 */
	@RequestMapping(value="/share")
	@ResponseBody
	public ApiMessage getShare(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		String key = request.getParameter("key");
		int page = StringUtil.parseInt(request.getParameter("page"),1);
		return shareFilesService.getShareFiles(openId, openGId, key, page);
	}
	/**
	 * 删除共享文件
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-29 下午4:08:34
	 */
	@RequestMapping(value="/share/delete")
	@ResponseBody
	public ApiMessage deleteShare(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		ReqCode reqCode = ReqCode.DeleteShare;
		WeixinUser weixinUser = weixinUserService.getUser(openId);
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		String s = request.getParameter("shareId");
		int[] shareIds = null;
		if(StringUtil.isNotEmpty(s)){
			shareIds = ParamUtil.parseIntArray(s.split(","));
		}
		if(StringUtil.isEmpty(openGId) || shareIds==null || shareIds.length<=0){
			return new ApiMessage(reqCode, ReqState.ApiParamError);
		}
		return shareFilesService.deleteShareFiles(weixinUser, openGId, shareIds, reqCode);
	}
}
