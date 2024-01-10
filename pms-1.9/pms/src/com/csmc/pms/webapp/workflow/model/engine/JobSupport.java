package com.csmc.pms.webapp.workflow.model.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.serialize.SerializeException;
import org.ofbiz.entity.serialize.XmlSerializer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.workflow.exception.FlowException;
import com.csmc.pms.webapp.workflow.model.Action;
import com.csmc.pms.webapp.workflow.model.ActionItem;
import com.csmc.pms.webapp.workflow.model.ActionStatus;
import com.csmc.pms.webapp.workflow.model.Job;

public class JobSupport {
	public static final String module = JobSupport.class.getName();
	
	public static final String FLOW_ERROR_MESSAGE = "���������⣬����ϵ����Ա��";
	public static final String ACTION_FROZEN_MESSAGE = "����δ���ᣬ����ϵ����Ա��";
	public static final String ACTION_ENABLED_MESSAGE = "������ʹ��״̬������ϵ����Ա��";
	public static final String STATUS_ERROR_MESSAGE = "����״̬��������ϵ����Ա��";
	
	/**
	 * Jobת����XML
	 * @param job
	 * @return
	 * @throws FlowException 
	 * @throws DOMException
	 * @throws SerializeException
	 * @throws IOException
	 */
	public String formatJob(Job job) throws FlowException{
		Document document = UtilXml.makeEmptyXmlDocument("job");
        Element rootElement = document.getDocumentElement();
        rootElement.setAttribute("jobIndex", String.valueOf(job.getJobIndex()));
        
        try {
			rootElement.appendChild(renderJobElement(job.getActionlist(), document));
			return UtilXml.writeXmlDocument(document);
		} catch (DOMException e) {
			Debug.logError(e.getMessage(), module);
			throw new FlowException(e);
		} catch (SerializeException e) {
			Debug.logError(e.getMessage(), module);
			throw new FlowException(e);
		} catch (IOException e) {
			Debug.logError(e.getMessage(), module);
			throw new FlowException(e);
		}
	}
	
	/**
	 * �ݹ�Object���xml
	 * @param object
	 * @param document
	 * @return
	 * @throws SerializeException
	 */
	private Element renderJobElement(Object object, Document document) throws SerializeException {
		if(object instanceof List) {
			List list = (List)object;
			//���elementName
			String elementName = getElementName(list);
			Element element = document.createElement(elementName);
			for(Iterator it = list.iterator();it.hasNext();) {
				element.appendChild(renderJobElement(it.next(), document));
			}
			return element;
		} else if(object instanceof Action) {
			Action action = (Action)object;
			//����actionId
			Element element = document.createElement("action");
			element.setAttribute("id", String.valueOf(action.getActionId()));
			//����nodeType
			String type = action.getNodeType();
			element.setAttribute("type", type);
			//����ActionIndex
			if("action".equals(type)) {
				element.setAttribute("actionIndex", String.valueOf(action.getActionIndex()));
			}
			//���ö���״��
			if(CommonUtil.isNotEmpty(action.getStatusList())) {
				element.appendChild(renderJobElement(action.getStatusList(), document));
			}
			return element;
		} else if(object instanceof ActionStatus) {
			ActionStatus status = (ActionStatus)object;
			Element element = null;
			//�����ж��Ƿ���StatusIndex���ж��������������״��
			if(status.getStatusIndex() != 0) {
				element = document.createElement("conditional-result");
				element.setAttribute("statusIndex", String.valueOf(status.getStatusIndex()));
				element.setAttribute("id", String.valueOf(status.getNextActionId()));
			} else {
				element = document.createElement("unconditional-result");
				element.setAttribute("id", String.valueOf(status.getNextActionId()));
			}
			return element;
		}
		
		return XmlSerializer.serializeCustom(object, document);
	}
	
	/**
	 * ���ElementName
	 * �ж�Ҫ�½���Element��Type(actions,results)
	 * @param list
	 * @return
	 */
	private String getElementName(List list) {
		String elename = null;
		try {
			Object object = list.get(0);
			if(object instanceof Action) elename = "actions";
			else if(object instanceof ActionStatus) elename = "results";
		} catch(IndexOutOfBoundsException e) {
			Debug.logError(e.getMessage(), module);
		}
		return elename;
	}
	
