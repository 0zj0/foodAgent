package com.doyd.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IReadWorkDao;
import com.doyd.dao.rowmapper.ReadWorkRowmapper;
import com.doyd.model.ReadWork;

@Repository
public class ReadWorkDao implements IReadWorkDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private ReadWorkRowmapper mapper;
	@Override
	public List<Map<String, Object>> getReadUserList(int groupId, int workId) {
		String sql = "SELECT c.wuId,a.aliasName,c.avatarUrl FROM (SELECT * FROM user_group WHERE groupId=? AND director=0 " +
				"AND teacher=0 AND patriarch=1 AND state=1 GROUP BY wuId,groupId) AS a "+
				"INNER JOIN (SELECT * FROM read_work WHERE groupId=? AND workId=? GROUP BY groupId,wuId,workId) AS b "+
				"ON a.wuId=b.wuId LEFT JOIN weixin_user AS c ON a.wuId=c.wuId ";
		return daoSupport.queryForList(sql, groupId, groupId, workId);
	}
	@Override
	public boolean isRead(int workId, int wuId) {
		String sql = "SELECT readId FROM read_work WHERE workId=? AND wuId=?";
		return daoSupport.queryForExist(sql, new Object[]{workId, wuId});
	}
	@Override
	public boolean addReadWork(ReadWork rw) {
		String sql = "INSERT INTO read_work(groupId,workId,wuId,`interval`,readDate,readHour,ctime) VALUES(?,?,?,?,?,?,NOW())";
		Object[] params = new Object[]{
				rw.getGroupId(), rw.getWorkId(), rw.getWuId()
				, rw.getInterval(), rw.getReadDate(), rw.getReadHour()
		};
		rw.setReadId(daoSupport.insert(sql, params));
		return rw.getReadId() > 0;
	}

}
