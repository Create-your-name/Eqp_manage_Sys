<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
	function cancel() {
		history.back();
	}
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>
	      特殊寿命规范
	 </legend>
      <table id="objTable1" width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb" >
      	<tr class="tabletitle">
			<td width="30%" class="en11pxb">物料号</td>
      		<td width="30%" class="en11pxb">设备</td>
      		<td width="40%" class="en11pxb">备件寿命(天)</td>
    	</tr>

	    <ofbiz:iterator name="cust" property="timeList">
	         <tr class="tablelist" id="objTr1">
	        	<td><ofbiz:entityfield attribute="cust" field="mtrNum" /></td>
	        	<td><ofbiz:entityfield attribute="cust" field="usingObjectId" /></td>
	        	<td><ofbiz:entityfield attribute="cust" field="usableTimeLimit" /></td>
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
			<li><a class="button-text" href="javascript:cancel();"><span>&nbsp;返回&nbsp;</span></a></li>
		</ul>
	</td>
  </tr>
</table>