package com.csmc.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

public class DateUtilTest extends TestCase {
 /**
	 * Logger for this class
	 */
 private static final Logger logger = Logger.getLogger( DateUtilTest.class );
 
 DateUtil dateUtil;
 protected void setUp() throws Exception {
  logger.debug( "setUp() - start" );
  
  super.setUp();
  dateUtil = new DateUtil();
  
  logger.debug( "setUp() - end" );
 }
 
 
 protected void tearDown() throws Exception {
  logger.debug( "tearDown() - start" );
  
  super.tearDown();
  
  logger.debug( "tearDown() - end" );
 }
 
 /**
	 * 获取2个字符日期的天数差
	 * 
	 * @param p_startDate
	 * @param p_endDate
	 * @return 天数差
	 * @author zhuqx
	 */
 public void testGetDayOfTowDiffDate() throws ParseException {
  logger.debug( "testGetDayOfTowDiffDate() - start" );
  
  String startDate = "2005-05-01";
  String endDate = "2006-09-30";
  long day = dateUtil.getDaysOfTowDiffDate( startDate,endDate );
  logger.debug( "day=========" + day);
  logger.debug( "week=========" + day/7);
  logger.debug( "month=========" + day/30);
  logger.debug( "year=========" + day/365);
  logger.debug( "testGetDayOfTowDiffDate() - end" );
 }
 /**
	 * 获取字符日期一个月的天数
	 * 
	 * @param p_date
	 * @return 天数
	 * @author zhuqx
	 */
 public void testGetDayOfMonth() throws ParseException {
  logger.debug( "testGetDayOfTowDiffDate() - start" );
  
  String l_date = "2006-02-01";
  String l_format = "yyyy-MM-dd";
  long day = dateUtil.getDayOfMonth( dateUtil.toUtilDateFromStrDateByFormat( l_date,l_format ) );
  logger.debug( "day=========" + day);
  logger.debug( "testGetDayOfTowDiffDate() - end" );
 }
 
 // -------------------------------日期转换---------------------------------------------------------------------------
 /**
	 * 字符串型转化util.Date
	 * 
	 * @Param: p_strDate 字符串型日期
	 * @Return: java.util.Date util.Date
	 * @Throws: ParseException
	 * @Author: zhuqx
	 * @Date: 2006-10-31
	 */
 public void testToUtilDateByFormat() throws ParseException {
  logger.debug( "testGetDayOfTowDiffDate() - start" );
  
  String l_date = "2005-05-01";
  Date day = dateUtil.toUtilDateFromStrDateByFormat( l_date,"yyyy-MM-dd" );
  logger.debug( "java.util.Date =========" + day);
  
  logger.debug( "testGetDayOfTowDiffDate() - end" );
 }
 /**
	 * 字符型日期转化成sql.Date型日期
	 * 
	 * @param p_strDate
	 *            字符型日期
	 * @return java.sql.Date sql.Date型日期
	 * @throws ParseException
	 * @author shizhuoyang
	 */
 public void testToSqlDatet() throws ParseException {
  logger.debug( "testToSqlDatet() - start" );
  
  String l_date = "2005-05-01";
  Date day = dateUtil.toSqlDateFromStrDate( l_date );
  logger.debug( "java.sql.Date=========" + day);
  
  logger.debug( "testToSqlDatet() - end" );
 }
 /**
	 * util.Date型日期转化指定的格式字符串型
	 * 
	 * @param p_date
	 *            Date
	 * @param p_format
	 *            String 格式:"yyyy-MM-dd" / "yyyy-MM-dd hh:mm:ss EE" 年-月-日 时:分:秒
	 *            星期 注意MM/mm大小写
	 * @return String
	 * @Author: zhuqx
	 * @Date: 2006-10-31
	 */ 
 public void testToStrDateByFormat() throws ParseException {
  logger.debug( "testToStrDateByFormat() - start" );
  
  java.util.Date l_date = new java.util.Date();
  String day = dateUtil.toStrDateFromUtilDateByFormat( l_date,"yyyy-MM-dd" );
  String day2 = dateUtil.toStrDateFromUtilDateByFormat( l_date,"yyyy-MM-dd hh:mm:ss EE" );
  logger.debug( "string-Date-yyyy-MM-dd=========" + day);
  logger.debug( "string-Date-yyyy-MM-dd hh:mm:ss=========" + day2);
  
  logger.debug( "testToStrDateByFormat() - end" );
 }
 /**
	 * util.Date型日期转化sql.Date型日期
	 * 
	 * @Param: p_utilDate util.Date型日期
	 * @Return: java.sql.Date sql.Date型日期
	 * @Author: zhuqx
	 * @Date: 2006-10-31
	 */
 public void testToSqlDateFromUtilDate() throws ParseException {
  logger.debug( "testToStrDateByFormat() - start" );
  
  java.util.Date l_date = new java.util.Date();
  java.sql.Date day = dateUtil.toSqlDateFromUtilDate( l_date);
  logger.debug( "java.sql.Date-=========" + day);
  
  logger.debug( "testToStrDateByFormat() - end" );
 }
 /**
	 * sql.Date型日期转化util.Date型日期
	 * 
	 * @Param: sqlDate sql.Date型日期
	 * @Return: java.util.Date util.Date型日期
	 * @Author: zhuqx
	 * @Date: 2006-10-31
	 */
 public void testToUtilDateFromSqlDate() throws ParseException {
  logger.debug( "testToStrDateByFormat() - start" );
  
  java.sql.Date l_date = dateUtil.toSqlDateFromStrDate("2005-05-01");
  java.util.Date date = dateUtil.toUtilDateFromSqlDate( l_date);
  logger.debug( "java.util.Date-=========" + date);
  
  logger.debug( "testToStrDateByFormat() - end" );
 }
 
 
 // -----------------获取日期(各种日期格式)-----------------------------------------------------------------------------------
 /**
	 * 获取当前日期的字符化处理
	 * 
	 * @param p_format
	 *            日期格式
	 * @return String 当前时间字符串
	 * @author zhuqx
	 * @Date: 2006-10-31
	 */
 public void testGetNowOfDateByFormat() throws ParseException {
  logger.debug( "testGetNowOfDate() - start" );
  // yyyy年MM月dd日 hh:mm:ss EE
  String date = dateUtil.getNowOfDateByFormat( "yyyyMM" );
  logger.debug( "java.util.Date-=========" + date);
  
  logger.debug( "testGetNowOfDate() - end" );
 }
 
