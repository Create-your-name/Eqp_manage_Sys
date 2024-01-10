package com.csmc.mcs.webapp.report.helper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;

import com.csmc.mcs.webapp.basic.helper.BasicHelper;
import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;

/**
 * 类 ReportEvent.java 
 * @version  1.0  2009-8-17
 * @author   wanggq
 */

public class ReportHelper {
    public static final String module = BasicHelper.class.getName();
	    
	/**
	  * 领料单查询
	  * @param delegator
	  * @param param
	  * @throws GenericEntityException,SQLProcessException 
	  */
	public static List getStoReqList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
		String startDate =(String) paramMap.get("startDate"); 			
		String endDate =(String) paramMap.get("endDate");	
		String deptIndex = (String) paramMap.get("deptIndex");		
		String vendorBatchNum = (String) paramMap.get("vendorBatchNum");			
		String mtrGrp =(String) paramMap.get("mtrGrp");				
		String mtrNum =(String) paramMap.get("mtrNum");	
		String status = (String) paramMap.get("status");
		
		StringBuffer sqlString = new StringBuffer();		
		sqlString.append("select t.*, s.moving_average_price avg_price,(s.moving_average_price * t.qty) sum_price, a.account_name,a.account_dept" +
				" from mcs_material_sto_req t,mcs_sap_mtr_table s, account a" +
				" where t.qty!=0 and t.mtr_num = s.mtr_num and t.recipient=a.account_no(+)");

		if (StringUtils.isNotEmpty(startDate)) {				
			sqlString.append(" and t.doc_time >= to_date('").append(startDate).append("','yyyy-mm-dd')");					
		}
		
		if (StringUtils.isNotEmpty(endDate)) {			
			sqlString.append(" and t.doc_time < to_date('").append(endDate).append("','yyyy-mm-dd')+1");				
		}		
		
		if (StringUtils.isNotEmpty(deptIndex)) {				
			sqlString.append(" and t.dept_Index='").append(deptIndex).append("'");
		}
		
		if (StringUtils.isNotEmpty(vendorBatchNum)) {				
			sqlString.append(" and t.vendor_batch_num like '").append(vendorBatchNum.toUpperCase()).append("%'");				
		}
		
		if (StringUtils.isNotEmpty(mtrGrp)) {			
				sqlString.append(" and t.mtr_grp='").append(mtrGrp).append("'");							
		}
		
		if (StringUtils.isNotEmpty(mtrNum)) {				
			sqlString.append(" and t.mtr_num like '").append(mtrNum.toUpperCase()).append("%'");				
		}
		
		if ("STO-OUT".equals(status)) {			
			sqlString.append(" and t.active_flag='N'");				
		} else if ("STO-IN".equals(status)) {			
			sqlString.append(" and t.active_flag='Y' and t.qty>0");
		} else {
			sqlString.append(" and (qty>0 or qty<0 and ACTIVE_FLAG='N')");
		}
		
		sqlString.append(" order by t.doc_time");
		
