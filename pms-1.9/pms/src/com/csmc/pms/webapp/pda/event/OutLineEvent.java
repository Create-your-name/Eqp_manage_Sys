package com.csmc.pms.webapp.pda.event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.util.GeneralEvents;
import com.csmc.pms.webapp.pda.help.OutLineHelp;
import com.csmc.pms.webapp.pda.model.FormLib;
import com.csmc.pms.webapp.qufollow.help.QuFollowHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.workflow.model.Job;
import com.csmc.pms.webapp.workflow.model.engine.JobSupport;

public class OutLineEvent extends GeneralEvents {
	public static final String module = OutLineEvent.class.getName();
	public static final String FILE_DIR="/pms/fileUpload/PDA/";
	
	/**
	 * �������ļ�����ϳ�XML��ʽ�����������ļ�д��/pms/fileUpload/PDA/Ŀ¼��
	 * �ɴ���ļ����ƹ���relationIndex_jobIndex_formName(10060_10000_ER2007-ENSINVI-10060.xml)
	 * ���ر��������е��ѿ�ʼJOBδ��ʼ�ı�
	 * @param request
	 * @param response
	 */
	public static void getIndex(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		String pcStyle=request.getParameter("pcStyle");
		String accountNo=request.getParameter("accountNo");
		
		try{
			List useInfoList=QuFollowHelper.getAccountSection(delegator, accountNo);
        	GenericValue gv=(GenericValue)useInfoList.get(0);
        	//��½�˵ĿƱ�
        	String section=gv.getString("accountSection");
			JobSupport jobSupport=JobSupport.getInstance();
			
			//TS
			StringBuffer sb=new StringBuffer();
			sb.append("select  t.ABNORMAL_INDEX FORM_INDEX,t.ABNORMAL_NAME FORM_NAME,t.EQUIPMENT_ID,t.CREATE_TIME,t.CREATE_USER from abnormal_form t,account t2 ");
			sb.append("where t.create_user=t2.account_no and t2.account_section='").append(section).append("'");
			sb.append("and t.status='0'");
			List abnormalList = SQLProcess.excuteSQLQuery(sb.toString(), delegator);
			String eventType=Constants.TS;
			List formLibList=new ArrayList();
			FormLib formLib=OutLineHelp.getFormLib(request, delegator, jobSupport, abnormalList, eventType,FILE_DIR);
			formLibList.add(formLib);
			
			//PM
			sb=new StringBuffer();
			eventType=Constants.PM;
			sb.append("select t.PM_INDEX FORM_INDEX,t.PM_NAME FORM_NAME,t.EQUIPMENT_ID EQUIPMENT_ID,t.CREATE_TIME,t.CREATE_USER from pm_form t,account t2 ");
			sb.append("where t.create_user=t2.account_no and t2.account_section='").append(section).append("'");
			sb.append("and t.status='0'");
			List pmFormList = SQLProcess.excuteSQLQuery(sb.toString(), delegator);
			formLib=OutLineHelp.getFormLib(request, delegator, jobSupport, pmFormList, eventType,FILE_DIR);
			formLibList.add(formLib);
			
			//PCע��	PC���޿�ʼ״̬��ֻ�д��������;-1:����
			if(StringUtils.isNotEmpty(pcStyle)){
				//ѡ����Ѳ����ʽ����Ҫ����Ѳ���
				sb=new StringBuffer();
				eventType=Constants.PC;
				sb.append("select t.PC_INDEX FORM_INDEX,t.PC_NAME FORM_NAME,t2.name EQUIPMENT_ID,t.CREATE_TIME,t.CREATE_USER from pc_form t,pc_style t2,account t3 ");
				sb.append("where t.style_index=t2.style_index and t.style_index='").append(pcStyle).append("'");
				sb.append(" and  t.create_user=t3.account_no and t3.account_section='").append(section).append("'");
				sb.append("and t.status='-1'");
				List pcFormList = SQLProcess.excuteSQLQuery(sb.toString(), delegator);
				formLib=OutLineHelp.getFormLib(request, delegator, jobSupport, pcFormList, eventType,FILE_DIR);
				formLibList.add(formLib);
			}
			
			//����index.xml�ļ�
			Document doc=OutLineHelp.createIndexXML(formLibList);
			response.setContentType("text/xml; charset=GBK");
			// ��ʽ��XML�����ʽ
			OutputFormat format = OutputFormat.createPrettyPrint(); //����XML�ĵ������ʽ
			format.setEncoding("GB2312"); //����XML�ĵ��ı�������
			format.setIndent(true); //�����Ƿ�����
			format.setIndent("   "); //�Կո�ʽʵ������
			format.setNewlines(true); //�����Ƿ���
			ServletOutputStream op = response.getOutputStream();
			XMLWriter xmlWriter = new XMLWriter(op, format);
			xmlWriter.write(doc);
			xmlWriter.close();
			op.flush();
			op.close();
		}catch (Exception e) {
			Debug.logError(e.getMessage(), module);
		}
	}
	
	/**
	 * �������δ��ʼ�ı���������Ϣ����ΪXML�ļ������̱���ΪHTML�ļ�
	 * @param request
	 * @param response
	 */
	public static void formDownload(HttpServletRequest request, HttpServletResponse response) {
		String fileName=request.getParameter("fileName");
		String fileDir=FILE_DIR+fileName;
		  try {
			String filePath = request.getSession().getServletContext().getRealPath(fileDir);
			File f = new File(filePath);
			if (f.exists()) {
				FileInputStream in = new FileInputStream(f);
				response.setContentType("text/xml; charset=GB2312");
				response.setContentLength((int) f.length());
				// fetch the file
				int length = (int) f.length();
				System.out.println(length);
				if (length != 0) {
					byte[] buf = new byte[4096];
					ServletOutputStream op = response.getOutputStream();
					while ((in != null) && ((length = in.read(buf)) != -1)) {
						op.write(buf, 0, length);
					}
					in.close();
					op.flush();
					op.close();
				}
				//ɾ���ɹ����غ���ļ�
				f.delete();
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}
	
	/**
	 * �ļ��ϴ� 1. check file exists in uploadDIR 2. upload file one by one 3.
	 * ��״̬Ϊ���ʱ�����ܸ���
	 * 
	 * @param request
	 * @param response
	 */
	public static void pdaUpLoad(HttpServletRequest request,HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		
		try {
			BufferedReader bufXML = request.getReader();
			Job job = OutLineHelp.getJobInfo(bufXML);
			//JOB�������
			LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
			Map jobMap=new HashMap();
			jobMap.put("job", job);
			Map result = dispatcher.runSync("outLineupload",UtilMisc.toMap("jobMap" ,jobMap));
			ServletOutputStream op = response.getOutputStream();
			//��ͻ��˽���-1:error;1:�˱�״̬���;0���ݱ���ɹ�
			if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
				op.write("-1".getBytes());
			}else{
				String msg=(String)result.get("returnMsg");
				op.write(msg.getBytes());
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
		}
	}
}
