<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<%
    //参考录入条码页面
    //List droneList = (List) request.getAttribute("droneList");

	String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
	String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum"));
	if ("".equals(mtrGrp)) {
	    //默认选择光刻胶
	    mtrGrp = ConstantsMcs.PHOTORESIST;
	}
%>

<script language="javascript">
var useMaterialById = function() {

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
			    allowBlank: true
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
			    allowBlank: true
		    });
		    mtrNumCom.applyTo('mtrNumSelect');
		    mtrNumCom.on('select', queryMaterial);
		},

		initLoad : function() {
			var mtrGrp = '<%=mtrGrp%>';
	    	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(mtrGrp); }});

	    	var mtrNum = '<%=UtilFormatOut.checkNull(request.getParameter("mtrNum"))%>';
	    	mtrNumDS.load({params:{mtrGrp:mtrGrp},callback:function(){ mtrNumCom.setValue(mtrNum); }});
		},

		loadMtrNum : function() {
			var mtrGrp = mtrGrpCom.getValue();
			var mtrNum  = '';

			if (mtrGrp=='<%=ConstantsMcs.QUARTZ%>') {
			    doSubmit("<ofbiz:url>/inputBarcodeEntry</ofbiz:url>");
			}
	 		mtrNumDS.load({params:{mtrGrp:mtrGrp},callback:function(){ mtrNumCom.setValue(mtrNum); }});
	 	}
	}
}();

Ext.EventManager.onDocumentReady(useMaterialById.init, useMaterialById, true);

</script>


<!-- ##################################### submit script ################################ -->
<script language="javascript">
function queryMaterial() {
    if (Ext.getDom('mtrGrp').value == "") {
        Ext.MessageBox.alert('警告', '请选择物料组');
		return;
    }

    if (Ext.getDom('mtrNum').value == "") {
        Ext.MessageBox.alert('警告', '请选择物料号');
		return;
    }

    doSubmit("<ofbiz:url>/modifyVendorInfoEntry</ofbiz:url>");
}

function saveBarcode() {

    if (inputForm.materialStatusIndex.length > 1) {//只有一行数据时，返回false
        var objMaterialStatusIndex = inputForm.materialStatusIndex;
        var objExp = inputForm.shelfLifeExpirationDate;

		for (i=0; i < objMaterialStatusIndex.length; i++) {
		    var shelfLifeExpirationDate = objExp[i].value;

	        if (shelfLifeExpirationDate.length != 10) {
	            Ext.MessageBox.alert('警告', shelfLifeExpirationDate + '格式错误，必须为YYYY-MM-DD(如2011-01-01)。');
                return;
	        }
		}
	}

    doSubmit("<ofbiz:url>/modifyVendorInfo</ofbiz:url>");
}

function doSubmit(url) {
	loading();
	document.inputForm.action = url;
	document.inputForm.submit();
}

// check input barcode
var sTime;
var eTime;
function doScan(obj) {
	eTime = new Date();
	if (eTime.getTime()-sTime.getTime() > 300) {
		obj.value = "";
		alert("请使用扫描枪操作");
	}
}
</script>


<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form method="post" name="inputForm">
<input type="hidden" name="mtrGrpSubmit" value="<%=mtrGrp%>">
<input type="hidden" name="mtrNumSubmit" value="<%=mtrNum%>">

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
			    <input type="text" size="40" name="mtrGrpSelect" autocomplete="off"/>
			</td>

			<td class="tabletitle" width="12%">
			    <i18n:message key="mcs.material_number" />
			    <i18n:message key="mcs.data_required_red_star" />
			</td>
			<td class="tablelist" width="30%">
			    <input type="text" size="40" name="mtrNumSelect" autocomplete="off"/>
			</td>
	    </tr>
	  </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
		<!--<li><a class="button-text" href="javascript:queryMaterial();"><span>&nbsp;查询&nbsp;</span></a></li>-->
	</ul></td>
  </tr>
</table>

<ofbiz:if name="statusList" size="0">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>物料列表</legend>
      <table id="mtrTable" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      	    <td class="en11pxb">NO.</td>
      		<!--<td class="en11pxb"><i18n:message key="mcs.index_alias_name" /></td>-->
      		<!--<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>-->
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>

      		<ofbiz:if name="mtrGrp" value="<%=ConstantsMcs.PHOTORESIST%>">
      		    <td class="en11pxb"><i18n:message key="mcs.barcode_prefix" /></td>
      		</ofbiz:if>

      		<td class="en11pxb"><i18n:message key="mcs.scan_barcode" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.doc_time" /></td>

      		<ofbiz:if name="mtrGrp" value="<%=ConstantsMcs.PHOTORESIST%>">
      		    <td class="en11pxb"><i18n:message key="mcs.unfrozen_trans_time" /></td>
      		    <td class="en11pxb"><i18n:message key="mcs.operator" /></td>
      		</ofbiz:if>
    	</tr>

    	<%int i = 0;%>
	    <ofbiz:iterator name="cust" property="statusList" type="java.util.Map">
	        <tr class="tablelist" id="objTr">
	            <td class="en11px"><%=++i%></td>
				<!--<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="ALIAS_NAME" tryEntityAttr="true" /></td>-->
	        	<!--<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MTR_NUM" tryEntityAttr="true" /></td>-->
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MTR_DESC" tryEntityAttr="true" /></td>

	        	<td class="en11px">
	        	    <input type="hidden" name="materialStatusIndex" value="<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_STATUS_INDEX" tryEntityAttr="true" />">

	        	    <input type="text" name="vendorBatchNum" value='<ofbiz:inputvalue entityAttr="cust" field="VENDOR_BATCH_NUM" tryEntityAttr="true" />'>
	        	</td>

	        	<ofbiz:if name="mtrGrp" value="<%=ConstantsMcs.PHOTORESIST%>">
    	        	<td class="en11px">
    	        	    <ofbiz:inputvalue entityAttr="cust" field="VENDOR_MTR_NUM" tryEntityAttr="true" />
    	        	    -
    	        	    <ofbiz:inputvalue entityAttr="cust" field="VENDOR_BATCH_NUM_BARCODE" tryEntityAttr="true" />
    	        	</td>
	        	</ofbiz:if>

	        	<td class="en11px">
	        	    <ofbiz:inputvalue entityAttr="cust" field="ALIAS_NAME" tryEntityAttr="true" />
	        	</td>

	        	<td class="en11px">
	        	    <input type="text" name="shelfLifeExpirationDate" value='<ofbiz:inputvalue entityAttr="cust" field="SHELF_LIFE_EXPIRATION_DATE" tryEntityAttr="true" />'>
	        	</td>

	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="DOC_TIME" tryEntityAttr="true" /></td>

	        	<ofbiz:if name="mtrGrp" value="<%=ConstantsMcs.PHOTORESIST%>">
    	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="UNFROZEN_TRANS_TIME" tryEntityAttr="true" /></td>
    	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="UNFROZEN_TRANS_BY" tryEntityAttr="true" /></td>
	        	</ofbiz:if>
	        </tr>
	   	</ofbiz:iterator>

      </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
	<td><ul class="button">
		<li><a class="button-text" href="javascript:saveBarcode();">
		    <span class="cabinet">&nbsp;修改&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>

</ofbiz:if>

</form>