<%@ page contentType="text/html;charset=gb2312"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>

<TABLE WIDTH="100%" BORDER="0" CELLPADDING="0" CELLSPACING="0">
  <TR>

    <TD BGCOLOR="#CCCCD4" CLASS="title-en" nowrap>&nbsp;
		部门: <%=request.getParameter("equipmentDept")%>; 设备大类: <%=request.getParameter("equipmentType")%>; PM周期: <%=request.getAttribute("periodName")%>; PM项目:<%=request.getAttribute("itemName")%>
	</TD>
  </TR>
</TABLE>


<FIELDSET><LEGEND>CHART INFO</LEGEND>
	<TABLE WIDTH="100%" HEIGHT="400" BORDER="0" CELLPADDING="0" CELLSPACING="0">
	  <TR>
	    <TD WIDTH="100%" HEIGHT="100%" align="center">
	    	<OBJECT classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" WIDTH="98%" HEIGHT="100%"
				ID="abcApplet" align="baseline"
    			codebase="http://<%=request.getServerName()%>:<%=request.getServerPort()%><%=request.getContextPath()%>/plugin/jinstall-1_4-windows-i586.cab#Version=1,4,0,0">
    			<param name="CODEBASE" value="<%=request.getContextPath()%>/pms/applet">
    			<param name="ARCHIVE" value= "PmsChartApplet.jar,AppletConfig.xml,AppletConfig.dtd">
			 	<param name="CODE" value= "com.mycim.spc.chart.PmParamAnalysisSpcChart.class">
			 	<param name=type value="application/x-java-applet;version=1.4">
				<param name="MAYSCRIPT" value="true">
			 	<param name="progressbar" value="true">

			 	<param name="chartName" value="PM_PARAM_ANALYSIS_CHART"/>
				<param name="startDate" value="<%=request.getParameter("startDate")%>"/>
				<param name="endDate" value="<%=request.getParameter("endDate")%>"/>
				<param name="equipmentType" value="<%=request.getParameter("equipmentType")%>"/>
				<param name="periodIndex" value="<%=request.getParameter("periodIndex")%>"/>
				<param name="itemIndex" value="<%=request.getParameter("itemIndex")%>"/>

				<param name="eqpId" value="<%=request.getAttribute("eqpId")%>"/>
				<param name="itemName" value="<%=request.getAttribute("itemName")%>"/>
				<param name="itemLowerSpec" value="<%=UtilFormatOut.checkNull((String) request.getAttribute("itemLowerSpec"))%>"/>
				<param name="itemUpperSpec" value="<%=UtilFormatOut.checkNull((String) request.getAttribute("itemUpperSpec"))%>"/>

				<param name="AgentServletPath" value="<%=request.getContextPath()%>/control/getPmParamItemPoints"/>

			</OBJECT>
	    </TD>
	  </TR>
	</TABLE>
</FIELDSET>