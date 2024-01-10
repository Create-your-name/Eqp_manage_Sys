<!-- KEYPART维护画面 keyPartsInfoList.jsp -->
<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.URLEncoder" %>
<%
    String eqpModel=request.getParameter("eqpModel").replace("aaa", "+");
    String error_message=(String)request.getAttribute("_ERROR_MESSAGE_");
    String error_flag=(String)request.getAttribute("flag");
    int size = 0;
    List list = (List) request.getAttribute("partsNoList");
    if (list != null){
        size = list.size();
    }
%>
<base target="_self">
<!-- yui page script-->
<script language="javascript">
    //查询
    function partsQuery() {
        var mtrGrpValue = Ext.get('mtrGrp').dom.value;
        if (mtrGrpValue == '') {
            Ext.MessageBox.alert('警告', "请选择物料组!");
            return;
        }

        loading();
        partsDataQueryForm.submit();
    }
   
    //物料保存
    
    function isChecked() {
        for (i = 0; i < <%=size%>; i++) {
            var checkObj = document.getElementById("parts_" + i);
            if (checkObj.checked == true) {
                return true;
            }
        }
        return false;
    }
    
    function partSave() {
        var checked = isChecked();
        if (!checked) {
            alert("没有选择物料，请确认!");
            return;
        }
        for (i = 0; i < <%=size%>; i++) {
            var checkObj = document.getElementById("parts_" + i);
            var alarmObj = document.getElementById("ifalarm_" + i);
            var keydescObj = document.getElementById("keydesc_" + i);
            var use_numberObj = document.getElementById("use_number_" + i);
            var sectionObj = document.getElementById("section_" + i);
            if (checkObj.checked == true) {
                if (keydescObj.value == "") {
                    alert("已选择的物料，必须输入关键字");
                    return;
                }
                if (alarmObj.value == "Y") {
                    var inputObj = document.getElementById("error_line" + i);
                    var inputObj_warm = document.getElementById("warm_line" + i);
                    if (inputObj.value == "") {
                        alert("报警关键备件，必须输入寿命报警线!");
                        return;
                    } else if (isNaN(inputObj.value)) {
                        alert("错误报警线必须为数字!");
                        return;
                    }
                    if (use_numberObj.value == "") {
                        alert("使用数量不能为空");
                        return;
                    } else if (isNaN(use_numberObj.value)) {
                        alert("使用数量必须为数字");
                        return;
                    }
                    var reg = /^[+]{0,1}(\d+)$|^[+]{0,1}(\d+\.\d+)$/;//Js验证非负数(0,正整数和正小数) 
                    if (reg.test(use_numberObj.value) == false) {
                        alert("使用数量不能为负值");
                        return "";
                    }
                    if (sectionObj.value == "") {
                        alert("报警关键备件，必须选择通知相关组!");
                        return;
                    }
                    if (parseInt(inputObj_warm.value) > parseInt(inputObj.value)) {
                        alert("预警线必须小于寿命报警线!");
                        return;
                    }
                }
                var inputRemarkValue = document.getElementById("remark_" + i).value;
                if (inputRemarkValue.length > 50) {
                    alert("备注长度不能超过50个字符!!");
                    return;
                }
            }
        }
        loading();
        var mtrGrp = document.getElementById("mtrGrp").value;
        var partNo = document.getElementById("partNo").value;
        var url = document.getElementById("partsDataSaveForm").action;
        partsDataSaveForm.action = url + "?mtrGrp=" + mtrGrp + "&partNo=" + partNo;
        partsDataSaveForm.submit();
    }

	Ext.onReady(function(){
        if("Y" == "<%=request.getAttribute("flag")%>") {
            window.opener.document.location.href="<%=request.getContextPath()%>/control/keyEqpPartsList?eqp_Model=<%=eqpModel%>";
            window.close();
        }        
    });

</script>

