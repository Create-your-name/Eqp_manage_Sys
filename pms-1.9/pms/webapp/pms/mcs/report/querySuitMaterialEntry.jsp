<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>

<%@ include file="../../pms/yui-ext/ext-comdlg.jsp"%>

<!-- ##################################### yui page script ################################ -->
<script language="javascript">
var querySuitMatetial = function() {

	var mtrGrpDS, mtrGrpCom;

	return {
		init : function() {
			this.createDataStore();
		    this.createCombox();
		    this.initLoad();
		},

		//设置数据源
		createDataStore : function() {

			mtrGrpDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonMtrGrp</ofbiz:url>?useBySuit=Y'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'mtrGrp'}, {name: 'mtrGrpDesc'}]))
		    });

		},

		createCombox : function() {

			//设置物料组
		    mtrGrpCom = new Ext.form.ComboBox({
			    store: mtrGrpDS,
			    displayField:'mtrGrpDesc',
			    valueField:'mtrGrp',
			    hiddenName:'mtrGrp',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all',
			    allowBlank: false
		    });
		    mtrGrpCom.applyTo('mtrGrpSelect');

		},

		initLoad : function() {
	    	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(<ofbiz:field attribute="mtrGrp"/>); }});
		}
	}
}();

Ext.EventManager.onDocumentReady(querySuitMatetial.init, querySuitMatetial, true);
</script>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
	function querySuit() {
	
		if(Ext.get('mtrGrp').dom.value==""){
			Ext.MessageBox.alert('警告', '请选择物料组!');
			return;
		}
		
		querySuitMatetialForm.submit();
	
	}
	
	function querySuitMaterial(materialSuitIndex,mtrGrp) {
		Ext.getDom('materialSuitIndex').value = materialSuitIndex;
		doSubmit('<ofbiz:url>/querySuitMaterial</ofbiz:url>?materialSuitIndex='+materialSuitIndex+'&mtrGrp='+mtrGrp);

	}

	function doSubmit(url) {
		loading();
		document.querySuitMatetialForm.action=url;
		document.querySuitMatetialForm.submit();
	}
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form action="<%=request.getContextPath()%>/control/querySuitMaterialEntry"  method="post" name="querySuitMatetialForm">
<input name="eventObject" type="hidden" value="" />
<input id="materialSuitIndex" type="hidden" name="materialSuitIndex" value="" />

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>分类条件</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
	    <tr>
    		<td class="tabletitle" width="8%">
    		    <i18n:message key="mcs.material_group" />
    		    <i18n:message key="mcs.data_required_red_star" />
			</td>
			<td width="31%" class="tablelist"><input type="text" size="40" name="mtrGrpSelect" autocomplete="off"/></td>
			<td width="61%" class="tablelist"><ul class="button">
					<li><a class="button-text" href="javascript:querySuit();"><span>&nbsp;查询&nbsp;</span></a></li>
			</ul></td>
	    </tr>
	  </table>
  </fieldset></td>
</tr>
</table>

<p>&nbsp;</p>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>套件列表</legend>
      <table id="objTable1" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      		<td width="34%" class="en11pxb"><i18n:message key="mcs.suit_name" /></td>
      		<td width="61%" class="en11pxb"><i18n:message key="mcs.suit_description" /></td>
    	</tr>

	    <ofbiz:iterator name="cust" property="suitList">
	         <tr class="tablelist" id="objTr1" style="cursor:hand">
	        	<td class="en11px">
				<a href="javascript:querySuitMaterial('<ofbiz:inputvalue entityAttr="cust" field="materialSuitIndex"/>','<ofbiz:field attribute="mtrGrp"/>')"><ofbiz:entityfield attribute="cust" field="suitName" /></a>
				</td>
	        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="suitDesc" /></td>
	        </tr>
	   	</ofbiz:iterator>

      </table>
  </fieldset></td>
</tr>
</table>
<div id="x-dlg">
</div>

</form>