 /**
	 * 获取指定日期格式系统日期的字符型日期
	 * 
	 * @param p_format
	 *            日期格式 格式1:"yyyy-MM-dd" 格式2:"yyyy-MM-dd hh:mm:ss EE"
	 *            格式3:"yyyy年MM月dd日 hh:mm:ss EE" 说明: 年-月-日 时:分:秒 星期 注意MM/mm大小写
	 * @return String 系统时间字符串
	 * @author zhuqx
	 * @Date: 2006-10-31
	 */
 public void testGetSystemOfDateByFormat() throws ParseException {
  logger.debug( "testGetSystemOfDateByFormat() - start" );
  // yyyy年MM月dd日 hh:mm:ss EE
  String date = dateUtil.getSystemOfDateByFormat( "yyyyMM" );
  logger.debug( "java.util.Date-=========" + date);
  
  logger.debug( "testGetSystemOfDateByFormat() - end" );
 }
 
 /**
	 * 获取指定月份的第一天
	 * 
	 * @param p_strdate
	 *            指定月份
	 * @param p_formate
	 *            日期格式
	 * @return String 时间字符串
	 * @author zhuqx
	 * @Date: 2006-10-31
	 */  
 public void testGetMonthBegin() throws ParseException {
  logger.debug( "testGetMonthBegin() - start" );
  // yyyy年MM月dd日 hh:mm:ss EE
  String l_strDate =  "2005-09-11";
  String l_formate = "yyyy-MM-dd";
  String date = dateUtil.getDateOfMonthBegin( l_strDate,l_formate );
  logger.debug( "java.util.Date-=========" + date);
  
  logger.debug( "testGetMonthBegin() - end" );
 }
 
 /**
	 * 取得指定月份的最后一天
	 * 
	 * @param p_strDate
	 *            指定月份
	 * @param p_formate
	 *            日期格式
	 * @return String 时间字符串
	 * @author zhuqx
	 * @Date: 2006-10-31
	 */  
 public void testGetMonthEnd() throws ParseException {
  logger.debug( "testGetMonthBegin() - start" );
  // yyyy年MM月dd日 hh:mm:ss EE
  String l_strDate =  "2006-02-11";
  String l_formate = "yyyy-MM-dd";
  String date = dateUtil.getDateOfMonthEnd( l_strDate,l_formate );
  logger.debug( "java.util.Date-=========" + date);
  
  logger.debug( "testGetMonthBegin() - end" );
 }
 
