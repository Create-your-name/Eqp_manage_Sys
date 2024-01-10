/** 
  * 版权归上揚软件（上海）有限公司所有
  * 本程序属上揚软件的私有机要资料
  * 未经本公司授权，不得非法传播和盗用
  * 可在本公司授权范围内，使用本程序
  * 保留所有权利
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
   *类 SecuritySetupEvent.java 用于响应UserSetup页面的操作，在Ofbiz框架下运行
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
				throw new Exception("无此PMS账号！");
			}
			
			if (CommonUtil.isLocalUser(accountGv)) {
				// 得到Local登陆者用户组
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
			
			//调用TP，返回User信息
			Map result = SecuritySetupHelper.getAccountInfo((String)attributes.get("accountid"),delegator,userLogin);
			//如果该user不存在，标识新建标志
			
			if (result == null) {
				request.setAttribute("_ERROR_MESSAGE_", "该用户不存在，请重新同步数据！");
				return "error";
			}else{
				//grouplist 为该user所属的group列表				
				groupList = (List) result.get("grouplist");	
				request.setAttribute("description", result.get("description"));
				request.setAttribute("groupList", groupList);
			}

			//取得可选择的所有group列表
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
			//页面中分隔group的分隔符
			String token = "\u0019";
			Map attributes = getInitParams(request);
			GenericDelegator delegator =
				(GenericDelegator) request.getAttribute("delegator");
			HttpSession session = request.getSession();
			GenericValue userLogin =
				(GenericValue) session.getAttribute("userLogin");
			
			//解码所有被选中的group列表
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

			
			//更新user信息以及group
			SecuritySetupHelper.updateAccountInfo((String)attributes.get("accountid"),recodeType,(String)attributes.get("description"),groupList,delegator,userLogin);			
			if((attributes.get("password")!=null&&attributes.get("confirmedPassword")!=null)&&(!attributes.get("password").equals("")||!attributes.get("confirmedPassword").equals(""))){
				if(attributes.get("password").equals(attributes.get("confirmedPassword"))){
					SecuritySetupHelper.changePassword((String)attributes.get("accountid"),(String)attributes.get("password"),delegator,userLogin);
				}else{    
					request.setAttribute(
						"_ERROR_MESSAGE_",
						"<b>两次输入的口令不一致，请重新输入:</b><br>");
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
			//页面中分隔group的分隔符			
			Map attributes = getInitParams(request);
			GenericDelegator delegator =
				(GenericDelegator) request.getAttribute("delegator");
			HttpSession session = request.getSession();
			GenericValue userLogin =
				(GenericValue) session.getAttribute("userLogin");
			//页面中分隔group的分隔符
			String token = "\u0019";
			//解码所有未被选中以及选中的group列表
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
			//将数据放入Attribute，当回到错误页面的时候引用
			if(selectedGroup!=null){
				request.setAttribute("groupList", Arrays.asList(selectedGroup));
			}	
			if(availableGroup!=null){
				request.setAttribute("availableGroups", Arrays.asList(availableGroup));	
			}

			//取出所有的Menu
			DefaultMutableTreeNode dmt = SecurityDAO.getAllPrivTree(delegator);
			request.setAttribute("dmt",dmt);
			
			String accountid = (String) attributes.get("accountid");
			GenericValue accountGv = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", accountid));
			
			if (CommonUtil.isLocalUser(accountGv)) {
				//进行本地校验
				Set accountGuiPriv = new HashSet();
				accountGuiPriv.addAll(SecuritySetupHelper.getLocalPriv(accountid, delegator));
				request.setAttribute("userPriv", accountGuiPriv);
				request.setAttribute("groupPriv", new HashSet());
			} else {
				//取出所有的account、group对应的GuiPriv
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
				//调用TP，返回User信息
				Map result = SecuritySetupHelper.getAccountInfo(request.getParameter("accountid"),delegator,userLogin);
				//grouplist 为该user所属的group列表
				List groupList = (List) result.get("grouplist");
				//取得可选择的所有group列表
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
				//放入原来的用户所输入的ID
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
			//调用TP，返回User信息
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
			//调用TP，返回User信息
			List groupNames = SecuritySetupHelper.getAllGroupNames(delegator,userLogin);
			// 如果是第一次进入该页面，默认使用第一个Group 作为当前操作的group。				
			String groupName =(String) request.getParameter("accountid");
			if (groupName==null){
				groupName= (String) groupNames.get(0);
			}
//				 取得所有的GroupPriv
			Map privMap = SecuritySetupHelper.getAllGuiPriv(groupName,delegator,userLogin);
			Collection groupPriv = (Collection) privMap.get("accountPriv");
			Collection legencyPrivs = (Collection) privMap.get("groupGuiPriv");
			//	取得menu tree
			DefaultMutableTreeNode dmt = SecurityDAO.getAllPrivTree(delegator);				
			// 将groupName，groupNames，groupPriv，legencyPrivs，dmt放入request
			
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
	
	//修改用户在Promis中的密码
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
					"<b>你输入的密码有误，请重新输入！</b><br>"
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
					"<b>你成功地修改了密码！</b><br>"
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
