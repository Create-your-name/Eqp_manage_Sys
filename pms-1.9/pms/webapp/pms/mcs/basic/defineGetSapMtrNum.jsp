<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>


<!-- ##################################### submit script ################################ -->
<script language="javascript">
function doSubmit(url) {
	if(Ext.getDom('mtrNum').value=='') {
		Ext.MessageBox.alert('����', '���������Ϻ�!');
		return;
	}
	document.MtrNumForm.action = url;
	document.MtrNumForm.submit();
}
function doSelect(mtrNum,mtrDesc,mtrGrp,plant){
	window.opener.document.getElementById("mtrNum").value=mtrNum;
	window.opener.document.getElementById("histMtrNum").value=mtrNum;
	window.opener.document.getElementById("mtrDesc").value=mtrDesc;
	window.opener.document.getElementById("mtrGrp").value=mtrGrp;
	window.opener.document.getElementById("plant").value=plant;
    window.close();
}
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form name="MtrNumForm" method="post"  action="<%=request.getContextPath()%>/control/defineMaterial">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>��ѯ���Ϻ�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#DFE1EC" height="30">
    		<td class="en11pxb" bgcolor="#ACD5C9" width="12%">���Ϻ�</td>
			<td><input type="text" size="40" name="mtrNum" autocomplete="off" value="<%=request.getParameter("mtrNum")%>"/></td>
    	</tr>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/getSapMtrNum</ofbiz:url>')"><span>&nbsp;��ѯ&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.MtrNumForm.reset();"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>
<%--ע��ά����ʱ���Ǹ���materialIndex(��������)���й����ֶε�--%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>���Ϻ��б�</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9">
    		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_group" /></td>
			<td class="en11pxb"><i18n:message key="mcs.plant"/></td>
    	</tr>
    	<ofbiz:if name="mtrNumList">
			<ofbiz:iterator name="cust" property="mtrNumList" type="java.util.Map">
			<tr bgcolor="#DFE1EC">
			 	<td class="en11px"><a href="#" onclick="doSelect('<ofbiz:entityfield attribute="cust" field="MTR_NUM"/>','<ofbiz:entityfield attribute="cust" field="MTR_DESC"/>','<ofbiz:entityfield attribute="cust" field="MTR_GRP"/>','<%=Constants.PLANT%>')"><ofbiz:entityfield attribute="cust" field="MTR_NUM"/></a></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_GRP"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="PLANT"/></td>
			</tr>
		    </ofbiz:iterator>
	    </ofbiz:if>
      </table>
	 </fieldset>
	</td>
  <tr>

</table>
</form>
