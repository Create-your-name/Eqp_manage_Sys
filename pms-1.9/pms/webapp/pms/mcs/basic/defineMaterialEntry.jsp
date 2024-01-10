<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>


<%
    String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
    boolean isSparePart = (ConstantsMcs.SPAREPART_2P.equals(mtrGrp) || ConstantsMcs.SPAREPART_2S.equals(mtrGrp));
%>


<!-- ##################################### submit script ################################ -->
<script language="javascript">
function doSubmit(url) {
	if(Ext.getDom('mtrGrp').value=='') {
		Ext.MessageBox.alert('警告', '请选择物料组!');
		return;
	}
	loading();
	document.MaterialForm.action = url;
	document.MaterialForm.submit();
}

<!-- ##################################### yui page script ################################ -->
Ext.onReady(function(){


	var mtrGrpDS = new Ext.data.Store({
	    proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonMtrGrp</ofbiz:url>'}),
	    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'mtrGrpDesc'},{name: 'mtrGrp'}]))
	 });


	var mtrGrpCom = new Ext.form.ComboBox({
	    store: mtrGrpDS,
	    displayField:'mtrGrpDesc',
	    valueField:'mtrGrp',
	    hiddenName:'mtrGrp',
	    typeAhead: true,
	    mode: 'local',
	    width: 170,
	    triggerAction: 'all'
	});
	mtrGrpCom.applyTo('mtrGrpSelect');

	var mtrGrp = '<%=mtrGrp%>';
	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(mtrGrp); }});
});
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form name="MaterialForm" method="post" action="<%=request.getContextPath()%>/control/defineMaterial">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>查询物料组信息</legend>
    	<table width="100%" border="0" cellspacing="1" cellpadding="2">
			<tr bgcolor="#DFE1EC" height="30">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%">物料组<i18n:message key="mcs.data_required_red_star" /></td>
				<td><input type="text" size="30" name="mtrGrpSelect" autocomplete="off"/></td>
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_number" /></td>
				<td ><input type="text"  size="30" name="mtrNum"  value="<%=UtilFormatOut.checkNull(request.getParameter("mtrNum"))%>"/></td>
			</tr>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/defineMaterialEntry</ofbiz:url>')"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.MaterialForm.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
	<td><ul class="button">
			<li><a class="button-text" href="<ofbiz:url>/defineMaterial</ofbiz:url>"><span>&nbsp;新增&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<%int i = 1;%>

<%--注意维护的时候是按materialIndex(物料索引)进行关联字段的--%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>待维护 物料列表</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9">
    	    <td class="en11pxb">No.</td>
    		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_group" /></td>
    	</tr>
    	<ofbiz:if name="disenableMaterialList">
			<ofbiz:iterator name="cust" property="disenableMaterialList" type="java.util.Map">
			<tr bgcolor="#DFE1EC">
			    <td class="en11px"><%=i++%></td>
			 	<td class="en11px"><a href="<ofbiz:url>/defineMaterial?materialIndex=<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_INDEX" tryEntityAttr="true" /></ofbiz:url>"><ofbiz:entityfield attribute="cust" field="MTR_NUM"/></a></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_GRP"/></td>
		    </ofbiz:iterator>
	    </ofbiz:if>
      </table>
	 </fieldset>
	</td>
  </tr>

  <tr><td>&nbsp;</td></tr>

  <%i = 1;%>
  <tr>
    <td><fieldset><legend>已维护 物料列表</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9">
    	    <td class="en11pxb">No.</td>
    		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_group" /></td>
			<%if (isSparePart) {%>
    			<td class="en11pxb">使用寿命(天)</td>
    			<td class="en11pxb">提前报警天数(天)</td>
    			<td class="en11pxb">提前领用数量</td>
    	    <%}%>
    	</tr>
    	<ofbiz:if name="enableMaterialList">
			<ofbiz:iterator name="cust" property="enableMaterialList" type="java.util.Map">
			<tr bgcolor="#DFE1EC">
			    <td class="en11px"><%=i++%></td>
			 	<td class="en11px"><a href="<ofbiz:url>/defineMaterial?materialIndex=<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_INDEX" tryEntityAttr="true" /></ofbiz:url>"><ofbiz:entityfield attribute="cust" field="MTR_NUM"/></a></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_GRP"/></td>
				<%if (isSparePart) {%>
    				<td class="en11px"><ofbiz:entityfield attribute="cust" field="USABLE_TIME_LIMIT"/></td>
    				<td class="en11px"><ofbiz:entityfield attribute="cust" field="PRE_ALARM_DAYS"/></td>
    				<td class="en11px"><ofbiz:entityfield attribute="cust" field="PRE_STO_NUMBER"/></td>
				<%}%>
			</tr>
		    </ofbiz:iterator>
	    </ofbiz:if>
      </table>
	 </fieldset>
	</td>
  </tr>

  <tr><td>&nbsp;</td></tr>

  <%i = 1;%>
  <tr>
    <td><fieldset><legend>已禁用 物料列表</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9">
    	    <td class="en11pxb">No.</td>
    		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
			<td class="en11pxb"><i18n:message key="mcs.material_group" /></td>
			<%if (isSparePart) {%>
    			<td class="en11pxb">使用寿命(天)</td>
    			<td class="en11pxb">提前报警天数(天)</td>
    			<td class="en11pxb">提前领用数量</td>
    	    <%}%>
    	</tr>
    	<ofbiz:if name="outOfControlMaterialList">
			<ofbiz:iterator name="cust" property="outOfControlMaterialList" type="java.util.Map">
			<tr bgcolor="#DFE1EC">
			    <td class="en11px"><%=i++%></td>
			 	<td class="en11px"><a href="<ofbiz:url>/defineMaterial?materialIndex=<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_INDEX" tryEntityAttr="true" /></ofbiz:url>"><ofbiz:entityfield attribute="cust" field="MTR_NUM"/></a></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_GRP"/></td>
				<%if (isSparePart) {%>
    				<td class="en11px"><ofbiz:entityfield attribute="cust" field="USABLE_TIME_LIMIT"/></td>
    				<td class="en11px"><ofbiz:entityfield attribute="cust" field="PRE_ALARM_DAYS"/></td>
    				<td class="en11px"><ofbiz:entityfield attribute="cust" field="PRE_STO_NUMBER"/></td>
    		    <%}%>
			</tr>
		    </ofbiz:iterator>
	    </ofbiz:if>
      </table>
	 </fieldset>
	</td>
  </tr>
</table>
</form>