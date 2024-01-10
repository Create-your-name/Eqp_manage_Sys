<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<%
    List forUseList = (List) request.getAttribute("forUseList");
    List cabinetList = (List) request.getAttribute("cabinetList");
    String lastPrUsed = UtilFormatOut.checkNull((String) request.getAttribute("lastPrUsed"));

    String strScanBarcode = UtilFormatOut.checkNull((String) request.getAttribute("strScanBarcode"));
    //strScanBarcode = "SPR6812X-38F8B067-160506-0063,6130XXXX-54302K23-160422-0140";//test barcodes use string format

    String eqpId = UtilFormatOut.checkNull(request.getParameter("eqpId"));
    String mtrGrp = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
    if ("".equals(mtrGrp)) {
	    //Ĭ��ѡ���̽�
	    mtrGrp = ConstantsMcs.PHOTORESIST;
	}

	String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum"));

	boolean isSog = false;
	if (eqpId.length() >= 5 && mtrNum.length() >= 9) {
    	if ("CCSOG".equals(eqpId.substring(0, 5)) && ConstantsMcs.SOG_PREFIX.equals(mtrNum.substring(0,9))) {
    	    isSog = true;
    	}
    }
%>

<!-- ##################################### yui page script ################################ -->
<script language="javascript">
var useMaterialById = function() {
	var eqpIdDS, eqpIdCom;
	var mtrGrpDS, mtrGrpCom;

	return {
		init : function() {
			this.createDataStore();
		    this.createCombox();
		    this.initLoad();
		},

		//��������Դ
		createDataStore : function() {

		    eqpIdDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentByDeptIndex</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
		    });

			mtrGrpDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonMtrGrp</ofbiz:url>?useByBarcode=Y'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'mtrGrp'}, {name: 'mtrGrpDesc'}]))
		    });
		},

		createCombox : function() {

		    //����eqpId
			eqpIdCom = new Ext.form.ComboBox({
			    store: eqpIdDS,
			    displayField:'equipmentId',
			    valueField:'equipmentId',
			    hiddenName:'eqpId',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all',
			    allowBlank: false
		    });
		    eqpIdCom.applyTo('eqpIdSelect');
		    eqpIdCom.on('select', queryEqpMaterial);

			//����������
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
		    mtrGrpCom.on('select', loadEquipment);

		    function loadEquipment() {
        	    var eqpId = '<%=eqpId%>';
    	    	var deptIndex = '';
        		if (mtrGrpCom.getValue() == '<%=ConstantsMcs.PHOTORESIST%>') {//��̽�: ��ѯ��̲��豸
    			    deptIndex = '<%=ConstantsMcs.DEPT_INDEX_PP%>';
    			}
        		eqpIdDS.load({params:{deptIndex:deptIndex}, callback:function(){ eqpIdCom.setValue(eqpId); }});
        	};
		},

		initLoad : function() {
			var mtrGrp = '<%=mtrGrp%>';
	    	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(mtrGrp); }});

	    	var eqpId = '<%=eqpId%>';
	    	var deptIndex = '';
    		if (mtrGrp == '<%=ConstantsMcs.PHOTORESIST%>') {//��̽�: ��ѯ��̲��豸
			    deptIndex = '<%=ConstantsMcs.DEPT_INDEX_PP%>';
			}
    		eqpIdDS.load({params:{deptIndex:deptIndex}, callback:function(){ eqpIdCom.setValue(eqpId); }});
		}
	}
}();

Ext.EventManager.onDocumentReady(useMaterialById.init, useMaterialById, true);
</script>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function queryEqpMaterial() {
    var eqpId = Ext.getDom('eqpId').value;
    if (eqpId.substring(0,5)=="CCSOG") {
        Ext.getDom('mtrGrp').value = "<%=ConstantsMcs.CHEMICAL%>";
        Ext.getDom('mtrNum').value = "<%=ConstantsMcs.SOG_PREFIX%>";
    }
    doSubmit("<ofbiz:url>/useMaterialByBarcodeEntry</ofbiz:url>");
}

function validSogOK() {
    if (checkForm("validSogOK")) {
		doSubmit("<ofbiz:url>/validSogOK</ofbiz:url>");
	}
}

