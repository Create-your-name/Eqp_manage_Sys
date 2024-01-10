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

    // ---------------------------------------------Ѳ����ʽshaoaj--------------------------------------
    /**
     * ���ݲ�ѯ��������Ѳ����ʽ��ѯ
     * 
     * @param delegator
     * @return �豸ID�б�
     */
    public static List getPcStyleListByCondition(GenericDelegator delegator) throws Exception {
        List list = delegator.findAll("PcStyle");
        return list;
    }

    /**
     * ����/����Ѳ����ʽ��Ϣ
     * 
     * @param delegator
     * @param param
     *            ��Ҫ�������Ϣ
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
     * ɾ��Ѳ����ʽ��Ϣ
     * 
     * @param delegator
     * @param value
     * @throws GenericEntityException
     */
    public static void deletePcStyle(GenericDelegator delegator, Integer value) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("PcStyle", UtilMisc.toMap("styleIndex", value));
        delegator.removeValue(gv);
    }

    // ---------------------------------------------��ʱ/�쳣�������趨shaoaj--------------------------------------
    /**
     * �����豸���༰�쳣�����ȡ��Ϣ�б�
     * @param delegator
     * @param equipmentType �豸����
     * @param reasonType �쳣����
     * @return
     * @throws Exception �쳣
     */
    public static List getReasonList(GenericDelegator delegator, String equipmentType, String reasonType) throws Exception {
        List list = delegator.findByAnd("PmsReason", UtilMisc.toMap("equipmentType", equipmentType, "reasonType", reasonType));
        return list;
    }

    /**
     * ����/����(��ʱ/�쳣)��������Ϣ
     * @param delegator
     * @param param
     * ��Ҫ�������Ϣ
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
     * ����reasonIndexɾ����Ϣreason
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

    // ---------------------------------------------�����ڱ��������趨shaoaj--------------------------------------
    /**
     * �õ����в����ڱ���������Ϣ�б�
     * 
     * @param delegator
     * @return
     */
    public static List queryUnscheduleParameterList(GenericDelegator delegator) throws Exception {
        List list = delegator.findAll("UnscheduleParameter");
        return list;
    }

    /**
     * ����/���²����ڱ���������Ϣ
     * 
     * @param delegator
     * @param param
     *            ��Ҫ�������Ϣ
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
     * ɾ�������ڱ���������Ϣ
     * 
     * @param delegator
     * @param value
     * @throws GenericEntityException
     */
    public static void deleteUnscheduleParameter(GenericDelegator delegator, Integer value) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("UnscheduleParameter", UtilMisc.toMap("paramIndex", value));
        delegator.removeValue(gv);
    }

    // ---------------------------------------------��ϴ��������shaoaj--------------------------------------
    /**
     * �õ�������ϴ����������Ϣ�б�
     * 
     * @param delegator
     * @return
     */
    public static List queryEquipmentVendorList(GenericDelegator delegator) throws Exception {
        List list = delegator.findByAnd("EquipmentVendor", UtilMisc.toMap("type", Constants.CLEAN));
        return list;
    }

    /**
     * ����/������ϴ����������Ϣ
     * 
     * @param delegator
     * @param param
     *            ��Ҫ�������Ϣ
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
     * ɾ����ϴ����������Ϣ
     * 
     * @param delegator
     * @param value
     * @throws GenericEntityException
     */
    public static void deleteEquipmentVendorByPk(GenericDelegator delegator, Integer value) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("EquipmentVendor", UtilMisc.toMap("seqIndex", value));
        delegator.removeValue(gv);
    }

    // ---------------------------------------------�豸�ͺ�ά��shaoaj--------------------------------------
    /**
     * �õ������豸�ͺ���Ϣ�б�
     * 
     * @param delegator
     * @return
     */
    public static List queryEquipmentBasicList(GenericDelegator delegator) throws Exception {
        List list = delegator.findByAnd("EquipmentBasicData", UtilMisc.toMap("type", Constants.MODEL));
        return list;
    }

    /**
     * ����/�����豸�ͺ���Ϣ
     * 
     * @param delegator
     * @param param
     *            ��Ҫ�������Ϣ
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
     * ɾ���豸�ͺ���Ϣ
     * 
     * @param delegator
     * @param value
     * @throws GenericEntityException
     */
    public static void deleteEquipmentBasicByPk(GenericDelegator delegator, Integer value) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("EquipmentBasicData", UtilMisc.toMap("seqIndex", value));
        delegator.removeValue(gv);
    }

    // ---------------------------------------------�¼�����shaoaj--------------------------------------
    /**
     * ��ѯ�¼������б�
     * 
     * @param delegator
     * @return �豸ID�б�
     */
    public static List getEventCategoryList(GenericDelegator delegator) throws Exception {
        List list = delegator.findAll("EventCategory");
        return list;
    }

    /**
     * ����/�����¼�����
     * 
     * @param delegator
     * @param param
     *            ��Ҫ�������Ϣ
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
     * ɾ���¼�������Ϣ
     * 
     * @param delegator
     * @param value
     * @throws GenericEntityException
     */
    public static void deleteEventCategoryByIndex(GenericDelegator delegator, Integer value) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("EventCategory", UtilMisc.toMap("eventIndex", value));
        delegator.removeValue(gv);
    }

    // ---------------------------------------------�¼�ϸ��shaoaj--------------------------------------
    /**
     * ��ѯ�¼�ϸ���б�
     * 
     * @param delegator
     * @param eventIndex
     *            �¼�����
     * @return �豸ID�б�
     */
    public static List getEventSubCategoryListByIndex(GenericDelegator delegator, String eventIndex) throws Exception {
        List list = delegator.findByAnd("EventSubcategory", UtilMisc.toMap("eventIndex", eventIndex));
        return list;
    }

    /**
     * ����/�����¼�ϸ��
     * 
     * @param delegator
     * @param param
     *            ��Ҫ�������Ϣ
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
     * ɾ���¼�ϸ����Ϣ
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
     * ���ݴ��ݵĲ���������½��豸��̨�������������ļ�¼д����ʷ��
     * @param delegator
     * @param paramMap ��̨����
     * @param user �û�
     * @throws Exception
     */
    public static void createEqpParam(GenericDelegator delegator, Map paramMap, String user) throws Exception {
        // ������½����棬ȡ��seqIndex(��sql�Զ�����)         
        if (StringUtils.isEmpty((String) paramMap.get("seqIndex"))) {
        	Long id = delegator.getNextSeqId("unscheduleEqpParamseqIndex");
            paramMap.put("seqIndex", id);
        }
        
        // ������ڴ˻�̨��Ϣ����ȡ����ֵ�������½���ֵ
        GenericValue gv = delegator.makeValidValue("UnscheduleEqpParam", paramMap);
        gv.put("updateTime", new Timestamp(System.currentTimeMillis()));
        delegator.createOrStore(gv);

        // д����ʷ��
        String event = (String) paramMap.get("event");
        if (StringUtils.isEmpty(event)) {
        	event = "update";
        }
        
        GenericValue histgv = delegator.findByPrimaryKey("UnscheduleEqpParam", UtilMisc.toMap("seqIndex", gv.getString("seqIndex")));
        BasicHelper.manageEquipmentParamHist(delegator, histgv, user, event);
    }

    /**
     * ����seqIndexɾ����̨�������������ļ�¼д����ʷ��
     * 
     * @param delegator
     * @param seqIndex
     *            ����
     * @param user
     *            �û�
     * @throws Exception
     */
    public static void delEqpParam(GenericDelegator delegator, String seqIndex, String user) throws Exception {
        GenericValue gv = delegator.findByPrimaryKey("UnscheduleEqpParam", UtilMisc.toMap("seqIndex", seqIndex));
        // д����ʷ��
        String event = "delete";
        BasicHelper.manageEquipmentParamHist(delegator, gv, user, event);

        // ɾ����̨����
        delegator.removeByAnd("UnscheduleEqpParam", UtilMisc.toMap("seqIndex", seqIndex));
    }

    /**
     * ����̨������Ϣ���޸ĺ�ɾ����������ʷ��
     * @param delegator
     * @param gv :GenericValue ����ʷ�����Ϣ
     * @param request
     * @param event
     * @throws Exception
     */
    public static void manageEquipmentParamHist(GenericDelegator delegator, GenericValue gv, String user, String event) throws Exception {
        // ��ʷ���GenericValue
        GenericValue hgv = delegator.makeValidValue("UnscheduleEqpParamHist", gv);

        // ����ʷ��д��Ϣ
        hgv.put("event", event);
        hgv.put("transBy", user);
        hgv.put("updateTime", new Timestamp(System.currentTimeMillis()));
        
        Long id = delegator.getNextSeqId("unscheduleEqpParamHistIndex");
        hgv.put("histIndex", id);

        delegator.create(hgv);
    }
}
