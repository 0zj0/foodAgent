package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.WeixinUser;

@Component
public class WeixinUserRowmapper implements RowMapper<WeixinUser> {

	public WeixinUser mapRow(ResultSet rs, int rowNum) throws SQLException {
		WeixinUser model = new WeixinUser();
		model.setWuId(rs.getInt("wuId"));
		model.setOpenId(rs.getString("openId"));
		model.setUnionId(rs.getString("unionId"));
		model.setNickName(rs.getString("nickName"));
		model.setGender(rs.getInt("gender"));
		model.setCountry(rs.getString("country"));
		model.setProvince(rs.getString("province"));
		model.setCity(rs.getString("city"));
		model.setAvatarUrl(rs.getString("avatarUrl"));
		model.setTimestamp(rs.getLong("timestamp"));
		model.setPhone(rs.getString("phone"));
		model.setRealName(rs.getString("realName"));
		model.setPatriarch(rs.getBoolean("patriarch"));
		model.setTeacher(rs.getBoolean("teacher"));
		model.setDirector(rs.getBoolean("director"));
		model.setSource(rs.getString("source"));
		model.setRegDate(rs.getInt("regDate"));
		model.setLoginDate(rs.getInt("loginDate"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
