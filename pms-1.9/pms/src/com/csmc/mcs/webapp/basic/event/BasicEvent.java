package com.csmc.mcs.webapp.basic.event;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityWhereString;

import com.csmc.mcs.webapp.basic.helper.BasicHelper;
import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.util.GeneralEvents;


/**
 * 类 BasicEvent.java 
 * @version  1.0  2009-7-22
 * @author   jiangjing
 */
public class BasicEvent extends GeneralEvents{
	public static final String module = BasicEvent.class.getName();    
	
	/**
     * 按照物料组查询物料信息
     * @param request
     * @param response
     * @return
     */    
    public static String queryMaterialListByMtrGrp(HttpServletRequest request, HttpServletResponse response) {    	
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String mtrGrp = request.getParameter("mtrGrp");
    	//String userNo = CommonUtil.getUserNo(request);
    	GenericValue user = AccountHelper.getUserInfo(request, delegator);
        String userDeptIndex = user.getString("deptIndex");
        String mtrNum = request.getParameter("mtrNum");
        
        try {
        	Map paramMap = new HashMap();
        	paramMap.put("mtrGrp", mtrGrp);
//        	paramMap.put("deptIndex", userDeptIndex);
        	paramMap.put("mtrNum" , mtrNum);        	
        	
			if (CommonUtil.isNotNull(mtrGrp)){
				// 1得到待维护物料List
				paramMap.put("enabled", String.valueOf(ConstantsMcs.INTEGER_0));
				paramMap.put("inControl", String.valueOf(ConstantsMcs.INTEGER_1));
				List disenableMateriaList = BasicHelper.getMaterialInfoList(delegator, paramMap);
				request.setAttribute("disenableMaterialList", disenableMateriaList);
				
				// 2得到已维护物料list				
				paramMap.put("enabled", String.valueOf(ConstantsMcs.INTEGER_1));
				paramMap.put("inControl", String.valueOf(ConstantsMcs.INTEGER_1));
				List enableMaterialList = BasicHelper.getMaterialInfoList(delegator, paramMap);
				request.setAttribute("enableMaterialList", enableMaterialList);
				
				// 3得到已禁用物料list				
				paramMap.put("enabled", String.valueOf(ConstantsMcs.INTEGER_1));
				paramMap.put("inControl", String.valueOf(ConstantsMcs.INTEGER_0));
				List outOfControlMaterialList = BasicHelper.getMaterialInfoList(delegator, paramMap);
				request.setAttribute("outOfControlMaterialList", outOfControlMaterialList);
			}
        }catch(Exception e){
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
	/**
     * 按照物料index查询物料信息
     * @param request
     * @param response
     * @return
     */    
    public static String getMaterialByMtrIndex(HttpServletRequest request, HttpServletResponse response) {   	
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String materialIndex = request.getParameter("materialIndex"); 
        String PrivFlag= AccountHelper.getPrivFlag(request, delegator);       
        if (PrivFlag != null || StringUtils.isNotEmpty(PrivFlag)) {
        // 判断报废入库是否有权限选择N的flag
        request.setAttribute("flag", "OK");
        }
        
        if (StringUtils.isEmpty(materialIndex)) {
        	materialIndex=(String) request.getAttribute("materialIndex");
        	
        	if(StringUtils.isEmpty(materialIndex)) {
            	materialIndex = null;        			
        	}
        }
        request.setAttribute("materialIndex", materialIndex);
    	//String userNo = CommonUtil.getUserNo(request);
        
        try {  	
        	GenericValue materialInfo = delegator.findByPrimaryKey("McsMaterialInfo", UtilMisc.toMap("materialIndex", materialIndex));
    	    // 得到相应物料Index对应的物料信息
    		request.setAttribute("materialInfo", materialInfo);
    		
        	if (materialIndex != null || StringUtils.isNotEmpty(materialIndex)) {
	        	//得到已维护的设备列表
	    		Map mtrObjectMap = new HashMap();
	    		mtrObjectMap.put("materialIndex", materialIndex);
	    		List mtrObjectList = delegator.findByAnd("McsMtrObject", mtrObjectMap,UtilMisc.toList("usingObjectId"));
				request.setAttribute("mtrObjectList", mtrObjectList);
        	}
        	
	        // 得到相应物料Index对应的物料信息
			//request.setAttribute("materialInfo", materialInfo);									
	        //request.setAttribute("_EVENT_MESSAGE_", request.getAttribute("_EVENT_MESSAGE_"));
        }catch(Exception e){
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";    	
    }
	
	/**
     * 查找SAP物料信息
     * @param request
     * @param response
     * @return
     */      
    public static String getSapMtrNum (HttpServletRequest request, HttpServletResponse response) {    	
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);    	
    	GenericValue user = AccountHelper.getUserInfo(request, delegator);
        String userDeptIndex = user.getString("deptIndex");    	
    	String mtrNum = request.getParameter("mtrNum").toUpperCase();
    	
        Map mtrNumInfo = new HashMap();
        mtrNumInfo.put("deptIndex", userDeptIndex);
        mtrNumInfo.put("mtrNum", mtrNum);
        
		try {			
			if (StringUtils.isNotEmpty(mtrNum)) { 
				List mtrNumList = BasicHelper.getSapMtrNumInfo(delegator, mtrNumInfo);
				request.setAttribute("mtrNumList", mtrNumList);
				request.setAttribute("_EVENT_MESSAGE_", "查询成功！");
			}
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}          
        
    	return "sucess";
    }
        
    /**
     * 维护物料信息
     * @param request
     * @param response
     * @return
     */  	
    public static String saveMaterial (HttpServletRequest request, HttpServletResponse response){  	
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);      	
        String materialIndex = request.getParameter("materialIndex");   
        String userNo = CommonUtil.getUserNo(request);
    	GenericValue user = AccountHelper.getUserInfo(request, delegator);
    	
        Map hashMap = new HashMap();        
        hashMap.put("deptIndex", user.getString("deptIndex"));
        hashMap.put("mtrNum", request.getParameter("mtrNum"));
        hashMap.put("mtrDesc", request.getParameter("mtrDesc"));
        hashMap.put("mtrGrp", request.getParameter("mtrGrp"));     
        hashMap.put("plant", request.getParameter("plant"));        
        hashMap.put("frozenTimeLimit", UtilFormatOut.checkNull(request.getParameter("frozenTimeLimit")));
        hashMap.put("maxFrozenTimeLimit", UtilFormatOut.checkNull(request.getParameter("maxFrozenTimeLimit")));
        hashMap.put("usableTimeLimit", request.getParameter("usableTimeLimit"));
        hashMap.put("preStoNumber", UtilFormatOut.checkNull(request.getParameter("preStoNumber")));
        hashMap.put("preAlarmDays", UtilFormatOut.checkNull(request.getParameter("preAlarmDays")));
        hashMap.put("needScrapStore", request.getParameter("needScrapStore"));
        hashMap.put("needVendorRecycle", request.getParameter("needVendorRecycle"));    	
    	hashMap.put("inControl", request.getParameter("inControl"));
    	hashMap.put("enabled", ConstantsMcs.INTEGER_1);
        hashMap.put("transBy", userNo);
        hashMap.put("updateTime", UtilDateTime.nowTimestamp());    
        
        //对于修改操作:管控带原来的值,维护状态更新
          if (StringUtils.isNotEmpty(materialIndex)) {       	
        	hashMap.put(ConstantsMcs.MATERIAL_INDEX, materialIndex);
            hashMap.put("evt", ConstantsMcs.EVT_UPDATE);
            request.setAttribute("materialIndex", materialIndex);
        } else {
        	Long addMaterialIndex = delegator.getNextSeqId(ConstantsMcs.MATERIAL_INDEX);
        	hashMap.put(ConstantsMcs.MATERIAL_INDEX, addMaterialIndex);        	
        	hashMap.put("evt", ConstantsMcs.EVT_INSERT);
        	request.setAttribute("materialIndex", addMaterialIndex.toString());
        }
              
        //数据插入正式表及历史表
		try {			
	        String msg = BasicHelper.saveMaterialInfo(delegator, hashMap);	
			request.setAttribute("_EVENT_MESSAGE_", "料号"+ msg + "保存成功！");
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}    
		return "success";
    }

    /**
     * 维护物料设备信息查询
     * @param request
     * @param response
     * @return
     */    
    public static String getMtrObject (HttpServletRequest request, HttpServletResponse response){
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);       
    	String materialIndex = request.getParameter("materialIndex");
    	GenericValue user = AccountHelper.getUserInfo(request, delegator);
        String deptIndex = user.getString("deptIndex");
    	 
        if (StringUtils.isEmpty(materialIndex)) {
        	materialIndex=(String) request.getAttribute("materialIndex");
        }
        request.setAttribute("materialIndex", materialIndex);    	
    	
    	try {
    		Map mtrObjectMap = new HashMap();
    		mtrObjectMap.put("materialIndex", materialIndex);
    		List mtrObjectList = delegator.findByAnd("McsMtrObject", mtrObjectMap, UtilMisc.toList("usingObjectId"));
    		List eqpTypeList=BasicHelper.getEqpTypeList(delegator,deptIndex);
    		List eqpList=BasicHelper.getEqpList(delegator,materialIndex,deptIndex);
    		request.setAttribute("mtrObjectList",mtrObjectList);
    		request.setAttribute("eqpTypeList", eqpTypeList);
    		request.setAttribute("eqpList", eqpList);    		
    	} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error"; 		
    	}
    	return "sucess";
    }
    
    /**
     * 维护物料设备Index查询物料设备信息
     * @param request
     * @param response
     * @return
     */    
    public static void getJsonMtrObjectByIndex (HttpServletRequest request, HttpServletResponse response){
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        String mtrObjectIndex = request.getParameter("mtrObjectIndex");
        try {
            GenericValue gv = delegator.findByPrimaryKey("McsMtrObject", UtilMisc.toMap("mtrObjectIndex", mtrObjectIndex));
            JSONObject mtrObjectInfo = new JSONObject();
            mtrObjectInfo.put("usingObjectId", gv.getString("usingObjectId"));
            
            if (CommonUtil.isNotNull(gv.getString("usableTimeLimit"))) {
            	mtrObjectInfo.put("usableTimeLimit", gv.getString("usableTimeLimit"));
            } else {
            	mtrObjectInfo.put("usableTimeLimit", "");
            }
            
            if (CommonUtil.isNotNull(gv.getString("objMaxUseAmount"))) {            	
            	mtrObjectInfo.put("objMaxUseAmount", gv.getString("objMaxUseAmount"));
            } else {
            	mtrObjectInfo.put("objMaxUseAmount", "");         	
            }
            
            if (CommonUtil.isNotNull(gv.getString("stdUseAmount"))) {            	
                mtrObjectInfo.put("stdUseAmount", gv.getString("stdUseAmount"));
            } else {
                mtrObjectInfo.put("stdUseAmount", "");         	
            }

            response.getWriter().write(mtrObjectInfo.toString());
        } catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());			
        }
    }
 
    /**
     * 新增/修改 物料设备
     * @param request
     * @param response
     * @return
     */     
    public static String saveMtrObject (HttpServletRequest request, HttpServletResponse response){
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
    	String mtrObjectIndex = request.getParameter("mtrObjectIndex");
    	String materialIndex = request.getParameter("materialIndex");
    	String equipmentType = request.getParameter("equipmentType");
    	String usingObjectId = request.getParameter("usingObjectId");
    	
    	if ( CommonUtil.isNull(usingObjectId) ){   		
    		usingObjectId=request.getParameter("usingObjectIdHidden");
    	}
    	
    	String usableTimeLimit = request.getParameter("usableTimeLimit");
    	String objMaxUseAmount = request.getParameter("objMaxUseAmount");
    	String stdUseAmount = request.getParameter("stdUseAmount");
        String userNo = CommonUtil.getUserNo(request);
    	GenericValue user = AccountHelper.getUserInfo(request, delegator);    	
  	   	
    	Map mtrObjectMap = new HashMap();
		mtrObjectMap.put("materialIndex", materialIndex);
    	mtrObjectMap.put("equipmentType", equipmentType);
    	mtrObjectMap.put("usingObjectId", usingObjectId);
    	mtrObjectMap.put("usableTimeLimit", usableTimeLimit);
    	mtrObjectMap.put("objMaxUseAmount", objMaxUseAmount);
    	mtrObjectMap.put("stdUseAmount", stdUseAmount);
        mtrObjectMap.put("deptIndex", user.getString("deptIndex"));
    	mtrObjectMap.put("transBy", userNo);
        mtrObjectMap.put("updateTime", UtilDateTime.nowTimestamp());  
              
    	if (StringUtils.isEmpty(mtrObjectIndex)) {
    		Long addMtrObjectIndex = delegator.getNextSeqId(ConstantsMcs.MTR_OBJECT_INDEX);
    		mtrObjectMap.put("mtrObjectIndex", addMtrObjectIndex); 
    		mtrObjectMap.put("evt", ConstantsMcs.EVT_INSERT);    		
    	} else {
    		mtrObjectMap.put("mtrObjectIndex", mtrObjectIndex);  		
    		mtrObjectMap.put("evt", ConstantsMcs.EVT_UPDATE);    		
    	}
    	
    	 try {
    		 BasicHelper.saveMtrObjectInfo(delegator, mtrObjectMap);
 			    		 
    		 request.setAttribute("_EVENT_MESSAGE_", "保存成功！"); 
    		 request.setAttribute("materialIndex", materialIndex);  		 
    	 } catch (Exception e) {
 			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
         }     	                                                     	
    	return "sucess";
    }
    
    /**
     * 删除 物料可用设备
     * @param request
     * @param response
     * @return
     */     
    public static String delMtrObjectByIndex (HttpServletRequest request, HttpServletResponse response){
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request); 	
    	String mtrObjectIndex = request.getParameter("mtrObjectIndex");
    	String materialIndex = request.getParameter("materialIndex");
    	String userNo = CommonUtil.getUserNo(request);
    	 
    	Map delMtrObjectMap = new HashMap();
    	delMtrObjectMap.put("mtrObjectIndex", mtrObjectIndex);
    	delMtrObjectMap.put("materialIndex", materialIndex);
    	delMtrObjectMap.put("transBy", userNo);
    	delMtrObjectMap.put("evt", ConstantsMcs.EVT_DELETE);
    	delMtrObjectMap.put("updateTime", UtilDateTime.nowTimestamp()); 
    	
    	try {
    		 BasicHelper.delMtrObjectInfo(delegator, delMtrObjectMap);
	   		 request.setAttribute("_EVENT_MESSAGE_", "删除成功！"); 
	   		 request.setAttribute("materialIndex", materialIndex);     		
    		
    	}  catch (Exception e) {    		
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";    		
    	}    	
    	return "sucess";
    }
    
    
    // ---------------------------------------------事件细项 jiangjing--------------------------------------		
		
	/**
     * 显示套件名称及描述
     * @param request
     * @param response
     * @return
     */
    public static String defineSuitMaterialEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String mtrGrp = request.getParameter("mtrGrp");
        //预设套件List为石英套件
		if (StringUtils.isEmpty(mtrGrp)) {
			 mtrGrp = ConstantsMcs.QUARTZ;
		}
		request.setAttribute("mtrGrp", mtrGrp);
    	        
        try{
	        // 显示查询后的套件 List
            Map paramMap = new HashMap();
			paramMap.put("mtrGrp", mtrGrp);

			List suitList = delegator.findByAnd("McsMaterialSuit", paramMap, UtilMisc.toList("suitName"));
			request.setAttribute("suitList", suitList);

        }catch(Exception e){
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /**
     * 增加套件名称
     * @param request
     * @param response
     * @return
     */
    public static String addSuit(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        String materialSuitIndex = request.getParameter("materialSuitIndex");
        String suitName = request.getParameter("suitName");
        String suitDesc = request.getParameter("suitDesc");
        String mtrGrp = request.getParameter("addMtrGrp");
        String userNo = CommonUtil.getUserNo(request); 
        Timestamp nowTs = UtilDateTime.nowTimestamp();
        
        Map hashMap = new HashMap();
        hashMap.put("materialSuitIndex", materialSuitIndex);
        hashMap.put("suitName", suitName);
        hashMap.put("suitDesc", suitDesc);
        hashMap.put("mtrGrp", mtrGrp);
        hashMap.put("transBy", userNo);
        hashMap.put("updateTime", nowTs);
        request.setAttribute("suitName", suitName);
        request.setAttribute("suitDesc", suitDesc);
        
        try {
            BasicHelper.addSuit(delegator, hashMap);
            request.setAttribute("_EVENT_MESSAGE_", "保存成功！");
        } catch (GenericEntityException e) {
        	String message = CommonUtil.checkOracleException(e);
        	request.setAttribute("_ERROR_MESSAGE_", message);
        }  catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    /**
     * 删除套件
     * @param request
     * @param response
     * @return
     */    
    public static String delSuitByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String id = request.getParameter("materialSuitIndex");
        
        Map paramMap = new HashMap();
		paramMap.put("materialSuitIndex", id);
        
        try {
        	List suitMaterialList = delegator.findByAnd("McsMaterialSuitItem", paramMap, UtilMisc.toList("materialSuitIndex"));
			request.setAttribute("suitMaterialList", suitMaterialList);
			
	    	if (suitMaterialList.isEmpty()) {
	    		BasicHelper.delSuitByIndex(delegator, id);
	            request.setAttribute("_EVENT_MESSAGE_", "删除成功！");
	        } else {
				request.setAttribute("_ERROR_MESSAGE_", "该套件尚有物料号存在，请先删除套件内的物料号！");
	            return "error";
	        }
	    	
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }
    
	/**
     * 显示套件物料号与数量
     * @param request
     * @param response
     * @return
     */    
    public static String defineSuitMaterial(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request); 
        String materialSuitIndex = request.getParameter("materialSuitIndex");
        String mtrGrp = request.getParameter("mtrGrp");
        request.setAttribute("materialSuitIndex", materialSuitIndex);
        request.setAttribute("mtrGrp", mtrGrp);
        
        // 得到用户部门
    	GenericValue user = AccountHelper.getUserInfo(request, delegator);
        String userDeptIndex = user.getString("deptIndex");
        
        // 显示物料组
        Map map = new HashMap();
        map.put("mtrGrp", mtrGrp);
        
        // 显示套件料号 List
        Map paramMap = new HashMap();
		paramMap.put("materialSuitIndex", materialSuitIndex);
   	        
        try{
        	List mtrGrpList = delegator.findByAnd("McsMaterialGroup", map, UtilMisc.toList("mtrGrp"));
			request.setAttribute("mtrGrpList", mtrGrpList);
        	
			List suitList = delegator.findByAnd("McsMaterialSuit", paramMap, UtilMisc.toList("materialSuitIndex"));
			request.setAttribute("suitList", suitList);
        	
        	List suitMaterialList = BasicHelper.querySuitMaterialList(delegator, paramMap);
        	request.setAttribute("suitMaterialList", suitMaterialList);
        	
        	//显示套件的物料号
            mtrGrp = mtrGrp.replace("'","");
        	String whereString = "MTR_GRP = '" + mtrGrp + "' and ENABLED = '1' and IN_CONTROL = '1' ";
        	whereString = whereString + " and DEPT_INDEX = '" + userDeptIndex + "'";
        	//whereString = whereString + " and MATERIAL_INDEX not in (select distinct t.MATERIAL_INDEX from MCS_MATERIAL_SUIT_ITEM t where t.MATERIAL_SUIT_INDEX = '" + materialSuitIndex + "') ";
        	EntityWhereString con = new EntityWhereString(whereString);
			List materialNumList = delegator.findByCondition("McsMaterialInfo", con, null, null);
			request.setAttribute("materialNumList", materialNumList);
        } catch(Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    } 
    
    /**
     * 增加套件料号
     * @param request
     * @param response
     * @return
     */
    public static String manageSuitMaterial(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        String materialSuitItemIndex = request.getParameter("materialSuitItemIndex");
        String materialSuitIndex = request.getParameter("manageMaterialSuitIndex");
        String mtrNum = request.getParameter("mtrNum");
        String mtrQty = request.getParameter("mtrQty");
        String userNo = CommonUtil.getUserNo(request);
        Timestamp nowTs = UtilDateTime.nowTimestamp();
        
        Map hashMap = new HashMap();
        hashMap.put("materialSuitItemIndex", materialSuitItemIndex);
        hashMap.put("materialSuitIndex", materialSuitIndex);
        hashMap.put("materialIndex", mtrNum);
        hashMap.put("mtrQty", mtrQty);
        hashMap.put("transBy", userNo);
        hashMap.put("updateTime", nowTs);
        request.setAttribute("materialIndex", mtrNum);
        request.setAttribute("mtrQty", mtrQty);
        
        try {
            BasicHelper.manageSuitMaterial(delegator, hashMap);
            request.setAttribute("_EVENT_MESSAGE_", "保存成功！");
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
     * 画面上的编辑按钮查询值
     * 
     * @param request
     *            materialSuitItemIndex
     * @param response
     */
    public static void getJsonSuitMaterialByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // materialSuitItemIndex取得
        String materialSuitItemIndex = request.getParameter("materialSuitItemIndex");
        
        try {
            // 取得套件物料号的信息
            GenericValue gv = delegator.findByPrimaryKey("McsMaterialSuitItem", UtilMisc.toMap("materialSuitItemIndex", materialSuitItemIndex));
            JSONObject mcsMaterialSuitItem = new JSONObject();
            mcsMaterialSuitItem.put("materialSuitItemIndex", UtilFormatOut.checkNull(gv.getString("materialSuitItemIndex")));
            mcsMaterialSuitItem.put("mtrNum", UtilFormatOut.checkNull(gv.getString("materialIndex")));
            mcsMaterialSuitItem.put("mtrQty", UtilFormatOut.checkNull(gv.getString("mtrQty")));

            // 写入response
            response.getWriter().write(mcsMaterialSuitItem.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }
    
    /**
     * 删除套件料号
     * @param request
     * @param response
     * @return
     */    
    public static String delSuitMaterialByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String id = request.getParameter("materialSuitItemIndex");
        
        try {
            BasicHelper.delSuitMaterialByIndex(delegator, id);
            request.setAttribute("_EVENT_MESSAGE_", "删除成功！");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }
    
	/**
     * 显示厂商与料号
     * @param request
     * @param response
     * @return
     */
    public static String defineVendorMaterialEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String mtrGrp = request.getParameter("mtrGrp");
        
        //预设物料组为光刻胶
		if (StringUtils.isEmpty(mtrGrp)) {
			 mtrGrp = ConstantsMcs.PHOTORESIST;
		}
		request.setAttribute("mtrGrp", mtrGrp);
		
		// 得到用户部门
    	GenericValue user = AccountHelper.getUserInfo(request, delegator);
        String userDeptIndex = user.getString("deptIndex");
    	        
        try{
	        // 显示查询后的厂商与料号
            Map paramMap = new HashMap();
			paramMap.put("mtrGrp", mtrGrp);
			paramMap.put("deptIndex", userDeptIndex);
			
			List vendorMaterialList = BasicHelper.queryVendorMaterialList(delegator, paramMap);
        	request.setAttribute("vendorMaterialList", vendorMaterialList);
        	
        	//显示物料号
			String whereString = "MTR_GRP = '" + mtrGrp + "' and ENABLED = '1' and IN_CONTROL = '1' ";
        	whereString = whereString + " and DEPT_INDEX = '" + userDeptIndex + "' ";
        	EntityWhereString con = new EntityWhereString(whereString);
			List materialNumList = delegator.findByCondition("McsMaterialInfo", con, null, UtilMisc.toList("mtrNum"));
			request.setAttribute("materialNumList", materialNumList);
			

        }catch(Exception e){
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /**
     * 增加料件厂商批号
     * @param request
     * @param response
     * @return
     */
    public static String manageVendorMaterial(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        String mtrGrp = request.getParameter("mtrGrp");
        request.setAttribute("mtrGrp", mtrGrp);
        
        GenericValue user = AccountHelper.getUserInfo(request, delegator);
        String userDeptIndex = user.getString("deptIndex");
        
        String mtrNum = request.getParameter("mtrNum");
        String vendorMtrNum = request.getParameter("vendorMtrNum");
        String userNo = CommonUtil.getUserNo(request);
        Timestamp nowTs = UtilDateTime.nowTimestamp();
        
        Map hashMap = new HashMap();
        hashMap.put("mtrNum", mtrNum);
        hashMap.put("vendorMtrNum", vendorMtrNum);
        hashMap.put("transBy", userNo);
        hashMap.put("updateTime", nowTs);
        request.setAttribute("mtrNum", mtrNum);
        request.setAttribute("vendorMtrNum", vendorMtrNum);
        
        try {
            BasicHelper.manageVendorMaterial(delegator, hashMap);
            request.setAttribute("_EVENT_MESSAGE_", "保存成功！");
            
            // 显示新增后的料件厂商批号
            Map paramMap = new HashMap();
			paramMap.put("mtrGrp", mtrGrp);
			paramMap.put("deptIndex", userDeptIndex);
			
			List vendorMaterialList = BasicHelper.queryVendorMaterialList(delegator, paramMap);
        	request.setAttribute("vendorMaterialList", vendorMaterialList);
        	
        	//显示物料号
			String whereString = "MTR_GRP = '" + mtrGrp + "' and ENABLED = '1' and IN_CONTROL = '1' ";
        	whereString = whereString + " and DEPT_INDEX = '" + userDeptIndex + "' ";
        	EntityWhereString con = new EntityWhereString(whereString);
			List materialNumList = delegator.findByCondition("McsMaterialInfo", con, null, UtilMisc.toList("mtrNum"));
			request.setAttribute("materialNumList", materialNumList);
            
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
     * 厂商批号编辑按钮查询值
     * @param request
     * @param response
     */
    public static void getJsonVendorMaterial(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String mtrNum = request.getParameter("mtrNum");
        
        try {
            GenericValue gv = delegator.findByPrimaryKey("McsVendorMaterial", UtilMisc.toMap("mtrNum", mtrNum));
            JSONObject mcsVendorMaterialItem = new JSONObject();
            mcsVendorMaterialItem.put("mtrNum", UtilFormatOut.checkNull(gv.getString("mtrNum")));
            mcsVendorMaterialItem.put("vendorMtrNum", UtilFormatOut.checkNull(gv.getString("vendorMtrNum")));

            // 写入response
            response.getWriter().write(mcsVendorMaterialItem.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }
    
    /**
     * 删除厂商批号
     * @param request
     * @param response
     * @return
     */    
    public static String delVendorMaterial(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String mtrNum = request.getParameter("mtrNum");
        
        try {
            BasicHelper.delVendorMaterial(delegator, mtrNum);
            request.setAttribute("_EVENT_MESSAGE_", "删除成功！");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }   
}
