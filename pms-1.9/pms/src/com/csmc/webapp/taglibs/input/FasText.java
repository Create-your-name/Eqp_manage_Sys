package com.csmc.webapp.taglibs.input;


import javax.servlet.jsp.*;

public class FasText extends Text {
  private String validates;
  private String label;//validates of the <input> element

  public void setValidates(String validates) {
	this.validates = validates;
  }
  
 
  public int doStartTag() throws JspException {
	try {
		if( !(this.validates == null || this.validates.equals("")) ){
		  Util.registerValidateField(this.pageContext,this.name,this.label,this.validates);
		}
	} catch (JspTagException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return super.doStartTag();
  }


	public void setLabel(String displayname) {
		this.label = displayname;
	}


}