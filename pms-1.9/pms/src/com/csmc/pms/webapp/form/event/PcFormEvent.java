package com.csmc.pms.webapp.form.event;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.common.helper.CommonHelper;
import com.csmc.pms.webapp.form.help.PcHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.GeneralEvents;
import com.csmc.pms.webapp.util.MiscUtils;
import com.csmc.pms.webapp.workflow.model.Job;
import com.csmc.pms.webapp.workflow.model.engine.JobEngine;

public class PcFormEvent extends GeneralEvents {
	public static final String module = PcFormEvent.class.getName();
	
	/**
	 * ����Ѳ�������/��дҳ��
	 * @param request
	 * @param response
	 */
	public static String setupPcFormEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String styleIndex = request.getParameter("styleIndex");
        
        if (StringUtils.isEmpty(styleIndex)) {
        	styleIndex = (String) request.getAttribute("styleIndex");
        }
        
        request.setAttribute("styleIndex", styleIndex);
        
        try {
            List pcStyleList = PcHelper.queryPcStyle(delegator);
            request.setAttribute("pcStyleList", pcStyleList);
            
            if (StringUtils.isNotEmpty(styleIndex)) {
	            List pcFormList = PcHelper.queryPcSchedule(delegator, styleIndex);
	            request.setAttribute("pcFormList", pcFormList);
            }
        } catch (Exception e) {        	
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
        }

        return "success";
    }
	
	/**
	 * ����Ѳ��
	 * @param request
	 * @param response
	 */
	public static String setupPcForm(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);   
        LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
        //�����ϴ��ݵ����в�����ϳ�Map
        //params(formStyleIndex,styleIndex,periodIndex,periodDesc,note,scheduleDate,scheduleIndex)
        Map paramMap = getInitParams(request, false, true);
        
        try {
        	paramMap.put("styleIndex", paramMap.get("formStyleIndex"));
        	request.setAttribute("styleIndex", paramMap.get("formStyleIndex"));
        	
        	GenericValue userInfo = AccountHelper.getUserInfo(request, delegator);
        	String accountDept = userInfo.getString("accountDept");
        	String eqpChar = PcHelper.getEqpCharByDept(delegator, accountDept);
        	paramMap.put("pcName", CommonHelper.getFormName(Constants.PC_CHAR, eqpChar, delegator));
        	
        	// ȡ���û�            
            String userNo = CommonUtil.getUserNo(request);
            paramMap.put("createUser", userNo);
            
            paramMap.put("status", new Integer(Constants.CREAT));
            
            // �޸�ScheduleΪPeriod�����õ�ScheduleDate
            // ������ӦPC��Ϊ����7��30�ֵ�����7��30��
            java.sql.Date scheduleDate = MiscUtils.toGuiDate((String) paramMap.get("scheduleDate"), "yyyy-MM-dd");
            paramMap.put("scheduleDate", scheduleDate);

            // ����ʱ�丳ֵ
            paramMap.put("createTime", new Timestamp(System.currentTimeMillis()));            
            
            // ����Serive�½�Ѳ��
            PcHelper.setupPcForm(delegator, dispatcher, paramMap, userNo);       
            
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
        }

        return "success";
    }
	
	
	/**
	 * ����Ѳ�����дҳ��
	 * @param request
	 * @param response
	 */
	public static String inputPcFormEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String periodName = request.getParameter("periodName");
        String pcIndex = request.getParameter("pcIndex");
        String jobRelationIndex = request.getParameter("jobRelationIndex");         
        
        try {
        	GenericValue pcFormGv = delegator.findByPrimaryKey("PcForm", UtilMisc.toMap("pcIndex",pcIndex));
        	request.setAttribute("pcName", pcFormGv.getString("pcName"));
        	request.setAttribute("createTime", pcFormGv.getString("createTime"));
        	request.setAttribute("createUser", pcFormGv.getString("createUser"));
        	request.setAttribute("note", pcFormGv.getString("note"));
        	request.setAttribute("jobText", pcFormGv.getString("jobText"));
        	
        	GenericValue pcStyleGv = delegator.findByPrimaryKey("PcStyle", UtilMisc.toMap("styleIndex",pcFormGv.getString("styleIndex")));
        	request.setAttribute("styleName", pcStyleGv.getString("name"));
        	
        	request.setAttribute("periodName", periodName);
        	request.setAttribute("pcIndex", pcIndex);
        	request.setAttribute("jobRelationIndex", jobRelationIndex);
        } catch (Exception e) {        	
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
        }

        return "success";
    }
	
	/**
	 * ����Ѳ�����дҳ��
	 * @param request
	 * @param response
	 */
	public static String newInputPcFormEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String periodName = request.getParameter("periodName");
        String pcIndex = request.getParameter("pcIndex");
        String jobRelationIndex = request.getParameter("jobRelationIndex");         
        
        try {
        	GenericValue pcFormGv = delegator.findByPrimaryKey("PcForm", UtilMisc.toMap("pcIndex",pcIndex));
        	request.setAttribute("pcName", pcFormGv.getString("pcName"));
        	request.setAttribute("createTime", pcFormGv.getString("createTime"));
        	request.setAttribute("createUser", pcFormGv.getString("createUser"));
        	request.setAttribute("note", pcFormGv.getString("note"));
        	request.setAttribute("jobText", pcFormGv.getString("jobText"));
        	
        	GenericValue pcStyleGv = delegator.findByPrimaryKey("PcStyle", UtilMisc.toMap("styleIndex",pcFormGv.getString("styleIndex")));
        	request.setAttribute("styleName", pcStyleGv.getString("name"));
        	
        	request.setAttribute("periodName", periodName);
        	request.setAttribute("pcIndex", pcIndex);
        	request.setAttribute("jobRelationIndex", jobRelationIndex);
        	
        	//huanghp,883609,20081008,
			if (jobRelationIndex != null && jobRelationIndex.length() > 0)
			{
	        	JobEngine jobEngine = JobEngine.create();
				jobEngine.setDelegator(delegator);
				jobEngine.setJobRelationIndex(jobRelationIndex);
				Job job = jobEngine.getViewJobFromEntity();
				request.setAttribute("singleJob", job);
			}
        	
        } catch (Exception e) {        	
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
        }

        return "success";
    }
	
	/**
	 * ��ѯѲ���
	 * @param request
	 * @param response
	 */
	public static String queryPcFormEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        
        String startDate = request.getParameter("startDate");
    	String endDate = request.getParameter("endDate");
    	String styleIndex = request.getParameter("styleIndex");
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    	if (null == startDate) {
    		Calendar cDay = Calendar.getInstance();
    		cDay.add(Calendar.DATE,-7);
    		startDate = formatter.format(cDay.getTime());
    	}

    	if (null == endDate) {
    		endDate = formatter.format(Calendar.getInstance().getTime());
    	}    	
    	               
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("styleIndex", styleIndex);
        
        try {
            List pcStyleList = PcHelper.queryPcStyle(delegator);
            request.setAttribute("pcStyleList", pcStyleList);
            
            if (StringUtils.isNotEmpty(styleIndex)) {
            	Map paramMap = new HashMap();
        		paramMap.put("startDate", startDate);
        		paramMap.put("endDate", endDate);
        		paramMap.put("styleIndex", styleIndex);
        		
	            List pcFormList = PcHelper.queryPcForm(delegator, paramMap);
	            request.setAttribute("pcFormList", pcFormList);
            }
            
        } catch (Exception e) {        	
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
        }

        return "success";
    }
}
