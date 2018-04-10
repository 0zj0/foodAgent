package com.doyd.biz.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doyd.biz.IDownRecordService;
import com.doyd.dao.IDownRecordDao;
import com.doyd.model.DownRecord;

@Service
public class DownRecordService implements IDownRecordService {
	@Autowired
	private IDownRecordDao downRecordDao;

	@Override
	public boolean batchAddRecord(List<DownRecord> records) {
		return downRecordDao.batchAddRecord(records);
	}

	@Override
	public boolean addRecord(DownRecord record) {
		return downRecordDao.addRecord(record);
	}
	
}
