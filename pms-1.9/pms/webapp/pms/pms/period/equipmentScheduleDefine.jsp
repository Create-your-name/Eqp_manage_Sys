<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*"%>
<%@ page import="java.lang.*"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="com.csmc.pms.webapp.util.*"%>

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
    function daytitle_onclick(oCell,year,month,day,creator,description,periodStr,isFinished,timeRangeIndex)
    {
        selectday.innerText=year+"年"+month+"月"+day+"日";
        document.equipmentScheduleForm.all.item("form_year").value=year;
        document.equipmentScheduleForm.all.item("form_month").value=month;
        document.equipmentScheduleForm.all.item("form_day").value=day;

     	document.equipmentScheduleForm.all.item("description").value=description;

    	document.equipmentScheduleForm.all.item("pm_creator").innerText=creator;

        pmtype_set(document.equipmentScheduleForm.all.item("periodIndex"),isFinished);

        selectday.innerText+=periodStr;

        if(timeRangeIndex != "") {
        	Ext.getDom('timeRangeIndex').value = timeRangeIndex;
    	}

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
    	loading();
        document.equipmentScheduleForm.action = "<%=request.getContextPath()%>/control/equipmentScheduleDefine?periodIndex="+obj.value;
        document.equipmentScheduleForm.submit();
    }

    function yearSelect()
    {
    	loading();
    	document.equipmentScheduleForm.action = "<%=request.getContextPath()%>/control/equipmentScheduleDefine";
        document.equipmentScheduleForm.submit();
    }

    function daySet()
    {
    	loading();
    	if (document.equipmentScheduleForm.form_pmsetup.checked)
    	{
    		document.equipmentScheduleForm.flag.value="1";
    	}

    	document.equipmentScheduleForm.action = "<%=request.getContextPath()%>/control/equipmentScheduleAdd";
    	document.equipmentScheduleForm.submit();
    }

    function dayClear()
    {
    	loading();
    	if (document.equipmentScheduleForm.form_pmsetup.checked)
    	{
    		document.equipmentScheduleForm.flag.value="1";
    	}

    	document.equipmentScheduleForm.action = "<%=request.getContextPath()%>/control/equipmentScheduleClear";
    	document.equipmentScheduleForm.submit();
    }

    // 单个计划日期新增需验证，改为提交后验证
	function validEquipmentScheduleAdd() {
	    if (document.equipmentScheduleForm.form_pmsetup.checked) {
	        // 周期性新增不限制
    		document.equipmentScheduleForm.flag.value = "1";
    		daySet();
    	} else {
    	    document.equipmentScheduleForm.flag.value = "";
    	    daySet();
    		//var actionURL = "<ofbiz:url>/validEquipmentScheduleAdd</ofbiz:url>?equipmentId=" + document.equipmentScheduleForm.all.item("equipmentId").value + "&periodIndex=" + document.equipmentScheduleForm.all.item("periodIndex").value + "&form_year=" + document.equipmentScheduleForm.all.item("form_year").value + "&form_month=" + document.equipmentScheduleForm.all.item("form_month").value + "&form_day=" + document.equipmentScheduleForm.all.item("form_day").value;
    		//Ext.lib.Ajax.formRequest('equipmentScheduleForm',actionURL,{success: validAddSuccess, failure: validFailure});
	    }
	}

	// 单个新增, 验证远程调用成功，已取消，改为提交后验证
	function validAddSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result != null & result != "") {
			if ("<%=Constants.Y%>" == result.addEnabled) {
				daySet();
			} else {
				Ext.MessageBox.alert('警告', '当前设定日期 超出 警示天数，如需调整，请提交修改申请，并通知设备与工艺主管签核。');
				return;
			}
		}
	}

    // 单个计划日期删除，改为提交后验证
	function validEquipmentScheduleClear() {
	    if (document.equipmentScheduleForm.form_pmsetup.checked) {
			<%
			List guiPriv = (List) request.getSession().getAttribute(SessionNames.GUI_PRIV_LIST_KEY);
			if ( ! guiPriv.contains("DEL_PM_SCHEDULE")) {
			%>
			Ext.MessageBox.alert('警告', '您没有权限按周期删除保养计划！');
				return;
			<% } %>
	        // 周期性删除不验证
    		document.equipmentScheduleForm.flag.value = "1";// 周期性操作标志位
    		dayClear();//执行删除

    		//验证删除
        	//var actionURL = "<ofbiz:url>/validEquipmentScheduleClear</ofbiz:url>?equipmentId=" + document.equipmentScheduleForm.all.item("equipmentId").value + "&periodIndex=" + document.equipmentScheduleForm.all.item("periodIndex").value + "&form_year=" + document.equipmentScheduleForm.all.item("form_year").value + "&form_month=" + document.equipmentScheduleForm.all.item("form_month").value + "&form_day=" + document.equipmentScheduleForm.all.item("form_day").value;
        	//Ext.lib.Ajax.formRequest('equipmentScheduleForm',actionURL,{success: validClearSuccess, failure: validFailure});
    	} else {
    	    document.equipmentScheduleForm.flag.value = "";
    	    dayClear();//单个计划执行删除
    	}
	}

	// 周期性删除, 验证远程调用成功
	function validClearSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result != null & result != "") {
			if ("<%=Constants.Y%>" == result.clearEnabled) {
				dayClear();
			} else {
				Ext.MessageBox.alert('警告', '当前计划不可删除，如需调整，请提交修改申请，并通知设备与工艺主管签核。');
				return;
			}
		}
	}

    //远程调用失败
    var validFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
    };

    function sendSubmit() {
        if(Ext.getDom('form_day').value == "") {
			Ext.MessageBox.alert('警告', '请选择需修改的保养计划!');
			return;
		}

        if(Ext.getDom('newScheduleDate').value == "") {
			Ext.MessageBox.alert('警告', '请选择新计划日期!');
			return;
		}

		if(Trim(Ext.getDom('ownerProcess').value).length == 0) {
			Ext.MessageBox.alert('警告', '请选择工艺课长审批!');
			return;
		}

		Ext.MessageBox.confirm('发送申请确认', '您确信要发送计划延期申请吗？',function result(value){
	        if(value=="yes"){
	            loading();
				var url="<ofbiz:url>/sendSubmitPmDelay</ofbiz:url>?equipmentId=" + Ext.getDom("equipmentId").value + "&periodIndex=" + Ext.getDom("periodIndex").value + "&form_year=" + Ext.getDom("form_year").value + "&form_month=" + Ext.getDom("form_month").value + "&form_day=" + Ext.getDom("form_day").value + "&newScheduleDate=" + Ext.getDom('newScheduleDate').value + "&ownerProcess=" + Ext.getDom("ownerProcess").value;
				document.location=url;
			}else{
				return;
			}
        });
	}

	//初始化页面控件
	Ext.onReady(function(){
	    var startDate = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:false
	    });
	    startDate.applyTo('newScheduleDate');
	 });
