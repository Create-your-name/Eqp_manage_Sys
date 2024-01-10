<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*" %>
<%
	int i = 1;
	List pmRecordList = (List)request.getAttribute("subEqpList");
    Map paramMap = (Map)request.getAttribute("paramMap");
    int j = 0;
    int k = 1;
%>
<!-- yui page script-->
<script language="javascript">
	//保存
	function save(){
		var subList = Ext.DomQuery.select('*[name^=select_]',Ext.getDom('subArea'));
		var checkedNum = 0;
		for(var i = 0; i < subList.length; i++) {
			var item = subList[i];
			if(item.checked) {
				checkedNum++;
			if(!valid(item)) {
				Ext.MessageBox.alert('警告', '请选择一个处理程序!');
				return;
			}
			}
		}
		if (checkedNum == 0)
		{
				Ext.MessageBox.alert('警告', '请至少选择一个子设备!');
				return;
		}
		
		loading();
		pmForm.submit();
	}
	
	function valid(item){
		var itemName = item.id;
		var index = itemName.split('_')[1];
		var jobList = Ext.DomQuery.select('*[name*=_jobSelect_]',Ext.getDom('jobArea'+index));
		for(var j = 0; j < jobList.length; j++) {
			var job = jobList[j];
			if(job.checked) {
				return true;
			}
		}
		return false;
	}
			
	function returnPmForm(){
		var actionURL='<ofbiz:url>/createPmRecord</ofbiz:url>'
		document.location.href=actionURL;
	}
</script>
<form action="<%=request.getContextPath()%>/control/subSumbitPmForm" method="post" id="pmForm" onsubmit="return false;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>子设备列表</legend>
	 <table width="100%" border="0" cellspacing="1" cellpadding="2" id="subArea">
	      <tr bgcolor="#ACD5C9">
	        <td width="10%" class="en11pxb">选择</td>	        
	        <td width="30%" class="en11pxb">子设备</td>
	        <td width="60%" class="en11pxb">处理程序</td>	       
	      </tr>
	     
	       <% if(pmRecordList != null && pmRecordList.size() > 0) {  
       		for(Iterator it = pmRecordList.iterator();it.hasNext();) { 
					Map map = (Map)it.next();%>
	        <tr bgcolor="#DFE1EC">
	        <% 
	        	k=i++;
	        %>
		    <td class="en11px"><input type="checkBox" id="select_<%=k%>" name="select_<%=k%>" value='1'></td>
		    <td class="en11px"><input id="equipmentId_<%=k%>" type="hidden" name="equipmentId_<%=k%>" value='<%=map.get("EQUIPMENT_ID")%>' /><%=map.get("EQUIPMENT_ID")%></td>
		    <td class="en11px">
       	 	<table width="100%" border="0" cellspacing="1" cellpadding="2" id="jobArea<%=k%>">
          	 <ofbiz:if name="dealProgrammeList">
          	 	<%
          	 		j=0;
          	 	%>
          	 	<ofbiz:iterator name="cust" property="dealProgrammeList">
    		    <%
		    		j++;
		    	%>
          	 	<tr bgcolor="#DFE1EC">
					<td width="10%"  class="en11pxb"><input type="checkbox" name="<%=map.get("EQUIPMENT_ID")%>_jobSelect_<%=j%>" id="<%=map.get("EQUIPMENT_ID")%>_jobSelect_<%=j%>" value="1"></td>
					<td width="90%"  class="en11pxb"><input type="hidden" name="<%=map.get("EQUIPMENT_ID")%>_jobIndex_<%=j%>" id="<%=map.get("EQUIPMENT_ID")%>_jobIndex_<%=j%>" value='<ofbiz:inputvalue entityAttr="cust" field="jobIndex"/>'><ofbiz:inputvalue entityAttr="cust" field="jobName"/></td>
          	 	</tr>
          	 	</ofbiz:iterator> 
          	 </ofbiz:if>
          	 <input type="hidden" name="jobNum_<%=k%>" id="jobNum_<%=k%>" value="<%=j%>"> 
          	 </table>
		    </td>
	      	</tr>
	        <%
	  		}
	 	 } %>
	     </table>
      </fieldset></td>
  </tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
	<tr height="30">
	    <td><ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:save();" id="save"><span>&nbsp;确定&nbsp;</span></a></li> 
		</ul>
		<ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:returnPmForm();"><span>&nbsp;返回&nbsp;</span></a></li> 
		</ul>
		</td>
	</tr>
</table>
<input type="hidden" name="periodIndex" id="periodIndex" value='<%=paramMap.get("periodIndex")%>'>
<input type="hidden" name="scheduleDate" id="schduleDate" value='<%=paramMap.get("scheduleDate")%>'>
<input type="hidden" name="formType" id="formType" value='<%=paramMap.get("formType")%>'>
<input type="hidden" name="eqpNum" id="eqpNum" value='<%=k%>'>
</form>