function changeMaterialStatus(newStatus, newStatusDesc) {
    if (checkForm(newStatus)) {
        Ext.MessageBox.confirm('����ȷ��', '��ȷ��ѡ�� ' + newStatusDesc + ' ��',
            function result(value) {
                if ( value=="yes" ) {
        			doSubmit("<ofbiz:url>/useMaterialByBarcode</ofbiz:url>?newStatus=" + newStatus);
        		} else {
        			return;
        		}
            }
        );
    }
}

function checkForm(newStatus) {
    if (newStatus == "<%=ConstantsMcs.ONLINE_SCRAP_OPT%>") {//SOG ���߱���
        if (document.useForm.offNote.value == "") {
            Ext.MessageBox.alert('����', '������ ���±�ע!');
            return false;
        }
    }

    if (totalSelected == 0) {
        Ext.MessageBox.alert('����', '��ѡ����Ҫ����������!');
        return false;
    }

    lastPrUsed = document.getElementById("lastPrUsed").value;//���һ�λ��ϵĹ�̽�
    objCheckbox = document.getElementsByName("objCheckboxEqpMtr");//�豸��������
    if (document.useForm.useNote.value == "" && lastPrUsed != "" && objCheckbox != null && <%=mtrGrp%> == <%=ConstantsMcs.PHOTORESIST%>) {
	    if (objCheckbox.length > 1) {
    		for(i=0; i < objCheckbox.length; i++){
    		    offMtrNumValue = document.getElementsByName("offMtrNum")[i].value;
    		    if (objCheckbox[i].checked && lastPrUsed.indexOf(offMtrNumValue) == -1 ) {//ѡ�л��¹�̽�ʱ�ȶ�
                    Ext.MessageBox.alert('����', '�˹�̽������һ�λ��ϵĹ�̽����Ͳ�ͬ����ȷ���豸���̽��Ƿ���ȷ����������д��ע��ɼ����ύ��');
                    return false;
    		    }
    		}
    	} else {// only one Checkbox
    	    //�����ҵ�����
    	    objCheckbox = document.getElementById("objCheckboxEqpMtr");

    	    offMtrNumValue = document.getElementById("offMtrNum").value;
    	    if (objCheckbox.checked && lastPrUsed.indexOf(offMtrNumValue) == -1 ) {
    	        Ext.MessageBox.alert('����', '�˹�̽������һ�λ��ϵĹ�̽����Ͳ�ͬ����ȷ���豸���̽��Ƿ���ȷ����������д��ע��ɼ����ύ��');
                return false;
        	}
    	}
	}

	return true;
}

function checkCondition() {
	var msg = "";
    if (Ext.getDom('eqpId').value == "") {
		msg = '��ѡ���豸!';
		return msg;
	}

	if (Ext.getDom('mtrGrp').value == "") {
		msg = '��ѡ��������!';
		return msg;
	}

	return msg;
}

function doSubmit(url) {
    var msg = checkCondition();
	if(msg.length > 0) {
		Ext.MessageBox.alert('����', msg);
		return;
	}

	loading();
	document.useForm.action = url;
	document.useForm.submit();
}

function viewStatusHist(materialStatusIndex) {
    var url = "<ofbiz:url>/queryMaterialStatusHist</ofbiz:url>?materialStatusIndex=" + materialStatusIndex;
    var result = window.showModalDialog(url, window, "dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");
}

// check input barcode
var sTime;
var eTime;
function doScan(obj) {
	eTime = new Date();
	if (eTime.getTime()-sTime.getTime() > 100) {
		obj.value = "";
		alert("��ʹ��ɨ��ǹ������");
	}
}

function abnormalInfo() {
    alert('���棺��ʹ��ɨ��ǹ�������ǹ淶����������¼�ڰ���');
    window.event.returnValue = false;
}
</script>

<!-- ##################################### checkbox script ################################ -->
<script language="javascript">
<!--
var totalSelected = 0;                       //ѡȡ������;
var totalCount = 0;                          //���ϵ�����;

