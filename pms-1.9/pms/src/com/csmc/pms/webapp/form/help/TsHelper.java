package com.csmc.pms.webapp.form.help;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityWhereString;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.common.helper.CommonHelper;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.util.Constants;

/**O
 * 
 * @author shaoaj
 * @2007-9-13
 */
public class TsHelper {
	public static final String module = TsHelper.class.getName();
	
	/**
	 * 查询异常记录信息 
	 * @param delegator
	 * @param parMap 查询参数
	 * @param flag 0：异常查询，1：子母设备查询
	 * @return
	 * @throws Exception
	 */
	public static List queryTsFormList(GenericDelegator delegator,Map parMap,String flag)throws Exception{
		
		String eqpId=(String)parMap.get("eqpId");
		String startDate=(String)parMap.get("startDate");
		String endDate=(String)parMap.get("endDate");
		String maintDept=(String)parMap.get("maintDept");
		String eqpStatus=(String)parMap.get("eqpStatus");
		
		StringBuffer queryString = new StringBuffer();
		queryString.append(" select t.EQUIPMENT_ID EQUIPMENTID,t.SEQ_INDEX SEQINDEX,t.START_TIME STARTTIME,t.EQUIPMENT_STATUS ");
		queryString.append(" from PM_ABNORMAL_RECORD t,equipment t2 where ");
		
		StringBuffer whereSql = new StringBuffer(" 1=1 ");		
		if("0".equals(flag)){
			if(StringUtils.isNotEmpty(eqpId)){
				whereSql.append(" and t.EQUIPMENT_ID='").append(eqpId).append("'");
			}
			if(StringUtils.isNotEmpty(startDate)){
				whereSql.append(" and t.START_TIME>=to_date('").append(startDate).append("','yyyy-mm-dd hh24:mi:ss')");
			}
			if(StringUtils.isNotEmpty(endDate)){
				whereSql.append(" and t.START_TIME<=to_date('").append(endDate).append("','yyyy-mm-dd hh24:mi:ss')");
			}
		}else if("1".equals(flag)){
			whereSql.append(" and t.PARENT_EQP_ID='").append(eqpId).append("'");
			if(StringUtils.isNotEmpty(startDate)){
				whereSql.append(" and t.CREATE_TIME>=to_date('").append(startDate).append("','yyyy-mm-dd hh24:mi:ss')");
			}
			if(StringUtils.isNotEmpty(endDate)){
				whereSql.append(" and t.CREATE_TIME<=to_date('").append(endDate).append("','yyyy-mm-dd hh24:mi:ss')");
			}
		}
		
		if(StringUtils.isNotEmpty(eqpStatus)){
			whereSql.append(" and t.EQUIPMENT_STATUS like '").append(eqpStatus.substring(0, 2)).append("%'");
		}
		
		whereSql.append(" and t.STATUS='").append(Constants.START).append("'");
		whereSql.append(" and t2.equipment_id=t.equipment_id and t2.maint_Dept='").append(maintDept).append("'");
		
		queryString.append(whereSql.toString());
		queryString.append(" order by t.START_TIME");
		
        List list = SQLProcess.excuteSQLQuery(queryString.toString(), delegator);
		return list;
	}
	
	/**
	 * 通过设备ID获得处理程序列表
	 * @param delegator
	 * @param eqpId
	 * @return
	 * @throws Exception
	 */
	public static List getDealProgrammeByEqpType(GenericDelegator delegator,String eqpId)throws Exception{
		GenericValue gv = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId", eqpId));
		List dealProgrammeList=delegator.findByAnd("FlowJob", UtilMisc.toMap("eventType",Constants.TS,"status", String.valueOf(Constants.ENABLE),"eventObject",gv.getString("equipmentType")));
		return dealProgrammeList;
	}
	
	/**
	 * 提交子母设备的异常表单
	 * @param delegator
	 * @param dispatcher
	 * @param tsMap
	 * @throws Exception
	 */
	public static void submitTsform(GenericDelegator delegator, LocalDispatcher dispatcher,Map tsMap,Map parMap)throws Exception{
		Map result = dispatcher.runSync("saveTsFormAction",UtilMisc.toMap("tsInfoMap" ,tsMap,"parmMap",parMap));
		
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
	}
	/**
	 * 保存异常表单信息
	 * @param delegator
	 * @param tsPar
	 * @throws Exception
	 */
	public static Long saveTsForm(GenericDelegator delegator,Map tsPar)throws Exception{
		 GenericValue gv = delegator.makeValidValue("AbnormalForm", tsPar);
		 Long id = delegator.getNextSeqId("abnormalIndex");
		 String formName=CommonHelper.getFormName(Constants.TS_CHAR, (String)tsPar.get("equipmentId"), delegator);
		 gv.put("abnormalIndex", id);
		 gv.put("abnormalName", formName);
		 delegator.create(gv);
		 return id;
	}
	
