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
	 * ��ȡ2���ַ����ڵ�������
	 * 
	 * @param p_startDate
	 * @param p_endDate
	 * @return ������
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
	 * ��ȡ�ַ�����һ���µ�����
	 * 
	 * @param p_date
	 * @return ����
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
 
 // -------------------------------����ת��---------------------------------------------------------------------------
 /**
	 * �ַ�����ת��util.Date
	 * 
	 * @Param: p_strDate �ַ���������
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
	 * �ַ�������ת����sql.Date������
	 * 
	 * @param p_strDate
	 *            �ַ�������
	 * @return java.sql.Date sql.Date������
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
	 * util.Date������ת��ָ���ĸ�ʽ�ַ�����
	 * 
	 * @param p_date
	 *            Date
	 * @param p_format
	 *            String ��ʽ:"yyyy-MM-dd" / "yyyy-MM-dd hh:mm:ss EE" ��-��-�� ʱ:��:��
	 *            ���� ע��MM/mm��Сд
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
	 * util.Date������ת��sql.Date������
	 * 
	 * @Param: p_utilDate util.Date������
	 * @Return: java.sql.Date sql.Date������
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
	 * sql.Date������ת��util.Date������
	 * 
	 * @Param: sqlDate sql.Date������
	 * @Return: java.util.Date util.Date������
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
 
 
 // -----------------��ȡ����(�������ڸ�ʽ)-----------------------------------------------------------------------------------
 /**
	 * ��ȡ��ǰ���ڵ��ַ�������
	 * 
	 * @param p_format
	 *            ���ڸ�ʽ
	 * @return String ��ǰʱ���ַ���
	 * @author zhuqx
	 * @Date: 2006-10-31
	 */
 public void testGetNowOfDateByFormat() throws ParseException {
  logger.debug( "testGetNowOfDate() - start" );
  // yyyy��MM��dd�� hh:mm:ss EE
  String date = dateUtil.getNowOfDateByFormat( "yyyyMM" );
  logger.debug( "java.util.Date-=========" + date);
  
  logger.debug( "testGetNowOfDate() - end" );
 }
 
 /**
	 * ��ȡָ�����ڸ�ʽϵͳ���ڵ��ַ�������
	 * 
	 * @param p_format
	 *            ���ڸ�ʽ ��ʽ1:"yyyy-MM-dd" ��ʽ2:"yyyy-MM-dd hh:mm:ss EE"
	 *            ��ʽ3:"yyyy��MM��dd�� hh:mm:ss EE" ˵��: ��-��-�� ʱ:��:�� ���� ע��MM/mm��Сд
	 * @return String ϵͳʱ���ַ���
	 * @author zhuqx
	 * @Date: 2006-10-31
	 */
 public void testGetSystemOfDateByFormat() throws ParseException {
  logger.debug( "testGetSystemOfDateByFormat() - start" );
  // yyyy��MM��dd�� hh:mm:ss EE
  String date = dateUtil.getSystemOfDateByFormat( "yyyyMM" );
  logger.debug( "java.util.Date-=========" + date);
  
  logger.debug( "testGetSystemOfDateByFormat() - end" );
 }
 
 /**
	 * ��ȡָ���·ݵĵ�һ��
	 * 
	 * @param p_strdate
	 *            ָ���·�
	 * @param p_formate
	 *            ���ڸ�ʽ
	 * @return String ʱ���ַ���
	 * @author zhuqx
	 * @Date: 2006-10-31
	 */  
 public void testGetMonthBegin() throws ParseException {
  logger.debug( "testGetMonthBegin() - start" );
  // yyyy��MM��dd�� hh:mm:ss EE
  String l_strDate =  "2005-09-11";
  String l_formate = "yyyy-MM-dd";
  String date = dateUtil.getDateOfMonthBegin( l_strDate,l_formate );
  logger.debug( "java.util.Date-=========" + date);
  
  logger.debug( "testGetMonthBegin() - end" );
 }
 
 /**
	 * ȡ��ָ���·ݵ����һ��
	 * 
	 * @param p_strDate
	 *            ָ���·�
	 * @param p_formate
	 *            ���ڸ�ʽ
	 * @return String ʱ���ַ���
	 * @author zhuqx
	 * @Date: 2006-10-31
	 */  
 public void testGetMonthEnd() throws ParseException {
  logger.debug( "testGetMonthBegin() - start" );
  // yyyy��MM��dd�� hh:mm:ss EE
  String l_strDate =  "2006-02-11";
  String l_formate = "yyyy-MM-dd";
  String date = dateUtil.getDateOfMonthEnd( l_strDate,l_formate );
  logger.debug( "java.util.Date-=========" + date);
  
  logger.debug( "testGetMonthBegin() - end" );
 }
 
 /**
	 * ��ȡָ�����ڵ���ݣ��·ݣ��շݣ�Сʱ���֣��룬����
	 * 
	 * @param p_date
	 *            util.Date����
	 * @return int ���
	 * @author zhuqx
	 * @Date: 2006-10-31
	 */  
 public void testGetYearOfDate() throws ParseException {
  logger.debug( "testGetMonthBegin() - start" );
  // yyyy��MM��dd�� hh:mm:ss EE
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
	 * �Ƿ�ʼ�����ڽ�������֮ǰ
	 * 
	 * @param p_startDate
	 * @param p_endDate
	 * @return boolean �ڽ�������ǰ:ture;����false
	 * @author zhuqx
	 */
 public void testIsStartDateBeforeEndDate() throws ParseException {
  Date l_startDate =  dateUtil.toUtilDateFromStrDateByFormat( "2005-02-11","yyyy-MM-dd" );
  Date l_endDate = dateUtil.toUtilDateFromStrDateByFormat( "2005-02-11","yyyy-MM-dd" );
  boolean isBofore = dateUtil.isStartDateBeforeEndDate(l_startDate,l_endDate );
  logger.debug( "isBofore=="+ isBofore);
 }
 
 /**
	 * �ڵ�ǰ��ʱ�����������¡��졢�������� �������3���£����Ҹ�ʽ��Ϊyyyy-MM-dd��ʽ��
	 * ������õķ�ʽΪaddMonth(3,Calendar.MONTH,"yyyy-MM-dd")
	 * 
	 * @param p_count
	 *            ʱ�������
	 * @param p_field
	 *            ��ӵ���
	 * @param p_format
	 *            ʱ��ת����ʽ�����磺yyyy-MM-dd hh:mm:ss ����yyyy-mm-dd��
	 * @return ��Ӻ��ʽ����ʱ��
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
	 * �жϸ��������ǲ�������
	 * 
	 * @param p_date
	 *            ��������
	 * @return boolean ������������Ϊ���꣬�򷵻� true�����򷵻� false��
	 * @Date: 2006-10-31
	 */
 public void testIsLeapYear() throws ParseException {
  
  Date l_date = dateUtil.toUtilDateFromStrDateByFormat( "2000-01-25","yyyy-MM-dd" );
  boolean isLeap = dateUtil.isLeapYear( l_date );
  logger.debug( "isLeapYear="+ isLeap );
 }
 
 
}
