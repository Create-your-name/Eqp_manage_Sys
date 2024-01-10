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
function queryEqpMaterial() {
    doSubmit("<ofbiz:url>/cabinetBackStockEntry</ofbiz:url>");
}

function changeMaterialStatus(newStatus, newStatusDesc) {
    Ext.MessageBox.confirm('操作确认', '您确信选择 ' + newStatusDesc + ' 吗？',function result(value){
        if(value=="yes"){
			if (checkForm()) {
        	    doSubmit("<ofbiz:url>/cabinetBackStock</ofbiz:url>?newStatus=" + newStatus);
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

function  onlyNum(obj){//---------------只能输入数字---------------------
	obj.value = obj.value.replace(/\D/g,"");
}

function checkQtyInput(objQtyInput, totalQty) {//onkeyup,onafterpaste 输入时立刻校验
    onlyNum(objQtyInput);

    var qty = objQtyInput.value;
    if (qty <= 0) {
        objQtyInput.value = "";
        objQtyInput.focus();
    } else if (qty > totalQty) {
        alert('输入数量大于已有数量，请重新输入');
        objQtyInput.value = "";
        objQtyInput.focus();
    }
}

function checkInputValue(objQtyInput) {// onblur校验
     var qty = objQtyInput.value;
    if (qty == "") {
        alert('请输入数量');
        objQtyInput.value = "";
        objQtyInput.focus();
    }
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
</script>

<!-- ##################################### checkbox script ################################ -->
<script language="javascript">
<!--
var totalSelected = 0;                       //选取的总数;
var totalCount = 0;                          //资料的总数;

function selectOneCheckBoxInput(formActionType, rowIndex) {
    //--------------rowIndex=1,2,3... theIdx=0,1,2,3......--------------
    var checkBoxSelectAll = document.getElementById(formActionType+"CheckBoxSelectAll");
	var objTable = document.getElementById(formActionType+"Table");
	totalCount = objTable.rows.length - 1;

	var theIdx = rowIndex - 1;

	if (totalCount > 1) {
	    var objCheckbox = document.getElementsByName(formActionType+"Checkbox")[theIdx];
	    var objTr = document.getElementsByName(formActionType+"Tr")[theIdx];
	    var objQty = document.getElementsByName(formActionType+"Qty")[theIdx];

	    var objTotalQty = document.getElementsByName(formActionType+"TotalQty")[theIdx];
	    var objVendorBatchNum = document.getElementsByName(formActionType+"VendorBatchNum")[theIdx];
	    var objMtrNum = document.getElementsByName(formActionType+"MtrNum")[theIdx];
	    var objShelfLifeExpirationDate = document.getElementsByName(formActionType+"ShelfLifeExpirationDate")[theIdx];
	    var objMrbDate = document.getElementsByName(formActionType+"MrbDate")[theIdx];
	} else {
	    var objCheckbox = document.getElementById(formActionType+"Checkbox");
	    var objTr = document.getElementById(formActionType+"Tr");
	    var objQty = document.getElementById(formActionType+"Qty");

        var objTotalQty = document.getElementById(formActionType+"TotalQty");
	    var objVendorBatchNum = document.getElementById(formActionType+"VendorBatchNum");
	    var objMtrNum = document.getElementById(formActionType+"MtrNum");
	    var objShelfLifeExpirationDate = document.getElementById(formActionType+"ShelfLifeExpirationDate");
	    var objMrbDate = document.getElementById(formActionType+"MrbDate");
	}

	objCheckbox.checked = !objCheckbox.checked;//CheckBox添加按行选择功能
	if (objCheckbox.checked) {
		objTr.className = "tablelistchecked";
		totalSelected++;

		objQty.disabled = false;
        objQty.focus();

        objTotalQty.disabled = false;
        objVendorBatchNum.disabled = false;
        objMtrNum.disabled = false;
        objShelfLifeExpirationDate.disabled = false;
        objMrbDate.disabled = false;
	} else {
		objTr.className = "tablelist";
		totalSelected--;

		objQty.disabled = true;
		objQty.value = "";

		objTotalQty.disabled = true;
		objVendorBatchNum.disabled = true;
        objMtrNum.disabled = true;
        objShelfLifeExpirationDate.disabled = true;
        objMrbDate.disabled = true;
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
		<li><a class="button-text" href="javascript:queryEqpMaterial();"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<br/>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>暂存区 新领物料列表</legend>
      <table id="useTable" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      		<td class="en11pxb">
      		    选择
      		    <input type="checkbox" name="useCheckBoxSelectAll" id="useCheckBoxSelectAll"
      		    class="hand" title="全选/取消选择" style="display:none;"
      		    <ofbiz:if name="cabinetList" size="0">
      		        onClick="selectAllCheckBox(useTable, useTr, useCheckbox, this.checked);"
      		    </ofbiz:if>
      		    >
      		</td>

      		<td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.qty" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.qty" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.mrb_date" /></td>
    	</tr>

    	<ofbiz:iterator name="cust" property="cabinetList" type="java.util.Map">
	        <tr class="tablelist" id="useTr" style="cursor:hand"
	        onClick="javascript:selectOneCheckBoxInput('use', this.rowIndex);">
	        	<td class="en11px">
	        	    <input type="checkbox" name="useCheckbox" id="useCheckbox"
	        	    onClick="this.checked=!this.checked;"
	        	    >
	        	</td>

	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="VENDOR_BATCH_NUM" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MTR_NUM" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MTR_DESC" tryEntityAttr="true" /></td>

	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="QTY" tryEntityAttr="true" /></td>

	        	<td class="en11px">
	        	    <!-- 更换数量 -->
      		        <input type="text" class="input" size="10" name="useQty" id="useQty" value=""
      		        disabled="true"
      		        onkeyup="checkQtyInput(this, <ofbiz:inputvalue entityAttr="cust" field="QTY" tryEntityAttr="true" />)"
      		        onafterpaste="checkQtyInput(this, <ofbiz:inputvalue entityAttr="cust" field="QTY" tryEntityAttr="true" />)"
      		        onblur="checkInputValue(this)"
      		        />

      		        <input type="hidden" name="useTotalQty" value="<ofbiz:inputvalue entityAttr="cust" field="QTY" tryEntityAttr="true" />" disabled="true">

      		        <input type="hidden" name="useVendorBatchNum" value="<ofbiz:inputvalue entityAttr="cust" field="VENDOR_BATCH_NUM" tryEntityAttr="true" />" disabled="true">

      		        <input type="hidden" name="useMtrNum" value="<ofbiz:inputvalue entityAttr="cust" field="MTR_NUM" tryEntityAttr="true" />" disabled="true">

      		        <input type="hidden" name="useShelfLifeExpirationDate" value="<ofbiz:inputvalue entityAttr="cust" field="SHELF_LIFE_EXPIRATION_DATE" tryEntityAttr="true" />" disabled="true">

      		        <input type="hidden" name="useMrbDate" value="<ofbiz:inputvalue entityAttr="cust" field="MRB_DATE" tryEntityAttr="true" />" disabled="true">
      		    </td>

	        	<td class="en11px">
	        	    <ofbiz:format type="date">
	        	        <ofbiz:inputvalue entityAttr="cust" field="SHELF_LIFE_EXPIRATION_DATE" tryEntityAttr="true" />
	        	    </ofbiz:format>
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