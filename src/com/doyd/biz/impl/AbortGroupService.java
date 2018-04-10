package com.doyd.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doyd.biz.IAbortGroupService;
import com.doyd.dao.IAbortGroupDao;

@Service
public class AbortGroupService implements IAbortGroupService {
	@Autowired
	private IAbortGroupDao abortGroupDao;
	
}
