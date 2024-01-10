/**
 * 
 */
package com.csmc.pms.webapp.form.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityWhereString;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.eqp.helper.FabAdapter;
import com.csmc.pms.webapp.eqp.helper.GuiHelper;
import com.csmc.pms.webapp.eqp.helper.PlldbHelper;
import com.csmc.pms.webapp.form.help.PmHelper;
import com.csmc.pms.webapp.form.help.TsHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;

/**
 * 建立异常记录表单
 * @author shaoaj
 * @2007-9-14
 */
public class TsFormService {
	public static final String module = TsFormService.class.getName();
	
	/**
	 * 保存子母设备的异常表单信息
	 * (1)保存子母设备的异常表单(ABNORMAL_FORM),并保存母设备的异常处理程序(FORM_JOB_RELATION)
	 * (2)更新异常记录表PM_ABNORMAL_RECORD的formIndex及status栏位
	 * (3)子母设备进行绑定:将子母设备的绑定状态更新到子母表单异常绑定表(ABNORMAL_BINDING)
	 * (4)子母设备未绑定:保存子设备的异常处理程序(FORM_JOB_RELATION)
	 * 
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map saveTsForm(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		Map tsInfoMap = (Map) context.get("tsInfoMap");
		Map map = (Map) context.get("parmMap");
		String eqpId = (String) tsInfoMap.get("eqpId");
		String jobIndex = (String) tsInfoMap.get("jobIndex");
		String oper=(String)map.get("operType");
		Map result = new HashMap();
		Timestamp nowTs = UtilDateTime.nowTimestamp();
		
		Map parMap=new HashMap();
        parMap.put("equipmentId", eqpId);
        parMap.put("createTime", nowTs);
        parMap.put("createUser",(String)map.get("createUser"));
        parMap.put("createName",(String)map.get("accountName"));
        parMap.put("updateTime", nowTs);
        parMap.put("formType",(String)map.get("formType"));
        parMap.put("status", String.valueOf(Constants.CREAT));
        if("0".equals(oper)){
        	Timestamp tm=Timestamp.valueOf((String)tsInfoMap.get("abnormalTime"));
        	parMap.put("abnormalTime",tm);
        }else if("1".equals(oper)){
        	parMap.put("abnormalTime", nowTs);
        }
		try {
			// ------------------母设备相关信息保存------------------
			//查询异常表单是否已建立（防止并发操作）
			if(TsHelper.hasDuplicatedTsForm(delegator, parMap)){
				result.put(ModelService.RESPONSE_MESSAGE,
						ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, "异常表单已建立！");
				return result;
			}
			//保存母设备异常表单信息
			Long eventIndex=TsHelper.saveTsForm(delegator, parMap);
			Map relatonMap=new HashMap();
			relatonMap.put("jobIndex", jobIndex);
			relatonMap.put("eventType", Constants.TS);
			relatonMap.put("eventIndex",eventIndex);
			relatonMap.put("creator",(String)map.get("createUser"));
			relatonMap.put("nextActionId", Integer.valueOf("0"));
			relatonMap.put("jobStatus", Integer.valueOf("0"));
			if(CommonUtil.isNotEmpty(jobIndex)) {
				//保存母设备的处理程序信息
				TsHelper.saveJobRelation(delegator, relatonMap);
			}
			// 0:根据异常表单新建1:手工补填
			if("0".equals(oper)){
				//更新异常记录状态,formIndex
				TsHelper.updateAbnomalRecordStatus(delegator, (String)tsInfoMap.get("seqIndex"),String.valueOf(eventIndex));
			}
			// ----------------------END--------------------------
			Map bindMap=new HashMap();
			bindMap.put("parentIndex", eventIndex);
			
			for (Iterator it = tsInfoMap.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				if (key.startsWith("childEqpId_")) {
					// 获得序号
					String orderNum = key.substring(key.indexOf("_") + 1);// 获得index
					String childEqpId = (String) tsInfoMap.get("childEqpId_"+ orderNum);
					//是否存在此子设备
					if(StringUtils.isNotEmpty(childEqpId)){
						parMap.put("equipmentId", childEqpId);
						//保存子设备异常表单信息
						Long childEventIndex;
						// ----------------------END--------------------------=
						String childEqpIdBind = (String) tsInfoMap.get("childEqpIdBind_" + orderNum);
						//此子设备是否需要和母设备进行绑定
						//进行绑定后,子设备和母设务用同一处理程序,表单状态改为绑定状态
						//不进行绑定,子设备单独建立处理程序
						if(StringUtils.isNotEmpty(childEqpIdBind)){
							parMap.put("status", String.valueOf(Constants.BIND));
							childEventIndex=TsHelper.saveTsForm(delegator, parMap);
							bindMap.put("sonIndex", childEventIndex);
	        				TsHelper.saveAbnomalBind(delegator, bindMap);
	        				
	        			}else{
	        				parMap.put("status", String.valueOf(Constants.CREAT));
	        				childEventIndex=TsHelper.saveTsForm(delegator, parMap);
	        				relatonMap.put("eventIndex",childEventIndex);
							// 保存子设备处理程序信息
	        				if(CommonUtil.isNotEmpty(jobIndex)) {
	        					TsHelper.saveJobRelation(delegator, relatonMap);
	        				}
	        			}
						// 更新异常记录状态,formIndex
						TsHelper.updateAbnomalRecordStatus(delegator, (String)tsInfoMap.get("childSeqIndex_"+orderNum),String.valueOf(childEventIndex));
					}
				}
			}
		} catch (Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE,
					ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}
	
	/**
	 *  补填的直接结束，不改设备状态；如果有其它未完成的保养表单，则表单不能结束。
	 *  如果没有未完成保养表单，查询是否有未完成的异常表单（不含补填），
	 *                         如果没有，则结束表单，修改设备状态，
	 *                         如果存在则结束表单，不修改设备状态(子设备和母设备一起)
	 * (1)若此设备尚有未完成表单(含「设备保养表单」、「设备异常表单」)，则提示：【此表单已完成，但尚有未完成表单，不更改MES设备状态！】。
	 * (2)若此完成表单无同一设备未完成表单，且更改MES设备状态成功，则提示用户：【此表单已完成，并成功更改MES设备状态为03-POST】
	 * (3)若更改MES设备状态失败，则异常维修仍视完成，提示用户：【此表单已完成，但更新MES设备状态失败，请与系统管理者联络！】
	 * (4)若此表单有子表单需一起改变子表单的设备状态及其表单状态也应改为完成
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map overAbnormalForm(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		Map abnormalFormMap = (Map) context.get("abnormalFormMap");
		Map parmMap = (Map) context.get("parmMap");
		String eqpId = (String) parmMap.get("eqpId");
		String formType = (String) parmMap.get("formType");
		GenericValue userLogin = (GenericValue) parmMap.get("userLogin");
		String eqpStatusChangeTo = (String) parmMap.get("eqpStatusChangeTo");
		
		
		
		Map result = new HashMap();
		String returnStr = "此表单已完成，手工补填表单不更改MES设备状态";
		String status = String.valueOf(Constants.OVER);//设备状态
		Timestamp nowTs = UtilDateTime.nowTimestamp();
		
		try {			
			//补填不需要改变设备状态
			if(Constants.FORM_TYPE_NORMAL.equals(formType)){
				String abnormalIndex = (String) abnormalFormMap.get("abnormalIndex");
				List pmFormList = TsHelper.getPmNoOverFormList(delegator, eqpId);
				
				//此设备没有未完成的保养表单，则可以修改设备状态,完成此表单
				if (pmFormList==null||pmFormList.size()==0) {
					GenericValue parentAbnormalForm = delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex", abnormalIndex));
					
					List list = delegator.findByAnd("PmAbnormalRecord", UtilMisc.toMap("formIndex", abnormalIndex));
					GenericValue gv = (GenericValue) list.get(0);
					String sql = " EQUIPMENT_ID='"+eqpId+"' and status!='1' and seq_index!="+gv.getString("seqIndex");
					EntityWhereString con = new EntityWhereString(sql);
					
					//查询异常记录表中是否存在此设备的异常记录
					List recordList = delegator.findByCondition("PmAbnormalRecord", con, null, null);
					if (recordList == null || recordList.size() == 0) {
						// 此设备无其它异常记录						
						
						//结束03-REP修改为03-POST,05-PR修改为07-PRUN
						String newstatus = Constants.TS_END_STATUS;						
						// 完成后设备状态修改为03-FD
						if (StringUtils.isNotEmpty(eqpStatusChangeTo)) {
							newstatus = eqpStatusChangeTo;
						}
						Map retEqpStatus = FabAdapter.runCallService(delegator, userLogin,
                        UtilMisc.toMap("eqpid", eqpId), com.csmc.pms.webapp.util.Constants.EQP_INFO_QUERY);
                        String eqpStatus = (String)retEqpStatus.get("status");
                        if (TsHelper.isPRStartStatus(eqpStatus)) {                        	
                        	newstatus = Constants.PR_END_STATUS;
                        }
						
						Map statusMap = new HashMap();
						statusMap.put("eqpid", eqpId);
						statusMap.put("newstatus", newstatus);
						statusMap.put("comment", "PMS");
						
						Map ret = new HashMap();
						try {
							// 更改设备状态，结束表单
							if (!newstatus.equals(eqpStatus) && !PlldbHelper.isFab5Eqp(delegator, eqpId)) {
								ret = FabAdapter.runCallService(delegator, userLogin,
										statusMap, com.csmc.pms.webapp.util.Constants.EQP_STATUS_CHANGE);
								Debug.logInfo("设备:"+eqpId+"表单:"+abnormalIndex+"修改MES设备状态"+newstatus, module);
							}
							
							if (ModelService.RESPOND_ERROR.equals(ret.get(ModelService.RESPONSE_MESSAGE))) {
        						Debug.logError((String) ret.get(ModelService.ERROR_MESSAGE), module);
        					} else {
    							if (!newstatus.equals(eqpStatus)) {
    								//added by steven 设备状态更改成功，触发QC
        							GuiHelper.triggerQc(eqpId,eqpStatus,newstatus,userLogin);
    							}
    							
    							/*double diffTime = System.currentTimeMillis() - ((Timestamp) abnormalFormMap.get("abnormalTime")).getTime();
    							if (diffTime/3600000 > 0.5) {
	    							// 异常超过半小时，重置GUI减频系统
	    							GuiHelper.resetReduceRatioQty(eqpId);
    							}*/ 							
    							
