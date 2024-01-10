<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>


<%
String nullvalue = null;
List statusList = (List )request.getAttribute("statusList");
List materialStatusList = (List) request.getAttribute("materialStatusList");
//String s1=(String)statusList.get(1);
//out.print(s1);
//Date testDate = new Date();

//testDate.setTime(testDate.getTime()-(long)30*24*60*60*1000);
//SimpleDateFormat testdf=new SimpleDateFormat("yyyy-MM-dd");
//   String test=testdf.format(testDate);
//out.print(test);

%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function doSubmit(url) {
//	if(Ext.getDom('deptIndex').value=='') {
//		Ext.MessageBox.alert('警告', '请选部门!');
//		return;
//	}
	//loading();
	document.MaterialForm.action = url;
	document.MaterialForm.submit();
}

//function cancel(url) {
//	if (window.opener != null) {
//	    window.close();
//	} else {
//	    history.back();
//	}
//}

</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form name="MaterialForm" method="post" >

<%--注意维护的时候是更具materialIndex(物料索引)进行关联字段的--%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>物料使用详细信息</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9" height="13">
			<td class="en11pxb"><i18n:message key="mcs.index_alias_name" /></td>
			<td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
			<td class="en11pxb"><i18n:message key="mcs.doc_time" /></td>
			<td class="en11pxb"><i18n:message key="mcs.dept" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_group" /></td>
			<td class="en11pxb"><i18n:message key="mcs.status" /></td>
			<td class="en11pxb"><i18n:message key="mcs.equipment" /></td>
			<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date"/></td>
			<td class="en11pxb"><i18n:message key="mcs.mrb_date" /></td>
    	</tr>

    	<ofbiz:if name="statusDetailList">
			<ofbiz:iterator name="cust" property="statusDetailList"  type="java.util.Map">
			<tr bgcolor="#DFE1EC">
			 	<td class="en11px"><ofbiz:entityfield attribute="cust" field="ALIAS_NAME"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="VENDOR_BATCH_NUM"  /></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="DOC_TIME"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="EQUIPMENT_DEPT"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_NUM"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_GRP"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="DESCRIPTION"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="USING_OBJECT_ID"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="SHELF_LIFE_EXPIRATION_DATE"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MRB_DATE"/></td>
		    </ofbiz:iterator>
	    </ofbiz:if>


      </table>
	 </fieldset>
	</td>
  <tr>
</table>

<br>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>物料使用历史信息</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9" height="13">
<%--		<td class="en11pxb"><i18n:message key="mcs.doc_time" /></td>
			<td class="en11pxb"><i18n:message key="mcs.dept" /></td>
			<td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
--%>
            <td class="en11pxb">NO.</td>
			<td class="en11pxb"><i18n:message key="mcs.status" /></td>
			<td class="en11pxb"><i18n:message key="mcs.equipment" /></td>
			<td class="en11pxb"><i18n:message key="mcs.operator" /></td>
			<td class="en11pxb"><i18n:message key="mcs.operation_time" /></td>
			<td class="en11pxb"><i18n:message key="mcs.note" /></td>
    	</tr>

    	<%int i=1;%>
    	<ofbiz:if name="statusHistList">
			<ofbiz:iterator name="cust" property="statusHistList"  type="java.util.Map">
			<tr bgcolor="#DFE1EC">
<%--
                <td class="en11px"><a href="<ofbiz:url>/queryMaterialStatusHist?materialStatusIndex=<ofbiz:entityfield attribute="cust" field="MATERIAL_STATUS_INDEX"/></ofbiz:url>"><ofbiz:entityfield attribute="cust" field="ALIAS_NAME"/></a></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="DOC_TIME"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="VENDOR_BATCH_NUM"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_NUM"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC"/></td>
--%>
                <td class="en11px"><%=i++%></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="DESCRIPTION"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="USING_OBJECT_ID"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="TRANS_BY"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="UPDATE_TIME"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="NOTE"/></td>
		    </ofbiz:iterator>
	    </ofbiz:if>

      </table>
	 </fieldset>
	</td>
  <tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td>
<%--		<ul class="button">
				<li><a class="button-text" href="#" onclick="javascript:cancel('<ofbiz:url>/queryMaterialStatusDetail</ofbiz:url>')"><span>&nbsp;返回&nbsp;</span></a></li>
		</ul>--%>
  	    <ul class="button">
				<li><a class="button-text" href="#" onclick="javascript:window.close();"><span>&nbsp;关闭&nbsp;</span></a></li>
		</ul>
	</td>
  </tr>
</table>

<input type="hidden"  id="startDate" NAME="startDate" value="<%=UtilFormatOut.checkNull(request.getParameter("startDate"))%>" >
<input type="hidden"  id="endDate" NAME="endDate" value="<%=UtilFormatOut.checkNull(request.getParameter("endDate"))%>">
<input type="hidden"  id="deptIndex" name="deptIndex" value="<%=UtilFormatOut.checkNull(request.getParameter("deptIndex"))%>" >
<input type="hidden"  id="mtrGrp" name="mtrGrp" value="<%=UtilFormatOut.checkNull(request.getParameter("mtrGrp"))%>" />
<input type="hidden"  id="mtrNum"  name="mtrNum" value="<%=UtilFormatOut.checkNull(request.getParameter("mtrNum"))%>" />
<input type="hidden"  id="status" name="status" value="<%=UtilFormatOut.checkNull(request.getParameter("status"))%>"/>

</form>