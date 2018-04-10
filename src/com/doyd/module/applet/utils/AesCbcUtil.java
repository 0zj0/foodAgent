package com.doyd.module.applet.utils;


import java.security.AlgorithmParameters;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * 
 * AES-128-CBC 加密方式
 * AES-128-CBC可以自己定义密钥和偏移量
 * AES-128是jdk自动生成的密钥
 * 
 * @author xieqing
 */
public class AesCbcUtil {
	
	private static Logger logger = Logger.getLogger(AesCbcUtil.class);
	
    static {
        //BouncyCastle
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * AES解密
     *
     * @param encryptedData  密文，被加密的数据
     * @param session_key    秘钥
     * @param iv       偏移量
     * @param encodingFormat 解密后的结果需要进行的编码
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptedData, String session_key, String iv, String encodingFormat) throws Exception {
        //被加密的数据
        byte[] dataByte = Base64.decodeBase64(encryptedData);
        //加密秘钥
        byte[] keyByte = Base64.decodeBase64(session_key);
        //偏移量
        byte[] ivByte = Base64.decodeBase64(iv);


        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");

            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");

            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));

            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化

            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, encodingFormat);
                return result;
            }
            return null;
        } catch (Exception e) {
        	e.printStackTrace();
        	logger.info("encryptedData:"+encryptedData+",session_key:"+session_key +",iv:"+iv);
        }

        return null;
    }
    
    public static void main(String[] args) throws Exception {
       String session_key = "vz3BZSBn8HGuhj1b9WGGUw==";
       String encryptedData = "ajtD6Z4nprkWVGW9gw/zSc6XIyx7xBH6YOcwNif1lROYmSzNQp/67qnziPTafbg2gG9mDzY8QkmByzSzBXqUcAqcbFArevxY/GcabyVXCP2DsUeesJGSz0bJSlfXkLNgAUK1JJmf+QjX5WIQpFp0Vjg3p08CeERlNymc8aDo8X1tQSLjlTB3PoZdm+KJWVT/cYRdU/yJT20YoR4gUAl5+ogbEHE/d94URZwTIo7LpzhAth8x9p5/FZ6B4wf4167eo4kCwztUU8dkYUXC5CYe9PS+5bMY82hsnBRqj1VHkF7+VuX+8W83t8Flq1WJ0IO9ftYsifDUbRycUZuWk0ZNY5VfWQgJ8B9ScrkDAE6ZXns5vHBwuFW/AQgv2WEJ8aePEHhDuIMCQ3xFVRYq5LpTPB0W5jEyY6F4jyzOB3oBQ93rg65UQLsRF7JsToHQCgwt93CwKn9WUTtZ5ygUmxP7tG/NR3Qkmyb4bfp4hC8/7DHC1RGlSZ+rs9LdO4cBLZZfhdbbt58OESkJz6pc8Lz2+WVFwsXooVAVK2Wshehjl+Y=";
   	   String vi = "SQAHI7eMYPDg5qE6s9mW5A==";
 	   String user = AesCbcUtil.decrypt(encryptedData,session_key,vi,"UTF-8");
 	   System.out.println(user);
	}

}