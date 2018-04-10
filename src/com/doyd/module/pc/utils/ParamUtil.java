/**
 * 
 */
package com.doyd.module.pc.utils;

import javax.servlet.http.HttpServletRequest;

import org.doyd.utils.StringUtil;

/**
 * @author Administrator
 *
 */
public class ParamUtil {
	
	public static int[] parseIntArray(String[] array){
		if(array==null){
			return null;
		}
		int[] value = new int[array.length];
		for(int i=0;i<array.length; i++){
			value[i] = StringUtil.parseInt(array[i]);
		}
		return value;
	}
	
	public static String[] getStringArray(HttpServletRequest request, String paramName){
		if(paramName==null){
			return null;
		}
		String paramName1 = paramName.replace("[]", "");
		String paramName2 = paramName1+"[]";
		String[] strArray = request.getParameterValues(paramName2);
		if(strArray==null||strArray.length<=0){
			strArray = request.getParameterValues(paramName1);
			if(strArray==null||strArray.length<=0){
				strArray = new String[]{request.getParameter(paramName1)};
			}
		}
		return strArray;
	}
	
	public static int[] getIntArray(HttpServletRequest request, String paramName){
		String[] strArray = getStringArray(request, paramName);
		return parseIntArray(strArray);
	}

}
