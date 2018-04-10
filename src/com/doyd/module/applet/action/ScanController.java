package com.doyd.module.applet.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.doyd.httpclient.HttpClientUtil;
import org.doyd.utils.StringUtil;
import org.doyd.utils.Tools;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IFilesService;
import com.doyd.biz.IWeixinUserService;
import com.doyd.cache.memory.SysCache;
import com.doyd.core.action.common.ControllerContext;
import com.doyd.core.convertor.AppConfig;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import com.doyd.netty.cache.WebSocketCache;
import com.doyd.server.util.SignUtil;

/**
 * @author ylb
 * @version 创建时间：2018-1-4 上午11:07:04
 */
@Controller
@RequestMapping(value = {"applet"})
public class ScanController {
	
	@Autowired
	private IWeixinUserService weixinUserService;
	@Autowired
	private IFilesService filesService;
	@Autowired
	private WebSocketCache webSocketCache;

	@RequestMapping(value="/scan")
	@ResponseBody
	public ApiMessage scan(HttpServletRequest request){
		/**
		 * 1、扫一扫发送至电脑、作业         sendWork
		 * 2、扫一扫发送至电脑、班务         sendNotify
		 * 3、扫一扫发送至电脑、成绩单     sendAchie
		 * 4、扫一扫发送至电脑、共享文件 sendShare
		 * 5、仅登录                                          
		 * 6、共享文件登录                              shareLogin
		 * 7、成绩单登录                                  achieLogin
		 */
		String baseUrl = ControllerContext.getBasePath(request);
		String pcUrl = baseUrl + "api/login";
		String info1 = request.getParameter("i1");
		String info2 = request.getParameter("i2");
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		String scanType = request.getParameter("scanType");
		int id = StringUtil.parseInt(request.getParameter("id"));
		//判断参数
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(info1) 
				|| StringUtil.isEmpty(info2)){
			return new ApiMessage(ReqCode.Scan, ReqState.ApiParamError);
		}
		int type = 1;
		if("sendWork".equals(scanType) || "sendNotify".equals(scanType)
				|| "sendAchie".equals(scanType) || "sendShare".equals(scanType)){
			type = 2;
		}else if("shareLogin".equals(scanType)){
			type = 40;
		}else if("achieLogin".equals(scanType)){
			type = 30;
		}
		if(type >= 10 && StringUtil.isEmpty(openGId)){
			return new ApiMessage(ReqCode.Scan, ReqState.ApiParamError);
		}else if(type == 2 && id <= 0){
			return new ApiMessage(ReqCode.Scan, ReqState.ApiParamError);
		}
		AppConfig config = SysCache.getCache().getAppConfig();
		String secret = config.getPcLoginSecret();
		//sessionKey非法，返回错误
		if(Tools.verify(info1, secret, info2)){
			return new ApiMessage(ReqCode.Scan, ReqState.ApiParamError);
		}
		//判断用户
		WeixinUser wu = weixinUserService.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.Scan, ReqState.NoExistUser);
		}
		try{
			//返回结果
			JSONObject resultMap = new JSONObject();
			//进行签名
			Map<String, String> map = new HashMap<String, String>();
			long now = System.currentTimeMillis();
			map.put("timestamp", now+"");
			String sign = SignUtil.sign(map, config.getPcLoginSecret());
			resultMap.put("timestamp", now);
			resultMap.put("sign", sign);
			resultMap.put("sessionKey", info1);
			resultMap.put("openId", openId);
			resultMap.put("state", 1);
			resultMap.put("type", type);
			//判断时间戳
			long timestamp = StringUtil.parseLong(info2);
			//时间戳 小于 当前时间的半个小时前
			if(timestamp < System.currentTimeMillis() - 30 * 60 * 1000){
				resultMap.put("state", 3);
				String resultStr = HttpClientUtil.postJson(pcUrl, resultMap);
				if(StringUtil.isEmpty(resultStr)){
					webSocketCache.set(info1, resultMap.toString(), pcUrl, 1);
				}
				return new ApiMessage(ReqCode.Scan, ReqState.QrcodeTimeOut);
			}
			//如果不是下载，则返回
			if(type != 2){
				if(!wu.isDirector() && !wu.isTeacher()){
					return new ApiMessage(ReqCode.Scan, ReqState.NoPower);
				}
				String resultStr = HttpClientUtil.postJson(pcUrl, resultMap);
				if(StringUtil.isEmpty(resultStr)){
					webSocketCache.set(info1, resultMap.toString(), pcUrl, 1);
				}
				return new ApiMessage(ReqCode.Scan, ReqState.Success);
			}
			String storageTable = null;
			if("sendWork".equals(scanType)){
				storageTable = "homework";
			}else if("sendNotify".equals(scanType)){
				storageTable = "class_notify";
			}else if("sendAchie".equals(scanType)){
				storageTable = "achievement";
			}else if("sendShare".equals(scanType)){
				storageTable = "shareFiles";
			}
			JSONArray fileList = new JSONArray();
			List<Map<String, Object>> files = filesService.getFiles("files", id, storageTable);
			if(files != null && files.size() > 0){
				for (Map<String, Object> map2 : files) {
					String fileAddr = (String) map2.get("fileAddr");
					int fileId = StringUtil.parseIntByObj(map2.get("fileId"));
					if(StringUtil.isNotEmpty(fileAddr)){
						JSONObject file = new JSONObject();
						file.put("fileAddr", fileAddr+"&download=true");
						file.put("fileId", fileId);
						fileList.put(file);
					}
				}
			}
			resultMap.put("files", fileList);
			String resultStr = HttpClientUtil.postJson(pcUrl, resultMap);
			if(StringUtil.isEmpty(resultStr)){
				webSocketCache.set(info1, resultMap.toString(), pcUrl, 1);
			}
			return new ApiMessage(ReqCode.Scan, ReqState.Success);
		}catch(Exception e){
			return new ApiMessage(ReqCode.Scan, ReqState.Failure);
		}
	}
	
	@RequestMapping(value="/scan/confirm")
	@ResponseBody
	public ApiMessage confirm(HttpServletRequest request){
		String info1 = request.getParameter("i1");
		String info2 = request.getParameter("i2");
		String openId = (String) request.getAttribute("openId");
		String openGId = request.getParameter("openGId");
		int result = StringUtil.parseInt(request.getParameter("result"));// 1、同意  2、取消
		String scanType = request.getParameter("scanType");
		if(StringUtil.isEmpty(info1) || StringUtil.isEmpty(info2) 
				|| StringUtil.isEmpty(openId) || result < 1 || result > 2 
				|| (StringUtil.isNotEmpty(scanType) && 
						!"shareLogin".equals(scanType) && !"achieLogin".equals(scanType))){
			return new ApiMessage(ReqCode.ScanConfirm, ReqState.ApiParamError);
		}
		if(StringUtil.isNotEmpty(scanType) && StringUtil.isEmpty(openGId)){
			return new ApiMessage(ReqCode.ScanConfirm, ReqState.ApiParamError);
		}
		String baseUrl = ControllerContext.getBasePath(request);
		String pcUrl = baseUrl + "api/login";
		int type = 1;
		if("shareLogin".equals(scanType)){
			type = 40;
		}else if("achieLogin".equals(scanType)){
			type = 30;
		}
		AppConfig config = SysCache.getCache().getAppConfig();
		String secret = config.getPcLoginSecret();
		//sessionKey非法，返回错误
		if(Tools.verify(info1, secret, info2)){
			return new ApiMessage(ReqCode.ScanConfirm, ReqState.ApiParamError);
		}
		//判断用户
		WeixinUser wu = weixinUserService.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.ScanConfirm, ReqState.NoExistUser);
		}
		if(!wu.isDirector() && !wu.isTeacher()){
			return new ApiMessage(ReqCode.Scan, ReqState.NoPower);
		}
		try{
			//返回结果
			JSONObject resultMap = new JSONObject();
			//进行签名
			Map<String, String> map = new HashMap<String, String>();
			long now = System.currentTimeMillis();
			map.put("timestamp", now+"");
			String sign = SignUtil.sign(map, config.getPcLoginSecret());
			resultMap.put("timestamp", now);
			resultMap.put("sign", sign);
			resultMap.put("sessionKey", info1);
			resultMap.put("openId", openId);
			resultMap.put("openGId", openGId);
			resultMap.put("state", result == 1 ? 2 : 4);
			resultMap.put("type", type);
			//判断时间戳
			long timestamp = StringUtil.parseLong(info2);
			//时间戳 小于 当前时间的半个小时前
			if(timestamp < System.currentTimeMillis() - 30 * 60 * 1000){
				resultMap.put("state", 3);
				String resultStr = HttpClientUtil.postJson(pcUrl, resultMap);
				if(StringUtil.isEmpty(resultStr)){
					webSocketCache.set(info1, resultMap.toString(), pcUrl, 1);
				}
				return new ApiMessage(ReqCode.ScanConfirm, ReqState.QrcodeTimeOut);
			}
			String resultStr = HttpClientUtil.postJson(pcUrl, resultMap);
			if(StringUtil.isEmpty(resultStr)){
				webSocketCache.set(info1, resultMap.toString(), pcUrl, 1);
			}
			return new ApiMessage(ReqCode.ScanConfirm, ReqState.Success);
		}catch(Exception e){
			return new ApiMessage(ReqCode.ScanConfirm, ReqState.Failure);
		}
	}
}
