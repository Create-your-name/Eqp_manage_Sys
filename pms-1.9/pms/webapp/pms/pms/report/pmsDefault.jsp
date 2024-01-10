<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="org.ofbiz.base.util.UtilMisc"%>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.entity.GenericDelegator"%>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>

<%! public String getDeptColor(String dept) {
		String deptColor = "#DFE1EC";

		if (dept.equals("工程一部")) {
			deptColor = "#FFFF99";
		} else if (dept.equals("工程二部")) {
			deptColor = "#99CCFF";
		} else if (dept.equals("生产制造部")){
			deptColor = "#66FF66";
		} else if (dept.equals("质量保证部")){
			deptColor = "#FFCCFF";
		} else if (dept.equals("动力保障部")){
			deptColor = "#FFCC66";
		}

		return deptColor;
	}
%>
<%
try {
	String user = CommonUtil.getUserNo(request);
	GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
    GenericValue listUser = delegator.findByPrimaryKey("Account", UtilMisc.toMap("accountNo", user));
    String accountDept = listUser.getString("accountDept");
    String UserDept = listUser.getString("accountDept");
	if (accountDept == null || accountDept.equals("无")){
		accountDept = "ALL";
	}
	if(pageContext.findAttribute("d")!=null){
		accountDept = pageContext.findAttribute("d").toString();
		if (accountDept == ""){
			accountDept = "ALL";
		}
	}
	String tabIndex = "abnormal";
	if(pageContext.findAttribute("tabIndex")!=null){
		tabIndex = pageContext.findAttribute("tabIndex").toString();
		if (tabIndex == ""){
			tabIndex = "abnormal";
		}
	}
%>
<script language="javascript">

   //查看流程数据
    function viewJob(jobRelationIndex) {
        var url = "<ofbiz:url>/viewFlow</ofbiz:url>?jobRelationIndex=" + jobRelationIndex;
        window.open(url,"viewJob",
                "top=130,left=240,width=600,height=400,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=no");
    }

	Ext.onReady(function(){
		var tabs = new Ext.TabPanel('tabs');
        var abnormal = tabs.addTab('abnormal', "设备异常和参数异常");
        var pc = tabs.addTab('pc', "巡检");
        var pm = tabs.addTab('pm', "保养");
        var dayPm = tabs.addTab('dayPm', "当日未保养(日保养)的设备清单");
        var eventList = tabs.addTab('eventList', "事件列表");

        var strTemp = '<%=accountDept %>';

        abnormal.on('activate', function(){
        		if ('<%=tabIndex%>' != 'abnormal'){
			    	loading();
			    	doSubmit('<%=accountDept %>','abnormal');
		    	}
        });
        pc.on('activate', function(){
        		if ('<%=tabIndex%>' != 'pc'){
			    	loading();
			    	doSubmit('<%=accountDept %>','pc');
			   	}
        });
        pm.on('activate', function(){
		    	if ('<%=tabIndex%>' != 'pm'){
			    	loading();
			    	doSubmit('<%=accountDept %>','pm');
			   	}
        });
        dayPm.on('activate', function(){
		    	if ('<%=tabIndex%>' != 'dayPm'){
			    	loading();
			    	doSubmit('<%=accountDept %>','dayPm');
			   	}
        });
        eventList.on('activate', function(){
		    	if ('<%=tabIndex%>' != 'eventList'){
			    	loading();
			    	doSubmit('<%=accountDept %>','eventList');
			   	}
        });
        tabs.activate('<%=tabIndex%>');
    });

    function doSubmit(para,tabIndex){
      	loading();
    	var param =  Ext.urlEncode({dept:para,tabIndex:tabIndex});
		//document.pmsDefault.action = '<ofbiz:url>/pmsDefault</ofbiz:url>?dept='+para+'&tabIndex='+tabIndex;
		var urlString = '<ofbiz:url>/pmsDefault</ofbiz:url>?' + param;
		document.location = urlString;
    }

    function editActionItem(obj,itemIndex) {
		var url='<ofbiz:url>/queryDefaultSub</ofbiz:url>?itemIndex='+itemIndex;
		document.pmsDefault.action = url;
		document.pmsDefault.submit();
	}

	function doSubmitPC(obj,item1,item2,item3,item4){
		var url='<ofbiz:url>/inputPcFormEntry</ofbiz:url>?periodDesc='+item1+'&pcIndex='+item2+'&jobRelationIndex='+item3+'&status=完成';
		document.pmsDefault.action = url;
		document.pmsDefault.submit();
	}

	function intoPmForm(index,eqpId){
        loading();
        var actionURL='<ofbiz:url>/overPmFormView</ofbiz:url>?functionType=3&pmIndex='+index+'&eqpId='+eqpId;
		document.pmsDefault.action = actionURL;
		document.pmsDefault.submit();
      }

	function pmRecordInfo(index,eqpId){
           loading();
           var actionURL='<ofbiz:url>/pmRecordInfo</ofbiz:url>?functionType=1&pmIndex='+index+'&eqpId='+eqpId;
           document.location.href=actionURL;
      }

	function managerAbnormalForm(index,eqpId){
           loading();
           var actionURL='<ofbiz:url>/intoAbnormalFormManageEntry</ofbiz:url>?functionType=1&abnormalIndex='+index+'&eqpId='+eqpId;
           document.location.href=actionURL;

      }

	function intoAbnormalForm(index,eqpId){
           loading();
           var actionURL='<ofbiz:url>/overAbnormalFormView</ofbiz:url>?functionType=3&abnormalIndex='+index+'&eqpId='+eqpId;
           document.location.href=actionURL;

      }

</script>

<form action="<%=request.getContextPath()%>/control/pmsDefault"  name="pmsDefault" method="POST" id ="pmsDefault" >

<!--copy area-->
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="javascript:doSubmit('ALL','<%=tabIndex%>')"><span>&nbsp;ALL</span></a></li>
	</ul></td>

	<%for (int i = 0; i < Constants.DEPTS.length; i++) {%>
    <td><ul class="button">
		<li><a class="button-text" href="javascript:doSubmit('<%=Constants.DEPTS[i]%>','<%=tabIndex%>')"><span><%=Constants.DEPTS[i]%><em style="color:<%=getDeptColor(Constants.DEPTS[i])%>">■</em></span></a></li>
	</ul></td>
    <%}%>
  </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr><td width="100%"><fieldset>
		<legend><%=accountDept %></legend>
<div id="tabs">
	<div id="abnormal" class="tab-content">
	    <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">当日GUI设备异常清单</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">设备名</td>
			          <td   class="en11pxb">处理时数</td>
			          <td   class="en11pxb">发生时间</td>
			          <td   class="en11pxb">表单开始时间</td>
			        </tr>
			        <%
			        	List pm_abnormal_record_List = (List)request.getAttribute("pm_abnormal_record_List");
			        	if ((pm_abnormal_record_List != null) && (pm_abnormal_record_List.size()!= 0)) {
				        	for (int i = 0 ; i <= pm_abnormal_record_List.size()-1 ; i++){
				        		Map map = (Map)pm_abnormal_record_List.get(i);
				        		String EQUIPMENT_ID = (String)(map.get("EQUIPMENT_ID"));
				        		String TREATTIME = UtilFormatOut.checkNull((String)(map.get("TREATTIME")));
				        		String CREATE_TIME = UtilFormatOut.checkNull((String)(map.get("CREATE_TIME")));
				        		String START_TIME = UtilFormatOut.checkNull((String)(map.get("START_TIME")));
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));
				        		String ABNORMAL_INDEX = UtilFormatOut.checkNull((String)(map.get("ABNORMAL_INDEX")));
				        		String STATUS = UtilFormatOut.checkNull((String)(map.get("STATUS")));
				        		%>
				        		 	<tr bgcolor="<%=getDeptColor(MAINT_DEPT)%>">
										<%
										if (UserDept.equals(MAINT_DEPT)){
											if (STATUS.equals("2")){
										%>
											<td class="en11px"><a href="#" onclick="intoAbnormalForm('<%=ABNORMAL_INDEX%>','<%=EQUIPMENT_ID%>',1)"><%=EQUIPMENT_ID %></a></td>
										<%
											}
											else
											{
											%>
				        					<td class="en11px"><%=EQUIPMENT_ID %></td>
				        				<%
											}
										}else{
										 %>
				        					<td class="en11px"><%=EQUIPMENT_ID %></td>
				        				<%
				        				}
				        				 %>
				        				<td class="en11px"><%=TREATTIME %></td>
				        				<td class="en11px"><%=CREATE_TIME %></td>
				        				<td class="en11px"><%=START_TIME %></td>
				        			</tr>
				        		<%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>
	   <br>
	   <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">目前设备参数已超过最小值清单</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">设备名</td>
			          <td   class="en11pxb">参数</td>
			          <td   class="en11pxb">参数基准</td>
			          <td   class="en11pxb">目前参数值</td>
			          <td   class="en11pxb">参数警示最大值</td>
			          <td   class="en11pxb">参数警示最小值</td>
			        </tr>
			        <%
			        	List unschedule_eqp_param_List = (List)request.getAttribute("unschedule_eqp_param_List");
			        	if ((unschedule_eqp_param_List != null) && (unschedule_eqp_param_List.size()!= 0)) {
				        	for (int i = 0 ; i <= unschedule_eqp_param_List.size()-1 ; i++){
				        		Map map = (Map)unschedule_eqp_param_List.get(i);
				        		String EQUIPMENT_ID = (String)(map.get("EQUIPMENT_ID"));
				        		String PARAM_NAME = UtilFormatOut.checkNull((String)(map.get("PARAM_NAME")));
				        		String STD_FLAG = UtilFormatOut.checkNull((String)(map.get("STD_FLAG")));
				        		String VALUE = UtilFormatOut.checkNull((String)(map.get("VALUE")));
				        		String MAX_VALUE = UtilFormatOut.checkNull((String)(map.get("MAX_VALUE")));
				        		String MIN_VALUE = UtilFormatOut.checkNull((String)(map.get("MIN_VALUE")));
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));
				     %>
				        		<tr bgcolor="<%=getDeptColor(MAINT_DEPT)%>">
				        				<td class="en11px"><%=EQUIPMENT_ID %></td>
				        				<td class="en11px"><%=PARAM_NAME %></td>
				        				<td class="en11px"><%=STD_FLAG %></td>
				        				<td class="en11px"><%=VALUE %></td>
				        				<td class="en11px"><%=MAX_VALUE %></td>
				        				<td class="en11px"><%=MIN_VALUE %></td>
				        			</tr>
				        		<%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>

	</div>

	<div id="pc" class="tab-content">
    	<table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">当日未完成的巡检</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">巡检样式(巡检周期)</td>
			        </tr>
			        <%
			        	List period_schedule_List = (List)request.getAttribute("period_schedule_List");
			        	if ((period_schedule_List != null) && (period_schedule_List.size()!= 0)) {
				        	for (int i = 0 ; i <= period_schedule_List.size()-1 ; i++){
				        		Map map = (Map)period_schedule_List.get(i);
				        		String NAME = UtilFormatOut.checkNull((String)(map.get("NAME")));
				        		//String MAINT_DEPT = (String)(map.get("MAINT_DEPT"));

				        		 %>
				        		 	<tr bgcolor="#DFE1EC">

				        				<td class="en11px"><%=NAME %></td>
				        			</tr>
				        		<%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>
	   <br>
	   <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">当日已经完成的巡检</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">巡检样式(巡检周期)</td>
			        </tr>
			        <%
			        	List pc_form_List = (List)request.getAttribute("pc_form_List");
			        	if ((pc_form_List != null) && (pc_form_List.size()!= 0)) {
				        	for (int i = 0 ; i <= pc_form_List.size()-1 ; i++){
				        		Map map = (Map)pc_form_List.get(i);
				        		String NAME = (String)(map.get("NAME"));
				        		//String MAINT_DEPT = (String)(map.get("MAINT_DEPT"));
				        		String SEQ_INDEX = UtilFormatOut.checkNull((String)(map.get("SEQ_INDEX")));
				        		String PERIOD_DESC = UtilFormatOut.checkNull((String)(map.get("PERIOD_DESC")));
				        		String PC_INDEX = UtilFormatOut.checkNull((String)(map.get("PC_INDEX")));
				        		String Status = UtilFormatOut.checkNull((String)(map.get("STATUS")));
				        	%>
				        		 	<tr bgcolor="#DFE1EC">


									<td class="en11px"><a href="#" onclick="doSubmitPC(this,'<%=PERIOD_DESC%>','<%=PC_INDEX%>','<%=SEQ_INDEX%>','<%=Status %>')"><%=NAME %></a></td>

				        			</tr>
				        		<%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>
  	</div>

	<div id="pm" class="tab-content">
	   <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">当日未保养的设备清单</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">设备名</td>
			          <td   class="en11pxb">保养类别</td>
			          <td   class="en11pxb">标准工时</td>
			          <td   class="en11pxb">预计开始时间</td>
			          <td   class="en11pxb">预计结束时间</td>
			        </tr>
			        <%
			        	List equipment_schedule_List = (List)request.getAttribute("equipment_schedule_List");
			        	if ((equipment_schedule_List != null) && (equipment_schedule_List.size()!= 0)) {
				        	for (int i = 0 ; i <= equipment_schedule_List.size()-1 ; i++){
				        		Map map = (Map)equipment_schedule_List.get(i);
				        		String EQUIPMENT_ID = (String)(map.get("EQUIPMENT_ID"));
				        		String PERIOD_NAME = UtilFormatOut.checkNull((String)(map.get("PERIOD_NAME")));
				        		String STANDARD_HOUR = UtilFormatOut.checkNull((String)(map.get("STANDARD_HOUR")));
				        		String START_TIME = UtilFormatOut.checkNull((String)(map.get("START_TIME")));
				        		String END_TIME = UtilFormatOut.checkNull((String)(map.get("END_TIME")));
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));
				        		String SCHEDULE_INDEX = (String)(map.get("SCHEDULE_INDEX"));
				        		String STATUS = (String)(map.get("STATUS"));
				        		String PM_INDEX = (String)(map.get("PM_INDEX"));
				        		String MSA = (String)(map.get("MSA"));
				        %>
				        		 	<tr bgcolor="<%=getDeptColor(MAINT_DEPT)%>">
									<%
										if (UserDept.equals(MAINT_DEPT)
										    || (UserDept.endsWith(Constants.DEPT_QC) && MSA.equals("Y"))){
											if (STATUS.equals("10")){
										%>
											<td class="en11px"><a href="#" onclick="editActionItem(this,'<%=SCHEDULE_INDEX%>')"><%=EQUIPMENT_ID %></a></td>
										<%
											}
											else{
											%>
											<td class="en11px"><a href="#" onclick="pmRecordInfo('<%=PM_INDEX%>','<%=EQUIPMENT_ID%>')"><%=EQUIPMENT_ID %></a></td>
										<%
											}
										}else{
										 %>
				        					<td class="en11px"><%=EQUIPMENT_ID %></td>
				        				<%
				        				}
				        				 %>
				        				<td class="en11px"><%=PERIOD_NAME %></td>
				        				<td class="en11px"><%=STANDARD_HOUR %></td>
				        				<td class="en11px"><%=START_TIME %></td>
				        				<td class="en11px"><%=END_TIME %></td>
				        			</tr>
				        		<%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>
	   <br>
	   <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">当日完成保养的设备清单</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">设备名</td>
			          <td   class="en11pxb">保养类别</td>
			          <td   class="en11pxb">标准工时</td>
			          <td   class="en11pxb">实际工时</td>
			          <td   class="en11pxb">开始时间</td>
			          <td   class="en11pxb">结束时间</td>
			          <td   class="en11pxb">处理流程</td>
			        </tr>
			        <%
			        	List pm_form_List = (List)request.getAttribute("pm_form_List");
			        	if ((pm_form_List != null) && (pm_form_List.size()!= 0)) {
				        	for (int i = 0 ; i <= pm_form_List.size()-1 ; i++){
				        		Map map = (Map)pm_form_List.get(i);
				        		String EQUIPMENT_ID = (String)(map.get("EQUIPMENT_ID"));
				        		String PM_NAME = UtilFormatOut.checkNull((String)(map.get("PM_NAME")));
				        		String TRUE_TIME = UtilFormatOut.checkNull((String)(map.get("TRUE_TIME")));
				        		String STANDARD_HOUR = UtilFormatOut.checkNull((String)(map.get("STANDARD_HOUR")));
				        		String START_TIME = UtilFormatOut.checkNull((String)(map.get("START_TIME")));
				        		String END_TIME = UtilFormatOut.checkNull((String)(map.get("END_TIME")));
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));
				        		String SEQ_INDEX = UtilFormatOut.checkNull((String)(map.get("SEQ_INDEX")));
				        		String JOB_NAME = UtilFormatOut.checkNull((String)(map.get("JOB_NAME")));
				        		 %>
				        		 	<tr bgcolor="<%=getDeptColor(MAINT_DEPT)%>">
				        				<td class="en11px"><%=EQUIPMENT_ID %></td>
				        				<td class="en11px"><%=PM_NAME %></td>
				        				<td class="en11px"><%=STANDARD_HOUR %></td>
				        				<td class="en11px"><%=TRUE_TIME %></td>
				        				<td class="en11px"><%=START_TIME %></td>
				        				<td class="en11px"><%=END_TIME %></td>
				        				<td class="en11px"><a href="javascript:viewJob(<%=SEQ_INDEX%>)"><%=JOB_NAME%></a></td>
				        			</tr>
				        		<%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>
	   <br>
    	<table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">明天已排程设备保养清单(不含日保养)</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">设备名</td>
			          <td   class="en11pxb">保养类别</td>
			          <td   class="en11pxb">标准工时</td>
			          <td   class="en11pxb">预计开始时间</td>
			          <td   class="en11pxb">预计结束时间</td>

			        </tr>
			        <%
			        	List next_pm_List = (List)request.getAttribute("next_pm_List");
			        	if ((next_pm_List != null) && (next_pm_List.size()!= 0)) {
				        	for (int i = 0 ; i <= next_pm_List.size()-1 ; i++){
				        		Map map = (Map)next_pm_List.get(i);
				        		String EQUIPMENT_ID = (String)(map.get("EQUIPMENT_ID"));
				        		String PERIOD_NAME = UtilFormatOut.checkNull((String)(map.get("PERIOD_NAME")));
				        		String STANDARD_HOUR = UtilFormatOut.checkNull((String)(map.get("STANDARD_HOUR")));
				        		String START_TIME = UtilFormatOut.checkNull((String)(map.get("START_TIME")));
				        		String END_TIME = UtilFormatOut.checkNull((String)(map.get("END_TIME")));
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));
				        		//String SCHEDULE_INDEX = UtilFormatOut.checkNull((String)(map.get("SCHEDULE_INDEX")));
				        		 %>
				        		 	<tr bgcolor="<%=getDeptColor(MAINT_DEPT)%>">
				        				<td class="en11px"><%=EQUIPMENT_ID %></td>
				        				<td class="en11px"><%=PERIOD_NAME %></td>
				        				<td class="en11px"><%=STANDARD_HOUR %></td>
				        				<td class="en11px"><%=START_TIME %></td>
				        				<td class="en11px"><%=END_TIME %></td>
				        			</tr>
				        		<%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>
	   <br>
	   <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">后天已排程设备保养清单(不含日保养)</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">设备名</td>
			          <td   class="en11pxb">保养类别</td>
			          <td   class="en11pxb">标准工时</td>
			          <td   class="en11pxb">预计开始时间</td>
			          <td   class="en11pxb">预计结束时间</td>
			        </tr>
			        <%
			        	List doublenext_pm_List = (List)request.getAttribute("doublenext_pm_List");
			        	if ((doublenext_pm_List != null) && (doublenext_pm_List.size()!= 0)) {
				        	for (int i = 0 ; i <= doublenext_pm_List.size()-1 ; i++){
				        		Map map = (Map)doublenext_pm_List.get(i);
				        		String EQUIPMENT_ID = (String)(map.get("EQUIPMENT_ID"));
				        		String PERIOD_NAME = UtilFormatOut.checkNull((String)(map.get("PERIOD_NAME")));
				        		String STANDARD_HOUR = UtilFormatOut.checkNull((String)(map.get("STANDARD_HOUR")));
				        		String START_TIME = UtilFormatOut.checkNull((String)(map.get("START_TIME")));
				        		String END_TIME = UtilFormatOut.checkNull((String)(map.get("END_TIME")));
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));
				        		//String SCHEDULE_INDEX = UtilFormatOut.checkNull((String)(map.get("SCHEDULE_INDEX")));
				        		 %>
				        		 	<tr bgcolor="<%=getDeptColor(MAINT_DEPT)%>">
				        				<td class="en11px"><%=EQUIPMENT_ID %></td>
				        				<td class="en11px"><%=PERIOD_NAME %></td>
				        				<td class="en11px"><%=STANDARD_HOUR %></td>
				        				<td class="en11px"><%=START_TIME %></td>
				        				<td class="en11px"><%=END_TIME %></td>
				        			</tr>
				        		<%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>
	   <br>
	   <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">后七日(含当日)设施部设备保养清单(不含日保养)</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">设备名</td>
			          <td   class="en11pxb">保养类别</td>
			          <td   class="en11pxb">标准工时</td>
			          <td   class="en11pxb">预计开始时间</td>
			          <td   class="en11pxb">预计结束时间</td>
			        </tr>
			        <%
			        	List nextseven_pm_List = (List)request.getAttribute("nextseven_pm_List");
			        	if ((nextseven_pm_List != null) && (nextseven_pm_List.size()!= 0)) {
				        	for (int i = 0 ; i <= nextseven_pm_List.size()-1 ; i++){
				        		Map map = (Map)nextseven_pm_List.get(i);
				        		String EQUIPMENT_ID = (String)(map.get("EQUIPMENT_ID"));
				        		String PERIOD_NAME = UtilFormatOut.checkNull((String)(map.get("PERIOD_NAME")));
				        		String STANDARD_HOUR = UtilFormatOut.checkNull((String)(map.get("STANDARD_HOUR")));
				        		String START_TIME = UtilFormatOut.checkNull((String)(map.get("START_TIME")));
				        		String END_TIME = UtilFormatOut.checkNull((String)(map.get("END_TIME")));
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));
				        		//String SCHEDULE_INDEX = UtilFormatOut.checkNull((String)(map.get("SCHEDULE_INDEX")));
				        		 %>
				        		 	<tr bgcolor="<%=getDeptColor(MAINT_DEPT)%>">

				        					<td class="en11px"><%=EQUIPMENT_ID %></td>

				        				<td class="en11px"><%=PERIOD_NAME %></td>
				        				<td class="en11px"><%=STANDARD_HOUR %></td>
				        				<td class="en11px"><%=START_TIME %></td>
				        				<td class="en11px"><%=END_TIME %></td>
				        			</tr>
				        		<%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>
	   <br>
	   <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">前一工作天保养设备清单(不含日保养)</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">设备名</td>
			          <td   class="en11pxb">保养类别</td>
			          <td   class="en11pxb">标准工时</td>
			          <td   class="en11pxb">保养时数</td>
			          <td   class="en11pxb">处理流程</td>
			        </tr>
			        <%
			        	List last_pm_List = (List)request.getAttribute("last_pm_List");
			        	if ((last_pm_List != null) && (last_pm_List.size()!= 0)) {
				        	for (int i = 0 ; i <= last_pm_List.size()-1 ; i++){
				        		Map map = (Map)last_pm_List.get(i);
				        		String EQUIPMENT_ID = UtilFormatOut.checkNull((String)(map.get("EQUIPMENT_ID")));
				        		String PM_INDEX = UtilFormatOut.checkNull((String)(map.get("PM_INDEX")));
				        		String PM_NAME = UtilFormatOut.checkNull((String)(map.get("PM_NAME")));
				        		String STANDARD_HOUR = UtilFormatOut.checkNull((String)(map.get("STANDARD_HOUR")));
				        		String TREATTIME = UtilFormatOut.checkNull((String)(map.get("TREATTIME")));
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));
				        		String SEQ_INDEX = UtilFormatOut.checkNull((String)(map.get("SEQ_INDEX")));
				        		String JOB_NAME = UtilFormatOut.checkNull((String)(map.get("JOB_NAME")));

				        		 %>
				        		 	<tr bgcolor="<%=getDeptColor(MAINT_DEPT)%>">
				        				<td class="en11px"><a href="#" onclick="intoPmForm('<%=PM_INDEX%>','<%=EQUIPMENT_ID %>')"><%=EQUIPMENT_ID %></a></td>
				        				<td class="en11px"><%=PM_NAME %></td>
				        				<td class="en11px"><%=STANDARD_HOUR %></td>
				        				<td class="en11px"><%=TREATTIME %></td>
				        				<td class="en11px"><a href="javascript:viewJob(<%=SEQ_INDEX%>)"><%=JOB_NAME%></a></td>
				        			</tr>
				        		<%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	    </table>

		<br>
    	<table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">已排程未保养历史纪录(当日以前,不含当日)(不含日保养)(设备状态非03或05)</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">设备名</td>
			          <td   class="en11pxb">保养类别</td>
			          <td   class="en11pxb">标准工时</td>
			          <td   class="en11pxb">预计开始时间</td>
			          <td   class="en11pxb">预计结束时间</td>
			        </tr>
			        <%
			        	List notdo_pm_List = (List)request.getAttribute("notdo_pm_List");
			        	if ((notdo_pm_List != null) && (notdo_pm_List.size()!= 0)) {
				        	for (int i = 0 ; i <= notdo_pm_List.size()-1 ; i++){
				        		Map map = (Map)notdo_pm_List.get(i);
				        		String EQUIPMENT_ID = UtilFormatOut.checkNull((String)(map.get("EQUIPMENT_ID")));
				        		String PERIOD_NAME = UtilFormatOut.checkNull((String)(map.get("PERIOD_NAME")));
				        		String STANDARD_HOUR = UtilFormatOut.checkNull((String)(map.get("STANDARD_HOUR")));
				        		String START_TIME = UtilFormatOut.checkNull((String)(map.get("START_TIME")));
				        		String END_TIME = UtilFormatOut.checkNull((String)(map.get("END_TIME")));
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));
				        		String SCHEDULE_INDEX = UtilFormatOut.checkNull((String)(map.get("SCHEDULE_INDEX")));
				        		%>
				        		 	<tr bgcolor="<%=getDeptColor(MAINT_DEPT)%>">
										<td class="en11px"><%=EQUIPMENT_ID %></td>
				        				<td class="en11px"><%=PERIOD_NAME %></td>
				        				<td class="en11px"><%=STANDARD_HOUR %></td>
				        				<td class="en11px"><%=START_TIME %></td>
				        				<td class="en11px"><%=END_TIME %></td>
				        			</tr>
				        		<%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>

	   <br>
    	<table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">已排程未保养历史纪录(当日以前,不含当日)(不含日保养)(设备状态03或05)</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">设备名</td>
			          <td   class="en11pxb">保养类别</td>
			          <td   class="en11pxb">标准工时</td>
			          <td   class="en11pxb">预计开始时间</td>
			          <td   class="en11pxb">预计结束时间</td>
			        </tr>
			        <%
			        	List notdo_pm_down_List = (List)request.getAttribute("notdo_pm_down_List");
			        	if ((notdo_pm_down_List != null) && (notdo_pm_down_List.size()!= 0)) {
				        	for (int i = 0 ; i <= notdo_pm_down_List.size()-1 ; i++){
				        		Map map = (Map)notdo_pm_down_List.get(i);
				        		String EQUIPMENT_ID = UtilFormatOut.checkNull((String)(map.get("EQUIPMENT_ID")));
				        		String PERIOD_NAME = UtilFormatOut.checkNull((String)(map.get("PERIOD_NAME")));
				        		String STANDARD_HOUR = UtilFormatOut.checkNull((String)(map.get("STANDARD_HOUR")));
				        		String START_TIME = UtilFormatOut.checkNull((String)(map.get("START_TIME")));
				        		String END_TIME = UtilFormatOut.checkNull((String)(map.get("END_TIME")));
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));
				        		String SCHEDULE_INDEX = UtilFormatOut.checkNull((String)(map.get("SCHEDULE_INDEX")));
				        		%>
				        		 	<tr bgcolor="<%=getDeptColor(MAINT_DEPT)%>">
										<td class="en11px"><%=EQUIPMENT_ID %></td>
				        				<td class="en11px"><%=PERIOD_NAME %></td>
				        				<td class="en11px"><%=STANDARD_HOUR %></td>
				        				<td class="en11px"><%=START_TIME %></td>
				        				<td class="en11px"><%=END_TIME %></td>
				        			</tr>
				        		<%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>
	</div>

	<div id="dayPm" class="tab-content">
		<table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">保养区段0:00--7:00</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">设备名</td>
			          <td   class="en11pxb"></td>
			          <td   class="en11pxb"></td>
			        </tr>
			        <%
			        	List today_pm_List1 = (List)request.getAttribute("today_pm_List1");
			        	if ((today_pm_List1 != null) && (today_pm_List1.size()!= 0)) {
			        		int intIndex = 0;

				        	for (int i = 0 ; i <= today_pm_List1.size()-1 ; i++){
				        	//for (intIndex = 0; intIndex < 3 ; intIndex++){
				        		Map map = (Map)today_pm_List1.get(i);
				        		String EQUIPMENT_ID = (String)(map.get("EQUIPMENT_ID"));
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));
				        		String SCHEDULE_INDEX = UtilFormatOut.checkNull((String)(map.get("SCHEDULE_INDEX")));

				        		if (intIndex == 0){
				        		%>
				        		 <tr bgcolor="#DFE1EC">
								<% } %>
								<!--start--->
								<% if (UserDept.equals(MAINT_DEPT)){ %>
					        		<td bgcolor="<%=getDeptColor(MAINT_DEPT)%>"  class="en11px"><a href="#" onclick="editActionItem(this,'<%=SCHEDULE_INDEX%>')"><%=EQUIPMENT_ID %></a></td>
								<% } else { %>
					        		<td bgcolor="<%=getDeptColor(MAINT_DEPT)%>"  class="en11px"><%=EQUIPMENT_ID %></a></td>
		        				<% } %>
		        				<!--end-->
		        				<% if (intIndex == 2){
				        			intIndex = -1;
				        		%>
				        			</tr>
				        		<%
				        		}
				        		intIndex++;
				        	}
				        	if (intIndex==1){
				        		%> <td class="en11px">&nbsp;</a></td>
				        			<td class="en11px">&nbsp;</a></td>
				        			</tr><%
				        	}
				        	if (intIndex==2){
				        		%> <td class="en11px">&nbsp;</a></td>
				        			</tr><%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>
	   <br>
	   <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">保养区段7:00--13:00</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">设备名</td>
			          <td   class="en11pxb"></td>
			          <td   class="en11pxb"></td>
			        </tr>
			        <%
			        	List today_pm_List2 = (List)request.getAttribute("today_pm_List2");
			        	if ((today_pm_List2 != null) && (today_pm_List2.size()!= 0)) {
			        		int intIndex = 0;

				        	for (int i = 0 ; i <= today_pm_List2.size()-1 ; i++){
				        	//for (intIndex = 0; intIndex < 3 ; intIndex++){
				        		Map map = (Map)today_pm_List2.get(i);
				        		String EQUIPMENT_ID = (String)(map.get("EQUIPMENT_ID"));
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));
				        		String SCHEDULE_INDEX = UtilFormatOut.checkNull((String)(map.get("SCHEDULE_INDEX")));

				        		if (intIndex == 0){
				        		%>
				        		 <tr bgcolor="#DFE1EC">
								<% } %>

								<% if (UserDept.equals(MAINT_DEPT)){ %>
					        		<td bgcolor="<%=getDeptColor(MAINT_DEPT)%>"  class="en11px"><a href="#" onclick="editActionItem(this,'<%=SCHEDULE_INDEX%>')"><%=EQUIPMENT_ID %></a></td>
								<% } else { %>
					        		<td bgcolor="<%=getDeptColor(MAINT_DEPT)%>"  class="en11px"><%=EQUIPMENT_ID %></a></td>
		        				<% } %>

		        				<%
				        		if (intIndex == 2){
				        			intIndex = -1;
				        			%>
				        			</tr>
				        		<%
				        		}
				        		intIndex++;
				        	}
				        	if (intIndex==1){
				        		%> <td class="en11px">&nbsp;</a></td>
				        			<td class="en11px">&nbsp;</a></td>
				        			</tr><%
				        	}
				        	if (intIndex==2){
				        		%> <td class="en11px">&nbsp;</a></td>
				        			</tr><%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>
	   <br>
	   <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">保养区段13:00--19:00</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">设备名</td>
			          <td   class="en11pxb"></td>
			          <td   class="en11pxb"></td>
			        </tr>
			        <%
			        	List today_pm_List3 = (List)request.getAttribute("today_pm_List3");
			        	if ((today_pm_List3 != null) && (today_pm_List3.size()!= 0)) {
			        		int intIndex = 0;

				        	for (int i = 0 ; i <= today_pm_List3.size()-1 ; i++){
				        	//for (intIndex = 0; intIndex < 3 ; intIndex++){
				        		Map map = (Map)today_pm_List3.get(i);
				        		String EQUIPMENT_ID = (String)(map.get("EQUIPMENT_ID"));
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));
				        		String SCHEDULE_INDEX = UtilFormatOut.checkNull((String)(map.get("SCHEDULE_INDEX")));

				        		if (intIndex == 0){
				        		%>
				        		 <tr bgcolor="#DFE1EC">
								<% } %>

								<% if (UserDept.equals(MAINT_DEPT)){ %>
					        		<td bgcolor="<%=getDeptColor(MAINT_DEPT)%>"  class="en11px"><a href="#" onclick="editActionItem(this,'<%=SCHEDULE_INDEX%>')"><%=EQUIPMENT_ID %></a></td>
								<% } else { %>
					        		<td bgcolor="<%=getDeptColor(MAINT_DEPT)%>"  class="en11px"><%=EQUIPMENT_ID %></a></td>
		        				<% } %>

		        				<% if (intIndex == 2){
				        			intIndex = -1;
				        			%>
				        			</tr>
				        		<%
				        		}
				        		intIndex++;
				        	}
				        	if (intIndex==1){
				        		%> <td class="en11px">&nbsp;</a></td>
				        			<td class="en11px">&nbsp;</a></td>
				        			</tr><%
				        	}
				        	if (intIndex==2){
				        		%> <td class="en11px">&nbsp;</a></td>
				        			</tr><%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>
	   <br>
	   <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">保养区段19:00--24:00</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">设备名</td>
			          <td   class="en11pxb"></td>
			          <td   class="en11pxb"></td>
			        </tr>
			        <%
			        	List today_pm_List4 = (List)request.getAttribute("today_pm_List4");
			        	if ((today_pm_List4 != null) && (today_pm_List4.size()!= 0)) {
			        		int intIndex = 0;

				        	for (int i = 0 ; i <= today_pm_List4.size()-1 ; i++){
				        	//for (intIndex = 0; intIndex < 3 ; intIndex++){
				        		Map map = (Map)today_pm_List4.get(i);
				        		String EQUIPMENT_ID = (String)(map.get("EQUIPMENT_ID"));
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));
				        		String SCHEDULE_INDEX = UtilFormatOut.checkNull((String)(map.get("SCHEDULE_INDEX")));

				        		if (intIndex == 0){
				        		%>
				        		 <tr bgcolor="#DFE1EC">
								<% } %>

								<% if (UserDept.equals(MAINT_DEPT)){ %>
					        		<td bgcolor="<%=getDeptColor(MAINT_DEPT)%>"  class="en11px"><a href="#" onclick="editActionItem(this,'<%=SCHEDULE_INDEX%>')"><%=EQUIPMENT_ID %></a></td>
								<% } else { %>
					        		<td bgcolor="<%=getDeptColor(MAINT_DEPT)%>"  class="en11px"><%=EQUIPMENT_ID %></a></td>
		        				<% } %>

		        				<% if (intIndex == 2){
				        			intIndex = -1;
				        			%>
				        			</tr>
				        		<%
				        		}
				        		intIndex++;
				        	}
				        	if (intIndex==1){
				        		%> <td class="en11px">&nbsp;</a></td>
				        			<td class="en11px">&nbsp;</a></td>
				        			</tr><%
				        	}
				        	if (intIndex==2){
				        		%> <td class="en11px">&nbsp;</a></td>
				        			</tr><%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>
	</div>
	<div id="eventList" class="tab-content">
    	 <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">事件list(一天内已完成、一天内有修改或未完成的异常记录及保养记录清单)</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">EQ ID</td>
			          <td   class="en11pxb">事件名称/保养类别</td>
			          <td   class="en11pxb">发生日期</td>
			          <td   class="en11pxb">状态</td>
			        </tr>
			        <%
			        	List event_List = (List)request.getAttribute("event_List");
			        	if ((event_List != null) && (event_List.size()!= 0)) {
				        	for (int i = 0 ; i <= event_List.size()-1 ; i++){
				        		Map map = (Map)event_List.get(i);
				        		String EQUIPMENT_ID = UtilFormatOut.checkNull((String)(map.get("EQUIPMENT_ID")));
				        		String NAME = UtilFormatOut.checkNull((String)(map.get("ABNORMAL_NAME")));
				        		String PM_INDEX = UtilFormatOut.checkNull((String)(map.get("PM_INDEX")));
				        		String CREATE_TIME = UtilFormatOut.checkNull((String)(map.get("START_TIME")));
				        		String STATUS = UtilFormatOut.checkNull((String)(map.get("STATUS")));
				        		String TYPE = UtilFormatOut.checkNull((String)(map.get("TYPE")));
				        		if (STATUS.equals("1")){
				        			STATUS = "已完成";
				        		}else{
				        			STATUS = "处理中";
				        		}
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));

				        		 %>
				        		 	<tr bgcolor="<%=getDeptColor(MAINT_DEPT)%>">
										<%
										if (UserDept.equals(MAINT_DEPT)){
											if (TYPE.equals("PM")){
												if (STATUS.equals("处理中")){
													%>
														<td class="en11px"><a href="#" onclick="pmRecordInfo('<%=PM_INDEX%>','<%=EQUIPMENT_ID%>')"><%=EQUIPMENT_ID %></a></td>
													<%
												}else{
													%>
														<td class="en11px"><a href="#" onclick="intoPmForm('<%=PM_INDEX%>','<%=EQUIPMENT_ID%>')"><%=EQUIPMENT_ID %></a></td>
													<%
												}
											}
											else{
												if (STATUS.equals("处理中")){
													%>
														<td class="en11px"><a href="#" onclick="managerAbnormalForm('<%=PM_INDEX%>','<%=EQUIPMENT_ID%>')"><%=EQUIPMENT_ID %></a></td>
													<%
												}else{
													%>
														<td class="en11px"><a href="#" onclick="intoAbnormalForm('<%=PM_INDEX%>','<%=EQUIPMENT_ID%>')"><%=EQUIPMENT_ID %></a></td>
													<%
												}
											}
										}else{
										 %>
				        					<td class="en11px"><%=EQUIPMENT_ID %></td>
				        				<%
				        				}
				        				 %>
				        				<td class="en11px"><%=NAME %></td>
				        				<td class="en11px"><%=CREATE_TIME %></td>
				        				<td class="en11px"><%=STATUS %></td>
				        			</tr>
				        		<%
				        	}
			        	}
			         %>
			      </table>
		       	</td>
  			</tr>
	   </table>
	</div>
</div>

</fieldset>
</td>
  </tr>
</table>
</form>

<%
} catch (Exception e) {
    out.println(e);
}
%>