package com.doyd.core.dao;

import java.util.List;

import com.doyd.core.model.SysConfig;

public interface ISysConfigDao {
	public List<SysConfig> getSysConfigList();

	public SysConfig getSysConfigByKey(String sysConfigKey);

	/**
	 * 
	 * @Depiction：更新系统配置信息
	 * @param sysConfig
	 * @return int
	 * @author 王思敏
	 * @date 2017-8-23 上午3:36:32
	 */
	public int updateSysConfig(SysConfig sysConfig);
}
