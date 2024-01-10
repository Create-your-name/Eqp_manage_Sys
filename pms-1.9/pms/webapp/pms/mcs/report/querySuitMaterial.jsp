<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>

<%@ include file="../../pms/yui-ext/ext-comdlg.jsp"%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
	function querySuit(mtrGrp) {
		doSubmit('<ofbiz:url>/querySuitMaterialEntry</ofbiz:url>?mtrGrp='+mtrGrp);
	}
	
	function doSubmit(url) {
		loading();
		document.querySuitMaterialForm.action=url;
		document.querySuitMaterialForm.submit();
	}
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form action="<%=request.getContextPath()%>/control/querySuitMaterial"  method="post" name="querySuitMaterialForm">
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
					<td width="14%" class="en11pxb"><i18n:message key="mcs.material_number" /></td>
					<td width="16%" class="en11pxb"><i18n:message key="mcs.material_description" /></td>
<%--					<td width="16%" class="en11pxb"><i18n:message key="mcs.qty" /></td>
--%>				</tr>
				
				<ofbiz:if name="suitMaterialList" size="0">
				<ofbiz:iterator name="cust" property="suitMaterialList" type="java.util.Map">
				<tr class="tablelist" id="objTr1" style="cursor:hand">
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
</div>