	/**
	 * 查询是否有重复的异常表单信息（已经提交过的）
	 * @param delegator
	 * @param tsPar
	 * @throws Exception
	 */
	public static boolean hasDuplicatedTsForm(GenericDelegator delegator, Map tsPar)
			throws Exception {
		List gvList = delegator.findByAnd("AbnormalForm", UtilMisc.toMap("equipmentId", tsPar.get("equipmentId"), "status", String.valueOf(Constants.CREAT)));
		if(gvList!=null && !gvList.isEmpty()){
			return true;
		}
		else return false;
	}
	
	/**
	 * 保存异常表单的处理程序信息
	 * @param delegator
	 * @param parMap
	 * @throws Exception
	 */
	public static void saveJobRelation(GenericDelegator delegator,Map parMap)throws Exception{
		GenericValue gv = delegator.makeValidValue("FormJobRelation", parMap);
		Long id = delegator.getNextSeqId("formJobRelationSeqIndex");
		
		GenericValue flowJob=delegator.findByPrimaryKey("FlowJob", UtilMisc.toMap("jobIndex",(String)parMap.get("jobIndex")));
		gv.put("seqIndex", id);
		gv.put("jobContent", flowJob.getString("jobContent"));
		gv.put("jobName", flowJob.getString("jobName"));
		delegator.create(gv);
	}
	
	/**
	 * 保存子母表的绑定信息
	 * @param delegator
	 * @param parMap
	 * @throws Exception
	 */
	public static void saveAbnomalBind(GenericDelegator delegator,Map parMap)throws Exception{
		GenericValue gv = delegator.makeValidValue("AbnormalBinding", parMap);
		delegator.create(gv);
	}
	
	/**
	 * 更新异常记录状态及表单INDEX
	 * @param delegator
	 * @param status
	 * @param index
	 * @throws Exception
	 */
	public static void updateAbnomalRecordStatus(GenericDelegator delegator,String index,String formIndex)throws Exception{
		GenericValue gv = delegator.makeValidValue("PmAbnormalRecord", UtilMisc.toMap("seqIndex",index,"formIndex",formIndex,"status",String.valueOf(Constants.HOLD)));
		delegator.store(gv);
	}
	
