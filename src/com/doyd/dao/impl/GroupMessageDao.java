package com.doyd.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IGroupMessageDao;
import com.doyd.dao.rowmapper.GroupMessageRowmapper;
import com.doyd.model.GroupMessage;

@Repository
public class GroupMessageDao implements IGroupMessageDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private GroupMessageRowmapper mapper;
	
	@Override
	public List<Map<String, Object>> getGroupMessageList(int wuId) {
		String sql = "SELECT msgId,groupId,msgType,title,content,pages,id,isRead FROM group_message " +
				"WHERE wuId=? AND isRead<3 ORDER BY groupId,msgId DESC ";
		return daoSupport.queryForList(sql, new Object[]{wuId});
	}
	
	@Override
	public List<Map<String, Object>> getGroupMessageList(int wuId, int groupId){
		String sql = "SELECT msgId,groupId,msgType,title,content,pages FROM group_message " +
				"WHERE wuId=? AND groupId=? AND isRead<3 ORDER BY groupId,msgId DESC ";
		return daoSupport.queryForList(sql, new Object[]{wuId, groupId});
	}
	
	@Override
	public boolean push(int wuId){
		String sql = "UPDATE group_message SET isRead=2 WHERE wuId=? AND isRead=1";
		return daoSupport.update(sql, wuId) > 0;
	}

	@Override
	public GroupMessage getGroupMessage(int msgId) {
		String sql = "SELECT * FROM group_message WHERE msgId=?";
		return daoSupport.queryForObject(sql, new Object[]{msgId}, mapper);
	}

	@Override
	public boolean readMsg(int msgId) {
		String sql = "UPDATE group_message SET isRead=3 WHERE msgId=?";
		return daoSupport.update(sql, msgId) > 0;
	}

	@Override
	public boolean addGroupMessage(List<GroupMessage> gmList) {
		if(gmList == null || gmList.size() <= 0){
			return true;
		}
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("INSERT INTO group_message(groupId,wuId,msgType" +
				",title,content,ctime) VALUES");
		for (GroupMessage gm : gmList) {
			sql.append("(?,?,?,?,?,NOW()),");
			params.add(gm.getGroupId());
			params.add(gm.getWuId());
			params.add(gm.getMsgType());
			params.add(gm.getTitle());
			params.add(gm.getContent());
		}
		sql.deleteCharAt(sql.length()-1);
		return daoSupport.update(sql.toString(), params.toArray()) > 0;
	}

	@Override
	public boolean addGroupMessage(GroupMessage gm){
		String sql = "INSERT INTO group_message(groupId,wuId,msgType,title,content" +
				",id,ctime) VALUES(?,?,?,?,?,?,NOW())";
		Object[] params = new Object[]{
				gm.getGroupId(), gm.getWuId(), gm.getMsgType()
				, gm.getTitle(), gm.getContent(), gm.getId()
		};
		gm.setMsgId(daoSupport.insert(sql, params));
		return gm.getMsgId() > 0;
	}
}
