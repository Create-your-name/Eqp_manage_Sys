<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="java.util.*"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.workflow.model.*"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<%
Job job = (Job)request.getAttribute("Job");
if(job.getRunStatus() == Constants.JOB_PROCESS) {

   String actionIdString = request.getParameter("nextActionId");
   if(request.getAttribute("nextActionId") != null) {
   		actionIdString = (String) request.getAttribute("nextActionId");
   }
   int actionId;
   if(actionIdString == null) {
      actionId = job.getNextActionId();
   } else {
   	  actionId = Integer.parseInt(actionIdString);
   }
   Action action = job.queryAction(actionId);
%>


<!-- yui page script-->
<script language="javascript">
function selectActionStatus(actionId) {
	var itemlist = Ext.DomQuery.select('*[name^=item_]');
	Ext.each(itemlist, function(item) {
	//	alert(item.name+"#"+item.value);
		if(Ext.getDom('empty').value==1 && item.value=="") {
			Ext.getDom('isEmpty').value = "1";
		}
	});
	Ext.getDom('nextActionId').value = actionId;
	//alert(Ext.getDom('isEmpty').value);
	doSumbit("<ofbiz:url>/runFlow</ofbiz:url>");
}
function checkItem() {
	<% if(CommonUtil.isNotEmpty(action.getItemlist())) {
			for(Iterator itemCheckIt = action.getItemlist().iterator(); itemCheckIt.hasNext(); ) {
				ActionItem checkItem = (ActionItem)itemCheckIt.next();
				long itemIndex = checkItem.getItemIndex();
				String key = "item_" + itemIndex;
				String noteKey = "note_" + itemIndex;
				out.println("if(Ext.getDom('empty').value*1 == 0) { \n");
				out.println("if(Ext.getDom('" + key + "').value.length == 0 && Ext.getDom('" + noteKey + "').value.length == 0) { \n");
				out.println("	alert('项目值未填，请填写备注'); Ext.getDom('" + key + "').select(); return;\n");
				out.println(" }　\n");
				if(Constants.NUMBER_ITEM == checkItem.getItemType()) {
					Double upperSpec = checkItem.getItemUpperSpec();
					Double lowerSpec = checkItem.getItemLowerSpec();
					out.println("if(!IsNumeric(Ext.getDom('" + key + "').value) && !(''==Ext.getDom('" + key + "').value)) { \n");
					out.println("	alert('所填非数值类型');Ext.getDom('" + key + "').select(); return;\n");
					out.println(" }　\n");
					if(upperSpec == null && lowerSpec != null) {
						out.println("if(Ext.getDom('" + key + "').value*1 < " + lowerSpec.doubleValue() + "*1 && Ext.getDom('" + noteKey + "').value.length == 0) { \n");
						out.println("	alert('所填数值超规范，请填写备注'); Ext.getDom('" + key + "').select(); return;\n");
						out.println(" }　\n");
					} else if(upperSpec != null && lowerSpec == null) {
						out.println("if(Ext.getDom('" + key + "').value*1 > " + upperSpec.doubleValue() + "*1 && Ext.getDom('" + noteKey + "').value.length == 0) { \n");
						out.println("	alert('所填数值超规范，请填写备注'); Ext.getDom('" + key + "').select(); return;\n");
						out.println(" }　\n");
					} else if(upperSpec != null && lowerSpec != null) {
						out.println("if((Ext.getDom('" + key + "').value*1 > " + upperSpec + "*1 || Ext.getDom('" + key + "').value*1 < " + lowerSpec + "*1) && Ext.getDom('" + noteKey + "').value.length == 0) { \n");
						out.println("	alert('所填数值超规范，请填写备注'); Ext.getDom('" + key + "').select(); return;\n");
						out.println(" }　\n");
					}
				}
				out.println(" }　\n");
				out.println("Ext.getDom('" + key + "').readOnly=true;");
				out.println("Ext.getDom('" + noteKey + "').readOnly=true;");
			}
		} %>
	displayButton(true);
}
function doSumbit(url) {
	loading();
	document.flowProcessForm.action=url;
	document.flowProcessForm.submit();
}
function displayButton(display) {
	var tdlist = Ext.DomQuery.select('td',Ext.getDom('statusButtonArea'));
	Ext.each(tdlist, function(td) {
		if(display) {
			Ext.get(td).show();
		} else {
			Ext.get(td).hide();
		}
	});
}
function reset() {
	displayButton(false);
	document.flowProcessForm.reset();
	<% if(CommonUtil.isNotEmpty(action.getItemlist())) {
			for(Iterator itemCheckIt = action.getItemlist().iterator(); itemCheckIt.hasNext(); ) {
				ActionItem checkItem = (ActionItem)itemCheckIt.next();
				long itemIndex = checkItem.getItemIndex();
				String key = "item_" + itemIndex;
				String noteKey = "note_" + itemIndex;
				out.println("Ext.getDom('" + key + "').readOnly=false;");
				out.println("Ext.getDom('" + noteKey + "').readOnly=false;");
			}
		} %>
}
function windowClose() {
	window.close();
}
Ext.onReady(function(){
	displayButton(false);
});
</script>

<form method="post" name="flowProcessForm">
<input type="hidden" name="jobRelationIndex" value="<%=request.getParameter("jobRelationIndex")%>" />
<input type="hidden" name="actionIndex" value="<%=action.getActionIndex()%>">
<input type="hidden" name="currentActionId" value="<%=action.getActionId()%>">
<input type="hidden" name="nextActionId">
<input type="hidden" name="empty" value="<%=action.getEmpty()%>">
<input type="hidden" name="isEmpty" value="0">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>动作</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td width="25%" class="en11pxb">动作名</td>
      		<td width="75%" class="en11pxb">动作描述</td>
    	</tr>
		<tr bgcolor="#DFE1EC">
			<td class="en11px"><%=action.getActionName()%></td>
			<td class="en11px"><%=UtilFormatOut.checkNull(action.getActionDescription())%></td>
		</tr>
		<tr bgcolor="#ACD5C9">
      		<td width="25%" class="en11pxb">备注</td>
      		<td width="75%" class="en11pxb" bgcolor="#DFE1EC"><input type="text" class="input" name="comment" size="40"/></td>
    	</tr>
      </table>
      </fieldset></td>
  </tr>
</table>
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>项目列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td width="25%" class="en11pxb">项目名称</td>
      		<td width="45%" class="en11pxb">输入值</td>
      		<td width="30%" class="en11pxb">备注</td>
    	</tr>
    	<% List itemList = action.getItemlist();
    		if(CommonUtil.isNotEmpty(itemList)) {
    		for(Iterator it = action.getItemlist().iterator(); it.hasNext(); ) {
    		ActionItem item = (ActionItem)it.next(); %>
		<tr bgcolor="#DFE1EC">
			<td class="en11px"><%=item.getItemName()%>(<%=UtilFormatOut.checkNull(item.getItemDescription())%>)</td>
			<td class="en11pxb"><% if(item.getItemType() == Constants.OPTION_ITEM) {
					String[] optionValues = item.getItemOption().split("\\|");
					String defaultValue = item.getDefaultValue(); %>
				<select name="item_<%=item.getItemIndex()%>">
					<% for(int optionIndex = 0; optionIndex < optionValues.length; optionIndex++) {
						String optionValue = optionValues[optionIndex]; %>
					<option value="<%=optionValue%>"<%if(optionValue.equals(defaultValue)) { %> selected<% } %>><%=optionValue%></option>
					<% } %>
				</select>
				<% } else { %>
				<input type="text" class="input" name="item_<%=item.getItemIndex()%>" value="<%=UtilFormatOut.checkNull(item.getDefaultValue())%>" /><% } %>
				<% if(item.getItemType() == Constants.NUMBER_ITEM) { %><font color="red">(<%=CommonUtil.checkDoubleNull(item.getItemLowerSpec())%>,<%=CommonUtil.checkDoubleNull(item.getItemUpperSpec())%>)</font><% } %></td>
			<td class="en11px"><input type="text" class="input" name="note_<%=item.getItemIndex()%>" /></td>
		</tr>
		<% }
		 } %>
      </table>
      </fieldset></td>
  </tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:checkItem();"><span>&nbsp;确定&nbsp;</span></a></li>
	</ul></td>
	<td><ul class="button">
			<li><a class="button-text" href="javascript:reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
	<!----<td><ul class="button">
			<li><a class="button-text" href="#"><span>&nbsp;暂存&nbsp;</span></a></li>
	</ul></td>----->
	<td><ul class="button">
			<li><a class="button-text" href="javascript:windowClose();"><span>&nbsp;关闭&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>流程图</legend>
      <table cellpadding="3" cellspacing="0" border="0" width="100%">
      	<tr bgcolor="#DFE1EC">
        	<td width="70%" style="border: 1px solid #bbbbb0; background: #fffff0;">
            <table cellpadding="3" cellspacing="0" border="0" width="100%">
		        <tr>
		            <td align="right" width="33%" align="right">
		                <table cellpadding="3" cellspacing="3" align="right">
		                	<tr>
		                	    <td align="right" class="en11px" style="border: 1px solid #bbb;" bgcolor="#ffffff" nowrap><%=action.getActionName()%></td>
		                	    <td align="absmiddle" nowrap><img src="<%=request.getContextPath()%>/pms/images/arrow_right_small.gif" height=16 width=16 border=0 align=absmiddle></td>
		                	</tr>
		                </table>
		            </td>

		            <td align="center" valign="middle" width="33%">
		            	<% List actionStatusList = action.getStatusList();
		                	if(CommonUtil.isNotEmpty(actionStatusList)) {  %>
		                <table cellpadding="3" cellspacing="3" border="0">
		                	<% for(Iterator it = actionStatusList.iterator(); it.hasNext(); ) {
		                			ActionStatus status = (ActionStatus)it.next();%>
		                    <tr>
		                        <td class="en11px" style="border: 1px solid #bbb;" bgcolor="#f0f0f0">
		                                <em><%=status.getStatusName()%></em>
		                        </td>
		                    </tr>
		                    <% } %>
		                </table>
		                <% } %>
		            </td>

		            <td align="left" width="33%">
		            	<% if(CommonUtil.isNotEmpty(actionStatusList)) {  %>
		                 <table cellpadding="3" cellspacing="3" border="0"">
		                 	<%   for(Iterator it = actionStatusList.iterator(); it.hasNext(); ) {
		                			ActionStatus status = (ActionStatus)it.next();
		                			int nextActionId = status.getNextActionId();
		                			Action nextAction = job.queryAction(nextActionId); %>
		                    <tr>
		                 	    <td align="absmiddle" nowrap><img src="<%=request.getContextPath()%>/pms/images/arrow_right_small.gif" height=16 width=16 border=0 align=absmiddle></td>
		                 	    <td class="en11px" align="left" style="border: 1px solid #bbb;" bgcolor="#ffffff" nowrap>
		                 	            <%=nextAction.getActionName()%>
		                 	    </td>
		                 	</tr>
		                    <% } %>
		                </table>
		                <% } %>
		            </td>
		        </tr>

		        <tr>
		            <td align="center" class="en11pxb" style="color:red">(当前步动作)</td>
		            <td align="center" class="en11pxb" style="color:red">(可选动作状态)</td>
		            <td align="center" class="en11pxb" style="color:red">(下一步动作)</td>
		        </tr>
		    </table>
            </td>
            <% if(CommonUtil.isNotEmpty(actionStatusList)) { %>
            <td width="30%" align="center"><table border="0" cellspacing="0" cellpadding="0" id="statusButtonArea">
              <% for(Iterator it = actionStatusList.iterator(); it.hasNext(); ) {
		            	ActionStatus status = (ActionStatus)it.next(); %>
              <tr height="30">
                <td><ul class="button">
                        <li><a class="button-text" href="javascript:selectActionStatus(<%=status.getNextActionId()%>);"><span>&nbsp;<%=status.getStatusName()%>&nbsp;</span></a></li>
                </ul></td>
              </tr>
              <% } %>
            </table></td>
            <% } %>
      	</tr>
      </table>
      </fieldset></td>
  </tr>
</table>
</form>

<%
} else {
    if(job.getJobText() != null) { %>
        <script language="javascript">
        	var jobTextArea = window.opener.document.getElementById("jobText");
        	if(jobTextArea != null){// && jobTextArea.innerText.length == 0) {
        		var jobText = '<%=job.getJobText().replaceAll("\n","<br>")%>';
        		jobTextArea.innerText = jobText.replace(new RegExp('<br>',"gm"),'\n');
        	}
        </script>
    <% } %>

    <div class="en11pxb">流程结束</div>
    <script language="javascript">window.close();</script>

<% } %>