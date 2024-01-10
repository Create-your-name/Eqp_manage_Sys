package com.csmc.pms.webapp.report.help;

import java.util.List;
import org.ofbiz.entity.GenericDelegator;

import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.util.Constants;

public class DefaultHelper {
	
	/**
	 * 显示当日设备异常清单
	 * @param delegator
	 * @param dept
	 * @return
	 * @throws SQLProcessException
	 */
	public static List getPmAbnormalRecordList(GenericDelegator delegator,
			String dept) throws SQLProcessException {
		String strSQL = "select a.equipment_id,";
		strSQL += "       a.start_time,";
		strSQL += "       a.form_index,";
		strSQL += "       c.start_time START_TIME,";
		strSQL += "       a.start_time CREATE_TIME,";
		strSQL += "       a.status,";
		strSQL += "       c.abnormal_index,";
		strSQL += "       b.MAINT_DEPT,";
		strSQL += "       round((c.end_time - c.start_time) * 24,2) TREATTIME";
		strSQL += "  from pm_abnormal_record a, equipment b, abnormal_form c";
		strSQL += " where a.equipment_id = b.equipment_id";
		strSQL += "   and a.start_time >= trunc(sysdate)";
		strSQL += "   and a.form_index = c.abnormal_index(+)";
		strSQL += "   and a.status != 1";
		strSQL += "   and (c.status != 3 or c.status is null)";

		if (dept.equals("") || dept.equals("ALL")) {
		} else {
			strSQL += " AND b.MAINT_DEPT = '" + dept + "'";
		}

		strSQL += " order by a.equipment_id, b.MAINT_DEPT";
		List pm_abnormal_record_List = SQLProcess.excuteSQLQuery(strSQL, delegator);
		return pm_abnormal_record_List;
	}

	/**
	 * 目前设备不定期参数超过最小值清单
	 * @param delegator
	 * @param dept
	 * @return
	 * @throws SQLProcessException
	 */
	public static List getUnscheduleEqpParamList(GenericDelegator delegator,
			String dept) throws SQLProcessException {
		String strSQL = " SELECT ";
		strSQL += "   UNSCHEDULE_EQP_PARAM.EQUIPMENT_ID ";
		strSQL += "  ,UNSCHEDULE_EQP_PARAM.PARAM_NAME ";
		strSQL += "  ,UNSCHEDULE_EQP_PARAM.STD_FLAG ";
		strSQL += "  ,UNSCHEDULE_EQP_PARAM.VALUE ";
		strSQL += "  ,UNSCHEDULE_EQP_PARAM.MAX_VALUE ";
		strSQL += "  ,UNSCHEDULE_EQP_PARAM.MIN_VALUE ";
		strSQL += "  ,Equipment.MAINT_DEPT ";
		strSQL += " FROM  ";
		strSQL += "   UNSCHEDULE_EQP_PARAM ";
		strSQL += " LEFT OUTER JOIN ";
		strSQL += "   Equipment ";
		strSQL += " ON ";
		strSQL += "     Equipment.EQUIPMENT_ID = UNSCHEDULE_EQP_PARAM.EQUIPMENT_ID ";
		strSQL += " WHERE ";
		strSQL += "     to_number(UNSCHEDULE_EQP_PARAM.VALUE) > to_number(UNSCHEDULE_EQP_PARAM.MIN_VALUE) ";
		strSQL += " AND UNSCHEDULE_EQP_PARAM.SORT = 0 ";

		if (dept.equals("") || dept.equals("ALL")) {
		} else {
			strSQL += " AND Equipment.MAINT_DEPT = '" + dept + "'";
		}

		List unschedule_eqp_param_List = SQLProcess.excuteSQLQuery(strSQL, delegator);
		return unschedule_eqp_param_List;
	}
	
