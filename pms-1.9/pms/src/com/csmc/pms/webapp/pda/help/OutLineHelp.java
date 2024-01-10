package com.csmc.pms.webapp.pda.help;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.htmlparser.util.ParserException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.csmc.pms.webapp.pda.model.EquipmentGroup;
import com.csmc.pms.webapp.pda.model.FormItem;
import com.csmc.pms.webapp.pda.model.FormLib;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.workflow.exception.FlowException;
import com.csmc.pms.webapp.workflow.help.WorkflowHelper;
import com.csmc.pms.webapp.workflow.model.Action;
import com.csmc.pms.webapp.workflow.model.ActionItem;
import com.csmc.pms.webapp.workflow.model.ActionStatus;
import com.csmc.pms.webapp.workflow.model.Job;
import com.csmc.pms.webapp.workflow.model.engine.JobEngine;
import com.csmc.pms.webapp.workflow.model.engine.JobSupport;


public class OutLineHelp {
	public static final String module = OutLineHelp.class.getName();
	
	/**
	 * 生成JOB的XML文件
	 * @param request
	 * @param delegator
	 * @param jobSupport jobSupport类
	 * @param fromList 表单列表
	 * @param eventType 表单类别
	 * @throws GenericEntityException
	 * @throws FlowException
	 * @throws ParserException 
	 * @throws IOException 
	 */
	public  static FormLib getFormLib(HttpServletRequest request, GenericDelegator delegator, JobSupport jobSupport, List fromList, String eventType,String fileDir) throws GenericEntityException, FlowException, ParserException, IOException {
		String fileName;
		FormLib formLib=new FormLib();
		formLib.setSheetTyp(eventType);
		GenericDelegator guiDelegator = (GenericDelegator)request.getAttribute("delegator");
		for (int i = 0; i < fromList.size(); i++) {
			Map map = (Map) fromList.get(i);
			List errorReasonList=new ArrayList();
			if(Constants.TS.equalsIgnoreCase(eventType)){
				GenericValue equipment=delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId",(String)map.get("EQUIPMENT_ID")));
				errorReasonList = delegator.findByAnd("PmsReason",UtilMisc.toMap("reasonType",Constants.ABNORMAL, "equipmentType",(String) equipment.getString("equipmentType")));
			}
			List formJobRealtion = delegator.findByAnd("FormJobRelation",
					UtilMisc.toMap("jobStatus",String.valueOf(Constants.START), "eventType",
							eventType, "eventIndex", (String) map.get("FORM_INDEX")));
			int size=formLib.getEquipmentGroupList().size();
			EquipmentGroup operEquipmentGroup=null;
			boolean isExists=false;
			if(size>0){
				for(int k=0;k<size;k++){
					EquipmentGroup equipmentGroup=(EquipmentGroup)formLib.getEquipmentGroupList().get(k);
					//如果此equipmentID已存在，则需要将fileItem存在此相应的列表中
					if(equipmentGroup.getEquipment().equals((String)map.get("EQUIPMENT_ID"))){
						operEquipmentGroup=equipmentGroup;
						isExists=true;
						break;
					}
				}
			}
			if(!isExists){
				operEquipmentGroup=new EquipmentGroup();
				if(formJobRealtion.size()>0){
					operEquipmentGroup.setEquipment((String)map.get("EQUIPMENT_ID"));
					formLib.getEquipmentGroupList().add(operEquipmentGroup);
				}
			}
			//TS一个表单对应一个JOB
			for (int j = 0; j < formJobRealtion.size(); j++) {
				FormItem formItem=new FormItem();
				GenericValue jobRelationEntity = (GenericValue) formJobRealtion.get(j);
				// Job job = jobSupport.parseJob(jobRelationEntity,false,delegator);
				
				GenericValue userLogin = 
					guiDelegator.makeValue("UserLogin", UtilMisc.toMap("userLoginId", "PMS", "currentPassword", "PMSTEST", "enabled", "Y"));
				
				JobEngine jobEngine = JobEngine.create();
				jobEngine.setDelegator(delegator);
				jobEngine.setUserLoginId(userLogin);
				Job job = jobEngine.getJobToOffline(jobRelationEntity);
				
				job.setEventType(eventType);
				job.setRecordName((String)map.get("FORM_NAME"));
				job.setCreateTime(String.valueOf(map.get("CREATE_TIME")));
				job.setCreator(String.valueOf(map.get("CREATE_USER")));
				if(Constants.TS.equalsIgnoreCase(eventType)){
					job.setErrorReasonList(errorReasonList);
				}
				job.setEquipment((String)map.get("EQUIPMENT_ID"));
				
				fileName=String.valueOf(jobRelationEntity.get("seqIndex"))+"_"+
								String.valueOf(jobRelationEntity.get("jobIndex"))+"_"+
								(String)map.get("FORM_NAME");
				//write html				
				String flowHTML=WorkflowHelper.getOfflineFlowHtml(String.valueOf(jobRelationEntity.get("seqIndex")));
				String flowName=fileName+"_flow.html";
				String flowpath=fileDir+flowName;
				String htmlPath = request.getSession().getServletContext().getRealPath(flowpath);
				File htmlFile=new File(htmlPath); 
				if(!htmlFile.exists()){
					htmlFile.createNewFile();
				}
				job.setFlowName(flowName);
				
				FileWriter resultFile=new FileWriter(htmlFile); 
				PrintWriter myFile=new PrintWriter(resultFile);				
				myFile.println(flowHTML);
				resultFile.close(); 
				//write xml
				String file=fileDir+fileName+".xml";
				String xmlPath = request.getSession().getServletContext().getRealPath(file);
				boolean isSuccess=createTempForm(job, xmlPath);
				//文件成功保存至相应服务器
				if(isSuccess){
					formItem.setFileName(fileName+".xml");
					formItem.setFormName((String)map.get("FORM_NAME"));
					formItem.setFlowName(flowName);
					operEquipmentGroup.getFormItemList().add(formItem);
				}
			}
		}
		return formLib;
	}
	
