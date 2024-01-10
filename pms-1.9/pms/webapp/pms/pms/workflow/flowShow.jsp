<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="java.util.*"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>
<%@ page import="org.ofbiz.entity.GenericDelegator"%>
<%@ page import="com.csmc.pms.webapp.workflow.model.*"%>
<%@ page import="com.csmc.pms.webapp.workflow.model.engine.JobSupport"%>

<% String jobIndex = request.getParameter("jobIndex");
//	GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
	GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");
	JobSupport support = JobSupport.getInstance();
	Job job = null;
	try {
		job = support.parseJob(jobIndex, delegator, false);
	} catch(Exception e) {
		//out.println(e.getMessage);
	}
	if(job != null) { %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>流程查看</title>
<script language='javascript' src='<%=request.getContextPath()%>/function/function.js' type='text/javascript'></script>

<link rel='stylesheet' href='<%=request.getContextPath()%>/images/maincss.css' type='text/css'>
<link rel='stylesheet' href='<%=request.getContextPath()%>/images/tabstyles.css' type='text/css'>
<link href="<%=request.getContextPath()%>/images/default.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath()%>/pms/css/pms.css" rel="stylesheet" type="text/css">

<!--include bubble css-->
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/pms/script/bubble/bt.css" />

<!--include bubble script-->
<script type="text/javascript" src="<%=request.getContextPath()%>/pms/script/bubble/BubbleTooltips.js"></script>
<script language="javascript" type="text/JavaScript">
	window.onload = function(){enableTooltips()};
</script>

<script language="JavaScript" type="text/JavaScript">
	/*function ClickX(){
		window.event.returnValue=false;
	}
	document.oncontextmenu=ClickX;*/
</script>
</head>
<body>
<form method="post" name="flowShowForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>流程显示</legend>
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
</body>
</html>
<% } %>