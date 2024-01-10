<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import ="com.csmc.pms.webapp.util.*"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>
<%@ page import="com.csmc.pms.webapp.workflow.model.*"%>
<%
	List guiPriv = (List)request.getSession().getAttribute(SessionNames.GUI_PRIV_LIST_KEY);
	GenericValue gv=(GenericValue)request.getAttribute("ABNORMAL");
	pageContext.setAttribute("abnormal",gv);
	String section=(String)request.getAttribute("SECTION");
	String accountDept=(String)request.getAttribute("ACCOUNT_DEPT");
	String inputType=gv.getString("formType");
	int status=Integer.parseInt(gv.getString("status"));
	String displayStatus="";
	String jobText=UtilFormatOut.checkNull(gv.getString("jobText"));
	String jobRelationIndex=(String)request.getAttribute("JOB_RELATION_INDEX");
	Job job = (Job)request.getAttribute("singleJob");
	List partsUseList = (List)request.getAttribute("PARTS_USE_LIST");
	if(StringUtils.isNotEmpty(jobText)){
		jobText=jobText.replaceAll("\r\n","<br>");
	}
	if(Constants.CREAT==status||Constants.START==status||Constants.HOLD==status){
		displayStatus="未完成";
	}else{
		displayStatus="完成";
	}
%>

<form method="post" id="abnorForm">
<CENTER><p align = "central">设备维修记录</p></CENTER>
<p align = "right"><%=accountDept%></p>
<table width="100%" border="1" cellspacing="0" cellpadding="0">
	<tr height="25">
		<td width="20%" class="en11pxb">设备名称</td>
		<td width="30%" class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/></td>
		<td width="20%" class="en11pxb">维护时间</td>
		<td width="30%" class="en11px">From：<ofbiz:inputvalue entityAttr="abnormal" field="abnormalTime"/><BR>
	          						   To：<ofbiz:inputvalue entityAttr="abnormal" field="returnTime"/></td>	
	</tr>
	
	<tr height="25">
		<td width="20%" class="en11pxb">异常原因</td>
		<td width="30%" class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="abnormalReason"/></td>
		<td width="20%" class="en11pxb">维护人员</td>
		<td width="30%" class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="createName"/></td>
	</tr>
	
	<tr height="25">
		<td width="20%" class="en11pxb">故障现象</td>
		<td colspan="3" class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="abnormalText"/></td>
	</tr>
	
	<tr height="25">
		<td width="20%" class="en11pxb">处理过程</td>
		<td colspan="3" class="en11px"><%=jobText%></td>
	</tr>
	
	<tr height="25">
	    <td width="20%" class="en11pxb">异常真正原因</td>
        <td colspan="3" class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="abnormalReason"/></td>
    </tr>
    
    <tr height="25">
		<td colspan="4" width="100%" class="en11pxb">更换易耗件和备件记录：</td>
	</tr>
</table>
<table width="100%" border="1" cellspacing="0" cellpadding="0" height="30">
      <tr height="25" >
      	 <td width="20%" class="en11pxb">序号</td>
         <td width="30%" class="en11pxb">料号或备件号</td>
         <td width="30%" class="en11pxb">料品名称</td>
         <td width="20%" class="en11pxb">数量</td>
       </tr>
       
       <%
       		if (partsUseList != null && partsUseList.size() > 0)
       		{
       			for (int i = 0, size = partsUseList.size();i < size; i++)
       			{
       				Map map = (Map)partsUseList.get(i);
       				if (map != null)
       				{
       %>
       					<tr height="25" >
		         			<td class="en11px"><%=(i+1)%></td>
		         			<td class="en11px"><%=(String)map.get("partNo")%></td>
		         			<td class="en11px"><%=(String)map.get("partName")%></td>
		         			<td class="en11px"><%=(String)map.get("partCount")%></td>
	       				</tr>
       <%					
       				}
       			}
       		} 
       %>
</table>
</form>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:history.back(-1)"><span>&nbsp;返回&nbsp;</span></a></li>
	</ul></td>

	</tr>
</table>
