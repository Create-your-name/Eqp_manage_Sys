<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="java.util.*"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>
<%@ page import="com.csmc.pms.webapp.workflow.model.*"%>
<%@ page import="com.csmc.pms.webapp.workflow.help.*"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
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
      		<td width="80%" bgcolor="#DFE1EC" class="en11px"><%=UtilFormatOut.checkNull(job.getJobDescription())%></td>
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
      		<td width="5%" class="en11pxb">ID</td>
		 	<td width="34%" class="en11pxb">����˵��</td>
		 	<td width="61%" class="en11pxb">��ע</td>
    	</tr>
<%  if(CommonUtil.isNotEmpty(job.getActionlist())) {
   		Iterator it = job.getActionlist().iterator();
   		while(it.hasNext()) {
	    	Action action = (Action)it.next(); %>
    	<tr bgcolor="#F0C000">
    		<td class="en11px"><%=action.getActionId()%></td>
		  	<td class="en11px"><%=action.getActionName()%><%if(Constants.ACTION_DCOP_TYPE.equals(action.getActionType())) { %><font color="#ff0000"> DCOP</font><% } %></td>
		  	<td class="en11px"><%if("action".equals(action.getNodeType())) { %><%=UtilFormatOut.checkNull(action.getActionNote())%><% } %></td>
	  	</tr>
	  	<% if(CommonUtil.isNotEmpty(action.getItemlist())) { %>
	  	<tr>
	  		<td bgcolor="#ACD5C9"></td>
	  		<td colspan="2"><table width="100%" border="0" cellpadding="1" cellspacing="1">
              <tr bgcolor="#ACD5C9">
                <td class="en11pxb" width="15%">��Ŀ����</td>
                <td class="en11pxb" width="24%">��Ŀֵ</td>
                <td class="en11pxb" width="8%">����</td>
                <td class="en11pxb" width="8%">����</td>
                <td class="en11pxb" width="5%">��λ</td>
                <td class="en11pxb" width="40%">��ע</td>
              </tr>
              <% for(Iterator itemIt = action.getItemlist().iterator(); itemIt.hasNext(); ) {
              		ActionItem item = (ActionItem) itemIt.next(); %>
              <tr<% if(item.isOOS()) { %> bgcolor="#ff0000"<% } else { %> bgcolor="#DFE1EC"<% } %>>
                <td class="en11px"><%=item.getItemName()%></td>
                <td class="en11px"><%=UtilFormatOut.checkNull(item.getItemValue())%></td>
                <td class="en11px"><% if(item.getItemType()==Constants.NUMBER_ITEM) { out.println(CommonUtil.checkDoubleNull(item.getItemUpperSpec()));}%></td>
                <td class="en11px"><% if(item.getItemType()==Constants.NUMBER_ITEM) { out.println(CommonUtil.checkDoubleNull(item.getItemLowerSpec()));}%></td>
                <td class="en11px"><%=UtilFormatOut.checkNull(item.getItemUnit())%></td>
                <td class="en11px"><%=UtilFormatOut.checkNull(item.getItemNode())%></td>
              </tr>
              <% } %>
            </table></td>
	  	</tr>
<% 		}
		}
	} %>
      </table>
      </fieldset></td>
  </tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:window.close()"><span>&nbsp;�ر�&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>
<!--<table width="100%" border="0" cellspacing="0" cellpadding="0">
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
		  	<td colspan="4" class="en11px"><%=action.getActionName()%><font color="red"><%=(CommonUtil.isNotEmpty(action.getActionNote())?"("+action.getActionNote()+")":"")%></font></td>
	  	  </tr>
		<%	if(CommonUtil.isNotEmpty(action.getItemlist())) {
			for(Iterator ite = action.getItemlist().iterator(); ite.hasNext(); ) {
				ActionItem item = (ActionItem)ite.next(); %>
		<tr bgcolor="#DFE1EC">
			<td class="en11px"><%=item.getItemName()%></td>
		  	<td class="en11px"><%=item.getItemDescription()%></td>
		  	<td class="en11px"><%=item.getItemValue()%></td>
		  	<td class="en11px"><%=UtilFormatOut.checkNull(item.getItemNode())%></td>
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
</table>  -->
</form>
<% } %>