        						//更改异常记录状态
        						TsHelper.overAbnormalRecord(delegator, abnormalIndex);
        						//结束异常表单
        						TsHelper.overAbnormalForm(delegator, abnormalFormMap);
        						returnStr = "此表单已完成，并成功更改MES设备状态为" + newstatus;							
    							Debug.logInfo("设备:"+eqpId+"表单:"+abnormalIndex+"已完成", module);
        					}
							
						} catch (Exception e) {
							status = String.valueOf(Constants.HOLD);
							returnStr = "Error: 修改MES设备状态发生错误，请重新完成表单！";
							Debug.logError("表单结束时，母设备更新MES设备状态" + newstatus + "失败，请与系统管理者联络！设备:"+eqpId+"表单:"+abnormalFormMap.get("abnormalIndex")+"\r\n",module);
							Debug.logError(e, module);
						}
						
						//更新子表单的状态及其设备状态
						List sonFormList=TsHelper.getSonFormList(delegator, abnormalIndex);
						if(sonFormList!=null&&sonFormList.size()>0){
							String childMsg = "";
							for(int k=0;k<sonFormList.size();k++){
								GenericValue sonForm = (GenericValue)sonFormList.get(k);								
								GenericValue form = delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex",sonForm.getString("sonIndex")));
								String formEqpId = form.getString("equipmentId");
								statusMap.put("eqpid", formEqpId);
								try {
									// 更改设备状态，结束表单
									if (!newstatus.equals(eqpStatus)) {
										ret = FabAdapter.runCallService(delegator, userLogin,
											statusMap, com.csmc.pms.webapp.util.Constants.EQP_STATUS_CHANGE);
										Debug.logInfo("子设备:"+formEqpId+"修改MES设备状态" + newstatus ,module);										
									}
									
									if (ModelService.RESPOND_ERROR.equals(ret.get(ModelService.RESPONSE_MESSAGE))) {
		        						Debug.logError((String) ret.get(ModelService.ERROR_MESSAGE), module);
		        					} else {
		        						if (!newstatus.equals(eqpStatus)) {
											// added by steven 设备状态更改成功，触发QC
											GuiHelper.triggerQc(formEqpId,eqpStatus,newstatus,userLogin);
										}
		        						
		        						//结束异常记录
										TsHelper.overAbnormalRecord(delegator, (String)sonForm.getString("sonIndex"));
										//结束子表单
										GenericValue childForm = delegator.makeValidValue("AbnormalForm", form);
										childForm.put("startUser", parentAbnormalForm.getString("startUser"));
										childForm.put("startTime",parentAbnormalForm.getTimestamp("startTime"));
										childForm.put("returnTime",parentAbnormalForm.getTimestamp("returnTime"));
										childForm.put("endUser",parentAbnormalForm.getString("endUser"));
										childForm.put("jobText",parentAbnormalForm.getString("jobText"));
										childForm.put("status", status);
										childForm.put("endTime", nowTs);
										childForm.put("updateTime", nowTs);										
										delegator.store(childForm);
										Debug.logInfo("子设备:"+formEqpId+"表单:"+sonForm.getString("sonIndex")+"已完成", module);
		        					}		    							
								} catch (Exception e) {
									childMsg = childMsg+formEqpId+" error;";
									Debug.logError(e, module);
								}
							}
							
							if(!"".equals(childMsg)){
								returnStr=returnStr+"\r\n"+"子表单成功结束，MES设备状态修改失败:"+childMsg;
								Debug.logInfo(returnStr+"--父表单:"+abnormalIndex,module);
							}else{
								returnStr=returnStr+"\r\n"+"子表单成功结束，MES设备状态成功修改!";
							}
						}
					} else {
						// 此设备 有 其它异常记录
						GenericValue otherTsRecord = (GenericValue) recordList.get(0);						
						returnStr = "设备 " + otherTsRecord.getString("equipmentId") + " 存在" 
								+ otherTsRecord.getString("startTime") + "发生的其他异常表单，表单成功结束，不修改设备状态!";
						
						//结束表单
						TsHelper.overAbnormalForm(delegator, abnormalFormMap);
						// 更异常记录状态
						TsHelper.overAbnormalRecord(delegator, abnormalIndex);
						
						// 更新子表单的状态
						List sonFormList=TsHelper.getSonFormList(delegator, abnormalIndex);
						if(sonFormList!=null&&sonFormList.size()>0){
							for(int k=0;k<sonFormList.size();k++){
								GenericValue sonForm=(GenericValue)sonFormList.get(k);
								GenericValue form=delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex",sonForm.getString("sonIndex")));
								//完成异常记录
								TsHelper.overAbnormalRecord(delegator, (String)sonForm.getString("sonIndex"));
								//结束异常表单								
								GenericValue childForm=delegator.makeValidValue("AbnormalForm", form);
								childForm.put("startUser", parentAbnormalForm.getString("startUser"));
								childForm.put("startTime",parentAbnormalForm.getTimestamp("startTime"));
								childForm.put("status", String.valueOf(Constants.OVER));
								childForm.put("endTime", nowTs);
								childForm.put("updateTime", nowTs);
								delegator.store(childForm);
							}
							returnStr=returnStr+"\r\n"+"子表单成功结束，还有其它异常记录，不修改设备状态!";
						}
						Debug.logInfo("母表单已成功结束,由于异常记录表中存在此设备未完成的其它表单,不更改MES设备状态！设备:"+eqpId+"此表单："+abnormalFormMap.get("abnormalIndex"),module);
					}
					
					//设置GUI检查步骤自动hold，触发QC
	                GuiHelper.saveEqpMaintainHoldList(delegator, parmMap);
				} else {
					// 此设备有 未完成 保养表单
					GenericValue pmFormGv = (GenericValue) pmFormList.get(0);
					returnStr = "表单不能完成，设备 " + eqpId + " 存在"
								+ pmFormGv.getString("createTime") + "建立的保养表单，请先完成保养表单!";
					Debug.logInfo("此设备存在未完成的保养表单!设备:"+eqpId+"表单："+abnormalIndex,module);
				}
			}else{
				// 手工作补填，直接结束表单
				TsHelper.overAbnormalForm(delegator, abnormalFormMap);
			}
			
			result.put("returnMsg", returnStr);
		} catch (Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			Debug.logError(e,module);
		}
		return result;
	}

	/**
     * 修改或删除异常期间的保养计划
     * @param request
     * @param response
     * @return Map
     */
	public static Map reScheduleTsUndoPM(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		Map paramMap = (Map) context.get("paramMap");
		String eqpId = (String) paramMap.get("eqpId");
		String modifySchedule = (String) paramMap.get("modifySchedule");
		String deleteSchedule = (String) paramMap.get("deleteSchedule");
		String updateUser = (String) paramMap.get("updateUser");
		
		Map result = new HashMap();
		String returnStr = "异常相关保养计划已修改或删除，请建立并完成今天的保养";
		
		try {
			// 1.勾选的保养类型当天已设计划，则删除，保留当天的保养计划即可。
			String initDelSql = "delete from equipment_schedule "
							+ " where (equipment_id,period_index) in "
							+ " (select t.equipment_id,t.period_index from equipment_schedule t"
							+ " where t.equipment_id='" + eqpId + "' and t.schedule_date=trunc(sysdate))" 
							+ " and schedule_index in (" + modifySchedule + ")";
			int i = SQLProcess.excuteSQLUpdate(initDelSql, delegator);
			Debug.logInfo("delete " + i + " rows, initDelSql: [" + initDelSql+ "]", module);
			
			// 2.更新之前的保养计划到当天
			String modifySql = "update equipment_schedule t set t.schedule_date=trunc(sysdate),"
							+ "t.creator='" + updateUser + "',t.create_date=sysdate"
							+ " where t.schedule_index in (" + modifySchedule + ")";
			i = SQLProcess.excuteSQLUpdate(modifySql, delegator);
	        Debug.logInfo("update " + i + " rows, modifySql: [" + modifySql+ "]", module);
	        
	        // 3.删除未勾选的保养计划
	        String deleteSql = "delete from equipment_schedule t "
							+ " where t.schedule_index in (" + deleteSchedule + ")";
	        i = SQLProcess.excuteSQLUpdate(deleteSql, delegator);
	        Debug.logInfo("delete " + i + " rows, deleteSql: [" + deleteSql+ "]", module);
	        
			result.put("returnMsg", returnStr);
		} catch (Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE,
					ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			Debug.logError(e,module);
		}
		return result;
	}
}
