package com.doyd.cache.memory;

import java.io.FileReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.doyd.cache.IModelCache;
import com.doyd.core.action.common.ControllerContext;
import com.doyd.core.convertor.AppConfig;
import com.doyd.core.convertor.AppConfigConvert;
import com.doyd.core.dao.impl.SysConfigDao;
import com.doyd.server.cache.CacheManager;

public class SysCache implements IModelCache {
	private static SysCache sysCache = new SysCache();

	private SysCache() {

	}

	public static SysCache getCache() {
		return sysCache;
	}

	private AppConfig appConfig = new AppConfig();

	public AppConfig getAppConfig() {
		return appConfig;
	}

	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	@Override
	public void init() {
		SysConfigDao sysConfigDao = (SysConfigDao) ControllerContext.getWac().getBean("sysConfigDao");
		if (sysConfigDao != null) {
			AppConfig appConfig = AppConfigConvert.convertToAppConfig(sysConfigDao.getSysConfigList());
			if (appConfig != null) {
				if("localhost".equals(appConfig.getRedisHost()) || "127.0.0.1".equals(appConfig.getRedisHost())){
					String host = getDbHost();
					if(host!=null && host.length()>0){
						if(!"localhost".equals(host) && !"127.0.0.1".equals(host)){
							appConfig.setRedisHost(host);
						}
					}
				}
				setAppConfig(appConfig);
				CacheManager.getRedis().setHost(getAppConfig().getRedisHost(), getAppConfig().getRedisPort(),
						getAppConfig().getRedisPassword());
			}
		}
	}
	
	public static String getDbHost() {
		try {
			String xml = ControllerContext.getWebAppPath() + "proxool.xml";
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			final SAXParser saxParser = saxParserFactory.newSAXParser();
			final XMLReader xmlReader = saxParser.getXMLReader();
			final XMLConfigurator xmlConfigurator = new XMLConfigurator();
			xmlReader.setErrorHandler(xmlConfigurator);
			final boolean NAMESPACE_AWARE = true;
			xmlReader.setFeature("http://xml.org/sax/features/namespaces", NAMESPACE_AWARE);
			xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", !NAMESPACE_AWARE);
			InputSource inputSource = new InputSource(new FileReader(xml));
			XMLReader reader = saxParser.getXMLReader();
            reader.setContentHandler(xmlConfigurator);
            reader.setEntityResolver(xmlConfigurator);
            reader.setErrorHandler(xmlConfigurator);
            reader.setDTDHandler(xmlConfigurator);
            reader.parse(inputSource);
            return xmlConfigurator.getDriverHost();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
