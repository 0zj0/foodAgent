/**
 * 
 */
package com.doyd.module.pc.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.doyd.ip.IP;
import org.doyd.ip.IpUtil;
import org.doyd.utils.DateUtil;
import org.doyd.utils.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IDownRecordService;
import com.doyd.biz.IUserPcLoginService;
import com.doyd.biz.IWeixinUserService;
import com.doyd.cache.memory.SysCache;
import com.doyd.cache.redis.OauthRedis;
import com.doyd.cache.redis.WebsocketSessionRedis;
import com.doyd.model.DownRecord;
import com.doyd.model.SocketRedis;
import com.doyd.model.UserPcLogin;
import com.doyd.model.WeixinUser;
import com.doyd.module.pc.model.Oauth;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import com.doyd.server.util.SignUtil;
import com.doyd.util.HttpclientUtil;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = {"api"})
public class ApiController {
	
	private static Logger logger = Logger.getLogger(ApiController.class);
	
	@Autowired
	private IWeixinUserService weixinUserService;
	@Autowired
	private WebsocketSessionRedis websocketSessionRedis;
	@Autowired
	private OauthRedis oauthRedis;
	@Autowired
	private IDownRecordService downRecordService;
	@Autowired
	private IUserPcLoginService userPcLoginService;
	
