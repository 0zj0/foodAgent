package com.doyd.module.applet.action;

import javax.servlet.http.HttpServletRequest;

import org.doyd.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IUserGroupService;
import com.doyd.core.action.common.ControllerContext;
import com.doyd.msg.ApiMessage;

/**
 * @author ylb
 * @version 创建时间：2017-12-18 上午10:10:04
 */
@Controller
@RequestMapping(value = {"applet"})
public class UserGroupController {
	
	@Autowired
	private IUserGroupService userGroupService;

	/**
	 * 获取用户身份信息
	 * 
	 * @param request 
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-20 上午9:51:52
	 */
	@RequestMapping(value="/identity")
	@ResponseBody
	public ApiMessage identity(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		return userGroupService.identity(openId, openGId);
	}
	/**
	 * 添加用户身份
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-20 下午7:22:11
	 */
	@RequestMapping(value="/identity/add")
	@ResponseBody
	public ApiMessage addIdentity(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		String groupName = request.getParameter("groupName");
		String childName = request.getParameter("childName");
		int education = StringUtil.parseInt(request.getParameter("education"));
		int grade = StringUtil.parseInt(request.getParameter("grade"));
		String identity = request.getParameter("identities");
		String[] identities = null;
		if(StringUtil.isNotEmpty(identity)){
			identities = identity.split(",");
		}
		String realName = request.getParameter("realName");
		String relation = request.getParameter("relation");
		String baseUrl = ControllerContext.getBasePath(request);
		return userGroupService.addIdentity(baseUrl, openId, openGId, groupName, identities, childName, education, grade, realName, relation);
	}
	/**
	 * 获得首页信息
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-25 上午9:35:26
	 */
	@RequestMapping(value="/home")
	@ResponseBody
	public ApiMessage home(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		return userGroupService.home(openId, openGId);
	}
	/**
	 * 获得用户群信息
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-7 下午3:17:23
	 */
	@RequestMapping(value="/user/group")
	@ResponseBody
	public ApiMessage getUserGroup(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String identity = request.getParameter("identity");
		int childId = StringUtil.parseInt(request.getParameter("childId"));
		return userGroupService.getUserGroup(openId, identity, childId);
	}
	/**
	 * 添加群关系---添加班级
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-25 下午4:28:40
	 */
	@RequestMapping(value="/user/group/add")
	@ResponseBody
	public ApiMessage addUserGroup(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		String groupName = request.getParameter("groupName");
		String identity = request.getParameter("identity");
		int childId = StringUtil.parseInt(request.getParameter("childId"));
		String baseUrl = ControllerContext.getBasePath(request);
		return userGroupService.addUserGroup(baseUrl, openId, openGId, groupName, identity, childId);
	}
	/**
	 * 添加群关系---添加班级
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-25 下午4:28:40
	 */
	@RequestMapping(value="/user/group/batch/add")
	@ResponseBody
	public ApiMessage batchAddUserGroup(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String g = request.getParameter("openGIds");
		String [] openGIds = null;
		if(StringUtil.isNotEmpty(g)){
			openGIds = g.split(",");
		}
		String identity = request.getParameter("identity");
		int childId = StringUtil.parseInt(request.getParameter("childId"));
		String baseUrl = ControllerContext.getBasePath(request);
		return userGroupService.batchAddUserGroup(baseUrl, openId, identity, openGIds, childId);
	}
	/**
	 * 解绑群关系
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-25 下午4:31:43
	 */
	@RequestMapping(value="/user/group/unbind")
	@ResponseBody
	public ApiMessage unbind(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		String identity = request.getParameter("identity");
		int childId = StringUtil.parseInt(request.getParameter("childId"));
		return userGroupService.unbindUserGroup(openId, openGId, identity, childId);
	}
	/**
	 * 获得任教科目
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-27 下午5:28:17
	 */
	@RequestMapping(value="/subjects")
	@ResponseBody
	public ApiMessage subjects(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		return userGroupService.getSubjects(openId, openGId);
	}
	/**
	 * 修改任教科目
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-27 下午5:26:09
	 */
	@RequestMapping(value="/subjects/update")
	@ResponseBody
	public ApiMessage updateSubjects(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		String subject = request.getParameter("subjects");
		String[] subjects = null;
		if(StringUtil.isNotEmpty(subject)){
			subjects = subject.split(",");
		}
		return userGroupService.updateSubjects(openId, openGId, subjects);
	}
	/**
	 * 获得群用户列表
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-20 下午7:21:42
	 */
	@RequestMapping(value="/group/user")
	@ResponseBody
	public ApiMessage getGroupUser(HttpServletRequest request){
		String openGId = request.getParameter("openGId");
		String openId = (String) request.getAttribute("openId");
		String key = request.getParameter("key");
		return userGroupService.getGroupUser(openGId, openId, key);
	}
	/**
	 * 修改群成员电话
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-2 上午9:51:15
	 */
	@RequestMapping(value="/user/phone/update")
	@ResponseBody
	public ApiMessage updatePhone(HttpServletRequest request){
		String openGId = request.getParameter("openGId");
		String phone = request.getParameter("phone");
		String pOpenId = request.getParameter("pOpenId");
		String openId = (String) request.getAttribute("openId");
		return userGroupService.updatePhone(openId, openGId, phone, pOpenId);
	}
	/**
	 * 删除群成员
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-2 上午9:51:15
	 */
	@RequestMapping(value="/user/group/delete")
	@ResponseBody
	public ApiMessage deleteUg(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		String t = request.getParameter("teacher");
		String[] teacher = null;
		if(StringUtil.isNotEmpty(t)){
			teacher = t.split(",");
		}
		String p = request.getParameter("patriarch");
		String[] patriarch = null;
		if(StringUtil.isNotEmpty(p)){
			patriarch = p.split(",");
		}
		return userGroupService.deleteUg(openId, openGId, teacher, patriarch);
	}
	/**
	 * 发起完善通知
	 * 
	 * @param request
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2018-1-3 下午4:04:39
	 */
	@RequestMapping(value="/perfect/add")
	@ResponseBody
	public ApiMessage addPerfect(HttpServletRequest request){
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		return userGroupService.addPerfect(openId, openGId);
	}
}
