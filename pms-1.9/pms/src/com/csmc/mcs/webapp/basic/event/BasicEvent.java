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
 * �� BasicEvent.java 
 * @version  1.0  2009-7-22
 * @author   jiangjing
 */
public class BasicEvent extends GeneralEvents{
	public static final String module = BasicEvent.class.getName();    
	
	/**
     * �����������ѯ������Ϣ
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
				// 1�õ���ά������List
				paramMap.put("enabled", String.valueOf(ConstantsMcs.INTEGER_0));
				paramMap.put("inControl", String.valueOf(ConstantsMcs.INTEGER_1));
				List disenableMateriaList = BasicHelper.getMaterialInfoList(delegator, paramMap);
				request.setAttribute("disenableMaterialList", disenableMateriaList);
				
				// 2�õ���ά������list				
				paramMap.put("enabled", String.valueOf(ConstantsMcs.INTEGER_1));
				paramMap.put("inControl", String.valueOf(ConstantsMcs.INTEGER_1));
				List enableMaterialList = BasicHelper.getMaterialInfoList(delegator, paramMap);
				request.setAttribute("enableMaterialList", enableMaterialList);
				
				// 3�õ��ѽ�������list				
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
     * ��������index��ѯ������Ϣ
     * @param request
     * @param response
     * @return
     */    
    public static String getMaterialByMtrIndex(HttpServletRequest request, HttpServletResponse response) {   	
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String materialIndex = request.getParameter("materialIndex"); 
        String PrivFlag= AccountHelper.getPrivFlag(request, delegator);       
        if (PrivFlag != null || StringUtils.isNotEmpty(PrivFlag)) {
        // �жϱ�������Ƿ���Ȩ��ѡ��N��flag
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
    	    // �õ���Ӧ����Index��Ӧ��������Ϣ
    		request.setAttribute("materialInfo", materialInfo);
    		
        	if (materialIndex != null || StringUtils.isNotEmpty(materialIndex)) {
	        	//�õ���ά�����豸�б�
	    		Map mtrObjectMap = new HashMap();
	    		mtrObjectMap.put("materialIndex", materialIndex);
	    		List mtrObjectList = delegator.findByAnd("McsMtrObject", mtrObjectMap,UtilMisc.toList("usingObjectId"));
				request.setAttribute("mtrObjectList", mtrObjectList);
        	}
        	
	        // �õ���Ӧ����Index��Ӧ��������Ϣ
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
     * ����SAP������Ϣ
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
				request.setAttribute("_EVENT_MESSAGE_", "��ѯ�ɹ���");
			}
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}          
        
