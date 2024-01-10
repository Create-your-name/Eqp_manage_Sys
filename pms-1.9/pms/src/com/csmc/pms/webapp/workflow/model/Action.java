package com.csmc.pms.webapp.workflow.model;

import java.util.List;

public class Action {
	//����index
	private long actionIndex;
	//��������Normal,Dcop��
	private String actionName;
	//��������
	private String actionDescription;
	//�������ͣ�Normal,Dcop��
	private String actionType;
	//�����ڵ�����(start,action,end)
	private String nodeType;
	//����
	private int frozen;
	//ʹ��
	private int enabled;
	//��Ϊjob�е����к�
	private int actionId;
	//������Ŀ�б�
	private List itemlist;
	//״���б�
	private List statusList;
	//��ע
	private String actionNote;
	// ���ʱ��
	private String finishData;
	//�����еĲ���Index
	private long actionRecordIndex;
	
	private int empty;
	
	public String getActionDescription() {
		return actionDescription;
	}
	public void setActionDescription(String actionDescription) {
		this.actionDescription = actionDescription;
	}
	public long getActionIndex() {
		return actionIndex;
	}
	public void setActionIndex(long actionIndex) {
		this.actionIndex = actionIndex;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public int getEnabled() {
		return enabled;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	public int getFrozen() {
		return frozen;
	}
	public void setFrozen(int frozen) {
		this.frozen = frozen;
	}
	public int getActionId() {
		return actionId;
	}
	public void setActionId(int actionId) {
		this.actionId = actionId;
	}
	public List getItemlist() {
		return itemlist;
	}
	public void setItemlist(List itemlist) {
		this.itemlist = itemlist;
	}
	public List getStatusList() {
		return statusList;
	}
	public void setStatusList(List statusList) {
		this.statusList = statusList;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public String getActionNote() {
		return actionNote;
	}
	public void setActionNote(String actionNote) {
		this.actionNote = actionNote;
	}
	public String getFinishData() {
		return finishData;
	}
	public void setFinishData(String finishData) {
		this.finishData = finishData;
	}
	public int getEmpty() {
		return empty;
	}
	public void setEmpty(int empty) {
		this.empty = empty;
	}
	public long getActionRecordIndex() {
		return actionRecordIndex;
	}
	public void setActionRecordIndex(long actionRecordIndex) {
		this.actionRecordIndex = actionRecordIndex;
	}
	
}
