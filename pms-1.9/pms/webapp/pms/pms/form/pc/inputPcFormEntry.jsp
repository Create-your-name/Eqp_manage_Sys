<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>

<%
    String pcName = (String) request.getAttribute("pcName");
    String createTime = (String) request.getAttribute("createTime");
    String createUser = (String) request.getAttribute("createUser");
    String note = UtilFormatOut.checkNull((String) request.getAttribute("note"));
    String jobText = UtilFormatOut.checkNull((String) request.getAttribute("jobText"));
    String styleName = (String) request.getAttribute("styleName");
    String periodName = (String) request.getAttribute("periodName");
    String pcIndex = (String) request.getAttribute("pcIndex");
    String jobRelationIndex = (String) request.getAttribute("jobRelationIndex");
 %>

<script language="javascript">
    //ִ������
	function runJob(jobRelationIndex) {
        var url = "<ofbiz:url>/runFlow</ofbiz:url>?jobRelationIndex=" + jobRelationIndex;
        window.open(url,"job",
                "top=130,left=240,width=600,height=400,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=no");
    }

    //�鿴��������
    function viewJob(jobRelationIndex) {
        var url = "<ofbiz:url>/viewFlow</ofbiz:url>?jobRelationIndex=" + jobRelationIndex;
        window.open(url,"viewJob",
                "top=130,left=240,width=600,height=400,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=no");
    }

</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>Ѳ���</legend>
          <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td width="15%" bgcolor="#ACD5C9" class="en11pxb">Ѳ����ʽ</td>
	          <td width="18%" class="en11px"><%=styleName%></td>
	          <td width="15%" bgcolor="#ACD5C9" class="en11pxb">Ѳ������</td>
	          <td width="18%" class="en11px"><%=periodName%></td>
	          <td width="16%" bgcolor="#ACD5C9" class="en11pxb">�����</td>
	          <td width="18%" class="en11px"><%=pcName%></td>
	        </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">Ѳ��ʱ��</td>
	          <td class="en11px"><%=createTime%></td>
	          <td class="en11pxb" bgcolor="#ACD5C9">������</td>
	          <td class="en11px"><%=createUser%></td>
	          <td class="en11pxb" bgcolor="#ACD5C9">��ע</td>
	          <td class="en11px"><%=note%></td>
	        </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">�������</td>
	          <td colspan="5" class="en11px">
	            <textarea name="jobText" cols="60" rows="10" readonly><%=jobText%></textarea>
	          </td>
	        </tr>
	      </table>
      </fieldset></td>
  </tr>
</table>

<%if (jobRelationIndex != null) {%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
	<td colspan="2" class="en11px"><ul class="button">
	    <%if (!request.getParameter("status").equals("���")) {%>
	        <li><a class="button-text" href="javascript:runJob(<%=jobRelationIndex%>)"><span>&nbsp;ִ�д������&nbsp;</span></a></li>
        <%}%>

	     <li><a class="button-text" href="javascript:viewJob(<%=jobRelationIndex%>)"><span>&nbsp;��ѯ������Ŀ&nbsp;</span></a></li>

	     <li><a class="button-text" href="javascript:javascript:history.back(-1)"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>
<%}%>