package com.csmc.pms.webapp.form.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityWhereString;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.eqp.helper.FabAdapter;
import com.csmc.pms.webapp.eqp.helper.GuiHelper;
import com.csmc.pms.webapp.eqp.helper.PlldbHelper;
import com.csmc.pms.webapp.form.help.PmHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;


public class PmFormService {
    public static final String module = PmFormService.class.getName();
        
    /**
     * 表单结束
     * @param ctx
     * @param context
     * @return
     */
    public static Map overPmForm(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
		Map pmFormMap = (Map) context.get("pmFormMap");
		Map parmMap = (Map) context.get("parmMap");
		String eqpId = (String) parmMap.get("eqpId");
		String formType = (String) parmMap.get("formType");
		GenericValue userLogin = (GenericValue) parmMap.get("userLogin");
		String periodName = (String) parmMap.get("periodName");
		
		Map result = new HashMap();
		String returnStr = "表单已成功结束，不改变MES设备状态";
        
        try {
        	String pmIndex = (String) pmFormMap.get("pmIndex");
        	GenericValue pmForm = delegator.findByPrimaryKey("PmForm", UtilMisc.toMap("pmIndex", pmIndex));
        	if (String.valueOf(Constants.OVER).equals(pmForm.getString("status")) && StringUtils.isEmpty(pmForm.getString("lockStatus"))) {
        		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "表单已结束，请勿重复提交！");
                return result;
        	}
            
            if (Constants.FORM_TYPE_NORMAL.equals(formType)){//普通表单
            	//是否需要修改设备状态
            	if (pmForm.getString("isChangeStatus").equalsIgnoreCase(Constants.Y)) {
            		// 查询保养表单中，是否存在此设备未结束的保养表单
            		String sql=" EQUIPMENT_ID='"+eqpId+"' and STATUS!='"+Constants.OVER+"' and FORM_TYPE='"+Constants.PM+"' and IS_CHANGE_STATUS = 'Y' ";
	                EntityWhereString con = new EntityWhereString(sql);
	                List pmFormList=delegator.findByCondition("PmForm", con, null, null);
	                
	                // 查询异常表单中，是否存在此设备未结束的异常表单
	                sql=" EQUIPMENT_ID='"+eqpId+"' and STATUS!='"+Constants.OVER+"' and FORM_TYPE='"+Constants.FORM_TYPE_NORMAL+"'";
	                con = new EntityWhereString(sql);	                
	                List abnormalFormList=delegator.findByCondition("AbnormalForm", con, null, null);
	                if (CommonUtil.isEmpty(pmFormList) && CommonUtil.isEmpty(abnormalFormList)) {
	                    Map statusMap = new HashMap();
	                    statusMap.put("eqpid", eqpId);
//	                    statusMap.put("newstatus", Constants.PM_END_STATUS);
	                    statusMap.put("newstatus","04_MON_PM");
	                    statusMap.put("comment", "PMS");
						String newStatus = (String)statusMap.get("newstatus");
	                    if (PlldbHelper.isMesEqp(delegator, eqpId)) {
	                    	try {
	                            Map retEqpStatus = FabAdapter.runCallService(delegator, userLogin,
	                                    UtilMisc.toMap("eqpid", eqpId), com.csmc.pms.webapp.util.Constants.EQP_INFO_QUERY);
	                            String eqpStatus = (String)retEqpStatus.get("status");
	                            
	                            if (PmHelper.needChangePmEqpStatus(eqpStatus)) {
	    	                        // 更改设备状态，结束表单    	                        
    	                        	Map ret = FabAdapter.runCallService(delegator, userLogin, statusMap, com.csmc.pms.webapp.util.Constants.EQP_STATUS_CHANGE);
    	                        	if (ModelService.RESPOND_ERROR.equals(ret.get(ModelService.RESPONSE_MESSAGE))) {
    	        						Debug.logError((String) ret.get(ModelService.ERROR_MESSAGE), module);
    	        					} else {
	    	                        	//added by steven 设备状态更改成功，触发GUI QC
//	    	                        	GuiHelper.triggerQc(eqpId, eqpStatus, Constants.PM_END_STATUS, userLogin);
	    	                        	GuiHelper.triggerQc(eqpId, eqpStatus, "04_MON_PM", userLogin);

	    	                        	// 时间达到半小时的保养，重置GUI减频系统
		    							double diffTime = System.currentTimeMillis() - ((Timestamp) pmFormMap.get("startTime")).getTime();
		    							if (diffTime/3600000 > 0.5) {
		    								GuiHelper.resetReduceRatioQty(eqpId);
		    							}
		    							
		    							returnStr = "此表单已完成，并成功更改MES设备状态为" + Constants.PM_END_STATUS;
	    	                        	Debug.logInfo("change status success [" + eqpId + "/" + pmIndex + "/" + Constants.PM_END_STATUS + "]", module);
    	        					}    	                        
	                            } else {
	                                returnStr="表单已成功结束，当前设备状态为"+eqpStatus+"，系统不更改设备状态";
	                            }
	                    	} catch(Exception e){
	                        	//重新保存表单,将状态改为暂存
	                        	pmFormMap.put("status", String.valueOf(Constants.HOLD));
	                            PmHelper.overPmForm(delegator, pmFormMap, formType);
	                            Debug.logInfo("overPmform fail [" + eqpId + "/" + pmIndex + "] hold", module);
	                            
	                            String eStr = e.toString();    	                        	
	                			if (eqpId.startsWith("6") && eStr.indexOf("没有登陆或权力不足") > -1) {
	                				returnStr = "权限不足：请检查确认 PM系统 与 GUI 登录密码是否一致，需先修改为相同的密码。";
	                			} else {
	                				returnStr = "修改MES设备状态发生错误，请重新完成表单！";
	                			}
	                            Debug.logError("change status fail [" + eqpId + "/" + pmIndex + "/" + Constants.PM_END_STATUS + "] " + eStr, module);
	                        }
	                    }
	                } else {
	                	if(CommonUtil.isNotEmpty(pmFormList)) {
	                        returnStr += "\n存在未完成的保养表单";
	                        Debug.logInfo("has undo pmlist", module);
	                        for(int i=0;i<pmFormList.size();i++)
	                        {
	                            returnStr=((GenericValue)pmFormList.get(i)).getString("pmName");
	                            if(i!=pmFormList.size())
	                            {
	                                returnStr=returnStr+",";
	                            }
	                        }
	                    }
	                    
	                	if(CommonUtil.isNotEmpty(abnormalFormList)) {
	                        returnStr += "\n存在未完成的异常表单 ";
	                        Debug.logInfo("has undo abnormallist", module);
	                        for(int i=0;i<abnormalFormList.size();i++)
	                        {
	                            returnStr = returnStr + ((GenericValue)abnormalFormList.get(i)).getString("abnormalName");
	                            if(i!=abnormalFormList.size())
	                            {
	                                returnStr = returnStr + ",";
	                            }
	                        }
	                    }                    
	                }
	            } else {//不需要修改设备状态
	            	GenericValue period = PmHelper.getPeriod(delegator, pmForm.getString("periodIndex"));
	            	returnStr = "表单已成功结束，" + period.getString("eqpType") + "的保养周期"
							+ period.getString("periodName") + " 已设置 不改变MES设备状态";
	            }
            	
            	//设置GUI检查步骤自动hold，触发QC                
                GuiHelper.saveEqpMaintainHoldList(delegator, parmMap);
            } else {//补填表单
            	returnStr = "表单已成功结束，补填表单 不改变MES设备状态";
            }
            result.put("returnMsg", returnStr);
            
            //fab5不定期PM换靶：结束保养表单后，关闭MCS开关
            //更换到子设备上，需先结束母设备，再结束子设备表单。否则子设备表单结束时，MCS开关将关闭，母设备就无法结束表单。
            if (PmHelper.isPmChangeTarget(formType, periodName)) {
            	String endUser = (String) pmFormMap.get("endUser");
            	List list = PmHelper.listMcsPmChangeTarget(delegator, eqpId);
            	for (int i = 0; i < list.size(); i++) {
            		Map map = (Map) list.get(i);
            		String usingObjectId = (String) map.get("USING_OBJECT_ID");
            		
            		if (usingObjectId.equals(eqpId)) {
	            		GenericValue changeTargetGv = delegator.makeValidValue("McsPmChangeTarget", UtilMisc.toMap("usingObjectId", usingObjectId, "transBy", endUser, "updateTime", pmFormMap.get("updateTime"), "enabled", ConstantsMcs.INTEGER_0, "enabledType", "PMS"));
	            		delegator.store(changeTargetGv);
            		}
            	}
            }

        	//save pm form
            PmHelper.overPmForm(delegator, pmFormMap, formType);
            Debug.logInfo("overPmform success [" + eqpId + "/" + pmIndex + "] over", module);
            
        } catch (Exception e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
            Debug.logError(e.getMessage(), module);
        }
        return result;
    }
    
    public static Map creatSubPmForm(DispatchContext ctx, Map context) {
    	GenericDelegator delegator = ctx.getDelegator();
    	Map result = new HashMap();
    	String user = (String)context.get("user");
    	String equipmentId = (String)context.get("equipmentId");
    	String periodIndex = (String)context.get("periodIndex");
    	String formType = (String)context.get("formType");
    	String jobIndex = (String)context.get("jobIndex");
    	String scheduleDate = (String)context.get("scheduleDate");
    	try {
    		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    		//删除当天同周期的子设备表单设定
    		List scheduleList = delegator.findByAnd("EquipmentSchedule", 
    				UtilMisc.toMap("periodIndex", periodIndex ,"equipmentId", equipmentId, "scheduleDate", new java.sql.Date(sdfDate.parse(scheduleDate).getTime())));
//    		建立保养表单，并得到表单编号
            Long id = PmHelper.createPmRecord(delegator, user, equipmentId, periodIndex, formType);
            
            GenericValue schedule = null;
    		if(scheduleList.size() > 0) {
    			schedule = (GenericValue)scheduleList.get(0);
    			schedule.set("eventIndex", id);
    		} else {
	            Map paramMap = new HashMap();
	            
	            Long scheduleIndex = null;
	            String sql = "select seq_pm_schedule_index.nextval id from dual";
	            List list = SQLProcess.excuteSQLQuery(sql, delegator);
	            if(list.size() > 0) {
	            	Map map = (Map)list.get(0);
	            	scheduleIndex = Long.valueOf(String.valueOf(map.get("ID")));
	            }
	            
	            paramMap.put("scheduleIndex", scheduleIndex);
	            paramMap.put("equipmentId", equipmentId);
	            paramMap.put("periodIndex", periodIndex);
	            paramMap.put("scheduleDate", new java.sql.Date(sdfDate.parse(scheduleDate).getTime()));
	            paramMap.put("creator", user);
	            paramMap.put("scheduleEvent", "PM");
	            paramMap.put("createDate", new Timestamp(System.currentTimeMillis()));
	            paramMap.put("eventIndex", id);
	            schedule = delegator.makeValidValue("EquipmentSchedule", paramMap);
    		}
    		
            delegator.createOrStore(schedule);
            Debug.logInfo("create chamber success [" + equipmentId +"] jobIndex[" + jobIndex + "]", module);
            
            //插入Job            
            PmHelper.createFormJobRelation(delegator, user, id, jobIndex);
    	} catch (Exception e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
        }
        return result;
    }
    
    /**
     * 删除PM记录
     * @param ctx
     * @param context
     * @return
     */
    public static Map deletePmForm(DispatchContext ctx, Map context){
        GenericDelegator delegator = ctx.getDelegator();
        String pmIndex = (String) context.get("pmIndex");
        Map result = new HashMap();
        
        try {
            delegator.removeByAnd("PmForm", UtilMisc.toMap("pmIndex", pmIndex));
            delegator.removeByAnd("FormJobRelation", UtilMisc.toMap("eventIndex", pmIndex));
            
            PmHelper.updateEquipmentSchedule(delegator, pmIndex);
        } catch (Exception e) {
            result.put(ModelService.RESPONSE_MESSAGE,
                    ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
        }
        return result;
    }
    
    /**
     * 建立表单
     * @param ctx
     * @param context
     * @return
     */
    public static Map createPmForm(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        Map paramMap = (Map) context.get("paramMap");
        
        String eqpid = (String) paramMap.get("eqpId"); 
        String periodIndex = (String) paramMap.get("periodIndex");
        String formType = (String) paramMap.get("formType");    
        String scheduleIndex = (String) paramMap.get("scheduleIndex");
        String user = (String) paramMap.get("user"); 
        int  jobNum = Integer.parseInt((String) paramMap.get("jobNum"));
        
        Map result = new HashMap();

        try {
        	//step1.建立保养表单，并得到表单编号
            Long id = PmHelper.createPmRecord(delegator, user, eqpid, periodIndex, formType);
            Debug.logInfo("create pmform success [" + eqpid + "/" + id + "]", module);

            //step2.更新equipmentSchedule
            PmHelper.saveEquipmentSchedule(delegator, scheduleIndex, id);
            Debug.logInfo("save pm schedule success [" + eqpid + "/" + id + "/" + scheduleIndex + "]", module);
            
            //step3.根据选择的job数量,循环建立FormJobRelation的记录
            for (int i=1;i<=jobNum;i++) {
                String jobSelect = (String) paramMap.get("jobSelect_"+i);
                if ("1".equalsIgnoreCase(jobSelect)) {                   
                    String jobIndex = (String) paramMap.get("jobIndex_"+i);                    
                    PmHelper.createFormJobRelation(delegator, user, id, jobIndex);
                    Debug.logInfo("create job success [eqpid: " + eqpid + "/ pmIndex: " + id + "/ jobIndex: " + jobIndex + "]", module);
                }
            }  

        } catch (Exception e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
        }
        return result;
    }
}