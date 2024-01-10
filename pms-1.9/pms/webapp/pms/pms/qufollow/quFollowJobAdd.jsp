<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%
	String dept=(String)request.getAttribute("DEPT");
	String section=(String)request.getAttribute("SECTION");
	String deptIndex=(String)request.getAttribute("DEPT_INDEX");
	String sectionIndex=(String)request.getAttribute("SECTION_INDEX");
	String creator=(String)request.getAttribute("creator");
%>
<!-- yui page script-->
<script language="javascript">
	//����
	function save(){
		if(document.getElementById("type1").checked==true){
			document.getElementById("objectType").value="STYLE";
			document.getElementById("object").value=document.getElementById("equipmentType").value;
		}else if(document.getElementById("type2").checked==true){
			document.getElementById("objectType").value="EQUIPMENT";
			document.getElementById("object").value=document.getElementById("eqpId").value;
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
	 	Ext.get('equipmentDiv').dom.style.display ='none';
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

<form action="<%=request.getContextPath()%>/control/followJobDefine" method="post" id="follwoJobForm" onsubmit="return false;">
<input id="deptIndex" type="hidden" name="deptIndex" value="<%=deptIndex%>" />
<input id="sectionIndex" type="hidden" name="sectionIndex" value="<%=sectionIndex%>" />
<input id="status" type="hidden" name="status" value="0" />
<input id="objectType" type="hidden" name="objectType" />
<input id="object" type="hidden" name="object" />
<input id="type" type="hidden" name="type" value='FOLLOW'/>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>����׷�ٲ�ѯ����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
	        <td width="15%" bgcolor="#ACD5C9" class="en11pxb" height="20">����׷������</td>
	    	<td width="35%"  class="en11px" colspan="3"><input type="text" name="followName" class="input" size="50"></td>
        </tr>
        <tr bgcolor="#DFE1EC">
	    	<td bgcolor="#ACD5C9" class="en11pxb" height="20">Ŀ��</td>
	    	<td class="en11pxb" colspan="3"><input type="text" name="purpose" class="input" size="50"></td>
        </tr>

        <tr bgcolor="#DFE1EC">
	        <td width="15%" bgcolor="#ACD5C9" class="en11pxb" height="20">����</td>
	    	<td width="35%"  class="en11px"><%=dept%></td>
	    	<td width="15%" bgcolor="#ACD5C9" class="en11pxb">�α�</td>
	    	<td width="35%" class="en11px"><%=section%></td>
        </tr>

        <tr bgcolor="#DFE1EC">
          <td bgcolor="#ACD5C9" class="en11pxb" ><input type="radio"  id="type1" name="type1" checked value="0" onclick="selectStyle();">�豸����&nbsp;
          <br><input type="radio"  id="type2" name="type1" checked value="0" onclick="selectEquipment();">�豸</td>
          <td class="en11pxb" ><div id="equipmentDiv">
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
    		<td bgcolor="#ACD5C9" class="en11pxb">������</td>
	    	<td class="en11px"><%=creator%></td>
        </tr>

        <tr bgcolor="#DFE1EC">
        	<td class="en11pxb" colspan="4" align="left">
	        	<table border="0" cellspacing="0" cellpadding="0">
			  		<tr height="30">
					   	<td width="20">&nbsp;</td>
					    <td><ul class="button">
								<li><a class="button-text" href="#" onclick="javascript:save();"><span>&nbsp;����&nbsp;</span></a></li>
						</ul></td>
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