<form action="<%=request.getContextPath()%>/control/queryPartsDataForKeyParts" method="post" id="partsDataQueryForm">
    <input id="eqpModel" type="hidden" name="eqpModel" value="<%=eqpModel%>" />
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>
                <fieldset>
                    <legend>物料信息查询参数</legend>
                    <table width="100%" border="0" cellspacing="1" cellpadding="2">
                        <tr bgcolor="#DFE1EC">
                            <td width="12%" class="en11pxb">选择物料:</td>
                            <td width="28%">
                                <select id="mtrGrp" name="mtrGrp">
                                    <option value=""></option>
                                    <option value="20002P">20002P(备件)</option>
                                    <option value="20002S">20002S(易耗备件)</option>
                                    <option value="20002T">20002T(无料号备件)</option>
                                    <option value="100018">石英</option>
                                </select>
                            </td>
                        </tr>
                        <tr bgcolor="#DFE1EC">
                            <td width="12%" class="en11pxb">物料号:</td>
                            <td width="28%">
                                <input class="input" type="text" name="partNo" id="partNo" value="" tabindex="1" />
                            </td>
                        </tr>
                        <tr bgcolor="#DFE1EC">
                            <td width="20%" class="en11pxb" align="left" colspan="2">
                                <table border="0" cellspacing="0" cellpadding="0">
                                    <tr height="30">
                                        <td width="20">&nbsp;</td>
                                        <td>
                                            <ul class="button">
                                                <li>
                                                    <a class="button-text" href="#" onclick="javascript:partsQuery();">
                                                        <span>&nbsp;查询&nbsp;</span>
                                                    </a>
                                                </li>
                                            </ul>
                                            <ul class="button">
                                                <li>
                                                    <a class="button-text" href="#" onclick="javascript:partSave();">
                                                        <span>&nbsp;保存&nbsp;</span>
                                                    </a>
                                                </li>
                                            </ul>
                                            <ul class="button">
                                                <li>
                                                    <a class="button-text" href="#" onclick="javascript:window.close();">
                                                        <span>&nbsp;关闭&nbsp;</span>
                                                    </a>
                                                </li>
                                            </ul>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </fieldset>
            </td>
        </tr>
    </table>
