package com.csmc.pms.webapp.eqp.helper;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.GenericDelegator;

import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.util.Constants;

/*
 * @author dinghh
 * dcop��ش���Ҳ�õ�plldb����fab1ʹ�ã�δ�Ƴ����ࣺGenDCOPHelper, WorkflowEvent, WorkflowHelper
 */
public class PlldbHelper {
	
	//pmʱ��ѯ�Ƿ�Ϊmes�豸
	public static boolean isMesEqp(GenericDelegator delegator, String eqpId) throws SQLProcessException {
	    String sql = "select count(*) num from PLLDB_EQPS_VIEW t where t.eqpid = '" + eqpId +"'";
	    List eqpList = SQLProcess.excuteSQLQuery(sql, delegator);
	    Map eqpMap = (Map) eqpList.get(0);
	    String eqpNum = (String) eqpMap.get("NUM");
	    return !"0".equals(eqpNum);
	}

	/**
	 * overPmForm:��ѯ�豸״̬�����޸�ʱ��
	 */
	public static List listEqpMaxChangeDate(GenericDelegator delegator,
			String equipmentId, String eqpStatus) throws SQLProcessException {
		if (Constants.CALL_ASURA_FLAG) return null;

		String sql = "select max(changedt) MAXCHANGEDT"
				+ " from PLLDB_EQPS_VIEW"
				+ " where eqpid = '" + equipmentId
				+ "' and status = '" + eqpStatus + "'";
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}

	/**
	 * ��ѯδ�����ı�����(����)(�豸״̬��03��05)
	 * @param delegator
	 * @param parMap
	 *            ��ѯ����
	 * @param flag
	 *            0���쳣��ѯ��1����ĸ�豸��ѯ
	 * @return
	 * @throws Exception
	 */
	public static List queryPmFormList(GenericDelegator delegator,Map parMap,String flag)throws Exception{
	    String eqpId=(String)parMap.get("eqpId");
	    String startDate=(String)parMap.get("startDate");
	    String endDate=(String)parMap.get("endDate");        
	    String dept=(String)parMap.get("dept");
	    
	    StringBuffer queryString=new StringBuffer();
	    queryString.append("select t1.schedule_index scheduleIndex,t1.equipment_id equipmentId,t1.period_index periodIndex,to_char(t1.schedule_date,'yyyy-MM-dd') scheduleDate,t2.period_name periodName");
	    queryString.append(" from equipment_schedule t1, default_period t2, equipment t3, PLLDB_EQPS_VIEW p");
	    
	    StringBuffer whereSql = new StringBuffer(" where t1.period_index = t2.period_index and t1.equipment_id = t3.equipment_id AND t1.EQUIPMENT_ID = p.eqpid(+)");
	    whereSql.append(" and (not (p.status like '03%' or p.status like '05%') or p.status is null)");
	    whereSql.append(" and t1.event_index is null and t1.schedule_date < trunc(sysdate)");
	    
	    if(StringUtils.isNotEmpty(eqpId)){        
	        if("0".equals(flag)){
	            whereSql.append(" and t3.EQUIPMENT_ID='").append(eqpId).append("'");
	        }else if("1".equals(flag)){         
	            whereSql.append(" and t3.PARENT_EQPID='").append(eqpId).append("'");
	        }
	    }
	    
	    if(StringUtils.isNotEmpty(startDate)){
	        whereSql.append(" and t1.schedule_date>=to_date('").append(startDate).append("','yyyy-mm-dd hh24:mi:ss')");
	    }
	    if(StringUtils.isNotEmpty(endDate)){
	        whereSql.append(" and t1.schedule_date< to_date('").append(endDate).append("','yyyy-mm-dd hh24:mi:ss') + 1 ");
	    }        
	            
	    if(parMap.containsKey("dept")){
	        whereSql.append(" and t3.maint_dept='").append(dept).append("'");
	        whereSql.append(" and t2.period_name not like 'MSA%'");
	    }
	    else if(parMap.containsKey("isMsa")){
	        whereSql.append(" and t3.msa='Y'");
	    }
	    
	    queryString.append(whereSql.toString());
	    queryString.append(" order by t1.schedule_date desc");
	    
	    List list = SQLProcess.excuteSQLQuery(queryString.toString(), delegator);
	    return list;
	}

