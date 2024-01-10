<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.csmc.pms.webapp.workflow.model.*"%>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%
	String startDate=UtilFormatOut.checkNull((String)request.getAttribute("startDate"));
	String endDate=UtilFormatOut.checkNull((String)request.getAttribute("endDate"));
	String eqpId=UtilFormatOut.checkNull((String)request.getAttribute("eqpId"));
	List pmList = (List)request.getAttribute("PMFORM_LIST");
	List dailyJobList = (List)request.getAttribute("dailyJobList");
	String maintDept = UtilFormatOut.checkNull((String) request.getAttribute("maintDept"));
	
%>

<!-- yui page script-->
<script language="javascript">

	//��ѯ
	function query(){
	    var maintDept=Ext.get('maintDept').dom.value;
		var eqpId=Ext.get('eqpId').dom.value;
		var endDate=Ext.get('endDate').dom.value;
		var startDate=Ext.get('startDate').dom.value;

		if (maintDept==""||maintDept==undefined) {
		    Ext.MessageBox.alert('����', '��ѡ����!');
			return;
		}
		
		if (eqpId==""||eqpId==undefined) {
		    Ext.MessageBox.alert('����', '��ѡ���豸!');
			return;
		}
		
		if (startDate==""||startDate==undefined) {
		    Ext.MessageBox.alert('����', '��ѡ������!');
			return;
		}
		else
		{
			var dateArray = startDate.split("-");
			var dateObj = new Date(dateArray[0],dateArray[1]-1,dateArray[2]);
			
			if (dateObj.getDay() != "1")
			{
				Ext.MessageBox.alert('����', '��ѡ����һ!');
				return;
			}
		}


		//if((startDate==""||startDate==undefined) && (endDate==""||endDate==undefined) && eqpId==""){
		//	Ext.MessageBox.alert('����', '�������ѯ����!');
		//	return;
		//}

		loading();
		pmForm.submit();
	}

	//����
	function reset(){
	    Ext.get('maintDept').dom.value='';
		Ext.get('eqpId').dom.value='';
		Ext.get('startDate').dom.value='';
		Ext.get('endDate').dom.value='';
	}

	//������½�ҳ��
	function intoPmForm(index,eqpId){
		loading();
		var actionURL='<ofbiz:url>/overPmFormView</ofbiz:url>?functionType=3&pmIndex='+index+'&eqpId='+eqpId;
		document.location.href=actionURL;
	}

	//��ʼ��ҳ��ؼ�
	Ext.onReady(function(){
	     var startDate = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:true
	    });

	 	var endDate = new Ext.form.DateField({
	 		format: 'Y-m-d',
	        allowBlank:true
	    });

	    startDate.applyTo('startDate');
	    //endDate.applyTo('endDate');

	    var dept = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'maintDept',
	        width:170,
	        forceSelection:true
	    });
	    dept.on('select',loadEqpId);

	    var eqpIdDS = new Ext.data.Store({
	        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentByAnd</ofbiz:url>'}),
		    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
	    });

	    //����eqpId
		var	eqpIdCom = new Ext.form.ComboBox({
		    store: eqpIdDS,
		    displayField:'equipmentId',
		    valueField:'equipmentId',
		    hiddenName:'eqpId',
		    typeAhead: true,
		    mode: 'local',
		    width: 170,
		    triggerAction: 'all'
	    });
	    eqpIdCom.applyTo('eqpIdSelect');

	    function loadEqpId() {
    		var val = dept.getValue();
    		if (val == '<%=maintDept%>') {
    		    var equipmentId  = '<%=UtilFormatOut.checkNull(request.getParameter("eqpId"))%>';
    		} else {
    		    var equipmentId  = '';
    		}

    		eqpIdDS.load({params:{maintDept:val},callback:function(){ eqpIdCom.setValue(equipmentId); }});
    	};

        // initial dept & epqid
    	dept.setValue("<%=maintDept%>");
    	eqpIdCom.setValue('<%=eqpId%>');
    	loadEqpId();

	    initDate();
	 });

	 function initDate(){
		 Ext.get('startDate').dom.value='<%=startDate%>';
		 Ext.get('endDate').dom.value='<%=endDate%>';
	 }

</script>

