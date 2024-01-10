<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<%@ include file="../../pms/yui-ext/ext-comdlg.jsp"%>

<%

    String eqpId = UtilFormatOut.checkNull(request.getParameter("equipmentId"));
    String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
    if ("".equals(mtrGrp)) {
        //默认选择石英
        mtrGrp = "100018";
    }

%>

<script language="javascript">
var useMaterialById = function() {
    var eqpIdDS, eqpIdCom;
	var mtrGrpDS, mtrGrpCom;
	var mtrNumDS, mtrNumCom;

	return {
		init : function() {
			this.createDataStore();
		    this.createCombox();
		    this.initLoad();
		},

		//设置数据源
		createDataStore : function() {

            eqpIdDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentByDeptIndex</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}, {name: 'equipmentId'}]))
		    });

			mtrGrpDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonMtrGrp</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'mtrGrp'}, {name: 'mtrGrpDesc'}]))
		    });

		    mtrNumDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonMtrNum</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'mtrNum'}, {name: 'mtrDesc'}]))
		    });
		},

		createCombox : function() {

		    //设置eqpId
			eqpIdCom = new Ext.form.ComboBox({
			    store: eqpIdDS,
			    displayField:'equipmentId',
			    valueField:'equipmentId',
			    hiddenName:'equipmentId',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all',
			    allowBlank: false
		    });
		    eqpIdCom.applyTo('eqpIdSelect');

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
		    mtrGrpCom.on('select', this.loadMtrNum);

		    //设置物料号
		    mtrNumCom = new Ext.form.ComboBox({
			    store: mtrNumDS,
			    displayField:'mtrNum',
			    valueField:'mtrNum',
			    hiddenName:'mtrNum',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all',
			    allowBlank: false
		    });
		    mtrNumCom.applyTo('mtrNumSelect');
		    mtrNumCom.on('select', queryMaterial);
		},

		initLoad : function() {

		    var eqpId = '<%=eqpId%>';
		    eqpIdDS.load({params:{deptIndex:''}, callback:function(){ eqpIdCom.setValue(eqpId); }});

			var mtrGrp = '<%=mtrGrp%>';
	    	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(mtrGrp); }});

	    	var mtrNum = '<%=UtilFormatOut.checkNull(request.getParameter("mtrNum"))%>';
	    	mtrNumDS.load({params:{mtrGrp:mtrGrp},callback:function(){ mtrNumCom.setValue(mtrNum); }});
		},

		loadMtrNum : function() {
			var mtrGrp = mtrGrpCom.getValue();
			var mtrNum  = '';
	 		mtrNumDS.load({params:{mtrGrp:mtrGrp},callback:function(){ mtrNumCom.setValue(mtrNum); }});
	 	}
	}
}();

Ext.EventManager.onDocumentReady(useMaterialById.init, useMaterialById, true);

</script>


<!-- ##################################### submit script ################################ -->
<script language="javascript">

function addVendorMaterial(){
        if (document.supplementaryCompleteForm.mtrGrpSelect.value=="") {
           alert('请选择物料组.');
           return;
        }
        if (document.supplementaryCompleteForm.mtrNumSelect.value=="") {
           alert('请选择物料号.');
           return;
        }
        if (document.supplementaryCompleteForm.eqpIdSelect.value=="") {
           alert('请选择设备号.');
           return;
        }
        if (document.supplementaryCompleteForm.vendorMtrNum.value=="") {
           alert('请输入序号/别名.');
           return;
        }
        if (document.supplementaryCompleteForm.shelfLifeExpirationDate.value=="") {
           alert('请输入有效期.');
           return;
        }

	    doSubmit("<ofbiz:url>/manageUsingHistory</ofbiz:url>");
	}

function queryMaterial() {
    if (document.supplementaryCompleteForm.mtrGrpSelect.value=="") {
       alert('请选择物料组.');
       return;
    }
    if (document.supplementaryCompleteForm.mtrNumSelect.value=="") {
       alert('请选择物料号.');
       return;
    }

    doSubmit("<ofbiz:url>/supplementaryCompleteEntry</ofbiz:url>");
}

function doSubmit(url) {
	loading();
	document.supplementaryCompleteForm.action = url;
	document.supplementaryCompleteForm.submit();
}

Ext.onReady(function(){

       //格式化日期
	   var shelfLifeExpirationDate = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:true
	    });

	    shelfLifeExpirationDate.applyTo("<%="shelfLifeExpirationDate"%>");


});

//修改功能弹出页
	function editVendorMaterial(obj,msIndex,mtrNum,mtrGrp,aliasName){
		Ext.get('msIndex').dom.value=msIndex;
		Ext.get('mtr_Num').dom.value=mtrNum;
		Ext.get('mtr_Grp').dom.value=mtrGrp;
		Ext.get('aliasName').dom.value=aliasName;
		extDlg.showEditDialog(obj);
	}

	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('aliasName').dom.value=result.aliasName;
		}
	}

	function checkForm(){
		var aliasName = Ext.get('aliasName').dom.value;
		if(aliasName==""){
			return "序号/别名不可以为空";
		}

		return "";
	}
