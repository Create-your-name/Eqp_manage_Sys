<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*"%>
<%@ page import="java.lang.*"%>
<style type="text/css">
<!--
font.title {
	font-size: 14px;
}
font.standard {
      font-size: 12px;
}
th {
	font-size: 12px;
}
td {
	font-size: 11px;
}
td.k{
      	cursor : hand;
}
a:link {
      color: #4040FF;
}
a:visited {
      color: #4040FF;
}
a:active {
      color: #4040FF;
}
h2 {
      font-size: 24px;
}
input {
      font-size: 12px;
}
select {
      font-size: 12px;
}
-->
</style>

<script language="javascript">
function daytitle_onclick(oCell,year,month,day,creator,description,periodStr,isFinished)
{
    selectday.innerText=year+"��"+month+"��"+day+"��";
    document.pcScheduleForm.all.item("form_year").value=year;
    document.pcScheduleForm.all.item("form_month").value=month;
    document.pcScheduleForm.all.item("form_day").value=day;

 	document.pcScheduleForm.all.item("description").value=description;

	document.pcScheduleForm.all.item("pm_creator").innerText=creator;

    pcStyle_set(document.pcScheduleForm.all.item("periodIndex"),isFinished);

    selectday.innerText+=periodStr;

    return false;
}

function pcStyle_set(oSelect,isFinished)
{
	var bvalue= false;
	if(isFinished=='true'){
		bvalue = true;
	}else{
		bvalue = false;
	}


    document.pcScheduleForm.all.item("reset").disabled =((oSelect.selectedIndex==0) || bvalue);
    document.pcScheduleForm.all.item("set").disabled=((oSelect.selectedIndex==0) || bvalue);
    document.pcScheduleForm.all.item("form_pcsetup").disabled=((oSelect.selectedIndex==0) || bvalue) ;
}

function pcStyle_change(obj)
{
    document.pcScheduleForm.action = "<%=request.getContextPath()%>/control/queryPcSchedule?periodIndex="+obj.value;
    document.pcScheduleForm.submit();
}

function yearSelect()
{
	document.pcScheduleForm.action = "<%=request.getContextPath()%>/control/queryPcSchedule";
    document.pcScheduleForm.submit();
}

function daySet()
{
	alert("����� �����趨 �˵���������");
}

function dayClear()
{
	alert("����� �����趨 �˵���������");
}
</script>

<%
	String[] daystitle = (String[])request.getAttribute("daystitle");
	int column;
	int row;

    String periodIndex = (String)request.getAttribute("periodIndex");

	Map colorMap = (Map)request.getAttribute("colorMap");
