<%@include file="/includes/envsetup.jsp" %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='regions' prefix='region' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@page contentType='text/html; charset=gb2312'%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title><region:render section='title'/></title>
<script language='javascript' src='<%=request.getContextPath()%>/images/calendar1.js' type='text/javascript'></script>
<script language='javascript' src='<%=request.getContextPath()%>/function/function.js' type='text/javascript'></script>

<link rel='stylesheet' href='<%=request.getContextPath()%>/images/maincss.css' type='text/css'>
<link rel='stylesheet' href='<%=request.getContextPath()%>/images/tabstyles.css' type='text/css'>

<link href="<%=request.getContextPath()%>/images/default.css" rel="stylesheet" type="text/css">
<script language="JavaScript" type="text/JavaScript">
	function ClickX(){
		window.event.returnValue=false;
	}
	document.oncontextmenu=ClickX;
</script>
</head>
<form method="POST" name="_main_template_frame" action='<%=request.getContextPath()%>/control/optConsoleEntry'>
</form>
<input type="hidden" name="test" value="1">
<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
<td width="1%"><img src="<%=request.getContextPath()%>/images/point.gif" width="20" height="19"></td>
<td width="99%" bgcolor="#CCCCD4" class="title-en"><region:render section='title'/></td>
</tr>
<tr>
<td  colspan="2" class="en11px-red" ><region:render section='error'/></td>
</tr>
<tr>
<td colspan="2" ><region:render section='content'/> </td>
</tr>
</table>
</body>
</html>


