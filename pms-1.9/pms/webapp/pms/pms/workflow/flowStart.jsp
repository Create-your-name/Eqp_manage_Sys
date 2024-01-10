<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="java.util.*"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>
<%@ page import="com.csmc.pms.webapp.workflow.model.*"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<% Job job = (Job)request.getAttribute("Job");
   if(job != null) { Action action = job.queryAction(0); %>
<!-- yui page script-->
<script language="javascript">
function doSumbit(url) {
	loading();
	document.flowStartForm.action=url;
	document.flowStartForm.submit();
}
function next(actionId) {
	Ext.getDom('nextActionId').value = actionId;
	doSumbit("<ofbiz:url>/runFlow</ofbiz:url>");
}
</script>

<form method="post" name="flowStartForm">
<input type="hidden" name="jobRelationIndex" value="<%=request.getParameter("jobRelationIndex")%>" />
<input type="hidden" name="currentActionId" value="<%=action.getActionId()%>" />
<input type="hidden" name="nextActionId" value="" />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>流程启动</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td width="20%" class="en11pxb">流程名</td>
      		<td width="80%" bgcolor="#DFE1EC" class="en11pxb"><%=job.getJobName()%></td>
    	</tr>
    	<tr bgcolor="#ACD5C9">
      		<td width="20%" class="en11pxb">流程描述</td>
      		<td width="80%" bgcolor="#DFE1EC" class="en11pxb"><%=UtilFormatOut.checkNull(job.getJobDescription())%></td>
    	</tr>
      </table>
      </fieldset></td>
  </tr>
</table>
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>流程图</legend>
      <table cellpadding="3" cellspacing="0" border="0" width="100%">
      	<tr bgcolor="#DFE1EC">
        	<td width="70%" style="border: 1px solid #bbbbb0; background: #fffff0;">
            <table cellpadding="3" cellspacing="0" border="0" width="100%">
		        <tr>
		            <td width="33%">
		                <table cellpadding="3" cellspacing="3">
		                	<% List actionList = job.getActionlist();
		                		if(CommonUtil.isNotEmpty(actionList)) {
		                			for(Iterator it = actionList.iterator(); it.hasNext(); ) {
		                			Action stepAction = (Action)it.next();
		                			String nodeType = stepAction.getNodeType();
		                			boolean isAction = false;
									if("action".equals(nodeType)) isAction = true; %>
		                	<tr>
		                	    <td align="center" class="en11px" style="border: 1px solid #bbb;" bgcolor="<%=isAction?"#ffffff":"#ACD5C9"%>" nowrap>
		                	    	<%=stepAction.getActionName()%>
		                	    </td>
		                	</tr>
		                	<% if(!"end".equals(stepAction.getNodeType())) { %>
		                	<tr>
		                		<td align="center" nowrap><img src="<%=request.getContextPath()%>/pms/images/arrow_down_blue.gif" height=16 width=16 border=0 align=absmiddle></td>
		                	</tr>
		                	<% }
		                	  }
		                	} %>

		                	<% List actionStatuList = action.getStatusList();
		                		if(CommonUtil.isNotEmpty(actionStatuList)) {
		                		for(Iterator it = actionStatuList.iterator(); it.hasNext(); ) {
		                			ActionStatus status = (ActionStatus) it.next(); %>
		                	<tr>
					      		<td><table border="0" cellspacing="0" cellpadding="0">
					              <tr height="30">
					                <td><ul class="button">
					                        <li><a class="button-text" href="javascript:next('<%=status.getNextActionId()%>');"><span>&nbsp;<%=status.getStatusName()%>&nbsp;</span></a></li>
					                </ul></td>
					              </tr>
					            </table></td>
					    	</tr>
					    	<% }
					    	} %>
		                </table>
		            </td>
		        </tr>
		    </table>
            </td>
      	</tr>
      </table>
      </fieldset></td>
  </tr>
</table>
</form>
<% } %>