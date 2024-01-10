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
      <legend>动作项目(<%=eventType%>类型：<%=eventName%>，动作名：<%=actionName%>)</legend>
<!--Item信息-->
<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
    <tr bgcolor="#ACD5C9">
                <td width="5%" class="en11pxb">操作类型</td>
                <td width="5%" class="en11pxb">序号</td>
                <td width="23%" class="en11pxb">项目名称</td>
                <td width="22%" class="en11pxb">说明</td>
                <td width="10%" class="en11pxb">样式</td>
                <td width="8%" class="en11pxb">单位</td>
                <td width="8%" class="en11pxb">上限</td>
                <td width="8%" class="en11pxb">下限</td>
                <td width="10%" class="en11pxb">预设</td>
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
    <legend>审批记录</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
        <tr bgcolor="#ACD5C9">
            <td class="en11pxb">处理步骤</td>
            <td class="en11pxb">处理者</td>
            <td class="en11pxb">处理时间</td>
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

<!--按钮-->
<table border="0" cellspacing="0" cellpadding="0" id="buttonArea">
  <tr height="30">
    <td><ul class="button">
            <li><a class="button-text" href="javascript:javascript:history.back(-1)"><span>&nbsp;返回&nbsp;</span></a></li>
    </ul></td>
  </tr>
</table>