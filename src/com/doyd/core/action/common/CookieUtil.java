package com.doyd.core.action.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

	
	/**
	 * 保存cookie
	 * @author wjs
	 * @date 2017-6-30 
	 *
	 * @param request
	 * @param response
	 * @param name
	 * @param value
	 */
	public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value){
		String contextPath = request.getContextPath();
		Cookie cookie = new Cookie(name, value);
		cookie.setPath(contextPath);
		response.addCookie(cookie);
	}
	
	/**
	 * 获取cookie
	 * @author wjs
	 * @date 2017-6-30 
	 *
	 * @param request
	 * @param response
	 * @param name
	 * @param value
	 * @return
	 */
	public static String getCookie(HttpServletRequest request, String name){
		Cookie[] cookies = request.getCookies();
		if(cookies==null||cookies.length<=0){
			return null;
		}
		for(Cookie cookie: cookies){
			if(cookie.getName().equals(name)){
				return cookie.getValue();
			}
		}
		return null;
	}
	
	
}
