package com.csmc.mcs.webapp.common.event;

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
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import com.csmc.mcs.webapp.common.helper.McsCommonHelper;
import com.csmc.mcs.webapp.expiration.event.ExpirationEvent;
import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.GeneralEvents;

/**
 * 类 McsCommonEvent.java 
 * @version  1.0  2009-7-1
 * @author   dinghh
 */
public class McsCommonEvent extends GeneralEvents{
	public static final String module = McsCommonEvent.class.getName();
	
	/**
     * 查询可用物料组
     * @param request(useBySuit, enableBackStock)
     * @param response
     * @return Json mtrGrp List
     */
    public static void getJsonCommonMtrGrp(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        String useById = request.getParameter("useById");
        String useByQty = request.getParameter("useByQty");
        String useBySuit = request.getParameter("useBySuit");
        String useByBarcode = request.getParameter("useByBarcode");
        String enableBackStock = request.getParameter("enableBackStock");
        
		Map map = new HashMap();
		map.put("enabled", ConstantsMcs.INTEGER_1);
		
		if (StringUtils.isNotEmpty(useById)) {
			map.put("useById", useById);
		}
		
		if (StringUtils.isNotEmpty(useByQty)) {
			map.put("useByQty", useByQty);
		}
		
		if (StringUtils.isNotEmpty(useBySuit)) {
			map.put("useBySuit", useBySuit);
		}
		
		if (StringUtils.isNotEmpty(useByBarcode)) {
			map.put("useByBarcode", useByBarcode);
		}
		
		if (StringUtils.isNotEmpty(enableBackStock)) {
			map.put("enableBackStock", enableBackStock);
		}
		
        JSONArray mtrGrpJson = new JSONArray();
        try {
			JSONObject blank = new JSONObject(UtilMisc.toMap("mtrGrp", "", "mtrGrpDesc", ""));
			mtrGrpJson.put(blank);
			
			List mtrGrpList = delegator.findByAndCache("McsMaterialGroup",
					map, 
					UtilMisc.toList("mtrGrp"));
			for (Iterator it = mtrGrpList.iterator(); it.hasNext();) {
				GenericValue gv = (GenericValue) it.next();
				JSONObject object = new JSONObject();
				object.put("mtrGrp", gv.getString("mtrGrp"));
				object.put("mtrGrpDesc", gv.getString("mtrGrpDesc"));
				mtrGrpJson.put(object);
			}
			response.getWriter().write(mtrGrpJson.toString());
        } catch(Exception e) {
            Debug.logError(e.getMessage(), module);
        }         
    } 
        
    /**
     * 查询部门可用物料号
     * @param request(mtrGrp)
     * @param response
     * @return Json mtrNum List
     */
    public static void getJsonCommonMtrNum(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        GenericValue user = AccountHelper.getUserInfo(request, delegator);
        Long userDeptIndex = user.getLong("deptIndex");
        
        String mtrGrp = request.getParameter("mtrGrp");
        
		Map map = new HashMap();
		map.put("enabled", ConstantsMcs.INTEGER_1);
		map.put("inControl", ConstantsMcs.INTEGER_1);
		map.put("deptIndex", userDeptIndex);
		
		if (StringUtils.isNotEmpty(mtrGrp)) {
			map.put("mtrGrp", mtrGrp);
		}
		
        JSONArray jsonArray = new JSONArray();
        try {
			JSONObject blank = new JSONObject(UtilMisc.toMap("mtrNum", "", "mtrDesc", ""));
			jsonArray.put(blank);
			
			List list = delegator.findByAnd("McsMaterialInfo",
					map, 
					UtilMisc.toList("mtrNum"));
			for (Iterator it = list.iterator(); it.hasNext();) {
				GenericValue gv = (GenericValue) it.next();
				JSONObject object = new JSONObject();
				object.put("mtrNum", gv.getString("mtrNum"));
				object.put("mtrDesc", gv.getString("mtrDesc"));
				jsonArray.put(object);
			}
			response.getWriter().write(jsonArray.toString());
        } catch(Exception e) {
            Debug.logError(e.getMessage(), module);
        }         
    }
    
