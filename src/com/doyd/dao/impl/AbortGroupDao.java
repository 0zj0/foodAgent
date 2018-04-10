package com.doyd.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IAbortGroupDao;
import com.doyd.dao.rowmapper.AbortGroupRowmapper;
import com.doyd.model.AbortGroup;

@Repository
public class AbortGroupDao implements IAbortGroupDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private AbortGroupRowmapper mapper;
	
	@Override
	public boolean addAbortGroup(List<AbortGroup> agList) {
		if(agList == null || agList.size() <= 0){
			return true;
		}
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("INSERT INTO abort_group(groupId,ugId,wuId" +
				",identities,abortType,operatorId,ctime) VALUES");
		for (AbortGroup ag : agList) {
			sql.append("(?,?,?,?,?,?,NOW()),");
			params.add(ag.getGroupId());
			params.add(ag.getUgId());
			params.add(ag.getWuId());
			params.add(ag.getIdentities());
			params.add(ag.getAbortType());
			params.add(ag.getOperatorId());
		}
		sql.deleteCharAt(sql.length()-1);
		return daoSupport.update(sql.toString(), params.toArray()) > 0;
	}
	
	@Override
	public boolean addAbortGroup(AbortGroup ag){
		String sql = "INSERT INTO abort_group(groupId,ugId,wuId" +
				",identities,abortType,operatorId,ctime) VALUES(?,?,?,?,?,?,NOW())";
		Object[] params = new Object[]{
				ag.getGroupId(), ag.getUgId(), ag.getWuId()
				, ag.getIdentities(), ag.getAbortType(), ag.getOperatorId()
		};
		ag.setAbortId(daoSupport.insert(sql, params));
		return ag.getAbortId() > 0;
	}

	@Override
	public AbortGroup getAbortGroup(int groupId, int wuId) {
		String sql = "SELECT * FROM abort_group WHERE groupId=? AND wuId=? ORDER BY abortId DESC LIMIT 1";
		return daoSupport.queryForObject(sql, new Object[]{groupId, wuId}, mapper);
	}

}
