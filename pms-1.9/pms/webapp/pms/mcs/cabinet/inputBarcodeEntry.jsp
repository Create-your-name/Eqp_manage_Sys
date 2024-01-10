<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<%
    List droneList = (List) request.getAttribute("droneList");

	String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
	if ("".equals(mtrGrp)) {
	    //默认选择光刻胶
	    mtrGrp = ConstantsMcs.PHOTORESIST;
	}

	String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum"));
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

	    	var mtrNum = '<%=mtrNum%>';
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

    /*if (Ext.getDom('mtrNum').value == "") {
        Ext.MessageBox.alert('警告', '请选择物料号');
		return;
    }*/

    doSubmit("<ofbiz:url>/inputBarcodeEntry</ofbiz:url>");
}

function saveBarcode() {
    if (inputForm.aliasName.length > 1) {
        //只有一行数据时inputForm.aliasName无length属性，返回false
        var objBarcode = inputForm.barcode;
        var objMaterialStatusIndex = inputForm.materialStatusIndex;
        var objBarcodePrefix = inputForm.barcodePrefix;
        var objVendorBatchNum = inputForm.vendorBatchNum;
        var objBarcodeMtrNum = inputForm.barcodeMtrNum;

		for (i=0; i < objBarcode.length; i++) {
		    var aliasName1 = objBarcode[i].value;
		    if(aliasName1.indexOf('￥')>-1){
		    	Ext.MessageBox.alert('警告','修改输入法设置，将￥改为$');
		    	return;
		    }

		    if (aliasName1 != "" && objMaterialStatusIndex[i].value != aliasName1) {
		        if (inputForm.mtrGrpSubmit.value == "<%=ConstantsMcs.PHOTORESIST%>"
		             && objBarcodeMtrNum[i].value != "<%=ConstantsMcs.PHOTORESIST_55CP%>"
		             && objBarcodeMtrNum[i].value != "<%=ConstantsMcs.PHOTORESIST_7510%>"
		             && objBarcodeMtrNum[i].value != "<%=ConstantsMcs.PHOTORESIST_RZJ50CP%>") {
		            //光刻胶标准格式校验
		            if (aliasName1.length != 28 && aliasName1.length != 29) {
		                Ext.MessageBox.alert('警告', aliasName1 + ' 扫描错误，标准光刻胶条码必须为28或29位。');
                        return;
                    }

                    if (aliasName1.substring(0,17) != objBarcodePrefix[i].value) {
		                Ext.MessageBox.alert('警告', aliasName1 + ' 扫描错误，与条码前缀不符。');
                        return;
                    }
		        }

		        if (objBarcodeMtrNum[i].value == "<%=ConstantsMcs.PHOTORESIST_55CP%>") {
		            //55CP光刻胶：0010218716$A039DBI014（实物扫描：10位字符+$+10位厂家批号）+另一条码序列号
		            if (aliasName1.length <= 21) {
		                Ext.MessageBox.alert('警告', aliasName1 + ' 扫描错误，MCPRI7010N-55CP光刻胶条码必须多于21位。');
                        return;
                    }
		        }

		        if (objBarcodeMtrNum[i].value == "<%=ConstantsMcs.PHOTORESIST_7510%>") {
		            //光刻胶Durimide 7510：新条码为27位，旧条码为28位（库存最后领用为2013年7月15日）
		            if (aliasName1.length != 27 && aliasName1.length != 28) {
		                Ext.MessageBox.alert('警告', aliasName1 + ' 扫描错误，Durimide 7510光刻胶条码必须为27或28位。');
                        return;
                    }

                    //光刻胶Durimide 7510条码示例：DUR7510N5H17RJK160804000009
                    if (aliasName1.length == 27 && aliasName1.substring(7,15) != objVendorBatchNum[i].value) {
		                Ext.MessageBox.alert('警告', aliasName1 + ' 扫描错误，与系统供应商批号(共8位字符)不符。');
                        return;
                    }
		        }

		        if (objBarcodeMtrNum[i].value == "<%=ConstantsMcs.PHOTORESIST_RZJ50CP%>") {
		            //26位正性光刻胶 RZJ-390PG-50CP：RZJ-390PG-050 5062566 0169
		            if (aliasName1.length != 26 && aliasName1.length != 29) {
		                Ext.MessageBox.alert('警告', aliasName1 + ' 扫描错误，RZJ-390PG-50CP光刻胶条码必须为26或29位。');
                        return;
                    }
		        }

		        if (objBarcodeMtrNum[i].value.substring(0,9) == "<%=ConstantsMcs.SOG_PREFIX%>") {
		            //SOG：原格式为16或18位，新供应商为9位（供应商批号+瓶号）
		            if (aliasName1.length != 16 && aliasName1.length != 18 && aliasName1.length != 9) {
		                Ext.MessageBox.alert('警告', aliasName1 + ' 扫描错误，SOG必须为16或18位 或 9位。');
                        return;
                    }

                    if (aliasName1.length >= 16 && aliasName1.substring(2,12) != objVendorBatchNum[i].value) {
		                Ext.MessageBox.alert('警告', aliasName1 + ' 扫描错误，与系统供应商批号(共10位字符)不符。');
                        return;
                    }
		        }

    		    for (j=i+1; j < objBarcode.length; j++) {
    		        var aliasName2 = objBarcode[j].value;

        		    if (aliasName1 == aliasName2) {
                        Ext.MessageBox.alert('警告', '存在重复的数据，请重新选择或输入');
                        return;
        		    }
        		}
        	}
		}//end for rows
	}//end if

    doSubmit("<ofbiz:url>/inputBarcode</ofbiz:url>");
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
      <legend>待扫描列表</legend>
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
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="VENDOR_BATCH_NUM" tryEntityAttr="true" /></td>

	        	<ofbiz:if name="mtrGrp" value="<%=ConstantsMcs.PHOTORESIST%>">
    	        	<td class="en11px">
    	        	    <ofbiz:inputvalue entityAttr="cust" field="VENDOR_MTR_NUM" tryEntityAttr="true" />
    	        	    -
    	        	    <ofbiz:inputvalue entityAttr="cust" field="VENDOR_BATCH_NUM_BARCODE" tryEntityAttr="true" />
    	        	</td>
	        	</ofbiz:if>

	        	<td class="en11px">
	        	    <%if (droneList != null) {%>
	        	        <!-- 回收类靶材，选择SAP入库靶背号 -->
	        	        <select name="barcode">
	        	            <option value='<ofbiz:inputvalue entityAttr="cust" field="ALIAS_NAME" tryEntityAttr="true" />'>
	        	                <ofbiz:inputvalue entityAttr="cust" field="ALIAS_NAME" tryEntityAttr="true" />
	        	            </option>
	        	            <option value=''>重置</option>

                        	<ofbiz:iterator name="dl" property="droneList" type="java.util.Map">
                            	<option value='<ofbiz:inputvalue entityAttr="dl" field="DEONE_ID_NUM" />'>
                            	    <ofbiz:inputvalue entityAttr="dl" field="DEONE_ID_NUM"/>
                            	</option>
                            </ofbiz:iterator>
                        </select>
	        	    <%} else {%>
	        	        <!-- 光刻胶、SOG扫描条码 -->
    	        	    <input type="text" name="barcode" id="barcode" maxlength="29" style="width:215;" value="<ofbiz:inputvalue entityAttr="cust" field="ALIAS_NAME" tryEntityAttr="true" />" onKeyDown="sTime=new Date()" onKeyUp="doScan(this)">
	        	    <%}%>

	        	    <input type="hidden" name="materialStatusIndex" id="materialStatusIndex" value="<ofbiz:inputvalue entityAttr="cust" field="MATERIAL_STATUS_INDEX" tryEntityAttr="true" />">

	        	    <input type="hidden" name="aliasName" id="aliasName" value="<ofbiz:inputvalue entityAttr="cust" field="ALIAS_NAME" tryEntityAttr="true" />">

	        	    <!-- 光刻胶校验栏位 -->
                    <input type="hidden" name="barcodePrefix" id="barcodePrefix" value="<ofbiz:inputvalue entityAttr="cust" field="VENDOR_MTR_NUM" tryEntityAttr="true" />-<ofbiz:inputvalue entityAttr="cust" field="VENDOR_BATCH_NUM_BARCODE" tryEntityAttr="true" />">
                    <input type="hidden" name="vendorBatchNum" id="vendorBatchNum" value="<ofbiz:inputvalue entityAttr="cust" field="VENDOR_BATCH_NUM" tryEntityAttr="true" />">
                    <input type="hidden" name="barcodeMtrNum" id="barcodeMtrNum" value="<ofbiz:inputvalue entityAttr="cust" field="MTR_NUM" tryEntityAttr="true" />">

	        	 </td>

	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="SHELF_LIFE_EXPIRATION_DATE" tryEntityAttr="true" /></td>
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
		    <span class="cabinet">&nbsp;保存&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>

</ofbiz:if>

</form>

<ofbiz:if name="statusList1" size="0">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>已录入列表</legend>
      <table id="mtrTable" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      	    <td class="en11pxb">NO.</td>
      	    <td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.scan_barcode" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.doc_time" /></td>

      		<ofbiz:if name="mtrGrp" value="<%=ConstantsMcs.PHOTORESIST%>">
      		    <td class="en11pxb"><i18n:message key="mcs.unfrozen_trans_time" /></td>
      		    <td class="en11pxb"><i18n:message key="mcs.operator" /></td>
      		</ofbiz:if>
    	</tr>

    	<%int i = 0;%>
	    <ofbiz:iterator name="cust" property="statusList1" type="java.util.Map">
	        <tr class="tablelist" id="objTr">
	            <td class="en11px"><%=++i%></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MTR_NUM" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="MTR_DESC" tryEntityAttr="true" /></td>
	        	<td class="en11px" nowrap><ofbiz:inputvalue entityAttr="cust" field="ALIAS_NAME" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="SHELF_LIFE_EXPIRATION_DATE" tryEntityAttr="true" /></td>
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
</ofbiz:if>