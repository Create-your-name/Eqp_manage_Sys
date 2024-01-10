package com.csmc.pms.webapp.workflow.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.MiscUtils;
import com.csmc.pms.webapp.workflow.help.WorkflowHelper;

public class WfSubmitService {
	public static final String moduel = WfSubmitService.class.getName();
	
	//����ǩ�ˣ�
	//1���̣��豸�γ�����
	//2������Ŀ���豸�γ����������տγ�����
	//3������٣����տγ�����
	//4�������ڣ��豸�γ����������տγ�����
	public static Map sendSubmit(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		Map result = new HashMap();

		String user = (String) context.get("user");
		String type = (String) context.get("type");
		String object = (String) context.get("object");
		String objectIndex = (String) context.get("objectIndex");
		String objectName = (String) context.get("objectName");
		String ownerProcess = (String) context.get("ownerProcess");
		
		try {
			Timestamp nowTime = UtilDateTime.nowTimestamp();
			
			GenericValue userInfo = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));
			String userName = userInfo.getString("accountName");
			String section = userInfo.getString("accountSection");
			
			//ȡ�õ�¼�˵ĿƳ� TODO ����Conditionȡ��ǩ���ˣ�����SubmitList�����ǩ����"|",�͵������У�
			String leader = AccountHelper.querySectionLeaderBySection(delegator, section);			
			// 883609��huanghp�����û��ȡ�����������ܣ���ʾһ�£�2009-02
			if (Constants.SUPER_USER.equals(leader)) {
				result.put(ModelService.ERROR_MESSAGE, "NoLeader");
			}
			
			//����ǩ����ϢWfSubmit
			Map makeValueMap = new HashMap();
			Long submitIndex = delegator.getNextSeqId("submitIndex");
			makeValueMap.put("submitIndex", submitIndex);
			makeValueMap.put("object", object);
			makeValueMap.put("objectIndex", objectIndex);
			makeValueMap.put("objectName", objectName);
			makeValueMap.put("type", type);					
			makeValueMap.put("creator", user);
			makeValueMap.put("creatorName", userName.toUpperCase());
			makeValueMap.put("createTime", nowTime);
			makeValueMap.put("updateTime", nowTime);
			makeValueMap.put("owner", leader);
			makeValueMap.put("status", Constants.SUBMITED);
			
			if (CommonUtil.isNotEmpty(ownerProcess)) {
				//������Ŀ��������١������������룬�蹤�տγ�����
				makeValueMap.put("ownerProcess", ownerProcess);
				makeValueMap.put("statusProcess", Constants.SUBMITED);
				
				if (object.equals(Constants.SUBMIT_FOLLOW)) {
					//������ٹ��տγ�����ʱ�������豸�γ�ͬʱ����
					makeValueMap.put("owner", null);
					makeValueMap.put("status", null);
				}
			}
			
			GenericValue submit = delegator.makeValue("WfSubmit", makeValueMap);
			delegator.create(submit);
			
			//����ǩ�˼�¼WfSubmitControl
			Map submitControlMap = new HashMap();
			submitControlMap.put("submitIndex", submitIndex);
			submitControlMap.put("status", Constants.SUBMITED);
			submitControlMap.put("approver", user);
			submitControlMap.put("approveTime", nowTime);
			WorkflowHelper.saveSubmitControl(delegator, submitControlMap);
			
