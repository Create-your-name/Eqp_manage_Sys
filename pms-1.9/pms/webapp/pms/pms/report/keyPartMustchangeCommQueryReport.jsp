<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>

<%
     
	List musgchangeCommList = (List)request.getAttribute("musgchangeCommList");
	
	String keydesc = (String)request.getAttribute("keydesc");
	String eqpmodel = (String)request.getAttribute("eqpmodel");
	String equId = request.getParameter("equipmentId");
	String eqpDept = (String)request.getAttribute("eqpDept");
	String deptIndex = (String)request.getAttribute("deptIndex");	
 	String section=(String)request.getAttribute("section");
	String equipmentId=(String)request.getAttribute("equipmentId");
	String reason=(String)request.getAttribute("reason");
	     	
	String sDate = UtilFormatOut.checkNull(request.getParameter("startDate"));
	String eDate = UtilFormatOut.checkNull(request.getParameter("endDate"));
	String flag = (String)request.getAttribute("flag");
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	Calendar cDay = Calendar.getInstance();
	 String  startDate = formatter.format(cDay.getTime());
	 
	
 %>

<!-- yui page script-->
<script language="javascript">

	 
	function partsUserQuery(){
		debugger;
		var starttime=Ext.get("startDate").dom.value;
		var endtime=Ext.get("endDate").dom.value;
		if(starttime==""){
			Ext.MessageBox.alert('����', '��ʼ���ڲ���Ϊ�գ�');
			return;
		}
		if(endtime==""){
			Ext.MessageBox.alert('����', '�������ڲ���Ϊ�գ�');
			return;
		}
		loading();
		document.keypartsMustchangeCommQueryForm.submit();
	}
	
	
	//�豸modelѡ�� replace("+", "aaa")
	function equipModelChange(){
		debugger;
		var newEqpValue=document.getElementById('eqp_Model').value.replace("+", "aaa");
		var actionURL='<ofbiz:url>/getEqpIdByModel</ofbiz:url>?equipmodel='+newEqpValue;
		Ext.lib.Ajax.formRequest('keypartsMustchangeCommQueryForm',actionURL,{success: commentSuccess, failure: commentFailure});
	}
	
	//Զ�̵��óɹ�
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			   debugger;
				//Ext.get('keyUseId').dom.value=result.keyUseId;
				//Ext.get('eqpId').dom.value=result.eqpId;
			}
           //�豸ID���ݳ�ʼ��
			var equipId=document.getElementById("equipmentId");
			var eqpArray=result.eqpIdArray;
			var eqpIdSize=result.eqpIdArray.length;
			equipId.length=1;
			for(var i=0;i<eqpIdSize;i++){
				equipId.options[equipId.length]=new Option(eqpArray[i],eqpArray[i]);
			}
			var keydesc=document.getElementById("keydesc");
			var keydescArray=result.keydescArray;
			var keydescSize=result.keydescArray.length;
			keydesc.length=1;
			for(var i=0;i<eqpIdSize;i++){
				keydesc.options[keydesc.length]=new Option(keydescArray[i],keydescArray[i]);
			}
		var obj=document.getElementById('keydesc');
		obj.value='<%=keydesc%>' 
		var obj=document.getElementById('equipmentId');
		obj.value='<%=equipmentId%>' 
		 
	}
	
	
	 
	
  
	
   //Զ�̵���ʧ��
   var commentFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
   };
	
	 
 
      
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
	    
	    var deptType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'eqp_Dept',
	        width:170,
	        forceSelection:true
	    });
	    deptType.on('select',deptChange);
	    
	 });
	 function deptChange(){
		debugger;
		var newDeptValue=Ext.get('eqp_Dept').dom.value;
		//alert(newDeptValue);
		var actionURL='<ofbiz:url>/getSectionAndModelByDeptIndex</ofbiz:url>?deptIndex='+newDeptValue;
		Ext.lib.Ajax.formRequest('keypartsMustchangeCommQueryForm',actionURL,{success: commentSuccess_d, failure: commentFailure});
	 }
	function commentSuccess_d(o){
		 var result = eval('(' + o.responseText + ')');
			var deptSection=document.getElementById("deptSection");
			var sectionArray=result.sectionArray;
			var sectionSize=result.sectionArray.length;
			deptSection.length=1;
			for(var i=0;i<sectionSize;i++){
				deptSection.options[deptSection.length]=new Option(sectionArray[i],sectionArray[i]);
			}
			var eqpModel=document.getElementById("eqp_Model");
			var modelArray=result.modelArray;
			var modelSize=result.modelArray.length;
			eqpModel.options.length=1;
			for(var i=0;i<modelSize;i++){
				eqpModel.options[eqpModel.length]=new Option(modelArray[i],modelArray[i]);
			} 
		var sectionobj=document.getElementById('deptSection');
		sectionobj.value='<%=section%>'
		var obj=document.getElementById('eqp_Model');
		obj.value='<%=eqpmodel%>'
		equipModelChange();
		 
	}
	
	 
