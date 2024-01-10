package com.csmc.pms.webapp.period.help;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityWhereString;

import com.csmc.pms.webapp.db.SQLProcess;
import com.csmc.pms.webapp.db.SQLProcessException;
import com.csmc.pms.webapp.util.Constants;

public class PeriodHelper {

    public static final String module = PeriodHelper.class.getName();

    /**
     * 根据传递的参数保存或新建设备保养类型
     * 
     * @param delegator
     * @param paramMap
     *            设备保养类型
     * @throws Exception
     */
    public static void createDefaultPeriod(GenericDelegator delegator, Map paramMap) throws Exception {
        // 如果是新建画面，取得seqIndex(非sql自动生成)
        Long id = null;
        if (StringUtils.isEmpty(String.valueOf(paramMap.get("periodIndex")) )) {
            id = delegator.getNextSeqId("periodIndex");
            paramMap.put("periodIndex", id);
        }

        // 如果存在此机台信息，则取出赋值，否则新建赋值
        GenericValue gv = delegator.makeValidValue("DefaultPeriod", paramMap);
        delegator.createOrStore(gv);
    }

    /**
     * 根据periodIndex删除保养周期
     * 
     * @param delegator
     * @param periodIndex
     *            主键
     * @throws Exception
     */
    public static void delDefaultPeriod(GenericDelegator delegator, String periodIndex) throws Exception {
        // 删除机台参数
        delegator.removeByAnd("DefaultPeriod", UtilMisc.toMap("periodIndex", periodIndex));
    }

    /**
     * 取得画面显示时使用的颜色Map，供画面上使用
     * 
     * @param maintenancecolor1
     *            有完成保养的颜色
     * @param maintenancecolor2
     *            无完成保养的颜色
     * @param maintenancecolor3
     *            同一日期上有多重保养的颜色
     * @param background
     *            无保养的颜色
     * @return
     */
    public static Map getColorMap(String maintenancecolor1, String maintenancecolor2, String maintenancecolor3, String background) {
        Map colorMap = new HashMap();
        colorMap.put("background", background); // background color
        colorMap.put("todayBgColor", "666666"); // current day's background
                                                // color
        colorMap.put("cellwidth", "100"); // width of cell in pixels
        colorMap.put("holidyColor", "#FF4040"); // Holiday font color
        colorMap.put("todayColor", "#CCFFCC"); // Today's color
        colorMap.put("fontColor", "#000000"); // Font color
        colorMap.put("borderColor", "#000000"); // Border color
        colorMap.put("titleColor", "#000000"); // TableHeader color
        colorMap.put("titlebgColor", "#C0C0C0"); // TableBackgroundHeader
                                                    // color
        colorMap.put("maintenancecolor1", maintenancecolor1); // maintenance
                                                                // finished
                                                                // Color
        colorMap.put("maintenancecolor2", maintenancecolor2); // maintenance
                                                                // unfinished
                                                                // color
        colorMap.put("maintenancecolor3", maintenancecolor3);

        return colorMap;
    }

    /**
     * 取的所给年份每月的天数，作为数组返回
     * 
     * @param thisYear
     *            传入的年份
     * @return
     */
    public static int[] getMonthArr(int thisYear) {
        int[] monthdays = new int[13];
        monthdays[1] = 31;
        monthdays[2] = 28;
        monthdays[3] = 31;
        monthdays[4] = 30;
        monthdays[5] = 31;
        monthdays[6] = 30;
        monthdays[7] = 31;
        monthdays[8] = 31;
        monthdays[9] = 30;
        monthdays[10] = 31;
        monthdays[11] = 30;
        monthdays[12] = 31;

        if (((thisYear % 4 == 0) && (thisYear % 100 != 0)) || (thisYear % 400 == 0)) {
            monthdays[2] = 29;
        } else {
            monthdays[2] = 28;
        }

        return monthdays;
    }

    /**
     * 取得星期的title显示
     * 
     * @return
     */
    public static String[] getWeekArr() {
        String[] daystitle = new String[8];
        daystitle[1] = "日";
        daystitle[2] = "一";
        daystitle[3] = "二";
        daystitle[4] = "三";
        daystitle[5] = "四";
        daystitle[6] = "五";
        daystitle[7] = "六";

        return daystitle;
    }