	@RequestMapping(value="/login",method={RequestMethod.POST})
	@ResponseBody
	public ApiMessage login(HttpServletRequest request,HttpServletResponse response){
		//{"sessionKey":"10cf6f292f692e25bb1f49c666a5ee20","openId":"oV6n_0BZDOq6xC8L2YviGe-jZ5uI","sign":"4ea6b71ada196ac771c9efeceeb24d6e","files":["http://test.wbgj.cn/papers/file/private/forever1/c4ca4238a0b923820dcc509a6f75849b/20180207/9c9bc4eab0d24461abbdaaaaee59d0db.xls?appId=c4ca4238a0b923820dcc509a6f75849b&sign=3e9f365f61f24a12d3f45610b4d2f632&timestamp=1518060940609&code=he4J04NY0nS0"],"state":1,"type":2,"timestamp":1518060940604}
		logger.debug("进入登录接口页");
		try{
			//获取请求数据
			String requestBody = HttpclientUtil.getRequestInput(request, null);
			logger.debug("requestBody:"+requestBody);
			if(StringUtil.isEmpty(requestBody)) {
				return new ApiMessage(ReqCode.Login, ReqState.NoRequestData);
			}
			JSONObject json = new JSONObject(requestBody);
			long timestamp = json.getLong("timestamp");
			Map<String, String> params = new HashMap<String, String>();
			params.put("timestamp", timestamp+"");
			String sign = json.getString("sign");
			if(StringUtil.isEmpty(sign) || !sign.equals(SignUtil.sign(params, SysCache.getCache().getAppConfig().getPcLoginSecret()))){
				return new ApiMessage(ReqCode.Login, ReqState.SignatureFailed);
			}
			String sessionKey = json.getString("sessionKey");
			String openId = json.getString("openId");
			//状态：1：已扫描；2：确认登录；3：二维码已过期
			int state = json.getInt("state");
			//登录类型：1：仅登录；2：登录并下载；10：登录并布置作业；20：登录并发布班务通知；30：登录并查看成绩单；40：登录并查看共享文件；
			int type = json.getInt("type");
			String openGId = json.getString("openGId");
			JSONArray files = json.getJSONArray("files");
			logger.debug("判断参数");
			if(StringUtil.isEmpty(sessionKey) || StringUtil.isEmpty(openId) || state<=0 
					|| type<=0 || (type==2 && (files==null || files.length()<=0))
					|| (type>=10 && StringUtil.isEmpty(openGId))){
				return new ApiMessage(ReqCode.Login, ReqState.ApiParamError);
			}
			logger.debug("sessionKey指向不同的用户说明传输了错误的信息");
			//sessionKey指向不同的用户说明传输了错误的信息
			if(StringUtil.isNotEmpty(oauthRedis.getOpenId(sessionKey)) && !openId.equals(oauthRedis.getOpenId(sessionKey))){
				return new ApiMessage(ReqCode.Login, ReqState.WrongUser);
			}
			logger.debug("获得用户信息");
			WeixinUser weixinUser = weixinUserService.getUser(openId);
			if(weixinUser==null){
				return new ApiMessage(ReqCode.Login, ReqState.NoExistUser);
			}
			
			SocketRedis sr = websocketSessionRedis.getSocketRedis(sessionKey);
			logger.debug("获得session");
			if(sr==null){
				return new ApiMessage(ReqCode.Login, ReqState.SessionTimeOut);
			}
			logger.debug("state参数："+state);
			if(state==3){
				websocketSessionRedis.sendMsg(sessionKey, new ApiMessage(type==2?ReqCode.DownLoad:ReqCode.Login, ReqState.QrcodeTimeOut));
			}
			
			String ip = sr.getIp();
			String userAgent = sr.getUserAgent();
			IP IP = IpUtil.getIp(ip);
			String city = null;
			String province = null;
			if(IP != null){
				city = IP.getCity();
				province = IP.getProvince();
			}
			long currentTimestamp = System.currentTimeMillis();
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", type);
			//已扫描
			if(state==1){
				if(type==2){
					//下载不需要登录
					/** 下载文件 开始  **/
					List<DownRecord> recordList = new ArrayList<DownRecord>();
					List<String> fileAddrs = new ArrayList<String>();
					for (int i = 0; i < files.length(); i++) {
						JSONObject file = files.getJSONObject(i);
						fileAddrs.add(file.getString("fileAddr"));
						DownRecord record = new DownRecord();
						record.setFileId(file.getInt("fileId"));
						record.setState(1);//无法获取下载状态，默认成功
						record.setWuId(weixinUser.getWuId());
						record.setIp(ip);
						record.setCity(city);
						record.setProvince(province);
						record.setUserAgent(userAgent);
						record.setDownDate(StringUtil.parseInt(DateUtil.today().replaceAll("-", "")));
						record.setDownHour(DateUtil.getHour());
						record.setCtime(DateUtil.now());
						recordList.add(record);
					}
					logger.debug("获得的文件列表：");
					logger.debug(fileAddrs);
					if(!downRecordService.batchAddRecord(recordList)){
						return new ApiMessage(ReqCode.DownLoad, ReqState.Failure);
					}
					/** 下载文件结束  **/
					map.put("fileAddrs", fileAddrs);
					websocketSessionRedis.sendMsg(sessionKey, new ApiMessage(ReqCode.DownLoad, ReqState.Success).setInfo(map));
					return new ApiMessage(ReqCode.DownLoad, ReqState.Success);
				}
				websocketSessionRedis.sendMsg(sessionKey, new ApiMessage(ReqCode.Login, ReqState.AlreadyScan));
			}
			//确认登录
			if(state==2){
				//既不是老师也不是班主任，无权登录
				if(!weixinUser.isTeacher() && !weixinUser.isDirector()){
					websocketSessionRedis.sendMsg(sessionKey, new ApiMessage(ReqCode.Login, ReqState.NoPower));
					return new ApiMessage(ReqCode.Login, ReqState.NoPower);
				}
				
				if(type>2){
					map.put("openGId", openGId);
				}
				
				oauthRedis.setOpenId(sessionKey, openId);
				Oauth oauth = oauthRedis.get(openId);
				if(oauth!=null){
					//同一会话，只做覆盖
					if(sessionKey.equals(oauth.getSessionKey())){
						//TODO后面如果有需求再添加
					}else {
						//同一浏览器，不同会话
						if(ip.equals(oauth.getIp()) && userAgent.equals(oauth.getUserAgent())){
							//TODO后面如果有需求再添加
						}else{
							//不同浏览器或不同设备，不同会话，顶号处理
							//修改原有的登录记录为退出登录
							UserPcLogin userPcLogin = userPcLoginService.getLatestRecord(weixinUser.getWuId());
							if(userPcLogin!=null){
								userPcLoginService.logout(userPcLogin);
							}
							//通知原页面被顶号
							logger.debug("通知原页面被顶号");
							oauthRedis.removeOauth(oauth.getSessionKey());
							websocketSessionRedis.sendMsg(oauth.getSessionKey(), new ApiMessage(ReqCode.Login, ReqState.CoverUser));
						}
					}
				}else{
					oauth = new Oauth();
				}
				oauth.setIp(ip);
				oauth.setUserAgent(userAgent);
				oauth.setLoginSecret(sign);
				oauth.setSessionKey(sessionKey);
				oauth.setWeixinUser(weixinUser);
				oauthRedis.set(openId, oauth);
				//添加登录记录
				UserPcLogin loginRecord = new UserPcLogin();
				loginRecord.setIp(ip);
				loginRecord.setCity(city);
				loginRecord.setProvince(province);
				loginRecord.setUserAgent(userAgent);
				loginRecord.setLoginDate(StringUtil.parseInt(DateUtil.today().replaceAll("-", "")));
				loginRecord.setHeartTime(currentTimestamp);
				loginRecord.setLogin(currentTimestamp);
				loginRecord.setWuId(weixinUser.getWuId());
				loginRecord.setLoginSecret(sign);
				userPcLoginService.addRecord(loginRecord);
				
				//通知（新）页面登录成功
				websocketSessionRedis.sendMsg(sessionKey, new ApiMessage(ReqCode.Login, ReqState.Success).setInfo(map));
			}
			
			return new ApiMessage(ReqCode.Login, ReqState.Success);
		}catch (Exception e) {
			e.printStackTrace();
			return new ApiMessage(ReqCode.Login, ReqState.Failure);
		}
	}

}
