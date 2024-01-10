package com.csmc.pms.webapp.parts.event;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityWhereString;

import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.common.helper.CommonHelper;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.form.event.TsFormEvent;
import com.csmc.pms.webapp.parts.help.PartsHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.GeneralEvents;
public class PartsEvent extends GeneralEvents {
	public static final String module = PartsEvent.class.getName();
	
	//-----------------------------------------�ͼ����Ϲ����趨--------------------------------------

	/**
	 * ���뱣�������趨
	 * @param request
	 * @param response
	 * @return �����Ƿ�ɹ�
	 */
	public static String intoFilterParts(HttpServletRequest request,
	        HttpServletResponse response){
	    GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
	    try{
	        request.setAttribute("eqpDeptList", delegator.findAllCache("EquipmentDept"));
	    }catch (Exception e) {
	        request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	        Debug.logError(e, module);
	        return "error";
	    }
	    return "success";
	}

	/**
	 * ���뱣�������趨
	 * @param request
	 * @param response
	 * @return �����Ƿ�ɹ�
	 */
	public static String queryFilterParts(HttpServletRequest request,
	        HttpServletResponse response){
	    GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
	    try{
	        String equipmentDept=request.getParameter("equipmentDept");
	        Map paraMap=new HashMap();
	        paraMap.put("equipmentDept", equipmentDept);
	        List filterPartsList = PartsHelper.getFilterPartsList(delegator,paraMap);
	        request.setAttribute("filterPartsList", filterPartsList);
	        request.setAttribute("eqpDeptList", delegator.findAllCache("EquipmentDept"));
	    }catch (Exception e) {
	        request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	        Debug.logError(e, module);
	        return "error";
	    }
	    return "success";
	}

