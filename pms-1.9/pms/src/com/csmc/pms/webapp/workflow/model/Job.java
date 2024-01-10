package com.csmc.pms.webapp.workflow.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class Job implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -746069687200129823L;
	
	//流程Index
	private long jobIndex;
	//流程名
	private String jobName;
	//流程描述
	private String jobDescription;
	//流程内容xml
	private String jobContent;
	//流程申请状态（0-未使用,1-使用）
	private int status;
	//流程运行状态(0-START,1-OVER,2-HOLD)
	private int runStatus;
	//动作列表
	private List actionlist;
	//下一步动作号
	private int nextActionId;
	//事件对象
	private String eventObject;
	//事件类型
	private String eventType;
	//tempIndex
	private long tempIndex;
	//是否新增
	private boolean newFlag = false;
	//jobFlowText
	private String jobText;
	//表单Index
	private String formIndex;
	//操作人
	private String operator="";
	//结束时间
	private String finishData="";
	//异常code
	private String errorCode="";
	//异常事件
	private String errorEvent="";
	//异常原因
	private String errorReason="";
	//设备
	private String equipment;
	//表单名称
	private String recordName;
	// 表单创建人
	private String creator;
	// 表单名创建时间
	private String createTime;
	//异常原因(下拉列表)
	private List errorReasonList;
	// 流程图名称
	private String flowName;
	// 操作类型
	private String evt;
	// 操作人
	private String transBy;
	
	public List getErrorReasonList() {
		return errorReasonList;
	}

	public void setErrorReasonList(List errorReasonList) {
		this.errorReasonList = errorReasonList;
	}

	public List getActionlist() {
		return actionlist;
	}

	public void setActionlist(List actionlist) {
		this.actionlist = actionlist;
	}

	public String getJobContent() {
		return jobContent;
	}

	public void setJobContent(String jobContent) {
		this.jobContent = jobContent;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public long getJobIndex() {
		return jobIndex;
	}

	public void setJobIndex(long jobIndex) {
		this.jobIndex = jobIndex;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getRunStatus() {
		return runStatus;
	}

	public void setRunStatus(int runStatus) {
		this.runStatus = runStatus;
	}
	
	public void setRunStatus(Integer runStatus) {
		if (runStatus != null) {
			this.runStatus = runStatus.intValue();
		} else {
			this.runStatus = 0;
		}
	}

	public int getNextActionId() {
		//如果运行状态为Start，则返回开始
		if(runStatus == 0) {
			return 0;
		}
		return nextActionId;
	}

	public void setNextActionId(int nextActionId) {
		this.nextActionId = nextActionId;
	}
	
	public void setNextActionId(Integer nextActionId) {
		if (nextActionId != null) {
			this.nextActionId = nextActionId.intValue();
		} else {
			this.nextActionId = 0;
		}
	}
	
	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getEventObject() {
		return eventObject;
	}

	public void setEventObject(String eventObject) {
		this.eventObject = eventObject;
	}

	public boolean isNewFlag() {
		return newFlag;
	}

	public void setNewFlag(boolean newFlag) {
		this.newFlag = newFlag;
	}

	public long getTempIndex() {
		return tempIndex;
	}

	public void setTempIndex(long tempIndex) {
		this.tempIndex = tempIndex;
	}

	public String getJobText() {
		return jobText;
	}

	public void setJobText(String jobText) {
		this.jobText = jobText;
	}

	public String getFormIndex() {
		return formIndex;
	}

	public void setFormIndex(String formIndex) {
		this.formIndex = formIndex;
	}
	
	public String getFinishData() {
		return finishData;
	}

	public void setFinishData(String finishData) {
		this.finishData = finishData;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public String getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorEvent() {
		return errorEvent;
	}

	public void setErrorEvent(String errorEvent) {
		this.errorEvent = errorEvent;
	}
	//根据ActionId获得Action
	public Action queryAction(int actionId) {
		Iterator it = this.getActionlist().iterator();
		while(it.hasNext()) {
			Action action = (Action)it.next();
			if(actionId == action.getActionId()) {
				return action;
			}
		}
		return null;
	}

	public String getEquipment() {
		return equipment;
	}

	public void setEquipment(String equipment) {
		this.equipment = equipment;
	}

	public String getRecordName() {
		return recordName;
	}

	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public String getEvt() {
		return evt;
	}

	public void setEvt(String evt) {
		this.evt = evt;
	}

	public String getTransBy() {
		return transBy;
	}

	public void setTransBy(String transBy) {
		this.transBy = transBy;
	}
}
