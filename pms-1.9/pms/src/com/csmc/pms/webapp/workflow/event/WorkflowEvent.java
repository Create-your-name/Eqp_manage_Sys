package com.csmc.pms.webapp.workflow.event;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityWhereString;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.common.helper.CommonHelper;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.parts.help.PartsHelper;
import com.csmc.pms.webapp.qufollow.help.QuFollowHelper;
import com.csmc.pms.webapp.util.CacheUtil;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.GeneralEvents;
import com.csmc.pms.webapp.workflow.help.WorkflowHelper;
import com.csmc.pms.webapp.workflow.model.Action;
import com.csmc.pms.webapp.workflow.model.ActionComparator;
import com.csmc.pms.webapp.workflow.model.Job;
import com.csmc.pms.webapp.workflow.model.Test;
import com.csmc.pms.webapp.workflow.model.engine.JobEngine;
import com.csmc.pms.webapp.workflow.model.engine.JobSupport;

public class WorkflowEvent extends GeneralEvents{
	 public static final String module = WorkflowEvent.class.getName();
	 
	 /**
	  * 设定动作入口
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String queryFlowActionEntryInfo(HttpServletRequest request,
			HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 try {
			 List eqpTypeList = CommonHelper.getEquipmentTypeList(delegator);
			 List pcStyleList = delegator.findAllCache("PcStyle");
			 request.setAttribute("eqpTypeList", eqpTypeList);
			 request.setAttribute("pcStyleList", pcStyleList);
		 } catch (Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	/**
	 * 1. 根据设备大类查询动作(PM) 2. 根据巡检类型查询动作(PC)
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	 public static String queryFlowActionList(HttpServletRequest request,
			HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 String event = request.getParameter("eventType");
		 String eventName = request.getParameter("eventName");
		 try {
			 List actionList = delegator.findByAnd("FlowAction", UtilMisc.toMap("eventType", event, "eventName", eventName), UtilMisc.toList("actionName"));
			 request.setAttribute("actionList", actionList);
			 
          	 List eqpTypeList = CommonHelper.getEquipmentTypeList(delegator);
			 request.setAttribute("eqpTypeList", eqpTypeList);
		 } catch (Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 1. 查询动作信息（PK）
	  * 2. 查询动作状况
	  * 3. 查询动作项目
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String queryFlowAction(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 String actionIndex = request.getParameter("actionIndex");
		 try {
			 if(CommonUtil.isEmpty(actionIndex)) {
				 actionIndex = (String)request.getAttribute("actionIndex");
				 if(CommonUtil.isEmpty(actionIndex)) return "success";
			 }
			 //查询动作信息
			 GenericValue gv = delegator.findByPrimaryKey("FlowAction", UtilMisc.toMap("actionIndex", actionIndex));
			 //查询动作状况
			 List statusList = delegator.findByAnd("FlowActionStatus", UtilMisc.toMap("actionIndex", actionIndex), UtilMisc.toList("statusOrder"));
			 //查询动作项目
			 List itemList = delegator.findByAnd("FlowActionItem", UtilMisc.toMap("actionIndex", actionIndex), UtilMisc.toList("itemOrder"));
			 //查询动作项目Temp
			 List itemTempList = delegator.findByAnd("FlowActionItemTemp", UtilMisc.toMap("actionIndex", actionIndex), UtilMisc.toList("itemOrder"));
			 //查询工艺课长
			 List ownerProcessList = WorkflowHelper.getProcessSectionLeaderList(delegator);			 
			 
			 request.setAttribute("flowAction", gv);
			 request.setAttribute("statusList", statusList);
			 request.setAttribute("itemList", itemList);
			 request.setAttribute("itemTempList", itemTempList);
			 request.setAttribute("ownerProcessList", ownerProcessList);
			 
			 //如果登录人为质量保证部(dept_index == 10010)，则周期命名必须以MSA开头
			 request.setAttribute("isMsaDept", AccountHelper.isMsaDept(request, delegator) ? "true" : "false");
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 
		 return "success";
	 }
	 
	 /**
	  * 保存动作信息
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String saveFlowAction(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		 Map paramMap = WorkflowEvent.getInitParams(request);
		 String userNo = CommonUtil.getUserNo(request);		 
		 String actionIndex = request.getParameter("actionIndex");
		 String actionType = request.getParameter("actionType");
		 String actionName = request.getParameter("actionName");
		 
		 try {
			 //如果是DCOP，判断isDcopName
			 if(Constants.ACTION_DCOP_TYPE.equalsIgnoreCase(actionType)) {				 
				 if(!WorkflowHelper.isDcopName(delegator, actionName)) {
					 request.setAttribute("_ERROR_MESSAGE_", "DCOP动作名在系统中不存在！");
					 return "error";
				 }
			 }
			 
			 //判断是否新增，自动生成Sequence
			 if(CommonUtil.isEmpty(actionIndex)) {
				 actionIndex = delegator.getNextSeqId("flowActionIndex").toString();
				 paramMap.put("actionIndex", actionIndex);
			 }
			 
			 //保存动作			 
			 WorkflowHelper.saveFlowAction(delegator, dispatcher, paramMap, userNo);
			 request.setAttribute("actionIndex", actionIndex);
			 request.setAttribute("_EVENT_MESSAGE_","保存基本动作成功！");
			 
			 //send mail to IPQA的路乐乐和郭蕾，cc 申请人或登录用户、课长、经理             
             String mailContent = WorkflowHelper.getFlowActionMailContent(delegator, userNo, actionIndex);			 
			 String sendCc = AccountHelper.getMailOfUserAndLeaders(request, delegator, userNo);
             CommonUtil.sendMail(Constants.IT_SYSTEM_MAIL, Constants.QC_MAIL, sendCc, "PMS - 基本动作设定(动作/状况) 保存成功: " + actionName, mailContent);
		 } catch (GenericServiceException e) {
	        	String message = CommonUtil.checkOracleException(e);
	        	request.setAttribute("_ERROR_MESSAGE_", message);
	        	return "error";
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 
		 return "success";
	 }
	 
	 /**
	  * 复制action
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String copyFlowAction(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		 String actionIndex = request.getParameter("seqIndex");
		 String actionName = request.getParameter("actionName").trim().toUpperCase();
		 String eqpType = request.getParameter("eqpType");
		 
		 try {
			//Service Save Flow Action
		    Map result = dispatcher.runSync("copyFlowAction", UtilMisc.toMap("actionIndex" ,actionIndex, "userNo", CommonUtil.getUserNo(request), "actionName", actionName,"eventName", eqpType));
		    request.setAttribute("_EVENT_MESSAGE_","COPY基本动作成功！");
		    request.setAttribute("eventName",eqpType);
		    //String event = request.getParameter("eventType");
			// String eventName = request.getParameter("eventName");
		    //throw exception
			if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
				throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
			}
		 } catch(Exception e) {
			 Debug.logError(module, e.getMessage());
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 
		 return "success";
	 }
	 
	 /**
	  * 查询DCOPList(当动作是DCOP类型的时候)
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String queryDcopList(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 try {
			 String actionName = request.getParameter("actionName").toUpperCase().trim();
			 String sql = "SELECT distinct opername||operversion dcop_id, description FROM oper@plldb WHERE (substr(opername,2,1)='7' or substr(opername,2,1)='8') and frozen=1";
			 if(CommonUtil.isNotEmpty(actionName)) {
				 sql += "and opername||operversion like '" + actionName + "%'";
			 }
			 List list = SQLProcess.excuteSQLQuery(sql,delegator);
			 request.setAttribute("dcoplist", list);
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 
		 return "success";
	 }
	 
	 /**
	  * 删除动作信息
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String deleteFlowAction(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		 String actionIndex = request.getParameter("actionIndex");
		
		 try {
			if (WorkflowHelper.isUsedAction(delegator, actionIndex)) {
				request.setAttribute("_ERROR_MESSAGE_", "该动作已经绑定流程！");
				return "error";
			}

			if (WorkflowHelper.isSubmitedAction(delegator, actionIndex)) {
				request.setAttribute("_ERROR_MESSAGE_", "该动作已经申请绑定流程！");
				return "error";
			}
			
			if (WorkflowHelper.isSubmitedActionItemTemp(delegator, actionIndex)) {
				request.setAttribute("_ERROR_MESSAGE_", "该动作的项目已经申请修改，请等待审批完成后删除！");
				return "error";
			}

			WorkflowHelper.deleteFlowAction(delegator, dispatcher, actionIndex, CommonUtil.getUserNo(request));
			request.setAttribute("_EVENT_MESSAGE_", "删除动作成功！");
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 
		 return "success";
	 }
	 
	 /**
	  * 检查是否能够删除Status
	  * @param request
	  * @param response
	  */
	 public static void deleteFlowActionStatus(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 String actionIndex = request.getParameter("actionIndex");
		 JSONObject object = new JSONObject();
		 try {
			 if(WorkflowHelper.isUsedAction(delegator, actionIndex)
					 || WorkflowHelper.isSubmitedAction(delegator, actionIndex)) {
				 object.put("checkflag", "error");
			 } else {
				 object.put("checkflag", "success");
			 }
			 response.getWriter().write(object.toString());
		 } catch(Exception e) {
			 Debug.logError(module, e.getMessage());
		 }
	 }
	 