function selectOneCheckBox(objTable, objTr, objCheckbox, rowIndex, checkBoxSelectAll) {
    //--------------rowIndex=1,2,3... theIdx=0,1,2,3......--------------
    totalCount = objTable.rows.length - 1;
	var theIdx = rowIndex - 1;
	var obj = (objCheckbox.length > 1) ? objCheckbox[theIdx] : objCheckbox;
	var varTR = (objTr.length > 1) ? objTr[theIdx] : objTr;

    // comment next row to cancal checkbox click function
	obj.checked = !obj.checked;//CheckBox��Ӱ���ѡ����

	if (obj.checked) {
		varTR.className = "tablelistchecked";
		totalSelected++;
	} else {
		varTR.className = "tablelist";
		totalSelected--;
	}

	if (totalSelected == totalCount) {
	    checkBoxSelectAll.checked = true;
	} else {
	    checkBoxSelectAll.checked = false;
	}

}

function selectAllCheckBox(objTable, objTr, objCheckbox, isChecked){//----------���� top chceckboxȫ��ѡ���ȡ��ѡ��---------
	if (objTable == null || objTr == null || objCheckbox == null) {
		return;
	}

	var i;

	totalCount = objTable.rows.length - 1;
	if (totalCount == 0) {
		return;
    }

	if (objCheckbox.length > 1) {
		for(i=0; i < objCheckbox.length; i++){
			objCheckbox[i].checked = isChecked;
			objTr[i].className = isChecked ? "tablelistchecked" : "tablelist";
		}
	} else {// only one Checkbox
	    objCheckbox.checked = isChecked;
		objTr.className = isChecked ? "tablelistchecked" : "tablelist";
	}

	totalSelected = isChecked ? totalCount : 0;
}

// ����ɨ��js, ѡ����ȡ��ѡ��
function selectBarcode(objTable, objTr, objCheckbox, formActionType){//----------ѡ��ɨ����---------
	if (objTable == null || objTr == null || objCheckbox == null) {
		return;
	}

	totalCount = objTable.rows.length - 1;
	if (totalCount == 0) {
		return;
    }

    var i, objBarcode, isChecked;
    var aliasNameValue, mtrNumValue, objBarcodeValue;
    objBarcode = Ext.getDom(formActionType+'BarCode');
    objBarcodeValue = objBarcode.value.toUpperCase();

    //55CP��̽�PHOTORESIST_55CP��0010218716$A039DBI014��ʵ��ɨ�裺10λ�ַ�+$+10λ�������ţ�

	if (objCheckbox.length > 1) {
		for(i=0; i < objCheckbox.length; i++){
		    aliasNameValue = document.getElementsByName(formActionType+"AliasName")[i].value;
		    mtrNumValue = document.getElementsByName(formActionType+"MtrNum")[i].value;
		    //Fab1&Fab5�в�����Ϊ22λ�ַ�: 11λ�Ϻ�+|+10λsap���š�ϵͳ����mtrStatusIdʹ����aliasNameΨһ��ȡǰ22λ����ƥ�䡣
            if (useForm.mtrGrpSubmit.value == "<%=ConstantsMcs.TARGET%>") {
                aliasNameValue = aliasNameValue.substr(0,22);
                //alert(aliasNameValue);
            }

		    if (objBarcodeValue == aliasNameValue) {
		        isChecked = !objCheckbox[i].checked;
    		    objCheckbox[i].checked = isChecked;
    			objTr[i].className = isChecked ? "tablelistchecked" : "tablelist";
    			objBarcode.value="";
    			totalSelected = isChecked ? (totalSelected + 1) : (totalSelected - 1);

    			if (formActionType == "use" && <%=mtrGrp%> == <%=ConstantsMcs.PHOTORESIST%>) {
        		    //selectBarcodeOffBy(mtrNumValue);
        		}

    			break;
		    }
		}
	} else {// only one Checkbox
	    aliasNameValue = document.getElementById(formActionType+"AliasName").value;
	    mtrNumValue = document.getElementById(formActionType+"MtrNum").value;
	    //Fa1&Fab5�в�����Ϊ: 11λ�Ϻ�+|+10λsap����
        if (useForm.mtrGrpSubmit.value == "<%=ConstantsMcs.TARGET%>") {
            aliasNameValue = aliasNameValue.substr(0,22);
            //alert(aliasNameValue);
        }

	    if (objBarcodeValue == aliasNameValue) {
	        isChecked = !objCheckbox.checked;
    	    objCheckbox.checked = isChecked;
    		objTr.className = isChecked ? "tablelistchecked" : "tablelist";
    		objBarcode.value="";
    		totalSelected = isChecked ? (totalSelected + 1) : (totalSelected - 1);

    		if (formActionType == "use" && <%=mtrGrp%> == <%=ConstantsMcs.PHOTORESIST%>) {
    		    //selectBarcodeOffBy(mtrNumValue);
    		}
    	}
	}
}

