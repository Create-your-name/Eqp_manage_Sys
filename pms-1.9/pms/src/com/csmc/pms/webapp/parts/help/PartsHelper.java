package com.csmc.pms.webapp.parts.help;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityWhereString;

import com.csmc.mcs.webapp.util.ConstantsMcs;
import com.csmc.pms.webapp.common.helper.AccountHelper;
import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;

public class PartsHelper {
	public static final String module = PartsHelper.class.getName();

//	-----------------------------------------保养物料设定--------------------------------------
    /**
     * 得到设备大类列表
     *
     * @param menuGroup
     * @param delegator
     * @return
     */
    public static List getEquipMentList(GenericDelegator delegator) throws Exception {
        List list = delegator.findAllCache("EquipmentType");
        return list;
    }
	/**
	 * 查询登陆人的科别
	 * @param delegator
	 * @return 科别列表
	 */
	public static List getAccountSection(GenericDelegator delegator,String accountNo)throws Exception{
		List list=delegator.findByAnd("Account", UtilMisc.toMap("accountNo",accountNo));
		return list;
	}

	/**
	 * 查询物料号列表
	 * @param delegator
	 * @return 物料号列表
	 */
	public static List getPartsNoList(GenericDelegator delegator,Map parMap)throws Exception{
	    /* 得到页面参数值 */
        String category = (String) parMap.get("category");
        String partName = (String) parMap.get("partName");
        String partNo = (String) parMap.get("partNo");

        StringBuffer sb = new StringBuffer();
        sb.append("select * from parts_data t where t.category = '").append(category).append("'");
        if (StringUtils.isNotEmpty(partNo)) {
            sb.append(" and t.part_no like '%").append(partNo).append("%'");
        }
        if (StringUtils.isNotEmpty(partName)) {
            sb.append(" and t.part_name like '%").append(partName).append("%'");
        }

        sb.append(" and t.part_no not in (select part_no from parts_filter)");
        sb.append(" order by t.part_no");
        List list = SQLProcess.excuteSQLQuery(sb.toString(), delegator);
 		return list;
	}

 	/**
	 * 查询本部门线边仓库存物料号列表
	 * @param delegator
	 * @return 物料号列表
	 */
	public static List getMcsPartsNoList(GenericDelegator delegator,Map parMap)throws Exception{
		/* 得到页面参数值 */
		String deptIndex = (String) parMap.get("deptIndex");
		String category = (String) parMap.get("category");
		String partNo = (String) parMap.get("partNo");
//        String jobIndex = (String) parMap.get("jobIndex");
//        String ifkey = (String) parMap.get("ifkey");
		String eqpId = (String) parMap.get("eqpId");
		String plant = Constants.CALL_TP_FLAG ? "1100" : "1000";

		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT k.*, kp.key_parts_id, case when kp.key_parts_id > 0 then 'Y' else 'N' end ifkey, trunc(sysdate - k.doc_time) delay_days, b.moving_average_price, d.part_name MTR_DESC,");
		sb.append(" '' vendor, '' parts_type, '' create_time, '' series_no, '' base_sn, '' update_time");
		sb.append(" FROM (");
		sb.append(" select material_sto_req_index req_index, mtr_num, batch_num, mtr_grp, plant, dept_index, doc_time, recipient, qty - NVL(active_qty, 0) qty ");
		sb.append(" FROM mcs_material_sto_req WHERE dept_index = '").append(deptIndex).append("'");
		if (StringUtils.isNotEmpty(partNo)) {
			sb.append(" AND mtr_num like '%").append(partNo).append("%'");
		}
		sb.append(" and mtr_grp = '").append(category).append("'");
		sb.append(" and mtr_num not in (select part_no from parts_filter) and mtr_num not in (select parts_id from key_eqp_parts where enable = 'Y')) k,");
		sb.append(" mcs_sap_mtr_table b, parts_data d,");
		sb.append(" (SELECT * FROM KEY_EQP_PARTS  WHERE PARTS_ID like '%");
		if (StringUtils.isNotEmpty(partNo)) {
			sb.append(partNo);
		}
		sb.append("%'");
		sb.append(" AND EQP_TYPE IN(SELECT MODEL FROM EQUIPMENT WHERE EQUIPMENT_ID='").append(eqpId).append("')) KP");
//		if (StringUtils.isNotEmpty(ifkey)) {
//			sb.append(" ,equipment eq ");
//		}
		sb.append(" WHERE k.qty > 0 and k.mtr_num = b.mtr_num(+) and k.plant = b.plant(+) and k.mtr_num = d.part_no");
		sb.append(" AND k.mtr_num = kp.parts_id(+)");
//		if (StringUtils.isNotEmpty(ifkey)) {
//			sb.append(" and eq.model=kp.eqp_type ");
//			sb.append(" and kp.key_parts_id IS  not  NULL");
//			sb.append(" and  eq.equipment_id='").append(eqpId).append("'");
//		} else {
//			sb.append(" and kp.key_parts_id IS    NULL");
//		}
		sb.append(" and ((k.plant='").append(plant).append("'");
		sb.append(" and b.mtr_grp = '").append(ConstantsMcs.SPAREPART_2T).append("'").append(" or b. moving_average_price  > 200 )");
		sb.append(" or  k.plant = '1501')");
		sb.append(" ORDER BY delay_days desc");

		List list = SQLProcess.excuteSQLQuery(sb.toString(), delegator);
		return list;
	}

