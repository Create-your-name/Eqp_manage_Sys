<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import ="com.csmc.pms.webapp.util.*"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>
<%@ page import="com.csmc.pms.webapp.workflow.model.*"%>

<%
	List guiPriv = (List)request.getSession().getAttribute(SessionNames.GUI_PRIV_LIST_KEY);
	Map paramMap = (Map)request.getAttribute("paramMap");
	String activate=(String)request.getAttribute("activate");
	GenericValue pmForm=(GenericValue)request.getAttribute("pmForm");
	String equipmentId = (String)pmForm.getString("equipmentId");
	String periodIndex = (String)pmForm.getString("periodIndex");
	GenericValue defaultPeriod = (GenericValue)request.getAttribute("defaultPeriod");
	List eqpStatusList=(List)request.getAttribute("eqpStatusList");
	String formType=pmForm.getString("formType");
	int status=Integer.parseInt(pmForm.getString("status"));
	List jobList = (List)request.getAttribute("jobList");
	Job[] jobsFromJobList = (Job[])request.getAttribute("jobsFromJobList");
	List partsUseList = (List)request.getAttribute("PARTS_USE_LIST");
	String displayStatus="";
	if(Constants.CREAT==status||Constants.START==status||Constants.HOLD==status){
	    displayStatus="δ���";
	}else{
	    displayStatus="���";
	}
	

%>

<form action="" id="pmViewForm" method="POST">


<div id="pmForm" class="tab-content">
<CENTER><p align = "central">�豸ά����¼</p></CENTER>
<p align = "right"><%=(String)paramMap.get("accountDept")%></p>
<table width="100%" border="1" cellspacing="0" cellpadding="0">
	<tr height="25" >
		<td width="20%" class="en11pxb">�豸����</td>
		<td width="30%" class="en11px"><ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/></td>
		<td width="20%" class="en11pxb">ά��ʱ��</td>
		<td width="30%" class="en11px">From��<ofbiz:inputvalue entityAttr="pmForm" field="startTime"/><BR>
	          						   To��<ofbiz:inputvalue entityAttr="pmForm" field="endTime"/></td>	
	</tr>
	
	<tr height="25">
		<td width="20%" class="en11pxb">ά�����</td>
		<td width="30%" class="en11px"><ofbiz:inputvalue entityAttr="defaultPeriod" field="periodName"/></td>
		<td width="20%" class="en11pxb">ά����Ա</td>
		<td width="30%" class="en11px"><ofbiz:inputvalue entityAttr="pmForm" field="createUser"/></td>
	</tr>
</table>
<table width="100%" border="1" cellspacing="0" cellpadding="0">
	<tr height="25">
		<td width="100%" class="en11pxb">һ��ά�������¼��</td>
	</tr>
</table>	
<table width="100%" border="1" cellspacing="0" cellpadding="0">
	<tr height="25">
		<td width="10%" class="en11pxb">���</td>
		<td width="30%" class="en11pxb">ά����Ҫ���輰���ݹ淶</td>
		<td width="10%" class="en11pxb">����</td>
		<td width="10%" class="en11pxb">����</td>
		<td width="20%" class="en11pxb">���ݼ�¼��״̬��¼</td>
		<td width="20%" class="en11pxb">�쳣��������¼</td>
	</tr>
	
	<%  
		for(int i=0;i<jobsFromJobList.length;i++) 
		{
			Job job = jobsFromJobList[i]; 
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
	<%
		} 
	%>
</table>
<table width="100%" border="1" cellspacing="0" cellpadding="0">
	<tr height="25">
		<td width="100%" class="en11pxb">���������׺ļ��ͱ�����¼��</td>
	</tr>
</table>
<table width="100%" border="1" cellspacing="0" cellpadding="0" height="30">
      <tr height="25" >
      	 <td width="20%" class="en11pxb">���</td>
         <td width="30%" class="en11pxb">�ϺŻ򱸼���</td>
         <td width="30%" class="en11pxb">��Ʒ����</td>
         <td width="20%" class="en11pxb">����</td>
       </tr>
       
       <%
       		if (partsUseList != null && partsUseList.size() > 0)
       		{
       			for (int i = 0, size = partsUseList.size();i < size; i++)
       			{
       				Map map = (Map)partsUseList.get(i);
       				if (map != null)
       				{
       %>
       					<tr height="25" >
		         			<td class="en11px"><%=(i+1)%></td>
		         			<td class="en11px"><%=(String)map.get("partNo")%></td>
		         			<td class="en11px"><%=(String)map.get("partName")%></td>
		         			<td class="en11px"><%=(String)map.get("partCount")%></td>
	       				</tr>
       <%					
       				}
       			}
       		} 
       %>
</table>
</div>
</form>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:history.back(-1)"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>

	</tr>
</table>
