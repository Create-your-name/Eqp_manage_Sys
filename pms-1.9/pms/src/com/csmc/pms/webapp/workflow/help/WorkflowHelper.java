package com.csmc.pms.webapp.workflow.help;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.ParserException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.common.helper.CommonHelper;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.eqp.helper.GenDCOPHelper;
import com.csmc.pms.webapp.util.CacheUtil;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.TPServiceException;
import com.csmc.pms.webapp.workflow.model.Action;
import com.csmc.pms.webapp.workflow.model.ActionItem;
import com.csmc.pms.webapp.workflow.model.ActionStatus;
import com.csmc.pms.webapp.workflow.model.Job;
import com.fa.object.ItemCollection;
import com.fa.object.Wafer;
import com.fa.object.WaferCollection;

public class WorkflowHelper {

	public static final String module = WorkflowHelper.class.getName();

	/**
	 * 调用Service保存动作信息
	 * service action(UPDATE)
	 * @param delegator
	 * @param dispatcher
	 * @param map
	 * @throws GenericServiceException
	 */
	public static void saveFlowAction(GenericDelegator delegator, 
    		LocalDispatcher dispatcher, Map map, String userNo) throws GenericServiceException {
		//Service Save Flow Action
    	Map result = dispatcher.runSync("saveFlowAction",UtilMisc.toMap("param" ,map, "userNo", userNo));
    	//throw exception
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
	}
	
	/**
	 * 调用Service删除动作信息，保留历史
	 * @param delegator
	 * @param dispatcher
	 * @param actionIndex
	 * @param userNo
	 * @throws GenericServiceException
	 */
	public static void deleteFlowAction(GenericDelegator delegator, 
    		LocalDispatcher dispatcher, String actionIndex, String userNo) throws GenericServiceException {
		//Service delete Flow Action
    	Map result = dispatcher.runSync("deleteFlowAction",UtilMisc.toMap("actionIndex" ,actionIndex, "userNo", userNo));
    	//throw exception
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
	}
	
	/**
	 * 保存动作历史
	 * @param delegator
	 * @param map
	 * @throws GenericEntityException
	 */
	public static void saveFlowActionHist(GenericDelegator delegator, Map map, String evt) throws GenericEntityException {
		GenericValue gv = delegator.makeValue("FlowActionHist", map);
		Long histIndex = delegator.getNextSeqId("flowActionHistIndex");
		gv.put("evt", evt);
		gv.put("histIndex", histIndex);
		delegator.create(gv);
	}
	
	/**
	 * 保存动作项目
	 * @param delegator
	 * @param dispatcher
	 * @param paramMap
	 * @throws GenericServiceException
	 */
	public static void saveFlowActionItem(GenericDelegator delegator, 
    		LocalDispatcher dispatcher, Map paramMap, String userNo) throws GenericServiceException {
		//Service Save Flow Item Action
    	Map result = dispatcher.runSync("saveFlowActionItem",UtilMisc.toMap("param" ,paramMap, "userNo", userNo));
    	//throw exception
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
	}
	
	/**
	 * 删除动作项目，保留历史
	 * @param delegator
	 * @param dispatcher
	 * @param itemIndex
	 * @param userNo
	 * @throws GenericServiceException
	 */
	public static void deleteFlowActionItem(GenericDelegator delegator, 
    		LocalDispatcher dispatcher, String itemIndex, String userNo) throws GenericServiceException {
		//Service Save Flow Item Action
    	Map result = dispatcher.runSync("deleteFlowActionItem",UtilMisc.toMap("itemIndex" ,itemIndex, "userNo", userNo));
    	//throw exception
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
	}
	
	/**
	 * 保存动作项目历史
	 * @param delegator
	 * @param map
	 * @throws GenericEntityException
	 */
	public static void saveFlowActionItemHist(GenericDelegator delegator, Map map, String evt) throws GenericEntityException {
		GenericValue gv = delegator.makeValue("FlowActionItemHist", map);
		Long histIndex = delegator.getNextSeqId("actionItemHistIndex");
		gv.put("evt", evt);
		gv.put("histIndex", histIndex);
		delegator.create(gv);
	}
	
	/**
	 * 保存动作项目Temp
	 * @param delegator
	 * @param map
	 * @throws GenericEntityException
	 */
	public static void saveFlowActionItemTemp(GenericDelegator delegator, Map map) throws GenericEntityException {
		map.put("updateTime", UtilDateTime.nowTimestamp());
		
		GenericValue gv = delegator.makeValidValue("FlowActionItemTemp", map);
		delegator.createOrStore(gv);
	}
	
	/**
	 * 保存动作状况
	 * @param delegator
	 * @param paramMap
	 * @throws GenericEntityException
	 */
	public static void saveFlowStatus(GenericDelegator delegator, Map paramMap) throws GenericEntityException {
		List list = new ArrayList();
		String transBy = (String)paramMap.get("transBy");
		Timestamp date = (Timestamp)paramMap.get("updateTime");
		String actionIndex = (String)paramMap.get("actionIndex");
		for(Iterator it = paramMap.keySet().iterator(); it.hasNext(); ) {
			String key = (String)it.next();
			if(key.startsWith("statusName_")) {
				//获得序号
				String orderNum = key.substring(key.indexOf("_") + 1);
				//获得index
				String statusIndex = (String)paramMap.get("statusIndex_" + orderNum);
				if("".equals(statusIndex)) {
					statusIndex = delegator.getNextSeqId("actionStatusIndex").toString();
				}
				//获得name
				String statusName = (String)paramMap.get(key);
				//new Record
				Map map = new HashMap();
				map.put("statusIndex", statusIndex);
				map.put("statusName", statusName);
				map.put("actionIndex", actionIndex);
				map.put("statusOrder", orderNum);
				map.put("transBy", transBy);
				map.put("updateTime", date);
				GenericValue gv = delegator.makeValue("FlowActionStatus", map);
				list.add(gv);
			}
		}
		delegator.storeAll(list);
	}
	
