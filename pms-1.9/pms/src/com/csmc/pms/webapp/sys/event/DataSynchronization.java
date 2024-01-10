/*
 * Created on 2004-7-22
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.csmc.pms.webapp.sys.event;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.sys.helper.SecuritySetupHelper;
import com.csmc.pms.webapp.util.GeneralEvents;



/** 
 * @author Sky
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DataSynchronization extends GeneralEvents {
	   
	public static final String module = GroupSetupEvent.class.getName();
	
	//获取Group列表
	public static String getGroupList(HttpServletRequest request, HttpServletResponse response) {
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		try {
			//取得所有Group的名称
			List groupList = SecuritySetupHelper.getAllGroupNames(dispatcher.getDelegator(), userLogin);
			Map groupMap = new TreeMap();
			Iterator groupItor = groupList.iterator();
			//将其保持到HashMap中
			while (groupItor.hasNext()) {
				String groupName = (String)groupItor.next();
				groupMap.put(groupName, groupName);
			}
			request.setAttribute("group_setup_all_group_map", groupMap);
		} catch (Exception e) {
			Debug.logError(e, "Error calling getGroupList service", module);
			request.setAttribute("_ERROR_MESSAGE_", "<b>The following error occurred during getGroupList:</b><br>" + e.getMessage());
			return "error";
		}
	
		return "success";
	}
	
	public static String synWip(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map context = new HashMap();
		
		try {
			context.put("userlogin", userLogin);
		   	dispatcher.runAsync("syncWip", context, false);
		   	request.setAttribute(
					   "_EVENT_MESSAGE_",
					   "<b>WIP 表与Promis正在后台进行同步!</b>");
	   } catch (Exception e) {
			Debug.logError(e, "Error calling getGroupList service", module);
			request.setAttribute("_ERROR_MESSAGE_", "<b>WIP 与 Promis 同步时出现如下错误:</b><br>" + e.getMessage());
			return "error";
	   }
		return "success";
	}
	
	public static void checkPmsStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		JSONObject result = new JSONObject();
		String msg = "success";
		String item_count = "1";
		String excu_item_count = "1";
		String err_item_no = "0";
		String err_key_item_no = "0";
		try {
			try {
				String sql = "select sysdate from dual";
				List list = SQLProcess.excuteSQLQuery(sql, delegator);
			} catch (Exception e) {
				msg = "PMS服务异常:连接PMS DB不通";
				err_item_no = "1";
				err_key_item_no = "1";
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		result.put("item_count", item_count);
		result.put("excu_item_count", excu_item_count);
		result.put("err_item_no", err_item_no);
		result.put("err_key_item_no", err_key_item_no);
		result.put("msg", msg);
		response.getWriter().write(result.toString());

	}
	
}	