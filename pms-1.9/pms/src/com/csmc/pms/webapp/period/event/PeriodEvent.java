package com.csmc.pms.webapp.period.event;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.basic.event.BasicEvent;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.common.helper.CommonHelper;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.eqp.helper.GuiHelper;
import com.csmc.pms.webapp.period.help.PeriodHelper;
import com.csmc.pms.webapp.period.model.YearPeriod;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.GeneralEvents;
import com.csmc.pms.webapp.util.MiscUtils;
import com.csmc.pms.webapp.workflow.help.WorkflowHelper;

public class PeriodEvent extends GeneralEvents {
    public static final String module = PeriodEvent.class.getName();

    // --------------------------设备保养周期-------------------------------

    public static String defaultPeriodDefine(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            List equipmentTypeList = CommonHelper.getEquipmentTypeList(delegator);
            request.setAttribute("equipmentTypeList", equipmentTypeList);
        } catch (Exception e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * 设备保养周期一览画面:取得关于设备保养周期信息画面上显示
     * @param request
     *            eqpType 设备大类
     * @param response
     * @return String success/error
     */
    public static String defaultPeriodList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // java中传递的参数，用于维护当前设备大类保持上次输入的值
            String eqpType = (String) request.getAttribute("eqpType");

            // 页面上传递的参数
            String eqp_Type = UtilFormatOut.checkNull((String) request.getParameter("eqp_Type"));
            String holdPeriodIndex = UtilFormatOut.checkNull((String) request.getParameter("holdPeriodIndex"));

            // 如果两个值都有，合并取页面参数
            if (!StringUtils.isEmpty(eqp_Type)) {
                eqpType = eqp_Type;
            }

            // 查看此设备大类是否存在
            List equipmentTypeList = CommonHelper.getEquipmentTypeList(delegator);
            List promisEqpStatusList = delegator.findByAnd("PromisEqpStatus", UtilMisc.toMap("type", Constants.PM), UtilMisc.toList("eqpStatus"));
            List timeRangeList = delegator.findAllCache("TimeRange");

            List defaultPeriodList = null;
            if (StringUtils.isNotEmpty(holdPeriodIndex)) {
            	defaultPeriodList = delegator.findByAnd("DefaultPeriod", UtilMisc.toMap("eqpType", eqpType, "periodIndex", holdPeriodIndex), UtilMisc.toList("periodName"));
            	
            	//fab1获得hold码，hold原因 
                if (Constants.CALL_TP_FLAG) {
                	GenericValue userInfo = AccountHelper.getUserInfo(request, delegator);
                    String accountDept = userInfo.getString("accountDept");
                	List holdCodeReasonList = GuiHelper.getHoldCodeReasonList(accountDept);
                	request.setAttribute("holdCodeReasonList", holdCodeReasonList);
                	
                	GenericValue holdPeriod = delegator.findByPrimaryKey("DefaultPeriod", UtilMisc.toMap("periodIndex", holdPeriodIndex));
                	request.setAttribute("holdPeriod", holdPeriod);
                	request.setAttribute("holdPeriodIndex", holdPeriodIndex);
                }
            } else {
            	defaultPeriodList = delegator.findByAnd("DefaultPeriod", UtilMisc.toMap("eqpType", eqpType), UtilMisc.toList("periodName"));
            }
            
            request.setAttribute("defaultPeriodList", defaultPeriodList);
            request.setAttribute("promisEqpStatusList", promisEqpStatusList);
            request.setAttribute("equipmentTypeList", equipmentTypeList);
            request.setAttribute("timeRangeList", timeRangeList);
            request.setAttribute("eqpType", eqpType);

            // 判断参数页面是否显示的flag
            request.setAttribute("flag", "OK");
            
            //如果登录人为质量保证部(dept_index == 10010)，则周期命名必须以MSA开头
            request.setAttribute("isMsaDept", AccountHelper.isMsaDept(request, delegator) ? "true" : "false");            
            
        } catch (Exception e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
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
    public static String defaultPeriodHold(HttpServletRequest request, HttpServletResponse response) {
		// 画面上传递的所有参数组合成Map
		Map paramMap = getInitParams(request, true, true);
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String eventMsg = "";
		
		String eqpType = UtilFormatOut.checkNull((String) request.getParameter("eqp_Type"));
		String holdPeriodIndex = UtilFormatOut.checkNull((String) request.getParameter("holdPeriodIndex"));
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
		    paramMap.put("periodIndex", holdPeriodIndex);
		    GenericValue holdGv = delegator.makeValidValue("DefaultPeriod", paramMap);
		    delegator.store(holdGv);
		
		    // 显示设备保养周期一览的flag
		    request.setAttribute("flag", "OK");
		    request.setAttribute("eqpType", eqpType);
		    request.setAttribute("_EVENT_MESSAGE_", eventMsg);
		} catch (Exception e) {
		    Debug.logError(e, module);
		    request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		    return "error";
		}
        return "success";
    }

    /**
     * 设备保养周期新建或保存
     * @param request
     *            periodIndex，equipmentId，periodName,periodDesc,eqpType,
     *            defaultDays,standardHour,eqpStatus,warningDays,isUpdatePromis
     * @param response
     * @return String success/error
     */
    public static String manageDefaultPeriod(HttpServletRequest request, HttpServletResponse response) {
		// 画面上传递的所有参数组合成Map
		Map paramMap = BasicEvent.getInitParams(request, true, true);
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try {
		    // 类型赋值，分为PM/PC
		    paramMap.put("event", "PM");
		
		    // 取得用户
		    GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		    String user = userLogin.getString("userLoginId");
		    paramMap.put("transBy", user);
		
		    // 更新时间赋值
		    paramMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
		
		    // 1.将信息写入设备保养周期表
		    PeriodHelper.createDefaultPeriod(delegator, paramMap);
		    
		    // 2.修改时更新
		    if (paramMap.get("periodIndex") != null) {
		    	PeriodHelper.updatePmNextStarttime(delegator, paramMap);
	        }
		
		    // 显示机设备保养周期一览的flag
		    request.setAttribute("flag", "OK");
		    request.setAttribute("eqpType", paramMap.get("eqpType"));
		    request.setAttribute("_EVENT_MESSAGE_", "保存成功！");

		} catch (GenericEntityException e) {
		    String message = CommonUtil.checkOracleException(e);
		    request.setAttribute("_ERROR_MESSAGE_", message);
		} catch (Exception e) {
		    Debug.logError(e, module);
		    request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		    return "error";
		}
        return "success";
    }

