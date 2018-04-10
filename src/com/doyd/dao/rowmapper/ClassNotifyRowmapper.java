package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.ClassNotify;

@Component
public class ClassNotifyRowmapper implements RowMapper<ClassNotify> {

	public ClassNotify mapRow(ResultSet rs, int rowNum) throws SQLException {
		ClassNotify model = new ClassNotify();
		model.setNotifyId(rs.getInt("notifyId"));
		model.setGroupId(rs.getInt("groupId"));
		model.setUgId(rs.getInt("ugId"));
		model.setWuId(rs.getInt("wuId"));
		model.setTitle(rs.getString("title"));
		model.setContent(rs.getString("content"));
		model.setFileCnt(rs.getInt("fileCnt"));
		model.setStorageTable(rs.getString("storageTable"));
		model.setReadCnt(rs.getInt("readCnt"));
		model.setState(rs.getInt("state"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