	 /**
	  * 冻结/解冻动作
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String frozenFlowAction(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 try {
			 
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
				request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		 }
		 
		 return "success";
	 }
	 
	/**
	  * 查询动作项目（PK）
	  * @param request
	  * @param response
	  * @return
	  */
	public static void queryFlowActionItem(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String itemIndex = request.getParameter("itemIndex");
		 
		try {
			JSONObject pcStyle = new JSONObject();	
			GenericValue gv = delegator.findByPrimaryKey("FlowActionItem", UtilMisc.toMap("itemIndex", itemIndex));
			
			pcStyle.put("flowActionItemTempIndex", "");
	        pcStyle.put("itemIndex", gv.getString("itemIndex"));
	        pcStyle.put("itemName", gv.getString("itemName"));
	        pcStyle.put("itemDescription", UtilFormatOut.checkNull(gv.getString("itemDescription")));
	        pcStyle.put("itemType", UtilFormatOut.checkNull(gv.getString("itemType")));
	        pcStyle.put("defaultValue", UtilFormatOut.checkNull(gv.getString("defaultValue")));
	        pcStyle.put("itemOption", UtilFormatOut.checkNull(gv.getString("itemOption")));
	        pcStyle.put("itemUnit", UtilFormatOut.checkNull(gv.getString("itemUnit")));
	        pcStyle.put("itemOrder", UtilFormatOut.checkNull(gv.getString("itemOrder")));
	        pcStyle.put("itemLowerSpec", UtilFormatOut.checkNull(gv.getString("itemLowerSpec")));
	        pcStyle.put("itemUpperSpec", UtilFormatOut.checkNull(gv.getString("itemUpperSpec")));
	        response.getWriter().write(pcStyle.toString());
			//request.setAttribute("actionItem", gv);
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
		}
	}
	 