	/**
	 * 当日未完成的巡检
	 * @param delegator
	 * @param dept
	 * @return
	 * @throws SQLProcessException
	 */
	public static List getPeriodScheduleList(GenericDelegator delegator, String dept) throws SQLProcessException {
		String strSQL = "SELECT (t2.NAME || '(' || t3.PERIOD_NAME || ')') NAME";
		strSQL += " FROM PERIOD_SCHEDULE t1, PC_STYLE t2, PC_PERIOD t3";
		strSQL += " WHERE t1.schedule_date = trunc(sysdate)";
		strSQL += " AND t1.event_index is null";
		strSQL += " AND t1.PC_STYLE_INDEX = t2.STYLE_INDEX";
		strSQL += " AND t1.PERIOD_INDEX = t3.PERIOD_INDEX";

		// 根据PC_STYLE的名称中是否出现部门的名称来决定PC的归属，没有特定名称的归为设施部，huanghp,2009.1.6
		if (dept.equals("") || dept.equals("ALL")) {
		} else {
			if (Constants.DEPT_PF.equals(dept)) {
				for (int i = 0; i < Constants.DEPTS.length; i++) {
					if (!Constants.DEPT_PF.equals(Constants.DEPTS[i])) {
						strSQL += " and instr(t2.NAME,'" + Constants.DEPTS[i].substring(0, 2) + "') <> 1";
					}
				}
			} else {
				strSQL += " and t2.NAME like '" + dept.substring(0, 2) + "%'";				
			}
		}
		
		strSQL += " order by t2.NAME";	

		List period_schedule_List = SQLProcess.excuteSQLQuery(strSQL, delegator);
		return period_schedule_List;
	}

	/**
	 * 当日已完成的巡检
	 * @param delegator
	 * @return List
	 * @throws SQLProcessException
	 */
	public static List getPcFormList(GenericDelegator delegator)
			throws SQLProcessException {
		String strSQL = " SELECT ";
		strSQL += "   (PC_STYLE.NAME || '(' || PC_PERIOD.PERIOD_NAME || ')') NAME ";
		//strSQL += "  ,Equipment.MAINT_DEPT ";
		strSQL += "  ,FORM_JOB_RELATION.SEQ_INDEX ";
		strSQL += "  ,DEFAULT_PERIOD.PERIOD_DESC ";
		strSQL += "  ,PC_FORM.PC_INDEX ";
		strSQL += "  ,PC_FORM.STATUS ";
		strSQL += " FROM  ";
		strSQL += "   PC_FORM ";
		strSQL += " LEFT OUTER JOIN ";
		strSQL += "   PC_STYLE ";
		strSQL += " ON ";
		strSQL += "     PC_STYLE.STYLE_INDEX = PC_FORM.STYLE_INDEX ";
		strSQL += " LEFT OUTER JOIN ";
		strSQL += "    FORM_JOB_RELATION ";
		strSQL += " ON ";
		strSQL += "     FORM_JOB_RELATION.EVENT_TYPE = 'PC' ";
		strSQL += " AND FORM_JOB_RELATION.EVENT_INDEX = PC_FORM.PC_INDEX ";
		strSQL += " LEFT OUTER JOIN DEFAULT_PERIOD ";
		strSQL += " ON ";
		strSQL += "     DEFAULT_PERIOD.PERIOD_INDEX = PC_FORM.PERIOD_INDEX ";
		strSQL += " LEFT OUTER JOIN ";
		strSQL += "   PC_PERIOD ";
		strSQL += " ON ";
		strSQL += "    PC_PERIOD.PERIOD_INDEX = PC_FORM.PERIOD_INDEX ";

		strSQL += " WHERE ";
		strSQL += "     PC_FORM.END_TIME >= trunc(sysdate) ";
		strSQL += " AND PC_FORM.STATUS = 1 ";
		
		List pc_form_List = SQLProcess.excuteSQLQuery(strSQL, delegator);
		return pc_form_List;
	}
	
