package com.csmc.pms.webapp.workflow.model;

public class ActionStatus {
	//״̬����
	private long statusIndex;
	//״̬��
	private String statusName;
	//״̬����
	private int statusOrder;
	//����ִ�е�Action
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
