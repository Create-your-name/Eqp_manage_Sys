<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="java.util.*"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>
<%@ page import="org.ofbiz.entity.GenericDelegator"%>
<%@ page import="org.ofbiz.base.util.UtilMisc"%>
<%@ page import="com.csmc.pms.webapp.workflow.model.*"%>
<%@ page import="com.csmc.pms.webapp.workflow.help.WorkflowHelper"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<%  //Job job = (Job)request.getAttribute("Job");
	Job job = WorkflowHelper.getJobDefineFromCache(request);
	String eventType = request.getParameter("eventType");
	String eventName = request.getParameter("eventName");
	GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
	List actionList = delegator.findByAnd("FlowAction", UtilMisc.toMap("eventType", eventType, "eventName", eventName),UtilMisc.toList("actionName")); 
	
	String isMsaDept=(String)request.getAttribute("isMsaDept");
%>
<!-- yui page script-->
<script language="javascript">
var actionCount = <%=job.getActionlist().size()%>
function addAction(actionIndex) {
	Ext.getDom("actionIndex").value = actionIndex;
	doSubmit("<ofbiz:url>/addFlowAction</ofbiz:url>");
}
function saveFlow() {
	var msg = checkFlow();
	if(msg.length>0) {
		Ext.MessageBox.alert('警告', msg);
		return;
	}
	doSubmit('<ofbiz:url>/saveFlow</ofbiz:url>');
}

function checkFlow() {
	if(Trim(Ext.getDom('jobName').value).length==0) {
		return "流程名不能为空！";
	}
	var regex = /^MSA.*$/;
    var isMsaDept = <%=isMsaDept%>;
    if(isMsaDept==true && !regex.test(Ext.getDom('jobName').value)){
        return "流程名必须以MSA开头命名";
    }
	if(Trim(Ext.getDom('jobDescription').value).length==0) {
		return "流程描述不能为空！";
	}
	//TODO ActionList不能为空
	if(actionCount == 2) {
		return "流程动作还未设置！";
	}
	return "";
}

function doSubmit(url) {
	loading();
	document.flowDefineForm.action=url;
	document.flowDefineForm.submit();
}

function delteAction(actionId) {
	Ext.MessageBox.confirm('确认', '确定要删除该动作？',function result(value){
		if(value=="yes"){
			doSubmit('<ofbiz:url>/removeFlowAction</ofbiz:url>?actionId='+actionId);
		} else {
			return;
		}
	});
}

function deleteTempJob() {
	Ext.MessageBox.confirm('确认', '确定要删除该流程副本？',function result(value){
		if(value=="yes"){
			doSubmit('<ofbiz:url>/deleteFlowJobTemp</ofbiz:url>');
		} else {
			return;
		}
	});
}

function resort() {
	doSubmit('<ofbiz:url>/resortFlowAction</ofbiz:url>?actionId='+actionId);
}

function stepResort(step) {
	doSubmit('<ofbiz:url>/stepResortFlowAction</ofbiz:url>?step='+step);
}

//送签
function sendSubmit(submitType){

    var msg = checkFlow();
	if(msg.length>0) {
		Ext.MessageBox.alert('警告', msg);
		return;
	}

	Ext.getDom('submitType').value = submitType;
	Ext.MessageBox.confirm('送签确认', '你确定要送签该信息？',function result(value){
		if(value=="yes"){
			var url='<ofbiz:url>/sendFlowSubmit</ofbiz:url>';
			doSubmit(url);
		}else{
			return;
		}
    });
}

function showAction(actionIndex) {
	var urlString = "<%=request.getContextPath()%>/control/showFlowAction?actionIndex="+actionIndex;
	var result=window.showModalDialog (urlString,'',"dialogWidth=650px;dialogHeight=350px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");
	/*window.open(urlString,"job",
                "top=130,left=240,width=600,height=400,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=no");*/
}

function cancel() {
	doSubmit('<ofbiz:url>/queryFlowList</ofbiz:url>');
}
</script>

