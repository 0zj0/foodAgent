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

import com.doyd.biz.IAchievementService;
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
public class PCAchievementController {
	
	@Autowired
	private IAchievementService achievementService;
	@Autowired
	private IWeixinUserService weixinUserService;
	
	/**
	 * 分页查询成绩单
	 * @Title: queryAchievement
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午2:11:27
	 */
	@RequestMapping(value="/achievement/query/page",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage queryAchievement(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.GetAchievementList;
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
		return achievementService.queryAchievementByPage(page, openGId, weixinUser.getWuId(), key, type, reqCode);
	}
	
	/**
	 * 新建成绩单
	 * @Title: createAchievement
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午6:35:00
	 */
	@RequestMapping(value="/achievement/create",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage createAchievement(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.CreateAchievement;
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
			return achievementService.createAchievement(weixinUser, openGId, remark, file, reqCode);
		}catch (Exception e) {
			e.printStackTrace();
			return new ApiMessage(reqCode, ReqState.Failure);
		}
	}
	
	/**
	 * 删除/批量删除成绩单
	 * @Title: deleteAchievement
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午3:37:11
	 */
	@RequestMapping(value="/achievement/delete",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage deleteAchievement(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.DeleteAchievement;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		if(StringUtil.isEmpty(openGId)){
			return new ApiMessage(reqCode, ReqState.LackOpenGId);
		}
		int[] scoreIds = ParamUtil.getIntArray(request, "scoreId");
		if(scoreIds==null || scoreIds.length<=0){
			return new ApiMessage(reqCode, ReqState.LackScoreId);
		}
		return achievementService.deleteAchievement(weixinUser, openGId, scoreIds, reqCode);
	}
	
	/**
	 * 修改成绩单（备注）
	 * @Title: updateAchievement
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-28 下午8:46:12
	 */
	@RequestMapping(value="/achievement/update",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage updateShare(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.UpdateAchievement;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String remark = StringUtil.trim(request.getParameter("remark"));
		if(StringUtil.isEmpty(remark)){
			return new ApiMessage(reqCode, ReqState.EmptyRemark);
		}
		int scoreId = StringUtil.parseInt(request.getParameter("scoreId"));
		if(scoreId<=0){
			return new ApiMessage(reqCode, ReqState.LackScoreId);
		}
		return achievementService.updateAchievement(weixinUser, scoreId, remark, reqCode);
	}
	
	/**
	 * 查询成绩单数量
	 * @Title: getAchievementCnt
	 * @param request
	 * @param response
	 * @return ApiMessage
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-27 下午4:02:09
	 */
	@RequestMapping(value="/achievement/getCnt",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage getAchievementCnt(HttpServletRequest request,HttpServletResponse response){
		ReqCode reqCode = ReqCode.GetAchievementCnt;
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(reqCode, ReqState.NoExistUser);
		}
		String openGId = StringUtil.trim(request.getParameter("openGId"));
		if(StringUtil.isEmpty(openGId)){
			return new ApiMessage(reqCode, ReqState.LackOpenGId);
		}
		int type = StringUtil.parseInt(request.getParameter("type"));
		return achievementService.getAchievementCnt(weixinUser, openGId, type, reqCode);
	}

}
