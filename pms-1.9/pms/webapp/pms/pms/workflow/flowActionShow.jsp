<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*"%>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="com.csmc.pms.webapp.workflow.help.*"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<%  GenericValue flowAction = (GenericValue)request.getAttribute("flowAction"); 
	pageContext.setAttribute("flowAction", flowAction); 
	String actionType = flowAction.getString("actionType");%>

<!-- yui page script-->
<script language="javascript">
var tabs;
	//��ʼ�������ǩ
	Ext.onReady(function(){
		var itemShowFlag = <%="DCOP".equals(actionType)%>;
		tabs = new Ext.TabPanel('tabs');
        var action = tabs.addTab('action', "����");
        var item = tabs.addTab('item', "������Ŀ");

        tabs.activate('action');
        if(itemShowFlag) tabs.disableTab('item');
	});
</script>

<fieldset><legend>��������</legend>
<!-- ����Tabs -->
<div id="tabs">
	<div id="action" class="tab-content">
		<!--��������-->
		<form name="flowActionShowForm" method="post">
		<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
			<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">������</td>
          		<td width="85%" class="en11px"><ofbiz:inputvalue entityAttr="flowAction" field="actionName" default="" tryEntityAttr="true"/></td>
         	</tr>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">��������</td>
          		<td width="85%" class="en11px"><ofbiz:inputvalue entityAttr="flowAction" field="actionDescription" default="" tryEntityAttr="true"/></td>
         	</tr>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">�Ƿ�DCOP</td>
          		<td width="85%" class="en11px">
          			<%  if("NORMAL".equals(actionType)) {
          					 out.println("��"); 
          				} else {
          					out.println("��");
          				}%></td>
         	</tr>
         	<% String enabled = flowAction.getString("enabled");%>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">�Ƿ�ʹ��</td>
          		<td width="85%" class="en11px"><%  if("0".equals(actionType)) {
          					 out.println("��"); 
          				} else {
          					out.println("��");
          				}%></td>
         	</tr>
         	<% String empty = flowAction.getString("empty");%>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">�Ƿ���ʱ����</td>
          		<td width="85%" class="en11px"><%  if("0".equals(actionType)) {
          					 out.println("��"); 
          				} else {
          					out.println("��");
          				}%></td>
         	</tr>
		</table>  
		<!--����״��-->
		<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30" id="statusArea">
			<tr bgcolor="#ACD5C9">
        		<td width="2%"></td>
          		<td width="98%" class="en11pxb">״��</td>
         	</tr>
         	<ofbiz:if name="statusList" size="0">
         		<ofbiz:iterator name="cust" property="statusList">
	         	<tr bgcolor="#DFE1EC">
	        		<td width="2%" class="en11pxb"><ofbiz:entityfield attribute="cust" field="statusOrder"/></td>
	          		<td width="98%" class="en11px"><ofbiz:entityfield attribute="cust" field="statusName"/></td>
	         	</tr>
         		</ofbiz:iterator>
         	</ofbiz:if>
		</table>
		</form>
    </div>
    <div id="item" class="tab-content">
    	<!--Item��Ϣ-->
		<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
			<tr bgcolor="#ACD5C9">
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
		        	<% GenericValue attrObject = (GenericValue)pageContext.findAttribute("cust"); %>
			        <tr bgcolor="#DFE1EC">
			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemOrder"/></td>
			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemName"/></td>
			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemDescription"/></td>
			          <td class="en11px"><%=WorkflowHelper.getItemTypeText(attrObject.getInteger("itemType").intValue())%></td>
			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemUnit"/></td>
			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemUpperSpec"/></td>
			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemLowerSpec"/></td>
			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="defaultValue"/></td>
			        </tr>
		      	</ofbiz:iterator>
      		</ofbiz:if>
		</table>
    </div>
</div>
</fieldset>
<!--��ť-->
<table border="0" cellspacing="0" cellpadding="0" id="buttonArea">
  <tr height="30">
	<td><ul class="button">
			<li><a class="button-text" href="#" onclick="window.close()"><span>&nbsp;�ر�&nbsp;</span></a></li> 
	</ul></td>
  </tr>
</table>