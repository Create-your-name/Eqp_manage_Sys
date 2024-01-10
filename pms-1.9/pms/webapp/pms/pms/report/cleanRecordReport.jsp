<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%
	List partList = (List)request.getAttribute("PART_LIST");
	String equ = UtilFormatOut.checkNull(request.getParameter("equipmentType"));
	String ts = (String)request.getAttribute("TS_TYP");
	String pm = (String)request.getAttribute("PM_TYP");
	String equId = request.getParameter("equipmentId");
	String sDate = UtilFormatOut.checkNull(request.getParameter("startDate"));
	String eDate = UtilFormatOut.checkNull(request.getParameter("endDate"));
	String flag = (String)request.getAttribute("flag");
	
 %>
<script language='javascript' src='<%=request.getContextPath()%>/images/pupdate.js' type='text/javascript'></script>
<!-- yui page script-->
<script language="javascript">
	var flag='<%=flag%>';
	function partsUserQuery(){
		partsDataQueryForm.submit();
	}
	
	//设备大类选择
	function equipMentChange(){
		var newEqpValue=Ext.get('equipmentType').dom.value;
		var actionURL='<ofbiz:url>/getEqpIdAndPeriod</ofbiz:url>?equipmentType='+newEqpValue;
		Ext.lib.Ajax.formRequest('partsDataQueryForm',actionURL,{success: commentSuccess, failure: commentFailure});
	}
	
	//远程调用成功
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			//设备保养种类数据初始化
			var nameSize=result.priodNameArray.length;
			var nameArray=result.priodNameArray;
			var valueArray=result.priodValueArray;
			var pmObj=document.getElementById("period");
			pmObj.length=1;
			for(var i=0;i<nameSize;i++){
				pmObj.options[pmObj.length]=new Option(nameArray[i],valueArray[i]);
			}
			
			//设备ID数据初始化
			var equipId=document.getElementById("equipmentId");
			var eqpArray=result.eqpIdArray;
			var eqpIdSize=result.eqpIdArray.length;
			equipId.length=1;
			for(var i=0;i<eqpIdSize;i++){
				equipId.options[equipId.length]=new Option(eqpArray[i],eqpArray[i]);
			}
			
			//设备原因分类数据初始化
			var tsTypeObj=document.getElementById("tsType");
			var tsSize=result.tsNameArray.length;
			var tsName=result.tsNameArray;
			var tsValue=result.tsValueArray;
			tsTypeObj.length=1;
			for(var i=0;i<tsSize;i++){
				tsTypeObj.options[tsTypeObj.length]=new Option(tsName[i],tsValue[i]);
			}
			if(flag=='true'){
				initQuery();
				flag='false';
			}
		}
	}
	
   //初如化查询后的界面
   function initQuery(){
   		if('<%=ts%>'=='0'){
   			enabled("tsType");
   			var tsObj=document.getElementById('TS');
   			tsObj.checked=false;
   		}else{
   			var tsObj=document.getElementById('tsType');
			tsObj.value='<%=ts%>'
   		}
   		
   		if('<%=pm%>'=='0'){
   			enabled("period");
   			var pmObj=document.getElementById('PM');
   			pmObj.checked=false;
   		}else{
   			var pmObj=document.getElementById('period');
			pmObj.value='<%=pm%>'
   		}
   		var eqpIdObj=document.getElementById('equipmentId');
   		eqpIdObj.value='<%=equId%>';
   }
	
   //远程调用失败
   var commentFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
   };
	
	//页面重置
	function reset(){
		Ext.get('startDate').dom.value=""
		Ext.get('endDate').dom.value=""
		Ext.get('equipmentType').dom.value=""
		Ext.get('equipmentId').dom.value=""
		Ext.get('tsType').dom.value=""
		Ext.get('pmType').dom.value=""
		document.partsDataQueryForm.tsType.disabled=true;
		document.partsDataQueryForm.pmType.disabled=true;
	}
	
    function enabled(feild){
        var obj = eval("document.partsDataQueryForm."+feild);
        if(obj.disabled == true){
          obj.disabled = false;
        }else{
          obj.disabled = true;
        }
      }
      
	//初始化页面控件
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
	    
	    var eqpType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'equipmentType',
	        width:170,
	        forceSelection:true
	    });
	    eqpType.on('select',equipMentChange);
	 });
