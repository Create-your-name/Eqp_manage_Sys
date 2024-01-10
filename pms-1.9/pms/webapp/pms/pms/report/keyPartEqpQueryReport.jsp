<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>

<%
	List partList = (List)request.getAttribute("PART_LIST");
	String eqpmodel = (String)request.getAttribute("eqpmodel");
	String eqpDept = (String)request.getAttribute("eqpDept");
	String deptIndex = (String)request.getAttribute("deptIndex");	
 	String section=(String)request.getAttribute("section");
	String equipmentId=(String)request.getAttribute("equipmentId"); 
	String isWarn=(String)request.getAttribute("isWarn");
	String isdelay=(String)request.getAttribute("isdelay");
	String isError=(String)request.getAttribute("isError");
 %>
<script language='javascript' src='<%=request.getContextPath()%>/images/pupdate.js' type='text/javascript'></script>
<!-- yui page script-->
<script language="javascript">
	function keyPartsDelayQuery(keyUseId){
		debugger;
        var result=window.open ("<%=request.getContextPath()%>/control/queryKeyPartsDelayReport?keyUseId="+keyUseId,"","top=130,left=240,width=650,height=350,title=,channelmode=0," +
             "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
             "scrollbars=1,status=1,titlebar=0,toolbar=0");
	}

	function partsUserQuery(){
		debugger;
		
		loading();
		document.keypartsDataQueryForm.submit();
	}
	
//初始化页面控件
	Ext.onReady(function(){	    
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
           //设备ID数据初始化
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
	//远程调用成功
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
	 
           //设备ID数据初始化
			var equipId=document.getElementById("equipmentId");
			var eqpArray=result.eqpIdArray;
			var eqpIdSize=result.eqpIdArray.length;
			equipId.length=1;
			for(var i=0;i<eqpIdSize;i++){
				equipId.options[equipId.length]=new Option(eqpArray[i],eqpArray[i]);
			}
		var obj=document.getElementById('equipmentId');
		obj.value='<%=equipmentId%>' 
		 
	}
		
	
	//远程调用失败
   var commentFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
   }; 
