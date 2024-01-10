<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>


<%
	String index=request.getParameter("abnormalIndex");
	GenericValue gv=(GenericValue)request.getAttribute("ABNORMAL");
	pageContext.setAttribute("abnormal",gv);
	String section=(String)request.getAttribute("SECTION");
	String accountDept=(String)request.getAttribute("ACCOUNT_DEPT");
	int status=Integer.parseInt(gv.getString("status"));
	String displayStatus="";
	if(Constants.CREAT==status||Constants.START==status||Constants.HOLD==status){
		displayStatus="未完成";
	}else{
		displayStatus="完成";
	}
	String type=gv.getString("formType");
%>

<script language="javascript">
	//开始表单
	//检查是否03-DOWN，03-REP，03-FD，03-SERV，是否可以开始
	//不是，弹出警告框
	function abnormalFormStart(){
		if('<%=Constants.FORM_TYPE_PATCH%>'=='<ofbiz:inputvalue entityAttr="abnormal" field="formType"/>'){
			loading();
			var actionURL='<ofbiz:url>/startAbnormalFormEntry</ofbiz:url>?functionType=1&abnormalIndex=<%=index%>&eqpId=<ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/>&formType=<%=Constants.FORM_TYPE_PATCH%>';
			document.location.href=actionURL;
		}else{
			var eqpId='<ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/>';
			var actionURL='<ofbiz:url>/abnormalFormEqpStatusQueryEntry</ofbiz:url>?eqpId='+eqpId;
			Ext.lib.Ajax.formRequest('abnorForm',actionURL,{success: commentQuerySuccess, failure: commentFailure});
		}

	}

	//远程调用成功
	function commentQuerySuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			var status=result.status;

			if("success"==status){
				var eqpStatus=result.eqpStatus;
				if("<%=Constants.TS_STATUS%>"==eqpStatus || "<%=Constants.TS_START_STATUS%>"==eqpStatus
				     ||"03-FD"==eqpStatus || "03-SERV"==eqpStatus || "03_REP_COMP" == eqpStatus || "03_DOWN" == eqpStatus
				     || "<%=Constants.PR_START_STATUS%>".indexOf(eqpStatus) > -1) {
				    if ("<%=Constants.PR_START_STATUS%>".indexOf(eqpStatus) > -1) {
				        alert(eqpStatus + "表单开始时不修改设备状态，结束后将修改为" + "<%=Constants.PR_END_STATUS%>");
				    }

					loading();
					var actionURL='<ofbiz:url>/startAbnormalFormEntry</ofbiz:url>?functionType=1&abnormalIndex=<%=index%>&eqpId=<ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/>&formType=<%=Constants.FORM_TYPE_NORMAL%>&eqpStatus=' + eqpStatus;
					document.location.href=actionURL;
				}else{
					Ext.MessageBox.alert('警告', '目前MES设备状态为'+eqpStatus+'，不可更改设备状态为<%=Constants.TS_START_STATUS%>，此异常维修无法开始，请与相关人员联络！');
					return;
				}
			}else if("error"==status){
				var message=result.message;
				setErrorMsg(message);
			}
		}
	}

   //远程调用失败
   var commentFailure = function(o){
    	Ext.MessageBox.alert('ERROR', 'Unable to connect.');
   };

   //表单删除
   function abnormalFormDelete(){
   		Ext.MessageBox.confirm('删除确认', '您确信要删除此表单吗？',function result(value){
			if(value=="yes"){
				loading();
	        	var url='<ofbiz:url>/delPathAbnormalForm</ofbiz:url>?abnormalIndex=<%=index%>'
   				document.location.href=url;
			}else{
				return;
			}
	     });
   }
</script>
<form action="" method="post" id="abnorForm">
<fieldset><legend>异常记录表</legend>
 <table width="100%" border="0" cellspacing="1" cellpadding="2">
    <tr bgcolor="#DFE1EC" height="25">
      <td width="15%" bgcolor="#ACD5C9" class="en11pxb">EQPID</td>
      <td width="18%" class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/></td>
      <td width="15%" bgcolor="#ACD5C9" class="en11pxb">部门</td>
      <td width="18%" class="en11px"><%=accountDept%></td>
      <td width="15%" bgcolor="#ACD5C9" class="en11pxb">课别</td>
      <td width="18%" class="en11px"><%=section%></td>
    </tr>
    <tr bgcolor="#DFE1EC" height="25">
      <td class="en11pxb" bgcolor="#ACD5C9">报告人</td>
      <td class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="createName"/></td>
      <td class="en11pxb" bgcolor="#ACD5C9">文件编号</td>
      <td class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="abnormalName"/></td>
      <td bgcolor="#ACD5C9" class="en11pxb">撰写时间</td>
      <td class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="createTime"/></td>
    </tr>
    <tr bgcolor="#DFE1EC" height="25">
       <td bgcolor="#ACD5C9" class="en11pxb">目前处理状况</td>
       <td class="en11px" colspan="5"><%=displayStatus%></td>
	</tr>
</table>
</fieldset>
</form>

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
			<li><a class="button-text" href="#" onclick="abnormalFormStart();"><span>&nbsp;开始&nbsp;</span></a></li>
	</ul>
	<%if(Constants.FORM_TYPE_PATCH.equals(type)){ %>
	<ul class="button">
			<li><a class="button-text" href="#" onclick="abnormalFormDelete();"><span>&nbsp;删除&nbsp;</span></a></li>
	</ul>
	<% }%>
	</td>
  </tr>
</table>
