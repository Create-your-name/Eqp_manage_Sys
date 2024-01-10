package com.csmc.pms.webapp.document.event;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.entity.condition.EntityWhereString;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.util.GeneralEvents;
import com.csmc.pms.webapp.basic.event.BasicEvent;
import com.csmc.pms.webapp.common.helper.FileUploadHelper;
import com.csmc.pms.webapp.parts.help.PartsHelper;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;

public class DocumentEvent extends GeneralEvents {

	public static final String module = DocumentEvent.class.getName();

	/**
	 * 进入页面
	 * @param request
	 * @param response
	 * @return
	 */
	public static String intoabnormalDocument(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try {
			GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
			String user = userLogin.getString("userLoginId");
			List list=PartsHelper.getAccountSection(delegator,user);
			GenericValue gv=(GenericValue)list.get(0);
			request.setAttribute("accountSection", gv.getString("accountSection"));
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
		return "success";
	}

	/**
	 * 查询设备ID
	 *
	 * @param request
	 * @param response
	 * @return action
	 */
	public static String queryDocumentNo(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try
		{
			String equipmentId = request.getParameter("equipmentId").toUpperCase().trim();
            GenericValue listEquipment = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId", equipmentId));
            if (listEquipment.size() <= 0){
            	request.setAttribute("_EVENT_MESSAGE_", "equipmentId无效！");
            	Debug.logError(equipmentId + " equipmentId无效！", module);
    			request.setAttribute("_ERROR_MESSAGE_", "equipmentId无效！");
            	return "error";
            }
            else
            {
				String equipmentID = request.getParameter("equipmentId");
				request.setAttribute("equipmentId", equipmentID);
				EntityWhereString con = new EntityWhereString("STATUS <> '1' AND EQUIPMENT_ID = '" + equipmentID + "'");

	            List list = delegator.findByCondition("AbnormalDocument", con, null, UtilMisc.toList("createTime"));
				request.setAttribute("DocumentList", list);
				request.setAttribute("flag", "OK");
				return "success";
            }
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
	}

	/**
	 * 显示设备ID
	 *
	 * @param request
	 * @param response
	 * @return action
	 */
	public static String abnormalDocDefineShow (HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);

		try
		{
			String user = CommonUtil.getUserNo(request);

            GenericValue listUser = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));

            String equipmentId = request.getParameter("equipmentId").toUpperCase().trim();
            GenericValue listEquipment = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId", equipmentId));
            if (listEquipment.size() <= 0){
            	request.setAttribute("_EVENT_MESSAGE_", "equipmentId无效！");
            	Debug.logError(equipmentId + " equipmentId无效！", module);
    			request.setAttribute("_ERROR_MESSAGE_", "equipmentId无效！");
            	return "error";
            }
            else
            {
            	request.setAttribute("equipmentId", equipmentId);
				request.setAttribute("AccountShowList", listUser);
				request.setAttribute("EquipmentShowList", listEquipment);
				//
				Long id = delegator.getNextSeqId("abnormalDocIndex");
				String member = com.csmc.pms.webapp.common.helper.CommonHelper.getDocumentName(Constants.ABNORMAL_DOC_CHAR, equipmentId, delegator);
				request.setAttribute("abnormalDocName", member);
				GenericValue gv = delegator.makeValue("AbnormalDocument", UtilMisc.toMap("abnormalDocIndex", id));
				gv.set("status", "0");
				gv.set("createTime", new Timestamp(System.currentTimeMillis()));
				gv.set("abnormalDocName", member);
				delegator.create(gv);
				GenericValue listDocument = delegator.findByPrimaryKey("AbnormalDocument", UtilMisc.toMap("abnormalDocIndex",id));
				request.setAttribute("DocumentShowList", listDocument);
				request.setAttribute("flag", "OK");

//				新增时需要更新upload表信息
				LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
				//String followIndex=request.getParameter("followIndex");
				String itemIndex=request.getParameter("itemIndex");
				String uploadIndex=request.getParameter("uploadIndex");
				Map itemMap=new HashMap();
				itemMap.put("itemIndex", itemIndex);
				itemMap.put("uploadIndex", uploadIndex);
				itemMap.put("uploadIndex", uploadIndex);
				manageFollowJbItem(delegator, dispatcher,itemMap);
				//
				return "success";
            }
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
	}