	public static void deleteFlowStatus(GenericDelegator delegator, String statusIndex) throws GenericEntityException {
		delegator.removeByAnd("FlowActionStatus", UtilMisc.toMap("statusIndex", statusIndex));
	}

	/**
	 * 查询获得JobTextFromEntity
	 * @param eventType
	 * @param eventIndex
	 * @param delegator
	 * @return
	 */
	public static String getJobTextFromEntity(String eventType, Long eventIndex, GenericDelegator delegator) {
		try {
			if(Constants.PM.equals(eventType)) {
				GenericValue formEntity = delegator.findByPrimaryKey("PmForm", UtilMisc.toMap("pmIndex", eventIndex));
				return formEntity.getString("jobText");
			} else if(Constants.PC.equals(eventType)) {
				GenericValue formEntity = delegator.findByPrimaryKey("PcForm", UtilMisc.toMap("pcIndex", eventIndex));
				return formEntity.getString("jobText");
			} else if(Constants.TS.equals(eventType)) {
				GenericValue formEntity = delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex", eventIndex));
				return formEntity.getString("jobText");
			}
		} catch(GenericEntityException e) {
			Debug.logError("GetJobTextError:" + e.getMessage(), module);
		}
		return "";
	}
	
	/**
	 * 查询下一步骤是否为结束
	 * @param job
	 * @param nextActionId
	 * @return
	 */
	public static boolean isJobEnd(Job job, String nextActionId) {
		Action nextAction = job.queryAction(Integer.parseInt(nextActionId));
		if("end".equals(nextAction.getNodeType())) {
			return true;
		}
		return false;
	}
	
	/**
	 * 生成流程描述
	 * @param jobText
	 * @param job
	 * @param currentActionId
	 * @param nextActionId
	 */
	public static String getJobText(String jobText, Job job, String currentActionId, String nextActionId) {
		StringBuffer sb = new StringBuffer();
		Action currentAction = job.queryAction(Integer.parseInt(currentActionId));
		
		if(job.getRunStatus() == Constants.JOB_START) {
			if(jobText.indexOf("流程") > 0) {
				sb.append("----------\n");
			}
			sb.append("流程:" + job.getJobName() + "\n");
			sb.append("0-开始\n");
		} else if(job.getRunStatus() == Constants.JOB_PROCESS) {
			sb.append(currentAction.getActionId() + "-" + currentAction.getActionName() + "\n");
			if(isJobEnd(job, nextActionId)) {
				sb.append(job.getActionlist().size() + "-结束\n");
			}
		}
		
		return jobText += sb.toString();
	}
	
	/**
	 * 保存当前动作相关信息
	 * @param action
	 * @param paramMap
	 * @param delegator
	 * @throws GenericEntityException
	 */
	public static void saveActionStepInfo(Action action, Map paramMap, GenericDelegator delegator) throws GenericEntityException {
		if (action.getActionIndex() != 0) {//outLineFormSave上传方法：开始和结束的actionIndex为0，不保存170613
			Map stepInfo = new HashMap();
			stepInfo.put("actionRecordIndex", paramMap.get("actionRecordIndex"));
			stepInfo.put("formJobIndex", paramMap.get("jobRelationIndex"));
			stepInfo.put("formType", paramMap.get("formType"));
			stepInfo.put("formIndex", paramMap.get("formIndex"));
			stepInfo.put("actionIndex", new Long(action.getActionIndex()));
			stepInfo.put("actionName", action.getActionName());
			stepInfo.put("actionType", action.getActionType());
			stepInfo.put("stepOrder", new Integer(action.getActionId()));
			stepInfo.put("stepComment", paramMap.get("comment"));
			stepInfo.put("transBy", paramMap.get("transBy"));
			stepInfo.put("updateTime", paramMap.get("updateTime"));
			//增加Empty判断
			stepInfo.put("isEmpty", paramMap.get("isEmpty"));
			GenericValue stepInfoEntity = delegator.makeValue("FlowActionRecord", stepInfo);
			delegator.create(stepInfoEntity);
		}
	}
	
	/**
	 * 填写保养表单，保存当前Action信息
	 * @param action
	 * @param paramMap
	 * @param delegator
	 * @throws GenericEntityException 
	 */
	public static void enterActionData(Action action, Map paramMap, GenericDelegator delegator) throws GenericEntityException {
		Long actionRecordIndex = (Long)paramMap.get("actionRecordIndex");
		String transBy = (String)paramMap.get("transBy");
		Timestamp updateTime = (Timestamp)paramMap.get("updateTime");
		String mailBody = "";
		
		List actionItemList = action.getItemlist();		
		if (CommonUtil.isNotEmpty(actionItemList)) {
			Iterator itemIte = actionItemList.iterator();
	
			List entityList = new ArrayList();
			int i = 0; 
			while(itemIte.hasNext()) {
				ActionItem item = (ActionItem)itemIte.next();
				Long itemIndex = null;
				if(Constants.ACTION_DCOP_TYPE.equals(action.getActionType())) {
					itemIndex = new Long(i);
				} else {
					itemIndex = new Long(item.getItemIndex());
				}
				String itemValue = (String) paramMap.get("item_" + itemIndex.longValue());
				String itemNote = (String) paramMap.get("note_" + itemIndex.longValue());
				Long pointIndex = delegator.getNextSeqId("actionItemPoint");
				
				Map itemMap = new HashMap();
				itemMap.put("itemName", item.getItemName());
				itemMap.put("pointIndex", pointIndex);
				itemMap.put("actionRecordIndex", actionRecordIndex);
				itemMap.put("formType", paramMap.get("formType"));
				itemMap.put("formIndex", paramMap.get("formIndex"));
				itemMap.put("itemIndex", itemIndex);
				itemMap.put("itemType", new Integer(item.getItemType()));
				itemMap.put("itemValue", itemValue);
				itemMap.put("itemOption", item.getItemOption());
				itemMap.put("itemUnit", item.getItemUnit());
				itemMap.put("itemOrder", new Long(item.getItemOrder()));
				itemMap.put("itemNote", itemNote);
				
				if (item.getItemType() == Constants.NUMBER_ITEM) {
					Double itemUpperSpec = item.getItemUpperSpec();
					Double itemLowerSpec = item.getItemLowerSpec();
					itemMap.put("itemUpperSpec", itemUpperSpec);
					itemMap.put("itemLowerSpec", itemLowerSpec);
					
					if (StringUtils.isNotEmpty(itemValue)) {
						double dItemValue = Double.parseDouble(itemValue);
						if (itemUpperSpec != null && dItemValue > itemUpperSpec.doubleValue()) {
							mailBody = mailBody + item.getItemName() + "：" + itemValue +" > 上限" + itemUpperSpec.toString() + "，备注：" + itemNote + "。\n";
						}
						if (itemLowerSpec != null && dItemValue < itemLowerSpec.doubleValue()) {
							mailBody = mailBody + item.getItemName() + "：" + itemValue +" < 下限" + itemLowerSpec.toString() + "，备注：" + itemNote + "。\n";
						}
					}
				}
				
				itemMap.put("transBy", transBy);
				itemMap.put("updateTime", updateTime);
				GenericValue itemEntity = delegator.makeValue("FlowItemPoints", itemMap);
				entityList.add(itemEntity);
				i++;
			}
			
			delegator.storeAll(entityList);
			
			//数据OOS时，send mail 设备组长，抄送填写人
			if (!"".equals(mailBody)) {
				String sendTo = AccountHelper.getMailOfSectionLeaderByUserNo(delegator, transBy);
				String sendCc = AccountHelper.getMailByAccountNo(delegator, transBy);
				
				GenericValue pmFormEntity = delegator.findByPrimaryKey("PmForm", UtilMisc.toMap("pmIndex", (Long) paramMap.get("formIndex")));
				String eqpId = pmFormEntity.getString("equipmentId");
				CommonUtil.sendMail(Constants.IT_SYSTEM_MAIL, sendTo, sendCc, "PMS - " + eqpId + " - " + action.getActionName() + "：数据OOS或未填写", mailBody);
			}
		}
	}
	
