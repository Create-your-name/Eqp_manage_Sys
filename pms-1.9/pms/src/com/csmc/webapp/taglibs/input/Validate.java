package com.csmc.webapp.taglibs.input;


import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import org.apache.taglibs.i18n.*;

import com.csmc.webapp.taglibs.model.CheckRules;



public class Validate extends BodyTagSupport {
  private String validates;
  private String formname;   //validates of the <input> element
private static Map defaultMessageMap =  new HashMap();
static{
	defaultMessageMap.put("MSG_LENGTH_RULE","msg.validate.msg_length_rule");
	defaultMessageMap.put("MSG_REQUIRED_RULE","msg.validate.msg_required_rule");
	defaultMessageMap.put("MSG_NUMERIC_RULE","msg.validate.msg_numeric_rule");
	defaultMessageMap.put("MSG_DATE_RULE","msg.validate.msg_date_rule");
	defaultMessageMap.put("MSG_TIME_RULE","msg.validate.msg_time_rule");
	defaultMessageMap.put("MSG_RANGE_RULE","msg.validate.msg_range_rule");
	defaultMessageMap.put("MSG_MASK_RULE","msg.validate.msg_mask_rule");
	defaultMessageMap.put("MSG_EMAIL_RULE","msg.validate.msg_email_rule");
	defaultMessageMap.put("MSG_LONG_RULE","msg.validate.msg_long_rule");
	defaultMessageMap.put("MSG_LIMIT_RULE","msg.validate.msg_limit_rule");

}

    public int doStartTag() throws JspException {
        Map validateMap = new HashMap();
        validateMap.put("required",new ArrayList());
        validateMap.put("length",new ArrayList());
        validateMap.put("range",new ArrayList());
        validateMap.put("numeric",new ArrayList());
        validateMap.put("date",new ArrayList());
        validateMap.put("time",new ArrayList());
        validateMap.put("mask",new ArrayList());
        validateMap.put("email",new ArrayList());
        validateMap.put("long", new ArrayList());
		validateMap.put("limit", new ArrayList());

        pageContext.setAttribute("validateMap",validateMap);

        // Continue processing this page
        return (EVAL_BODY_TAG);
    }

    /**
     * Save any body content of this tag, which will generally be the
     * option(s) representing the values displayed to the user.
     *
     * @exception JspException if a JSP exception has occurred
     */
 public int doAfterBody() throws javax.servlet.jsp.JspException{
    try {
      getBodyContent().writeOut(getPreviousOut());
    } catch (IOException ioe) {
      throw new JspTagException(ioe.toString());
    }
   return SKIP_BODY;
  }

  public int doEndTag() throws JspException{

    JspWriter out = pageContext.getOut();


    StringBuffer sb = new StringBuffer();
    sb.append(getValidateScreen());

    try {
      out.print(sb.toString());
    } catch(IOException ioe) {
      ioe.printStackTrace();
        //throw new JspException("Failed to write label");
    }


    // Continue processing this page
    return (EVAL_PAGE);
  }



  private String getValidateScreen(){
    Map validateMap = (Map)pageContext.getAttribute("validateMap");
    if(validateMap == null){
        return "";
    }
    StringBuffer sb = new StringBuffer();
    sb.append("<script language=\"javascript1.2\">          \n");
    sb.append(" var sType='novalidate';                     \n");

    Set validateRuleSet = validateMap.keySet();
    Iterator it = validateRuleSet.iterator();
    while(it.hasNext()){
      String validateRule = (String)it.next();
      List fieldList = (List)validateMap.get(validateRule);
      if(fieldList.size() > 0 ){
      /** @todo  */
          Map messages = this.getMessages();
          sb.append(getCheckRuleMethods(parseValidateRule(validateRule),fieldList,messages));
        }
    }
    sb.append(makeValidateMethod(validateMap));
    sb.append("</script>");
    return sb.toString();
  }

