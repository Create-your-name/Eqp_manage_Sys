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
 * �� UseEvent.java 
 * @version  1.0  2009-7-21
 * @author   dinghh
 */
public class UseEvent extends GeneralEvents {
	public static final String module = UseEvent.class.getName();
	
	/**
     * ��ѯ���������Ͽ�ʹ���豸
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
     * �ݴ��˿���ʼҳ
     * �õ��û�����
     * ��ʾ�ݴ�����������
     * �õ�7�������˿��б�
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
        	// �õ��û�����
        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
	        String userDeptIndex = user.getString("deptIndex");
	        
	        if (StringUtils.isNotEmpty(mtrGrp)) {							
				// ��ʾ�ݴ�����������			
				List cabinetList = UseHelper.queryCabinetNewMaterialList(delegator, userDeptIndex, mtrGrp, mtrNum);			
				request.setAttribute("cabinetList", cabinetList);				
	            
				// �õ�7�������˿��б�
				List backStockList = UseHelper.queryBackStockList(delegator, userDeptIndex, mtrGrp, mtrNum);
				request.setAttribute("backStockList", backStockList);
	        }	
			
	        // ����������ɺ�õ�transactionId
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
     * �������ύ�¼�, ����ݴ��˿����
     * �����˿⡢��¼��ʷ
     * @param request
     * @param response
     * @return
     */
    public static String cabinetBackStock(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
    	LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String userNo = CommonUtil.getUserNo(request);
        // �õ��û�����
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
				
				// ����Service By QTY
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
     * �ݴ��˿�(BARCODE)��ʼҳ
     * �õ��û�����
     * ��ʾ�ݴ�������Ч����������(��̽���SOG)
     * �õ�7�������˿��б�
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
        	// �õ��û�����
        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
	        String userDeptIndex = user.getString("deptIndex");
	        
	        if (StringUtils.isNotEmpty(mtrGrp)) {
	        	Map paramMap = new HashMap();
				paramMap.put("deptIndex", userDeptIndex);
				paramMap.put("status", ConstantsMcs.CABINET_NEW);				
				paramMap.put("mtrGrp", mtrGrp);
				paramMap.put("mtrNum", mtrNum);
				paramMap.put("whereString", "nvl(mrb_date, shelf_life_expiration_date) < sysdate");
	        	
				// ��ʾ�ݴ�������Ч����������List
	        	List cabinetList = McsCommonHelper.queryMaterialMapList(delegator, paramMap);
				request.setAttribute("cabinetList", cabinetList);
	            
				// �õ�7�������˿��б�
				List backStockList = UseHelper.queryBackStockList(delegator, userDeptIndex, mtrGrp, mtrNum);
				request.setAttribute("backStockList", backStockList);
	        }	
			
	        // ����������ɺ�õ�transactionId
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
     * ��Ƭ��ʹ����ʼҳ OR ���׼�ʹ����ʼҳ
     * �õ��û�����
     * ��ʾ�豸�������ϣ�
     * ��ʾ�ݴ�����������
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
        
        // ���׼�ʹ�ã����Ӳ���materialSuitIndex,aliasName
        String materialSuitIndex = request.getParameter("materialSuitIndex");
        String aliasName = request.getParameter("aliasName");
        request.setAttribute("aliasName", aliasName);
        
        try {
        	// �õ��û�����
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
				
		        // ��ʾ�豸��������List
	        	List eqpMaterialList = UseHelper.queryEqpMaterialList(delegator, paramMap);
				request.setAttribute("eqpMaterialList", eqpMaterialList);
							
				// ��ʾ�ݴ�����������	
				List cabinetList = UseHelper.queryCabinetMaterialList(delegator, paramMap);
				request.setAttribute("cabinetList", cabinetList);
	        }
			
	        // ����������ɺ�õ�transactionId
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
     * ��Ƭ�� OR ���׼� ʹ���ύ�¼�, ��ɻ��� �� ���²���
     * ���ϻ����豸����¼��ʷ
     * ���ϻ����豸����¼��ʷ     
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
				
				// ����Service�޸�״̬
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
     * ������ʹ����ʼҳ
     * �õ��û�����
     * ��ʾ�豸�������ϣ�
     * ��ʾ�ݴ�����������
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
        	// �õ��û�����
        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
	        String userDeptIndex = user.getString("deptIndex");
	        
	        if (StringUtils.isNotEmpty(eqpId)) {
		        // ��ʾ�豸��������List
	        	List eqpMaterialList = UseHelper.queryEqpMaterialListByQty(delegator, userDeptIndex, eqpId, mtrGrp, mtrNum);
				request.setAttribute("eqpMaterialList", eqpMaterialList);
							
				// ��ʾ�ݴ�����������			
				List cabinetList = UseHelper.queryCabinetMaterialListByQty(delegator, userDeptIndex, eqpId, mtrGrp, mtrNum);			
				request.setAttribute("cabinetList", cabinetList);
	        }
			
	        // ����������ɺ�õ�transactionId
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
     * ������ʹ���ύ�¼�, ��ɻ��� �� ���²���
     * ���ϻ����豸����¼��ʷ
     * ���ϻ����豸����¼��ʷ     
     * @param request
     * @param response
     * @return
     */
    public static String useMaterialByQty(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
    	LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String userNo = CommonUtil.getUserNo(request);
        // �õ��û�����
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
				
				// ����Service By QTY
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
     * ��Barcodeʹ����ʼҳ
     * �õ��û�����
     * ��ʾ�豸�������ϣ�
     * ��ʾͬƷ��һ����������
     * ��ʾ�ݴ�����������
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
        	// �õ��û�����
        	GenericValue user = AccountHelper.getUserInfo(request, delegator);
	        String userDeptIndex = user.getString("deptIndex");
	        
	        if (StringUtils.isNotEmpty(eqpId)) {
	        	Map paramMap = new HashMap();
				paramMap.put("deptIndex", userDeptIndex);
				paramMap.put("eqpId", eqpId);
				paramMap.put("mtrGrp", mtrGrp);
				paramMap.put("mtrNum", mtrNum);
				
		        // 1.��ʾ�豸��������List
	        	List eqpMaterialList = UseHelper.queryEqpMaterialList(delegator, paramMap);
				request.setAttribute("eqpMaterialList", eqpMaterialList);
				
				// 2.��ʾͬƷ��һ����������
				// 101028�޸�Ϊ ���� ��¼������Ŀ�������
				List forUseList = UseHelper.queryForUseList(delegator, paramMap);
				request.setAttribute("forUseList", forUseList);
							
				// 3.��ʾ�ݴ�����������	
				List cabinetList = UseHelper.queryCabinetMaterialListByBarcode(delegator, paramMap);
				request.setAttribute("cabinetList", cabinetList);
				
				// 4.����ʱ��У��24Сʱ�������һ�λ��ϵĹ�̽�
				String lastPrUsed = UseHelper.getLastPrUsed(delegator, eqpId);
				request.setAttribute("lastPrUsed", lastPrUsed);
				
				if (ConstantsMcs.PHOTORESIST.equals(mtrGrp)) {
					// 5.����ɨ��ǹ15������ɨ��Ĺ�̽���ҳ���Զ�ѡ���豸�������ʹ�ù�̽�
					List scanBarcodeList = UseHelper.queryScanBarcodeList(delegator, eqpId);
					request.setAttribute("scanBarcodeList", scanBarcodeList);
					
					String strScanBarcode = "";//���Ϲ�̽������ַ���
					for (int i=0; i < scanBarcodeList.size(); i++) {
						HashMap map = (HashMap) scanBarcodeList.get(i);
						String barcode = (String) map.get("BARCODE");
						strScanBarcode = strScanBarcode + barcode + ",";						
					}
					request.setAttribute("strScanBarcode", strScanBarcode);
				}
	        }
			
	        // ����������ɺ�õ�transactionId
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
     * ��Barcode�����ύ�¼�, ��ɻ��²�����
     * ���ϻ����豸����¼��ʷ
     * ���ϻ����豸����¼��ʷ          
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
			
			// ����
			if (eqpMtrSelectArray != null) {
				mtrStatusIdArray = eqpMtrSelectArray;
				oldStatus = ConstantsMcs.USING;
				String offNote = request.getParameter("offNote");
				
				if (newStatus.equals(ConstantsMcs.OFF_AND_USE)) {
					newStatus = ConstantsMcs.FINISH; // ��̽���SOGĬ��Ϊ����
					//newStatus = ConstantsMcs.CABINET_RECYCLE;// MCS BARCODE USE for test
				}
				
				paramMap.put("oldStatus", oldStatus);
				paramMap.put("newStatus", newStatus);
				paramMap.put("mtrStatusIdArray", mtrStatusIdArray);
				paramMap.put("note", offNote);//���±�ע
				
				// ����Service�޸�״̬
				UseHelper.modifyMtrStatusByService(request, dispatcher, "changeMaterialStatusById", paramMap);
				
				// SOG ���߱��ϲ�������email
				if (newStatus.equals(ConstantsMcs.ONLINE_SCRAP_OPT) && ConstantsMcs.CHEMICAL.equals(mtrGrp)) {
					StringBuffer mailContent = new StringBuffer();
					mailContent.append("������: " + userNo + "\n");
                    mailContent.append("�豸: " + eqpId + "\n");
                    mailContent.append("���/����: " + request.getParameter("offAliasName") + "\n");
                    mailContent.append("���±�ע: " + offNote + "\n");                    
					CommonUtil.sendMail(Constants.IT_SYSTEM_MAIL, Constants.THINFILM_PROCESS_MAIL, null, "MCS - SOG���߱�������", mailContent.toString());
				}
			}// end ����
			
			// ����
			if (cabinetMtrSelectArray != null) {
				mtrStatusIdArray = cabinetMtrSelectArray;
				oldStatus = ConstantsMcs.CABINET;
				newStatus = ConstantsMcs.USING;
				
				paramMap.put("oldStatus", oldStatus);
				paramMap.put("newStatus", newStatus);
				paramMap.put("mtrStatusIdArray", mtrStatusIdArray);
				paramMap.put("note", request.getParameter("useNote"));
				
				// ����Service�޸�״̬
				UseHelper.modifyMtrStatusByService(request, dispatcher, "changeMaterialStatusById", paramMap);
			}// end ����			
        
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module); 
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }

