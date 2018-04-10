package com.doyd.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IElectionLordDao;
import com.doyd.dao.rowmapper.ElectionLordRowmapper;
import com.doyd.model.ElectionLord;

@Repository
public class ElectionLordDao implements IElectionLordDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private ElectionLordRowmapper mapper;
	
	@Override
	public ElectionLord getElectionLord(int electionId) {
		String sql = "SELECT * FROM election_lord WHERE electionId=?";
		return daoSupport.queryForObject(sql, new Object[]{electionId}, mapper);
	}
	
	@Override
	public ElectionLord getElectionLord(String openId, String openGId, int type){
		String sql = "SELECT t1.* FROM election_lord t1 "
				+ "LEFT JOIN weixin_user t2 ON t1.candidateWuId=t2.wuId "
				+ "LEFT JOIN wx_user_oauth AS d ON t2.unionId=d.unionId "
				+ "LEFT JOIN weixin_group t3 ON t1.groupId=t3.groupId "
				+ "WHERE d.openId=? AND t3.openGId=? AND electionType=? ORDER BY electionId DESC LIMIT 1";
		return daoSupport.queryForObject(sql, new Object[]{openId, openGId, type}, mapper);
	}
	
	@Override
	public List<ElectionLord> getElectionLordList(int wuId, int groupId) {
		String sql = "SELECT * FROM election_lord WHERE groupId=? AND candidateWuId=?";
		return daoSupport.query(sql, new Object[]{groupId, wuId}, mapper);
	}
	
	@Override
	public boolean addElectionLord(ElectionLord el) {
		String sql = "INSERT INTO election_lord(groupId,candidateWuId,electionType,initiateTime" +
				",participant,participateCnt,state,ctime) " +
				"VALUES(?,?,?,UNIX_TIMESTAMP(NOW(3))*1000,?,?,1,NOW())";
		Object[] params = new Object[]{
				el.getGroupId(), el.getCandidateWuId(), el.getElectionType()
				, el.getParticipant(), el.getParticipateCnt()
		};
		el.setElectionId(daoSupport.insert(sql, params));
		return el.getElectionId() > 0;
	}

	@Override
	public boolean update(int electionId, int result, int state) {
		String sql = "UPDATE election_lord SET participateCnt=participateCnt+1";
		if(result == 1){
			sql += ",agreeCnt=agreeCnt+1";
		}
		if(state > 0){
			sql += ",state=" + state;
		}
		sql += " WHERE electionId=? AND state=1";
		return daoSupport.update(sql, electionId) > 0;
	}

}
