package com.doyd.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.ITransferGroupDao;
import com.doyd.dao.rowmapper.TransferGroupRowmapper;
import com.doyd.model.TransferGroup;

@Repository
public class TransferGroupDao implements ITransferGroupDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private TransferGroupRowmapper mapper;
	
	@Override
	public TransferGroup getTransferGroup(int transferId) {
		String sql = "SELECT * FROM transfer_group WHERE transferId=?";
		return daoSupport.queryForObject(sql, new Object[]{transferId}, mapper);
	}
	
	@Override
	public TransferGroup getTransferGroup(String openId, String openGId){
		String sql = "SELECT t1.* FROM transfer_group t1 "
				+ "LEFT JOIN weixin_user t2 ON t1.acceptWuId=t2.wuId "
				+ "LEFT JOIN wx_user_oauth AS d ON t2.unionId=d.unionId "
				+ "LEFT JOIN weixin_group t3 ON t1.groupId=t3.groupId "
				+ "WHERE d.openId=? AND t3.openGId=? AND t1.transferState=1";
		return daoSupport.queryForObject(sql, new Object[]{openId, openGId}, mapper);
	}

	@Override
	public boolean addTransferGroup(TransferGroup tg) {
		String sql = "INSERT INTO transfer_group(groupId,transferWuId,acceptWuId,transferTime) " +
				"VALUES(?,?,?,UNIX_TIMESTAMP(NOW(3))*1000)";
		Object[] params = new Object[]{
				tg.getGroupId(), tg.getTransferWuId(), tg.getAcceptWuId()
		};
		tg.setTransferId(daoSupport.insert(sql, params));
		return tg.getTransferId()>0;
	}

}
