package com.doyd.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.action.common.Page;
import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.dao.rowmapper.WeixinGroupRowmapper;
import com.doyd.model.WeixinGroup;

@Repository
public class WeixinGroupDao implements IWeixinGroupDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private WeixinGroupRowmapper mapper;
	
	@Override
	public WeixinGroup getWeixinGroup(String openGId) {
		String sql = "SELECT * FROM weixin_group WHERE openGId=? AND state<=2";
		return daoSupport.queryForObject(sql, new Object[]{openGId}, mapper);
	}

	@Override
	public WeixinGroup getWeixinGroup(int groupId) {
		String sql = "SELECT * FROM weixin_group WHERE groupId=? AND state<=2";
		return daoSupport.queryForObject(sql, new Object[]{groupId}, mapper);
	}

	@Override
	public List<Map<String, Object>> getWeixinGroupList(int wuId) {
		String sql = "SELECT a.openGId, a.groupName, b.newLeave, a.totalWork, a.totalNotify, a.totalShare, a.totalLeave, a.totalAchievement, " +
				"a.peopleCnt, if(b.director=1,3,if(b.teacher=1,2,1)) as power FROM weixin_group AS a INNER JOIN " +
				"(SELECT * FROM user_group WHERE state=1 AND wuId=? AND (director=1 OR teacher=1) GROUP BY groupId) AS b ON a.groupId=b.groupId " +
				"WHERE a.state!=3 order by b.newLeave desc, a.groupId asc";
		return daoSupport.queryForList(sql, new Object[]{wuId});
	}

	@Override
	public List<Map<String, Object>> getWeixinGroupByPage(Page page, int wuId) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM weixin_group AS a INNER JOIN (SELECT * FROM user_group WHERE state=1 AND wuId=? AND (director=1 OR teacher=1) GROUP BY groupId) AS b " +
				" ON a.groupId=b.groupId WHERE a.state!=3");
		List<Object> params = new ArrayList<Object>();
		params.add(wuId);
		page.setTotalSize(daoSupport.queryForInt(sql.toString(), params.toArray()));
		sql = new StringBuilder("SELECT a.openGId, a.groupPic, a.groupName, a.totalWork, a.totalNotify, a.totalShare, a.totalLeave, a.totalAchievement, " +
				"a.peopleCnt, IF(b.director=1, '班主任', CONCAT(if(b.subjects is null, '', b.subjects), '老师')) as identity FROM weixin_group AS a INNER JOIN " +
				"(SELECT * FROM user_group WHERE state=1 AND wuId=? AND (director=1 OR teacher=1) GROUP BY groupId) AS b ON a.groupId=b.groupId WHERE a.state!=3 ");
		sql.append(" limit ?,?");
		params.add(page.getCursor());
		params.add(page.getPerSize());
		return daoSupport.queryForList(sql.toString(), params.toArray());
	}

	@Override
	public boolean addWeixinGroup(WeixinGroup wg) {
		String sql = "INSERT INTO weixin_group(openGId,groupName,directorId,ctime) VALUES(?,?,?,NOW())";
		Object[] params = new Object[]{
				wg.getOpenGId(), wg.getGroupName(), wg.getDirectorId()
		};
		wg.setGroupId(daoSupport.insert(sql, params));
		return wg.getGroupId() > 0;
	}

	@Override
	public boolean updateDirectorId(int groupId, int wuId) {
		String sql = "UPDATE weixin_group SET directorId=? WHERE groupId=? AND state<=2";
		return daoSupport.update(sql, wuId, groupId) > 0;
	}

	@Override
	public boolean unbindDirector(int groupId, int wuId) {
		String sql = "UPDATE weixin_group SET directorId=0 WHERE groupId=? AND state<=2 and directorId=?";
		return daoSupport.update(sql, groupId, wuId) > 0;
	}

	@Override
	public List<Map<String, Object>> getUserGroupList(int wuId, int type, int childId) {
		List<Object> params = new ArrayList<Object>();
		String sql = "SELECT t1.groupId,t1.openGId,t1.groupName,t1.groupPic FROM weixin_group t1 "
				+ "INNER JOIN (SELECT * FROM user_group WHERE wuId=?";
		params.add(wuId);
		if(type == 1){
			sql += " AND groupId NOT IN (SELECT groupId FROM user_group WHERE wuId=? AND director=1 AND state=1)";
			params.add(wuId);
		}else if(type == 2){
			sql += " AND groupId NOT IN (SELECT groupId FROM user_group WHERE wuId=? AND teacher=1 AND state=1)";
			params.add(wuId);
		}else if(type == 3 && childId > 0){
			sql += " AND groupId NOT IN (SELECT groupId FROM user_group WHERE wuId=? AND childId=? AND state=1)";
			params.add(wuId);
			params.add(childId);
		}
		sql += " AND state=1 GROUP BY groupId) t2 ON t1.groupId=t2.groupId"
				+ " WHERE t1.state!=3 AND t2.state=1 AND t2.wuId=?";
		if(type == 1){
			sql += " AND t1.directorId=0";
		}
		params.add(wuId);
		return daoSupport.queryForList(sql, params.toArray());
	}
	
	@Override
	public boolean updatePeopleCnt(String openGId, int type, int cnt){
		String sql = "UPDATE weixin_group SET";
		if(type == 1){
			sql += " peopleCnt=peopleCnt+" + cnt;
		}else{
			sql += " peopleCnt=peopleCnt-" + cnt;
		}
		sql += " WHERE openGId=? AND state<=2";
		return daoSupport.update(sql, openGId) > 0;
	}
	@Override
	public boolean updatePeopleCnt(List<String> openGIdList, int type) {
		if(openGIdList == null || openGIdList.size() <= 0){
			return true;
		}
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("UPDATE weixin_group SET");
		if(type == 1){
			sql.append(" peopleCnt=peopleCnt+1");
		}else{
			sql.append(" peopleCnt=peopleCnt-1");
		}
		sql.append(" WHERE openGId IN (");
		for (String str : openGIdList) {
			sql.append("?,");
			params.add(str);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(") AND state<2");
		return daoSupport.update(sql.toString(), params.toArray()) > 0;
	}

	@Override
	public boolean update(WeixinGroup wg){
		String sql = "UPDATE weixin_group SET groupPic=?,picCnt=? WHERE groupId=?";
		Object[] params = new Object[]{
				wg.getGroupPic(), wg.getPicCnt(), wg.getGroupId()
		};
		return daoSupport.update(sql, params) > 0;
	}
	@Override
	public boolean upCnt(int groupId, String field) {
		String sql = "update weixin_group set " + field + "=" + field + "+1 where groupId=? and state<2";
		return daoSupport.update(sql, groupId) > 0;
	}

	@Override
	public Boolean downCnt(int groupId, String field) {
		String sql = "update weixin_group set " + field + "=" + field + "-1 where groupId=? and " + field + ">0 and state=1";
		return daoSupport.update(sql, groupId) > 0;
	}

	@Override
	public Boolean downCnt(int groupId, String field, int cnt) {
		String sql = "update weixin_group set " + field + "=" + field + "-? where groupId=? and " + field + ">=? and state=1";
		return daoSupport.update(sql, cnt, groupId, cnt) > 0;
	}

	@Override
	public boolean updateGroupName(int groupId, String groupName) {
		String sql = "UPDATE weixin_group SET groupName=? WHERE groupId=? AND state<2";
		return daoSupport.update(sql, groupName, groupId) > 0;
	}

}
