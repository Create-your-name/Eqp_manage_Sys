package com.csmc.pms.webapp.eqp.helper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;

/*
 * @author dinghh
 * dcop相关代码也用到guidb(getGuiDelegator)，仅fab1使用，未移出的类：GenDCOPService, WorkflowHelper, InitialServlet
 */
public class GuiHelper {
	public static final String module = GuiHelper.class.getName();

	//added by steven
	public static void triggerQc(String eqpId, String eqpStatus, String newstatus, GenericValue userLogin) throws Exception {
		if (Constants.CALL_ASURA_FLAG) return;
		
		GenericDelegator delegatorGui = CommonUtil.getGuiDelegator();
		List qcStatus = delegatorGui.findByAnd("EqpQcStatus",
				UtilMisc.toMap("equipmentId", eqpId, "qcFactor", "EQPSTATUS_CHANGE"));
		if (qcStatus!=null&&qcStatus.size()>0) {
			for (Iterator it = qcStatus.iterator(); it.hasNext();) {
				GenericValue qc = (GenericValue) it.next();
				String lastMenugroup = (String) qc.get("lastMenugroup");
				if (lastMenugroup!=null&&lastMenugroup.indexOf("&comm&")!=-1&&lastMenugroup.indexOf("@@@")!=-1) {
			    	String[] eqpstatus = lastMenugroup.split("&comm&");
			    	for(int i = 0; i < eqpstatus.length; i++) {
			    		String[] statuses = eqpstatus[i].split("@@@");
			    		if (eqpStatus.equalsIgnoreCase(statuses[0])&&newstatus.equalsIgnoreCase(statuses[1])) {
			    			qc.set("qcStatus", "OPEN");
							qc.set("currMenugroup", eqpStatus + "@@@" + newstatus);
							GenericValue qcHist = GuiHelper.getRecurrQcHist((Map)qc,delegatorGui,userLogin);
							qc.store();
							delegatorGui.createOrStore(qcHist);
							break;
			    		}					    		 
			    	}
				}												
			}
		}
	}

	private static GenericValue getRecurrQcHist(Map map,
			GenericDelegator delegator, GenericValue userLogin)
			throws Exception {
		GenericValue gv = delegator.makeValidValue("EqpQcHist", map);
		gv.set("qcTransDate", UtilDateTime.nowTimestamp());
		gv.set("qcTransBy", userLogin.getString("userLoginId"));
		return gv;
	}
	//end
	
	//保养或异常正常结束后，按设备重置GUI减频系统
	public static void resetReduceRatioQty(String eqpId)
			throws SQLProcessException, GenericEntityException {
		if (Constants.CALL_ASURA_FLAG) return;
		
		GenericDelegator delegatorGui = CommonUtil.getGuiDelegator();
		String sql = "select frequency_id,attribute_id,frequency_type,frequency_tot_qty"
				+ " from reduce_ratio_qty t1"
				+ " where exists(select 'X' from reduce_ratio_eqpt t2"
				+ " where t1.frequency_id = t2.frequency_id and t2.eqpt_id in"
				+ " (select '" + eqpId + "' from dual union" 
				+ " select nvl(t.parent_eqpid,t.equipment_id) from pms.equipment t"
				+ " where t.equipment_id='" + eqpId + "'))";

		List list = SQLProcess.excuteSQLQueryGui(sql, delegatorGui);
		for(Iterator it = list.iterator(); it.hasNext(); ) {
            Map map = (Map) it.next();
            String frequencyId = (String) map.get("FREQUENCY_ID");
            String attributeId = (String) map.get("ATTRIBUTE_ID");
            String frequencyType = (String) map.get("FREQUENCY_TYPE");
            String frequencyTotQty = (String) map.get("FREQUENCY_TOT_QTY");
            
            if(frequencyTotQty!=null && !"".equals(frequencyTotQty)){                
                Integer frequencyQty = new Integer(Integer.parseInt(frequencyTotQty) - 1);
                
                Map resetMap = UtilMisc.toMap("frequencyId", frequencyId, "attributeId", attributeId);
                if ("0".equals(frequencyType)) {
                    resetMap.put("lastTestTime", null);
                } else if ("1".equals(frequencyType)) {
                    resetMap.put("frequencyQty", frequencyQty);
                } else if ("2".equals(frequencyType)) {
                    resetMap.put("lastTestTime", null);
                    resetMap.put("frequencyQty", frequencyQty);
                }
                GenericValue gv = delegatorGui.makeValidValue("ReduceRatioQty", resetMap);
                delegatorGui.store(gv);
            }
		}
	}
	
