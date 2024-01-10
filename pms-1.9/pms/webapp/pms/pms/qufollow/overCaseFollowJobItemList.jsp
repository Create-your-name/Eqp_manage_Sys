<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>

<%
	GenericValue gv=(GenericValue)request.getAttribute("GU_FOLLOW");
	pageContext.setAttribute("follow",gv);
	List followItemList = (List)request.getAttribute("ITEM_LIST");
	String section=(String)request.getAttribute("SECTION");
	String dept=(String)request.getAttribute("DEPT");
%>

<!-- yui page script-->
<script language="javascript">
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>����׷�ٲ�ѯ����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
	      <td width="15%" bgcolor="#ACD5C9" class="en11pxb">����׷������</td>
	      <td width="35%" class="en11px"><ofbiz:inputvalue entityAttr="follow" field="followName"/></td>
	      <td width="15%"  bgcolor="#ACD5C9" class="en11pxb">Ŀ��</td>
          <td width="35%" class="en11px"><ofbiz:inputvalue entityAttr="follow" field="purpose"/></td>
        </tr>
        <tr bgcolor="#DFE1EC">
	        <td bgcolor="#ACD5C9" class="en11pxb">����</td>
	    	<td class="en11px"><%=dept%></td>
	    	<td bgcolor="#ACD5C9" class="en11pxb">�α�</td>
	    	<td class="en11px"><%=section%></td>
        </tr>
         <tr bgcolor="#DFE1EC">
	        <td bgcolor="#ACD5C9" class="en11pxb"><ofbiz:inputvalue entityAttr="follow" field="objectType"/></td>
	    	<td class="en11px"><ofbiz:inputvalue entityAttr="follow" field="object"/></td>
	    	<td bgcolor="#ACD5C9" class="en11pxb">������</td>
	    	<td class="en11px"><ofbiz:inputvalue entityAttr="follow" field="creator"/></td>
        </tr>
      </table>
      </fieldset></td>
  </tr>
</table>

<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>����׷�ٹ����б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="8%" class="en11pxb">�����</td>
          <td width="27%" class="en11pxb">׷�ٲ�������</td>
          <td width="24%" class="en11pxb">���ӵ���</td>
          <td width="18%" class="en11pxb">�������</td>
          <td width="18%" class="en11pxb">�޸�ʱ��</td>
        </tr>
          <% if(followItemList != null && followItemList.size() > 0) {
          		int size=followItemList.size();
				for(int i=0;i<size;i++){
					Map map=(Map)followItemList.get(i);
					%>
		        <tr bgcolor="#DFE1EC">
		          <td class="en11px"><%=map.get("ITEM_ORDER")%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("ITEM_CONTENT"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("FILE_DESCRIPTION"))%></td>
		          <td class="en11px"><%=map.get("CREATE_TIME")%></td>
		          <td class="en11px"><%=map.get("UPDATE_TIME")%></td>
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
			<li><a class="button-text" href="javascript:history.back(-1)"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>