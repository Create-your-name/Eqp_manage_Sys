<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.pms.webapp.util.SessionNames" %>

<% List guiPriv = (List)request.getSession().getAttribute(SessionNames.GUI_PRIV_LIST_KEY); %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>PMS</title>

</head>
  <frameset rows="65,*" cols="*" framespacing="0" frameborder="NO" border="0">

  <frame src="<%=request.getContextPath()%>/includes/top.jsp" name="topFrame" scrolling="NO" noresize>

  <frameset name="mainFrameSet" cols="220,*" frameborder="NO" border="0" framespacing="3">

    <frameset  rows="*" cols="*,14" frameborder="NO" border="0" framespacing="0"> 
			<frame src="<%=request.getContextPath()%>/includes/left.jsp" name="leftFrame" scrolling="NO">
  	 	<frame src="/pms/includes/hide.htm" name="x_hidden" noresize scrolling="NO" >
    </frameset>
    <frameset rows="*" cols="*,1"  frameborder="NO" border="0" framespacing="0">
    <% if(guiPriv.contains("PMS_DEFAULT")) { %>
		<frame src="<ofbiz:url>/pmsDefault</ofbiz:url>" name="mainFrame">
		<% } else { %>
		<frame src="/pms/content.jsp" name="mainFrame">
		<% } %>
	</frameset>	
  </frameset>

</frameset>
<noframes><body>

</body></noframes>

</html>
