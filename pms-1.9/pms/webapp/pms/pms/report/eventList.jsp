<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<% 
		String date1 = "";
		String date2 = "";	
		String PM = "";	
		String TS = "";
		if(pageContext.findAttribute("startDate")!=null){
			date1 = request.getAttribute("startDate").toString();
		}
		if(pageContext.findAttribute("endDate")!=null){
			date2 = request.getAttribute("endDate").toString();
		}
		if(pageContext.findAttribute("PM")!=null){
			PM = request.getAttribute("PM").toString();
		}
		if(pageContext.findAttribute("TS")!=null){
			TS = request.getAttribute("TS").toString();
		}
%>
<!-- yui page script-->
<script language="javascript">
	var flag=false;
	var oldDept="";
	//选择部门
	function deptChange(){
		var dept=Ext.get('dept').dom.value;
		var actionURL='<ofbiz:url>/getSectionInfo</ofbiz:url>?deptIndex='+dept;
		if(oldDept!=dept){
			Ext.lib.Ajax.formRequest('eventList',actionURL,{success: commentSuccess, failure: commentFailure});
		}
		oldDept=dept;
	}
	//远程调用成功
	function commentSuccess(o){
		//alert(o.responseText)
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			//设备保养种类数据初始化
			var nameSize=result.sectionArray.length;
			var nameArray=result.sectionArray;
			var valueArray=result.sectionIndexArray;
			var secObj=document.getElementById("section");
			secObj.length=1;
			for(var i=0;i<nameSize;i++){
				secObj.options[secObj.length]=new Option(nameArray[i],valueArray[i]);
			}
			if(!flag){
				secObj.value='<%=request.getParameter("section")%>'
				flag=true;
			}
		}
	}
	
	//远程调用失败
   var commentFailure = function(o){
    	Ext.MessageBox.alert('警告', 'Unable to connect.');
   };
   
	//初始化页面控件
	Ext.onReady(function(){
	   var deptSelect = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'dept',
	        width:170,
	        forceSelection:true
	    });
	    
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
	    
	    deptSelect.on('select',deptChange);
	    
	 });
	 
	 function reasonDocument(url){
		loading();;
		document.eventList.action = url;
		document.eventList.submit();	
		
	}
</script>
<form name="eventList" action="<%=request.getContextPath()%>/control/queryOverCaseFollowJobInfo" method="post">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>查询条件</legend>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr bgColor="#dfe1ec">
     <td width="28%" class="en11pxb"><input type="checkbox" id="PM" name="PM"  <%if (PM.equals("on")){%>checked<%} %>>保养记录</td>
     <td width="28%" class="en11pxb"><input type="checkbox" id="TS" name="TS" <%if (TS.equals("on")){%>checked<%} %>>异常记录</td>
     <td width="28%"></td>
     <td width="28%"></td>
</tr>
<tr bgcolor="#dfe1ec">
	        <td width="15%" bgcolor="#dfe1ec" class="en11pxb" height="20">部门</td>
	    	<td width="35%"  class="en11px"><select id="dept" name="dept">
		          <option value=""></option>
		          <ofbiz:if name="DEPT_LIST">
			        <ofbiz:iterator name="dept" property="DEPT_LIST">
				    <option value='<ofbiz:inputvalue entityAttr="dept" field="deptIndex"/>'><ofbiz:inputvalue entityAttr="dept" field="equipmentDept"/></option>
			      </ofbiz:iterator>
		      	</ofbiz:if>
    		</select></td>
	    	<td width="15%" bgcolor="#dfe1ec" class="en11pxb">课别</td>
	    	<td width="35%" class="en11px"><select id="section" name="section">
		          <option value=""></option>
		          </select>
		     </td>
        </tr>
        <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb" bgcolor="#dfe1ec">日期:</td>
	       <td width="28%" class="en11px"><input type="text" ID="startDate" NAME="startDate"  readonly value = <%=date1%>></td>
	    <td width="12%" class="en11pxb" bgcolor="#dfe1ec" >到:</td>
	    <td width="28%" class="en11px"><input type="text" ID="endDate" NAME="endDate" readonly value = <%=date2%>></td>
	    </tr>
</table>
</fieldset></td>
</table>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
   	<td><ul class="button">
			<li><a class="button-text" href="#"  onclick="javascript:reasonDocument('<%=request.getContextPath()%>/control/queryeventList');"><span>&nbsp;查询&nbsp;</span></a></li> 
	</ul></td>
  </tr>
</table>
</form>
<br>
<ofbiz:if name="flag" value="OK">
<div id="eventList" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="eventList" style="visibility:'hidden';">
</ofbiz:unless>

 <table width="100%" border="0" cellspacing="1" cellpadding="2">
  
<tr>
	<td><fieldset>
      <legend>事件List</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9"> 
        <td   class="en11pxb">EQ ID</td>
        <td   class="en11pxb">事件名称/保养类别</td>
        <td   class="en11pxb">发生日期</td>
        <td   class="en11pxb">状态</td>
      </tr>
      <%
      	List event_List = (List)request.getAttribute("event_List");
      	if ((event_List != null) && (event_List.size()!= 0)) {
       	for (int i = 0 ; i <= event_List.size()-1 ; i++){
       		Map map = (Map)event_List.get(i);
       		String EQUIPMENT_ID = UtilFormatOut.checkNull((String)(map.get("EQUIPMENT_ID")));
       		String NAME = UtilFormatOut.checkNull((String)(map.get("ABNORMAL_NAME")));
       		String CREATE_TIME = UtilFormatOut.checkNull((String)(map.get("START_TIME")));
       		String STATUS = UtilFormatOut.checkNull((String)(map.get("STATUS")));
       		if (STATUS.equals("1")){
       			STATUS = "已完成";
       		}else{
       			STATUS = "处理中";
       		}
		%>
       		 	<tr bgcolor="#DFE1EC">
					<td class="en11px"><%=EQUIPMENT_ID %></td>
       				<td class="en11px"><%=NAME %></td>
       				<td class="en11px"><%=CREATE_TIME %></td>
       				<td class="en11px"><%=STATUS %></td>
       			</tr>
       		<%
       	}
      	}
       %>
     </table>
     </fieldset>
     	</td>
	</tr>
</table>
<script language="javascript">
	 
	 document.getElementById("dept").value='<%=request.getParameter("dept")%>'
	 deptChange();
</script>