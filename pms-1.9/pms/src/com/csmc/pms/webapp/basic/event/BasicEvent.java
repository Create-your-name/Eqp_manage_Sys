package com.csmc.pms.webapp.basic.event;

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
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.csmc.pms.webapp.basic.help.BasicHelper;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.common.helper.CommonHelper;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.eqp.helper.GuiHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.GeneralEvents;

public class BasicEvent extends GeneralEvents {

    public static final String module = BasicEvent.class.getName();

    // ---------------------------------------------Ѳ����ʽshaoaj--------------------------------------
    /**
     * ��ѯѲ����ʽ
     * 
     * @param request
     * @param response
     * @return action
     */
    public static String queryPcStyle(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            List list = BasicHelper.getPcStyleListByCondition(delegator);
            request.setAttribute("pcStyleList", list);
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * ����������ѯѲ����ʽ
     * 
     * @param request
     * @param response
     */
    public static void queryPcStyleByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String id = request.getParameter("styleIndex");
        try {
            GenericValue gv = delegator.findByPrimaryKey("PcStyle", UtilMisc.toMap("styleIndex", id));
            JSONObject pcStyle = new JSONObject();
            pcStyle.put("name", gv.getString("name"));
            pcStyle.put("desc", gv.getString("description"));
            response.getWriter().write(pcStyle.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }

    /**
     * ����/����Ѳ����ʽ��Ϣ
     * 
     * @param request
     * @param response
     * @return
     */
    public static String managePcStyle(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String styleIndex = request.getParameter("styleIndex");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        Map hashMap = new HashMap();
        hashMap.put("styleIndex", styleIndex);
        hashMap.put("name", name.toUpperCase());
        hashMap.put("description", description.toUpperCase());
        try {
            BasicHelper.managePcStyle(delegator, hashMap);
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
     * ɾ��Ѳ����ʽ��Ϣ
     * 
     * @param request
     * @param response
     * @return �����Ƿ�ɹ�
     */
    public static String deletePcStyleByPk(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Integer id = new Integer(request.getParameter("styleIndex"));
        try {
            BasicHelper.deletePcStyle(delegator, id);
            request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    // ---------------------------------------------��ʱ/�쳣�������趨 shaoaj--------------------------------------
    /**
     * ��ѯ�豸����
     * 
     * @param request
     * @param response
     * @return �����Ƿ�ɹ�
     */
    public static String queryEquipMentList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            List eqpList = CommonHelper.getEquipmentTypeList(delegator);
            request.setAttribute("equipMentList", eqpList);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    /**
     * �����豸���༰�쳣�����ȡ��Ϣ�б�,����/����/ɾ��������ɺ���Ҫ����˷�������ѯ�б�
     * 
     * @param request
     * @param response
     * @return �����Ƿ�ɹ�
     */
    public static String queryReasonOrOverTimeList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String equipmentType = request.getParameter("equipmentType");
        String reasonType = request.getParameter("reasonType");
        // ����һ�������󣬽���˷����õ�request
        String attrEquip = (String) request.getAttribute("equipmentType");
        String attrReason = (String) request.getAttribute("reasonType");
        String message = "";
        message = (String) request.getAttribute("_EVENT_MESSAGE_");
        List reasonList = null;
        try {
            if (StringUtils.isNotEmpty(attrEquip) && StringUtils.isNotEmpty(attrReason)) {
                reasonList = BasicHelper.getReasonList(delegator, attrEquip, attrReason);
                request.setAttribute("equType", attrEquip);
                request.setAttribute("reasonType", attrReason);
            } else {
                reasonList = BasicHelper.getReasonList(delegator, equipmentType, reasonType);
                request.setAttribute("equType", equipmentType);
                request.setAttribute("reasonType", reasonType);
            }
            List eqpList = CommonHelper.getEquipmentTypeList(delegator);
            request.setAttribute("equipMentList", eqpList);
            request.setAttribute("pmsReasonList", reasonList);
            request.setAttribute("_EVENT_MESSAGE_", message);
            // ��Ҫ��ʼ��ҳ���е��豸���༰ԭ����������
            request.setAttribute("flag", "ok");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }
    
    
    /**
     * ������������GUI��鲽���Զ�Hold
     * @param request
     * @param response
     * @return String success/error
     */
    public static String pmsReasonHold(HttpServletRequest request, HttpServletResponse response) {
		// �����ϴ��ݵ����в�����ϳ�Map
		Map paramMap = getInitParams(request, true, true);
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String eventMsg = "";
		
		String equipmentType = UtilFormatOut.checkNull((String) request.getParameter("equipmentType"));
		String holdReasonIndex = UtilFormatOut.checkNull((String) request.getParameter("holdReasonIndex"));
        String holdFlag = UtilFormatOut.checkNull((String) request.getParameter("holdFlag"));
		
		try {
			if ("1".equals(holdFlag)) {
			    String holdCodeReason = (String) paramMap.get("holdCodeReason");
				String holdCode = holdCodeReason.substring(0, holdCodeReason.indexOf(":"));
				String holdReason = holdCodeReason.substring(holdCodeReason.indexOf(":")+1);
				paramMap.put("holdCode", holdCode);
				paramMap.put("holdReason", holdReason);
				eventMsg = "Hold���óɹ�!";
		    } else if ("0".equals(holdFlag)) {
		    	paramMap.put("holdCode", "");
				paramMap.put("holdReason", "");
				paramMap.put("holdLotNum", "");
				paramMap.put("immediateHold", "");
				paramMap.put("triggerStage", "");
				paramMap.put("holdDesc", "");
				eventMsg = "Hold������ɾ��!";
		    }
			
			// ȡ���û�
		    GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		    String user = userLogin.getString("userLoginId");
		    paramMap.put("transBy", user);
		    paramMap.put("updateTime", UtilDateTime.nowTimestamp());
		    paramMap.put("reasonIndex", holdReasonIndex);
		    GenericValue holdGv = delegator.makeValidValue("PmsReason", paramMap);
		    delegator.store(holdGv);
		
		    // ��ʾ�豸��������һ����flag
		    request.setAttribute("flag", "OK");
		    request.setAttribute("equipmentType", equipmentType);
		    request.setAttribute("_EVENT_MESSAGE_", eventMsg);
		} catch (Exception e) {
		    Debug.logError(e, module);
		    request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		    return "error";
		}
        return "success";
    }

    
    
    /**
     * �쳣/��ʱ�������趨һ������:ȡ�ù����쳣/��ʱ�������趨��Ϣ��������ʾ
     * @param request
     *            eqpType �豸����
     * @param response
     * @return String success/error
     */
    public static String pmsReasonList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // java�д��ݵĲ���������ά����ǰ�豸���ౣ���ϴ������ֵ
            String equipmentType = (String) request.getAttribute("equipType");
            String reasonType = request.getParameter("reasonType");

            // ҳ���ϴ��ݵĲ���
            String equipment_Type = UtilFormatOut.checkNull((String) request.getParameter("equipmentType"));
            String holdReasonIndex = UtilFormatOut.checkNull((String) request.getParameter("holdReasonIndex"));
            String attrReason = (String) request.getAttribute("reasonType");
             

            // �������ֵ���У��ϲ�ȡҳ�����
            if (!StringUtils.isEmpty(equipment_Type)) {
            	equipmentType = equipment_Type;
            }
       
            
            if (!StringUtils.isEmpty(attrReason)) {
            	reasonType = attrReason;
            }
            
            // �鿴���豸�����Ƿ����
            List eqpList = CommonHelper.getEquipmentTypeList(delegator);      
            List promisEqpStatusList = delegator.findByAnd("PromisEqpStatus", UtilMisc.toMap("type", Constants.PM), UtilMisc.toList("eqpStatus"));
            List timeRangeList = delegator.findAllCache("TimeRange");

            List pmsReasonList = null;
            if (StringUtils.isNotEmpty(holdReasonIndex)) {
            	pmsReasonList = delegator.findByAnd("PmsReason", UtilMisc.toMap("equipmentType", equipmentType, "reasonIndex", holdReasonIndex,"reasonType",reasonType), UtilMisc.toList("reason"));
            	
            	//fab1���hold�룬holdԭ�� 
                if (Constants.CALL_TP_FLAG) {
                	GenericValue userInfo = AccountHelper.getUserInfo(request, delegator);
                    String accountDept = userInfo.getString("accountDept");
                	List holdCodeReasonList = GuiHelper.getHoldCodeReasonList(accountDept);
                	request.setAttribute("holdCodeReasonList", holdCodeReasonList);
                	
                	GenericValue holdReason = delegator.findByPrimaryKey("PmsReason", UtilMisc.toMap("reasonIndex", holdReasonIndex));
                	request.setAttribute("holdReason", holdReason);
                	request.setAttribute("holdReasonIndex", holdReasonIndex);
                }
            } else {
            	pmsReasonList = delegator.findByAnd("PmsReason", UtilMisc.toMap("equipmentType", equipmentType,"reasonType",reasonType), UtilMisc.toList("reason"));
            }
            
            request.setAttribute("pmsReasonList", pmsReasonList);
            request.setAttribute("promisEqpStatusList", promisEqpStatusList);   
            request.setAttribute("equipMentList", eqpList);
            request.setAttribute("timeRangeList", timeRangeList);             
            request.setAttribute("equType", equipmentType);
            request.setAttribute("equipmentType", equipmentType);
            request.setAttribute("reasonType", reasonType);

            // �жϲ���ҳ���Ƿ���ʾ��flag
            request.setAttribute("flag", "ok");
            
            //�����¼��Ϊ������֤��(dept_index == 10010)������������������MSA��ͷ
          //  request.setAttribute("isMsaDept", AccountHelper.isMsaDept(request, delegator) ? "true" : "false");            
            
        } catch (Exception e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
    

    /**
     * ����������ѯ�쳣��Ϣ
     * 
     * @param request
     * @param response
     */
    public static void queryReasonByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String id = request.getParameter("reasonIndex");
        try {
            GenericValue gv = delegator.findByPrimaryKey("PmsReason", UtilMisc.toMap("reasonIndex", id));
            JSONObject pmsReason = new JSONObject();
            pmsReason.put("reason", gv.getString("reason"));
            response.getWriter().write(pmsReason.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }

    /**
     * ����/����(��ʱ/�쳣)��������Ϣ
     * 
     * @param request
     * @param response
     * @return �����Ƿ�ɹ�
     */
    public static String manageReason(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String reasonIndex = request.getParameter("reasonIndex");
        String equipmentType = request.getParameter("equipType");
        String reasonType = request.getParameter("reType");
        String reason = request.getParameter("reason");
        Map hashMap = new HashMap();
        hashMap.put("reasonIndex", reasonIndex);
        hashMap.put("equipmentType", equipmentType);
        hashMap.put("reasonType", reasonType);
        hashMap.put("reason", reason);
        hashMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
        request.setAttribute("equipmentType", equipmentType);
        request.setAttribute("reasonType", reasonType);
        try {
            BasicHelper.manageReason(delegator, hashMap);
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
     * ����reasonIndexɾ����Ϣreason
     * 
     * @param request
     * @param response
     * @return �����Ƿ�ɹ�
     */
    public static String deleteReasonByPk(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String id = request.getParameter("reasonIndex");
        String equipmentType = request.getParameter("equipType");
        String reasonType = request.getParameter("reType");
        request.setAttribute("equipmentType", equipmentType);
        request.setAttribute("reasonType", reasonType);
        try {
            BasicHelper.deleteReasonByPk(delegator, id);
            request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    // ---------------------------------------------�����ڱ��������趨 shaoaj--------------------------------------
    /**
     * ��ѯ�����ڱ��������趨
     * 
     * @param request
     * @param response
     * @return action
     */
    public static String queryUnscheduleParameterList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            List list = BasicHelper.queryUnscheduleParameterList(delegator);
            request.setAttribute("unscheduleParameterList", list);
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * ����/���²����ڱ�������
     * 
     * @param request
     * @param response
     * @return
     */
    public static String manageUnscheduleParameter(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String paramIndex = request.getParameter("paramIndex");
        String name = request.getParameter("paramName");
        String description = request.getParameter("description");
        Map hashMap = new HashMap();
        hashMap.put("paramIndex", paramIndex);
        hashMap.put("paramName", name.toUpperCase());
        hashMap.put("description", description.toUpperCase());
        hashMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
        try {
            BasicHelper.manageUnscheduleParameter(delegator, hashMap);
            request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
        } catch (GenericEntityException e) {
            String message = CommonUtil.checkOracleException(e);
            request.setAttribute("_ERROR_MESSAGE_", message);
         }catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    /**
     * ����������ѯ�����ڱ���������Ϣ
     * 
     * @param request
     * @param response
     */
    public static void queryUnscheduleParameterByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String id = request.getParameter("paramIndex");
        try {
            GenericValue gv = delegator.findByPrimaryKey("UnscheduleParameter", UtilMisc.toMap("paramIndex", id));
            JSONObject unscheduleParameter = new JSONObject();
            unscheduleParameter.put("paramName", gv.getString("paramName"));
            unscheduleParameter.put("description", gv.getString("description"));
            response.getWriter().write(unscheduleParameter.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }

    /**
     * ɾ�������ڱ���������Ϣ
     * 
     * @param request
     * @param response
     * @return �����Ƿ�ɹ�
     */
    public static String deleteUnscheduleParameterByPk(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Integer id = new Integer(request.getParameter("paramIndex"));
        try {
            BasicHelper.deleteUnscheduleParameter(delegator, id);
            request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    // ---------------------------------------------��ϴ�������� shaoaj--------------------------------------
    /**
     * ��ѯ��ϴ���������б�
     * 
     * @param request
     * @param response
     * @return action
     */
    public static String queryEquipmentVendorList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            List list = BasicHelper.queryEquipmentVendorList(delegator);
            request.setAttribute("equipmentVendorList", list);
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * ����/������ϴ��������
     * 
     * @param request
     * @param response
     * @return
     */
    public static String manageEquipmentVendor(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String seqIndex = request.getParameter("seqIndex");
        String vendor = request.getParameter("vendor");
        String address = request.getParameter("address");
        String contactName = request.getParameter("contactName");
        String phoneNo = request.getParameter("phoneNo");
        Map hashMap = new HashMap();
        hashMap.put("seqIndex", seqIndex);
        hashMap.put("vendor", vendor);
        hashMap.put("address", address);
        hashMap.put("contactName", contactName);
        hashMap.put("phoneNo", phoneNo);
        hashMap.put("type", Constants.CLEAN);
        hashMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
        try {
            BasicHelper.manageEquipmentVendor(delegator, hashMap);
            request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
        } catch (GenericEntityException e) {
        	String message = CommonUtil.checkOracleException(e);
        	request.setAttribute("_ERROR_MESSAGE_", message);
        }catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    /**
     * ����������ѯ��ϴ����������Ϣ
     * 
     * @param request
     * @param response
     */
    public static void queryEquipmentVendorByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String id = request.getParameter("seqIndex");
        try {
            GenericValue gv = delegator.findByPrimaryKey("EquipmentVendor", UtilMisc.toMap("seqIndex", id));
            JSONObject equipmentVendor = new JSONObject();
            equipmentVendor.put("vendor", gv.getString("vendor"));
            equipmentVendor.put("address", gv.getString("address"));
            equipmentVendor.put("contactName", gv.getString("contactName"));
            equipmentVendor.put("phoneNo", gv.getString("phoneNo"));
            equipmentVendor.put("type", gv.getString("type"));
            response.getWriter().write(equipmentVendor.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }

    /**
     * ɾ����ϴ����������Ϣ
     * 
     * @param request
     * @param response
     * @return �����Ƿ�ɹ�
     */
    public static String deleteEquipmentVendorByPk(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Integer id = new Integer(request.getParameter("seqIndex"));
        try {
            BasicHelper.deleteEquipmentVendorByPk(delegator, id);
            request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    // ---------------------------------------------�豸�ͺ�ά�� shaoaj--------------------------------------
    /**
     * ��ѯ�豸�ͺ��б�
     * 
     * @param request
     * @param response
     * @return action
     */
    public static String queryEquipmentBasicList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            List list = BasicHelper.queryEquipmentBasicList(delegator);
            request.setAttribute("equipmentBasicList", list);
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * ����/�����豸�ͺ�
     * 
     * @param request
     * @param response
     * @return
     */
    public static String manageEquipmentBasic(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String seqIndex = request.getParameter("seqIndex");
        String name = request.getParameter("name");
        String description = request.getParameter("description").toUpperCase();
        Map hashMap = new HashMap();
        hashMap.put("seqIndex", seqIndex);
        hashMap.put("name", name.toUpperCase());
        hashMap.put("description", description.toUpperCase());
        hashMap.put("type", Constants.MODEL);
        hashMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
        try {
            BasicHelper.manageEquipmentBasic(delegator, hashMap);
            request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    /**
     * ����������ѯ�豸�ͺ���Ϣ
     * 
     * @param request
     * @param response
     */
    public static void queryEquipmentBasicByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String id = request.getParameter("seqIndex");
        try {
            GenericValue gv = delegator.findByPrimaryKey("EquipmentBasicData", UtilMisc.toMap("seqIndex", id));
            JSONObject equipmentVendor = new JSONObject();
            equipmentVendor.put("name", gv.getString("name"));
            equipmentVendor.put("description", gv.getString("description"));
            response.getWriter().write(equipmentVendor.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }

    /**
     * ɾ���豸�ͺ���Ϣ
     * 
     * @param request
     * @param response
     * @return �����Ƿ�ɹ�
     */
    public static String deleteEquipmentBasicByPk(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Integer id = new Integer(request.getParameter("seqIndex"));
        try {
            BasicHelper.deleteEquipmentBasicByPk(delegator, id);
            request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    // ---------------------------------------------�¼����� shaoaj--------------------------------------
    /**
     * ��ѯ�¼�����
     * 
     * @param request
     * @param response
     * @return action
     */
    public static String getEventCategoryList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            List list = BasicHelper.getEventCategoryList(delegator);
            request.setAttribute("eventCategoryList", list);
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * ����������ѯ�¼�����
     * 
     * @param request
     * @param response
     */
    public static void queryEventCategoryByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String id = request.getParameter("eventIndex");
        try {
            GenericValue gv = delegator.findByPrimaryKey("EventCategory", UtilMisc.toMap("eventIndex", id));
            JSONObject eventCateGory = new JSONObject();
            eventCateGory.put("category", gv.getString("category"));
            eventCateGory.put("desc", gv.getString("description"));
            response.getWriter().write(eventCateGory.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }

    /**
     * ����/�����¼�����
     * 
     * @param request
     * @param response
     * @return
     */
    public static String manageEventCategory(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String eventIndex = request.getParameter("eventIndex");
        String category = request.getParameter("category");
        String description = request.getParameter("description");
        Map hashMap = new HashMap();
        hashMap.put("eventIndex", eventIndex);
        hashMap.put("category", category.toUpperCase());
        hashMap.put("description", description.toUpperCase());
        hashMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
        try {
            BasicHelper.manageEventCategory(delegator, hashMap);
            request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
        } catch (GenericEntityException e) {
        	String message = CommonUtil.checkOracleException(e);
        	request.setAttribute("_ERROR_MESSAGE_", message);
        }catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    /**
     * ɾ���¼�������Ϣ
     * 
     * @param request
     * @param response
     * @return �����Ƿ�ɹ�
     */
    public static String deleteEventCategoryByPk(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Integer id = new Integer(request.getParameter("eventIndex"));
        try {
            BasicHelper.deleteEventCategoryByIndex(delegator, id);
            request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    // ---------------------------------------------�¼�ϸ�� shaoaj--------------------------------------
    /**
     * ��ѯ�¼�ϸ��
     * 
     * @param request
     * @param response
     * @return action
     */
    public static String queryEventSubCategoryList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String eventIndex = request.getParameter("eventCategory");
        String eventIndex_atter = (String) request.getAttribute("eventCategory");
        try {
            List list = null;
            if (StringUtils.isNotEmpty(eventIndex)) {
                list = BasicHelper.getEventSubCategoryListByIndex(delegator, eventIndex);
                request.setAttribute("eventIndex", eventIndex);
            } else {
                list = BasicHelper.getEventSubCategoryListByIndex(delegator, eventIndex_atter);
                request.setAttribute("eventIndex", eventIndex_atter);
            }
            List eventCategoryList = BasicHelper.getEventCategoryList(delegator);
            request.setAttribute("eventCategoryList", eventCategoryList);
            request.setAttribute("eventSubCategoryList", list);
            // ��Ҫ��ʼ��ҳ���е��豸���༰ԭ����������
            request.setAttribute("flag", "ok");

        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * ����������ѯ�¼�ϸ��
     * 
     * @param request
     * @param response
     */
    public static void queryEventSubCategoryByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String id = request.getParameter("eventSubIndex");
        try {
            GenericValue gv = delegator.findByPrimaryKey("EventSubcategory", UtilMisc.toMap("eventSubIndex", id));
            JSONObject eventSubCateGory = new JSONObject();
            eventSubCateGory.put("subCategory", gv.getString("subCategory"));
            eventSubCateGory.put("desc", gv.getString("description"));
            response.getWriter().write(eventSubCateGory.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }

    /**
     * ����/�����¼�ϸ��
     * 
     * @param request
     * @param response
     * @return
     */
    public static String manageEventSubCategory(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String eventSubIndex = request.getParameter("eventSubIndex");
        String subCategory = request.getParameter("subCategory");
        String description = request.getParameter("description");
        String eventIndex = request.getParameter("eventIndex");
        Map hashMap = new HashMap();
        hashMap.put("eventSubIndex", eventSubIndex);
        hashMap.put("subCategory", subCategory.toUpperCase());
        hashMap.put("description", description.toUpperCase());
        hashMap.put("eventIndex", eventIndex);
        hashMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
        try {
            BasicHelper.manageEventSubCategory(delegator, hashMap);
            request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");
            request.setAttribute("eventCategory", eventIndex);
        } catch (GenericEntityException e) {
        	String message = CommonUtil.checkOracleException(e);
        	request.setAttribute("_ERROR_MESSAGE_", message);
        }catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    /**
     * ɾ���¼�ϸ����Ϣ
     * 
     * @param request
     * @param response
     * @return �����Ƿ�ɹ�
     */
    public static String deleteEventSubCategoryByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Integer id = new Integer(request.getParameter("eventSubIndex"));
        String eventIndex = request.getParameter("eventIndex");
        try {
            BasicHelper.deleteEventSubCategoryByIndex(delegator, id);
            request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
            request.setAttribute("eventCategory", eventIndex);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }
    
    public static String queryEquipmentListByDept(HttpServletRequest request, HttpServletResponse response) {
    	GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
    	String equipmentDept = request.getParameter("equipmentDept");
    	if (StringUtils.isEmpty(equipmentDept)) {
    		GenericValue userInfo = AccountHelper.getUserInfo(request,delegator);
    		equipmentDept = userInfo.getString("accountDept");
    	}
    	request.setAttribute("equipmentDept", equipmentDept);
    	
    	try {
    		List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("maintDept", equipmentDept), UtilMisc.toList("equipmentId"));
    		request.setAttribute("equipmentList", equipmentList);
    	} catch(Exception e) {
    		request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
    	}
    	return "success";
    }

    /**
     * ��ѯ���Equipment��Ϣ
     * 
     * @param request
     * @param response
     * @return
     */
    public static String getEquipment(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String eqpId = request.getParameter("eqpid").toUpperCase();

        try {
        	GenericValue userInfo = AccountHelper.getUserInfo(request,delegator);
        	String maintDept = userInfo.getString("accountDept");
        	
            GenericValue equipment = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId", eqpId));
            if(equipment != null && CommonUtil.isNotEmpty(equipment.getString("maintDept"))) {
            	maintDept = equipment.getString("maintDept");
            }
            

            // �豸����
            List equipmentTypeList = CommonHelper.getEquipmentTypeList(delegator);
            // �豸����
            List equipmentDeptList = delegator.findAll("EquipmentDept");
            // �豸Location
            String locationSql = "select location from equipment_location t";
            List locationList = SQLProcess.excuteSQLQuery(locationSql, delegator);
            //User���ŵĿα�
            String sectionSql = "select section from equipment_section a,equipment_dept b where a.dept_index = b.dept_index and b.equipment_dept = '" + maintDept + "'";
            List sectionList = SQLProcess.excuteSQLQuery(sectionSql, delegator);
            
            List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("maintDept", maintDept));
            
            List meterList = delegator.findByAnd("EquipmentBasicData",UtilMisc.toMap("type", "meter"));
            List voltageList = delegator.findByAnd("EquipmentBasicData",UtilMisc.toMap("type", "voltage"));
            List exhaustList = delegator.findByAnd("EquipmentBasicData",UtilMisc.toMap("type", "exhaust"));
            List powerList = delegator.findByAnd("EquipmentBasicData",UtilMisc.toMap("type", "power"));

            // �豸����ʦ���豸BACKUP����ʦ���豸LEADER�����չ���ʦ��ѡ��Χ��huanghp,2008.10.31
            String engineerSql = "select account_no, account_name from account where account_dept='" + maintDept + "' order by account_no";
            List engineerList = SQLProcess.excuteSQLQuery(engineerSql, delegator);

            String processEngineerSql = "select account_no, account_name from account where account_section like  '%����%' order by account_no";
            List processEngineerList =  SQLProcess.excuteSQLQuery(processEngineerSql, delegator);
            // ���ý�request
            request.setAttribute("engineerList", engineerList);
            request.setAttribute("processEngineerList", processEngineerList);
            request.setAttribute("equipment", equipment);
            request.setAttribute("equipmentTypeList", equipmentTypeList);
            request.setAttribute("equipmentDeptList", equipmentDeptList);
            request.setAttribute("equipmentList", equipmentList);
            request.setAttribute("meterList", meterList);
            request.setAttribute("voltageList", voltageList);
            request.setAttribute("exhaustList", exhaustList);
            request.setAttribute("modelList", powerList);
            request.setAttribute("maintDept", maintDept);
            request.setAttribute("locationList", locationList);
            request.setAttribute("sectionList", sectionList);

        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }

        return "success";

    }

    /**
     * ɾ���豸��Ϣ
     * 
     * @param request
     * @param response
     * @return
     */
    public static String deleteEquipment(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String eqpId = request.getParameter("equipmentId");

        try {
            delegator.removeByAnd("Equipment", UtilMisc.toMap("equipmentId", eqpId));
            request.setAttribute("_EVENT_MESSAGE_", eqpId + " ��ɾ����");
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }

        return "success";

    }

    /**
     * �½��������豸��ϢequipmentDefineSave
     * 
     * @param request
     * @param response
     * @return
     */
    public static String saveEquipment(HttpServletRequest request, HttpServletResponse response) {
        // ���ҳ�������е�ֵ
    	GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Map param = BasicEvent.getInitParams(request, true, true);

        param.put("equipmentId", param.get("equipmentId").toString().replaceAll(" ", ""));
		param.put("transBy", CommonUtil.getUserNo(request));
		param.put("updateTime", new Timestamp(System.currentTimeMillis()));

		//System.out.println("equpmentId------->" + param.get("equipmentId"));

		try {
		    
		    //����ά�����Ų���deptIndex
            List deptList = delegator.findByAnd("EquipmentDept", UtilMisc.toMap("equipmentDept", param.get("maintDept")));
            if (deptList != null && !deptList.isEmpty()) {
                param.put("deptIndex", ((Map)(deptList.get(0))).get("deptIndex"));
            }
			// ����������ѯ���Ƿ��м�¼,û�����½�
			GenericValue equipment = delegator.makeValidValue("Equipment", param);
			// ����
			delegator.createOrStore(equipment);
			request.setAttribute("_EVENT_MESSAGE_", param.get("equipmentId")
					+ "����ɹ���");
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "success";
	}

    // --------------------------��̨����jiyw-------------------------------

    /**
	 * �豸����һ������:ȡ�ù��ڻ�̨���豸������Ϣ��������ʾ
	 * 
	 * @param request
	 *            equipmentId ��̨��Ϣ
	 * @param response
	 * @return String success/error
	 */
    public static String equipmentParamList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // java�д��ݵĲ���������ά����ǰ�豸��λ�����ϴ������ֵ
            String equipmentId = (String) request.getAttribute("equipmentId");

            // ҳ���ϴ��ݵĲ���
            String equipment_Id = (String) request.getParameter("equipment_Id");

            // �������ֵ���У��ϲ�
            if (!StringUtils.isEmpty(equipment_Id)) {
                equipmentId = equipment_Id;
            }

            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");

            GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));   
            
            // �鿴���豸�Ƿ����
            List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("equipmentId", equipmentId,"section",account.getString("accountSection")));

            if (equipmentList.size() != 0) {
                // ���ڣ�ȡ�ô��豸ȫ���Ļ�̨������Ϣ
                List unscheduleEqpParamList = delegator.findByAnd("UnscheduleEqpParam", UtilMisc.toMap("equipmentId", equipmentId));
                request.setAttribute("unscheduleEqpParamList", unscheduleEqpParamList);
                
                List promisStatusList = delegator.findAll("PromisEqpStatus");
                request.setAttribute("promisStatusList", promisStatusList);                
                request.setAttribute("equipmentId", equipmentId);

                // �жϲ���ҳ���Ƿ���ʾ��flag
                request.setAttribute("flag", "OK");
            } else {
                // �����ڣ���ʾMESSAGE
                request.setAttribute("_EVENT_MESSAGE_", "�ò��Ų����ڴ��豸");
            }
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * �豸�����½��򱣴�
     * 
     * @param request
     *            seqIndex��equipmentId��eqpStatus,maxValue,minValue,stdFlag(��̨��������)
     * @param response
     * @return String success/error
     */
    public static String manageEquipmentParam(HttpServletRequest request, HttpServletResponse response) {
        // �����ϴ��ݵ����в�����ϳ�Map
        Map paramMap = BasicEvent.getInitParams(request, true, true);
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        if (!StringUtils.isNumericSpace((String) paramMap.get("maxValue")) || !StringUtils.isNumericSpace((String) paramMap.get("minValue"))) {
        	request.setAttribute("_ERROR_MESSAGE_", "����ֵ����Ϊ����");
        	return "error";    	
        }    	
        
        try {
            // ȡ���û�
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");

            // ����Ϣд���豸������д����ʷ��
            BasicHelper.createEqpParam(delegator, paramMap, user);

            // ��ʾ��̨��Ϣһ����flag
            request.setAttribute("flag", "OK");
            request.setAttribute("equipmentId", paramMap.get("equipmentId"));
            request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");

        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }

    /**
     * ɾ���豸��Ϣ����������
     * 
     * @param request
     *            seqIndex��equipment_Id
     * @param response
     * @return String : success/error
     */
    public static String delEquipmentParam(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // ȡ��ҳ�����
        String seqIndex = request.getParameter("seqIndex");
        String equipment_id = request.getParameter("equipment_Id");
        try {
            // ȡ���û�
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");

            BasicHelper.delEqpParam(delegator, seqIndex, user);

            request.setAttribute("equipmentId", equipment_id);
            request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * �����ϵı༭��ť��ѯֵ
     * 
     * @param request
     *            seqIndex
     * @param response
     */
    public static void queryEquipmentParamByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // seqIndexȡ��
        String seqIndex = request.getParameter("seqIndex");

        try {
            // ȡ�û�̨������Ϣ
            GenericValue gv = delegator.findByPrimaryKey("UnscheduleEqpParam", UtilMisc.toMap("seqIndex", seqIndex));
            JSONObject unscheduleEquipemntParam = new JSONObject();
            unscheduleEquipemntParam.put("equipmentId", UtilFormatOut.checkNull(gv.getString("equipmentId")));
            unscheduleEquipemntParam.put("paramName", UtilFormatOut.checkNull(gv.getString("paramName")));
            unscheduleEquipemntParam.put("eqpStatus", UtilFormatOut.checkNull(gv.getString("eqpStatus")));
            unscheduleEquipemntParam.put("maxValue", UtilFormatOut.checkNull(gv.getString("maxValue")));
            unscheduleEquipemntParam.put("minValue", UtilFormatOut.checkNull(gv.getString("minValue")));
            unscheduleEquipemntParam.put("stdFlag", UtilFormatOut.checkNull(gv.getString("stdFlag")));
            unscheduleEquipemntParam.put("sort", UtilFormatOut.checkNull(gv.getString("sort")));

            // д��response
            response.getWriter().write(unscheduleEquipemntParam.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }
}
