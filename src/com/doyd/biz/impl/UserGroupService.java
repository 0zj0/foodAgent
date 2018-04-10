package com.doyd.biz.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.doyd.biz.IUserGroupService;
import com.doyd.biz.IWeixinGroupService;
import com.doyd.core.CoreVars;
import com.doyd.core.biz.impl.MyTransSupport;
import com.doyd.dao.IAbortGroupDao;
import com.doyd.dao.IChildrenDao;
import com.doyd.dao.IElectionLordDao;
import com.doyd.dao.IGroupMessageDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.model.AbortGroup;
import com.doyd.model.Children;
import com.doyd.model.ElectionLord;
import com.doyd.model.GroupMessage;
import com.doyd.model.UserGroup;
import com.doyd.model.WeixinGroup;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;

import org.apache.log4j.Logger;
import org.doyd.utils.StringUtil;

@Service
public class UserGroupService extends MyTransSupport implements IUserGroupService {
	
	private static Logger logger = Logger.getLogger(UserGroupService.class);
	
	@Autowired
	private IUserGroupDao userGroupDao;
	@Autowired
	private IWeixinGroupDao weixinGroupDao;
	@Autowired
	private IWeixinUserDao weixinUserDao;
	@Autowired
	private IChildrenDao childrenDao;
	@Autowired
	private IGroupMessageDao groupMessageDao;
	@Autowired
	private IElectionLordDao electionLordDao;
	@Autowired
	private IAbortGroupDao abortGroupDao;
	@Autowired
	private IWeixinGroupService weixinGroupService;

	@Override
	public UserGroup getUserGroup(int wuId, int groupId) {
		return userGroupDao.getUserGroup(wuId, groupId);
	}

	@Override
	public ApiMessage getGroupUser(String openGId, String openId, String key) {
		if(StringUtil.isEmpty(openGId) || StringUtil.isEmpty(openId)){
			return new ApiMessage(ReqCode.GroupUser, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.GroupUser, ReqState.NoExistUser);
		}
		//获得微信群信息
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return new ApiMessage(ReqCode.GroupUser, ReqState.NoExistGroup);
		}
		UserGroup userGroup = userGroupDao.getUserGroup(wu.getWuId(), wg.getGroupId());
		if(userGroup == null){
			return new ApiMessage(ReqCode.GroupUser, ReqState.UserNotInGroup);
		}
		//用户不是班主任，并且不是老师
		if(!userGroup.isDirector() && !userGroup.isTeacher()){
			return new ApiMessage(ReqCode.GroupUser, ReqState.NoPower);
		}
		//获得群成员列表
		List<Map<String, Object>> userGroupList = userGroupDao.getUserGroupList(wg.getGroupId(), key);
		if(userGroupList == null || userGroupList.size() <= 0){
			return new ApiMessage(ReqCode.GroupUser, ReqState.Success);
		}
		//班主任信息
		Map<String, Object> directorMap = new HashMap<String, Object>();
		//老师列表
		List<Map<String, Object>> teacherList = new ArrayList<Map<String,Object>>();
		//家长列表
		List<Map<String, Object>> patriarchList = new ArrayList<Map<String,Object>>();
		//群用户总数
		int totalCnt = userGroupList.size();
		//通讯录计数
		int addressBookCnt = 0;
		//用户通讯录是否增加
		for (int i=0;i<userGroupList.size();i++) {
			Map<String, Object> map = userGroupList.get(i);
			int ugId = StringUtil.parseIntByObj(map.get("ugId"));
			String id = (String) map.get("openId");
			String phone = (String) map.get("phone");
			String nickName = (String) map.get("nickName");
			String realName = (String) map.get("realName");
			String subjects = (String) map.get("subjects");
			String avatarUrl = (String) map.get("avatarUrl");
			boolean director = (Boolean)map.get("director");
			boolean teacher = (Boolean)map.get("teacher");
			boolean patriarch = (Boolean)map.get("patriarch");
			
			realName = StringUtil.isEmpty(realName) ? nickName : realName;
			//判断匹配的是否是别名，如果别名则
			boolean flag = StringUtil.isEmpty(key) || 
					(StringUtil.isNotEmpty(realName) && realName.contains(key)) ||
					(StringUtil.isNotEmpty(nickName) && nickName.contains(key));
			//身份为班主任
			if(director && flag){
				directorMap = getUserGroupMap(ugId, id, phone, realName, "班主任", avatarUrl);
			}
			//身份为老师
			if(teacher && flag){
				subjects = StringUtil.isEmpty(subjects) ? "老师" : subjects+"老师";
				teacherList.add(getUserGroupMap(ugId, id, phone, realName, subjects, avatarUrl));
			}
			//通讯录没有添加并且电话不为空
			if(StringUtil.isNotEmpty(phone)){
				addressBookCnt++;
			}
			if(patriarch){
				String an = (String) map.get("aliasName");
				if(StringUtil.isNotEmpty(an)){
					an = WeixinUserService.transferAliasName(an);
				}
				patriarchList.add(getUserGroupMap(ugId, id, phone, an, nickName, avatarUrl));
			}
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("totalCnt", totalCnt);
		resultMap.put("addressBookCnt", addressBookCnt);
		resultMap.put("director", directorMap);
		resultMap.put("teacherList", teacherList);
		resultMap.put("patriarchList", patriarchList);
		return new ApiMessage(ReqCode.GroupUser, ReqState.Success).setInfo(resultMap);
	}
	private Map<String, Object> getUserGroupMap(int ugId, String openId, String phone, String aliasName
			, String nickName, String avatarUrl){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ugId", ugId);
		map.put("openId", openId);
		map.put("phone", phone);
		map.put("aliasName", aliasName);
		map.put("nickName", nickName);
		map.put("avatarUrl", avatarUrl);
		return map;
	}