// ʹ�ù�̽�ʱ, �Զ�ѡ���豸���ö�Ӧ�ͺŹ�̽�
// SOGĿǰ��isSog�ж�Ĭ��ѡ��һƿ��Ҳ��ʹ�ô˷����滻��
function selectBarcodeOffBy(mtrNumValue){
    return;//ע�⣺��ȡ��ʹ�ô˹���

    objTable = document.getElementById("objTableEqpMtr");
    objTr = document.getElementsByName("objTrEqpMtr");
    objCheckbox = document.getElementsByName("objCheckboxEqpMtr");

	if (objTable == null || objTr == null || objCheckbox == null) {
		return;
	}

	totalCount = objTable.rows.length - 1;
	if (totalCount == 0) {
		return;
    }

    var i, isChecked;
    var offMtrNumValue;

	if (objCheckbox.length > 1) {
		for(i=0; i < objCheckbox.length; i++){
		    offMtrNumValue = document.getElementsByName("offMtrNum")[i].value;
		    if (offMtrNumValue == mtrNumValue) {
		        isChecked = !objCheckbox[i].checked;
    		    objCheckbox[i].checked = isChecked;
    			objTr[i].className = isChecked ? "tablelistchecked" : "tablelist";
    			totalSelected = isChecked ? (totalSelected + 1) : (totalSelected - 1);
    			break;
		    }
		}
	} else {// only one Checkbox
	    //�����ҵ�����
	    objTr = document.getElementById("objTrEqpMtr");
        objCheckbox = document.getElementById("objCheckboxEqpMtr");

	    offMtrNumValue = document.getElementById("offMtrNum").value;
	    if (offMtrNumValue == mtrNumValue) {
	        isChecked = !objCheckbox.checked;
    	    objCheckbox.checked = isChecked;
    		objTr.className = isChecked ? "tablelistchecked" : "tablelist";
    		totalSelected = isChecked ? (totalSelected + 1) : (totalSelected - 1);
    	}
	}
}

