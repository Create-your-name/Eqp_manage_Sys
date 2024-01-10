<%@ page contentType="text/html;charset=gb2312"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<%
    String startDate = request.getParameter("startDate");
	String endDate = request.getParameter("endDate");
	String equipmentType = request.getParameter("equipmentType");
	String periodIndex = request.getParameter("periodIndex");
    String equipmentDept = request.getParameter("equipmentDept");

    String eqpId = (String) request.getAttribute("eqpId");
    //String periodName = (String) request.getAttribute("periodName");
    //String standardHour = (String) request.getAttribute("standardHour");
    //String eqpStatus = (String) request.getAttribute("eqpStatus");
    //String warningDays = (String) request.getAttribute("warningDays");
    //String defaultDays = (String) request.getAttribute("defaultDays");

	List pmFormList = (List) request.getAttribute("pmFormList");
	double overtimes = 0 ;
	java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");  
	String overtime_rate = "";
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>设备保养用时查询</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        

      	<tr bgcolor="#ACD5C9">
      		<td class="en11pxb">EqpId</td>
      		<td class="en11pxb">保养种类</td>
      		<td class="en11pxb">标准状态码</td>
      		<td class="en11pxb">警示天数</td>
      		<td class="en11pxb">pm频次</td>
      		<td class="en11pxb">Target(小时)</td>
      		<td class="en11pxb">PM Time(小时)</td>
      		<td class="en11pxb">超时</td>
      		<td class="en11pxb">开始人</td>
      		<td class="en11pxb">开始日期</td>
      		<td class="en11pxb">完成日期</td>
    	</tr>
    	<%
    	if(pmFormList != null && pmFormList.size() > 0) {

    		for(Iterator it = pmFormList.iterator();it.hasNext();) {

				Map map = (Map) it.next();
				double pm_time = Double.parseDouble((String) map.get("PM_TIME"));
				double stand_hour = Double.parseDouble((String) map.get("STANDARD_HOUR"));
				double diff = (pm_time - stand_hour)/stand_hour;
				String overtime = "";
				if(diff>0.2){
				    overtime = df.format(diff*100).toString() + "%";
				    overtimes++;
				}
				
    	%>
    	
		        <tr bgcolor="#DFE1EC">
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("EQUIPMENT_ID"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("PERIOD_NAME"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("EQP_STATUS"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("WARNING_DAYS"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("DEFAULT_DAYS"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("STANDARD_HOUR"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("PM_TIME"))%></td>
		        	<td class="en11px"><%=overtime%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("START_USER"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("START_TIME"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("END_TIME"))%></td>
		        </tr>
		        
		 <%
		 	}
		 }
    	    if(pmFormList.size()>0){
    	        
    	        overtime_rate = df.format(overtimes/pmFormList.size()*100).toString()+"%";
    	    }
		 %>
		 <tr bgcolor="#ACD5C9">
         <td class="en11pxb" colspan="14">
                                 超时率：<%=overtime_rate%>
         </td>
         </tr>
      </table>
      </fieldset></td>
  </tr>
</table>