</script>
<form action="<%=request.getContextPath()%>/control/queryCleanRecordByCondition" method="post" id="partsDataQueryForm" name="partsDataQueryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>机台用料记录查询参数</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2"> 
	     <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb">日期:</td>
	       <td width="28%"><input type="text" ID="startDate" NAME="startDate" readonly></td>
	    <td width="12%" class="en11pxb">到:</td>
	    <td width="28%"><input type="text" ID="endDate" NAME="endDate"  readonly></td>
	    </tr>
	     <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb">设备大类:</td>
	       <td width="28%"><select id="equipmentType" name="equipmentType">
	          <option value=""></option>
	          <ofbiz:if name="equipMentList">
		        <ofbiz:iterator name="cust" property="equipMentList">
			    <option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentType"/>'><ofbiz:inputvalue entityAttr="cust" field="equipmentType"/></option>
		      </ofbiz:iterator>
	      	</ofbiz:if>
    		</select></td>
	    <td width="12%" class="en11pxb">EqpId:</td>
	    <td width="28%"><select id="equipmentId" name="equipmentId">
	          <option value=""></option>
    		</select></td>
	    </tr>
	    <tr bgcolor="#DFE1EC"> 
		    <td width="12%" class="en11pxb">记录类型:</td>
		    <td width="28%" colspan="3" class="en11pxb"><input type="checkbox" name="TS" id="TS" checked onclick="enabled('tsType')">异常记录&nbsp;&nbsp;&nbsp;&nbsp;
		    <input type="checkbox" name="PM" id="PM" checked onclick="enabled('period')">保养记录</td>
	    </tr>
	     <tr bgcolor="#DFE1EC"> 
		    <td width="12%" class="en11pxb">设备原因分类:</td>
		  	<td width="28%"><select id="tsType" name="tsType">
		  	<option value=""></option>
		  	</select></td>
		    <td width="12%" class="en11pxb">设备保养种类:</td>
		    <td width="28%"><select id="period" name="period">
		  	<option value=""></option>
		  	</select></td>
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
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>机台用料记录列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" style="word-break:keep-all">
        <tr bgcolor="#ACD5C9"> 
          <td width="10%" class="en11pxb">物料号</td>
          <td width="20%" class="en11pxb">品名规格</td>
          <td width="10%" class="en11pxb">品名类型</td>
          <td width="10%" class="en11pxb">EqpId</td>
          <td width="10%" class="en11pxb">事件类别</td>
          <td width="10%" class="en11pxb">数量</td>
          <td width="10%" class="en11pxb">单价</td>
          <td width="10%" class="en11pxb">总额</td>
          <td width="10%" class="en11pxb">更换日期</td>
        </tr>
       <% if(partList != null && partList.size() > 0) {  
       		for(Iterator it = partList.iterator();it.hasNext();) { 
					Map map = (Map)it.next();%>
		        <tr bgcolor="#DFE1EC">
		          <td class="en11px"><%=map.get("PART_NO")%></td>
		          <td class="en11px"><%=map.get("PART_NAME")%></td>
		          <td class="en11px"><%=map.get("PART_TYPE")%></td>
		          <td class="en11px"><%=map.get("EQUIPMENT_ID")%></td>
		          <td class="en11px"><%=map.get("EVENT_TYPE")%></td>
		          <td class="en11px"><%=map.get("PART_COUNT")%></td>
		          <%
		          	if ("DATA".equalsIgnoreCase((String)map.get("PART_TYPE")) && map.get("UNIT_PRICE") != null)
		          	{
		          %>	
		          <td class="en11px"><%=map.get("UNIT_PRICE")%></td>
		          <td class="en11px"><%=Long.parseLong((String)map.get("PART_COUNT")) * Double.parseDouble((String)map.get("UNIT_PRICE"))%></td>
		          <%	
		          	}
		          	else
		          	{
		          %>
		          <td class="en11px"></td>
		          <td class="en11px"></td>
		          <%
		          	}
		          %>
		          <td class="en11px"><%=map.get("END_TIME")%></td>
		        </tr>
	  <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>
<script language="javascript">
		var obj=document.getElementById('equipmentType');
		obj.value='<%=equ%>'
		equipMentChange();
   		var sDateObj=document.getElementById('startDate');
   		sDateObj.value='<%=sDate%>';
   		var eDateObj=document.getElementById('endDate');
   		eDateObj.value='<%=eDate%>';
</script>