-->
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form method="post" name="useForm">
<input type="hidden" name="eqpIdSubmit" value="<%=eqpId%>">
<input type="hidden" name="mtrGrpSubmit" value="<%=mtrGrp%>">
<input type="hidden" name="lastPrUsed" value="<%=lastPrUsed%>">

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr ondragstart="abnormalInfo()" oncontextmenu="abnormalInfo()">
  <td><fieldset>
      <legend>��������</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
		<tr>
    		<td class="tabletitle" width="12%">
    		    <i18n:message key="mcs.equipment" />
    		    <i18n:message key="mcs.data_required_red_star" />
    		</td>
			<td class="tablelist" width="30%" colspan="3">
			    <input type="text" size="40" name="eqpIdSelect" autocomplete="off"/>
			</td>
	    </tr>

	    <tr>
    		<td class="tabletitle" width="12%">
    		    <i18n:message key="mcs.material_group" />
    		    <i18n:message key="mcs.data_required_red_star" />
    		</td>
			<td class="tablelist" width="30%">
			    <input type="text" size="40" name="mtrGrpSelect" autocomplete="off"/>
			</td>

			<td class="tabletitle" width="12%"><i18n:message key="mcs.material_number" /></td>
			<td class="tablelist" width="30%">
			    <input type="text" class="input" size="20" name="mtrNum" value="<ofbiz:field attribute="mtrNum"/>" />
			</td>
	    </tr>
	  </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="javascript:queryEqpMaterial();"><span>&nbsp;��ѯ&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr ondragstart="abnormalInfo()" oncontextmenu="abnormalInfo()" onselectstart="abnormalInfo()">
  <td><fieldset>
      <legend>
          �豸���������б� ɨ�����룺
          <input type="text" class="input" size="40" name="offBarcode" id="offBarcode" value="" onPropertyChange="selectBarcode(objTableEqpMtr, objTrEqpMtr, objCheckboxEqpMtr, 'off');" onKeyDown="sTime=new Date()" onKeyUp="doScan(this)">
      </legend>
      <table id="objTableEqpMtr" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      	    <td class="en11pxb">
      	        ѡ��
      		    <input type="checkbox" name="checkBoxSelectAllEqpMtr" id="checkBoxSelectAllEqpMtr"
      		    class="hand" title="ȫѡ/ȡ��ѡ��" style="display:none;"
      		    <ofbiz:if name="eqpMaterialList" size="0">
      		        onClick="selectAllCheckBox(objTableEqpMtr, objTrEqpMtr, objCheckboxEqpMtr, this.checked);"
      		    </ofbiz:if>
      		    >
      		</td>
      	    <td class="en11pxb"><i18n:message key="mcs.index_alias_name" /></td>
      	    <td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.mrb_date" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.using_time" /></td>
    	</tr>

	    <ofbiz:iterator name="cust" property="eqpMaterialList">
	        <%if (isSog) {%>
        	<script language="javascript">
                totalSelected++;
            </script>
            <%}%>

            <!--�вĻ��£��ɲ�ɨ������˶�-->
            <tr class="tablelist" id="objTrEqpMtr"
                <%if (ConstantsMcs.TARGET.equals(mtrGrp)) {%>
                    style="cursor:hand"
                    onClick="javascript:selectOneCheckBox(objTableEqpMtr, objTrEqpMtr, objCheckboxEqpMtr, this.rowIndex, checkBoxSelectAllEqpMtr);"
                <%}%>
            >
	            <td class="en11px">
	        	    <input type="checkbox" name="objCheckboxEqpMtr" id="objCheckboxEqpMtr"
	        	    value="<ofbiz:inputvalue entityAttr="cust" field="materialStatusIndex" tryEntityAttr="true" />"
	        	    onClick="this.checked=!this.checked;"
    	        	    <%if (isSog) {%>
    	        	        checked
    	        	    <%}%>
	        	    >

	        	    <ofbiz:inputvalue entityAttr="cust" field="remark" tryEntityAttr="true" />
	        	</td>

	        	<td class="en11px">
	        	    <a href="javascript:viewStatusHist('<ofbiz:inputvalue entityAttr="cust" field="materialStatusIndex" tryEntityAttr="true" />')">
	        	        <ofbiz:inputvalue entityAttr="cust" field="aliasName" tryEntityAttr="true" />
	        	    </a>

	        	    <input type="hidden" name="offAliasName" value="<ofbiz:inputvalue entityAttr="cust" field="aliasName" tryEntityAttr="true" />">
	        	    <input type="hidden" name="offMtrNum" value="<ofbiz:inputvalue entityAttr="cust" field="mtrNum" tryEntityAttr="true" />">
	        	</td>

	            <td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="vendorBatchNum" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="mtrNum" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="mtrDesc" tryEntityAttr="true" /></td>

	        	<td class="en11px">
	        	    <ofbiz:format type="date">
	        	        <ofbiz:inputvalue entityAttr="cust" field="shelfLifeExpirationDate" tryEntityAttr="true" />
	        	    </ofbiz:format>
	        	</td>
	        	<td class="en11px">
	        	    <ofbiz:inputvalue entityAttr="cust" field="mrbDate" tryEntityAttr="true" />
	        	</td>

	        	<td class="en11px">
	        	    <ofbiz:inputvalue entityAttr="cust" field="updateTime" tryEntityAttr="true" />
	        	</td>
	        </tr>
	   	</ofbiz:iterator>

      </table>
  </fieldset></td>
</tr>
</table>

<%if (isSog) {%>
    <table border="0" cellspacing="0" cellpadding="0">
      <tr height="30">
        <td class="en11pxb">
            ���±�ע
            <input type="text" class="input" size="20" name="offNote" value="">
        </td>

        <td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.CABINET_RECYCLE%>','<i18n:message key="mcs.status_cabinet_recycle" />');">
    		    <span class="cabinet">&nbsp;<i18n:message key="mcs.status_cabinet_recycle" />&nbsp;</span>
    		</a></li>
    	</ul></td>

        <td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.ONLINE_SCRAP_OPT%>', '<i18n:message key="mcs.status_online_scrap" />');">
    		    <span class="scrap">&nbsp;<i18n:message key="mcs.status_online_scrap" />&nbsp;</span>
    		</a></li>
    	</ul></td>

    	<td><ul class="button">
    		<li><a class="button-text" href="javascript:validSogOK();">
    		    <span>&nbsp;��֤OK&nbsp;</span>
    		</a></li>
    	</ul></td>
      </tr>
    </table>
<%}%>

