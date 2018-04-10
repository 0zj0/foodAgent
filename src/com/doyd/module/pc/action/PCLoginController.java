/**
 * 
 */
package com.doyd.module.pc.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IUserPcLoginService;
import com.doyd.cache.memory.SysCache;
import com.doyd.cache.redis.OauthRedis;
import com.doyd.model.UserPcLogin;
import com.doyd.model.WeixinUser;
import com.doyd.module.pc.model.Oauth;
import com.doyd.module.pc.ticket.SessionKeyTicketServer;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import com.doyd.server.util.SignUtil;
import com.doyd.util.QRCodeUtil;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = {"pc"})
public class PCLoginController {
	
	@Autowired
	private IUserPcLoginService userPcLoginService;
	@Autowired
	private SessionKeyTicketServer sessionKeyTicketServer;
	@Autowired
	private OauthRedis oauthRedis;
	
	@RequestMapping(value="/login",method={RequestMethod.POST})
	@ResponseBody
	public ApiMessage login(HttpServletRequest request,HttpServletResponse response){
		WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser != null){
			String realName = null;
			try {
				realName = URLEncoder.encode(weixinUser.getRealName(), "utf-8");
			} catch (UnsupportedEncodingException e) {
			}
			String contextPath = request.getContextPath();
			Cookie cookie = new Cookie("realName", realName);
			cookie.setPath(contextPath);
			cookie.setMaxAge(-1);
			response.addCookie(cookie);
			cookie = new Cookie("avatarUrl", weixinUser.getAvatarUrl());
			cookie.setPath(contextPath);
			cookie.setMaxAge(-1);
			response.addCookie(cookie);
		}
		return new ApiMessage(ReqCode.Login, ReqState.Success);
	}
	
	@RequestMapping(value="/login/qrcode",method={RequestMethod.GET,RequestMethod.POST})
	public void getQrcode(HttpServletRequest request,HttpServletResponse response){
		Long timestamp = System.currentTimeMillis();//时间戳
		Map<String,String> params = new HashMap<String, String>();
		params.put("timestamp", timestamp+"");
		String sessionKey = SignUtil.sign(params, SysCache.getCache().getAppConfig().getPcLoginSecret());
		System.out.println(sessionKey);
		sessionKeyTicketServer.addTicket(request, response, sessionKey);
		String url = SysCache.getCache().getAppConfig().getAppletDomain() + "/" + SysCache.getCache().getAppConfig().getAppletPath();
		url += "?i1="+sessionKey+"&t=pc&i2="+timestamp;
		QRCodeUtil.zxingCodeCreate(url, 300, 300, response, "jpg");
	}
	
	@RequestMapping(value="/logout",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage logout(HttpServletRequest request,HttpServletResponse response){
		/*WeixinUser weixinUser = (WeixinUser)request.getAttribute("weixinUser");
		if(weixinUser==null){
			return new ApiMessage(ReqCode.Logout, ReqState.NoExistUser);
		}*/
		
		String sessionKey = sessionKeyTicketServer.getUser(request, response);
		String openId =	oauthRedis.getOpenId(sessionKey);
		Oauth oauth = oauthRedis.get(openId);
		if(oauth!=null){
			UserPcLogin record = userPcLoginService.getLatestRecord(oauth.getWeixinUser().getWuId());
			if(record!=null){
				userPcLoginService.logout(record);
			}
			oauthRedis.removeOauth(sessionKey);
			sessionKeyTicketServer.deleteTicket(request, response, sessionKey);
			Cookie[] cookies = request.getCookies();
			if(cookies != null && cookies.length > 0){
				String contextPath = request.getContextPath();
				for(Cookie cookie: cookies){
					if(cookie.getName().equals("realName")){
						cookie.setMaxAge(0);
						cookie.setPath(contextPath);
						response.addCookie(cookie);
					}
					if(cookie.getName().equals("avatarUrl")){
						cookie.setMaxAge(0);
						cookie.setPath(contextPath);
						response.addCookie(cookie);
					}
				}
			}
		}
		//weixinUserTicketServer.deleteTicket(request, response, weixinUser);
		return new ApiMessage(ReqCode.Logout, ReqState.Success);
	}

}