    /**
     * 删除设备保养周期，根据主键
     * 
     * @param request
     *            periodIndex，eqpType
     * @param response
     * @return String : success/error
     */
    public static String delDefaultPeriod(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // 取得页面参数
        String periodIndex = request.getParameter("periodIndex");
        String eqpType = request.getParameter("eqpType");
        try {
            // 删除此设备保养类型
        	if(!PeriodHelper.checkPmPeriod(delegator, periodIndex)) {
        		PeriodHelper.delDefaultPeriod(delegator, periodIndex);
        		request.setAttribute("_EVENT_MESSAGE_", "删除成功！");
        	} else {
        		request.setAttribute("_ERROR_MESSAGE_", "该周期已在行事历上设定，不能删除!");
        	}

            request.setAttribute("eqpType", eqpType);
            
        } catch (Exception e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * 画面上的编辑按钮查询值
     * 
     * @param request
     *            periodIndex
     * @param response
     */
    public static void queryDefaultPeriodByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // seqIndex取得
        String periodIndex = request.getParameter("periodIndex");

        try {
            // 取得机台参数信息
            GenericValue gv = delegator.findByPrimaryKey("DefaultPeriod", UtilMisc.toMap("periodIndex", periodIndex));
            JSONObject defaultPeriodParam = new JSONObject();
            defaultPeriodParam.put("eqpType", gv.getString("eqpType"));
            defaultPeriodParam.put("periodName", gv.getString("periodName"));
            defaultPeriodParam.put("periodDesc", gv.getString("periodDesc"));
            defaultPeriodParam.put("defaultDays", gv.getString("defaultDays"));
            defaultPeriodParam.put("standardHour", gv.getString("standardHour"));
            defaultPeriodParam.put("warningDays", gv.getString("warningDays"));
            defaultPeriodParam.put("eqpStatus", gv.getString("eqpStatus"));
            defaultPeriodParam.put("isUpdatePromis", gv.getString("isUpdatePromis"));
            defaultPeriodParam.put("timeRangeIndex", gv.getString("timeRangeIndex"));
            defaultPeriodParam.put("enabled", gv.getString("enabled"));
            defaultPeriodParam.put("isContainSmallPm", gv.getString("isContainSmallPm"));

            // 写入response
            response.getWriter().write(defaultPeriodParam.toString());
        } catch (Exception e) {
            Debug.logError(e, module);
        }
    }

    // -----------------------------------------------------设备保养日期设定----------------------------

    /**
     * 设备保养日期设定初始画面
     * 
     * @param request
     * @param response
     * @return
     */
    public static String equipmentSchedule(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // 取得用户
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");

            GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));
            String deptIndex = account.getString("deptIndex");
            //如果登录人为质量保证部(dept_index == 10010)，显示(MSA==”Y”)的设备
            if(deptIndex!=null && deptIndex.equals(Constants.DEPT_QC_INDEX)){
                List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("msa", "Y"), UtilMisc.toList("equipmentId"));
                request.setAttribute("equipmentList", equipmentList);
            }
            else{
                List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("maintDept", account.getString("accountDept")), UtilMisc.toList("equipmentId"));
                request.setAttribute("equipmentList", equipmentList);
            }
        } catch (Exception e) {
            Debug.logError(e, module);
        }

        return "success";
    }

    /**
     * 设备保养日期设定显示
     * @param request
     * @param response
     * @return
     */
    public static String equipmentScheduleDefine(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
        	
            // 取得用户部门
        	GenericValue userInfo = AccountHelper.getUserInfo(request,delegator);
        	String maintDept = userInfo.getString("accountDept");
        	request.setAttribute("maintDept", maintDept);
        	List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
            request.setAttribute("deptList", deptList);
        	
	        // 得到参数--------->equipmentId设备
	        String equipmentId = (String) request.getParameter("equipmentId");
	        if (!StringUtils.isEmpty((String) request.getAttribute("equipmentId"))) {
	            equipmentId = (String) request.getAttribute("equipmentId");
	        }
	        if (equipmentId == null || equipmentId.equals(""))
	        {
	        	return "success";
	        }
	
	        // 得到参数------->periodIndex保养类型
	        String periodIndex = (String) request.getParameter("periodIndex");
	        if (!StringUtils.isEmpty((String) request.getAttribute("periodIndex"))) {
	            periodIndex = (String) request.getAttribute("periodIndex");
	        }
	
	        // 得到参数-------->年份
	        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
	        if (!StringUtils.isEmpty(request.getParameter("setup_year"))) {
	            thisYear = Integer.parseInt(request.getParameter("setup_year"));
	        }
	
	        // 定义画面显示的无保养颜色，和有保养得三种分别显示颜色
	        String maintenancecolor1 = "#40FF60";
	        String maintenancecolor2 = "#FFFF00";
	        String maintenancecolor3 = "#B0B000";
	        String background = "#E8E8FF";
	
	        // 生成颜色的HashMap ColorMap
	        Map colorMap = PeriodHelper.getColorMap(maintenancecolor1, maintenancecolor2, maintenancecolor3, background);
	
	        // 得到星期的title显示
	        String[] daystitle = PeriodHelper.getWeekArr();
	
	        // 取得所给年份的每月天数
	        int[] monthdays = PeriodHelper.getMonthArr(thisYear);
	
	        Calendar firstDay = Calendar.getInstance();
	        firstDay.set(thisYear, 0, 1);
	        
	        List timeRangeList = new ArrayList();            
            List scheduleList = new ArrayList();
            for (int i = 1; i <= 12; i++) {
                // scheduleMap存放每月的各类信息
                HashMap scheduleMap = new HashMap();

                // 取得当月首日的日期信息
                Calendar day = Calendar.getInstance();
                day = firstDay;

                // 设定第一个日期
                scheduleMap.put("firstDay", day);

                // 设定第一天处于当周的第几天
                scheduleMap.put("startDay", new Integer(day.get(Calendar.DAY_OF_WEEK)));

                // 设定title
                String title = PeriodHelper.getMonthStr(day);
                scheduleMap.put("title", title);

                // 从0开始
                int month = day.get(Calendar.MONTH);
                scheduleMap.put("month", new Integer(month));

                // 设定当月多少天
                scheduleMap.put("ndays", new Integer(monthdays[day.get(Calendar.MONTH) + 1]));

                // 设定description
                String description = "";
                if (!StringUtils.isEmpty(periodIndex)) {
                    GenericValue dpgv = (GenericValue) delegator.findByPrimaryKey("DefaultPeriod", UtilMisc.toMap("periodIndex", periodIndex));
                    description = dpgv.getString("periodDesc");
                    
                    if("1".equalsIgnoreCase(dpgv.getString("defaultDays")))
                    {
                        timeRangeList = delegator.findAll("TimeRange");
                    }
                    
                }
                scheduleMap.put("description", description);

                // 生成与时间相关的四个Map
                Map timeMap = new HashMap();
                Map periodMap = new HashMap();
                Map creatorMap = new HashMap();
                Map isFinishedMap = new HashMap();
                Map timeRangeMap = new HashMap();

                // 设定以上四个Map
                PeriodHelper.setPmTimeMap(delegator, equipmentId, periodIndex, thisYear, maintenancecolor1, maintenancecolor2, maintenancecolor3, background, monthdays, day, month, timeMap,
                        periodMap, creatorMap, isFinishedMap, timeRangeMap);

                scheduleMap.put("timeMap", timeMap);
                scheduleMap.put("periodMap", periodMap);
                scheduleMap.put("creatorMap", creatorMap);
                scheduleMap.put("isFinishedMap", isFinishedMap);
                scheduleMap.put("timeRangeMap", timeRangeMap);

                scheduleList.add(scheduleMap);
                if (i != 12) {
                    firstDay.add(Calendar.MONTH, 1);
                }
            }

            // 保养周期
            GenericValue eqpgv = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId", equipmentId));
            List equipmentPeriodList = delegator.findByAnd("DefaultPeriod", UtilMisc.toMap("eqpType", eqpgv.getString("equipmentType"), "enabled", new Integer(Constants.ENABLE)), UtilMisc.toList("periodName"));

            // 设入request
            request.setAttribute("scheduleList", scheduleList);
            request.setAttribute("equipmentId", equipmentId);
            request.setAttribute("periodIndex", periodIndex);
            request.setAttribute("equipmentPeriodList", equipmentPeriodList);
            request.setAttribute("thisYear", String.valueOf(thisYear));
            request.setAttribute("colorMap", colorMap);
            request.setAttribute("daystitle", daystitle);
            request.setAttribute("timeRangeList", timeRangeList);
            
            if (StringUtils.isNotEmpty(periodIndex)) {
	            //查询工艺课长
				List ownerProcessList = WorkflowHelper.getProcessSectionLeaderList(delegator);
				request.setAttribute("ownerProcessList", ownerProcessList);
				
				//按设备和周期，查询签核中的计划延期申请
				List wfSubmitList = PeriodHelper.getPmDelaySubmitList(delegator,equipmentId, periodIndex, null);
				request.setAttribute("wfSubmitList", wfSubmitList);
            }
			
        } catch (Exception e) {
            Debug.logError(e, module);
        }

        return "success";
    }

	/**
     * 单个保养日期的设定验证，已改为提交后验证
     * 周期性保养日期的设定 不验证
     * @param request
     * @param response
     * @return
     */
    public static void validEquipmentScheduleAdd(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        JSONObject result = new JSONObject();
        
        try {
            String equipmentId = request.getParameter("equipmentId");
            String year = request.getParameter("form_year");
            String month = request.getParameter("form_month");
            String day = request.getParameter("form_day");
            String periodIndex = request.getParameter("periodIndex");
            
            result.put("addEnabled", PeriodHelper.equipmentScheduleAddEnabled(delegator, equipmentId, periodIndex, year + "-" + month + "-" + day));
            
            response.getWriter().write(result.toString());
        } catch (Exception e) {
        	Debug.logError(e, module);        	
            result.put("addEnabled", Constants.N);
            try{
            	response.getWriter().write(result.toString());
            } catch (IOException io) {
            	Debug.logError(io.getMessage(), module);
            }
        }        
    }

    /**
     * 保养日期的添加
     * @param request
     * @param response
     * @return
     */
    public static String equipmentScheduleAdd(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {        	
            String equipmentId = request.getParameter("equipmentId");
            String year = request.getParameter("form_year");
            String month = request.getParameter("form_month");
            String day = request.getParameter("form_day");
            String periodIndex = request.getParameter("periodIndex");
            String flag = request.getParameter("flag");
            String timeRangeIndex = request.getParameter("timeRangeIndex");
            String scheduleDate = year + "-" + month + "-" + day;
            
            Debug.logInfo("eqpSchedule add [" + equipmentId + "/" + periodIndex + "/" + scheduleDate + "]", module);
            GenericValue defaultPeriod = delegator.findByPrimaryKey("DefaultPeriod", UtilMisc.toMap("periodIndex", periodIndex));

            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));

            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");
            
            if ("1".equalsIgnoreCase(flag)) {
            	// 按周期循环设定计划日期
                while (cal.get(Calendar.YEAR) == Integer.parseInt(year)) {
                    if (StringUtils.isEmpty(defaultPeriod.getString("defaultDays")) || defaultPeriod.getLong("defaultDays").intValue() == 0) {
                    	break;
                    } else {                        
                        defaultPeriod.getString("defaultDays");
                        PeriodHelper.pmDaySet(delegator, equipmentId, cal.getTime(), periodIndex, user,timeRangeIndex);
                        cal.add(Calendar.DATE, Integer.parseInt(defaultPeriod.getString("defaultDays")));
                    }

                }
            } else {//单个新增先验证
            	if (Constants.Y.equals(PeriodHelper.equipmentScheduleAddEnabled(delegator, equipmentId, periodIndex, scheduleDate))) {
            		PeriodHelper.pmDaySet(delegator, equipmentId, cal.getTime(), periodIndex,user,timeRangeIndex);
            	} else {
            		request.setAttribute("_ERROR_MESSAGE_", "当前设定日期 超出 警示天数，如需调整，请提交修改申请，并通知设备与工艺主管签核。");
            	}
            }
            
            request.setAttribute("equipmentId", equipmentId);
            request.setAttribute("periodIndex", periodIndex);
        } catch (Exception e) {
            Debug.logError(e, module);
        }
        return "success";
    }
    
    /**
     * 单个保养日期的删除验证，已改为提交后验证
     * 周期性保养日期的删除 验证
     * @param request
     * @param response
     * @return
     */
    public static void validEquipmentScheduleClear(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        JSONObject result = new JSONObject();
        String clearEnabled = Constants.Y;
        
        try {
            String equipmentId = request.getParameter("equipmentId");
            String year = request.getParameter("form_year");
            String month = request.getParameter("form_month");
            String day = request.getParameter("form_day");
            String periodIndex = request.getParameter("periodIndex");
            String flag = request.getParameter("flag");
            String formDate = year + "-" + month + "-" + day;

            GenericValue defaultPeriod = delegator.findByPrimaryKey("DefaultPeriod", UtilMisc.toMap("periodIndex", periodIndex));
            String defaultDays = defaultPeriod.getString("defaultDays");
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));

            if ("1".equalsIgnoreCase(flag)) {//周期性            	
                while (cal.get(Calendar.YEAR) == Integer.parseInt(year)) {
                    if (StringUtils.isEmpty(defaultDays) || "0".equals(defaultDays)) {
                    	break;
                    } else {
                    	List equipmentScheduleList = delegator.findByAnd("EquipmentSchedule", UtilMisc.toMap("equipmentId", equipmentId, "periodIndex", periodIndex, "scheduleDate", new java.sql.Date(cal.getTimeInMillis())));
                        
                        //年度最后一个计划不校验
                    	cal.add(Calendar.DATE, Integer.parseInt(defaultDays));
                        if (cal.get(Calendar.YEAR) == Integer.parseInt(year) && equipmentScheduleList.size() > 0) {
                        	clearEnabled = PeriodHelper.equipmentScheduleClearEnabled(delegator, equipmentId, periodIndex, formDate);
                        	if (Constants.N.equalsIgnoreCase(clearEnabled)) {
                            	break;
                            }
                        }
                        month = String.valueOf(cal.get(Calendar.MONTH) + 1);
                        day = String.valueOf(cal.get(Calendar.DATE));
                        formDate = year + "-" + month + "-" + day;
                    }
                }
            } else {//单个
            	clearEnabled = PeriodHelper.equipmentScheduleClearEnabled(delegator, equipmentId, periodIndex, formDate);
            }            
            
            result.put("clearEnabled", clearEnabled);
            response.getWriter().write(result.toString());
        } catch (Exception e) {
        	Debug.logError(e, module);
        	
            result.put("clearEnabled", Constants.N);
            try{
            	response.getWriter().write(result.toString());
            } catch (IOException io) {
            	Debug.logError(io.getMessage(), module);
            }
        }        
    }

    /**
     * 保养日期的删除
     * @param request
     * @param response
     * @return
     */
    public static String equipmentScheduleClear(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            String equipmentId = request.getParameter("equipmentId");
            String year = request.getParameter("form_year");
            String month = request.getParameter("form_month");
            String day = request.getParameter("form_day");
            String periodIndex = request.getParameter("periodIndex");
            String flag = request.getParameter("flag");

            GenericValue defaultPeriod = delegator.findByPrimaryKey("DefaultPeriod", UtilMisc.toMap("periodIndex", periodIndex));

            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");

            if ("1".equalsIgnoreCase(flag)) {//周期性删除
                while (cal.get(Calendar.YEAR) == Integer.parseInt(year)) {
                    if (StringUtils.isEmpty(defaultPeriod.getString("defaultDays")) || defaultPeriod.getLong("defaultDays").intValue() == 0) {
                    	break;
                    } else {                        
                        PeriodHelper.pmDayReset(delegator, equipmentId, cal.getTime(), periodIndex, user);
                        cal.add(Calendar.DATE, Integer.parseInt(defaultPeriod.getString("defaultDays")));
                    }
                }
            } else {//单个删除先验证
            	String scheduleDate = year + "-" + month + "-" + day;
            	if (Constants.Y.equals(PeriodHelper.equipmentScheduleClearEnabled(delegator, equipmentId, periodIndex, scheduleDate))) {
            		List wfSubmitList = PeriodHelper.getPmDelaySubmitList(delegator,equipmentId, periodIndex, scheduleDate);
            		if (wfSubmitList.size() >= 1) {
                		request.setAttribute("_ERROR_MESSAGE_", "当前计划已提交延期申请，无法删除，请通知设备与工艺主管签核。");
                	} else {
                		PeriodHelper.pmDayReset(delegator, equipmentId, cal.getTime(), periodIndex, user);
                	}
            	} else {
            		request.setAttribute("_ERROR_MESSAGE_", "当前计划不可删除，如需调整，请提交修改申请，并通知设备与工艺主管签核。");
            	}
            }
            request.setAttribute("equipmentId", equipmentId);
            request.setAttribute("periodIndex", periodIndex);
        } catch (Exception e) {
            Debug.logError(e, module);
        }
        return "success";
    }

    // --------------------------------巡检保养周期的设定-----------------------
    public static String pcPeriodDefine(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            List pcStyleList = delegator.findAll("PcStyle", UtilMisc.toList("name"));
            request.setAttribute("pcStyleList", pcStyleList);
        } catch (Exception e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * 巡检周期一览画面:取得关于巡检周期信息画面上显示
     * 
     * @param request
     *            styleIndex---->PcStyle
     * @param response
     * @return String success/error
     */
    public static String pcPeriodList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // java中传递的参数，用于维护当前pcStyle保持上次输入的值
            String styleIndex = (String) request.getAttribute("styleIndex");

            // 页面上传递的参数
            String style_Index = (String) request.getParameter("style_Index");

            // 如果两个值都有，合并
            if (!StringUtils.isEmpty(style_Index)) {
                styleIndex = style_Index;
            }

            // 查看此巡检类别是否存在
            List pcStyleList = delegator.findAll("PcStyle");

            // 存在，取得此巡检类别的信息
            List pcPeriodList = delegator.findByAnd("PcPeriod", UtilMisc.toMap("styleIndex", styleIndex), UtilMisc.toList("periodName"));
            request.setAttribute("pcPeriodList", pcPeriodList);
            request.setAttribute("pcStyleList", pcStyleList);
            request.setAttribute("styleIndex", styleIndex);

            // 判断参数页面是否显示的flag
            request.setAttribute("flag", "OK");

        } catch (Exception e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * 巡检周期新建或保存
     * 
     * @param request
     *            periodIndex，styleIndex，periodName,periodDesc,eqpType,
     *            defaultDays,standardHour
     * @param response
     * @return String success/error
     */
    public static String managePcPeriod(HttpServletRequest request, HttpServletResponse response) {
        // 画面上传递的所有参数组合成Map
        Map paramMap = BasicEvent.getInitParams(request, true, true);
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // 类型赋值，分为PM/PC
            paramMap.put("event", "PC");

            // 取得用户
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");
            paramMap.put("transBy", user);

            // 更新时间赋值
            paramMap.put("updateTime", new Timestamp(System.currentTimeMillis()));

            // 将信息写入巡检周期表
            PeriodHelper.createPcPeriod(delegator, paramMap);

            // 显示巡检周期一览的flag
            request.setAttribute("flag", "OK");
            request.setAttribute("styleIndex", paramMap.get("styleIndex"));
            request.setAttribute("_EVENT_MESSAGE_", "保存成功！");

        }catch (GenericEntityException e) {
            String message = CommonUtil.checkOracleException(e);
            request.setAttribute("_ERROR_MESSAGE_", message);
         } catch (Exception e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * 删除巡检周期，根据主键
     * 
     * @param request
     *            periodIndex，styleIndex
     * @param response
     * @return String : success/error
     */
    public static String delPcPeriod(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // 取得页面参数
        String periodIndex = request.getParameter("periodIndex");
        String styleIndex = request.getParameter("styleIndex");
        try {
            // 删除此巡检类型
        	if(!PeriodHelper.checkPcPeriod(delegator, periodIndex)) {
        		PeriodHelper.delPcPeriod(delegator, periodIndex);
        		request.setAttribute("_EVENT_MESSAGE_", "删除成功！");
        	} else {
        		request.setAttribute("_ERROR_MESSAGE_", "该周期已在行事历上设定，不能删除！");
        	}

            request.setAttribute("styleIndex", styleIndex);
            
        } catch (Exception e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * 画面上的编辑按钮查询值
     * 
     * @param request
     *            periodIndex
     * @param response
     */
    public static void queryPcPeriodByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // periodIndex取得
        String periodIndex = request.getParameter("periodIndex");

        try {
            // 取得巡检信息
            GenericValue gv = delegator.findByPrimaryKey("PcPeriod", UtilMisc.toMap("periodIndex", periodIndex));
            JSONObject defaultPeriodParam = new JSONObject();
            defaultPeriodParam.put("styleIndex", gv.getString("styleIndex"));
            defaultPeriodParam.put("periodName", gv.getString("periodName"));
            defaultPeriodParam.put("periodDesc", gv.getString("periodDesc"));
            defaultPeriodParam.put("defaultDays", gv.getString("defaultDays"));
            defaultPeriodParam.put("standardHour", gv.getString("standardHour"));
            defaultPeriodParam.put("enabled", gv.getString("enabled"));

            // 写入response
            response.getWriter().write(defaultPeriodParam.toString());
        } catch (Exception e) {
            Debug.logError(e, module);
        }
    }

    // --------------------------------巡检保养日期的设定-----------------------
    /**
     * 巡检保养日期设定初始画面
     * 
     * @param request
     * @param response
     * @return
     */
    public static String pcSchedule(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            List pcStyleList = delegator.findAll("PcStyle", UtilMisc.toList("name"));
            request.setAttribute("pcStyleList", pcStyleList);
        } catch (Exception e) {
            Debug.logError(e, module);
        }

        return "success";
    }

    /**
     * 巡检保养日期设定显示
     * 
     * @param request
     * @param response
     * @return
     */
    public static String pcScheduleDefine(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // 得到参数--------->styleIndex---pcStyle
        String styleIndex = (String) request.getParameter("styleIndex");
        if (!StringUtils.isEmpty((String) request.getAttribute("styleIndex"))) {
            styleIndex = (String) request.getAttribute("styleIndex");
        }

        // 得到参数------->periodIndex保养类型
        String periodIndex = (String) request.getParameter("periodIndex");
        if (!StringUtils.isEmpty((String) request.getAttribute("periodIndex"))) {
            periodIndex = (String) request.getAttribute("periodIndex");
        }

        // 得到参数-------->年份
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        if (!StringUtils.isEmpty(request.getParameter("setup_year"))) {
            thisYear = Integer.parseInt(request.getParameter("setup_year"));
        }

        // 定义画面显示的无保养颜色，和有保养得三种分别显示颜色
        String maintenancecolor1 = "#40FF60";
        String maintenancecolor2 = "#FFFF00";
        String maintenancecolor3 = "#B0B000";
        String background = "#E8E8FF";

        // 生成颜色的HashMap ColorMap
        Map colorMap = PeriodHelper.getColorMap(maintenancecolor1, maintenancecolor2, maintenancecolor3, background);

        // 得到星期的title显示
        String[] daystitle = PeriodHelper.getWeekArr();

        // 取得所给年份的每月天数
        int[] monthdays = PeriodHelper.getMonthArr(thisYear);

        Calendar firstDay = Calendar.getInstance();
        firstDay.set(thisYear, 0, 1);

        try {
            List scheduleList = new ArrayList();
            for (int i = 1; i <= 12; i++) {
                // scheduleMap存放每月的各类信息
                HashMap scheduleMap = new HashMap();

                // 取得当月首日的日期信息
                Calendar day = Calendar.getInstance();
                day = firstDay;

                // 设定第一个日期
                scheduleMap.put("firstDay", day);

                // 设定第一天处于当周的第几天
                scheduleMap.put("startDay", new Integer(day.get(Calendar.DAY_OF_WEEK)));

                // 设定title
                String title = PeriodHelper.getMonthStr(day);
                scheduleMap.put("title", title);

                // 从0开始
                int month = day.get(Calendar.MONTH);
                scheduleMap.put("month", new Integer(month));

                // 设定当月多少天
                scheduleMap.put("ndays", new Integer(monthdays[day.get(Calendar.MONTH) + 1]));

                // 设定description
                String description = "";
                if (!StringUtils.isEmpty(periodIndex)) {
                    GenericValue dpgv = (GenericValue) delegator.findByPrimaryKey("PcPeriod", UtilMisc.toMap("periodIndex", periodIndex));
                    description = dpgv.getString("periodDesc");
                }
                scheduleMap.put("description", description);

                // 生成与时间相关的四个Map
                Map timeMap = new HashMap();
                Map periodMap = new HashMap();
                Map creatorMap = new HashMap();
                Map isFinishedMap = new HashMap();

                // 设定以上四个Map
                PeriodHelper.setPcTimeMap(delegator, styleIndex, periodIndex, thisYear, maintenancecolor1, maintenancecolor2, maintenancecolor3, background, monthdays, day, month, timeMap, periodMap,
                        creatorMap, isFinishedMap);

                scheduleMap.put("timeMap", timeMap);
                scheduleMap.put("periodMap", periodMap);
                scheduleMap.put("creatorMap", creatorMap);
                scheduleMap.put("isFinishedMap", isFinishedMap);

                scheduleList.add(scheduleMap);
                if (i != 12) {
                    firstDay.add(Calendar.MONTH, 1);
                }
            }

            // 巡检周期List
            List pcPeriodList = delegator.findByAnd("PcPeriod", UtilMisc.toMap("styleIndex", styleIndex, "enabled", new Integer(Constants.ENABLE)), UtilMisc.toList("periodName"));
            GenericValue pcgv = delegator.findByPrimaryKey("PcStyle", UtilMisc.toMap("styleIndex", styleIndex));
            String styleName = pcgv.getString("name");

            // 设入request
            request.setAttribute("scheduleList", scheduleList);
            request.setAttribute("styleIndex", styleIndex);
            request.setAttribute("styleName", styleName);
            request.setAttribute("periodIndex", periodIndex);
            request.setAttribute("pcPeriodList", pcPeriodList);
            request.setAttribute("thisYear", String.valueOf(thisYear));
            request.setAttribute("colorMap", colorMap);
            request.setAttribute("daystitle", daystitle);
        } catch (Exception e) {
            Debug.logError(e, module);
        }

        return "success";
    }

    /**
     * 巡检日期的添加
     * 
     * @param request
     * @param response
     * @return
     */
    public static String pcScheduleAdd(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            String styleIndex = request.getParameter("styleIndex");
            String year = request.getParameter("form_year");
            String month = request.getParameter("form_month");
            String day = request.getParameter("form_day");
            String periodIndex = request.getParameter("periodIndex");
            String flag = request.getParameter("flag");
            GenericValue pcPeriod = delegator.findByPrimaryKey("PcPeriod", UtilMisc.toMap("periodIndex", periodIndex));

            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));

            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");
            if ("1".equalsIgnoreCase(flag)) {
                while (cal.get(Calendar.YEAR) == Integer.parseInt(year)) {
                    if (StringUtils.isEmpty(pcPeriod.getString("defaultDays")) || pcPeriod.getLong("defaultDays").intValue() == 0) {
                    	break;
                    } else {                        
                        PeriodHelper.pcDaySet(delegator, styleIndex, cal.getTime(), periodIndex, user);
                        cal.add(Calendar.DATE, Integer.parseInt(pcPeriod.getString("defaultDays")));
                    }

                }
            } else {
                PeriodHelper.pcDaySet(delegator, styleIndex, cal.getTime(), periodIndex, user);
            }
            request.setAttribute("styleIndex", styleIndex);
            request.setAttribute("periodIndex", periodIndex);
        } catch (Exception e) {
            Debug.logError(e, module);
        }
        return "success";
    }

    /**
     * 巡检日期的删除
     * 
     * @param request
     * @param response
     * @return
     */
    public static String pcScheduleClear(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            String styleIndex = request.getParameter("styleIndex");
            String year = request.getParameter("form_year");
            String month = request.getParameter("form_month");
            String day = request.getParameter("form_day");
            String periodIndex = request.getParameter("periodIndex");
            String flag = request.getParameter("flag");

            GenericValue defaultPeriod = delegator.findByPrimaryKey("PcPeriod", UtilMisc.toMap("periodIndex", periodIndex));

            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");

            if ("1".equalsIgnoreCase(flag)) {
                while (cal.get(Calendar.YEAR) == Integer.parseInt(year)) {
                    if (StringUtils.isEmpty(defaultPeriod.getString("defaultDays")) || defaultPeriod.getLong("defaultDays").intValue() == 0) {
                    	break;
                    } else {                        
                        PeriodHelper.pcDayReset(delegator, styleIndex, cal.getTime(), periodIndex, user);
                        cal.add(Calendar.DATE, Integer.parseInt(defaultPeriod.getString("defaultDays")));
                    }

                }
            } else {
                PeriodHelper.pcDayReset(delegator, styleIndex, cal.getTime(), periodIndex, user);
            }
            request.setAttribute("styleIndex", styleIndex);
            request.setAttribute("periodIndex", periodIndex);
        } catch (Exception e) {
            Debug.logError(e, module);
        }
        return "success";
    }

    /**
     * 年度PM计划生成初始化
     * 
     * @param request
     * @param response
     * @return
     */
    public static String yearPeriod(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // 取得用户
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");

            GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));
            String accountSection = account.getString("accountSection");
            String accountName = account.getString("accountName");

            request.setAttribute("accountSection", accountSection);
            request.setAttribute("accountNo", user);
            request.setAttribute("accountName", accountName);
            request.setAttribute("month", new Integer(Calendar.getInstance().get(Calendar.MONTH)));
        } catch (Exception e) {
            Debug.logError(e, module);
        }
        return "success";
    }

    public static String yearPeriodSchedule(HttpServletRequest request, HttpServletResponse response) {
    	String accountNo = request.getParameter("accountNo");
    	String accountSection = request.getParameter("accountSection");
    	String year = request.getParameter("year");
    	GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
    	boolean callProcedure = false;
    	try {
    		Long seqIndex = null;
    		List yearDetailList = delegator.findByAnd("PmNextYearDetail", UtilMisc.toMap("section", accountSection, "year", year));
    		if(CommonUtil.isNotEmpty(yearDetailList)) {
    			GenericValue gv = (GenericValue)yearDetailList.get(0);
    			int status = gv.getInteger("status").intValue();
    			if(status == Constants.START) {
    				seqIndex = gv.getLong("seqIndex");
    				callProcedure = true;
    			} else if(status == Constants.HOLD) {
    				request.setAttribute("_EVENT_MESSAGE_", year + "年计划生成中，请稍后等邮件通知！");
    				return "error";
    			} else if(status == Constants.OVER) {
    				request.setAttribute("_EVENT_MESSAGE_", year + "年计划已经完成，请在行事历中查看调整！");
    				return "error";
    			}
    		} else {    		
    			callProcedure = true;
	    		Map yearDetail = new HashMap();
	    		seqIndex = delegator.getNextSeqId("pmNextYearDetail");
	    		yearDetail.put("seqIndex", seqIndex);
	    		yearDetail.put("section", accountSection);
	    		yearDetail.put("year", year);
	    		yearDetail.put("status", new Integer(Constants.START));
	    		yearDetail.put("transBy", accountNo);
	    		yearDetail.put("updateTime", new Timestamp(System.currentTimeMillis()));
	    		GenericValue gv = delegator.makeValue("PmNextYearDetail", yearDetail);
	    		delegator.create(gv);
    		}
    		//call Procedure 生成nextYear数据
    		if(callProcedure) {
    			YearPeriod yearPeriod = new YearPeriod();
    			yearPeriod.setAccountNo(accountNo);
    			yearPeriod.setDelegator(delegator);
    			yearPeriod.setNextYear(Long.valueOf(year));
    			yearPeriod.setSeqIndex(seqIndex);
    			Thread thread = new Thread(yearPeriod);
    			
    			Debug.logInfo("Start Call Procedure nextYear Pm [" + accountSection + "]", module);
    			thread.start();
    			Debug.logInfo("end Call Procedure nextYear Pm [" + accountSection + "]", module);
    			
    			request.setAttribute("_EVENT_MESSAGE_", "正在后台生成下一年度保养计划...");
    		}
    	} catch(Exception e) {
    		Debug.logError(e.getMessage(),module);
    		return "error";
    	}
    	
    	return "success";
    }
    
    /**
     * 下年度PM计划的生成
     * @param request
     * @param response
     * @return
     */
    public static String yearPeriodScheduleBk(HttpServletRequest request, HttpServletResponse response) {
        // 取得页面参数
        String accountSection = request.getParameter("accountSection");
        String accountName = request.getParameter("accountName");
        String accountNo = request.getParameter("accountNo");
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // 取得当前月份与生成PM的年度
            int year;
            int month;
            Calendar now = Calendar.getInstance();
            month = now.get(Calendar.MONTH);
            if (month == 0) {
                year = now.get(Calendar.YEAR);
            } else {
                year = now.get(Calendar.YEAR) + 1;
            }

            // 得到用户所属课别下的所有设备
            List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("section", accountSection));
            for (int i = 0; i < equipmentList.size(); i++) {
                // 取得设备资料
                GenericValue equipment = (GenericValue) equipmentList.get(i);
                String equipmentId = equipment.getString("equipmentId");
                String equipmentType = equipment.getString("equipmentType");

                // 得到此设备下的全部保养类型
                List defaultPeriodList = delegator.findByAnd("DefaultPeriod", UtilMisc.toMap("eqpType", equipmentType));
                for (int j = 0; j < defaultPeriodList.size(); j++) {
                    GenericValue defaultPeriod = (GenericValue) defaultPeriodList.get(j);                                      
                    if ("1".equals(defaultPeriod.getString("enabled"))) {                                                
                        String periodIndex = defaultPeriod.getString("periodIndex");

                        // 取得此此设备，此类型中最后一天做PM的日期
                        String sql = "select  t1.schedule_date, t1.time_Range_Index from equipment_schedule t1 where  t1.schedule_date = (select max(t.schedule_date) schedule_date from equipment_schedule t where t.equipment_id = '" + equipmentId + "' and t.period_index = '" + periodIndex + "') and t1.equipment_id = '" + equipmentId + "' and t1.period_index = '" + periodIndex + "'";
                        List list = SQLProcess.excuteSQLQuery(sql, delegator);

                        // 根据最后一天的PM，加上基本天数，生成在制定年度内需要设定的PM
                        PeriodHelper.yearPmSet(accountNo, delegator, year, equipmentId, defaultPeriod, periodIndex,list);
                    }
                }
            }
            request.setAttribute("accountNo", accountNo);
            request.setAttribute("accountName", accountName);
            request.setAttribute("accountSection", accountSection);
            String message = accountSection + "下的" + year + "年度的保养全部生成";
            request.setAttribute("_EVENT_MESSAGE_", message);
            request.setAttribute("month", new Integer(Calendar.getInstance().get(Calendar.MONTH)));

        } catch (Exception e) {
            Debug.logError(e, module);
        }
        return "success";
    }

    // -----------------------------------删除区间内的保养计划-----------------
    /**
     * 删除保养区间的页面初始化动作
     */
    public static String defaultPeriodClearInit(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // 得到用户名 用户所属部门
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");
            GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));
            String accountSection = account.getString("accountSection");

            // 传到页面上
            request.setAttribute("accountSection", accountSection);
            if (!StringUtils.isEmpty(request.getParameter("_EVENT_MESSAGE_"))) {
                request.setAttribute("_EVENT_MESSAGE_", request.getParameter("_EVENT_MESSAGE_"));
            }

        } catch (Exception e) {
            Debug.logError(e, module);
        }

        return "success";
    }

    /**
     * 区间内该部门保养全部删除动作
     * @param request
     * @param response
     * @return
     */
    public static String defaultPeriodClear(HttpServletRequest request, HttpServletResponse response) {
        // 得到页面参数
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String startDate = (String) request.getParameter("startDate");
        String endDate = (String) request.getParameter("endDate");
        String accountSection = request.getParameter("accountSection");
        try {
            // 得到部门内的全部设备
                List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("section", accountSection));
            for (int i = 0; i < equipmentList.size(); i++) {
                // 根据设备大类得到该设备下的全部保养类型
                GenericValue equipment = (GenericValue) equipmentList.get(i);
                String equipmentType = equipment.getString("equipmentType");
                List defaultPeriodList = delegator.findByAnd("DefaultPeriod", UtilMisc.toMap("eqpType", equipmentType));

                // 删除动作
                for (int j = 0; j < defaultPeriodList.size(); j++) {
                    GenericValue defaultPeriod = (GenericValue) defaultPeriodList.get(j);
                    String sql = "delete from equipment_Schedule t  where t.event_index is  null and  t.equipment_Id = '" + equipment.getString("equipmentId") + "' and t.period_Index = '" + defaultPeriod.getString("periodIndex")
                            + "' and t.schedule_Date >= to_date('" + startDate + "','yyyy-MM-dd') and t.schedule_date <= to_date('" + endDate + "','yyyy-MM-dd')";
                    SQLProcess.excuteSQLUpdateForQc(sql, delegator);
                }
            }
            request.setAttribute("_EVENT_MESSAGE_", "删除成功！");
        } catch (Exception e) {
            Debug.logError(e, module);
        }
        return "success";
    }
    
    /**
	  * 保养计划延期，超出基本天数+警示天数限制时，发送工艺主管签核
	  * Object(PMDELAY)
	  * @param request
	  * @param response
	  * @return
	  */
	public static String sendSubmitPmDelay(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		 
		String equipmentId = request.getParameter("equipmentId");
        String year = request.getParameter("form_year");
        String month = request.getParameter("form_month");
        String day = request.getParameter("form_day");
        String periodIndex = request.getParameter("periodIndex");
        String newScheduleDate = request.getParameter("newScheduleDate");//新计划日期
        String ownerProcess = request.getParameter("ownerProcess");
        String user = CommonUtil.getUserNo(request);
        
        String formDate = year + "-" + month + "-" + day;//原计划日期
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
		 
		try {
			GenericValue defaultPeriod = delegator.findByPrimaryKey("DefaultPeriod", UtilMisc.toMap("periodIndex", periodIndex));
	        String periodName = defaultPeriod.getString("periodName");
	        String defaultDays = defaultPeriod.getString("defaultDays");
	        String warningDays = defaultPeriod.getString("warningDays");
	        
			Map paramMap = new HashMap();
			paramMap.put("equipmentId", equipmentId);
			paramMap.put("periodIndex", periodIndex);
			paramMap.put("scheduleDate", new java.sql.Date(cal.getTimeInMillis()));
			
			String objectIndex = null;
			List equipmentScheduleList = delegator.findByAnd("EquipmentSchedule", paramMap);
			if (equipmentScheduleList.size() > 0) {
			    GenericValue gv = (GenericValue) equipmentScheduleList.get(0);            
			    objectIndex = gv.getString("scheduleIndex");			    
			    
			    //未超出周期天数+警示天数，直接修改，无需申请
				if (PeriodHelper.isNewScheduleValid(delegator, equipmentId, periodIndex, objectIndex, newScheduleDate, defaultDays, warningDays)) {
					gv.put("scheduleDate", MiscUtils.toGuiDate(newScheduleDate, "yyyy-MM-dd"));
					gv.put("scheduleNote", "已修改");
					gv.put("creator", user);
					gv.put("createDate", UtilDateTime.nowTimestamp());
					delegator.store(gv);
					request.setAttribute("_EVENT_MESSAGE_", "成功：保养计划日期已修改到 "+newScheduleDate);
					return "success";
				}
			} else {
				request.setAttribute("_ERROR_MESSAGE_", formDate+" 未找到保养计划，请重新选择!");
				return "success";
			}		
						
			if (WorkflowHelper.checkSubmit(delegator, objectIndex, Constants.SUBMIT_PM_DELAY)) {
				request.setAttribute("_ERROR_MESSAGE_", "该对象已申请签核，发送签核信息失败!");
				return "success";
			}			 
			
	        String objectName = "[" +equipmentId + "],[" + periodName + "],[原计划日期" + formDate + "],[新计划日期" + newScheduleDate + "]";
			Map result = WorkflowHelper.sendSubmitToProcess(dispatcher, Constants.SUBMIT_PM_DELAY, objectIndex, objectName, Constants.SUBMIT_MODIFY, user, ownerProcess);
			// 883609，huanghp，如果没有取到正常的主管，标示一下，2009-02
			if ("NoLeader".equals(result.get(ModelService.ERROR_MESSAGE))) {
				request.setAttribute("_ERROR_MESSAGE_", "找不到部门课长，发送签核信息失败，请联系系统负责人!");
			} else {
				request.setAttribute("_EVENT_MESSAGE_", "已经成功送达主管签核!");
			}
			
			String sendTo = AccountHelper.getMailOfSectionLeaderByUserNo(delegator, user)
				+ AccountHelper.getMailByAccountNo(delegator, ownerProcess);
			String sendCc = AccountHelper.getMailByAccountNo(delegator, user);
			CommonUtil.sendMail(Constants.IT_SYSTEM_MAIL, sendTo, sendCc, "PMS - 保养计划延期申请 已启动: " + equipmentId, objectName + "，请主管登录PM系统进行签核。");
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		 
		return "success";
	}
}
