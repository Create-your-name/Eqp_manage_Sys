package com.csmc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;

/**
 * @Functionality: �й����ڹ�����
 */
public class DateUtil extends TimeUtil {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(DateUtil.class);

	/** ����ת�� */
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd");

	/**
	 * @Functionality: ���캯��
	 */
	public DateUtil() {
	}

	// ----------------------���ڼ���---------------------------------------------------------------------------------

	/**
	 * �Ƿ�ʼ�����ڽ�������֮ǰ(���������)
	 * 
	 * @param p_startDate
	 * @param p_endDate
	 * @return boolean �ڽ�������ǰ:ture;����false
	 * @author zhuqx
	 * @Date: 2006-10-31
	 */
	public static boolean isStartDateBeforeEndDate(Date p_startDate,
			Date p_endDate) throws ParseException {
		long l_startTime = getMillisOfDate(p_startDate);
		long l_endTime = getMillisOfDate(p_endDate);
		return (l_startTime - l_endTime > (long) 0) ? true : false;
	}

	/**
	 * ��ȡ2���ַ����ڵ�������
	 * 
	 * @param p_startDate
	 * @param p_endDate
	 * @return ������
	 * @author zhuqx
	 * @Date: 2006-10-31
	 */
	public static long getDaysOfTowDiffDate(String p_startDate, String p_endDate)
			throws ParseException {

		Date l_startDate = toUtilDateFromStrDateByFormat(p_startDate,
				"yyyy-MM-dd");
		Date l_endDate = toUtilDateFromStrDateByFormat(p_endDate, "yyyy-MM-dd");
		long l_startTime = getMillisOfDate(l_startDate);
		long l_endTime = getMillisOfDate(l_endDate);
		long betweenDays = (long) ((l_endTime - l_startTime) / (1000 * 60 * 60 * 24));
		return betweenDays;
	}

	/**
	 * ��ȡ2���ַ����ڵ�������
	 * 
	 * @param p_startDate
	 * @param p_endDate
	 * @return ������
	 * @author zhuqx
	 * @Date: 2006-10-31
	 */
	public static long getWeeksOfTowDiffDate(String p_startDate,
			String p_endDate) throws ParseException {
		return getDaysOfTowDiffDate(p_startDate, p_endDate) / 7;
	}

	/**
	 * ��ȡ2���ַ����ڵ�������
	 * 
	 * @param p_startDate
	 * @param p_endDate
	 * @return ������
	 * @author zhuqx
	 * @Date: 2006-10-31
	 */
	public static long getMonthsOfTowDiffDate(String p_startDate,
			String p_endDate) throws ParseException {
		return getDaysOfTowDiffDate(p_startDate, p_endDate) / 30;
	}

	/**
	 * ��ȡ2���ַ����ڵ�������
	 * 
	 * @param p_startDate
	 * @param p_endDate
	 * @return ������
	 * @author zhuqx
	 * @Date: 2006-10-31
	 */
	public static long getYearsOfTowDiffDate(String p_startDate,
			String p_endDate) throws ParseException {
		return getDaysOfTowDiffDate(p_startDate, p_endDate) / 365;
	}

	/**
	 * �ڸ��������ڻ���������꣬�£��ա�ʱ���֣��� ����Ҫ��2006��10��21��uitl���ڣ����3���£����Ҹ�ʽ��Ϊyyyy-MM-dd��ʽ��
	 * ������õķ�ʽΪ addDate(2006��10��21,3,Calendar.MONTH,"yyyy-MM-dd")
	 * 
	 * @param p_startDate
	 *            ����������
	 * @param p_count
	 *            ʱ�������
	 * @param p_field
	 *            ��ӵ���
	 * @param p_format
	 *            ʱ��ת����ʽ�����磺yyyy-MM-dd hh:mm:ss ����yyyy-mm-dd��
	 * @return ��Ӻ��ʽ����ʱ��
	 * @Date: 2006-10-31
	 */
	public static String addDate(Date p_startDate, int p_count, int p_field,
			String p_format) throws ParseException {

		// �꣬�£��ա�ʱ���֣���
		int l_year = getYearOfDate(p_startDate);
		int l_month = getMonthOfDate(p_startDate) - 1;
		int l_day = getDayOfDate(p_startDate);
		int l_hour = getHourOfDate(p_startDate);
		int l_minute = getMinuteOfDate(p_startDate);
		int l_second = getSecondOfDate(p_startDate);
		Calendar l_calendar = new GregorianCalendar(l_year, l_month, l_day,
				l_hour, l_minute, l_second);
		l_calendar.add(p_field, p_count);
		return toStrDateFromUtilDateByFormat(l_calendar.getTime(), p_format);
	}

	/**
	 * �жϸ��������ǲ�������
	 * 
	 * @param p_date
	 *            ��������
	 * @return boolean ������������Ϊ���꣬�򷵻� true�����򷵻� false��
	 * @Date: 2006-10-31
	 */
	public static boolean isLeapYear(Date p_date) {
		int l_year = getYearOfDate(p_date);
		GregorianCalendar l_calendar = new GregorianCalendar();
		return l_calendar.isLeapYear(l_year);
	}

}
