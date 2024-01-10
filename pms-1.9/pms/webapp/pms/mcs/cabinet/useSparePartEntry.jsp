<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<%
	String eqpId = UtilFormatOut.checkNull((String)request.getParameter("eqpId"));
    String period = UtilFormatOut.checkNull((String)request.getParameter("period"));
    String flow = UtilFormatOut.checkNull((String)request.getParameter("flow"));

    String mtrGrp = "";
    if ("".equals(flow)) {
        mtrGrp  = UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
    }

    //pms��д�������쳣��ʱ��ʹ�ñ����ӿڣ����浽form hidden��λ
    String eventType = UtilFormatOut.checkNull(request.getParameter("eventType"));
    String eventIndex = UtilFormatOut.checkNull(request.getParameter("eventIndex"));
%>

<!-- ##################################### yui page script ################################ -->
<script language="javascript">
    var oldEqpValue="";

    function mtrGrpChange() {
        document.getElementById("period").value = "";
        document.getElementById("flow").value = "";
    }

    //ѡ���豸
    function equipMentChange() {
        var newEqpValue=Ext.get('eqpId').dom.value;

        if (oldEqpValue!=newEqpValue) {
            var actionURL='<ofbiz:url>/queryEqpIdAndPmTypeList</ofbiz:url>?equipmentId='+newEqpValue;
            Ext.lib.Ajax.formRequest('stoReqEntryForm',actionURL,{success: selectSuccess, failure: commentFailure});
        }

        oldEqpValue=newEqpValue;
    }

    //Զ�̵��óɹ�
    function selectSuccess(o){
         var result = eval('(' + o.responseText + ')');
         if (result!=null & result!=""){
            //�豸�����������ݳ�ʼ��
            var nameSize=result.priodNameArray.length;
            var nameArray=result.priodNameArray;
            var valueArray=result.priodValueArray;
            var pmObj;

            pmObj=document.getElementById("period");
            pmObj.length=1;
            for(var i=0;i<nameSize;i++){
                pmObj.options[pmObj.length]=new Option(nameArray[i],valueArray[i]);
            }

            pmObj.value='<%=period%>';
            periodTypeChange();
        }
    }

    //Զ�̵���ʧ��
    var commentFailure = function(o){
        Ext.MessageBox.alert('Status', 'Unable to connect.');
    };

    //ѡ��������
    function periodTypeChange(){
        periodIndex=Ext.get('period').dom.value;
        var actionURL='<ofbiz:url>/queryJsonFlowList</ofbiz:url>?periodIndex='+periodIndex;
        Ext.lib.Ajax.formRequest('stoReqEntryForm',actionURL,{success: selectPeriodSuccess, failure: commentFailure});
    }

    function selectPeriodSuccess(o){
        var result = eval('(' + o.responseText + ')');
        if (result!=null & result!=""){
            var nameSize=result.flowNameArray.length;
            var nameArray=result.flowNameArray;
            var valueArray=result.flowValueArray;
            var pmObj;
            flowObj=document.getElementById("flow");
            flowObj.length=1;
            for(var i=0;i<nameSize;i++){
                flowObj.options[flowObj.length]=new Option(nameArray[i],valueArray[i]);
            }
        }
        var flowObj=document.getElementById('flow');
        flowObj.value='<%=flow%>'
    }

    Ext.onReady(function(){
    	var mtrGrpDS = new Ext.data.Store({
    	    proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonMtrGrp</ofbiz:url>?useBySuit=Y'}),
    	    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'mtrGrp'}, {name: 'mtrGrpDesc'}]))
    	});

    	var mtrGrpCom = new Ext.form.ComboBox({
    	    store: mtrGrpDS,
    	    displayField: 'mtrGrpDesc',
    	    valueField: 'mtrGrp',
    	    hiddenName: 'mtrGrp',
    	    typeAhead: true,
    	    mode: 'local',
    	    width: 150,
    	    triggerAction: 'all',
    	    emptyText: 'Select a group...',
    	    allowBlank: true
    	});
    	mtrGrpCom.applyTo('mtrGrpSelect');
    	mtrGrpCom.on('select',mtrGrpChange);

    	var mtrGrp = '<%=mtrGrp%>';
    	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(mtrGrp); }});

    	var eqpIdDS = new Ext.data.Store({
            proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentByDeptIndex</ofbiz:url>?equipmentId=<%=eqpId%>'}),
    	    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
        });

        var eqpIdCom = new Ext.form.ComboBox({
    	    store: eqpIdDS,
    	    displayField:'equipmentId',
    	    valueField:'equipmentId',
    	    hiddenName:'eqpId',
    	    typeAhead: true,
    	    mode: 'local',
    	    width: 150,
    	    triggerAction: 'all',
    	    allowBlank: false
        });
        eqpIdCom.applyTo('eqpIdSelect');
        eqpIdCom.on('select',equipMentChange);

        var eqpId = '<%=eqpId%>';
        eqpIdDS.load({params:{deptIndex:''}, callback:function(){ eqpIdCom.setValue(eqpId); equipMentChange();}});
    });
