<%@ page contentType="text/html;charset=gb2312"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>

<%
	String strEqpId = "";
	try{
		String[] arrEqpId = request.getParameterValues("eqpIdSelected");

		if(arrEqpId != null) {
			for(int i=0;i<arrEqpId.length;i++) {
				if ("".equals(strEqpId)) {
					strEqpId = "'" + arrEqpId[i] + "'";
				} else {
					strEqpId = strEqpId + ",'" + arrEqpId[i] + "'";
				}
			}
		}
	} catch(Exception   e) {
		out.print(e.toString());
	}

%>

<FIELDSET><LEGEND>CHART INFO</LEGEND>
	<TABLE WIDTH="100%" HEIGHT="600" BORDER="0" CELLPADDING="0" CELLSPACING="0">
	  <TR>
	    <TD WIDTH="100%" HEIGHT="100%" align="center">
	    	<OBJECT classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" WIDTH="98%" HEIGHT="100%"
				ID="abcApplet" align="baseline"
    			codebase="http://<%=request.getServerName()%>:<%=request.getServerPort()%><%=request.getContextPath()%>/plugin/jinstall-1_4-windows-i586.cab#Version=1,4,0,0">
    			<PARAM NAME = CODEBASE VALUE = "<%=request.getContextPath()%>/pms/applet">
    			<PARAM NAME = ARCHIVE VALUE = "chart.jar,jcommon-1.0.10.jar,jfreechart-1.0.6.jar">
			 	<PARAM NAME = CODE VALUE = "com.csmc.pms.webapp.report.applet.EqpAvailUpTimeChart.class">
			 	<param name=type value="application/x-java-applet;version=1.4">
				<PARAM NAME="MAYSCRIPT" VALUE="true">
			 	<PARAM name="progressbar" value="true">

				<param name="Period" value="<%=request.getParameter("period")%>"/>
				<param name="StartDate" value="<%=request.getParameter("startDate")%>"/>
				<param name="EndDate" value="<%=request.getParameter("endDate")%>"/>

				<param name="ShowAvailableTime" value="<%=UtilFormatOut.checkNull(request.getParameter("showAvailableTime"))%>"/>
				<param name="ShowUpTime" value="<%=UtilFormatOut.checkNull(request.getParameter("showUpTime"))%>"/>
				<param name="ReferLine" value="<%=UtilFormatOut.checkNull(request.getParameter("referLine"))%>"/>

				<param name="EqpId" value="<%=strEqpId%>"/>
				<param name="AgentServletPath" value="<%=request.getContextPath()%>/control/getEqpAvailUpTime"/>
			</OBJECT>
	    </TD>
	  </TR>
	</TABLE>
</FIELDSET>