    /**
     * 取得当前时间对应的color
     * 
     * @param listSize
     *            当天存在的保养数量
     * @param finishedListSize
     *            当天完成的保养数量
     * @param backgroup
     * @param maintenancecolor1
     * @param maintenancecolor2
     * @param maintenancecolor3
     * @return
     */
    public static String getTimeColor(int listSize, int finishedListSize, String backgroup, String maintenancecolor1, String maintenancecolor2, String maintenancecolor3) {
        // 显示颜色
        String tdColor;
        if (listSize == 0) {
            tdColor = backgroup;
        } else {
            if (finishedListSize != 0) {
                tdColor = maintenancecolor1;
            } else {
                if (listSize == 1) {
                    tdColor = maintenancecolor2;
                } else {
                    tdColor = maintenancecolor3;
                }
            }
        }
        return tdColor;
    }

    /**
     * 得到今天拥有的所有保养字符串
     * 
     * @param delegator
     * @param equipmentScheduleList
     * @return
     * @throws GenericEntityException
     */
    public static StringBuffer getPmPeriodStr(GenericDelegator delegator, List equipmentScheduleList) throws GenericEntityException {
        Iterator equipmentScheduleIter = equipmentScheduleList.iterator();
        StringBuffer periodStr = new StringBuffer("");
        while (equipmentScheduleIter.hasNext()) {
            String periodId = ((GenericValue) equipmentScheduleIter.next()).getString("periodIndex");
            GenericValue gv = delegator.findByPrimaryKey("DefaultPeriod", UtilMisc.toMap("periodIndex", periodId));
            if (gv!= null){
            	periodStr.append(gv.getString("periodName"));
            
	            if (equipmentScheduleIter.hasNext()) {
	                periodStr.append(",");
	            }
            }
        }
        return periodStr;
    }

    /**
     * 得到今天拥有的所有巡检字符串
     * 
     * @param delegator
     * @param equipmentScheduleList
     * @return
     * @throws GenericEntityException
     */
    public static StringBuffer getPcPeriodStr(GenericDelegator delegator, List equipmentScheduleList) throws GenericEntityException {
        Iterator equipmentScheduleIter = equipmentScheduleList.iterator();
        StringBuffer periodStr = new StringBuffer("");
        while (equipmentScheduleIter.hasNext()) {
            String periodId = ((GenericValue) equipmentScheduleIter.next()).getString("periodIndex");
            GenericValue gv = delegator.findByPrimaryKey("PcPeriod", UtilMisc.toMap("periodIndex", periodId));
            periodStr.append(gv.getString("periodName"));
            if (equipmentScheduleIter.hasNext()) {
                periodStr.append(",");
            }
        }
        return periodStr;
    }

