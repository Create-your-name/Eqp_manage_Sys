/*
 * Created on 2004-7-12
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.csmc.webapp.taglibs.input;


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
public abstract class BaseAbstractTag extends TagSupport{
	String name;        // name of the text field
	String defaultValue;        // default value if none is found
	boolean password;
	Map attributes = new HashMap();
      
	/**
	 * @return
	 */
	public String getAlt() {
		return (String)attributes.get("alt");
	}

	/**
	 * @return
	 */
	public String getDisabled() {
		return (String)attributes.get("disabled");
	}

	/**
	 * @return
	 */
	public String getDefaultValue() {
		return defaultValue;
	}


	/**
	 * @return
	 */

	public String getMaxlength() {
		return (String)attributes.get("maxlength");
	}
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getOnblur() {
		return (String)attributes.get("onblur");
	}

	/**
	 * @return
	 */
	public String getOnchange() {
		return (String)attributes.get("onchange");
	}

	/**
	 * @return
	 */
	public String getOnclick() {
		return (String)attributes.get("onclick");
	}

	/**
	 * @return
	 */
	public String getOndblclick() {
		return (String)attributes.get("ondblclick");
	}

	/**
	 * @return
	 */
	public String getOnfocus() {
		return (String)attributes.get("onfocus");
	}

	/**
	 * @return
	 */
	public String getOnkeydown() {
		System.out.println((String)attributes.get("onkeydown"));
		return (String)attributes.get("onkeydown");
	}

	/**
	 * @return
	 */
	public String getOnkeypress() {
		return (String)attributes.get("onkeypress");
	}

	/**
	 * @return
	 */
	public String getOnkeyup() {
		return (String)attributes.get("onkeyup");
	}

	/**
	 * @return
	 */
	public String getOnmousedown() {
		return (String)attributes.get("onmousedown");
	}

	/**
	 * @return
	 */
	public String getOnmousemove() {
		return (String)attributes.get("onmousemove");
	}

	/**
	 * @return
	 */
	public String getOnmouseover() {
		return (String)attributes.get("onmouseover");
	}

	/**
	 * @return
	 */
	public String getOnmouseup() {
		return (String)attributes.get("onmouseup");
	}

	/**
	 * @return
	 */
	public String getOnselect() {
		return (String)attributes.get("onselect");
	}

	/**
	 * @return
	 */
	public boolean isPassword() {
		return password;
	}

	/**
	 * @return
	 */
	public String getReadonly() {
		return (String)attributes.get("readonly");
	}

	/**
	 * @return
	 */
	public String getSize() {
		return (String)attributes.get("size");
	}

	/**
	 * @return
	 */
	public String getStyle() {
		return (String)attributes.get("style");
	}

	/**
	 * @return
	 */
	public String getStyleclass() {
		return (String)attributes.get("class");
	}

	/**
	 * @return
	 */
	public String getTabindex() {
		return (String)attributes.get("tabindex");
	}

	/**
	 * @param string
	 */
	public void setAlt(String string) {
		attributes.put("alt",string);
	}

	/**
	 * @param string
	 */
	public void setDisabled(String string) {
	//	System.out.println("put the disabled" );
		attributes.put("disabled",string);
//		System.out.println("attributes = " + attributes);		
	}

	/**
	 * @param string
	 */
	public void setDefaultValue(String string) {
		defaultValue = string;
	}
 

	/**
	 * @param string
	 */
	public void setMaxlength(String string) {
		attributes.put("maxlength",string);
	}
	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param string
	 */
	public void setOnblur(String string) {
		attributes.put("onblur",string);
	}

	/**
	 * @param string
	 */
	public void setOnchange(String string) {
		attributes.put("onchange",string);
	}

	/**
	 * @param string
	 */
	public void setOnclick(String string) {
		attributes.put("onclick",string);
	}

	/**
	 * @param string
	 */
	public void setOndblclick(String string) {
		attributes.put("ondblclick",string);
	}

	/**
	 * @param string
	 */
	public void setOnfocus(String string) {
		attributes.put("onfocus",string);
	}

	/**
	 * @param string
	 */
	public void setOnkeydown(String string) {
		attributes.put("onkeydown",string);
	}

	/**
	 * @param string
	 */
	public void setOnkeypress(String string) {
		attributes.put("onkeypress",string);
	}

	/**
	 * @param string
	 */
	public void setOnkeyup(String string) {
		attributes.put("onkeyup",string);
	}

	/**
	 * @param string
	 */
	public void setOnmousedown(String string) {
		attributes.put("onmousedown",string);
	}

	/**
	 * @param string
	 */
	public void setOnmousemove(String string) {
		attributes.put("onmousemove",string);
	}

	/**
	 * @param string
	 */
	public void setOnmouseover(String string) {
		attributes.put("onmouseover",string);
	}

	/**
	 * @param string
	 */
	public void setOnmouseup(String string) {
		attributes.put("onmouseup",string);
	}

	/**
	 * @param string
	 */
	public void setOnselect(String string) {
		attributes.put("onselect",string);
	}

	/**
	 * @param b
	 */
	public void setPassword(boolean b) {
		password = b;
	}

	/**
	 * @param b
	 */
	public void setReadonly(String b) {
		attributes.put("readonly",b); 
		attributes.put("class", "readonly-diable");
	}

	/**
	 * @param string
	 */
	public void setSize(String string) {
		attributes.put("size",string);
	}

	/**
	 * @param string
	 */
	public void setStyle(String string) {
		attributes.put("style",string);
	}

	/**
	 * @param string
	 */
	public void setStyleclass(String string) {
		attributes.put("class",string);
	}

	/**
	 * @param string
	 */
	public void setTabindex(String string) {
		attributes.put("tabindex",string);
	}



	/**
	 * @return
	 */
	public Map getAttributes() {
		return attributes;
	}



	/**
	 * @param map
	 */
	public void setAttributes(Map map) {
		attributes = map;
	}



}
