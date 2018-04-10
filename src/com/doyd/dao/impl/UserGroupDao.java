package com.doyd.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.rowmapper.UserGroupRowmapper;
import com.doyd.model.UserGroup;
import org.doyd.utils.StringUtil;

@Repository
public class UserGroupDao implements IUserGroupDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private UserGroupRowmapper mapper;
	
	@Override
	public UserGroup getUserGroup(int wuId, int groupId) {
		String sql = "SELECT t1.* FROM user_group t1 WHERE t1.wuId=? AND t1.groupId=? AND state=1";
		return daoSupport.queryForObject(sql, new Object[]{wuId, groupId}, mapper);
	}
	
	@Override
	public boolean userInGroup(int wuId, int groupId){
		String sql = "SELECT ugId FROM user_group WHERE wuId=? AND groupId=? AND state=1";
		return daoSupport.queryForExist(sql, new Object[]{wuId, groupId});
	}
	
	@Override
	public boolean userInGroup(String openId, int groupId){
		String sql = "SELECT ugId FROM user_group t1 "
				+ "LEFT JOIN weixin_user t2 ON t1.wuId=t2.wuId "
				+ "LEFT JOIN wx_user_oauth AS d ON t2.unionId=d.unionId "
				+ "WHERE d.openId=? AND groupId=? AND state=1";
		return daoSupport.queryForExist(sql, new Object[]{openId, groupId});
	}

	@Override
	public List<Map<String, Object>> getUserGroupList(int groupId, String key) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("SELECT t1.ugId,t2.nickName,t2.realName,t1.subjects"
					+ ",GROUP_CONCAT(aliasName SEPARATOR '|') aliasName,t2.avatarUrl,d.openId"
					+ ",t1.director,t1.patriarch"
					+ ",t1.teacher,IF(t1.phone IS NULL, t2.phone, t1.phone) phone FROM user_group t1 "
					+ "LEFT JOIN weixin_user t2 ON t1.wuId=t2.wuId "
					+ "LEFT JOIN wx_user_oauth AS d ON t2.unionId=d.unionId "
					+ "WHERE t1.groupId=? AND t1.state=1 ");
		params.add(groupId);
		if(StringUtil.isNotEmpty(key)){
			sql.append(" AND (aliasName LIKE ? OR realName LIKE ? OR nickName LIKE ? OR t1.phone LIKE ?)");
			key = StringUtil.toLikeStr(key);
			params.add(key);
			params.add(key);
			params.add(key);
			params.add(key);
		}
		sql.append(" GROUP BY t2.wuId");
		return daoSupport.queryForList(sql.toString(), params.toArray());
	}

	@Override
	public boolean addUserGroup(UserGroup ug) {
		String sql = "INSERT INTO user_group(wuId,groupId,childId,patriarch,teacher,director,subjects,aliasName,phone,ctime) VALUES(?,?,?,?,?,?,?,?,?,NOW())";
		Object[] params = new Object[]{
				ug.getWuId(), ug.getGroupId(), ug.getChildId(), ug.isPatriarch(), ug.isTeacher()
				, ug.isDirector(), ug.getSubjects(), ug.getAliasName(), ug.getPhone()
		};
		ug.setUgId(daoSupport.insert(sql, params));
		return ug.getUgId() > 0;
	}
	
	@Override
	public boolean batchAddUserGroup(List<UserGroup> ugList){
		if(ugList == null || ugList.size() <= 0){
			return true;
		}
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("INSERT INTO user_group(wuId,groupId,childId,patriarch" +
				",teacher,director,subjects,aliasName,phone,ctime) VALUES");
		for (UserGroup ug : ugList) {
			sql.append("(?,?,?,?,?,?,?,?,?,NOW()),");
			params.add(ug.getWuId());
			params.add(ug.getGroupId());
			params.add(ug.getChildId());
			params.add(ug.isPatriarch());
			params.add(ug.isTeacher());
			params.add(ug.isDirector());
			params.add(ug.getSubjects());
			params.add(ug.getAliasName());
			params.add(ug.getPhone());
		}
		sql.deleteCharAt(sql.length()-1);
		return daoSupport.update(sql.toString(), params.toArray()) == ugList.size();
	}
	
	@Override
	public boolean batchUpdateUserGroup(List<UserGroup> ugList){
		if(ugList == null || ugList.size() <= 0){
			return true;
		}
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("UPDATE user_group SET childId = CASE ugId");
		for (UserGroup ug : ugList) {
			sql.append(" WHEN ? THEN ?");
			params.add(ug.getUgId());
			params.add(ug.getChildId());
		}
		sql.append(" END,patriarch=1,aliasName = CASE ugId");
		for (UserGroup ug : ugList) {
			sql.append(" WHEN ? THEN ?");
			params.add(ug.getUgId());
			params.add(ug.getAliasName());
		}
		sql.append(" END WHERE ugId IN (");
		for (UserGroup ug : ugList) {
			sql.append("?,");
			params.add(ug.getUgId());
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(") AND patriarch=0 AND state=1");
		return daoSupport.update(sql.toString(), params.toArray()) == ugList.size();
	}

	@Override
	public List<Map<String, Object>> getUserGroupListByWuId(int wuId, String openGId) {
		List<Object> params = new ArrayList<Object>();
		String sql = "SELECT t1.ugId,t2.openGId,t1.childId,t1.patriarch,t1.teacher,t1.director"
					+ ",t1.subjects,t1.newLeave,t1.newNotify"
					+ ",t1.newWork,t1.groupId,t2.groupName,t2.groupPic,t1.newUse";
		if(StringUtil.isEmpty(openGId)){
			sql += ",1 AS `match`";
		}else{
			sql += ",IF(t2.openGId=?,2,1) AS `match`";
			params.add(openGId);
		}
		sql += " FROM user_group t1 "
					+ "LEFT JOIN weixin_group t2 ON t1.groupId=t2.groupId "
					+ "WHERE wuId=? AND t1.state=1 ORDER BY `match` DESC,t1.groupId,t1.newUse desc";
		params.add(wuId);
		return daoSupport.queryForList(sql, params.toArray());
	}

	@Override
	public boolean update(UserGroup ug) {
		String sql = "UPDATE user_group SET childId=?,patriarch=?,teacher=?,director=?" +
				",subjects=?,aliasName=? WHERE ugId=?";
		Object[] params = new Object[]{
				ug.getChildId(), ug.isPatriarch(), ug.isTeacher(), ug.isDirector()
				, ug.getSubjects(), ug.getAliasName(), ug.getUgId()
		};
		return daoSupport.update(sql, params) > 0;
	}
	
	@Override
	public boolean updateIdentity(UserGroup ug, boolean director, boolean teacher, boolean patriarch){
		List<Object> params = new ArrayList<Object>();
		String sql = "UPDATE user_group SET ";
		if(director){
			sql += "director=?,teacher=?";
			params.add(ug.isDirector());
			params.add(ug.isTeacher());
		}else if(teacher){
			sql += "teacher=?,subjects=?";
			params.add(ug.isTeacher());
			params.add(ug.getSubjects());
		}else if(patriarch){
			sql += "childId=?,patriarch=?,aliasName=?";
			params.add(ug.getChildId());
			params.add(ug.isPatriarch());
			params.add(ug.getAliasName());
		}
		sql += " WHERE wuId=? AND groupId=? AND state=1";
		params.add(ug.getWuId());
		params.add(ug.getGroupId());
		return daoSupport.update(sql, params.toArray()) > 0;
	}

	@Override
	public boolean existUserGroup(int wuId, int groupId, int childId) {
		List<Object> params = new ArrayList<Object>();
		String sql = "SELECT ugId FROM user_group WHERE wuId=? AND groupId=? AND state=1";
		params.add(wuId);
		params.add(groupId);
		if(childId > 0 ){
			sql += " AND childId=?";
			params.add(childId);
		}
		return daoSupport.queryForExist(sql, params.toArray());
	}

	@Override
	public UserGroup getUserGroup(int ugId) {
		String sql = "SELECT * FROM user_group WHERE ugId=? AND state=1";
		return daoSupport.queryForObject(sql, new Object[]{ugId}, mapper);
	}

	@Override
	public boolean deleteUser(UserGroup userGroup) {
		String sql = "update user_group set patriarch=?,teacher=?," +
				"state=? where director!=1 and wuId=? and groupId=? and state=1";
		return daoSupport.update(sql, userGroup.isPatriarch(), 
				userGroup.isTeacher(), userGroup.getState(), 
				userGroup.getWuId(), userGroup.getGroupId())>0;
	}

	@Override
	public boolean batchDeleteUser(int[] ugIds, int groupId) {
		StringBuilder sql = new StringBuilder("update user_group set patriarch=0, teacher=0, state=3 where " +
				"director!=1 and patriarch+teacher=1 and groupId=? and ugId in (");
		List<Object> params = new ArrayList<Object>();
		params.add(groupId);
		for (int ugId : ugIds) {
			sql.append("?,");
			params.add(ugId);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		return daoSupport.update(sql.toString(), params.toArray())>=0;
	}

	@Override
	public boolean updateUser(UserGroup userGroup) {
		String sql = "update user_group set phone=? where wuId=? and groupId=? and state=1";
		return daoSupport.update(sql, userGroup.getPhone(), userGroup.getWuId(),
				userGroup.getGroupId()) > 0;
	}

	@Override
	public int getUserCnt(int groupId, int type) {
		StringBuilder sql = new StringBuilder("select count(*) from (select 1 from user_group where state=1 and groupId=?");
		if(type>0){
			if(type==1){
				sql.append(" and patriarch=1");
			}
			if(type==2){
				sql.append(" and teacher=1");
			}
			if(type==3){
				sql.append(" and director=1");
			}
		}
		sql.append(" group by wuId,groupId) as a");
		return daoSupport.queryForInt(sql.toString(), groupId);
	}
	
	@Override
	public int getUserGroupCnt(int wuId, int groupId){
		String sql = "SELECT COUNT(1) FROM user_group WHERE wuId=? AND groupId=? AND state=1";
		return daoSupport.queryForInt(sql, wuId, groupId);
	}

	@Override
	public boolean unbindUserGroup(int wuId, int groupId, int type, int childId) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("UPDATE user_group SET ");
		if(type == 1){
			sql.append("director=FALSE");
		}else if(type == 2){
			sql.append("teacher=FALSE,subjects=NULL");
		}else{
			sql.append("patriarch=FALSE,childId=0,aliasName=NULL");
		}
		sql.append(" WHERE wuId=? AND groupId=? AND state=1");
		params.add(wuId);
		params.add(groupId);
		if(type == 1){
			sql.append(" AND director=TRUE");
		}else if(type == 2){
			sql.append(" AND teacher=TRUE");
		}else{
			sql.append(" AND patriarch=TRUE AND childId=?");
			params.add(childId);
		}
		return daoSupport.update(sql.toString(), params.toArray())>0;
	}

	@Override
	public boolean deleteUserGroup(int wuId, int groupId, int childId) {
		List<Object> params = new ArrayList<Object>();
		String sql = "UPDATE user_group SET state=2 WHERE wuId=? AND groupId=? AND state=1";
		params.add(wuId);
		params.add(groupId);
		if(childId > 0){
			sql += " AND childId=?";
			params.add(childId);
		}
		return daoSupport.update(sql, params.toArray()) > 0;
	}

	@Override
	public List<Map<String, Object>> getAllUserList(int groupId) {
		String sql = "SELECT b.wuId,a.aliasName,b.avatarUrl FROM (SELECT * FROM user_group WHERE groupId=? AND director=0 " +
				"AND teacher=0 AND patriarch=1 AND state=1 GROUP BY wuId,groupId) AS a "+
				"LEFT JOIN weixin_user AS b ON a.wuId=b.wuId";
		return daoSupport.queryForList(sql, groupId);
	}

	@Override
	public boolean addIdentity(int wuId, int groupId, boolean director,
			boolean teacher) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("UPDATE user_group SET ");
		if(director){
			sql.append("director=TRUE");
		}else if(teacher){
			sql.append("teacher=TRUE");
		}
		sql.append(" WHERE wuId=? AND groupId=? AND state=1");
		params.add(wuId);
		params.add(groupId);
		return daoSupport.update(sql.toString(), params.toArray()) > 0;
	}

	@Override
	public boolean updateSubject(int wuId, int groupId, String subjects) {
		String sql = "UPDATE user_group SET subjects=? WHERE wuId=? AND groupId=? AND state=1";
		return daoSupport.update(sql, subjects, wuId, groupId) > 0;
	}

	@Override
	public List<UserGroup> getUserGroupList(int groupId, int type, String[] openIds) {
		if(openIds == null || openIds.length <= 0){
			return null;
		}
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("SELECT * FROM user_group t1 "
			+ "LEFT JOIN weixin_user t2 ON t1.wuId=t2.wuId "
			+ "LEFT JOIN wx_user_oauth AS d ON t2.unionId=d.unionId "
			+ "WHERE t1.groupId=?");
		params.add(groupId);
		if(type == 1){
			sql.append(" AND t1.teacher=TRUE");
		}else{
			sql.append(" AND t1.patriarch=TRUE");
		}
		sql.append(" AND d.openId IN (");
		for (int i = 0; i < openIds.length; i++) {
			sql.append("?,");
			params.add(openIds[i]);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(") ORDER BY openId");
		return daoSupport.query(sql.toString(), params.toArray(), mapper);
	}

	@Override
	public boolean deleteUserGroup(int groupId, List<Integer> wuIdList) {
		if(wuIdList == null || wuIdList.size() <= 0){
			return true;
		}
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("UPDATE user_group SET state=3 WHERE groupId=? AND state=1 AND wuId IN (");
		params.add(groupId);
		for (Integer wuId : wuIdList) {
			sql.append("?,");
			params.add(wuId);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		return daoSupport.update(sql.toString(), params.toArray()) >= 0;
	}

	@Override
	public boolean updateUserGroup(int groupId, List<Integer> wuIdList, int type) {
		if(wuIdList == null || wuIdList.size() <= 0){
			return true;
		}
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("UPDATE user_group SET ");
		if(type == 1){
			sql.append("teacher=0");
		}else{
			sql.append("patriarch=0");
		}
		sql.append(" WHERE groupId=? AND ");
		if(type == 1){
			sql.append("teacher=1");
		}else{
			sql.append("patriarch=1");
		}
		sql.append(" AND state=1 AND wuId IN (");
		params.add(groupId);
		for (Integer wuId : wuIdList) {
			sql.append("?,");
			params.add(wuId);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		return daoSupport.update(sql.toString(), params.toArray()) >= 0;
	}

	@Override
	public boolean deleteUserGroup(int groupId, Object[] ugIdList) {
		if(ugIdList == null || ugIdList.length <= 0){
			return true;
		}
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("UPDATE user_group SET state=3 WHERE groupId=? AND state=1 AND ugId IN (");
		params.add(groupId);
		for (int i = 0; i < ugIdList.length; i++) {
			sql.append("?,");
			params.add(ugIdList[i]);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		return daoSupport.update(sql.toString(), params.toArray()) >= 0;
	}

	@Override
	public List<UserGroup> getUserGroupList(int groupId) {
		String sql = "SELECT * FROM user_group WHERE groupId=? AND state=1 GROUP BY wuId";
		return daoSupport.query(sql, new Object[]{groupId}, mapper);
	}
	
	@Override
	public boolean upCnt(int groupId, String field, int type) {//wuId=? and 
		String sql = "update user_group set " + field + "=" + field + "+1 where groupId=? and state=1";
		if(type == 2){
			sql += " AND (director=1 OR teacher=1)";
		}else if(type == 3){
			sql += " AND patriarch=1";
		}
		return daoSupport.update(sql, groupId) >= 0;
	}

	@Override
	public Boolean downCnt(int wuId, int groupId, String field) {
		String sql = "update user_group set " + field + "=" + field + "-1 where wuId=? and groupId=? and " + field + ">0 and state=1";
		return daoSupport.update(sql, wuId, groupId) > 0;
	}
	
	@Override
	public boolean downCnt(int groupId, String field, String readTable, String joinField, int id){
		String sql = "UPDATE user_group AS a LEFT JOIN (SELECT * FROM " + readTable + " WHERE " + joinField + "=?) AS b ON a.groupId = b.groupId AND a.wuId = b.wuId SET a." + field + " = a." + field + " - 1 " +
				"WHERE a.groupId=? AND b." + joinField + " IS NULL AND a." + field + ">0";
		return daoSupport.update(sql, id, groupId) >= 0;
	}

	@Override
	public Boolean downCnt(int wuId, int groupId, String field, int cnt) {
		String sql = "update user_group set " + field + "=" + field + "-? where wuId=? and groupId=? and " + field + ">? and state=1";
		return daoSupport.update(sql, cnt, wuId, groupId, cnt) > 0;
	}
	
	@Override
	public List<UserGroup> getUserGroupList(int[] ugIds, int groupId) {
		StringBuilder sql = new StringBuilder("SELECT a.* FROM (SELECT * FROM user_group WHERE groupId=? AND state=1 AND ugId IN (");
		List<Object> params = new ArrayList<Object>();
		params.add(groupId);
		for (int ugId : ugIds) {
			sql.append("?,");
			params.add(ugId);
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(" GROUP BY wuId,groupId) AS a LEFT JOIN weixin_user AS b ON a.wuId=b.wuId");
		return daoSupport.query(sql.toString(), params.toArray(), mapper);
	}

	@Override
	public Boolean clearCnt(int wuId, int groupId, String field) {
		String sql = "update user_group set " + field + "=0 where wuId=? and groupId=? and state=1";
		return daoSupport.update(sql, wuId, groupId) > 0;
	}

	@Override
	public boolean updatePhone(int wuId, String phone) {
		String sql = "UPDATE user_group SET phone=? WHERE wuId=? AND phone IS NULL";
		return daoSupport.update(sql, phone, wuId) >= 0;
	}

	@Override
	public boolean updateNewUse(String openId) {
		String sql = "UPDATE user_group t1 "
				+ "LEFT JOIN weixin_user t2 ON t1.wuId=t2.wuId "
				+ "LEFT JOIN wx_user_oauth t3 ON t2.unionId=t3.unionId "
				+ "SET newUse=0 WHERE t3.openId=? AND t1.state=1";
		return daoSupport.update(sql, openId) >= 0;
	}

	@Override
	public boolean updateNewUse(String openId, String openGId) {
		String sql = "UPDATE user_group t1 "
				+ "LEFT JOIN weixin_user t2 ON t1.wuId=t2.wuId "
				+ "LEFT JOIN wx_user_oauth t3 ON t2.unionId=t3.unionId "
				+ "LEFT JOIN weixin_group t4 ON t1.groupId=t4.groupId "
				+ "SET newUse=1 WHERE t4.openGId=? AND t3.openId=? AND t1.state=1";
		return daoSupport.update(sql, openGId, openId) > 0;
	}

}
