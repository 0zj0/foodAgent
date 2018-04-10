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

import com.doyd.biz.IHomeworkService;
import com.doyd.biz.IWeixinUserService;
import com.doyd.core.CoreVars;
import com.doyd.core.action.common.Page;
import com.doyd.model.WeixinUser;
import com.doyd.module.pc.utils.ParamUtil;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import com.doyd.util.HttpclientUtil;
import org.doyd.utils.DateUtil;
import org.doyd.utils.StringUtil;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = {"pc"})
public class PCWorkController {
	
	@Autowired
	private IHomeworkService homeworkService;
	@Autowired
	private IWeixinUserService weixinUserService;
	
	/**
	 * 分页查询作业
	 * @Title: queryWork
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午2:11:27
	 */
	@RequestMapping(value="/work/query/page",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage queryWork(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.GetWorkList;
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
		//搜索条件：可根据发布人（作业内容）搜索
		String key = StringUtil.trim(request.getParameter("key"));
		//发布起始日期
		String beginDate = StringUtil.trim(request.getParameter("beginDate"));
		//发布结束日期
		String endDate = StringUtil.trim(request.getParameter("endDate"));
		Page page = new Page(request);
		return homeworkService.queryWorkByPage(page, openGId, type, weixinUser.getWuId(), key, beginDate, endDate, reqCode);
	}
	
	/**
	 * 显示作业详情
	 * @Title: showWork
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午5:46:48
	 */
	@RequestMapping(value="/work/show",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage showWork(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.ShowWork;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		int workId = StringUtil.parseInt(request.getParameter("workId"));
		if(workId<=0){
			return new ApiMessage(reqCode, ReqState.LackWorkId);
		}
		return homeworkService.showWork(workId, weixinUser, reqCode);
	}
	
	/**
	 * 新建作业
	 * @Title: createWork
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午6:35:00
	 */
	@RequestMapping(value="/work/create",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage createWork(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.CreateWork;
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
			String subject = json.getString("subject");
			String submitTime = json.getString("submitTime");
			String work = json.getString("work");
			if(StringUtil.isEmpty(openGId)){
				return new ApiMessage(reqCode, ReqState.LackOpenGId);
			}
			if(StringUtil.isEmpty(subject)){
				return new ApiMessage(reqCode, ReqState.EmptySubject);
			}
			if(StringUtil.isEmpty(submitTime)){
				return new ApiMessage(reqCode, ReqState.EmptySubmitTime);
			}
			if(!submitTime.matches(CoreVars.RULES_DATA)){
				return new ApiMessage(reqCode, ReqState.WrongTime);
			}
			if(DateUtil.before(submitTime + " 00:00:00", DateUtil.today() + " 00:00:00")){
				return new ApiMessage(reqCode, ReqState.SubmitTimeBeforeToday);
			}
			if(StringUtil.isEmpty(work)){
				return new ApiMessage(reqCode, ReqState.EmptyWork);
			}
			if(work.length() > 300){
				return new ApiMessage(reqCode, ReqState.OutOfContent);
			}
				
			int costTime = json.getInt("costTime");
			JSONArray files = json.getJSONArray("files");
			return homeworkService.createWork(weixinUser, openGId, subject, submitTime, work, costTime, files, reqCode);
		}catch (Exception e) {
			e.printStackTrace();
			return new ApiMessage(reqCode, ReqState.Failure);
		}
	}
	
	/**
	 * 删除/批量删除作业
	 * @Title: deleteWork
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午3:37:11
	 */
	@RequestMapping(value="/work/delete",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage deleteWork(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.DeleteWork;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		if(StringUtil.isEmpty(openGId)){
			return new ApiMessage(reqCode, ReqState.LackOpenGId);
		}
		int[] workIds = ParamUtil.getIntArray(request, "workId");
		if(workIds==null || workIds.length<=0){
			return new ApiMessage(reqCode, ReqState.LackWorkId);
		}
		return homeworkService.deleteWork(weixinUser, openGId, workIds, reqCode);
	}
	
	/**
	 * 查询作业数量
	 * @Title: getWorkCnt
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午4:02:09
	 */
	@RequestMapping(value="/work/getCnt",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage getWorkCnt(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.GetWorkCnt;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		if(StringUtil.isEmpty(openGId)){
			return new ApiMessage(reqCode, ReqState.LackOpenGId);
		}
		int type = StringUtil.parseInt(request.getParameter("type"));
		return homeworkService.getWorkCnt(weixinUser, openGId, type, reqCode);
	}

}
