package com.doyd.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IAppletContactDao;
import com.doyd.dao.rowmapper.AppletContactRowmapper;
import com.doyd.model.AppletContact;

@Repository
public class AppletContactDao implements IAppletContactDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private AppletContactRowmapper mapper;

}