    /**
     * 保养周期设定中 设定timeMap， periodMap，creatorMap，isFinishedMap 四个Map
     *
     * @param delegator
     * @param equipmentId
     * @param periodIndex
     * @param thisYear
     * @param maintenancecolor1
     * @param maintenancecolor2
     * @param maintenancecolor3
     * @param background
     * @param monthdays
     * @param day
     * @param month
     * @param timeMap
     * @param periodMap
     * @param creatorMap
     * @param isFinishedMap
     * @throws GenericEntityException
     */
    public static void setPmTimeMap(GenericDelegator delegator, String equipmentId, String periodIndex, int thisYear, String maintenancecolor1, String maintenancecolor2, String maintenancecolor3,
            String background, int[] monthdays, Calendar day, int month, Map timeMap, Map periodMap, Map creatorMap, Map isFinishedMap, Map timeRangeMap) throws GenericEntityException {
        for (int j = 1; j <= monthdays[day.get(Calendar.MONTH) + 1]; j++) {
            String tdColor; // 页面颜色
            String creator = ""; // 保养日期的建立者
            String timeRangeIndex = ""; //日保养的TimeRange
            Boolean isFinished = Boolean.FALSE; // 是否完成

            // 是否有保养LISt,是否有完成保养List
            List equipmentScheduleList = new LinkedList();
            List equipmentScheduleFinishList = new LinkedList();

            // 设定日期
            Calendar cal = Calendar.getInstance();
            cal.set(day.get(Calendar.YEAR), day.get(Calendar.MONTH), j);
            Date date = cal.getTime();
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            String sdf = sdfDate.format(date);

            // 取得当天的数量，当天已建立的保养数量
            if (!StringUtils.isEmpty(periodIndex)) {
                equipmentScheduleList = delegator.findByAnd("EquipmentSchedule", UtilMisc.toMap("periodIndex", periodIndex, "equipmentId", equipmentId, "scheduleDate", new java.sql.Date(date
                        .getTime())));

                EntityWhereString con = new EntityWhereString("schedule_Date = to_date('" + sdf + "','yyyy-MM-dd') and period_index='" + periodIndex + "' and equipment_Id = '" + equipmentId
                        + "'  and event_index is not null");
                equipmentScheduleFinishList = delegator.findByCondition("EquipmentSchedule", con, null, null);

            } else {
                equipmentScheduleList = delegator.findByAnd("EquipmentSchedule", UtilMisc.toMap("equipmentId", equipmentId, "scheduleDate", new java.sql.Date(date.getTime())));
                EntityWhereString con = new EntityWhereString("schedule_Date = to_date('" + sdf + "','yyyy-MM-dd') and equipment_Id ='" + equipmentId + "' and  event_index is not null");
                equipmentScheduleFinishList = delegator.findByCondition("EquipmentSchedule", con, null, null);
            }

            // 取得时间对应的颜色
            tdColor = PeriodHelper.getTimeColor(equipmentScheduleList.size(), equipmentScheduleFinishList.size(), background, maintenancecolor1, maintenancecolor2, maintenancecolor3);
            
            // 对于宕机后被锁定的保养计划标记红色
            for (int i = 0; i < equipmentScheduleFinishList.size(); i++) {
            	GenericValue gv = (GenericValue) equipmentScheduleFinishList.get(i);
            	String eventIndex = gv.getString("eventIndex");
            	if (eventIndex.length() >= 8) { // event_index是8位的宕机日期
            		tdColor = "#C1121C"; // 标记红色
            	}
            }
            
            timeMap.put(String.valueOf(thisYear) + String.valueOf(month) + String.valueOf(j), tdColor);

            // 日期对应的建立者
            if (!StringUtils.isEmpty(periodIndex)) {
                if (equipmentScheduleList.size() != 0) {
                    GenericValue esgv = (GenericValue) equipmentScheduleList.get(0);
                    creator = (String) esgv.getString("creator");
                }
            }
            creatorMap.put(String.valueOf(thisYear) + String.valueOf(month) + String.valueOf(j), creator);

            //日期对应的TimeRange(日保养)
            if (!StringUtils.isEmpty(periodIndex)) {
                if (equipmentScheduleList.size() != 0) {
                    GenericValue esgv = (GenericValue) equipmentScheduleList.get(0);
                    Long index = (Long) esgv.getLong("timeRangeIndex");
                    if(index != null)  timeRangeIndex = String.valueOf(index);
                }
            }
            timeRangeMap.put(String.valueOf(thisYear) + String.valueOf(month) + String.valueOf(j), timeRangeIndex);
            
            // 日期对应是否有完成表单
            if (equipmentScheduleFinishList.size() > 0) {
                isFinished = Boolean.TRUE;
            }

            isFinishedMap.put(String.valueOf(thisYear) + String.valueOf(month) + String.valueOf(j), isFinished);

            // 时间对应日历包含的保养类型
            StringBuffer periodStr = PeriodHelper.getPmPeriodStr(delegator, equipmentScheduleList);
            if (periodStr != null)
            {	
            	periodMap.put(String.valueOf(thisYear) + String.valueOf(month) + String.valueOf(j), periodStr.toString());
            }else
            {
            	periodMap.put(String.valueOf(thisYear) + String.valueOf(month) + String.valueOf(j),"");
            }

        }
    }

