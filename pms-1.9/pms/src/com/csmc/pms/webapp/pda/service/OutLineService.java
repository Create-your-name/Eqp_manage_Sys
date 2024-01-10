package com.csmc.pms.webapp.pda.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.workflow.help.WorkflowHelper;
import com.csmc.pms.webapp.workflow.model.Action;
import com.csmc.pms.webapp.workflow.model.Job;

public class OutLineService {
	public static final String module = OutLineService.class.getName();
	
	/**
	 * 保存JOB信息
	 * (1)将action记录保存进FLOW_ACTION_RECORD表。
	 * (2)将item记录保存至FLOW_ITEM_POINTS表
	 * (3)将JOB信息保存至相关表单中(TS)
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map outLineFormSave(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		Map result = new HashMap();
		Map jobMap = (Map) context.get("jobMap");
		Job job=(Job)jobMap.get("job");
				
		try {
			List formJobRelationList=delegator.findByAnd("FormJobRelation", UtilMisc.toMap("jobIndex",String.valueOf(job.getJobIndex()),"eventType",job.getEventType(),"eventIndex",job.getFormIndex()));
			if(formJobRelationList!=null){
				GenericValue gv=(GenericValue)formJobRelationList.get(0);
				String jobRelationIndex=gv.getString("seqIndex");
				int jobStatus=Integer.parseInt(gv.getString("jobStatus"));
				//表单的JOB未完成
				if(Constants.JOB_END!=jobStatus){
					Map actionRecordMap=new HashMap();
					Map formJobRelationMap=new HashMap();
					formJobRelationMap.put("seqIndex", jobRelationIndex);
					formJobRelationMap.put("jobStatus", String.valueOf(Constants.JOB_END));
					//更新FormJobRelation中的JOB状态为完成
					GenericValue formJobRelation = delegator.makeValue("FormJobRelation", formJobRelationMap);
					delegator.store(formJobRelation);
					actionRecordMap.put("jobRelationIndex", jobRelationIndex);
					actionRecordMap.put("formType", job.getEventType());
					actionRecordMap.put("formIndex", job.getFormIndex());
					actionRecordMap.put("transBy", job.getOperator());
					Timestamp finishData=Timestamp.valueOf(job.getFinishData());
					actionRecordMap.put("updateTime", finishData);
					List actionList=job.getActionlist();
					if(actionList!=null&&actionList.size()>0){
						for(int i=0;i<actionList.size();i++){
							Action action=(Action)actionList.get(i);
							Long actionRecordIndex = delegator.getNextSeqId("actionRecord");
							actionRecordMap.put("actionRecordIndex", actionRecordIndex);
							actionRecordMap.put("comment", action.getActionNote());
							//保存action信息
							WorkflowHelper.saveActionStepInfo(action, actionRecordMap, delegator);
							//保存actonItem信息
							Map itemMap=new HashMap();
							itemMap.put("actionRecordIndex", actionRecordIndex);
							itemMap.put("transBy", job.getOperator());
							itemMap.put("updateTime", finishData);
							itemMap.put("formType", job.getEventType());
							itemMap.put("formIndex", job.getFormIndex());
							WorkflowHelper.enterActionItemInfo(action, itemMap, delegator);
						}
					}
					//如果是TS需要更新errorEvent,errorCode,errorReason
					if(Constants.TS.equalsIgnoreCase(job.getEventType())){
						List eventList=delegator.findByAnd("EventCategory", UtilMisc.toMap("category",job.getErrorEvent()));
						GenericValue equipment=delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId",job.getEquipment()));
						List errorReasonList = delegator.findByAnd("PmsReason",UtilMisc.toMap("reasonType",Constants.ABNORMAL, "equipmentType",(String) equipment.getString("equipmentType"),"reason", job.getErrorReason()));
						Map formMap=new HashMap();
						formMap.put("errorCode", job.getErrorCode());
						String eventIndex="";
						String category="";
						if(eventList!=null&&eventList.size()>0){
							GenericValue event=(GenericValue)eventList.get(0);
							eventIndex=event.getString("eventIndex");
							category=event.getString("category");
						}
						formMap.put("eventCategory", eventIndex);
						formMap.put("eventIndex", category);
						String abnormalReasonIndex="";
						String abnormalReason="";
						if(errorReasonList!=null&&errorReasonList.size()>0){
							GenericValue reason=(GenericValue)errorReasonList.get(0);
							abnormalReasonIndex=reason.getString("reasonIndex");
							abnormalReason=reason.getString("reason");
						}
						formMap.put("abnormalReasonIndex", abnormalReasonIndex);
						formMap.put("abnormalReason", abnormalReason);
						formMap.put("abnormalIndex", job.getFormIndex());
						GenericValue abnormalForm = delegator.makeValue("AbnormalForm", formMap);
						delegator.store(abnormalForm);
					}
					//保成成功
					result.put("returnMsg", "0");
				}else{
					//JOB的状态已完成
					result.put("returnMsg", "1");
				}
			}else{
				//FormJobRelation表查无记录
				result.put("returnMsg", "-1");
			}
		}catch (Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE,ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}
}
