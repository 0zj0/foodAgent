package com.doyd.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.action.common.Page;
import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IShareFilesDao;
import com.doyd.dao.rowmapper.ShareFilesRowmapper;
import com.doyd.model.ShareFiles;
import org.doyd.utils.StringUtil;

@Repository
public class ShareFilesDao implements IShareFilesDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private ShareFilesRowmapper mapper;
	@Override
	public ShareFiles getShareFilesById(int shareId) {
		String sql = "select * from shareFiles where shareId=? and state=1";
		return daoSupport.queryForObject(sql, new Object[]{shareId}, mapper);
	}
	@Override
	public List<Map<String, Object>> queryShareFilesByPage(Page page,
			int groupId, int wuId, String key, int type) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM shareFiles AS a " +
				"LEFT JOIN weixin_user AS b ON a.wuId=b.wuId INNER JOIN " +
				"(SELECT * FROM files WHERE storageTable='shareFiles' AND state=1) AS c ON a.shareId=c.id " +
				"INNER JOIN (SELECT * FROM user_group WHERE state=1 AND groupId=? GROUP BY wuId,groupId) AS d ON a.groupId=d.groupId AND a.wuId=d.wuId " +
				"WHERE a.state=1 AND a.groupId=?");
		List<Object> params = new ArrayList<Object>();
		params.add(groupId);
		params.add(groupId);
		if(StringUtil.isNotEmpty(key)){
			sql.append(" AND (b.realName LIKE ? OR c.fileName LIKE ?)");
			params.add(StringUtil.toLikeStr(key));
			params.add(StringUtil.toLikeStr(key));
		}
		if(type>0){
			if(type==1){
				sql.append(" AND a.wuId=?");
			}
			if(type==2){
				sql.append(" AND a.wuId!=?");
			}
			params.add(wuId);
		}
		page.setTotalSize(daoSupport.queryForInt(sql.toString(), params.toArray()));
		sql = new StringBuilder("SELECT a.shareId,a.remark,a.storageTable,a.ctime,IF(d.director=1,'班主任',IF(d.teacher=1,IF(d.subjects IS NULL, IF(b.realName IS NULL, b.nickName, b.realName), CONCAT(d.subjects,'老师')),IF(b.realName IS NULL, b.nickName, b.realName))) AS name,IF(a.wuId=?, TRUE, FALSE) AS flag FROM shareFiles AS a " +
				"LEFT JOIN weixin_user AS b ON a.wuId=b.wuId INNER JOIN " +
				"(SELECT * FROM files WHERE storageTable='shareFiles' AND state=1) AS c ON a.shareId=c.id " +
				"INNER JOIN (SELECT * FROM user_group WHERE state=1 AND groupId=? GROUP BY wuId,groupId) AS d ON a.groupId=d.groupId AND a.wuId=d.wuId " +
				"WHERE a.state=1 AND a.groupId=?");
		params.add(0, wuId);
		if(StringUtil.isNotEmpty(key)){
			sql.append(" AND (b.realName LIKE ? OR c.fileName LIKE ?)");
		}
		if(type>0){
			if(type==1){
				sql.append(" AND a.wuId=?");
			}
			if(type==2){
				sql.append(" AND a.wuId!=?");
			}
		}
		sql.append(" ORDER BY a.shareId DESC LIMIT ?,?");
		params.add(page.getCursor());
		params.add(page.getPerSize());
		return daoSupport.queryForList(sql.toString(), params.toArray());
	}
	@Override
	public List<Map<String, Object>> queryShareFilesByPage(int page,
			int groupId, String key) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("SELECT LEFT(a.ctime,10) cDate,a.shareId,a.remark,a.ctime"
				+ ",CASE WHEN t3.director THEN '班主任' WHEN t3.teacher THEN IF(t3.subjects IS NULL, b.realName"
				+ ", CONCAT(t3.subjects,'老师')) WHEN t3.patriarch THEN t3.aliasName END AS realName"
				+ ",fileId,fileAddr,fileFormat,fileName,fileSize,fileType,d.openId FROM shareFiles AS a "
				+ "LEFT JOIN weixin_user AS b ON a.wuId=b.wuId "
				+ "LEFT JOIN wx_user_oauth AS d ON b.unionId=d.unionId "
				+ "LEFT JOIN files c ON c.storageTable='shareFiles' AND a.shareId=c.id "
				+ "LEFT JOIN (SELECT * FROM user_group WHERE groupId=? AND state=1 GROUP BY wuId) t3 ON b.wuId=t3.wuId "
				+ "WHERE a.state=1 AND a.groupId=?");
		params.add(groupId);
		params.add(groupId);
		if(StringUtil.isNotEmpty(key)){
			sql.append(" AND (b.realName LIKE ? OR c.fileName LIKE ?)");
			params.add(StringUtil.toLikeStr(key));
			params.add(StringUtil.toLikeStr(key));
		}
		sql.append(" ORDER BY a.shareId DESC LIMIT ?,?");
		params.add((page-1)*20);
		params.add(20);
		return daoSupport.queryForList(sql.toString(), params.toArray());
	}
	@Override
	public boolean addShareFiles(ShareFiles shareFiles) {
		String sql = "insert into shareFiles (groupId,ugId,wuId,`upDate`,fileCnt," +
				"storageTable,remark,ctime) values (?,?,?,?,?, ?,?,now())";
		shareFiles.setShareId(daoSupport.insert(sql, shareFiles.getGroupId(), shareFiles.getUgId(),
				shareFiles.getWuId(), shareFiles.getUpDate(), shareFiles.getFileCnt(),
				shareFiles.getStorageTable(), shareFiles.getRemark()));
		return shareFiles.getShareId()>0;
	}
	@Override
	public boolean deleteShareFiles(int[] shareIds, int groupId, boolean flag,
			int wuId) {
		StringBuilder sql = new StringBuilder("update shareFiles set state=2 where state=1 and groupId=? and shareId in (");
		List<Object> params = new ArrayList<Object>();
		params.add(groupId);
		for (int shareId : shareIds) {
			sql.append("?,");
			params.add(shareId);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		if(!flag){
			sql.append(" and wuId=?");
			params.add(wuId);
		}
		return daoSupport.update(sql.toString(), params.toArray())>0;
	}
	@Override
	public boolean deleteShareFiles(int[] shareIds) {
		StringBuilder sql = new StringBuilder("update shareFiles set state=2 where state=1 and shareId in (");
		List<Object> params = new ArrayList<Object>();
		for (int shareId : shareIds) {
			sql.append("?,");
			params.add(shareId);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		return daoSupport.update(sql.toString(), params.toArray())>0;
	}
	@Override
	public boolean updateShareFiles(ShareFiles shareFiles) {
		String sql = "update shareFiles set remark=? where shareId=? and state=1";
		return daoSupport.update(sql, shareFiles.getRemark(), shareFiles.getShareId()) > 0;
	}
	@Override
	public int getShareFilesCnt(int groupId, int wuId, int type) {
		String sql = "select count(*) from shareFiles where state=1 and groupId=?";
		List<Object> params = new ArrayList<Object>();
		params.add(groupId);
		if(type>0){
			if(type==1){
				sql+=" and wuId=?";
				params.add(wuId);
			}
		}
		return daoSupport.queryForInt(sql, params.toArray());
	}
	@Override
	public List<ShareFiles> getShareFilesList(int[] shareIds) {
		StringBuilder sql = new StringBuilder("select * from shareFiles where state=1 and shareId in (");
		List<Object> params = new ArrayList<Object>();
		for (int shareId : shareIds) {
			sql.append("?,");
			params.add(shareId);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		return daoSupport.query(sql.toString(), params.toArray(), mapper);
	}
	@Override
	public List<ShareFiles> getShareFilesList(boolean flag, int wuId,
			int[] shareIds) {
		StringBuilder sql = new StringBuilder("select * from shareFiles where state=1 and shareId in (");
		List<Object> params = new ArrayList<Object>();
		for (int shareId : shareIds) {
			sql.append("?,");
			params.add(shareId);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		if(!flag){
			sql.append(" and wuId=?");
			params.add(wuId);
		}
		return daoSupport.query(sql.toString(), params.toArray(), mapper);
	}
}
