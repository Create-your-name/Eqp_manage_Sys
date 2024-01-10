/*
 * Created on 2004-7-13
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.csmc.webapp.taglibs.input;
import java.io.IOException;

import java.util.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;   

/**
 * @author Sky
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

class Util
{


	public static void printAttributes(JspWriter out, Map attributes)
		throws JspTagException, IOException
	{
		if(attributes != null)
		{
			String key;
			String value;
			for(Iterator i = attributes.keySet().iterator(); i.hasNext(); out.print(quote(key) + "=\"" + quote(value) + "\" "))
			{
				Object oKey = i.next();
				Object oVal = attributes.get(oKey);
				if(!(oKey instanceof String) || oVal != null && !(oVal instanceof String))
					throw new JspTagException("all members in attributes Map must be Strings");
				key = (String)oKey;
				value = (String)oVal;
				if(key.equals("name") || key.equals("value") || key.equals("type") || key.equals("checked"))
					throw new JspTagException("illegal key '" + key + "'found in attributes Map");
				if(value == null)
					value = key;
			}

		}
	}

	public static String quote(String x)
	{
		if(x == null)
		{
			return null;
		} else
		{
			x = replace(x, "&", "&amp;");
			x = replace(x, "\"", "&quot;");
			x = replace(x, "<", "&lt;");
			x = replace(x, ">", "&gt;");
			return x;
		}
	}

	public static String replace(String subject, String find, String replace)
	{
		StringBuffer buf = new StringBuffer();
		int l = find.length();
		int s = 0;
		for(int i = subject.indexOf(find); i != -1; i = subject.indexOf(find, s))
		{
			buf.append(subject.substring(s, i));
			buf.append(replace);
			s = i + l;
		}

		buf.append(subject.substring(s));
		return buf.toString();
	}
	
	private static void addFieldToValidateMap(String fieldName,String label,String[] validateInfo,
										Map validateMap){
		List fieldArray = (List)validateMap.get(validateInfo[0]);
		if(fieldArray == null){
			fieldArray = new ArrayList();
			validateMap.put(validateInfo[0],fieldArray);
		}
		Map field = new HashMap();
		// this field's name
		field.put("fieldName",fieldName);
		// the validate property ,it's in the middle of the ().
		field.put("validateProperty",validateInfo[1]);
		
		if(label != null && !label.equals("")){
			field.put("label",label);
		}else{
			// If the content is not available ,use the field name !
		}
		field.put("name",fieldName);

		fieldArray.add(field);

	}

	private static String[] decodeValidateInfo(String validateString){
		String[] validateInfo = new String[2];
		int indexLeft = validateString.indexOf("(");
		int indexRight = validateString.indexOf(")");
		if(indexLeft > 0){
			if(indexRight < 0){
				throw new IllegalArgumentException("Incorrect input's validates property value!");
			}
			try{
				validateInfo[0] = validateString.substring(0,indexLeft);

				//if the validaterule is length or the range the validate property is requied.

				if(validateInfo[0].equalsIgnoreCase("length") || validateInfo[0].equalsIgnoreCase("range") || validateInfo[0].equalsIgnoreCase("limit")){
					String var = validateString.substring(indexLeft+1,indexRight);
					StringTokenizer varSt = new StringTokenizer(var,":");
					validateInfo[1] = varSt.nextToken();
				}
			}catch(NoSuchElementException ne){
				// if the check rule is length&range and the property is null ,so the validate is not necessary!
				validateInfo[0] = null;
			}
		}else{
			//if the format of the property value is not correct,throw exception!
			if(indexRight > 0){
				throw new IllegalArgumentException("Incorrect input's validates property value!");
			}
			validateInfo[0] = validateString;
			//default context is the field name

		}

		return validateInfo;

	}	
	//this method is add by sky
	public static void registerValidateField( PageContext pageContext,String fieldName,String label,String validates) throws JspTagException{
	  //get the validate map from the page context
	  Map validateMap = (Map)pageContext.getAttribute("validateMap");

	  if(validateMap == null){
		throw new JspTagException("The input tag is depends on validate Tag if you have set the validates attribute!");
	  }
	  // set the validate rule into the validate map.
	  StringTokenizer st = new StringTokenizer(validates,",");
	  // add the field validate infomation into the pagecontext
	  while(st.hasMoreTokens()){
		  String validateString = st.nextToken();
		  // decode the validate parameter
		  String[] validateInfo = decodeValidateInfo(validateString);
		  if(validateInfo[0] != null){
			  addFieldToValidateMap(fieldName,label,validateInfo,validateMap);
		  }
	  }

	}	

}