	//-----------------------------表单管理---------------------------------------
	/**
	 * 查询异常表单列表
	 * @param delegator
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public static List queryAbnormalFormList(GenericDelegator delegator,String beginDate,String endDate,String maintDept)throws Exception{
		StringBuffer sql=new StringBuffer();
		sql.append("select t.ABNORMAL_INDEX,t.EQUIPMENT_ID,t.ABNORMAL_NAME,t.CREATE_USER,t.CREATE_NAME,t.ABNORMAL_TIME,t.UPDATE_TIME,decode(t.STATUS,'-1','建立未开始','0','开始','2','暂存','1','完成') STATUS,t.FORM_TYPE from ABNORMAL_FORM t,equipment t2 where 1=1");
		sql.append("and t2.equipment_id=t.equipment_id and t2.maint_Dept='").append(maintDept).append("'");
		if(StringUtils.isNotEmpty(beginDate)){
			sql.append(" and t.CREATE_TIME>=to_date('").append(beginDate).append("','yyyy-mm-dd hh24:mi:ss')");
		}
		if(StringUtils.isNotEmpty(endDate)){
			sql.append(" and t.CREATE_TIME< to_date('").append(endDate).append("','yyyy-mm-dd hh24:mi:ss') + 1");
		}
		sql.append(" and t.STATUS!='").append(Constants.BIND).append("'");
		sql.append(" and t.STATUS!='").append(Constants.OVER).append("'");
		sql.append(" order by t.CREATE_TIME desc");
		List list=SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		return list;
	}
	
	/**
	 * 根据条件查询异常表单查询
	 * @param delegator
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public static List queryAbnormalFormByCondition(GenericDelegator delegator,Map parMap)throws Exception{
		String eqpId = (String) parMap.get("eqpId");
		String startDate = (String) parMap.get("startDate");
		String endDate = (String) parMap.get("endDate");
		String maintDept = (String) parMap.get("maintDept");
		StringBuffer sql = new StringBuffer();
		
		sql.append("select t.ABNORMAL_INDEX,t.EQUIPMENT_ID,t.ABNORMAL_NAME,t.CREATE_USER,t.CREATE_NAME,"
						+ "t.ABNORMAL_TIME,t.UPDATE_TIME,'完成' STATUS,t.JOB_TEXT,t.END_USER,t.END_TIME,"
						+ "t.FORM_TYPE,ROUND((t.END_TIME-t.ABNORMAL_TIME)*24,2) man_hour,t.abnormal_text");
		sql.append(" from ABNORMAL_FORM t,equipment t2 where 1=1");
		sql.append(" and t2.equipment_id=t.equipment_id and t2.maint_dept='").append(maintDept).append("'");
		
		if(StringUtils.isNotEmpty(eqpId)){
			sql.append(" and t.equipment_id='").append(eqpId).append("' ");
		}
		if(StringUtils.isNotEmpty(startDate)){
			sql.append(" and t.CREATE_TIME>=to_date('").append(startDate).append("','yyyy-mm-dd hh24:mi:ss')");
		}
		if(StringUtils.isNotEmpty(endDate)){
			sql.append(" and t.CREATE_TIME< to_date('").append(endDate).append("','yyyy-mm-dd hh24:mi:ss') + 1 ");
		}
		
		sql.append(" and t.STATUS ='").append(Constants.OVER).append("'");
//        sql.append(" and t.LOCK_STATUS IS NULL");
		sql.append(" order by t.CREATE_TIME desc");
		List list=SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		return list;
	}
	
	public static List queryUndoAbnormalFormlist(GenericDelegator delegator,String eqpId,String dept)throws Exception{
		StringBuffer sql=new StringBuffer();
		sql.append("select t.ABNORMAL_INDEX,t.EQUIPMENT_ID,t.ABNORMAL_NAME,t.CREATE_USER,t.CREATE_NAME,t.ABNORMAL_TIME,t.UPDATE_TIME,t.STATUS,t.CREATE_TIME from ABNORMAL_FORM t,equipment t2 where 1=1");
		sql.append("and t2.equipment_id=t.equipment_id and t2.maint_dept='").append(dept).append("'");
		if(StringUtils.isNotEmpty(eqpId)){
			sql.append(" and t.equipment_id='").append(eqpId).append("' ");
		}
		sql.append(" and t.STATUS !='").append(Constants.OVER).append("'");
		sql.append(" and t.STATUS !='").append(Constants.BIND).append("'");
		sql.append(" order by t.CREATE_TIME desc");
		List list=SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		return list;
	}
	
	/**
	 * 通过设备编号及原因类型,得到此设备大类下的原因列表
	 * @param delegator
	 * @param eqpId
	 * @param reasonType
	 * @return
	 * @throws Exception
	 */
	public static List getReasonList(GenericDelegator delegator,String eqpId,String reasonType)throws Exception{
		StringBuffer sql=new StringBuffer();
		sql.append("select t.REASON_INDEX,t.REASON from PMS_REASON t,EQUIPMENT t2");
		sql.append(" where t.EQUIPMENT_TYPE=t2.EQUIPMENT_TYPE and t2.EQUIPMENT_ID='").append(eqpId).append("'");
		if(StringUtils.isNotEmpty(reasonType)){
			sql.append(" and t.REASON_TYPE='").append(reasonType).append("'");
		}
		List list=SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		return list;
	}
	
	
	/**
	 * 通过设备编号及原因类型,得到此设备大类下的原因列表
	 * @param delegator
	 * @param eqpId
	 * @param reasonType
	 * @return
	 * @throws Exception
	 */
	public static List getReasonListNew(GenericDelegator delegator,String eqpId,String reasonIndex,String reasonType)throws Exception{
		StringBuffer sql=new StringBuffer();
		sql.append("select t.REASON_INDEX,t.REASON from PMS_REASON t,EQUIPMENT t2");
		sql.append(" where t.EQUIPMENT_TYPE=t2.EQUIPMENT_TYPE and t2.EQUIPMENT_ID='").append(eqpId).append("'");
		if(StringUtils.isNotEmpty(reasonIndex)){
			sql.append("  and t.REASON_INDEX='").append(reasonIndex).append("'");
		}
		if(StringUtils.isNotEmpty(reasonType)){
			sql.append(" and t.REASON_TYPE='").append(reasonType).append("'");
		}
		List list=SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		return list;
	}
	
