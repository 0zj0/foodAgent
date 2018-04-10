package com.doyd.model;

import java.io.Serializable;

public class TransferGroup implements Serializable {
	private static final long serialVersionUID = 1L;
	private int transferId; // 主键
	private int groupId; // 群Id
	private int transferWuId; // 转让人Id
	private int acceptWuId; // 接收人
	private long transferTime; // 转让时间
	private long acceptTime; // 接受转让时间
	private int transferState; // 转让状态1：转让中，2：转让成功，3：转让失败

	public int getTransferId() {
		return transferId;
	}
	public void setTransferId(int transferId) {
		this.transferId = transferId;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getTransferWuId() {
		return transferWuId;
	}
	public void setTransferWuId(int transferWuId) {
		this.transferWuId = transferWuId;
	}
	public int getAcceptWuId() {
		return acceptWuId;
	}
	public void setAcceptWuId(int acceptWuId) {
		this.acceptWuId = acceptWuId;
	}
	public long getTransferTime() {
		return transferTime;
	}
	public void setTransferTime(long transferTime) {
		this.transferTime = transferTime;
	}
	public long getAcceptTime() {
		return acceptTime;
	}
	public void setAcceptTime(long acceptTime) {
		this.acceptTime = acceptTime;
	}
	public int getTransferState() {
		return transferState;
	}
	public void setTransferState(int transferState) {
		this.transferState = transferState;
	}
}
