<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>
<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>

<%
String loginFlag = (String)session.getAttribute("loginFlag");
if ("T".equalsIgnoreCase(loginFlag)) {
	session.removeAttribute("loginFlag");
%>
<script language="JavaScript">
	var w;
	//w=window.open("","csmcGuiMainFrame");
	//w.close();
	w=window.open("<ofbiz:url>/view</ofbiz:url>","csmcPmsMainFrame","width="+(screen.availWidth)+",height="+(screen.availHeight)+",toolbar=0,location=0,directories=0,status=2,menubar=0,scrollbars=2,resizable=2,copyhistory=0");

	w.moveTo(-4,-4);
	w.resizeTo(screen.availWidth+8, screen.availHeight+8);
	window.close();
</script>
<%
}
%>

<html>
<head>
<title>PMS Login</title>
<script language='javascript' src='<%=request.getContextPath()%>/function/function.js' type='text/javascript'></script>
<link href="<%=request.getContextPath()%>/images/csmc.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath()%>/images/default.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/pms/yui-ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/pms/css/dialog.css" />

<!--include yui script-->
<script type="text/javascript" src="<%=request.getContextPath()%>/pms/yui-ext/adapter/yui/yui-utilities.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/pms/yui-ext/adapter/yui/ext-yui-adapter.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/pms/yui-ext/ext-all-debug.js"></script>
</head>
<body bgcolor="white" leftmargin="0" topmargin="0">
<form method="post" action="<ofbiz:url>/login</ofbiz:url>" name="loginform">
<table width="100%" height="100%"  border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td height="336" align="center" valign="center" bgcolor="#DDDDDD"><TABLE WIDTH=664 BORDER=0 CELLPADDING=0 CELLSPACING=0>
      <TR>
        <TD COLSPAN=3><IMG SRC="<%=request.getContextPath()%>/images/index_01.jpg" WIDTH=664 HEIGHT=117 ALT=""></TD>
      </TR>
      <TR>
        <TD ROWSPAN=6><IMG SRC="<%=request.getContextPath()%>/images/index_02.jpg" WIDTH=215 HEIGHT=255 ALT=""></TD>
        <TD height="21" bgcolor="#9C3818">
          <input type="text" name="USERNAME" class="input" size="15" onkeydown="deEnter();" /></TD>
        <TD ROWSPAN=6><IMG SRC="<%=request.getContextPath()%>/images/index_04.jpg" WIDTH=295 HEIGHT=255 ALT=""></TD>
      </TR>
      <TR>
        <TD><IMG SRC="<%=request.getContextPath()%>/images/index_05.jpg" WIDTH=154 HEIGHT=23 ALT=""></TD>
      </TR>
      <TR>
        <TD height="24" bgcolor="#9C3818">
            <input type="password" name="PASSWORD" class="input" size="15" onkeydown="deEnter();" />
        </TD>
      </TR>
      <TR>
        <TD><IMG SRC="<%=request.getContextPath()%>/images/index_07.jpg" WIDTH=154 HEIGHT=9 ALT=""></TD>
      </TR>
      <TR>
        <TD height="40" bgcolor="#9C3818" align="center">&nbsp;<table border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
            <td background="<%=request.getContextPath()%>/images/button_bg.gif"><a href="javascript:submitform();" class="cn12px">登录</a></td>
            <td><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
          </tr>
        </table></TD>
      </TR>
      <TR>
        <TD><IMG SRC="<%=request.getContextPath()%>/images/index_09.jpg" WIDTH=154 HEIGHT=138 ALT=""></TD>
      </TR>
    </TABLE></td>
  </tr>
</table>
</form>
</body>
</html>

<script language="JavaScript">

<%
String errorMsgReq = (String) request.getAttribute("_ERROR_MESSAGE_");
if (errorMsgReq != null) {
	request.removeAttribute("_ERROR_MESSAGE_");
%>
	Ext.MessageBox.alert('警告', '<%=UtilFormatOut.replaceString(errorMsgReq, "\n", "<br>")%>');
<%}
%>

document.loginform.elements["USERNAME"].focus();

function submitform(){
	// if(Ext.getDom("USERNAME").value=="") {
	// 	Ext.MessageBox.alert('警告','用户名不能为空');
	// 	return;
	// }
	// if(Ext.getDom("PASSWORD").value=="") {
	// 	Ext.MessageBox.alert('警告','密码不能为空');
	// 	return;
	// }

	<%--if (<%=Constants.CALL_ASURA_FLAG%>) {--%>
  <%--  var retxtpwd = new RegExp(/^(?![0-9]+$)(?![a-z]+$)(?![A-Z]+$)^.{8,20}$/);--%>
  <%--  if (!retxtpwd.test(Ext.getDom("PASSWORD").value)) {--%>
	<%--	  Ext.MessageBox.alert('警告','密码必须包含大写字母,小写字母,数字，且长度为8-20位才能使用，请到FactoryWorks更改密码！');--%>
	<%--	  //验证不通过--%>
  <%--    return ;--%>
  <%--  }--%>
  <%--}--%>

	document.loginform.submit();
}

function deEnter(){
var iCode=event.keyCode;
	if(iCode==13)
	 {
	 	submitform();
	}
}
</script>