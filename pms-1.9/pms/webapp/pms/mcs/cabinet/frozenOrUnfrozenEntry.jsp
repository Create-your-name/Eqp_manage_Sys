<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<%
    String opType = UtilFormatOut.checkNull(request.getParameter("opType"));
    if ("".equals(opType)) {
	    //默认选择恒温操作
	    opType = "Unfrozen";
	}

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

    doSubmit("<ofbiz:url>/frozenOrUnfrozenEntry</ofbiz:url>");
}

function frozenOrUnfrozen() {
	if (checkForm()) {
		doSubmit("<ofbiz:url>/frozenOrUnfrozen</ofbiz:url>");
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
	document.useForm.action = url;
	document.useForm.submit();
}

function viewStatusHist(materialStatusIndex) {
    var url = "<ofbiz:url>/queryMaterialStatusHist</ofbiz:url>?materialStatusIndex=" + materialStatusIndex;
    var result = window.showModalDialog(url, window, "dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");
}

// check input barcode
var sTime;
var eTime;
function doScan(obj) {
	eTime = new Date();
	if (eTime.getTime()-sTime.getTime() > 300) {
		obj.value = "";
		alert("请使用扫描枪操作");
	}
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

    // comment next row to cancal checkbox click function
	//obj.checked = !obj.checked;//CheckBox添加按行选择功能

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

// 条码扫描js, 选择与取消选择
function selectBarcode(objTable, objTr, objCheckbox, formActionType){//----------选中扫描行---------
	if (objTable == null || objTr == null || objCheckbox == null) {
		return;
	}

	totalCount = objTable.rows.length - 1;
	if (totalCount == 0) {
		return;
    }

    var i, objBarcode, isChecked;
    objBarcode = Ext.getDom(formActionType+'BarCode');

	if (objCheckbox.length > 1) {
		for(i=0; i < objCheckbox.length; i++){
		    if (objBarcode.value == document.getElementsByName(formActionType+"AliasName")[i].value) {
		        isChecked = !objCheckbox[i].checked;
    		    objCheckbox[i].checked = isChecked;
    			objTr[i].className = isChecked ? "tablelistchecked" : "tablelist";
    			objBarcode.value="";

    			totalSelected = isChecked ? (totalSelected + 1) : (totalSelected - 1);
		    }
		}
	} else {// only one Checkbox
	    if (objBarcode.value == document.getElementById(formActionType+"AliasName").value) {
	        isChecked = !objCheckbox.checked;
    	    objCheckbox.checked = isChecked;
    		objTr.className = isChecked ? "tablelistchecked" : "tablelist";
    		objBarcode.value="";

    		totalSelected = isChecked ? (totalSelected + 1) : (totalSelected - 1);
    	}
	}
}

-->
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form method="post" name="useForm">

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
			    <input type="radio" id="opType" name="opType" value="Frozen" <%if ("Frozen".equals(opType)) {%>checked<%}%>>放回冰箱
			    &nbsp;&nbsp;&nbsp;
			    <input type="radio" id="opType" name="opType" value="Unfrozen" <%if ("Unfrozen".equals(opType)) {%>checked<%}%>>恒温
			    &nbsp;&nbsp;&nbsp;
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
      <legend>
          暂存区物料列表 扫描条码：
          <input type="text" class="input" size="40" name="useBarcode" id="offBarcode" value="" onPropertyChange="selectBarcode(objTableCabinet, objTrCabinet, objCheckboxCabinet, 'use');" onKeyDown="sTime=new Date()" onKeyUp="doScan(this)">
      </legend>
      <table id="objTableCabinet" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      		<td class="en11pxb">
      		    选择
      		    <input type="checkbox" name="checkBoxSelectAllCabinet" id="checkBoxSelectAllCabinet"
      		    class="hand" title="全选/取消选择" style="display:none;"
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
      		<td class="en11pxb"><i18n:message key="mcs.unfrozen_time" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.frozen" /></td>
    	</tr>

    	<ofbiz:iterator name="cust" property="frozenOrUnfrozenList" type="java.util.Map">
	        <tr class="tablelist" id="objTrCabinet">
	        	<td class="en11px">
	        	    <input type="checkbox" name="objCheckboxCabinet" id="objCheckboxCabinet"
	        	    value="<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_STATUS_INDEX" tryEntityAttr="true"/>"
	        	    onClick="this.checked=!this.checked;"
	        	    >
	        	</td>
	        	<td class="en11px">
	        	    <a href="javascript:viewStatusHist('<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_STATUS_INDEX" tryEntityAttr="true" />')">
	        	        <ofbiz:inputvalue entityAttr="cust" field="ALIAS_NAME" tryEntityAttr="true" />
	        	    </a>

	        	    <input type="hidden" name="useAliasName" value="<ofbiz:inputvalue entityAttr="cust" field="ALIAS_NAME" tryEntityAttr="true" />">
	        	</td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="VENDOR_BATCH_NUM" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MTR_NUM" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MTR_DESC" tryEntityAttr="true" /></td>

	            <td class="en11px">
	        	    <ofbiz:format type="date">
	        	        <ofbiz:inputvalue entityAttr="cust" field="SHELF_LIFE_EXPIRATION_DATE" tryEntityAttr="true" />
	        	    </ofbiz:format>
	        	</td>
	        	<td class="en11px">
	        	    <ofbiz:inputvalue entityAttr="cust" field="MRB_DATE" tryEntityAttr="true" />
	        	</td>

	        	<td class="en11px">
	        	    <ofbiz:inputvalue entityAttr="cust" field="UNFROZEN_TIME" tryEntityAttr="true" />
	        	</td>

	        	<td class="en11px">
	        	    <ofbiz:inputvalue entityAttr="cust" field="FROZEN" tryEntityAttr="true" />
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
		<li><a class="button-text" href="javascript:frozenOrUnfrozen();">
		    <span>&nbsp;确定&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>

</form>