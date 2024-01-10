package com.csmc.pms.webapp.workflow.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.util.CacheUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.workflow.help.WorkflowHelper;
import com.csmc.pms.webapp.workflow.model.Action;
import com.csmc.pms.webapp.workflow.model.Job;
import com.csmc.pms.webapp.workflow.model.engine.JobEngine;

public class WorkflowService {

	public static final String module = WorkflowService.class.getName();
	
	/**
	 * ���涯������¼��ʷ
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map saveFlowAction(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		
		Map param = (Map)context.get("param");
		String userNo = (String)context.get("userNo");
		Timestamp nowTime = UtilDateTime.nowTimestamp();
		String evt = "";
		
		Map result = new HashMap();
		try {			
			param.put("transBy", userNo);
			param.put("updateTime", nowTime);					
			
			 // ��ѯ�Ƿ�Ϊ�¼�¼
			GenericValue checkGv = delegator.findByPrimaryKey("FlowAction", UtilMisc.toMap("actionIndex", param.get("actionIndex")));
			if (checkGv == null) {
				evt = Constants.INSERT;
			} else {
				evt = Constants.UPDATE;
			}
			
			GenericValue gv = delegator.makeValidValue("FlowAction", param);
			WorkflowHelper.saveFlowActionHist(delegator, gv, evt);
			delegator.createOrStore(gv);
			
			//��������״��
			WorkflowHelper.saveFlowStatus(delegator, param);
			//ɾ������״��
			String statusIndexString = (String)param.get("deleteStatus");
			String[] statusIndexs = statusIndexString.split(",");
			for(int i = 0 ; i < statusIndexs.length ; i++) {
				String statusIndex = statusIndexs[i];
				WorkflowHelper.deleteFlowStatus(delegator, statusIndex);
			}
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * ɾ����������¼��ʷ
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map deleteFlowAction(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		
		String actionIndex = (String)context.get("actionIndex");
		String userNo = (String)context.get("userNo");
		
		Map result = new HashMap();
		try {
			//��֤���gv
			GenericValue gv = delegator.findByPrimaryKey("FlowAction", UtilMisc.toMap("actionIndex", actionIndex));
			gv.set("transBy", userNo);
			gv.set("updateTime", new Date(System.currentTimeMillis()));
			WorkflowHelper.saveFlowActionHist(delegator, gv, Constants.DELETE);//��¼��ʷ			
			delegator.removeValue(gv);//ɾ������
			
			//ɾ������Item
			List itemList = delegator.findByAnd("FlowActionItem", UtilMisc.toMap("actionIndex", actionIndex));
			for (Iterator it = itemList.iterator(); it.hasNext(); ) {
				GenericValue item = (GenericValue) it.next();
				item.set("transBy", userNo);
				item.set("updateTime", new Date(System.currentTimeMillis()));
				WorkflowHelper.saveFlowActionItemHist(delegator, item, Constants.DELETE);
				item.remove();
			}
			
			//ɾ������Status
			delegator.removeByAnd("FlowActionStatus", UtilMisc.toMap("actionIndex", actionIndex));
			
			//ɾ������ItemTemp
			delegator.removeByAnd("FlowActionItemTemp", UtilMisc.toMap("actionIndex", actionIndex));
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * �������̶���
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map copyFlowAction(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		
		String actionIndex = (String)context.get("actionIndex");
		String userNo = (String)context.get("userNo");
		Timestamp nowDay = UtilDateTime.nowTimestamp();
		String actionName = (String) context.get("actionName");
		String eventName = (String) context.get("eventName");
		
		
		Map result = new HashMap();
		try {
			//��ñ�Copy����
			GenericValue gv = delegator.findByPrimaryKey("FlowAction", UtilMisc.toMap("actionIndex", actionIndex));
			List itemList = delegator.findByAnd("FlowActionItem", UtilMisc.toMap("actionIndex", actionIndex));
			List statusList = delegator.findByAnd("FlowActionStatus", UtilMisc.toMap("actionIndex", actionIndex));
			
			//�趨Copy����
			Long copyActionIndex = delegator.getNextSeqId("flowActionIndex");
			gv.set("actionIndex", copyActionIndex);
			gv.set("actionName", actionName);
			gv.set("eventName", eventName);
			gv.set("transBy", userNo);
			gv.set("updateTime", nowDay);
			delegator.create(gv);
			WorkflowHelper.saveFlowActionHist(delegator, gv, Constants.INSERT);
			
			//�趨CopyItem
			for(Iterator it = itemList.iterator(); it.hasNext(); ) {
				GenericValue item = (GenericValue) it.next();
				Long itemIndex = delegator.getNextSeqId("flowActionItemIndex");
				item.set("itemIndex", itemIndex);
				item.set("actionIndex", copyActionIndex);
				item.set("transBy", userNo);
				item.set("updateTime", nowDay);
				item.create();
				WorkflowHelper.saveFlowActionItemHist(delegator, item, Constants.INSERT);
			}
			
			//�趨CopyStatus
			for(Iterator it = statusList.iterator(); it.hasNext(); ) {
				GenericValue status = (GenericValue) it.next();
				Long statusIndex = delegator.getNextSeqId("actionStatusIndex");
				status.set("statusIndex", statusIndex);
				status.set("actionIndex", copyActionIndex);
				status.set("transBy", userNo);
				status.set("updateTime", nowDay);
				status.create();
			}
			
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * ���涯����Ŀ
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map saveFlowActionItem(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		
		Map param = (Map)context.get("param");
		String userNo = (String)context.get("userNo");
		Timestamp nowTime = UtilDateTime.nowTimestamp();
		String evt = "";
		
		Map result = new HashMap();
		try {			
			param.put("transBy", userNo);
			param.put("updateTime", nowTime);			
			
			// ��ѯ�Ƿ�Ϊ�¼�¼
			GenericValue checkGv = delegator.findByPrimaryKey("FlowActionItem", UtilMisc.toMap("itemIndex", param.get("itemIndex")));
			if (checkGv == null) {
				evt = Constants.INSERT;
			} else {
				evt = Constants.UPDATE;
			}
			
			GenericValue gv = delegator.makeValidValue("FlowActionItem", param);
			WorkflowHelper.saveFlowActionItemHist(delegator, gv, evt);
			delegator.createOrStore(gv);
			
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * ɾ��������Ŀ��������ʷ
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map deleteFlowActionItem(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		
		String itemIndex = (String)context.get("itemIndex");
		String userNo = (String)context.get("userNo");
		
		Map result = new HashMap();
		try {
			//��֤���gv
			GenericValue gv = delegator.findByPrimaryKey("FlowActionItem", UtilMisc.toMap("itemIndex", itemIndex));
			//��¼��ʷ
			gv.set("transBy", userNo);
			gv.set("updateTime", new Date(System.currentTimeMillis()));
			WorkflowHelper.saveFlowActionItemHist(delegator, gv, Constants.DELETE);
			//ɾ������
			delegator.removeValue(gv);
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * ��������
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map saveFlow(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		
		Job job = (Job)context.get("job");
		String user = (String)context.get("user");
		Timestamp nowTime = UtilDateTime.nowTimestamp();
		
		Map result = new HashMap();
		try {
			Long tempIndex = job.getTempIndex()!=0?
				new Long(job.getTempIndex()):delegator.getNextSeqId("jobTempIndex");
			
			Map entityMap = new HashMap();

			if(job.isNewFlag()) {
				job.setJobIndex(delegator.getNextSeqId("jobDefineIndex").longValue());
				entityMap.put("submitType", Constants.SUBMIT_INSERT);
			} else {
				entityMap.put("submitType", Constants.SUBMIT_MODIFY);
			}
			
			JobEngine engine = JobEngine.create();
			String xml = engine.formatJob(job);
			
			entityMap.put("tempIndex", tempIndex);
			entityMap.put("jobIndex", new Long(job.getJobIndex()));
			entityMap.put("jobName", job.getJobName());
			entityMap.put("jobDescription", job.getJobDescription());
			entityMap.put("eventObject", job.getEventObject());
			entityMap.put("eventType", job.getEventType());
			entityMap.put("jobContent", xml);
			entityMap.put("status", new Integer(Constants.SAVED_CODE));
			entityMap.put("owner", user);
			entityMap.put("transBy", user);
			entityMap.put("updateTime", nowTime);
			GenericValue jobEntity = delegator.makeValue("FlowJobTemp", entityMap);
			delegator.createOrStore(jobEntity);
			
			job.setTempIndex(tempIndex.longValue());
			result.put("returnJob", job);
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * Copy����
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map copyFlow(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		
		String flowIndex = (String)context.get("flowIndex");
		String user = (String)context.get("user");
		Timestamp nowTime = UtilDateTime.nowTimestamp();
		String jobName = (String) context.get("jobName");
		String jobDescription = (String) context.get("jobDescription");
		
		Map result = new HashMap();
		try {
			GenericValue flow = delegator.findByPrimaryKey("FlowJob", UtilMisc.toMap("jobIndex", flowIndex));
			Long tempIndex = delegator.getNextSeqId("jobTempIndex");
			Long jobIndex = delegator.getNextSeqId("jobDefineIndex");
			
			Map entityMap = new HashMap();
			entityMap.put("tempIndex", tempIndex);
			entityMap.put("jobIndex", jobIndex);
			entityMap.put("submitType", Constants.SUBMIT_INSERT);
			entityMap.put("jobName", jobName);
			entityMap.put("jobDescription", jobDescription);
			entityMap.put("eventObject", flow.getString("eventObject"));
			entityMap.put("eventType", flow.getString("eventType"));
			entityMap.put("jobContent", flow.getString("jobContent"));
			entityMap.put("status", new Integer(Constants.SAVED_CODE));
			entityMap.put("owner", user);
			entityMap.put("transBy", user);
			entityMap.put("updateTime", nowTime);
			GenericValue jobEntity = delegator.makeValue("FlowJobTemp", entityMap);
			delegator.create(jobEntity);
			
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * ��д��������д������Ŀ�󣬻��ѯ����ɵı��������޸���������
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map saveFlowEditedData(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		Map result = new HashMap();
		Map paramMap = (Map)context.get("paramMap");
		
		try {
			for(Iterator it = paramMap.keySet().iterator(); it.hasNext(); ) {
				String key = (String)it.next();
				if(key.startsWith("note_")) {
					String actionRecordIndex = key.split("_")[2];
					String actionNote = (String)paramMap.get(key);
					GenericValue gv = delegator.findByPrimaryKey("FlowActionRecord", UtilMisc.toMap("actionRecordIndex",actionRecordIndex));
					gv.set("stepComment", actionNote);
					gv.set("isEmpty", "0");
					delegator.store(gv);
				} else if(key.startsWith("value_")) {
					String pointIndex = key.split("_")[2];
					String actionId = key.split("_")[1];
					String itemNote = (String)paramMap.get("itemNote_"+ actionId +"_" + pointIndex);
					String itemValue = (String)paramMap.get(key);
					GenericValue gv = delegator.findByPrimaryKey("FlowItemPoints", UtilMisc.toMap("pointIndex", pointIndex));
					gv.set("itemNote", itemNote);
					gv.set("itemValue", itemValue);
					delegator.store(gv);
				}
			}
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}

		return result;
	}
	
	/**
	 * �������̵�ǰ����Ϣ
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map storeActionStep(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		Map result = new HashMap();
		
		JobEngine jobEngine = (JobEngine) context.get("jobEngine");
		//ҳ�洫�ݵ�ֵ
		Map paramMap = jobEngine.getActionStepInfo();
		
		//��ǰʱ��
		Timestamp updateTime = (Timestamp)paramMap.get("updateTime");
		try {
			//Job
			Job job = jobEngine.getJob();
			
			String jobRelationIndex = (String)paramMap.get("jobRelationIndex");
			String transBy = (String)paramMap.get("transBy");
			String sessionId = (String)paramMap.get("sessionId");
			int runStatus = job.getRunStatus();
			
			//��õ�ǰ��Action��Ϣ
			String actionId = (String) paramMap.get("currentActionId");
			Action action = job.queryAction(Integer.parseInt(actionId));
			Debug.logInfo("----CurrentAction(" + action.getActionIndex() + ")----", module);
			
			//��ѯ���JobFormRelationEntity
			GenericValue jobRelationEntity = delegator.findByPrimaryKey("FormJobRelation", UtilMisc.toMap("seqIndex", jobRelationIndex));
			
			
			//��¼��ǰ�������Ϣ(FlowActionRecord)
			String eventType = jobRelationEntity.getString("eventType");
			Long eventIndex = jobRelationEntity.getLong("eventIndex");
			Long actionRecordIndex = delegator.getNextSeqId("actionRecord");
			paramMap.put("formType", eventType);
			paramMap.put("formIndex", eventIndex);
			paramMap.put("actionRecordIndex", actionRecordIndex);
			if(runStatus == Constants.JOB_PROCESS) {
				WorkflowHelper.saveActionStepInfo(action, paramMap, delegator);
				Debug.logInfo("----Save ActionStepInfo success("+ actionRecordIndex +")----", module);
			}
			
			//����AcionTyp���ò�ͬ��Function����(Normal,Dcop)
			String actionType = action.getActionType();
			if(Constants.ACTION_DCOP_TYPE.equals(actionType)) {
				//call tp
				WorkflowHelper.enterDcopActionData(delegator, jobEngine.getUserLoginId(), action, paramMap);
				//save flowpoints
				WorkflowHelper.enterActionData(action, paramMap, delegator);
				Debug.logInfo("----Enter DcopActionItem Data success(" + action.getActionIndex() + ")----", module);
			} else if(Constants.ACTION_NORMAL_TYPE.equals(actionType)) {
				WorkflowHelper.enterActionData(action, paramMap, delegator);
				Debug.logInfo("----Enter ActionItem Data success(" + action.getActionIndex() + ")----", module);
			}
			
			//�����һ��Action��Ϣ
			String nextActionId = (String)paramMap.get("nextActionId");
			Action nextAction = job.queryAction(Integer.parseInt(nextActionId));
			Debug.logInfo("----Get NextAction(" + nextAction.getActionIndex() + ")----", module);
			
			//����JobRelation�е�Job״̬��NextActionId��������Holdס�󣬿��Բ�ѯ�����һ����Ϣ
			if(runStatus == Constants.JOB_START) {
				jobRelationEntity.set("jobStatus", new Integer(Constants.JOB_PROCESS));
				jobRelationEntity.set("jobStartTime", updateTime);
				jobRelationEntity.set("nextActionId", nextActionId);
			} else if(runStatus == Constants.JOB_PROCESS) {
				jobRelationEntity.set("nextActionId", nextActionId);
				//��������״̬���ж���һ���Ƿ�Ϊ����
				if(WorkflowHelper.isJobEnd(job, nextActionId)) {
					jobRelationEntity.set("jobStatus", new Integer(Constants.JOB_END));
					jobRelationEntity.set("jobOverTime", updateTime);
				}
			}
			delegator.store(jobRelationEntity);
			Debug.logInfo("----Save jobRelationEntity success(" + jobRelationIndex + ")----", module);
			
			//����JobText,����Form��
			String jobText = UtilFormatOut.checkNull(WorkflowHelper.getJobTextFromEntity(eventType, eventIndex, delegator));
			jobText = WorkflowHelper.getJobText(jobText, job, actionId, nextActionId);
			//store to form
			GenericValue formEntity = null;
			if(Constants.PM.equals(eventType)) {
				formEntity = delegator.findByPrimaryKey("PmForm", UtilMisc.toMap("pmIndex", eventIndex));
			} else if(Constants.PC.equals(eventType)) {
				formEntity = delegator.findByPrimaryKey("PcForm", UtilMisc.toMap("pcIndex", eventIndex));
			} else if(Constants.TS.equals(eventType)) {
				formEntity = delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex", eventIndex));
			}
			if(formEntity != null) {
				formEntity.setString("jobText", jobText);
				job.setJobText(jobText);
				delegator.store(formEntity);
			}
			
			//����Job��Ϣ������Cache
			if(runStatus == Constants.JOB_START) {
				job.setRunStatus(Constants.JOB_PROCESS);
				job.setNextActionId(Integer.parseInt(nextActionId));
			} else if(runStatus == Constants.JOB_PROCESS) {
				job.setNextActionId(Integer.parseInt(nextActionId));
				if("end".equals(nextAction.getNodeType())) {
					job.setRunStatus(Constants.JOB_END);
					//�ж��Ƿ�Ѳ�죬Ѳ��ֱ�Ӹ���״̬
					if(Constants.PC.equals(eventType)) {
						GenericValue pcForm = delegator.findByPrimaryKey("PcForm", UtilMisc.toMap("pcIndex", eventIndex));
						pcForm.set("status", String.valueOf(Constants.OVER));
						pcForm.set("endTime", updateTime);
						pcForm.set("endUser", transBy);
						pcForm.set("updateTime", updateTime);
						delegator.store(pcForm);
					}
				}
			}
			String cacheKey = jobEngine.getCacheElementKey(jobRelationIndex, sessionId);
			CacheUtil cacheUtil = CacheUtil.getInstance();
			cacheUtil.setObjectToCache(cacheKey, CacheUtil.JOB_CACHE, job, true);
			
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
}
