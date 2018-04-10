package com.doyd.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IWxUserOauthDao;
import com.doyd.dao.rowmapper.WxUserOauthRowmapper;
import com.doyd.model.WxUserOauth;

@Repository
public class WxUserOauthDao implements IWxUserOauthDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private WxUserOauthRowmapper mapper;
	
	@Override
	public WxUserOauth getWxUserOauth(String appId, String openId) {
		String sql = "SELECT * FROM wx_user_oauth WHERE appId=? AND openId=? AND state=1";
		return daoSupport.queryForObject(sql, new Object[]{appId, openId}, mapper);
	}

	@Override
	public boolean addWxUserOauth(WxUserOauth wuo) {
		String sql = "INSERT INTO wx_user_oauth(unionId,appId,openId,ctime) VALUES(?,?,?,NOW())";
		wuo.setWuoId(daoSupport.insert(sql, wuo.getUnionId(), wuo.getAppId(), wuo.getOpenId()));
		return wuo.getWuoId() > 0;
	}

	@Override
	public boolean updateWxUserOauthForUnionId(int wuoId, String unionId) {
		String sql = "UPDATE wx_user_oauth SET unionId=? WHERE wuoId=?";
		return daoSupport.update(sql, unionId, wuoId) > 0;
	}

}
