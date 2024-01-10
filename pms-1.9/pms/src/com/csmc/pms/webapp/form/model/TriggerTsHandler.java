//package com.csmc.pms.webapp.form.model;
//
//import java.sql.Timestamp;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.ofbiz.base.util.Debug;
//import org.ofbiz.entity.GenericDelegator;
//import org.ofbiz.entity.GenericValue;
//
//import com.csmc.asura.core.AsuraHandler;
//import com.csmc.asura.core.AsuraModel;
//import com.csmc.asura.exception.AsuraHandleException;
//import com.csmc.asura.dreams.model.Equipment;
//import com.csmc.asura.util.CommandConstants;
//
//public class TriggerTsHandler implements AsuraHandler {
//	public static final String module = "com.csmc.asura.handler";
//
//	public synchronized AsuraModel handle(AsuraModel model) throws AsuraHandleException {
//		Equipment equipment = (Equipment) model;
//		Debug.logInfo("start trigger Ts handle [" + equipment.getEqpId() + "]", module);
//
//		//EqpID∫ÕDownTime≤Â»Î±ÌPM_ABNORMAL_RECORD
//		GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");
//		Long id = null;
//		Timestamp nowTime = new Timestamp(System.currentTimeMillis());
//		try{
//			Map paramMap=new HashMap();
//			paramMap.put("equipmentId", equipment.getEqpId());
//			paramMap.put("startTime", equipment.getDownTime());
//			id = delegator.getNextSeqId("pmAbnormalRecordseqIndex");
//            paramMap.put("seqIndex", id);
//            paramMap.put("status", "0");
//            paramMap.put("equipmentStatus","WAITENG" );
//            paramMap.put("createTime",nowTime );
//            paramMap.put("updateTime",nowTime );
//            //
//            GenericValue gv = delegator.makeValidValue("PmAbnormalRecord", paramMap);
//            delegator.create(gv);
//            Debug.logInfo("save abnoramlRecord success[" + equipment.getEqpId() + "]", module);
//		}
//		catch(Exception e) {
//            Debug.logError(e.getMessage(), module);
//            throw new AsuraHandleException(e);
//		}
//		return equipment;
//	}
//
//	public String getHandleName() {
//		return CommandConstants.TRIGGER_EQP_ABNORMAL;
//	}
//}