	/**
	 * 当日未保养
	 * @param delegator
	 * @param Dept
	 * @return
	 * @throws SQLProcessException
	 */
	public static List getEquipmentScheduleList(GenericDelegator delegator,
			String Dept) throws SQLProcessException {
		String strSQL = "select a.equipment_id, d.period_name, d.standard_hour,";
		strSQL += " (to_char(a.SCHEDULE_DATE,'yyyy-MM-dd') || ' 09:00:00') START_TIME";
		strSQL += "   ,to_char(to_date((to_char(a.SCHEDULE_DATE,'yyyy-MM-dd') || ' 09:00:00'),'yyyy-MM-dd hh24:mi:ss') + d.STANDARD_HOUR/24,'yyyy-MM-dd hh24:mi:ss') END_TIME ";
		strSQL += "   ,b.MAINT_DEPT";
		strSQL += "   ,b.MSA";
		strSQL += "   ,a.SCHEDULE_INDEX ";
		strSQL += "  ,NVL(c.STATUS,10) STATUS ";
		strSQL += "  ,c.PM_INDEX ";
		strSQL += "  from equipment_schedule a, equipment b, pm_form c, default_period d";
		strSQL += " where a.schedule_date = trunc(sysdate)";
		strSQL += "   and a.equipment_id = b.equipment_id";
		strSQL += "   and a.event_index = c.pm_index(+)";
		strSQL += "   and a.period_index = d.period_index";
		//strSQL += "   and (d.default_days > " + Constants.MAX_DAYPM_DEFAULT_DAYS + " or d.default_days = 0)";
		//strSQL += "   and (c.status != 1 or c.status is null)";
		strSQL += "   and c.status is null";

		if (Dept.equals("") || Dept.equals("ALL")) {
		} else if (AccountHelper.isDeptQC(Dept)) {
		    strSQL += " AND b.msa = 'Y'";
		} else {
			strSQL += " AND b.MAINT_DEPT = '" + Dept + "'";
		    strSQL += " AND d.period_name not like 'MSA%'";  // 非质量人员不能查看MSA表单
		}

		strSQL += "  order by a.equipment_id";

		List equipment_schedule_List = SQLProcess.excuteSQLQuery(strSQL, delegator);
		return equipment_schedule_List;
	}

	/**
	 * 当日完成保养的设备清单
	 * @param delegator
	 * @param dept
	 * @return
	 * @throws SQLProcessException
	 */
	public static List getPmFormList(GenericDelegator delegator, String dept)
			throws SQLProcessException {
		String strSQL = " SELECT ";
		strSQL += "   PM_FORM.EQUIPMENT_ID ";
		strSQL += "  ,DEFAULT_PERIOD.period_name PM_NAME ";
		strSQL += "  ,ROUND((PM_FORM.END_TIME - PM_FORM.START_TIME)*24,2) TRUE_TIME ";
		strSQL += "  ,DEFAULT_PERIOD.STANDARD_HOUR STANDARD_HOUR ";
		strSQL += "  ,PM_FORM.START_TIME START_TIME ";
		strSQL += "  ,PM_FORM.END_TIME END_TIME ";
		strSQL += "  ,Equipment.MAINT_DEPT ";

		// new add by huanghp
		strSQL += "  ,t.seq_index,t.job_name ";

		strSQL += "   FROM PM_FORM, DEFAULT_PERIOD, Equipment ";

		// new add by huanghp
		strSQL += "  , form_job_relation t ";
		strSQL += " WHERE ";
		strSQL += "     PM_FORM.END_TIME >= trunc(sysdate) ";
		strSQL += " AND PM_FORM.STATUS = 1 ";
		//strSQL += " AND DEFAULT_PERIOD.default_days > 1 ";
		strSQL += " AND PM_FORM.PERIOD_INDEX = DEFAULT_PERIOD.PERIOD_INDEX(+)  ";
		strSQL += " AND PM_FORM.EQUIPMENT_ID = Equipment.EQUIPMENT_ID(+)  ";

		// new add by huanghp
		strSQL += " and PM_FORM.PM_INDEX = t.event_index and t.event_type = 'PM'";

		if (dept.equals("") || dept.equals("ALL")){
		} else if (AccountHelper.isDeptQC(dept)) {
            strSQL += " AND Equipment.msa = 'Y'";
        } else {
			strSQL += " AND Equipment.MAINT_DEPT = '" + dept + "'";
			strSQL += " AND DEFAULT_PERIOD.period_name not like 'MSA%'";  // 非质量人员不能查看MSA表单
		}
		
		strSQL += " order by PM_FORM.END_TIME";
		
		List pm_form_List = SQLProcess.excuteSQLQuery(strSQL, delegator);
		return pm_form_List;
	}
	
