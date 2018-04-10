package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.Files;

@Component
public class FilesRowmapper implements RowMapper<Files> {

	public Files mapRow(ResultSet rs, int rowNum) throws SQLException {
		Files model = new Files();
		model.setFileId(rs.getInt("fileId"));
		model.setGroupId(rs.getInt("groupId"));
		model.setFileType(rs.getString("fileType"));
		model.setFileName(rs.getString("fileName"));
		model.setFileFormat(rs.getString("fileFormat"));
		model.setStorageTable(rs.getString("storageTable"));
		model.setId(rs.getInt("id"));
		model.setFileSize(rs.getInt("fileSize"));
		model.setFileAddr(rs.getString("fileAddr"));
		model.setState(rs.getInt("state"));
		model.setLicitState(rs.getInt("licitState"));
		model.setCDate(rs.getInt("cDate"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
