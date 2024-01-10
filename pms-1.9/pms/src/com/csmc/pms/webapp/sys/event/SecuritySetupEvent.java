/** 
  * ��Ȩ���ϓP������Ϻ������޹�˾����
  * ���������ϓP�����˽�л�Ҫ����
  * δ������˾��Ȩ�����÷Ƿ������͵���
  * ���ڱ���˾��Ȩ��Χ�ڣ�ʹ�ñ�����
  * ��������Ȩ��
  */
package com.csmc.pms.webapp.sys.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.tree.DefaultMutableTreeNode;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import com.csmc.pms.webapp.eqp.helper.FabAdapter;
import com.csmc.pms.webapp.security.SecurityDAO;
import com.csmc.pms.webapp.sys.helper.SecuritySetupHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.GeneralEvents;
import com.csmc.pms.webapp.util.TPServiceException;
import com.fa.object.Account;
import com.fa.object.ObjectCollection;

/**
   *�� SecuritySetupEvent.java ������ӦUserSetupҳ��Ĳ�������Ofbiz���������
   *@version  1.0  2004-8-9
   *@author   Sky
   */
public class SecuritySetupEvent extends GeneralEvents {
	public static String getUserInfo(
		HttpServletRequest request,
		HttpServletResponse response) {
		try {
			Map attributes = getInitParams(request, true);
			GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
			HttpSession session = request.getSession();
			GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
			List groupList = null;
			
			String accountid = (String)attributes.get("accountid");
			GenericValue accountGv = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", accountid));
			if (accountGv == null) {
				throw new Exception("�޴�PMS�˺ţ�");
			}
			
			if (CommonUtil.isLocalUser(accountGv)) {
				// �õ�Local��½���û���
				String roles = accountGv.getString("roles");
				if (roles != null) {
					groupList = new ArrayList();
					String[] roleArr = roles.split(",");
					for(int i = 0; i < roleArr.length; i++) {
						String role = roleArr[i];
						groupList.add(role);
					}
					request.setAttribute("groupList", groupList);
					request.setAttribute("availableGroups",	groupList);
					request.setAttribute("accountid", accountid);
				}
				
				return "success";
			}
			
			//����TP������User��Ϣ
			Map result = SecuritySetupHelper.getAccountInfo((String)attributes.get("accountid"),delegator,userLogin);
			//�����user�����ڣ���ʶ�½���־
			
			if (result == null) {
				request.setAttribute("_ERROR_MESSAGE_", "���û������ڣ�������ͬ�����ݣ�");
				return "error";
			}else{
				//grouplist Ϊ��user������group�б�				
				groupList = (List) result.get("grouplist");	
				request.setAttribute("description", result.get("description"));
				request.setAttribute("groupList", groupList);
			}

			//ȡ�ÿ�ѡ�������group�б�
			List availableGroupList = SecuritySetupHelper.getAvailableGroupList(request,delegator,userLogin,groupList);
			//System.out.println("size = " + availableGroupList.size());   
			if(availableGroupList!=null){
				Collections.sort(availableGroupList);
			}
			request.setAttribute("availableGroups",	availableGroupList);
			putResultToRequest(result, request);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(
				"_ERROR_MESSAGE_",
				"<b>The following error occurred during get User Info:</b><br>"
					+ e.getMessage());
			return "error";
		}
		
		return "success";
	}

	public static String getUserList(
		HttpServletRequest request,
		HttpServletResponse response) {
		try {
			GenericDelegator delegator =
				(GenericDelegator) request.getAttribute("delegator");
			HttpSession session = request.getSession();
			GenericValue userLogin =
				(GenericValue) session.getAttribute("userLogin");
			List users = SecuritySetupHelper.getAccountList("U",delegator,userLogin);
			request.setAttribute("users",users);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(
				"_ERROR_MESSAGE_",
				"<b>The following error occurred during get User List:</b><br>"
					+ e.getMessage());
			return "error";
		}
		return "success";
	}
	/**
	 * @url element://model:project::csmcgui/design:view:::V21xx84dxgs68wn-288bnm
	 */

