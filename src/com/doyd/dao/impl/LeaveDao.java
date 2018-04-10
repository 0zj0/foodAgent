package com.doyd.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.action.common.Page;
import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.ILeaveDao;
import com.doyd.dao.rowmapper.LeaveRowmapper;
import com.doyd.model.Leave;
import org.doyd.utils.StringUtil;

@Repository
public class LeaveDao implements ILeaveDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private LeaveRowmapper mapper;
	
	@Override
	public Leave getLeaveById(int leaveId) {
		String sql = "select * from `leave` where leaveId=?";
		return daoSupport.queryForObject(sql, new Object[]{leaveId}, mapper);
	}
	@Override
	public List<Map<String, Object>> queryLeaveByPage(Page page, int groupId,
			int type, String key) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM `leave` AS a " +
				"LEFT JOIN children AS b ON a.childId=b.childId WHERE a.groupId=?");
		List<Object> params = new ArrayList<Object>();
		params.add(groupId);
		if(type>0){
			if(type==1){//待审批
				sql.append(" AND a.auditState=1");
			}
			if(type==2){//审批通过
				sql.append(" AND a.auditState=3");
			}
			if(type==3){//审批不通过
				sql.append(" AND a.auditState=4");
			}
		}
		if(StringUtil.isNotEmpty(key)){
			sql.append(" AND (b.childName LIKE ? OR a.reason LIKE ?)");
			params.add(StringUtil.toLikeStr(key));
			params.add(StringUtil.toLikeStr(key));
		}
		page.setTotalSize(daoSupport.queryForInt(sql.toString(), params.toArray()));
		sql = new StringBuilder("SELECT a.leaveId,a.auditState,a.auditResult,a.startTime,a.endTime,a.reason,b.childName as leaveName FROM `leave` AS a " +
				"LEFT JOIN children AS b ON a.childId=b.childId WHERE a.groupId=?");
		if(type>0){
			if(type==1){//待审批
				sql.append(" AND a.auditState=1");
			}
			if(type==2){//审批通过
				sql.append(" AND a.auditState=3");
			}
			if(type==3){//审批不通过
				sql.append(" AND a.auditState=4");
			}
		}
		if(StringUtil.isNotEmpty(key)){
			sql.append(" AND (b.childName LIKE ? OR a.reason LIKE ?)");
		}
		sql.append(" ORDER BY a.auditState ASC, leaveId DESC LIMIT ?,?");
		params.add(page.getCursor());
		params.add(page.getPerSize());
		return daoSupport.queryForList(sql.toString(), params.toArray());
	}
	@Override
	public boolean updateLeave(Leave leave) {
		String sql = "update `leave` set auditState=?, auditResult=?, auditWuId=?, auditTime=now() where leaveId=? AND auditState<=2";
		return daoSupport.update(sql, leave.getAuditState(), leave.getAuditResult(), leave.getAuditWuId(), leave.getLeaveId()) > 0;
	}
	@Override
	public int getLeaveCnt(int groupId, int type) {
		String sql = "select count(*) from `leave` where groupId=?";
		List<Object> params = new ArrayList<Object>();
		params.add(groupId);
		if(type>0){
			if(type==1){//待审批
				sql+=" and auditState=1";
			}
			if(type==2){//已审批
				sql+=" and auditState>2";
			}
		}
		return daoSupport.queryForInt(sql, params.toArray());
	}
	
	@Override
	public List<Map<String, Object>> getLeaveList(int wuId, int groupId,
			int childId, int type, int state, int page) {
		List<Object> params = new ArrayList<Object>();
		String sql = "SELECT t1.auditResult,t2.childName,t1.ctime,t1.endTime,t1.startTime"
				+ ",t1.leaveId,t1.reason,t1.auditState FROM `leave` t1 "
				+ "LEFT JOIN children t2 ON t1.childId=t2.childId "
				+ "WHERE t1.groupId=?";
		params.add(groupId);
		//如果传入的身份是家长
		if(type == 3){
			sql += " AND t1.leaveWuId=? AND t1.childId=?";
			params.add(wuId);
			params.add(childId);
		}
		if(state > 0){
			if(state == 1){
				sql += " AND t1.auditState<=2";
			}else{
				sql += " AND t1.auditState=?";
				params.add(state+1);
			}
		}
		sql += " ORDER BY t1.leaveId DESC LIMIT ?,?";
		params.add((page-1)*20);
		params.add(20);
		return daoSupport.queryForList(sql, params.toArray());
	}

	@Override
	public boolean addLeave(Leave leave) {
		String sql = "INSERT INTO `leave`(groupId,childId,leaveWuId,reason,startTime,endTime,ctime) VALUES(?,?,?,?,?,?,NOW())";
		Object[] params = new Object[]{
				leave.getGroupId(), leave.getChildId(), leave.getLeaveWuId()
				, leave.getReason(), leave.getStartTime(), leave.getEndTime()
		};
		leave.setLeaveId(daoSupport.insert(sql, params));
		return leave.getLeaveId()>0;
	}
	@Override
	public boolean readLeave(int leaveId) {
		String sql = "UPDATE `leave` SET auditState=2 WHERE leaveId=? AND auditState=1";
		return daoSupport.update(sql, leaveId) > 0;
	}

}
