package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.WxUserOauth;

@Component
public class WxUserOauthRowmapper implements RowMapper<WxUserOauth> {

	public WxUserOauth mapRow(ResultSet rs, int rowNum) throws SQLException {
		WxUserOauth model = new WxUserOauth();
		model.setWuoId(rs.getInt("wuoId"));
		model.setUnionId(rs.getString("unionId"));
		model.setAppId(rs.getString("appId"));
		model.setOpenId(rs.getString("openId"));
		model.setState(rs.getInt("state"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
