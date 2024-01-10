package com.csmc.pms.webapp.form.event;

import java.io.IOException;
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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityWhereString;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.basic.event.BasicEvent;
import com.csmc.pms.webapp.basic.help.BasicHelper;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.common.helper.CommonHelper;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.eqp.helper.FabAdapter;
import com.csmc.pms.webapp.eqp.helper.GuiHelper;
import com.csmc.pms.webapp.eqp.helper.PlldbHelper;
import com.csmc.pms.webapp.form.help.PmHelper;
import com.csmc.pms.webapp.parts.event.PartsEvent;
import com.csmc.pms.webapp.parts.help.PartsHelper;
import com.csmc.pms.webapp.period.help.PeriodHelper;
import com.csmc.pms.webapp.qufollow.help.QuFollowHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.GeneralEvents;
import com.csmc.pms.webapp.util.TPServiceException;
import com.csmc.pms.webapp.workflow.model.Job;
import com.csmc.pms.webapp.workflow.model.engine.JobEngine;

public class PmFormEvent extends GeneralEvents{
	public static final String module = PmFormEvent.class.getName();
    
    
    //---------------------------填写机台参数-----------------------------

    public static String equipmentParamEnter(HttpServletRequest request, HttpServletResponse response) {
        //GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try{
/*            List equipmentSectionList = delegator.findAll("EquipmentSection");
            List equipmentList = delegator.findAll("Equipment");                                          
            request.setAttribute("equipmentSectionList", equipmentSectionList);
            request.setAttribute("equipmentList", equipmentList);    */        
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
    
    /**
     * 机台参数值一览
     * 
     * @param request
     *            equipmentId 机台信息
     * @param response
     * @return String success/error
     */
    public static String equipmentParamValueList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            String equipmentId = (String) request.getAttribute("equipmentId");
            // 页面上传递的参数
            String equipment_Id = (String) request.getParameter("equipment_Id");

            // 如果两个值都有，合并
            if (!StringUtils.isEmpty(equipment_Id)) {
                equipmentId = equipment_Id;
            }else{
            	equipmentId="";
            }
            
            String section = (String) request.getAttribute("section");

            // 页面上传递的参数
            String v_section = (String) request.getParameter("section");

            // 如果两个值都有，合并
            if (!StringUtils.isEmpty(v_section)) {
                section = v_section;
            }
            List equipmentList=new ArrayList();
            List unscheduleEqpParamList=new ArrayList();
            //排序
            List order=new ArrayList();
        	order.add("equipmentId");
        	//equipmentId不为空时查找特定设备
            if (!StringUtils.isEmpty(equipmentId)) {
            	 // 查看此设备是否存在
                equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("equipmentId", equipmentId));
                if (equipmentList.size() != 0) {
                    // 存在，取得此设备全部的机台参数信息 
                	StringBuffer queryString=new StringBuffer();
                	queryString.append("select  t.SEQ_INDEX,t.EQUIPMENT_ID,t.PARAM_NAME,t.EQP_STATUS,t.MAX_VALUE,t.MIN_VALUE,t.VALUE,t.MANUAL_VALUE,t.GUI_VALUE,t.STD_FLAG,t.GUI_CODE,t.TRANS_BY,t.UPDATE_TIME,t.SORT from unschedule_eqp_param t ");
                	queryString.append(" where t.equipment_id='").append(equipmentId).append("'");
                	queryString.append(" order by t.equipment_id,t.param_name");
                	unscheduleEqpParamList = SQLProcess.excuteSQLQuery(queryString.toString(), delegator);
                } else {
                    // 不存在，显示MESSAGE
                    request.setAttribute("_EVENT_MESSAGE_", "不存在此设备");
                }
            }else{
				// equipmentId为空时查找所有设备
            	StringBuffer queryString=new StringBuffer();
            	queryString.append("select  t.SEQ_INDEX,t.EQUIPMENT_ID,t.PARAM_NAME,t.EQP_STATUS,t.MAX_VALUE,t.MIN_VALUE,t.VALUE,t.MANUAL_VALUE,t.GUI_VALUE,t.STD_FLAG,t.GUI_CODE,t.TRANS_BY,t.UPDATE_TIME,t.SORT from unschedule_eqp_param t,equipment t2 ");
            	queryString.append("where t.equipment_id=t2.equipment_id and t2.section='").append(section).append("' order by t.equipment_id,t.param_name");
                unscheduleEqpParamList = SQLProcess.excuteSQLQuery(queryString.toString(), delegator);
            }
            request.setAttribute("unscheduleEqpParamList", unscheduleEqpParamList);
            request.setAttribute("equipmentId", equipmentId);
            request.setAttribute("section", section);                
            // 判断参数页面是否显示的flag
            request.setAttribute("flag", "OK");
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * 设备参数新建或保存
     * 
     * @param request
     *            seqIndex，equipmentId，eqpStatus,maxValue,minValue,stdFlag(机台参数资料)
     * @param response
     * @return String success/error
     */
    public static String manageEquipmentParamValue(HttpServletRequest request, HttpServletResponse response) {
        // 画面上传递的所有参数组合成Map
        Map paramMap = BasicEvent.getInitParams(request, true, true);
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // 取得用户
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");
            paramMap.put("value", paramMap.get("manualValue"));
            // 将信息写入设备参数表，写入历史表
            BasicHelper.createEqpParam(delegator, paramMap, user);

            // 显示机台信息一览的flag
            request.setAttribute("flag", "OK");
            request.setAttribute("equipmentId", paramMap.get("equipmentId"));
            request.setAttribute("section", request.getParameter("_section"));
            request.setAttribute("_EVENT_MESSAGE_", "保存成功！");

        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * 批量修改quipmentManualValue
     * @param request
     * @param response
     * @return
     */
    public static String equipmentManualValueBatchEdit(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
    	Map map=PmFormEvent.getInitParams(request);
    	Map paramMap=new HashMap();
    	 // 取得用户
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String user = userLogin.getString("userLoginId");
    	try {
    		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				if (key.startsWith("seqIndex_")) {
					// 获得序号
					String orderNum = key.substring(key.indexOf("_") + 1);// 获得index
					String seqIndex = (String) map.get("seqIndex_"+ orderNum);
					String equipmentId = (String) map.get("equipmentId_"+ orderNum);
					String manualValue = (String) map.get("manualValue_"+ orderNum);
					if(StringUtils.isEmpty(manualValue)){
						manualValue="0";
					}
					paramMap.put("seqIndex", seqIndex);
					paramMap.put("equipmentId", equipmentId);
					paramMap.put("manualValue", manualValue);
					paramMap.put("value", manualValue);
					
					//检查有没有修改manualValue值
					List unscheduleEqpParamList=delegator.findByAnd("UnscheduleEqpParam", paramMap);
					//manualValue值已被修改
					if(unscheduleEqpParamList.size()<=0){
						// table UnscheduleEqpParam中添加修改者，2008.12.10，黄海平，883609
						paramMap.put("transBy", user);
						GenericValue gv = delegator.makeValidValue("UnscheduleEqpParam", paramMap);
						delegator.store(gv);
						paramMap.remove("transBy");
						GenericValue histgv=delegator.findByPrimaryKey("UnscheduleEqpParam", UtilMisc.toMap("seqIndex", seqIndex));
						// 写入历史表
				        String event = "update";
				        BasicHelper.manageEquipmentParamHist(delegator, histgv, user, event);
					}
				}
    		}
    		// 显示机台信息一览的flag
            request.setAttribute("flag", "OK");
            request.setAttribute("equipmentId", paramMap.get("equipmentId"));
            request.setAttribute("section", request.getParameter("_section_batch"));
            request.setAttribute("_EVENT_MESSAGE_", "保存成功！");
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
    
    /**
     * 通过EQPID获取此设备的手动输入值
     * @param request
     * @param response
     * @return
     */
    public static String getEquipmenManualValueListByEeqpId(HttpServletRequest request, HttpServletResponse response) {
    	 // 画面上传递的所有参数组合成Map
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String equipmentId = (String) request.getAttribute("eqpId");
        try {
    		String sql=" equipment_id='"+equipmentId+"' and  value is not null and  value!=0";
    		EntityWhereString con = new EntityWhereString(sql);
        	List unscheduleList=delegator.findByCondition("UnscheduleEqpParam", con, null, null);
        	if (unscheduleList==null||unscheduleList.size()==0){
        		return "pmrecordinfo";
        	}
        	request.setAttribute("UNSCHEDULE_EQP_PARAM", unscheduleList);
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
    
    /**
     * PM开始时，更新value,manualValue为0
     * @param request
     * @param response
     * @return
     */
    public static String upateEquipmentParamManualValue(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Map paramMap=new HashMap();
        String[] seqIndexAry=request.getParameterValues("seqIndex");
        String sucessIndex="";
        int sucessCount=0;
        try {
            // 取得用户
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");
            if(seqIndexAry!=null){
            	for(int i=0;i<seqIndexAry.length;i++){
                	sucessCount=i;
                	paramMap.put("seqIndex", seqIndexAry[i]);
                    paramMap.put("value", "0");
                    paramMap.put("manualValue", "0");
                    paramMap.put("event", Constants.PM);
                    // 将信息写入设备参数表，写入历史表
                    BasicHelper.createEqpParam(delegator, paramMap, user);
                    sucessIndex=sucessIndex+seqIndexAry[i]+";";
                }
                request.setAttribute("_EVENT_MESSAGE_","手动输入值清空成功,表单已成功开始!");
            }
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            if(sucessCount!=0){
            	request.setAttribute("_ERROR_MESSAGE_","表单已成功开始!!部分手动输入值清空失败!更新成功的主键为:"+sucessIndex);
            }else{
            	request.setAttribute("_ERROR_MESSAGE_","手动输入值清空失败,表单已成功开始!!");
            }
            return "error";
        }
        return "success";
    }
    
    /**
     * 画面上的编辑按钮查询值
     * 
     * @param request
     *            seqIndex
     * @param response
     */
    public static void queryEquipmentParamValueByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // seqIndex取得
        String seqIndex = request.getParameter("seqIndex");

        try {
            // 取得机台参数信息
            GenericValue gv = delegator.findByPrimaryKey("UnscheduleEqpParam", UtilMisc.toMap("seqIndex", seqIndex));
            JSONObject unscheduleEquipemntParam = new JSONObject();
            unscheduleEquipemntParam.put("equipmentId", PmHelper.EmptyToString(gv.getString("equipmentId")));
            unscheduleEquipemntParam.put("paramName", PmHelper.EmptyToString(gv.getString("paramName")));
            unscheduleEquipemntParam.put("eqpStatus", PmHelper.EmptyToString(gv.getString("eqpStatus")));
            unscheduleEquipemntParam.put("maxValue", PmHelper.EmptyToString(gv.getString("maxValue")));
            unscheduleEquipemntParam.put("minValue", PmHelper.EmptyToString(gv.getString("minValue")));
            unscheduleEquipemntParam.put("stdFlag", PmHelper.EmptyToString(gv.getString("stdFlag")));
            unscheduleEquipemntParam.put("guiValue", PmHelper.EmptyToString(gv.getString("guiValue")));
            unscheduleEquipemntParam.put("manualValue", PmHelper.EmptyToString(gv.getString("manualValue")));
            unscheduleEquipemntParam.put("transBy", PmHelper.EmptyToString(gv.getString("transBy")));            
                        
            // 写入response
            response.getWriter().write(unscheduleEquipemntParam.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }    
    

    
    /**
     * 建立保养表单初始画面
     * @param request
     * @param response
     * @return
     */
    public static String createPmRecord(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);        
        
        try{
	        // 取得用户
	        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
	        String user = userLogin.getString("userLoginId");
	
	        GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));
	        String deptIndex = account.getString("deptIndex");
	        //如果登录人为质量保证部(dept_index == 10010)，显示(MSA==”Y”)的设备
	        if(deptIndex!=null && deptIndex.equals(Constants.DEPT_QC_INDEX)){
	            List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("msa", "Y"), UtilMisc.toList("equipmentId"));
	            request.setAttribute("equipmentList", equipmentList);
	            
	            // 查询部门所有补填表单
	            List list = PlldbHelper.queryPmFormList(delegator, UtilMisc.toMap("isMsa", "Y"), "0");
	            request.setAttribute("pmRecordList", list);
	        }
	        else{
	            List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("maintDept", account.getString("accountDept")), UtilMisc.toList("equipmentId"));
	            request.setAttribute("equipmentList", equipmentList);
	            
	            // 查询部门所有补填表单
	            List list = PlldbHelper.queryPmFormList(delegator, UtilMisc.toMap("dept", account.getString("accountDept")), "0");
	            request.setAttribute("pmRecordList", list);
	        }
	        request.setAttribute("_EVENT_MESSAGE_", request.getAttribute("_EVENT_MESSAGE_"));
	        
//	        // 查询部门所有补填表单
//            List list = PlldbHelper.queryPmFormList(delegator, UtilMisc.toMap("dept", account.getString("accountDept")), "0");
//            request.setAttribute("pmRecordList", list);
        } catch(Exception e) {
            Debug.logError(e.getMessage(), module); 
        }
        return "success";
    }
    
    
    /**
     * 查询未完成的保养计划
     * @param request
     * @param response
     * @return
     */
    public static String queryPmForm(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String eqpId=request.getParameter("eqpId");
        if(StringUtils.isEmpty(eqpId)){
            eqpId=(String)request.getAttribute("eqpId");
        }
        String startDate=request.getParameter("startDate");
        if(StringUtils.isEmpty(startDate)){
            startDate=(String)request.getAttribute("startDate");
        }
        String endDate=request.getParameter("endDate");
        if(StringUtils.isEmpty(endDate)){
            endDate=(String)request.getAttribute("endDate");                      
        }
                
        Map map=new HashMap();
        map.put("eqpId", eqpId);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        try {
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");

            GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));
            String deptIndex = account.getString("deptIndex");
            //如果登录人为质量保证部(dept_index == 10010)，显示(MSA==”Y”)的设备
            if(deptIndex!=null && deptIndex.equals(Constants.DEPT_QC_INDEX)){
                map.put("isMsa", "Y");
                List list=PlldbHelper.queryPmFormList(delegator, map,"0");
                request.setAttribute("pmRecordList", list);
                
                List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("msa", "Y"));
                request.setAttribute("equipmentList", equipmentList);
            } else {
                //登陆人的科别
                map.put("dept", account.getString("accountDept"));
                
                List list=PlldbHelper.queryPmFormList(delegator, map,"0");
                request.setAttribute("pmRecordList", list);
                request.setAttribute("maintDept", account.getString("accountDept"));
                
                List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("maintDept", account.getString("accountDept")));
                request.setAttribute("equipmentList", equipmentList);
            }
            //request.setAttribute("equipmentList", equipmentList);
        } catch (Exception e) {
        	Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
    
   /**
    * 非补填的保养表单添加画面
    * 
    * @param request
    * @param response
    * @return
    */
   public static String addPmForm(HttpServletRequest request, HttpServletResponse response){
       GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
       
       //取得界面的参数
       String scheduleDate = request.getParameter("scheduleDate");
       
       //queryEqpId是前一个画面传过来的，eqpId是本画面选择保养以后传递的
       String eqpId = request.getParameter("eqpId");
       if (!StringUtils.isEmpty(request.getParameter("queryEqpId"))){
           eqpId = request.getParameter("queryEqpId").toString();
       }
       String periodIndex = request.getParameter("periodIndex");
       
       try{
           //取得该设备全部保养，供选择用
           GenericValue equipment = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId", eqpId));
           String equipmentType = equipment.getString("equipmentType");           
           List periodList = delegator.findByAnd("DefaultPeriod", UtilMisc.toMap("eqpType",equipmentType));
           
           //scheduleDate,eqpId作为参数向后传递
           Map paramMap = new HashMap();
           paramMap.put("scheduleDate", scheduleDate);
           paramMap.put("eqpId", eqpId);
           paramMap.put("formType", Constants.FORM_TYPE_NORMAL);

           //如果存在periodIndex 此时必然是选择了保养类型了
           if (!StringUtils.isEmpty(periodIndex))
           {    
               //查看此保养当天是否有未建立的表单
               List equipmentScheduleList = PmHelper.getUnPmScheduleList(delegator, scheduleDate, eqpId, periodIndex);
               
               //如果没有了，传没有计划信息，让用户重选
               //如果存在，将scheduleIndex,periodIndex传入画面，并把对应的处理程序传入画面
               if (equipmentScheduleList.size()==0)
               {
                   GenericValue defaultPeriod = PmHelper.getPeriod(delegator, periodIndex);
                   String periodName = defaultPeriod.getString("periodName");
                   request.setAttribute("_EVENT_MESSAGE_", eqpId+" 保养类型"+periodName+" 当天没有计划，或正在填写，或已经做完");
               }
               else
               {
                   GenericValue equipmentSchedule = (GenericValue)equipmentScheduleList.get(0);
                   paramMap.put("periodIndex", periodIndex);
                   paramMap.put("scheduleIndex", equipmentSchedule.getString("scheduleIndex"));
                   //处理程序
                   List dealProgrammeList=delegator.findByAnd("FlowJob", UtilMisc.toMap("eventType",Constants.PM,"status", String.valueOf(Constants.ENABLE),"eventObject",periodIndex));
                   request.setAttribute("dealProgrammeList", dealProgrammeList);
               }
           }
           request.setAttribute("periodList", periodList);
           request.setAttribute("paramMap", paramMap);
       }catch(Exception e){
    	   Debug.logError(e.getMessage(), module);
           request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
           return "error";
       }
       return "success";
   }
  
   /**
    * 删除比当前PM小的保养设定，已经建立表单的不删除
    * @param request
    * @param response
    * @return
    */
   public static String submitSubSmallPmDelete(HttpServletRequest request, HttpServletResponse response){
       GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
       Map paramMap = PmFormEvent.getInitParams(request);       
       try{
           int num = Integer.parseInt(paramMap.get("eqpNum").toString());
           for(int i=1;i<=num;i++)
           {              
               String selectIndex = "";
               if(paramMap.get("select_"+i)!=null){
                   selectIndex = paramMap.get("select_"+i).toString();
               }

               if("1".equalsIgnoreCase(selectIndex))
               {
                   String scheduleIndex = paramMap.get("scheduleIndex_"+i).toString();
                   delegator.removeByAnd("EquipmentSchedule", UtilMisc.toMap("scheduleIndex", scheduleIndex));
               }
           }           
       }catch(Exception e){
    	   Debug.logError(e.getMessage(), module);
           request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
           return "error";  
       }
       return "success";
   }
   
   public static String submitPmDelete(HttpServletRequest request, HttpServletResponse response){
       Map paramMap = PmFormEvent.getInitParams(request);
       GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
       String result = "success";
       try{
           int num = Integer.parseInt(paramMap.get("eqpNum").toString());
           for(int i=1;i<=num;i++)
           {              
               String selectIndex = "";
               if(paramMap.get("select_"+i)!=null){
                   selectIndex = paramMap.get("select_"+i).toString();
               }

               if("1".equalsIgnoreCase(selectIndex))
               {
                   String scheduleIndex = paramMap.get("scheduleIndex_"+i).toString();
                   delegator.removeByAnd("EquipmentSchedule", UtilMisc.toMap("scheduleIndex", scheduleIndex));
               }
           }
      
           String eqpid = paramMap.get("eqpId").toString();
           String periodIndex = paramMap.get("periodIndex").toString();
   
           //得到未建立表单的子设备
//           List subEqpList = PmHelper.getSubEqpList(delegator, paramMap, eqpid,periodIndex);
           List subEqpList = PmHelper.getSubEqpList(delegator, eqpid);
           
           if (CommonUtil.isNotEmpty(subEqpList)){         
               HashMap map = new HashMap();
               map.put("scheduleDate", paramMap.get("scheduleDate"));
               map.put("periodIndex", paramMap.get("periodIndex"));
               map.put("formType",paramMap.get("formType"));
               
               //如果存在子设备，传处理程序和对应的参数
               List dealProgrammeList=delegator.findByAnd("FlowJob", UtilMisc.toMap("eventType",Constants.PM,"status", String.valueOf(Constants.ENABLE),"eventObject",periodIndex));                                 
               request.setAttribute("subEqpList", subEqpList);
               request.setAttribute("dealProgrammeList", dealProgrammeList);
               request.setAttribute("paramMap", map);
               result="subPmForm";
           }else{
               result = "success";
           }
               
          
       }catch(Exception e){
    	   Debug.logError(e.getMessage(), module);
           request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
           return "error";    
       }
       
       return result;
   }
   
   /**
    * 补填的保养表单添加画面
    * @param request
    * @param response
    * @return
    */
   public static String addPatchPmForm(HttpServletRequest request, HttpServletResponse response) {
       GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
       //取得页面上传递的参数
       String eqpId=request.getParameter("queryEqpId");
       String periodIndex = request.getParameter("periodIndex");
       String scheduleIndex = request.getParameter("scheduleIndex");        
       String scheduleDate = request.getParameter("scheduleDate");
                     
       //形成paramMap
       Map paramMap = new HashMap();
       paramMap.put("eqpId", eqpId);
       paramMap.put("periodIndex", periodIndex);
       paramMap.put("scheduleIndex", scheduleIndex);
       paramMap.put("scheduleDate", scheduleDate);
       paramMap.put("formType", Constants.FORM_TYPE_PATCH);
       
       try{
           GenericValue defaultPeriod = PmHelper.getPeriod(delegator, periodIndex);
           String periodName = defaultPeriod.getString("periodName");           
           paramMap.put("periodName", periodName);
           
           //得到处理程序
           List dealProgrammeList=delegator.findByAnd("FlowJob", UtilMisc.toMap("eventType",Constants.PM,"status", String.valueOf(Constants.ENABLE),"eventObject",periodIndex));
           request.setAttribute("dealProgrammeList", dealProgrammeList);
           request.setAttribute("paramMap", paramMap);           
       }catch(Exception e){
    	   Debug.logError(e.getMessage(), module);
           request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
           return "error";          
       }
       
       return "success";
   }
   
   /**
    * 保养表单的建立
    * 包括 equipment_schedule的更新，pm_form,form_job_relation的建立
    * @param request
    * @param response
    * @return
    */
   public static String submitPmForm(HttpServletRequest request, HttpServletResponse response){
       GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
       String result = "success";
       
       //得到页面全部参数
       Map paramMap = PmFormEvent.getInitParams(request);
       paramMap.put("jobNum", paramMap.get("jobNum").toString());
       
       //得到用户名
       GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
       String user = userLogin.getString("userLoginId");
       paramMap.put("user", user);
       
       //页面参数
       String eqpid = paramMap.get("eqpId").toString(); 
       String periodIndex = paramMap.get("periodIndex").toString();
       String scheduleDate = paramMap.get("scheduleDate").toString();
       String formType = paramMap.get("formType").toString();    
       //String scheduleIndex = paramMap.get("scheduleIndex").toString();        
       //int  jobNum = Integer.parseInt(paramMap.get("jobNum").toString());       
       
       LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);

       String user_message = "";
       
       try {
    	   Map ret = dispatcher.runSync("createPmForm",UtilMisc.toMap("paramMap" ,paramMap));
           if (ModelService.RESPOND_ERROR.equals(ret.get(ModelService.RESPONSE_MESSAGE))) {
               request.setAttribute("_ERROR_MESSAGE_",(String) ret.get(ModelService.ERROR_MESSAGE));
               return "error";
           }
           
           //得到未建立表单的子设备
           //List subEqpList = PmHelper.getSubEqpList(delegator, paramMap, eqpid);
 
           String sql = "select t1.schedule_index scheduleIndex,t1.equipment_id equipmentId,t2.period_name periodName"
        	   		+ " from equipment_schedule t1,default_period t2"
        	   		+ " where t1.event_index is null and  t1.period_index = t2.period_index"
        	   		+ " and t1.schedule_date = to_date('" + scheduleDate + "','yyyy-MM-dd')"
        	   		+ " and t1.equipment_id = '" + eqpid + "'"
					+ " and t2.default_days < "
					+ "(select default_days from default_period where period_index = '"	+ periodIndex + "')"
					+ " and t2.default_days > 1";
			List smallPeriodList = SQLProcess.excuteSQLQuery(sql, delegator);                                                               
            request.setAttribute("smallPeriodList", smallPeriodList);
			
			// result = "subPmForm";
			result = "pmdelete";
			if (smallPeriodList.size() != 0) {
				user_message = "没有需要删除的小保养";
			}   
           
           // 界面显示消息
			GenericValue defaultPeriod = PmHelper.getPeriod(delegator, periodIndex);
			String periodName = defaultPeriod.getString("periodName");
			String message = "";
			if (formType.equalsIgnoreCase("patch")) {
				message = eqpid + "设备" + periodName + "保养" + scheduleDate
						+ "补填表单建立完成";
			} else {
				message = eqpid + "设备" + periodName + "保养" + scheduleDate
						+ "当日表单建立完成";
			}
			
			HashMap map = new HashMap();
			map.put("scheduleDate", scheduleDate);
			map.put("periodIndex", periodIndex);
			map.put("formType", formType);
			map.put("eqpId", eqpid);
			request.setAttribute("paramMap", map);
			
			request.setAttribute("_USER_MESSAGE_", user_message);
			request.setAttribute("_EVENT_MESSAGE_", message);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error"; 
       }
       
       return result;
   }

   /**
    * 子保养表单的建立
    * @param request
    * @param response
    * @return
    */
   public static String subSumbitPmForm(HttpServletRequest request, HttpServletResponse response){
       GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
       LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
       try{
           //得到界面参数
           Map paramMap = PmFormEvent.getInitParams(request);
           int eqpNum = Integer.parseInt(paramMap.get("eqpNum").toString());
           String periodIndex = paramMap.get("periodIndex").toString();
           String scheduleDate = paramMap.get("scheduleDate").toString();
           String formType = paramMap.get("formType").toString();
           
           Debug.logInfo("periodIndex [" + periodIndex +"] scheduleDate[" + scheduleDate + "]", module);
           
           //得到用户
           GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
           String user = userLogin.getString("userLoginId");           
           String message = "";
           
           GenericValue defaultPeriod = PmHelper.getPeriod(delegator, periodIndex);
           String periodName = defaultPeriod.getString("periodName");
           
           String equipmentStr = "";
           for (int i=1;i<=eqpNum;i++)
           {
               String equipmentId = (String) paramMap.get("equipmentId_"+i);
               String selectStr = (String) paramMap.get("select_"+i);
               boolean flag = false;
//               String scheduleIndex = paramMap.get("scheduleIndex_"+i).toString();
               if ("1".equals(selectStr))
               {
                   //建立保养表单，并得到表单编号
//                   Long id = PmHelper.createPmRecord(delegator, user, equipmentId, periodIndex, formType);
//                   //更新equipmentSchedule
//                   PmHelper.saveEquipmentSchedule(delegator, scheduleIndex, id);
                   
             //-------------------------------------------
                   int jobNum = Integer.parseInt((String) paramMap.get("jobNum_"+i));
                   String jobIndex = null;
                   for (int j=1;j<=jobNum;j++)
                   {
                       String jobSelect = request.getParameter(equipmentId+"_jobSelect_"+j);
                       if ("1".equalsIgnoreCase(jobSelect))
                       {
                           jobIndex = (String) paramMap.get(equipmentId+"_jobIndex_"+j);
                           Debug.logInfo("equipmentId [" + equipmentId +"] jobIndex[" + jobIndex + "]", module);
//                           PmHelper.createFormJobRelation(delegator, user, id, jobRelationMap, jobIndex);
                       }
                   }
                   
                   if(jobIndex != null) {
                	   //调用Services生成子腔体表单数据
                	   Map serviceParamMap = new HashMap();
                	   serviceParamMap.put("user", user);
                	   serviceParamMap.put("equipmentId", equipmentId);
                	   serviceParamMap.put("periodIndex", periodIndex);
                	   serviceParamMap.put("formType", formType);
                	   serviceParamMap.put("jobIndex", jobIndex);
                	   serviceParamMap.put("scheduleDate", scheduleDate);
	                   	Map result = dispatcher.runSync("creatSubPmForm",serviceParamMap);
	                   	//throw exception
	               		if (!ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
	               			//throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
	               			flag = true;
	               		}
                   }
               }
                                            
               //界面消息的传递
               if(flag) {
	               if (formType.equalsIgnoreCase("patch")){
	                   message = message + equipmentId+"设备"+periodName+"保养"+ scheduleDate + "补填表单建立完成\n";   
	               }else{
	                   message = message + equipmentId+"设备"+periodName+"保养"+ scheduleDate + "当日表单建立完成\n";
	               }
	               
	               if ("".equals(equipmentStr)){
	                   equipmentStr = "'" + equipmentId + "'";
	               } else {
	                   equipmentStr = equipmentStr + ",'" + equipmentId + "'";
	               }         
               }
           }
           
           //PM子设备的小表单删除
           String sql = "select t1.schedule_index scheduleIndex,t1.equipment_id equipmentId,t2.period_name periodName from equipment_schedule t1,default_period t2 where t1.event_index is null and  t1.period_index = t2.period_index and t1.schedule_date = to_date('"+ scheduleDate +"','yyyy-MM-dd') and t1.equipment_id in ("+ equipmentStr +") and t2.default_days < (select default_days from default_period where period_index = '"+ periodIndex +"')";
           List smallPeriodList = SQLProcess.excuteSQLQuery(sql, delegator);
           request.setAttribute("smallPeriodList", smallPeriodList);
                                 
           request.setAttribute("_EVENT_MESSAGE_", message);                     
       }catch(Exception e){
    	   Debug.logError(e.getMessage(), module);
           request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
           return "error"; 
       }
       return "success";
   }
   
   /**
    * 进入填写保养记录
    * 
    * @param request
    * @param response
    * @return
    */
   public static String enterPmRecord(HttpServletRequest request, HttpServletResponse response){
       GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
       try {
           Calendar nowDate = Calendar.getInstance();
           nowDate.add(Calendar.DATE, -2);
           String startDate = "";
           String endDate = "";
           //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
           //startDate = sdf.format(nowDate.getTime());
           
           List pmRecordList;
           if (!StringUtils.isEmpty(request.getParameter("startDate"))){
               startDate = request.getParameter("startDate");               
           }
           
           if (!StringUtils.isEmpty(request.getParameter("endDate"))){
               endDate = request.getParameter("endDate");               
           }
           // 取得用户
           GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
           String user = userLogin.getString("userLoginId");

           GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));
           String deptIndex = account.getString("deptIndex");
           //如果登录人为质量保证部(dept_index == 10010)，显示(MSA==”Y”)的设备
           if (deptIndex!=null && deptIndex.equals(Constants.DEPT_QC_INDEX)) {
               pmRecordList = PmHelper.getEnterMsaPmList(delegator,startDate,endDate);
           } else {
               pmRecordList = PmHelper.getEnterPmList(delegator,startDate,endDate,account.getString("accountDept"));
           }
           request.setAttribute("pmRecordList",pmRecordList);
           request.setAttribute("startDate", startDate);  
           request.setAttribute("endDate", endDate);
       } catch(Exception e) {
    	   Debug.logError(e.getMessage(), module);
           request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
           return "error";
       }
       return "success";
   }
   
	/**
	 * PM信息全显示
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String pmRecordInfo(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String pmIndex = request.getParameter("pmIndex");
		String eqpId = request.getParameter("eqpId");
		String functionType = request.getParameter("functionType");
		String result = "";

		try {
			Map paramMap = new HashMap();
			paramMap.put("pmIndex", pmIndex);

			// 保养信息
			GenericValue pmForm = delegator.findByPrimaryKey("PmForm", UtilMisc.toMap("pmIndex", pmIndex));
			if (StringUtils.isEmpty(eqpId)) {
				eqpId = pmForm.getString("equipmentId");
			}
			paramMap.put("eqpId", eqpId);

			// 得到部门和课别
			GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
			String loginUser = userLogin.getString("userLoginId");
			request.setAttribute("userId", loginUser);
			String createUser = pmForm.getString("createUser");
			GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", createUser));

			// 课别
			String accountSection = account.getString("accountSection");
			paramMap.put("accountSection", accountSection);

			// 部门
			String accountDept = account.getString("accountDept");
			paramMap.put("accountDept", accountDept);

			// PM类型
			String periodIndex = pmForm.getString("periodIndex");
			GenericValue defaultPeriod = delegator.findByPrimaryKey("DefaultPeriod",
					UtilMisc.toMap("periodIndex", periodIndex));

			// 备件类型
			request.setAttribute("partTypeList", CommonHelper.getPartTypeList(delegator));

			result = "start";
			// 开始的表单
			if (!String.valueOf(Constants.CREAT).equals(pmForm.getString("status"))) {

				// 检索备件使用列表
				List partsUseList = PartsHelper.getPartsUseList(delegator, eqpId, pmIndex, Constants.PM, loginUser);
				request.setAttribute("PARTS_USE_LIST", partsUseList);

				// 检索关键备件使用信息
				String deptIndex = AccountHelper.getUserDeptIndex(delegator, loginUser);
				List keyPartsUseList = PartsHelper.getMcsPartsNoList_other(delegator, eqpId, pmIndex, deptIndex);// modified
				request.setAttribute("keyPartsUseList", keyPartsUseList);

				GenericValue equipment = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId", eqpId));
				List eqpStatusList = PmHelper.getEqpStatusList(delegator, Constants.PM);
				request.setAttribute("eqpStatusList", eqpStatusList);

				List overTimeList = PmHelper.getOverTimeList(delegator, Constants.OVERTIME,
						equipment.getString("equipmentType"));
				request.setAttribute("overTimeList", overTimeList);

				// 获得相关的处理程序
				request.setAttribute("jobList", PmHelper.getJobByPmIndex(delegator, Constants.PM, pmIndex));

				// 获得hold码，hold原因
				if (Constants.CALL_TP_FLAG) {
					List holdCodeReasonList = GuiHelper.getHoldCodeReasonList(accountDept);
					request.setAttribute("holdCodeReasonList", holdCodeReasonList);

					GenericValue eqpMaintainHold = delegator.findByPrimaryKey("EqpMaintainHold",
							UtilMisc.toMap("periodIndex", periodIndex, "eqpId", eqpId));
					request.setAttribute("eqpMaintainHold", eqpMaintainHold);
					;
				}

				// functionType=3:进入保养表单查看页面
				// functionType=1：进入保养表单管理页面
				if ("3".equals(functionType)) {
					result = "formview";
				} else if ("1".equals(functionType)) {
					result = "manager";
				}
			}
			String activate = request.getParameter("activate");
			if (StringUtils.isEmpty(activate)) {
				activate = (String) request.getAttribute("activate");
			}
			request.setAttribute("activate", activate);
			request.setAttribute("pmForm", pmForm);
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("defaultPeriod", defaultPeriod);

			List quFollowJobList = QuFollowHelper.queryQuFollowJobList(delegator,
					UtilMisc.toMap("eventType", "PM", "eventIndex", pmIndex));
			request.setAttribute("FOLLOWJOB_LIST", quFollowJobList);

		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return result;
	}
   
   /**
    * PM信息全显示
    * 
    * @param request
    * @param response
    * @return
    */
   public static String newPmRecordInfo(HttpServletRequest request, HttpServletResponse response){
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String index=request.getParameter("pmIndex");
        String eqpId=request.getParameter("eqpId");
        String functionType=request.getParameter("functionType");        
        String result="";
        try {
            Map paramMap = new HashMap();
            paramMap.put("pmIndex", index);
            paramMap.put("eqpId", eqpId);
            

            //保养信息
            GenericValue pmForm=delegator.findByPrimaryKey("PmForm", UtilMisc.toMap("pmIndex",index));
            
            //得到部门和课别
            //GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = pmForm.getString("createUser");                  
            GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));       
          
            //课别
            String accountSection = account.getString("accountSection");
            paramMap.put("accountSection", accountSection);
            
            //部门
            String accountDept = account.getString("accountDept");
            paramMap.put("accountDept", accountDept);
            
            //PM类型
            GenericValue defaultPeriod = delegator.findByPrimaryKey("DefaultPeriod", UtilMisc.toMap("periodIndex", pmForm.getString("periodIndex")));
            result="start";
            //开始的表单
            if(!String.valueOf(Constants.CREAT).equals(pmForm.getString("status"))){
                //获得相关的处理程序
                List jobList = new ArrayList();
                //jobList = delegator.findByAnd("FormJobRelation", UtilMisc.toMap("eventType",Constants.PM,"eventIndex", index));
                jobList = PmHelper.getJobByPmIndex(delegator, Constants.PM, index);
                List partsUseList=PartsHelper.getPartsList(delegator,index,Constants.PM);
                request.setAttribute("PARTS_USE_LIST", partsUseList);
                GenericValue equipment = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId",eqpId));
                List eqpStatusList=PmHelper.getEqpStatusList(delegator, Constants.PM);
                List overTimeList=PmHelper.getOverTimeList(delegator, Constants.OVERTIME, equipment.getString("equipmentType"));
                request.setAttribute("overTimeList", overTimeList);
                request.setAttribute("eqpStatusList", eqpStatusList);
                request.setAttribute("jobList", jobList);  
                // huanghp,883609,20081008,
                if (jobList != null)
                {
                	Job[] jobs = new Job[jobList.size()];
	                for(int i=0;i<jobList.size();i++) 
	                {
						Map jobMap = (Map)jobList.get(i);
						JobEngine jobEngine = JobEngine.create();
						jobEngine.setDelegator(delegator);
						jobEngine.setJobRelationIndex((String)jobMap.get("SEQ_INDEX"));
						Job job = jobEngine.getViewJobFromEntity();
						jobs[i] = job;
	                }
	                request.setAttribute("jobsFromJobList", jobs);  
                }
                else
                {
                	request.setAttribute("jobsFromJobList", new Job[0]); 
                }
                //functionType=3:进入保养表单查看页面
                //functionType=1：进入保养表单管理页面
                if("3".equals(functionType)){
                    result="formview";
                }else if("1".equals(functionType)){
                    result="manager";
                }
            }
            request.setAttribute("activate", request.getParameter("activate")==null?"":request.getParameter("activate"));
            request.setAttribute("pmForm", pmForm);
            request.setAttribute("paramMap", paramMap);
            request.setAttribute("defaultPeriod", defaultPeriod);            
        } catch (Exception e) {
        	Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return result;
   }
   
    /**
     * 开始保养表单
     * @param request
     * @param response
     * @return
     */
    public static String startPmForm(HttpServletRequest request,HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        HttpSession session=request.getSession();
        
        //更改设备状态所需要的信息
        String index = request.getParameter("pmIndex");
        String eqpId = request.getParameter("eqpId");
        String promisStatus = request.getParameter("promisStatus");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String user = userLogin.getString("userLoginId");
        Timestamp nowTime = new Timestamp(System.currentTimeMillis());
        String eventMessage = "";
        
        try {
            GenericValue pmForm = delegator.findByPrimaryKey("PmForm", UtilMisc.toMap("pmIndex", index));
            String periodIndex = pmForm.getString("periodIndex");
            GenericValue defaultPeriod = delegator.findByPrimaryKey("DefaultPeriod", UtilMisc.toMap("periodIndex", periodIndex));
            String periodName = defaultPeriod.getString("periodName");
            String isContainSmallPm = defaultPeriod.getString("isContainSmallPm");
            String defaultDays = defaultPeriod.getString("defaultDays");
            
            if (pmForm.getString("formType").equalsIgnoreCase(Constants.FORM_TYPE_NORMAL) && pmForm.getString("isChangeStatus").equalsIgnoreCase(Constants.Y)){                
                Map statusMap = new HashMap();
                statusMap.put("eqpid", eqpId);
                request.setAttribute("eqpId", eqpId);
                statusMap.put("newstatus", promisStatus);
                statusMap.put("comment", "PMS");
                
                if (PlldbHelper.isMesEqp(delegator, eqpId)) {
                	String eqpStatus = "";
                	try {
	                	Map retEqpStatus = FabAdapter.runCallService(delegator, userLogin,
	                            UtilMisc.toMap("eqpid", eqpId), Constants.EQP_INFO_QUERY);
	                    eqpStatus = (String) retEqpStatus.get("status");
	                    
	                    if (PmHelper.needChangePmEqpStatus(eqpStatus)) {
	                        // 更改设备状态，开始保养表单
	                        Map ret = new HashMap();
                            ret = FabAdapter.runCallService(delegator, userLogin,
                                statusMap, Constants.EQP_STATUS_CHANGE);
                            Debug.logInfo("change status success [" + eqpId + "/" + index + "/" + promisStatus + "]", module);	                        
	                        eventMessage = "表单成功开始，设备状态改为" + promisStatus;                        
	                    } else {
	                    	eventMessage = "当前设备状态为" + eqpStatus + "，系统不更改设备状态，保养开始。";
	                    }
                	} catch(TPServiceException e) {
                    	String eStr = e.toString();
                    	String errorMsg = "修改设备状态出错：当前设备状态" + eqpStatus + "，无法修改为" + promisStatus
										+ "。\r\n请先到MES中修改为01-PROD或02-M-N，重新开始保养。";
                    	
            			if (eqpId.startsWith("6") && eStr.indexOf("没有登陆或权力不足") > -1) {
            				errorMsg = "权限不足：请检查确认 PM系统 与 GUI 登录密码是否一致，需先修改为相同的密码。";
            			}
            			
                        request.setAttribute("_ERROR_MESSAGE_", errorMsg + "\r\n" + eStr);
                        Debug.logError("change status fail [" + eqpId + "/" + index + "/" + promisStatus + "] " + eStr, module);
                        return "error";
                    }
                }
                
                //向下包含维护项目(Y自动删除当天的周期性保养计划: 例如开始年维护时，则删除当天月、周和日维护计划)
                if (Constants.Y.equals(isContainSmallPm)) {
                	String scheduleDateString = new java.sql.Date(nowTime.getTime()).toString();                
                	String sql = "select t1.period_index, t2.period_name"
	    				+ " from equipment_schedule t1, default_period t2"
	    				+ " where t1.period_index = t2.period_index"
	    				+ "		and t1.equipment_id = '" + eqpId + "'"
	    				+ "		and t1.schedule_date = to_date('" + scheduleDateString + "', 'yyyy-mm-dd')"
	    				+ "		and t2.default_days < " + defaultDays
	    				+ "     and t2.eqp_status like '04-7%' and t1.event_index is null";
		    		
                	List list = SQLProcess.excuteSQLQuery(sql, delegator);
		    		for (Iterator it = list.iterator(); it.hasNext();) {
		    			Map map = (Map) it.next();
                        String smallPmPeriodIndex = String.valueOf(map.get("PERIOD_INDEX"));
                        String smallPmPeriodName = String.valueOf(map.get("PERIOD_NAME"));
                        eventMessage = eventMessage + "\n保养周期设定向下包含维护项目，已删除今天的小保养计划：" + smallPmPeriodName;
                        
		    			PeriodHelper.pmDayReset(delegator, eqpId, nowTime, smallPmPeriodIndex, user);
		    		}
                }
                
                request.setAttribute("_EVENT_MESSAGE_", eventMessage);
            }//end 正常表单、需修改设备状态
            
            Map map = new HashMap();
            map.put("pmIndex", index);
            map.put("status", String.valueOf(Constants.START));
            map.put("startUser", user);
            map.put("startTime", nowTime);
            GenericValue gv = delegator.makeValidValue("PmForm", map);
            delegator.store(gv);  
            Debug.logInfo("start pm success [" + eqpId + "/" + index + "]", module);          
                        
            //保养周期名称如果为“MSA”开头，则不更新pm_next_starttime表
            if (!periodName.startsWith(Constants.MSA)){
                //往next_starttime表里面写数据
                PmHelper.creatNextStartTime(delegator, pmForm, userLogin.getString("userLoginId"), nowTime);
            }           
            
        } catch (Exception e) {
        	Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }	 
    
    //---------------------parts设定-----------
    /**
     * 进入物料查询页面, fab2方式
     * @param request
     * @param response
     * @return
     */
    public static String intoPartsQuery(HttpServletRequest request,HttpServletResponse response) {
    	return PartsEvent.queryPartsNoPMList(request, response);
    	/*GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String eqpId = request.getParameter("eqpId");
        
        try {
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");
			List list = PartsHelper.getAccountSection(delegator, user);
			GenericValue gvAccount = (GenericValue) list.get(0);
			request.setAttribute("accountSection", gvAccount.getString("accountSection"));
			
			GenericValue gvEqp = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId", eqpId));
			request.setAttribute("equipmentType", gvEqp.getString("equipmentType"));
			request.setAttribute("maintDept", gvEqp.getString("maintDept"));
			
			GenericValue gvEqpDept = CommonUtil.findFirstRecordByAnd(delegator, "EquipmentDept", UtilMisc.toMap("equipmentDept", gvEqp.getString("maintDept")));
			request.setAttribute("deptIndex", gvEqpDept.getString("deptIndex"));
			
			request.setAttribute("queryType", Constants.EQUIPMENT_TYPE);
        } catch (Exception e) {
        	Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";*/
    }
    
    /**
     * PARTS查询
     * @param request
     * @param response
     * Parameter queryType: EQUIPMENT_TYPE, DEPT
     * @return
     */
    public static String queryPartsList(HttpServletRequest request,HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String eqpId = request.getParameter("eqpId");
		String periodIndex = request.getParameter("periodIndex");
		String partType = request.getParameter("partType");
		String pregion = request.getParameter("pregion");
		String partNo = request.getParameter("partNo").trim();
		
		String queryType = request.getParameter("queryType"); 
		String equipmentType = request.getParameter("equipmentType");
		String maintDept = request.getParameter("maintDept");
		String deptIndex = request.getParameter("deptIndex");
		
		Map parMap = new HashMap();
		parMap.put("eqpId", eqpId);
		parMap.put("periodIndex", periodIndex);
		parMap.put("partType", partType);
		parMap.put("pregion", pregion);
		parMap.put("partNo", partNo);
		
		parMap.put("queryType", queryType);
		parMap.put("equipmentType", equipmentType);
		parMap.put("maintDept", maintDept);
		parMap.put("deptIndex", deptIndex);
		
		try {
			GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
			String user = userLogin.getString("userLoginId");
			List list = PartsHelper.getAccountSection(delegator, user);
			GenericValue gv = (GenericValue) list.get(0);
			request.setAttribute("accountSection", gv.getString("accountSection"));

			List partsNoList = PartsHelper.queryPartsListInForm(delegator,parMap);
			request.setAttribute("partsNoList", partsNoList);
			
			request.setAttribute("queryType", queryType);
			request.setAttribute("equipmentType", equipmentType);
			request.setAttribute("maintDept", maintDept);
			request.setAttribute("deptIndex", deptIndex);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
    }   
    
    /**
	 * PARTS更新 
	 * @param request
	 * @param response
	 * @return
	 */
    public static String updatePartsUse(HttpServletRequest request,HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String userNo = CommonUtil.getUserNo(request);
        String seqIndex = request.getParameter("seqIndex");
        String partCount = request.getParameter("partCount");
		String partType = request.getParameter("partType");
        
        Map parMap = new HashMap();
        parMap.put("seqIndex", seqIndex);
        parMap.put("partCount", partCount);
        parMap.put("transBy", userNo);
        parMap.put("updateTime",new Timestamp(System.currentTimeMillis()));
        parMap.put("partType", partType);
        
        try {
            //保存或更新物料信息
            PartsHelper.updatePartsUse(delegator, parMap);

            //设备页面默认的打开的TABS
            request.setAttribute("activate", "parts");
            request.setAttribute("_EVENT_MESSAGE_", "保存成功！");
        } catch (Exception e) {
        	Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }   
    
    /**
     * 保养表单保存PARTS，mcs接口
     * @param request
     * @param response
     * @return
     */
    public static String savePartsUseInfo(HttpServletRequest request,HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		Map map = GeneralEvents.getInitParams(request);
		String userNo = CommonUtil.getUserNo(request);
		String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);
		map.put("transBy", userNo);
		map.put("deptIndex", deptIndex);
		map.put("eventType", Constants.PM);

		try {
			//TODO
			StringBuffer rSB = new StringBuffer();
			PartsHelper.savePartsUseInfo(delegator, map, rSB);
			if (rSB.length() > 0) {
                request.setAttribute("_ERROR_MESSAGE_", rSB.toString());
                request.setAttribute("flag", "N");
            } else {
                request.setAttribute("_EVENT_MESSAGE_", "保存成功！");
                request.setAttribute("flag", "Y");
            }
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
    
    /**
	 * 删除物料信息
	 * @param request
	 * @param response
	 * @return
	 */
	public static String delPartsUse(HttpServletRequest request,HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String seqIndex = request.getParameter("seqIndex");
		String keyuseid = request.getParameter("keyuseid");
		try {
			PartsHelper.deletePartsUse(delegator, seqIndex);
			
			if (StringUtils.isNotEmpty(keyuseid) && !keyuseid.equals("null")) {
				PartsHelper.deleteKeyPartsUse(delegator, keyuseid);
			}
			
			// 设备页面默认的打开的TABS
			request.setAttribute("activate", "parts");
			request.setAttribute("_EVENT_MESSAGE_", "删除成功！");
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		
		return "success";
	}
	//	---------------------end parts设定-----------
    
	/**
	 * 保养表单暂存
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String tempSavePmForm(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String pmIndex = request.getParameter("pmIndex");
		String formType = request.getParameter("formType");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("returnTime");
		String reasonIndex = request.getParameter("overTimeIndex");
		String jobText = request.getParameter("jobText");
		String comment = request.getParameter("comment");
		String eqpId = request.getParameter("eqpId");
		String userNo = CommonUtil.getUserNo(request);
		String eventIndex = request.getParameter("eventIndex");
		String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);
		// String relAbnormalReason=request.getParameter("relAbnormalReason");
		Map map = TsFormEvent.getInitParams(request, false, false);

		// 根据关键字，eqpid，判断是否提醒维护上一个的换下原因
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			if (key.startsWith("parts_")) {
				String orderNum = key.substring(key.indexOf("_") + 1);// 获得index
				String d_keydesc = (String) map.get("keydesc_" + orderNum);
				String d_offline = (String) map.get("offline_" + orderNum);
				String d_seriesno = (String) map.get("seriesno_" + orderNum);
				String d_newparttype = (String) map.get("newparts_type" + orderNum);
				if (!d_newparttype.equals("DELAY")) {
					if (d_offline == "null" || StringUtils.isEmpty(d_offline)) {
						// 执行查询语句 keydesc,eqpid,status
						StringBuffer sQueryPartsCount = new StringBuffer("");
						sQueryPartsCount.append(
								"  select t1.* from key_parts_use t1,  parts_use t2,key_eqp_parts t3  where t1.parts_use_id =t2.seq_index ");
						sQueryPartsCount.append("   and t1.key_parts_id=t3.key_parts_id   and t2.event_index <> '")
								.append(pmIndex).append("' ");
						sQueryPartsCount.append("    and t1.eqp_id = '").append(eqpId)
								.append("'  and t1.status='USING' ");
						sQueryPartsCount.append("    and keydesc='").append(d_keydesc).append("'");
						try {
							List partsUseList = SQLProcess.excuteSQLQuery(sQueryPartsCount.toString(), delegator);
							int size = partsUseList.size();
							if (size > 0) {
								// String delaytime=partsUseList.get();
								// map.put("delaytime",delaytime);
								request.setAttribute("_ERROR_MESSAGE_", d_keydesc + "之前已在此设备上使用过，请输入换下原因！");
								return "error";
							}
						} catch (Exception e) {
							request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
							return "error";
						}
					}
				}
			} else {
				continue;
			}
		}

		map.put("pmIndex", pmIndex);
		map.put("formType", formType);
		map.put("status", String.valueOf(Constants.HOLD));
		if (startTime.indexOf(":") < 0) {
			startTime = startTime + " 00:00:00";
		}
		map.put("startTime", Timestamp.valueOf(startTime));
		if (!StringUtils.isEmpty(endTime)) {
			map.put("endTime", Timestamp.valueOf(endTime));
		}
		map.put("jobText", jobText);
		map.put("updateTime", UtilDateTime.nowTimestamp());

		try {
			if (!StringUtils.isEmpty(reasonIndex)) {
				GenericValue pmsReason = delegator.findByPrimaryKey("PmsReason",
						UtilMisc.toMap("reasonIndex", reasonIndex));
				String reason = pmsReason.getString("reason");
				map.put("overtimeReasonIndex", reasonIndex);
				map.put("overtimeReason", reason);
			}
			map.put("overtimeComment", comment);

			GenericValue gv = delegator.makeValidValue("PmForm", map);
			delegator.store(gv);

			map.put("transBy", userNo);
			map.put("deptIndex", deptIndex);
			map.put("eventType", Constants.PM);
			map.put("status", "USING");
			map.put("eqpId", eqpId);
			StringBuffer rSB = new StringBuffer();
			PartsHelper.savePartsUseInfo(delegator, map, rSB);
			if (rSB.length() > 0) {
				request.setAttribute("_ERROR_MESSAGE_", rSB.toString());
				request.setAttribute("flag", "N");
			} else {
				request.setAttribute("_EVENT_MESSAGE_", "暂存成功！");
				request.setAttribute("flag", "Y");
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}

	/**
	 * 表单结束
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String overPmForm(HttpServletRequest request, HttpServletResponse response) {
		Debug.logInfo("overPmform start on " + new Timestamp(System.currentTimeMillis()), module);

		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String accountNo = CommonUtil.getUserNo(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		String returnStr = "";

		String eqpId = request.getParameter("eqpId");
		String pmIndex = request.getParameter("pmIndex");
		String formType = request.getParameter("formType");
		String startTime = request.getParameter("startTime");
		String returnTime = request.getParameter("returnTime");
		String reasonIndex = request.getParameter("overTimeIndex");
		String jobText = request.getParameter("jobText");
		String comment = request.getParameter("comment");
		String periodName = request.getParameter("periodName");

		try {
			// fab5不定期PM换靶：结束保养表单时，判断MCS开关是否开启
			if (PmHelper.isPmChangeTarget(formType, periodName)) {
				List list = PmHelper.listMcsPmChangeTarget(delegator, eqpId);
				if (list == null || list.size() == 0) {
					request.setAttribute("_ERROR_MESSAGE_", "不定期PM换靶：必须先在MCS系统中操作使用靶材 或 点击使用旧靶（需开权限）！");
					return "error";
				}
			}

			// pm form map
			Map map = new HashMap();
			map.put("pmIndex", pmIndex);
			map.put("formType", formType);
			if (startTime.indexOf(":") < 0) {
				startTime = startTime + " 00:00:00";
			}
			map.put("startTime", Timestamp.valueOf(startTime));
			map.put("returnTime", Timestamp.valueOf(returnTime));
			map.put("jobText", jobText);

			if (!StringUtils.isEmpty(reasonIndex)) {
				GenericValue pmsReason = delegator.findByPrimaryKey("PmsReason",
						UtilMisc.toMap("reasonIndex", reasonIndex));
				map.put("overtimeReasonIndex", reasonIndex);
				if (pmsReason != null) {
					String reason = pmsReason.getString("reason");
					map.put("overtimeReason", reason);
				}
			}

			List partsUseList = delegator.findByAnd("PartsUse",
					UtilMisc.toMap("eventIndex", pmIndex, "eventType", Constants.PM));

			StringBuffer errorString = new StringBuffer();
			List updateList = new ArrayList();

			for (int i = 0; i < partsUseList.size(); i++) {
				Map parts = (Map) partsUseList.get(i);
				String partNo = (String) parts.get("partNo");
				String mtrGrp = (String) parts.get("mtrGrp");
				String deptIndex = parts.get("deptIndex").toString();
				String partType = (String) parts.get("partType");// 新品，维修品
				String reqIndex = String.valueOf(parts.get("partsRequireIndex"));
				Timestamp nowTime = UtilDateTime.nowTimestamp();
				long usedCount = Long.valueOf((String) parts.get("partCount")).longValue();

				if (partType.equals("NEW") && !"null".equals(reqIndex)) {
					GenericValue gv = delegator.findByPrimaryKey("McsMaterialStoReq",
							UtilMisc.toMap("materialStoReqIndex", reqIndex));
					long reqQty = gv.getLong("qty").longValue();
					long activeQty = gv.getLong("activeQty").longValue();
					long stockQty = reqQty - activeQty;
					if (stockQty > 0 && stockQty >= usedCount) {
						gv.put("materialStoReqIndex", reqIndex);
						if (stockQty == usedCount) {gv.put("activeFlag", Constants.Y);}
						gv.put("activeTime", nowTime);
						gv.put("activeQty", Long.valueOf(activeQty + usedCount));
						gv.put("eventIndex", pmIndex);
						updateList.add(gv);
						
						// 更新物料状态
						// 在用物料状态更改为 FINISH
						List mtrStatusUpdateList = new ArrayList();
						List inUseMtrList = delegator.findByAnd("McsMaterialStatus", 
								UtilMisc.toMap("mtrNum", partNo, "mtrGrp", mtrGrp, "usingObjectId", eqpId, "status", "USING"));
						if (!inUseMtrList.isEmpty()) {
							GenericValue mtrStatusGv = (GenericValue) inUseMtrList.get(0);
							mtrStatusGv.put("status", ConstantsMcs.FINISH);
							mtrStatusGv.put("usingObjectId", null);
							mtrStatusGv.put("updateTime", nowTime);
							mtrStatusUpdateList.add(mtrStatusGv);
						}
						
						// 查询materialIndex
					    GenericValue materialInfo = CommonUtil.findFirstRecordByAnd(
								delegator, "McsMaterialInfo", 
								UtilMisc.toMap("mtrNum", partNo, "deptIndex", deptIndex));
						String materialIndex = materialInfo.getString("materialIndex");
						
						// 新增在用物料状态
						Long transactionId = delegator.getNextSeqId(ConstantsMcs.TRANSACTION_ID);
						Map mtrStatusMap = new HashMap(gv);
						Long mtrStatusId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_INDEX);
						mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_INDEX, mtrStatusId);
						mtrStatusMap.put("materialIndex", materialIndex);
						mtrStatusMap.put("aliasName", mtrStatusId.toString());//MCS get alias Name
						mtrStatusMap.put("status", ConstantsMcs.USING);
						mtrStatusMap.put("usingObjectId", eqpId);
						mtrStatusMap.put(ConstantsMcs.TRANSACTION_ID, transactionId);
						mtrStatusMap.put("transBy", accountNo);
						mtrStatusMap.put("updateTime", nowTime);		
						
					    GenericValue mtrStatusGv = delegator.makeValidValue("McsMaterialStatus", mtrStatusMap);
					    updateList.add(mtrStatusGv);
					    
					    // 3.按片记录历史
					    //3.1记录使用
					    Long mtrStatusHistId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX);
					    mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX, mtrStatusHistId);
					    
					    mtrStatusMap.put("status", ConstantsMcs.USING);
					    mtrStatusMap.put("usingObjectId", eqpId);				    
//					    mtrStatusMap.put("note", useNote);
					    GenericValue mtrStatusHistGv = delegator.makeValidValue("McsMaterialStatusHist", mtrStatusMap);
					    updateList.add(mtrStatusHistGv);
					    
						continue;
					}
					// 库存不足
					errorString.append(mtrGrp).append("组物料:").append(partNo).append(" 指定批次: ")
							.append(gv.get("batchNum").toString()).append(" 的库存不足! \n");
				}
			}

			if (errorString.length() > 0) {
				request.setAttribute("_ERROR_MESSAGE_", errorString.toString());
				return "error";
			}

			delegator.storeAll(updateList);

			map.put("overtimeComment", comment);
			map.put("status", String.valueOf(Constants.OVER));
			map.put("endUser", accountNo);
			map.put("endTime", Timestamp.valueOf(returnTime));
			map.put("updateTime", UtilDateTime.nowTimestamp());
			map.put("lockStatus", null);
			map.put("lockUser", null);

			// page param map
			Map parmMap = getInitParams(request, true, true);
			parmMap.put("userLogin", userLogin);

			Map result = dispatcher.runSync("overPmFormAction", UtilMisc.toMap("pmFormMap", map, "parmMap", parmMap));
			if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
				request.setAttribute("_ERROR_MESSAGE_", (String) result.get(ModelService.ERROR_MESSAGE));
				return "error";
			} else {
				returnStr = (String) result.get("returnMsg");
			}
			request.setAttribute("_EVENT_MESSAGE_", returnStr);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		Debug.logInfo("overPmform end on " + new Timestamp(System.currentTimeMillis()), module);
		return "success";
	}

	/**
     * 更改设备状态
     * @param request
     * @param response
     */
    public static void updateEqpState(HttpServletRequest request,HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        String eqpId = request.getParameter("eqpId");
        String eqpState = request.getParameter("eqpState");
        String pmIndex = request.getParameter("pmIndex");
        
        Map statusMap = new HashMap();
        statusMap.put("eqpid", eqpId);
        statusMap.put("newstatus", eqpState);
        statusMap.put("comment", "PMS");
        
        JSONObject result = CommonHelper.updateN05EqpStatus(delegator, userLogin, Constants.PM, pmIndex, statusMap);
                
        try{
			response.getWriter().write(result.toString());
		} catch (IOException io) {
			Debug.logInfo(io.toString(),module);
		}
    }
    
    /**
     * 使用旧靶，开启MCS开关
     * @param request
     * @param response
     */
    public static void updateMcsPmChangeTarget(HttpServletRequest request,HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String eqpId = request.getParameter("eqpId");  
        
        JSONObject result = new JSONObject();        
        try {	        
	        Map statusMap = new HashMap();
	        statusMap.put("usingObjectId", eqpId);
	        statusMap.put("transBy", CommonUtil.getUserNo(request));
	        statusMap.put("updateTime", UtilDateTime.nowTimestamp());
	        statusMap.put("enabled", ConstantsMcs.INTEGER_1);
	        statusMap.put("enabledType", "PMS");
	    	GenericValue changeTargetGv = delegator.makeValidValue("McsPmChangeTarget", statusMap);
	    	delegator.createOrStore(changeTargetGv);
	    	
	    	result.put("status", "success");
			result.put("message", "使用旧靶成功，可结束表单！");
		} catch (Exception e) {
			result.put("status", "error");
			result.put("message", "使用旧靶时出现系统异常,请重新操作或联系管理员!");
			Debug.logError(e.toString() + eqpId, module);
		}
		
		try {
			response.getWriter().write(result.toString());
		} catch (IOException io) {
			Debug.logInfo(io.toString(),module);
		}
    }
 
    /**
     * 删除PM类型
     * @param request
     * @param response
     */    
    public static String deletePmFormEntry(HttpServletRequest request,HttpServletResponse response) {
        String pmIndex = request.getParameter("pmIndex");
        String pmName = request.getParameter("pmName");
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try{
            //delegator.removeByAnd("PmForm", UtilMisc.toMap("pmIndex", pmIndex));
            //delegator.removeByAnd("FormJobRelation", UtilMisc.toMap("eventIndex", pmIndex));
            LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);            
            Map result = dispatcher.runSync("deletePmForm",UtilMisc.toMap("pmIndex" ,pmIndex));
            if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
                request.setAttribute("_ERROR_MESSAGE_",(String) result.get(ModelService.ERROR_MESSAGE));
                return "error";
            }     
            request.setAttribute("_EVENT_MESSAGE_", pmName+"已经被删除");
        }catch(Exception e){
        	Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
    
    /**
     * 进入查询保养表单页面,默认查询两天的记录
     * @param request
     * @param response
     * @return
     */
    public static String intoQueryPmForm(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Calendar nowDate = Calendar.getInstance();// 得到当前时间
        nowDate.add(Calendar.DATE, -2);
        SimpleDateFormat bartDateFormat =new SimpleDateFormat("yyyy-MM-dd");
        String beginDate=bartDateFormat.format(nowDate.getTime());
         try {
        	 	GenericValue userInfo = AccountHelper.getUserInfo(request,delegator);
                //登陆人的科别
                String section = userInfo.getString("accountSection");                
                String dept = userInfo.getString("accountDept");
                
                Map parm=new HashMap();
                parm.put("eqpId", "");
                parm.put("startDate", beginDate);
                parm.put("endDate", "");
                parm.put("dept", dept);
                
                List pmFormList=PmHelper.queryPmFormByCondition(delegator,parm);
                List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
                
                request.setAttribute("PMFORM_LIST", pmFormList);
                request.setAttribute("startDate", beginDate);
                request.setAttribute("section", section);
                request.setAttribute("maintDept", dept);
                request.setAttribute("deptList", deptList);
                
            } catch (Exception e) {
            	Debug.logError(e.getMessage(), module);
                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
            }
       return "success";
    }   

    /**
     * 根据条件查询保养表单
     * @param request
     * @param response
     * @return
     */
    public static String queryPmFormByCondition(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String startDate=request.getParameter("startDate");
        String endDate=request.getParameter("endDate");
        String eqpId=request.getParameter("eqpId");
        String dept = request.getParameter("maintDept");
         try {
                    
                Map parm=new HashMap();
                parm.put("eqpId", eqpId);
                parm.put("startDate", startDate);
                parm.put("endDate", endDate);
                parm.put("dept", dept);
                List pmList=PmHelper.queryPmFormByCondition(delegator,parm);
                List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
                
                List eqpTypeList = CommonHelper.getEquipmentTypeList(delegator);
                
                request.setAttribute("PMFORM_LIST", pmList);
                request.setAttribute("startDate", startDate);
                request.setAttribute("endDate", endDate);
                request.setAttribute("eqpId", eqpId);
                request.setAttribute("maintDept", dept);
                request.setAttribute("deptList", deptList);
                request.setAttribute("eqpTypeList", eqpTypeList);
                
            } catch (Exception e) {
            	Debug.logError(e.getMessage(), module);
                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
            }
       return "success";
    }    
    
    /**
     * 根据条件查询保养表单
     * @param request
     * @param response
     * @return
     */
    public static String newQueryPmFormByCondition(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String startDate=request.getParameter("startDate");
        String endDate=request.getParameter("endDate");
        String eqpId = "";
        String dept = request.getParameter("maintDept");
        String equipmentType = request.getParameter("equipmentType");
        String periodIndex = request.getParameter("periodIndex");
        if (dept != null && !dept.equals(""))
        {
        	eqpId=request.getParameter("eqpId");
        }
        else if (equipmentType != null && !equipmentType.equals(""))
        {
        	eqpId=request.getParameter("eqpId2");
        }
         try {
                    
                Map parm=new HashMap();
                parm.put("eqpId", eqpId);
                parm.put("startDate", startDate);
                parm.put("endDate", endDate);
                parm.put("dept", dept);
                parm.put("equipmentType", equipmentType);
                parm.put("periodIndex", periodIndex);
                List pmList=PmHelper.newQueryPmFormByCondition(delegator,parm);
                List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
                
                List eqpTypeList = CommonHelper.getEquipmentTypeList(delegator);
                
                request.setAttribute("PMFORM_LIST", pmList);
                request.setAttribute("startDate", startDate);
                request.setAttribute("endDate", endDate);
                request.setAttribute("eqpId", eqpId);
                request.setAttribute("maintDept", dept);
                request.setAttribute("deptList", deptList);
                request.setAttribute("eqpTypeList", eqpTypeList);
                
            } catch (Exception e) {
            	Debug.logError(e.getMessage(), module);
                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
            }
       return "success";
    }
    
    /**
     * 根据条件查询保养表单
     * @param request
     * @param response
     * @return
     */
    public static String newQueryDailyPmFormByCondition(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String startDate=request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        // 结束日期比开始日期+7
        if (startDate != null)
        {
	        String[] arrs = startDate.split("-");
	        Calendar cDay = Calendar.getInstance();
	        cDay.set(Integer.parseInt(arrs[0]), Integer.parseInt(arrs[1]) - 1, Integer.parseInt(arrs[2]));
	    	cDay.add(Calendar.DATE,+7);
	    	endDate = formatter.format(cDay.getTime());
        }
        String eqpId=request.getParameter("eqpId");
        String dept = request.getParameter("maintDept");
         try {
                    
                Map parm=new HashMap();
                parm.put("eqpId", eqpId);
                parm.put("startDate", startDate);
                parm.put("endDate", endDate);
                parm.put("dept", dept);
                List pmList=PmHelper.newQueryDailyPmFormByCondition(delegator,parm);
                List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
                
                List eqpTypeList = CommonHelper.getEquipmentTypeList(delegator);
                
                request.setAttribute("PMFORM_LIST", pmList);
                request.setAttribute("startDate", startDate);
                request.setAttribute("endDate", endDate);
                request.setAttribute("eqpId", eqpId);
                request.setAttribute("maintDept", dept);
                request.setAttribute("deptList", deptList);
                request.setAttribute("eqpTypeList", eqpTypeList);
                
                // 直接处理pmList
            	if(pmList != null && pmList.size() > 0) 
            	{
            		List dailyJobList = new ArrayList(pmList.size());
            		//String huanghp = "";
            		for(Iterator it = pmList.iterator();it.hasNext();) 
            		{
            			Map map = (Map)it.next();
            			
            			List jobList = new ArrayList();
                        //jobList = delegator.findByAnd("FormJobRelation", UtilMisc.toMap("eventType",Constants.PM,"eventIndex", index));
                        jobList = PmHelper.getJobByPmIndex(delegator, Constants.PM, (String)map.get("PM_INDEX"));
                        if (jobList != null)
                        {
                        	Job[] jobs = new Job[jobList.size()];
        	                for(int i=0;i<jobList.size();i++) 
        	                {
        						Map jobMap = (Map)jobList.get(i);
        						JobEngine jobEngine = JobEngine.create();
        						jobEngine.setDelegator(delegator);
        						jobEngine.setJobRelationIndex((String)jobMap.get("SEQ_INDEX"));
        						Job job = jobEngine.getViewJobFromEntity();
        						jobs[i] = job;
        	                }
        	                //request.setAttribute("jobsFromJobList", jobs);  
        	                dailyJobList.add(jobs);
                        }
                        else
                        {
                        	//request.setAttribute("jobsFromJobList", new Job[0]); 
                        	dailyJobList.add(new Job[0]);
                        }
            			
            			//huanghp += " " + map.get("PM_INDEX") + " " + map.get("EQUIPMENT_ID");
            			//request.setAttribute("huanghp", huanghp);
            			//request.setAttribute("pmIndex", map.get("PM_INDEX"));
            			//request.setAttribute("eqpId", map.get("EQUIPMENT_ID"));
            			//request.setAttribute("functionType", "3");
            	        //PmFormEvent.newPmRecordInfo(request, response);
            	        //dailyJobList.add(request.getAttribute("jobsFromJobList"));
            	  	}
            		request.setAttribute("dailyJobList", dailyJobList);
            	}
                
            } catch (Exception e) {
            	Debug.logError(e.getMessage(), module);
                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
            }
       return "success";
    }
    /**
     * 根据relationIndex查询JOB状态
     * 
     * @param request
     * @param response
     */
    public static void getJObStatus(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String jobStr = request.getParameter("jobStr");
        String[] job = jobStr.split(":");
        String jobStatus = "1";
        try {
            for (int i=0;i<job.length;i++){
                GenericValue gv = delegator.findByPrimaryKey("FormJobRelation", UtilMisc.toMap("seqIndex", job[i]));
                if(gv.getString("jobStatus").equalsIgnoreCase("0")||gv.getString("jobStatus").equalsIgnoreCase("2")){
                    jobStatus = "0";
                }
            }
            JSONObject pcStyle = new JSONObject();
            pcStyle.put("jobStatus", jobStatus);
            response.getWriter().write(pcStyle.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }   
    
    /**
     * 查询设备状态
     * @param request
     * @param response
     */
    public static void queryEqpState(HttpServletRequest request,HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        String eqpId = request.getParameter("eqpId");                
        
        JSONObject result = new JSONObject();
        try { 
            if (PlldbHelper.isMesEqp(delegator, eqpId)){            
                Map ret = FabAdapter.runCallService(delegator, userLogin,
                        UtilMisc.toMap("eqpid", eqpId), Constants.EQP_INFO_QUERY);
                String eqpStatus = (String)ret.get("status");
                
                if (ModelService.RESPOND_ERROR.equals(ret.get(ModelService.RESPONSE_MESSAGE))) {
                    result.put("status", "error");
                    result.put("message", (String) ret.get(ModelService.ERROR_MESSAGE));
                } else {
                    result.put("status", "success");
                    result.put("eqpStatus", eqpStatus);
                }
            } else {
            	// 非promis设备，仍然可以正常操作
                result.put("status", "success");                
                result.put("eqpStatus", "非MES设备");
            }
            
            response.getWriter().write(result.toString());            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "查询设备状态出错,请联系管理员:" + e.getMessage());
            try {
            	response.getWriter().write(result.toString());
            } catch (IOException io) {
            	Debug.logError(io.getMessage(), module);
            }
        }
    }
    
    /**
     * 制造作业PM后QC
     * @param request
     * @param response
     */
    public static void pmQcTime(HttpServletRequest request,HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        HttpSession session = request.getSession();
        
        //更改设备状态所需要的信息
        String index = request.getParameter("pmIndex");
        String eqpId = request.getParameter("eqpId");
        String promisStatus = request.getParameter("promisStatus");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");            
        
        JSONObject result = new JSONObject();
        try { 
            if (PlldbHelper.isMesEqp(delegator, eqpId)){ 
                //查询当前设备状态
                Map retEqpStatus = FabAdapter.runCallService(delegator, userLogin,
                        UtilMisc.toMap("eqpid", eqpId), Constants.EQP_INFO_QUERY);
                String eqpStatus = (String)retEqpStatus.get("status");
                
                // 更改设备状态
                Map statusMap = new HashMap();
                statusMap.put("eqpid", eqpId);
                request.setAttribute("eqpId", eqpId);
                statusMap.put("newstatus", promisStatus);
                statusMap.put("comment", "PMS");
                
                Map ret = new HashMap();
                try {
                	ret = FabAdapter.runCallService(delegator, userLogin, statusMap, Constants.EQP_STATUS_CHANGE);
                    Debug.logInfo("change status success [" + eqpId + "/" + index + "/" + promisStatus + "]", module);
                    result.put("status", "success");
                    result.put("message", "更改设备状态"+promisStatus+"成功！");
                } catch(TPServiceException e) {
                    String errMsg = "修改设备状态出错，当前设备状态" + eqpStatus 
                            + "，无法修改为" + promisStatus +  "\r\n"
                            + e.getMessage();
                    Debug.logError("change status fail [" + eqpId + "/" + index + "/" + promisStatus + "] " + (String) ret.get(ModelService.ERROR_MESSAGE), module);
                    result.put("status", "error");
                    result.put("message", errMsg);
                }    
            } else {
                // 非promis设备，仍然可以正常操作
                result.put("status", "error");                
                result.put("message", "非promis设备，不能更改设备状态");
            }
            response.getWriter().write(result.toString());
            
            Timestamp nowTime = new Timestamp(System.currentTimeMillis());
            Map pmformMap = new HashMap();
            pmformMap.put("pmIndex", index);                        
            if("04-POST".equalsIgnoreCase(promisStatus)){
                pmformMap.put("pmQcTime", nowTime);
            } else if("04-RWPS".equalsIgnoreCase(promisStatus)){
                pmformMap.put("pmReworkQcTime", nowTime);
            } else if("04-REW".equalsIgnoreCase(promisStatus)){
                pmformMap.put("pmReworkTime", nowTime);
            }
            GenericValue gv = delegator.makeValidValue("PmForm", pmformMap);
            delegator.store(gv);  
            Debug.logInfo("update pm success [" + eqpId + "/" + index + "]", module);
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "更改设备状态出错,请联系管理员:" + e.getMessage());
            try{
                response.getWriter().write(result.toString());
            } catch (IOException io) {
                Debug.logError(io.getMessage(), module);
            }
        }
    }
    
    /**
     * 查询未结束保养表单
     * @param request
     * @param response
     */
    public static String queryUndoPmformlist(HttpServletRequest request,
            HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String eqpId = request.getParameter("eqpId");
        String maintDept = request.getParameter("maintDept");
        try {
            List list;
            
            if (AccountHelper.isMsaDept(request, delegator)) {
                list = PmHelper.queryUndoMsaPmformlist(delegator, eqpId);
            } else {
                list = PmHelper.queryUndoPmformlist(delegator, eqpId, maintDept);
            }
            request.setAttribute("undoPmList", list);
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }

        return "success";
    }
    
    /**
     * 领导修改保养表单
     * @param request
     * @param response
     * @return
     */
    public static String editPmFormByLeader(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
    	String userNo = CommonUtil.getUserNo(request);
        String pmIndex=request.getParameter("pmIndex");
        String reasonIndex=request.getParameter("overTimeIndex");        
        String jobText=request.getParameter("jobText");
        String comment=request.getParameter("comment");

        try {
        	Map map=new HashMap();
            map.put("pmIndex", pmIndex);       
            map.put("jobText", jobText);        
            map.put("updateTime",new Timestamp(System.currentTimeMillis()));
            
            if (!StringUtils.isEmpty(reasonIndex)){            
                GenericValue pmsReason = delegator.findByPrimaryKey("PmsReason", UtilMisc.toMap("reasonIndex", reasonIndex));
                String reason = pmsReason.getString("reason");
                map.put("overtimeReasonIndex", reasonIndex);
                map.put("overtimeReason", reason);
            } else {
            	map.put("overtimeReasonIndex", reasonIndex);
                map.put("overtimeReason", "");
            }
            
            map.put("overtimeComment", comment);
            
            GenericValue gv=delegator.makeValidValue("PmForm", map);
            request.setAttribute("_EVENT_MESSAGE_", "修改成功！");
            delegator.store(gv);
            
            Debug.logInfo("modify PmForm success [" + pmIndex + "/" + userNo + "]", module);
        } catch (Exception e) {
        	Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

	private static List queryKeyPartsUseList(GenericDelegator delegator, List keyPartsUseList) throws Exception {
		List partsUseList_key = new ArrayList();
		for (int i = 0; i < keyPartsUseList.size(); i++) {
			Map partsUseKeyMap = (Map) keyPartsUseList.get(i);
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
			String mustchange = (String) partsUseKeyMap.get("MUSTCHANGE");
			if (mustchange == null) {
				mustchange = "N";
			}
			String keyPartsMustchangeConnId = (String) partsUseKeyMap.get("KEY_PARTS_MUSTCHANGE_COMM_ID");
			String remainLife = "";
			String partsTypeWar = "";
			String createTimeWar = "";
			String warnRst = "N";
			String errorRst = "N";
			String isUsed = "N";
			String oldLife = "";
			String mustchangeRemark = "1";
			String lifeType = "";

			if (limitType.equals("WAFERCOUNT")) {
				lifeType = "W";
			} else if (limitType.equals("TIME(天)")) {
				lifeType = "D";
			} else if (limitType.equals("RFTIME(Hours)")) {
				lifeType = "H";
			}

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
				partsUseKeyMap.put("mustchangeRemark", mustchangeRemark);
				partsUseKeyMap.put("lifeType", lifeType);
				partsUseList_key.add(partsUseKeyMap);
				continue;
			}
			long delayLife = PartsHelper.getDelayLife(delegator, keyUseId);
			remainLife = (Integer.parseInt(errorSpec) - Integer.parseInt(actul) + delayLife) + "";
			if (createTime != null) {
				createTimeWar = createTime;
			} else if (createTimeNew != null) {
				createTimeWar = createTimeNew;
			} else {
				partsUseKeyMap.put("isUsed", isUsed);
				partsUseKeyMap.put("warnRst", warnRst);
				partsUseKeyMap.put("errorRst", errorRst);
				partsUseKeyMap.put("mustchangeRemark", mustchangeRemark);
				partsUseKeyMap.put("lifeType", lifeType);
				if (limitType != null) {
					partsUseKeyMap.put("remainLife", errorSpec.trim());
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
			if (!limitType.equals("TIME(天)")) {
				// actul = PartsHelper.getActulFromFdc(delegator, eqpId, limitType,
				// createTimeWar, null);
				// if (actul.equals("fdcError") || actul.equals("relationError")) {
				// partsUseKeyMap.put("lifeError", actul);
				// actul = "0";// 对应eqpid，limittype没有fdc收值时，使用寿命置为零
				// } else {
				// actul = (Integer.parseInt(actul) + Integer.parseInt(initLife) +
				// Integer.parseInt(initLifeUsed) + "")
				// .trim();
				// }
				actul = "0"; // TODO moxiaoqi 后续追加FDC功能
				remainLife = (Integer.parseInt(errorSpec) - Integer.parseInt(actul) + delayLife) + "";
			}
			long warn_days = Long.parseLong(warnSpec);
			long error_days = Long.parseLong(errorSpec);
			long actul_days = Long.parseLong(actul);
			if (!limitType.equals("")) {
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
			if (mustchange.equals("Y") && errorRst.equals("Y") && keyPartsMustchangeConnId == null) {
				mustchangeRemark = "0";
			}
			partsUseKeyMap.put("mustchangeRemark", mustchangeRemark);
			partsUseKeyMap.put("lifeType", lifeType);
			partsUseKeyMap.put("isUsed", isUsed);
			partsUseKeyMap.put("warnRst", warnRst);
			partsUseKeyMap.put("errorRst", errorRst);
			partsUseList_key.add(partsUseKeyMap);
		}
		return partsUseList_key;
	}

    public static void changeEqpStatus(HttpServletRequest request,HttpServletResponse response) {
        String eqpId = request.getParameter("eqpId");
        String status = "04_MON_PM";
        JSONObject result = new JSONObject();
        String msg = "success!";
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
//        GenericValue userLogin = delegator.makeValue("UserLogin", UtilMisc.toMap("userLoginId", Constants.SUPER_USER, "currentPassword", Constants.PASSWORD, "enabled", "Y"));
        GenericValue userLogin = delegator.makeValue("UserLogin", UtilMisc.toMap("userLoginId", "IT_SUPER", "currentPassword", "CSMCSUPER", "enabled", "Y"));
        Map statusMap = new HashMap();
        statusMap.put("eqpid", eqpId);
        statusMap.put("newstatus", status);
        statusMap.put("comment", "Manual");
        try {
        	if (StringUtils.isNotEmpty(eqpId) && Constants.CALL_ASURA_FLAG && PlldbHelper.isMesEqp(delegator, eqpId)) {
            	Map ret = FabAdapter.runCallService(delegator, userLogin, statusMap, Constants.EQP_STATUS_CHANGE);
                if (ModelService.RESPOND_ERROR.equals(ret.get(ModelService.RESPONSE_MESSAGE))) {
                    Debug.logError((String) ret.get(ModelService.ERROR_MESSAGE), module);
                }
        	}
        	else {
        		msg = "eqpId is invalid!";
        	}
        } catch(Exception e){
        	msg = "manual change equipment status failed!";
        	Debug.log("manual change equipment status failed");
        	e.printStackTrace();
        }
        result.put("eqpId", eqpId);
		result.put("change status to", status);
		result.put("operation datetime", UtilDateTime.nowTimestamp());
		result.put("msg", msg);
		try {
			response.getWriter().write(result.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
