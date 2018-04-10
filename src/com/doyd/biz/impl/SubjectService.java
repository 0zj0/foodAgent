package com.doyd.biz.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.doyd.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doyd.biz.ISubjectService;
import com.doyd.dao.ISubjectDao;
import com.doyd.dao.IUserGroupDao;
import com.doyd.dao.IWeixinGroupDao;
import com.doyd.model.Subject;
import com.doyd.model.UserGroup;
import com.doyd.model.WeixinGroup;
import com.doyd.msg.ApiMessage;
import com.doyd.msg.ReqCode;
import com.doyd.msg.ReqState;

@Service
public class SubjectService implements ISubjectService {
	@Autowired
	private ISubjectDao subjectDao;
	@Autowired
	private IWeixinGroupDao weixinGroupDao;
	@Autowired
	private IUserGroupDao userGroupDao;
	
	private static String subj = "语文，数学，英语，物理，化学，生物，政治，地理，历史，音乐，美术，体育";

	@Override
	public ApiMessage getSubjectList(String openGId, int wuId, ReqCode reqCode) {
		List<String> subjectList = new ArrayList<String>();
		WeixinGroup group = weixinGroupDao.getWeixinGroup(openGId);
		if(group==null){
			return new ApiMessage(reqCode, ReqState.NoExistGroup);
		}
		UserGroup userGroup = userGroupDao.getUserGroup(wuId, group.getGroupId());
		if(userGroup==null || (!userGroup.isDirector() && !userGroup.isTeacher())){
			return new ApiMessage(reqCode, ReqState.NoPower);
		}
		String subjects = userGroup.getSubjects();
		subjectList.addAll(subjectDao.getSubjectList(wuId));
		if(StringUtil.isNotEmpty(subjects)){
			List<String> asList = Arrays.asList(subjects.split("\\/"));
			for (String sub : asList) {
				if(!subj.contains(sub) && !subjectList.contains(sub)){
					subjectList.add(sub);
				}
			}
		}
		return new ApiMessage(reqCode, ReqState.Success).setInfo(subjectList);
	}

	@Override
	public ApiMessage addSubject(int wuId, String subject,
			ReqCode reqCode) {
		Subject subjectTmp = new Subject();
		subjectTmp.setWuId(wuId);
		subjectTmp.setSubject(subject);
		if(!subjectDao.addSubject(subjectTmp)){
			return new ApiMessage(reqCode, ReqState.Failure);
		}
		return new ApiMessage(reqCode, ReqState.Success);
	}

	
}
