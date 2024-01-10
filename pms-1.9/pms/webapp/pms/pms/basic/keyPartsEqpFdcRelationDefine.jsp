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
 
<!--include yui css-->
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">

	extDlg.dlgInit('500','550');
	
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
	
	function getEditParaname(eqid,chamber,paraname){
		var actionURL='<ofbiz:url>/getParanameByEqIdAndCh</ofbiz:url>?eqId='+eqid+'&chamber='+chamber+'&paraname='+paraname;
		Ext.lib.Ajax.formRequest('eqpFdcRelationUpdateForm',actionURL,{success: commentSuccess_3, failure: commentFailure});		
	}

	function commentSuccess_3(o){
		var result = eval('(' + o.responseText + ')');
		var paraname=result.paraname;
		var paranameArray=result.paranameArray;
		var paranameobj=Ext.get('eParaname').dom;
		paranameobj.length=1;
		for(var i=0;i<paranameArray.length;i++){
			paranameobj.options[paranameobj.length]=new Option(paranameArray[i],paranameArray[i]);
		}
		Ext.get('eParaname').dom.value=paraname;
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

	    var eEqIdType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'aEqId',
	        width:180,
	        forceSelection:true
	    });
	    eEqIdType.on('select',eEqIdChange);	    
	 });
	    
	
	
	function addRelation(obj){
		Ext.get('formType').dom.value="ADD";
		Ext.get('aeqdiv').dom.style.display="";
		Ext.get('achdiv').dom.style.display="";
		Ext.get('aeqpdiv').dom.style.display="";
		Ext.get('alifetypediv').dom.style.display="";
		Ext.get('eeqdiv').dom.style.display="none";
		Ext.get('echdiv').dom.style.display="none";
		Ext.get('eeqpdiv').dom.style.display="none";
		Ext.get('elifetypediv').dom.style.display="none";
		Ext.get('eParaname').dom.length=1;
		Ext.get('eParaname').dom.value="";
		Ext.get('aLifeType').dom.value="";
		Ext.get('enable').dom.value="Y";
		var url='<%=request.getContextPath()%>/control/';
		eqpFdcRelationUpdateForm.action='<%=request.getContextPath()%>/control/insertEqpFdcRelation';
		extDlg.showEditDialog(obj,url);
	}
	function editEqpFdcRelation(obj,eqpfdcrelationid,eqid,chamber,eqpuid,lifetype,paraname,enable){
		Ext.get('formType').dom.value="EDIT";
		Ext.get('eEqpfdcrelationId').dom.value=eqpfdcrelationid;
		Ext.get('eeqdiv').dom.style.display="";
		Ext.get('echdiv').dom.style.display="";
		Ext.get('eeqpdiv').dom.style.display="";
		Ext.get('elifetypediv').dom.style.display="";
		Ext.get('aeqdiv').dom.style.display="none";
		Ext.get('achdiv').dom.style.display="none";
		Ext.get('aeqpdiv').dom.style.display="none";
		Ext.get('alifetypediv').dom.style.display="none";
		Ext.get('eEqId').dom.value=eqid;
		getEditParaname(eqid,chamber,paraname);
		Ext.get('eChamber').dom.value=chamber;
		Ext.get('eEquipmentId').dom.value=eqpuid;
		Ext.get('eLifeType').dom.value=lifetype;
		Ext.get('enable').dom.value=enable;
		var url='<%=request.getContextPath()%>/control/';
		eqpFdcRelationUpdateForm.action='<%=request.getContextPath()%>/control/updateEqpFdcRelation';
		extDlg.showEditDialog(obj,url);
	}	

	function commentSuccess(){
	}
	function checkForm(){
		var paraname=Ext.get('eParaname').dom.value;
		var formType=Ext.get('formType').dom.value;
		var lifeType;
		var eqid;
		var chamber;
		var equId;
		var reg = /^[+]{0,1}(\d+)$|^[+]{0,1}(\d+\.\d+)$/;//Js验证非负数(0,正整数和正小数)
		if(formType=="ADD"){
			eqid=Ext.get('aEqId').dom.value;
			chamber=Ext.get('aChamber').dom.value;
			equId=Ext.get('aEquipmentId').dom.value;
			lifeType=Ext.get('aLifeType').dom.value;
		}else if(formType=="EDIT"){
			eqid=Ext.get('eEqId').dom.value;
			chamber=Ext.get('eChamber').dom.value;
			equId=Ext.get('eEquipmentId').dom.value;
			lifeType=Ext.get('eLifeType').dom.value;
		}
		if(eqid=="" || chamber=="" || equId=="" || paraname=="" || lifeType==""){
			return "参数必填，不能为空！"
		}else{			
        	return "";
		}

	}
	

