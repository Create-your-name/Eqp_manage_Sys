<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.math.BigDecimal"%>

<%
// 成本中心
String costCenter = UtilFormatOut.checkNull(request.getParameter("costCenter"));

String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));

List cabinetStockList = (List) request.getAttribute("cabinetStockList");
%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function doSubmit(url) {
	if(Ext.getDom('costCenter').value=='') {
		Ext.MessageBox.alert('警告', '请选择成本中心!');
		return;
	}
	if(Ext.getDom('mtrGrp').value=='') {
		Ext.MessageBox.alert('警告', '请选择物料组!');
		return;
	}
	loading();

	document.MaterialForm.action = url;
	document.MaterialForm.submit();
}

function turnto(url) {
	document.MaterialForm.action = url;
	document.MaterialForm.submit();
}

// <!-- ##################################### yui page script ################################ -->
Ext.onReady(function(){
    //给select控件(aa)应用样式
    var costCenter = new Ext.form.ComboBox({
        typeAhead: true,
        triggerAction: 'all',
        transform:'costCenter',
        width:150,
    	emptyText: 'Select a cost center...',
        forceSelection:true
    });
    costCenter.setValue("<%=costCenter%>");

    // 初始化物料组
	var mtrGrp = new Ext.form.ComboBox({
        typeAhead: true,
        triggerAction: 'all',
        transform:'mtrGrp',
        width:150,
    	emptyText: 'Select a material group...',
        forceSelection:true
    });
    mtrGrp.setValue("<%=mtrGrp%>");
});
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form name="MaterialForm" method="post">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>查询条件</legend>
    	<table width="100%" border="0" cellspacing="1" cellpadding="2">
			<tr bgcolor="#DFE1EC" height="20">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%">成本中心<i18n:message key="mcs.data_required_red_star" /></td>
				<td colspan="3">
					<select id="costCenter" name="costCenter">
						<option value='工程一部'>工程一部</option>
						<option value='工程二部'>工程二部</option>
						<option value='生产制造部'>生产制造部</option>
						<option value='质量保证部'>质量保证部</option>
						<option value='动力保障部'>动力保障部</option>
					</select>
				</td>

				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_group" /><i18n:message key="mcs.data_required_red_star" /></td>
				<td>
					<select id="mtrGrp" name="mtrGrp">
						<option value='20002P'>备件</option>
						<option value='20002S'>耗材</option>
						<option value='100018'>石英</option>
					</select>
				</td>
			</tr>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryCabinetStockEntry</ofbiz:url>')"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset>
    <legend>物料线边仓库存列表</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr class="tabletitle">
				<td class="en11pxb">部门/成本中心</td>
				<td class="en11pxb">PLANT</td>
    		<td class="en11pxb">物料号</td>
    		<td class="en11pxb">物料描述</td>
			<td class="en11pxb">物料组</td>
			<td class="en11pxb">最近领用日期</td>
			<td class="en11pxb">领用人</td>
    		<td class="en11pxb">单价（RMB）</td>
				<td class="en11pxb">库存余量</td>
    		<td class="en11pxb">库存金额（RMB）</td>
    		<td class="en11pxb">库存金额（KHKD）</td>
    	</tr>

		<ofbiz:if name="cabinetStockList">
			<ofbiz:iterator name="cust" property="cabinetStockList" type="java.util.Map">
				<tr bgcolor="#DFE1EC">
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="COST_CENTER"/></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="PLANT"/></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_NUM"/></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC"/></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_GRP"/></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="DOC_TIME"/></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="RECIPIENT"/></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="MOVING_AVERAGE_PRICE"/></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="CNT"/></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="AMOUNT"/></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="MONEY"/></td>
				</tr>
			</ofbiz:iterator>
			<tr bgcolor="#DFE1EC">
					<td class="en11px"></td>
					<td class="en11px"></td>
					<td class="en11px"></td>
					<td class="en11px"></td>
					<td class="en11px"></td>
					<td class="en11px"></td>
					<td class="en11px"></td>
					<td class="en11px"></td>
					<td class="en11px">汇总：</td>
					<td class="en11px"><%=request.getAttribute("stockAmount2")%></td>
					<td class="en11px"><%=request.getAttribute("stockAmount")%></td>
			</tr>
		</ofbiz:if>
    </table>
    </fieldset></td>
  <tr>
</table>
</form>