    /**
     * 巡检日期设定中 设定timeMap， periodMap，creatorMap，isFinishedMap 四个Map
     * 
     * @param delegator
     * @param styleIndex
     * @param periodIndex
     * @param thisYear
     * @param maintenancecolor1
     * @param maintenancecolor2
     * @param maintenancecolor3
     * @param background
     * @param monthdays
     * @param day
     * @param month
     * @param timeMap
     * @param periodMap
     * @param creatorMap
     * @param isFinishedMap
     * @throws GenericEntityException
     */
    public static void setPcTimeMap(GenericDelegator delegator, String styleIndex, String periodIndex, int thisYear, String maintenancecolor1, String maintenancecolor2, String maintenancecolor3,
            String background, int[] monthdays, Calendar day, int month, Map timeMap, Map periodMap, Map creatorMap, Map isFinishedMap) throws GenericEntityException {
        for (int j = 1; j <= monthdays[day.get(Calendar.MONTH) + 1]; j++) {
            String tdColor; // 页面颜色
            String creator = ""; // 巡检日期的建立者
            Boolean isFinished = Boolean.FALSE; // 是否完成

            // 是否有巡检LISt,是否有完成巡检List
            List pcScheduleList = new LinkedList();
            List pcScheduleFinishList = new LinkedList();

            // 设定日期
            Calendar cal = Calendar.getInstance();
            cal.set(day.get(Calendar.YEAR), day.get(Calendar.MONTH), j);
            Date date = cal.getTime();
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
            String sdf = sdfDate.format(date);

            // 取得当天的数量，当天已建立的保养数量
            if (!StringUtils.isEmpty(periodIndex)) {
                pcScheduleList = delegator.findByAnd("PeriodSchedule", UtilMisc.toMap("periodIndex", periodIndex, "pcStyleIndex", styleIndex, "scheduleDate", new java.sql.Date(date.getTime())));

                EntityWhereString con = new EntityWhereString("schedule_Date = to_date('" + sdf + "','yyyy-MM-dd') and period_index='" + periodIndex + "' and pc_Style_Index = '" + styleIndex
                        + "'  and event_index is not null");
                pcScheduleFinishList = delegator.findByCondition("PeriodSchedule", con, null, null);

            } else {
                pcScheduleList = delegator.findByAnd("PeriodSchedule", UtilMisc.toMap("pcStyleIndex", styleIndex, "scheduleDate", new java.sql.Date(date.getTime())));
                EntityWhereString con = new EntityWhereString("schedule_Date = to_date('" + sdf + "','yyyy-MM-dd') and pc_Style_Index ='" + styleIndex + "' and  event_index is not null");
                pcScheduleFinishList = delegator.findByCondition("PeriodSchedule", con, null, null);
            }

            // 取得时间对应的颜色
            tdColor = PeriodHelper.getTimeColor(pcScheduleList.size(), pcScheduleFinishList.size(), background, maintenancecolor1, maintenancecolor2, maintenancecolor3);
            timeMap.put(String.valueOf(thisYear) + String.valueOf(month) + String.valueOf(j), tdColor);

            // 日期对应的建立者
            if (!StringUtils.isEmpty(periodIndex)) {
                if (pcScheduleList.size() != 0) {
                    GenericValue esgv = (GenericValue) pcScheduleList.get(0);
                    creator = (String) esgv.getString("creator");
                }
            }
            creatorMap.put(String.valueOf(thisYear) + String.valueOf(month) + String.valueOf(j), creator);

            // 日期对应是否有完成表单
            if (pcScheduleFinishList.size() > 0) {
                isFinished = Boolean.TRUE;
            }

            isFinishedMap.put(String.valueOf(thisYear) + String.valueOf(month) + String.valueOf(j), isFinished);

            // 时间对应日历包含的巡检类型
            StringBuffer periodStr = PeriodHelper.getPcPeriodStr(delegator, pcScheduleList);
            periodMap.put(String.valueOf(thisYear) + String.valueOf(month) + String.valueOf(j), periodStr.toString());

        }
    }

    /**
     * 得到月份的中文汉字
     * 
     * @param theDate
     * @return
     */
    public static String getMonthStr(Calendar theDate) {
        int month;
        String result = "";
        month = theDate.get(Calendar.MONTH);
        switch (month) {
        case 0:
            result = "一月";
            break;
        case 1:
            result = "二月";
            break;
        case 2:
            result = "三月";
            break;
        case 3:
            result = "四月";
            break;
        case 4:
            result = "五月";
            break;
        case 5:
            result = "六月";
            break;
        case 6:
            result = "七月";
            break;
        case 7:
            result = "八月";
            break;
        case 8:
            result = "九月";
            break;
        case 9:
            result = "十月";
            break;
        case 10:
            result = "十一月";
            break;
        case 11:
            result = "十二月";
            break;

        default:
            ;
        }

        return result + " " + theDate.get(Calendar.YEAR);
    }

    /**
     * 保养日期的设定
     * 
     * @param delegator
     * @param equipmentId
     * @param date
     * @param periodIndex
     * @param dayPmPeriod
     * @param user
     * @throws Exception
     * 20071226 laol 改变Sequence的取值方式，采用Oracle的Sequence
     */
    public static void pmDaySet(GenericDelegator delegator, String equipmentId, Date date, String periodIndex, String user,String timeRangeIndex) throws Exception {
        Map paramMap = new HashMap();
        paramMap.put("equipmentId", equipmentId);
        paramMap.put("periodIndex", Long.valueOf(periodIndex));
        paramMap.put("scheduleDate", new java.sql.Date(date.getTime()));
        paramMap.put("creator", user);
        paramMap.put("scheduleEvent", "PM");
        paramMap.put("createDate", new Timestamp(System.currentTimeMillis()));
        if(StringUtils.isNotEmpty(timeRangeIndex)){
        	paramMap.put("timeRangeIndex", Long.valueOf(timeRangeIndex));
        }

        Long id = null;
        
        List equipmentScheduleList = delegator
                .findByAnd("EquipmentSchedule", UtilMisc.toMap("equipmentId", equipmentId, "periodIndex", periodIndex, "scheduleDate", new java.sql.Date(date.getTime())));
        if (equipmentScheduleList.size() == 0) {
        	String sql = "select seq_pm_schedule_index.nextval id from dual";
            List list = SQLProcess.excuteSQLQuery(sql, delegator);
            if (list.size() > 0) {
            	Map map = (Map)list.get(0);
            	id = Long.valueOf(String.valueOf(map.get("ID")));
            }
           
            paramMap.put("scheduleIndex", id);
        } else {
            GenericValue gv = (GenericValue) equipmentScheduleList.get(0);
            id = gv.getLong("scheduleIndex");
            paramMap.put("scheduleIndex", id);
        }

        GenericValue gv = delegator.makeValidValue("EquipmentSchedule", paramMap);
        delegator.createOrStore(gv);
    }