</script>
<form action="<%=request.getContextPath()%>/control/queryKeyPartsMustchangeComm" method="post" id="keypartsMustchangeCommQueryForm" name="keypartsMustchangeCommQueryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�ؼ�������ѯ����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2"> 
	     <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb">����:</td>
	       <td width="28%"><input type="text" ID="startDate" NAME="startDate" value="<%=sDate %>" readonly></td>
	    <td width="12%" class="en11pxb">��:</td>
	    <td width="28%"><input type="text" ID="endDate" NAME="endDate"  value="<%=eDate %>" readonly></td>
	    </tr>
	     <tr bgcolor="#DFE1EC"> 
	        <td width="12%" class="en11pxb">����:</td>
		    <td>          		
         		<select id="eqp_Dept" name="eqp_Dept">
          			<option value=''></option>
	          		<ofbiz:if name="equipDeptList">
		        		<ofbiz:iterator name="EquipmentDept" property="equipDeptList">
			    			<option value='<ofbiz:inputvalue entityAttr="EquipmentDept" field="deptIndex"/>'><ofbiz:inputvalue entityAttr="EquipmentDept" field="equipmentDept"/></option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
    			</select>          		
          </td>
          <td width="12%" class="en11pxb">�α�:</td>
	      <td width="28%"><select id="deptSection" name="deptSection">
	          <option value=""></option>
    		</select></td>
	    </tr>
	    </tr>
	     <tr bgcolor="#DFE1EC"> 
	        <td width="12%" class="en11pxb">eqpModel:</td>
		    <td>          		
         		<select id="eqp_Model" name="eqp_Model" onchange="equipModelChange()">
          			<option value=''></option>
    			</select>          		
          </td>
	    <td width="12%" class="en11pxb">EqpId:</td>
	    <td width="28%"><select id="equipmentId" name="equipmentId">
	          <option value=""></option>
    		</select></td>
	    </tr>
	    <tr bgcolor="#DFE1EC"> 
		    <td width="12%" class="en11pxb">�ؼ���:</td>
		    <td width="28%" ><select id="keydesc" name="keydesc">
	          <option value=""></option>
    		</select></td>
		      <td width="12%" class="en11pxb">δ��ԭ��</td>
	       <td width="28%"><select id="reason" name="reason">
	          <option value=""></option>
	          <option value="��Ԥ�㿪PR">��Ԥ�㿪PR</option>
	          <option value="תPOʱ�䳤">תPOʱ�䳤</option>
	          <option value="�򽻻��ڳ�δ����">�򽻻��ڳ�δ����</option>
	          <option value="����">����</option>
    		</select></td>
		   </tr>
	    <tr bgcolor="#DFE1EC">
	   		<td width="20%" class="en11pxb" align="left" colspan="4">
		    	<table border="0" cellspacing="0" cellpadding="0">
				  <tr height="30" >
				   	<td width="0">&nbsp;</td>
				    <td><ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:partsUserQuery();"><span>&nbsp;��ѯ&nbsp;</span></a></li> 
					</ul><ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:reset();"><span>&nbsp;����&nbsp;</span></a></li> 
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

<ofbiz:if name="flag" value="OK">
<div id="queryKeyPartsUseByCondition" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="queryKeyPartsUseByCondition" style="visibility:'hidden';">
</ofbiz:unless>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�ؼ�����ʹ�ü�¼�б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="8%" class="en11pxb">����</td>
          <td width="8%" class="en11pxb">�α�</td>
          <td width="8%" class="en11pxb">MODEL</td>
          <td width="8%" class="en11pxb">EqpID</td>
          <td width="8%" class="en11pxb">�Ϻ�</td>
          <td width="10%" class="en11pxb">��������</td> 
          <td width="10%" class="en11pxb">�ؼ���</td>
          <td width="10%" class="en11pxb">ԭ��</td>
          <td width="10%" class="en11pxb">��ע</td>
          <td width="10%" class="en11pxb">������</td>
          <td width="10%" class="en11pxb">����ʱ��</td>
        </tr>
       <% if(musgchangeCommList != null && musgchangeCommList.size() > 0) {  
       		for(Iterator it = musgchangeCommList.iterator();it.hasNext();) { 
				Map map = (Map)it.next();
		%>
			<tr bgcolor= "#DFE1EC">  				  			 
		          <td class="en11px"><%=map.get("MAINT_DEPT")%></td>
		          <td class="en11px"><%=map.get("SECTION")%></td>
		          <td class="en11px"><%=map.get("MODEL")%></td>
		          <td class="en11px"><%=map.get("EQP_ID")%></td>
		          <td class="en11px"><%=map.get("PARTS_ID")%></td>
		          <td class="en11px"><%=map.get("PARTS_NAME")%></td>
		          <td class="en11px"><%=map.get("KEYDESC")%></td>
		          <td class="en11px"><%=map.get("REASON")%></td>
		          <td class="en11px"><%=map.get("REMARK")%></td>
		          <td class="en11px"><%=map.get("UPDATE_USER")%></td>
		          <td class="en11px"><%=map.get("UPDATE_TIME")%></td>
		        </tr>
	  <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>


<script language="javascript"> 	
		var deptobj=document.getElementById('eqp_Dept');
		deptobj.value='<%=deptIndex%>'
		deptChange();
   		var sDateObj=document.getElementById('startDate');
   		sDateObj.value='<%=sDate%>';
   		var eDateObj=document.getElementById('endDate');
   		eDateObj.value='<%=eDate%>';
   		var obj=document.getElementById('equipmentId');
		obj.value='<%=equipmentId%>'
		var reasonObj=document.getElementById('reason');
		reasonObj.value='<%=reason%>'
</script>