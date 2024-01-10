/*
 * Created on 2004-8-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.csmc.pms.webapp.eqp.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.service.LocalDispatcher;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.util.CommonUtil;
import com.csmc.pms.webapp.util.Constants;
import com.csmc.pms.webapp.util.MiscUtils;
import com.csmc.pms.webapp.util.TPServiceException;
import com.csmc.pms.webapp.workflow.service.WorkflowService;
import com.fa.csmcgui.webapp.util.GuiAction;
import com.fa.csmcgui.webapp.util.TriggerRuleAlarmByLot;
import com.fa.object.Chart;
import com.fa.object.Item;
import com.fa.object.ItemCollection;
import com.fa.object.Plot;
import com.fa.object.Site;
import com.fa.object.SiteCollection;
import com.fa.object.Wafer;
import com.fa.object.WaferCollection;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class GenDCOPHelper {
    public static final String module = GenDCOPHelper.class.getName();

    /*
     * 根据operationId，取得进行DCOP的必须数据，主要有DCOP类型，
     * 要进行测试的WaferCollection，及对应的ItemList等
     *
     */
    public static Map queryDcopFormat(String operationId,
            GenericDelegator delegator, GenericValue userLogin)
            throws TPServiceException {
        Map result = new HashMap();
        //根据operationId，取得DCOP的类型
        Map ret = FabAdapter.runCallService(delegator, userLogin, UtilMisc
                .toMap("operid", operationId), Constants.DCOP_FORMAT_QUERY);

        String dcopTypeCategory = (String) ret.get("dcoptypecategory");
        //保存DCOP的类型
        result.put("dcoptypecategory", dcopTypeCategory);
        //compstotest保持着需要进行测试的wafer对象集合
        result.put("compstotest", ret.get("compstotest"));
        //是否允许NONE
        result.put("noneallowed", ret.get("noneallowed"));
        //@todo

        if ("ITEM".equalsIgnoreCase(dcopTypeCategory)) {
            result.put("itemlist", ret.get("itemlist"));
        } else if ("COMP".equalsIgnoreCase(dcopTypeCategory)) {
            result.put("itemlist", ret.get("itemlist"));
        } else if ("SITE".equalsIgnoreCase(dcopTypeCategory)) {
            result.put("itemlist", ret.get("itemlist"));
            result.put("sitelist", ret.get("sitelist"));
        }

        return result;
    }

    /*
     * 输入Item型DCOP数据, itemDCOP包含operId, location, eqpid和itemList的数据
     *
     */
    public static Map enterGenDCOPItem(HashMap itemDCOP,
            GenericDelegator delegator, GenericValue userLogin)
            throws TPServiceException {
        //		Map result = new HashMap();
        //调用TP
        Map ret = FabAdapter.runCallService(delegator, userLogin, itemDCOP,
                Constants.ENTER_LOC_DCOP_ITEM);

        //calculationlist保存计算结果
        //		result.put("calculationlist", ret.get("calculationlist"));
        //		//warnmessage保存警告信息
        //		result.put("warnmessage", ret.get("warnmessage"));
        //		result.put("enttime", ret.get("enttime"));
        return ret;
    }

    /*
     * 输入Comp型DCOP数据, compDCOP包含operId, location, eqpid和waferlist的数据
     *
     */
    public static Map enterGenDCOPComp(HashMap compDCOP,
            GenericDelegator delegator, GenericValue userLogin)
            throws TPServiceException {
        Map result = new HashMap();
        //调用TP
        Map ret = FabAdapter.runCallService(delegator, userLogin, compDCOP,
                Constants.ENTER_LOC_DCOP_COMP);

        //calculationlist保存计算结果
        //		result.put("calculationlist", ret.get("calculationlist"));
        //		//warnmessage保存警告信息
        //		result.put("warnmessage", ret.get("warnmessage"));
        //		result.put("enttime", ret.get("enttime"));
        return ret;
    }

    /*
     * 输入Site型DCOP数据, siteDCOP包含operId, location, eqpid和waferlist的数据
     *
     */
    public static Map enterGenDCOPSite(HashMap siteDCOP,
            GenericDelegator delegator, GenericValue userLogin)
            throws TPServiceException {
        //		Map result = new HashMap();
        //调用TP
        Map ret = FabAdapter.runCallService(delegator, userLogin, siteDCOP,
                Constants.ENTER_LOC_DCOP_SITE);

        //calculationlist保存计算结果
        //		result.put("calculationlist", ret.get("calculationlist"));
        //		//warnmessage保存警告信息
        //		result.put("warnmessage", ret.get("warnmessage"));
        //		result.put("enttime", ret.get("enttime"));
        return ret;
    }


    /**
     * @param dataMap
     * @param userLogin
     * @param chart
     * @return
     */
    private static String buildSpcAlarmContent(Map dataMap, GenericValue userLogin, Chart chart) {
        String content = "Chart Alarm :" + "\n DCOP ID : "
                + dataMap.get("operationId") + "\n Location ID : "
                + dataMap.get("locationId") + "\n CHART ID : "
                + chart.getChartID() + "\n CHART Title : "
                + chart.getChartTitle() + "\n 设备 : "
                + dataMap.get("equipmentId") + "\n OPERATOR : "
                + userLogin.get("userLoginId") + "\n 输入数据时间 : "
                + new Date() + "\n 输入数据 : ";

        String dcopType = (String) dataMap.get("dcopType");
        String dataString = "";
        if ("ITEM".equalsIgnoreCase(dcopType)) {
            ItemCollection itemList = (ItemCollection) dataMap
                    .get("itemlist");
            for (int i = 0; i < itemList.size(); i++) {
                Item item = (Item) itemList.get(i);
                String itemName = item.getItemName();
                String itemValue = item.getItemValue();
                dataString = dataString + "\n " + itemName + " : "
                        + itemValue;
            }
        } else if ("COMP".equalsIgnoreCase(dcopType)) {
            WaferCollection waferList = (WaferCollection) dataMap
                    .get("waferlist");
            for (int i = 0; i < waferList.size(); i++) {
                Wafer wafer = (Wafer) waferList.get(i);
                String waferId = wafer.getWaferid();
                dataString = dataString + "\n Wafer ID : "
                        + waferId;
                ItemCollection itemList = wafer.getItemCollection();
                for (int k = 0; k < itemList.size(); k++) {
                    Item item = (Item) itemList.get(k);
                    String itemName = item.getItemName();
                    String itemValue = item.getItemValue();
                    dataString = dataString + "\n " + itemName
                            + " : " + itemValue;
                }
            }
        } else {
            WaferCollection waferList = (WaferCollection) dataMap
                    .get("waferlist");
            for (int i = 0; i < waferList.size(); i++) {
                Wafer wafer = (Wafer) waferList.get(i);
                String waferId = wafer.getWaferid();
                dataString = dataString + "\n Wafer ID : "
                        + waferId;
                SiteCollection siteList = wafer.getSiteCollection();
                for (int j = 0; j < siteList.size(); j++) {
                    Site site = (Site) siteList.get(j);
                    String siteId = site.getSiteID();
                    dataString = dataString + "\n Site ID : "
                            + siteId;
                    ItemCollection itemList = site
                            .getItemCollection();
                    for (int k = 0; k < itemList.size(); k++) {
                        Item item = (Item) itemList.get(k);
                        String itemName = item.getItemName();
                        String itemValue = item.getItemValue();
                        dataString = dataString + "\n " + itemName
                                + " : " + itemValue;
                    }
                }

            }
        }

        //add by rolex for oos mail
//        String oosFlag = (String)dataMap.get("oosFlag");
//        if("Y".equals(oosFlag)) {
//          content = "The Chart is OOS!\n" + content;
//        }
        content = content + dataString;
        return content;
    }

    /**
     * 调用GUI判断OOC
     * 
     * @param delegator
     * @param dispatcher
     * @param result
     * @param dataMap
     * @param userLogin Create on 2011-7-15 Update on 2011-7-15
     */
    public static void triggerSPCAlarm(GenericDelegator delegator, LocalDispatcher dispatcher,
            Map result, Map dataMap, GenericValue userLogin) {
        List chartlist = (List)result.get("chartlist");
        Timestamp t;
        try {
            t = MiscUtils.promisToTimestamp((String)result.get("enttime"));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            t = new Timestamp(System.currentTimeMillis());
        }

        if (chartlist != null) {
            for (Iterator it = chartlist.iterator(); it.hasNext();) {
                Map alarmDataMap = new HashMap();
                Chart chart = (Chart)it.next();
                String chartType = null;
                List plotList = new ArrayList();
                Map runResult = null;
                Map chartMap = new HashMap();
                Map dcopResultMap = new HashMap();
                String chartId = chart.getChartID();
                String dcopId = (String)dataMap.get("operationId");
                try {
                    chartMap.put("chartid", chartId);
                    runResult = FabAdapter.runCallService(delegator, userLogin, chartMap,
                            Constants.CHTQUERY_READCHART);
                    Chart c = (Chart)runResult.get("chart");
                    chart.setChartTitle(c.getChartTitle());
                    plotList = (List)runResult.get("plotlist");
                    Debug.logInfo("Start DCOP alarm...", module);
                    chartType = ((Plot)plotList.get(0)).getPlotType();
                    Debug.logInfo("chartId..."+chartId, module);
                    Debug.logInfo("dcopId..."+dcopId, module);
                    dcopResultMap = getDcopResultMap(delegator, chartId, dcopId, t);
                } catch (TPServiceException e2) {
                    Debug.logError(e2, module);
                }

                Set guiRules = getRulesByLot(delegator, chart.getChartID());
                // end
                if (guiRules != null && guiRules.size() > 0) {
                    List hisDcopValueList = getHisDcopValueByLot(delegator, dispatcher, dcopId,
                            chartId, "RANGE", t);
                    try {
                        Collection alarmDataList = TriggerRuleAlarmByLot.triggerRuleAlarm(
                                delegator, dispatcher, result, dataMap, userLogin, t, chartType,
                                chart, runResult, dcopResultMap, hisDcopValueList);
                        Debug.logInfo("alarmDataList...", module);
                        Debug.logInfo(alarmDataList.toString(), module);
                        Debug.logInfo("result...", module);
                        Debug.logInfo(result.toString(), module);
                        Debug.logInfo("chart in result...", module);
//                        List cl = (List)result.get("chart");
//                        for (Iterator iterator = cl.iterator(); iterator.hasNext();) {
//                            Chart c = (Chart)iterator.next();
//                            Debug.logInfo(chartToString(c), module);
//                        }
                        Debug.logInfo("dataMap...", module);
                        Debug.logInfo(dataMap.toString(), module);
                        Debug.logInfo("t...", module);
                        Debug.logInfo(t.toString(), module);
                        Debug.logInfo("chartType...", module);
                        Debug.logInfo(chartType.toString(), module);
                        Debug.logInfo("chart...", module);
//                        Debug.logInfo(chartToString(chart), module);
                        Debug.logInfo("runResult...", module);
                        Debug.logInfo(runResult.toString(), module);
                        Debug.logInfo("dcopResultMap...", module);
                        Debug.logInfo(dcopResultMap.toString(), module);
                        Debug.logInfo("hisDcopValueList...", module);
                        Debug.logInfo(hisDcopValueList.toString(), module);
                        Debug.logInfo("End DCOP alarm...", module);
                        if (alarmDataList != null && alarmDataList.size() > 0) {
                            // trigger++;
                            String content = buildSpcAlarmContent(dataMap, userLogin, chart);
                            alarmDataMap.put("dcopId", dcopId);
                            alarmDataMap.put("mailContent", content);
                            alarmDataMap.put("dcopEntDate", t);
                            alarmDataMap.put("oos", "N");
                            alarmDataMap.put("alarmDataList", alarmDataList);
                            alarmDataMap.put("collectionLevel", "LOT");
                            try {
                                Map triggerResult = dispatcher.runSync("triggerAlarmByRule",
                                        UtilMisc.toMap("objectType", "SPC", "objectId", chart
                                                .getChartID().trim(), "trancComment", "DCOP Alarm",
                                                "alarmDataMap", alarmDataMap));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    /**
     * for debug
     * @param chart
     * @return
     * Create on 2011-8-9
     * Update on 2011-8-9
     */
    private static String chartToString(Chart chart){
        StringBuffer sb = new StringBuffer();
        sb.append("Activity ID : ").append(chart.getActivityID()).append("\n")
        .append("CapsRecognized: ").append(chart.getCapsRecognized()).append("\n")
        .append("ChartID: ").append(chart.getChartID()).append("\n")
        .append("ChartOOC: ").append(chart.getChartOOC()).append("\n")
        .append("ChartTitle: ").append(chart.getChartTitle()).append("\n")
        .append("CommandFile: ").append(chart.getCommandFile()).append("\n")
        .append("Cp: ").append(chart.getCp()).append("\n")
        .append("Cpk: ").append(chart.getCpk()).append("\n")
        .append("Cpl: ").append(chart.getCpl()).append("\n")
        .append("Enabled: ").append(chart.getEnabled()).append("\n")
        .append("EngOwner: ").append(chart.getEngOwner()).append("\n")
        .append("EqpID: ").append(chart.getEqpID()).append("\n")
        .append("EqpStatus: ").append(chart.getEqpStatus()).append("\n")
        .append("SendAlarm: ").append(chart.getSendAlarm()).append("\n");
        return sb.toString();
    }
    /**
     * 获得rule
     * 
     * @param delegator
     * @param chartId
     * @return Create on 2011-7-15 Update on 2011-7-15
     */
    private static Set getRulesByLot(GenericDelegator delegator, String chartId) {

        Set ruleList = new TreeSet();

        try {

            String sql = "SELECT SCR.RULE_ID"
                    + " FROM SPC_INLINE_RULE SR,SPC_INLINE_CHART_RULE SCR WHERE SR.RULE_ID=SCR.RULE_ID AND SR.STATUS='ACTIVE' "
                    + " AND SCR.BY_LOT_FLAG='Y' AND SCR.CHART_ID= '" + chartId + "'";
            List list = SQLProcess.excuteSQLQueryGui(sql, delegator);
            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                Map map = (Map)iterator.next();
                ruleList.add((String)map.get("RULE_ID"));
            }
        } catch (Exception e) {
            Debug.logError(e, module);
        }
        return ruleList;
    }

    /**
     * @param chartId
     * @param dcopId
     * @param pointDate
     * @return Create on 2011-7-18 Update on 2011-7-18
     */
    private static Map getDcopResultMap(GenericDelegator delegator, String chartId, String dcopId,

    Timestamp pointDate) {

        Map resultMap = new HashMap();
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date armFormateDate = format.parse(pointDate.toString());
            String strPointDate = format.format(armFormateDate);

            String sql = "select t.var1value,t.var2value from chrt_points@plldb t where t.chrtid='"
                    + chartId + "' and t.pointdate=to_date('"
                    + strPointDate + "','yyyy-mm-dd hh24:mi:ss')"
                    + " and t.testopno='" + dcopId + "'";
            Debug.logInfo("getDcopResultMap sql ..."+sql, module);
            List list = SQLProcess.excuteSQLQueryGui(sql, delegator);
            int i = 0;
            if (list.size() == 0) {
                if (i < 10) {
                    Thread.sleep(3000);
                    Debug.logInfo("Get DCOP result ,time is "+new Date(System.currentTimeMillis()).toString(), module);
                    list = SQLProcess.excuteSQLQueryGui(sql, delegator);
                    i++;
                } else
                    Debug.logInfo("Get DCOP result failed ........", module);
            }
            Debug.logInfo("getDcopResultMap list size ..."+list.size(), module);
            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                Map map = (Map)iterator.next();
                resultMap.put("xBarValue", new Double((String)map.get("VAR1VALUE")));
                resultMap.put("rangeValue", new Double((String)map.get("VAR2VALUE")));
                Debug.logInfo("xBarValue ..."+(String)map.get("VAR1VALUE"), module);
                Debug.logInfo("rangeValue ..."+(String)map.get("VAR2VALUE"), module);
            }

        } catch (Exception e) {

            e.printStackTrace();

            Debug.logError(e, module);

        }
        return resultMap;

    }

    /**
     * 获得历史DCOP数据
     * 
     * @param delegator
     * @param dispatcher
     * @param dcopId
     * @param chartId
     * @param chartType
     * @param revdate
     * @return Create on 2011-7-15 Update on 2011-7-15
     */
    private static List getHisDcopValueByLot(GenericDelegator delegator,
            LocalDispatcher dispatcher, String dcopId, String chartId, String chartType,
            Timestamp revdate) {

        List histResultList = new ArrayList();

        try {

            String sql = "select eqpid,partid,othereqpid1,othereqpid2,othereqpid3,var1value,var2value,pointdate from "
                    + " (select * from chrt_points@plldb cp,tres@plldb t where cp.chrtid= '"
                    + chartId
                    + "'"
                    + " and cp.testopno=t.testopno and cp.pointdate = t.revdate order by cp.pointdate desc) "
                    + " where rownum <= 100 ";
            List sqlResultList = SQLProcess.excuteSQLQueryGui(sql, delegator);

            for (Iterator iterator = sqlResultList.iterator(); iterator.hasNext();) {
                Map map = (Map)iterator.next();
                // String pointdateStr = (String)map.get("POINTDATE");
                String pointdateStr = map.get("POINTDATE").toString();
                pointdateStr = StringUtils.substringBefore(pointdateStr, ".");
                Timestamp pointdate = Timestamp.valueOf(pointdateStr);
                if (revdate.compareTo(pointdate) == 0) {
                    // 如果时间一样，就是重测点。删除违反rule记录，重测
                    String waferId = "WAFER";
                    try {
                        Map result = dispatcher.runSync("deleteViolateRulePoint", UtilMisc.toMap(
                                "dcopId", dcopId, "chartId", chartId, "revdate", revdate,
                                "collectionLevel", "LOT", "lotId", " ", "waferId", waferId));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // end
                } else if (revdate.compareTo(pointdate) > 0) {
                    Map hisValue = new HashMap();
                    hisValue.put("eqptID", (String)map.get("EQPID"));
                    hisValue.put("productID", (String)map.get("PARTID"));
                    hisValue.put("partID", (String)map.get("PARTID"));
                    hisValue.put("processEqp1", (String)map.get("OTHEREQPID1"));
                    hisValue.put("processEqp2", (String)map.get("OTHEREQPID2"));
                    hisValue.put("processEqp3", (String)map.get("OTHEREQPID3"));
                    hisValue.put("sampleType", new String("Lot"));

                    hisValue.put("xbarValue", new Double((String)map.get("VAR1VALUE")));

                    if (chartType != null && "RANGE".equalsIgnoreCase(chartType)) {

                        hisValue.put("rangeValue", new Double((String)map.get("VAR2VALUE")));
                        hisValue.put("sigmaValue", null);
                    } else if (chartType != null && "SIGMA".equalsIgnoreCase(chartType)) {

                        hisValue.put("rangeValue", null);
                        hisValue.put("sigmaValue", new Double((String)map.get("VAR1VALUE")));
                    } else {
                        hisValue.put("rangeValue", null);
                        hisValue.put("sigmaValue", null);
                    }
                    histResultList.add(hisValue);
                }
            }

        } catch (Exception e) {
            Debug.logError(e, module);
        }

        return histResultList;
    }

    /**
     * 触发警报
     * 
     * @param delegator
     * @param objectType
     * @param objectId
     * @param transComment
     * @param alarmDataMap
     * @param severity
     * @param priority
     * @throws GenericEntityException
     * @throws SQLProcessException Create on 2011-7-26 Update on 2011-7-26
     */
    public static void triggerAlarmByRule(GenericDelegator delegator, String objectType,
            String objectId, String transComment, Map alarmDataMap, String severity, String priority)
            throws GenericEntityException, SQLProcessException {
        // get Alarm by object type & alarmObjectId
        // added by steven 纪录ViolateRulePoint表
        GenDCOPHelper.recordViolateRule(delegator, (Collection)alarmDataMap.get("alarmDataList"),
                "", (String)alarmDataMap.get("collectionLevel"), objectId);
        // end
        // modify by steven 关联alarm_object_ext 表过滤警报
        List alarmLists = GenDCOPHelper.getAlarmListExtByRule(delegator, objectType, objectId,
                severity, priority, (String)alarmDataMap.get("collectionLevel"));
        // end
        // save alarm info by
        // modify by steven
        if (alarmLists != null && alarmLists.size() > 0) {
            int i = 0;
            Collection alarmDataList = (Collection)alarmDataMap.get("alarmDataList");
            for (Iterator it = alarmDataList.iterator(); it.hasNext();) {
                Map alarmMap = (Map)it.next();
                Debug.logInfo("alarmMap ..."+alarmMap, module);
                String oos = (String)alarmMap.get("oos");
                if (oos != null && (oos.equalsIgnoreCase("Y"))) {
                    i++;
                }
                if (transComment != null) {
                    alarmMap.put("transComment", transComment);
                }
                alarmMap.put("mailContent", (String)alarmDataMap.get("mailContent"));
                GenDCOPHelper.genenateAlarmInstanceNew(delegator, alarmLists, alarmMap,
                        (String)alarmDataMap.get("collectionLevel"));
            }
            // end
            String oosFlag = "N";
            if (i > 0) {
                oosFlag = "Y";
            }
            List alarmActions = new ArrayList();
            Debug.logInfo("alarmLists size ..."+alarmLists.size(), module);
            for (Iterator it = alarmLists.iterator(); it.hasNext();) {
                Map alarm = (Map)it.next();
                Debug.logInfo("alarm ..."+alarm, module);
                List list = GenDCOPHelper.getAlarmActionsByRule(delegator, alarm, oosFlag,
                        (String)alarmDataMap.get("collectionLevel"));
                if (list != null) {
                    alarmActions.addAll(list);
                }
            }
            Debug.logInfo("send mail.....", module);
            Debug.logInfo(alarmActions.toString(), module);
            try {
                // 发送警报
                GuiAction.excuteAction(alarmActions);
            } catch (Exception e) {
                Debug.logError("send alarm error....." + e, module);
            }
        }
    }

    /**
     * 记录重测点
     * 
     * @param delegator
     * @param alarmDataList
     * @param alarmOcapId
     * @param collectionLevel
     * @param chartId
     * @throws GenericEntityException Create on 2011-7-26 Update on 2011-7-26
     */
    public static void recordViolateRule(GenericDelegator delegator, Collection alarmDataList,
            String alarmOcapId, String collectionLevel, String chartId)
            throws GenericEntityException {
        List violateRuleList = new ArrayList();
        Map findMap = new HashMap();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        for (Iterator itt = alarmDataList.iterator(); itt.hasNext();) {
            Map alarmDataMap = (Map)itt.next();
            Map violateRuleMap = new HashMap();
            violateRuleMap.put("testOpno", alarmDataMap.get("dcopId"));
            findMap.put("testOpno", alarmDataMap.get("dcopId"));
            violateRuleMap.put("chartId", chartId);
            findMap.put("chartId", chartId);
            violateRuleMap.put("dcopDate", alarmDataMap.get("dcopEntDate"));
            findMap.put("dcopDate", alarmDataMap.get("dcopEntDate"));
            if (alarmDataMap.get("lotId") != null
                    && !"".equalsIgnoreCase((String)alarmDataMap.get("lotId"))) {
                violateRuleMap.put("lotId", alarmDataMap.get("lotId"));
            } else {
                violateRuleMap.put("lotId", " ");
            }
            if (alarmDataMap.get("waferId") != null
                    && !"".equalsIgnoreCase((String)alarmDataMap.get("waferId"))) {
                violateRuleMap.put("waferId", alarmDataMap.get("waferId"));
            } else {
                violateRuleMap.put("waferId", " ");
            }
            if (alarmDataMap.get("siteId") != null
                    && !"".equalsIgnoreCase((String)alarmDataMap.get("siteId"))) {
                violateRuleMap.put("siteName", alarmDataMap.get("siteId"));
            } else {
                violateRuleMap.put("siteName", " ");
            }
            violateRuleMap.put("collectionLevel", collectionLevel);
            findMap.put("collectionLevel", collectionLevel);
            violateRuleMap.put("graphType", alarmDataMap.get("graphType"));
            if (alarmDataMap.get("alarmOcapId") != null)
                alarmOcapId = (String)alarmDataMap.get("alarmOcapId");
            violateRuleMap.put("alarmOcapId", alarmOcapId);
            violateRuleMap.put("violateRules", alarmDataMap.get("violateRuleID"));
            violateRuleMap.put("violateDate", now);
            if (collectionLevel != null && "LOT".equalsIgnoreCase(collectionLevel)) {
                findMap.put("lotId", violateRuleMap.get("lotId"));
            } else if (collectionLevel != null && "WAFER".equalsIgnoreCase(collectionLevel)) {
                findMap.put("lotId", violateRuleMap.get("lotId"));
                findMap.put("waferId", violateRuleMap.get("waferId"));
            } else if (collectionLevel != null && "POINT".equalsIgnoreCase(collectionLevel)) {
                findMap.put("lotId", violateRuleMap.get("lotId"));
                findMap.put("waferId", violateRuleMap.get("waferId"));
                findMap.put("siteName", violateRuleMap.get("siteName"));
            }
            violateRuleList.add(delegator.makeValidValue("ViolateRulePoint", violateRuleMap));
        }
        List ruleValue = delegator.findByAnd("ViolateRulePoint", findMap);
        delegator.removeAll(ruleValue);
        delegator.storeAll(violateRuleList);
    }

    /**
     * 根据rule获得警报邮件收件人
     * 
     * @param delegator
     * @param alarmData
     * @param oosFlag
     * @param collectionLevel
     * @return
     * @throws GenericEntityException Create on 2011-7-26 Update on 2011-7-26
     */
    public static List getAlarmActionsByRule(GenericDelegator delegator, Map alarmData,
            String oosFlag, String collectionLevel) throws GenericEntityException {
        List actionList = new ArrayList();
        List list = null;
        try {
            list = delegator.findByAnd("AlarmAction",
                    UtilMisc.toMap("alarmId", alarmData.get("alarmId")));
        } catch (GenericEntityException e1) {
            e1.printStackTrace();
        }

        for (Iterator it = list.iterator(); it.hasNext();) {
            Map action = (Map)it.next();
            String programId = (String)action.get("programId");
            String p1 = (String)action.get("programPara1");
            String p2 = (String)action.get("programPara2");
            String p3 = (String)action.get("programPara3");
            String p4 = (String)action.get("programPara4");
            String p5 = (String)action.get("programPara5");

            if ("Y".equals(oosFlag)) {
                p4 = p4 + " (The " + collectionLevel + " is OOS!!)";
            }
            p4 = p4 + " 按" + collectionLevel + "量测，触发警报。";

            // 接受人增加产品owner，add by zhangwf(2005/08/04)
            String partId = (String)alarmData.get("partId");
            if ((partId != null) && (!"".equalsIgnoreCase(partId))) {
                partId = partId.substring(0, partId.indexOf('.'));
                String ownerMailAdd = "";
                String owner = "";
                String selectSQL = "";
                try {
                    List ownerList = new ArrayList();
                    selectSQL = "select OWNER from part_owner  where partname = '" + partId + "'";

                    ownerList = SQLProcess.excuteSQLQueryGui(selectSQL, delegator);
                    if (ownerList != null && ownerList.size() > 0) {
                        owner = (String)((Map)ownerList.get(0)).get("OWNER");
                    }

                    if ((owner != null) && !("".equals(owner))) {
                        List emailAddList = new ArrayList();
                        selectSQL = "select t.EMAIL_ADDR from mail_address t where t.account_id = '"
                                + owner + "'";
                        // 执行SQL
                        emailAddList = SQLProcess.excuteSQLQueryGui(selectSQL, delegator);
                        if (emailAddList != null && emailAddList.size() > 0) {
                            ownerMailAdd = (String)((Map)emailAddList.get(0)).get("EMAIL_ADDR");
                        }

                        if ((ownerMailAdd != null) && !("".equals(ownerMailAdd))) {
                            p2 = p2 + ";" + ownerMailAdd;
                        }

                    }

                    // 增加其他收件人
                    List otherEmailAddrList = new ArrayList();
                    selectSQL = "select EMAIL_ADDRESS from OOC_OTHER_MAILADD where inline_spc_alarm=1";
                    otherEmailAddrList = SQLProcess.excuteSQLQueryGui(selectSQL, delegator);

                    for (int i = 0; i < otherEmailAddrList.size(); i++) {
                        if (((Map)otherEmailAddrList.get(i)).get("EMAIL_ADDRESS") != null
                                && !"".equalsIgnoreCase((String)((Map)otherEmailAddrList.get(i))
                                        .get("EMAIL_ADDRESS"))
                                && !"null"
                                        .equalsIgnoreCase((String)((Map)otherEmailAddrList.get(i))
                                                .get("EMAIL_ADDRESS"))) {
                            p2 = p2 + ";"
                                    + (String)((Map)otherEmailAddrList.get(i)).get("EMAIL_ADDRESS");
                        }
                    }

                    // 增加OOS收件人

                    if ("Y".equalsIgnoreCase(oosFlag)) {
                        List oosMailList = SQLProcess.excuteSQLQueryGui(
                                "SELECT EMAIL_ADDRESS FROM OOS_MAILADD", delegator);
                        for (Iterator ite = oosMailList.iterator(); ite.hasNext();) {
                            String mailA = (String)((Map)ite.next()).get("EMAIL_ADDRESS");
                            if (mailA != null && !"".equalsIgnoreCase(mailA)
                                    && !"null".equalsIgnoreCase(mailA)) {
                                p2 += ";" + mailA;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Get partid owner error(add by zhangwf):" + e.toString());
                }
            }

            GuiAction ca;
            try {
                ca = (GuiAction)Class.forName(programId).newInstance();
                ca.init(delegator, alarmData, p1, p2, p3, p4, p5);
                actionList.add(ca);
            } catch (InstantiationException e) {
                Debug.logError(e, WorkflowService.module);
            } catch (IllegalAccessException e) {
                Debug.logError(e, WorkflowService.module);
            } catch (ClassNotFoundException e) {
                Debug.logError(e, WorkflowService.module);
            } catch (Exception e) {
                Debug.logError(e, WorkflowService.module);
            }
        }

        return actionList;

    }

    /**
     * 获取警报
     * 
     * @param delegator
     * @param objectType
     * @param objectId
     * @param severity
     * @param priority
     * @param collectionLevel
     * @return
     * @throws GenericEntityException
     * @throws SQLProcessException Create on 2011-7-25 Update on 2011-7-25
     */
    public static List getAlarmListExtByRule(GenericDelegator delegator, String objectType,
            String objectId, String severity, String priority, String collectionLevel)
            throws GenericEntityException, SQLProcessException {
        String sql = "";
        sql = " select A.ALARM_ID,ALARM_DESC,OBJECT_TYPE,SEVERITY,PRIORITY,DEFAULT_ALARM_TEXT,ALARM_OWNER,OBJECT_ID,ALARM_TEXT FROM ALARM A,ALARM_OBJECT B WHERE A.ALARM_ID=B.ALARM_ID AND ENABLE_FLAG='Y' AND OBJECT_TYPE='"
                + objectType + "' AND (OBJECT_ID='" + objectId + "' OR OBJECT_ID is null)";
        if (severity != null) {
            sql = sql + " AND SEVERITY=" + severity;
        }
        if (priority != null) {
            sql = sql + " AND PRIORITY" + priority;
        }
        List list = SQLProcess.excuteSQLQueryGui(sql, delegator);
        List alarmList = new ArrayList();

        if (list != null) {
            for (Iterator it = list.iterator(); it.hasNext();) {
                Map map = (Map)it.next();
                Map alarm = new HashMap();

                alarm.put("alarmId", map.get("ALARM_ID"));
                alarm.put("alarmDesc", map.get("ALARM_DESC"));
                alarm.put("objectType", map.get("OBJECT_TYPE"));
                alarm.put("severity", map.get("SEVERITY"));
                alarm.put("priority", map.get("PRIORITY"));
                alarm.put("alarmOwner", map.get("ALARM_OWNER"));
                alarm.put("objectId", map.get("OBJECT_ID"));
                alarm.put("objectDept",
                        GenDCOPHelper.getDept((String)map.get("OBJECT_ID"), delegator));

                if (map.get("ALARM_TEXT") != null) {
                    alarm.put("alarmText", map.get("ALARM_TEXT"));
                } else {
                    alarm.put("alarmText", map.get("DEFAULT_ALARM_TEXT"));
                }

                alarmList.add(alarm);
            }
        }
        return alarmList;
    }

    /**
     * 部门信息
     * 
     * @param objectId
     * @param delegator
     * @return
     * @throws SQLProcessException Create on 2011-7-26 Update on 2011-7-26
     */
    public static String getDept(String objectId, GenericDelegator delegator)
            throws SQLProcessException {
        String moduleId = "";
        String selectSQL = "select PREFIX_INCLUDE, MODULE_ID, PREFIX_EXCLUDE from SPC_MODULE";
        // String selectSQL = "select Distinct MODULE_ID from SPC_MODULE where
        // substr(PREFIX_INCLUDE,0,1) = '" + flag + "'";
        // 执行SQL
        List moduleList = SQLProcess.excuteSQLQueryGui(selectSQL, delegator);
        if (moduleList != null && moduleList.size() > 0) {
            for (Iterator it = moduleList.iterator(); it.hasNext();) {
                Map moduleInfo = (Map)it.next();
                String moduleStr = (String)moduleInfo.get("PREFIX_INCLUDE");
                String[] moduleArr = moduleStr.split(",");
                for (int i = 0; i < moduleArr.length; i++) {
                    if (objectId.startsWith(moduleArr[i])) {
                        String moduleExc = (String)moduleInfo.get("PREFIX_EXCLUDE");
                        if (moduleExc != null && !"".equals(moduleExc)) {
                            String[] moduleErr = moduleExc.split(",");
                            int x = 0;
                            for (int j = 0; j < moduleErr.length; j++) {
                                if (objectId.startsWith(moduleErr[j])) {
                                    x++;
                                }
                            }
                            if (x == 0) {
                                moduleId = (String)moduleInfo.get("MODULE_ID");
                                return moduleId;
                            }
                        } else {
                            moduleId = (String)moduleInfo.get("MODULE_ID");
                            return moduleId;
                        }
                        // break;
                    }
                }
            }
        }
        return moduleId;
    }

    /**
     * 记录警报历史
     * 
     * @param delegator
     * @param alarmLists
     * @param alarmDataMap
     * @param triggerType
     * @return
     * @throws GenericEntityException Create on 2011-7-25 Update on 2011-7-25
     */
    public static String genenateAlarmInstanceNew(GenericDelegator delegator, List alarmLists,
            Map alarmDataMap, String triggerType) throws GenericEntityException {

        String alarmOcapId = "";

        try {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            for (Iterator it = alarmLists.iterator(); it.hasNext();) {
                Map alarm = (Map)it.next();
                alarm.put("ocapId", CommonUtil.getGuiMinusSeq("Alarm"));
                if (alarmDataMap != null) {
                    alarm.putAll(alarmDataMap);
                }

                alarmOcapId = alarmOcapId + ((Long)alarm.get("ocapId")).toString();
                alarm.put("transDate", now);
                alarm.put("triggerDate", now);
                alarm.put("ooc", "Y");
                alarm.put("oos", "N");

                Map alarmExt = new HashMap();
                alarmExt.put("ocapId", alarm.get("ocapId"));
                alarmExt.put("triggerType", triggerType);
                alarmExt.put("graphType", alarm.get("graphType"));
                alarm.remove("graphType");

                if ("0".equals(alarm.get("severity"))) {
                    alarm.put("alarmStatus",
                            Constants.ALARM_STATUS[Constants.ALARM_STATUS.length - 1]);
                } else {
                    alarm.put("alarmStatus", Constants.ALARM_STATUS[0]);
                    GenDCOPHelper.insertAlarmStatus(delegator, alarm);
                    GenDCOPHelper.insertAlarmStatusExt(delegator, alarmExt);

                }

                alarm.put("histSeq", CommonUtil.getGuiMinusSeq("AlarmHist"));
                alarmExt.put("histSeq", alarm.get("histSeq"));

                GenDCOPHelper.insertAlarmHist(delegator, alarm);
                GenDCOPHelper.insertAlarmHistExt(delegator, alarmExt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alarmOcapId;
    }

    /**
     * 插入警报历史
     * 
     * @param delegator
     * @param alarmMap Create on 2011-7-25 Update on 2011-7-25
     */
    public static void insertAlarmHist(GenericDelegator delegator, Map alarmMap) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            String sql = "INSERT INTO ALARM_HIST (OCAP_ID,ALARM_ID,ALARM_DESC,ALARM_OWNER,OBJECT_TYPE,OBJECT_ID,DCOP_ID,DCOP_ENT_DATE,"
                    + "SEVERITY,PRIORITY,TRIGGER_DATE,ALARM_CODE,ALARM_TEXT,ALARM_STATUS,TRANS_DATE,TRANS_COMMENT,TRANS_BY,OBJECT_DEPT,"
                    + "EAP_ALARM_ID,LOT_ID,OOS,STAGE_ID,EQP_ID,TRES_ROWDATA,ABNORMAL_FLAG,RECPID,LOCATION_ID,ALARM_SOURCE,OOC,HIST_SEQ) VALUES "
                    + " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            connection = ConnectionFactory.getConnection("localoracle");

            statement = connection.prepareStatement(sql);
            statement.setString(1, ((Long)alarmMap.get("ocapId")).toString());
            statement.setString(2, (String)alarmMap.get("alarmId"));
            statement.setString(3, (String)alarmMap.get("alarmDesc"));
            statement.setString(4, (String)alarmMap.get("alarmOwner"));
            statement.setString(5, (String)alarmMap.get("objectType"));
            statement.setString(6, (String)alarmMap.get("objectId"));
            statement.setString(7, (String)alarmMap.get("dcopId"));
            statement.setTimestamp(8, (java.sql.Timestamp)alarmMap.get("dcopEntDate"));
            statement.setString(9, (String)alarmMap.get("severity"));
            statement.setString(10, (String)alarmMap.get("priority"));
            statement.setTimestamp(11, (java.sql.Timestamp)alarmMap.get("triggerDate"));
            statement.setString(12, (String)alarmMap.get("alarmCode"));
            statement.setString(13, (String)alarmMap.get("alarmText"));
            statement.setString(14, (String)alarmMap.get("alarmStatus"));
            statement.setTimestamp(15, (java.sql.Timestamp)alarmMap.get("transDate"));
            statement.setString(16, (String)alarmMap.get("transComment"));
            statement.setString(17, (String)alarmMap.get("transBy"));
            statement.setString(18, (String)alarmMap.get("objectDept"));
            statement.setString(19, (String)alarmMap.get("eapAlarmId"));
            statement.setString(20, (String)alarmMap.get("lotId"));
            statement.setString(21, (String)alarmMap.get("oos"));
            statement.setString(22, (String)alarmMap.get("stageId"));
            statement.setString(23, (String)alarmMap.get("eqpId"));
            statement.setString(24, (String)alarmMap.get("tresRowdata"));
            statement.setString(25, (String)alarmMap.get("abnormalFlag"));
            statement.setString(26, (String)alarmMap.get("recpid"));
            statement.setString(27, (String)alarmMap.get("locationId"));
            statement.setString(28, (String)alarmMap.get("alarmSource"));
            statement.setString(29, (String)alarmMap.get("ooc"));
            statement.setString(30, ((Long)alarmMap.get("histSeq")).toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            Debug.logError(e, WorkflowService.module);
        } catch (Exception e) {
            Debug.logError(e, WorkflowService.module);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle, "Error closing statement in sequence util",
                        WorkflowService.module);
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle, "Error closing connection in sequence util",
                        WorkflowService.module);
            }
        }
    }

    /**
     * 插入警报历史
     * 
     * @param delegator
     * @param alarmMap Create on 2011-7-25 Update on 2011-7-25
     */
    public static void insertAlarmHistExt(GenericDelegator delegator, Map alarmMap) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            String sql = "INSERT INTO ALARM_HIST_EXT (OCAP_ID,TRIGGER_TYPE,GRAPH_TYPE,HIST_SEQ) VALUES "
                    + " (?,?,?,?)";
            connection = ConnectionFactory.getConnection("localoracle");

            statement = connection.prepareStatement(sql);
            statement.setString(1, ((Long)alarmMap.get("ocapId")).toString());
            statement.setString(2, (String)alarmMap.get("triggerType"));
            statement.setString(3, (String)alarmMap.get("graphType"));
            statement.setString(4, ((Long)alarmMap.get("histSeq")).toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            Debug.logError(e, WorkflowService.module);
        } catch (Exception e) {
            Debug.logError(e, WorkflowService.module);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle, "Error closing statement in sequence util",
                        WorkflowService.module);
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle, "Error closing connection in sequence util",
                        WorkflowService.module);
            }
        }
    }

    /**
     * 插入警报信息
     * 
     * @param delegator
     * @param alarmMap Create on 2011-7-25 Update on 2011-7-25
     */
    public static void insertAlarmStatus(GenericDelegator delegator, Map alarmMap) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            String sql = "INSERT INTO ALARM_STATUS (OCAP_ID,ALARM_ID,ALARM_DESC,ALARM_OWNER,OBJECT_TYPE,OBJECT_ID,DCOP_ID,DCOP_ENT_DATE,"
                    + "SEVERITY,PRIORITY,TRIGGER_DATE,ALARM_CODE,ALARM_TEXT,ALARM_STATUS,TRANS_DATE,TRANS_COMMENT,TRANS_BY,OBJECT_DEPT,"
                    + "EAP_ALARM_ID,LOT_ID,OOS,STAGE_ID,EQP_ID,TRES_ROWDATA,ABNORMAL_FLAG,RECPID,LOCATION_ID,ALARM_SOURCE,OOC) VALUES "
                    + " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            connection = ConnectionFactory.getConnection("localoracle");
            statement = connection.prepareStatement(sql);
            statement.setString(1, ((Long)alarmMap.get("ocapId")).toString());
            statement.setString(2, (String)alarmMap.get("alarmId"));
            statement.setString(3, (String)alarmMap.get("alarmDesc"));
            statement.setString(4, (String)alarmMap.get("alarmOwner"));
            statement.setString(5, (String)alarmMap.get("objectType"));
            statement.setString(6, (String)alarmMap.get("objectId"));
            statement.setString(7, (String)alarmMap.get("dcopId"));
            statement.setTimestamp(8, (java.sql.Timestamp)alarmMap.get("dcopEntDate"));
            statement.setString(9, (String)alarmMap.get("severity"));
            statement.setString(10, (String)alarmMap.get("priority"));
            statement.setTimestamp(11, (java.sql.Timestamp)alarmMap.get("triggerDate"));
            statement.setString(12, (String)alarmMap.get("alarmCode"));
            statement.setString(13, (String)alarmMap.get("alarmText"));
            statement.setString(14, (String)alarmMap.get("alarmStatus"));
            statement.setTimestamp(15, (java.sql.Timestamp)alarmMap.get("transDate"));
            statement.setString(16, (String)alarmMap.get("transComment"));
            statement.setString(17, (String)alarmMap.get("transBy"));
            statement.setString(18, (String)alarmMap.get("objectDept"));
            statement.setString(19, (String)alarmMap.get("eapAlarmId"));
            statement.setString(20, (String)alarmMap.get("lotId"));
            statement.setString(21, (String)alarmMap.get("oos"));
            statement.setString(22, (String)alarmMap.get("stageId"));
            statement.setString(23, (String)alarmMap.get("eqpId"));
            statement.setString(24, (String)alarmMap.get("tresRowdata"));
            statement.setString(25, (String)alarmMap.get("abnormalFlag"));
            statement.setString(26, (String)alarmMap.get("recpid"));
            statement.setString(27, (String)alarmMap.get("locationId"));
            statement.setString(28, (String)alarmMap.get("alarmSource"));
            statement.setString(29, (String)alarmMap.get("ooc"));
            statement.executeUpdate();
        } catch (SQLException e) {
            Debug.logError(e, WorkflowService.module);
            // System.out.println("*****************insertAlarmStatus!****************"
            // + e.getMessage());
        } catch (Exception e) {
            Debug.logError(e, WorkflowService.module);
            // System.out.println("*****************insertAlarmStatus!****************"
            // + e.getMessage());
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle, "Error closing statement in sequence util",
                        WorkflowService.module);
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle, "Error closing connection in sequence util",
                        WorkflowService.module);
            }
        }
    }

    /**
     * @param delegator
     * @param alarmMap Create on 2011-7-25 Update on 2011-7-25
     */
    public static void insertAlarmStatusExt(GenericDelegator delegator, Map alarmMap) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            String sql = "INSERT INTO ALARM_STATUS_EXT (OCAP_ID,TRIGGER_TYPE,GRAPH_TYPE) VALUES "
                    + " (?,?,?)";
            connection = ConnectionFactory.getConnection("localoracle");
            statement = connection.prepareStatement(sql);
            statement.setString(1, ((Long)alarmMap.get("ocapId")).toString());
            statement.setString(2, (String)alarmMap.get("triggerType"));
            statement.setString(3, (String)alarmMap.get("graphType"));
            statement.executeUpdate();
        } catch (SQLException e) {
            Debug.logError(e, WorkflowService.module);
            // System.out.println("*****************insertAlarmStatusExt!****************"
            // + e.getMessage());
        } catch (Exception e) {
            Debug.logError(e, WorkflowService.module);
            // System.out.println("*****************insertAlarmStatusExt!****************"
            // + e.getMessage());
        } finally {
            // SpcSystemConnector.closeConnection(connection, statement);
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle, "Error closing statement in sequence util",
                        WorkflowService.module);
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle, "Error closing connection in sequence util",
                        WorkflowService.module);
            }
        }
    }
}