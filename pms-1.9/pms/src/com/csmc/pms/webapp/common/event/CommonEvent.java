package com.csmc.pms.webapp.common.event;

import java.sql.Timestamp;
import java.util.ArrayList;
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

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.GeneralEvents;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.common.helper.CommonHelper;
import com.csmc.pms.webapp.qufollow.help.QuFollowHelper;
import com.csmc.pms.webapp.util.CommonUtil;

public class CommonEvent extends GeneralEvents{
	public static final String module = CommonEvent.class.getName();
	
	/**
     * 获得查询月份列表
     *
     * @param delegator
     * @return
     * @throws Exception
     */
    public static void getMonthsList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        StringBuffer sb = new StringBuffer();
        sb.append("select to_char(add_months(to_date('").append(Constants.MONTH_START)
                .append("', 'yyyy-mm'), rownum ), 'yyyy-mm') month");
        sb.append(" from dual connect by rownum <= months_between(trunc(sysdate, 'mm'), trunc(to_date('")
                .append(Constants.MONTH_START).append("', 'yyyy-mm'), 'mm'))");
        try {
            List monthsList = SQLProcess.excuteSQLQuery(sb.toString(), delegator);

            JSONArray monthJson = new JSONArray();
            JSONObject blank = new JSONObject(UtilMisc.toMap("monthsList", ""));
            monthJson.put(blank);
            for (Iterator it = monthsList.iterator(); it.hasNext();) {
                Map map = (Map) it.next();
                JSONObject object = new JSONObject();
                object.put("month", map.get("MONTH"));
                monthJson.put(object);
            }
            response.getWriter().write(monthJson.toString());
        } catch (Exception e) {
            Debug.logError(module, e.getMessage());
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
        }
    }
    
	/**
	 * 查询获得EquipmentType
	 * @param request
	 * @param response
	 */
	public static void queryEquipmentType(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		JSONArray eqpTypeJson = new JSONArray();
		try {
			JSONObject blank = new JSONObject(UtilMisc.toMap("equipmentType", ""));
			eqpTypeJson.put(blank);
			List eqpTypeList = CommonHelper.getEquipmentTypeList(delegator);
			for(Iterator it = eqpTypeList.iterator(); it.hasNext();) {
				 GenericValue gv = (GenericValue)it.next();
				 JSONObject object = new JSONObject();
				 object.put("equipmentType", gv.getString("equipmentType"));
				 eqpTypeJson.put(object);
			}
			response.getWriter().write(eqpTypeJson.toString());
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	 }
	 
	/**
	 * 查询获得巡检类型
	 * @param request
	 * @param response
	 */
	 public static void queryPcStyle(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		JSONArray pcStyleJson = new JSONArray();
		try {
			JSONObject blank = new JSONObject(UtilMisc.toMap("styleIndex", "", "name", ""));
			pcStyleJson.put(blank);
			List pcStyleList = delegator.findAllCache("PcStyle");
			for (Iterator it = pcStyleList.iterator(); it.hasNext();) {
				GenericValue gv = (GenericValue) it.next();
				JSONObject object = new JSONObject();
				object.put("styleIndex", gv.getString("styleIndex"));
				object.put("name", gv.getString("name"));
				pcStyleJson.put(object);
			}
			response.getWriter().write(pcStyleJson.toString());
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	 }
	 
	 /**
	  * 根据设备大类查询PM周期
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String queryPMPeriodByEqpType(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 String equipmentType = request.getParameter("equipmentType");
		 JSONArray periodJson = new JSONArray();
		 try {
			 JSONObject blank = new JSONObject(UtilMisc.toMap("periodIndex", "", "periodName", ""));
			 periodJson.put(blank);
			 //查询获得PM周期
			 List periodList = delegator.findByAnd("DefaultPeriod", 
					 UtilMisc.toMap("eqpType", equipmentType, 
							 "enabled", new Integer(Constants.ENABLE)), UtilMisc.toList("periodName"));
			for (Iterator it = periodList.iterator(); it.hasNext();) {
				GenericValue gv = (GenericValue) it.next();
				JSONObject object = new JSONObject();
				object.put("periodIndex", gv.getString("periodIndex"));
				object.put("periodName", gv.getString("periodName"));
				periodJson.put(object);
			}
			 response.getWriter().write(periodJson.toString());
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
		 }
		 
		 return null;
	 }

	 /**
	  * 根据巡检类型查询Pc周期
	  * @param request
	  * @param response
	  * @return
	  */
	 public static String queryPCPeriodByPcStyle(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 String styleIndex = request.getParameter("pcStyle");
		 JSONArray periodJson = new JSONArray();
		 try {
			 //查询获得PC周期
			 List periodList = delegator.findByAnd("PcPeriod", 
					 UtilMisc.toMap("styleIndex",styleIndex, 
							 "enabled", new Integer(Constants.ENABLE)),UtilMisc.toList("periodName"));
			 for(Iterator it = periodList.iterator(); it.hasNext();) {
				 GenericValue gv = (GenericValue)it.next();
				 JSONObject object = new JSONObject();
				 object.put("periodIndex", gv.getString("periodIndex"));
				 object.put("periodName", gv.getString("periodName"));
				 periodJson.put(object);
			 }
			 response.getWriter().write(periodJson.toString());
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
		 }
		 return null;
	 }	
	
	 /**
	  * 根据request中的参数条件，查询Equipment
	  * @param request{equipmentType, maintDept, section}
	  * @param response
	  */
	public static void queryEquipmentByAnd(HttpServletRequest request,
			HttpServletResponse response) {		

		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		JSONArray eqpJson = new JSONArray();
		
		String equipmentType = request.getParameter("equipmentType");
		String maintDept = request.getParameter("maintDept");
		String section = request.getParameter("section");
		String blank = request.getParameter("blank");
        try {
            Map map = new HashMap();

            if (StringUtils.isNotEmpty(equipmentType)) {
                map.put("equipmentType", equipmentType);
            }

            // 如果登录人为质量保证部(dept_index == 10010)，显示(MSA==”Y”)的设备
            if (AccountHelper.isMsaDept(request, delegator)) {
                map.put("msa", "Y");
            } else {
                if (StringUtils.isNotEmpty(maintDept)) {
                    map.put("maintDept", maintDept);
                }
            }

            if (StringUtils.isNotEmpty(section)) {
                map.put("section", section);
            }

            if ("1".equals(blank)) {
                JSONObject blankObject = new JSONObject(UtilMisc.toMap(
                        "equipmentId", ""));
                eqpJson.put(blankObject);
            }

            List equipmentList = delegator.findByAnd("Equipment", map,
                    UtilMisc.toList("equipmentId"));
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
      * 查询课别
      * @param request
      * @param response
      * @return
      */
     public static String querySection(HttpServletRequest request,
                HttpServletResponse response) {
         GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
         JSONArray sectionJson = new JSONArray();
         try {
             List sectionList = delegator.findAllCache("EquipmentSection");
             for(Iterator it = sectionList.iterator(); it.hasNext();) {
                 GenericValue gv = (GenericValue)it.next();
                 JSONObject object = new JSONObject();
                 object.put("sectionIndex", gv.getString("sectionIndex"));
                 object.put("section", gv.getString("section"));
                 sectionJson.put(object);
             }
             response.getWriter().write(sectionJson.toString());
         } catch(Exception e) {
             Debug.logError(e.getMessage(), module);
         }
         return null;
     }
     
     /**
      * 查询登陆人所属部门的课别列表
      * @param request
      * @param response
      * @return
      */
     public static String querySectionByDept(HttpServletRequest request,
                HttpServletResponse response) {
         GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
         String accountNo=CommonUtil.getUserNo(request);
         JSONArray sectionJson = new JSONArray();
         try {
        	 List useInfoList=QuFollowHelper.getAccountSection(delegator, accountNo);
          	 GenericValue userInfo=(GenericValue)useInfoList.get(0);
          	 //登陆人的部门
          	 String maintDept=userInfo.getString("accountDept");
          	 StringBuffer queryString=new StringBuffer();
          	 queryString.append("select t.section_index,t.section from equipment_section t ");
          	 queryString.append(" where t.dept_index=(select t2.dept_index from equipment_dept t2 where t2.equipment_dept='").append(maintDept).append("') order by t.section_index");
             List sectionList = SQLProcess.excuteSQLQuery(queryString.toString(), delegator);
             for(Iterator it = sectionList.iterator(); it.hasNext();) {
                 Map map = (Map)it.next();
                 JSONObject object = new JSONObject();
                 object.put("sectionIndex", map.get("SECTION_INDEX"));
                 object.put("section", map.get("SECTION"));
                 sectionJson.put(object);
             }
             response.getWriter().write(sectionJson.toString());
         } catch(Exception e) {
             Debug.logError(e.getMessage(), module);
         }
         return null;
     }
     
     /**
      * 根据课别查询设备
      * @param request
      * @param response
      * @return
      */
     public static String queryEquipmentBySection(HttpServletRequest request,
             HttpServletResponse response) {
      GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
      String section = request.getParameter("section");
      JSONArray eqpJson = new JSONArray();
      try {

     	 List ordrList=new ArrayList();
     	 ordrList.add("equipmentId");
          List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("section", section),ordrList);
          JSONObject blank = new JSONObject(UtilMisc.toMap("equipmentId", ""));
          eqpJson.put(blank);
          for(Iterator it = equipmentList.iterator(); it.hasNext();) {
              GenericValue gv = (GenericValue)it.next();
              JSONObject object = new JSONObject();
              object.put("equipmentId", gv.getString("equipmentId"));
              eqpJson.put(object);
          }
          response.getWriter().write(eqpJson.toString());
      } catch(Exception e) {
          Debug.logError(e.getMessage(), module);
      }
      return null;
  }
     
     /**
      * 根据工号获得所在课别的设备列表
      * @param request
      * @param response
      * @return
      */
     public static String querySectionEquipmentList(HttpServletRequest request,
             HttpServletResponse response) {
      GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
      JSONArray eqpJson = new JSONArray();
      try {
    	  GenericValue user = AccountHelper.getUserInfo(request, delegator);
    	  String section = user.getString("accountSection");
    	  JSONObject blank = new JSONObject(UtilMisc.toMap("equipmentId", ""));
    	  eqpJson.put(blank);
          List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("section", section));
          for(Iterator it = equipmentList.iterator(); it.hasNext();) {
              GenericValue gv = (GenericValue)it.next();
              JSONObject object = new JSONObject();
              object.put("equipmentId", gv.getString("equipmentId"));
              eqpJson.put(object);
          }
          response.getWriter().write(eqpJson.toString());
      } catch(Exception e) {
          Debug.logError(e.getMessage(), module);
      }
      return null;
  }
     
     /**
      * 查询设备部门
      * @param request
      * @param response
      * @return
      */
     public static void queryEquipmentDept(HttpServletRequest request,
                HttpServletResponse response) {
         GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
         JSONArray deptJson = new JSONArray();
         try {
        	 JSONObject blank = new JSONObject(UtilMisc.toMap("deptIndex", "", "equipmentDept", ""));
        	 deptJson.put(blank);
             List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
             for(Iterator it = deptList.iterator(); it.hasNext();) {
                 GenericValue gv = (GenericValue)it.next();
                 JSONObject object = new JSONObject();
                 object.put("deptIndex", gv.getString("deptIndex"));
                 object.put("equipmentDept", gv.getString("equipmentDept"));
                 deptJson.put(object);
             }
             response.getWriter().write(deptJson.toString());
         } catch(Exception e) {
             Debug.logError(e.getMessage(), module);
         }         
     }
     
     /**
      * 根据设备大类查询flow_action
      * @param request
      * @param response
      * @return
      */
     public static void queryFlowActionByEqpType(HttpServletRequest request,
                HttpServletResponse response) {
         GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
         String equipmentType = request.getParameter("equipmentType");
         
         JSONArray flowActionJson = new JSONArray();
         try {
             List flowActionList = delegator.findByAnd(
            		 "FlowAction", 
            		 UtilMisc.toMap("eventName", equipmentType, "frozen", "1", "enabled", "1", "eventType", "PM"), 
			         UtilMisc.toList("actionName"));
             for(Iterator it = flowActionList.iterator(); it.hasNext();) {
                 GenericValue gv = (GenericValue)it.next();
                 JSONObject object = new JSONObject();
                 object.put("actionIndex", gv.getString("actionIndex"));
                 object.put("actionName", gv.getString("actionName"));
                 flowActionJson.put(object);
             }
             response.getWriter().write(flowActionJson.toString());
         } catch(Exception e) {
             Debug.logError(e.getMessage(), module);
         }         
     }

     //
     public static String equiList(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 try {
			 String actionName = request.getParameter("actionName").toUpperCase().trim();
			 List list = new ArrayList();
			 if(!"".equals(actionName)) {
				 String sql = "SELECT equipment_id, equipment_desc,equipment_type,maint_dept FROM equipment  ";
				 if(CommonUtil.isNotEmpty(actionName)) {
					 sql += " WHERE equipment_id like '" + actionName + "%'";
				 }
				 list = SQLProcess.excuteSQLQuery(sql,delegator);
			 }
			 request.setAttribute("equiList", list);
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
			 request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			 return "error";
		 }
		 
		 return "success";
	 }
     
     /**
 	 * 获得系统时间
 	 * @param request
 	 * @param response
 	 */
 	public static void getSystemTimestamp(HttpServletRequest request,
 			HttpServletResponse response) {
 		JSONObject nowTime = new JSONObject();
 		try {
 			nowTime.put("time", CommonUtil.toGuiDate(new Timestamp(System.currentTimeMillis()),"yyyy-MM-dd HH:mm:ss"));
 			response.getWriter().write(nowTime.toString());
 		} catch (Exception e) {
 			Debug.logError(e.getMessage(), module);
 		}
 	}

	/**
	 * 根据vendor 查询model
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String queryModelByVendor(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String vendor = request.getParameter("vendor");
		JSONArray modelJson = new JSONArray();
		try {
			List modelList = delegator.findByAnd("EquipmentModel", UtilMisc.toMap("vendor", vendor));
			JSONObject blank = new JSONObject(UtilMisc.toMap("model", ""));
			modelJson.put(blank);
			for (Iterator it = modelList.iterator(); it.hasNext();) {
				GenericValue gv = (GenericValue) it.next();
				JSONObject object = new JSONObject();
				object.put("model", gv.getString("model"));
				modelJson.put(object);
			}
			response.getWriter().write(modelJson.toString());
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
		}
		return null;
	}

	/**
	 * 查询vendor
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String queryVendor(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		JSONArray modelJson = new JSONArray();
		String userNo = CommonUtil.getUserNo(request);
		String deptIndex = AccountHelper.getUserDeptIndex(delegator, userNo);
		try {
			String vendorSql = "select distinct vendor from equipment_model where maint_dept = '" + deptIndex + "'";
			vendorSql += " order by vendor";
			List vendorList = SQLProcess.excuteSQLQuery(vendorSql, delegator);

			JSONObject blank = new JSONObject(UtilMisc.toMap("model", ""));
			modelJson.put(blank);
			for (Iterator it = vendorList.iterator(); it.hasNext();) {
				Map map = (Map) it.next();
				String vendor = (String) map.get("VENDOR");
				JSONObject object = new JSONObject();
				object.put("vendor", vendor);
				modelJson.put(object);
			}
			response.getWriter().write(modelJson.toString());
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
		}
		return null;
	}
}
