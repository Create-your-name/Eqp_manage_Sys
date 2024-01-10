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
    //执行流程
	function runJob(jobRelationIndex) {
        var url = "<ofbiz:url>/runFlow</ofbiz:url>?jobRelationIndex=" + jobRelationIndex;
        window.open(url,"job",
                "top=130,left=240,width=600,height=400,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=no");
    }

    //查看流程数据
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
      <legend>巡检表单</legend>
          <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td width="15%" bgcolor="#ACD5C9" class="en11pxb">巡检样式</td>
	          <td width="18%" class="en11px"><%=styleName%></td>
	          <td width="15%" bgcolor="#ACD5C9" class="en11pxb">巡检周期</td>
	          <td width="18%" class="en11px"><%=periodName%></td>
	          <td width="16%" bgcolor="#ACD5C9" class="en11pxb">表单编号</td>
	          <td width="18%" class="en11px"><%=pcName%></td>
	        </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">巡检时间</td>
	          <td class="en11px"><%=createTime%></td>
	          <td class="en11pxb" bgcolor="#ACD5C9">建表人</td>
	          <td class="en11px"><%=createUser%></td>
	          <td class="en11pxb" bgcolor="#ACD5C9">备注</td>
	          <td class="en11px"><%=note%></td>
	        </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">处理过程</td>
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
	    <%if (!request.getParameter("status").equals("完成")) {%>
	        <li><a class="button-text" href="javascript:runJob(<%=jobRelationIndex%>)"><span>&nbsp;执行处理程序&nbsp;</span></a></li>
        <%}%>

	     <li><a class="button-text" href="javascript:viewJob(<%=jobRelationIndex%>)"><span>&nbsp;查询流程项目&nbsp;</span></a></li>

	     <li><a class="button-text" href="javascript:javascript:history.back(-1)"><span>&nbsp;返回&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>
<%}%>