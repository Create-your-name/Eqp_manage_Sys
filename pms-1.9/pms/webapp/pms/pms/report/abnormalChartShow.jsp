<%@ page contentType="text/html;charset=gb2312"%>
<%@ page import="java.util.*" %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%
    String startDate = request.getParameter("startDate");
	String endDate = request.getParameter("endDate");
	String equipmentType = request.getParameter("equipmentType");
    String equipmentDept = request.getParameter("equipmentDept");

	//List abnormalFormList = (List) request.getAttribute("abnormalFormList");

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
			 	<PARAM NAME = CODE VALUE = "com.csmc.pms.webapp.report.applet.AbnormalFormChart.class">
			 	<param name=type value="application/x-java-applet;version=1.4">
				<param name="MAYSCRIPT" value="true">
			 	<param name="progressbar" value="true">

			 	<param name="chartName" value="ABNORMAL_FORM_CHART"/>
				<param name="startDate" value="<%=startDate%>"/>
				<param name="endDate" value="<%=endDate%>"/>
				<param name="equipmentType" value="<%=equipmentType%>"/>
				<param name="equipmentDept" value="<%=equipmentDept%>"/>

				<param name="AgentServletPath" value="<%=request.getContextPath()%>/control/AbnormalChartShow"/>

			</OBJECT>
	    </TD>
	  </TR>
	</TABLE>
</FIELDSET>