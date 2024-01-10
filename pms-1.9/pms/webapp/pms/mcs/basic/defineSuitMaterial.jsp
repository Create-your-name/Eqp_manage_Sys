<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>

<%@ include file="../../pms/yui-ext/ext-comdlg.jsp"%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">

	//改变弹出框大小width,height
	extDlg.dlgInit('500','300');

	  $(".nav,.left-menu-click").click(function () {
            addSuitMaterial($(this).attr("data-url"));
        });
	
	//新增功能弹出页
	function addSuitMaterial(obj){
		Ext.get('materialSuitItemIndex').dom.value="";
		Ext.get('manageMaterialSuitIndex').dom.value='<ofbiz:field attribute="materialSuitIndex"/>';
		Ext.get('mtrNum').dom.value="";
		//Ext.get('mtrQty').dom.value="";
		extDlg.showAddDialog(obj);
	}
	
	//修改功能弹出页
//	function editSuitMaterial(obj,materialSuitItemIndex,manageMaterialSuitIndex){
//		Ext.get('materialSuitItemIndex').dom.value=materialSuitItemIndex;
//		Ext.get('manageMaterialSuitIndex').dom.value=manageMaterialSuitIndex;
//		var url='<ofbiz:url>/getJsonSuitMaterialByIndex</ofbiz:url>?materialSuitItemIndex='+materialSuitItemIndex+'&materialSuitIndex='+manageMaterialSuitIndex;
//		extDlg.showEditDialog(obj,url);
//	}
		
	//删除
	function delSuitMaterial(materialSuitItemIndex){
		Ext.MessageBox.confirm('删除确认', '您确信要删除此记录吗？',function result(value){
			if(value=="yes"){
				var url='<ofbiz:url>/delSuitMaterialByIndex</ofbiz:url>?materialSuitItemIndex='+materialSuitItemIndex+'&materialSuitIndex='+<ofbiz:field attribute="materialSuitIndex"/>+'&mtrGrp='+'<ofbiz:field attribute="mtrGrp"/>';
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
			Ext.get('materialSuitItemIndex').dom.value=result.materialSuitItemIndex;
			Ext.get('mtrNum').dom.value=result.mtrNum;
			//Ext.get('mtrQty').dom.value=result.mtrQty;
		}
	}
	
	//数据合法性校验
	function checkForm(){
		var mtrNum = Ext.get('mtrNum').dom.value;
		//var mtrQty = Ext.get('mtrQty').dom.value;
		if(mtrNum==""){
			return "请选择物料号";
		}
		//if(mtrQty==""){
		//	return "数量不可以为空";
		//}
		//if(!IsNumeric(mtrQty)){
		//	return "数量必须为整数数字";
		//}
		
		return "";
	}
	
	function querySuit(mtrGrp) {
		doSubmit('<ofbiz:url>/defineSuitMaterialEntry</ofbiz:url>?mtrGrp='+ "'"+mtrGrp+"'");
	}
	
	function doSubmit(url) {
		loading();
		document.defineSuitMaterialForm.action=url;
		document.defineSuitMaterialForm.submit();
	}
</script>


<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form action="<%=request.getContextPath()%>/control/defineSuitMaterial"  method="post" name="defineSuitMaterialForm">
<input name="eventObject" type="hidden" value="" />

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
	  <legend>套件信息</legend>
	  <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
		<ofbiz:if name="mtrGrpList">
			<ofbiz:iterator name="mtrGroup" property="mtrGrpList">
			<tr class="tabletitle">
				<td width="14%" class="en11pxb"><i18n:message key="mcs.material_group" /></td>
				<td class="en11px"><ofbiz:inputvalue entityAttr="mtrGroup" field="mtrGrpDesc"/></td>
			</tr>
			</ofbiz:iterator>
		</ofbiz:if>
		<ofbiz:if name="suitList">
			<ofbiz:iterator name="suit" property="suitList">
			<tr class="tabletitle">
				<td width="14%" class="en11pxb"><i18n:message key="mcs.suit_name" /></td>
				<td class="en11px"><ofbiz:inputvalue entityAttr="suit" field="suitName"/></td>
			</tr>
			<tr class="tabletitle">
				<td width="14%" class="en11pxb"><i18n:message key="mcs.suit_description" /></td>
				<td class="en11px"><ofbiz:inputvalue entityAttr="suit" field="suitDesc" /></td>
			</tr>
			</ofbiz:iterator>
		</ofbiz:if>
	  </table>
  </fieldset></td>
</tr>
</table>

<p>&nbsp;</p>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><fieldset>
		<legend>套件料号列表</legend>
			<table id="objTable1" width="100%" border="0" cellspacing="1" cellpadding="2">
				<tr class="tabletitle">
					<td width="5%" class="en11pxb" align="center">
				    <img src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addSuitMaterial(this);"/>

					</td>
					<td width="14%" class="en11pxb"><i18n:message key="mcs.material_number" /></td>
					<td width="16%" class="en11pxb"><i18n:message key="mcs.material_description" /></td>
<%--					<td width="16%" class="en11pxb"><i18n:message key="mcs.qty" /></td>
--%>				</tr>
				
				<ofbiz:if name="suitMaterialList" size="0">
				<ofbiz:iterator name="cust" property="suitMaterialList" type="java.util.Map">
				<tr class="tablelist" id="objTr1" style="cursor:hand">
					<td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand"  onclick="delSuitMaterial('<ofbiz:entityfield attribute="cust" field="MATERIAL_SUIT_ITEM_INDEX"/>');"/></td>
<%--					<td class="en11px"><a href="#" onclick="editSuitMaterial(this,'<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_SUIT_ITEM_INDEX"/>','<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_SUIT_INDEX"/>')"><ofbiz:entityfield attribute="cust" field="MTR_NUM" /></a></td>

--%>
					    <td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_NUM" /></td>
  					    <td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC" /></td>
<%--					<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_QTY" /></td>
--%>				</tr>
				</ofbiz:iterator>
				</ofbiz:if>
			</table>
		</fieldset></td>
	</tr>
</table>

<p>&nbsp;</p>

<p>
    <ul class="button">
					<li><a class="button-text" href="javascript:querySuit('<ofbiz:field attribute="mtrGrp"/>');"><span>&nbsp;返回&nbsp;</span></a></li>
	</ul>
</p>

</form>

<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">套件料号设定</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="套件料号设定">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/manageSuitMaterial?materialSuitIndex=<ofbiz:field attribute="materialSuitIndex"/>&mtrGrp='<ofbiz:field attribute="mtrGrp"/>'" method="post" id="manageSuitMaterialForm" onsubmit="return false;">
                <input id="materialSuitItemIndex" type="hidden" name="materialSuitItemIndex"/>
                <input id="manageMaterialSuitIndex" type="hidden" name="manageMaterialSuitIndex"/>
                <p>
                <label for="name"><small><i18n:message key="mcs.material_number" /></small><big><i18n:message key="mcs.data_required_red_star" /></big></label>
                <select name="mtrNum" id="mtrNum" class="select">
					<ofbiz:if name="materialNumList">
		        		<ofbiz:iterator name="materialNum" property="materialNumList">
			    			<option value='<ofbiz:inputvalue entityAttr="materialNum" field="materialIndex"/>'><ofbiz:inputvalue entityAttr="materialNum" field="mtrNum"/></option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
				</select>
<%--                <label for="name"><small><i18n:message key="mcs.qty" /></small><big><i18n:message key="mcs.data_required_red_star" /></big></label>
                <input class="textinput" type="text" name="mtrQty" id="mtrQty" value="" size="22" tabindex="1" />
--%>                </p>
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