</script>
<form action="<%=request.getContextPath()%>/control/queryKeypartsEqpFdcRelation" method="post" id="eqpFdcRelationQueryForm" name="eqpFdcRelationQueryForm">
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
					</ul><ul class="button">
					<li><a class="button-text" href="#" onclick="addRelation(this);"><span>&nbsp;新增&nbsp;</span></a></li> 
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
		  <td class="en11px"><a href="#" onclick="editEqpFdcRelation(this,'<%=map.get("EQPFDCRELATION_ID")%>','<%=map.get("EQID")%>','<%=map.get("CHAMBER")%>','<%=map.get("EQUIPMENT_ID") %>','<%=map.get("LIFE_TYPE") %>','<%=map.get("PARANAME") %>','<%=map.get("ENABLE") %>')" >
		  <%=UtilFormatOut.checkNull((String)map.get("EQUIPMENT_ID"))%></a></td>
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

<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">Eqp-Fdc关系设定</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="Eqp-Fdc关系设定">
            <div class="inner-tab" id="x-form">
                <form name="eqpFdcRelationUpdateForm" action="" method="post" id="eqpFdcRelationUpdateForm" onsubmit="return false;">
                <input type="hidden" id="eEqpfdcrelationId" name="eEqpfdcrelationId" value="" />
                <input type="hidden" id="formType" name="formType" value="" />
	                <table>
	                	<tr><td>
		                	<label for="name"><small>EQID</small></label>
		                	<div id="aeqdiv" style="display:'';"><select id="aEqId" name="aEqId">
		          				<option value=''></option>
			       <% if(eqIdList != null && eqIdList.size() > 0) {
			       		for(Iterator it = eqIdList.iterator();it.hasNext();) {
							Map map = (Map)it.next();  %>    	
				   			<option value='<%=map.get("EQUIPMENT_ID")%>'><%=map.get("EQUIPMENT_ID")%></option> 
	    		   <%   }
	    		   	  } %>  
		    				</select></div>
		    				<div id="eeqdiv" style="display:'none'"><input class="textinput" type="text" name="eEqId" id="eEqId" value="" size="22" tabindex="1" style="background:E9E9E9" readonly="readonly"/></div>	    				
	                	</td></tr>
	                	<tr><td>
		                	<label for="name"><small>CHAMBER</small></label>
			                <div id="achdiv" style="display:'none'"><select id="aChamber" name="aChamber" style="width:180" onchange="getParaname()">
				          		<option value=""></option>
			    			</select></div>
			    			<div id="echdiv" style="display:'none'"><input class="textinput" type="text" name="eChamber" id="eChamber" value="" size="22" tabindex="1" style="background:E9E9E9" readonly="readonly"/></div>
	                	</td></tr>
	                	<tr><td>
		                	<label for="name"><small>EQUIPMENTID</small></label>
			                <div id="aeqpdiv" style="display:'none'"><select id="aEquipmentId" name="aEquipmentId" style="width:180" >
				          		<option value=""></option>
			    			</select></div>
			    			<div id="eeqpdiv" style="display:'none'"><input class="textinput" type="text" name="eEquipmentId" id="eEquipmentId" value="" size="22" tabindex="1" style="background:E9E9E9" readonly="readonly"/></div>
	                	</td></tr>
	                	<tr><td>
	                		<label for="name"><small>寿命类型</small></label>
							<div id="alifetypediv" style="display:'none'"><select id="aLifeType" name="aLifeType" style="width:180" >
				          		<option value=""></option>
			      	        	<option value="TIME(天)">TIME(天)</option>
			      	        	<option value="RFTIME(Hours)">RFTIME(Hours)</option>
			      	        	<option value="WAFERCOUNT">WAFERCOUNT</option>
			    			</select></div>
			    			<div id="elifetypediv" style="display:'none'"><input class="textinput" type="text" name="eLifeType" id="eLifeType" value="" size="22" tabindex="1" style="background:E9E9E9" readonly="readonly"/></div>
	                	</td></tr>
	                	<tr><td>
	                	<label for="name"><small>FDC参数名</small></label>
			                <select id="eParaname" name="eParaname" style="width:180" >
			      	        	<option value=""></option>
			      			</select>
	                	</td></tr>
	                	<tr><td>
		                	<label for="name"><small>是否启用</small></label>
		                	<select id="enable" name="enable" style="width:180" >
			      	        	<option value="Y">Y</option>
			      	        	<option value="N">N</option>
			      			</select>
	                	</td></tr>	                	
	                </table>
                </form>
            </div>
        </div>
    </div>
    <div class="x-dlg-ft">
        <div id="dlg-msg">
            <span id="post-error" class="posting-msg"><img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" />&nbsp;<span id="post-error-msg"></span></span>
    	</div>
    </div>
</div>

<script language="javascript"> 	
		var sectionobj=document.getElementById('section');
		sectionobj.value='<%=section%>'
		sectionOrModelChange();
		var modelobj=document.getElementById('model');
		modelobj.value='<%=model%>'
		sectionOrModelChange();
</script>

