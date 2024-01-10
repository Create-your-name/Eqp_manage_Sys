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

    // ---------------------------------------------巡检样式shaoaj--------------------------------------
    /**
     * 查询巡检样式
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
     * 根据主键查询巡检样式
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
     * 新增/更新巡检样式信息
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
     * 删除巡检样式信息
     * 
     * @param request
     * @param response
     * @return 操作是否成功
     */
    public static String deletePcStyleByPk(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Integer id = new Integer(request.getParameter("styleIndex"));
        try {
            BasicHelper.deletePcStyle(delegator, id);
            request.setAttribute("_EVENT_MESSAGE_", "删除成功！");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    // ---------------------------------------------超时/异常分类码设定 shaoaj--------------------------------------
    /**
     * 查询设备大类
     * 
     * @param request
     * @param response
     * @return 操作是否成功
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
     * 根据设备大类及异常分类获取信息列表,新增/更新/删除操作完成后都需要进入此方法，查询列表
     * 
     * @param request
     * @param response
     * @return 操作是否成功
     */
    public static String queryReasonOrOverTimeList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String equipmentType = request.getParameter("equipmentType");
        String reasonType = request.getParameter("reasonType");
        // 由上一步操作后，进入此方法得到request
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
            // 需要初始化页面中的设备大类及原因类型下拉
            request.setAttribute("flag", "ok");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }
    
    
    /**
     * 保养周期设置GUI检查步骤自动Hold
     * @param request
     * @param response
     * @return String success/error
     */
    public static String pmsReasonHold(HttpServletRequest request, HttpServletResponse response) {
		// 画面上传递的所有参数组合成Map
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
				eventMsg = "Hold设置成功!";
		    } else if ("0".equals(holdFlag)) {
		    	paramMap.put("holdCode", "");
				paramMap.put("holdReason", "");
				paramMap.put("holdLotNum", "");
				paramMap.put("immediateHold", "");
				paramMap.put("triggerStage", "");
				paramMap.put("holdDesc", "");
				eventMsg = "Hold设置已删除!";
		    }
			
			// 取得用户
		    GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		    String user = userLogin.getString("userLoginId");
		    paramMap.put("transBy", user);
		    paramMap.put("updateTime", UtilDateTime.nowTimestamp());
		    paramMap.put("reasonIndex", holdReasonIndex);
		    GenericValue holdGv = delegator.makeValidValue("PmsReason", paramMap);
		    delegator.store(holdGv);
		
		    // 显示设备保养周期一览的flag
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
     * 异常/超时分类码设定一览画面:取得关于异常/超时分类码设定信息画面上显示
     * @param request
     *            eqpType 设备大类
     * @param response
     * @return String success/error
     */
    public static String pmsReasonList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // java中传递的参数，用于维护当前设备大类保持上次输入的值
            String equipmentType = (String) request.getAttribute("equipType");
            String reasonType = request.getParameter("reasonType");

            // 页面上传递的参数
            String equipment_Type = UtilFormatOut.checkNull((String) request.getParameter("equipmentType"));
            String holdReasonIndex = UtilFormatOut.checkNull((String) request.getParameter("holdReasonIndex"));
            String attrReason = (String) request.getAttribute("reasonType");
             

            // 如果两个值都有，合并取页面参数
            if (!StringUtils.isEmpty(equipment_Type)) {
            	equipmentType = equipment_Type;
            }
       
            
            if (!StringUtils.isEmpty(attrReason)) {
            	reasonType = attrReason;
            }
            
            // 查看此设备大类是否存在
            List eqpList = CommonHelper.getEquipmentTypeList(delegator);      
            List promisEqpStatusList = delegator.findByAnd("PromisEqpStatus", UtilMisc.toMap("type", Constants.PM), UtilMisc.toList("eqpStatus"));
            List timeRangeList = delegator.findAllCache("TimeRange");

            List pmsReasonList = null;
            if (StringUtils.isNotEmpty(holdReasonIndex)) {
            	pmsReasonList = delegator.findByAnd("PmsReason", UtilMisc.toMap("equipmentType", equipmentType, "reasonIndex", holdReasonIndex,"reasonType",reasonType), UtilMisc.toList("reason"));
            	
            	//fab1获得hold码，hold原因 
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

            // 判断参数页面是否显示的flag
            request.setAttribute("flag", "ok");
            
            //如果登录人为质量保证部(dept_index == 10010)，则周期命名必须以MSA开头
          //  request.setAttribute("isMsaDept", AccountHelper.isMsaDept(request, delegator) ? "true" : "false");            
            
        } catch (Exception e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
    

    /**
     * 根据主键查询异常信息
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
     * 保存/更新(超时/异常)分类码信息
     * 
     * @param request
     * @param response
     * @return 操作是否成功
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
     * 根据reasonIndex删除信息reason
     * 
     * @param request
     * @param response
     * @return 操作是否成功
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
            request.setAttribute("_EVENT_MESSAGE_", "删除成功！");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    // ---------------------------------------------不定期保养参数设定 shaoaj--------------------------------------
    /**
     * 查询不定期保养参数设定
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
     * 新增/更新不定期保养参数
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
            request.setAttribute("_EVENT_MESSAGE_", "保存成功！");
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
     * 根据主键查询不定期保养参数信息
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
     * 删除不定期保养参数信息
     * 
     * @param request
     * @param response
     * @return 操作是否成功
     */
    public static String deleteUnscheduleParameterByPk(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Integer id = new Integer(request.getParameter("paramIndex"));
        try {
            BasicHelper.deleteUnscheduleParameter(delegator, id);
            request.setAttribute("_EVENT_MESSAGE_", "删除成功！");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    // ---------------------------------------------清洗备件厂商 shaoaj--------------------------------------
    /**
     * 查询清洗备件厂商列表
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
     * 新增/更新清洗备件厂商
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
            request.setAttribute("_EVENT_MESSAGE_", "保存成功！");
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
     * 根据主键查询清洗备件厂商信息
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
     * 删除清洗备件厂商信息
     * 
     * @param request
     * @param response
     * @return 操作是否成功
     */
    public static String deleteEquipmentVendorByPk(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Integer id = new Integer(request.getParameter("seqIndex"));
        try {
            BasicHelper.deleteEquipmentVendorByPk(delegator, id);
            request.setAttribute("_EVENT_MESSAGE_", "删除成功！");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    // ---------------------------------------------设备型号维护 shaoaj--------------------------------------
    /**
     * 查询设备型号列表
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
     * 新增/更新设备型号
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
            request.setAttribute("_EVENT_MESSAGE_", "保存成功！");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    /**
     * 根据主键查询设备型号信息
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
     * 删除设备型号信息
     * 
     * @param request
     * @param response
     * @return 操作是否成功
     */
    public static String deleteEquipmentBasicByPk(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Integer id = new Integer(request.getParameter("seqIndex"));
        try {
            BasicHelper.deleteEquipmentBasicByPk(delegator, id);
            request.setAttribute("_EVENT_MESSAGE_", "删除成功！");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    // ---------------------------------------------事件分类 shaoaj--------------------------------------
    /**
     * 查询事件分类
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
     * 根据主键查询事件分类
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
     * 新增/更新事件分类
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
            request.setAttribute("_EVENT_MESSAGE_", "保存成功！");
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
     * 删除事件分类信息
     * 
     * @param request
     * @param response
     * @return 操作是否成功
     */
    public static String deleteEventCategoryByPk(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Integer id = new Integer(request.getParameter("eventIndex"));
        try {
            BasicHelper.deleteEventCategoryByIndex(delegator, id);
            request.setAttribute("_EVENT_MESSAGE_", "删除成功！");
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            Debug.logError(e, module);
            return "error";
        }
        return "success";
    }

    // ---------------------------------------------事件细项 shaoaj--------------------------------------
    /**
     * 查询事件细项
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
            // 需要初始化页面中的设备大类及原因类型下拉
            request.setAttribute("flag", "ok");

        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * 根据主键查询事件细项
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
     * 新增/更新事件细项
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
            request.setAttribute("_EVENT_MESSAGE_", "保存成功！");
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
     * 删除事件细项信息
     * 
     * @param request
     * @param response
     * @return 操作是否成功
     */
    public static String deleteEventSubCategoryByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Integer id = new Integer(request.getParameter("eventSubIndex"));
        String eventIndex = request.getParameter("eventIndex");
        try {
            BasicHelper.deleteEventSubCategoryByIndex(delegator, id);
            request.setAttribute("_EVENT_MESSAGE_", "删除成功！");
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
     * 查询获得Equipment信息
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
            

            // 设备大类
            List equipmentTypeList = CommonHelper.getEquipmentTypeList(delegator);
            // 设备部门
            List equipmentDeptList = delegator.findAll("EquipmentDept");
            // 设备Location
            String locationSql = "select location from equipment_location t";
            List locationList = SQLProcess.excuteSQLQuery(locationSql, delegator);
            //User部门的课别
            String sectionSql = "select section from equipment_section a,equipment_dept b where a.dept_index = b.dept_index and b.equipment_dept = '" + maintDept + "'";
            List sectionList = SQLProcess.excuteSQLQuery(sectionSql, delegator);
            
            List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("maintDept", maintDept));
            
            List meterList = delegator.findByAnd("EquipmentBasicData",UtilMisc.toMap("type", "meter"));
            List voltageList = delegator.findByAnd("EquipmentBasicData",UtilMisc.toMap("type", "voltage"));
            List exhaustList = delegator.findByAnd("EquipmentBasicData",UtilMisc.toMap("type", "exhaust"));
            List powerList = delegator.findByAnd("EquipmentBasicData",UtilMisc.toMap("type", "power"));

            // 设备工程师，设备BACKUP工程师，设备LEADER，工艺工程师的选择范围，huanghp,2008.10.31
            String engineerSql = "select account_no, account_name from account where account_dept='" + maintDept + "' order by account_no";
            List engineerList = SQLProcess.excuteSQLQuery(engineerSql, delegator);

            String processEngineerSql = "select account_no, account_name from account where account_section like  '%工艺%' order by account_no";
            List processEngineerList =  SQLProcess.excuteSQLQuery(processEngineerSql, delegator);
            // 设置进request
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
     * 删除设备信息
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
            request.setAttribute("_EVENT_MESSAGE_", eqpId + " 已删除！");
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }

        return "success";

    }

    /**
     * 新建、保存设备信息equipmentDefineSave
     * 
     * @param request
     * @param response
     * @return
     */
    public static String saveEquipment(HttpServletRequest request, HttpServletResponse response) {
        // 获得页面上所有的值
    	GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        Map param = BasicEvent.getInitParams(request, true, true);

        param.put("equipmentId", param.get("equipmentId").toString().replaceAll(" ", ""));
		param.put("transBy", CommonUtil.getUserNo(request));
		param.put("updateTime", new Timestamp(System.currentTimeMillis()));

		//System.out.println("equpmentId------->" + param.get("equipmentId"));

		try {
		    
		    //根据维护部门插入deptIndex
            List deptList = delegator.findByAnd("EquipmentDept", UtilMisc.toMap("equipmentDept", param.get("maintDept")));
            if (deptList != null && !deptList.isEmpty()) {
                param.put("deptIndex", ((Map)(deptList.get(0))).get("deptIndex"));
            }
			// 根据主键查询表是否有纪录,没有则新建
			GenericValue equipment = delegator.makeValidValue("Equipment", param);
			// 保存
			delegator.createOrStore(equipment);
			request.setAttribute("_EVENT_MESSAGE_", param.get("equipmentId")
					+ "保存成功！");
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "success";
	}

    // --------------------------机台参数jiyw-------------------------------

    /**
	 * 设备参数一览画面:取得关于机台的设备参数信息画面上显示
	 * 
	 * @param request
	 *            equipmentId 机台信息
	 * @param response
	 * @return String success/error
	 */
    public static String equipmentParamList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // java中传递的参数，用于维护当前设备栏位保持上次输入的值
            String equipmentId = (String) request.getAttribute("equipmentId");

            // 页面上传递的参数
            String equipment_Id = (String) request.getParameter("equipment_Id");

            // 如果两个值都有，合并
            if (!StringUtils.isEmpty(equipment_Id)) {
                equipmentId = equipment_Id;
            }

            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");

            GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));   
            
            // 查看此设备是否存在
            List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("equipmentId", equipmentId,"section",account.getString("accountSection")));

            if (equipmentList.size() != 0) {
                // 存在，取得此设备全部的机台参数信息
                List unscheduleEqpParamList = delegator.findByAnd("UnscheduleEqpParam", UtilMisc.toMap("equipmentId", equipmentId));
                request.setAttribute("unscheduleEqpParamList", unscheduleEqpParamList);
                
                List promisStatusList = delegator.findAll("PromisEqpStatus");
                request.setAttribute("promisStatusList", promisStatusList);                
                request.setAttribute("equipmentId", equipmentId);

                // 判断参数页面是否显示的flag
                request.setAttribute("flag", "OK");
            } else {
                // 不存在，显示MESSAGE
                request.setAttribute("_EVENT_MESSAGE_", "该部门不存在此设备");
            }
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * 设备参数新建或保存
     * 
     * @param request
     *            seqIndex，equipmentId，eqpStatus,maxValue,minValue,stdFlag(机台参数资料)
     * @param response
     * @return String success/error
     */
    public static String manageEquipmentParam(HttpServletRequest request, HttpServletResponse response) {
        // 画面上传递的所有参数组合成Map
        Map paramMap = BasicEvent.getInitParams(request, true, true);
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        if (!StringUtils.isNumericSpace((String) paramMap.get("maxValue")) || !StringUtils.isNumericSpace((String) paramMap.get("minValue"))) {
        	request.setAttribute("_ERROR_MESSAGE_", "输入值必须为数字");
        	return "error";    	
        }    	
        
        try {
            // 取得用户
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");

            // 将信息写入设备参数表，写入历史表
            BasicHelper.createEqpParam(delegator, paramMap, user);

            // 显示机台信息一览的flag
            request.setAttribute("flag", "OK");
            request.setAttribute("equipmentId", paramMap.get("equipmentId"));
            request.setAttribute("_EVENT_MESSAGE_", "保存成功！");

        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        
        return "success";
    }

    /**
     * 删除设备信息，根据主键
     * 
     * @param request
     *            seqIndex，equipment_Id
     * @param response
     * @return String : success/error
     */
    public static String delEquipmentParam(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // 取得页面参数
        String seqIndex = request.getParameter("seqIndex");
        String equipment_id = request.getParameter("equipment_Id");
        try {
            // 取得用户
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");

            BasicHelper.delEqpParam(delegator, seqIndex, user);

            request.setAttribute("equipmentId", equipment_id);
            request.setAttribute("_EVENT_MESSAGE_", "删除成功！");
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * 画面上的编辑按钮查询值
     * 
     * @param request
     *            seqIndex
     * @param response
     */
    public static void queryEquipmentParamByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // seqIndex取得
        String seqIndex = request.getParameter("seqIndex");

        try {
            // 取得机台参数信息
            GenericValue gv = delegator.findByPrimaryKey("UnscheduleEqpParam", UtilMisc.toMap("seqIndex", seqIndex));
            JSONObject unscheduleEquipemntParam = new JSONObject();
            unscheduleEquipemntParam.put("equipmentId", UtilFormatOut.checkNull(gv.getString("equipmentId")));
            unscheduleEquipemntParam.put("paramName", UtilFormatOut.checkNull(gv.getString("paramName")));
            unscheduleEquipemntParam.put("eqpStatus", UtilFormatOut.checkNull(gv.getString("eqpStatus")));
            unscheduleEquipemntParam.put("maxValue", UtilFormatOut.checkNull(gv.getString("maxValue")));
            unscheduleEquipemntParam.put("minValue", UtilFormatOut.checkNull(gv.getString("minValue")));
            unscheduleEquipemntParam.put("stdFlag", UtilFormatOut.checkNull(gv.getString("stdFlag")));
            unscheduleEquipemntParam.put("sort", UtilFormatOut.checkNull(gv.getString("sort")));

            // 写入response
            response.getWriter().write(unscheduleEquipemntParam.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }
}