	/**
	 * 保存/更新低价物料信息
	 * @param delegator
	 * @param param 需要保存的信息
	 */
	public static void addFilterParts(GenericDelegator delegator, Map param)throws GenericEntityException{
	    GenericValue gv = delegator.makeValidValue("PartsFilter", param);
	    delegator.createOrStore(gv);
	}
	/**
     * 根据partNo删除低价物料信息
     * @param delegator
     * @param value
     * @throws GenericEntityException
     */
    public static void deleteFilterPartsByPk(GenericDelegator delegator, String partNo)
            throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("PartsFilter", UtilMisc.toMap(
                "partNo", partNo));
        delegator.removeValue(gv);
    }

	//-----------------------------------------低价物料过滤设定--------------------------------------
	/**
     * 得到低价物料列表
     *
     * @param menuGroup
     * @param delegator
     * @return
     */
    public static List getFilterPartsList(GenericDelegator delegator,Map paraMap) throws Exception {
        String deptIndex = (String) paraMap.get("equipmentDept");
        List list = null;
        if(StringUtils.isNotEmpty(deptIndex)){
            list = delegator.findByAnd("PartsFilter",UtilMisc.toMap("deptIndex", deptIndex),UtilMisc.toList("createTime DESC"));
        }
        else{
            list = delegator.findAllCache("PartsFilter");
        }
        return list;
    }

    /**
     * 通过保养得到处理流程
     *
     * @param menuGroup
     * @param periodIndex
     * @return 保养种处理流程列表
     * @author qinchao
     */
    public static List getFlowList(GenericDelegator delegator,
	    String periodIndex) throws Exception {
	List list = delegator.findByAnd("FlowJob", UtilMisc.toMap(
		"eventObject", periodIndex, "eventType", Constants.PM));
	return list;
    }

    /**
     * 查询保养物料模板
     * @param delegator
     * @param parMap
     * @author qinchao
     * @return
     * @throws Exception
     */
    public static List getPmPartsTemplateList(GenericDelegator delegator, Map parMap) throws Exception {
		String equipmentType = (String) parMap.get("equipmentType");
		String period = (String) parMap.get("period");
		String flow = (String) parMap.get("flow");
		StringBuffer sb = new StringBuffer();
		sb.append("select t1.PART_PM_INDEX,t1.EQP_ID,t1.PERIOD_INDEX,t1.PART_NO,t1.PART_NAME,t1.PART_COUNT,t1.TEMPLATE_COUNT,t1.MTR_GRP,t3.PART_TYPE,t3.id,t1.REMARK,t2.period_name ");
		sb.append(" from default_period t2, parts_pm t1");
		sb.append(" left join part_type t3 ON t3.id = t1.part_type");
		sb.append(" where t1.period_index=t2.period_index");
		if (StringUtils.isNotEmpty(equipmentType)) {
		    sb.append(" and t1.eqp_type='").append(equipmentType).append("'");
		}
		if (StringUtils.isNotEmpty("period")) {
		    sb.append(" and t1.period_index='").append(period).append("'");
		}
		if (StringUtils.isNotEmpty(flow)) {
		    sb.append(" and t1.flow_index='").append(flow).append("'");
		}

		sb.append(" order by t1.PART_NO");
		List list = SQLProcess.excuteSQLQuery(sb.toString(), delegator);
		return list;
    }

	/**
	 * 根据partStyle，region查询物料列表
	 * @param delegator
	 * @return 物料号列表
	 */
	public static List getRecylePartsList(GenericDelegator delegator,String partStyle,String region)throws Exception{
		List orderBy=new ArrayList();
		orderBy.add("updateTime");
		List list=delegator.findByAnd("Parts", UtilMisc.toMap("partStyle",partStyle,"region",region), orderBy);
		return list;
	}

	/**
	 * 保存/更新物料信息
	 * @param delegator
	 * @param param 需要保存的信息
	 */
	public static void manageRecyleParts(GenericDelegator delegator, Map param)throws GenericEntityException{
		GenericValue gv = delegator.makeValidValue("Parts", param);
		delegator.createOrStore(gv);
	}

	/**
	 * 根据partNo删除物料信息
	 * @param delegator
	 * @param value
	 * @throws GenericEntityException
	 */
	public static void deleteRecylePartsByPk(GenericDelegator delegator, String partNo)
			throws GenericEntityException {
		GenericValue gv = delegator.findByPrimaryKey("Parts", UtilMisc.toMap("partNo", partNo));
		delegator.removeValue(gv);
	}

	//	-----------------------------------------保养物料设定--------------------------------------
    /**
     * 通过设备大类得到eqpid
     *
     * @param menuGroup
     * @param delegator
     * @return 设备ID列表
     */
    public static List getEquipIDList(GenericDelegator delegator,String equipmentType) throws Exception {
        List list = delegator.findByAndCache("Equipment",UtilMisc.toMap("equipmentType", equipmentType),UtilMisc.toList("equipmentId"));
        return list;
    }

    /**
     * 通过设备大类得到保养种类
     * @param menuGroup
     * @param delegator
     * @return 保养种类列表
     */
    public static List getPeriodList(GenericDelegator delegator,String equipmentType) throws Exception {
        List list = delegator.findByAnd("DefaultPeriod", UtilMisc.toMap("eqpType", equipmentType,"event",Constants.PM), UtilMisc.toList("defaultDays", "periodName"));
        return list;
    }

    /**
     * 根据查询条件查询保养物料
     * @param delegator
     * @param parMap
     * @return
     * @throws Exception
     */
    public static List getPmPartsList(GenericDelegator delegator,Map parMap) throws Exception {
    	String equipmentType=(String)parMap.get("equipmentType");
    	String equipmentId=(String)parMap.get("equipmentId");
    	String period=(String)parMap.get("period");
    	StringBuffer sb=new StringBuffer();
    	sb.append("select t1.PART_PM_INDEX,t1.EQP_ID,t1.PERIOD_INDEX,t1.PART_NO,t1.PART_NAME,t1.PART_COUNT,t3.period_name from parts_pm t1,equipment t2,default_period t3");
    	sb.append(" where t2.equipment_id = t1.eqp_id and   t1.period_index=t3.period_index");
    	if(StringUtils.isNotEmpty(equipmentType)){
    		sb.append(" and t2.equipment_type='").append(equipmentType).append("'");
    	}
    	if(StringUtils.isNotEmpty("equipmentId")){
    		sb.append(" and t1.eqp_id='").append(equipmentId).append("'");
    	}
    	if(StringUtils.isNotEmpty("period")){
    		sb.append(" and t1.period_index='").append(period).append("'");
    	}
    	List list = SQLProcess.excuteSQLQuery(sb.toString(), delegator);
    	return list;
    }

    /**
     * 保存/更新保养物料信息
     *
     * @param delegator
     * @param param
     *            需要保存的信息
     */
    public static void managePmParts(GenericDelegator delegator, Map param) throws GenericEntityException {
        GenericValue gv = delegator.makeValidValue("PartsPm", param);
        Long id = null;
        if (StringUtils.isEmpty((String) param.get("partPmIndex"))) {
            id = delegator.getNextSeqId("partPmIndex");
            gv.put("partPmIndex", id);
        }
        delegator.createOrStore(gv);
    }

    /**
     * 删除保养物料信息
     *
     * @param delegator
     * @param value
     * @throws GenericEntityException
     */
    public static void deletePmPartsByPk(GenericDelegator delegator, String id) throws GenericEntityException {
        GenericValue gv = delegator.findByPrimaryKey("PartsPm", UtilMisc.toMap("partPmIndex", id));
        delegator.removeValue(gv);
    }

    //--------------------------------------------表单parts设定用方法--------------------------------

    /**
     * 通过表单INDEX及表单类型获取物料信息
     * @param delegator
     * @param eventIndex
     * @param eventType
     * @return
     * @throws GenericEntityException
     */
    public static List getPartsList(GenericDelegator delegator,String eventIndex,String eventType) throws GenericEntityException {
    	List orderBy=new ArrayList();
		orderBy.add("seqIndex");
    	return delegator.findByAnd("PartsUse", UtilMisc.toMap("eventIndex", eventIndex,"eventType",eventType),orderBy);
    }

    /**
     * 表单-物料设定-物料查询
     * @param delegator
     * @param parMap
     * @return
     * @throws GenericEntityException
     */
    public static List queryPartsListInForm(GenericDelegator delegator, Map parMap) throws GenericEntityException {
    	String partType = (String) parMap.get("partType");
		String partNo = (String) parMap.get("partNo");
		String queryType = (String) parMap.get("queryType");

		String partNoStr = "";
		if (StringUtils.isNotEmpty(partNo)) {
			partNoStr = " and PART_NO like '" + partNo.toUpperCase() + "%'";
		}

    	List resultList = null;
		if ("PM".equals(partType)) {
			String eqpId = (String) parMap.get("eqpId");
			// PM周期
			String periodIndex = (String) parMap.get("periodIndex");
			StringBuffer sb = new StringBuffer();
			sb.append(" EQP_ID='").append(eqpId).append("'");
			sb.append(" and PERIOD_INDEX='").append(periodIndex).append("'");
			sb.append(partNoStr);
			EntityWhereString con = new EntityWhereString(sb.toString());
			resultList = delegator.findByCondition("PartsPm", con, null, null);

		} else if ("RECYCLE".equals(partType) || "OTHER".equals(partType)) {
			String pregion = (String) parMap.get("pregion");
			StringBuffer sb = new StringBuffer();
			sb.append(" PART_STYLE='").append(partType).append("'");
			if (StringUtils.isNotEmpty(pregion)) {
				sb.append(" and REGION='").append(pregion).append("'");
			}
			sb.append(partNoStr);
			EntityWhereString con = new EntityWhereString(sb.toString());
			resultList = delegator.findByCondition("Parts", con, null, null);

		} else if ("DATA".equals(partType)) {

			if (StringUtils.isNotEmpty(partNo)) {
				// 输入料号,查询所有库存料号PartsData
				StringBuffer sb = new StringBuffer();
				sb.append(" 1=1").append(partNoStr);
				EntityWhereString con = new EntityWhereString(sb.toString());
				resultList = delegator.findByCondition("PartsData", con, null, null);
			} else {
				// 不输入料号，按设备大类或部门查询已领可用物料parts_require
				String sql = "select t1.seq_index, t1.part_no, t1.part_name, t1.require_time,"
					+ "   t1.part_count - NVL(t2.use_part_count, 0) avail_part_count"
					+ " from parts_require t1,"
					+ "     (select parts_require_index, sum(part_count) use_part_count from parts_use"
					+ "      group by parts_require_index) t2"
					+ " where t1.seq_index = t2.parts_require_index(+)"
					+ "   and t1.part_count > NVL(t2.use_part_count, 0)";

				if (Constants.EQUIPMENT_TYPE.equalsIgnoreCase(queryType)) {
					sql = sql + " and t1.equipment_id in (select t3.equipment_id from equipment t3 where t3.equipment_type = '" + (String) parMap.get("equipmentType") + "')";
				} else if (Constants.DEPT.equalsIgnoreCase(queryType)) {
					sql = sql + " and t1.dept_index = " + (String) parMap.get("deptIndex");
				}

				sql = sql + " and t1.require_time >= trunc(sysdate) - 3";

				sql = sql + " order by t1.require_time";

		    	try {
					resultList = SQLProcess.excuteSQLQuery(sql, delegator);
				} catch (SQLProcessException e) {
					Debug.logError(e.getMessage(), module);
				}
			}
		}

		return resultList;
    }

    /**
	 * 更新PARTS
	 *
	 * @param delegator
	 * @param parMap
	 * @throws GenericEntityException
	 */
    public static void updatePartsUse(GenericDelegator delegator, Map parMap) throws GenericEntityException {
    	GenericValue gv = delegator.makeValue("PartsUse", parMap);
        delegator.store(gv);
//        String keyPartUseId = (String) parMap.get("keyuseid");
//        if (StringUtils.isNotEmpty(keyPartUseId)) {
//        	gv = delegator.findByPrimaryKey("KeyPartsUse", UtilMisc.toMap("keyUseId", keyPartUseId));
//        	gv.put("", value)
//        }
    }

	/**
	 * 保养表单 保存parts
	 *
	 * @param delegator
	 * @param parMap
	 * @throws GenericEntityException
	 */
	public static void savePartsUseInfo(GenericDelegator delegator, Map parMap, StringBuffer rSB)
			throws GenericEntityException {

		String eventType = (String) parMap.get("eventType");
		String status = (String) parMap.get("status");
		String transBy = (String) parMap.get("transBy");
		Timestamp updateTime = UtilDateTime.nowTimestamp();
		String eventIndex = (String) parMap.get("eventIndex");
		String periodIndex = (String) parMap.get("periodIndex");
		String deptIndex = (String) parMap.get("deptIndex");
		String eqpId = (String) parMap.get("eqpId");
		String flowIndex = (String) parMap.get("flowIndex");

		List mapList = new ArrayList();// for save db
		List keyuseidList = new ArrayList();// 存储已勾选的keyuseid
		List mapList_key = new ArrayList();
		List list = new ArrayList();// for query mcs sto

		for (Iterator it = parMap.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			if (!key.startsWith("parts_")) {
				continue;
			}
			String orderNum = key.substring(key.indexOf("_") + 1);// 获得行号
			String partsUseIndex = (String) parMap.get("partsuseid_" + orderNum);
			String newpartstype = (String) parMap.get("newparts_type" + orderNum); // 关键备件的类别（NEW、OLD之类的）
			if (StringUtils.isEmpty(newpartstype)) {
				newpartstype = "NEW";
			}

			String keyuseid = (String) parMap.get("keyuseid_" + orderNum);
			String keyUseIdUsed = (String) parMap.get("keyUseIdUsed_" + orderNum);
			String partsNo = (String) parMap.get(key);
			String partsName = (String) parMap.get("partsName_" + orderNum);
			String partCount = (String) parMap.get("useNum_" + orderNum); // 关键备件使用数量
			String mtrGrp = (String) parMap.get("keyPartsMtrGrp_" + orderNum);
			String reqIndex = (String) parMap.get("mcsid_" + orderNum); // 领用信息
			if (partCount == null) {
				partCount = "1";
			}
			if (mtrGrp == null || mtrGrp.equals("null")) {
				mtrGrp = (String) parMap.get("type");
			}
			String useNum = (String) parMap.get("useNum_" + orderNum); // 关键备件里设置的使用数量
			if (useNum == null) {
				useNum = "1";
			}
			String remark = (String) parMap.get("remark_" + orderNum);
			String seriesno = (String) parMap.get("seriesno_" + orderNum);
			String baseno = (String) parMap.get("baseno_" + orderNum);
			String keypartsid = (String) parMap.get("keypartsid_" + orderNum);
			String keydesc = (String) parMap.get("keydesc_" + orderNum);
			String initlife = (String) parMap.get("initlife_" + orderNum);
			String offreasion = (String) parMap.get("offline_" + orderNum);
			String delaytime = (String) parMap.get("delaytime_" + orderNum);
			String createTime = (String) parMap.get("createTime_" + orderNum); // createTimeNew->updateTime??
			String eqpModel = (String) parMap.get("eqpModel_" + orderNum);
			String keyPartsCleanId = (String) parMap.get("keyPartsCleanId_"+orderNum);
			String vendor = (String) parMap.get("vendor_"+orderNum);
			String isClean = (String)parMap.get("isClean_"+orderNum);
			if ("Y".equals(isClean)) {
				List KeyEqpPartsCleanList=delegator.findByAnd("KeyEqpPartsClean", UtilMisc.toMap("keyPartsId", keypartsid, "seriesNo", seriesno,"enable","Y"));
				if(KeyEqpPartsCleanList!=null && ! KeyEqpPartsCleanList.isEmpty()){
					keyPartsCleanId = ((Map)KeyEqpPartsCleanList.get(0)).get("keyPartsCleanId").toString();
				}
			}

			if (delaytime == null || delaytime.equals("")) {
				delaytime = "0";
			}
			if (flowIndex == null || flowIndex.equals("null")) {
				flowIndex = (String) parMap.get("flow_" + orderNum);
			}

			Map partsUseMap = new HashMap();
			if(createTime==null||createTime.equals("null")){
				partsUseMap.put("updateTime", updateTime);
            }else{
            	try {
            		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            		Calendar cal = Calendar.getInstance();
					cal.setTime(sdf.parse(createTime));
					long time1 = cal.getTimeInMillis();
					Timestamp timestamp = new Timestamp(time1);
					partsUseMap.put("updateTime", timestamp);
				} catch (Exception e) {
					Debug.logError(module, e.getMessage());
				}

            }
			// 已有 PARTS_USE_INDEX
			if (StringUtils.isNotEmpty(partsUseIndex) && !partsUseIndex.equals("null")) {
				GenericValue gv = delegator.findByPrimaryKey("PartsUse", UtilMisc.toMap("seqIndex", partsUseIndex));
				gv.put("partType", newpartstype);
				gv.put("remark", remark);
				gv.store();

			} else {
				// 新建 PARTS_USE 记录
				Long partsUseSeqIndex = delegator.getNextSeqId("partsUseSeqIndex");
				partsUseIndex = partsUseSeqIndex.toString();
				partsUseMap.put("seqIndex", partsUseSeqIndex);
				partsUseMap.put("mtrGrp", mtrGrp);
				partsUseMap.put("partNo", partsNo);
				partsUseMap.put("partName", partsName);
				partsUseMap.put("partType", newpartstype);
				partsUseMap.put("partCount", partCount);
				partsUseMap.put("eventIndex", eventIndex);
				partsUseMap.put("periodIndex", periodIndex);
				partsUseMap.put("flowIndex", flowIndex);
				partsUseMap.put("eventType", eventType);
				partsUseMap.put("deptIndex", deptIndex);
				partsUseMap.put("transBy", transBy);
				partsUseMap.put("remark", remark);
				partsUseMap.put("partsRequireIndex", reqIndex);

				GenericValue gv = delegator.makeValidValue("PartsUse", partsUseMap);
				gv.create();
			}

			// delay时只改变remark,delaytime
			if (newpartstype.equals("DELAY")) {
				if (keyuseid.equals("0")) {
					keyuseid = keyUseIdUsed;
				}
				StringBuffer sql = new StringBuffer("");
				sql.append(" update key_parts_use t1 SET t1.remark='").append(remark)
						.append("',t1.delaytime='").append(delaytime).append("',t1.parts_use_id='")
						.append(partsUseIndex).append("'").append(" ,t1.update_time=SYSDATE");
				sql.append(",t1.parts_type='").append(newpartstype).append("'");
				sql.append(" where key_use_id='").append(keyuseid).append("'");
				try {
					SQLProcess.excuteSQLUpdateForQc(sql.toString(), delegator);
				} catch (Exception e) {
					Debug.logError(module, e.getMessage());
				}

			} else {
				// 更新 KEY_PARTS_USE
				if (StringUtils.isNotEmpty(keypartsid)) {
					// 先把同一个eqpid,同一个关键字，USING，改成 OFFLINE
					StringBuffer sql = new StringBuffer("");
					sql.append(" update key_parts_use t1");
					sql.append(" SET t1.UPDATE_TIME=SYSDATE, t1.status='OFFLINE', t1.off_line='")
							.append(offreasion).append("'");
					sql.append(" where exists (select t1.* from parts_use t2,key_eqp_parts t3 where t1.parts_use_id =t2.seq_index and t1.key_parts_id=t3.key_parts_id ");
					sql.append(" and t2.event_index <> '").append(eventIndex)
							.append("' and t1.eqp_id = '").append(eqpId).append("' ");
					sql.append(" and t1.status='USING' and t3.keydesc='").append(keydesc).append("' ) ");
					try {

						SQLProcess.excuteSQLUpdateForQc(sql.toString(), delegator);
					} catch (Exception e) {
						Debug.logError(module, e.getMessage());
					}

					// 更新 KEY_PARTS_USE 记录
					Map keyPartsUseMap = new HashMap();
					if (StringUtils.isEmpty(keyuseid) || keyuseid.equals("0")) {
						keyuseid = delegator.getNextSeqId("KeyPartsUseKeyUseId").toString();
						keyPartsUseMap.put("keyPartsId", keypartsid);
						keyPartsUseMap.put("partsUseId", partsUseIndex);
						keyPartsUseMap.put("createTime", updateTime);
						keyPartsUseMap.put("createUser", transBy);
						keyPartsUseMap.put("eqpModel", eqpModel);
					}

					keyPartsUseMap.put("eqpId", eqpId);
					keyPartsUseMap.put("keyUseId", keyuseid);
					keyPartsUseMap.put("partsType", newpartstype);
					keyPartsUseMap.put("seriesNo", seriesno);
					keyPartsUseMap.put("baseSn", baseno);
					keyPartsUseMap.put("initLife", Long.valueOf(initlife));
					keyPartsUseMap.put("delaytime", Long.valueOf(delaytime));
					keyPartsUseMap.put("offLine", offreasion);
					keyPartsUseMap.put("remark", remark);
					keyPartsUseMap.put("status", "USING");
					keyPartsUseMap.put("updateTime", updateTime);
					keyPartsUseMap.put("updateUser", transBy);
					keyPartsUseMap.put("keyPartsCleanId", keyPartsCleanId);
					keyPartsUseMap.put("vendor", vendor);

					GenericValue gv = delegator.makeValidValue("KeyPartsUse", keyPartsUseMap);
					delegator.createOrStore(gv);

				}
			}
		}
	}

    /**
	 * 根据主键查询PARTS信息
	 *
	 * @param request
	 * @param response
	 */
    public static void queryPartsBySeqIndex(HttpServletRequest request, HttpServletResponse response) {
        GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
        String id = request.getParameter("seqIndex");
        try {
            GenericValue gv = delegator.findByPrimaryKey("PartsUse", UtilMisc.toMap("seqIndex", id));
            JSONObject parts = new JSONObject();
            parts.put("partNo", gv.getString("partNo"));
            parts.put("partName", gv.getString("partName"));
            parts.put("partCount", gv.getString("partCount"));
            parts.put("remark", gv.getString("remark"));
            parts.put("partType", gv.getString("partType"));
            response.getWriter().write(parts.toString());
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
    }

    /**
     * 根据主键删除物料信息
     * @param delegator
     * @param seqIndex
     * @throws GenericEntityException
     */
    public static void deletePartsUse(GenericDelegator delegator, String seqIndex) throws GenericEntityException {
    	GenericValue gv=delegator.findByPrimaryKey("PartsUse", UtilMisc.toMap("seqIndex", seqIndex));
    	delegator.removeValue(gv);

    	// TODO MCS
    	//delegator.removeByAnd("McsPartsUse", UtilMisc.toMap("partsUseSeqIndex", seqIndex));
    }

	/**
	 * 根据msc_group, partno,partname 查询相关parts
	 *
	 * @param delegator
	 * @return 物料号列表
	 */
	public static List getMcsPartsList(GenericDelegator delegator, Map parMap) throws Exception {
		/* 得到页面参数值 */
		String category = (String) parMap.get("category");
		String deptIndex = (String) parMap.get("deptIndex");
		String partNo = (String) parMap.get("partNo");
		StringBuffer sb = new StringBuffer();

		sb.append(" select part_no, part_name from parts_data");
		sb.append(" left join mcs_material_info t ON t.mtr_num = part_no");
		sb.append(" where t.enabled=1 and t.in_control=1"); // and t.dept_index ='").append(deptIndex).append("'");
		sb.append(" and category='").append(category).append("'");
		if (StringUtils.isNotEmpty(partNo)) {
			sb.append(" and part_no like '%").append(partNo).append("%'");
		}
		sb.append(" ORDER BY  part_no desc");

		List list = SQLProcess.excuteSQLQuery(sb.toString(), delegator);
		return list;
	}

	public static void saveKeyPartsInfo(GenericDelegator delegator, Map parMap, StringBuffer rSB)
			throws GenericEntityException {
		String transBy = (String) parMap.get("transBy");
		String maintDept = (String) parMap.get("maintDept");
		List mapList = new ArrayList();// for save db
		List mapList_key = new ArrayList();
		List list = new ArrayList();// for query mcs sto
		for (Iterator it = parMap.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			if (key.startsWith("parts_")) {
				// 获得序号
				String orderNum = key.substring(key.indexOf("_") + 1);// 获得index
				String partsNo = (String) parMap.get(key);
				String partsName = (String) parMap.get("partsName_" + orderNum);
				String eqpType = (String) parMap.get("eqp_model" + orderNum);
				String keydesc = (String) parMap.get("keydesc_" + orderNum);
				String partsNum = (String) parMap.get("parts_" + orderNum);
				String remark = (String) parMap.get("remark_" + orderNum);
				String errorline = (String) parMap.get("error_line" + orderNum);
				String warmline = (String) parMap.get("warm_line" + orderNum);
				String limitType = (String) parMap.get("limit_type" + orderNum);
				String ifalarm = (String) parMap.get("ifalarm_" + orderNum);
				String section = (String) parMap.get("section_" + orderNum);
				String useNumber = (String) parMap.get("use_number_" + orderNum);
				String mustchange = (String) parMap.get("mustchange_" + orderNum);
				String enable = (String) parMap.get("enable_" + orderNum);
				list = delegator.findByAnd("KeyEqpParts",
						UtilMisc.toMap("keydesc", keydesc, "eqpType", eqpType, "enable", "Y"));

				if (list.size() != 0) {
					if (rSB.length() > 0) {
						rSB.append(partsNo + "\n");
					} else {
						rSB.append("以下eqpModel中已有此关键字，请重新确定关键字，保存失败！:\n");
						rSB.append(partsNo + "\n");
					}
					continue;
				}
				// 有重复物料就不保存
				if (rSB.length() == 0) {
					Map map = new HashMap();
					map.put("createUser", transBy);
					map.put("updateTime", new Timestamp(System.currentTimeMillis()));
					map.put("updateUser", transBy);
					map.put("createTime", new Timestamp(System.currentTimeMillis()));
					map.put("partsId", partsNum);
					map.put("partsName", partsName);
					map.put("remark", remark);
					map.put("eqpType", eqpType);
					map.put("errorSpec", errorline);
					map.put("warnSpec", warmline);
					map.put("limitType", limitType);
					map.put("isAlarm", ifalarm);
					map.put("keydesc", keydesc);
					map.put("notify", section);
					map.put("maintDept", maintDept);
					map.put("useNumber", useNumber);
					map.put("mustchange", mustchange);
					map.put("enable", enable);
					mapList.add(map);

				}
			}
		}
		// 有重复物料就不保存
		if (rSB.length() == 0) {
			for (Iterator it = mapList.iterator(); it.hasNext();) {
				Map map = (Map) it.next();
				GenericValue gv = delegator.makeValidValue("KeyEqpParts", map);
				Long id = delegator.getNextSeqId("keyEqpPartsKeyPartsId");
				gv.put("keyPartsId", id);
				delegator.create(gv);
				gv = delegator.makeValidValue("KeyEqpPartsHist", map);
				gv.put("keyPartsId", id);
				gv.put("action", "create");
				Long histId = delegator.getNextSeqId("keyEqpPartsHistId");
				gv.put("histId", histId);
				delegator.create(gv);
			}
		}
	}

	public static void updateKeyPartsInfo(GenericDelegator delegator, Map paramMap)
			throws GenericEntityException {
		GenericValue gv = delegator.makeValidValue("KeyEqpParts", paramMap);
		delegator.createOrStore(gv);

		// 更新hist
		gv = delegator.findByPrimaryKey("KeyEqpParts", UtilMisc.toMap("keyPartsId", gv.get("keyPartsId")));
		paramMap = gv.getAllFields();
		Long histId = delegator.getNextSeqId("keyEqpPartsHistId");
		paramMap.put("histId", histId);
		paramMap.put("action", "modify");
		gv = delegator.makeValidValue("KeyEqpPartsHist", paramMap);
		delegator.create(gv);

		// 邮件通知相关组成员，spec被修改！
		// 获取邮件地址，
//		if (gv.getString("notify") != null) {
//			GenericValue sectionEntity = delegator.findByPrimaryKey("EquipmentSection",
//					UtilMisc.toMap("sectionIndex", gv.getString("notify")));
//			String mail = sectionEntity.getString("mail");
//			String body = "<br/>您好！ <br/> eqpModel:" + eqpModel + ",关键备件：" + keyDesc + "，料号："
//					+ gv.getString("partsId") + "，名称：" + gv.getString("partsName") + "，寿命类型："
//					+ gv.getString("limitType") + "，已被修改。";
//			body += "<br/>最新使用寿命：" + errorspec + "，预警寿命：" + warmspec + "，使用数量：" + usenumber + "，是否必换：" + mustchange
//					+ "，是否报警：" + ifalarm + "，是否启用：" + enable + "。 <br/>请知悉！";
//			try {
////				MiscUtils.sendMail(mail, "JIANGTAO153@csmc.crmicro.com;YANGHX@csmc.crmicro.com;", "",
////						"PMS关键备件寿命修改通知!", null, null, body.toString(), null);
////				Log.logDebug("send alarm mail success.");
//			} catch (Exception e) {
////				Log.logDebug("send alarm mail fail.");
//			}
//
//		}
	}

	/**
	 * 获得技改备件最大序号
	 *
	 * @param mtrNumTemp
	 * @param delegator
	 * @return
	 */
	public static String getMtrNumIndex(GenericDelegator delegator, String mtrNumTemp) {
		try {
			String sql = "select distinct MAX(mtr_num) from MCS_MATERIAL_STO_REQ t where mtr_num like '" + mtrNumTemp
					+ "%'";
			List list = SQLProcess.excuteSQLQuery(sql, delegator);
			Map map = (Map) list.get(0);
			return (String) map.get("MAX(MTR_NUM)");

		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
		}
		return null;
	}

	/**
	 * 插入parts_data表
	 *
	 * @param mtrNumIndex
	 * @param mtrDesc
	 * @param userNo
	 * @param delegator
	 * @return
	 */
	public static void insertPartsData(GenericDelegator delegator, String mtrNumIndex, String mtrDesc, String userNo) {
		Map parmMap = new HashMap();
		parmMap.put("partNo", mtrNumIndex);
		parmMap.put("partName", mtrDesc);
		parmMap.put("category", ConstantsMcs.SPAREPART_2T);
		parmMap.put("updateTime", UtilDateTime.nowTimestamp());
		parmMap.put("transBy", userNo);

		try {
			GenericValue gv = delegator.makeValidValue("PartsData", parmMap);
			gv.create();
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
		}
		return;
	}

	/**
	 * 更新parts_data表mtrDesc字段
	 *
	 * @param partNo
	 * @param partName
	 * @param userNo
	 * @param delegator
	 * @return
	 */
	public static void updatePartsData(GenericDelegator delegator, String partNo, String partName, String userNo) {
		try {
			GenericValue gv = delegator.findByPrimaryKey("PartsData", UtilMisc.toMap("partNo", partNo));
			if (gv != null) {
				gv.put("partName", partName);
				gv.put("updateTime", UtilDateTime.nowTimestamp());
				gv.put("transBy", userNo);
				gv.store();
			}
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
		}
		return;
	}

	/**
	 * 删除parts_data表记录
	 *
	 * @param mtrNum
	 * @param delegator
	 * @return
	 */
	public static String deletePartsData(GenericDelegator delegator, String mtrNum) {
		try {
			delegator.removeByAnd("PartsData", UtilMisc.toMap("partNo", mtrNum));
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
		}
		return null;
	}

	/**
	 * 根据传递的参数保存或新建设备保养类型
	 *
	 * @param delegator
	 * @param paramMap
	 *            技改备件设定
	 * @throws Exception
	 */
	public static void createDefaultTechnicalSpare(GenericDelegator delegator, Map paramMap) throws Exception {
		// 如果是新建画面，取得McsMaterialStoReqSeqIndex(非sql自动生成)
		Long id = null;
		if (StringUtils.isEmpty((String) paramMap.get("materialStoReqIndex"))) {
			// id = delegator.getNextSeqId("McsMaterialStoReqSeqIndex");
			id = Long.parseLong(getMaxMaterialIndex(delegator));
			paramMap.put("materialStoReqIndex", id);
			paramMap.put("mtrGrp", "20002T");
			paramMap.put("batchNum", "00000000");
		}
		// 如果存在此机台信息，则取出赋值，否则新建赋值
		GenericValue gv = delegator.makeValidValue("McsMaterialStoReq", paramMap);
		delegator.createOrStore(gv);
	}

	/*
	  *    SELECT MAX(material_sto_req_index) FROM mcs_material_sto_req
	  * */

	public static String getMaxMaterialIndex(GenericDelegator delegator) {
		try {
			String sql = "SELECT MAX(material_sto_req_index)+1 num FROM mcs_material_sto_req  ";
			List list = SQLProcess.excuteSQLQuery(sql, delegator);
			Map map = (Map) list.get(0);
			return (String) map.get("NUM").toString();
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
		}

		return null;
	}

	/**
	 * 根据技改备件Index获得MtrNum
	 *
	 * @param materialStoReqIndex
	 * @param delegator
	 * @return
	 */
	public static String getMtrNumByMaterialStoReqIndex(GenericDelegator delegator, String materialStoReqIndex) {
		try {
			String sql = "select distinct MTR_NUM from MCS_MATERIAL_STO_REQ t where MATERIAL_STO_REQ_INDEX = '"
					+ materialStoReqIndex + "'";
			List list = SQLProcess.excuteSQLQuery(sql, delegator);
			Map map = (Map) list.get(0);
			return (String) map.get("MTR_NUM");

		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
		}
		return null;
	}

	public static List getEquipIDListbyModel(GenericDelegator delegator, String model) throws Exception {
		List list = delegator.findByAndCache("Equipment", UtilMisc.toMap("model", model),
				UtilMisc.toList("equipmentId"));
		return list;
	}

	public static List getkeydescListbyModel(GenericDelegator delegator, String model) throws Exception {
		String sql = "select distinct keydesc from key_eqp_parts t where t.eqp_type='" + model + "'";
		List list = SQLProcess.excuteSQLQuery(sql.toString(), delegator);
		return list;
	}

	public static long getDelayLife(GenericDelegator delegator, String keyUseId) throws Exception {
		long DelayLife = 0;
		String sql = "select NVL(sum(delay_life),0) delay_life from key_parts_delay_info where key_use_id=" + keyUseId;
		List list = SQLProcess.excuteSQLQuery(sql, delegator);
		for (int k = 0; k < list.size(); k++) {
			Map dLifeMap = (Map) list.get(k);
			String sDelayLife = (String) dLifeMap.get("DELAY_LIFE");
			DelayLife = Long.parseLong(sDelayLife);
		}
		return DelayLife;
	}

	/**
	 * 查询本部门线边仓库存物料号列表 --另一种写法
	 *
	 * @param delegator
	 * @return 物料号列表 by dingyuyan
	 */
	public static List getMcsPartsNoList_other(GenericDelegator delegator, String eqpId, String Index, String deptIndex)
			throws Exception {
		/* 得到页面参数值 */
		StringBuffer sb = new StringBuffer();
		sb.append(" ");
		sb.append(" select td.delaytime,t2.use_number,t2.mustchange,t2.warn_spec,t2.error_spec,t1.parts_use_id,NVL(t1.key_use_id, 0) key_use_id,");
	    sb.append(" NVL(t6.key_use_id_used, 0)key_use_id_used,case when t1.parts_type='NEW' then t4.part_count else '0' end used_number, ");
	    sb.append(" case when tdr.delay_table_id is null then 0 else 1 end isdelayed, ");
	    sb.append(" t2.key_parts_id,   t5.category mtr_grp,    t2.parts_id mtr_num,");
		sb.append(" t2.parts_name mtr_desc, t2.keydesc,  t1.parts_type,");
		sb.append(" t1.vendor,  t1.series_no,   t1.base_sn,  t4.trans_by, NVL(t1.init_life,0) init_life,NVL(t6.init_life,0) init_life_used,t1.off_line,");
		sb.append(" to_char(t4.update_time, 'yyyy/MM/dd') doc_time,  t1.remark,t2.limit_type,t6.parts_type parts_type_used,to_char(t6.update_time,'yyyy/MM/dd HH24:mi:ss') create_time,to_char(t1.update_time,'yyyy/MM/dd HH24:mi:ss') create_time_new, ");
//		sb.append(" nvl(floor(sysdate - t1.update_time ), 0)+nvl(floor(sysdate - T6.Update_Time), 0) + NVL(T1.INIT_LIFE, 0) + NVL(T6.INIT_LIFE, 0) actul, ");
		sb.append(" NVL(T1.INIT_LIFE, 0) + NVL(T6.INIT_LIFE, 0) actul, ");
		sb.append(" case when t1.eqp_model is null then eq.model else eq.model end eqp_model ,tkm.key_parts_mustchange_comm_id, ");
		sb.append(" kc.key_parts_clean_id,kc.life_type clean_life_type,kc.limit_life clean_limit_life,t6.life_type life_type_used,t6.limit_life limit_life_used,t6.series_no series_no_used,"
				+ "case when t2.key_parts_id in (select distinct key_parts_id from key_eqp_parts_clean) then 'Y' else 'N' end isClean ");
		sb.append(" from key_eqp_parts t2 ");
		sb.append(" left join parts_data t5 on t2.parts_id=t5.part_no");
		sb.append(" left join ( SELECT * FROM key_parts_use ");
		sb.append(" where parts_use_id in(select seq_index　from parts_use where event_index = '").append(Index).append("') ) t1 on  t2.key_parts_id=t1.key_parts_id ");
		sb.append(" left join equipment eq on t2.eqp_type = eq.model and t2.maint_dept = eq.dept_index ");
		sb.append(" left join (select * from parts_use  where event_index = '").append(Index).append("' ) t4 on t1.parts_use_id = t4.seq_index");
//		sb.append(" left join (select sum(qty)qty,mtr_num,dept_index,plant from mcs_material_sto_req ").append(" group by  mtr_num,dept_index,plant) t3 on t2.parts_id = t3.mtr_num and t3.dept_index=eq.dept_index ");
		sb.append(" left join (select t7.update_time,t9.key_parts_id,t9.keydesc,t7.parts_type,t7.key_use_id key_use_id_used,t7.init_life,t7.series_no,cp.life_type,cp.limit_life from key_parts_use t7 "
				+ " left join parts_use t8 on t7.parts_use_id = t8.seq_index "
				+ " left join key_eqp_parts t9 on t7.key_parts_id = t9.key_parts_id "
				+ " left join key_eqp_parts_clean cp on t7.key_parts_id = cp.key_parts_id and t7.series_no = cp.series_no ");
		sb.append(" where t8.event_index <> '").append(Index).append("'");
		sb.append(" and t7.eqp_id = '").append(eqpId).append("' and t7.status = 'USING') t6 on t2.keydesc=t6.keydesc and t2.key_parts_id=t6.key_parts_id");
		sb.append(" left join (select count(*) delaytime, key_use_id from key_parts_delay_info group by key_use_id) td on td.key_use_id = t1.key_use_id");
		sb.append(" left join key_parts_delay_info tdr on tdr.key_use_id=t1.key_use_id and tdr.event_index='").append(Index).append("'");
		sb.append(" left join key_parts_mustchange_comm tkm on tkm.key_use_id = t1.key_use_id or tkm.key_use_id =t6.key_use_id_used and tkm.event_index='").append(Index).append("' ");
		sb.append(" left join key_eqp_parts_clean kc on t2.key_parts_id=kc.key_parts_id and kc.series_no=t1.series_no");
		sb.append(" where eq.equipment_id = '").append(eqpId).append("'");
		sb.append(" and t2.enable = 'Y'  and (( t1.status='USING' AND t4.event_index='").append(Index).append("')  OR  t1.status IS NULL) ");
		sb.append(" union ");
		sb.append(" ");/*用来查询该机台所有已上机记录（包括已上机关键备件ENABLE被改为N的记录,eqptyp更改过的记录）*/
	    sb.append(" select td.delaytime,t2.use_number,t2.mustchange,t2.warn_spec,t2.error_spec,t1.parts_use_id,NVL(t1.key_use_id, 0) key_use_id,");
	    sb.append(" NVL(t6.key_use_id_used, 0)key_use_id_used,case when t1.parts_type='NEW' then t4.part_count else '0' end used_number, ");
	    sb.append(" CASE WHEN tdr.delay_table_id IS NULL THEN 0 ELSE 1 END isdelayed, ");
	    sb.append(" t2.key_parts_id,   t5.category mtr_grp,    t2.parts_id mtr_num,");
		sb.append(" t2.parts_name mtr_desc, t2.keydesc,  t1.parts_type,");
		sb.append(" t1.vendor,  t1.series_no,   t1.base_sn,  t4.trans_by, NVL(t1.init_life,0) init_life,NVL(t6.init_life,0) init_life_used,t1.off_line,");
		sb.append(" to_char(t4.update_time, 'yyyy/MM/dd') doc_time,  t1.remark,t2.limit_type,t6.parts_type parts_type_used,to_char(t6.update_time,'yyyy/MM/dd HH24:mi:ss') create_time,to_char(t1.update_time,'yyyy/MM/dd HH24:mi:ss') create_time_new, ");
//		sb.append(" nvl(floor(sysdate - t1.update_time ), 0)+nvl(floor(sysdate - T6.Update_Time), 0) + NVL(T1.INIT_LIFE, 0) + NVL(T6.INIT_LIFE, 0) actul, ");
		sb.append(" NVL(T1.INIT_LIFE, 0) + NVL(T6.INIT_LIFE, 0) actul, ");
		sb.append(" case when t1.eqp_model is null then eq.model else eq.model end eqp_model,tkm.key_parts_mustchange_comm_id, ");
		sb.append(" kc.key_parts_clean_id,kc.life_type clean_life_type,kc.limit_life clean_limit_life,t6.life_type life_type_used,t6.limit_life limit_life_used,t6.series_no series_no_used,"
				+ "case when t2.key_parts_id in (select distinct key_parts_id from key_eqp_parts_clean) then 'Y' else 'N' end isClean ");
		sb.append(" from  ( SELECT * FROM key_parts_use ");
		sb.append(" where parts_use_id in(select seq_index　from parts_use where event_index = '").append(Index).append("') ) t1");
		sb.append(" left join key_eqp_parts t2 on  t2.key_parts_id=t1.key_parts_id ");
		sb.append(" left join parts_data t5 on t2.parts_id=t5.part_no");
		sb.append(" left join equipment eq on t2.eqp_type = eq.model and t2.maint_dept = eq.dept_index ");
		sb.append(" left join (select * from parts_use  where event_index = '").append(Index).append("' ) t4 on t1.parts_use_id = t4.seq_index");
//		sb.append(" left join (select sum(qty)qty,mtr_num,dept_index,plant from mcs_material_sto_req ").append(" group by  mtr_num,dept_index,plant) t3 on t2.parts_id = t3.mtr_num and t3.dept_index=eq.dept_index ");
		sb.append(" left join (select t7.update_time,t9.key_parts_id,t9.keydesc,t7.parts_type,t7.key_use_id key_use_id_used,t7.init_life,t7.series_no,cp.life_type,cp.limit_life from key_parts_use t7 "
				+ " left join parts_use t8 on t7.parts_use_id = t8.seq_index "
				+ " left join key_eqp_parts t9 on t7.key_parts_id = t9.key_parts_id "
				+ " left join key_eqp_parts_clean cp on t7.key_parts_id = cp.key_parts_id and t7.series_no = cp.series_no");
		sb.append(" where t8.event_index <> '").append(Index).append("'");
		sb.append(" and t7.eqp_id = '").append(eqpId).append("' and t7.status = 'USING') t6 on t2.keydesc=t6.keydesc and t2.key_parts_id=t6.key_parts_id");
		sb.append(" left join (select count(*) delaytime, key_use_id from key_parts_delay_info group by key_use_id) td on td.key_use_id = t1.key_use_id");
		sb.append(" left join key_parts_delay_info tdr on tdr.key_use_id=t1.key_use_id and tdr.event_index='").append(Index).append("'");
		sb.append(" left join key_parts_mustchange_comm tkm on tkm.key_use_id = t1.key_use_id or tkm.key_use_id =t6.key_use_id_used and tkm.event_index='").append(Index).append("' ");
		sb.append(" left join key_eqp_parts_clean kc on t2.key_parts_id=kc.key_parts_id and kc.series_no=t1.series_no ");
		sb.append(" where eq.equipment_id = '").append(eqpId).append("'");
		sb.append(" and t2.enable is not null  and (( t1.status='USING' AND t4.event_index='").append(Index).append("')  OR  t1.status IS NULL) ");

		List list = SQLProcess.excuteSQLQuery(sb.toString(), delegator);

		List partsUseList_key =new ArrayList();
        for(int i=0;i<list.size();i++){
        	Map partsUseKeyMap=(Map) list.get(i);
        	String vendor=(String)partsUseKeyMap.get("VENDOR");
        	String partsId=(String)partsUseKeyMap.get("MTR_NUM");
        	String keyUseIdUsed=(String)partsUseKeyMap.get("KEY_USE_ID_USED");
        	String keyUseId=(String)partsUseKeyMap.get("KEY_USE_ID");
        	String keyPartsId=(String)partsUseKeyMap.get("KEY_PARTS_ID");
        	String limitType=(String)partsUseKeyMap.get("LIMIT_TYPE");
        	String partsType=(String) partsUseKeyMap.get("PARTS_TYPE");
        	String createTime=(String) partsUseKeyMap.get("CREATE_TIME");
        	String createTimeNew=(String) partsUseKeyMap.get("CREATE_TIME_NEW");
        	String warnSpec=(String)partsUseKeyMap.get("WARN_SPEC");
        	String errorSpec=(String)partsUseKeyMap.get("ERROR_SPEC");
        	String initLife=(String)partsUseKeyMap.get("INIT_LIFE");
        	String initLifeUsed=(String)partsUseKeyMap.get("INIT_LIFE_USED");
        	String partsTypeUsed=(String)partsUseKeyMap.get("PARTS_TYPE_USED");
        	String actul=(String)partsUseKeyMap.get("ACTUL");
        	String mustchange=(String)partsUseKeyMap.get("MUSTCHANGE");
        	String seriesNo=(String)partsUseKeyMap.get("SERIES_NO");
        	String keyPartsCleanId=(String)partsUseKeyMap.get("KEY_PARTS_CLEAN_ID");
        	String isCleanParts=(String)partsUseKeyMap.get("ISCLEAN");
        	String seriesNoUsed=(String)partsUseKeyMap.get("SERIES_NO_USED");
        	if(mustchange==null){mustchange="N";}
        	String keyPartsMustchangeConnId=(String)partsUseKeyMap.get("KEY_PARTS_MUSTCHANGE_COMM_ID");
        	String remainLife="";
        	String partsTypeWar="";
        	String createTimeWar="";
        	String warnRst="N";
        	String errorRst="N";
        	String isUsed="N";
        	String oldLife="";
        	String mustchangeRemark="1";
        	String lifeType="";
        	List seriesNoList=null;
        	List vendorList=new ArrayList();;

        	if(limitType.equals("WAFERCOUNT")){
        		lifeType="W";
        	}else if(limitType.equals("TIME(天)")){
        		lifeType="D";
        	}else if(limitType.equals("RFTIME(Hours)")){
        		lifeType="H";
        	}

        	if(keyUseId.equals("0") && !keyUseIdUsed.equals("0")){
        		isUsed="Y";
        		keyUseId=keyUseIdUsed;
        	}
        	if(initLife.equals("0") && !initLifeUsed.equals("0")){
        		oldLife=initLifeUsed;
        	}else{
        		oldLife=initLife;
        	}

        	if(isCleanParts.equals("Y")){
        		if(isUsed.equals("Y")){
        			seriesNo=seriesNoUsed;
        			partsUseKeyMap.remove("SERIES_NO");
        			partsUseKeyMap.put("SERIES_NO", seriesNo);
        		}
        	}

        	seriesNoList=PartsHelper.getSeriesNoList(delegator, keyPartsId, Index);
        	if(seriesNo!=null&&!seriesNoList.contains(seriesNo)){
        		seriesNoList.add(seriesNo);
        		partsUseKeyMap.put("errorSeriesNo", seriesNo);
        	}

        	List vList=delegator.findByAnd("PartsVendors", UtilMisc.toMap("partsId", partsId), UtilMisc.toList("vendors"));
        	for (int v=0;v<vList.size();v++) {
        		GenericValue gv=(GenericValue)vList.get(v);
				vendorList.add(gv.getString("vendors"));
        	}
        	if(vendor!=null&&!vendorList.contains(vendor)){
        		vendorList.add(vendor);
        	}

            if(warnSpec==null||errorSpec==null){
        		partsUseKeyMap.put("isUsed", isUsed);
        		partsUseKeyMap.put("warnRst", warnRst);
        		partsUseKeyMap.put("errorRst", errorRst);
        		partsUseKeyMap.put("mustchangeRemark", mustchangeRemark);
        		partsUseKeyMap.put("lifeType", lifeType);
        		partsUseKeyMap.put("seriesNoList", seriesNoList);
        		partsUseKeyMap.put("vendorList", vendorList);
        		partsUseList_key.add(partsUseKeyMap);
        		continue;
            }

        	long delayLife=PartsHelper.getDelayLife(delegator,keyUseId);

        	remainLife=(Integer.parseInt(errorSpec)-Integer.parseInt(actul)+delayLife)+"";

        	if(createTime!=null){
        		createTimeWar=createTime;
        	}else if(createTimeNew!=null){
        		createTimeWar=createTimeNew;
        	}else{
    			partsUseKeyMap.put("isUsed", isUsed);
    			partsUseKeyMap.put("warnRst", warnRst);
    			partsUseKeyMap.put("errorRst", errorRst);
    			partsUseKeyMap.put("mustchangeRemark", mustchangeRemark);
    			partsUseKeyMap.put("lifeType", lifeType);
    			partsUseKeyMap.put("seriesNoList", seriesNoList);
    			partsUseKeyMap.put("vendorList", vendorList);
    			if(limitType!=null){
    				partsUseKeyMap.put("remainLife", errorSpec.trim());
    			}
    			partsUseList_key.add(partsUseKeyMap);
    			continue;
        	}
            if(limitType==null){limitType="";}
            if(partsType==null){
            	if(partsTypeUsed!=null){
            		partsTypeWar=partsTypeUsed;
            	}
            }else{
            	partsTypeWar=partsType;
            }
            if(!limitType.equals("TIME(天)")){
//            	actul=PartsHelper.getActulFromFdc(delegator, eqpId, limitType, createTimeWar,null);
//				if(actul.equals("fdcError") || actul.equals("relationError")){
//					partsUseKeyMap.put("lifeError", actul);
//					actul="0";//对应eqpid，limittype没有fdc收值时，使用寿命置为零
//				}else{
//					actul=(Double.parseDouble(actul)+Double.parseDouble(initLife)+Double.parseDouble(initLifeUsed)+"").trim();
//				}
            	actul="0";
            	remainLife=(Double.parseDouble(errorSpec)-Double.parseDouble(actul)+delayLife)+"";
            }
            double warn_days=Double.parseDouble(warnSpec);
            double error_days=Double.parseDouble(errorSpec);
            double actul_days=Double.parseDouble(actul);
            if(!limitType.equals("")){
            	warn_days=warn_days+delayLife;
                error_days=error_days+delayLife;
                if(actul_days>warn_days&&actul_days<=error_days){
                	warnRst="Y";
                    errorRst="N";
                }else if(actul_days>error_days){
                    warnRst="Y";
                    errorRst="Y";
                }

            	partsUseKeyMap.put("remainLife", remainLife.trim());
            }
            if(mustchange.equals("Y") && errorRst.equals("Y") && keyPartsMustchangeConnId==null){
            	mustchangeRemark="0";
            }
            partsUseKeyMap.put("mustchangeRemark", mustchangeRemark);
            partsUseKeyMap.put("lifeType", lifeType);
            partsUseKeyMap.put("seriesNoList", seriesNoList);
            partsUseKeyMap.put("vendorList", vendorList);
            partsUseKeyMap.put("isUsed", isUsed);
        	partsUseKeyMap.put("warnRst", warnRst);
        	partsUseKeyMap.put("errorRst", errorRst);
        	partsUseList_key.add(partsUseKeyMap);
        }

		return partsUseList_key;
	}

	public static void updateMaterial(GenericDelegator delegator, Map parMap) throws GenericEntityException {
		GenericValue gv = delegator.makeValue("McsMaterialStoReq", parMap);
		delegator.store(gv);
	}

	public static void deleteKeyPartsUse(GenericDelegator delegator, String keyuseid) throws GenericEntityException {
		GenericValue gv = delegator.findByPrimaryKey("KeyPartsUse", UtilMisc.toMap("keyUseId", keyuseid));
		delegator.removeValue(gv);
	}

	public static void deleteMcsMaterialStoReq(GenericDelegator delegator, String materialindex)
			throws GenericEntityException {
		GenericValue gv = delegator.findByPrimaryKey("McsMaterialStoReq",
				UtilMisc.toMap("materialStoReqIndex", materialindex));
		delegator.removeValue(gv);
	}

	/**
	 * 开始保养表单，生成“使用物料”的记录 通过表单INDEX及表单类型获取物料信息 优先查找物料模板
	 *
	 * @param delegator
	 * @param eventIndex
	 * @param eventType
	 * @param userNo
	 * @author moxiaoqi
	 * @return
	 * @throws GenericEntityException
	 * @throws SQLProcessException
	 */
	public static List getPartsUseList(GenericDelegator delegator, String eqpId, String eventIndex, String eventType,
			String userNo) throws GenericEntityException, SQLProcessException {
		List partsUseList = new ArrayList();
		// 查询parts_use中是否已生成记录
		StringBuffer sResultQuery = new StringBuffer("");
		sResultQuery.append(" SELECT DISTINCT pu.*,pt.part_type,NVL(msr.batch_NUM, 'Unspecified') batch_num,")
				.append(" fjr.job_name,to_char(pu.update_time, 'yyyy/MM/dd HH24:mi') updatetime,kpu.vendor,")
				.append(" pp.template_count,kpu.series_no,kpu.key_use_id,msr.material_sto_req_index, nvl(msr.qty - nvl(msr.active_qty, 0), 0) stock_qty,")
				.append(" CASE WHEN pu.part_no IN (SELECT parts_id FROM key_eqp_parts")
				.append(" WHERE parts_id = pu.part_no AND eqp_type = eq.model AND enable = 'Y') THEN 'Y' ELSE 'N' END iskeyparts");
		sResultQuery.append(" FROM parts_use pu");
		sResultQuery.append(" LEFT JOIN form_job_relation fjr ON pu.flow_index = fjr.job_index AND pu.event_index = fjr.event_index");
		sResultQuery.append(" LEFT JOIN part_type pt ON pu.part_type = pt.id");
		sResultQuery.append(" LEFT JOIN mcs_material_sto_req msr ON pu.parts_require_index = msr.material_sto_req_index");
		sResultQuery.append(" LEFT JOIN key_parts_use kpu ON pu.seq_index = kpu.parts_use_id");
		sResultQuery.append(" LEFT JOIN parts_pm pp ON pu.part_no = pp.part_no AND pu.flow_index = pp.flow_index AND pu.period_index = pp.period_index");
		sResultQuery.append(" LEFT JOIN equipment eq ON eq.equipment_id = '").append(eqpId).append("'");
		sResultQuery.append(" WHERE pu.event_index = '").append(eventIndex).append("'");
		sResultQuery.append(" AND pu.event_type = '").append(eventType).append("'");
		sResultQuery.append(" ORDER BY pu.flow_index, pu.seq_index");

		partsUseList = SQLProcess.excuteSQLQuery(sResultQuery.toString(), delegator);

		if (partsUseList.isEmpty()) {
			// 查询物料模板
			StringBuffer sPartsPmQuery = new StringBuffer("");
			sPartsPmQuery.append("select a.*, k.key_parts_id, k.eqp_type");
			sPartsPmQuery.append(" from (select t.*, e.model from parts_pm t, pm_form p, equipment e");
			sPartsPmQuery.append("  where t.eqp_type = e.equipment_type  and t.period_index = p.period_index and p.equipment_id = e.equipment_id");
			sPartsPmQuery.append("  and p.pm_index = '").append(eventIndex).append("') a");
			sPartsPmQuery.append(" left join key_eqp_parts k on k.parts_id = a.part_no and k.eqp_type = a.model and k.enable = 'Y'");

			List partsPmList = SQLProcess.excuteSQLQuery(sPartsPmQuery.toString(), delegator);

			Map map = new HashMap();
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			map.put("transBy", userNo);
			map.put("updateTime", nowTimestamp);
			map.put("deptIndex", AccountHelper.getUserDeptIndex(delegator, userNo));
			map.put("eventType", Constants.PM);
			map.put("eventIndex", eventIndex);
			map.put("createTime", nowTimestamp);
			map.put("createUser", userNo);
			map.put("updateUser", userNo);

			boolean partsUseUpdatedFlag = false;
			for (Iterator it = partsPmList.iterator(); it.hasNext();) {
				Map pm = (Map) it.next();
				String keyPartsId = (String) pm.get("KEY_PARTS_ID");
				map.put("mtrGrp", pm.get("MTR_GRP"));
				map.put("partNo", pm.get("PART_NO"));
				map.put("partName", pm.get("PART_NAME"));
				map.put("partType", pm.get("PART_TYPE"));
				map.put("partCount", pm.get("PART_COUNT"));
				map.put("remark", pm.get("REMARK"));
				map.put("flowIndex", pm.get("FLOW_INDEX"));
				map.put("periodIndex", pm.get("PERIOD_INDEX"));
				map.put("eqpId", pm.get("EQUIPMENT_ID"));

				// 根据PM备件使用模板 更新PARTS_USE
				GenericValue gv = delegator.makeValidValue("PartsUse", map);
				Long partsUseIndex = delegator.getNextSeqId("partsUseSeqIndex");
				gv.put("seqIndex", partsUseIndex);
				delegator.create(gv);
				partsUseUpdatedFlag = true;

				// 更新KEY_PARTS_USE
				if (StringUtils.isNotEmpty(keyPartsId)) {
					map.put("keyUseId", delegator.getNextSeqId("keyPartsUseSeqIndex"));
					map.put("keyPartsId", keyPartsId);
					map.put("partsUseId", partsUseIndex);
					map.put("eqpModel", pm.get("EQP_TYPE"));
					map.put("partsType", pm.get("PART_TYPE"));
					gv = delegator.makeValidValue("KeyPartsUse", map);
					gv.create();
				}
				// 更新线边仓
				// if(!partType.equals("新品")){
				// map.put("qty","0");
				// }
				// //同时插入线边仓表
				// gv=delegator.makeValidValue("McsMaterialStoReq", map) ;
				// // id=delegator.getNextSeqId("McsMaterialStoReqSeqIndex");
				// id= Long.parseLong(PartsHelper.getMaxMaterialIndex(delegator)) ;
				// gv.put("materialStoReqIndex", id);
				// delegator.create(gv);
			}
			// PRATS_USE更新后重新检索用于显示
			if (partsUseUpdatedFlag) {
				partsUseList = SQLProcess.excuteSQLQuery(sResultQuery.toString(), delegator);
			}
		}

		return partsUseList;
	}

	public static void getKeydescByModel(HttpServletRequest request, HttpServletResponse response){
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    	String model=request.getParameter("model");
    	String sql="select distinct t.keydesc from key_eqp_parts t where t.eqp_type='"+model+"' ";
    	JSONObject jsObject=new JSONObject();
    	JSONArray keydescArray=new JSONArray();
    	try {
			List partsIdList=SQLProcess.excuteSQLQuery(sql, delegator);
			if(partsIdList!=null&&partsIdList.size()>0){
				for(int i=0;i<partsIdList.size();i++){
					keydescArray.put(((Map)partsIdList.get(i)).get("KEYDESC"));
				}
			}
			jsObject.put("keydescArray", keydescArray);
			response.getWriter().write(jsObject.toString());
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
		}
    }

	public static void getPartsIdByKeydesc(HttpServletRequest request, HttpServletResponse response){
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    	String model=request.getParameter("model");
    	String keydesc=request.getParameter("keydesc");
    	String sql="select distinct t.parts_id from key_eqp_parts t where t.eqp_type='"+model+"' and t.keydesc='"+keydesc+"' ";
    	JSONObject jsObject=new JSONObject();
    	try {
			List partsIdList=SQLProcess.excuteSQLQuery(sql, delegator);
			if(partsIdList!=null&&partsIdList.size()>0){
				String partsId=(String) ((Map)partsIdList.get(0)).get("PARTS_ID");
				jsObject.put("partsId", partsId);
			}
			response.getWriter().write(jsObject.toString());
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
		}
    }

    public static void getSeriesNoByModelAndKeydesc(HttpServletRequest request, HttpServletResponse response){
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    	String model=request.getParameter("model");
    	String keydesc=request.getParameter("keydesc");
    	String sql="select t.series_no from key_eqp_parts_clean t left join key_eqp_parts t1 on t.key_parts_id=t1.key_parts_id "
    			+ " where t1.eqp_type='"+model+"' and t1.keydesc='"+keydesc+"' ";
    	JSONObject jsObject=new JSONObject();
    	JSONArray seriesNoArray=new JSONArray();
    	try {
			List list=SQLProcess.excuteSQLQuery(sql, delegator);
			if(list!=null&&list.size()>0){
				for(int i=0;i<list.size();i++){
					String seriesNo=(String) ((Map)list.get(i)).get("SERIES_NO");
					seriesNoArray.put(seriesNo);
				}
			}
			jsObject.put("seriesNoArray", seriesNoArray);
			response.getWriter().write(jsObject.toString());
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
		}
    }

    /**
	 * 获取未到期的清洗件list
	 * @param delegator
	 * @param keyPartsId
	 * @return
	 * @throws Exception
	 */
	public static List getSeriesNoList(GenericDelegator delegator,String keyPartsId,String eventIndex) throws Exception{
		List list=delegator.findByAnd("KeyEqpPartsClean", UtilMisc.toMap("keyPartsId", keyPartsId,"enable","Y"));
		List seriesNoList=new ArrayList();

		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();i++){
				String actul="0";
				Map map=(Map) list.get(i);
				String sn=(String) map.get("seriesNo");
				String lifeType=(String) map.get("lifeType");
				String limitLife=(String) map.get("limitLife");
				String keyPartsCleanId=(map.get("keyPartsCleanId")+"").trim();
				String sql_actul="select nvl(floor(sysdate - t.update_time), '0') actul from key_parts_use t "
						+ "where t.key_parts_clean_id='"+keyPartsCleanId+"' and t.status='USING' ";
				String sql="select * from key_parts_use t left join parts_use t1 on t.parts_use_id=t1.seq_index "
						+ "where t.key_parts_clean_id='"+keyPartsCleanId+"' and t.status='USING' and t1.event_index<>'"+eventIndex+"' ";
				List cleanPartsList_inuse=SQLProcess.excuteSQLQuery(sql, delegator);
				List actulList=SQLProcess.excuteSQLQuery(sql_actul, delegator);
				if(actulList!=null&&actulList.size()>0){
					actul=(String) ((Map)actulList.get(0)).get("ACTUL");
				}
				String clenaPartsLife=getCleanPartsLife(delegator, keyPartsCleanId, actul, lifeType);
				if(cleanPartsList_inuse!=null&&cleanPartsList_inuse.size()>0){
					continue;
				}else{
					if(Integer.parseInt(clenaPartsLife)<Integer.parseInt(limitLife)){
						seriesNoList.add(sn);
					}
				}
			}
		}

		return seriesNoList;
	}

	/**
	 * 获取清洗件的历史使用寿命
	 * @param delegator
	 * @param eqpId
	 * @param keyPartsId
	 * @param seriesNo
	 * @return
	 * @throws SQLProcessException
	 */
	public static String getCleanPartsLife(GenericDelegator delegator,String keyPartsCleanId,String actul,String limitType) throws SQLProcessException{
		String cleanPartsLife="";
		String sql="select nvl(sum(nvl(floor(t.create_time-t.update_time),'0')),'0') usedLife from key_parts_use t "
				+ "where t.key_parts_clean_id = '"+keyPartsCleanId+"' ";
		String sql1="select count(*) usedLife from key_parts_use t where t.key_parts_clean_id = '"+keyPartsCleanId+"' ";
		if(limitType.equals("TIME(天)")){
			List list=SQLProcess.excuteSQLQuery(sql, delegator);
			cleanPartsLife=(String) ((Map)list.get(0)).get("USEDLIFE");
			cleanPartsLife=Integer.parseInt(cleanPartsLife)+Integer.parseInt(actul)+"";
		}else if(limitType.equals("TIMES(次)")){
			List list1=SQLProcess.excuteSQLQuery(sql1, delegator);
			cleanPartsLife=(String) ((Map)list1.get(0)).get("USEDLIFE");
		}

		if(cleanPartsLife.equals("")){
			cleanPartsLife="0";
		}
		return cleanPartsLife.trim();
	}

	public static String getMaxkeyPartsMustchangeCommId(GenericDelegator delegator){
    	String sql="select max(key_parts_mustchange_comm_id) key_parts_mustchange_commId from key_parts_mustchange_comm";
    	String keyPartsMustchangeCommId="";
    	try {
			List list=SQLProcess.excuteSQLQuery(sql, delegator);
			Map map=(Map) list.get(0);
			keyPartsMustchangeCommId=(String) map.get("KEY_PARTS_MUSTCHANGE_COMMID");
		} catch (Exception e) {
			Debug.logError(module, e.getMessage());
		}
		return keyPartsMustchangeCommId;
    }

	public static GenericValue getKeyPartInfo(GenericDelegator delegator, String partId, String eqpId) {
		GenericValue keyPartInfo = null;
        try {
            GenericValue eqpGv = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId", eqpId));
            String eqpModel = eqpGv.getString("model");
            List<GenericValue> keyPartInfoList = delegator.findByAnd("KeyEqpParts", UtilMisc.toMap("partsId", partId, "eqpType", eqpModel));
            if (keyPartInfoList != null && keyPartInfoList.size() > 0) {
            	keyPartInfo = keyPartInfoList.get(0);
            }
        } catch (Exception e) {
            Debug.logError(e.getMessage(), module);
        }
        return keyPartInfo;
    }

	public static void savePartsUsePatch(GenericDelegator delegator, Map paramMap) throws GenericEntityException {
		Timestamp nowTs = UtilDateTime.nowTimestamp();
		Long seqIndex = delegator.getNextSeqId("partsUseSeqIndex");
		GenericValue partsUse = delegator.makeValidValue("PartsUse", paramMap);
		partsUse.put("seqIndex", seqIndex);
		partsUse.put("remark", "补填");
		partsUse.put("updateTime", nowTs);
		partsUse.create();

		// KeyPartsUse
		String keyPartsId = (String) paramMap.get("keyPartsId");
		String eventIndex = (String) paramMap.get("eventIndex");
		String eventType = (String) paramMap.get("eventType");
		String eqpId = (String) paramMap.get("eqpId");
		String offreason = "PATCH";
		if (StringUtils.isNotEmpty(keyPartsId)) {
			// 先把同一个eqpid,同一个关键字，USING，改成 OFFLINE
			StringBuffer sql = new StringBuffer("");
			sql.append(" update key_parts_use t1");
			sql.append(" SET t1.UPDATE_TIME=SYSDATE, t1.status='OFFLINE', t1.off_line='").append(offreason).append("'");
			sql.append(" where exists (select t1.* from parts_use t2,key_eqp_parts t3 where t1.parts_use_id =t2.seq_index and t1.key_parts_id=t3.key_parts_id ");
			sql.append(" and t2.event_index <> '").append(eventIndex).append("'");
			sql.append(" and t2.event_type = '").append(eventType).append("'");
			sql.append(" and t1.eqp_id = '").append(eqpId).append("' ");
			sql.append(" and t1.status='USING' and t1.key_parts_id='").append(keyPartsId).append("' ) ");
			try {

				SQLProcess.excuteSQLUpdateForQc(sql.toString(), delegator);
			} catch (Exception e) {
				Debug.logError(module, e.getMessage());
			}

			GenericValue keyPartsUse = delegator.makeValidValue("KeyPartsUse", paramMap);
			Long keyUseId = delegator.getNextSeqId("KeyPartsUseKeyUseId");
			keyPartsUse.put("keyUseId", seqIndex);
			keyPartsUse.put("partsUseId", seqIndex);
			keyPartsUse.put("createTime", nowTs);
			keyPartsUse.put("partsType", "NEW");
			keyPartsUse.put("status", "USING");
			keyPartsUse.put("updateUser", (String) paramMap.get("transBy"));
			keyPartsUse.create();
		}
	}
}
