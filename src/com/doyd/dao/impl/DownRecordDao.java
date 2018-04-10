package com.doyd.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.doyd.core.dao.impl.DaoSupport;
import com.doyd.dao.IDownRecordDao;
import com.doyd.dao.rowmapper.DownRecordRowmapper;
import com.doyd.model.DownRecord;

@Repository
public class DownRecordDao implements IDownRecordDao {
	@Autowired
	private DaoSupport daoSupport;
	@Autowired
	private DownRecordRowmapper mapper;
	@Override
	public boolean batchAddRecord(List<DownRecord> records) {
		if(records==null || records.size()<=0){
			return true;
		}
		StringBuilder sql = new StringBuilder("insert into down_record (fileId,wuId,ip," +
				"province,city,userAgent,downDate,downHour,state,remark,ctime) values");
		List<Object> params = new ArrayList<Object>();
		for (DownRecord downRecord : records) {
			sql.append("(?,?,?,?,?, ?,?,?,?,?, now()),");
			params.add(downRecord.getFileId());
			params.add(downRecord.getWuId());
			params.add(downRecord.getIp());
			params.add(downRecord.getProvince());
			params.add(downRecord.getCity());
			params.add(downRecord.getUserAgent());
			params.add(downRecord.getDownDate());
			params.add(downRecord.getDownHour());
			params.add(downRecord.getState());
			params.add(downRecord.getRemark());
		}
		sql.deleteCharAt(sql.length()-1);
		return daoSupport.update(sql.toString(), params.toArray()) >= 0;
	}
	@Override
	public boolean addRecord(DownRecord record) {
		String sql = "insert into down_record (fileId,wuId,ip," +
				"province,city,userAgent,downDate,downHour,state,remark,ctime) values" +
				" (?,?,?,?,?, ?,?,?,?,?, now())";
		record.setRecordId(daoSupport.insert(sql, record.getFileId(), record.getWuId(), 
				record.getIp(), record.getProvince(), record.getCity(), record.getUserAgent(),
				record.getDownDate(), record.getDownHour(), record.getState(), record.getRemark()));
		return record.getRecordId()>0;
	}

}
