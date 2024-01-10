<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*" %>
<%
	int i = 1;
	List smallPeriodList = (List)request.getAttribute("smallPeriodList");
    //int j = 0;
    int k = 1;
%>
<!-- yui page script-->
<script language="javascript">
	//保存
	function save(){
		loading();
		pmForm.submit();
	}
			
	function returnPmForm(){
		var actionURL='<ofbiz:url>/createPmRecord</ofbiz:url>'
		document.location.href=actionURL;
	}
</script>
<form action="<%=request.getContextPath()%>/control/submitSubSmallPmDelete" method="post" id="pmForm" onsubmit="return false;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>需要删除的小保养</legend>
	 <table width="100%" border="0" cellspacing="1" cellpadding="2">
	      <tr bgcolor="#ACD5C9">
	        <td width="30%" class="en11pxb">选择</td>	        
	        <td width="30%" class="en11pxb">保养类型</td>
	        <td width="30%" class="en11pxb">设备</td>		       
	      </tr>
	     
	       <% if(smallPeriodList != null && smallPeriodList.size() > 0) {  
       		for(Iterator it = smallPeriodList.iterator();it.hasNext();) { 
					Map map = (Map)it.next();%>
	        <tr bgcolor="#DFE1EC">
	        <% 
	        	k=i++;
	        %>
	        <input id="scheduleIndex_<%=k%>" type="hidden" name="scheduleIndex_<%=k%>" value='<%=map.get("SCHEDULEINDEX")%>' />
		    <td class="en11px"><input type="checkBox" id="select_<%=k%>" name="select_<%=k%>" value='1'></td>
		    <td class="en11px"><%=map.get("PERIODNAME")%></td>
		    <td class="en11px"><%=map.get("EQUIPMENTID")%></td>
	      	</tr>
	        <%
	  		}
	 	 }else{ %>
	 	 <tr bgcolor="#DFE1EC">
	 	 	<td class="en11px" colspan="3">
	 	 		<ofbiz:field attribute="_USER_MESSAGE_"/>
	 	 	</td>
	 	 </tr>	 	 
	 	 <%
	 	 }
	 	 %>
	     </table>
      </fieldset></td>
  </tr>
</table>
<br>
<table border="0" cellspacing="0" cellpadding="0">
	<tr height="30">
	    <td><ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:save();" id="save"><span>&nbsp;确定&nbsp;</span></a></li> 
		</ul>
		</td>
	</tr>
</table>
<input type="hidden" name="eqpNum" id="eqpNum" value='<%=k%>'>
</form>