	/**
	 * ����JobIndex�õ�Job
	 * @param jobIndex
	 * @param delegator
	 * @return
	 * @throws FlowException
	 */
	public Job parseJob(String jobIndex, GenericDelegator delegator, boolean isQueryItem) throws FlowException {
		Document document = null;
		GenericValue jobEntity = null;
		try {
			jobEntity = delegator.findByPrimaryKey("FlowJob", UtilMisc.toMap("jobIndex", new Long(jobIndex)));
			
			if (jobEntity == null) {
				//Job��ɾ��,��Job��ʷ�в�ѯ
				List jobList = delegator.findByAnd("FlowJobHist", UtilMisc.toMap("jobIndex",new Long(jobIndex),"evt", Constants.DELETE), UtilMisc.toList("histIndex"));
				jobEntity = (GenericValue) jobList.get(jobList.size()-1);
			}
			
			document = UtilXml.readXmlDocument(jobEntity.getString("jobContent"));
		} catch (SAXException e) {
			throw new FlowException(e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new FlowException(e.getMessage());
		} catch (IOException e) {
			throw new FlowException(e.getMessage());
		} catch (GenericEntityException e) {
			throw new FlowException(e.getMessage());
		}
		
		if(document == null) throw new FlowException("xml document is null");
		
		Element rootElement = document.getDocumentElement();
		
		Job job = this.getJobFromEntity(jobEntity);
		if(CommonUtil.isNotEmpty(job)) {
			//getActionList
			List actionList = getActionList(rootElement, isQueryItem, delegator);
			
			if(CommonUtil.isNotEmpty(actionList)) {
				job.setActionlist(actionList);
			}
		}
		
		return job;
	}
	

	
	/**
	 * ����FlowJobHist�õ�Job
	 * @param delegator
	 * @return
	 * @throws FlowException
	 */
	public Job parseHistJob(String histIndex, GenericDelegator delegator, boolean isQueryItem) throws FlowException {
		Document document = null;
		GenericValue jobEntity = null;
		try {
			jobEntity = delegator.findByPrimaryKey("FlowJobHist", UtilMisc.toMap("histIndex", new Long(histIndex)));
			
//			if (jobEntity == null) {
//				//Job��ɾ��,��Job��ʷ�в�ѯ
//				List jobList = delegator.findByAnd("FlowJobHist", UtilMisc.toMap("jobIndex",new Long(jobIndex),"evt", Constants.DELETE), UtilMisc.toList("histIndex"));
//				jobEntity = (GenericValue) jobList.get(jobList.size()-1);
//			}
			
			if (jobEntity != null) document = UtilXml.readXmlDocument(jobEntity.getString("jobContent"));
		} catch (SAXException e) {
			throw new FlowException(e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new FlowException(e.getMessage());
		} catch (IOException e) {
			throw new FlowException(e.getMessage());
		} catch (GenericEntityException e) {
			throw new FlowException(e.getMessage());
		}
		
		if(document == null) throw new FlowException("xml document is null��");
		
		Element rootElement = document.getDocumentElement();
		
		Job job = this.getJobHistFromEntity(jobEntity);
		if(CommonUtil.isNotEmpty(job)) {
			//getActionList
			List actionList = getActionList(rootElement, isQueryItem, delegator);
			
			if(CommonUtil.isNotEmpty(actionList)) {
				job.setActionlist(actionList);
			}
		}
		
		return job;
	}
	
	/**
	 * ��ȡJobRelation
	 * @param jobIndex
	 * @param delegator
	 * @return
	 * @throws FlowException
	 */
	public Job parseJobRelation(String jobRelationIndex, GenericDelegator delegator) throws FlowException {
		Document document = null;
		GenericValue jobRelationEntity = null;
		GenericValue jobEntity = null;
		try {
			jobRelationEntity = delegator.findByPrimaryKey("FormJobRelation", UtilMisc.toMap("seqIndex", new Long(jobRelationIndex)));
			document = UtilXml.readXmlDocument(jobRelationEntity.getString("jobContent"));
			jobEntity = delegator.findByPrimaryKey("FlowJob", UtilMisc.toMap("jobIndex", jobRelationEntity.getLong("jobIndex")));
		} catch (SAXException e) {
			throw new FlowException(e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new FlowException(e.getMessage());
		} catch (IOException e) {
			throw new FlowException(e.getMessage());
		} catch (GenericEntityException e) {
			throw new FlowException(e.getMessage());
		}
		
		if(document == null) throw new FlowException("xml document is null");
		
		Element rootElement = document.getDocumentElement();
		
		Job job = this.getJobFromEntity(jobEntity);
		if(CommonUtil.isNotEmpty(job)) {
			//getActionList
			List actionList = getActionList(rootElement, false, delegator);
			
			if(CommonUtil.isNotEmpty(actionList)) {
				job.setActionlist(actionList);
			}
		}
		
		return job;
	}
	
	/**
	 * �����ʱ�����Job����
	 * @param tempIndex
	 * @param delegator
	 * @return
	 * @throws FlowException
	 */
	public Job parseJobTemp(String tempIndex, GenericDelegator delegator, boolean isQueryItem) throws FlowException {
		Document document = null;
		GenericValue jobEntity = null;
		try {
			jobEntity = delegator.findByPrimaryKey("FlowJobTemp", UtilMisc.toMap("tempIndex", new Long(tempIndex)));
			if (jobEntity != null) document = UtilXml.readXmlDocument(jobEntity.getString("jobContent"));
		} catch (SAXException e) {
			throw new FlowException(e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new FlowException(e.getMessage());
		} catch (IOException e) {
			throw new FlowException(e.getMessage());
		} catch (GenericEntityException e) {
			throw new FlowException(e.getMessage());
		}
		
		if (document == null) {
			throw new FlowException("xml document is null���Ҳ������̸�����ǩ�˾ܾ�����ɾ�� �� ���޸ĺ����·���ǩ����ͬ�⡣");
		}
		
		Element rootElement = document.getDocumentElement();
		
		Job job = this.getJobTempFromEntity(jobEntity);
		if(CommonUtil.isNotEmpty(job)) {
			//getActionList
			List actionList = getActionList(rootElement, isQueryItem, delegator);
			
			if(CommonUtil.isNotEmpty(actionList)) {
				job.setActionlist(actionList);
			}
			
			if(Constants.SUBMIT_INSERT.equals(jobEntity.getString("submitType"))) {
				job.setNewFlag(true);
			}
		}
		
		return job;
	}
	
	/**
	 * ���ݱ�JobEntity�õ�Job
	 * @param jobXml(XML),isView(�Ƿ���ʾJob����)
	 * @throws FlowException 
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public Job parseJob(GenericValue jobRelationEntity, boolean isView, GenericDelegator delegator) throws FlowException {
		Document document = null;
		try {
			document = UtilXml.readXmlDocument(jobRelationEntity.getString("jobContent"));
		} catch (SAXException e) {
			throw new FlowException(e.getMessage());
		} catch (ParserConfigurationException e) {
			throw new FlowException(e.getMessage());
		} catch (IOException e) {
			throw new FlowException(e.getMessage());
		}
		
		if(document == null) throw new FlowException("xml document is null");
		
		Element rootElement = document.getDocumentElement();
		
//		long jobIndex = Long.parseLong(rootElement.getAttribute("jobIndex"));
		
		Job job = getJobFromRelationEntity(jobRelationEntity, delegator);
		if(CommonUtil.isNotEmpty(job)) {
			//getActionList
			List actionList = null;
			if(isView) {
				//�����ʾ���ݣ������JobRelation,FormType,FormIndex����Ѿ�����ĳ�������
				actionList = this.getViewActionList(jobRelationEntity, delegator);
			} else {
				//�����̻��ActionList��Item����Ҫ��
				actionList = getActionList(rootElement, true, delegator);
			}
			
			if(CommonUtil.isNotEmpty(actionList)) {
				job.setActionlist(actionList);
			}
		}
		
		return job;
	}
	
	/**
	 * ��ѯEntity���Job����
	 * @param jobEntity
	 * @return Job
	 */
	private Job getJobFromEntity(GenericValue jobEntity) {
		try {
			Job job = new Job();
			job.setJobIndex(jobEntity.getLong("jobIndex").longValue());
			job.setJobName(jobEntity.getString("jobName"));
			job.setJobDescription(jobEntity.getString("jobDescription"));
			job.setStatus(jobEntity.getInteger("status").intValue());
			job.setTransBy(jobEntity.getString("transBy"));			
			return job;
		} catch (NullPointerException e) {
			Debug.logError(e.getMessage(), module);
		}
		return null;
	}
	
	/**
	 * ��ѯhist���Job����
	 * @param jobEntity
	 * @return Job
	 */
	private Job getJobHistFromEntity(GenericValue jobEntity) {
		try {
			Job job = new Job();
			job.setJobIndex(jobEntity.getLong("jobIndex").longValue());
			job.setJobName(jobEntity.getString("jobName"));
			job.setJobDescription(jobEntity.getString("jobDescription"));
			job.setStatus(jobEntity.getInteger("status").intValue());
			job.setTransBy(jobEntity.getString("transBy"));
			job.setEvt(jobEntity.getString("evt"));
			return job;
		} catch (NullPointerException e) {
			Debug.logError(e.getMessage(), module);
		}
		return null;
	}
	
	/**
	 * ��ѯtemp���Job����
	 * @param jobEntity
	 * @return Job
	 */
	private Job getJobTempFromEntity(GenericValue jobEntity) {
		try {
			Job job = new Job();
			job.setJobIndex(jobEntity.getLong("jobIndex").longValue());
			job.setJobName(jobEntity.getString("jobName"));
			job.setJobDescription(jobEntity.getString("jobDescription"));
			job.setStatus(jobEntity.getInteger("status").intValue());
			job.setTempIndex(jobEntity.getLong("tempIndex").longValue());
			job.setTransBy(jobEntity.getString("transBy"));
			job.setEvt(jobEntity.getString("submitType"));
			return job;
		} catch (NullPointerException e) {
			Debug.logError(e.getMessage(), module);
		}
		return null;
	}
	
	/**
	 * ���ύRelation���ݣ���ѯ�õ�Job
	 * @param jobRelationEntity
	 * @param delegator
	 * @return
	 */
	private Job getJobFromRelationEntity(GenericValue jobRelationEntity, GenericDelegator delegator) {
		Job job = new Job();
		
		try {
			Long jobIndex = jobRelationEntity.getLong("jobIndex");
			job.setJobIndex(jobIndex.longValue());
			job.setJobName(jobRelationEntity.getString("jobName"));
			job.setNextActionId(jobRelationEntity.getInteger("nextActionId"));
			job.setRunStatus(jobRelationEntity.getInteger("jobStatus"));
			
			GenericValue jobEntity = delegator.findByPrimaryKey("FlowJob", UtilMisc.toMap("jobIndex", jobIndex));			
			if (jobEntity != null) {				
				job.setJobDescription(jobEntity.getString("jobDescription"));
				job.setStatus(jobEntity.getInteger("status").intValue());				
			} else {
				//Job�ѱ�ɾ��,ʹ��Ĭ������ JobDescΪjobName,statusΪ1		
				job.setJobDescription(jobRelationEntity.getString("jobName"));
				job.setStatus(1);	
			}
		} catch (NullPointerException e) {					
			Debug.logError(e.getMessage(), module);
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
		}
		
		return job;
	}
	
	/**
	 * ���Action�б�
	 * @param rootElement
	 * @return
	 * @throws FlowException 
	 */
	private List getActionList(Element rootElement, boolean isQyeryItem, GenericDelegator delegator) throws FlowException {
		List actionList = new LinkedList();
		Node node = rootElement.getFirstChild();
		
		while (node != null && node.getNodeType() != Node.ELEMENT_NODE) {
			node = node.getNextSibling();
        }
		
		if(node != null && node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			
			List actionsElement = UtilXml.childElementList(element, "action");
			//ѭ��action-element
			Iterator actionIte = actionsElement.iterator(); 
			while (actionIte.hasNext()) {
				Element actionEle = (Element) actionIte.next();
				//xml�ڵ�����(start,action,end)
				String nodeType = actionEle.getAttribute("type");
				//xml��action�����
				String actionOrder = actionEle.getAttribute("id");
				//action-index
				String actionIndex = actionEle.getAttribute("actionIndex");
				//���Action
				Action action = getActionFromEntity(actionIndex, nodeType, delegator);
				action.setActionId(Integer.parseInt(actionOrder));
				action.setNodeType(nodeType);
				
				if("start".equals(nodeType)) {
					action.setActionName("��ʼ");
				} else if("end".equals(nodeType)) {
					action.setActionName("����");
				}
				
				//���ActionStatus
				List actionStatusList = getActionStatusList(actionEle, delegator);
				if(CommonUtil.isNotEmpty(actionStatusList)) {
					action.setStatusList(actionStatusList);
				}
				actionList.add(action);
				
				//���ActionItem
				if(isQyeryItem) {
					List itemList = queryActionItemList(actionIndex, delegator);
					if(CommonUtil.isNotEmpty(itemList)) {
						action.setItemlist(itemList);
					}
				}
			}
		}
		
		return actionList;
	}
	
	/**
	 * ��ѯ���ActionItem�б�
	 * @param actionIndex
	 * @param delegator
	 * @return
	 */
	private List queryActionItemList(String actionIndex, GenericDelegator delegator) {
		if(CommonUtil.isNotEmpty(actionIndex)) {
			try {
				List itemList = new LinkedList();
				List itemEntityList = delegator.findByAnd("FlowActionItem", UtilMisc.toMap("actionIndex", actionIndex), UtilMisc.toList("itemOrder"));
				if(CommonUtil.isNotEmpty(itemEntityList)) {
					for(Iterator it = itemEntityList.iterator(); it.hasNext(); ) {
						GenericValue itemEntity = (GenericValue)it.next();
						ActionItem item = getActionItemFromEntity(itemEntity);
						if(CommonUtil.isNotEmpty(item)) {
							itemList.add(item);
						}
					}
				}
				return itemList;
			} catch (GenericEntityException e) {
				Debug.logError(e.getMessage(), module);
			}
		}
		return null;
	}

	/**
	 * �ӱ���ѯ�����д��ʱ��ActionItem�б�
	 * @param actionRecordIndex
	 * @param formType
	 * @param formIndex
	 * @param delegator
	 * @return
	 */
	private List queryFormActionItemList(String actionRecordIndex, String formType,
			String formIndex, GenericDelegator delegator) {
		
		if (CommonUtil.isNotEmpty(actionRecordIndex)
				&& CommonUtil.isNotEmpty(formType)
				&& CommonUtil.isNotEmpty(formIndex)) {
			try {
				List itemList = new LinkedList();
				
				List itemPointsList = delegator.findByAnd("FlowItemPoints",
						UtilMisc.toMap("actionRecordIndex", actionRecordIndex,
								"formType", formType, "formIndex", formIndex),
						UtilMisc.toList("pointIndex"));
				
				if (CommonUtil.isNotEmpty(itemPointsList)) {
					for (Iterator it = itemPointsList.iterator(); it.hasNext();) {
						GenericValue itemPointsEntity = (GenericValue) it.next();
						Long itemIndex = itemPointsEntity.getLong("itemIndex");

						GenericValue itemEntity = delegator.findByPrimaryKey(
								"FlowActionItem", UtilMisc.toMap("itemIndex", itemIndex));
						if (CommonUtil.isNull(itemEntity)) {
							// item��ɾ��,��item��ʷ�в�ѯ
							List itemHistList = delegator.findByAnd(
									"FlowActionItemHist", UtilMisc.toMap(
											"itemIndex", itemIndex,
											"evt", Constants.DELETE), UtilMisc.toList("histIndex"));
							itemEntity = (GenericValue) itemHistList.get(itemHistList.size() - 1);
						}

						ActionItem item = getActionItemFromEntity(itemEntity);
						if (CommonUtil.isNotEmpty(item)) {
							itemList.add(item);
						}
					}
				}
				
				return itemList;
				
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
			}
		}
		
		return null;
	}

	/**
	 * ��ѯItemEntity���ActionItem����
	 * @param itemEntity
	 * @return
	 */
	private ActionItem getActionItemFromEntity(GenericValue itemEntity) {
		try {
			ActionItem item = new ActionItem();
			item.setItemName(itemEntity.getString("itemName"));
			item.setItemDescription(itemEntity.getString("itemDescription"));
			item.setItemIndex(itemEntity.getLong("itemIndex").longValue());
			item.setItemType(itemEntity.getInteger("itemType").intValue());
			item.setItemOrder(itemEntity.getInteger("itemOrder").intValue());
			//TODO ����Type��ͬ���벻ͬ����λ������null���д���
			item.setDefaultValue(UtilFormatOut.checkNull(itemEntity.getString("defaultValue")));
			item.setItemUnit(UtilFormatOut.checkNull(itemEntity.getString("itemUnit")));
			
			if(CommonUtil.isNotNull(itemEntity.getDouble("itemLowerSpec"))) {
				item.setItemLowerSpec(itemEntity.getDouble("itemLowerSpec"));
			}
			if(CommonUtil.isNotNull(itemEntity.getDouble("itemUpperSpec"))) {
				item.setItemUpperSpec(itemEntity.getDouble("itemUpperSpec"));
			}
			
			item.setItemOption(UtilFormatOut.checkNull(itemEntity.getString("itemOption")));
			
			return item;
		} catch (NullPointerException e) {
			Debug.logError(e.getMessage(), module);
		}
		return null;
	}
	
	/**
	 * �õ�Action����
	 * @param actionIndex
	 * @param nodeType
	 * @param delegator
	 * @return
	 * @throws FlowException 
	 */
	private Action getActionFromEntity(String actionIndex, String nodeType, GenericDelegator delegator) throws FlowException {
		Action action = new Action();
		
		if("action".equals(nodeType)) {
			if(CommonUtil.isNotEmpty(actionIndex)) {
				try {
					GenericValue actionEntity = delegator.findByPrimaryKey("FlowAction", UtilMisc.toMap("actionIndex", actionIndex));
					if(actionEntity == null) {
						// query deleted action from FlowActionHist
						actionEntity = CommonUtil.findFirstRecordByAnd(delegator, "FlowActionHist", UtilMisc.toMap("actionIndex", actionIndex, "evt", Constants.DELETE));
						if (actionEntity == null) {
							Debug.logError(FLOW_ERROR_MESSAGE + " actionIndex: " + actionIndex, module);
							throw new FlowException(FLOW_ERROR_MESSAGE);
						}
					}
					//index
					action.setActionIndex(Long.parseLong(actionIndex));
					//����
					action.setActionDescription(actionEntity.getString("actionDescription"));
					//����
					action.setActionName(actionEntity.getString("actionName"));
					//actionType(normal,dcop)
					action.setActionType(actionEntity.getString("actionType"));
					//ʹ��
					action.setEnabled(Integer.parseInt(actionEntity.getString("enabled")));
					//����
					//action.setFrozen(Integer.parseInt(actionEntity.getString("frozen")));
					action.setEmpty(Integer.parseInt(actionEntity.getString("empty")));
				} catch (GenericEntityException e) {
					Debug.logError(e.getMessage(), module);
				}
			}
		}
		
		return action;
	}
	
	/**
	 * ��ѯ��øö��������е�״��
	 * @param actionElement
	 * @return
	 * @throws FlowException 
	 */
	private List getActionStatusList(Element actionElement, GenericDelegator delegator) throws FlowException {
		List actionStatusList = new LinkedList();
		
		if(actionElement.getNextSibling() != null) {
			Node node = actionElement.getFirstChild();//.getFirstChild();
			
			while (node != null && node.getNodeType() != Node.ELEMENT_NODE) {
				node = node.getNextSibling();
            }
			
			if(node != null && node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				List unconditionalList = UtilXml.childElementList(element, "unconditional-result");
				List conditionList = UtilXml.childElementList(element, "conditional-result");
				//���������
				if(CommonUtil.isNotEmpty(unconditionalList)) {
					Iterator ite = unconditionalList.iterator();
					while(ite.hasNext()) {
						Element statusElement = (Element)ite.next();
						int nextActionId = Integer.parseInt(statusElement.getAttribute("id"));
						
						ActionStatus actionStatus = new ActionStatus();
						actionStatus.setNextActionId(nextActionId);
						actionStatus.setStatusName("��һ��");
						actionStatusList.add(actionStatus);
					}
				}
				//�������
				if(CommonUtil.isNotEmpty(conditionList)) {
					Iterator ite = conditionList.iterator();
					while(ite.hasNext()) {
						Element statusElement = (Element)ite.next();
						int nextActionId = Integer.parseInt(statusElement.getAttribute("id"));
						long statusIndex = Long.parseLong(statusElement.getAttribute("statusIndex"));
						
						ActionStatus actionStatus = getActionStatusFromEntity(statusIndex, delegator);
						if(CommonUtil.isNotEmpty(actionStatus)) {
							actionStatus.setNextActionId(nextActionId);
							actionStatusList.add(actionStatus);
						}
					}
				}
			}
		}
		return actionStatusList;
	}
	
	/**
	 * ��ѯ�������״̬
	 * @param statusIndex
	 * @param delegator
	 * @return
	 * @throws FlowException 
	 */
	private ActionStatus getActionStatusFromEntity(long statusIndex, GenericDelegator delegator) throws FlowException {
		try {
			ActionStatus status = new ActionStatus();
			GenericValue statusEntity = delegator.findByPrimaryKey("FlowActionStatus", UtilMisc.toMap("statusIndex", new Long(statusIndex)));
			if(statusEntity == null) {
				Debug.logError(STATUS_ERROR_MESSAGE + ":" + statusIndex, module);
				throw new FlowException(STATUS_ERROR_MESSAGE);
			}
			//index
			status.setStatusIndex(statusEntity.getLong("statusIndex").longValue());
			//name
			status.setStatusName(statusEntity.getString("statusName"));
			return status;
		} catch(GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
		}
		return null;
	}
	
	/**
	 * ��ѯ��ʾ�����޸�����ʱ����ActionList
	 * @param jobRelationJob
	 * @param delegator
	 * @return
	 * @throws FlowException
	 */
	private List getViewActionList(GenericValue jobRelationJob, GenericDelegator delegator) throws FlowException{
		List actionList = new LinkedList();
		String jobRelationIndex = jobRelationJob.getString("seqIndex");
		
		try {
			//.startAction
			Action startAction = new Action();
			startAction.setActionId(0);
			startAction.setActionName("��ʼ");
			startAction.setNodeType("start");
			actionList.add(startAction);
			 
			List actionRecordEntityList = delegator.findByAnd("FlowActionRecord", UtilMisc.toMap("formJobIndex", jobRelationIndex), UtilMisc.toList("stepOrder"));
			GenericValue jobRelationEntity = delegator.findByPrimaryKey("FormJobRelation", UtilMisc.toMap("seqIndex", jobRelationIndex));
			Iterator it = actionRecordEntityList.iterator();
			int i = 1;
			while(it.hasNext()) {
				GenericValue actionRecordEntity = (GenericValue)it.next();
				String actionIndex = actionRecordEntity.getString("actionIndex");
				String actionRecordIndex = actionRecordEntity.getString("actionRecordIndex");
				String formType = actionRecordEntity.getString("formType");
				String formIndex = actionRecordEntity.getString("formIndex");
				
//				Action action = this.getActionFromEntity(actionIndex, "action", delegator);
				Action action = new Action();
				action.setActionName(actionRecordEntity.getString("actionName"));
				action.setActionType(actionRecordEntity.getString("actionType"));
				action.setActionNote(actionRecordEntity.getString("stepComment"));
				action.setNodeType("action");
				action.setActionRecordIndex(actionRecordEntity.getLong("actionRecordIndex").longValue());
				
				//����Type��ͬ�ֱ�ȡ�ò�ͬ��Item				
				/**List actionItemList = this.queryFormActionItemList(actionRecordIndex, formType, formIndex, delegator);
				if(Constants.ACTION_DCOP_TYPE.equals(action.getActionType())) {
					this.setDcopItemItemValue(action, actionRecordIndex, delegator);
				} else {
					if(CommonUtil.isNotEmpty(actionItemList)) {
						action.setItemlist(actionItemList);
						this.setActionItemValue(action, actionRecordIndex, delegator);
					}
				}**/
				
				List actionItemList = new ArrayList();
				List itemPoints = delegator.findByAnd("FlowItemPoints",
						UtilMisc.toMap("actionRecordIndex", actionRecordEntity.getLong("actionRecordIndex")));
				for (Iterator itemIt = itemPoints.iterator(); itemIt.hasNext();) {
					GenericValue points = (GenericValue) itemIt.next();
					ActionItem item = new ActionItem();
					item.setItemOption(UtilFormatOut.checkNull(points.getString("itemOption")));
					item.setItemUnit(UtilFormatOut.checkNull(points.getString("itemUnit")));
					if (points.getInteger("itemOrder") != null)
						item.setItemOrder(points.getInteger("itemOrder").intValue());
					item.setItemIndex(points.getLong("itemIndex").longValue());
					item.setItemName(points.getString("itemName"));
					item.setItemValue(points.getString("itemValue"));
					item.setItemType(points.getInteger("itemType").intValue());
					item.setPointIndex(points.getLong("pointIndex").longValue());
					item.setItemNode(points.getString("itemNote"));
					if (CommonUtil.isNotNull(points.getDouble("itemLowerSpec"))) {
						item.setItemLowerSpec(points.getDouble("itemLowerSpec"));
					}
					if (CommonUtil.isNotNull(points.getDouble("itemUpperSpec"))) {
						item.setItemUpperSpec(points.getDouble("itemUpperSpec"));
					}
					actionItemList.add(item);
				}
				
				if(CommonUtil.isNotEmpty(actionItemList)) {
					action.setItemlist(actionItemList);
				}
				
				action.setActionId(i);
				actionList.add(action);
				i++;
			}
			
			if(jobRelationEntity.getInteger("jobStatus").intValue() == 1) {
				//.endAction
				Action endAction = new Action();
				endAction.setActionId(i);
				endAction.setActionName("����");
				endAction.setNodeType("end");
				actionList.add(endAction);
			}
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), module);
			throw new FlowException(e);
		} 
		return actionList;
	}
	
	/**
	 * Set Item Value
	 * @param action
	 * @param actionRecordIndex
	 * @param delegator
	 * @throws GenericEntityException
	 */
	private void setActionItemValue(Action action, String actionRecordIndex, GenericDelegator delegator) throws GenericEntityException {
		List itemList = action.getItemlist();
		Iterator it = itemList.iterator();
		while(it.hasNext()) {
			ActionItem item = (ActionItem)it.next();
			long itemIndex = item.getItemIndex();
			List valueList = delegator.findByAnd("FlowItemPoints", UtilMisc.toMap("itemIndex", new Long(itemIndex), "actionRecordIndex", actionRecordIndex));
			
			if(valueList.size() > 0) {
				GenericValue value = (GenericValue)valueList.get(0);
				item.setItemValue(value.getString("itemValue"));
				item.setPointIndex(value.getLong("pointIndex").longValue());
				item.setItemNode(value.getString("itemNote"));
				if(CommonUtil.isNotNull(value.getDouble("itemLowerSpec"))) {
					item.setItemLowerSpec(value.getDouble("itemLowerSpec"));
				}
				if(CommonUtil.isNotNull(value.getDouble("itemUpperSpec"))) {
					item.setItemUpperSpec(value.getDouble("itemUpperSpec"));
				}
			}
		}
	}
	
	/**
	 * ��ѯ���ݻ��DCOP��Item��
	 * @param action
	 * @param actionRecordIndex
	 * @param delegator
	 * @throws GenericEntityException
	 */
	private void setDcopItemItemValue(Action action, String actionRecordIndex, GenericDelegator delegator) throws GenericEntityException {
		List actionRecordList = delegator.findByAnd("FlowItemPoints", UtilMisc.toMap("actionRecordIndex", actionRecordIndex));
		List itemList = action.getItemlist()==null?new LinkedList():action.getItemlist();
		for(Iterator it = actionRecordList.iterator(); it.hasNext(); ) {
			GenericValue itemPointEntity = (GenericValue)it.next();
			ActionItem item = new ActionItem();
			item.setItemName(itemPointEntity.getString("itemName"));
			item.setItemType(Constants.NUMBER_ITEM);
			item.setItemNode(itemPointEntity.getString("itemNote"));
			item.setItemValue(itemPointEntity.getString("itemValue"));
			item.setPointIndex(itemPointEntity.getLong("pointIndex").longValue());
			
			if(CommonUtil.isNotNull(itemPointEntity.getDouble("itemLowerSpec"))) {
				item.setItemLowerSpec(itemPointEntity.getDouble("itemLowerSpec"));
			}
			if(CommonUtil.isNotNull(itemPointEntity.getDouble("itemUpperSpec"))) {
				item.setItemUpperSpec(itemPointEntity.getDouble("itemUpperSpec"));
			}
			
			itemList.add(item);
		}
		action.setItemlist(itemList);
	}
	
	public static JobSupport getInstance() {
		return new JobSupport();
	}
}
