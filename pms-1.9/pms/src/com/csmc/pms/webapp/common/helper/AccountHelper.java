package com.csmc.pms.webapp.common.helper;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;

public class AccountHelper {
	public static final String module = AccountHelper.class.getName();

	/**
	  * ��õ�½�˵��û���Ϣ
	  * @param request
	  * @param delegator
	  * @return Account GenericValue
	  */
	public static GenericValue getUserInfo(HttpServletRequest request, GenericDelegator delegator) {
		String userNo = CommonUtil.getUserNo(request);
		
		try {
			GenericValue user = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", userNo));
			return user;
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
		}
		return null;
	}
	 
	/**
	  * �����Ų�ѯaccount��Ϣ
	  * @param delegator
	  * @param accountNo
	  * @return Account GenericValue
	  */
	public static GenericValue getAccountByNo(GenericDelegator delegator, String accountNo) {
		try {
			GenericValue accountGv = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", accountNo));
			return accountGv;
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
		}
		
		return null;
	}
	
	/**
	  * �����Ų�ѯmail
	  * @param delegator
	  * @param accountNo
	  * @return mailAddress
	  */
	public static String getMailByAccountNo(GenericDelegator delegator, String accountNo) {
		GenericValue accountGv = getAccountByNo(delegator, accountNo);
		if (accountGv != null) {
			return accountGv.getString("mailAddress") + ";";			
		} else {
			return Constants.ADMIN_MAIL + ";";
		}
	}

	
	/**
	  * �õ���½��Ȩ��check
	  * @param request
	  * @param delegator
	  * @return
	  */
		
		public static String  getPrivFlag(HttpServletRequest request, GenericDelegator delegator) {
			String userNo = CommonUtil.getUserNo(request);
		try {
			String sql = "select *   from account t where (t.account_pos like '%����%' or t.account_pos like '%����%' or  t.account_pos like '%�γ�%' or t.account_pos like '%�ܼ�%')  "
				 	   + "  and t.account_no ='" + userNo + "'";
			List list = SQLProcess.excuteSQLQuery(sql, delegator);
			Map map = (Map) list.get(0);
			return (String) map.get("ACCOUNT_NO");
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
		}
		return null;
	}

		/**
		  * �õ���½��Ȩ��check
		  * @param request
		  * @param delegator
		  * @return
		  */
			
			public static String  getPricFlag(HttpServletRequest request, GenericDelegator delegator) {
				String userNo = CommonUtil.getUserNo(request);
			try {
				String sql = "select *   from account t where (t.account_pos like '%����%' or t.account_pos like '%����%' or  t.account_pos like '%�γ�%' )  "
					 	   + "  and t.account_no ='" + userNo + "'";
				List list = SQLProcess.excuteSQLQuery(sql, delegator);
				Map map = (Map) list.get(0);
				return (String) map.get("ACCOUNT_NO");
			} catch (Exception e) {
				Debug.logError(e.getMessage(), module);
			}
			return null;
		}
	
	
	
	
	/**
	  * �õ���½�˵Ĳ���
	  * @param request
	  * @param delegator
	  * @return
	  */
	public static String getUserDeptIndex(GenericDelegator delegator,String userNo) {
		try {
			String sql = "select t.dept_index from equipment_dept t, account t2 "
				 	   + " where t.equipment_dept = t2.account_dept and t2.account_no='" + userNo + "'";
			List list = SQLProcess.excuteSQLQuery(sql, delegator);
			Map map = (Map) list.get(0);
			return (String) map.get("DEPT_INDEX");
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
		}
		return null;
	}

	/**
	 * ���ݹ��Ų��ҿγ�
	 * @param delegator
	 * @param user
	 * @return
	 */
	public static String querySectionLeaderByUser(GenericDelegator delegator, String accountNo) {
		try {
			GenericValue userInfo = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", accountNo));
			String section = userInfo.getString("accountSection");
		    return querySectionLeaderBySection(delegator, section);
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
		}
		
	 	return Constants.SUPER_USER;
	}

