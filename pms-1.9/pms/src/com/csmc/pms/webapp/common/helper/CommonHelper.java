package com.csmc.pms.webapp.common.helper;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ModelService;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.eqp.helper.FabAdapter;
import com.csmc.pms.webapp.eqp.helper.GuiHelper;
import com.csmc.pms.webapp.form.event.TsFormEvent;
import com.csmc.pms.webapp.form.help.TsHelper;
import com.csmc.pms.webapp.util.Constants;

public class CommonHelper {
	public static final String module = CommonHelper.class.getName();
	
	/**
	 * 获得表单名
	 * @param type(PM-保养，PC-巡检，TS-异常)
	 * @param name(设备ID, 部门)
	 * @param delegator
	 * @return
	 */
	public static String getFormName(String type, String typeName, GenericDelegator delegator) {
		try {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			if(Constants.PM_CHAR.equals(type)) {
				Long s = delegator.getNextSeqId("pmFormIndex");
				return Constants.PM_CHAR + year + "-" + typeName.toUpperCase() + "-" + s.toString();
			} else if(Constants.PC_CHAR.equals(type)) {
				Long s = delegator.getNextSeqId("pcFormIndex");
				return Constants.PC_CHAR + year + "-" + typeName.toUpperCase() + "-" + s.toString();
			} else if(Constants.TS_CHAR.equals(type)) {
				Long s = delegator.getNextSeqId("tsFormIndex");
				return Constants.TS_CHAR + year + "-" + typeName.toUpperCase() + "-" + s.toString();
			}
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
		}
		return null;
	}
	 
	/**
	 * 获得文档名
	 * @param type(异常书，改善书)
	 * @param typeName
	 * @param delegator
	 * @return
	 */
	public static String getDocumentName(String type, String typeName, GenericDelegator delegator) {
		try {
			int year = Calendar.getInstance().get(Calendar.YEAR);
			if(Constants.ABNORMAL_DOC_CHAR.equals(type)) {
				Long s = delegator.getNextSeqId("tsDocIndex");
				return Constants.ABNORMAL_DOC_CHAR + year + "-" + typeName.toUpperCase() + "-" + s.toString();
			} else if(Constants.IMPROVE_DOC_CHAR.equals(type)) {
				Long s = delegator.getNextSeqId("impDocIndex");
				return Constants.IMPROVE_DOC_CHAR + year + "-" + typeName.toUpperCase() + "-" + s.toString();
			}
		} catch(Exception e) {
			Debug.logError(e.getMessage(), module);
		}
		
		return null;
	}
	
    /**
	  * 保存更换设备状态的历史记录
	  * @param delegator
	  * @param map
	  * @throws GenericEntityException
	  */
	public static void insertEqpChgstatusHist(GenericDelegator delegator,Map map) throws GenericEntityException{
		GenericValue gv=delegator.makeValidValue("EqpChgstatusHist", map);
		Long id=delegator.getNextSeqId("eqpChgstatusHistSeqIndex");
		gv.put("seqIndex", id);
		delegator.create(gv);
	}
	
    /**
     * POST Form Attribute
     * @param attributes
     * @return
     */
    public static StringBuffer getFormAttributes(Map attributes) {
        StringBuffer sb = new StringBuffer();
        
        Iterator it = attributes.keySet().iterator();
        while(it.hasNext()) {
            Object key = it.next();
            Object value = attributes.get(key);
            sb.append(key + "=" + value);
            if(it.hasNext()) sb.append("&");
        }
        
        return sb;
    }
    
    /**
     * Call Http Post 
     * @param su
     * @param attributes
     * @return
     * @throws ParserException
     */
    public static Parser callHttpPost(String su, Map attributes) throws ParserException {
        Parser parser;
        URL url;
        HttpURLConnection connection;
        PrintWriter out;

        try {
            url = new URL(su);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Referer", su);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            
            StringBuffer buffer = getFormAttributes(attributes);
            out = new PrintWriter(connection.getOutputStream());
            out.print(buffer);
            out.close();
            
            parser = new Parser(connection);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParserException("You must be offline!", e);
        }

        return parser;
    }

	/**
	 * 表单中修改设备状态
	 * @param delegator
	 * @param userLogin
	 * @param formType: PM, TS
	 * @param formIndex
	 * @param statusMap(eqpId, newstatus, comment)
	 * @return
	 */
	public static JSONObject updateN05EqpStatus(GenericDelegator delegator, GenericValue userLogin, String formType, String formIndex, Map statusMap) {
		JSONObject result = new JSONObject();		
		String eqpId = (String) statusMap.get("eqpid");
		String newstatus = (String) statusMap.get("newstatus");
		
		try {			
			Map retQuery = FabAdapter.runCallService(delegator, userLogin,
					UtilMisc.toMap("eqpid", eqpId), com.csmc.pms.webapp.util.Constants.EQP_INFO_QUERY);
			String eqpStatus = (String) retQuery.get("status");
	
			if (TsHelper.isPRStartStatus(eqpStatus)) {
				// 05限制更改设备状态
				result.put("status", "error");
				result.put("message", eqpStatus + "限制更改设备状态，请完成异常表单!");
				
			} else if (eqpStatus.substring(0, 2).equalsIgnoreCase(newstatus.substring(0, 2))){
				// 限制只能修改设备状态04到04，或03到03
				Map ret = FabAdapter.runCallService(delegator, userLogin,
						statusMap, com.csmc.pms.webapp.util.Constants.EQP_STATUS_CHANGE);
				
				if (ModelService.RESPOND_ERROR.equals(ret.get(ModelService.RESPONSE_MESSAGE))) {
					result.put("status", "error");
					result.put("message", (String) ret.get(ModelService.ERROR_MESSAGE));					
				} else {
					Map histMap = new HashMap();
					histMap.put("newStatus", newstatus);
					histMap.put("formType", formType);
					histMap.put("formIndex",formIndex);
					histMap.put("transBy",userLogin.getString("userLoginId"));
					histMap.put("updateTime", UtilDateTime.nowTimestamp());
					insertEqpChgstatusHist(delegator, histMap);
					
					result.put("status", "success");
					result.put("message", "设备状态已成功修改为:" + newstatus);
					Debug.logInfo("表单中更改更改设备状态----设备:" + eqpId + "修改MES设备状态" + newstatus, TsFormEvent.module);	
					//added by steven 设备状态更改成功，触发QC
					GuiHelper.triggerQc(eqpId,eqpStatus,newstatus,userLogin);
					//end
				}
				
			} else {
				result.put("status", "error");
				result.put("message", eqpStatus + " 限制更改设备状态为 " + newstatus);
			}
			
		} catch (Exception e) {
			result.put("status", "error");
			result.put("message", "更改设备状态出错,请联系管理员!");			
			Debug.logError(e.toString() + eqpId + " formType: " + formType, module);
		}
		
		return result;
	}

	/**
	 * 得到设备大类列表
	 * @param delegator
	 * @return 
	 */
	public static List getEquipmentTypeList(GenericDelegator delegator) throws Exception {
	    List list = delegator.findAll("EquipmentType", UtilMisc.toList("equipmentType"));
	    return list;
	}
	
	// 取得PartType列表
	public static List getPartTypeList(GenericDelegator delegator) throws Exception {
		String sql = "select * from part_type";
		List partTypeList = SQLProcess.excuteSQLQuery(sql, delegator);
		return partTypeList;
	}
}
