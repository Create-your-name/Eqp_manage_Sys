/** 	1.0  2004-8-30
  * 版权归上揚软件（上海）有限公司所有
  * 本程序属上揚软件的私有机要资料
  * 未经本公司授权，不得非法传播和盗用
  * 可在本公司授权范围内，使用本程序
  * 保留所有权利
  */
package com.csmc.pms.webapp.util;

import java.io.*;
import java.util.*;
import java.util.zip.DataFormatException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;

/**
    *类 MiscUtils.java 
    *@version  1.0  2004-8-30
    *@author   Sky
    */
public class MiscUtils {

	public static String secsToDayTime(long secs) {
		StringBuffer time = new StringBuffer();
		long temp = 0;

		if (secs < 1) {
			return null;
		}

		time.append((secs / 3600) -
					(secs / 3600 / 24 * 24));
		time.append(":");
		temp = secs % 3600 / 60;

		if (temp < 10) {
			time.append("0");
		}

		time.append(temp);
		time.append(":");
		temp = secs % 3600 % 60;

		if (temp < 10) {
			time.append("0");
		}

		time.append(secs % 3600 % 60);

		return time.toString();
	}
	
	public static String getResourceValue(String key){
		String value = null;
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("resource");
			if(bundle != null){
				value = bundle.getString(key);
			}else{
				value = key;
			}
		} catch (MissingResourceException e) {
			value = key;
		}
		return value;
	} 
	
	public static String getResourceValue(String key, String value1, String value2, String value3) {
		String value = null;
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("resource");
			if(bundle != null){
				value = bundle.getString(key);
				StringBuffer sb = new StringBuffer();
				sb.append(value);
				int pos1 = value.indexOf("$1");
				int pos2 = value.indexOf("$2");
				int pos3 = value.indexOf("$3");
				if (pos3 >= 0) {
				 	if (value3 != null) {
						sb.replace(pos3,pos3+2, value3);
				 	} else {
						sb.replace(pos3,pos3+2, "");
				 	}
				}
				if (pos2 >= 0) {
					if (value2 != null) {
						sb.replace(pos2,pos2+2, value2);
					} else {
						sb.replace(pos2,pos2+2, "");
					}
				}
				if (pos1 >= 0) {
					if (value1 != null) {
						sb.replace(pos1,pos1+2, value1);
					} else {
						sb.replace(pos1,pos1+2, "");
					}
				}
				return sb.toString();
			}else{
				value = key;
			}
		} catch (MissingResourceException e) {
			value = key;
		}
		return value;
	}

	 
	public static String toPromisDate(String date) throws ParseException{

		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfString = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SS", Locale.US);
		String rDate = sdfString.format(sdfDate.parse(date));
		return rDate;
	}
	
	public static String toPromisTime(String dateTime) throws ParseException{
   
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S" , Locale.US);
		SimpleDateFormat sdfString = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SS", Locale.US);
		String rDate = sdfString.format(sdfDate.parse(dateTime));
		return rDate;
	}
	
	public static String toPromisTime5(String dateTime) throws ParseException{
   
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S" , Locale.US);
		SimpleDateFormat sdfString = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		String rDate = sdfString.format(sdfDate.parse(dateTime));
		return rDate;
	}
	
	public static String toPromisTime2(String dateTime) throws ParseException{
   
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SS" , Locale.US);
		SimpleDateFormat sdfString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.US);
		String rDate = sdfString.format(sdfDate.parse(dateTime));
		return rDate;
	}
		
	public static String toGuiDate(String date) throws ParseException{
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfString = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SS", Locale.US);
		String rDate = sdfDate.format(sdfString.parse(date));
		return rDate;
	}
	
	public static java.sql.Date toGuiDate(String date, String format) throws ParseException{
		SimpleDateFormat sdfDate = new SimpleDateFormat(format, Locale.US);
		java.util.Date rDate = sdfDate.parse(date);
		return new java.sql.Date(rDate.getTime());
	}
	
	public static String toGuiDate2(String date) throws ParseException{
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfString = new SimpleDateFormat("yyyyMMdd", Locale.US);
		String rDate = sdfString.format(sdfDate.parse(date));
		return rDate;
	}
	
	public static String toGuiDate3(String date) throws ParseException{
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfString = new SimpleDateFormat("yyMMdd", Locale.US);
		String rDate = sdfString.format(sdfDate.parse(date));
		return rDate;
	}
	
	public static String toGuiDate4(String date) throws ParseException{
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdfString = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		String rDate = sdfString.format(sdfDate.parse(date));
		return rDate;
	}
	
	public static String toGuiDate5(String date) throws ParseException{
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyMMdd");
		SimpleDateFormat sdfString = new SimpleDateFormat("yy-MM-dd", Locale.US);
		String rDate = sdfString.format(sdfDate.parse(date));
		return rDate;
	}
	
	public static Timestamp toGuiDateTime(String date, String format) throws ParseException{
		SimpleDateFormat sdfDate = new SimpleDateFormat(format, Locale.US);
		java.util.Date rDate = sdfDate.parse(date);
		return new java.sql.Timestamp(rDate.getTime());
	}

	public static String toGuiDate(java.sql.Date date, String format) throws ParseException{
		SimpleDateFormat sdfDate = new SimpleDateFormat(format);
		return sdfDate.format(date);
	}
	
	public static String formatGuiDate(java.sql.Date date, String format) {
		if (date == null)
		return "";
		SimpleDateFormat sdfDate = new SimpleDateFormat(format); 
		return sdfDate.format(date);      
	}
	
	//	当前时间与参数时间之差
	public static String toDateBalance(String date)
	  {
		  try {
			  SimpleDateFormat sdfString = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SS", Locale.US);

			  return toDateBalance(sdfString.parse(date));

		  } catch (Exception e) {
			  return "";
		  }
		
	  }
	public static double toDateBalanceDoubleValue(String dateStr)
	  {
		try {
			SimpleDateFormat sdfString = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SS", Locale.US);
			
			Date date = sdfString.parse(dateStr);	  	
			  Date nowdate = new Date();
				  double mm = (double) nowdate.getTime()-date.getTime();
				  double second = mm /1000;
				  return second / 3600;
		} catch (ParseException e) {
			return 0.00;
		}
		  
	  }	
	//	当前时间与参数时间之差
	public static String toDateBalance(Date date)
	  {
		  Date nowdate = new Date();
			  double mm = (double) nowdate.getTime()-date.getTime();
			  double second = mm /1000;
			  
			  DecimalFormat priceDecimalFormat = new DecimalFormat("#,##0.00");
			  String sbalance =priceDecimalFormat.format(second / 3600);
			  
			  sbalance = sbalance + "小时";
			  
			  return sbalance ;
		  
	  }	     
	  public static Timestamp promisToTimestamp(String dateString) throws ParseException{
	  	if(dateString==null){
	  		return null;
	  	}   
		SimpleDateFormat sdfString = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SS", Locale.US);
		
		return new Timestamp(sdfString.parse(dateString).getTime());
	  }
	public static Timestamp addTimeToDate(Date d, int year,int month, int day, int hour,int minute, int second) {
		if(d==null){
			return null;
		}
		Timestamp date = new Timestamp(d.getTime());
		date.setYear(date.getYear()+year);
		date.setMonth(date.getMonth()+month);
		date.setDate(date.getDate()+day);
		date.setHours(date.getHours()+hour);		
		date.setMinutes(date.getMinutes()+minute);
		date.setSeconds(date.getSeconds()+second);
	  return date;
	}	
	public static Date addHourToDate(Date date, int hour) {

	  return addTimeToDate(date,0,0,0,hour,0,0);
	}	
	public static Date addHourToDate(Date date, double hour) {
	  int day = (int)hour/24;
	  int hr = (int)(hour - day*24);
	  int min = (int)((hour - day*24 - hr)*60);	
	  int sec = (int)((hour - day*24 - hr - ((double)min)/60)*60*60);
	  return addTimeToDate(date,0,0,day,hr,min,sec);
	}
	
	public static int comparePromisTime(String time1,String time2) throws ParseException{
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SS", Locale.US);
	    
	    return dateFormat.parse(time1).compareTo(dateFormat.parse(time2));
	}
	
	public static long getPromisTimeSpan(String time1,String time2) throws ParseException{
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SS", Locale.US);
 
	    return Math.abs(dateFormat.parse(time1).getTime() -  dateFormat.parse(time2).getTime());
	}
	
	public static Timestamp getGuiDate(String date) throws ParseException{	    
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		
		return new Timestamp(sdfDate.parse(date).getTime());
	}
	
	public static String toGuiDateFormat(String date, String format) throws ParseException{
		SimpleDateFormat sdfDate = new SimpleDateFormat(format, Locale.US);
		SimpleDateFormat sdfString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		String rDate = sdfString.format(new Timestamp(sdfDate.parse(date).getTime()));
		return rDate;
	}	

    /**
     * @param sendTo
     * @param sendCc
     * @param sendBcc
     * @param subject
     * @param sendFrom
     * @param priority
     * @param body
     * @param contentType
     * @throws MessagingException
     * @throws AddressException
     * @throws NoSuchProviderException
     */
    public static void sendMail(String sendTo, String sendCc, String sendBcc, String subject, String sendFrom, String priority, String body, String contentType) throws MessagingException, AddressException, NoSuchProviderException {
        // create a message
        Properties p = UtilProperties.getProperties("csmcgui");
        if (p.get("mail.auth.user") != null && p.get("mail.auth.user").toString().length() > 0) {
            p.setProperty("mail.smtp.auth", "true");
        }
    
        Session session = Session.getInstance(p, null);
        session.setDebug(Boolean.getBoolean(p.getProperty("mail.debug")));            
        Message msg = new MimeMessage(session);
        msg.setHeader("X-Priority", priority);
        if(sendFrom==null){
         sendFrom=p.getProperty("mail.defaultsendaddress");    
        }
        if (contentType == null) {
			contentType = "text/plain;charset=GBK";     
        }
        if (priority == null) {
            priority = "2";
        }
        msg.setFrom(new InternetAddress(sendFrom));
//      set the to 
        if(UtilValidate.isNotEmpty(sendTo)){
            msg.setRecipients(Message.RecipientType.TO, buildAddressArray(sendTo));
    	}
        if (UtilValidate.isNotEmpty(sendCc)) {
            msg.addRecipients(Message.RecipientType.CC, buildAddressArray(sendCc));
        }
        if (UtilValidate.isNotEmpty(sendBcc)) {
            msg.addRecipients(Message.RecipientType.BCC, buildAddressArray(sendBcc));
        }
        if(subject ==null){
            subject = " ";
        }
        if(body ==null){
            body = " ";
        }        
        msg.setSentDate(new Date());
        msg.setSubject(subject);
        msg.setContent(body, contentType);
        msg.saveChanges();
    
        Transport trans = session.getTransport("smtp");
        if (p.getProperty("mail.auth.user") != null && p.getProperty("mail.auth.user").length() > 0) {
            trans.connect(p.getProperty("mail.smtp.host"), p.getProperty("mail.auth.user"), p.getProperty("mail.auth.password"));
        } else {
            trans.connect();
        }
        trans.sendMessage(msg, msg.getAllRecipients());
        trans.close();
    }

    /**
     * @param sendTo
     * @return
     * @throws AddressException
     */
    public static InternetAddress[] buildAddressArray(String addressStr) throws AddressException {
        Set addSet = new HashSet();
        String[] astrTo = null;
        if(addressStr.indexOf(",")>0){
            astrTo = addressStr.split(",");
        }else{
            astrTo=new String[]{addressStr};
        }
        for (int i = 0; i<astrTo.length; ++i) 
        { 
            String aStr = astrTo[i];
            if(aStr.indexOf(";")>0){
                String[] ts = aStr.split(";");
                addSet.addAll(Arrays.asList(ts));
            }else{
                addSet.add(aStr);
            }
        }

        InternetAddress[] address = new InternetAddress[addSet.size()]; 
        int i = 0;
        for (Iterator it = addSet.iterator(); it.hasNext();i++ ) 
        { 
            address[i] = new InternetAddress((String)it.next()); 
        }
        return address;
    }
    
    public static String join(List list, String delim) {
        if (list == null || list.size() < 1)
            return "";
        StringBuffer buf = new StringBuffer();
        Iterator i = list.iterator();

        while (i.hasNext()) {
            buf.append((String) i.next());
            if (i.hasNext())
                buf.append(delim);
        }
        return buf.toString();
    }   
}
