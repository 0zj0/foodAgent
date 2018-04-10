package com.doyd.server.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.doyd.utils.StringUtil;
import org.doyd.utils.WeiXinSign;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Administrator
 *
 */
public class SignUtil {
	
	/**
	 * 
	 * @Title: getParamsFromJson
	 * @Description: TODO从json数据中获取参数，复杂对象先对内层签名
	 * @param json
	 * @param secret
	 * @return Map<String,String>
	 * @author 创建人：王伟
	 * @date 创建时间：2016-10-12 上午10:39:35
	 * @author 修改人：王伟
	 * @throws JSONException 
	 * @date 修改时间：2016-10-12 上午10:39:35
	 * @remark 修改备注：
	 */
	public static Map<String, Object> getParamsFromJson(JSONObject json, String secret) throws JSONException{
		
		
		
		if(json == null) return null;
		//获取用户传过来的参数键值对
		Map<String, Object> params = new HashMap<String, Object>();
		Iterator it = json.keys();
		if(it == null) return null;
		while(it.hasNext()){  
			String key = it.next().toString();
			Object param = json.get(key);
        	if(param == null) continue;
        	if(param instanceof JSONObject){
        		if(StringUtil.isEmpty(secret)) continue; 
				params.put(key, WeiXinSign.sign(getParamsFromJson((JSONObject)param, secret), secret));
        	}else if(param instanceof JSONArray){
        		if(StringUtil.isEmpty(secret)) continue; 
        		List<String> list = new ArrayList<String>();
        		for(int i = 0; i < ((JSONArray)param).length(); i ++){
        			list.add(WeiXinSign.sign(getParamsFromJson(((JSONArray)param).getJSONObject(i), secret), secret));
        		}
        		params.put(key, list.toString());
        	}else{
        		if(StringUtil.isEmpty(param.toString())) continue;
        		params.put(key, param.toString());
        	}
		}
		return params;
	}
	
	/**
	 * 
	 * @Title: Sign
	 * @Description: TODO做参数签名
	 * @param parameters参加签名的参数集
	 * @param secret api用户secret（双方已知的密钥）
	 * @return String
	 * @author 创建人：王伟
	 * @date 创建时间：2016-9-9 上午9:45:04
	 * @author 修改人：王伟
	 * @date 修改时间：2016-9-9 上午9:45:04
	 * @remark 修改备注：
	 */
	/*
	public static String signByOld(Map<String, String> params, String secret){
		//返回空说明签名失败
		if(params == null || params.size() <= 0 || StringUtil.isEmpty(secret)) return null;
		// 第一步：把参数按Key的字母顺序排序
		Collection<String> keyset= params.keySet();
		//对key值排序过程
		List<String> list = new ArrayList<String>(keyset); 
		Collections.sort(list);
		
		// 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder(secret);
        for(String key : list){
            String value = params.get(key);
            if (StringUtil.isNotEmpty(value)){
                query.append(key).append(value);
            }
        }
		
        // 第三部：使用md5运算
        return DigestUtils.md5Hex(query.toString());
	}
	*/
	
	public static String sign(Map<String, String> params, String secret){
		//返回空说明签名失败
		if(params == null || params.size() <= 0 || StringUtil.isEmpty(secret)) return null;
		// 第一步：把参数按Key的字母顺序排序
		Collection<String> keyset= params.keySet();
		//对key值排序过程
		List<String> list = new ArrayList<String>(keyset); 
		Collections.sort(list);
		
		// 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        for(String key : list){
            String value = params.get(key);
            if (StringUtil.isNotEmpty(value)){
                query.append(key).append("=").append(value).append("&");
            }
        }
        if(query.length()>0){
        	query.append("secret="+secret);
        }
        // 第三部：使用md5运算
        return DigestUtils.md5Hex(query.toString());
	}
	
	public static void main(String[] args) {
		Map<String,String> map = new HashMap<String, String>();
		map.put("access", "sdgb-fhsj-jdfj-lklm");
		map.put("Nonce", "ssadfasdf");
		map.put("password", "lajdfo");
		map.put("timestamp", "123948192843");
		//System.out.println(sign(map, "simin"));
		
		/*
		JSONObject json = new JSONObject();
		System.out.println(System.currentTimeMillis());
		Map<String, String> parameters = null;
		try {
			json.put("timestamp", Long.parseLong("1479108056595"));
			json.put("orderNo", "9KPqhaoOvx0helyI2S6O3J1PGMVf3PPV");
			parameters = SignUtil.getParamsFromJson(json, "simin");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		/*json.put("playDate", "2016-09-26");
		json.put("timestamp", Long.parseLong("1476157211000"));
		json.put("thirdPartId", "77b872abfe1260053032219354882d91");
		json.put("mobile", "18823704631");
		json.put("payment", 1);
		json.put("isPaid", 1);
		json.put("payTime", "2016-09-16");
		JSONArray subOrders = new JSONArray();
		JSONObject subOrder = new JSONObject();
		subOrder.put("thirdPartSubId", "77b872abfe1260053032219354882d92");
		subOrder.put("sn", "59dsfadsaf42386a87e901977986260c");
		subOrder.put("sourceCnt", 2);
		subOrder.put("playDate", "2016-09-26");
		subOrders.put(subOrder);
		subOrder = new JSONObject();
		subOrder.put("thirdPartSubId", "77b872abfe1260053032219354882d93");
		subOrder.put("sn", "59dsfadsaf42386a87e901977986260d");
		subOrder.put("sourceCnt", 4);
		subOrder.put("playDate", "2016-09-26");
		subOrders.put(subOrder);
		json.put("subOrders", subOrders);
		JSONObject userInfo = new JSONObject();
		userInfo.put("userName", "蜡笔小新");
		userInfo.put("idCard", "421562199804165822");
		userInfo.put("birthDay", "04-16");
		userInfo.put("sex", 1);
		userInfo.put("height", 86);
		json.put("userInfo", userInfo);*/
		//System.out.println(SignUtil.sign(parameters, "simin"));
	}
}
