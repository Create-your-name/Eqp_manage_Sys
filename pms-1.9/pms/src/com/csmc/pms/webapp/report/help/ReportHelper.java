package com.csmc.pms.webapp.report.help;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.util.Constants;

public class ReportHelper {
	
	/**
	 * 得到设备字符串格式如: 'A','B','C'
	 * @param arrEqpId
	 * @return strEqpId
	 */
	public static String getEqpIdSqlStr(String[] arrEqpId) {
		String strEqpId = "";
		if (arrEqpId != null) {
			for (int i = 0; i < arrEqpId.length; i++) {
				if ("".equals(strEqpId)) {
					strEqpId = "'" + arrEqpId[i] + "'";
				} else {
					strEqpId = strEqpId + ",'" + arrEqpId[i] + "'";
				}
			}
		}
		return strEqpId;
	}
	
	/**
	 * @param delegator
	 * @param eqpId
	 * @param ownDept
	 * @param maintDept
	 * @param location
	 * @param section
	 * @param keyEqp
	 * @param adjustEqp
	 * @param measureEqp
	 * @param equipmentType
	 * @return EquipmentList
	 * @throws GenericEntityException
	 */
	public static List queryEqpBasicInfo(
		GenericDelegator delegator,
		String eqpId, 
		String ownDept, 
		String maintDept, 
		String location, 
		String section, 
		String keyEqp,
		String adjustEqp,
		String measureEqp, 
		String equipmentType)
		throws GenericEntityException {

		EntityCondition con = buildQueryCondition(eqpId, ownDept, maintDept,
				location, section, keyEqp, adjustEqp, measureEqp, equipmentType);

		List list =
			delegator.findByCondition(
				"Equipment",
				con,
				null,
				UtilMisc.toList("equipmentId"));

		return list;
	}

	/**
	 * @param eqpId
	 * @param ownDept
	 * @param maintDept
	 * @param location
	 * @param section
	 * @param keyEqp
	 * @param adjustEqp
	 * @param measureEqp
	 * @param equipmentType
	 * @return EntityCondition
	 */
	private static EntityCondition buildQueryCondition(
			String eqpId, 
			String ownDept, 
			String maintDept, 
			String location, 
			String section, 
			String keyEqp,
			String adjustEqp,
			String measureEqp, 
			String equipmentType) {
		List entityExprList = new ArrayList();
		if (eqpId != null && !"".equals(eqpId)) {
			EntityExpr expr =
				new EntityExpr("equipmentId", EntityOperator.LIKE, eqpId + "%");
			entityExprList.add(expr);
		}
		if (ownDept != null && !"".equals(ownDept)) {
			EntityExpr expr =
				new EntityExpr("ownDept", EntityOperator.EQUALS, ownDept);			
			entityExprList.add(expr);
		}
		if (maintDept != null && !"".equals(maintDept)) {
			EntityExpr expr =
				new EntityExpr("maintDept", EntityOperator.EQUALS, maintDept);
			entityExprList.add(expr);
		}
		if (location != null && !"".equals(location)) {
			EntityExpr expr =
				new EntityExpr("location", EntityOperator.EQUALS, location);
			entityExprList.add(expr);
		}
		if (section != null && !"".equals(section)) {
			EntityExpr expr =
				new EntityExpr("section", EntityOperator.EQUALS, section);
			entityExprList.add(expr);
		}
		if (keyEqp != null && !"".equals(keyEqp)) {
			EntityExpr expr =
				new EntityExpr("keyEqp", EntityOperator.EQUALS, keyEqp);
			entityExprList.add(expr);
		}
		if (adjustEqp != null && !"".equals(adjustEqp)) {
			EntityExpr expr =
				new EntityExpr("adjustEqp", EntityOperator.EQUALS, adjustEqp);
			entityExprList.add(expr);
		}
		if (measureEqp != null && !"".equals(measureEqp)) {
			EntityExpr expr =
				new EntityExpr("measureEqp", EntityOperator.EQUALS, measureEqp);
			entityExprList.add(expr);
		}
		if (equipmentType != null && !"".equals(equipmentType)) {
			EntityExpr expr =
				new EntityExpr("equipmentType", EntityOperator.EQUALS, equipmentType);
			entityExprList.add(expr);
		}	

		EntityCondition con =
			new EntityConditionList(entityExprList, EntityOperator.AND);
		return con;
	}
	
	// ------------------------机台用料查询shaoaj-----------------------------	
    /**
     * 通过设备大类得到eqpid
     * 
     * @param menuGroup
     * @param delegator
     * @return 设备ID列表
     */
    public static List getEquipIDList(GenericDelegator delegator,String equipmentType) throws Exception {
        List list = delegator.findByAndCache("Equipment",UtilMisc.toMap("equipmentType", equipmentType),UtilMisc.toList("equipmentId"));
        return list;
    }
    
    /**
     * 通过设备大类得到保养种类
     * 
     * @param menuGroup
     * @param delegator
     * @return 保养种类列表
     */
    public static List getPeriodList(GenericDelegator delegator,String equipmentType) throws Exception {
        List list = delegator.findByAnd("DefaultPeriod",UtilMisc.toMap("eqpType", equipmentType,"event",Constants.PM));
        return list;
    }
    
    /**
     * 通过设备大类得到设备原因分类
     * 
     * @param menuGroup
     * @param delegator
     * @return 设备原因分类列表
     */
    public static List getTsTypeList(GenericDelegator delegator,String equipmentType) throws Exception {
        List list = delegator.findByAnd("PmsReason",UtilMisc.toMap("equipmentType", equipmentType,"reasonType",Constants.ABNORMAL));
        return list;
    }
    
    /**
     * 根据查询条件进行查询
     * (1)如果选择了异常记录(TS)根据设备原因分类Index(tsType)及equipmentId查询异常表单(ABNORMAL_FORM)过滤PARTS_USERS记录
	 * (2)如果选择了保养记录(PM)根据设备保养种类Index(period)及equipmentId查询保养表单(PM_FORM)过滤PARTS_USERS
	 * (3)sql:select PART_NO, EQUIPMENT_ID from 
	 * 	(select t1.PART_NO t2.EQUIPMENT_ID from PARTS_USE t1,ABNORMAL_FORM t2
	 *    union
	 *    select t1.PART_NO t2.EQUIPMENT_ID from PARTS_USE t1,PM_FORM t2)
	 *  where ......
     * @param delegator
     * @param map
     * @return
     * @throws Exception
     */
	public static List queryCleanRecordByCondition(GenericDelegator delegator,Map map) throws Exception {
		String startDate=(String)map.get("startDate");
		String endDate=(String)map.get("endDate");
		String equipmentId=(String)map.get("equipmentId");
		String tsType=(String)map.get("tsType");
		String pmType=(String)map.get("pmType");
		String equipmentType=(String)map.get("equipmentType");

		//查询sql的拼装
		StringBuffer sql=new StringBuffer();
		String commonSql="select  t1.PART_NO,t1.PART_NAME,t1.PART_TYPE,t1.PART_COUNT,t1.EVENT_TYPE,t1.UPDATE_TIME,t2.EQUIPMENT_ID,t2.end_time as endtime";
		//sql.append("select PART_NO,PART_NAME,PART_TYPE,PART_COUNT,EVENT_TYPE,UPDATE_TIME ");
		sql.append("select PART_NO,PART_NAME,PART_TYPE,PART_COUNT,EVENT_TYPE,UPDATE_TIME,TO_CHAR(endtime, 'yyyy-MM-dd') AS END_TIME,moving_average_price ");
		//关断是否需要加入equipmentId查询条件
		boolean eqpIdflag=false;
		boolean tsFlag=false;
		boolean startDateFlag=false;
		if(StringUtils.isNotEmpty(equipmentId)){
			eqpIdflag=true;
		}

		//拼装异常分类查询SQL,tsType为null时，不进行查询
		if(tsType!=null){
			sql.append(",EQUIPMENT_ID from (");
			sql.append(commonSql);
			sql.append(" from  PARTS_USE t1,ABNORMAL_FORM t2,equipment t3 where t1.EVENT_INDEX=t2.ABNORMAL_INDEX and t2.equipment_id = t3.equipment_id");
			sql.append(" and t1.EVENT_TYPE='").append(Constants.TS).append("'");
			sql.append(" and t2.STATUS='").append(Constants.OVER).append("'");
			tsFlag=true;
			//设备Id
			if(eqpIdflag){
				sql.append(" and t2.EQUIPMENT_ID='").append(equipmentId).append("'");
			}
			//设备大类
			if(StringUtils.isNotEmpty(equipmentType)){
				sql.append(" and t3.equipment_type='").append(equipmentType).append("'");
			}
			//设备异常原因
			if(!"".equals(tsType)){
				sql.append(" and t2.ABNORMAL_REASON_INDEX='").append(tsType).append("'");
			}
		}

		//拼装保养分类查询SQL,判断tsType部分是否已经进行了SQL拼装再进行相应组合
		if(pmType!=null){
			if(tsFlag){
				sql.append(" union all ");
			}else{
				sql.append(",EQUIPMENT_ID from (");
			}
			sql.append(commonSql);
			sql.append(" from PARTS_USE t1,PM_FORM t2,equipment t3 where t1.EVENT_INDEX=t2.PM_INDEX and t2.equipment_id = t3.equipment_id");
			sql.append(" and t1.EVENT_TYPE='").append(Constants.PM).append("'");
			sql.append(" and t2.STATUS='").append(Constants.OVER).append("'");
			//设备ID
			if(eqpIdflag){
				sql.append(" and t2.EQUIPMENT_ID='").append(equipmentId).append("'");
			}
			//设备大类
			if(StringUtils.isNotEmpty(equipmentType)){
				sql.append(" and t3.equipment_type='").append(equipmentType).append("'");
			}
			//设备保养原因
			if(!"".equals(pmType)){
				sql.append(" and t2.PERIOD_INDEX='").append(pmType).append("'");
			}
		}
		//没有查询条件时，须重新设备查询用表
		//sql.append(")");
		sql.append("), mcs_sap_mtr_table");
		List list=new ArrayList();
		if(tsType!=null||pmType!=null){
			//加入日期查询区间
			boolean hasTimeLimit = false;
			if(StringUtils.isNotEmpty(startDate)){
				hasTimeLimit = true;
				sql.append(" where  UPDATE_TIME>=to_date('").append(startDate).append("','yyyy-mm-dd hh24:mi:ss')");
				startDateFlag=true;
			}
			if(StringUtils.isNotEmpty(endDate)){
				hasTimeLimit = true;
				if(startDateFlag){
					sql.append(" and ");
				}else{
					sql.append(" where ");
				}
				sql.append(" UPDATE_TIME< to_date('").append(endDate).append("','yyyy-mm-dd hh24:mi:ss') + 1");
			}
			if (hasTimeLimit)
			{
				sql.append(" and PART_NO = mtr_num(+)");
			}
			else
			{
				sql.append(" where PART_NO = mtr_num(+)");
			}
			list = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		}
		return list;
	}
    
