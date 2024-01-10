/**
 * 
 */
package com.csmc.pms.webapp.qufollow.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.common.helper.FileUploadHelper;
import com.csmc.pms.webapp.qufollow.help.QuFollowHelper;
import com.csmc.pms.webapp.util.Constants;

/**
 * @author shaoaj
 * @2007-9-3
 */
public class QuFollowService {
	public static final String module = QuFollowService.class.getName();
	
	/**
	 * 新增问题信息，如果是form过来的问题需要同时新增FOLLOW_FORM_RELATION
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map saveFollowJob(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		Map followMap = (Map) context.get("followMap");
		String eventType=(String)context.get("eventType");
		String eventIndex=(String)context.get("eventIndex");
		Map result = new HashMap();
		String resultStr = "";
		String followIndex = "";
		
		try {
			Map map=QuFollowHelper.mangeQuFollowJob(delegator, followMap);
			resultStr = (String)map.get("oper");
			followIndex = String.valueOf(map.get("followIndex"));
			
			//新增操作时进入
			if("FORM".equals((String) followMap.get("type")) && "addSuccess".equals(resultStr)){
				Map relationMap=new HashMap();
				relationMap.put("followIndex", followIndex);
				relationMap.put("eventType", eventType);
				relationMap.put("eventIndex",eventIndex);
				QuFollowHelper.addQuFollowFormRealation(delegator, relationMap);
				resultStr="formAddSuccess";				
			}
		}catch (Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE,
					ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		result.put("resultStr", resultStr);
		result.put("followIndex", followIndex);
		return result;
	}

	/**
	 * 删除问题信息
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map deleteFollowJob(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		String followIndex = (String) context.get("followIndex");
		Map result = new HashMap();
		try {
			QuFollowHelper.deleteQuFollowJobByPk(delegator, followIndex);
			QuFollowHelper.deleteQuFollowRelationByFollowIndex(delegator, followIndex);
		} catch (Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE,
					ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}
	
	/**
	 * 新增问题步骤信息及问题文档信息
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map saveFollowItem(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		Map itemMap = (Map) context.get("itemMap");
		Map result = new HashMap();
		try {
			GenericValue gv = delegator.makeValidValue("FollowItem", itemMap);
			Long id = null;
			if (StringUtils.isEmpty((String) itemMap.get("itemIndex"))) {
				id = delegator.getNextSeqId("itemJobIndex");
				gv.put("itemIndex", id);
				String sql = " select nvl(max(ITEM_ORDER),0)+1 MAXORDER from FOLLOW_ITEM where FOLLOW_INDEX="
					+ (String)itemMap.get("followIndex");
				List orderList = SQLProcess.excuteSQLQuery(sql, delegator);
				String maxOrder = (String) ((Map) orderList.get(0)).get("MAXORDER");
				gv.put("itemOrder", maxOrder);
				gv.put("createTime", new Timestamp(System.currentTimeMillis()));
				gv.put("updateTime", new Timestamp(System.currentTimeMillis()));
			} else {
				gv.put("updateTime", new Timestamp(System.currentTimeMillis()));
			}
			String uploadIndex=(String)itemMap.get("uploadIndex");
			delegator.createOrStore(gv);
			//新增时需要更新upload表信息
			if(id!=null&&StringUtils.isNotEmpty(uploadIndex)){
				FileUploadHelper.updateFile(delegator, uploadIndex, String.valueOf(id),String.valueOf(Constants.DOC_FOLLOW));
			}
		} catch (Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE,
					ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}
	
	public static Map saveDocItem(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		Map itemMap = (Map) context.get("itemMap");
		Map result = new HashMap();
		try {
			String id = null;
			id=(String)itemMap.get("eventIndex");
			String uploadIndex=(String)itemMap.get("uploadIndex");
			//新增时需要更新upload表信息
			if(id!=null&&StringUtils.isNotEmpty(uploadIndex)){
				FileUploadHelper.updateFile(delegator, uploadIndex, String.valueOf(id),String.valueOf(Constants.DOC_ABNORMAL));
			}
		} catch (Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE,
					ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}
	
	public static Map saveDocItemImprove(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		Map itemMap = (Map) context.get("itemMap");
		Map result = new HashMap();
		try {
			String id = null;
			id=(String)itemMap.get("eventIndex");
			String uploadIndex=(String)itemMap.get("uploadIndex");
			//新增时需要更新upload表信息
			if(id!=null&&StringUtils.isNotEmpty(uploadIndex)){
				FileUploadHelper.updateFile(delegator, uploadIndex, String.valueOf(id),String.valueOf(Constants.DOC_IMPROVER));
			}
		} catch (Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE,
					ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}


	/**
	 * 删除问题步骤信息及问题文档信息
	 * @param ctx
	 * @param context
	 * @return
	 */
	public static Map deleteFollowItem(DispatchContext ctx, Map context) {
		GenericDelegator delegator = ctx.getDelegator();
		String itemIndex = (String) context.get("itemIndex");
		String eventType = (String) context.get("eventType");
		Map result = new HashMap();
		try {
			//删除item信息
			GenericValue gv = delegator.findByPrimaryKey("FollowItem", UtilMisc.toMap("itemIndex", itemIndex));
	        delegator.removeValue(gv);
	        //删除uploadFile信息
	        FileUploadHelper.delUploadFileByEventIndex(delegator, itemIndex, eventType);
		} catch (Exception e) {
			result.put(ModelService.RESPONSE_MESSAGE,
					ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}
}
