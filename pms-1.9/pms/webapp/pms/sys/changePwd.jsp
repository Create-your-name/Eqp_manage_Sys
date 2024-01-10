<%@page contentType='text/html; charset=gb2312'%>
<%if (request.getAttribute("changePwdResult") != null) {%>
	<SCRIPT LANGUAGE="JavaScript">
		window.setTimeout("window.close()",2000)
	</SCRIPT>	
<%
return;
}%>

<FORM ID="changePwdForm" NAME="changePwdForm" METHOD=POST ACTION="<%=request.getContextPath()%>/control/changePwdDone">
	<FIELDSET><LEGEND>修改密码</LEGEND>
      	<TABLE WIDTH="100%" BORDER="0" CELLSPACING="1" CELLPADDING="0">  
        	<TR BGCOLOR="#DFE1EC"> 
          		<TD CLASS="en11pxb" WIDTH="40%">旧密码:</TD>
          		<TD WIDTH="40%"> 
          			<INPUT TYPE="password" ID="oldPwd" NAME="oldPwd" CLASS="input" VALUE=""><FONT COLOR="red">*</FONT>
            	</TD>
          		<TD CLASS="en11pxb" WIDTH="20%">&nbsp;</TD>
          	</TR>
          	<TR BGCOLOR="#DFE1EC"> 
          		<TD CLASS="en11pxb" WIDTH="40%">新密码:</TD>
          		<TD WIDTH="40%"> 
          			<INPUT TYPE="password" ID="newPwd" NAME="newPwd" CLASS="input" VALUE=""><FONT COLOR="red">*</FONT>
            	</TD>
          		<TD CLASS="en11pxb" WIDTH="20%">&nbsp;</TD>
          	</TR>
          	<TR BGCOLOR="#DFE1EC"> 
          		<TD CLASS="en11pxb" WIDTH="40%">确认新密码:</TD>
          		<TD WIDTH="40%"> 
          			<INPUT TYPE="password" ID="confirmPwd" NAME="confirmPwd" CLASS="input" VALUE=""><FONT COLOR="red">*</FONT>
            	</TD>
          		<TD CLASS="en11pxb" WIDTH="20%">&nbsp;</TD>
          	</TR>
        	
    	</TABLE>    
	</FIELDSET> 
	   
	<TABLE WIDTH="100%" HEIGHT="35" BORDER="0" CELLPADDING="0" CELLSPACING="0">
  		<TR>
    		<TD WIDTH="1%"><IMG NAME="" SRC="" WIDTH="20" HEIGHT="1" ALT=""></TD>
    		<TD WIDTH="99%">
    			<TABLE HEIGHT="35" BORDER="0" CELLPADDING="0" CELLSPACING="0">
        			<TR> 
          				<TD> 
          					<TABLE BORDER="0" CELLSPACING="0" CELLPADDING="0">
              					<TR> 
                					<TD NOWRAP><IMG SRC="<%=request.getContextPath()%>/images/button_left.gif"></TD>
                					<TD NOWRAP BACKGROUND="<%=request.getContextPath()%>/images/button_bg.gif" ><A HREF="javascript:OK()" CLASS="button-text">确认</A></TD>
                					<TD NOWRAP><IMG SRC="<%=request.getContextPath()%>/images/button_right.gif"></TD>
              					</TR>
            				</TABLE>
            			</TD>
          				<TD>&nbsp;</TD>
          				<TD> 
          					<TABLE BORDER="0" CELLSPACING="0" CELLPADDING="0">
              					<TR> 
                					<TD NOWRAP><IMG SRC="<%=request.getContextPath()%>/images/button_left.gif"></TD>
                					<TD NOWRAP BACKGROUND="<%=request.getContextPath()%>/images/button_bg.gif" ><A HREF="javascript:Reset()" CLASS="button-text">重置</A></TD>
                					<TD NOWRAP><IMG SRC="<%=request.getContextPath()%>/images/button_right.gif"></TD>
              					</TR>
            				</TABLE>
            			</TD>
        			</TR>
      			</TABLE>
      		</TD>
  		</TR>
	</TABLE>
</FORM>

<SCRIPT LANGUAGE="JavaScript">
	function OK(){
		if (document.changePwdForm.oldPwd.value==""){
			window.alert("请输入旧密码!")
			return
		}
		if (document.changePwdForm.newPwd.value==""){
			window.alert("请输入新密码!")
			return
		}
		if (document.changePwdForm.confirmPwd.value==""){
			window.alert("请再次确认新密码!")
			return
		}
		if (document.changePwdForm.newPwd.value!=document.changePwdForm.confirmPwd.value){
			window.alert("新密码输入与确认值不相等!")
			return
		}
		document.forms["changePwdForm"].submit()
	}
	function Reset(){
		document.forms["changePwdForm"].reset()
	}
</SCRIPT>




