package com.doyd.biz.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.doyd.sdk.WeixinApi;
import org.doyd.utils.DateUtil;
import org.doyd.utils.StringUtil;
import org.doyd.weixin.entity.user.WeixinAppletUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.doyd.biz.IAppletService;
import com.doyd.biz.IWeixinUserService;
import com.doyd.cache.memory.SysCache;
import com.doyd.core.CoreVars;
import com.doyd.core.action.common.ControllerContext;
import com.doyd.core.action.common.Page;
import com.doyd.core.biz.impl.MyTransSupport;
import com.doyd.dao.IAbortGroupDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.dao.IWxUserOauthDao;
import com.doyd.model.AbortGroup;
import com.doyd.model.Applet;
import com.doyd.model.UserGroup;
import com.doyd.model.WeixinGroup;
import com.doyd.model.WeixinUser;
import com.doyd.model.WxUserOauth;
import com.doyd.module.applet.Enums.AppletEntranceEnum;
import com.doyd.module.applet.Enums.AppletOutEnum;
import com.doyd.module.applet.entrance.IEntranceService;
import com.doyd.module.applet.utils.WeixinUserModelUtil;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;

@Service
public class WeixinUserService extends MyTransSupport implements IWeixinUserService {
	@Autowired
	private IWeixinUserDao weixinUserDao;
	@Autowired
	private IWxUserOauthDao wxUserOauthDao;
	@Autowired
	private IWeixinGroupDao weixinGroupDao;
	@Autowired
	private IUserGroupDao userGroupDao;
	@Autowired
	private IAbortGroupDao abortGroupDao;
	@Autowired
	private IAppletService appletService;