    /**
     * 巡检日期的设定
     * 
     * @param delegator
     * @param equipmentId
     * @param date
     * @param periodIndex
     * @param dayPmPeriod
     * @param user
     * @throws Exception
     */
    public static void pcDaySet(GenericDelegator delegator, String styleIndex, Date date, String periodIndex, String user) throws Exception {
        Map paramMap = new HashMap();
        paramMap.put("pcStyleIndex", Long.valueOf(styleIndex));
        paramMap.put("periodIndex", Long.valueOf(periodIndex));
        paramMap.put("scheduleDate", new java.sql.Date(date.getTime()));
        paramMap.put("creator", user);
        paramMap.put("scheduleEvent", "PC");
        paramMap.put("createDate", new Timestamp(System.currentTimeMillis()));

        Long id = null;
        List pcScheduleList = delegator.findByAnd("PeriodSchedule", UtilMisc.toMap("pcStyleIndex", styleIndex, "periodIndex", periodIndex, "scheduleDate", new java.sql.Date(date.getTime())));
        if (pcScheduleList.size() == 0) {
            id = delegator.getNextSeqId("pcScheduleIndex");
            paramMap.put("scheduleIndex", id);
        } else {
            GenericValue gv = (GenericValue) pcScheduleList.get(0);
            id = gv.getLong("scheduleIndex");
            paramMap.put("scheduleIndex", id);
        }

        GenericValue gv = delegator.makeValidValue("PeriodSchedule", paramMap);
        delegator.createOrStore(gv);
    }

    /**
     * 保养日期的删除
     * 
     * @param delegator
     * @param equipmentId
     * @param scheduleDate
     * @param periodIndex
     * @param dayPmPeriod
     * @param user
     * @throws Exception
     */
    public static void pmDayReset(GenericDelegator delegator, String equipmentId, Date scheduleDate, String periodIndex, String user) throws Exception {
        Map paramMap = new HashMap();
        paramMap.put("equipmentId", equipmentId);
        paramMap.put("periodIndex", periodIndex);
        paramMap.put("scheduleDate", new java.sql.Date(scheduleDate.getTime()));
        //paramMap.put("creator", user);

        Long id = null;
        List equipmentScheduleList = delegator.findByAnd("EquipmentSchedule", paramMap);
        if (equipmentScheduleList.size() != 0) {
            GenericValue gv = (GenericValue) equipmentScheduleList.get(0);            
            id = gv.getLong("scheduleIndex");
            
            if(StringUtils.isEmpty(gv.getString("eventIndex"))){
            	Map delMap = gv.getAllFields(); 
            	delMap.put("deleteBy", user);
            	delMap.put("deleteDate", new Timestamp(System.currentTimeMillis()));
            	GenericValue delGv = delegator.makeValidValue("EquipmentScheduleDel", delMap);
            	delegator.create(delGv);//保留历史记录
                delegator.removeByAnd("EquipmentSchedule", UtilMisc.toMap("scheduleIndex", id));
            }
        }
    }

    /**
     * 巡检日期的删除
     * 
     * @param delegator
     * @param equipmentId
     * @param date
     * @param periodIndex
     * @param dayPmPeriod
     * @param user
     * @throws Exception
     */
    public static void pcDayReset(GenericDelegator delegator, String styleIndex, Date date, String periodIndex, String user) throws Exception {
        Map paramMap = new HashMap();
        paramMap.put("pcStyleIndex", styleIndex);
        paramMap.put("periodIndex", periodIndex);
        paramMap.put("scheduleDate", new java.sql.Date(date.getTime()));
        paramMap.put("creator", user);

        Long id = null;
        List pcScheduleList = delegator.findByAnd("PeriodSchedule", UtilMisc.toMap("pcStyleIndex", styleIndex, "periodIndex", periodIndex, "scheduleDate", new java.sql.Date(date.getTime())));
        if (pcScheduleList.size() != 0) {
            GenericValue gv = (GenericValue) pcScheduleList.get(0);
            id = gv.getLong("scheduleIndex");
            if(StringUtils.isEmpty(gv.getString("eventIndex"))){            
                delegator.removeByAnd("PeriodSchedule", UtilMisc.toMap("scheduleIndex", id));
            }
        }
    }