	/**
	 * ��ʾ��ҳ�����ų�δ������ʷ��¼(������ǰ,��������)�������ձ�����,����״̬
	 * @param delegator
	 * @param dept
	 * @param isEqpStatusDown 
	 * @return List
	 * @throws SQLProcessException
	 */
	public static List getUndoPmList(GenericDelegator delegator, String dept, boolean isEqpStatusDown)
			throws SQLProcessException {
		String strSQL = "SELECT ";
		strSQL += "   t.EQUIPMENT_ID ";
		strSQL += "   ,d.PERIOD_NAME ";
		strSQL += "   ,d.STANDARD_HOUR ";
		strSQL += "   ,(to_char(t.SCHEDULE_DATE,'yyyy-MM-dd') || ' 09:00:00') START_TIME ";
		strSQL += "   ,to_char(to_date((to_char(t.SCHEDULE_DATE,'yyyy-MM-dd') || ' 09:00:00'),'yyyy-MM-dd hh24:mi:ss') + d.STANDARD_HOUR/24,'yyyy-MM-dd hh24:mi:ss') END_TIME ";
		strSQL += "  ,e.MAINT_DEPT ";
		strSQL += "   ,t.SCHEDULE_INDEX ";
		strSQL += " FROM EQUIPMENT_SCHEDULE t,DEFAULT_PERIOD d,Equipment e, PLLDB_EQPS_VIEW p";
		strSQL += " WHERE t.EQUIPMENT_ID = e.EQUIPMENT_ID and t.PERIOD_INDEX = d.PERIOD_INDEX ";
		strSQL += " AND t.EQUIPMENT_ID = p.eqpid(+)";
		strSQL += " and t.schedule_date < trunc(sysdate) and t.event_index is null";
		strSQL += " and (d.default_days > " + Constants.MAX_DAYPM_DEFAULT_DAYS + " or d.default_days = 0)";	
				
		if (dept.equals("") || dept.equals("ALL")){
		} else if (AccountHelper.isDeptQC(dept)) {
	        strSQL += " AND e.msa = 'Y'";
	    } else {
			strSQL += " AND e.MAINT_DEPT = '" + dept + "'";
		}
		
		if (isEqpStatusDown){		
			strSQL += " and (p.status like '03%' or p.status like '05%')";
	    } else {
	    	strSQL += " and (not (p.status like '03%' or p.status like '05%') or p.status is null)";
		}		
		
		strSQL += " ORDER BY t.schedule_date DESC";
		
		List notdo_pm_List = SQLProcess.excuteSQLQuery(strSQL, delegator);
		return notdo_pm_List;
	}

	//�޸��豸״̬����ѯ�Ƿ�Ϊfab5�豸
	public static boolean isFab5Eqp(GenericDelegator delegator, String eqpId) throws SQLProcessException {
		if (Constants.CALL_TP_FLAG || eqpId.startsWith("6")) {
			return false;
		}
		
		String sql = "select count(*) num from RXGZ_EQPS_VIEW t where t.eqpid = '" + eqpId +"'";
	    List eqpList = SQLProcess.excuteSQLQuery(sql, delegator);
	    Map eqpMap = (Map) eqpList.get(0);
	    String eqpNum = (String) eqpMap.get("NUM");
	    return !"0".equals(eqpNum);
	}

	//�޸��豸״̬����ѯ�Ƿ�Ϊfab6�豸
//	public static boolean isFab6Eqp(GenericDelegator delegator, String eqpId) throws SQLProcessException {
//		if (!eqpId.startsWith("6") || Constants.CALL_TP_FLAG) {
//			return false;
//		}
//
//		String sql = "select count(*) num from fab6_eqps_view t where t.eqpid = '" + eqpId +"'";
//	    List eqpList = SQLProcess.excuteSQLQuery(sql, delegator);
//	    Map eqpMap = (Map) eqpList.get(0);
//	    String eqpNum = (String) eqpMap.get("NUM");
//	    return !"0".equals(eqpNum);
//	}

	//��ѯfab6�豸״̬
//	public static String getFab6EqpStatus(GenericDelegator delegator, String eqpId) throws SQLProcessException {
//		String fab6EqpStatus = "";
//
//		String sql = "select status from fab6_eqps_view t where t.eqpid = '" + eqpId +"'";
//	    List eqpList = SQLProcess.excuteSQLQuery(sql, delegator);
//
//	    if (eqpList != null && eqpList.size() > 0) {
//	    	Map eqpMap = (Map) eqpList.get(0);
//	    	fab6EqpStatus = (String) eqpMap.get("STATUS");
//	    }
//
//	    return fab6EqpStatus;
//	}
}
