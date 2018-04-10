package com.doyd.dao;

import java.util.List;

import com.doyd.model.Subject;

public interface ISubjectDao {
	
	/**
	 * 查询自定义学科
	 * @Title: getSubjectList
	 * @param wuId
	 * @return List<String>
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午6:16:15
	 */
	public List<String> getSubjectList(int wuId);
	
	/**
	 * 添加自定义学科
	 * @Title: addSubject
	 * @param subject
	 * @return boolean
	 * @author 创建人：王伟
	 * @date 创建时间：2017-12-26 下午6:16:30
	 */
	public boolean addSubject(Subject subject);
	/**
	 * 是否存在学科
	 * 
	 * @param wuId 用户Id
	 * @param subject 学科
	 * @return boolean
	 * @author 创建人：ylb
	 * @date 创建时间：2018-2-7 上午10:33:06
	 */
	public boolean existSubject(int wuId, String subject);
}
