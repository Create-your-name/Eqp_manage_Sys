<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="java.util.*"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>
<%@ page import="com.csmc.pms.webapp.workflow.model.*"%>
<%@ page import="com.csmc.pms.webapp.workflow.help.*"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<script language=javascript>
function doPrint1() 
{
	var bdhtml=window.document.body.innerHTML;
	var sprnstr="<!--startprint-->";
	var eprnstr="<!--endprint-->";
	prnhtml=bdhtml.substring(bdhtml.lastIndexOf(sprnstr)+sprnstr.length,bdhtml.lastIndexOf(eprnstr));
	window.document.body.innerHTML=prnhtml;
	window.print();
}

var hkey_root,hkey_path,hkey_key;
  hkey_root="HKEY_CURRENT_USER";
  hkey_path="\\Software\\Microsoft\\Internet Explorer\\PageSetup\\";

  //网页打印时清空页眉页脚
  function pagesetup_null(){
      try{
        var RegWsh = new ActiveXObject("WScript.Shell")
        hkey_key="\header"    
        RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"")
        hkey_key="\footer"
        RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"")
      }catch(e){
      }
  }  
  
  //网页打印的时恢复页眉页脚为默认值
  function pagesetup_default(){
      try{
        var RegWsh = new ActiveXObject("WScript.Shell")
        hkey_key="\header"    
        RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"&w&b页码，&p/&P")
        hkey_key="\footer"
        RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"&u&b&d")
      }catch(e){
      	
      }
  }

  function doPrint(printDiv)
  {
  	  try{
        pagesetup_null();

      newwin=window.open("", "newwin", "height="+window.screen.height+",width="+window.screen.width+",toolbar=no,scrollbars=auto,menubar=no");
      newwin.document.body.innerHTML=document.getElementById(printDiv).innerHTML;
      newwin.window.print();
      newwin.window.close();
      pagesetup_default();
    }catch(e){
   }
  }


</script>


