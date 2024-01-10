package com.csmc.pms.webapp.form.help;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.util.CommonUtil;


public class PcHelper {
	public static final String module = PcHelper.class.getName();
	
	/**
     * ���PcStyle
     * @param delegator
     * @return
     * @throws Exception
     */
    public static List queryPcStyle(GenericDelegator delegator) throws Exception{
    	return delegator.findAll("PcStyle",UtilMisc.toList("name"));
    }
    
    /**
     * ��ѯ����Ѳ��
     * @param delegator
     * @param styleIndex
     * @return
     * @throws Exception
     * @modify 1. 2009-08-03 �޸�SQL����Sysdate�ĳ�Ѳ��Ľ����ӽ���7��30�ֵ�����7��30��,���Ƴ�7.5Сʱ
     */
    public static List queryPcSchedule(GenericDelegator delegator, String styleIndex) throws Exception{
    	String sql = "select t1.schedule_index,to_char(t1.schedule_date,'yyyy-mm-dd') schedule_date,"
    				+ " t2.period_name,t2.period_desc,t2.period_index,"
    				+ " t3.pc_index,t3.pc_name,decode(t3.status,null,'',1,'���','δ���') status,"
    				+ " t4.job_index,t4.seq_index,t4.job_name"
					+ " from period_schedule t1,pc_period t2,pc_form t3,"
					+ "(select t5.event_index,t5.job_index,t5.seq_index,t5.job_name from form_job_relation t5 where t5.event_type='PC') t4"
					+ " where t1.period_index=t2.period_index "
					+ " and t1.pc_style_index=t3.style_index(+)"
					+ " and t1.period_index=t3.period_index(+)"
					+ " and t1.schedule_date=t3.schedule_date(+)"
					+ " and t3.pc_index=t4.event_index(+)"
					+ " and t1.pc_style_index=" + styleIndex
					+ " and t1.schedule_date = trunc(sysdate - 7.5/24)"		//�Ƴ�7.5Сʱ
					+ " order by t2.period_desc,t3.pc_name";

		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
    }        
    
    /**
     * ���ݲ��Ų�ѯEqpChar
     * 
     * @param delegator
     * @param dept
     * return pcIndex
     */
	public static String getEqpCharByDept(GenericDelegator delegator, String dept) {
		GenericValue equipmentDept = CommonUtil.findFirstRecordByAnd(delegator, "EquipmentDept", UtilMisc.toMap("equipmentDept", dept));
		
		return equipmentDept.getString("equipmentChar");
	}
	
	/**
     * �½�Ѳ��ʱ�½�PcForm��¼
     * 
     * @param delegator
     * @param param
     * return pcIndex
     */
    public static Long createPcForm(GenericDelegator delegator, Map paramMap) throws GenericEntityException {    
    	Long id = null;
        if (StringUtils.isEmpty((String) paramMap.get("pcIndex"))) {
            id = delegator.getNextSeqId("pcFormPcIndex");
            paramMap.put("pcIndex", id);
        }
        
        paramMap.put("startTime", new Timestamp(System.currentTimeMillis()));
        paramMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
        
        GenericValue gv = delegator.makeValidValue("PcForm", paramMap);
        delegator.create(gv);
        
        return id;
    }
	
	/**
     * �½�Ѳ��ʱ�½�FormJobRelation��¼
     * 
     * @param delegator
     * @param param
     * ��Ҫ�������Ϣ
     */
    public static void createFormJobRelation(GenericDelegator delegator, Map paramMap) throws GenericEntityException {    
    	Long id = null;
        if (StringUtils.isEmpty((String) paramMap.get("seqIndex"))) {
            id = delegator.getNextSeqId("formJobRelationSeqIndex");
            paramMap.put("seqIndex", id);
        }        
        
        GenericValue gv = delegator.makeValidValue("FormJobRelation", paramMap);
        delegator.create(gv);
    }
    
    /**
     * �½�Ѳ��ʱ����Ѳ��ƻ����е�eventIndex
     * 
     * @param delegator
     * @param param
     * 
     */
    public static void updatePeriodSchedule(GenericDelegator delegator, Map map) throws GenericEntityException {    	
        GenericValue gv = delegator.makeValidValue("PeriodSchedule", map);
        delegator.store(gv);
    }
    
    /**
	 * ����Service����Ѳ��
	 * service action(insert)
	 * @param delegator
	 * @param dispatcher
	 * @param map
	 * @param String
	 * @throws GenericServiceException
	 */
	public static void setupPcForm(GenericDelegator delegator,
			LocalDispatcher dispatcher, Map paramMap, String userNo)
			throws GenericServiceException {
		//	Service setupPcForm
    	Map result = dispatcher.runSync("setupPcForm",UtilMisc.toMap("param" ,paramMap, "userNo", userNo));
    	//throw exception
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
	}
	
	/**
     * ���Ѳ��,����״̬status(1),endTime,endUser,updateTime
     * 
     * @param delegator
     * @param param
     * 
     */
    public static void finishPcForm(GenericDelegator delegator, Map map) throws GenericEntityException {    	
        GenericValue gv = delegator.makeValidValue("PcForm", map);
        delegator.store(gv);
    }
    
    /**
     * ��ѯ�����Ѳ���
     * @param delegator
     * @param filterMap
     * @return
     * @throws Exception
     */
    public static List queryPcForm(GenericDelegator delegator, Map filterMap) throws Exception {
		String startDate = (String) filterMap.get("startDate");
		String endDate = (String) filterMap.get("endDate");	
		String styleIndex = (String) filterMap.get("styleIndex");
		
		String sql = "select t2.period_name,t2.period_desc,"
				+ "t1.start_time,t1.end_time,t1.pc_name,t1.pc_index,"
				+ "decode(t1.status,1,'���','δ���') status, t3.seq_index"
				+ " from pc_form t1,pc_period t2,form_job_relation t3"
				+ " where t1.period_index=t2.period_index"
				+ " and t1.pc_index=t3.event_index and t3.event_type='PC'"
				+ " and t1.start_time>=to_date('" + startDate + "','yyyy-mm-dd')"
				+ " and t1.start_time<to_date('" + endDate + "','yyyy-mm-dd')+1";		
		
		if (StringUtils.isNotEmpty(styleIndex)) {
			sql = sql + " and t1.style_index=" + styleIndex;
		}
		
		sql = sql + " order by t2.period_name,t1.start_time";
		
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}
}
