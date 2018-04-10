package com.doyd.core.interceptor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.doyd.utils.StringUtil;
import org.doyd.utils.WeiXinSign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.doyd.biz.IUserGroupService;
import com.doyd.cache.memory.SysCache;
import com.doyd.core.CoreVars;
import com.doyd.core.action.common.ControllerContext;
import com.doyd.module.pc.ticket.PcLoginSignTicketServer;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import com.doyd.server.util.SignUtil;

/**
 * 签名拦截器
 * @author xieqing
 *
 */
public class SignInterceptor  implements HandlerInterceptor {
	
	@Autowired
	private PcLoginSignTicketServer pcLoginSignTicketServer;
	@Autowired
	private IUserGroupService userGroupService;
	
	private String newUseInterceptPath = "applet/(subjects|leave|homework|homework_notify|classnitify|share|transfer|achievement|election)(/\\w*)*";
	
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o) throws Exception {
    	String userAgent = request.getHeader("User-Agent");
		String uri = request.getServletPath();
		if (uri != null && uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		if (uri.matches(CoreVars.ANONYMOUS_ACCESS_PATH)) {
			return true;
		}
    	if(uri.startsWith("applet")){
       	 	//秘钥
            String appletCustomerSecret = SysCache.getCache().getAppConfig().getAppletCustomerSecret();
            Enumeration<String> a = request.getParameterNames();
            Map<String, Object> paramMap = new HashMap<String, Object>();
            String paramName;
            while(a.hasMoreElements()){
                paramName = a.nextElement();
                paramMap.put(paramName, request.getParameter(paramName));
            }
            paramMap.remove("sign"); //移除签名参数
            System.out.println(paramMap);
            //获取签名
            String sign = WeiXinSign.sign(paramMap, appletCustomerSecret);
            String customerSign = request.getParameter("sign");
            if(!sign.equalsIgnoreCase(customerSign)){
                //签名错误，非法访问
                ControllerContext.print(httpServletResponse,new ApiMessage(ReqCode.Common,ReqState.SignatureFailed).toString(), "text/html;charset=utf-8");
                return false;
            }
            /*long timestamp = StringUtil.parseLong(request.getParameter("timestamp"));
            if(timestamp <= 0 || timestamp < System.currentTimeMillis()-5*60*1000){
                //时间戳非法，或超时
                ControllerContext.print(httpServletResponse,new ApiMessage(ReqCode.Common,ReqState.SignatureFailed).toString(), "text/html;charset=utf-8");
                return false;
            }*/
            String openId = request.getParameter("openId");
            String openGId = request.getParameter("openGId");
            if(uri.matches(newUseInterceptPath) && StringUtil.isNotEmpty(openGId) && StringUtil.isNotEmpty(openId)){
            	userGroupService.newUse(openId, openGId);
            }
            request.setAttribute("openId", openId);
    	}else{
    		String sign = pcLoginSignTicketServer.getUser(request, httpServletResponse);
    		boolean r = true;
    		ApiMessage msg = null;
    		if(StringUtil.isEmpty(sign)){
    			r = false;
    			msg = new ApiMessage(ReqCode.Sign, ReqState.SignTimeOut);
    		}else{
    			String ip = ControllerContext.getIp(request);
    			Map<String, String> params = new HashMap<String, String>();
    			params.put("ip", ip);
    			params.put("userAgent", userAgent);
    			if(!sign.equalsIgnoreCase(SignUtil.sign(params, SysCache.getCache().getAppConfig().getPcLoginSecret()))){
    				r = false;
    				msg = new ApiMessage(ReqCode.Sign, ReqState.SignatureFailed);//中文会乱码
    			}
    		}
    		if(!r){
    			ControllerContext.print(httpServletResponse, msg.toString(), "text/html;charset=utf-8");
    			return r;
    		}
    		return r;
    	}
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
