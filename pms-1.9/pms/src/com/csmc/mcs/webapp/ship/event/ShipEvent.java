package com.csmc.mcs.webapp.ship.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

import com.csmc.mcs.webapp.ship.helper.ShipHelper;
import com.csmc.mcs.webapp.use.helper.UseHelper;
import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.GeneralEvents;

/**
 * �� ShipEvent.java 
 * @version  1.0  2009-9-1
 * @author jiangjing
 */
public class ShipEvent extends GeneralEvents{
	public static final String module = ShipEvent.class.getName();
	
	/**
     * ���������ά�� �γ�ȷ����ʼҳ
     * �õ��û�����
     * ��ʾ���������ά�޿γ�ȷ������
     * @param request
     * @param response
     * @return
     */
    public static String leadConfirmEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        String mtrGrp = request.getParameter("mtrGrp");
        String oldStatus = request.getParameter("oldStatus");
        
        if (StringUtils.isEmpty(oldStatus)) {
        	oldStatus = ConstantsMcs.ONLINE_SCRAP_OPT;
		}
		request.setAttribute("oldStatus", oldStatus);
        
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
        
        try{
        	// �õ��û�����
        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
	        String userDeptIndex = user.getString("deptIndex");
	        String userSection = user.getString("accountSection");
	        
	        Map paramMap = new HashMap();
			paramMap.put("deptIndex", userDeptIndex);
			paramMap.put("status", oldStatus);
			
			if (StringUtils.isNotEmpty(userSection) && userSection.indexOf("��") > -1) {
				paramMap.put("accountSection", userSection);
			}
			if (StringUtils.isNotEmpty(mtrGrp)) {
				paramMap.put("mtrGrp", mtrGrp);
			}
			if (StringUtils.isNotEmpty(mtrNum)) {
				paramMap.put("mtrNum", mtrNum);
			}
			
	        // ��ʾ���������ά�޿γ�ȷ������List
        	List leadConfirmList = ShipHelper.queryScrapVendorConfirmList(delegator, paramMap);
			request.setAttribute("leadConfirmList", leadConfirmList);
			
	        // ����������ɺ�õ�transactionId
	        String transactionId = (String) request.getAttribute("transactionId");
	        request.setAttribute("transactionId", transactionId);
        } catch(Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /**
     * ���������ά��(�γ�ȷ��)  
     * @param request
     * @param response
     * @return
     */
    public static String leadConfirmById(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
    	LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String userNo = CommonUtil.getUserNo(request);

        String newStatus = request.getParameter("newStatus");
        String oldStatus = request.getParameter("oldStatus");
        
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
        
        try{
        	
			String[] leadConfirmSelectArray = request.getParameterValues("objCheckboxLeadConfirm");
			
			if (leadConfirmSelectArray == null) {
	    		request.setAttribute("_ERROR_MESSAGE_", "δѡ��Ҫ����������ϣ����������б��й�ѡ��");
	            return "error";
			} else {
				Map paramMap = new HashMap();
				paramMap.put("userNo", userNo);
				paramMap.put("oldStatus", oldStatus);
				paramMap.put("newStatus", newStatus);
				paramMap.put("mtrStatusIdArray", leadConfirmSelectArray);
				
				// ����Service�޸�״̬
				UseHelper.modifyMtrStatusByService(request, dispatcher, "changeMaterialStatusById", paramMap);
			}
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /**
     * ���������ά�� �ֿ�ȷ����ʼҳ
     * �õ��û�����
     * ��ʾ���������ά�޲ֿ�ȷ������
     * @param request
     * @param response
     * @return
     */
    public static String shipConfirmEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        String mtrGrp = request.getParameter("mtrGrp");
        String oldStatus = request.getParameter("oldStatus");
        
        if (StringUtils.isEmpty(oldStatus)) {
        	oldStatus = ConstantsMcs.GENERAL_SCRAP_LEADER;
		}
		request.setAttribute("oldStatus", oldStatus);
        
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
        
        try {
	        Map paramMap = new HashMap();
			paramMap.put("deptIndex", null);
			paramMap.put("status", oldStatus);
			
			if (StringUtils.isNotEmpty(mtrGrp)) {
				paramMap.put("mtrGrp", mtrGrp);
			}
			if (StringUtils.isNotEmpty(mtrNum)) {
				paramMap.put("mtrNum", mtrNum);
			}
			
	        // ��ʾ���������ά�޲ֿ�ȷ������List
        	List shipConfirmList = ShipHelper.queryScrapVendorConfirmList(delegator, paramMap);
			request.setAttribute("shipConfirmList", shipConfirmList);
			
	        // ����������ɺ�õ�transactionId
	        String transactionId = (String) request.getAttribute("transactionId");
	        request.setAttribute("transactionId", transactionId);
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /**
     * ���������ά��(�ֿ�ȷ��)  
     * @param request
     * @param response
     * @return
     */
    public static String shipConfirmById(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
    	LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String userNo = CommonUtil.getUserNo(request);

        String newStatus = request.getParameter("newStatus");
        String oldStatus = request.getParameter("oldStatus");
        //String stockAddress = UtilFormatOut.checkNull(request.getParameter("stockAddress")).toUpperCase();
        String[] stockAddressArray = request.getParameterValues("stockAddress");
        
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
        
        try{
        	
			String[] shipConfirmSelectArray = request.getParameterValues("shipConfirmCheckbox");

			if (shipConfirmSelectArray == null) {
	    		request.setAttribute("_ERROR_MESSAGE_", "δѡ��Ҫ����������ϣ����������б��й�ѡ��");
	            return "error";
			}  else {
				Map paramMap = new HashMap();
				paramMap.put("userNo", userNo);
				paramMap.put("oldStatus", oldStatus);
				paramMap.put("newStatus", newStatus);
				paramMap.put("stockAddressArray", stockAddressArray);
				paramMap.put("mtrStatusIdArray", shipConfirmSelectArray);
				
				// ����Service�޸�״̬
				UseHelper.modifyMtrStatusByService(request, dispatcher, "changeMaterialStatusById", paramMap);
			}
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
}

