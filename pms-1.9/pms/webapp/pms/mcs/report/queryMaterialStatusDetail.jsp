<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>


<%
String startDate = UtilFormatOut.checkNull(request.getParameter("startDate"));
String endDate = UtilFormatOut.checkNull(request.getParameter("endDate"));
String equipmentDept = UtilFormatOut.checkNull(request.getParameter("deptIndex"));
String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));

SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
Date date = new Date();
if (endDate.equals("")) {
   endDate = df.format(date);
}
//out.print(endDate);

int n = -30;
date.setDate(date.getDate()+n);
if (startDate.equals("")) {
   startDate = df.format(date);
}
%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function doSubmit(url) {
//	if(Ext.getDom('deptIndex').value=='') {
//		Ext.MessageBox.alert('警告', '请选部门!');
//		return;
//	}
	//loading();
	document.MaterialForm.action = url;
	document.MaterialForm.submit();
}
function cancel(url) {
	history.back();
}

function turnto(url) {
    var result = window.showModalDialog(url, window, "dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");
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
    equipmentDept.setValue("<%=equipmentDept%>");
    
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

	var mtrGrp = '<%=mtrGrp%>';
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

	//状态数据初始化
    var statusDS = new Ext.data.Store({
	    proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonStatusList</ofbiz:url>'}),
		reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'description'}, {name: 'status'}]))
	});

	statusCom = new Ext.form.ComboBox({
	    store: statusDS,
	    displayField:'description',
	    valueField:'status',
	    hiddenName:'status',
	    typeAhead: true,
	    mode: 'local',
	    width: 150,
		emptyText: 'Select a status...',
	    triggerAction: 'all'
    });
    statusCom.applyTo('status');

	var status = '<%=UtilFormatOut.checkNull(request.getParameter("status"))%>';
	statusDS.load({callback:function(){ statusCom.setValue(status); }});

	loadEquipment();
	function loadEquipment() {
		var val = equipmentDept.getValue();
		if (val == '<%=equipmentDept%>') {
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
<form name="MaterialForm" method="post" >
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>查询条件</legend>
    	<table width="100%" border="0" cellspacing="1" cellpadding="2">
		    <tr bgcolor="#DFE1EC" height="30">
	     		<tr bgcolor="#DFE1EC">
	      		 <td width="12%" bgcolor="#ACD5C9" class="en11pxb"><i18n:message key="mcs.doc_time" /></td>
	      		 <td width="28%"><input type="text" ID="startDate" NAME="startDate" value="<%=startDate%>" readonly></td>
	   			 <td width="9%" bgcolor="#ACD5C9" class="en11pxb">到:</td>
	    		 <td width="28%"><input type="text" ID="endDate" NAME="endDate" value="<%=endDate%>"  readonly></td>
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
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%">
					<i18n:message key="mcs.equipment" />
				</td>
				<td ><input type="text" size="20" name="equipmentId" autocomplete="off"/>
				</td>
			</tr>
			<tr bgcolor="#DFE1EC" height="30">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_group" /></td>
				<td><input type="text" size="20" name="mtrGrpSelect" autocomplete="off"/></td>
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_number" /></td>
				<td ><input type="text"  size="20" name="mtrNum" autocomplete="off" value="<%=UtilFormatOut.checkNull(request.getParameter("mtrNum"))%>"/></td>
			</tr>
			<tr bgcolor="#DFE1EC" height="30">
				<td class="en11pxb"  bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.current_status" /></td>
				<td colspan="3"><input type="text" size="20" name="status" autocomplete="off" /></td>
			</tr>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryMaterialStatusDetail</ofbiz:url>')"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.MaterialForm.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
    <td>
		<ul class="button">
<%--				<li><a class="button-text" href="javascript:cancel('<ofbiz:url>/queryMaterialStatusBoardEntry</ofbiz:url>')"><span>&nbsp;返回&nbsp;</span></a></li>--%>
				<li><a class="button-text" href="javascript:history.back();"><span>&nbsp;返回&nbsp;</span></a></li>
		</ul>
	</td>
  </tr>
</table>

<%--注意维护的时候是更具materialIndex(物料索引)进行关联字段的--%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>当前状态信息</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9" height="13">
			<td class="en11pxb"><i18n:message key="mcs.index_alias_name" /></td>
			<td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
			<td class="en11pxb"><i18n:message key="mcs.doc_time" /></td>
			<td class="en11pxb"><i18n:message key="mcs.dept" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_group" /></td>
			<td class="en11pxb"><i18n:message key="mcs.current_status" /></td>
			<td class="en11pxb"><i18n:message key="mcs.equipment" /></td>
			<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date"/></td>
			<td class="en11pxb"><i18n:message key="mcs.mrb_date" /></td>
    	</tr>
    	<ofbiz:if name="statusDetailList">
			<ofbiz:iterator name="cust" property="statusDetailList" type="java.util.Map">
			<tr bgcolor="#DFE1EC">
			 	<td class="en11px"><a href="#" onclick="turnto('<ofbiz:url>/queryMaterialStatusHist?materialStatusIndex=<ofbiz:entityfield attribute="cust" field="MATERIAL_STATUS_INDEX"/></ofbiz:url>')"><ofbiz:entityfield attribute="cust" field="ALIAS_NAME"/></a></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="VENDOR_BATCH_NUM"  /></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="DOC_TIME"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="EQUIPMENT_DEPT"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_NUM"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_GRP"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="DESCRIPTION"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="USING_OBJECT_ID"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="SHELF_LIFE_EXPIRATION_DATE"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MRB_DATE"/></td>
		    </ofbiz:iterator>
	    </ofbiz:if>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>
</form>