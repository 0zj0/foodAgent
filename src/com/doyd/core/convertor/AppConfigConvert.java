package com.doyd.core.convertor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.doyd.core.model.SysConfig;

public class AppConfigConvert {
	public static AppConfig convertToAppConfig(List<SysConfig> sysConfigList){
		AppConfig config = new AppConfig();
		if(sysConfigList==null || sysConfigList.size()<=0){
			return config;
		}
		Field[] fieldArr = AppConfig.class.getDeclaredFields();
		if(fieldArr==null || fieldArr.length<=0){
			return config;
		}
		Map<String, SysConfig> map = new HashMap<String, SysConfig>();
		for(SysConfig sysConfig: sysConfigList){
			map.put(sysConfig.getKey(), sysConfig);
		}
		for(Field field:fieldArr){
			SysConfig sysConfig = map.get(field.getName());
			if(sysConfig==null){
				continue;
			}
			field.setAccessible(true);
			try{
				if("int".equals(sysConfig.getValueType())){
					field.setInt(config, sysConfig.getIntValue());
				}else if("boolean".equals(sysConfig.getValueType())){
					field.setBoolean(config, sysConfig.isBoolValue());
				}else if("string".equals(sysConfig.getValueType())){
					field.set(config, sysConfig.getTextValue());
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return config;
	}
	
	public List<SysConfig> convertToSysConfigList(AppConfig appConfig){
		List<SysConfig> sysConfigList = new ArrayList<SysConfig>();
		
		return sysConfigList;
	}
}
