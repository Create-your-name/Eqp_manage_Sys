<%@ page contentType="text/html;charset=gb2312"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<%
    List pmFormList = (List) request.getAttribute("pmFormList");

    Map paramMap = (Map) session.getAttribute("paramMap");

    String startDate = (String) paramMap.get("startDate");
	String endDate = (String) paramMap.get("endDate");
	String equipmentType = (String) paramMap.get("equipmentType");

    String dayCount = (String) paramMap.get("dayCount");

    String periodName = (String) paramMap.get("periodName");

%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>��̨�¼�����ͳ��ͼ - <%=periodName%></legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
            <td class="en11pxb" colspan="9">
                �豸���ࣺ<%=equipmentType%>��
                ��ѯʱ�䣺<%=startDate%> ~ <%=endDate%>��
                ��<%=dayCount%>��
            </td>
        </tr>

      	<tr bgcolor="#ACD5C9">
      		<td class="en11pxb">EqpId</td>
      		<td class="en11pxb">��ʼʱ��</td>
      		<td class="en11pxb">����ʱ��</td>
      		<td class="en11pxb">��׼��ʱ</td>
      		<td class="en11pxb">ʵ�ʹ�ʱ</td>
    	</tr>

    	<%

    	if(pmFormList != null && pmFormList.size() > 0) {

    		for(Iterator it = pmFormList.iterator();it.hasNext();) {

				Map map = (Map) it.next();

				String equipmentId = UtilFormatOut.checkNull((String) map.get("EQUIPMENT_ID"));
				String startTime = UtilFormatOut.checkNull((String) map.get("START_TIME"));
				String endTime = UtilFormatOut.checkNull((String) map.get("END_TIME"));
				String standardHour = UtilFormatOut.checkNull((String) map.get("STANDARD_HOUR"));
				String actualtime = UtilFormatOut.checkNull((String) map.get("ACTUALTIME"));

    	%>
		        <tr bgcolor="#DFE1EC">
		        	<td class="en11px"><%=equipmentId%></td>
		        	<td class="en11px"><%=startTime%></td>
		        	<td class="en11px"><%=endTime%></td>
		        	<td class="en11px"><%=standardHour%></td>
		        	<td class="en11px"><%=actualtime%></td>
		        </tr>
		 <%
		    }
		 }
		 %>

      </table>
      </fieldset></td>
  </tr>
</table>