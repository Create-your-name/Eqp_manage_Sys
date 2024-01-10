<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%
	List quFollowList = (List)request.getAttribute("QUFOLLOW_LIST");
%>

<!-- yui page script-->
<script language="javascript">
	//��ѯ
	function query(){
		if(document.getElementById("type1").checked==true){
				document.getElementById("objectType").value="STYLE";
				document.getElementById("object").value=document.getElementById("equipmentType").value;
		}else if(document.getElementById("type2").checked==true){
			document.getElementById("objectType").value="EQUIPMENT";
			document.getElementById("object").value=document.getElementById("eqpId").value;
		}
		follwoJobForm.submit();
	}

	//����
	function addFollowJob(){
		var url='<ofbiz:url>/intoAddQuFollow</ofbiz:url>?type=FOLLOW';
		document.location.href=url;
	}

	//�޸�
	function editFollowJob(status,index){
		var url='';
		if(status=='0'){
			//����
			url='<ofbiz:url>/intoEditQuFollow</ofbiz:url>?followIndex='+index;
		}else if(status=='2'){
			//δ�᰸
			url='<ofbiz:url>/intoAddItemQuFollow</ofbiz:url>?followIndex='+index;
		}
		document.location.href=url;
	}
	//��ʼ��ҳ��ؼ�
	Ext.onReady(function(){
	    var eqpType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'equipmentType',
	        width:170,
	        forceSelection:true
	    });

	    var eqpId = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'eqpId',
	        width:170,
	        forceSelection:true
	    });

	     var stats = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'status',
	        width:170,
	        forceSelection:true
	 	});
	    initEqpState();
	 });


	 function initEqpState(){
	  	if('<%=request.getParameter("objectType")%>'=='STYLE'){
		 	Ext.get('equipmentDiv').dom.style.display ='';
	 		Ext.get('eqpidDiv').dom.style.display ='none';
		 }else if('<%=request.getParameter("objectType")%>'=='EQUIPMENT'){
		 	Ext.get('equipmentDiv').dom.style.display ='none';
	 		Ext.get('eqpidDiv').dom.style.display ='';
		 }else{
	 		Ext.get('equipmentDiv').dom.style.display ='none';
 			Ext.get('eqpidDiv').dom.style.display ='';
 		}
	 }

	 //ѡ�й��մ���
	 function selectStyle(){
	 	Ext.get('equipmentDiv').dom.style.display ='';
	 	Ext.get('eqpidDiv').dom.style.display ='none';
	 }

	 //ѡ���豸ID
	 function selectEquipment(){
	 	Ext.get('equipmentDiv').dom.style.display ='none';
	 	Ext.get('eqpidDiv').dom.style.display ='';
	 }

</script>
<form action="<%=request.getContextPath()%>/control/queryFollowJobList" method="post" id="follwoJobForm" onsubmit="return false;">
<input id="objectType" type="hidden" name="objectType" />
<input id="object" type="hidden" name="object" />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>����׷�ٲ�ѯ����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
          <td width="30%" bgcolor="#ACD5C9" class="en11pxb"><input type="radio"  id="type1" name="type1" value="0" onclick="selectStyle();" >�豸����&nbsp;
          <input type="radio"  id="type2" name="type1"  value="0" onclick="selectEquipment();" checked>�豸</td>
          <td width="70%"><div id="equipmentDiv">
	          <select id="equipmentType" name="equipmentType">
		          <option value=""></option>
		          <ofbiz:if name="EQUIPMENT_LIST">
			        <ofbiz:iterator name="cust" property="EQUIPMENT_LIST">
				    <option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentType"/>'><ofbiz:inputvalue entityAttr="cust" field="equipmentType"/></option>
			      </ofbiz:iterator>
		      	</ofbiz:if>
    		</select>
    		</div>
    		<div id="eqpidDiv">
	    		<select id="eqpId" name="eqpId">
		          <option value=""></option>
		          <ofbiz:if name="EQPID_LIST">
			        <ofbiz:iterator name="eqpid" property="EQPID_LIST">
				    <option value='<ofbiz:inputvalue entityAttr="eqpid" field="equipmentId"/>'><ofbiz:inputvalue entityAttr="eqpid" field="equipmentId"/></option>
			      </ofbiz:iterator>
		      	</ofbiz:if>
	    		</select>
    		</div>
    		</td>
        </tr>
        <tr bgcolor="#DFE1EC">
	        <td bgcolor="#ACD5C9" class="en11pxb">״̬</td>
	        <td>
	            <select id="status" name="status">
        	        <option value=''></option>
        	        <option value='0'>����</option>
        	        <option value='2'>δ�᰸</option>
        	    </select>
	        </td>
        </tr>
        <tr bgcolor="#DFE1EC">
        	<td class="en11pxb" colspan="4" align="left">
	        	<table border="0" cellspacing="0" cellpadding="0">
			  		<tr height="30">
					   	<td width="20">&nbsp;</td>
					    <td><ul class="button">
								<li><a class="button-text" href="#" onclick="javascript:query();"><span>&nbsp;��ѯ&nbsp;</span></a></li>
						</ul>
						<ul class="button">
								<li><a class="button-text" href="#" onclick="javascript:addFollowJob();"><span>&nbsp;����&nbsp;</span></a></li>
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
      <legend>����׷���б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="10%" class="en11pxb">����</td>
          <td width="15%" class="en11pxb">Ŀ��</td>
          <td width="10%" class="en11pxb">����</td>
          <td width="10%" class="en11pxb">�α�</td>
          <td width="10%" class="en11pxb">�豸</td>
          <td width="8%" class="en11pxb">״̬</td>
          <td width="7%" class="en11pxb">������</td>
          <td width="15%" class="en11pxb">����ʱ��</td>
        </tr>
        <%
        if(quFollowList != null && quFollowList.size() > 0) {
       		for(Iterator it = quFollowList.iterator();it.hasNext();) {
				Map map = (Map)it.next();%>
		        <tr bgcolor="#DFE1EC">
		          <td class="en11px">
		            <a href="#" onclick="editFollowJob('<%=map.get("STATUS")%>','<%=map.get("FOLLOW_INDEX")%>')">
		                <%=UtilFormatOut.checkNull((String)map.get("FOLLOW_NAME"))%>
		            </a>
		          </td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("PURPOSE"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("EQUIPMENT_DEPT"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("SECTION"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("OBJECT"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("CSTATUS"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("CREATOR"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("CREATE_TIME"))%></td>
		        </tr>
	  <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>

<script language="javascript">
		 if('<%=request.getParameter("objectType")%>'=='STYLE'){
		 	document.getElementById("type1").checked=true;
		 	document.getElementById("equipmentType").value='<%=request.getParameter("object")%>';
		 }else if('<%=request.getParameter("objectType")%>'=='EQUIPMENT'){
		 	document.getElementById("type2").checked=true;
		 	document.getElementById("eqpId").value='<%=request.getParameter("object")%>';
		 }

		  if('<%=UtilFormatOut.checkNull(request.getParameter("status"))%>'!=''){
		 	document.getElementById("status").value='<%=request.getParameter("status")%>';
		 }
</script>
