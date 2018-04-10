package com.doyd.core.annotation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 装配自定义类，获取相应的上下文bean
 * @author wjs
 * @date 2017-5-12 
 *
 */
@Component
public class ContextBeanListener implements ApplicationListener<ContextRefreshedEvent> {
	public Map<String, Object> listenter = new HashMap<String, Object>();
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Map<String, Object>   mp  = event.getApplicationContext().getBeansWithAnnotation(AssemblyClass.class);
		if(mp == null || mp.size() < 1){
			return ;
		}
		listenter.putAll(mp);
	}
	/**
	 * 根据类的注解，扫描出相应的bean
	 * @author wjs
	 * @date 2017-5-12 
	 * @param key
	 * @param t
	 * @return
	 */
	public Object getBean(String key){
		return  listenter.get(key);
	}

}
