package com.doyd.core.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.ISysConfigDao;
import com.doyd.core.dao.rowmapper.SysConfigRowmapper;
import com.doyd.core.model.SysConfig;

@Repository
public class SysConfigDao implements ISysConfigDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private SysConfigRowmapper rowmapper;

	@Override
	public List<SysConfig> getSysConfigList() {
		String sql = "select * from sys_config";
		return daoSupport.query(sql, null, rowmapper);
	}

	@Override
	public SysConfig getSysConfigByKey(String sysConfigKey) {
		String sql = "select * from sys_config where `key` = ?";
		return daoSupport.queryForObject(sql, new Object[]{sysConfigKey}, rowmapper);
	}

	@Override
	public int updateSysConfig(SysConfig sysConfig) {
		String sql = "update sys_config set textValue=? where configId=?";
		return daoSupport.update(sql, new Object[]{sysConfig.getTextValue(),sysConfig.getConfigId()});
	}
}
