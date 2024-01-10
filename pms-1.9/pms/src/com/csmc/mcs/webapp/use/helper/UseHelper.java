package com.csmc.mcs.webapp.use.helper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityWhereString;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.csmc.mcs.webapp.cabinet.helper.CabinetHelper;
import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.eqp.helper.PlldbHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;


/**
 * �� UseHelper.java 
 * @version  1.0  2009-7-16
 * @author   dinghh
 */
public class UseHelper {

    public static final String module = UseHelper.class.getName();
    
    /**
     * ��ʾ�豸��������List, for demo test entityExprList
	 * @param delegator
	 * @param eqpId
	 * @param mtrGrp
	 * @param mtrNum
	 * @return Eqp Material List
	 * @throws GenericEntityException
	 */
	public static List queryEqpMaterialListById(GenericDelegator delegator, String userDeptIndex, String eqpId, String mtrGrp, String mtrNum) throws GenericEntityException {
		List entityExprList = new ArrayList();
		entityExprList.add(new EntityExpr("status", EntityOperator.EQUALS, ConstantsMcs.USING));
		entityExprList.add(new EntityExpr("deptIndex", EntityOperator.EQUALS, userDeptIndex));
		entityExprList.add(new EntityExpr("usingObjectId", EntityOperator.EQUALS, eqpId));		
		
		if (StringUtils.isNotEmpty(mtrGrp)) {
			EntityExpr expr =
				new EntityExpr("mtrGrp", EntityOperator.EQUALS, mtrGrp);
			entityExprList.add(expr);
		}
		
		if (StringUtils.isNotEmpty(mtrNum)) {
			EntityExpr expr =
				new EntityExpr("mtrNum", EntityOperator.LIKE, mtrNum + "%");
			entityExprList.add(expr);
		}		

		EntityCondition con =
			new EntityConditionList(entityExprList, EntityOperator.AND);
		List eqpMaterialList = delegator.findByCondition("McsMaterialStatus", con, null, UtilMisc.toList("mtrNum", "materialStatusIndex"));
		return eqpMaterialList;
	}
	
	/**
	 * ��ʾ�ݴ�����������Qty List(Cabinet Back Stock)
	 * @param delegator
	 * @param mtrGrp
	 * @param userDeptIndex
	 * @return Cabinet Material List
	 * @throws SQLProcessException
	 */
	public static List queryCabinetNewMaterialList(GenericDelegator delegator, String userDeptIndex, String mtrGrp, String mtrNum) throws SQLProcessException {
		String sql = "select t.vendor_batch_num, t.mtr_num, t.mtr_desc,"
				+ " to_char(t.shelf_life_expiration_date, 'yyyy-mm-dd') shelf_life_expiration_date,"
				+ " to_char(t.mrb_date, 'yyyy-mm-dd') mrb_date, count(*) qty"
				+ " from mcs_material_status t" 
				+ " where t.dept_index=" + userDeptIndex
				+ " and t.status = '" + ConstantsMcs.CABINET_NEW + "'";
	
		if (StringUtils.isNotEmpty(mtrGrp)) {
			sql = sql + " and t.mtr_grp='" + mtrGrp + "'";
		}
		
		if (StringUtils.isNotEmpty(mtrNum)) {
			sql = sql + " and t.mtr_num like '" + mtrNum + "%'";
		}
		
		sql = sql + " group by t.vendor_batch_num,t.mtr_num,t.mtr_desc,t.shelf_life_expiration_date,t.mrb_date"
				+ " order by t.mtr_num, t.shelf_life_expiration_date, t.mrb_date";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}
	
	/**
	 * ��ʾ7�����˿�״̬����List(Cabinet Back Stock)
	 * @param delegator
	 * @param mtrGrp
	 * @param userDeptIndex
	 * @return Back Stock Material List
	 * @throws GenericEntityException
	 */	
	public static List queryBackStockList( GenericDelegator delegator, String userDeptIndex,String mtrGrp,String mtrNum ) throws SQLProcessException {
		String sql = "select t.material_status_index, t.alias_name, t.vendor_batch_num, t.mtr_num, t.mtr_desc,"
				+ " to_char(t.shelf_life_expiration_date, 'yyyy-mm-dd') shelf_life_expiration_date,"
				+ " to_char(t.mrb_date, 'yyyy-mm-dd') mrb_date, t.update_time"
				+ " from mcs_material_status t" 
				+ " where t.dept_index=" + userDeptIndex
				+ " and t.status = '" + ConstantsMcs.NEW_BACK_STOCK + "'";

		if (StringUtils.isNotEmpty(mtrGrp)) {
			sql = sql + " and t.mtr_grp='" + mtrGrp + "'";
		}
		
		if (StringUtils.isNotEmpty(mtrNum)) {
			sql = sql + " and t.mtr_num like '" + mtrNum + "%'";
		}
		sql = sql +" and t.update_time>=trunc(sysdate)-7 ";
		
		List backStockList = SQLProcess.excuteSQLQuery(sql, delegator);
	    return backStockList;
	}
	
	/**
	 * ��ʾ�豸��������List(Use By Id, Use By Suit, Use By Barcode)
	 * @param delegator
	 * @param paramMap(deptIndex, eqpId, mtrGrp, mtrNum)
	 * @param paramMap(materialSuitIndex, aliasName)
	 * @return Cabinet Material List
	 * @throws GenericEntityException
	 */
	public static List queryEqpMaterialList(GenericDelegator delegator, Map paramMap) throws GenericEntityException {
		String deptIndex = (String) paramMap.get("deptIndex");
		String eqpId = (String) paramMap.get("eqpId");
		String mtrGrp = (String) paramMap.get("mtrGrp");
		String mtrNum = (String) paramMap.get("mtrNum");
		
		// By Suit param
		String materialSuitIndex = (String) paramMap.get("materialSuitIndex");
		String aliasName = (String) paramMap.get("aliasName");
		
		String whereString = "dept_index = " + deptIndex + " and using_object_id = '" + eqpId + "'";
		
		if (StringUtils.isNotEmpty(mtrGrp)) {
			whereString = whereString + " and mtr_grp = '" + mtrGrp + "'";
		}
		
		if (StringUtils.isNotEmpty(mtrNum)) {
			whereString = whereString + " and mtr_num like '" + mtrNum + "%'";
		}
		
		if (StringUtils.isNotEmpty(materialSuitIndex)) {
			// By Suit materialSuitIndex
			whereString = whereString + " and material_index in "
				+ "(select material_index from mcs_material_suit_item"
				+ " where material_suit_index = " + materialSuitIndex + ")";
		}
		
		if (StringUtils.isNotEmpty(aliasName)) {
			// By Suit aliasName
			whereString = whereString + " and alias_name like '" + aliasName + "%'";
		}
		
		whereString = whereString + " and status = '" + ConstantsMcs.USING + "'";
				
		EntityWhereString con = new EntityWhereString(whereString);
		List eqpMaterialList = delegator.findByCondition("McsMaterialStatus", con, null, UtilMisc.toList("updateTime"));
		return eqpMaterialList;
	}

