package com.csmc.mcs.webapp.expiration.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.GenericDelegator;

import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;

/**
 * 类 ExpirationHelper.java 
 * @version  1.0  2009-7-16
 * @author   jiyw
 */
public class ExpirationHelper {

    public static final String module = ExpirationHelper.class.getName();
    
    /**
     * 显示到用户选择的截止日期前为止超有效期物料List
	 * @param delegator
	 * @param deptIndex
	 * @param mtrGrp
	 * @param vendorBatchNum
	 * @param startDate
     * @throws SQLProcessException 
	 */
	public static List queryOverExpirationList(GenericDelegator delegator, String deptIndex, String mtrGrp, String vendorBatchNum, String startDate) throws SQLProcessException {
		List materialList = new ArrayList();
		String sql = "select t.alias_name, t.vendor_batch_num, t.mtr_num, t.mtr_desc, t.mrb_id,"
					+" to_char(t.shelf_life_expiration_date,'yyyy-MM-dd') shelf_life_expiration_date,"
					+" to_char(t.mrb_date,'yyyy-MM-dd') mrb_date,"
					+" t.material_status_index, t.status, t.using_object_id,"
					+" trunc(nvl(t.mrb_date,t.shelf_life_expiration_date)-sysdate) left_exp_days"
					+" from mcs_material_status t"
					+" where shelf_life_expiration_date <> to_date('1900-1-1', 'yyyy-MM-dd')";					
		
		if (StringUtils.isNotEmpty(startDate)) {
		    sql = sql + " and nvl(mrb_date, shelf_life_expiration_date) < to_date('"+startDate+"', 'yyyy-MM-dd')";
		} else {
			sql = sql + " and nvl(mrb_date, shelf_life_expiration_date) < sysdate";
		}
		
		if (StringUtils.isNotEmpty(deptIndex)) {
			sql = sql + " and t.dept_index = '" + deptIndex + "'";
		}
		
		if (StringUtils.isNotEmpty(mtrGrp)) {
			sql = sql + " and t.mtr_Grp = '" + mtrGrp + "'";
		}
		
		if (StringUtils.isNotEmpty(vendorBatchNum)) {
		    sql = sql + " and t.vendor_batch_num = '" + vendorBatchNum + "'";
		}
		
		sql = sql + " and t.status in ('" + ConstantsMcs.CABINET_NEW + "','"
				+ ConstantsMcs.CABINET_RECYCLE + "','" + ConstantsMcs.USING
				+ "','" + ConstantsMcs.FAB_REPAIR + "')";
		
		sql = sql + " order by t.shelf_life_expiration_date, t.vendor_batch_num, t.alias_name";
		
		materialList = SQLProcess.excuteSQLQuery(sql, delegator);			
		return materialList;
	}
	
	
	/**
	 * 从string得到Date
	 * @param mrbDate
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String mrbDate) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date =  sdf.parse(mrbDate); 
		return date;
	}
}