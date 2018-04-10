package com.doyd.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doyd.biz.IUserPcLoginService;
import com.doyd.dao.IUserPcLoginDao;
import com.doyd.model.UserPcLogin;

@Service
public class UserPcLoginService implements IUserPcLoginService {
	@Autowired
	private IUserPcLoginDao userPcLoginDao;

	@Override
	public boolean addRecord(UserPcLogin record) {
		return userPcLoginDao.addRecord(record);
	}

	@Override
	public boolean updateHeartTime(UserPcLogin record) {
		return userPcLoginDao.updateHeartTime(record);
	}

	@Override
	public boolean logout(UserPcLogin record) {
		long currentTimestamp = System.currentTimeMillis();
		int duration = (int) (currentTimestamp-record.getLogin())/1000;
		record.setDuration(duration);
		record.setQuitOut(currentTimestamp);
		return userPcLoginDao.logout(record);
	}

	@Override
	public UserPcLogin getLatestRecord(int wuId) {
		return userPcLoginDao.getLatestRecord(wuId);
	}
	
}
