<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import ="com.csmc.pms.webapp.util.SessionNames"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>

<%
	List guiPriv = (List)request.getSession().getAttribute(SessionNames.GUI_PRIV_LIST_KEY);
	String mtrGrp = UtilFormatOut.checkNull((String) request.getAttribute("mtrGrp"));
%>

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

	var mtrGrp = '<%=mtrGrp%>';
	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(mtrGrp); }});

	var eqpIdDS = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentByDeptIndex</ofbiz:url>'}),
	    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
    });

    var eqpIdCom = new Ext.form.ComboBox({
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

    var eqpId = '<%=UtilFormatOut.checkNull(request.getParameter("eqpId"))%>';
    eqpIdDS.load({params:{deptIndex:''}, callback:function(){ eqpIdCom.setValue(eqpId); }});
});
</script>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function queryStoReqList() {
    if (Ext.getDom('mtrGrp').value == "") {
        Ext.MessageBox.alert('警告', '请选择物料组');
		return;
	}

	doSubmit("<ofbiz:url>/intoCabinetQtyEntry</ofbiz:url>");
}

function intoCabinet() {
	doSubmit("<ofbiz:url>/intoCabinetQty</ofbiz:url>");
}

function doSubmit(url) {
	loading();
	document.stoReqEntryForm.action = url;
	document.stoReqEntryForm.submit();
}

function useMaterial(newStatus, newStatusDesc) {
    if (checkForm(newStatus)) {
        Ext.MessageBox.confirm('操作确认', '您确信选择 ' + newStatusDesc + ' 吗？',
            function result(value) {
                if ( value=="yes" ) {
        			doSubmit("<ofbiz:url>/useMaterialCabinetQty</ofbiz:url>?newStatus=" + newStatus);
        		} else {
        			return;
        		}
            }
        );
    }
}

