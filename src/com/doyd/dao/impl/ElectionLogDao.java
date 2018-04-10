package com.doyd.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IElectionLogDao;
import com.doyd.dao.rowmapper.ElectionLogRowmapper;
import com.doyd.model.ElectionLog;

@Repository
public class ElectionLogDao implements IElectionLogDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private ElectionLogRowmapper mapper;
	
	@Override
	public boolean exist(int electionId, int wuId) {
		String sql = "SELECT recordId FROM election_log WHERE electionId=? AND wuId=?";
		return daoSupport.queryForExist(sql, new Object[]{electionId, wuId});
	}
	
	@Override
	public ElectionLog getElectionLog(int electionId, int wuId){
		String sql = "SELECT * FROM election_log WHERE electionId=? AND wuId=?";
		return daoSupport.queryForObject(sql, new Object[]{electionId, wuId}, mapper);
	}

	@Override
	public boolean addLog(ElectionLog log) {
		String sql = "INSERT INTO election_log(electionId,wuId,agree,ctime) VALUES(?,?,?,NOW())";
		Object[] params = new Object[]{
				log.getElectionId(), log.getWuId(), log.getAgree()
		};
		log.setRecordId(daoSupport.insert(sql, params));
		return log.getRecordId() > 0;
	}

}
