package com.csmc.mcs.webapp.cabinet.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityWhereString;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.csmc.mcs.webapp.cabinet.helper.CabinetHelper;
import com.csmc.mcs.webapp.use.helper.UseHelper;
import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.parts.help.PartsHelper;
import com.csmc.pms.webapp.util.CommonUtil;

/**
 * �� UseService.java 
 * @version  1.0  2009-11-25
 * @author   dinghh
 */
public class CabinetService {
	
	public static final String module = CabinetService.class.getName();
	
	/**
	 * �����ݴ�(������)
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map intoCabinetByQty(DispatchContext ctx, Map context) {
		Map result = new HashMap();
		GenericDelegator delegator = ctx.getDelegator();		
		
		String userNo = (String) context.get("userNo");
		String stoReqSelectStr = (String) context.get("stoReqSelectStr");
		HashMap useQtyMap = (HashMap) context.get("useQtyMap");
		
		try {
			String whereString = "active_flag='" + ConstantsMcs.N + "' and material_sto_req_index in (" + stoReqSelectStr + ")";			
			EntityWhereString con = new EntityWhereString(whereString);
			List stoReqList = delegator.findByCondition("McsMaterialStoReq", con, null, null);
			
			// transaction process
			String returnMsg = CabinetHelper.intoCabinetQtyProcess(delegator, userNo, stoReqList, useQtyMap);	        
			result.put("returnMsg", returnMsg);
			
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * �����ݴ棺��ѧƷʹ�ò�����USE_AND_FINISH�����ļ�ֱ��ʹ��USING
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map useMaterialByCabinet(DispatchContext ctx, Map context) {
		Map result = new HashMap();
		GenericDelegator delegator = ctx.getDelegator();		
		HashMap paramMap = (HashMap) context.get("paramMap");
		
		String userNo = (String) paramMap.get("userNo");
		String stoReqSelectStr = (String) paramMap.get("stoReqSelectStr");
		HashMap useQtyMap = (HashMap) paramMap.get("useQtyMap");
		
		String eqpId = (String) paramMap.get("eqpId");
		String useNote = (String) paramMap.get("useNote");
		String newStatus = (String) paramMap.get("newStatus");
		
		try {
			String whereString = "active_flag='" + ConstantsMcs.N + "' and material_sto_req_index in (" + stoReqSelectStr + ")";			
			EntityWhereString con = new EntityWhereString(whereString);
			List stoReqList = delegator.findByCondition("McsMaterialStoReq", con, null, null);
			
			// transaction process
			String returnMsg = CabinetHelper.intoCabinetQtyUse(delegator, userNo, stoReqList, useQtyMap, eqpId, useNote, newStatus);	        
			result.put("returnMsg", returnMsg);
			
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * MCS�ְ߲߱��ϺŻ��� �������ļ�
	 *   ִ�в����� 1���п��ֱ��ʹ�ã�2���޿���Զ����ϲ�ʹ�ã�3����ʹ�������Զ������豸���ã���������ʱ�ۼ���0������
	 *   ���������̲�ѯ���������趨��ִ�У� 1��2��3
	 *   ����������ϺŲ�ѯ�߲֣߱�ִ�У� 1��3
	 * PMS��д��ʱ��ʹ������� eventType�ǿգ�����PartsUse
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map useSparePartBySto(DispatchContext ctx, Map context) {
		Map result = new HashMap();
		String returnMsg = "";
		GenericDelegator delegator = ctx.getDelegator();
		HashMap paramMap = (HashMap) context.get("paramMap");
		
		String userNo = (String) paramMap.get("userNo");
		String[] mtrNumSelectArray = (String[]) paramMap.get("mtrNumSelectArray");
		String[] useQtyArray = (String[]) paramMap.get("useQtyArray");		
		String eqpId = (String) paramMap.get("eqpId");
		String note = (String) paramMap.get("note");
		
		String eventType = (String) paramMap.get("eventType");
		//String eventIndex = (String) paramMap.get("eventIndex");
		//String flowIndex = (String) paramMap.get("flowIndex");
		
		try {
			GenericValue accountGv = AccountHelper.getAccountByNo(delegator, userNo);
			String deptIndex = accountGv.getString("deptIndex");
			
			Timestamp nowTs = UtilDateTime.nowTimestamp();
			Long transactionId = delegator.getNextSeqId(ConstantsMcs.TRANSACTION_ID);
			for (int i = 0; i < mtrNumSelectArray.length; i++) {
				String mtrNum = mtrNumSelectArray[i];
				String useQty = useQtyArray[i];
				long lUseQty = Long.parseLong(useQty);
				
				GenericValue materialInfo = CommonUtil.findFirstRecordByAnd(
						delegator, "McsMaterialInfo", 
						UtilMisc.toMap("mtrNum", mtrNum, "deptIndex", deptIndex));
				if (materialInfo == null) {
					throw new GenericServiceException(mtrNum + " [Error: ����ά�� MCS���ϻ�����Ϣ��]");
				}
				
				// 1.mcs�������ļ�
				String msg = CabinetHelper.useSparePartBySto(delegator, userNo, mtrNum, lUseQty, eqpId, "�������ļ�:" + note, transactionId, nowTs);
				returnMsg = returnMsg + msg + "\n";
				
				// 2.pms form interface: pm/ts using parts, save to PartsUse			
				if (StringUtils.isNotEmpty(eventType)) {
					paramMap.put("mtrGrp", materialInfo.getString("mtrGrp"));
					paramMap.put("deptIndex", deptIndex);
					Long partsUseSeqIndex = UseHelper.savePartsUseByMcs(
							delegator, paramMap, transactionId, nowTs, 
							useQty, mtrNum, materialInfo.getString("mtrDesc"));
				}
			}
			
			result.put("returnMsg", returnMsg);			
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * ���汸��ʹ�ü�¼
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map savePartsUse(DispatchContext ctx, Map context) {
		Map result = new HashMap();
		String returnMsg = "";
		GenericDelegator delegator = ctx.getDelegator();
		HashMap paramMap = (HashMap) context.get("paramMap");
		
		try {
			String partNo = (String) paramMap.get("partNo");
			String userNo = (String) paramMap.get("userNo");

			List partInfoList = delegator.findByAnd("McsMaterialInfo", UtilMisc.toMap("mtrNum", partNo));
			if (partInfoList == null || partInfoList.isEmpty()) {
				returnMsg = "������Ϣδά����";
			}
			else {
			
				GenericValue partInfo = (GenericValue) partInfoList.get(0);
				paramMap.put("partType", "NEW");
				paramMap.put("partsRequireIndex", (String) paramMap.get("reqIndex"));
				paramMap.put("transBy", userNo);
				paramMap.put("partName", partInfo.getString("mtrDesc"));
				paramMap.put("mtrGrp", partInfo.getString("mtrGrp"));
	
				paramMap.put("createUser", userNo);
				paramMap.put("initLife", 0);
				paramMap.put("delaytime", 0);
				
				PartsHelper.savePartsUsePatch(delegator, paramMap);
			}
			String reqIndex = (String) paramMap.get("reqIndex");
			String whereString = "active_flag='" + ConstantsMcs.N + "' and material_sto_req_index in (" + reqIndex + ")";			
			EntityWhereString con = new EntityWhereString(whereString);
			List stoReqList = delegator.findByCondition("McsMaterialStoReq", con, null, null);
			
			// transaction process
			String eqpId = (String) paramMap.get("eqpId");
			HashMap useQtyMap = new HashMap();
			useQtyMap.put(reqIndex, paramMap.get("partCount"));
			
			CabinetHelper.intoCabinetQtyUse(delegator, userNo, stoReqList, useQtyMap, eqpId, "PATCH", ConstantsMcs.USING);
			result.put("returnMsg", returnMsg);
			
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
}
