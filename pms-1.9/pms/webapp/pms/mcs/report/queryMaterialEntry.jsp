<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.entity.GenericDelegator"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>


<%
String deptIndex = UtilFormatOut.checkNull(request.getParameter("deptIndex"));
if (deptIndex == "") {
	deptIndex = (String) request.getAttribute("iniDeptIndex");
}
String flag = "0";
%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function doSubmit(url) {
	if(Ext.getDom('deptIndex').value=='') {
		Ext.MessageBox.alert('警告', '请选部门!');
		return;
	}
	//loading();
	document.MaterialForm.action = url;
	document.MaterialForm.submit();
}

<!-- ##################################### yui page script ################################ -->
Ext.onReady(function(){

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
//给select控件(aa)应用样式
		dept = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'deptIndex',
	        width:150,
			emptyText: 'Select a dept...',
	        forceSelection:true
	    });
      //表示当列表选择的时候，取loadObjectId作为处理函数
	  dept.on('select',loadObjectId);

//初始化已维护设备
	var objectDS = new Ext.data.Store({
		proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentByDeptIndex</ofbiz:url>'}),
	    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
	});

	var objectCom = new Ext.form.ComboBox({
	    store: objectDS,
	    displayField:'equipmentId',
	    valueField:'equipmentId',
	    hiddenName:'usingObjectId',
	    typeAhead: true,
	    mode: 'local',
	    width: 150,
		emptyText: 'Select a eqpid...',
	    triggerAction: 'all'
	});
	objectCom.applyTo('usingObjectId');

	//var usingObjectId = '<%//=UtilFormatOut.checkNull(request.getParameter("usingObjectId"))%>';
	//objectDS.load({callback:function(){ objectCom.setValue(usingObjectId); }});

    //联动函数
    function loadObjectId() {
    		var val = dept.getValue();
			//alert(val);
    		if (val == '<%=deptIndex%>') {
    		    var usingObjectId  = '<%=UtilFormatOut.checkNull(request.getParameter("usingObjectId"))%>';
    		} else {
    		    var usingObjectId  = '';
    		}

    		objectDS.load({params:{deptIndex:val},callback:function(){ objectCom.setValue(usingObjectId); }});
    };

	//设置dept和设备的值
	objectCom.setValue('<%=UtilFormatOut.checkNull(request.getParameter("usingObjectId"))%>');
	dept.setValue("<%=deptIndex%>");
	//初始化的时候带出来设备
	loadObjectId();

});
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form name="MaterialForm" method="post" action="<%=request.getContextPath()%>/control/defineMaterial">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>查询条件</legend>
    	<table width="100%" border="0" cellspacing="1" cellpadding="2">
		    <tr bgcolor="#DFE1EC" height="30">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.dept" /><i18n:message key="mcs.data_required_red_star" />	</td>
				<td>
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
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.equipment" /></td>
				<td><input type="text" size="20" name="usingObjectId" autocomplete="off"/></td>
			</tr>

			<tr bgcolor="#DFE1EC" height="30">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_group" /></td>
				<td><input type="text" size="20" name="mtrGrpSelect" autocomplete="off"/></td>
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_number" /></td>
				<td><input type="text" size="20" name="mtrNum" autocomplete="off" value="<%=UtilFormatOut.checkNull(request.getParameter("mtrNum"))%>"/></td>
			</tr>

			<tr bgcolor="#DFE1EC" height="30">
    		    <td class="en11pxb" bgcolor="#ACD5C9" width="12%">是否启用MCS管控</td>
    		    <td>
    		        <select id="inControl" name="inControl" class="select-short">
    					<option value='1' <%if ("1".equals(request.getParameter("inControl"))) {%>selected<%}%> >1启用</option>
    					<option value='0' <%if ("0".equals(request.getParameter("inControl"))) {%>selected<%}%> >0禁用</option>
    				</select>
    		    </td>

    		    <td class="en11pxb" bgcolor="#ACD5C9" width="12%">需要报废入库</td>
    		    <td>
    		        <select id="needScrapStore" name="needScrapStore" class="select-short">
    		        	<option value='' <%if ("".equals(request.getParameter("needScrapStore"))) {%>selected<%}%> ></option>
    					<option value='Y' <%if ("Y".equals(request.getParameter("needScrapStore"))) {%>selected<%}%> >Y</option>
    					<option value='N' <%if ("N".equals(request.getParameter("needScrapStore"))) {%>selected<%}%> >N</option>
    				</select>
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
			<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryMaterialEntry</ofbiz:url>')"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.MaterialForm.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<%--注意维护的时候是更具materialIndex(物料索引)进行关联字段的--%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>物料信息列表</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9">
    		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
			<ofbiz:if name="flag" value="OK">
			<td class="en11pxb">价格</td>
			</ofbiz:if>
			<td class="en11pxb"><i18n:message key="mcs.material_group" /></td>
			<td class="en11pxb"><i18n:message key="mcs.need_scrap_store" /></td>
			<td class="en11pxb"><i18n:message key="mcs.usable_time_limit" /></td>
			<td class="en11pxb">提前报警天数(天)</td>
			<td class="en11pxb">提前领用数量</td>
    	</tr>
    	<ofbiz:if name="materialList">
			<ofbiz:iterator name="cust" property="materialList"  type="java.util.Map">
			<tr bgcolor="#DFE1EC">
			 	<td class="en11px"><a href="<ofbiz:url>/queryMaterial?materialIndex=<ofbiz:entityfield attribute="cust" field="MATERIAL_INDEX"/></ofbiz:url>"><ofbiz:entityfield attribute="cust" field="MTR_NUM"/></a></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC"/></td>
				<ofbiz:if name="flag" value="OK">	
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MOVING_AVERAGE_PRICE"/></td>	
				</ofbiz:if>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_GRP"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="NEED_SCRAP_STORE"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="USABLE_TIME_LIMIT"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="PRE_ALARM_DAYS"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="PRE_STO_NUMBER"/></td>
		    </ofbiz:iterator>
	    </ofbiz:if>
	    <input type="hidden" name="flag" id="flag" value="<%=flag%>">
      </table>
	 </fieldset>
	</td>
  <tr>
</table>
</form>