</script>

<%
    String[] daystitle = (String[])request.getAttribute("daystitle");
	int column;
	int row;

    String periodIndex = (String)request.getAttribute("periodIndex");
	Map colorMap = (Map)request.getAttribute("colorMap");
%>

<ofbiz:if name="wfSubmitList" size="0">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>
	      保养计划延期申请
	 </legend>
      <table id="objTable1" width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb" >
      	<tr class="tabletitle">
      		<td class="en11pxb">设备/PM周期/延期申请</td>
      		<td class="en11pxb">申请人</td>
      		<td class="en11pxb">申请时间</td>
      		<td class="en11pxb">课长审批</td>
      		<td class="en11pxb">工艺审批</td>
    	</tr>

	    <ofbiz:iterator name="cust" property="wfSubmitList" type="java.util.Map">
	         <tr class="tablelist" id="objTr1">
	        	<td><ofbiz:entityfield attribute="cust" field="OBJECT_NAME" /></td>
	        	<td><ofbiz:entityfield attribute="cust" field="CREATOR_NAME"/></td>
	        	<td><ofbiz:entityfield attribute="cust" field="CREATE_TIME" /></td>
	        	<td>
	        	    <ofbiz:entityfield attribute="cust" field="OWNER" />
	        	    <ofbiz:entityfield attribute="cust" field="STATUS"/>
	        	</td>
	        	<td>
	        	    <ofbiz:entityfield attribute="cust" field="OWNER_PROCESS" />
	        	    <ofbiz:entityfield attribute="cust" field="STATUS_PROCESS" />
	        	</td>
	        </tr>
	   	</ofbiz:iterator>
      </table>
  </fieldset></td>
</tr>
</table>
</ofbiz:if>