		List stoReqList = SQLProcess.excuteSQLQuery(sqlString.toString(), delegator);
		return stoReqList;	  
	}
	  
	/**
	  * 备耗件领用金额汇总
	  * @param delegator
	  * @param param
	  * @throws GenericEntityException,SQLProcessException 
	  */
	public static List getHaojianLingList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
		String startDate =(String) paramMap.get("startDate"); 			
		String endDate =(String) paramMap.get("endDate");	
		String deptIndex = (String) paramMap.get("deptIndex");		
		
		StringBuffer sqlString = new StringBuffer();		
		sqlString.append("select  a.*,b.beijian,(a.haojian+b.beijian) sum_price from "+
						" (select t.dept_index,d.equipment_dept ,sum(t.qty*moving_average_price) haojian"+
						" from mcs_material_sto_req t,mcs_sap_mtr_table s ,equipment_dept d"+
						" where t.qty!=0 and t.mtr_num = s.mtr_num and t.dept_index = d.dept_index"+
						" and (qty>0 or qty<0 and ACTIVE_FLAG='N')"+
						" and t.mtr_grp ='20002S'");
		if (StringUtils.isNotEmpty(startDate)) {				
			sqlString.append(" and t.doc_time >= to_date('").append(startDate).append("','yyyy-mm-dd')");					
		}
		
		if (StringUtils.isNotEmpty(endDate)) {			
			sqlString.append(" and t.doc_time < to_date('").append(endDate).append("','yyyy-mm-dd')+1");				
		}		
		
		if (StringUtils.isNotEmpty(deptIndex)) {				
			sqlString.append(" and t.dept_Index='").append(deptIndex).append("'");
		}
		sqlString.append(" group by t.dept_Index,d.equipment_dept) a, "+
						" (select t.dept_index,d.equipment_dept,sum(t.qty*moving_average_price) beijian"+
						" from mcs_material_sto_req t,mcs_sap_mtr_table s ,equipment_dept d"+
						" where t.qty!=0 and t.mtr_num = s.mtr_num and t.dept_index = d.dept_index"+
						" and (qty>0 or qty<0 and ACTIVE_FLAG='N')"+
						" and t.mtr_grp ='20002P'");
		if (StringUtils.isNotEmpty(startDate)) {				
			sqlString.append(" and t.doc_time >= to_date('").append(startDate).append("','yyyy-mm-dd')");					
		}
		
		if (StringUtils.isNotEmpty(endDate)) {			
			sqlString.append(" and t.doc_time < to_date('").append(endDate).append("','yyyy-mm-dd')+1");				
		}		
		
		if (StringUtils.isNotEmpty(deptIndex)) {				
			sqlString.append(" and t.dept_Index='").append(deptIndex).append("'");
		}
		
		sqlString.append(" group by t.dept_Index,d.equipment_dept) b"+
						" where a.dept_index=b.dept_index");
		
		
		List HaojianList = SQLProcess.excuteSQLQuery(sqlString.toString(), delegator);
		return HaojianList;	  
	}
	
	/**
	 * 备耗件使用金额汇总
	 * @param delegator
	 * @param param
	 * @throws SQLProcessException
	 */
	public static List getHaojianShiList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
		String startDate = (String) paramMap.get("startDate");
		String endDate = (String) paramMap.get("endDate");
		String deptIndex = (String) paramMap.get("deptIndex");
		String equipmentId=(String) paramMap.get("equipmentId");
		String equipmentType = (String) paramMap.get("equipmentType");
		String toolGroup = (String) paramMap.get("toolGroup");	
	    
		StringBuffer sqlString = new StringBuffer();		
		sqlString.append("select nvl(t1.dept_index, t2.dept_index) dept_index,"+
					      " nvl(t1.maint_dept, t2.maint_dept) maint_dept,"+
					      " nvl(t1.equipment_id, t2.equipment_id) equipment_id,"+
					      " nvl(t1.equipment_type, t2.equipment_type) equipment_type,"+
					      " nvl(t1.tool_group, t2.tool_group) tool_group,"+
					      " nvl(t1.beijian_price, 0) beijian_price,"+
					      " nvl(t2.haojian_price, 0) haojian_price,"+
					      " (nvl(t2.haojian_price, 0) + nvl(t1.beijian_price, 0)) total_price"+
					 " from (select d.dept_index,"+
					                   " d.maint_dept,"+
					                   " nvl(d.parent_eqpid,d.equipment_id) equipment_id,"+
					                   " d.equipment_type,"+
					                   " d.tool_group,"+
					                   " t.mtr_grp,"+
					                   " sum(s.moving_average_price) beijian_price"+
					              " from mcs_material_status t,"+
					                           " mcs_sap_mtr_table s,"+
					                           " equipment d,"+
					                           " (select t1.material_status_index,"+
					                                   " t2.part_no,"+
					                                   " t2.part_name,"+
					                                   " t2.event_index,"+
					                                   " t2.event_type,"+
					                                   " t2.dept_index,"+
					                                   " t3.equipment_id,"+
					                                   " t3.form_name,"+
					                                   " t3.form_type,"+
					                                   " t3.start_time,"+
					                                   " t3.end_time"+
					                              " from MCS_PARTS_USE t1,"+
					                                   " PARTS_USE     t2,"+
					                                   " form_view     t3"+
					                             " where t1.parts_use_seq_index = t2.seq_index"+
					                               " and t2.event_index = t3.event_index"+
					                               " and t2.event_type = t3.event_type) e"+
					                     " where t.mtr_num = s.mtr_num"+
					                       " and t.material_status_index = e.material_status_index"+
					                       " and d.equipment_id = e.equipment_id" +
					                       " and t.mtr_grp = '20002P'");
		if (StringUtils.isNotEmpty(startDate)) {				
			sqlString.append(" and t.doc_time >= to_date('").append(startDate).append("','yyyy-mm-dd')");					
		}
		
		if (StringUtils.isNotEmpty(endDate)) {			
			sqlString.append(" and t.doc_time < to_date('").append(endDate).append("','yyyy-mm-dd')+1");				
		}		
		
		if (StringUtils.isNotEmpty(deptIndex)) {				
			sqlString.append(" and d.dept_Index='").append(deptIndex).append("'");
		}
		
		if (StringUtils.isNotEmpty(equipmentId)) {
			sqlString.append(" and nvl(d.parent_eqpid, d.equipment_id)='").append(equipmentId).append("'");
		}
		
		if (StringUtils.isNotEmpty(equipmentType)) {			
			sqlString.append(" and d.equipment_Type='").append(equipmentType).append("'");				
		}		
		
		if (StringUtils.isNotEmpty(toolGroup)) {				
			sqlString.append(" and d.tool_group='").append(toolGroup).append("'");
		}
		
		sqlString.append(" group by d.dept_index, d.maint_dept, nvl(d.parent_eqpid,d.equipment_id),d.equipment_type,d.tool_group, t.mtr_grp) t1"+
			 " full join (select d.dept_index,"+
			                   " d.maint_dept,"+
			                   " nvl(d.parent_eqpid,d.equipment_id) equipment_id,"+
			                   " d.equipment_type,"+
			                   " d.tool_group,"+
			                   " t.mtr_grp,"+
			                   " sum(s.moving_average_price) haojian_price"+
			              " from mcs_material_status t,"+
			                           " mcs_sap_mtr_table s,"+
			                           " equipment d,"+
			                           " (select t1.material_status_index,"+
			                                   " t2.part_no,"+
			                                   " t2.part_name,"+
			                                   " t2.event_index,"+
			                                   " t2.event_type,"+
			                                   " t2.dept_index,"+
			                                   " t3.equipment_id,"+
			                                   " t3.form_name,"+
			                                   " t3.form_type,"+
			                                   " t3.start_time,"+
			                                   " t3.end_time"+
			                              " from MCS_PARTS_USE t1,"+
			                                   " PARTS_USE     t2,"+
			                                   " form_view     t3"+
			                             " where t1.parts_use_seq_index = t2.seq_index"+
			                               " and t2.event_index = t3.event_index"+
			                               " and t2.event_type = t3.event_type) e"+
			                     " where t.mtr_num = s.mtr_num"+
			                       " and t.material_status_index = e.material_status_index"+
			                       " and d.equipment_id = e.equipment_id"+
			                       " and t.mtr_grp = '20002S'");
		
		if (StringUtils.isNotEmpty(startDate)) {				
			sqlString.append(" and t.doc_time >= to_date('").append(startDate).append("','yyyy-mm-dd')");					
		}
		
		if (StringUtils.isNotEmpty(endDate)) {			
			sqlString.append(" and t.doc_time < to_date('").append(endDate).append("','yyyy-mm-dd')+1");				
		}		
		
		if (StringUtils.isNotEmpty(deptIndex)) {				
			sqlString.append(" and d.dept_Index='").append(deptIndex).append("'");
		}
		
		if (StringUtils.isNotEmpty(equipmentId)) {
			sqlString.append(" and nvl(d.parent_eqpid, d.equipment_id)='").append(equipmentId).append("'");
		}
		
		if (StringUtils.isNotEmpty(equipmentType)) {			
			sqlString.append(" and d.equipment_Type='").append(equipmentType).append("'");				
		}		
		
		if (StringUtils.isNotEmpty(toolGroup)) {				
			sqlString.append(" and d.tool_group='").append(toolGroup).append("'");
		}
		
		sqlString.append(" group by d.dept_index, d.maint_dept, nvl(d.parent_eqpid,d.equipment_id),d.equipment_type,d.tool_group, t.mtr_grp) t2"+
			   " on t1.equipment_id = t2.equipment_id"+
			" order by maint_dept, equipment_id");
		
		List list = SQLProcess.excuteSQLQuery(sqlString.toString(), delegator);
		return list;
	}
	
	/**
	 * 备耗件使用金额明细
	 * @param delegator
	 * @param param
	 * @throws SQLProcessException
	 */
	public static List getHaojianUseListM(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
		String startDate = (String) paramMap.get("startDate");
		String endDate = (String) paramMap.get("endDate");
		String deptindex = (String) paramMap.get("deptIndex");
		String equipmentId=(String) paramMap.get("equipmentId");
		String equipmentType = (String) paramMap.get("equipmentType");
		String toolGroup = (String) paramMap.get("toolGroup");		
		String mtrGrp = (String) paramMap.get("mtrGrp");
		String mtrNum = (String) paramMap.get("mtrNum");		
		
		StringBuffer sqlString = new StringBuffer();
		sqlString.append("select e.maint_dept,"+
					      " e.equipment_id,"+
					      " e.equipment_type,"+
					      " e.tool_group,"+					      
					      " s.event_index,"+
					      " s.event_type,"+
					      " s.form_name,"+
					      " s.start_time,"+
					      " s.end_time,"+
					      " t.mtr_grp,"+
					      " t.mtr_num,"+
					      " t.mtr_desc,"+
					      " m.MOVING_AVERAGE_PRICE,"+
					      " count(*) count_num,"+
					      " sum(m.MOVING_AVERAGE_PRICE) total_price"+
					 " from mcs_material_status t,"+
					      " equipment e,"+
					      " mcs_sap_mtr_table m,"+
					      " (select t1.material_status_index,"+
					              " t2.part_no,"+
					              " t2.part_name,"+
					              " t2.event_index,"+
					              " t2.event_type,"+
					              " t2.dept_index,"+
					              " t3.equipment_id,"+
					              " t3.form_name,"+
					              " t3.form_type,"+
					              " t3.start_time,"+
					              " t3.end_time"+
					         " from MCS_PARTS_USE t1, PARTS_USE t2, form_view t3"+
					        " where t1.parts_use_seq_index = t2.seq_index"+
					          " and t2.event_index = t3.event_index"+
					          " and t2.event_type = t3.event_type) s"+
					" where t.mtr_num = m.mtr_num"+
					  " and t.material_status_index = s.material_status_index"+
					  " and e.equipment_id = s.equipment_id");
		if (StringUtils.isNotEmpty(startDate)) {
			sqlString.append(" and t.doc_time >= to_date('").append(startDate).append("','yyyy-mm-dd')");					
		}
		
		if (StringUtils.isNotEmpty(endDate)) {			
			sqlString.append(" and t.doc_time < to_date('").append(endDate).append("','yyyy-mm-dd')+1");				
		}		
		
		if (StringUtils.isNotEmpty(deptindex)) {				
			sqlString.append(" and e.dept_index='").append(deptindex).append("'");
		}
		
		if (StringUtils.isNotEmpty(equipmentId)) {				
			sqlString.append(" and (e.equipment_id='").append(equipmentId).append("'");
			sqlString.append(" or e.parent_eqpid='").append(equipmentId).append("')");
		}
		
		if (StringUtils.isNotEmpty(equipmentType)) {			
			sqlString.append(" and e.equipment_Type='").append(equipmentType).append("'");				
		}		
		
		if (StringUtils.isNotEmpty(toolGroup)) {				
			sqlString.append(" and e.tool_group='").append(toolGroup).append("'");
		}
		
		if (StringUtils.isNotEmpty(mtrGrp)) {				
			sqlString.append(" and t.mtr_Grp='").append(mtrGrp).append("'");					
		} else {
			sqlString.append(" and t.mtr_Grp in ('20002S','20002P')");
		}
		
		if (StringUtils.isNotEmpty(mtrNum)) {			
			sqlString.append(" and t.mtr_Num='").append(mtrNum).append("'");				
		}
		
		sqlString.append(" group by e.maint_dept,"+
				         " e.equipment_id,"+
				         " e.equipment_type,"+
				         " e.tool_group,"+
				         " s.event_index,"+
				         " s.event_type,"+
				         " s.form_name,"+
				         " s.start_time,"+
				         " s.end_time,"+
				         " t.mtr_grp,"+
				         " t.mtr_num,"+
				         " t.mtr_desc,"+
				         " m.MOVING_AVERAGE_PRICE"+
				" order by e.maint_dept,"+
				         " e.equipment_id,"+
				         " s.event_type,"+
				         " s.form_name,"+
				         " t.mtr_grp,"+
				         " t.mtr_num");

		List list = SQLProcess.excuteSQLQuery(sqlString.toString(), delegator);
		return list;
	}	
	
    /**
	 * 查询物料状态汇总 历史信息
	 * @param delegator
	 * @param param
	 * @throws SQLProcessException
	 */
	public static List getStatusHistList (GenericDelegator delegator,Map paramMap) throws SQLProcessException{		  
		String materialStatusIndex = (String) paramMap.get("materialStatusIndex");		
		StringBuffer sqlString = new StringBuffer();	
		
		sqlString.append("select t.*,t2.description from mcs_material_status_hist t,mcs_material_status_code t2 where t.status=t2.status");
		sqlString.append(" and t.material_status_index='").append(materialStatusIndex).append("'");
		sqlString.append(" order by t.material_status_hist_index");	
		
		List statusDetailList = SQLProcess.excuteSQLQuery(sqlString.toString(), delegator);		 
		return statusDetailList;
	}

	/**
     * 查询物料状态汇总 详细信息
     * @param delegator
     * @param param
     *            
	 * @throws SQLProcessException 
     */
	  public static List getStatusDetailList (GenericDelegator delegator,Map paramMap) throws SQLProcessException{		  
		 String startDate = (String) paramMap.get("startDate"); 			
		 String endDate = (String) paramMap.get("endDate");	
		 String deptIndex = (String) paramMap.get("deptIndex");		
		 String usingObjectId = (String) paramMap.get("usingObjectId");			
		 String mtrGrp =(String) paramMap.get("mtrGrp");	
		 String status =(String) paramMap.get("status");			
		 String mtrNum =(String) paramMap.get("mtrNum");	
		 String materialStatusIndex = (String) paramMap.get("materialStatusIndex");
		 
		 StringBuffer sqlString = new StringBuffer();
		 sqlString.append("select t.*,t2.description,t3.equipment_dept from mcs_material_status t,mcs_material_status_code t2,equipment_dept t3 where t.dept_index=t3.dept_index and t.status=t2.status");
		 
		 if (StringUtils.isNotEmpty(materialStatusIndex)) {
			 sqlString.append(" and t.material_status_index='").append(materialStatusIndex).append("'");				 
		 }
		 
		 if (StringUtils.isNotEmpty(startDate)) {				 
			 sqlString.append(" and t.doc_time>=to_date('").append(startDate).append("','yyyy-mm-dd')");
		 }
		 
		 if (StringUtils.isNotEmpty(endDate)) {				 
			 sqlString.append(" and t.doc_time<to_date('").append(endDate).append("','yyyy-mm-dd')+1");			 
		 }
		 
		 if (StringUtils.isNotEmpty(deptIndex)) {				 
			 sqlString.append(" and t.dept_index='").append(deptIndex).append("'");
		 }			 
		 
		 if (StringUtils.isNotEmpty(usingObjectId)) {				 
			 sqlString.append(" and t.using_Object_Id='").append(usingObjectId).append("'");			 
		 }
		 
		 if (StringUtils.isNotEmpty(mtrGrp)) {				 
			 sqlString.append(" and t.mtr_grp='").append(mtrGrp).append("'");
		 }
		 
		 if (StringUtils.isNotEmpty(status)) {				 
			 sqlString.append(" and t.status='").append(status).append("'");
		 }
		 
		 if (StringUtils.isNotEmpty(mtrNum)) {				 
			 sqlString = sqlString.append(" and t.mtr_num like '").append(mtrNum.toUpperCase()).append("%'"); 
		 }
		 
		 List statusDetailList = SQLProcess.excuteSQLQuery(sqlString.toString(), delegator);			 
		 return statusDetailList;
	  }
	  
    /**
	 * 查询物料状态汇总 信息
	 * @param delegator
	 * @param param
	 * @throws GenericEntityException, SQLProcessException
	 */
	public static List getMaterialStatusList(GenericDelegator delegator,Map paramMap) throws GenericEntityException,SQLProcessException{			 
		String startDate = (String) paramMap.get("startDate");
		String endDate = (String) paramMap.get("endDate");
		String deptIndex = (String) paramMap.get("deptIndex");
		//String usingObjectId = (String) paramMap.get("usingObjectId");
		String mtrGrp = (String) paramMap.get("mtrGrp");
		//String status = (String) paramMap.get("status");
		String mtrNum = (String) paramMap.get("mtrNum");
		List statusList = delegator.findAll("McsMaterialStatusCode", UtilMisc.toList("seqNum"));
		String codeListString = "";
		int i = 0; 
	     
		for (Iterator it = statusList.iterator(); it.hasNext(); it.next()) {
			codeListString=codeListString+"sum(decode(t2.seq_num,"+(10000+i)+", countnum, null)) COLUMN"+i+",";
			i = i+1;			
		}
		
        StringBuffer sqlString = new StringBuffer();
        String stoWhereString = "";
        //sqlString.append("select t1.mtr_grp, sum(decode(t2.seq_num,10000, countnum, null)) COLUMN0,  sum(decode(t2.seq_num,10001, t1.countnum, null)) COLUMN1,  sum(decode(t2.seq_num,10002, t1.countnum, null)) COLUMN2,  sum(decode(t2.seq_num,10003, countnum, null)) COLUMN3,  sum(decode(t2.seq_num,10004, t1.countnum, null)) COLUMN4, sum(decode(t2.seq_num,10005, t1.countnum, null)) COLUMN5,   sum(decode(t2.seq_num,10006, countnum, null)) COLUMN6,   sum(decode(t2.seq_num,10007, t1.countnum, null)) COLUMN7,  sum(decode(t2.seq_num,10008, t1.countnum, null)) COLUMN8,   sum(decode(t2.seq_num,10009, countnum, null)) COLUMN9, sum(decode(t2.seq_num,10010, t1.countnum, null)) COLUMN10,   sum(decode(t2.seq_num,10011, t1.countnum, null)) COLUMN11,  sum(decode(t2.seq_num,10012, t1.countnum, null)) COLUMN12  from (select t.mtr_grp, t.status, count(*) countnum from mcs_material_status t where 1=1    ");
        sqlString
				.append("select t1.mtr_num, t3.mtr_desc,t3.MOVING_AVERAGE_PRICE,")
				.append(codeListString)
				.append(" '11' from ( "
				+ " select t.mtr_num, 'STO-REQ' status, sum(t.qty) countnum from mcs_material_sto_req t where (qty>0 or qty<0 and ACTIVE_FLAG='N')");
        
        if (StringUtils.isNotEmpty(startDate)) {
        	stoWhereString = " and t.doc_time>=to_date('" + startDate + "','yyyy-mm-dd')";
        }
        
        if (StringUtils.isNotEmpty(endDate)) {
        	stoWhereString = stoWhereString + " and t.doc_time<to_date('" + endDate + "','yyyy-mm-dd')+1";
        }
        
        if (StringUtils.isNotEmpty(deptIndex)) {
        	stoWhereString = stoWhereString + " and t.dept_index='" + deptIndex + "'";
        }
        
        if (StringUtils.isNotEmpty(mtrGrp)) {
        	stoWhereString = stoWhereString + " and t.mtr_grp='" + mtrGrp + "'";
        }
        
        if (StringUtils.isNotEmpty(mtrNum)) {
        	stoWhereString = stoWhereString + " and t.mtr_num like '" + mtrNum.toUpperCase() + "%'";
        }
        
        sqlString.append(stoWhereString);
        sqlString.append(" group by t.Mtr_Num union all");
        
        // STO-OUT
        sqlString.append(" select t.mtr_num, 'STO-OUT' status, sum(t.qty-t.active_qty) countnum" +
        		" from mcs_material_sto_req t where T.ACTIVE_FLAG='N' " );
        sqlString.append(stoWhereString);
        sqlString.append(" group by t.Mtr_Num union all");
        
        
        //mcs_material_status
        sqlString.append(" select tt.mtr_num, tt.status, count(*) countnum" +
        		" from mcs_material_status tt where 1=1 ");
        
        /*if (StringUtils.isNotEmpty(usingObjectId)) {
        	sqlString.append(" and tt.using_object_id='" + usingObjectId + "'");
        }
        
        if (StringUtils.isNotEmpty(status)) {
        	sqlString.append(" and tt.status='" + status + "'");
        }*/
        
        sqlString.append(" and tt.material_sto_req_index in (select t.material_sto_req_index from mcs_material_sto_req t where qty>0 "
						+ stoWhereString + ")");        	 		 		 
        sqlString.append(" group by tt.Mtr_Num, tt.status) t1,");
        
        sqlString.append(" mcs_material_status_code t2, mcs_sap_mtr_table t3");
        sqlString.append(" where t1.status = t2.status and t1.mtr_num = t3.mtr_num(+) group by t1.Mtr_Num, t3.mtr_desc,t3.MOVING_AVERAGE_PRICE order by t1.Mtr_Num");			 
        List materialStatusList=SQLProcess.excuteSQLQuery(sqlString.toString(), delegator);
        
        return materialStatusList; 
	}
	  
	/**
     * 查询物料信息
     * @param delegator
     * @param param
	 * @throws SQLProcessException 
     */
	  public static List getMaterialList (GenericDelegator delegator,Map paramMap) throws SQLProcessException{
		  String mtrNum = (String) paramMap.get("mtrNum");
          String deptIndex = (String) paramMap.get("deptIndex");
          String mtrGrp = (String) paramMap.get("mtrGrp");
          String usingObjectId = (String) paramMap.get("usingObjectId");
          String inControl = (String) paramMap.get("inControl");
          String needScrapStore = (String) paramMap.get("needScrapStore");
          
          StringBuffer sql = new StringBuffer();
          sql.append("select t1.*,round(t2.moving_average_price,2) moving_average_price from mcs_material_info t1,mcs_sap_mtr_table t2 where t1.enabled=1 and t1.mtr_num=t2.mtr_num ");
          
          if (StringUtils.isNotEmpty(deptIndex)) {
        	  sql = sql.append("and t1.dept_index='").append(deptIndex).append("'");          
          }
          
          if (StringUtils.isNotEmpty(mtrGrp)) {
        	  sql = sql.append("and t1.mtr_grp='").append(mtrGrp).append("'");         
          }
          
 	      if (StringUtils.isNotEmpty(mtrNum)) { 
 	    	 sql = sql.append(" and t1.mtr_num like '").append(mtrNum).append("%'");
 	      }
 	     
 	      if (StringUtils.isNotEmpty(usingObjectId)){
 	    	sql = sql.append("and t1.material_index in (select material_index from mcs_mtr_object t2 where t2.using_object_id='").append(usingObjectId).append("')");
 	      }
 	      
 	     if (StringUtils.isNotEmpty(inControl)) { 
 	    	 sql = sql.append(" and t1.in_control=").append(inControl);
 	      }
 	      
 	     if (StringUtils.isNotEmpty(needScrapStore)) { 
	    	 sql = sql.append(" and t1.need_scrap_store='").append(needScrapStore).append("'");
	      }
 	    
 	      sql = sql.append("  order by t1.mtr_num");
		  List materialList = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		  
		  return materialList;
	  }

	/**
	 * 按使用日期 查询历史使用信息
	 * @param delegator
	 * @param param
	 * @throws GenericEntityException,SQLProcessException
	 */
	public static List getUsingHistoryList(GenericDelegator delegator,
			Map paramMap) throws GenericEntityException, SQLProcessException {
		String startDate = (String) paramMap.get("startDate");
		String endDate = (String) paramMap.get("endDate");
		String deptIndex = (String) paramMap.get("deptIndex");
		String usingObjectId = (String) paramMap.get("usingObjectId");
		String mtrGrp = (String) paramMap.get("mtrGrp");
		String mtrNum = (String) paramMap.get("mtrNum");
	
		StringBuffer sqlString = new StringBuffer();
		
		sqlString
				.append("select t2.material_status_index,t2.alias_name,t2.mtr_num,t2.mtr_desc,")
				.append("t1.using_object_id,t1.trans_by,t1.update_time,t2.status,t3.description ,t4.unfrozen_start_time")
				.append(
						" from mcs_material_status t2, mcs_material_status_code t3, mcs_material_status_hist t1")
				.append(
						" left join (SELECT max(update_time) unfrozen_start_time, material_status_index from mcs_material_status_hist where status = 'CABINET-NEW' and note = '恒温' group by material_status_index) t4 on t1.material_status_index = t4.material_status_index")
				.append(
						" where t1.status='USING' and t1.material_status_index = t2.material_status_index and t2.status=t3.status ");
		
		if (StringUtils.isNotEmpty(usingObjectId)) {
			sqlString.append(" and t1.using_object_id ='").append(usingObjectId)
					.append("' ");
		}

		if (StringUtils.isNotEmpty(startDate)) {
			sqlString.append(" and t1.update_time >=to_date('").append(startDate)
					.append("','yyyy-mm-dd' ) ");
		}

		if (StringUtils.isNotEmpty(endDate)) {
			sqlString.append(" and t1.update_time <=to_date('").append(endDate)
					.append("','yyyy-mm-dd')+1  ");
		}

		if (StringUtils.isNotEmpty(deptIndex)) {
			sqlString.append(" and t2.dept_index ='").append(deptIndex).append(
					"' ");
		}

		if (StringUtils.isNotEmpty(mtrGrp)) {
			sqlString.append(" and t2.mtr_grp ='").append(mtrGrp).append("'");
		}

		if (StringUtils.isNotEmpty(mtrNum)) {
			sqlString = sqlString.append(" and t2.mtr_num like '").append(
					mtrNum.toUpperCase()).append("%'");
		}
		sqlString.append(" order by t2.mtr_num,t2.mtr_desc");
		
		List materialStatusList = SQLProcess.excuteSQLQuery(sqlString
				.toString(), delegator);

		return materialStatusList;
	}
	
	/**
	 * 查询物料追踪
	 * @param delegator
	 * @param param
	 * @throws SQLProcessException
	 */
	public static List getMaterialTraceList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
		String startDate = (String) paramMap.get("startDate");
