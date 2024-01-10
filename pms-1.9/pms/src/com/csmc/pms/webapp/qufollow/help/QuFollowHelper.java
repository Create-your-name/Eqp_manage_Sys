package com.csmc.pms.webapp.qufollow.help;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.util.Constants;

/**
 * @author shaoaj
 * @2007-8-29
 */
public class QuFollowHelper {
	public static final String module = QuFollowHelper.class.getName();
//----------------------------------------共通方法-----------------------------------------------------
	/**
	 * 查询登陆人信息
	 * 
	 * @param delegator
	 * @return 登陆人信息列表
	 */
	public static List getAccountSection(GenericDelegator delegator,
			String accountNo) throws Exception {
		List list = delegator.findByAnd("Account", UtilMisc.toMap("accountNo",accountNo));
		return list;
	}

	 /**
	 * 通过科别得到eqpid,并根据eqpid排序
	 * @param section
	 * @param delegator
	 * @return 设备ID列表
	 */
	public static List getEquipIDList(GenericDelegator delegator,String section) throws Exception {
		List list = delegator.findByAndCache("Equipment", UtilMisc.toMap("section",section), UtilMisc.toList("equipmentId"));
		return list;
	}
	
	/**
	 * 通过部门得到eqpid,并根据eqpid排序
	 * @param maintDept
	 * @param delegator
	 * @return 设备ID列表
	 */
	public static List getEquipIDListByUseDept(GenericDelegator delegator,String maintDept) throws Exception {
		List list = delegator.findByAnd("Equipment", UtilMisc.toMap("maintDept",maintDept), UtilMisc.toList("equipmentId"));
		return list;
	}
	
	/**
	 * 通过科别名称得到科别INDEX及部门INDEX
	 * @param delegator
	 * @param section 科别中文名
	 * @return
	 * @throws Exception
	 */
	public static List getSectionInfoList(GenericDelegator delegator,String section)throws Exception {
		List list = delegator.findByAndCache("EquipmentSection", UtilMisc.toMap("section",section));
		return list;
	}
	
	/**
	 * 根据dept_index获得section信息列表
	 * @param delegator
	 * @param deptIndex
	 * @return
	 * @throws Exception
	 */
	public static List getSectionList(GenericDelegator delegator,String deptIndex)throws Exception {
		List list = delegator.findByAndCache("EquipmentSection", UtilMisc.toMap("deptIndex",deptIndex), UtilMisc.toList("section"));
		return list;
	}
	
    //-------------------------------------------------问题--------------------------------------------------------
	/**
	 * 新增/更新问题追踪信息
	 * @param delegator
	 * @param param
	 * @throws GenericEntityException
	 */
	public static Map mangeQuFollowJob(GenericDelegator delegator, Map param) throws GenericEntityException {
        GenericValue gv = delegator.makeValidValue("FollowJob", param);
        Long id = null;
        Map map=new HashMap();
        String result="";
        if (StringUtils.isEmpty((String) param.get("followIndex"))) {
            id = delegator.getNextSeqId("followIndex");
            map.put("followIndex", id);
            gv.put("followIndex", id);
            gv.put("createTime", new Timestamp(System.currentTimeMillis()));
            result="addSuccess";
        }else{
        	 gv.put("updateTime", new Timestamp(System.currentTimeMillis()));
        	 result="editSuccess";
        }
        delegator.createOrStore(gv);
        map.put("oper", result);
        return map;
    }
	
	/**
	 * 新增异常与表单的关联表
	 * @param delegator
	 * @param map
	 * @throws GenericEntityException
	 */
	public static void addQuFollowFormRealation(GenericDelegator delegator, Map map)throws GenericEntityException {
		 Long id =delegator.getNextSeqId("FollowFormRelationIndex");
		 map.put("seqIndex", id);
		 GenericValue gv=delegator.makeValue("FollowFormRelation", map);
		 delegator.create(gv);
	}
	
	/**
     * 维护问题信息
     * @param delegator
     * @param map
     * @throws GenericEntityException
     */
    public static Map manageFollowJob(GenericDelegator delegator, LocalDispatcher dispatcher,Map followMap, String eventType, String eventIndex)throws Exception {
    	if(eventType==null){
    		eventType="";
    	}
    	
    	if(eventIndex==null){
    		eventIndex="";
    	}
    	
    	Map result = dispatcher.runSync("saveFollowJobAction",UtilMisc.toMap("followMap",followMap, "eventType",eventType, "eventIndex",eventIndex));
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
		return result;
    }
    