</script>


<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form method="post" name="supplementaryCompleteForm" action="<%=request.getContextPath()%>/control/supplementaryCompleteEntry" >
<input type="hidden" name="mtrGrpSubmit" value="<%=mtrGrp%>">

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>分类条件</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
	    <tr>
	    	<td class="tabletitle" width="12%">
    		    <i18n:message key="mcs.material_group" />
    		    <i18n:message key="mcs.data_required_red_star" />
    		</td>
			<td class="tablelist" width="30%">
			    <input type="text" size="40" name="mtrGrpSelect" id="mtrGrpSelect" autocomplete="off"/>
			</td>

			<td class="tabletitle" width="12%">
			    <i18n:message key="mcs.material_number" />
			    <i18n:message key="mcs.data_required_red_star" />
			</td>
			<td class="tablelist" width="30%">
			    <input type="text" size="40" name="mtrNumSelect" id="mtrNumSelect" autocomplete="off"/>
			</td>
	    </tr>
	  </table>
  </fieldset></td>
</tr>
</table>

<!--<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="javascript:queryMaterial();"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>-->

<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>补填石英历史使用&nbsp;&nbsp;&nbsp;<%=request.getAttribute("mtrNumDesc")==null?"":request.getAttribute("mtrNumDesc")%></legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
        <tr>
	    	<td class="tabletitle" width="12%">
    		    <i18n:message key="mcs.equipment" />
    		    <i18n:message key="mcs.data_required_red_star" />
    		</td>
			<td class="tablelist" width="30%">
				<input type="text" size="40" name="eqpIdSelect" id="eqpIdSelect" autocomplete="off"/>
			</td>
			<td class="tabletitle" width="12%">
    		    <i18n:message key="mcs.index_alias_name" />
    		    <i18n:message key="mcs.data_required_red_star" />
    		</td>
			<td class="tablelist" width="30%">
			    <input class="textinput" type="text" name="vendorMtrNum" id="vendorMtrNum" value="<%=request.getAttribute("vendorMtrNum")==null?"":request.getAttribute("vendorMtrNum")%>" size="22" />
			</td>
	    </tr>

	    <tr>
	    	<td class="tabletitle" width="12%">
    		    <i18n:message key="mcs.shelf_life_expiration_date" />
    		    <i18n:message key="mcs.data_required_red_star" />
    		</td>
			<td class="tablelist" width="30%">
			    <input type=text name="shelfLifeExpirationDate" id="shelfLifeExpirationDate" value="<%=request.getAttribute("shelfLifeExpirationDate")==null?"1900-1-1":request.getAttribute("shelfLifeExpirationDate")%>" size="26"/>
			</td>
			<td class="tabletitle" width="12%">

    		</td>
			<td  width="30%">
			</td>
	    </tr>
	  </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="javascript:addVendorMaterial();"><span>&nbsp;添加&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>
</form>
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><fieldset>
		<legend>补填石英历史使用列表</legend>
			<table id="objTable1" width="100%" border="0" cellspacing="1" cellpadding="2">
				<tr class="tabletitle">

					<td width="16%" class="en11pxb"><i18n:message key="mcs.index_alias_name" /></td>
					<td width="14%" class="en11pxb"><i18n:message key="mcs.equipment" /></td>
					<td width="16%" class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>
				</tr>

				<ofbiz:if name="vendorMaterialList" size="0">
				<ofbiz:iterator name="cust" property="vendorMaterialList" type="java.util.Map">
				<tr class="tablelist" id="objTr1" style="cursor:hand">
					<td class="en11px">
					<a href="#" onclick="editVendorMaterial(this,'<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_STATUS_INDEX"/>','<ofbiz:inputvalue entityAttr="cust" field="MTR_NUM"/>','<ofbiz:inputvalue entityAttr="cust" field="MTR_GRP"/>','<ofbiz:inputvalue entityAttr="cust" field="ALIAS_NAME"/>')">
					<ofbiz:entityfield attribute="cust" field="ALIAS_NAME" /></a></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="USING_OBJECT_ID" /></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="SHELF_LIFE_EXPIRATION_DATE" /></td>
				</tr>
				</ofbiz:iterator>
				</ofbiz:if>
			</table>
		</fieldset></td>
	</tr>
</table>

<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">修改别名</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="修改别名">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/updateAliasName" method="post" id="manageUsingHistoryForm" onsubmit="return false;">
                <p>

                <label for="name"><small><i18n:message key="mcs.index_alias_name" /></small><big><i18n:message key="mcs.data_required_red_star" /></big></label>
                <input class="textinput" type="text" name="aliasName" id="aliasName" value="" size="22" tabindex="1" />
                <input class="textinput" type="hidden" name="msIndex" id="msIndex" value="" tabindex="1" />
                <input class="textinput" type="hidden" name="mtr_Num" id="mtr_Num" value="" tabindex="1" />
                <input class="textinput" type="hidden" name="mtr_Grp" id="mtr_Grp" value="" tabindex="1" />
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
