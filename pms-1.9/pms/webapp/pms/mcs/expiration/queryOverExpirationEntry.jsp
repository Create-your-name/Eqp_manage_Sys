<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

<%
	//String listSize = (String) request.getAttribute("listSize");
    String startDate = UtilFormatOut.checkNull((String) request.getAttribute("startDate"));
%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
Ext.onReady(function(){
    //格式化日期
    var startDate = new Ext.form.DateField({
        format: 'Y-m-d',
        allowBlank:true
    });
    startDate.applyTo('startDate');

});
var useMaterialById = function() {

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
			var mtrGrp = '<%=UtilFormatOut.checkNull((String) request.getAttribute("mtrGrp"))%>';
	    	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(mtrGrp); }});
		}
	}
}();

Ext.EventManager.onDocumentReady(useMaterialById.init, useMaterialById, true);


var useMaterialById = function() {

	var deptIndexDS, equipmentDeptCom;

	return {
		init : function() {
			this.createDataStore();
		    this.createCombox();
		    this.initLoad();
		},

		//设置数据源
		createDataStore : function() {

			deptIndexDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonEquipmentDept</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'deptIndex'}, {name: 'equipmentDept'}]))
		    });
		},

		createCombox : function() {

			//设置物料组
		    equipmentDeptCom = new Ext.form.ComboBox({
			    store: deptIndexDS,
			    displayField:'equipmentDept',
			    valueField:'deptIndex',
			    hiddenName:'deptIndex',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all',
			    allowBlank: true
		    });
		    equipmentDeptCom.applyTo('deptIndexSelect');
		},

		initLoad : function() {
	    	deptIndexDS.load({callback:function(){ equipmentDeptCom.setValue(<ofbiz:field attribute="deptIndex"/>); }});
		}
	}
}();

Ext.EventManager.onDocumentReady(useMaterialById.init, useMaterialById, true);

</script>



<script language="javascript">
function queryMaterial() {
    doSubmit("<ofbiz:url>/queryOverExpirationEntry</ofbiz:url>");
}

function doSubmit(url) {
	loading();
	document.expirationDateForm.action = url;
	document.expirationDateForm.submit();
}

function viewStatusHist(materialStatusIndex) {
    var url = "<ofbiz:url>/queryMaterialStatusHist</ofbiz:url>?materialStatusIndex=" + materialStatusIndex;
    var result = window.showModalDialog(url, window, "dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");
}
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form method="post" name="expirationDateForm">

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>分类条件</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
			<td class="tabletitle" width="12%"><i18n:message key="mcs.dept" /></td>
			<td class="tablelist" width="25%">
			    <input type="text" size="40" name="deptIndexSelect" autocomplete="off"/>
			</td>
			<td class="tabletitle" width="12%">
			    <i18n:message key="mcs.material_group" />
			</td>
			<td class="tablelist" width="25%">
			    <input type="text" size="40" name="mtrGrpSelect" autocomplete="off"/>
			    <input type="hidden" name="flag" value="1">
			</td>
            <td class="tabletitle" width="12%" bgcolor="#ACD5C9" class="en11pxb">有效期截止日期:</td>
            <td class="tablelist" width="25%">
                <input type="text" ID="startDate" NAME="startDate" value="<%=startDate%>" readonly>
            </td>
        </tr>
	    </tr>
	  </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="javascript:queryMaterial();"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>物料列表</legend>
      <table id="objTableFabRepair" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      		<td class="en11pxb"><i18n:message key="mcs.index_alias_name" /></td>
      	    <td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.mrb_date" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.status" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.equipment" /></td>
      		<td class="en11pxb">距有效期(天)</td>
    	</tr>

	    <ofbiz:if name="materialList" size="0">
			<ofbiz:iterator name="material" property="materialList" type="java.util.Map">
	         <tr class="tablelist" id="objTrFabRepair" style="cursor:hand">
				<td class="en11px">
				    <a href="javascript:viewStatusHist('<ofbiz:entityfield attribute="material" field="MATERIAL_STATUS_INDEX" />')">
	        	        <ofbiz:entityfield attribute="material" field="ALIAS_NAME" />
	        	    </a>
				</td>
	            <td class="en11px"><ofbiz:entityfield attribute="material" field="VENDOR_BATCH_NUM" /></td>
	        	<td class="en11px"><ofbiz:entityfield attribute="material" field="MTR_NUM" /></td>
	        	<td class="en11px"><ofbiz:entityfield attribute="material" field="MTR_DESC" /></td>
	        	<td class="en11px"><ofbiz:entityfield attribute="material" field="SHELF_LIFE_EXPIRATION_DATE" /></td>
	        	<td class="en11px"><ofbiz:entityfield attribute="material" field="MRB_DATE" /></td>
	        	<td class="en11px"><ofbiz:entityfield attribute="material" field="STATUS" /></td>
	        	<td class="en11px"><ofbiz:entityfield attribute="material" field="USING_OBJECT_ID" /></td>
        	    <td class="en11px"><ofbiz:inputvalue entityAttr="material" field="LEFT_EXP_DAYS" /></td>
	        </tr>
			</ofbiz:iterator>
		</ofbiz:if>

      </table>
  </fieldset></td>
</tr>
</table>

</form>