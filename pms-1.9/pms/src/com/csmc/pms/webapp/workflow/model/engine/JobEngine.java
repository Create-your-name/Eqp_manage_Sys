package com.csmc.pms.webapp.workflow.model.engine;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.eqp.helper.GenDCOPHelper;
import com.csmc.pms.webapp.util.TPServiceException;
import com.csmc.pms.webapp.util.CacheUtil;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.workflow.exception.FlowException;
import com.csmc.pms.webapp.workflow.model.Action;
import com.csmc.pms.webapp.workflow.model.ActionItem;
import com.csmc.pms.webapp.workflow.model.Job;

/**
 * 为表单提供的流程类
 * @author laol
 *
 */
public class JobEngine extends JobSupport{
	public static final String module = JobEngine.class.getName();

	//JobEngine Constants
	public static final String RETURN_PAGE_KEY = "pageReturnKey";
	public static final String VIEW_DCOPFMT_KEY = "dcopViewKey";
	public static final String JOB_KEY = "job";
	public static final String RUN_RESPONSE_MESSAGE = "runResponseMessage";
	public static final String RUN_ERROR = "runError";
	public static final String RUN_ERROR_MESSAGE = "runErrorMessage";
	public static final String RUN_SUCCESS = "runSuccess";

	//CacheUtil
	private CacheUtil cacheUtil = CacheUtil.getInstance();

	//GenericDelegator
	private GenericDelegator delegator = null;
	//job form relation index (FormJobRelation)
	private String jobRelationIndex;
	//cacheKey (sessionId + jobRelationIndex)
	private String sessionId;
	//pageRequest current action mapInfo
	private Map actionStepInfo;
	//user loginId
	private GenericValue userLoginId;

	public GenericDelegator getDelegator() {
		return delegator;
	}

	public void setDelegator(GenericDelegator delegator) {
		this.delegator = delegator;
	}

	public String getJobRelationIndex() {
		return jobRelationIndex;
	}

