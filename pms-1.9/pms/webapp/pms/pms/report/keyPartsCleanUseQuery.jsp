<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.net.URLEncoder" %>
<% 	
	List keyPartsCleanDetailList=(List)request.getAttribute("keyPartsCleanDetailList");
	List keyPartsCleanSumList=(List)request.getAttribute("keyPartsCleanSumList");
	String deptIndex = (String)request.getAttribute("deptIndex");
	String model=(String)request.getAttribute("model");
	String keydesc=(String)request.getAttribute("keydesc");
	String seriesNo=(String)request.getAttribute("seriesNo");
	String isError=(String)request.getAttribute("isError");
%>
<script language="javascript">

	function keyPartsCleanUseDetaiQuery(){
		var deptValue=Ext.get('dept').dom.value;
		var modelValue=Ext.get('model').dom.value;
		var keydescValue=Ext.get('keydesc').dom.value;
		if(deptValue==""||modelValue==""){
			alert("部门、设备Model必填");
			return ;
		}
		document.getElementById("keyPartsCleanSumList").style.display="none";
		keyPartsCleanUseForm.action="<%=request.getContextPath()%>/control/keyPartsCleanUseList?type=detail";
		keyPartsCleanUseForm.submit();
	}
	
	function keyPartsCleanUseSumQuery(){
		var deptValue=Ext.get('dept').dom.value;
		var modelValue=Ext.get('model').dom.value;
		var keydescValue=Ext.get('keydesc').dom.value;
		if(deptValue==""||modelValue==""){
			alert("部门、设备Model必填");
			return ;
		}
		document.getElementById("keyPartsCleanDetailList").style.display="none";
		keyPartsCleanUseForm.action="<%=request.getContextPath()%>/control/keyPartsCleanUseList?type=sum";
		keyPartsCleanUseForm.submit();
	}	

	function deptChange(){
		debugger;
		var deptValue=Ext.get('dept').dom.value;
		var actionURL='<ofbiz:url>/getSectionAndModelByDeptIndex</ofbiz:url>?deptIndex='+deptValue;
		Ext.lib.Ajax.formRequest('keyPartsCleanUseForm',actionURL,{success: commentSuccess_d, failure: commentFailure});
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
		Ext.lib.Ajax.formRequest('keyPartsCleanUseForm',actionURL,{success: commentSuccess_m, failure: commentFailure});
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
		Ext.lib.Ajax.formRequest('keyPartsCleanUseForm',actionURL,{success: commentSuccess_k, failure: commentFailure});		
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
<form id="keyPartsCleanUseForm" name="keyPartsCleanUseForm" method="POST" action="<%=request.getContextPath()%>/control/keyPartsCleanUseList">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td><fieldset><legend>关键备件清洗件信息查询</legend>
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
        <tr bgcolor="#DFE1EC" height="30"> 
          <td class="en11pxb" width="12%">是否超期(仅对于汇总查询)</td>
          <td class="en11pxb" width="28%">
          	<select id="isError" name="isError" style="width:150px">
          		<option value=''></option>
          		<option value='Y'>Y</option>
          		<option value='N'>N</option>
          	</select>          
          </td>        
          <td class="en11pxb" width="12%"></td>
          <td class="en11pxb" width="28%"></td>
        </tr>              
      </table>
	 </fieldset> 
	</td>
  <tr> 
</table>
<table border="0" cellspacing="0" cellpadding="0">
	<tr height="30">
	 	<td><ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:keyPartsCleanUseDetaiQuery();"><span>&nbsp;详细查询&nbsp;</span></a></li>
			</ul>
			<ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:keyPartsCleanUseSumQuery();"><span>&nbsp;汇总查询&nbsp;</span></a></li>
			</ul>
		</td>
	</tr>
</table>

<ofbiz:if name="dflag" value="OK">
<div id="keyPartsCleanDetailList" style="display:'';">
</ofbiz:if>
<ofbiz:unless name="dflag">
<div id="keyPartsCleanDetailList" style="display:'none';">
</ofbiz:unless>


	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
	  <td><fieldset>
	      <legend>关键备件清洗件使用信息</legend>
	      <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#ACD5C9">
	          <td class="en11pxb">关键字</td>
	          <td class="en11pxb">物料号</td>      
	          <td class="en11pxb">物料名</td>
	          <td class="en11pxb">SeriesNo</td>
	          <td class="en11pxb">使用状态</td>
	          <td class="en11pxb">使用机台</td>
	          <td class="en11pxb">供应商</td>
	          <td class="en11pxb">baseS/N</td>
	          <td class="en11pxb">使用类别</td>
	          <td class="en11pxb">寿命类型</td>
	          <td class="en11pxb">预警寿命</td>
	          <td class="en11pxb">超期寿命</td>
	          <td class="en11pxb">上机时间</td>
	          <td class="en11pxb">下机时间</td>
	          <td class="en11pxb">使用寿命</td>  
	          <td class="en11pxb">换下原因</td>
	          <td class="en11pxb">备注</td> 
	          <td class="en11pxb">操作人员</td>          
	        </tr>
	    <%
	    	if(keyPartsCleanDetailList!=null&&keyPartsCleanDetailList.size()>0){
	    		for(int i=0;i<keyPartsCleanDetailList.size();i++){
	    			Map map=(Map)keyPartsCleanDetailList.get(i);
	    %>
	       	<tr bgcolor="#DFE1EC">
	          <td class="en11px"><%=map.get("KEYDESC")%></td>
	          <td class="en11px"><%=map.get("PARTS_ID")%></td>      
	          <td class="en11px"><%=map.get("PARTS_NAME")%></td>
	          <td class="en11px"><%=map.get("SERIES_NO")%></td>
	          <td class="en11px"><%=map.get("STATUS")%></td>
	          <td class="en11px"><%=map.get("EQP_ID")%></td>
	          <td class="en11px"><%=map.get("VENDOR")%></td>
	          <td class="en11px"><%=map.get("BASE_SN")%></td>
	          <td class="en11px"><%=map.get("PARTS_TYPE")%></td>
	          <td class="en11px"><%=map.get("LIMIT_TYPE")%></td>
	          <td class="en11px"><%=map.get("WARN_SPEC")%></td>
	          <td class="en11px"><%=map.get("ERROR_SPEC")%></td>
	          <td class="en11px"><%=map.get("UPDATE_TIME")%></td>
	          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("CREATE_TIME"))%></td>
	          <td class="en11px"><%=map.get("ACTUL")%></td>
	          <td class="en11px"><%=map.get("OFF_LINE")%></td>
	          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("REMARK"))%></td>
	          <td class="en11px"><%=map.get("UPDATE_USER")%></td>
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

<ofbiz:if name="sflag" value="OK">
<div id="keyPartsCleanSumList" style="display:'';">
</ofbiz:if>
<ofbiz:unless name="sflag">
<div id="keyPartsCleanSumList" style="display:'none';">
</ofbiz:unless>


	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
	  <td><fieldset>
	      <legend>关键备件清洗件寿命信息</legend>
	      <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#ACD5C9">
	          <td class="en11pxb">关键字</td>
	          <td class="en11pxb">物料号</td>      
	          <td class="en11pxb">物料名</td>
	          <td class="en11pxb">SeriesNo</td>
	          <td class="en11pxb">寿命类型</td>
	          <td class="en11pxb">寿命设定</td>
	          <td class="en11pxb">已用寿命</td>
	        </tr>
	    <%
	    	if(keyPartsCleanSumList!=null&&keyPartsCleanSumList.size()>0){
	    		for(int i=0;i<keyPartsCleanSumList.size();i++){
	    			Map map=(Map)keyPartsCleanSumList.get(i);
	    			int remainLife=(Integer)map.get("cleanRemainLife");
	    			if(remainLife<=0){
	    %>
	       	<tr bgcolor= "YELLOW"> 
	    <% 			} else{%>
	    	<tr bgcolor="#DFE1EC">
	    <% 			}%>	
	          <td class="en11px"><%=map.get("KEYDESC")%></td>
	          <td class="en11px"><%=map.get("PARTS_ID")%></td>      
	          <td class="en11px"><%=map.get("PARTS_NAME")%></td>
	          <td class="en11px"><%=map.get("SERIES_NO")%></td>
	          <td class="en11px"><%=map.get("LIFE_TYPE")%></td>
	          <td class="en11px"><%=map.get("LIMIT_LIFE")%></td>
	          <td class="en11px"><%=map.get("cleanPartsLife")%></td>
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
	var isErrorObj=document.getElementById("isError");
	isErrorObj.value='<%=isError%>'
</script>

