/** 生成程序GeneralEvents.java	1.0  2004/02/027
  * 版权归上揚软件（上海）有限公司所有
  * 本程序属上揚软件的私有机要资料
  * 未经本公司授权，不得非法传播和盗用
  * 可在本公司授权范围内，使用本程序
  * 保留所有权利
  */


package com.csmc.pms.webapp.util;

import java.util.*;
import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;   

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*; 
import org.ofbiz.service.*;
import org.ofbiz.security.*; 


/**
   *类 GeneralEvents 用处理event中的通用方法
   *@see  
   *@version  1.0  2004/02/027 
   *@author   Jim.Guo
   */


public class GeneralEvents {
	
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	public static final String CLIENT_IP = "clientIp";
	public static final String USER_LOGIN_ID = "userLoginId";
	public static final String STATUS_ID = "statusId";
	public static final String REQUEST_URL = "requestUrl";
	public static final String REFERRER_URL = "referrerUrl";
	public static final String SERVER_IP_ADDRESS = "serverIpAddress";
	public static final String SERVER_HOST_NAME = "serverHostName";
	public static final String TRANS_NAME = "transName";		
	
	public static Map getInitParams(HttpServletRequest request){
		return getInitParams(request,true);
	}
	
	public static Map getInitParams(HttpServletRequest request, boolean toUpperCase){
		Map attributes=new HashMap();

		//get params from request 
		Enumeration paramNames = request.getParameterNames();

		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if(request.getParameter(paramName)!=null){
				if(toUpperCase){
					attributes.put(paramName,request.getParameter(paramName).toString().trim().toUpperCase());
				}else{
					attributes.put(paramName,request.getParameter(paramName).toString().trim());
				}
			}
		}
  
		
		//get info from session for transaction_log and tp process
		HttpSession session = request.getSession();		
		if(session.getAttribute("visit")!=null && session.getAttribute("userLogin")!=null){
			GenericValue visit = (GenericValue) session.getAttribute("visit");
			GenericValue userLogin = (GenericValue) session.getAttribute("userLogin"); 		
			attributes.put(GeneralEvents.USER_LOGIN_ID,userLogin.get("userLoginId"));
//			attributes.put(GeneralEvents.USER_LOGIN_ID,"train57");						
//			attributes.put(GeneralEvents.PASSWORD,userLogin.get("currentPassword"));
			//attributes.put(GeneralEvents.PASSWORD,"train57");
			attributes.put(GeneralEvents.CLIENT_IP,visit.get("clientIpAddress"));
			attributes.put(GeneralEvents.REQUEST_URL,visit.get("initialRequest"));
			attributes.put(GeneralEvents.SERVER_IP_ADDRESS,visit.get("serverIpAddress"));
			attributes.put(GeneralEvents.REQUEST_URL,visit.get("initialRequest"));
			attributes.put(GeneralEvents.REFERRER_URL,visit.get("initialReferrer"));
			attributes.put(GeneralEvents.SERVER_HOST_NAME,visit.get("serverHostName"));
		}
		return attributes;
	}
	
	public static Map getInitParams(HttpServletRequest request, boolean toUpperCase, boolean trim){
		Map attributes=new HashMap();

		//get params from request 
		Enumeration paramNames = request.getParameterNames();

		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			if(request.getParameter(paramName)!=null){
				if(toUpperCase && trim){
					attributes.put(paramName,request.getParameter(paramName).toString().trim().toUpperCase());
				}else if(!toUpperCase || trim){
					attributes.put(paramName,request.getParameter(paramName).toString().trim());
				}else if(toUpperCase || !trim){
					attributes.put(paramName,request.getParameter(paramName).toString().toUpperCase());
				}
			}
		}
  
		
		//get info from session for transaction_log and tp process
		HttpSession session = request.getSession();		
		if(session.getAttribute("visit")!=null && session.getAttribute("userLogin")!=null){
			GenericValue visit = (GenericValue) session.getAttribute("visit");
			GenericValue userLogin = (GenericValue) session.getAttribute("userLogin"); 		
			attributes.put(GeneralEvents.USER_LOGIN_ID,userLogin.get("userLoginId"));
//			attributes.put(GeneralEvents.USER_LOGIN_ID,"train57");						
//			attributes.put(GeneralEvents.PASSWORD,userLogin.get("currentPassword"));
			//attributes.put(GeneralEvents.PASSWORD,"train57");
			attributes.put(GeneralEvents.CLIENT_IP,visit.get("clientIpAddress"));
			attributes.put(GeneralEvents.REQUEST_URL,visit.get("initialRequest"));
			attributes.put(GeneralEvents.SERVER_IP_ADDRESS,visit.get("serverIpAddress"));
			attributes.put(GeneralEvents.REQUEST_URL,visit.get("initialRequest"));
			attributes.put(GeneralEvents.REFERRER_URL,visit.get("initialReferrer"));
			attributes.put(GeneralEvents.SERVER_HOST_NAME,visit.get("serverHostName"));
		}
		return attributes;
	}
	
	public static boolean validateInteger(Object num,int llimit,int ulimit){
		if(num==null){
			return false;
		}
		try{
			Integer lnum = new Integer (num.toString());
			if(lnum.intValue()>ulimit || lnum.intValue()<llimit){
				return false;
			}
		}catch (NumberFormatException e){
			return false;
		}
		return true;
	}
	
	public static boolean validateLong(Object num,long llimit,long ulimit){
			if(num==null){
				return false;
			}
			try{
				Long lnum = new Long (num.toString());
				if(lnum.longValue()>ulimit || lnum.longValue()<llimit){
					return false;
				}
			}catch (NumberFormatException e){
				return false;
			}
			return true;
		}
		
	public static void putResultToRequest(Map map,HttpServletRequest request){
		if(map==null){
			return;
		}
		for(Iterator it = map.keySet().iterator();it.hasNext();) {
			String paramName = (String) it.next();

				request.setAttribute(paramName,map.get(paramName));

		}	
	}	
	public static Map getRequestPrefixparameter(String prefix,HttpServletRequest request){
		Map paramMap = new HashMap();
		Enumeration e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			if (key.startsWith(prefix)) {
				paramMap.put(key, request.getParameter(key));
			}
		}
		return paramMap;
	}		

}
