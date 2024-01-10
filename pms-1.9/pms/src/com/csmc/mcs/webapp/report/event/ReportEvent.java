package com.csmc.mcs.webapp.report.event;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.csmc.mcs.webapp.common.helper.McsCommonHelper;
import com.csmc.mcs.webapp.report.helper.ReportHelper;
import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.GeneralEvents;
/**
 * 类 ReportEvent.java 
 * @version  1.0  2009-8-17
 * @author   wanggq
 */
public class ReportEvent extends GeneralEvents{
	public static final String module = ReportEvent.class.getName();
	// ---------------------------------------------事件细项 wanggq--------------------------------------
	/**

//	public static void getJsonObjectIdList (HttpServletRequest request, HttpServletResponse response) {
//		 GenericDelegator delegator = CommonUtil.getMcsDelegator(request); 		
//	     JSONArray mtrGrpJson = new JSONArray();
//	     
//	     try {
//			JSONObject blank = new JSONObject(UtilMisc.toMap("usingObjectId", ""));
//			mtrGrpJson.put(blank);    	 	 
//	    	List objectIdList = delegator.findAllCache("Equipment", UtilMisc.toList("equipmentId")); 
//	    	
//	    	for ( Iterator it = objectIdList.iterator(); it.hasNext(); ) { 
//				GenericValue gv = (GenericValue) it.next();
//				JSONObject object = new JSONObject();
//				object.put("usingObjectId", gv.getString("equipmentId"));
//	    		mtrGrpJson.put(object);
//	    	 }
//				response.getWriter().write(mtrGrpJson.toString());	 
//	     } catch(Exception e ) {	    	 
//	            Debug.logError(e.getMessage(), module); 	    	 
//	     }
//		
//	}
//	
	/**
     * 查询物料信息
     * @param request
     * @param response
     * @return
     */ 
	public static String queryMaterialInfo(HttpServletRequest request, HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getMcsDelegator(request); 		
	     GenericValue user = AccountHelper.getUserInfo(request, delegator);
	     String iniDeptIndex = UtilFormatOut.checkNull(user.getString("deptIndex"));	     
		 String userDeptIndex = request.getParameter("deptIndex");
	     String mtrGrp = request.getParameter("mtrGrp");	    	 
	     String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase(); 
	     String usingObjectId = request.getParameter("usingObjectId");
	     String inControl = request.getParameter("inControl");
	     String needScrapStore = request.getParameter("needScrapStore");
	     
	     Map materialMap = new HashMap();
	     materialMap.put("deptIndex", userDeptIndex);
		 materialMap.put("mtrGrp", mtrGrp);	    	 	    	 
	     materialMap.put("mtrNum",mtrNum);
		 materialMap.put("usingObjectId", usingObjectId);
		 materialMap.put("inControl", inControl);
		 materialMap.put("needScrapStore", needScrapStore);
		 
		 String PrivFlag= AccountHelper.getPricFlag(request, delegator);       
	        if (PrivFlag != null || StringUtils.isNotEmpty(PrivFlag)) {
	        // 判断是否有权限查看价格
	        request.setAttribute("flag", "OK");
	        }
	        
	     try { 
	    	 List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept")); 
	    	 request.setAttribute("deptList", deptList);
	    	 
	    	 if (StringUtils.isEmpty(userDeptIndex) &&  StringUtils.isEmpty(mtrGrp) && StringUtils.isEmpty(mtrNum) && StringUtils.isEmpty(usingObjectId)) {
	    		 
	    	 } else {	    		 
			     List materialList = ReportHelper.getMaterialList(delegator, materialMap);
			     request.setAttribute("materialList", materialList);	    		 
	    	 }
	    	 request.setAttribute("iniDeptIndex",iniDeptIndex);

	     } catch (Exception e ){
	         Debug.logError(e.getMessage(), module); 	
	         request.setAttribute("_ERROR_MESSAGE_", e.getMessage());  
	         return "error";
	     }
		return "sucess";
		     
	}
	
	/**
     * 查询物料详细信息及设备信息
     * @param request
     * @param response
     * @return
     */ 	
	public static String queryMaterialDetail(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request); 	
		String materialIndex = request.getParameter("materialIndex");	 
		
		Map mtrObjectMap = new HashMap();
		mtrObjectMap.put("materialIndex", materialIndex);
		 
		try {   
        	GenericValue materialInfo = delegator.findByPrimaryKey("McsMaterialInfo", UtilMisc.toMap("materialIndex", materialIndex));
        	List mtrObjectList = delegator.findByAnd("McsMtrObject", mtrObjectMap,UtilMisc.toList("usingObjectId"));
        	request.setAttribute("mtrObjectList", mtrObjectList);
        	request.setAttribute("materialInfo", materialInfo);	        	
		} catch (Exception e) {
	        Debug.logError(e.getMessage(), module); 	
	        request.setAttribute("_ERROR_MESSAGE_", e.getMessage());  
	        return "error";	    	 
	    }
		 