			//����object������ر�
			if (object.equals(Constants.SUBMIT_FLOW)) {//����
				GenericValue tempValue = delegator.findByPrimaryKey("FlowJobTemp", UtilMisc.toMap("tempIndex", objectIndex));
				tempValue.set("status", new Integer(Constants.SUBMITED_CODE));//��ǩ��
				tempValue.set("submitType", type);
				tempValue.set("updateTime", nowTime);
				delegator.store(tempValue);
			} else if (object.equals(Constants.SUBMIT_FLOW_ACTION_ITEM)) {//������Ŀ
				Map tempMap = new HashMap();
				tempMap.put("flowActionItemTempIndex", objectIndex);
				tempMap.put("status", new Integer(Constants.SUBMITED_CODE));
				WorkflowHelper.saveFlowActionItemTemp(delegator, tempMap);
			} else if (object.equals(Constants.SUBMIT_PM_DELAY)) {//��������				
				GenericValue gv = delegator.findByPrimaryKey("EquipmentSchedule", UtilMisc.toMap("scheduleIndex", objectIndex));
				gv.put("scheduleNote", "�������ڵ�" + getNewScheduleDateFromObjectName(objectName));
				delegator.store(gv);
			}else if (object.equals("PMFORM") || object.equals("PM_PARTS_USE")){
				Map FormMap = new HashMap();
				FormMap.put("pmIndex", objectIndex);
				FormMap.put("lockStatus", "SUBMITED");
				FormMap.put("lockUser", user);
				GenericValue gv=delegator.makeValidValue("PmForm", FormMap);
				delegator.store(gv); 
			}else if (object.equals("TSFORM") || object.equals("TS_PARTS_USE")){
				Map FormMap = new HashMap();
				FormMap.put("abnormalIndex", objectIndex);
				FormMap.put("lockStatus", "SUBMITED");
				FormMap.put("lockUser", user);
				GenericValue gv=delegator.makeValidValue("AbnormalForm", FormMap);
				delegator.store(gv); 
			}
		} catch(Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * ���̵�ǩ��
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map flowSubmit(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		Map result = new HashMap();
		
		GenericValue submit = (GenericValue) context.get("submit");
		String status = (String) context.get("status");
		String user = (String) context.get("user");
		Long objectIndex = submit.getLong("objectIndex");
		String comment = (String) context.get("comment");
		Long histIndex = null;
		
		try {
			String submitIndex = submit.getString("submitIndex");
			Timestamp nowTime = UtilDateTime.nowTimestamp();
			
			if(Constants.APPROVED.equalsIgnoreCase(status)) {
				String type = submit.getString("type");
				GenericValue tempValue = delegator.findByPrimaryKey("FlowJobTemp", UtilMisc.toMap("tempIndex", objectIndex));
				if (tempValue != null){
					tempValue.set("status", new Integer(Constants.APPROVED_CODE));
					tempValue.set("updateTime", nowTime);
					//���ݲ�ͬType���ò�ͬ�Ĵ������ִ��
					if(Constants.SUBMIT_INSERT.equals(type)) {
						//����flow��
						GenericValue flowValue = delegator.makeValidValue("FlowJob", tempValue);
						delegator.create(flowValue);
						//��¼��ʷ
						GenericValue flowHistValue = delegator.makeValidValue("FlowJobHist", tempValue);
						histIndex = delegator.getNextSeqId("flowJobHistIndex");
						flowHistValue.set("histIndex", histIndex);
						flowHistValue.set("evt", Constants.INSERT);
						delegator.create(flowHistValue);
						//ɾ��tempValue
						delegator.removeValue(tempValue);
					} else if(Constants.SUBMIT_MODIFY.equals(type)) {
						//����flow��
						GenericValue flowValue = delegator.makeValidValue("FlowJob", tempValue);
						delegator.store(flowValue);
						//��¼��ʷ
						GenericValue flowHistValue = delegator.makeValidValue("FlowJobHist", tempValue);
						histIndex = delegator.getNextSeqId("flowJobHistIndex");
						flowHistValue.set("histIndex", histIndex);
						flowHistValue.set("evt", Constants.UPDATE);
						delegator.create(flowHistValue);
						//ɾ��tempValue
						delegator.removeValue(tempValue);
					} else if(Constants.SUBMIT_DELETE.equals(type)) {
						//��¼��ʷ
						GenericValue flowHistValue = delegator.makeValidValue("FlowJobHist", tempValue);
						histIndex = delegator.getNextSeqId("flowJobHistIndex");
						flowHistValue.set("histIndex", histIndex);
						flowHistValue.set("evt", Constants.DELETE);
						delegator.create(flowHistValue);
						//ɾ������
						delegator.removeByAnd("FlowJob", UtilMisc.toMap("jobIndex", tempValue.getLong("jobIndex")));
						//ɾ��tempValue
						delegator.removeValue(tempValue);
					}
				}
			} else if(Constants.REJECTED.equalsIgnoreCase(status)) {
//				delegator.removeByAnd("FlowJobTemp", UtilMisc.toMap("tempIndex", objectIndex));
				GenericValue tempValue = delegator.findByPrimaryKey("FlowJobTemp", UtilMisc.toMap("tempIndex", objectIndex));
				tempValue.set("status", new Integer(Constants.REJECTED_CODE));//�ܾ�״̬
				delegator.store(tempValue);
			}
			
			//����WfControl
			Map submitControlMap = new HashMap();
			submitControlMap.put("submitIndex", submitIndex);
			submitControlMap.put("status", status);
			submitControlMap.put("approver", user);
			submitControlMap.put("approveTime", nowTime);
			submitControlMap.put("submitComment", comment);
			WorkflowHelper.saveSubmitControl(delegator, submitControlMap);
			
			//����submit��Ϣ
			submit.put("status", status);
			submit.put("updateTime", nowTime);
			submit.put("flowJobHistIndex", histIndex);
			delegator.store(submit);
		} catch(Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * ������ٵ�ǩ��
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map followSubmit(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		Map result = new HashMap();
		
		GenericValue submit = (GenericValue) context.get("submit");
		String status = (String) context.get("status");
		String user = (String) context.get("user");
		String comment = (String) context.get("comment");
		
		try {
			String submitIndex = submit.getString("submitIndex");
			String objectIndex = submit.getString("objectIndex");
			String type = submit.getString("type");
			String ownerProcess = submit.getString("ownerProcess");
			Timestamp nowTime = UtilDateTime.nowTimestamp();
			
			//ͬ�⴦��
			if(Constants.APPROVED.equalsIgnoreCase(status)) {
				if(Constants.SUBMIT_INSERT.equals(type)) {//��������������ٵ�ǩ��
					GenericValue followJobGv = delegator.makeValidValue("FollowJob",  UtilMisc.toMap("followIndex", objectIndex));
					if (user.equals(ownerProcess)) {//���տγ�ͬ���״̬��Ϊ�᰸������������
						followJobGv.set("status", new Integer(Constants.FOLLOWJOB_OVER));
					} else {//�豸�γ�ͬ���״̬��Ϊδ�᰸������ʦ�ɿ�ʼ����
						followJobGv.set("status", new Integer(Constants.FOLLOWJOB_NOT_OVER));
					}
					followJobGv.set("updateTime", nowTime);
					delegator.store(followJobGv);
				} 
			} else if(Constants.REJECTED.equalsIgnoreCase(status)) {
				
			}
			
			//����WfControl
			Map submitControlMap = new HashMap();
			submitControlMap.put("submitIndex", submitIndex);
			submitControlMap.put("status", status);
			submitControlMap.put("approver", user);
			submitControlMap.put("approveTime", nowTime);
			submitControlMap.put("submitComment", comment);
			WorkflowHelper.saveSubmitControl(delegator, submitControlMap);
			
			//����submit��Ϣ
			submit.put("status", status);
			if (user.equals(ownerProcess)) {//���տγ�����
				submit.put("statusProcess", status);
			}
			submit.put("updateTime", nowTime);
			delegator.store(submit);
		} catch(Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * �쳣���Ĵ���
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map tsFormSubmit(DispatchContext ctx, Map context) {
//		GenericDelegator delegator = ctx.getDelegator();
//		Map result = new HashMap();
//		
//		GenericValue submit = (GenericValue) context.get("submit");
//		String status = (String) context.get("status");
//		String user = (String) context.get("user");
//		try {
//			
//		} catch(Exception e) {
//			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
//			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
//		}
//		
//		return result;
		GenericDelegator delegator = ctx.getDelegator();
		Map result = new HashMap();
		
		GenericValue submit = (GenericValue) context.get("submit");
		String status = (String) context.get("status");
		String user = (String) context.get("user");
		Long objectIndex = submit.getLong("objectIndex");
		String object = submit.getString("object");
		String comment = (String) context.get("comment");
		try {
			String submitIndex = submit.getString("submitIndex");
			Timestamp nowTime = new Timestamp(System.currentTimeMillis());
			
			//����WfControl
			Map submitControlMap = new HashMap();
			submitControlMap.put("submitIndex", submitIndex);
			submitControlMap.put("status", status);
			submitControlMap.put("approver", user);
			submitControlMap.put("approveTime", nowTime);
			submitControlMap.put("submitComment", comment);
			WorkflowHelper.saveSubmitControl(delegator, submitControlMap);
			
			//����submit��Ϣ
			submit.put("status", status);
			submit.put("updateTime", nowTime);
			delegator.store(submit);
			
			//���±�����״̬
			if(Constants.REJECTED.equalsIgnoreCase(status)) {
				GenericValue gv;
				Map FormMap = new HashMap();
				if (object.equals("PMFORM") || object.equals("PM_PARTS_USE") ){
					FormMap.put("pmIndex", objectIndex);
					FormMap.put("lockStatus", "");
					FormMap.put("lockUser", "");
					gv=delegator.makeValidValue("PmForm", FormMap);
				}else {
					FormMap.put("abnormalIndex", objectIndex);
					FormMap.put("lockStatus", "");
					FormMap.put("lockUser", "");
					gv=delegator.makeValidValue("AbnormalForm", FormMap);
				}
				delegator.store(gv); 
			}else if (Constants.APPROVED.equalsIgnoreCase(status)) {
				GenericValue gv = null;
				Map FormMap = new HashMap();
				if (object.equals("PMFORM") || object.equals("PM_PARTS_USE")){
					FormMap.put("pmIndex", objectIndex);
					FormMap.put("lockStatus", "LOCKED");
					gv=delegator.makeValidValue("PmForm", FormMap);
				}else if (object.equals("TSFORM") || object.equals("TS_PARTS_USE")) {
					FormMap.put("abnormalIndex", objectIndex);
					FormMap.put("lockStatus", "LOCKED");
					gv=delegator.makeValidValue("AbnormalForm", FormMap);
				}
				if (gv != null) delegator.store(gv); 
			}
		} catch(Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * ������Ŀ��ǩ��
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map flowActionItemSubmit(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		Map result = new HashMap();
		
		GenericValue submit = (GenericValue) context.get("submit");
		String status = (String) context.get("status");
		String user = (String) context.get("user");		
		String comment = (String) context.get("comment");
		
		try {
			Timestamp nowTime = UtilDateTime.nowTimestamp();
			String submitIndex = submit.getString("submitIndex");
			String type = submit.getString("type");
			Long objectIndex = submit.getLong("objectIndex");
			String owner = submit.getString("owner");
			String submitStatus = submit.getString("status");
			String ownerProcess = submit.getString("ownerProcess");
			String submitStatusProcess = submit.getString("statusProcess");
			
			if (user.equals(owner)) {//�豸�γ�����
				submitStatus = status;
				submit.put("status", status);
			}
			
			if (user.equals(ownerProcess)) {//���տγ�����
				submitStatusProcess = status;
				submit.put("statusProcess", status);
			}
			
			if (Constants.APPROVED.equalsIgnoreCase(status)) {				
				if (Constants.APPROVED.equals(submitStatus) && Constants.APPROVED.equals(submitStatusProcess)) {
					GenericValue tempValue = delegator.findByPrimaryKey("FlowActionItemTemp", UtilMisc.toMap("flowActionItemTempIndex", objectIndex));
					if (tempValue != null){
						tempValue.set("updateTime", nowTime);

						//���ݲ�ͬType���ò�ͬ�Ĵ������ִ��
						if (Constants.SUBMIT_INSERT.equals(type)) {
							//����FlowActionItem
							GenericValue gv = delegator.makeValidValue("FlowActionItem", tempValue);
							delegator.create(gv);
						} else if(Constants.SUBMIT_MODIFY.equals(type)) {
							//�޸�FlowActionItem
							GenericValue gv = delegator.makeValidValue("FlowActionItem", tempValue);
							delegator.store(gv);
						} else if(Constants.SUBMIT_DELETE.equals(type)) {
							//ɾ������
							delegator.removeByAnd("FlowActionItem", UtilMisc.toMap("itemIndex", tempValue.getLong("itemIndex")));
						}

						//��tempValue��¼��ʷ
						GenericValue histGv = delegator.makeValidValue("FlowActionItemHist", tempValue);
						histGv.set("histIndex", delegator.getNextSeqId("actionItemHistIndex"));
						delegator.create(histGv);
						//ɾ��tempValue
						delegator.removeValue(tempValue);
					}
				}
			} else if(Constants.REJECTED.equalsIgnoreCase(status)) {
				GenericValue tempValue = delegator.findByPrimaryKey("FlowActionItemTemp", UtilMisc.toMap("flowActionItemTempIndex", objectIndex));
				tempValue.set("status", new Integer(Constants.REJECTED_CODE));//�ܾ�״̬
				delegator.store(tempValue);
			}
			
			//����WfControl
			Map submitControlMap = new HashMap();
			submitControlMap.put("submitIndex", submitIndex);
			submitControlMap.put("status", status);
			submitControlMap.put("approver", user);
			submitControlMap.put("approveTime", nowTime);
			submitControlMap.put("submitComment", comment);
			WorkflowHelper.saveSubmitControl(delegator, submitControlMap);
			
			//����submit��Ϣ			
			submit.put("updateTime", nowTime);
			delegator.store(submit);
		} catch(Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * �����ƻ����������ǩ��
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map pmDelaySubmit(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		Map result = new HashMap();
		
		GenericValue submit = (GenericValue) context.get("submit");
		String status = (String) context.get("status");
		String user = (String) context.get("user");		
		String comment = (String) context.get("comment");
		
		try {
			Timestamp nowTime = UtilDateTime.nowTimestamp();
			String submitIndex = submit.getString("submitIndex");
			String type = submit.getString("type");
			Long objectIndex = submit.getLong("objectIndex");
			String owner = submit.getString("owner");
			String submitStatus = submit.getString("status");
			String ownerProcess = submit.getString("ownerProcess");
			String submitStatusProcess = submit.getString("statusProcess");
			String objectName = submit.getString("objectName");
			
			if (user.equals(owner)) {//�豸�γ�����
				submitStatus = status;
				submit.put("status", status);
			}
			
			if (user.equals(ownerProcess)) {//���տγ�����
				submitStatusProcess = status;
				submit.put("statusProcess", status);
			}
			
			if (Constants.APPROVED.equalsIgnoreCase(status)) {
				if (Constants.APPROVED.equals(submitStatus) && Constants.APPROVED.equals(submitStatusProcess)) {
					String newScheduleDateStr = getNewScheduleDateFromObjectName(objectName);
					java.sql.Date newScheduleDate = MiscUtils.toGuiDate(newScheduleDateStr, "yyyy-MM-dd");
					
					GenericValue gv = delegator.findByPrimaryKey("EquipmentSchedule", UtilMisc.toMap("scheduleIndex", objectIndex));
					if (gv != null) {
						gv.put("scheduleDate", newScheduleDate);
						gv.put("scheduleNote", "������");
						gv.put("creator", user);
						gv.put("createDate", nowTime);
						delegator.store(gv);
					}
				}
			}
			
			//����WfControl
			Map submitControlMap = new HashMap();
			submitControlMap.put("submitIndex", submitIndex);
			submitControlMap.put("status", status);
			submitControlMap.put("approver", user);
			submitControlMap.put("approveTime", nowTime);
			submitControlMap.put("submitComment", comment);
			WorkflowHelper.saveSubmitControl(delegator, submitControlMap);
			
			//����submit��Ϣ			
			submit.put("updateTime", nowTime);
			delegator.store(submit);
		} catch(Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		
		return result;
	}

	//������������ȡ���¼ƻ����ڣ�10λ�ַ�yyyy-mm-dd
	//����[CBACK01],[WEEK],[ԭ�ƻ�����2014-5-10],[�¼ƻ�����2014-06-12]��ȡ��2014-06-12
	private static String getNewScheduleDateFromObjectName(String objectName) {
		return objectName.substring(objectName.length()-11, objectName.length()-1);
	}
}
