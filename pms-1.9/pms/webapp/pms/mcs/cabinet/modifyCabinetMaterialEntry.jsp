<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import ="com.csmc.pms.webapp.util.SessionNames"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<%
	String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
    if ("".equals(mtrGrp)) {
	    //默认选择化学用品
	    mtrGrp = ConstantsMcs.CHEMICAL;
	}
%>

<!-- ##################################### yui page script ################################ -->
<script language="javascript">
var useMaterialById = function() {

	var mtrGrpDS, mtrGrpCom;
	var mtrNumDS, mtrNumCom;

	return {
		init : function() {
			this.createDataStore();
		    this.createCombox();
		    this.initLoad();
		},

		//设置数据源
		createDataStore : function() {

			mtrGrpDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonMtrGrp</ofbiz:url>?useByBarcode=Y'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'mtrGrp'}, {name: 'mtrGrpDesc'}]))
		    });

		    mtrNumDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonMtrNum</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'mtrNum'}, {name: 'mtrDesc'}]))
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
			    allowBlank: false
		    });
		    mtrGrpCom.applyTo('mtrGrpSelect');
		    mtrGrpCom.on('select', this.loadMtrNum);

		    //设置物料号
		    mtrNumCom = new Ext.form.ComboBox({
			    store: mtrNumDS,
			    displayField:'mtrNum',
			    valueField:'mtrNum',
			    hiddenName:'mtrNum',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all',
			    allowBlank: true
		    });
		    mtrNumCom.applyTo('mtrNumSelect');
		    mtrNumCom.on('select', queryMaterial);
		},

		initLoad : function() {
			var mtrGrp = '<%=mtrGrp%>';
	    	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(mtrGrp); }});

	    	var mtrNum = '<%=UtilFormatOut.checkNull(request.getParameter("mtrNum"))%>';
	    	mtrNumDS.load({params:{mtrGrp:mtrGrp},callback:function(){ mtrNumCom.setValue(mtrNum); }});
		},

		loadMtrNum : function() {
			var mtrGrp = mtrGrpCom.getValue();
			var mtrNum  = '';

	 		mtrNumDS.load({params:{mtrGrp:mtrGrp},callback:function(){ mtrNumCom.setValue(mtrNum); }});
	 	}
	}
}();

Ext.EventManager.onDocumentReady(useMaterialById.init, useMaterialById, true);
</script>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function queryMaterial() {
    if (Ext.getDom('mtrGrp').value == "") {
        Ext.MessageBox.alert('警告', '请选择物料组');
		return;
    }

    if (Ext.getDom('mtrNum').value == "") {
        Ext.MessageBox.alert('警告', '请选择物料号');
		return;
    }

    doSubmit("<ofbiz:url>/modifyCabinetMaterialEntry</ofbiz:url>");
}

function modifyCabinetMaterial() {
	if (checkForm()) {
		doSubmit("<ofbiz:url>/modifyCabinetMaterial</ofbiz:url>");
	}
}

function checkForm() {
    if (totalSelected == 0) {
        Ext.MessageBox.alert('警告', '请选择需要操作的物料');
        return false;
    }

	return true;
}

function doSubmit(url) {
	loading();
	document.myForm.action = url;
	document.myForm.submit();
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

	if (totalCount > 1) {
	    var objNote = document.getElementsByName("note")[theIdx];
	    var objUnfrozenTransTime = document.getElementsByName("unfrozenTransTime")[theIdx];
	} else {
	    var objNote = document.getElementById("note");
	    var objUnfrozenTransTime = document.getElementById("unfrozenTransTime");
	}

	obj.checked = !obj.checked;//CheckBox添加按行选择功能
	if (obj.checked) {
		varTR.className = "tablelistchecked";
		totalSelected++;

		objNote.disabled = false;
		objUnfrozenTransTime.disabled = false;
        objUnfrozenTransTime.focus();
	} else {
		varTR.className = "tablelist";
		totalSelected--;

		objNote.disabled = true;
		objUnfrozenTransTime.disabled = true;
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

			document.getElementsByName("note")[i].disabled = !isChecked;
			document.getElementsByName("unfrozenTransTime")[i].disabled = !isChecked;
		}
	} else {// only one Checkbox
	    objCheckbox.checked = isChecked;
		objTr.className = isChecked ? "tablelistchecked" : "tablelist";

		document.getElementById("note").disabled = !isChecked;
		document.getElementById("unfrozenTransTime").disabled = !isChecked;
	}

	totalSelected = isChecked ? totalCount : 0;
}