function checkForm(newStatus) {
    if (totalSelected == 0) {
        Ext.MessageBox.alert('警告', '请选择需要使用的物料!');
        return false;
    }

    if (Ext.getDom('eqpId').value == "") {
		Ext.MessageBox.alert('警告', '请选择设备!');
        return false;
	}

	if (Ext.getDom('mtrGrp').value == "") {
		Ext.MessageBox.alert('警告', '请选择物料组!');
        return false;
	}

	return true;
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
	    var objQty = document.getElementsByName("useQty")[theIdx];
	} else {
	    var objQty = document.getElementById("useQty");
	}

	obj.checked = !obj.checked;//CheckBox添加按行选择功能
	if (obj.checked) {
		varTR.className = "tablelistchecked";
		totalSelected++;

		objQty.disabled = false;
        objQty.focus();
	} else {
		varTR.className = "tablelist";
		totalSelected--;

		objQty.disabled = true;
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

			document.getElementsByName("useQty")[i].disabled = !isChecked;
		}
	} else {// only one Checkbox
	    objCheckbox.checked = isChecked;
		objTr.className = isChecked ? "tablelistchecked" : "tablelist";

		document.getElementById("useQty").disabled = !isChecked;
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
        objQtyInput.value = totalQty;
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
    		<td class="tabletitle" width="12%">
    		    <i18n:message key="mcs.material_group" />
    		    <i18n:message key="mcs.data_required_red_star" />
    		</td>
			<td class="tablelist" colspan="3"><input type="text" size="40" name="mtrGrpSelect" autocomplete="off"/></td>
	    </tr>

	    <tr bgcolor="#DFE1EC" height="30">
			<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_number" /></td>
			<td ><input type="text"  size="20" name="mtrNum"  value="<%=UtilFormatOut.checkNull(request.getParameter("mtrNum"))%>"/></td>

			<td class="en11pxb" bgcolor="#ACD5C9" width="12%">收货人工号</td>
			<td ><input type="text"  size="20" name="recipient"  value="<%=UtilFormatOut.checkNull(request.getParameter("recipient"))%>"/></td>
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
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<!--<td class="en11pxb">物料组</td>-->
    	</tr>

	    <ofbiz:iterator name="cust" property="mtrMaintainList">
	        <tr class="tablelist">
	        	<td class="en11px">
	        	    <% if (guiPriv.contains("MATETIAL_INFO_DEFINE")) { %>
    	        	    <a href="<ofbiz:url>/defineMaterial?materialIndex=<ofbiz:inputvalue entityAttr="cust" field="materialIndex" tryEntityAttr="true" /></ofbiz:url>">
    	        	        <ofbiz:entityfield attribute="cust" field="mtrNum" />
    	        	    </a>
	        	    <% } else { %>
	        	        <ofbiz:entityfield attribute="cust" field="mtrNum" />
	        	    <%}%>
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

<ofbiz:if name="stoReqList" size="0">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>已领物料列表</legend>
      <table id="objTable" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      		<td class="en11pxb">
      		    <input type="checkbox" name="checkBoxSelectAll" id="checkBoxSelectAll"
      		    class="hand" title="全选/取消选择"
      		    <ofbiz:if name="stoReqList" size="0">
      		        onClick="selectAllCheckBox(objTable, objTr, objCheckbox, this.checked);"
      		    </ofbiz:if>
      		    >
      		</td>

      		<td class="en11pxb">
      		    <a href="javascript:doSubmit('<ofbiz:url>/intoCabinetQtyEntry?orderBy=mtr_num</ofbiz:url>')">
      		        <i18n:message key="mcs.material_number" />
      		    </a>
      		</td>

      		<td class="en11pxb">
      		    <a href="javascript:doSubmit('<ofbiz:url>/intoCabinetQtyEntry?orderBy=mtr_desc</ofbiz:url>')">
      		        <i18n:message key="mcs.material_description" />
      		    </a>
      		</td>

			  <td class="en11pxb">
				<% if (mtrGrp.equals(ConstantsMcs.SPAREPART_2P) || mtrGrp.equals(ConstantsMcs.SPAREPART_2S)) { %>
					使用数量
				<% } else { %>
					暂存数量
				<%}%>
				</td>
      		<td class="en11pxb">可用/总领用</td>

      		<td class="en11pxb">
      		    <a href="javascript:doSubmit('<ofbiz:url>/intoCabinetQtyEntry?orderBy=doc_time</ofbiz:url>')">
      		        <i18n:message key="mcs.doc_time" />
      		    </a>
      		</td>

      		<td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>
      		<td class="en11pxb">成本中心</td>
      		<td class="en11pxb">收货部门</td>

      		<td class="en11pxb">
      		    <a href="javascript:doSubmit('<ofbiz:url>/intoCabinetQtyEntry?orderBy=recipient</ofbiz:url>')">
      		        <i18n:message key="mcs.recipient" />
      		    </a>
      		</td>

      		<td class="en11pxb">申请原因</td>
    	</tr>

    	<ofbiz:iterator name="cust" property="stoReqList" type="java.util.Map">
        <tr class="tablelist" id="objTr" style="cursor:hand"
        onClick="javascript:selectOneCheckBox(objTable, objTr, objCheckbox, this.rowIndex, checkBoxSelectAll);">
        	<td class="en11px">
        	    <input type="checkbox" name="objCheckbox" id="objCheckbox"
        	    value="<ofbiz:entityfield attribute="cust" field="MATERIAL_STO_REQ_INDEX" />"
        	    onClick="this.checked=!this.checked;"
        	    >
        	</td>

			<td class="en11px">
				<% if (mtrGrp.equals(ConstantsMcs.SPAREPART_2P) || mtrGrp.equals(ConstantsMcs.SPAREPART_2S)) { %>
					<a href="javascript:doSubmit('<ofbiz:url>/intoRelateForm?reqIndex=<ofbiz:entityfield attribute="cust" field="MATERIAL_STO_REQ_INDEX" /></ofbiz:url>')">
						<ofbiz:entityfield attribute="cust" field="MTR_NUM" />
					</a>
				<% } else { %>
					<ofbiz:entityfield attribute="cust" field="MTR_NUM" />
				<%}%>
			</td>
        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC" /></td>

        	<td class="en11px">
        	    <input type="text" class="input" size="8" name="useQty" id="useQty" value="<ofbiz:entityfield attribute="cust" field="QTY" />"
  		        disabled="true"
  		        onkeyup="checkQtyInput(this, <ofbiz:entityfield attribute="cust" field="QTY" />)"
  		        onafterpaste="checkQtyInput(this, <ofbiz:entityfield attribute="cust" field="QTY" />)"
  		        onblur="checkInputValue(this)"
  		        />
  		    </td>

        	<td class="en11px">
        	    <ofbiz:entityfield attribute="cust" field="QTY" />
        	    /
        	    <ofbiz:entityfield attribute="cust" field="TOTAL" />
        	</td>

        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="DOC_TIME" /></td>
        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="VENDOR_BATCH_NUM" /></td>
        	<td class="en11px">
        	    <ofbiz:format type="date">
        	        <ofbiz:entityfield attribute="cust" field="SHELF_LIFE_EXPIRATION_DATE" />
        	    </ofbiz:format>
        	</td>

        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="COST_CENTER" /></td>
        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="ACCOUNT_DEPT" /></td>
        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="ACCOUNT_NAME" /></td>
        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="REASON_FOR_MOVEMENT" /></td>
        </tr>
	   	</ofbiz:iterator>

      </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <%if (true==Constants.CALL_TP_FLAG && ConstantsMcs.CHEMICAL.equals(mtrGrp) || ConstantsMcs.PHOTORESIST.equals(mtrGrp) || ConstantsMcs.QUARTZ.equals(mtrGrp) || ConstantsMcs.TARGET.equals(mtrGrp) || ConstantsMcs.GAS.equals(mtrGrp) || ConstantsMcs.DEVELOPER.equals(mtrGrp)) {%>
  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="javascript:intoCabinet();">
		    <span class="cabinet">&nbsp;<i18n:message key="mcs.status_cabinet_new" />&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
  <%}%>

  <%if (ConstantsMcs.CHEMICAL.equals(mtrGrp) || true==Constants.CALL_ASURA_FLAG && (ConstantsMcs.SPAREPART_2P.equals(mtrGrp) || ConstantsMcs.SPAREPART_2S.equals(mtrGrp))) {%>
  <tr>
	<td class="tabletitle" width="12%">
	    <i18n:message key="mcs.equipment" />
	    <i18n:message key="mcs.data_required_red_star" />
	</td>
	<td class="tablelist" width="30%">
	    <input type="text" size="40" name="eqpIdSelect" autocomplete="off"/>
	</td>

	<td class="en11pxb">
        使用备注
        <input type="text" class="input" size="20" name="useNote" value="" />
    </td>

    <%if (ConstantsMcs.CHEMICAL.equals(mtrGrp) || true==Constants.CALL_ASURA_FLAG && ConstantsMcs.PHOTORESIST.equals(mtrGrp)) {%>
    <td width="30%"><ul class="button">
		<li><a class="button-text" href="javascript:useMaterial('<%=ConstantsMcs.USE_AND_FINISH%>', '使用并用完');">
		    <span>&nbsp;使用并用完&nbsp;</span>
		</a></li>
	</ul></td>
	<%}%>

	<!-- <%if (ConstantsMcs.SPAREPART_2P.equals(mtrGrp) || ConstantsMcs.SPAREPART_2S.equals(mtrGrp)) {%>
	<td width="30%"><ul class="button">
		<li><a class="button-text" href="javascript:useMaterial('<%=ConstantsMcs.USING%>', '<i18n:message key="mcs.status_using" />');">
		    <span>&nbsp;<i18n:message key="mcs.status_using" />&nbsp;</span>
		</a></li>
	</ul></td>
	<%}%> -->
  </tr>
  <%}%>
</table>

</ofbiz:if>

<br/>

<ofbiz:if name="alertMaterialList" size="0">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>10天内有效期过期 物料列表，请通知工艺!</legend>
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


		<ofbiz:iterator name="material" property="alertMaterialList" type="java.util.Map">
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
      </table>
  </fieldset></td>
</tr>
</table>
</ofbiz:if>

</form>

<br/>