	/**
     * ����/���µͼ۹�������
     * @param request
     * @param response
     */
    public static String addFilterParts(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String partNo = request.getParameter("partNo").toUpperCase().trim();
        String partName = request.getParameter("partName").trim();
        String price = request.getParameter("price").trim();
        String deptIndex = request.getParameter("costCenter");
        request.setAttribute("equipmentDept", deptIndex);
        try {
            GenericValue deptGv = delegator.findByPrimaryKey("EquipmentDept", UtilMisc.toMap("deptIndex", deptIndex));
            String costCenter = deptGv.getString("equipmentDept");
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");

            String equipmentDept = request.getParameter("equipmentDept");
            Map paraMap = new HashMap();
            paraMap.put("equipmentDept", deptIndex);

            Map hashMap = new HashMap();
            hashMap.put("partNo", partNo);
            hashMap.put("partName", partName);
            hashMap.put("price", price);
            hashMap.put("deptIndex", deptIndex);
            hashMap.put("costCenter", costCenter);
            hashMap.put("createTime", new Timestamp(System.currentTimeMillis()));
            hashMap.put("transBy", user);
            GenericValue gv = delegator.findByPrimaryKey("PartsFilter", UtilMisc.toMap("partNo", partNo));
            // ����
            if (gv != null) {
                request.setAttribute("_ERROR_MESSAGE_", "���Ϻ��Ѵ���");
                request.setAttribute("eqpDeptList", delegator.findAllCache("EquipmentDept"));
                List filterPartsList = PartsHelper.getFilterPartsList(delegator,paraMap);
                request.setAttribute("filterPartsList", filterPartsList);
                return "error";
            } else {
                PartsHelper.addFilterParts(delegator, hashMap);
                request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
            }
            request.setAttribute("eqpDeptList", delegator.findAllCache("EquipmentDept"));
            List filterPartsList = PartsHelper.getFilterPartsList(delegator,paraMap);
            request.setAttribute("filterPartsList", filterPartsList);
        } catch (Exception e) {
            Debug.logError(module, e.getMessage());
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }


    /**
     * ����partNoɾ����Ϣ
     * @param request
     * @param response
     * @return �����Ƿ�ɹ�
     */
    public static String deleteFilterPartsByPk(HttpServletRequest request,
            HttpServletResponse response){
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String partNo=request.getParameter("partNo");

        String equipmentDept=request.getParameter("equipmentDept");
        Map paraMap=new HashMap();
        paraMap.put("equipmentDept", equipmentDept);
        try{
            GenericValue gv = delegator.findByPrimaryKey("PartsFilter", UtilMisc.toMap("partNo",partNo));
            PartsHelper.deleteFilterPartsByPk(delegator, partNo);
            request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
            request.setAttribute("eqpDeptList", delegator.findAllCache("EquipmentDept"));
            List filterPartsList = PartsHelper.getFilterPartsList(delegator,paraMap);
            request.setAttribute("filterPartsList", filterPartsList);
        }catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }
    
	/**
	 * ����������Ϣҳ��
	 * @param request
	 * @param response
	 * @return
	 */
	public static String intoRcyleParts(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try {
			GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
			String user = userLogin.getString("userLoginId");
			List list=PartsHelper.getAccountSection(delegator,user);
			GenericValue gv=(GenericValue)list.get(0);
			request.setAttribute("accountSection", gv.getString("accountSection"));
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	/**
	 * ��ѯ���Ϻ�
	 * 
	 * @param request
	 * @param response
	 * @return action
	 */
	public static String queryPartsNo(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String partNo=request.getParameter("partNo");
		String partName=request.getParameter("partName");
		Map hashMap=new HashMap();
		boolean flag=false;
		if(StringUtils.isNotEmpty(partNo)){
			hashMap.put("partNo",partNo.toUpperCase()+"%");
			flag=true;
		}
		if(StringUtils.isNotEmpty(partName)){
			hashMap.put("partName",partName.toUpperCase()+"%");
			flag=true;
		}
		try {
			List list=null;
			if(flag){
				list = PartsHelper.getPartsNoList(delegator,hashMap);
			}else{
				list=new ArrayList();
			}
			request.setAttribute("partsNoList", list);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	/**
	 * ���Ϻ�Ψһ��У��
	 * @param request
	 * @param response
	 */
	public static void checkRecylePartsNo(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String partNo=request.getParameter("partNo");
		try {
			GenericValue gv = delegator.findByPrimaryKey("Parts", UtilMisc.toMap("partNo",partNo));
			JSONObject pmsReason = new JSONObject();
			pmsReason.put("partNo", gv.getString("partNo"));
			pmsReason.put("partName", gv.getString("partName"));
			response.getWriter().write(pmsReason.toString());
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
		}
	}
	
	
	/**
	 * ����partStyle��region��ѯ������Ϣ�б�
	 * @param request
	 * @param response
	 * @return ������Ϣ�б�
	 */
	public static String queryRecyclePartsList(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String partStyle=request.getParameter("pStyle");
		String region=request.getParameter("pregion");
		// ����һ�������󣬽���˷����õ�request
		String partStyleAttr=(String)request.getAttribute("partStyle");
		String regionAttr=(String)request.getAttribute("region");
		try {
			List list=null;
			if (StringUtils.isNotEmpty(partStyle)&&StringUtils.isNotEmpty(region)){
				list = PartsHelper.getRecylePartsList(delegator,partStyle,region);
				request.setAttribute("partStyle", partStyle);
				request.setAttribute("region", region);
			}else{
				list = PartsHelper.getRecylePartsList(delegator,partStyleAttr,regionAttr);
				request.setAttribute("partStyle", partStyleAttr);
				request.setAttribute("region", regionAttr);
			}
			request.setAttribute("recyclePartsList", list);
			request.setAttribute("flag", "ok");
			getAccountSection(request,response);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	/**
	 * ����/��������
	 * @param request
	 * @param response
	 */
	public static String manageRecycleParts(HttpServletRequest request,HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String partStyle=request.getParameter("partStyle");
		String functionType=request.getParameter("functionType");
		String region=request.getParameter("region");
		String partNo=request.getParameter("partNo");
		String partName=request.getParameter("partName");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String user = userLogin.getString("userLoginId");
		
		Map hashMap=new HashMap();
		hashMap.put("partNo", partNo.toUpperCase());
		hashMap.put("partStyle", partStyle);
		hashMap.put("region", region);
		hashMap.put("partName", partName.toUpperCase());
		hashMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
		try {
			hashMap.put("transBy", user);
			GenericValue gv = delegator.findByPrimaryKey("Parts", UtilMisc.toMap("partNo",partNo));
			request.setAttribute("partStyle", partStyle);
			request.setAttribute("region", region);
			//����
			if(functionType=="2" && gv!=null){
				request.setAttribute("_ERROR_MESSAGE_","���Ϻ��Ѵ���");
				return "error";
			}else{
				PartsHelper.manageRecyleParts(delegator,hashMap);
				request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	/**
	 * ����partNoɾ����Ϣ
	 * @param request
	 * @param response
	 * @return �����Ƿ�ɹ�
	 */
	public static String deleteRecyclePartsByPk(HttpServletRequest request,
			HttpServletResponse response){
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String partNo=request.getParameter("partNo");
		try{
			GenericValue gv = delegator.findByPrimaryKey("Parts", UtilMisc.toMap("partNo",partNo));
			request.setAttribute("partStyle", gv.getString("partStyle"));
			request.setAttribute("region", gv.getString("region"));
			PartsHelper.deleteRecylePartsByPk(delegator, partNo);
			request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
		}catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			Debug.logError(e, module);
			return "error";
		}
		return "success";	
	}
	
	//-----------------------------------------���������趨--------------------------------------
	
	/**
	 * ���뱣�������趨
	 * @param request
	 * @param response
	 * @return �����Ƿ�ɹ�
	 */
	public static String intoPmParts(HttpServletRequest request,
			HttpServletResponse response){
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try{
			List eqpList = CommonHelper.getEquipmentTypeList(delegator);
			request.setAttribute("equipMentList", eqpList);
		}catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			Debug.logError(e, module);
			return "error";
		}
		return "success";	
	}
	
	/**
	 * ͨ���豸������eqpId,��������
	 * @param request
	 * @param response
	 */
	public static void getEqpipAndPmTypeIDList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String equipmentType=request.getParameter("equipmentType");
		JSONObject jsObject=new JSONObject();
		JSONArray eqpArray=new JSONArray();
		JSONArray periodNameArray=new JSONArray();
		JSONArray periodValueArray=new JSONArray();
		
		try {
			//���豸��ѯ�豸����
			String equipmentId = request.getParameter("equipmentId");
			if (StringUtils.isNotEmpty(equipmentId)) {
				GenericValue equipment = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId", equipmentId));
	            if(equipment != null && CommonUtil.isNotEmpty(equipment.getString("equipmentType"))) {
	            	equipmentType = equipment.getString("equipmentType");
	            }
			}
			
			//�õ�eqpId�б�
			List eqpList = PartsHelper.getEquipIDList(delegator, equipmentType);
			for (int i=0;i<eqpList.size();i++){
				GenericValue gv=(GenericValue)eqpList.get(i);
				eqpArray.put(gv.getString("equipmentId"));
			}
			jsObject.put("eqpIdArray", eqpArray);
			
			//���������б�
			List prodList = PartsHelper.getPeriodList(delegator, equipmentType);
			for (int i=0;i<prodList.size();i++){
				GenericValue gv=(GenericValue)prodList.get(i);
				periodValueArray.put(gv.getString("periodIndex"));
				periodNameArray.put(gv.getString("periodName"));
			}
			jsObject.put("priodNameArray", periodNameArray);
			jsObject.put("priodValueArray", periodValueArray);
			
			//����jsobject
			response.getWriter().write(jsObject.toString());
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}
	
	/**
	 * ͨ�������õ�������Ϣ
	 * @param request
	 * @param response
	 */
	public static void getPmPartsInfo(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String partPmIndex=request.getParameter("partPmIndex");
		try {
			GenericValue gv = delegator.findByPrimaryKey("PartsPm", UtilMisc.toMap("partPmIndex",partPmIndex));
			JSONObject pmsReason = new JSONObject();
			pmsReason.put("partsNo", gv.getString("partNo"));
			pmsReason.put("partName", gv.getString("partName"));
			pmsReason.put("partCount", gv.getString("partCount"));
			pmsReason.put("mtrGrp", gv.getString("mtrGrp"));
			pmsReason.put("partType", gv.getString("partType"));
			pmsReason.put("remark", gv.getString("remark"));
			pmsReason.put("templateCount", gv.getString("templateCount"));
			response.getWriter().write(pmsReason.toString());
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
		}
	}
	
	/**
	 * ����������ѯPartsPm
	 * @param request
	 * @param response
	 * @return
	 */
	public static String queryPartsPmList(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String equipmentType=request.getParameter("equipmentType");
		String equipmentId=request.getParameter("equipmentId");
		String pmType=request.getParameter("period");
		//��ά��ҳ�����ʱ���õ�����ֵ
		String equipmentType_Atr=(String)request.getAttribute("equipmentType");
		String equipmentId_Atr=(String)request.getAttribute("equipmentId");
		String pmType_Atr=(String)request.getAttribute("period");
				
		Map map=new HashMap();
		map.put("equipmentType", equipmentType);
		map.put("equipmentId", equipmentId);
		map.put("period", pmType);
		List list=null;
		//ά��ҳ���ʱ�Ĳ���
		Map hashMap=new HashMap();
		hashMap.put("equipmentType", equipmentType_Atr);
		hashMap.put("equipmentId", equipmentId_Atr);
		hashMap.put("period", pmType_Atr);
		try {
			//��ѯʱ����
			if(equipmentType!=null&&equipmentId!=null&&pmType!=null){
				list=PartsHelper.getPmPartsList(delegator, map);
				request.setAttribute("equipmentType", equipmentType);
				request.setAttribute("period", pmType);
				request.setAttribute("equipmentId", equipmentId);
			}else{
				//��ά��ҳ�����
				list=PartsHelper.getPmPartsList(delegator, hashMap);
				request.setAttribute("equipmentType", equipmentType_Atr);
				request.setAttribute("period", pmType_Atr);
				request.setAttribute("equipmentId", equipmentId_Atr);
			}
			request.setAttribute("PM_PARTS_LIST", list);
			List eqpList = CommonHelper.getEquipmentTypeList(delegator);
			request.setAttribute("equipMentList", eqpList);
			request.setAttribute("flag", "ok");
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
//	/**
//	 * ����/����PartsPm
//	 * @param request
//	 * @param response
//	 */
//	public static String managePmParts(HttpServletRequest request,HttpServletResponse response) {
//		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
//		String eqpId=request.getParameter("eqpId");
//		String periodIndex=request.getParameter("periodIndex");
//		String partPmIndex=request.getParameter("partPmIndex");
//		String partsNo=request.getParameter("partsNo");
//		String partName=request.getParameter("partName");
//		
//		String partCount=request.getParameter("partCount");
//		String equipType=request.getParameter("equipType");
//		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
//		String user = userLogin.getString("userLoginId");
//		Map map=new HashMap();
//		map.put("eqpId", eqpId);
//		map.put("periodIndex", periodIndex);
//		map.put("partPmIndex", partPmIndex);
//		map.put("partNo", partsNo);
//		map.put("partName", partName);
//		map.put("partCount", partCount);
//		map.put("transBy", user);
//		map.put("updateTime", new Timestamp(System.currentTimeMillis()));
//		try {
//			List gv = delegator.findByAnd("PartsPm", UtilMisc.toMap("partNo",partsNo,"eqpId",eqpId,"periodIndex",periodIndex));
//			//�������д����ϺŴ���
//			List eqpList = CommonHelper.getEquipmentTypeList(delegator);
//			request.setAttribute("equipMentList", eqpList);
//			//�����request��,����ѯҳ��ʱʹ��
//			request.setAttribute("equipmentType", equipType);
//			request.setAttribute("period", periodIndex);
//			request.setAttribute("equipmentId", eqpId);
//			if(StringUtils.isEmpty(partPmIndex) && gv.size()!=0){
//				request.setAttribute("_ERROR_MESSAGE_","���Ϻ��Ѵ���");
//				return "error";
//			}else{
//				PartsHelper.managePmParts(delegator, map);
//				request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
//			}
//		} catch (Exception e) {
//			Debug.logError(e.getMessage(), module);
//			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
//			return "error";
//		}
//		return "success";
//	}
	
    /**
     * �����趨 ����PartsPm
     *
     * @param request
     * @param response
     */
    public static String managePmParts(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String eqpId = request.getParameter("eqpId");
        String periodIndex = request.getParameter("periodIndex");
        String partPmIndex = request.getParameter("partPmIndex");
        String flowIndex = request.getParameter("flowIndex");
        String partsNo = request.getParameter("partsNo");
        String partName = request.getParameter("partName");
        String remark = request.getParameter("remark");
        String mtrGrp = request.getParameter("mtrGrp");
        String partType = request.getParameter("partType");
        String templateCount = request.getParameter("templateCount");
        String partCount = request.getParameter("partCount");
        String equipType = request.getParameter("equipType");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String user = userLogin.getString("userLoginId");
        Map map = new HashMap();
        map.put("eqpId", eqpId);
        map.put("eqpType", equipType);
        map.put("periodIndex", periodIndex);
        map.put("partPmIndex", partPmIndex);
        map.put("flowIndex", flowIndex);
        map.put("partNo", partsNo);
        map.put("partName", partName);
        map.put("partCount", partCount);
        map.put("remark", remark);
        map.put("mtrGrp", mtrGrp);
        map.put("partType", partType);
        map.put("templateCount",templateCount);
        map.put("transBy", user);
        map.put("updateTime", new Timestamp(System.currentTimeMillis()));
        try {
            List gv = delegator.findByAnd("PartsPm", UtilMisc.toMap("partNo", partsNo, "periodIndex", periodIndex,
                    "flowIndex", flowIndex, "mtrGrp", mtrGrp, "partType", partType));
            // �������д����ϺŴ���
            List eqpList = PartsHelper.getEquipMentList(delegator);
            request.setAttribute("equipMentList", eqpList);
            // �����request��,����ѯҳ��ʱʹ��
            request.setAttribute("equipmentType", equipType);
            request.setAttribute("period", periodIndex);
            request.setAttribute("equipmentId", eqpId);
            request.setAttribute("flow", flowIndex);
            if (StringUtils.isEmpty(partPmIndex) && gv.size() != 0) {
                request.setAttribute("_ERROR_MESSAGE_", partsNo + "�Ѵ���");
                return "error";
            } else {
                PartsHelper.managePmParts(delegator, map);
                request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
            }
        } catch (Exception e) {
            Debug.logError(module, e.getMessage());
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
	
	/**
	 * ����partPmIndexɾ����Ϣ
	 * @param request
	 * @param response
	 * @return �����Ƿ�ɹ�
	 */
	public static String deletePmPartsByPk(HttpServletRequest request,
			HttpServletResponse response){
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String partPmIndex=request.getParameter("partPmIndex");
		String equipmentType=request.getParameter("equipmentType");
		try{
			GenericValue gv = delegator.findByPrimaryKey("PartsPm", UtilMisc.toMap("partPmIndex",partPmIndex));
			request.setAttribute("period", gv.getString("periodIndex"));
			request.setAttribute("equipmentId", gv.getString("eqpId"));
			request.setAttribute("equipmentType", equipmentType);
			request.setAttribute("flow", gv.getString("flowIndex"));
			PartsHelper.deletePmPartsByPk(delegator, partPmIndex);
			List eqpList = CommonHelper.getEquipmentTypeList(delegator);
			request.setAttribute("equipMentList", eqpList);
			request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
		}catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			Debug.logError(e, module);
			return "error";
		}
		return "success";	
	}
	
//	/**
//	 * ������Ϣ����
//	 * @param request
//	 * @param response
//	 * @return �����Ƿ�ɹ�
//	 */
//	public static String copyPmParts(HttpServletRequest request,
//			HttpServletResponse response){
//		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
//		String count=request.getParameter("count");
//		String equipType=request.getParameter("equipmentTypeCopy");
//		String eqpId=request.getParameter("equipmentIdCopy");
//		String periodIndex=request.getParameter("periodCopy");
//		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
//		String user = userLogin.getString("userLoginId");
//		int i=new Integer(count).intValue();
//		List parap=new ArrayList();
//		StringBuffer sb=new StringBuffer();
//		sb.append("select count(*) VALUECOUNT from parts_pm where eqp_id='").append(eqpId).append("'");
//		sb.append(" and period_index='").append(periodIndex).append("' and(");
//		for(int j=1;j<=i;j++){
//			String copyPartsNo=request.getParameter("copyPartsNo_"+j);
//			String copyPartsCount=request.getParameter("copyPartsCount_"+j);
//			String copyPartsName=request.getParameter("copyPartsName_"+j);
//			Map map=new HashMap();
//			map.put("eqpId", eqpId);
//			map.put("periodIndex", periodIndex);
//			map.put("periodIndex", periodIndex);
//			map.put("partPmIndex", "");
//			map.put("partNo", copyPartsNo);
//			map.put("partName", copyPartsName);
//			map.put("partCount", copyPartsCount);
//			map.put("transBy", user);
//			map.put("updateTime", new Timestamp(System.currentTimeMillis()));
//			parap.add(map);
//			map=null;
//			if(j==1){
//				sb.append(" part_no='").append(copyPartsNo).append("'");
//			}else{
//				sb.append(" or part_no='").append(copyPartsNo).append("'");
//			}
//		}
//		sb.append(")");
//		try{
//			List list = SQLProcess.excuteSQLQuery(sb.toString(), delegator);
//			Map valueMap=(Map)list.get(0);
//			String partCount=(String)valueMap.get("VALUECOUNT");
//			// �����request��,����ѯҳ��ʱʹ��
//			request.setAttribute("equipmentType", equipType);
//			request.setAttribute("period", periodIndex);
//			request.setAttribute("equipmentId", eqpId);
//			if(!"0".equals(partCount)){
//				request.setAttribute("_ERROR_MESSAGE_", "���ƴ��󣬴����ظ���partNo");
//				return "error";
//			}
//			for(int k=0;k<parap.size();k++){
//				PartsHelper.managePmParts(delegator, (Map)parap.get(k));
//			}
//		}catch (Exception e) {
//			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
//			Debug.logError(e, module);
//			return "error";
//		}
//		return "success";	
//	}
	
    /**
     * ������Ϣ����
     *
     * @param request
     * @param response
     * @return �����Ƿ�ɹ�
     */
    public static String copyPmParts(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String count = request.getParameter("count");
        String equipType = request.getParameter("equipmentTypeCopy");
        String periodIndex = request.getParameter("periodCopy");
        String flowIndex = request.getParameter("flowCopy");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String user = userLogin.getString("userLoginId");
        int i = new Integer(count).intValue();
        List parap = new ArrayList();// ���ڴ洢��PART_PM��
        StringBuffer rSB = new StringBuffer();// ���нY����Ϣ
        try {
            for (int j = 1; j <= i; j++) {
                String checkParts = request.getParameter("check_parts_" + j);// �Ƿ�ѡ��
                if (StringUtils.isEmpty(checkParts) || checkParts.indexOf("on") < 0) {
                    continue;
                }

                String copyPartsNo = request.getParameter("copyPartsNo_" + j);
                String copyPartsCount = request.getParameter("copyPartsCount_" + j);
                String copyPartsName = request.getParameter("copyPartsName_" + j);
                String copyMtrGrp = request.getParameter("copyMtrGrp_" + j);
                String copyPartType = request.getParameter("copyPartType_" + j);
                String copyRemark = UtilFormatOut.checkNull(request.getParameter("copyRemark_" + j));

                // ��ѯͬһ�����������Ƿ����ظ�������
                StringBuffer qExist = new StringBuffer();
                qExist.append("select count(*) COUNT from dual where exists(select 1 from parts_pm where eqp_type='");
                qExist.append(equipType).append("'");
                qExist.append(" and period_index='").append(periodIndex).append("'");
                qExist.append(" and flow_index='").append(flowIndex).append("'");
                qExist.append(" and mtr_grp='").append(copyMtrGrp).append("'");
                qExist.append(" and part_no='").append(copyPartsNo).append("')");
                List list = SQLProcess.excuteSQLQuery(qExist.toString(), delegator);
                Map valueMap = (Map) list.get(0);
                String qCount = (String) valueMap.get("COUNT");
                if (StringUtils.isNotEmpty(qCount) && !"0".equals(qCount)) {
                    if (rSB.length() > 0) {
                        rSB.append(copyPartsNo + "\n");
                    } else {
                        rSB.append("���������ڴ������������趨������ʧ�ܣ�:\n");
                        rSB.append(copyPartsNo + "\n");
                    }
                    continue;
                }
                // ���ظ����ϾͲ�����
                if (rSB.length() == 0) {
                    Map map = new HashMap();
                    map.put("eqpType", equipType);
                    map.put("periodIndex", periodIndex);
                    map.put("flowIndex", flowIndex);
                    map.put("partPmIndex", "");
                    map.put("partNo", copyPartsNo);
                    map.put("partName", copyPartsName);
                    map.put("partCount", copyPartsCount);
                    map.put("mtrGrp", copyMtrGrp);
                    map.put("partType", copyPartType);
                    map.put("remark", copyRemark);
                    map.put("transBy", user);
                    map.put("updateTime", new Timestamp(System.currentTimeMillis()));
                    parap.add(map);
                }
            }
            // �����request��,����ѯҳ��ʱʹ��
            request.setAttribute("equipmentType", equipType);
            request.setAttribute("period", periodIndex);
            request.setAttribute("flow", flowIndex);
            // ���ظ����ϾͲ�����
            if (rSB.length() == 0) {
                for (int k = 0; k < parap.size(); k++) {
                    PartsHelper.managePmParts(delegator, (Map) parap.get(k));// save
                                                                             // db
                }
            }
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        if (rSB.length() > 0) {
            request.setAttribute("_ERROR_MESSAGE_", rSB.toString());
        }
        return "success";
    }

    /**
     * ����������ѯPartsPmģ��
     * @param request
     * @param response
     * @author qinchao
     * @return
     */
    public static String queryPmPartsTemplateList(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = (GenericDelegator) request
    		.getAttribute("delegator");
    	String equipmentType = request.getParameter("equipmentType");
    	String pmType = request.getParameter("period");
    	String flowType = request.getParameter("flow");
		
    	// ��ά��ҳ�����ʱ���õ�����ֵ
    	String equipmentType_Atr = (String) request
    		.getAttribute("equipmentType");
    	String pmType_Atr = (String) request.getAttribute("period");
    	String flowType_Atr = (String) request.getAttribute("flow");

    	Map map = new HashMap();
    	map.put("equipmentType", equipmentType);
    	map.put("period", pmType);
    	map.put("flow", flowType);

    	// ά��ҳ���ʱ�Ĳ���
    	Map hashMap = new HashMap();
    	hashMap.put("equipmentType", equipmentType_Atr);
    	hashMap.put("period", pmType_Atr);
    	hashMap.put("flow", flowType_Atr);

    	List list = null;
    	try {
    		// ȡ��partType
			request.setAttribute("partTypeList", CommonHelper.getPartTypeList(delegator));
    	    // ��ѯʱ����
    	    if (equipmentType != null && pmType != null) {
    		list = PartsHelper.getPmPartsTemplateList(delegator, map);
    		request.setAttribute("equipmentType", equipmentType);
    		request.setAttribute("period", pmType);
    		request.setAttribute("flow", flowType);
    	    } else {
    		// ��ά��ҳ���<cr>
    		list = PartsHelper.getPmPartsTemplateList(delegator, hashMap);
    		request.setAttribute("equipmentType", equipmentType_Atr);
    		request.setAttribute("period", pmType_Atr);
    		request.setAttribute("flow", flowType_Atr);
    	    }
    	    request.setAttribute("PM_PARTS_LIST", list);
    	    List eqpList = PartsHelper.getEquipMentList(delegator);
    	    request.setAttribute("equipMentList", eqpList);
    	    request.setAttribute("flag", "ok");
    	} catch (Exception e) {
    	    Debug.logError(module, e.getMessage());
    	    request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
    	    return "error";
    	}
    	return "success";
    }

    /**
     * �������ϲ�ѯҳ��
     * @param request
     * @param response
     * @author qinchao
     * @return
     */
    public static String intoPartsQuery(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    	String eqpType = request.getParameter("eqpType");
    	String periodIndex = request.getParameter("periodIndex");
    	String flowIndex = request.getParameter("flowIndex");
    	
    	try {
    	    request.setAttribute("eqpType", eqpType);
    	    request.setAttribute("periodIndex", periodIndex);
    	    request.setAttribute("flowIndex", flowIndex);
    	} catch (Exception e) {
    	    request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
    	    return "error";
    	}
    	return "success";
    }

    /**
     * �����趨
     * ����PARTS ģ��
     * @param request
     * @param response
     * @return
     */
    public static String savePartsPmInfo(HttpServletRequest request, HttpServletResponse response) {
        try {
            //StringBuilder errSB = new StringBuilder();
            GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
            Map parMap = TsFormEvent.getInitParams(request, false, false);
            String transBy = CommonUtil.getUserNo(request);
            String periodIndex = (String) parMap.get("periodIndex");
            String partPmIndex = (String) parMap.get("partPmIndex");
            String flowIndex = (String) parMap.get("flowIndex");
            String eqpType = (String) parMap.get("equipType");
            String mtrGrp = (String) parMap.get("mtrGrp");

            request.setAttribute("eqpType", eqpType);
            StringBuffer rSB = new StringBuffer();// �ظ�������Ϣ
            List mapList = new ArrayList();// for save db
            for (Iterator it = parMap.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                if (key.startsWith("parts_")) {
                    // ������
                    String orderNum = key.substring(key.indexOf("_") + 1);// ���index
                    String partsNo = (String) parMap.get(key);
                    String partsName = (String) parMap.get("partsName_" + orderNum);
                    String partsNum = (String) parMap.get("partsNum_" + orderNum);
                    String remark = (String) parMap.get("remark_" + orderNum);
                    String partType = (String) parMap.get("partType_" + orderNum);
                    String templateCount = (String) parMap.get("templateCount_" + orderNum);

                    //��ͬ�ı�������,��������,������,�������(1200,1201)��,�������ظ�������
                    List list = delegator
                            .findByAnd("PartsPm", UtilMisc.toMap("partNo", partsNo, "eqpType", eqpType, "periodIndex",
                                    periodIndex, "mtrGrp", mtrGrp, "flowIndex", flowIndex, "partType", partType));
                    if (StringUtils.isEmpty(partPmIndex) && list.size() != 0) {
                        if (rSB.length() > 0) {
                            rSB.append(partsNo + "\n");
                        } else {
                            rSB.append("���������ڴ������������趨������ʧ�ܣ�:\n");
                            rSB.append(partsNo + "\n");
                        }
                        continue;
                    }
                    // ���ظ����ϾͲ�����
                    if (rSB.length() == 0) {
                        Map map = new HashMap();
                        map.put("periodIndex", periodIndex);
                        map.put("partPmIndex", partPmIndex);
                        map.put("mtrGrp", mtrGrp);
                        map.put("transBy", transBy);
                        map.put("flowIndex", flowIndex);
                        map.put("eqpType", eqpType);
                        map.put("updateTime", new Timestamp(System.currentTimeMillis()));
                        map.put("partNo", partsNo);
                        map.put("partName", partsName);
                        map.put("partCount", partsNum);
                        map.put("remark", remark);
                        map.put("partType", partType);
                        map.put("templateCount", templateCount);

                        mapList.add(map);
                    }
                }
            }
            if (rSB.length() > 0) {
                request.setAttribute("_ERROR_MESSAGE_", rSB.toString());
                request.setAttribute("flag", "N");// ��ˢ��ĸҳ��
            } else {
                for (Iterator it = mapList.iterator(); it.hasNext();) {
                    Map map = (Map) it.next();
                    GenericValue gv = delegator.makeValidValue("PartsPm", map);
                    Long id = delegator.getNextSeqId("partPmIndex");
                    gv.put("partPmIndex", id);
                    delegator.create(gv);
                }
                request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
                request.setAttribute("flag", "Y");// ˢ��ĸҳ��
            }
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * ���������趨: ��ѯ���Ϻ�
     * @param request
     * @param response
     * @author qinchao
     * @return action
     */
    public static String queryPartsNoTemplate(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    	String partNo = request.getParameter("partNo");
    	String partName = request.getParameter("partName");
    	String category = request.getParameter("mtrGrp");
    	String eqpType = request.getParameter("eqpType");
    	String periodIndex = request.getParameter("periodIndex");
    	String flowIndex = request.getParameter("flowIndex");
    	String formIndex = request.getParameter("eventIndex");
    	
    	GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
    	String userNo = userLogin.getString("userLoginId");
    	String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);
    	Map hashMap = new HashMap();
    	hashMap.put("deptIndex", deptIndex);
    	boolean flag = false;
    	
    	if (StringUtils.isNotEmpty(category)) {
    	    hashMap.put("category", category.toUpperCase());
    	    flag = true;
    	}
    	if (StringUtils.isNotEmpty(partNo)) {
    	    hashMap.put("partNo", partNo.toUpperCase());
    	    flag = true;
    	}
    	if (StringUtils.isNotEmpty(partName)) {
    	    hashMap.put("partName", partName.toUpperCase());
    	    flag = true;
    	}
    	
    	try {
    	    List list = null;
    	    if (flag) {
//    	    	list = PartsHelper.getPartsNoList(delegator, hashMap);
    	    	list = PartsHelper.getMcsPartsList(delegator, hashMap);
    	    } else {
    	    	list = new ArrayList();
    	    }
    	    request.setAttribute("partsNoList", list);
    	    request.setAttribute("category", category);
    	    request.setAttribute("eqpType", eqpType);
    	    request.setAttribute("periodIndex", periodIndex);
    	    request.setAttribute("flowIndex", flowIndex);
    	    // ȡ��partType
    	 	request.setAttribute("partTypeList", CommonHelper.getPartTypeList(delegator));
    	 	
    	    if(StringUtils.isNotEmpty(formIndex)){
    	        List flowList = delegator.findByAnd("FormJobRelation", UtilMisc.toMap("eventIndex", formIndex));
                request.setAttribute("flowList", flowList);
    	    }
    	} catch (Exception e) {
    	    Debug.logError(module, e.getMessage());
    	    request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
    	    return "error";
    	}
    	return "success";
    }

    /**
     * ��ѯ�����߲߱�������������
     * @param request
     * @param response
     * @author qinchao
     * @return action
     */
    public static String queryPartsNoPMList_(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String eqpId = request.getParameter("eqpId");
        String formIndex = request.getParameter("eventIndex");
        String jobIndex = UtilFormatOut.checkNull(request.getParameter("jobIndex"));
        String category = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
        String partNo = UtilFormatOut.checkNull(request.getParameter("partNo"));
		String ifkey = request.getParameter("ifkey");
        
        try {
	        //�õ�����
	        String userNo = CommonUtil.getUserNo(request);
	        String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);
	        
	        Map hashMap = new HashMap();
	        hashMap.put("deptIndex", deptIndex);
	        hashMap.put("category", category);
	        hashMap.put("partNo", partNo.toUpperCase());
	        hashMap.put("jobIndex", jobIndex);

	        if (StringUtils.isNotEmpty(category) || StringUtils.isNotEmpty(partNo) || StringUtils.isNotEmpty(jobIndex)) {
	        	List list = PartsHelper.getMcsPartsNoList(delegator, hashMap);
	        	request.setAttribute("partsNoList", list);
	        }
            
            //request.setAttribute("category", category);
            //request.setAttribute("eqpType", eqpType);
            //request.setAttribute("periodIndex", periodIndex);
            //request.setAttribute("jobIndex", jobIndex);
            
            if (StringUtils.isNotEmpty(formIndex)) {
                List flowList = delegator.findByAnd("FormJobRelation", UtilMisc.toMap("eventIndex", formIndex));
                request.setAttribute("flowList", flowList);
            }
        } catch (Exception e) {
            Debug.logError(module, e.getMessage());
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
    
	public static String queryPartsNoPMList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String partNo = request.getParameter("partNo");
		String partName = request.getParameter("partName");
		String category = request.getParameter("mtrGrp");
		String eqpId = request.getParameter("eqpId");
		String periodIndex = request.getParameter("periodIndex");
		String flowIndex = UtilFormatOut.checkNull(request.getParameter("flowIndex"));
		String formIndex = request.getParameter("eventIndex");
		String ifkey = request.getParameter("ifkey"); // null?
		try {
			// �õ����źͿα�
			GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
			String userId = userLogin.getString("userLoginId");
			// ����
			String deptIndex = AccountHelper.getUserDeptIndex(delegator, userId);

			Map<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("deptIndex", deptIndex);

			boolean flag = false;
			if (StringUtils.isNotEmpty(category)) {
				hashMap.put("category", category.toUpperCase());
				flag = true;
			}
			if (StringUtils.isNotEmpty(partNo)) {
				hashMap.put("partNo", partNo.toUpperCase());
				flag = true;
			}
			if (StringUtils.isNotEmpty(partName)) {
				hashMap.put("partName", partName.toUpperCase());
				flag = true;
			}
			if (StringUtils.isNotEmpty(ifkey)) {
				hashMap.put("ifkey", ifkey.toUpperCase());
				flag = true;
			}
			if (StringUtils.isNotEmpty(eqpId)) {
				hashMap.put("eqpId", eqpId);
				flag = true;
			}
			List list = null;
			if (flag) {
				list = PartsHelper.getMcsPartsNoList(delegator, hashMap);
			} else {
				list = new ArrayList();
			}
			request.setAttribute("partsNoList", list);
			request.setAttribute("category", category);
			request.setAttribute("eqpId", eqpId);
			request.setAttribute("periodIndex", periodIndex);
			request.setAttribute("flowIndex", flowIndex);
			request.setAttribute("ifkey", ifkey);
			if (StringUtils.isNotEmpty(formIndex)) {
				List flowList = delegator.findByAnd("FormJobRelation", UtilMisc.toMap("eventIndex", formIndex));
				request.setAttribute("flowList", flowList);
			}
			
			// ȡ��partType
			request.setAttribute("partTypeList", CommonHelper.getPartTypeList(delegator));

		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}

        /**
         * ͨ�����������ô��������б�
         *
         * @param request
         * @param response
         * @author qinchao
         */
        public static void queryJsonFlowList(HttpServletRequest request,
    	    HttpServletResponse response) {
    	GenericDelegator delegator = (GenericDelegator) request
    		.getAttribute("delegator");
    	String periodIndex = request.getParameter("periodIndex");
    	JSONObject jsObject = new JSONObject();
    	JSONArray flowNameArray = new JSONArray();
    	JSONArray flowValueArray = new JSONArray();
    	try {
    	    // ���������б�
    	    List flowList = PartsHelper.getFlowList(delegator, periodIndex);
    	    for (int i = 0; i < flowList.size(); i++) {
    		GenericValue gv = (GenericValue) flowList.get(i);
    		flowValueArray.put(gv.getString("jobIndex"));
    		flowNameArray.put(gv.getString("jobName"));
    	    }
    	    jsObject.put("flowNameArray", flowNameArray);
    	    jsObject.put("flowValueArray", flowValueArray);

    	    // ����jsobject
    	    response.getWriter().write(jsObject.toString());
    	} catch (Exception e) {
    	    request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
    	}
      }
        
	private static void getAccountSection(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try {
			GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
			String user = userLogin.getString("userLoginId");
			List list=PartsHelper.getAccountSection(delegator,user);
			GenericValue gv=(GenericValue)list.get(0);
			request.setAttribute("accountSection", gv.getString("accountSection"));
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}
	
	// ��ʼҳ�棬����eqptype �豸����
	public static String keyPartsMaintain(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		try {
			GenericValue userInfo = AccountHelper.getUserInfo(request, delegator);
			String maintDept = userInfo.getString("deptIndex");
			List equipmentTypeList = delegator.findByAnd("EquipmentModel", UtilMisc.toMap("maintDept", maintDept),
					UtilMisc.toList("Model"));

			request.setAttribute("equipmentTypeList", equipmentTypeList);
//			request.setAttribute("flag", "OK");
		} catch (Exception e) {
			Debug.logError(e, module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	/**
	 * ��ѯ�ؼ�������Ϣ ����ѯ��ɾ�����޸�
	 * 
	 * @param
	 * @param
	 * @author dingyuyan
	 * 
	 */
	public static String keyEqpPartsList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		try {
			GenericValue userInfo = AccountHelper.getUserInfo(request, delegator);
			String maintDept = userInfo.getString("deptIndex");
			// java�д��ݵĲ���������ά����ǰ�豸���ౣ���ϴ������ֵ
			String eqpModel = (String) request.getAttribute("eqpModel");
			// ҳ���ϴ��ݵĲ���
			String eqp_Model = (String) request.getParameter("eqp_Model");

			// �������ֵ���У��ϲ�
			if (StringUtils.isNotEmpty(eqp_Model)) {
				eqpModel = eqp_Model;
			}

			StringBuffer sb = new StringBuffer();
			sb.append(
					"  select t1.*,to_char(t1.update_time,'yyyy/MM/dd hh24:mi') updatetime, t2.section,t2.section_index, ");
			sb.append(
					"  case when t1.key_parts_id in (select key_parts_id from key_parts_delay t3 where t1.parts_id=t3.parts_id and t1.keydesc=t3.keydesc) ");
			sb.append("  then 'Y' else 'N' end isdelay");
			sb.append("  from key_eqp_parts t1 left join equipment_section t2  ");
			sb.append("  on t1.notify=t2.section_index ");
			sb.append("  where  eqp_type='").append(eqpModel).append("' ");
			sb.append(" and  maint_dept='").append(maintDept).append("'");
			// sb.append(" and t1.enable='Y'");
			sb.append("  ORDER BY t1.enable desc,t1.update_time desc ");

			List keyList = SQLProcess.excuteSQLQuery(sb.toString(), delegator);

			List SectionList = delegator.findByAnd("EquipmentSection", UtilMisc.toMap("deptIndex", maintDept));
			request.setAttribute("SectionList", SectionList);
			request.setAttribute("keyList", keyList);
			request.setAttribute("eqpModel", eqpModel.replace("+", "aaa"));

			// �жϲ���ҳ���Ƿ���ʾ��flag
			request.setAttribute("flag", "OK");

		} catch (Exception e) {
			Debug.logError(e, module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	// KeyPartsά�� �ӻ���������parts��Ϣ
	public static String queryPartsData(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String category = request.getParameter("mtrGrp");
		String eqpModel = request.getParameter("eqpModel");
		String partNo = request.getParameter("partNo");
		try {
			// �õ����źͿα�
			GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
			String userId = userLogin.getString("userLoginId");
			// ����
			String deptIndex = AccountHelper.getUserDeptIndex(delegator, userId);
			List SectionList = delegator.findByAnd("EquipmentSection", UtilMisc.toMap("deptIndex", deptIndex));
			request.setAttribute("SectionList", SectionList);
			
			Map<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("deptIndex", deptIndex);
			
			boolean flag = false;
			if (StringUtils.isNotEmpty(partNo)) {
				hashMap.put("partNo", partNo.toUpperCase());
				flag = true;
			}
			if (StringUtils.isNotEmpty(category)) {
				hashMap.put("category", category.toUpperCase());
				flag = true;
			}
			List list = null;
			if (flag) {
				list = PartsHelper.getMcsPartsList(delegator, hashMap);
			} else {
				list = new ArrayList();
			}

			
			request.setAttribute("partsNoList", list);
			request.setAttribute("eqpModel", eqpModel);
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	// ��ӹؼ�����
	public static String saveKeyParts(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		Map map = TsFormEvent.getInitParams(request, false, false);
		String eqpModel = request.getParameter("eqpModel");
		GenericValue userInfo = AccountHelper.getUserInfo(request, delegator);
		map.put("transBy", userInfo.get("accountNo"));
		map.put("maintDept", userInfo.getString("deptIndex"));
		try {
			StringBuffer rSB = new StringBuffer();
			PartsHelper.saveKeyPartsInfo(delegator, map, rSB);
			if (rSB.length() > 0) {
				String aaaa = rSB.toString();
				request.setAttribute("_ERROR_MESSAGE_", rSB.toString());
				request.setAttribute("flag", "N");
				request.setAttribute("eqpModel", eqpModel);
			} else {
				request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
				request.setAttribute("flag", "Y");
				request.setAttribute("eqpModel", eqpModel);

			}
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	// �޸� �ؼ�����
	public static String updateKeyParts(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String userNo = CommonUtil.getUserNo(request);
		String maintDept = AccountHelper.getUserDeptIndex(delegator, userNo);
		String eqpModel = request.getParameter("eqpModel");
		
		Map paramMap = new HashMap();
		paramMap.put("maintDept", maintDept);
		paramMap.put("keyPartsId", request.getParameter("keypartsID"));
		paramMap.put("keydesc", request.getParameter("keyDesc"));
		paramMap.put("errorSpec", request.getParameter("errorspec"));
		paramMap.put("warnSpec", request.getParameter("warmspec"));
		paramMap.put("eqpType", request.getParameter("eqpModel"));
		paramMap.put("isAlarm", request.getParameter("ifalarm"));
		paramMap.put("useNumber", request.getParameter("usenumber"));
		paramMap.put("mustchange", request.getParameter("mustchange"));
		paramMap.put("enable", request.getParameter("enable"));
		paramMap.put("notify", request.getParameter("section"));
		paramMap.put("updateTime", UtilDateTime.nowTimestamp());
		paramMap.put("updateUser", userNo);
		try {
			PartsHelper.updateKeyPartsInfo(delegator, paramMap);
			
			request.setAttribute("eqpModel", eqpModel);
			request.setAttribute("_EVENT_MESSAGE_", "�޸ĳɹ���");
		} catch (Exception e) {
			Debug.logError(e, module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";

	}
	
	// ɾ���ؼ�����
	public static String deleteKeyParts(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String userNo = CommonUtil.getUserNo(request);
		String maintDept = AccountHelper.getUserDeptIndex(delegator, userNo);
		// ȡ��ҳ�����
		String keyPartsId = request.getParameter("keyPartsId");
		String eqpModel = request.getParameter("eqpModel");
		try {

			EntityWhereString con = new EntityWhereString(" key_parts_id = '" + keyPartsId + "'");
			List list = delegator.findByCondition("KeyPartsUse", con, null, null);
			if (list.size() > 0) {
				// throw new Exception("�ùؼ������ѱ�ʹ�ã�����ɾ��");
				request.setAttribute("eqpModel", eqpModel);
				request.setAttribute("_EVENT_MESSAGE_", "�ùؼ������ѱ�ʹ�ã�����ɾ����");
				return "error";
			}

			GenericValue gv = delegator.findByPrimaryKey("KeyEqpParts", UtilMisc.toMap("keyPartsId", keyPartsId));
			
			delegator.removeByAnd("KeyEqpParts", UtilMisc.toMap("keyPartsId", keyPartsId));
			
			Map histMap = gv.getAllFields();
			Long histId = delegator.getNextSeqId("keyEqpPartsHistId");
			histMap.put("histId", histId);
			histMap.put("action", "delete");
			histMap.put("maintDept", maintDept);
			histMap.put("updateUser", userNo);
			histMap.put("updateTime", UtilDateTime.nowTimestamp());
			gv = delegator.makeValidValue("KeyEqpPartsHist", histMap);
			delegator.create(gv);

			request.setAttribute("eqpModel", eqpModel);
			request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
		} catch (Exception e) {
			Debug.logError(e, module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	// ��ȡ����������
	public static String queryKeyPartsDelayList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String keyPartsId = (String) request.getParameter("keyPartsId");
		try {
			List keyPartsinfo = delegator.findByAnd("KeyEqpParts", UtilMisc.toMap("keyPartsId", keyPartsId));
			String sql = "select * from key_parts_delay t where key_parts_id='" + keyPartsId
					+ "' order by delay_item asc";
			List keyPartsDelayList = SQLProcess.excuteSQLQuery(sql, delegator);
			// List keyPartsDelayList = delegator.findByAnd("KeyPartsDelay",
			// UtilMisc.toMap("keyPartsId", keyPartsId));
			request.setAttribute("keyPartsinfo", keyPartsinfo);
			request.setAttribute("keyPartsDelayList", keyPartsDelayList);
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
		}
		return "success";
	}
	
	public static String saveKeyPartsDelay(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		int size = Integer.parseInt(request.getParameter("size"));
		String keyPartsId = request.getParameter("keyPartsId");
		String partsId = request.getParameter("partsId_0");
		String keydesc = request.getParameter("keydesc_0");
		List removelist = new ArrayList();
		try {
			String itemsql = "select delay_item from key_parts_delay t where key_parts_id='" + keyPartsId + "'";
			List pklist = SQLProcess.excuteSQLQuery(itemsql, delegator);
			List itemlist = new ArrayList();
			for (int p = 0; p < pklist.size(); p++) {
				Map itemmap = (Map) pklist.get(p);
				String item = (String) itemmap.get("DELAY_ITEM");
				itemlist.add(item);
			}

			for (int i = 0; i < size; i++) {
				Map map = new HashMap();
				int indexNum = i + 1;
				String delayItem = request.getParameter("delayItem_" + indexNum);
				String delaySpec = request.getParameter("delaySpec_" + indexNum);
				String delayRule = request.getParameter("delayRule_" + indexNum);
				String keyPartsDelayId = request.getParameter("keyPartsDelayId_" + indexNum);
				map.put("keyPartsId", keyPartsId);
				map.put("partsId", partsId);
				map.put("keydesc", keydesc);
				map.put("delayItem", delayItem);
				map.put("delaySpec", delaySpec);
				map.put("delayRule", delayRule);
				List list = delegator.findByAnd("KeyPartsDelay",
						UtilMisc.toMap("keyPartsId", keyPartsId, "delayItem", delayItem));
				if (list == null || list.size() == 0) {

					String sql = "select max(key_parts_delay_id) from key_parts_delay t";
					List maxpklist = SQLProcess.excuteSQLQuery(sql, delegator);
					Map keyPartsDelayIdMap = (Map) maxpklist.get(0);
					String pkindex = (String) keyPartsDelayIdMap.get("MAX(KEY_PARTS_DELAY_ID)");
					if (pkindex == null || pkindex.equals("")) {
						keyPartsDelayId = "1";
					} else {
						int pkNum = Integer.parseInt(pkindex) + 1;
						keyPartsDelayId = (pkNum + "").trim();
					}
					map.put("keyPartsDelayId", keyPartsDelayId);
					GenericValue gv = delegator.makeValidValue("KeyPartsDelay", map);
					delegator.create(gv);
				} else {
					map.put("keyPartsDelayId", keyPartsDelayId);
					delegator.removeByAnd("KeyPartsDelay",
							UtilMisc.toMap("keyPartsId", keyPartsId, "delayItem", delayItem));
					GenericValue gv = delegator.makeValidValue("KeyPartsDelay", map);
					delegator.create(gv);
				}
				removelist.add(delayItem);
			}
			itemlist.removeAll(removelist);
			if (itemlist != null || itemlist.size() != 0) {
				for (int k = 0; k < itemlist.size(); k++) {
					delegator.removeByAnd("KeyPartsDelay",
							UtilMisc.toMap("keyPartsId", keyPartsId, "delayItem", itemlist.get(k)));
				}
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";

	}
	
	public static String deleteKeyPartsDelayItem(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String keyPartsDelayId = request.getParameter("keyPartsDelayId");
		try {
			if (!keyPartsDelayId.equals("undefined")) {
				delegator.removeByAnd("KeyPartsDelay", UtilMisc.toMap("keyPartsDelayId", keyPartsDelayId));
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	public static String technicalSparePartsSet(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		try {
			List equipmentTypeList = delegator.findAll("EquipmentType");
			request.setAttribute("equipmentTypeList", equipmentTypeList);
		} catch (Exception e) {
			Debug.logError(e, module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}

	/**
	 * ���ı����趨һ������:ȡ�ù��ڼ��ı�����Ϣ��������ʾ
	 * 
	 * @param request
	 *            eqpType �豸����
	 * @param response
	 * @return String success/error
	 */
	public static String technicalSparePartsList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		try {
			// java�д��ݵĲ���������ά����ǰ�豸���ౣ���ϴ������ֵ
			String eqpType = (String) request.getAttribute("eqpType");
			// ҳ���ϴ��ݵĲ���
			String eqp_Type = (String) request.getParameter("eqp_Type");
			// �������ֵ���У��ϲ�
			if (!StringUtils.isEmpty(eqp_Type)) {
				eqpType = eqp_Type;
			}

			// �鿴���豸�����Ƿ����
//			List equipmentTypeList = delegator.findByAnd("EquipmentType", UtilMisc.toMap("type", "1"));
			List equipmentTypeList = delegator.findAll("EquipmentType");

			// ���ڣ�ȡ�ô˴��ౣ����������Ϣ
			String whereStr = "mtr_num like '2T" + eqpType + "%' and QTY>0 ";
			EntityWhereString con = new EntityWhereString(whereStr);
			List technicalSparePartsList = delegator.findByCondition("McsMaterialStoReq", con, null,
					UtilMisc.toList("periodName"));

			// ȡ��ϵͳ�Զ��屣����������
//			List equipmentPeriodList = delegator.findAll("EquipmentCycDefault");
//			request.setAttribute("equipmentPeriodList", equipmentPeriodList);
			
			request.setAttribute("technicalSparePartsList", technicalSparePartsList);
			request.setAttribute("equipmentTypeList", equipmentTypeList);
			request.setAttribute("eqpType", eqpType);

			// �жϲ���ҳ���Ƿ���ʾ��flag
			request.setAttribute("flag", "OK");

		} catch (Exception e) {
			Debug.logError(e, module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}

	/**
	 * �豸���������½��򱣴�
	 * 
	 * @param request
	 *            periodIndex��equipmentId��periodName,periodDesc,eqpType,
	 *            defaultDays,standardHour,eqpStatus,warningDays,isUpdatePromis
	 * @param response
	 * @return String success/error
	 */
	public static String manageTechnicalSpareParts(HttpServletRequest request, HttpServletResponse response) {
		// �����ϴ��ݵ����в�����ϳ�Map
		Map paramMap = TsFormEvent.getInitParams(request, true, true);

		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String eqpType = request.getParameter("eqpType");
		try {
			// ȡ���û�
			String userNo = CommonUtil.getUserNo(request);
			String deptName = AccountHelper.getUserInfo(request, delegator).getString("accountDept");
			// String deptIndex = userInfo.getString("deptIndex");
			String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);
			String mtrDesc = request.getParameter("mtrDesc");
			String mtrNum = request.getParameter("mtrNum");
			paramMap.put("costCenter", deptName);
			paramMap.put("deptIndex", deptIndex);
			// ����ʱ�丳ֵ
			paramMap.put("updateTime", UtilDateTime.nowTimestamp());
			
			paramMap.put("plant", Constants.CALL_TP_FLAG ? "1100" : "1000");

			// ����materialStoReqIndex�ж��Ƿ�Ϊ���������
			if (StringUtils.isEmpty((String) paramMap.get("materialStoReqIndex"))) {
				String mtrNumTemp = request.getParameter("mtrNumTemp");
				// ���ݴ����ж��Ƿ��ü��ı����ţ����½���������
				String mtrNumIndex = PartsHelper.getMtrNumIndex(delegator, mtrNumTemp);
				if (StringUtils.isEmpty(mtrNumIndex)) {
					mtrNumIndex = mtrNumTemp + "000001";
				} else {
					// ��ȡ����������ۼ�
					String mtrNumIndexTemp = mtrNumIndex.substring(mtrNumTemp.length(), mtrNumIndex.length());
					int i = Integer.parseInt(mtrNumIndexTemp) + 1;
					String s = String.format("%06d", i);
					mtrNumIndex = mtrNumTemp + s;
				}
				paramMap.put("mtrNum", mtrNumIndex);

				// �������ֶβ����±�Parts_Data
				PartsHelper.insertPartsData(delegator, mtrNumIndex, mtrDesc, userNo);
			} else {
				// �����ֶ���
				PartsHelper.updatePartsData(delegator, mtrNum, mtrDesc, userNo);
			}
			// ����Ϣд���豸�������ڱ��Parts_Data
			PartsHelper.createDefaultTechnicalSpare(delegator, paramMap);

			// ��ʾ���豸��������һ����flag
			request.setAttribute("flag", "OK");
			// save��ȡ eqpType
			request.setAttribute("mtrNum", paramMap.get("mtrNum"));
			request.setAttribute("eqpType", eqpType);
			request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");

		} catch (GenericEntityException e) {
			String message = CommonUtil.checkOracleException(e);
			request.setAttribute("_ERROR_MESSAGE_", message);
		} catch (Exception e) {
			Debug.logError(e, module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}

	/**
	 * ɾ���豸�������ڣ���������
	 * 
	 * @param request
	 *            periodIndex��eqpType
	 * @param response
	 * @return String : success/error
	 */
	public static String deleteTechnicalSpareParts(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

		// ȡ��ҳ�����
		String materialStoReqIndex = request.getParameter("materialStoReqIndex");
		String eqpType = request.getParameter("eqpType");

		try {
			// ��ȡ���ı�������ɾ����Parts_Data����
			String mtrNumtemp = PartsHelper.getMtrNumByMaterialStoReqIndex(delegator, materialStoReqIndex);

			EntityWhereString con = new EntityWhereString("part_no='" + mtrNumtemp + "'");
			List list = delegator.findByCondition("PartsUse", con, null, null);
			if (list.size() > 0) {
				throw new Exception("�ñ����ѱ�ʹ�ã�����ɾ��");

			}
			PartsHelper.deletePartsData(delegator, mtrNumtemp);

			delegator.removeByAnd("McsMaterialStoReq", UtilMisc.toMap("materialStoReqIndex", materialStoReqIndex));

			request.setAttribute("eqpType", eqpType);
			request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");

		} catch (Exception e) {
			Debug.logError(e, module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	/**
	 * �����ϵı༭��ť��ѯֵ
	 * 
	 * @param request
	 *            periodIndex
	 * @param response
	 */
	public static void queryTechnicalSparePartsByIndex(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

		// materialStoReqIndexȡ��
		String materialStoReqIndex = request.getParameter("materialStoReqIndex");
		String eqpType = request.getParameter("eqpType");

		try {
			// ȡ�û�̨������Ϣ
			GenericValue gv = delegator.findByPrimaryKey("McsMaterialStoReq",
					UtilMisc.toMap("materialStoReqIndex", materialStoReqIndex));
			JSONObject defaultTechnicalSparePartsParam = new JSONObject();
			defaultTechnicalSparePartsParam.put("plant", gv.getString("plant"));
			defaultTechnicalSparePartsParam.put("mtrNum", gv.getString("mtrNum"));
			defaultTechnicalSparePartsParam.put("mtrDesc", gv.getString("mtrDesc"));
			defaultTechnicalSparePartsParam.put("mtrGrp", gv.getString("mtrGrp"));
			defaultTechnicalSparePartsParam.put("batchNum", gv.getString("batchNum"));
			defaultTechnicalSparePartsParam.put("qty", gv.getString("qty"));
			defaultTechnicalSparePartsParam.put("costCenter", gv.getString("costCenter"));
			defaultTechnicalSparePartsParam.put("updateTime", gv.getString("updateTime"));
			defaultTechnicalSparePartsParam.put("eqpType", eqpType);

			// д��response
			response.getWriter().write(defaultTechnicalSparePartsParam.toString());
		} catch (Exception e) {
			Debug.logError(e, module);
		}
	}
	
	// �ؼ������»�ά��
	public static String keyPartsUnuse(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String userNo = CommonUtil.getUserNo(request);
		String maintDept = AccountHelper.getUserDeptIndex(delegator, userNo);
		try {
			List equipmentTypeList = delegator.findByAnd("EquipmentModel", UtilMisc.toMap("maintDept", maintDept),
					UtilMisc.toList("Model"));
			request.setAttribute("equipmentTypeList", equipmentTypeList);
		} catch (Exception e) {
			Debug.logError(e, module);
		}
		return "success";
	}

	// �ؼ������»�ά����ѯ
	public static String queryKeyPartsUnuseList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String model = request.getParameter("eqp_Model");
		String eqpid = request.getParameter("equipmentId");
		String sql0 = "";
		if (StringUtils.isNotEmpty(model)) {
			sql0 = sql0 + " and t3.model like '" + model + "'";
		}
		if (StringUtils.isNotEmpty(eqpid)) {
			sql0 = sql0 + " and t2.eqp_id like '" + eqpid + "'";
		}
		String sql1 = "select t.pm_index t_index,t.pm_name t_name,t1.event_type,t1.event_index,t.status,t3.model,t4.parts_id,t4.parts_name,t4.keydesc,nvl(t1.part_count,0) use_number,t4.limit_type,t2.* from pm_form t "
				+ " left join (select * from parts_use where event_type='PM')t1 on t1.event_index=t.pm_index "
				+ " left join key_parts_use t2 on t2.parts_use_id=t1.seq_index "
				+ " left join equipment t3 on t2.eqp_id=t3.equipment_id "
				+ " left join key_eqp_parts t4 on t4.key_parts_id=t2.key_parts_id "
				+ " where t2.status='USING' and t.STATUS = 1 ";
		String sql2 = "select t.abnormal_index t_index,t.abnormal_name t_name,t1.event_type,t1.event_index,t.status,t3.model,t4.parts_id,t4.parts_name,t4.keydesc,nvl(t1.part_count,0) use_number,t4.limit_type,t2.* from abnormal_form t "
				+ " left join (select * from parts_use where event_type='TS')t1 on t1.event_index=t.abnormal_index "
				+ " left join key_parts_use t2 on t2.parts_use_id=t1.seq_index "
				+ " left join equipment t3 on t2.eqp_id=t3.equipment_id "
				+ " left join key_eqp_parts t4 on t4.key_parts_id=t2.key_parts_id "
				+ " where t2.status='USING' and t.STATUS = 1 ";
		String sql = sql1 + sql0 + " union all " + sql2 + sql0;
		try {
			List keyPartsUnuseList = SQLProcess.excuteSQLQuery(sql, delegator);
			request.setAttribute("keyPartsUnuseList", keyPartsUnuseList);
			request.setAttribute("eqp_Model", model);
			request.setAttribute("equipmentId", eqpid);
			request.setAttribute("flag", "OK");
		} catch (Exception e) {
			Debug.logError(e, module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}

	/*
	 * ͨ��eqpModel���eqpid
	 */
	public static void getEqpIdByModelList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String equipmodel = request.getParameter("equipmodel").toUpperCase();
		JSONArray eqpArray = new JSONArray();
		JSONArray keydescArray = new JSONArray();
		JSONObject jsObject = new JSONObject();
		Debug.logInfo("Model......"+equipmodel, module);
		try {
			// �õ�eqpId�б�
			List eqpList = PartsHelper.getEquipIDListbyModel(delegator, equipmodel.replace("AAA", "+"));
			List keydescList = PartsHelper.getkeydescListbyModel(delegator, equipmodel.replace("AAA", "+"));
			for (int i = 0; i < keydescList.size(); i++) {
				Map map = new HashMap();
				map = (Map) keydescList.get(i);
				keydescArray.put(map.get("KEYDESC"));
			}
			for (int i = 0; i < eqpList.size(); i++) {
				GenericValue gv = (GenericValue) eqpList.get(i);
				eqpArray.put(gv.getString("equipmentId"));
			}
			jsObject.put("eqpIdArray", eqpArray);
			jsObject.put("keydescArray", keydescArray);
			// ����jsobject
			response.getWriter().write(jsObject.toString());
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}

	/**
	 * PARTS��ѯ
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String queryPartsList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String eqpId = request.getParameter("eqpId");
		String periodIndex = request.getParameter("periodIndex");
		String partType = request.getParameter("partType");
		String pregion = request.getParameter("pregion");
		String partNo = request.getParameter("partNo");
		String formIndex = request.getParameter("eventIndex");
		partType = StringUtils.isEmpty(partType) ? "PM" : partType;
		Map parMap = new HashMap();
		parMap.put("eqpId", eqpId);
		parMap.put("periodIndex", periodIndex);
		parMap.put("partType", partType);
		parMap.put("pregion", pregion);
		parMap.put("partNo", partNo);
		try {
			GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
			String accountNo = userLogin.getString("userLoginId");
			GenericValue account = AccountHelper.getAccountByNo(delegator, accountNo);
			request.setAttribute("accountSection", account.get("accountSection"));
			List partsNoList = PartsHelper.queryPartsListInForm(delegator, parMap);
			request.setAttribute("partsNoList", partsNoList);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}

	/**
	 * PARTS����
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String updatePartsUse(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String userNo = CommonUtil.getUserNo(request);
		String seqIndex = request.getParameter("seqIndex");
		String partCount = request.getParameter("partCount");
		String templateCount = request.getParameter("templateCount");
		String partType = request.getParameter("partType");
		String remark = request.getParameter("remark");
		String materialIndex = request.getParameter("materialIndex");
		if (templateCount.equals("")) {
			templateCount = "0";
		}
		Map parMap = new HashMap();
		parMap.put("seqIndex", seqIndex);
		// parMap.put("templateCount",Double.valueOf(templateCount));
		parMap.put("partCount", Double.valueOf(partCount));
		parMap.put("partType", partType);
		parMap.put("remark", remark);
		parMap.put("transBy", userNo);
		parMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
		Map parMap2 = new HashMap();
		parMap2.put("recipient", userNo);
		parMap2.put("materialStoReqIndex", materialIndex);
		parMap2.put("updateTime", new Timestamp(System.currentTimeMillis()));
		try {
			// ��������������Ϣ
			PartsHelper.updatePartsUse(delegator, parMap);
			if (!materialIndex.endsWith("null")) {
				if (partType.equals("��Ʒ")) {
					parMap2.put("qty", (-1) * Float.valueOf(partCount));
				} else {
					parMap2.put("qty", 0);
				}
				PartsHelper.updateMaterial(delegator, parMap2);
			}
			// �豸ҳ��Ĭ�ϵĴ򿪵�TABS
			request.setAttribute("activate", "parts");
			request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	/**
	 * ����һ�㱸��PARTS
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String savePartsUseInfo(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		Map map = TsFormEvent.getInitParams(request, false, false);
		String userNo = CommonUtil.getUserNo(request);
		String eqpId = request.getParameter("eqpId");
		String eventIndex = request.getParameter("eventIndex");
		String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);
		map.put("transBy", userNo);
		map.put("deptIndex", deptIndex);
		map.put("eventType", Constants.PM);
//		map.put("status", ConstantsMcs.USING);
		map.put("eqpId", eqpId);
		map.put("category", request.getParameter("type"));
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
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	/**
	 * ����PART_NO��ѯ�ֿ߲߱��ÿ��������Ϣ
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String queryPartBatchNum(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		try {
			String partNo = request.getParameter("partNo");
			String partsUseId = request.getParameter("partsUseId");
			String eventIndex = request.getParameter("eventIndex");
			String eqpId = request.getParameter("eqpId");
			GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
			String userNo = userLogin.getString("userLoginId");
			String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);
			StringBuffer msrQuery = new StringBuffer();
			msrQuery.append("select material_sto_req_index, mtr_num, mtr_desc, batch_num, qty - nvl(active_qty, 0) stock_qty ");
			msrQuery.append("  from mcs_material_sto_req ");
			msrQuery.append(" where mtr_num = '").append(partNo).append("' ");
			msrQuery.append("   and dept_index = '").append(deptIndex).append("' "); 
			msrQuery.append("   and qty - nvl(active_qty, 0) > 0");
			msrQuery.append("   and active_flag = 'N'");

			List partBatchNumList = SQLProcess.excuteSQLQuery(msrQuery.toString(), delegator);
			request.setAttribute("partBatchNumList", partBatchNumList);
			request.setAttribute("partsUseId", partsUseId);
			request.setAttribute("eventIndex", eventIndex);
			request.setAttribute("eqpId", eqpId);
			
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	/**
	 * ����PARTS_USE�б���������Ϣ
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String saveBatchNumForPartsUse(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		try {
			String reqIndex = request.getParameter("reqIndex");
			String partsUseId = request.getParameter("partsUseId");
			String eqpId = request.getParameter("eqpId");
			
			GenericValue gv = delegator.findByPrimaryKey("PartsUse", UtilMisc.toMap("seqIndex", partsUseId));
			gv.put("partsRequireIndex", reqIndex);
			gv.store();

			request.setAttribute("formreturn", "new");
			request.setAttribute("flag", "Y");
			request.setAttribute("eventType", gv.get("eventType"));
			request.setAttribute("eventIndex", gv.get("eventIndex").toString());
			request.setAttribute("eqpId", eqpId);
			request.setAttribute("activate", "parts");
			
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	//��ȡ��ǰ������������
    public static void queryKeyPartsDelayInfo(HttpServletRequest request, HttpServletResponse response){
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String keyPartsId=(String) request.getParameter("keyPartsId");			
		String keyUseId=(String) request.getParameter("keyUseId");
		String keyUseIdUsed=(String) request.getParameter("keyUseIdUsed");		
		String eventIndex=request.getParameter("eventIndex");
		String nameIndex=request.getParameter("nameIndex");
		String actul=request.getParameter("actul");
		if(keyUseId.equals("0")&&!keyUseIdUsed.equals("0")){
			keyUseId=keyUseIdUsed;
		}else if(keyUseId.equals("0")&&keyUseIdUsed.equals("0")){
			return ;
		}		
		try {
			String sql0="select NVL(t3.delaytime, 0) delaytime,t2.*,t1.* from key_parts_use t, key_eqp_parts t1,key_parts_delay_info t2, "
					+" (select count(*) delaytime,key_use_id from key_parts_delay_info group by key_use_id) t3 "
					+ " where t.key_parts_id = t1.key_parts_id and t1.key_parts_id=t2.key_parts_id and t.key_use_id=t3.key_use_id and t.key_use_id ='"+keyUseId+"' and t2.event_index = '"+eventIndex+"'";
			List keyPartsInfo=SQLProcess.excuteSQLQuery(sql0, delegator);
			if(keyPartsInfo.size()==0||keyPartsInfo==null){
				sql0="select * from key_eqp_parts "
						+ " left join (select count(*) from key_parts_delay_info where key_use_id='"+keyUseId+"') on 1=1"
						+ " where key_parts_id='"+keyPartsId+"'";
				keyPartsInfo=SQLProcess.excuteSQLQuery(sql0, delegator);
			}
			
			Map keyPartsMap=new HashMap();
			JSONArray keyPartsJson = new JSONArray();
			for(int k=0;k<keyPartsInfo.size();k++){
				JSONObject object = new JSONObject();
				keyPartsMap=(Map)keyPartsInfo.get(k);
				object.put("partsId",keyPartsMap.get("PARTS_ID"));
				object.put("partsName",keyPartsMap.get("PARTS_NAME"));
				object.put("keydesc",keyPartsMap.get("KEYDESC"));
				object.put("delaytime",keyPartsMap.get("DELAYTIME"));
				object.put("limitType",keyPartsMap.get("LIMIT_TYPE"));
				object.put("delayLife",keyPartsMap.get("DELAY_LIFE"));
				object.put("errorSpec",keyPartsMap.get("ERROR_SPEC"));
				object.put("keyPartsId",keyPartsId);
				object.put("eventIndex",eventIndex);
				object.put("keyUseId",keyUseId);
				object.put("keyUseIdUsed",keyUseIdUsed);
				object.put("nameIndex",nameIndex);
				object.put("actul",actul);
				keyPartsJson.put(object);
			}
			JSONObject keyPartsObject=new JSONObject();
			keyPartsObject.put("keyPartsJson", keyPartsJson);
			
			String sql="select * from keypartsdelay_result t1   left join key_parts_delay_info t2 on t2.key_parts_id=t1.key_parts_id and t1.event_index=t2.event_index where t1.delay_table_id=t2.delay_table_id and t1.delay_table_id = "
					+" (select delay_table_id from key_parts_delay_info t where t.key_parts_id='"+keyPartsId+"' and t.event_index='"+eventIndex+"') and t1.key_parts_id='"+keyPartsId+"' and t1.event_index='"+eventIndex+"' order by delay_item asc";
			
			List keyPartsDelayList=SQLProcess.excuteSQLQuery(sql, delegator);
			if(keyPartsDelayList==null||keyPartsDelayList.size()==0){
				sql="select * from key_parts_delay  where key_parts_id='"+keyPartsId+"' order by delay_item asc";
				keyPartsDelayList=SQLProcess.excuteSQLQuery(sql, delegator);				
			}			
			JSONArray keyPartsDelayJson = new JSONArray();			
			Map keyPartsDelayMap=new HashMap();
			for (int i=0;i<keyPartsDelayList.size(); i++) {
				keyPartsDelayMap=(Map)keyPartsDelayList.get(i);
				JSONObject object = new JSONObject();
				object.put("delayItem",keyPartsDelayMap.get("DELAY_ITEM"));
				object.put("delaySpec",keyPartsDelayMap.get("DELAY_SPEC"));
				object.put("delayRule",keyPartsDelayMap.get("DELAY_RULE"));
				object.put("delayResult",keyPartsDelayMap.get("DELAY_RESULT"));
				object.put("limitType",keyPartsDelayMap.get("LIMIT_TYPE"));
				object.put("delayLife",keyPartsDelayMap.get("DELAY_LIFE"));
				keyPartsDelayJson.put(object);
			}
			JSONObject keyPartsDelayObject=new JSONObject();
			keyPartsDelayObject.put("keyPartsDelayJson", keyPartsDelayJson);
			JSONArray keyPartsDelayInfoJson = new JSONArray();
			keyPartsDelayInfoJson.put(keyPartsObject);
			keyPartsDelayInfoJson.put(keyPartsDelayObject);
			response.getWriter().write(keyPartsDelayInfoJson.toString());
			//List keyPartsDelayList = delegator.findByAnd("KeyPartsDelay", UtilMisc.toMap("keyPartsId", keyPartsId));
			//request.setAttribute("keyPartsinfo", keyPartsinfo);
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
		}
    }
    
    public static void saveKeyPartsDelayInfo(HttpServletRequest request, HttpServletResponse response){
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    	String userNo=CommonUtil.getUserNo(request);
    	Date date=new Date();
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String updateTime=sdf.format(date);
    	String d_str=sdf.format(date).substring(0, 10).replaceAll("-", "").trim();
    	String keyPartsId=request.getParameter("keyPartsId_1");	
		String partsId= request.getParameter("partsId_1");
		String limitType=request.getParameter("limitType_1");
		String delayLife=request.getParameter("delayLife_1");
		String delaytime=request.getParameter("delaytime_1");
		String keyUseId=request.getParameter("keyUseId_1");
		String keyUseIdUsed=request.getParameter("keyUseIdUsed_1");
		String eventIndex=request.getParameter("eventIndex_1");
		String keydesc=request.getParameter("keydesc_1");
		String nameIndex=request.getParameter("nameIndex_1");
		String delayItems="";
		String delayTableId="";
		JSONObject dKeyPartsObject = new JSONObject();
		if(keyUseId.equals("0")&&!keyUseIdUsed.equals("0")){
			keyUseId=keyUseIdUsed;
		}else if(keyUseId.equals("0")&&keyUseIdUsed.equals("0")){
			return ;
		}
		Map map=new HashMap();				
		map.put("keyPartsId", keyPartsId);
    	map.put("partsId",partsId);
    	map.put("limitType",limitType);
    	map.put("delayLife",delayLife);
    	map.put("eventIndex",eventIndex);
    	map.put("keyUseId", keyUseId);
    	int size=Integer.parseInt(request.getParameter("dsize"));
    	try {
    		String sql0="select * from key_parts_delay_info where event_index='"+eventIndex+"' and key_parts_id='"+keyPartsId+"'";
    		List elist = SQLProcess.excuteSQLQuery(sql0, delegator);
    		if(elist.size()!=0){
    			Map tIdMap=(Map) elist.get(0);
    			delayTableId=(String) tIdMap.get("DELAY_TABLE_ID");
    			delegator.removeByAnd("KeyPartsDelayInfo", UtilMisc.toMap("delayTableId", delayTableId,"eventIndex",eventIndex));
    			delegator.removeByAnd("KeypartsdelayResult", UtilMisc.toMap("delayTableId", delayTableId,"eventIndex",eventIndex));
    			
    		}else{
	    		String sql = "select t.delay_table_id from key_parts_delay_info t where t.event_index='"+eventIndex+"' and t.delay_table_id like '%"+d_str+"%' order by t.delay_table_id desc";
				List list = SQLProcess.excuteSQLQuery(sql, delegator);
				if(list.size()==0||list==null){
					delayTableId=d_str+"A";
				}else{
					Map tIdMap=(Map)list.get(0);
					String tIdIndex=((String) tIdMap.get("DELAY_TABLE_ID")).substring(8);
					char[] tIdIndexArray=tIdIndex.toCharArray();
					char index1=(char) (tIdIndexArray[0]+1);
					delayTableId=d_str+index1;
				}
				if(delaytime.equals("null")||delaytime.equals("")){
					delaytime="1";
				}else{
					int t=Integer.parseInt(delaytime)+1;
					delaytime=t+"";
				}
				String dTimesql=" update key_parts_use  set delaytime='"+delaytime.trim()+"' where key_use_id='"+keyUseId+"' ";
				int s= SQLProcess.excuteSQLUpdate(dTimesql, delegator);
    		}
	    	for(int i=0;i<size;i++){
	    		Map resultMap=new HashMap();
	    		int indexNum=i+1;    	
	    		String delayItem=request.getParameter("delayItem_"+indexNum);
	    		String delaySpec=request.getParameter("delaySpec_"+indexNum);
	    		String delayRule=request.getParameter("delayRule_"+indexNum);
	    		String delayResult=request.getParameter("delayResult_"+indexNum);
	    		if(i==0){
	    			delayItems=delayItems+delayItem;
	    		}else{
	    			delayItems=delayItems+","+delayItem;
	    		}
	    		resultMap.put("eventIndex", eventIndex);
	    		resultMap.put("keyPartsId", keyPartsId);
	    		resultMap.put("keydesc", keydesc);
	    		resultMap.put("delayTableId", delayTableId);
	    		resultMap.put("delayItem",delayItem);
	    		resultMap.put("delaySpec",delaySpec);
	    		resultMap.put("delayRule",delayRule);
	    		resultMap.put("delayResult",delayResult);
	    		GenericValue gv = delegator.makeValidValue("KeypartsdelayResult", resultMap);	                 
				delegator.create(gv);
	    	}
	    	map.put("keydesc", keydesc);
	    	map.put("delayTableId",delayTableId.trim());
	    	map.put("delayItems",delayItems.trim());
	    	GenericValue gv = delegator.makeValidValue("KeyPartsDelayInfo", map);
	    	gv.put("updateUser", userNo);
	    	gv.put("updateTime",new Timestamp(System.currentTimeMillis()));
			delegator.create(gv);
			
			//List plist=delegator.findByAnd("PartsUse", UtilMisc.toMap("partNo", partsId,"eventIndex",eventIndex));
			//if(plist.size()==0||plist==null){
			//gv = delegator.makeValidValue("PartsUse", map);                     
            //Long seqIndex = delegator.getNextSeqId("partsUseSeqIndex");
            //gv.put("seqIndex", seqIndex);
            //delegator.create(gv); 
            //String dParTysql=" update key_parts_use  set parts_use_id='"+seqIndex+"'  where key_use_id='"+keyUseId+"' ";
			//int s= SQLProcess.excuteSQLUpdate(dParTysql, delegator);
			//}
			//String dParTysql=" update key_parts_use  set parts_type='DELAY'  where key_use_id='"+keyUseId+"' ";
			//int s= SQLProcess.excuteSQLUpdate(dParTysql, delegator);
			
			List dKeyParts=delegator.findByAnd("KeyPartsUse", UtilMisc.toMap("keyUseId", keyUseId));
			for(int i=0;i<dKeyParts.size();i++){
				Map dkMap=(Map) dKeyParts.get(i);
				dKeyPartsObject.put("partsType", dkMap.get("partsType"));
				dKeyPartsObject.put("vendor", dkMap.get("vendor"));
				dKeyPartsObject.put("seriesNo", dkMap.get("seriesNo"));
				dKeyPartsObject.put("baseSn", dkMap.get("baseSn"));
				dKeyPartsObject.put("initLife", dkMap.get("initLife"));
				dKeyPartsObject.put("offLine", dkMap.get("offLine"));
				dKeyPartsObject.put("delaytime", delaytime);
				dKeyPartsObject.put("remark", dkMap.get("remark"));
				dKeyPartsObject.put("nameIndex", nameIndex);
			}
			dKeyPartsObject.put("delayRst", "���ڱ���ɹ�");
		} catch (Exception e) {
            Debug.logError(module, e.getMessage());
			dKeyPartsObject.put("delayRst", "���ڱ������:"+e.getMessage());
		}   	    	
    	try {
			response.getWriter().write(dKeyPartsObject.toString());
		} catch (IOException e) {
			Debug.logError(module, e.getMessage());
		}
    }
    
    /**
	 * �ؼ�������ϴ��ά��
	 * @param request
	 * @param response
	 * @return
	 */
	public static String keyEqpPartsCleanMaintain(HttpServletRequest request, HttpServletResponse response){
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String userNo = CommonUtil.getUserNo(request);
		String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);
        try {
			List modelList = delegator.findByAnd("EquipmentModel", UtilMisc.toMap("maintDept", deptIndex), UtilMisc.toList("Model"));
			request.setAttribute("modelList", modelList);
		} catch (GenericEntityException e) {
			Debug.logError(module, e.getMessage());
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
        
		return "success";
	}
	
	public static String keyPartsCleanList(HttpServletRequest request, HttpServletResponse response){
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		GenericValue userInfo = AccountHelper.getUserInfo(request,delegator);
        String userId=userInfo.getString("accountNo");
        String deptIndex = userInfo.getString("deptIndex");
		String model=request.getParameter("model");
		String keydesc=request.getParameter("keydesc");
		String sql="select DISTINCT t.*,t1.key_parts_id,t1.parts_id,t1.parts_name,t1.keydesc,t1.eqp_type, "
				+ " case when t.key_parts_clean_id in(select distinct key_parts_clean_id from key_parts_use) then 'Y' else 'N' end inuse  from key_eqp_parts_clean t "
				+ " left join key_eqp_parts t1 on t.key_parts_id=t1.key_parts_id where t1.enable='Y' ";
		String sqlcondition="";
		if(StringUtils.isNotEmpty(model)){
			sqlcondition=" and t1.eqp_type='"+model+"' ";
		}
		if(StringUtils.isNotEmpty(keydesc)){
			sqlcondition=" and t1.keydesc='"+keydesc+"' ";
		}
		sql=sql+sqlcondition+" order by t.enable desc,t1.parts_id,t1.keydesc,t.series_no ";
		try {
			List keyPartsCleanList=SQLProcess.excuteSQLQuery(sql, delegator);

			List modelList = delegator.findByAnd("EquipmentModel", UtilMisc.toMap("maintDept", deptIndex), UtilMisc.toList("Model"));
			
			request.setAttribute("modelList", modelList);			
			request.setAttribute("model", model);
			request.setAttribute("keydesc", keydesc);
			request.setAttribute("keyPartsCleanList", keyPartsCleanList);
			request.setAttribute("flag", "OK");
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
		return "success";		
	}
	
	public static String addKeyPartsClean(HttpServletRequest request, HttpServletResponse response){
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		GenericValue userInfo = AccountHelper.getUserInfo(request,delegator);
        String userId=userInfo.getString("accountNo");		
		String model=request.getParameter("aModel");
		String partsId=request.getParameter("ePartsId");
		String keydesc=request.getParameter("aKeydesc");
		String seriesNo=request.getParameter("eSeriesNo");
		String lifeType=request.getParameter("aLifeType");
		String limitLife=request.getParameter("eLimitLife");
		String remark=request.getParameter("eRemark");
		String enable=request.getParameter("eEnable");
		String keyPartsId="11430";
		Map cleanMap=new HashMap();
		try {
			List keyPartsList=delegator.findByAnd("KeyEqpParts", UtilMisc.toMap("eqpType", model, "partsId", partsId,"keydesc",keydesc));
			if (keyPartsList != null ){
//					keyPartsId=(String) ((Map)keyPartsList.get(0)).get("keyPartsId");
					keyPartsId=String.valueOf(((Map)keyPartsList.get(0)).get("keyPartsId"));
//					keyPartsId=(String) (Map)keyPartsList.get(0).getkeyPartsId;
			}
			List keyPartsCleanList=delegator.findByAnd("KeyEqpPartsClean", UtilMisc.toMap("keyPartsId", keyPartsId, "seriesNo", seriesNo));
			if(keyPartsCleanList!=null&&keyPartsCleanList.size()>0){
				request.setAttribute("_ERROR_MESSAGE_", "�ùؼ������Ѵ��ڴ�SeriesNo");
			}else{				
//				long keyPartsCleanId=PartsHelper.getMaxKeyPartsCleanId(delegator)+1;
				long keyPartsCleanId = delegator.getNextSeqId("keyPartsCleanIndex");
				cleanMap.put("keyPartsCleanId", keyPartsCleanId);
				cleanMap.put("keyPartsId", keyPartsId);
				cleanMap.put("seriesNo", seriesNo);
				cleanMap.put("lifeType", lifeType);
				cleanMap.put("limitLife", limitLife);
				cleanMap.put("remark", remark);
				cleanMap.put("enable", enable);
				cleanMap.put("updateUser", userId);
				cleanMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
				
				GenericValue gv=delegator.makeValue("KeyEqpPartsClean", cleanMap);
				gv.create();
				
				cleanMap.put("action", "ADD");
				long keyPartsCleanHistId = delegator.getNextSeqId("keyPartsCleanHistIndex");
				cleanMap.put("keyPartsCleanHistId", keyPartsCleanHistId);
				GenericValue histgv=delegator.makeValue("KeyEqpPartsCleanHist", cleanMap);
				histgv.create();

				request.setAttribute("_EVENT_MESSAGE_", "�����ɹ�");
			}
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
			request.setAttribute("_ERROR_MESSAGE_",e);
		}
		return "success";
	}
	
	public static String editKeyPartsClean(HttpServletRequest request, HttpServletResponse response){
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		GenericValue userInfo = AccountHelper.getUserInfo(request,delegator);
        String userId=userInfo.getString("accountNo");	
        String keyPartsCleanId=request.getParameter("eKeyPartsCleanId");
        String keyPartsId=request.getParameter("eKeyPartsId");
		String model=request.getParameter("eModel");
		String partsId=request.getParameter("ePartsId");
		String keydesc=request.getParameter("eKeydesc");
		String seriesNo=request.getParameter("eSeriesNo");
		String lifeType=request.getParameter("eLifeType");
		String limitLife=request.getParameter("eLimitLife");
		String remark=request.getParameter("eRemark");
		String enable=request.getParameter("eEnable");
		
		Map cleanMap=new HashMap();
		try {
			GenericValue gv=delegator.findByPrimaryKey("KeyEqpPartsClean", UtilMisc.toMap("keyPartsCleanId", keyPartsCleanId));
			gv.set("limitLife", limitLife);
			gv.set("remark", remark);
			gv.set("enable", enable);
			gv.set("updateUser", userId);
			gv.set("updateTime", new Timestamp(System.currentTimeMillis()));
			gv.store();
			
			long keyPartsCleanHistId = delegator.getNextSeqId("keyPartsCleanHistIndex");
			cleanMap.put("keyPartsCleanHistId", keyPartsCleanHistId);
			cleanMap.put("keyPartsCleanId", keyPartsCleanId);
			cleanMap.put("keyPartsId", keyPartsId);
			cleanMap.put("seriesNo", seriesNo);
			cleanMap.put("lifeType", lifeType);
			cleanMap.put("limitLife", limitLife);
			cleanMap.put("remark", remark);
			cleanMap.put("enable", enable);
			cleanMap.put("updateUser", userId);
			cleanMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
			cleanMap.put("action", "EDIT");
			GenericValue histgv=delegator.makeValue("KeyEqpPartsCleanHist", cleanMap);
			histgv.create();	
			
			request.setAttribute("_EVENT_MESSAGE_", "����ɹ�");
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
		return "success";
	}

	public static String delKeyCleanParts(HttpServletRequest request, HttpServletResponse response){
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		GenericValue userInfo = AccountHelper.getUserInfo(request,delegator);
        String userId=userInfo.getString("accountNo");
        String keyPartsCleanId=request.getParameter("keyPartsCleanId");
        Map cleanMap=new HashMap();
        try {
			GenericValue gv=delegator.findByPrimaryKey("KeyEqpPartsClean", UtilMisc.toMap("keyPartsCleanId", keyPartsCleanId));
			String keyPartsId=gv.getString("keyPartsId");
			String seriesNo=gv.getString("seriesNo");
			String lifeType=gv.getString("lifeType");
			String limitLife=gv.getString("limitLife");
			String remark=gv.getString("remark");
			String enable=gv.getString("enable");
			
			gv.remove();
			
			long keyPartsCleanHistId = delegator.getNextSeqId("keyPartsCleanHistIndex");
			cleanMap.put("keyPartsCleanHistId", keyPartsCleanHistId);
			cleanMap.put("keyPartsCleanId", keyPartsCleanId);
			cleanMap.put("keyPartsId", keyPartsId);
			cleanMap.put("seriesNo", seriesNo);
			cleanMap.put("lifeType", lifeType);
			cleanMap.put("limitLife", limitLife);
			cleanMap.put("remark", remark);
			cleanMap.put("enable", enable);
			cleanMap.put("updateUser", userId);
			cleanMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
			cleanMap.put("action", "DEL");
			GenericValue histgv=delegator.makeValue("KeyEqpPartsCleanHist", cleanMap);
			
			histgv.create();
			
			request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ�");			
			
		} catch (GenericEntityException e) {
			Debug.logError(module, e.getMessage());
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
		return "success";
	}
	
	public static void savekeypartsdelayinfo(HttpServletRequest request, HttpServletResponse response){
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    	String userNo=CommonUtil.getUserNo(request);
    	Date date=new Date();
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String updateTime=sdf.format(date);
    	String d_str=sdf.format(date).substring(0, 10).replaceAll("-", "").trim();
    	String keyPartsId=request.getParameter("keyPartsId_1");	
		String partsId= request.getParameter("partsId_1");
		String limitType=request.getParameter("limitType_1");
		String delayLife=request.getParameter("delayLife_1");
		String delaytime=request.getParameter("delaytime_1");
		String keyUseId=request.getParameter("keyUseId_1");
		String keyUseIdUsed=request.getParameter("keyUseIdUsed_1");
		String eventIndex=request.getParameter("eventIndex_1");
		String keydesc=request.getParameter("keydesc_1");
		String nameIndex=request.getParameter("nameIndex_1");
		String delayItems="";
		String delayTableId="";
		JSONObject dKeyPartsObject = new JSONObject();
		if(keyUseId.equals("0")&&!keyUseIdUsed.equals("0")){
			keyUseId=keyUseIdUsed;
		}else if(keyUseId.equals("0")&&keyUseIdUsed.equals("0")){
			return ;
		}
		Map map=new HashMap();				
		map.put("keyPartsId", keyPartsId);
    	map.put("partsId",partsId);
    	map.put("limitType",limitType);
    	map.put("delayLife",delayLife);
    	map.put("eventIndex",eventIndex);
    	map.put("keyUseId", keyUseId);
    	int size=Integer.parseInt(request.getParameter("dsize"));
    	try {
    		String sql0="select * from key_parts_delay_info where event_index='"+eventIndex+"' and key_parts_id='"+keyPartsId+"'";
    		List elist = SQLProcess.excuteSQLQuery(sql0, delegator);
    		if(elist.size()!=0){
    			Map tIdMap=(Map) elist.get(0);
    			delayTableId=(String) tIdMap.get("DELAY_TABLE_ID");
    			delegator.removeByAnd("KeyPartsDelayInfo", UtilMisc.toMap("delayTableId", delayTableId,"eventIndex",eventIndex));
    			delegator.removeByAnd("KeypartsdelayResult", UtilMisc.toMap("delayTableId", delayTableId,"eventIndex",eventIndex));
    			
    		}else{
	    		String sql = "select t.delay_table_id from key_parts_delay_info t where t.event_index='"+eventIndex+"' and t.delay_table_id like '%"+d_str+"%' order by t.delay_table_id desc";
				List list = SQLProcess.excuteSQLQuery(sql, delegator);
				if(list.size()==0||list==null){
					delayTableId=d_str+"A";
				}else{
					Map tIdMap=(Map)list.get(0);
					String tIdIndex=((String) tIdMap.get("DELAY_TABLE_ID")).substring(8);
					char[] tIdIndexArray=tIdIndex.toCharArray();
					char index1=(char) (tIdIndexArray[0]+1);
					delayTableId=d_str+index1;
				}
				if(delaytime.equals("null")||delaytime.equals("")){
					delaytime="1";
				}else{
					int t=Integer.parseInt(delaytime)+1;
					delaytime=t+"";
				}
				String dTimesql=" update key_parts_use  set delaytime='"+delaytime.trim()+"' where key_use_id='"+keyUseId+"' ";
				int s= SQLProcess.excuteSQLUpdate(dTimesql, delegator);
    		}
	    	for(int i=0;i<size;i++){
	    		Map resultMap=new HashMap();
	    		int indexNum=i+1;    	
	    		String delayItem=request.getParameter("delayItem_"+indexNum);
	    		String delaySpec=request.getParameter("delaySpec_"+indexNum);
	    		String delayRule=request.getParameter("delayRule_"+indexNum);
	    		String delayResult=request.getParameter("delayResult_"+indexNum);
	    		if(i==0){
	    			delayItems=delayItems+delayItem;
	    		}else{
	    			delayItems=delayItems+","+delayItem;
	    		}
	    		resultMap.put("eventIndex", eventIndex);
	    		resultMap.put("keyPartsId", keyPartsId);
	    		resultMap.put("keydesc", keydesc);
	    		resultMap.put("delayTableId", delayTableId);
	    		resultMap.put("delayItem",delayItem);
	    		resultMap.put("delaySpec",delaySpec);
	    		resultMap.put("delayRule",delayRule);
	    		resultMap.put("delayResult",delayResult);
	    		resultMap.put("resultId", delegator.getNextSeqId("KeyPartsDelayId"));
	    		GenericValue gv = delegator.makeValidValue("KeypartsdelayResult", resultMap);	                 
				delegator.create(gv);
	    	}
	    	map.put("keydesc", keydesc);
	    	map.put("delayTableId",delayTableId.trim());
	    	map.put("delayItems",delayItems.trim());
	    	map.put("infoId", delegator.getNextSeqId("KeyPartsDelayInfoId"));
	    	GenericValue gv = delegator.makeValidValue("KeyPartsDelayInfo", map);
	    	gv.put("updateUser", userNo);
	    	gv.put("updateTime",new Timestamp(System.currentTimeMillis()));
			delegator.create(gv);
			
			List dKeyParts=delegator.findByAnd("KeyPartsUse", UtilMisc.toMap("keyUseId", keyUseId));
			for(int i=0;i<dKeyParts.size();i++){
				Map dkMap=(Map) dKeyParts.get(i);
				dKeyPartsObject.put("partsType", dkMap.get("partsType"));
				dKeyPartsObject.put("vendor", dkMap.get("vendor"));
				dKeyPartsObject.put("seriesNo", dkMap.get("seriesNo"));
				dKeyPartsObject.put("baseSn", dkMap.get("baseSn"));
				dKeyPartsObject.put("initLife", dkMap.get("initLife"));
				dKeyPartsObject.put("offLine", dkMap.get("offLine"));
				dKeyPartsObject.put("delaytime", delaytime);
				dKeyPartsObject.put("remark", dkMap.get("remark"));
				dKeyPartsObject.put("nameIndex", nameIndex);
			}
			dKeyPartsObject.put("delayRst", "���ڱ���ɹ�");
		} catch (Exception e) {
            Debug.logError(module, e.getMessage());
			dKeyPartsObject.put("delayRst", "���ڱ������:"+e.getMessage());
		}   	    	
    	try {
			response.getWriter().write(dKeyPartsObject.toString());
		} catch (IOException e) {
			Debug.logError(module, e.getMessage());
		}
    }
	
	public static void saveMustchangeRemark(HttpServletRequest request, HttpServletResponse response){
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String userNo=CommonUtil.getUserNo(request);
		String formType=request.getParameter("mFormType");
		String eventIndex=request.getParameter("mEventIndex");
		String eqpId=request.getParameter("mEqpId");
		String size=request.getParameter("trlength");
		String checkArray[]=request.getParameterValues("mCheckBox");
		JSONObject msgObject=new JSONObject();
		try {
			for(int k=0;k<Integer.parseInt(size);k++){
				String delKeyPartsMustchangeCommId=request.getParameter("mKeyPartsMustchangeCommId_"+k);
				GenericValue gv=delegator.findByPrimaryKey("KeyPartsMustchangeComm", UtilMisc.toMap("keyPartsMustchangeCommId", delKeyPartsMustchangeCommId));
				if(gv!=null){					
					gv.remove();
				}
			}
			if(checkArray!=null&&checkArray.length>0){				
				for(int i=0;i<checkArray.length;i++){
					String index=checkArray[i];
					Map map=new HashMap();
					String keyPartsMustchangeCommId=request.getParameter("mKeyPartsMustchangeCommId_"+index);
					String keyPartsId=request.getParameter("mKeyPartsId_"+index);
					String keyUseId=request.getParameter("mKeyUseId_"+index);			
					String keydesc=request.getParameter("mKeydesc_"+index);
					String partsId=request.getParameter("mPartsId_"+index);
					String partsName=request.getParameter("mPartsName_"+index);
					String reason=request.getParameter("mReason_"+index);
					String remark=request.getParameter("mRemark_"+index);
					
					
					map.put("keyPartsId", keyPartsId);
					map.put("keyUseId", keyUseId);
					map.put("keydesc", keydesc);
					map.put("partsId", partsId);
					map.put("partsName", partsName);
					map.put("reason", reason);
					map.put("remark", remark);
					map.put("eqpId", eqpId);
					map.put("eventIndex", eventIndex);
					map.put("formType", formType);
					map.put("updateUser", userNo);
					map.put("updateTime", new Timestamp(System.currentTimeMillis()));
					
					if(keyPartsMustchangeCommId==null||keyPartsMustchangeCommId.equals("")){
						keyPartsMustchangeCommId=PartsHelper.getMaxkeyPartsMustchangeCommId(delegator);
						if(keyPartsMustchangeCommId==null||keyPartsMustchangeCommId.equals("0")){
							keyPartsMustchangeCommId="1";
						}else{
							keyPartsMustchangeCommId=((Long.parseLong(keyPartsMustchangeCommId)+1)+"").trim();
						}
						map.put("keyPartsMustchangeCommId", keyPartsMustchangeCommId);
						GenericValue gv=delegator.makeValue("KeyPartsMustchangeComm", map);
						gv.create();
					}else{
						map.put("keyPartsMustchangeCommId", keyPartsMustchangeCommId);
						GenericValue gv=delegator.makeValue("KeyPartsMustchangeComm", map);
						gv.create();
					}				
				}
			}
			msgObject.put("mustchangeCommRst", "�ػ�������˵������ɹ�");
		} catch (Exception e) {
            Debug.logError(module, e.getMessage());
            msgObject.put("mustchangeCommRst", "�ػ�������˵���������:"+e.getMessage());
		}
		try {
			response.getWriter().write(msgObject.toString());
		} catch (IOException e) {
			Debug.logError(module, e.getMessage());
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}
	
	public static String unUseKeyParts(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    	GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
    	String model=request.getParameter("model");
    	String eqpId=request.getParameter("eqpId");
    	String keyUseId=request.getParameter("keyUseId");
        String userId = userLogin.getString("userLoginId");
        String unTime=request.getParameter("unTime");
        String remark=request.getParameter("remark");
//    	String sql=" update  key_parts_use t1  SET t1.CREATE_TIME=to_date('"+unTime+"','yyyy-mm-dd hh24:mi:ss'),t1.status='OFFLINE',t1.CREATE_USER='"+userId+"',t1.remark='"+remark+"' where t1.key_use_id='"+keyUseId+"'";
    	try {
//			SQLProcess.excuteSQLUpdate(sql, delegator);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Timestamp date = new Timestamp(simpleDateFormat.parse(unTime).getTime());
			Map param1 = new HashMap();
			param1.put("createTime", date);
			param1.put("status", "OFFLINE");
			param1.put("createUser", userId);
			param1.put("remark", remark);
			GenericValue KeyPartsUse = delegator.makeValidValue("KeyPartsUse",param1);
			KeyPartsUse.put("keyUseId",keyUseId);
			KeyPartsUse.store();

		} catch (Exception e) {
			Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
    	return "success"; 
    }

	public static String partsVendorsMaintain(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String userNo = CommonUtil.getUserNo(request);
		String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);
		String sql = "select distinct parts_id from key_eqp_parts where maint_dept='" + deptIndex
				+ "' order by parts_id";
		try {
			List partsList = SQLProcess.excuteSQLQuery(sql, delegator);
			request.setAttribute("partsList", partsList);
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
		return "success";
	}

	public static String queryPartsVendorsList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String userNo = CommonUtil.getUserNo(request);
		String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);
		String partsId = request.getParameter("partsId");
		String partsListSql = "select distinct parts_id from key_eqp_parts where maint_dept='" + deptIndex + "'";
		String sql = "select * from parts_vendors where parts_id like '%" + partsId + "%' ";
		try {
			List partsList = SQLProcess.excuteSQLQuery(partsListSql, delegator);
			List partsVendorsList = SQLProcess.excuteSQLQuery(sql, delegator);
			request.setAttribute("partsVendorsList", partsVendorsList);
			request.setAttribute("partsList", partsList);
			request.setAttribute("partsId", partsId);
			request.setAttribute("flag", "OK");
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
		return "success";
	}

	public static String addPartsVendors(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String userNo = CommonUtil.getUserNo(request);
		String partsId = request.getParameter("aPartsId");
		int size = Integer.parseInt(request.getParameter("size"));
		for (int i = 0; i < size; i++) {
			String vendors = request.getParameter("aVendors_" + i);
			Long id = delegator.getNextSeqId("partsVendor");
			Map map = new HashMap();
			map.put("partsVendorsId", id);
			map.put("partsId", partsId);
			map.put("vendors", vendors);
			map.put("updateUser", userNo);
			map.put("updateTime", new Timestamp(System.currentTimeMillis()));
			GenericValue gv = delegator.makeValidValue("PartsVendors", map);
			try {
				gv.create();
			} catch (Exception e) {
				Debug.logError(module, e.getMessage());
				request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			}
		}
		request.setAttribute("_EVENT_MESSAGE_", " �����ɹ���");
		return "success";
	}

	public static String delPartsVendors(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String partsVendorsId = request.getParameter("partsVendorsId");
		String partdId = request.getParameter("partsId");
		try {
			delegator.removeByAnd("PartsVendors", UtilMisc.toMap("partsVendorsId", partsVendorsId));
			request.setAttribute("_EVENT_MESSAGE_", " ɾ���ɹ���");
		} catch (GenericEntityException e) {
			Debug.logError(module, e.getMessage());
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
		return "success";
	}

	public static String editPartsVendors(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String userNo = CommonUtil.getUserNo(request);
		int size = Integer.parseInt(request.getParameter("size"));
		String partsId = request.getParameter("partsNo");
		for (int i = 0; i < size; i++) {
			String partsVendorsId = request.getParameter("parts_vendors_id_" + i);
			String vendors = request.getParameter("vendors_" + i);
			try {
				GenericValue gv = delegator.findByPrimaryKey("PartsVendors",
						UtilMisc.toMap("partsVendorsId", partsVendorsId));
				gv.set("updateUser", userNo);
				gv.set("updateTime", new Timestamp(System.currentTimeMillis()));
				gv.set("vendors", vendors);
				gv.store();
			} catch (GenericEntityException e) {
				Debug.logError(module, e.getMessage());
				request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			}
		}
		request.setAttribute("_EVENT_MESSAGE_", " �޸ĳɹ���");
		return "success";
	}

	public static String queryVendorsPartsList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String userNo = CommonUtil.getUserNo(request);
		String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);
		String partsId = request.getParameter("partNo");
		List partsList = new ArrayList();
		String sql = "select distinct part_no,part_name,t1.maint_dept from parts_data t0 "
				+ " left join key_eqp_parts t1 on t0.part_no=t1.parts_id " + " where part_no like '%" + partsId
				+ "%' and t1.maint_dept='" + deptIndex + "'";
		if (partsId != null && !partsId.equals("")) {
			try {
				partsList = SQLProcess.excuteSQLQuery(sql, delegator);
				request.setAttribute("flag", "OK");
			} catch (Exception e) {
				Debug.logError(module, e.getMessage());
				request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			}
		}
		request.setAttribute("partsList", partsList);
		return "success";
	}
	
	/**
	 * ����߼������趨
	 * @param request
	 * @param response
	 * @return �����Ƿ�ɹ�
	 */
	public static String queryHighValuePartList(HttpServletRequest request,
	        HttpServletResponse response){
	    GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
	    try{
	        GenericValue userInfo = AccountHelper.getUserInfo(request, delegator);
			String accountDept = userInfo.getString("accountDept");
 			String dept = (String) request.getParameter("dept");
 			String dept1 = (String) request.getAttribute("dept");
			if (StringUtils.isEmpty(dept)) {
				dept = StringUtils.isEmpty(dept1)? accountDept : dept1;
			}
			request.setAttribute("dept", dept);
//			List list = delegator.findByAnd("HighValueParts", UtilMisc.toMap("dept", dept, "enable", "1"));
			List list = delegator.findByAnd("HighValueParts", UtilMisc.toMap("dept", dept));
			request.setAttribute("highValuePartsList", list);
	    }catch (Exception e) {
	        request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	        Debug.logError(e, module);
	        return "error";
	    }
	    return "success";
	}
	/**
	 * �������ϺŲ�ѯ�߼�ֵ�����趨
	 * @param request
	 * @param response
	 */
	public static void queryHighValuePartsByPartNo(HttpServletRequest request,
	        HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
	    try{
	    	String partNo = (String) request.getParameter("partNo");
			GenericValue partGV = delegator.findByPrimaryKey("HighValueParts", UtilMisc.toMap("partNo", partNo));
			
			JSONObject partInfo = new JSONObject();
			partInfo.put("partNo", partGV.getString("partNo"));
			partInfo.put("partName", partGV.getString("partName"));
			partInfo.put("averagePrice", partGV.getDouble("averagePrice"));
            partInfo.put("dept", partGV.getString("dept"));
            response.getWriter().write(partInfo.toString());
	    }catch (Exception e) {
	        request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	        Debug.logError(e, module);
	    }
	}
	
	/**
	 * �־û��߼������趨
	 * @param request
	 * @param response
	 * @return �����Ƿ�ɹ�
	 */
	public static String manageHighValuePartDefine(HttpServletRequest request,
	        HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String partNo = (String) request.getParameter("partNo");
		String partName = (String) request.getParameter("partName");
		Double averagePrice = Double.valueOf(request.getParameter("averagePrice"));
		String dept = (String) request.getParameter("deptSel");
		String mode = (String) request.getParameter("mode");
		request.setAttribute("dept", dept);
/*		GenericValue partGV = delegator.makeValidValue("HighValueParts",
				UtilMisc.toMap("partNo", partNo, "partName", partName, "averagePrice", averagePrice, "dept", dept, "enable", 1));*/
		GenericValue partGV = delegator.makeValidValue("HighValueParts",
				UtilMisc.toMap("partNo", partNo, "partName", partName, "averagePrice", averagePrice, "dept", dept));
		try {
			if("insert".equals(mode)) {
				delegator.create(partGV);
			}
			else if("update".equals(mode)) {
				delegator.store(partGV);
			}
			request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
		} catch (GenericEntityException e) {
        	String message = CommonUtil.checkOracleException(e);
        	request.setAttribute("_ERROR_MESSAGE_", message);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	        Debug.logError(e, module);
	        return "error";
		}
		return "success";
	}
	
	/**
	 * ɾ���߼������趨��enble��Ϊ0����ʵ��ɾ����¼��
	 * @param request
	 * @param response
	 * @return �����Ƿ�ɹ�
	 */
	public static String delHighValuePartDefine(HttpServletRequest request,
	        HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String partNo = (String) request.getParameter("partNo");
		try {
			GenericValue partGV = delegator.findByPrimaryKey("HighValueParts", UtilMisc.toMap("partNo", partNo));
			partGV.set("enable", 0);
			partGV.store();
			request.setAttribute("dept", partGV.getString("dept"));
			request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
		} catch (GenericEntityException e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	        Debug.logError(e, module);
	        return "error";
		}
		return "success";
	}
}
