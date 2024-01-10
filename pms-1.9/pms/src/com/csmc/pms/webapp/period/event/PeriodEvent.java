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

    // --------------------------�豸��������-------------------------------

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
     * �豸��������һ������:ȡ�ù����豸����������Ϣ��������ʾ
     * @param request
     *            eqpType �豸����
     * @param response
     * @return String success/error
     */
    public static String defaultPeriodList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // java�д��ݵĲ���������ά����ǰ�豸���ౣ���ϴ������ֵ
            String eqpType = (String) request.getAttribute("eqpType");

            // ҳ���ϴ��ݵĲ���
            String eqp_Type = UtilFormatOut.checkNull((String) request.getParameter("eqp_Type"));
            String holdPeriodIndex = UtilFormatOut.checkNull((String) request.getParameter("holdPeriodIndex"));

            // �������ֵ���У��ϲ�ȡҳ�����
            if (!StringUtils.isEmpty(eqp_Type)) {
                eqpType = eqp_Type;
            }

            // �鿴���豸�����Ƿ����
            List equipmentTypeList = CommonHelper.getEquipmentTypeList(delegator);
            List promisEqpStatusList = delegator.findByAnd("PromisEqpStatus", UtilMisc.toMap("type", Constants.PM), UtilMisc.toList("eqpStatus"));
            List timeRangeList = delegator.findAllCache("TimeRange");

            List defaultPeriodList = null;
            if (StringUtils.isNotEmpty(holdPeriodIndex)) {
            	defaultPeriodList = delegator.findByAnd("DefaultPeriod", UtilMisc.toMap("eqpType", eqpType, "periodIndex", holdPeriodIndex), UtilMisc.toList("periodName"));
            	
            	//fab1���hold�룬holdԭ�� 
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

            // �жϲ���ҳ���Ƿ���ʾ��flag
            request.setAttribute("flag", "OK");
            
            //�����¼��Ϊ������֤��(dept_index == 10010)������������������MSA��ͷ
            request.setAttribute("isMsaDept", AccountHelper.isMsaDept(request, delegator) ? "true" : "false");            
            
        } catch (Exception e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
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
    public static String defaultPeriodHold(HttpServletRequest request, HttpServletResponse response) {
		// �����ϴ��ݵ����в�����ϳ�Map
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
		    paramMap.put("periodIndex", holdPeriodIndex);
		    GenericValue holdGv = delegator.makeValidValue("DefaultPeriod", paramMap);
		    delegator.store(holdGv);
		
		    // ��ʾ�豸��������һ����flag
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
     * �豸���������½��򱣴�
     * @param request
     *            periodIndex��equipmentId��periodName,periodDesc,eqpType,
     *            defaultDays,standardHour,eqpStatus,warningDays,isUpdatePromis
     * @param response
     * @return String success/error
     */
    public static String manageDefaultPeriod(HttpServletRequest request, HttpServletResponse response) {
		// �����ϴ��ݵ����в�����ϳ�Map
		Map paramMap = BasicEvent.getInitParams(request, true, true);
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try {
		    // ���͸�ֵ����ΪPM/PC
		    paramMap.put("event", "PM");
		
		    // ȡ���û�
		    GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		    String user = userLogin.getString("userLoginId");
		    paramMap.put("transBy", user);
		
		    // ����ʱ�丳ֵ
		    paramMap.put("updateTime", new Timestamp(System.currentTimeMillis()));
		
		    // 1.����Ϣд���豸�������ڱ�
		    PeriodHelper.createDefaultPeriod(delegator, paramMap);
		    
		    // 2.�޸�ʱ����
		    if (paramMap.get("periodIndex") != null) {
		    	PeriodHelper.updatePmNextStarttime(delegator, paramMap);
	        }
		
		    // ��ʾ���豸��������һ����flag
		    request.setAttribute("flag", "OK");
		    request.setAttribute("eqpType", paramMap.get("eqpType"));
		    request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");

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
     * ɾ���豸�������ڣ���������
     * 
     * @param request
     *            periodIndex��eqpType
     * @param response
     * @return String : success/error
     */
    public static String delDefaultPeriod(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // ȡ��ҳ�����
        String periodIndex = request.getParameter("periodIndex");
        String eqpType = request.getParameter("eqpType");
        try {
            // ɾ�����豸��������
        	if(!PeriodHelper.checkPmPeriod(delegator, periodIndex)) {
        		PeriodHelper.delDefaultPeriod(delegator, periodIndex);
        		request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
        	} else {
        		request.setAttribute("_ERROR_MESSAGE_", "�������������������趨������ɾ��!");
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
     * �����ϵı༭��ť��ѯֵ
     * 
     * @param request
     *            periodIndex
     * @param response
     */
    public static void queryDefaultPeriodByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // seqIndexȡ��
        String periodIndex = request.getParameter("periodIndex");

        try {
            // ȡ�û�̨������Ϣ
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

            // д��response
            response.getWriter().write(defaultPeriodParam.toString());
        } catch (Exception e) {
            Debug.logError(e, module);
        }
    }

    // -----------------------------------------------------�豸���������趨----------------------------

    /**
     * �豸���������趨��ʼ����
     * 
     * @param request
     * @param response
     * @return
     */
    public static String equipmentSchedule(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // ȡ���û�
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");

            GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));
            String deptIndex = account.getString("deptIndex");
            //�����¼��Ϊ������֤��(dept_index == 10010)����ʾ(MSA==��Y��)���豸
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
     * �豸���������趨��ʾ
     * @param request
     * @param response
     * @return
     */
    public static String equipmentScheduleDefine(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
        	
            // ȡ���û�����
        	GenericValue userInfo = AccountHelper.getUserInfo(request,delegator);
        	String maintDept = userInfo.getString("accountDept");
        	request.setAttribute("maintDept", maintDept);
        	List deptList = delegator.findAllCache("EquipmentDept", UtilMisc.toList("equipmentDept"));
            request.setAttribute("deptList", deptList);
        	
	        // �õ�����--------->equipmentId�豸
	        String equipmentId = (String) request.getParameter("equipmentId");
	        if (!StringUtils.isEmpty((String) request.getAttribute("equipmentId"))) {
	            equipmentId = (String) request.getAttribute("equipmentId");
	        }
	        if (equipmentId == null || equipmentId.equals(""))
	        {
	        	return "success";
	        }
	
	        // �õ�����------->periodIndex��������
	        String periodIndex = (String) request.getParameter("periodIndex");
	        if (!StringUtils.isEmpty((String) request.getAttribute("periodIndex"))) {
	            periodIndex = (String) request.getAttribute("periodIndex");
	        }
	
	        // �õ�����-------->���
	        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
	        if (!StringUtils.isEmpty(request.getParameter("setup_year"))) {
	            thisYear = Integer.parseInt(request.getParameter("setup_year"));
	        }
	
	        // ���廭����ʾ���ޱ�����ɫ�����б��������ֱַ���ʾ��ɫ
	        String maintenancecolor1 = "#40FF60";
	        String maintenancecolor2 = "#FFFF00";
	        String maintenancecolor3 = "#B0B000";
	        String background = "#E8E8FF";
	
	        // ������ɫ��HashMap ColorMap
	        Map colorMap = PeriodHelper.getColorMap(maintenancecolor1, maintenancecolor2, maintenancecolor3, background);
	
	        // �õ����ڵ�title��ʾ
	        String[] daystitle = PeriodHelper.getWeekArr();
	
	        // ȡ��������ݵ�ÿ������
	        int[] monthdays = PeriodHelper.getMonthArr(thisYear);
	
	        Calendar firstDay = Calendar.getInstance();
	        firstDay.set(thisYear, 0, 1);
	        
	        List timeRangeList = new ArrayList();            
            List scheduleList = new ArrayList();
            for (int i = 1; i <= 12; i++) {
                // scheduleMap���ÿ�µĸ�����Ϣ
                HashMap scheduleMap = new HashMap();

                // ȡ�õ������յ�������Ϣ
                Calendar day = Calendar.getInstance();
                day = firstDay;

                // �趨��һ������
                scheduleMap.put("firstDay", day);

                // �趨��һ�촦�ڵ��ܵĵڼ���
                scheduleMap.put("startDay", new Integer(day.get(Calendar.DAY_OF_WEEK)));

                // �趨title
                String title = PeriodHelper.getMonthStr(day);
                scheduleMap.put("title", title);

                // ��0��ʼ
                int month = day.get(Calendar.MONTH);
                scheduleMap.put("month", new Integer(month));

                // �趨���¶�����
                scheduleMap.put("ndays", new Integer(monthdays[day.get(Calendar.MONTH) + 1]));

                // �趨description
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

                // ������ʱ����ص��ĸ�Map
                Map timeMap = new HashMap();
                Map periodMap = new HashMap();
                Map creatorMap = new HashMap();
                Map isFinishedMap = new HashMap();
                Map timeRangeMap = new HashMap();

                // �趨�����ĸ�Map
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

            // ��������
            GenericValue eqpgv = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId", equipmentId));
            List equipmentPeriodList = delegator.findByAnd("DefaultPeriod", UtilMisc.toMap("eqpType", eqpgv.getString("equipmentType"), "enabled", new Integer(Constants.ENABLE)), UtilMisc.toList("periodName"));

            // ����request
            request.setAttribute("scheduleList", scheduleList);
            request.setAttribute("equipmentId", equipmentId);
            request.setAttribute("periodIndex", periodIndex);
            request.setAttribute("equipmentPeriodList", equipmentPeriodList);
            request.setAttribute("thisYear", String.valueOf(thisYear));
            request.setAttribute("colorMap", colorMap);
            request.setAttribute("daystitle", daystitle);
            request.setAttribute("timeRangeList", timeRangeList);
            
            if (StringUtils.isNotEmpty(periodIndex)) {
	            //��ѯ���տγ�
				List ownerProcessList = WorkflowHelper.getProcessSectionLeaderList(delegator);
				request.setAttribute("ownerProcessList", ownerProcessList);
				
				//���豸�����ڣ���ѯǩ���еļƻ���������
				List wfSubmitList = PeriodHelper.getPmDelaySubmitList(delegator,equipmentId, periodIndex, null);
				request.setAttribute("wfSubmitList", wfSubmitList);
            }
			
        } catch (Exception e) {
            Debug.logError(e, module);
        }

        return "success";
    }

	/**
     * �����������ڵ��趨��֤���Ѹ�Ϊ�ύ����֤
     * �����Ա������ڵ��趨 ����֤
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
     * �������ڵ����
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
            	// ������ѭ���趨�ƻ�����
                while (cal.get(Calendar.YEAR) == Integer.parseInt(year)) {
                    if (StringUtils.isEmpty(defaultPeriod.getString("defaultDays")) || defaultPeriod.getLong("defaultDays").intValue() == 0) {
                    	break;
                    } else {                        
                        defaultPeriod.getString("defaultDays");
                        PeriodHelper.pmDaySet(delegator, equipmentId, cal.getTime(), periodIndex, user,timeRangeIndex);
                        cal.add(Calendar.DATE, Integer.parseInt(defaultPeriod.getString("defaultDays")));
                    }

                }
            } else {//������������֤
            	if (Constants.Y.equals(PeriodHelper.equipmentScheduleAddEnabled(delegator, equipmentId, periodIndex, scheduleDate))) {
            		PeriodHelper.pmDaySet(delegator, equipmentId, cal.getTime(), periodIndex,user,timeRangeIndex);
            	} else {
            		request.setAttribute("_ERROR_MESSAGE_", "��ǰ�趨���� ���� ��ʾ������������������ύ�޸����룬��֪ͨ�豸�빤������ǩ�ˡ�");
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
     * �����������ڵ�ɾ����֤���Ѹ�Ϊ�ύ����֤
     * �����Ա������ڵ�ɾ�� ��֤
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

            if ("1".equalsIgnoreCase(flag)) {//������            	
                while (cal.get(Calendar.YEAR) == Integer.parseInt(year)) {
                    if (StringUtils.isEmpty(defaultDays) || "0".equals(defaultDays)) {
                    	break;
                    } else {
                    	List equipmentScheduleList = delegator.findByAnd("EquipmentSchedule", UtilMisc.toMap("equipmentId", equipmentId, "periodIndex", periodIndex, "scheduleDate", new java.sql.Date(cal.getTimeInMillis())));
                        
                        //������һ���ƻ���У��
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
            } else {//����
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
     * �������ڵ�ɾ��
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

            if ("1".equalsIgnoreCase(flag)) {//������ɾ��
                while (cal.get(Calendar.YEAR) == Integer.parseInt(year)) {
                    if (StringUtils.isEmpty(defaultPeriod.getString("defaultDays")) || defaultPeriod.getLong("defaultDays").intValue() == 0) {
                    	break;
                    } else {                        
                        PeriodHelper.pmDayReset(delegator, equipmentId, cal.getTime(), periodIndex, user);
                        cal.add(Calendar.DATE, Integer.parseInt(defaultPeriod.getString("defaultDays")));
                    }
                }
            } else {//����ɾ������֤
            	String scheduleDate = year + "-" + month + "-" + day;
            	if (Constants.Y.equals(PeriodHelper.equipmentScheduleClearEnabled(delegator, equipmentId, periodIndex, scheduleDate))) {
            		List wfSubmitList = PeriodHelper.getPmDelaySubmitList(delegator,equipmentId, periodIndex, scheduleDate);
            		if (wfSubmitList.size() >= 1) {
                		request.setAttribute("_ERROR_MESSAGE_", "��ǰ�ƻ����ύ�������룬�޷�ɾ������֪ͨ�豸�빤������ǩ�ˡ�");
                	} else {
                		PeriodHelper.pmDayReset(delegator, equipmentId, cal.getTime(), periodIndex, user);
                	}
            	} else {
            		request.setAttribute("_ERROR_MESSAGE_", "��ǰ�ƻ�����ɾ����������������ύ�޸����룬��֪ͨ�豸�빤������ǩ�ˡ�");
            	}
            }
            request.setAttribute("equipmentId", equipmentId);
            request.setAttribute("periodIndex", periodIndex);
        } catch (Exception e) {
            Debug.logError(e, module);
        }
        return "success";
    }

    // --------------------------------Ѳ�챣�����ڵ��趨-----------------------
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
     * Ѳ������һ������:ȡ�ù���Ѳ��������Ϣ��������ʾ
     * 
     * @param request
     *            styleIndex---->PcStyle
     * @param response
     * @return String success/error
     */
    public static String pcPeriodList(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // java�д��ݵĲ���������ά����ǰpcStyle�����ϴ������ֵ
            String styleIndex = (String) request.getAttribute("styleIndex");

            // ҳ���ϴ��ݵĲ���
            String style_Index = (String) request.getParameter("style_Index");

            // �������ֵ���У��ϲ�
            if (!StringUtils.isEmpty(style_Index)) {
                styleIndex = style_Index;
            }

            // �鿴��Ѳ������Ƿ����
            List pcStyleList = delegator.findAll("PcStyle");

            // ���ڣ�ȡ�ô�Ѳ��������Ϣ
            List pcPeriodList = delegator.findByAnd("PcPeriod", UtilMisc.toMap("styleIndex", styleIndex), UtilMisc.toList("periodName"));
            request.setAttribute("pcPeriodList", pcPeriodList);
            request.setAttribute("pcStyleList", pcStyleList);
            request.setAttribute("styleIndex", styleIndex);

            // �жϲ���ҳ���Ƿ���ʾ��flag
            request.setAttribute("flag", "OK");

        } catch (Exception e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    /**
     * Ѳ�������½��򱣴�
     * 
     * @param request
     *            periodIndex��styleIndex��periodName,periodDesc,eqpType,
     *            defaultDays,standardHour
     * @param response
     * @return String success/error
     */
    public static String managePcPeriod(HttpServletRequest request, HttpServletResponse response) {
        // �����ϴ��ݵ����в�����ϳ�Map
        Map paramMap = BasicEvent.getInitParams(request, true, true);
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // ���͸�ֵ����ΪPM/PC
            paramMap.put("event", "PC");

            // ȡ���û�
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");
            paramMap.put("transBy", user);

            // ����ʱ�丳ֵ
            paramMap.put("updateTime", new Timestamp(System.currentTimeMillis()));

            // ����Ϣд��Ѳ�����ڱ�
            PeriodHelper.createPcPeriod(delegator, paramMap);

            // ��ʾѲ������һ����flag
            request.setAttribute("flag", "OK");
            request.setAttribute("styleIndex", paramMap.get("styleIndex"));
            request.setAttribute("_EVENT_MESSAGE_", "����ɹ���");

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
     * ɾ��Ѳ�����ڣ���������
     * 
     * @param request
     *            periodIndex��styleIndex
     * @param response
     * @return String : success/error
     */
    public static String delPcPeriod(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // ȡ��ҳ�����
        String periodIndex = request.getParameter("periodIndex");
        String styleIndex = request.getParameter("styleIndex");
        try {
            // ɾ����Ѳ������
        	if(!PeriodHelper.checkPcPeriod(delegator, periodIndex)) {
        		PeriodHelper.delPcPeriod(delegator, periodIndex);
        		request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
        	} else {
        		request.setAttribute("_ERROR_MESSAGE_", "�������������������趨������ɾ����");
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
     * �����ϵı༭��ť��ѯֵ
     * 
     * @param request
     *            periodIndex
     * @param response
     */
    public static void queryPcPeriodByIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // periodIndexȡ��
        String periodIndex = request.getParameter("periodIndex");

        try {
            // ȡ��Ѳ����Ϣ
            GenericValue gv = delegator.findByPrimaryKey("PcPeriod", UtilMisc.toMap("periodIndex", periodIndex));
            JSONObject defaultPeriodParam = new JSONObject();
            defaultPeriodParam.put("styleIndex", gv.getString("styleIndex"));
            defaultPeriodParam.put("periodName", gv.getString("periodName"));
            defaultPeriodParam.put("periodDesc", gv.getString("periodDesc"));
            defaultPeriodParam.put("defaultDays", gv.getString("defaultDays"));
            defaultPeriodParam.put("standardHour", gv.getString("standardHour"));
            defaultPeriodParam.put("enabled", gv.getString("enabled"));

            // д��response
            response.getWriter().write(defaultPeriodParam.toString());
        } catch (Exception e) {
            Debug.logError(e, module);
        }
    }

    // --------------------------------Ѳ�챣�����ڵ��趨-----------------------
    /**
     * Ѳ�챣�������趨��ʼ����
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
     * Ѳ�챣�������趨��ʾ
     * 
     * @param request
     * @param response
     * @return
     */
    public static String pcScheduleDefine(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

        // �õ�����--------->styleIndex---pcStyle
        String styleIndex = (String) request.getParameter("styleIndex");
        if (!StringUtils.isEmpty((String) request.getAttribute("styleIndex"))) {
            styleIndex = (String) request.getAttribute("styleIndex");
        }

        // �õ�����------->periodIndex��������
        String periodIndex = (String) request.getParameter("periodIndex");
        if (!StringUtils.isEmpty((String) request.getAttribute("periodIndex"))) {
            periodIndex = (String) request.getAttribute("periodIndex");
        }

        // �õ�����-------->���
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        if (!StringUtils.isEmpty(request.getParameter("setup_year"))) {
            thisYear = Integer.parseInt(request.getParameter("setup_year"));
        }

        // ���廭����ʾ���ޱ�����ɫ�����б��������ֱַ���ʾ��ɫ
        String maintenancecolor1 = "#40FF60";
        String maintenancecolor2 = "#FFFF00";
        String maintenancecolor3 = "#B0B000";
        String background = "#E8E8FF";

        // ������ɫ��HashMap ColorMap
        Map colorMap = PeriodHelper.getColorMap(maintenancecolor1, maintenancecolor2, maintenancecolor3, background);

        // �õ����ڵ�title��ʾ
        String[] daystitle = PeriodHelper.getWeekArr();

        // ȡ��������ݵ�ÿ������
        int[] monthdays = PeriodHelper.getMonthArr(thisYear);

        Calendar firstDay = Calendar.getInstance();
        firstDay.set(thisYear, 0, 1);

        try {
            List scheduleList = new ArrayList();
            for (int i = 1; i <= 12; i++) {
                // scheduleMap���ÿ�µĸ�����Ϣ
                HashMap scheduleMap = new HashMap();

                // ȡ�õ������յ�������Ϣ
                Calendar day = Calendar.getInstance();
                day = firstDay;

                // �趨��һ������
                scheduleMap.put("firstDay", day);

                // �趨��һ�촦�ڵ��ܵĵڼ���
                scheduleMap.put("startDay", new Integer(day.get(Calendar.DAY_OF_WEEK)));

                // �趨title
                String title = PeriodHelper.getMonthStr(day);
                scheduleMap.put("title", title);

                // ��0��ʼ
                int month = day.get(Calendar.MONTH);
                scheduleMap.put("month", new Integer(month));

                // �趨���¶�����
                scheduleMap.put("ndays", new Integer(monthdays[day.get(Calendar.MONTH) + 1]));

                // �趨description
                String description = "";
                if (!StringUtils.isEmpty(periodIndex)) {
                    GenericValue dpgv = (GenericValue) delegator.findByPrimaryKey("PcPeriod", UtilMisc.toMap("periodIndex", periodIndex));
                    description = dpgv.getString("periodDesc");
                }
                scheduleMap.put("description", description);

                // ������ʱ����ص��ĸ�Map
                Map timeMap = new HashMap();
                Map periodMap = new HashMap();
                Map creatorMap = new HashMap();
                Map isFinishedMap = new HashMap();

                // �趨�����ĸ�Map
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

            // Ѳ������List
            List pcPeriodList = delegator.findByAnd("PcPeriod", UtilMisc.toMap("styleIndex", styleIndex, "enabled", new Integer(Constants.ENABLE)), UtilMisc.toList("periodName"));
            GenericValue pcgv = delegator.findByPrimaryKey("PcStyle", UtilMisc.toMap("styleIndex", styleIndex));
            String styleName = pcgv.getString("name");

            // ����request
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
     * Ѳ�����ڵ����
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
     * Ѳ�����ڵ�ɾ��
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
     * ���PM�ƻ����ɳ�ʼ��
     * 
     * @param request
     * @param response
     * @return
     */
    public static String yearPeriod(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // ȡ���û�
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
    				request.setAttribute("_EVENT_MESSAGE_", year + "��ƻ������У����Ժ���ʼ�֪ͨ��");
    				return "error";
    			} else if(status == Constants.OVER) {
    				request.setAttribute("_EVENT_MESSAGE_", year + "��ƻ��Ѿ���ɣ������������в鿴������");
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
    		//call Procedure ����nextYear����
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
    			
    			request.setAttribute("_EVENT_MESSAGE_", "���ں�̨������һ��ȱ����ƻ�...");
    		}
    	} catch(Exception e) {
    		Debug.logError(e.getMessage(),module);
    		return "error";
    	}
    	
    	return "success";
    }
    
    /**
     * �����PM�ƻ�������
     * @param request
     * @param response
     * @return
     */
    public static String yearPeriodScheduleBk(HttpServletRequest request, HttpServletResponse response) {
        // ȡ��ҳ�����
        String accountSection = request.getParameter("accountSection");
        String accountName = request.getParameter("accountName");
        String accountNo = request.getParameter("accountNo");
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // ȡ�õ�ǰ�·�������PM�����
            int year;
            int month;
            Calendar now = Calendar.getInstance();
            month = now.get(Calendar.MONTH);
            if (month == 0) {
                year = now.get(Calendar.YEAR);
            } else {
                year = now.get(Calendar.YEAR) + 1;
            }

            // �õ��û������α��µ������豸
            List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("section", accountSection));
            for (int i = 0; i < equipmentList.size(); i++) {
                // ȡ���豸����
                GenericValue equipment = (GenericValue) equipmentList.get(i);
                String equipmentId = equipment.getString("equipmentId");
                String equipmentType = equipment.getString("equipmentType");

                // �õ����豸�µ�ȫ����������
                List defaultPeriodList = delegator.findByAnd("DefaultPeriod", UtilMisc.toMap("eqpType", equipmentType));
                for (int j = 0; j < defaultPeriodList.size(); j++) {
                    GenericValue defaultPeriod = (GenericValue) defaultPeriodList.get(j);                                      
                    if ("1".equals(defaultPeriod.getString("enabled"))) {                                                
                        String periodIndex = defaultPeriod.getString("periodIndex");

                        // ȡ�ô˴��豸�������������һ����PM������
                        String sql = "select  t1.schedule_date, t1.time_Range_Index from equipment_schedule t1 where  t1.schedule_date = (select max(t.schedule_date) schedule_date from equipment_schedule t where t.equipment_id = '" + equipmentId + "' and t.period_index = '" + periodIndex + "') and t1.equipment_id = '" + equipmentId + "' and t1.period_index = '" + periodIndex + "'";
                        List list = SQLProcess.excuteSQLQuery(sql, delegator);

                        // �������һ���PM�����ϻ����������������ƶ��������Ҫ�趨��PM
                        PeriodHelper.yearPmSet(accountNo, delegator, year, equipmentId, defaultPeriod, periodIndex,list);
                    }
                }
            }
            request.setAttribute("accountNo", accountNo);
            request.setAttribute("accountName", accountName);
            request.setAttribute("accountSection", accountSection);
            String message = accountSection + "�µ�" + year + "��ȵı���ȫ������";
            request.setAttribute("_EVENT_MESSAGE_", message);
            request.setAttribute("month", new Integer(Calendar.getInstance().get(Calendar.MONTH)));

        } catch (Exception e) {
            Debug.logError(e, module);
        }
        return "success";
    }

    // -----------------------------------ɾ�������ڵı����ƻ�-----------------
    /**
     * ɾ�����������ҳ���ʼ������
     */
    public static String defaultPeriodClearInit(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        try {
            // �õ��û��� �û���������
            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String user = userLogin.getString("userLoginId");
            GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));
            String accountSection = account.getString("accountSection");

            // ����ҳ����
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
     * �����ڸò��ű���ȫ��ɾ������
     * @param request
     * @param response
     * @return
     */
    public static String defaultPeriodClear(HttpServletRequest request, HttpServletResponse response) {
        // �õ�ҳ�����
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String startDate = (String) request.getParameter("startDate");
        String endDate = (String) request.getParameter("endDate");
        String accountSection = request.getParameter("accountSection");
        try {
            // �õ������ڵ�ȫ���豸
                List equipmentList = delegator.findByAnd("Equipment", UtilMisc.toMap("section", accountSection));
            for (int i = 0; i < equipmentList.size(); i++) {
                // �����豸����õ����豸�µ�ȫ����������
                GenericValue equipment = (GenericValue) equipmentList.get(i);
                String equipmentType = equipment.getString("equipmentType");
                List defaultPeriodList = delegator.findByAnd("DefaultPeriod", UtilMisc.toMap("eqpType", equipmentType));

                // ɾ������
                for (int j = 0; j < defaultPeriodList.size(); j++) {
                    GenericValue defaultPeriod = (GenericValue) defaultPeriodList.get(j);
                    String sql = "delete from equipment_Schedule t  where t.event_index is  null and  t.equipment_Id = '" + equipment.getString("equipmentId") + "' and t.period_Index = '" + defaultPeriod.getString("periodIndex")
                            + "' and t.schedule_Date >= to_date('" + startDate + "','yyyy-MM-dd') and t.schedule_date <= to_date('" + endDate + "','yyyy-MM-dd')";
                    SQLProcess.excuteSQLUpdateForQc(sql, delegator);
                }
            }
            request.setAttribute("_EVENT_MESSAGE_", "ɾ���ɹ���");
        } catch (Exception e) {
            Debug.logError(e, module);
        }
        return "success";
    }
    
    /**
	  * �����ƻ����ڣ�������������+��ʾ��������ʱ�����͹�������ǩ��
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
        String newScheduleDate = request.getParameter("newScheduleDate");//�¼ƻ�����
        String ownerProcess = request.getParameter("ownerProcess");
        String user = CommonUtil.getUserNo(request);
        
        String formDate = year + "-" + month + "-" + day;//ԭ�ƻ�����
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
			    
			    //δ������������+��ʾ������ֱ���޸ģ���������
				if (PeriodHelper.isNewScheduleValid(delegator, equipmentId, periodIndex, objectIndex, newScheduleDate, defaultDays, warningDays)) {
					gv.put("scheduleDate", MiscUtils.toGuiDate(newScheduleDate, "yyyy-MM-dd"));
					gv.put("scheduleNote", "���޸�");
					gv.put("creator", user);
					gv.put("createDate", UtilDateTime.nowTimestamp());
					delegator.store(gv);
					request.setAttribute("_EVENT_MESSAGE_", "�ɹ��������ƻ��������޸ĵ� "+newScheduleDate);
					return "success";
				}
			} else {
				request.setAttribute("_ERROR_MESSAGE_", formDate+" δ�ҵ������ƻ���������ѡ��!");
				return "success";
			}		
						
			if (WorkflowHelper.checkSubmit(delegator, objectIndex, Constants.SUBMIT_PM_DELAY)) {
				request.setAttribute("_ERROR_MESSAGE_", "�ö���������ǩ�ˣ�����ǩ����Ϣʧ��!");
				return "success";
			}			 
			
	        String objectName = "[" +equipmentId + "],[" + periodName + "],[ԭ�ƻ�����" + formDate + "],[�¼ƻ�����" + newScheduleDate + "]";
			Map result = WorkflowHelper.sendSubmitToProcess(dispatcher, Constants.SUBMIT_PM_DELAY, objectIndex, objectName, Constants.SUBMIT_MODIFY, user, ownerProcess);
			// 883609��huanghp�����û��ȡ�����������ܣ���ʾһ�£�2009-02
			if ("NoLeader".equals(result.get(ModelService.ERROR_MESSAGE))) {
				request.setAttribute("_ERROR_MESSAGE_", "�Ҳ������ſγ�������ǩ����Ϣʧ�ܣ�����ϵϵͳ������!");
			} else {
				request.setAttribute("_EVENT_MESSAGE_", "�Ѿ��ɹ��ʹ�����ǩ��!");
			}
			
			String sendTo = AccountHelper.getMailOfSectionLeaderByUserNo(delegator, user)
				+ AccountHelper.getMailByAccountNo(delegator, ownerProcess);
			String sendCc = AccountHelper.getMailByAccountNo(delegator, user);
			CommonUtil.sendMail(Constants.IT_SYSTEM_MAIL, sendTo, sendCc, "PMS - �����ƻ��������� ������: " + equipmentId, objectName + "�������ܵ�¼PMϵͳ����ǩ�ˡ�");
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		 
		return "success";
	}
}