    /**
     * 根据条件 按月 查询EqpAvailUpTime
     * 
     * @param delegator
     * @param startDate
     * @param endDate
     * @param eqpId
     * @return List
     */
    public static List getEqpAvailUpTimeMonthList(GenericDelegator delegator,String startDate, String endDate, String eqpId) throws Exception {
    	String sql = "select to_char(t1.repdate-10,'yyyy-MONTH') repdate,round(avg(avabtime), 1) avabt,round(avg(uptime), 1) upt" + 
					 " from rep_eqps_effi_hist@dbre t1, b_equn@dbre t2" +
					 " where t1.eqpid = t2.eqpid and t1.eqpid is not null" +
					 " and t1.repdate > to_date('" + startDate + "','yyyy-mm-dd')" +
					 " and t1.repdate <= add_months(to_date('" + endDate + "','yyyy-mm-dd'),1)" + 
					 " and t1.eqpid in (" + eqpId + ")" +
					 " group by t1.repdate order by t1.repdate";
    	
    	List list = SQLProcess.excuteSQLQuery(sql, delegator);
        return list;
    }
    
    /**
     * 根据条件 按周 查询EqpAvailUpTime
     * 
     * @param delegator
     * @param startDate
     * @param endDate
     * @param eqpId
     * @return List
     */
    public static List getEqpAvailUpTimeWeekList(GenericDelegator delegator,String startDate, String endDate, String eqpId) throws Exception {
    	// 883609,huanghp,明确按周查询时的时间范围，2009.02.23
    	String sql = "select to_char(t1.repdate -7,'yymmdd')||'~'|| to_char(t1.repdate,'yymmdd') repdate,round(avg(avabtime), 1) avabt,round(avg(uptime), 1) upt" + 
					 " from rep_effi_weekhist@dbre t1, b_equn@dbre t2" +
					 " where t1.eqpid = t2.eqpid and t1.eqpid is not null" +
					 " and t1.repdate >= to_date('" + startDate + "','yyyy-mm-dd')" +
					 " and t1.repdate <= to_date('" + endDate + "','yyyy-mm-dd')+7" + 
					 " and t1.eqpid in (" + eqpId + ")" +
					 " group by t1.repdate order by t1.repdate";
    	
    	List list = SQLProcess.excuteSQLQuery(sql, delegator);
        return list;
    }
    
    /**
     * 根据条件 按日 查询EqpAvailUpTime
     * 
     * @param delegator
     * @param startDate
     * @param endDate
     * @param eqpId
     * @return List
     */
    public static List getEqpAvailUpTimeDayList(GenericDelegator delegator,String startDate, String endDate, String eqpId) throws Exception {
    	String conditionExpress = " repdate>to_date('"	+ startDate	+ "','yyyy-mm-dd')"
								+ " and repdate<to_date('" + endDate + "','yyyy-mm-dd')+1"
								+ " and eqpid in ( select distinct rtrim(eqpid) from b_equn@dbre where uptimeflag=1 )"
								+ " and eqpid in (" + eqpId + ")";
    	String groupbyExpress = " group by repdate";
    	String orderbyExpress = " order by repdate";
       		
    	String upSql = "select repdate,sum(time) up from rep_eqps_time@dbre WHERE " + conditionExpress
    					+ " and rtrim(repfunction) in ('IN USE','OPERATION')"
    					+ groupbyExpress;
    	
    	String avabSql = "select repdate,sum(time) avab from rep_eqps_time@dbre WHERE " + conditionExpress
						+ " and rtrim(repstatus) in ('PRODUCTIVE','02-STANDBY')"
						+ groupbyExpress;
    	
    	String totaltimeSql = "select repdate,sum(time) totaltime from rep_eqps_time@dbre WHERE " + conditionExpress + groupbyExpress;
    	
    	String reducetimeSql = "select repdate,sum(time) reducetime from rep_eqps_time@dbre WHERE " + conditionExpress
								+ " and rtrim(repfunction) in ('TOTAL REDUCE')"
								+ groupbyExpress;
    	
    	String sql = "select to_char(t1.repdate,'yymmdd') repdate," 
    				+ "round(t1.up/(t3.totaltime-nvl(t4.reducetime,0))*100,1) upt,"
		    		+ "round(t2.avab/(t3.totaltime-nvl(t4.reducetime,0))*100,1) avabt"
		    		+ " from "
		    		+ "(" + upSql + ") t1,"
		    		+ "(" + avabSql + ") t2,"
		    		+ "(" + totaltimeSql + ") t3,"
		    		+ "(" + reducetimeSql + ") t4"
		    		+ " where t1.repdate=t2.repdate and t1.repdate=t3.repdate and t1.repdate=t4.repdate(+)"
		    		+ orderbyExpress;
    	List list = SQLProcess.excuteSQLQuery(sql, delegator);
        return list;
    }
    
