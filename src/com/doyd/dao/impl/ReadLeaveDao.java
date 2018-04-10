package com.doyd.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IReadLeaveDao;
import com.doyd.dao.rowmapper.ReadLeaveRowmapper;
import com.doyd.model.ReadLeave;

/**
 * @author ylb
 * @version 创建时间：2018-3-7 下午7:26:50
 */
@Repository
public class ReadLeaveDao implements IReadLeaveDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private ReadLeaveRowmapper readLeaveRowmapper;
	
	@Override
	public boolean isRead(int leaveId, int wuId) {
		String sql = "SELECT readId FROM read_leave WHERE leaveId=? AND wuId=?";
		return daoSupport.queryForExist(sql, new Object[]{leaveId, wuId});
	}
	
	@Override
	public boolean addReadLeave(ReadLeave rl) {
		String sql = "INSERT INTO read_leave(groupId,leaveId,wuId,ctime) VALUES(?,?,?,NOW())";
		Object[] params = new Object[]{
				rl.getGroupid(), rl.getLeaveId(), rl.getWuId()
		};
		rl.setReadId(daoSupport.insert(sql, params));
		return rl.getReadId() > 0;
	}

}