	/**
	 * 明天保养清单
	 * @param delegator
	 * @param dept
	 * @return List
	 * @throws SQLProcessException
	 */
	public static List getTommorowPmList(GenericDelegator delegator, String dept)
			throws SQLProcessException {
		String strSQL = "SELECT ";
		strSQL += "   EQUIPMENT_SCHEDULE.EQUIPMENT_ID ";
		strSQL += "   ,DEFAULT_PERIOD.PERIOD_NAME ";
		strSQL += "   ,DEFAULT_PERIOD.STANDARD_HOUR ";
		strSQL += "   ,(to_char(EQUIPMENT_SCHEDULE.SCHEDULE_DATE,'yyyy-MM-dd') || ' 09:00:00') START_TIME ";
		strSQL += "   ,to_char(to_date((to_char(EQUIPMENT_SCHEDULE.SCHEDULE_DATE,'yyyy-MM-dd') || ' 09:00:00'),'yyyy-MM-dd hh24:mi:ss') + DEFAULT_PERIOD.STANDARD_HOUR/24,'yyyy-MM-dd hh24:mi:ss') END_TIME ";
		strSQL += "  ,Equipment.MAINT_DEPT ";
		strSQL += "   ,EQUIPMENT_SCHEDULE.SCHEDULE_INDEX ";
		strSQL += " FROM  ";
		strSQL += "   EQUIPMENT_SCHEDULE ";
		strSQL += " LEFT OUTER JOIN ";
		strSQL += "   DEFAULT_PERIOD ";
		strSQL += " ON ";
		strSQL += "     EQUIPMENT_SCHEDULE.PERIOD_INDEX = DEFAULT_PERIOD.PERIOD_INDEX ";
		strSQL += " LEFT OUTER JOIN ";
		strSQL += "   Equipment ";
		strSQL += " ON ";
		strSQL += "     Equipment.EQUIPMENT_ID = EQUIPMENT_SCHEDULE.EQUIPMENT_ID ";
		strSQL += " WHERE ";
		strSQL += "     EQUIPMENT_SCHEDULE.schedule_date = trunc(sysdate+1) ";
		strSQL += " AND (DEFAULT_PERIOD.default_days > " + Constants.MAX_DAYPM_DEFAULT_DAYS + " or DEFAULT_PERIOD.default_days = 0)";
		
		if (dept.equals("") || dept.equals("ALL")) {
		} else if (AccountHelper.isDeptQC(dept)) {
            strSQL += " AND Equipment.msa = 'Y'";
        } else {
			strSQL += " AND Equipment.MAINT_DEPT = '" + dept + "'";
		}
		
		strSQL += " order by EQUIPMENT_SCHEDULE.EQUIPMENT_ID";
		
		List next_pm_List = SQLProcess.excuteSQLQuery(strSQL, delegator);
		return next_pm_List;
	}
	
