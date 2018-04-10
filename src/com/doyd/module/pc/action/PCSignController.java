/**
 * 
 */
package com.doyd.module.pc.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.cache.memory.SysCache;
import com.doyd.core.action.common.ControllerContext;
import com.doyd.module.pc.ticket.PcLoginSignTicketServer;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import com.doyd.server.util.SignUtil;
import org.doyd.utils.StringUtil;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = {"pc"})
public class PCSignController {
	
	@Autowired
	private PcLoginSignTicketServer pcLoginSignTicketServer;
	
	/**
	 * 获取签名
	 * @Title: sign
	 * @param request
	 * @param response
	 * @return MsgContext
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-14 上午11:24:51
	 */
	@RequestMapping(value="/sign",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public ApiMessage sign(HttpServletRequest request,HttpServletResponse response){
		String ip = ControllerContext.getIp(request);
		String userAgent = request.getHeader("user-agent");
		Map<String, String> params = new HashMap<String, String>();
		params.put("ip", ip);
		params.put("userAgent", userAgent);
		String sign = SignUtil.sign(params, SysCache.getCache().getAppConfig().getPcLoginSecret());
		if(StringUtil.isEmpty(sign)){
			return new ApiMessage(ReqCode.Sign, ReqState.Failure);
		}
		pcLoginSignTicketServer.addTicket(request, response, sign);
		return new ApiMessage(ReqCode.Sign, ReqState.Success).setInfo(sign);
	}

}
