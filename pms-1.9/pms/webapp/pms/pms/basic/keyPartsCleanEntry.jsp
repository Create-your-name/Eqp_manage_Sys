<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.net.URLEncoder" %>
<% 	
	List keyPartsCleanList=(List)request.getAttribute("keyPartsCleanList");
	String model=(String)request.getAttribute("model");
	String keydesc=(String)request.getAttribute("keydesc");
%>
<!--include yui css-->
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">

	extDlg.dlgInit('400','500');

	function keyPartsCleanListQuery(){
		keyPartsCleanForm.submit();
	}
	
	function addKeyPartsClean(obj){
		var actionUrl="<%=request.getContextPath()%>/control/addKeyPartsClean?1=1";
		document.getElementById("keyPartsCleanUpdateForm").action=actionUrl;
		Ext.get('formType').dom.value="ADD";		
		Ext.get('emdiv').dom.style.display="none";
		Ext.get('ekdiv').dom.style.display="none";
		Ext.get('eldiv').dom.style.display="none";
		Ext.get('amdiv').dom.style.display="";
		Ext.get('akdiv').dom.style.display="";
		Ext.get('aldiv').dom.style.display="";
		
		Ext.get('ePartsId').dom.readOnly=false;
		Ext.get('ePartsId').dom.style.background=""; 		
		Ext.get('eSeriesNo').dom.readOnly=false;
		Ext.get('eSeriesNo').dom.style.background=""; 
		Ext.get('ePartsId').dom.value="";
		Ext.get('eSeriesNo').dom.value="";
		Ext.get('eLimitLife').dom.value="";
		
		var url=" ";
        extDlg.showEditDialog(obj,url);
	}
	
	function delKeyCleanParts(obj,keyPartsCleanId){
		var modelValue=Ext.get('model').dom.value;
		var keydescValue=Ext.get('keydesc').dom.value;
		Ext.MessageBox.confirm('删除确认', '您确信要删除此记录吗？',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/delKeyCleanParts</ofbiz:url>?keyPartsCleanId='+keyPartsCleanId+'&model='+modelValue+'&keydesc='+keydescValue;
				document.location=url;
			}else{
				return;
			}
        });	
	}
	
	function commentSuccess(o){
	}
	
	var commentFailure = function(o){
        Ext.MessageBox.alert('Status', 'Unable to connect.');
    };

	function editKeyPartsClean(obj,keyPartsCleanId,keyPartsId,model,partsId,keydesc,seriesNo,lifeType,limitLife,enable){
		var actionUrl="<%=request.getContextPath()%>/control/editKeyPartsClean?keyPartsCleanId="+keyPartsCleanId;
		document.getElementById("keyPartsCleanUpdateForm").action=actionUrl;
		
		Ext.get('formType').dom.value="EDIT";
		Ext.get('eKeyPartsCleanId').dom.value=keyPartsCleanId;
		Ext.get('eKeyPartsId').dom.value=keyPartsId;
		Ext.get('eSeriesNo').dom.value=seriesNo;
		Ext.get('eModel').dom.value=model;
		Ext.get('ePartsId').dom.value=partsId;
		Ext.get('eKeydesc').dom.value=keydesc;
		Ext.get('elifeType').dom.value=lifeType;
		Ext.get('elimitLife').dom.value=limitLife;
		Ext.get('eEnable').dom.value=enable;
		
		Ext.get('eSeriesNo').dom.readOnly=true;
		Ext.get('eSeriesNo').dom.style.background="#E9E9E9"; 
		Ext.get('ePartsId').dom.readOnly=true;
		Ext.get('ePartsId').dom.style.background="#E9E9E9"; 		
		Ext.get('amdiv').dom.style.display="none";
		Ext.get('akdiv').dom.style.display="none";
		Ext.get('aldiv').dom.style.display="none";
		Ext.get('emdiv').dom.style.display="";
		Ext.get('ekdiv').dom.style.display="";
		Ext.get('eldiv').dom.style.display="";
		var url=" ";
        extDlg.showEditDialog(obj,url);	
	}
   
    function checkForm(){
    	var formType=Ext.get('formType').dom.value;
    	var model;
    	var keydesc;
    	var lifeType;
	    var limitLife=Ext.get('eLimitLife').dom.value;
		var seriesNo=Ext.get('eSeriesNo').dom.value;
		var remark=Ext.get('eRemark').dom.value;
	    var	partsId=Ext.get('ePartsId').dom.value;
	    var reg = /^[+]{0,1}(\d+)$/;
    	if(formType=="ADD"){
    		model=Ext.get('aModel').dom.value;
		    keydesc=Ext.get('aKeydesc').dom.value;
		    lifeType=Ext.get('aLifeType').dom.value;		    
    	}else if(formType=="EDIT"){
	    	model=Ext.get('eModel').dom.value;
		    keydesc=Ext.get('eKeydesc').dom.value;
		    lifeType=Ext.get('eLifeType').dom.value;
		}
	    if(model=="" || partsId=="" || keydesc=="" || seriesNo=="" || lifeType=="" || limitLife==""){
	    	return "栏位不能为空";
	    }
	    if(formType=="EDIT"&&remark==""){
	    	return "修改必须填写备注";
	    }
	    if(reg.test(limitLife)==false){
	    	return "寿命定义必须为大于0的整数";
	    }
	    var actionUrl=document.getElementById("keyPartsCleanUpdateForm").action;
	    document.getElementById("keyPartsCleanUpdateForm").action=actionUrl+"&model="+model+"&partsId="+partsId+"&keydesc="+keydesc;
     	return "";
    }

	function modelChange(){
		debugger;
		var modelValue=Ext.get('model').dom.value;
		var actionURL='<ofbiz:url>/getKeydescByModel</ofbiz:url>?model='+modelValue;
		Ext.lib.Ajax.formRequest('keyPartsCleanForm',actionURL,{success: commentSuccess_m, failure: commentFailure});
	 }
	function commentSuccess_m(o){
		 var result = eval('(' + o.responseText + ')');
			var keydescObj=document.getElementById("keydesc");
			var keydescArray=result.keydescArray;
			var keydescObjSize=result.keydescArray.length;
			keydescObj.length=1;
			for(var i=0;i<keydescObjSize;i++){
				keydescObj.options[keydescObj.length]=new Option(keydescArray[i],keydescArray[i]);
			} 
			keydescObj.value='<%=keydesc%>';		 
	}

	function aModelChange(){
		debugger;
		var modelValue=Ext.get('aModel').dom.value;
		var actionURL='<ofbiz:url>/getKeydescByModel</ofbiz:url>?model='+modelValue;
		Ext.lib.Ajax.formRequest('keyPartsCleanUpdateForm',actionURL,{success: commentSuccess_em, failure: commentFailure});
	 }
	function commentSuccess_em(o){
		 var result = eval('(' + o.responseText + ')');
			var keydescObj=document.getElementById("aKeydesc");
			var keydescArray=result.keydescArray;
			var keydescObjSize=result.keydescArray.length;
			keydescObj.length=1;
			for(var i=0;i<keydescObjSize;i++){
				keydescObj.options[keydescObj.length]=new Option(keydescArray[i],keydescArray[i]);
			} 
	}
	
	function aKeydescChange(){
		debugger;
		var modelValue=Ext.get('aModel').dom.value;
		var keydescValue=Ext.get('aKeydesc').dom.value;
		var actionURL='<ofbiz:url>/getPartsIdByKeydesc</ofbiz:url>?keydesc='+keydescValue+'&model='+modelValue;
		actionURL = encodeURI(actionURL);
		Ext.lib.Ajax.formRequest('keyPartsCleanUpdateForm',actionURL,{success: commentSuccess_ek, failure: commentFailure});
	}
	function commentSuccess_ek(o){
		 var result = eval('(' + o.responseText + ')');
			var partsIdObj=document.getElementById("ePartsId");
			var partsId=result.partsId;			
			partsIdObj.value=partsId;
	}
	
	Ext.onReady(function(){
	    var model = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'model',
	        width:150,
	        forceSelection:true
	    });
	    model.on('select',modelChange);
	    
	    var aModel = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'aModel',
	        width:180,
	        forceSelection:true
	    });
	    aModel.on('select',aModelChange);	    	    

	});	
