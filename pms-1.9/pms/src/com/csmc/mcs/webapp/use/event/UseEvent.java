package com.csmc.mcs.webapp.use.event;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.csmc.mcs.webapp.common.helper.McsCommonHelper;
import com.csmc.mcs.webapp.use.helper.UseHelper;
import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.GeneralEvents;

/**
 * 类 UseEvent.java 
 * @version  1.0  2009-7-21
 * @author   dinghh
 */
public class UseEvent extends GeneralEvents {
	public static final String module = UseEvent.class.getName();
	
	/**
     * 查询本部门物料可使用设备
     * @param request(useBySuit)
     * @param response
     * @return Json
     */
    /*public static void getJsonMtrUsingObject(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String deptIndex = request.getParameter("deptIndex");               
        
        JSONArray arrJson = new JSONArray();
        try {
			JSONObject blank = new JSONObject(UtilMisc.toMap("usingObjectId", ""));
			arrJson.put(blank);
			
			String sql = "select distinct using_object_id from mcs_mtr_object where dept_index= " + deptIndex;
	        List list = SQLProcess.excuteSQLQuery(sql, delegator);
			for (Iterator it = list.iterator(); it.hasNext();) {
				Map map = (Map) it.next();
				JSONObject object = new JSONObject();
				object.put("usingObjectId", map.get("usingObjectId"));				
				arrJson.put(object);
			}
			response.getWriter().write(arrJson.toString());
        } catch(Exception e) {
            Debug.logError(e.getMessage(), module);
        }         
    }*/
	
