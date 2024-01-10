/*
 *	@ Copyright 2001 FA Software;
 *	All right reserved. No part of this program may be reproduced or
 *	transmitted in any form or by any means, electronic or
 *	mechanical, including photocopying, recording, or by any
 *	information storage or retrieval system without written
 *	permission from FA Software, except for inclusion of brief
 *	quotations in a review.
 */
package com.csmc.webapp.taglibs.model;

/**
 * Assistant object for field
 * @author      Jessica
 * @version	1.0
 * Created on
 */
import java.util.Map;

public abstract class CheckRules{

    public final static int CHK_LENGTH = 0;
    public final static int CHK_REQUIRED = 1;
    public final static int CHK_NUMERIC = 2;
    public final static int CHK_DATE = 3;
    public final static int CHK_TIME = 4;
    public final static int CHK_RANGE = 5;
    public final static int CHK_MASK = 6;
    public final static int CHK_EMAIL = 7;
    public final static int CHK_LONG = 8;
	public final static int CHK_LIMIT = 9;
	
    protected String text;
    protected String property;
    protected String context;
    protected Map messages;

    public abstract String constructCheckMethod();

    protected String makeMethod(String method, String content){
      StringBuffer sb = new StringBuffer();
      sb.append("function " + method+ "{                \n");
      sb.append("   msg = \"\";                         \n");
      sb.append( content + "\n");
      sb.append("   return msg;                         \n");
      sb.append("}                                      \n");
      return sb.toString();
    }
    public abstract String makeMethod(String content);

    private String getMessageContext(int messageType,Map messages){
      if(messageType==CHK_LENGTH)
        return (messages.get("MSG_LENGTH_RULE")==null)?"$1 必须小于 $2":(String)messages.get("MSG_LENGTH_RULE");
      if(messageType==CHK_REQUIRED)
        return (messages.get("MSG_REQUIRED_RULE")==null)?"$1 是必填栏位":(String)messages.get("MSG_REQUIRED_RULE");
      if(messageType==CHK_NUMERIC)
        return (messages.get("MSG_NUMERIC_RULE")==null)?"$1 必须是一个数字":(String)messages.get("MSG_NUMERIC_RULE");
      if(messageType==CHK_DATE)
        return (messages.get("MSG_DATE_RULE")==null)?"$1 必须是一个日期":(String)messages.get("MSG_DATE_RULE");
      if(messageType==CHK_TIME)
        return (messages.get("MSG_TIME_RULE")==null)?"$1 必须是一个时间":(String)messages.get("MSG_TIME_RULE");
      if(messageType==CHK_RANGE)
        return (messages.get("MSG_RANGE_RULE")==null)?"$1 必须在范围($2-$3)之内":(String)messages.get("MSG_RANGE_RULE");
      if(messageType==CHK_MASK)
        return (messages.get("MSG_MASK_RULE")==null)?"$1 is mask":(String)messages.get("MSG_MASK_RULE");
      if(messageType==CHK_EMAIL)
        return (messages.get("MSG_EMAIL_RULE")==null)?"$1 必须是一个Email地址":(String)messages.get("MSG_EMAIL_RULE");
      //if(messageType.equals(CHK_DELETE))
      //  return (messages.get("MSG_DELETE")!=null)?"Are you sure?":(String)messages.get("MSG_DELETE");
      if(messageType==CHK_LONG)
          return (messages.get("MSG_LONG_RULE")==null)?"$1 必须是一个整数":(String)messages.get("MSG_LONG_RULE");
	  if(messageType==CHK_LIMIT)
		  return (messages.get("MSG_LIMIT_RULE")==null)?"$1 超出限制($2-$3)":(String)messages.get("MSG_LIMIT_RULE");

      return null;
    }

    protected String getMessage(String lblText,String value,String value1,int messageType,Map messages){
      StringBuffer sb = new StringBuffer();
      try{
      sb.append(getMessageContext(messageType,messages));
      String context = getMessageContext(messageType,messages);
      int pos1 = context.indexOf("$1");
      int pos2 = context.indexOf("$2");
      int pos3 = context.indexOf("$3");

      if(pos3>=0){
        sb.replace(pos3,pos3+2,value1);
      }

      if(pos2>=0){
        sb.replace(pos2,pos2+2,value);
      }

      if(pos1>=0){
        sb.replace(pos1,pos1+2,lblText);
      }

      return sb.toString();
        }catch(Exception ex){
            //ex.printStackTrace();
            throw new IllegalArgumentException("Incorrect type code value!");
        }
    }

