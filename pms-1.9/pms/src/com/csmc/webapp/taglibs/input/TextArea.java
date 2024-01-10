package com.csmc.webapp.taglibs.input;

import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.http.*;
import javax.servlet.*;

/**
 *
 *  This class implements the &lt;input:textarea&gt; tag, which presents a
 *  &lt;textarea&gt; form element.
 *
 *  @version 0.90
 *  @author Shawn Bayern
 *  @author Lance Lavandowska
 */
  
public class TextArea extends BaseAbstractTag {

    private String dVal;        // default value if none is found
    private String label;
    private String validates;     // attributes of the <textarea> element


    public void setDefault(String x) {
        dVal = x;
    }

    public int doStartTag() throws JspException {
        try {

            if( !(this.validates == null || this.validates.equals("")) ){
              Util.registerValidateField(this.pageContext,name,label,validates);
            }
            // sanity check
            if (name == null || name.equals(""))
                throw new JspTagException("invalid null or empty 'name'");

            // get what we need from the page
            ServletRequest req = pageContext.getRequest();
            JspWriter out = pageContext.getOut();
			HttpSession session = pageContext.getSession();
			
            // start building up the tag
            out.print("<textarea name=\"" + Util.quote(name) + "\" ");

            // include any attributes we've got here
            Util.printAttributes(out, attributes);
            
			
            // end the starting tag
            out.print(">");

            /*
             * print out the value from the request if it's there, or
             * use the default value if it's not
             */
             /*
            if (req.getParameter(name) != null)
                out.print(Util.quote(req.getParameter(name)));
            else if (dVal != null)
                out.print(Util.quote(dVal));
            */
			
			if (defaultValue != null) {
			   out.print(Util.quote(defaultValue));
		   	} 
			
            // end the textarea
            out.print("</textarea>");

        } catch (Exception ex) {
            throw new JspTagException(ex.getMessage());
        }
        return SKIP_BODY;
    }



    public void setLabel(String displayname) {
        this.label = displayname;
    }
    public String getLabel() {
        return label;
    }
    public void setValidates(String validates) {
        this.validates = validates;
    }
    public String getValidates() {
        return validates;
    }
}
