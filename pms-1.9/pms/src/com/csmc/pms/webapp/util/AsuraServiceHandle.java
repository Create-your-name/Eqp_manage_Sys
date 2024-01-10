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
 * fab5 asura�ӿ�
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
			//��ȡgui����
//			StringBuffer pdSql = new StringBuffer("select * from USER_PASSWORD@f6gui WHERE USER_ID ='");
//			pdSql.append(sysAccount).append("'");
//			List pdList = SQLProcess.excuteSQLQuery(pdSql.toString(), delegator);
//			if (pdList != null && !pdList.isEmpty()) {
//				Map csmcPasswordMap = (Map) pdList.get(0);
//				sysPassword = (String) csmcPasswordMap.get("PASSWORD");
//			} else {
				// ���û���ʼ����abc123
				sysPassword = "abc123";
//			}

			if (Constants.LOGIN_CHECK.equals(serviceName)) {
				//�û���¼
				login(userLogin);

			} else if (Constants.EQP_INFO_QUERY.equals(serviceName)) {
				String eqpid = (String) map.get("eqpid");

				/* �޸�k��s���豸���ж�˳�� by moxiaoqi 20180712
				if (PlldbHelper.isFab5Eqp(delegator, eqpid)){
					//��ѯfab5�豸״̬
					result.put("status", eqpInfoQuery(eqpid));
				} else if (PlldbHelper.isFab6Eqp(delegator, eqpid)) {
					//��ѯfab6�豸״̬
					result = TPServiceHandle.runTPService(delegator, userLogin, map, serviceName);
				}
				*/

//				if (PlldbHelper.isFab6Eqp(delegator, eqpid)) {
//					//��ѯfab6�豸״̬
//					userLogin.set("currentPassword", sysPassword);
//					result = TPServiceHandle.runTPService(delegator, userLogin, map, serviceName);
//				}else if (PlldbHelper.isFab5Eqp(delegator, eqpid)){
					//��ѯfab5�豸״̬
					result.put("status", eqpInfoQuery(eqpid,delegator));
//				}

			} else if (Constants.EQP_STATUS_CHANGE.equals(serviceName)) {
				String eqpid = (String) map.get("eqpid");
				String newstatus = (String) map.get("newstatus");

                /* �޸�k��s���豸���ж�˳�� by moxiaoqi 20180712
				//1.�޸�fab5�豸״̬
				if (PlldbHelper.isFab5Eqp(delegator, eqpid)) {
					eqpStatusChange(userLogin, eqpid, newstatus);
				}
				*/

				//2.fab6��Ƭ������豸���޸�fab6�豸״̬
				//160524 dinghh: fab6�豸״̬Ϊ04MONPMʱ����ʼ����������������豸״̬
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
				//��ѯ�����û����û����б�
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
				//��ѯ�û���Ϣ
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
				// �޸�����
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
				throw new Exception("MES�ʺ�δ����������ϵ����Ա��");
			}
			throw new Exception("MES���벻�ԣ�");
		}
	}*/
	public static void login(GenericValue userLogin) throws Exception {
		String loginMsg = null;
		String message = null;
		try {
			loginMsg = CommonUtil.loginCheckForMES(userLogin.getString("userLoginId"), userLogin.getString("currentPassword"));
			JSONObject jsonObject = JSONObject.parseObject(loginMsg);
			if (StringUtils.isEmpty(loginMsg)){
				throw new Exception("MES�ʺ�δ����������ϵ����Ա��");
			}else if (StringUtils.isNotEmpty(jsonObject.getString("code"))){
				String code = jsonObject.getString("code");
					if ("5".equals(code)){
						throw new Exception("MES�ʺ�δ����������ϵ����Ա��");
					}else if ("1".equals(code)){
						throw new Exception(userLogin.getString("userLoginId")+"�������!");
					}
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	//��ѯ�豸״̬
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
					Debug.logInfo("��ѯ�豸:["+eqpId+" ] ״̬���� \t"+jsonObject,module);
					throw new RuntimeException("�豸"+eqpId +"�����ڻ�δ�����ָ����ʱ�������.");
				}
			}else {
				eqpStatus = jsonObject.getString("state");
			}
		} catch (RuntimeException e) {
			Debug.logError("��ѯ�豸:["+eqpId+" ] ״̬���� \t"+e.getMessage(),module);
			throw new RuntimeException(e.getMessage());
		}
		Debug.logInfo("��ѯ�豸: ["+eqpId+" ]״̬Ϊ: ["+eqpStatus+" ]",module);
		return eqpStatus;
	}

	//�޸��豸״̬
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
					Debug.logError("δ�ҵ�״̬: ["+eqpState+" ] \t"+jsonObject,module);
					throw new Exception("δ�ҵ�״̬"+eqpState+".");
				}
			}
