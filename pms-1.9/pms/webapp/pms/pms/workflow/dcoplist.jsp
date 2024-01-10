<%@ page contentType='text/html; charset=gb2312'%>
<%@ page import="java.util.*"%>
<%@ page import="org.ofbiz.entity.*" %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ taglib uri="displaytag" prefix="display" %>
<%@ taglib uri='regions' prefix='region' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<script language="javascript">
	window.resizeTo(500, 420);
</script>
<html><head>
<i18n:bundle baseName="resource" id="bundle"/>
<title>Dcop List</title>
<script language='javascript' src='<%=request.getContextPath()%>/images/calendar1.js' type='text/javascript'></script>
<script language='javascript' src='<%=request.getContextPath()%>/function/function.js' type='text/javascript'></script>
<link rel='stylesheet' href='<%=request.getContextPath()%>/images/maincss.css' type='text/css'>
<link rel='stylesheet' href='<%=request.getContextPath()%>/images/tabstyles.css' type='text/css'>
<link href="<%=request.getContextPath()%>/images/default.css" rel="stylesheet" type="text/css">
</head>
<body>
<form name="dcopListForm" action="<%=request.getContextPath()%>/control/queryDcopList" method="post">
<fieldset> <legend><span class="title-en">查询DCOP列表</span></legend>
	<table width="100%" border="0" cellspacing="1" cellpadding="0">
        <tr bgcolor="#DFE1EC">
          <td width="12%" class="en11pxb"><span class="title-en">DCOP</span></td>
          <td width="88%">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td height="26" class="en11pxb" width="16%">
            		<input name="actionName" type="text" class="input" value="<%=(String)request.getParameter("actionName")%>">
          		</td>
                <td width="99%"><table height="35" border="0" cellpadding="0" cellspacing="0">
                  <tr>
                    <td>
					<table border="0" cellspacing="0" cellpadding="0">
                        <tr>
						  <td width="40"></td>
                          <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                          <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="javascript:doSubmit();" class="button-text">确定</a></td>
                          <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
                        </tr>
                    </table></td>
                    <td>&nbsp;</td>
                    <td><table border="0" cellspacing="0" cellpadding="0">
                        <tr>
                          <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                          <td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="javascript:doReset();" class="button-text">重置</a></td>
                          <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
                        </tr>
                    </table></td>
                  </tr>
                </table> </td>
              </tr>
            </table>
          </td>
      </tr>
  </table>
	<display:table name="requestScope.dcoplist" id="dcoplistIt" defaultsort="1" pagesize="15" defaultorder="ascending"  border="0" cellspacing="1" cellpadding="0">
	<%
		String dcopId = "";
		String dcopDesc = "";

		if(pageContext.findAttribute("dcoplistIt")!=null) {
			Map dcopInfo= (Map)pageContext.findAttribute("dcoplistIt");
			if(dcopInfo != null) {
				dcopId = (String)dcopInfo.get("DCOP_ID");
				dcopDesc = (String)dcopInfo.get("DESCRIPTION");
			}
	%>
		<display:column title="DCOP名" class="tablelist" headerClass="tabletitle"  >
	 		<a href="javascript:doSelect('<%=dcopId%>','<%=dcopDesc%>');">
				<%=dcopId%></a>
		</display:column>
		<display:column title="DCOP描述" class="tablelist" headerClass="tabletitle"  >
				<%=UtilFormatOut.checkNull(dcopDesc)%>
		</display:column>
	<% } %>
		<display:setProperty name="basic.empty.showtable" value="true" />
 		<display:setProperty name="basic.msg.empty_list" value=" " />
		<display:setProperty name="paging.banner.include_first_last" value="false" />
		<display:setProperty name="paging.banner.group_size" value="6" />
		<display:setProperty name="paging.banner.placement" value="bottom" />
		<display:setProperty name="paging.banner.some_items_found" value="" />
		<display:setProperty name="paging.banner.one_item_found" value="" />
		<display:setProperty name="paging.banner.no_items_found" value="" />
		<display:setProperty name="paging.banner.all_items_found" value="" />
		<display:setProperty name="basic.msg.empty_list_row" value=""/>
		<display:setProperty name="paging.banner.onepage" value=""/>
		<display:setProperty name="paging.banner.first" value="<span class=\"pagelinks\">
					<font face=Webdings>7</font><font face=Webdings>3</font>
					{0}<a href=\"{3}\"><font face=Webdings>4</font></a>
					<a href=\"{4}\"><font face=Webdings>8</font></a></span>"/>
		<display:setProperty name="paging.banner.full" value="<span class=\"pagelinks\">
					<a href=\"{1}\"><font face=Webdings>7</font></a>
					<a href=\"{2}\"><font face=Webdings>3</font></a>
					{0}<a href=\"{3}\"><font face=Webdings>4</font></a>
					<a href=\"{4}\"><font face=Webdings>8</font></a></span>"/>
		<display:setProperty name="paging.banner.last" value="<span class=\"pagelinks\">
					<a href=\"{1}\"><font face=Webdings>7</font></a>
					<a href=\"{2}\"><font face=Webdings>3</font></a>
					{0}<font face=Webdings>4</font><font face=Webdings>8</font></span>"/>
	</display:table>
	<table width="100%" border="0" cellspacing="1" cellpadding="0">
        <tr>
		 <td align="right"><a href="javascript:doClose();" class="close-text">关闭窗口</a></td>
		</tr>
  </table>
</fieldset>
</FORM>
<script language="JavaScript">
function doSelect(actionName, actionDesc) {
	window.opener.document.getElementById("actionName").value=actionName;
	window.opener.document.getElementById("actionDescription").value=actionDesc;
    window.close();
}
function doClose() {
	window.close();
}
function doReset() {
	document.dcopListForm.actionName.value = "";
}
function doSubmit(){
	document.dcopListForm.submit();
}
</script>
</body>
</html>