<%@ include file="/includes/envsetup.jsp" %>
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
<script language='javascript' src='<%=request.getContextPath()%>/function/prototype.js' type='text/javascript'></script>

<link rel='stylesheet' href='<%=request.getContextPath()%>/images/maincss.css' type='text/css'>
<link rel='stylesheet' href='<%=request.getContextPath()%>/images/tabstyles.css' type='text/css'>

<link href="<%=request.getContextPath()%>/images/default.css" rel="stylesheet" type="text/css">
<script language="JavaScript" type="text/JavaScript">
	/*function ClickX(){
		window.event.returnValue=false;
	}
	document.oncontextmenu=ClickX;*/
</script>
</head>
<%
	String optFlag = (String)session.getAttribute("isOperatorConsole");
	if("true".equals(optFlag)){
		session.setAttribute("isOperatorConsole","false");
%>
<SCRIPT LANGUAGE="javascript">
try{
parent.document.frames("topFrame").document.location.href=parent.document.frames("topFrame").document.location.href;
}catch(e){

}
</script>
<%}
%>

<form method="POST" name="_main_template_frame" action='<%=request.getContextPath()%>/control/optConsoleEntry'>
</form>
<input type="hidden" name="test" value="1">
<body>
<!-----------add by laol-------------------->
<div id="loadBar" style="width:20px;height:12px;display:none;background-color:#ffd363;padding:6px;position:absolute;z-index:3;left:9px;top:9px;z-index:6;FONT-WEIGHT: bold; FONT-SIZE: 11px; MARGIN: 2px 6px; FONT-FAMILY: Tahoma">loading...</div>
<div id="dialogScreen" style="width:0px;height:16px;"></div>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
<td width="1%"><img src="<%=request.getContextPath()%>/images/point.gif" width="20" height="19"></td>
<td width="99%" bgcolor="#CCCCD4" class="title-en"><region:render section='title'/></td>
</tr>
<tr>
<td class="en11px-red">&nbsp;</td>
<td class="en11px-red"><region:render section='error'/></td>
</tr>
<tr>
<td >&nbsp;</td>
<td ><region:render section='content'/> </td>
</tr>
</table>
</body>
</html>