</script>
<form action="<%=request.getContextPath()%>/control/queryKeyPartsEqpByCondition" method="post" id="keypartsDataQueryForm" name="keypartsDataQueryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>关键备件查询参数</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2"> 	     
	     <tr bgcolor="#DFE1EC"> 
	        <td width="12%" class="en11pxb">部门:</td>
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
          <td width="12%" class="en11pxb">课别:</td>
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
	          		<ofbiz:if name="equipMoelList">
		        		<ofbiz:iterator name="EquipmentModel" property="equipMoelList">
			    			<option value='<ofbiz:inputvalue entityAttr="EquipmentModel" field="model"/>'><ofbiz:inputvalue entityAttr="EquipmentModel" field="model"/></option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
    			</select>          		
          </td>
	    <td width="12%" class="en11pxb">EqpId:</td>
	    <td width="28%"><select id="equipmentId" name="equipmentId">
	          <option value=""></option>
    		</select></td>
	    </tr>
	    
	    <tr bgcolor="#DFE1EC"> 
	        <td width="12%" class="en11pxb">是否预警:</td>
		    <td width="28%">          		
         		<select id="isWarn" name="isWarn">
          			<option value=''></option>
          			<option  <% if(UtilFormatOut.checkNull(isWarn).equals("Y")) { %>  selected <% }%> value="Y">Y</option>
    			</select>          		
          </td>
          <td width="12%" class="en11pxb">是否超期</td>
	      <td width="28%">    
         	<select id="isError" name="isError">
	          <option value=""></option>
	          <option  <% if(UtilFormatOut.checkNull(isError).equals("Y")) { %>  selected <% }%> value="Y">Y</option>  
    		</select>       		
          </td>
        </tr>
        <tr bgcolor="#DFE1EC">
	    <td class="en11pxb">是否延期</td>
	    <td>    
         	<select id="isdelay" name="isdelay">
	          <option value=""></option>
	          <option  <% if(UtilFormatOut.checkNull(isdelay).equals("Y")) { %>  selected <% }%> value="Y">Y</option>
		      <option  <% if(UtilFormatOut.checkNull(isdelay).equals("N")) { %>  selected <% }%> value="N">N</option>    
    		</select>       		
          </td>
          <td></td>
          <td></td>
	    </tr>
	 
	    <tr bgcolor="#DFE1EC">
	   		<td width="20%" class="en11pxb" align="left" colspan="4">
		    	<table border="0" cellspacing="0" cellpadding="0">
				  <tr height="30" >
				   	<td width="0">&nbsp;</td>
				    <td><ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:partsUserQuery();"><span>&nbsp;查询&nbsp;</span></a></li> 
					</ul><ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:reset();"><span>&nbsp;重置&nbsp;</span></a></li> 
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
<div id="queryKeyPartsEqpByCondition" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="queryKeyPartsEqpByCondition" style="visibility:'hidden';">
</ofbiz:unless>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>关键备件使用记录列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb">设备Group</td>
          <td width="5%" class="en11pxb">EqpID</td>
          <td width="10%" class="en11pxb">料号</td>
          <td width="15%" class="en11pxb">备件描述</td> 
          <td width="15%" class="en11pxb">关键字</td>
          <td width="15%" class="en11pxb">类别</td>
          <td width="15%" class="en11pxb">寿命定义</td> 
          <td width="5%" class="en11pxb">Alarmlife</td>
          <td width="5%" class="en11pxb">Warminglife</td>
          <td width="5%" class="en11pxb">延期次数</td>
          <td width="5%" class="en11pxb">延期寿命</td>
          <td width="5%" class="en11pxb">Actual</td> 
          <td width="10%" class="en11pxb">上机时间</td>
          <td width="5%" class="en11pxb">Vendor</td>
          <td width="5%" class="en11pxb">SeriesNo.</td>
          <td width="5%" class="en11pxb">baseS/N</td>
          <td width="5%" class="en11pxb">下机时间</td>
          <td width="5%" class="en11pxb">Status</td>
          <td width="5%" class="en11pxb">操作者</td>
          <td width="10%" class="en11pxb">REMARK</td>      
        </tr>
       <% if(partList != null && partList.size() > 0) {  
       		for(Iterator it = partList.iterator();it.hasNext();) { 
					Map map = (Map)it.next();
					String partsType=(String)map.get("PARTS_TYPE");
					if(partsType!=null&&partsType.equals("DELAY")){
						partsType="延期";
					}
					if( ( Float.parseFloat((String)map.get("ACTUL")) >=  (Float.parseFloat((String)map.get("ERROR_SPEC"))+Float.parseFloat((String)map.get("DELAY_LIFE"))) ) && ((String)map.get("CREATE_TIME")==null || (String)map.get("CREATE_TIME")=="" ) ){%> 
						<tr bgcolor= "OrangeRed">  
					<% }else if(( Float.parseFloat((String)map.get("ACTUL")) >=  (Float.parseFloat((String)map.get("WARN_SPEC"))+Float.parseFloat((String)map.get("DELAY_LIFE"))) ) && ((String)map.get("CREATE_TIME")==null || (String)map.get("CREATE_TIME")=="" ) ){%>
						 <tr bgcolor= "PINK"> 
					<% }else if(Integer.parseInt((String)map.get("DELAYTIME"))>0){%>
						 <tr bgcolor= "YELLOW"> 
					<%}else{%> 
						 <tr bgcolor= "#DFE1EC">  
					<% } %>
			 
		          <td class="en11px"><%=map.get("EQUIPMENT_TYPE")%></td>
		          <td class="en11px"><%=map.get("EQP_ID")%></td>
		          <!--  <td class="en11px"><a href="#" onclick="editKeyPartoffline(this,'<%=map.get("KEY_USE_ID")%>')"><%=map.get("PART_NO")%></td>-->
		          <td class="en11px"><%=map.get("PARTS_ID")%></td>
		          <td class="en11px"><%=map.get("PARTS_NAME")%></td>		          
		          <td class="en11px"><%=map.get("KEYDESC")%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull(partsType)%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("LIMIT_TYPE"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("ERROR_SPEC"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("WARN_SPEC"))%></td>
		          <td class="en11px">
		          <%
		          String delaytime=(String)map.get("DELAYTIME");
		          if(delaytime.equals("0")){
		          %>
		          	<%=map.get("DELAYTIME")%>
		          <%}else{%>
		          	<a href="#" onclick="keyPartsDelayQuery('<%=map.get("KEY_USE_ID")%>');"><%=map.get("DELAYTIME")%></a>
		          <%}%>
		          </td>
		          <td class="en11px"><%=map.get("DELAY_LIFE")%></td>
		          <td class="en11px"><% if(map.get("lifeError")!=null ){ %><%=map.get("lifeError")%><% }else{ %><%=map.get("ACTUL")%><% } %>
		          </td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("UPDATE_TIME"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("VENDOR"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("SERIES_NO"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("BASE_SN"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("CREATE_TIME"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("STATUS"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("TRANS_BY"))%></td>
		           <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("REMARK"))%></td>
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
		var sectionobj=document.getElementById('deptSection');
		sectionobj.value='<%=section%>'
		var obj=document.getElementById('eqp_Model');
		obj.value='<%=eqpmodel%>'
   		var obj=document.getElementById('equipmentId');
		obj.value='<%=equipmentId%>'
</script>

