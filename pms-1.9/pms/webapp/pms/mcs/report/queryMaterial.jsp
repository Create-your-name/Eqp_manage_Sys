<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>


<%
	GenericValue materialInfo = (GenericValue) request.getAttribute("materialInfo");
%>
<!-- ##################################### submit script ################################ -->
<script language="javascript">

	function cancel(url) {
		history.back();
		//document.MaterialForm.action = url;
		//document.MaterialForm.submit();
	}
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form name="MaterialForm"  method="POST">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
 	<%--���ڰѵڶ���ҳ������ݴ��ص���һ��ҳ��ȥ--%>
 	<input type="hidden" name="mtrNum" id="mtrNum" value="<%=UtilFormatOut.checkNull(request.getParameter("mtrNum"))%>">
 	<input type="hidden" name="deptIndex"  id="deptIndex" value="<%=UtilFormatOut.checkNull(request.getParameter("deptIndex"))%>">
	<input type="hidden" name="mtrGrp" id="mtrGrp" value="<%=UtilFormatOut.checkNull(request.getParameter("mtrGrp"))%>">
	<input type="hidden" name="usingObjectId" id="usingObjectId" value="<%=UtilFormatOut.checkNull(request.getParameter("usingObjectId"))%>">
  <tr>
	<td valign="top"> <fieldset> <legend>������Ϣ��ϸ</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">
        <tr bgcolor="#DFE1EC">
          <td width="18%" bgcolor="#ACD5C9"><i18n:message key="mcs.material_number" /></td>
    	  <td width="36%"><%=UtilFormatOut.checkNull(materialInfo.getString("mtrNum"))%></td>
          <td width="17%" bgcolor="#ACD5C9"><i18n:message key="mcs.material_description" /></td>
          <td width="29%"><%=UtilFormatOut.checkNull(materialInfo.getString("mtrDesc"))%></td>
		</tr>

		<tr bgcolor="#DFE1EC">
          <td width="18%" bgcolor="#ACD5C9"><i18n:message key="mcs.material_group" /></td>
    	  <td width="36%"><%=UtilFormatOut.checkNull(materialInfo.getString("mtrGrp"))%></td>
		  <td width="18%" bgcolor="#ACD5C9"><i18n:message key="mcs.plant" /></td>
		  <td width="36%"><%=Constants.PLANT%></td>
	    </tr>

	    <tr bgcolor="#DFE1EC">
		  <td width="18%" bgcolor="#ACD5C9"><i18n:message key="mcs.need_scrap_store" /></td>
		  <td width="36%"><%=UtilFormatOut.checkNull(materialInfo.getString("needScrapStore"))%></td>

		  <td width="18%" bgcolor="#ACD5C9">��ǰ��������</td>
		  <td width="36%"><%=UtilFormatOut.checkNull(materialInfo.getString("preStoNumber"))%></td>
		</tr>

		<tr bgcolor="#DFE1EC">
		  <td width="18%" bgcolor="#ACD5C9"><i18n:message key="mcs.usable_time_limit" /></td>
		  <td width="36%"><%=UtilFormatOut.checkNull(materialInfo.getString("usableTimeLimit"))%></td>

		  <td width="18%" bgcolor="#ACD5C9">����������ǰ��������(��)</td>
		  <td width="36%"><%=UtilFormatOut.checkNull(materialInfo.getString("preAlarmDays"))%></td>
		</tr>

		<tr bgcolor="#DFE1EC">
		    <td width="18%" bgcolor="#ACD5C9"><i18n:message key="mcs.frozen_time_limit" /></td>
		    <td width="36%"><%=UtilFormatOut.checkNull(materialInfo.getString("frozenTimeLimit"))%></td>

		    <td width="18%" bgcolor="#ACD5C9"><i18n:message key="mcs.max_frozen_time_limit" /></td>
		    <td width="36%"><%=UtilFormatOut.checkNull(materialInfo.getString("maxFrozenTimeLimit"))%></td>
		</tr>

		<tr bgcolor="#DFE1EC">
		    <td width="18%" bgcolor="#ACD5C9">�Ƿ�����MCS�ܿ�</td>
		    <td width="36%"><%=UtilFormatOut.checkNull(materialInfo.getString("inControl"))%></td>

		    <td width="18%"></td>
		    <td width="36%"></td>
		</tr>

		<%--
		<tr bgcolor="#DFE1EC">
		    <td width="18%"  bgcolor="#ACD5C9"  ><i18n:message key="mcs.need_vendor_recycle" /></td>
		    <td width="36%"><%//=UtilFormatOut.checkNull(materialInfo.getString("needVendorRecycle"))%></td>
		</tr>
		--%>
	  </table>
    </fieldset>
  </tr>
</table>

<br />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>
	      �����豸��Ϣ��ϸ
	 </legend>
      <table id="objTable1" width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb" >
      	<tr class="tabletitle">
			<td width="20%" class="en11pxb">�����豸</td>
			<td class="en11pxb">�����豸ʹ������(��)</td>
      		<td class="en11pxb">�豸ͬʱʹ���������</td>
      		<td class="en11pxb">�豸��׼����</td>
    	</tr>

	    <ofbiz:iterator name="cust" property="mtrObjectList">
	         <tr class="tablelist" id="objTr1" style="cursor:hand">
	        	<td class="en11pxb" ><ofbiz:entityfield attribute="cust" field="usingObjectId" /></td>
	        	<td class="en11pxb" ><ofbiz:entityfield attribute="cust" field="usableTimeLimit" /></td>
	        	<td class="en11pxb" ><ofbiz:entityfield attribute="cust" field="objMaxUseAmount" /></td>
	        	<td class="en11pxb" ><ofbiz:entityfield attribute="cust" field="stdUseAmount" /></td>
	        </tr>
	   	</ofbiz:iterator>

      </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td>
		<ul class="button">
				<li><a class="button-text" href="javascript:cancel('<ofbiz:url>/queryMaterialEntry</ofbiz:url>')"><span>&nbsp;����&nbsp;</span></a></li>
		</ul>
	</td>
  </tr>
</table>
</form>