package com.doyd.core.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doyd.core.biz.ISysConfigService;
import com.doyd.core.dao.ISysConfigDao;
import com.doyd.core.model.SysConfig;

@Service
public class SysConfigService implements ISysConfigService {
	@Autowired
	private ISysConfigDao sysConfigDao;

	@Override
	public SysConfig getSysConfigByKey(String sysConfigKey) {
		return sysConfigDao.getSysConfigByKey(sysConfigKey);
	}

	@Override
	public int updateSysConfig(SysConfig sysConfig) {
		return sysConfigDao.updateSysConfig(sysConfig);
	}
	
}
