package com.csmc.mcs.webapp.cabinet.helper;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityWhereString;

import com.csmc.mcs.webapp.use.helper.UseHelper;
import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.MiscUtils;


/**
 * �� CabinetHelper.java 
 * @version  1.0  2009-7-1
 * @author   dinghh
 */
public class CabinetHelper {

    public static final String module = CabinetHelper.class.getName();
    
    /**
	 * ͨ������map��ѯ�����б�,���Ϻ�����
	 * @param delegator
	 * @param paramMap
	 * @return McsMaterialInfo List
     * @throws GenericEntityException 
	 */
	public static List queryMaterialList(GenericDelegator delegator, Map paramMap) throws GenericEntityException {
		return delegator.findByAnd("McsMaterialInfo", paramMap, UtilMisc.toList("mtrNum"));
	}
	
	/** 
     * ��ѯ�����б�(��ά���������ϵĹܿ��Ϻ�)
     * @param delegator
     * @param paramMap
     * @return StoReqList
     * @throws SQLProcessException 
     */
    public static List queryStoReqList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
        Long deptIndex = (Long) paramMap.get("deptIndex");
        String activeFlag = (String) paramMap.get("activeFlag");
        String mtrGrp = (String) paramMap.get("mtrGrp");
        String mtrNum = (String) paramMap.get("mtrNum");
        String recipient = (String) paramMap.get("recipient");
        String orderBy = (String) paramMap.get("orderBy");

        StringBuffer sql = new StringBuffer();
        sql.append("select t1.material_sto_req_index, t1.vendor_batch_num, t1.mtr_num,t1.mtr_desc,");
        sql.append(" t1.qty-t1.active_qty qty, t1.qty total, t1.doc_time,t1.shelf_life_expiration_date,");
        sql.append(" t1.reason_for_movement, nvl(a.account_name,t1.recipient) account_name, a.account_dept, t1.cost_center");
        sql.append(" from mcs_material_sto_req t1, mcs_material_info t2, account a");
        sql.append(" where t1.mtr_num=t2.mtr_num and t1.dept_index=t2.dept_index and t1.recipient=a.account_no(+)");
        sql.append(" and t1.qty > t1.active_qty and t2.enabled = 1 and t2.in_control = 1");
        
        if (StringUtils.isNotEmpty(deptIndex.toString())) {
            sql.append(" and t1.dept_index='").append(deptIndex).append("'");
        }

        if (StringUtils.isNotEmpty(activeFlag)) {
            sql.append(" and t1.active_flag = '").append(activeFlag).append("'");
        }

        sql.append(" and t1.mtr_grp = '").append(mtrGrp).append("'");
        
        if (StringUtils.isNotEmpty(mtrNum)) {
            sql.append(" and t1.mtr_num like '").append(mtrNum).append("%'");
        }

        if (StringUtils.isNotEmpty(recipient)) {
            sql.append(" and t1.recipient = '").append(recipient).append("'");
        }
        
        if (StringUtils.isNotEmpty(orderBy)) {
        	sql.append(" order by t1." + orderBy);
        } else {
        	sql.append(" order by t1.doc_time");
        }
        
        List list = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
        return list;
    }
    
	/** 
	 * ��ѯ�ⷿ������¼
	 * @param delegator
	 * @param paramMap
	 * @return StoReqList
	 * @throws SQLProcessException 
	 */
	public static List queryCancelVendorStoReqList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
		Long deptIndex = (Long) paramMap.get("deptIndex");
    	String activeFlag = (String) paramMap.get("activeFlag");
    	String mtrGrp = (String) paramMap.get("mtrGrp");
    	Boolean isQtyBelowZero = (Boolean) paramMap.get("isQtyBelowZero");

		StringBuffer sql = new StringBuffer();
		sql.append("select t1.material_sto_req_index, t1.vendor_batch_num, t1.mtr_num,t1.mtr_desc, t1.active_qty,");
		sql.append(" t1.qty-t1.active_qty qty, t1.qty total, t1.doc_time,t1.shelf_life_expiration_date");
		sql.append(" from mcs_material_sto_req t1, mcs_material_info t2");
		sql.append(" where t1.mtr_num=t2.mtr_num and t1.dept_index=t2.dept_index");
		sql.append(" and t2.enabled = 1 and t2.in_control = 1");
		
		if (deptIndex != null) {
		    if (StringUtils.isNotEmpty(deptIndex.toString())) {
		        sql.append(" and t1.dept_index='").append(deptIndex).append("'");
		    }
		}

		if (StringUtils.isNotEmpty(activeFlag)) {
			sql.append(" and t1.active_flag = '").append(activeFlag).append("'");
		}
		
		if (isQtyBelowZero != null) {
		    //SAP�б�
		    if (isQtyBelowZero.booleanValue() == true) {
		        sql.append(" and t1.qty < 0");
		    } else {
		        sql.append(" and t1.qty > 0");
		        String vendorBatchNum = (String) paramMap.get("vendorBatchNum");
		        String mtrNum = (String) paramMap.get("mtrNum");
		        sql.append(" and t1.vendor_batch_num = '").append(vendorBatchNum+"' and t1.mtr_num = '").append(mtrNum).append("'");
		    }
		} else {
		    sql.append(" and t1.qty > t1.active_qty");
		}

		sql.append(" and t1.mtr_grp = '").append(mtrGrp).append("'");
		
		sql.append(" order by t1.doc_time");
		List list = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		return list;
	}

	/** 
	 * ���ɱ���
	 * @param stoReqGv
	 * @param defaultAliasName: mtrStatusId
	 * @return Alias Name
	 */
	public static String getAliasName(GenericValue stoReqGv, String defaultAliasName) {
		String aliasName = null;		
		String mtrGrp = stoReqGv.getString("mtrGrp");
		String mtrNum = stoReqGv.getString("mtrNum");
		
        if (ConstantsMcs.TARGET.equalsIgnoreCase(mtrGrp)) {        	
    		//Fab1&Fab5�в�����Ϊ22λ�ַ�: 11λ�Ϻ�+|+10λsap���š�ϵͳ����mtrStatusIdʹ����aliasNameΨһ
//    		aliasName = mtrNum + "|" + stoReqGv.getString("batchNum").trim() + "-" + defaultAliasName;
			if (isPhotoResist55cp(mtrNum)) {
				aliasName = stoReqGv.getString("vendorBatchNum").trim() + "-" + defaultAliasName;
			}
		} else if (ConstantsMcs.PHOTORESIST.equalsIgnoreCase(mtrGrp)) {
        	if (isPhotoResist55cp(mtrNum)) {
            	aliasName = stoReqGv.getString("vendorBatchNum").trim() + "-" + defaultAliasName;
            } 
        }
        
        if (StringUtils.isEmpty(aliasName)) {
        	aliasName = defaultAliasName;
        }
        
		return aliasName;
	}

	private static boolean isPhotoResist55cp(String mtrNum) {
		//return ConstantsMcs.PHOTORESIST_55CP.equalsIgnoreCase(mtrNum);
		return false;//����ͨ��̽�ɨ��¼������
	}
	
	/** 
	 * �вı���
	 * @return Target Alias
	 */ 
