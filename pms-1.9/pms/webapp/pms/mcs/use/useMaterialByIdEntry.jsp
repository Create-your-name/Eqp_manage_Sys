<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<%
    String eqpId = UtilFormatOut.checkNull(request.getParameter("eqpId"));
    String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
    if ("".equals(mtrGrp)) {
        //默认选择靶材
        mtrGrp = ConstantsMcs.TARGET;
    }
%>

<!-- ##################################### yui page script ################################ -->
<script language="javascript">
var useMaterialById = function() {

	var eqpIdDS, eqpIdCom;
	var mtrGrpDS, mtrGrpCom;

	return {
		init : function() {
			this.createDataStore();
		    this.createCombox();
		    this.initLoad();
		},

		//设置数据源
		createDataStore : function() {

		    eqpIdDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentByDeptIndex</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
		    });

			mtrGrpDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonMtrGrp</ofbiz:url>?useById=Y'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'mtrGrp'}, {name: 'mtrGrpDesc'}]))
		    });
		},

		createCombox : function() {

		    //设置eqpId
			eqpIdCom = new Ext.form.ComboBox({
			    store: eqpIdDS,
			    displayField:'equipmentId',
			    valueField:'equipmentId',
			    hiddenName:'eqpId',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all',
			    allowBlank: false
		    });
		    eqpIdCom.applyTo('eqpIdSelect');
		    eqpIdCom.on('select', queryEqpMaterial);

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
			    allowBlank: false
		    });
		    mtrGrpCom.applyTo('mtrGrpSelect');
		},

		initLoad : function() {
			var eqpId = '<%=eqpId%>';
			var mtrGrp = '<%=mtrGrp%>';
			eqpIdDS.load({params:{deptIndex:''}, callback:function(){ eqpIdCom.setValue(eqpId); }});
	    	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(mtrGrp); }});
		}
	}
}();

Ext.EventManager.onDocumentReady(useMaterialById.init, useMaterialById, true);
</script>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function queryEqpMaterial() {
    doSubmit("<ofbiz:url>/useMaterialByIdEntry</ofbiz:url>");
}