    /**
     * 根据传递的参数保存或新建巡检类型
     * 
     * @param delegator
     * @param paramMap
     *            巡检类型
     * @throws Exception
     */
    public static void createPcPeriod(GenericDelegator delegator, Map paramMap) throws Exception {
        Long id = null;
        if (StringUtils.isEmpty((String) paramMap.get("periodIndex"))) {
            id = delegator.getNextSeqId("periodIndex");
            paramMap.put("periodIndex", id);
        }

        // 如果存在此巡检信息，则取出赋值，否则新建赋值
        GenericValue gv = delegator.makeValidValue("PcPeriod", paramMap);

        delegator.createOrStore(gv);
    }

    /**
     * 根据periodIndex删除巡检周期
     * 
     * @param delegator
     * @param periodIndex
     *            主键
     * @throws Exception
     */
    public static void delPcPeriod(GenericDelegator delegator, String periodIndex) throws Exception {
        delegator.removeByAnd("PcPeriod", UtilMisc.toMap("periodIndex", periodIndex));
    }
    
    /**
     * 检查保养行事历设定
     * @param delegator
     * @param periodIndex
     * @return
     * @throws Exception
     */
    public static boolean checkPmPeriod(GenericDelegator delegator, String periodIndex) throws Exception {
    	String sql = "select count(*) num from equipment_schedule where period_index = " + periodIndex;
    	try {
    		List list = SQLProcess.excuteSQLQuery(sql, delegator);
    		Map map = (Map)list.get(0);
    		int num = Integer.valueOf((String)map.get("NUM")).intValue();
    		if(num > 0) return true;
    	} catch(SQLProcessException e) {
    		
    	}
    	return false;
    }
    
    /**
     * 检查行事历是否有巡检样式
     * @param delegator
     * @param periodIndex
     * @return
     * @throws Exception
     */
    public static boolean checkPcPeriod(GenericDelegator delegator, String periodIndex) throws Exception {
    	String sql = "select count(*) num from period_schedule where period_index = " + periodIndex;
    	try {
    		List list = SQLProcess.excuteSQLQuery(sql, delegator);
    		Map map = (Map)list.get(0);
    		int num = Integer.valueOf((String)map.get("NUM")).intValue();
    		if(num > 0) return true;
    	} catch(SQLProcessException e) {
    		
    	}
    	return false;
    }
    
