<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>

<% List list = (List)request.getAttribute("PC_STYLE_LIST"); %>
<table height="35" border="0" cellpadding="0" cellspacing="0">
	<% for(Iterator it = list.iterator(); it.hasNext(); ) { 
		GenericValue info = (GenericValue)it.next();%>
	<tr> 
	  <td><%=UtilFormatOut.checkNull(info.getString("name"))%></td>
	</tr>
	<% } %>
</table>