	/**
	 * 保存DCOP类型的ActionData
	 * @param delegator
	 * @param userLogin
	 * @param action
	 * @param paramMap
	 * @throws TPServiceException 
	 * @throws SQLProcessException 
	 */
	public static void enterDcopActionData(GenericDelegator delegator, GenericValue userLogin, Action action, Map paramMap) throws SQLProcessException {
		//查询DCOP数据
		String operationId = action.getActionName();
		String formType = (String) paramMap.get("formType");
		Long formIndex = (Long) paramMap.get("formIndex");
		String eqpId = "";
		String location = "";
		
		GenericDelegator csmcDelegator = GenericDelegator.getGenericDelegator("default");
		if(csmcDelegator == null) {Debug.logInfo("get csmcDelegator null", module);}
		GenericValue dcopUser = csmcDelegator.makeValue("UserLogin", UtilMisc.toMap("userLoginId", Constants.SUPER_USER, "currentPassword", Constants.PASSWORD, "enabled", "Y"));
		List list = null;
		if(Constants.PM_CHAR.equals(formType)) {
			String sql = "select a.equipment_id,b.location from pm_form a,equipment b where pm_index = " + formIndex.longValue() 
					+ " and a.equipment_id = b.equipment_id";
			list = SQLProcess.excuteSQLQuery(sql, delegator);
		} else if(Constants.TS_CHAR.equals(formType)) {
			String sql = "select a.equipment_id,b.location from abnormal_form a,equipment b where abnormal_index = " + formIndex + " and a.equipment_id = b.equipment_id";
			list = SQLProcess.excuteSQLQuery(sql, delegator);
		}
		
		if(CommonUtil.isNotEmpty(list)) {
			Map map = (Map)list.get(0);
			eqpId = (String)map.get("EQUIPMENT_ID");
			location = (String)map.get("LOCATION");
		}
		
		if(!"".equals(eqpId) && !"".equals(location)) {
//			List actionItemList = action.getItemlist()==null?new LinkedList():action.getItemlist();
			Map map = null;
			try {
				map = GenDCOPHelper.queryDcopFormat(operationId, delegator, dcopUser);
				Debug.logInfo("query dcop [" + operationId + "] success", module);
			} catch(TPServiceException e) {
				Debug.logInfo("query dcop [" + operationId + "] error", module);
				Debug.logInfo(e, module);
			}
			//set dcop data to job
			if(map != null) {
				String dcopType = (String) map.get("dcoptypecategory");
				if("ITEM".equals(dcopType)) {
					List itemlist = (List) map.get("itemlist");
					Iterator tpItemIt = itemlist.iterator();
					HashMap itemDCOP = new HashMap();
					int itemOrder = 0;
					while(tpItemIt.hasNext()) {
						com.fa.object.Item tpItem = (com.fa.object.Item)tpItemIt.next();
						String itemValue = (String) paramMap.get("item_" + itemOrder);
						tpItem.setItemValue(itemValue);
						
//						ActionItem item = new ActionItem();
//						item.setItemName(tpItem.getItemName());
//						item.setItemDescription(tpItem.getItemName());
//						item.setItemType(Constants.NUMBER_ITEM);
//						item.setItemIndex(itemOrder);
//						item.setItemLowerSpec(Double.parseDouble(tpItem.getItemLowLimit()));
//						item.setItemUpperSpec(Double.parseDouble(tpItem.getItemUpLimit()));
//						item.setItemOrder(itemOrder);
//						actionItemList.add(item);
						
						itemOrder++;
					}
					
					itemDCOP.put("itemlist",itemlist);
					itemDCOP.put("operid",action.getActionName());
					itemDCOP.put("location",location);
					itemDCOP.put("eqpid",eqpId);
					try {
						Map resultMap = GenDCOPHelper.enterGenDCOPItem(itemDCOP, delegator, dcopUser);
						Debug.logInfo("enter dcop [" + operationId + "] success", module);
						
						//调用SPC service判断OOC
						Map dataMap = new HashMap();
				        dataMap.put("locationId", itemDCOP.get("location"));            
				        dataMap.put("equipmentId", itemDCOP.get("eqpid")); 
				        dataMap.put("operationId", itemDCOP.get("operid"));
				        dataMap.put("dcopType", "ITEM");
				        dataMap.put("itemlist", itemDCOP.get("itemlist"));
				        GenericDelegator delegatorGui = CommonUtil.getGuiDelegator();
				        GenDCOPHelper.triggerSPCAlarm(delegatorGui,CommonUtil.getGuiDispatch(delegatorGui),resultMap,dataMap,userLogin);
					} catch(TPServiceException tpe) {
						Debug.logInfo("enter dcop [" + operationId + "] error", module);
						Debug.logInfo(tpe, module);
					}
//					action.setItemlist(actionItemList);
				} else if("COMP".equals(dcopType)) {
					ItemCollection itemlist = (ItemCollection) map.get("itemlist");
					WaferCollection waferlist = (WaferCollection)map.get("compstotest");
					
					Iterator tpItemIt = itemlist.iterator();
					HashMap itemDCOP = new HashMap();
					int itemOrder = 0;
					while(tpItemIt.hasNext()) {
						com.fa.object.Item tpItem = (com.fa.object.Item)tpItemIt.next();
						String itemValue = (String) paramMap.get("item_" + itemOrder);
						tpItem.setItemValue(itemValue);
						itemOrder++;
					}
					
					//COMP，PM中默认只输入一片数据
					if(CommonUtil.isNotEmpty(waferlist)) {
						int i = 0;
						for(Iterator waferIt = waferlist.iterator(); waferIt.hasNext();) {
							Wafer wafer = (Wafer)waferIt.next();
							wafer.setWaferid(String.valueOf(i++));
							wafer.setItemCollection(itemlist);
						}
				
						itemDCOP.put("waferlist",waferlist);
						itemDCOP.put("operid",action.getActionName());
						itemDCOP.put("location",location);
						itemDCOP.put("eqpid",eqpId);
						try {
							Map resultMap = GenDCOPHelper.enterGenDCOPComp(itemDCOP, delegator, dcopUser);
							Debug.logInfo("enter dcop [" + operationId + "] success", module);
							
							//调用SPC service判断OOC
	                        Map dataMap = new HashMap();
	                        dataMap.put("locationId", itemDCOP.get("location"));            
	                        dataMap.put("equipmentId", itemDCOP.get("eqpid")); 
	                        dataMap.put("operationId", itemDCOP.get("operid"));
	                        dataMap.put("dcopType", "COMP");
	                        dataMap.put("waferlist", itemDCOP.get("waferlist"));
	                        GenericDelegator delegatorGui = CommonUtil.getGuiDelegator();
	                        GenDCOPHelper.triggerSPCAlarm(delegatorGui,CommonUtil.getGuiDispatch(delegatorGui),resultMap,dataMap,userLogin);
						} catch(TPServiceException tpe) {
							Debug.logInfo("enter dcop [" + operationId + "] error", module);
							Debug.logInfo(tpe, module);
						}
					} else {
						Debug.logInfo("enter comp dcop [" + operationId + "] faild, comp size less then 0", module);
					}
				} else if("SITE".equals(dcopType)) {
					//TODO 
				}
			}
		}
	}
	
