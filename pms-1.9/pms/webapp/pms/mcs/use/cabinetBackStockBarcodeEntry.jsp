<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<%
    String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
%>

<!-- ##################################### yui page script ################################ -->
<script language="javascript">
var useMaterialByQty = function() {

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
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonMtrGrp</ofbiz:url>?enableBackStock=Y'}),
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
			    allowBlank: false
		    });
		    mtrGrpCom.applyTo('mtrGrpSelect');
		},

		initLoad : function() {
			var mtrGrp = '<%=mtrGrp%>';
	    	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(mtrGrp); }});
		}
	}
}();

Ext.EventManager.onDocumentReady(useMaterialByQty.init, useMaterialByQty, true);
</script>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function queryNewMaterial() {
    doSubmit("<ofbiz:url>/cabinetBackStockBarcodeEntry</ofbiz:url>");
}

function changeMaterialStatus(newStatus, newStatusDesc) {
    Ext.MessageBox.confirm('操作确认', '您确信选择 ' + newStatusDesc + ' 吗？',function result(value){
        if(value=="yes"){
			if (checkForm()) {
        	    doSubmit("<ofbiz:url>/cabinetBackStockBarcode</ofbiz:url>?oldStatus=<%=ConstantsMcs.CABINET_NEW%>&newStatus=" + newStatus);
        	}
		}else{
			return;
		}
    });
}

function checkForm() {
    if (totalSelected == 0) {
        Ext.MessageBox.alert('警告', '请选择需要退库的物料');
        return false;
    }

	return true;
}

function checkCondition() {
	var msg = "";
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
<%--<input type="hidden" name="eqpIdSubmit" value="">--%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>分类条件</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">

	    <tr>
    		<td class="tabletitle" width="6%">
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
		<li><a class="button-text" href="javascript:queryNewMaterial();"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<br/>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>
          暂存区 超有效期新领物料列表 扫描条码：
          <input type="text" class="input" size="40" name="useBarcode" id="offBarcode" value="" onPropertyChange="selectBarcode(objTableCabinet, objTrCabinet, objCheckboxSI, 'use');" onKeyDown="sTime=new Date()" onKeyUp="doScan(this)">
      </legend>
      <table id="objTableCabinet" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      		<td class="en11pxb">
      		    选择
      		    <input type="checkbox" name="checkBoxSelectAllCabinet" id="checkBoxSelectAllCabinet"
      		    class="hand" title="全选/取消选择" style="display:none;"
      		    <ofbiz:if name="cabinetList" size="0">
      		        onClick="selectAllCheckBox(objTableCabinet, objTrCabinet, objCheckboxSI, this.checked);"
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

    	<ofbiz:iterator name="cust" property="cabinetList" type="java.util.Map">
	        <tr class="tablelist" id="objTrCabinet" style="cursor:hand">
	        	<td class="en11px">
	        	    <input type="checkbox" name="objCheckboxSI" id="objCheckboxSI"
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
	        	    <ofbiz:inputvalue entityAttr="cust" field="SHELF_LIFE_EXPIRATION_DATE" tryEntityAttr="true" />
	        	</td>
	        	<td class="en11px">
	        	    <ofbiz:inputvalue entityAttr="cust" field="MRB_DATE" tryEntityAttr="true" />
	        	</td>
	        </tr>
	   	</ofbiz:iterator>


      </table>
  </fieldset></td>
</tr>
</table>

<ofbiz:if name="mtrGrp">
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.NEW_BACK_STOCK%>', '<i18n:message key="mcs.status_new_back_stock" />');">
		    <span>&nbsp;<i18n:message key="mcs.status_new_back_stock" />&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>
</ofbiz:if>

<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>7天内退库状态清单</legend>
      <table id="useTable" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      	    <td class="en11pxb">NO.</td>
      		<td class="en11pxb"><i18n:message key="mcs.index_alias_name" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.mrb_date" /></td>
			<td class="en11pxb"><i18n:message key="mcs.operation_time" /></td>
    	</tr>

    	<%int i = 1;%>
    	<ofbiz:iterator name="cust" property="backStockList" type="java.util.Map">
	        <tr class="tablelist" >
	            <td class="en11px"><%=i++%></td>
	        	<td class="en11px">
	        	    <a href="javascript:viewStatusHist('<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_STATUS_INDEX" tryEntityAttr="true" />')">
	        	        <ofbiz:inputvalue entityAttr="cust" field="ALIAS_NAME" tryEntityAttr="true" />
	        	    </a>
	        	</td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="VENDOR_BATCH_NUM" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MTR_NUM" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MTR_DESC" tryEntityAttr="true" /></td>

	        	<td class="en11px">
	        	    <ofbiz:inputvalue entityAttr="cust" field="SHELF_LIFE_EXPIRATION_DATE" tryEntityAttr="true" />
	        	</td>
	        	<td class="en11px">
                    <ofbiz:inputvalue entityAttr="cust" field="MRB_DATE" tryEntityAttr="true" />
	        	</td>
				<td class="en11px">
					<ofbiz:inputvalue entityAttr="cust" field="UPDATE_TIME" tryEntityAttr="true"/>
				</td>
	        </tr>
	   	</ofbiz:iterator>


      </table>
  </fieldset></td>
</tr>
</table>

</form>