	public static String updateUserInfo(
		HttpServletRequest request,
		HttpServletResponse response) {
			String recodeType = null;
			if("true".equalsIgnoreCase(request.getParameter("isNewUser"))){
				recodeType = "U";
				request.setAttribute("isNewUser","true");
			}			
		try {
			//ҳ���зָ�group�ķָ���
			String token = "\u0019";
			Map attributes = getInitParams(request);
			GenericDelegator delegator =
				(GenericDelegator) request.getAttribute("delegator");
			HttpSession session = request.getSession();
			GenericValue userLogin =
				(GenericValue) session.getAttribute("userLogin");
			
			//�������б�ѡ�е�group�б�
			String[] selectedGroup = null;
			if (!"".equals(request.getParameter("selectedGroup"))) {
				selectedGroup =
					request.getParameter("selectedGroup").split(token);
			} 
			List groupList = null;
			if(selectedGroup!=null){
				groupList =Arrays.asList(selectedGroup);				
				request.setAttribute("groupList", groupList);
			}
			List availableGroupList = SecuritySetupHelper.getAvailableGroupList(request,delegator,userLogin,groupList);			
			request.setAttribute("availableGroups",availableGroupList);

			
			//����user��Ϣ�Լ�group
			SecuritySetupHelper.updateAccountInfo((String)attributes.get("accountid"),recodeType,(String)attributes.get("description"),groupList,delegator,userLogin);			
			if((attributes.get("password")!=null&&attributes.get("confirmedPassword")!=null)&&(!attributes.get("password").equals("")||!attributes.get("confirmedPassword").equals(""))){
				if(attributes.get("password").equals(attributes.get("confirmedPassword"))){
					SecuritySetupHelper.changePassword((String)attributes.get("accountid"),(String)attributes.get("password"),delegator,userLogin);
				}else{    
					request.setAttribute(
						"_ERROR_MESSAGE_",
						"<b>��������Ŀ��һ�£�����������:</b><br>");
					return "error";				
				}   
			}
			request.setAttribute("isNewUser","false");
  
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(
				"_ERROR_MESSAGE_",
				"<b>The following error occurred during update user:</b><br>"
					+ e.getMessage());
			return "error";
		}
		return "success";
	}


	/**
	 * @url element://model:project::csmcgui/design:view:::V21xx84dxgsc3m0-n3uo8y
	 */
	public static String setupUserPriv(    
		HttpServletRequest request,
		HttpServletResponse response) {   
		try {
			//ҳ���зָ�group�ķָ���			
			Map attributes = getInitParams(request);
			GenericDelegator delegator =
				(GenericDelegator) request.getAttribute("delegator");
			HttpSession session = request.getSession();
			GenericValue userLogin =
				(GenericValue) session.getAttribute("userLogin");
			//ҳ���зָ�group�ķָ���
			String token = "\u0019";
			//��������δ��ѡ���Լ�ѡ�е�group�б�
			String[] availableGroup = null;
			if (!"".equals(request.getParameter("availableGroup"))&&request.getParameter("availableGroup")!=null) {
				availableGroup =
					request.getParameter("availableGroup").split(token);
			}		
			String[] selectedGroup = null;
			if (!"".equals(request.getParameter("selectedGroup"))&&request.getParameter("selectedGroup")!=null) {
				selectedGroup =
					request.getParameter("selectedGroup").split(token);
			}					
			//�����ݷ���Attribute�����ص�����ҳ���ʱ������
			if(selectedGroup!=null){
				request.setAttribute("groupList", Arrays.asList(selectedGroup));
			}	
			if(availableGroup!=null){
				request.setAttribute("availableGroups", Arrays.asList(availableGroup));	
			}

			//ȡ�����е�Menu
			DefaultMutableTreeNode dmt = SecurityDAO.getAllPrivTree(delegator);
			request.setAttribute("dmt",dmt);
			
			String accountid = (String) attributes.get("accountid");
			GenericValue accountGv = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", accountid));
			
			if (CommonUtil.isLocalUser(accountGv)) {
				//���б���У��
				Set accountGuiPriv = new HashSet();
				accountGuiPriv.addAll(SecuritySetupHelper.getLocalPriv(accountid, delegator));
				request.setAttribute("userPriv", accountGuiPriv);
				request.setAttribute("groupPriv", new HashSet());
			} else {
				//ȡ�����е�account��group��Ӧ��GuiPriv
				Map guiPrivMap = SecuritySetupHelper.getAllGuiPriv((String)attributes.get("accountid"),delegator,userLogin);
				request.setAttribute("userPriv",guiPrivMap.get("accountPriv"));
				request.setAttribute("groupPriv",guiPrivMap.get("groupGuiPriv"));
			}		

		} catch (Exception e) { 
			e.printStackTrace();
			request.setAttribute(
				"_ERROR_MESSAGE_",
				"<b>The following error occurred during get user priv:</b><br>"
					+ e.getMessage());
			return "error";
		}
		return "success";  
	}

	public static String copyUserInfo(
		HttpServletRequest request,
		HttpServletResponse response) 	{
			try {
				GenericDelegator delegator =
					(GenericDelegator) request.getAttribute("delegator");
				HttpSession session = request.getSession();
				GenericValue userLogin =
					(GenericValue) session.getAttribute("userLogin");
				//����TP������User��Ϣ
				Map result = SecuritySetupHelper.getAccountInfo(request.getParameter("accountid"),delegator,userLogin);
				//grouplist Ϊ��user������group�б�
				List groupList = (List) result.get("grouplist");
				//ȡ�ÿ�ѡ�������group�б�
				List availableGroupList = null;
				availableGroupList =
					SecuritySetupHelper.getAvailableGroupList(
						request,
						delegator,
						userLogin,
						groupList); 
				request.setAttribute(
						"groupList",
						groupList);
				//����ԭ�����û��������ID
				request.setAttribute("accountid",request.getParameter("newaccountid"));
				request.setAttribute(   
					"availableGroups",
					availableGroupList);
				request.setAttribute(
					"isNewUser",
				request.getParameter("isNewUser"));					
									

			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute(
					"_ERROR_MESSAGE_",
					"<b>The following error occurred during get User Info:</b><br>"
						+ e.getMessage());
				return "error";
			}
			return "success";	
		
	}
	
