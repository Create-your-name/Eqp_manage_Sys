/*
 * Created on 2004-7-22
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.csmc.pms.webapp.sys.event;

import java.util.*;

import javax.servlet.http.*;
import javax.swing.tree.DefaultMutableTreeNode;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.service.LocalDispatcher;

import com.fa.object.*; 
import com.csmc.pms.webapp.util.*;
import com.csmc.pms.webapp.security.*;
import com.csmc.pms.webapp.security.login.LoginEvents;
import com.csmc.pms.webapp.sys.helper.SecuritySetupHelper;
/** 
 * @author Sky
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GroupSetupEvent extends GeneralEvents {
	   
	public static final String module = GroupSetupEvent.class.getName();
	
	//��ȡGroup�б�
	public static String getGroupList(HttpServletRequest request, HttpServletResponse response) {
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		try {
			//ȡ������Group������
			List groupList = SecuritySetupHelper.getAllGroupNames(dispatcher.getDelegator(), userLogin);
			Map groupMap = new TreeMap();
			Iterator groupItor = groupList.iterator();
			//���䱣�ֵ�HashMap��
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
	
	public static String getGroupInfo(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String groupId = request.getParameter("group_setup_group_id");
		
		GenericDelegator delegator = dispatcher.getDelegator();
		
		try{
			//����TP������Group��Ϣ
			Map result = SecuritySetupHelper.getAccountInfo(groupId, delegator, userLogin);
			
			if (result.get("accountid") == null) {
				request.setAttribute("_ERROR_MESSAGE_",	"<b>The following error occurred during get Group Info: Group Not Exist!</b><br>");
				return "error";
			}
			//groupPrivSetΪ��group��ӵ�е�PromisȨ��
			Set groupPrivSet = (Set)result.get("privSet");
						
			//����group��Ȩ�޵õ�gui��Ȩ��
			groupPrivSet = SecuritySetupHelper.convertPromisPrivToGuiPriv(groupPrivSet);
			
			//ȡ��gui��localȨ��
			Set groupLocaPrivSet = new HashSet();
			GenericDelegator pmDelegator = (GenericDelegator) request.getAttribute("delegator");
			List guiNotPromis = pmDelegator.findByAnd("AccountPrivs", UtilMisc.toMap("accountId", groupId));
			Iterator itorGuiNotPromis = guiNotPromis.iterator();
			while (itorGuiNotPromis.hasNext()) {
				GenericValue guiNotPromisValue = (GenericValue) itorGuiNotPromis.next();
				groupPrivSet.add((String)guiNotPromisValue.get("localPriv"));
			}
			List list = new LinkedList();
			list.addAll(groupPrivSet);
			//ȡ����Group��Ȩ�����ṹ
			DefaultMutableTreeNode privTree = SecurityDAO.getUserPrivTree(delegator, list);
			request.setAttribute("group_setup_group_priv_tree", privTree);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", "<b>The following error occurred during getGroupInfo:</b><br>" + e.getMessage());
			return "error";
		}
		
		return "success";
		
	}

}
