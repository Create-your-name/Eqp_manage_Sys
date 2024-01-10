package com.csmc.pms.webapp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.alibaba.fastjson.JSONObject;
import com.csmc.asura.core.Asura;
import com.csmc.asura.core.AsuraClient;
import com.fa.util.Log;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ModelService;

import com.csmc.asura.exception.AsuraException;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.eqp.helper.PlldbHelper;
import com.fa.object.Account;

/**
 * fab5 asura接口
 * @author dinghh
 * @2013-7-25
 */
public class AsuraServiceHandle {
	public static final String module = AsuraServiceHandle.class.getName();

	public static Map runAsuraService(GenericDelegator delegator,GenericValue userLogin,Map map,String serviceName) throws TPServiceException {
		Map result = new HashMap();
		try {	
			String sysAccount = (String) userLogin.get("userLoginId");
			String sysPassword = null;
			//获取gui密码
//			StringBuffer pdSql = new StringBuffer("select * from USER_PASSWORD@f6gui WHERE USER_ID ='");
//			pdSql.append(sysAccount).append("'");
//			List pdList = SQLProcess.excuteSQLQuery(pdSql.toString(), delegator);
//			if (pdList != null && !pdList.isEmpty()) {
//				Map csmcPasswordMap = (Map) pdList.get(0);
//				sysPassword = (String) csmcPasswordMap.get("PASSWORD");
//			} else {
				// 新用户初始密码abc123
				sysPassword = "abc123";
//			}

			if (Constants.LOGIN_CHECK.equals(serviceName)) {
				//用户登录
				login(userLogin);

			} else if (Constants.EQP_INFO_QUERY.equals(serviceName)) {
				String eqpid = (String) map.get("eqpid");

				/* 修改k栋s栋设备的判断顺序 by moxiaoqi 20180712
				if (PlldbHelper.isFab5Eqp(delegator, eqpid)){
					//查询fab5设备状态
					result.put("status", eqpInfoQuery(eqpid));
				} else if (PlldbHelper.isFab6Eqp(delegator, eqpid)) {
					//查询fab6设备状态
					result = TPServiceHandle.runTPService(delegator, userLogin, map, serviceName);
				}
				*/

//				if (PlldbHelper.isFab6Eqp(delegator, eqpid)) {
//					//查询fab6设备状态
//					userLogin.set("currentPassword", sysPassword);
//					result = TPServiceHandle.runTPService(delegator, userLogin, map, serviceName);
//				}else if (PlldbHelper.isFab5Eqp(delegator, eqpid)){
					//查询fab5设备状态
					result.put("status", eqpInfoQuery(eqpid,delegator));
//				}

			} else if (Constants.EQP_STATUS_CHANGE.equals(serviceName)) {
				String eqpid = (String) map.get("eqpid");
				String newstatus = (String) map.get("newstatus");

                /* 修改k栋s栋设备的判断顺序 by moxiaoqi 20180712
				//1.修改fab5设备状态
				if (PlldbHelper.isFab5Eqp(delegator, eqpid)) {
					eqpStatusChange(userLogin, eqpid, newstatus);
				}
				*/

				//2.fab6薄片或兼做设备，修改fab6设备状态
				//160524 dinghh: fab6设备状态为04MONPM时，开始或结束保养表单不改设备状态
//				if (PlldbHelper.isFab6Eqp(delegator, eqpid) && !"04MONPM".equals(PlldbHelper.getFab6EqpStatus(delegator, eqpid))) {
//					String newstatus_fab6 = newstatus;
//					//if (Constants.PM_END_STATUS.equals(newstatus)) {
//					//	newstatus_fab6 = "04MONPM";
//					//} else if (newstatus.startsWith("04")) {
//					//	newstatus_fab6 = "04-PM";
//					//}
//					if ("04_MON_PM".equals(newstatus)) {
//					    newstatus_fab6 = "04MONPM";
//					}
//					if ("04_PM".equals(newstatus)) {
//					    newstatus_fab6 = "04-PM";
//					}
//
//					Map statusMap = new HashMap();
//		            		statusMap.put("eqpid", eqpid);
//		            		statusMap.put("newstatus", newstatus_fab6);
//		            		statusMap.put("comment", "PMS");
//					userLogin.set("currentPassword", sysPassword);
//					TPServiceHandle.runTPService(delegator, userLogin, statusMap, serviceName);
//				}

//				else
				if (PlldbHelper.isFab5Eqp(delegator, eqpid)) {
					eqpStatusChange(userLogin, eqpid, newstatus,delegator);
				}

			} else if (Constants.ACCOUNT_LIST_QUERY.equals(serviceName)) {
				//查询所有用户或用户组列表
				List accountlist = new ArrayList();
				List namelist = new ArrayList();

				String rectype = (String) map.get("rectype");
				if ("U".equals(rectype)) {
					//query Account
					namelist = getAllUserNames(delegator);
				} else if ("G".equals(rectype)) {
					//query FW_GROUP_VIEW
					namelist = getAllGroupNames(delegator);
				}

				for (int i = 0; i < namelist.size(); i++) {
					Map map1 = (Map) namelist.get(i);
					Account account = new Account();
					account.setAccountID((String) map1.get("ACCOUNT_ID"));
					accountlist.add(account);
				}

				result.put("accountlist", accountlist);
			} else if (Constants.ACCOUNT_INFO_QUERY.equals(serviceName)) {
				//查询用户信息
				String accountid = (String) map.get("accountid");
				GenericValue accountGv = AccountHelper.getAccountByNo(delegator, accountid);
				if (accountGv != null) {
					String description = accountGv.getString("accountName");
					String roles = accountGv.getString("roles");
					String[] groups = roles.split(",");
					List grouplist = UtilMisc.toListArray(groups);

					result.put("owner", description);
					result.put("grouplist", grouplist);
				}
			}
/*			else if (Constants.CHANGE_PASSWORD.equals(serviceName)){
				// 修改密码
				int i = updateUserInfo(delegator, userLogin, map);
			}*/
		} catch (AsuraException e) {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.toString());
			Debug.logError(e, module);
			//throw e;
			throw new TPServiceException(e);
		} catch (Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.toString());
			Debug.logError(e, module);
			throw new TPServiceException(e);
		}

		return result;
	}

	// check login
