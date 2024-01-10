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
 * �����쳣��¼��
 * @author shaoaj
 * @2007-9-14
 */
public class TsFormService {
	public static final String module = TsFormService.class.getName();
	
	/**
	 * ������ĸ�豸���쳣����Ϣ
	 * (1)������ĸ�豸���쳣��(ABNORMAL_FORM),������ĸ�豸���쳣�������(FORM_JOB_RELATION)
	 * (2)�����쳣��¼��PM_ABNORMAL_RECORD��formIndex��status��λ
	 * (3)��ĸ�豸���а�:����ĸ�豸�İ�״̬���µ���ĸ���쳣�󶨱�(ABNORMAL_BINDING)
	 * (4)��ĸ�豸δ��:�������豸���쳣�������(FORM_JOB_RELATION)
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
			// ------------------ĸ�豸�����Ϣ����------------------
			//��ѯ�쳣���Ƿ��ѽ�������ֹ����������
			if(TsHelper.hasDuplicatedTsForm(delegator, parMap)){
				result.put(ModelService.RESPONSE_MESSAGE,
						ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, "�쳣���ѽ�����");
				return result;
			}
			//����ĸ�豸�쳣����Ϣ
			Long eventIndex=TsHelper.saveTsForm(delegator, parMap);
			Map relatonMap=new HashMap();
			relatonMap.put("jobIndex", jobIndex);
			relatonMap.put("eventType", Constants.TS);
			relatonMap.put("eventIndex",eventIndex);
			relatonMap.put("creator",(String)map.get("createUser"));
			relatonMap.put("nextActionId", Integer.valueOf("0"));
			relatonMap.put("jobStatus", Integer.valueOf("0"));
			if(CommonUtil.isNotEmpty(jobIndex)) {
				//����ĸ�豸�Ĵ��������Ϣ
				TsHelper.saveJobRelation(delegator, relatonMap);
			}
			// 0:�����쳣���½�1:�ֹ�����
			if("0".equals(oper)){
				//�����쳣��¼״̬,formIndex
				TsHelper.updateAbnomalRecordStatus(delegator, (String)tsInfoMap.get("seqIndex"),String.valueOf(eventIndex));
			}
			// ----------------------END--------------------------
			Map bindMap=new HashMap();
			bindMap.put("parentIndex", eventIndex);
			
			for (Iterator it = tsInfoMap.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				if (key.startsWith("childEqpId_")) {
					// ������
					String orderNum = key.substring(key.indexOf("_") + 1);// ���index
					String childEqpId = (String) tsInfoMap.get("childEqpId_"+ orderNum);
					//�Ƿ���ڴ����豸
					if(StringUtils.isNotEmpty(childEqpId)){
						parMap.put("equipmentId", childEqpId);
						//�������豸�쳣����Ϣ
						Long childEventIndex;
						// ----------------------END--------------------------=
						String childEqpIdBind = (String) tsInfoMap.get("childEqpIdBind_" + orderNum);
						//�����豸�Ƿ���Ҫ��ĸ�豸���а�
						//���а󶨺�,���豸��ĸ������ͬһ�������,��״̬��Ϊ��״̬
						//�����а�,���豸���������������
						if(StringUtils.isNotEmpty(childEqpIdBind)){
							parMap.put("status", String.valueOf(Constants.BIND));
							childEventIndex=TsHelper.saveTsForm(delegator, parMap);
							bindMap.put("sonIndex", childEventIndex);
	        				TsHelper.saveAbnomalBind(delegator, bindMap);
	        				
	        			}else{
	        				parMap.put("status", String.valueOf(Constants.CREAT));
	        				childEventIndex=TsHelper.saveTsForm(delegator, parMap);
	        				relatonMap.put("eventIndex",childEventIndex);
							// �������豸���������Ϣ
	        				if(CommonUtil.isNotEmpty(jobIndex)) {
	        					TsHelper.saveJobRelation(delegator, relatonMap);
	        				}
	        			}
						// �����쳣��¼״̬,formIndex
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
	 *  �����ֱ�ӽ����������豸״̬�����������δ��ɵı�������������ܽ�����
	 *  ���û��δ��ɱ���������ѯ�Ƿ���δ��ɵ��쳣�������������
	 *                         ���û�У�����������޸��豸״̬��
	 *                         �������������������޸��豸״̬(���豸��ĸ�豸һ��)
	 * (1)�����豸����δ��ɱ�(�����豸�������������豸�쳣����)������ʾ�����˱�����ɣ�������δ��ɱ���������MES�豸״̬������
	 * (2)������ɱ���ͬһ�豸δ��ɱ����Ҹ���MES�豸״̬�ɹ�������ʾ�û������˱�����ɣ����ɹ�����MES�豸״̬Ϊ03-POST��
	 * (3)������MES�豸״̬ʧ�ܣ����쳣ά��������ɣ���ʾ�û������˱�����ɣ�������MES�豸״̬ʧ�ܣ�����ϵͳ���������磡��
	 * (4)���˱����ӱ���һ��ı��ӱ����豸״̬�����״̬ҲӦ��Ϊ���
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
		String returnStr = "�˱�����ɣ��ֹ������������MES�豸״̬";
		String status = String.valueOf(Constants.OVER);//�豸״̬
		Timestamp nowTs = UtilDateTime.nowTimestamp();
		
		try {			
			//�����Ҫ�ı��豸״̬
			if(Constants.FORM_TYPE_NORMAL.equals(formType)){
				String abnormalIndex = (String) abnormalFormMap.get("abnormalIndex");
				List pmFormList = TsHelper.getPmNoOverFormList(delegator, eqpId);
				
				//���豸û��δ��ɵı�������������޸��豸״̬,��ɴ˱�
				if (pmFormList==null||pmFormList.size()==0) {
					GenericValue parentAbnormalForm = delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex", abnormalIndex));
					
					List list = delegator.findByAnd("PmAbnormalRecord", UtilMisc.toMap("formIndex", abnormalIndex));
					GenericValue gv = (GenericValue) list.get(0);
					String sql = " EQUIPMENT_ID='"+eqpId+"' and status!='1' and seq_index!="+gv.getString("seqIndex");
					EntityWhereString con = new EntityWhereString(sql);
					
					//��ѯ�쳣��¼�����Ƿ���ڴ��豸���쳣��¼
					List recordList = delegator.findByCondition("PmAbnormalRecord", con, null, null);
					if (recordList == null || recordList.size() == 0) {
						// ���豸�������쳣��¼						
						
						//����03-REP�޸�Ϊ03-POST,05-PR�޸�Ϊ07-PRUN
						String newstatus = Constants.TS_END_STATUS;						
						// ��ɺ��豸״̬�޸�Ϊ03-FD
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
							// �����豸״̬��������
							if (!newstatus.equals(eqpStatus) && !PlldbHelper.isFab5Eqp(delegator, eqpId)) {
								ret = FabAdapter.runCallService(delegator, userLogin,
										statusMap, com.csmc.pms.webapp.util.Constants.EQP_STATUS_CHANGE);
								Debug.logInfo("�豸:"+eqpId+"��:"+abnormalIndex+"�޸�MES�豸״̬"+newstatus, module);
							}
							
							if (ModelService.RESPOND_ERROR.equals(ret.get(ModelService.RESPONSE_MESSAGE))) {
        						Debug.logError((String) ret.get(ModelService.ERROR_MESSAGE), module);
        					} else {
    							if (!newstatus.equals(eqpStatus)) {
    								//added by steven �豸״̬���ĳɹ�������QC
        							GuiHelper.triggerQc(eqpId,eqpStatus,newstatus,userLogin);
    							}
    							
    							/*double diffTime = System.currentTimeMillis() - ((Timestamp) abnormalFormMap.get("abnormalTime")).getTime();
    							if (diffTime/3600000 > 0.5) {
	    							// �쳣������Сʱ������GUI��Ƶϵͳ
	    							GuiHelper.resetReduceRatioQty(eqpId);
    							}*/ 							
    							
