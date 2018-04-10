package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.Subject;

@Component
public class SubjectRowmapper implements RowMapper<Subject> {

	public Subject mapRow(ResultSet rs, int rowNum) throws SQLException {
		Subject model = new Subject();
		model.setSubjectId(rs.getInt("subjectId"));
		model.setWuId(rs.getInt("wuId"));
		model.setSubject(rs.getString("subject"));
		model.setState(rs.getInt("state"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