	/**
	 * ��ʾ�ݴ�����������List(Use By Id, Use By Suit)
	 * @param delegator
	 * @param paramMap(deptIndex, eqpId, mtrGrp, mtrNum)
	 * @param paramMap(materialSuitIndex, aliasName)
	 * @return Cabinet Material List
	 * @throws GenericEntityException
	 */
	public static List queryCabinetMaterialList(GenericDelegator delegator, Map paramMap) throws GenericEntityException {
		String deptIndex = (String) paramMap.get("deptIndex");
		String eqpId = (String) paramMap.get("eqpId");
		String mtrGrp = (String) paramMap.get("mtrGrp");
		String mtrNum = (String) paramMap.get("mtrNum");
		
		String materialSuitIndex = (String) paramMap.get("materialSuitIndex");
		String aliasName = (String) paramMap.get("aliasName");
		
		String whereString = "material_index in (select material_index from mcs_mtr_object where dept_index="
							+ deptIndex	+ " and using_object_id = '" + eqpId + "')";		
		
		if (StringUtils.isNotEmpty(mtrGrp)) {
			whereString = whereString + " and mtr_grp = '" + mtrGrp + "'";
		}
		
		if (StringUtils.isNotEmpty(mtrNum)) {
			whereString = whereString + " and mtr_num like '" + mtrNum + "%'";
		}
		
		if (StringUtils.isNotEmpty(materialSuitIndex)) {
			// By Suit materialSuitIndex
			whereString = whereString + " and material_index in "
				+ "(select material_index from mcs_material_suit_item"
				+ " where material_suit_index = " + materialSuitIndex + ")";
		}
		
		if (StringUtils.isNotEmpty(aliasName)) {
			// By Suit aliasName
			whereString = whereString + " and alias_name like '" + aliasName + "%'";
		}
		
		whereString = whereString + " and (shelf_life_expiration_date > sysdate or mrb_date > sysdate or shelf_life_expiration_date=to_date('1900-1-1','yyyy-mm-dd'))";
		whereString = whereString + " and status like '" + ConstantsMcs.CABINET + "%'";
		
		EntityWhereString con = new EntityWhereString(whereString);
		List cabinetList = delegator.findByCondition("McsMaterialStatus", con, null, UtilMisc.toList("mtrNum", "shelfLifeExpirationDate", "aliasName"));
		return cabinetList;
	}
	
	/**
	 * ��ʾ�ݴ�����������List(Use By Barcode)
	 * @param delegator
	 * @param paramMap(deptIndex, eqpId, mtrGrp, mtrNum)
	 * @return Cabinet Material List
	 * @throws GenericEntityException
	 */
	public static List queryCabinetMaterialListByBarcode(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
		String deptIndex = (String) paramMap.get("deptIndex");
		String eqpId = (String) paramMap.get("eqpId");
		String mtrGrp = (String) paramMap.get("mtrGrp");
		String mtrNum = (String) paramMap.get("mtrNum");
		
		String whereString = "t1.material_index in (select material_index from mcs_mtr_object where dept_index="
							+ deptIndex	+ " and using_object_id = '" + eqpId + "')";		
		
		if (StringUtils.isNotEmpty(mtrGrp)) {
			whereString = whereString + " and t1.mtr_grp = '" + mtrGrp + "'";
		}
		
		if (StringUtils.isNotEmpty(mtrNum)) {
			whereString = whereString + " and t1.mtr_num like '" + mtrNum + "%'";
		}
			
		whereString = whereString + " and (t1.shelf_life_expiration_date > sysdate or mrb_date > sysdate or t1.shelf_life_expiration_date=to_date('1900-1-1','yyyy-mm-dd'))";
		whereString = whereString + " and status like '" + ConstantsMcs.CABINET + "%'";
		
		String sql = "select t1.material_status_index, t1.alias_name, t1.vendor_batch_num, t1.mtr_num,"
			+ " t1.mtr_desc, t1.shelf_life_expiration_date, t1.mrb_date, t1.unfrozen_trans_time,"
			+ " round((sysdate - t1.unfrozen_trans_time) * 1, 2) unfrozen_time,"
			+ " case when sysdate - t1.unfrozen_trans_time > nvl(t2.max_frozen_time_limit,24000)/24 then 'Y' end frozen,"
			+ " t1.remark"
			+ " from mcs_material_status t1, mcs_material_info t2"
			+ " where t1.material_index=t2.material_index and " + whereString
			+ " order by t1.mtr_num, t1.shelf_life_expiration_date, t1.unfrozen_trans_time";
		
		// FAB5 ��̽�K/S��������ʾ
		if (ConstantsMcs.PHOTORESIST.equals(mtrGrp) && Constants.CALL_ASURA_FLAG) {
//			if (PlldbHelper.isFab6Eqp(delegator, eqpId)) {
//				whereString = whereString + " and r.cost_center like '%S��'";
//			}
//			else
			if (PlldbHelper.isFab5Eqp(delegator, eqpId)) {
				whereString = whereString + " and r.cost_center not like '%S��'";
			}
			sql = "select t1.material_status_index, t1.alias_name, t1.vendor_batch_num, t1.mtr_num,"
				+ " t1.mtr_desc, t1.shelf_life_expiration_date, t1.mrb_date, t1.unfrozen_trans_time,"
				+ " round((sysdate - t1.unfrozen_trans_time) * 1, 2) unfrozen_time,"
				+ " case when sysdate - t1.unfrozen_trans_time > nvl(t2.max_frozen_time_limit,24000)/24 then 'Y' end frozen,"
				+ " t1.remark"
				+ " from mcs_material_status t1, mcs_material_info t2, mcs_material_sto_req r"
				+ " where t1.material_index=t2.material_index and t1.material_sto_req_index = r.material_sto_req_index and " + whereString
				+ " order by t1.mtr_num, t1.shelf_life_expiration_date, t1.unfrozen_trans_time";
		}

		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}
	
