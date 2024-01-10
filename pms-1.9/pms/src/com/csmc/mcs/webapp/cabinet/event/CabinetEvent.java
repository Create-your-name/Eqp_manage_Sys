package com.csmc.mcs.webapp.cabinet.event;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.csmc.mcs.webapp.cabinet.helper.CabinetHelper;
import com.csmc.mcs.webapp.expiration.helper.ExpirationHelper;
import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.form.event.PmFormEvent;
import com.csmc.pms.webapp.form.help.PmHelper;
import com.csmc.pms.webapp.form.help.TsHelper;
import com.csmc.pms.webapp.parts.help.PartsHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.GeneralEvents;
import com.csmc.pms.webapp.util.SessionNames;
import com.csmc.pms.webapp.workflow.help.WorkflowHelper;

/**
 * 类 CabinetEvent.java 
 * @version  1.0  2009-6-30
 * @author   dinghh
 */
public class CabinetEvent extends GeneralEvents {
	public static final String module = CabinetEvent.class.getName();
    
	/** 
	 * 领料暂存(按数量)起始页
     * 1.显示是否有需维护的新物料，
     * 2.显示需暂存的物料
     * 3.10天内有效期过期 物料列表
     * @param request
     * @param response
     * @return
     */
    public static String intoCabinetQtyEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).trim().toUpperCase();
        String recipient = UtilFormatOut.checkNull(request.getParameter("recipient")).trim();
        String orderBy = UtilFormatOut.checkNull(request.getParameter("orderBy"));
        
        try {
        	if (StringUtils.isNotEmpty(mtrGrp)) {
        		request.setAttribute("mtrGrp", mtrGrp);
	        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
		        Long userDeptIndex = user.getLong("deptIndex");
				
		        // 1.用户本部门物料基本资料待维护列表
				Map paramMap = new HashMap();
				paramMap.put("deptIndex", userDeptIndex);
				paramMap.put("enabled", ConstantsMcs.INTEGER_0);
				paramMap.put("inControl", ConstantsMcs.INTEGER_1);
				paramMap.put("mtrGrp", mtrGrp);
				List mtrMaintainList = CabinetHelper.queryMaterialList(delegator, paramMap);
				request.setAttribute("mtrMaintainList", mtrMaintainList);
				
				// 2.需暂存领料记录List
				paramMap.clear();
				paramMap.put("deptIndex", userDeptIndex);
				paramMap.put("activeFlag", ConstantsMcs.N);
				paramMap.put("mtrGrp", mtrGrp);
				paramMap.put("mtrNum", mtrNum);
				paramMap.put("recipient", recipient);
				paramMap.put("orderBy", orderBy);
				List stoReqList = CabinetHelper.queryStoReqList(delegator, paramMap);
				request.setAttribute("stoReqList", stoReqList);
				
				// 3.10天内有效期过期 物料列表
				Calendar c = Calendar.getInstance(); 
    	        c.setTime(new Date());
    	        c.add(Calendar.DATE, 10);
    	        SimpleDateFormat df2 =new SimpleDateFormat("yyyy-MM-dd");
    	        String startDate = df2.format(c.getTime());
    	    
	        	List alertMaterialList = ExpirationHelper.queryOverExpirationList(delegator, userDeptIndex.toString(), mtrGrp,null,startDate);
				request.setAttribute("alertMaterialList", alertMaterialList);
        	}
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
     * 领料暂存(按数量)提交事件
     * @param request
     * @param response
     * @return
     */
    public static String intoCabinetQty(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String userNo = CommonUtil.getUserNo(request);
                
        try {
			String[] stoReqSelectArray = request.getParameterValues("objCheckbox");
			String stoReqSelectStr = StringUtils.join(stoReqSelectArray, ",");
			String[] useQtyArray = request.getParameterValues("useQty");			    

	    	if (StringUtils.isEmpty(stoReqSelectStr)) {
	    		request.setAttribute("_ERROR_MESSAGE_", "未选择要进入暂存区的物料，请在暂存物料列表中勾选！");
	            return "error";
			} else {
				HashMap useQtyMap = new HashMap();
				for (int i = 0; i < stoReqSelectArray.length; i++  ) {
					useQtyMap.put(stoReqSelectArray[i], Long.valueOf(useQtyArray[i]));
				}
				
				Map result = dispatcher.runSync("intoCabinetByQty", UtilMisc
						 .toMap("userNo", userNo, 
								"stoReqSelectStr", stoReqSelectStr,
								"useQtyMap", useQtyMap));
				// throw exception
				if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
					throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
				}
						
				String returnMsg = (String) result.get("returnMsg");
				request.setAttribute("_EVENT_MESSAGE_", returnMsg + "已进入暂存区。");
			}
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
     * 领料暂存：化学品用完、备耗件直接使用
     * @param request
     * @param response
     * @return
     */
    public static String useMaterialCabinetQty(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String userNo = CommonUtil.getUserNo(request);
                
        try {
			String[] stoReqSelectArray = request.getParameterValues("objCheckbox");
			String stoReqSelectStr = StringUtils.join(stoReqSelectArray, ",");
			String[] useQtyArray = request.getParameterValues("useQty");
			String eqpId = request.getParameter("eqpId");
			String useNote = request.getParameter("useNote");
			String newStatus = request.getParameter("newStatus");			

	    	if (StringUtils.isEmpty(stoReqSelectStr)) {
	    		request.setAttribute("_ERROR_MESSAGE_", "未选择要进入暂存区的物料，请在暂存物料列表中勾选！");
	            return "error";
			} else {
				HashMap useQtyMap = new HashMap();
				for (int i = 0; i < stoReqSelectArray.length; i++  ) {
					useQtyMap.put(stoReqSelectArray[i], Long.valueOf(useQtyArray[i]));
				}
				
				HashMap paramMap = new HashMap();
				paramMap.put("userNo", userNo);
				paramMap.put("stoReqSelectStr", stoReqSelectStr);
				paramMap.put("useQtyMap", useQtyMap);
				paramMap.put("eqpId", eqpId);
				paramMap.put("useNote", useNote);
				paramMap.put("newStatus", newStatus);
				
				Map result = dispatcher.runSync("useMaterialByCabinet", UtilMisc.toMap("paramMap", paramMap));
				// throw exception
				if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
					throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
				}
						
				String returnMsg = (String) result.get("returnMsg");
				request.setAttribute("_EVENT_MESSAGE_", returnMsg + "已使用。");
			}
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
	 * 领料暂存(按Barcode)起始页
     * 1.显示是否有需维护的新物料，
     * 2.显示需暂存的物料
     * @param request
     * @param response
     * @return
     */
    public static String intoCabinetBarcodeEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String mtrGrp = request.getParameter("mtrGrp");
        
        try {
        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
	        Long userDeptIndex = user.getLong("deptIndex");
			
	        // 1.用户本部门物料基本资料待维护列表
			Map paramMap = new HashMap();
			paramMap.put("deptIndex", userDeptIndex);
			paramMap.put("enabled", ConstantsMcs.INTEGER_0);
			paramMap.put("inControl", ConstantsMcs.INTEGER_1);
			if (StringUtils.isNotEmpty(mtrGrp)) {
				paramMap.put("mtrGrp", mtrGrp);
			}
			List mtrMaintainList = CabinetHelper.queryMaterialList(delegator, paramMap);
			request.setAttribute("mtrMaintainList", mtrMaintainList);
			
			// 2.需暂存领料记录List
			paramMap.clear();
			paramMap.put("deptIndex", userDeptIndex);
			paramMap.put("activeFlag", ConstantsMcs.N);
			paramMap.put("mtrGrp", mtrGrp);
			
			List stoReqList = null;
			if (ConstantsMcs.PHOTORESIST.equals(mtrGrp)) {
				stoReqList = CabinetHelper.queryStoReqListBarCode(delegator, paramMap);
			} else if (StringUtils.isNotEmpty(mtrGrp)){
				stoReqList = CabinetHelper.queryStoReqList(delegator, paramMap);
			}
			request.setAttribute("stoReqList", stoReqList);
			
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
     * 领料暂存(按Barcode)提交事件
     * @param request
     * @param response
     * @return
     */
    public static String intoCabinetBarcode(HttpServletRequest request, HttpServletResponse response) {
        //GenericDelegator delegator = CommonUtil.getMcsDelegator(request);         
                
        try {
			
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
     * 修改重名条码 起始页。
     * @param request
     * @param response
     * @return
     */
    public static String inputBarcodeErrorEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);        
        
        try {			
            List statusList = delegator.findByAndCache("McsMaterialStatusError", UtilMisc.toMap("isChanged", "N"), UtilMisc.toList("materialStatusIndex"));
            request.setAttribute("statusList", statusList);
            
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
     * 修改条码 提交
     * @param request
     * @param response
     * @return
     */
    public static String inputBarcodeError(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);        
                
        String[] objCheckboxArray = request.getParameterValues("objCheckbox");
        List toStore = new LinkedList();
        try {
            if (objCheckboxArray != null && objCheckboxArray.length > 0) {

                for (int i = 0; i < objCheckboxArray.length; i++) {
                    String[] objCheckValue = objCheckboxArray[i].split(ConstantsMcs.SEPARATOR);
                    Long materialStatusIndex = Long.valueOf(objCheckValue[0]);
                    String aliasName = objCheckValue[1];
                    
                    Map paramMap = new HashMap();
                    paramMap.put("materialStatusIndex", materialStatusIndex);
                    paramMap.put("aliasName", aliasName+"X");
                    
                    GenericValue mtrStatusErrorGv = delegator.makeValidValue(
                            "McsMaterialStatus", paramMap);
                    toStore.add(mtrStatusErrorGv);
                    
                    Map statusParamMap = new HashMap();
                    statusParamMap.put("materialStatusIndex", materialStatusIndex);
                    statusParamMap.put("isChanged", "Y");
                    
                    GenericValue mtrStautsGv = delegator.makeValidValue(
                            "McsMaterialStatusError", statusParamMap);
                    toStore.add(mtrStautsGv);
                }

            }
            delegator.storeAll(toStore);
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
     * 录入条码 起始页。
     * 光刻胶有厂商料号和厂商批号的前缀限制； SOG有供应商批号比对限制； 其他类型物料扫描条码无限制。
     * 回收类靶材从SAP入库数据表选择:已取消
     * 注意：库房发货数据修改(modifyVendorInfoEntry) 也调用此方法。
     * @param request
     * @param response
     * @return
     */
    public static String inputBarcodeEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);        
        GenericValue user = AccountHelper.getUserInfo(request, delegator);
        Long userDeptIndex = user.getLong("deptIndex");
        
        String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum"));
        
        try {			
        	if ("".equals(mtrGrp)) {
        	    //默认选择光刻胶
        	    mtrGrp = ConstantsMcs.PHOTORESIST;
        	}
        	request.setAttribute("mtrGrp", mtrGrp);
        	
        	Map paramMap = new HashMap();
			paramMap.put("deptIndex", userDeptIndex);
			paramMap.put("mtrGrp", mtrGrp);
			paramMap.put("mtrNum", mtrNum);
			
			//1.未录入条码
			List statusList = CabinetHelper.queryStatusListBarCode(delegator, paramMap, "0");
			request.setAttribute("statusList", statusList);
			
			//2.已录入条码
			List statusList1 = CabinetHelper.queryStatusListBarCode(delegator, paramMap, "1");
			request.setAttribute("statusList1", statusList1);
			
			// fab1 SAP靶材接口:已取消, isCallbackDrone返回false
			if (Constants.CALL_TP_FLAG && ConstantsMcs.TARGET.equals(mtrGrp)) {
				boolean isCallbackDrone = CabinetHelper.isCallbackDrone(delegator, mtrNum);
				if (isCallbackDrone) {
					List droneList = CabinetHelper.queryDroneList(delegator, mtrNum);
					request.setAttribute("droneList", droneList);
				}			
			}			
			
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
     * 录入条码 提交
     * @param request
     * @param response
     * @return
     */
    public static String inputBarcode(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);        
        String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrpSubmit"));
    	String[] mtrStatusIdArray = request.getParameterValues("materialStatusIndex");
    	String[] aliasNameArray = request.getParameterValues("aliasName");
    	String[] barcodePrefixArray = request.getParameterValues("barcodePrefix");
    	String[] barcodeArray = request.getParameterValues("barcode");
    	
    	try {        	
        	if (mtrStatusIdArray != null && mtrStatusIdArray.length > 0) {
	        	Map paramMap = new HashMap();
	        	paramMap.put("mtrGrp", mtrGrp);
	        	paramMap.put("mtrStatusIdArray", mtrStatusIdArray);
	        	paramMap.put("aliasNameArray", aliasNameArray);
	        	paramMap.put("barcodePrefixArray", barcodePrefixArray);
	        	paramMap.put("barcodeArray", barcodeArray);
	        	
	        	// 薄膜部SOG作业员,作业长,薄膜作业长,班长 不能修改条码，只能输入一次
	        	//ref. 得到用户组SecuritySetupEvent.java
	    		//Map result = SecuritySetupHelper.getAccountInfo((String)attributes.get("accountid"),delegator,userLogin);
	    		//groupList = (List) result.get("grouplist");	
	        	Set accountgroupset = (Set) request.getSession().getAttribute(SessionNames.ACCOUNT_GROUP_SET_KEY);
	        	if (accountgroupset.contains("OP_THIN") || accountgroupset.contains("DISPATCH") || accountgroupset.contains("DISPATCH_PL") || accountgroupset.contains("MONITOR")) {
	        		paramMap.put("isOperator", ConstantsMcs.Y);
	        	}
	        	
	        	String errorMsg = CabinetHelper.saveBarcode(delegator, paramMap);
	        	if (StringUtils.isNotEmpty(errorMsg)) {
	        		request.setAttribute("_ERROR_MESSAGE_", errorMsg);
	        	} else {
	        		request.setAttribute("_EVENT_MESSAGE_", "条码已保存，可开始按BARCODE使用。");
	        	}
        	}			
        	
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            String errorMsg = e.getMessage();
            if (StringUtils.contains(errorMsg, "ORA-00001")) {
                String aliasName = StringUtils.substringBefore(StringUtils.substringAfter(errorMsg, "aliasName,"), "(java.lang.String)");
                errorMsg = "瓶号 "+ aliasName + "已使用过，请截屏联系MES处理";
                // 记录到Material Status Error表
                try {
                    Map errorMap = new HashMap();
                    errorMap.put("aliasName", aliasName);
                    List list = delegator.findByAnd("McsMaterialStatus", errorMap);
                    String materialStatusIndex = ((Map)list.get(0)).get("materialStatusIndex").toString();
                    
                    errorMap.put("isChanged", "N");
                    errorMap.put("transBy", CommonUtil.getUserNo(request));
                    errorMap.put("updateTime", UtilDateTime.nowTimestamp());
                    errorMap.put("materialStatusIndex", materialStatusIndex);
                    
                    GenericValue gv = delegator.makeValidValue("McsMaterialStatusError", errorMap);
                    delegator.createOrStore(gv);
                } catch (GenericEntityException e1) {
                    e1.printStackTrace();
                }
            }
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            return "error";
        }
        
        return "success";
    }
    
    /** 
     * 放回冰箱/恒温 起始页
     * @param request
     * @param response
     * @return
     */
    public static String frozenOrUnfrozenEntry(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);        
        GenericValue user = AccountHelper.getUserInfo(request, delegator);
        Long userDeptIndex = user.getLong("deptIndex");
        
        String opType = UtilFormatOut.checkNull(request.getParameter("opType"));
        String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum"));
        
        if (StringUtils.isEmpty(mtrNum)) {
        	return "success";
        }
        
        try {			
			Map paramMap = new HashMap();
			paramMap.put("deptIndex", userDeptIndex);
			paramMap.put("opType", opType);
			paramMap.put("mtrGrp", mtrGrp);
			paramMap.put("mtrNum", mtrNum);
			
			List frozenOrUnfrozenList = CabinetHelper.queryFrozenOrUnfrozenList(delegator, paramMap);
			request.setAttribute("frozenOrUnfrozenList", frozenOrUnfrozenList);
			
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
     * 放回冰箱/恒温 提交
     * @param request
     * @param response
     * @return
     */
    public static String frozenOrUnfrozen(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        String userNo = CommonUtil.getUserNo(request);
        
        String opType = UtilFormatOut.checkNull(request.getParameter("opType"));
        String[] mtrStatusIdArray = request.getParameterValues("objCheckboxCabinet");
        
        try {
        	if (mtrStatusIdArray != null && mtrStatusIdArray.length > 0) {        		
    			Timestamp nowTs = UtilDateTime.nowTimestamp();
    			List toStore = new LinkedList();
    			String successMsg = "";
    			
    			for (int i = 0; i < mtrStatusIdArray.length; i++) {
    				Long mtrStatusId = Long.valueOf(mtrStatusIdArray[i]);
    				Map mtrStatusMap = new HashMap();
    				mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_INDEX, mtrStatusId);
    				mtrStatusMap.put("transBy", userNo);
    				mtrStatusMap.put("updateTime", nowTs);
    				
    				mtrStatusMap.put("status", ConstantsMcs.CABINET_NEW);    
    				mtrStatusMap.put("unfrozenTransBy", userNo);    				
    				if ("Frozen".equals(opType)) {
    					mtrStatusMap.put("note", "放回冰箱");
    					mtrStatusMap.put("unfrozenTransTime", null);
    				} else if ("Unfrozen".equals(opType)) {
    					mtrStatusMap.put("note", "恒温");
    					mtrStatusMap.put("unfrozenTransTime", nowTs);
    				}
    				
    			    GenericValue mtrStatusGv = delegator.makeValidValue("McsMaterialStatus", mtrStatusMap);
    			    toStore.add(mtrStatusGv);
    			    
    			    // 记录历史
				    Long mtrStatusHistId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX);
				    mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX, mtrStatusHistId);
				    
				    GenericValue mtrStatusHistGv = delegator.makeValidValue("McsMaterialStatusHist", mtrStatusMap);
				    toStore.add(mtrStatusHistGv);
    			    
    			    successMsg = successMsg + mtrStatusId.toString() +  " " + opType + ",\n";
    			}
    			
    			delegator.storeAll(toStore);
    			request.setAttribute("_EVENT_MESSAGE_", successMsg);
        	}
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
     * 库房发货数据修改 起始页
     * @param request
     * @param response
     * @return
     */
    public static String modifyVendorInfoEntry(HttpServletRequest request, HttpServletResponse response) {  
        return inputBarcodeEntry(request, response);
    }
    
    /** 
     * 库房发货数据修改 提交
     * @param request
     * @param response
     * @return
     */
    public static String modifyVendorInfo(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);        
        String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrpSubmit"));
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNumSubmit"));
    	String[] mtrStatusIdArray = request.getParameterValues("materialStatusIndex");
    	String[] vendorBatchNumArray = request.getParameterValues("vendorBatchNum");
    	String[] shelfLifeExpirationDateArray = request.getParameterValues("shelfLifeExpirationDate");
                
        try {        	
        	if (mtrStatusIdArray != null && mtrStatusIdArray.length > 0) {
	        	Map paramMap = new HashMap();
	        	paramMap.put("mtrGrp", mtrGrp);
	        	paramMap.put("mtrNum", mtrNum);
	        	paramMap.put("mtrStatusIdArray", mtrStatusIdArray);
	        	paramMap.put("vendorBatchNumArray", vendorBatchNumArray);
	        	paramMap.put("shelfLifeExpirationDateArray", shelfLifeExpirationDateArray);
	        	
	        	CabinetHelper.saveVendorInfo(delegator, paramMap);
	        	request.setAttribute("_EVENT_MESSAGE_", "修改成功");	        
        	}			
        	
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /**
     * 库房撤销页面,查询SAP撤销记录
     * @param request
     * @param response
     * @return
     * Create on 2011-6-22
     * Update on 2011-6-22
     */
    public static String cancelVendorInfoEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
        String objRadio = UtilFormatOut.checkNull(request.getParameter("objRadio"));
        try {
            //SAP撤销列表
            if (StringUtils.isNotEmpty(mtrGrp)) {
                Map paramMap = new HashMap();
                paramMap.put("activeFlag", ConstantsMcs.N);
                paramMap.put("mtrGrp", mtrGrp);
                // 查询QTY<0的记录
                paramMap.put("isQtyBelowZero", new Boolean(true));
                List sapStoReqList = CabinetHelper.queryCancelVendorStoReqList(delegator,
                        paramMap);
                request.setAttribute("sapStoReqList", sapStoReqList);
            }
            //已领用列表
            if (StringUtils.isNotEmpty(objRadio)) {
                String[] objRadioArray = objRadio.split(ConstantsMcs.SEPARATOR);
                //撤销领用提交后不显示已领物料列表
                String showStoReqList = UtilFormatOut.checkNull(request.getParameter("showStoReqList"));
                if (!showStoReqList.equals("0")) {
                    String vendorBatchNum = objRadioArray[1];
                    String mtrNum = objRadioArray[2];
                    Map paramMap = new HashMap();
                    paramMap.put("activeFlag", ConstantsMcs.N);
                    paramMap.put("mtrGrp", mtrGrp);
                    paramMap.put("vendorBatchNum", vendorBatchNum);
                    paramMap.put("mtrNum", mtrNum);
                    paramMap.put("isQtyBelowZero", new Boolean(false));
                    List stoReqList = CabinetHelper.queryCancelVendorStoReqList(delegator,
                            paramMap);
                    request.setAttribute("stoReqList", stoReqList);
                }
            }
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /**
     * 库房发货撤销提交
     * @param request
     * @param response
     * @return
     * Create on 2011-6-23
     * Update on 2011-6-23
     */
    public static String cancelVendorQty(HttpServletRequest request, HttpServletResponse response){
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        String objRadio = UtilFormatOut.checkNull(request.getParameter("objRadio"));
        String[] objCheckboxArray = request.getParameterValues("objCheckbox");
        String[] cancelVendorArray = request.getParameterValues("cancelVendor");    
        String totalCancel = request.getParameter("totalCancel");    
        List toStore = new LinkedList();
        Date activeTime = UtilDateTime.nowTimestamp();
        StringBuffer eventMsg = new StringBuffer("成功撤销:\n");
        
        try {
            //修改SAP撤销记录
            if (StringUtils.isNotEmpty(objRadio)) {
                String[] objRadioValue = objRadio.split(ConstantsMcs.SEPARATOR);
                Long mtrStoIndex = Long.valueOf(objRadioValue[0]);
                Map paramMap = new HashMap();
                paramMap.put("materialStoReqIndex", mtrStoIndex);
                paramMap.put("activeFlag", ConstantsMcs.Y);
                paramMap.put("activeTime", activeTime);
                paramMap.put("activeQty", new Integer(totalCancel));
                StringBuffer reasonForMovement = new StringBuffer();
                for (int i = 0; i < objCheckboxArray.length; i++) {
                    String[] objCheckValue = objCheckboxArray[i]
                            .split(ConstantsMcs.SEPARATOR);
                    reasonForMovement.append(Long.valueOf(objCheckValue[0])).append(",");
                }
                reasonForMovement.deleteCharAt(reasonForMovement.length()-1);
                paramMap.put("reasonForMovement", reasonForMovement.toString());
                GenericValue mtrStoReqGv = delegator.makeValidValue("McsMaterialStoReq", paramMap);
                toStore.add(mtrStoReqGv);
            }
            
            //修改已领用列表记录
            if (objCheckboxArray != null && objCheckboxArray.length > 0) {

                for (int i = 0; i < objCheckboxArray.length; i++) {
                    String[] objCheckValue = objCheckboxArray[i]
                            .split(ConstantsMcs.SEPARATOR);
                    Long mtrStoIndex = Long.valueOf(objCheckValue[0]);
                    long qty = Long.valueOf(objCheckValue[1]).longValue();
                    long activeQty = Long.valueOf(objCheckValue[2]).longValue();
                    String vendorBatchNum = (String) objCheckValue[3];
                    String mtrNum = (String) objCheckValue[4];
                    String mtrDesc = (String) objCheckValue[5];
                    long cancelVendorNum = Long.valueOf(cancelVendorArray[i])
                            .longValue();
                    
                    Map paramMap = new HashMap();
                    paramMap.put("materialStoReqIndex", mtrStoIndex);
                    paramMap.put("qty", new Long(qty - cancelVendorNum));
                    if ((qty - cancelVendorNum) == (activeQty)) {
                        paramMap.put("activeTime", activeTime);
                        paramMap.put("activeFlag", ConstantsMcs.Y);
                    }
                    
                    GenericValue mtrStoReqGv = delegator.makeValidValue(
                            "McsMaterialStoReq", paramMap);
                    toStore.add(mtrStoReqGv);
                    eventMsg.append("厂家批号 : ").append(vendorBatchNum)
                            .append(" ,物料号 : ").append(mtrNum)
                            .append(" ,物料描述 : ").append(mtrDesc).append(" ")
                            .append(" ,撤销数量 : ").append(cancelVendorNum).append(";\n");
                }

            }
            
            delegator.storeAll(toStore);
            request.setAttribute("_EVENT_MESSAGE_", eventMsg.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
    
    /** 
	 * 修改暂存物料 起始页
     * @param request
     * @param response
     * @return
     */
    public static String modifyCabinetMaterialEntry(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);        
        GenericValue user = AccountHelper.getUserInfo(request, delegator);
        Long userDeptIndex = user.getLong("deptIndex");
        
        String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum"));
        
        try {			
			Map paramMap = new HashMap();
			paramMap.put("deptIndex", userDeptIndex);
			paramMap.put("mtrGrp", mtrGrp);
			paramMap.put("mtrNum", mtrNum);
			
			List cabinetMaterialList = CabinetHelper.queryCabinetMaterialList(delegator, paramMap);
			request.setAttribute("cabinetMaterialList", cabinetMaterialList);        	
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
     * 修改暂存物料 提交
     * @param request
     * @param response
     * @return
     */
    public static String modifyCabinetMaterial(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        String userNo = CommonUtil.getUserNo(request);
        
        String[] mtrStatusIdArray = request.getParameterValues("objCheckbox");
        String[] unfrozenTransTimeArray = request.getParameterValues("unfrozenTransTime");
        String[] noteArray = request.getParameterValues("note");
        
        try {
        	if (mtrStatusIdArray != null && mtrStatusIdArray.length > 0) {        		
    			Timestamp nowTs = UtilDateTime.nowTimestamp();
    			List toStore = new LinkedList();
    			String successMsg = "";
    			
    			for (int i = 0; i < mtrStatusIdArray.length; i++) {
    				Long mtrStatusId = Long.valueOf(mtrStatusIdArray[i]);
    				String unfrozenTransTime = unfrozenTransTimeArray[i];
    				String note = "修改暂存物料: [" + unfrozenTransTime + "] [" + noteArray[i] + "]";

    				Map mtrStatusMap = new HashMap();
    				mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_INDEX, mtrStatusId);
    				mtrStatusMap.put("transBy", userNo);
    				mtrStatusMap.put("updateTime", nowTs);
    				
    				mtrStatusMap.put("status", ConstantsMcs.CABINET_NEW);    
    				mtrStatusMap.put("unfrozenTransBy", userNo);    				
    				mtrStatusMap.put("note", note);
    				mtrStatusMap.put("unfrozenTransTime", Timestamp.valueOf(unfrozenTransTime));    				
    				
    			    GenericValue mtrStatusGv = delegator.makeValidValue("McsMaterialStatus", mtrStatusMap);
    			    toStore.add(mtrStatusGv);
    			    
    			    // 记录历史
				    Long mtrStatusHistId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX);
				    mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX, mtrStatusHistId);
				    
				    GenericValue mtrStatusHistGv = delegator.makeValidValue("McsMaterialStatusHist", mtrStatusMap);
				    toStore.add(mtrStatusHistGv);
    			    
    			    successMsg = successMsg + mtrStatusId.toString() +  " 已修改。\n";
    			}
    			
    			delegator.storeAll(toStore);
    			request.setAttribute("_EVENT_MESSAGE_", successMsg);
        	}
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /**
     * 光刻胶领用查询
     * @param request
     * @param response
     * @return
     */
    public static String queryPhotoResistPickupEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);	
    	        
        try {			
			List photoResistPickupList = CabinetHelper.queryPhotoResistPickupList(delegator);
        	request.setAttribute("photoResistPickupList", photoResistPickupList);
        	
        	List prRefList = delegator.findAll("McsPrSafeQty", UtilMisc.toList("mtrNum"));
        	request.setAttribute("prRefList", prRefList);
        } catch(Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /**
     * 光刻胶安全库存编辑按钮查询值
     * @param request
     * @param response
     */
    public static void getJsonPrSafeQty(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String mtrNum = request.getParameter("mtrNum");
        
        try {
            GenericValue gv = delegator.findByPrimaryKey("McsPrSafeQty", UtilMisc.toMap("mtrNum", mtrNum));
            JSONObject jsObj = new JSONObject();
            jsObj.put("mtrNum", UtilFormatOut.checkNull(gv.getString("mtrNum")));
            jsObj.put("safeQty", UtilFormatOut.checkNull(gv.getString("safeQty")));

            // 写入response
            response.getWriter().write(jsObj.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }
    
    /**
     * 光刻胶领用:安全库存修改
     * @param request
     * @param response
     * @return
     */
    public static String managePrSafeQty(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);		
        String userNo = CommonUtil.getUserNo(request);
		Timestamp nowTs = UtilDateTime.nowTimestamp();
        
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum"));
        String safeQty = UtilFormatOut.checkNull(request.getParameter("safeQty"));
        
        try {	
			Map map = new HashMap();
			map.put("mtrNum", mtrNum);
			map.put("safeQty", safeQty);
			map.put("transBy", userNo);
			map.put("updateTime", nowTs);
			
			GenericValue gv = delegator.makeValidValue("McsPrSafeQty", map);
			delegator.store(gv);	        
        } catch(Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
	 * 线边仓数量盘点 起始页
     * @param request
     * @param response
     * @return
     */
    public static String modifyStoReqEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).trim().toUpperCase();
        //String recipient = UtilFormatOut.checkNull(request.getParameter("recipient")).trim();
        
        String orderBy = UtilFormatOut.checkNull(request.getParameter("orderBy"));
        if (StringUtils.isEmpty(orderBy)) {
        	orderBy = "mtr_num";
        }
        
        try {
        	if (StringUtils.isNotEmpty(mtrGrp)) {
	        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
		        Long userDeptIndex = user.getLong("deptIndex");
				
				// 需暂存领料记录List
		        Map paramMap = new HashMap();
				paramMap.put("deptIndex", userDeptIndex);
				paramMap.put("activeFlag", ConstantsMcs.N);
				paramMap.put("mtrGrp", mtrGrp);
				paramMap.put("mtrNum", mtrNum);
				//paramMap.put("recipient", recipient);
				paramMap.put("orderBy", orderBy);
				List stoReqList = CabinetHelper.queryStoReqList(delegator, paramMap);
				request.setAttribute("stoReqList", stoReqList);
        	}
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
     * 线边仓数量盘点 提交事件
     * @param request
     * @param response
     * @return
     */
    public static String modifyStoReq(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);        
        String[] materialStoReqIndexArray = request.getParameterValues("objCheckbox");
        String[] actualQtyArray = request.getParameterValues("actualQty");
        
        try {
        	if (materialStoReqIndexArray != null && materialStoReqIndexArray.length > 0) {
        		String userNo = CommonUtil.getUserNo(request);
    			Timestamp nowTs = UtilDateTime.nowTimestamp();
    			List toStore = new LinkedList();
    			String successMsg = "";
    			
    			for (int i = 0; i < materialStoReqIndexArray.length; i++) {
    				Long materialStoReqIndex = Long.valueOf(materialStoReqIndexArray[i]);
    				GenericValue stoReqGv = delegator.findByPrimaryKey("McsMaterialStoReq", UtilMisc.toMap(ConstantsMcs.MATERIAL_STO_REQ_INDEX, materialStoReqIndex));
    				Long activeQty = stoReqGv.getLong("activeQty");//已暂存使用数量
    				
    				Long actualQty = Long.valueOf(actualQtyArray[i]);//实际库存(未暂存数量)
    				long qty = actualQty.longValue() + activeQty.longValue();//盘点实际总领用
    				stoReqGv.put("qty", new Long(qty));
    				
    				if (actualQty.longValue() == 0) {
    					stoReqGv.put("activeFlag", ConstantsMcs.Y);
    				}    				
    				stoReqGv.put("activeTime", nowTs);
    				stoReqGv.put("reasonForMovement", userNo + "盘点库存");
    			    toStore.add(stoReqGv);
    			    
    			    successMsg = successMsg + stoReqGv.getString("mtrDesc") +  " 实际库存已修改为 " + actualQtyArray[i] + "。\n";
    			}
    			
    			delegator.storeAll(toStore);
    			request.setAttribute("_EVENT_MESSAGE_", successMsg);
        	}
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
	 * 更换备耗件 查询页面
	 * 选择处理流程，查询保养物料设定备耗件
	 * 选择物料组和料号，查询线边仓已领用库存
     * @param request
     * @param response
     * @return
     */
    public static String useSparePartEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
        String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum")).trim().toUpperCase();
        String eqpId = UtilFormatOut.checkNull(request.getParameter("eqpId"));
        String flow = UtilFormatOut.checkNull(request.getParameter("flow"));
        
        try {
        	if (StringUtils.isNotEmpty(eqpId)) {
	        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
		        Long userDeptIndex = user.getLong("deptIndex");
	
				Map paramMap = new HashMap();
				paramMap.put("deptIndex", userDeptIndex);
				paramMap.put("eqpId", eqpId);
				
				if (StringUtils.isNotEmpty(flow)) {//按处理流程查询
					paramMap.put("flow", flow);
				} else {//按物料组料号查询
					paramMap.put("mtrGrp", mtrGrp);
					paramMap.put("mtrNum", mtrNum);
				}				
				
				List stoReqList = CabinetHelper.queryStoReqListPartsPm(delegator, paramMap);
				request.setAttribute("stoReqList", stoReqList);
        	}
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }    
    
    /** 
     * 更换备耗件
     * @param request
     * @param response
     * @return
     */
    public static String useSparePart(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String userNo = CommonUtil.getUserNo(request);        
                
        try {
			String[] mtrNumSelectArray = request.getParameterValues("objCheckbox");//料号
			String[] useQtyArray = request.getParameterValues("useQty");
			//String eqpId = request.getParameter("eqpId");
			//String note = request.getParameter("note");

	    	if (0 == mtrNumSelectArray.length) {
	    		request.setAttribute("_ERROR_MESSAGE_", "未选择物料，请在列表中勾选！");
	            return "error";
			} else {				
				Map paramMap = GeneralEvents.getInitParams(request);//eqpId,useNote; pms:eventType,eventIndex,periodIndex,flowIndex
				paramMap.put("userNo", userNo);
				paramMap.put("mtrNumSelectArray", mtrNumSelectArray);
				paramMap.put("useQtyArray", useQtyArray);
				//paramMap.put("eqpId", eqpId);
				//paramMap.put("note", note);
				
				Map result = dispatcher.runSync("useSparePartBySto", UtilMisc.toMap("paramMap", paramMap));
				// throw exception
				if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
					throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
				}
						
				String returnMsg = (String) result.get("returnMsg");
				request.setAttribute("_EVENT_MESSAGE_", returnMsg + "已更换。");
				
				// pms填写保养或异常表单返回主页面
				if (StringUtils.isNotEmpty(request.getParameter("eventType"))) {
					request.setAttribute("flag", "Y");
				}
			}
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
	 * 使用备耗件 查询物料可用设备
     * @param request
     * @param response
     * @return
     */
    public static String intoRelateForm(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String reqIndex = UtilFormatOut.checkNull(request.getParameter("reqIndex"));
        request.setAttribute("reqIndex", reqIndex);
        
        try {
        	if (StringUtils.isNotEmpty(reqIndex)) {
	        	GenericValue stoReqInfo = delegator.findByPrimaryKey("McsMaterialStoReq", UtilMisc.toMap("materialStoReqIndex", reqIndex));
	        	request.setAttribute("stoReqInfo", stoReqInfo);
	        	
	        	Integer availableQty = Integer.valueOf(stoReqInfo.getString("qty")) - Integer.valueOf(stoReqInfo.getString("activeQty"));
	        	request.setAttribute("availableQty", availableQty.toString());
	        	
	        	String mtrNum = stoReqInfo.getString("mtrNum");
	        	request.setAttribute("partNo", mtrNum);
	        	
	        	List eqpList = delegator.findByAnd("McsMtrObject", UtilMisc.toMap("mtrNum", mtrNum));
	        	request.setAttribute("eqpList", eqpList);
        	}
        } catch (Exception e) {
          Debug.logError(e.getMessage(), module); 
          request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
          return "error";
      }
        
        return "success";
    }
    
	/**
	 * 根据设备ID查询最近2周的保养/异常表单
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String queryOverFormByEqpId(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String partCount = request.getParameter("partCount"); // 备件使用数量
		String reqIndex = request.getParameter("reqIndex"); // 领料记录
		request.setAttribute("reqIndex", reqIndex);

		String eqpId = request.getParameter("eqpId");
		String eventType = request.getParameter("eventType");
		
		request.setAttribute("eqpId", eqpId);
		request.setAttribute("eventType", eventType);

		try {
			GenericValue stoReqInfo = delegator.findByPrimaryKey("McsMaterialStoReq",
					UtilMisc.toMap("materialStoReqIndex", reqIndex));
			request.setAttribute("stoReqInfo", stoReqInfo);
			Integer availableQty = Integer.valueOf(stoReqInfo.getString("qty")) - Integer.valueOf(stoReqInfo.getString("activeQty"));
        	request.setAttribute("availableQty", availableQty.toString());
			
			String mtrNum = stoReqInfo.getString("mtrNum");
			request.setAttribute("partNo", mtrNum);
        	List eqpList = delegator.findByAnd("McsMtrObject", UtilMisc.toMap("mtrNum", mtrNum));
        	request.setAttribute("eqpList", eqpList);

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar nowDate = Calendar.getInstance();// 得到当前时间
			String endDate = dateFormat.format(nowDate.getTime());
			nowDate.add(Calendar.DATE, -30);
			String startDate = dateFormat.format(nowDate.getTime());
			
			GenericValue userInfo = AccountHelper.getUserInfo(request,delegator);              
            String dept = userInfo.getString("accountDept");

			Map parm = new HashMap();
			parm.put("eqpId", eqpId);
			parm.put("startDate", startDate);
			parm.put("endDate", endDate);
			parm.put("dept", dept);
			parm.put("maintDept", dept);

			// 判断表单类型
			if ("PM".equals(eventType)) {
				List pmFormList = PmHelper.queryPmFormByCondition(delegator, parm);
				request.setAttribute("pmFormList", pmFormList);
			} else if ("TS".equals(eventType)) {
				List abnormalFormList = TsHelper.queryAbnormalFormByCondition(delegator, parm);
				request.setAttribute("abnormalFormList", abnormalFormList);
			}

			request.setAttribute("startDate", startDate);
			request.setAttribute("endDate", endDate);
			request.setAttribute("eqpId", eqpId);
			
			// 关键备件信息
			GenericValue keyPartInfo = PartsHelper.getKeyPartInfo(delegator, mtrNum, eqpId);
			request.setAttribute("keyPartInfo", keyPartInfo);
			
			// 备件供应商
			List partsVendors = delegator.findByAnd("PartsVendors", UtilMisc.toMap("partsId", mtrNum));
			request.setAttribute("partsVendors", partsVendors);

		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	 /** 
     * 更换备耗件
     * @param request
     * @param response
     * @return
     */
    public static String savePartsUse(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String userNo = CommonUtil.getUserNo(request);
        String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);

        Map paramMap = PmFormEvent.getInitParams(request);
        paramMap.put("userNo", userNo);
        paramMap.put("deptIndex", deptIndex);
        String eventType = (String) request.getParameter("eventType");
        String eventIndex = (String) request.getParameter("eventIndex");
                
        try {
			Map result = dispatcher.runSync("savePartsUse", UtilMisc.toMap("paramMap", paramMap));
			// throw exception
			if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
				throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
			}
			String returnMsg = (String) result.get("returnMsg");
			if (StringUtils.isNotEmpty(returnMsg)) {
				request.setAttribute("_ERROR_MESSAGE_", returnMsg);
				return "error";
			}
			
			// 发送备件使用补填主管签核
			String objectIndex = eventIndex;
			String object = "PM".equals(eventType) ? "PM_PARTS_USE" : "TS_PARTS_USE"; // PM or TS
//			if(WorkflowHelper.checkSubmit(delegator, objectIndex, object)) {
//				request.setAttribute("_ERROR_MESSAGE_", "该对象已有签核中，发送签核信息失败");
//				return "error";
//			}

			Map submitResult = WorkflowHelper.sendSubmit(dispatcher, object, objectIndex, "备耗件使用补填", "PATCH", userNo);
			if (submitResult.get(ModelService.ERROR_MESSAGE) != null) {
				request.setAttribute("_ERROR_MESSAGE_", submitResult.get(ModelService.ERROR_MESSAGE));
				return "error";
			}
			
			request.setAttribute("_EVENT_MESSAGE_", "备耗件使用补填，已提交主管审批。");
				
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
}