//	private static String getAliasTarget() {
//		// TODO MCS getAliasTarget Auto-generated method stub
//		return null;
//	}

	/**
	 * 1.�������ϱ��¼activeFlag/activeTime/activeQty��λ
     * 2.�����ݴ���
     * 3.��¼��ʷ
	 * @param delegator
	 * @param userNo
	 * @param userDeptIndex
	 * @param stoReqList
	 * @param useQtyMap
	 * @return process message
	 * @throws GenericEntityException
	 */
	public static String intoCabinetQtyProcess(GenericDelegator delegator,
			String userNo, List stoReqList, HashMap useQtyMap)
			throws GenericEntityException {
		String msg = "";
		
		if (stoReqList != null && stoReqList.size() > 0) {				
			Long transactionId = delegator.getNextSeqId(ConstantsMcs.TRANSACTION_ID);
			Timestamp nowTs = UtilDateTime.nowTimestamp();
			List toStore = new LinkedList();
			
			for (Iterator it = stoReqList.iterator(); it.hasNext();) {
				GenericValue stoReqGv = (GenericValue) it.next();
				String materialStoReqIndex = stoReqGv.getString("materialStoReqIndex");
				String mtrGrp = stoReqGv.getString("mtrGrp");
				String mtrNum = stoReqGv.getString("mtrNum");
				Long stoDeptIndex = stoReqGv.getLong("deptIndex");
				Long qty = stoReqGv.getLong("qty");
				Long activeQty = stoReqGv.getLong("activeQty");
				
				Long useQty = (Long) useQtyMap.get(materialStoReqIndex);
				long  totalActiveQty = activeQty.longValue() + useQty.longValue();
				if (totalActiveQty > qty.longValue()) {
					//��ʹ��������������ʱ����������
					continue;
				}
				
				// 1.�������ϱ��¼�ݴ�������activeFlag����λ
				Map mtrStatusMap = new HashMap(stoReqGv);
				if (totalActiveQty == qty.longValue()) {
					mtrStatusMap.put("activeFlag", ConstantsMcs.Y);
				}
				mtrStatusMap.put("activeTime", nowTs);
				mtrStatusMap.put("activeQty", new Long(totalActiveQty));
				GenericValue matStoReqGv = delegator.makeValidValue("McsMaterialStoReq", mtrStatusMap);
			    toStore.add(matStoReqGv);
			    
			    // ��ѯmaterialIndex
			    GenericValue materialInfo = CommonUtil.findFirstRecordByAnd(
						delegator, "McsMaterialInfo", 
						UtilMisc.toMap("mtrNum", mtrNum, "deptIndex", stoDeptIndex));
				String materialIndex = materialInfo.getString("materialIndex");
			    	
			    for (int i=0; i < useQty.intValue(); i++) {
					// 2.��Ƭ�����ݴ���
					Long mtrStatusId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_INDEX);
					mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_INDEX, mtrStatusId);
					mtrStatusMap.put("materialIndex", materialIndex);
					mtrStatusMap.put("aliasName", getAliasName(stoReqGv, mtrStatusId.toString()));//MCS get alias Name
					mtrStatusMap.put("status", ConstantsMcs.CABINET_NEW);
					//matStatusMap.put("usingObjectId", "");
					mtrStatusMap.put(ConstantsMcs.TRANSACTION_ID, transactionId);
					mtrStatusMap.put("transBy", userNo);
					mtrStatusMap.put("updateTime", nowTs);
					
					if (ConstantsMcs.PHOTORESIST.equalsIgnoreCase(mtrGrp)) {
						//��̽��ݴ漴��ʼ����
						mtrStatusMap.put("unfrozenTransBy", userNo);
						mtrStatusMap.put("unfrozenTransTime", nowTs);
					}
					
				    GenericValue mtrStatusGv = delegator.makeValidValue("McsMaterialStatus", mtrStatusMap);
				    toStore.add(mtrStatusGv);
				    
				    // 3.��Ƭ��¼��ʷ
				    Long mtrStatusHistId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX);
				    mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX, mtrStatusHistId);
				    
				    GenericValue mtrStatusHistGv = delegator.makeValidValue("McsMaterialStatusHist", mtrStatusMap);
				    toStore.add(mtrStatusHistGv);
				} // end for qty
			    
			    msg = msg + mtrNum + "(" + useQty + "), ";
			} // end for stoReqList
			
			delegator.storeAll(toStore);				
		} // end if
		
		return msg;
	}
	
	/**
	 * 1.�������ϱ��¼activeFlag/activeTime/activeQty��λ
     * 2.��Ƭʹ�ò����꣬��ʹ��
     * 3.��¼��ʷ
	 * @param delegator
	 * @param userNo
	 * @param stoReqList
	 * @param useQtyMap
	 * @param eqpId
	 * @param useNote
	 * @param newStatus
	 * @return process message
	 * @throws GenericEntityException
	 */
	public static String intoCabinetQtyUse(GenericDelegator delegator,
			String userNo, List stoReqList, HashMap useQtyMap,
			String eqpId, String useNote, String newStatus)
			throws GenericEntityException {
		String msg = "";
		
		//������������״̬
		String usingObjectId = "";
		String status = "";
		if (ConstantsMcs.USE_AND_FINISH.equals(newStatus)) {
			status = ConstantsMcs.FINISH;
			usingObjectId = "";
		} else if (ConstantsMcs.USING.equals(newStatus)) {
			status = ConstantsMcs.USING;
			usingObjectId = eqpId;
		}
		
		if (stoReqList != null && stoReqList.size() > 0) {				
			Long transactionId = delegator.getNextSeqId(ConstantsMcs.TRANSACTION_ID);
			Timestamp nowTs = UtilDateTime.nowTimestamp();
			List toStore = new LinkedList();
			
			for (Iterator it = stoReqList.iterator(); it.hasNext();) {
				GenericValue stoReqGv = (GenericValue) it.next();
				String materialStoReqIndex = stoReqGv.getString("materialStoReqIndex");
				String mtrNum = stoReqGv.getString("mtrNum");
				Long stoDeptIndex = stoReqGv.getLong("deptIndex");
				Long qty = stoReqGv.getLong("qty");
				Long activeQty = stoReqGv.getLong("activeQty");
				
				Long useQty = Long.valueOf(useQtyMap.get(materialStoReqIndex).toString());
				long  totalActiveQty = activeQty.longValue() + useQty.longValue();
				if (totalActiveQty > qty.longValue()) {
					//��ʹ��������������ʱ����������
					continue;
				}
				
				// 1.�������ϱ��¼�ݴ�������activeFlag����λ
				Map mtrStatusMap = new HashMap(stoReqGv);
				if (totalActiveQty == qty.longValue()) {
					mtrStatusMap.put("activeFlag", ConstantsMcs.Y);
				}
				mtrStatusMap.put("activeTime", nowTs);
				mtrStatusMap.put("activeQty", new Long(totalActiveQty));
				GenericValue matStoReqGv = delegator.makeValidValue("McsMaterialStoReq", mtrStatusMap);
			    toStore.add(matStoReqGv);
			    
			    // ��ѯmaterialIndex
			    GenericValue materialInfo = CommonUtil.findFirstRecordByAnd(
						delegator, "McsMaterialInfo", 
						UtilMisc.toMap("mtrNum", mtrNum, "deptIndex", stoDeptIndex));
				String materialIndex = materialInfo.getString("materialIndex");
			    	
			    for (int i=0; i < useQty.intValue(); i++) {
					// 2.��Ƭʹ�ò����꣬��ʹ��:��¼����״̬
					Long mtrStatusId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_INDEX);
					mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_INDEX, mtrStatusId);
					mtrStatusMap.put("materialIndex", materialIndex);
					mtrStatusMap.put("aliasName", getAliasName(stoReqGv, mtrStatusId.toString()));//MCS get alias Name
					mtrStatusMap.put("status", status);
					mtrStatusMap.put("usingObjectId", usingObjectId);
					mtrStatusMap.put(ConstantsMcs.TRANSACTION_ID, transactionId);
					mtrStatusMap.put("transBy", userNo);
					mtrStatusMap.put("updateTime", nowTs);		
					
				    GenericValue mtrStatusGv = delegator.makeValidValue("McsMaterialStatus", mtrStatusMap);
				    toStore.add(mtrStatusGv);
				    
				    // 3.��Ƭ��¼��ʷ
				    //3.1��¼ʹ��
				    Long mtrStatusHistId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX);
				    mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX, mtrStatusHistId);
				    
				    mtrStatusMap.put("status", ConstantsMcs.USING);
				    mtrStatusMap.put("usingObjectId", eqpId);				    
				    mtrStatusMap.put("note", useNote);
				    GenericValue mtrStatusHistGv = delegator.makeValidValue("McsMaterialStatusHist", mtrStatusMap);
				    toStore.add(mtrStatusHistGv);
				    
				    //3.2��ѧƷ��¼����
				    if (ConstantsMcs.USE_AND_FINISH.equals(newStatus)) {
				    	mtrStatusHistId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX);
					    mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX, mtrStatusHistId);
					    
					    mtrStatusMap.put("status", status);
					    mtrStatusMap.put("usingObjectId", usingObjectId);					    
					    GenericValue mtrStatusHistFinishGv = delegator.makeValidValue("McsMaterialStatusHist", mtrStatusMap);
					    toStore.add(mtrStatusHistFinishGv);
					}
				} // end for qty
			    
			    msg = msg + mtrNum + "(" + useQty + "), ";
			} // end for stoReqList
			
			delegator.storeAll(toStore);				
		} // end if
		
		return msg;
	}
	
	/** 
	 * ��ѯ�����б�(��ά���������ϵĹܿ��Ϻ�BarCode)
	 * @param delegator
	 * @param paramMap
	 * @return StoReqList
	 * @throws SQLProcessException 
	 */
	public static List queryStoReqListBarCode(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
		Long deptIndex = (Long) paramMap.get("deptIndex");
    	String activeFlag = (String) paramMap.get("activeFlag");
    	String mtrGrp = (String) paramMap.get("mtrGrp");

		StringBuffer sql = new StringBuffer();
		sql.append("select t1.material_sto_req_index, t1.vendor_batch_num, t1.mtr_num,t1.mtr_desc,t1.qty,t1.doc_time,t1.shelf_life_expiration_date,");
		
		sql.append(" t3.vendor_mtr_num,");
		sql.append(" case when length(t1.vendor_batch_num)<8 then lpad(t1.vendor_batch_num, 8, '0')");
		sql.append(" else substr(t1.vendor_batch_num,length(t1.vendor_batch_num)-7,8) end vendor_batch_num_barcode");
		
		sql.append(" from mcs_material_sto_req t1, mcs_material_info t2, mcs_vendor_material t3");
		
		sql.append(" where t1.mtr_num=t2.mtr_num and t1.dept_index=t2.dept_index");
		sql.append(" and t1.mtr_num=t3.mtr_num");
		sql.append(" and t1.qty > 0 and t2.enabled = 1 and t2.in_control = 1");
		
		if (StringUtils.isNotEmpty(deptIndex.toString())) {
			sql.append(" and t1.dept_index='").append(deptIndex).append("'");
		}

		if (StringUtils.isNotEmpty(activeFlag)) {
			sql.append(" and t1.active_flag = '").append(activeFlag).append("'");
		}

		if (StringUtils.isNotEmpty(mtrGrp)) {
			sql.append(" and t1.mtr_grp = '").append(mtrGrp).append("'");
		}

		sql.append(" order by t1.doc_time");
		List list = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		return list;
	}
	
	/** 
	 * ��ѯSAP�Ƿ�Ϊ���հв�
	 * @param delegator
	 * @param mtrNum
	 * @return 
	 * @throws SQLProcessException 
	 */
	public static boolean isCallbackDrone(GenericDelegator delegator, String mtrNum) throws SQLProcessException {
		return false;//��ȡ��У����sapͬ���ӿڹ���
		
		/*StringBuffer sql = new StringBuffer();
		sql.append("select matcode from mis_DRONE_CALLBACK_MATNR");
		sql.append(" where matcode='").append(mtrNum).append("'");
		
		List list = SQLProcess.excuteSQLQuery(sql.toString(), delegator);		
		if (list != null && list.size() > 0) {
			return true;
		} else {
			return false;
		}*/		
	}
	
	/** 
	 * ��ѯSAP���б���
	 * @param delegator
	 * @param mtrNum
	 * @return Drone List
	 * @throws SQLProcessException 
	 */
	public static List queryDroneList(GenericDelegator delegator, String mtrNum) throws SQLProcessException {
		StringBuffer sql = new StringBuffer();
		sql.append("select upper(t1.deone_id)||'-'||t1.num deone_id_num");
		sql.append(" from mis_DRONE_CALLBACK_RK t1, mcs_material_status t2");
		sql.append(" where upper(t1.deone_id)||'-'||t1.num = t2.alias_name(+)");
		sql.append(" and t2.alias_name is null");
		sql.append(" and t1.matcode like '%").append(mtrNum).append("'");
		sql.append(" and t1.rk_flag='���'");
		sql.append(" order by t1.deone_id");
		
		List list = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		return list;
	}
	
	/** 
	 * ��ѯ����������ɨ������BarCode
	 * @param delegator
	 * @param paramMap
	 * @param queryType
	 * @return 
	 * @throws SQLProcessException 
	 */
	public static List queryStatusListBarCode(GenericDelegator delegator, Map paramMap, String queryType) throws SQLProcessException {
		Long deptIndex = (Long) paramMap.get("deptIndex");
    	String mtrGrp = (String) paramMap.get("mtrGrp");
    	String mtrNum = (String) paramMap.get("mtrNum");

		StringBuffer sql = new StringBuffer();
		sql.append("select t1.material_status_index, t1.alias_name, t1.vendor_batch_num, t1.mtr_grp, t1.mtr_num, t1.mtr_desc,");
		sql.append(" to_char(t1.shelf_life_expiration_date,'yyyy-mm-dd') shelf_life_expiration_date, t1.doc_time, t1.status, t3.vendor_mtr_num,");
		sql.append(" t1.unfrozen_trans_time,t1.unfrozen_trans_by,");
		sql.append(" case when length(t1.vendor_batch_num)<8 then lpad(t1.vendor_batch_num, 8, '0')");
		sql.append(" else substr(t1.vendor_batch_num,length(t1.vendor_batch_num)-7,8) end vendor_batch_num_barcode");
		
		sql.append(" from mcs_material_status t1, mcs_vendor_material t3");
		sql.append(" where t1.mtr_num = t3.mtr_num(+)");		
			sql.append(" and t1.status like '" + ConstantsMcs.CABINET + "%'");
		
		if (deptIndex != null) {
			sql.append(" and t1.dept_index='").append(deptIndex).append("'");
		}		
		
		sql.append(" and t1.mtr_grp = '").append(mtrGrp).append("'");		
		if (StringUtils.isNotEmpty(mtrNum)) {		
			sql.append(" and t1.mtr_num = '").append(mtrNum).append("'");
		}
		
		if ("0".equals(queryType)) {//δ¼��
			sql.append(" and to_char(t1.material_status_index) = t1.alias_name");
		} else if ("1".equals(queryType)) {//��¼��
			sql.append(" and to_char(t1.material_status_index) != t1.alias_name");
		}

		sql.append(" order by t1.mtr_num, t1.material_status_index");
		List list = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		return list;
	}
	
	/** 
	 * ����BarCode
	 * @param delegator
	 * @param paramMap(mtrGrp, mtrStatusIdArray���к�, aliasNameArray�޸�ǰ����, barcodePrefixArray, barcodeArrayɨ������)
	 * @param paramMap(isOperator��Ĥ��ҵԱ)
	 * @return errorMsg
	 * @throws GenericEntityException 
	 */
	public static String saveBarcode(GenericDelegator delegator, Map paramMap) throws GenericEntityException {
		String errorMsg = "";
		List toStore = new LinkedList();
		
		String mtrGrp = (String) paramMap.get("mtrGrp");
		//String mtrNum = (String) paramMap.get("mtrNum");
		String[] mtrStatusIdArray = (String[]) paramMap.get("mtrStatusIdArray");
		String[] aliasNameArray = (String[]) paramMap.get("aliasNameArray");
		String[] barcodePrefixArray = (String[]) paramMap.get("barcodePrefixArray");
		String[] barcodeArray = (String[]) paramMap.get("barcodeArray");
		String isOperator = (String) paramMap.get("isOperator");
		
		for (int i = 0; i < mtrStatusIdArray.length; i++) {
			String mtrStatusId = mtrStatusIdArray[i];
			String aliasName = aliasNameArray[i];//��ǰ��������ʼ��mtrStatusId��ͬ
			String barcodePrefix = barcodePrefixArray[i];
			String barcode = barcodeArray[i].toUpperCase();//ɨ�������
			
			if (StringUtils.isEmpty(barcode)) {
				// �����barcodeΪ�գ�������Ϊϵͳ���к�
				barcode = mtrStatusId;
			}
			
			if (aliasName.equals(barcode)) {
				// δ�޸ı���ʱ������
				continue;
			} else if(ConstantsMcs.Y.equals(isOperator) && !mtrStatusId.equals(aliasName)) {
				// ��Ĥ��SOG��ҵԱ������������룬���ܸ���
				continue;
			} 
			
			if (StringUtils.isNotEmpty(mtrStatusId)) {
				if (ConstantsMcs.PHOTORESIST.equals(mtrGrp)
						&& barcode.length() == 29
						&& !barcode.startsWith(barcodePrefix)) {//�жϱ�׼��̽������ʽ
					errorMsg = errorMsg + barcode + " ����������淶��";
					continue;
				}
				
				Map mtrStatusMap = new HashMap();
				mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_INDEX, mtrStatusId);
				mtrStatusMap.put("aliasName", barcode);
				mtrStatusMap.put("updateTime", UtilDateTime.nowTimestamp());
				
				if (ConstantsMcs.CHEMICAL.equals(mtrGrp) &&  barcode.length() == 18) {
					// SOG���ֿ�����sap�����д��󣬰�ʵ������������������
					mtrStatusMap.put("vendorBatchNum", barcode.substring(2, 12));
				}
				
				GenericValue mtrStatusGv = delegator.makeValidValue("McsMaterialStatus", mtrStatusMap);
			    toStore.add(mtrStatusGv);
			}
		}
		
		delegator.storeAll(toStore);
		return errorMsg;
	}
	
	/** 
	 * ��ѯ��Żر������µ��ݴ��������б�
	 * @param delegator
	 * @param paramMap
	 * @return frozenOrUnfrozenList
	 * @throws SQLProcessException 
	 */
	public static List queryFrozenOrUnfrozenList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
		Long deptIndex = (Long) paramMap.get("deptIndex");
		String opType = (String) paramMap.get("opType");
		String mtrGrp = (String) paramMap.get("mtrGrp");
    	String mtrNum = (String) paramMap.get("mtrNum");

		StringBuffer sql = new StringBuffer();
		sql.append("select t1.material_status_index, t1.alias_name, t1.vendor_batch_num, t1.mtr_num,");
		sql.append(" t1.mtr_desc, t1.shelf_life_expiration_date, t1.mrb_date,");
		sql.append(" round((sysdate-t1.unfrozen_trans_time)*1, 2) unfrozen_time,");
		sql.append(" case when sysdate - t1.unfrozen_trans_time > nvl(t2.max_frozen_time_limit,24000)/24 then 'Y' end frozen");

		sql.append(" from mcs_material_status t1, mcs_material_info t2");
		
		sql.append(" where t1.material_index=t2.material_index");
		sql.append(" and t1.status like '" + ConstantsMcs.CABINET + "%'");		
		sql.append(" and t1.dept_index='").append(deptIndex).append("'");
		
		if ("Frozen".equals(opType)) {
			sql.append(" and t1.unfrozen_trans_time is not null");
		} else if ("Unfrozen".equals(opType)) {
			sql.append(" and t1.unfrozen_trans_time is null");
		}

		if (StringUtils.isNotEmpty(mtrGrp)) {
			sql.append(" and t1.mtr_grp = '").append(mtrGrp).append("'");
		}
		
		if (StringUtils.isNotEmpty(mtrNum)) {
			sql.append(" and t1.mtr_num = '").append(mtrNum).append("'");
		}

		sql.append(" order by t1.doc_time");
		List list = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		return list;
	}
	
	/** 
	 * �ⷿ���������޸�
	 * @param delegator
	 * @param paramMap(mtrGrp, mtrNum, mtrStatusIdArray, vendorBatchNumArray, shelfLifeExpirationDateArray)
	 * @return 
	 * @throws GenericEntityException 
	 * @throws ParseException 
	 */
	public static void saveVendorInfo(GenericDelegator delegator, Map paramMap) throws GenericEntityException, ParseException {
		List toStore = new LinkedList();
		
    	String mtrNum = (String) paramMap.get("mtrNum");
		String[] mtrStatusIdArray = (String[]) paramMap.get("mtrStatusIdArray");
		String[] vendorBatchNumArray = (String[]) paramMap.get("vendorBatchNumArray");
		String[] shelfLifeExpirationDateArray = (String[]) paramMap.get("shelfLifeExpirationDateArray");
		
		for (int i = 0; i < mtrStatusIdArray.length; i++) {
			String mtrStatusId = mtrStatusIdArray[i];
			String vendorBatchNum = vendorBatchNumArray[i].trim();
			String shelfLifeExpirationDate = shelfLifeExpirationDateArray[i].trim();
			
			if (StringUtils.isNotEmpty(mtrStatusId)) {
				Map mtrStatusMap = new HashMap();
				mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_INDEX, mtrStatusId);
				mtrStatusMap.put("vendorBatchNum", vendorBatchNum);
				mtrStatusMap.put("shelfLifeExpirationDate", MiscUtils.getGuiDate(shelfLifeExpirationDate));
				
				if (isPhotoResist55cp(mtrNum)) {
					mtrStatusMap.put("aliasName", vendorBatchNum + "-" + mtrStatusId);
				}
				
				GenericValue mtrStatusGv = delegator.makeValidValue("McsMaterialStatus", mtrStatusMap);
			    toStore.add(mtrStatusGv);
			}
		}
		
		delegator.storeAll(toStore);
	}
	
	/** 
	 * ��ѯ�ݴ�����������
	 * @param delegator
	 * @param paramMap
	 * @return 
	 * @throws SQLProcessException 
	 */
	public static List queryCabinetMaterialList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
		Long deptIndex = (Long) paramMap.get("deptIndex");
    	String mtrGrp = (String) paramMap.get("mtrGrp");
    	String mtrNum = (String) paramMap.get("mtrNum");

		StringBuffer sql = new StringBuffer();
		sql.append("select t1.material_status_index, t1.alias_name, t1.vendor_batch_num, t1.mtr_grp, t1.mtr_num, t1.mtr_desc,");
		sql.append(" to_char(t1.shelf_life_expiration_date,'yyyy-mm-dd') shelf_life_expiration_date, t1.doc_time, t1.status, ");
		sql.append(" t1.unfrozen_trans_time, t1.unfrozen_trans_by");
		
		sql.append(" from mcs_material_status t1");
		sql.append(" where t1.status = '" + ConstantsMcs.CABINET_NEW + "'");
		
		if (deptIndex != null) {
			sql.append(" and t1.dept_index='").append(deptIndex).append("'");
		}		
		
		sql.append(" and t1.mtr_grp = '").append(mtrGrp).append("'");		
		sql.append(" and t1.mtr_num = '").append(mtrNum).append("'");

		sql.append(" order by t1.material_status_index");
		List list = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		return list;
	}
	
	/**
     * ��̽����ò�ѯ
     * @param delegator
     */
	public static List queryPhotoResistPickupList(GenericDelegator delegator) throws SQLProcessException {
    	StringBuffer sql = new StringBuffer();
		sql.append("select t1.mtr_num, t1.mtr_desc, t1.safe_qty - nvl(t2.cabinet_qty,0) req_qty, nvl(t2.cabinet_qty,0) cabinet_qty, t1.safe_qty, t2.unfrozen_time, t2.shelf_life_time");
		sql.append(" from MCS_PR_SAFE_QTY t1,");
		sql.append(" (select s.mtr_num, count(*) cabinet_qty, round(sysdate-min(s.unfrozen_trans_time),1) unfrozen_time, round(min(s.shelf_life_expiration_date)-sysdate,1) shelf_life_time");
		sql.append(" from mcs_material_status s");
		sql.append(" where to_char(s.material_status_index) <> s.alias_name and s.mtr_grp = '" + ConstantsMcs.PHOTORESIST + "' and s.status like '" + ConstantsMcs.CABINET + "%' group by s.mtr_num) t2");
		sql.append(" where t1.mtr_num=t2.mtr_num(+) order by t1.mtr_num");

    	List list = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
    	return list;
	}
	
	/** 
     * ��ѯ���������趨���ļ�parts_pm �� �ֿ߲߱��mcs_material_sto_req
     * @param delegator
     * @param paramMap
     * @return StoReqList
     * @throws SQLProcessException 
     */
    public static List queryStoReqListPartsPm(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
        Long deptIndex = (Long) paramMap.get("deptIndex");
        String mtrGrp = (String) paramMap.get("mtrGrp");
        String mtrNum = (String) paramMap.get("mtrNum");
        String eqpId = (String) paramMap.get("eqpId");
        String flow = (String) paramMap.get("flow");

        String sqlT2 = "select mtr_num, dept_index, sum(t1.qty - t1.active_qty) qty, min(t1.doc_time) doc_time"
        	+ " from mcs_material_sto_req t1"
        	+ " where t1.qty > 0 and t1.active_flag = 'N' and t1.dept_index = " + deptIndex;
        
        String sqlT3 = "select mtr_num, dept_index, count(*) eqp_num, round(sysdate - min(t.update_time)) max_use_period"
        	+ " from mcs_material_status t"
        	+ " where t.status = '" + ConstantsMcs.USING + "' and t.dept_index = " + deptIndex
        	+ " and t.using_object_id = '" + eqpId + "'";
        
        String sqlT4 = "select * from mcs_material_info where dept_index = " + deptIndex;

        if (StringUtils.isNotEmpty(mtrGrp)) {
        	sqlT2 = sqlT2 + " and mtr_grp = '" + mtrGrp + "'";
        	sqlT3 = sqlT3 + " and mtr_grp = '" + mtrGrp + "'";
        }
        
        if (StringUtils.isNotEmpty(mtrNum)) {
        	sqlT2 = sqlT2 + " and mtr_num like '" + mtrNum + "%'";
        	sqlT3 = sqlT3 + " and mtr_num like '" + mtrNum + "%'";
        }
        
        sqlT2 = sqlT2 + " group by mtr_num, dept_index";
        sqlT3 = sqlT3 + " group by mtr_num, dept_index";
        
        String sql = null;
        if (StringUtils.isNotEmpty(flow)) {
        	sql = "select r1.*, t2.qty,t2.doc_time, t3.eqp_num,t3.max_use_period, t4.mtr_num,t4.mtr_desc,t4.usable_time_limit"
        		+ " from parts_pm r1, (" + sqlT2 + ") t2, (" + sqlT3 + ") t3, (" + sqlT4 + ") t4"
        		+ " where r1.part_no = t2.mtr_num(+) and r1.part_no = t3.mtr_num(+) and r1.part_no = t4.mtr_num(+)"
        		+ " and r1.flow_index = " + flow
        		+ " order by r1.part_no";
        } else {
        	sql = "select t2.qty,t2.doc_time, t3.eqp_num,t3.max_use_period, t4.mtr_num,t4.mtr_desc,t4.usable_time_limit"
            	+ " from (" + sqlT2 + ") t2, (" + sqlT3 + ") t3, (" + sqlT4 + ") t4"
            	+ " where t2.mtr_num = t3.mtr_num(+) and t2.dept_index = t3.dept_index(+)"
            	+ " and t2.mtr_num = t4.mtr_num and t2.dept_index = t4.dept_index"
            	+ " order by t2.mtr_num";
        }
        
        List list = SQLProcess.excuteSQLQuery(sql, delegator);
        return list;
    }
    
    /** �������ļ�
	 * һ���п��ֱ��ʹ��: 1.�������ϱ��¼�� 2.������Ƭʹ�ã�������ʷ��¼
	 * �����޿���Զ����ϲ�ʹ��: 1.�������ϱ��¼�� 2.������Ƭʹ�ã�������ʷ��¼
	 * ������ʹ�������Զ������豸���ã���������ʱ�ۼ���0����: 1.���°�Ƭ���߱��ϣ� 2.������ʷ��¼
	 * @return process message
	 * @throws GenericEntityException
     * @throws SQLProcessException 
	 */
	public static String useSparePartBySto(GenericDelegator delegator,
			String userNo, String mtrNum, long lUseQty,
			String eqpId, String note, Long transactionId, Timestamp nowTs)
			throws GenericEntityException, SQLProcessException {
		String msg = "";
		List toStoreList = new LinkedList();				
		
		GenericValue accountGv = AccountHelper.getAccountByNo(delegator, userNo);
		String accountDept = accountGv.getString("accountDept");
		String deptIndex = accountGv.getString("deptIndex");
		
		// ��ѯmaterialIndex
		String materialIndex = "";
		GenericValue materialInfo = CommonUtil.findFirstRecordByAnd(
				delegator, "McsMaterialInfo", 
				UtilMisc.toMap("mtrNum", mtrNum, "deptIndex", deptIndex));
	    if (materialInfo != null) {
	    	materialIndex = materialInfo.getString("materialIndex");
	    }
	    
		//һ����ʹ�������Զ������豸���ã���������ʱ�ۼ���0����
		//1.��Ƭ���£�״̬��Ϊ ���߱��Ͽγ�ȷ�� ONLINE_SCRAP_LEADER��
	    //2.������ʷ��¼���Զ����²�������eqpId
	    int lOffQty = (int) lUseQty;//���±�������
		List offStatusList = getOffStatusListByQty(delegator, lOffQty, eqpId, mtrNum, deptIndex);		
		if (offStatusList != null && offStatusList.size() > 0) {
			UseHelper.updateStatusAndHistToStoreList(delegator, toStoreList,
					offStatusList, userNo, ConstantsMcs.ONLINE_SCRAP_LEADER, eqpId,
					note, transactionId, nowTs);
			msg = msg + mtrNum + " (���߱���" + offStatusList.size() + "), ";
		}
		
		//�����п��ֱ��ʹ��
		//��ѯ�ֿ߲߱��
		String whereString = "qty > 0 and active_flag='" + ConstantsMcs.N + "'"
			+ " and mtr_num = '" + mtrNum + "' and dept_index = '" + deptIndex + "'";
		EntityWhereString con = new EntityWhereString(whereString);
		List stoReqList = delegator.findByCondition("McsMaterialStoReq", con, null, UtilMisc.toList("materialStoReqIndex"));		
		if (stoReqList != null && stoReqList.size() > 0) {
			for (Iterator it = stoReqList.iterator(); it.hasNext();) {
				GenericValue stoReqGv = (GenericValue) it.next();
				long lQty = stoReqGv.getLong("qty").longValue();
				long lActiveQty = stoReqGv.getLong("activeQty").longValue();				
				long availQty = lQty - lActiveQty;
				long stoUseQty = 0;
				
				// 1.�������ϱ��¼�ݴ�������activeFlag����λ
				Map stoReqMap = new HashMap(stoReqGv);
				if (lUseQty >= availQty) {
					stoUseQty = availQty;
					stoReqMap.put("activeFlag", ConstantsMcs.Y);
					stoReqMap.put("activeQty", new Long(lQty));
				} else {
					stoUseQty = lUseQty;
					stoReqMap.put("activeFlag", ConstantsMcs.N);
					stoReqMap.put("activeQty", new Long(lActiveQty + lUseQty));
				}
				stoReqMap.put("activeTime", nowTs);
				
				GenericValue matStoReqGv = delegator.makeValidValue("McsMaterialStoReq", stoReqMap);
			    toStoreList.add(matStoReqGv);
			    
			    // 2.������Ƭʹ�ã�������ʷ��¼
			    insertStatusAndHistToStoreList(delegator, toStoreList, stoUseQty, stoReqMap, userNo,
						eqpId, note, transactionId, materialIndex, nowTs);
			    msg = msg + mtrNum + " (ʹ��" + stoUseQty + "), ";
			    
			    lUseQty = lUseQty - availQty;
			    if (lUseQty <= 0) {
					break;
				}
			} // end for stoReqList							
		} // end if
		
		//�������������̲�ѯ���������趨�� �޿����治��ʱ���Զ����ϲ�ʹ��
		if (lUseQty > 0) {
			// 1.�Զ�����: �������ϱ��¼
			note = note + " (�Զ�����)";			
			long minStoIndex = UseHelper.getMinStoIndex(delegator);
			Map autoStoMap = new HashMap();
			autoStoMap.put("materialStoReqIndex", new Long(minStoIndex));
			
			autoStoMap.put("plant", materialInfo.getString("plant"));
			autoStoMap.put("mtrNum", mtrNum);
			autoStoMap.put("mtrDesc", materialInfo.getString("mtrDesc"));
			autoStoMap.put("mtrGrp", materialInfo.getString("mtrGrp"));
			autoStoMap.put("batchNum", "0");
			autoStoMap.put("vendorBatchNum", "0");			
			autoStoMap.put("shelfLifeExpirationDate", UseHelper.toShelfLifeExpirationDate("1900-1-1"));
			autoStoMap.put("docTime", nowTs);
			autoStoMap.put("costCenter", accountDept);
			autoStoMap.put("equipmentId", eqpId);
			autoStoMap.put("reasonForMovement", note);
			autoStoMap.put("deptIndex", deptIndex);
			autoStoMap.put("updateTime", nowTs);
			autoStoMap.put("qty", new Long(lUseQty));
			autoStoMap.put("activeQty", new Long(lUseQty));
			autoStoMap.put("activeFlag", "Y");
			autoStoMap.put("activeTime", nowTs);			
			autoStoMap.put("recipient", userNo);
			GenericValue stoSeqGv = delegator.makeValidValue("McsMaterialStoReq", autoStoMap);
			toStoreList.add(stoSeqGv);			
			
			// 2.������Ƭʹ�ã�������ʷ��¼
		    insertStatusAndHistToStoreList(delegator, toStoreList, lUseQty, autoStoMap, userNo,
					eqpId, note, transactionId, materialIndex, nowTs);    
			msg = msg + mtrNum + " (����ʹ��" + lUseQty + "), ";
		}		

		delegator.storeAll(toStoreList);
		return msg;
	}
	
	//�õ��豸���ñ��ļ�
	private static List getOffStatusListByQty(GenericDelegator delegator, int offQty, String eqpId, String mtrNum, String deptIndex) throws GenericEntityException {
		String whereString = "status = '" + ConstantsMcs.USING + "' and dept_index=" + deptIndex 
			+ " and using_object_id = '" + eqpId + "' and mtr_num = '" + mtrNum + "'";
		EntityWhereString con = new EntityWhereString(whereString);
		List list = delegator.findByCondition("McsMaterialStatus", con, null, UtilMisc.toList("materialStatusIndex"));
		
		int totalQty = list.size();//�豸��������
		if (totalQty > 0) {
			if (offQty < totalQty) {
				return list.subList(0, offQty);
			} else {//�軻������>=�豸��������ʱ��ȡ�豸����list
				return list;
			}
		} else {
			return null;
		}		
	}

	//add toStoreList�� ������Ƭʹ�ã���Ƭ��¼��ʷ
	private static void insertStatusAndHistToStoreList(
			GenericDelegator delegator, List toStoreList, long useQty, Map stoReqMap, String userNo,
			String eqpId, String note, Long transactionId, String materialIndex, Timestamp nowTs) {
		for (int i=0; i < useQty; i++) {
			// ��Ƭʹ��
			Long mtrStatusId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_INDEX);
			stoReqMap.put(ConstantsMcs.MATERIAL_STATUS_INDEX, mtrStatusId);
			stoReqMap.put("materialIndex", materialIndex);
			stoReqMap.put("aliasName", mtrStatusId.toString());//MCS get alias Name: parts
			stoReqMap.put("status", ConstantsMcs.USING);
			stoReqMap.put("usingObjectId", eqpId);
			stoReqMap.put(ConstantsMcs.TRANSACTION_ID, transactionId);
			stoReqMap.put("transBy", userNo);
			stoReqMap.put("updateTime", nowTs);		
			
		    GenericValue mtrStatusGv = delegator.makeValidValue("McsMaterialStatus", stoReqMap);
		    toStoreList.add(mtrStatusGv);
		    
		    // ��Ƭ��¼��ʷ
		    Long mtrStatusHistId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX);
		    stoReqMap.put(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX, mtrStatusHistId);
		    stoReqMap.put("note", note);
		    
		    GenericValue mtrStatusHistGv = delegator.makeValidValue("McsMaterialStatusHist", stoReqMap);
		    toStoreList.add(mtrStatusHistGv);
		}
	}
}