</script>
<form id="keyPartsCleanForm" name="keyPartsCleanForm" method="POST" action="<%=request.getContextPath()%>/control/keyPartsCleanList">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td><fieldset><legend>关键备件清洗件查询</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC" height="30"> 
          <td class="en11pxb" width="12%">设备类型</td>
          <td class="en11pxb" width="28%">
    		<select id="model" name="model">
          		<option value=''></option>
	          		<ofbiz:if name="modelList">
		        		<ofbiz:iterator name="EquipmentModel" property="modelList">
			    			<option value='<ofbiz:inputvalue entityAttr="EquipmentModel" field="model"/>'><ofbiz:inputvalue entityAttr="EquipmentModel" field="model"/></option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>          		
    		</select>
          </td>
          <td class="en11pxb" width="12%">关键字</td>
          <td class="en11pxb" width="28%">
          	<select id="keydesc" name="keydesc" style="width:150px">
          		<option value=''></option>
          	</select>          
          </td>
        </tr>
      </table>
	 </fieldset> 
	</td>
  <tr> 
</table>
<table border="0" cellspacing="0" cellpadding="0">
	<tr height="30">
	 	<td><ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:keyPartsCleanListQuery();"><span>&nbsp;查询&nbsp;</span></a></li>
			</ul>
			<ul class="button">
	            <li><a class="button-text" id="addForm" href="#" onclick="addKeyPartsClean();"><span>&nbsp; 新增&nbsp;</span></a></li> 
	    	</ul>
		</td>
	</tr>