%>
<form name="pcScheduleForm" id="pcScheduleForm">
   <table>
   		<tr>
   			<td><h2 class="title" color="<%=colorMap.get("fontColor") %>">PC Style: <ofbiz:field attribute="styleName"/></h2></td>
   			<td><input name="setup_year" type="text" style="font-size=16" size=3 value=<ofbiz:field attribute="thisYear"/>></td>
   			<td><h2 class="title" color="<%=colorMap.get("fontColor") %>">���  Ѳ��������</h2></td>
   			<td><input type="button" value="����" onclick = "return yearSelect();"></td>
    	</tr>
    </table>

   <table border="2" width="80%" cellpadding="0" bgcolor=<%=colorMap.get("background")%> bordercolor=<%=colorMap.get("borderColor")%>>
   		<%
   			int i = 1;
   			int j;
   			List scheduleList = (List)request.getAttribute("scheduleList");
   			//Iterator scheduleIter = scheduleList.iterator();
   			for (int k=0;k<scheduleList.size();k++)
   			{
   			 	Map map = (Map)scheduleList.get(k);
   				Calendar firstDay = (Calendar)(map.get("firstDay"));
   				int startDay = ((Integer)(map.get("startDay"))).intValue();
   				int month = ((Integer)(map.get("month"))).intValue();
   				int ndays = ((Integer)(map.get("ndays"))).intValue();
   				String title = (String)map.get("title");
   				String description = (String)map.get("description");

   				Map timeMap = (HashMap)(map.get("timeMap"));
   				Map periodMap = (HashMap)(map.get("periodMap"));
   				Map creatorMap = (HashMap)(map.get("creatorMap"));
				Map isFinishedMap = (HashMap)(map.get("isFinishedMap"));

      				//out.println("num---------->"+num);
   				if (i == 1)
   				{
   					out.println("<tr>");
   				}
   				else if ((i==5) || (i==9))
   				{
   					out.println("</tr><tr>");
   				}

   		%>
		<td width="25%"><%@ include file="monthformat.jsp"%></td>
    	<%
    			i++;
    		}
    	%>
		</tr>
    </table>
    <input type=hidden name="styleIndex" value=<ofbiz:field attribute="styleIndex"/>>
    <input type=hidden name="form_year">
    <input type=hidden name="form_month">
    <input type=hidden name="form_day">
    <input type=hidden name="flag">
    <table border="1" cellpadding="2" bgcolor=<%=colorMap.get("background") %> bordercolor=<%=colorMap.get("borderColor") %>>
    	<tr><td width=30%>PM����:&nbsp&nbsp&nbsp&nbsp
    		<select  name="periodIndex" onchange="pcStyle_change(this)">
    			<option value="">ȫ��</option>
 	          		<ofbiz:if name="pcPeriodList">
		        		<ofbiz:iterator name="pcPeriod" property="pcPeriodList">
			    			<option value='<ofbiz:inputvalue entityAttr="pcPeriod" field="periodIndex"/>'>
			    				<ofbiz:inputvalue entityAttr="pcPeriod" field="periodName"/>
			    			</option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
    		</select>
    		</td>
    		<td width=40%><font color=<%=colorMap.get("fontColor") %>>����:&nbsp&nbsp&nbsp&nbsp</font><input type="text" size=25 name="description"></td>
    		<td width=30%><font color=<%=colorMap.get("fontColor") %>>������:&nbsp&nbsp&nbsp&nbsp</font><span id='pm_creator'></span></td>
    	</tr>
    	<tr><td width=30% colspan=2>����:&nbsp&nbsp&nbsp&nbsp<span id=selectday></span></td>
    		<td width=30%>
    			<center>
    				<font color=<%=colorMap.get("fontColor") %>>�����趨:&nbsp&nbsp
    				<input type="checkbox" name="form_pcsetup">
    				</font>
    				<input name="set" type="button" value="�趨"  onclick="daySet();">&nbsp&nbsp
    				<input name="reset" type="button" value="���"  onclick="dayClear();">
    			</center>
    		</td>
    	</tr>
		<tr><td>״̬:&nbsp&nbsp<font color='red'></font></td>
    	<td colspan='2'><table cellpadding="0" bgcolor=<%=colorMap.get("background") %>><tr>
    	<td width=7% bgcolor=<%=colorMap.get("maintenancecolor1") %>></td><td>&nbsp���ɸ���&nbsp</td>
    	<td width=7% bgcolor=<%=colorMap.get("maintenancecolor2") %>></td><td>&nbsp�ɸ���PM&nbsp</td>
    	<td width=7% bgcolor=<%=colorMap.get("maintenancecolor3") %>></td><td>&nbsp�ɸ����ص�PM&nbsp</td>
    	</tr>
    </table>
	<script language="javascript">
	var periodIndex;
	periodIndex = '<%=periodIndex%>'
	for(i=0;i<document.pcScheduleForm.periodIndex.options.length;i++)
	{
		if (document.pcScheduleForm.periodIndex.options[i].value==periodIndex)
		{
			document.pcScheduleForm.periodIndex.options[i].selected=true;
		}
	}
	    pcStyle_set(document.pcScheduleForm.all.item("periodIndex"),false);
	</script>
</form>