    	return "sucess";
    }
        
    /**
     * ά��������Ϣ
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
        
        //�����޸Ĳ���:�ܿش�ԭ����ֵ,ά��״̬����
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
              
        //���ݲ�����ʽ����ʷ��
		try {			
	        String msg = BasicHelper.saveMaterialInfo(delegator, hashMap);	
			request.setAttribute("_EVENT_MESSAGE_", "�Ϻ�"+ msg + "����ɹ���");
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}    
		return "success";
    }

    /**
     * ά�������豸��Ϣ��ѯ
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
     * ά�������豸Index��ѯ�����豸��Ϣ
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
     * ����/�޸� �����豸
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
 			    		 
    		 request.setAttribute("_EVENT_MESSAGE_", "����ɹ���"); 
    		 request.setAttribute("materialIndex", materialIndex);  		 
    	 } catch (Exception e) {
 			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
         }     	                                                     	
    	return "sucess";
    }
    
    /**
     * ɾ�� ���Ͽ����豸
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
	   		 request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���"); 
	   		 request.setAttribute("materialIndex", materialIndex);     		
    		
    	}  catch (Exception e) {    		
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";    		
    	}    	
    	return "sucess";
    }
    
    
    // ---------------------------------------------�¼�ϸ�� jiangjing--------------------------------------		
		
	/**
     * ��ʾ�׼����Ƽ�����
     * @param request
     * @param response
     * @return
     */
    public static String defineSuitMaterialEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String mtrGrp = request.getParameter("mtrGrp");
        //Ԥ���׼�ListΪʯӢ�׼�
		if (StringUtils.isEmpty(mtrGrp)) {
			 mtrGrp = ConstantsMcs.QUARTZ;
		}
		request.setAttribute("mtrGrp", mtrGrp);
    	        
        try{
	        // ��ʾ��ѯ����׼� List
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
     * �����׼�����
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
            request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
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
     * ɾ���׼�
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
	            request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
	        } else {
				request.setAttribute("_ERROR_MESSAGE_", "���׼��������ϺŴ��ڣ�����ɾ���׼��ڵ����Ϻţ�");
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
     * ��ʾ�׼����Ϻ�������
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
        
        // �õ��û�����
    	GenericValue user = AccountHelper.getUserInfo(request, delegator);
        String userDeptIndex = user.getString("deptIndex");
        
        // ��ʾ������
        Map map = new HashMap();
        map.put("mtrGrp", mtrGrp);
        
        // ��ʾ�׼��Ϻ� List
        Map paramMap = new HashMap();
		paramMap.put("materialSuitIndex", materialSuitIndex);
   	        
        try{
        	List mtrGrpList = delegator.findByAnd("McsMaterialGroup", map, UtilMisc.toList("mtrGrp"));
			request.setAttribute("mtrGrpList", mtrGrpList);
        	
			List suitList = delegator.findByAnd("McsMaterialSuit", paramMap, UtilMisc.toList("materialSuitIndex"));
			request.setAttribute("suitList", suitList);
        	
        	List suitMaterialList = BasicHelper.querySuitMaterialList(delegator, paramMap);
        	request.setAttribute("suitMaterialList", suitMaterialList);
        	
        	//��ʾ�׼������Ϻ�
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
     * �����׼��Ϻ�
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
            request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
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
     * �����ϵı༭��ť��ѯֵ
     * 
     * @param request
     *            materialSuitItemIndex
     * @param response
     */
    public static void getJsonSuitMaterialByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // materialSuitItemIndexȡ��
        String materialSuitItemIndex = request.getParameter("materialSuitItemIndex");
        
        try {
            // ȡ���׼����Ϻŵ���Ϣ
            GenericValue gv = delegator.findByPrimaryKey("McsMaterialSuitItem", UtilMisc.toMap("materialSuitItemIndex", materialSuitItemIndex));
            JSONObject mcsMaterialSuitItem = new JSONObject();
            mcsMaterialSuitItem.put("materialSuitItemIndex", UtilFormatOut.checkNull(gv.getString("materialSuitItemIndex")));
            mcsMaterialSuitItem.put("mtrNum", UtilFormatOut.checkNull(gv.getString("materialIndex")));
            mcsMaterialSuitItem.put("mtrQty", UtilFormatOut.checkNull(gv.getString("mtrQty")));

            // д��response
            response.getWriter().write(mcsMaterialSuitItem.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }
    
    /**
     * ɾ���׼��Ϻ�
     * @param request
     * @param response
     * @return
     */    
    public static String delSuitMaterialByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String id = request.getParameter("materialSuitItemIndex");
        
        try {
            BasicHelper.delSuitMaterialByIndex(delegator, id);
            request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }
    
	/**
     * ��ʾ�������Ϻ�
     * @param request
     * @param response
     * @return
     */
    public static String defineVendorMaterialEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getMcsDelegator(request);     
        String mtrGrp = request.getParameter("mtrGrp");
        
        //Ԥ��������Ϊ��̽�
		if (StringUtils.isEmpty(mtrGrp)) {
			 mtrGrp = ConstantsMcs.PHOTORESIST;
		}
		request.setAttribute("mtrGrp", mtrGrp);
		
		// �õ��û�����
    	GenericValue user = AccountHelper.getUserInfo(request, delegator);
        String userDeptIndex = user.getString("deptIndex");
    	        
        try{
	        // ��ʾ��ѯ��ĳ������Ϻ�
            Map paramMap = new HashMap();
			paramMap.put("mtrGrp", mtrGrp);
			paramMap.put("deptIndex", userDeptIndex);
			
			List vendorMaterialList = BasicHelper.queryVendorMaterialList(delegator, paramMap);
        	request.setAttribute("vendorMaterialList", vendorMaterialList);
        	
        	//��ʾ���Ϻ�
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
     * �����ϼ���������
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
            request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
            
            // ��ʾ��������ϼ���������
            Map paramMap = new HashMap();
			paramMap.put("mtrGrp", mtrGrp);
			paramMap.put("deptIndex", userDeptIndex);
			
			List vendorMaterialList = BasicHelper.queryVendorMaterialList(delegator, paramMap);
        	request.setAttribute("vendorMaterialList", vendorMaterialList);
        	
        	//��ʾ���Ϻ�
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
     * �������ű༭��ť��ѯֵ
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

            // д��response
            response.getWriter().write(mcsVendorMaterialItem.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }
    
    /**
     * ɾ����������
     * @param request
     * @param response
     * @return
     */    
    public static String delVendorMaterial(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String mtrNum = request.getParameter("mtrNum");
        
        try {
            BasicHelper.delVendorMaterial(delegator, mtrNum);
            request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }   
}
