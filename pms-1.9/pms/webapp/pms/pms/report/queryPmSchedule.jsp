<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
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
    document.equipmentScheduleForm.all.item("form_year").value=year;
    document.equipmentScheduleForm.all.item("form_month").value=month;
    document.equipmentScheduleForm.all.item("form_day").value=day;

 	document.equipmentScheduleForm.all.item("description").value=description;

	document.equipmentScheduleForm.all.item("pm_creator").innerText=creator;

    pmtype_set(document.equipmentScheduleForm.all.item("periodIndex"),isFinished);

    selectday.innerText+=periodStr;

    return false;
}

function pmtype_set(oSelect,isFinished)
{
	var bvalue= false;
	if(isFinished=='true'){
		bvalue = true;
	}else{
		bvalue = false;
	}


    document.equipmentScheduleForm.all.item("reset").disabled =((oSelect.selectedIndex==0) || bvalue);
    document.equipmentScheduleForm.all.item("set").disabled=((oSelect.selectedIndex==0) || bvalue);
    document.equipmentScheduleForm.all.item("form_pmsetup").disabled=((oSelect.selectedIndex==0) || bvalue) ;
}

function pmtype_change(obj)
{
    document.equipmentScheduleForm.action = "<%=request.getContextPath()%>/control/queryPmSchedule?periodIndex="+obj.value;
    document.equipmentScheduleForm.submit();
}

function yearSelect()
{
	document.equipmentScheduleForm.action = "<%=request.getContextPath()%>/control/queryPmSchedule";
    document.equipmentScheduleForm.submit();
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
    String maintDept = UtilFormatOut.checkNull((String) request.getAttribute("maintDept"));
%>
<script language="javascript">

	Ext.onReady(function(){

	    var dept = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'maintDept',
	        width:170,
	        forceSelection:true
	    });
	    dept.on('select',loadEqpId);

	    var eqpIdDS = new Ext.data.Store({
	        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentByAnd</ofbiz:url>'}),
		    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
	    });

	    //����eqpId
		var	eqpIdCom = new Ext.form.ComboBox({
		    store: eqpIdDS,
		    displayField:'equipmentId',
		    valueField:'equipmentId',
		    hiddenName:'equipmentId',
		    typeAhead: true,
		    mode: 'local',
		    width: 170,
		    triggerAction: 'all'
	    });
	    eqpIdCom.applyTo('eqpIdSelect');

	    function loadEqpId() {
    		var val = dept.getValue();
    		if (val == '<%=maintDept%>') {
    		    var eqpId  = '<%=UtilFormatOut.checkNull(request.getParameter("equipmentId"))%>';
    		} else {
    		    var eqpId  = '';
    		}

    		eqpIdDS.load({params:{maintDept:val},callback:function(){ eqpIdCom.setValue(eqpId); }});
    	};

        // initial dept & epqid
    	dept.setValue("<%=maintDept%>");
    	loadEqpId();
	});

	function equipmentScheduleDefine() {
		if(Ext.getDom('equipmentId').value=='') {
			Ext.MessageBox.alert('����', '��ѡ���豸��');
			return;
		}
		loading();
		document.equipmentScheduleForm.submit();
	}

</script>

<%
	String[] daystitle = (String[])request.getAttribute("daystitle");
	int column;
	int row;

    String periodIndex = (String)request.getAttribute("periodIndex");

	Map colorMap = (Map)request.getAttribute("colorMap");
%>



<form name="equipmentScheduleForm" id="equipmentScheduleForm">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>��̨PM���ڲ�ѯ</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC" height="30">
            <td width="10%" class="en11pxb">&nbsp;��&nbsp;��&nbsp;</td>
            <td>
         		<select id="maintDept" name="maintDept">
         			<option value=''></option>
	          		<ofbiz:if name="deptList">
		        		<ofbiz:iterator name="cust" property="deptList">
			    			<option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>'>
			    			    <ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>
			    			</option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
    			</select>
            </td>
        </tr>

        <tr bgcolor="#DFE1EC" height="30">
            <td width="10%" class="en11pxb">&nbsp;��&nbsp;��&nbsp;</td>
            <td><input type="text" size="40" name="eqpIdSelect" autocomplete="off"/></td>
        </tr>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
	<tr height="30">
	 	<td><ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:equipmentScheduleDefine();"><span>&nbsp;ȷ��&nbsp;</span></a></li>
			</ul>
		</td>
	</tr>
