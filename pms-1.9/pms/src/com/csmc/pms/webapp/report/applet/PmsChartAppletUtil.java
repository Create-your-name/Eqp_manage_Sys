package com.csmc.pms.webapp.report.applet;

import java.text.ParseException;

public class PmsChartAppletUtil {

	public static String checkNull(String string1, String string2) {
	    if (string1 != null)
	        return string1;
	    else if (string2 != null)
	        return string2;
	    else
	        return "";
	}

	public static String checkEmpty(String string1, String string2) {
	    if (string1 != null && !string1.equals("")) {
	        return string1;
	    } else if (string2 != null && !string2.equals("")) {
	        return string2;
	    } else {
	    	return " ";
	    }
	}
	
	/**
	 * 字符型日期转化util.Date型日期
	 * @Param:p_strDate 字符型日期 
	 * @param p_format  格式:"yyyy-MM-dd" / "yyyy-MM-dd hh:mm:ss"
	 * @Return:java.util.Date util.Date型日期
	 * @Throws: ParseException
	 */
	public static java.util.Date toUtilDateFromStrDateByFormat(
			String p_strDate, String p_format) throws ParseException {
		java.util.Date l_date = null;
		java.text.DateFormat df = new java.text.SimpleDateFormat(p_format);
		if (p_strDate != null && (!"".equals(p_strDate)) && p_format != null
				&& (!"".equals(p_format))) {
			l_date = df.parse(p_strDate);
		}
		return l_date;
	}

}
