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
 * �� RepairEvent.java 
 * @version  1.0  2009-8-18
 * @author   jiyw
 */

public class ExpirationEvent extends GeneralEvents {
	public static final String module = ExpirationEvent.class.getName();
	
	/**
	 * ���ݳ������Ų�ѯ������Ϣ
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
	        // ��ʾ����List
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
     * ������Ч��
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
		request.setAttribute("_EVENT_MESSAGE_", "MRBʱ���Ѿ�����");
		return queryMaterialByVendorBatchNum(request,response);
    }
        
    /**
	 * ����Ч�����ϲ�ѯ
	 * ��ʾ���û�ѡ�������ǰ��Ĭ��Ϊ��������죩Ϊֹ����Ч������
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
    		//�ж��Ƿ��ʼ���ύ����ʼ��Ĭ��Ϊ�û�����    	
        	if(!"1".equalsIgnoreCase(flag)){
        		GenericValue user = AccountHelper.getUserInfo(request, delegator);
        		deptIndex = user.getString("deptIndex");    		
        		
        		//��� ������Ĭ�Ϲ�̽�
        		if ("10001".equals(deptIndex)) {
        			mtrGrp = ConstantsMcs.PHOTORESIST;
        		}
        	}
        	
	        // ��ʾ������Ϊֹ����Ч������List��Ĭ��Ϊ10���ڵ��ڣ�
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