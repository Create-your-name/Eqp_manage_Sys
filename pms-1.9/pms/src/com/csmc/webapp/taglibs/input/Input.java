/*
 * Created on 2004-7-13
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.csmc.webapp.taglibs.input;

import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;


/**
 * @author Sky
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Input  extends BaseAbstractTag{
	String type;
	/**
	 * @url element://model:project::csmcgui/design:view:::V21xx84dxoccemz21bggj
	 */

	public int doStartTag() throws JspException {
		try {
			// sanity check
			if (name == null || name.equals(""))
				throw new JspTagException("invalid null or empty 'name'");

			// get what we need from the page
			ServletRequest req = pageContext.getRequest();
			JspWriter out = pageContext.getOut();
			HttpSession session = pageContext.getSession();


			// start building up the tag
			out.print("<input type=");
			if(this.isPassword()){
				out.print("\"password\" ");
			}else{
				out.print("\""+type+"\" ");
			}
			out.print("name=\"" + Util.quote(name) + "\" ");
			// include any attributes we've got here
			Util.printAttributes(out, attributes);

			 if (defaultValue != null) {
					out.print("value=\"" + Util.quote(defaultValue) + "\" ");
			} else {

				if (req.getAttribute(name) != null)
					out.print("value=\""
							  + Util.quote(req.getAttribute(name).toString()) + "\" ");							  
				else if (session.getAttribute(name) != null)
					out.print("value=\""
							  + Util.quote(session.getAttribute(name).toString()) + "\" ");
				else if (req.getParameter(name) != null && !req.getParameter(name).equals(""))
				out.print("value=\""
						  + Util.quote(req.getParameter(name)) + "\" ");
				else
					out.print("value=\"\" ");
			}

			// end the tag
			out.print("/>");  
			//add by sky
			//if validate is required ,do this.


		} catch (Exception ex) {
			throw new JspTagException(ex.getMessage());
		}
		return SKIP_BODY;
	}	

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param string
	 */
	public void setType(String string) {
		type = string;
	}

}