<form method="post" name="flowDefineForm">
<input name="eventType" type="hidden" value="<%=request.getParameter("eventType")%>" />
<input name="eventObject" type="hidden" value="<%=request.getParameter("eventObject")%>" />
<input name="eventName" type="hidden" value="<%=request.getParameter("eventName")%>" />

<input name="jobIndex" type="hidden" value="<%=job.getJobIndex()%>" />
<input name="tempIndex" type="hidden" value="<%=job.getTempIndex()%>" />
<input name="actionIndex" type="hidden" value="" />

<input name="submitObject" type="hidden" value="<%=Constants.SUBMIT_FLOW%>" />
<input name="submitObjectIndex" type="hidden" value="<%=job.getTempIndex()%>" />
<input name="submitType" type="hidden" value="" />
<input name="submitObjectName" type="hidden" value="<%=UtilFormatOut.checkNull(job.getJobName())%>" />

<input name="equipmentType" type="hidden" value="<%=request.getParameter("equipmentType")%>" />
<input name="pmPeriod" type="hidden" value="<%=request.getParameter("pmPeriod")%>" />
<input name="pcPeriod" type="hidden" value="<%=request.getParameter("pcPeriod")%>" />
<input name="styleIndex" type="hidden" value="<%=request.getParameter("styleIndex")%>" />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>处理程序设定</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
         <tr bgcolor="#DFE1EC">
          <td width="15%" class="en11pxb">程序名称</td>
          <td width="85%"><input type="text" name="jobName" value="<%=UtilFormatOut.checkNull(job.getJobName())%>" class="input" /></td>
         </tr>
         <tr bgcolor="#DFE1EC">
         	<td class="en11pxb">程序说明</td>
       	  	<td><input type="text" name="jobDescription" value="<%=UtilFormatOut.checkNull(job.getJobDescription())%>" class="input" /></td>
         </tr>
      </table>
      </fieldset></td>
  </tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
  	<td><ul class="button">
			<li><a class="button-text" href="javascript:saveFlow();"><span>&nbsp;储存程序&nbsp;</span></a></li>
	</ul></td>
	<td><ul class="button">
			<li><a class="button-text" href="javascript:cancel();"><span>&nbsp;取消&nbsp;</span></a></li>
	</ul></td>
	<% if(job.isNewFlag()) { %>
	<td><ul class="button">
			<li><a class="button-text" href="javascript:sendSubmit('<%=Constants.SUBMIT_INSERT%>');"><span>&nbsp;发送新增申请&nbsp;</span></a></li>
	</ul></td>
	<% } else if(job.getTempIndex() == 0){ %>
	<td><ul class="button">
			<li><a class="button-text" href="javascript:sendSubmit('<%=Constants.SUBMIT_MODIFY%>');"><span>&nbsp;发送修改申请&nbsp;</span></a></li>
	</ul></td>
	<td><ul class="button">
			<li><a class="button-text" href="javascript:sendSubmit('<%=Constants.SUBMIT_DELETE%>');"><span>&nbsp;发送删除申请&nbsp;</span></a></li>
	</ul></td>
	<% } %>
	<%if(job.getTempIndex() != 0) { %>
	<% if(!job.isNewFlag()) { %>
	<td><ul class="button">
			<li><a class="button-text" href="javascript:sendSubmit('<%=Constants.SUBMIT_MODIFY%>');"><span>&nbsp;发送修改申请&nbsp;</span></a></li>
	</ul></td>
	<% }%>
	<td><ul class="button">
			<li><a class="button-text" href="javascript:deleteTempJob();"><span>&nbsp;删除此流程副本&nbsp;</span></a></li>
	</ul></td>
	<% } %>
  </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>动作列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<% if(CommonUtil.isNotEmpty(actionList)) {
      		Iterator actionIt = actionList.iterator();
      		int i = 0;
      		while(actionIt.hasNext()) {
      		i++;
      		GenericValue action = (GenericValue)actionIt.next();
      		if(i == 1) { %>
      	<tr bgcolor="#DFE1EC">
      		<% } %>
      		<td class="en11px"><a class="button-text" href="javascript:showAction('<%=action.getLong("actionIndex")%>')"><%=action.getString("actionName")%></td>
      		<td class="en11pxb"><a href="javascript:addAction('<%=action.getLong("actionIndex")%>')"><img src="<%=request.getContextPath()%>/pms/images/arrow_down_blue.gif" height=16 width=16 border=0 align=absmiddle></td>
    		<% if(i == 2) {
    			i = 0; %>
    	</tr>
    	<% } %>
    	<%	}
    	} %>
      </table>
      </fieldset></td>
  </tr>
