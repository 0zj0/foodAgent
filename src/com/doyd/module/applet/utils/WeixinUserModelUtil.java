package com.doyd.module.applet.utils;

import org.doyd.sdk.WeixinApi;
import org.doyd.utils.StringUtil;
import org.doyd.weixin.entity.user.WeixinAppletUserInfo;
import org.json.JSONObject;

import com.doyd.model.WeixinUser;

/**
 * @author ylb
 * @version 创建时间：2017-12-13 下午3:11:42
 */
public class WeixinUserModelUtil {
	
	public static String getOpenGId(String session_key,String encryptedData,String iv){
		if(StringUtil.isEmpty(session_key) || StringUtil.isEmpty(encryptedData) || StringUtil.isEmpty(iv)){
			return null;
		}
		try {
			String result = AesCbcUtil.decrypt(encryptedData.replace(' ','+'),session_key,iv,"UTF-8");
			if(StringUtil.isEmpty(result)){
				return null;
			}
			JSONObject json = new JSONObject(result);
			return json.getString("openGId");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void createUser(String session_key,String encryptedData,String iv, WeixinUser wu) {
		if(StringUtil.isEmpty(session_key) || StringUtil.isEmpty(encryptedData) || StringUtil.isEmpty(iv)){
			return;
		}
		try {
			WeixinAppletUserInfo wau = WeixinApi.getAppletUserService().getAppletUserInfo(session_key, encryptedData, iv);
			createUser(wau, wu);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void createUser(WeixinAppletUserInfo wau, WeixinUser wu) throws Exception{
		if(wau == null){
			return;
		}
		wu.setUnionId(wau.getUnionId());
		wu.setNickName(wau.getNickName());
		wu.setGender(wau.getGender());
		wu.setCountry(wau.getCountry());
		wu.setProvince(wau.getProvince());
		wu.setCity(wau.getCity());
		wu.setAvatarUrl(wau.getAvatarUrl());
	}
}
