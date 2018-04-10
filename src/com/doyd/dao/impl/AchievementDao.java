package com.doyd.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.action.common.Page;
import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IAchievementDao;
import com.doyd.dao.rowmapper.AchievementRowmapper;
import com.doyd.model.Achievement;
import org.doyd.utils.StringUtil;

@Repository
public class AchievementDao implements IAchievementDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private AchievementRowmapper mapper;
	
	@Override
	public Achievement getAchievementById(int scoreId){
		String sql = "select * from achievement where scoreId=? and state=1";
		return daoSupport.queryForObject(sql, new Object[]{scoreId}, mapper);
	}
	
	@Override
	public List<Map<String, Object>> queryAchievementByPage(Page page,
			int groupId, int wuId, String key, int type) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM achievement AS a " +
				"LEFT JOIN weixin_user AS b ON a.wuId=b.wuId INNER JOIN " +
				"(SELECT * FROM files WHERE storageTable='achievement' AND state=1) AS c ON a.scoreId=c.id " +
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
		sql = new StringBuilder("SELECT a.scoreId,a.remark,a.storageTable,a.ctime,IF(d.director=1,'班主任',IF(d.teacher=1,IF(d.subjects IS NULL, IF(b.realName IS NULL, b.nickName, b.realName), CONCAT(d.subjects,'老师')),IF(b.realName IS NULL, b.nickName, b.realName))) AS name,IF(a.wuId=?, TRUE, FALSE) AS flag FROM achievement AS a " +
				"LEFT JOIN weixin_user AS b ON a.wuId=b.wuId INNER JOIN " +
				"(SELECT * FROM files WHERE storageTable='achievement' AND state=1) AS c ON a.scoreId=c.id " +
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
		sql.append(" ORDER BY a.scoreId DESC LIMIT ?,?");
		params.add(page.getCursor());
		params.add(page.getPerSize());
		return daoSupport.queryForList(sql.toString(), params.toArray());
	}
	@Override
	public boolean addAchievement(Achievement achievement) {
		String sql = "insert into achievement (groupId,ugId,wuId,`upDate`,fileCnt," +
				"storageTable,remark,ctime) values (?,?,?,?,?, ?,?,now())";
		achievement.setScoreId(daoSupport.insert(sql, achievement.getGroupId(), achievement.getUgId(),
				achievement.getWuId(), achievement.getUpDate(), achievement.getFileCnt(),
				achievement.getStorageTable(), achievement.getRemark()));
		return achievement.getScoreId()>0;
	}
	@Override
	public boolean deleteAchievement(int[] scoreIds, int groupId, boolean flag,
			int wuId) {
		StringBuilder sql = new StringBuilder("update achievement set state=2 where state=1 and groupId=? and scoreId in (");
		List<Object> params = new ArrayList<Object>();
		params.add(groupId);
		for (int scoreId : scoreIds) {
			sql.append("?,");
			params.add(scoreId);
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
	public boolean deleteAchievement(int[] scoreIds) {
		StringBuilder sql = new StringBuilder("update achievement set state=2 where state=1 and scoreId in (");
		List<Object> params = new ArrayList<Object>();
		for (int scoreId : scoreIds) {
			sql.append("?,");
			params.add(scoreId);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		return daoSupport.update(sql.toString(), params.toArray())>0;
	}
	
	@Override
	public boolean updateAchievement(Achievement achievement) {
		String sql = "update achievement set remark=? where scoreId=? and state=1";
		return daoSupport.update(sql, achievement.getRemark(), achievement.getScoreId()) > 0;
	}
	@Override
	public int getAchievementCnt(int groupId, int wuId, int type) {
		String sql = "select count(*) from achievement where state=1 and groupId=?";
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
	public List<Achievement> getAchievementList(int[] scoreIds) {
		StringBuilder sql = new StringBuilder("select * from achievement where state=1 and scoreId in (");
		List<Object> params = new ArrayList<Object>();
		for (int scoreId : scoreIds) {
			sql.append("?,");
			params.add(scoreId);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		return daoSupport.query(sql.toString(), params.toArray(), mapper);
	}
	
	@Override
	public List<Achievement> getAchievementList(boolean flag, int wuId, int[] scoreIds){
		StringBuilder sql = new StringBuilder("select * from achievement where state=1 and scoreId in (");
		List<Object> params = new ArrayList<Object>();
		for (int scoreId : scoreIds) {
			sql.append("?,");
			params.add(scoreId);
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
	public List<Map<String, Object>> queryAchievementByPage(int page,
			int groupId, String key) {
		List<Object> params = new ArrayList<Object>();
		String sql = "SELECT LEFT(t1.ctime,10) cDate,fileId,t2.fileFormat,t2.fileAddr,t2.fileType,t2.fileSize,t2.fileName"
				+ ",CASE WHEN t4.director THEN '班主任' WHEN t4.teacher THEN IF(t4.subjects IS NULL, t3.realName "
				+ ", CONCAT(t4.subjects,'老师')) WHEN t4.patriarch THEN t4.aliasName END AS realName,t1.remark,t1.scoreId,d.openId FROM achievement t1 "
				+ "LEFT JOIN files t2 ON t2.storageTable='achievement' AND t2.id=t1.scoreId "
				+ "LEFT JOIN weixin_user t3 ON t1.wuId=t3.wuId "
				+ "LEFT JOIN wx_user_oauth AS d ON t3.unionId=d.unionId "
				+ "LEFT JOIN (SELECT * FROM user_group WHERE groupId=? AND state=1 GROUP BY wuId) t4 ON t4.wuId=t3.wuId "
				+ "WHERE t1.groupId=? AND t1.state=1";
		params.add(groupId);
		params.add(groupId);
		if(StringUtil.isNotEmpty(key)){
			sql += " AND (t2.fileName LIKE ? OR t3.realName LIKE ?)";
			params.add(StringUtil.toLikeStr(key));
			params.add(StringUtil.toLikeStr(key));
		}
		sql += " ORDER BY scoreId DESC LIMIT ?,?";
		params.add((page-1)*20);
		params.add(20);
		return daoSupport.queryForList(sql, params.toArray());
	}

}
