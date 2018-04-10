package com.doyd.core.biz;

import com.doyd.core.model.SysConfig;

public interface ISysConfigService {

	/**
	 * 
	 * @Depiction：根据key查询系统配置
	 * @param sysConfigKey
	 * @return SysConfig
	 * @author 王思敏
	 * @date 2017-8-22 下午9:24:54
	 */
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
