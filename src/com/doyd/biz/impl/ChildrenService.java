package com.doyd.biz.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.doyd.biz.IChildrenService;
import com.doyd.biz.IWeixinGroupService;
import com.doyd.core.biz.impl.MyTransSupport;
import com.doyd.dao.IChildrenDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.dao.IWeixinUserDao;
import com.doyd.model.Children;
import com.doyd.model.UserGroup;
import com.doyd.model.WeixinGroup;
import com.doyd.model.WeixinUser;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;
import org.doyd.utils.StringUtil;

@Service
public class ChildrenService extends MyTransSupport implements IChildrenService {
	@Autowired
	private IChildrenDao childrenDao;
	@Autowired
	private IWeixinUserDao weixinUserDao;
	@Autowired
	private IWeixinGroupDao weixinGroupDao;
	@Autowired
	private IUserGroupDao userGroupDao;
	@Autowired
	private IWeixinGroupService weixinGroupService;

	@Override
	public ApiMessage addChild(String baseUrl, String openId, String openGId, String groupName, String childName,
			String relation, int education, int grade) {
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(openGId)
				|| StringUtil.isEmpty(childName) || StringUtil.isEmpty(relation)
				|| education <= 0 || grade <= 0){
			return new ApiMessage(ReqCode.CreateChild, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.CreateChild, ReqState.NoExistUser);
		}
		//判断是否存在相同名称的孩子
		if(childrenDao.existChildren(wu.getWuId(), childName)){
			return new ApiMessage(ReqCode.CreateChild, ReqState.ExistChildren);
		}
		boolean existGroupName = false;
		//获得微信群信息
		WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGId);
		//如果（群不存在或者群名称为空）但传入群名称为空
		if((wg == null || StringUtil.isEmpty(wg.getGroupName())) && StringUtil.isEmpty(groupName)){
			return new ApiMessage(ReqCode.CreateUserIdentify, ReqState.ApiParamError);
		}
		if(wg != null && StringUtil.isNotEmpty(wg.getGroupName()) && StringUtil.isNotEmpty(groupName)){
			existGroupName = true;
		}
		if(wg == null){
			wg = new WeixinGroup();
			wg.setOpenGId(openGId);
			wg.setGroupName(groupName);
		}
		//创建孩子
		Children children = new Children();
		children.setWuId(wu.getWuId());
		children.setChildName(childName);
		children.setEducation(education);
		children.setRelation(relation);
		children.setGrade(grade);
		UserGroup ug = null;
		boolean isAddPeopleCnt = false;
		//判断群是否存在，存在获得用户群关系
		if(wg.getGroupId() > 0){
			ug = userGroupDao.getUserGroup(wu.getWuId(), wg.getGroupId());
		}
		boolean newGroup = wg.getGroupId() <= 0;
		if(ug == null){
			isAddPeopleCnt = true;
		}
		//如果群不存在或者用户群关系不存在或者存在家长身份，则创建
		if(ug == null || ug.getChildId() > 0){
			UserGroup tmpUg = ug;
			ug = new UserGroup();
			ug.setWuId(wu.getWuId());
			if(tmpUg != null && tmpUg.getChildId() > 0){
				ug.setDirector(tmpUg.isDirector());
				ug.setTeacher(tmpUg.isTeacher());
				ug.setSubjects(tmpUg.getSubjects());
			}
			ug.setPhone(wu.getPhone());
		}
		ug.setPatriarch(true);
		ug.setAliasName(childName + "的" + relation);
		TransactionStatus txStatus = getTxStatus();
		boolean r = false;
		try {
			r = childrenDao.addChildren(children);
			//微信群不存在
			if(r && wg.getGroupId() <= 0){
				r = weixinGroupDao.addWeixinGroup(wg);
			}else if(r){
				//群名称为空
				if(StringUtil.isEmpty(wg.getGroupName())){
					r = r && weixinGroupDao.updateGroupName(wg.getGroupId(), groupName);
				}
			}
			if(r && !newGroup && isAddPeopleCnt){
				r = weixinGroupDao.updatePeopleCnt(openGId, 1, 1);
			}
			if(r){
				ug.setChildId(children.getChildId());
				ug.setGroupId(wg.getGroupId());
				//存在用户群关系
				if(ug.getUgId() <= 0){
					r = userGroupDao.addUserGroup(ug);
				}else{
					r = userGroupDao.update(ug);
				}
			}
		} catch (Exception e) {
			r = false;
		}finally{
			commit(txStatus, r);
		}
		if(r && (isAddPeopleCnt || newGroup)){
			weixinGroupService.addGroupPic(openGId, baseUrl);
		}
		if(r && existGroupName){
			return new ApiMessage(ReqCode.CreateChild, ReqState.ExistGroupName);
		}
		ReqState reqState = r ? ReqState.Success : ReqState.Failure;
		return new ApiMessage(ReqCode.CreateChild, reqState);
	}
	
	@Override
	public ApiMessage addChild(String baseUrl, String openId, String childName, String relation
			, int education, int grade, String[] openGIds){
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(childName) 
				|| StringUtil.isEmpty(relation) || education <= 0 
				|| grade <= 0 || openGIds == null || openGIds.length <= 0){
			return new ApiMessage(ReqCode.CreateChild, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.CreateChild, ReqState.NoExistUser);
		}
		//判断是否存在相同名称的孩子
		if(childrenDao.existChildren(wu.getWuId(), childName)){
			return new ApiMessage(ReqCode.CreateChild, ReqState.ExistChildren);
		}
		//创建孩子
		Children children = new Children();
		children.setWuId(wu.getWuId());
		children.setChildName(childName);
		children.setEducation(education);
		children.setRelation(relation);
		children.setGrade(grade);
		
		List<String> openGIdList = new ArrayList<String>();//暂存openGId
		List<UserGroup> newUgList = new ArrayList<UserGroup>();//新的群关系
		List<UserGroup> oldUgList = new ArrayList<UserGroup>();//旧的群关系
		for (int i = 0; i < openGIds.length; i++) {
			if(openGIdList.contains(openGIds[i])){
				continue;
			}
			WeixinGroup wg = weixinGroupDao.getWeixinGroup(openGIds[i]);
			if(wg == null){
				return new ApiMessage(ReqCode.CreateChild, ReqState.NoExistGroup);
			}
			//获得群关系
			UserGroup tmpUg = userGroupDao.getUserGroup(wu.getWuId(), wg.getGroupId());
			//不存在群关系
			if(tmpUg == null){
				tmpUg = new UserGroup();
				tmpUg.setWuId(wu.getWuId());
				tmpUg.setGroupId(wg.getGroupId());
				tmpUg.setPatriarch(true);
				tmpUg.setAliasName(childName + "的" + relation);
				tmpUg.setPhone(wu.getPhone());
				newUgList.add(tmpUg);
				//用户不存在和群的关系
				openGIdList.add(openGIds[i]);
			//存在群关系
			}else{
				if(tmpUg.isPatriarch()){
					tmpUg.setAliasName(childName + "的" + relation);
					newUgList.add(tmpUg);
				}else{
					tmpUg.setAliasName(childName + "的" + relation);
					oldUgList.add(tmpUg);
				}
			}
		}
		TransactionStatus txStatus = getTxStatus();
		boolean r = false;
		try {
			r = childrenDao.addChildren(children);
			if(r){
				for (UserGroup userGroup : oldUgList) {
					userGroup.setChildId(children.getChildId());
				}
				for (UserGroup userGroup : newUgList) {
					userGroup.setChildId(children.getChildId());
				}
				//批量修改微信群用户数量
				r = weixinGroupDao.updatePeopleCnt(openGIdList, 1)
						//批量添加用户群关系
						&& userGroupDao.batchAddUserGroup(newUgList)
						//批量修改用户关系
						&& userGroupDao.batchUpdateUserGroup(oldUgList);
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
		ReqState reqState = r ? ReqState.Success : ReqState.Failure;
		return new ApiMessage(ReqCode.BatchAddGroup, reqState);
	}

	@Override
	public ApiMessage updateChild(String openId, int childId, String childName, String relation) {
		if(StringUtil.isEmpty(openId) || StringUtil.isEmpty(childName) 
				|| childId <= 0 || StringUtil.isEmpty(relation)){
			return new ApiMessage(ReqCode.CreateChild, ReqState.ApiParamError);
		}
		WeixinUser wu = weixinUserDao.getUser(openId);
		if(wu == null){
			return new ApiMessage(ReqCode.CreateChild, ReqState.NoExistUser);
		}
		Children children = childrenDao.getChildren(childId);
		if(children == null){
			return new ApiMessage(ReqCode.UpdateChild, ReqState.NoExistChild);
		}
		if(children.getWuId() != wu.getWuId()){
			return new ApiMessage(ReqCode.BatchAddGroup, ReqState.NoPower);
		}
		boolean r = childrenDao.updateChildName(childId, childName, relation);
		ReqState reqState = r ? ReqState.Success : ReqState.Failure;
		return new ApiMessage(ReqCode.UpdateChild, reqState);
	}
	
}
