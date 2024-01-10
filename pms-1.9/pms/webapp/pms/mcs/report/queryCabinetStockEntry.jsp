<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.math.BigDecimal"%>

<%
// �ɱ�����
String costCenter = UtilFormatOut.checkNull(request.getParameter("costCenter"));

String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));

List cabinetStockList = (List) request.getAttribute("cabinetStockList");
%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function doSubmit(url) {
	if(Ext.getDom('costCenter').value=='') {
		Ext.MessageBox.alert('����', '��ѡ��ɱ�����!');
		return;
	}
	if(Ext.getDom('mtrGrp').value=='') {
		Ext.MessageBox.alert('����', '��ѡ��������!');
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
    //��select�ؼ�(aa)Ӧ����ʽ
    var costCenter = new Ext.form.ComboBox({
        typeAhead: true,
        triggerAction: 'all',
        transform:'costCenter',
        width:150,
    	emptyText: 'Select a cost center...',
        forceSelection:true
    });
    costCenter.setValue("<%=costCenter%>");

    // ��ʼ��������
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
    <td><fieldset><legend>��ѯ����</legend>
    	<table width="100%" border="0" cellspacing="1" cellpadding="2">
			<tr bgcolor="#DFE1EC" height="20">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%">�ɱ�����<i18n:message key="mcs.data_required_red_star" /></td>
				<td colspan="3">
					<select id="costCenter" name="costCenter">
						<option value='����һ��'>����һ��</option>
						<option value='���̶���'>���̶���</option>
						<option value='�������첿'>�������첿</option>
						<option value='������֤��'>������֤��</option>
						<option value='�������ϲ�'>�������ϲ�</option>
					</select>
				</td>

				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_group" /><i18n:message key="mcs.data_required_red_star" /></td>
				<td>
					<select id="mtrGrp" name="mtrGrp">
						<option value='20002P'>����</option>
						<option value='20002S'>�Ĳ�</option>
						<option value='100018'>ʯӢ</option>
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
		<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryCabinetStockEntry</ofbiz:url>')"><span>&nbsp;��ѯ&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset>
    <legend>�����ֿ߲߱���б�</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr class="tabletitle">
				<td class="en11pxb">����/�ɱ�����</td>
				<td class="en11pxb">PLANT</td>
    		<td class="en11pxb">���Ϻ�</td>
    		<td class="en11pxb">��������</td>
			<td class="en11pxb">������</td>
			<td class="en11pxb">�����������</td>
			<td class="en11pxb">������</td>
    		<td class="en11pxb">���ۣ�RMB��</td>
				<td class="en11pxb">�������</td>
    		<td class="en11pxb">����RMB��</td>
    		<td class="en11pxb">����KHKD��</td>
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
					<td class="en11px">���ܣ�</td>
					<td class="en11px"><%=request.getAttribute("stockAmount2")%></td>
					<td class="en11px"><%=request.getAttribute("stockAmount")%></td>
			</tr>
		</ofbiz:if>
    </table>
    </fieldset></td>
  <tr>
</table>
</form>