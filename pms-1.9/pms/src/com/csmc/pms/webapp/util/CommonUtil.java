package com.csmc.pms.webapp.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import org.apache.xml.utils.res.XResources_zh_TW;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

public class CommonUtil {
	public static final String module = CommonUtil.class.getName();
	
	/**
	 * ����pms GenericDelegator
	 * @param request
	 * @return GenericDelegator
	 */
	public static GenericDelegator getPmsDelegator(HttpServletRequest request) {
		GenericDelegator delegator = null;
		try {
			// delegator = GenericDelegator.getGenericDelegator("pms");
			if (request != null) {
				delegator = (GenericDelegator) request.getAttribute("delegator");
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return delegator;
	}
	
	/**
	 * ����ofbiz default GenericDelegator
	 * @return GenericDelegator
	 */
	public static GenericDelegator getDefaultDelegator() {
		GenericDelegator delegator = null;
		try {			
			delegator = GenericDelegator.getGenericDelegator("default");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return delegator;
	}

	/**
	 * ����pms Dispatch
	 * @param delegator
	 * @return
	 */
	public static LocalDispatcher getPmsDispatch(GenericDelegator delegator) {
		LocalDispatcher dispatcher = null;
		try {
			dispatcher = GenericDispatcher.getLocalDispatcher("pms",delegator);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		
		return dispatcher;
	}
	
	/**
	 * ���ص�¼�߹���
	 * @param request
	 * @return
	 */
	public static String getUserNo(HttpServletRequest request) {
		 HttpSession session = request.getSession();
		 GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		 String userNo = userLogin.getString("userLoginId");
		 return userNo;
	}
	/**
	 * check is null
	 * @param object
	 * @return
	 */
	public static boolean isNull(Object object) {
		if(object == null) return true;
		return false;
	}
	
	/**
	 * check is not null
	 * @param object
	 * @return
	 */
	public static boolean isNotNull(Object object) {
		if(!isNull(object)) return true;
		return false;
	}
	
	/**
	 * check is empty
	 * String & List
	 * @param object
	 * @return
	 */
	public static boolean isEmpty(Object object) {
		if(isNull(object)) return true;
		if(object instanceof List) {
			List list = (List)object;
			if(list.size() == 0) return true;
		} else if(object instanceof String) {
			String string = (String)object;
			if(string.length() == 0) return true;
		}
		return false;
	}
	
	/**
	 * check is not empty
	 * String & List
	 * @param object
	 * @return
	 */
	public static boolean isNotEmpty(Object object) {
		if(!isEmpty(object)) return true;
		return false;
	}
	
	/**
	 * get one record
	 * @param delegator
	 * @param entityName
	 * @param fields
	 * @return GenericValue
	 */
	public static GenericValue findFirstRecordByAnd(GenericDelegator delegator, String entityName, Map fields) {				
		return findFirstRecordByAnd(delegator, entityName, fields, null);
	}
		
	/** Finds a Generic Entity record by all of the specified fields (ie: combined using AND)
	 * @param delegator
	 * @param entityName The Name of the Entity as defined in the entity XML file
     * @param fields The fields of the named entity to query by with their corresponging values
     * @param orderBy The fields of the named entity to order the query by;
     *      optionally add a " ASC" for ascending or " DESC" for descending
     *      e.g.:UtilMisc.toList("fromDate DESC")
	 * @return a GenericValue instance that match the query
	 */
	public static GenericValue findFirstRecordByAnd(GenericDelegator delegator, String entityName, Map fields, List orderBy) {
		
		try {
			List list = delegator.findByAnd(entityName, fields, orderBy);
			
			if (CommonUtil.isNotEmpty(list)) {
				return (GenericValue) list.get(0);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * CheckOracleException
	 * @param e
	 * @return
	 */
	public static String checkOracleException(Exception e) {
		String message = e.getMessage(); 
		for(int i = 0; i < Constants.ORA_EXCEPTION.length; i ++) {
			String exception = Constants.ORA_EXCEPTION[i];
			if(message.indexOf(exception) != -1) {
				return Constants.ORA_EXCEPTION_MESSAGE[i];
			}
		}
		return message;
	}
	
	/**
	 * ��������ת��
	 * @param date
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static String toGuiDate(java.util.Date date, String format) throws ParseException{
		SimpleDateFormat sdfDate = new SimpleDateFormat(format);
		return sdfDate.format(date);
	}
	
	/**
	 * check double
	 * @param a
	 * @return
	 */
	public static String checkDoubleNull(Double a) {
		if(a == null) return "";
		return a.toString();
	}
	
	/**
	 * check double
	 * @param a
	 * @return
	 */
	public static String checkDoubleNull(Double a, String s) {
		if(a == null) return s;
		return a.toString();
	}
	
	/**
	 * С������
	 *//*
	public static double round(double v1, int scale) {
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(1);
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	*//**
	 * ���ʵ�ʹ�ʱ
	 * @param diff
	 * @return
	 *//*
	public static double getManHour(long diff) {
		BigDecimal b1 = new BigDecimal(diff);
		b1 = b1.divide(new BigDecimal(1000));
		b1 = b1.divide(new BigDecimal(60), 4, BigDecimal.ROUND_HALF_UP);
		b1 = b1.divide(new BigDecimal(60), 4, BigDecimal.ROUND_HALF_UP);
		b1 = b1.divide(new BigDecimal(24), 2, BigDecimal.ROUND_HALF_UP);
		return b1.doubleValue();
	}*/
	
	/**
	 * ����mcs GenericDelegator
	 * @param request
	 * @return GenericDelegator
	 */
	public static GenericDelegator getMcsDelegator(HttpServletRequest request) {
		return getPmsDelegator(request);		 
	}
	
	/**
	 * ����mcs Dispatch
	 * @param delegator
	 * @return
	 */
	public static LocalDispatcher getMcsDispatch(GenericDelegator delegator) {		
		return getPmsDispatch(delegator);
	}
	
	/**
	 * ����ofbiz gui GenericDelegator
	 * @return GenericDelegator
	 */
	public static GenericDelegator getGuiDelegator() {
		GenericDelegator delegator = null;
		try {
			delegator = GenericDelegator.getGenericDelegator("gui");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return delegator;
	}
	
	/**
     * ����gui Dispatch
     * @param delegator
     * @return
     */
    public static LocalDispatcher getGuiDispatch(GenericDelegator delegator) {
        LocalDispatcher dispatcher = null;
        try {
            dispatcher = GenericDispatcher.getLocalDispatcher("gui",delegator);
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        
        return dispatcher;
    }
    
    /**
     * ����sequence name����sequence id�ĸ�ֵ
     * @param seqName
     * @return
     * Create on 2011-7-26
     * Update on 2011-7-26
     */
    public static Long getGuiMinusSeq(String seqName){
        return new Long (-CommonUtil.getDefaultDelegator().getNextSeqId(seqName).longValue());
    }

	/**
	 * @param sendFrom
	 * @param sendTo
	 * @param sendCc
	 * @param subject
	 * @param body
	 * Create & Update on 2011-10-31
	 * @author qinchao
	 */
	public static void sendMail(String sendFrom, String sendTo, String sendCc, String subject, String body) {
	    String sendBcc = null;
	    String priority = null;
	    String contentType = null;
		
		try {
	        if (body == null) {
	        	body = "";
	        }
	        body = body + "\n";
	        MiscUtils.sendMail(sendTo, sendCc, sendBcc, subject, sendFrom, priority, body, contentType);
	    } catch (Exception e) {
	        //Debug.logError(e, "Cannot send mail message", module);
	    	Debug.logError(e.getMessage(), module);
	    	e.printStackTrace();
	    }
	}

	/**
	 * ��pmsʱ���ַ���ת��ΪTimestamp
	 * @param date
	 * @return Timestamp
	 * Create & Update on 2012-9-17
	 * @author dinghh
	 */
	public static Timestamp toPmsTimestamp(String dateStr) throws ParseException{
		String format = "yyyy-MM-dd HH:mm:ss";
		return MiscUtils.toGuiDateTime(dateStr, format);
	}

	//Fab1 Promis       isCheckLogin=Y ���б���У��
	//Fab5 FactoryWorks isCheckLogin=N ���б���У��
	public static boolean isLocalUser(GenericValue accountGv) {
		if (accountGv != null) {
			String isCheckLogin = accountGv.getString("isCheckLogin");
			return Constants.CALL_TP_FLAG && "Y".equals(isCheckLogin) || Constants.CALL_ASURA_FLAG && "N".equals(isCheckLogin);
		}
		
		return false;
	}

	// Sha256����
	public static String Sha256(String password){

		StringBuilder hexString = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(password.getBytes());

			hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1){
					hexString.append('0');
				}
				hexString.append(hex);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

		return hexString.toString();
	}

	//
	public static String post(String path,String jsonStr) {
		StringBuilder sb = new StringBuilder();
		try {
			URL url =  new URL(path);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");// �ύģʽ
			httpURLConnection.setConnectTimeout(30000);//���ӳ�ʱ ��λ����
			httpURLConnection.setReadTimeout(30000);//��ȡ��ʱ ��λ����
			// ����POST�������������������
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Content-Type", "application/json");

			httpURLConnection.connect();
			OutputStream os=httpURLConnection.getOutputStream();
			os.write(jsonStr.getBytes("UTF-8"));
			os.flush();

			BufferedReader br = null;
			int httpRspCode = httpURLConnection.getResponseCode();
			if (httpRspCode == HttpURLConnection.HTTP_OK) { // ��������
				// ��ʼ��ȡ����
				br = new BufferedReader(
						new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				return sb.toString();

			}else { // ʧ��
				// ��ʼ��ȡ����
				br = new BufferedReader(
						new InputStreamReader(httpURLConnection.getErrorStream(), "UTF-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				return sb.toString();
			}

		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			e.printStackTrace();
		}
		return null;
	}

	//	��¼
	public static String loginCheckForMES(String username,String password){
		String jsonStr = "{\"class\": \"FwIDELoginTxn\",\"userName\": \""+username+"\",\"password\": \""+password+"\",\"action\": \"LOGIN\",\"clientType\": \"IDE\"}";
		String postResult = post("http://10.163.76.8:8080/http-gateway/json/mbx/FwIDELoginTxn", jsonStr);
		return postResult;
	}

	//	����豸״̬
	public static String queryMesEqpStatus(String eqpId,GenericDelegator delegator) throws SQLProcessException {
		String jsonStr = "{\"class\": \"FwCheckStatusTxn\",\"name\": \""+eqpId+"\"}";
		String checkHttpSql = "SELECT  HTTP_POST FROM MES_HTTP WHERE  ACTION  = 'check'";
		List checkHttpList = SQLProcess.excuteSQLQuery(checkHttpSql, delegator);
		HashMap checkHttpMap = (HashMap) checkHttpList.get(0);
		String checkHttp = (String) checkHttpMap.get("HTTP_POST");
		Debug.logInfo("���ò�ѯ�豸״̬�ӿ�["+checkHttp+"],�豸["+eqpId+"] ",module);
		String postResult = post(checkHttp, jsonStr);
		Debug.logInfo("��ѯ�豸״̬�ӿڵ��ý���,����ֵΪ: "+postResult,module);
		return postResult;
	}

	//�����豸״̬
	public static String changeMesEqpStatus(String eqpId,String state, String owner,GenericDelegator delegator) throws SQLProcessException {
		String jsonStr = "{\"class\": \"FwChangeStateTxn\",\"name\": \""+eqpId+"\",\"state\": \""+state+"\",\"userName\":\"PMSSYS\" }";
		String changeHttpSql = "SELECT  HTTP_POST  FROM MES_HTTP WHERE  ACTION  = 'change'";
		List changeHttpList = SQLProcess.excuteSQLQuery(changeHttpSql, delegator);
		Map changeHttpMap = (Map) changeHttpList.get(0);
		String changeHttp = (String) changeHttpMap.get("HTTP_POST");
		Debug.logInfo("���ø����豸״̬�ӿ�["+changeHttp+"],�����豸["+eqpId+"] ״̬Ϊ[ "+state+" ]",module);
		String postResult = post(changeHttp, jsonStr);
		Debug.logInfo("�����豸״̬�ӿڵ��ý���,����ֵΪ: "+postResult,module);
		return postResult;
	}
}
