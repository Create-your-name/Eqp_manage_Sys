<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="java.util.Map"%>
<%
	Map paramMap = (Map)request.getAttribute("paramMap");
	GenericValue pmForm=(GenericValue)request.getAttribute("pmForm");
	GenericValue defaultPeriod = (GenericValue)request.getAttribute("defaultPeriod");
	pageContext.setAttribute("pmForm",pmForm);
	pageContext.setAttribute("defaultPeriod",defaultPeriod);
	String index=(String)paramMap.get("pmIndex");
	//Map paramMap = (Map)request.Attribute("paramMap");
	//String section=(String)request.getAttribute("SECTION");
	//String accountDept=(String)request.getAttribute("ACCOUNT_DEPT");
	int status=Integer.parseInt(pmForm.getString("status"));
	String displayStatus="";
	if(Constants.CREAT==status||Constants.START==status||Constants.HOLD==status){
		displayStatus="未完成";
	}else{
		displayStatus="完成";
	}
%>
<script language="javascript">
	//开始表单
	function pmFormStart(){
		if('<%=Constants.FORM_TYPE_PATCH%>'=='<ofbiz:inputvalue entityAttr="pmForm" field="formType"/>'){
			loading();
			var eqpId = '<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
			var promisStatus = '<ofbiz:inputvalue entityAttr="defaultPeriod" field="eqpStatus"/>';
			var actionURL='<ofbiz:url>/startPmFormEntry</ofbiz:url>?functionType=1&pmIndex=<%=index%>&eqpId='+eqpId+'&promisStatus='+promisStatus;
			document.location.href=actionURL;
		}else{
			var eqpId='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
			var actionURL='<ofbiz:url>/pmFormEqpStatusQueryEntry</ofbiz:url>?eqpId='+eqpId;
			Ext.lib.Ajax.formRequest('pmForm',actionURL,{success: commentQuerySuccess, failure: commentFailure});
		}
	}

	//远程调用成功
	function commentQuerySuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!="") {
			var status=result.status;

			if ("success"==status) {
				var eqpStatus = result.eqpStatus;
				var eqpId = '<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
				var promisStatus = '<ofbiz:inputvalue entityAttr="defaultPeriod" field="eqpStatus"/>';

				if ('<%=Constants.TS_END_STATUS%>'==eqpStatus && '<%=Constants.Y%>'=='<ofbiz:inputvalue entityAttr="pmForm" field="isChangeStatus"/>') {
				    //03-POST状态下不能开始更改设备状态的PM表单的限制
					Ext.MessageBox.alert('警告', '当前MES设备状态为'+eqpStatus+'，不可更改设备状态为'+promisStatus+'，此保养无法开始，请先改到01或02状态后开始保养');
					return;
				} else {
    				loading();
    				var actionURL = '<ofbiz:url>/startPmFormEntry</ofbiz:url>?functionType=1&pmIndex=<%=index%>&eqpId=' + eqpId + '&promisStatus=' + promisStatus;
    				document.location.href = actionURL;
				}
			} else if ("error"==status) {
				var message = result.message;
				setErrorMsg(message);
			}
		}
	}

   //远程调用失败
   var commentFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
   };

	function deletePmForm(){
		var pmIndex='<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>'
		var pmName='<ofbiz:inputvalue entityAttr="pmForm" field="pmName"/>'
		var actionURL='<ofbiz:url>/deletePmFormEntry</ofbiz:url>?pmIndex='+pmIndex+'&pmName='+pmName;
		pmForm.action=actionURL;
		pmForm.submit();
	}
</script>
<fieldset><legend>保养记录表</legend>
<form action="" method="POST" name="pmForm" id="pmForm">
 <table width="100%" border="0" cellspacing="1" cellpadding="2">
    <tr bgcolor="#DFE1EC" height="25">
      <td width="15%" bgcolor="#ACD5C9" class="en11pxb">EQPID</td>
      <td width="18%" class="en11px"><ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/></td>
      <td width="15%" bgcolor="#ACD5C9" class="en11pxb">部门</td>
      <td width="18%" class="en11px"><%=(String)paramMap.get("accountDept")%></td>
      <td width="15%" bgcolor="#ACD5C9" class="en11pxb">课别</td>
      <td width="18%" class="en11px"><%=(String)paramMap.get("accountSection")%></td>
    </tr>
    <tr bgcolor="#DFE1EC" height="25">
      <td class="en11pxb" bgcolor="#ACD5C9">报告人</td>
      <td class="en11px"><ofbiz:inputvalue entityAttr="pmForm" field="createUser"/></td>
      <td class="en11pxb" bgcolor="#ACD5C9">文件编号</td>
      <td class="en11px"><ofbiz:inputvalue entityAttr="pmForm" field="pmName"/></td>
      <td bgcolor="#ACD5C9" class="en11pxb">撰写时间</td>
      <td class="en11px"><ofbiz:inputvalue entityAttr="pmForm" field="createTime"/></td>
    </tr>
    <tr bgcolor="#DFE1EC" height="25">
       <td bgcolor="#ACD5C9" class="en11pxb">目前处理状况</td>
       <td class="en11px" colspan="5"><%=displayStatus%></td>
	</tr>
</table>
</fieldset>

<table cellpadding='5' width='100%' cellspacing='5px' class='successMessage' border="0">
<colgroup><col width='15'><col></colgroup>
<tr><td valign='top'>
		<img src="/pms/pms/images/warning.gif" width="16" height="16" align="absmiddle" alt="" border="0">
	</td>
<td class="en11pxb">请在操作之前确认处于安全操作状态，确保不会发生高压、高温、爆炸以及毒气泄漏等造成的安全事故</td></tr>
</table>


<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="#" onclick="pmFormStart();"><span>&nbsp;开始&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<%
	if ("-1".equalsIgnoreCase(pmForm.getString("status"))){
%>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" id="deletePmForm" href="#" onclick="deletePmForm();"><span>&nbsp;删除保养表单&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>
<%
	}
%>
</form>
