package com.doyd.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.action.common.Page;
import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.dao.rowmapper.WeixinUserRowmapper;
import com.doyd.model.WeixinUser;
import org.doyd.utils.StringUtil;

@Repository
public class WeixinUserDao implements IWeixinUserDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private WeixinUserRowmapper mapper;

	@Override
	public boolean addWeixinUser(WeixinUser wu) {
		String sql = "INSERT INTO weixin_user(unionId,nickName,gender,country,province"
				+ ",city,avatarUrl,`timestamp`,source,regDate,loginDate,ctime) VALUES(?,?,?,?,?,?,?,?,?,?,?,NOW())";
		Object[] params = new Object[]{
				wu.getUnionId(), wu.getNickName()
				, wu.getGender(), wu.getCountry(), wu.getProvince(), wu.getCity()
				, wu.getAvatarUrl(), wu.getTimestamp(), wu.getSource(), wu.getRegDate()
				, wu.getLoginDate()
		};
		wu.setWuId(daoSupport.insert(sql, params));
		return wu.getWuId() > 0;
	}

	@Override
	public boolean updateWeixinUser(WeixinUser wu) {
		String sql = "UPDATE weixin_user SET unionId=?,nickName=?,gender=?,country=?,province=?" +
				",city=?,avatarUrl=?,`timestamp`=?,loginDate=? WHERE wuId=?";
		Object[] params = new Object[]{
				wu.getUnionId(), wu.getNickName(), wu.getGender(), wu.getCountry()
				, wu.getProvince(), wu.getCity(), wu.getAvatarUrl(), wu.getTimestamp()
				, wu.getLoginDate(), wu.getWuId()
		};
		return daoSupport.update(sql, params) > 0;
	}

	@Override
	public boolean login(int wuId, long timestamp, int loginDate) {
		String sql = "UPDATE weixin_user SET `timestamp`=?,loginDate=? WHERE wuId=?";
		return daoSupport.update(sql, timestamp, loginDate, wuId) > 0;
	}

	@Override
	public WeixinUser getUser(String openId) {
		String sql = "SELECT t1.*,t2.openId FROM weixin_user t1 "
				+ "LEFT JOIN wx_user_oauth t2 ON t1.unionId=t2.unionId "
				+ "WHERE t2.openId=?";
		return daoSupport.queryForObject(sql, new Object[]{openId}, mapper);
	}
	
	@Override
	public WeixinUser getUserByUnionId(String unionId){
		String sql = "SELECT t1.*,1 openId FROM weixin_user t1 WHERE unionId=?";
		return daoSupport.queryForObject(sql, new Object[]{unionId}, mapper);
	}

	@Override
	public WeixinUser getUserById(int wuId) {
		String sql = "SELECT *,1 openId FROM weixin_user WHERE wuId=?";
		return daoSupport.queryForObject(sql, new Object[]{wuId}, mapper);
	}

	@Override
	public List<Map<String, Object>> getWeixinUserList(Page page, int groupId,
			int type, String key, int wuId) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM (SELECT MIN(ugId) AS ugId, " +
				"GROUP_CONCAT(aliasName SEPARATOR '|') AS aliasName FROM user_group " +
				"WHERE state=1 AND groupId=? GROUP BY wuId,groupId) t " +
				"LEFT JOIN user_group AS a ON t.ugId=a.ugId LEFT JOIN weixin_user AS b ON a.wuId=b.wuId WHERE 1=1 ");
		List<Object> params = new ArrayList<Object>();
		params.add(groupId);
		if(type>0){
			if(type==1){//家长
				sql.append(" AND a.patriarch=1");
			}
			if(type==2){//老师
				sql.append(" AND a.teacher=1");
			}
			if(type==3){//班主任
				sql.append(" AND a.director=1");
			}
		}
		if(StringUtil.isNotEmpty(key)){
			sql.append(" AND (t.aliasName LIKE ? OR b.realName LIKE ? OR b.nickName LIKE ? OR a.phone LIKE ?)");
			params.add(StringUtil.toLikeStr(key));
			params.add(StringUtil.toLikeStr(key));
			params.add(StringUtil.toLikeStr(key));
			params.add(StringUtil.toLikeStr(key));
		}
		page.setTotalSize(daoSupport.queryForInt(sql.toString(), params.toArray()));
		
		sql = new StringBuilder("SELECT a.ugId, a.phone, a.director, a.teacher, a.patriarch, t.aliasName, " +
				"a.subjects, b.realName, b.avatarUrl, b.nickName, IF(a.wuId=?, TRUE, FALSE) AS flag "+
				"FROM (SELECT MIN(ugId) AS ugId, GROUP_CONCAT(aliasName SEPARATOR '|') AS aliasName " +
				"FROM user_group WHERE state=1 AND groupId=? GROUP BY wuId,groupId) t "+
				"LEFT JOIN user_group AS a ON t.ugId=a.ugId LEFT JOIN weixin_user AS b ON a.wuId=b.wuId WHERE 1=1 ");
		params.add(0, wuId);
		if(type>0){
			if(type==1){//家长
				sql.append(" AND a.patriarch=1");
			}
			if(type==2){//老师
				sql.append(" AND a.teacher=1");
			}
			if(type==3){//班主任
				sql.append(" AND a.director=1");
			}
		}
		if(StringUtil.isNotEmpty(key)){
			sql.append(" AND (t.aliasName LIKE ? OR b.realName LIKE ? OR b.nickName LIKE ? OR a.phone LIKE ?)");
		}
		sql.append(" LIMIT ?,?");
		params.add(page.getCursor());
		params.add(page.getPerSize());
		return daoSupport.queryForList(sql.toString(), params.toArray());
	}

	@Override
	public boolean addIdentity(WeixinUser wu) {
		String sql = "UPDATE weixin_user SET realName=?,patriarch=?,teacher=?,director=? WHERE wuId=?";
		Object[] params = new Object[]{
				wu.getRealName(), wu.isPatriarch(), wu.isTeacher()
				, wu.isDirector(), wu.getWuId()
		};
		return daoSupport.update(sql, params) > 0;
	}

	@Override
	public boolean updateUser(int wuId, String realName, String phone) {
		String sql = "UPDATE weixin_user SET realName=?,phone=? WHERE wuId=?";
		return daoSupport.update(sql, realName, phone, wuId) > 0;
	}

	@Override
	public List<WeixinUser> getWeixinUserList(int groupId) {
		String sql = "SELECT t2.*,1 openId FROM user_group t1 "
				+ "LEFT JOIN weixin_user t2 ON t1.wuId=t2.wuId "
				+ "WHERE t1.groupId=? AND t1.state=1 GROUP BY t2.wuId ORDER BY t1.ugId";
		return daoSupport.query(sql, new Object[]{groupId}, mapper);
	}

}
