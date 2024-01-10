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
	 * 将所有文件名组合成XML格式，并将所有文件写入/pms/fileUpload/PDA/目录中
	 * 可存的文件名称规则：relationIndex_jobIndex_formName(10060_10000_ER2007-ENSINVI-10060.xml)
	 * 下载本部门所有的已开始JOB未开始的表单
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
        	//登陆人的科别
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
			
			//PC注：	PC中无开始状态，只有创建和完成;-1:创建
			if(StringUtils.isNotEmpty(pcStyle)){
				//选择了巡检样式后需要下载巡检表单
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
			
			//构构index.xml文件
			Document doc=OutLineHelp.createIndexXML(formLibList);
			response.setContentType("text/xml; charset=GBK");
			// 格式化XML输出格式
			OutputFormat format = OutputFormat.createPrettyPrint(); //设置XML文档输出格式
			format.setEncoding("GB2312"); //设置XML文档的编码类型
			format.setIndent(true); //设置是否缩进
			format.setIndent("   "); //以空格方式实现缩进
			format.setNewlines(true); //设置是否换行
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
	 * 获得所有未开始的表单，将其信息保存为XML文件，流程保存为HTML文件
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
				//删除成功下载后的文件
				f.delete();
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
		}
	}
	
	/**
	 * 文件上传 1. check file exists in uploadDIR 2. upload file one by one 3.
	 * 表单状态为完成时，不能更新
	 * 
	 * @param request
	 * @param response
	 */
	public static void pdaUpLoad(HttpServletRequest request,HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		
		try {
			BufferedReader bufXML = request.getReader();
			Job job = OutLineHelp.getJobInfo(bufXML);
			//JOB构造完成
			LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
			Map jobMap=new HashMap();
			jobMap.put("job", job);
			Map result = dispatcher.runSync("outLineupload",UtilMisc.toMap("jobMap" ,jobMap));
			ServletOutputStream op = response.getOutputStream();
			//与客户端交互-1:error;1:此表单状态完成;0数据保存成功
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