	@Override
	public ApiMessage entrance(String appId, String code, String gEncryptedData
			, String gIv, String t, String info1, String info2){
		//用户授权并判断用户是否第一进入
		ApiMessage am = entrance(appId, code);
		if(am.getState() != 0){
			return am;
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) am.getInfo();
		WxUserOauth wuo = (WxUserOauth) map.get("wuo");
		WeixinUser wu = (WeixinUser) map.get("user");
		boolean newUser = (Boolean)map.get("newUser");
		String session_key = (String) map.get("session_key");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("openGId", null);
		if(StringUtil.isNotEmpty(gEncryptedData) && StringUtil.isNotEmpty(gIv)){
			String openGId = WeixinUserModelUtil.getOpenGId(session_key, gEncryptedData, gIv);
			resultMap.put("openGId", openGId);
		}
		resultMap.put("PCURL", SysCache.getCache().getAppConfig().getPcUrl());
		resultMap.put("openId", wuo.getOpenId());
		//用户第一次登录
		if(newUser){
			resultMap.put("pageType", AppletOutEnum.FirstEnter);
			return am.setInfo(resultMap);
		}
		//用户未授权
		if(StringUtil.isEmpty(wuo.getUnionId()) || wu == null){
			resultMap.put("pageType", AppletOutEnum.Authorized);
			return am.setInfo(resultMap);
		}
		resultMap.put("openId", wuo.getOpenId());
		//用户不存在身份
		if(!wu.isPatriarch() && !wu.isTeacher() && !wu.isDirector()){
			resultMap.put("pageType", AppletOutEnum.CreateIdentity);
			return am.setInfo(resultMap);
		}
		Map<String, Object> userInfo = new HashMap<String, Object>();
		userInfo.put("avatarUrl", wu.getAvatarUrl());
		userInfo.put("nickName", wu.getNickName());
		resultMap.put("userInfo", userInfo);
		//入口类型
		AppletEntranceEnum aee = AppletEntranceEnum.getEnum(t);
		IEntranceService entranceService = null;
		try{
			entranceService = (IEntranceService) ControllerContext.getWac().getBean(aee.getType()+"EntranceService");
		}catch(Exception e){
		}
		//从其他入口进入
		if(entranceService == null){
			resultMap.put("pageType", AppletOutEnum.Home);
			return am.setInfo(resultMap);
		}
		return entranceService.entrance(wu, resultMap, am, info1, info2);
	}
	/**
	 * 入口操作
	 * 
	 * @param code
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-19 下午6:17:31
	 */
	private ApiMessage entrance(String appId, String code){
        //检查参数是否为空
        if (StringUtil.isEmpty(appId) || StringUtil.isEmpty(code)) {
            return new ApiMessage(ReqCode.Entrance, ReqState.ApiParamError);
        }
        Applet applet = appletService.getApplet(appId);
        if(applet == null){
            return new ApiMessage(ReqCode.Entrance, ReqState.ApiParamError);
        }
        //获取微信用户相关信息
    	WeixinAppletUser wau = WeixinApi.getAppletUserService().getAppletUser(appId, null, code);
    	if(wau == null || StringUtil.isEmpty(wau.getOpenid())){
            return new ApiMessage(ReqCode.Entrance, ReqState.ApiParamError);
    	}
    	String openId = wau.getOpenid();
        //获得数据库中的用户信息
        WxUserOauth wuo = wxUserOauthDao.getWxUserOauth(appId, openId);
        //是否为新用户
        boolean newUser = wuo == null;
        //当前日期
        int nowDate = StringUtil.parseInt(DateUtil.now("yyyyMMdd"));
        WeixinUser wu = null;
        //如果用户信息或unionId为空，则进行授权
        if(newUser){
        	wuo = new WxUserOauth();
        	wuo.setAppId(appId);
        	wuo.setOpenId(openId);
        	//修改失败
        	if(!wxUserOauthDao.addWxUserOauth(wuo)){
                return new ApiMessage(ReqCode.Entrance, ReqState.Failure);
        	}
        }else{
        	if(StringUtil.isNotEmpty(wuo.getUnionId())){
        		wu = weixinUserDao.getUserByUnionId(wuo.getUnionId());
        		if(wu != null){
    	        	//修改登录信息
    	        	if(!weixinUserDao.login(wu.getWuId(), System.currentTimeMillis(), nowDate)){
    	                return new ApiMessage(ReqCode.Entrance, ReqState.Failure);
    	        	}
        		}
        	}
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("newUser", newUser);
        resultMap.put("user", wu);
        resultMap.put("wuo", wuo);
        resultMap.put("session_key", wau.getSession_key());
        return new ApiMessage(ReqCode.Entrance, ReqState.Success).setInfo(resultMap);
	}
	/**
	 * 初始化用户信息
	 * 
	 * @param code
	 * @param encryptedData
	 * @param iv
	 * @param source
	 * @return ApiMessage
	 * @author 创建人：ylb
	 * @date 创建时间：2017-12-14 下午6:23:53
	 */
	public ApiMessage init(String appId, String code, String encryptedData, String iv, String source) {
        //检查参数是否为空
        if (StringUtil.isEmpty(appId) || StringUtil.isEmpty(code)) {
            return new ApiMessage(ReqCode.Init, ReqState.ApiParamError);
        }
        Applet applet = appletService.getApplet(appId);
        if(applet == null){
            return new ApiMessage(ReqCode.Init, ReqState.ApiParamError);
        }
        //获取微信用户相关信息
    	WeixinAppletUser wau = WeixinApi.getAppletUserService().getAppletUser(appId, null, code);
    	if(wau == null || StringUtil.isEmpty(wau.getOpenid())){
            return new ApiMessage(ReqCode.Init, ReqState.ApiParamError);
    	}
    	String openId = wau.getOpenid();
    	String session_key = wau.getSession_key();
        //当前日期
        int nowDate = StringUtil.parseInt(DateUtil.now("yyyyMMdd"));
        //获得数据库中的用户信息
        WxUserOauth wuo = wxUserOauthDao.getWxUserOauth(appId, openId);
        //
        WeixinUser user = null;
        
        boolean newtUser = true;
        //是否为新用户
        boolean newWuo = wuo == null;
        if(newWuo){
        	user = new WeixinUser();
            //解密获取授权用户
            WeixinUserModelUtil.createUser(session_key, encryptedData, iv, user);
            if(StringUtil.isEmpty(user.getUnionId())){
                return new ApiMessage(ReqCode.Init, ReqState.NoAuthorized);
            }
        	wuo = new WxUserOauth();
        	wuo.setAppId(appId);
        	wuo.setOpenId(openId);
        	wuo.setUnionId(user.getUnionId());
        	if(!wxUserOauthDao.addWxUserOauth(wuo)){
                return new ApiMessage(ReqCode.Init, ReqState.Failure);
        	}
        //不是新用户但未授权
        }else if(StringUtil.isEmpty(wuo.getUnionId())){
        	user = new WeixinUser();
            //解密获取授权用户
            WeixinUserModelUtil.createUser(session_key, encryptedData, iv, user);
            if(StringUtil.isEmpty(user.getUnionId())){
                return new ApiMessage(ReqCode.Init, ReqState.NoAuthorized);
            }
            wuo.setUnionId(user.getUnionId());
            //修改关联信息unionId
            if(!wxUserOauthDao.updateWxUserOauthForUnionId(wuo.getWuoId(), user.getUnionId())){
                return new ApiMessage(ReqCode.Init, ReqState.Failure);
            }
        }
        //不是新用户
    	WeixinUser wu = weixinUserDao.getUserByUnionId(wuo.getUnionId());
    	newtUser = wu == null;
    	if(newtUser){
        	user = new WeixinUser();
            //解密获取授权用户
            WeixinUserModelUtil.createUser(session_key, encryptedData, iv, user);
            if(StringUtil.isEmpty(user.getUnionId())){
                return new ApiMessage(ReqCode.Init, ReqState.NoAuthorized);
            }
        //是老用户并且数据库中存在用户信息
    	}else{
    		return new ApiMessage(ReqCode.Init, ReqState.Success);
    	}
        user.setTimestamp(System.currentTimeMillis());
        user.setLoginDate(nowDate);
    	user.setSource(source);
    	user.setRegDate(nowDate);
        if(!weixinUserDao.addWeixinUser(user)){
            return new ApiMessage(ReqCode.Init, ReqState.Failure);
        }
		return new ApiMessage(ReqCode.Init, ReqState.Success);
	}

	@Override
	public WeixinUser getUser(String openId) {
		return weixinUserDao.getUser(openId);
	}

	@Override
	public ApiMessage queryWeixinUserList(Page page, String openGId, int type, String key, int wuId, ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		
		UserGroup userGroup = userGroupDao.getUserGroup(wuId, group.getGroupId());
		if(userGroup==null){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		WeixinGroup weixinGroup = weixinGroupDao.getWeixinGroup(openGId);
		if(weixinGroup==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		List<Map<String, Object>> weixinUserList = weixinUserDao.getWeixinUserList(page, weixinGroup.getGroupId(), type, key, wuId);
		/********************* 下面为处理称呼的程序  **********************/
		if(weixinUserList!=null && weixinUserList.size()>0){
			for (Map<String, Object> weixinUser : weixinUserList) {
				if((Boolean)weixinUser.get("director") || (Boolean)weixinUser.get("teacher")){
					weixinUser.put("aliasName", weixinUser.get("realName"));
				}else{
					if(weixinUser.get("aliasName") == null){
						weixinUser.put("aliasName", weixinUser.get("realName")==null?weixinUser.get("nickName"):weixinUser.get("realName"));
					}else{
						String aliasName = (String)weixinUser.get("aliasName");
						aliasName = transferAliasName(aliasName);
						weixinUser.put("aliasName", aliasName);
					}
				}
				weixinUser.remove("realName");
			}
		}
		/********************** 上面为处理称呼的程序   *********************/
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", weixinUserList);
		map.put("totalPage", page.getTotalPage());
		return new ApiMessage(reqCode, ReqState.Success).setInfo(map);
	}
	
	/**
	 * 处理称呼
	 * @Title: transferAliasName
	 * @param aliasName
	 * @return String
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-20 下午4:58:22
	 */
	public static String transferAliasName(String aliasName){
		String[] aliasNames = aliasName.split("\\|");
		if(aliasNames!=null && aliasNames.length>0){
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			for (String name : aliasNames) {
				String[] names = name.split("的");
				List<String> relationList = map.get(names[1]);
				if(relationList==null){
					relationList = new ArrayList<String>();
				}
				relationList.add(names[0]);
				map.put(names[1], relationList);
			}
			List<String> nameTemps = new ArrayList<String>();
			for (Entry<String, List<String>> entry : map.entrySet()) {
				nameTemps.add(org.springframework.util.StringUtils.collectionToDelimitedString(entry.getValue(), "/") + "的" + entry.getKey());
			}
			aliasName = org.springframework.util.StringUtils.collectionToDelimitedString(nameTemps, "，");
		}
		return aliasName;
	}
	
	@Override
	public ApiMessage deleteUser(int ugId, int type, int wuId, ReqCode reqCode) {
		//被操作者群关系
		UserGroup userGroup2 = userGroupDao.getUserGroup(ugId);
		if(userGroup2==null){
			return new ApiMessage(reqCode, ReqState.NoExistMember);
		}
		if(userGroup2.isDirector()){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		//操作者群关系
		UserGroup userGroup1 = userGroupDao.getUserGroup(wuId, userGroup2.getGroupId());
		//跟此群无关系或者不是班主任无权删除
		if(userGroup1==null || !userGroup1.isDirector()){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		String identities = "";
		if(type == 1){
			identities = "patriarch";
			userGroup2.setPatriarch(false);
		}
		if(type == 2){
			identities = "teacher";
			userGroup2.setTeacher(false);
		}
		boolean flag = false;//群成员是否减少
		if(!userGroup2.isDirector() && !userGroup2.isTeacher() && !userGroup2.isPatriarch()){
			userGroup2.setState(3);
			flag = true;
		}
		List<AbortGroup> agList = new ArrayList<>();
		AbortGroup abortGroup = new AbortGroup();
		abortGroup.setGroupId(userGroup2.getGroupId());
		abortGroup.setUgId(userGroup2.getUgId());
		abortGroup.setWuId(userGroup2.getWuId());
		abortGroup.setOperatorId(wuId);
		abortGroup.setAbortType(2);
		abortGroup.setIdentities(identities);
		agList.add(abortGroup);
		boolean r = true;
		TransactionStatus status = getTxStatus();
		try{
			r = r && userGroupDao.deleteUser(userGroup2);
			r = r && abortGroupDao.addAbortGroup(agList);
			if(flag){
				r = r && weixinGroupDao.downCnt(userGroup2.getGroupId(), "peopleCnt");
			}
		}catch (Exception e) {
			e.printStackTrace();
			r = false;
		}finally{
			commit(status, r);
		}
		if(!r){
			return new ApiMessage(reqCode, ReqState.Failure);
		}
		return new ApiMessage(reqCode, ReqState.Success);
	}
	
	@Override
	public ApiMessage updateUser(int ugId, String phone, int wuId, ReqCode reqCode) {
		//被操作者群关系
		UserGroup userGroup2 = userGroupDao.getUserGroup(ugId);
		if(userGroup2==null){
			return new ApiMessage(reqCode, ReqState.NoExistMember);
		}
		//操作者群关系
		UserGroup userGroup1 = userGroupDao.getUserGroup(wuId, userGroup2.getGroupId());
		//跟此群无关系无权修改
		if(userGroup1==null){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		//不是班主任且不是老师
		if(!userGroup1.isDirector() && !userGroup1.isTeacher()){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		//是老师但不是自己
		if(userGroup1.isTeacher() && userGroup1.getWuId()!=userGroup2.getWuId()){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		userGroup2.setPhone(phone);
		if(!userGroupDao.updateUser(userGroup2)){
			return new ApiMessage(reqCode, ReqState.Failure);
		}
		return new ApiMessage(reqCode, ReqState.Success);
	}
	
	@Override
	public ApiMessage batchDeleteUser(String openGId, int[] ugIds, int wuId, ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		//操作者群关系
		UserGroup userGroup = userGroupDao.getUserGroup(wuId, group.getGroupId());
		//跟此群无关系或者不是班主任无权删除
		if(userGroup==null || !userGroup.isDirector()){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		List<UserGroup> userGroupList = userGroupDao.getUserGroupList(ugIds, group.getGroupId());
		if(userGroupList==null || userGroupList.size()!=ugIds.length){
			return new ApiMessage(reqCode, ReqState.ExistNoPowerItem);
		}
		List<AbortGroup> agList = new ArrayList<>();
		for (UserGroup ug : userGroupList) {
			AbortGroup abortGroup = new AbortGroup();
			abortGroup.setGroupId(ug.getGroupId());
			abortGroup.setUgId(ug.getUgId());
			abortGroup.setWuId(ug.getWuId());
			abortGroup.setOperatorId(wuId);
			abortGroup.setAbortType(2);
			abortGroup.setIdentities(ug.isTeacher()?"teacher":"patriarch");
			agList.add(abortGroup);
		}
		boolean r = true;
		TransactionStatus status = getTxStatus();
		try{
			r = r && userGroupDao.batchDeleteUser(ugIds, group.getGroupId());
			r = r && abortGroupDao.addAbortGroup(agList);
			r = r && weixinGroupDao.downCnt(group.getGroupId(), "peopleCnt", ugIds.length);
		}catch (Exception e) {
			e.printStackTrace();
			r = false;
		}finally{
			commit(status, r);
		}
		if(!r){
			return new ApiMessage(reqCode, ReqState.Failure);
		}
		return new ApiMessage(reqCode, ReqState.Success);
	}
	
	@Override
	public ApiMessage getGroupUserCnt(String openGId, int type, int wuId, ReqCode reqCode) {
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		
		UserGroup userGroup = userGroupDao.getUserGroup(wuId, group.getGroupId());
		if(userGroup==null){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		return new ApiMessage(reqCode, ReqState.Success).setInfo(userGroupDao.getUserCnt(group.getGroupId(), type));
	}
	
	@Override
	public ApiMessage getUser(String openId, String openGId) {
		//判断参数信息
		if(StringUtil.isEmpty(openId)){
			return new ApiMessage(ReqCode.User, ReqState.ApiParamError);
		}
		//获得用户信息
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.User, ReqState.NoExistUser);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("phone", wu.getPhone());
		WeixinGroup wg = null;
		//传入群Id--获得群别名
		if(StringUtil.isNotEmpty(openGId)){
			//获得群信息
			wg = weixinGroupDao.getWeixinGroup(openGId);
			if(wg == null){
				return new ApiMessage(ReqCode.User, ReqState.NoExistGroup);
			}
			//获的用户群关系
			UserGroup ug = userGroupDao.getUserGroup(wu.getWuId(), wg.getGroupId());
			if(ug == null){
				return new ApiMessage(ReqCode.User, ReqState.UserNotInGroup);
			}
			resultMap.put("aliasName", ug.getAliasName());
			resultMap.put("phone", StringUtil.isEmpty(ug.getPhone()) ? wu.getPhone() : ug.getPhone());
		}
		resultMap.put("avatarUrl", wu.getAvatarUrl());
		resultMap.put("nickName", wu.getNickName());
		resultMap.put("realName", wu.getRealName());
		return new ApiMessage(ReqCode.User, ReqState.Success).setInfo(resultMap);
	}
	@Override
	public ApiMessage updateUser(String openId, String openGId, String phone,
			String realName) {
		//判断参数是否合法
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(phone) || StringUtil.isEmpty(realName)){
			return new ApiMessage(ReqCode.UpdateUser, ReqState.ApiParamError);
		}
		//电话号码不是移动电话并且不是座机
		if(!phone.matches(CoreVars.RULES_MOBILE) && !phone.matches(CoreVars.RULES_PHONE)){
			return new ApiMessage(ReqCode.UpdateUser, ReqState.WrongPhone);
		}
		//获得用户信息
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.UpdateUser, ReqState.NoExistUser);
		}
		UserGroup ug = new UserGroup();
		ug.setWuId(wu.getWuId());
		ug.setPhone(phone);
		//如果传入群Id则修改用户群关系的信息
		if(StringUtil.isNotEmpty(openGId)){
			WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
			if(wg == null){
				return new ApiMessage(ReqCode.UpdateUser, ReqState.NoExistGroup);
			}
			if(!userGroupDao.existUserGroup(wu.getWuId(), wg.getGroupId(), 0)){
				return new ApiMessage(ReqCode.UpdateUser, ReqState.UserNotInGroup);
			}
			ug.setGroupId(wg.getGroupId());
		}
		TransactionStatus txStatus = getTxStatus();
		boolean r = true;
		try {
			//没有传入群Id，直接修改用户信息
			if(StringUtil.isEmpty(openGId)){
				r = weixinUserDao.updateUser(wu.getWuId(), realName, phone) 
						&& userGroupDao.updatePhone(wu.getWuId(), phone);
			//传入群Id，修改用户群关系--和用户信息
			}else{
				r = userGroupDao.updateUser(ug) 
						&& weixinUserDao.updateUser(wu.getWuId(), realName, phone);
			}
		} catch (Exception e) {
			r = false;
		}finally{
			commit(txStatus, r);
		}
		ReqState reqState = r ? ReqState.Success : ReqState.Failure;
		return new ApiMessage(ReqCode.UpdateUser, reqState);
	}
	
}
