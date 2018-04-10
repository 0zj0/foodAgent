package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.UserPcLogin;

@Component
public class UserPcLoginRowmapper implements RowMapper<UserPcLogin> {

	public UserPcLogin mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserPcLogin model = new UserPcLogin();
		model.setLoginId(rs.getInt("loginId"));
		model.setWuId(rs.getInt("wuId"));
		model.setLoginDate(rs.getInt("loginDate"));
		model.setIp(rs.getString("ip"));
		model.setProvince(rs.getString("province"));
		model.setCity(rs.getString("city"));
		model.setUserAgent(rs.getString("userAgent"));
		model.setDuration(rs.getInt("duration"));
		model.setLoginSecret(rs.getString("loginSecret"));
		model.setHeartTime(rs.getLong("heartTime"));
		model.setLogin(rs.getLong("login"));
		model.setQuitOut(rs.getLong("quitOut"));
		return model;
	}
}
