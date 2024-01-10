<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>
<%
     
	List partList = (List)request.getAttribute("PART_LIST");
	String sDate = UtilFormatOut.checkNull(request.getParameter("startDate"));
	String eDate = UtilFormatOut.checkNull(request.getParameter("endDate"));
	String flag = (String)request.getAttribute("flag");
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	Calendar cDay = Calendar.getInstance();
	 String  startDate = formatter.format(cDay.getTime());
	String eqpmodel = (String)request.getAttribute("eqpmodel");
	String eqpDept = (String)request.getAttribute("eqpDept");
	String deptIndex = (String)request.getAttribute("deptIndex");	
 	String section=(String)request.getAttribute("section");
	
 %>
<script language='javascript' src='<%=request.getContextPath()%>/images/pupdate.js' type='text/javascript'></script>
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
		document.keypartsDataQueryForm.submit();
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
		var actionURL='<ofbiz:url>/getSectionAndModelByDeptIndex</ofbiz:url>?deptIndex='+newDeptValue;
		Ext.lib.Ajax.formRequest('keypartsDataQueryForm',actionURL,{success: commentSuccess_d, failure: commentFailure});
	 }
	function commentSuccess_d(o){
		 var result = eval('(' + o.responseText + ')');
           //�豸ID���ݳ�ʼ��
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
	function equipModelChange(){
		debugger;
		var newEqpValue=Ext.get('eqp_Model').dom.value.replace("+", "aaa");
		var actionURL='<ofbiz:url>/getEqpIdByModel</ofbiz:url>?equipmodel='+newEqpValue;
		Ext.lib.Ajax.formRequest('keypartsDataQueryForm',actionURL,{success: commentSuccess, failure: commentFailure});
	}
	//Զ�̵��óɹ�
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
	 
           //�豸ID���ݳ�ʼ��
			//var equipId=document.getElementById("equipmentId");
			//var eqpArray=result.eqpIdArray;
			//var eqpIdSize=result.eqpIdArray.length;
			//equipId.length=1;
			//for(var i=0;i<eqpIdSize;i++){
			//	equipId.options[equipId.length]=new Option(eqpArray[i],eqpArray[i]);
			//}
			 
		 
	}
	
	
	//Զ�̵���ʧ��
   var commentFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
   }; 
</script>
<form action="<%=request.getContextPath()%>/control/queryKeyPartsUpdateByCondition" method="post" id="keypartsDataQueryForm" name="keypartsDataQueryForm">
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
         		<select id="eqp_Model" name="eqp_Model">
          			<option value=''></option>
	          		<ofbiz:if name="equipMoelList">
		        		<ofbiz:iterator name="EquipmentModel" property="equipMoelList">
			    			<option value='<ofbiz:inputvalue entityAttr="EquipmentModel" field="model"/>'><ofbiz:inputvalue entityAttr="EquipmentModel" field="model"/></option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
    			</select>          		
          </td>
          <td width="12%" class="en11pxb"></td>
		    <td></td>
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
          <td width="8%" class="en11pxb">�Ϻ�</td>
          <td width="10%" class="en11pxb">��������</td> 
          <td width="8%" class="en11pxb">�ؼ���</td> 
          <td width="8%" class="en11pxb">����</td>
          <td width="8%" class="en11pxb">Model</td>
          <td width="5%" class="en11pxb">ʹ������</td>
          <td width="10%" class="en11pxb">��������</td> 
          <td width="5%" class="en11pxb">AlarmLife</td>
          <td width="5%" class="en11pxb">Warminglife</td>
          <td width="5%" class="en11pxb">�Ƿ�ػ�</td>
          <td width="5%" class="en11pxb">�Ƿ񱨾�</td>
          <td width="5%" class="en11pxb">�Ƿ�����</td>
          <td width="5%" class="en11pxb">���¶���</td>
          <td width="5%" class="en11pxb">������</td>
          <td width="8%" class="en11pxb">����ʱ��</td> 
        </tr>
       <% if(partList != null && partList.size() > 0) {  
       		for(Iterator it = partList.iterator();it.hasNext();) { 
					Map map = (Map)it.next();
			%>
		<tr bgcolor= "#DFE1EC"> 
          <td class="en11px"><%=map.get("PARTS_ID")%></td>
          <td class="en11px"><%=map.get("PARTS_NAME")%></td> 
          <td class="en11px"><%=map.get("KEYDESC")%></td> 
          <td class="en11px"><%=map.get("MAINT_DEPT")%></td>
          <td class="en11px"><%=map.get("EQP_TYPE")%></td>
          <td class="en11px"><%=map.get("USE_NUMBER")%></td>
          <td class="en11px"><%=map.get("LIMIT_TYPE")%></td> 
          <td class="en11px"><%=map.get("ERROR_SPEC")%></td>
          <td class="en11px"><%=map.get("WARN_SPEC")%></td>
          <td class="en11px"><%=map.get("MUSTCHANGE")%></td>
          <td class="en11px"><%=map.get("IS_ALARM")%></td>
          <td class="en11px"><%=map.get("ENABLE")%></td>
          <td class="en11px"><%=map.get("ACTION")%></td>                  
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
</script>