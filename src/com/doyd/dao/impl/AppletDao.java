package com.doyd.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IAppletDao;
import com.doyd.dao.rowmapper.AppletRowmapper;
import com.doyd.model.Applet;

@Repository
public class AppletDao implements IAppletDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private AppletRowmapper mapper;
	
	@Override
	public Applet getApplet(String appId) {
		String sql = "SELECT * FROM applet WHERE appId=? AND state=1";
		return daoSupport.queryForObject(sql, new Object[]{appId}, mapper);
	}

}
