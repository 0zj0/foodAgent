package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.WeixinGroup;

@Component
public class WeixinGroupRowmapper implements RowMapper<WeixinGroup> {

	public WeixinGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
		WeixinGroup model = new WeixinGroup();
		model.setGroupId(rs.getInt("groupId"));
		model.setGroupPic(rs.getString("groupPic"));
		model.setPicCnt(rs.getInt("picCnt"));
		model.setOpenGId(rs.getString("openGId"));
		model.setGroupName(rs.getString("groupName"));
		model.setDirectorId(rs.getInt("directorId"));
		model.setCity(rs.getString("city"));
		model.setPeopleCnt(rs.getInt("peopleCnt"));
		model.setTotalWork(rs.getInt("totalWork"));
		model.setTotalNotify(rs.getInt("totalNotify"));
		model.setTotalShare(rs.getInt("totalShare"));
		model.setTotalLeave(rs.getInt("totalLeave"));
		model.setTotalAchievement(rs.getInt("totalAchievement"));
		model.setState(rs.getInt("state"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
