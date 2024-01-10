package com.csmc.pms.webapp.basic.help;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.csmc.pms.webapp.util.Constants;

public class BasicHelper {

    public static final String module = BasicHelper.class.getName();

    // ---------------------------------------------巡检样式shaoaj--------------------------------------
    /**
     * 根据查询条件进行巡检样式查询
     * 
     * @param delegator
     * @return 设备ID列表
     */
    public static List getPcStyleListByCondition(GenericDelegator delegator) throws Exception {
        List list = delegator.findAll("PcStyle");
        return list;
    }

    /**
     * 保存/更新巡检样式信息
     * 
     * @param delegator
     * @param param
     *            需要保存的信息
     */
    public static void managePcStyle(GenericDelegator delegator, Map param) throws GenericEntityException {
        GenericValue gv = delegator.makeValidValue("PcStyle", param);
        Long id = null;
        if (StringUtils.isEmpty((String) param.get("styleIndex"))) {
            id = delegator.getNextSeqId("pcStyleIndex");
            gv.put("styleIndex", id);
        }
        delegator.createOrStore(gv);
    }

    /**
     * 删除巡检样式信息
     * 
     * @param delegator
     * @param value
     * @throws GenericEntityException
     */
    public static void deletePcStyle(GenericDelegator delegator, Integer value) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("PcStyle", UtilMisc.toMap("styleIndex", value));
        delegator.removeValue(gv);
    }

    // ---------------------------------------------超时/异常分类码设定shaoaj--------------------------------------
    /**
     * 根据设备大类及异常分类获取信息列表
     * @param delegator
     * @param equipmentType 设备大类
     * @param reasonType 异常分类
     * @return
     * @throws Exception 异常
     */
    public static List getReasonList(GenericDelegator delegator, String equipmentType, String reasonType) throws Exception {
        List list = delegator.findByAnd("PmsReason", UtilMisc.toMap("equipmentType", equipmentType, "reasonType", reasonType));
        return list;
    }

    /**
     * 保存/更新(超时/异常)分类码信息
     * @param delegator
     * @param param
     * 需要保存的信息
     */
    public static void manageReason(GenericDelegator delegator, Map param) throws GenericEntityException {
        GenericValue gv = delegator.makeValidValue("PmsReason", param);
        Long id = null;
        if (StringUtils.isEmpty((String) param.get("reasonIndex"))) {
            id = delegator.getNextSeqId("pmsReasonIndex");
            gv.put("reasonIndex", id);
        }
        delegator.createOrStore(gv);
    }

    /**
     * 根据reasonIndex删除信息reason
     * 
     * @param delegator
     * @param value
     *            reasonIndex
     * @throws GenericEntityException
     */
    public static void deleteReasonByPk(GenericDelegator delegator, String value) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("PmsReason", UtilMisc.toMap("reasonIndex", value));
        delegator.removeValue(gv);
    }

    // ---------------------------------------------不定期保养参数设定shaoaj--------------------------------------
    /**
     * 得到所有不定期保养参数信息列表
     * 
     * @param delegator
     * @return
     */
    public static List queryUnscheduleParameterList(GenericDelegator delegator) throws Exception {
        List list = delegator.findAll("UnscheduleParameter");
        return list;
    }

    /**
     * 保存/更新不定期保养参数信息
     * 
     * @param delegator
     * @param param
     *            需要保存的信息
     */
    public static void manageUnscheduleParameter(GenericDelegator delegator, Map param) throws GenericEntityException {
        GenericValue gv = delegator.makeValidValue("UnscheduleParameter", param);
        Long id = null;
        if (StringUtils.isEmpty((String) param.get("paramIndex"))) {
            id = delegator.getNextSeqId("paramIndex");
            gv.put("paramIndex", id);
        }
        delegator.createOrStore(gv);
    }

    /**
     * 删除不定期保养参数信息
     * 
     * @param delegator
     * @param value
     * @throws GenericEntityException
     */
    public static void deleteUnscheduleParameter(GenericDelegator delegator, Integer value) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("UnscheduleParameter", UtilMisc.toMap("paramIndex", value));
        delegator.removeValue(gv);
    }

    // ---------------------------------------------清洗备件厂商shaoaj--------------------------------------
    /**
     * 得到所有清洗备件厂商信息列表
     * 
     * @param delegator
     * @return
     */
    public static List queryEquipmentVendorList(GenericDelegator delegator) throws Exception {
        List list = delegator.findByAnd("EquipmentVendor", UtilMisc.toMap("type", Constants.CLEAN));
        return list;
    }

    /**
     * 保存/更新清洗备件厂商信息
     * 
     * @param delegator
     * @param param
     *            需要保存的信息
     */
    public static void manageEquipmentVendor(GenericDelegator delegator, Map param) throws GenericEntityException {
        GenericValue gv = delegator.makeValidValue("EquipmentVendor", param);
        Long id = null;
        if (StringUtils.isEmpty((String) param.get("seqIndex"))) {
            id = delegator.getNextSeqId("equipmentVendorSeqIndex");
            gv.put("seqIndex", id);
        }
        delegator.createOrStore(gv);
    }

    /**
     * 删除清洗备件厂商信息
     * 
     * @param delegator
     * @param value
     * @throws GenericEntityException
     */
    public static void deleteEquipmentVendorByPk(GenericDelegator delegator, Integer value) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("EquipmentVendor", UtilMisc.toMap("seqIndex", value));
        delegator.removeValue(gv);
    }

    // ---------------------------------------------设备型号维护shaoaj--------------------------------------
    /**
     * 得到所有设备型号信息列表
     * 
     * @param delegator
     * @return
     */
    public static List queryEquipmentBasicList(GenericDelegator delegator) throws Exception {
        List list = delegator.findByAnd("EquipmentBasicData", UtilMisc.toMap("type", Constants.MODEL));
        return list;
    }

    /**
     * 保存/更新设备型号信息
     * 
     * @param delegator
     * @param param
     *            需要保存的信息
     */
    public static void manageEquipmentBasic(GenericDelegator delegator, Map param) throws GenericEntityException {
        GenericValue gv = delegator.makeValidValue("EquipmentBasicData", param);
        Long id = null;
        if (StringUtils.isEmpty((String) param.get("seqIndex"))) {
            id = delegator.getNextSeqId("equipmentBasicDataSeqIndex");
            gv.put("seqIndex", id);
        }
        delegator.createOrStore(gv);
    }

    /**
     * 删除设备型号信息
     * 
     * @param delegator
     * @param value
     * @throws GenericEntityException
     */
    public static void deleteEquipmentBasicByPk(GenericDelegator delegator, Integer value) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("EquipmentBasicData", UtilMisc.toMap("seqIndex", value));
        delegator.removeValue(gv);
    }

    // ---------------------------------------------事件分类shaoaj--------------------------------------
    /**
     * 查询事件分类列表
     * 
     * @param delegator
     * @return 设备ID列表
     */
    public static List getEventCategoryList(GenericDelegator delegator) throws Exception {
        List list = delegator.findAll("EventCategory");
        return list;
    }

    /**
     * 保存/更新事件分类
     * 
     * @param delegator
     * @param param
     *            需要保存的信息
     */
    public static void manageEventCategory(GenericDelegator delegator, Map param) throws GenericEntityException {
        GenericValue gv = delegator.makeValidValue("EventCategory", param);
        Long id = null;
        if (StringUtils.isEmpty((String) param.get("eventIndex"))) {
            id = delegator.getNextSeqId("eventCategoryIndex");
            gv.put("eventIndex", id);
        }
        delegator.createOrStore(gv);
    }

    /**
     * 删除事件分类信息
     * 
     * @param delegator
     * @param value
     * @throws GenericEntityException
     */
    public static void deleteEventCategoryByIndex(GenericDelegator delegator, Integer value) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("EventCategory", UtilMisc.toMap("eventIndex", value));
        delegator.removeValue(gv);
    }

    // ---------------------------------------------事件细项shaoaj--------------------------------------
    /**
     * 查询事件细项列表
     * 
     * @param delegator
     * @param eventIndex
     *            事件主键
     * @return 设备ID列表
     */
    public static List getEventSubCategoryListByIndex(GenericDelegator delegator, String eventIndex) throws Exception {
        List list = delegator.findByAnd("EventSubcategory", UtilMisc.toMap("eventIndex", eventIndex));
        return list;
    }

    /**
     * 保存/更新事件细项
     * 
     * @param delegator
     * @param param
     *            需要保存的信息
     */
    public static void manageEventSubCategory(GenericDelegator delegator, Map param) throws GenericEntityException {
        GenericValue gv = delegator.makeValidValue("EventSubcategory", param);
        Long id = null;
        if (StringUtils.isEmpty((String) param.get("eventSubIndex"))) {
            id = delegator.getNextSeqId("eventSubCategoryIndex");
            gv.put("eventSubIndex", id);
        }
        delegator.createOrStore(gv);
    }

    /**
     * 删除事件细项信息
     * 
     * @param delegator
     * @param value
     * @throws GenericEntityException
     */
    public static void deleteEventSubCategoryByIndex(GenericDelegator delegator, Integer value) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("EventSubcategory", UtilMisc.toMap("eventSubIndex", value));
        delegator.removeValue(gv);
    }

    /**
     * 根据传递的参数保存或新建设备机台参数，并将更改记录写入历史表
     * @param delegator
     * @param paramMap 机台参数
     * @param user 用户
     * @throws Exception
     */
    public static void createEqpParam(GenericDelegator delegator, Map paramMap, String user) throws Exception {
        // 如果是新建画面，取得seqIndex(非sql自动生成)         
        if (StringUtils.isEmpty((String) paramMap.get("seqIndex"))) {
        	Long id = delegator.getNextSeqId("unscheduleEqpParamseqIndex");
            paramMap.put("seqIndex", id);
        }
        
        // 如果存在此机台信息，则取出赋值，否则新建赋值
        GenericValue gv = delegator.makeValidValue("UnscheduleEqpParam", paramMap);
        gv.put("updateTime", new Timestamp(System.currentTimeMillis()));
        delegator.createOrStore(gv);

        // 写入历史表
        String event = (String) paramMap.get("event");
        if (StringUtils.isEmpty(event)) {
        	event = "update";
        }
        
        GenericValue histgv = delegator.findByPrimaryKey("UnscheduleEqpParam", UtilMisc.toMap("seqIndex", gv.getString("seqIndex")));
        BasicHelper.manageEquipmentParamHist(delegator, histgv, user, event);
    }

    /**
     * 根据seqIndex删除机台参数，并将更改记录写入历史表
     * 
     * @param delegator
     * @param seqIndex
     *            主键
     * @param user
     *            用户
     * @throws Exception
     */
    public static void delEqpParam(GenericDelegator delegator, String seqIndex, String user) throws Exception {
        GenericValue gv = delegator.findByPrimaryKey("UnscheduleEqpParam", UtilMisc.toMap("seqIndex", seqIndex));
        // 写入历史表
        String event = "delete";
        BasicHelper.manageEquipmentParamHist(delegator, gv, user, event);

        // 删除机台参数
        delegator.removeByAnd("UnscheduleEqpParam", UtilMisc.toMap("seqIndex", seqIndex));
    }

    /**
     * 将机台参数信息的修改和删除都存入历史表
     * @param delegator
     * @param gv :GenericValue 非历史表的信息
     * @param request
     * @param event
     * @throws Exception
     */
    public static void manageEquipmentParamHist(GenericDelegator delegator, GenericValue gv, String user, String event) throws Exception {
        // 历史表的GenericValue
        GenericValue hgv = delegator.makeValidValue("UnscheduleEqpParamHist", gv);

        // 往历史表写信息
        hgv.put("event", event);
        hgv.put("transBy", user);
        hgv.put("updateTime", new Timestamp(System.currentTimeMillis()));
        
        Long id = delegator.getNextSeqId("unscheduleEqpParamHistIndex");
        hgv.put("histIndex", id);

        delegator.create(hgv);
    }
}
