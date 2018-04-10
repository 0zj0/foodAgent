package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.DownRecord;

@Component
public class DownRecordRowmapper implements RowMapper<DownRecord> {

	public DownRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
		DownRecord model = new DownRecord();
		model.setRecordId(rs.getInt("recordId"));
		model.setFileId(rs.getInt("fileId"));
		model.setWuId(rs.getInt("wuId"));
		model.setIp(rs.getString("ip"));
		model.setProvince(rs.getString("province"));
		model.setCity(rs.getString("city"));
		model.setUserAgent(rs.getString("userAgent"));
		model.setDownDate(rs.getInt("downDate"));
		model.setDownHour(rs.getInt("downHour"));
		model.setState(rs.getInt("state"));
		model.setRemark(rs.getString("remark"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
