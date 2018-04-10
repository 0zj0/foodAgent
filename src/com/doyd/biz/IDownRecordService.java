package com.doyd.biz;

import java.util.List;

import com.doyd.model.DownRecord;

public interface IDownRecordService {
	
	public boolean batchAddRecord(List<DownRecord> records);
	
	public boolean addRecord(DownRecord record);
	
}