<form name="equipmentScheduleForm" id="equipmentScheduleForm">
   <table>
   		<tr>
   			<td><h2 class="title" color="<%=colorMap.get("fontColor") %>">EQ ID: <ofbiz:field attribute="equipmentId"/></h2></td>
   			<td><input name="setup_year" type="text" style="font-size=16" size=3 value=<ofbiz:field attribute="thisYear"/>></td>
   			<td><h2 class="title" color="<%=colorMap.get("fontColor") %>">年度  保养行事历</h2></td>
   			<td><input type="button" value="检视" onclick = "return yearSelect();"></td>
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
				Map timeRangeMap = (HashMap)(map.get("timeRangeMap"));

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

    <input type=hidden name="equipmentId" value="<ofbiz:field attribute="equipmentId"/>">
    <input type=hidden name="form_year">
    <input type=hidden name="form_month">
    <input type=hidden name="form_day">
    <input type=hidden name="flag">

    <table border="1" cellpadding="2" bgcolor=<%=colorMap.get("background") %> bordercolor=<%=colorMap.get("borderColor") %>>
    	<tr>
    	    <td width=30%>PM周期:&nbsp&nbsp&nbsp&nbsp
        		<select  name="periodIndex" onchange="pmtype_change(this)">
        			<option value="">全部</option>
     	          		<ofbiz:if name="equipmentPeriodList">
    		        		<ofbiz:iterator name="defaultPeriod" property="equipmentPeriodList">
    			    			<option value='<ofbiz:inputvalue entityAttr="defaultPeriod" field="periodIndex"/>'>
    			    				<ofbiz:inputvalue entityAttr="defaultPeriod" field="periodName"/>
    			    			</option>
    		      			</ofbiz:iterator>
    	      			</ofbiz:if>
        		</select>
    		</td>
    		<td width=40%><font color=<%=colorMap.get("fontColor") %>>描述:&nbsp&nbsp&nbsp&nbsp</font><input type="text" size=25 name="description"></td>
    		<td width=30%><font color=<%=colorMap.get("fontColor") %>>建立者:&nbsp&nbsp&nbsp&nbsp</font><span id='pm_creator'></span></td>
    	</tr>

    	<tr>
    	    <td width=30%>日期:&nbsp&nbsp&nbsp&nbsp<span id=selectday></span></td>
    		<td width=30%>时段:&nbsp&nbsp&nbsp&nbsp
        		<select name="timeRangeIndex">
     	          		<ofbiz:if name="timeRangeList">
    		        		<ofbiz:iterator name="timeRange" property="timeRangeList">
    			    			<option value='<ofbiz:inputvalue entityAttr="timeRange" field="rangeIndex"/>'>
    			    				<ofbiz:inputvalue entityAttr="timeRange" field="timeRange"/>
    			    			</option>
    		      			</ofbiz:iterator>
    	      			</ofbiz:if>
        		</select>
    		</td>
    		<td width=30%>
    			<center>
    				<font color=<%=colorMap.get("fontColor")%>>周期设定:&nbsp&nbsp
    				    <input type="checkbox" name="form_pmsetup">
    				</font>
					<input name="set" type="button" value="设定"  onclick="validEquipmentScheduleAdd();">&nbsp&nbsp
					<input name="reset" type="button" value="清除"  onclick="validEquipmentScheduleClear();">
    			</center>
    		</td>
    	</tr>

		 <tr>
		    <td>状态:&nbsp;&nbsp;</td>
        	<td colspan='2'>
        	    <table cellpadding="0" bgcolor=<%=colorMap.get("background") %>>
        	        <tr>
                    	<td width=7% bgcolor=<%=colorMap.get("maintenancecolor1") %>></td>
                    	<td nowrap>不可更动PM&nbsp;&nbsp;</td>
                    	<td width=7% bgcolor=<%=colorMap.get("maintenancecolor2") %>></td>
                    	<td nowrap>可更动PM&nbsp;&nbsp;</td>
                    	<td width=7% bgcolor=<%=colorMap.get("maintenancecolor3") %>></td>
                    	<td nowrap>可更动重叠PM&nbsp;&nbsp;</td>
						<td width=7% bgcolor="#C1121C"></td>
                    	<td nowrap>被锁定PM&nbsp;&nbsp;</td>
                    </tr>
                </table>
            </td>
    	</tr>

    	<tr bgcolor="#DFE1EC">
    	    <td>申请修改计划日期为:<input type="text" id="newScheduleDate" name="newScheduleDate" value="" readonly></td>
    	    <td colspan="2">
    	        *工艺审批选择：
	            <select name="ownerProcess" id="ownerProcess">
	                <option value=""></option>
  		            <ofbiz:if name="ownerProcessList">
            			<ofbiz:iterator name="cust" property="ownerProcessList" type="java.util.Map">
            			    <option value="<ofbiz:entityfield attribute="cust" field="SECTION_LEADER"/>">
            			        <ofbiz:entityfield attribute="cust" field="SECTION"/>
            			        :
            			        <ofbiz:entityfield attribute="cust" field="ACCOUNT_NAME"/>
            			    </option>
            		    </ofbiz:iterator>
            	    </ofbiz:if>
			    </select>
	            <input type="button" name="btn" value="发送签核" style="cursor:hand" onclick="sendSubmit()">
            </td>
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
</form>