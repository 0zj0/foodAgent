package com.doyd.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IChildrenDao;
import com.doyd.dao.rowmapper.ChildrenRowmapper;
import com.doyd.model.Children;

@Repository
public class ChildrenDao implements IChildrenDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private ChildrenRowmapper mapper;
	
	@Override
	public boolean existChildren(int wuId, String childName) {
		String sql = "SELECT childId FROM children WHERE wuId=? AND childName=? AND state=1";
		return daoSupport.queryForExist(sql, new Object[]{wuId, childName});
	}

	@Override
	public boolean addChildren(Children child) {
		String sql = "INSERT INTO children(wuId,childName,relation,education,grade,ctime) VALUES(?,?,?,?,?,NOW())";
		Object[] params = new Object[]{
				child.getWuId(), child.getChildName(), child.getRelation()
				, child.getEducation(), child.getGrade()
		};
		child.setChildId(daoSupport.insert(sql, params));
		return child.getChildId() > 0;
	}

	@Override
	public List<Children> getChildrenList(int wuId) {
		String sql = "SELECT * FROM children WHERE wuId=? AND state=1";
		return daoSupport.query(sql, new Object[]{wuId}, mapper);
	}

	@Override
	public Children getChildren(int childId) {
		String sql = "SELECT * FROM children WHERE childId=? AND state=1";
		return daoSupport.queryForObject(sql, new Object[]{childId}, mapper);
	}

	@Override
	public boolean updateChildName(int childId, String childName, String relation) {
		String sql = "UPDATE children SET childName=?,relation=? WHERE childId=? AND state=1";
		return daoSupport.update(sql, childName, relation, childId) > 0;
	}

}
