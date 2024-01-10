<%@ page import="org.ofbiz.base.util.*" %>
<link href="<%=request.getContextPath()%>/images/default.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath()%>/images/csmc.css" rel="stylesheet" type="text/css">
<%
String eventMsgReq1 = (String)request.getAttribute("eventMsgReq");
if(eventMsgReq1!=null&&eventMsgReq1.toUpperCase().indexOf("TIMEOUT")>0){
eventMsgReq1 = eventMsgReq1 + "\n 请确认操作已成功！";
}
if(eventMsgReq1!=null && eventMsgReq1.indexOf("Exception:")>0){
eventMsgReq1 = eventMsgReq1.substring(eventMsgReq1.lastIndexOf(":")+1);
}

String errorMsgReq1 = (String)request.getAttribute("errorMsgReq");
if(errorMsgReq1!=null&&errorMsgReq1.toUpperCase().indexOf("TIMEOUT")>0){
errorMsgReq1 =  errorMsgReq1 + "\n 请确认操作已成功！";
}
if(errorMsgReq1!=null && errorMsgReq1.indexOf("Exception:")>0){
errorMsgReq1 = errorMsgReq1.substring(errorMsgReq1.lastIndexOf(":")+1);
}


String errorMsgSes1 = (String)session.getAttribute("errorMsgSes");
if(errorMsgSes1!=null && errorMsgSes1.toUpperCase().indexOf("TIMEOUT")>0){
errorMsgSes1 =errorMsgSes1 + "\n 请确认操作已成功！";
}
if(errorMsgSes1!=null && errorMsgSes1.indexOf("Exception:")>0){
errorMsgSes1 = errorMsgSes1.substring(errorMsgSes1.lastIndexOf(":")+1);
}


if(errorMsgSes1 != null) session.removeAttribute("errorMsgSes");
if(errorMsgReq1 != null){ %>
<!--error message-->
<br><table cellpadding='5' width='100%' cellspacing='5px' class='errorMessage' border="0">
<colgroup><col width='15'><col></colgroup>
<tr><td valign='top'>
		<img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" align="absmiddle" alt="" border="0">
	</td>
<td class="en11px"><%=UtilFormatOut.replaceString(errorMsgReq1, "\n", "<br>")%></td></tr></table><br><%} if(errorMsgSes1 != null) {%>
<!--error message-->
<br><table cellpadding='5' width='100%' cellspacing='5px' class='errorMessage' border="0">
<colgroup><col width='15'><col></colgroup>
<tr><td valign='top'>
		<img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" align="absmiddle" alt="" border="0">
	</td>
<td class="en11px"><%=UtilFormatOut.replaceString(errorMsgSes1, "\n", "<br>")%></td></tr></table><br><%} if(eventMsgReq1 != null) {%>
<!--success message-->
<br><table cellpadding='5' width='100%' cellspacing='5px' class='successMessage' border="0">
<colgroup><col width='15'><col></colgroup>
<tr><td valign='top'>
		<img src="<%=request.getContextPath()%>/pms/images/success.gif" width="16" height="16" align="absmiddle" alt="" border="0">
	</td>
<td class="en11px"><%=UtilFormatOut.replaceString(eventMsgReq1, "\n", "<br>")%></td></tr></table><%}%>

<!--//////////////////////////////////////////////////////////////////////////////-->
<div id='Eqp_Status_Msg_Success_Div' style='display:none'>
<!--success message-->
<br><table cellpadding='5' width='100%' cellspacing='5px' class='successMessage' border="0">
<colgroup><col width='15'><col></colgroup>
<tr><td valign='top'>
		<img src="<%=request.getContextPath()%>/pms/images/success.gif" width="16" height="16" align="absmiddle" alt="" border="0">
	</td>
<td class="en11px"><span id='Eqp_Status_Msg_Success_Span'></span></td></tr></table>
</div>
	
<div id='Eqp_Status_Msg_Error_Div' style='display:none'>
<!--ERROR message-->
<br><table cellpadding='5' width='100%' cellspacing='5px' class='errorMessage' border="0">
<colgroup><col width='15'><col></colgroup>
<tr><td valign='top'>
		<img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" align="absmiddle" alt="" border="0">
	</td>
<td class="en11px"><span id='Eqp_Status_Msg_Error_Span'></span></td></tr></table>
</div>