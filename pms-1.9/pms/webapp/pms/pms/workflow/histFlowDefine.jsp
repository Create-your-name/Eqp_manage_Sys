<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.workflow.model.*"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<script language="javascript">
	function showFlowDetail(histIndex,periodName)
	{
		document.location="<ofbiz:url>/viewHistFlow</ofbiz:url>?histIndex="+histIndex+"&periodName="+periodName;
	}
</script>

<form method="post" name="histFlowEntryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>历史处理程序列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td class="en11pxb">程序名称</td>
      		<td class="en11pxb">程序描述</td>
      		<td class="en11pxb">版本时间</td>
    	</tr>

    	<ofbiz:if name="histJobs">
		     <ofbiz:iterator name="cust" property="histJobs">
		        <tr bgcolor="#DFE1EC">
		        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="jobName"/></td>
		        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="jobDescription"/></td>
		        	<td class="en11px">
		        	    <img src="<%=request.getContextPath()%>/pms/images/copy_16.gif" style="cursor:hand" onclick="showFlowDetail(<ofbiz:inputvalue entityAttr="cust" field="histIndex" default="" tryEntityAttr="false"/>,'<%=request.getAttribute("periodName")%>')" />
		        	    <ofbiz:inputvalue entityAttr="cust" field="updateTime" default="" tryEntityAttr="false"/>
		        	</td>
		        </tr>
		   	</ofbiz:iterator>
      </ofbiz:if>

      </table>
      </fieldset></td>
  </tr>
</table>
<br>

<ul class="button">
			<li><a class="button-text" href="javascript:javascript:history.back(-1)"><span>&nbsp;返回&nbsp;</span></a></li>
	</ul>
</form>