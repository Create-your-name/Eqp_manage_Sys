<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.workflow.model.*"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>


<%
    String pcName = (String) request.getAttribute("pcName");
    String createTime = (String) request.getAttribute("createTime");
    String createUser = (String) request.getAttribute("createUser");
    String note = UtilFormatOut.checkNull((String) request.getAttribute("note"));
    String jobText = UtilFormatOut.checkNull((String) request.getAttribute("jobText"));
    String styleName = (String) request.getAttribute("styleName");
    String periodName = (String) request.getAttribute("periodName");
    String pcIndex = (String) request.getAttribute("pcIndex");
    String jobRelationIndex = (String) request.getAttribute("jobRelationIndex");
    Job job = (Job)request.getAttribute("singleJob");
 %>

<CENTER><p align = "central">巡检表单</p></CENTER>
<table width="100%" border="1" cellspacing="0" cellpadding="0">
	<tr height="25">
		<td width="20%" class="en11pxb">巡检样式</td>
		<td width="30%" class="en11px"><%=styleName%></td>
		<td width="20%" class="en11pxb">巡检周期</td>
		<td width="30%" class="en11px"><%=periodName%></td>	
	</tr>
	
	<tr height="25">
		<td width="20%" class="en11pxb">巡检时间</td>
		<td width="30%" class="en11px"><%=createTime%></td>
		<td width="20%" class="en11pxb">建表人</td>
		<td width="30%" class="en11px"><%=createUser%></td>
	</tr>
	
	<tr height="25">
		<td width="20%" class="en11pxb">表单编号</td>
		<td width="30%" class="en11px"><%=pcName%></td>
		<td border = "1" width="20%" class="en11pxb">备注</td>
		<td width="30%" class="en11px"><%=note%></td>
	</tr>
</table>
<table width="100%" border="1" cellspacing="0" cellpadding="0">
	<tr height="25">
		<td width="100%" class="en11pxb">处理过程：</td>
	</tr>
</table>	
<table width="100%" border="1" cellspacing="0" cellpadding="0">
	<tr height="25">
		<td width="10%" class="en11pxb">序号</td>
		<td width="30%" class="en11pxb">维护简要步骤及数据规范</td>
		<td width="10%" class="en11pxb">上限</td>
		<td width="10%" class="en11pxb">下限</td>
		<td width="20%" class="en11pxb">数据记录和状态记录</td>
		<td width="20%" class="en11pxb">异常情况处理记录</td>
	</tr>
	
	<%  
			if(CommonUtil.isNotNull(job) && CommonUtil.isNotEmpty(job.getActionlist())) 
			{
	   			Iterator it = job.getActionlist().iterator(); 
	   			while(it.hasNext()) 
	   			{ 
		    		Action action = (Action)it.next(); 
	%>
			    	<tr height="25">
			    		<td class="en11px"><%=action.getActionId()%></td>
					  	<td class="en11px"><%=action.getActionName()%><%if(Constants.ACTION_DCOP_TYPE.equals(action.getActionType())) { %><font color="#ff0000"> DCOP</font><% } %></td>
					  	<td class="en11px"></td>
					  	<td class="en11px"></td>
					  	<td class="en11px"></td>
					  	<td class="en11px"><%if("action".equals(action.getNodeType())) { %><%=UtilFormatOut.checkNull(action.getActionNote())%><% } %></td>
				  	</tr>
		  			<% 
		  				if(CommonUtil.isNotEmpty(action.getItemlist())) 
		  			  	{
		  			%>
					 		<% 
					 			for(Iterator itemIt = action.getItemlist().iterator(); itemIt.hasNext(); ) 
					 		  	{ 
					              	ActionItem item = (ActionItem) itemIt.next(); 
					        %>
					              	<tr height="25"<% if(item.isOOS()) { %> bgcolor="#ff0000"<% } else { %> bgcolor="#DFE1EC"<% } %>>
						                <td class="en11px"></td>
						                <td class="en11px"><%=item.getItemName()%></td>
						                <td class="en11px"><% if(item.getItemType()==Constants.NUMBER_ITEM) { out.println(CommonUtil.checkDoubleNull(item.getItemUpperSpec()));}%></td>
						                <td class="en11px"><% if(item.getItemType()==Constants.NUMBER_ITEM) { out.println(CommonUtil.checkDoubleNull(item.getItemLowerSpec()));}%></td>
						                <td class="en11px"><%=UtilFormatOut.checkNull(item.getItemValue())%></td>
						                <td class="en11px"><%=UtilFormatOut.checkNull(item.getItemNode())%></td>
					               </tr>
					        <% } %>
					<%
						}
					%>
			<%
				}
			%>
		<%
			} 
		%>

</table>

<%if (jobRelationIndex != null) {%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
	<td colspan="2" class="en11px"><ul class="button">
	     <li><a class="button-text" href="javascript:javascript:history.back(-1)"><span>&nbsp;返回&nbsp;</span></a></li> 
	</ul></td>
  </tr>
</table>
<%}%>