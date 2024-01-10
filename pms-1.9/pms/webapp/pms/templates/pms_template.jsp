<%@ include file="/includes/envsetup.jsp" %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='regions' prefix='region' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@page contentType='text/html; charset=gb2312'%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title><region:render section='title'/></title>
<script language='javascript' src='<%=request.getContextPath()%>/function/function.js' type='text/javascript'></script>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/images/default.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/pms/css/pms.css" />

<!--include yui css-->
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/pms/yui-ext/resources/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/pms/css/dialog.css" />

<!--include yui script-->
<script type="text/javascript" src="<%=request.getContextPath()%>/pms/yui-ext/adapter/yui/yui-utilities.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/pms/yui-ext/adapter/yui/ext-yui-adapter.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/pms/yui-ext/ext-all-debug.js"></script>

<!--include bubble css-->
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/pms/script/bubble/bt.css" />

<!--include bubble script-->
<script type="text/javascript" src="<%=request.getContextPath()%>/pms/script/bubble/BubbleTooltips.js"></script>
<script language="javascript" type="text/JavaScript">
	window.onload = function(){enableTooltips()};
</script>

<script language="JavaScript" type="text/JavaScript">
	/*function ClickX(){
		window.event.returnValue=false;
	}
	document.oncontextmenu=ClickX;*/
</script>
</head>
<body>

<div id="loadBar" style="width:20px;height:12px;display:none;background-color:#ffd363;padding:6px;position:absolute;z-index:9001;left:9px;top:9px;z-index:9002;FONT-WEIGHT: bold; FONT-SIZE: 11px; MARGIN: 2px 6px; FONT-FAMILY: Tahoma">loading...</div>
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


