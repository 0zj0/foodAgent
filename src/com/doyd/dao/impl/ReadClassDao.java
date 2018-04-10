package com.doyd.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IReadClassDao;
import com.doyd.dao.rowmapper.ReadClassRowmapper;
import com.doyd.model.ReadClass;

@Repository
public class ReadClassDao implements IReadClassDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private ReadClassRowmapper mapper;
	@Override
	public List<Map<String, Object>> getReadUserList(int groupId, int workId) {
		String sql = "SELECT c.wuId,a.aliasName,c.avatarUrl FROM (SELECT * FROM user_group WHERE groupId=? AND director=0 " +
				"AND teacher=0 AND patriarch=1 AND state=1 GROUP BY wuId,groupId) AS a "+
				"INNER JOIN (SELECT * FROM read_class WHERE groupId=? AND notifyId=? GROUP BY groupId,wuId,notifyId) AS b "+
				"ON a.wuId=b.wuId LEFT JOIN weixin_user AS c ON a.wuId=c.wuId";
		return daoSupport.queryForList(sql, groupId, groupId, workId);
	}
	
	@Override
	public boolean isRead(int notifyId, int wuId) {
		String sql = "SELECT readId FROM read_class WHERE notifyId=? AND wuId=?";
		return daoSupport.queryForExist(sql, new Object[]{notifyId, wuId});
	}
	
	@Override
	public boolean addReadClass(ReadClass rc) {
		String sql = "INSERT INTO read_class(groupId,notifyId,wuId,`interval`,readDate,readHour,ctime) VALUES(?,?,?,?,?,?,NOW())";
		Object[] params = new Object[]{
				rc.getGroupId(), rc.getNotifyId(), rc.getWuId()
				, rc.getInterval(), rc.getReadDate(), rc.getReadHour()
		};
		rc.setReadId(daoSupport.insert(sql, params));
		return rc.getReadId() > 0;
	}

}