	/**
	 * 后天保养清单
	 * @param delegator
	 * @param dept
	 * @return List
	 * @throws SQLProcessException
	 */
	public static List getDayAfterTmrPmList(GenericDelegator delegator, String dept)
			throws SQLProcessException {
		String strSQL = " SELECT ";
		strSQL += "   EQUIPMENT_SCHEDULE.EQUIPMENT_ID ";
		strSQL += "   ,DEFAULT_PERIOD.PERIOD_NAME ";
		strSQL += "   ,DEFAULT_PERIOD.STANDARD_HOUR ";
		strSQL += "   ,(to_char(EQUIPMENT_SCHEDULE.SCHEDULE_DATE,'yyyy-MM-dd') || ' 09:00:00') START_TIME ";
		strSQL += "   ,to_char(to_date((to_char(EQUIPMENT_SCHEDULE.SCHEDULE_DATE,'yyyy-MM-dd') || ' 09:00:00'),'yyyy-MM-dd hh24:mi:ss') + DEFAULT_PERIOD.STANDARD_HOUR/24,'yyyy-MM-dd hh24:mi:ss') END_TIME ";
		strSQL += "  ,Equipment.MAINT_DEPT ";
		strSQL += "   ,EQUIPMENT_SCHEDULE.SCHEDULE_INDEX ";
		strSQL += " FROM  ";
		strSQL += "   EQUIPMENT_SCHEDULE ";
		strSQL += " LEFT OUTER JOIN ";
		strSQL += "   DEFAULT_PERIOD ";
		strSQL += " ON ";
		strSQL += "     EQUIPMENT_SCHEDULE.PERIOD_INDEX = DEFAULT_PERIOD.PERIOD_INDEX ";
		strSQL += " LEFT OUTER JOIN ";
		strSQL += "   Equipment ";
		strSQL += " ON ";
		strSQL += "     Equipment.EQUIPMENT_ID = EQUIPMENT_SCHEDULE.EQUIPMENT_ID ";
		strSQL += " WHERE ";
		strSQL += "     EQUIPMENT_SCHEDULE.schedule_date = trunc(sysdate+2) ";
		strSQL += " AND (DEFAULT_PERIOD.default_days > " + Constants.MAX_DAYPM_DEFAULT_DAYS + " or DEFAULT_PERIOD.default_days = 0)";
		
		if (dept.equals("") || dept.equals("ALL")) {
		} else if (AccountHelper.isDeptQC(dept)) {
            strSQL += " AND Equipment.msa = 'Y'";
        } else {
			strSQL += " AND Equipment.MAINT_DEPT = '" + dept + "'";
		}
		
		strSQL += " order by EQUIPMENT_SCHEDULE.EQUIPMENT_ID";
		
		List doublenext_pm_List = SQLProcess.excuteSQLQuery(strSQL, delegator);
		return doublenext_pm_List;
	}
	
	/**
	 * 后七日(含当日)设施部设备保养清单(不含日保养)
	 * @param delegator
	 * @return List
	 * @throws SQLProcessException
	 */
	public static List getPfNext7dayPmList(GenericDelegator delegator)
			throws SQLProcessException {
		String strSQL = "SELECT ";
		strSQL += "   EQUIPMENT_SCHEDULE.EQUIPMENT_ID ";
		strSQL += "   ,DEFAULT_PERIOD.PERIOD_NAME ";
		strSQL += "   ,DEFAULT_PERIOD.STANDARD_HOUR ";
		strSQL += "   ,(to_char(EQUIPMENT_SCHEDULE.SCHEDULE_DATE,'yyyy-MM-dd') || ' 09:00:00') START_TIME ";
		strSQL += "   ,to_char(to_date((to_char(EQUIPMENT_SCHEDULE.SCHEDULE_DATE,'yyyy-MM-dd') || ' 09:00:00'),'yyyy-MM-dd hh24:mi:ss') + DEFAULT_PERIOD.STANDARD_HOUR/24,'yyyy-MM-dd hh24:mi:ss') END_TIME ";
		strSQL += "  ,Equipment.MAINT_DEPT ";
		strSQL += "   ,EQUIPMENT_SCHEDULE.SCHEDULE_INDEX ";
		strSQL += " FROM  ";
		strSQL += "   EQUIPMENT_SCHEDULE ";
		strSQL += " LEFT OUTER JOIN ";
		strSQL += "   DEFAULT_PERIOD ";
		strSQL += " ON ";
		strSQL += "     EQUIPMENT_SCHEDULE.PERIOD_INDEX = DEFAULT_PERIOD.PERIOD_INDEX ";
		strSQL += " LEFT OUTER JOIN ";
		strSQL += "   Equipment ";
		strSQL += " ON ";
		strSQL += "     Equipment.EQUIPMENT_ID = EQUIPMENT_SCHEDULE.EQUIPMENT_ID ";
		strSQL += " WHERE ";
		strSQL += "     EQUIPMENT_SCHEDULE.schedule_date >= trunc(sysdate)";
		strSQL += " and EQUIPMENT_SCHEDULE.schedule_date <= trunc(sysdate+6)";
		strSQL += " AND (DEFAULT_PERIOD.default_days > " + Constants.MAX_DAYPM_DEFAULT_DAYS + " or DEFAULT_PERIOD.default_days = 0)";
		strSQL += " AND Equipment.MAINT_DEPT = '" + Constants.DEPT_PF + "'";	
		
		strSQL += " order by EQUIPMENT_SCHEDULE.EQUIPMENT_ID";
		
		List nextseven_pm_List = SQLProcess.excuteSQLQuery(strSQL, delegator);
		return nextseven_pm_List;
	}
	