</table>
<br/>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>流程列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td class="en11pxb"></td>
      		<td class="en11pxb">动作序号</td>
      		<td class="en11pxb">动作说明</td>
      		<td class="en11pxb">动作状况</td>
      		<td class="en11pxb">下一步序号</td>
    	</tr>
		<% if(CommonUtil.isNotEmpty(job.getActionlist())) {
			int actionNum = 0;
			for(Iterator it = job.getActionlist().iterator(); it.hasNext(); ) {
			boolean delflag = false;
			Action action = (Action) it.next();
			if("action".equals(action.getNodeType())) delflag = true;
			actionNum++; %>
		<tr bgcolor="#DFE1EC">
			<td class="en11pxb"><%if(delflag) { %><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delteAction(<%=action.getActionId()%>)"/><% } %></td>
			<td class="en11px"><input type="text" size="1" class="input" name="<%=(action.getActionId()+","+action.getActionIndex())%>" value="<%=action.getActionId()%>" />
				<%if(delflag) { %><%if(actionNum > 2) { %><img src="<%=request.getContextPath()%>/pms/images/arrow_first.gif" border="0" align="absmiddle" style="cursor:hand" onclick="stepResort('<%=action.getActionId()%>,<%=action.getActionId()-1%>')" /><% } %>
				<% if(actionNum != job.getActionlist().size()-1) { %><img src="<%=request.getContextPath()%>/pms/images/arrow_last.gif" style="cursor:hand" border="0" align="absmiddle" onclick="stepResort('<%=action.getActionId()%>,<%=action.getActionId()+1%>')" /><% } %><% } %></td>
			<td class="en11px"><%=action.getActionName()%></td>
			<% if(CommonUtil.isNotEmpty(action.getStatusList())) { %>
			<td class="en11px"><table width="100%" border="0" cellpadding="0" cellspacing="0">
			<% for(Iterator statusIt = action.getStatusList().iterator(); statusIt.hasNext(); ) {
				ActionStatus status = (ActionStatus)statusIt.next();%>
              <tr>
                <td class="en11px"><%=status.getStatusName()%></td>
              </tr>
            <% } %>
            </table></td>
            <% if("action".equals(action.getNodeType())) { %>
			<td class="en11px"><table width="100%" border="0" cellpadding="0" cellspacing="0">
				<% for(Iterator statusIt = action.getStatusList().iterator(); statusIt.hasNext(); ) {
					ActionStatus status = (ActionStatus)statusIt.next(); %>
				<tr>
                <td class="en11px"><select name="<%=(action.getActionId()+"_"+status.getStatusIndex())%>">
                	<%for(int i = 1; i < job.getActionlist().size(); i++) {
                		String nextActionValue = "";
	                	//if(i == 0) { nextActionValue = "START"; }
	                	//else
	                	if(i == job.getActionlist().size()-1) { nextActionValue = "END"; }
	            		else { nextActionValue = String.valueOf(i); } %>
                		<option value="<%=i%>"<%if(i==status.getNextActionId()) { %> selected<% } %>><%=nextActionValue%></option><% } %>
                	</select></td>
            	</tr><% } %></table>
			<% } else if("start".equals(action.getNodeType())) { %>
				<td class="en11px">1</td>
			<% } %>
			<% } else { %>
				<td class="en11px"></td>
				<td class="en11px"></td>
			<% } %>
		</tr>
		<%   }
			} %>
      </table>
      </fieldset></td>
  </tr>
</table>
</form>