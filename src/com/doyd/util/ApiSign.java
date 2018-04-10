package com.doyd.util;

import org.doyd.utils.EncryptUtils;
import org.doyd.utils.StringUtil;

public class ApiSign {
	

	public static String IV = "!@#$%^&*";
	public static String generateSecret(String token){
		if(token.length()<40){
			return null;
		}
		String msn = getMsnFromToken(token);
		String nonce = token.substring(msn.length(), token.length()-2);
		return EncryptUtils.MD5(msn+"_"+nonce+"_doyd"+"_"+IV);
	}

	public static String buildToken(String msn){
		if(StringUtil.isEmpty(msn) || msn.length()%32!=0){
			return null;
		}
		String nonce = StringUtil.getRandomString(6);
		String str = msn+"_"+nonce+"_doyd";
		String mid = EncryptUtils.MD5(str);
		String token = msn + nonce + mid.substring(18, 19)+mid.substring(12, 13);
		return token;
	}
	
	public static  boolean checkToken(String token){
		if(StringUtil.isEmpty(token) || token.length()<40){
			return false;
		}
		String msn = getMsnFromToken(token);
		String nonce = token.substring(msn.length(), token.length()-2);
		String str = msn+"_"+nonce+"_doyd";
		String mid = EncryptUtils.MD5(str);
		if(!token.substring(token.length()-2).equals(mid.substring(18, 19)+mid.substring(12, 13))){		
			return false;
		}
		return true;
	}
	
	public static String getMsnFromToken(String token){
		if(StringUtil.isEmpty(token) || token.length()<40){
			return null;
		}
		if(token.length()>=64+8){
			return token.substring(0, 64);
		}
		return token.substring(0, 32);
	}
	
	public static void main(String[] args) {
		String msn = "01234567890123456789012345678901";
		String token = buildToken(msn);
		System.out.println(token);
		System.out.println(checkToken(token));
	}
}