	/**
	 * 前一天已完成保养清单（不含日保养）
	 * @param delegator
	 * @param Dept
	 * @return List
	 * @throws SQLProcessException
	 */
	public static List getLastPmList(GenericDelegator delegator, String dept)
			throws SQLProcessException {
		String strSQL = " SELECT ";
		strSQL += "   PM_FORM.EQUIPMENT_ID ";
		strSQL += "   ,PM_FORM.PM_INDEX ";
		strSQL += "   ,DEFAULT_PERIOD.PERIOD_NAME PM_NAME ";
		strSQL += "   ,DEFAULT_PERIOD.STANDARD_HOUR ";
		strSQL += "   ,(ROUND((PM_FORM.END_TIME-PM_FORM.START_TIME)*24,2)) TREATTIME ";
		strSQL += "  ,Equipment.MAINT_DEPT ";
		// new add by huanghp
		strSQL += "  ,form_job_relation.seq_index,form_job_relation.job_name ";

		strSQL += " FROM  ";
		strSQL += "   PM_FORM ";
		strSQL += " LEFT OUTER JOIN ";
		strSQL += "   DEFAULT_PERIOD ";
		strSQL += " ON ";
		strSQL += "     PM_FORM.PERIOD_INDEX = DEFAULT_PERIOD.PERIOD_INDEX ";
		strSQL += " LEFT OUTER JOIN ";
		strSQL += "   Equipment ";
		strSQL += " ON ";
		strSQL += "     Equipment.EQUIPMENT_ID = PM_FORM.EQUIPMENT_ID ";

		// new add by huanghp
		strSQL += " JOIN form_job_relation on PM_FORM.PM_INDEX = form_job_relation.event_index and form_job_relation.event_type = 'PM'";

		strSQL += " WHERE ";
		strSQL += "     PM_FORM.END_TIME >= trunc(sysdate-1) and PM_FORM.END_TIME < trunc(sysdate)";
		strSQL += " AND (DEFAULT_PERIOD.default_days > " + Constants.MAX_DAYPM_DEFAULT_DAYS + " or DEFAULT_PERIOD.default_days = 0)";
		
		if (dept.equals("") || dept.equals("ALL")) {
		} else if (AccountHelper.isDeptQC(dept)) {
            strSQL += " AND Equipment.msa = 'Y'";
        } else {
			strSQL += " AND Equipment.MAINT_DEPT = '" + dept + "'";
		}
		
		strSQL += " order by PM_FORM.EQUIPMENT_ID";
		
		List last_pm_List = SQLProcess.excuteSQLQuery(strSQL, delegator);
		return last_pm_List;
	}
	
	/**
	 * dayPm: 当日未作日保养清单
	 * @param delegator
	 * @param dept 部门
	 * @param rangeIndex 保养区段
	 * @return List
	 * @throws SQLProcessException
	 */
	public static List getTodayPmList(GenericDelegator delegator, String dept, String rangeIndex) throws SQLProcessException {		
		String strSQL = " SELECT ";
		strSQL += "   EQUIPMENT_SCHEDULE.EQUIPMENT_ID ";
		strSQL += "  ,Equipment.MAINT_DEPT ";
		strSQL += "   ,EQUIPMENT_SCHEDULE.SCHEDULE_INDEX ";
		strSQL += " FROM  ";
		strSQL += "   EQUIPMENT_SCHEDULE ";
		strSQL += " LEFT OUTER JOIN ";
		strSQL += "   Equipment ";
		strSQL += " ON ";
		strSQL += "     Equipment.EQUIPMENT_ID = EQUIPMENT_SCHEDULE.EQUIPMENT_ID ";
		strSQL += " LEFT OUTER JOIN ";
		strSQL += "   TIME_RANGE ";
		strSQL += " ON ";
		strSQL += "   TIME_RANGE.RANGE_INDEX = EQUIPMENT_SCHEDULE.TIME_RANGE_INDEX";
		strSQL += " WHERE ";
		strSQL += "     EQUIPMENT_SCHEDULE.schedule_date = trunc(sysdate)";
		strSQL += " AND EQUIPMENT_SCHEDULE.event_index is null ";
		
		strSQL =  strSQL + " AND TIME_RANGE.RANGE_INDEX = " + rangeIndex;
		
		if (dept.equals("") || dept.equals("ALL")) {
		} else {
			strSQL += " AND Equipment.MAINT_DEPT = '" + dept + "'";
		}
		
		List today_pm_List = SQLProcess.excuteSQLQuery(strSQL, delegator);
		return today_pm_List;
	}
	
