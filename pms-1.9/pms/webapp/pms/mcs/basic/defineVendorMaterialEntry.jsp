<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<%@ include file="../../pms/yui-ext/ext-comdlg.jsp"%>

<!-- ##################################### yui page script ################################ -->
<script language="javascript">
var defineVendorMatetial = function() {

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
	    	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue('<ofbiz:field attribute="mtrGrp"/>'); }});
		}
	}
}();

Ext.EventManager.onDocumentReady(defineVendorMatetial.init, defineVendorMatetial, true);
</script>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function queryDefineVendorMaterial() {

	if(Ext.get('mtrGrp').dom.value==""){
		Ext.MessageBox.alert('警告', '请选择物料组!');
		return;
	}
	
		defineVendorMaterialForm.submit();

}

function doSubmit(url) {

	loading();
	document.defineVendorMaterialForm.action = url;
	document.defineVendorMaterialForm.submit();
}


	//改变弹出框大小width,height
	extDlg.dlgInit('500','300');
	
	//新增功能弹出页
	function addVendorMaterial(obj){
		Ext.get('mtrNum').dom.value="";
		Ext.get('vendorMtrNum').dom.value="";
		extDlg.showAddDialog(obj);
	}
	
	//修改功能弹出页
	function editVendorMaterial(obj,mtrNum){
		Ext.get('mtrNum').dom.value=mtrNum;
		var url='<ofbiz:url>/getJsonVendorMaterial</ofbiz:url>?mtrNum='+mtrNum;
		extDlg.showEditDialog(obj,url);
	}
		
	//删除
	function delVendorMaterial(mtrNum,mtrGrp){
		Ext.MessageBox.confirm('删除确认', '您确信要删除此记录吗？',function result(value){
			if(value=="yes"){
				var url='<ofbiz:url>/delVendorMaterial</ofbiz:url>?mtrNum='+mtrNum+'&mtrGrp='+mtrGrp;
				document.location=url;
			}else{
				return;
			}
		});
	}
	
	function commentSuccess(o){
	//alert(o.responseText);
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('mtrNum').dom.value=result.mtrNum;
			Ext.get('vendorMtrNum').dom.value=result.vendorMtrNum;
		}
	}
	
	//数据合法性校验
	function checkForm(){
		var mtrNum = Ext.get('mtrNum').dom.value;
		var vendorMtrNum = Ext.get('vendorMtrNum').dom.value;
		if(mtrNum==""){
			return "请选择物料号";
		}
		if(vendorMtrNum==""){
			return "厂商批号不可以为空";
		}
		
		return "";
	}

</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form method="post" name="defineVendorMaterialForm" action="<%=request.getContextPath()%>/control/defineVendorMaterialEntry" >
<input name="eventObject" type="hidden" value="" />

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

<%--			<td class="tabletitle" width="12%"><i18n:message key="mcs.material_number" /></td>--%>
<%--			<td class="tablelist" width="30%">--%>
<%--			    <input type="text" class="input" size="20" name="mtr_Num" value="<ofbiz:field attribute="mtr_Num"/>" />--%>
<%--			</td>--%>
	    </tr>
	  </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="#" onclick="javascript:queryDefineVendorMaterial();"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>
</form>
<br>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><fieldset>
		<legend>厂商料号列表</legend>
			<table id="objTable1" width="100%" border="0" cellspacing="1" cellpadding="2">
				<tr class="tabletitle">
					<td width="5%" class="en11pxb" align="center"><img src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addVendorMaterial(this);"/></td>
					<td width="14%" class="en11pxb"><i18n:message key="mcs.material_number" /></td>
					<td width="16%" class="en11pxb"><i18n:message key="mcs.material_description" /></td>
					<td width="16%" class="en11pxb"><i18n:message key="mcs.vendor_mtr_num" /></td>
				</tr>
				
				<ofbiz:if name="vendorMaterialList" size="0">
				<ofbiz:iterator name="cust" property="vendorMaterialList" type="java.util.Map">
				<tr class="tablelist" id="objTr1" style="cursor:hand">
					<td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand"  onclick="delVendorMaterial('<ofbiz:entityfield attribute="cust" field="MTR_NUM"/>','<ofbiz:field attribute="mtrGrp"/>');"/></td>
					<td class="en11px"><a href="#" onclick="editVendorMaterial(this,'<ofbiz:inputvalue entityAttr="cust" field="MTR_NUM"/>')"><ofbiz:entityfield attribute="cust" field="MTR_NUM" /></a></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC" /></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="VENDOR_MTR_NUM" /></td>
				</tr>
				</ofbiz:iterator>
				</ofbiz:if>
			</table>
		</fieldset></td>
	</tr>
</table>
</div>

<div id="x-dlg" style="visibility:hidden;">
        <div class="x-dlg-hd">厂商料号设定</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="厂商料号设定">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/manageVendorMaterial?mtrGrp=<ofbiz:field attribute="mtrGrp"/>" method="post" id="manageVendorMaterialForm" onsubmit="return false;">

                <p>
                <label for="name"><small><i18n:message key="mcs.material_number" /></small><big><i18n:message key="mcs.data_required_red_star" /></big></label>
                <select name="mtrNum" id="mtrNum" class="select">
					<ofbiz:if name="materialNumList">
		        		<ofbiz:iterator name="materialNum" property="materialNumList">
			    			<option value='<ofbiz:inputvalue entityAttr="materialNum" field="mtrNum"/>'><ofbiz:inputvalue entityAttr="materialNum" field="mtrNum"/></option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
				</select>
                <label for="name"><small><i18n:message key="mcs.vendor_mtr_num" /></small><big><i18n:message key="mcs.data_required_red_star" /></big></label>
                <input class="textinput" type="text" name="vendorMtrNum" id="vendorMtrNum" value="" size="22" tabindex="1" />
                </p>
                </form>
            </div>
        </div>
    </div>
    <div class="x-dlg-ft">
        <div id="dlg-msg">
            <span id="post-error" class="posting-msg"><img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" />&nbsp;<span id="post-error-msg"></span></span>
        </div>
    </div>
</div>
