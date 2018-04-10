package com.doyd.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doyd.biz.IWxUserOauthService;
import com.doyd.dao.IWxUserOauthDao;

@Service
public class WxUserOauthService implements IWxUserOauthService {
	@Autowired
	private IWxUserOauthDao wxUserOauthDao;
	
}