<form action="<%=request.getContextPath()%>/control/newQueryDailyOverPmFormByCondition" method="post" id="pmForm" onsubmit="return false;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>ÿ�ռ����ѯ</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
	        <td bgcolor="#ACD5C9" class="en11pxb">��ѡ�� ��һ</td>
	        <td width="30%"><input type="text" ID="startDate" NAME="startDate" readonly size="26"></td>
	        
	        <td bgcolor="#ACD5C9" class="en11pxb"> </td>
	        <td width="30%"><input type="text" ID="endDate" NAME="endDate" readonly style="visibility:hidden" size="26"></td>
	    </tr>

        <tr bgcolor="#DFE1EC">
	        <td bgcolor="#ACD5C9" class="en11pxb">����</td>
	        <td width="30%">
	            <select id="maintDept" name="maintDept">
         			<option value=''></option>
	          		<ofbiz:if name="deptList">
		        		<ofbiz:iterator name="cust" property="deptList">
			    			<option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>'>
			    			    <ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>
			    			</option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
    			</select>
    	    </td>

	    	<td width="10%" bgcolor="#ACD5C9" class="en11pxb">EqpId</td>
	    	<td width="45%">
	    	    <input type="text" size="40" name="eqpIdSelect" autocomplete="off"/>
	    	</td>
        </tr>

        <tr bgcolor="#DFE1EC">
        	<td class="en11pxb" colspan="4" align="left">
	        	<table border="0" cellspacing="0" cellpadding="0">
			  		<tr height="30">
					    <td><ul class="button">
							<li><a class="button-text" href="#" onclick="javascript:query();"><span>&nbsp;��ѯ&nbsp;</span></a></li>
						</ul>
						<ul class="button">
							<li><a class="button-text" href="#" onclick="javascript:reset();"><span>&nbsp;����&nbsp;</span></a></li>
						</ul>
						</td>
			  		</tr>
				</table>
        	</td>
        </tr>
      </table>
      </fieldset></td>
  </tr>
