<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<%
	//String dept = UtilFormatOut.checkNull(request.getParameter("equipmentDept"));
	String dept = UtilFormatOut.checkNull((String) request.getAttribute("equipmentDept"));
%>

<script language="javascript">
function doSubmit(url) {
	if(Ext.getDom('equipmentDept').value=='') {
		Ext.MessageBox.alert('警告', '请选择部门!');
		return;
	}
	loading();
	document.eqpForm.action = url;
	document.eqpForm.submit();
}

function queryDcop() {
		var url = '<ofbiz:url>/equiList</ofbiz:url>?actionName=' + Ext.getDom('eqpid').value;
		window.open(url,"equiList",
			"top=130,left=240,width=685,height=180,title=,channelmode=0," +
			"directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
			"scrollbars=1,status=1,titlebar=0,toolbar=no");
	}

Ext.onReady(function(){
	var deptDS = new Ext.data.Store({
	    proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentDept</ofbiz:url>'}),
	    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentDept'}]))
	});

	var deptCom = new Ext.form.ComboBox({
	    store: deptDS,
	    displayField:'equipmentDept',
	    valueField:'equipmentDept',
	    hiddenName:'equipmentDept',
	    typeAhead: true,
	    mode: 'local',
	    width: 170,
	    triggerAction: 'all'
	});
	deptCom.applyTo('deptSelect');

	var dept = '<%=dept%>';
	deptDS.load({callback:function(){ deptCom.setValue(dept); }});
});
</script>
<form name="eqpForm" method="post" action="<%=request.getContextPath()%>/control/equipmentDefine">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>查询设备信息</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#DFE1EC" height="30">
    		<td class="en11pxb" bgcolor="#ACD5C9" width="12%">部门</td>
			<td><input type="text" size="40" name="deptSelect" autocomplete="off"/></td>
    	</tr>
        <!--<tr bgcolor="#DFE1EC">
          <td width="12%" class="en11pxb">&nbsp;设备</td>
          <td width="12%"><input name="eqpid" type="text" class="input" value="<ofbiz:inputvalue entityAttr="flowAction" field="actionName" default="" tryEntityAttr="true"/>" /></td>
          <td width="76%"><a href="#"><img src="../images/icon_search.gif" width="15" height="16" border="0" onClick="queryDcop()"></a></td>
        </tr>-->
      </table>
	 </fieldset>
	</td>
  <tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/equipmentDefineQueryList')"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.eqpForm.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
	<td><ul class="button">
			<li><a class="button-text" href="<%=request.getContextPath()%>/control/equipmentDefine?eqpid=''"><span>&nbsp;新增&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>设备列表</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9">
    		<td class="en11pxb">设备ID</td>
			<td class="en11pxb">Loaction</td>
			<td class="en11pxb">设备大类</td>
			<td class="en11pxb">维护部门</td>
			<td class="en11pxb">课别</td>
    	</tr>
    	<ofbiz:if name="equipmentList">
			<ofbiz:iterator name="cust" property="equipmentList">
			<tr bgcolor="#DFE1EC">
			 	<td class="en11px"><a href="<%=request.getContextPath()%>/control/equipmentDefine?eqpid=<ofbiz:entityfield attribute="cust" field="equipmentId"/>"><ofbiz:entityfield attribute="cust" field="equipmentId"/></a></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="location"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="equipmentType"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="maintDept"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="section"/></td>
			</tr>
		    </ofbiz:iterator>
	    </ofbiz:if>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>
</form>