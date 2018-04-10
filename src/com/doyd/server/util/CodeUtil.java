package com.doyd.server.util; 

import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;

import org.doyd.utils.StringUtil;


public class CodeUtil {
	
	private static String verify = "kpzs";
	private static Random random = new Random();
	
	/**
	 * 随即len长度的随即数字字符串
	 * @Title: getRandomNumber
	 * @param len
	 * @return String
	 * @author 创建人：王伟
	 * @date 创建时间：2017-6-22 下午7:22:32
	 */
	 public static String getRandomNumber(int len) {
	        String randomNumber="";
	        for (int index = 0; index < len; index++) {
	            int num = (int) (Math.random()*9) ;// [0,99]之间的整数
	            randomNumber+=num;
	        }
			return randomNumber;
	    }
	 
	 
	 
	 /**
	  * 
	  * @author wjs
	  * @date 2017-9-15 
	  *
	  * @param type 1: 注册码(八位数)，2：推广码（六位数）
	  * @return
	  */
	 public static String getCode(int... Object){
		 	int type = 1;
		 	if(Object != null && Object.length ==1){
		 		type = Object[0];
		 	}
		 	String result = null;
		 	if(type ==1){
		 	    result = getRandomString(6).toLowerCase();
				String md5 = DigestUtils.md5Hex(result + verify).toLowerCase();
				if (StringUtil.isNotEmpty(result) && StringUtil.isNotEmpty(md5)) {
					char c = md5.charAt(0);
					char d = md5.charAt(7);
					result = result.substring(0, 1) + c + result.substring(1, 5) + d
							+ result.substring(5);
				}
		 	}else if(type  == 2){
		 		    result = getRandomString(4).toLowerCase();
					String md5 = DigestUtils.md5Hex(result + verify).toLowerCase();
					if (StringUtil.isNotEmpty(result) && StringUtil.isNotEmpty(md5)) {
						char c = md5.charAt(0);
						char d = md5.charAt(7);
						result = result.substring(0, 1) + c + result.substring(1, 3) + d
								+ result.substring(3);
					}
		 		
		 	}
			return result;
	 }
	 public static boolean verify(String sm) {
			boolean flag = false;
			if(StringUtil.isEmpty(sm)){
				return flag;
			}
			if(sm.length() == 6){
				char a = sm.charAt(1);
				char b = sm.charAt(4);
				String newUrl = sm.substring(0, 1) + sm.substring(2, 4)
						+ sm.substring(5);
				String md5 = DigestUtils.md5Hex(newUrl + verify).toLowerCase();
				char c = md5.charAt(0);
				char d = md5.charAt(7);
				return (a == c && b == d);
			}else if(sm.length() == 8){
				char a = sm.charAt(1);
				char b = sm.charAt(6);
				String newUrl = sm.substring(0, 1) + sm.substring(2, 6)
						+ sm.substring(7);
				String md5 = DigestUtils.md5Hex(newUrl + verify).toLowerCase();
				char c = md5.charAt(0);
				char d = md5.charAt(7);
				return (a == c && b == d);
			}
			return flag;
		}
	 
		public static String getRandomString(int length) {
			String baseStr = "23456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < length; i++) {
				sb.append(baseStr.charAt(random.nextInt(baseStr.length())));
			}
			return sb.toString();
		}
		
		
		
		 public static void main(String[] args) {
			
			 for(int i  =0 ; i < 1 ; i++){
				 	String code = getCode();
					System.out.println(code);
					System.out.println(verify(code));
			 }
			
		}
	 
	 
	}