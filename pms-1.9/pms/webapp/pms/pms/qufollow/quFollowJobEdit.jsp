<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<%
	String dept=(String)request.getAttribute("DEPT");
	String section=(String)request.getAttribute("SECTION");
	String deptIndex=(String)request.getAttribute("DEPT_INDEX");
	String sectionIndex=(String)request.getAttribute("SECTION_INDEX");
	String existWfSubmitedFollow = (String) request.getAttribute("existWfSubmitedFollow");

	GenericValue gv=(GenericValue)request.getAttribute("GU_FOLLOW");
	pageContext.setAttribute("follow",gv);
	String type=gv.getString("type");
%>

<!-- yui page script-->
<script language="javascript">
	//����
	function save(){
		if("FOLLOW"=='<%=type%>'){
			if(document.getElementById("type1").checked==true){
				document.getElementById("objectType").value="STYLE";
				document.getElementById("object").value=document.getElementById("equipmentType").value;
			}else if(document.getElementById("type2").checked==true){
				document.getElementById("objectType").value="EQUIPMENT";
				document.getElementById("object").value=document.getElementById("eqpId").value;
			}
		}else{
			document.getElementById("objectType").value="EQUIPMENT";
			document.getElementById("object").value='<%=gv.getString("object")%>';
		}
		var followName = Ext.get('followName').dom.value;
        var purpose = Ext.get('purpose').dom.value;
        if(followName==''){
        	Ext.MessageBox.alert('����', '��������������!');
        	return;
        }

        if(followName.length >19){
        	Ext.MessageBox.alert('����', '�����������!');
        	return;
        }

        if(document.getElementById("object").value==""){
        	Ext.MessageBox.alert('����', '��ѡ���豸������豸!');
        	return;
        }
        if(purpose==''){
        	Ext.MessageBox.alert('����', '������Ŀ��!');
        	return;
        }
		follwoJobForm.submit();
	}

	//ɾ������
	function delFollowJob(followIndex){
		Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ����Ҫɾ������Ϣ��',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/delFollowJob</ofbiz:url>?followIndex='+followIndex
				document.location.href=url;
			}else{
				return;
			}
	     });
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
	    initEqpState();
	 });

	 function initEqpState(){
	 	if('<%=gv.getString("objectType")%>'=='STYLE'){
		 	Ext.get('equipmentDiv').dom.style.display ='';
	 		Ext.get('eqpidDiv').dom.style.display ='none';
		 }else if('<%=gv.getString("objectType")%>'=='EQUIPMENT'){
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

	 //��ǩ
	 function sendSubmit(){
	 	var obj = '<%=Constants.SUBMIT_FOLLOW%>';
	 	var objectIndex = Ext.getDom('followIndex').value;
	 	var submitType = '<%=Constants.SUBMIT_INSERT%>';
	 	var submitObjectName = Ext.getDom("followName").value;
	 	var parameter = Ext.urlEncode({submitObject: obj, submitObjectIndex: objectIndex, submitType: submitType, submitObjectName: submitObjectName});
	 	//alert(parameter); return;
	 	Ext.MessageBox.confirm('��ǩȷ��', '���б�������ȵ���޸İ�ť�������޸����ݡ�ȷ���ѱ��棬ֱ����ǩ��',function result(value){
	 		if(value=="yes"){
	 			var url='<ofbiz:url>/sendSubmit</ofbiz:url>?'+parameter;
				document.location.href=url;
			}else{
				return;
			}
	     });
	 }
</script>

<form action="<%=request.getContextPath()%>/control/followJobDefine" method="post" id="follwoJobForm" onsubmit="return false;">
<input id="deptIndex" type="hidden" name="deptIndex" value="<%=deptIndex%>" />
<input id="sectionIndex" type="hidden" name="sectionIndex" value="<%=sectionIndex%>" />
<input id="status" type="hidden" name="status" value='<ofbiz:inputvalue entityAttr="follow" field="status"/>' />
<input id="objectType" type="hidden" name="objectType" />
<input id="object" type="hidden" name="object" />
<input id="type" type="hidden" name="type" value='<%=type%>'/>
<input id="followIndex" type="hidden" name="followIndex" value='<ofbiz:inputvalue entityAttr="follow" field="followIndex"/>'/>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>����׷�ٲ�ѯ����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
	        <td width="15%" bgcolor="#ACD5C9" class="en11pxb" height="20">����׷������</td>
	    	<td width="35%"  class="en11px" colspan="3"><input type="text" name="followName" class="input" size="50" value='<ofbiz:entityfield attribute="follow" field="followName"/>'></td>
        </tr>

        <tr bgcolor="#DFE1EC">
	    	<td bgcolor="#ACD5C9" class="en11pxb" height="20">Ŀ��</td>
	    	<td class="en11pxb" colspan="3"><input type="text" name="purpose" class="input" size="50" value='<ofbiz:entityfield attribute="follow" field="purpose"/>'></td>
        </tr>

        <tr bgcolor="#DFE1EC">
	        <td width="15%" bgcolor="#ACD5C9" class="en11pxb" height="20">����</td>
	    	<td width="35%"  class="en11px"><%=dept%></td>
	    	<td width="15%" bgcolor="#ACD5C9" class="en11pxb">�α�</td>
	    	<td width="35%" class="en11px"><%=section%></td>
        </tr>

        <tr bgcolor="#DFE1EC">
          <td bgcolor="#ACD5C9" class="en11pxb" >
            <%if ("FOLLOW".equals(type)){%>
            <input type="radio"  id="type1" name="type1" checked value="0" onclick="selectStyle();">�豸����&nbsp;
            <br><input type="radio"  id="type2" name="type1" checked value="0" onclick="selectEquipment();">�豸
            <%}else if("FORM".equals(type)){
    			out.print("�豸");
    		}
    		%>
    	  </td>

          <td class="en11pxb" >
            <%if ("FOLLOW".equals(type)){%>
            <div id="equipmentDiv">
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
    		<%}else if("FORM".equals(type)){
    			out.print(gv.getString("object"));
    		}
    		%>
    		</td>

    		<td bgcolor="#ACD5C9" class="en11pxb">������</td>
	    	<td class="en11px"><ofbiz:inputvalue entityAttr="follow" field="creator"/></td>
        </tr>

        <tr bgcolor="#DFE1EC">
        	<td class="en11pxb" colspan="4" align="left">
	        	<table border="0" cellspacing="0" cellpadding="0">
			  		<tr height="30">
			  		    <%if (existWfSubmitedFollow.equals("N")) {%>
					   	<td width="20">&nbsp;</td>
					    <td>
					        <ul class="button">
    							<li><a class="button-text" href="#" onclick="javascript:save();"><span>&nbsp;�޸�&nbsp;</span></a></li>
    						</ul>
    						<ul class="button">
    							<li><a class="button-text" href="#" onclick="javascript:delFollowJob('<ofbiz:inputvalue entityAttr="follow" field="followIndex"/>');"><span>&nbsp;ɾ��&nbsp;</span></a></li>
    						</ul>
    						<ul class="button">
    							<li><a class="button-text" href="#" onclick="javascript:sendSubmit();"><span>&nbsp;��ǩ&nbsp;</span></a></li>
    						</ul>
						</td>
						<%} else {%>
						<td>����ǩ����֪ͨ��������ǩ�ˣ�<td/>
						<%}%>
			  		</tr>
				</table>
        	</td>
        </tr>
      </table>
      </fieldset></td>
  </tr>
</table>
</form>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:history.back(-1)"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>
	</tr>
</table>

<script language="javascript">
	if("FOLLOW"=='<%=type%>'){
		 if('<%=gv.getString("objectType")%>'=='STYLE'){
		 	document.getElementById("type1").checked=true;
		 	document.getElementById("equipmentType").value='<%=gv.getString("object")%>';
		 }else if('<%=gv.getString("objectType")%>'=='EQUIPMENT'){
		 	document.getElementById("type2").checked=true;
		 	document.getElementById("eqpId").value='<%=gv.getString("object")%>';
		 }
	}
</script>