</table>

<ofbiz:if name="flag" value="OK">
<div id="keyEqpPartsList" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="keyEqpPartsList" style="visibility:'hidden';">
</ofbiz:unless>


	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
	  <td><fieldset>
	      <legend>关键备件清洗件信息</legend>
	      <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#ACD5C9">
	          <td width="4%" class="en11pxb"></td>  
	          <td width="10%" class="en11pxb">物料号</td>      
	          <td width="12%" class="en11pxb">物料名</td>
	          <td width="12%" class="en11pxb">关键字</td>
	          <td width="10%" class="en11pxb">SeriesNo</td>
	          <td width="10%" class="en11pxb">寿命类型</td>
	          <td width="10%" class="en11pxb">寿命设定</td>
	          <td width="4%" class="en11pxb">是否启用</td>
	          <td width="12%" class="en11pxb">备注说明</td>
	          <td width="8%" class="en11pxb">操作人员</td>
	          <td width="8%" class="en11pxb">更新时间</td>
	        </tr>
	    <%
	    	if(keyPartsCleanList!=null&&keyPartsCleanList.size()>0){
	    		for(int i=0;i<keyPartsCleanList.size();i++){
	    			Map map=(Map)keyPartsCleanList.get(i);
	    			String inuse=(String)map.get("INUSE");
	    %>
	       	<tr bgcolor="#DFE1EC">
	       	  <td width="4%" class="en11pxb" align="center">
	    <%	  if(inuse.equals("N")){ %>   	  
	       	  <img  src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delKeyCleanParts(this,'<%=map.get("KEY_PARTS_CLEAN_ID")%>');"/>
	    <% 	  } %>   	  
	       	  </td>  
	          <td width="10%" class="en11px"><%=map.get("PARTS_ID")%></td>      
	          <td width="12%" class="en11px"><%=map.get("PARTS_NAME")%></td>
	          <td width="12%" class="en11px"><%=map.get("KEYDESC")%></td>
	          <td width="10%" class="en11px"><a href="#" onclick="editKeyPartsClean(this,'<%=map.get("KEY_PARTS_CLEAN_ID")%>','<%=map.get("KEY_PARTS_ID")%>','<%=map.get("EQP_TYPE")%>','<%=map.get("PARTS_ID")%>','<%=map.get("KEYDESC")%>','<%=map.get("SERIES_NO")%>','<%=map.get("LIFE_TYPE")%>','<%=map.get("LIMIT_LIFE")%>','<%=map.get("ENABLE")%>')"><%=map.get("SERIES_NO")%></a></td>
	          <td width="10%" class="en11px"><%=map.get("LIFE_TYPE")%></td>
	          <td width="10%" class="en11px"><%=map.get("LIMIT_LIFE")%></td>
	          <td width="4%" class="en11px" align="center"><%=map.get("ENABLE")%></td>
	          <td width="12%" class="en11px"><%=UtilFormatOut.checkNull((String)map.get("REMARK"))%></td>
	          <td width="8%" class="en11px"><%=map.get("UPDATE_USER")%></td>
	          <td width="8%" class="en11px"><%=map.get("UPDATE_TIME")%></td>
			</tr>
	    <%		}
	    	}
	    %>     
	      </table>
	      </fieldset></td>
	  </tr>
	</table>
