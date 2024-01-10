<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.Calendar"%>

<%
		int nextYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
		if (Calendar.getInstance().get(Calendar.MONTH) == 0) {
				nextYear = nextYear - 1;
		}
%>

<script language="javascript">
	function yearPeriod(month)
	{
		if (month==11||month==10||month==0)
		{
			Ext.MessageBox.confirm('ȷ��', '��ȷ��Ҫ����' + Ext.getDom('year').value + '�걣���ƻ���',function result(value){
				if(value=='yes'){
					loading();
					document.yearPeriodSchedule.submit();
				}else{
					return;
				}
			});

		}
		else
		{
			alert("����11�£�12�£�1��ִ�д˳���");
			return;
		}
	}
</script>
<form name="yearPeriodSchedule" method="POST" action="<%=request.getContextPath()%>/control/yearPeriodSchedule">
<table width="100%" border="0" cellspacing="0" cellpadding="2">
  <tr>
    <td width="100%"><fieldset><legend>�����PM�ƻ�������</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
          <td class="en11pxb" width="10%" bgcolor="#ACD5C9">�û�</td>
          <td class="en11pxb"><input type="text" class="input" name="accountName" value="<ofbiz:field attribute="accountName"/>" <ofbiz:unless name="accountNo" value="GUISUP">readonly</ofbiz:unless>></td>
          <input type="hidden" name="accountNo" value="<ofbiz:field attribute="accountNo"/>" />
        </tr>
        <tr bgcolor="#DFE1EC">
          <td class="en11pxb" bgcolor="#ACD5C9">�α�</td>
          <td class="en11pxb"><input type="text" class="input" name="accountSection" value="<ofbiz:field attribute="accountSection"/>" <ofbiz:unless name="accountNo" value="GUISUP">readonly</ofbiz:unless>></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td class="en11pxb" bgcolor="#ACD5C9">���</td>
          <td class="en11pxb"><input type="text" class="input" name="year" value="<%=nextYear%>" <ofbiz:unless name="accountNo" value="GUISUP">readonly</ofbiz:unless>></td>
        </tr>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
<tr height="30">
	<% Integer month = (Integer)request.getAttribute("month"); %>
 	<td><ul class="button">
		<li><a class="button-text" href="#" onclick='<%="yearPeriod("+month+")"%>'><span>&nbsp;ȷ��&nbsp;</span></a></li>
		</ul>
	</td>
</tr>
</table>
</form>

<p>
	<font color="#FF0000" face="����">����11�¡�12�¼�1��ʹ�øù���������һ���PM�ƻ�!</font>
</p>