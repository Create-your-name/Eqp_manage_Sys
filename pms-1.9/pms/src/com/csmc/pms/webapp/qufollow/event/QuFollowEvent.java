/**
 * 
 */
package com.csmc.pms.webapp.qufollow.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

import com.csmc.pms.webapp.common.helper.CommonHelper;
import com.csmc.pms.webapp.qufollow.help.QuFollowHelper;
import com.csmc.pms.webapp.report.help.ReportHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.GeneralEvents;
import com.csmc.pms.webapp.workflow.help.WorkflowHelper;

/**
 * ����׷��
 * @author shaoaj
 * @2007-8-29
 */
public class QuFollowEvent extends GeneralEvents{
	public static final String module = QuFollowEvent.class.getName();
	
	/**
	 * ���������ѯҳ��
	 * @param request
	 * @param response
	 * @return
	 */
	public static String intoQuFollowList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String accountNo=CommonUtil.getUserNo(request);
        try {
        	List useInfoList=QuFollowHelper.getAccountSection(delegator, accountNo);
        	GenericValue gv=(GenericValue)useInfoList.get(0);
        	//��½�˵ĿƱ�
        	String section=gv.getString("accountSection");
        	List eqpIdList=QuFollowHelper.getEquipIDList(delegator, section);
        	List equipMentList=CommonHelper.getEquipmentTypeList(delegator);
        	request.setAttribute("EQPID_LIST", eqpIdList);
        	request.setAttribute("EQUIPMENT_LIST", equipMentList);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
	
	/**
	 * ����������ѯ���Ʊ����ѽ᰸�������б�
	 * @param request
	 * @param response
	 * @return
	 */
	public static String queryQuFollowList(HttpServletRequest request,HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String accountNo = CommonUtil.getUserNo(request);
		String type = request.getParameter("objectType");
		String value = request.getParameter("object");
		String status = request.getParameter("status");
		try {
			List useInfoList = QuFollowHelper.getAccountSection(delegator,accountNo);
			GenericValue gv = (GenericValue) useInfoList.get(0);
			// ��½�˵ĿƱ�
			String section = gv.getString("accountSection");
			List sectionInfo = QuFollowHelper.getSectionInfoList(delegator,section);
			GenericValue gvSction = (GenericValue) sectionInfo.get(0);
			List quFollowList = QuFollowHelper.queryQuFollowJobList(delegator,type, value,status, gvSction.getString("sectionIndex"));
			
			List eqpIdList=QuFollowHelper.getEquipIDList(delegator, section);
        	List equipMentList=CommonHelper.getEquipmentTypeList(delegator);
        	request.setAttribute("EQPID_LIST", eqpIdList);
        	request.setAttribute("EQUIPMENT_LIST", equipMentList);
        	request.setAttribute("QUFOLLOW_LIST", quFollowList);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	/**
	 * ������������ҳ��
	 * @param request
	 * @param response
	 * @return
	 */
	public static String intoAddQuFollow(HttpServletRequest request, HttpServletResponse response) {
	  	GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String accountNo=CommonUtil.getUserNo(request);
        String addType=request.getParameter("type");
        String returnStr="abnormalform";
        try {
        	List useInfoList=QuFollowHelper.getAccountSection(delegator, accountNo);
        	GenericValue gv=(GenericValue)useInfoList.get(0);
        	//��½�˵Ĳ���
        	String dept=gv.getString("accountDept");
        	//��½�˵ĿƱ�
        	String section=gv.getString("accountSection");
        	List userList=QuFollowHelper.getSectionInfoList(delegator, section);
        	GenericValue userGv=(GenericValue)userList.get(0);
        	if("FOLLOW".equals(addType)){
        		returnStr="followJob";
	        	List eqpIdList=QuFollowHelper.getEquipIDList(delegator, section);
	        	List equipMentList=CommonHelper.getEquipmentTypeList(delegator);
	        	request.setAttribute("EQPID_LIST", eqpIdList);
	        	request.setAttribute("EQUIPMENT_LIST", equipMentList);
        	}
        	request.setAttribute("DEPT", dept);
        	request.setAttribute("SECTION", section);
          	request.setAttribute("DEPT_INDEX", userGv.getString("deptIndex"));
          	request.setAttribute("creator", accountNo);
        	request.setAttribute("SECTION_INDEX", userGv.getString("sectionIndex"));
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return returnStr;
	}
	
	/**
	 * ��������׷�ٲ�ѯ �����޸�ҳ�� �� �����б��޸�ҳ��
	 * @param request
	 * @param response
	 * @return
	 */
	public static String intoEditQuFollow(HttpServletRequest request,HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String followIndex = request.getParameter("followIndex");
		if(StringUtils.isEmpty(followIndex)){
			followIndex = (String) request.getAttribute("followIndex");
		}
		String url=request.getContextPath()+"/control/saveFile";
		
		try {
			GenericValue gv=QuFollowHelper.queryQuFollowJobByIndex(delegator, followIndex);
			List itemList=new ArrayList();
			//״̬Ϊ"δ�᰸"��"�ѽ᰸"ҳ����Ҫ��ʾ�����б�
			if(String.valueOf(Constants.FOLLOWJOB_NOT_OVER).equals(gv.getString("status"))
					|| String.valueOf(Constants.FOLLOWJOB_OVER).equals(gv.getString("status"))){
				itemList=QuFollowHelper.queryQuItemList(delegator, followIndex, url);
			}
			
			String accountNo=CommonUtil.getUserNo(request);
			List useInfoList=QuFollowHelper.getAccountSection(delegator, accountNo);
        	GenericValue userInfo=(GenericValue)useInfoList.get(0);
        	//��½�˵Ĳ���
        	String dept=userInfo.getString("accountDept");
        	//��½�˵ĿƱ�
        	String section=userInfo.getString("accountSection");
        	List userList=QuFollowHelper.getSectionInfoList(delegator, section);
        	GenericValue userGv=(GenericValue)userList.get(0);
        	List eqpIdList=QuFollowHelper.getEquipIDList(delegator, section);
        	List equipMentList=CommonHelper.getEquipmentTypeList(delegator);
        	
        	request.setAttribute("EQPID_LIST", eqpIdList);
        	request.setAttribute("EQUIPMENT_LIST", equipMentList);
			request.setAttribute("GU_FOLLOW", gv);
			request.setAttribute("ITEM_LIST", itemList);
			
			//�ѽ᰸�Ĳ��źͿα���ʾ(��ʾ��ѯ���Ĳ��ż��α�)
			if(String.valueOf(Constants.FOLLOWJOB_OVER).equals(gv.getString("status"))){
				GenericValue deptGv=delegator.findByPrimaryKey("EquipmentDept",UtilMisc.toMap("deptIndex", gv.getString("deptIndex")));
		    	GenericValue sectioGvn=delegator.findByPrimaryKey("EquipmentSection",UtilMisc.toMap("sectionIndex", gv.getString("sectionIndex")));
		    	request.setAttribute("DEPT", deptGv.getString("equipmentDept"));
	        	request.setAttribute("SECTION", sectioGvn.getString("section"));
			}else{
				request.setAttribute("DEPT", dept);
	        	request.setAttribute("SECTION", section);
			}
			
          	request.setAttribute("DEPT_INDEX", userGv.getString("deptIndex"));
        	request.setAttribute("SECTION_INDEX", userGv.getString("sectionIndex"));
        	request.setAttribute("followIndex", followIndex);       	
        	        	
        	//��ѯ���տγ�
			List ownerProcessList = WorkflowHelper.getProcessSectionLeaderList(delegator);
			request.setAttribute("ownerProcessList", ownerProcessList);
			
			if (WorkflowHelper.existWfSubmitedFollow(delegator, followIndex, Constants.SUBMIT_FOLLOW)) {
        		request.setAttribute("existWfSubmitedFollow", "Y");
        	} else {
        		request.setAttribute("existWfSubmitedFollow", "N");
        	}
        	
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";

	}
	
	/**
	 * �������޸�������Ϣ
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String manageQuFollow(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		
		String eventType=request.getParameter("eventType");
		String eventIndex=request.getParameter("eventIndex");
		String followName=request.getParameter("followName");
		String deptIndex=request.getParameter("deptIndex");
		String sectionIndex=request.getParameter("sectionIndex");
		String objectType=request.getParameter("objectType");
		String object=request.getParameter("object");
		String purpose=request.getParameter("purpose");
		String status=request.getParameter("status");
		String type=request.getParameter("type");
		String creator=CommonUtil.getUserNo(request);
		//String owner=request.getParameter("owner");
		String followIndex=request.getParameter("followIndex");		
		
		Map map=new HashMap();
		map.put("followName", followName);
		map.put("deptIndex", deptIndex);
		map.put("sectionIndex", sectionIndex);
		map.put("objectType", objectType);
		map.put("object", object);
		map.put("objectType", objectType);
		map.put("purpose", purpose);
		map.put("status", status);
		map.put("type", type);
		//map.put("owner", owner);
		map.put("followIndex", followIndex);
		map.put("creator", creator);
		String result="";
		try {
			Map resultMap=QuFollowHelper.manageFollowJob(delegator, dispatcher, map, eventType, eventIndex);
			result=(String)resultMap.get("resultStr");
			request.setAttribute("_EVENT_MESSAGE_", "�������޸ĳɹ�����ȷ�ϲ���ǩ��");
			
			if (result.equals("formAddSuccess")) {
				//ת����ǩҳ��ʱ����ʹ��followIndex
				request.setAttribute("followIndex", (String) resultMap.get("followIndex"));
			}			
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return result;
	}
	
	/**
	 * ��������״̬(�ѽ᰸-1��δ�᰸(��ǩ)-2)
	 * @param request
	 * @param response
	 * @return
	 */
	public static String updateFollowJobStatus(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String flag=request.getParameter("flag");
		String followIndex=request.getParameter("followIndex");
		String status="";
		if("1".equals(flag)){
			status=String.valueOf(Constants.FOLLOWJOB_OVER);
		}else if("2".equals(flag)){
			status=String.valueOf(Constants.FOLLOWJOB_NOT_OVER);
		}
		Map map=new HashMap();
		map.put("status", status);
		map.put("followIndex", followIndex);
		try {
			QuFollowHelper.updateJobStatus(delegator, map);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	/**
	 * ��������ɾ������
	 * @param request
	 * @param response
	 * @return
	 */
	public static String deleteFollowJob(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String followIndex=request.getParameter("followIndex");
		LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		try {
			QuFollowHelper.delFollowJobService(delegator, dispatcher, followIndex);
			request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
//---------------------------------������Ϣ--------------------------------------------------------
    /**
     * ����������ѯ����׷�ٹ�����Ϣ
     * 
     * @param request
     * @param response
     */
    public static void queryFollowItemByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String uploadIndex = request.getParameter("uploadIndex");
        String itemIndex = request.getParameter("itemIndex");
        try {
            GenericValue followItem = delegator.findByPrimaryKey("FollowItem", UtilMisc.toMap("itemIndex", itemIndex));
            JSONObject doc = new JSONObject();
            doc.put("itemContent", followItem.getString("itemContent"));
            if(StringUtils.isNotEmpty(uploadIndex)){
            	GenericValue document = delegator.findByPrimaryKey("DocumentUpload", UtilMisc.toMap("uploadIndex", uploadIndex));
            	doc.put("fileUrl", document.getString("fileUrl"));
            }else{
            	doc.put("fileUrl", "");
            }
            response.getWriter().write(doc.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }

    /**
     * ����/�������ⲽ��
     * 
     * @param request
     * @param response
     */
	public static String manageQuFollowItem(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String transBy=CommonUtil.getUserNo(request);
		LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		String itemContent=request.getParameter("itemContent");
		String followIndex=request.getParameter("followIndex");
		String itemIndex=request.getParameter("itemIndex");
		String uploadIndex=request.getParameter("uploadIndex");
		try {
			Map itemMap=new HashMap();
			itemMap.put("itemIndex", itemIndex);
			itemMap.put("itemContent", itemContent);
			itemMap.put("followIndex", followIndex);
			itemMap.put("transBy", transBy);
			itemMap.put("uploadIndex", uploadIndex);
			itemMap.put("uploadIndex", uploadIndex);
			QuFollowHelper.manageFollowJbItem(delegator, dispatcher,itemMap);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
	}
	
    /**
     * ��������ɾ������׷�ٹ��̼��ĵ���Ϣ(documentUpload����Ϣ)
     * TODO����ɾ���ĵ�����
     * @param request
     * @param response
     * @return �����Ƿ�ɹ�
     */
    public static String deleteFollowIndexByPk(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String itemIndex = request.getParameter("itemIndex");
        String followIndex = request.getParameter("followIndex");
        LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
        try {
            QuFollowHelper.delFollowJobItemByPk(delegator,dispatcher,itemIndex);
            request.setAttribute("followIndex", followIndex);
            request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }
    
    //---------------------------------�ѽ᰸��Ϣ��ѯ--------------------------------------------------------
    /**
     * �����ѽ᰸��ѯҳ��
     */
    public static String intoQueryQuFollow(HttpServletRequest request, HttpServletResponse response) {
	  	GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String accountNo=CommonUtil.getUserNo(request);
        try {
        	List useInfoList=QuFollowHelper.getAccountSection(delegator, accountNo);
        	GenericValue gv=(GenericValue)useInfoList.get(0);
        	//��½�˵ĿƱ�
        	String section=gv.getString("accountSection");
        	List eqpIdList=QuFollowHelper.getEquipIDList(delegator, section);
        	List equipMentList=CommonHelper.getEquipmentTypeList(delegator);
        	List deptList=ReportHelper.getDeptList(delegator);
        	request.setAttribute("EQPID_LIST", eqpIdList);
        	request.setAttribute("EQUIPMENT_LIST", equipMentList);
        	request.setAttribute("DEPT_LIST", deptList);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
	}
    
    /**
	 * ����deptIndex�õ�section��Ϣ
	 * @param request
	 * @param response
	 */
	public static void getSectionInfo(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String deptIndex=request.getParameter("deptIndex");
		JSONObject jsObject=new JSONObject();
		JSONArray periodNameArray=new JSONArray();
		JSONArray periodValueArray=new JSONArray();
		try {
			//���������б�
			List prodList = QuFollowHelper.getSectionList(delegator, deptIndex);
			for (int i=0;i<prodList.size();i++){
				GenericValue gv=(GenericValue)prodList.get(i);
				periodValueArray.put(gv.getString("sectionIndex"));
				periodNameArray.put(gv.getString("section"));
			}
			jsObject.put("sectionArray", periodNameArray);
			jsObject.put("sectionIndexArray", periodValueArray);
			
			//����jsobject
			response.getWriter().write(jsObject.toString());
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}
	
	/**
	 * ��ѯ�ѽ᰸������׷����Ϣ
	 * @param request
	 * @param response
	 * @return
	 */
	public static String queryOverFollowJob(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String accountNo=CommonUtil.getUserNo(request);
		
    	Map map=new HashMap();
		String objectType=request.getParameter("objectType");
		String value=request.getParameter("object");
		String sectionIndex=request.getParameter("section");
		String deptIndex=request.getParameter("dept");
		String beginTime=request.getParameter("startDate");
		String endTime=request.getParameter("endDate");
		map.put("objectType",objectType);
    	map.put("object",value);
    	map.put("sectionIndex",sectionIndex);
    	map.put("deptIndex",deptIndex);
    	map.put("beginTime",beginTime);
    	map.put("endTime",endTime);
    	map.put("status","1");//״̬Ϊ�ѽ᰸
    	
		try {
			//��½�˵ĿƱ�
			List useInfoList=QuFollowHelper.getAccountSection(delegator, accountNo);
        	GenericValue gv=(GenericValue)useInfoList.get(0);        	
        	String section=gv.getString("accountSection");
        	
        	List eqpIdList=QuFollowHelper.getEquipIDList(delegator, section);
        	List equipMentList=CommonHelper.getEquipmentTypeList(delegator);
        	List deptList=ReportHelper.getDeptList(delegator);
        	List quFollowJobList = QuFollowHelper.queryQuFollowJobList(delegator, map);        	
        	
        	request.setAttribute("EQPID_LIST", eqpIdList);
        	request.setAttribute("EQUIPMENT_LIST", equipMentList);
        	request.setAttribute("DEPT_LIST", deptList);
        	request.setAttribute("FOLLOWJOB_LIST", quFollowJobList);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
	}
}
