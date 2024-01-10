<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import ="org.ofbiz.base.util.UtilProperties" %>

<%
String time = UtilProperties.getPropertyValue("system", "buttomrefesh", "3");
String defalutEqpId = org.ofbiz.base.util.UtilFormatOut.checkNull((String)session.getAttribute(com.csmc.pms.webapp.util.SessionNames.CURRENT_EQP_ID_KEY));
System.out.print("defalutEqpId ===== " + defalutEqpId);
String currentPos = request.getParameter("currentPos");

String bottomType = (String)session.getAttribute(com.csmc.pms.webapp.util.SessionNames.CURRENT_BOTTOM_TYPE_KEY);
String panelLink = "";
if ("EAP".equalsIgnoreCase(bottomType))
{
	panelLink="eapConsoleEntry";
}
else
{
	panelLink="optConsoleEntry";
}
%>
<script>
var limit="<%=time%>"
function beginrefresh(){
if (limit==1)
	window.location='<ofbiz:url>/getEQPList</ofbiz:url>?currentPos='+document.body.scrollTop;
//	window.location.reload()
else{
	limit-=1
	setTimeout("beginrefresh()",1000)
	}
}
//window.onload=beginrefresh

function beginscroll() {
	//window.location='<ofbiz:url>/getEQPList</ofbiz:url>?currentPos='+document.body.scrollTop;
}
//window.onscroll=beginscroll


</script>


<html>
<head>
<title>bottom</title>

<head>
<link href="<%=request.getContextPath()%>/images/default.css" rel="stylesheet" type="text/css">
<body leftmargin="0" topmargin="0">
<table width="100%"  border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="10%" class="button-text"><a href="<%=request.getContextPath()%>/control/getEQPList">刷新</a></td>
  </tr>
