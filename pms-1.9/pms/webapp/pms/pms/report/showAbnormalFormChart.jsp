<%@ page contentType="text/html;charset=gb2312"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<%
    String startDate = request.getParameter("startDate");
	String endDate = request.getParameter("endDate");
	String equipmentType = request.getParameter("equipmentType");
    String equipmentDept = request.getParameter("equipmentDept");

    String eqpId = (String) request.getAttribute("eqpId");

	List abnormalFormList = (List) request.getAttribute("abnormalFormList");

%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�豸�쳣��ʱ��ѯ</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
            <td class="en11pxb" colspan="11">
                ��ѯ��ʼ���ڣ�<%=startDate%>
                ����<%=endDate%>��
                �豸���ࣺ<%=equipmentType%>��
                ���ţ�<%=equipmentDept%>
            </td>
        </tr>

      	<tr bgcolor="#ACD5C9">
      	    <td class="en11pxb">�쳣��¼���</td>
      		<td class="en11pxb">EqpId</td>
      		<td class="en11pxb">��������</td>
      		<td class="en11pxb">�쳣ԭ��</td>
      		<td class="en11pxb">��ʼ����</td>
      		<td class="en11pxb">��������</td>
      		<td class="en11pxb">Down Time(h)</td>
      		<td class="en11pxb">�������</td>
      		<td class="en11pxb">������Ա</td>
      		<td class="en11pxb">��ʼ��</td>
      		<td class="en11pxb">������</td>
    	</tr>

    	<%
    	if(abnormalFormList != null && abnormalFormList.size() > 0) {

    		for(Iterator it = abnormalFormList.iterator();it.hasNext();) {

				Map map = (Map) it.next();

    	%>
		        <tr bgcolor="#DFE1EC">
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("ABNORMAL_NAME"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("EQUIPMENT_ID"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("ABNORMAL_TEXT"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("ABNORMAL_REASON"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("START_TIME"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("END_TIME"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("DOWN_TIME"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("JOB_TEXT"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("CREATE_USER"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("START_USER"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("END_USER"))%></td>
		        </tr>
		 <%
		 	}
		 }
		 %>

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
			 	<PARAM NAME = CODE VALUE = "com.csmc.pms.webapp.report.applet.AbnormalFormChart.class">
			 	<param name=type value="application/x-java-applet;version=1.4">
				<param name="MAYSCRIPT" value="true">
			 	<param name="progressbar" value="true">

			 	<param name="chartName" value="ABNORMAL_FORM_CHART"/>
				<param name="startDate" value="<%=startDate%>"/>
				<param name="endDate" value="<%=endDate%>"/>
				<param name="equipmentType" value="<%=equipmentType%>"/>
				<param name="equipmentDept" value="<%=equipmentDept%>"/>

				<param name="eqpId" value="<%=eqpId%>"/>

				<param name="AgentServletPath" value="<%=request.getContextPath()%>/control/getAbnormalFormItemPoints"/>

			</OBJECT>
	    </TD>
	  </TR>
	</TABLE>
</FIELDSET>
