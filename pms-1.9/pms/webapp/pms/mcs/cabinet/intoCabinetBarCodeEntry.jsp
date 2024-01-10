<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>


<!-- ##################################### yui page script ################################ -->
<script language="javascript">
Ext.onReady(function(){
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
});
</script>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function queryStoReqList() {
	doSubmit("<ofbiz:url>/intoCabinetBarCodeEntry</ofbiz:url>");
}

function intoCabinet() {
	doSubmit("<ofbiz:url>/intoCabinetBarCode</ofbiz:url>");
}

function doSubmit(url) {
	loading();
	document.stoReqEntryForm.action = url;
	document.stoReqEntryForm.submit();
}

var sTime;
var eTime;
function doCopy(obj, barcodePrefix) {
	eTime = new Date();
	if (eTime.getTime()-sTime.getTime() > 30) {
		obj.value = "";
		alert("请使用扫描枪操作");
	} else if (obj.value == barcodePrefix){
	    //alert('请核对扫描条码是否符合领用记录!');
	    obj.value = obj.value + ",";
	}
}

function checkScanValue(objInput, barcodePrefix) {// onblur校验
    var barcode = objInput.value;
    if (barcode != barcodePrefix) {
        alert('请核对扫描条码是否符合系统记录!');
        objInput.value = "";
        //objInput.focus();
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

function myGetSelectIdx(){//---------取得选取值---------------
	var i;
	var varIdx="'";
    for(i=0;i<document.getElementsByName("choose").length;i++){
			if (document.getElementsByName("choose")[i].checked){
				varIdx = varIdx +document.getElementsByName("ReqCard")[i].value+ "','";
		    }
	}
	if (varIdx.length>0)
		varIdx = varIdx.substring(0,varIdx.length-2);
	return varIdx;
}
-->
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form method="post" name="stoReqEntryForm">

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>分类条件</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
		<tr>
    		<td class="tabletitle" width="12%"><i18n:message key="mcs.material_group" /></td>
			<td class="tablelist"><input type="text" size="40" name="mtrGrpSelect" autocomplete="off"/></td>
	    </tr>
	  </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:queryStoReqList();"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<ofbiz:if name="mtrMaintainList" size="0">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>等待维护基本资料的物料列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
      		<td class="en11pxb">物料描述</td>
      		<!--<td class="en11pxb">物料组</td>-->
    	</tr>

	    <ofbiz:iterator name="cust" property="mtrMaintainList">
	        <tr class="tablelist">
	        	<td class="en11px">
	        	    <a href="<ofbiz:url>/defineMaterial?materialIndex=<ofbiz:inputvalue entityAttr="cust" field="materialIndex" tryEntityAttr="true" /></ofbiz:url>">
	        	        <ofbiz:entityfield attribute="cust" field="mtrNum" />
	        	    </a>
	        	</td>
	        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="mtrDesc" /></td>
	        	<!--<td class="en11px"><ofbiz:entityfield attribute="cust" field="mtrGrp" /></td>-->
	        </tr>
	   	</ofbiz:iterator>

      </table>
  </fieldset></td>
</tr>
</table>
</ofbiz:if>

<br/>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>已领物料列表</legend>
      <table id="objTable" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      		<td class="en11pxb">
      		    选择
      		    <input type="checkbox" name="checkBoxSelectAll" id="checkBoxSelectAll"
      		    class="hand" title="全选/取消选择"  style="display:none;"
      		    <ofbiz:if name="stoReqList" size="0">
      		        onClick="selectAllCheckBox(objTable, objTr, objCheckbox, this.checked);"
      		    </ofbiz:if>
      		    >
      		</td>

      		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.qty" /></td>

            <ofbiz:if name="mtrGrp" value="<%=ConstantsMcs.PHOTORESIST%>">
      		    <td class="en11pxb"><i18n:message key="mcs.barcode" /></td>
      		</ofbiz:if>

      		<td class="en11pxb"><i18n:message key="mcs.scan_barcode" /></td>

      		<td class="en11pxb"><i18n:message key="mcs.doc_time" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>
    	</tr>

    	<ofbiz:if name="stoReqList">
        	<ofbiz:iterator name="cust" property="stoReqList" type="java.util.Map">
    	        <tr class="tablelist" id="objTr" style="cursor:hand"
    	        onClick="javascript:selectOneCheckBox(objTable, objTr, objCheckbox, this.rowIndex, checkBoxSelectAll);">
    	        	<td class="en11px">
    	        	    <input type="checkbox" name="objCheckbox" id="objCheckbox"
    	        	    value="<ofbiz:entityfield attribute="cust" field="MATERIAL_STO_REQ_INDEX" />"
    	        	    onClick="this.checked=!this.checked;"
    	        	    >
    	        	</td>

    	        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_NUM" /></td>
    	        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC" /></td>
    	        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="VENDOR_BATCH_NUM" /></td>
    	        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="QTY" /></td>

    	        	<ofbiz:if name="mtrGrp" value="<%=ConstantsMcs.PHOTORESIST%>">
        	        	<td class="en11px">
        	        	    <ofbiz:entityfield attribute="cust" field="VENDOR_MTR_NUM" />-<ofbiz:entityfield attribute="cust" field="VENDOR_BATCH_NUM_BARCODE" />
        	        	</td>
    	        	</ofbiz:if>

    	        	<td class="en11px">
    	        	    <!-- 扫描条码 -->
          		        <input type="text" class="input" size="10" name="barcode" id="barcode" value=""
          		         onKeyDown="sTime=new Date()" onKeyUp="doCopy(this, '<ofbiz:entityfield attribute="cust" field="VENDOR_MTR_NUM" />-<ofbiz:entityfield attribute="cust" field="VENDOR_BATCH_NUM_BARCODE" />')"
              		         <ofbiz:if name="mtrGrp" value="<%=ConstantsMcs.PHOTORESIST%>">
              		            onchange="checkScanValue(this.value, '<ofbiz:entityfield attribute="cust" field="VENDOR_MTR_NUM" />-<ofbiz:entityfield attribute="cust" field="VENDOR_BATCH_NUM_BARCODE" />')"
              		         </ofbiz:if>
          		        />

          		        <input type="hidden" name="barcodePrefix" value="<ofbiz:entityfield attribute="cust" field="VENDOR_MTR_NUM" />-<ofbiz:entityfield attribute="cust" field="VENDOR_BATCH_NUM_BARCODE" />" disabled="true">
          		    </td>

    	        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="DOC_TIME" /></td>
    	        	<td class="en11px">
    	        	    <ofbiz:format type="date">
    	        	        <ofbiz:entityfield attribute="cust" field="SHELF_LIFE_EXPIRATION_DATE" />
    	        	    </ofbiz:format>
    	        	</td>
    	        </tr>
    	   	</ofbiz:iterator>
        </ofbiz:if>

      </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="javascript:intoCabinet();">
		    <span class="cabinet">&nbsp;<i18n:message key="mcs.status_cabinet_new" />&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>

</form>