	/**
	 * @param bufXML
	 * @return
	 * @throws DocumentException
	 * @throws NumberFormatException
	 */
	public static Job getJobInfo(BufferedReader bufXML) throws DocumentException, NumberFormatException {
		Job job=new Job();
		SAXReader reader = new SAXReader();
		Document document = reader.read(bufXML);
		Element root = document.getRootElement();
		Element sheetInfo=root.element("sheet_info");
		String event=(sheetInfo.element("event")).getTextTrim();
		job.setEventType(event);
		job.setJobIndex(Long.parseLong((sheetInfo.element("job_index")).getTextTrim()));
		job.setJobName((sheetInfo.element("job_name")).getTextTrim());
		String recordName=(sheetInfo.element("record_name")).getTextTrim();
		String[] formNameInfo=recordName.split("-");
		job.setRecordName(recordName);
		job.setFormIndex(formNameInfo[2]);
		job.setOperator((sheetInfo.element("operator")).getTextTrim());
		job.setFinishData((sheetInfo.element("finish_data")).getTextTrim());
		job.setErrorCode((sheetInfo.element("error_code")).getTextTrim());
		job.setErrorEvent((sheetInfo.element("error_event")).getTextTrim());
		job.setErrorReason((sheetInfo.element("error_reason")).getTextTrim());
		job.setEquipment((sheetInfo.element("equipment_no")).getTextTrim());
		
		Element actionsElement=root.element("actions");
		List actionList=new ArrayList();
		//action
		for ( Iterator i = actionsElement.elementIterator("action"); i.hasNext(); ) {
			Element actionElement = (Element) i.next();
			Action action=new Action();
			String actionId=actionElement.attributeValue("action_id");
			action.setActionId(Integer.parseInt(actionId));
			action.setActionName(actionElement.attributeValue("action_name"));
			System.out.println(actionElement.attributeValue("action_name"));
			action.setActionDescription(actionElement.attributeValue("action_desc"));
			if(actionElement.attributeValue("action_index")!=null&&!"".equals(actionElement.attributeValue("action_index"))){
				action.setActionIndex(Long.parseLong(actionElement.attributeValue("action_index")));
			}
			String finishData=actionElement.attributeValue("finish_date");
			String actionValue=actionElement.attributeValue("action_value");
			action.setFinishData(finishData);
			action.setActionNote(actionValue);
			//actionItem
			List itemList=new ArrayList();
			Element itemsElement=actionElement.element("action_item");
			if(itemsElement!=null){
				for ( Iterator j = itemsElement.elementIterator("item"); j.hasNext(); ) {
					Element itemElement = (Element) j.next();
					ActionItem actionItem=new ActionItem();
					actionItem.setItemIndex(Long.parseLong(itemElement.attributeValue("item_index")));
					actionItem.setItemName(itemElement.attributeValue("item_name"));
					actionItem.setItemDescription(itemElement.attributeValue("item_desc"));
					
					if (!itemElement.attributeValue("upper_spec").equals("null")) {
						actionItem.setItemUpperSpec(Double.valueOf(itemElement.attributeValue("upper_spec")));
					}
					if (!itemElement.attributeValue("lower_spec").equals("null")) {
						actionItem.setItemLowerSpec(Double.valueOf(itemElement.attributeValue("lower_spec")));
					}
					
					actionItem.setItemUnit(itemElement.attributeValue("spec_unit"));
					actionItem.setItemOption(itemElement.attributeValue("item_option"));
					//actionItem.setItemOrder(Integer.parseInt(itemElement.attributeValue("item_order")));//xml缺少此项目
					
					String itemType=itemElement.attributeValue("item_type");
					String itemValue=itemElement.attributeValue("item_value");
					String itemRemak=itemElement.attributeValue("item_remak");
					actionItem.setItemType(Integer.parseInt(itemType));
					actionItem.setItemValue(itemValue);
					actionItem.setItemNode(itemRemak);
					
					itemList.add(actionItem);
				}
				action.setItemlist(itemList);
		    }
			actionList.add(action);
		}
		job.setActionlist(actionList);
		return job;
	}
	

