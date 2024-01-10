package com.csmc.pms.webapp.form.help;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fa.util.Log;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityWhereString;

import com.csmc.pms.webapp.common.helper.CommonHelper;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.eqp.helper.PlldbHelper;
import com.csmc.pms.webapp.parts.help.PartsHelper;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.MiscUtils;

public class PmHelper {
	public static final String module = PmHelper.class.getName();
    
    /**
     * 得到子表单
     * 
     * @param delegator
     * @param paramMap
     * @return
     * @throws Exception
     */
    public static List querySubPmFormList(GenericDelegator delegator,Map paramMap) throws Exception {
        String eqpid = (String)paramMap.get("eqpId");
        String periodIndex = (String)paramMap.get("periodIndex");
        String scheduleDate = (String)paramMap.get("scheduleDate");
        StringBuffer queryString=new StringBuffer();
        queryString.append("select t1.schedule_index scheduleIndex,t1.equipment_id equipmentId,t1.period_index periodIndex,to_char(t1.schedule_date,'yyyy-MM-dd') scheduleDate,t2.period_name periodName");
        queryString.append(" from equipment_schedule t1, default_period t2,equipment t3 ");
        StringBuffer whereSql=new StringBuffer(" where t1.period_index = t2.period_index and t1.equipment_id = t3.equipment_id and t1.event_index is null ");
        whereSql.append(" and t3.parent_eqpid = '").append(eqpid).append("'");
        whereSql.append(" and t1.period_Index = '").append(periodIndex).append("'");
        whereSql.append(" and t1.schedule_date = to_date('").append(scheduleDate).append("','yyyy-MM-dd')");
        queryString.append(whereSql.toString());
        List list = SQLProcess.excuteSQLQuery(queryString.toString(), delegator);
        return list;
    }
    
