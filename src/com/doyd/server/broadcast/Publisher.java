package com.doyd.server.broadcast;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.doyd.server.task.ThreadManager;

@SuppressWarnings({ "rawtypes" })
public class Publisher {
	/**
	 * 发送广播通知
	 * @param t 广播的消息
	 */
	public static <T> void notify(T t){
		try{
			BroadcastJob<T> job = getJob(t);
			if(job==null){
				return;
			}
			taskManager.addTask(job);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param t
	 */
	public static <T> void notifyExec(T t){
		try{
			BroadcastJob<T> job = getJob(t);
			if(job==null){
				return;
			}
			job.run();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static <T> BroadcastJob<T> getJob(T t){
		if(t==null){
			return null;
		}
		try{
			//如果还没初始化广播系统，则初始化广播系统
			if(serverMap.size()<=0){
				initServer();
			}
			// 获取当前t的消息类型，如果t含有泛型，需要识别t的泛型类型与t同时作为消息类型的识别码
			// 如t为com.doyd.server.broadcast.consumer.EntityInserted<ProviderConfig>的实例
			// 则消息类型为com.doyd.server.broadcast.consumer.EntityInserted<com.doyd.model.ProviderConfig>
			String name = t.getClass().getName();
			if(t.getClass().getTypeParameters().length>0){
				TypeVariable type = t.getClass().getTypeParameters()[0];
				if("T".equals(type.getName())){
					try{
						Method method = t.getClass().getMethod("getT");
						Object o = method.invoke(t);
						if(o!=null){
							name = name + "<"+o.getClass().getName()+">";
						}
					}catch (Exception e) {
					}
				}else{
					name = name + "<"+type.getName()+">";
				}
			}
			// 获取接收该消息类型的所有Consumer，并处理消息
			List<IConsumer> list = serverMap.get(name);
			BroadcastJob<T> job = new BroadcastJob<T>(list, t);
			return job;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * serverMap：消息和对应Consumer的缓存
	 * key: 消息的类型
	 * value: 每个消息对应的所有的Consumer
	 */
	private static Map<String, List<IConsumer>> serverMap = new HashMap<String, List<IConsumer>>();
	private static ThreadManager taskManager = new ThreadManager();
	/**
	 * 初始化广播系统，缓存所有相关Consumer
	 */
	public synchronized static void initServer(){
		String packageName = Publisher.class.getPackage().getName();
		// 查找所有Consumer的根目录
		String filePath = getFileBasePath() + packageName.replace(".", "/");
		File file = new File(filePath);
		serverMap.clear();
		findConsumerClass(file);
	}
	
	/**
	 * 查找某个目录下所有的Consumer，并缓存
	 * @param file
	 */
	public static void findConsumerClass(File file){
		File[] fileList = file.listFiles();
		if(fileList==null){
			return;
		}
		//查找所有实现了IConsumer接口的类
		for (File f : fileList) {
			if (f.isFile()) {
				checkClass(f);
			} else if (f.isDirectory()) {
				File[] childFileList = f.listFiles();
				if(childFileList==null){
					break;
				}
				for(File childFile: childFileList){
					if(childFile.isFile()){
						checkClass(childFile);
					}else{
						findConsumerClass(childFile);
					}
				}
			}
		}
	}
	
	public static ThreadManager getTaskManager() {
		return taskManager;
	}

	/**
	 * 判断文件是否Consumer，如果是，添加到serverMap当中。
	 * @param file
	 */
	private static void checkClass(File file){
		// 获取class的完整类名className
		// 如：com.doyd.server.broadcast.consumer.ProviderConfigConsumer
		if(!file.getPath().endsWith(".class")){
			return;
		}
		String className = getClassName(file.getPath());
		try{
			Class<?> clazz = Class.forName(className);
			if(!clazz.isInterface() && IConsumer.class.isAssignableFrom(clazz)){
				for(Type t: clazz.getGenericInterfaces()){
					// 获取相应的接口名称，如果为IConsumer的接口时，把IConsumer接口的泛型作为消息类型
					// 如接口名称为：com.doyd.server.broadcast.IConsumer<com.doyd.model.ProviderConfig>
					// 则消息类型为：com.doyd.model.ProviderConfig
					// 同一种消息类型缓存到同一个List当中
					String iname = t.toString();
					final String baseClassName = IConsumer.class.getName();
					if(iname.startsWith(baseClassName+"<")){
						// 获取泛型的类型，并作为消息类型
						// 如：com.doyd.model.ProviderConfig
						String key = iname.substring(baseClassName.length()+1, iname.length()-1);
						IConsumer<?> consumer = (IConsumer<?>) clazz.newInstance();
						// 缓存Consumer到同一类型的消息类型队列中
						synchronized (serverMap) {
							List<IConsumer> list = serverMap.get(key);
							if(list==null){
								list = new ArrayList<IConsumer>();
							}
							list.add(consumer);
							serverMap.put(key, list);
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getClassName(String filename){
		filename = filename.substring(filename.replace("\\", "/").indexOf("/classes") + 9,
				filename.lastIndexOf("."));
		filename = filename.replace("\\", ".").replace("/", ".");
		return filename;
	}
	
	private static String getFileBasePath(){
		java.net.URL url = Publisher.class.getClassLoader().getResource("");
		if(url==null){
			url = ClassLoader.getSystemResource("");
		}
		return url.getPath().replace("%20", " ");
	}
}
