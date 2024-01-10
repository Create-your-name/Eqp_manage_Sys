<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<%
    String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
    String type = UtilFormatOut.checkNull(request.getParameter("type1"));
%>

<!-- ##################################### yui page script ################################ -->
<script language="javascript">
var shipConfirmById = function() {

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
			var mtrGrp = '<%=UtilFormatOut.checkNull(request.getParameter("mtrGrp"))%>';
	    	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(mtrGrp); }});
		}
	}
}();

Ext.EventManager.onDocumentReady(shipConfirmById.init, shipConfirmById, true);
</script>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function queryShipConfirmMaterial() {
	if(document.getElementById("type1").checked==true){
		document.getElementById("oldStatus").value="<%=ConstantsMcs.GENERAL_SCRAP_LEADER%>";
	}else if(document.getElementById("type2").checked==true){
		document.getElementById("oldStatus").value="<%=ConstantsMcs.VENDOR_REPAIR_LEADER%>";
	}
    doSubmit("<ofbiz:url>/shipConfirmEntry</ofbiz:url>");
}

function changeMaterialStatus(oldStatus, newStatus, newStatusDesc) {
    Ext.MessageBox.confirm('操作确认', '您确信选择 ' + newStatusDesc + ' 吗？',function result(value){
        if(value=="yes"){
			if (checkForm()) {
				doSubmit('<ofbiz:url>/shipConfirmById</ofbiz:url>?newStatus=' + newStatus +'&oldStatus=' + oldStatus);
        	}
		}else{
			return;
		}
    });
}

function checkForm() {
    if (totalSelected == 0) {
        Ext.MessageBox.alert('警告', '请选择需要更换的物料');
        return false;
    }

	return true;
}

function checkInputValue(stockAddressInput) {// onblur校验
    var stockAddress = stockAddressInput.value;
    if (stockAddress == "") {
        alert('请输入 库位！');
        stockAddressInput.value = "";
        stockAddressInput.focus();
    }
}

function doSubmit(url) {

	loading();
	document.shipConfirmForm.action = url;
	document.shipConfirmForm.submit();
}
</script>

<!-- ##################################### checkbox script ################################ -->
<script language="javascript">
<!--
var totalSelected = 0;                       //选取的总数;
var totalCount = 0;                          //资料的总数;

function selectOneCheckBoxInput(formActionType, rowIndex,oldStatus) {
    //--------------rowIndex=1,2,3... theIdx=0,1,2,3......--------------
    var checkBoxSelectAll = document.getElementById(formActionType+"CheckBoxSelectAll");
	var objTable = document.getElementById(formActionType+"Table");
	totalCount = objTable.rows.length - 1;

	var theIdx = rowIndex - 1;

	if (totalCount > 1) {
	    var objCheckbox = document.getElementsByName(formActionType+"Checkbox")[theIdx];
	    var objTr = document.getElementsByName(formActionType+"Tr")[theIdx];
		var objStockAddress = document.getElementsByName("stockAddress")[theIdx];

	} else {
	    var objCheckbox = document.getElementById(formActionType+"Checkbox");
	    var objTr = document.getElementById(formActionType+"Tr");
		var objStockAddress = document.getElementsByName("stockAddress")[theIdx];

	}

	objCheckbox.checked = !objCheckbox.checked;//CheckBox添加按行选择功能
	if (objCheckbox.checked) {
		objTr.className = "tablelistchecked";
		totalSelected++;

		//if (oldStatus == "VENDOR-REPAIR-LEADER") {
			objStockAddress.disabled = false;
			objStockAddress.focus();
		//}

	} else {
		objTr.className = "tablelist";
		totalSelected--;

		//if (oldStatus == "VENDOR-REPAIR-LEADER") {
			objStockAddress.disabled = true;
			objStockAddress.value = "";
		//}
	}

	if (totalSelected == totalCount) {
	    checkBoxSelectAll.checked = true;
	} else {
	    checkBoxSelectAll.checked = false;
	}
}

function selectAllCheckBox(objTable, objTr, objCheckbox, isChecked){//----------单击 top chceckbox全部选择和取消选择---------
	if (objTable == null || objTr == null || objCheckbox == null) {
		return;
	}

	var i;
	var varColor;

	totalCount = objTable.rows.length - 1;
	if (totalCount == 0) {
		return;
    }

	if (objCheckbox.length > 1) {
		for(i=0; i < objCheckbox.length; i++){
			objCheckbox[i].checked = isChecked;
			objTr[i].className = isChecked ? "tablelistchecked" : "tablelist";
		}
	} else {// only one Checkbox
	    objCheckbox.checked = isChecked;
		objTr.className = isChecked ? "tablelistchecked" : "tablelist";
	}

	totalSelected = isChecked ? totalCount : 0;
}

function viewStatusHist(materialStatusIndex) {
    var url = "<ofbiz:url>/queryMaterialStatusHist</ofbiz:url>?materialStatusIndex=" + materialStatusIndex;
    var result = window.showModalDialog(url, window, "dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");
}

