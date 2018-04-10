package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IAchievementService;
import com.doyd.biz.IWeixinUserService;
import com.doyd.model.WeixinUser;
import com.doyd.module.pc.utils.ParamUtil;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import org.doyd.utils.StringUtil;

/**
 * @author ylb
 * @version 创建时间：2018-1-3 下午5:00:25
 */
@Controller
@RequestMapping(value = {"applet"})
public class AchievementController {

	@Autowired
	private IAchievementService achievementService;
	@Autowired
	private IWeixinUserService weixinUserService;
	/**
	 * 获得成绩单
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-3 下午5:07:36
	 */
	@RequestMapping(value="/achievement")
	@ResponseBody
	public ApiMessage achievement(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		String key = request.getParameter("key");
		int page = StringUtil.parseInt(request.getParameter("page"), 1);
		return achievementService.queryAchievementByPage(page, openId, openGId, key);
	}
	/**
	 * 删除成绩单
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-3 下午7:07:59
	 */
	@RequestMapping(value="/achievement/delete")
	@ResponseBody
	public ApiMessage deleteAchievement(HttpServletRequest request){
		ReqCode reqCode = ReqCode.DeleteAchievement;
		String openId = (String) request.getAttribute("openId");
		WeixinUser weixinUser = weixinUserService.getUser(openId);
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String openGId = request.getParameter("openGId");
		String s = request.getParameter("scoreId");
		int[] scoreIds = null;
		if(StringUtil.isNotEmpty(s)){
			scoreIds = ParamUtil.parseIntArray(s.split(","));
		}
		if(StringUtil.isEmpty(openGId) || scoreIds==null || scoreIds.length<=0){
			return new ApiMessage(reqCode, ReqState.ApiParamError);
		}
		return achievementService.deleteAchievement(weixinUser, openGId, scoreIds, reqCode);
	}
}