	/**
	 * ��ʾ�ȴ�ʹ������List(Use By Barcode)
	 * �����ݴ����С�����Ч���ڡ��ﵽ�ⶳʱ�䡢С��������ʱ�䡢�豸���á���������������ϣ�
     * ÿ�����ϰ���������ʱ�����򣬽ⶳʱ����ͬʱ����ͬһʱ���ݴ�����Ĺ�̽�������ƿ�ɲ��е�һ�����ɽ���ȴ�ʹ���б�
	 * @param delegator
	 * @param paramMap(deptIndex, eqpId, mtrGrp, mtrNum)
	 * @param paramMap(materialSuitIndex, aliasName)
	 * @return Cabinet ForUseList
	 * @throws SQLProcessException 
	 */
	public static List queryForUseList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
		String deptIndex = (String) paramMap.get("deptIndex");
		String eqpId = (String) paramMap.get("eqpId");
		String mtrGrp = (String) paramMap.get("mtrGrp");
		String mtrNum = (String) paramMap.get("mtrNum");		
		
		String whereString = "t.material_index in (select material_index from mcs_mtr_object where dept_index="
							+ deptIndex	+ " and using_object_id = '" + eqpId + "')";		
		
		if (StringUtils.isNotEmpty(mtrGrp)) {
			whereString = whereString + " and t.mtr_grp = '" + mtrGrp + "'";
		}
		
		if (StringUtils.isNotEmpty(mtrNum)) {
			whereString = whereString + " and t.mtr_num like '" + mtrNum + "%'";
		}
				
		whereString = whereString + " and (t.shelf_life_expiration_date > sysdate or mrb_date > sysdate or t.shelf_life_expiration_date=to_date('1900-1-1','yyyy-mm-dd'))";
		whereString = whereString + " and status like '" + ConstantsMcs.CABINET + "%'";
		
		String sqlT2 = "(select t.alias_name,"
				+ "   Rank() over(partition by t.material_index order by t.unfrozen_trans_time) row_number"
				+ " from mcs_material_status t, mcs_material_info i"				
				+ " where t.material_index = i.material_index "
				+ "   and to_char(t.material_status_index) <> t.alias_name";
		
		// FAB5 ��̽�K/S��������ʾ
		if (ConstantsMcs.PHOTORESIST.equals(mtrGrp) && Constants.CALL_ASURA_FLAG) {
			sqlT2 = "(select t.alias_name,"
					+ "   Rank() over(partition by t.material_index order by t.unfrozen_trans_time) row_number"
					+ " from mcs_material_status t, mcs_material_info i, mcs_material_sto_req r"				
					+ " where t.material_index = i.material_index "
					+ "   and to_char(t.material_status_index) <> t.alias_name"
					+ "   and t.material_sto_req_index = r.material_sto_req_index";
//			if (PlldbHelper.isFab6Eqp(delegator, eqpId)) {
//				whereString = whereString + " and r.cost_center like '%S��'";
//			}
//			else
			if (PlldbHelper.isFab5Eqp(delegator, eqpId)) {
				whereString = whereString + " and r.cost_center not like '%S��'";
			}
		}
		
		if (ConstantsMcs.PHOTORESIST.equals(mtrGrp) || ConstantsMcs.CHEMICAL.equals(mtrGrp)) {
			sqlT2 = sqlT2
				+ "   and t.unfrozen_trans_time + i.frozen_time_limit/24 < sysdate"
				+ "   and sysdate - t.unfrozen_trans_time < nvl(i.max_frozen_time_limit,24000)/24";
		}
		
		sqlT2 = sqlT2 
				+ "   and " + whereString + ") t2";
		
		String sql = "select t1.material_status_index, t1.alias_name, t1.vendor_batch_num, t1.mtr_num,"
					+ " t1.mtr_desc, to_char(t1.shelf_life_expiration_date,'yyyy-mm-dd') shelf_life_expiration_date, to_char(t1.mrb_date,'yyyy-mm-dd') mrb_date,"
					+ " round((sysdate-t1.unfrozen_trans_time)*1, 2) unfrozen_time, t1.remark,"
					+ " trunc(t1.shelf_life_expiration_date-sysdate) left_exp_days"
					+ " from mcs_material_status t1, " + sqlT2
					+ " where t1.alias_name = t2.alias_name";
		
		if (ConstantsMcs.PHOTORESIST.equals(mtrGrp)) {
			sql = sql + " and t2.row_number = 1";
		}
		
