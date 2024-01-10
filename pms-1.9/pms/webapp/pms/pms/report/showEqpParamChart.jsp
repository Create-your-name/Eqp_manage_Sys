<%@ page contentType="text/html;charset=gb2312"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>


<FIELDSET><LEGEND>CHART INFO</LEGEND>
	<TABLE WIDTH="100%" HEIGHT="600" BORDER="0" CELLPADDING="0" CELLSPACING="0">
	  <TR>
	    <TD WIDTH="100%" HEIGHT="100%" align="center">
	    	<OBJECT classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" WIDTH="98%" HEIGHT="100%"
				ID="abcApplet" align="baseline"
    			codebase="http://<%=request.getServerName()%>:<%=request.getServerPort()%><%=request.getContextPath()%>/plugin/jinstall-1_4-windows-i586.cab#Version=1,4,0,0">
    			<PARAM NAME = CODEBASE VALUE = "<%=request.getContextPath()%>/pms/applet">
    			<PARAM NAME = ARCHIVE VALUE = "chart.jar,jcommon-1.0.10.jar,jfreechart-1.0.6.jar">
			 	<PARAM NAME = CODE VALUE = "com.csmc.pms.webapp.report.applet.EqpParamChart.class">
			 	<param name=type value="application/x-java-applet;version=1.4">
				<param name="MAYSCRIPT" value="true">
			 	<param name="progressbar" value="true">

			 	<param name="chartName" value="EQP_PARAM_CHART"/>
				<param name="startDate" value="<%=request.getParameter("startDate")%>"/>
				<param name="endDate" value="<%=request.getParameter("endDate")%>"/>
				<param name="equipmentType" value="<%=request.getParameter("equipmentType")%>"/>
				<param name="eqpId" value="<%=UtilFormatOut.checkNull(request.getParameter("eqpId"))%>"/>
				<param name="paramName" value="<%=UtilFormatOut.checkNull(request.getParameter("paramName"))%>"/>

				<param name="AgentServletPath" value="<%=request.getContextPath()%>/control/getEqpParamPoints"/>

			</OBJECT>
	    </TD>
	  </TR>
	</TABLE>
</FIELDSET>