</script>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function queryStoReqList() {
    if (Ext.getDom('eqpId').value == "") {
        Ext.MessageBox.alert('����', '��ѡ���豸');
		return;
	}

    if (Ext.getDom('mtrGrp').value == "" && Ext.getDom('flow').value == "") {
        Ext.MessageBox.alert('����', '��ѡ�� ������ �� ������������');
		return;
	}

	doSubmit("<ofbiz:url>/useSparePartEntry</ofbiz:url>");
}

function doSubmit(url) {
	loading();
	document.stoReqEntryForm.action = url;
	document.stoReqEntryForm.submit();
}

function useMaterial(newStatus, newStatusDesc) {
    if (checkForm(newStatus)) {
        Ext.MessageBox.confirm('����ȷ��', '��ȷ��ѡ�� ' + newStatusDesc + ' ��',
            function result(value) {
                if ( value=="yes" ) {
        			doSubmit("<ofbiz:url>/useSparePart</ofbiz:url>");
        		} else {
        			return;
        		}
            }
        );
    }
}

function checkForm(newStatus) {
    if (totalSelected == 0) {
        Ext.MessageBox.alert('����', '��ѡ��ʹ�õ�����!');
        return false;
    }

    if (Ext.getDom('eqpId').value == "") {
		Ext.MessageBox.alert('����', '��ѡ���豸!');
        return false;
	}

	/*if (Ext.getDom('mtrGrp').value == "") {
		Ext.MessageBox.alert('����', '��ѡ��������!');
        return false;
	}*/

	return true;
}

//�رղ�ѯҳ�棬ˢ�±�����ĸҳ��
function closeWindow() {
    window.close();
    window.opener.document.location.href="<%=request.getContextPath()%>/control/pmRecordInfo?functionType=1&activate=parts&pmIndex=<%=eventIndex%>&eqpId=<%=eqpId%>";
}

<%if ("Y".equals(request.getAttribute("flag"))) {%>
    closeWindow();//�����ر�ҳ�沢ˢ��
<%}%>
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

	if (totalCount > 1) {
	    var objQty = document.getElementsByName("useQty")[theIdx];
	} else {
	    var objQty = document.getElementById("useQty");
	}

	obj.checked = !obj.checked;//CheckBox��Ӱ���ѡ����
	if (obj.checked) {
		varTR.className = "tablelistchecked";
		totalSelected++;

		objQty.disabled = false;
        objQty.focus();

        objQtyOff.disabled = false;
	} else {
		varTR.className = "tablelist";
		totalSelected--;

		objQty.disabled = true;
		objQtyOff.disabled = true;
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
	var varColor;

	totalCount = objTable.rows.length - 1;
	if (totalCount == 0) {
		return;
    }

	if (objCheckbox.length > 1) {
		for(i=0; i < objCheckbox.length; i++){
			objCheckbox[i].checked = isChecked;
			objTr[i].className = isChecked ? "tablelistchecked" : "tablelist";

			document.getElementsByName("useQty")[i].disabled = !isChecked;
		}
	} else {// only one Checkbox
	    objCheckbox.checked = isChecked;
		objTr.className = isChecked ? "tablelistchecked" : "tablelist";

		document.getElementById("useQty").disabled = !isChecked;
	}

	totalSelected = isChecked ? totalCount : 0;
}

