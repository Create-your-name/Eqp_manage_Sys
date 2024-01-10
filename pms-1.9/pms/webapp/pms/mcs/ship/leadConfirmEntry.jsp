<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<%
    String materialStatus = UtilFormatOut.checkNull(request.getParameter("oldStatus"));
    String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
%>

<!-- ##################################### yui page script ################################ -->
<script language="javascript">
var leadConfirmById = function() {

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

Ext.EventManager.onDocumentReady(leadConfirmById.init, leadConfirmById, true);
</script>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function queryLeadConfirmMaterial() {
	if(document.getElementById("type1").checked==true){
		document.getElementById("oldStatus").value="<%=ConstantsMcs.ONLINE_SCRAP_OPT%>";
	}else if(document.getElementById("type2").checked==true){
		document.getElementById("oldStatus").value="<%=ConstantsMcs.GENERAL_SCRAP_OPT%>";
	}else if(document.getElementById("type3").checked==true){
		document.getElementById("oldStatus").value="<%=ConstantsMcs.VENDOR_REPAIR_OPT%>";
	}
    doSubmit("<ofbiz:url>/leadConfirmEntry</ofbiz:url>");
}

function changeMaterialStatus(oldStatus, newStatus, newStatusDesc,operateType) {
    Ext.MessageBox.confirm('操作确认', '您确信 ' + operateType +' 为 ' + newStatusDesc + ' 吗？',function result(value){
        if(value=="yes"){
			if (checkForm()) {
				doSubmit('<ofbiz:url>/leadConfirmById</ofbiz:url>?newStatus=' + newStatus +'&oldStatus=' + oldStatus);
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

function doSubmit(url) {

	loading();
	document.leadConfirmForm.action = url;
	document.leadConfirmForm.submit();
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

function viewStatusHist(materialStatusIndex) {
    var url = "<ofbiz:url>/queryMaterialStatusHist</ofbiz:url>?materialStatusIndex=" + materialStatusIndex;
    var result = window.showModalDialog(url, window, "dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");
}

-->
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form method="post" name="leadConfirmForm">
<input id="oldStatus" type="hidden" name="oldStatus" value="<%=materialStatus%>" />

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
			    <input type="radio"  id="type1" name="type1" value="<%=ConstantsMcs.ONLINE_SCRAP_OPT%>" checked onclick="queryLeadConfirmMaterial();"><i18n:message key="mcs.status_online_scrap" />&nbsp;&nbsp;&nbsp;
			    <input type="radio"  id="type2" name="type1" value="<%=ConstantsMcs.GENERAL_SCRAP_OPT%>" onclick="queryLeadConfirmMaterial();"><i18n:message key="mcs.status_general_scrap" />&nbsp;&nbsp;&nbsp;
			    <input type="radio"  id="type3" name="type1" value="<%=ConstantsMcs.VENDOR_REPAIR_OPT%>" onclick="queryLeadConfirmMaterial();"><i18n:message key="mcs.status_vendor_repair" />
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
		<li><a class="button-text" href="javascript:queryLeadConfirmMaterial();"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>课长确认物料列表</legend>
      <table id="objTableLeadConfirm" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      	    <td class="en11pxb">
      		    <input type="checkbox" name="checkBoxSelectAllLeadConfirm" id="checkBoxSelectAllLeadConfirm"
      		    class="hand" title="全选/取消选择"
      		    <ofbiz:if name="leadConfirmList" size="0">
      		        onClick="selectAllCheckBox(objTableLeadConfirm, objTrLeadConfirm, objCheckboxLeadConfirm, this.checked);"
      		    </ofbiz:if>
      		    >
      		</td>
      	    <td class="en11pxb"><i18n:message key="mcs.index_alias_name" /></td>
      	    <td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.avg_price" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.mrb_date" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.operator" /></td>
    	</tr>

		<ofbiz:if name="leadConfirmList" size="0">
		<ofbiz:iterator name="cust" property="leadConfirmList" type="java.util.Map">
	         <tr class="tablelist" id="objTrLeadConfirm" style="cursor:hand"
	        onClick="javascript:selectOneCheckBox(objTableLeadConfirm, objTrLeadConfirm, objCheckboxLeadConfirm, this.rowIndex, checkBoxSelectAllLeadConfirm);">
	            <td class="en11px">
	        	    <input type="checkbox" name="objCheckboxLeadConfirm" id="objCheckboxLeadConfirm"
	        	    value="<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_STATUS_INDEX" tryEntityAttr="true" />"
	        	    onClick="this.checked=!this.checked;"
	        	    >
	        	</td>
	        	<td class="en11px">
	        	    <a href="javascript:viewStatusHist('<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_STATUS_INDEX" tryEntityAttr="true" />')">
	        	        <ofbiz:inputvalue entityAttr="cust" field="ALIAS_NAME" tryEntityAttr="true" />
	        	    </a>
	        	</td>
	            <td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="VENDOR_BATCH_NUM" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MTR_NUM" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MTR_DESC" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MOVING_AVERAGE_PRICE" tryEntityAttr="true" /></td>
	        	<td class="en11px">
	        	    <ofbiz:format type="date">
	        	        <ofbiz:inputvalue entityAttr="cust" field="SHELF_LIFE_EXPIRATION_DATE" tryEntityAttr="true" />
	        	    </ofbiz:format>
	        	</td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MRB_DATE" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="ACCOUNT_NAME" tryEntityAttr="true" /></td>
	        </tr>
		</ofbiz:iterator>
		</ofbiz:if>

      </table>
  </fieldset></td>
</tr>
</table>

<%
if(materialStatus.equals("GENERAL-SCRAP-OPT")){
%>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td class="en11pxb">
        确认动作：&nbsp;
    </td>
	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.GENERAL_SCRAP_OPT%>','<%=ConstantsMcs.GENERAL_SCRAP_LEADER%>','<i18n:message key="mcs.status_general_scrap" />','确认');">
		    <span class="generalScrap">&nbsp;<i18n:message key="mcs.status_general_scrap" />&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
  <tr height="30">
    <td class="en11pxb">
        转换动作：&nbsp;
    </td>
	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.GENERAL_SCRAP_OPT%>','<%=ConstantsMcs.ONLINE_SCRAP_OPT%>','<i18n:message key="mcs.status_online_scrap" />','转换');">
		    <span class="onlineScrap">&nbsp;<i18n:message key="mcs.status_online_scrap" />&nbsp;</span>
		</a></li>
	</ul></td>

	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.GENERAL_SCRAP_OPT%>','<%=ConstantsMcs.VENDOR_REPAIR_OPT%>','<i18n:message key="mcs.status_vendor_repair" />','转换');">
		    <span class="vendor">&nbsp;<i18n:message key="mcs.status_vendor_repair" />&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>
<%
}else if(materialStatus.equals("VENDOR-REPAIR-OPT")){
%>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td class="en11pxb">
        确认动作：&nbsp;
    </td>
	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.VENDOR_REPAIR_OPT%>','<%=ConstantsMcs.VENDOR_REPAIR_LEADER%>','<i18n:message key="mcs.status_vendor_repair" />','确认');">
		    <span class="vendor">&nbsp;<i18n:message key="mcs.status_vendor_repair" />&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
  <tr height="30">
    <td class="en11pxb">
        转换动作：&nbsp;
    </td>
	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.VENDOR_REPAIR_OPT%>','<%=ConstantsMcs.ONLINE_SCRAP_OPT%>','<i18n:message key="mcs.status_online_scrap" />','转换');">
		    <span class="onlineScrap">&nbsp;<i18n:message key="mcs.status_online_scrap" />&nbsp;</span>
		</a></li>
	</ul></td>

	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.VENDOR_REPAIR_OPT%>','<%=ConstantsMcs.GENERAL_SCRAP_OPT%>','<i18n:message key="mcs.status_general_scrap" />','转换');">
		    <span class="generalScrap">&nbsp;<i18n:message key="mcs.status_general_scrap" />&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>

<%} else {%>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td class="en11pxb">
        确认动作：&nbsp;
    </td>
	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.ONLINE_SCRAP_OPT%>','<%=ConstantsMcs.ONLINE_SCRAP_LEADER%>','<i18n:message key="mcs.status_online_scrap" />','确认');">
		    <span class="onlineScrap">&nbsp;<i18n:message key="mcs.status_online_scrap" />&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
  <tr height="30">
    <td class="en11pxb">
        转换动作：&nbsp;
    </td>
	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.ONLINE_SCRAP_OPT%>','<%=ConstantsMcs.GENERAL_SCRAP_OPT%>','<i18n:message key="mcs.status_general_scrap" />','转换');">
		    <span class="generalScrap">&nbsp;<i18n:message key="mcs.status_general_scrap" />&nbsp;</span>
		</a></li>
	</ul></td>

	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.ONLINE_SCRAP_OPT%>','<%=ConstantsMcs.VENDOR_REPAIR_OPT%>','<i18n:message key="mcs.status_vendor_repair" />','转换');">
		    <span class="vendor">&nbsp;<i18n:message key="mcs.status_vendor_repair" />&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>

<%}%>

<br/>

<script language="javascript">
	if('<%=materialStatus%>'=='<%=ConstantsMcs.ONLINE_SCRAP_OPT%>'){
	document.getElementById("type1").checked=true;
	}else if('<%=materialStatus%>'=='<%=ConstantsMcs.GENERAL_SCRAP_OPT%>'){
	document.getElementById("type2").checked=true;
	}else if('<%=materialStatus%>'=='<%=ConstantsMcs.VENDOR_REPAIR_OPT%>'){
	document.getElementById("type3").checked=true;
	}
</script>

</form>