package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.Children;

@Component
public class ChildrenRowmapper implements RowMapper<Children> {

	public Children mapRow(ResultSet rs, int rowNum) throws SQLException {
		Children model = new Children();
		model.setChildId(rs.getInt("childId"));
		model.setWuId(rs.getInt("wuId"));
		model.setChildName(rs.getString("childName"));
		model.setSex(rs.getInt("sex"));
		model.setBirthday(rs.getString("birthday"));
		model.setIdCard(rs.getString("idCard"));
		model.setRelation(rs.getString("relation"));
		model.setEducation(rs.getInt("education"));
		model.setGrade(rs.getInt("grade"));
		model.setState(rs.getInt("state"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
