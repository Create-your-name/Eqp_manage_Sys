<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

<%
String startDate = UtilFormatOut.checkNull(request.getParameter("startDate"));
String endDate = UtilFormatOut.checkNull(request.getParameter("endDate"));
String deptIndex = UtilFormatOut.checkNull(request.getParameter("deptIndex"));

if (deptIndex == "") {
	deptIndex = UtilFormatOut.checkNull((String) request.getAttribute("deptIndex"));
}

SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
Date d = new Date();
if (endDate == "") {
    endDate = df.format(d);
}

if (startDate == "") {
    d.setDate(d.getDate() - 30);
    startDate = df.format(d);
}
%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function doSubmit(url) {
//	if(Ext.getDom('deptIndex').value=='') {
//		Ext.MessageBox.alert('����', '��ѡ����!');
//		return;
//	}
	//loading();
	document.MaterialForm.action = url;
	document.MaterialForm.submit();
}

function turnto(url) {
    //window.navigate(url);
	document.MaterialForm.action = url;
	document.MaterialForm.submit();
}

function cancel(url) {
    window.navigate(url);
	//����������ݾ���Ϊ�˰����ݴ�������ҳ��
	//document.MaterialForm.action = url;
	//document.MaterialForm.submit();
}
<!-- ##################################### yui page script ################################ -->
Ext.onReady(function(){
       //��ʽ������
	   var startDate = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:true
	    });

	 	var endDate = new Ext.form.DateField({
	 		format: 'Y-m-d',
	        allowBlank:true
	    });

	    startDate.applyTo('startDate');
	    endDate.applyTo('endDate');

        //��select�ؼ�(aa)Ӧ����ʽ
		equipmentDept = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'deptIndex',
	        width:150,
			emptyText: 'Select a equipmentDept...',
	        forceSelection:true
	    });
	    equipmentDept.setValue("<%=deptIndex%>");

        // ��ʼ��������
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

		var mtrGrp = '<%=UtilFormatOut.checkNull(request.getParameter("mtrGrp"))%>';
		mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(mtrGrp); }});
});
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form name="MaterialForm" method="post" >
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>��ѯ����</legend>
    	<table width="100%" border="0" cellspacing="1" cellpadding="2">
		    <tr bgcolor="#DFE1EC" height="30">
	     		<tr bgcolor="#DFE1EC">
	      		 <td width="12%" bgcolor="#ACD5C9" class="en11pxb">��������:</td>
	      		 <td width="28%"><input type="text" ID="startDate" NAME="startDate" value="<%=startDate%>" readonly></td>
	   			 <td width="9%" bgcolor="#ACD5C9" class="en11pxb">��:</td>
	    		 <td width="28%"><input type="text" ID="endDate" NAME="endDate" value="<%=endDate%>"  readonly></td>
	    	</tr>
			<tr bgcolor="#DFE1EC" height="12">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.dept" /></td>
				<td nowrap="nowrap" >
					<select id="deptIndex" name="deptIndex">
						<option value=''></option>
						<ofbiz:if name="deptList">
							<ofbiz:iterator name="cust" property="deptList">
								<option value='<ofbiz:inputvalue entityAttr="cust" field="deptIndex"/>'>
									<ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>
								</option>

							</ofbiz:iterator>
						</ofbiz:if>
					</select>
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%">
					<i18n:message key="mcs.vendor_batch_num" />
				</td>
				<td ><input type="text" size="20" name="vendorBatchNum" autocomplete="off" value="<%=UtilFormatOut.checkNull(request.getParameter("vendorBatchNum"))%>"/>
				</td>
			</tr>
			<tr bgcolor="#DFE1EC" height="30">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_group" /></td>
				<td><input type="text" size="20" name="mtrGrpSelect" autocomplete="off"/></td>
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_number" /></td>
				<td ><input type="text"  size="20" name="mtrNum" autocomplete="off" value="<%=UtilFormatOut.checkNull(request.getParameter("mtrNum"))%>"/></td>
			</tr>
			<tr bgcolor="#DFE1EC" height="30">
				<td class="en11pxb"  bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.status" /></td>

				<td colspan="3">
					<select   name="status" id="status" >
						<option <% if ( (UtilFormatOut.checkNull(request.getParameter("status"))).equals("STO-REQ") ) { %> selected <% } %> value="STO-REQ">������</option>
						<option <% if ( (UtilFormatOut.checkNull(request.getParameter("status"))).equals("STO-OUT") ) { %> selected <% } %> value="STO-OUT">δ�ݴ�</option>
						<option <% if ( (UtilFormatOut.checkNull(request.getParameter("status"))).equals("STO-IN") ) { %> selected <% } %> value="STO-IN">���ݴ�</option>
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
			<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryStoReqEntry</ofbiz:url>')"><span>&nbsp;��ѯ&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.MaterialForm.reset();"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" onclick="javascript: history.back();" href="#"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<%--ע��ά����ʱ���Ǹ���materialIndex(��������)���й����ֶε�--%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>���ϵ���ϸ��Ϣ</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9" height="13">
			<td class="en11pxb"><i18n:message key="mcs.doc_time" /></td>
			<td class="en11pxb">�ɱ�����</td>
			<td class="en11pxb">�ջ�����</td>
			<td class="en11pxb"><i18n:message key="mcs.recipient" /></td>
			<td class="en11pxb">����ԭ��</td>
			<td class="en11pxb"><i18n:message key="mcs.material_group" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
			<td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
			<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date"/></td>
			<td class="en11pxb"><i18n:message key="mcs.qty" /></td>
			<td class="en11pxb"><i18n:message key="mcs.confirm_qty" /></td>
			<td class="en11pxb">����</td>
			<td class="en11pxb">�����ܽ��</td>
    	</tr>

    	<ofbiz:if name="stoReqList">
			<ofbiz:iterator name="cust" property="stoReqList"  type="java.util.Map">
			<tr bgcolor="#DFE1EC">
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="DOC_TIME"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="COST_CENTER"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="ACCOUNT_DEPT"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="ACCOUNT_NAME"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="REASON_FOR_MOVEMENT"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_GRP"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_NUM"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="VENDOR_BATCH_NUM"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="SHELF_LIFE_EXPIRATION_DATE"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="QTY"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="ACTIVE_QTY"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="AVG_PRICE"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="SUM_PRICE"/></td>
		    </ofbiz:iterator>
	    </ofbiz:if>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>
</form>