	/**
	 * 构造INDEX XML文件
	 * 
	 * @param formLibList
	 * @return
	 */
	public static Document createIndexXML(List formLibList){
		Document doc = DocumentHelper.createDocument();
		//sheet_list
		Element sheetList=doc.addElement("sheet_list");
		List downList=new ArrayList();
		for(int i=0;i<formLibList.size();i++){
			FormLib formLib=(FormLib)formLibList.get(i);
			Element sheetType=sheetList.addElement("sheet_type");
			sheetType.addAttribute("desc", formLib.getSheetTyp());
			List equipList=formLib.getEquipmentGroupList();
			if(equipList!=null&&equipList.size()>0){
				//equipment
				for(int k=0;k<equipList.size();k++){
					EquipmentGroup equipmentGroup=(EquipmentGroup)equipList.get(k);
					Element equipmentElement=sheetType.addElement("equipment");
					equipmentElement.addAttribute("desc",equipmentGroup.getEquipment());
					List itemList=equipmentGroup.getFormItemList();
					// equipment item
					if(itemList!=null&&itemList.size()>0){
						for(int j=0;j<itemList.size();j++){
							FormItem formItem=(FormItem)itemList.get(j);
							Element itemElement=equipmentElement.addElement("sheet");
							itemElement.addAttribute("desc",formItem.getFormName());
							itemElement.addAttribute("file_name",formItem.getFileName());
							downList.add(formItem.getFileName());
							downList.add(formItem.getFlowName());
						}
					}
				}
			}
		}
		Element download=sheetList.addElement("DOWNLOAD");
		for(int l=0;l<downList.size();l++){
			String fileName=String.valueOf(downList.get(l));
			Element fileElement=download.addElement("FILE");
			fileElement.addAttribute("file_name",fileName);
		}
		return doc;
	}
	
