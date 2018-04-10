/**
 * 
 */
package com.doyd.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.doyd.utils.StringUtil;

/**
 * @author Administrator
 *
 */
public class HttpclientUtil {
	
	/**
	 * 从流中获取数据
	 * @Title: getRequestInput
	 * @param request
	 * @param charset
	 * @return String
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-20 下午6:23:00
	 */
	public static String getRequestInput(HttpServletRequest request, String charset){
		PrintWriter pw = null;
		BufferedReader bf = null;
		StringBuffer sb = new StringBuffer();
		try {
			bf = new BufferedReader(new InputStreamReader(request.getInputStream(), StringUtil.isEmpty(charset) ? "UTF-8" : charset));
			String xml = "";
			while((xml=bf.readLine())!=null){
				sb.append(xml);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(pw!=null){
				pw.flush();
				pw.close();
			}
			if(bf!=null){
				try {
					bf.close();
				} catch (IOException e) {
					bf = null;
				}
			}
		}
		return sb.toString();
	}

}