    /**
     * 根据followIndex删除信息
     * 
     * @param delegator
     * @param value
     *            reasonIndex
     * @throws GenericEntityException
     */
    public static void deleteQuFollowJobByPk(GenericDelegator delegator, String followIndex) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("FollowJob", UtilMisc.toMap("followIndex", followIndex));
        delegator.removeValue(gv);
    }
    
	/**
     * 删除问题信息
     * @param delegator
     * @param map
     * @throws GenericEntityException
     */
    public static void delFollowJobService(GenericDelegator delegator, LocalDispatcher dispatcher,String followIndex)throws Exception {
    	Map result = dispatcher.runSync("delFollowJobAction",UtilMisc.toMap("followIndex" ,followIndex));
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
    }
    
    /**
     * 根据followIndex删除表单关联信息
     * 
     * @param delegator
     * @param value
     *            reasonIndex
     * @throws GenericEntityException
     */
    public static void deleteQuFollowRelationByFollowIndex(GenericDelegator delegator, String followIndex) throws GenericEntityException {
        List list = delegator.findByAnd("FollowFormRelation", UtilMisc.toMap("followIndex", followIndex));
        delegator.removeAll(list);
    }
    
	/**
	 * 根据工艺大类或设备查询本课下面的未结案及创建的问题信息
	 * @param delegator
	 * @param type  类别(EQUIPMENT,STYLE)
	 * @param value 工艺大类的值或是设备id
	 * @param sectionIndex 登陆人科别 
	 * @param stats 问题状态  
	 * @return
	 * @throws GenericEntityException
	 */
    
    public static List queryQuFollowJobList(GenericDelegator delegator, String type,String value,String status,String sectionIndex) throws Exception {
    	StringBuffer sb=new StringBuffer();
    	sb.append("select t.FOLLOW_INDEX,t.FOLLOW_NAME,t.PURPOSE, t.OBJECT," +
    			"t.STATUS,decode(t.STATUS,'0','建立','1','已结案','2','未结案') CSTATUS," +
    			"t.CREATOR,t.CREATE_TIME, t2.SECTION,t3.equipment_dept ");
    	sb.append(" from FOLLOW_JOB t,equipment_section t2,equipment_dept t3 where ");
    	sb.append("t.SECTION_INDEX=t2.SECTION_INDEX and t.DEPT_INDEX=t3.DEPT_INDEX");
    	sb.append(" and t.SECTION_INDEX="+sectionIndex);
    	
    	if(StringUtils.isNotEmpty(value)){
    		sb.append(" and t.OBJECT_TYPE='").append(type).append("' and t.OBJECT='").append(value).append("'");
    	}
    	if(StringUtils.isNotEmpty(status)){
    		sb.append(" and t.STATUS='").append(status).append("'");
    	}else{
    		sb.append(" and t.STATUS!='").append(Constants.FOLLOWJOB_OVER).append("'");
    	}
    	
    	sb.append(" order by t.CREATE_TIME desc");
    	List resultList=SQLProcess.excuteSQLQuery(sb.toString(), delegator);
        return resultList;
    }
    
    /**
     * 根据followIndex查询问题信息
     * @param delegator
     * @param followIndex
     * @return
     * @throws GenericEntityException
     */
    public static GenericValue queryQuFollowJobByIndex(GenericDelegator delegator,String followIndex)throws GenericEntityException{
    	GenericValue gv=delegator.findByPrimaryKey("FollowJob",  UtilMisc.toMap("followIndex", followIndex));
    	return gv;
    }
    
    /**
     * 更新问题状态(已结案-1；未结案-2)
     * @param delegator
     * @param param
     * @throws GenericEntityException
     */
    public static void updateJobStatus(GenericDelegator delegator,Map param)throws GenericEntityException{
    	GenericValue gv = delegator.makeValidValue("FollowJob", param);
    	gv.put("updateTime", new Timestamp(System.currentTimeMillis()));
    	delegator.store(gv);
    }
   //---------------------------------------------问题步骤----------------------------------------------
    
    /**
     * 维护问题步骤
     * @param delegator
     * @param map
     * @throws GenericEntityException
     */
    public static void manageFollowJbItem(GenericDelegator delegator, LocalDispatcher dispatcher,Map itemMap)throws Exception {
    	Map result = dispatcher.runSync("saveFollowItemAction",UtilMisc.toMap("itemMap" ,itemMap));
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
    }
    
    /**
     * 根据主键删除问题步骤及文档信息
     * @param delegator
     * @param flowIndex followItem主键
     * @param uploadIndex documentUpload主键
     * @throws GenericEntityException
     */
    public static void delFollowJobItemByPk(GenericDelegator delegator,  LocalDispatcher dispatcher,String itemIndex) throws Exception {
    	Map result = dispatcher.runSync("deleteFollowItemAction",UtilMisc.toMap("itemIndex" ,itemIndex,"eventType",String.valueOf(Constants.DOC_FOLLOW)));
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
    }
    
    /**
     * 得到问题的具体步骤信息及其文档
     * @param delegator
     * @param followIndex
     * @return
     * @throws GenericEntityException
     */
    public static List queryQuItemList(GenericDelegator delegator, String followIndex,String url) throws Exception {
    	List itemList=new ArrayList();
    	List fileList=new ArrayList();
    	List resultList=new ArrayList();
    	List orderList=new ArrayList();
    	orderList.add("itemOrder");
    	itemList=delegator.findByAnd("FollowItem", UtilMisc.toMap("followIndex" ,followIndex),orderList);
    	if(itemList.size()>0){
    		//follosItem
    		for (int i=0;i<itemList.size();i++){
    			GenericValue gv=(GenericValue)itemList.get(i);
    			fileList=delegator.findByAnd("DocumentUpload", UtilMisc.toMap("eventIndex" ,gv.getString("itemIndex"),"eventType",String.valueOf(Constants.DOC_FOLLOW)));
    			StringBuffer fileDescription=new StringBuffer();;
    			//uploadFile
    			for(int j=0;j<fileList.size();j++){
    				GenericValue fileGv=(GenericValue)fileList.get(j);
    				fileDescription.append("<a href='").append(url);
    				fileDescription.append("?uploadIndex=").append(fileGv.getString("uploadIndex")).append("&followIndex=").append(followIndex).append("'>");
    				fileDescription.append(fileGv.getString("fileDescription")).append("</a>;");
    			}
    			Map resultMap=new HashMap();
    			resultMap.put("ITEM_INDEX",gv.getString("itemIndex"));
    			resultMap.put("ITEM_ORDER",gv.getString("itemOrder"));
    			resultMap.put("ITEM_CONTENT",gv.getString("itemContent"));
    			resultMap.put("FILE_DESCRIPTION",fileDescription.toString());
    			resultMap.put("CREATE_TIME",gv.getString("createTime"));
    			resultMap.put("UPDATE_TIME",gv.getString("updateTime"));
    			resultList.add(resultMap);
    			resultMap=null;
    		}
    	}
        return resultList;
    }
    
    //-------------------------------------------------问题--------------------------------------------------------
	/**
	 * 根据条件查询问题追踪信息
	 * @param delegator
	 * @param map
	 * @return
	 * @throws GenericEntityException
	 */    
    public static List queryQuFollowJobList(GenericDelegator delegator, Map map)throws Exception {
    	String objectType=(String)map.get("objectType");
    	String object=(String)map.get("object");
    	String sectionIndex=(String)map.get("sectionIndex");
    	String deptIndex=(String)map.get("deptIndex");
    	String beginTime=(String)map.get("beginTime");
    	String endTime=(String)map.get("endTime");
    	String status=(String)map.get("status");
    	String eventType = (String) map.get("eventType");
    	String eventIndex = (String) map.get("eventIndex");
    	
    	StringBuffer sb=new StringBuffer();
    	sb.append(" select t.FOLLOW_INDEX,t.FOLLOW_NAME,t.PURPOSE,t.STATUS," +
    			"decode(t.STATUS,'0','建立','1','已结案','2','未结案') CSTATUS," +
    			"t.CREATOR,t.CREATE_TIME, t2.SECTION,t3.EQUIPMENT_DEPT," +
    			"t.update_time, t.OBJECT, t4.event_type,t4.event_index");
    	sb.append(" from FOLLOW_JOB t,equipment_section t2,equipment_dept t3, FOLLOW_FORM_RELATION t4");
    	sb.append(" where t.SECTION_INDEX=t2.SECTION_INDEX and t.DEPT_INDEX=t3.DEPT_INDEX");
    	sb.append(" and t.follow_index = t4.follow_index(+)");
    	
    	//课别
    	if(StringUtils.isNotEmpty(sectionIndex)){
    		sb.append(" and t.SECTION_INDEX="+sectionIndex);
    	}
    	
    	//部门
    	if(StringUtils.isNotEmpty(sectionIndex)){
    		sb.append(" and t.DEPT_INDEX="+deptIndex);
    	}
    	
    	if(StringUtils.isNotEmpty(object)){
    		sb.append(" and t.OBJECT_TYPE='").append(objectType).append("' and t.OBJECT='").append(object).append("'");
    	}
    	
    	if(StringUtils.isNotEmpty(beginTime)){
    		sb.append(" and t.CREATE_TIME>=to_date('").append(beginTime).append("','yyyy-mm-dd hh24:mi:ss')");
    	}
    	
    	if(StringUtils.isNotEmpty(endTime)){
    		sb.append(" and t.CREATE_TIME<to_date('").append(endTime).append("','yyyy-mm-dd hh24:mi:ss')+1");
    	}
    	
    	if (StringUtils.isNotEmpty(status)){
    		sb.append(" and t.STATUS='").append(status).append("'");
    	}
    	
    	if (StringUtils.isNotEmpty(eventType)){
    		sb.append(" and t4.event_type='").append(eventType).append("'");
    	}
    	
    	if (StringUtils.isNotEmpty(eventIndex)){
    		sb.append(" and t4.event_index='").append(eventIndex).append("'");
    	}
    	
    	sb.append(" order by t.CREATE_TIME desc");
    	List resultList = SQLProcess.excuteSQLQuery(sb.toString(), delegator);
        return resultList;
    }
}
