package com.csmc.mcs.webapp.common.helper;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityWhereString;

import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;

/**
 * 类 McsCommonHelper.java 
 * @version  1.0  2009-7-10
 * @author   dinghh
 */
public class McsCommonHelper {
	public static final String module = McsCommonHelper.class.getName();

	/**
	 * 显示物料Id GenericValue List
	 * @param delegator
	 * @param paramMap(status, deptIndex, mtrGrp, mtrNum, vendorBatchNum)
	 * @return material gv List
	 * @throws GenericEntityException
	 */
	public static List queryMaterialGvList(GenericDelegator delegator, Map paramMap) throws GenericEntityException {
		String status = (String) paramMap.get("status");
		String deptIndex = (String) paramMap.get("deptIndex");
		String mtrGrp = (String) paramMap.get("mtrGrp");
		String mtrNum = (String) paramMap.get("mtrNum");
		String vendorBatchNum = (String) paramMap.get("vendorBatchNum");
		
		String whereString = "1=1";
		
		if (StringUtils.isNotEmpty(status)) {
			whereString = whereString + " and dept_index = '" + deptIndex + "'";
		}
		
		if (StringUtils.isNotEmpty(status)) {
			whereString = whereString + " and status = '" + status + "'";
		}
		
		if (StringUtils.isNotEmpty(mtrGrp)) {
			whereString = whereString + " and mtr_grp = '" + mtrGrp + "'";
		}
		
		if (StringUtils.isNotEmpty(mtrNum)) {//料号模糊查询
			whereString = whereString + " and mtr_num like '" + mtrNum + "%'";
		}
		
		if (StringUtils.isNotEmpty(vendorBatchNum)) {
			whereString = whereString + " and vendor_batch_num = '" + vendorBatchNum + "'";
		}
	
		EntityWhereString con = new EntityWhereString(whereString);
		List materialList = delegator.findByCondition("McsMaterialStatus", con, null, UtilMisc.toList("mtrNum", "materialStatusIndex"));
		return materialList;
	}
	
	/**
	 * 显示物料Id Map List
	 * @param delegator
	 * @param paramMap(status, deptIndex, mtrGrp, mtrNum, vendorBatchNum, whereString)
	 * @return material Map List
	 * @throws SQLProcessException 
	 */
	public static List queryMaterialMapList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
		String status = (String) paramMap.get("status");
		String deptIndex = (String) paramMap.get("deptIndex");
		String mtrGrp = (String) paramMap.get("mtrGrp");
		String mtrNum = (String) paramMap.get("mtrNum");
		String vendorBatchNum = (String) paramMap.get("vendorBatchNum");
		
		String whereString = (String) paramMap.get("whereString");
		if (StringUtils.isEmpty(whereString)) {
			whereString = "1=1";
		}			
		
		if (StringUtils.isNotEmpty(status)) {
			whereString = whereString + " and status = '" + status + "'";
		}
		
		if (StringUtils.isNotEmpty(status)) {
			whereString = whereString + " and dept_index = '" + deptIndex + "'";
		}		
		
		if (StringUtils.isNotEmpty(mtrGrp)) {
			whereString = whereString + " and mtr_grp = '" + mtrGrp + "'";
		}
		
		if (StringUtils.isNotEmpty(mtrNum)) {//料号模糊查询
			whereString = whereString + " and mtr_num like '" + mtrNum + "%'";
		}
		
		if (StringUtils.isNotEmpty(vendorBatchNum)) {
			whereString = whereString + " and vendor_batch_num = '" + vendorBatchNum + "'";
		}
	
		String sql = "select t.material_status_index, t.alias_name,"
			+ " t.vendor_batch_num, t.mtr_num, t.mtr_desc,"
			+ " to_char(t.shelf_life_expiration_date, 'yyyy-mm-dd') shelf_life_expiration_date,"
			+ " to_char(t.mrb_date, 'yyyy-mm-dd') mrb_date, t.update_time"
			+ " from mcs_material_status t" 
			+ " where " + whereString
			+ " order by t.mtr_num, t.material_status_index";
		
		List materialList = SQLProcess.excuteSQLQuery(sql, delegator);
		return materialList;
	}

	//F5生产制造部，不分module组别，按使用部门查询所有设备
	//F1生产制造部各组别deptIndex，与module相同，按deptIndex查询设备
	public static Map getEquipmentFiledsParam(GenericDelegator delegator,
			String deptIndex) throws GenericEntityException {
		Map map = new HashMap();
		
		if (StringUtils.isNotEmpty(deptIndex)) {
			GenericValue deptGv = delegator.findByPrimaryKey("EquipmentDept", UtilMisc.toMap("deptIndex", deptIndex));
			if (ConstantsMcs.DEPT_PD.equals(deptGv.getString("equipmentDept"))
					|| ConstantsMcs.DEPT_PF.equals(deptGv.getString("equipmentDept"))) {
				map.put("useDept", ConstantsMcs.DEPT_PD);
			} else {				
				map.put("deptIndex", deptIndex);								
			}
		}
		
		return map;
	}
}