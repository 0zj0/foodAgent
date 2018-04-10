package com.doyd.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.action.common.Page;
import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IClassNotifyDao;
import com.doyd.dao.rowmapper.ClassNotifyRowmapper;
import com.doyd.model.ClassNotify;
import org.doyd.utils.StringUtil;

@Repository
public class ClassNotifyDao implements IClassNotifyDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private ClassNotifyRowmapper mapper;
	
	@Override
	public ClassNotify getClassNotify(int notifyId) {
		String sql = "SELECT * FROM class_notify WHERE notifyId=? AND state=1";
		return daoSupport.queryForObject(sql, new Object[]{notifyId}, mapper);
	}
	
	@Override
	public Map<String, Object> getClassNotifyForMap(int notifyId){
		String sql = "SELECT t1.*,CASE WHEN t2.director THEN '班主任' WHEN t2.teacher THEN IF(t2.subjects IS NULL, t3.realName"
				+ ", CONCAT(t2.subjects,'老师')) WHEN t2.patriarch THEN t2.aliasName END AS realName FROM class_notify t1 "
				+ "LEFT JOIN weixin_user t3 ON t1.wuId=t3.wuId "
				+ "LEFT JOIN user_group t2 ON t1.groupId=t2.groupId AND t2.wuId=t1.wuId AND t2.state=1 "
				+ "WHERE notifyId=? AND t1.state=1 LIMIT 1";
		return daoSupport.queryForMap(sql, notifyId);
	}

	@Override
	public List<Map<String, Object>> queryClassNotifyByPage(Page page,
			int groupId, int type, int wuId, String key,
			String beginDate, String endDate) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM class_notify AS a " +
				"INNER JOIN (SELECT * FROM user_group WHERE state=1 AND groupId=? GROUP BY wuId,groupId) AS b ON a.groupId=b.groupId AND a.wuId=b.wuId " +
				"LEFT JOIN weixin_user AS c ON a.wuId=c.wuId WHERE a.state=1 AND a.groupId=?");
		List<Object> params = new ArrayList<Object>();
		params.add(groupId);
		params.add(groupId);
		if(type>0){
			if(type==1){
				sql.append(" AND a.wuId=?");
			}
			if(type==2){
				sql.append(" AND a.wuId!=?");
			}
			params.add(wuId);
		}
		if(StringUtil.isNotEmpty(key)){
			sql.append(" AND (c.realName LIKE ? OR a.title LIKE ?)");
			params.add(StringUtil.toLikeStr(key));
			params.add(StringUtil.toLikeStr(key));
		}
		if(StringUtil.isNotEmpty(beginDate) && StringUtil.isNotEmpty(endDate)){
			sql.append(" AND DATE(a.ctime)>=? AND DATE(a.ctime)<=?");
			params.add(beginDate);
			params.add(endDate);
		}
		if(StringUtil.isNotEmpty(beginDate) && StringUtil.isEmpty(endDate)){
			sql.append(" AND DATE(a.ctime)=?");
			params.add(beginDate);
		}
		if(StringUtil.isEmpty(beginDate) && StringUtil.isNotEmpty(endDate)){
			sql.append(" AND DATE(a.ctime)=?");
			params.add(endDate);
		}
		page.setTotalSize(daoSupport.queryForInt(sql.toString(), params.toArray()));
		sql = new StringBuilder("SELECT a.notifyId,a.title,a.storageTable,a.ctime,IF(b.director=1,'班主任',IF(b.teacher=1,IF(b.subjects IS NULL, IF(c.realName IS NULL, c.nickName, c.realName), CONCAT(b.subjects,'老师')),IF(c.realName IS NULL, c.nickName, c.realName))) AS name,IF(a.wuId=?, TRUE, FALSE) AS flag FROM class_notify AS a " +
				"INNER JOIN (SELECT * FROM user_group WHERE state=1 AND groupId=? GROUP BY wuId,groupId) AS b ON a.groupId=b.groupId AND a.wuId=b.wuId " +
				"LEFT JOIN weixin_user AS c ON a.wuId=c.wuId WHERE a.state=1 AND a.groupId=?");
		params.add(0, wuId);
		if(type>0){
			if(type==1){
				sql.append(" AND a.wuId=?");
			}
			if(type==2){
				sql.append(" AND a.wuId!=?");
			}
		}
		if(StringUtil.isNotEmpty(key)){
			sql.append(" AND (c.realName LIKE ? OR a.title LIKE ?)");
		}
		if(StringUtil.isNotEmpty(beginDate) && StringUtil.isNotEmpty(endDate)){
			sql.append(" AND DATE(a.ctime)>=? AND DATE(a.ctime)<=?");
		}
		if(StringUtil.isNotEmpty(beginDate) && StringUtil.isEmpty(endDate)){
			sql.append(" AND DATE(a.ctime)=?");
		}
		if(StringUtil.isEmpty(beginDate) && StringUtil.isNotEmpty(endDate)){
			sql.append(" AND DATE(a.ctime)=?");
		}
		sql.append(" ORDER BY a.notifyId DESC LIMIT ?,?");
		params.add(page.getCursor());
		params.add(page.getPerSize());
		return daoSupport.queryForList(sql.toString(), params.toArray());
	}
	
	@Override
	public List<Map<String, Object>> queryClassNotifyByPage(int page, int groupId, int wuId, int key){
		List<Object> params = new ArrayList<Object>();
		String sql = "SELECT t1.ctime,t1.notifyId,t1.title,d.openId,"
				+ "CASE WHEN t3.director THEN '班主任' WHEN t3.teacher THEN IF(t3.subjects IS NULL, t2.realName"
				+ ", CONCAT(t3.subjects,'老师')) WHEN t3.patriarch THEN t3.aliasName END AS realName FROM class_notify t1 "
				+ "LEFT JOIN weixin_user t2 ON t1.wuId=t2.wuId "
				+ "LEFT JOIN wx_user_oauth AS d ON t2.unionId=d.unionId "
				+ "LEFT JOIN (SELECT * FROM user_group WHERE groupId=? AND state=1 GROUP BY wuId) t3 ON t2.wuId=t3.wuId "
				+ "WHERE t1.groupId=? AND t1.state=1";
		params.add(groupId);
		params.add(groupId);
		if(key == 1){
			sql += " AND t1.wuId=?";
			params.add(wuId);
		}else if(key == 2){
			sql += " AND t1.wuId!=?";
			params.add(wuId);
		}
		sql += " ORDER BY notifyId DESC LIMIT ?,?";
		params.add((page-1)*20);
		params.add(20);
		return daoSupport.queryForList(sql, params.toArray());
	}

	@Override
	public boolean addClassNotify(ClassNotify notify) {
		String sql = "insert into class_notify (groupId,ugId,wuId,title,content,fileCnt," +
				"storageTable,ctime) values (?,?,?,?,?, ?,?,now())";
		notify.setNotifyId(daoSupport.insert(sql, notify.getGroupId(), notify.getUgId(), notify.getWuId(),
				notify.getTitle(), notify.getContent(), notify.getFileCnt(), notify.getStorageTable()));
		return notify.getNotifyId()>0;
	}

	@Override
	public boolean deleteClassNotify(int[] notifyIds, int groupId,
			boolean flag, int wuId) {
		StringBuilder sql = new StringBuilder("update class_notify set state=2 where state=1 and groupId=? and notifyId in (");
		List<Object> params = new ArrayList<Object>();
		params.add(groupId);
		for (int notifyId : notifyIds) {
			sql.append("?,");
			params.add(notifyId);
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
	public int getClassNotifyCnt(int groupId, int wuId, int type) {
		String sql = "select count(*) from class_notify where state=1 and groupId=?";
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
	public List<ClassNotify> getClassNotifyList(boolean flag, int wuId,
			int[] notifyIds) {
		StringBuilder sql = new StringBuilder("select * from class_notify where state=1 and notifyId in (");
		List<Object> params = new ArrayList<Object>();
		for (int notifyId : notifyIds) {
			sql.append("?,");
			params.add(notifyId);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		if(!flag){
			sql.append(" and wuId=?");
			params.add(wuId);
		}
		return daoSupport.query(sql.toString(), params.toArray(), mapper);
	}

	@Override
	public boolean readNotify(int notifyId) {
		String sql = "UPDATE class_notify SET readCnt=readCnt+1 WHERE notifyId=? AND state=1";
		return daoSupport.update(sql, notifyId) > 0;
	}

}
