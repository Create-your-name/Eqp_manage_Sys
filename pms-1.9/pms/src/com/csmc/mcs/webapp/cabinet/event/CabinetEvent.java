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
 * �� CabinetEvent.java 
 * @version  1.0  2009-6-30
 * @author   dinghh
 */
public class CabinetEvent extends GeneralEvents {
	public static final String module = CabinetEvent.class.getName();
    
	/** 
	 * �����ݴ�(������)��ʼҳ
     * 1.��ʾ�Ƿ�����ά���������ϣ�
     * 2.��ʾ���ݴ������
     * 3.10������Ч�ڹ��� �����б�
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
				
		        // 1.�û����������ϻ������ϴ�ά���б�
				Map paramMap = new HashMap();
				paramMap.put("deptIndex", userDeptIndex);
				paramMap.put("enabled", ConstantsMcs.INTEGER_0);
				paramMap.put("inControl", ConstantsMcs.INTEGER_1);
				paramMap.put("mtrGrp", mtrGrp);
				List mtrMaintainList = CabinetHelper.queryMaterialList(delegator, paramMap);
				request.setAttribute("mtrMaintainList", mtrMaintainList);
				
				// 2.���ݴ����ϼ�¼List
				paramMap.clear();
				paramMap.put("deptIndex", userDeptIndex);
				paramMap.put("activeFlag", ConstantsMcs.N);
				paramMap.put("mtrGrp", mtrGrp);
				paramMap.put("mtrNum", mtrNum);
				paramMap.put("recipient", recipient);
				paramMap.put("orderBy", orderBy);
				List stoReqList = CabinetHelper.queryStoReqList(delegator, paramMap);
				request.setAttribute("stoReqList", stoReqList);
				
				// 3.10������Ч�ڹ��� �����б�
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
     * �����ݴ�(������)�ύ�¼�
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
	    		request.setAttribute("_ERROR_MESSAGE_", "δѡ��Ҫ�����ݴ��������ϣ������ݴ������б��й�ѡ��");
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
				request.setAttribute("_EVENT_MESSAGE_", returnMsg + "�ѽ����ݴ�����");
			}
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
     * �����ݴ棺��ѧƷ���ꡢ���ļ�ֱ��ʹ��
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
	    		request.setAttribute("_ERROR_MESSAGE_", "δѡ��Ҫ�����ݴ��������ϣ������ݴ������б��й�ѡ��");
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
				request.setAttribute("_EVENT_MESSAGE_", returnMsg + "��ʹ�á�");
			}
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /** 
	 * �����ݴ�(��Barcode)��ʼҳ
     * 1.��ʾ�Ƿ�����ά���������ϣ�
     * 2.��ʾ���ݴ������
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
			
	        // 1.�û����������ϻ������ϴ�ά���б�
			Map paramMap = new HashMap();
			paramMap.put("deptIndex", userDeptIndex);
			paramMap.put("enabled", ConstantsMcs.INTEGER_0);
			paramMap.put("inControl", ConstantsMcs.INTEGER_1);
			if (StringUtils.isNotEmpty(mtrGrp)) {
				paramMap.put("mtrGrp", mtrGrp);
			}
			List mtrMaintainList = CabinetHelper.queryMaterialList(delegator, paramMap);
			request.setAttribute("mtrMaintainList", mtrMaintainList);
			
			// 2.���ݴ����ϼ�¼List
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
     * �����ݴ�(��Barcode)�ύ�¼�
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
     * �޸��������� ��ʼҳ��
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
     * �޸����� �ύ
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
     * ¼������ ��ʼҳ��
     * ��̽��г����Ϻźͳ������ŵ�ǰ׺���ƣ� SOG�й�Ӧ�����űȶ����ƣ� ������������ɨ�����������ơ�
     * ������вĴ�SAP������ݱ�ѡ��:��ȡ��
     * ע�⣺�ⷿ���������޸�(modifyVendorInfoEntry) Ҳ���ô˷�����
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
        	    //Ĭ��ѡ���̽�
        	    mtrGrp = ConstantsMcs.PHOTORESIST;
        	}
        	request.setAttribute("mtrGrp", mtrGrp);
        	
        	Map paramMap = new HashMap();
			paramMap.put("deptIndex", userDeptIndex);
			paramMap.put("mtrGrp", mtrGrp);
			paramMap.put("mtrNum", mtrNum);
			
			//1.δ¼������
			List statusList = CabinetHelper.queryStatusListBarCode(delegator, paramMap, "0");
			request.setAttribute("statusList", statusList);
			
			//2.��¼������
			List statusList1 = CabinetHelper.queryStatusListBarCode(delegator, paramMap, "1");
			request.setAttribute("statusList1", statusList1);
			
			// fab1 SAP�вĽӿ�:��ȡ��, isCallbackDrone����false
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
     * ¼������ �ύ
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
	        	
	        	// ��Ĥ��SOG��ҵԱ,��ҵ��,��Ĥ��ҵ��,�೤ �����޸����룬ֻ������һ��
	        	//ref. �õ��û���SecuritySetupEvent.java
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
	        		request.setAttribute("_EVENT_MESSAGE_", "�����ѱ��棬�ɿ�ʼ��BARCODEʹ�á�");
	        	}
        	}			
        	
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            String errorMsg = e.getMessage();
            if (StringUtils.contains(errorMsg, "ORA-00001")) {
                String aliasName = StringUtils.substringBefore(StringUtils.substringAfter(errorMsg, "aliasName,"), "(java.lang.String)");
                errorMsg = "ƿ�� "+ aliasName + "��ʹ�ù����������ϵMES����";
                // ��¼��Material Status Error��
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
     * �Żر���/���� ��ʼҳ
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
     * �Żر���/���� �ύ
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
    					mtrStatusMap.put("note", "�Żر���");
    					mtrStatusMap.put("unfrozenTransTime", null);
    				} else if ("Unfrozen".equals(opType)) {
    					mtrStatusMap.put("note", "����");
    					mtrStatusMap.put("unfrozenTransTime", nowTs);
    				}
    				
    			    GenericValue mtrStatusGv = delegator.makeValidValue("McsMaterialStatus", mtrStatusMap);
    			    toStore.add(mtrStatusGv);
    			    
    			    // ��¼��ʷ
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
     * �ⷿ���������޸� ��ʼҳ
     * @param request
     * @param response
     * @return
     */
    public static String modifyVendorInfoEntry(HttpServletRequest request, HttpServletResponse response) {  
        return inputBarcodeEntry(request, response);
    }
    