	/**
	 * ���ݿα�section���ҿγ�
	 * @param delegator
	 * @param section
	 * @return �γ�����
	 * @throws GenericEntityException
	 */
	public static String querySectionLeaderBySection(GenericDelegator delegator, String section) {
		try {
			List sectionList = delegator.findByAnd("EquipmentSection", UtilMisc.toMap("section", section));
			if(CommonUtil.isNotEmpty(sectionList)) {
				GenericValue gv =  (GenericValue)sectionList.get(0);
			 	if(CommonUtil.isNotEmpty(gv.getString("sectionLeader"))) {
			 		return gv.getString("sectionLeader");
			 	}
		 	}
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
		}
		
	 	return Constants.SUPER_USER;
	}
	
	/**
	 * ���ݲ���deptIndex���Ҿ���
	 * @param delegator
	 * @param deptIndex
	 * @return ������
	 * @throws GenericEntityException
	 */
	public static String queryDeptLeaderByDeptIndex(GenericDelegator delegator, String deptIndex) {
		try {
			GenericValue gv = delegator.findByPrimaryKey("EquipmentDept", UtilMisc.toMap("deptIndex", deptIndex));
			if (gv != null) {
				return gv.getString("deptLeader");
			}
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
		}
		
		return Constants.SUPER_USER;
	}
	
	/**
	  * �����Ų�ѯ�γ�mail
	  * @param delegator
	  * @param accountNo
	  * @return �γ�mail
	 * @throws GenericEntityException 
	  */
	public static String getMailOfSectionLeaderByUserNo(GenericDelegator delegator, String accountNo) {
		String sectionLeader = querySectionLeaderByUser(delegator, accountNo);
		return getMailByAccountNo(delegator, sectionLeader);
	}
	
	/**
	  * ��section��ѯ�γ�mail
	  * @param delegator
	  * @param section
	  * @return �γ�mail
	 * @throws GenericEntityException 
	  */
	public static String getMailOfSectionLeaderBySection(GenericDelegator delegator, String section) {
		String sectionLeader = querySectionLeaderBySection(delegator, section);
		return getMailByAccountNo(delegator, sectionLeader);
	}
	
	/**
	  * ��deptIndex��ѯ���ž���mail
	  * @param delegator
	  * @param deptIndex
	  * @return ����mail
	 * @throws GenericEntityException 
	  */
	public static String getMailOfDeptLeaderByDeptIndex(GenericDelegator delegator, String deptIndex) {
		String deptLeader = queryDeptLeaderByDeptIndex(delegator, deptIndex);
		return getMailByAccountNo(delegator, deptLeader);
	}
	
	/**
	 * get mail addresses of �����˻��¼�û����γ�������
	 * @param request
	 * @param delegator
	 * @param transBy
	 * @return String
	 */
	public static String getMailOfUserAndLeaders(
			HttpServletRequest request, GenericDelegator delegator,	String transBy) {
		GenericValue accountGv = getAccountByNo(delegator, transBy);
		if (accountGv == null) {
			accountGv = getAccountByNo(delegator, CommonUtil.getUserNo(request));
		}
			
		String mailAddresses = null;	                    
		mailAddresses = accountGv.getString("mailAddress") + ";"
			+ getMailOfSectionLeaderBySection(delegator, accountGv.getString("accountSection"))
			+ getMailOfDeptLeaderByDeptIndex(delegator, accountGv.getString("deptIndex"));
		return mailAddresses;
	}

	/**
	 * ���ص�¼���Ƿ�Ϊ�������Ʋ�
	 * @param request
	 * @return "true"/"false"
	 */
	public static boolean isMsaDept(HttpServletRequest request,GenericDelegator delegator) {
	    boolean result = false;
	    String userNo = CommonUtil.getUserNo(request);
	    GenericValue account;
	    try {
	        account = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", userNo));
	        String dept = account.getString("deptIndex");
	        //�����¼��Ϊ������֤��(dept_index == 10010)������������������MSA��ͷ
	        if(dept!=null && dept.equals(Constants.DEPT_QC_INDEX)){
	            result = true;
	        }
	    } catch (GenericEntityException e) {
	    	Debug.logError(e.getMessage(), module);
	    }
	    return result;
	}

	/**
	 * @param deptName
	 * @return
	 */
	public static boolean isDeptQC(String deptName) {
		return deptName.endsWith(Constants.DEPT_QC);
	}
}
