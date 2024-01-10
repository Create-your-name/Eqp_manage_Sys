<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="com.csmc.pms.webapp.workflow.help.*"%>
<%@ include file="../yui-ext/ext-comdlg.jsp"%>


<!-- yui page script-->
<script language="javascript">

	//签核功能弹出页
	function submitCheck(obj,object){
		//Ext.get('comment').dom.value=object;
		Ext.get('submitIndex').dom.value=object;
		var url='<ofbiz:url>/queryFlowLeadCheck</ofbiz:url>?submitIndex='+object+'&status=1';
		//alert('dfasfas');
		extDlg.showEditDialog(obj,url);
	}


	//改变弹出框大小width,height
	extDlg.dlgInit('500','300');

	function editDocument(item1){
		var url='<ofbiz:url>/queryLeadCheckNo</ofbiz:url>?submitIndex='+item1;
		document.flowLeadCheck.action = url;
		document.flowLeadCheck.submit();
	}

	function showFlowDetail(tempIndex) {
		document.location="<ofbiz:url>/viewFlowStructure</ofbiz:url>?submitFlag=true&tempIndex=" + tempIndex;
	}

	//动作项目变更
	function viewFlowActionItemApply(objectIndex) {
		document.location="<ofbiz:url>/viewFlowActionItemApply</ofbiz:url>?isTemp=Y&objectIndex=" + objectIndex;
	}

	//全选
	function checkAll(){
		var i = document.forms['flowLeadCheck'].count.value;
		if (i < 100){
				for(var loop = 0;loop < i;loop++) {
        			if(document.forms['flowLeadCheck'].check_all.checked) {
        				document.getElementById("check_" + loop).checked = true;
        			} else {
        				document.getElementById("check_" + loop).checked = false;
        			}
        		}
		}else{
				for(var loop = 0;loop < 100;loop++) {
        			if(document.forms['flowLeadCheck'].check_all.checked) {
        				document.getElementById("check_" + loop).checked = true;
        			} else {
        				document.getElementById("check_" + loop).checked = false;
        			}
        		}
		}

	}

	function ALLSubmit(obj){
		var i = document.forms['flowLeadCheck'].count.value;
		var submitList = "";

		for(var loop = 0;loop < i;loop++) {
			if(document.getElementById("check_" + loop).checked) {
				submitList += "," + document.getElementById("submitIndex_" + loop).value;
			}
		}
		if (submitList.length > 0){
			submitList = submitList.substr(1);
		}else{
			return;
		}
		//alert(submitList);
		Ext.get('submitIndex').dom.value=submitList;
		var url='<ofbiz:url>/queryFlowLeadCheck</ofbiz:url>?submitIndex='+submitList+'&status=1';
		extDlg.showEditDialog(obj,url);
	}

	//进入表单新建页面
	function intoPmForm(index){
		loading();
		var actionURL='<ofbiz:url>/overPmFormView</ofbiz:url>?functionType=3&pmIndex='+index;
		document.location.href=actionURL;
	}
	
	function intoTsForm(index){
		loading();
		var actionURL='<ofbiz:url>/overAbnormalFormView</ofbiz:url>?functionType=3&abnormalIndex='+index;
		document.location.href=actionURL;
	}

	function commentSuccess() {}
	function commentFailure() {}
	function checkForm(){return "";}
</script>