    /**
     * 查询套件
     * @param request(mtrGrp)
     * @param response
     * @return suit index and suit name
     */
    public static void getJsonCommonSuit(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        String mtrGrp = request.getParameter("mtrGrp");        
		
        JSONArray suitJson = new JSONArray();
        try {
			JSONObject blank = new JSONObject(UtilMisc.toMap("materialSuitIndex", "", "suitName", ""));
			suitJson.put(blank);
			
			List mtrGrpList = delegator.findByAnd("McsMaterialSuit", 
					UtilMisc.toMap("mtrGrp", mtrGrp), 
					UtilMisc.toList("suitName"));
			for (Iterator it = mtrGrpList.iterator(); it.hasNext();) {
				GenericValue gv = (GenericValue) it.next();
				JSONObject object = new JSONObject();
				object.put("materialSuitIndex", gv.getString("materialSuitIndex"));
				object.put("suitName", gv.getString("suitName"));
				suitJson.put(object);
			}
			response.getWriter().write(suitJson.toString());
        } catch(Exception e) {
            Debug.logError(e.getMessage(), module);
        }         
    }
    
    /**
	  * 使用时，根据部门deptIndex(默认登录人部门)查询Equipment
	  * 选择光刻胶时，deptIndex取DEPT_INDEX_PP，查询光刻部设备 20160616
	  * 参数equipmentId有值，则取单个设备 20161109
	  * @param request
	  * @param response
	  */
    public static void queryEquipmentByDeptIndex(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String deptIndex = request.getParameter("deptIndex");
		String equipmentId = request.getParameter("equipmentId");
		
		if (StringUtils.isEmpty(deptIndex)) {
			GenericValue user = AccountHelper.getUserInfo(request, delegator);
			deptIndex = user.getString("deptIndex");
		}

		JSONArray eqpJson = new JSONArray();
		try {
			Map map = McsCommonHelper.getEquipmentFiledsParam(delegator, deptIndex);
			if (StringUtils.isNotEmpty(equipmentId)) {
				map.put("equipmentId", equipmentId);
			}
			
			List equipmentList = delegator.findByAndCache("Equipment", map, UtilMisc.toList("equipmentId"));
			for (Iterator it = equipmentList.iterator(); it.hasNext();) {
				GenericValue gv = (GenericValue) it.next();
				JSONObject object = new JSONObject();
				object.put("equipmentId", gv.getString("equipmentId"));
				eqpJson.put(object);
			}
			response.getWriter().write(eqpJson.toString());
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}

    /**
	  * 查询EquipmentDept
	  * @param request
	  * @param response
	  */
	public static void getJsonCommonEquipmentDept(HttpServletRequest request, HttpServletResponse response) {		
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);       
		JSONArray eqpJson = new JSONArray();
				
		try {
			List equipmentDeptList = delegator.findAll("EquipmentDept");
			for (Iterator it = equipmentDeptList.iterator(); it.hasNext();) {
				GenericValue gv = (GenericValue) it.next();
				JSONObject object = new JSONObject();
				object.put("deptIndex", gv.getString("deptIndex"));
				object.put("equipmentDept", gv.getString("equipmentDept"));
				eqpJson.put(object);
			}
			response.getWriter().write(eqpJson.toString());
		} catch (Exception e) {
			Debug.logError(e.getMessage(), ExpirationEvent.module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}
	
	/**
	 * toolGroup信息初始化 
	 * @param request
	 * @param response
	 * @return
	 */ 	
	public static void getJsonToolGroup (HttpServletRequest request, HttpServletResponse response) {		
		GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
	    JSONArray mtrGrpJson = new JSONArray();
	     
	    try {
			JSONObject blank = new JSONObject(UtilMisc.toMap("toolGroup", ""));
			mtrGrpJson.put(blank);
	    	List objectIdList = delegator.findAllCache("EquipmentToolgroup",UtilMisc.toList("toolGroup")); 
	    	
	    	for ( Iterator it = objectIdList.iterator(); it.hasNext(); ) { 
				GenericValue gv = (GenericValue) it.next();
				JSONObject object = new JSONObject();
				object.put("toolGroup", gv.getString("toolGroup"));
	    		mtrGrpJson.put(object);
	    	}
			response.getWriter().write(mtrGrpJson.toString());	 
	    } catch(Exception e ) {
	        Debug.logError(e.getMessage(), module); 
	        request.setAttribute("_ERROR_MESSAGE_", e.getMessage());   
	    }		
	}
}
