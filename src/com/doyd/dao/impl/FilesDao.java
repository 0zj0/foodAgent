package com.doyd.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IFilesDao;
import com.doyd.dao.rowmapper.FilesRowmapper;
import com.doyd.model.Files;
import org.doyd.utils.StringUtil;

@Repository
public class FilesDao implements IFilesDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private FilesRowmapper mapper;
	
	@Override
	public List<Map<String, Object>> getFiles(String table, int id, String storageTable) {
		if(StringUtil.isEmpty(table)) return null;
		String sql = "select fileAddr,fileFormat,fileName,fileSize,fileType,fileId from "+ table +" where id=? and storageTable=? and state=1 ORDER BY fileType desc";
		return daoSupport.queryForList(sql, id, storageTable);
	}

	@Override
	public boolean batchAddFiles(List<Files> fileList, int id) {
		if(fileList==null || fileList.size()<=0){
			return true;
		}
		StringBuilder sql = new StringBuilder("insert into files (groupId,fileName,fileType,fileFormat," +
				"fileSize,fileAddr,storageTable,id,cDate,ctime) values ");
		List<Object> params = new ArrayList<Object>();
		for (Files files : fileList) {
			sql.append("(?,?,?,?,?, ?,?,?,?,now()),");
			params.add(files.getGroupId());
			params.add(files.getFileName());
			params.add(files.getFileType());
			params.add(files.getFileFormat());
			params.add(files.getFileSize());
			params.add(files.getFileAddr());
			params.add(files.getStorageTable());
			params.add(id);
			params.add(files.getCDate());
		}
		sql.deleteCharAt(sql.length()-1);
		return daoSupport.update(sql.toString(), params.toArray())>0;
	}

	@Override
	public boolean batchDeleteFiles(String table, String storageTable, List<Integer> ids) {
		if(StringUtil.isEmpty(table)) return false;
		StringBuilder sql = new StringBuilder("update " + table + " set state=2 where storageTable=? and id in (");
		List<Object> params = new ArrayList<Object>();
		params.add(storageTable);
		for (int id : ids) {
			sql.append("?,");
			params.add(id);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		return daoSupport.update(sql.toString(), params.toArray())>=0;
	}

	@Override
	public boolean deleteFiles(String table, int id, int groupId, String storageTable) {
		if(StringUtil.isEmpty(table)) return false;
		String sql = "update " + table + " set state=2 where groupId=? and id=? and storageTable=?";
		return daoSupport.update(sql, groupId, id, storageTable)>0;
	}

	@Override
	public Files getFileById(int fileId) {
		String sql = "select * from files where fileId=? and state=1";
		return daoSupport.queryForObject(sql, new Object[]{fileId}, mapper);
	}

}
