
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
     * 如果时间一样，就是重测点。删除违反rule记录，重测
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
     * 触发警报service
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
            Debug.logError("Severity 必须为1或者0！", WorkflowService.module);
            map.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            map.put(ModelService.ERROR_MESSAGE, "Severity 必须为1或者0！");
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
                    Debug.logError("priority[" + priorityStr + "] " + opt + "后必须为有效的数字!",
                            WorkflowService.module);
                    map.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    map.put(ModelService.ERROR_MESSAGE, "priority[" + priorityStr + "] " + opt
                            + "后必须为有效的数字!");
                    return map;
                }
            } else if (priorityStr.startsWith(">") || priorityStr.startsWith("<")) {
                String pStr = priorityStr.substring(1);
                String opt = priorityStr.substring(0, 1);
                try {
                    Integer.parseInt(pStr.trim());
                } catch (NumberFormatException e1) {
                    Debug.logError("priority[" + priorityStr + "] " + opt + "后必须为有效的数字!",
                            WorkflowService.module);
                    map.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    map.put(ModelService.ERROR_MESSAGE, "priority[" + priorityStr + "] " + opt
                            + "后必须为有效的数字!");
                    return map;
                }
            } else {
                try {
                    Integer.parseInt(priorityStr.trim());
                } catch (NumberFormatException e1) {
                    Debug.logError("priority[" + priorityStr + "]" + "后必须为有效的数字!",
                            WorkflowService.module);
                    map.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                    map.put(ModelService.ERROR_MESSAGE, "priority[" + priorityStr + "] 必须为有效的数字!");
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
            map.put(ModelService.ERROR_MESSAGE, "数据库错误，请再试一次！");
        } catch (SQLProcessException e) {
            Debug.logError(e.getMessage(), WorkflowService.module);
            map.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            map.put(ModelService.ERROR_MESSAGE, "数据库错误，请再试一次！");
        }
        return map;
    }

}