/*	public static void login(GenericValue userLogin) throws Exception {
		try {
			Asura asura = new AsuraClient();
			asura.login(userLogin.getString("userLoginId"),userLogin.getString("currentPassword"));
		} catch(AsuraException e) {
			if(com.csmc.asura.util.Constants.ACCOUNT_NOT_FOUND_EXCEPTION.equals(e.getErrorCode())) {
				throw new Exception("MES帐号未建立，请联系管理员！");
			}
			throw new Exception("MES密码不对！");
		}
	}*/
	public static void login(GenericValue userLogin) throws Exception {
		String loginMsg = null;
		String message = null;
		try {
			loginMsg = CommonUtil.loginCheckForMES(userLogin.getString("userLoginId"), userLogin.getString("currentPassword"));
			JSONObject jsonObject = JSONObject.parseObject(loginMsg);
			if (StringUtils.isEmpty(loginMsg)){
				throw new Exception("MES帐号未建立，请联系管理员！");
			}else if (StringUtils.isNotEmpty(jsonObject.getString("code"))){
				String code = jsonObject.getString("code");
					if ("5".equals(code)){
						throw new Exception("MES帐号未建立，请联系管理员！");
					}else if ("1".equals(code)){
						throw new Exception(userLogin.getString("userLoginId")+"密码错误!");
					}
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	//查询设备状态
/*	public static String eqpInfoQuery(String eqpId) throws AsuraException {
		Asura asura = new AsuraClient();
		String eqpStatus = asura.queryMesEqpStatus(eqpId);
		return eqpStatus;
	}*/
	public static String eqpInfoQuery(String eqpId,GenericDelegator delegator) throws Exception {
		String eqpStatus = null;
		try {
			String eqpStatusMsg = CommonUtil.queryMesEqpStatus(eqpId,delegator);
			JSONObject jsonObject = JSONObject.parseObject(eqpStatusMsg);
			if (StringUtils.isNotEmpty(jsonObject.getString("code"))){
				String code = jsonObject.getString("code");
				if ("2".equals(code)){
					Debug.logInfo("查询设备:["+eqpId+" ] 状态错误 \t"+jsonObject,module);
					throw new RuntimeException("设备"+eqpId +"不存在或未激活或指定的时间戳过期.");
				}
			}else {
				eqpStatus = jsonObject.getString("state");
			}
		} catch (RuntimeException e) {
			Debug.logError("查询设备:["+eqpId+" ] 状态错误 \t"+e.getMessage(),module);
			throw new RuntimeException(e.getMessage());
		}
		Debug.logInfo("查询设备: ["+eqpId+" ]状态为: ["+eqpStatus+" ]",module);
		return eqpStatus;
	}

	//修改设备状态
	public static void eqpStatusChange(GenericValue userLogin, String eqpId, String eqpState,GenericDelegator delegator) throws Exception {
		try {
			String postResult = CommonUtil.changeMesEqpStatus(eqpId, eqpState, userLogin.getString("userLoginId"),delegator);
			JSONObject jsonObject = JSONObject.parseObject(postResult);
			if(jsonObject == null){
				return;
			}
			if (StringUtils.isNotEmpty(jsonObject.getString("code"))){
				String code = jsonObject.getString("code");
				if ("27".equals(code)){
					Debug.logError("未找到状态: ["+eqpState+" ] \t"+jsonObject,module);
					throw new Exception("未找到状态"+eqpState+".");
				}
			}
/*			String errorMsg = "";
			if (eqpId.startsWith("6")) {
				errorMsg = "请检查确认 PM系统 与 GUI 登录密码是否一致，需先修改为相同的密码。";
			}
			if (e.getErrorCode().equals(com.csmc.asura.util.Constants.EQUIPMENT_NOT_FOUND_EXCEPTION)) { //ASURA_00008
				throw new Exception(errorMsg + "MES系统中没有为该设备输入SPC数据，无法修改设备状态!");
			} else if (e.getErrorCode().equals(com.csmc.asura.util.Constants.OPRATOR_NOT_PRIVILEGE_EXCEPTION)) { //ASURA_00009
				throw new Exception(errorMsg + "MES系统中用户没有修改该该设备的权限，无法修改设备状态!");
			} else if(e.getErrorCode().equals(com.csmc.asura.util.Constants.EQUIPMENT_STATE_NOT_CHANGE_EXCEPTION)) {//ASURA_00010
				throw new Exception(errorMsg + "MES系统中该设备不能修改为新的状态，无法修改设备状态！");
			}*/
			Debug.logInfo("更改设备: ["+eqpId+" ]状态为: ["+eqpState+" ]成功",module);
		} catch (Exception e) {
			Debug.logError("更改设备: ["+eqpId+" ]状态为: ["+eqpState+" ] 失败\t"+e.getMessage(),module);
			throw new Exception("改变设备状态失败！\r\n" + e.getMessage());
		}
	}
/*	public static void eqpStatusChange(GenericValue userLogin, String eqpId, String eqpState) throws Exception {
		try {
			Asura asura = new AsuraClient();
			asura.changeMesEqpStatus(eqpId, eqpState, userLogin.getString("userLoginId"));
		} catch (AsuraException e) {
			String errorMsg = "";
			if (eqpId.startsWith("6")) {
				errorMsg = "请检查确认 PM系统 与 GUI 登录密码是否一致，需先修改为相同的密码。";
			}

        	if (e.getErrorCode().equals(com.csmc.asura.util.Constants.EQUIPMENT_NOT_FOUND_EXCEPTION)) {
				throw new Exception(errorMsg + "MES系统中没有为该设备输入SPC数据，无法修改设备状态!");
			} else if (e.getErrorCode().equals(com.csmc.asura.util.Constants.OPRATOR_NOT_PRIVILEGE_EXCEPTION)) {
				throw new Exception(errorMsg + "MES系统中用户没有修改该该设备的权限，无法修改设备状态!");
			} else if(e.getErrorCode().equals(com.csmc.asura.util.Constants.EQUIPMENT_STATE_NOT_CHANGE_EXCEPTION)) {
				throw new Exception(errorMsg + "MES系统中该设备不能修改为新的状态，无法修改设备状态！");
			} else {
				throw new Exception(errorMsg + "改变设备状态失败！\r\n" + e.getMessage());
			}
        }
	}*/

	//fab5查询数据库中的所有用户
	private static List getAllUserNames(GenericDelegator delegator) throws SQLProcessException {
		String sql = "select account_no account_id from ACCOUNT t order by t.account_no";
        List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}

	//fab5查询数据库中的所有用户组
	private static List getAllGroupNames(GenericDelegator delegator) throws SQLProcessException {
		String sql = "select t.user_group account_id from FW_GROUP_VIEW t";
        List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}

	//  修改密码
/*	private static int updateUserInfo(GenericDelegator delegator,GenericValue userLogin,Map map) throws SQLProcessException {

		String sql = "update account set password = '"+(String) map.get("newpassword") +"' where account_NO = '"+(String) map.get("accountid")+"'";
		int row = SQLProcess.excuteSQLUpdate(sql, delegator);
		return row;
	}*/

}