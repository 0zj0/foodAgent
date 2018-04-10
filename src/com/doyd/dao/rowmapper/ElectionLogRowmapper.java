package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.ElectionLog;

@Component
public class ElectionLogRowmapper implements RowMapper<ElectionLog> {

	public ElectionLog mapRow(ResultSet rs, int rowNum) throws SQLException {
		ElectionLog model = new ElectionLog();
		model.setRecordId(rs.getInt("recordId"));
		model.setElectionId(rs.getInt("electionId"));
		model.setWuId(rs.getInt("wuId"));
		model.setAgree(rs.getInt("agree"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
