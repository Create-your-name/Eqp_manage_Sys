package com.csmc.webapp.taglibs.input;

import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.http.*;




/**
 * The   xxxxxx
 * @author      FA-Software
 * @version	1.0
 * Created on	2002/05/13
 */

public class Select extends BaseAbstractTag{

    private Object options;
    private String nValue;
	private boolean multiple;
    public void setOptions(Object x) {   
        options = x;
    }

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
            out.print("<select name=\"" + Util.quote(name) + "\" ");

            // include any attributes we've got here
            Util.printAttributes(out, attributes);
			if(multiple){
				out.print(" multiple ");
			}
            // end the starting tag
            out.println(">");
  
            /*
             * Print out our options, selecting one or more if appropriate.
             * If there are multiple selections but the page doesn't call
             * for a <select> that accepts them, ignore the selections.
             * This is preferable to throwing a JspException because the
             * (end) user can control input, and we don't want the user
             * causing exceptions in our application.
             */

            // get the current selection
             String[] selected;
             if (defaultValue != null) {
                 selected = new String[]{defaultValue};
             } else {
                 selected = req.getParameterValues(name);
             }

            if (selected != null && selected.length > 1 &&
                    (attributes == null ||
                    !attributes.containsKey("multiple")))
                selected = null;

            if (selected == null) {
                if (session.getAttribute(name) != null) {
                    selected = new String[1];
                    selected[0] = session.getAttribute(name).toString();
                }
            }

            // load up the selected values into a hash table for faster access
            HashMap chosen = new HashMap();
            if (selected != null)
                for (int i = 0; i < selected.length; i++)
                    chosen.put(selected[i], null);
            // actually print the <option> tags
            if (options != null) {
                Iterator i = null;
                if(options instanceof Collection){
                    i = ((Collection)options).iterator();
                }else if(options instanceof Map){
                    i = ((Map)options).keySet().iterator();
                }
                if(i != null){
                    while (i.hasNext()) {
                        Object oKey = null;
                        Object oVal = null;
                        if(options instanceof Collection){
                            Object o = i.next();
                            if(o instanceof java.util.Map){
                            	Map item = (Map)o;
                            oVal = item.get("value");
                            oKey = item.get("key");
                            }else{
								oVal =o;
								oKey =o;                            
                            }
                        }else if(options instanceof Map){
                            oKey = i.next();
                            oVal = ((Map)options).get(oKey);
                        }


                        /* If the option contains non-Strings, give the user
                         * a more meaningful message than what he or she would get
                         * if we just propagated a ClassCastException back.
                         * (This'll get caught below).
                         */
                        if (!(oKey instanceof String) ||
                                (oVal != null && !(oVal instanceof String)))
                            throw new JspException(
                                "all members in options Map must be Strings");
                        String key = (String) oKey;
                        String value = (String) oVal;
                        out.print("<option");
                        if (value!=null)
                            out.print(" value=\"" + Util.quote(value) + "\"");
                        /*
                         * This may look confusing: we match the VALUE of
                         * this option pair with the KEY of the 'chosen' Map
                         * (We want to match <option>s on values, not keys.)
                         */
                        //Tony modify
                        if ((selected != null && chosen.containsKey(value))
                                || (selected == null && defaultValue != null &&
                                value.equals(defaultValue))) {
                            out.print(" selected=\"selected\"");
                        }
                        out.print(">");
                        out.print(quote(key));
                        out.println("</option>");
                    }
                }
            } else
                //throw new JspTagException("invalid select: no options");

            // close off the surrounding select
            out.print("</select>");

        } catch (Exception ex) {
			//ex.printStackTrace();
            throw new JspTagException(ex.getMessage());  
        }
        return SKIP_BODY;
    }

    /** Quote metacharacters in HTML. */
    public String quote(String x) {
        if (x == null)
            return null;
        else {
            // deal with ampersands first so we can ignore the ones we add later
            int c, oldC = -1;
            while ((c = x.indexOf('"')) != -1)
                x = new String((new StringBuffer(x)).replace(c, c+1, "&quot;"));
            while ((c = x.indexOf('<')) != -1)
                x = new String((new StringBuffer(x)).replace(c, c+1, "&lt;"));
            while ((c = x.indexOf('>')) != -1)
                x = new String((new StringBuffer(x)).replace(c, c+1, "&gt;"));
            return x;
        }
    }
    public String getNValue() {
        return nValue;
    }
    public void setNValue(String nValue) {
        this.nValue = nValue;
    }
	/**
	 * @return
	 */
	public boolean isMultiple() {
		return multiple;   
	}

	/**
	 * @param b  
	 */
	public void setMultiple(boolean b) {
		multiple = b;
	}

}
