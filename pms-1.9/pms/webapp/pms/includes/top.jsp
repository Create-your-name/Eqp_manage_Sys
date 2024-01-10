<%@page contentType='text/html; charset=gb2312'%>
<%@page  import="org.ofbiz.base.util.*, java.util.* ,com.csmc.pms.webapp.util.*,org.ofbiz.entity.*"%>
<%@page import="com.csmc.pms.webapp.util.MiscUtils" %>
<html>
<head>
<title>CSMC GUI</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="/pms/images/default.css" rel="stylesheet" type="text/css">
<style type="text/css">
<!--
body {
	margin-left: 0px;
	margin-top: 0px;
}
-->
</style></head>
<%
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

	String showFlag = (String)session.getAttribute("isOperatorConsole");

%>

<body bgcolor="#ffffff">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="98%" class="top-bg"><img src="/pms/images/spacer.gif" height="9"></td>
    <td width="1%"><img name="top_r1_c2" src="/pms/images/top_r1_c2.gif" border="0" alt=""></td>
    <td width="1%"><img src="/pms/images/top_r1_c3.gif" alt="" name="top_r1_c3" border="0" usemap="#top_r1_c3Map"></td>
  </tr>
  <tr>
    <td height="18" colspan="3" class="menu-bg">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="33%" class="en11pxb-white" align="center">
          <font color="#FFFF00"><%=userLogin.get("userLoginId")%></font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <%
	      if("true".equals(showFlag)){
	          java.util.Map map = (java.util.Map)session.getAttribute(com.csmc.pms.webapp.util.SessionNames.CURRENT_EQP_INFO_KEY);
	          if(map!=null){
	          	out.print(org.ofbiz.base.util.UtilFormatOut.checkNull((String)map.get("locationId")));
	          }
          }

          %>
          </td>
          <td width="33%" class="en11pxb-white" align="center"><%
   	      if("true".equals(showFlag)){
             String eqpId = (String)session.getAttribute(com.csmc.pms.webapp.util.SessionNames.CURRENT_EQP_ID_KEY);
          	out.print(org.ofbiz.base.util.UtilFormatOut.checkNull(eqpId));
          	if(eqpId!=null && !"".equals(eqpId)){
	          	try{
						GenericDelegator delegator =
							(GenericDelegator) request.getAttribute("delegator");
	          			Map	result = TPServiceHandle.runTPService(delegator,userLogin,UtilMisc.toMap("eqpid", eqpId),Constants.EQP_INFO_QUERY);
	          			session.setAttribute(SessionNames.CURRENT_EQP_STATUS_KEY,result.get("status"));
	          			out.print("("+ result.get("status")+ ")");
	          	}catch(Exception ex){
	          		Debug.logError(ex,"com.fa.top.jsp");
	          	}
          	}
      	}
          %>
          </td>
          <td width="33%" align="right">
            <!--<a href="javascript:changePwd()"><img src="/pms/images/change_password_en.gif" width="138" height="18" border="0"></a>-->
          </td>
        </tr>
      </table></td>
  </tr>
</table>
<map name="top_r1_c3Map">
  <area shape="rect" coords="12,0,82,13" href="javascript:doLogout()">
</map>
</body>
</html>
<SCRIPT LANGUAGE="JavaScript">
	function changePwd(){
		OpenUrl('changePwd')
	}
	function OpenUrl(Url){
		window.open('<%=request.getContextPath()%>/control/'+ Url, 'newwindow', 'height=250, width=400, top=150,left=250, toolbar=no, menubar=no, scrollbars=yes, resizable=yes,location=no, status=no')
	}

	function doLogout(){
		var w;
		w=window.open("","csmcPmsMainFrame");
		w.close();
		w=window.open("<%=request.getContextPath()%>/control/logout","csmcGuiLogin","width="+(screen.availWidth)+",height="+(screen.availHeight)+",toolbar=0,location=0,directories=0,status=2,menubar=0,scrollbars=2,resizable=2,copyhistory=0");
		w.moveTo(-4,-4);
		w.resizeTo(screen.availWidth+8, screen.availHeight+8);
	}


</SCRIPT>