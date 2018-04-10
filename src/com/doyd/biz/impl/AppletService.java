package com.doyd.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doyd.biz.IAppletService;
import com.doyd.cache.memory.AppletCache;
import com.doyd.dao.IAppletDao;
import com.doyd.model.Applet;

@Service
public class AppletService implements IAppletService {
	@Autowired
	private IAppletDao appletDao;
	@Autowired
	private AppletCache appletCache;

	@Override
	public Applet getApplet(String appId) {
		Applet applet = appletCache.getApplet(appId);
		if(applet == null){
			applet = appletDao.getApplet(appId);
			if(applet != null){
				appletCache.setApplet(appId, applet);
			}
		}
		return applet;
	}
	
}