</table>

<%
	if (colorMap != null)
	{
%>

   <table>
   		<tr>
   			<td><h2 class="title" color="<%=colorMap.get("fontColor") %>">EQ ID: <ofbiz:field attribute="equipmentId"/></h2></td>
   			<td><input name="setup_year" type="text" style="font-size=16" size=3 value=<ofbiz:field attribute="thisYear"/>></td>
   			<td><h2 class="title" color="<%=colorMap.get("fontColor") %>">���  ����������</h2></td>
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
    <input type=hidden name="equipmentId" value=<ofbiz:field attribute="equipmentId"/>>
    <input type=hidden name="form_year">
    <input type=hidden name="form_month">
    <input type=hidden name="form_day">
    <input type=hidden name="flag">
    <table border="1" cellpadding="2" bgcolor=<%=colorMap.get("background") %> bordercolor=<%=colorMap.get("borderColor") %>>
    	<tr><td width=30%>PM����:&nbsp&nbsp&nbsp&nbsp
    		<select  name="periodIndex" onchange="pmtype_change(this)">
    			<option value="">ȫ��</option>
 	          		<ofbiz:if name="equipmentPeriodList">
		        		<ofbiz:iterator name="defaultPeriod" property="equipmentPeriodList">
			    			<option value='<ofbiz:inputvalue entityAttr="defaultPeriod" field="periodIndex"/>'>
			    				<ofbiz:inputvalue entityAttr="defaultPeriod" field="periodName"/>
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
    				<input type="checkbox" name="form_pmsetup">
    				</font>
    				<input name="set" type="button" value="�趨"  onclick="daySet();">&nbsp&nbsp
    				<input name="reset" type="button" value="���"  onclick="dayClear();">
    			</center>
    		</td>
    	</tr>
    <%
	//    if('thisDayPM'=='Y')
    if ('Y'=='N')
    {
    %>
    	<tr><td width=30%>�ձ�������:&nbsp&nbsp&nbsp&nbsp<select name="dayPmPeriod"></select></td>
    	</tr>
    <%
    }
    else
    {
    %>
    <tr><td width=30%>�ձ�������:&nbsp&nbsp&nbsp&nbsp<select name="dayPmPeriod" disabled></select></td>
    	</tr>
    <%
    }
    %>
		 <tr><td>״̬:&nbsp&nbsp<font color='red'></font></td>

    	<td colspan='2'><table cellpadding="0" bgcolor=<%=colorMap.get("background") %>><tr>
    	<td width=7% bgcolor=<%=colorMap.get("maintenancecolor1") %>></td><td>&nbsp���ɸ���&nbsp</td>
    	<td width=7% bgcolor=<%=colorMap.get("maintenancecolor2") %>></td><td>&nbsp�ɸ���PM&nbsp</td>
    	<td width=7% bgcolor=<%=colorMap.get("maintenancecolor3") %>></td><td>&nbsp�ɸ����ص�PM&nbsp</td>
    	<td width=7% bgcolor="#C1121C"></td><td>&nbsp������PM&nbsp</td>
    	</tr>
    </table>
    



	<script language="javascript">
	var periodIndex;
	periodIndex = '<%=periodIndex%>'
	for(i=0;i<document.equipmentScheduleForm.periodIndex.options.length;i++)
	{
		if (document.equipmentScheduleForm.periodIndex.options[i].value==periodIndex)
		{
			document.equipmentScheduleForm.periodIndex.options[i].selected=true;
		}
	}
	    pmtype_set(document.equipmentScheduleForm.all.item("periodIndex"),false);
	</script>
	
	<%
	}
%>
</form>