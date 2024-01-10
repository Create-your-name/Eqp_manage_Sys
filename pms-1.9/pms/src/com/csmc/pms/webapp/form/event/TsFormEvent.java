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
import org.json.JSONArray;
import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.basic.help.BasicHelper;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.common.helper.CommonHelper;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.eqp.helper.FabAdapter;
import com.csmc.pms.webapp.eqp.helper.GuiHelper;
import com.csmc.pms.webapp.eqp.helper.PlldbHelper;
import com.csmc.pms.webapp.form.help.TsHelper;
import com.csmc.pms.webapp.parts.help.PartsHelper;
import com.csmc.pms.webapp.qufollow.help.QuFollowHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.GeneralEvents;
import com.csmc.pms.webapp.workflow.model.Job;
import com.csmc.pms.webapp.workflow.model.engine.JobEngine;
/**
 * 
 * @author shaoaj
 * @2007-9-13
 */
public class TsFormEvent extends GeneralEvents{
	public static final String module = TsFormEvent.class.getName();
	
	/**
	 * �����쳣����ѯ
	 * @param request
	 * @param response
	 * @return
	 */
	public static  String intoTsFormQuery(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String accountNo=CommonUtil.getUserNo(request);
        try {
        	List useInfoList=QuFollowHelper.getAccountSection(delegator, accountNo);
        	GenericValue gv=(GenericValue)useInfoList.get(0);
        	//��½�˵Ĳ���
        	String maintDept=gv.getString("accountDept");
        	List eqpIdList=QuFollowHelper.getEquipIDListByUseDept(delegator, maintDept);
        	
        	Calendar nowDate = Calendar.getInstance();// �õ���ǰʱ��
    		nowDate.add(Calendar.DATE, -2);
    		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    		String startDate = bartDateFormat.format(nowDate.getTime());
    		startDate = "";//Ĭ�ϲ�ѯ�����轨�����쳣��¼��dinghh081112
    		
    		Map map=new HashMap();
            map.put("startDate", startDate);
            map.put("maintDept", maintDept);
            
    		List list = TsHelper.queryTsFormList(delegator, map, "0");
        	request.setAttribute("TS_LIST", list);
        	request.setAttribute("EQPID_LIST", eqpIdList);
        	request.setAttribute("startDate", startDate);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
	
	
	/**
	 * �ύ��ѯ�쳣��
	 * @param request
	 * @param response
	 * @return
	 */
	public static String queryTsForm(HttpServletRequest request, HttpServletResponse response) {
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
        	String accountNo=CommonUtil.getUserNo(request);
        	List useInfoList=QuFollowHelper.getAccountSection(delegator, accountNo);
        	GenericValue gv=(GenericValue)useInfoList.get(0);
        	//��½�˵Ĳ���
        	String maintDept=gv.getString("accountDept");
        	map.put("maintDept", maintDept);
        	//�Կ�ʼʱ���ѯ
        	List list=TsHelper.queryTsFormList(delegator, map,"0");
        	request.setAttribute("TS_LIST", list);
        	List eqpIdList=QuFollowHelper.getEquipIDListByUseDept(delegator, maintDept);
        	request.setAttribute("EQPID_LIST", eqpIdList);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
	
	/**
	 * �����쳣���½�ҳ��
	 * @param request
	 * @param response
	 * @return
	 */
	public static String intoAddTsForm(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String eqpId=request.getParameter("parentEqpId");
        String startDate=request.getParameter("beginDate");
        String endDate=request.getParameter("eDate");
        String eqpStatus=request.getParameter("eqpStatus");
        
        Map map=new HashMap();
        map.put("eqpId", eqpId);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("eqpStatus", eqpStatus);
        
        try {
        	String accountNo = CommonUtil.getUserNo(request);
        	List useInfoList = QuFollowHelper.getAccountSection(delegator, accountNo);
        	GenericValue gv = (GenericValue)useInfoList.get(0);
			// ��½�˵Ĳ���
        	String maintDept=gv.getString("accountDept");
        	map.put("maintDept", maintDept);
        	
        	//�Դ���ʱ���ѯ
        	List eqpIdlist = TsHelper.queryTsFormList(delegator, map, "1");
        	List dealProgrammeList = TsHelper.getDealProgrammeByEqpType(delegator, eqpId);
        	request.setAttribute("TS_LIST", eqpIdlist);
        	request.setAttribute("DEALPRO_LIST", dealProgrammeList);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
	
	/**
	 * �����쳣�������Ϣ,������ɺ�,��ת����ѯҳ��
	 * @param request
	 * @param response
	 * @return
	 */
	public static String submitTsForm(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String accountNo = CommonUtil.getUserNo(request);
		Map map = TsFormEvent.getInitParams(request);
		Map parMpa = new HashMap();
		parMpa.put("createUser", accountNo);
		GenericValue userInfo = AccountHelper.getUserInfo(request, delegator);
		parMpa.put("accountName", userInfo.getString("accountName"));
		parMpa.put("formType", Constants.FORM_TYPE_NORMAL);
		parMpa.put("operType", "0");
		LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		
		try {
			GenericValue gv = delegator.findByPrimaryKey("PmAbnormalRecord",
					UtilMisc.toMap("seqIndex", map.get("seqIndex")));
			if (StringUtils.isNotEmpty(gv.getString("formIndex"))) {
				// ����ת������ֵ
				request.setAttribute("eqpId", (String) map.get("eqpId"));
				request.setAttribute("startDate", (String) map.get("startDate"));
				request.setAttribute("endDate", (String) map.get("endDate"));
				request.setAttribute("_ERROR_MESSAGE_", "�쳣������ʧ�ܣ����豸�ѽ�����Ӧ����");
				return "error";
			} else {
				TsHelper.submitTsform(delegator, dispatcher, map, parMpa);
				// ����ת������ֵ
				request.setAttribute("eqpId", (String) map.get("eqpId"));
				request.setAttribute("startDate", (String) map.get("startDate"));
				request.setAttribute("endDate", (String) map.get("endDate"));
				request.setAttribute("_EVENT_MESSAGE_", "�쳣������ɹ���");
			}
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
    }
	
	/**
	 * �����ֹ�����ҳ��
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String intoPatchTsForm(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String accountNo=CommonUtil.getUserNo(request);
		String eqpId=request.getParameter("eqpId");
	        try {
	        	List useInfoList=QuFollowHelper.getAccountSection(delegator, accountNo);
	        	GenericValue gv=(GenericValue)useInfoList.get(0);
	        	// ��½�˵Ĳ���
	        	String maintDept=gv.getString("accountDept");
	        	List eqpIdList=QuFollowHelper.getEquipIDListByUseDept(delegator, maintDept);
	        	List dealProgrammeList=TsHelper.getDealProgrammeByEqpType(delegator, eqpId);
	        	request.setAttribute("DEALPRO_LIST", dealProgrammeList);
	        	request.setAttribute("EQPID_LIST", eqpIdList);
	        } catch (Exception e) {
	            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	            return "error";
	        }
	    return "success";
	}
	
	/**
	 * �����ֹ������쳣��ҵ
	 * @param request
	 * @param response
	 * @return
	 */
	public static String submitPatchTsForm(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String accountNo=CommonUtil.getUserNo(request);
		Map map=TsFormEvent.getInitParams(request);
		Map parMpa=new HashMap();
		parMpa.put("createUser", accountNo);
		parMpa.put("formType", Constants.FORM_TYPE_PATCH);
		parMpa.put("operType", "1");
		LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		
		try {
			GenericValue userInfo = AccountHelper
					.getUserInfo(request, delegator);
			parMpa.put("accountName", userInfo.getString("accountName"));
			TsHelper.submitTsform(delegator, dispatcher, map, parMpa);
			request.setAttribute("_EVENT_MESSAGE_", "�쳣��������ҵ����ɹ���");
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
	   return "success";
	}
	
	/**
	 * ͨ���豸ID��ȡ��������б�
	 * 
	 * @param request
	 * @param response
	 */
	public static void getDealPromByEqpId(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String eqpId=request.getParameter("eqpId");
		JSONObject jsObject=new JSONObject();
		JSONArray jobNameArray=new JSONArray();
		JSONArray jobValueArray=new JSONArray();
		try {
			//��������б�
			List prodList = TsHelper.getDealProgrammeByEqpType(delegator, eqpId);
			for (int i=0;i<prodList.size();i++){
				GenericValue gv=(GenericValue)prodList.get(i);
				jobValueArray.put(gv.getString("jobIndex"));
				jobNameArray.put(gv.getString("jobName"));
			}
			jsObject.put("jobNameArray", jobNameArray);
			jsObject.put("jobIndexArray", jobValueArray);
			
			//����jsobject
			response.getWriter().write(jsObject.toString());
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}
	//----------------------------------������-----------------------------------------
	/**
	 * �������дҳ��,Ĭ�ϲ�ѯ����ļ�¼
	 * @param request
	 * @param response
	 * @return
	 */
	public static String intoAbnormalForm(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		Calendar nowDate = Calendar.getInstance();// �õ���ǰʱ��
		nowDate.add(Calendar.DATE, -2);
		SimpleDateFormat bartDateFormat =new SimpleDateFormat("yyyy-MM-dd");
		String beginDate = bartDateFormat.format(nowDate.getTime());
		beginDate = "";//��ʾȫ��δ��ɵ�PM��
		
		try {
		 	String accountNo=CommonUtil.getUserNo(request);
	    	List useInfoList=QuFollowHelper.getAccountSection(delegator, accountNo);
	    	GenericValue gv=(GenericValue)useInfoList.get(0);
	    	// ��½�˵Ĳ���
			String maintDept=gv.getString("accountDept");
		 	List abnormalList=TsHelper.queryAbnormalFormList(delegator, beginDate, "", maintDept);
		 	request.setAttribute("ABNORMALFORM_LIST", abnormalList);
		 	request.setAttribute("startDate", beginDate);
		} catch (Exception e) {
		    request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		    return "error";
		}
	   return "success";
	}
	
	/**
	 * ��ѯ�쳣��
	 * @param request
	 * @param response
	 * @return
	 */
	public static String queryAbnormalFormList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String startDate=request.getParameter("startDate");
		String endDate=request.getParameter("endDate");
		 try {
			 	String accountNo=CommonUtil.getUserNo(request);
	        	List useInfoList=QuFollowHelper.getAccountSection(delegator, accountNo);
	        	GenericValue gv=(GenericValue)useInfoList.get(0);
	        	// ��½�˵Ĳ���
	        	String maintDept=gv.getString("accountDept");
			 	List abnormalList=TsHelper.queryAbnormalFormList(delegator, startDate, endDate, maintDept);
			 	request.setAttribute("ABNORMALFORM_LIST", abnormalList);
			 	request.setAttribute("startDate", startDate);
			 	request.setAttribute("endDate", endDate);
	        } catch (Exception e) {
	            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	            return "error";
	        }
	   return "success";
	}
	
	/**
	 * �������ѯҳ��,Ĭ�ϲ�ѯ����ļ�¼
	 * @param request
	 * @param response
	 * @return
	 */
	public static String intoQueryAbnormalForm(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		Calendar nowDate = Calendar.getInstance();// �õ���ǰʱ��
		nowDate.add(Calendar.DATE, -2);
		SimpleDateFormat bartDateFormat =new SimpleDateFormat("yyyy-MM-dd");
		String beginDate=bartDateFormat.format(nowDate.getTime());
		 try {
	        	GenericValue userInfo = AccountHelper.getUserInfo(request,delegator);
                //��½�˵ĿƱ�
                String section = userInfo.getString("accountSection");
//              ��½�˵Ĳ���
                String maintDept = userInfo.getString("accountDept");
	        	
	        	Map parm=new HashMap();
	        	parm.put("eqpId", "");
	        	parm.put("startDate", beginDate);
	        	parm.put("endDate", "");
	        	parm.put("maintDept", maintDept);
	        	
			 	List abnormalList=TsHelper.queryAbnormalFormByCondition(delegator,parm);
			 	List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
			 	
			 	request.setAttribute("ABNORMALFORM_LIST", abnormalList);
			 	request.setAttribute("startDate", beginDate);
			 	request.setAttribute("section", section);
			 	request.setAttribute("maintDept", maintDept);
                request.setAttribute("deptList", deptList);	
	        } catch (Exception e) {
	            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	            return "error";
	        }
	   return "success";
	}
	
	/**
	 * ����������ѯ�쳣��(����ɱ���ѯҳ��)
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String queryAbnormalFormByCondition(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String eqpId = request.getParameter("eqpId");
		String maintDept = request.getParameter("maintDept");
		
		try {
			Map parm = new HashMap();
			parm.put("eqpId", eqpId);
			parm.put("startDate", startDate);
			parm.put("endDate", endDate);
			parm.put("maintDept", maintDept);

			List abnormalList = TsHelper.queryAbnormalFormByCondition(delegator, parm);
			List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));

			request.setAttribute("ABNORMALFORM_LIST", abnormalList);
			request.setAttribute("startDate", startDate);
			request.setAttribute("endDate", endDate);
			request.setAttribute("eqpId", eqpId);
			request.setAttribute("maintDept", maintDept);
            request.setAttribute("deptList", deptList);

		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	/**
	 * �����쳣������ҳ��
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String intoAbnormalFormManage1(HttpServletRequest request,HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String index=request.getParameter("abnormalIndex");
		String eqpId=request.getParameter("eqpId");
		String functionType=request.getParameter("functionType");
		String result="";
		
		try {
//			String accountNo = CommonUtil.getUserNo(request);
//			List useInfoList = QuFollowHelper.getAccountSection(delegator,accountNo);
//			GenericValue gv = (GenericValue) useInfoList.get(0);
//			//��½�˵ĿƱ�
//			String section = gv.getString("accountSection");
//			//����
//			String accountDept = gv.getString("accountDept");
			GenericValue abnormal=delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex",index));
			
            //�õ����źͿα�
            String user = abnormal.getString("createUser");                  
            GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));       
            //�α�
            String section = account.getString("accountSection");
            //����
            String accountDept = account.getString("accountDept");
			
			
			if("3".equals(functionType)){
				List partentFormList=TsHelper.getParentFormList(delegator, index);
				if(partentFormList!=null&&partentFormList.size()>0){
					GenericValue parentForm=(GenericValue)partentFormList.get(0);
					abnormal=delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex",parentForm.getString("parentIndex")));
				}
			}
			request.setAttribute("ABNORMAL", abnormal);
			
			result="start";
			//��ʼ�ı�
			if(!String.valueOf(Constants.CREAT).equals(abnormal.getString("status"))){
				//����abnormal�����صĴ������
				List list=new ArrayList();
				String jobRelationIndex="";
				list=delegator.findByAnd("FormJobRelation", UtilMisc.toMap("eventType",Constants.TS,"eventIndex",abnormal.getString("abnormalIndex")));
				if(list.size()>0){
					GenericValue job=(GenericValue)list.get(0);
					jobRelationIndex=job.getString("seqIndex");
				}
				List eventCategory = BasicHelper.getEventCategoryList(delegator);
				List reasonList=TsHelper.getReasonList(delegator, eqpId, Constants.ABNORMAL);
				List eqpStatus=TsHelper.getEqpStatusList(delegator, Constants.TS);
				List partsUseList=PartsHelper.getPartsList(delegator,abnormal.getString("abnormalIndex"),Constants.TS);
				request.setAttribute("EQP_STATUS_LIST", eqpStatus);
				request.setAttribute("EVENT_CATEGORY_LIST", eventCategory);
				request.setAttribute("PARTS_USE_LIST", partsUseList);
				request.setAttribute("REASON_List", reasonList);
				request.setAttribute("JOB_RELATION_INDEX", jobRelationIndex);
				
				//���hold�룬holdԭ�� 
                if (Constants.CALL_TP_FLAG) {
                	List holdCodeReasonList = GuiHelper.getHoldCodeReasonList(accountDept);
                	request.setAttribute("holdCodeReasonList", holdCodeReasonList);    	            
                }				
				
				//functionType=3:�����쳣���鿴ҳ��
				//functionType=1�������쳣������ҳ��
				if("3".equals(functionType)){
					result="formview";
					//�ӱ���ѯ
					abnormal.put("abnormalIndex", index);
					abnormal.put("equipmentId", eqpId);
				}else if("1".equals(functionType)){
					result="manager";
				}
			}
			request.setAttribute("SECTION", section);
			request.setAttribute("ACCOUNT_DEPT", accountDept);
			request.setAttribute("activate", request.getParameter("activate")==null?"":request.getParameter("activate"));
			
			List quFollowJobList = QuFollowHelper.queryQuFollowJobList(delegator, UtilMisc.toMap("eventType", "TS", "eventIndex", index));
            request.setAttribute("FOLLOWJOB_LIST", quFollowJobList);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return result;
	}
	
	
	/**
	 * �쳣ԭ�������쳣��ҳ��hold�Զ�����
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String  intoAbnormalFormManage(HttpServletRequest request,HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String index=request.getParameter("abnormalIndex");
		String eqpId=request.getParameter("eqpId");
		String functionType=request.getParameter("functionType");
		String reasonIndex=request.getParameter("reasonIndex");
	    String reasonIndex1 = (String) request.getAttribute("abnormalReason");
		String result="";
		
		try {
			String accountNo = CommonUtil.getUserNo(request);
//			List useInfoList = QuFollowHelper.getAccountSection(delegator,accountNo);
//			GenericValue gv = (GenericValue) useInfoList.get(0);
//			//��½�˵ĿƱ�
//			String section = gv.getString("accountSection");
//			//����
//			String accountDept = gv.getString("accountDept");
			GenericValue abnormal = delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex", index));
			if (StringUtils.isEmpty(eqpId)) {
				eqpId = abnormal.getString("equipmentId");
			}
			request.setAttribute("userId", accountNo);
            //�õ����źͿα�
            String user = abnormal.getString("createUser");                  
            GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));       
            //�α�
            String section = account.getString("accountSection");
            //����
            String accountDept = account.getString("accountDept");
			
            //�쳣ԭ��
            String abnormalReasonIndex = abnormal.getString("abnormalReasonIndex");
            
            if(reasonIndex!="" && reasonIndex != null )
            {
            	abnormalReasonIndex=reasonIndex;
            }
            if(abnormalReasonIndex!="" && abnormalReasonIndex!=null){
            GenericValue pmsReason = delegator.findByPrimaryKey("PmsReason", UtilMisc.toMap("reasonIndex", abnormalReasonIndex));
        	
        	request.setAttribute("pmsReason", pmsReason);
            }
            
			if("3".equals(functionType)){
				List partentFormList=TsHelper.getParentFormList(delegator, index);
				if(partentFormList!=null&&partentFormList.size()>0){
					GenericValue parentForm=(GenericValue)partentFormList.get(0);
					abnormal=delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex",parentForm.getString("parentIndex")));
				}
			}
			request.setAttribute("ABNORMAL", abnormal);
			
			// ��������
			request.setAttribute("partTypeList", CommonHelper.getPartTypeList(delegator));
            
			result="start";
			//��ʼ�ı�
			if(!String.valueOf(Constants.CREAT).equals(abnormal.getString("status"))){
				//����abnormal�����صĴ������
				List list=new ArrayList();
				String jobRelationIndex="";
				list=delegator.findByAnd("FormJobRelation", UtilMisc.toMap("eventType",Constants.TS,"eventIndex",abnormal.getString("abnormalIndex")));
				if(list.size()>0){
					GenericValue job=(GenericValue)list.get(0);
					jobRelationIndex=job.getString("seqIndex");
				}
				List eventCategory = BasicHelper.getEventCategoryList(delegator);
				List reasonList=TsHelper.getReasonList(delegator, eqpId,Constants.ABNORMAL);
				List eqpStatus=TsHelper.getEqpStatusList(delegator, Constants.TS);
				
				// ��������ʹ���б�
				List partsUseList = PartsHelper.getPartsUseList(delegator, eqpId, index, Constants.TS, user);
				request.setAttribute("PARTS_USE_LIST", partsUseList);

				String deptIndex = AccountHelper.getUserDeptIndex(delegator, user);
				List keyPartsUseList = PartsHelper.getMcsPartsNoList_other(delegator, eqpId, index, deptIndex);// modified
				request.setAttribute("keyPartsUseList", keyPartsUseList);
				
				request.setAttribute("EQP_STATUS_LIST", eqpStatus);
				request.setAttribute("EVENT_CATEGORY_LIST", eventCategory);
				request.setAttribute("REASON_List", reasonList);
				request.setAttribute("JOB_RELATION_INDEX", jobRelationIndex);
				
				//���hold�룬holdԭ�� 
                if (Constants.CALL_TP_FLAG) {
                	List holdCodeReasonList = GuiHelper.getHoldCodeReasonList(accountDept);
                	request.setAttribute("holdCodeReasonList", holdCodeReasonList);    	            
                }				
				
				//functionType=3:�����쳣���鿴ҳ��
				//functionType=1�������쳣������ҳ��
				if("3".equals(functionType)){
					result="formview";
					//�ӱ���ѯ
					abnormal.put("abnormalIndex", index);
					abnormal.put("equipmentId", eqpId);
				}else if("1".equals(functionType)){
					result="manager";
				}
			}
			request.setAttribute("SECTION", section);
			request.setAttribute("ACCOUNT_DEPT", accountDept);
			request.setAttribute("activate", request.getParameter("activate")==null?"":request.getParameter("activate"));
		
			 
			List quFollowJobList = QuFollowHelper.queryQuFollowJobList(delegator, UtilMisc.toMap("eventType", "TS", "eventIndex", index));
            request.setAttribute("FOLLOWJOB_LIST", quFollowJobList);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return result;
	}
	
	
	/**
	 * �����쳣������ҳ��
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String newIntoAbnormalFormManage(HttpServletRequest request,HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String index=request.getParameter("abnormalIndex");
		String eqpId=request.getParameter("eqpId");
		String functionType=request.getParameter("functionType");
		String result="";
		try {
//			String accountNo = CommonUtil.getUserNo(request);
//			List useInfoList = QuFollowHelper.getAccountSection(delegator,accountNo);
//			GenericValue gv = (GenericValue) useInfoList.get(0);
//			//��½�˵ĿƱ�
//			String section = gv.getString("accountSection");
//			//����
//			String accountDept = gv.getString("accountDept");
			GenericValue abnormal=delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex",index));
			
            //�õ����źͿα�
            String user = abnormal.getString("createUser");                  
            GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));       
            //�α�
            String section = account.getString("accountSection");
            //����
            String accountDept = account.getString("accountDept");
			
			if("3".equals(functionType)){
				List partentFormList=TsHelper.getParentFormList(delegator, index);
				if(partentFormList!=null&&partentFormList.size()>0){
					GenericValue parentForm=(GenericValue)partentFormList.get(0);
					abnormal=delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex",parentForm.getString("parentIndex")));
				}
			}
			request.setAttribute("ABNORMAL", abnormal);
			result="start";
			//��ʼ�ı�
			if(!String.valueOf(Constants.CREAT).equals(abnormal.getString("status"))){
				//����abnormal�����صĴ������
				List list=new ArrayList();
				String jobRelationIndex="";
				list=delegator.findByAnd("FormJobRelation", UtilMisc.toMap("eventType",Constants.TS,"eventIndex",abnormal.getString("abnormalIndex")));
				if(list.size()>0){
					GenericValue job=(GenericValue)list.get(0);
					jobRelationIndex=job.getString("seqIndex");
				}
				List eventCategory = BasicHelper.getEventCategoryList(delegator);
				List reasonList=TsHelper.getReasonList(delegator, eqpId, Constants.ABNORMAL);
				List eqpStatus=TsHelper.getEqpStatusList(delegator, Constants.TS);
				List partsUseList=PartsHelper.getPartsList(delegator,abnormal.getString("abnormalIndex"),Constants.TS);
				request.setAttribute("EQP_STATUS_LIST", eqpStatus);
				request.setAttribute("EVENT_CATEGORY_LIST", eventCategory);
				request.setAttribute("PARTS_USE_LIST", partsUseList);
				request.setAttribute("REASON_List", reasonList);
				request.setAttribute("JOB_RELATION_INDEX", jobRelationIndex);
				
	        	//huanghp,20081008,
				if (jobRelationIndex != null && jobRelationIndex.length() > 0)
				{
		        	JobEngine jobEngine = JobEngine.create();
					jobEngine.setDelegator(delegator);
					jobEngine.setJobRelationIndex(jobRelationIndex);
					Job job = jobEngine.getViewJobFromEntity();
					request.setAttribute("singleJob", job);
				}
				
				
				//functionType=3:�����쳣���鿴ҳ��
				//functionType=1�������쳣������ҳ��
				if("3".equals(functionType)){
					result="formview";
					//�ӱ���ѯ
					abnormal.put("abnormalIndex", index);
					abnormal.put("equipmentId", eqpId);
				}else if("1".equals(functionType)){
					result="manager";
				}
			}
			request.setAttribute("SECTION", section);
			request.setAttribute("ACCOUNT_DEPT", accountDept);
			request.setAttribute("activate", request.getParameter("activate")==null?"":request.getParameter("activate"));
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return result;
	}
	
	/**
	 * ͨ���¼������ȡ�¼�ϸ��
	 * @param request
	 * @param response
	 */
	public static void getEventSubCategoryList(HttpServletRequest request,HttpServletResponse response){
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 String eventIndex = request.getParameter("eventIndex");
		 JSONArray eventSubJson = new JSONArray();
		 try {
			 //��ѯ����¼�ϸ��
			 JSONObject blank = new JSONObject(UtilMisc.toMap("eventSubIndex", "", "subCategory", ""));
			 eventSubJson.put(blank);
			 List eventSubList = delegator.findByAnd("EventSubcategory", UtilMisc.toMap("eventIndex",eventIndex));
			 for(Iterator it = eventSubList.iterator(); it.hasNext();) {
				 GenericValue gv = (GenericValue)it.next();
				 JSONObject object = new JSONObject();
				 object.put("eventSubIndex", gv.getString("eventSubIndex"));
				 object.put("subCategory", gv.getString("subCategory"));
				 eventSubJson.put(object);
			 }
			 response.getWriter().write(eventSubJson.toString());
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
		 }
	}
	
	/**
	 * ��ʼ�쳣��
	 * 03-DOWN״̬��Ҫ��Ϊ03-REP(У�鲿���������)
	 * 05���޸��豸״̬
	 * @param request
	 * @param response
	 * @return
	 */
	public static String startAbnormalForm(HttpServletRequest request,HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String index = request.getParameter("abnormalIndex");
		String formType = request.getParameter("formType");
		String eqpStatus = request.getParameter("eqpStatus");

		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String eqpId = request.getParameter("eqpId");
		Map map = new HashMap();
		map.put("abnormalIndex", index);
		map.put("status", String.valueOf(Constants.START));
		String accountNo = CommonUtil.getUserNo(request);
		map.put("startUser", accountNo);
		map.put("startTime", new Timestamp(System.currentTimeMillis()));
		map.put("updateTime", new Timestamp(System.currentTimeMillis()));
		
		try {
			Map statusMap = new HashMap();
			statusMap.put("eqpid", eqpId);
			statusMap.put("newstatus", Constants.TS_START_STATUS);
			statusMap.put("comment", "PMS");
			String childMsg="";
			
			//�ֹ�������ҵ����Ҫ�޸��豸״̬
			//05-PR����Ҫ�޸��豸״̬
			if(Constants.FORM_TYPE_NORMAL.equals(formType) && !TsHelper.isPRStartStatus(eqpStatus) && !PlldbHelper.isFab5Eqp(delegator, eqpId)){
				Map ret=new HashMap();
				// �����豸״̬��������
				try{
					ret = FabAdapter.runCallService(delegator, userLogin,
							statusMap, com.csmc.pms.webapp.util.Constants.EQP_STATUS_CHANGE);
					if (ModelService.RESPOND_ERROR.equals(ret.get(ModelService.RESPONSE_MESSAGE))) {
						request.setAttribute("_ERROR_MESSAGE_", (String) ret.get(ModelService.ERROR_MESSAGE));
						Debug.logError((String) ret.get(ModelService.ERROR_MESSAGE), module);
					}
					
					List sonFormList=TsHelper.getSonFormList(delegator, index);
					//�޸����豸���豸״̬
					if(sonFormList!=null&&sonFormList.size()>0){
						for(int k=0;k<sonFormList.size();k++){
							GenericValue sonForm=(GenericValue)sonFormList.get(k);
							GenericValue form=delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex",sonForm.getString("sonIndex")));
							statusMap.put("eqpid", form.getString("equipmentId"));	
							// �����豸״̬
							ret = FabAdapter.runCallService(delegator, userLogin,
									statusMap, com.csmc.pms.webapp.util.Constants.EQP_STATUS_CHANGE);
							childMsg=childMsg+form.getString("equipmentId")+";";
							
							if (ModelService.RESPOND_ERROR.equals(ret.get(ModelService.RESPONSE_MESSAGE))) {
								request.setAttribute("_ERROR_MESSAGE_", (String) ret.get(ModelService.ERROR_MESSAGE));
								Debug.logError((String) ret.get(ModelService.ERROR_MESSAGE), module);
							}
						}
					}
				}catch (Exception e) {
					request.setAttribute("_ERROR_MESSAGE_", "�޸����豸״̬����: " + e.getMessage());
					Debug.logError(e,module);
					if(StringUtils.isNotEmpty(childMsg))
						Debug.logInfo(childMsg, module);
					return "error";
				}
			}
			
			GenericValue gv = delegator.makeValidValue("AbnormalForm", map);
			delegator.store(gv);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	
	/**
	 * �쳣���ݴ�
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String tempSaveAbnormalForm(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String abnormalIndex = request.getParameter("abnormalIndex");
		String formType = request.getParameter("formType");
		String abnormalTime = request.getParameter("abnormalTime");
		String member = request.getParameter("member");
		String abnormalText = request.getParameter("abnormalText");
		String reworkNote = request.getParameter("reworkNote");
		String eventSubIndex = request.getParameter("eventSubIndex");
		String eventSubName = request.getParameter("eventSubName");
		String event = request.getParameter("event");
		String eventName = request.getParameter("eventName");
		String errorCode = request.getParameter("errorCode");
		String anormalReason = request.getParameter("anormalReason");
		String anormalReasonName = request.getParameter("anormalReasonName");
		String jobText = request.getParameter("jobText");
		String relAbnormalReason = request.getParameter("relAbnormalReason");
		String eqpId = request.getParameter("eqpId");
		String userNo = CommonUtil.getUserNo(request);
		String eventIndex = request.getParameter("eventIndex");
		String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);

		Map map = TsFormEvent.getInitParams(request, false, false);
		// ���ݹؼ��֣�eqpid���ж��Ƿ�����ά����һ���Ļ���ԭ��
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			if (key.startsWith("parts_")) {
				String orderNum = key.substring(key.indexOf("_") + 1);// ���index
				String d_keydesc = (String) map.get("keydesc_" + orderNum);
				String d_offline = (String) map.get("offline_" + orderNum);
				String d_seriesno = (String) map.get("seriesno_" + orderNum);
				String d_newparttype = (String) map.get("newparts_type" + orderNum);
				if (!d_newparttype.equals("DELAY")) {
					if (d_offline == "null" || StringUtils.isEmpty(d_offline)) {
						// ִ�в�ѯ��� keydesc,eqpid,status
						StringBuffer sQueryPartsCount = new StringBuffer("");
						sQueryPartsCount.append(
								"  select t1.* from key_parts_use t1,  parts_use t2,key_eqp_parts t3  where t1.parts_use_id =t2.seq_index ");
						sQueryPartsCount.append("   and t1.key_parts_id=t3.key_parts_id   and t2.event_index <> '")
								.append(abnormalIndex).append("' ");
						sQueryPartsCount.append("    and t1.eqp_id = '").append(eqpId)
								.append("'  and t1.status='USING' ");
						sQueryPartsCount.append("    and keydesc='").append(d_keydesc).append("'");
						try {
							List partsUseList = SQLProcess.excuteSQLQuery(sQueryPartsCount.toString(), delegator);
							int size = partsUseList.size();
							if (size > 0) {
								// String delaytime=partsUseList.get();
								// map.put("delaytime",delaytime);
								request.setAttribute("_ERROR_MESSAGE_", d_keydesc + "֮ǰ���ڴ��豸��ʹ�ù��������뻻��ԭ��");
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

		map.put("abnormalIndex", abnormalIndex);
		map.put("formType", formType);
		map.put("status", String.valueOf(Constants.HOLD));
		if (abnormalTime.indexOf(":") < 0) {
			abnormalTime = abnormalTime + " 00:00:00";
		}
		map.put("abnormalTime", Timestamp.valueOf(abnormalTime));
		// ����쳣ʱ��Ϊ�գ�Ĭ��Ϊ��ǰʱ��
		Calendar nowDate = Calendar.getInstance();// �õ���ǰʱ��
		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String returnTime = bartDateFormat.format(nowDate.getTime());
		map.put("returnTime", Timestamp.valueOf(returnTime));
		map.put("member", member);
		map.put("abnormalText", abnormalText);
		map.put("reworkNote", reworkNote);
		map.put("subeventIndex", eventSubIndex);
		map.put("subeventCategory", eventSubName);
		map.put("eventIndex", event);
		map.put("eventCategory", eventName);
		map.put("errorCode", errorCode);
		map.put("abnormalReasonIndex", anormalReason);
		map.put("abnormalReason", anormalReasonName);
		map.put("jobText", jobText);
		map.put("relAbnormalReason", relAbnormalReason);
		map.put("updateTime", new Timestamp(System.currentTimeMillis()));
		try {
			GenericValue gv = delegator.makeValidValue("AbnormalForm", map);
			request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
			delegator.store(gv);

			map.put("transBy", userNo);
			map.put("deptIndex", deptIndex);
			map.put("eventType", Constants.TS);
			map.put("status", "USING");
			map.put("eqpId", eqpId);
			map.put("eventIndex", abnormalIndex);
			StringBuffer rSB = new StringBuffer();
			PartsHelper.savePartsUseInfo(delegator, map, rSB);
			if (rSB.length() > 0) {
				request.setAttribute("_ERROR_MESSAGE_", rSB.toString());
				request.setAttribute("flag", "N");
			} else {
				request.setAttribute("_EVENT_MESSAGE_", "�ݴ�ɹ���");
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
	 * ������
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String overAbnormalForm(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String eqpId = request.getParameter("eqpId");
		String abnormalIndex = request.getParameter("abnormalIndex");
		String formType = request.getParameter("formType");
		String abnormalTime = request.getParameter("abnormalTime");
		String member = request.getParameter("member");
		String abnormalText = request.getParameter("abnormalText");
		String reworkNote = request.getParameter("reworkNote");
		String eventSubIndex = request.getParameter("eventSubIndex");
		String eventSubName = request.getParameter("eventSubName");
		String event = request.getParameter("event");
		String eventName = request.getParameter("eventName");
		String errorCode = request.getParameter("errorCode");
		String anormalReason = request.getParameter("anormalReason");
		String anormalReasonName = request.getParameter("anormalReasonName");
		String jobText = request.getParameter("jobText");
		String relAbnormalReason = request.getParameter("relAbnormalReason");
		String accountNo = CommonUtil.getUserNo(request);

		Map map = new HashMap();
		map.put("abnormalIndex", abnormalIndex);
		map.put("formType", formType);
		if (abnormalTime.indexOf(":") < 0) {
			abnormalTime = abnormalTime + " 00:00:00";
		}
		map.put("abnormalTime", Timestamp.valueOf(abnormalTime));
		Calendar nowDate = Calendar.getInstance();// �õ���ǰʱ��
		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String returnTime = bartDateFormat.format(nowDate.getTime());
		map.put("returnTime", Timestamp.valueOf(returnTime));
		map.put("member", member);
		map.put("abnormalText", abnormalText);
		map.put("reworkNote", reworkNote);
		map.put("subeventIndex", eventSubIndex);
		map.put("subeventCategory", eventSubName);
		map.put("eventIndex", event);
		map.put("eventCategory", eventName);
		map.put("errorCode", errorCode);
		map.put("abnormalReasonIndex", anormalReason);
		map.put("abnormalReason", anormalReasonName);
		map.put("jobText", jobText);
		map.put("relAbnormalReason", relAbnormalReason);

		try {
			// �鿴���쳣�ڼ��Ƿ���δ�����ı�����
			List equipmentScheduleList = TsHelper.getTsUnPmScheduleList(delegator, eqpId, abnormalTime);
			// ���û���ˣ�����쳣
			// ������ڣ���תҳ�棬ѡ��������
			if (equipmentScheduleList.size() > 0) {
				request.setAttribute("eqpId", eqpId);
				request.setAttribute("equipmentScheduleList", equipmentScheduleList);
				request.setAttribute("_EVENT_MESSAGE_", "�����쳣��ǰ�������Ƚ���������쳣�ڼ�ı���");
				return "doPm";
			}

			// ���±������
			List partsUseList = delegator.findByAnd("PartsUse",
					UtilMisc.toMap("eventIndex", abnormalIndex, "eventType", Constants.TS));
			StringBuffer errorString = new StringBuffer();
			List updateList = new ArrayList();
			for (int i = 0; i < partsUseList.size(); i++) {
				Map parts = (Map) partsUseList.get(i);
				String partNo = (String) parts.get("partNo");
				String mtrGrp = (String) parts.get("mtrGrp");
				String deptIndex = parts.get("deptIndex").toString();
				String partType = (String) parts.get("partType");
				String reqIndex = String.valueOf(parts.get("partsRequireIndex"));
				long usedCount = Long.valueOf((String) parts.get("partCount")).longValue();
				Timestamp nowTime = UtilDateTime.nowTimestamp();
				if (partType.equals("NEW") && !"null".equals(reqIndex)) {
					GenericValue gv = delegator.findByPrimaryKey("McsMaterialStoReq",
							UtilMisc.toMap("materialStoReqIndex", reqIndex));
					long reqQty = gv.getLong("qty").longValue();
					long activeQty = gv.getLong("activeQty").longValue();
					long stockQty = reqQty - activeQty;
					if (stockQty > 0 && stockQty >= usedCount) {
						gv.put("materialStoReqIndex", reqIndex);
						if (stockQty == usedCount) {gv.put("activeFlag", Constants.Y);}
						gv.put("activeTime", UtilDateTime.nowTimestamp());
						gv.put("activeQty", Long.valueOf(activeQty + usedCount));
						gv.put("eventIndex", abnormalIndex);
						updateList.add(gv);
						
						// ��������״̬
						// ��������״̬����Ϊ FINISH
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
						
						// ��ѯmaterialIndex
					    GenericValue materialInfo = CommonUtil.findFirstRecordByAnd(
								delegator, "McsMaterialInfo", 
								UtilMisc.toMap("mtrNum", partNo, "deptIndex", deptIndex));
						String materialIndex = materialInfo.getString("materialIndex");
						
						// ������������״̬
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
					    
					    // 3.��Ƭ��¼��ʷ
					    //3.1��¼ʹ��
					    Long mtrStatusHistId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX);
					    mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX, mtrStatusHistId);
					    
					    mtrStatusMap.put("status", ConstantsMcs.USING);
					    mtrStatusMap.put("usingObjectId", eqpId);				    
//					    mtrStatusMap.put("note", useNote);
					    GenericValue mtrStatusHistGv = delegator.makeValidValue("McsMaterialStatusHist", mtrStatusMap);
					    updateList.add(mtrStatusHistGv);
					    
						continue;
					}
					// ��治��
					errorString.append(mtrGrp).append("������:").append(partNo).append(" ָ������: ")
							.append(gv.get("batchNum").toString()).append(" �Ŀ�治��! \n");
				}
			}

			if (errorString.length() > 0) {
				request.setAttribute("_ERROR_MESSAGE_", errorString.toString());
				return "error";
			}

			delegator.storeAll(updateList);

		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String returnStr = "";
		LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		try {
			map.put("status", String.valueOf(Constants.OVER));
			map.put("endUser", accountNo);
			map.put("endTime", new Timestamp(System.currentTimeMillis()));
			map.put("updateTime", new Timestamp(System.currentTimeMillis()));
			map.put("lockStatus", null);
			map.put("lockUser", null);

			// page param map
			Map parmMap = getInitParams(request, true, true);
			parmMap.put("userLogin", userLogin);

			// �豸״̬���ֵ�ɻ��水ťѡ��
			parmMap.put("eqpStatusChangeTo", request.getParameter("eqpStatusChangeTo"));

			Map result = dispatcher.runSync("overAbnormalFormAction",
					UtilMisc.toMap("abnormalFormMap", map, "parmMap", parmMap));
			if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
				request.setAttribute("_ERROR_MESSAGE_", (String) result.get(ModelService.ERROR_MESSAGE));
				return "error";
			} else {
				returnStr = (String) result.get("returnMsg");
			}
			request.setAttribute("_EVENT_MESSAGE_", returnStr);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	/**
	 * ɾ���ֹ�����ı�
	 * @param request
	 * @param response
	 * @return
	 */
	public static String delAbnormalForm(HttpServletRequest request,HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String abnormalIndex=request.getParameter("abnormalIndex");
		try {
			GenericValue gv=delegator.findByPrimaryKey("AbnormalForm", UtilMisc.toMap("abnormalIndex", abnormalIndex));
	    	delegator.removeValue(gv);
	    	request.setAttribute("_EVENT_MESSAGE_", "���ѳɹ�ɾ��!");
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	//--------------------------------------PARTS�趨---------------------------------	
	/**
	 * �������ϲ�ѯҳ��
	 * @param request
	 * @param response
	 * @return
	 */
	public static String intoPartsQuery(HttpServletRequest request,HttpServletResponse response) {
		return PmFormEvent.intoPartsQuery(request, response);
	}
	
	/**
	 * PARTS��ѯ
	 * @param request
	 * @param response
	 * @return
	 */
	public static String queryPartsList(HttpServletRequest request,HttpServletResponse response) {
		return PmFormEvent.queryPartsList(request, response);
	}
	
	/**
	 * PARTS����
	 * @param request
	 * @param response
	 * @return
	 */
	public static String updatePartsUse(HttpServletRequest request,HttpServletResponse response) {
		return PmFormEvent.updatePartsUse(request, response);
	} 
	
	/**
	 * ����PARTS
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String savePartsUseInfo(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		Map map = GeneralEvents.getInitParams(request);
		String userNo = CommonUtil.getUserNo(request);
		String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);
		map.put("transBy", userNo);
		map.put("deptIndex", deptIndex);
		map.put("eventType", Constants.TS);

		try {
			StringBuffer rSB = new StringBuffer();
			PartsHelper.savePartsUseInfo(delegator, map, rSB);
			if (rSB.length() > 0) {
				request.setAttribute("_ERROR_MESSAGE_", rSB.toString());
				request.setAttribute("flag", "N");
			} else {
				request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
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
	 * ɾ��������Ϣ
	 * @param request
	 * @param response
	 * @return
	 */
	public static String delPartsUse(HttpServletRequest request,HttpServletResponse response) {
		return PmFormEvent.delPartsUse(request, response);
	}
    //	--------------------------------------end PARTS�趨---------------------------------
	
	/**
	 * �����豸״̬
	 * @param request
	 * @param response
	 */
	public static void updateEqpState(HttpServletRequest request,HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		HttpSession session=request.getSession();
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		String eqpId=request.getParameter("eqpId");
		String eqpState=request.getParameter("eqpState");
		String abnormalIndex=request.getParameter("abnormalIndex");
		
		Map statusMap = new HashMap();
		statusMap.put("eqpid", eqpId);
		statusMap.put("newstatus", eqpState);
		statusMap.put("comment", "PMS");
		
		JSONObject result = CommonHelper.updateN05EqpStatus(delegator, userLogin, Constants.TS, abnormalIndex, statusMap);
		
		try{
			response.getWriter().write(result.toString());
		} catch (IOException io) {
			Debug.logError(io.toString(),module);
		}
	}

	/**
	 * ��ѯ�豸״̬
	 * @param request
	 * @param response
	 */
	public static void queryEqpState(HttpServletRequest request,HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		HttpSession session=request.getSession();
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		String eqpId=request.getParameter("eqpId");
		JSONObject result = new JSONObject();
		try { 
			Map ret = FabAdapter.runCallService(delegator, userLogin,
					UtilMisc.toMap("eqpid", eqpId), com.csmc.pms.webapp.util.Constants.EQP_INFO_QUERY);
			String eqpStatus = (String)ret.get("status");
			if (ModelService.RESPOND_ERROR.equals(ret.get(ModelService.RESPONSE_MESSAGE))) {
				result.put("status", "error");
				result.put("message", (String) ret.get(ModelService.ERROR_MESSAGE));
				response.getWriter().write(result.toString());
			}else{
				result.put("status", "success");
				result.put("eqpStatus", eqpStatus);
				response.getWriter().write(result.toString());
			}
		}catch (Exception e) {
			result.put("status", "error");
			result.put("message", "��ѯ�豸״̬����,����ϵ����Ա:"+e.getMessage());
			try{
			response.getWriter().write(result.toString());
			}catch (IOException io) {
			}
		}
	}
	
	/**
     * ����relationIndex��ѯJOB״̬
     * 
     * @param request
     * @param response
     */
    public static void getJObStatus(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String id = request.getParameter("relationIndex");
        try {
            GenericValue gv = delegator.findByPrimaryKey("FormJobRelation", UtilMisc.toMap("seqIndex", id));
            JSONObject pcStyle = new JSONObject();
            pcStyle.put("jobStatus", gv.getString("jobStatus"));
            response.getWriter().write(pcStyle.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }
    
    /**
     * ��ѯδ�����쳣
     * @param request
     * @param response
     */
    public static String queryUndoTsformlist(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String eqpId = request.getParameter("eqpId");
        String maintDept = request.getParameter("maintDept");
        try {
            if(maintDept == null || "".equals(maintDept)){
                maintDept = AccountHelper.getUserInfo(request, delegator).getString("accountDept");
            }
            List list = TsHelper.queryUndoAbnormalFormlist(delegator, eqpId, maintDept);
            request.setAttribute("maintDept", maintDept);
            request.setAttribute("undoTsList", list);
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
    
    /**
     * �쵼�޸��쳣��(ֻ�ı����Ϣ��ʱ���״̬���ı�)
     * @param request
     * @param response
     * @return
     */
    public static String editTsFormByLeader(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
    	String userNo = CommonUtil.getUserNo(request);
		String abnormalIndex=request.getParameter("abnormalIndex");
		String member=request.getParameter("member");
		String abnormalText=request.getParameter("abnormalText");
		String reworkNote=request.getParameter("reworkNote");
		String eventSubIndex=request.getParameter("eventSubIndex");
		String eventSubName=request.getParameter("eventSubName");
		String event=request.getParameter("event");
		String eventName=request.getParameter("eventName");
		String errorCode=request.getParameter("errorCode");
		String anormalReason=request.getParameter("anormalReason");
		String anormalReasonName=request.getParameter("anormalReasonName");
		String jobText=request.getParameter("jobText");
		String relAbnormalReason=request.getParameter("relAbnormalReason");
		
		Map map=new HashMap();
		map.put("abnormalIndex", abnormalIndex);
		map.put("member", member);
		map.put("abnormalText", abnormalText);
		map.put("reworkNote", reworkNote);
		map.put("subeventIndex", eventSubIndex);
		map.put("subeventCategory", eventSubName);
		map.put("eventIndex", event);
		map.put("eventCategory", eventName);
		map.put("errorCode", errorCode);
		map.put("abnormalReasonIndex", anormalReason);
		map.put("abnormalReason", anormalReasonName);
		map.put("jobText", jobText);
		map.put("relAbnormalReason", relAbnormalReason);
		map.put("updateTime",new Timestamp(System.currentTimeMillis()));
		
		try {
			GenericValue gv=delegator.makeValidValue("AbnormalForm", map);
			request.setAttribute("_EVENT_MESSAGE_", "�޸ĳɹ���");
			delegator.store(gv);
			Debug.logInfo("modify AbnormalForm success [" + abnormalIndex + "/" + userNo + "]", module);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
    }
    
    /**
     * �޸Ļ�ɾ���쳣�ڼ�ı����ƻ�
     * @param request
     * @param response
     * @return
     */
    public static String selectTsUnPm(HttpServletRequest request, HttpServletResponse response) {
    	String eqpId = request.getParameter("eqpId");
    	String modifySchedule = request.getParameter("modifySchedule");
		String deleteSchedule = request.getParameter("deleteSchedule");
		
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);		
		String returnStr = "";
		Map paramMap = new HashMap();
		try {
			paramMap.put("eqpId", eqpId);
			paramMap.put("modifySchedule", modifySchedule);
			paramMap.put("deleteSchedule", deleteSchedule);
			paramMap.put("updateUser",CommonUtil.getUserNo(request));
			
			Map result = dispatcher.runSync("reScheduleTsUndoPM", UtilMisc.toMap("paramMap" ,paramMap));
			if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
				request.setAttribute("_ERROR_MESSAGE_",(String) result.get(ModelService.ERROR_MESSAGE));
				return "error";
			}else{
				returnStr = (String)result.get("returnMsg");
			}
			request.setAttribute("_EVENT_MESSAGE_", returnStr);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
    	return "success";
    }
    
}