	/**
	 * 发送设备主管签核信息
	 * @param delegator
	 * @param map
	 * @throws GenericServiceException 
	 */
	public static Map sendSubmit(LocalDispatcher dispatcher, String object,
			String objectIndex, String objectName, String type, String user)
			throws GenericServiceException {
		return sendSubmitToProcess(dispatcher, object, objectIndex,
				objectName, type, user, "");
	}
	
	/**
	 * 发送工艺主管签核信息：动作项目，保养计划延期
	 * @param delegator
	 * @param map
	 * @throws GenericServiceException 
	 */
	public static Map sendSubmitToProcess(LocalDispatcher dispatcher,
			String object, String objectIndex, String objectName, String type,
			String user, String ownerProcess) throws GenericServiceException {
		// Service send to process owner
		Map result = dispatcher.runSync("sendSubmit", UtilMisc.toMap("user",
				user, "type", type, "object", object, "objectIndex",
				objectIndex, "objectName", objectName, "ownerProcess",
				ownerProcess));

		// throw exception
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
		return result;
	}
	
	/**
	 * 检查签核是否已经存在，true存在，false不存在
	 * @param delegator
	 * @param objectIndex
	 * @param objectName
	 * @return
	 */
	public static boolean checkSubmit(GenericDelegator delegator, String objectIndex, String object) {
		try {
			if(object.equals(Constants.SUBMIT_FLOW)) {//流程	
				GenericValue jobTemp = delegator.findByPrimaryKey("FlowJobTemp", UtilMisc.toMap("tempIndex", objectIndex));
				if(jobTemp.getString("submitType").equals(Constants.SUBMIT_INSERT)) {
					return false;
				} else {
					String eventObject = jobTemp.getString("eventObject");
					String eventType = jobTemp.getString("eventType");
					String jobIndex = jobTemp.getString("jobIndex");
					List list = delegator.findByAnd("FlowJobTemp", UtilMisc.toMap("eventObject", eventObject, "eventType", eventType, "status", "2", "jobIndex", jobIndex));
					if (CommonUtil.isNotEmpty(list)) return true;
				}			
			} else if(object.equals(Constants.SUBMIT_FOLLOW)) {//问题跟踪
				return existWfSubmitedFollow(delegator, objectIndex, object);
			} else if(object.equals(Constants.SUBMIT_PM_DELAY)) {//保养计划延期
				return existWfSubmitedPE(delegator, objectIndex, object);
			}
		} catch(GenericEntityException e) {
			return false;
		} catch (SQLProcessException e) {
			return false;
		}
		
		return false;
	}