	/**
	 * eventList: 事件List
	 * @param delegator
	 * @param dept 部门
	 * @return List
	 * @throws SQLProcessException
	 */
	public static List getEventList(GenericDelegator delegator, String dept) throws SQLProcessException {
		String strSQL = "SELECT t.*,Equipment.MAINT_DEPT ";
		strSQL += " FROM (";
		strSQL += " (select ABNORMAL_FORM.ABNORMAL_INDEX PM_INDEX,ABNORMAL_FORM.Equipment_Id,ABNORMAL_FORM.ABNORMAL_REASON ABNORMAL_NAME,ABNORMAL_FORM.START_TIME ";
		strSQL += " ,ABNORMAL_FORM.STATUS,'TS' Type from ABNORMAL_FORM ";
		strSQL += " WHERE ABNORMAL_FORM.Status = 0 or ABNORMAL_FORM.Status=2 ";
		strSQL += " AND ABNORMAL_FORM.START_TIME >= trunc(sysdate) ";
		strSQL += " ) union all ";
		strSQL += " (select ABNORMAL_FORM.ABNORMAL_INDEX PM_INDEX,ABNORMAL_FORM.Equipment_Id,ABNORMAL_FORM.ABNORMAL_REASON ABNORMAL_NAME,ABNORMAL_FORM.END_TIME START_TIME ";
		strSQL += " ,ABNORMAL_FORM.STATUS,'TS' Type from ABNORMAL_FORM ";
		strSQL += " WHERE ABNORMAL_FORM.Status = 1 ";
		strSQL += " AND ABNORMAL_FORM.END_TIME >= trunc(sysdate) ";
		strSQL += " ) union all ";
		strSQL += " (select PM_FORM.PM_INDEX PM_INDEX,PM_FORM.Equipment_Id,default_period.period_name PM_NAME,PM_FORM.START_TIME ";
		strSQL += " ,PM_FORM.STATUS,'PM' Type from PM_FORM,default_period  ";
		strSQL += " WHERE (PM_FORM.Status = 0 or PM_FORM.Status=2) ";
		strSQL += " AND PM_FORM.START_TIME >= trunc(sysdate) ";
		strSQL += " AND PM_FORM.PERIOD_INDEX = default_period.period_index(+) ";
		strSQL += " ) union all ";
		strSQL += " (select PM_FORM.PM_INDEX PM_INDEX,PM_FORM.Equipment_Id,default_period.period_name PM_NAME,PM_FORM.END_TIME START_TIME ";
		strSQL += " ,PM_FORM.STATUS,'PM' Type from PM_FORM,default_period  ";
		strSQL += " WHERE PM_FORM.Status = 1 ";
		strSQL += " AND PM_FORM.END_TIME >= trunc(sysdate) ";
		strSQL += " AND PM_FORM.PERIOD_INDEX = default_period.period_index(+) ";

		strSQL += " )) t ";
		strSQL += " LEFT OUTER JOIN ";
		strSQL += "   Equipment ";
		strSQL += " ON  Equipment.EQUIPMENT_ID = t.EQUIPMENT_ID ";
		strSQL += " WHERE ";
		strSQL += "   t.START_TIME >= trunc(sysdate) ";
		
		if (dept.equals("") || dept.equals("ALL")){
		} else {
			strSQL += " AND Equipment.MAINT_DEPT = '" + dept + "'";
		}
		
		strSQL += " order by t.START_TIME";
		
		List eventList = SQLProcess.excuteSQLQuery(strSQL, delegator);
		return eventList;
	}
}
