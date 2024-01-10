<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>

<%
	List eqpFdcRelationList = (List)request.getAttribute("eqpFdcRelationList");
	List eqIdList=(List)request.getAttribute("eqIdList");
	String model = (String)request.getAttribute("model");
	String deptIndex = (String)request.getAttribute("deptIndex");	
 	String section=(String)request.getAttribute("sectionIndex");
	String eqId=(String)request.getAttribute("eqId"); 
 %>
 

<!-- yui page script-->
<script language="javascript">
	
	function eqpFdcRelationQuery(){
		debugger;		
		loading();
		document.eqpFdcRelationQueryForm.submit();
	}	

	function sectionOrModelChange(){
		debugger;
		var deptIndex=<%=deptIndex%>;
		var section=Ext.get('section').dom.value;
		var model=Ext.get('model').dom.value.replace("+", "aaa");
		var actionURL='<ofbiz:url>/getEqpList</ofbiz:url>?deptIndex='+deptIndex+'&section='+section+'&model='+model;
		Ext.lib.Ajax.formRequest('eqpFdcRelationQueryForm',actionURL,{success: commentSuccess_1, failure: commentFailure});
	}
	
	//远程调用成功
	function commentSuccess_1(o){
		 var result = eval('(' + o.responseText + ')');
			var eqId=document.getElementById("eqId");
			var eqpArray=result.eqpArray;
			var eqIdSize=result.eqpArray.length;
			eqId.length=1;
			for(var i=0;i<eqIdSize;i++){
				eqId.options[eqId.length]=new Option(eqpArray[i],eqpArray[i]);
			}
		 var eqIdobj=document.getElementById('eqId');
		 eqIdobj.value='<%=eqId%>'		 	
	}		
	
	//远程调用失败
   var commentFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
   }; 
   
	function eEqIdChange(){
		var eEqId=Ext.get('aEqId').dom.value;
		var actionURL='<ofbiz:url>/getEqpFdcInfo</ofbiz:url>?eqId='+eEqId
		Ext.lib.Ajax.formRequest('eqpFdcRelationQueryForm',actionURL,{success: commentSuccess_2, failure: commentFailure});
	}
	function commentSuccess_2(o){
		var result = eval('(' + o.responseText + ')');
		var chamberArray=result.chamberArray;
		var paranameArray=result.paranameArray;
		var eqpArray=result.eqpArray;
		
		var chamber=Ext.get('aChamber').dom;
		var paraname=Ext.get('eParaname').dom;
		var equipmentId=Ext.get('aEquipmentId').dom;
		
		chamber.length=1;
		for(var i=0;i<chamberArray.length;i++){
			chamber.options[chamber.length]=new Option(chamberArray[i],chamberArray[i]);
		}
		
		paraname.length=1;
		//for(var i=0;i<chamberArray.length;i++){
		//	paraname.options[paraname.length]=new Option(paranameArray[i],paranameArray[i]);
		//}

		equipmentId.length=1;
		for(var i=0;i<eqpArray.length;i++){
			equipmentId.options[equipmentId.length]=new Option(eqpArray[i],eqpArray[i]);
		}
	}
	
	function getParaname(){
		var eEqId=Ext.get('aEqId').dom.value;
		var eChamber=Ext.get('aChamber').dom.value;
		var actionURL='<ofbiz:url>/getParanameByEqIdAndCh</ofbiz:url>?eqId='+eEqId+'&chamber='+eChamber;
		Ext.lib.Ajax.formRequest('eqpFdcRelationUpdateForm',actionURL,{success: commentSuccess_3, failure: commentFailure});
	}	
	

	
   //初始化页面控件
	Ext.onReady(function(){
	    var sectionType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'section',
	        width:150,
	        forceSelection:true
	    });
	    sectionType.on('select',sectionOrModelChange);
	    
	    var modelType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'model',
	        width:150,
	        forceSelection:true
	    });
	    modelType.on('select',sectionOrModelChange);

	 });
	    

	function commentSuccess(){
	}

	

