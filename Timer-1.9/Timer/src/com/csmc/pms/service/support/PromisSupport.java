package com.csmc.pms.service.support;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.csmc.asura.exception.AsuraException;
import com.csmc.db.DataSourceFactory;
import com.csmc.db.SQLProcess;
import com.csmc.util.Log;

public class PromisSupport {

	public static final String module = PromisSupport.class.getName();
	/**
	 * ��ѯ�豸״̬
	 * @param eqpId
	 * @return
	 * @throws TPServiceException
	 */
/*	protected String queryEqpStatus(String eqpId) throws AsuraException {
		Asura asura = new AsuraClient();
		String status = asura.queryEqpStatus(eqpId);
		return status;
    }
	*/
	protected String queryEqpStatus(String eqpId) throws SQLException {
		String eqpStatus = null;
		String jsonStr = "{\"class\": \"FwCheckStatusTxn\",\"name\": \""+eqpId+"\"}";
		String checkHttpSql = "SELECT  HTTP_POST FROM MES_HTTP WHERE  ACTION  = 'check'";
		List list = SQLProcess.excuteSQLQuery(checkHttpSql, DataSourceFactory.PMS_DATASOURCE);
		Map checkHttpMap = (Map) list.get(0);
		String checkHttp = (String) checkHttpMap.get("HTTP_POST");
		Log.logInfo("call http check start["+checkHttp+"],eqpId["+eqpId+"] ",module);
		String postResult = post(checkHttp, jsonStr);
		Log.logInfo("call http check end : "+postResult,module);
		JSONObject jsonObject = JSONObject.parseObject(postResult);
		if (isNotEmpty(jsonObject.getString("code"))){
			String code = jsonObject.getString("code");
			if ("2".equals(code)){
				Log.logInfo("eqpId[ "+eqpId+" ],Check status failed ",module);
				throw new RuntimeException("�豸"+eqpId +"�����ڻ�δ�����ָ����ʱ�������.");
			}
		}else {
			eqpStatus = jsonObject.getString("state");
		}
		Log.logInfo("eqpId[ "+eqpId+" ],status[ "+eqpStatus+" ]",module);
		return eqpStatus;
	}

	/**
	 * ��ѯ�豸��Ϣ
	 * @param eqpId
	 * @return
	 * @throws TPServiceException
	 */
	protected Map queryEqpInfo(String eqpId) throws AsuraException {
		return null;
	}
    
    /**
     * �޸��豸״̬
     * @param eqpId
     * @param newStatus
     * @throws TPServiceException 
     */
/*    protected void changeEqpStatus(String eqpId, String newStatus) throws AsuraException {
    	Asura asura = new AsuraClient();
    	asura.changeEqpStatus(eqpId, newStatus, "IT_SUPER");
    }*/
	protected void changeEqpStatus(String eqpId, String newStatus) throws SQLException {
		String jsonStr = "{\"class\": \"FwChangeStateTxn\",\"name\": \""+eqpId+"\",\"state\": \""+newStatus+"\",\"userName\":\"PMSSYS\" }";
		String changeHttpSql = "SELECT  HTTP_POST  FROM MES_HTTP WHERE  ACTION  = 'change'";
		List list = SQLProcess.excuteSQLQuery(changeHttpSql, DataSourceFactory.PMS_DATASOURCE);
		Map changeHttpMap = (Map) list.get(0);
		String changeHttp = (String) changeHttpMap.get("HTTP_POST");
		Log.logInfo("call http change start["+changeHttp+"],change eqpId ["+eqpId+"] newStatus[ "+newStatus+" ]",module);
		String postResult = post(changeHttp, jsonStr);
		Log.logInfo("call http change end : "+postResult,module);
		JSONObject jsonObject = JSONObject.parseObject(postResult);
		if(jsonObject == null){
			Log.logInfo("eqpId[ "+eqpId+" ]change status success,status[ "+newStatus+" ]",module);
		}else{
			Log.logError("eqpId[ "+eqpId+" ]change status failed,status[ "+newStatus+" ]", module);
		}

	}
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
			Log.logError("post������ʧ��[" + e.getMessage() + "]", module);
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isNotEmpty(String str) {
		return str != null && str.length() > 0;
	}

}