    /**
     * 根据最后一天的PM，加上基本天数，生成在制定年度内需要设定的PM
     * @param accountNo 用户名
     * @param delegator 
     * @param year 生成PM的年份
     * @param equipmentId 设备ID
     * @param defaultPeriod 生成年度PM的保养类型
     * @param periodIndex 保养类型的Index
     * @param list 最后一天完成此保养的日期
     * @throws ParseException
     * @throws Exception
     */
    public static void yearPmSet(String accountNo, GenericDelegator delegator, int year, String equipmentId, GenericValue defaultPeriod, String periodIndex, List list) throws ParseException, Exception {
        if(list.size() > 0) {
            Map map=(Map)list.get(0);
            String lastDate=(String)map.get("SCHEDULE_DATE");
            String timeRangeIndex ="";
            if (map.get("TIME_RANGE_INDEX") != null)
            {
                timeRangeIndex = (String)map.get("TIME_RANGE_INDEX");
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");                        
            if (lastDate != null)
            {
                Date date=formatter.parse(lastDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                while (cal.get(Calendar.YEAR) <= year) {
                    if (!StringUtils.isEmpty(defaultPeriod.getString("defaultDays")) && Integer.parseInt(defaultPeriod.getString("defaultDays")) != 0) {
                        if (cal.get(Calendar.YEAR) == year){                            
                            PeriodHelper.pmDaySet(delegator, equipmentId, cal.getTime(), periodIndex, accountNo, timeRangeIndex);
                        }
                        cal.add(Calendar.DATE, Integer.parseInt(defaultPeriod.getString("defaultDays")));
                    } else {
                        break;
                    }

                }
            }
        }
    }
    
    /**
     * 检查保养计划删除后，前后2个保养计划是否符合日期保养规范
     * @param delegator
     * @param equipmentId
     * @param periodIndex
     * @param scheduleDate
     * @return
     * @throws Exception
     */
    public static String equipmentScheduleClearEnabled(GenericDelegator delegator, String equipmentId, String periodIndex, String scheduleDate) throws Exception {
    	//子腔体无需校验
    	if (isSonEqp(delegator, equipmentId)) {
    		return Constants.Y;
    	}
    	
    	GenericValue defaultPeriod = delegator.findByPrimaryKey("DefaultPeriod", UtilMisc.toMap("periodIndex", periodIndex));
        String defaultDays = defaultPeriod.getString("defaultDays");
        String warningDays = defaultPeriod.getString("warningDays");  
    	
        if (defaultDays != null && Integer.parseInt(defaultDays) > Integer.parseInt(Constants.MAX_DAYPM_DEFAULT_DAYS)) {
	    	String sql = "select t.next_schedule_date-t.last_schedule_date date_interval, "
				+ "		 t.default_days+t.warning_days std_interval"
				+ " from ("
				+ "	    select t1.schedule_date, t1.period_index, t1.event_index,"
				+ "			t2.period_name, t2.period_desc, t2.default_days, t2.warning_days, "
				+ "			lag(t1.schedule_date,1,null) over ( order by t1.schedule_date ) last_schedule_date," 
				+ "			lead(t1.schedule_date,1,null) over ( order by t1.schedule_date )  next_schedule_date"
				+ "		from equipment_schedule t1, default_period t2"
				+ "		where t1.period_index = t2.period_index"
				+ "			   and t2.default_days > " + Constants.MAX_DAYPM_DEFAULT_DAYS
				+ "			   and t1.equipment_id = '" + equipmentId + "'"
				+ "			   and t1.schedule_date >= to_date('" + scheduleDate + "', 'yyyy-mm-dd') - " + defaultDays + "-" + warningDays
				+ "			   and t1.schedule_date <= to_date('" + scheduleDate + "', 'yyyy-mm-dd') + " + defaultDays + "+" + warningDays
				+ "			   and t2.default_days >= " + defaultDays
				+ "		 ) t"
				+ "	where t.schedule_date = to_date('" + scheduleDate + "', 'yyyy-mm-dd') and t.period_index = '" + periodIndex + "'"
				+ "		and t.next_schedule_date-t.last_schedule_date <= t.default_days+t.warning_days"
				+ "     and t.event_index is null";
	    				
	    	try {
	    		List list = SQLProcess.excuteSQLQuery(sql, delegator);
	    		if(list != null && list.size() > 0) { 
	    			return Constants.Y;
	    		}
	    	} catch(SQLProcessException e) {
	    		Debug.logError(e, module);
	    	}
        } else {
        	//不定期保养和日保养 不限制删除
        	return Constants.Y;
        }
        
    	return Constants.N;
    }
    
    /**
     * 检查保养计划 单个设定 是否在 警示天数范围内存在原来的计划
     * @param delegator
     * @param equipmentId
     * @param periodIndex
     * @param scheduleDate
     * @return
     * @throws Exception
     */
    public static String equipmentScheduleAddEnabled(GenericDelegator delegator, String equipmentId, String periodIndex, String scheduleDate) throws Exception {
    	//Fab1不校验
    	if (true == Constants.CALL_TP_FLAG) {
    		return Constants.Y;
    	}
    	
    	//子腔体无需校验
    	if (isSonEqp(delegator, equipmentId)) {
    		return Constants.Y;
    	}
    	
    	GenericValue defaultPeriod = delegator.findByPrimaryKey("DefaultPeriod", UtilMisc.toMap("periodIndex", periodIndex));
        String defaultDays = defaultPeriod.getString("defaultDays");
        String warningDays = defaultPeriod.getString("warningDays");  
    	
        if (defaultDays != null && Integer.parseInt(defaultDays) > Integer.parseInt(Constants.MAX_DAYPM_DEFAULT_DAYS)) {
	    	String sql = "select t1.schedule_date, t1.period_index, t1.event_index,"
				+ "		 t2.period_name, t2.period_desc, t2.default_days, t2.warning_days "
				+ " from equipment_schedule t1, default_period t2"
				+ " where t1.period_index = t2.period_index"
				+ "		and t2.default_days > " + Constants.MAX_DAYPM_DEFAULT_DAYS
				+ "		and t1.equipment_id = '" + equipmentId + "'"
				+ "		and t1.schedule_date >= to_date('" + scheduleDate + "', 'yyyy-mm-dd') - " + warningDays
				+ "		and t1.schedule_date <= to_date('" + scheduleDate + "', 'yyyy-mm-dd') + " + warningDays
				+ "		and t2.default_days >= " + defaultDays;
	    				
	    	try {
	    		List list = SQLProcess.excuteSQLQuery(sql, delegator);
	    		if (list != null && list.size() > 0) {
					return Constants.Y;
				}
	    	} catch(SQLProcessException e) {
	    		Debug.logError(e, module);
	    	}
        } else {
        	//不定期保养和日保养 不限制新增
        	return Constants.Y;
        }
        
    	return Constants.N;
    }

    //判断是否为子腔体
	private static boolean isSonEqp(GenericDelegator delegator, String equipmentId) throws GenericEntityException {
		GenericValue gvEqp = delegator.findByPrimaryKey("Equipment", UtilMisc.toMap("equipmentId", equipmentId));
    	if (StringUtils.isNotEmpty(gvEqp.getString("parentEqpid"))) {
    		return true;
        } else {
        	return false;
        }
	}
    
    /**
     * 更新PmNextStarttime
     * @param delegator
     * @param paramMap
     *            periodIndex,defaultDays,warningDays,transBy
     * @throws Exception
     */
    public static void updatePmNextStarttime(GenericDelegator delegator, Map paramMap) throws Exception {
    	String periodIndex = String.valueOf(paramMap.get("periodIndex"));
    	String defaultDays = String.valueOf(paramMap.get("defaultDays"));
    	String warningDays = String.valueOf(paramMap.get("warningDays"));
    	String transBy = String.valueOf(paramMap.get("transBy"));
    	
    	int iDefaultDays = Integer.parseInt(defaultDays);
    	int iWarningDays = Integer.parseInt(warningDays);
    	if (iDefaultDays > Long.parseLong(Constants.MAX_DAYPM_DEFAULT_DAYS)) {
	        List list = delegator.findByAnd("PmNextStarttime", UtilMisc.toMap("periodIndex", periodIndex));
	        for (int i = 0; i < list.size(); i++) {
	            GenericValue gv = (GenericValue) list.get(i);
	            Timestamp lastPmDate = gv.getTimestamp("lastPmDate");
	            Calendar calendar = Calendar.getInstance();
	            calendar.setTimeInMillis(lastPmDate.getTime());            
	            calendar.add(Calendar.DAY_OF_YEAR, iDefaultDays + iWarningDays);
	            Timestamp nextPmDate = new Timestamp(calendar.getTimeInMillis());
	            gv.set("nextPmDate", new java.sql.Date(nextPmDate.getTime()));
	            gv.set("transBy", transBy);
	            delegator.store(gv);
	        }
    	}
    }

    //查询签核中的保养计划延期申请
	public static List getPmDelaySubmitList(GenericDelegator delegator,
			String equipmentId, String periodIndex, String scheduleDate) throws SQLProcessException {
		String strSQL = "select s.object_name,s.creator_name,s.create_time,s.owner,s.status,s.owner_process,s.status_process"
			+ " from wf_submit s, equipment_schedule e, default_period d"
			+ " where s.object_index = e.schedule_index and e.period_index=d.period_index"
			+ " and s.object = '" + Constants.SUBMIT_PM_DELAY + "'"
			+ " and (s.status, s.status_process) in (('SUBMITED', 'SUBMITED'), ('SUBMITED', 'APPROVED'), ('APPROVED', 'SUBMITED'))"
			+ " and e.equipment_id = '" + equipmentId + "' and e.period_index=" + periodIndex;
		
		if (StringUtils.isNotEmpty(scheduleDate)) {
			strSQL = strSQL + " and e.schedule_date = to_date('" + scheduleDate + "', 'yyyy-mm-dd')";
		}
		
		strSQL = strSQL + " order by s.create_time";
		List wfSubmitList = SQLProcess.excuteSQLQuery(strSQL, delegator);
		return wfSubmitList;
	}
	
	//提交保养计划延期申请
	//校验以申请的新计划日期为基准，往前或往后推算一个周期+警示天数，都至少有一个同样的计划，这样的可直接修改生效
	public static boolean isNewScheduleValid(GenericDelegator delegator, String equipmentId, String periodIndex,
			String oldScheduleIndex, String newScheduleDate, String defaultDays, String warningDays) throws SQLProcessException {
		String sql1 = "select count(*) count_no from equipment_schedule t"
			+ " where t.equipment_id = '" + equipmentId + "' and t.period_index = " + periodIndex
			+ " and t.schedule_index <> " + oldScheduleIndex;
		
		String scheduleDate = "to_date('" + newScheduleDate + "', 'yyyy-mm-dd')";
		String unionSql = sql1 + " and t.schedule_date >= " + scheduleDate + "-" + defaultDays+ "-" + warningDays
			+ " and t.schedule_date < " + scheduleDate
			+ " union all "
			+ sql1 + " and t.schedule_date > " + scheduleDate
			+ " and t.schedule_date <= " + scheduleDate + "+" + defaultDays+ "+" + warningDays;
		
		String sql = "select * from (" + unionSql + ") where count_no>=1";		
	    List list = SQLProcess.excuteSQLQuery(sql, delegator);
	    if (list.size() < 2) {
	    	return false;
	    }
		return true;
	}
}
