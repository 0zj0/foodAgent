/**
 * 
 */
package com.doyd.module.pc.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doyd.biz.IFilesService;
import com.doyd.cache.memory.SysCache;
import com.doyd.msg.ApiMessage;
import com.doyd.server.util.SignUtil;
import org.doyd.httpclient.HttpClientUtil;

/**
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = {"test"})
public class TestController {
	
	@Autowired
	private IFilesService filesService;
	
	@RequestMapping(value="/files/delete",method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public ApiMessage deleteFiles(HttpServletRequest request,HttpServletResponse response){
		int id = 1;
		int groupId = 1;
		List<Map<String, Object>> files = filesService.getFiles("files", id, "achievement");
		filesService.deleteFiles("files", id, groupId, "achievement");
		return null;
	}
	
	@RequestMapping(value="/login",method={RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public ApiMessage login(HttpServletRequest request,HttpServletResponse response){
		int id = 1;
		int groupId = 1;
		List<Map<String, Object>> files = filesService.getFiles("files", id, "achievement");
		filesService.deleteFiles("files", id, groupId, "achievement");
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		JSONObject json = new JSONObject();
		/*Map<String, String> params = new HashMap<String, String>();
		long timestamp = System.currentTimeMillis();
		json.put("timestamp", timestamp);
		params.put("timestamp", timestamp+"");
		String sign = SignUtil.sign(params, SysCache.getCache().getAppConfig().getPcLoginSecret());
		json.put("sign", sign);
		String sessionKey = "c95a649e89a9d3c8cb26afa86730b408";
		String openId = "oV6n_0HNOB3dv1AhxfriYTSo0Y0w";
		int state = 1;
		json.put("sessionKey", sessionKey);
		json.put("openId", openId);
		json.put("state", state);
		json.put("type", 2);
		json.put("openGId", "c4ca4238a0b923820dcc509a6f758491");*/
		//JSONArray array = new JSONArray();
		JSONObject file = new JSONObject();
		//file.put("fileId", 2);
		file.put("fileName", "1.2-首页all.png");
		file.put("fileAddr", "http://act.doyd.cn/picture3/upload/image/8a3bd444432e63c50668b1800a2cadea/20170704/1499150873952091717.jpg");
		//array.put(file);
		/*file = new JSONObject();
		file.put("fileId", 3);
		file.put("fileAddr", "http://test.wbgj.cn/papers/file/public/forever1/c4ca4238a0b923820dcc509a6f75849b/20180103/400c13072760494bbc8f44d4f2880b17.docx");
		array.put(file);*/
		/*array.put(5);
		array.put(6);*/
		json.put("file", file);
		json.put("openGId", "c4ca4238a0b923820dcc509a6f758491");
		//System.out.println(HttpClientUtil.postJson("http://192.168.4.82:8080/jzqzs/pc/share/delete", json));
		//System.out.println(HttpClientUtil.postJson("http://192.168.4.82:8080/jzqzs/api/login", json));
		System.out.println(HttpClientUtil.postJson("http://192.168.4.82:8080/jzqzs/pc/achievement/create", json));
		
	}

}