	/**
	 * 将job信息进行暂存(存为暂存格式的XML)
	 * 将存放进JOB轨迹信息中的所有信息写入XML
	 * @param job
	 * @param fileName 文件绝对路径
	 * @return
	 */
	private static boolean createTempForm(Job job,String fileName){
		Document doc = DocumentHelper.createDocument();
		Element sheet=doc.addElement("sheet");
		Element sheetInfo=sheet.addElement("sheet_info");
		Element event=sheetInfo.addElement("event");
		event.addAttribute("desc", "Event");
		event.setText(job.getEventType());
		Element jobIndexElement=sheetInfo.addElement("job_index");
		jobIndexElement.addAttribute("desc", "Job_Index");
		jobIndexElement.setText(String.valueOf(job.getJobIndex()));
		
		Element recordNameElement=sheetInfo.addElement("record_name");
		recordNameElement.addAttribute("desc", "Record_Name");
		recordNameElement.setText(job.getRecordName());
		
		Element equipmentIdElement=sheetInfo.addElement("equipment_id");
		equipmentIdElement.addAttribute("desc", "Equipment_Id");
		equipmentIdElement.setText(job.getEquipment());
		
		Element equipmentNoElement=sheetInfo.addElement("equipment_no");
		equipmentNoElement.addAttribute("desc", "Equipment_No");
		equipmentNoElement.setText(job.getEquipment());
		
		Element statusElement=sheetInfo.addElement("status");
		statusElement.addAttribute("desc", "status");
		statusElement.setText(String.valueOf(job.getRunStatus()));
		
		Element jobNameElement=sheetInfo.addElement("job_name");
		jobNameElement.addAttribute("desc", "Job_Name");
		jobNameElement.setText(String.valueOf(job.getJobName()));
		
		Element flowNameElement=sheetInfo.addElement("flow_name");
		flowNameElement.addAttribute("desc", "Flow_Name");
		flowNameElement.setText(job.getFlowName());
		
		Element createorElement=sheetInfo.addElement("createor");
		createorElement.addAttribute("desc", "Createor");
		createorElement.setText(String.valueOf(job.getCreator()));
		
		Element createDateElement=sheetInfo.addElement("create_date");
		createDateElement.addAttribute("desc", "create_date");
		createDateElement.setText(String.valueOf(job.getCreateTime()));
		
		Element operatorElement=sheetInfo.addElement("operator");
		operatorElement.addAttribute("desc", "operator");
		operatorElement.setText(String.valueOf(job.getOperator()));
		
		Element errorCodeElement=sheetInfo.addElement("error_code");
		errorCodeElement.addAttribute("desc", "error_code");
		errorCodeElement.setText(String.valueOf(job.getErrorCode()));
		
		Element errorEventElement=sheetInfo.addElement("error_event");
		errorEventElement.addAttribute("desc", "error_event");
		errorEventElement.setText(String.valueOf(job.getErrorEvent()));
		
		Element errorReasonElement=sheetInfo.addElement("error_reason");
		errorReasonElement.addAttribute("desc", "error_reason");
		errorReasonElement.setText("");
		
		Element nextActionIdElement=sheetInfo.addElement("next_action_id");
		nextActionIdElement.addAttribute("desc", "next_action_id");
		nextActionIdElement.setText(String.valueOf(job.getNextActionId()));
		
		Element finshDataElement=sheetInfo.addElement("finish_data");
		finshDataElement.addAttribute("desc", "finish_data");
		finshDataElement.setText(job.getFinishData());
		
		Element flowList=sheet.addElement("actions");
		List actionList=job.getActionlist();
		for(int i=0;i<actionList.size();i++){
			Action action=(Action)actionList.get(i);
			int actionId=action.getActionId();
			//在下一节点之前的所有action写入XML
			Element actionElement=flowList.addElement("action");
			if("end".equalsIgnoreCase(action.getNodeType())){
				actionId=-1;
				action.setActionDescription("结束流程");
			}else if("start".equalsIgnoreCase(action.getNodeType())){
				action.setActionDescription("开始流程");
			}else{
				actionElement.addAttribute("action_index",String.valueOf(action.getActionIndex()));
			}
			actionElement.addAttribute("action_id", String.valueOf(actionId));
			actionElement.addAttribute("action_name",action.getActionName());
			actionElement.addAttribute("action_desc", action.getActionDescription());
			actionElement.addAttribute("action_value", action.getActionNote());
			actionElement.addAttribute("finish_date",action.getFinishData());
			Element itemsElement=actionElement.addElement("action_item");
			List itemList=action.getItemlist();
			if(itemList!=null){
				// 写入ITEM
				for(int j=0;j<itemList.size();j++){
					ActionItem actionItem=(ActionItem)itemList.get(j);
					Element itemElement=itemsElement.addElement("item");
					itemElement.addAttribute("item_index", String.valueOf(actionItem.getItemIndex()));
					itemElement.addAttribute("item_name", actionItem.getItemName());
					itemElement.addAttribute("item_desc", actionItem.getItemDescription());
					itemElement.addAttribute("upper_spec", String.valueOf(actionItem.getItemUpperSpec()));
					itemElement.addAttribute("lower_spec", String.valueOf(actionItem.getItemLowerSpec()));
					itemElement.addAttribute("spec_unit", actionItem.getItemUnit());
					itemElement.addAttribute("item_option", actionItem.getItemOption());
					itemElement.addAttribute("item_type", String.valueOf(actionItem.getItemType()));
					itemElement.addAttribute("item_value", actionItem.getItemValue());
					itemElement.addAttribute("item_remak", actionItem.getItemNode());
				}
			}
			
			List stepList=action.getStatusList();
			if(stepList!=null&&stepList.size()>0){
				for(int k=0;k<stepList.size();k++){
					Element stepElement=actionElement.addElement("next_step");
					ActionStatus actionStatus=(ActionStatus)stepList.get(k);
					stepElement.addAttribute("desc", actionStatus.getStatusName());
					Action nextAciton=job.queryAction(actionStatus.getNextActionId());
					stepElement.addAttribute("next_action_name", nextAciton.getActionName());
					if("结束".equals(nextAciton.getActionName())){
						stepElement.addAttribute("next_action_id", "-1");
					}else{
						stepElement.addAttribute("next_action_id", String.valueOf(actionStatus.getNextActionId()));
					}
				}
			}
		}
		//异常表单需要异常原因
		if(Constants.TS.equals(job.getEventType())){
			Element reasonListElement=sheet.addElement("error_reason_list");
			List reasonList=job.getErrorReasonList();
			if(reasonList!=null&&reasonList.size()>0){
				for(int l=0;l<reasonList.size();l++){
					GenericValue gv=(GenericValue)reasonList.get(l);
					Element errorElement=reasonListElement.addElement("reason");
					errorElement.setText(gv.getString("reason"));
				}
			}
		}
		//格式化XML输出格式
		OutputFormat format = OutputFormat.createPrettyPrint(); //设置XML文档输出格式
		format.setEncoding("GB2312"); //设置XML文档的编码类型
		format.setIndent(true); //设置是否缩进
		format.setIndent("   "); //以空格方式实现缩进
		format.setNewlines(true); //设置是否换行
		try {
			XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(new File(fileName)), format);
			xmlWriter.write(doc);
			xmlWriter.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
