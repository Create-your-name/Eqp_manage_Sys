package com.csmc.mcs.webapp.ship.helper;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;

/**
 * 类 ShipHelper.java 
 * @version  1.0  2009-9-1
 * @author   
 */

public class ShipHelper {
	public static final String module = ShipHelper.class.getName();
	
	/**
     * 显示报废与出厂维修List(课长确认 与 仓库确认)
     * 20161215取最近1000条记录，避免显示过多数据
	 * @param delegator
	 * @param paramMap(status, deptIndex, mtrGrp, mtrNum)
	 * @return scrapVendorList
	 * @throws GenericEntityException
	 */
	public static List queryScrapVendorConfirmList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
		String status = (String) paramMap.get("status");
		String deptIndex = (String) paramMap.get("deptIndex");
		String mtrGrp = (String) paramMap.get("mtrGrp");
		String mtrNum = (String) paramMap.get("mtrNum");
		String accountSection = (String) paramMap.get("accountSection");
		
		String sql = "select t.*,t1.moving_average_price,t2.account_no,t2.account_name"
			+ " from mcs_material_status t, mcs_sap_mtr_table t1, account t2"
			+ " where t.mtr_num = t1.mtr_num(+) and t.trans_by = t2.account_no(+) and t.status = '" + status + "'";
		
		if (StringUtils.isNotEmpty(mtrGrp)) {
			sql = sql + " and t.mtr_grp = '" + mtrGrp + "'";
		}
		
		if (StringUtils.isNotEmpty(mtrNum)) {
			sql = sql + " and t.mtr_num like '" + mtrNum + "'";
		}

		if (StringUtils.isNotEmpty(deptIndex)) {
			sql = sql + " and t2.dept_index = '" + deptIndex + "'";
		}
		
		if (StringUtils.isNotEmpty(accountSection)) {
			sql = sql + " and t2.account_section = '" + accountSection + "'";
		}
		
		sql = sql + " order by t.update_time desc";

		String sql1 = "select * from (" + sql + ") where rownum <= 1000";
		List scrapVendorList = SQLProcess.excuteSQLQuery(sql1, delegator);
		return scrapVendorList;
	}
}
