package com.doyd.core.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.core.model.SysConfig;

@Component
public class SysConfigRowmapper implements RowMapper<SysConfig> {

	public SysConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
		SysConfig model = new SysConfig();
		model.setConfigId(rs.getInt("configId"));
		model.setKey(rs.getString("key"));
		model.setName(rs.getString("name"));
		model.setValueType(rs.getString("valueType"));
		model.setIntValue(rs.getInt("intValue"));
		model.setBoolValue(rs.getBoolean("boolValue"));
		model.setTextValue(rs.getString("textValue"));
		model.setRule(rs.getString("rule"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
