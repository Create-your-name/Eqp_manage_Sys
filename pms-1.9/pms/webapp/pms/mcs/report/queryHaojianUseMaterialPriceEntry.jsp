<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>


<%
String startDate = UtilFormatOut.checkNull((String) request.getAttribute("startDate"));
String endDate = UtilFormatOut.checkNull((String) request.getAttribute("endDate"));
String deptIndex = UtilFormatOut.checkNull(request.getParameter("deptIndex"));

List HaojianUseList = (List) request.getAttribute("HaojianUseList");

SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
Date d = new Date();
if (endDate == "") {
    endDate = df.format(d);
}

if (startDate == "") {
    d.setDate(d.getDate() - 30);
    startDate = df.format(d);
}
%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function doSubmit(url) {
	loading();
	document.MaterialForm.action = url;
	document.MaterialForm.submit();
}

function turnto(url) {
	document.MaterialForm.action = url;
	document.MaterialForm.submit();
}

<!-- ##################################### yui page script ################################ -->
Ext.onReady(function(){
   //格式化日期
   var startDate = new Ext.form.DateField({
    	format: 'Y-m-d',
        allowBlank:true
    });

 	var endDate = new Ext.form.DateField({
 		format: 'Y-m-d',
        allowBlank:true
    });

    startDate.applyTo('startDate');
    endDate.applyTo('endDate');

    //给select控件(aa)应用样式
	// 初始化部门
	equipmentDept = new Ext.form.ComboBox({
        typeAhead: true,
        triggerAction: 'all',
        transform:'deptIndex',
        width:150,
		emptyText: 'Select a equipmentDept...',
        forceSelection:true
    });
	equipmentDept.on('select',loadEquipment);
    equipmentDept.setValue("<%=deptIndex%>");

    // 初始化设备列表
	var equipmentDS = new Ext.data.Store({
		proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonEquipmentList</ofbiz:url>'}),
		reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
	});

	var equipmentCom = new Ext.form.ComboBox({
		store: equipmentDS,
		displayField:'equipmentId',
		valueField:'equipmentId',
		hiddenName:'equipmentId',
		typeAhead: true,
		mode: 'local',
		width: 150,
		emptyText: 'Select a eqpid...',
		triggerAction: 'all'
	});
	equipmentCom.applyTo('equipmentId');
    var equipmentId='';
	equipmentDS.load({callback:function(){ equipmentCom.setValue(equipmentId); }});

	loadEquipment();
	function loadEquipment() {
		var val = equipmentDept.getValue();
		if (val == '<%=deptIndex%>') {
			var equipmentId  = '<%=UtilFormatOut.checkNull(request.getParameter("equipmentId"))%>';
		} else {
			var equipmentId  = '';
		}
		equipmentDS.load({params:{equipmentDept:val},callback:function(){ equipmentCom.setValue(equipmentId); }});
	};

	// 初始化设备大类
	var EquipmentTypeDS = new Ext.data.Store({
	    proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentTypeByCommon</ofbiz:url>'}),
	    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentType'}]))
	});

	var EquipmentTypeCom = new Ext.form.ComboBox({
	    store: EquipmentTypeDS,
	    displayField: 'equipmentType',
	    valueField: 'equipmentType',
	    hiddenName: 'equipmentType',
	    typeAhead: true,
	    mode: 'local',
	    width: 150,
	    triggerAction: 'all',
	    //emptyText: 'Select a Type...',
	    allowBlank: true
	});
	EquipmentTypeCom.applyTo('equipmentType');
	var equipmentType = '<%=UtilFormatOut.checkNull(request.getParameter("equipmentType"))%>';
	EquipmentTypeDS.load({callback:function(){ EquipmentTypeCom.setValue(equipmentType); }});

	// 初始化ToolGroup
	var ToolGroupDS = new Ext.data.Store({
	    proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonToolGroup</ofbiz:url>'}),
	    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'toolGroup'}]))
	});

	var ToolGroupCom = new Ext.form.ComboBox({
	    store: ToolGroupDS,
	    displayField: 'toolGroup',
	    valueField: 'toolGroup',
	    hiddenName: 'toolGroup',
	    typeAhead: true,
	    mode: 'local',
	    width: 150,
	    triggerAction: 'all',
	    //emptyText: 'Select a Type...',
	    allowBlank: true
	});
	ToolGroupCom.applyTo('toolGroupSelect');
	var ToolGroup = '<%=UtilFormatOut.checkNull(request.getParameter("toolGroup"))%>';
	ToolGroupDS.load({callback:function(){ ToolGroupCom.setValue(ToolGroup); }});
});
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form name="MaterialForm" method="post">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>查询条件</legend>
    	<table width="100%" border="0" cellspacing="1" cellpadding="2">
		    <tr bgcolor="#DFE1EC" height="30">
	     		<td width="12%" bgcolor="#ACD5C9" class="en11pxb">领用日期</td>
	      		<td width="28%"><input type="text" ID="startDate" NAME="startDate" value="<%=startDate%>" readonly size="22"></td>
	   			<td width="9%" bgcolor="#ACD5C9" class="en11pxb">到:</td>
	    		<td width="28%"><input type="text" ID="endDate" NAME="endDate" value="<%=endDate%>"  readonly size="22"></td>
	    	</tr>

			<tr bgcolor="#DFE1EC" height="12">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%">部门</td>
				<td nowrap="nowrap" >
					<select id="deptIndex" name="deptIndex">
						<option value=''></option>
						<ofbiz:if name="deptList">
							<ofbiz:iterator name="cust" property="deptList">
								<option value='<ofbiz:inputvalue entityAttr="cust" field="deptIndex"/>'>
									<ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>
								</option>
							</ofbiz:iterator>
						</ofbiz:if>
					</select>
				</td>

				<td class="en11pxb" bgcolor="#ACD5C9" width="12%">设备</td>
				<td>
				    <input type="text" size="20" name="equipmentId" autocomplete="off"/>
				</td>
			</tr>

			<tr bgcolor="#DFE1EC" height="30">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%">设备大类</td>
				<td>
					<input type="text" size="20" name="equipmentType" autocomplete="off"/>
				</td>
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%">Tool Group</td>
				<td>
					<input type="text" size="20" name="toolGroupSelect" autocomplete="off"/>
				</td>
			</tr>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryHaojianUseMaterialPriceEntry</ofbiz:url>')"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.MaterialForm.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset>
    <legend>备耗件使用金额汇总 列表（子设备金额统计到母设备上）</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9" height="13">
    	    <td class="en11pxb">部门</td>
            <td class="en11pxb">设备</td>
            <td class="en11pxb">设备大类</td>
            <td class="en11pxb">ToolGroup</td>
            <td class="en11pxb">备件金额</td>
        	<td class="en11pxb">耗件金额</td>
    		<td class="en11pxb">总金额</td>
    	</tr>

		<%
		if(HaojianUseList != null && HaojianUseList.size() > 0) {
			for(Iterator it1 = HaojianUseList.iterator();it1.hasNext();) {
			    Map map1 = (Map) it1.next();
		%>
		        <tr class="tablelist">
    		        <td class="en11px"><%=map1.get("MAINT_DEPT")%></td>
        			<td class="en11px"><%=map1.get("EQUIPMENT_ID")%></td>
        			<td class="en11px"><%=map1.get("EQUIPMENT_TYPE")%></td>
        			<td class="en11px"><%=map1.get("TOOL_GROUP")%></td>
        			<td class="en11px">
        				<a href="#" onclick="turnto('<ofbiz:url>/queryHaojianUseDetailPriceEntry?deptIndex=<%=map1.get("DEPT_INDEX")%>&equipmentId=<%=map1.get("EQUIPMENT_ID")%>&mtrGrp=20002P</ofbiz:url>')" >
        				    <%=map1.get("BEIJIAN_PRICE")%>
        				</a>
       				</td>
        			<td class="en11px">
        				<a href="#" onclick="turnto('<ofbiz:url>/queryHaojianUseDetailPriceEntry?deptIndex=<%=map1.get("DEPT_INDEX")%>&equipmentId=<%=map1.get("EQUIPMENT_ID")%>&mtrGrp=20002S</ofbiz:url>')" >
        				    <%=map1.get("HAOJIAN_PRICE")%>
        				</a>
        			</td>
        			<td class="en11px"><%=map1.get("TOTAL_PRICE")%></td>
    		    </tr>
		<%
		    }
		}
		%>

    </table>
    </fieldset></td>
  <tr>
</table>
</form>