  private int parseValidateRule(String validateRule){
    if(validateRule.equalsIgnoreCase("length")){
        return CheckRules.CHK_LENGTH;
    }else if(validateRule.equalsIgnoreCase("required")){
        return CheckRules.CHK_REQUIRED;
    }else if(validateRule.equalsIgnoreCase("numeric")){
        return CheckRules.CHK_NUMERIC;
    }else if(validateRule.equalsIgnoreCase("date")){
        return CheckRules.CHK_DATE;
    }else if(validateRule.equalsIgnoreCase("time")){
        return CheckRules.CHK_TIME;
    }else if(validateRule.equalsIgnoreCase("range")){
        return CheckRules.CHK_RANGE;
    }else if(validateRule.equalsIgnoreCase("mask")){
        return CheckRules.CHK_MASK;
    }else if(validateRule.equalsIgnoreCase("email")){
        return CheckRules.CHK_EMAIL;
    }else if(validateRule.equalsIgnoreCase("long")) {
        return CheckRules.CHK_LONG;
    }else if(validateRule.equalsIgnoreCase("limit")) {
		return CheckRules.CHK_LIMIT;
	}else{
        throw new IllegalArgumentException("Incorrect type code value!");
    }
  }   
    private String getCheckRuleMethods(int checkRule,Collection fields,Map messages){
        StringBuffer sb = new StringBuffer();
        CheckRules checkRules = null;
        for(Iterator it = fields.iterator();it.hasNext();){
            Map field = (Map)it.next();
			String content = null;
            if (field.get("label") != null) {
				content = this.getResourceValue((String)field.get("label"));
            } else {
				
            }
            
			if(content == null){
				content = (String)field.get("label");
			}
						
            if(content == null){
                content = (String)field.get("fieldName");
            }
            
            checkRules = CheckRules.create(checkRule + "(" + (String)field.get("validateProperty") + ")",
                                            content,
                                            (String)field.get("fieldName"),
                                            messages);
            sb.append(checkRules.constructCheckMethod());
        }
        try{
        return  checkRules.makeMethod(sb.toString());
        }catch(Exception ex){
            throw new IllegalArgumentException("Incorrect type code value!");
        }
    }
 