    /** 
     * �ⷿ���������޸� �ύ
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
	        	request.setAttribute("_EVENT_MESSAGE_", "�޸ĳɹ�");	        
        	}			
        	
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
    
    /**
     * �ⷿ����ҳ��,��ѯSAP������¼
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
            //SAP�����б�
            if (StringUtils.isNotEmpty(mtrGrp)) {
                Map paramMap = new HashMap();
                paramMap.put("activeFlag", ConstantsMcs.N);
                paramMap.put("mtrGrp", mtrGrp);
                // ��ѯQTY<0�ļ�¼
                paramMap.put("isQtyBelowZero", new Boolean(true));
                List sapStoReqList = CabinetHelper.queryCancelVendorStoReqList(delegator,
                        paramMap);
                request.setAttribute("sapStoReqList", sapStoReqList);
            }
            //�������б�
            if (StringUtils.isNotEmpty(objRadio)) {
                String[] objRadioArray = objRadio.split(ConstantsMcs.SEPARATOR);
                //���������ύ����ʾ���������б�
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
     * �ⷿ���������ύ
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
        StringBuffer eventMsg = new StringBuffer("�ɹ�����:\n");
        
        try {
            //�޸�SAP������¼
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
            
            //�޸��������б��¼
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
                    eventMsg.append("�������� : ").append(vendorBatchNum)
                            .append(" ,���Ϻ� : ").append(mtrNum)
                            .append(" ,�������� : ").append(mtrDesc).append(" ")
                            .append(" ,�������� : ").append(cancelVendorNum).append(";\n");
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
	 * �޸��ݴ����� ��ʼҳ
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
     * �޸��ݴ����� �ύ
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
    				String note = "�޸��ݴ�����: [" + unfrozenTransTime + "] [" + noteArray[i] + "]";

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
    			    
    			    // ��¼��ʷ
				    Long mtrStatusHistId = delegator.getNextSeqId(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX);
				    mtrStatusMap.put(ConstantsMcs.MATERIAL_STATUS_HIST_INDEX, mtrStatusHistId);
				    
				    GenericValue mtrStatusHistGv = delegator.makeValidValue("McsMaterialStatusHist", mtrStatusMap);
				    toStore.add(mtrStatusHistGv);
    			    
    			    successMsg = successMsg + mtrStatusId.toString() +  " ���޸ġ�\n";
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
     * ��̽����ò�ѯ
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
     * ��̽���ȫ���༭��ť��ѯֵ
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

            // д��response
            response.getWriter().write(jsObj.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }
    
    /**
     * ��̽�����:��ȫ����޸�
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
	 * �߲߱������̵� ��ʼҳ
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
				
				// ���ݴ����ϼ�¼List
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
     * �߲߱������̵� �ύ�¼�
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
    				Long activeQty = stoReqGv.getLong("activeQty");//���ݴ�ʹ������
    				
    				Long actualQty = Long.valueOf(actualQtyArray[i]);//ʵ�ʿ��(δ�ݴ�����)
    				long qty = actualQty.longValue() + activeQty.longValue();//�̵�ʵ��������
    				stoReqGv.put("qty", new Long(qty));
    				
    				if (actualQty.longValue() == 0) {
    					stoReqGv.put("activeFlag", ConstantsMcs.Y);
    				}    				
    				stoReqGv.put("activeTime", nowTs);
    				stoReqGv.put("reasonForMovement", userNo + "�̵���");
    			    toStore.add(stoReqGv);
    			    
    			    successMsg = successMsg + stoReqGv.getString("mtrDesc") +  " ʵ�ʿ�����޸�Ϊ " + actualQtyArray[i] + "��\n";
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
	 * �������ļ� ��ѯҳ��
	 * ѡ�������̣���ѯ���������趨���ļ�
	 * ѡ����������Ϻţ���ѯ�߲߱������ÿ��
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
				
				if (StringUtils.isNotEmpty(flow)) {//���������̲�ѯ
					paramMap.put("flow", flow);
				} else {//���������ϺŲ�ѯ
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
     * �������ļ�
     * @param request
     * @param response
     * @return
     */
    public static String useSparePart(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String userNo = CommonUtil.getUserNo(request);        
                
        try {
			String[] mtrNumSelectArray = request.getParameterValues("objCheckbox");//�Ϻ�
			String[] useQtyArray = request.getParameterValues("useQty");
			//String eqpId = request.getParameter("eqpId");
			//String note = request.getParameter("note");

	    	if (0 == mtrNumSelectArray.length) {
	    		request.setAttribute("_ERROR_MESSAGE_", "δѡ�����ϣ������б��й�ѡ��");
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
				request.setAttribute("_EVENT_MESSAGE_", returnMsg + "�Ѹ�����");
				
				// pms��д�������쳣��������ҳ��
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
	 * ʹ�ñ��ļ� ��ѯ���Ͽ����豸
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
	 * �����豸ID��ѯ���2�ܵı���/�쳣��
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String queryOverFormByEqpId(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String partCount = request.getParameter("partCount"); // ����ʹ������
		String reqIndex = request.getParameter("reqIndex"); // ���ϼ�¼
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
			Calendar nowDate = Calendar.getInstance();// �õ���ǰʱ��
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

			// �жϱ�����
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
			
			// �ؼ�������Ϣ
			GenericValue keyPartInfo = PartsHelper.getKeyPartInfo(delegator, mtrNum, eqpId);
			request.setAttribute("keyPartInfo", keyPartInfo);
			
			// ������Ӧ��
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
     * �������ļ�
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
			
			// ���ͱ���ʹ�ò�������ǩ��
			String objectIndex = eventIndex;
			String object = "PM".equals(eventType) ? "PM_PARTS_USE" : "TS_PARTS_USE"; // PM or TS
//			if(WorkflowHelper.checkSubmit(delegator, objectIndex, object)) {
//				request.setAttribute("_ERROR_MESSAGE_", "�ö�������ǩ���У�����ǩ����Ϣʧ��");
//				return "error";
//			}

			Map submitResult = WorkflowHelper.sendSubmit(dispatcher, object, objectIndex, "���ļ�ʹ�ò���", "PATCH", userNo);
			if (submitResult.get(ModelService.ERROR_MESSAGE) != null) {
				request.setAttribute("_ERROR_MESSAGE_", submitResult.get(ModelService.ERROR_MESSAGE));
				return "error";
			}
			
			request.setAttribute("_EVENT_MESSAGE_", "���ļ�ʹ�ò�����ύ����������");
				
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }
}
