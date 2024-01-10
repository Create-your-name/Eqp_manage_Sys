<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="java.util.*" %>
<% 
	String startDate=UtilFormatOut.checkNull((String)request.getAttribute("startDate"));
	String endDate=UtilFormatOut.checkNull((String)request.getAttribute("endDate"));
	List abnormalList = (List)request.getAttribute("ABNORMALFORM_LIST");
%>

<!-- yui page script-->
<script language="javascript">
	//��ѯ
	function query(){
		var startDate=Ext.get('startDate').dom.value;
		var endDate=Ext.get('endDate').dom.valu;
		if((startDate==""||startDate==undefined) &&(endDate==""||endDate==undefined)){
			Ext.MessageBox.alert('����', '�������ѯ����!');
			return;
		}
		loading();
		tsForm.submit();
	}
	
	//����
	function reset(){
		 Ext.get('eqpId').dom.value='';
		 Ext.get('startDate').dom.value='';
		 Ext.get('endDate').dom.value='';
	}
	
	//��������½�ҳ��
	function intoAbnormalForm(index,eqpId){
		loading();
		var actionURL='<ofbiz:url>/intoAbnormalFormManageEntry</ofbiz:url>?functionType=1&abnormalIndex='+index+'&eqpId='+eqpId;
		document.location.href=actionURL;	
	}
	
	//��ʼ��ҳ��ؼ�
	Ext.onReady(function(){
	     var startDate = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:true
	    });
	    
	 	var endDate = new Ext.form.DateField({
	 		format: 'Y-m-d',
	        allowBlank:true
	    });
	    
	    startDate.applyTo('startDate');
	    endDate.applyTo('endDate');
	    
	    initDate();
	 });
	 
	 function initDate(){
		 Ext.get('startDate').dom.value='<%=startDate%>';
		 Ext.get('endDate').dom.value='<%=endDate%>';
	 }
</script>
<form action="<%=request.getContextPath()%>/control/queryAbnormalFormEntry" method="post" id="tsForm" onsubmit="return false;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�쳣��¼��ѯ����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC"> 
	        <td bgcolor="#ACD5C9" class="en11pxb">������������</td>
	        <td width="30%"><input type="text" ID="startDate" NAME="startDate" readonly size="26"></td>
	    	<td  width="5%" class="en11pxb">��:</td>
	    	<td width="45%"><input type="text" ID="endDate" NAME="endDate"  readonly size="26"></td>
        </tr>
        <tr bgcolor="#DFE1EC"> 
        	<td class="en11pxb" colspan="4" align="left">
	        	<table border="0" cellspacing="0" cellpadding="0">
			  		<tr height="30">
					    <td><ul class="button">
							<li><a class="button-text" href="#" onclick="javascript:query();"><span>&nbsp;��ѯ&nbsp;</span></a></li> 
						</ul>
						<ul class="button">
							<li><a class="button-text" href="#" onclick="javascript:reset();"><span>&nbsp;����&nbsp;</span></a></li> 
						</ul>
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
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�쳣��¼�����б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="10%" class="en11pxb">��̨���</td>
          <td width="20%" class="en11pxb">�������</td>
          <td width="10%" class="en11pxb">��������</td>
          <td width="10%" class="en11pxb">׫д��</td>
          <td width="20%" class="en11pxb">�쳣����ʱ��</td>
          <td width="10%" class="en11pxb">����״̬</td>
          <td width="20%" class="en11pxb">��������޸�ʱ��</td>
        </tr>
         <% if(abnormalList != null && abnormalList.size() > 0) {  
       		for(Iterator it = abnormalList.iterator();it.hasNext();) { 
					Map map = (Map)it.next();
					%>
	        <tr bgcolor="#DFE1EC">
		    <td class="en11px"><a href="#" onclick="intoAbnormalForm('<%=map.get("ABNORMAL_INDEX")%>','<%=map.get("EQUIPMENT_ID")%>')"><%=map.get("EQUIPMENT_ID")%></a></td>
		    <td class="en11px"><%=map.get("ABNORMAL_NAME")%></td>
		    <td class="en11px"><%if(Constants.FORM_TYPE_PATCH.equalsIgnoreCase((String)map.get("FORM_TYPE"))){%>����<%}else{%>����<%}%></td>
		    <td class="en11px"><%=map.get("CREATE_NAME")%></td>
		    <td class="en11px"><%=map.get("ABNORMAL_TIME")%></td>
		    <td class="en11px"><%=map.get("STATUS")%></td>
		    <td class="en11px"><%=map.get("UPDATE_TIME")%></td>
		    </tr>
	     <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>