<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*"%>
<%@ page import="com.csmc.pms.webapp.util.*"%>
<%@ page import="org.ofbiz.base.util.*"%>
<%@ include file="../yui-ext/ext-comdlg.jsp"%>

<%
try {
%>
<!-- yui page script-->
<script language="javascript">
	//功能弹出页
	function submitCheck(obj,submitIndex){
		if (submitIndex == 1){
			//同意
		}else{
			//拒绝
		}
	}

	function viewFlowStructure(tempIndex) {
		document.location="<ofbiz:url>/viewFlowStructure</ofbiz:url>?submitFlag=true&tempIndex=" + tempIndex;
	}

	function viewHistFlow(histIndex) {
		document.location="<ofbiz:url>/viewHistFlow</ofbiz:url>?histIndex="+histIndex;
	}

	//动作项目变更
	function viewFlowActionItemApply(objectIndex, submitIndex) {
		document.location="<ofbiz:url>/viewFlowActionItemApply</ofbiz:url>?isTemp=Y&objectIndex=" + objectIndex + "&submitIndex=" + submitIndex;
	}
	function viewFlowActionItemHist(objectIndex, submitIndex)	{
		document.location="<ofbiz:url>/viewFlowActionItemApply</ofbiz:url>?isTemp=N&objectIndex=" + objectIndex + "&submitIndex=" + submitIndex;
	}
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td>
      <fieldset>
      <legend>送签列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
            <td class="en11pxb">No.</td>
            <td class="en11pxb">事件</td>
            <td class="en11pxb">说明</td>
            <td class="en11pxb">类型</td>
            <td class="en11pxb">申请日期</td>
            <td class="en11pxb">签核日期</td>
            <td class="en11pxb">签核状态</td>
            <td class="en11pxb">待签人员</td>
        </tr>

        <%
    	List flowList = (List)request.getAttribute("flowList");
    	if ((flowList != null) && (flowList.size()!= 0)) {
        	for (int i = 0 ; i <= flowList.size()-1 ; i++){
        		Map map = (Map)flowList.get(i);
        		String submitIndex = (String) map.get("SUBMIT_INDEX");
        		String objectIndex = (String) map.get("OBJECT_INDEX");
        		String flowJobHistIndex = (String) map.get("FLOW_JOB_HIST_INDEX");
        		String object_description = (String)(map.get("OBJECT_DESCRIPTION"));
        		String object_name = (String)(map.get("OBJECT_NAME"));
        		String object = (String)(map.get("OBJECT"));
        		String type = (String)(map.get("TYPE"));
        		String create_time = (String)(map.get("CREATE_TIME"));
        		String update_time = (String) map.get("UPDATE_TIME");
        		String owner = UtilFormatOut.checkNull((String)(map.get("OWNER")));
        		String status = UtilFormatOut.checkNull((String)(map.get("STATUS")));
        		String ownerProcess = UtilFormatOut.checkNull((String)(map.get("OWNER_PROCESS")));
        		String status_process = UtilFormatOut.checkNull((String)(map.get("STATUS_PROCESS")));

        		if (type.equals("INSERT")){
        			type = "新增";
        		}else if (type.equals("MODIFY")){
        			type = "修改";
        		}else if (type.equals("DELETE")){
        			type = "删除";
        		}

        		if (status.equals("APPROVED")){
        			status = "同意";
        		}else if (status.equals("SUBMITED")){
        			status = "签核";
        		}else if (status.equals("REJECTED")){
        			status = "拒绝";
        		}

        		if (status_process.equals("APPROVED")){
    		        status_process = "同意";
                } else if (status_process.equals("SUBMITED")){
                    status_process = "签核";
                } else if (status_process.equals("REJECTED")){
                    status_process = "拒绝";
                }

                String ownerShow = owner;
        		String statusShow = status;
        		if (object.equals(Constants.SUBMIT_FLOW)){
        			if (!status.equals("签核")){
        			    ownerShow = "";
        		    }
        		} else if (object.equals(Constants.SUBMIT_FLOW_ACTION_ITEM) || object.equals(Constants.SUBMIT_PM_DELAY)){
        		    statusShow = status + " / " + status_process;

        		    if (statusShow.indexOf("签核") >= 0 && statusShow.indexOf("拒绝") == -1) {
        		        ownerShow = owner + " / " + ownerProcess;
        		    } else {
        		        ownerShow = "";
        		    }
                } else if (object.equals(Constants.SUBMIT_FOLLOW)){
                    if (status.equals("签核")) {
        			    ownerShow = owner;
        			    statusShow = status;
        		    } else if (status_process.equals("签核")) {
        		        ownerShow = ownerProcess;
        		        statusShow = status_process;
        		    } else {
        		        ownerShow = "";
        		    }
                }

        		%>
        			<tr bgcolor="#DFE1EC">
        			    <td class="en11px"><%=i+1%></td>
        				<td class="en11px"><%=object_description%></td>
        				<td class="en11px"><%=object_name%></td>
        				<td class="en11px"><%=type%></td>
        				<td class="en11px">
        				    <%
        				    if (object.equals(Constants.SUBMIT_FLOW_ACTION_ITEM)) {
        				        if (statusShow.equals("同意 / 同意")) {
        				    %>
        				            <img src="<%=request.getContextPath()%>/pms/images/copy_16.gif" style="cursor:hand" onclick="viewFlowActionItemHist(<%=objectIndex%>, <%=submitIndex%>)" />

        				    <%  }else{%>
        				            <img src="<%=request.getContextPath()%>/pms/images/copy_16.gif" style="cursor:hand" onclick="viewFlowActionItemApply(<%=objectIndex%>, <%=submitIndex%>)" />
        				    <%  }
        				    } else if (object.equals(Constants.SUBMIT_FLOW)) {
        				        if (status.equals("拒绝") || status.equals("签核")) {
        				    %>
	        				        <img src="<%=request.getContextPath()%>/pms/images/copy_16.gif" style="cursor:hand" onclick="viewFlowStructure(<%=objectIndex%>)" />
	        				<%  } else if (null != flowJobHistIndex && !"".equals(flowJobHistIndex)) {%>
	        				        <img src="<%=request.getContextPath()%>/pms/images/copy_16.gif" style="cursor:hand" onclick="viewHistFlow(<%=flowJobHistIndex%>)" />
	        				<%  }
        				    }%>
        				    <%=create_time%>
        				</td>

        				<td class="en11px">
        				    <%if (status.startsWith("拒绝") || status.startsWith("同意")) {%>
        				        <%=update_time%>
        				    <%}%>
        				</td>
        				<td class="en11px"><%=statusShow%></td>
        				<td class="en11px"><%=ownerShow%></td>
        			</tr>
        		<%
        	}
    	}
        %>

      </table>
      </fieldset>
    </td>
  </tr>
</table>

<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">主管签核作业</div>
    <div class="x-dlg-bd">

    </div>
</div>

<%
} catch(Exception e) {
    out.print(e);
}
%>