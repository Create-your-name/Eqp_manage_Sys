<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>


<%
    GenericValue stoReqInfo = (GenericValue) request.getAttribute("stoReqInfo");
%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
    Ext.onReady(function(){
        Ext.getDom('eqpId').value = '<ofbiz:field attribute="eqpId"/>';
        Ext.getDom('eventType').value = '<ofbiz:field attribute="eventType"/>';
    });

	function doSubmit(url)
	{
		if(Trim(Ext.getDom('partCount').value)=='') {
			Ext.MessageBox.alert('����', '������ʹ������');
			return;
		}
        loading();
        document.partsUseForm.action = url;
        document.partsUseForm.submit();
	}

    function savePartsUse(eventIndex, periodIndex, flowIndex) {
        Ext.MessageBox.confirm('�ύȷ��','ȷ��ʹ�øñ��ļ���', function result(value){
			if(value=="yes"){
				var url='<ofbiz:url>/savePartsUse</ofbiz:url>?eventIndex=' + eventIndex + '&periodIndex=' + periodIndex + '&flowIndex=' + flowIndex;
                loading();
                document.partsUseForm.action = url;
                document.partsUseForm.submit();
			}else{
				return;
			}
		});
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
            alert('�����������ڿ�������������������');
            objQtyInput.value = totalQty;
            objQtyInput.focus();
        }
    }

    function checkInputValue(objQtyInput) {// onblurУ��
        var qty = objQtyInput.value;
        if (qty == "") {
            alert('������ʹ������');
            objQtyInput.value = "";
            objQtyInput.focus();
        }
    }
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>