	/** check exist follow submited object
	 * 问题跟踪工艺课长审批时，无需设备课长同时审批
	 * @param delegator
	 * @param objectIndex
	 * @param object
	 * @return 
	 * @throws GenericEntityException
	 */
	public static boolean existWfSubmitedFollow(GenericDelegator delegator,
			String objectIndex, String object) throws GenericEntityException {		
		List eqplist = delegator.findByAnd("WfSubmit", UtilMisc.toMap("objectIndex", objectIndex, "object", object, "status", Constants.SUBMITED));
		if (CommonUtil.isNotEmpty(eqplist)) {
			return true;
		}
		
		List processlist = delegator.findByAnd("WfSubmit", UtilMisc.toMap("objectIndex", objectIndex, "object", object, "statusProcess", Constants.SUBMITED));
		if (CommonUtil.isNotEmpty(processlist)) {
			return true;
		}
		
		return false;
	}
	
	/** 
	 * 动作项目、保养计划延期，需设备(E)和工艺(P)课长审批
	 * @param delegator
	 * @param objectIndex
	 * @param object
	 * @return 
	 * @throws GenericEntityException
	 * @throws SQLProcessException 
	 */
	public static boolean existWfSubmitedPE(GenericDelegator delegator,
			String objectIndex, String object) throws SQLProcessException {		
		String strSQL = "SELECT * FROM wf_submit s WHERE s.object = '" + object + "' and s.object_index =" + objectIndex
        	+ " and (s.status, s.status_process) in (('SUBMITED', 'SUBMITED'), ('SUBMITED', 'APPROVED'), ('APPROVED', 'SUBMITED'))";
		List list = SQLProcess.excuteSQLQuery(strSQL, delegator);
		if (list.size() > 0){
			return true;
		}		
		
		return false;
	}
	
	/**
	 * 保存送签的步骤信息
	 * @param delegator
	 * @param map
	 * @throws GenericEntityException 
	 */
	public static void saveSubmitControl(GenericDelegator delegator, Map map) throws GenericEntityException {
		Long controlIndex = delegator.getNextSeqId("submitControlIndex");
		map.put("controlIndex", controlIndex);
		GenericValue gv = delegator.makeValue("WfSubmitControl", map);
		delegator.create(gv);
	}
	
	/**
	 * 签核
	 * @param dispatcher
	 * @param delegator
	 * @param status 签核状态（APPROVED,REJECTED）
	 * @param object (FLW,FUW,FAI)
	 * @param objectIndex (对象index)
	 * return object_index;object;flowJobHistIndex
	 * @throws GenericEntityException
	 * @throws GenericServiceException 
	 */
	public static String submit(LocalDispatcher dispatcher,GenericDelegator delegator, String user, String status, String submitIndex, String comment) throws GenericEntityException, GenericServiceException {
		GenericValue submit = delegator.findByPrimaryKey("WfSubmit", UtilMisc.toMap("submitIndex", submitIndex));		
		String object_index = submit.getString("objectIndex");
		String object = submit.getString("object");		
		
		GenericValue wfObject = delegator.findByPrimaryKey("WfSubmitObject", UtilMisc.toMap("object", object));
		String serviceName = wfObject.getString("rule");
		//String objectDesc = wfObject.getString("objectDescription");
		
		//调用Service进行签核
    	Map result = dispatcher.runSync(serviceName,UtilMisc.toMap("user" ,user.toUpperCase(), "status", status, "submit", submit, "comment", comment));
    	//throw exception
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
		
		submit = delegator.findByPrimaryKey("WfSubmit", UtilMisc.toMap("submitIndex", submitIndex));
		String flowJobHistIndex = submit.getString("flowJobHistIndex");
		
        return object_index + ";" + object + ";" + flowJobHistIndex;
	}
	
	/**
	 * 获得JobDefineCacheKey
	 * @param request
	 * @return
	 */
	public static String getJobDefineCacheKey(HttpServletRequest request) {
		String jobIndex = request.getParameter("jobIndex");
		if(CommonUtil.isEmpty(jobIndex) || "0".equals(jobIndex)) {
			if(request.getAttribute("jobIndex") != null)
				jobIndex = String.valueOf(request.getAttribute("jobIndex"));
		}
		String id = request.getSession().getId();
		if(CommonUtil.isEmpty(jobIndex) || Long.parseLong(jobIndex) == 0) {
			jobIndex = "new-";
		} else {
			jobIndex += "-";
		}
		return jobIndex + id;
	}

	/**
	 * 缓存中得到Job设定
	 * @param request
	 * @return
	 */
	public static Job getJobDefineFromCache(HttpServletRequest request) {
		CacheUtil cacheUtil = CacheUtil.getInstance();
		String key = WorkflowHelper.getJobDefineCacheKey(request);
		Job job = (Job) cacheUtil.getObjectFromCache(key,CacheUtil.JOB_DEFINE_CACHE);
		return job;
	}

	/**
	 * 将动作放入Job
	 * @param actionIndex
	 * @param delegator
	 * @throws GenericEntityException
	 */
	public static void setActionToJob(Job job, String actionIndex, GenericDelegator delegator) throws GenericEntityException {
		GenericValue actionEntity = delegator.findByPrimaryKey("FlowAction", UtilMisc.toMap("actionIndex", actionIndex));
		Action action = new Action();
		//index
		action.setActionIndex(Long.parseLong(actionIndex));
		//描述
		action.setActionDescription(actionEntity.getString("actionDescription"));
		//名字
		action.setActionName(actionEntity.getString("actionName"));
		//actionType(normal,dcop)
		action.setActionType(actionEntity.getString("actionType"));
		//使用
		action.setEnabled(Integer.parseInt(actionEntity.getString("enabled")));
		//冻结
		//action.setFrozen(Integer.parseInt(actionEntity.getString("frozen")));
		//nodeType
		action.setNodeType("action");
		List actionList = job.getActionlist();
		
		//get end Action
		Action endAction = (Action)actionList.get(actionList.size()-1);
		//actionId
		action.setActionId(endAction.getActionId());
		
		//setStatus
		setActionStatusToJob(action, delegator);
		
		actionList.add(endAction.getActionId(), action);
		
		//update endAction
		endAction.setActionId(endAction.getActionId()+1);
	}