</form>
<br>
<form action="<%=request.getContextPath()%>/control/keyPartsSaveEntry" method="post" id="partsDataSaveForm">
    <input id="eqpModel" type="hidden" name="eqpModel" value="<%=eqpModel%>" />
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>
                <fieldset>
                    <legend>物料信息列表</legend>
                    <table width="100%" border="0" cellspacing="1" cellpadding="2">
                        <tr bgcolor="#ACD5C9">
                            <td class="en11pxb">选择</td>
                            <td class="en11pxb">物料号</td>
                            <td class="en11pxb">物料描述</td>
                            <td class="en11pxb">Model</td>
                            <td class="en11pxb">关键字</td>
                            <td class="en11pxb">使用数量</td>
                            <td class="en11pxb">寿命报警线</td>
                            <td class="en11pxb">预警线</td>
                            <td class="en11pxb">寿命定义</td>
                            <td class="en11pxb">备注</td>
                            <td class="en11pxb">是否报警</td>
                            <td class="en11pxb">是否必换</td>
                            <td class="en11pxb">是否启用</td>
                            <td class="en11pxb">通知组</td>
                        </tr>
                        <% 
                            List partsUseList=(List)request.getAttribute("partsNoList");
                            if(partsUseList != null && partsUseList.size() > 0) { 
                                int k = 0;
                                for(Iterator it = partsUseList.iterator();it.hasNext();) {
                                    Map map = (Map)it.next();            
                        %>
                        <tr bgcolor="#DFE1EC">
                            <td class="en11px">
                                <input type="checkBox" id="parts_<%=k%>" name="parts_<%=k%>" value='<%=map.get("PART_NO")%>' />
                                
                                <input id="partsMtrGrp_<%=k%>" type="hidden" name="partsMtrGrp_<%=k%>" value='<%=map.get("PART_NO")%>' />
                            </td>
                            <td class="en11px"><%=map.get("PART_NO")%></td>
                            <td class="en11px">
                                <input id="partsName_<%=k%>" type="hidden" name="partsName_<%=k%>" value='<%=map.get("PART_NAME")%>' />
                                <%=map.get("PART_NAME")%>
                            </td>
                            <td class="en11px">
                                <input id="eqp_model<%=k%>" name="eqp_model<%=k%>" class="input" value="<%=eqpModel%>" readonly="readonly" style="background:E9E9E9" />
                            </td>
                            <td class="en11px">
                                <input id="keydesc_<%=k%>" name="keydesc_<%=k%>" class="input" size=10 type="text" style="background:FFFFFF" />
                            </td>
                            <td class="en11px">
                                <input id="use_number_<%=k%>" name="use_number_<%=k%>" class="input" size=10 type="text" value="1" style="background:FFFFFF" />
                            </td>
                            <td class="en11px">
                                <input id="error_line<%=k%>" name="error_line<%=k%>" class="input" size=3 type="text" style="background:FFFFFF" />
                            </td>
                            <td class="en11px">
                                <input id="warm_line<%=k%>" name="warm_line<%=k%>" class="input" size=3 type="text" style="background:FFFFFF" />
                            </td>
                            <td class="en11px">
                                <select id="limit_type<%=k%>" name="limit_type<%=k%>">
                                    <option value="TIME(天)">TIME(天)</option>
                                    <option value="WAFERCOUNT">WAFERCOUNT</option>
                                    <option value="RFTIME(Hours)">RFTIME(Hours)</option>
                                </select>
                            </td>
                            <td class="en11px">
                                <input id="remark_<%=k%>" name="remark_<%=k%>" class="input" size=10 type="text" style="background:FFFFFF" />
                            </td>
                            <td class="en11px">
                                <select id="ifalarm_<%=k%>" name="ifalarm_<%=k%>">
                                    <option value="Y">Y</option>
                                    <option value="N">N</option>
                                </select>
                            </td>
                            <td class="en11px">
                                <select id="mustchange_<%=k%>" name="mustchange_<%=k%>">
                                    <option value="Y">Y</option>
                                    <option value="N">N</option>
                                </select>
                            </td>
                            <td class="en11px">
                                <select id="enable_<%=k%>" name="enable_<%=k%>">
                                    <option value="Y">Y</option>
                                    <option value="N">N</option>
                                </select>
                            </td>
                            <td class="en11px">
                                <select id="section_<%=k%>" name="section_<%=k%>">
                                    <option></option>
                                    <ofbiz:if name="SectionList">
                                        <ofbiz:iterator name="cust" property="SectionList">
                                            <option value='<ofbiz:inputvalue entityAttr="cust" field="sectionIndex"/>'>
                                                <ofbiz:inputvalue entityAttr="cust" field="section" />
                                            </option>
                                        </ofbiz:iterator>
                                    </ofbiz:if>
                                </select>
                            </td>
                        </tr>
                        <%
                                k++;
                                }
                            }
                        %>
                    </table>
                </fieldset>
            </td>
        </tr>
    </table>
</form>

<script language="javascript">
    //查询后的页面的初始化
    Ext.get('partNo').dom.value='<%=UtilFormatOut.checkNull(request.getParameter("partNo"))%>'
    Ext.get('mtrGrp').dom.value='<%=UtilFormatOut.checkNull(request.getParameter("mtrGrp"))%>'
    Ext.get('eqpModel').dom.value='<%=UtilFormatOut.checkNull(request.getParameter("eqpModel"))%>'
    <ofbiz:if name="flag" value="OK">		
        var obj=document.getElementById('eqpModel');
        obj.value='<%=eqpModel%>' 
    </ofbiz:if> 
</script>