	@Override
	public ApiMessage identity(String openId, String openGId) {
		List<String> list = new ArrayList<String>();
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.UserIdentify, ReqState.NoExistUser);
		}
		if(wu.isDirector()){
			list.add("director");
		}
		if(wu.isTeacher()){
			list.add("teacher");
		}
		if(wu.isPatriarch()){
			list.add("patriarch");
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("identities", list);
		resultMap.put("existName", false);
		resultMap.put("groupName", null);
		resultMap.put("realName", wu.getRealName());
		//如果传入openGId，则返回是否存在名称和群名称
		if(StringUtil.isNotEmpty(openGId)){
			WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
			if(wg != null && StringUtil.isNotEmpty(wg.getGroupName())){
				resultMap.put("existName", true);
				resultMap.put("groupName", wg.getGroupName());
			}
		}
		return new ApiMessage(ReqCode.UserIdentify, ReqState.Success).setInfo(resultMap);
	}

	@Override
	public ApiMessage addIdentity(String baseUrl, String openId, String openGId, String groupName, String[] identities
			, String childName, int education, int grade, String realName, String relation) {
		//参数错误
		if(identities == null || identities.length <= 0 
				|| StringUtil.isEmpty(openId)){
			return new ApiMessage(ReqCode.CreateUserIdentify, ReqState.ApiParamError);
		}
		//如果openId不为空，则从群内进入，群内进入身份为单选
		if(StringUtil.isNotEmpty(openGId) && identities.length > 1){
			return new ApiMessage(ReqCode.CreateUserIdentify, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.CreateUserIdentify, ReqState.NoExistUser);
		}
		boolean director = false; // 班主任
		boolean teacher = false; // 老师
		boolean patriarch = false; // 家长
		//判断参数，并获得添加的身份
		for (int i = 0; i < identities.length; i++) {
			if("director".equals(identities[i])){
				//注册为班主任时，真实姓名为空或者用户已经是班主任
				if(StringUtil.isEmpty(realName) || wu.isDirector()){
					return new ApiMessage(ReqCode.CreateUserIdentify, ReqState.ApiParamError);
				}
				director = true;
				wu.setDirector(true);
			}else if("teacher".equals(identities[i])){
				//注册为班主任时，真实姓名为空或者用户已经是老师
				if(StringUtil.isEmpty(realName) || wu.isTeacher()){
					return new ApiMessage(ReqCode.CreateUserIdentify, ReqState.ApiParamError);
				}
				teacher = true;
				wu.setTeacher(true);
			}else if("patriarch".equals(identities[i])){
				//注册为班主任时，参数为空或者用户已经是家长
				if(StringUtil.isEmpty(childName) || StringUtil.isEmpty(relation) 
						|| education <= 0 || grade <= 0 || wu.isPatriarch()){
					return new ApiMessage(ReqCode.CreateUserIdentify, ReqState.ApiParamError);
				}
				patriarch = true;
				wu.setPatriarch(true);
			}
		}
		//如果没有正确的身份
		if(!patriarch && !teacher && !director){
			return new ApiMessage(ReqCode.CreateUserIdentify, ReqState.ApiParamError);
		}
		Children children =  null;
		//添加了家长身份
		if(patriarch){
			children = new Children();
			children.setWuId(wu.getWuId());
			children.setChildName(childName);
			children.setEducation(education);
			children.setRelation(relation);
			children.setGrade(grade);
		}
		boolean correct = false;
		WeixinGroup wg = null;
		UserGroup ug = null;
		boolean isAddPeopleCnt = false;
		boolean existGroupName = false;
		boolean newGroup = false;
		//判读是否传入群Id
		if(StringUtil.isNotEmpty(openGId)){
			wg = weixinGroupDao.getWeixinGroup(openGId);
			//如果（群不存在或者群名称为空）但传入群名称为空
			if((wg == null || StringUtil.isEmpty(wg.getGroupName())) && StringUtil.isEmpty(groupName)){
				return new ApiMessage(ReqCode.CreateUserIdentify, ReqState.ApiParamError);
			}
			if(wg != null && StringUtil.isNotEmpty(wg.getGroupName()) && StringUtil.isNotEmpty(groupName)){
				existGroupName = true;
			}
			//判断群是否存在
			if(wg == null){
				wg = new WeixinGroup();
				wg.setOpenGId(openGId);
				wg.setGroupName(groupName);
				if(director){
					wg.setDirectorId(wu.getWuId());
				}
			}else{
				//注册班主任身份时，
				if(director){
					//判断群内是否存在班主任
					if(wg.getDirectorId() > 0){
						correct = true;
						director = false;
					}else{
						wg.setDirectorId(wu.getWuId());
					}
				}
			}
			//如果没有正确的身份
			if(!patriarch && !teacher && !director){
				return new ApiMessage(ReqCode.CreateUserIdentify, ReqState.GroupExistDirector);
			}
			//只有用户第一次进入时，才有openGId
			ug = new UserGroup();
			ug.setWuId(wu.getWuId());
			ug.setDirector(director);
			ug.setTeacher(teacher);
			ug.setPatriarch(patriarch);
			if(patriarch){
				ug.setAliasName(childName + "的" + relation);
			}
			newGroup = wg.getGroupId() <= 0;
			if(!userGroupDao.existUserGroup(wu.getWuId(), wg.getGroupId(), 0)){
				isAddPeopleCnt = true;
			}
		}
		if(director || teacher){
			wu.setRealName(realName);
		}
		TransactionStatus txStatus = getTxStatus();
		boolean r = false;
		try {
			//修改微信用户身份和添加孩子
			r = weixinUserDao.addIdentity(wu);
			if(r && patriarch){
				r = childrenDao.addChildren(children);
			}
			//修改成功，并且存在群Id
			if(r && StringUtil.isNotEmpty(openGId)){
				//不存在群
				if(wg.getGroupId() <= 0){
					r = weixinGroupDao.addWeixinGroup(wg);
				}else{
					if(director){
						r = weixinGroupDao.updateDirectorId(wg.getGroupId(), wu.getWuId());
					}
					//群名称为空，或者班主任提交了群名称
					if(StringUtil.isEmpty(wg.getGroupName()) 
							|| (director && StringUtil.isNotEmpty(groupName))){
						r = r && weixinGroupDao.updateGroupName(wg.getGroupId(), groupName);
					}
				}
				if(r && !newGroup && isAddPeopleCnt){
					//添加群人数
					r = weixinGroupDao.updatePeopleCnt(openGId, 1, 1);
				}
				ug.setGroupId(wg.getGroupId());
				if(patriarch){
					ug.setChildId(children.getChildId());
				}
				r = r && userGroupDao.addUserGroup(ug);
			}
		} catch (Exception e) {
			r = false;
		}finally{
			commit(txStatus, r);
		}
		if(r && (isAddPeopleCnt || newGroup)){
			weixinGroupService.addGroupPic(openGId, baseUrl);
		}
		if(r){
			if(correct){
				return new ApiMessage(ReqCode.CreateUserIdentify, ReqState.GroupExistDirector);
			}else if(existGroupName){
				return new ApiMessage(ReqCode.CreateUserIdentify, ReqState.ExistGroupName);
			}else{
				return new ApiMessage(ReqCode.CreateUserIdentify, ReqState.Success);
			}
		}
		return new ApiMessage(ReqCode.CreateUserIdentify, ReqState.Failure);
	}

	@Override
	public ApiMessage home(String openId, String openGId) {
		if(StringUtil.isEmpty(openId)){
			return new ApiMessage(ReqCode.HomeInfo, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.HomeInfo, ReqState.NoExistUser);
		}
		//如果用户没有身份
		if(!wu.isDirector() && !wu.isTeacher() && !wu.isPatriarch()){
			return new ApiMessage(ReqCode.HomeInfo, ReqState.ApiParamError);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<String> subjects = new ArrayList<String>();
		//获得用户拥有身份
		List<String> identity = new ArrayList<String>();
		if(wu.isDirector()){ 
			subjects.add("班主任");
			identity.add("director");
		}
		if(wu.isTeacher()){ 
			subjects.add("老师");
			identity.add("teacher");
		}
		if(wu.isPatriarch()){ 
			subjects.add("家长");
			identity.add("patriarch");
		}
		resultMap.put("identities", identity);
		Map<String, Object> userInfo = new HashMap<String, Object>();
		userInfo.put("avatarUrl", wu.getAvatarUrl());
		userInfo.put("nickName", wu.getNickName());
		resultMap.put("userInfo", userInfo);
		//孩子信息列表
		List<Map<String, Object>> childMapList = new ArrayList<Map<String,Object>>();
		//班主任群列表
		List<Map<String, Object>> directorGroup = new ArrayList<Map<String,Object>>();
		//老师群列表
		List<Map<String, Object>> teacherGroup = new ArrayList<Map<String,Object>>();
		//临时存放孩子信息
		Map<Integer, Map<String, Object>> childMap = new HashMap<Integer, Map<String,Object>>();
		//如果用户拥有家长身份，获得用户孩子信息
		if(wu.isPatriarch()){
			List<Children> childList = childrenDao.getChildrenList(wu.getWuId());
			if(childList != null && childList.size() > 0){
				for (Children children : childList) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("childId", children.getChildId());
					map.put("childName", children.getChildName());
					map.put("relation", children.getRelation());
					map.put("show", false);
					map.put("groupList", null);
					childMapList.add(map);
					childMap.put(children.getChildId(), map);
				}
			}
		}
		//消息map
		Map<Integer, List<Map<String, Object>>> gmMap = new HashMap<Integer, List<Map<String,Object>>>();
		//遍历中，上一个groupId
		int oldGroupId = -1;
		//
		List<Map<String, Object>> groupGm = null;
		//标识是否获得最新的消息
		boolean newMsg = false;
		resultMap.put("msg", null);
		//获得用户消息
		List<Map<String, Object>> gmList = groupMessageDao.getGroupMessageList(wu.getWuId());
		if(gmList != null && gmList.size() > 0){
			groupMessageDao.push(wu.getWuId());
			for (int i=0;i<gmList.size();i++) {
				Map<String, Object> gm = gmList.get(i);
				int msgType = StringUtil.parseIntByObj(gm.get("msgType"));
				int isRead = StringUtil.parseIntByObj(gm.get("isRead"));
				int groupId = StringUtil.parseIntByObj(gm.get("groupId"));
				//和上次的不同个群的消息
				if(groupId != oldGroupId){
					//不是第一个遍历的，将上次的消息集合添加入map中
					if(i > 0){
						gmMap.put(oldGroupId, groupGm);
					}
					oldGroupId = groupId;
					groupGm = new ArrayList<Map<String,Object>>();
				}
				//弹出的消息并且消息类型是弹出的并且消息未推送
				if(!newMsg && (msgType < 7 || msgType == 9) && isRead == 1){
					newMsg = true;
					resultMap.put("msg", gm);
				}else{
					gm.remove("groupId");
					groupGm.add(gm);
				}
				//如果是最后一条，将消息添加入map中
				if(i == gmList.size()-1){
					gmMap.put(oldGroupId, groupGm);
				}
			}
		};
		oldGroupId = -1;
		//获得用户群列表
		List<Map<String, Object>> ugList = userGroupDao.getUserGroupListByWuId(wu.getWuId(), openGId);
		//如果用户群为空，则直接返回
		if(ugList == null || ugList.size() <= 0){
			resultMap.put("directorGroup", directorGroup);
			resultMap.put("teacherGroup", teacherGroup);
			resultMap.put("childList", childMapList);
			resultMap.put("msgList", gmMap.get(0));
			resultMap.put("label", subjects);
			resultMap.put("identities", identity);
			return new ApiMessage(ReqCode.HomeInfo, ReqState.Success).setInfo(resultMap);
		}
		//通知是否已添加
		boolean isNotify = false;
		//是否第一个没有任教科目的老师项
		boolean teacherBubble = false;
		//是否为默认显示
		boolean show = false;
		//小程序身份显示的项
		int showItem = -1;
		//是否已经调整展示列表
		boolean isShow = false;
		//遍历用户群信息
		for (int j=0;j<ugList.size();j++) {
			Map<String, Object> map = ugList.get(j);
			int groupId = StringUtil.parseIntByObj(map.get("groupId"));
			int childId = StringUtil.parseIntByObj(map.get("childId"));
			boolean director = (Boolean) map.get("director");
			boolean teacher = (Boolean) map.get("teacher");
			boolean patriarch = (Boolean) map.get("patriarch");
			String subject = (String) map.get("subjects");
			String tmpOpenGId = (String) map.get("openGId");
			boolean newUse = (Boolean) map.get("newUse");
			//和上一个是不同个群
			if(groupId != oldGroupId){
				isNotify = false;
				oldGroupId = groupId;
				//群内存在班主任身份，消息首先通知班主任
				if(director){
					Map<String, Object> tmp = working(map);
					tmp.put("show", false);
					tmp.put("newLeave", map.get("newLeave"));
					tmp.put("msgList", gmMap.get(groupId));
					if(!show && (tmpOpenGId.equals(openGId) || newUse || j == ugList.size()-1)){
						tmp.put("show", true);
						showItem = 2;
						show = true;
					}
					tmp.put("newNotify", map.get("newNotify"));
					//如果是展开的就插入第一行
					if(show && !isShow){
						isShow = true;
						directorGroup.add(0, tmp);
					}else{
						directorGroup.add(tmp);
					}
					isNotify = true;
				}
				if(teacher){
					Map<String, Object> tmp = working(map);
					tmp.put("show", false);
					tmp.put("teacherBubble", false);
					tmp.put("subjects", new String[]{});
					if(StringUtil.isNotEmpty(subject)){
						if(subjects.contains("老师")){
							subjects.remove("老师");
						}
						String[] s = subject.split("/");
						for(int i=0;i<s.length;i++){
							if(!subjects.contains(s[i]+"老师")){
								subjects.add(s[i]+"老师");
							}
						}
						tmp.put("subjects", s);
					}else if(!teacherBubble){
						tmp.put("teacherBubble", true);
						teacherBubble = true;
					}
					if(!show && (tmpOpenGId.equals(openGId) || newUse || j == ugList.size()-1)){
						tmp.put("show", true);
						showItem = 1;
						show = true;
					}
					int newLeave = 0;
					int newNotify = 0;
					if(!isNotify){
						newLeave = StringUtil.parseIntByObj(map.get("newLeave"));
						newNotify = StringUtil.parseIntByObj(map.get("newNotify"));
						//消息如果没有通知班主任，则通知老师身份
						tmp.put("msgList", gmMap.get(groupId));
					}
					tmp.put("newLeave", newLeave);
					tmp.put("newNotify", newNotify);
					//如果是展开的就插入第一行
					if(show && !isShow){
						isShow = true;
						teacherGroup.add(0, tmp);
					}else{
						teacherGroup.add(tmp);
					}
					isNotify = true;
				}
			}
			if(patriarch){
				Map<String, Object> tmp = working(map);
				tmp.put("show", false);
				tmp.put("newWork", map.get("newWork"));
				int newNotify = 0;
				/*if(!show && tmpOpenGId.equals(openGId)){
					tmp.put("show", true);
					show = true;
				}*/
				if(!isNotify){
					//消息如果前面都没有通知，则通知第一个家长身份
					tmp.put("msgList", gmMap.get(groupId));
					newNotify = StringUtil.parseIntByObj(map.get("newNotify"));
				}
				tmp.put("newNotify", newNotify);
				Map<String, Object> child = childMap.get(childId);
				if(child == null){
					continue;
				}
				if(!show && (tmpOpenGId.equals(openGId) || newUse || j == ugList.size()-1)){
					child.put("show", true);
					showItem = 0;
					tmp.put("show", true);
					show = true;
				}
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> groupList = (List<Map<String, Object>>) child.get("groupList");
				if(groupList == null){
					groupList = new ArrayList<Map<String,Object>>();
				}
				//如果是展开的就插入第一行
				if(show && !isShow){
					isShow = true;
					groupList.add(0, tmp);
					//并把孩子设置为第一行
					for (int i=0;i<childMapList.size();i++) {
						Map<String, Object> child2 = childMapList.get(i);
						int childId2 = StringUtil.parseIntByObj(child2.get("childId"));
						if(childId == childId2){
							childMapList.remove(i);
							childMapList.add(0, child);
						}
					}
				}else{
					groupList.add(tmp);
				}
				child.put("groupList", groupList);
			}
		}
		resultMap.put("showItem", showItem < 0 ? null : showItem);
		resultMap.put("directorGroup", directorGroup);
		resultMap.put("teacherGroup", teacherGroup);
		resultMap.put("childList", childMapList);
		resultMap.put("msgList", gmMap.get(0));
		resultMap.put("label", subjects);
		resultMap.put("identities", identity);
		return new ApiMessage(ReqCode.HomeInfo, ReqState.Success).setInfo(resultMap);
	}
	private Map<String, Object> working(Map<String, Object> ug){
		Map<String, Object> tmp = new HashMap<String, Object>();
		tmp.put("ugId", ug.get("ugId"));
		tmp.put("openGId", ug.get("openGId"));
		tmp.put("groupName", ug.get("groupName"));
		tmp.put("groupPic", ug.get("groupPic"));
		return tmp;
	}

	@Override
	public ApiMessage addUserGroup(String baseUrl, String openId, String openGId, String groupName,
			String identity, int childId) {
		logger.debug("进入添加群关系,openGId："+openGId);
		//判断参数信息是否合法
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(openGId) 
				|| StringUtil.isEmpty(identity) || ("patriarch".equals(identity) && childId <= 0)){
			return new ApiMessage(ReqCode.UserGroupAdd, ReqState.ApiParamError);
		}
		boolean director = "director".equals(identity);
		boolean teacher = "teacher".equals(identity);
		boolean patriarch = "patriarch".equals(identity);
		if(!director && !teacher && !patriarch){
			return new ApiMessage(ReqCode.UserGroupUnbind, ReqState.ApiParamError);
		}
		//用户信息
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.UserGroupAdd, ReqState.NoExistUser);
		}
		if(director && !wu.isDirector()){
			return new ApiMessage(ReqCode.UserGroupAdd, ReqState.ApiParamError);
		}else if(teacher && !wu.isTeacher()){
			return new ApiMessage(ReqCode.UserGroupAdd, ReqState.ApiParamError);
		}else if(patriarch && !wu.isPatriarch()){
			return new ApiMessage(ReqCode.UserGroupAdd, ReqState.ApiParamError);
		}
		//获得群信息
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		boolean existGroupName = false;
		if(wg != null && StringUtil.isNotEmpty(wg.getGroupName()) && StringUtil.isNotEmpty(groupName)){
			existGroupName = true;
		}
		if(wg == null){
			wg = new WeixinGroup();
			wg.setOpenGId(openGId);
			wg.setGroupName(groupName);
		}
		//如果添加的关系是班主任并且群存在，则判断是否存在班主任
		if(director && wg.getGroupId() > 0 && wg.getDirectorId() > 0){
			//如果群班主任是当前用户
			if(wg.getDirectorId() == wu.getWuId()){
				return new ApiMessage(ReqCode.UserGroupAdd, ReqState.ExistUserGroup);
			}
			//判断用户是否存在纠正信息
			List<ElectionLord> elList = electionLordDao.getElectionLordList(wu.getWuId(), wg.getGroupId());
			//不存在纠正信息
			if(elList == null || elList.size() <= 0){
				return new ApiMessage(ReqCode.UserGroupAdd, ReqState.GroupExistDirector);
			}
			boolean correct = false;
			boolean vote = false;
			ElectionLord tmpEl = null;
			for (ElectionLord el : elList) {
				//存在纠正信息并纠正失败的，只能选择投票
				if(el.getElectionType() == 1 && el.getState() != 2){
					correct = true;
				//存在投票信息并正在进行的，进入投票详情页
				}else if(el.getElectionType() == 2 && el.getState() == 1){
					vote = true;
					tmpEl = el;
				}else if(el.getState() == 2){
					correct = false;
					vote = false;
					tmpEl = null;
				}
			}
			if(vote){
				return new ApiMessage(ReqCode.UserGroupAdd, ReqState.UserExistVote).setInfo(tmpEl.getElectionId());
			}
			if(correct){
				return new ApiMessage(ReqCode.UserGroupAdd, ReqState.UserExistElection);
			}
			return new ApiMessage(ReqCode.UserGroupAdd, ReqState.GroupExistDirector);
		}
		boolean existTeacherIdentity = false;
		//如果添加的关系是班主任并且群不存在班主任Id
		if(director && wg.getDirectorId() <= 0){
			wg.setDirectorId(wu.getWuId());
		}
		UserGroup ug = null;
		//班群存在获得用户群关系
		if(wg.getGroupId() > 0){
			ug = userGroupDao.getUserGroup(wu.getWuId(), wg.getGroupId());
		}
		//是否添加群用户数，并且群不是新的
		boolean isAddPeopleCnt = ug == null;
		
		boolean newGroup = wg.getGroupId() <= 0;
		//用户群关系中存在老师身份
		if(ug != null && teacher && ug.isTeacher()){
			return new ApiMessage(ReqCode.UserGroupAdd, ReqState.ExistUserGroup);
		}
		//如果用户添加老师身份，并且已存在班主任身份，，提示用户不用添加
		if(ug != null && teacher && ug.isDirector()){
			return new ApiMessage(ReqCode.UserGroupAdd, ReqState.PowerRepeat);
		}
		//用户不存在关系，或(添加的是否孩子群关系并关系中存在孩子)
		if(ug == null || (patriarch && ug.getChildId() > 0)){
			UserGroup tmpUg = ug;
			ug = new UserGroup();
			ug.setWuId(wu.getWuId());
			ug.setDirector(director);
			ug.setTeacher(teacher);
			ug.setPatriarch(patriarch);
			ug.setPhone(wu.getPhone());
			//注册的是家长，并之前群内存在孩子，保存之前班主任和老师的身份
			if(patriarch && tmpUg != null && tmpUg.getChildId() > 0){
				ug.setDirector(tmpUg.isDirector());
				ug.setTeacher(tmpUg.isTeacher());
				ug.setSubjects(tmpUg.getSubjects());
				ug.setPhone(tmpUg.getPhone());
			}
		}else{
			if(director){
				ug.setDirector(true);
				//如果添加的是班主任身份，并且之前存在老师身份
				if(ug.isTeacher()){
					existTeacherIdentity = true;
					ug.setTeacher(false);
				}
			}else if(teacher){
				ug.setTeacher(teacher);
			}else if(patriarch){
				ug.setPatriarch(patriarch);
			}
		}
		//如果科任老师被提出重进入，提示班主任
		if(teacher && wg.getGroupId() > 0 && wg.getDirectorId() > 0){
			//获得退群信息
			AbortGroup ag = abortGroupDao.getAbortGroup(wg.getGroupId(), wu.getWuId());
			//如果存在退群信息，并且是被班主任移除群的，并且移除的是老师身份
			if(ag != null && ag.getAbortType() == 2 && ag.getIdentities().contains("teacher")){
				GroupMessage gm = new GroupMessage();
				gm.setGroupId(wg.getGroupId());
				gm.setWuId(wg.getDirectorId());
				gm.setTitle("老师申请入群");
				gm.setContent(wu.getNickName() + "申请在群内添加老师身份");
				gm.setMsgType(7);
				gm.setId(wu.getWuId());
				boolean r = groupMessageDao.addGroupMessage(gm);
				ReqState reqState = r ? ReqState.EnterAgain : ReqState.Failure;
				return new ApiMessage(ReqCode.UserGroupAdd, reqState);
			}
		}
		//判断添加的是否是家长，是的话添加别名和孩子Id
		if(patriarch){
			//如果群存在并且群内存在这个孩子
			if(wg.getGroupId() > 0 && 
					userGroupDao.existUserGroup(wu.getWuId(), wg.getGroupId(), childId)){
				return new ApiMessage(ReqCode.UserGroupAdd, ReqState.ExistChildren);
			}
			Children child = childrenDao.getChildren(childId);
			if(child == null){
				return new ApiMessage(ReqCode.UserGroupAdd, ReqState.NoExistChild);
			}
			if(child.getWuId() != wu.getWuId()){
				return new ApiMessage(ReqCode.BatchAddGroup, ReqState.NoPower);
			}
			ug.setAliasName(child.getChildName() + "的" + child.getRelation());
			ug.setChildId(childId);
		}
		TransactionStatus txStatus = getTxStatus();
		boolean r = true;
		try {
			//群不存在
			if(wg.getGroupId() <= 0){
				r = weixinGroupDao.addWeixinGroup(wg);
			//群存在，并且注册的是班主任
			}else{ 
				if(director){
					r = weixinGroupDao.updateDirectorId(wg.getGroupId(), wu.getWuId());
				}
				//群名称为空，或者班主任提交了群名称
				if((StringUtil.isEmpty(wg.getGroupName()) && StringUtil.isNotEmpty(groupName)) 
						|| (director && StringUtil.isNotEmpty(groupName))){
					r = r && weixinGroupDao.updateGroupName(wg.getGroupId(), groupName);
				}
			}
			//添加微信群用户数
			if(r && !newGroup && isAddPeopleCnt){
				logger.debug("添加群成员数");
				r = weixinGroupDao.updatePeopleCnt(openGId, 1, 1);
			}
			//不存在群关系
			if(r && ug.getUgId() <= 0){
				ug.setGroupId(wg.getGroupId());
				r = userGroupDao.addUserGroup(ug);
			//存在群关系
			}else if(r){
				r = userGroupDao.updateIdentity(ug, director, teacher, patriarch);
			}
		} catch (Exception e) {
			r = false;
		}finally{
			commit(txStatus, r);
		}
		if(r && (isAddPeopleCnt || newGroup)){
			weixinGroupService.addGroupPic(openGId, baseUrl);
		}
		//成功
		if(r){
			if(existTeacherIdentity){
				return new ApiMessage(ReqCode.UserGroupAdd, ReqState.ExistTeacherIdentity);
			}
			//传入名称称，但已存在名称
			if(existGroupName){
				return new ApiMessage(ReqCode.UserGroupAdd, ReqState.ExistGroupName);
			}
			return new ApiMessage(ReqCode.UserGroupAdd, ReqState.Success);
		}
		return new ApiMessage(ReqCode.UserGroupAdd, ReqState.Failure);
	}
	
	@Override
	public ApiMessage batchAddUserGroup(String baseUrl, String openId, String identity, String[] openGIds, int childId){
		if(StringUtil.isEmpty(openId) || openGIds == null 
				|| openGIds.length <= 0){
			return new ApiMessage(ReqCode.BatchAddGroup, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.BatchAddGroup, ReqState.NoExistUser);
		}
		boolean director = "director".equals(identity);
		boolean teacher = "teacher".equals(identity);
		boolean patriarch = "patriarch".equals(identity);
		if(!director && !teacher && !patriarch){
			return new ApiMessage(ReqCode.BatchAddGroup, ReqState.ApiParamError);
		}
		Children child = null;
		if(patriarch){
			child = childrenDao.getChildren(childId);
			if(child == null){
				return new ApiMessage(ReqCode.BatchAddGroup, ReqState.NoExistChild);
			}
			if(child.getWuId() != wu.getWuId()){
				return new ApiMessage(ReqCode.BatchAddGroup, ReqState.NoPower);
			}
		}
		List<String> openGIdList = new ArrayList<String>();
		List<Integer> groupIdList = new ArrayList<Integer>();
		List<UserGroup> newUgList = new ArrayList<UserGroup>();
		List<UserGroup> oldUgList = new ArrayList<UserGroup>();
		boolean existTeacherIdentity = false;
		boolean existPowerRepeat = false;
		for(int i=openGIds.length-1;i>=0;i--){
			if(openGIdList.contains(openGIds[i])){
				continue;
			}
			//获得群信息
			WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGIds[i]);
			if(wg == null){
				return new ApiMessage(ReqCode.BatchAddGroup, ReqState.NoExistGroup);
			}
			if(director && wg.getDirectorId() != 0){
				return new ApiMessage(ReqCode.BatchAddGroup, ReqState.GroupExistDirector);
			}
			UserGroup tmpUg = userGroupDao.getUserGroup(wu.getWuId(), wg.getGroupId());
			//判断是否存在关系
			if(tmpUg != null){
				//注册的是家长身份，并存在家长身份，则新添加一条关系
				if(tmpUg.isPatriarch() && patriarch){
					//存在改孩子关系，不操作
					if(userGroupDao.existUserGroup(wu.getWuId(), wg.getGroupId(), childId)){
						continue;
					}
					tmpUg.setChildId(childId);
					tmpUg.setAliasName(child.getChildName() + "的" + child.getRelation());
					newUgList.add(tmpUg);
					//如果不存在孩子关系
				}else{
					//班群关系不存在班主任关系，注册的是班主任身份，并存在班主任身份
					if(!tmpUg.isDirector() && wu.isDirector() && director){
						tmpUg.setDirector(director);
						if(tmpUg.isTeacher()){
							existTeacherIdentity = true;
							tmpUg.setTeacher(false);
						}
					}
					//班群关系不存在老师关系，注册的是老师身份，并存在老师身份
					if(!tmpUg.isTeacher() && wu.isTeacher() && teacher){
						tmpUg.setTeacher(teacher);
					}
					//添加老师身份当时群内已有班主任身份
					if(teacher && tmpUg.isDirector()){
						existPowerRepeat = true;
						break;
					}
					//班群关系不存在家长关系，注册的是家长身份，并存在家长身份
					if(!tmpUg.isPatriarch() && wu.isPatriarch() && patriarch){
						tmpUg.setChildId(childId);
						tmpUg.setPatriarch(patriarch);
						tmpUg.setAliasName(child.getChildName() + "的" + child.getRelation());
					}
					oldUgList.add(tmpUg);
				}
			//不存在群关系
			}else{
				tmpUg = new UserGroup();
				tmpUg.setWuId(wu.getWuId());
				tmpUg.setGroupId(wg.getGroupId());
				tmpUg.setPatriarch(patriarch);
				tmpUg.setTeacher(teacher);
				tmpUg.setDirector(director);
				tmpUg.setPhone(wu.getPhone());
				if(patriarch){
					tmpUg.setChildId(childId);
					tmpUg.setAliasName(child.getChildName() + "的" + child.getRelation());
				}
				newUgList.add(tmpUg);
				//用户不存在和群的关系
				openGIdList.add(openGIds[i]);
			}
			groupIdList.add(wg.getGroupId());
		}
		if(existPowerRepeat && oldUgList.size() <= 0 && newUgList.size() <= 0){
			return new ApiMessage(ReqCode.BatchAddGroup, ReqState.PowerRepeat);
		}
		TransactionStatus txStatus = getTxStatus();
		boolean r = false;
		try {
			//批量修改微信群用户数量
			//weixinGroupDao.updatePeopleCnt(openGIdList, 1)
			//批量添加用户群关系
			r = userGroupDao.batchAddUserGroup(newUgList);
			if(r){
				for (UserGroup userGroup : oldUgList) {
					r = userGroupDao.updateIdentity(userGroup, director, teacher, patriarch);
					if(!r){
						break;
					}
				}
			}
			if(r && director){
				for(Integer groupId : groupIdList){
					r = weixinGroupDao.updateDirectorId(groupId, wu.getWuId());
					if(!r){
						break;
					}
				}
			}
		} catch (Exception e) {
			r = false;
		}finally{
			commit(txStatus, r);
		}
		if(r && openGIdList.size() > 0){
			for (String openGId : openGIdList) {
				weixinGroupService.addGroupPic(openGId, baseUrl);
			}
		}
		if(r){
			if(existTeacherIdentity){
				return new ApiMessage(ReqCode.BatchAddGroup, ReqState.ExistTeacherIdentity);
			}
			if (existPowerRepeat){
				return new ApiMessage(ReqCode.BatchAddGroup, ReqState.PowerRepeat);
			}
			return new ApiMessage(ReqCode.BatchAddGroup, ReqState.Success);
		}else{
			return new ApiMessage(ReqCode.BatchAddGroup, ReqState.Failure);
		}
	}

	@Override
	public ApiMessage unbindUserGroup(String openId, String openGId,
			String identity, int childId) {
		//判断参数信息是否合法
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(openGId) 
				|| StringUtil.isEmpty(identity) || ("patriarch".equals(identity) && childId <= 0)){
			return new ApiMessage(ReqCode.UserGroupUnbind, ReqState.ApiParamError);
		}
		boolean director = "director".equals(identity);
		boolean teacher = "teacher".equals(identity);
		boolean patriarch = "patriarch".equals(identity);
		if(!director && !teacher && !patriarch){
			return new ApiMessage(ReqCode.UserGroupUnbind, ReqState.ApiParamError);
		}
		//用户信息
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.UserGroupUnbind, ReqState.NoExistUser);
		}
		//获得群信息
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return new ApiMessage(ReqCode.UserGroupUnbind, ReqState.NoExistGroup);
		}
		//获得用户群关系
		UserGroup ug = userGroupDao.getUserGroup(wu.getWuId(), wg.getGroupId());
		if(ug == null){
			return new ApiMessage(ReqCode.UserGroupUnbind, ReqState.UserNotInGroup);
		}
		//解绑班主任身份但不存在班主任身份
		if(director && !ug.isDirector()){
			return new ApiMessage(ReqCode.UserGroupUnbind, ReqState.NoPower);
		//解绑老师身份但不存在班主任身份
		}else if(teacher && !ug.isTeacher()){
			return new ApiMessage(ReqCode.UserGroupUnbind, ReqState.NoPower);
		//解绑家长身份
		}else if(patriarch){
			//用户不存在家长身份
			if(!ug.isPatriarch()){
				return new ApiMessage(ReqCode.UserGroupUnbind, ReqState.NoPower);
			}
			//群内不存在该孩子
			if(!userGroupDao.existUserGroup(wu.getWuId(), wg.getGroupId(), childId)){
				return new ApiMessage(ReqCode.UserGroupUnbind, ReqState.UserNotInGroup);
			}
		}
		//计算用户群中身份数量
		int identityCnt = 0;
		identityCnt = ug.isDirector() ? ++identityCnt : identityCnt;
		identityCnt = ug.isTeacher() ? ++identityCnt : identityCnt;
		identityCnt = ug.isPatriarch() ? ++identityCnt : identityCnt;
		//获得用户在群内存在的关系个数
		int ugCnt = userGroupDao.getUserGroupCnt(wu.getWuId(), wg.getGroupId());
		// 解绑类型  1、班主任  2、老师   3、家长
		int type = director ? 1 : teacher ? 2 : 3;
		
		//退群记录---主动退群
		AbortGroup ag = new AbortGroup();
		ag.setGroupId(wg.getGroupId());
		ag.setUgId(ug.getUgId());
		ag.setWuId(wu.getWuId());
		ag.setIdentities(identity);
		ag.setAbortType(1);
		ag.setOperatorId(wu.getWuId());
		TransactionStatus txStatus = getTxStatus();
		boolean r = true;
		try {
			//修改群内班主任Id
			if(director){
				r = weixinGroupDao.unbindDirector(wg.getGroupId(), wu.getWuId());
			}
			if(r && identityCnt == 1){
				r = weixinGroupDao.updatePeopleCnt(openGId, 2, 1);
			}
			//如果解绑的是家长并且存在多条关系记录，则删除当前用户关系
			if(r && (patriarch && ugCnt > 1 || identityCnt==1)){
				r = userGroupDao.deleteUserGroup(wu.getWuId(), wg.getGroupId(), childId);
			//如果解绑的不是家长或者记录只有一条
			}else if(r){
				r = userGroupDao.unbindUserGroup(wu.getWuId(), wg.getGroupId(), type, childId);
			}
			r = r && abortGroupDao.addAbortGroup(ag);
		} catch (Exception e) {
			r = false;
		}finally{
			commit(txStatus, r);
		}
		ReqState reqState = r ? ReqState.Success : ReqState.Failure;
		return new ApiMessage(ReqCode.UserGroupUnbind, reqState);
	}

	@Override
	public UserGroup getUserGroup(int ugId) {
		return userGroupDao.getUserGroup(ugId);
	}

	@Override
	public ApiMessage getSubjects(String openId, String openGId) {
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(openGId)){
			return new ApiMessage(ReqCode.Subjects, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.Subjects, ReqState.NoExistUser);
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return new ApiMessage(ReqCode.Subjects, ReqState.NoExistGroup);
		}
		UserGroup ug = userGroupDao.getUserGroup(wu.getWuId(), wg.getGroupId());
		if(ug == null){
			return new ApiMessage(ReqCode.Subjects, ReqState.UserNotInGroup);
		}
		String[] subjects = new String[]{};
		if(StringUtil.isNotEmpty(ug.getSubjects())){
			subjects = ug.getSubjects().split("/");
		}
		return new ApiMessage(ReqCode.Subjects, ReqState.Success).setInfo(subjects);
	}

	@Override
	public ApiMessage updateSubjects(String openId, String openGId,
			String[] subjects) {
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(openGId) 
				|| subjects == null || subjects.length <= 0){
			return new ApiMessage(ReqCode.UpdateSubjects, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.UpdateSubjects, ReqState.NoExistUser);
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return new ApiMessage(ReqCode.UpdateSubjects, ReqState.NoExistGroup);
		}
		if(!userGroupDao.userInGroup(wu.getWuId(), wg.getGroupId())){
			return new ApiMessage(ReqCode.UpdateSubjects, ReqState.UserNotInGroup);
		}
		String sub = "";
		for (int i = 0; i < subjects.length; i++) {
			sub += subjects[i];
			if(i < subjects.length-1){
				sub += "/";
			}
		}
		boolean r = userGroupDao.updateSubject(wu.getWuId(), wg.getGroupId(), sub);
		ReqState reqState = r ? ReqState.Success : ReqState.Failure;
		return new ApiMessage(ReqCode.UpdateSubjects, reqState);
	}

	@Override
	public ApiMessage updatePhone(String openId, String openGId, String phone,
			String pOpenId) {
		if(StringUtil.isEmpty(phone) || StringUtil.isEmpty(openId) 
				|| StringUtil.isEmpty(openGId) || StringUtil.isEmpty(pOpenId)){
			return new ApiMessage(ReqCode.UpdateUserPhone, ReqState.ApiParamError);
		}
		if(!phone.matches(CoreVars.RULES_PHONE) && !phone.matches(CoreVars.RULES_MOBILE)){
			return new ApiMessage(ReqCode.UpdateUserPhone, ReqState.WrongPhone);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.UpdateUserPhone, ReqState.NoExistUser);
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return new ApiMessage(ReqCode.UpdateUserPhone, ReqState.NoExistGroup);
		}
		//当前用户不是群班主任
		if(wg.getDirectorId() != wu.getWuId()){
			return new ApiMessage(ReqCode.UpdateUserPhone, ReqState.NoPower);
		}
		WeixinUser pWu = weixinUserDao.getUser(pOpenId);
		if(pWu == null){
			return new ApiMessage(ReqCode.UpdateUserPhone, ReqState.NoExistUser);
		}
		//判断被修改者是否在群内
		if(!userGroupDao.existUserGroup(pWu.getWuId(), wg.getGroupId(), 0)){
			return new ApiMessage(ReqCode.UpdateUserPhone, ReqState.UserNotInGroup);
		}
		UserGroup ug = new UserGroup();
		ug.setWuId(pWu.getWuId());
		ug.setGroupId(wg.getGroupId());
		ug.setPhone(phone);
		boolean r = userGroupDao.updateUser(ug);
		ReqState reqState = r ? ReqState.Success : ReqState.Failure;
		return new ApiMessage(ReqCode.UpdateUserPhone, reqState);
	}

	@Override
	public ApiMessage deleteUg(String openId, String openGId, String[] teacher,
			String[] patriarch) {
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(openGId) 
				|| ((teacher == null || teacher.length<=0) 
						&& (patriarch == null || patriarch.length<=0))){
			return new ApiMessage(ReqCode.DeleteUser, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.DeleteUser, ReqState.NoExistUser);
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return new ApiMessage(ReqCode.DeleteUser, ReqState.NoExistGroup);
		}
		List<Integer> delTeacher = new ArrayList<Integer>(); // 要删除老师的
		List<Integer> upTeacher = new ArrayList<Integer>(); // 要修改的老师
		List<Integer> delPatriarch = new ArrayList<Integer>(); // 要删除的家长
		List<Integer> upPatriarch = new ArrayList<Integer>(); // 要修改的家长身份
		List<Integer> ugIdList = new ArrayList<Integer>(); // 要删除的关系
		Map<Integer, AbortGroup> agMap = new HashMap<Integer, AbortGroup>(); // 退群记录
		Map<Integer, GroupMessage> gmMap = new HashMap<Integer, GroupMessage>(); // 退群消息
		//有传入老师的
		if(teacher != null && teacher.length > 0){
			List<UserGroup> ugList = userGroupDao.getUserGroupList(wg.getGroupId(), 1, teacher);
			if(ugList == null){
				return new ApiMessage(ReqCode.DeleteUser, ReqState.ExistNoPowerItem);
			}
			//总用户个数
			int wuCount = 0;
			for (int i = 0; i < ugList.size(); i++) {
				UserGroup ug = ugList.get(i);
				AbortGroup ag = new AbortGroup();//添加退群记录
				ag.setGroupId(wg.getGroupId());
				ag.setUgId(ug.getUgId());
				ag.setWuId(ug.getWuId());
				ag.setOperatorId(wu.getWuId());
				ag.setAbortType(2);
				ag.setIdentities("teacher");
				agMap.put(ug.getUgId(), ag);
				if(i == ugList.size() - 1 || 
						(i < ugList.size()-1 && ug.getWuId() != ugList.get(i+1).getWuId())){
					
					GroupMessage gm = new GroupMessage(); // 添加退群消息
					gm.setGroupId(0);
					gm.setWuId(ug.getWuId());
					gm.setMsgType(1);
					gm.setTitle("您已被移除" + wg.getGroupName() + "家长群助手");
					gm.setContent("您已被移除" + wg.getGroupName() + "家长群助手");
					gmMap.put(ug.getWuId(), gm);
					//存在除了老师之外的其他身份，则修改老师身份
					if(ug.isDirector() || ug.isPatriarch()){
						upTeacher.add(ug.getWuId());
					}else{
						delTeacher.add(ug.getWuId());
					}
					wuCount++;
				}
			}
			if(wuCount != teacher.length){
				return new ApiMessage(ReqCode.DeleteUser, ReqState.ExistNoPowerItem);
			}
		}
		if(patriarch != null && patriarch.length > 0){
			List<UserGroup> ugList = userGroupDao.getUserGroupList(wg.getGroupId(), 2, patriarch);
			if(ugList == null){
				return new ApiMessage(ReqCode.DeleteUser, ReqState.ExistNoPowerItem);
			}
			//总用户个数
			int wuCount = 0;
			//用户下的身份个数
			int count = 0;
			List<Integer> list = new ArrayList<Integer>();
			for (int i = 0; i < ugList.size(); i++) {
				UserGroup ug = ugList.get(i);
				count++;
				//获得agMap中的推群记录
				AbortGroup ag = agMap.get(ug.getUgId());
				if(ag == null){
					ag = new AbortGroup();//添加退群记录
					ag.setGroupId(wg.getGroupId());
					ag.setUgId(ug.getUgId());
					ag.setWuId(ug.getWuId());
					ag.setOperatorId(wu.getWuId());
					ag.setAbortType(2);
					ag.setIdentities("patriarch");
				}else{
					ag.setIdentities("teacher/patriarch");
				}
				agMap.put(ug.getUgId(), ag);
				if(i == ugList.size() - 1 || 
						(i < ugList.size()-1 && ug.getWuId() != ugList.get(i+1).getWuId())){
					GroupMessage gm = gmMap.get(ug.getWuId());
					if(gm == null){
						gm = new GroupMessage(); // 添加退群消息
						gm.setGroupId(0);
						gm.setWuId(ug.getWuId());
						gm.setMsgType(1);
						gm.setTitle("您已被移除" + wg.getGroupName() + "家长群助手");
						gm.setContent("您已被移除" + wg.getGroupName() + "家长群助手");
						gmMap.put(ug.getWuId(), gm);
					}
					wuCount++;
					//如果老师的修改集合中存在，并且没有班主任身份，则删除
					if(upTeacher.contains(ug.getWuId()) && !ug.isDirector()){
						upTeacher.remove(new Integer(ug.getWuId()));
						delTeacher.add(ug.getWuId());
					}else{
						//如果存在其他身份，
						if(ug.isDirector() || ug.isTeacher()){
							//存在多个身份
							if(count > 1){
								ugIdList.addAll(list);
							}
							upPatriarch.add(ug.getWuId());
							
						//不存在其他身份，则直接删除
						}else{
							delPatriarch.add(ug.getWuId());
						}
					}
					count = 0;
					list = new ArrayList<Integer>();
					continue;
				}
				list.add(ug.getUgId());
			}
			if(wuCount != patriarch.length){
				return new ApiMessage(ReqCode.DeleteUser, ReqState.ExistNoPowerItem);
			}
		}
		TransactionStatus txStatus = getTxStatus();
		boolean r = true;
		try {
			if(teacher != null && teacher.length > 0){
				//删除只有一个身份的用户
				r = userGroupDao.deleteUserGroup(wg.getGroupId(), delTeacher)
						//修改拥有多个身份的用户的老师身份
						&& userGroupDao.updateUserGroup(wg.getGroupId(), upTeacher, 1);
			}
			if(r && patriarch != null && patriarch.length > 0){
				//删除只有一个身份的用户
				r = userGroupDao.deleteUserGroup(wg.getGroupId(), delPatriarch)
						//删除多余的项
						&& userGroupDao.deleteUserGroup(wg.getGroupId(), ugIdList.toArray())
						//修改拥有多个身份的用户的家长身份
						&& userGroupDao.updateUserGroup(wg.getGroupId(), upPatriarch, 2);
			}
			//减少群人数
			r = r && weixinGroupDao.updatePeopleCnt(openGId, 2, delTeacher.size() + delPatriarch.size());
			//退群记录
			r = r && abortGroupDao.addAbortGroup(new ArrayList<AbortGroup>(agMap.values()));
			//退群消息
			r = r && groupMessageDao.addGroupMessage(new ArrayList<GroupMessage>(gmMap.values()));
		} catch (Exception e) {
			r = false;
		}finally{
			commit(txStatus, r);
		}
		ReqState reqState = r ? ReqState.Success : ReqState.Failure;
		return new ApiMessage(ReqCode.DeleteUser, reqState);
	}

	@Override
	public ApiMessage addPerfect(String openId, String openGId) {
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(openGId)){
			return new ApiMessage(ReqCode.CreatePerfect, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.CreatePerfect, ReqState.NoExistUser);
		}
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		if(wg == null){
			return new ApiMessage(ReqCode.CreatePerfect, ReqState.NoExistGroup);
		}
		//用户不是班群中的班主任
		if(wu.getWuId() != wg.getDirectorId()){
			return new ApiMessage(ReqCode.CreatePerfect, ReqState.NoPower);
		}
		List<UserGroup> ugList = userGroupDao.getUserGroupList(wg.getGroupId());
		if(ugList == null || ugList.size() <= 0){
			return new ApiMessage(ReqCode.CreatePerfect, ReqState.Success);
		}
		List<GroupMessage> gmList = new ArrayList<GroupMessage>();
		for (int i = 0; i < ugList.size(); i++) {
			UserGroup ug = ugList.get(i);
			if(StringUtil.isEmpty(ug.getPhone())){
				GroupMessage gm = new GroupMessage();
				gm.setGroupId(wg.getGroupId());
				gm.setWuId(ug.getWuId());
				gm.setMsgType(8);
				gm.setTitle("完善通知");
				gm.setContent(wu.getRealName()+"发起完善通知");
				gmList.add(gm);
			}
		}
		boolean r = groupMessageDao.addGroupMessage(gmList);
		ReqState reqState = r ? ReqState.Success : ReqState.Failure;
		return new ApiMessage(ReqCode.CreatePerfect, reqState);
	}

	@Override
	public ApiMessage getUserGroup(String openId, String identity, int childId) {
		if(StringUtil.isEmpty(openId)){
			return new ApiMessage(ReqCode.UserGroup, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.UserGroup, ReqState.NoExistUser);
		}
		int type = "director".equals(identity) ? 1 : 
					"teacher".equals(identity) ? 2 : 
					"patriarch".equals(identity) ? 3 : 4;
		if(type == 4){
			return new ApiMessage(ReqCode.UserGroup, ReqState.ApiParamError);
		}
		List<Map<String, Object>> ugList = weixinGroupDao.getUserGroupList(wu.getWuId(), type, childId);
		return new ApiMessage(ReqCode.UserGroup, ReqState.Success).setInfo(ugList);
	}

	@Override
	public void newUse(String openId, String openGId) {
		TransactionStatus txStatus = getTxStatus();
		boolean r = false;
		try{
			r = userGroupDao.updateNewUse(openId) && 
					userGroupDao.updateNewUse(openId, openGId);
		}catch(Exception e){
			r = false;
		}finally{
			commit(txStatus, r);
		}
	}
	
}