</table>
</form>
<br>
<!-- 
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>������¼���б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="10%" class="en11pxb">��̨���</td>
          <td width="20%" class="en11pxb">�����</td>
          <td width="10%" class="en11pxb">׫д��</td>
          <td width="10%" class="en11pxb">������</td>
          <td width="10%" class="en11pxb">�������</td>
          <td width="10%" class="en11pxb">ʵ�ʹ�ʱ</td>
          <td width="15%" class="en11pxb">��ʼʱ��</td>
          <td width="15%" class="en11pxb">����ʱ��</td>
        </tr>
         <% if(pmList != null && pmList.size() > 0) {
       		for(Iterator it = pmList.iterator();it.hasNext();) {
					Map map = (Map)it.next();%>
	        <tr bgcolor="#DFE1EC">
		    <td class="en11px"><a href="#" onclick="intoPmForm('<%=map.get("PM_INDEX")%>','<%=map.get("EQUIPMENT_ID")%>')"><%=map.get("EQUIPMENT_ID")%></a></td>
		    <td class="en11px"><%=map.get("PM_NAME")%></td>
		    <td class="en11px"><%=map.get("ACCOUNT_NAME")%></td>
		    <td class="en11px"><%=map.get("FORM_TYPE")%></td>
		    <td class="en11px"><%=map.get("PERIOD_NAME")%></td>
		    <td class="en11px"><%=map.get("MAN_HOUR")%></td>
		    <td class="en11px"><%=map.get("START_TIME")%></td>
		    <td class="en11px"><%=map.get("END_TIME")%></td>
		    </tr>
	     <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>
 -->
<% 
	if(dailyJobList != null && dailyJobList.size() > 0) 
	{
%>
		<CENTER><p align = "central">�豸ÿ�ռ���</p></CENTER>
		<p align = "central">�豸���ƣ�<%=eqpId %></p>
		<p align = "central">��¼���ڣ�From��<%=startDate %> To��<%=endDate %></p>
<%
		Job[] jobsFromJobList = new Job[7];
		int index_begin = -1;
		int index_end = -1;
		Vector diffs = new Vector();
		long cur_jobIndex = -1;
		// ����jobindex���з��飬��ͬ��job��һ���������ʾ
		for(int k = 0; k < 7; k ++) 
		{
			Job cur_job = null;
			if (k < dailyJobList.size())
			{
				cur_job = ((Job[])dailyJobList.get(k))[0];
			}
			jobsFromJobList[k] = cur_job;
			
			// ��û�ҵ���һ����Ч��job
			if (index_begin == -1)
			{
				// �ҵ���һ����Ϊ�յ�job����һ������ 7 ��job��
				if(cur_job != null)
				{
					index_begin = k;
					index_end = index_begin;
					cur_jobIndex = cur_job.getJobIndex();
				}
			}
			else
			{
				// �жϵ�ǰJobIndex�Ƿ��ǰ�����ͬ
				if(cur_job != null)
				{
					if (cur_job.getJobIndex() == cur_jobIndex)
					{
						// jobIndex��ǰ�����ͬ������index_end
						index_end = k;
					}
					else
					{
						// �µ�һ��jobIndex����Ҫ�ֶδ���
						index_end = k - 1;
						diffs.add(new Integer(index_begin));
						diffs.add(new Integer(index_end));
						// ��ʼ�µķֶΣ�����begin��end��jobindex
						index_begin = k;
						index_end = index_begin;
						cur_jobIndex = cur_job.getJobIndex();
					}
				}
				else
				{
					// ��jobΪ�գ���Ϊ��ǰ�����ͬ
					index_end = k;
				}
			}
		}
		// ��¼���һ���ֶ�
		diffs.add(new Integer(index_begin));
		diffs.add(new Integer(index_end));
		
		for (int k = 0, size = diffs.size(); k < size;)
		{
			int begin = ((Integer)diffs.get(k)).intValue();
			int end = ((Integer)diffs.get(k + 1)).intValue();
			k += 2;
			if (begin != -1 && end != -1)
			{
%>
				<table width="100%" border="1" cellspacing="0" cellpadding="0">
					<tr height="25">
						<td width="4%" class="en11pxb">���</td>
						<td width="20%" class="en11pxb">�����Ŀ</td>
						<td width="10%" class="en11pxb">����</td>
						<td width="10%" class="en11pxb">����</td>
						<td width="8%" class="en11pxb">����һ</td>
						<td width="8%" class="en11pxb">���ڶ�</td>
						<td width="8%" class="en11pxb">������</td>
						<td width="8%" class="en11pxb">������</td>
						<td width="8%" class="en11pxb">������</td>
						<td width="8%" class="en11pxb">������</td>
						<td width="8%" class="en11pxb">������</td>
					</tr>
				
				<%  
						Job job = jobsFromJobList[begin];
						
						if(CommonUtil.isNotNull(job) && CommonUtil.isNotEmpty(job.getActionlist())) 
						{
				   			List action_list = job.getActionlist();
				   			for (int x = 0, size_x = action_list.size(); x < size_x; x++)
				   			{
				   				Action action = (Action)action_list.get(x); 
				%>
						    	<tr height="25">
						    		<td class="en11px"><%=action.getActionId()%></td>
								  	<td class="en11px"><%=action.getActionName()%><%if(Constants.ACTION_DCOP_TYPE.equals(action.getActionType())) { %><font color="#ff0000"> DCOP</font><% } %></td>
								  	<td class="en11px">&nbsp;</td>
								  	<td class="en11px">&nbsp;</td>
								  	<td class="en11px">&nbsp;</td>
								  	<td class="en11px">&nbsp;</td>
								  	<td class="en11px">&nbsp;</td>
								  	<td class="en11px">&nbsp;</td>
								  	<td class="en11px">&nbsp;</td>
								  	<td class="en11px">&nbsp;</td>
								  	<td class="en11px">&nbsp;</td>
								  	<!-- <td class="en11px"><%if("action".equals(action.getNodeType())) { %><%=UtilFormatOut.checkNull(action.getActionNote())%><% } %></td> -->
							  	</tr>
					  			<% 
					  				if(CommonUtil.isNotEmpty(action.getItemlist())) 
					  			  	{
					  			  		List item_list = action.getItemlist();
				   						for (int y = 0, size_y = item_list.size(); y < size_y; y++)
				   						{
					  						ActionItem item = (ActionItem)item_list.get(y); 
								%>
								              	<tr height="25"<% if(item.isOOS()) { %> bgcolor="#ff0000"<% } else { %> bgcolor="#DFE1EC"<% } %>>
									                <td class="en11px">&nbsp;</td>
									                <td class="en11px"><%=item.getItemName()%></td>
									                <td class="en11px"><% if(item.getItemType()==Constants.NUMBER_ITEM) { out.println(CommonUtil.checkDoubleNull(item.getItemUpperSpec()));}%></td>
									                <td class="en11px"><% if(item.getItemType()==Constants.NUMBER_ITEM) { out.println(CommonUtil.checkDoubleNull(item.getItemLowerSpec()));}%></td>
									                
								<%
									    	for (int u = 0; u < 7; u ++)
									    	{
									    		if (u >= begin && u <= end)
									    		{
									    			if (jobsFromJobList[u] != null)
									    			{
									    				ActionItem cur_item = (ActionItem)((Action)jobsFromJobList[u].getActionlist().get(x)).getItemlist().get(y);
								%>	    												                
													<td class="en11px"><%=UtilFormatOut.checkNull(cur_item.getItemValue())%>&nbsp;<%=UtilFormatOut.checkNull(cur_item.getItemNode())%></td>
								<%	                
													}
									    		}
									    		else
									    		{
								%>
									    			<td class="en11px">&nbsp;</td>
								<%
									    		}
									    	}
								%>
												</tr>
								<%	    		
										 }
									}
								%>
				<%
							}
						} 
				%>
				</table>
<%
			} 
		}
	}
%>

