package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.ReadClass;

@Component
public class ReadClassRowmapper implements RowMapper<ReadClass> {

	public ReadClass mapRow(ResultSet rs, int rowNum) throws SQLException {
		ReadClass model = new ReadClass();
		model.setReadId(rs.getInt("readId"));
		model.setGroupId(rs.getInt("groupId"));
		model.setNotifyId(rs.getInt("notifyId"));
		model.setWuId(rs.getInt("wuId"));
		model.setInterval(rs.getInt("interval"));
		model.setReadDate(rs.getInt("readDate"));
		model.setReadHour(rs.getInt("readHour"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
