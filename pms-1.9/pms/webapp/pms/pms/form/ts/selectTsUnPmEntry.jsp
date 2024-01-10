<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%
	String eqpId = (String) request.getAttribute("eqpId");
	List equipmentScheduleList = (List) request.getAttribute("equipmentScheduleList");
%>
<!-- yui page script-->
<script language="javascript">

	//修改删除保养计划
	function selectTsUnPm(){
	    if(!document.getElementsByName("pmSelect")[0].checked){
	        alert("第一项为最大的保养，必须勾选");
	        return false;
	    }

	    var modifySchedule = "'" + document.getElementsByName("scheduleIndex")[0].value + "'";
	    var deleteSchedule = "''";
	    var periods = document.getElementsByName("periodIndex")[0].value;
	    var periodIndex = "";

        for (var i=1;i<document.getElementsByName("pmSelect").length;i++){
    		if (document.getElementsByName("pmSelect")[i].checked){
    		    periodIndex = document.getElementsByName("periodIndex")[i].value;
    		    if (periods.indexOf(periodIndex) >= 0) {
    		        alert("同一个保养类型只能勾选一个");
    		        return false;
    		    } else {
    		        periods = periods + "," + periodIndex;
    		    }

    		    modifySchedule = modifySchedule + ",'" + document.getElementsByName("scheduleIndex")[i].value + "'";

    		} else {
    		    deleteSchedule = deleteSchedule + ",'" + document.getElementsByName("scheduleIndex")[i].value + "'";
    		}
    	}

    	document.pmForm.modifySchedule.value = modifySchedule;
    	document.pmForm.deleteSchedule.value = deleteSchedule;

    	if (confirm("勾选项将修改为今天的保养计划，未勾选项保养计划将删除，确定修改并删除吗？")) {
    	    pmForm.submit();
    	}
	}
</script>

<form action="<%=request.getContextPath()%>/control/selectTsUnPm?eqpId=<%=eqpId%>" method="post" name="pmForm" onsubmit="return false;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset>
      <legend>保养计划</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="10%" class="en11pxb">计划日期</td>
          <td width="20%" class="en11pxb">保养类型</td>
          <td width="20%" class="en11pxb">计划日期</td>
        </tr>

        <%
        if(equipmentScheduleList != null && equipmentScheduleList.size() > 0) {
       		for(Iterator it = equipmentScheduleList.iterator();it.hasNext();) {
				Map map = (Map) it.next();%>
    	        <tr bgcolor="#DFE1EC">
        		    <td width="10%"  class="en11pxb">
    				    <input type="checkbox" name="pmSelect" id="pmSelect">
    				    <input type="hidden" name="scheduleIndex" value="<%=map.get("SCHEDULE_INDEX")%>">
    				    <input type="hidden" name="periodIndex" value="<%=map.get("PERIOD_INDEX")%>">
    				</td>

    				<td width="20%"  class="en11pxb">
    				    <%=map.get("PERIOD_NAME")%>
    				</td>

    				<td width="20%"  class="en11pxb">
    				    <%=map.get("SCHEDULE_DATE").toString().substring(0, 10)%>
    				</td>
				</tr>
	     <%
    	  	}
    	 }
    	 %>
      </table>
    </fieldset></td>
  </tr>
</table>

<input type="hidden" name="modifySchedule" value="">
<input type="hidden" name="deleteSchedule" value="">
</form>

<table border="0" cellspacing="0" cellpadding="0">
	<tr height="30">
	    <td><ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:selectTsUnPm();" id="save"><span>&nbsp;确定&nbsp;</span></a></li>
		</ul>
		<ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:history.go(-1);"><span>&nbsp;返回&nbsp;</span></a></li>
		</ul>
		</td>
	</tr>
</table>