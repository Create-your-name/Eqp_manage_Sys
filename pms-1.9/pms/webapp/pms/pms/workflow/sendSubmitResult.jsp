<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="java.util.*"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>
<%@ page import="com.csmc.pms.webapp.workflow.model.*"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<% Job job = (Job)request.getAttribute("Job"); 
	if(CommonUtil.isNotNull(job)) { %>
   
<!-- yui page script-->
<script language="javascript">
	
</script>

<form method="post" name="flowDefineForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td width="20%" class="en11pxb">������</td>
      		<td width="80%" bgcolor="#DFE1EC" class="en11px"><%=job.getJobName()%></td>
    	</tr>
    	<tr bgcolor="#ACD5C9">
      		<td width="20%" class="en11pxb">��������</td>
      		<td width="80%" bgcolor="#DFE1EC" class="en11px"><%=job.getJobDescription()%></td>
    	</tr>
      </table>
      </fieldset></td>
  </tr>
</table>
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�����б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
		 	<td width="15%" class="en11pxb">��Ŀ����</td>
		 	<td width="25%" class="en11pxb">��Ŀ˵��</td>
		 	<td width="30%" class="en11pxb">��Ŀֵ</td>
		 	<td width="30%" class="en11pxb">��Ŀ��ע</td>
    	</tr>
    	<tr bgcolor="#F0C000">
		  	<td colspan="4" class="en11px">��ʼ</td>
	  	 </tr>
<%  if(CommonUtil.isNotEmpty(job.getActionlist())) {
   		Iterator it = job.getActionlist().iterator(); 
   		while(it.hasNext()) { 
	    	Action action = (Action)it.next(); %>
    	<tr bgcolor="#F0C000">
		  	<td colspan="4" class="en11px"><%=action.getActionName()%></td>
	  	  </tr>
		<%	if(CommonUtil.isNotEmpty(action.getItemlist())) {
			for(Iterator ite = action.getItemlist().iterator(); ite.hasNext(); ) { 
				ActionItem item = (ActionItem)ite.next(); %>
		<tr bgcolor="#DFE1EC">
			<td class="en11px"><%=item.getItemName()%></td>
		  	<td class="en11px"><%=item.getItemDescription()%></td>
		  	<td class="en11px"><%=item.getItemValue()%></td>
		  	<td class="en11px"></td>
		</tr>
<% 			}
			}
		} 
	} %>
		<tr bgcolor="#F0C000">
		  	<td colspan="4" class="en11px">����</td>
	  	 </tr>
      </table>
      </fieldset></td>
  </tr>
</table>  
</form>
<% } %>