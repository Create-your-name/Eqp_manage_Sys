<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*"%>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="com.csmc.pms.webapp.workflow.help.*"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>


<%
    String eventType = "";
    String eventName = "";
    String actionName = "";

    GenericValue actionGv = (GenericValue) request.getAttribute("actionGv");
    if (actionGv != null) {
		eventType = actionGv.getString("eventType");
		eventName = actionGv.getString("eventName");
		actionName = actionGv.getString("actionName");
	}
%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>������Ŀ(<%=eventType%>���ͣ�<%=eventName%>����������<%=actionName%>)</legend>
<!--Item��Ϣ-->
<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
    <tr bgcolor="#ACD5C9">
                <td width="5%" class="en11pxb">��������</td>
                <td width="5%" class="en11pxb">���</td>
                <td width="23%" class="en11pxb">��Ŀ����</td>
                <td width="22%" class="en11pxb">˵��</td>
                <td width="10%" class="en11pxb">��ʽ</td>
                <td width="8%" class="en11pxb">��λ</td>
                <td width="8%" class="en11pxb">����</td>
                <td width="8%" class="en11pxb">����</td>
                <td width="10%" class="en11pxb">Ԥ��</td>
        </tr>
        <ofbiz:if name="itemList">
        <ofbiz:iterator name="cust" property="itemList">
            <% GenericValue attrObject = (GenericValue)pageContext.findAttribute("cust");%>
            <tr bgcolor="#DFE1EC">
              <td class="en11px"><%=WorkflowHelper.getEvtText(attrObject.getString("evt"))%></td>
              <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemOrder"/></td>
              <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemName"/></td>
              <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemDescription"/></td>
              <td class="en11px">
                  <%=WorkflowHelper.getItemTypeText(attrObject.getInteger("itemType").intValue())%>
                  <ofbiz:entityfield attribute="cust" field="itemOption"/>
              </td>
              <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemUnit"/></td>
              <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemUpperSpec"/></td>
              <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemLowerSpec"/></td>
              <td class="en11px"><ofbiz:entityfield attribute="cust" field="defaultValue"/></td>
            </tr>
        </ofbiz:iterator>
        </ofbiz:if>
</table>
</fieldset></td>
  </tr>
</table>
<br>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
    <legend>������¼</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
        <tr bgcolor="#ACD5C9">
            <td class="en11pxb">������</td>
            <td class="en11pxb">������</td>
            <td class="en11pxb">����ʱ��</td>
        </tr>
        <ofbiz:if name="wscList">
        <ofbiz:iterator name="cust" property="wscList">
            <tr bgcolor="#DFE1EC">
              <td class="en11px"><ofbiz:entityfield attribute="cust" field="status"/></td>
              <td class="en11px"><ofbiz:entityfield attribute="cust" field="approver"/></td>
              <td class="en11px"><ofbiz:entityfield attribute="cust" field="approveTime"/></td>
            </tr>
        </ofbiz:iterator>
        </ofbiz:if>
    </table>
  </fieldset></td>
</tr>
</table>
<br>

<!--��ť-->
<table border="0" cellspacing="0" cellpadding="0" id="buttonArea">
  <tr height="30">
    <td><ul class="button">
            <li><a class="button-text" href="javascript:javascript:history.back(-1)"><span>&nbsp;����&nbsp;</span></a></li>
    </ul></td>
  </tr>
</table>