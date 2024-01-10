<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<%@ include file="../../pms/yui-ext/ext-comdlg.jsp"%>

<!-- ##################################### yui page script ################################ -->
<script language="javascript">
var defineVendorMatetial = function() {

	var mtrGrpDS, mtrGrpCom;

	return {
		init : function() {
			this.createDataStore();
		    this.createCombox();
		    this.initLoad();
		},

		//设置数据源
		createDataStore : function() {

			mtrGrpDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonMtrGrp</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'mtrGrp'}, {name: 'mtrGrpDesc'}]))
		    });
		},

		createCombox : function() {

			//设置物料组
		    mtrGrpCom = new Ext.form.ComboBox({
			    store: mtrGrpDS,
			    displayField:'mtrGrpDesc',
			    valueField:'mtrGrp',
			    hiddenName:'mtrGrp',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all',
			    allowBlank: true
		    });
		    mtrGrpCom.applyTo('mtrGrpSelect');
		},

		initLoad : function() {
	    	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(<ofbiz:field attribute="mtrGrp"/>); }});
		}
	}
}();

Ext.EventManager.onDocumentReady(defineVendorMatetial.init, defineVendorMatetial, true);
</script>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function queryqueryVendorMaterial() {

	if(Ext.get('mtrGrp').dom.value==""){
		Ext.MessageBox.alert('警告', '请选择物料组!');
		return;
	}
	
		queryVendorMaterialForm.submit();

}

function doSubmit(url) {

	loading();
	document.queryVendorMaterialForm.action = url;
	document.queryVendorMaterialForm.submit();
}

</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form method="post" name="queryVendorMaterialForm" action="<%=request.getContextPath()%>/control/queryVendorMaterialEntry" >
<input name="eventObject" type="hidden" value="" />

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>分类条件</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
	    <tr>
    		<td class="tabletitle" width="12%">
			    <i18n:message key="mcs.material_group" />
				<i18n:message key="mcs.data_required_red_star" />
			</td>
			<td class="tablelist" width="30%">
			    <input type="text" size="40" name="mtrGrpSelect" autocomplete="off"/>
			</td>

			<td class="tabletitle" width="12%"><i18n:message key="mcs.material_number" /></td>
			<td class="tablelist" width="30%">
			    <input type="text" class="input" size="20" name="mtr_Num" value="<ofbiz:field attribute="mtr_Num"/>" />
			</td>
	    </tr>
	  </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="javascript:queryqueryVendorMaterial();"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<p>&nbsp;</p>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><fieldset>
		<legend>厂商料号列表</legend>
			<table id="objTable1" width="100%" border="0" cellspacing="1" cellpadding="2">
				<tr class="tabletitle">
					<td width="14%" class="en11pxb"><i18n:message key="mcs.material_number" /></td>
					<td width="16%" class="en11pxb"><i18n:message key="mcs.material_description" /></td>
					<td width="16%" class="en11pxb"><i18n:message key="mcs.vendor_mtr_num" /></td>
				</tr>
				
				<ofbiz:if name="vendorMaterialList" size="0">
				<ofbiz:iterator name="cust" property="vendorMaterialList" type="java.util.Map">
				<tr class="tablelist" id="objTr1" style="cursor:hand">
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_NUM" /></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC" /></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="VENDOR_MTR_NUM" /></td>
				</tr>
				</ofbiz:iterator>
				</ofbiz:if>
			</table>
		</fieldset></td>
	</tr>
</table>

</form>

<div id="x-dlg" style="visibility:hidden;">
</div>
