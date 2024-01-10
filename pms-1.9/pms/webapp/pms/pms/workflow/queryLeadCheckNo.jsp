<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>


<%
	String dept = (String) request.getAttribute("DEPT");
	String section = (String) request.getAttribute("SECTION");
	List ListFollow = (List) request.getAttribute("ListFollow");
	List followItemList = (List) request.getAttribute("ITEM_LIST");

	String followName = "";
	String purpose = "";
	String object = "";
	String creator = "";
	if ((ListFollow != null) && (ListFollow.size()!= 0)) {
		Map map = (Map)ListFollow.get(0);
		followName = UtilFormatOut.checkNull((String)(map.get("FOLLOW_NAME")));
		purpose = UtilFormatOut.checkNull((String)(map.get("PURPOSE")));
		object = UtilFormatOut.checkNull((String)(map.get("OBJECT")));
		creator = UtilFormatOut.checkNull((String)(map.get("CREATOR")));
	}
%>

<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	//数据合法性校验
	function checkForm(){
		var content = Ext.get('itemContent').dom.value;
		if(content==""){
			return "问题追踪内容不能为空";
		}
		return "";
	}

	//改变弹出框大小width,height
	extDlg.dlgInit('500','300');

	//新增功能弹出页
	function addFollowItem(obj){
		Ext.get('itemContent').dom.value="";
        Ext.get('itemIndex').dom.value="";
        Ext.get('uploadIndex').dom.value="";
		extDlg.showAddDialog(obj);
	}

	//修改功能弹出页
	function editFollowItem(obj,itemIndex){
		Ext.get('itemIndex').dom.value=itemIndex;
		Ext.get('uploadIndex').dom.value="";
		var url='<ofbiz:url>/intoEditfollowItem</ofbiz:url>?itemIndex='+itemIndex;
		extDlg.showEditDialog(obj,url);
	}

	//修改时远程调用成功后给页面赋值
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('itemContent').dom.value=result.itemContent;
		}
	}

	function doSubmit(url,status) {
		document.queryLeadCheckNo.action = url;
		document.queryLeadCheckNo.submit();
	}

</script>
<form  name="queryLeadCheckNo" method="POST">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>问题追踪查询参数</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
	      <td width="15%" bgcolor="#ACD5C9" class="en11pxb">问题追踪名称</td>
	      <td width="35%" class="en11px"><%=followName%></td>
	      <td width="15%" bgcolor="#ACD5C9" class="en11pxb">目的</td>
          <td width="35%" class="en11px"><%=purpose%></td>
        </tr>
        <tr bgcolor="#DFE1EC">
	        <td bgcolor="#ACD5C9" class="en11pxb">部门</td>
	    	<td class="en11px"><%=dept%></td>
	    	<td bgcolor="#ACD5C9" class="en11pxb">课别</td>
	    	<td class="en11px"><%=section%></td>
        </tr>
         <tr bgcolor="#DFE1EC">
	        <td bgcolor="#ACD5C9" class="en11pxb">EQUIPMENT</td>
	    	<td class="en11px"><%=object%></td>
	    	<td bgcolor="#ACD5C9" class="en11pxb">创建者</td>
	    	<td class="en11px"><%=creator%></td>
        </tr>

      </table>
      </fieldset></td>
  </tr>
</table>

<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>问题追踪过程列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="8%" class="en11pxb">步骤号</td>
          <td width="27%" class="en11pxb">追踪步骤内容</td>
          <td width="24%" class="en11pxb">附加档案</td>
          <td width="18%" class="en11pxb">监测日期</td>
          <td width="18%" class="en11pxb">修改时间</td>
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
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/flowLeadCheck','2')"><span>&nbsp;返回&nbsp;</span></a></li>
	</ul></td>
	</tr>
</table>
</form>