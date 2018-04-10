package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.Homework;

@Component
public class HomeworkRowmapper implements RowMapper<Homework> {

	public Homework mapRow(ResultSet rs, int rowNum) throws SQLException {
		Homework model = new Homework();
		model.setWorkId(rs.getInt("workId"));
		model.setGroupId(rs.getInt("groupId"));
		model.setUgId(rs.getInt("ugId"));
		model.setWuId(rs.getInt("wuId"));
		model.setSubject(rs.getString("subject"));
		model.setWork(rs.getString("work"));
		model.setFileCnt(rs.getInt("fileCnt"));
		model.setStorageTable(rs.getString("storageTable"));
		model.setSubmitTime(rs.getString("submitTime"));
		model.setCostTime(rs.getInt("costTime"));
		model.setState(rs.getInt("state"));
		model.setReadCnt(rs.getInt("readCnt"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
