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
		displayStatus="δ���";
	}else{
		displayStatus="���";
	}
	String type=gv.getString("formType");
%>

<script language="javascript">
	//��ʼ��
	//����Ƿ�03-DOWN��03-REP��03-FD��03-SERV���Ƿ���Կ�ʼ
	//���ǣ����������
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

	//Զ�̵��óɹ�
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
				        alert(eqpStatus + "����ʼʱ���޸��豸״̬���������޸�Ϊ" + "<%=Constants.PR_END_STATUS%>");
				    }

					loading();
					var actionURL='<ofbiz:url>/startAbnormalFormEntry</ofbiz:url>?functionType=1&abnormalIndex=<%=index%>&eqpId=<ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/>&formType=<%=Constants.FORM_TYPE_NORMAL%>&eqpStatus=' + eqpStatus;
					document.location.href=actionURL;
				}else{
					Ext.MessageBox.alert('����', 'ĿǰMES�豸״̬Ϊ'+eqpStatus+'�����ɸ����豸״̬Ϊ<%=Constants.TS_START_STATUS%>�����쳣ά���޷���ʼ�����������Ա���磡');
					return;
				}
			}else if("error"==status){
				var message=result.message;
				setErrorMsg(message);
			}
		}
	}

   //Զ�̵���ʧ��
   var commentFailure = function(o){
    	Ext.MessageBox.alert('ERROR', 'Unable to connect.');
   };

   //��ɾ��
   function abnormalFormDelete(){
   		Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˱���',function result(value){
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
<fieldset><legend>�쳣��¼��</legend>
 <table width="100%" border="0" cellspacing="1" cellpadding="2">
    <tr bgcolor="#DFE1EC" height="25">
      <td width="15%" bgcolor="#ACD5C9" class="en11pxb">EQPID</td>
      <td width="18%" class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/></td>
      <td width="15%" bgcolor="#ACD5C9" class="en11pxb">����</td>
      <td width="18%" class="en11px"><%=accountDept%></td>
      <td width="15%" bgcolor="#ACD5C9" class="en11pxb">�α�</td>
      <td width="18%" class="en11px"><%=section%></td>
    </tr>
    <tr bgcolor="#DFE1EC" height="25">
      <td class="en11pxb" bgcolor="#ACD5C9">������</td>
      <td class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="createName"/></td>
      <td class="en11pxb" bgcolor="#ACD5C9">�ļ����</td>
      <td class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="abnormalName"/></td>
      <td bgcolor="#ACD5C9" class="en11pxb">׫дʱ��</td>
      <td class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="createTime"/></td>
    </tr>
    <tr bgcolor="#DFE1EC" height="25">
       <td bgcolor="#ACD5C9" class="en11pxb">Ŀǰ����״��</td>
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
<td class="en11pxb">���ڲ���֮ǰȷ�ϴ��ڰ�ȫ����״̬��ȷ�����ᷢ����ѹ�����¡���ը�Լ�����й©����ɵİ�ȫ�¹�</td></tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="#" onclick="abnormalFormStart();"><span>&nbsp;��ʼ&nbsp;</span></a></li>
	</ul>
	<%if(Constants.FORM_TYPE_PATCH.equals(type)){ %>
	<ul class="button">
			<li><a class="button-text" href="#" onclick="abnormalFormDelete();"><span>&nbsp;ɾ��&nbsp;</span></a></li>
	</ul>
	<% }%>
	</td>
  </tr>
</table>