/*			String errorMsg = "";
			if (eqpId.startsWith("6")) {
				errorMsg = "����ȷ�� PMϵͳ �� GUI ��¼�����Ƿ�һ�£������޸�Ϊ��ͬ�����롣";
			}
			if (e.getErrorCode().equals(com.csmc.asura.util.Constants.EQUIPMENT_NOT_FOUND_EXCEPTION)) { //ASURA_00008
				throw new Exception(errorMsg + "MESϵͳ��û��Ϊ���豸����SPC���ݣ��޷��޸��豸״̬!");
			} else if (e.getErrorCode().equals(com.csmc.asura.util.Constants.OPRATOR_NOT_PRIVILEGE_EXCEPTION)) { //ASURA_00009
				throw new Exception(errorMsg + "MESϵͳ���û�û���޸ĸø��豸��Ȩ�ޣ��޷��޸��豸״̬!");
			} else if(e.getErrorCode().equals(com.csmc.asura.util.Constants.EQUIPMENT_STATE_NOT_CHANGE_EXCEPTION)) {//ASURA_00010
				throw new Exception(errorMsg + "MESϵͳ�и��豸�����޸�Ϊ�µ�״̬���޷��޸��豸״̬��");
			}*/
			Debug.logInfo("�����豸: ["+eqpId+" ]״̬Ϊ: ["+eqpState+" ]�ɹ�",module);
		} catch (Exception e) {
			Debug.logError("�����豸: ["+eqpId+" ]״̬Ϊ: ["+eqpState+" ] ʧ��\t"+e.getMessage(),module);
			throw new Exception("�ı��豸״̬ʧ�ܣ�\r\n" + e.getMessage());
		}
	}
/*	public static void eqpStatusChange(GenericValue userLogin, String eqpId, String eqpState) throws Exception {
		try {
			Asura asura = new AsuraClient();
			asura.changeMesEqpStatus(eqpId, eqpState, userLogin.getString("userLoginId"));
		} catch (AsuraException e) {
			String errorMsg = "";
			if (eqpId.startsWith("6")) {
				errorMsg = "����ȷ�� PMϵͳ �� GUI ��¼�����Ƿ�һ�£������޸�Ϊ��ͬ�����롣";
			}

        	if (e.getErrorCode().equals(com.csmc.asura.util.Constants.EQUIPMENT_NOT_FOUND_EXCEPTION)) {
				throw new Exception(errorMsg + "MESϵͳ��û��Ϊ���豸����SPC���ݣ��޷��޸��豸״̬!");
			} else if (e.getErrorCode().equals(com.csmc.asura.util.Constants.OPRATOR_NOT_PRIVILEGE_EXCEPTION)) {
				throw new Exception(errorMsg + "MESϵͳ���û�û���޸ĸø��豸��Ȩ�ޣ��޷��޸��豸״̬!");
			} else if(e.getErrorCode().equals(com.csmc.asura.util.Constants.EQUIPMENT_STATE_NOT_CHANGE_EXCEPTION)) {
				throw new Exception(errorMsg + "MESϵͳ�и��豸�����޸�Ϊ�µ�״̬���޷��޸��豸״̬��");
			} else {
				throw new Exception(errorMsg + "�ı��豸״̬ʧ�ܣ�\r\n" + e.getMessage());
			}
        }
	}*/

	//fab5��ѯ���ݿ��е������û�
	private static List getAllUserNames(GenericDelegator delegator) throws SQLProcessException {
		String sql = "select account_no account_id from ACCOUNT t order by t.account_no";
        List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}

	//fab5��ѯ���ݿ��е������û���
	private static List getAllGroupNames(GenericDelegator delegator) throws SQLProcessException {
		String sql = "select t.user_group account_id from FW_GROUP_VIEW t";
        List list = SQLProcess.excuteSQLQuery(sql, delegator);
		return list;
	}

	//  �޸�����
/*	private static int updateUserInfo(GenericDelegator delegator,GenericValue userLogin,Map map) throws SQLProcessException {

		String sql = "update account set password = '"+(String) map.get("newpassword") +"' where account_NO = '"+(String) map.get("accountid")+"'";
		int row = SQLProcess.excuteSQLUpdate(sql, delegator);
		return row;
	}*/

}