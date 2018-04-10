package com.doyd.biz;

import com.doyd.model.Applet;

public interface IAppletService {
	/**
	 * 根据appId获得小程序信息
	 * 
	 * @param appId
	 * @return Applet
	 * @author 创建人：ylb
	 * @date 创建时间：2018-3-13 上午10:36:52
	 */
	public Applet getApplet(String appId);
}