	/**
     * 暂存退库起始页
     * 得到用户部门
     * 显示暂存区新领物料
     * 得到7天内已退库列表
     * @param request
     * @param response
     * @return
     */    
    public static String cabinetBackStockEntry(HttpServletRequest request, HttpServletResponse response) {
    	
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String mtrGrp = request.getParameter("mtrGrp");
        
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
        
        try {
        	// 得到用户部门
        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
	        String userDeptIndex = user.getString("deptIndex");
	        
	        if (StringUtils.isNotEmpty(mtrGrp)) {							
				// 显示暂存区新领物料			
				List cabinetList = UseHelper.queryCabinetNewMaterialList(delegator, userDeptIndex, mtrGrp, mtrNum);			
				request.setAttribute("cabinetList", cabinetList);				
	            
				// 得到7天内已退库列表
				List backStockList = UseHelper.queryBackStockList(delegator, userDeptIndex, mtrGrp, mtrNum);
				request.setAttribute("backStockList", backStockList);
	        }	
			
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
     * 按数量提交事件, 完成暂存退库操作
     * 物料退库、记录历史
     * @param request
     * @param response
     * @return
     */
    public static String cabinetBackStock(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
    	LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String userNo = CommonUtil.getUserNo(request);
        // 得到用户部门
    	GenericValue user = AccountHelper.getUserInfo(request, delegator);
        String userDeptIndex = user.getString("deptIndex");

        String newStatus = request.getParameter("newStatus");
        String oldStatus = ConstantsMcs.CABINET_NEW;
        String formActionType = "use";
        
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
        
        try {    	
			String[] qtyArray = request.getParameterValues(formActionType + "Qty");						
			if (qtyArray != null && qtyArray.length > 0) {
				String[] totalQtyArray = request.getParameterValues(formActionType + "TotalQty");
				String[] vendorBatchNumArray = request.getParameterValues(formActionType + "VendorBatchNum");
				String[] mtrNumArray = request.getParameterValues(formActionType + "MtrNum");
				String[] shelfLifeExpirationDateArray = request.getParameterValues(formActionType + "ShelfLifeExpirationDate");
				String[] mrbDateArray = request.getParameterValues(formActionType + "MrbDate");
								
				Map paramMap = new HashMap();
				paramMap.put("userNo", userNo);
				paramMap.put("deptIndex", userDeptIndex);
				paramMap.put("oldStatus", oldStatus);
				paramMap.put("newStatus", newStatus);
				
				paramMap.put("qtyArray", qtyArray);
				paramMap.put("totalQtyArray", totalQtyArray);
				paramMap.put("vendorBatchNumArray", vendorBatchNumArray);
				paramMap.put("mtrNumArray", mtrNumArray);
				paramMap.put("shelfLifeExpirationDateArray", shelfLifeExpirationDateArray);
				paramMap.put("mrbDateArray", mrbDateArray);
				
				// 调用Service By QTY
				UseHelper.modifyMtrStatusByService(request, dispatcher, "changeMaterialStatusByQty", paramMap);
			}
        
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /**
     * 暂存退库(BARCODE)起始页
     * 得到用户部门
     * 显示暂存区超有效期新领物料(光刻胶、SOG)
     * 得到7天内已退库列表
     * @param request
     * @param response
     * @return
     */    
    public static String cabinetBackStockBarcodeEntry(HttpServletRequest request, HttpServletResponse response) {
    	
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String mtrGrp = request.getParameter("mtrGrp");
        
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
        
        try {
        	// 得到用户部门
        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
	        String userDeptIndex = user.getString("deptIndex");
	        
	        if (StringUtils.isNotEmpty(mtrGrp)) {
	        	Map paramMap = new HashMap();
				paramMap.put("deptIndex", userDeptIndex);
				paramMap.put("status", ConstantsMcs.CABINET_NEW);				
				paramMap.put("mtrGrp", mtrGrp);
				paramMap.put("mtrNum", mtrNum);
				paramMap.put("whereString", "nvl(mrb_date, shelf_life_expiration_date) < sysdate");
	        	
				// 显示暂存区超有效期新领物料List
	        	List cabinetList = McsCommonHelper.queryMaterialMapList(delegator, paramMap);
				request.setAttribute("cabinetList", cabinetList);
	            
				// 得到7天内已退库列表
				List backStockList = UseHelper.queryBackStockList(delegator, userDeptIndex, mtrGrp, mtrNum);
				request.setAttribute("backStockList", backStockList);
	        }	
			
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
     * 按片号使用起始页 OR 按套件使用起始页
     * 得到用户部门
     * 显示设备在用物料，
     * 显示暂存区可用物料
     * @param request
     * @param response
     * @return
     */
    public static String useMaterialByIdEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);  
        String eqpId = request.getParameter("eqpId");
        String mtrGrp = request.getParameter("mtrGrp");
        
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
        
        // 按套件使用，附加参数materialSuitIndex,aliasName
        String materialSuitIndex = request.getParameter("materialSuitIndex");
        String aliasName = request.getParameter("aliasName");
        request.setAttribute("aliasName", aliasName);
        
        try {
        	// 得到用户部门
        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
	        String userDeptIndex = user.getString("deptIndex");
	        
	        if (StringUtils.isNotEmpty(eqpId)) {
	        	Map paramMap = new HashMap();
				paramMap.put("deptIndex", userDeptIndex);
				paramMap.put("eqpId", eqpId);
				paramMap.put("mtrGrp", mtrGrp);
				paramMap.put("mtrNum", mtrNum);
				
				paramMap.put("materialSuitIndex", materialSuitIndex);
				paramMap.put("aliasName", aliasName);
				
		        // 显示设备在用物料List
	        	List eqpMaterialList = UseHelper.queryEqpMaterialList(delegator, paramMap);
				request.setAttribute("eqpMaterialList", eqpMaterialList);
							
				// 显示暂存区可用物料	
				List cabinetList = UseHelper.queryCabinetMaterialList(delegator, paramMap);
				request.setAttribute("cabinetList", cabinetList);
	        }
			
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
     * 按片号 OR 按套件 使用提交事件, 完成换上 或 换下操作
     * 物料换上设备、记录历史
     * 物料换下设备、记录历史     
     * @param request
     * @param response
     * @return
     */
    public static String useMaterialById(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
    	LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String userNo = CommonUtil.getUserNo(request);
        
        String eqpId = request.getParameter("eqpIdSubmit");
        String newStatus = request.getParameter("newStatus");
        String oldStatus = null;
        String[] mtrStatusIdArray = null;
        String note = null;
        
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
        
        try {
        	if (ConstantsMcs.USING.equalsIgnoreCase(newStatus)) {
        		oldStatus = ConstantsMcs.CABINET;
        		mtrStatusIdArray = request.getParameterValues("objCheckboxCabinet");
        		note = request.getParameter("useNote");
        	} else {
        		oldStatus = ConstantsMcs.USING;
        		mtrStatusIdArray = request.getParameterValues("objCheckboxEqpMtr");
        		note = request.getParameter("offNote");
        	}

			if (mtrStatusIdArray == null) {
	    		request.setAttribute("_ERROR_MESSAGE_", ConstantsMcs.MSG_UN_SELECT);
	            return "error";
			} else {
				Map paramMap = new HashMap();
				paramMap.put("userNo", userNo);
				paramMap.put("oldStatus", oldStatus);
				paramMap.put("newStatus", newStatus);
				paramMap.put("eqpId", eqpId);
				paramMap.put("mtrStatusIdArray", mtrStatusIdArray);
				paramMap.put("note", note);
				
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
     * 按数量使用起始页
     * 得到用户部门
     * 显示设备在用物料，
     * 显示暂存区可用物料
     * @param request
     * @param response
     * @return
     */
    public static String useMaterialByQtyEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String eqpId = request.getParameter("eqpId");
        String mtrGrp = request.getParameter("mtrGrp");
        
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
        
        try {
        	// 得到用户部门
        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
	        String userDeptIndex = user.getString("deptIndex");
	        
	        if (StringUtils.isNotEmpty(eqpId)) {
		        // 显示设备在用物料List
	        	List eqpMaterialList = UseHelper.queryEqpMaterialListByQty(delegator, userDeptIndex, eqpId, mtrGrp, mtrNum);
				request.setAttribute("eqpMaterialList", eqpMaterialList);
							
				// 显示暂存区可用物料			
				List cabinetList = UseHelper.queryCabinetMaterialListByQty(delegator, userDeptIndex, eqpId, mtrGrp, mtrNum);			
				request.setAttribute("cabinetList", cabinetList);
	        }
			
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
     * 按数量使用提交事件, 完成换上 或 换下操作
     * 物料换上设备、记录历史
     * 物料换下设备、记录历史     
     * @param request
     * @param response
     * @return
     */
    public static String useMaterialByQty(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
    	LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String userNo = CommonUtil.getUserNo(request);
        // 得到用户部门
    	GenericValue user = AccountHelper.getUserInfo(request, delegator);
        String userDeptIndex = user.getString("deptIndex");
        
        String eqpId = request.getParameter("eqpIdSubmit");
        String newStatus = request.getParameter("newStatus");
        String oldStatus = null;
        String formActionType = null;
        String note = null;
        
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
        
        try {
        	if (ConstantsMcs.USING.equalsIgnoreCase(newStatus)) {
        		oldStatus = ConstantsMcs.CABINET;
        		formActionType = "use";
        		note = request.getParameter("useNote");
        	} else {
        		oldStatus = ConstantsMcs.USING;
        		formActionType = "off";
        		note = request.getParameter("offNote");
        	}
        	
			String[] qtyArray = request.getParameterValues(formActionType + "Qty");
						
			if (qtyArray != null && qtyArray.length > 0) {
				String[] totalQtyArray = request.getParameterValues(formActionType + "TotalQty");
				String[] vendorBatchNumArray = request.getParameterValues(formActionType + "VendorBatchNum");
				String[] materialIndexArray = request.getParameterValues(formActionType + "MaterialIndex");
				String[] mtrNumArray = request.getParameterValues(formActionType + "MtrNum");
				String[] mtrDescArray = request.getParameterValues(formActionType + "MtrDesc");
				String[] shelfLifeExpirationDateArray = request.getParameterValues(formActionType + "ShelfLifeExpirationDate");
				String[] mrbDateArray = request.getParameterValues(formActionType + "MrbDate");
								
				Map paramMap = new HashMap();
				paramMap.put("userNo", userNo);
				paramMap.put("deptIndex", userDeptIndex);
				paramMap.put("oldStatus", oldStatus);
				paramMap.put("newStatus", newStatus);
				paramMap.put("eqpId", eqpId);
				paramMap.put("note", note);
				
				paramMap.put("qtyArray", qtyArray);
				paramMap.put("totalQtyArray", totalQtyArray);
				paramMap.put("vendorBatchNumArray", vendorBatchNumArray);
				paramMap.put("materialIndexArray", materialIndexArray);
				paramMap.put("mtrNumArray", mtrNumArray);
				paramMap.put("mtrDescArray", mtrDescArray);
				paramMap.put("shelfLifeExpirationDateArray", shelfLifeExpirationDateArray);
				paramMap.put("mrbDateArray", mrbDateArray);
				
				// pms interface
				paramMap.put("eventType", request.getParameter("eventType"));
				paramMap.put("eventIndex", request.getParameter("eventIndex"));
				paramMap.put("eqpIdPms", request.getParameter("eqpIdPms"));
				
				// 调用Service By QTY
				UseHelper.modifyMtrStatusByService(request, dispatcher, "changeMaterialStatusByQty", paramMap);
			}
        
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";

    }

	/**
     * 按Barcode使用起始页
     * 得到用户部门
     * 显示设备在用物料，
     * 显示同品种一件可用物料
     * 显示暂存区可用物料
     * @param request
     * @param response
     * @return
     */
    public static String useMaterialByBarcodeEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);  
        String eqpId = request.getParameter("eqpId");
        String mtrGrp = request.getParameter("mtrGrp");

        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
      
        try {
        	// 得到用户部门
        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
	        String userDeptIndex = user.getString("deptIndex");
	        
	        if (StringUtils.isNotEmpty(eqpId)) {
	        	Map paramMap = new HashMap();
				paramMap.put("deptIndex", userDeptIndex);
				paramMap.put("eqpId", eqpId);
				paramMap.put("mtrGrp", mtrGrp);
				paramMap.put("mtrNum", mtrNum);
				
		        // 1.显示设备在用物料List
	        	List eqpMaterialList = UseHelper.queryEqpMaterialList(delegator, paramMap);
				request.setAttribute("eqpMaterialList", eqpMaterialList);
				
				// 2.显示同品种一件可用物料
				// 101028修改为 所有 已录入条码的可用物料
				List forUseList = UseHelper.queryForUseList(delegator, paramMap);
				request.setAttribute("forUseList", forUseList);
							
				// 3.显示暂存区可用物料	
				List cabinetList = UseHelper.queryCabinetMaterialListByBarcode(delegator, paramMap);
				request.setAttribute("cabinetList", cabinetList);
				
				// 4.换下时需校验24小时以内最近一次换上的光刻胶
				String lastPrUsed = UseHelper.getLastPrUsed(delegator, eqpId);
				request.setAttribute("lastPrUsed", lastPrUsed);
				
				if (ConstantsMcs.PHOTORESIST.equals(mtrGrp)) {
					// 5.离线扫描枪15分钟内扫描的光刻胶，页面自动选中设备在用与待使用光刻胶
					List scanBarcodeList = UseHelper.queryScanBarcodeList(delegator, eqpId);
					request.setAttribute("scanBarcodeList", scanBarcodeList);
					
					String strScanBarcode = "";//换上光刻胶条码字符串
					for (int i=0; i < scanBarcodeList.size(); i++) {
						HashMap map = (HashMap) scanBarcodeList.get(i);
						String barcode = (String) map.get("BARCODE");
						strScanBarcode = strScanBarcode + barcode + ",";						
					}
					request.setAttribute("strScanBarcode", strScanBarcode);
				}
	        }
			
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
     * 按Barcode更换提交事件, 完成换下并换上
     * 物料换下设备、记录历史
     * 物料换上设备、记录历史          
     * @param request
     * @param response
     * @return
     */
    public static String useMaterialByBarcode(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
    	LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String userNo = CommonUtil.getUserNo(request);
        
        String eqpId = request.getParameter("eqpIdSubmit");
        String mtrGrp = request.getParameter("mtrGrpSubmit");
        String newStatus = request.getParameter("newStatus");
        String oldStatus = null;
        
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
        
        try {        	
			String[] eqpMtrSelectArray = request.getParameterValues("objCheckboxEqpMtr");
			String[] cabinetMtrSelectArray = request.getParameterValues("objCheckboxCabinet");
			String[] mtrStatusIdArray = null;
			
			if (eqpMtrSelectArray == null && cabinetMtrSelectArray == null) {
	    		request.setAttribute("_ERROR_MESSAGE_", ConstantsMcs.MSG_UN_SELECT);
	            return "error";
			}
			
			Map paramMap = new HashMap();
			paramMap.put("userNo", userNo);
			paramMap.put("eqpId", eqpId);
			
			// 换下
			if (eqpMtrSelectArray != null) {
				mtrStatusIdArray = eqpMtrSelectArray;
				oldStatus = ConstantsMcs.USING;
				String offNote = request.getParameter("offNote");
				
				if (newStatus.equals(ConstantsMcs.OFF_AND_USE)) {
					newStatus = ConstantsMcs.FINISH; // 光刻胶、SOG默认为用完
					//newStatus = ConstantsMcs.CABINET_RECYCLE;// MCS BARCODE USE for test
				}
				
				paramMap.put("oldStatus", oldStatus);
				paramMap.put("newStatus", newStatus);
				paramMap.put("mtrStatusIdArray", mtrStatusIdArray);
				paramMap.put("note", offNote);//换下备注
				
				// 调用Service修改状态
				UseHelper.modifyMtrStatusByService(request, dispatcher, "changeMaterialStatusById", paramMap);
				
				// SOG 在线报废操作发送email
				if (newStatus.equals(ConstantsMcs.ONLINE_SCRAP_OPT) && ConstantsMcs.CHEMICAL.equals(mtrGrp)) {
					StringBuffer mailContent = new StringBuffer();
					mailContent.append("操作人: " + userNo + "\n");
                    mailContent.append("设备: " + eqpId + "\n");
                    mailContent.append("序号/别名: " + request.getParameter("offAliasName") + "\n");
                    mailContent.append("换下备注: " + offNote + "\n");                    
					CommonUtil.sendMail(Constants.IT_SYSTEM_MAIL, Constants.THINFILM_PROCESS_MAIL, null, "MCS - SOG在线报废提醒", mailContent.toString());
				}
			}// end 换下
			
			// 换上
			if (cabinetMtrSelectArray != null) {
				mtrStatusIdArray = cabinetMtrSelectArray;
				oldStatus = ConstantsMcs.CABINET;
				newStatus = ConstantsMcs.USING;
				
				paramMap.put("oldStatus", oldStatus);
				paramMap.put("newStatus", newStatus);
				paramMap.put("mtrStatusIdArray", mtrStatusIdArray);
				paramMap.put("note", request.getParameter("useNote"));
				
				// 调用Service修改状态
				UseHelper.modifyMtrStatusByService(request, dispatcher, "changeMaterialStatusById", paramMap);
			}// end 换上			
        
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }

	/**
     * 维修清洗 管理起始页
     * 得到用户部门
     * 显示物料：内部维修、自主清洗、送外维修、送外清洗等
     * @param request
     * @param response
     * @return
     */
    public static String fabRepairEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        String mtrGrp = request.getParameter("mtrGrp");
        String oldStatus = request.getParameter("oldStatus");
        
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
        
        try {
        	if (StringUtils.isNotEmpty(oldStatus)) {
	        	// 得到用户部门
	        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
		        String userDeptIndex = user.getString("deptIndex");
		        
		        Map paramMap = new HashMap();
				paramMap.put("deptIndex", userDeptIndex);
				paramMap.put("status", oldStatus);
				
				if (StringUtils.isNotEmpty(mtrGrp)) {
					paramMap.put("mtrGrp", mtrGrp);
				}
				
				if (StringUtils.isNotEmpty(mtrNum)) {
					paramMap.put("mtrNum", mtrNum);
				}
				
		        // 显示内部维修物料List
	        	List fabRepairMaterialList = McsCommonHelper.queryMaterialGvList(delegator, paramMap);
				request.setAttribute("fabRepairMaterialList", fabRepairMaterialList);
		        
		        // 更换操作完成后得到transactionId
		        String transactionId = (String) request.getAttribute("transactionId");
		        request.setAttribute("transactionId", transactionId);
        	}
        } catch(Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /**
     * 按片号提交事件, 完成 暂存退库(BARCODE) 或 内部维修 操作
     * 修改物料状态，记录历史   
     * @param request
     * @param response
     * @return
     */
    public static String backStockAndFabRepairById(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
    	LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String userNo = CommonUtil.getUserNo(request);

        String newStatus = request.getParameter("newStatus");
        String oldStatus = request.getParameter("oldStatus");
        
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
        
        try {        	
			String[] mtrStatusIdArray = request.getParameterValues("objCheckboxSI");
			
			if (mtrStatusIdArray == null) {
	    		request.setAttribute("_ERROR_MESSAGE_", ConstantsMcs.MSG_UN_SELECT);
	            return "error";
			} else {
				Map paramMap = new HashMap();
				paramMap.put("userNo", userNo);
				paramMap.put("oldStatus", oldStatus);
				paramMap.put("newStatus", newStatus);
				paramMap.put("mtrStatusIdArray", mtrStatusIdArray);
				
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
     * 补填石英历史使用 起始页。
     * @param request
     * @param response
     * @return
     */
    public static String supplementaryCompleteEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);        
        GenericValue user = AccountHelper.getUserInfo(request, delegator);
        String userDeptIndex = user.getString("deptIndex");
        String mtrGrp = request.getParameter("mtrGrp");
        //预设套件List为石英套件
		if (StringUtils.isEmpty(mtrGrp)) {
			 mtrGrp = ConstantsMcs.QUARTZ;
		}
		request.setAttribute("mtrGrp", mtrGrp);
		String mtrNum = request.getParameter("mtrNum");
        request.setAttribute("mtrNum", mtrNum);
        String eqptId = request.getParameter("equipmentId");
        request.setAttribute("equipmentId", eqptId);
		request.setAttribute("vendorMtrNum", request.getParameter("vendorMtrNum"));
        request.setAttribute("shelfLifeExpirationDate", request.getParameter("shelfLifeExpirationDate"));
        try {
            // 显示石英历史使用列表
            Map paramMap = new HashMap();
			paramMap.put("mtrNum", mtrNum);
			paramMap.put("mtrGrp", mtrGrp);
			paramMap.put("deptIndex", userDeptIndex);	
			List vendorMaterialList = UseHelper.queryAliasNameList(delegator, paramMap);
        	request.setAttribute("vendorMaterialList", vendorMaterialList);
        	GenericValue materialInfo = CommonUtil.findFirstRecordByAnd(
					delegator, "McsMaterialInfo", 
					UtilMisc.toMap("mtrNum", mtrNum, "deptIndex", userDeptIndex));
        	request.setAttribute("mtrNumDesc", materialInfo.getString("mtrDesc"));
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }
    
    /**
     * 增加石英历史使用
     * @param request
     * @param response
     * @return
     */
    public static String manageUsingHistory(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String shelfLifeExpirationDate = request.getParameter("shelfLifeExpirationDate");
        if (UseHelper.toShelfLifeExpirationDate(shelfLifeExpirationDate)==null) {
        	request.setAttribute("_ERROR_MESSAGE_", "输入的日期格式错误.");
            return "error";
        }
        
        String mtrGrp = request.getParameter("mtrGrp");
        request.setAttribute("mtrGrp", mtrGrp);
        String mtrNum = request.getParameter("mtrNum");
        request.setAttribute("mtrNum", mtrNum);
        
        GenericValue user = AccountHelper.getUserInfo(request, delegator);
        String userDeptIndex = user.getString("deptIndex");
        
        String eqptId = request.getParameter("equipmentId");
        String vendorMtrNum = request.getParameter("vendorMtrNum");
        String userNo = CommonUtil.getUserNo(request);
        Timestamp nowTs = UtilDateTime.nowTimestamp();
        
        Map hashMap = new HashMap();
        hashMap.put("mtrNum", mtrNum);
        hashMap.put("mtrGrp", mtrGrp);
        hashMap.put("usingObjectId", eqptId);
        hashMap.put("aliasName", vendorMtrNum);
        hashMap.put("transBy", userNo);
        hashMap.put("updateTime", nowTs);
        hashMap.put("deptIndex", userDeptIndex);
        hashMap.put("shelfLifeExpirationDate", UseHelper.toShelfLifeExpirationDate(shelfLifeExpirationDate));
        hashMap.put("accountDept", user.getString("accountDept"));
        request.setAttribute("equipmentId", eqptId);
        request.setAttribute("vendorMtrNum", vendorMtrNum);
        request.setAttribute("shelfLifeExpirationDate", shelfLifeExpirationDate);
        try {
        	Map result = dispatcher.runSync("manageUsingHistory", UtilMisc.toMap("usingMap", hashMap));
			// throw exception
			if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
				throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
			}
            request.setAttribute("_EVENT_MESSAGE_", "保存成功！");
            
            // 显示石英历史使用列表
            Map paramMap = new HashMap();
			paramMap.put("mtrNum", mtrNum);
			paramMap.put("mtrGrp", mtrGrp);
			paramMap.put("deptIndex", userDeptIndex);	
			List vendorMaterialList = UseHelper.queryAliasNameList(delegator, paramMap);
        	request.setAttribute("vendorMaterialList", vendorMaterialList);
        	GenericValue materialInfo = CommonUtil.findFirstRecordByAnd(
					delegator, "McsMaterialInfo", 
					UtilMisc.toMap("mtrNum", mtrNum, "deptIndex", userDeptIndex));
        	request.setAttribute("mtrNumDesc", materialInfo.getString("mtrDesc"));   
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }
    
    /**
     * 修改别名
     * @param request
     * @param response
     * @return
     */
    public static String updateAliasName(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        String msIndex = request.getParameter("msIndex");   
        String vendorMtrNum = request.getParameter("aliasName");
        String mtrGrp = request.getParameter("mtr_Grp");
        request.setAttribute("mtrGrp", mtrGrp);
        String mtrNum = request.getParameter("mtr_Num");
        request.setAttribute("mtrNum", mtrNum);   
        GenericValue user = AccountHelper.getUserInfo(request, delegator);
        String userDeptIndex = user.getString("deptIndex"); 
        
        Map hashMap = new HashMap();
        hashMap.put("materialStatusIndex", msIndex);
        hashMap.put("aliasName", vendorMtrNum);
 
        try {
        	GenericValue usingGv = delegator.makeValidValue("McsMaterialStatus", hashMap);
        	usingGv.store();
            request.setAttribute("_EVENT_MESSAGE_", "修改成功！");
            
            // 显示石英历史使用列表
            Map paramMap = new HashMap();
			paramMap.put("mtrNum", mtrNum);
			paramMap.put("mtrGrp", mtrGrp);
			paramMap.put("deptIndex", userDeptIndex);	
			List vendorMaterialList = UseHelper.queryAliasNameList(delegator, paramMap);
        	request.setAttribute("vendorMaterialList", vendorMaterialList);
        	GenericValue materialInfo = CommonUtil.findFirstRecordByAnd(
					delegator, "McsMaterialInfo", 
					UtilMisc.toMap("mtrNum", mtrNum, "deptIndex", userDeptIndex));
        	request.setAttribute("mtrNumDesc", materialInfo.getString("mtrDesc"));
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }
    
    /**
     * 验证SOG 512 OK
     * @param request
     * @param response
     * @return
     */
    public static String validSogOK(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).toUpperCase();
        request.setAttribute("mtrNum", mtrNum);
        
        try {        	
			String[] mtrStatusIdArray = request.getParameterValues("objCheckboxEqpMtr");			
			if (mtrStatusIdArray == null) {
	    		request.setAttribute("_ERROR_MESSAGE_", ConstantsMcs.MSG_UN_SELECT);
	            return "error";
			}
						
			// 验证OK
			for (int i = 0; i < mtrStatusIdArray.length; i++) {
				Long mtrStatusId = Long.valueOf(mtrStatusIdArray[i]);
				GenericValue gv = delegator.findByPrimaryKey("McsMaterialStatus", UtilMisc.toMap("materialStatusIndex", mtrStatusId));

				List validOkList = delegator.findByAnd("McsMaterialStatus", UtilMisc.toMap("vendorBatchNum", gv.getString("vendorBatchNum"), "createdTxStamp", gv.getTimestamp("createdTxStamp")));
				for (int j = 0; j < validOkList.size(); j++) {
					GenericValue validOkGv = (GenericValue) validOkList.get(j);
					validOkGv.set("remark", "验证OK");
					validOkGv.store();
				}
			}
        
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
}
