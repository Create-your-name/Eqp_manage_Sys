<%@ page contentType="text/html;charset=gb2312"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<%
    String startDate = request.getParameter("startDate");
	String endDate = request.getParameter("endDate");
	String equipmentType = request.getParameter("equipmentType");
    String equipmentDept = request.getParameter("equipmentDept");

	List abnormalTableList = (List) request.getAttribute("abnormalTableList");
%>

<script language="javascript">

	function doSubmit(url) {
		document.abnormaltable.action = url;
		document.abnormaltable.submit();
	}
</script>

<form name="abnormaltable" method="post">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�쳣����ͳ�Ʊ���</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
            <td class="en11pxb" colspan="11">
                <input type="hidden" name="startDate" id="startDate" class="input" value=<%=startDate%>>
                <input type="hidden" name="endDate" id="endDate" class="input" value=<%=endDate%>>
                <input type="hidden" name="equipmentType" id="equipmentType" class="input" value=<%=equipmentType%>>
                <input type="hidden" name="equipmentDept" id="equipmentDept" class="input" value=<%=equipmentDept%>>
                    ��ѯ��ʼ���ڣ�<%=startDate%>����<%=endDate%>��
                    �豸���ࣺ<%=equipmentType%>��
                    ���ţ�<%=equipmentDept%>
            </td>
        </tr>

      	<tr bgcolor="#ACD5C9">
      	    <td class="en11pxb">�豸����</td>
      	    <td class="en11pxb">�쳣ԭ��</td>
      		<td class="en11pxb">�쳣����</td>
      		<td class="en11pxb">ƽ��ÿ���쳣ʱ�䣨Сʱ��</td>
    	</tr>

    	<%
    	if (abnormalTableList != null && abnormalTableList.size() > 0) {

    		for(Iterator it = abnormalTableList.iterator();it.hasNext();) {
				Map map = (Map) it.next();
    	%>
		        <tr bgcolor="#DFE1EC">
		            <td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("EQUIPMENT_TYPE"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("ABNORMAL_REASON"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("C"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("A"))%></td>
		        </tr>
		 <%
		 	}
		 }
		 %>

      </table>
      </fieldset></td>
  </tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/showAbnormalChartEntry')"><span>&nbsp;��ʾChart&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>
</form>

