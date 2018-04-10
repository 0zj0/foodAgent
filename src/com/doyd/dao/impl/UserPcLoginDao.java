package com.doyd.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IUserPcLoginDao;
import com.doyd.dao.rowmapper.UserPcLoginRowmapper;
import com.doyd.model.UserPcLogin;

@Repository
public class UserPcLoginDao implements IUserPcLoginDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private UserPcLoginRowmapper mapper;
	@Override
	public boolean addRecord(UserPcLogin record) {
		String sql = "insert into user_pc_login (wuId, loginDate, ip, province, city, userAgent, " +
				"loginSecret, heartTime, login) values (?,?,?,?,?, ?,?,?,?)";
		record.setLoginId(daoSupport.insert(sql, record.getWuId(), record.getLoginDate(), record.getIp(),
				record.getProvince(), record.getCity(), record.getUserAgent(), record.getLoginSecret(),
				record.getHeartTime(), record.getLogin()));
		return record.getLoginId()>0;
	}
	@Override
	public boolean updateHeartTime(UserPcLogin record) {
		String sql = "update user_pc_login set heartTime=? where loginId=?";
		return daoSupport.update(sql, record.getHeartTime(), record.getLoginId()) > 0;
	}
	@Override
	public boolean logout(UserPcLogin record) {
		String sql = "update user_pc_login set duration=?, quitOut=? where loginId=?";
		return daoSupport.update(sql, record.getDuration(), record.getQuitOut(), record.getLoginId()) > 0;
	}
	@Override
	public UserPcLogin getLatestRecord(int wuId) {
		String sql = "select * from user_pc_login where wuId=? and quitOut=0 order by loginId desc limit 1";
		return daoSupport.queryForObject(sql, new Object[]{wuId}, mapper);
	}

}