	/**
     * ά����ϴ ������ʼҳ
     * �õ��û�����
     * ��ʾ���ϣ��ڲ�ά�ޡ�������ϴ������ά�ޡ�������ϴ��
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
	        	// �õ��û�����
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
				
		        // ��ʾ�ڲ�ά������List
	        	List fabRepairMaterialList = McsCommonHelper.queryMaterialGvList(delegator, paramMap);
				request.setAttribute("fabRepairMaterialList", fabRepairMaterialList);
		        
		        // ����������ɺ�õ�transactionId
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
     * ��Ƭ���ύ�¼�, ��� �ݴ��˿�(BARCODE) �� �ڲ�ά�� ����
     * �޸�����״̬����¼��ʷ   
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
				
				// ����Service�޸�״̬
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
     * ����ʯӢ��ʷʹ�� ��ʼҳ��
     * @param request
     * @param response
     * @return
     */
    public static String supplementaryCompleteEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);        
        GenericValue user = AccountHelper.getUserInfo(request, delegator);
        String userDeptIndex = user.getString("deptIndex");
        String mtrGrp = request.getParameter("mtrGrp");
        //Ԥ���׼�ListΪʯӢ�׼�
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
            // ��ʾʯӢ��ʷʹ���б�
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
     * ����ʯӢ��ʷʹ��
     * @param request
     * @param response
     * @return
     */
    public static String manageUsingHistory(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);
        LocalDispatcher dispatcher = CommonUtil.getMcsDispatch(delegator);
        String shelfLifeExpirationDate = request.getParameter("shelfLifeExpirationDate");
        if (UseHelper.toShelfLifeExpirationDate(shelfLifeExpirationDate)==null) {
        	request.setAttribute("_ERROR_MESSAGE_", "��������ڸ�ʽ����.");
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
            request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
            
            // ��ʾʯӢ��ʷʹ���б�
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
     * �޸ı���
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
            request.setAttribute("_EVENT_MESSAGE_", "�޸ĳɹ���");
            
            // ��ʾʯӢ��ʷʹ���б�
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
     * ��֤SOG 512 OK
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
						
			// ��֤OK
			for (int i = 0; i < mtrStatusIdArray.length; i++) {
				Long mtrStatusId = Long.valueOf(mtrStatusIdArray[i]);
				GenericValue gv = delegator.findByPrimaryKey("McsMaterialStatus", UtilMisc.toMap("materialStatusIndex", mtrStatusId));

				List validOkList = delegator.findByAnd("McsMaterialStatus", UtilMisc.toMap("vendorBatchNum", gv.getString("vendorBatchNum"), "createdTxStamp", gv.getTimestamp("createdTxStamp")));
				for (int j = 0; j < validOkList.size(); j++) {
					GenericValue validOkGv = (GenericValue) validOkList.get(j);
					validOkGv.set("remark", "��֤OK");
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