function changeMaterialStatus(newStatus, newStatusDesc) {
    Ext.MessageBox.confirm('操作确认', '您确信选择 ' + newStatusDesc + ' 吗？',function result(value){
        if(value=="yes"){
			if (checkForm()) {
				doSubmit("<ofbiz:url>/useMaterialById</ofbiz:url>?newStatus=" + newStatus);
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

function checkCondition() {
	var msg = "";
    if (Ext.getDom('eqpId').value == "") {
		msg = '请选择设备!';
		return msg;
	}

	if (Ext.getDom('mtrGrp').value == "") {
		msg = '请选择物料组!';
		return msg;
	}

	return msg;
}

function doSubmit(url) {
    var msg = checkCondition();
	if(msg.length > 0) {
		Ext.MessageBox.alert('警告', msg);
		return;
	}

	loading();
	document.useForm.action = url;
	document.useForm.submit();
}

function viewStatusHist(materialStatusIndex) {
    var url = "<ofbiz:url>/queryMaterialStatusHist</ofbiz:url>?materialStatusIndex=" + materialStatusIndex;
    var result = window.showModalDialog(url, window, "dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");
}
</script>

<!-- ##################################### checkbox script ################################ -->
<script language="javascript">
<!--
var totalSelected = 0;                       //选取的总数;
var totalCount = 0;                          //资料的总数;

function selectOneCheckBox(objTable, objTr, objCheckbox, rowIndex, checkBoxSelectAll) {
    //--------------rowIndex=1,2,3... theIdx=0,1,2,3......--------------
    totalCount = objTable.rows.length - 1;
	var theIdx = rowIndex - 1;
	var obj = (objCheckbox.length > 1) ? objCheckbox[theIdx] : objCheckbox;
	var varTR = (objTr.length > 1) ? objTr[theIdx] : objTr;

	obj.checked = !obj.checked;//CheckBox添加按行选择功能
	if (obj.checked) {
		varTR.className = "tablelistchecked";
		totalSelected++;
	} else {
		varTR.className = "tablelist";
		totalSelected--;
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

-->
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form method="post" name="useForm">
<input type="hidden" name="eqpIdSubmit" value="<%=eqpId%>">

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>分类条件</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
		<tr>
    		<td class="tabletitle" width="12%">
    		    <i18n:message key="mcs.equipment" />
    		    <i18n:message key="mcs.data_required_red_star" />
    		</td>
			<td class="tablelist" width="30%" colspan="3">
			    <input type="text" size="40" name="eqpIdSelect" autocomplete="off"/>
			</td>
	    </tr>

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
		<li><a class="button-text" href="javascript:queryEqpMaterial();"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>设备在用物料列表</legend>
      <table id="objTableEqpMtr" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      	    <td class="en11pxb">
      		    <input type="checkbox" name="checkBoxSelectAllEqpMtr" id="checkBoxSelectAllEqpMtr"
      		    class="hand" title="全选/取消选择"
      		    <ofbiz:if name="eqpMaterialList" size="0">
      		        onClick="selectAllCheckBox(objTableEqpMtr, objTrEqpMtr, objCheckboxEqpMtr, this.checked);"
      		    </ofbiz:if>
      		    >
      		</td>
      	    <td class="en11pxb"><i18n:message key="mcs.index_alias_name" /></td>
      	    <td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.mrb_date" /></td>
    	</tr>

	    <ofbiz:iterator name="cust" property="eqpMaterialList">
	        <tr class="tablelist" id="objTrEqpMtr" style="cursor:hand"
	        onClick="javascript:selectOneCheckBox(objTableEqpMtr, objTrEqpMtr, objCheckboxEqpMtr, this.rowIndex, checkBoxSelectAllEqpMtr);">
	            <td class="en11px">
	        	    <input type="checkbox" name="objCheckboxEqpMtr" id="objCheckboxEqpMtr"
	        	    value="<ofbiz:inputvalue entityAttr="cust" field="materialStatusIndex" tryEntityAttr="true" />"
	        	    onClick="this.checked=!this.checked;"
	        	    >
	        	</td>

	        	<td class="en11px">
	        	    <a href="javascript:viewStatusHist('<ofbiz:inputvalue entityAttr="cust" field="materialStatusIndex" tryEntityAttr="true" />')">
	        	        <ofbiz:inputvalue entityAttr="cust" field="aliasName" tryEntityAttr="true" />
	        	    </a>
	        	</td>

	            <td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="vendorBatchNum" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="mtrNum" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="mtrDesc" tryEntityAttr="true" /></td>

	        	<td class="en11px">
	        	    <ofbiz:format type="date">
	        	        <ofbiz:inputvalue entityAttr="cust" field="shelfLifeExpirationDate" tryEntityAttr="true" />
	        	    </ofbiz:format>
	        	</td>
	        	<td class="en11px">
	        	    <ofbiz:inputvalue entityAttr="cust" field="mrbDate" tryEntityAttr="true" />
	        	</td>
	        </tr>
	   	</ofbiz:iterator>

      </table>
  </fieldset></td>
</tr>
</table>

<%@include file="nextStatusButtons.jsp"%>

<br/>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>暂存区物料列表</legend>
      <table id="objTableCabinet" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      		<td class="en11pxb">
      		    <input type="checkbox" name="checkBoxSelectAllCabinet" id="checkBoxSelectAllCabinet"
      		    class="hand" title="全选/取消选择"
      		    <ofbiz:if name="cabinetList" size="0">
      		        onClick="selectAllCheckBox(objTableCabinet, objTrCabinet, objCheckboxCabinet, this.checked);"
      		    </ofbiz:if>
      		    >
      		</td>
      		<td class="en11pxb"><i18n:message key="mcs.index_alias_name" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.mrb_date" /></td>
    	</tr>

    	<ofbiz:iterator name="cust" property="cabinetList">
	        <tr class="tablelist"
	        id="objTrCabinet" style="cursor:hand"
	        onClick="javascript:selectOneCheckBox(objTableCabinet, objTrCabinet, objCheckboxCabinet, this.rowIndex, checkBoxSelectAllCabinet);">
	        	<td class="en11px">
	        	    <input type="checkbox" name="objCheckboxCabinet" id="objCheckboxCabinet"
	        	    value="<ofbiz:inputvalue entityAttr="cust" field="materialStatusIndex" tryEntityAttr="true"/>"
	        	    onClick="this.checked=!this.checked;"
	        	    >
	        	</td>

	        	<td class="en11px">
	        	    <a href="javascript:viewStatusHist('<ofbiz:inputvalue entityAttr="cust" field="materialStatusIndex" tryEntityAttr="true" />')">
	        	        <ofbiz:inputvalue entityAttr="cust" field="aliasName" tryEntityAttr="true" />
	        	    </a>
	        	</td>

	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="vendorBatchNum" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="mtrNum" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="mtrDesc" tryEntityAttr="true" /></td>

	            <td class="en11px">
	        	    <ofbiz:format type="date">
	        	        <ofbiz:inputvalue entityAttr="cust" field="shelfLifeExpirationDate" tryEntityAttr="true" />
	        	    </ofbiz:format>
	        	</td>
	        	<td class="en11px">
	        	    <ofbiz:inputvalue entityAttr="cust" field="mrbDate" tryEntityAttr="true" />
	        	</td>
	        </tr>
	   	</ofbiz:iterator>


      </table>
  </fieldset></td>
</tr>
</table>

<%@include file="usingButton.jsp"%>

</form>