 /**
	 * 获取指定日期的年份，月份，日份，小时，分，秒，毫秒
	 * 
	 * @param p_date
	 *            util.Date日期
	 * @return int 年份
	 * @author zhuqx
	 * @Date: 2006-10-31
	 */  
 public void testGetYearOfDate() throws ParseException {
  logger.debug( "testGetMonthBegin() - start" );
  // yyyy年MM月dd日 hh:mm:ss EE
  String l_strDate =  "2004-02-11 08:25:15";
  String l_format = "yyyy-MM-dd hh:mm:ss";
  int year = dateUtil.getYearOfDate( dateUtil.toUtilDateFromStrDateByFormat( l_strDate,l_format ));
  int month = dateUtil.getMonthOfDate( dateUtil.toUtilDateFromStrDateByFormat( l_strDate,l_format ));
  int day = dateUtil.getDayOfDate( dateUtil.toUtilDateFromStrDateByFormat( l_strDate,l_format ));
  int hour = dateUtil.getHourOfDate( dateUtil.toUtilDateFromStrDateByFormat( l_strDate,l_format ));
  int minute = dateUtil.getMinuteOfDate( dateUtil.toUtilDateFromStrDateByFormat( l_strDate,l_format ));
  int second = dateUtil.getSecondOfDate( dateUtil.toUtilDateFromStrDateByFormat( l_strDate,l_format ));
  long millis = dateUtil.getMillisOfDate( dateUtil.toUtilDateFromStrDateByFormat( l_strDate,l_format ));
  
  logger.debug( "year==========" + year);
  logger.debug( "month==========" + month);
  logger.debug( "day==========" + day);
  logger.debug( "hour==========" + hour);
  logger.debug( "minute==========" + minute);
  logger.debug( "second==========" + second);
  logger.debug( "millis==========" + millis);
  
  logger.debug( "testGetMonthBegin() - end" );
 }
 
 /**
	 * 是否开始日期在结束日期之前
	 * 
	 * @param p_startDate
	 * @param p_endDate
	 * @return boolean 在结束日期前:ture;否则：false
	 * @author zhuqx
	 */
 public void testIsStartDateBeforeEndDate() throws ParseException {
  Date l_startDate =  dateUtil.toUtilDateFromStrDateByFormat( "2005-02-11","yyyy-MM-dd" );
  Date l_endDate = dateUtil.toUtilDateFromStrDateByFormat( "2005-02-11","yyyy-MM-dd" );
  boolean isBofore = dateUtil.isStartDateBeforeEndDate(l_startDate,l_endDate );
  logger.debug( "isBofore=="+ isBofore);
 }
 
 /**
	 * 在当前的时间基础上添加月、天、或者其他 例如添加3个月，并且格式化为yyyy-MM-dd格式，
	 * 这里调用的方式为addMonth(3,Calendar.MONTH,"yyyy-MM-dd")
	 * 
	 * @param p_count
	 *            时间的数量
	 * @param p_field
	 *            添加的域
	 * @param p_format
	 *            时间转化格式，例如：yyyy-MM-dd hh:mm:ss 或者yyyy-mm-dd等
	 * @return 添加后格式化的时间
	 * @Date: 2006-10-31
	 */
 public void testAddDate() throws ParseException {
  Date l_startDate = dateUtil.toUtilDateFromStrDateByFormat( "2006-02-27 07:59:59","yyyy-MM-dd hh:mm:ss" );
  int l_count = 2;
// int l_field = Calendar.YEAR;
// int l_field = Calendar.MONTH;
  int l_field = Calendar.DATE;
// int l_field = Calendar.HOUR;
// int l_field = Calendar.MINUTE;
// int l_field = Calendar.SECOND;
  String l_format = "yyyy-MM-dd hh:mm:ss";
  String date = this.dateUtil.addDate( l_startDate,l_count,l_field,l_format );
  logger.debug( "addDate============"+date );
 }
 
 /**
	 * 判断给定日期是不是润年
	 * 
	 * @param p_date
	 *            给定日期
	 * @return boolean 如果给定的年份为闰年，则返回 true；否则返回 false。
	 * @Date: 2006-10-31
	 */
 public void testIsLeapYear() throws ParseException {
  
  Date l_date = dateUtil.toUtilDateFromStrDateByFormat( "2000-01-25","yyyy-MM-dd" );
  boolean isLeap = dateUtil.isLeapYear( l_date );
  logger.debug( "isLeapYear="+ isLeap );
 }
 
 
}