	//获得hold码，hold原因
	public static List getHoldCodeReasonList(String accountDept) {
		if (Constants.CALL_ASURA_FLAG) return null;
		
		GenericDelegator delegatorGui = CommonUtil.getGuiDelegator();
		String sql = "select distinct hold_code,hold_code_desc from hold_code_group"
					+ " where hold_code_desc like '" + accountDept.substring(0, 2) + "%'"
					+ " order by hold_code,hold_code_desc";
        try {        	
			return SQLProcess.excuteSQLQueryGui(sql, delegatorGui);
		} catch (SQLProcessException e) {			
			e.printStackTrace();
			Debug.logError(e.getMessage(), module);
			return null;
		}
		
	}
	
	

	//结束保养或异常表单时：设置GUI检查步骤自动hold，记录历史，触发GUI QC
	public static void saveEqpMaintainHoldList(GenericDelegator delegator, Map parmMap) {
		if (Constants.CALL_ASURA_FLAG) return;
		
		try {
			String holdEnabled = (String) parmMap.get("holdEnabled");		
			if ("ON".equals(holdEnabled)) {
				String holdCodeReason = (String) parmMap.get("holdCodeReason");
				String holdCode = holdCodeReason.substring(0, holdCodeReason.indexOf(":"));
				String holdReason = holdCodeReason.substring(holdCodeReason.indexOf(":")+1);
				parmMap.put("holdCode", holdCode);
				parmMap.put("holdReason", holdReason);
				
				Long listId = delegator.getNextSeqId("eqpMaintainHoldListId");
				parmMap.put("listId", listId);
				parmMap.put("triggerNum", "0");
				parmMap.put("triggerDate", UtilDateTime.nowTimestamp());
				
				String eqpId = (String) parmMap.get("eqpId");
				GenericValue gvEqp = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId", eqpId));
				String parentEqpId = gvEqp.getString("parentEqpid");
		    	if (StringUtils.isNotEmpty(parentEqpId)) {//子设备按母设备触发
		    		parmMap.put("eqpId", parentEqpId);
		        }
				
				GenericDelegator delegatorGui = CommonUtil.getGuiDelegator();			
				//1.插入或更新list
				GenericValue holdListGv = delegatorGui.makeValidValue("EqpMaintainHoldList", parmMap);
				delegatorGui.createOrStore(holdListGv);
				
				//2.插入hist
				GenericValue holdHistGv = delegatorGui.makeValidValue("EqpMaintainHoldHist", parmMap);
				delegatorGui.create(holdHistGv);
				
				//3.触发GUI QC			
				triggerQcEqpMaintainTime(parmMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), module);
		}
	}
	
	//added by dinghh
	private static void triggerQcEqpMaintainTime(Map parmMap) throws Exception {
		if (Constants.CALL_ASURA_FLAG) return;
		
		GenericDelegator delegatorGui = CommonUtil.getGuiDelegator();
		String eqpId = (String) parmMap.get("eqpId");
		
		List qcStatus = delegatorGui.findByAnd("EqpQcStatus", UtilMisc.toMap("equipmentId", eqpId, "qcFactor", "EQP_MAINTAIN_TIME", "enableFlag", "Y"));
		if (qcStatus != null && qcStatus.size() > 0) {
			for (Iterator it = qcStatus.iterator(); it.hasNext();) {
				GenericValue qc = (GenericValue) it.next();				
    			qc.set("qcStatus", "OPEN");
				qc.set("eqpMaintainTime", UtilDateTime.nowTimestamp());
				qc.store();
				
				GenericValue userLogin = (GenericValue) parmMap.get("userLogin");
				GenericValue qcHist = getRecurrQcHist((Map) qc, delegatorGui, userLogin);				
				delegatorGui.createOrStore(qcHist);
			}
		}
	}
}