<form name="partsUseForm" method="POST">
    <input name="reqIndex" type="hidden" value="<ofbiz:field attribute='reqIndex'/>">
    <input name="partNo" type="hidden" value="<ofbiz:field attribute='partNo'/>">
    <input name="keyPartsId" type="hidden" value="<ofbiz:inputvalue entityAttr='keyPartInfo' field='keyPartsId'/>">
    <input name="eqpModel" type="hidden" value="<ofbiz:entityfield attribute='keyPartInfo' field='eqpType'/>">

    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td valign="top">
                <fieldset>
                    <legend>���ļ�ʹ��</legend>
                    <table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">
                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">���Ϻ�</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="stoReqInfo" field="mtrNum" />
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">��������</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="stoReqInfo" field="mtrDesc" />
                            </td>
                        </tr>

                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">��Ӧ������</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="stoReqInfo" field="batchNum" />
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">����ʱ��</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="stoReqInfo" field="docTime" />
                            </td>
                        </tr>

                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">�ջ���</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="stoReqInfo" field="recipient" />
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">����ԭ��</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="stoReqInfo" field="reasonForMovement" />
                            </td>
                        </tr>

                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">ʹ������</td>
                            <td width="40%">
                                <input name="partCount" type="number" style="width:100px" class="input" value="1"
                                onkeyup="checkQtyInput(this, <ofbiz:field attribute='availableQty'/>)"
                                onafterpaste="checkQtyInput(this, <ofbiz:field attribute='availableQty'/>)"
                                onblur="checkInputValue(this)"
                                >
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">����/������</td>
                            <td width="40%">
                                <ofbiz:field attribute="availableQty"/> /
                                <ofbiz:entityfield attribute="stoReqInfo" field="qty" />
                            </td>
                        </tr>

                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">�����豸</td>
                            <td width="40%">
                                <select id="eqpId" name="eqpId" style="width:100px" class="select">
                                    <ofbiz:if name="eqpList">
                                    <ofbiz:iterator name="cust" property="eqpList">
                                        <option value='<ofbiz:inputvalue entityAttr="cust" field="usingObjectId"/>'><ofbiz:inputvalue entityAttr="cust" field="usingObjectId"/></option>
                                    </ofbiz:iterator>
                                    </ofbiz:if>
                                </select>
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">������</td>
                            <td width="40%">
                                <select id="eventType" name="eventType" style="width:100px" class="select">
                                    <option value="PM">������</option>
                                    <option value="TS">�쳣��</option>
                                </select>
                            </td>
                        </tr>

                    </table>
                </fieldset>
        </tr>
    </table>

    <ofbiz:if name="keyPartInfo">
    <br>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td valign="top">
                <fieldset>
                    <legend>�ؼ�������Ϣ</legend>
                    <table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">
                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">�豸Model</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="keyPartInfo" field="eqpType" />
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">�ؼ���</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="keyPartInfo" field="keydesc" />
                            </td>
                        </tr>

                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">��������</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="keyPartInfo" field="limitType" />
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">ʹ������</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="keyPartInfo" field="useNumber" />
                            </td>
                        </tr>

                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">��Ӧ��</td>
                            <td width="40%">
                                <select id="vendor" name="vendor" style="width:100px" class="select">
                                    <ofbiz:if name="partsVendors">
                                    <ofbiz:iterator name="cust" property="partsVendors">
                                        <option value='<ofbiz:inputvalue entityAttr="cust" field="vendors"/>'><ofbiz:inputvalue entityAttr="cust" field="vendors"/></option>
                                    </ofbiz:iterator>
                                    </ofbiz:if>
                                </select>
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">Series No.</td>
                            <td width="40%">
                                <input name="seriesNo" type="number" class="input">
                            </td>
                        </tr>

                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">baseS/N</td>
                            <td width="40%">
                                <input name="baseSn" type="number" class="input">
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">��ע</td>
                            <td width="40%">
                                <input name="remark" type="number" class="input">
                            </td>
                        </tr>

                    </table>
                </fieldset>
        </tr>
    </table>
    </ofbiz:if>

    <br>

    <table border="0" cellspacing="0" cellpadding="0">
        <tr height="30">
            <td>
                <ul class="button">
                    <li>
                        <a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryOverFormByEqpId</ofbiz:url>')">
                            <span>&nbsp;��ѯ��&nbsp;</span>
                        </a>
                    </li>
                </ul>
            </td>
            <td>
                <ul class="button">
                    <li>
                        <a class="button-text" href="javascript:history.back(-1)">
                            <span>&nbsp;����&nbsp;</span>
                        </a>
                    </li>
                </ul>
            </td>
        </tr>
    </table>

    <br>

    <ofbiz:if name="pmFormList">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td>
                    <fieldset>
                        <legend>�������б�</legend>
                        <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr bgcolor="#ACD5C9">
                                <td width="5%" class="en11pxb"></td>
                                <td width="20%" class="en11pxb">�����</td>
                                <td width="10%" class="en11pxb">������</td>
                                <td width="10%" class="en11pxb">׫д��</td>
                                <td width="10%" class="en11pxb">�������</td>
                                <td width="10%" class="en11pxb">ʵ�ʹ�ʱ</td>
                                <td width="15%" class="en11pxb">��ʼʱ��</td>
                                <td width="15%" class="en11pxb">����ʱ��</td>
                            </tr>
                            <ofbiz:iterator name="cust" property="pmFormList" type="java.util.Map">
                                <tr bgcolor="#DFE1EC">
                                    <td class="en11px">
                                        <a href="#" onclick="savePartsUse('<ofbiz:inputvalue entityAttr="cust" field="PM_INDEX"/>', '<ofbiz:inputvalue entityAttr="cust" field="PERIOD_INDEX"/>', '<ofbiz:inputvalue entityAttr="cust" field="FLOW_INDEX"/>')">
                                            ѡ��
                                        </a>
                                    </td>
                                    <td class="en11px">
                                        <ofbiz:inputvalue entityAttr="cust" field="PM_NAME" tryEntityAttr="true" />
                                    </td>
                                    <td class="en11px">
                                        <ofbiz:inputvalue entityAttr="cust" field="FORM_TYPE" tryEntityAttr="true" />
                                    </td>
                                    <td class="en11px">
                                        <ofbiz:inputvalue entityAttr="cust" field="ACCOUNT_NAME" tryEntityAttr="true" />
                                    </td>
                                    <td class="en11px">
                                        <ofbiz:inputvalue entityAttr="cust" field="PERIOD_NAME" tryEntityAttr="true" />
                                    </td>
                                    <td class="en11px">
                                        <ofbiz:inputvalue entityAttr="cust" field="MAN_HOUR" tryEntityAttr="true" />
                                    </td>
                                    <td class="en11px">
                                        <ofbiz:inputvalue entityAttr="cust" field="START_TIME" tryEntityAttr="true" />
                                    </td>
                                    <td class="en11px">
                                        <ofbiz:inputvalue entityAttr="cust" field="END_TIME" tryEntityAttr="true" />
                                    </td>
                                </tr>
                            </ofbiz:iterator>
                        </table>
                    </fieldset>
                </td>
            </tr>
        </table>
    </ofbiz:if>
    
    <ofbiz:if name="abnormalFormList">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td>
                    <fieldset>
                        <legend>�쳣��¼���б�</legend>
                        <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr bgcolor="#ACD5C9">
                                <td width="20%" class="en11pxb">�����</td>
                                <td width="10%" class="en11pxb">������</td>
                                <td width="10%" class="en11pxb">׫д��</td>
                                <td width="10%" class="en11pxb">��������</td>
                                <td width="10%" class="en11pxb">ʵ�ʹ�ʱ</td>
                                <td width="15%" class="en11pxb">����ʱ��</td>
                                <td width="15%" class="en11pxb">����ʱ��</td>
                            </tr>
                            <ofbiz:iterator name="cust" property="abnormalFormList" type="java.util.Map">
                                <tr bgcolor="#DFE1EC">
                                    <td class="en11px">
                                        <a href="#" onclick="savePartsUse('<ofbiz:inputvalue entityAttr="cust" field="ABNORMAL_INDEX"/>', '<ofbiz:inputvalue entityAttr="cust" field="PERIOD_INDEX"/>', '<ofbiz:inputvalue entityAttr="cust" field="FLOW_INDEX"/>')">
                                            ѡ��
                                        </a>
                                    </td>
                                    <td class="en11px">
                                        <ofbiz:inputvalue entityAttr="cust" field="FORM_TYPE" />
                                    </td>
                                    <td class="en11px">
                                        <ofbiz:inputvalue entityAttr="cust" field="CREATE_NAME" />
                                    </td>
                                    <td class="en11px">
                                        <ofbiz:inputvalue entityAttr="cust" field="ABNORMAL_TEXT" />
                                    </td>
                                    <td class="en11px">
                                        <ofbiz:inputvalue entityAttr="cust" field="MAN_HOUR" />
                                    </td>
                                    <td class="en11px">
                                        <ofbiz:inputvalue entityAttr="cust" field="ABNORMAL_TIME" />
                                    </td>
                                    <td class="en11px">
                                        <ofbiz:inputvalue entityAttr="cust" field="END_TIME" />
                                    </td>
                                </tr>
                            </ofbiz:iterator>
                        </table>
                    </fieldset>
                </td>
            </tr>
        </table>
    </ofbiz:if>
</form>