</table>
<table width="100%" height="60"  border="0" cellpadding="0" cellspacing="1" bgcolor="#FFFFFF">
	<%
	java.util.Map locEqpMap = (java.util.Map)request.getAttribute("cur_eqp_panel_eqp_map");
	if (locEqpMap != null) {
	%>
	<tr>
		<td width="45%">&nbsp;</td>
    	<td>
    		<table width="100%" border="0" cellspacing="0" cellpadding="0">
    			<%
    			int i = 0;
    			java.util.Iterator locEqpItor =  locEqpMap.keySet().iterator();
    			while (locEqpItor.hasNext()) {
    				String locId = (String)locEqpItor.next();
    				java.util.List eqpList = (java.util.List)locEqpMap.get(locId);
    			%>
    			<tr>
        			<%	int lastRow = 0;
        				for (int j = 0; j < eqpList.size(); j++) {
        				com.csmc.pms.webapp.eqp.model.EqpModel model = (com.csmc.pms.webapp.eqp.model.EqpModel)eqpList.get(j);
	         			String eqpId = model.getEqpId();

	         			if ((i == 0) && (j == 0) && ("".equalsIgnoreCase(defalutEqpId))) {
	         			%>
	         			<script language="javascript">
	         				parent.document.getElementById("mainFrame").src="<%=request.getContextPath()%>/control/optConsoleEntry?eqpId=<%=eqpId%>";
	         			</script>
	         			<%
	         			}
	         			String eqpStatus = model.getEqpStatus();
	         			String showFlag = model.getShowFlag();
	         			//String waitNum = model.getWaitNum();
	         			if (j == 0) {
	         			%>
	         			<tr>
	         			<td width="1%" class="title-en" nowrap>
        					<%=locId%>
        				</td>
        				<% } else if ((j%10 == 0)) {
	         			%>
	         			<tr>
	         			<td>
	         			</td>
	         			<%
	         			}
        			%>
        			<td width="1%">
	        			<table width="100%" border="0" cellspacing="0" cellpadding="0">
	        				<tr>
              					<td align="center">
              						<a href="javascript:click('<%=eqpId%>');" class="button-text">
              						<%
		          						if ("01".equalsIgnoreCase(eqpStatus)) {
		          					%>
										<span class="eqpStyle_01"><%=eqpId%></span>
									<%
										} else if ("02".equalsIgnoreCase(eqpStatus)) {
		          					%>
										<span class="eqpStyle_02"><%=eqpId%></span>
									<%
										} else if ("03".equalsIgnoreCase(eqpStatus)) {
					          		%>
										<span class="eqpStyle_03"><%=eqpId%></span>
									<%
										} else if ("04".equalsIgnoreCase(eqpStatus)) {
					          		%>
										<span class="eqpStyle_04"><%=eqpId%></span>
					          		<%
					          			} else if ("05".equalsIgnoreCase(eqpStatus)) {
					          		%>
					          			<span class="eqpStyle_05"><%=eqpId%></span>
					          		<%
					          			} else if ("06".equalsIgnoreCase(eqpStatus)) {
					          		%>
					          			<span class="eqpStyle_06"><%=eqpId%></span>
					          		<%
					          			} else if ("07".equalsIgnoreCase(eqpStatus)) {
					          		%>
					          			<span class="eqpStyle_07"><%=eqpId%></span>
					          		<%
					          			} else {
					          		%>
										<span class="eqpStyle_07"><%=eqpId%></span>
									<%
										}
									%>
              						</a>
              					</td>
            				</tr>

        				</table>
        			</td>

        			<%
        				if ((j == lastRow + 9)) {
        					lastRow = j + 1;
	         			%>
	         			</tr>
	         			<%
	         			}
        				}
        			%>
	         	</tr>
            	<%
            		i++;
            	}
            	%>
        	</table>
        </td>
        <td width="45%">&nbsp;</td>
    </tr>
	 <%
    	}
    %>

  		<tr>
          	<td width="70%" colspan="3" nowrap>
          	<FONT size=2><STRONG><FONT style="BACKGROUND-COLOR: #ccffff">
          	<FONT color=#00ff00><FONT face=宋体 color=#000000>设备状态对应：</FONT>01--PROD&nbsp; </FONT>
          	<FONT color=#ffff00>02--IDLE&nbsp; </FONT>
          	<FONT color=#ff0000>03--DOWN&nbsp; </FONT>
          	<FONT color=#dda0dd>04--PM/换备件类</FONT>
          	<FONT color=#ffa500>05--设备/动力改造类</FONT>
          	<FONT color=#ff69b4>06--定期QC</FONT>
          	<FONT color=#00ffff>07--工艺开发类</FONT>
          	</FONT></STRONG></FONT>
          	</td>
        	<td width="30%" align ="right">
        		<table border="0" cellspacing="0" cellpadding="0">
	      			<tr><td>&nbsp;</td></tr>
          			<tr>
            			<td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
            			<td background="<%=request.getContextPath()%>/images/button_bg.gif" ><a href="javascript:doOpen('<ofbiz:url>/setupOperatorEMB</ofbiz:url>')" class="button-text">More</a></td>
            			<td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
          			</tr>
        		</table>
        	</td>
	   </tr>



</table>
</body>
</html>

<script language="javascript">
function click(val){
	//parent.document.getElementById("mainFrame").src="<%=request.getContextPath()%>/control/optConsoleEntry?eqpId="+val;
	//alert(document.body.scrollTop);
	parent.document.getElementById("mainFrame").Document._main_template_frame.action="<%=request.getContextPath()%>/control/<%=panelLink%>?eqpId="+val;
	parent.document.getElementById("mainFrame").Document._main_template_frame.submit();
}

function doOpen(url) {
	window.open(url,"",
	"top=100,left=100,width=820,height=550,title=,channelmode=0," +
	"directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
	"scrollbars=1,status=1,titlebar=0,toolbar=no");
}
window.scroll(0, <%=currentPos%>);
</script>
