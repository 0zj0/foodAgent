package com.doyd.module.applet.entrance;

import java.util.Map;

import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;

/**
 * @author ylb
 * @version 创建时间：2017-12-14 下午4:37:01
 */
public interface IEntranceService {
	/**
	 * 微信入口
	 * 
	 * @param wu 微信用户信息
	 * @param id 对应Id
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-14 下午4:42:41
	 */
	public ApiMessage entrance(WeixinUser wu, Map<String, Object> resultMap, ApiMessage am, String info1, String info2);
}
