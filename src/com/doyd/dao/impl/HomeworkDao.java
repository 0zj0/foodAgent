package com.doyd.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.action.common.Page;
import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IHomeworkDao;
import com.doyd.dao.rowmapper.HomeworkRowmapper;
import com.doyd.model.Homework;
import org.doyd.utils.StringUtil;

@Repository
public class HomeworkDao implements IHomeworkDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private HomeworkRowmapper mapper;
	
	@Override
	public Homework getHomework(int workId) {
		String sql = "SELECT * FROM homework WHERE workId=? AND state=1";
		return daoSupport.queryForObject(sql, new Object[]{workId}, mapper);
	}

	@Override
	public List<Map<String, Object>> queryWorkByPage(Page page, int groupId,
			int type, boolean flag, int wuId, String key, String beginDate, String endDate) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM homework AS a " +
				"INNER JOIN (SELECT * FROM user_group WHERE state=1 AND groupId=? GROUP BY wuId,groupId) AS b ON a.groupId=b.groupId AND a.wuId=b.wuId " +
				"LEFT JOIN weixin_user AS c ON a.wuId=c.wuId WHERE a.state=1 AND a.groupId=?");
		List<Object> params = new ArrayList<Object>();
		params.add(groupId);
		params.add(groupId);
		if(!flag){
			sql.append(" AND a.wuId=?");
			params.add(wuId);
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
		if(StringUtil.isNotEmpty(key)){
			sql.append(" AND (c.realName LIKE ? OR a.work LIKE ?)");
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
		sql = new StringBuilder("SELECT a.workId,a.work,a.subject,a.storageTable,a.ctime,IF(b.director=1,'班主任',IF(b.teacher=1,IF(b.subjects IS NULL, IF(c.realName IS NULL, c.nickName, c.realName), CONCAT(b.subjects,'老师')),IF(c.realName IS NULL, c.nickName, c.realName))) AS name,IF(a.wuId=?, TRUE, FALSE) AS flag FROM homework AS a " +
				"INNER JOIN (SELECT * FROM user_group WHERE state=1 AND groupId=? GROUP BY wuId,groupId) AS b ON a.groupId=b.groupId AND a.wuId=b.wuId " +
				"LEFT JOIN weixin_user AS c ON a.wuId=c.wuId WHERE a.state=1 AND a.groupId=?");
		params.add(0, wuId);
		if(!flag){
			sql.append(" AND a.wuId=?");
		}
		if(type>0){
			if(type==1){
				sql.append(" AND a.wuId=?");
			}
			if(type==2){
				sql.append(" AND a.wuId!=?");
			}
		}
		if(StringUtil.isNotEmpty(key)){
			sql.append(" AND (c.realName LIKE ? OR a.work LIKE ?)");
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
		sql.append(" ORDER BY a.workId DESC LIMIT ?,?");
		params.add(page.getCursor());
		params.add(page.getPerSize());
		return daoSupport.queryForList(sql.toString(), params.toArray());
	}
	
	@Override
	public List<Map<String, Object>> queryWorkByPage(int page, int groupId, boolean isDirector, int wuId, int key){
		List<Object> params = new ArrayList<Object>();
		String sql = "SELECT t1.ctime,t3.realName,t1.subject,t1.work,t1.workId FROM homework t1 "
				+ "LEFT JOIN weixin_user t3 ON t1.wuId=t3.wuId "
				+ "WHERE groupId=? AND t1.state=1";
		params.add(groupId);
		if(!isDirector){
			sql += " AND t1.wuId=?";
			params.add(wuId);
		}else{
			if(key == 1){
				sql += " AND t1.wuId=?";
				params.add(wuId);
			}else if(key == 2){
				sql += " AND t1.wuId!=?";
				params.add(wuId);
			}
		}
		sql += " ORDER BY workId DESC LIMIT ?,?";
		params.add((page-1)*20);
		params.add(20);
		return daoSupport.queryForList(sql.toString(), params.toArray());
	}

	@Override
	public boolean addWork(Homework work) {
		String sql = "insert into homework (groupId,ugId,wuId,subject,work,fileCnt," +
				"storageTable,submitTime,costTime,ctime) values (?,?,?,?,?, ?,?,?,?,now())";
		work.setWorkId(daoSupport.insert(sql, work.getGroupId(), work.getUgId(), work.getWuId(),
				work.getSubject(), work.getWork(), work.getFileCnt(), work.getStorageTable(),
				work.getSubmitTime(), work.getCostTime()));
		return work.getWorkId()>0;
	}
	

	@Override
	public int getWorkCnt(int groupId, int wuId, int type) {
		String sql = "select count(*) from homework where state=1 and groupId=?";
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
	public boolean deleteWork(int[] workIds, int groupId, boolean flag, int wuId) {
		StringBuilder sql = new StringBuilder("update homework set state=2 where state=1 and groupId=? and workId in (");
		List<Object> params = new ArrayList<Object>();
		params.add(groupId);
		for (int workId : workIds) {
			sql.append("?,");
			params.add(workId);
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
	public List<Homework> getHomeworkList(boolean flag, int wuId, int[] workIds) {
		StringBuilder sql = new StringBuilder("select * from homework where state=1 and workId in (");
		List<Object> params = new ArrayList<Object>();
		for (int workId : workIds) {
			sql.append("?,");
			params.add(workId);
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
	public boolean readHomework(int workId) {
		String sql = "UPDATE homework SET readCnt=readCnt+1 WHERE workId=? AND state=1";
		return daoSupport.update(sql, workId) > 0;
	}

	@Override
	public List<Map<String, Object>> getHomework_notify(int groupId, int wuId,
			int page) {
		String sql = "SELECT t1.*,t2.realName,t2.avatarUrl,t3.director,t3.teacher,t3.subjects FROM ("
				+ "(SELECT hw.workId AS id,1 AS title,hw.ctime,hw.wuId,hw.subject,hw.submitTime,1 AS msgType"
				+ ",CASE WHEN rw.readId IS NULL THEN 0 ELSE 1 END AS isRead FROM homework hw "
				+ "LEFT JOIN read_work rw ON rw.wuId=? AND hw.workId=rw.workId "
				+ "WHERE hw.groupId=? AND hw.state=1 LIMIT ?) UNION ("
				+ "SELECT cn.notifyId AS id,cn.title,cn.ctime,cn.wuId,1 AS `subject`,1,2 AS msgType"
				+ ",CASE WHEN rc.readId IS NULL THEN 0 ELSE 1 END AS isRead FROM class_notify cn "
				+ "LEFT JOIN read_class rc ON rc.wuId=? AND rc.notifyId=cn.notifyId "
				+ "WHERE cn.groupId=? AND cn.state=1 LIMIT ?)) t1 "
				+ "LEFT JOIN weixin_user t2 ON t1.wuId=t2.wuId "
				+ "LEFT JOIN (SELECT * FROM user_group WHERE groupId=? AND state=1 GROUP BY wuId) t3 ON t2.wuId=t3.wuId "
				+ "GROUP BY t1.msgType,t1.id ORDER BY ctime DESC LIMIT ?,20";
		Object[] params = new Object[]{
				wuId, groupId, page*20
				, wuId, groupId, page*20
				, groupId, (page-1)*20
		};
		return daoSupport.queryForList(sql, params);
	}
	
}
