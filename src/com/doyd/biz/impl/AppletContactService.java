package com.doyd.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doyd.biz.IAppletContactService;
import com.doyd.dao.IAppletContactDao;

@Service
public class AppletContactService implements IAppletContactService {
	@Autowired
	private IAppletContactDao appletContactDao;
	
}
