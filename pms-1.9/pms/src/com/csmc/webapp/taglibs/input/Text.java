package com.csmc.webapp.taglibs.input;

import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import javax.servlet.jsp.tagext.*;



/**
 *
 *  This class implements the &lt;input:text&gt; tag, which presents an
 *  &lt;input type="text" ... /&gt; form element.
 *
 *  @version 0.90
 *  @author Shawn Bayern
 *  @author Lance Lavandowska
 */

public class Text extends Input {

	public int doStartTag() throws JspException {
		this.type = "text";
		return super.doStartTag();
	}


}