    /**
     * 得到用户信息accountSection
     * @param request
     * @param delegator
     * @return
     * @throws Exception
     */
    public static String getAccountDept(HttpServletRequest request,GenericDelegator delegator) throws Exception{
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String user = userLogin.getString("userLoginId");                
        GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));       
        
        return account.getString("accountDept");
    }
    
    /**
     * 通过设备ID获得处理程序列表
     * @param delegator
     * @param eqpId
     * @return
     * @throws Exception
     */
    public static List getDealProgrammeByEqpType(GenericDelegator delegator,String periodIndex)throws Exception{        
        List dealProgrammeList=delegator.findByAnd("FlowJob", UtilMisc.toMap("eventType",Constants.PM,"status", String.valueOf(Constants.ENABLE),"eventObject",periodIndex));
        return dealProgrammeList;
    }     
    
    /**
     * 根据EQPID取得对应的Period
     * @param delegator
     * @param eqpId
     * @return
     * @throws Exception
     */
    public static List queryPeriodByEqpid(GenericDelegator delegator,String eqpId) throws Exception{
        List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("equipmentId",eqpId));
        String equipmentType = ((GenericValue)equipmentList.get(0)).getString("equipmentType");
        List defaultPeriodList = delegator.findByAnd("DefaultPeriod", UtilMisc.toMap("eqpType", equipmentType));
        return defaultPeriodList;
    }
    
    /**
     * 得到PeriodName
     * @param delegator
     * @param periodIndex
     * @return
     * @throws Exception
     */
    public static GenericValue getPeriod(GenericDelegator delegator,String periodIndex) throws Exception{
        GenericValue defaultPeriod = delegator.findByPrimaryKey("DefaultPeriod",UtilMisc.toMap("periodIndex", periodIndex));
        return defaultPeriod;
    }
    
    /**
     * 得到EquipmentSchedule
     * @param delegator
     * @param eqpId
     * @param periodIndex
     * @param scheduleDate
     * @return
     * @throws Exception
     */
    public static List getEquipmentSchedule(GenericDelegator delegator,String eqpId, String periodIndex, String scheduleDate) throws Exception{
        StringBuffer queryString = new StringBuffer("");
        queryString.append("select schedule_Index scheduleIndex from Equipment_Schedule t ");
        queryString.append("where t.equipment_id ='").append(eqpId).append("'");
        queryString.append(" and t.period_Index = '").append(periodIndex).append("'");
        queryString.append(" and t.schedule_Date = to_date('").append(scheduleDate).append("','yyyy-MM-dd')");
        List equipmentScheduleList = SQLProcess.excuteSQLQuery(queryString.toString(), delegator);
        return equipmentScheduleList;
    }
    
    /**
     * 表PmForm的操作
     * 
     * @param delegator
     * @param pmFormMap
     * @throws Exception
     */
    public static void createPmForm(GenericDelegator delegator,Map pmFormMap) throws Exception{
        String eqpid = pmFormMap.get("equipmentId").toString();
        String periodIndex = pmFormMap.get("periodIndex").toString();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-hh24mmss");
        String startDate = simpleDateFormat.format(new java.util.Date().getTime());
        Debug.logInfo("start creat pmForm [" + eqpid + "/" + periodIndex + "  开始创建时间:"+startDate+"]", module);
        GenericValue gv = delegator.makeValidValue("PmForm", pmFormMap);
        delegator.create(gv);
        String endDate = simpleDateFormat.format(new java.util.Date().getTime());
        Debug.logInfo("end creat pmForm [" + eqpid + "/" + periodIndex + " 结束创建时间时间:"+startDate+"]", module);
    }
        
    /**
     * 表FormJobRelation的操作
     * 
     * @param delegator
     * @param jobRelationMap
     * @throws Exception
     */
    public static void createFormJobRelation(GenericDelegator delegator,Map jobRelationMap) throws Exception{
        GenericValue gv = delegator.makeValidValue("FormJobRelation", jobRelationMap);
        delegator.create(gv);
    }    
    
    /**
     * 手工建立设备保养中查询选择的保养下是否有未完成的计划安排
     * @param delegator
     * @param scheduleDate 日期
     * @param eqpId 设备
     * @param periodIndex 保养类型
     * @return
     * @throws GenericEntityException
     */
    public static List getUnPmScheduleList(GenericDelegator delegator, String scheduleDate, String eqpId, String periodIndex) throws GenericEntityException {
        EntityWhereString con = new EntityWhereString("schedule_Date = to_date('" + scheduleDate + "','yyyy-MM-dd') and equipment_Id ='" + eqpId + "' and  event_index is  null and period_Index = '" + periodIndex + "'");
        List equipmentScheduleList = delegator.findByCondition("EquipmentSchedule", con, null, null);
        return equipmentScheduleList;
    }
    
    /**
     * 建立保养表单，并得到表单编号
     * @param delegator
     * @param user 用户名
     * @param eqpid 设备
     * @param periodIndex 保养类型
     * @param formType normal/patch
     * @return
     * @throws Exception
     */
    public static Long createPmRecord(GenericDelegator delegator, String user, String eqpid, String periodIndex, String formType) throws Exception {
       //生成Map,放入参数
       Map pmFormMap = new HashMap();
       
       pmFormMap.put("equipmentId", eqpid);
       pmFormMap.put("pmName", CommonHelper.getFormName(Constants.PM_CHAR,eqpid,delegator));
       pmFormMap.put("periodIndex", periodIndex);
       pmFormMap.put("status", "-1");
       pmFormMap.put("createTime", new Timestamp(System.currentTimeMillis()));
       pmFormMap.put("createUser", user);
       pmFormMap.put("formType", formType);
       //是否更改设备状态
       String isChangeStatus=Constants.Y;
       if(formType.equalsIgnoreCase(Constants.FORM_TYPE_PATCH)){
           isChangeStatus=Constants.N;
       }else{
           GenericValue period = PmHelper.getPeriod(delegator, periodIndex);
           if(Constants.Y.equalsIgnoreCase(period.getString("isUpdatePromis"))){
               isChangeStatus=Constants.Y;
           }else{
               isChangeStatus=Constants.N;
           }
       }
       pmFormMap.put("isChangeStatus", isChangeStatus);
       
       //取得保养表单的编号
       Long id = delegator.getNextSeqId("pmFormSeqIndex");
       Debug.logInfo("get  pmFormSeqIndex success[" + eqpid + "/" + id + "]", module);
       pmFormMap.put("pmIndex", id);
       PmHelper.createPmForm(delegator,pmFormMap);
       Debug.logInfo("create pm success(createPmRecord) [" + eqpid + "/" + id + "]", module);
       //返回表单编号
       return id;
    }   
    
    /**
     * 更新表equipmentSchedule
     * @param delegator
     * @param scheduleIndex equimentSchedule的编号
     * @param id 保养表单编号
     * @throws GenericEntityException
     */
    public static void saveEquipmentSchedule(GenericDelegator delegator, String scheduleIndex, Long id) throws GenericEntityException {
        GenericValue equipmentSchedule = delegator.makeValidValue("EquipmentSchedule", UtilMisc.toMap("scheduleIndex", scheduleIndex));
           equipmentSchedule.put("eventIndex", id);
           delegator.store(equipmentSchedule);
           Debug.logInfo("update equipment Schedule success [/" + id + "/" + scheduleIndex + "]", module);
    }    
    
    /**
     * 建立FormJobRelation
     * 
     * @param delegator
     * @param user 用户
     * @param id 保养表单的编号
     * @param jobIndex 选择的job
     * @throws Exception
     */
    public static void createFormJobRelation(GenericDelegator delegator, String user, Long id, String jobIndex) throws Exception {
    	Map jobRelationMap = new HashMap();
    	jobRelationMap.put("jobIndex", jobIndex);
	    jobRelationMap.put("eventType", Constants.PM);
		jobRelationMap.put("eventIndex", id);		
		
		jobRelationMap.put("creator", user);
		jobRelationMap.put("jobStatus", Integer.valueOf("0"));
		jobRelationMap.put("nextActionId", Integer.valueOf("0"));
		
		GenericValue flowJob = delegator.findByPrimaryKey("FlowJob", UtilMisc.toMap("jobIndex",jobIndex));
		jobRelationMap.put("jobContent", flowJob.getString("jobContent"));
		jobRelationMap.put("jobName", flowJob.getString("jobName"));
		
		Long seqIndex = delegator.getNextSeqId("formJobRelationSeqIndex");
		jobRelationMap.put("seqIndex", seqIndex);

		PmHelper.createFormJobRelation(delegator, jobRelationMap);
    }    
    
    /**
     * NULL转为""
     * @param str
     * @return
     */
    public static String  EmptyToString(String str)
    {
        String result = "";
        if (str!=null)
        {
            result = str;
        }
        return result;
    }
    
    /**
     * 得到给定时间点未建立的子设备保养表单
     * @param delegator
     * @param paramMap
     * @param eqpid
     * @return
     * @throws SQLProcessException
     */
    /*public static List getSubEqpList(GenericDelegator delegator, Map paramMap, String eqpid, String periodIndex) throws SQLProcessException {
        StringBuffer queryString = new StringBuffer("");
           queryString.append("select t1.schedule_index scheduleIndex,t1.schedule_date scheduleDate, t1.equipment_id equipmentId from equipment_schedule t1, equipment t2 ");
           queryString.append("where t1.equipment_id = t2.equipment_id ");
           queryString.append("and t1.schedule_date = to_date('").append(paramMap.get("scheduleDate").toString()).append("','yyyy-MM-dd') ");
           queryString.append("and t2.parent_eqpid ='").append(eqpid).append("' ");
           queryString.append("and t1.period_index = '").append(periodIndex).append("'");
           queryString.append("and t1.event_index is null");
           List subEqpList = SQLProcess.excuteSQLQuery(queryString.toString(),delegator);
        return subEqpList;
    }*/
    
    public static List getSubEqpList(GenericDelegator delegator, String eqpid) {
    	List subEqpList = null;
    	try {
    		String sql = "select equipment_id from equipment where parent_eqpid = '" + eqpid +"' order by equipment_id";
            subEqpList = SQLProcess.excuteSQLQuery(sql,delegator);
    	} catch(Exception e) {
    		
    	}
    	return subEqpList;
    }
    
    public static List getEnterPmList(GenericDelegator delegator,String startDate,String endDate,String dept) throws Exception{
        StringBuffer queryString = new StringBuffer("");
        queryString.append("select t1.pm_index, t1.equipment_id, t3.period_name, t1.pm_name,t1.create_user,t1.create_time, decode(t1.form_type,'NORMAL','正常','PATCH','补填') form_type,t1.update_time,decode(t1.STATUS,'-1','建立未开始','0','开始','2','暂存','1','完成') status ");
        queryString.append(" from pm_form t1,equipment t2,default_period t3 ");
        queryString.append(" where t1.equipment_id = t2.equipment_id and t1.period_index = t3.period_index and t1.status <> '1' ");
        
        if (StringUtils.isNotEmpty(startDate)) {
            queryString.append(" and t1.create_time >= to_date('").append(startDate).append("','yyyy-MM-dd') ");
        }
        
        if (StringUtils.isNotEmpty(endDate)) {
            queryString.append(" and t1.create_time < to_date('").append(endDate).append("','yyyy-MM-dd') + 1 ");
        }
        
        queryString.append(" and t2.maint_dept='").append(dept).append("'");
        queryString.append(" and t3.period_name not like 'MSA%'");  // 非MSA部门人员不显示MSA保养项目
        queryString.append(" order by t1.create_time desc");
        
        List pmRecordList = SQLProcess.excuteSQLQuery(queryString.toString(), delegator);        
        return pmRecordList;
    }
    
    /**
     * 获得MSA设备的保养记录
     * @param delegator
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     * Create on 2011-6-13
     * Update on 2011-6-13
     */
    public static List getEnterMsaPmList(GenericDelegator delegator,String startDate,String endDate) throws Exception{
        StringBuffer queryString = new StringBuffer("");
        queryString.append("select t1.pm_index, t1.equipment_id, t3.period_name, t1.pm_name,t1.create_user,t1.create_time, decode(t1.form_type,'NORMAL','正常','PATCH','补填') form_type,t1.update_time,decode(t1.STATUS,'-1','建立未开始','0','开始','2','暂存','1','完成') status ");
        queryString.append(" from pm_form t1,equipment t2,default_period t3 ");
        queryString.append(" where t1.equipment_id = t2.equipment_id and t1.period_index = t3.period_index and t1.status <> '1' ");
        
        if (StringUtils.isNotEmpty(startDate)) {
            queryString.append(" and t1.create_time >= to_date('").append(startDate).append("','yyyy-MM-dd') ");
        }
        
        if (StringUtils.isNotEmpty(endDate)) {
            queryString.append(" and t1.create_time < to_date('").append(endDate).append("','yyyy-MM-dd') + 1 ");
        }
        
        queryString.append(" and t3.period_name like 'MSA%'");
        queryString.append(" order by t1.create_time desc");
        
        List pmRecordList = SQLProcess.excuteSQLQuery(queryString.toString(), delegator);        
        return pmRecordList;
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
    
    public static List getOverTimeList(GenericDelegator delegator,String reasonType,String equipmentType) throws Exception{
        List overTimeList = delegator.findByAnd("PmsReason", UtilMisc.toMap("reasonType", reasonType,"equipmentType",equipmentType));
        return overTimeList;
    }
    
    public static List getJobByPmIndex(GenericDelegator delegator,String eventType,String pmIndex) throws Exception{
    	StringBuffer sql=new StringBuffer();
        sql.append("select t1.seq_index,t1.event_index,t1.job_index,t1.job_name from form_job_relation t1 ");
        sql.append("where 1=1 ");
        sql.append("and t1.event_type = '").append(eventType).append("' ");
        sql.append("and t1.event_index = '").append(pmIndex).append("'");
        return SQLProcess.excuteSQLQuery(sql.toString(), delegator);   
    }
    
    /**
     * 结束保养表单
     * @param delegator
     * @param parMap
     * @throws Exception
     */
    public static void overPmForm(GenericDelegator delegator, Map parMap, String formType) throws Exception {
        if (Constants.FORM_TYPE_NORMAL.equals(formType)) {
            GenericValue pmForm = delegator.findByPrimaryKey("PmForm", UtilMisc.toMap("pmIndex", parMap.get("pmIndex")));
            if (pmForm.get("pmQcTime") == null) {
                parMap.put("pmQcTime", pmForm.get("startTime"));
            } else {
                parMap.put("pmQcTime", pmForm.get("pmQcTime"));
            }

            if (pmForm.get("pmReworkTime") == null) {
                List sqlRet = PlldbHelper.listEqpMaxChangeDate(delegator, (String) parMap.get("equipmentId"), "04-REW");
                if (sqlRet != null && !sqlRet.isEmpty()) {
                    if (((Map) sqlRet.get(0)).get("MAXCHANGEDT") != null) {
                        parMap.put("pmReworkTime", ((Map) sqlRet.get(0)).get("MAXCHANGEDT"));
                        parMap.put("isRework", "Y");
                    } else {
                        parMap.put("pmReworkTime", parMap.get("pmQcTime"));
                        parMap.put("isRework", "N");
                    }
                } else {
                    parMap.put("pmReworkTime", parMap.get("pmQcTime"));
                    parMap.put("isRework", "N");
                }
            } else {
                parMap.put("pmReworkTime", pmForm.get("pmReworkTime"));
            }
            
            if (pmForm.get("pmReworkQcTime") == null) {
                parMap.put("pmReworkQcTime", parMap.get("pmReworkTime"));
            }

            if (pmForm.get("qcResultConfirmTime") == null) {
                List sqlRet = PlldbHelper.listEqpMaxChangeDate(delegator, (String) parMap.get("equipmentId"), "04-CONF");
                if (sqlRet != null && !sqlRet.isEmpty()) {
                    if (((Map) sqlRet.get(0)).get("MAXCHANGEDT") != null) {
                        parMap.put("qcResultConfirmTime", ((Map) sqlRet.get(0)).get("MAXCHANGEDT"));
                    } else
                        parMap.put("qcResultConfirmTime", parMap.get("endTime"));
                } else {
                    parMap.put("qcResultConfirmTime", parMap.get("endTime"));
                }
            }
        }
        GenericValue gv = delegator.makeValidValue("PmForm", parMap);
        delegator.store(gv);
    }    
    
    /**
     * 根据条件查询保养表单
     * @param delegator
     * @param beginDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public static List queryPmFormByCondition(GenericDelegator delegator,Map parMap)throws Exception{
        String eqpId=(String)parMap.get("eqpId");
        String startDate=(String)parMap.get("startDate");
        String endDate=(String)parMap.get("endDate");
        String dept=(String)parMap.get("dept");
        
        StringBuffer sql=new StringBuffer();
        sql.append("select t.PM_INDEX,t.EQUIPMENT_ID,t.PM_NAME,t3.ACCOUNT_NAME, t4.period_name,");
        sql.append("t.START_TIME,decode(t.form_type,'NORMAL','正常','PATCH','补填') form_type,");
        sql.append("t.UPDATE_TIME,'完成' STATUS,t.JOB_TEXT,t.END_USER,t.END_TIME,ROUND((t.END_TIME-t.START_TIME)*24,2) man_hour");
        sql.append(" from PM_FORM t,equipment t2,account t3,default_period t4 where 1=1");
        sql.append(" and t2.equipment_id=t.equipment_id and t2.maint_dept='").append(dept).append("'");
        sql.append(" and t3.ACCOUNT_NO=t.CREATE_USER");
        sql.append(" and t.period_index = t4.period_index");
        
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
    
    /**
     * 根据条件查询保养表单
     * @param delegator
     * @param beginDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public static List newQueryDailyPmFormByCondition(GenericDelegator delegator,Map parMap)throws Exception{
        String eqpId=(String)parMap.get("eqpId");
        String startDate=(String)parMap.get("startDate");
        String endDate=(String)parMap.get("endDate");
        String dept=(String)parMap.get("dept");
        
        StringBuffer sql=new StringBuffer();
        sql.append("select t.PM_INDEX,t.EQUIPMENT_ID,t.PM_NAME,t3.ACCOUNT_NAME, t4.period_name,");
        sql.append("t.START_TIME,decode(t.form_type,'NORMAL','正常','PATCH','补填') form_type,");
        sql.append("t.UPDATE_TIME,'完成' STATUS,t.JOB_TEXT,t.END_USER,t.END_TIME,ROUND((t.END_TIME-t.START_TIME)*24,2) man_hour");
        sql.append(" from PM_FORM t,equipment t2,account t3,default_period t4 where 1=1");
        sql.append(" and t2.equipment_id=t.equipment_id and t2.maint_dept='").append(dept).append("'");
        sql.append(" and t3.ACCOUNT_NO=t.CREATE_USER");
        sql.append(" and t.period_index = t4.period_index");
        
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
        // 确定是日保养
        sql.append(" and t4.default_days = 1");
        sql.append(" order by t.CREATE_TIME desc");
        
        List list=SQLProcess.excuteSQLQuery(sql.toString(), delegator);
        return list;
    }
    
    /**
     * 根据条件查询保养表单
     * @param delegator
     * @param beginDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public static List newQueryPmFormByCondition(GenericDelegator delegator,Map parMap)throws Exception{
        String eqpId=(String)parMap.get("eqpId");
        String startDate=(String)parMap.get("startDate");
        String endDate=(String)parMap.get("endDate");
        String dept=(String)parMap.get("dept");
        String equipmentType = (String)parMap.get("equipmentType");
        String periodIndex=(String)parMap.get("periodIndex");
        
        StringBuffer sql=new StringBuffer();
        sql.append("select t.PM_INDEX,t.EQUIPMENT_ID,t.PM_NAME,t3.ACCOUNT_NAME, t4.period_name,");
        sql.append("t.START_TIME,decode(t.form_type,'NORMAL','正常','PATCH','补填') form_type,");
        sql.append("t.UPDATE_TIME,'完成' STATUS,t.JOB_TEXT,t.END_USER,t.END_TIME,ROUND((t.END_TIME-t.START_TIME)*24,2) man_hour");
        sql.append(" from PM_FORM t,equipment t2,account t3,default_period t4 where 1=1");
        if(StringUtils.isNotEmpty(equipmentType))
        {
        	sql.append(" and t2.equipment_id=t.equipment_id and t2.equipment_type='").append(equipmentType).append("'");
        }
        else
        {
        	sql.append(" and t2.equipment_id=t.equipment_id and t2.maint_dept='").append(dept).append("'");
        }
        sql.append(" and t3.ACCOUNT_NO=t.CREATE_USER");
        sql.append(" and t.period_index = t4.period_index");
        
        if(StringUtils.isNotEmpty(equipmentType) && StringUtils.isNotEmpty(periodIndex))
        {
            sql.append(" and t.period_index='").append(periodIndex).append("' ");
        }
        
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
        sql.append(" order by t.CREATE_TIME desc");
        
        List list=SQLProcess.excuteSQLQuery(sql.toString(), delegator);
        return list;
    }
    
    public static List queryUndoMsaPmformlist(GenericDelegator delegator,String eqpId)throws Exception{
        StringBuffer sql=new StringBuffer();
        sql.append("select t.PM_INDEX,t.EQUIPMENT_ID,t.PM_NAME,t3.ACCOUNT_NAME, t4.period_name,t.CREATE_TIME,decode(t.form_type,'NORMAL','正常','PATCH','补填') form_type,t.UPDATE_TIME, t.STATUS");
        sql.append(" from PM_FORM t,equipment t2,account t3,default_period t4 where 1=1");
        sql.append(" and t2.equipment_id=t.equipment_id");
        sql.append(" and t3.ACCOUNT_NO=t.CREATE_USER");
        sql.append(" and t.period_index = t4.period_index");
        if(StringUtils.isNotEmpty(eqpId)){
            sql.append(" and t.equipment_id='").append(eqpId).append("' ");
        }
        
        sql.append(" and t.STATUS !='").append(Constants.OVER).append("'");
        sql.append(" and t4.period_name like 'MSA%'");
        sql.append(" order by t.CREATE_TIME desc");
        
        List list=SQLProcess.excuteSQLQuery(sql.toString(), delegator);
        return list;
    }
    public static List queryUndoPmformlist(GenericDelegator delegator,String eqpId, String dept)throws Exception{
        StringBuffer sql=new StringBuffer();
        sql.append("select t.PM_INDEX,t.EQUIPMENT_ID,t.PM_NAME,t3.ACCOUNT_NAME, t4.period_name,t.CREATE_TIME,decode(t.form_type,'NORMAL','正常','PATCH','补填') form_type,t.UPDATE_TIME, t.STATUS");
        sql.append(" from PM_FORM t,equipment t2,account t3,default_period t4 where 1=1");
        sql.append(" and t2.equipment_id=t.equipment_id and t2.maint_dept='").append(dept).append("'");
        sql.append(" and t3.ACCOUNT_NO=t.CREATE_USER");
        sql.append(" and t.period_index = t4.period_index");
        if(StringUtils.isNotEmpty(eqpId)){
            sql.append(" and t.equipment_id='").append(eqpId).append("' ");
        }

        sql.append(" and t.STATUS !='").append(Constants.OVER).append("'");
        sql.append(" and t4.period_name not like 'MSA%'");
        sql.append(" order by t.CREATE_TIME desc");
        
        List list=SQLProcess.excuteSQLQuery(sql.toString(), delegator);
        return list;
    }
    
    public static void updateEquipmentSchedule(GenericDelegator delegator,String pmIndex) throws Exception {
        StringBuffer sql = new StringBuffer("");
        sql.append("update equipment_schedule t set t.event_index =''  where t.event_index = '").append(pmIndex).append("'");
        SQLProcess.excuteSQLUpdate(sql.toString(), delegator);
    }
    
    /**
     * 1、根据当前保养，生成下次保养应该执行的日期
     * 2、根据补填的原计划日期，生成下次保养应该执行的日期
     * @param delegator
     * @param pmForm
     * @param user
     * @param nowTime
     */
    public static void creatNextStartTime(GenericDelegator delegator, GenericValue pmForm, String user, Timestamp nowTime) {
    	try {
    		String eqpId = pmForm.getString("equipmentId");
    		Long periodIndex = pmForm.getLong("periodIndex");
    		
    		GenericValue defaultPeriod = delegator.findByPrimaryKey("DefaultPeriod", 
    				UtilMisc.toMap("periodIndex", periodIndex));
    		int defaultDays = defaultPeriod.getLong("defaultDays").intValue();
    		int warningDays = defaultPeriod.getLong("warningDays").intValue();
            
            //1、正常表单
            if (defaultDays > Long.parseLong(Constants.MAX_DAYPM_DEFAULT_DAYS)
            		&& pmForm.getString("formType").equals(Constants.FORM_TYPE_NORMAL)) {
            	Debug.logInfo("start creat nextStartTime [" + eqpId + "/" + periodIndex + "]", module);	            
	            
            	Calendar calendar = Calendar.getInstance();
	            calendar.setTimeInMillis(nowTime.getTime());	            
	            calendar.add(Calendar.DAY_OF_YEAR, (defaultDays + warningDays));
	            Timestamp nextPmTime = new Timestamp(calendar.getTimeInMillis());
	                	            
            	// 更新下次保养开始时间
	            Map nextStartTimeMap = new HashMap();
	            nextStartTimeMap.put("periodIndex", periodIndex);
	            nextStartTimeMap.put("equipmentId", eqpId);
	            nextStartTimeMap.put("lastPmDate", nowTime);
	            nextStartTimeMap.put("transBy", user);
	            nextStartTimeMap.put("nextPmDate", new java.sql.Date(nextPmTime.getTime()));

	            GenericValue nextTime = delegator.makeValidValue("PmNextStarttime", nextStartTimeMap);
	            try {
					delegator.createOrStore(nextTime);
					Debug.logInfo("creatNextStartTime success [" + eqpId + "/" + periodIndex + "/" + nextPmTime + "]", module);
				} catch (GenericEntityException e1) {
					Debug.logError("creatNextStartTime error [" + eqpId + "/" + periodIndex + "/" + nextPmTime + "]", module);
				}
            
	            // 更新小保养的下一次时间, 非日保养default_days > 2
	            // (更新上周日0点<=next_pm_date<=下周日0点 的下次保养时间，)已改为更新所有
                try {
					String sql = "select t1.period_index, trunc(sysdate + t2.default_days + t2.warning_days) next_date"
							+ " from pm_next_starttime t1,default_period t2"
							+ " where t1.period_index = t2.period_index"
							+ " and t2.default_days <= " + defaultDays
							+ " and t2.default_days > " + Constants.MAX_DAYPM_DEFAULT_DAYS
							+ " and t2.enabled = 1"
							//+ " and next_pm_date >= trunc(sysdate - (to_char(sysdate-1,'D')))"
							//+ " and next_pm_date <= trunc(sysdate + (7-to_char(sysdate-1,'D')))"
							+ " and t1.equipment_id = '" + eqpId + "'";

					List list = SQLProcess.excuteSQLQuery(sql, delegator);
					for (Iterator it = list.iterator(); it.hasNext();) {
						Map map = (Map) it.next();
						String includePeriodIndex = String.valueOf(map.get("PERIOD_INDEX"));
						String includeNextDateString = String.valueOf(map.get("NEXT_DATE"));
						java.sql.Date includeNextDate = MiscUtils.toGuiDate(includeNextDateString, "yyyy-MM-dd HH:mm:ss.S");

						Map includeMap = new HashMap();
						includeMap.put("periodIndex", includePeriodIndex);
						includeMap.put("equipmentId", eqpId);
						includeMap.put("lastPmDate", nowTime);
						includeMap.put("transBy", user);
						includeMap.put("nextPmDate", includeNextDate);
						
						GenericValue includeTime = delegator.makeValidValue("PmNextStarttime", includeMap);
						delegator.createOrStore(includeTime);
						Debug.logInfo(
								"creatNextStartTime update includePm nextStartTime success ["
										+ eqpId + "/" + includePeriodIndex
										+ "/" + includeNextDate + "]", module);
					}

				} catch (SQLProcessException e) {
					Debug.logError("creatNextStartTime update include pm error:" + e.getMessage(),module);
				} catch (GenericEntityException e) {
					Debug.logError("creatNextStartTime update include pm error:" + e.getMessage(),module);
				}
            }
            //2、补填表单
            else if (defaultDays > Long.parseLong(Constants.MAX_DAYPM_DEFAULT_DAYS) && pmForm.getString("formType").equals(Constants.FORM_TYPE_PATCH)) {
                Debug.logInfo("start creat patch form nextStartTime [" + eqpId + "/" + periodIndex + "]", module);

                try {
                    String pmIndex = pmForm.getString("pmIndex");
                    List eqpScheduleList = delegator.findByAnd("EquipmentSchedule", UtilMisc.toMap("eventIndex", pmIndex));
                    Date scheduleDate = ((GenericValue)eqpScheduleList.get(0)).getDate("scheduleDate");
                    String scheduleDateString = scheduleDate.toString();
                    
                    // 更新已存在的保养及小保养的下一次时间, 非日保养default_days > 2, 下一次时间需大于系统中next_pm_date才更新
                    String sql = "select t1.period_index,"
                    			+ " trunc(to_date('"+ scheduleDateString + "','yyyy-mm-dd') + t2.default_days + t2.warning_days) next_date,"
                    			+ " t1.next_pm_date" 
                                + " from pm_next_starttime t1,default_period t2" 
                                + " where t1.period_index = t2.period_index" + " and t2.default_days <= " + defaultDays + " and t2.default_days > " + Constants.MAX_DAYPM_DEFAULT_DAYS + " and t2.enabled = 1"
                                + " and t1.equipment_id = '" + eqpId 
                                + "' and trunc(to_date('" + scheduleDateString + "','yyyy-mm-dd') + t2.default_days + t2.warning_days) > t1.next_pm_date";

                    List list = SQLProcess.excuteSQLQuery(sql, delegator);
                    for (Iterator it = list.iterator(); it.hasNext();) {
                        Map map = (Map) it.next();
                        String includePeriodIndex = String.valueOf(map.get("PERIOD_INDEX"));
                        String includeNextDateString = String.valueOf(map.get("NEXT_DATE"));
                        java.sql.Date includeNextDate = MiscUtils.toGuiDate(includeNextDateString, "yyyy-MM-dd HH:mm:ss.S");

                        Map includeMap = new HashMap();
                        includeMap.put("periodIndex", includePeriodIndex);
                        includeMap.put("equipmentId", eqpId);
                        includeMap.put("lastPmDate", scheduleDate);
                        includeMap.put("transBy", user);
                        includeMap.put("nextPmDate", includeNextDate);

                        GenericValue includeTime = delegator.makeValidValue("PmNextStarttime", includeMap);
                        delegator.createOrStore(includeTime);
                        Debug.logInfo("creatNextStartTime update includePm patch form nextStartTime success [" + eqpId + "/" + includePeriodIndex + "/" + includeNextDate + "]", module);
                    }

                } catch (SQLProcessException e) {
                    Debug.logError("creatNextStartTime update include pm patch form error:" + e.getMessage(), module);
                } catch (GenericEntityException e) {
                    Debug.logError("creatNextStartTime update include pm patch form error:" + e.getMessage(), module);
                }
            }
    	} catch(Exception e) {
    		Debug.logError("creatNextStartTime error " + e.getMessage(), module);
    	}
    }
    
    /**
	 * @param eqpStatus
	 * @return
	 */
	public static boolean needChangePmEqpStatus(String eqpStatus) {
		return !TsHelper.isEqpStatus03(eqpStatus) && !TsHelper.isPRStartStatus(eqpStatus);
	}
	
	/**
     * 不定期PM换靶 结束表单check 子设备 开关是否开启
     * @param delegator
     * @param eqpId
     */
	public static List listMcsPmChangeTarget(GenericDelegator delegator,String eqpId) throws Exception{
    	StringBuffer sql = new StringBuffer();
        sql.append("select t2.* from equipment t1, Mcs_Pm_Change_Target t2");
        sql.append(" where t1.equipment_id=t2.using_object_id and t2.enabled=1");
        sql.append(" and (t1.equipment_id='").append(eqpId).append("' or t1.parent_eqpid='").append(eqpId).append("')");
        return SQLProcess.excuteSQLQuery(sql.toString(), delegator);   
    }

	/**
     * 判断是否不定期PM换靶、正常Normal表单
     * @param formType
     * @param periodName
     */
	public static boolean isPmChangeTarget(String formType, String periodName) {
		return periodName.indexOf(Constants.CHANGE_TARGET_PERIOD_PREFIX)>-1 && Constants.FORM_TYPE_NORMAL.equals(formType);
	}

	/**
     * 保存自动hold设置, add by dinghh 20151111
     * 20160412取消保存，改用保养周期设定hold
     * @param request
     * @param delegator
     * @return
     */
	public static void saveEqpMaintainHold(GenericDelegator delegator, Map parmMap) throws GenericEntityException {
//		String holdEnabled = (String) parmMap.get("holdEnabled");
//		
//		if ("ON".equals(holdEnabled)) {
//			String holdCodeReason = (String) parmMap.get("holdCodeReason");
//			String holdCode = holdCodeReason.substring(0, holdCodeReason.indexOf(":"));
//			String holdReason = holdCodeReason.substring(holdCodeReason.indexOf(":")+1);
//			parmMap.put("holdCode", holdCode);
//			parmMap.put("holdReason", holdReason);
//			parmMap.put("updateTime", UtilDateTime.nowTimestamp());
//		    
//		    GenericValue holdGv = delegator.makeValidValue("EqpMaintainHold", parmMap);
//		    delegator.createOrStore(holdGv);
//		}
	}
	
	public static void getMustChangeErrorRemark(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String eventIndex = request.getParameter("eventIndex");
		String eqpId = request.getParameter("eqpId");
		String size = request.getParameter("size");
		List mcrKeyUseIdList = new ArrayList();

		JSONObject jsObject = new JSONObject();
		JSONArray mustchangeCommreqArray = new JSONArray();
		JSONArray mustchangeCommedArray = new JSONArray();
		String sql = "select * from key_parts_mustchange_comm where event_index='" + eventIndex + "'";
		try {

			GenericValue eqpgv = (GenericValue) delegator.findByPrimaryKey("Equipment",
					UtilMisc.toMap("equipmentId", eqpId));
			String maintDept = eqpgv.getString("maintDept");
			List equDept = (List) delegator.findByAnd("EquipmentDept", UtilMisc.toMap("equipmentDept", maintDept));
			Map equMap = (Map) equDept.get(0);
			// new HashMap();
			String deptIndex = (equMap.get("deptIndex") + "").trim();
			List mcrList = SQLProcess.excuteSQLQuery(sql, delegator);
			// List mcrList=delegator.findByAnd("KeyPartsMustchangeComm",
			// UtilMisc.toMap("eventIndex",eventIndex));
			if (mcrList != null) {
				for (int i = 0; i < mcrList.size(); i++) {
					Map map = new HashMap();
					JSONObject object = new JSONObject();
					map = (Map) mcrList.get(i);
					object.put("keyPartsMustchangeCommId", map.get("KEY_PARTS_MUSTCHANGE_COMM_ID"));
					object.put("keyUseId", map.get("KEY_USE_ID"));
					object.put("keyPartsId", map.get("KEY_PARTS_ID"));
					object.put("partsId", map.get("PARTS_ID"));
					object.put("partsName", map.get("PARTS_NAME"));
					object.put("keydesc", map.get("KEYDESC"));
					object.put("eqpId", map.get("EQP_ID"));
					object.put("reason", map.get("REASON"));
					object.put("remark", map.get("REMARK"));
					object.put("updateTime", map.get("UPDATE_TIME"));
					object.put("updateUser", map.get("UPDATE_USER"));
					object.put("eventIndex", map.get("EVENT_INDEX"));
					mustchangeCommedArray.put(object);
					mcrKeyUseIdList.add(map.get("KEY_USE_ID"));
				}
			}
			List partsUseListkey = PartsHelper.getMcsPartsNoList_other(delegator, eqpId, eventIndex, deptIndex);
			List partsUseList_key = new ArrayList();
			for (int i = 0; i < partsUseListkey.size(); i++) {
				Map partsUseKeyMap = (Map) partsUseListkey.get(i);
				String keyUseIdUsed = (String) partsUseKeyMap.get("KEY_USE_ID_USED");
				String keyUseId = (String) partsUseKeyMap.get("KEY_USE_ID");
				String keyPartsId = (String) partsUseKeyMap.get("KEY_PARTS_ID");
				String limitType = (String) partsUseKeyMap.get("LIMIT_TYPE");
				String partsType = (String) partsUseKeyMap.get("PARTS_TYPE");
				String createTime = (String) partsUseKeyMap.get("CREATE_TIME");
				String createTimeNew = (String) partsUseKeyMap.get("CREATE_TIME_NEW");
				String warnSpec = (String) partsUseKeyMap.get("WARN_SPEC");
				String errorSpec = (String) partsUseKeyMap.get("ERROR_SPEC");
				String initLife = (String) partsUseKeyMap.get("INIT_LIFE");
				String initLifeUsed = (String) partsUseKeyMap.get("INIT_LIFE_USED");
				String partsTypeUsed = (String) partsUseKeyMap.get("PARTS_TYPE_USED");
				String actul = (String) partsUseKeyMap.get("ACTUL");
				String remainLife = "";
				String partsTypeWar = "";
				String createTimeWar = "";
				String warnRst = "N";
				String errorRst = "N";
				String isUsed = "N";
				String oldLife = "";

				if (keyUseId.equals("0") && !keyUseIdUsed.equals("0")) {
					isUsed = "Y";
					keyUseId = keyUseIdUsed;
				}
				if (initLife.equals("0") && !initLifeUsed.equals("0")) {
					oldLife = initLifeUsed;
				} else {
					oldLife = initLife;
				}
				if (warnSpec == null || errorSpec == null) {
					partsUseKeyMap.put("isUsed", isUsed);
					partsUseKeyMap.put("warnRst", warnRst);
					partsUseKeyMap.put("errorRst", errorRst);
					partsUseList_key.add(partsUseKeyMap);
					continue;
				}
				long delayLife = PartsHelper.getDelayLife(delegator, keyUseId);
				remainLife = (Integer.parseInt(errorSpec) - Integer.parseInt(actul) - Integer.parseInt(oldLife)
						+ delayLife) + "";
				if (createTime != null) {
					createTimeWar = createTime;
				} else if (createTimeNew != null) {
					createTimeWar = createTimeNew;
				} else {
					partsUseKeyMap.put("isUsed", isUsed);
					partsUseKeyMap.put("warnRst", warnRst);
					partsUseKeyMap.put("errorRst", errorRst);
					if (limitType != null && limitType.equals("TIME(天)")) {
						partsUseKeyMap.put("remainLife", remainLife.trim());
					}
					partsUseList_key.add(partsUseKeyMap);
					continue;
				}
				if (limitType == null) {
					limitType = "";
				}
				if (partsType == null) {
					if (partsTypeUsed != null) {
						partsTypeWar = partsTypeUsed;
					}
				} else {
					partsTypeWar = partsType;
				}
				long warn_days = Long.parseLong(warnSpec);
				long error_days = Long.parseLong(errorSpec);
				long actul_days = Long.parseLong(actul);
				if (limitType.equals("TIME(天)")) {
					warn_days = warn_days + delayLife;
					error_days = error_days + delayLife;
					if (actul_days > warn_days && actul_days <= error_days) {
						warnRst = "Y";
						errorRst = "N";
					} else if (actul_days > error_days) {
						warnRst = "Y";
						errorRst = "Y";
					}

					partsUseKeyMap.put("remainLife", remainLife.trim());
				}
				partsUseKeyMap.put("isUsed", isUsed);
				partsUseKeyMap.put("warnRst", warnRst);
				partsUseKeyMap.put("errorRst", errorRst);
				partsUseKeyMap.put("updateTime", createTimeWar);
				partsUseKeyMap.put("keyUseId", keyUseId);
				partsUseList_key.add(partsUseKeyMap);
			}

			for (int i = 0; i < partsUseList_key.size(); i++) {
				Map keyMap = (Map) partsUseList_key.get(i);
				String errorRst_key = (String) keyMap.get("errorRst");
				String mustchange_key = (String) keyMap.get("MUSTCHANGE");
				String keyUseId = (String) keyMap.get("keyUseId");
				String keyPartsId = (String) keyMap.get("KEY_PARTS_ID");
				String partsId = (String) keyMap.get("MTR_NUM");
				String partsName = (String) keyMap.get("MTR_DESC");
				String keydesc = (String) keyMap.get("KEYDESC");

				if (errorRst_key.equals("Y") && mustchange_key.equals("Y")) {
					if (mcrKeyUseIdList != null && mcrKeyUseIdList.size() > 0) {
						if (!mcrKeyUseIdList.contains(keyUseId)) {
							JSONObject object = new JSONObject();
							object.put("keyPartsMustchangeCommId", "");
							object.put("keyUseId", keyUseId);
							object.put("keyPartsId", keyPartsId);
							object.put("partsId", partsId);
							object.put("partsName", partsName);
							object.put("keydesc", keydesc);
							object.put("eqpId", eqpId);
							object.put("reason", "");
							object.put("reamrk", "");
							object.put("updateTime", "");
							object.put("updateUser", "");
							object.put("eventIndex", eventIndex);
							mustchangeCommreqArray.put(object);
						}
					} else {
						JSONObject object = new JSONObject();
						object.put("keyPartsMustchangeCommId", "");
						object.put("keyUseId", keyUseId);
						object.put("keyPartsId", keyPartsId);
						object.put("partsId", partsId);
						object.put("partsName", partsName);
						object.put("keydesc", keydesc);
						object.put("eqpId", eqpId);
						object.put("reason", "");
						object.put("reamrk", "");
						object.put("updateTime", "");
						object.put("updateUser", "");
						object.put("eventIndex", eventIndex);
						mustchangeCommreqArray.put(object);
					}
				}
			}

			jsObject.put("mustchangeCommedArray", mustchangeCommedArray);
			jsObject.put("mustchangeCommreqArray", mustchangeCommreqArray);
			response.getWriter().write(jsObject.toString());
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}
}
