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

import com.doyd.biz.IWeixinUserService;
import com.doyd.core.CoreVars;
import com.doyd.core.action.common.Page;
import com.doyd.model.WeixinUser;
import com.doyd.module.pc.utils.ParamUtil;
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
public class PCWeixinUserController {
	
	@Autowired
	private IWeixinUserService weixinUserService;
	
	/**
	 * 查询某群内的成员列表
	 * @Title: queryUser
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-14 下午4:07:02
	 */
	@RequestMapping(value="/weixin_user/query",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage queryUser(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.GetGroupUserList;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		//群id
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		if(StringUtil.isEmpty(openGId)){
			return new ApiMessage(reqCode, ReqState.LackOpenGId);
		}
		//查询类型；0：所有；1：家长；2：老师； 3：班主任
		int type = StringUtil.parseInt(request.getParameter("type"));
		//搜索条件：可搜索称呼、昵称、手机号
		String key = StringUtil.trim(request.getParameter("key"));
		Page page = new Page(request);
		return weixinUserService.queryWeixinUserList(page, openGId, type, key, weixinUser.getWuId(), reqCode);
	}
	
	/**
	 * 删除群成员
	 * @Title: delete
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-14 下午4:30:41
	 */
	@RequestMapping(value="/weixin_user/delete",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage delete(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.DeleteUser;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		int ugId = StringUtil.parseInt(request.getParameter("ugId"));
		if(ugId<=0){
			return new ApiMessage(reqCode, ReqState.LackUgId);
		}
		//删除类型；1：家长；2：老师
		int type = StringUtil.parseInt(request.getParameter("type"));
		if(type!=1 && type!=2){
			return new ApiMessage(reqCode, ReqState.WrongType);
		}
		return weixinUserService.deleteUser(ugId, type, weixinUser.getWuId(), reqCode);
	}
	
	/**
	 * 批量删除群成员
	 * @Title: batchDelete
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-14 下午4:30:41
	 */
	@RequestMapping(value="/weixin_user/batchDelete",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage batchDelete(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.BatchDeleteGroupUser;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		if(StringUtil.isEmpty(openGId)){
			return new ApiMessage(reqCode, ReqState.LackOpenGId);
		}
		int[] ugIds = ParamUtil.getIntArray(request, "ugId");
		if(ugIds==null || ugIds.length<=0){
			return new ApiMessage(reqCode, ReqState.LackUgId);
		}
		return weixinUserService.batchDeleteUser(openGId, ugIds, weixinUser.getWuId(), reqCode);
	}
	
	/**
	 * 修改群成员（手机号）
	 * @Title: update
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-25 上午10:50:10
	 */
	@RequestMapping(value="/weixin_user/update",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage update(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.UpdateGroupUser;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		int ugId = StringUtil.parseInt(request.getParameter("ugId"));
		if(ugId<=0){
			return new ApiMessage(reqCode, ReqState.LackUgId);
		}
		String phone = StringUtil.trim(request.getParameter("phone"));
		if(StringUtil.isEmpty(phone)){
			return new ApiMessage(reqCode, ReqState.EmptyPhone);
		}
		if(!phone.matches(CoreVars.RULES_PHONE) && !phone.matches(CoreVars.RULES_MOBILE)){
			return new ApiMessage(reqCode, ReqState.WrongPhone);
		}
		return weixinUserService.updateUser(ugId, phone, weixinUser.getWuId(), reqCode);
	}
	
	/**
	 * 查询群成员数量
	 * @Title: getCnt
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-25 上午10:50:32
	 */
	@RequestMapping(value="/weixin_user/getCnt",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage getCnt(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.GetGroupUserCnt;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		//群id
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		if(StringUtil.isEmpty(openGId)){
			return new ApiMessage(reqCode, ReqState.LackOpenGId);
		}
		int type = StringUtil.parseInt(request.getParameter("type"));
		return weixinUserService.getGroupUserCnt(openGId, type, weixinUser.getWuId(), reqCode);
	}

}