    /**
     * 设备查询equipment_schedule
     * 
     * @param delegator
     * @param startDate
     * @param endDate
     * @param equipmentType
     * @param equipmentId
     * @param pmType
     * @param keyEqp
     * @param adjustEqp
     * @param measureEqp
     * @return List
     */
    public static List queryPmSchedule(GenericDelegator delegator,
			String startDate, String endDate, String equipmentType, String equipmentId,
			String pmType, String keyEqp, String adjustEqp, String measureEqp)
			throws Exception {
    	String sql = getSqlQueryPmSchedule(startDate, endDate, equipmentType, equipmentId, pmType, keyEqp, adjustEqp, measureEqp);
		
		sql = sql + " order by t1.equipment_id,t1.schedule_date,t3.period_name";
		//sql = sql + " group by t1.equipment_id,t1.schedule_date order by t1.equipment_id,t1.schedule_date";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}    
    
    /**
     * 根据条件查询equipment_schedule
     * 
     * @param delegator
     * @param startDate
     * @param endDate
     * @param equipmentType
     * @param equipmentId
     * @param pmType
     * @param keyEqp
     * @param adjustEqp
     * @param measureEqp
     * @return List
     */
    public static List queryPmScheduleByPeriod(GenericDelegator delegator,
			String startDate, String endDate, String equipmentType, String equipmentId,
			String pmType, String keyEqp, String adjustEqp, String measureEqp)
			throws Exception {
		String sql = getSqlQueryPmSchedule(startDate, endDate, equipmentType, equipmentId, pmType, keyEqp, adjustEqp, measureEqp);
		
		sql = sql + " order by t1.equipment_id,t3.period_name,t1.schedule_date";
		//sql = sql + " group by t1.equipment_id,t1.schedule_date order by t1.equipment_id,t1.schedule_date";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}

	/**
	 * @param startDate
	 * @param endDate
	 * @param equipmentType
	 * @param equipmentId
	 * @param pmType
	 * @param keyEqp
	 * @param adjustEqp
	 * @param measureEqp
	 * @return
	 */
	private static String getSqlQueryPmSchedule(String startDate, String endDate, String equipmentType, String equipmentId, String pmType, String keyEqp, String adjustEqp, String measureEqp) {
		// 20090304,huanghp,PM预测报表中需要提供标准工时
		String sql = "select t1.equipment_id,to_char(t1.schedule_date,'yyyy/mm/dd') schedule_date,t3.period_name,t3.default_days,t3.standard_hour"//count(t3.period_index) count_period
					+ " from equipment_schedule t1,equipment t2,default_period t3"
					+ " where t1.equipment_id=t2.equipment_id and t1.period_index=t3.period_index";
		
		if (null != startDate && !"".equals(startDate)) {
			sql = sql + " and t1.schedule_date>=to_date('" + startDate +  "','yyyy-mm-dd')";
		}
		
		if (null != endDate && !"".equals(endDate)) {
			sql = sql + " and t1.schedule_date< to_date('" + endDate +  "','yyyy-mm-dd') + 1";
		}
		
		if (null != equipmentType && !"".equals(equipmentType)) {
			sql = sql + " and t2.equipment_type='" + equipmentType + "'";
		}
		
		if (null != equipmentId && !"".equals(equipmentId)) {
			sql = sql + " and t2.equipment_id='" + equipmentId + "'";
		}
		
		if (null != pmType && !"".equals(pmType)) {
			sql = sql + " and t3.period_index='" + pmType + "'";
		}
		
		if (null != keyEqp && !"".equals(keyEqp)) {
			sql = sql + " and t2.key_eqp='" + keyEqp + "'";
		}
		
		if (null != adjustEqp && !"".equals(adjustEqp)) {
			sql = sql + " and t2.adjust_eqp='" + adjustEqp + "'";
		}
		
		if (null != measureEqp && !"".equals(measureEqp)) {
			sql = sql + " and t2.measure_eqp='" + measureEqp + "'";
		}
		return sql;
	}
    
    /**
     * 获得部门列表
     * @param delegator
     * @return
     * @throws Exception
     */
    public static List getDeptList(GenericDelegator delegator) throws Exception{
    	return delegator.findAll("EquipmentDept", UtilMisc.toList("equipmentDept"));
    }
    
    /**
     * 备件领料和使用差异查询
     * @param delegator
     * @param map
     * @return
     * @throws Exception
     */
    public static List queryPartUseDiffByCondition(GenericDelegator delegator,Map map,String flag) throws Exception {
    	/*得到页面参数值*/
    	String date=(String)map.get("date");
    	String deptMent=(String)map.get("deptMent");
    	String partNo=(String)map.get("partNo");
    	List list=new ArrayList();
		if ("0".equals(flag)) {
			list = queryUseDiffList(delegator, date, deptMent, partNo);
		} else if ("1".equals(flag)) {
			list = queryPartInfo(delegator, date, deptMent, partNo);
		}
    	return list;
    }

	/**
	 * 使用差异
	 * @param delegator
	 * @param date 时间
	 * @param deptMent 部门
	 * @param partNo 物料号
	 * @return
	 * @throws SQLProcessException
	 */
	private static List queryUseDiffList(GenericDelegator delegator, String date, String deptMent, String partNo) throws SQLProcessException {
    	StringBuffer sb=new StringBuffer();
    	//外围共通SQL语句
    	sb.append("select t1.part_no, t1.part_sum - t2.part_sum part_sub, t1.part_sum require_sum, t2.part_sum use_sum from ");
    	//拼装表parts_require查询语句
    	sb.append(" (select part_no, sum(part_count) part_sum from parts_require t1 where ");
	   	Map whereMap = getQueryPublicWhere(date, deptMent, partNo);
    	sb.append((String)whereMap.get("partRequire"));
    	//开始拼装parts_use表查询语句
    	sb.append("  group by t1.part_no)t1,(select part_no, sum(part_count) part_sum from parts_use t1 where ");
    	sb.append((String)whereMap.get("partUse"));
    	sb.append("  group by t1.part_no)t2  where t1.part_no = t2.part_no(+)");
    	List list = SQLProcess.excuteSQLQuery(sb.toString(), delegator);
		return list;
	}
	
	/**
	 * 查看物料使用详细信息
	 * @param delegator
	 * @param date 时间
	 * @param deptMent 部门
	 * @param partNo 物料号
	 * @return
	 * @throws SQLProcessException
	 */
	private static List queryPartInfo(GenericDelegator delegator, String date, String deptMent, String partNo) throws SQLProcessException {
		Map whereMap = getQueryPublicWhere(date, deptMent, partNo);
		String commonStr="select t1.PART_NO,t1.PART_NAME,t1.PART_TYPE,t1.PART_COUNT,t1.EVENT_TYPE,t1.UPDATE_TIME,t2.EQUIPMENT_ID from parts_use t1,";
		StringBuffer sb=new StringBuffer();
		sb.append(commonStr);
		sb.append("PM_FORM t2 where  t1.EVENT_INDEX = t2.PM_INDEX(+) and t1.EVENT_TYPE = 'PM' and");
		sb.append((String)whereMap.get("partUse"));
		sb.append(" union ").append(commonStr);
		sb.append(" ABNORMAL_FORM t2 where  t1.EVENT_INDEX= t2.ABNORMAL_INDEX(+) and t1.EVENT_TYPE = 'TS' and");
		sb.append((String)whereMap.get("partUse"));
		List list = SQLProcess.excuteSQLQuery(sb.toString(), delegator);
		return list;
	}
	
	/**
	 * 获得过时间，物料号，部门的查询条件
	 * @param delegator
	 * @param date 时间
	 * @param deptMent 部门
	 * @param partNo 物料号
	 * @return
	 */
	private static Map getQueryPublicWhere(String date, String deptMent, String partNo) {
		/*定义查询语句中共通查询条件的变量*/
    	String departWhere="";
    	String partNoWhere="";
    	String requireDateWhere="";
    	String useDateWhere="";
		/*是否为空的标志位设定，默认为true 既为空*/
    	boolean isDeptMentEmpty=true;
    	boolean isPartNoEmpty=true;
    	boolean isDateEmpty=true;
    	/*开始设定参数的*/
    	if(StringUtils.isNotEmpty(deptMent)){
    		isDeptMentEmpty=false;
    	}
    	if(StringUtils.isNotEmpty(partNo)){
    		isPartNoEmpty=false;
    	}
    	if(StringUtils.isNotEmpty(date)){
    		isDateEmpty=false;
    	}
    	if(!isDeptMentEmpty){
    		departWhere="  t1.dept_index = "+deptMent;
    	}
    	//拼装物料信息查询条件
    	if(!isPartNoEmpty){
    		if(!isDeptMentEmpty){
    			partNoWhere=" and ";
    		}
    		partNoWhere=partNoWhere+" t1.part_no like '%"+partNo.toUpperCase()+"%'";
    	}
    	//拼装查询时间区域条件
    	if(!isDateEmpty){
    		if(!isDeptMentEmpty||!isPartNoEmpty){
    			requireDateWhere=" and ";
    			useDateWhere=" and ";
    		}
    		Calendar nowDate = Calendar.getInstance();// 得到当前时间
    		nowDate.add(Calendar.DATE, -(new Integer(date).intValue()));
    		SimpleDateFormat bartDateFormat =new SimpleDateFormat("yyyy-MM-dd");
    		String queryDate=bartDateFormat.format(nowDate.getTime());
    		
    		requireDateWhere=requireDateWhere+" t1.REQUIRE_TIME>=to_date('"+queryDate+"','yyyy-mm-dd')";
    		useDateWhere=useDateWhere+" t1.UPDATE_TIME>=to_date('"+queryDate+"','yyyy-mm-dd hh24:mi:ss')";
    	}
    	Map whereMap=new HashMap();
    	whereMap.put("partRequire", departWhere+partNoWhere+requireDateWhere);
    	whereMap.put("partUse", departWhere+partNoWhere+useDateWhere);
		return whereMap;
	}
	
	/**
     * 根据查询条件进行查询机台参数表
     * @param delegator
     * @param map
     * @return
     * @throws Exception
     */
    public static List queryUnscheduleEqpParamHist(GenericDelegator delegator,Map map) throws Exception {
    	String startDate = (String) map.get("startDate");
		String endDate = (String) map.get("endDate");
		String equipmentType = (String) map.get("equipmentType");
		String eqpId = (String) map.get("eqpId");
		String paramName = (String) map.get("paramName");
    	
    	// 查询sql的拼装
		String sql = "";
		sql = "select to_char(t1.update_time,'yyyy-mm-dd hh24:Mi:ss') update_time,t2.equipment_type,"
			+ "t1.equipment_id,t1.param_name,t1.value,t1.max_value,t1.min_value,t1.trans_by" 
			+ " from unschedule_eqp_param_hist t1,equipment t2"
			+ " where t1.equipment_id=t2.equipment_id and t1.event!='delete'";
		
		sql = sql + " and t1.update_time>=to_date('" + startDate + "','yyyy-mm-dd')"
				  + " and t1.update_time<to_date('" + endDate + "','yyyy-mm-dd')+1";
		
		// 设备大类
		if(StringUtils.isNotEmpty(equipmentType)){
			sql = sql + " and t2.equipment_type='" + equipmentType + "'";
		}
		
		if(StringUtils.isNotEmpty(eqpId)){
			sql = sql + " and t1.equipment_id='" + eqpId + "'";
		}
		
		if(StringUtils.isNotEmpty(paramName)){
			sql = sql + " and t1.param_name='" + paramName + "'";
		}

		sql = sql + " order by t1.equipment_id,t1.update_time,t1.param_name";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);

		return list;
    }
    
    /**
     * 根据设备大类查询FlowActionItem
     * @param delegator
     * @param isMsa 
     * @param String
     * @return list
     * @throws Exception
     */
    public static List getFlowActionItemList(GenericDelegator delegator, String equipmentType,
            String isMsa) throws Exception {
        // 查询sql的拼装
        String sql = "select t2.item_index,t2.item_name"
                + " from flow_action t1,flow_action_item t2"
                + " where t1.action_index=t2.action_index and t1.frozen=1 and t1.enabled=1"
                + " and t1.event_type='PM' and t1.event_name='" + equipmentType + "'";
        
        // MSA设备保养查询,项目包括数字型和文字型
        if (StringUtils.isEmpty(isMsa)) {
            sql += " and t2.item_type=2";
        }
        sql += " order by t2.item_name";

        List list = SQLProcess.excuteSQLQuery(sql, delegator);

        return list;
    }

    public static List getMsaPmItemPointsList(GenericDelegator delegator, Map filterMap) throws Exception {
        String startDate = (String) filterMap.get("startDate");
        String endDate = (String) filterMap.get("endDate");
        String equipmentType = (String) filterMap.get("equipmentType");
        String eqpId = (String) filterMap.get("eqpId");
        String periodIndex = (String) filterMap.get("periodIndex");
        String itemIndex = (String) filterMap.get("itemIndex");
        
        String sql = "select t1.equipment_id,t2.update_time,t2.item_value,t2.item_note,t4.action_description,t3.item_name,t3.item_description,t3.item_upper_spec,t3.item_lower_spec,t2.item_note,t3.item_unit,t3.default_value,t2.item_type" 
            + " from pm_form t1,flow_item_points t2, flow_action_item t3, flow_action t4"
            + " where t1.pm_index=t2.form_index"
            + " and t3.action_index=t4.action_index"
            + " and t2.form_type='PM'"
            + " and t2.update_time>=to_date('" + startDate + "','yyyy-mm-dd')"
            + " and t2.update_time<to_date('" + endDate + "','yyyy-mm-dd')+1";
        
        if(StringUtils.isNotEmpty(eqpId)){
            sql = sql + " and t1.equipment_id in (" + eqpId + ")";
        } else if(StringUtils.isNotEmpty(equipmentType)){
            sql = sql + " and t1.equipment_id in (select equipment_id from equipment where equipment_type='" + equipmentType + "')";
        }
        
        if(StringUtils.isNotEmpty(periodIndex)){
            sql = sql + " and t1.period_index=" + periodIndex;
        }
        
        if(StringUtils.isNotEmpty(itemIndex)){
            sql = sql + " and t2.item_index in (" + itemIndex+")"
            + " and t3.item_index in (" + itemIndex+")"
            + " and t2.item_index = t3.item_index";
        }
        
        sql = sql + " order by t1.equipment_id desc ,t3.item_order asc";
        System.out.println(sql);
        List list = SQLProcess.excuteSQLQuery(sql, delegator);
        return list;
    }
	public static List getPmItemPointsList(GenericDelegator delegator, Map filterMap) throws Exception {
		String startDate = (String) filterMap.get("startDate");
		String endDate = (String) filterMap.get("endDate");
		String equipmentType = (String) filterMap.get("equipmentType");
		String eqpId = (String) filterMap.get("eqpId");
		String periodIndex = (String) filterMap.get("periodIndex");
		String itemIndex = (String) filterMap.get("itemIndex");
		
		String sql = "select t1.equipment_id,t2.update_time,t2.item_value,t2.item_upper_spec,t2.item_lower_spec,t2.item_note" 
				+ " from pm_form t1,flow_item_points t2"
				+ " where t1.pm_index=t2.form_index"
				+ " and t2.form_type='PM'"
				+ " and t2.update_time>=to_date('" + startDate + "','yyyy-mm-dd')"
				+ " and t2.update_time<to_date('" + endDate + "','yyyy-mm-dd')+1";
		
		if(StringUtils.isNotEmpty(eqpId)){
			sql = sql + " and t1.equipment_id in (" + eqpId + ")";
		} else if(StringUtils.isNotEmpty(equipmentType)){
			sql = sql + " and t1.equipment_id in (select equipment_id from equipment where equipment_type='" + equipmentType + "')";
		}
		
		if(StringUtils.isNotEmpty(periodIndex)){
			sql = sql + " and t1.period_index=" + periodIndex;
		}
		
		if(StringUtils.isNotEmpty(itemIndex)){
			sql = sql + " and t2.item_type=2";				//don't needed itemType, itemIndex is enough
			sql = sql + " and t2.item_index=" + itemIndex;
		}
		
		sql = sql + " order by t2.update_time";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}
	
	public static List queryPmFormHist(GenericDelegator delegator, Map filterMap) throws Exception {
		String startDate = (String) filterMap.get("startDate");
		String endDate = (String) filterMap.get("endDate");	
		String equipmentType = (String) filterMap.get("equipmentType");
		String periodIndex = (String) filterMap.get("periodIndex");
		String eqpId = (String) filterMap.get("eqpId");
		String equipmentDept = (String) filterMap.get("equipmentDept");	
		String target = (String) filterMap.get("target");	
		
		String sql = "select t1.pm_index,t1.equipment_id,t1.end_time,t1.start_time,"
				+ "round((t1.end_time-t1.start_time)*24,1) pm_time,"
				+ "t1.owner,t1.start_user,t1.end_user,t2.schedule_date,t3.period_name, t3.standard_hour, t3.eqp_status, t3.warning_days, t3.default_days " 
				+ " from pm_form t1,equipment_schedule t2, default_period t3"
				+ " where t1.pm_index=t2.event_index and t1.status=1"
				+ " and t1.period_index = t3.period_index"
				+ " and t1.start_time>=to_date('" + startDate + "','yyyy-mm-dd')"
				+ " and t1.start_time<to_date('" + endDate + "','yyyy-mm-dd')+1";
		
		if(StringUtils.isNotEmpty(target)){
		    sql = sql + " and t3.standard_hour " + target;
		}
		if(StringUtils.isNotEmpty(equipmentType)){
			sql = sql + " and t1.equipment_id in (select equipment_id from equipment where equipment_type='" + equipmentType + "')";
		}
		
		if(StringUtils.isNotEmpty(eqpId)){
			sql = sql + " and t1.equipment_id in (" + eqpId + ")";
		} else if (StringUtils.isNotEmpty(equipmentDept)) {
			sql = sql + " and t1.equipment_id in (select equipment_id from equipment where maint_dept='" + equipmentDept + "')";
		}
		
		if(StringUtils.isNotEmpty(periodIndex)){
			sql = sql + " and t1.period_index=" + periodIndex;
		}
		
		sql = sql + " order by t1.start_time,t1.equipment_id";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}
	
	public static List queryAbnormalFormHist(GenericDelegator delegator, Map filterMap) throws Exception {
		String startDate = (String) filterMap.get("startDate");
		String endDate = (String) filterMap.get("endDate");	
		String equipmentType = (String) filterMap.get("equipmentType");
		String eqpId = (String) filterMap.get("eqpId");
		String equipmentDept = (String) filterMap.get("equipmentDept");	
		
		String sql = "select t1.abnormal_name,t1.equipment_id,t1.abnormal_text,t1.abnormal_reason,"
				+ "t1.start_time,t1.end_time,round((t1.end_time-t1.abnormal_time)*24,1) down_time,"
				+ "t1.job_text,t1.create_user,t1.start_user,t1.end_user" 
				+ " from abnormal_form t1"
				+ " where t1.status=1"
				+ " and t1.start_time>=to_date('" + startDate + "','yyyy-mm-dd')"
				+ " and t1.start_time<to_date('" + endDate + "','yyyy-mm-dd')+1";		
		
		if(StringUtils.isNotEmpty(eqpId)){
			sql = sql + " and t1.equipment_id in (" + eqpId + ")";
		} else {
			if(StringUtils.isNotEmpty(equipmentType)){
				sql = sql + " and t1.equipment_id in (select equipment_id from equipment where equipment_type='" + equipmentType + "')";
			}
			
			if (StringUtils.isNotEmpty(equipmentDept)) {
				sql = sql + " and t1.equipment_id in (select equipment_id from equipment where maint_dept='" + equipmentDept + "')";
			}
		}
		
		
		sql = sql + " order by t1.start_time,t1.equipment_id";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}
	
	public static List queryAbnormalTable(GenericDelegator delegator, Map filterMap) throws Exception {
		String startDate = (String) filterMap.get("startDate");
		String endDate = (String) filterMap.get("endDate");	
		String equipmentType = (String) filterMap.get("equipmentType");
		String eqpId = (String) filterMap.get("eqpId");
		String equipmentDept = (String) filterMap.get("equipmentDept");	
		
		String sql = "select t2.equipment_type, nvl(t1.abnormal_reason,'') abnormal_reason "
				+ " ,Count(nvl(t1.abnormal_reason,'')) C "
				+ " ,ROUND(SUM(t1.end_time-t1.abnormal_time)/Count(*)*24,2) A " 
				+ " from abnormal_form t1, equipment t2"
				+ " where t1.equipment_id=t2.equipment_id and t1.status=1"
				+ " and t1.start_time>=to_date('" + startDate + "','yyyy-mm-dd')"
				+ " and t1.start_time<to_date('" + endDate + "','yyyy-mm-dd')+1";		
		
		if (StringUtils.isNotEmpty(eqpId)) {
			sql = sql + " and t2.equipment_id in (" + eqpId + ")";
		} else {
			if(StringUtils.isNotEmpty(equipmentType)) {
				sql = sql + " and t2.equipment_type='" + equipmentType + "'";
			}
			
			if (StringUtils.isNotEmpty(equipmentDept)) {
				sql = sql + " and t2.maint_dept='" + equipmentDept + "'";
			}
		}
		
		sql = sql + " group by t2.equipment_type,nvl(t1.abnormal_reason, '')";
		sql = sql + " order by c desc";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}

	public static List queryPmEventHist(GenericDelegator delegator, Map filterMap) throws Exception {
		String startDate = (String) filterMap.get("startDate");
		String endDate = (String) filterMap.get("endDate");	
		String equipmentType = (String) filterMap.get("equipmentType");
		String eqpId = (String) filterMap.get("eqpId");
		String keyEqp = (String) filterMap.get("keyEqp");
		String adjustEqp = (String) filterMap.get("adjustEqp");
		String measureEqp = (String) filterMap.get("measureEqp");
		String dayCount = (String) filterMap.get("dayCount");
		
		String sql = "select t1.period_index,t3.period_name,"
				+ "round(sum((t1.end_time - t1.start_time) * 24)/" + dayCount + ", 1) avgDay,"
				+ "round(sum((t1.end_time - t1.start_time) * 24)/count(t1.period_index), 1) avgTime,"
				+ "count(t1.period_index) eventCount"
				+ " from pm_form t1,equipment t2,default_period t3"
				+ " where t1.equipment_id=t2.equipment_id" 
				+ " and t1.period_index=t3.period_index"
				+ " and t1.status = 1"
				+ " and t1.end_time>=to_date('" + startDate + "','yyyy-mm-dd')"
				+ " and t1.end_time<to_date('" + endDate + "','yyyy-mm-dd')+1";
		
		if(StringUtils.isNotEmpty(eqpId)){
			sql = sql + " and t1.equipment_id in (" + eqpId + ")";
		} else if (StringUtils.isNotEmpty(equipmentType)) {
			sql = sql + " and t1.equipment_id in (select equipment_id from equipment where equipment_type='" + equipmentType + "')";
		}
		
		if(StringUtils.isNotEmpty(keyEqp)){
			sql = sql + " and t2.key_eqp='" + keyEqp + "'";
		}
		
		if(StringUtils.isNotEmpty(adjustEqp)){
			sql = sql + " and t2.adjust_eqp='" + adjustEqp + "'";
		}
		
		if(StringUtils.isNotEmpty(measureEqp)){
			sql = sql + " and t2.measure_eqp='" + measureEqp + "'";
		}
		
		sql = sql + " group by t1.period_index,t3.period_name order by t3.period_name";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}
	
	public static List queryPmEventEqpHist(GenericDelegator delegator, Map filterMap) throws Exception {
		String startDate = (String) filterMap.get("startDate");
		String endDate = (String) filterMap.get("endDate");	
		String equipmentType = (String) filterMap.get("equipmentType");
		String eqpId = (String) filterMap.get("eqpId");
		String keyEqp = (String) filterMap.get("keyEqp");
		String adjustEqp = (String) filterMap.get("adjustEqp");
		String measureEqp = (String) filterMap.get("measureEqp");
		String dayCount = (String) filterMap.get("dayCount");
		
		String periodIndex = (String) filterMap.get("periodIndex");
		
		String sql = "select t1.equipment_id,"
				+ "round(sum((t1.end_time - t1.start_time) * 24)/" + dayCount + ", 1) avgDay,"
				+ "round(sum((t1.end_time - t1.start_time) * 24)/count(t1.period_index), 1) avgTime,"
				+ "count(t1.period_index) eventCount"
				+ " from pm_form t1,equipment t2"
				+ " where t1.equipment_id=t2.equipment_id"
				+ " and t1.status = 1"
				+ " and t1.end_time>=to_date('" + startDate + "','yyyy-mm-dd')"
				+ " and t1.end_time<to_date('" + endDate + "','yyyy-mm-dd')+1"
				+ " and t1.period_index=" + periodIndex;
		
		if(StringUtils.isNotEmpty(eqpId)){
			sql = sql + " and t1.equipment_id in (" + eqpId + ")";
		} else if (StringUtils.isNotEmpty(equipmentType)) {
			sql = sql + " and t1.equipment_id in (select equipment_id from equipment where equipment_type='" + equipmentType + "')";
		}
		
		if(StringUtils.isNotEmpty(keyEqp)){
			sql = sql + " and t2.key_eqp='" + keyEqp + "'";
		}
		
		if(StringUtils.isNotEmpty(adjustEqp)){
			sql = sql + " and t2.adjust_eqp='" + adjustEqp + "'";
		}
		
		if(StringUtils.isNotEmpty(measureEqp)){
			sql = sql + " and t2.measure_eqp='" + measureEqp + "'";
		}
		
		sql = sql + " group by t1.equipment_id order by t1.equipment_id";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}
	
	public static List queryPmEventEqpDetail(GenericDelegator delegator, Map filterMap) throws Exception {
		String startDate = (String) filterMap.get("startDate");
		String endDate = (String) filterMap.get("endDate");	
		String equipmentType = (String) filterMap.get("equipmentType");
		String eqpId = (String) filterMap.get("eqpId");
		String keyEqp = (String) filterMap.get("keyEqp");
		String adjustEqp = (String) filterMap.get("adjustEqp");
		String measureEqp = (String) filterMap.get("measureEqp");
		//String dayCount = (String) filterMap.get("dayCount");
		
		String periodIndex = (String) filterMap.get("periodIndex");
		
		String equipmentId = (String) filterMap.get("equipmentId");
		
		String sql = "select t1.equipment_id, t1.start_time, t1.end_time, t3.standard_hour,"
				+ "round((t1.end_time - t1.start_time) * 24, 1) actualTime"		
				+ " from pm_form t1,equipment t2,default_period t3"
				+ " where t1.equipment_id=t2.equipment_id"
				+ " and t1.period_index=t3.period_index"
				+ " and t1.status = 1"
				+ " and t1.end_time>=to_date('" + startDate + "','yyyy-mm-dd')"
				+ " and t1.end_time<to_date('" + endDate + "','yyyy-mm-dd')+1"
				+ " and t1.period_index=" + periodIndex
				+ " and t1.equipment_id='" + equipmentId + "'";
		
		if(StringUtils.isNotEmpty(eqpId)){
			sql = sql + " and t1.equipment_id in (" + eqpId + ")";
		} else if (StringUtils.isNotEmpty(equipmentType)) {
			sql = sql + " and t1.equipment_id in (select equipment_id from equipment where equipment_type='" + equipmentType + "')";
		}
		
		if(StringUtils.isNotEmpty(keyEqp)){
			sql = sql + " and t2.key_eqp='" + keyEqp + "'";
		}
		
		if(StringUtils.isNotEmpty(adjustEqp)){
			sql = sql + " and t2.adjust_eqp='" + adjustEqp + "'";
		}
		
		if(StringUtils.isNotEmpty(measureEqp)){
			sql = sql + " and t2.measure_eqp='" + measureEqp + "'";
		}
		
		sql = sql + " order by t1.start_time";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}

	/**
	 * @param cStartDate
	 * @param cEndDate
	 * @return
	 */
	public static List getMonthList(Calendar cStartDate, Calendar cEndDate) {
		SimpleDateFormat monthDateFormat = new SimpleDateFormat ("yyyy/MM");
		List monthList = new ArrayList();			
		String yearMonth;
		int startDayOfMonth, endDayOfMonth;	
		
		Calendar cTmpDate = new GregorianCalendar();
		cTmpDate.setTime(cStartDate.getTime());		    
		
		while (!cTmpDate.after(cEndDate)) {
			yearMonth = monthDateFormat.format(cTmpDate.getTime());
			
			if (!cTmpDate.after(cStartDate) && !cTmpDate.before(cStartDate)) {
				startDayOfMonth = cStartDate.get(Calendar.DAY_OF_MONTH);
				endDayOfMonth = cStartDate.getActualMaximum(Calendar.DAY_OF_MONTH);
			} else if (!cTmpDate.after(cEndDate) && !cTmpDate.before(cEndDate)) {
				startDayOfMonth = cEndDate.getActualMinimum(Calendar.DAY_OF_MONTH);
				endDayOfMonth = cEndDate.get(Calendar.DAY_OF_MONTH);
			} else {
				startDayOfMonth = cTmpDate.getActualMinimum(Calendar.DAY_OF_MONTH);
				endDayOfMonth = cTmpDate.getActualMaximum(Calendar.DAY_OF_MONTH);
			}
			
			Map dayMap = new HashMap();
			dayMap.put("yearMonth", yearMonth);
			dayMap.put("startDayOfMonth", Integer.toString(startDayOfMonth));
			dayMap.put("endDayOfMonth", Integer.toString(endDayOfMonth));
			monthList.add(dayMap);
			
			cTmpDate.add(Calendar.MONTH, 1);
		}
		return monthList;
	}	
	
	/**
	 * @author huanghp 修改预测日期的范围 2009-3-12
	 * @param cStartDate
	 * @param cEndDate
	 * @return
	 */
	public static List getMonthListByPeriod(Calendar cStartDate, Calendar cEndDate) {
		SimpleDateFormat monthDateFormat = new SimpleDateFormat ("yyyy/MM");
		List monthList = new ArrayList();			
		String yearMonth;
		int startDayOfMonth, endDayOfMonth;	
		
		Calendar cTmpDate = new GregorianCalendar();
		cTmpDate.setTime(cStartDate.getTime());		    
		
		while (!cTmpDate.after(cEndDate)) {
			yearMonth = monthDateFormat.format(cTmpDate.getTime());
			
			if (cTmpDate.equals(cStartDate))
			{
				// 第一次进入，取开始日期
				startDayOfMonth = cStartDate.get(Calendar.DAY_OF_MONTH);
			}
			else
			{
				// 取游标日的最小开始日期
				startDayOfMonth = cTmpDate.getActualMinimum(Calendar.DAY_OF_MONTH);
			}
			
			// 将游标日调整为当月最后一日
			cTmpDate.set(Calendar.DAY_OF_MONTH, cTmpDate.getActualMaximum(Calendar.DAY_OF_MONTH));
			if(cTmpDate.after(cEndDate))
			{
				// 游标日和结束日期是同一个月，取结束日期
				endDayOfMonth = cEndDate.get(Calendar.DAY_OF_MONTH);
			}
			else
			{
				endDayOfMonth = cTmpDate.getActualMaximum(Calendar.DAY_OF_MONTH);
				// 游标日后退一月
				cTmpDate.add(Calendar.MONTH, 1);
				cTmpDate.set(Calendar.DAY_OF_MONTH, cTmpDate.getActualMinimum(Calendar.DAY_OF_MONTH));
			}
			
			Map dayMap = new HashMap();
			dayMap.put("yearMonth", yearMonth);
			dayMap.put("startDayOfMonth", Integer.toString(startDayOfMonth));
			dayMap.put("endDayOfMonth", Integer.toString(endDayOfMonth));
			monthList.add(dayMap);
		}
		return monthList;
	}	
	
	 /**
	 * 物料使用和物料模板差异
	 * @param delegator
	 * @param date 时间
	 * @param deptMent 部门
	 * @param partNo 物料号
	 * @author qinchao
	 * @return
	 * @throws SQLProcessException
	 */
    public static List queryMcsStoReqList(GenericDelegator delegator, Map map) throws SQLProcessException {
        /* 得到页面参数值 */
        String month = (String) map.get("month");
        String equipmentDept = (String) map.get("equipmentDept");
        String partNo = (String) map.get("partNo");

        StringBuffer sbHist = new StringBuffer();
        sbHist.append("select * from mcs_sto_req_monthly_hist t where t.qty != 0 and t.record_month='").append(month)
                .append("' and dept_index='").append(equipmentDept).append("'");
        if (StringUtils.isNotEmpty(partNo)) {
            sbHist.append(" AND t.mtr_num like '%").append(partNo).append("%'");
        }
        sbHist.append(" order by t.delay_days desc");
        List list = SQLProcess.excuteSQLQuery(sbHist.toString(), delegator);

        if (list == null || list.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT k.*, trunc(sysdate - k.doc_time) delay_days, b.moving_average_price,d.part_name MTR_DESC ");
            sb.append(" FROM (SELECT t.mtr_num,t.mtr_grp,t.plant,SUM(t.qty) qty,t.dept_index,MAX(t.doc_time) doc_time ");
            sb.append("  FROM mcs_material_sto_req t WHERE t.dept_index = '").append(equipmentDept).append("'");
            if (StringUtils.isNotEmpty(partNo)) {
                sb.append(" AND t.mtr_num like '%").append(partNo).append("%'");
            }
            sb.append("  and t.mtr_grp in ('20008P','20008S','100088','100085','100093','100083','100084')");
            sb.append("  and t.mtr_num not in (select part_no from parts_filter)");
            sb.append("  GROUP BY t.mtr_num, t.mtr_grp, t.plant, t.dept_index) k,");
            sb.append("  mcs_sap_mtr_table b,parts_data d");
            sb.append("  WHERE k.qty != 0 and k.mtr_num = b.mtr_num(+) and k.plant = b.plant and k.mtr_num = d.part_no ");
            sb.append("  ORDER BY delay_days desc");
            list = SQLProcess.excuteSQLQuery(sb.toString(), delegator);
        }
        return list;
    }
	
	/**
	 * PM用料明细查询
	 * @param delegator
	 * @author qinchao
	 * @return
	 * @throws SQLProcessException
	 */
    public static List queryPartsUsedDetail(GenericDelegator delegator, Map map) throws SQLProcessException {
        /* 得到页面参数值 */
        String equipmentDept = (String) map.get("equipmentDept");
        String equipmentType = (String) map.get("equipmentType");
        String pmType = (String) map.get("pmType");
        String month = (String) map.get("month");
        String partNo = (String) map.get("partNo");
        String periodIndex = (String) map.get("periodIndex");
        String strEqpId = (String) map.get("strEqpId");
        StringBuffer sbHist = new StringBuffer();
        sbHist.append("select * from MCS_PARTS_USED_MONTHLY_HIST t where t.record_month='").append(month)
                .append("' and maint_dept='").append(equipmentDept).append("' and pm_type='").append(pmType).append("'");
        if (StringUtils.isNotEmpty(equipmentDept)) {
            sbHist.append(" and t.maint_dept ='").append(equipmentDept).append("' ");
        }
        if (StringUtils.isNotEmpty(equipmentType)) {
            sbHist.append(" and t.equipment_type ='").append(equipmentType).append("' ");
        }
        if (StringUtils.isNotEmpty(partNo)) {
            sbHist.append(" and t.part_no like'%").append(partNo).append("%' ");
        }
        if (StringUtils.isNotEmpty(periodIndex)) {
            sbHist.append(" and t.period_index ='").append(periodIndex).append("' ");
        }
        if (StringUtils.isNotEmpty(strEqpId)) {
            sbHist.append(" and t.equipment_id in(").append(strEqpId).append(") ");
        }
        sbHist.append(" order by t.equipment_id,t.start_time desc");
        List list = SQLProcess.excuteSQLQuery(sbHist.toString(), delegator);

        if (list == null || list.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            if (Constants.PM.equals(pmType)) {
                sb.append("select t.*,t2.verpr");
                sb.append("  from (SELECT b.maint_dept,");
                sb.append("               b.equipment_id,");
                sb.append("               b.pm_name,");
                sb.append("               b.start_time,");
                sb.append("               b.part_no,");
                sb.append("               b.part_name,");
                sb.append("               b.part_type,");
                sb.append("               B.MTR_grp,");
                sb.append("               b.part_count used,");
                sb.append("               nvl(a.part_count, 0) part_count,");
                sb.append("               b.pm_index,");
                sb.append("               b.part_count - nvl(a.part_count, 0) count_diff");
                sb.append("          FROM (SELECT e.maint_dept,");
                sb.append("                       f.equipment_id,");
                sb.append("                       f.pm_name,");
                sb.append("                       f.start_time,");
                sb.append("                       t.part_no,");
                sb.append("                       t.part_name,");
                sb.append("                       t.part_type,");
                sb.append("                       t.mtr_grp,");
                sb.append("                       t.part_count,");
                sb.append("                       f.pm_index");
                sb.append("                  FROM pm_form f, equipment e, parts_pm t");
                sb.append("                 WHERE TO_CHAR(f.start_time, 'yyyy-mm') =");
                sb.append("                       TO_CHAR(SYSDATE, 'yyyy-mm')");
                sb.append("                   AND f.status = 1");
                sb.append("                   AND f.period_index = t.period_index");
                if (StringUtils.isNotEmpty(equipmentDept)) {
                    sb.append(" and e.maint_dept ='").append(equipmentDept).append("' ");
                }
                if (StringUtils.isNotEmpty(equipmentType)) {
                    sb.append(" and e.equipment_type ='").append(equipmentType).append("' ");
                }
                if (StringUtils.isNotEmpty(partNo)) {
                    sb.append(" and t.part_no like'%").append(partNo).append("%' ");
                }
                if (StringUtils.isNotEmpty(periodIndex)) {
                    sb.append(" and f.period_index ='").append(periodIndex).append("' ");
                }
                if (StringUtils.isNotEmpty(strEqpId)) {
                    sb.append(" and e.equipment_id in(").append(strEqpId).append(") ");
                }
                sb.append("                   AND f.equipment_id = e.equipment_id) a,");
                sb.append("               (SELECT e.maint_dept,");
                sb.append("                       f.equipment_id,");
                sb.append("                       f.pm_name,");
                sb.append("                       f.start_time,");
                sb.append("                       u.part_no,");
                sb.append("                       u.part_name,");
                sb.append("                       U.MTR_GRP,");
                sb.append("                       u.part_type,");
                sb.append("                       u.part_count,");
                sb.append("                       f.pm_index");
                sb.append("                  FROM pm_form f, equipment e, parts_use u");
                sb.append("                 WHERE TO_CHAR(f.start_time, 'yyyy-mm') =");
                sb.append("                       TO_CHAR(SYSDATE, 'yyyy-mm')");
                sb.append("                   AND f.status = 1");
                sb.append("                   AND f.pm_index = u.event_index");
                if (StringUtils.isNotEmpty(equipmentDept)) {
                    sb.append(" and e.maint_dept ='").append(equipmentDept).append("' ");
                }
                if (StringUtils.isNotEmpty(equipmentType)) {
                    sb.append(" and e.equipment_type ='").append(equipmentType).append("' ");
                }
                if (StringUtils.isNotEmpty(partNo)) {
                    sb.append(" and u.part_no like'%").append(partNo).append("%' ");
                }
                if (StringUtils.isNotEmpty(periodIndex)) {
                    sb.append(" and f.period_index ='").append(periodIndex).append("' ");
                }
                if (StringUtils.isNotEmpty(strEqpId)) {
                    sb.append(" and e.equipment_id in(").append(strEqpId).append(") ");
                }
                sb.append("                   AND f.equipment_id = e.equipment_id) b");
                sb.append("         WHERE b.part_no = a.part_no(+)");
                sb.append("           AND b.part_type = a.part_type(+)");
                sb.append("           AND b.pm_index = a.pm_index(+)) t,");
                sb.append("       (select *");
                sb.append("          from mcs_sap_mtr_table p, part_plant y");
                sb.append("         where p.plant = y.plant) t2");
                sb.append(" where t.part_no not in (select part_no from parts_filter) and t.part_no = t2.matnr(+)");
                sb.append("   and t.part_type = t2.type(+)");

            } else {// 异常保养
                sb.append("select t.*, t2.verpr");
                sb.append("  from (SELECT e.maint_dept,");
                sb.append("               f.equipment_id,");
                sb.append("               f.abnormal_name pm_name,");
                sb.append("               f.start_time,");
                sb.append("               u.part_no,");
                sb.append("               u.part_name,");
                sb.append("               U.MTR_GRP,");
                sb.append("               u.part_type,");
                sb.append("               u.part_count used,");
                sb.append("               0 part_count,");
                sb.append("               u.part_count - 0 count_diff,");
                sb.append("               f.abnormal_index");
                sb.append("          FROM abnormal_form f, equipment e, parts_use u");
                sb.append("         WHERE TO_CHAR(f.start_time, 'yyyy-mm') =");
                sb.append("               TO_CHAR(SYSDATE, 'yyyy-mm')");
                sb.append("           AND f.status = 1 and u.mtr_grp in ('20008P', '20008S', '100088', '100085','100093','100083','100084')");
                if (StringUtils.isNotEmpty(equipmentDept)) {
                    sb.append(" and e.maint_dept ='").append(equipmentDept).append("' ");
                }
                if (StringUtils.isNotEmpty(equipmentType)) {
                    sb.append(" and e.equipment_type ='").append(equipmentType).append("' ");
                }
                if (StringUtils.isNotEmpty(partNo)) {
                    sb.append(" and u.part_no like'%").append(partNo).append("%' ");
                }
                if (StringUtils.isNotEmpty(strEqpId)) {
                    sb.append(" and e.equipment_id in(").append(strEqpId).append(") ");
                }
                sb.append("           AND f.abnormal_index = u.event_index");
                sb.append("           AND f.equipment_id = e.equipment_id) t,");
                sb.append("       (select *");
                sb.append("          from mcs_sap_mtr_table p, part_plant y");
                sb.append("         where p.plant = y.plant) t2");
                sb.append(" where t.part_no not in (select part_no from parts_filter) and t.part_no = t2.matnr(+)");
                sb.append("   and t.part_type = t2.type(+)");
            }
            list = SQLProcess.excuteSQLQuery(sb.toString(), delegator);
        }


        return list;
    }
    
    /**
     * PM费用汇总查询
     * @param delegator
     * @author qinchao
     * @return
     * @throws SQLProcessException
     */
    public static List queryPartsUsedCostDetail(GenericDelegator delegator, Map map) throws SQLProcessException {
        /* 得到页面参数值 */
        String equipmentDept = (String) map.get("equipmentDept");
        String equipmentType = (String) map.get("equipmentType");
        String pmType = (String) map.get("pmType");
        String month = (String) map.get("month");
        String periodIndex = (String) map.get("periodIndex");
        String strEqpId = (String) map.get("strEqpId");
        StringBuffer sbHist = new StringBuffer();
        sbHist.append("select * from mcs_parts_cost_monthly_hist t where t.record_month='").append(month)
                .append("' and maint_dept='").append(equipmentDept).append("' and pm_type='").append(pmType)
                .append("'");
        if (StringUtils.isNotEmpty(equipmentType)) {
            sbHist.append(" and t.equipment_type ='").append(equipmentType).append("' ");
        }
        if (StringUtils.isNotEmpty(periodIndex)) {
            sbHist.append(" and t.period_index ='").append(periodIndex).append("' ");
        }
        if (StringUtils.isNotEmpty(strEqpId)) {
            sbHist.append(" and t.equipment_id in(").append(strEqpId).append(") ");
        }
        sbHist.append(" order by t.equipment_id,t.start_time desc");
        List list = SQLProcess.excuteSQLQuery(sbHist.toString(), delegator);

        if (list == null || list.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            if (Constants.PM.equals(pmType)) {
                sb.append("select t.maint_dept,t.equipment_id,t.pm_name,t.start_time,t.pm_index, sum(nvl(t2.verpr,0)*used) used_cost, sum(nvl(t2.verpr,0)*part_count) cost,sum(nvl(t2.verpr,0)*used)-sum(nvl(t2.verpr,0)*part_count) cost_diff");
                sb.append("  from (SELECT b.maint_dept,");
                sb.append("               b.equipment_id,");
                sb.append("               b.pm_name,");
                sb.append("               b.start_time,");
                sb.append("               b.part_no,");
                sb.append("               b.part_name,");
                sb.append("               b.part_type,");
                sb.append("               B.MTR_grp,");
                sb.append("               b.part_count used,");
                sb.append("               nvl(a.part_count, 0) part_count,");
                sb.append("               b.pm_index,");
                sb.append("               b.part_count - nvl(a.part_count, 0) count_diff");
                sb.append("          FROM (SELECT e.maint_dept,");
                sb.append("                       f.equipment_id,");
                sb.append("                       f.pm_name,");
                sb.append("                       f.start_time,");
                sb.append("                       t.part_no,");
                sb.append("                       t.part_name,");
                sb.append("                       t.part_type,");
                sb.append("                       t.mtr_grp,");
                sb.append("                       t.part_count,");
                sb.append("                       f.pm_index");
                sb.append("                  FROM pm_form f, equipment e, parts_pm t");
                sb.append("                 WHERE TO_CHAR(f.start_time, 'yyyy-mm') =");
                sb.append("                       TO_CHAR(SYSDATE, 'yyyy-mm')");
                sb.append("                   AND f.status = 1");
                sb.append("                   AND f.period_index = t.period_index");
                if (StringUtils.isNotEmpty(equipmentDept)) {
                    sb.append(" and e.maint_dept ='").append(equipmentDept).append("' ");
                }
                if (StringUtils.isNotEmpty(equipmentType)) {
                    sb.append(" and e.equipment_type ='").append(equipmentType).append("' ");
                }
                if (StringUtils.isNotEmpty(periodIndex)) {
                    sb.append(" and f.period_index ='").append(periodIndex).append("' ");
                }
                if (StringUtils.isNotEmpty(strEqpId)) {
                    sb.append(" and e.equipment_id in(").append(strEqpId).append(") ");
                }
                sb.append("                   AND f.equipment_id = e.equipment_id) a,");
                sb.append("               (SELECT e.maint_dept,");
                sb.append("                       f.equipment_id,");
                sb.append("                       f.pm_name,");
                sb.append("                       f.start_time,");
                sb.append("                       u.part_no,");
                sb.append("                       u.part_name,");
                sb.append("                       U.MTR_GRP,");
                sb.append("                       u.part_type,");
                sb.append("                       u.part_count,");
                sb.append("                       f.pm_index");
                sb.append("                  FROM pm_form f, equipment e, parts_use u");
                sb.append("                 WHERE TO_CHAR(f.start_time, 'yyyy-mm') =");
                sb.append("                       TO_CHAR(SYSDATE, 'yyyy-mm')");
                sb.append("                   AND f.status = 1");
                sb.append("                   AND f.pm_index = u.event_index");
                if (StringUtils.isNotEmpty(equipmentDept)) {
                    sb.append(" and e.maint_dept ='").append(equipmentDept).append("' ");
                }
                if (StringUtils.isNotEmpty(equipmentType)) {
                    sb.append(" and e.equipment_type ='").append(equipmentType).append("' ");
                }
                if (StringUtils.isNotEmpty(periodIndex)) {
                    sb.append(" and f.period_index ='").append(periodIndex).append("' ");
                }
                if (StringUtils.isNotEmpty(strEqpId)) {
                    sb.append(" and e.equipment_id in(").append(strEqpId).append(") ");
                }
                sb.append("                   AND f.equipment_id = e.equipment_id) b");
                sb.append("         WHERE b.part_no = a.part_no(+)");
                sb.append("           AND b.part_type = a.part_type(+)");
                sb.append("           AND b.pm_index = a.pm_index(+)) t,");
                sb.append("       (select *");
                sb.append("          from mcs_sap_mtr_table p, part_plant y");
                sb.append("         where p.plant = y.plant) t2");
                sb.append(" where t.part_no not in (select part_no from parts_filter) and t.part_no = t2.matnr(+)");
                sb.append("   and t.part_type = t2.type(+)");
                sb.append("   group by t.maint_dept,t.equipment_id,t.pm_name,t.start_time,t.pm_index");

            } else {// 异常保养
                sb.append("select t.maint_dept,t.equipment_id,t.pm_name,t.start_time,t.abnormal_index, sum(nvl(t2.verpr,0)*used) used_cost, sum(nvl(t2.verpr,0)*part_count) cost,sum(nvl(t2.verpr,0)*used)-sum(nvl(t2.verpr,0)*part_count) cost_diff");
                sb.append("  from (SELECT e.maint_dept,");
                sb.append("               f.equipment_id,");
                sb.append("               f.abnormal_name pm_name,");
                sb.append("               f.start_time,");
                sb.append("               u.part_no,");
                sb.append("               u.part_name,");
                sb.append("               U.MTR_GRP,");
                sb.append("               u.part_type,");
                sb.append("               u.part_count used,");
                sb.append("               0 part_count,");
                sb.append("               u.part_count - 0 count_diff,");
                sb.append("               f.abnormal_index");
                sb.append("          FROM abnormal_form f, equipment e, parts_use u");
                sb.append("         WHERE TO_CHAR(f.start_time, 'yyyy-mm') =");
                sb.append("               TO_CHAR(SYSDATE, 'yyyy-mm')");
                sb.append("           AND f.status = 1");
                if (StringUtils.isNotEmpty(equipmentDept)) {
                    sb.append(" and e.maint_dept ='").append(equipmentDept).append("' ");
                }
                if (StringUtils.isNotEmpty(equipmentType)) {
                    sb.append(" and e.equipment_type ='").append(equipmentType).append("' ");
                }
                if (StringUtils.isNotEmpty(strEqpId)) {
                    sb.append(" and e.equipment_id in(").append(strEqpId).append(") ");
                }
                sb.append("           AND f.abnormal_index = u.event_index");
                sb.append("           AND f.equipment_id = e.equipment_id) t,");
                sb.append("       (select *");
                sb.append("          from mcs_sap_mtr_table p, part_plant y");
                sb.append("         where p.plant = y.plant) t2");
                sb.append(" where t.part_no not in (select part_no from parts_filter) and t.part_no = t2.matnr(+)");
                sb.append("   and t.part_type = t2.type(+)");
                sb.append("   group by t.maint_dept,t.equipment_id,t.pm_name,t.start_time,t.abnormal_index");
            }

            list = SQLProcess.excuteSQLQuery(sb.toString(), delegator);
        }

        return list;
    }
    
	/*
	 * 关键备件使用查询 SQL ...
	 */
	public static List queryKeyPartsUseByCondition(GenericDelegator delegator, Map map) throws Exception {

		String startDate = (String) map.get("startDate");
		String endDate = (String) map.get("endDate");
		String equipmentId = (String) map.get("equipmentId");
		String partNo = (String) map.get("partNo");
		String keydesc = (String) map.get("keydesc");
		String eqpmodel = (String) map.get("eqpmodel");
		String section = (String) map.get("section");
		String maintDept = (String) map.get("maintDept");
		String isalarm = (String) map.get("isalarm");
		String isdelay = (String) map.get("isdelay");
		String isError = (String) map.get("isError");
		String isUsed = (String) map.get("isUsed");
		String sql = "";
		String sql1 = "";
		String sqlcontion = "";
		String sqlcontion1 = "";
		String sqlcontion0 = "";

		if (StringUtils.isNotEmpty(equipmentId)) {
			sqlcontion = sqlcontion + " and t3.eqp_id like '%" + equipmentId + "%' ";
		}
		if (StringUtils.isNotEmpty(keydesc)) {
			sqlcontion = sqlcontion + " and t4.keydesc like '%" + keydesc + "%' ";
		}

		if (StringUtils.isNotEmpty(eqpmodel)) {
			sqlcontion = sqlcontion + " and   t4.eqp_type like '%" + eqpmodel + "%' ";
		}

		if (StringUtils.isNotEmpty(startDate)) {
			sqlcontion = sqlcontion + " and   t3.UPDATE_TIME>=to_date('" + startDate + "','yyyy-mm-dd hh24:mi:ss')";
		}
		if (StringUtils.isNotEmpty(endDate)) {
			sqlcontion = sqlcontion + " and   t3.UPDATE_TIME<=to_date('" + endDate + "','yyyy-mm-dd hh24:mi:ss')";
		}
		if (StringUtils.isNotEmpty(section)) {
			sqlcontion = sqlcontion + " and t5.section='" + section + "'";
		}
		if (StringUtils.isNotEmpty(maintDept)) {
			sqlcontion = sqlcontion + " and t5.maint_dept='" + maintDept + "'";
		}
		if (StringUtils.isNotEmpty(isalarm) && isalarm.equals("Y")) {
			sqlcontion0 = sqlcontion0
					+ "and t4.limit_type='TIME(天)' and (nvl(floor(sysdate - t3.update_time), 0) + NVL(T3.INIT_LIFE, 0) - nvl(t6.delay_life, 0))>=nvl(t4.warn_spec,0) ";
			sqlcontion1 = sqlcontion1
					+ "and t4.limit_type='TIME(天)' and (nvl(floor(trunc(t3.create_time, 'dd') - trunc(t3.update_time, 'dd')), 0) + NVL(T3.INIT_LIFE, 0) - nvl(t6.delay_life, 0))>=nvl(t4.warn_spec,0) ";

		}
		if (StringUtils.isNotEmpty(isError) && isError.equals("Y")) {
			sqlcontion0 = sqlcontion0
					+ "and t4.limit_type='TIME(天)' and nvl(floor(sysdate - t3.update_time), 0) + NVL(T3.INIT_LIFE, 0)- nvl(t6.delay_life, 0)-nvl(t4.error_spec, 0) >0 ";
			sqlcontion1 = sqlcontion1
					+ "and t4.limit_type='TIME(天)' and nvl(floor(trunc(t3.create_time, 'dd') - trunc(t3.update_time, 'dd')), 0) + NVL(T3.INIT_LIFE, 0)- nvl(t6.delay_life, 0)-nvl(t4.error_spec, 0) >0 ";
		}
		if (StringUtils.isNotEmpty(isdelay)) {
			if (isdelay.equals("Y")) {
				sqlcontion = sqlcontion + " and nvl(t3.delaytime, 0)>0";
			} else {
				sqlcontion = sqlcontion + " and nvl(t3.delaytime, 0)=0";
			}
		}
		sql = "  select  t3.parts_type,t5.equipment_type, t3.eqp_id, t4.parts_id,t4.parts_name,t4.limit_type, "
				+ " nvl(t4.error_spec,0) error_spec,  nvl(t4.warn_spec,0) warn_spec, t4.eqp_type,t4.keydesc,to_char(t1.update_time,'yyyy/MM/dd HH24:mi') updatetime, "
				+ " t3.vendor ,t3.key_use_id, t3.series_no,   t3.base_sn,t3.status, "
				+ " to_char(T3.CREATE_TIME,'yyyy/MM/dd') create_time,nvl(floor(sysdate - trunc(t3.update_time, 'dd')), 0) + NVL(T3.INIT_LIFE, 0) actul,"
				+ "to_char(t3.update_time,'yyyy/MM/dd') update_time,T1.REMARK,T1.TRANS_BY,"
				+ " nvl(t3.delaytime,0) delaytime,nvl(t6.delay_life, 0) delay_life,t3.init_life "
				+ " from key_parts_use t3 " + " left join parts_use t1 on t1.seq_index=t3.parts_use_id "
				+ " LEFT JOIN form_job_relation t2 ON t1.flow_index = t2.job_index and t1.event_index=t2.event_index"
				+ " left join key_eqp_parts t4 on t3.key_parts_id=t4.key_parts_id "
				+ " left join equipment t5 on t3.eqp_id = t5.equipment_id  "
				+ " left join (select sum(t7.delay_life) delay_life,t7.key_use_id from key_parts_delay_info t7 group by key_use_id) t6 "
				+ " on t6.key_use_id=t3.key_use_id"
				+ " where  t4.enable='Y' AND t4.Is_Alarm = 'Y' and t3.status='USING' "// and t4.limit_type='TIME(天)'
				+ sqlcontion + sqlcontion0;

		/* 已下机件寿命计算 */
		sql1 = " select  t3.parts_type,t5.equipment_type, t3.eqp_id, t4.parts_id,t4.parts_name,t4.limit_type, "
				+ " nvl(t4.error_spec,0) error_spec,  nvl(t4.warn_spec,0) warn_spec, t4.eqp_type,t4.keydesc,to_char(t1.update_time,'yyyy/MM/dd HH24:mi') updatetime, "
				+ " t3.vendor ,t3.key_use_id, t3.series_no,   t3.base_sn,t3.status, "
				+ " to_char(T3.CREATE_TIME,'yyyy/MM/dd') create_time,nvl(floor(trunc(t3.create_time, 'dd') - trunc(t3.update_time, 'dd')), 0) + NVL(T3.INIT_LIFE, 0) actul,"
				+ "to_char(t3.update_time,'yyyy/MM/dd') update_time,T1.REMARK,T1.TRANS_BY,"
				+ " nvl(t3.delaytime,0) delaytime,nvl(t6.delay_life, 0) delay_life,t3.init_life "
				+ " from key_parts_use t3 " + " left join parts_use t1 on t1.seq_index=t3.parts_use_id "
				+ " LEFT JOIN form_job_relation t2 ON t1.flow_index = t2.job_index and t1.event_index=t2.event_index"
				+ " left join key_eqp_parts t4 on t3.key_parts_id=t4.key_parts_id "
				+ " left join equipment t5 on t3.eqp_id = t5.equipment_id  "
				+ " left join (select sum(t7.delay_life) delay_life,t7.key_use_id from key_parts_delay_info t7 group by key_use_id) t6 "
				+ " on t6.key_use_id=t3.key_use_id"
				+ " where  t4.enable='Y' AND t4.Is_Alarm = 'Y' and t3.status='OFFLINE' "// and t4.limit_type='TIME(天)'
				+ sqlcontion + sqlcontion1;
		sql = sql + " UNION " + sql1;
		if (StringUtils.isNotEmpty(isUsed) && isUsed.equals("Y")) {
			sql = "select * from (" + sql + ") t0 where t0.status='USING' order by eqp_id ";
		} else if (StringUtils.isNotEmpty(isUsed) && isUsed.equals("N")) {
			sql = "select * from (" + sql + ") t0 where t0.status='OFFLINE' order by eqp_id ";
		} else {
			sql = sql + "order by eqp_id";
		}

		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}
	
	/**
	 * 得到设备大类列表
	 * 
	 * @param menuGroup
	 * @param delegator
	 * @return
	 */
	public static List getEquipMentList(GenericDelegator delegator) throws Exception {
		List list = delegator.findAllCache("EquipmentType");
		return list;
	}
	
	public static List getSectionListbyDeptIndex(GenericDelegator delegator,String deptIndex) throws Exception {
        List list = delegator.findByAndCache("EquipmentSection",UtilMisc.toMap("deptIndex", deptIndex),UtilMisc.toList("section"));
        return list;
    }
	
	public static List queryKeyPartsEqpByCondition(GenericDelegator delegator, Map map) throws Exception {
		String equipmentId = (String) map.get("equipmentId");
		String eqpmodel = (String) map.get("eqpmodel");
		String section = (String) map.get("section");
		String maintDept = (String) map.get("maintDept");
		String isWarn = (String) map.get("isWarn");
		String isdelay = (String) map.get("isdelay");
		String isError = (String) map.get("isError");
		String sql = "";
		String sqlcontion = "";

		if (StringUtils.isNotEmpty(equipmentId)) {
			sqlcontion = sqlcontion + " and t1.equipment_id like '%" + equipmentId + "%' ";
		}
		if (StringUtils.isNotEmpty(eqpmodel)) {
			sqlcontion = sqlcontion + " and   t0.eqp_type like '%" + eqpmodel + "%' ";
		}
		if (StringUtils.isNotEmpty(section)) {
			sqlcontion = sqlcontion + " and t1.section='" + section + "'";
		}
		if (StringUtils.isNotEmpty(maintDept)) {
			sqlcontion = sqlcontion + " and t0.maint_dept='" + maintDept + "'";
		}
		if (StringUtils.isNotEmpty(isdelay)) {
			if (isdelay.equals("Y")) {
				sqlcontion = sqlcontion + " and nvl(t2.delaytime, 0)>0";
			} else {
				sqlcontion = sqlcontion + " and nvl(t2.delaytime, 0)=0";
			}
		}

		sql = "  select t2.parts_type,t1.equipment_group, t1.equipment_type, t1.equipment_id eqp_id, t0.parts_id,t0.parts_name,t0.limit_type, "
				+ " nvl(t0.error_spec,0) error_spec,  nvl(t0.warn_spec,0) warn_spec, t0.eqp_type,t0.keydesc, "
				+ " t2.vendor ,t2.key_use_id, t2.series_no,   t2.base_sn,t2.status, "
				+ " to_char(T2.CREATE_TIME,'yyyy/MM/dd') create_time,nvl(floor(sysdate - t2.update_time), 0) + NVL(T2.INIT_LIFE, 0) actul,"
				+ "to_char(t2.update_time,'yyyy/MM/dd') update_time,T2.REMARK,T2.Update_User TRANS_BY,"
				+ " nvl(t2.delaytime,0) delaytime,nvl(t6.delay_life, 0) delay_life,t1.section,NVL(T2.INIT_LIFE, 0) init_life "
				+ " from key_eqp_parts t0   " + " left join equipment t1 on t0.eqp_type = t1.model  "
				+ " left join (select * from key_parts_use where status<>'OFFLINE') t2 on t0.key_parts_id = t2.key_parts_id  and t1.equipment_id = t2.eqp_id "
				+ " left join parts_use t3 on t2.parts_use_id = t3.seq_index  "
				+ " left join (select sum(t7.delay_life) delay_life,t7.key_use_id from key_parts_delay_info t7 group by key_use_id) t6 "
				+ " on t6.key_use_id=t2.key_use_id" + " where  t0.enable='Y' "// AND t4.Is_Alarm = 'Y' and
																				// t4.limit_type='TIME(天)' and
																				// t3.status='INUSE'"
				+ sqlcontion + " order by t1.equipment_id,t0.parts_id";

		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}
	
	public static List queryKeyPartsUpdateByCondition(GenericDelegator delegator, Map map) throws Exception {
		String eqpmodel = (String) map.get("eqpmodel");
		String section = (String) map.get("section");
		String maintDept = (String) map.get("maintDept");
		String startDate = (String) map.get("startDate");
		String endDate = (String) map.get("endDate");
		String sql = "";
		String sqlcontion = "";

		if (StringUtils.isNotEmpty(startDate)) {
			sqlcontion = sqlcontion + " and   t.UPDATE_TIME>=to_date('" + startDate + "','yyyy-mm-dd hh24:mi:ss')";
		}
		if (StringUtils.isNotEmpty(endDate)) {
			sqlcontion = sqlcontion + " and   t.UPDATE_TIME<=to_date('" + endDate + "','yyyy-mm-dd hh24:mi:ss')";
		}
		if (StringUtils.isNotEmpty(maintDept)) {
			sqlcontion = sqlcontion + " and t.maint_dept='" + maintDept + "'";
		}
		if (StringUtils.isNotEmpty(section)) {
			sqlcontion = sqlcontion + " and t1.region='" + section + "'";
		}
		if (StringUtils.isNotEmpty(eqpmodel)) {
			sqlcontion = sqlcontion + " and   t.eqp_type like '%" + eqpmodel + "%' ";
		}

		sql = "  select t.*,t1.region from key_eqp_parts_hist t left join parts t1 on t.parts_id=t1.part_no "
				+ " left join (select distinct model,equipment_group from equipment)t2 on t.eqp_type=t2.model where 1=1 "
				+ sqlcontion + " order by t.keydesc desc";

		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}
}
