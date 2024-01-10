
package com.csmc.pms.webapp.eqp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.eqp.helper.GenDCOPHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.workflow.service.WorkflowService;

/**
 * GenDCOPService.java Create on 2011-7-26 Copyright (c) 2011-7-26 www.csmc.com
 * Inc. All rights reserved.
 * 
 * @author qinchao
 * @version 1.0
 */
public class GenDCOPService {

    public static final String module = GenDCOPService.class.getName();

    /**
     * ���ʱ��һ���������ز�㡣ɾ��Υ��rule��¼���ز�
     * 
     * @param ctx
     * @param context
     * @return Create on 2011-7-13 Update on 2011-7-13
     */
    public static Map deleteViolateRulePoint(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        Map result = new HashMap();
        String dcopId = (String)context.get("dcopId");
        String chartId = (String)context.get("chartId");
        java.sql.Timestamp revdate = (java.sql.Timestamp)context.get("revdate");
        String collectionLevel = (String)context.get("collectionLevel");
        String lotId = (String)context.get("lotId");
        String waferId = (String)context.get("waferId");
        Map findMap = new HashMap();
        findMap.put("testOpno", dcopId);
        findMap.put("chartId", chartId);
        findMap.put("dcopDate", revdate);
        findMap.put("collectionLevel", collectionLevel);
        findMap.put("lotId", lotId);
        if (collectionLevel != null && collectionLevel.equalsIgnoreCase("WAFER")) {
            findMap.put("waferId", waferId);
        }
        try {
            List ruleValue = delegator.findByAnd("ViolateRulePoint", findMap);
            if (ruleValue != null && ruleValue.size() > 0) {
                delegator.removeAll(ruleValue);
            }
            return result;
        } catch (Exception e) {
            Debug.logError(e, WorkflowService.module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
            return result;
        }
    }

    /**
     * ��������service
     * 
     * @param ctx
     * @param context
     * @return Create on 2011-7-26 Update on 2011-7-26
     */
    public static Map triggerAlarmByRule(DispatchContext ctx, Map context) {
        Map map = new HashMap();
        GenericDelegator delegator = CommonUtil.getGuiDelegator();
        String objectType = (String)context.get("objectType");
        String alarmObjectId = (String)context.get("objectId");
        String trancComment = (String)context.get("trancComment");
        Map alarmDataMap = (Map)context.get("alarmDataMap");
        String severity = (String)context.get("severity");
        if (!(severity == null || "0".equals(severity) || "1".equals(severity))) {
            Debug.logError("Severity ����Ϊ1����0��", WorkflowService.module);
            map.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            map.put(ModelService.ERROR_MESSAGE, "Severity ����Ϊ1����0��");
            return map;
        }
        String priorityStr = (String)context.get("priority");
        if (priorityStr != null) {
            if (priorityStr.indexOf("=") != -1) {
                String pStr = priorityStr.substring(priorityStr.indexOf("=") + 1);
                String opt = priorityStr.substring(0, priorityStr.indexOf("=") + 1);
                try {
                    Integer.parseInt(pStr.trim());
                } catch (NumberFormatException e1) {
                    Debug.logError("priority[" + priorityStr + "] " + opt + "�����Ϊ��Ч������!",
                            WorkflowService.module);
                    map.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    map.put(ModelService.ERROR_MESSAGE, "priority[" + priorityStr + "] " + opt
                            + "�����Ϊ��Ч������!");
                    return map;
                }
            } else if (priorityStr.startsWith(">") || priorityStr.startsWith("<")) {
                String pStr = priorityStr.substring(1);
                String opt = priorityStr.substring(0, 1);
                try {
                    Integer.parseInt(pStr.trim());
                } catch (NumberFormatException e1) {
                    Debug.logError("priority[" + priorityStr + "] " + opt + "�����Ϊ��Ч������!",
                            WorkflowService.module);
                    map.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    map.put(ModelService.ERROR_MESSAGE, "priority[" + priorityStr + "] " + opt
                            + "�����Ϊ��Ч������!");
                    return map;
                }
            } else {
                try {
                    Integer.parseInt(priorityStr.trim());
                } catch (NumberFormatException e1) {
                    Debug.logError("priority[" + priorityStr + "]" + "�����Ϊ��Ч������!",
                            WorkflowService.module);
                    map.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    map.put(ModelService.ERROR_MESSAGE, "priority[" + priorityStr + "] ����Ϊ��Ч������!");
                    return map;
                }
                priorityStr = "=" + priorityStr.trim();
            }
        }
        if (alarmDataMap == null) {
            alarmDataMap = new HashMap();
        }
        try {
            GenDCOPHelper.triggerAlarmByRule(delegator, objectType, alarmObjectId, trancComment,
                    alarmDataMap, severity, priorityStr);
        } catch (GenericEntityException e) {
            Debug.logError(e.getMessage(), WorkflowService.module);
            map.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            map.put(ModelService.ERROR_MESSAGE, "���ݿ����������һ�Σ�");
        } catch (SQLProcessException e) {
            Debug.logError(e.getMessage(), WorkflowService.module);
            map.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            map.put(ModelService.ERROR_MESSAGE, "���ݿ����������һ�Σ�");
        }
        return map;
    }

}
