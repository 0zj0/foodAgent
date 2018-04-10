/**
 * 
 */
package com.doyd.module.pc.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IShareFilesService;
import com.doyd.biz.IWeixinUserService;
import com.doyd.core.action.common.Page;
import com.doyd.model.WeixinUser;
import com.doyd.module.pc.ticket.WeixinUserTicketServer;
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
public class PCShareController {
	
	@Autowired
	private IShareFilesService shareFilesService;
	@Autowired
	private IWeixinUserService weixinUserService;
	
	/**
	 * 分页查询共享文件
	 * @Title: queryShare
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午2:11:27
	 */
	@RequestMapping(value="/share/query/page",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage queryShare(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.GetShareList;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		//群id
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		if(StringUtil.isEmpty(openGId)){
			return new ApiMessage(reqCode, ReqState.LackOpenGId);
		}
		
		//搜索条件：可根据发布人（作业内容）搜索
		String key = StringUtil.trim(request.getParameter("key"));
		int type = StringUtil.parseInt(request.getParameter("type"));
		Page page = new Page(request);
		return shareFilesService.queryShareFilesByPage(page, openGId, weixinUser.getWuId(), key, type, reqCode);
	}
	
	/**
	 * 新建共享文件
	 * @Title: createShare
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午6:35:00
	 */
	@RequestMapping(value="/share/create",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage createShare(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.CreateShare;
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
			JSONObject file = json.getJSONObject("file");
			if(file==null){
				return new ApiMessage(reqCode, ReqState.LackFile);
			}
			String remark = json.getString("remark");
			return shareFilesService.createShareFiles(weixinUser, openGId, remark, file, reqCode);
		}catch (Exception e) {
			e.printStackTrace();
			return new ApiMessage(reqCode, ReqState.Failure);
		}
	}
	
	/**
	 * 删除/批量删除共享文件
	 * @Title: deleteShare
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午3:37:11
	 */
	@RequestMapping(value="/share/delete",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage deleteShare(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.DeleteShare;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		if(StringUtil.isEmpty(openGId)){
			return new ApiMessage(reqCode, ReqState.LackOpenGId);
		}
		int[] shareIds = ParamUtil.getIntArray(request, "shareId");
		if(shareIds==null || shareIds.length<=0){
			return new ApiMessage(reqCode, ReqState.LackShareId);
		}
		return shareFilesService.deleteShareFiles(weixinUser, openGId, shareIds, reqCode);
	}
	
	/**
	 * 修改共享文件（备注）
	 * @Title: updateShare
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午8:46:29
	 */
	@RequestMapping(value="/share/update",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage updateShare(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.UpdateShare;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String remark = StringUtil.trim(request.getParameter("remark"));
		if(StringUtil.isEmpty(remark)){
			return new ApiMessage(reqCode, ReqState.EmptyRemark);
		}
		int shareId = StringUtil.parseInt(request.getParameter("shareId"));
		if(shareId<=0){
			return new ApiMessage(reqCode, ReqState.LackShareId);
		}
		return shareFilesService.updateShareFiles(weixinUser, shareId, remark, reqCode);
	}
	
	/**
	 * 查询共享文件数量
	 * @Title: getShareCnt
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午4:02:09
	 */
	@RequestMapping(value="/share/getCnt",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage getShareCnt(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.GetShareCnt;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		if(StringUtil.isEmpty(openGId)){
			return new ApiMessage(reqCode, ReqState.LackOpenGId);
		}
		int type = StringUtil.parseInt(request.getParameter("type"));
		return shareFilesService.getShareFilesCnt(weixinUser, openGId, type, reqCode);
	}

}
