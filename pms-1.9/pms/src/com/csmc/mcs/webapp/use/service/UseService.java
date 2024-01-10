package com.csmc.mcs.webapp.use.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.csmc.mcs.webapp.use.helper.UseHelper;
import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.util.CommonUtil;

/**
 * 类 UseService.java 
 * @version  1.0  2009-9-15
 * @author   dinghh
 */
public class UseService {
	
	public static final String module = UseService.class.getName();
	
	/**
	 * 校验并修改物料状态 By Id
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map changeMaterialStatusById(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		
		Map result = new HashMap();
		Map paramMap = (Map) context.get("paramMap");
		
		try {
			// 校验物料状态
			Map checkResult = UseHelper.checkMaterialStatusById(delegator, paramMap);
			String errorMsg = (String) result.get("errorMsg");
			
			if (StringUtils.isNotEmpty(errorMsg)) {
				result.put("returnMap", checkResult);
			} else {
				// 修改
				Map changeResult = UseHelper.changeMaterialStatusById(delegator, paramMap);
				result.put("returnMap", changeResult);
			}
			
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}

	/**
	 * 校验并修改物料状态 By Qty
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map changeMaterialStatusByQty(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		
		Map result = new HashMap();
		Map paramMap = (Map) context.get("paramMap");
		
		try {
			Map returnMap = UseHelper.changeMaterialStatusByQty(delegator, paramMap);
			result.put("returnMap", returnMap);
			
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	//补填使用记录
	public static Map manageUsingHistory(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		
		Map result = new HashMap();
		Map usingMap = (Map) context.get("usingMap");
		
		try {			
			long minStoIndex = UseHelper.getMinStoIndex(delegator);
			Map troMap = new HashMap();
			troMap.put("materialStoReqIndex", new Long(minStoIndex));
						
			Long transactionId = delegator.getNextSeqId(ConstantsMcs.TRANSACTION_ID);
			GenericValue materialInfo = CommonUtil.findFirstRecordByAnd(
					delegator, "McsMaterialInfo", 
					UtilMisc.toMap("mtrNum", usingMap.get("mtrNum"), "deptIndex", usingMap.get("deptIndex")));
			
			troMap.put("plant", materialInfo.getString("plant"));
			troMap.put("mtrNum", usingMap.get("mtrNum"));
			troMap.put("mtrDesc", materialInfo.getString("mtrDesc"));
			troMap.put("mtrGrp", usingMap.get("mtrGrp"));
			troMap.put("batchNum", "0");
			troMap.put("vendorBatchNum", "0");
			troMap.put("qty", new Long(1));
			troMap.put("shelfLifeExpirationDate", usingMap.get("shelfLifeExpirationDate"));
			troMap.put("docTime", usingMap.get("updateTime"));
			troMap.put("costCenter", usingMap.get("accountDept"));
			troMap.put("equipmentId", usingMap.get("usingObjectId"));
			troMap.put("reasonForMovement", "补填");
			troMap.put("deptIndex", usingMap.get("deptIndex"));
			troMap.put("updateTime", usingMap.get("updateTime"));
			troMap.put("activeFlag", "Y");
			troMap.put("activeTime", usingMap.get("updateTime"));
			troMap.put("activeQty", new Long(1));
			troMap.put("recipient", usingMap.get("transBy"));
			GenericValue stoSeqGv = delegator.makeValidValue("McsMaterialStoReq", troMap);
			delegator.createOrStore(stoSeqGv);
			
			String materialIndex = materialInfo.getString("materialIndex");
			Long mtrStatusId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_INDEX);
			usingMap.put(ConstantsMcs.MATERIAL_STATUS_INDEX, mtrStatusId);
			usingMap.put("materialIndex", materialIndex);
			usingMap.put("materialStoReqIndex", new Long(minStoIndex));
			usingMap.put("docTime", usingMap.get("updateTime"));
			usingMap.put("mtrDesc", materialInfo.getString("mtrDesc"));
			usingMap.put("mtrGrp", usingMap.get("mtrGrp"));
			usingMap.put("status", ConstantsMcs.USING);
			usingMap.put(ConstantsMcs.TRANSACTION_ID, transactionId);
			usingMap.put("vendorBatchNum", "0");			
			GenericValue usingGv = delegator.makeValidValue("McsMaterialStatus", usingMap);
			delegator.createOrStore(usingGv);
			
			Long mtrStatusHistId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX);
			usingMap.put(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX, mtrStatusHistId);
			usingMap.put("note", "补填");
			GenericValue usingHistGv = delegator.makeValidValue("McsMaterialStatusHist", usingMap);
			delegator.createOrStore(usingHistGv);
			
			result.put("returnMsg", "OK");
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
}
