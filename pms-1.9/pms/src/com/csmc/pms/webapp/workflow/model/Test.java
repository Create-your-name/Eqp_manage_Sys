package com.csmc.pms.webapp.workflow.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.serialize.SerializeException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.csmc.pms.webapp.workflow.exception.FlowException;
import com.csmc.pms.webapp.workflow.model.engine.JobSupport;

public class Test {
	public static String getXmlJob() throws FlowException {
		Job job = new Job();
		
		Action action1 = new Action();
		action1.setActionId(0);
		action1.setNodeType("start");
		ActionStatus status1 = new ActionStatus();
		status1.setNextActionId(2);
		action1.setStatusList(UtilMisc.toList(status1));
		
		Action action2 = new Action();
		action2.setActionId(1);
		action2.setNodeType("action");
		ActionStatus status2 = new ActionStatus();
		status2.setStatusIndex(1);
		status2.setNextActionId(3);
		ActionStatus status3 = new ActionStatus();
		status2.setStatusIndex(10000);
		status2.setNextActionId(2);
		action2.setStatusList(UtilMisc.toList(status2,status3));
		
		/*Action action3 = new Action();
		action3.setActionId(3);
		action3.setNodeType("action");
		ActionStatus status4 = new ActionStatus();
		status2.setStatusIndex(11113);
		status2.setNextActionId(3);
		ActionStatus status5 = new ActionStatus();
		status2.setStatusIndex(11114);
		status2.setNextActionId(4);
		action3.setStatusList(UtilMisc.toList(status4,status5));*/
		
		Action action4 = new Action();
		action4.setActionId(2);
		action4.setNodeType("end");
		
		job.setActionlist(UtilMisc.toList(action1, action2, action4));
		JobSupport support = new JobSupport();
		return support.formatJob(job);
	}
	
	public static void getJob() throws DOMException, SerializeException, IOException, SAXException, ParserConfigurationException, FlowException {
		String x = getXmlJob();
		Document doc = UtilXml.readXmlDocument(x);
		Element rootEle = doc.getDocumentElement();
		Element jobEle = UtilXml.firstChildElement(rootEle, "actions");
		List list = UtilXml.childElementList(jobEle, "action");
	}

	public static void main(String[] args) {
		String serializedPK = null;
        try {
//            serializedPK = getXmlJob();
			try {
				getJob();
			} catch (FlowException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } catch (SerializeException e) {
        	e.printStackTrace();
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        } catch (DOMException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}
