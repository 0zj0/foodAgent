package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.ElectionLord;

@Component
public class ElectionLordRowmapper implements RowMapper<ElectionLord> {

	public ElectionLord mapRow(ResultSet rs, int rowNum) throws SQLException {
		ElectionLord model = new ElectionLord();
		model.setElectionId(rs.getInt("electionId"));
		model.setGroupId(rs.getInt("groupId"));
		model.setCandidateWuId(rs.getInt("candidateWuId"));
		model.setElectionType(rs.getInt("electionType"));
		model.setInitiateTime(rs.getLong("initiateTime"));
		model.setParticipant(rs.getInt("participant"));
		model.setParticipateCnt(rs.getInt("participateCnt"));
		model.setAgreeCnt(rs.getInt("agreeCnt"));
		model.setState(rs.getInt("state"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
