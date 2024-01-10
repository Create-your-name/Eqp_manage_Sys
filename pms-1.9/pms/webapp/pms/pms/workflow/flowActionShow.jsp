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
	//初始化界面标签
	Ext.onReady(function(){
		var itemShowFlag = <%="DCOP".equals(actionType)%>;
		tabs = new Ext.TabPanel('tabs');
        var action = tabs.addTab('action', "动作");
        var item = tabs.addTab('item', "动作项目");

        tabs.activate('action');
        if(itemShowFlag) tabs.disableTab('item');
	});
</script>

<fieldset><legend>动作设置</legend>
<!-- 动作Tabs -->
<div id="tabs">
	<div id="action" class="tab-content">
		<!--动作设置-->
		<form name="flowActionShowForm" method="post">
		<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
			<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">动作名</td>
          		<td width="85%" class="en11px"><ofbiz:inputvalue entityAttr="flowAction" field="actionName" default="" tryEntityAttr="true"/></td>
         	</tr>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">动作描述</td>
          		<td width="85%" class="en11px"><ofbiz:inputvalue entityAttr="flowAction" field="actionDescription" default="" tryEntityAttr="true"/></td>
         	</tr>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">是否DCOP</td>
          		<td width="85%" class="en11px">
          			<%  if("NORMAL".equals(actionType)) {
          					 out.println("否"); 
          				} else {
          					out.println("是");
          				}%></td>
         	</tr>
         	<% String enabled = flowAction.getString("enabled");%>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">是否使用</td>
          		<td width="85%" class="en11px"><%  if("0".equals(actionType)) {
          					 out.println("否"); 
          				} else {
          					out.println("是");
          				}%></td>
         	</tr>
         	<% String empty = flowAction.getString("empty");%>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">是否延时输入</td>
          		<td width="85%" class="en11px"><%  if("0".equals(actionType)) {
          					 out.println("否"); 
          				} else {
          					out.println("是");
          				}%></td>
         	</tr>
		</table>  
		<!--动作状况-->
		<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30" id="statusArea">
			<tr bgcolor="#ACD5C9">
        		<td width="2%"></td>
          		<td width="98%" class="en11pxb">状况</td>
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
    	<!--Item信息-->
		<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
			<tr bgcolor="#ACD5C9">
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
<!--按钮-->
<table border="0" cellspacing="0" cellpadding="0" id="buttonArea">
  <tr height="30">
	<td><ul class="button">
			<li><a class="button-text" href="#" onclick="window.close()"><span>&nbsp;关闭&nbsp;</span></a></li> 
	</ul></td>
  </tr>
</table>