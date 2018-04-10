package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.UserGroup;

@Component
public class UserGroupRowmapper implements RowMapper<UserGroup> {

	public UserGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserGroup model = new UserGroup();
		model.setUgId(rs.getInt("ugId"));
		model.setWuId(rs.getInt("wuId"));
		model.setGroupId(rs.getInt("groupId"));
		model.setChildId(rs.getInt("childId"));
		model.setPatriarch(rs.getBoolean("patriarch"));
		model.setTeacher(rs.getBoolean("teacher"));
		model.setDirector(rs.getBoolean("director"));
		model.setSubjects(rs.getString("subjects"));
		model.setAliasName(rs.getString("aliasName"));
		model.setPhone(rs.getString("phone"));
		model.setNewWork(rs.getInt("newWork"));
		model.setNewNotify(rs.getInt("newNotify"));
		model.setNewShare(rs.getInt("newShare"));
		model.setNewLeave(rs.getInt("newLeave"));
		model.setNewAchievement(rs.getInt("newAchievement"));
		model.setState(rs.getInt("state"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
