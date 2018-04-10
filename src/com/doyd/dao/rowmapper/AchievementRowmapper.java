package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.Achievement;

@Component
public class AchievementRowmapper implements RowMapper<Achievement> {

	public Achievement mapRow(ResultSet rs, int rowNum) throws SQLException {
		Achievement model = new Achievement();
		model.setScoreId(rs.getInt("scoreId"));
		model.setGroupId(rs.getInt("groupId"));
		model.setUgId(rs.getInt("ugId"));
		model.setWuId(rs.getInt("wuId"));
		model.setUpDate(rs.getInt("upDate"));
		model.setFileCnt(rs.getInt("fileCnt"));
		model.setStorageTable(rs.getString("storageTable"));
		model.setRemark(rs.getString("remark"));
		model.setState(rs.getInt("state"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
