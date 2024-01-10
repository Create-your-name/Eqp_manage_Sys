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
<br><div class='errorMessage'><%=UtilFormatOut.replaceString(errorMsgReq1, "\n", "<br>")%></div><br><%} if(errorMsgSes1 != null) {%>
<br><div class='errorMessage'><%=UtilFormatOut.replaceString(errorMsgSes1, "\n", "<br>")%></div><br><%} if(eventMsgReq1 != null) {%>
<br><div class='eventMessage'><%=UtilFormatOut.replaceString(eventMsgReq1, "\n", "<br>")%></div><br><%}%>
