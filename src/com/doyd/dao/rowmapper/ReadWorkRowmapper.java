package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.ReadWork;

@Component
public class ReadWorkRowmapper implements RowMapper<ReadWork> {

	public ReadWork mapRow(ResultSet rs, int rowNum) throws SQLException {
		ReadWork model = new ReadWork();
		model.setReadId(rs.getInt("readId"));
		model.setGroupId(rs.getInt("groupId"));
		model.setWorkId(rs.getInt("workId"));
		model.setWuId(rs.getInt("wuId"));
		model.setInterval(rs.getInt("interval"));
		model.setReadDate(rs.getInt("readDate"));
		model.setReadHour(rs.getInt("readHour"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
