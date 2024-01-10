<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>

<%@ include file="../../pms/yui-ext/ext-comdlg.jsp"%>

<!-- ##################################### yui page script ################################ -->
<script language="javascript">
var defineSuitMatetial = function() {

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
	    	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue('<ofbiz:field attribute="mtrGrp"/>'); }});
		}
	}
}();

Ext.EventManager.onDocumentReady(defineSuitMatetial.init, defineSuitMatetial, true);
</script>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
	function querySuit() {
	
		if(Ext.get('mtrGrp').dom.value==""){
			Ext.MessageBox.alert('警告', '请选择物料组!');
			return;
		}
		
		defineSuitMatetialForm.submit();
	
	}
	
	//改变弹出框大小width,height
	extDlg.dlgInit('500','300');
	
	//新增功能弹出页
	function addSuit(obj){
		Ext.get('materialSuitIndex').dom.value="";
		Ext.get('addMtrGrp').dom.value='<ofbiz:field attribute="mtrGrp"/>';
		Ext.get('suitName').dom.value="";
		Ext.get('suitDesc').dom.value="";
		extDlg.showAddDialog(obj);
	}
		
	//删除
	function delSuit(materialSuitIndex){
		Ext.MessageBox.confirm('删除确认', '您确信要删除此记录吗？',function result(value){
			if(value=="yes"){
				var url='<ofbiz:url>/delSuitByIndex</ofbiz:url>?materialSuitIndex='+materialSuitIndex;
				document.location=url;
			}else{
				return;
			}
		});
	}
	
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('suitName').dom.value=result.suitName;
			Ext.get('suitDesc').dom.value=result.suitDesc;
		}
	}
	
	//数据合法性校验
	function checkForm(){
		var suitName = Ext.get('suitName').dom.value;
		var suitDesc = Ext.get('suitDesc').dom.value;
		if(suitName==""){
			return "套件名称不能为空";
		}
		if(suitDesc==""){
			return "套件描述不能为空";
		}
		
		return "";
	}

	function defineSuitMaterial(materialSuitIndex,mtrGrp) {
		Ext.getDom('materialSuitIndex').value = materialSuitIndex;
		doSubmit('<ofbiz:url>/defineSuitMaterial</ofbiz:url>?materialSuitIndex='+materialSuitIndex+'&mtrGrp='+mtrGrp);

	}

	function doSubmit(url) {
		loading();
		document.defineSuitMatetialForm.action=url;
		document.defineSuitMatetialForm.submit();
	}
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form action="<%=request.getContextPath()%>/control/defineSuitMaterialEntry"  method="post" name="defineSuitMatetialForm">
<input name="eventObject" type="hidden" value="" />

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
            <td width="5%" class="en11pxb" align="center"><img src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addSuit(this);"/></td>
      		<td width="34%" class="en11pxb"><i18n:message key="mcs.suit_name" /></td>
      		<td width="61%" class="en11pxb"><i18n:message key="mcs.suit_description" /></td>
    	</tr>

	    <ofbiz:iterator name="cust" property="suitList">
	         <tr class="tablelist" id="objTr1" style="cursor:hand">
		        <td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand"  onclick="delSuit('<ofbiz:inputvalue entityAttr="cust" field="materialSuitIndex"/>');"/></td>
	        	<td class="en11px">
				<a href="javascript:defineSuitMaterial('<ofbiz:inputvalue entityAttr="cust" field="materialSuitIndex"/>','<ofbiz:field attribute="mtrGrp"/>')"><ofbiz:entityfield attribute="cust" field="suitName" /></a>
				</td>
	        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="suitDesc" /></td>
	        </tr>
	   	</ofbiz:iterator>

      </table>
  </fieldset></td>
</tr>
</table>
</form>

<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">套件名称设定</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="套件名称设定">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/addSuit" method="post" id="addSuitForm" onsubmit="return false;">
                	<input id="materialSuitIndex" type="hidden" name="materialSuitIndex" value="" />
                	<input id="addMtrGrp" type="hidden" name="addMtrGrp" />
                	<input id="test" type="hidden" name="test" value="test" />
                <p>
                <label for="name"><small><i18n:message key="mcs.suit_name" /></small><big><i18n:message key="mcs.data_required_red_star" /></big></label>
                <input class="textinput" type="text" name="suitName" id="suitName" value="" size="22" tabindex="1" />
                <label for="name"><small><i18n:message key="mcs.suit_description" /></small><big><i18n:message key="mcs.data_required_red_star" /></big></label>
                <input class="textinput" type="text" name="suitDesc" id="suitDesc" value="" size="22" tabindex="1" />
                </p>
                </form>
            </div>
        </div>
    </div>
    <div class="x-dlg-ft">
        <div id="dlg-msg">
            <span id="post-error" class="posting-msg"><img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" />&nbsp;<span id="post-error-msg"></span></span>
        </div>
    </div>
</div>