<% Job job = (Job)request.getAttribute("Job"); 
	if(CommonUtil.isNotNull(job)) { 
	boolean tempFlag = false; 
	boolean printFlag = false; 
	if("true".equals(request.getParameter("submitFlag"))) tempFlag = true; 
	if("true".equals(request.getParameter("isPrint"))) printFlag = true; 
	%>
<!-- yui page script-->
<script language="javascript">
	function showJob(jobIndex) {
        var url = "<%=request.getContextPath()%>/pms/workflow/flowShow.jsp?jobIndex=" + jobIndex;
        window.open(url,"job",
                "top=130,left=240,width=600,height=400,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=no");
    }
    
    function showJobTemp(tempIndex) {
        var url = "<%=request.getContextPath()%>/pms/workflow/flowTempShow.jsp?tempIndex=" + tempIndex;
        window.open(url,"job",
                "top=130,left=240,width=600,height=400,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=no");
    }
    
    function doSubmit(url) {
		loading();
		document.flowDefineForm.action=url;
		document.flowDefineForm.submit();
	}
</script>

<form method="post" name="flowDefineForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>流程</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td width="20%" class="en11pxb">流程名</td>
      		<% if(!tempFlag) { %>
      		<td width="80%" bgcolor="#DFE1EC" class="en11px"><a href="javascript:showJob(<%=job.getJobIndex()%>);"><%=job.getJobName()%></a></td>
    		<% } else { %>
    		<td width="80%" bgcolor="#DFE1EC" class="en11px"><a href="javascript:showJobTemp(<%=job.getTempIndex()%>);"><%=job.getJobName()%></a></td>
    		<% } %>
    	</tr>
    	<tr bgcolor="#ACD5C9">
      		<td width="20%" class="en11pxb">流程描述</td>
      		<td width="80%" bgcolor="#DFE1EC" class="en11px"><%=UtilFormatOut.checkNull(job.getJobDescription())%></td>
    	</tr>
      </table>
      </fieldset></td>
  </tr>
</table>
<br>

<%
if(!"".equals(request.getAttribute("periodName")))
{
%>

<!--startprint-->
<div id="printDiv" class="tab-content">
<CENTER><p align = "central">设备维护记录</p></CENTER>
<table width="100%" border="1" cellspacing="0" cellpadding="0">
	<tr height="25" >
		<td width="20%" class="en11pxb">设备名称</td>
		<td width="30%" class="en11px">&nbsp;</td>
		<td width="20%" class="en11pxb">维护时间</td>
		<td width="30%" class="en11px">From：&nbsp;<BR>
	          						   To：&nbsp;</td>	
	</tr>
	
	<tr height="25">
		<td width="20%" class="en11pxb">维护类别</td>
		<!-- job.getFlowName()<%=job.getFlowName()%>job.getFormIndex()<%=job.getFormIndex()%>job.getJobName()<%=job.getJobName()%>job.getJobIndex()<%=job.getJobIndex()%> -->
		<!--<td width="30%" class="en11px"><%=job.getJobName()%></td>-->
		<td width="30%" class="en11px"><%=request.getAttribute("periodName")%></td>
		<td width="20%" class="en11pxb">维护人员</td>
		<td width="30%" class="en11px">&nbsp;</td>
	</tr>
</table>
<table width="100%" border="1" cellspacing="0" cellpadding="0">
	<tr height="25">
		<td width="100%" class="en11pxb">一、维护情况记录：</td>
	</tr>
</table>	
<table width="100%" border="1" cellspacing="0" cellpadding="0">
	<tr height="25">
		<td width="10%" class="en11pxb">序号</td>
		<td width="30%" class="en11pxb">维护简要步骤及数据规范</td>
		<td width="10%" class="en11pxb">上限</td>
		<td width="10%" class="en11pxb">下限</td>
		<td width="20%" class="en11pxb">数据记录和状态记录</td>
		<td width="20%" class="en11pxb">异常情况处理记录</td>
	</tr>
	
	<%  
		
			if(CommonUtil.isNotNull(job) && CommonUtil.isNotEmpty(job.getActionlist())) 
			{
	   			Iterator it = job.getActionlist().iterator(); 
	   			while(it.hasNext()) 
	   			{ 
		    		Action action = (Action)it.next(); 
	%>
			    	<tr height="25">
			    		<td class="en11px"><%=action.getActionId()%></td>
					  	<td class="en11px"><%=action.getActionName()%><%if(Constants.ACTION_DCOP_TYPE.equals(action.getActionType())) { %><font color="#ff0000"> DCOP</font><% } %></td>
					  	<td class="en11px">&nbsp;</td>
					  	<td class="en11px">&nbsp;</td>
					  	<td class="en11px">&nbsp;</td>
					  	<td class="en11px">&nbsp;</td>
				  	</tr>
		  			<% 
		  				if(CommonUtil.isNotEmpty(action.getItemlist())) 
		  			  	{
		  			%>
					 		<% 
					 			for(Iterator itemIt = action.getItemlist().iterator(); itemIt.hasNext(); ) 
					 		  	{ 
					              	ActionItem item = (ActionItem) itemIt.next(); 
					        %>
					              	<tr height="25"<% if(item.isOOS()) { %> bgcolor="#ff0000"<% } else { %> bgcolor="#DFE1EC"<% } %>>
						                <td class="en11px">&nbsp;</td>
						                <td class="en11px"><%=item.getItemName()%></td>
						                <td class="en11px"><% if(item.getItemType()==Constants.NUMBER_ITEM) { out.println(CommonUtil.checkDoubleNull(item.getItemUpperSpec()));}%></td>
						                <td class="en11px"><% if(item.getItemType()==Constants.NUMBER_ITEM) { out.println(CommonUtil.checkDoubleNull(item.getItemLowerSpec()));}%></td>
						                <td class="en11px">&nbsp;</td>
						                <td class="en11px">&nbsp;</td>
					               </tr>
					        <% } %>
					<%
						}
					%>
			<%
				}
			%>
		<%
			} 
		%>

</table>
<table width="100%" border="1" cellspacing="0" cellpadding="0">
	<tr height="25">
		<td width="100%" class="en11pxb">二、更换易耗件和备件记录：</td>
	</tr>
</table>
<table width="100%" border="1" cellspacing="0" cellpadding="0" height="30">
      <tr height="25" >
      	 <td width="20%" class="en11pxb">序号</td>
         <td width="30%" class="en11pxb">料号或备件号</td>
         <td width="30%" class="en11pxb">料品名称</td>
         <td width="20%" class="en11pxb">数量</td>
       </tr>
       <tr height="25" >
      	 <td width="20%" class="en11pxb">&nbsp;</td>
         <td width="30%" class="en11pxb">&nbsp;</td>
         <td width="30%" class="en11pxb">&nbsp;</td>
         <td width="20%" class="en11pxb">&nbsp;</td>
       </tr>
       <tr height="25" >
      	 <td width="20%" class="en11pxb">&nbsp;</td>
         <td width="30%" class="en11pxb">&nbsp;</td>
         <td width="30%" class="en11pxb">&nbsp;</td>
         <td width="20%" class="en11pxb">&nbsp;</td>
       </tr>
       <tr height="25" >
      	 <td width="20%" class="en11pxb">&nbsp;</td>
         <td width="30%" class="en11pxb">&nbsp;</td>
         <td width="30%" class="en11pxb">&nbsp;</td>
         <td width="20%" class="en11pxb">&nbsp;</td>
       </tr>
       
</table>
</div>
<!--endprint-->
<%
}
else
{
%>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>流程列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td width="5%" class="en11pxb">ID</td>
		 	<td width="40%" class="en11pxb">动作说明</td>
		 	<td width="25%" class="en11pxb">状态</td>
		 	<td width="30%" class="en11pxb">下一步动作</td>
    	</tr>
<%  if(CommonUtil.isNotEmpty(job.getActionlist())) {
   		Iterator it = job.getActionlist().iterator(); 
   		while(it.hasNext()) { 
	    	Action action = (Action)it.next(); %>
    	<tr bgcolor="#F0C000">
    		<td class="en11px"><%=action.getActionId()%></td>
		  	<td class="en11px"><%=action.getActionName()%></td>
		  	<td class="en11px"><table width="100%" border="0" cellpadding="0" cellspacing="0">
			<% if(CommonUtil.isNotEmpty(action.getStatusList())) {
				for(Iterator statusIt = action.getStatusList().iterator(); statusIt.hasNext(); ) { 
				ActionStatus status = (ActionStatus)statusIt.next();%>
              <tr>
                <td class="en11px"><%=status.getStatusName()%></td>
              </tr>
            <% } 
             } %>
            </table></td>
		  	<td class="en11px"><table width="100%" border="0" cellpadding="0" cellspacing="0">
			<% if(CommonUtil.isNotEmpty(action.getStatusList())) {
				for(Iterator statusIt = action.getStatusList().iterator(); statusIt.hasNext(); ) { 
				ActionStatus status = (ActionStatus)statusIt.next();%>
              <tr>
                <td class="en11px"><%=status.getNextActionId()%></td>
              </tr>
            <% } 
             } %>
            </table></td>
	  	</tr>
	  	<% if(CommonUtil.isNotEmpty(action.getItemlist())) { %>
	  	<tr>
	  		<td bgcolor="#ACD5C9"></td>
	  		<td colspan="3"><table width="100%" border="0" cellpadding="1" cellspacing="1">
              <tr bgcolor="#ACD5C9">
                <td class="en11pxb">项目名称</td>
                <td class="en11pxb">项目说明</td>
                <td class="en11pxb">样式</td>
                <td class="en11pxb">单位</td>
                <td class="en11pxb">上限</td>
                <td class="en11pxb">下限</td>
                <td class="en11pxb">预设值</td>
              </tr>
              <% for(Iterator itemIt = action.getItemlist().iterator(); itemIt.hasNext(); ) { 
              		ActionItem item = (ActionItem) itemIt.next(); %>
              <tr bgcolor="#DFE1EC">
                <td class="en11px"><%=item.getItemName()%></td>
                <td class="en11px"><%=item.getItemDescription()%></td>
                <td class="en11px"><%=WorkflowHelper.getItemTypeText(item.getItemType())%></td>
                <td class="en11px"><%=UtilFormatOut.checkNull(item.getItemUnit())%></td>
                <td class="en11px"><%if(item.getItemType()==Constants.NUMBER_ITEM){%><%=CommonUtil.checkDoubleNull(item.getItemUpperSpec())%><%}%></td>
                <td class="en11px"><%if(item.getItemType()==Constants.NUMBER_ITEM){%><%=CommonUtil.checkDoubleNull(item.getItemLowerSpec())%><%}%></td>
                <td class="en11px"><%=UtilFormatOut.checkNull(item.getDefaultValue())%></td>
              </tr>
              <% } %>
            </table></td>
	  	</tr>
<% 		}
		} 
	} %>
      </table>
      </fieldset></td>
  </tr>
</table>

<%
}
%>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
  	<td>
    	<% if(printFlag) { %>
    	<!--<a href="javascript:;" onClick="doPrint()">打印</a>-->
    	<a href="javascript:;" onClick="doPrint('printDiv')">打印</a>
    	<% } %>
		</td>
	 <td> 	
    	<ul class="button">
			<li><a class="button-text" href="javascript:javascript:history.back(-1)"><span>&nbsp;返回&nbsp;</span></a></li> 
	</ul></td>
	</tr>
</table>
</form>
<% } %>
