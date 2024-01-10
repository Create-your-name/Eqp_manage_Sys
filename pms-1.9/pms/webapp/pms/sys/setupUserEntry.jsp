<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ taglib uri="input" prefix="input" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>
<%@ page import="org.ofbiz.entity.*" %>


<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
  <form method=POST action='<%=request.getContextPath()%>/control/viewUserInfo' onsubmit="return validate()" name="setupUserEntryForm">
  <fieldset> <legend>User Query </legend>
        <table width="100%" border="0" cellspacing="1" cellpadding="0">
        <tr bgcolor="#DFE1EC">
          <td width="10%" class="en11pxb">用户名</td>
          <td width="90%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td height="26" class="en11pxb" width="16%">
            <input:text name="accountid" styleclass="input" maxlength="20" />
          </td>
                <td width="80%" align="left"><a href="<%=request.getContextPath()%>/control/queryUser"><img src="<%=request.getContextPath()%>/images/icon_search.gif" width="15" height="16" border="0"></a></td>
              </tr>
            </table>
          </td>
          </tr>
      </table>
</table>
<table width="100%" height="35"  border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td width="1%"><img name="" src="" width="20" height="1" alt=""></td>
    <td width="99%"><table height="35" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td> <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="javascript:submitform();" class="button-text">确定</a></td>
                <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
              </tr>
            </table></td>
          <td>&nbsp;</td>
		  <td> <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="user_setup_query.htm" class="button-text">重置</a></td>
                <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
              </tr>
            </table></td>
          <td>&nbsp;</td>

        </tr>
      </table></td>
  </tr>
</table>
</form>
<script language="JavaScript">
<!--
  document.setupUserEntryForm.accountid.focus();
//-->
function submitform(){
if(!validate()){
	return;
}
else{
	document.setupUserEntryForm.submit();
}
}
function validate(){
if(document.setupUserEntryForm.accountid.value.length==0){
	alert('用户名不能为空！');
	return false;
}else{
	return true;
}
}
</script>

