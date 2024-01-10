package com.csmc.pms.webapp.report.event;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.eqp.helper.PlldbHelper;
import com.csmc.pms.webapp.report.help.DefaultHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.GeneralEvents;

public class DefaultEvent  extends GeneralEvents{
	public static final String module = DefaultEvent.class.getName();

	/**
	 * ��ʾ��ҳ
	 * @param request
	 * @param response
	 * @return list
	 */
	public static String pmsDefault(HttpServletRequest request, HttpServletResponse response) {
		try {
			GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
			String user = CommonUtil.getUserNo(request);
			GenericValue listUser = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));
		    String accountDept = listUser.getString("accountDept");

		    String tabIndex = request.getParameter("tabIndex");
		    if (tabIndex==null){
		    	tabIndex = "abnormal";
		    }
		    request.setAttribute("tabIndex", tabIndex);
		    
			String dept = request.getParameter("dept");
			if (dept==null){
				dept = accountDept;
			}			
			request.setAttribute("d", dept);
			
			//abnormal: �쳣
			if (tabIndex.equals("abnormal")) {
				//��ʾ�����豸�쳣�嵥
				List pm_abnormal_record_List = DefaultHelper.getPmAbnormalRecordList(delegator, dept);				
				request.setAttribute("pm_abnormal_record_List", pm_abnormal_record_List);
				
				//Ŀǰ�豸�����ڲ���������Сֵ�嵥
				List unschedule_eqp_param_List = DefaultHelper.getUnscheduleEqpParamList(delegator, dept);				
				request.setAttribute("unschedule_eqp_param_List", unschedule_eqp_param_List);
			}	
			
			//pc: Ѳ��
			if (tabIndex.equals("pc")){
				//����δ��ɵ�Ѳ��
				List period_schedule_List = DefaultHelper.getPeriodScheduleList(delegator, dept);
				request.setAttribute("period_schedule_List", period_schedule_List);	
				
				//��������ɵ�Ѳ��
				List pc_form_List = DefaultHelper.getPcFormList(delegator);
				request.setAttribute("pc_form_List", pc_form_List);
			}						
			
			//pm: ����
			if (tabIndex.equals("pm")) {
				//����δ����
				List equipment_schedule_List = DefaultHelper.getEquipmentScheduleList(delegator, dept);			
				request.setAttribute("equipment_schedule_List", equipment_schedule_List);
				
				//������ɱ������豸�嵥			
				List pm_form_List = DefaultHelper.getPmFormList(delegator, dept);
				request.setAttribute("pm_form_List", pm_form_List);
				
				//���챣���嵥(�����ձ���)
				List next_pm_List = DefaultHelper.getTommorowPmList(delegator, dept);
				request.setAttribute("next_pm_List", next_pm_List);
				
				//���챣���嵥(�����ձ���)
				List doublenext_pm_List = DefaultHelper.getDayAfterTmrPmList(delegator, dept);
				request.setAttribute("doublenext_pm_List", doublenext_pm_List);
				
				//������(������)��ʩ���豸�����嵥(�����ձ���)
				List nextseven_pm_List = DefaultHelper.getPfNext7dayPmList(delegator);				
				request.setAttribute("nextseven_pm_List", nextseven_pm_List);
				
				//ǰһ������ɱ����嵥(�����ձ���)
				List last_pm_List = DefaultHelper.getLastPmList(delegator, dept);				
				request.setAttribute("last_pm_List", last_pm_List);	
				
				//���ų�δ������ʷ��¼(������ǰ,��������)(�����ձ���),����״̬
				List notdo_pm_List = PlldbHelper.getUndoPmList(delegator, dept, false);			
				request.setAttribute("notdo_pm_List", notdo_pm_List);
				
				//���ų�δ������ʷ��¼(������ǰ,��������)(�����ձ���),05 cost down��03�쳣״̬
				List notdo_pm_down_List = PlldbHelper.getUndoPmList(delegator, dept, true);			
				request.setAttribute("notdo_pm_down_List", notdo_pm_down_List);
			}
			
			//dayPm: ����δ���ձ����嵥			
			if (tabIndex.equals("dayPm")){				
				List today_pm_List1 = DefaultHelper.getTodayPmList(delegator, dept, "1");
				request.setAttribute("today_pm_List1", today_pm_List1);
								
				List today_pm_List2 = DefaultHelper.getTodayPmList(delegator, dept, "2");
				request.setAttribute("today_pm_List2", today_pm_List2);
								
				List today_pm_List3 = DefaultHelper.getTodayPmList(delegator, dept, "3");
				request.setAttribute("today_pm_List3", today_pm_List3);
				
				List today_pm_List4 = DefaultHelper.getTodayPmList(delegator, dept, "4");
				request.setAttribute("today_pm_List4", today_pm_List4);
			}
			
			//eventList: �¼�List
			if (tabIndex.equals("eventList")) {
				List eventList = DefaultHelper.getEventList(delegator, dept);
				request.setAttribute("event_List", eventList);
			}
			
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
		
		return "success";
	}

	/**
	 * ��ʾ��ҳ
	 *
	 * @param request
	 * @param response
	 * @return list
	 */
	public static String queryDefaultSub(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String itemIndex = request.getParameter("itemIndex");
		try{
			String strSQL = "";
			strSQL  = " SELECT   ";
			strSQL += "  EQUIPMENT_SCHEDULE.EQUIPMENT_ID ";
			strSQL += "  ,default_period.PERIOD_INDEX ";
			strSQL += "   ,DEFAULT_PERIOD.PERIOD_NAME ";
			strSQL += " ,PM_FORM.PM_NAME ";
			strSQL += "   ,(to_char(EQUIPMENT_SCHEDULE.SCHEDULE_DATE,'yyyy-MM-dd') || ' 09:00:00') SCHEDULE_DATE ";
			strSQL += "   ,to_char(EQUIPMENT_SCHEDULE.SCHEDULE_DATE,'yyyy-MM-dd') SCHEDULE_DATE1 ";
			strSQL += " ,PM_FORM.PM_INDEX ";
			strSQL += " ,default_period.eqp_status ";
			strSQL += " FROM EQUIPMENT_SCHEDULE ";
			strSQL += " LEFT OUTER JOIN ";
			strSQL += "   PM_FORM ";
			strSQL += " ON ";
			strSQL += "     PM_FORM.EQUIPMENT_ID = EQUIPMENT_SCHEDULE.EQUIPMENT_ID ";
			strSQL += " AND PM_FORM.PERIOD_INDEX = EQUIPMENT_SCHEDULE.PERIOD_INDEX ";
			strSQL += " LEFT OUTER JOIN ";
			strSQL += "  default_period ";
			strSQL += " ON ";
			strSQL += "  default_period.period_index = EQUIPMENT_SCHEDULE.PERIOD_INDEX ";
			strSQL += " WHERE ";
			strSQL += "   EQUIPMENT_SCHEDULE.SCHEDULE_INDEX = " + itemIndex;
			List pm_List = SQLProcess.excuteSQLQuery(strSQL, delegator);
			request.setAttribute("pm_List", pm_List);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
		return "success";
	}
}
