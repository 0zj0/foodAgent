package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IChildrenService;
import com.doyd.core.action.common.ControllerContext;
import com.doyd.msg.ApiMessage;
import org.doyd.utils.StringUtil;

/**
 * @author ylb
 * @version 创建时间：2017-12-21 下午4:56:38
 */
@Controller
@RequestMapping(value="applet")
public class ChildrenController {

	@Autowired
	private IChildrenService childrenService;
	/**
	 * 添加孩子
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-21 下午6:23:47
	 */
	@RequestMapping(value="/children/add")
	@ResponseBody
	public ApiMessage addChild(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		String childName = request.getParameter("childName");
		String relation = request.getParameter("relation");
		String groupName = request.getParameter("groupName");
		int education = StringUtil.parseInt(request.getParameter("education"));
		int grade = StringUtil.parseInt(request.getParameter("grade"));
		String baseUrl = ControllerContext.getBasePath(request);
		return childrenService.addChild(baseUrl, openId, openGId, groupName, childName, relation, education, grade);
	}
	/**
	 * 添加孩子--多个群
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-13 上午10:56:58
	 */
	@RequestMapping(value="/children/batch/add")
	@ResponseBody
	public ApiMessage batchAddChild(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String relation = request.getParameter("relation");
		String childName = request.getParameter("childName");
		int education = StringUtil.parseInt(request.getParameter("education"));
		int grade = StringUtil.parseInt(request.getParameter("grade"));
		String g = request.getParameter("openGIds");
		String[] openGIds = null;
		if(StringUtil.isNotEmpty(g)){
			openGIds = g.split(",");
		}
		String baseUrl = ControllerContext.getBasePath(request);
		return childrenService.addChild(baseUrl, openId, childName, relation, education, grade, openGIds);
	}
	/**
	 * 修改孩子信息
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-21 下午7:34:50
	 */
	@RequestMapping(value="/children/update")
	@ResponseBody
	public ApiMessage updateChild(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		int childId = StringUtil.parseInt(request.getParameter("childId"));
		String childName = request.getParameter("childName");
		String relation = request.getParameter("relation");
		return childrenService.updateChild(openId, childId, childName, relation);
	}
}
