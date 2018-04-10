package com.doyd.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.ISubjectDao;
import com.doyd.dao.rowmapper.SubjectRowmapper;
import com.doyd.model.Subject;

@Repository
public class SubjectDao implements ISubjectDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private SubjectRowmapper mapper;
	
	@Override
	public List<String> getSubjectList(int wuId) {
		String sql = "select `subject` from `subject` where wuId=? and state=1 order by subjectId desc";
		return daoSupport.queryForStringList(sql, wuId);
	}
	
	@Override
	public boolean addSubject(Subject subject) {
		String sql = "INSERT INTO `subject` (wuId,`subject`,ctime) VALUES (?,?,NOW()) ON DUPLICATE KEY UPDATE state = 1";
		subject.setSubjectId(daoSupport.insert(sql, subject.getWuId(), subject.getSubject()));
		return subject.getSubjectId()>0;
	}
	
	@Override
	public boolean existSubject(int wuId, String subject) {
		String sql = "SELECT subjectId FROM `subject` WHERE wuId=? AND state=1 AND SUBJECT=?";
		return daoSupport.queryForExist(sql, new Object[]{wuId, subject});
	}

}
