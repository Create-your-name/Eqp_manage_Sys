<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>


<%
    String eqpId=(String)request.getAttribute("eqpId");
    String eventIndex=(String)request.getAttribute("eventIndex");
    String formreturn=request.getParameter("formreturn");
    String partsUseId=request.getParameter("partsUseId");
    String eventType = (String)request.getAttribute("eventType");
%>

<base target="_self">
<!-- yui page script-->
<script language="javascript">

    //����������Ϣ����
    function partBatchNumSave(reqIndex){
        debugger;
        var url="<%=request.getContextPath()%>/control/saveBatchNumForPartsUse?reqIndex=" + reqIndex + "&partsUseId=<%=partsUseId%>" + "&eqpId=<%=eqpId%>";
        partBatchSaveForm.action=url
        loading();
        partBatchSaveForm.submit();
    }

    //�رղ�ѯҳ�棬ˢ��ĸҳ��
	function closeWindow(){
        window.close();
        if("PM"=="<%=eventType%>") {
            window.opener.document.location.href="<%=request.getContextPath()%>/control/pmRecordInfo?functionType=1&activate=parts&pmIndex=<%=eventIndex%>&eqpId=<%=eqpId%>";
        }
        else if("TS"=="<%=eventType%>") {
            window.opener.document.location.href="<%=request.getContextPath()%>/control/intoAbnormalFormManageEntry?functionType=1&activate=parts&abnormalIndex=<%=eventIndex%>&eqpId=<%=eqpId%>";
        }
	}

	Ext.onReady(function(){
        if("Y" == "<%=request.getAttribute("flag")%>") {
            window.close();
        }        
    });
    

    <%if ("Y".equals(request.getAttribute("flag"))) {%>
        closeWindow();//�����ر�ҳ�沢ˢ��
    <%}%>
</script>


<form action="" method="post" id="partBatchSaveForm">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>
                <fieldset>
                    <legend>����������Ϣ</legend>
                    <table width="100%" border="0" cellspacing="1" cellpadding="2">
                        <tr bgcolor="#ACD5C9">
                            <td class="en11pxb">���Ϻ�</td>
                            <td class="en11pxb">������</td>
                            <td class="en11pxb">����</td>
                            <td class="en11pxb">�������</td>
                        </tr>
                        <ofbiz:if name="partBatchNumList">
                            <ofbiz:iterator name="cust" property="partBatchNumList" type="java.util.Map">
                                <tr bgcolor="#DFE1EC">
                                    <td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_NUM"/></td>
                                    <td class="en11px"><ofbiz:entityfield attribute="cust" field="MTR_DESC"/></td>
                                    <td class="en11px">
                                        <a href="#" onclick="partBatchNumSave('<ofbiz:entityfield attribute="cust" field="MATERIAL_STO_REQ_INDEX"/>')"><ofbiz:entityfield attribute="cust" field="BATCH_NUM"/></td>
                                    <td class="en11px"><ofbiz:entityfield attribute="cust" field="STOCK_QTY"/></td>
                                </tr>
                            </ofbiz:iterator>
                        </ofbiz:if>  
                    </table>
                </fieldset>
            </td>
        </tr>
    </table>
</form>