    /**
     * 维护问题步骤
     * @param delegator
     * @param map
     * @throws GenericEntityException
     */
    public static void manageFollowJbItem(GenericDelegator delegator, LocalDispatcher dispatcher,Map itemMap)throws Exception {
    	Map result = dispatcher.runSync("saveDocItemAction",UtilMisc.toMap("itemMap" ,itemMap));
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
    }

    /**
     * 维护问题步骤
     * @param delegator
     * @param map
     * @throws GenericEntityException
     */
    public static void manageFollowImprove(GenericDelegator delegator, LocalDispatcher dispatcher,Map itemMap)throws Exception {
    	Map result = dispatcher.runSync("saveDocItemImprove",UtilMisc.toMap("itemMap" ,itemMap));
		if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
			throw new GenericServiceException((String) result.get(ModelService.ERROR_MESSAGE));
		}
    }

	/**
	 * 显示设备ID
	 *
	 * @param request
	 * @param response
	 * @return action
	 */
	public static String abnormalDocDefineShow1 (HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try
		{
               	String abnormalDocid = request.getParameter("abnormalDocIndex").toString();
            	GenericValue listDocument = delegator.findByPrimaryKey("AbnormalDocument", UtilMisc.toMap("abnormalDocIndex",abnormalDocid));
    			//
    			String user = CommonUtil.getUserNo(request);

                GenericValue listUser = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));

                String equipmentId = request.getParameter("equipmentId").toString();
                GenericValue listEquipment = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId", equipmentId));

				request.setAttribute("AccountShowList", listUser);
				request.setAttribute("DocumentShowList", listDocument);
				request.setAttribute("EquipmentShowList", listEquipment);
				//
				//
				String eventIndex = abnormalDocid;
				String eventType = String.valueOf(Constants.DOC_ABNORMAL);
				List fileList=new ArrayList();
				Map map=new HashMap();
				map.put("eventIndex", eventIndex);
				map.put("eventType", eventType);
				String uploadIndex=request.getParameter("uploadIndex");
				//从event信息新增页面进行文件管理页面时，eventIndex,uploadIndex都为空
				if(StringUtils.isNotEmpty(eventIndex)){
					fileList=FileUploadHelper.getFileList(delegator, map);
				}else if(StringUtils.isNotEmpty(uploadIndex)){
					//通过uploadIndex开始查询文件
					fileList=FileUploadHelper.getFileList(delegator, uploadIndex);
				}
				// 编辑文件时，第一次打开，没有uploadIndex,所以需要依次取出
				if(StringUtils.isEmpty(uploadIndex)){
					for (int i = 0; i < fileList.size(); i++) {
						GenericValue gv = (GenericValue) fileList.get(i);
						uploadIndex = uploadIndex + gv.getString("uploadIndex") + ";";
					}
				}
				request.setAttribute("FILE_LIST", fileList);
				//
				//
				request.setAttribute("flag", "OK");
				return "success";
         //   }
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
	}

	/**
	 * 保存/更新Document信息
	 * @param delegator
	 * @param param 需要保存的信息
	 */
	public static String saveDocument(HttpServletRequest request, HttpServletResponse response){
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try {
	//		 获得页面上所有的值
	        Map param = BasicEvent.getInitParams(request, false, true);
			GenericValue gv = delegator.makeValidValue("AbnormalDocument", param);

			String flag = request.getParameter("status").toString();
			gv.set("updateTime", new Timestamp(System.currentTimeMillis()));
			gv.set("status", flag);

			//
			String createTime = request.getParameter("create_Time").toString();
			if (!createTime.equals("")){
				gv.set("createTime",  com.csmc.pms.webapp.util.MiscUtils.getGuiDate(createTime));
			}
			String endTime = request.getParameter("end_Time").toString();
			if (!endTime.equals("")){
				gv.set("endTime",  com.csmc.pms.webapp.util.MiscUtils.getGuiDate(endTime));
			}
			//撰写人
			String user = CommonUtil.getUserNo(request);
			gv.set("owner", user);
			gv.set("transBy", user);
			if (flag.equals("1")){
				gv.set("endUser", user);
			}
			delegator.createOrStore(gv);
			request.setAttribute("_EVENT_MESSAGE_", "保存异常报告书成功！");
		} catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
		 return "success";
	}


	/**
	 * 查询设备ID
	 *
	 * @param request
	 * @param response
	 * @return action
	 */
	public static String queryDocumentNo1(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try
		{
			String equipmentId = request.getParameter("equipmentId").toUpperCase().trim();
			String createTime1 = request.getParameter("create_Time1").toString();
			String createTime2 = request.getParameter("create_Time2").toString();
            GenericValue listEquipment = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId", equipmentId));
            if (listEquipment.size() <= 0){
            	Debug.logError(equipmentId + " equipmentId无效！", module);
    			request.setAttribute("_ERROR_MESSAGE_", "equipmentId无效！");
            	return "error";
            }
            else
            {
				String equipmentID = request.getParameter("equipmentId").toUpperCase().trim();
				request.setAttribute("equipmentId", equipmentID);
				request.setAttribute("create_Time1", createTime1);
				request.setAttribute("create_Time2", createTime2);
				EntityWhereString con = new EntityWhereString("to_char(create_Time, 'yyyy-mm-dd') >= '" + createTime1 + "' AND to_char(create_Time, 'yyyy-mm-dd') <= '" + createTime2 + "' AND EQUIPMENT_ID = '" + equipmentID + "'");

	            List list = delegator.findByCondition("AbnormalDocument", con, null, UtilMisc.toList("createTime"));
				request.setAttribute("DocumentList", list);
				request.setAttribute("flag", "OK");
				return "success";
            }
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
	}

	/**
	 * 查询改善类报告书
	 *
	 * @param request
	 * @param response
	 * @return action
	 */
	public static String queryDocumentNo2(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try
		{
			String user = CommonUtil.getUserNo(request);

            GenericValue listUser = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));

            String improveType = request.getParameter("type");
            String flag = request.getParameter("flag");
            request.setAttribute("AccountShowList", listUser);
        	request.setAttribute("type", improveType);
            if (flag.equals("new")){
            	request.setAttribute("flag", "new");
            }
            else
            {
            	request.setAttribute("type", improveType);

				String id = request.getParameter("improveDocIndex");
				//
				//Long id = delegator.getNextSeqId("impDocIndex");
				//GenericValue gv = delegator.makeValue("ImproveDocument", UtilMisc.toMap("impDocIndex", id));
				//delegator.create(gv);
				GenericValue listDocument = delegator.findByPrimaryKey("ImproveDocument", UtilMisc.toMap("impDocIndex",id));

				request.setAttribute("DocumentShowList", listDocument);
				request.setAttribute("flag", "show");
				String eventIndex = id;
				String eventType = String.valueOf(Constants.DOC_IMPROVER);
				List fileList=new ArrayList();
				Map map=new HashMap();
				map.put("eventIndex", eventIndex);
				map.put("eventType", eventType);
				String uploadIndex=request.getParameter("uploadIndex");
				//从event信息新增页面进行文件管理页面时，eventIndex,uploadIndex都为空
				if(StringUtils.isNotEmpty(eventIndex)){
					fileList=FileUploadHelper.getFileList(delegator, map);
				}else if(StringUtils.isNotEmpty(uploadIndex)){
					//通过uploadIndex开始查询文件
					fileList=FileUploadHelper.getFileList(delegator, uploadIndex);
				}
				// 编辑文件时，第一次打开，没有uploadIndex,所以需要依次取出
				if(StringUtils.isEmpty(uploadIndex)){
					for (int i = 0; i < fileList.size(); i++) {
						GenericValue gv = (GenericValue) fileList.get(i);
						uploadIndex = uploadIndex + gv.getString("uploadIndex") + ";";
					}
				}
				request.setAttribute("FILE_LIST", fileList);
            }
            //新增时需要更新upload表信息
			LocalDispatcher dispatcher = CommonUtil.getPmsDispatch(delegator);
			String itemIndex=request.getParameter("itemIndex");
			String uploadIndex=request.getParameter("uploadIndex");
			Map itemMap=new HashMap();
			itemMap.put("itemIndex", itemIndex);
			itemMap.put("uploadIndex", uploadIndex);
			itemMap.put("uploadIndex", uploadIndex);
			manageFollowImprove(delegator, dispatcher,itemMap);
            return "success";
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
	}

	/**
	 * 保存/更新Document信息
	 * @param delegator
	 * @param param 需要保存的信息
	 */
	public static String saveDocument1(HttpServletRequest request, HttpServletResponse response){
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try {
			String id = request.getParameter("impDocIndex");
			String newID = "";
			if (id.equals("")){
				newID = delegator.getNextSeqId("impDocIndex").toString();
				id = newID;
			}
			//	获得页面上所有的值
	     	GenericValue gv = delegator.makeValidValue("ImproveDocument",  UtilMisc.toMap("impDocIndex", id));
	     	gv.set("content", request.getParameter("content"));
	     	gv.set("type", request.getParameter("type"));
			gv.set("updateTime", new Timestamp(System.currentTimeMillis()));
			//
			String user = CommonUtil.getUserNo(request);

			String createTime = request.getParameter("create_Time").toString();
			if (!newID.equals("")){
				gv.set("createTime",  com.csmc.pms.webapp.util.MiscUtils.getGuiDate(createTime));
				gv.set("owner", user);
			}
			//撰写人
			gv.set("transBy", user);
			String member = com.csmc.pms.webapp.common.helper.CommonHelper.getDocumentName(Constants.IMPROVE_DOC_CHAR, request.getParameter("type"), delegator);
			gv.set("docName", member);
			delegator.createOrStore(gv);
			request.setAttribute("_EVENT_MESSAGE_", "保存改善类报告书成功！");
		} catch (Exception e) {
            Debug.logError(e.getMessage(), module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
		 return "success";
	}

	public static String queryDocumentNo3(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try
		{
			String user = CommonUtil.getUserNo(request);
			String type = request.getParameter("type");
			String createTime1 = request.getParameter("create_Time1").toString();
			String createTime2 = request.getParameter("create_Time2").toString();
            request.setAttribute("type", type);
            request.setAttribute("date1", createTime1);
            request.setAttribute("date2", createTime2);
			EntityWhereString con = new EntityWhereString("to_char(create_Time, 'yyyy-mm-dd') >= '" + createTime1 + "' AND to_char(create_Time, 'yyyy-mm-dd') <= '" + createTime2 + "' AND Type = '" + type + "'" + " AND OWNER = '" + user + "'");

            List list = delegator.findByCondition("ImproveDocument", con, null, UtilMisc.toList("createTime"));
			request.setAttribute("DocumentList", list);
			request.setAttribute("flag", "OK");
			return "success";
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
	}
	
	//保养表单超规范数据查询
	public static String queryShowPm(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try{
			String startDate = request.getParameter("startDate").toString();
			String endDate = request.getParameter("endDate").toString();
			String periodSelect = request.getParameter("periodIndex").toString();
			//
			String strEqpId = "";
			String[] arrEqpId = request.getParameterValues("eqpIdSelected");
			
			//没选择设备，则默认查询所有设备列表
            if (arrEqpId == null) {
                arrEqpId = request.getParameterValues("eqpId");
            }
            for (int i = 0; i < arrEqpId.length; i++) {
                if ("".equals(strEqpId)) {
                    strEqpId = "'" + arrEqpId[i] + "'";
                } else {
                    strEqpId = strEqpId + ",'" + arrEqpId[i] + "'";
                }
            }

			String strSQL = "";
			strSQL += "select t1.pm_index, t1.equipment_id, t3.*";
			strSQL += " from pm_form t1, flow_item_points t3";
			strSQL += " WHERE t1.pm_index = t3.form_index";
			strSQL += " AND (REGEXP_SUBSTR(t3.item_value, '^(-|+)?\\d+(\\.\\d+)?$') > t3.item_Upper_Spec OR REGEXP_SUBSTR(t3.item_value, '^(-|+)?\\d+(\\.\\d+)?$') < t3.item_Lower_Spec) ";
			strSQL += " AND t1.equipment_id in (" + strEqpId + ")" ;
			//strSQL += " AND t1.status = 1";
			strSQL += " AND t3.form_type = 'PM'";
			strSQL += " AND t3.item_type = 2 ";//数字型

			if (!periodSelect.equals("")){
				strSQL += " AND t1.period_index = '" + periodSelect + "'";
			}

			// use index update_Time
			strSQL += " AND t3.update_Time >= to_date('" + startDate + "', 'yyyy-mm-dd') AND t3.update_Time < to_date('" + endDate + "', 'yyyy-mm-dd') + 1";

			strSQL += " order by t1.equipment_id, t3.update_Time";

			List list = SQLProcess.excuteSQLQuery(strSQL, delegator);
			request.setAttribute("DocumentList", list);
			request.setAttribute("startDate", startDate);
			request.setAttribute("endDate", endDate);
			request.setAttribute("flag", "OK");
			return "success";
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
	}


	/**
	 * 未填数据查询
	 * @param delegator
	 * @param response
	 */
	public static String queryNotEndDocument(HttpServletRequest request,
			HttpServletResponse response) {
		GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
		try
		{
			String type = request.getParameter("type");
			String createTime1 = request.getParameter("create_Time1").toString();
			String createTime2 = request.getParameter("create_Time2").toString();
			//PM_SQL
			String strSQL_PM = "";
			strSQL_PM += " SELECT DISTINCT ";
			strSQL_PM += "    PM_FORM.PM_INDEX as FORM_INDEX" ;
			strSQL_PM += "   ,PM_FORM.PM_NAME as FORM_NAME" ;
			strSQL_PM += "   ,NVL(PM_FORM.EQUIPMENT_ID,'') EQUIPMENT_ID ";
			strSQL_PM += "   ,NVL(DEFAULT_PERIOD.PERIOD_NAME,'') PERIOD_NAME ";
			strSQL_PM += "   ,NVL(FLOW_JOB.JOB_NAME,'') JOB_NAME ";
			strSQL_PM += "   ,NVL(FORM_JOB_RELATION.SEQ_INDEX,'') SEQ_INDEX ";
			strSQL_PM += "   ,NVL(PM_FORM.END_TIME,sysdate) END_TIME ";
			strSQL_PM += " FROM ";
			strSQL_PM += "   PM_FORM ";
			strSQL_PM += " LEFT OUTER JOIN ";
			strSQL_PM += "   DEFAULT_PERIOD ";
			strSQL_PM += " ON ";
			strSQL_PM += "     DEFAULT_PERIOD.PERIOD_INDEX = PM_FORM.PERIOD_INDEX ";
			strSQL_PM += " LEFT OUTER JOIN ";
			strSQL_PM += "   FORM_JOB_RELATION ";
			strSQL_PM += " ON ";
			strSQL_PM += "     FORM_JOB_RELATION.EVENT_TYPE = 'PM' ";
			strSQL_PM += " AND FORM_JOB_RELATION.EVENT_INDEX = PM_FORM.PM_INDEX ";
			strSQL_PM += " LEFT OUTER JOIN ";
			strSQL_PM += "   FLOW_JOB ";
			strSQL_PM += " ON ";
			strSQL_PM += "     FLOW_JOB.JOB_INDEX = FORM_JOB_RELATION.JOB_INDEX ";
			strSQL_PM += " INNER JOIN ";
			strSQL_PM += "   flow_action_record ";
			strSQL_PM += " ON ";
			strSQL_PM += "     flow_action_record.FORM_JOB_INDEX = FORM_JOB_RELATION.Seq_Index";
			strSQL_PM += " AND flow_action_record.FORM_TYPE = 'PM' ";
			strSQL_PM += " AND flow_action_record.FORM_INDEX = PM_FORM.PM_INDEX ";
			strSQL_PM += " WHERE " ;
			strSQL_PM += "     flow_action_record.IS_EMPTY = 1 ";
			strSQL_PM += " AND flow_job.STATUS = 1 ";
			strSQL_PM += " AND to_char(PM_FORM.create_Time, 'yyyy-mm-dd') >= '" + createTime1 + "' AND to_char(PM_FORM.create_Time, 'yyyy-mm-dd') <= '" + createTime2 + "'";
			//TS_SQL
			String strSQL_TS = "";
			strSQL_TS += " SELECT DISTINCT ";
			strSQL_TS += "    abnormal_form.ABNORMAL_INDEX as FORM_INDEX" ;
			strSQL_TS += "   ,abnormal_form.ABNORMAL_NAME as FORM_NAME" ;
			strSQL_TS += "   ,NVL(abnormal_form.EQUIPMENT_ID,'') EQUIPMENT_ID ";
			strSQL_TS += "   ,'' PERIOD_NAME ";
			strSQL_TS += "   ,NVL(FLOW_JOB.JOB_NAME,'') JOB_NAME ";
			strSQL_TS += "   ,NVL(FORM_JOB_RELATION.SEQ_INDEX,'') SEQ_INDEX ";
			strSQL_TS += "   ,NVL(abnormal_form.END_TIME,sysdate) END_TIME ";
			strSQL_TS += " FROM ";
			strSQL_TS += "   abnormal_form ";
			strSQL_TS += " LEFT OUTER JOIN ";
			strSQL_TS += "   FORM_JOB_RELATION ";
			strSQL_TS += " ON ";
			strSQL_TS += "     FORM_JOB_RELATION.EVENT_TYPE = 'TS' ";
			strSQL_TS += " AND FORM_JOB_RELATION.EVENT_INDEX = abnormal_form.ABNORMAL_INDEX ";
			strSQL_TS += " LEFT OUTER JOIN ";
			strSQL_TS += "   FLOW_JOB ";
			strSQL_TS += " ON ";
			strSQL_TS += "     FLOW_JOB.JOB_INDEX = FORM_JOB_RELATION.JOB_INDEX ";
			strSQL_TS += " INNER JOIN ";
			strSQL_TS += "   flow_action_record ";
			strSQL_TS += " ON ";
			strSQL_TS += "     flow_action_record.FORM_JOB_INDEX = FORM_JOB_RELATION.Seq_Index";
			strSQL_TS += " AND flow_action_record.FORM_TYPE = 'TS' ";
			strSQL_TS += " AND flow_action_record.FORM_INDEX = abnormal_form.ABNORMAL_INDEX ";
			strSQL_TS += " WHERE " ;
			strSQL_TS += "     flow_action_record.IS_EMPTY = 1 ";
			strSQL_TS += " AND flow_job.STATUS = 1 ";
			strSQL_TS += " AND to_char(abnormal_form.create_Time, 'yyyy-mm-dd') >= '" + createTime1 + "' AND to_char(abnormal_form.create_Time, 'yyyy-mm-dd') <= '" + createTime2 + "'";
			//PC_SQL
			String strSQL_PC = "";
			strSQL_PC += " SELECT DISTINCT ";
			strSQL_PC += "    PC_FORM.PC_INDEX as FORM_INDEX" ;
			strSQL_PC += "   ,PC_FORM.PC_NAME as FORM_NAME" ;
			strSQL_PC += "   ,NVL(pc_style.name,'') EQUIPMENT_ID ";
			strSQL_PC += "   ,NVL(DEFAULT_PERIOD.PERIOD_NAME,'') PERIOD_NAME ";
			strSQL_PC += "   ,NVL(FLOW_JOB.JOB_NAME,'') JOB_NAME ";
			strSQL_PC += "   ,NVL(FORM_JOB_RELATION.SEQ_INDEX,'') SEQ_INDEX ";
			strSQL_PC += "   ,NVL(PC_FORM.END_TIME,sysdate) END_TIME ";
			strSQL_PC += " FROM ";
			strSQL_PC += "   PC_FORM ";
			strSQL_PC += " LEFT OUTER JOIN ";
			strSQL_PC += "   DEFAULT_PERIOD ";
			strSQL_PC += " ON ";
			strSQL_PC += "     DEFAULT_PERIOD.PERIOD_INDEX = PC_FORM.PERIOD_INDEX ";
			strSQL_PC += " LEFT OUTER JOIN ";
			strSQL_PC += "   FORM_JOB_RELATION ";
			strSQL_PC += " ON ";
			strSQL_PC += "     FORM_JOB_RELATION.EVENT_TYPE = 'PC' ";
			strSQL_PC += " AND FORM_JOB_RELATION.EVENT_INDEX = PC_FORM.PC_INDEX ";
			strSQL_PC += " LEFT OUTER JOIN ";
			strSQL_PC += "   FLOW_JOB ";
			strSQL_PC += " ON ";
			strSQL_PC += "     FLOW_JOB.JOB_INDEX = FORM_JOB_RELATION.JOB_INDEX ";
			strSQL_PC += " left outer join pc_style on PC_FORM.Style_Index = pc_style.style_index ";
			strSQL_PC += " INNER JOIN ";
			strSQL_PC += "   flow_action_record ";
			strSQL_PC += " ON ";
			strSQL_PC += "     flow_action_record.FORM_JOB_INDEX = FORM_JOB_RELATION.Seq_Index";
			strSQL_PC += " AND flow_action_record.FORM_TYPE = 'PC' ";
			strSQL_PC += " AND flow_action_record.FORM_INDEX = PC_FORM.PC_INDEX ";
			strSQL_PC += " WHERE " ;
			strSQL_PC += "     flow_action_record.IS_EMPTY = 1 ";
			strSQL_PC += " AND flow_job.STATUS = 1 ";
			strSQL_PC += " AND to_char(PC_FORM.create_Time, 'yyyy-mm-dd') >= '" + createTime1 + "' AND to_char(PC_FORM.create_Time, 'yyyy-mm-dd') <= '" + createTime2 + "'";
			//
			String strSQL ;
			if (type.equals("PM")){
				strSQL = strSQL_PM;
			}else if (type.equals("TS")){
				strSQL = strSQL_TS;
			}else{
				strSQL = strSQL_PC;
			}
			List list = SQLProcess.excuteSQLQuery(strSQL, delegator);
			//
            request.setAttribute("type", type);
            request.setAttribute("date1", createTime1);
            request.setAttribute("date2", createTime2);
            request.setAttribute("DocumentList", list);
			request.setAttribute("flag", "OK");
			return "success";
		} catch (Exception e) {
			Debug.logError(e.getMessage(), module);
			request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
			return "error";
		}
	}
}
