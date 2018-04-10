/**
 * 
 */
package com.doyd.module.pc.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IClassNotifyService;
import com.doyd.biz.IWeixinUserService;
import com.doyd.core.action.common.Page;
import com.doyd.model.WeixinUser;
import com.doyd.module.pc.utils.ParamUtil;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import com.doyd.util.HttpclientUtil;
import org.doyd.utils.StringUtil;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = {"pc"})
public class PCClassNotifyController {
	
	@Autowired
	private IClassNotifyService classNotifyService;
	@Autowired
	private IWeixinUserService weixinUserService;
	
	/**
	 * 分页查询班务通知
	 * @Title: queryClassNotify
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午2:11:27
	 */
	@RequestMapping(value="/class_notify/query/page",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage queryClassNotify(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.GetClassNotifyList;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		//群id
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		if(StringUtil.isEmpty(openGId)){
			return new ApiMessage(reqCode, ReqState.LackOpenGId);
		}
		
		//查询类型；0：发布者不限；1：我发布的； 2：其他人发布的
		int type = StringUtil.parseInt(request.getParameter("type"));
		//搜索条件：可根据主题、发布人搜索
		String key = StringUtil.trim(request.getParameter("key"));
		//发布起始日期
		String beginDate = StringUtil.trim(request.getParameter("beginDate"));
		//发布结束日期
		String endDate = StringUtil.trim(request.getParameter("endDate"));
		Page page = new Page(request);
		return classNotifyService.queryClassNotifyByPage(page, openGId, type, weixinUser.getWuId(), key, beginDate, endDate, reqCode);
	}
	
	/**
	 * 显示班务通知详情
	 * @Title: showClassNotify
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午5:46:48
	 */
	@RequestMapping(value="/class_notify/show",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage showClassNotify(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.ShowClassNotify;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		int notifyId = StringUtil.parseInt(request.getParameter("notifyId"));
		if(notifyId<=0){
			return new ApiMessage(reqCode, ReqState.LackNotifyId);
		}
		return classNotifyService.showClassNotify(notifyId, weixinUser, reqCode);
	}
	
	/**
	 * 新建班务通知
	 * @Title: createClassNotify
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午6:35:00
	 */
	@RequestMapping(value="/class_notify/create",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage createClassNotify(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.CreateClassNotify;
		try{
			WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
			if(weixinUser==null){
				return new ApiMessage(reqCode, ReqState.NoExistUser);
			}
			String requestBody = HttpclientUtil.getRequestInput(request, null);
			if(StringUtil.isEmpty(requestBody)) {
				return new ApiMessage(reqCode, ReqState.NoRequestData);
			}
			JSONObject json = new JSONObject(requestBody);
			String openGId = json.getString("openGId");
			if(StringUtil.isEmpty(openGId)){
				return new ApiMessage(reqCode, ReqState.LackOpenGId);
			}
			String title = json.getString("title");
			if(StringUtil.isEmpty(title)){
				return new ApiMessage(reqCode, ReqState.EmptyTitle);
			}
			String content = json.getString("content");
			if(StringUtil.isEmpty(title)){
				return new ApiMessage(reqCode, ReqState.EmptyContent);
			}
			JSONArray files = json.getJSONArray("files");
			return classNotifyService.createClassNotify(weixinUser, openGId, title, content, files, reqCode);
		}catch (Exception e) {
			e.printStackTrace();
			return new ApiMessage(reqCode, ReqState.Failure);
		}
	}
	
	/**
	 * 删除/批量删除班务通知
	 * @Title: deleteClassNotify
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午3:37:11
	 */
	@RequestMapping(value="/class_notify/delete",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage deleteClassNotify(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.DeleteClassNotify;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		int[] notifyIds = ParamUtil.getIntArray(request, "notifyId");
		if(StringUtil.isEmpty(openGId) || notifyIds==null || notifyIds.length<=0){
			return new ApiMessage(reqCode, ReqState.ApiParamError);
		}
		return classNotifyService.deleteClassNotify(weixinUser, openGId, notifyIds, reqCode);
	}
	
	/**
	 * 查询班务通知数量
	 * @Title: getClassNotifyCnt
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午4:02:09
	 */
	@RequestMapping(value="/class_notify/getCnt",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage getClassNotifyCnt(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.GetClassNotifyCnt;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		if(StringUtil.isEmpty(openGId)){
			return new ApiMessage(reqCode, ReqState.ApiParamError);
		}
		int type = StringUtil.parseInt(request.getParameter("type"));
		return classNotifyService.getClassNotifyCnt(weixinUser, openGId, type, reqCode);
	}

}