<%if (ConstantsMcs.TARGET.equals(mtrGrp)) {%>
    <table border="0" cellspacing="0" cellpadding="0">
      <tr height="30">
        <td class="en11pxb">
            ���±�ע
            <input type="text" class="input" size="20" name="offNote" value="">
        </td>

    	<td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.CABINET_RECYCLE%>','<i18n:message key="mcs.status_cabinet_recycle" />');">
    		    <span class="cabinet">&nbsp;<i18n:message key="mcs.status_cabinet_recycle" />&nbsp;</span>
    		</a></li>
    	</ul></td>

    	<td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.ONLINE_SCRAP_OPT%>', '<i18n:message key="mcs.status_general_scrap" />');">
    		    <span class="onlineScrap">&nbsp;<i18n:message key="mcs.status_online_scrap" />&nbsp;</span>
    		</a></li>
    	</ul></td>

    	<td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.VENDOR_REPAIR_OPT%>', '<i18n:message key="mcs.status_vendor_repair" />');">
    		    <span class="vendor">&nbsp;����&nbsp;</span>
    		</a></li>
    	</ul></td>
      </tr>
    </table>
<%}%>

<br/>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr ondragstart="abnormalInfo()" oncontextmenu="abnormalInfo()" onselectstart="abnormalInfo()">
  <td><fieldset>
      <legend>
          �ȴ�ʹ���ݴ�������(��¼������)�б� ɨ�����룺
          <input type="text" class="input" size="40" name="useBarcode" id="useBarcode" value="" onPropertyChange="selectBarcode(objTableCabinet, objTrCabinet, objCheckboxCabinet, 'use');" onKeyDown="sTime=new Date()" onKeyUp="doScan(this)">
      </legend>
      <table id="objTableCabinet" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      		<td class="en11pxb">
      		    ѡ��
      		    <input type="checkbox" name="checkBoxSelectAllCabinet" id="checkBoxSelectAllCabinet"
      		    class="hand" title="ȫѡ/ȡ��ѡ��" style="display:none;"
      		    <ofbiz:if name="cabinetList" size="0">
      		        onClick="selectAllCheckBox(objTableCabinet, objTrCabinet, objCheckboxCabinet, this.checked);"
      		    </ofbiz:if>
      		    >
      		</td>
      		<td class="en11pxb"><i18n:message key="mcs.index_alias_name" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.vendor_batch_num" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_number" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.material_description" /></td>
      		<td class="en11pxb">����Ч��(��)</td>
      		<td class="en11pxb"><i18n:message key="mcs.shelf_life_expiration_date" /></td>
      		<td class="en11pxb"><i18n:message key="mcs.mrb_date" /></td>

      		<%if (ConstantsMcs.PHOTORESIST.equals(mtrGrp) || isSog) {%>
      		    <td class="en11pxb"><i18n:message key="mcs.unfrozen_time" /></td>
      		<%}%>
    	</tr>

    	<%
    	if(forUseList != null && forUseList.size() > 0) {
    	    for(Iterator it = forUseList.iterator();it.hasNext();) {
				Map map = (Map) it.next();
				String materialStatusIndex = UtilFormatOut.checkNull((String) map.get("MATERIAL_STATUS_INDEX"));
				String useAliasName = UtilFormatOut.checkNull((String) map.get("ALIAS_NAME"));
				String useMtrNum = UtilFormatOut.checkNull((String) map.get("MTR_NUM"));
        %>
            <tr class="tablelist" id="objTrCabinet" >
	        	<td class="en11px">
	        	    <input type="checkbox" name="objCheckboxCabinet" id="objCheckboxCabinet"
	        	    value="<%=materialStatusIndex%>"
	        	    onClick="this.checked=!this.checked;"
	        	    >

	        	    <%=UtilFormatOut.checkNull((String) map.get("REMARK"))%>
	        	</td>

	        	<td class="en11px">
	        	    <a href="javascript:viewStatusHist('<%=materialStatusIndex%>')">
	        	        <%=useAliasName%>
	        	    </a>

	        	    <input type="hidden" name="useAliasName" value="<%=useAliasName%>">
	        	    <input type="hidden" name="useMtrNum" value="<%=useMtrNum%>">
	        	</td>

	        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("VENDOR_BATCH_NUM"))%></td>
	        	<td class="en11px"><%=useMtrNum%></td>
	        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("MTR_DESC"))%></td>

                <td class="en11px">
                    <%=UtilFormatOut.checkNull((String) map.get("LEFT_EXP_DAYS"))%>
	        	</td>
	            <td class="en11px">
	                <%=UtilFormatOut.checkNull((String) map.get("SHELF_LIFE_EXPIRATION_DATE"))%>
	        	</td>
	        	<td class="en11px">
	        	    <%=UtilFormatOut.checkNull((String) map.get("MRB_DATE"))%>
	        	</td>

	        	<%if (ConstantsMcs.PHOTORESIST.equals(mtrGrp) || isSog) {%>
    	        	<td class="en11px">
    	        	    <%=UtilFormatOut.checkNull((String) map.get("UNFROZEN_TIME"))%>
    	        	</td>
	        	<%}%>
	        </tr>
        <%
	        }
	    }
	    %>
      </table>
  </fieldset></td>