	public void setJobRelationIndex(String jobRelationIndex) {
		this.jobRelationIndex = jobRelationIndex;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Map getActionStepInfo() {
		return actionStepInfo;
	}

	public void setActionStepInfo(Map actionStepInfo) {
		this.actionStepInfo = actionStepInfo;
	}

	public GenericValue getUserLoginId() {
		return userLoginId;
	}

	public void setUserLoginId(GenericValue userLoginId) {
		this.userLoginId = userLoginId;
	}

	/**
	 * 初始化Job对象，并放入缓存
	 * @param jobRelationIndex
	 * @param sessionId
	 * @param delegator
	 * @return
	 * @throws FlowException
	 */
	private Job initialize() throws FlowException {
		//查询获得Job-xml
		try {
			Job job = this.getJob();
			cacheUtil.setObjectToCache(getCacheElementKey(jobRelationIndex, sessionId),CacheUtil.JOB_CACHE ,job, false);
			return job;
		} catch(GenericEntityException e) {
			throw new FlowException("Get Job Error:" + e.getMessage());
		}
	}

	public Map run() {
		Map result = new HashMap();
		try {
			Job job = this.initialize();
			boolean isStore = false;
			String nextActionId = (String)actionStepInfo.get("nextActionId");
			//String currentActionId = (String)actionStepInfo.get("currentActionId");

			if(CommonUtil.isEmpty(nextActionId)) {
				//load数据库保存的nextActionId
				 nextActionId = String.valueOf(job.getNextActionId());
			} else {
				 isStore = true;
			}

			if(isStore) {
				//如果从页面得到NextActionId，则进行保存动作
				 try {
					this.storeActionStep();
				} catch (GenericServiceException e) {
					Debug.logError(e, module);
					result.put(JobEngine.RUN_RESPONSE_MESSAGE, JobEngine.RUN_ERROR);
					result.put(JobEngine.RUN_ERROR_MESSAGE, "保存失败:" + e.getMessage());
				}
			}

			result.put(JobEngine.JOB_KEY, job);
			//获得nextActionId，判断下一步是否为DCOP类型，是跳转DCOP查询
			//根据jobStatus跳转页面,start,process
			if(job.getRunStatus() == Constants.JOB_START) {
				result.put(JobEngine.RETURN_PAGE_KEY, "start");
				 //return "start";
			} else if(job.getRunStatus() == Constants.JOB_PROCESS){
				 //若页面得不到nextActionId，且状态为Process,则从Job中得到
				 //Job中的nextActionId从FORM_JOB_RELATION中查询得到
				 //job中得到的nextActionId为了查询下一步是否为DCOP数据
				 //TODO Check Next is dcop
				result.put(JobEngine.RETURN_PAGE_KEY, "process");
			} else if(job.getRunStatus() == Constants.JOB_END) {
				result.put(JobEngine.RETURN_PAGE_KEY, "process");
			}
		} catch(Exception e) {
			result.put(JobEngine.RETURN_PAGE_KEY, "error");
			result.put(JobEngine.RUN_RESPONSE_MESSAGE, JobEngine.RUN_ERROR);
			result.put(JobEngine.RUN_ERROR_MESSAGE, e.getMessage());
			//throw new FlowException(e.getMessage());
		}
		return result;
	}

	/**
	 * 1. 根据JobStatus,ActionType保存数据(Normal,Dcop)
	 * 2. 获得下一步Action信息
	 * 3. 根据runStatus，保存当前步辅助信息(InsertJobActionRecord,UpdateFromJobRelation)
	 * 4. 生成JobText，保存入Form
	 * 5. 更新缓存中的Job信息（runStatus,nextActionId）
	 * 6. 根据下一步Action信息的Type执行查询，如果是DCOP跳入QueryDCOPFunction
	 * @param paramMap
	 * @param delegator
	 * @throws GenericServiceException
	 */
	private void storeActionStep() throws GenericServiceException {
		LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
		Map result = dispatcher.runSync("storeActionStepInfo",UtilMisc.toMap("jobEngine",this));
    	//throw exception
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
	}

	/**
	 * 获得JOB
	 * @param jobIndex
	 * @param sessionId
	 * @param delegator
	 * @return
	 * @throws GenericEntityException
	 * @throws FlowException
	 */
	public synchronized Job getJob() throws GenericEntityException, FlowException {
		//从cache查询Job，如果没有则查询FormJobRelation
		Job job = (Job)cacheUtil.getObjectFromCache(getCacheElementKey(jobRelationIndex, sessionId), CacheUtil.JOB_CACHE);
		if(job == null) {
			job = getJobFromEntity();
			if(job == null) {
				Debug.logError("FormJobRelationIndex:" + jobRelationIndex, module);
				throw new FlowException(JobSupport.FLOW_ERROR_MESSAGE);
			}
		}
		return job;
	}

	/**
	 * 从数据库中得到Job
	 * @param jobIndex
	 * @param delegator
	 * @return
	 * @throws FlowException
	 * @throws GenericEntityException
	 */
	private Job getJobFromEntity() throws FlowException, GenericEntityException {
		GenericValue jobRelationInfo = delegator.findByPrimaryKey("FormJobRelation", UtilMisc.toMap("seqIndex", new Long(jobRelationIndex)));
		//得到job
		Job job = this.parseJob(jobRelationInfo, false, delegator);
		//query dcopAction format
		exchangeDcopFmtToJob(job);
		return job;
	}

	public Job getJobToOffline(GenericValue jobRelationEntity) throws FlowException {
		Job job = this.parseJob(jobRelationEntity, false,delegator);
		exchangeDcopFmtToJob(job);
		return job;
	}

	/**
	 * DCOP数据类型转换成ActionItem
	 * @param job
	 */
	public void exchangeDcopFmtToJob(Job job) {
		Iterator it = job.getActionlist().iterator();
		while(it.hasNext()) {
			Action action = (Action) it.next();
			List actionItemList = action.getItemlist()==null?new LinkedList():action.getItemlist();
			if(Constants.ACTION_DCOP_TYPE.equals(action.getActionType()) && actionItemList.size() == 0) {
				//查询DCOP数据
				String operationId = action.getActionName();
				Map map = null;
				try {
					map = GenDCOPHelper.queryDcopFormat(operationId, delegator, userLoginId);
				} catch (TPServiceException e) {
					Debug.logError("query dcopFmt error " + e.getMessage(), module);
				}
				//set dcop data to job
				if(map != null) {
					String dcopType = (String) map.get("dcoptypecategory");
					if("ITEM".equals(dcopType) || "COMP".equals(dcopType)) {
						List itemlist = (List) map.get("itemlist");
						Iterator tpItemIt = itemlist.iterator();
						int itemOrder = 0;
						while(tpItemIt.hasNext()) {
							com.fa.object.Item tpItem = (com.fa.object.Item)tpItemIt.next();
							ActionItem item = new ActionItem();
							item.setItemName(tpItem.getItemName());
							item.setItemDescription(tpItem.getItemName());
							item.setItemType(Constants.NUMBER_ITEM);
							item.setItemIndex(itemOrder);
							try {
								item.setItemLowerSpec(Double.valueOf(tpItem.getItemLowLimit()));
								item.setItemUpperSpec(Double.valueOf(tpItem.getItemUpLimit()));
							} catch(NumberFormatException e) {

							}
							item.setItemOrder(itemOrder);
							actionItemList.add(item);
							itemOrder++;
						}
						action.setItemlist(actionItemList);
					}
				}
			}
		}
	}

	/**
	 * 查询已完成流程列表数据
	 * @return
	 * @throws FlowException
	 * @throws GenericEntityException
	 */
	public Job getViewJobFromEntity()  throws FlowException, GenericEntityException {
		GenericValue jobRelationInfo = delegator.findByPrimaryKey("FormJobRelation", UtilMisc.toMap("seqIndex", new Long(jobRelationIndex)));
		//得到job
		Job job = this.parseJob(jobRelationInfo, true, delegator);
		return job;
	}

	/**
	 * return jobEngine
	 * @return
	 */
	public static JobEngine create() {
		return new JobEngine();
	}

	/**
	 * 获得元素index
	 * @param jobIndex
	 * @param sessionId
	 * @return
	 */
	public String getCacheElementKey(String jobIndex, String sessionId) {
		return sessionId + "-" + jobIndex;
	}
}
