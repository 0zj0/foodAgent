package com.doyd.core.action.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import com.doyd.core.CoreVars;

import org.doyd.utils.PropertiesUtil;
import org.doyd.utils.RegexUtil;
import org.doyd.utils.StringUtil;

public class ControllerContext {
	
	private static String BASEPATH_IP = "";
	
	public static final String REFLUSH_DEFAULT = "reflush_server";
	public static final String REFLUSH_PRODUCT = "reflush_server_product";
	public static boolean flushClientCache(HttpServletRequest request, boolean reflush){
		return flushClientCache(request, reflush, REFLUSH_DEFAULT);
	}
	
	public static boolean flushClientCache(HttpServletRequest request, boolean reflush, String reflushName){
		if(reflush){
			request.setAttribute(REFLUSH_DEFAULT, reflushName);
		}
		return reflush;
	}
	
	public static String noexist(HttpServletRequest request, String error, boolean simple){
		request.setAttribute("error", error);
		if(simple){
			request.setAttribute("error", simple);
			return "error-view";
		}else{
			request.setAttribute("uri", "error-view.jsp");
			return "index";
		}
	}
	
	public static String getIp(HttpServletRequest request){
		String ip = request.getHeader("x-forwarded-for");
		String regex = CoreVars.RULES_IP;
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}else{
			ip = RegexUtil.getMatchString(ip, regex, 0);
		}
		return ip;
	}
	
	public final static String getBasePath(HttpServletRequest request) {
		StringBuffer basePath = new StringBuffer();
		final String scheme = request.getScheme();
		basePath.append(scheme).append("://");
		basePath.append(request.getServerName());
		if("http".equals(scheme) && request.getServerPort()!=80){
			basePath.append(":").append(request.getServerPort());
		}else if("https".equals(scheme) && request.getServerPort()!=443){
			basePath.append(":").append(request.getServerPort());
		}
		basePath.append(request.getContextPath()).append("/");
		return basePath.toString();
	}
	
	
	public static String getBaseIp(){
		if(StringUtil.isEmpty(BASEPATH_IP)){
			getIPBasePath(null, null);;
		}
		return BASEPATH_IP;
	}
	
	public final static String getIPBasePath(HttpServletRequest request,String ip) {
		if(StringUtil.isEmpty(BASEPATH_IP)){
			StringBuffer basePath = new StringBuffer();
			try {
				 MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
				 Set<ObjectName> objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
				         Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
				 String host = InetAddress.getLocalHost().getHostAddress();
				 String port = objectNames.iterator().next().getKeyProperty("port");
				 basePath.append("http://");
				 basePath.append(host);
				 if(!"80".equals(port)){
					 basePath.append(":").append(port);
				 }
				 String proName = PropertiesUtil.getProperty("projectName");
				 if(request!=null){
					 proName= request.getContextPath();
				 }
				 basePath.append(proName).append("/");
				 BASEPATH_IP = basePath.toString();
				} catch (Exception e) {
			}finally{
				if(StringUtil.isEmpty(BASEPATH_IP) && request != null){
					final String scheme = request.getScheme();
					basePath.append(scheme).append("://");
					ip = StringUtil.isEmpty(ip) ? PropertiesUtil.getProperty("localaddr"):ip;
					basePath.append(ip);
					if(request.getServerPort()!=80){
						basePath.append(":").append(request.getServerPort());
					}
					basePath.append(request.getContextPath()).append("/");
					BASEPATH_IP = basePath.toString();
				}
			}
		}
		return BASEPATH_IP;
	}
	
	public final static String getIPBasePath(HttpServletRequest request) {
		return getIPBasePath(request,null);
	}
	public final static String getBasePathWithoutContextPath(HttpServletRequest request) {
		StringBuffer basePath = new StringBuffer();
		final String scheme = request.getScheme();
		basePath.append(scheme).append("://");
		basePath.append(request.getServerName());
		if("http".equals(scheme) && request.getServerPort()!=80){
			basePath.append(":").append(request.getServerPort());
		}else if("https".equals(scheme) && request.getServerPort()!=443){
			basePath.append(":").append(request.getServerPort());
		}
		basePath.append("/");
		return basePath.toString();
	}
	
	/**
	 * 设置basePath路径
	 * 
	 * @param request
	 */
	public final static void setBasePath(HttpServletRequest request) {
		StringBuffer basePath = new StringBuffer();
		basePath.append(request.getScheme()).append("://");
		basePath.append(request.getServerName());
		if(request.getServerPort()!=80 && request.getServerPort()!=443){
			basePath.append(":").append(request.getServerPort());
		}
		basePath.append(request.getContextPath()).append("/");
		request.setAttribute(CoreVars.BASE_PATH, basePath.toString());
	}
	
	
	
	public final static void print(HttpServletResponse response, String msg, String contentType){
		if(StringUtil.isEmpty(contentType) || "html".equalsIgnoreCase(contentType)){
			response.setContentType("text/html;charset=utf-8");
		}else if("json".equals(contentType)){
			response.setContentType("application/json;charset=utf-8");
		}else{
			response.setContentType(contentType);
		}
		response.setHeader("pragma", "no-cache");
		response.setHeader("cache-control", "no-cache");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.print(msg.toString());
		} catch (IOException e) {
			System.err.println("向页面打印的时候出错：\n" + e + "\n所要打印的内容：" + msg);
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 * 	0：非ajax请求
	 * 	1：ajax请求html页面
	 *  2：ajax请求json数据
	 */
	public final static int getAjaxType(HttpServletRequest request){
		if(StringUtil.isEmpty(request.getHeader("X-Requested-With"))){
			return 0;
		}
		String accept = request.getHeader("accept");
		if(StringUtil.isNotEmpty(accept) && accept.indexOf("application/json")>=0){
			return 2;
		}
		return 1;
	}
	
	public static WebApplicationContext getWac(){
		return ContextLoaderListener.getCurrentWebApplicationContext();
	}
	
	public static String getBaseFilePath(){
		String path = ControllerContext.class.getProtectionDomain().getCodeSource().getLocation()
				.getFile().replace("\\", "/");
		if(path.indexOf("/WEB-INF")>=0){
			path = path.substring(0, path.indexOf("/WEB-INF"));
		}
		try{
			path = URLDecoder.decode(path, "utf-8");
		}catch (Exception e) {
		}
		return path;
	}
	
	/**
	 * 获取web项目路径
	 * @return
	 */
	public static String getWebAppPath(){
		String path = StringUtil.class.getProtectionDomain().getCodeSource().getLocation()
				.getFile().replace("\\", "/");
		if(path.indexOf("/WEB-INF")>=0){
			path = path.substring(0, path.indexOf("/WEB-INF"))+"/WEB-INF/";
		}
		if(!path.endsWith("/")){
			path = path.substring(0, path.lastIndexOf("/")+1);
		}
		try{
			path = URLDecoder.decode(path, "utf-8");
		}catch (Exception e) {
		}
		return path;
	}
	
	public static String getProjectName(){
		String path = ControllerContext.class.getProtectionDomain().getCodeSource().getLocation()
				.getFile().replace("\\", "/");
		if(path.indexOf("/WEB-INF")>=0){
			path = path.substring(0, path.indexOf("/WEB-INF"));
		}
		int lastPos = path.lastIndexOf("/");
		if(lastPos>=0){
			path = path.substring(lastPos+1);
		}
		try{
			path = URLDecoder.decode(path, "utf-8");
		}catch (Exception e) {
		}
		return path;
	}
	
	public static boolean validUserAgentValue(HttpServletRequest request, String uri, String allowUri, String userAgentValue){
		if(StringUtil.isEmpty(uri) || StringUtil.isEmpty(allowUri) || StringUtil.isEmpty(userAgentValue)){
			return false;
		}
		if(uri.startsWith(allowUri)){
			String userAgent = request.getHeader("User-Agent");
			if(StringUtil.isNotEmpty(userAgent) 
					&& userAgent.toLowerCase().indexOf(userAgentValue.toLowerCase())>=0){
				return true;
			}
		}
		return false;
	}
}