</tr>
</table>

<!-- ���ϸ�����ť -->
<%if (mtrGrp==null || mtrGrp.equals("")) {%>
    <table border="0" cellspacing="0" cellpadding="0">
      <tr height="30">
        <td class="must-input">
            ѡ�������飬��ѯ��ʾ�ɲ�������
        </td>
      </tr>
    </table>
<%} else if (ConstantsMcs.TARGET.equals(mtrGrp)) {%>
    <table border="0" cellspacing="0" cellpadding="0">
      <tr height="30" ondragstart="abnormalInfo()" oncontextmenu="abnormalInfo()" onselectstart="abnormalInfo()">
        <td class="en11pxb">
            ���ϱ�ע
            <input type="text" class="input" size="20" name="useNote" value="" />
        </td>

        <td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.USING%>', '<i18n:message key="mcs.status_using" />');">
    		    <span>&nbsp;<i18n:message key="mcs.status_using" />&nbsp;</span>
    		</a></li>
    	</ul></td>
      </tr>
    </table>
<%} else if (ConstantsMcs.PHOTORESIST.equals(mtrGrp) || isSog) {%>
    <table border="0" cellspacing="0" cellpadding="0">
      <tr height="30" ondragstart="abnormalInfo()" oncontextmenu="abnormalInfo()" onselectstart="abnormalInfo()">
        <td class="en11pxb">
            ������ע
            <input type="text" class="input" size="20" name="useNote" value="" />
        </td>

        <td><ul class="button">
    		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.OFF_AND_USE%>', '<i18n:message key="mcs.off_and_use" />');">
    		    <span>&nbsp;<i18n:message key="mcs.off_and_use" />&nbsp;</span>
    		</a></li>
    	</ul></td>
      </tr>
    </table>
<%}%>

</form>

<!-- ##################################### McsScanBarcode checkbox script ################################ -->
<script language="javascript">
<!--
    <ofbiz:iterator name="cust" property="scanBarcodeList" type="java.util.Map">
        //MTR_TYPE�ɹ�̽����£�onPropertyChange �ı�offBarcode value���Զ����ýű� selectBarcode(objTableCabinet, objTrCabinet, objCheckboxCabinet, 'off')�ж�ѡ��
        //useForm.offBarcode.value = "SPR6812X-38F8B067-160506-0063";
        useForm.offBarcode.value = '<ofbiz:inputvalue entityAttr="cust" field="MTR_TYPE" tryEntityAttr="true" />';
    </ofbiz:iterator>

    <ofbiz:iterator name="cust" property="scanBarcodeList" type="java.util.Map">
        //BARCODE�¹�̽�ʹ�ã�onPropertyChange �ı�useBarcode value���Զ����ýű� selectBarcode(objTableCabinet, objTrCabinet, objCheckboxCabinet, 'use')�ж�ѡ��
        //useForm.useBarcode.value = "SPR6812X-38F8B067-160506-0063";
        useForm.useBarcode.value = '<ofbiz:inputvalue entityAttr="cust" field="BARCODE" tryEntityAttr="true" />';
    </ofbiz:iterator>
-->
</script>
<!-- ##################################### end McsScanBarcode checkbox script ################################ -->

