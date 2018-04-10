package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.Leave;

@Component
public class LeaveRowmapper implements RowMapper<Leave> {

	public Leave mapRow(ResultSet rs, int rowNum) throws SQLException {
		Leave model = new Leave();
		model.setLeaveId(rs.getInt("leaveId"));
		model.setGroupId(rs.getInt("groupId"));
		model.setChildId(rs.getInt("childId"));
		model.setLeaveWuId(rs.getInt("leaveWuId"));
		model.setReason(rs.getString("reason"));
		model.setStartTime(rs.getString("startTime"));
		model.setEndTime(rs.getString("endTime"));
		model.setAuditWuId(rs.getInt("auditWuId"));
		model.setAuditTime(rs.getString("auditTime"));
		model.setAuditResult(rs.getString("auditResult"));
		model.setAuditState(rs.getInt("auditState"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
