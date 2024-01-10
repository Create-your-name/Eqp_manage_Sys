<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="java.util.*"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%
		String EQUIPMENT_ID = "";
		String PM_NAME = "";
		String SCHEDULE_DATE = "";
		String PERIOD_INDEX = "";
		String SCHEDULE_DATE1 = "";
		if(pageContext.findAttribute("pm_List")!=null){
			List pm_List = (List)request.getAttribute("pm_List");
			if ((pm_List != null) && (pm_List.size()!= 0)) {
				Map map = (Map)pm_List.get(0);
				EQUIPMENT_ID = (String)(map.get("EQUIPMENT_ID"));
				PM_NAME = (String)(map.get("PERIOD_NAME"));
				SCHEDULE_DATE = (String)(map.get("SCHEDULE_DATE"));
				PERIOD_INDEX = (String)(map.get("PERIOD_INDEX"));
				SCHEDULE_DATE1 = (String)(map.get("SCHEDULE_DATE1"));
			}
		}
%>

<script language="javascript">
	function doSubmit(url){
		//alert(url);
		document.pmsDefaultSub.action = url;
		document.pmsDefaultSub.submit();	
	}
	
	function pmFormStart(){
		//loading();
		var actionURL='<ofbiz:url>/addPmFormEntry</ofbiz:url>?periodIndex=<%=PERIOD_INDEX%>&scheduleDate=<%=SCHEDULE_DATE1%>&eqpId=<%=EQUIPMENT_ID%>';
		document.pmsDefaultSub.action = actionURL;
		document.pmsDefaultSub.submit();
	}
</script>
<form action="<%=request.getContextPath()%>/control/pmsDefaultSub"  name="pmsDefaultSub" method="POST" id ="pmsDefaultSub" >
<table>
<tr>
<td width="100%"><fieldset><legend>保养</legend>
<table width="100%" border="0" cellspacing="1" cellpadding="2">
      <tr bgcolor="#DFE1EC">
      
        <td width="35%" align="right" class="en11pxb">
        <h4>EQ ID:</h4>
        </td>
        <td align="left" class="en11pxb">
        <h4>
          <%=EQUIPMENT_ID %>
        </h4>
        </td>
      </tr>
      <tr bgcolor="#DFE1EC">
        <td width="35%" align="right" class="en11pxb">
        <h4>保养类型:</h4>
        </td>
        <td align="left" class="en11pxb">
        <h4>
          <%=PM_NAME %>
        </h4>
        </td>
      </tr>
      <tr bgcolor="#DFE1EC">
        <td width="35%" align="right" class="en11pxb">
        <h4>保养日期:</h4>
        </td>
        <td align="left" class="en11pxb">
        <h4>
          <%=SCHEDULE_DATE %>
        </h4>
        </td>
      </tr>
      </table>
     </td>
</tr>
    </table>
<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><ul class="button">
				<li><a class="button-text" href="javascript:pmFormStart();"><span>&nbsp;确定&nbsp;</span></a></li> 
		</ul></td>
	    <td><ul class="button">
				<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/pmsDefault');"><span>&nbsp;取消&nbsp;</span></a></li> 
		</ul></td>
	</tr>
</table>
</form>