    public static CheckRules create(String context,String text,String property,Map messages){
        switch(Integer.parseInt(context.substring(0,1))){
        case CHK_LENGTH   : return new CheckLengthRule(context,text,property,messages);
        case CHK_REQUIRED : return new CheckRequiredRule(context,text,property,messages);
        case CHK_NUMERIC  : return new CheckNumericRule(context,text,property,messages);
        case CHK_DATE     : return new CheckDateRule(context,text,property,messages);
        case CHK_TIME     : return new CheckTimeRule(context,text,property,messages);
        case CHK_RANGE    : return new CheckRangeRule(context,text,property,messages);
        case CHK_MASK     : return new CheckMaskRule(context,text,property,messages);
        case CHK_EMAIL    : return new CheckEmailRule(context,text,property,messages);
        case CHK_LONG     : return new CheckLongRule(context, text,property,messages);
		case CHK_LIMIT     : return new CheckLimitRule(context, text,property,messages);
        default :
            throw new IllegalArgumentException("Incorrect type code value!");
        }
      }
  }

  class CheckLengthRule extends CheckRules{
      CheckLengthRule(String context,String text,String property,Map messages){
          this.text = text;
          this.property = property;
          this.context = context;
          this.messages = messages;
      }

      public String constructCheckMethod(){
          if (context.indexOf("(") == -1 || context.indexOf(")") == -1) return null;
          StringBuffer sb = new StringBuffer();
          sb.append("try{ if(Trim(form.elements[\"");
          sb.append(property);
          sb.append("\"].value).length>0&&getLength(form.elements[\"");
          sb.append(property);
          sb.append("\"].value)"+">");
          sb.append(context.substring(context.indexOf("(")+1,context.indexOf(")")));
          sb.append(")\n msg +=\"");
          sb.append(getMessage(text,context.substring(context.indexOf("(")+1,context.indexOf(")")),null,CHK_LENGTH,messages));
          sb.append("\\n\";\n}catch(e){}");
          return sb.toString();
      }
      public String makeMethod(String context){
        return super.makeMethod("validateLength(form)",context);
      }
    }

  class CheckRequiredRule extends CheckRules{
      CheckRequiredRule(String context,String text,String property,Map messages){
          this.text = text;
          this.property = property;
          this.context = context;
          this.messages = messages;
      }
      public String constructCheckMethod(){
          StringBuffer sb = new StringBuffer();
          sb.append("try{ if(Trim(form.elements[\"");
          sb.append(property);
          sb.append("\"].value).length==0)\n msg +=\"");
          sb.append(getMessage(text,null,null,CHK_REQUIRED,messages));
          sb.append("\\n\";\n}catch(e){}");

          return sb.toString();
      }
      public String makeMethod(String context){
        return super.makeMethod("validateRequired(form)",context);
      }
    }

    class CheckNumericRule extends CheckRules{
      CheckNumericRule(String context,String text,String property,Map messages){
          this.text = text;
          this.property = property;
          this.context = context;
          this.messages = messages;
      }
      public String constructCheckMethod(){
          StringBuffer sb = new StringBuffer();
          sb.append("try{ if(Trim(form.elements[\"");
          sb.append(property);
          sb.append("\"].value).length>0&&!IsNumeric(form.elements[\"");
          sb.append(property);
          sb.append("\"].value))\n msg +=\"");
          sb.append(getMessage(text,null,null,CHK_NUMERIC,messages));
          sb.append("\\n\";\n}catch(e){}");

          return sb.toString();
      }
      public String makeMethod(String context){
        return super.makeMethod("validateNumeric(form)",context);
      }
    }

    class CheckDateRule extends CheckRules{
      CheckDateRule(String context,String text,String property,Map messages){
          this.text = text;
          this.property = property;
          this.context = context;
          this.messages = messages;
      }
      public String constructCheckMethod(){
          StringBuffer sb = new StringBuffer();
          sb.append("try{ if(Trim(form.elements[\"");
          sb.append(property);
          sb.append("\"].value).length>0&&!IsDate(form.elements[\"");
          sb.append(property);
          sb.append("\"].value))\n msg +=\"");
          sb.append(getMessage(text,null,null,CHK_DATE,messages));
          sb.append("\\n\";\n}catch(e){}");

          return sb.toString();
      }
      public String makeMethod(String context){
        return super.makeMethod("validateDate(form)",context);
      }
    }

    class CheckTimeRule extends CheckRules{
      CheckTimeRule(String context,String text,String property,Map messages){
          this.text = text;
          this.property = property;
          this.context = context;
          this.messages = messages;
      }
      public String constructCheckMethod(){
          StringBuffer sb = new StringBuffer();
          sb.append("try{ if(Trim(form.elements[\"");
          sb.append(property);
          sb.append("\"].value).length>0&&!IsTime(form.elements[\"");
          sb.append(property);
          sb.append("\"].value))\n msg +=\"");
          sb.append(getMessage(text,null,null,CHK_TIME,messages));
          sb.append("\\n\";\n}catch(e){}");

          return sb.toString();
      }
      public String makeMethod(String context){
        return super.makeMethod("validateTime(form)",context);
      }

    }

    class CheckRangeRule extends CheckRules{
      CheckRangeRule(String context,String text,String property,Map messages){
          this.text = text;
          this.property = property;
          this.context = context;
          this.messages = messages;
      }
      public String constructCheckMethod(){
          String min = context.substring(context.indexOf("(")+1,context.indexOf("#"));
          String max = context.substring(context.indexOf("#")+1,context.indexOf(")"));
          StringBuffer sb = new StringBuffer();

          if(!min.equals("*")){
            sb.append("try{ if(Trim(form.elements[\"");
            sb.append(property);
            sb.append("\"].value).length>0&&form.elements[\"");
            sb.append(property);
            sb.append("\"].value"+"<");
            sb.append(min);
            sb.append(")\n msg +=\"");
            sb.append(getMessage(text,min,max,CHK_RANGE,messages));
            sb.append("\\n\";\n}catch(e){}");

          }
          if(!max.equals("*")){
            sb.append("try{ if(Trim(form.elements[\"");
            sb.append(property);
            sb.append("\"].value).length>0&&form.elements[\"");
            sb.append(property);
            sb.append("\"].value"+">");
            sb.append(max);
            sb.append(")\n msg +=\"");
            sb.append(getMessage(text,min,max,CHK_RANGE,messages));
            sb.append("\\n\";\n}catch(e){}");
          }

          return sb.toString();
      }
      public String makeMethod(String context){
        return super.makeMethod("validateRange(form)",context);
      }
    }

    class CheckMaskRule extends CheckRules{
      CheckMaskRule(String context,String text,String property,Map messages){
          this.text = text;
          this.property = property;
          this.context = context;
          this.messages = messages;
      }
      public String constructCheckMethod(){
          StringBuffer sb = new StringBuffer();
          sb.append("try{ if(Trim(form.elements[\"");
          sb.append(property);
          sb.append("\"].value).length>0&&(form.elements[\"");
          sb.append(property);
          sb.append("\"].value.search(");
          sb.append(context.substring(context.indexOf("(",1)+1,context.length()-1));
          sb.append(")==-1))\n msg +=\"");
          sb.append(getMessage(text,null,null,CHK_MASK,messages));
          sb.append("\\n\";\n}catch(e){}");

          return sb.toString();
      }
      public String makeMethod(String context){
        return super.makeMethod("validateMask(form)",context);
      }
    }
    class CheckEmailRule extends CheckRules{
      CheckEmailRule(String context,String text,String property,Map messages){
          this.text = text;
          this.property = property;
          this.context = context;
          this.messages = messages;
      }
      public String constructCheckMethod(){
          StringBuffer sb = new StringBuffer();
          sb.append("try{ if(Trim(form.elements[\"");
          sb.append(property);
          sb.append("\"].value).length>0&&!IsEmail(form.elements[\"");
          sb.append(property);
          sb.append("\"].value))\n msg +=\"");
          sb.append(getMessage(text,null,null,CHK_EMAIL,messages));
          sb.append("\\n\";\n}catch(e){}");

          return  sb.toString();
      }
      public String makeMethod(String context){
        return super.makeMethod("validateEmail(form)",context);
      }
    }
    class CheckLongRule extends CheckRules{
        CheckLongRule(String context,String text,String property,Map messages){
            this.text = text;
            this.property = property;
            this.context = context;
            this.messages = messages;
        }
        public String constructCheckMethod(){
            StringBuffer sb = new StringBuffer();
            sb.append("try{ if(Trim(form.elements[\"");
            sb.append(property);
            sb.append("\"].value).length>0&&!IsLong(form.elements[\"");
            sb.append(property);
            sb.append("\"].value))\n msg +=\"");
            sb.append(getMessage(text,null,null,CHK_LONG,messages));
            sb.append("\\n\";\n}catch(e){}");

            return sb.toString();
        }
        public String makeMethod(String context){
            return super.makeMethod("validateLong(form)",context);
        }
    }
    
