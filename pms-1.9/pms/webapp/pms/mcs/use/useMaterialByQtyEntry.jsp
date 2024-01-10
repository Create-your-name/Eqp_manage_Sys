<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<%
try {
    String eqpId = UtilFormatOut.checkNull(request.getParameter("eqpId"));
    String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));

    // mcs interface: pm/ts using parts
    String eventType = UtilFormatOut.checkNull(request.getParameter("eventType"));
    String eventIndex = UtilFormatOut.checkNull(request.getParameter("eventIndex"));
    String eqpIdPms = UtilFormatOut.checkNull(request.getParameter("eqpIdPms"));
    if (!eventType.equals("")) {
        if (!eqpIdPms.equals("")) {
            eqpId = eqpIdPms;   // pm第二次查询
        }
        eqpIdPms = eqpId;  // pm第一次查询
    }
%>

<head>
    <base target="_self" />
</head>

<!-- ##################################### yui page script ################################ -->
<script language="javascript">
var useMaterialByQty = function() {

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
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonMtrGrp</ofbiz:url>?useByQty=Y'}),
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
		    //eqpIdCom.on('select', queryEqpMaterial);

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

Ext.EventManager.onDocumentReady(useMaterialByQty.init, useMaterialByQty, true);
</script>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function queryEqpMaterial() {
    doSubmit("<ofbiz:url>/useMaterialByQtyEntry</ofbiz:url>");
}

function changeMaterialStatus(newStatus, newStatusDesc) {
    Ext.MessageBox.confirm('操作确认', '您确信选择 ' + newStatusDesc + ' 吗？',function result(value){
        if(value=="yes"){
			if (checkForm()) {
        	    doSubmit("<ofbiz:url>/useMaterialByQty</ofbiz:url>?newStatus=" + newStatus);
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
    if (Ext.getDom('eqpId').value == "") {
		msg = '请选择设备!';
		return msg;
	}

	if (Ext.getDom('mtrGrp').value == "") {
		msg = '请选择物料组!';
		return msg;
	}

	if (Ext.getDom('eventType').value != "" && Ext.getDom('eqpId').value != Ext.getDom('eqpIdPms').value) {
		Ext.getDom('eqpId').value = Ext.getDom('eqpIdPms').value;
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
        var objMaterialIndex = document.getElementsByName(formActionType+"MaterialIndex")[theIdx];
	    var objMtrNum = document.getElementsByName(formActionType+"MtrNum")[theIdx];
	    var objMtrDesc = document.getElementsByName(formActionType+"MtrDesc")[theIdx];
	    var objShelfLifeExpirationDate = document.getElementsByName(formActionType+"ShelfLifeExpirationDate")[theIdx];
	    var objMrbDate = document.getElementsByName(formActionType+"MrbDate")[theIdx];
	} else {
	    var objCheckbox = document.getElementById(formActionType+"Checkbox");
	    var objTr = document.getElementById(formActionType+"Tr");
	    var objQty = document.getElementById(formActionType+"Qty");

        var objTotalQty = document.getElementById(formActionType+"TotalQty");
	    var objVendorBatchNum = document.getElementById(formActionType+"VendorBatchNum");
	    var objMaterialIndex = document.getElementById(formActionType+"MaterialIndex");
	    var objMtrNum = document.getElementById(formActionType+"MtrNum");
	    var objMtrDesc = document.getElementById(formActionType+"MtrDesc");
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
        objMaterialIndex.disabled = false;
        objMtrNum.disabled = false;
        objMtrDesc.disabled = false;
        objShelfLifeExpirationDate.disabled = false;
        objMrbDate.disabled = false;
	} else {
		objTr.className = "tablelist";
		totalSelected--;

		objQty.disabled = true;
		objQty.value = "";

		objTotalQty.disabled = true;
		objVendorBatchNum.disabled = true;
		objMaterialIndex.disabled = true;
        objMtrNum.disabled = true;
        objMtrDesc.disabled = true;
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
<input type="hidden" name="eqpIdSubmit" value="<%=eqpId%>">

<input type="hidden" name="eventType" value="<%=eventType%>">
<input type="hidden" name="eventIndex" value="<%=eventIndex%>">
<input type="hidden" name="eqpIdPms" value="<%=eqpIdPms%>">

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
      <table id="offTable" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      	    <td class="en11pxb">
      	        选择
      		    <input type="checkbox" name="offCheckBoxSelectAll" id="offCheckBoxSelectAll"
      		    class="hand" title="全选/取消选择" style="display:none;"
      		    onClick="selectAllCheckBox(offTable, offTr, offCheckbox, this.checked);"
      		    >
      		</td>
      	    <td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.qty" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.off_qty" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.mrb_date" /></td>
      		<td class="en11pxb">最大已使用天数 / 备件寿命</td>
    	</tr>

	   	<%
    	List list = (List) request.getAttribute("eqpMaterialList");
    	if (list != null && list.size() > 0) {
        	for (Iterator it = list.iterator();it.hasNext();) {
    			Map map = (Map) it.next();
    			String maxUsePeriod = UtilFormatOut.checkNull((String) map.get("MAX_USE_PERIOD"));
    			String usableTimeLimit = UtilFormatOut.checkNull((String) map.get("USABLE_TIME_LIMIT"));

    			double dMaxUsePeriod = 0;
    			if (!maxUsePeriod.equals("")) {
    				dMaxUsePeriod = Double.parseDouble(maxUsePeriod);
    			}

    			double dUsableTimeLimit = 0;
    			if (!usableTimeLimit.equals("")) {
    				dUsableTimeLimit = Double.parseDouble(usableTimeLimit);
    			}
    			if (dUsableTimeLimit <= 0) {
    			    dUsableTimeLimit = 10000;
    			}

    			String alarmColor = "black";
    			if (dMaxUsePeriod+15 >= dUsableTimeLimit) {
    			    alarmColor = "blue";//还有15天寿命超期
    			}
    			if (dMaxUsePeriod >= dUsableTimeLimit) {
    			    alarmColor = "red";//已超期
    			}
    	%>

	        <tr class="tablelist" id="offTr" style="cursor:hand"
	        onClick="javascript:selectOneCheckBoxInput('off', this.rowIndex);"
	        style="color:<%=alarmColor%>"
	        >
	            <td>
	        	    <input type="checkbox" name="offCheckbox" id="offCheckbox"
	        	    onClick="this.checked=!this.checked;"
	        	    >
	        	</td>

	            <td><%=UtilFormatOut.checkNull((String) map.get("VENDOR_BATCH_NUM"))%></td>
	            <td><%=UtilFormatOut.checkNull((String) map.get("MTR_NUM"))%></td>
	            <td><%=UtilFormatOut.checkNull((String) map.get("MTR_DESC"))%></td>
	            <td><%=UtilFormatOut.checkNull((String) map.get("QTY"))%></td>

	        	<td>
	        	    <!-- 换下数量 -->
      		        <input type="text" class="input" size="10" name="offQty" id="offQty" value=""
      		        disabled="true"
      		        onkeyup="checkQtyInput(this, <%=UtilFormatOut.checkNull((String) map.get("QTY"))%>)"
      		        onafterpaste="checkQtyInput(this, <%=UtilFormatOut.checkNull((String) map.get("QTY"))%>)"
      		        onblur="checkInputValue(this)"
      		        />

      		        <input type="hidden" name="offTotalQty" value="<%=UtilFormatOut.checkNull((String) map.get("QTY"))%>" disabled="true">

      		        <input type="hidden" name="offVendorBatchNum" value="<%=UtilFormatOut.checkNull((String) map.get("VENDOR_BATCH_NUM"))%>" disabled="true">

      		        <input type="hidden" name="offMaterialIndex" value="<%=UtilFormatOut.checkNull((String) map.get("MATERIAL_INDEX"))%>" disabled="true">

      		        <input type="hidden" name="offMtrNum" value="<%=UtilFormatOut.checkNull((String) map.get("MTR_NUM"))%>" disabled="true">

                    <input type="hidden" name="offMtrDesc" value="<%=UtilFormatOut.checkNull((String) map.get("MTR_DESC"))%>" disabled="true">

      		        <input type="hidden" name="offShelfLifeExpirationDate" value="<ofbiz:format type="date"><%=UtilFormatOut.checkNull((String) map.get("SHELF_LIFE_EXPIRATION_DATE"))%></ofbiz:format>" disabled="true">

      		        <input type="hidden" name="offMrbDate" value="<%=UtilFormatOut.checkNull((String) map.get("MRB_DATE"))%>" disabled="true">
      		    </td>

	        	<td>
	        	    <ofbiz:format type="date">
	        	        <%=UtilFormatOut.checkNull((String) map.get("SHELF_LIFE_EXPIRATION_DATE"))%>
	        	    </ofbiz:format>
	        	</td>
	        	<td>
	        	    <%=UtilFormatOut.checkNull((String) map.get("MRB_DATE"))%>
	        	</td>

	        	<td>
	        	    <%=UtilFormatOut.checkNull((String) map.get("MAX_USE_PERIOD"))%>
	        	    /
	        	    <%=UtilFormatOut.checkNull((String) map.get("USABLE_TIME_LIMIT"))%>
	        	</td>
	        </tr>
	   	<%
	   	    }
	   	}
	   	%>

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
      		<td class="en11pxb"><i18n:message key="mcs.use_qty" /></td>
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
	        	    <!-- 换上数量 -->
      		        <input type="text" class="input" size="10" name="useQty" id="useQty" value=""
      		        disabled="true"
      		        onkeyup="checkQtyInput(this, <ofbiz:inputvalue entityAttr="cust" field="QTY" tryEntityAttr="true" />)"
      		        onafterpaste="checkQtyInput(this, <ofbiz:inputvalue entityAttr="cust" field="QTY" tryEntityAttr="true" />)"
      		        onblur="checkInputValue(this)"
      		        />

      		        <input type="hidden" name="useTotalQty" value="<ofbiz:inputvalue entityAttr="cust" field="QTY" tryEntityAttr="true" />" disabled="true">

      		        <input type="hidden" name="useVendorBatchNum" value="<ofbiz:inputvalue entityAttr="cust" field="VENDOR_BATCH_NUM" tryEntityAttr="true" />" disabled="true">

      		        <input type="hidden" name="useMaterialIndex" value="<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_INDEX" tryEntityAttr="true" />" disabled="true">

      		        <input type="hidden" name="useMtrNum" value="<ofbiz:inputvalue entityAttr="cust" field="MTR_NUM" tryEntityAttr="true" />" disabled="true">

      		        <input type="hidden" name="useMtrDesc" value="<ofbiz:inputvalue entityAttr="cust" field="MTR_DESC" tryEntityAttr="true" />" disabled="true">

      		        <input type="hidden" name="useShelfLifeExpirationDate" value="<ofbiz:format type="date"><ofbiz:inputvalue entityAttr="cust" field="SHELF_LIFE_EXPIRATION_DATE" tryEntityAttr="true" /></ofbiz:format>" disabled="true">

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

<%@include file="usingButton.jsp"%>

</form>

<%
} catch (Exception e) {
    out.println(e);
}
%>