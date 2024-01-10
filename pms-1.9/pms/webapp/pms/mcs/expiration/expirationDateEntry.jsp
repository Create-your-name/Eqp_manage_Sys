<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>
<%@ page import="java.lang.String" %>

<%
	Object listSize = request.getAttribute("listSize");
%>

<script language="javascript">
	/**
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


var useMaterialById = function() {

	var deptIndexDS, equipmentDeptCom;

	return {
		init : function() {
			this.createDataStore();
		    this.createCombox();
		    this.initLoad();
		},

		//设置数据源
		createDataStore : function() {

			deptIndexDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonEquipmentDept</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'deptIndex'}, {name: 'equipmentDept'}]))
		    });
		},

		createCombox : function() {

			//设置物料组
		    equipmentDeptCom = new Ext.form.ComboBox({
			    store: deptIndexDS,
			    displayField:'equipmentDept',
			    valueField:'deptIndex',
			    hiddenName:'deptIndex',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all',
			    allowBlank: true
		    });
		    equipmentDeptCom.applyTo('deptIndexSelect');
		},

		initLoad : function() {
			var deptIndex = '<%=UtilFormatOut.checkNull(request.getParameter("deptIndex"))%>';
	    	deptIndexDS.load({callback:function(){ equipmentDeptCom.setValue(deptIndex); }});
		}
	}
}();

Ext.EventManager.onDocumentReady(useMaterialById.init, useMaterialById, true);
*/
</script>


<!-- ##################################### submit script ################################ -->
<script language="javascript">
function queryMaterial() {
		if(document.expirationDateForm.vendorBatchNum.value == ""){
			alert("厂家批号不能为空");
		}else{
    	doSubmit("<ofbiz:url>/queryMaterialByVendorBatchNum</ofbiz:url>");
    }
}

function saveExpirationDate() {
    Ext.MessageBox.confirm('操作确认', '有效期保存吗？',function result(value){
        if(value=="yes"){
			if (checkForm()) {
				doSubmit("<ofbiz:url>/saveExpirationDate</ofbiz:url>");
        	}
		}else{
			return;
		}
    });
}

function checkForm() {
    //if (totalSelected == 0) {
        //Ext.MessageBox.alert('警告', '请选择需要更换的物料');
       // return false;
    //}
  	for(var i=0;i<document.expirationDateForm.listSize.value;i++){
  			if(eval("document.expirationDateForm.mrbDate"+i).value==""&&eval("document.expirationDateForm.mrbId"+i).value!=""){
  					alert("mrbId和mrbDate必须同时存在");
  					return false;
  			}

  			if(eval("document.expirationDateForm.mrbDate"+i).value!=""&&eval("document.expirationDateForm.mrbId"+i).value==""){
  					alert("mrbId和mrbDate必须同时存在");
  					return false;
  			}

  			if(eval("document.expirationDateForm.mrbDate"+i).value!=""){
  					var startStr = eval("document.expirationDateForm.shelfLifeExpirationDate"+i).value;
						var startTime = new Date(startStr.substr(0,startStr.indexOf(' ')).replace(/-/g,"\/"));
   					var endTime = new Date(eval("document.expirationDateForm.mrbDate"+i).value.replace(/-/g,"\/"));

   					if(startTime.getTime() - endTime.getTime()>=0){
							alert("mrb有效期时间不能小于初始有效期");
							return false;
  					}
  			}
  	}

	return true;
}

function doSubmit(url) {

	loading();
	document.expirationDateForm.action = url;
	document.expirationDateForm.submit();
}