</form>
</div>

<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">关键备件清洗件维护</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="关键备件清洗件维护">
            <div class="inner-tab" id="x-form">
                <form name="keyPartsCleanUpdateForm" action="" method="post" id="keyPartsCleanUpdateForm" onsubmit="return false;">
                <input type="hidden" id="eKeyPartsCleanId" name="eKeyPartsCleanId" value="" />
                <input type="hidden" id="eKeyPartsId" name="eKeyPartsId" value="" />
                <input type="hidden" id="formType" name="formType" value="" />
	                <table>
	                	<tr><td>
		                	<label for="name"><small>设备MODEL</small></label>
		                	<div id="amdiv" style="display:''"><select id="aModel" name="aModel">
		          				<option value=''></option>
		          				<ofbiz:if name="modelList">
		        					<ofbiz:iterator name="EquipmentModel" property="modelList">
			    						<option value='<ofbiz:inputvalue entityAttr="EquipmentModel" field="model"/>'><ofbiz:inputvalue entityAttr="EquipmentModel" field="model"/></option>
		      						</ofbiz:iterator>
	      						</ofbiz:if>   
		    				</select></div>
		                	<div id="emdiv" style="display:'none'"><input class="textinput" type="text" id="eModel" name="eModel" value="" style="background:E9E9E9;width:180" readonly /></div>
	                	</td></tr>
	                	<tr><td>
		                	<label for="name"><small>关键字</small></label>
			                <div id="akdiv" style="display:'none'"><select id="aKeydesc" name="aKeydesc" style="width:180" onchange="aKeydescChange()" >
				          		<option value=""></option>
			    			</select></div>
			    			<div id="ekdiv" style="display:'none'"><input class="textinput" id="eKeydesc" name="eKeydesc" value="" style="background:E9E9E9;width:180" readonly /></div>
			    			</td></tr>
	                	<tr><td>
		                	<label for="name"><small>物料号</small></label>
			    			<input class="textinput" id="ePartsId" name="ePartsId" value="" style="background:E9E9E9;width:180" readonly />
	                	</td></tr>
	                	<tr><td>
		                	<label for="name"><small>SeriesNo</small></label>
			                <input class="textinput" id="eSeriesNo" name="eSeriesNo" style="width:180" />
			    			</td></tr>			    			
	                	<tr><td>
	                		<label for="name"><small>寿命类型</small></label>
							<div id="aldiv" style="display:'none'"><select id="aLifeType" name="aLifeType" style="width:180" >
				          		<option value=""></option>
			      	        	<option value="TIME(天)">TIME(天)</option>
			      	        	<option value="TIMES(次)">TIMES(次)</option>
			    			</select></div>
			    			<div id="eldiv" style="display:'none'"><input class="textinput" id="eLifeType" name="eLifeType" value="" style="background:E9E9E9;width:180" readonly /></div>
			    			</td></tr>
	                	<tr><td>
	                	<label for="name"><small>寿命设定</small></label>
			                <input class="textinput" id="eLimitLife" name="eLimitLife" style="width:180" />
	                	</td></tr>	 
	                	<tr><td>
	                	<label for="name"><small>备注说明</small></label>
			                <input class="textinput" id="eRemark" name="eRemark" style="width:180" />
	                	</td></tr>
	                	<tr><td>
	                	<label for="name"><small>是否启用</small></label>
			                <select id="eEnable" name="eEnable" style="width:180" >
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
	var modelObj=document.getElementById("model");
	modelObj.value='<%=model%>'
	modelChange();
</script>