		if (ConstantsMcs.PHOTORESIST.equals(mtrGrp) || ConstantsMcs.CHEMICAL.equals(mtrGrp)) {
			sql = sql + " order by t1.unfrozen_trans_time";
		} else {
			sql = sql + " order by t1.alias_name";
		}
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}
	
	/**
	 * ����״̬У�� By Id
	 * @param delegator
	 * @param paramMap {oldStatus, mtrStatusIdArray}
	 * @param i
	 * @return result
	 * @throws GenericEntityException
	 */
	public static Map checkMaterialStatusById(GenericDelegator delegator, Map paramMap) throws GenericEntityException {
		String oldStatus = (String) paramMap.get("oldStatus");
		String[] mtrStatusIdArray = (String[]) paramMap.get("mtrStatusIdArray");
		String mtrStatusIdStr = StringUtils.join(mtrStatusIdArray, ",");
		
		Map result = new HashMap();
		String errorMsg = "";
		
		String whereString = " material_status_index in (" + mtrStatusIdStr + ") ";
		if (ConstantsMcs.CABINET.equalsIgnoreCase(oldStatus)) {
			whereString = whereString + " and status not like '" + oldStatus + "%'";
		} else {			
			whereString = whereString + " and status <> '" + oldStatus + "'";
		}
				
		EntityWhereString con = new EntityWhereString(whereString);
		List errorList = delegator.findByCondition("McsMaterialStatus", con, null, null);
		
		for (Iterator it = errorList.iterator(); it.hasNext();) {
			GenericValue errorGv = (GenericValue) it.next();
			String materialStatusIndex = errorGv.getString("materialStatusIndex");
			
			errorMsg = errorMsg + materialStatusIndex + " ״̬�ѷ����仯�������²�ѯ������� ";			
		}
		
		result.put("errorMsg", errorMsg);
		return result;	
	}
    
	/**
	 * ����ʹ�û���� By Id
     * 1.�޸�״̬
     * 2.��¼��ʷ
     * 3.SAP������вĽӿ�
	 * @param delegator
	 * @param paramMap{userNo, newStatus, eqpId, ��עnote,
	 * 				mtrStatusIdArray{mtrStatusId}, ����ά�޿�λstockAddressArray(stockAddress)}
	 * @return transactionId, successMsg, errorMsg
	 * @throws GenericEntityException
	 * @throws SQLProcessException 
	 */
	public static Map changeMaterialStatusById(GenericDelegator delegator, Map paramMap) throws GenericEntityException, SQLProcessException {
		
		String userNo = (String) paramMap.get("userNo");
		String newStatus = (String) paramMap.get("newStatus");
		String actlNewStatus = newStatus;
		String eqpId = (String) paramMap.get("eqpId");
		String usingObjectId = (ConstantsMcs.USING.equalsIgnoreCase(newStatus)) ? eqpId : "";
		String note = UtilFormatOut.checkNull((String) paramMap.get("note"));
		
		String[] mtrStatusIdArray = (String[]) paramMap.get("mtrStatusIdArray");
		String[] stockAddressArray = (String[]) paramMap.get("stockAddressArray");
		
		Map result = new HashMap();
		Long transactionId = null;
		String successMsg = "";
		String errorMsg = "";
		
		int useQty = 1;
		String materialIndex = "";	//��һ����¼��materialIndex
		String msg = "";
		
		String mtrGrp = "";
		Map mtrStatusMap = new HashMap();		
		
		if (mtrStatusIdArray != null && mtrStatusIdArray.length > 0) {
			transactionId = delegator.getNextSeqId(ConstantsMcs.TRANSACTION_ID);
			Timestamp nowTs = UtilDateTime.nowTimestamp();
			List toStore = new LinkedList();
			
			for (int i = 0; i < mtrStatusIdArray.length; i++) {
				Long mtrStatusId = Long.valueOf(mtrStatusIdArray[i]);
				String stockAddress = (stockAddressArray != null) ? stockAddressArray[i] : null;				
				GenericValue gv = delegator.findByPrimaryKey("McsMaterialStatus", UtilMisc.toMap("materialStatusIndex", mtrStatusId));
				
				// (1)У���̽���������в��Ƿ���¼������
				mtrGrp = gv.getString("mtrGrp");
				String mtrNum = gv.getString("mtrNum");
				String aliasName = gv.getString("aliasName");				
				boolean isCallbackDrone = false;
				
				if (Constants.CALL_TP_FLAG) {//fab1					
					if (ConstantsMcs.TARGET.equals(mtrGrp)) {
						//return false: ��ȡ��У����sapͬ���ӿڹ���
						isCallbackDrone = CabinetHelper.isCallbackDrone(delegator, gv.getString("mtrNum"));
					}
					
					if (aliasName.indexOf("-") == -1
							&& (ConstantsMcs.PHOTORESIST.equals(mtrGrp) 
									&& !ConstantsMcs.PHOTORESIST_55CP.equals(mtrNum)
									&& !ConstantsMcs.PHOTORESIST_7510.equals(mtrNum)
								|| isCallbackDrone)) {
						errorMsg = errorMsg + aliasName + "��¼�����롣\n";
						continue;
					} else if (aliasName.indexOf("-") > 0 
							&& isCallbackDrone && isErrorDroneNum(delegator, aliasName)) {
						errorMsg = errorMsg + aliasName + "�Ѹı䣬���� ¼������ ����������ѡ��\n";
						continue;
					}
				}// end ����У��
				
				// (2)����ʹ��ʱ��У���豸�������ʹ����
				if (ConstantsMcs.USING.equalsIgnoreCase(newStatus)) {
					if (materialIndex.equals(gv.getString("materialIndex"))) {
						useQty++;
						
						if (StringUtils.isNotEmpty(msg)) {
							// ��ͬ�Ϻŵ���һ����¼�ѳ�����������������жϣ�ֱ������������һ����¼
							continue;
						}
					} else {
						// ���ñ���ʹ������
						useQty = 1;
						materialIndex = gv.getString("materialIndex");
					}						
					
					msg = checkMaxUseAmount(delegator, eqpId, materialIndex, String.valueOf(useQty));
					if (StringUtils.isNotEmpty(msg)) {//�������ʹ������������ʹ��
						errorMsg = errorMsg + msg;
						continue;
					}// end У���豸�������ʹ����
				} else if (ConstantsMcs.SCRAP.equalsIgnoreCase(newStatus) && !materialIndex.equals(gv.getString("materialIndex"))) {
					// (3)����ʱ����ѯ�����趨�Ƿ���ⱨ�ϡ��Ϻ�����һ����ͬʱ����actlNewStatus��
					materialIndex = gv.getString("materialIndex");
					actlNewStatus  = getScrapStatus(delegator, materialIndex);
	        	}
				
				//---------------------У�����----------------------
				// 1.�޸�״̬				
				mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_INDEX, mtrStatusId);
				mtrStatusMap.put("status", actlNewStatus);
				mtrStatusMap.put("usingObjectId", usingObjectId);				
				mtrStatusMap.put(ConstantsMcs.TRANSACTION_ID, transactionId);
				mtrStatusMap.put("transBy", userNo);
				mtrStatusMap.put("updateTime", nowTs);
				mtrStatusMap.put("stockAddress", stockAddress);
				
			    GenericValue mtrStatusGv = delegator.makeValidValue("McsMaterialStatus", mtrStatusMap);
			    toStore.add(mtrStatusGv);
			    
			    // 2.��¼��ʷ
			    Long mtrStatusHistId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX);
			    mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX, mtrStatusHistId);
			    mtrStatusMap.put("note", note);
			    
			    GenericValue matStatusHistGv = delegator.makeValidValue("McsMaterialStatusHist", mtrStatusMap);
			    toStore.add(matStatusHistGv);
			    
			    successMsg = successMsg + aliasName +  " ����ɹ���\n";
			    
			    // 3.Fab1 SAP������вİ�����ʹ�á��������ռ������ӿ�
			    if (isCallbackDrone) {
			    	String sapId = aliasName.substring(aliasName.indexOf("-") + 1);
			    	updateDroneCallback(delegator, userNo, actlNewStatus,
			    			usingObjectId, note, sapId);
			    }			    
			} // end for
			
			// 4.fab5���� �вİ�barcodeʹ�ã����ϳɹ��� ������PM���� ������������MCS���ء��ϼ
		    if (ConstantsMcs.TARGET.equals(mtrGrp) && ConstantsMcs.USING.equalsIgnoreCase(newStatus) && StringUtils.isNotEmpty(successMsg)) {
		    	mtrStatusMap.put("enabled", ConstantsMcs.INTEGER_1);
		    	mtrStatusMap.put("enabledType", "MCS");
		    	GenericValue changeTargetGv = delegator.makeValidValue("McsPmChangeTarget", mtrStatusMap);
			    toStore.add(changeTargetGv);
		    }
			
			delegator.storeAll(toStore);
			result.put("transactionId", transactionId);
			result.put("successMsg", successMsg);
			result.put("errorMsg", errorMsg);			
		} // end if
		
		return result;
	}

	/**
     * Fab1 SAP������вİ�����ʹ�á��������ռ������ӿ�
     * ͬ��SAP�б�ϵͳ��¼
	 * @param delegator
	 * @param userNo
	 * @param actlNewStatus
	 * @param usingObjectId
	 * @param note
	 * @param sapId
	 * @return void
	 * @throws SQLProcessException 
	 */
	private static void updateDroneCallback(GenericDelegator delegator,
			String userNo, String actlNewStatus, String usingObjectId,
			String note, String sapId) throws SQLProcessException {
		
		if (StringUtils.isNotEmpty(sapId)) {
			String drone_sql = "update mis_DRONE_CALLBACK_RK set";
			
			if (ConstantsMcs.USING.equalsIgnoreCase(actlNewStatus)) {
				drone_sql += " EQUIP_ID ='"
					+ usingObjectId +"',USE_BEIZHU ='"+ note +"', RK_FLAG ='ʹ��', USE_HRID = '"
					+ userNo +"' ,USE_NAME =(select account_name from account where account_no ='"
					+ userNo + "'),USE_DATE = sysdate";
				
			} else if (ConstantsMcs.CABINET_RECYCLE.equalsIgnoreCase(actlNewStatus)) {
				drone_sql += " RECALL_USE_BEIZHU ='"+ note +"', RK_FLAG ='���', RECALL_USE_HRID = '"
					+ userNo +"' ,RECALL_USE_NAME =(select account_name from account where account_no ='"
					+ userNo + "'),RECALL_USE_DATE = sysdate";
				
			} else if (ConstantsMcs.GENERAL_SCRAP_OPT.equalsIgnoreCase(actlNewStatus)
					|| ConstantsMcs.ONLINE_SCRAP_OPT.equalsIgnoreCase(actlNewStatus)) {
				drone_sql += " CALLBACK_ZHUBEI ='"+ note +"', RK_FLAG ='����-����', CALLBACK_HRID = '"
					+ userNo +"' ,CALLBACK_NAME =(select account_name from account where account_no ='"
					+ userNo + "'),CALLBACK_DATE = sysdate";
				
			} else if (ConstantsMcs.VENDOR_REPAIR_OPT.equalsIgnoreCase(actlNewStatus)) {
				drone_sql += " CALLBACK_ZHUBEI ='"+ note +"', RK_FLAG ='����', CALLBACK_HRID = '"
					+ userNo +"' ,CALLBACK_NAME =(select account_name from account where account_no ='"
					+ userNo + "'),CALLBACK_DATE = sysdate";
			} else {
				return;
			}
			
			drone_sql += " where num = '" + sapId + "'";
			SQLProcess.excuteSQLUpdate(drone_sql, delegator);
		}
	}
	
	/**
     * У��б��ţ��ж��Ƿ���SAP�����޸�
	 * @param delegator
	 * @param aliasName
	 * @return boolean
	 * @throws SQLProcessException 
	 */
	private static boolean isErrorDroneNum(GenericDelegator delegator, String aliasName) throws SQLProcessException {
		String deone_id = aliasName.substring(0, aliasName.indexOf("-"));
		String num = aliasName.substring(aliasName.indexOf("-")+1);
		
		StringBuffer sql = new StringBuffer();
		sql.append("select upper(deone_id)||'-'||num deone_id_num");
		sql.append(" from mis_DRONE_CALLBACK_RK");
		sql.append(" where upper(deone_id)='").append(deone_id).append("'");
		sql.append(" and num='").append(num).append("'");
		
		List list = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		if (list != null && list.size() > 0) {
			return false;
		} else {
			return true;
		}		
	}

	/**
     * ��ʾ�豸��������List(Use By Qty)
	 * @param delegator
	 * @param eqpId
	 * @param mtrGrp
	 * @param mtrNum
	 * @return Eqp Material List
	 * @throws GenericEntityException
	 * @throws SQLProcessException 
	 */
	public static List queryEqpMaterialListByQty(GenericDelegator delegator, String userDeptIndex, String eqpId, String mtrGrp, String mtrNum) throws SQLProcessException {
		String sql = "select t.vendor_batch_num, t.material_index, t.mtr_num, t.mtr_desc,"
				+ " t.shelf_life_expiration_date, t.mrb_date, count(*) qty,"
				+ " round(sysdate-min(t.update_time)) max_use_period,"
				+ " min(m.usable_time_limit) usable_time_limit"
				+ " from mcs_material_status t, mcs_material_info m" 
				+ " where t.material_index=m.material_index and t.status = '" + ConstantsMcs.USING + "'"
				+ " and t.dept_index=" + userDeptIndex
				+ " and t.using_object_id = '" + eqpId + "'";
		
		if (StringUtils.isNotEmpty(mtrGrp)) {
			sql = sql + " and t.mtr_grp='" + mtrGrp + "'";
		}
		
		if (StringUtils.isNotEmpty(mtrNum)) {
			sql = sql + " and t.mtr_num like '" + mtrNum + "%'";
		}
		
		sql = sql + " group by t.vendor_batch_num,t.material_index,t.mtr_num,t.mtr_desc,t.shelf_life_expiration_date,t.mrb_date"
				+ " order by t.mtr_num, t.shelf_life_expiration_date, t.mrb_date";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);		
		return list;		
	}
	
	/**
	 * ��ʾ�ݴ�����������List(Use By Qty)
	 * @param delegator
	 * @param eqpId
	 * @param mtrGrp
	 * @param userDeptIndex
	 * @return Cabinet Material List
	 * @throws GenericEntityException
	 */
	public static List queryCabinetMaterialListByQty(GenericDelegator delegator, String userDeptIndex, String eqpId, String mtrGrp, String mtrNum) throws SQLProcessException {
		String sql = "select t.vendor_batch_num, t.material_index, t.mtr_num, t.mtr_desc,"
				+ " t.shelf_life_expiration_date, t.mrb_date, count(*) qty"
				+ " from mcs_material_status t" 
				+ " where t.material_index in"
				+ " (select material_index from mcs_mtr_object"
				+ " where dept_index=" + userDeptIndex	+ " and using_object_id = '" + eqpId + "')"
				+ " and (t.shelf_life_expiration_date > sysdate or t.mrb_date > sysdate"
				+ "     or t.shelf_life_expiration_date = to_date('1900-1-1', 'yyyy-mm-dd'))"
				+ " and t.status like '" + ConstantsMcs.CABINET + "%'";
	
		if (StringUtils.isNotEmpty(mtrGrp)) {
			sql = sql + " and t.mtr_grp='" + mtrGrp + "'";
		}
		
		if (StringUtils.isNotEmpty(mtrNum)) {
			sql = sql + " and t.mtr_num like '" + mtrNum + "%'";
		}
		
		sql = sql + " group by t.vendor_batch_num,t.material_index,t.mtr_num,t.mtr_desc,t.shelf_life_expiration_date,t.mrb_date"
				+ " order by t.mtr_num, t.shelf_life_expiration_date, t.mrb_date";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}

	/**
	 * ����ʹ�û���� By Qty
     * 1.У�鲢�޸�����״̬
     * 2.��¼��ʷ
	 * @param delegator
	 * @param paramMap {userNo, deptIndex, oldStatus, newStatus, eqpId, note,
	 * qtyArray, totalQtyArray, vendorBatchNumArray, materialIndexArray, mtrNumArray, mtrDescArray, shelfLifeExpirationDateArray, mrbDateArray
	 * eventType, eventIndex, eqpIdPms}
	 * @return transactionId, successMsg, errorMsg
	 * @throws GenericEntityException
	 * @throws SQLProcessException 
	 */
	public static Map changeMaterialStatusByQty(GenericDelegator delegator, Map paramMap) throws GenericEntityException, SQLProcessException {
		String userNo = (String) paramMap.get("userNo");
		//String deptIndex = (String) paramMap.get("deptIndex");
		//String oldStatus = (String) paramMap.get("oldStatus");
		String newStatus = (String) paramMap.get("newStatus");
		String actlNewStatus = newStatus;
		String eqpId = (String) paramMap.get("eqpId");
		String usingObjectId = (ConstantsMcs.USING.equalsIgnoreCase(newStatus)) ? eqpId : "";
		String note = UtilFormatOut.checkNull((String) paramMap.get("note"));
		
		String[] qtyArray = (String[]) paramMap.get("qtyArray");
		//String[] totalQtyArray = (String[]) paramMap.get("totalQtyArray");
		String[] vendorBatchNumArray = (String[]) paramMap.get("vendorBatchNumArray");
		String[] materialIndexArray = (String[]) paramMap.get("materialIndexArray");
		String[] mtrNumArray = (String[]) paramMap.get("mtrNumArray");
		String[] mtrDescArray = (String[]) paramMap.get("mtrDescArray");
		String[] shelfLifeExpirationDateArray = (String[]) paramMap.get("shelfLifeExpirationDateArray");
		String[] mrbDateArray = (String[]) paramMap.get("mrbDateArray");
		
		Map result = new HashMap();
		Long transactionId = null;
		String successMsg = "";
		String errorMsg = "";
		
		if (qtyArray != null && qtyArray.length > 0) {				
			transactionId = delegator.getNextSeqId(ConstantsMcs.TRANSACTION_ID);
			Timestamp nowTs = UtilDateTime.nowTimestamp();
			List toStore = new LinkedList();
			
			for (int i = 0; i < qtyArray.length; i++) {
				String qty = qtyArray[i];
				//String totalQty = totalQtyArray[i];
				String vendorBatchNum = vendorBatchNumArray[i];
				String materialIndex = (materialIndexArray != null) ? materialIndexArray[i] : null;
				String mtrNum = mtrNumArray[i];
				String mtrDesc = (mtrDescArray != null) ? mtrDescArray[i] : null;
				String shelfLifeExpirationDate = shelfLifeExpirationDateArray[i];
				String mrbDate = mrbDateArray[i];
				
				// ����ǰ������У��
				List statusList = getValidStatusListByQty(delegator, paramMap, i);
				
				if (statusList != null && statusList.size() > 0) {
					
					if (ConstantsMcs.USING.equalsIgnoreCase(newStatus)) {
						// ����ʹ��ʱ��У���豸�������ʹ����
						String msg = checkMaxUseAmount(delegator, eqpId, materialIndex, qty);
						if (StringUtils.isNotEmpty(msg)) {//�������ʹ����������������һ��
							errorMsg = errorMsg + msg;
							continue;
						}
						
						// pms interface: pm/ts using parts
						// save PartsUse & McsPartsUse
						String eventType = (String) paramMap.get("eventType");
						if (StringUtils.isNotEmpty(eventType)) {
							Long partsUseSeqIndex = savePartsUseByMcs(
									delegator, paramMap, transactionId, nowTs, 
									qty, mtrNum, mtrDesc);
							
							for (Iterator it = statusList.iterator(); it.hasNext();) {
								GenericValue gv = (GenericValue) it.next();								
								Map mcsPartsUseMap = new HashMap(gv);
								mcsPartsUseMap.put("partsUseSeqIndex", partsUseSeqIndex);
								
								GenericValue mcsPartsUseGv = delegator.makeValidValue("McsPartsUse", mcsPartsUseMap);
								//toStore.add(mcsPartsUseGv);
								delegator.create(mcsPartsUseGv);
							}
						} // end mcs interface
						
					} else if (ConstantsMcs.SCRAP.equalsIgnoreCase(newStatus)) {
						// ����ʱ����ѯ�����趨����״̬
		        		actlNewStatus  = getScrapStatus(delegator, materialIndex);
		        	}
					
					//����status��hist
					updateStatusAndHistToStoreList(delegator, toStore,
							statusList, userNo, actlNewStatus, usingObjectId,
							note, transactionId, nowTs);
					
					successMsg = successMsg + vendorBatchNum + "," 
								+ mtrNum + " (" + qty + ")," 
								+ shelfLifeExpirationDate + "," 
								+ mrbDate + " ����ɹ��� ";
					
				} else {
					errorMsg = errorMsg + vendorBatchNum + "," 
							+ mtrNum + " (" + qty + ")," 
							+ shelfLifeExpirationDate + "," 
							+ mrbDate + " �����ѷ����仯�������²����� ";
				}
				
			} // end for qtyArray
			
			delegator.storeAll(toStore);
			result.put("transactionId", transactionId);
			result.put("successMsg", successMsg);
			result.put("errorMsg", errorMsg);
		} // end qtyArray if
		
		return result;
	}

	public static Long savePartsUseByMcs(GenericDelegator delegator,
			Map paramMap, Long transactionId, Timestamp nowTs,
			String qty, String mtrNum, String mtrDesc)
			throws GenericEntityException {
		Map partsUseMap = new HashMap();
		partsUseMap.put("partType", "DATA");
		partsUseMap.put("eventType", paramMap.get("eventType"));
		partsUseMap.put("eventIndex", paramMap.get("eventIndex"));
		partsUseMap.put("flowIndex", paramMap.get("flowIndex"));		
		partsUseMap.put("deptIndex", paramMap.get("deptIndex"));
		partsUseMap.put("transBy", paramMap.get("userNo"));
		partsUseMap.put("updateTime", nowTs);
		partsUseMap.put("remark", paramMap.get("note"));
		
		partsUseMap.put("mtrGrp", paramMap.get("mtrGrp"));
		partsUseMap.put("partNo", mtrNum);
		partsUseMap.put("partName", mtrDesc);
		partsUseMap.put("partCount", qty);
		partsUseMap.put("partsRequireIndex", transactionId);
		
		Long partsUseSeqIndex = delegator.getNextSeqId("partsUseSeqIndex");
		partsUseMap.put("seqIndex", partsUseSeqIndex);		
		GenericValue partsUseGv = delegator.makeValidValue("PartsUse", partsUseMap);							
		//toStore.add(partsUseGv);
		delegator.create(partsUseGv);
		return partsUseSeqIndex;
	}

	public static void updateStatusAndHistToStoreList(
			GenericDelegator delegator, List toStore, List statusList,
			String userNo, String actlNewStatus, String usingObjectId,
			String note, Long transactionId, Timestamp nowTs) {
		for (Iterator it = statusList.iterator(); it.hasNext();) {
			// 1.�޸�״̬
			GenericValue gv = (GenericValue) it.next();
			
			Map mtrStatusMap = new HashMap(gv);
			mtrStatusMap.put("status", actlNewStatus);
			mtrStatusMap.put("usingObjectId", usingObjectId);
			mtrStatusMap.put(ConstantsMcs.TRANSACTION_ID, transactionId);
			mtrStatusMap.put("transBy", userNo);
			mtrStatusMap.put("updateTime", nowTs);
			
			GenericValue mtrStatusGv = delegator.makeValidValue("McsMaterialStatus", mtrStatusMap);
			toStore.add(mtrStatusGv);
			
			// 2.��¼��ʷ
			Long mtrStatusHistId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX);
			mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX, mtrStatusHistId);
			mtrStatusMap.put("note", note);
		    
		    GenericValue mtrStatusHistGv = delegator.makeValidValue("McsMaterialStatusHist", mtrStatusMap);
		    toStore.add(mtrStatusHistGv);
		} // end for statusList
	}

	/**
	 * ��������У�� By Qty, ����ID List
	 * @param delegator
	 * @param paramMap {userNo, deptIndex, oldStatus, newStatus, eqpId,  
	 * qtyArray, totalQtyArray, vendorBatchNumArray, mtrNumArray, shelfLifeExpirationDateArray, mrbDateArray}
	 * @param i, array index
	 * @return Valid Material Status List
	 * @throws GenericEntityException
	 */
	private static List getValidStatusListByQty(GenericDelegator delegator, Map paramMap, int i) throws GenericEntityException {
		String deptIndex = (String) paramMap.get("deptIndex");
		String oldStatus = (String) paramMap.get("oldStatus");
		String eqpId = (String) paramMap.get("eqpId");
		
		String[] qtyArray = (String[]) paramMap.get("qtyArray");
		String[] totalQtyArray = (String[]) paramMap.get("totalQtyArray");
		String[] vendorBatchNumArray = (String[]) paramMap.get("vendorBatchNumArray");
		String[] mtrNumArray = (String[]) paramMap.get("mtrNumArray");
		String[] shelfLifeExpirationDateArray = (String[]) paramMap.get("shelfLifeExpirationDateArray");
		String[] mrbDateArray = (String[]) paramMap.get("mrbDateArray");
		
		int qty = Integer.parseInt(qtyArray[i]);
		int totalQty = Integer.parseInt(totalQtyArray[i]);
		String vendorBatchNum = vendorBatchNumArray[i];
		String mtrNum = mtrNumArray[i];
		String shelfLifeExpirationDate = shelfLifeExpirationDateArray[i];
		String mrbDate = mrbDateArray[i];
		
		String whereString = "dept_index=" + deptIndex 
							+ " and vendor_batch_num = '" + vendorBatchNum + "'"
							+ " and mtr_num = '" + mtrNum + "'"
							+ " and shelf_life_expiration_date = to_date('" + shelfLifeExpirationDate + "','yyyy-mm-dd')";
		
		if (StringUtils.isNotEmpty(mrbDate)) {
			whereString = whereString + " and mrb_date = to_date('" + mrbDate.substring(0, 10) + "','yyyy-mm-dd')";
		} else {
			whereString = whereString + " and mrb_date is null";
		}
		
		if (ConstantsMcs.CABINET.equalsIgnoreCase(oldStatus)) {
			// ��״̬ΪUSING��oldStatusΪCABINET-NEW��CABINET-RECYCLE
			whereString = whereString + " and status like '" + oldStatus + "%'";
			whereString = whereString + " and using_object_id is null";
		} else {			
			whereString = whereString + " and status = '" + oldStatus + "'";			
			if (StringUtils.isNotEmpty(eqpId)) {
				// oldStatusΪUSINGʱ��eqpId��ֵ
				whereString = whereString + " and using_object_id = '" + eqpId + "'";
			}
		}			

		EntityWhereString con = new EntityWhereString(whereString);
		List list = delegator.findByCondition("McsMaterialStatus", con, null, UtilMisc.toList("materialStatusIndex"));
		
		if (list.size() == totalQty && qty <= totalQty) {			
			return list.subList(0, qty);
		} else {
			return null;
		}
		
	}
	
	/**
	 * �޸�����״̬����
	 * ����Service1 changeMaterialStatusById. (Use By ID/SUIT, BY BARCODE, �ڲ�ά��, ���������ά�޿γ�ȷ��, ���������ά�޲ֿ�ȷ��)
	 * ����Service2 changeMaterialStatusByQty.(Use By QTY & Cabinet Back Stock)
	 * @param request
	 * @param dispatcher
	 * @param serviceName(changeMaterialStatusById or changeMaterialStatusByQty)
	 * @param paramMap1 by id  {userNo, eqpId, oldStatus, newStatus, mtrStatusIdArray)  
	 * @param paramMap2 by qty {userNo, deptIndex, oldStatus, newStatus, eqpId,  
	 * qtyArray, totalQtyArray, vendorBatchNumArray, materialIndexArray, mtrNumArray, shelfLifeExpirationDateArray, mrbDateArray}
	 * @return 
	 * @throws 
	 */
	public static void modifyMtrStatusByService(HttpServletRequest request,
			LocalDispatcher dispatcher, String serviceName, Map paramMap) {
		try {
			Map result = dispatcher.runSync(serviceName, UtilMisc.toMap("paramMap", paramMap));
			// throw exception
			if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
				throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
			}
			
			Map returnMap = (Map) result.get("returnMap");
			Long transactionId = (Long) returnMap.get("transactionId");
			String successMsg = (String) returnMap.get("successMsg");
			String errorMsg = (String) returnMap.get("errorMsg");
						
			if (StringUtils.isNotEmpty(successMsg)) {
				request.setAttribute("transactionId", transactionId.toString());
				request.setAttribute("_EVENT_MESSAGE_", 
						UtilFormatOut.checkNull((String) request.getAttribute("_EVENT_MESSAGE_"))
						+ " " + paramMap.get("newStatus") + " :\n" + successMsg);
			}
			
			if (StringUtils.isNotEmpty(errorMsg)) {
				request.setAttribute("_ERROR_MESSAGE_", errorMsg);
			}
		} catch (GenericServiceException e) {
	    	String message = CommonUtil.checkOracleException(e);
	    	Debug.logError(e.getMessage(), message); 
	    	request.setAttribute("_ERROR_MESSAGE_", message);
		}
	}
	
	/**
	 * ����ʹ��ʱ����ѯ�豸��ǰʹ������ + ���λ�������useQty �Ƿ���� �豸������������ʹ������������Ϊ�Ƿ�ʹ��
	 * @param delegator
	 * @param eqpId
	 * @param materialIndex	 
	 * @param useQty
	 * @return error message
	 * @throws SQLProcessException 
	 */
	private static String checkMaxUseAmount(GenericDelegator delegator,
			String eqpId, String materialIndex, String useQty) throws SQLProcessException {
		String msg = "";
		
		String sql = "select t2.mtr_num, t2.obj_max_use_amount, t1.eqp_qty from mcs_mtr_object t2,"
					+ "(select count(*) eqp_qty from mcs_material_status"
					+ " where using_object_id = '" + eqpId + "'"
					+ " and material_index = " + materialIndex
					+ " and status = '" + ConstantsMcs.USING + "') t1"
					+ " where t2.using_object_id = '" + eqpId + "' and t2.material_index = " + materialIndex;
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		if (list != null && list.size() > 0) {
			Map map = (Map) list.get(0);
			String mtrNum = (String) map.get("MTR_NUM");
			String objMaxUseAmount = (String) map.get("OBJ_MAX_USE_AMOUNT");
			String eqpQty = (String) map.get("EQP_QTY");
			
			if (StringUtils.isNotEmpty(objMaxUseAmount)
					&& Long.parseLong(eqpQty) + Long.parseLong(useQty) > Long.parseLong(objMaxUseAmount)) {
				msg = mtrNum + " �豸����(" + eqpQty + ") + ����ʹ��(" + useQty
						+ ")ʱ���ѳ����豸���ʹ������(" + objMaxUseAmount
						+ ")��������ѡ��ʹ������ �� �޸����ϻ������ϡ�\n";
			}
		}
		
		return msg;
	}
	
	/**
	 * ���±���ʱ����ѯ����ʵ�ʱ���״̬
	 * @param delegator
	 * @param materialIndex
	 * @return GENERAL-SCRAP-OPT or ONLINE-SCRAP-OPT
	 * @throws GenericEntityException
	 */
	private static String getScrapStatus(GenericDelegator delegator, String materialIndex) throws GenericEntityException {
		String scrapStatus = null;
		
		GenericValue gv = delegator.findByPrimaryKey("McsMaterialInfo", UtilMisc.toMap("materialIndex", materialIndex));
		if (gv != null && ConstantsMcs.Y.equals(gv.getString("needScrapStore"))) {
			// ��ⱨ��
			scrapStatus = ConstantsMcs.GENERAL_SCRAP_OPT;
		} else {
			// ���߱���
			scrapStatus = ConstantsMcs.ONLINE_SCRAP_OPT;
		}
		
		return scrapStatus;
	}
	
	public static long getMinStoIndex(GenericDelegator delegator) throws SQLProcessException {
		long minSeq = 0;
		String sql = "SELECT Min(MATERIAL_STO_REQ_INDEX) STO_INDEX FROM MCS_MATERIAL_STO_REQ";
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		if (list!=null&&list.size()>0) {
			Map stoMap = (Map) list.get(0);
			String stoSeq = (String) stoMap.get("STO_INDEX");
			if (stoSeq!=null) {
				minSeq = Long.parseLong(stoSeq) - 1;
			}
		}
		return minSeq;
	}
	
	/**
     * ��ѯʯӢ��ʷʹ���б�
     * @param delegator
     * @param param
     */    
	public static List queryAliasNameList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
    	String mtrNum = (String) paramMap.get("mtrNum");
    	String mtrGrp = (String) paramMap.get("mtrGrp");
    	String userDeptIndex = (String) paramMap.get("deptIndex");
		
    	String sql = "SELECT S.MATERIAL_STATUS_INDEX,S.MTR_NUM,S.MTR_GRP,S.USING_OBJECT_ID,S.ALIAS_NAME,S.SHELF_LIFE_EXPIRATION_DATE FROM MCS_MATERIAL_STATUS S " +
                     " WHERE S.MTR_NUM='" + mtrNum + "' AND S.MTR_GRP='" + mtrGrp + "' AND S.DEPT_INDEX='" + userDeptIndex + 
                     "' AND S.STATUS='USING' AND VENDOR_BATCH_NUM='0' ORDER BY S.UPDATE_TIME DESC";
    	List list = SQLProcess.excuteSQLQuery(sql, delegator);
    	return list;    	

	}
	
	/**
	 * 24Сʱ�������һ�λ��ϵĹ�̽�
	 * @param delegator
	 * @param eqpId
	 * @return String lastPrUsed
	 * @throws SQLProcessException 
	 */
	public static String getLastPrUsed(GenericDelegator delegator,	String eqpId) throws SQLProcessException {
		String lastPrUsed = "";
		
		String sql = "select mtr_num from mcs_material_status"
					+ " where using_object_id = '" + eqpId + "'"
					+ " and update_time > sysdate-1"
					+ " and status = '" + ConstantsMcs.USING + "'"
					+ " and mtr_grp = '" + ConstantsMcs.PHOTORESIST + "'"
					+ " order by update_time desc";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		if (list.size() > 0) {
			Map map = (Map) list.get(0);
			lastPrUsed = (String) map.get("MTR_NUM");			
		}
		
		return lastPrUsed;
	}
	
	/**
	 * ����ɨ��ǹ15������ɨ��Ĺ�̽�
	 * @param delegator
	 * @param eqpId
	 * @return list scanBarcodeList
	 * @throws SQLProcessException 
	 */
	public static List queryScanBarcodeList(GenericDelegator delegator,	String eqpId) throws SQLProcessException {		
		String sql = "select * from mcs_scan_barcode"
					+ " where equipment_id = '" + eqpId + "'"
					+ " and update_time > sysdate-1/96";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);		
		return list;
	}

	public static java.util.Date toShelfLifeExpirationDate(String date) {
	    if (date == null) return null;
	    String month;
	    String day;
	    String year;
	
	    int dateSlash1 = date.indexOf("-");
	    int dateSlash2 = date.lastIndexOf("-");
	
	    if (dateSlash1 <= 0 || dateSlash1 == dateSlash2) return null;
	    
	    year = date.substring(0, dateSlash1);
	    month = date.substring(dateSlash1 + 1, dateSlash2);
	    day = date.substring(dateSlash2 + 1);
	
	    return UtilDateTime.toSqlDate(month, day, year);
	}
}