Ext.onReady(function(){
		<%
			if (listSize != null){
				for(int i=0;i<Integer.parseInt(listSize.toString());i++){
		%>
       //格式化日期
	   var mrbDate<%=i%> = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:true
	    });

	    mrbDate<%=i%>.applyTo("<%="mrbDate"+i%>");

	   <%
	   		}
	   	}
	   %>
});
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form method="post" name="expirationDateForm">

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>分类条件</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
			<td class="tabletitle" width="12%">
			    <i18n:message key="mcs.vendor_batch_num" />
			    <i18n:message key="mcs.data_required_red_star" />
			</td>

			<td class="tablelist" width="30%">
			    <input type="text" class="input" size="20" name="vendorBatchNum" value="<ofbiz:field attribute="vendorBatchNum"/>" />
			</td>
			<td class="tablelist" colspan = "2"></td>
	    </tr>
	    <!--
	    <tr>
				<td class="tabletitle" width="12%"><i18n:message key="mcs.dept" /></td>
				<td class="tablelist" width="30%">
				    <input type="text" size="40" name="deptIndexSelect" autocomplete="off"/>
				</td>
	    	<td class="tabletitle" width="12%">
				    <i18n:message key="mcs.material_group" />
				</td>
				<td class="tablelist" width="30%">
				    <input type="text" size="40" name="mtrGrpSelect" autocomplete="off"/>
				</td>
	    </tr>
	    -->
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
      <legend>物料列表</legend>
      <table id="objTableFabRepair" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      		<td class="en11pxb"><i18n:message key="mcs.index_alias_name" /></td>
      	  <td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>
      		<td class="en11pxb">MRB_ID</td>
      		<td class="en11pxb"><i18n:message key="mcs.mrb_date" /></td>
    	</tr>
			<%
				int i=0;
			%>
		<ofbiz:iterator name="material" property="materialList" type="java.util.Map">
	         <tr class="tablelist" id="objTrFabRepair" style="cursor:hand">
				<td class="en11px"><ofbiz:entityfield attribute="material" field="ALIAS_NAME" /></td>
				<td class="en11px"><input type=hidden name=materialStatusIndex<%=i%> id=materialStatusIndex<%=i%> value='<ofbiz:entityfield attribute="material" field="MATERIAL_STATUS_INDEX" />'><ofbiz:entityfield attribute="material" field="VENDOR_BATCH_NUM" /></td>
	        	<td class="en11px"><ofbiz:entityfield attribute="material" field="MTR_NUM" /></td>
	        	<td class="en11px"><ofbiz:entityfield attribute="material" field="MTR_DESC" /></td>
	        	<td class="en11px">
	        	    <ofbiz:format type="date">
	        	        <ofbiz:entityfield attribute="material" field="SHELF_LIFE_EXPIRATION_DATE" />
	        	    </ofbiz:format>
					<input type=hidden name=shelfLifeExpirationDate<%=i%> id=shelfLifeExpirationDate<%=i%> value='<ofbiz:entityfield attribute="material" field="SHELF_LIFE_EXPIRATION_DATE" />'>
				</td>
	        	<td class="en11px">
	        	    <input type=text name="mrbId<%=i%>" id="mrbId<%=i%>" value='<ofbiz:entityfield attribute="material" field="MRB_ID" />'>
	        	    <input type=hidden name="oldMrbId<%=i%>" id="oldMrbId<%=i%>" value='<ofbiz:entityfield attribute="material" field="MRB_ID" />'>
	        	</td>
	        	<td class="en11px">
        		    <input type=text name="mrbDate<%=i%>" id="mrbDate<%=i%>" value='<ofbiz:entityfield attribute="material" field="MRB_DATE" />'>
					<input type=hidden name="oldMrbDate<%=i%>" id="oldMrbDate<%=i%>" value='<ofbiz:entityfield attribute="material" field="MRB_DATE" />'>
				</td>
	        </tr>
					
	        <%
	        i++;
	        %>
	   	</ofbiz:iterator>

      </table>
  </fieldset></td>
  <input type=hidden name="listSize" id="listSize" value='<ofbiz:field attribute="listSize"/>'>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
	<td><ul class="button">
		<li><a class="button-text" href="javascript:saveExpirationDate();">
		    <span class="cabinet">&nbsp;保存&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>

<br/>
</form>