<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<%
    String oldStatus = UtilFormatOut.checkNull(request.getParameter("oldStatus"));
    String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
%>

<!-- ##################################### yui page script ################################ -->
<script language="javascript">
var useMaterialById = function() {

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

Ext.EventManager.onDocumentReady(useMaterialById.init, useMaterialById, true);
</script>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function queryFabRepairMaterial() {
    if(document.getElementById("type1").checked==true){
		document.getElementById("oldStatus").value = "<%=ConstantsMcs.FAB_REPAIR%>";
	}else if(document.getElementById("type2").checked == true){
		document.getElementById("oldStatus").value = "<%=ConstantsMcs.FAB_WASH%>";
	}else if(document.getElementById("type3").checked == true){
		document.getElementById("oldStatus").value = "<%=ConstantsMcs.VENDOR_REPAIR_OPT%>";
	}else if(document.getElementById("type4").checked == true){
		document.getElementById("oldStatus").value = "<%=ConstantsMcs.VENDOR_WASH_OPT%>";
	}else if(document.getElementById("type5").checked == true){
		document.getElementById("oldStatus").value = "<%=ConstantsMcs.VENDOR_REPAIR_LEADER%>";
	}else if(document.getElementById("type6").checked == true){
		document.getElementById("oldStatus").value = "<%=ConstantsMcs.VENDOR_WASH_LEADER%>";
	}else if(document.getElementById("type7").checked == true){
		document.getElementById("oldStatus").value = "<%=ConstantsMcs.CABINET_RECYCLE_REPAIR%>";
	}else if(document.getElementById("type8").checked == true){
		document.getElementById("oldStatus").value = "<%=ConstantsMcs.CABINET_RECYCLE_WASH%>";
	}

	if(document.getElementById("oldStatus").value==""){
        alert("请选择类型");
        return;
    }

    doSubmit("<ofbiz:url>/fabRepairEntry</ofbiz:url>");
}

function changeMaterialStatus(newStatus, newStatusDesc) {
    Ext.MessageBox.confirm('操作确认', '您确信选择 ' + newStatusDesc + ' 吗？',function result(value){
        if(value=="yes"){
			if (checkForm()) {
				doSubmit("<ofbiz:url>/fabRepairById</ofbiz:url>?newStatus=" + newStatus);
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
	document.fabRepairForm.action = url;
	document.fabRepairForm.submit();
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
<form method="post" name="fabRepairForm">
<input id="oldStatus" type="hidden" name="oldStatus" value="<%=oldStatus%>" />
<input type="hidden" name="mtrGrpSubmit" value="<%=mtrGrp%>">

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
			    <input type="radio" id="type1" name="type1" value="<%=ConstantsMcs.FAB_REPAIR%>" onclick="queryFabRepairMaterial();"><i18n:message key="mcs.status_fab_repair" />&nbsp;&nbsp;&nbsp;&nbsp;
			    <input type="radio" id="type3" name="type1" value="<%=ConstantsMcs.VENDOR_REPAIR_OPT%>" onclick="queryFabRepairMaterial();"><i18n:message key="mcs.status_vendor_repair" />&nbsp;&nbsp;&nbsp;&nbsp;
			    <input type="radio" id="type5" name="type1" value="<%=ConstantsMcs.VENDOR_REPAIR_LEADER%>" onclick="queryFabRepairMaterial();"><i18n:message key="mcs.status_vendor_repair_leader" />&nbsp;&nbsp;&nbsp;&nbsp;
			    <input type="radio" id="type7" name="type1" value="<%=ConstantsMcs.CABINET_RECYCLE_REPAIR%>" onclick="queryFabRepairMaterial();">已维修

			    <br/>
			    <input type="radio" id="type2" name="type1" value="<%=ConstantsMcs.FAB_WASH%>" onclick="queryFabRepairMaterial();"><i18n:message key="mcs.status_fab_wash" />&nbsp;&nbsp;&nbsp;&nbsp;
			    <input type="radio" id="type4" name="type1" value="<%=ConstantsMcs.VENDOR_WASH_OPT%>" onclick="queryFabRepairMaterial();"><i18n:message key="mcs.status_vendor_wash" />&nbsp;&nbsp;&nbsp;&nbsp;
			    <input type="radio" id="type6" name="type1" value="<%=ConstantsMcs.VENDOR_WASH_LEADER%>" onclick="queryFabRepairMaterial();"><i18n:message key="mcs.status_vendor_wash_leader" />&nbsp;&nbsp;&nbsp;&nbsp;
			    <input type="radio" id="type8" name="type1" value="<%=ConstantsMcs.CABINET_RECYCLE_WASH%>" onclick="queryFabRepairMaterial();">已清洗
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
		<li><a class="button-text" href="javascript:queryFabRepairMaterial();"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>

	<td><ul class="button">
			<li><a class="button-text" onclick="javascript: history.back();" href="#"><span>&nbsp;返回&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>维修清洗 物料列表</legend>
      <table id="objTableFabRepair" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      	    <td class="en11pxb">
      		    <input type="checkbox" name="checkBoxSelectAllFabRepair" id="checkBoxSelectAllFabRepair"
      		    class="hand" title="全选/取消选择"
      		    <ofbiz:if name="fabRepairMaterialList" size="0">
      		        onClick="selectAllCheckBox(objTableFabRepair, objTrFabRepair, objCheckboxSI, this.checked);"
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

	    <ofbiz:iterator name="cust" property="fabRepairMaterialList">
	         <tr class="tablelist" id="objTrFabRepair" style="cursor:hand"
	        onClick="javascript:selectOneCheckBox(objTableFabRepair, objTrFabRepair, objCheckboxSI, this.rowIndex, checkBoxSelectAllFabRepair);">
	            <td class="en11px">
	        	    <input type="checkbox" name="objCheckboxSI" id="objCheckboxSI"
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


<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <%//1内部维修
    if(oldStatus.equals("FAB-REPAIR")){
    %>
	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.CABINET_RECYCLE_REPAIR%>','<i18n:message key="mcs.status_cabinet_recycle" />');">
		    <span class="cabinet">&nbsp;<i18n:message key="mcs.status_cabinet_recycle" />(已维修)&nbsp;</span>
		</a></li>
	</ul></td>

	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.VENDOR_REPAIR_OPT%>', '<i18n:message key="mcs.status_vendor_repair" />');">
		    <span class="vendor">&nbsp;<i18n:message key="mcs.status_vendor_repair" />&nbsp;</span>
		</a></li>
	</ul></td>

	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.SCRAP%>', '<i18n:message key="mcs.status_scrap" />');">
		    <span class="scrap">&nbsp;<i18n:message key="mcs.status_scrap" />&nbsp;</span>
		</a></li>
	</ul></td>

	<%//2自主清洗
    }else if(oldStatus.equals("FAB-WASH")){
    %>
    <td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.CABINET_RECYCLE_WASH%>','<i18n:message key="mcs.status_cabinet_recycle" />');">
		    <span class="cabinet">&nbsp;<i18n:message key="mcs.status_cabinet_recycle" />(已清洗)&nbsp;</span>
		</a></li>
	</ul></td>

	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.VENDOR_WASH_OPT%>', '<i18n:message key="mcs.status_vendor_wash" />');">
		    <span class="vendor">&nbsp;<i18n:message key="mcs.status_vendor_wash" />&nbsp;</span>
		</a></li>
	</ul></td>

	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.SCRAP%>', '<i18n:message key="mcs.status_scrap" />');">
		    <span class="scrap">&nbsp;<i18n:message key="mcs.status_scrap" />&nbsp;</span>
		</a></li>
	</ul></td>

    <%//3送外待维修
    }else if(oldStatus.equals("VENDOR-REPAIR-OPT")){
    %>
	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.VENDOR_REPAIR_LEADER%>', '<i18n:message key="mcs.status_vendor_repair_leader" />');">
		    <span class="vendor">&nbsp;<i18n:message key="mcs.status_vendor_repair_leader" />&nbsp;</span>
		</a></li>
	</ul></td>

    <%//4送外待清洗
    }else if(oldStatus.equals("VENDOR-WASH-OPT")){
    %>
	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.VENDOR_WASH_LEADER%>', '<i18n:message key="mcs.status_vendor_wash_leader" />');">
		    <span class="vendor">&nbsp;<i18n:message key="mcs.status_vendor_wash_leader" />&nbsp;</span>
		</a></li>
	</ul></td>

    <%//5送外维修中
    }else if(oldStatus.equals("VENDOR-REPAIR-LEADER")){
    %>
	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.CABINET_RECYCLE_REPAIR%>','<i18n:message key="mcs.status_cabinet_recycle" />');">
		    <span class="cabinet">&nbsp;<i18n:message key="mcs.status_cabinet_recycle" />(已维修)&nbsp;</span>
		</a></li>
	</ul></td>

	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.SCRAP%>', '<i18n:message key="mcs.status_scrap" />');">
		    <span class="scrap">&nbsp;<i18n:message key="mcs.status_scrap" />&nbsp;</span>
		</a></li>
	</ul></td>

    <%//6送外清洗中
    }else if(oldStatus.equals("VENDOR-WASH-LEADER")){
    %>
	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.CABINET_RECYCLE_WASH%>','<i18n:message key="mcs.status_cabinet_recycle" />');">
		    <span class="cabinet">&nbsp;<i18n:message key="mcs.status_cabinet_recycle" />(已清洗)&nbsp;</span>
		</a></li>
	</ul></td>

	<td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.SCRAP%>', '<i18n:message key="mcs.status_scrap" />');">
		    <span class="scrap">&nbsp;<i18n:message key="mcs.status_scrap" />&nbsp;</span>
		</a></li>
	</ul></td>
    <%}%>

  </tr>
</table>

<br/>

<script language="javascript">
	if('<%=oldStatus%>'=='<%=ConstantsMcs.FAB_REPAIR%>'){
	    document.getElementById("type1").checked=true;
	}else if('<%=oldStatus%>'=='<%=ConstantsMcs.FAB_WASH%>'){
	    document.getElementById("type2").checked=true;
	}else if('<%=oldStatus%>'=='<%=ConstantsMcs.VENDOR_REPAIR_OPT%>'){
	    document.getElementById("type3").checked=true;
	}else if('<%=oldStatus%>'=='<%=ConstantsMcs.VENDOR_WASH_OPT%>'){
	    document.getElementById("type4").checked=true;
	}else if('<%=oldStatus%>'=='<%=ConstantsMcs.VENDOR_REPAIR_LEADER%>'){
	    document.getElementById("type5").checked=true;
	}else if('<%=oldStatus%>'=='<%=ConstantsMcs.VENDOR_WASH_LEADER%>'){
	    document.getElementById("type6").checked=true;
	}else if('<%=oldStatus%>'=='<%=ConstantsMcs.CABINET_RECYCLE_REPAIR%>'){
	    document.getElementById("type7").checked=true;
	}else if('<%=oldStatus%>'=='<%=ConstantsMcs.CABINET_RECYCLE_WASH%>'){
	    document.getElementById("type8").checked=true;
	}
</script>

</form>