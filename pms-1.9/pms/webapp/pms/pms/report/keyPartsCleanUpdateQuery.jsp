<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.net.URLEncoder" %>
<% 	
	List keyPartsCleanUpdateList=(List)request.getAttribute("keyPartsCleanUpdateList");
	String deptIndex = (String)request.getAttribute("deptIndex");
	String model=(String)request.getAttribute("model");
	String keydesc=(String)request.getAttribute("keydesc");
	String seriesNo=(String)request.getAttribute("seriesNo");
%>
<script language="javascript">

	function keyPartsCleanUpdateQuery(){
		var deptValue=Ext.get('dept').dom.value;
		var modelValue=Ext.get('model').dom.value;
		var keydescValue=Ext.get('keydesc').dom.value;
		if(deptValue==""||modelValue==""||keydescValue==""){
			alert("部门、设备Model和关键字必填");
			return ;
		}
		keyPartsCleanUpdateForm.submit();
	}	

	 function deptChange(){
		debugger;
		var deptValue=Ext.get('dept').dom.value;
		var actionURL='<ofbiz:url>/getSectionAndModelByDeptIndex</ofbiz:url>?deptIndex='+deptValue;
		Ext.lib.Ajax.formRequest('keyPartsCleanUpdateForm',actionURL,{success: commentSuccess_d, failure: commentFailure});
	 }
	function commentSuccess_d(o){
		 var result = eval('(' + o.responseText + ')');
			var modelObj=document.getElementById("model");
			var modelArray=result.modelArray;
			var modelSize=result.modelArray.length;
			modelObj.options.length=1;
			for(var i=0;i<modelSize;i++){
				modelObj.options[modelObj.length]=new Option(modelArray[i],modelArray[i]);
			}
			modelObj.value='<%=model%>'
			modelChange();
	}

	function modelChange(){
		debugger;
		var modelValue=Ext.get('model').dom.value;
		var actionURL='<ofbiz:url>/getKeydescByModel</ofbiz:url>?model='+modelValue;
		Ext.lib.Ajax.formRequest('keyPartsCleanUpdateForm',actionURL,{success: commentSuccess_m, failure: commentFailure});
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
			keydescChange();
	}
	
	function keydescChange(){
		debugger;
		var modelValue=Ext.get('model').dom.value;
		var keydescValue=Ext.get('keydesc').dom.value;
		var actionURL='<ofbiz:url>/getSeriesNoByModelAndKeydesc</ofbiz:url>?model='+modelValue+'&keydesc='+keydescValue;
		Ext.lib.Ajax.formRequest('keyPartsCleanUpdateForm',actionURL,{success: commentSuccess_k, failure: commentFailure});		
	}
	function commentSuccess_k(o){
		 var result = eval('(' + o.responseText + ')');
			var seriesNoObj=document.getElementById("seriesNo");
			var seriesNoArray=result.seriesNoArray;
			var seriesNoObjSize=result.seriesNoArray.length;
			seriesNoObj.length=1;
			for(var i=0;i<seriesNoObjSize;i++){
				seriesNoObj.options[seriesNoObj.length]=new Option(seriesNoArray[i],seriesNoArray[i]);
			} 
			seriesNoObj.value='<%=seriesNo%>';		 
	}
	
	var commentFailure = function(o){
        Ext.MessageBox.alert('Status', 'Unable to connect.');
    };
    
	Ext.onReady(function(){
	    var dept = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'dept',
	        width:150,
	        forceSelection:true
	    });
	    dept.on('select',deptChange);
	    
	});	
</script>
<form id="keyPartsCleanUpdateForm" name="keyPartsCleanUpdateForm" method="POST" action="<%=request.getContextPath()%>/control/keyPartsCleanUpdateList">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td><fieldset><legend>关键备件清洗件更新查询</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC" height="30"> 
          <td class="en11pxb" width="12%">部门</td>
          <td class="en11pxb" width="28%">
    		<select id="dept" name="dept">
          		<option value=''></option>
	          		<ofbiz:if name="deptList">
		        		<ofbiz:iterator name="EquipmentDept" property="deptList">
			    			<option value='<ofbiz:inputvalue entityAttr="EquipmentDept" field="deptIndex"/>'><ofbiz:inputvalue entityAttr="EquipmentDept" field="equipmentDept"/></option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
    			</select>       		
    		</select>
          </td>
          <td class="en11pxb" width="12%">设备MODEL</td>
          <td class="en11pxb" width="28%">
          	<select id="model" name="model" style="width:150px" onchange="modelChange()">
          		<option value=''></option>
          	</select>          
          </td>
        </tr>
        <tr bgcolor="#DFE1EC" height="30"> 
          <td class="en11pxb" width="12%">关键字</td>
          <td class="en11pxb" width="28%">
          	<select id="keydesc" name="keydesc" style="width:150px" onchange="keydescChange()">
          		<option value=''></option>
          	</select>          
          </td>        
          <td class="en11pxb" width="12%">SERIESNO</td>
          <td class="en11pxb" width="28%">
    		<select id="seriesNo" name="seriesNo" style="width:150px">
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
			<li><a class="button-text" href="#" onclick="javascript:keyPartsCleanUpdateQuery();"><span>&nbsp;查询&nbsp;</span></a></li>
			</ul>
		</td>
	</tr>
</table>

<ofbiz:if name="flag" value="OK">
<div id="keyPartsCleanUpdateList" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="keyPartsCleanUpdateList" style="visibility:'hidden';">
</ofbiz:unless>


	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
	  <td><fieldset>
	      <legend>关键备件清洗件信息</legend>
	      <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#ACD5C9">
	          <td width="12%" class="en11pxb">关键字</td>
	          <td width="10%" class="en11pxb">物料号</td>      
	          <td width="12%" class="en11pxb">物料名</td>
	          <td width="10%" class="en11pxb">SeriesNo</td>
	          <td width="10%" class="en11pxb">寿命类型</td>
	          <td width="10%" class="en11pxb">寿命设定</td>
	          <td width="4%" class="en11pxb">是否启用</td>
	          <td width="12%" class="en11pxb">备注说明</td>
	          <td width="4%" class="en11pxb">操作类型</td>  
	          <td width="8%" class="en11pxb">操作人员</td>
	          <td width="8%" class="en11pxb">更新时间</td>
	        </tr>
	    <%
	    	if(keyPartsCleanUpdateList!=null&&keyPartsCleanUpdateList.size()>0){
	    		for(int i=0;i<keyPartsCleanUpdateList.size();i++){
	    			Map map=(Map)keyPartsCleanUpdateList.get(i);
	    %>
	       	<tr bgcolor="#DFE1EC">
	          <td width="12%" class="en11px"><%=map.get("KEYDESC")%></td>
	          <td width="10%" class="en11px"><%=map.get("PARTS_ID")%></td>      
	          <td width="12%" class="en11px"><%=map.get("PARTS_NAME")%></td>
	          <td width="10%" class="en11px"><%=map.get("SERIES_NO")%></td>
	          <td width="10%" class="en11px"><%=map.get("LIFE_TYPE")%></td>
	          <td width="10%" class="en11px"><%=map.get("LIMIT_LIFE")%></td>
	          <td width="4%" class="en11px" align="center"><%=map.get("ENABLE")%></td>
	          <td width="12%" class="en11px"><%=UtilFormatOut.checkNull((String)map.get("REMARK"))%></td>
	       	  <td width="4%" class="en11px" align="center"><%=map.get("ACTION")%></td>  
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

<script language="javascript">
	var deptObj=document.getElementById("dept");
	deptObj.value='<%=deptIndex%>'
	deptChange();
</script>

