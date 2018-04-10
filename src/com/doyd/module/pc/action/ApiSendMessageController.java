package com.doyd.module.pc.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.doyd.httpclient.HttpServletUtil;
import org.doyd.utils.StringUtil;
import org.doyd.utils.Tools;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.cache.redis.WebsocketSessionRedis;
import com.doyd.core.action.common.MsgContext;
import com.doyd.msg.ApiMessage;

/**
 * 发送消息
 * @author wjs
 * @date 2017-7-5 
 *
 */
@Controller
public class ApiSendMessageController {
	
	
	@Autowired
	private WebsocketSessionRedis websocketSessionRedis;
	
	/**
	 * 发送消息
	 * @author wjs
	 * @date 2017-7-5 
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="msg/sendMessage",method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public MsgContext createQrcode(HttpServletRequest request,HttpServletResponse response){
		String key = request.getParameter("key");
		long time = StringUtil.parseLong(request.getParameter("time"));
		String tag = request.getParameter("tag");
		long curTime = System.currentTimeMillis();
		String message  = HttpServletUtil.getRequestInput(request);
		if((time +60*1000)>curTime && Tools.verify(tag, time+"",key) && StringUtil.isNotEmpty(message)){
			return  new MsgContext(websocketSessionRedis.sendRemoteMsg(key, message));
		}
		return new MsgContext(false);
	}
}
