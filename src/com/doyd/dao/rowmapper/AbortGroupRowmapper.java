package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.AbortGroup;

@Component
public class AbortGroupRowmapper implements RowMapper<AbortGroup> {

	public AbortGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
		AbortGroup model = new AbortGroup();
		model.setAbortId(rs.getInt("abortId"));
		model.setGroupId(rs.getInt("groupId"));
		model.setUgId(rs.getInt("ugId"));
		model.setWuId(rs.getInt("wuId"));
		model.setIdentities(rs.getString("identities"));
		model.setAbortType(rs.getInt("abortType"));
		model.setOperatorId(rs.getInt("operatorId"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
