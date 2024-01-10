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
 * 类 ShipEvent.java 
 * @version  1.0  2009-9-1
 * @author jiangjing
 */
public class ShipEvent extends GeneralEvents{
	public static final String module = ShipEvent.class.getName();
	
	/**
     * 报废与出厂维修 课长确认起始页
     * 得到用户部门
     * 显示报废与出厂维修课长确认物料
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
        	// 得到用户部门
        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
	        String userDeptIndex = user.getString("deptIndex");
	        String userSection = user.getString("accountSection");
	        
	        Map paramMap = new HashMap();
			paramMap.put("deptIndex", userDeptIndex);
			paramMap.put("status", oldStatus);
			
			if (StringUtils.isNotEmpty(userSection) && userSection.indexOf("课") > -1) {
				paramMap.put("accountSection", userSection);
			}
			if (StringUtils.isNotEmpty(mtrGrp)) {
				paramMap.put("mtrGrp", mtrGrp);
			}
			if (StringUtils.isNotEmpty(mtrNum)) {
				paramMap.put("mtrNum", mtrNum);
			}
			
	        // 显示报废与出厂维修课长确认物料List
        	List leadConfirmList = ShipHelper.queryScrapVendorConfirmList(delegator, paramMap);
			request.setAttribute("leadConfirmList", leadConfirmList);
			
	        // 更换操作完成后得到transactionId
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
     * 报废与出厂维修(课长确认)  
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
	    		request.setAttribute("_ERROR_MESSAGE_", "未选择要需更换的物料，请在物料列表中勾选！");
	            return "error";
			} else {
				Map paramMap = new HashMap();
				paramMap.put("userNo", userNo);
				paramMap.put("oldStatus", oldStatus);
				paramMap.put("newStatus", newStatus);
				paramMap.put("mtrStatusIdArray", leadConfirmSelectArray);
				
				// 调用Service修改状态
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
     * 报废与出厂维修 仓库确认起始页
     * 得到用户部门
     * 显示报废与出厂维修仓库确认物料
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
			
	        // 显示报废与出厂维修仓库确认物料List
        	List shipConfirmList = ShipHelper.queryScrapVendorConfirmList(delegator, paramMap);
			request.setAttribute("shipConfirmList", shipConfirmList);
			
	        // 更换操作完成后得到transactionId
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
     * 报废与出厂维修(仓库确认)  
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
	    		request.setAttribute("_ERROR_MESSAGE_", "未选择要需更换的物料，请在物料列表中勾选！");
	            return "error";
			}  else {
				Map paramMap = new HashMap();
				paramMap.put("userNo", userNo);
				paramMap.put("oldStatus", oldStatus);
				paramMap.put("newStatus", newStatus);
				paramMap.put("stockAddressArray", stockAddressArray);
				paramMap.put("mtrStatusIdArray", shipConfirmSelectArray);
				
				// 调用Service修改状态
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

