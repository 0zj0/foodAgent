package com.doyd.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.doyd.model.GroupMessage;

@Component
public class GroupMessageRowmapper implements RowMapper<GroupMessage> {

	public GroupMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
		GroupMessage model = new GroupMessage();
		model.setMsgId(rs.getInt("msgId"));
		model.setGroupId(rs.getInt("groupId"));
		model.setWuId(rs.getInt("wuId"));
		model.setMsgType(rs.getInt("msgType"));
		model.setTitle(rs.getString("title"));
		model.setContent(rs.getString("content"));
		model.setId(rs.getInt("id"));
		model.setPages(rs.getString("pages"));
		model.setIsRead(rs.getInt("isRead"));
		model.setCtime(rs.getString("ctime"));
		return model;
	}
}
