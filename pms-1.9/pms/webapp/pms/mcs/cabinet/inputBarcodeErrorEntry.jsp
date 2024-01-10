<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function saveBarcode() {
    doSubmit("<ofbiz:url>/inputBarcodeError</ofbiz:url>");
}

function doSubmit(url) {
	loading();
	document.inputForm.action = url;
	document.inputForm.submit();
}

</script>

<!-- ##################################### checkbox script ################################ -->
<script language="javascript">
var totalSelected = 0;                       //ѡȡ������;
var totalCount = 0;                          //���ϵ�����;

function selectOneCheckBox(objTable, objTr, objCheckbox, rowIndex, checkBoxSelectAll) {
    //--------------rowIndex=1,2,3... theIdx=0,1,2,3......--------------
    totalCount = objTable.rows.length - 1;
    var theIdx = rowIndex - 1;
    var obj = (objCheckbox.length > 1) ? objCheckbox[theIdx] : objCheckbox;
    var varTR = (objTr.length > 1) ? objTr[theIdx] : objTr;

    if (totalCount > 1) {
        var objQty = document.getElementsByName("newAliasName")[theIdx];
    } else {
        var objQty = document.getElementById("newAliasName");
    }

    obj.checked = !obj.checked;//CheckBox��Ӱ���ѡ����
    if (obj.checked) {
        varTR.className = "tablelistchecked";
        totalSelected++;

        objQty.focus();
    } else {
        varTR.className = "tablelist";
        totalSelected--;

    }
    if (objCheckbox.length > 1) {
        for(i=0; i < objCheckbox.length; i++){
            isChecked = objCheckbox[i].checked;
            objTr[i].className = isChecked ? "tablelistchecked" : "tablelist";

        }
    } else {
        isChecked = objCheckbox.checked;
        objTr.className = isChecked ? "tablelistchecked" : "tablelist";

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

        }
    } else {// only one Checkbox
        objCheckbox.checked = isChecked;
        objTr.className = isChecked ? "tablelistchecked" : "tablelist";

    }

    totalSelected = isChecked ? totalCount : 0;
}
</script>
<!-- ##################################### html content ################################ -->
<form method="post" name="inputForm">


<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�����б�</legend>
      <table id="objTable" width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr class="tabletitle">
          	<td class="en11pxb">
                <input type="checkbox" name="checkBoxSelectAll" id="checkBoxSelectAll"
                class="hand" title="ȫѡ/ȡ��ѡ��"
                <ofbiz:if name="statusList" size="0">
                    onClick="selectAllCheckBox(objTable, objTr, objCheckbox, this.checked);"
                </ofbiz:if>
                >
            </td>
      		<td class="en11pxb">�������</td>
      		<td class="en11pxb">���������</td>
      		<td class="en11pxb">Trans By</td>
      		<td class="en11pxb">Update Time</td>
    	</tr>
    	<ofbiz:if name="statusList" size="0">
	    <ofbiz:iterator name="cust" property="statusList" type="java.util.Map">
	            <tr class="tablelist" id="objTr" onClick="javascript:selectOneCheckBox(objTable, objTr, objCheckbox, this.rowIndex, checkBoxSelectAll);">
        	        <td class="en11px">
                    <input type="checkbox" name="objCheckbox" id="objCheckbox"
                    value="<ofbiz:inputvalue entityAttr="cust" field="materialStatusIndex" tryEntityAttr="true" /><%=ConstantsMcs.SEPARATOR%><ofbiz:entityfield attribute="cust" field="aliasName" />"
                    onClick="this.checked=!this.checked;"
                    >
                </td>
				<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="aliasName" tryEntityAttr="true" /></td>
	        	<td class="en11px"><input type="text" class="input" disabled= disabled size="50" name="newAliasName" id="newAliasName" value="<ofbiz:inputvalue entityAttr="cust" field="aliasName" tryEntityAttr="true" />X"</td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="transBy" tryEntityAttr="true" /></td>
	        	<td class="en11px"><ofbiz:inputvalue entityAttr="cust" field="updateTime" tryEntityAttr="true" /></td>
	        </tr>
	   	</ofbiz:iterator>
	   	</ofbiz:if>

      </table>
  </fieldset></td>
</tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
	<td><ul class="button">
		<li><a class="button-text" href="javascript:saveBarcode();">
		    <span class="cabinet">&nbsp;����&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>


</form>