		return "sucess";
	}

	/**
     * 物料状态汇总 历史信息查询
     * @param request
     * @param response
     * @return
     */ 	
	public static String queryMaterialStatusHist (HttpServletRequest request, HttpServletResponse response) {		
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
		String materialStatusIndex = request.getParameter("materialStatusIndex");
		
		Map statusMap = new HashMap();
		statusMap.put("materialStatusIndex", materialStatusIndex);
	
		try {
			List statusDetailList = ReportHelper.getStatusDetailList(delegator, statusMap);
			request.setAttribute("statusDetailList", statusDetailList);
			
			List statusHistList = ReportHelper.getStatusHistList(delegator, statusMap);
			request.setAttribute("statusHistList", statusHistList);			
		} catch (Exception e) {			
           Debug.logError(e.getMessage(), module); 
           request.setAttribute("_ERROR_MESSAGE_", e.getMessage());  
           return "error";
		}
		
		return "sucess";		
		
	}
	
	/**
     * 物料状态汇总 详细信息查询
     * @param request
     * @param response
     * @return
     */	
	public static String queryMaterialStatusDetail (HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
	     String startDate = request.getParameter("startDate");
	     String endDate = request.getParameter("endDate");
		 String deptIndex = request.getParameter("deptIndex");
		 String usingObjectId = request.getParameter("equipmentId");
	     String mtrGrp = request.getParameter("mtrGrp");	
	     String status = request.getParameter("status");
	     String mtrNum = request.getParameter("mtrNum"); 
	     String materialStatusIndex = request.getParameter("materialStatusIndex");
	     
		Map statusMap = new HashMap();
		statusMap.put("startDate", startDate);
		statusMap.put("endDate", endDate);
		statusMap.put("deptIndex", deptIndex);
		statusMap.put("usingObjectId", usingObjectId);
		statusMap.put("mtrGrp", mtrGrp);
		statusMap.put("status", status);
		statusMap.put("mtrNum", mtrNum);
		statusMap.put("materialStatusIndex", materialStatusIndex);
		
		try {
	    	List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept")); 
	    	request.setAttribute("deptList", deptList);
	    	
			List statusDetailList = ReportHelper.getStatusDetailList(delegator, statusMap);
			request.setAttribute("statusDetailList", statusDetailList);
		} catch ( Exception e){			
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());  
            return "error";			
		}
		
		return "sucess";
	}
	
	/**
     * 物料状态汇总
     * @param request
     * @param response
     * @return
     */ 		
	public static String queryMaterialStatus (HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request); 	
		String deptIndex = request.getParameter("deptIndex");
		String queryDate = request.getParameter("queryDate");
		 
		if (StringUtils.isEmpty(deptIndex)) {
			GenericValue user = AccountHelper.getUserInfo(request, delegator);
			deptIndex = UtilFormatOut.checkNull(user.getString("deptIndex"));
		}
		String usingObjectId = request.getParameter("equipmentId");
		String mtrGrp = request.getParameter("mtrGrp");
		String mtrNum = request.getParameter("mtrNum");

		Map materialStatusMap = new HashMap();

		if (StringUtils.isNotEmpty(queryDate)) {

			if (queryDate.equals("2")) {
				String status = "STO-OUT";
				materialStatusMap.put("status", status);
			} else {
				String status = ConstantsMcs.CABINET_NEW;
				materialStatusMap.put("status", status);
			}

			Calendar c = Calendar.getInstance();
			// Date now = c.getTime();
			c.add(Calendar.DATE, -Integer.parseInt(queryDate));
			Date lastMonth = c.getTime();
			String startDate = "";
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String endDate = df2.format(lastMonth);

			materialStatusMap.put("startDate", startDate);
			materialStatusMap.put("endDate", endDate);

		} else {
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			String status = request.getParameter("status");

			materialStatusMap.put("status", status);

			Calendar c = Calendar.getInstance();
			Date now = c.getTime();
			c.add(Calendar.DATE, -7);
			Date initStartdate = c.getTime();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

			if (StringUtils.isEmpty(endDate)) {
				endDate = df.format(now);
			}
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");

			if (StringUtils.isEmpty(startDate)) {
				startDate = df2.format(initStartdate);
			}

			materialStatusMap.put("startDate", startDate);
			materialStatusMap.put("endDate", endDate);
		}

		materialStatusMap.put("deptIndex", deptIndex);
		materialStatusMap.put("usingObjectId", usingObjectId);
		materialStatusMap.put("mtrGrp", mtrGrp);
		materialStatusMap.put("mtrNum", mtrNum);

		try {
			List statusList = delegator.findAllCache("McsMaterialStatusCode",UtilMisc.toList("seqNum"));
			request.setAttribute("statusList", statusList);
			
			List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
			request.setAttribute("deptList", deptList);
			
			List materialStatusList = ReportHelper.getMaterialStatusList(delegator, materialStatusMap);
			request.setAttribute("materialStatusList", materialStatusList);
			
			request.setAttribute("deptIndex", deptIndex);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "sucess";
	}
	
	/**
	 * 物料状态汇总 设备信息初始化
	 * @param request
	 * @param response
	 * @return
	 */ 	
	public static void getJsonEquipmentList (HttpServletRequest request, HttpServletResponse response) {		
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request); 		
		String deptIndex = request.getParameter("equipmentDept");		
	    
		JSONArray eqpJson = new JSONArray();
		JSONObject blank = new JSONObject(UtilMisc.toMap("equipmentId", ""));
		eqpJson.put(blank);
		
	    try {
	    	Map map = McsCommonHelper.getEquipmentFiledsParam(delegator, deptIndex);
			
	    	List objectIdList = delegator.findByAnd("Equipment", map, UtilMisc.toList("equipmentId"));
	    	for ( Iterator it = objectIdList.iterator(); it.hasNext(); ) { 
				GenericValue gv = (GenericValue) it.next();
				JSONObject object = new JSONObject();
				object.put("equipmentId", gv.getString("equipmentId"));
				eqpJson.put(object);
	    	}
			response.getWriter().write(eqpJson.toString());	 
	    } catch(Exception e ) {	    	 
	        Debug.logError(e.getMessage(), module); 
	        request.setAttribute("_ERROR_MESSAGE_", e.getMessage());   
	    }		
	}
	
	/**
     * 物料状态汇总 状态信息初始化
     * @param request
     * @param response
     * @return
     */ 	
	public static void getJsonStatusList (HttpServletRequest request, HttpServletResponse response) {		
		 GenericDelegator delegator = CommonUtil.getMcsDelegator(request); 			     
	     JSONArray mtrGrpJson = new JSONArray();
	     
	     try {
	    	 JSONObject blank = new JSONObject(UtilMisc.toMap("status", "", "description", ""));
			 mtrGrpJson.put(blank);
	    	 List statusList = delegator.findAllCache("McsMaterialStatusCode", UtilMisc.toList("seqNum")); 

	    	 for ( Iterator it = statusList.iterator(); it.hasNext(); ) { 
				GenericValue gv = (GenericValue) it.next();
				JSONObject object = new JSONObject();
				object.put("status", gv.getString("status"));
				object.put("description", gv.getString("description"));
	    		mtrGrpJson.put(object);
	    	 }
				response.getWriter().write(mtrGrpJson.toString());	 
	     } catch(Exception e ) {	    	 
	            Debug.logError(e.getMessage(), module); 
	            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());  
	     }		
	}
	
	/**
     * 领料单查询
     * @param request
     * @param response
     * @return
     */ 	
	public static String queryStoReqEntry  (HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String deptIndex = request.getParameter("deptIndex");
		String vendorBatchNum = request.getParameter("vendorBatchNum");
		String mtrGrp = request.getParameter("mtrGrp");
		String mtrNum = request.getParameter("mtrNum");
		String status = request.getParameter("status");

		if (StringUtils.isEmpty(deptIndex)) {
			GenericValue user = AccountHelper.getUserInfo(request, delegator);
			deptIndex = UtilFormatOut.checkNull(user.getString("deptIndex"));
			request.setAttribute("deptIndex", deptIndex);
		}
		
	 	Calendar c = Calendar.getInstance(); 
	 	Date now = c.getTime(); 
	 	c.add(Calendar.DATE, -30); 
	 	Date lastMonth = c.getTime(); 
     
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");   
	    
	 	if (StringUtils.isEmpty(endDate)) {
	 		endDate = df.format(now); 
	 	}
	    
	    if (StringUtils.isEmpty(startDate)) {
	      startDate = df.format(lastMonth);   
	    }
	    
		Map stoReqMap = new HashMap();
		stoReqMap.put("startDate", startDate);
		stoReqMap.put("endDate", endDate);
		stoReqMap.put("deptIndex", deptIndex);
		stoReqMap.put("vendorBatchNum", vendorBatchNum);
		stoReqMap.put("mtrGrp", mtrGrp);
		stoReqMap.put("mtrNum", mtrNum);
		stoReqMap.put("status", status);
		
		try {		
	    	List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept")); 
	    	request.setAttribute("deptList", deptList);	
	    	List stoReqList = ReportHelper.getStoReqList(delegator, stoReqMap);
	    	request.setAttribute("stoReqList", stoReqList);	    	
		} catch (Exception e ) {			
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());  
            return "error";						
		}	
		
		return "sucess";
	}
	
	/**
     * 备耗件领用金额汇总
     * @param request
     * @param response
     * @return
     */ 	
	public static String queryHaojianLingMaterialPriceEntry (HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String deptIndex = request.getParameter("deptIndex");

		
		
	 	Calendar c = Calendar.getInstance(); 
	 	Date now = c.getTime(); 
	 	c.add(Calendar.DATE, -30); 
	 	Date lastMonth = c.getTime(); 
     
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");   
	    
	 	if (StringUtils.isEmpty(endDate)) {
	 		endDate = df.format(now); 
	 	}
	    
	    if (StringUtils.isEmpty(startDate)) {
	      startDate = df.format(lastMonth);   
	    }
	    
		Map stoReqMap = new HashMap();
		stoReqMap.put("startDate", startDate);
		stoReqMap.put("endDate", endDate);
		stoReqMap.put("deptIndex", deptIndex);
		
		try {		
	    	List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept")); 
	    	request.setAttribute("deptList", deptList);	
	    	List HaojianLingList = ReportHelper.getHaojianLingList(delegator, stoReqMap);
	    	request.setAttribute("HaojianLingList", HaojianLingList);	    	
		} catch (Exception e ) {			
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());  
            return "error";						
		}	
		
		return "sucess";
	}
	
	/**
     * 备耗件使用金额汇总
     * @param request
     * @param response
     * @return
     */ 	
	public static String queryHaojianUseMaterialPriceEntry (HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String deptIndex = request.getParameter("deptIndex");
		String equipmentId = request.getParameter("equipmentId");
		String equipmentType = request.getParameter("equipmentType");
		String toolGroup = request.getParameter("toolGroup");		
	
		try {
			request.setAttribute("startDate", startDate);
			request.setAttribute("endDate", endDate);
			
			List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
			request.setAttribute("deptList", deptList);
			
			if (StringUtils.isEmpty(startDate)) {
				//首次打开页面
				return "sucess";
			}
			
			Map paramMap = new HashMap();
			paramMap.put("startDate", startDate);
			paramMap.put("endDate", endDate);
			paramMap.put("deptIndex", deptIndex);
			paramMap.put("equipmentId", equipmentId);
			paramMap.put("equipmentType", equipmentType);
			paramMap.put("toolGroup", toolGroup);
			
			List HaojianUseList = ReportHelper.getHaojianShiList(delegator, paramMap);
			request.setAttribute("HaojianUseList", HaojianUseList);			
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "sucess";
	}
	
	/**
     * 备耗件使用金额明细
     * @param request
     * @param response
     * @return
      */	
	public static String queryHaojianUseDetailPriceEntry (HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String deptIndex = request.getParameter("deptIndex");
		String equipmentId = request.getParameter("equipmentId");
		String equipmentType = request.getParameter("equipmentType");
		String toolGroup = request.getParameter("toolGroup");		
		String mtrGrp = request.getParameter("mtrGrp");
		String mtrNum = request.getParameter("mtrNum");		
				    
		try {
			request.setAttribute("startDate", startDate);
			request.setAttribute("endDate", endDate);
			
			List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
			request.setAttribute("deptList", deptList);
			
			if (StringUtils.isEmpty(startDate)) {
				//首次打开页面
				return "sucess";
			}
			
			Map paramMap = new HashMap();
			paramMap.put("startDate", startDate);
			paramMap.put("endDate", endDate);
			paramMap.put("deptIndex", deptIndex);
			paramMap.put("equipmentId", equipmentId);
			paramMap.put("equipmentType", equipmentType);
			paramMap.put("toolGroup", toolGroup);
			paramMap.put("mtrGrp", mtrGrp);
			paramMap.put("mtrNum", mtrNum);		
			
			List HaojianUseListM = ReportHelper.getHaojianUseListM(delegator, paramMap);
			request.setAttribute("HaojianUseListM", HaojianUseListM);			
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "sucess";
	}
	
	
	/**
     * 按设备查询历史使用
     * @param request
     * @param response
     * @return
     */ 		
	public static String queryUsingHistoryByEqpt (HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String deptIndex = request.getParameter("deptIndex");

		if (StringUtils.isEmpty(deptIndex)) {
			GenericValue user = AccountHelper.getUserInfo(request, delegator);
			deptIndex = UtilFormatOut.checkNull(user.getString("deptIndex"));
		}
		String usingObjectId = request.getParameter("equipmentId");
		String mtrGrp = request.getParameter("mtrGrp");
		String mtrNum = request.getParameter("mtrNum");

		Calendar c = Calendar.getInstance();
		Date now = c.getTime();
		c.add(Calendar.DATE, -7);
		Date lastMonth = c.getTime();		

		if (StringUtils.isEmpty(endDate)) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			endDate = df.format(now);
		}		

		if (StringUtils.isEmpty(startDate)) {
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			startDate = df2.format(lastMonth);
		}
		
		Map usingHistory = new HashMap();
		usingHistory.put("startDate", startDate);
		usingHistory.put("endDate", endDate);
		usingHistory.put("deptIndex", deptIndex);
		usingHistory.put("usingObjectId", usingObjectId);
		usingHistory.put("mtrGrp", mtrGrp);
		usingHistory.put("mtrNum", mtrNum);

		try {
			// List statusList = delegator.findAllCache("McsMaterialStatusCode", UtilMisc.toList("seqNum")); 
			// request.setAttribute("statusList", statusList);			
			List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
			request.setAttribute("deptList", deptList);
			List usingHistoryList = ReportHelper.getUsingHistoryList(delegator, usingHistory);
			request.setAttribute("materialStatusList", usingHistoryList);
			request.setAttribute("deptIndex", deptIndex);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "sucess";
	}
	
	/**
     * 物料追踪
     * @param request
     * @param response
     * @return
     */ 		
	public static String materialTraceEntry(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		
		String deptIndex = request.getParameter("deptIndex");
		if (StringUtils.isEmpty(deptIndex)) {
			GenericValue user = AccountHelper.getUserInfo(request, delegator);
			deptIndex = UtilFormatOut.checkNull(user.getString("deptIndex"));
		}
		
		String mtrGrp = request.getParameter("mtrGrp");
		String mtrNum = request.getParameter("mtrNum");

		try {
			if (StringUtils.isNotEmpty(startDate)) {
				Map paraMap = new HashMap();
				paraMap.put("startDate", startDate);
				paraMap.put("endDate", endDate);
				paraMap.put("deptIndex", deptIndex);
				paraMap.put("mtrGrp", mtrGrp);
				paraMap.put("mtrNum", mtrNum);
				
				List materialTraceList = ReportHelper.getMaterialTraceList(delegator, paraMap);			
				request.setAttribute("materialStatusList", materialTraceList);
			}
			
			List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
			request.setAttribute("deptList", deptList);
			
			request.setAttribute("deptIndex", deptIndex);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "sucess";
	}
	
	/**
	 * 季度异常用量分析
	 * @param request
	 * @param response
	 * @return
	 */ 		
	public static String materialQuaterAnalysisEntry(HttpServletRequest request, HttpServletResponse response) {
	    GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
//	    String startDate = request.getParameter("startDate");
	    String endDate = request.getParameter("endDate");
	    String deptIndex = request.getParameter("deptIndex");
	    String sortby = request.getParameter("sortby");
	    if (StringUtils.isEmpty(deptIndex)) {
	        GenericValue user = AccountHelper.getUserInfo(request, delegator);
	        deptIndex = UtilFormatOut.checkNull(user.getString("deptIndex"));
	    }
	    
	    String mtrGrp = request.getParameter("mtrGrp");
	    String mtrNum = request.getParameter("mtrNum");
	    
	    try {
	        if (StringUtils.isNotEmpty(endDate)) {
	            Map paraMap = new HashMap();
//	            paraMap.put("startDate", startDate);
	            paraMap.put("endDate", endDate);
	            paraMap.put("deptIndex", deptIndex);
	            paraMap.put("mtrGrp", mtrGrp);
	            paraMap.put("mtrNum", mtrNum);
	            paraMap.put("sortby", sortby);
	            
	            List materialTraceList = ReportHelper.getMaterialQuaterAnalysisList(delegator, paraMap);			
	            request.setAttribute("materialStatusList", materialTraceList);
	        }
	        
	        List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
	        request.setAttribute("deptList", deptList);
	        
	        request.setAttribute("deptIndex", deptIndex);
	    } catch (Exception e) {
	        Debug.logError(e.getMessage(), module);
	        request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
	        return "error";
	    }
	    
	    return "sucess";
	}
	
	/**
	 * 季度异常用量分析 实际用量详细
	 * @param request
	 * @param response
	 * @return
	 */ 		
	public static String materialQuaterAnalysisUsing(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
		String endDate = request.getParameter("endDate");
		String deptIndex = request.getParameter("deptIndex");
		String usingObjectId = request.getParameter("equipmentId");
		String mtrNum = request.getParameter("mtrNum");
		
		Map usingHistory = new HashMap();
		usingHistory.put("endDate", endDate);
		usingHistory.put("deptIndex", deptIndex);
		usingHistory.put("usingObjectId", usingObjectId);
		usingHistory.put("mtrNum", mtrNum);

		try {
			List usingHistoryList = ReportHelper.getMaterialQuaterAnalysisUsing(delegator, usingHistory);
			request.setAttribute("materialStatusList", usingHistoryList);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		
		return "sucess";
	}
	
	/**
     * 设备使用金额汇总
     * @param request
     * @param response
     * @return
     */ 		
	public static String queryEqpMaterialPriceEntry(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String deptIndex = request.getParameter("deptIndex");
		String usingObjectId = request.getParameter("equipmentId");
		String mtrGrp = request.getParameter("mtrGrp");
		String mtrNum = request.getParameter("mtrNum");

		Calendar c = Calendar.getInstance();
		Date now = c.getTime();
		if (StringUtils.isEmpty(endDate)) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			endDate = df.format(now);
		}		

		if (StringUtils.isEmpty(startDate)) {			
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			c.add(Calendar.DATE, -7);
			startDate = df2.format(c.getTime());
		}
		
		try {
			request.setAttribute("startDate", startDate);
			request.setAttribute("endDate", endDate);
			
			List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
			request.setAttribute("deptList", deptList);
			
			if (StringUtils.isEmpty(request.getParameter("startDate"))) {
				//首次打开页面
				return "sucess";
			}
			
			Map paramMap = new HashMap();
			paramMap.put("startDate", startDate);
			paramMap.put("endDate", endDate);
			paramMap.put("deptIndex", deptIndex);
			paramMap.put("usingObjectId", usingObjectId);
			paramMap.put("mtrGrp", mtrGrp);
			paramMap.put("mtrNum", mtrNum);
			
			List eqpMtrGrpPriceList = ReportHelper.getEqpMtrGrpPriceList(delegator, paramMap);
			request.setAttribute("eqpMtrGrpPriceList", eqpMtrGrpPriceList);
			
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "sucess";
	}
	
	/**
     * 设备使用金额汇总 详细
     * @param request
     * @param response
     * @return
     */ 		
	public static String queryEqpMaterialPrice(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String deptIndex = request.getParameter("deptIndex");
		String usingObjectId = request.getParameter("equipmentId");
		String mtrGrp = request.getParameter("mtrGrp");
		String mtrNum = request.getParameter("mtrNum");
	
		try {
			request.setAttribute("startDate", startDate);
			request.setAttribute("endDate", endDate);
			
			List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
			request.setAttribute("deptList", deptList);
			
			Map paramMap = new HashMap();
			paramMap.put("startDate", startDate);
			paramMap.put("endDate", endDate);
			paramMap.put("deptIndex", deptIndex);
			paramMap.put("usingObjectId", usingObjectId);
			paramMap.put("mtrGrp", mtrGrp);
			paramMap.put("mtrNum", mtrNum);
			
			List eqpMtrPriceList = ReportHelper.getEqpMtrPriceList(delegator, paramMap);
			request.setAttribute("eqpMtrPriceList", eqpMtrPriceList);
			
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "sucess";
	}
	
	/**
     * 线边仓查询
     * @param request
     * @param response
     * @return
     */ 		
	public static String queryStoCabinetEntry(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);		
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");

		Calendar c = Calendar.getInstance();
		Date now = c.getTime();
		c.add(Calendar.DATE, -7);
		Date initStartdate = c.getTime();
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (StringUtils.isEmpty(endDate)) {
			endDate = df.format(now);
		}

		if (StringUtils.isEmpty(startDate)) {
			startDate = df.format(initStartdate);
		}
		
		String deptIndex = request.getParameter("deptIndex");
		if (StringUtils.isEmpty(deptIndex)) {
			GenericValue user = AccountHelper.getUserInfo(request, delegator);
			deptIndex = UtilFormatOut.checkNull(user.getString("deptIndex"));
		}
		String mtrGrp = request.getParameter("mtrGrp");
		String mtrNum = request.getParameter("mtrNum");
		
		Map materialStatusMap = new HashMap();
		materialStatusMap.put("startDate", startDate);
		materialStatusMap.put("endDate", endDate);
		materialStatusMap.put("deptIndex", deptIndex);
		materialStatusMap.put("mtrGrp", mtrGrp);
		materialStatusMap.put("mtrNum", mtrNum);

		try {			
			List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
			request.setAttribute("deptList", deptList);
			
			List materialStatusList = ReportHelper.getCabinetListByUpdateTime(delegator, materialStatusMap);
			request.setAttribute("materialStatusList", materialStatusList);
			
			request.setAttribute("deptIndex", deptIndex);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "sucess";
	}
	
	/**
     * 备件寿命查询
     * @param request
     * @param response
     * @return
     */ 		
	public static String queryUsePeriodEntry(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);		
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String deptIndex = request.getParameter("deptIndex");
		String usingObjectId = request.getParameter("equipmentId");
		String mtrGrp = request.getParameter("mtrGrp");
		String mtrNum = request.getParameter("mtrNum");
		
		if (StringUtils.isEmpty(endDate)) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, 30);
			Date nextMonth = c.getTime();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			endDate = df.format(nextMonth);
		}		
		
		if (StringUtils.isEmpty(deptIndex)) {
			GenericValue user = AccountHelper.getUserInfo(request, delegator);
			deptIndex = UtilFormatOut.checkNull(user.getString("deptIndex"));
		}
		
		Map materialStatusMap = new HashMap();
		materialStatusMap.put("startDate", startDate);
		materialStatusMap.put("endDate", endDate);
		materialStatusMap.put("deptIndex", deptIndex);
		materialStatusMap.put("usingObjectId", usingObjectId);
		materialStatusMap.put("mtrGrp", mtrGrp);
		materialStatusMap.put("mtrNum", mtrNum);

		try {			
			List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
			request.setAttribute("deptList", deptList);
			
			List materialStatusList = ReportHelper.getUsePeriodList(delegator, materialStatusMap);
			request.setAttribute("materialStatusList", materialStatusList);
			
			request.setAttribute("deptIndex", deptIndex);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "sucess";
	}
	
	/**
     * 特殊寿命规范查询
     * @param request
     * @param response
     * @return
     */
	public static String querySpecialUsableTimeEntry(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
		
		try {
			//List timeList = delegator.findAll("McsSpecialUsableTime", UtilMisc.toList("mtrNum", "usingObjectId"));
			
			EntityWhereString con = new EntityWhereString("usable_time_limit>0");
			List timeList = delegator.findByCondition("McsMtrObject", con, null, UtilMisc.toList("mtrNum", "usingObjectId"));
			request.setAttribute("timeList", timeList);
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}		
		
		return "sucess";
	}
	
	/**
     * 备件寿命延期使用 
     * mrbDate = updateTime + expandUseDays, expandUseDays = usableTimeLimit / 2
     * @param request
     * @param response
     * @return
     */
	public static String expandUseDays(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
		String materialStatusIndex = request.getParameter("materialStatusIndex");
		String expandUseDays = request.getParameter("expandUseDays");
		
		try {
			GenericValue msGv = delegator.findByPrimaryKey("McsMaterialStatus", UtilMisc.toMap("materialStatusIndex", materialStatusIndex));
			Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(msGv.getTimestamp("updateTime").getTime());            
            calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(expandUseDays));
            Timestamp mrbDate = new Timestamp(calendar.getTimeInMillis());
			
			msGv.put("mrbDate", mrbDate);
			msGv.put("mrbTransBy", CommonUtil.getUserNo(request));
			msGv.put("mrbTransTime", UtilDateTime.nowTimestamp());
			delegator.store(msGv);
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}		
		
		return "sucess";
	}
	
	/**
     * 维修与清洗查询
     * @param request
     * @param response
     * @return
     */ 		
	public static String queryRepairWashEntry(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);		
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");

		Calendar c = Calendar.getInstance();
		Date now = c.getTime();
		c.add(Calendar.DATE, -7);
		Date initStartdate = c.getTime();
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (StringUtils.isEmpty(endDate)) {
			endDate = df.format(now);
		}

		if (StringUtils.isEmpty(startDate)) {
			startDate = df.format(initStartdate);
		}
		
		String deptIndex = request.getParameter("deptIndex");
		if (StringUtils.isEmpty(deptIndex)) {
			GenericValue user = AccountHelper.getUserInfo(request, delegator);
			deptIndex = UtilFormatOut.checkNull(user.getString("deptIndex"));
		}
		
		String mtrGrp = request.getParameter("mtrGrp");
		String mtrNum = request.getParameter("mtrNum");
		
		String queryType = request.getParameter("queryType");
		if (StringUtils.isEmpty(queryType)) {
			queryType = "REPAIR";
		}
		
		Map materialStatusMap = new HashMap();
		materialStatusMap.put("startDate", startDate);
		materialStatusMap.put("endDate", endDate);
		materialStatusMap.put("deptIndex", deptIndex);
		materialStatusMap.put("mtrGrp", mtrGrp);
		materialStatusMap.put("mtrNum", mtrNum);
		materialStatusMap.put("queryType", queryType);

		try {			
			List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
			request.setAttribute("deptList", deptList);
			
			List materialStatusList = ReportHelper.getRepairWashListByUpdateTime(delegator, materialStatusMap);
			request.setAttribute("materialStatusList", materialStatusList);
			
			request.setAttribute("deptIndex", deptIndex);
			request.setAttribute("queryType", queryType);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "sucess";
	}
	
	/**
     * 线边仓库存金额查询
     * @param request
     * @param response
     * @return
     */ 		
	public static String queryCabinetStockEntry(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
		
		String costCenter = request.getParameter("costCenter");
		String mtrGrp = request.getParameter("mtrGrp");
		
		if(StringUtils.isNotEmpty(costCenter) && StringUtils.isNotEmpty(mtrGrp)) {
			request.setAttribute("costCenter", costCenter);
			request.setAttribute("mtrGrp", mtrGrp);
			
			StringBuffer sql = new StringBuffer(" SELECT * FROM RXGZ_MCS_STOCK T ");
			if (Constants.CALL_TP_FLAG) {
				sql = new StringBuffer(" SELECT * FROM FAB1_MCS_STOCK T ");
			}
//			else if (Constants.CALL_ASURA_FLAG) {
//				sql = new StringBuffer(" SELECT * FROM FAB5_MCS_STOCK T ");
//			}
			sql.append(" WHERE T.COST_CENTER = '").append(costCenter).append("'");
			sql.append(" AND T.MTR_GRP = '").append(mtrGrp).append("'");
			sql.append(" ORDER BY T.PLANT DESC, T.DOC_TIME");
			try {
				List cabinetStockList = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
				request.setAttribute("cabinetStockList", cabinetStockList);
				
				BigDecimal stockAmount = new BigDecimal(Double.toString(0));  // khkd
				BigDecimal stockAmount2 = new BigDecimal(Double.toString(0));  // rmb
				for (int i = 0; i < cabinetStockList.size(); i++) {
					Map item = (Map)cabinetStockList.get(i);
					stockAmount = stockAmount.add(new BigDecimal(item.get("MONEY").toString()));
					stockAmount2 = stockAmount2.add(new BigDecimal(item.get("AMOUNT").toString()));
				}
				request.setAttribute("stockAmount", stockAmount);
				request.setAttribute("stockAmount2", stockAmount2);
				
			} catch (SQLProcessException e) {
				Debug.logError(e.getMessage(), module);
				request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
				return "error";
			}
		}
		return "sucess";
	}
	
	/**
     * 关键备件寿命管控报表
     * @param request
     * @param response
     * @return
     */ 		
	public static String keyPartsOverdueHistEntry(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try {
			String userNo = CommonUtil.getUserNo(request);
			String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);
			request.setAttribute("deptIndex", deptIndex);
			request.setAttribute("queryMonth", "all");
			
			StringBuffer sql = new StringBuffer(" SELECT t.* FROM KEY_PARTS_LIFE_CONTROL_VIEW t");
			sql.append(" where t.dept_index = '").append(deptIndex).append("'");
			List results = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
			request.setAttribute("delayHistList", results);
			
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "sucess";
	}
	
	/**
     * 获取关键备件寿命管控记录
     * @param request
     * @param response
     * @return
     */ 		
	public static String queryKeyPartUseOverdueHist(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try {			
			String deptIndex = request.getParameter("deptSelect");
			String queryMonth = request.getParameter("queryMonth");
			
			request.setAttribute("deptIndex", deptIndex);
			request.setAttribute("queryMonth", queryMonth);
			List results = new ArrayList();
			if (queryMonth.equals("all")) {
				StringBuffer sql = new StringBuffer(" SELECT t.* FROM KEY_PARTS_LIFE_CONTROL_VIEW t");
				if (!deptIndex.equals("all")) {
					sql.append(" where t.dept_index = '").append(deptIndex).append("'");
				}
				results = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
			}
			else {
				Calendar c = Calendar.getInstance();
				Date today = c.getTime();
				
				c.set(c.get(Calendar.YEAR), Integer.valueOf(queryMonth) - 1, 26);
				Date endDate = c.getTime();
				c.add(Calendar.MONTH, -1);
				Date startDate = c.getTime();
				SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
				String startDateStr = df.format(startDate);
				String endDateStr = df.format(endDate);
				
				// 超时在用的关键备件
				StringBuffer sql = new StringBuffer("SELECT * FROM KEY_PARTS_LIFE_CONTROL_VIEW T WHERE T.LIFE_CONTROL = 'NG'");
				if (!deptIndex.equals("all")) {
					sql.append(" and t.dept_index = '").append(deptIndex).append("'");
				}
				sql.append(" and t.overdue_time <= to_date('").append(endDateStr).append("','yyyy/mm/dd')");
				
				results = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
				Integer overdueNum = Integer.valueOf(results.size());
				request.setAttribute("overdueNum", overdueNum.toString());
				
				String sql1 = "select * from key_parts_use a, key_eqp_parts b where a.key_parts_id = b.key_parts_id and b.enable = 'Y' and b.is_alarm = 'Y'";
				StringBuffer kpuSql = new StringBuffer();
				kpuSql.append("select count(*) num from(");
				kpuSql.append(sql1).append(" and a.status = 'USING'"); // 在用的
				if (!deptIndex.equals("all")) {
					kpuSql.append("and b.maint_dept = '").append(deptIndex).append("'");
				}
				kpuSql.append(" union ").append(sql1);
				if (!deptIndex.equals("all")) {
					kpuSql.append("and b.maint_dept = '").append(deptIndex).append("'");
				}
				// 期间内下线的(UPDATE_TIME)
				kpuSql.append(" and a.status = 'OFFLINE' and a.update_time between to_date('").append(startDateStr).append("','yyyy/mm/dd')");
				kpuSql.append(" and to_date('").append(endDateStr).append("','yyyy/mm/dd'))");
				List re = SQLProcess.excuteSQLQuery(kpuSql.toString(), delegator);
				Map num = (Map) re.get(0);
				Integer kpuNum = Integer.valueOf(num.get("NUM").toString());
				request.setAttribute("kpuNum", kpuNum.toString());
				
				BigDecimal num1 = new BigDecimal(kpuNum - overdueNum);
				BigDecimal num2 = new BigDecimal(kpuNum);
				//下面将结果转化成百分比
				NumberFormat percent = NumberFormat.getPercentInstance();
				percent.setMaximumFractionDigits(2);
				String percentStr = percent.format(num1.divide(num2, 3, RoundingMode.HALF_UP));
				request.setAttribute("percent", percentStr);
			}
			request.setAttribute("delayHistList", results);
			
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "sucess";
	}
}