<br/>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�ݴ��������б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      		<td>
      		    No.
      		</td>
      		<td><i18n:message key="mcs.index_alias_name" /></td>
      		<td><i18n:message key="mcs.vendor_batch_num" /></td>
      		<td><i18n:message key="mcs.material_number" /></td>
      		<td><i18n:message key="mcs.material_description" /></td>
      		<td><i18n:message key="mcs.shelf_life_expiration_date" /></td>
      		<td><i18n:message key="mcs.mrb_date" /></td>

      		<%if (ConstantsMcs.PHOTORESIST.equals(mtrGrp) || isSog) {%>
          		<td><i18n:message key="mcs.unfrozen_trans_time" /></td>
          		<td><i18n:message key="mcs.unfrozen_time" /></td>
          		<td><i18n:message key="mcs.frozen" /></td>
          	<%}%>
    	</tr>

    	<%
    	int i = 1;
    	if(cabinetList != null && cabinetList.size() > 0) {
    	    for(Iterator it = cabinetList.iterator();it.hasNext();) {
				Map map = (Map) it.next();
				String unfrozenTime = UtilFormatOut.checkNull((String) map.get("UNFROZEN_TIME"));

				double dUnfrozenTime = 0;
				if (!unfrozenTime.equals("")) {
					dUnfrozenTime = Double.parseDouble(unfrozenTime);
				}

				String aliasName = UtilFormatOut.checkNull((String) map.get("ALIAS_NAME"));
				String bgcolor = "#DFE1EC";
				if (strScanBarcode.indexOf(aliasName) > -1) {
				    bgcolor = "orange";
				}
    	%>
	        <tr class="tablelist" style="background-color:<%=bgcolor%>" <%if (dUnfrozenTime > 5) {%>style="color:red"<%}%>>
	        	<td nowrap>
	        	    <%=i++%>
	        	    <%=UtilFormatOut.checkNull((String) map.get("REMARK"))%>
	        	</td>

	        	<td>
	        	    <a href="javascript:viewStatusHist('<%=UtilFormatOut.checkNull((String) map.get("MATERIAL_STATUS_INDEX"))%>')">
	        	        <%=aliasName%>
	        	    </a>
	        	</td>
	        	<td><%=UtilFormatOut.checkNull((String) map.get("VENDOR_BATCH_NUM"))%></td>
	        	<td><%=UtilFormatOut.checkNull((String) map.get("MTR_NUM"))%></td>
	        	<td><%=UtilFormatOut.checkNull((String) map.get("MTR_DESC"))%></td>

	            <td>
	                <%=UtilFormatOut.checkNull((String) map.get("SHELF_LIFE_EXPIRATION_DATE")).substring(0,10)%>
	        	</td>
	        	<td>
	        	    <%=UtilFormatOut.checkNull((String) map.get("MRB_DATE"))%>
	        	</td>

                <%if (ConstantsMcs.PHOTORESIST.equals(mtrGrp) || isSog) {%>
    	        	<td><%=UtilFormatOut.checkNull((String) map.get("UNFROZEN_TRANS_TIME"))%></td>
    	        	<td><%=UtilFormatOut.checkNull((String) map.get("UNFROZEN_TIME"))%></td>
    	        	<td><%=UtilFormatOut.checkNull((String) map.get("FROZEN"))%></td>
	        	<%}%>
	        </tr>
	    <%
	        }
	    }
	    %>

      </table>
  </fieldset></td>
</tr>
</table>

<br>
<p>
	<font color="#FF0000" face="����" size="-1">
		Tips :

		<br>
		&nbsp;1. ʹ���ݴ�������ǰ��������ά�������豸��
		<a href="<ofbiz:url>/queryMaterialEntry?usingObjectId=<%=eqpId%></ofbiz:url>">
		    ��ѯ������Ϣ�趨
		</a>

		<br>
		&nbsp;2. �ݴ��������б������ȴ�ʹ�á��������г���Ч���ڵ����ϡ�
		<a href="<ofbiz:url>/queryOverExpirationEntry</ofbiz:url>">
		    ��ѯ����Ч�ڲ���
		</a>
		����ϵIQC�޸���Ч�ڡ�

		<br>
		&nbsp;3. ��̽��ȴ�ʹ�ã�������¼�����룬�Ҵﵽ����ʱ�䣬С��������ʱ�䡣
	</font>
</p>