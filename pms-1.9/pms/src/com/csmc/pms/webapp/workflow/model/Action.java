package com.csmc.pms.webapp.workflow.model;

import java.util.List;

public class Action {
	//动作index
	private long actionIndex;
	//动作名（Normal,Dcop）
	private String actionName;
	//动作描述
	private String actionDescription;
	//动作类型（Normal,Dcop）
	private String actionType;
	//动作节点类型(start,action,end)
	private String nodeType;
	//冻结
	private int frozen;
	//使用
	private int enabled;
	//作为job中的序列号
	private int actionId;
	//动作项目列表
	private List itemlist;
	//状况列表
	private List statusList;
	//备注
	private String actionNote;
	// 完成时间
	private String finishData;
	//流程中的步骤Index
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
