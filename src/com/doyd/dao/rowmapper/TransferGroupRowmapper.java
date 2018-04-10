package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.TransferGroup;

@Component
public class TransferGroupRowmapper implements RowMapper<TransferGroup> {

	public TransferGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
		TransferGroup model = new TransferGroup();
		model.setTransferId(rs.getInt("transferId"));
		model.setGroupId(rs.getInt("groupId"));
		model.setTransferWuId(rs.getInt("transferWuId"));
		model.setAcceptWuId(rs.getInt("acceptWuId"));
		model.setTransferTime(rs.getLong("transferTime"));
		model.setAcceptTime(rs.getLong("acceptTime"));
		model.setTransferState(rs.getInt("transferState"));
		return model;
	}
}