	/**
	 * @url element://model:project::csmcgui/design:view:::V21xx84dxh00t6sid3s2m
	 */
	public static String deleteUser(
		HttpServletRequest request,
		HttpServletResponse response) 	{
		try {
			String userId = request.getParameter("accountid");
			Account account = new Account();
			account.setAccountID(userId);
			ObjectCollection oc = new ObjectCollection();
			oc.add(account);
			GenericDelegator delegator =
				(GenericDelegator) request.getAttribute("delegator");
			HttpSession session = request.getSession();
			GenericValue userLogin =
				(GenericValue) session.getAttribute("userLogin");
			//����TP������User��Ϣ
			Map result = FabAdapter.runCallService(delegator,userLogin,UtilMisc.toMap("userlist",oc),Constants.ACCOUNT_DELETE);
			putResultToRequest(result, request);

		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(
				"_ERROR_MESSAGE_",
				"<b>The following error occurred during delete user:</b><br>"
					+ e.getMessage());
			return "error";
		}
		return "success";		
	}
	
	public static String getUserGroupInfo(
		HttpServletRequest request,
		HttpServletResponse response) 	{
		try {
			Map attributes = getInitParams(request);
			GenericDelegator delegator =
				(GenericDelegator) request.getAttribute("delegator");
			HttpSession session = request.getSession();
			GenericValue userLogin =
				(GenericValue) session.getAttribute("userLogin");
			//����TP������User��Ϣ
			List groupNames = SecuritySetupHelper.getAllGroupNames(delegator,userLogin);
			// ����ǵ�һ�ν����ҳ�棬Ĭ��ʹ�õ�һ��Group ��Ϊ��ǰ������group��				
			String groupName =(String) request.getParameter("accountid");
			if (groupName==null){
				groupName= (String) groupNames.get(0);
			}
//				 ȡ�����е�GroupPriv
			Map privMap = SecuritySetupHelper.getAllGuiPriv(groupName,delegator,userLogin);
			Collection groupPriv = (Collection) privMap.get("accountPriv");
			Collection legencyPrivs = (Collection) privMap.get("groupGuiPriv");
			//	ȡ��menu tree
			DefaultMutableTreeNode dmt = SecurityDAO.getAllPrivTree(delegator);				
			// ��groupName��groupNames��groupPriv��legencyPrivs��dmt����request
			
			request.setAttribute("groupNames", groupNames);
			request.setAttribute("groupName", groupName);
			request.setAttribute("groupPriv", groupPriv);
			request.setAttribute("legencyPrivs", legencyPrivs);
			request.setAttribute("dmt", dmt);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(
				"_ERROR_MESSAGE_",
				"<b>The following error occurred during get UserGroup Info:</b><br>"
					+ e.getMessage());
			return "error";
		}
		return "success";		
	}
	
	//�޸��û���Promis�е�����
	public static String changePwdDone(
		HttpServletRequest request,
		HttpServletResponse response) 	{
		try {
			GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
			HttpSession session = request.getSession();
			GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
			
			String userLoginName = (String) userLogin.get("userLoginId");
			String userLoginPwd = (String) userLogin.get("currentPassword");
			String oldPwd = (String) request.getParameter("oldPwd");
			String newPwd = (String) request.getParameter("newPwd");
			
			if (!userLoginPwd.toUpperCase().equalsIgnoreCase(oldPwd.toUpperCase())){
				request.setAttribute("_ERROR_MESSAGE_",
					"<b>������������������������룡</b><br>"
				);
				return "error";
			}
			
			if(Constants.CALL_TP_FLAG) {
				Map map = new HashMap();
				map.put("accountid",userLoginName);
				map.put("newpassword",newPwd);
				
				Map result =
					FabAdapter.runCallService(
						delegator,
						userLogin,
						map,
						Constants.CHANGE_PASSWORD);
				
				request.setAttribute("_ERROR_MESSAGE_",
					"<b>��ɹ����޸������룡</b><br>"
				);
			} else {
				GenericValue account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", userLoginName));
				account.setString("password", newPwd.trim().toUpperCase());
				delegator.store(account);
			}
			
			request.setAttribute("changePwdResult","OK");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(
				"_ERROR_MESSAGE_",
				"<b>The following error occurred during change user password:</b><br>"
					+ e.getMessage());
			return "error";
		}
		return "success";		
	}
}
