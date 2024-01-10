package com.csmc.pms.webapp.form.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.form.help.PcHelper;
import com.csmc.pms.webapp.util.Constants;

public class PcFormService {
	public static final String module = PcFormService.class.getName();
	
	/**
	 * 保存动作，记录历史
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map setupPcForm(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		
		Map paramMap = (Map) context.get("param");
		String userNo = (String) context.get("userNo");
		
		Map result = new HashMap();
		try {            
            List flowJobList = delegator.findByAnd("FlowJob", UtilMisc.toMap("eventType", Constants.PC, "eventObject", (String)paramMap.get("periodIndex")));
            
            if (flowJobList.size() > 0) {
            	// 新增PcForm
    			Long pcIndex = PcHelper.createPcForm(delegator, paramMap);
            	
            	GenericValue flowJobGv = (GenericValue) flowJobList.get(0);
            	
            	Map formJobRelationMap = new HashMap();
            	formJobRelationMap.put("jobIndex", flowJobGv.getString("jobIndex"));
                formJobRelationMap.put("jobContent", flowJobGv.getString("jobContent"));
                formJobRelationMap.put("jobStatus", new Integer(Constants.JOB_START));
                formJobRelationMap.put("eventType", Constants.PC);
                formJobRelationMap.put("eventIndex", pcIndex);
                formJobRelationMap.put("creator", userNo);
                formJobRelationMap.put("nextActionId", new Integer(Constants.JOB_START));
                formJobRelationMap.put("jobName", flowJobGv.getString("jobName"));
                
                // 新建FormJobRelation
                PcHelper.createFormJobRelation(delegator, formJobRelationMap);
                
                //更新PeriodSchedule
                PcHelper.updatePeriodSchedule(delegator, UtilMisc.toMap("scheduleIndex",paramMap.get("scheduleIndex"),"eventIndex",pcIndex));
            } else {
            	result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
    			result.put(ModelService.ERROR_MESSAGE, "此巡检样式周期未绑定流程，请联系管理员");
            }
		} catch(Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
}
