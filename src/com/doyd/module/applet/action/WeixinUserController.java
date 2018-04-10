package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IWeixinUserService;
import com.doyd.msg.ApiMessage;

/**
 * @author ylb
 * @version 创建时间：2017-12-13 下午6:40:16
 */
@Controller
@RequestMapping(value = {"applet"})
public class WeixinUserController {
	
	@Autowired
	private IWeixinUserService weixinUserService;
	/**
	 * 初始化---中转页面
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-19 下午7:08:34
	 */
	@RequestMapping(value="/entrance")
	@ResponseBody
	public ApiMessage entrance(HttpServletRequest request){
		String appId = request.getParameter("appId");
		String code = request.getParameter("code");
		String t = request.getParameter("t");
		String info1 = request.getParameter("i1");
		String info2 = request.getParameter("i2");
		String gEncryptedData = request.getParameter("gEncryptedData");
		String gIv = request.getParameter("gIV");
		return weixinUserService.entrance(appId, code, gEncryptedData, gIv, t, info1, info2);
	}
	/**
	 * 用户授权
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-19 下午7:08:27
	 */
	@RequestMapping(value="/init")
	@ResponseBody
	public ApiMessage init(HttpServletRequest request){
		String appId = request.getParameter("appId");
		String code = request.getParameter("code");
		String encryptedData = request.getParameter("encryptedData");
		String iv = request.getParameter("iv");
		String source = request.getParameter("source");
		return weixinUserService.init(appId, code, encryptedData, iv, source);
	}
	/**
	 * 获得用户信息
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-27 下午2:12:53
	 */
	@RequestMapping(value="/user")
	@ResponseBody
	public ApiMessage getUser(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		return weixinUserService.getUser(openId, openGId);
	}
	/**
	 * 修改用户信息
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-27 下午2:12:53
	 */
	@RequestMapping(value="/user/update")
	@ResponseBody
	public ApiMessage updateUser(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		String phone = request.getParameter("phone");
		String realName = request.getParameter("realName");
		return weixinUserService.updateUser(openId, openGId, phone, realName);
	}
}
