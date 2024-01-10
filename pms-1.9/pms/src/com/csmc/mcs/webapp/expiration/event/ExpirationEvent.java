package com.csmc.mcs.webapp.expiration.event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.csmc.mcs.webapp.expiration.helper.ExpirationHelper;
import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.GeneralEvents;

/**
 * 类 RepairEvent.java 
 * @version  1.0  2009-8-18
 * @author   jiyw
 */

public class ExpirationEvent extends GeneralEvents {
	public static final String module = ExpirationEvent.class.getName();
	
	/**
	 * 根据厂家批号查询物料信息
	 * @param request
	 * @param response
	 * @return
	 */
    public static String queryMaterialByVendorBatchNum(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        
        String vendorBatchNum = UtilFormatOut.checkNull(request.getParameter("vendorBatchNum")).toUpperCase();
    	String deptIndex = UtilFormatOut.checkNull(request.getParameter("deptIndex")).toUpperCase();
    	String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp")).toUpperCase();
        
        request.setAttribute("vendorBatchNum", vendorBatchNum);
        
        try {
	        // 显示物料List
        	List materialList = ExpirationHelper.queryOverExpirationList(delegator, deptIndex, mtrGrp, vendorBatchNum,null);
			request.setAttribute("materialList", materialList);
	        
			request.setAttribute("listSize", String.valueOf(materialList.size()));

        } catch(Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /**
     * 保存有效期
     * @param request
     * @param response
     * @return
     */
    public static String saveExpirationDate(HttpServletRequest request, HttpServletResponse response){
		Map paramMap = UtilHttp.getParameterMap(request);
		String errorDesc = "";	
		String userId = CommonUtil.getUserNo(request);
		paramMap.put("userId", userId);
		try{
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			Map result = dispatcher.runSync("saveExpirationDate", UtilMisc.toMap("paramMap",paramMap));
			if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
				String err = (String)result.get(ModelService.ERROR_MESSAGE);
				errorDesc = err;
			}		
			
			if (!"".equalsIgnoreCase(errorDesc)) {
				request.setAttribute("_ERROR_MESSAGE_", errorDesc);
				return "error";
			}					
		}catch(Exception e){
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}		
		request.setAttribute("_EVENT_MESSAGE_", "MRB时间已经保存");
		return queryMaterialByVendorBatchNum(request,response);
    }
        
    /**
	 * 超有效期物料查询
	 * 显示到用户选择的日期前（默认为当天后两天）为止超有效期物料
	 * @param request
	 * @param response
	 * @return
	 */
    public static String queryOverExpirationEntry(HttpServletRequest request,HttpServletResponse response){
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
    	String flag = UtilFormatOut.checkNull(request.getParameter("flag")).toUpperCase();
    	String deptIndex = UtilFormatOut.checkNull(request.getParameter("deptIndex")).toUpperCase();
    	String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp")).toUpperCase();
    	String startDate = UtilFormatOut.checkNull(request.getParameter("startDate"));    	
    	
    	try {
    		//判断是否初始化提交，初始化默认为用户部门    	
        	if(!"1".equalsIgnoreCase(flag)){
        		GenericValue user = AccountHelper.getUserInfo(request, delegator);
        		deptIndex = user.getString("deptIndex");    		
        		
        		//光刻 物料组默认光刻胶
        		if ("10001".equals(deptIndex)) {
        			mtrGrp = ConstantsMcs.PHOTORESIST;
        		}
        	}
        	
	        // 显示到当天为止超有效期物料List（默认为10天内到期）
    	    if (startDate == null || startDate.equals("")){
    	        Calendar c = Calendar.getInstance(); 
    	        c.setTime(new Date());
    	        c.add(Calendar.DATE, 10);
    	        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
    	        startDate = df2.format(c.getTime());
    	    }
    	    
        	List materialList = ExpirationHelper.queryOverExpirationList(delegator, deptIndex, mtrGrp,null,startDate);
			request.setAttribute("materialList", materialList);
			request.setAttribute("listSize", String.valueOf(materialList.size()));
			request.setAttribute("deptIndex", deptIndex);
			request.setAttribute("mtrGrp", mtrGrp);
			request.setAttribute("startDate", startDate);
    	} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
    	
    	return "success";
    }    
}