	/**
	  * 保存动作项目
	  * @param request
	  * @param response
	  * @return
	  */
	public static String saveFlowActionItem(HttpServletRequest request,
				HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		Map paramMap = GeneralEvents.getInitParams(request);
		String userNo = CommonUtil.getUserNo(request);
		String itemIndex = request.getParameter("itemIndex");
		String actionIndex = request.getParameter("actionIndex");
		String itemName = request.getParameter("itemName");
		 
		try {
			if("".equals(itemIndex)) {
				//新增FlowActionItem
				itemIndex = delegator.getNextSeqId("flowActionItemIndex").toString();
				paramMap.put("itemIndex", itemIndex);
			}
			WorkflowHelper.saveFlowActionItem(delegator, dispatcher, paramMap, userNo);
			request.setAttribute("_EVENT_MESSAGE_", "保存动作项目成功！");
			 
			//send mail to IPQA的路乐乐和郭蕾，cc 申请人或登录用户、课长、经理
			String mailContent = WorkflowHelper.getFlowActionMailContent(delegator, userNo, actionIndex);
            String sendCc = AccountHelper.getMailOfUserAndLeaders(request, delegator, userNo);                    
            CommonUtil.sendMail(Constants.IT_SYSTEM_MAIL, Constants.QC_MAIL, sendCc, "PMS - 基本动作设定(动作项目) 新增或修改: " + itemName, mailContent);
		} catch (GenericServiceException e) {
	       	String message = CommonUtil.checkOracleException(e);
	        request.setAttribute("_ERROR_MESSAGE_", message);
	        return "error";
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		 
		return "success";
	}
	 
	/**
	  * 删除动作项目
	  * @param request
	  * @param response
	  * @return
	  */
	public static String deleteFlowActionItem(HttpServletRequest request,
				HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);		 
		LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		String userNo = CommonUtil.getUserNo(request);
		String itemIndex = request.getParameter("itemIndex");
		String actionIndex = request.getParameter("actionIndex");
		String itemName = request.getParameter("itemName");
		 
		try {
			WorkflowHelper.deleteFlowActionItem(delegator, dispatcher, itemIndex, userNo);
			request.setAttribute("_EVENT_MESSAGE_", "删除动作项目成功！");
			 
			//send mail to IPQA的路乐乐和郭蕾，cc 申请人或登录用户、课长、经理
			String mailContent = WorkflowHelper.getFlowActionMailContent(delegator, userNo, actionIndex);
	        String sendCc = AccountHelper.getMailOfUserAndLeaders(request, delegator, userNo);                    
	        CommonUtil.sendMail(Constants.IT_SYSTEM_MAIL, Constants.QC_MAIL, sendCc, "PMS - 基本动作设定(动作项目) 已删除: " + itemName, mailContent);
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
		 
		return "success";
	 }
	
	/**
	  * 查询动作项目Temp
	  * @param request
	  * @param response
	  * @return
	  */
	public static void queryFlowActionItemTemp(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String flowActionItemTempIndex = request.getParameter("flowActionItemTempIndex");
		 
		try {
			JSONObject jsonObj = new JSONObject();	
			GenericValue gv = delegator.findByPrimaryKey("FlowActionItemTemp", UtilMisc.toMap("flowActionItemTempIndex", flowActionItemTempIndex));
			
			jsonObj.put("flowActionItemTempIndex", flowActionItemTempIndex);
	        jsonObj.put("itemIndex", gv.getString("itemIndex"));
	        jsonObj.put("itemName", gv.getString("itemName"));
	        jsonObj.put("itemDescription", UtilFormatOut.checkNull(gv.getString("itemDescription")));
	        jsonObj.put("itemType", UtilFormatOut.checkNull(gv.getString("itemType")));
	        jsonObj.put("defaultValue", UtilFormatOut.checkNull(gv.getString("defaultValue")));
	        jsonObj.put("itemOption", UtilFormatOut.checkNull(gv.getString("itemOption")));
	        jsonObj.put("itemUnit", UtilFormatOut.checkNull(gv.getString("itemUnit")));
	        jsonObj.put("itemOrder", UtilFormatOut.checkNull(gv.getString("itemOrder")));
	        jsonObj.put("itemLowerSpec", UtilFormatOut.checkNull(gv.getString("itemLowerSpec")));
	        jsonObj.put("itemUpperSpec", UtilFormatOut.checkNull(gv.getString("itemUpperSpec")));
	        response.getWriter().write(jsonObj.toString());
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
		}
	}
	
	/**
	  * 保存动作项目Temp
	  * @param request
	  * @param response
	  * @return
	  */
	public static String saveFlowActionItemTemp(HttpServletRequest request,	HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		Map paramMap = GeneralEvents.getInitParams(request);
		String userNo = CommonUtil.getUserNo(request);
		String itemIndex = request.getParameter("itemIndex");
		String flowActionItemTempIndex = request.getParameter("flowActionItemTempIndex");
		 
		try {
			if ("".equals(itemIndex)) {
				//新增动作项目申请
				paramMap.put("evt", Constants.INSERT);				
				paramMap.put("itemIndex", delegator.getNextSeqId("flowActionItemIndex"));
				flowActionItemTempIndex = delegator.getNextSeqId("flowActionItemTempIndex").toString();
			} else if ("".equals(flowActionItemTempIndex)) {
				//操作当前项目列表：点击链接修改，查询temp表是否已存在
				GenericValue itemTempGv = CommonUtil.findFirstRecordByAnd(delegator, "FlowActionItemTemp", UtilMisc.toMap("itemIndex", itemIndex));
				if (itemTempGv == null) {
					//首次修改动作项目
					GenericValue itemGv = delegator.findByPrimaryKey("FlowActionItem", UtilMisc.toMap("itemIndex", itemIndex));
					paramMap.put("itemType", itemGv.getString("itemType"));
					
					paramMap.put("evt", Constants.UPDATE);					
					flowActionItemTempIndex = delegator.getNextSeqId("flowActionItemTempIndex").toString();
				} else if (Constants.SUBMITED_CODE != itemTempGv.getInteger("status").intValue()
						&& !Constants.DELETE.equals(itemTempGv.getString("evt"))) {
					//再次修改动作项目，非提交状态
					//一个item修改，一次只能提交一个申请
					flowActionItemTempIndex = itemTempGv.getString("flowActionItemTempIndex");					
				} else {
					request.setAttribute("_ERROR_MESSAGE_", "动作项目申请已提交或申请删除，请审批完成后继续修改!");
					return "error";
				}
			} else {
				//操作Temp申请列表
				GenericValue itemTempGv = delegator.findByPrimaryKey("FlowActionItemTemp", UtilMisc.toMap("flowActionItemTempIndex", flowActionItemTempIndex));
				if (Constants.SUBMITED_CODE == itemTempGv.getInteger("status").intValue()) {
					request.setAttribute("_ERROR_MESSAGE_", "动作项目申请已提交，请审批完成后继续修改!");
					return "error";
				}
				
				if (Constants.DELETE.equals(itemTempGv.getString("evt"))) {
					request.setAttribute("_ERROR_MESSAGE_", "动作项目已申请删除，请核对申请列表!");
					return "error";
				}
			}
			
			paramMap.put("flowActionItemTempIndex", flowActionItemTempIndex);			
			paramMap.put("transBy", userNo);
			paramMap.put("status", new Integer(Constants.SAVED_CODE));
			WorkflowHelper.saveFlowActionItemTemp(delegator, paramMap);
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		 
		return "success";
	}
	
	/**
	  * 申请删除动作项目,删除申请保存到Temp
	  * @param request
	  * @param response
	  * @return
	  */
	public static String deleteFlowActionItemApply(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String itemIndex = request.getParameter("itemIndex");
		 
		try {
			List itemTempList = delegator.findByAnd("FlowActionItemTemp", UtilMisc.toMap("itemIndex", itemIndex));
			if (CommonUtil.isNotEmpty(itemTempList)) {
				request.setAttribute("_ERROR_MESSAGE_", "此动作项目申请已存在，请修改申请列表 或 等待审批完成!");
				return "error";
			}
			
			GenericValue gv = delegator.findByPrimaryKey("FlowActionItem", UtilMisc.toMap("itemIndex", itemIndex));
			Map map = gv.getAllFields();
			map.put("evt", Constants.DELETE);
			map.put("flowActionItemTempIndex", delegator.getNextSeqId("flowActionItemTempIndex"));
			map.put("status", new Integer(Constants.SAVED_CODE));
			WorkflowHelper.saveFlowActionItemTemp(delegator, map);
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		 
		return "success";
	 }
	 
	/**
	  * 删除动作项目Temp
	  * @param request
	  * @param response
	  * @return
	  */
	public static String deleteFlowActionItemTemp(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String flowActionItemTempIndex = request.getParameter("flowActionItemTempIndex");
		 
		try {
			GenericValue gv = delegator.findByPrimaryKey("FlowActionItemTemp", UtilMisc.toMap("flowActionItemTempIndex", flowActionItemTempIndex));
			delegator.removeValue(gv);
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		 
		return "success";
	 }
	
	/**
	  * 动作项目Temp，发送设备主管和工艺主管签核
	  * FAI
	  * @param request
	  * @param response
	  * @return
	  */
	public static String sendSubmitFlowActionItem(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		String user = CommonUtil.getUserNo(request);
		String actionIndex = request.getParameter("actionIndex");
		String ownerProcess = request.getParameter("ownerProcess");
		 
		try {
			List itemTempList = delegator.findByAnd("FlowActionItemTemp", UtilMisc.toMap("actionIndex", actionIndex), UtilMisc.toList("itemOrder"));
			for(Iterator it = itemTempList.iterator(); it.hasNext(); ) {
				GenericValue gv = (GenericValue) it.next();
				int itemTempStatus = gv.getInteger("status").intValue();
				String objectIndex = gv.getString("flowActionItemTempIndex");
				String objectName = gv.getString("itemName");
				String submitType = WorkflowHelper.convertEvtToType(gv.getString("evt"));
				
				if (itemTempStatus == Constants.SAVED_CODE || itemTempStatus == Constants.REJECTED_CODE) {
					Map result = WorkflowHelper.sendSubmitToProcess(dispatcher, Constants.SUBMIT_FLOW_ACTION_ITEM, objectIndex, objectName, submitType, user, ownerProcess);
					// 883609，huanghp，如果没有取到正常的主管，标示一下，2009-02
					if ("NoLeader".equals(result.get(ModelService.ERROR_MESSAGE))) {
						request.setAttribute("_ERROR_MESSAGE_", "找不到主管，发送签核信息失败，请联系系统负责人!");
					} else {				 
						request.setAttribute("_EVENT_MESSAGE_", "已经成功送达设备和工艺主管签核！");
					}
					
					String sendTo = AccountHelper.getMailOfSectionLeaderByUserNo(delegator, user)
									+ AccountHelper.getMailByAccountNo(delegator, ownerProcess);
					String sendCc = AccountHelper.getMailByAccountNo(delegator, user);
					CommonUtil.sendMail(Constants.IT_SYSTEM_MAIL, sendTo, sendCc, "PMS - 动作项目签核流程 已启动: " + objectName, "请设备和工艺主管登录PM系统进行签核。");
				}
			}
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		 
		return "success";
	 }
	 
	 /********************流程设定***************************/ 
	 /**
	  * 查询获得处理程序
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String queryFlowList(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 String eventObject = request.getParameter("eventObject");
		 String eventType = request.getParameter("eventType");
		 String pmPeriod = request.getParameter("pmPeriod");
		 try {
			 List list = delegator.findByAnd("FlowJob", UtilMisc.toMap("eventObject", eventObject, "eventType", eventType), UtilMisc.toList("jobName"));
			 request.setAttribute("flowList", list);
			 
			 // 显示未申请签核和拒绝的
			 String whereString = "event_object = '" + eventObject + "' and event_type = '" + eventType + "' and (status = 0 or status = 3)";		
			 EntityWhereString con = new EntityWhereString(whereString);
			 List templist = delegator.findByCondition("FlowJobTemp",con,null, UtilMisc.toList("jobName"));
			 //List templist = delegator.findByAnd("FlowJobTemp", UtilMisc.toMap("eventObject", eventObject, "eventType", eventType, "status", "0"));
			 request.setAttribute("flowTempList", templist);
			 request.setAttribute("eventType", eventType);
			 if ("PM".equals(eventType)) {
				 GenericValue gv = delegator.findByPrimaryKey("DefaultPeriod", UtilMisc.toMap("periodIndex", pmPeriod));
				 request.setAttribute("periodName", gv.getString("periodName"));
			 } else {
				 request.removeAttribute("periodName");
			 }
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 查询flow
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String queryFlow(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 String jobIndex = request.getParameter("jobIndex");
		 String tempIndex = request.getParameter("tempIndex");
		 
		 try {
			 JobSupport support = JobSupport.getInstance();
			 Job job = null;
			 if(CommonUtil.isNotEmpty(tempIndex)) {
				 job = support.parseJobTemp(tempIndex, delegator, false);
				 job.setTempIndex(Long.valueOf(tempIndex).longValue());
				 request.setAttribute("jobIndex", String.valueOf(job.getJobIndex()));
			 } else {
				 job = support.parseJob(jobIndex, delegator, false);
			 }
			//如果登录人为质量保证部(dept_index == 10010)，则周期命名必须以MSA开头
             request.setAttribute("isMsaDept", AccountHelper.isMsaDept(request, delegator) ? "true" : "false");
			 //setToCache
			 CacheUtil cacheUtil = CacheUtil.getInstance();
			 String key = WorkflowHelper.getJobDefineCacheKey(request);
			 cacheUtil.setObjectToCache(key, CacheUtil.JOB_DEFINE_CACHE, job, true);
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 增加流程
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String addFlow(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 try {
			 //巡检只能有一个处理程序
			 if(Constants.PC_CHAR.equalsIgnoreCase(request.getParameter("eventType"))) {
				 List list = delegator.findByAnd("FlowJob", UtilMisc.toMap(
						 "eventObject", request.getParameter("eventObject"), "eventType", Constants.PC));
				 if(list.size() >= 1) {
					 request.setAttribute("_ERROR_MESSAGE_", "该巡检周期处理流程已存在，不能再新增！");
					 return "error";
				 }
				 //
				 list = delegator.findByAnd("FlowJobTemp", UtilMisc.toMap(
						 "eventObject", request.getParameter("eventObject"), "eventType", Constants.PC));
				 if(list.size() >= 1) {
					 request.setAttribute("_ERROR_MESSAGE_", "该巡检周期暂存未申请签核程序已存在，不能再新增！");
					 return "error";
				 }
			 }
			 Job job = new Job();
			 job.setNewFlag(true);
			 job.setActionlist(WorkflowHelper.initFlowActionList());
			//如果登录人为质量保证部(dept_index == 10010)，则周期命名必须以MSA开头
             request.setAttribute("isMsaDept", AccountHelper.isMsaDept(request, delegator) ? "true" : "false");
			 //setToCache
			 CacheUtil cacheUtil = CacheUtil.getInstance();
			 String key = WorkflowHelper.getJobDefineCacheKey(request);
			 cacheUtil.setObjectToCache(key, CacheUtil.JOB_DEFINE_CACHE, job, true);
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 增加动作
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String addFlowAction(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 String actionIndex = request.getParameter("actionIndex");
		 Map param = WorkflowEvent.getInitParams(request);
		 try {
			 Job job = WorkflowHelper.getJobDefineFromCache(request);
			 //set action to job
			 WorkflowHelper.setActionToJob(job, actionIndex, delegator);
			 //set pageParameter to job
			 WorkflowHelper.savePageInfoToJob(job, param);
			 //setToCache
			 CacheUtil cacheUtil = CacheUtil.getInstance();
			 String key = WorkflowHelper.getJobDefineCacheKey(request);
			 cacheUtil.setObjectToCache(key, CacheUtil.JOB_DEFINE_CACHE, job, true);
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 删除动作
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String removeFlowAction(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 Map param = WorkflowEvent.getInitParams(request);
		 String actionId = request.getParameter("actionId");
		 try {
			 Job job = WorkflowHelper.getJobDefineFromCache(request);
//			set pageParameter to job
			 WorkflowHelper.savePageInfoToJob(job, param);
//			 job.getActionlist().remove(job.getActionlist().size()-2);
			 
			 for(int i = Integer.parseInt(actionId)+1; i < job.getActionlist().size()-1; i++) {
				 Action action = job.queryAction(i);
				 action.setActionId(i-1);
				 WorkflowHelper.updateActionStatus(action);
			 }
			 
			 job.getActionlist().remove(Integer.parseInt(actionId));

			 //update end action
			 Action endAction = (Action)job.getActionlist().get(job.getActionlist().size()-1);
			 endAction.setActionId(job.getActionlist().size()-1);
			 //setToCache
			 CacheUtil cacheUtil = CacheUtil.getInstance();
			 String key = WorkflowHelper.getJobDefineCacheKey(request);
			 cacheUtil.setObjectToCache(key, CacheUtil.JOB_DEFINE_CACHE, job, true);
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 对ActionId重新排序
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String resortFlowAction(HttpServletRequest request,
				HttpServletResponse response) {
		 Map param = WorkflowEvent.getInitParams(request);
		 try {
			 Job job = WorkflowHelper.getJobDefineFromCache(request);
			 //set pageParameter to job
			 WorkflowHelper.savePageInfoToJob(job, param);
			 //reSort
			 Collections.sort(job.getActionlist(), new ActionComparator());
			 //TODO update action status
			 //setToCache
			 CacheUtil cacheUtil = CacheUtil.getInstance();
			 String key = WorkflowHelper.getJobDefineCacheKey(request);
			 cacheUtil.setObjectToCache(key, CacheUtil.JOB_DEFINE_CACHE, job, true);
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 复制job
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String copyJob(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		 String actionIndex = request.getParameter("actionIndex");
		 String actionName = request.getParameter("actionName");
		 
		 try {
			//Service Save Flow Action
		    Map result = dispatcher.runSync("copyFlowAction", UtilMisc.toMap("actionIndex" ,actionIndex, "userNo", CommonUtil.getUserNo(request), "actionName", actionName));
		    //throw exception
			if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
				throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
			}
		 } catch(Exception e) {
			 Debug.logError(module, e.getMessage());
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 
		 return "success";
	 }
	 
	 /**
	  * 当前步骤Action的上下移动
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String stepResortFlowAction(HttpServletRequest request,
				HttpServletResponse response) {
		 Map param = WorkflowEvent.getInitParams(request);
		 String step = request.getParameter("step");
		 try {
			 String[] steps = step.split(",");
			 String currentActionId = steps[0];
			 String changeActionId = steps[1];
			 
			 Job job = WorkflowHelper.getJobDefineFromCache(request);
			 
			 //set pageParameter to job
			 WorkflowHelper.savePageInfoToJob(job, param);
			 //changeAction
			 Action currentAction = job.queryAction(Integer.parseInt(currentActionId));
			 Action changeAction = job.queryAction(Integer.parseInt(changeActionId));
			 currentAction.setActionId(Integer.parseInt(changeActionId));
			 changeAction.setActionId(Integer.parseInt(currentActionId));
			 //updateActionStatus
			 WorkflowHelper.updateActionStatus(currentAction);
			 WorkflowHelper.updateActionStatus(changeAction);
			 //reSort
			 Collections.sort(job.getActionlist(), new ActionComparator());
			 //setToCache
			 CacheUtil cacheUtil = CacheUtil.getInstance();
			 String key = WorkflowHelper.getJobDefineCacheKey(request);
			 cacheUtil.setObjectToCache(key, CacheUtil.JOB_DEFINE_CACHE, job, true);
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 保存流程
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String saveFlow(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		 //String jobIndex = request.getParameter("jobIndex");
		 Map param = WorkflowEvent.getInitParams(request);
		 try {
			 String user = CommonUtil.getUserNo(request);
			 Job job = WorkflowHelper.getJobDefineFromCache(request);
			 if(job == null || CommonUtil.isEmpty(job.getActionlist()) || job.getActionlist().size() == 1) {
				 request.setAttribute("_ERROR_MESSAGE_", "流程为空请重新设定！");
				 return "error";
			 }
			 //保存页面信息
			 WorkflowHelper.savePageInfoToJob(job, param);
			 //调用Service进行保存
			 Map result = dispatcher.runSync("saveFlow", UtilMisc.toMap("user" ,user, "job", job));
		     //throw exception
			 if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			 	throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
			 }
			 
			 Job returnJob = (Job)result.get("returnJob");
			 request.setAttribute("jobIndex", new Long(returnJob.getJobIndex()));
			 //setToCache
			 CacheUtil cacheUtil = CacheUtil.getInstance();
			 String key = WorkflowHelper.getJobDefineCacheKey(request);
			 cacheUtil.setObjectToCache(key, CacheUtil.JOB_DEFINE_CACHE, returnJob, true);
			 
			 request.setAttribute("_EVENT_MESSAGE_", "保存成功！");
		 } catch (GenericServiceException e) {
	        	String message = CommonUtil.checkOracleException(e);
	        	request.setAttribute("_ERROR_MESSAGE_", message);
	        	return "error";
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 发送新增处理程序申请
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String submitInsertFlow(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 try {
			 String xml = Test.getXmlJob();
			 Long jobIndex = delegator.getNextSeqId("flowJobIndex");
			 GenericValue gv = delegator.makeValue("FlowJob", 
					 UtilMisc.toMap("jobIndex", jobIndex, "jobName", "每日检察温度","jobContent",xml));
			 delegator.create(gv);
		 } catch(Exception e) {
			 
		 }
		 return "success";
	 }
	 
	 /**
	  * 运行Job
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String runFlow(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 HttpSession session = request.getSession();
		 
		 Map paramMap = WorkflowEvent.getInitParams(request, true, true);
		 String jobRelationIndex = request.getParameter("jobRelationIndex");
		 GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		 //String nextActionId = request.getParameter("nextActionId");
		 //String currentActionId = request.getParameter("currentActionId");
		 
		 try {
			 String user = userLogin.getString("userLoginId");
			 paramMap.put("transBy", user);
			 Timestamp updateTime = new Timestamp(System.currentTimeMillis());
			 paramMap.put("updateTime", updateTime);
			 
			 JobEngine jobEngine = JobEngine.create();
			 jobEngine.setDelegator(delegator);
			 jobEngine.setJobRelationIndex(jobRelationIndex);
			 jobEngine.setSessionId(session.getId());
			 jobEngine.setActionStepInfo(paramMap);
			 jobEngine.setUserLoginId(userLogin);
			 
			 Map result = jobEngine.run();
			 Job job = (Job) result.get(JobEngine.JOB_KEY);
			 if(JobEngine.RUN_ERROR.equals(result.get(JobEngine.RUN_RESPONSE_MESSAGE))) {
				 String errorMsg = (String) result.get(JobEngine.RUN_ERROR_MESSAGE);
				 request.setAttribute("_ERROR_MESSAGE_", errorMsg);
				 //如果错误则返回当前Action
				 request.setAttribute("nextActionId", paramMap.get("currentActionId"));
			 }
			 
			 String pageValue = (String) result.get(JobEngine.RETURN_PAGE_KEY);
			
			 request.setAttribute("Job", job);
			 
			 return pageValue;
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
	 }
	 
	 /**
	  * 删除Job申请的副本
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String deleteFlowJobTemp(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 try {
			 String tempIndex = request.getParameter("tempIndex");
			 delegator.removeByAnd("FlowJobTemp", UtilMisc.toMap("tempIndex", tempIndex));
			 request.setAttribute("_EVENT_MESSAGE_","删除流程副本成功！");
		 } catch(Exception e) {
			 //Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 查看流程
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String viewFlowStructure(HttpServletRequest request,
				HttpServletResponse response) {		 
		 HttpSession session = request.getSession();
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		 
		 String submitFlag = request.getParameter("submitFlag");
		 String isPrint = request.getParameter("isPrint");		 
		 String periodName = request.getParameter("periodName");
		 
		 try {
			 Job job = null;
			 JobEngine jobEngine = JobEngine.create();
			 jobEngine.setDelegator(delegator);
			 jobEngine.setUserLoginId(userLogin);
			 //签核查看和查询流程时查看
			 if("true".equals(submitFlag)) {
				 String tempIndex = request.getParameter("tempIndex");
				 job = jobEngine.parseJobTemp(tempIndex, delegator, true);
			 } else {
				 String jobIndex = request.getParameter("jobIndex");
				 job = jobEngine.parseJob(jobIndex, delegator, true);
				 jobEngine.exchangeDcopFmtToJob(job);
			 }
			 
			 request.setAttribute("Job", job);
			 request.setAttribute("isPrint", isPrint);
			 request.setAttribute("periodName", periodName);
		 } catch(Exception e) {
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 查询历史流程
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String queryHistFlow(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 String jobIndex = request.getParameter("jobIndex");
		 String periodName = request.getParameter("periodName");
		 try {
			 List histJobs = delegator.findByAnd("FlowJobHist", UtilMisc.toMap("jobIndex", jobIndex), UtilMisc.toList("updateTime"));
			 
//			 List histJobs = null;
//			 JobEngine jobEngine = JobEngine.create();
//			 jobEngine.setDelegator(delegator);
//			 jobEngine.setUserLoginId(userLogin);
//			 
//			 histJobs = jobEngine.parsehistJobs(jobIndex, delegator, true);
			 
//			 for (int i = 0,size = histJobs.length; i < size; i++)
//			 {
//				 jobEngine.exchangeDcopFmtToJob(histJobs[i]);
//			 }
				 
			 request.setAttribute("histJobs", histJobs);
			 request.setAttribute("periodName", periodName);
			  } catch(Exception e) {
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 查看历史流程, ref viewFlowStructure
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String viewHistFlow(HttpServletRequest request, HttpServletResponse response) {		 
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 HttpSession session = request.getSession();
		 GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		 
		//String jobIndex = request.getParameter("jobIndex");
		 String histIndex = request.getParameter("histIndex");
		 String periodName = request.getParameter("periodName");
		 try {
			 Job job = null;
			 JobEngine jobEngine = JobEngine.create();
			 jobEngine.setDelegator(delegator);
			 jobEngine.setUserLoginId(userLogin);
			 //签核查看和查询流程时查看
			 job = jobEngine.parseHistJob(histIndex, delegator, true);
			 jobEngine.exchangeDcopFmtToJob(job);
			 
			 request.setAttribute("Job", job);
			 request.setAttribute("isPrint", "false");
			 request.setAttribute("periodName", periodName);
		 } catch(Exception e) {
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 查看流程数据
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String viewFlow(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 String jobRelationIndex = request.getParameter("jobRelationIndex");
		 try {
			 JobEngine jobEngine = JobEngine.create();
			 jobEngine.setDelegator(delegator);
			 jobEngine.setJobRelationIndex(jobRelationIndex);
			 Job job = jobEngine.getViewJobFromEntity();
			 
			 if(job == null) {
				 request.setAttribute("_ERROR_MESSAGE_", JobEngine.FLOW_ERROR_MESSAGE);
				 return "error";
			 }
			 request.setAttribute("Job", job);
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 修改流程数据
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String editFlow(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		 Map paramMap = WorkflowEvent.getInitParams(request);
		 try {
			 String user = CommonUtil.getUserNo(request);
			 Map result = dispatcher.runSync("saveFlowEditData", UtilMisc.toMap("user" ,user, "paramMap", paramMap));
			 //throw exception
			 if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			 	throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
			 }
			 request.setAttribute("_EVENT_MESSAGE_", "修改流程数据成功");
		 } catch(Exception e) {
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 发送流程签核
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String sendFlowSubmit(HttpServletRequest request,
			HttpServletResponse response) {
		 try {
			 //保存FlowTemp信息
			 String returnMsg = saveFlow(request, response);
			 if("error".equals(returnMsg)) {
				 return "error";
			 }
			 //getjobfrom cache
			 Job job = WorkflowHelper.getJobDefineFromCache(request);
			 request.setAttribute("submitObjectIndex", String.valueOf(job.getTempIndex()));
			 request.setAttribute("submitObjectName", job.getJobName());
			 //发送送签信息
			 returnMsg = sendSubmit(request, response);
			 if("error".equals(returnMsg)) {
				 return "error";
			 }
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 return "success";
	 }
	 
	 /**
	  * 发送设备主管签核
	  * Object(FLW, FAI, FUW)
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String sendSubmit(HttpServletRequest request, HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		 String object = request.getParameter("submitObject");
		 String objectIndex = request.getParameter("submitObjectIndex");
		 String objectName = request.getParameter("submitObjectName");
		 String type = request.getParameter("submitType");
		 
		 try {
			 if(CommonUtil.isEmpty(objectIndex) || "0".equals(objectIndex)) {
				 objectIndex = (String) request.getAttribute("submitObjectIndex");//为了获得流程的副本Index
			 }
			 if(CommonUtil.isEmpty(objectName)) {
				 objectName = (String) request.getAttribute("submitObjectName");//为了获得流程的副本Name
			 }			 		 
			 			 
			 if(WorkflowHelper.checkSubmit(delegator, objectIndex, object)) {
				 request.setAttribute("_ERROR_MESSAGE_", "该对象已有签核中，发送签核信息失败");
				 return "error";
			 }
			 
			 String user = CommonUtil.getUserNo(request);
			 Map result = WorkflowHelper.sendSubmit(dispatcher, object, objectIndex, objectName, type, user);
			 // 883609，huanghp，如果没有取到正常的主管，标示一下，2009-02
			 if ("NoLeader".equals(result.get(ModelService.ERROR_MESSAGE))) {
				 request.setAttribute("_ERROR_MESSAGE_", "找不到部门课长，发送签核信息失败，请联系系统负责人!");
			 } else {				 
				 request.setAttribute("_EVENT_MESSAGE_", "已经成功送达主管签核!");
			 }
			 
			 String sendTo = AccountHelper.getMailOfSectionLeaderByUserNo(delegator, user);
			 String sendCc = AccountHelper.getMailByAccountNo(delegator, user);
			 CommonUtil.sendMail(Constants.IT_SYSTEM_MAIL, sendTo, sendCc, "PMS - 签核流程 已启动: " + objectName, "请主管登录PM系统进行签核。");
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 
		 return "success";
	 }
	 
	 /**
	  * 发送工艺主管签核
	  * Object(FAI, FUW)
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String sendSubmitProcess(HttpServletRequest request, HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		 String object = request.getParameter("submitObject");
		 String objectIndex = request.getParameter("submitObjectIndex");
		 String objectName = request.getParameter("submitObjectName");
		 String type = request.getParameter("submitType");
		 String ownerProcess = request.getParameter("ownerProcess");
		 
		 try {
			 if(CommonUtil.isEmpty(objectIndex) || "0".equals(objectIndex)) {
				 objectIndex = (String) request.getAttribute("submitObjectIndex");//为了获得流程的副本Index
			 }
			 if(CommonUtil.isEmpty(objectName)) {
				 objectName = (String) request.getAttribute("submitObjectName");//为了获得流程的副本Name
			 }			 		 
			 
			 if(WorkflowHelper.checkSubmit(delegator, objectIndex, object)) {
				 request.setAttribute("_ERROR_MESSAGE_", "该对象已有签核中，发送签核信息失败");
				 return "error";
			 }
			 
			 String user = CommonUtil.getUserNo(request);
			 Map result = WorkflowHelper.sendSubmitToProcess(dispatcher, object, objectIndex, objectName, type, user, ownerProcess);
			 // 883609，huanghp，如果没有取到正常的主管，标示一下，2009-02
			 if ("NoLeader".equals(result.get(ModelService.ERROR_MESSAGE))) {
				 request.setAttribute("_ERROR_MESSAGE_", "找不到部门课长，发送签核信息失败，请联系系统负责人!");
			 } else {				 
				 request.setAttribute("_EVENT_MESSAGE_", "已经成功送达主管签核!");
			 }
			 
			 String sendTo = AccountHelper.getMailByAccountNo(delegator, ownerProcess);
			 String sendCc = AccountHelper.getMailByAccountNo(delegator, user);
			 CommonUtil.sendMail(Constants.IT_SYSTEM_MAIL, sendTo, sendCc, "PMS - 签核流程 已启动: " + objectName, "请主管登录PM系统进行签核。");
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 
		 return "success";
	 }
	 
	 /**
	  * 签核信息Demo
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String submit(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		 
		 String submitIndex = request.getParameter("submitIndex");
		 String status = request.getParameter("submitStatus");
		 try {
			 String user = CommonUtil.getUserNo(request);
			 WorkflowHelper.submit(dispatcher, delegator, user, status, submitIndex, "");
			 request.setAttribute("_EVENT_MESSAGE_", "签核成功！");
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 
		 return "success";
	 }
	 
	/**
	  * 主管签核入口
	  * @param request
	  * @param response
	  * @return
	  */
	public static String queryFlowLeadCheck(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try {
		 	String user = CommonUtil.getUserNo(request);
		 	//SQL语句
			String strSQL = "";
			strSQL += "SELECT s.object object";
			strSQL += ",nvl(o.object_description,'') object_description";
			strSQL += ",nvl(s.object_name,'') object_name";
			strSQL += ",nvl(s.type,'') type";
			strSQL += ",s.object_index object_index";
			strSQL += ",nvl(s.create_time,sysdate) create_time, creator_name";
			strSQL += ",nvl(s.submit_index,'') submit_index";
			strSQL += " FROM wf_submit s";
			strSQL += " LEFT OUTER JOIN wf_submit_object o";
			strSQL += " ON s.object = o.object";
			strSQL += " WHERE s.owner = '" + user + "' and s.status = 'SUBMITED'";
			strSQL += "    and (s.status_process in ('SUBMITED','APPROVED') or s.status_process is null)";
			strSQL += " or s.owner_process = '" + user + "' and s.status_process = 'SUBMITED'";
			strSQL += "    and (s.status in ('SUBMITED','APPROVED') or s.status is null)";
			strSQL += " ORDER BY s.create_time DESC";
			
			List list = SQLProcess.excuteSQLQuery(strSQL, delegator);
			if (list.size() > 0){
				request.setAttribute("flowList", list);
				request.setAttribute("flag", "OK");
			}
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	 
	/**
	  * 申请人查询送签信息入口
	  * @param request
	  * @param response
	  * @return
	  */
	public static String queryFlowIssueInfo(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try {
		 	String user = CommonUtil.getUserNo(request);
		 	//SQL语句
			String strSQL = null;
			strSQL = "SELECT o.object_description,s.object_name, s.object,s.type,s.create_time,s.update_time,"
				+ " s.status, nvl(s.status_process, '') status_process, object_index,flow_job_hist_index,"
				+ " submit_index, a1.account_name owner,a2.account_name owner_process"
				+ " FROM wf_submit s,wf_submit_object o,account a1,account a2"
				+ " WHERE s.object = o.object and s.owner=a1.account_no(+) and s.owner_process=a2.account_no(+)" 
				+ " and s.creator = '" + user + "'"
				+ " ORDER BY s.create_time DESC";
			
			List list = SQLProcess.excuteSQLQuery(strSQL, delegator);
			if (list.size() > 0){
				request.setAttribute("flowList", list);
				request.setAttribute("flag", "OK");
			}
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	/**
     * 查看动作项目流程变更
     * @param request
     * @param response
     * @return
     */
   public static String viewFlowActionItemApply(HttpServletRequest request, HttpServletResponse response) {    
       GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
       String isTemp = request.getParameter("isTemp");
       String objectIndex = request.getParameter("objectIndex");
       String submitIndex = request.getParameter("submitIndex");
        
       try {             
           //查询动作项目
           String table = "FlowActionItemTemp";
           if("N".equals(isTemp)){
               table = "FlowActionItemHist";
           }
           
           List itemList = delegator.findByAnd(table, UtilMisc.toMap("flowActionItemTempIndex", objectIndex), UtilMisc.toList("itemOrder"));
           request.setAttribute("itemList", itemList);
           
           if (CommonUtil.isNotEmpty(itemList)) {
        	   GenericValue itemGv = (GenericValue) itemList.get(0);
        	   GenericValue actionGv = delegator.findByPrimaryKey("FlowAction", UtilMisc.toMap("actionIndex", itemGv.getLong("actionIndex")));
        	   request.setAttribute("actionGv", actionGv);
           }
           
           List wscList = delegator.findByAnd("WfSubmitControl", UtilMisc.toMap("submitIndex", submitIndex), UtilMisc.toList("controlIndex"));
           request.setAttribute("wscList", wscList);
       } catch(Exception e) {
           Debug.logError(e.getMessage(), module);
           request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
           return "error";
       }
        
       return "success";
   }
	 	
	/**
	  * 批量送签
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String flowLeadCheckBak(HttpServletRequest request, HttpServletResponse response) {
			GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
	        LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
	        String submitList = request.getParameter("submitList");
	        String status = request.getParameter("status");
	        String comment = request.getParameter("comment");
	        Map hashMap = new HashMap();
	        hashMap.put("submitList", submitList);
	        hashMap.put("status", status.toUpperCase());
	        try {
	        	String user = CommonUtil.getUserNo(request);
	        	WorkflowHelper.submit(dispatcher, delegator, user, status, submitList, comment);
	            request.setAttribute("_EVENT_MESSAGE_", "签核成功！");
	        } catch (Exception e) {
	            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	            Debug.logError(e, module);
	            return "error";
	        }
	        return "success";
	 }
	 
	/**
     * 根据主键查询签核细项
     * 
     * @param request
     * @param response
     */
    public static void queryFlowLeadCheckNo(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String id = request.getParameter("submitIndex");
        try {
            GenericValue gv = delegator.findByPrimaryKey("EventSubcategory", UtilMisc.toMap("eventSubIndex", id));
            JSONObject eventSubCateGory = new JSONObject();
            eventSubCateGory.put("subCategory", gv.getString("subCategory"));
            eventSubCateGory.put("desc", gv.getString("description"));
            response.getWriter().write(eventSubCateGory.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }
	    	    
    /**
     * 主管签核:单个签核 或 批量签核
     * @param request
     * @param response
     * @return
     */
    public static String queryFlowLeadCheckSubmit(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
        String submitIndex = request.getParameter("submitIndex");
        String status = request.getParameter("status");
        String comment = request.getParameter("comment");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        Map hashMap = new HashMap();
        hashMap.put("submitIndex", submitIndex);
        hashMap.put("status", status);
        
        try {
        	String user = CommonUtil.getUserNo(request);
        	String[] submitList = submitIndex.split(",");
        	for (int i = 0; i <= submitList.length-1; i++){
        		//签核
        	    String submitResult = WorkflowHelper.submit(dispatcher, delegator, user, status, submitList[i], comment);
        		String object_index = (String) StringUtil.split(submitResult, ";").get(0);
        		String object = (String) StringUtil.split(submitResult, ";").get(1);
        		String flow_job_hist_index = (String) StringUtil.split(submitResult, ";").get(2);	        		
        		
        		if (Constants.APPROVED.equals(status)) {
        			if (Constants.SUBMIT_FLOW.equals(object)) {
	                    JobEngine jobEngine = JobEngine.create();
	                    jobEngine.setDelegator(delegator);
	                    jobEngine.setUserLoginId(userLogin);
	                    
	                    //签核查看和查询流程时查看
	                    Job job = null;
	                    //job = jobEngine.parseJobTemp(object_index, delegator, true);//拒绝后查看temp
	                    job = jobEngine.parseHistJob(flow_job_hist_index, delegator, true);//同意后查看hist                    
	                    StringBuffer mailContent = WorkflowHelper.getFlowMailContent(job);
	                    
		                //send mail to IPQA的路乐乐和郭蕾，cc 申请人或登录用户、课长、经理
	                    String sendCc = AccountHelper.getMailOfUserAndLeaders(request, delegator, job.getTransBy());                    
		                CommonUtil.sendMail(Constants.IT_SYSTEM_MAIL, Constants.QC_MAIL, sendCc, "PMS - 流程设定变更 APPROVED: " + job.getJobName(), mailContent.toString());
	        		} else if (Constants.SUBMIT_FLOW_ACTION_ITEM.equals(object)) {
	        			GenericValue histGv = CommonUtil.findFirstRecordByAnd(delegator, "FlowActionItemHist", UtilMisc.toMap("flowActionItemTempIndex", object_index));
	        			if (histGv != null) {
	        				//存在历史记录，则动作项目审批已完成，发送邮件通知
	        				String mailContent = WorkflowHelper.getFlowActionItemMailContent(delegator, histGv);
	        				String sendCc = AccountHelper.getMailOfUserAndLeaders(request, delegator, histGv.getString("transBy"));
			                CommonUtil.sendMail(Constants.IT_SYSTEM_MAIL, Constants.QC_MAIL, sendCc, "PMS - 动作项目变更 APPROVED: " + histGv.getString("itemName"), mailContent);
	        			}
        			} else if (Constants.SUBMIT_PM_DELAY.equals(object)) {
        				GenericValue gv = delegator.findByPrimaryKey("WfSubmit", UtilMisc.toMap("submitIndex", submitList[i]));
        				String creator = gv.getString("creator");
        				String owner = gv.getString("owner");
        				String statusEqp = gv.getString("status");
        				String ownerProcess = gv.getString("ownerProcess");
        				String statusProcess = gv.getString("statusProcess");
        				String objectName = gv.getString("objectName");
        				
        				if (Constants.APPROVED.equals(statusEqp) && Constants.APPROVED.equals(statusProcess)) {
	        				String sendTo = AccountHelper.getMailByAccountNo(delegator, creator);
	        				String sendCc = AccountHelper.getMailByAccountNo(delegator, owner)
	        					+ AccountHelper.getMailByAccountNo(delegator, ownerProcess);	        			
		        			CommonUtil.sendMail(Constants.IT_SYSTEM_MAIL, sendTo, sendCc, "PMS - 保养计划延期申请 APPROVED", objectName);
        				}
        			}
        		}//end if approved	        		
        	}
            request.setAttribute("_EVENT_MESSAGE_", "签核成功！");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

	/**
     * 主管签核：问题跟踪查询
     * @param request
     * @param response
     * @return
     */
    public static String queryLeadCheckNo(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String submitIndex = request.getParameter("submitIndex");
        String strSQL = "";
        
        try {
        	strSQL += "SELECT t1.object_type,t1.object,t1.follow_name,t1.purpose,t1.creator,t1.follow_index";
        	strSQL += " FROM wf_submit t";
        	strSQL += " LEFT OUTER JOIN follow_job t1";
        	strSQL += " ON t1.follow_index = t.object_index";
        	strSQL += " WHERE t.submit_index = " + submitIndex;
        	List list = SQLProcess.excuteSQLQuery(strSQL, delegator);
        	request.setAttribute("ListFollow",list);

        	String user = CommonUtil.getUserNo(request);
        	List listUser = PartsHelper.getAccountSection(delegator,user);
        	GenericValue gv = (GenericValue)listUser.get(0);
        	request.setAttribute("SECTION", gv.getString("accountSection"));
        	request.setAttribute("DEPT", gv.getString("accountDept"));        	
        	
        	if ((list != null) && (list.size()!= 0)) {
        		Map map = (Map) list.get(0);
        		String followIndex = UtilFormatOut.checkNull((String)(map.get("FOLLOW_INDEX")));
        		
        		String url=request.getContextPath()+"/control/saveFile";
        		List itemList = QuFollowHelper.queryQuItemList(delegator, followIndex, url);
        		request.setAttribute("ITEM_LIST", itemList);
        	}    	
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }
}
