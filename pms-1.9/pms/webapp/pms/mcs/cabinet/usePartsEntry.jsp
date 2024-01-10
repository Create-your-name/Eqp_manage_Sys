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
			Ext.MessageBox.alert('警告', '请输入使用数量');
			return;
		}
        loading();
        document.partsUseForm.action = url;
        document.partsUseForm.submit();
	}

    function savePartsUse(eventIndex, periodIndex, flowIndex) {
        Ext.MessageBox.confirm('提交确认','确定使用该备耗件吗？', function result(value){
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

    function  onlyNum(obj){//---------------只能输入数字---------------------
        obj.value = obj.value.replace(/\D/g,"");
    }

    function checkQtyInput(objQtyInput, totalQty) {//onkeyup,onafterpaste 输入时立刻校验
        onlyNum(objQtyInput);

        var qty = objQtyInput.value;
        if (qty <= 0) {
            objQtyInput.value = "";
            objQtyInput.focus();
        } else if (qty > totalQty) {
            alert('输入数量大于可用数量，请重新输入');
            objQtyInput.value = totalQty;
            objQtyInput.focus();
        }
    }

    function checkInputValue(objQtyInput) {// onblur校验
        var qty = objQtyInput.value;
        if (qty == "") {
            alert('请输入使用数量');
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
                    <legend>备耗件使用</legend>
                    <table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">
                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">物料号</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="stoReqInfo" field="mtrNum" />
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">物料描述</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="stoReqInfo" field="mtrDesc" />
                            </td>
                        </tr>

                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">供应商批号</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="stoReqInfo" field="batchNum" />
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">领料时间</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="stoReqInfo" field="docTime" />
                            </td>
                        </tr>

                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">收货人</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="stoReqInfo" field="recipient" />
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">申请原因</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="stoReqInfo" field="reasonForMovement" />
                            </td>
                        </tr>

                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">使用数量</td>
                            <td width="40%">
                                <input name="partCount" type="number" style="width:100px" class="input" value="1"
                                onkeyup="checkQtyInput(this, <ofbiz:field attribute='availableQty'/>)"
                                onafterpaste="checkQtyInput(this, <ofbiz:field attribute='availableQty'/>)"
                                onblur="checkInputValue(this)"
                                >
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">可用/总领用</td>
                            <td width="40%">
                                <ofbiz:field attribute="availableQty"/> /
                                <ofbiz:entityfield attribute="stoReqInfo" field="qty" />
                            </td>
                        </tr>

                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">可用设备</td>
                            <td width="40%">
                                <select id="eqpId" name="eqpId" style="width:100px" class="select">
                                    <ofbiz:if name="eqpList">
                                    <ofbiz:iterator name="cust" property="eqpList">
                                        <option value='<ofbiz:inputvalue entityAttr="cust" field="usingObjectId"/>'><ofbiz:inputvalue entityAttr="cust" field="usingObjectId"/></option>
                                    </ofbiz:iterator>
                                    </ofbiz:if>
                                </select>
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">表单类型</td>
                            <td width="40%">
                                <select id="eventType" name="eventType" style="width:100px" class="select">
                                    <option value="PM">保养表单</option>
                                    <option value="TS">异常表单</option>
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
                    <legend>关键备件信息</legend>
                    <table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">
                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">设备Model</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="keyPartInfo" field="eqpType" />
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">关键字</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="keyPartInfo" field="keydesc" />
                            </td>
                        </tr>

                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">寿命定义</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="keyPartInfo" field="limitType" />
                            </td>
                            <td width="10%" bgcolor="#ACD5C9">使用数量</td>
                            <td width="40%">
                                <ofbiz:entityfield attribute="keyPartInfo" field="useNumber" />
                            </td>
                        </tr>

                        <tr bgcolor="#DFE1EC">
                            <td width="10%" bgcolor="#ACD5C9">供应商</td>
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
                            <td width="10%" bgcolor="#ACD5C9">备注</td>
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
                            <span>&nbsp;查询表单&nbsp;</span>
                        </a>
                    </li>
                </ul>
            </td>
            <td>
                <ul class="button">
                    <li>
                        <a class="button-text" href="javascript:history.back(-1)">
                            <span>&nbsp;返回&nbsp;</span>
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
                        <legend>保养表单列表</legend>
                        <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr bgcolor="#ACD5C9">
                                <td width="5%" class="en11pxb"></td>
                                <td width="20%" class="en11pxb">表单编号</td>
                                <td width="10%" class="en11pxb">表单类型</td>
                                <td width="10%" class="en11pxb">撰写人</td>
                                <td width="10%" class="en11pxb">保养类别</td>
                                <td width="10%" class="en11pxb">实际工时</td>
                                <td width="15%" class="en11pxb">开始时间</td>
                                <td width="15%" class="en11pxb">结束时间</td>
                            </tr>
                            <ofbiz:iterator name="cust" property="pmFormList" type="java.util.Map">
                                <tr bgcolor="#DFE1EC">
                                    <td class="en11px">
                                        <a href="#" onclick="savePartsUse('<ofbiz:inputvalue entityAttr="cust" field="PM_INDEX"/>', '<ofbiz:inputvalue entityAttr="cust" field="PERIOD_INDEX"/>', '<ofbiz:inputvalue entityAttr="cust" field="FLOW_INDEX"/>')">
                                            选择
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
                        <legend>异常记录表单列表</legend>
                        <table width="100%" border="0" cellspacing="1" cellpadding="2">
                            <tr bgcolor="#ACD5C9">
                                <td width="20%" class="en11pxb">表单编号</td>
                                <td width="10%" class="en11pxb">表单类型</td>
                                <td width="10%" class="en11pxb">撰写人</td>
                                <td width="10%" class="en11pxb">故障现象</td>
                                <td width="10%" class="en11pxb">实际工时</td>
                                <td width="15%" class="en11pxb">发生时间</td>
                                <td width="15%" class="en11pxb">结束时间</td>
                            </tr>
                            <ofbiz:iterator name="cust" property="abnormalFormList" type="java.util.Map">
                                <tr bgcolor="#DFE1EC">
                                    <td class="en11px">
                                        <a href="#" onclick="savePartsUse('<ofbiz:inputvalue entityAttr="cust" field="ABNORMAL_INDEX"/>', '<ofbiz:inputvalue entityAttr="cust" field="PERIOD_INDEX"/>', '<ofbiz:inputvalue entityAttr="cust" field="FLOW_INDEX"/>')">
                                            选择
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