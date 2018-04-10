package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.AppletContact;

@Component
public class AppletContactRowmapper implements RowMapper<AppletContact> {

	public AppletContact mapRow(ResultSet rs, int rowNum) throws SQLException {
		AppletContact model = new AppletContact();
		model.setAcId(rs.getInt("acId"));
		model.setJzqzsId(rs.getInt("jzqzsId"));
		model.setClassfeeId(rs.getInt("classfeeId"));
		model.setState(rs.getInt("state"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
