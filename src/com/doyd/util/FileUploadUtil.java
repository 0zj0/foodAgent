package com.doyd.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.doyd.httpclient.HttpClientUtil;
import org.doyd.utils.EncryptUtils;
import org.doyd.utils.StringUtil;
import org.json.JSONObject;

import com.doyd.cache.memory.SysCache;
import com.doyd.core.convertor.AppConfig;

/**
 * @author ylb
 * @version 创建时间：2017-12-8 上午11:22:44
 */
public class FileUploadUtil {
	public static String getPrivateFileUrl(String fileUrl) throws PapersException{
		if(fileUrl.indexOf("?") > 0){
			fileUrl = fileUrl.substring(0, fileUrl.indexOf("?"));
		}
		
		long timestamp = System.currentTimeMillis();
		AppConfig config = SysCache.getCache().getAppConfig();
		String appId = config.getPapersAppId();
		String secret = config.getPapersSecret();
		String sign = EncryptUtils.MD5(appId+secret+timestamp);
		String codeServer = config.getPapersGetCodeUrl();
		try {
			codeServer = codeServer + "?appId=" + appId 
					+ "&sign=" + sign + "&timestamp=" + timestamp + "&url=" + URLEncoder.encode(fileUrl, "utf-8");
		} catch (UnsupportedEncodingException e1) {
		}
		String result = HttpClientUtil.post(codeServer, null);
		if(StringUtil.isEmpty(result)){
			throw new PapersException("从服务器获得访问码失败");
		}
		try {
			JSONObject obj = new JSONObject(result);
			boolean state = obj.getBoolean("state");
			if(!state){
				throw new PapersException(obj.getString("message"));
			}
			String code = obj.getString("message");
			if(StringUtil.isEmpty(code)){
				throw new PapersException("从服务器获得访问码失败");
			}
			return fileUrl + "?appId=" + appId + "&sign=" + sign 
					+ "&timestamp=" + timestamp + "&code=" + code;
		} catch (Exception e) {
			throw new PapersException("出现异常", e);
		}
	}
	
	/**
	 * 上传文件
	 * 
	 * @param appId 渠道appId
	 * @param secret 渠道密钥
	 * @param papersServer 文件服务器地址
	 * @param file 上传的文件
	 * @param purview 权限   true:私有的，false:公有的
	 * @param debug 是否为临时文件  
	 * @param suffix 文件后缀   ，可传，可不传
	 * @return String
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-8 下午2:13:22
	 */
	public static String uploadToFileServer(File file
			, boolean purview, boolean debug, String suffix) throws PapersException{
		AppConfig config = SysCache.getCache().getAppConfig();
		String appId = config.getPapersAppId();
		String secret = config.getPapersSecret();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("file", file);
		params.put("purview", purview ? "private" : "public");
		params.put("debug", debug);
		params.put("suffix", suffix);
		long timestamp = System.currentTimeMillis();
		String sign = EncryptUtils.MD5(appId+secret+timestamp);
		String papersServer = SysCache.getCache().getAppConfig().getPapersUploadUrl();
		papersServer = papersServer + "?appId=" + appId + "&sign=" + sign + "&timestamp=" + timestamp;
		String result = HttpClientUtil.upload(papersServer, params, null);
		if(StringUtil.isEmpty(result)){
			throw new PapersException("文件上传服务器时出错");
		}
		try {
			JSONObject obj = new JSONObject(result);
			boolean state = obj.getBoolean("state");
			if(!state){
				throw new PapersException(obj.getString("message"));
			}
			return obj.getString("message");
		} catch (Exception e) {
			throw new PapersException("出现异常", e);
		}
	}
}
