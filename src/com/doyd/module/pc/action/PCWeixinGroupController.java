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

import com.doyd.biz.IWeixinGroupService;
import com.doyd.biz.IWeixinUserService;
import com.doyd.core.action.common.Page;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = {"pc"})
public class PCWeixinGroupController {
	
	@Autowired
	private IWeixinGroupService weixinGroupService;
	
	/**
	 * 查询所有与用户有关系的微信群接口，只返回所有群信息
	 * @Title: queryGroup
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-15 上午10:05:48
	 */
	@RequestMapping(value="/weixin_group/query/all",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage queryAll(HttpServletRequest request,HttpServletResponse response){
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		ReqCode reqCode = ReqCode.GetWeixinGroupList;
		return weixinGroupService.queryWeixinGroup(weixinUser, reqCode);
	}
	
	/**
	 * 分页查询与用户有关系的微信群接口，将返回群信息、用户与群关系、用户所带科目、作业数量、班务通知数量、成绩单数量、请假审批数量、共享文件数量、班成员数量等等
	 * @Title: queryPage
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-15 上午10:07:21
	 */
	@RequestMapping(value="/weixin_group/query/page",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage queryPage(HttpServletRequest request,HttpServletResponse response){
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		ReqCode reqCode = ReqCode.GetWeixinGroupList;
		Page page = new Page(request);
		return weixinGroupService.queryWeixinGroupByPage(page, weixinUser, reqCode);
	}

}
