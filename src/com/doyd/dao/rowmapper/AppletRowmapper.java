package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.Applet;

@Component
public class AppletRowmapper implements RowMapper<Applet> {

	public Applet mapRow(ResultSet rs, int rowNum) throws SQLException {
		Applet model = new Applet();
		model.setAppletId(rs.getInt("appletId"));
		model.setAppletName(rs.getString("appletName"));
		model.setAppId(rs.getString("appId"));
		model.setGhId(rs.getString("ghId"));
		model.setAppletType(rs.getInt("appletType"));
		model.setState(rs.getInt("state"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