	/**
	 * 将状态放入action
	 * @param action
	 * @param delegator
	 * @throws GenericEntityException
	 */
	public static void setActionStatusToJob(Action action, GenericDelegator delegator) throws GenericEntityException {
		List statusEntityList = delegator.findByAnd("FlowActionStatus", UtilMisc.toMap("actionIndex", new Long(action.getActionIndex())));
		Iterator it = statusEntityList.iterator();
		List statusList = action.getStatusList();
		statusList = statusList == null?new LinkedList():statusList;
		
		while(it.hasNext()) {
			GenericValue statusEntity = (GenericValue)it.next();
			ActionStatus status = new ActionStatus();
			//index
			status.setStatusIndex(statusEntity.getLong("statusIndex").longValue());
			//name
			status.setStatusName(statusEntity.getString("statusName"));
			//nextActionId
			status.setNextActionId(action.getActionId()+1);
			
			statusList.add(status);
		}
		action.setStatusList(statusList);
	}

	/**
	 * 将页面信息保存进Job
	 * @param job
	 * @param param
	 */
	public static void savePageInfoToJob(Job job, Map param) {
		String jobName = (String) param.get("jobName");
		String jobDescription = (String) param.get("jobDescription");
		String eventObject = (String) param.get("eventObject");
		String eventType = (String) param.get("eventType");
		job.setJobName(jobName);
		job.setJobDescription(jobDescription);
		job.setEventObject(eventObject);
		job.setEventType(eventType);
		
		List actionList = job.getActionlist();
		Iterator actionIt = actionList.iterator();
		
		while(actionIt.hasNext())  {
			Action action = (Action) actionIt.next();
			List statusList = action.getStatusList();
			if(CommonUtil.isNotEmpty(statusList)) {
				Iterator statusIt = statusList.iterator();
				//set nextActionId
				while(statusIt.hasNext()) {
					ActionStatus status = (ActionStatus) statusIt.next();
					if(status.getStatusIndex() != 0) {
						String key = action.getActionId() + "_" + status.getStatusIndex();
						if(CommonUtil.isNotNull(param.get(key))) {
							int nextActionId = Integer.parseInt((String)param.get(key));
							status.setNextActionId(nextActionId);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 上传保存当前Action中的actionItem
	 * OutLineService调用
	 * @param action
	 * @param paramMap
	 * @param delegator
	 * @throws GenericEntityException 
	 */
	public static void enterActionItemInfo(Action action, Map paramMap, GenericDelegator delegator) throws GenericEntityException {
		Long actionRecordIndex = (Long)paramMap.get("actionRecordIndex");
		String transBy = (String)paramMap.get("transBy");
		Timestamp updateTime = (Timestamp)paramMap.get("updateTime");
		
		List actionItemList = action.getItemlist();
		
		if(CommonUtil.isNotEmpty(actionItemList)) {
			Iterator itemIte = actionItemList.iterator();
	
			List entityList = new ArrayList();
			while(itemIte.hasNext()) {
				ActionItem item = (ActionItem)itemIte.next();
				Long itemIndex = new Long(item.getItemIndex());
				String itemValue = item.getItemValue();
				String itemNote = item.getItemNode();
				Long pointIndex = delegator.getNextSeqId("actionItemPoint");
				
				Map itemMap = new HashMap();
				itemMap.put("pointIndex", pointIndex);
				itemMap.put("actionRecordIndex", actionRecordIndex);
				itemMap.put("formType", paramMap.get("formType"));
				itemMap.put("formIndex", paramMap.get("formIndex"));
				itemMap.put("itemIndex", itemIndex);
				itemMap.put("itemType", new Integer(item.getItemType()));
				itemMap.put("itemValue", itemValue);
				itemMap.put("itemNote", itemNote);
				if(item.getItemType() == Constants.NUMBER_ITEM) {
					itemMap.put("itemUpperSpec", item.getItemUpperSpec());
					itemMap.put("itemLowerSpec", item.getItemLowerSpec());
				}
				itemMap.put("transBy", transBy);
				itemMap.put("updateTime", updateTime);
				
				itemMap.put("itemName", item.getItemName());
				//itemMap.put("itemOrder", new Integer(item.getItemOrder()));
				itemMap.put("itemOption", item.getItemOption());
				itemMap.put("itemUnit", item.getItemUnit());
				
				GenericValue itemEntity = delegator.makeValue("FlowItemPoints", itemMap);
				entityList.add(itemEntity);
			}
			delegator.storeAll(entityList);
		}
	}
	
	/**
	 * 初始化动作List
	 * @return
	 */
	public static List initFlowActionList() {
		List actionList = new LinkedList();
		//StartAction
		 Action action = new Action();
		 action.setActionId(0);
		 action.setActionName("开始");
		 action.setNodeType("start");
		 actionList.add(action);
		 
		 //end action
		 Action endAction = new Action();
		 endAction.setActionId(actionList.size());
		 endAction.setNodeType("end");
		 endAction.setActionName("结束");
		 actionList.add(endAction);
		 
		 //startStatus
		 ActionStatus status = new ActionStatus();
		 status.setStatusName("下一步");
		 status.setNextActionId(1);
		 List statusList = new LinkedList();
		 statusList.add(status);
		 action.setStatusList(statusList);
		 return actionList;
	}
	
	/**
	 * 更新ActionStatus
	 * @param action
	 */
	public static void updateActionStatus(Action action) {
		List statuslist = action.getStatusList();
		for(Iterator it = statuslist.iterator(); it.hasNext(); ) {
			ActionStatus status = (ActionStatus)it.next();
			status.setNextActionId(action.getActionId()+1);
		}
	}
	
	/**
	 * 获得离线输入的Flow图
	 * @return
	 * @throws ParserException 
	 */
	public static String getOfflineFlowHtml(String jobRelationIndex) throws ParserException {
		StringBuffer bs = new StringBuffer();
		NodeIterator enumeration;
        Node node;
        
        Map map = new HashMap();
        map.put("jobRelationIndex", jobRelationIndex);
        String path = "http://localhost:8080/pms/pms/workflow/flowShowByOffline.jsp";//流程为空error todo
        
        Parser parser = CommonHelper.callHttpPost(path, map);
        
        for (enumeration = parser.elements(); enumeration.hasMoreNodes();) {
            node = enumeration.nextNode();
            bs.append(node.toHtml());
        }
		return bs.toString();
	}
	
	public static boolean isDcopName(GenericDelegator delegator, String dcopName) {
		String sql = "SELECT count(*) count"
					+ "  FROM oper@plldb"
					+ " WHERE (substr(opername, 2, 1) = '7' or substr(opername, 2, 1) = '8')"
					+ "  and frozen = 1"
					+ "   and opername || operversion = '" + dcopName + "'";
		try {
			List list = SQLProcess.excuteSQLQuery(sql,delegator);
			if(list.size() > 0 ) {
				Map map = (Map)list.get(0);
				int count = Integer.parseInt(String.valueOf(map.get("COUNT")));
				if(count > 0 ) return true;
			}
		} catch(SQLProcessException e) {
			return false;
		}
		return false;
	}
	
	public static String getItemTypeText(int itemType) {
		String itemText = "文字";
		switch (itemType) {
			case Constants.TEXT_ITEM:
				itemText = "文字";
				break;
			case Constants.NUMBER_ITEM:
				itemText = "数字";
				break;
			case Constants.OPTION_ITEM:
				itemText = "选项";
				break;
		}
		return itemText;
	}
	
	public static String getEvtText(String evt) {
		String evtText = "";
		if (Constants.INSERT.equals(evt)) {
			evtText = "新增";
		} else if (Constants.UPDATE.equals(evt)) {
			evtText = "修改";
		} else if (Constants.DELETE.equals(evt)) {
			evtText = "删除";
		}
		return evtText;
	}
	
	public static String getSubmitTypeText(String submitType) {
		String submitTypeText = "";
		if (Constants.SUBMIT_INSERT.equals(submitType)) {
			submitTypeText = "新增";
		} else if (Constants.SUBMIT_MODIFY.equals(submitType)) {
			submitTypeText = "修改";
		} else if (Constants.SUBMIT_DELETE.equals(submitType)) {
			submitTypeText = "删除";
		} else if (Constants.SUBMIT_PATCH.equals(submitType)) {
			submitTypeText = "补填";
		}
		return submitTypeText;
	}
	
	public static String getStatusText(int status) {
		String statusText = "";
		switch (status) {
			case Constants.SAVED_CODE:
				statusText = "暂存";
				break;
			case Constants.APPROVED_CODE:
				statusText = "同意";
				break;
			case Constants.SUBMITED_CODE:
				statusText = "申请中";
				break;
			case Constants.REJECTED_CODE:
				statusText = "拒绝";
				break;
		}
		return statusText;
	}
	
	public static String convertEvtToType(String evt) {
		String submitType = "";
		if (Constants.INSERT.equals(evt)) {
			submitType = Constants.SUBMIT_INSERT;
		} else if (Constants.UPDATE.equals(evt)) {
			submitType = Constants.SUBMIT_MODIFY;
		} else if (Constants.DELETE.equals(evt)) {
			submitType = Constants.SUBMIT_DELETE;
		}
		return submitType;
	}
	
	public static boolean isUsedAction(GenericDelegator delegator, String actionIndex) throws SQLProcessException {
    	String sql = "select count(*) num from flow_job where job_content like '%actionIndex=' || chr(34) || '" + actionIndex +"' || chr(34) || '%'";
        List list = SQLProcess.excuteSQLQuery(sql, delegator);
        Map map = (Map)list.get(0);
        int num = Integer.parseInt((String)map.get("NUM"));
        if(num > 0) return true;
        return false;
    }
    
    public static boolean isSubmitedAction(GenericDelegator delegator, String actionIndex) throws SQLProcessException {
    	String sql = "select count(*) num from flow_job_temp where job_content like '%actionIndex=' || chr(34) || '" + actionIndex + "' || chr(34) || '%'";
    	
    	// 暂存或签核中的流程
    	sql = sql + " and status in (0,2)";
    	
        List list = SQLProcess.excuteSQLQuery(sql, delegator);
        Map map = (Map)list.get(0);
        int num = Integer.parseInt((String)map.get("NUM"));
        if(num > 0) return true;
        return false;
    }
    
    public static boolean isSubmitedActionItemTemp(GenericDelegator delegator, String actionIndex) throws GenericEntityException {
    	List itemTempList = delegator.findByAnd("FlowActionItemTemp", UtilMisc.toMap("actionIndex", actionIndex, "status", new Integer(Constants.SUBMITED_CODE)));
    	if (itemTempList != null && itemTempList.size() > 0) {
    		return true;
    	}
        return false;
    }

	public static String getFlowActionMailContent(GenericDelegator delegator, String userNo, String actionIndex) throws GenericEntityException {
		String mailContent = null;
		GenericValue flowActionGv = delegator.findByPrimaryKey("FlowAction", UtilMisc.toMap("actionIndex", actionIndex));
		List statusList = delegator.findByAnd("FlowActionStatus", UtilMisc.toMap("actionIndex", actionIndex), UtilMisc.toList("statusOrder"));
		List itemList = delegator.findByAnd("FlowActionItem", UtilMisc.toMap("actionIndex", actionIndex), UtilMisc.toList("itemOrder"));
		
		mailContent = "操作人: " + userNo + "\n";
		mailContent += "类型: " + flowActionGv.getString("eventType") + "\n";
		mailContent += "设备大类: " + flowActionGv.getString("eventName") + "\n";
		
		mailContent += "\n";
		mailContent += "--------------------动作/状况--------------------\n";
		mailContent += "动作名: " + flowActionGv.getString("actionName") + "\n";
		mailContent += "动作描述: " + flowActionGv.getString("actionDescription") + "\n";
		mailContent += "NORMAL/DCOP: " + flowActionGv.getString("actionType") + "\n";
		mailContent += "1使用/0未用: " + flowActionGv.getString("enabled") + "\n";
		mailContent += "1延时/0未延时输入: " + flowActionGv.getString("empty") + "\n";		 
		
		mailContent += "NO.    状况\n";
		for(Iterator it = statusList.iterator(); it.hasNext(); ) {
			GenericValue gv = (GenericValue) it.next();			
			mailContent += gv.getString("statusOrder") + "      " + gv.getString("statusName") + "\n";
		}
		
		mailContent += "\n";
		mailContent += "--------------------项目--------------------\n";
		mailContent += "序号    项目名称            说明            样式      单位      上限      下限      预设\n";
		for(Iterator it = itemList.iterator(); it.hasNext(); ) {
			GenericValue gv = (GenericValue) it.next();			
			mailContent += gv.getString("itemOrder") + "        "
						+ gv.getString("itemName")+ "        "
						+ gv.getString("itemDescription")+ "        "
						+ WorkflowHelper.getItemTypeText(gv.getInteger("itemType").intValue())+ "        "
						+ gv.getString("itemUnit")+ "        "
						+ gv.getString("itemUpperSpec")+ "        "
						+ gv.getString("itemLowerSpec")+ "        "
						+ gv.getString("defaultValue") + "\n";
		}
		return mailContent;
	 }
	
	public static List getProcessSectionLeaderList(GenericDelegator delegator) throws SQLProcessException {
    	String sql = "select t1.section_leader,t1.section,t2.account_name"
    		+ " from equipment_section t1, account t2"
    		+ " where t1.section_leader=t2.account_no and t1.section like '%工艺%'"
    		+ " order by t1.section";    	
    	
        List list = SQLProcess.excuteSQLQuery(sql, delegator);        
        return list;
    }

	/**
	 * @param job
	 * @return Flow Mail Content StringBuffer
	 */
	public static StringBuffer getFlowMailContent(Job job) {
		StringBuffer mailContent = new StringBuffer();
		mailContent.append("操作类型:" + job.getEvt() + "\n");
		mailContent.append("操作人:" + job.getTransBy() + "\n");
		
		//流程
		mailContent.append("----------------------------------------\n");
		mailContent.append("流程名:"+job.getJobName()+"\n");
		mailContent.append("流程描述:"+job.getJobDescription()+"\n");
		
		//维护情况记录
		mailContent.append("流程设定:");
		if(job != null && !job.getActionlist().isEmpty()){
		    Iterator it = job.getActionlist().iterator(); 
		    while(it.hasNext()) 
		    { 
		        Action action = (Action)it.next(); 
		        mailContent.append("序号:"+action.getActionId()+"\n");
		        mailContent.append("维护简要步骤及数据规范:"+action.getActionName()+"\n");
		        if(CommonUtil.isNotEmpty(action.getItemlist())) 
		        {
		            for(Iterator itemIt = action.getItemlist().iterator(); itemIt.hasNext(); ) 
		            { 
		                ActionItem item = (ActionItem) itemIt.next(); 
		                mailContent.append("步骤名:"+item.getItemName()+"\n");
		                mailContent.append("上限:");
		                if(item.getItemType()==Constants.NUMBER_ITEM) { 
		                    if(item.getItemUpperSpec()!=null){
		                        mailContent.append(item.getItemUpperSpec());
		                    }
		                }
		                mailContent.append("\n");
		                mailContent.append("下限:");
		                if(item.getItemType()==Constants.NUMBER_ITEM) { 
		                    if(item.getItemLowerSpec()!=null){
		                        mailContent.append(item.getItemLowerSpec());
		                    }
		                }
		                mailContent.append("\n");
		            }
		        }
		        mailContent.append("----------------------------------------\n");
		    }
		}
		return mailContent;
	}
	
	public static String getFlowActionItemMailContent(GenericDelegator delegator, GenericValue histGv) throws GenericEntityException {
		String mailContent = null;
		String actionIndex = histGv.getString("actionIndex");
		GenericValue flowActionGv = delegator.findByPrimaryKey("FlowAction", UtilMisc.toMap("actionIndex", actionIndex));
		List statusList = delegator.findByAnd("FlowActionStatus", UtilMisc.toMap("actionIndex", actionIndex), UtilMisc.toList("statusOrder"));
		
		mailContent = "操作人: " + histGv.getString("transBy") + "\n";
		mailContent += "类型: " + flowActionGv.getString("eventType") + "\n";
		mailContent += "设备大类: " + flowActionGv.getString("eventName") + "\n";
		
		mailContent += "\n";
		mailContent += "--------------------动作/状况--------------------\n";
		mailContent += "动作名: " + flowActionGv.getString("actionName") + "\n";
		mailContent += "动作描述: " + flowActionGv.getString("actionDescription") + "\n";
		mailContent += "NORMAL/DCOP: " + flowActionGv.getString("actionType") + "\n";
		mailContent += "1使用/0未用: " + flowActionGv.getString("enabled") + "\n";
		mailContent += "1延时/0未延时输入: " + flowActionGv.getString("empty") + "\n";		 
		
		mailContent += "NO.    状况\n";
		for(Iterator it = statusList.iterator(); it.hasNext(); ) {
			GenericValue gv = (GenericValue) it.next();			
			mailContent += gv.getString("statusOrder") + "      " + gv.getString("statusName") + "\n";
		}
		
		mailContent += "\n";
		mailContent += "--------------------动作项目（仅显示申请变更的项目）--------------------\n";
		mailContent += "操作类型    序号    项目名称            说明            样式      单位      上限      下限      预设\n";
		mailContent += WorkflowHelper.getEvtText(histGv.getString("evt")) + "        "
					+ histGv.getString("itemOrder") + "        "
					+ histGv.getString("itemName")+ "        "
					+ histGv.getString("itemDescription")+ "        "
					+ WorkflowHelper.getItemTypeText(histGv.getInteger("itemType").intValue())+ "        "
					+ histGv.getString("itemUnit")+ "        "
					+ histGv.getString("itemUpperSpec")+ "        "
					+ histGv.getString("itemLowerSpec")+ "        "
					+ histGv.getString("defaultValue") + "\n";
		return mailContent;
	 }
}
