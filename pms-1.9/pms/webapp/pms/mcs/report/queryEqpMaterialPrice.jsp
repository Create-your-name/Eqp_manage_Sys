<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>


<%
String startDate = UtilFormatOut.checkNull((String) request.getAttribute("startDate"));
String endDate = UtilFormatOut.checkNull((String) request.getAttribute("endDate"));
String deptIndex = UtilFormatOut.checkNull(request.getParameter("deptIndex"));

List eqpMtrPriceList = (List) request.getAttribute("eqpMtrPriceList");
%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function doSubmit(url) {
//	if(Ext.getDom('deptIndex').value=='') {
//		Ext.MessageBox.alert('警告', '请选部门!');
//		return;
//	}
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

    // 初始化物料组
	var mtrGrpDS = new Ext.data.Store({
	    proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonMtrGrp</ofbiz:url>'}),
	    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'mtrGrp'}, {name: 'mtrGrpDesc'}]))
	});

	var mtrGrpCom = new Ext.form.ComboBox({
	    store: mtrGrpDS,
	    displayField: 'mtrGrpDesc',
	    valueField: 'mtrGrp',
	    hiddenName: 'mtrGrp',
	    typeAhead: true,
	    mode: 'local',
	    width: 150,
	    triggerAction: 'all',
	    emptyText: 'Select a group...',
	    allowBlank: true
	});
	mtrGrpCom.applyTo('mtrGrpSelect');

	var mtrGrp = '<%=UtilFormatOut.checkNull(request.getParameter("mtrGrp"))%>';
	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(mtrGrp); }});

    //初始设备列表
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
	     		<tr bgcolor="#DFE1EC">
	      		 <td width="12%" bgcolor="#ACD5C9" class="en11pxb"><i18n:message key="mcs.using_time" /></td>
	      		 <td width="28%"><input type="text" ID="startDate" NAME="startDate" value="<%=startDate%>" readonly size="22"></td>
	   			 <td width="9%" bgcolor="#ACD5C9" class="en11pxb">到:</td>
	    		 <td width="28%"><input type="text" ID="endDate" NAME="endDate" value="<%=endDate%>"  readonly size="22"></td>
	    	</tr>
			<tr bgcolor="#DFE1EC" height="12">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.dept" /></td>
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

				<td class="en11pxb" bgcolor="#ACD5C9" width="12%">
					<i18n:message key="mcs.equipment" />
				</td>
				<td >
				    <input type="text" size="20" name="equipmentId" autocomplete="off"/>
				</td>
			</tr>

			<tr bgcolor="#DFE1EC" height="30">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_group" /></td>
				<td><input type="text" size="20" name="mtrGrpSelect" autocomplete="off"/></td>
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_number" /></td>
				<td ><input type="text"  size="19" name="mtrNum"  value="<%=UtilFormatOut.checkNull(request.getParameter("mtrNum"))%>"/></td>
			</tr>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryEqpMaterialPrice</ofbiz:url>')"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.MaterialForm.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
	<td><ul class="button">
			<li><a class="button-text" href="javascript:history.back();"><span>&nbsp;返回&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset>
    <legend>物料号 列表</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9" height="13">
    	    <td class="en11pxb"><i18n:message key="mcs.dept" /></td>
            <td class="en11pxb">Tool Type</td>
            <td class="en11pxb">Tool Group</td>
        	<td class="en11pxb"><i18n:message key="mcs.equipment" /></td>
    		<td class="en11pxb"><i18n:message key="mcs.material_group" /></td>
    		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
    		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
    		<td class="en11pxb"><i18n:message key="mcs.avg_price" /></td>
    		<td class="en11pxb"><i18n:message key="mcs.qty" /></td>
    		<td class="en11pxb">总价</td>
    	</tr>

		<%
		if(eqpMtrPriceList != null && eqpMtrPriceList.size() > 0) {
			for(Iterator it1 = eqpMtrPriceList.iterator();it1.hasNext();) {
			    Map map1 = (Map) it1.next();
		%>
		        <tr class="tablelist">
    		        <td class="en11px"><%=map1.get("MAINT_DEPT")%></td>
        			<td class="en11px"><%=map1.get("TOOL_TYPE")%></td>
        			<td class="en11px"><%=map1.get("TOOL_GROUP")%></td>
        			<td class="en11px"><%=map1.get("USING_OBJECT_ID")%></td>
        			<td class="en11px"><%=map1.get("MTR_GRP")%></td>
        			<td class="en11px"><%=map1.get("MTR_NUM")%></td>
        			<td class="en11px"><%=map1.get("MTR_DESC")%></td>
        			<td class="en11px"><%=map1.get("MOVING_AVERAGE_PRICE")%></td>
        			<td class="en11px"><%=map1.get("COUNT_NUM")%></td>
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