<form  name="flowLeadCheck" method="POST" id ="flowLeadCheck" >
<%
	response.getWriter().write("<font color=\"red\">" + "全选只能选择100个进行签核!" + "</font>");
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td> <fieldset>
      <legend>主管签核</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
        	<td   class="en11pxb"><input type="checkbox" id="check_all" onclick="checkAll();" checked></td>
          <td   class="en11pxb">事件</td>
          <td   class="en11pxb">说明</td>
          <td   class="en11pxb">类型</td>
          <td   class="en11pxb">申请日期</td>
          <td   class="en11pxb">申请人</td>
          <td   class="en11pxb">备注</td>
        </tr>
        <%
        	int i = 0;
        	List flowList = (List)request.getAttribute("flowList");
        	if ((flowList != null) && (flowList.size()!= 0)) {
	        	for (i = 0 ; i <= flowList.size()-1 ; i++){
	        		Map map = (Map)flowList.get(i);
	        		String object = (String)(map.get("OBJECT"));
	        		String object_description = (String)(map.get("OBJECT_DESCRIPTION"));
	        		String object_name = (String)(map.get("OBJECT_NAME"));
	        		String type = WorkflowHelper.getSubmitTypeText((String) map.get("TYPE"));
	        		String create_time = (String)(map.get("CREATE_TIME"));
	        		String creator_name = (String)(map.get("CREATOR_NAME"));
	        		String submit_index = (String)(map.get("SUBMIT_INDEX"));
	        		String objectIndex = String.valueOf(map.get("OBJECT_INDEX"));
	        		%>
	        			<tr bgcolor="#DFE1EC">
	        				<td class="en11px"><input type="hidden"  name="submitIndex_<%=i%>" id="submitIndex_<%=i%>"  value='<%=submit_index%>'>
	        					<input type="checkbox" name="check_<%=i%>" id="check_<%=i%>" checked>
	        				</td>
	        				<td class="en11px"><%=object_description%></td>

                            <td class="en11px">
	        				<%if(Constants.SUBMIT_FLOW.equals(object)) {%>
        				        <a href="#" onclick="showFlowDetail('<%=objectIndex%>')">
        				            <%=object_name %>
        				        </a>
	        				<%} else if(Constants.SUBMIT_FOLLOW.equals(object)) {%>
        				        <a href="#" onclick="editDocument('<%=submit_index%>')">
        				            <%=object_name %>
        				        </a>
	        				<%} else if(Constants.SUBMIT_FLOW_ACTION_ITEM.equals(object)) {%>
	        				    <!-- 显示动作项目 -->
        				        <a href="#" onclick="viewFlowActionItemApply('<%=objectIndex%>')">
        				            <%=object_name%>
        				        </a>
	        				<%} else if(Constants.SUBMIT_PM_DELAY.equals(object)) {%>
	        				    <!-- 显示保养延期申请 -->
	        				    <%=object_name%>
	        				<%} else if("PMFORM".equals(object)) { %>
								<a href="#" onclick="intoPmForm('<%=objectIndex%>')"><%=object_name %></a>
							<% } else if("TSFORM".equals(object)) { %>
								<a href="#" onclick="intoTsForm('<%=objectIndex%>')"><%=object_name%></a>
							<% } else if("PM_PARTS_USE".equals(object)) { %>
								<a href="#" onclick="intoPmForm('<%=objectIndex%>')"><%=object_name%></a>
							<% } else if("TS_PARTS_USE".equals(object)) { %>
								<a href="#" onclick="intoTsForm('<%=objectIndex%>')"><%=object_name%></a>
							<%}%>
	        				</td>

	        				<td class="en11px"><%=type%></td>
	        				<td class="en11px"><%=create_time %></td>
	        				<td class="en11px"><%=creator_name%></td>
	        				<td class="en11px"><a href="#" onclick="submitCheck(this,'<%=submit_index%>')">签核</a>&nbsp;&nbsp;</td>
	        			</tr>
	        		<%
	        	}
        	}
         %>

      </table>
      </fieldset><input type="hidden"  name="count" id="count"  value='<%=i%>'>
       </td>
  </tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
  	<td><ul class="button">
		<li><a class="button-text" href="#" onclick="ALLSubmit(this)"><span>&nbsp;整批签核&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>
</form>

<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">主管签核作业</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="主管签核">
            <div class="inner-tab" id="x-form">
            <form action="<%=request.getContextPath()%>/control/queryFlowLeadCheckSubmit" method="post" id="flowLeadCheck1" onsubmit="return false;">
                <label for="name"><small>备注</small></label>
                <input class="textinput" type="text" name="comment" id="comment" value="" size="22" tabindex="1" />
                <input class="textinput" type="hidden" name="submitIndex" id="submitIndex" value=""/>

                <br>
                <input type="radio" name="status" value="APPROVED" checked>
                <small>同意</small>&nbsp;
        	   	<input type="radio" name="status" value="REJECTED">
        	   	<small>拒绝</small>
		    </form>
            </div>
        </div>
    </div>
    <div class="x-dlg-ft">
        <div id="dlg-msg">
            <span id="post-error" class="posting-msg"><img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" />&nbsp;<span id="post-error-msg"></span></span>
        </div>
    </div>
</div>
