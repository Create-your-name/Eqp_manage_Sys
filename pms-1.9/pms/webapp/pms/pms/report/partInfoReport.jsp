<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*" %>
<% 
List partList = (List)request.getAttribute("PART_LIST");
%>
<!-- yui page script-->
<script language="javascript">
</script>
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>����ʹ����Ϣ��ϸ</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="20%" class="en11pxb">���Ϻ�</td>
          <td width="30%" class="en11pxb">Ʒ�����</td>
          <td width="20%" class="en11pxb">Ʒ������</td>
          <td width="10%" class="en11pxb">EqpId</td>
          <td width="10%" class="en11pxb">�¼����</td>
          <td width="10%" class="en11pxb">����</td>
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
		        </tr>
	  <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>