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
//----------------------------------------��ͨ����-----------------------------------------------------
	/**
	 * ��ѯ��½����Ϣ
	 * 
	 * @param delegator
	 * @return ��½����Ϣ�б�
	 */
	public static List getAccountSection(GenericDelegator delegator,
			String accountNo) throws Exception {
		List list = delegator.findByAnd("Account", UtilMisc.toMap("accountNo",accountNo));
		return list;
	}

	 /**
	 * ͨ���Ʊ�õ�eqpid,������eqpid����
	 * @param section
	 * @param delegator
	 * @return �豸ID�б�
	 */
	public static List getEquipIDList(GenericDelegator delegator,String section) throws Exception {
		List list = delegator.findByAndCache("Equipment", UtilMisc.toMap("section",section), UtilMisc.toList("equipmentId"));
		return list;
	}
	
	/**
	 * ͨ�����ŵõ�eqpid,������eqpid����
	 * @param maintDept
	 * @param delegator
	 * @return �豸ID�б�
	 */
	public static List getEquipIDListByUseDept(GenericDelegator delegator,String maintDept) throws Exception {
		List list = delegator.findByAnd("Equipment", UtilMisc.toMap("maintDept",maintDept), UtilMisc.toList("equipmentId"));
		return list;
	}
	
	/**
	 * ͨ���Ʊ����Ƶõ��Ʊ�INDEX������INDEX
	 * @param delegator
	 * @param section �Ʊ�������
	 * @return
	 * @throws Exception
	 */
	public static List getSectionInfoList(GenericDelegator delegator,String section)throws Exception {
		List list = delegator.findByAndCache("EquipmentSection", UtilMisc.toMap("section",section));
		return list;
	}
	
	/**
	 * ����dept_index���section��Ϣ�б�
	 * @param delegator
	 * @param deptIndex
	 * @return
	 * @throws Exception
	 */
	public static List getSectionList(GenericDelegator delegator,String deptIndex)throws Exception {
		List list = delegator.findByAndCache("EquipmentSection", UtilMisc.toMap("deptIndex",deptIndex), UtilMisc.toList("section"));
		return list;
	}
	
    //-------------------------------------------------����--------------------------------------------------------
	/**
	 * ����/��������׷����Ϣ
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
	 * �����쳣����Ĺ�����
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
     * ά��������Ϣ
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
     * ����followIndexɾ����Ϣ
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
     * ɾ��������Ϣ
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
     * ����followIndexɾ����������Ϣ
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
	 * ���ݹ��մ�����豸��ѯ���������δ�᰸��������������Ϣ
	 * @param delegator
	 * @param type  ���(EQUIPMENT,STYLE)
	 * @param value ���մ����ֵ�����豸id
	 * @param sectionIndex ��½�˿Ʊ� 
	 * @param stats ����״̬  
	 * @return
	 * @throws GenericEntityException
	 */
    
    public static List queryQuFollowJobList(GenericDelegator delegator, String type,String value,String status,String sectionIndex) throws Exception {
    	StringBuffer sb=new StringBuffer();
    	sb.append("select t.FOLLOW_INDEX,t.FOLLOW_NAME,t.PURPOSE, t.OBJECT," +
    			"t.STATUS,decode(t.STATUS,'0','����','1','�ѽ᰸','2','δ�᰸') CSTATUS," +
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
     * ����followIndex��ѯ������Ϣ
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
     * ��������״̬(�ѽ᰸-1��δ�᰸-2)
     * @param delegator
     * @param param
     * @throws GenericEntityException
     */
    public static void updateJobStatus(GenericDelegator delegator,Map param)throws GenericEntityException{
    	GenericValue gv = delegator.makeValidValue("FollowJob", param);
    	gv.put("updateTime", new Timestamp(System.currentTimeMillis()));
    	delegator.store(gv);
    }
   //---------------------------------------------���ⲽ��----------------------------------------------
    
    /**
     * ά�����ⲽ��
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
     * ��������ɾ�����ⲽ�輰�ĵ���Ϣ
     * @param delegator
     * @param flowIndex followItem����
     * @param uploadIndex documentUpload����
     * @throws GenericEntityException
     */
    public static void delFollowJobItemByPk(GenericDelegator delegator,  LocalDispatcher dispatcher,String itemIndex) throws Exception {
    	Map result = dispatcher.runSync("deleteFollowItemAction",UtilMisc.toMap("itemIndex" ,itemIndex,"eventType",String.valueOf(Constants.DOC_FOLLOW)));
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
    }
    
    /**
     * �õ�����ľ��岽����Ϣ�����ĵ�
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
    
    //-------------------------------------------------����--------------------------------------------------------
	/**
	 * ����������ѯ����׷����Ϣ
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
    			"decode(t.STATUS,'0','����','1','�ѽ᰸','2','δ�᰸') CSTATUS," +
    			"t.CREATOR,t.CREATE_TIME, t2.SECTION,t3.EQUIPMENT_DEPT," +
    			"t.update_time, t.OBJECT, t4.event_type,t4.event_index");
    	sb.append(" from FOLLOW_JOB t,equipment_section t2,equipment_dept t3, FOLLOW_FORM_RELATION t4");
    	sb.append(" where t.SECTION_INDEX=t2.SECTION_INDEX and t.DEPT_INDEX=t3.DEPT_INDEX");
    	sb.append(" and t.follow_index = t4.follow_index(+)");
    	
    	//�α�
    	if(StringUtils.isNotEmpty(sectionIndex)){
    		sb.append(" and t.SECTION_INDEX="+sectionIndex);
    	}
    	
    	//����
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
