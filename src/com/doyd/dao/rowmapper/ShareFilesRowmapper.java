package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.ShareFiles;

@Component
public class ShareFilesRowmapper implements RowMapper<ShareFiles> {

	public ShareFiles mapRow(ResultSet rs, int rowNum) throws SQLException {
		ShareFiles model = new ShareFiles();
		model.setShareId(rs.getInt("shareId"));
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
