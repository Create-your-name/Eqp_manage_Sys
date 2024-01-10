<%@ page contentType="text/html;charset=gb2312"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<%
    String startDate = request.getParameter("startDate");
	String endDate = request.getParameter("endDate");
	String equipmentType = request.getParameter("equipmentType");
	String keyEqp = request.getParameter("keyEqp");
	String adjustEqp = request.getParameter("adjustEqp");
	String measureEqp = request.getParameter("measureEqp");

    String eqpId = UtilFormatOut.checkNull((String) request.getAttribute("eqpId"));
    String dayCount = UtilFormatOut.checkNull((String) request.getAttribute("dayCount"));

	List pmFormList = (List) request.getAttribute("pmFormList");

%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>机台事件分析统计图-总图</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
            <td class="en11pxb" colspan="9">
                设备大类：<%=equipmentType%>，
                查询时间：<%=startDate%> ~ <%=endDate%>，
                共<%=dayCount%>天
            </td>
        </tr>

      	<tr bgcolor="#ACD5C9">
      		<td class="en11pxb">保养种类</td>
      		<td class="en11pxb">平均时数/天</td>
      		<td class="en11pxb">平均时数/次</td>
      		<td class="en11pxb">事件数</td>
    	</tr>

    	<%
    	double sumAvgDay = 0;
    	double sumAvgTime = 0;
    	int sumEventCount = 0;

    	if(pmFormList != null && pmFormList.size() > 0) {

    		for(Iterator it = pmFormList.iterator();it.hasNext();) {

				Map map = (Map) it.next();

				String periodIndex = UtilFormatOut.checkNull((String) map.get("PERIOD_INDEX"));
				String periodName = UtilFormatOut.checkNull((String) map.get("PERIOD_NAME"));
				String avgDay = UtilFormatOut.checkNull((String) map.get("AVGDAY"));
				String avgTime = UtilFormatOut.checkNull((String) map.get("AVGTIME"));
				String eventCount = UtilFormatOut.checkNull((String) map.get("EVENTCOUNT"));

    	%>
		        <tr bgcolor="#DFE1EC">
		        	<td class="en11px">
		        	    <a href="<ofbiz:url>/showPmEventEqpChart</ofbiz:url>?periodIndex=<%=periodIndex%>&periodName=<%=periodName%>">
		        	        <%=periodName%>
		        	    </a>
		        	</td>
		        	<td class="en11px"><%=avgDay%></td>
		        	<td class="en11px"><%=avgTime%></td>
		        	<td class="en11px"><%=eventCount%></td>
		        </tr>
		 <%
		 	    sumAvgDay = sumAvgDay + Double.parseDouble(avgDay);
		 	    sumAvgTime = sumAvgTime + Double.parseDouble(avgTime);
		 	    sumEventCount = sumEventCount + Integer.parseInt(eventCount);
		 	}
		 }

		 DecimalFormat df = new DecimalFormat("###.0");
		 %>

         <tr bgcolor="#DFE1EC">
             <td class="en11pxb">ALL</td>
        	 <td class="en11pxb"><%=df.format(sumAvgDay)%></td>
        	 <td class="en11pxb"><%=df.format(sumAvgTime)%></td>
        	 <td class="en11pxb"><%=sumEventCount%></td>
	      </tr>
      </table>
      </fieldset></td>
  </tr>
</table>


<FIELDSET><LEGEND>CHART INFO</LEGEND>
	<TABLE WIDTH="100%" HEIGHT="600" BORDER="0" CELLPADDING="0" CELLSPACING="0">
	  <TR>
	    <TD WIDTH="100%" HEIGHT="100%" align="center">
	    	<OBJECT classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" WIDTH="98%" HEIGHT="100%"
				ID="abcApplet" align="baseline"
    			codebase="http://<%=request.getServerName()%>:<%=request.getServerPort()%><%=request.getContextPath()%>/plugin/jinstall-1_4-windows-i586.cab#Version=1,4,0,0">
    			<PARAM NAME = CODEBASE VALUE = "<%=request.getContextPath()%>/pms/applet">
    			<PARAM NAME = ARCHIVE VALUE = "chart.jar,jcommon-1.0.10.jar,jfreechart-1.0.6.jar">
			 	<PARAM NAME = CODE VALUE = "com.csmc.pms.webapp.report.applet.PmEventChart.class">
			 	<param name=type value="application/x-java-applet;version=1.4">
				<param name="MAYSCRIPT" value="true">
			 	<param name="progressbar" value="true">

			 	<param name="chartName" value="PM_EVENT_CHART"/>
				<param name="startDate" value="<%=startDate%>"/>
				<param name="endDate" value="<%=endDate%>"/>
				<param name="equipmentType" value="<%=equipmentType%>"/>
				<param name="keyEqp" value="<%=keyEqp%>"/>
				<param name="adjustEqp" value="<%=adjustEqp%>"/>
				<param name="measureEqp" value="<%=measureEqp%>"/>

				<param name="eqpId" value="<%=eqpId%>"/>
				<param name="dayCount" value="<%=dayCount%>"/>

				<param name="AgentServletPath" value="<%=request.getContextPath()%>/control/getPmEventHist"/>

			</OBJECT>
	    </TD>
	  </TR>
	</TABLE>
</FIELDSET>