function myGetSelectIdx(){//---------ȡ��ѡȡֵ---------------
	var i;
	var varIdx="'";
    for(i=0;i<document.getElementsByName("choose").length;i++){
			if (document.getElementsByName("choose")[i].checked){
				varIdx = varIdx +document.getElementsByName("ReqCard")[i].value+ "','";
		    }
	}
	if (varIdx.length>0)
		varIdx = varIdx.substring(0,varIdx.length-2);
	return varIdx;
}


function  onlyNum(obj){//---------------ֻ����������---------------------
	obj.value = obj.value.replace(/\D/g,"");
}

function checkQtyInput(objQtyInput, totalQty) {//onkeyup,onafterpaste ����ʱ����У��
    onlyNum(objQtyInput);

    var qty = objQtyInput.value;
    if (qty <= 0) {
        objQtyInput.value = "";
        objQtyInput.focus();
    } else if (qty > totalQty) {
        alert('��������������������������������');
        objQtyInput.value = totalQty;
        objQtyInput.focus();
    }
}

function checkInputValue(objQtyInput) {// onblurУ��
     var qty = objQtyInput.value;
    if (qty == "") {
        alert('����������');
        objQtyInput.value = "";
        objQtyInput.focus();
    }
}
-->
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form method="post" name="stoReqEntryForm">
<input id="eventType" type="hidden" name="eventType" value="<%=eventType%>" />
<input id="eventIndex" type="hidden" name="eventIndex" value="<%=eventIndex%>" />
<input id="periodIndex" type="hidden" name="periodIndex" value="<%=period%>" />
<input id="flowIndex" type="hidden" name="flowIndex" value="<%=flow%>" />

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>��������</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
        <tr bgcolor="#DFE1EC">
            <td class="tabletitle" width="12%">
        	    <i18n:message key="mcs.equipment" />
        	    <i18n:message key="mcs.data_required_red_star" />
        	</td>
        	<td colspan="3">
        	    <input type="text" size="40" name="eqpIdSelect" autocomplete="off"/>
        	</td>
        </tr>

		<tr bgcolor="#DFE1EC">
		    <td class="tabletitle" width="12%"><i18n:message key="mcs.material_group" /></td>
			<td><input type="text" size="40" name="mtrGrpSelect" autocomplete="off"/></td>

    		<td class="tabletitle" width="12%">��������</td>
			<td>
			    <select id="period" name="period" class="select-short" onchange="periodTypeChange()">
                    <option value=""></option>
                </select>
			</td>
	    </tr>

	    <tr bgcolor="#DFE1EC">
	        <td class="tabletitle" width="12%"><i18n:message key="mcs.material_number" /></td>
			<td><input type="text"  size="20" name="mtrNum"  value="<%=UtilFormatOut.checkNull(request.getParameter("mtrNum"))%>"/></td>

	        <td class="tabletitle" width="12%">��������</td>
			<td>
			    <select id="flow" name="flow" class="select-short">
                    <option value=""></option>
                </select>
			</td>
		</tr>
	  </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:queryStoReqList();"><span>&nbsp;��ѯ&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<ofbiz:if name="stoReqList" size="0">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>���������б�</legend>
      <table id="objTable" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
      		<td class="en11pxb">
      		    <input type="checkbox" name="checkBoxSelectAll" id="checkBoxSelectAll"
      		    class="hand" title="ȫѡ/ȡ��ѡ��"
      		    <ofbiz:if name="stoReqList" size="0">
      		        onClick="selectAllCheckBox(objTable, objTr, objCheckbox, this.checked);"
      		    </ofbiz:if>
      		    >
      		</td>

      		<td class="en11pxb">No.</td>

      		<td class="en11pxb">
      		    <i18n:message key="mcs.material_number" />
      		</td>

      		<td class="en11pxb">
      		    <i18n:message key="mcs.material_description" />
      		</td>

      		<td class="en11pxb">
      		    <i18n:message key="mcs.doc_time" />
      		</td>

      		<td class="en11pxb">�ܿ��</td>
      		<td class="en11pxb">��������</td>
      		<td class="en11pxb">�豸��������</td>
      		<td class="en11pxb">�����ʹ������ / ��������</td>
    	</tr>

    	<%
    	int i = 0;
    	List list = (List) request.getAttribute("stoReqList");
    	if (list != null && list.size() > 0) {
        	for (Iterator it = list.iterator();it.hasNext();) {
    			Map map = (Map) it.next();
    			String mtrNum = UtilFormatOut.checkNull((String) map.get("MTR_NUM"));
    			String mtrDesc = UtilFormatOut.checkNull((String) map.get("MTR_DESC"));
    			String qty = UtilFormatOut.checkNull((String) map.get("QTY"));
    			String partCount = UtilFormatOut.checkNull((String) map.get("PART_COUNT"));

    			if ("".equals(mtrNum)) {//�����̲�ѯʱ�����ÿ���Ϊ��
    			    mtrNum = UtilFormatOut.checkNull((String) map.get("PART_NO"));
    			}

    			if ("".equals(mtrDesc)) {//�����̲�ѯʱ�����ÿ���Ϊ��
    			    mtrDesc = UtilFormatOut.checkNull((String) map.get("PART_NAME"));
    			}

    			if ("".equals(partCount)) {//δѡ�����̲�ѯʱΪ��
    			    partCount = "1";
    			}

    			String maxUsePeriod = UtilFormatOut.checkNull((String) map.get("MAX_USE_PERIOD"));
    			String usableTimeLimit = UtilFormatOut.checkNull((String) map.get("USABLE_TIME_LIMIT"));

    			double dMaxUsePeriod = 0;
    			if (!maxUsePeriod.equals("")) {
    				dMaxUsePeriod = Double.parseDouble(maxUsePeriod);
    			}

    			double dUsableTimeLimit = 0;
    			if (!usableTimeLimit.equals("")) {
    				dUsableTimeLimit = Double.parseDouble(usableTimeLimit);
    			}
    			if (dUsableTimeLimit <= 0) {
    			    dUsableTimeLimit = 10000;
    			}

    			String alarmColor = "black";
    			if (dMaxUsePeriod+15 >= dUsableTimeLimit) {
    			    alarmColor = "blue";//����15����������
    			}
    			if (dMaxUsePeriod >= dUsableTimeLimit) {
    			    alarmColor = "red";//�ѳ���
    			}

    			i = i + 1;
    	%>
            <tr class="tablelist" id="objTr" style="cursor:hand"
            onClick="javascript:selectOneCheckBox(objTable, objTr, objCheckbox, this.rowIndex, checkBoxSelectAll);"
            style="color:<%=alarmColor%>"
            >
            	<td class="en11px">
            	    <input type="checkbox" name="objCheckbox" id="objCheckbox"
            	    value="<%=mtrNum%>"
            	    onClick="this.checked=!this.checked;"
            	    >
            	</td>

                <td class="en11px"><%=i%></td>
            	<td class="en11px"><%=mtrNum%></td>
            	<td class="en11px"><%=mtrDesc%></td>
                <td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("DOC_TIME"))%></td>

                <td class="en11px"><%=qty%></td>
            	<td class="en11px">
            	    <input type="text" class="input" size="8" name="useQty" id="useQty" value="<%=partCount%>"
      		        disabled="true"
      		        onkeyup="checkQtyInput(this, <%=qty%>)"
      		        onafterpaste="checkQtyInput(this, <%=qty%>)"
      		        onblur="checkInputValue(this)"
      		        />
      		    </td>


            	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("EQP_NUM"))%></td>
            	<td>
	        	    <%=maxUsePeriod%>
	        	    /
	        	    <%=usableTimeLimit%>
	        	</td>
            </tr>
	   	<%
	   	    }
	   	}
	   	%>

      </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
	<td class="tabletitle" width="12%">
        ������ע
        <input type="text" class="input" size="20" name="note" value="" />
    </td>

	<td width="30%"><ul class="button">
		<li><a class="button-text" href="javascript:useMaterial('<%=ConstantsMcs.USING%>', '<i18n:message key="mcs.status_using" />');">
		    <span>&nbsp;<i18n:message key="mcs.status_using" />&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>

</ofbiz:if>
</form>
<br/>