//		String startDate = "2000-05-11";
		String endDate = (String) paramMap.get("endDate");
		String deptIndex = (String) paramMap.get("deptIndex");
		String mtrGrp = (String) paramMap.get("mtrGrp");
		String mtrNum = (String) paramMap.get("mtrNum");
	
		StringBuffer sqlString = new StringBuffer();
		
		sqlString
				.append("select s.material_status_index,s.alias_name,s.mtr_num,s.mtr_desc,s.doc_time,")
				.append("min(decode(t.status, 'CABINET-NEW', t.update_time)) cabinet_time,")
				.append("min(decode(t.status, 'CABINET-NEW', t.trans_by)) as cabinet_trans_by,")
				.append("s.unfrozen_trans_time,")
				.append("s.unfrozen_trans_by,")
				.append("min(decode(t.status,'USING',to_char(t.update_time, 'yyyy-mm-dd hh24:mi:ss'))) using_time,")
				.append("min(decode(t.status, 'USING', t.trans_by)) as using_trans_by,")
				.append("min(decode(t.status, 'USING', t.using_object_id)) as using_object_id,")
				.append("min(decode(t.status, 'USING', t.note)) as using_note,")
				.append(" min(decode(t.status, 'FINISH', to_char(t.update_time, 'yyyy-mm-dd hh24:mi:ss') || t.note)) finish_time,")
				.append(" min(decode(t.status, 'ONLINE-SCRAP-OPT', to_char(t.update_time, 'yyyy-mm-dd hh24:mi:ss') || t.note)) scrap_time")
				.append(" from mcs_material_status s, mcs_material_status_hist t")
				.append(" where s.material_status_index = t.material_status_index");
		
		if (StringUtils.isNotEmpty(startDate)) {
			sqlString.append(" and s.doc_time >=to_date('").append(startDate).append("','yyyy-mm-dd')");
		}

		if (StringUtils.isNotEmpty(endDate)) {
			sqlString.append(" and s.doc_time <=to_date('").append(endDate).append("','yyyy-mm-dd')+1");
		}

		if (StringUtils.isNotEmpty(deptIndex)) {
			sqlString.append(" and s.dept_index ='").append(deptIndex).append("'");
		}

		if (StringUtils.isNotEmpty(mtrGrp)) {
			sqlString.append(" and s.mtr_grp ='").append(mtrGrp).append("'");
		}

		if (StringUtils.isNotEmpty(mtrNum)) {
			sqlString = sqlString.append(" and s.mtr_num like '").append(mtrNum.toUpperCase()).append("%'");
		}
		
		sqlString.append(" group by s.material_status_index,s.alias_name,s.mtr_num,s.mtr_desc,s.doc_time,s.unfrozen_trans_time,s.unfrozen_trans_by");
		sqlString.append(" order by s.doc_time,s.unfrozen_trans_time");		
		
		List materialStatusList = SQLProcess.excuteSQLQuery(sqlString.toString(), delegator);
		return materialStatusList;
	}


    /**
     * 季度异常用量分析
     * @param delegator
     * @param paraMap
     * @return Create on 2011-8-2 Update on 2011-8-2
     */
    public static List getMaterialQuaterAnalysisList(GenericDelegator delegator, Map paramMap)
            throws SQLProcessException {
//        String startDate = (String)paramMap.get("startDate");
        String endDate = (String)paramMap.get("endDate");
        String deptIndex = (String)paramMap.get("deptIndex");
        String mtrGrp = (String)paramMap.get("mtrGrp");
        String mtrNum = (String)paramMap.get("mtrNum");
        String sortby = (String)paramMap.get("sortby");

        StringBuffer sqlString = new StringBuffer();

        sqlString
                .append("select t2.material_index, t2.mtr_num, t1.mtr_grp,")
                .append(" t2.mtr_desc, t3.equipment_type, t2.using_object_id,")
                .append(" t3.maint_dept, t1.qty, t2.use_amount,t2.std_use_amount,")
                .append(" t2.use_amount - t2.std_use_amount diff")
                .append(" from (select r.mtr_num, r.dept_index, r.mtr_grp, sum(r.qty) qty")
                .append(" from mcs_material_sto_req r")
                .append(" where (qty>0 or qty<0 and ACTIVE_FLAG='N')")
                .append(" and r.doc_time >= to_date('"+endDate+"', 'yyyy-mm-dd')-90")
                .append(" and r.doc_time < to_date('"+endDate+"', 'yyyy-mm-dd') + 1")
                .append(" group by r.mtr_num, r.dept_index, r.mtr_grp) t1,")
                .append(" (select s.material_index, h.using_object_id, s.mtr_num,")
                .append(" s.mtr_desc, s.dept_index, o.std_use_amount, count(*) use_amount")
                .append(" from mcs_material_status_hist h, mcs_material_status s, mcs_mtr_object o")
                .append(" where h.material_status_index = s.material_status_index")
                .append(" and o.std_use_amount > 0 and o.material_index = s.material_index and o.using_object_id = h.using_object_id and h.status = 'USING'")
                .append(" and s.doc_time >= to_date('").append(endDate).append("','yyyy-mm-dd') -90")
                .append(" and s.doc_time < to_date('").append(endDate).append("','yyyy-mm-dd')+1")
                .append(" group by s.material_index, h.using_object_id, s.mtr_num, s.mtr_desc, s.dept_index, o.std_use_amount) t2,")
                .append(" equipment t3")
                .append(" where t1.mtr_num = t2.mtr_num and t1.dept_index = t2.dept_index and t2.using_object_id = t3.equipment_id");

        if (StringUtils.isNotEmpty(deptIndex)) {
            sqlString.append(" and t1.dept_index ='").append(deptIndex).append("'");
        }

        if (StringUtils.isNotEmpty(mtrGrp)) {
            sqlString.append(" and t1.mtr_grp ='").append(mtrGrp).append("'");
        }

        if (StringUtils.isNotEmpty(mtrNum)) {
            sqlString = sqlString.append(" and t1.mtr_num like '").append(mtrNum.toUpperCase())
                    .append("%'");
        }
        
        if (StringUtils.isNotEmpty(sortby)){
            sqlString.append(" order by ").append(sortby);
        }
        else sqlString.append(" order by t1.mtr_num");

        List materialStatusList = SQLProcess.excuteSQLQuery(sqlString.toString(), delegator);
        return materialStatusList;
    }
    
    /**
     * 季度异常用量分析 实际用量详细
     * @param delegator
     * @param paraMap
     * @return Create on 2015-5-12
     */
    public static List getMaterialQuaterAnalysisUsing(GenericDelegator delegator, Map paramMap)
            throws SQLProcessException {
        String endDate = (String)paramMap.get("endDate");
        String deptIndex = (String)paramMap.get("deptIndex");
        String mtrNum = (String)paramMap.get("mtrNum");
        String usingObjectId = (String)paramMap.get("usingObjectId");

        StringBuffer sqlString = new StringBuffer();        
        sqlString
		.append("select t2.material_status_index,t2.alias_name,t2.mtr_num,t2.mtr_desc,")
		.append("t1.using_object_id,t1.trans_by,t1.update_time,t2.status,t3.description ")
		.append(
				" from mcs_material_status_hist t1, mcs_material_status t2, mcs_material_status_code t3 ")
		.append(
				" where t1.status='USING' and t1.material_status_index = t2.material_status_index and t2.status=t3.status ");

        if (StringUtils.isNotEmpty(mtrNum)) {
			sqlString = sqlString.append(" and t2.mtr_num = '").append(mtrNum.toUpperCase()).append("'");
		}
        
		if (StringUtils.isNotEmpty(usingObjectId)) {
			sqlString.append(" and t1.using_object_id ='").append(usingObjectId).append("'");
		}
		
		if (StringUtils.isNotEmpty(deptIndex)) {
            sqlString.append(" and t2.dept_index ='").append(deptIndex).append("'");
        }
		
		if (StringUtils.isNotEmpty(endDate)) {
			sqlString.append(" and t2.doc_time >= to_date('"+endDate+"', 'yyyy-mm-dd')-90");
			sqlString.append(" and t2.doc_time < to_date('"+endDate+"', 'yyyy-mm-dd') + 1");
		}
		
		if (StringUtils.isNotEmpty(deptIndex)) {
			sqlString.append(" and t2.dept_index ='").append(deptIndex).append("'");
		}		

		
		sqlString.append(" order by t1.using_object_id, t1.update_time");
		
		List materialStatusList = SQLProcess.excuteSQLQuery(sqlString.toString(), delegator);		
		return materialStatusList;
    }
    
    /**
	 * 历史使用物料组金额
	 * @param delegator
	 * @param param
	 * @throws SQLProcessException
	 */
	public static List getEqpMtrGrpPriceList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
		String startDate = (String) paramMap.get("startDate");
		String endDate = (String) paramMap.get("endDate");
		String deptIndex = (String) paramMap.get("deptIndex");
		String usingObjectId = (String) paramMap.get("usingObjectId");
		String mtrGrp = (String) paramMap.get("mtrGrp");
		String mtrNum = (String) paramMap.get("mtrNum");
	
		String mainTableSql = "select t1.using_object_id, t2.mtr_grp, t2.mtr_num, t2.mtr_desc"
			+ " from MCS_MATERIAL_STATUS_HIST t1, mcs_material_status t2"
			+ " where t1.material_status_index = t2.material_status_index"
			+ " and t1.status='USING'";

		mainTableSql += " and t1.update_time >= to_date('" + startDate + "', 'yyyy-mm-dd')";
		mainTableSql += " and t1.update_time < to_date('" + endDate + "', 'yyyy-mm-dd') + 1";

		if (StringUtils.isNotEmpty(deptIndex)) {
			mainTableSql += " and t2.dept_index=" + deptIndex;
		}
		
		if (StringUtils.isNotEmpty(usingObjectId)) {
			mainTableSql += " and t1.using_object_id='" + usingObjectId + "'";
		}

		if (StringUtils.isNotEmpty(mtrGrp)) {
			mainTableSql += " and t2.mtr_grp='" + mtrGrp + "'";
		}

		if (StringUtils.isNotEmpty(mtrNum)) {
			mainTableSql += " and t2.mtr_num like '" + mtrNum + "%'";
		}
		
		String sql = "select e.maint_dept,e.tool_type,e.tool_group,t.using_object_id,t.mtr_grp," 
			+ " count(*) count_num, sum(m.MOVING_AVERAGE_PRICE) total_price"
			+ " from (" + mainTableSql + ") t, equipment e, mcs_sap_mtr_table m"
			+ " where t.using_object_id = e.equipment_id and t.mtr_num = m.mtr_num"
			+ " group by e.maint_dept,e.tool_type,e.tool_group,t.using_object_id,t.mtr_grp"
			+ " order by e.maint_dept,e.tool_type,e.tool_group,t.using_object_id,t.mtr_grp";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}
	
	/**
	 * 历史使用物料金额
	 * @param delegator
	 * @param param
	 * @throws SQLProcessException
	 */
	public static List getEqpMtrPriceList(GenericDelegator delegator, Map paramMap) throws SQLProcessException {
		String startDate = (String) paramMap.get("startDate");
		String endDate = (String) paramMap.get("endDate");
		String deptIndex = (String) paramMap.get("deptIndex");
		String usingObjectId = (String) paramMap.get("usingObjectId");
		String mtrGrp = (String) paramMap.get("mtrGrp");
		String mtrNum = (String) paramMap.get("mtrNum");
	
		String mainTableSql = "select t1.using_object_id, t2.mtr_grp, t2.mtr_num, t2.mtr_desc"
			+ " from MCS_MATERIAL_STATUS_HIST t1, mcs_material_status t2"
			+ " where t1.material_status_index = t2.material_status_index"
			+ " and t1.status='USING'";

		mainTableSql += " and t1.update_time >= to_date('" + startDate + "', 'yyyy-mm-dd')";
		mainTableSql += " and t1.update_time < to_date('" + endDate + "', 'yyyy-mm-dd') + 1";

		if (StringUtils.isNotEmpty(deptIndex)) {
			mainTableSql += " and t2.dept_index=" + deptIndex;
		}
		
		if (StringUtils.isNotEmpty(usingObjectId)) {
			mainTableSql += " and t1.using_object_id='" + usingObjectId + "'";
		}

		if (StringUtils.isNotEmpty(mtrGrp)) {
			mainTableSql += " and t2.mtr_grp='" + mtrGrp + "'";
		}

		if (StringUtils.isNotEmpty(mtrNum)) {
			mainTableSql += " and t2.mtr_num like '" + mtrNum + "%'";
		}
		
		String sql = "select e.maint_dept,e.tool_type,e.tool_group,t.using_object_id,t.mtr_grp,t.mtr_num,t.mtr_desc,"
			+ " m.MOVING_AVERAGE_PRICE, count(*) count_num, sum(m.MOVING_AVERAGE_PRICE) total_price"
			+ " from (" + mainTableSql + ") t, equipment e, mcs_sap_mtr_table m"
			+ " where t.using_object_id = e.equipment_id and t.mtr_num = m.mtr_num"
			+ " group by e.maint_dept,e.tool_type,e.tool_group,t.using_object_id,t.mtr_grp,t.mtr_num,t.mtr_desc,m.MOVING_AVERAGE_PRICE"
			+ " order by e.maint_dept,e.tool_type,e.tool_group,t.using_object_id,t.mtr_grp,t.mtr_num";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}
	
	/**
	 * 线边仓查询 
	 * 报废数量按操作时间查询
	 * @param delegator
	 * @param param
	 * @throws GenericEntityException, SQLProcessException
	 */
	public static List getCabinetListByUpdateTime(GenericDelegator delegator,Map paramMap) throws GenericEntityException,SQLProcessException{			 
		String startDate = (String) paramMap.get("startDate");
		String endDate = (String) paramMap.get("endDate");
		String deptIndex = (String) paramMap.get("deptIndex");
		String mtrGrp = (String) paramMap.get("mtrGrp");
		String mtrNum = (String) paramMap.get("mtrNum");
		List statusList = delegator.findAll("McsMaterialStatusCode", UtilMisc.toList("seqNum"));
		String codeListString = "";
		int i = 0;
	     
		for (Iterator it = statusList.iterator(); it.hasNext(); it.next()) {
			codeListString = codeListString + "sum(decode(t2.seq_num,"+(10000+i)+", countnum, 0)) COLUMN"+i+",";
			i = i+1;
		}
		
        StringBuffer sqlString = new StringBuffer();        
        sqlString
			.append("select t1.mtr_num, t3.mtr_desc,t3.MOVING_AVERAGE_PRICE,")
			.append(codeListString)
			.append(" 20 from (select t.mtr_num, 'STO-REQ' status, sum(t.qty) countnum from mcs_material_sto_req t where (qty>0 or qty<0 and ACTIVE_FLAG='N')");
        
        String whereString = ""; 
        if (StringUtils.isNotEmpty(deptIndex)) {
        	whereString = whereString + " and dept_index='" + deptIndex + "'";
        }
        
        if (StringUtils.isNotEmpty(mtrGrp)) {
        	whereString = whereString + " and mtr_grp='" + mtrGrp + "'";
        }
        
        if (StringUtils.isNotEmpty(mtrNum)) {
        	whereString = whereString + " and mtr_num like '" + mtrNum.toUpperCase() + "%'";
        }
        
        sqlString.append(whereString);//线边仓查询无需统计总领用，仅供参考
        sqlString.append(" group by t.Mtr_Num union all");
        
        // STO-OUT 未暂存
        sqlString.append(" select t.mtr_num, 'STO-OUT' status, sum(t.qty-t.active_qty) countnum" +
        		" from mcs_material_sto_req t where T.ACTIVE_FLAG='N'");
        sqlString.append(whereString);
        sqlString.append(" group by t.Mtr_Num union all");
        
        //mcs_material_status查询当前库存和使用中
        //报废数量按操作时间查询
        sqlString.append(" select tt.mtr_num, tt.status, count(*) countnum" +
        		" from mcs_material_status tt where 1=1");
        sqlString.append(whereString);
        sqlString.append(" and (status in ('CABINET-NEW','CABINET-RECYCLE','USING', 'FAB-REPAIR','VENDOR-REPAIR-OPT','FAB-WASH','VENDOR-WASH-OPT','CABINET-RECYCLE-WASH','CABINET-RECYCLE-REPAIR', 'VENDOR-REPAIR-LEADER','VENDOR-WASH-LEADER')"
        		+ " or status like '%SCRAP%' and tt.update_time>=to_date('" + startDate + "','yyyy-mm-dd')"
        		+ " and tt.update_time<to_date('" + endDate + "','yyyy-mm-dd')+1)");
        sqlString.append(" group by tt.Mtr_Num, tt.status) t1,");
        
        sqlString.append(" mcs_material_status_code t2, mcs_sap_mtr_table t3");
        sqlString.append(" where t1.status = t2.status and t1.mtr_num = t3.mtr_num(+) group by t1.Mtr_Num, t3.mtr_desc,t3.MOVING_AVERAGE_PRICE"
        		+ " having sum(decode(t2.seq_num, 10001, countnum, 0)) + sum(decode(t2.seq_num, 10002, countnum, 0)) + sum(decode(t2.seq_num, 10003, countnum, 0)) + sum(decode(t2.seq_num, 10005, countnum, 0)) + sum(decode(t2.seq_num, 10011, countnum, 0)) + sum(decode(t2.seq_num, 10012, countnum, 0)) + sum(decode(t2.seq_num, 10013, countnum, 0)) + sum(decode(t2.seq_num, 10014, countnum, 0)) + sum(decode(t2.seq_num, 10015, countnum, 0))"
        		+ "      + sum(decode(t2.seq_num, 10007, countnum, 0)) + sum(decode(t2.seq_num, 10008, countnum, 0)) + sum(decode(t2.seq_num, 10016, countnum, 0)) + sum(decode(t2.seq_num, 10017, countnum, 0)) + sum(decode(t2.seq_num, 10019, countnum, 0)) + sum(decode(t2.seq_num, 10020, countnum, 0))"
        		+ "      + sum(decode(t2.seq_num, 10009, countnum, 0)) + sum(decode(t2.seq_num, 10018, countnum, 0)) > 0"
        		+ " order by t1.Mtr_Num");	 
        List materialStatusList=SQLProcess.excuteSQLQuery(sqlString.toString(), delegator);
        
        return materialStatusList; 
	}

	/**
	 * 备件寿命查询 
	 * @param delegator
	 * @param param
	 * @throws SQLProcessException 
	 */
	public static List getUsePeriodList(GenericDelegator delegator,	Map paramMap) throws SQLProcessException {
		String startDate = (String) paramMap.get("startDate");
		String endDate = (String) paramMap.get("endDate");
		String deptIndex = (String) paramMap.get("deptIndex");
		String usingObjectId = (String) paramMap.get("usingObjectId");
		String mtrGrp = (String) paramMap.get("mtrGrp");
		String mtrNum = (String) paramMap.get("mtrNum");
		
		String sqlSelect = "select e.maint_dept, e.section, t.using_object_id, t.material_index, t.mtr_num, t.mtr_desc,"
			+ " t.mtr_grp, t.trans_by, a.account_name, t.update_time,"
			+ " to_char(nvl(t.mrb_date,t.update_time) + m.usable_time_limit, 'yyyy-mm-dd') forcast_change_date,"
			+ " m.usable_time_limit, round(sysdate - t.update_time) use_period,"
			+ " m.usable_time_limit - round(sysdate - nvl(t.mrb_date,t.update_time)) left_days,"
			+ " t.material_status_index, nvl(t.mrb_date,t.update_time) - t.update_time mrb_days";
		
		//备耗件寿命
		String sqlFrom1 = " from mcs_material_status t, mcs_material_info m, account a, equipment e"
			+ " where t.material_index=m.material_index"
			+ " and (t.mtr_num, t.using_object_id) not in (select mtr_num, using_object_id from mcs_mtr_object where usable_time_limit>0)";
		
		String sqlAnd = " and t.trans_by = a.account_no(+) and t.using_object_id = e.equipment_id"
			+ " and m.usable_time_limit > 0 and t.status = '" + ConstantsMcs.USING + "'";
		
		if (StringUtils.isNotEmpty(startDate)) {
			sqlAnd += " and t.update_time + m.usable_time_limit >= to_date('" + startDate + "', 'yyyy-mm-dd')";
		}
		
		if (StringUtils.isNotEmpty(endDate)) {
			sqlAnd += " and t.update_time + m.usable_time_limit < to_date('" + endDate + "', 'yyyy-mm-dd') + 1";
		}		

		if (StringUtils.isNotEmpty(deptIndex)) {
			sqlAnd += " and t.dept_index=" + deptIndex;
		}
		
		if (StringUtils.isNotEmpty(usingObjectId)) {
			sqlAnd += " and t.using_object_id='" + usingObjectId + "'";
		}

		if (StringUtils.isNotEmpty(mtrGrp)) {
			sqlAnd += " and t.mtr_grp='" + mtrGrp + "'";
		}

		if (StringUtils.isNotEmpty(mtrNum)) {
			sqlAnd += " and t.mtr_num like '" + mtrNum + "%'";
		}
		
		//特殊设备寿命
		String sqlFrom2 = " from mcs_material_status t, (select * from mcs_mtr_object where usable_time_limit>0) m, account a, equipment e"
			+ " where t.mtr_num = m.mtr_num and t.using_object_id = m.using_object_id";
		
		String sql = sqlSelect + sqlFrom1 + sqlAnd
			+ " union all "
			+ sqlSelect + sqlFrom2 + sqlAnd
			+ " order by maint_dept, using_object_id, mtr_num, update_time";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);		
		return list;	
	}
	
	/**
	 * 维修与清洗查询
	 * 报废数量按操作时间查询
	 * @param delegator
	 * @param param
	 * @throws GenericEntityException, SQLProcessException
	 */
	public static List getRepairWashListByUpdateTime(GenericDelegator delegator,Map paramMap) throws GenericEntityException,SQLProcessException{			 
		String startDate = (String) paramMap.get("startDate");
		String endDate = (String) paramMap.get("endDate");
		String deptIndex = (String) paramMap.get("deptIndex");
		String mtrGrp = (String) paramMap.get("mtrGrp");
		String mtrNum = (String) paramMap.get("mtrNum");
		String queryType = (String) paramMap.get("queryType");
		
		List statusList = delegator.findAll("McsMaterialStatusCode", UtilMisc.toList("seqNum"));
		String codeListString = "";
		int i = 0;	     
		for (Iterator it = statusList.iterator(); it.hasNext(); it.next()) {
			codeListString = codeListString + "sum(decode(t2.seq_num,"+(10000+i)+", countnum, 0)) COLUMN"+i+",";
			i = i+1;
		}
        
        String whereString = ""; 
        if (StringUtils.isNotEmpty(deptIndex)) {
        	whereString = whereString + " and dept_index='" + deptIndex + "'";
        }
        
        if (StringUtils.isNotEmpty(mtrGrp)) {
        	whereString = whereString + " and mtr_grp='" + mtrGrp + "'";
        }
        
        if (StringUtils.isNotEmpty(mtrNum)) {
        	whereString = whereString + " and mtr_num like '" + mtrNum.toUpperCase() + "%'";
        }
        
        String statuses = "";
        if ("REPAIR".equals(queryType)) {
        	statuses = "'CABINET-RECYCLE-REPAIR', 'FAB-REPAIR','VENDOR-REPAIR-OPT', 'VENDOR-REPAIR-LEADER'";
        } else if ("WASH".equals(queryType)) {
        	statuses = "'CABINET-RECYCLE-WASH', 'FAB-WASH','VENDOR-WASH-OPT', 'VENDOR-WASH-LEADER'";
        }
        
        StringBuffer sqlString = new StringBuffer();        
        sqlString
			.append("select t1.mtr_num, t3.mtr_desc,t3.MOVING_AVERAGE_PRICE,")
			.append(codeListString)
			.append(" 20 from (");
        
        //mcs_material_status查询当前库存和使用中
        //报废数量按操作时间查询
        sqlString.append(" select tt.mtr_num, tt.status, count(*) countnum" +
        		" from mcs_material_status tt where 1=1");
        sqlString.append(whereString);        
        sqlString.append(" and (status in ('USING', " + statuses + ")"
        		+ " or status like '%SCRAP%' and tt.update_time>=to_date('" + startDate + "','yyyy-mm-dd')"
        		+ " and tt.update_time<to_date('" + endDate + "','yyyy-mm-dd')+1)");
        sqlString.append(" group by tt.Mtr_Num, tt.status) t1,");
        
        sqlString.append(" mcs_material_status_code t2, mcs_sap_mtr_table t3");
        sqlString.append(" where t1.status = t2.status and t1.mtr_num = t3.mtr_num(+) group by t1.Mtr_Num, t3.mtr_desc,t3.MOVING_AVERAGE_PRICE"
        		+ " order by t1.Mtr_Num");	 
        List materialStatusList=SQLProcess.excuteSQLQuery(sqlString.toString(), delegator);
        
        return materialStatusList; 
	}
}