    private String makeValidateMethod(Map validateMap){
        StringBuffer sbValidate = new StringBuffer();
        sbValidate.append("function validate(form){	      \n");
        sbValidate.append("if(sType=='novalidate') return;          \n");
        sbValidate.append("if(sType=='delete'){                     \n");
        sbValidate.append("sType='novalidate';                      \n");
        sbValidate.append("return validateDelete();}                \n");
        sbValidate.append("sType='novalidate';                      \n");
        sbValidate.append("   try{                                  \n");
        sbValidate.append("	var msg;                              \n");
        Set validateRuleSet = validateMap.keySet();

        for(Iterator it = validateRuleSet.iterator();it.hasNext();){
            String validateRule = (String)it.next();
            if(validateMap.get(validateRule) != null &&
                    ((Collection)validateMap.get(validateRule)).size()>0){
                if(parseValidateRule(validateRule) == CheckRules.CHK_REQUIRED){
                    sbValidate.append("	msg = validateRequired(form);         \n");
                    sbValidate.append("   if( msg.length!= 0 ) {alert(msg);    \n");
                    sbValidate.append("   return false; }                       \n");
                } else if(parseValidateRule(validateRule) == CheckRules.CHK_NUMERIC){

                    sbValidate.append("   msg = validateNumeric(form);          \n");
                    sbValidate.append("   if( msg.length!= 0 ) {alert(msg);    \n");
                    sbValidate.append("   return false; }                       \n");
                } else if(parseValidateRule(validateRule) == CheckRules.CHK_LENGTH){
                    sbValidate.append("   msg = validateLength(form);          \n");
                    sbValidate.append("   if( msg.length!= 0 ) {alert(msg);    \n");
                    sbValidate.append("   return false; }                       \n");
                } else if(parseValidateRule(validateRule) == CheckRules.CHK_RANGE){
                    sbValidate.append("   msg = validateRange(form);          \n");
                    sbValidate.append("   if( msg.length!= 0 ) {alert(msg);    \n");
                    sbValidate.append("   return false; }                       \n");
                } else if(parseValidateRule(validateRule) == CheckRules.CHK_EMAIL){
                    sbValidate.append("   msg = validateEmail(form);          \n");
                    sbValidate.append("   if( msg.length!= 0 ) {alert(msg);    \n");
                    sbValidate.append("   return false; }                       \n");
                } else if(parseValidateRule(validateRule) == CheckRules.CHK_DATE){
                    sbValidate.append("   msg = validateDate(form);          \n");
                    sbValidate.append("   if( msg.length!= 0 ) {alert(msg);    \n");
                    sbValidate.append("   return false; }                       \n");
                } else if(parseValidateRule(validateRule) == CheckRules.CHK_TIME){
                    sbValidate.append("   msg = validateTime(form);          \n");
                    sbValidate.append("   if( msg.length!= 0 ) {alert(msg);    \n");
                    sbValidate.append("   return false; }                       \n");
                } else if(parseValidateRule(validateRule) == CheckRules.CHK_MASK){
                    sbValidate.append("   msg = validateMask(form);          \n");
                    sbValidate.append("   if( msg.length!= 0 ) {alert(msg);    \n");
                    sbValidate.append("   return false; }                       \n");
                } else if(parseValidateRule(validateRule) == CheckRules.CHK_LONG) {
                    sbValidate.append("   msg = validateLong(form);           \n");
                    sbValidate.append("   if( msg.length!= 0 ) {alert(msg);    \n");
                    sbValidate.append("   return false; }                      \n");
                }  else if(parseValidateRule(validateRule) == CheckRules.CHK_LIMIT) {
					sbValidate.append("   msg = validateLimit(form);           \n");
					sbValidate.append("   if( msg.length!= 0 ) { if(!confirm(msg)) {	\n");
					sbValidate.append("   if(form.elements[\"qcOosFlag\"]!=null) {	\n");
					sbValidate.append("   form.elements[\"qcOosFlag\"].value = \"N\"}	\n");	
					sbValidate.append("   return false;} else {		\n");
					sbValidate.append("   if(form.elements[\"qcOosFlag\"]!=null) {	\n");
					sbValidate.append("   form.elements[\"qcOosFlag\"].value = \"Y\"}	\n");
					sbValidate.append(		"}} \n");
				}
            }

        }
        sbValidate.append("     msg = validateAdhoc();        \n");
        sbValidate.append("     if( msg.length!= 0 ) {alert(msg);   \n");
        sbValidate.append("     return false; }                     \n");
        //no error occurs even that method does not exist.
        sbValidate.append("   }catch(e){}                           \n");
        sbValidate.append("   return true; }                        \n");
        return sbValidate.toString();
    }
    public Map getMessages(){
        Map messages = new HashMap();
        messages.put("MSG_LENGTH_RULE",getResourceValue("msg.validate.msg_length_rule"));
        messages.put("MSG_REQUIRED_RULE",getResourceValue("msg.validate.msg_required_rule"));
        messages.put("MSG_NUMERIC_RULE",getResourceValue("msg.validate.msg_numeric_rule"));
        messages.put("MSG_DATE_RULE",getResourceValue("msg.validate.msg_date_rule"));
        messages.put("MSG_TIME_RULE",getResourceValue("msg.validate.msg_time_rule"));
        messages.put("MSG_RANGE_RULE",getResourceValue("msg.validate.msg_range_rule"));
        messages.put("MSG_MASK_RULE",getResourceValue("msg.validate.msg_mask_rule"));
        messages.put("MSG_EMAIL_RULE",getResourceValue("msg.validate.msg_email_rule"));
        messages.put("MSG_LONG_RULE",getResourceValue("msg.validate.msg_long_rule"));
		messages.put("MSG_LIMIT_RULE",getResourceValue("msg.validate.msg_limit_rule"));
        return messages;
    }

    public String getResourceValue(String key){
		String value = null;
    	try {
    	    ResourceBundle bundle = ResourceHelper.getBundle(pageContext);
	        if(bundle != null){
	            value = bundle.getString(key);
	        }else{
	    		//value = (String)defaultMessageMap.get(key);
	        }
    	} catch (MissingResourceException e) {
    		value = key;
    	}
        return value;
    }


    public void setFormname(String formname) {
        this.formname = formname;
    }
    public String getFormname() {
        return formname;
    }





}