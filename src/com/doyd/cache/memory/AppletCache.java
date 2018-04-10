package com.doyd.cache.memory;

import org.springframework.stereotype.Component;

import com.doyd.cache.CacheKey;
import com.doyd.model.Applet;
import com.doyd.server.cache.CacheManager;

/**
 * @author ylb
 * @version 创建时间：2018-3-13 上午10:30:51
 */
@Component
public class AppletCache {

	public Applet getApplet(String appId){
		return (Applet) CacheManager.getMemory().get(CacheKey.getApplet(appId));
	}
	
	public void setApplet(String appId, Applet applet){
		CacheManager.getMemory().set(appId, applet);
	}
}