-->
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form method="post" name="shipConfirmForm">
<input id="oldStatus" type="hidden" name="oldStatus" />

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>分类条件</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
		<tr>
    		<td class="tabletitle" width="12%">
    		    <i18n:message key="mcs.type" />
    		    <i18n:message key="mcs.data_required_red_star" />
    		</td>
			<td class="tablelist" width="30%" colspan="3">
			    <input type="radio"  id="type1" name="type1" value="<%=ConstantsMcs.GENERAL_SCRAP_LEADER%>" checked onclick="queryShipConfirmMaterial();"><i18n:message key="mcs.status_general_scrap" />&nbsp;&nbsp;&nbsp;
			    <input type="radio"  id="type2" name="type1" value="<%=ConstantsMcs.VENDOR_REPAIR_LEADER%>" onclick="queryShipConfirmMaterial();"><i18n:message key="mcs.status_vendor_repair" />
			</td>
	    </tr>
	    <tr>
    		<td class="tabletitle" width="12%">
			    <i18n:message key="mcs.material_group" />
			</td>
			<td class="tablelist" width="30%">
			    <input type="text" size="40" name="mtrGrpSelect" autocomplete="off"/>
			</td>

			<td class="tabletitle" width="12%"><i18n:message key="mcs.material_number" /></td>
			<td class="tablelist" width="30%">
			    <input type="text" class="input" size="20" name="mtrNum" value="<ofbiz:field attribute="mtrNum"/>" />
			</td>
	    </tr>
	  </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="javascript:queryShipConfirmMaterial();"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>仓库确认物料列表</legend>
      <table id="shipConfirmTable" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      	    <td class="en11pxb">
      		    <input type="checkbox" name="shipConfirmCheckBoxSelectAll" id="shipConfirmCheckBoxSelectAll"
      		    class="hand" title="全选/取消选择"
      		    <ofbiz:if name="shipConfirmList" size="0">
      		        onClick="selectAllCheckBox(shipConfirmTable, shipConfirmTr, shipConfirmCheckbox, this.checked);"
      		    </ofbiz:if>
      		    >
      		</td>
      	    <td class="en11pxb"><i18n:message key="mcs.index_alias_name" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.avg_price" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.stock_address" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.operator" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.operation_time" /></td>
    	</tr>

		<ofbiz:if name="shipConfirmList" size="0">
		<ofbiz:iterator name="cust" property="shipConfirmList" type="java.util.Map">
	         <tr class="tablelist" id="shipConfirmTr" style="cursor:hand"
	          onClick="javascript:selectOneCheckBoxInput('shipConfirm', this.rowIndex,'<ofbiz:inputvalue entityAttr="cust" field="STATUS"/>');">
	            <td class="en11px">
	        	    <input type="checkbox" name="shipConfirmCheckbox" id="shipConfirmCheckbox"
	        	    value="<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_STATUS_INDEX" tryEntityAttr="true" />"
	        	    onClick="this.checked=!this.checked;"
	        	    >
	        	</td>
	        	<td class="en11px">
	        	    <a href="javascript:viewStatusHist('<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_STATUS_INDEX" tryEntityAttr="true" />')">
	        	        <ofbiz:inputvalue entityAttr="cust" field="ALIAS_NAME" tryEntityAttr="true" />
	        	    </a>
	        	</td>
	            <td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MTR_NUM" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MTR_DESC" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MOVING_AVERAGE_PRICE" tryEntityAttr="true" /></td>
				<td class="en11px">
	        	    <!-- 输入库位 -->
      		        <input type="text" class="input" size="10" name="stockAddress" id="stockAddress" value=""
      		        disabled="true" onblur="checkInputValue(this)"/>
      		    </td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="ACCOUNT_NAME" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="UPDATE_TIME" tryEntityAttr="true" /></td>
	        </tr>
		</ofbiz:iterator>
		</ofbiz:if>

      </table>
  </fieldset></td>
</tr>
</table>

<%
String materialStatus = UtilFormatOut.checkNull(request.getParameter("oldStatus"));
if (materialStatus.equals("VENDOR-REPAIR-LEADER")) {
%>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.VENDOR_REPAIR_LEADER%>','<%=ConstantsMcs.VENDOR_REPAIR_SHIP%>','<i18n:message key="mcs.status_vendor_repair" />');">
		    <span class="vendor">&nbsp;<i18n:message key="mcs.status_vendor_repair" />&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>
<%} else {%>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.GENERAL_SCRAP_LEADER%>','<%=ConstantsMcs.GENERAL_SCRAP_SHIP%>','<i18n:message key="mcs.status_general_scrap" />');">
		    <span class="generalScrap">&nbsp;<i18n:message key="mcs.status_general_scrap" />&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>
<%}%>

</form>

<script language="javascript">
	if('<%=request.getParameter("oldStatus")%>'=='<%=ConstantsMcs.GENERAL_SCRAP_LEADER%>'){
	document.getElementById("type1").checked=true;
	}else if('<%=request.getParameter("oldStatus")%>'=='<%=ConstantsMcs.VENDOR_REPAIR_LEADER%>'){
	document.getElementById("type2").checked=true;
	}
</script>