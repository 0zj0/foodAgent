package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.ReadLeave;

/**
 * @author ylb
 * @version 创建时间：2018-3-7 下午7:21:05
 */
@Component
public class ReadLeaveRowmapper implements RowMapper<ReadLeave>{

	@Override
	public ReadLeave mapRow(ResultSet rs, int arg1) throws SQLException {
		ReadLeave model = new ReadLeave();
		model.setReadId(rs.getInt("readId"));
		model.setGroupid(rs.getInt("groupId"));
		model.setLeaveId(rs.getInt("leaveId"));
		model.setWuId(rs.getInt("wuId"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
	
}
