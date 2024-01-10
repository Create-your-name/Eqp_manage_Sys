package com.csmc.pms.webapp.report.event;

import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.parts.help.PartsHelper;
import com.csmc.pms.webapp.util.GeneralEvents;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.common.helper.CommonHelper;
import com.csmc.pms.webapp.qufollow.help.QuFollowHelper;
import com.csmc.pms.webapp.report.help.ReportHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;

public class ReportEvent extends GeneralEvents {
	public static final String module = ReportEvent.class.getName();

    /**
     * 机台基本资料报表查询
     *
     * @param request
     * @param response
     * @return action
     */
    public static String queryEqpBasicInfoEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
        	request.setAttribute("eqpDeptList", delegator.findAllCache("EquipmentDept"));
        	request.setAttribute("eqpSectionList", delegator.findAllCache("EquipmentSection"));
        	request.setAttribute("eqpLocationList", delegator.findAllCache("EquipmentLocation"));
            request.setAttribute("eqpTypeList", CommonHelper.getEquipmentTypeList(delegator));

        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

	/**
	 * 根据条件查询机台基本资料
	 * @param request
	 * @param response
	 */
	public static String queryEqpBasicInfo(HttpServletRequest request, HttpServletResponse response) {

		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String eqpId = request.getParameter("eqpId").trim().toUpperCase();
		String ownDept = request.getParameter("ownDept");
		String maintDept = request.getParameter("maintDept");
		String location = request.getParameter("location");
		String section = request.getParameter("section");
		String keyEqp = request.getParameter("keyEqp");
		String adjustEqp = request.getParameter("adjustEqp");
		String measureEqp = request.getParameter("measureEqp");
		String equipmentType = request.getParameter("equipmentType");

		try {
			List equipmentList = ReportHelper.queryEqpBasicInfo(delegator,
					eqpId, ownDept, maintDept, location, section, keyEqp,
					adjustEqp, measureEqp, equipmentType);
			request.setAttribute("equipmentList", equipmentList);

			if (equipmentList != null && equipmentList.size() == 0) {
				request.setAttribute("_ERROR_MESSAGE_", "没有符合条件的设备，请减少查询条件");
				return "error";
			}

		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "success";
	}

	//------------------------------------机台用料查询---------------------------------------
	/**
	 * 进入机台用料查询页面
	 * 
	 * @param request
	 * @param response
	 */
	public static String intoCleanRecordReport(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try {
			List eqpList = CommonHelper.getEquipmentTypeList(delegator);
			String userNo = CommonUtil.getUserNo(request);
			String maintDept = AccountHelper.getUserDeptIndex(delegator, userNo);

			List equipMoelList = delegator.findByAnd("EquipmentModel", UtilMisc.toMap("maintDept", maintDept),
					UtilMisc.toList("Model"));
			List equipDeptList = delegator.findAll("EquipmentDept");

			request.setAttribute("equipDeptList", equipDeptList);
			request.setAttribute("equipMentList", eqpList);
			request.setAttribute("equipMoelList", equipMoelList);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}

	/**
	 * 通过设备大类获得eqpId,保养种类
	 * @param request
	 * @param response
	 */
	public static void getEqpipAndPeriodIDList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String equipmentType=request.getParameter("equipmentType");
		JSONObject jsObject=new JSONObject();
		JSONArray eqpArray=new JSONArray();
		JSONArray periodNameArray=new JSONArray();
		JSONArray periodValueArray=new JSONArray();
		JSONArray tsNameArray=new JSONArray();
		JSONArray tsValueArray=new JSONArray();
		try {
			//得到eqpId列表
			List eqpList = ReportHelper.getEquipIDList(delegator, equipmentType);
			for (int i=0;i<eqpList.size();i++){
				GenericValue gv=(GenericValue)eqpList.get(i);
				eqpArray.put(gv.getString("equipmentId"));
			}
			jsObject.put("eqpIdArray", eqpArray);

			//保养种类列表
			List prodList = ReportHelper.getPeriodList(delegator, equipmentType);
			for (int i=0;i<prodList.size();i++){
				GenericValue gv=(GenericValue)prodList.get(i);
				periodValueArray.put(gv.getString("periodIndex"));
				periodNameArray.put(gv.getString("periodName"));
			}
			jsObject.put("priodNameArray", periodNameArray);
			jsObject.put("priodValueArray", periodValueArray);

			//设备原因分类列表
			List tsTypeList=ReportHelper.getTsTypeList(delegator, equipmentType);
			for (int i=0;i<tsTypeList.size();i++){
				GenericValue gv=(GenericValue)tsTypeList.get(i);
				tsValueArray.put(gv.getString("reasonIndex"));
				tsNameArray.put(gv.getString("reason"));
			}

			jsObject.put("tsNameArray", tsNameArray);
			jsObject.put("tsValueArray", tsValueArray);

			//返回jsobject
			response.getWriter().write(jsObject.toString());
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}


	/**
	 * 根据查询条件进行机台用料查询
	 * @param request
	 * @param response
	 */
	public static String queryCleanRecordByCondition(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String startDate=request.getParameter("startDate");
		String endDate=request.getParameter("endDate");
		String equipmentId=request.getParameter("equipmentId");
		String equipmentType=request.getParameter("equipmentType");
		String tsType=request.getParameter("tsType");
		String pmType=request.getParameter("period");
		Map map=new HashMap();
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("equipmentId", equipmentId);
		map.put("equipmentType", equipmentType);
		map.put("tsType", tsType);
		map.put("pmType", pmType);
		try {
			//根据条件查询数据
			List eqpList = ReportHelper.queryCleanRecordByCondition(delegator,map);
			//得到设备大类
			List equipMentList = CommonHelper.getEquipmentTypeList(delegator);
			request.setAttribute("PART_LIST", eqpList);
			request.setAttribute("equipMentList", equipMentList);
			//如果值为NULL说明，前台页面没有选择此查询项
			if(tsType==null){
				request.setAttribute("TS_TYP", "0");
			}else{
				request.setAttribute("TS_TYP", tsType);
			}
			if(pmType==null){
				request.setAttribute("PM_TYP", "0");
			}else{
				request.setAttribute("PM_TYP", pmType);
			}
			request.setAttribute("flag", "true");
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
//	------------------------------------机台用料查询end---------------------------------------
	/**
     * 查询Available/UpTime Chart
     *
     * @param request
     * @param response
     * @return action
     */
    public static String queryEqpAvailUpTimeEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        try {
        	request.setAttribute("eqpTypeList", CommonHelper.getEquipmentTypeList(delegator));

        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
	 * 通过设备大类获得eqpId Ajax jsObject
	 * @param request
	 * @param response
	 */
	public static void getEqpIdByType(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String equipmentType = request.getParameter("equipmentType");
		JSONObject jsObject = new JSONObject();
		JSONArray eqpArray = new JSONArray();
		try {
			//得到eqpId列表
			List eqpList = ReportHelper.getEquipIDList(delegator, equipmentType);
			for (int i=0;i<eqpList.size();i++){
				GenericValue gv = (GenericValue)eqpList.get(i);
				eqpArray.put(gv.getString("equipmentId"));
			}
			jsObject.put("eqpIdArray", eqpArray);

			//返回jsobject
			response.getWriter().write(jsObject.toString());
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}

	//------------------------------------备件领料和使用差异查询---------------------------------------

	/**
	 * 进入机台用料查询页面
	 * @param request
	 * @param response
	 */
	public static String intoDrawMateDifferentiaReport(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try {
			List eqpList = ReportHelper.getDeptList(delegator);
			request.setAttribute("DEP_LIST", eqpList);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}

	/**
	 * 根据查询条件进行机台用料查询
	 * @param request
	 * @param response
	 */
	public static String queryPartUseDiffList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String date=request.getParameter("date");
		String deptMent=request.getParameter("deptMent");
		String partNo=request.getParameter("partNo");
		Map parMap=new HashMap();
		parMap.put("date", date);
		parMap.put("deptMent", deptMent);
		parMap.put("partNo", partNo);
		try {
			//根据条件查询数据
			List partList = ReportHelper.queryPartUseDiffByCondition(delegator,parMap,"0");
			List eqpList = ReportHelper.getDeptList(delegator);
			request.setAttribute("PART_LIST", partList);
			request.setAttribute("DEP_LIST", eqpList);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}


	/**
	 * 查看物料使用详细信息
	 * @param request
	 * @param response
	 */
	public static String queryPartInfoList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String date=request.getParameter("date");
		String deptMent=request.getParameter("deptMent");
		String partNo=request.getParameter("partNo");
		Map parMap = new HashMap();
		parMap.put("date", date);
		parMap.put("deptMent", deptMent);
		parMap.put("partNo", partNo);
		try {
			//根据条件查询数据
			List partList = ReportHelper.queryPartUseDiffByCondition(delegator,parMap,"1");
			request.setAttribute("PART_LIST", partList);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}

	/**
	 * applet 根据条件查询EqpAvailUpTime,ObjectOutputStream传回list
	 * @param request
	 * @param response
	 */
	public static void getEqpAvailUpTimeList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

		String period = request.getParameter("Period");				//value is DAY,WEEK,MONTH
		String startDate = request.getParameter("StartDate");		//value as 2007-06-15
    	String endDate = request.getParameter("EndDate");			//value as 2007-08-15
    	String eqpId = request.getParameter("EqpId");				//value as 'MD21221','MD21321','MD21351'

    	List dataList = null;
		try {
			if ("DAY".equals(period)) {
				dataList = ReportHelper.getEqpAvailUpTimeDayList(delegator, startDate, endDate, eqpId);
			} else if ("WEEK".equals(period)) {
				dataList = ReportHelper.getEqpAvailUpTimeWeekList(delegator, startDate, endDate, eqpId);
			}else if ("MONTH".equals(period)) {
				dataList = ReportHelper.getEqpAvailUpTimeMonthList(delegator, startDate, endDate, eqpId);
			}

			response.setContentType(Constants.CONTENT_TYPE);
			ObjectOutputStream os = new ObjectOutputStream(response.getOutputStream());
			os.writeObject(dataList);
			os.close();
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}

	/**
	 * 进入PM预测报表页面
	 * @param request
	 * @param response
	 */
	public static String pmForecastPlanEntry(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try {
			List eqpList = CommonHelper.getEquipmentTypeList(delegator);
			request.setAttribute("equipMentList", eqpList);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}

	/**
	 * 设备查询显示PM预测报表
	 * @param request
	 * @param response
	 */
	public static String queryPmForecastPlan(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String sDate = request.getParameter("startDate");
		String equipmentType = request.getParameter("equipmentType");
		String equipmentId = request.getParameter("equipmentId");
		String pmType = request.getParameter("period");
		String keyEqp = request.getParameter("keyEqp");
		String adjustEqp = request.getParameter("adjustEqp");
		String measureEqp = request.getParameter("measureEqp");

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			Date startDate;
			startDate = dateFormat.parse(sDate);

			Calendar cStartDate = new GregorianCalendar();
			cStartDate.setTime(startDate);

			Calendar cEndDate = new GregorianCalendar();
			cEndDate.setTime(startDate);
		    cEndDate.add(Calendar.MONTH, 3);

		    List monthList = ReportHelper.getMonthList(cStartDate, cEndDate);

			request.setAttribute("monthList", monthList);

			// 根据条件查询数据
			List pmScheduleList = ReportHelper.queryPmSchedule(delegator, sDate, dateFormat.format(cEndDate.getTime()), equipmentType, equipmentId, pmType, keyEqp, adjustEqp, measureEqp);
			// 得到设备大类
			List equipMentList = CommonHelper.getEquipmentTypeList(delegator);

			request.setAttribute("pmScheduleList", pmScheduleList);
			request.setAttribute("equipMentList", equipMentList);

			request.setAttribute("flag", "true");
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}

	/**
	 * 根据条件显示PM预测报表
	 * @param request
	 * @param response
	 */
	public static String queryPmForecastPlanByPeriod(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String sDate = request.getParameter("startDate");
		String eDate = request.getParameter("endDate");
		String equipmentType = request.getParameter("equipmentType");
		String equipmentId = request.getParameter("equipmentId");
		String pmType = request.getParameter("period");
		String keyEqp = request.getParameter("keyEqp");
		String adjustEqp = request.getParameter("adjustEqp");
		String measureEqp = request.getParameter("measureEqp");

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			Date startDate;
			startDate = dateFormat.parse(sDate);
			Date endDate = dateFormat.parse(eDate);

			Calendar cStartDate = new GregorianCalendar();
			cStartDate.setTime(startDate);

			Calendar cEndDate = new GregorianCalendar();
			cEndDate.setTime(endDate);

			// 开始日期与结束日期的跨度不能超过6个月
			cStartDate.add(Calendar.MONTH, 6);
			if (cEndDate.after(cStartDate))
			{
				request.setAttribute("_ERROR_MESSAGE_", "开始日期与结束日期的跨度不能超过6个月");
				return "error";
			}
			cStartDate.add(Calendar.MONTH, -6);

		    List monthList = ReportHelper.getMonthListByPeriod(cStartDate, cEndDate);

			request.setAttribute("monthList", monthList);

			// 根据条件查询数据
			List pmScheduleList = ReportHelper.queryPmScheduleByPeriod(delegator, sDate, eDate, equipmentType, equipmentId, pmType, keyEqp, adjustEqp, measureEqp);
			// 得到设备大类
			List equipMentList = CommonHelper.getEquipmentTypeList(delegator);

			request.setAttribute("pmScheduleList", pmScheduleList);
			request.setAttribute("equipMentList", equipMentList);

			request.setAttribute("flag", "true");
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}

	/**
	 * 查看机台参数表
	 * @param request
	 * @param response
	 */
	public static String queryEqpParamHist(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String equipmentType = request.getParameter("equipmentType");
		String eqpId = request.getParameter("eqpId");
		String paramName = request.getParameter("paramName");

		Map parMap = new HashMap();
		parMap.put("startDate", startDate);
		parMap.put("endDate", endDate);
		parMap.put("equipmentType", equipmentType);
		parMap.put("eqpId", eqpId);
		parMap.put("paramName", paramName);

		try {
			//根据条件查询数据
			List eqpParamList = ReportHelper.queryUnscheduleEqpParamHist(delegator,parMap);
			request.setAttribute("eqpParamList", eqpParamList);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}

	/**
	 * 根据设备查询获得设备参数
	 * @param request
	 * @param response
	 */
	 public static void queryParamNameByEqpId(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		JSONArray eqpJson = new JSONArray();
		String eqpId = request.getParameter("eqpId");

		try {
			List equipmentList = delegator.findByAnd("UnscheduleEqpParam", UtilMisc
					.toMap("equipmentId", eqpId), UtilMisc
					.toList("paramName"));
			for (Iterator it = equipmentList.iterator(); it.hasNext();) {
				GenericValue gv = (GenericValue) it.next();
				JSONObject object = new JSONObject();
				object.put("paramName", gv.getString("paramName"));
				//object.put("eqpStatus", gv.getString("eqpStatus"));
				eqpJson.put(object);
			}
			response.getWriter().write(eqpJson.toString());
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}

	/**
	 * applet EqpParamChart
	 *
	 * @param request
	 * @param response
	 * @return list
	 */
	public static void getEqpParamPoints(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		//InitParams: startDate,endDate,equipmentType,eqpId,paramName
		Map filterMap = GeneralEvents.getInitParams(request, false);

		List dataList = null;
		try {
			dataList = ReportHelper.queryUnscheduleEqpParamHist(delegator, filterMap);

			response.setContentType(Constants.CONTENT_TYPE);
			ObjectOutputStream os = new ObjectOutputStream(response.getOutputStream());
			os.writeObject(dataList);
			os.close();
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}

	/**
	 * 根据设备大类查询FlowActionItem
	 *
	 * @param request
	 * @param response
	 * @return itemJSONArray(object{itemIndex,itemName})
	 */
	 public static void queryFlowActionItemByEqpType(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String equipmentType = request.getParameter("equipmentType");

		JSONArray flowActionItemJson = new JSONArray();
		try {
			List flowActionItemList = ReportHelper.getFlowActionItemList(delegator,equipmentType,null);

			for (Iterator it = flowActionItemList.iterator(); it.hasNext();) {
				Map map = (Map)it.next();
				JSONObject object = new JSONObject();
				object.put("itemIndex", map.get("ITEM_INDEX"));
				object.put("itemName", map.get("ITEM_NAME"));
				flowActionItemJson.put(object);
			}
			response.getWriter().write(flowActionItemJson.toString());
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
		}
	}

	/**
	 * 根据设备大类,周期 查询FlowActionItem，刷选掉没有数据的item
	 * @author huanghp
	 * @param request
	 * @param response
	 * @return itemJSONArray(object{itemIndex,itemName})
	 */
	 public static void queryFlowActionItemByEqpTypeAndPeriod(HttpServletRequest request, HttpServletResponse response) {
		 try {
			GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
			String equipmentType = request.getParameter("equipmentType");
			String periodIndex = request.getParameter("periodIndex");
			String sDate = request.getParameter("sDate");
			String eDate = request.getParameter("eDate");
			String isMsa = request.getParameter("isMsa");
			// 根据设备大类取到所有设备
			String sql = "  select distinct equipment_id from equipment where equipment_type = '"+equipmentType+"'";
			List eqpList = SQLProcess.excuteSQLQuery(sql, delegator);
			StringBuffer eqpStr = new StringBuffer();
			eqpStr.append("(");
            for(Iterator it = eqpList.iterator(); it.hasNext();)
            {
                Map map = (Map)it.next();
                eqpStr.append("'");
                eqpStr.append(map.get("EQUIPMENT_ID"));
                eqpStr.append("',");
            }
            if (eqpList.size() > 0)
            {
            	eqpStr.deleteCharAt(eqpStr.length() - 1);
            }
            eqpStr.append(") ");
			sql = "select t1.equipment_id,t1.period_index,t2.item_index,t2.update_time,t2.item_value,t2.item_upper_spec,t2.item_lower_spec,t2.item_note "+
						 "from pm_form t1, flow_item_points t2 "+
						 "where t1.pm_index = t2.form_index and t2.form_type = 'PM' "+
						 "and t2.update_time >= to_date('"+sDate+"', 'yyyy-mm-dd') and t2.update_time < to_date('"+eDate+"', 'yyyy-mm-dd') + 1 "+
						 "and t1.equipment_id in "+eqpStr.toString()+
						 "and t1.period_index = '"+periodIndex+"' ";
			//MSA设备保养查询,项目包括数字型和文字型
			if (StringUtils.isEmpty(isMsa)) {
	            sql += "and t2.item_type=2 ";
	        } 
            sql += "and t2.item_index='";
	        
			JSONArray flowActionItemJson = new JSONArray();
			List flowActionItemJsonList = new ArrayList();
			List flowActionItemList = ReportHelper.getFlowActionItemList(delegator,equipmentType,isMsa);
			
			StringBuffer itemIndexAll = new StringBuffer();
			for (Iterator it = flowActionItemList.iterator(); it.hasNext();)
			{
				Map map = (Map)it.next();
				// 先刷选一下有没有数据
				String querySql = sql + map.get("ITEM_INDEX") + "'";
				List itemList = SQLProcess.excuteSQLQuery(querySql, delegator);
				if (itemList.size() > 0)
				{
					JSONObject object = new JSONObject();
					object.put("itemIndex", map.get("ITEM_INDEX"));
					object.put("itemName", map.get("ITEM_NAME"));
					itemIndexAll.append(map.get("ITEM_INDEX")).append(",");
//						flowActionItemJson.put(object);
					flowActionItemJsonList.add(object);
				}
			}
			if (StringUtils.isNotEmpty(isMsa) && isMsa.equals("1")) {
			    if(itemIndexAll.length() >0 && flowActionItemJsonList.size() > 1){
			        JSONObject object = new JSONObject();
			        object.put("itemIndex", itemIndexAll.deleteCharAt(itemIndexAll.length()-1).toString());
			        object.put("itemName", "--所有项目--");
			        flowActionItemJson.put(object);
			    }
			} 
			for (Iterator iterator = flowActionItemJsonList.iterator(); iterator.hasNext();) {
			    JSONObject object = (JSONObject)iterator.next();
			    flowActionItemJson.put(object);
			}
			response.getWriter().write(flowActionItemJson.toString());
		}
		 catch (Exception e)
		 {
			Debug.logError(e.getMessage(), module);
		 }
	}

	/**
	 * 根据设备大类,周期 ,FlowActionItem，得到有数据的设备ID
	 * @author huanghp
	 * @param request
	 * @param response
	 * @return itemJSONArray(object{itemIndex,itemName})
	 */
	 public static void queryEquipmentByValidData(HttpServletRequest request, HttpServletResponse response)
	 {
		 try
		 {
			GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
			String periodIndex = request.getParameter("periodIndex");
			String sDate = request.getParameter("sDate");
			String eDate = request.getParameter("eDate");
			String itemIndex = request.getParameter("itemIndex");
			String isMsa = request.getParameter("isMsa");
			// 根据设备大类取到所有设备
			String sql = "select distinct t1.equipment_id "+
						 "from pm_form t1, flow_item_points t2 "+
						 "where t1.pm_index = t2.form_index and t2.form_type = 'PM' "+
						 "and t2.update_time >= to_date('"+sDate+"', 'yyyy-mm-dd') and t2.update_time < to_date('"+eDate+"', 'yyyy-mm-dd') + 1 "+
						 "and t1.period_index = '"+periodIndex+"' "+
						 "and t2.item_index in(" + itemIndex +") ";
			//MSA设备保养查询,项目包括数字型和文字型
			if (StringUtils.isEmpty(isMsa)) {
                sql += "and t2.item_type=2 ";
            } 
            sql +="order by t1.equipment_id";
			JSONArray eqpJson = new JSONArray();

			List itemList = SQLProcess.excuteSQLQuery(sql, delegator);

			for (Iterator it = itemList.iterator(); it.hasNext();)
			{
				Map map = (Map)it.next();
				JSONObject object = new JSONObject();
				object.put("equipmentId", map.get("EQUIPMENT_ID"));
				eqpJson.put(object);
			}
			response.getWriter().write(eqpJson.toString());
		}
		 catch (Exception e)
		 {
			Debug.logError(e.getMessage(), module);
		 }
	}

	/**
	 * 根据设备大类,周期 ,FlowActionItem，得到有数据的设备ID
	 * @author huanghp
	 * @param request
	 * @param response
	 * @return itemJSONArray(object{itemIndex,itemName})
	 */
	 public static void queryEquipmentByValidDataInChart(HttpServletRequest request, HttpServletResponse response)
	 {
		 try
		 {
			GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
			String periodIndex = request.getParameter("periodIndex");
			String sDate = request.getParameter("sDate");
			String eDate = request.getParameter("eDate");
			String equipmentType = request.getParameter("equipmentType");
			// 根据设备大类取到所有设备
			String sql = "  select distinct equipment_id from equipment where equipment_type = '"+equipmentType+"'";
			List eqpList = SQLProcess.excuteSQLQuery(sql, delegator);
			StringBuffer eqpStr = new StringBuffer();
			eqpStr.append("(");
            for(Iterator it = eqpList.iterator(); it.hasNext();)
            {
                Map map = (Map)it.next();
                eqpStr.append("'");
                eqpStr.append(map.get("EQUIPMENT_ID"));
                eqpStr.append("',");
            }
            if (eqpList.size() > 0)
            {
            	eqpStr.deleteCharAt(eqpStr.length() - 1);
            }
            eqpStr.append(") ");

            sql = " select distinct t1.equipment_id from pm_form t1 , flow_item_points t3 ";
            sql += " WHERE ";
            sql += "  t1.pm_index = t3.form_index";
            sql += " AND (REGEXP_SUBSTR(t3.item_value, '^(-|+)?\\d+(\\.\\d+)?$') > t3.item_Upper_Spec OR REGEXP_SUBSTR(t3.item_value, '^(-|+)?\\d+(\\.\\d+)?$') < t3.item_Lower_Spec) ";
            sql += " AND t1.equipment_id in " + eqpStr;
            sql += " AND t1.status = 1";
            sql += " AND t3.form_type = 'PM'";
            sql += " AND t3.item_type = 2";

			if (!periodIndex.equals("")){
				sql += " AND t1.period_index = '" + periodIndex + "'";
			}

			// use index update_Time
			sql += " AND t3.update_Time >= to_date('" + sDate + "', 'yyyy-mm-dd') AND t3.update_Time < to_date('" + eDate + "', 'yyyy-mm-dd') + 1 ";

			JSONArray eqpJson = new JSONArray();

			List itemList = SQLProcess.excuteSQLQuery(sql, delegator);

			for (Iterator it = itemList.iterator(); it.hasNext();)
			{
				Map map = (Map)it.next();
				JSONObject object = new JSONObject();
				object.put("equipmentId", map.get("EQUIPMENT_ID"));
				eqpJson.put(object);
			}
			response.getWriter().write(eqpJson.toString());
		}
		 catch (Exception e)
		 {
			Debug.logError(e.getMessage(), module);
		 }
	}

	 /**
	  * 根据设备大类查询PM周期,还有时间等
	  * @author huanghp
	  * @param request
	  * @param response
	  * @return
	  */
    public static void queryPMPeriodByEqpTypeAndDate(HttpServletRequest request, HttpServletResponse response) {
    	try {    
    	    GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
    	    String equipmentType = request.getParameter("equipmentType");
    	    //String sDate = request.getParameter("sDate");
    	    //String eDate = request.getParameter("eDate");
    	    String isMsa = request.getParameter("isMsa");
    	    JSONArray periodJson = new JSONArray();
    	    
    	    //不是MSA报表查询
    	    String sql = "select * from default_period t where t.eqp_type='"+equipmentType+"'";
    	    if ("1".equals(isMsa)) {//MSA报表查询
	    		//sql = sql + " and period_name like 'MSA%'";
    	    }
    	    
    	    List itemList = SQLProcess.excuteSQLQuery(sql, delegator);
    		if (itemList.size() > 0) {
    		    for (Iterator it = itemList.iterator(); it.hasNext();) {
	    			Map map = (Map) it.next();
	    			JSONObject object = new JSONObject();
	    			object.put("periodIndex", map.get("PERIOD_INDEX"));
	    			object.put("periodName", map.get("PERIOD_NAME"));
	    			periodJson.put(object);
    		    }
    		}    		
    	    
    	    response.getWriter().write(periodJson.toString());
    	} catch (Exception e) {
    	    Debug.logError(e.getMessage(), module);
    	}
    }

	 /**
	  * 根据设备大类查询PM周期,还有时间等
	  * @author huanghp
	  * @param request
	  * @param response
	  * @return
	  */
	 public static void queryPMPeriodByEqpTypeAndDateInChart(HttpServletRequest request, HttpServletResponse response)
	 {
		 try
		 {
			GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
			String equipmentType = request.getParameter("equipmentType");
			String sDate = request.getParameter("sDate");
			String eDate = request.getParameter("eDate");
			// 根据设备大类取到所有设备
			String sql = "  select distinct equipment_id from equipment where equipment_type = '"+equipmentType+"'";
			List eqpList = SQLProcess.excuteSQLQuery(sql, delegator);
			StringBuffer eqpStr = new StringBuffer();
			eqpStr.append("(");
            for(Iterator it = eqpList.iterator(); it.hasNext();)
            {
                Map map = (Map)it.next();
                eqpStr.append("'");
                eqpStr.append(map.get("EQUIPMENT_ID"));
                eqpStr.append("',");
            }
            if (eqpList.size() > 0)
            {
            	eqpStr.deleteCharAt(eqpStr.length() - 1);
            }
            eqpStr.append(") ");

            sql = " select t3.* from pm_form t1 , flow_item_points t3 ";
            sql += " WHERE ";
            sql += "  t1.pm_index = t3.form_index";
            sql += " AND (REGEXP_SUBSTR(t3.item_value, '^(-|+)?\\d+(\\.\\d+)?$') > t3.item_Upper_Spec OR REGEXP_SUBSTR(t3.item_value, '^(-|+)?\\d+(\\.\\d+)?$') < t3.item_Lower_Spec) ";
            sql += " AND t1.equipment_id in " + eqpStr.toString();
            sql += " AND t1.status = 1";
            sql += " AND t3.form_type = 'PM'";
            sql += " AND t3.item_type = 2";
            sql += " AND t3.update_Time >= to_date('" + sDate + "', 'yyyy-mm-dd') AND t3.update_Time <  to_date('" + eDate + "', 'yyyy-mm-dd') + 1 ";
            sql += " AND t1.period_index = '";

			JSONArray periodJson = new JSONArray();

			// 查询获得PM周期
			List periodList = delegator.findByAnd("DefaultPeriod", UtilMisc.toMap("eqpType",equipmentType),UtilMisc.toList("periodName"));
			//List periodList = SQLProcess.excuteSQLQuery(sql, delegator);

			for (Iterator it = periodList.iterator(); it.hasNext();)
			{
				Map map = (Map)it.next();
				// 先刷选一下有没有数据
				String querySql = sql + map.get("periodIndex") + "'";
				List itemList = SQLProcess.excuteSQLQuery(querySql, delegator);
				if (itemList.size() > 0)
				{
					JSONObject object = new JSONObject();
					object.put("periodIndex", map.get("periodIndex"));
					object.put("periodName", map.get("periodName"));
					periodJson.put(object);
				}
			}
			response.getWriter().write(periodJson.toString());
		 } catch(Exception e) {
			 Debug.logError(e.getMessage(), module);
		 }
	 }

    /**
     * 查看MSA机台保养纪录 初始画面
     * 
     * @param request
     * @param response
     * @return
     */
    public static String showMsaPmParamEntry(HttpServletRequest request,
            HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // 取得用户部门
            GenericValue userInfo = AccountHelper.getUserInfo(request, delegator);
            String maintDept = userInfo.getString("accountDept");
            // List deptList = delegator.findAllCache("EquipmentDept",
            // UtilMisc.toList("equipmentDept"));

            request.setAttribute("equipmentDept", maintDept);
            // request.setAttribute("deptList", deptList);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * 查看MSA机台保养纪录
     * 
     * @param request
     * @param response
     */
    public static String showMsaPm(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        Map filterMap = GeneralEvents.getInitParams(request, false);
        String periodIndex = request.getParameter("periodIndex");
        String itemIndex = request.getParameter("itemIndex");

        String[] arrEqpId = request.getParameterValues("eqpIdSelected");
        String strEqpId = ReportHelper.getEqpIdSqlStr(arrEqpId);
        request.setAttribute("eqpId", strEqpId);
        filterMap.put("eqpId", strEqpId);
        List dataList = null;
        try {
            // 根据条件查询数据
            if (StringUtils.isNotEmpty(periodIndex)) {
                GenericValue periodInfo = delegator.findByPrimaryKey("DefaultPeriod",
                        UtilMisc.toMap("periodIndex", periodIndex));
                request.setAttribute("periodName", periodInfo.getString("periodName"));
                request.setAttribute("defaultDays", periodInfo.getString("defaultDays"));
            } else {
                request.setAttribute("periodName", "");
                request.setAttribute("defaultDays", "");
            }

            dataList = ReportHelper.getMsaPmItemPointsList(delegator, filterMap);
            request.setAttribute("dataList", dataList);

        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
	 /**
     * 查看机台保养纪录参数分析图 初始画面
     *
     * @param request
     * @param response
     * @return
     */
    public static String queryPmParamAnalysisChart(HttpServletRequest request, HttpServletResponse response)
    {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try
        {
            // 取得用户部门
        	GenericValue userInfo = AccountHelper.getUserInfo(request,delegator);
        	String maintDept = userInfo.getString("accountDept");
	        //List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));

        	request.setAttribute("equipmentDept", maintDept);
            //request.setAttribute("deptList", deptList);
	    }
        catch (Exception e)
        {
        	request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
        	return "error";
        }
        return "success";
	}

    /**
     * 查看机台保养纪录参数分析图
     *
     * @param request
     * @param response
     */
    public static String showPmParamAnalysisChart(HttpServletRequest request,
            HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        
        Map filterMap = GeneralEvents.getInitParams(request, false);
        String periodIndex = request.getParameter("periodIndex");
        String itemIndex = request.getParameter("itemIndex");
    
        String[] arrEqpId = request.getParameterValues("eqpIdSelected");
        String strEqpId = ReportHelper.getEqpIdSqlStr(arrEqpId);
        request.setAttribute("eqpId", strEqpId);
        List dataList = null;
        try {
            // 根据条件查询数据
            GenericValue periodInfo = delegator.findByPrimaryKey("DefaultPeriod", UtilMisc.toMap("periodIndex", periodIndex));
            request.setAttribute("periodName", periodInfo.getString("periodName"));
            request.setAttribute("defaultDays", periodInfo.getString("defaultDays"));
    
            GenericValue itemInfo = delegator.findByPrimaryKey("FlowActionItem", UtilMisc.toMap("itemIndex", itemIndex));
            request.setAttribute("itemName", itemInfo.getString("itemName"));
            request.setAttribute("itemLowerSpec", itemInfo.getString("itemLowerSpec"));
            request.setAttribute("itemUpperSpec", itemInfo.getString("itemUpperSpec"));
    
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

	/**
	 * applet PmParamAnalysisChart根据条件查询pm_form,flow_item_points
	 *
	 * @param request
	 * @param response
	 * @return list
	 */
	public static void getPmParamItemPoints(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

		Map filterMap = GeneralEvents.getInitParams(request, false);

		List dataList = null;
		try {
			dataList = ReportHelper.getPmItemPointsList(delegator, filterMap);

			response.setContentType(Constants.CONTENT_TYPE);
			ObjectOutputStream os = new ObjectOutputStream(response.getOutputStream());
			os.writeObject(dataList);
			os.close();
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}

	/**
	 * 查看设备保养用时图
	 *
	 * @param request
	 * @param response
	 */
	public static String showPmFormChart(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

		String startDate = request.getParameter("startDate");
//		String startDate = "2000-05-11";
		String endDate = request.getParameter("endDate");
		String equipmentType = request.getParameter("equipmentType");
		String periodIndex = request.getParameter("periodIndex");
		String equipmentDept = request.getParameter("equipmentDept");
		String target = request.getParameter("targetSelect");
		String[] arrEqpId = request.getParameterValues("eqpIdSelected");
		String strEqpId = ReportHelper.getEqpIdSqlStr(arrEqpId);
		request.setAttribute("eqpId", strEqpId);

		Map paramMap = new HashMap();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("equipmentType", equipmentType);
		paramMap.put("periodIndex", periodIndex);
		paramMap.put("eqpId", strEqpId);
		paramMap.put("equipmentDept", equipmentDept);
		paramMap.put("target", target);

		try {
			// 根据条件查询数据
//			GenericValue defaultPeriod = delegator.findByPrimaryKey("DefaultPeriod", UtilMisc.toMap("periodIndex", periodIndex));
//
//			request.setAttribute("periodIndex", defaultPeriod.getString("periodIndex"));
//			request.setAttribute("periodName", defaultPeriod.getString("periodName"));
//			request.setAttribute("standardHour", defaultPeriod.getString("standardHour"));
//			request.setAttribute("eqpStatus", defaultPeriod.getString("eqpStatus"));
//			request.setAttribute("warningDays", defaultPeriod.getString("warningDays"));
//			request.setAttribute("defaultDays", defaultPeriod.getString("defaultDays"));

			List pmFormList = ReportHelper.queryPmFormHist(delegator,paramMap);
			request.setAttribute("pmFormList", pmFormList);

		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "success";
	}

	/**
	 * applet PmFormChart根据条件查询pm_form
	 *
	 * @param request
	 * @param response
	 * @return list
	 */
	public static void getPmFormItemPoints(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

		Map filterMap = GeneralEvents.getInitParams(request, false);

		List dataList = null;
		try {
			dataList = ReportHelper.queryPmFormHist(delegator, filterMap);

			response.setContentType(Constants.CONTENT_TYPE);
			ObjectOutputStream os = new ObjectOutputStream(response.getOutputStream());
			os.writeObject(dataList);
			os.close();
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}

	/**
	 * 查看设备异常用时图
	 *
	 * @param request
	 * @param response
	 */
	public static String showAbnormalFormChart(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String equipmentType = request.getParameter("equipmentType");
		String equipmentDept = request.getParameter("equipmentDept");

		String[] arrEqpId = request.getParameterValues("eqpIdSelected");
		String strEqpId = ReportHelper.getEqpIdSqlStr(arrEqpId);
		request.setAttribute("eqpId", strEqpId);

		Map paramMap = new HashMap();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("equipmentType", equipmentType);
		paramMap.put("eqpId", strEqpId);
		paramMap.put("equipmentDept", equipmentDept);

		try {
			List abnormalFormList = ReportHelper.queryAbnormalFormHist(delegator,paramMap);
			request.setAttribute("abnormalFormList", abnormalFormList);

		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "success";
	}

	/**
	 * applet AbnormalFormChart根据条件查询abnormal_form
	 *
	 * @param request
	 * @param response
	 * @return list
	 */
	public static void getAbnormalFormItemPoints(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

		Map filterMap = GeneralEvents.getInitParams(request, false);

		List dataList = null;
		try {
			dataList = ReportHelper.queryAbnormalFormHist(delegator, filterMap);

			response.setContentType(Constants.CONTENT_TYPE);
			ObjectOutputStream os = new ObjectOutputStream(response.getOutputStream());
			os.writeObject(dataList);
			os.close();
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}

	/**
	 * 查看设备异常统计表
	 *
	 * @param request
	 * @param response
	 */
	public static String showAbnormalTable(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String equipmentType = request.getParameter("equipmentType");
		String equipmentDept = request.getParameter("equipmentDept");

		String strEqpId = "";
		String[] arrEqpId = request.getParameterValues("eqpIdSelected");

		if (arrEqpId != null) {
			for (int i = 0; i < arrEqpId.length; i++) {
				if ("".equals(strEqpId)) {
					strEqpId = "'" + arrEqpId[i] + "'";
				} else {
					strEqpId = strEqpId + ",'" + arrEqpId[i] + "'";
				}
			}
		}

		request.setAttribute("eqpId", strEqpId);

		Map paramMap = new HashMap();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("equipmentType", equipmentType);
		paramMap.put("eqpId", strEqpId);
		paramMap.put("equipmentDept", equipmentDept);

		try {
			List abnormalFormList = ReportHelper.queryAbnormalTable(delegator,paramMap);
			request.setAttribute("abnormalTableList", abnormalFormList);

		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "success";
	}

	/**
	 * applet AbnormalFormChart根据条件查询abnormal_form
	 *
	 * @param request
	 * @param response
	 * @return list
	 */
	public static void AbnormalChartShow(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

		Map filterMap = GeneralEvents.getInitParams(request, false);

		List dataList = null;
		try {
			dataList = ReportHelper.queryAbnormalTable(delegator, filterMap);

			response.setContentType(Constants.CONTENT_TYPE);
			ObjectOutputStream os = new ObjectOutputStream(response.getOutputStream());
			os.writeObject(dataList);
			os.close();
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}

	/**
	 * applet AbnormalFormChart根据条件查询abnormal_form
	 *
	 * @param request
	 * @param response
	 * @return list
	 */
	public static void getAbnormalChartPoints(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

		Map filterMap = GeneralEvents.getInitParams(request, false);

		List dataList = null;
		try {
			dataList = ReportHelper.queryAbnormalTable(delegator, filterMap);

			response.setContentType(Constants.CONTENT_TYPE);
			ObjectOutputStream os = new ObjectOutputStream(response.getOutputStream());
			os.writeObject(dataList);
			os.close();
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}
	/**
	 * 查看机台保养事件分析图
	 *
	 * @param request
	 * @param response
	 */
	public static String showPmEventChart(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String equipmentType = request.getParameter("equipmentType");
		String keyEqp = request.getParameter("keyEqp");
		String adjustEqp = request.getParameter("adjustEqp");
		String measureEqp = request.getParameter("measureEqp");

		String[] arrEqpId = request.getParameterValues("eqpIdSelected");
		String strEqpId = ReportHelper.getEqpIdSqlStr(arrEqpId);

		Map paramMap = new HashMap();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("equipmentType", equipmentType);
		paramMap.put("eqpId", strEqpId);
		paramMap.put("keyEqp", keyEqp);
		paramMap.put("adjustEqp", adjustEqp);
		paramMap.put("measureEqp", measureEqp);

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date _startDate = dateFormat.parse(startDate);
			Date _endDate = dateFormat.parse(endDate);
			long dayCount = (_endDate.getTime()-_startDate.getTime())/1000/60/60/24 + 1;
			paramMap.put("dayCount", String.valueOf(dayCount));

			// 根据条件查询数据
			List pmFormList = ReportHelper.queryPmEventHist(delegator,paramMap);
			request.setAttribute("pmFormList", pmFormList);

			request.setAttribute("dayCount", String.valueOf(dayCount));

			session.setAttribute("paramMap", paramMap);

		} catch (Exception e) {
			session.removeAttribute("paramMap");
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "success";
	}

	/**
	 * 保养机台分析图
	 *
	 * @param request
	 * @param response
	 */
	public static String showPmEventEqpChart(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String periodIndex = (String) request.getParameter("periodIndex");
	    String periodName = (String) request.getParameter("periodName");

	    HttpSession session = request.getSession();
		Map paramMap = (Map) session.getAttribute("paramMap");
		paramMap.put("periodIndex", periodIndex);
		paramMap.put("periodName", periodName);

		try {
			// 根据条件查询数据
			List pmFormList = ReportHelper.queryPmEventEqpHist(delegator,paramMap);
			request.setAttribute("pmFormList", pmFormList);

			session.setAttribute("paramMap", paramMap);

		} catch (Exception e) {
			session.removeAttribute("paramMap");
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "success";
	}

	/**
	 * 机台细项
	 *
	 * @param request
	 * @param response
	 */
	public static String showPmEventEqpDetail(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

		String equipmentId = request.getParameter("equipmentId");

		HttpSession session = request.getSession();
		Map paramMap = (Map) session.getAttribute("paramMap");
		paramMap.put("equipmentId", equipmentId);

		try {
			List pmFormList = ReportHelper.queryPmEventEqpDetail(delegator,paramMap);
			request.setAttribute("pmFormList", pmFormList);

		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "success";
	}

	/**
	 * applet PmEventChart根据条件查询pm_form
	 *
	 * @param request
	 * @param response
	 * @return list
	 */
	public static void getPmEventHist(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

		Map filterMap = GeneralEvents.getInitParams(request, false);

		List dataList = null;
		try {
			dataList = ReportHelper.queryPmEventHist(delegator, filterMap);

			response.setContentType(Constants.CONTENT_TYPE);
			ObjectOutputStream os = new ObjectOutputStream(response.getOutputStream());
			os.writeObject(dataList);
			os.close();
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}

	/**
	 * applet PmEventEqpChart根据条件查询pm_form
	 *
	 * @param request
	 * @param response
	 * @return list
	 */
	public static void getPmEventEqpHist(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

		Map filterMap = GeneralEvents.getInitParams(request, false);

		List dataList = null;
		try {
			dataList = ReportHelper.queryPmEventEqpHist(delegator, filterMap);

			response.setContentType(Constants.CONTENT_TYPE);
			ObjectOutputStream os = new ObjectOutputStream(response.getOutputStream());
			os.writeObject(dataList);
			os.close();
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}

	/**
	 * 查询事件List
	 *
	 * @param request
	 * @param response
	 * @return list
	 */
	public static String eventList(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String PM = request.getParameter("PM");
		String TS = request.getParameter("TS");
		String strWhere = "";
		if (PM == null){

		}else{
			strWhere += ",'PM'";
		}
		if (TS == null){

		}else{
			strWhere += ",'TS'";
		}
		if (!strWhere.equals("")){
			strWhere = strWhere.substring(1);
		}
		String strSQL = "";
		String startDate=request.getParameter("startDate");
		String endDate=request.getParameter("endDate");
		String sectionIndex=request.getParameter("section");
		String deptIndex=request.getParameter("dept");
		boolean hasTimeLimit = false;
		if (StringUtils.isNotEmpty(startDate) || StringUtils.isNotEmpty(endDate))
		{
			hasTimeLimit = true;
		}
		try {
//			事件List
			List event_List = null;
			strSQL = " SELECT t.*,Equipment.MAINT_DEPT ";
			strSQL += " FROM (";
			strSQL += " (select ABNORMAL_FORM.ABNORMAL_INDEX PM_INDEX,ABNORMAL_FORM.Equipment_Id,ABNORMAL_FORM.ABNORMAL_REASON ABNORMAL_NAME,ABNORMAL_FORM.START_TIME ";
			strSQL += " ,ABNORMAL_FORM.STATUS,'TS' Type from ABNORMAL_FORM ";
			strSQL += " WHERE ABNORMAL_FORM.Status = 0 or ABNORMAL_FORM.Status=2 ";
			if (!hasTimeLimit)
			{
				strSQL += " AND to_char(ABNORMAL_FORM.START_TIME,'yyyy-MM-dd') = to_char(sysdate,'yyyy-MM-dd') ";
			}
			strSQL += " )union ";
			strSQL += " (select ABNORMAL_FORM.ABNORMAL_INDEX PM_INDEX,ABNORMAL_FORM.Equipment_Id,ABNORMAL_FORM.ABNORMAL_REASON ABNORMAL_NAME,ABNORMAL_FORM.END_TIME START_TIME ";
			strSQL += " ,ABNORMAL_FORM.STATUS,'TS' Type from ABNORMAL_FORM ";
			strSQL += " WHERE ABNORMAL_FORM.Status = 1 ";
			if (!hasTimeLimit)
			{
				strSQL += " AND to_char(ABNORMAL_FORM.END_TIME,'yyyy-MM-dd') = to_char(sysdate,'yyyy-MM-dd') ";
			}
			strSQL += " )union ";
			strSQL += " (select PM_FORM.PM_INDEX PM_INDEX,PM_FORM.Equipment_Id,default_period.period_name PM_NAME,PM_FORM.START_TIME ";
			strSQL += " ,PM_FORM.STATUS,'PM' Type from PM_FORM,default_period  ";
			strSQL += " WHERE (PM_FORM.Status = 0 or PM_FORM.Status=2) ";
			if (!hasTimeLimit)
			{
				strSQL += " AND to_char(PM_FORM.START_TIME,'yyyy-MM-dd') = to_char(sysdate,'yyyy-MM-dd') ";
			}
			strSQL += " AND PM_FORM.PERIOD_INDEX = default_period.period_index(+) ";
			strSQL += " )union ";
			strSQL += " (select PM_FORM.PM_INDEX PM_INDEX,PM_FORM.Equipment_Id,default_period.period_name PM_NAME,PM_FORM.END_TIME START_TIME ";
			strSQL += " ,PM_FORM.STATUS,'PM' Type from PM_FORM,default_period  ";
			strSQL += " WHERE PM_FORM.Status = 1 ";
			if (!hasTimeLimit)
			{
				strSQL += " AND to_char(PM_FORM.END_TIME,'yyyy-MM-dd') = to_char(sysdate,'yyyy-MM-dd') ";
			}
			strSQL += " AND PM_FORM.PERIOD_INDEX = default_period.period_index(+) ";

			strSQL += " )) t ";
			strSQL += " LEFT OUTER JOIN ";
			strSQL += "   Equipment ";
			strSQL += " ON  Equipment.EQUIPMENT_ID = t.EQUIPMENT_ID ";
			if (!strWhere.equals("")){
				strSQL += " WHERE ";
				strSQL += "   t.Type in (" + strWhere + ")";
				if (StringUtils.isNotEmpty(startDate)){
					strSQL += " AND t.START_TIME  >=  to_date('" + startDate + "','yyyy-MM-dd')";
				}
				if (StringUtils.isNotEmpty(endDate)){
					strSQL += " AND t.START_TIME  < to_date('" + endDate + "','yyyy-MM-dd') + 1";
				}
				if (sectionIndex != null && sectionIndex.length() > 0){
					strSQL += " AND Equipment.SECTION in (SELECT SECTION FROM equipment_section WHERE SECTION_INDEX = '" + sectionIndex + "')";
				}
				if (deptIndex != null && deptIndex.length() > 0){
					strSQL += " AND Equipment.MAINT_DEPT in (SELECT equipment_dept FROM equipment_dept WHERE DEPT_INDEX = '" + deptIndex + "')";
				}
				strSQL += " order by t.START_TIME";
			}
			else{
				strSQL += " WHERE ";
				strSQL += "   1=3";
			}
			event_List = SQLProcess.excuteSQLQuery(strSQL, delegator);
			request.setAttribute("event_List", event_List);
			request.setAttribute("PM", PM);
			request.setAttribute("TS", TS);
			request.setAttribute("flag", "OK");
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
		Map map=new HashMap();
		String type=request.getParameter("objectType");
		String value=request.getParameter("object");
		map.put("objectType",type);
    	map.put("object",value);
    	map.put("sectionIndex",sectionIndex);
    	map.put("deptIndex",deptIndex);
		try {
			String accountNo=CommonUtil.getUserNo(request);
			List useInfoList=QuFollowHelper.getAccountSection(delegator, accountNo);
        	GenericValue gv=(GenericValue)useInfoList.get(0);
        	//登陆人的科别
        	String section=gv.getString("accountSection");
        	List eqpIdList=QuFollowHelper.getEquipIDList(delegator, section);
        	List equipMentList=CommonHelper.getEquipmentTypeList(delegator);
        	List deptList=ReportHelper.getDeptList(delegator);
        	List quFollowJobList=QuFollowHelper.queryQuFollowJobList(delegator, map);
        	request.setAttribute("FOLLOWJOB_LIST", quFollowJobList);
        	request.setAttribute("EQPID_LIST", eqpIdList);
        	request.setAttribute("EQUIPMENT_LIST", equipMentList);
        	request.setAttribute("DEPT_LIST", deptList);
        	request.setAttribute("startDate", startDate);
        	request.setAttribute("endDate", endDate);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
        }
		return "success";
	}

	/**
     * 查询机台保养日期 初始画面
     *
     * @param request
     * @param response
     * @return
     */
    public static String queryPmScheduleEntry(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // 取得用户部门
        	GenericValue userInfo = AccountHelper.getUserInfo(request,delegator);
        	String maintDept = userInfo.getString("accountDept");

        	List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));

        	request.setAttribute("maintDept", maintDept);
            request.setAttribute("deptList", deptList);
        } catch (Exception e) {
        	request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
        	return "error";
        }

        return "success";
    }
    
//------------------------------------线边仓报表---------------------------------------
	
    
	/**
	 * 进入线边仓库存余量查询页面
	 * @param request
	 * @param response
	 */
	public static String intoMcsStoQueryReport(HttpServletRequest request, HttpServletResponse response) {
	    return "success";
	}
	
    /**
     * 根据查询条件进行线边仓库存余量查询
     * 
     * @param request
     * @param response
     */
    public static String queryMcsPartUseDiffList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String month = request.getParameter("month");
        String equipmentDept = request.getParameter("equipmentDept");
        String partNo = request.getParameter("partNo");
        Map parMap = new HashMap();
        parMap.put("month", month);
        parMap.put("equipmentDept", equipmentDept);
        parMap.put("partNo", partNo);
        try {
            // 根据条件查询数据

            List partList = ReportHelper.queryMcsStoReqList(delegator, parMap);
            request.setAttribute("PART_LIST", partList);
            request.setAttribute("partNo", partNo);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
        
	/**
	 * 进入PM用料明细查询页面
	 * @param request
	 * @param response
	 */
	public static String intoPmPartsUsedQueryReport(HttpServletRequest request, HttpServletResponse response) {
	    return "success";
	}
	
    /**
     * 根据查询条件进行pm用料明细查询
     * 
     * @param request
     * @param response
     */
    public static String pmPartsUsedDetailQuery(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String equipmentDept = request.getParameter("equipmentDept");//部门
        String equipmentType = request.getParameter("equipmentType");//设备大类
        String pmType = request.getParameter("pmType");//保养类别
        String month = request.getParameter("month");//查询月份
        String partNo = request.getParameter("partNo");//物料号
        String periodIndex = request.getParameter("periodIndex");//保养周期
        String[] arrEqpId = request.getParameterValues("eqpIdSelected");
        String strEqpId = ReportHelper.getEqpIdSqlStr(arrEqpId);        //设备列表
        
        Map parMap = new HashMap();
        parMap.put("equipmentDept", equipmentDept);
        parMap.put("equipmentType", equipmentType);
        parMap.put("pmType", pmType);
        parMap.put("month", month);
        parMap.put("partNo", partNo);
        parMap.put("periodIndex", periodIndex);
        parMap.put("strEqpId", strEqpId);
        try {
            // 根据条件查询数据

            List partList = ReportHelper.queryPartsUsedDetail(delegator, parMap);
            request.setAttribute("PART_LIST", partList);
            request.setAttribute("eqpId", strEqpId);
            request.setAttribute("partNo", partNo);
            request.setAttribute("pmType", pmType);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
    /**
     * 进入PM费用明细查询页面
     * @param request
     * @param response
     */
    public static String intoPmPartsUsedCostQueryReport(HttpServletRequest request, HttpServletResponse response) {
        return "success";
    }
    
    /**
     * 根据查询条件进行pm用料明细查询
     * 
     * @param request
     * @param response
     */
    public static String pmPartsUsedCostDetailQuery(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String equipmentDept = request.getParameter("equipmentDept");//部门
        String equipmentType = request.getParameter("equipmentType");//设备大类
        String pmType = request.getParameter("pmType");//保养类别
        String month = request.getParameter("month");//查询月份
        String partNo = request.getParameter("partNo");//物料号
        String periodIndex = request.getParameter("periodIndex");//保养周期
        String[] arrEqpId = request.getParameterValues("eqpIdSelected");
        String strEqpId = ReportHelper.getEqpIdSqlStr(arrEqpId);        //设备列表
        
        Map parMap = new HashMap();
        parMap.put("equipmentDept", equipmentDept);
        parMap.put("equipmentType", equipmentType);
        parMap.put("pmType", pmType);
        parMap.put("month", month);
        parMap.put("partNo", partNo);
        parMap.put("periodIndex", periodIndex);
        parMap.put("strEqpId", strEqpId);
        try {
            // 根据条件查询数据
            
            List partList = ReportHelper.queryPartsUsedCostDetail(delegator, parMap);
            request.setAttribute("PART_LIST", partList);
            request.setAttribute("eqpId", strEqpId);
            request.setAttribute("partNo", partNo);
            request.setAttribute("pmType", pmType);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }
    
	public static String queryKeyPartsUseByCondition(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String equipmentId = request.getParameter("equipmentId");
		String equipmentType = request.getParameter("equipmentType");
		String keydesc = request.getParameter("keydesc");
		String eqpmodel = request.getParameter("eqp_Model");
		String deptIndex = request.getParameter("eqp_Dept");
		String isalarm = request.getParameter("isalarm");
		String isdelay = request.getParameter("isdelay");
		String isError = request.getParameter("isError");
		String isUsed = request.getParameter("isUsed");
		String userNo = CommonUtil.getUserNo(request);
		GenericValue user = AccountHelper.getAccountByNo(delegator, userNo);
		String dept = user.getString("accountDept");
		String section = request.getParameter("deptSection");
//		GenericValue userInfo = CommonHelper.getUserInfo(request, delegator);
//		String maintDept = userInfo.getString("accountDept");
		Map map = new HashMap();
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("equipmentId", equipmentId);
		map.put("equipmentType", equipmentType);
		map.put("keydesc", keydesc);
		map.put("eqpmodel", eqpmodel);
		map.put("userNo", userNo);
		map.put("maintDept", dept);
		map.put("section", section);
		map.put("isalarm", isalarm);
		map.put("isdelay", isdelay);
		map.put("isError", isError);
		map.put("isUsed", isUsed);
		try {
			// 根据条件查询数据
			List eqpList = ReportHelper.queryKeyPartsUseByCondition(delegator, map);
			List reportList = new ArrayList();
			for (int i = 0; i < eqpList.size(); i++) {
				Map eqmap = (Map) eqpList.get(i);
				String eqpId = (String) eqmap.get("EQP_ID");
				String actul = (String) eqmap.get("ACTUL");
				String delayLife = (String) eqmap.get("DELAY_LIFE");
				String errorSpec = (String) eqmap.get("ERROR_SPEC");
				String warnSpec = (String) eqmap.get("WARN_SPEC");
				String limitType = (String) eqmap.get("LIMIT_TYPE");
				String initLife = (String) eqmap.get("INIT_LIFE");
				String updateTime = (String) eqmap.get("UPDATE_TIME");
				if (StringUtils.isNotEmpty(limitType) && limitType.equals("TIME(天)")) {
					if (StringUtils.isEmpty(isalarm) && StringUtils.isEmpty(isError)) {
						reportList.add(eqmap);
					}
					if (StringUtils.isNotEmpty(isalarm) && isalarm.equals("Y")) {
						if (Long.parseLong(actul) >= (Long.parseLong(warnSpec) + Long.parseLong(delayLife))) {
							reportList.add(eqmap);
						}
					}
					if (StringUtils.isNotEmpty(isError) && isError.equals("Y")) {
						if (Long.parseLong(actul) >= (Long.parseLong(errorSpec) + Long.parseLong(delayLife))) {
							reportList.add(eqmap);
						}
					}
				}
				if (StringUtils.isNotEmpty(limitType) && !limitType.equals("TIME(天)")) {
//					if (updateTime == null) {
//						actul = "0";
//					} else {
//						actul = PartsHelper.getActulFromFdc(delegator, eqpId, limitType, updateTime, null);
//					}
					actul = "0";
					if (actul.equals("fdcError") || actul.equals("relationError")) {
						eqmap.put("lifeError", actul);
						actul = "0";
						actul = (Long.parseLong(actul) + "").trim(); // 对应eqpid，limittype没有fdc收值时，使用寿命置为零
					} else {
						actul = (Double.parseDouble(actul) + Double.parseDouble(initLife) + "").trim();
					}
					eqmap.remove("ACTUL");
					eqmap.put("ACTUL", actul);
					if (StringUtils.isEmpty(isalarm) && StringUtils.isEmpty(isError)) {
						reportList.add(eqmap);
					}
					if (StringUtils.isNotEmpty(isalarm) && isalarm.equals("Y")) {
						if (Double
								.parseDouble(actul) >= (Double.parseDouble(warnSpec) + Double.parseDouble(delayLife))) {
							reportList.add(eqmap);
						}
					}
					if (StringUtils.isNotEmpty(isError) && isError.equals("Y")) {
						if (Double.parseDouble(
								actul) >= (Double.parseDouble(errorSpec) + Double.parseDouble(delayLife))) {
							reportList.add(eqmap);
						}
					}
				}
			}

			// 得到设备大类
			List equipMentList = ReportHelper.getEquipMentList(delegator);

			List equipMoelList = delegator.findByAnd("EquipmentModel", UtilMisc.toMap("maintDept", dept),
					UtilMisc.toList("Model"));
			List equipDeptList = delegator.findAll("EquipmentDept");

			request.setAttribute("equipDeptList", equipDeptList);
			request.setAttribute("PART_LIST", reportList);
			request.setAttribute("equipMentList", equipMentList);
			request.setAttribute("equipMoelList", equipMoelList);
			// 如果值为NULL说明，前台页面没有选择此查询项
			request.setAttribute("keydesc", keydesc);
			request.setAttribute("eqpmodel", eqpmodel);
			request.setAttribute("deptIndex", deptIndex);
			request.setAttribute("eqpDept", dept);
			request.setAttribute("section", section);
			request.setAttribute("equipmentId", equipmentId);
			request.setAttribute("isalarm", isalarm);
			request.setAttribute("isdelay", isdelay);
			request.setAttribute("isError", isError);
			request.setAttribute("isUsed", isUsed);
			request.setAttribute("flag", "true");
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}

		return "Success";
	}
	
	/*
	 * 通过部门Id获得课别list
	 */
	public static void getSectionAndModelByDeptIndex(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String deptIndex = request.getParameter("deptIndex");

		JSONArray modelArray = new JSONArray();
		JSONArray sectionArray = new JSONArray();
		JSONObject jsObject = new JSONObject();
		try {
			// 得到eqpId列表
			List sectionList = ReportHelper.getSectionListbyDeptIndex(delegator, deptIndex);
			for (int i = 0; i < sectionList.size(); i++) {
				GenericValue gv = (GenericValue) sectionList.get(i);
				sectionArray.put(gv.getString("section"));
			}
			List equipMoelList = delegator.findByAnd("EquipmentModel", UtilMisc.toMap("maintDept", deptIndex),
					UtilMisc.toList("Model"));
			for (int i = 0; i < equipMoelList.size(); i++) {
				GenericValue gv = (GenericValue) equipMoelList.get(i);
				modelArray.put(gv.getString("model"));
			}
			jsObject.put("sectionArray", sectionArray);
			jsObject.put("modelArray", modelArray);
			// 返回jsobject
			response.getWriter().write(jsObject.toString());
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}
	
	/**
	 * 进入机台用料查询页面
	 * 
	 * @param request
	 * @param response
	 */
	public static String intoKeyPartsEqpQuery(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String userNo = CommonUtil.getUserNo(request);
		try {
			List eqpList = ReportHelper.getEquipMentList(delegator);
			String maintDept = AccountHelper.getUserDeptIndex(delegator, userNo);

			List equipMoelList = delegator.findByAnd("EquipmentModel", UtilMisc.toMap("maintDept", maintDept),
					UtilMisc.toList("Model"));
			List equipDeptList = delegator.findAll("EquipmentDept");

			request.setAttribute("equipDeptList", equipDeptList);
			request.setAttribute("equipMentList", eqpList);
			request.setAttribute("equipMoelList", equipMoelList);
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}
	
	// 关键备件机台记录查询
	public static String queryKeyPartsEqpByCondition(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String equipmentId = request.getParameter("equipmentId");
		String eqpmodel = request.getParameter("eqp_Model");
		String deptIndex = request.getParameter("eqp_Dept");
		String section = request.getParameter("deptSection");
		String isWarn = request.getParameter("isWarn");
		String isdelay = request.getParameter("isdelay");
		String isError = request.getParameter("isError");
		String userNo = CommonUtil.getUserNo(request);
		String maintDept = AccountHelper.getUserDeptIndex(delegator, userNo);
		Map map = new HashMap();
		map.put("equipmentId", equipmentId);
		map.put("eqpmodel", eqpmodel);
		map.put("userNo", userNo);
		map.put("maintDept", maintDept);
		map.put("section", section);
		map.put("isWarn", isWarn);
		map.put("isdelay", isdelay);
		map.put("isError", isError);
		try {
			// 根据条件查询数据
			List eqpList = ReportHelper.queryKeyPartsEqpByCondition(delegator, map);
			List reportList = new ArrayList();
			for (int i = 0; i < eqpList.size(); i++) {
				Map eqmap = (Map) eqpList.get(i);
				String eqpId = (String) eqmap.get("EQP_ID");
				String actul = (String) eqmap.get("ACTUL");
				String delayLife = (String) eqmap.get("DELAY_LIFE");
				String errorSpec = (String) eqmap.get("ERROR_SPEC");
				String warnSpec = (String) eqmap.get("WARN_SPEC");
				String limitType = (String) eqmap.get("LIMIT_TYPE");
				String initLife = (String) eqmap.get("INIT_LIFE");
				String updateTime = (String) eqmap.get("UPDATE_TIME");
				String unuseTime = (String) eqmap.get("CREATE_TIME");
				if (StringUtils.isNotEmpty(limitType) && limitType.equals("TIME(天)")) {
					if (StringUtils.isEmpty(isWarn) && StringUtils.isEmpty(isError)) {
						reportList.add(eqmap);
					}
					if (StringUtils.isNotEmpty(isWarn) && isWarn.equals("Y")) {
						if (Long.parseLong(actul) >= (Long.parseLong(warnSpec) + Long.parseLong(delayLife))) {
							reportList.add(eqmap);
						}
					}
					if (StringUtils.isNotEmpty(isError) && isError.equals("Y")) {
						if (Long.parseLong(actul) >= (Long.parseLong(errorSpec) + Long.parseLong(delayLife))) {
							reportList.add(eqmap);
						}
					}
				}
				if (StringUtils.isNotEmpty(limitType) && !limitType.equals("TIME(天)")) {
//					if (updateTime == null) {
//						actul = "0";
//					} else {
//						actul = PartsHelper.getActulFromFdc(delegator, eqpId, limitType, updateTime, unuseTime);
//					}
//					if (actul.equals("fdcError") || actul.equals("relationError")) {
//						eqmap.put("lifeError", actul);
//						actul = "0";
//						actul = (Long.parseLong(actul) + "").trim(); // 对应eqpid，limittype没有fdc收值时，使用寿命置为零
//					} else {
//						actul = (Double.parseDouble(actul) + Double.parseDouble(initLife) + "").trim();
//					}
					actul = "0";
					eqmap.remove("ACTUL");
					eqmap.put("ACTUL", actul);
					if (StringUtils.isEmpty(isWarn) && StringUtils.isEmpty(isError)) {
						reportList.add(eqmap);
					}
					if (StringUtils.isNotEmpty(isWarn) && isWarn.equals("Y")) {
						if (Double
								.parseDouble(actul) >= (Double.parseDouble(warnSpec) + Double.parseDouble(delayLife))) {
							reportList.add(eqmap);
						}
					}
					if (StringUtils.isNotEmpty(isError) && isError.equals("Y")) {
						if (Double.parseDouble(
								actul) >= (Double.parseDouble(errorSpec) + Double.parseDouble(delayLife))) {
							reportList.add(eqmap);
						}
					}
				}
			}

			// 得到设备大类
			List equipMentList = ReportHelper.getEquipMentList(delegator);

			List equipMoelList = delegator.findByAnd("EquipmentModel", UtilMisc.toMap("maintDept", maintDept),
					UtilMisc.toList("Model"));
			List equipDeptList = delegator.findAll("EquipmentDept");

			request.setAttribute("equipDeptList", equipDeptList);
			request.setAttribute("PART_LIST", reportList);
			request.setAttribute("equipMentList", equipMentList);
			request.setAttribute("equipMoelList", equipMoelList);
			// 如果值为NULL说明，前台页面没有选择此查询项
			request.setAttribute("eqpmodel", eqpmodel);
			request.setAttribute("deptIndex", deptIndex);
			request.setAttribute("eqpDept", deptIndex);
			request.setAttribute("section", section);
			request.setAttribute("equipmentId", equipmentId);
			request.setAttribute("isWarn", isWarn);
			request.setAttribute("isdelay", isdelay);
			request.setAttribute("isError", isError);
			request.setAttribute("flag", "true");
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "Success";
	}
	
	// 关键备件维护更新历史查询
	public static String queryKeyPartsUpdateByCondition(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String eqpmodel = request.getParameter("eqp_Model");
		String deptIndex = request.getParameter("eqp_Dept");
		String section = request.getParameter("deptSection");
		String userNo = CommonUtil.getUserNo(request);
		Map map = new HashMap();
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("eqpmodel", eqpmodel);
		map.put("userNo", userNo);
		map.put("maintDept", deptIndex);
		map.put("section", section);

		try {
			// 根据条件查询数据
			List keyPartsHist = ReportHelper.queryKeyPartsUpdateByCondition(delegator, map);
			// 得到设备大类
			List equipMentList = ReportHelper.getEquipMentList(delegator);

			List equipMoelList = delegator.findByAnd("EquipmentModel", UtilMisc.toMap("maintDept", deptIndex),
					UtilMisc.toList("Model"));
			List equipDeptList = delegator.findAll("EquipmentDept");

			request.setAttribute("equipDeptList", equipDeptList);
			request.setAttribute("PART_LIST", keyPartsHist);
			request.setAttribute("equipMentList", equipMentList);
			request.setAttribute("equipMoelList", equipMoelList);
			// 如果值为NULL说明，前台页面没有选择此查询项
			request.setAttribute("eqpmodel", eqpmodel);
			request.setAttribute("deptIndex", deptIndex);
			request.setAttribute("eqpDept", deptIndex);
			request.setAttribute("section", section);
			request.setAttribute("flag", "true");
		} catch (Exception e) {
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "Success";
	}
	
	public static String queryKeyPartsMustchangeComm(HttpServletRequest request, HttpServletResponse response){
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    	String startTime=request.getParameter("startDate");
    	String endTime=request.getParameter("endDate");
    	String deptIndex=request.getParameter("eqp_Dept");
    	String section=request.getParameter("deptSection");
    	String model=request.getParameter("eqp_Model");
    	String eqpId=request.getParameter("equipmentId");
    	String keydesc=request.getParameter("keydesc");
    	String reason=request.getParameter("reason");
    	
    	String sql="select t.*,t1.maint_dept,t1.section,t1.model,t2.dept_index from key_parts_mustchange_comm t "
    			+ "left join equipment t1 on t.eqp_id=t1.equipment_id "
    			+ "left join equipment_dept t2 on t1.maint_dept=t2.equipment_dept where 1=1";
    	
    	if(StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)){
    		sql=sql+"and t.UPDATE_TIME>=to_date('"+startTime+"','yyyy-mm-dd hh24:mi:ss') and t.UPDATE_TIME<=to_date('"+endTime+"','yyyy-mm-dd hh24:mi:ss')";
    	}
    	if(StringUtils.isNotEmpty(deptIndex)){
    		sql=sql+" and t2.dept_index='"+deptIndex+"'";
    	}
    	if(StringUtils.isNotEmpty(section)){   		
    		sql=sql+" and t1.section='"+section+"'";
    	}
    	if(StringUtils.isNotEmpty(model)){   		
    		sql=sql+" and t1.model='"+model+"'";
    	}
    	if(StringUtils.isNotEmpty(eqpId)){   		
    		sql=sql+" and t.eqp_id='"+eqpId+"'";
    	}
    	if(StringUtils.isNotEmpty(keydesc)){   		
    		sql=sql+" and t.keydesc='"+keydesc+"'";
    	}
    	if(StringUtils.isNotEmpty(reason)){   		
    		sql=sql+" and t.reason='"+reason+"'";
    	}
    	
    	try {
			List list=SQLProcess.excuteSQLQuery(sql, delegator);
			
	    	List equipDeptList=delegator.findAll("EquipmentDept");

	        request.setAttribute("equipDeptList", equipDeptList);
			
			request.setAttribute("musgchangeCommList", list);
			request.setAttribute("eqpDept", deptIndex);  
			request.setAttribute("deptIndex", deptIndex);
			request.setAttribute("section", section);
			request.setAttribute("eqpmodel",model);
			request.setAttribute("equipmentId", eqpId);
	     	request.setAttribute("keydesc", keydesc);
	     	request.setAttribute("reason", reason);
			request.setAttribute("flag", "true");
			
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
		}
		return "success";
    }

	/**
	 * 进入清洗件报表查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public static String intoKeyEqpPartsCleanReport(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		try {
			List deptList = delegator.findAll("EquipmentDept");
			request.setAttribute("deptList", deptList);
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}

		return "success";
	}

	public static String keyPartsCleanUpdateList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String deptIndex = request.getParameter("dept");
		String model = request.getParameter("model");
		String keydesc = request.getParameter("keydesc");
		String seriesNo = request.getParameter("seriesNo");

		String sql = "select t1.parts_id,t1.parts_name,t1.keydesc,t.* from key_eqp_parts_clean_hist t "
				+ " left join key_eqp_parts t1 on t.key_parts_id=t1.key_parts_id where 1=1";
		String sqlcondition = "";
		if (StringUtils.isNotEmpty(model)) {
			sqlcondition = sqlcondition + " and t1.eqp_type='" + model + "' ";
		}
		if (StringUtils.isNotEmpty(keydesc)) {
			sqlcondition = sqlcondition + " and t1.keydesc='" + keydesc + "' ";
		}
		if (StringUtils.isNotEmpty(seriesNo)) {
			sqlcondition = sqlcondition + " and t.series_no='" + seriesNo + "' ";
		}
		sql = sql + sqlcondition + " order by t.series_no,t.update_time desc";
		try {
			List list = SQLProcess.excuteSQLQuery(sql, delegator);

			List deptList = delegator.findAll("EquipmentDept");
			request.setAttribute("deptList", deptList);

			request.setAttribute("keyPartsCleanUpdateList", list);
			request.setAttribute("deptIndex", deptIndex);
			request.setAttribute("model", model);
			request.setAttribute("keydesc", keydesc);
			request.setAttribute("seriesNo", seriesNo);
			request.setAttribute("flag", "OK");
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
		return "success";
	}

	public static String keyPartsCleanUseList(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String type = request.getParameter("type");
		String deptIndex = request.getParameter("dept");
		String model = request.getParameter("model");
		String keydesc = request.getParameter("keydesc");
		String seriesNo = request.getParameter("seriesNo");
		String isError = request.getParameter("isError");
		String sqlcondition = "";
		String sql_detal1 = "select t2.keydesc,t2.parts_id,t2.parts_name,t2.limit_type,t2.warn_spec,t2.error_spec,t2.eqp_type, "
				+ " t.eqp_id,t.vendor,t.series_no,t.base_sn,t.status,t.parts_type,t.off_line,t.update_time,t.create_time,t.update_user, "
				+ " nvl(floor(trunc(t.create_time, 'dd') - trunc(t.update_time, 'dd')), '0') actul from key_parts_use t "
				+ " left join key_eqp_parts_clean t1 on t.key_parts_clean_id = t1.key_parts_clean_id "
				+ " left join key_eqp_parts t2 on t.key_parts_id = t2.key_parts_id "
				+ " where t.key_parts_clean_id is not null and t.status='OFFLINE' ";
		String sql_detal2 = "select t2.keydesc,t2.parts_id,t2.parts_name,t2.limit_type,t2.warn_spec,t2.error_spec,t2.eqp_type, "
				+ " t.eqp_id,t.vendor,t.series_no,t.base_sn,t.status,t.parts_type,t.off_line,t.update_time,t.create_time,t.update_user, "
				+ " nvl(floor(sysdate - trunc(t.update_time, 'dd')), '0') actul from key_parts_use t "
				+ " left join key_eqp_parts_clean t1 on t.key_parts_clean_id = t1.key_parts_clean_id "
				+ " left join key_eqp_parts t2 on t.key_parts_id = t2.key_parts_id "
				+ " where t.key_parts_clean_id is not null and t.status='USING' ";

		if (StringUtils.isNotEmpty(deptIndex)) {
			sqlcondition = sqlcondition + "and t2.maint_dept='" + deptIndex + "' ";
		}
		if (StringUtils.isNotEmpty(model)) {
			sqlcondition = sqlcondition + " and t2.eqp_type='" + model + "' ";
		}
		if (StringUtils.isNotEmpty(keydesc)) {
			sqlcondition = sqlcondition + " and t2.keydesc='" + keydesc + "' ";
		}
		if (StringUtils.isNotEmpty(seriesNo)) {
			sqlcondition = sqlcondition + " and t1.series_no='" + seriesNo + "' ";
		}
		String sql_detal = sql_detal1 + sqlcondition + " union " + sql_detal2 + sqlcondition
				+ " order by eqp_type,keydesc,series_no,update_time desc";

		String sql_sum = "select t1.key_parts_clean_id,t1.key_parts_id,t1.series_no,t1.life_type,t1.limit_life,t2.keydesc,t2.parts_id,t2.parts_name,t2.eqp_type "
				+ " from key_eqp_parts_clean t1 left join key_eqp_parts t2 on t1.key_parts_id=t2.key_parts_id where t1.enable='Y' ";
		sql_sum = sql_sum + sqlcondition + " order by t2.eqp_type,t2.keydesc,t1.series_no";

		try {
			List deptList = delegator.findAll("EquipmentDept");
			if (type.equals("detail")) {
				List keyPartsCleanDetailList = SQLProcess.excuteSQLQuery(sql_detal, delegator);
				request.setAttribute("keyPartsCleanDetailList", keyPartsCleanDetailList);
				request.setAttribute("dflag", "OK");
			} else if (type.equals("sum")) {
				List keyPartsCleanSumList = new ArrayList();
				List keyPartsCleanList = SQLProcess.excuteSQLQuery(sql_sum, delegator);
				if (keyPartsCleanList != null && keyPartsCleanList.size() > 0) {
					for (int i = 0; i < keyPartsCleanList.size(); i++) {
						String actul = "0";
						Map map = (Map) keyPartsCleanList.get(i);
						String sn = (String) map.get("SERIES_NO");
						String lifeType = (String) map.get("LIFE_TYPE");
						String limitLife = (String) map.get("LIMIT_LIFE");
						String keyPartsCleanId = (map.get("KEY_PARTS_CLEAN_ID") + "").trim();
						String sql_actul = "select nvl(floor(sysdate - t.update_time), '0') actul from key_parts_use t "
								+ "where t.key_parts_clean_id='" + keyPartsCleanId + "' and t.status='INUSE' ";
						List actulList = SQLProcess.excuteSQLQuery(sql_actul, delegator);
						if (actulList != null && actulList.size() > 0) {
							actul = (String) ((Map) actulList.get(0)).get("ACTUL");
						}
						String cleanPartsLife = PartsHelper.getCleanPartsLife(delegator, keyPartsCleanId, actul,
								lifeType);
						int cleanRemainLife = Integer.parseInt(limitLife) - Integer.parseInt(cleanPartsLife);
						String errorRst = "Y";
						if (cleanRemainLife > 0) {
							errorRst = "N";
						}
						map.put("cleanPartsLife", cleanPartsLife);
						map.put("cleanRemainLife", cleanRemainLife);
						map.put("errorRst", errorRst);
						if (isError == null || isError.equals("")) {
							keyPartsCleanSumList.add(map);
						} else if (isError != null && isError.equals("N") && cleanRemainLife > 0) {
							keyPartsCleanSumList.add(map);
						} else if (isError != null && isError.equals("Y") && cleanRemainLife <= 0) {
							keyPartsCleanSumList.add(map);
						}
					}
					Collections.sort(keyPartsCleanSumList, new Comparator<Map>() {
						public int compare(Map o1, Map o2) {
							int ret = 0;
							try {
								// 比较两个对象的顺序，如果前者小于、等于或者大于后者，则分别返回-1/0/1
								if (o1.get("errorRst").equals("N") && o2.get("errorRst").equals("N")) {
									ret = ((Integer) o1.get("cleanRemainLife"))
											.compareTo((Integer) o2.get("cleanRemainLife"));
								} else {
									ret = ((String) o1.get("errorRst")).compareTo((String) o2.get("errorRst"));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							return ret;
						}
					});
				}

				request.setAttribute("keyPartsCleanSumList", keyPartsCleanSumList);
				request.setAttribute("sflag", "OK");
			}
			request.setAttribute("deptList", deptList);
			request.setAttribute("deptIndex", deptIndex);
			request.setAttribute("model", model);
			request.setAttribute("keydesc", keydesc);
			request.setAttribute("seriesNo", seriesNo);
			request.setAttribute("isError", isError);

		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}

		return "success";
	}
}
