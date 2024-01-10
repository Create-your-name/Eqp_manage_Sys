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
function editData() {
	doSubmit("<ofbiz:url>/editFlowDataDone</ofbiz:url>");
}
function doSubmit(url) {
	loading();
	document.flowDataEditForm.action=url;
	document.flowDataEditForm.submit();
}
function checkItem() {
	<% if(CommonUtil.isNotEmpty(job.getActionlist())) { 
	for(Iterator actionIt = job.getActionlist().iterator(); actionIt.hasNext(); ) { 
	Action action = (Action) actionIt.next();
	int actionId = action.getActionId(); %>
	<% if(CommonUtil.isNotEmpty(action.getItemlist())) {
			for(Iterator itemCheckIt = action.getItemlist().iterator(); itemCheckIt.hasNext(); ) {
				ActionItem checkItem = (ActionItem)itemCheckIt.next();
				long pointIndex = checkItem.getPointIndex();
				String key = "value_" + actionId + "_" + pointIndex;
				String noteKey = "itemNote_" + actionId + "_" + pointIndex;
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
			}
		} 
		}
		} %>
	doSubmit("<ofbiz:url>/editFlowDataDone</ofbiz:url>");
}
</script>

<form method="post" name="flowDataEditForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<input type="hidden" name="jobRelationIndex" value="<%=request.getParameter("jobRelationIndex")%>" />
<tr>
  <td><fieldset>
      <legend>流程</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td width="20%" class="en11pxb">流程名</td>
      		<td width="80%" bgcolor="#DFE1EC" class="en11px"><%=job.getJobName()%></td>
    	</tr>
    	<tr bgcolor="#ACD5C9">
      		<td width="20%" class="en11pxb">流程描述</td>
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
      <legend>流程列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td width="5%" class="en11pxb">ID</td>
		 	<td width="34%" class="en11pxb">动作说明</td>
		 	<td width="61%" class="en11pxb">备注</td>
    	</tr>
<%  if(CommonUtil.isNotEmpty(job.getActionlist())) {
   		Iterator it = job.getActionlist().iterator(); 
   		while(it.hasNext()) { 
	    	Action action = (Action)it.next(); %>
    	<tr bgcolor="#F0C000">
    		<td class="en11px"><%=action.getActionId()%></td>
		  	<td class="en11px"><%=action.getActionName()%><%if(Constants.ACTION_DCOP_TYPE.equals(action.getActionType())) { %><font color="#ff0000"> DCOP</font><% } %></td>
		  	<td class="en11px"><%if("action".equals(action.getNodeType())) { %><input type="text" size="25" name="note_<%=action.getActionId()%>_<%=action.getActionRecordIndex()%>" class="input" value="<%=UtilFormatOut.checkNull(action.getActionNote())%>" /><% } %></td>
	  	</tr>
	  	<% if(CommonUtil.isNotEmpty(action.getItemlist())) { %>
	  	<tr>
	  		<td bgcolor="#ACD5C9"></td>
	  		<td colspan="2"><table width="100%" border="0" cellpadding="1" cellspacing="1">
              <tr bgcolor="#ACD5C9">
                <td class="en11pxb" width="15%">项目名称</td>
                <td class="en11pxb" width="24%">项目值</td>
                <td class="en11pxb" width="8%">上限</td>
                <td class="en11pxb" width="8%">下限</td>
                <td class="en11pxb" width="5%">单位</td>
                <td class="en11pxb" width="40%">备注</td>
              </tr>
              <% for(Iterator itemIt = action.getItemlist().iterator(); itemIt.hasNext(); ) { 
              		ActionItem item = (ActionItem) itemIt.next(); %>
              <tr<% if(item.isOOS()) { %> bgcolor="#ff0000"<% } else { %> bgcolor="#DFE1EC"<% } %>>
                <td class="en11px"><%=item.getItemName()%></td>
                <td class="en11px"><% if(item.getItemType() == Constants.OPTION_ITEM) { 
					String[] optionValues = item.getItemOption().split("\\|"); 
					String defaultValue = item.getItemValue(); %>
				<select name="value_<%=action.getActionId()%>_<%=item.getPointIndex()%>">
					<% for(int optionIndex = 0; optionIndex < optionValues.length; optionIndex++) { 
						String optionValue = optionValues[optionIndex]; %>
					<option value="<%=optionValue%>"<%if(optionValue.equals(defaultValue)) { %> selected<% } %>><%=optionValue%></option>
					<% } %>
				</select>
				<% } else { %><input type="text" name="value_<%=action.getActionId()%>_<%=item.getPointIndex()%>" class="input" value="<%=UtilFormatOut.checkNull(item.getItemValue())%>" />
				<% } %></td>
                <td class="en11px"><% if(item.getItemType()==Constants.NUMBER_ITEM) { out.println(CommonUtil.checkDoubleNull(item.getItemUpperSpec()));}%></td>
                <td class="en11px"><% if(item.getItemType()==Constants.NUMBER_ITEM) { out.println(CommonUtil.checkDoubleNull(item.getItemLowerSpec()));}%></td>
                <td class="en11px"><%=UtilFormatOut.checkNull(item.getItemUnit())%></td>
                <td class="en11px"><input type="text" size="25" name="itemNote_<%=action.getActionId()%>_<%=item.getPointIndex()%>" class="input" value="<%=UtilFormatOut.checkNull(item.getItemNode())%>" /></td>
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
</form>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
  	<% if(CommonUtil.isNotEmpty(job.getActionlist()) && job.getActionlist().size() > 1) { %>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:checkItem()"><span>&nbsp;保存&nbsp;</span></a></li> 
	</ul></td>
	<% } %>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:window.close()"><span>&nbsp;关闭&nbsp;</span></a></li> 
	</ul></td>
  </tr>
</table>
<% } %>