	/**
	 * 得到设备状态列表
	 * @param delegator
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static List getEqpStatusList(GenericDelegator delegator,String type)throws Exception{
		StringBuffer sql=new StringBuffer();
		sql.append("select EQP_STATUS,EQP_STATUS||'('||DESCRIPTION||')' EQP_DESC from promis_eqp_status t");
		sql.append(" where t.TYPE='").append(type).append("'");
		return SQLProcess.excuteSQLQuery(sql.toString(), delegator);
	}
	
	/**
	 * 结束异常表单
	 * @param delegator
	 * @param parMap
	 * @throws Exception
	 */
	public static void overAbnormalForm(GenericDelegator delegator,Map parMap)throws Exception{
		GenericValue gv = delegator.makeValidValue("AbnormalForm", parMap);
		delegator.store(gv);
	}
	
	/**
	 * 更新异常记录为完成状态
	 * @param delegator
	 * @param abnormalIndex
	 * @throws Exception
	 */
	public static void overAbnormalRecord(GenericDelegator delegator,String abnormalIndex)throws Exception{
		List list=delegator.findByAnd("PmAbnormalRecord", UtilMisc.toMap("formIndex",abnormalIndex));
		GenericValue gv = (GenericValue)list.get(0);
		gv.put("status", String.valueOf(Constants.OVER));
		gv.put("updateTime", new Timestamp(System.currentTimeMillis()));
		gv.put("endTime", new Timestamp(System.currentTimeMillis()));
		delegator.store(gv);
	}
	
	/**
	 * 查找PM表单中此设备未完成非手工补填的表单 
	 * @param delegator
	 * @param eqpId
	 * @return
	 * @throws Exception
	 */
	public static List getPmNoOverFormList(GenericDelegator delegator,String eqpId)throws Exception{
		String sql = " EQUIPMENT_ID='" + eqpId + "' and STATUS!='"
				+ Constants.OVER + "' and is_change_status = 'Y'"
				+ " and FORM_TYPE='" + Constants.FORM_TYPE_NORMAL + "'";
		EntityWhereString con = new EntityWhereString(sql);
		//查询异常表单中是否存在此设备未结束的保养表单
		List pmFormList=delegator.findByCondition("PmForm", con, null, null);
		return pmFormList;
	}
	
	/**
	 * 通过父表单获到了表单List
	 * @param delegator
	 * @param parentIndex
	 * @return
	 * @throws Exception
	 */
	public static List getSonFormList(GenericDelegator delegator,String parentIndex)throws Exception{
		List chindList=delegator.findByAnd("AbnormalBinding",  UtilMisc.toMap("parentIndex",parentIndex));
		return chindList;
	}
	
	/**
	 * 通过子表单获到父表单信息
	 * @param delegator
	 * @param sonIndex
	 * @return
	 * @throws Exception
	 */
	public static List getParentFormList(GenericDelegator delegator,String sonIndex)throws Exception{
		List chindList=delegator.findByAnd("AbnormalBinding",  UtilMisc.toMap("sonIndex",sonIndex));
		return chindList;
	}
	
	/**
	 * 查看异常开始日 到 完成异常动作前一天 是否有未建立的保养计划
	 * @param delegator
	 * @param eqpId
	 * @param abnormalTime
	 * @return List
	 * @throws GenericEntityException 
	 * @throws SQLProcessException 
	 * @throws Exception
	 */
	public static List getTsUnPmScheduleList(GenericDelegator delegator, String eqpId, String abnormalTime) throws SQLProcessException {
        
        String sql = "select t1.schedule_index,t1.schedule_date, t2.period_index,t2.period_name"
				+ " from equipment_schedule t1, default_period t2"
				+ " where t1.period_index=t2.period_index"
				+ " and schedule_date >= to_date('" + abnormalTime.substring(0, 10) + "','yyyy-MM-dd') " 
				+ " and schedule_Date < trunc(sysdate) " 
				+ " and equipment_Id ='" + eqpId + "'"
				+ " and event_index is null"
				+ " order by t2.default_days desc";
		
		List equipmentScheduleList = SQLProcess.excuteSQLQuery(sql, delegator);
        return equipmentScheduleList;
	}
	
	/**
	 * @param eqpStatus
	 * @return
	 */
	public static boolean isPRStartStatus(String eqpStatus) {
		return Constants.PR_START_STATUS.indexOf(eqpStatus) > -1;
	}

	/**
	 * @param eqpStatus
	 * @return
	 */
	public static boolean isEqpStatus03(String eqpStatus) {
		return eqpStatus.substring(0,2).equals("03");
	}
}