        						//�����쳣��¼״̬
        						TsHelper.overAbnormalRecord(delegator, abnormalIndex);
        						//�����쳣��
        						TsHelper.overAbnormalForm(delegator, abnormalFormMap);
        						returnStr = "�˱�����ɣ����ɹ�����MES�豸״̬Ϊ" + newstatus;							
    							Debug.logInfo("�豸:"+eqpId+"��:"+abnormalIndex+"�����", module);
        					}
							
						} catch (Exception e) {
							status = String.valueOf(Constants.HOLD);
							returnStr = "Error: �޸�MES�豸״̬����������������ɱ���";
							Debug.logError("������ʱ��ĸ�豸����MES�豸״̬" + newstatus + "ʧ�ܣ�����ϵͳ���������磡�豸:"+eqpId+"��:"+abnormalFormMap.get("abnormalIndex")+"\r\n",module);
							Debug.logError(e, module);
						}
						
						//�����ӱ���״̬�����豸״̬
						List sonFormList=TsHelper.getSonFormList(delegator, abnormalIndex);
						if(sonFormList!=null&&sonFormList.size()>0){
							String childMsg = "";
							for(int k=0;k<sonFormList.size();k++){
								GenericValue sonForm = (GenericValue)sonFormList.get(k);								
								GenericValue form = delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex",sonForm.getString("sonIndex")));
								String formEqpId = form.getString("equipmentId");
								statusMap.put("eqpid", formEqpId);
								try {
									// �����豸״̬��������
									if (!newstatus.equals(eqpStatus)) {
										ret = FabAdapter.runCallService(delegator, userLogin,
											statusMap, com.csmc.pms.webapp.util.Constants.EQP_STATUS_CHANGE);
										Debug.logInfo("���豸:"+formEqpId+"�޸�MES�豸״̬" + newstatus ,module);										
									}
									
									if (ModelService.RESPOND_ERROR.equals(ret.get(ModelService.RESPONSE_MESSAGE))) {
		        						Debug.logError((String) ret.get(ModelService.ERROR_MESSAGE), module);
		        					} else {
		        						if (!newstatus.equals(eqpStatus)) {
											// added by steven �豸״̬���ĳɹ�������QC
											GuiHelper.triggerQc(formEqpId,eqpStatus,newstatus,userLogin);
										}
		        						
		        						//�����쳣��¼
										TsHelper.overAbnormalRecord(delegator, (String)sonForm.getString("sonIndex"));
										//�����ӱ�
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
										Debug.logInfo("���豸:"+formEqpId+"��:"+sonForm.getString("sonIndex")+"�����", module);
		        					}		    							
								} catch (Exception e) {
									childMsg = childMsg+formEqpId+" error;";
									Debug.logError(e, module);
								}
							}
							
							if(!"".equals(childMsg)){
								returnStr=returnStr+"\r\n"+"�ӱ��ɹ�������MES�豸״̬�޸�ʧ��:"+childMsg;
								Debug.logInfo(returnStr+"--����:"+abnormalIndex,module);
							}else{
								returnStr=returnStr+"\r\n"+"�ӱ��ɹ�������MES�豸״̬�ɹ��޸�!";
							}
						}
					} else {
						// ���豸 �� �����쳣��¼
						GenericValue otherTsRecord = (GenericValue) recordList.get(0);						
						returnStr = "�豸 " + otherTsRecord.getString("equipmentId") + " ����" 
								+ otherTsRecord.getString("startTime") + "�����������쳣�������ɹ����������޸��豸״̬!";
						
						//������
						TsHelper.overAbnormalForm(delegator, abnormalFormMap);
						// ���쳣��¼״̬
						TsHelper.overAbnormalRecord(delegator, abnormalIndex);
						
						// �����ӱ���״̬
						List sonFormList=TsHelper.getSonFormList(delegator, abnormalIndex);
						if(sonFormList!=null&&sonFormList.size()>0){
							for(int k=0;k<sonFormList.size();k++){
								GenericValue sonForm=(GenericValue)sonFormList.get(k);
								GenericValue form=delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex",sonForm.getString("sonIndex")));
								//����쳣��¼
								TsHelper.overAbnormalRecord(delegator, (String)sonForm.getString("sonIndex"));
								//�����쳣��								
								GenericValue childForm=delegator.makeValidValue("AbnormalForm", form);
								childForm.put("startUser", parentAbnormalForm.getString("startUser"));
								childForm.put("startTime",parentAbnormalForm.getTimestamp("startTime"));
								childForm.put("status", String.valueOf(Constants.OVER));
								childForm.put("endTime", nowTs);
								childForm.put("updateTime", nowTs);
								delegator.store(childForm);
							}
							returnStr=returnStr+"\r\n"+"�ӱ��ɹ����������������쳣��¼�����޸��豸״̬!";
						}
						Debug.logInfo("ĸ���ѳɹ�����,�����쳣��¼���д��ڴ��豸δ��ɵ�������,������MES�豸״̬���豸:"+eqpId+"�˱���"+abnormalFormMap.get("abnormalIndex"),module);
					}
					
					//����GUI��鲽���Զ�hold������QC
	                GuiHelper.saveEqpMaintainHoldList(delegator, parmMap);
				} else {
					// ���豸�� δ��� ������
					GenericValue pmFormGv = (GenericValue) pmFormList.get(0);
					returnStr = "��������ɣ��豸 " + eqpId + " ����"
								+ pmFormGv.getString("createTime") + "�����ı�������������ɱ�����!";
					Debug.logInfo("���豸����δ��ɵı�����!�豸:"+eqpId+"����"+abnormalIndex,module);
				}
			}else{
				// �ֹ������ֱ�ӽ�����
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
     * �޸Ļ�ɾ���쳣�ڼ�ı����ƻ�
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
		String returnStr = "�쳣��ر����ƻ����޸Ļ�ɾ�����뽨������ɽ���ı���";
		
		try {
			// 1.��ѡ�ı������͵�������ƻ�����ɾ������������ı����ƻ����ɡ�
			String initDelSql = "delete from equipment_schedule "
							+ " where (equipment_id,period_index) in "
							+ " (select t.equipment_id,t.period_index from equipment_schedule t"
							+ " where t.equipment_id='" + eqpId + "' and t.schedule_date=trunc(sysdate))" 
							+ " and schedule_index in (" + modifySchedule + ")";
			int i = SQLProcess.excuteSQLUpdate(initDelSql, delegator);
			Debug.logInfo("delete " + i + " rows, initDelSql: [" + initDelSql+ "]", module);
			
			// 2.����֮ǰ�ı����ƻ�������
			String modifySql = "update equipment_schedule t set t.schedule_date=trunc(sysdate),"
							+ "t.creator='" + updateUser + "',t.create_date=sysdate"
							+ " where t.schedule_index in (" + modifySchedule + ")";
			i = SQLProcess.excuteSQLUpdate(modifySql, delegator);
	        Debug.logInfo("update " + i + " rows, modifySql: [" + modifySql+ "]", module);
	        
	        // 3.ɾ��δ��ѡ�ı����ƻ�
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
