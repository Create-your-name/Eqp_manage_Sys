package com.csmc.pms.webapp.workflow.model;

public class ActionStatus {
	//状态索引
	private long statusIndex;
	//状态名
	private String statusName;
	//状态序列
	private int statusOrder;
	//下面执行的Action
	private int nextActionId;
	
	public long getStatusIndex() {
		return statusIndex;
	}
	public void setStatusIndex(long statusIndex) {
		this.statusIndex = statusIndex;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public int getStatusOrder() {
		return statusOrder;
	}
	public void setStatusOrder(int statusOrder) {
		this.statusOrder = statusOrder;
	}
	public int getNextActionId() {
		return nextActionId;
	}
	public void setNextActionId(int nextActionId) {
		this.nextActionId = nextActionId;
	}
}
