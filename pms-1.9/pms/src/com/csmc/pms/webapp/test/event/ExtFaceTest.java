package com.csmc.pms.webapp.test.event;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.common.helper.CommonHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.MiscUtils;
import com.csmc.pms.webapp.workflow.help.WorkflowHelper;

public class ExtFaceTest {
	public static final String module = ExtFaceTest.class.getName();
	
	public static void queryEquipmentType(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		JSONArray eqpTypeJson = new JSONArray();
		try {
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
	 
	 public static void queryPcStyle(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		JSONArray pcStyleJson = new JSONArray();
		try {
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
	 
	 public static String queryPMPeriodByEqpType(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 String equipmentType = request.getParameter("equipmentType");
		 JSONArray periodJson = new JSONArray();
		 try {
			 //查询获得PM周期
			 List periodList = delegator.findByAnd("DefaultPeriod", UtilMisc.toMap("eqpType",equipmentType));
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

	 public static String queryPcPeriodByPcStyle(HttpServletRequest request,
				HttpServletResponse response) {
		 GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		 String styleIndex = request.getParameter("pcStyle");
		 JSONArray periodJson = new JSONArray();
		 try {
			 //查询获得PC周期
			 List periodList = delegator.findByAnd("PcPeriod", UtilMisc.toMap("styleIndex",styleIndex));
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
	 
	public static String sendMailTest(HttpServletRequest request, HttpServletResponse response) {
		try {
			Properties p = UtilProperties.getProperties("csmcgui");
			String auth = p.get("mail.auth.user").toString();
			String host = p.get("mail.smtp.host").toString();
			String de = p.get("mail.defaultsendaddress").toString();
//			String sendFrom = Constants.IT_SYSTEM_MAIL;
//			String sendFrom = "zhuchong11@rxgz.crmicro.com";
			String sendFrom = de;
			String sendTo = "liuhai82@rxgz.crmiro.com";
			String sendCc = "";
			String mailSubject = "测试发送邮件!";
			String mailContent = "发件人: " + sendFrom + "\n";
			mailContent += "收件人: " + sendTo + "\n";
			mailContent += "auth: " + auth + "\n";
			mailContent += "host: " + host + "\n";
			mailContent += "结束！";
			CommonUtil.sendMail(sendFrom, sendTo, sendCc, mailSubject, mailContent);
			Debug.logInfo("sendFrom:  "+ sendFrom+"\t sendTo: "+sendTo+"\t host:  "+host +"\t de:  "+de+"\t auth:  "+auth+"\t mailContent: "+mailContent ,module);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
		return null;
	}
}