</script>
<form action="<%=request.getContextPath()%>/control/queryEqpFdcRelationReport" method="post" id="eqpFdcRelationQueryForm" name="eqpFdcRelationQueryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>关键备件查询参数</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<input type="hidden" id="deptIndex" name="deptIndex" value="<%=deptIndex%>" />
	     <tr bgcolor="#DFE1EC"> 
          <td width="12%" class="en11pxb">课别:</td>
	      <td width="28%"><select id="section" name="section" >
	          <option value=""></option>
	         <ofbiz:if name="sectionList">
		       <ofbiz:iterator name="EquipmentSection" property="sectionList">
			    <option value='<ofbiz:inputvalue entityAttr="EquipmentSection" field="sectionIndex"/>'><ofbiz:inputvalue entityAttr="EquipmentSection" field="section"/></option>
		      </ofbiz:iterator>
	      	</ofbiz:if>	          
    		</select></td>
    	  <td width="12%" class="en11pxb">eqpModel:</td>
		    <td>          		
         	<select id="model" name="model" >
          		<option value=''></option>
	         <ofbiz:if name="equipMoelList">
		       <ofbiz:iterator name="EquipmentModel" property="equipMoelList">
			    <option value='<ofbiz:inputvalue entityAttr="EquipmentModel" field="model"/>'><ofbiz:inputvalue entityAttr="EquipmentModel" field="model"/></option>
		      </ofbiz:iterator>
	      	</ofbiz:if>
    		</select>          		
          </td>
	    </tr>
	    </tr>
	    <tr bgcolor="#DFE1EC"> 	        
	    	<td width="12%" class="en11pxb">EqpId:</td>
	    	<td width="28%"><select id="eqId" name="eqId" style="width:150" >
	          <option value=""></option>
    		</select></td>
    		
    		<td width="12%" class="en11pxb"></td>
	    	<td width="28%"></td>
	    </tr>
	    	 
	    <tr bgcolor="#DFE1EC">
	   		<td width="20%" class="en11pxb" align="left" colspan="4">
		    	<table border="0" cellspacing="0" cellpadding="0">
				  <tr height="30" >
				   	<td width="0">&nbsp;</td>
				    <td><ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:eqpFdcRelationQuery();"><span>&nbsp;查询&nbsp;</span></a></li> 
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
<div id="queryKeypartsEqpFdcRelation" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="queryKeypartsEqpFdcRelation" style="visibility:'hidden';">
</ofbiz:unless>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>关键备件使用记录列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td class="en11pxb">EQID</td>
          <td class="en11pxb">CHAMBER</td>
          <td class="en11pxb">EQUIPMENTID</td> 
          <td class="en11pxb">寿命类型</td>
          <td class="en11pxb">FDC参数名</td>
          <td class="en11pxb">是否启用</td>
          <td class="en11pxb">updateUser</td>
          <td class="en11pxb">updateTime</td>
        </tr>
       <% if(eqpFdcRelationList != null && eqpFdcRelationList.size() > 0) {  
       		for(Iterator it = eqpFdcRelationList.iterator();it.hasNext();) { 
					Map map = (Map)it.next();
	   %>
	   	<tr bgcolor="#DFE1EC">
	   	  <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("EQID"))%></td>
		  <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("CHAMBER"))%></td>
		  <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("EQUIPMENT_ID"))%></td>
		  <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("LIFE_TYPE"))%></td>		          
		  <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("PARANAME"))%></td>
		  <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("ENABLE"))%></td>
		  <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("UPDATE_USER"))%></td>
		  <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("UPDATE_TIME"))%></td>
		 </tr>
	  <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>


<script language="javascript"> 	
		var sectionobj=document.getElementById('section');
		sectionobj.value='<%=section%>'
		sectionOrModelChange();
		var modelobj=document.getElementById('model');
		modelobj.value='<%=model%>'
		sectionOrModelChange();
</script>