class CheckLimitRule extends CheckRules{
	CheckLimitRule(String context,String text,String property,Map messages){
	  this.text = text;
	  this.property = property;
	  this.context = context;
	  this.messages = messages;
  }
  public String constructCheckMethod(){
	  String min = context.substring(context.indexOf("(")+1,context.indexOf("#"));
	  String max = context.substring(context.indexOf("#")+1,context.indexOf(")"));
	  StringBuffer sb = new StringBuffer();

	  if(!min.equals("*")){
		sb.append("try{ if(Trim(form.elements[\"");
		sb.append(property);
		sb.append("\"].value).length>0&&form.elements[\"");
		sb.append(property);
		sb.append("\"].value"+"<");
		sb.append(min);
		sb.append(")\n msg +=\"");
		sb.append(getMessage(text,min,max,CHK_LIMIT,messages));
		sb.append("\\n\";\n}catch(e){}");

	  }
	  if(!max.equals("*")){
		sb.append("try{ if(Trim(form.elements[\"");
		sb.append(property);
		sb.append("\"].value).length>0&&form.elements[\"");
		sb.append(property);
		sb.append("\"].value"+">");
		sb.append(max);
		sb.append(")\n msg +=\"");
		sb.append(getMessage(text,min,max,CHK_LIMIT,messages));
		sb.append("\\n\";\n}catch(e){}");
	  }

	  return sb.toString();
  }
  public String makeMethod(String context){
	return super.makeMethod("validateLimit(form)",context);
  }
}