-->
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form method="post" name="myForm">

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

			<td class="tabletitle" width="12%">
			    <i18n:message key="mcs.material_number" />
			    <i18n:message key="mcs.data_required_red_star" />
			</td>
			<td class="tablelist" width="30%">
			    <input type="text" size="40" name="mtrNumSelect" autocomplete="off"/>
			</td>
	    </tr>
	  </table>
  </fieldset></td>
</tr>
</table>

<br />

<ofbiz:if name="cabinetMaterialList" size="0">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>暂存区新进物料列表</legend>
      <table id="objTable" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      		<td class="en11pxb">
      		    <input type="checkbox" name="checkBoxSelectAll" id="checkBoxSelectAll"
      		    class="hand" title="全选/取消选择"
      		    <ofbiz:if name="cabinetMaterialList" size="0">
      		        onClick="selectAllCheckBox(objTable, objTr, objCheckbox, this.checked);"
      		    </ofbiz:if>
      		    >
      		</td>

      		<td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.doc_time" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>

            <td class="en11pxb"><i18n:message key="mcs.index_alias_name" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.unfrozen_trans_time" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.operator" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.note" /></td>
    	</tr>

    	<ofbiz:iterator name="cust" property="cabinetMaterialList" type="java.util.Map">
	        <tr class="tablelist" id="objTr" style="cursor:hand"
	        onClick="javascript:selectOneCheckBox(objTable, objTr, objCheckbox, this.rowIndex, checkBoxSelectAll);">
	        	<td class="en11px">
	        	    <input type="checkbox" name="objCheckbox" id="objCheckbox"
	        	    value="<ofbiz:entityfield attribute="cust" field="MATERIAL_STATUS_INDEX" />"
	        	    onClick="this.checked=!this.checked;"
	        	    >
	        	</td>

	        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="VENDOR_BATCH_NUM" /></td>
	        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_NUM" /></td>
	        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC" /></td>
	        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="DOC_TIME" /></td>
	        	<td class="en11px">
	        	    <ofbiz:format type="date">
	        	        <ofbiz:entityfield attribute="cust" field="SHELF_LIFE_EXPIRATION_DATE" />
	        	    </ofbiz:format>
	        	</td>

	        	<td class="en11px">
	        	    <a href="javascript:viewStatusHist('<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_STATUS_INDEX" tryEntityAttr="true" />')">
	        	        <ofbiz:inputvalue entityAttr="cust" field="ALIAS_NAME" tryEntityAttr="true" />
	        	    </a>
	        	</td>

	        	<td class="en11px">
	        	    <input type="text" class="input" size="20" name="unfrozenTransTime" id="unfrozenTransTime" value="<ofbiz:inputvalue entityAttr="cust" field="UNFROZEN_TRANS_TIME" />" disabled="true" />
	            </td>

    	        <td class="en11px">
    	            <ofbiz:inputvalue entityAttr="cust" field="UNFROZEN_TRANS_BY" tryEntityAttr="true" />
    	        </td>

    	        <td class="en11px">
	        	    <input type="text" class="input" size="10" name="note" id="note" value="" disabled="true" onClick="this.focus()"/>
      		    </td>
	        </tr>
	   	</ofbiz:iterator>

      </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="javascript:modifyCabinetMaterial();">
		    <span class="cabinet">&nbsp;修改&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>

</ofbiz:if>

</form>

<br>
<p>
	<font color="#FF0000" face="黑体" size="-1">
		Tips :

		<br>
		&nbsp;1. 恒温开始时间格式为 YYYY-MM-DD HH:MI:SS，如“2011-09-20 08:22:00”。

		<br>
		&nbsp;2. 选中一行后，按“Tab”键输入备注。
	</font>
</p>