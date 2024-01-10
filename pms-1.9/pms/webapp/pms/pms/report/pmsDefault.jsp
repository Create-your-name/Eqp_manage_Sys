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

		if (dept.equals("����һ��")) {
			deptColor = "#FFFF99";
		} else if (dept.equals("���̶���")) {
			deptColor = "#99CCFF";
		} else if (dept.equals("�������첿")){
			deptColor = "#66FF66";
		} else if (dept.equals("������֤��")){
			deptColor = "#FFCCFF";
		} else if (dept.equals("�������ϲ�")){
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
	if (accountDept == null || accountDept.equals("��")){
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

   //�鿴��������
    function viewJob(jobRelationIndex) {
        var url = "<ofbiz:url>/viewFlow</ofbiz:url>?jobRelationIndex=" + jobRelationIndex;
        window.open(url,"viewJob",
                "top=130,left=240,width=600,height=400,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=no");
    }

	Ext.onReady(function(){
		var tabs = new Ext.TabPanel('tabs');
        var abnormal = tabs.addTab('abnormal', "�豸�쳣�Ͳ����쳣");
        var pc = tabs.addTab('pc', "Ѳ��");
        var pm = tabs.addTab('pm', "����");
        var dayPm = tabs.addTab('dayPm', "����δ����(�ձ���)���豸�嵥");
        var eventList = tabs.addTab('eventList', "�¼��б�");

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
		var url='<ofbiz:url>/inputPcFormEntry</ofbiz:url>?periodDesc='+item1+'&pcIndex='+item2+'&jobRelationIndex='+item3+'&status=���';
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
		<li><a class="button-text" href="javascript:doSubmit('<%=Constants.DEPTS[i]%>','<%=tabIndex%>')"><span><%=Constants.DEPTS[i]%><em style="color:<%=getDeptColor(Constants.DEPTS[i])%>">��</em></span></a></li>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">����GUI�豸�쳣�嵥</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">�豸��</td>
			          <td   class="en11pxb">����ʱ��</td>
			          <td   class="en11pxb">����ʱ��</td>
			          <td   class="en11pxb">����ʼʱ��</td>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">Ŀǰ�豸�����ѳ�����Сֵ�嵥</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">�豸��</td>
			          <td   class="en11pxb">����</td>
			          <td   class="en11pxb">������׼</td>
			          <td   class="en11pxb">Ŀǰ����ֵ</td>
			          <td   class="en11pxb">������ʾ���ֵ</td>
			          <td   class="en11pxb">������ʾ��Сֵ</td>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">����δ��ɵ�Ѳ��</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">Ѳ����ʽ(Ѳ������)</td>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">�����Ѿ���ɵ�Ѳ��</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">Ѳ����ʽ(Ѳ������)</td>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">����δ�������豸�嵥</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">�豸��</td>
			          <td   class="en11pxb">�������</td>
			          <td   class="en11pxb">��׼��ʱ</td>
			          <td   class="en11pxb">Ԥ�ƿ�ʼʱ��</td>
			          <td   class="en11pxb">Ԥ�ƽ���ʱ��</td>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">������ɱ������豸�嵥</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">�豸��</td>
			          <td   class="en11pxb">�������</td>
			          <td   class="en11pxb">��׼��ʱ</td>
			          <td   class="en11pxb">ʵ�ʹ�ʱ</td>
			          <td   class="en11pxb">��ʼʱ��</td>
			          <td   class="en11pxb">����ʱ��</td>
			          <td   class="en11pxb">��������</td>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">�������ų��豸�����嵥(�����ձ���)</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">�豸��</td>
			          <td   class="en11pxb">�������</td>
			          <td   class="en11pxb">��׼��ʱ</td>
			          <td   class="en11pxb">Ԥ�ƿ�ʼʱ��</td>
			          <td   class="en11pxb">Ԥ�ƽ���ʱ��</td>

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
	          <td bgcolor="#ACD5C9" class="en11pxb">�������ų��豸�����嵥(�����ձ���)</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">�豸��</td>
			          <td   class="en11pxb">�������</td>
			          <td   class="en11pxb">��׼��ʱ</td>
			          <td   class="en11pxb">Ԥ�ƿ�ʼʱ��</td>
			          <td   class="en11pxb">Ԥ�ƽ���ʱ��</td>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">������(������)��ʩ���豸�����嵥(�����ձ���)</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">�豸��</td>
			          <td   class="en11pxb">�������</td>
			          <td   class="en11pxb">��׼��ʱ</td>
			          <td   class="en11pxb">Ԥ�ƿ�ʼʱ��</td>
			          <td   class="en11pxb">Ԥ�ƽ���ʱ��</td>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">ǰһ�����챣���豸�嵥(�����ձ���)</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">�豸��</td>
			          <td   class="en11pxb">�������</td>
			          <td   class="en11pxb">��׼��ʱ</td>
			          <td   class="en11pxb">����ʱ��</td>
			          <td   class="en11pxb">��������</td>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">���ų�δ������ʷ��¼(������ǰ,��������)(�����ձ���)(�豸״̬��03��05)</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">�豸��</td>
			          <td   class="en11pxb">�������</td>
			          <td   class="en11pxb">��׼��ʱ</td>
			          <td   class="en11pxb">Ԥ�ƿ�ʼʱ��</td>
			          <td   class="en11pxb">Ԥ�ƽ���ʱ��</td>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">���ų�δ������ʷ��¼(������ǰ,��������)(�����ձ���)(�豸״̬03��05)</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">�豸��</td>
			          <td   class="en11pxb">�������</td>
			          <td   class="en11pxb">��׼��ʱ</td>
			          <td   class="en11pxb">Ԥ�ƿ�ʼʱ��</td>
			          <td   class="en11pxb">Ԥ�ƽ���ʱ��</td>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">��������0:00--7:00</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">�豸��</td>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">��������7:00--13:00</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">�豸��</td>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">��������13:00--19:00</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">�豸��</td>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">��������19:00--24:00</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">�豸��</td>
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
	          <td bgcolor="#ACD5C9" class="en11pxb">�¼�list(һ��������ɡ�һ�������޸Ļ�δ��ɵ��쳣��¼��������¼�嵥)</td>
	        </tr>
	  		<tr>
		  		<td>
			      <table width="100%" border="0" cellspacing="1" cellpadding="2">
			      	<tr bgcolor="#ACD5C9">
			          <td   class="en11pxb">EQ ID</td>
			          <td   class="en11pxb">�¼�����/�������</td>
			          <td   class="en11pxb">��������</td>
			          <td   class="en11pxb">״̬</td>
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
				        			STATUS = "�����";
				        		}else{
				        			STATUS = "������";
				        		}
				        		String MAINT_DEPT = UtilFormatOut.checkNull((String)(map.get("MAINT_DEPT")));

				        		 %>
				        		 	<tr bgcolor="<%=getDeptColor(MAINT_DEPT)%>">
										<%
										if (UserDept.equals(MAINT_DEPT)){
											if (TYPE.equals("PM")){
												if (STATUS.equals("������")){
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
												if (STATUS.equals("������")){
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