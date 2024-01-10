<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.Integer" %>

<%
	List pmScheduleList = (List)request.getAttribute("pmScheduleList");
	List monthList = (List)request.getAttribute("monthList");
	String equ = UtilFormatOut.checkNull(request.getParameter("equipmentType"));
	String equId = request.getParameter("equipmentId");
	String period = request.getParameter("period");
	String keyEqp = request.getParameter("keyEqp");
	String adjustEqp = request.getParameter("adjustEqp");
	String measureEqp = request.getParameter("measureEqp");

	String sDate = request.getParameter("startDate");

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	if (null == sDate) {
		sDate = formatter.format(Calendar.getInstance().getTime());
	}

	String flag = (String) request.getAttribute("flag");


 %>

<script language='javascript' src='<%=request.getContextPath()%>/images/pupdate.js' type='text/javascript'></script>

<script language="javascript">
	var oldEqpValue="";
	var flag='<%=flag%>';

	function doSubmit(url) {
		document.pmForecastPlanForm.action = url;
		document.pmForecastPlanForm.submit();
	}

	//�豸����ѡ��
	function equipMentChange(){
		var newEqpValue=Ext.get('equipmentType').dom.value;
		var actionURL='<ofbiz:url>/getEqpIdAndPeriod</ofbiz:url>?equipmentType='+newEqpValue;
		if(oldEqpValue!=newEqpValue){
				Ext.lib.Ajax.formRequest('pmForecastPlanForm',actionURL,{success: commentSuccess, failure: commentFailure});
		}
		oldEqpValue=newEqpValue;
	}

	//Զ�̵��óɹ�
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			//�豸�����������ݳ�ʼ��
			var nameSize=result.priodNameArray.length;
			var nameArray=result.priodNameArray;
			var valueArray=result.priodValueArray;
			var pmObj=document.getElementById("period");
			pmObj.length=1;
			for(var i=0;i<nameSize;i++){
				pmObj.options[pmObj.length]=new Option(nameArray[i],valueArray[i]);
			}

			//�豸ID���ݳ�ʼ��
			var equipId=document.getElementById("equipmentId");
			var eqpArray=result.eqpIdArray;
			var eqpIdSize=result.eqpIdArray.length;
			equipId.length=1;
			for(var i=0;i<eqpIdSize;i++){
				equipId.options[equipId.length]=new Option(eqpArray[i],eqpArray[i]);
			}

			if(flag=='true'){
				initQuery();
				flag='false';
			}
		}
	}

   //���绯��ѯ��Ľ���
   function initQuery(){
   		var eqpIdObj=document.getElementById('equipmentId');
   		eqpIdObj.value='<%=equId%>';

   		var periodObj=document.getElementById('period');
		periodObj.value='<%=period%>';

		document.getElementById('keyEqp').value = '<%=keyEqp%>';
		document.getElementById('adjustEqp').value = '<%=adjustEqp%>';
		document.getElementById('measureEqp').value = '<%=measureEqp%>';
   }

   //Զ�̵���ʧ��
   var commentFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
   };

	//ҳ������
	function reset(){
		Ext.get('equipmentType').dom.value = "";
		Ext.get('equipmentId').dom.value = "";
        Ext.get('keyEqp').dom.value = "";
        Ext.get('adjustEqp').dom.value = "";
        Ext.get('measureEqp').dom.value = "";
	}

	//��ʼ��ҳ��ؼ�
	Ext.onReady(function(){
	    var startDate = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:false
	    });

	    startDate.applyTo('startDate');

	    var eqpType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'equipmentType',
	        width:170,
	        forceSelection:true
	    });
	    eqpType.on('select',equipMentChange);
	 });
</script>

<form method="post" id="pmForecastPlanForm" name="pmForecastPlanForm">
<table width="700" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>PMԤ�ⱨ��(3 months)</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
	     <tr bgcolor="#DFE1EC">
	       <td width="12%" class="en11pxb">��ʼ����:</td>
	       <td width="28%">
	       	 <input type="text" id="startDate" name="startDate" value="<%=sDate%>" readonly>
	       </td>

	       <td width="15%" class="en11pxb">�豸����:</td>
	       <td><select id="equipmentType" name="equipmentType">
	          <option value=""></option>
	          <ofbiz:if name="equipMentList">
		        <ofbiz:iterator name="cust" property="equipMentList">
			    <option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentType"/>'><ofbiz:inputvalue entityAttr="cust" field="equipmentType"/></option>
		      </ofbiz:iterator>
	      	</ofbiz:if>
    		</select></td>
	    </tr>

	    <tr bgcolor="#DFE1EC">
		    <td width="12%" class="en11pxb">EqpId:</td>
		    <td width="28%"><select id="equipmentId" name="equipmentId">
		          <option value=""></option>
	    	</select></td>

	    	<td width="15%" class="en11pxb">�豸��������:</td>
		    <td><select id="period" name="period">
		  		<option value=""></option>
		  	</select></td>
	    </tr>

	    <tr bgcolor="#DFE1EC">
			<td class="en11pxb">�ؼ��豸:</td>
			<td>
				<select name='keyEqp' id='keyEqp'>
					<option value='' ></option>
					<option value='Y'>Y</option>
					<option value='N'>N</option>
				</select>
			</td>

			<td class="en11pxb">�����豸:</td>
			<td>
				<select name='adjustEqp' id='adjustEqp'>
					<option value='' ></option>
					<option value='Y'>Y</option>
					<option value='N'>N</option>
				</select>
			</td>
	    </tr>

	    <tr bgcolor="#DFE1EC">
			<td class="en11pxb">У׼�豸:</td>
			<td colspan="3">
				<select name='measureEqp' id='measureEqp'>
					<option value='' ></option>
					<option value='Y'>Y</option>
					<option value='N'>N</option>
				</select>
			</td>

	    </tr>

	    <tr bgcolor="#DFE1EC">
	   		<td width="20%" class="en11pxb" align="left" colspan="4">
		    	<table border="0" cellspacing="0" cellpadding="0">
				  <tr height="30" >
				   	<td width="0">&nbsp;</td>
				    <td>
				        <ul class="button">
    					<li><a class="button-text" href="#" onclick="javascript:doSubmit('<%=request.getContextPath()%>/control/pmForecastPlanByPeriod');"><span>&nbsp;��ѯ&nbsp;</span></a></li>
    					</ul>

    					<ul class="button">
    					<li><a class="button-text" href="#" onclick="javascript:doSubmit('<%=request.getContextPath()%>/control/pmForecastPlan');"><span>&nbsp;�豸��ѯ&nbsp;</span></a></li>
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

<%if(monthList != null && monthList.size() > 0) {%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>PM�ƻ��б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
        	<td rowspan="2" class="en11pxb">EqpId</td>
          <%
              Map monthMap;
              String yearMonth;
              int startDayOfMonth;
              int endDayOfMonth;
              int i, j;

              for (i = 0;i < monthList.size();i++) {
		        monthMap = (HashMap)monthList.get(i);
		        yearMonth = (String) monthMap.get("yearMonth");
		        startDayOfMonth = Integer.parseInt((String) monthMap.get("startDayOfMonth"));
		        endDayOfMonth = Integer.parseInt((String) monthMap.get("endDayOfMonth"));
		  %>
                <td colspan="<%=endDayOfMonth-startDayOfMonth+1%>" class="en11pxb"><%=yearMonth%></td>
          <%}%>
        </tr>

        <tr bgcolor="#ACD5C9">
    	  <%
              for (i = 0;i < monthList.size();i++) {
		        monthMap = (HashMap)monthList.get(i);
		        yearMonth = (String) monthMap.get("yearMonth");
		        startDayOfMonth = Integer.parseInt((String) monthMap.get("startDayOfMonth"));
		        endDayOfMonth = Integer.parseInt((String) monthMap.get("endDayOfMonth"));

		        for (j = startDayOfMonth;j <= endDayOfMonth; j++) {
		  			%><td class="en11pxb"><%=j%></td><%
                }
              }
          %>
    	</tr>

    	<%
    	if(pmScheduleList != null && pmScheduleList.size() > 0) {
    		Map map;
			String tmpEqpId = "";
			String eqpId;
			String scheduleDate;
			int countPeriod;
			String title;

       		for (int k = 0;k < pmScheduleList.size();k++) {
				map = (HashMap) pmScheduleList.get(k);
				eqpId = (String)map.get("EQUIPMENT_ID");
				scheduleDate = (String) map.get("SCHEDULE_DATE");

				if (!tmpEqpId.equals(eqpId)) {
					if (tmpEqpId.equals("")) {
						out.println("<tr bgcolor='#DFE1EC'>");
					} else {
						out.println("</tr><tr bgcolor='#DFE1EC'>");
					}
					tmpEqpId = eqpId;

					%><td class="en11pxb"><%=eqpId%></td><%
				}

				for (i = 0;i < monthList.size();i++) {
				    monthMap = (HashMap)monthList.get(i);
				    yearMonth = (String) monthMap.get("yearMonth");
				    startDayOfMonth = Integer.parseInt((String) monthMap.get("startDayOfMonth"));
				    endDayOfMonth = Integer.parseInt((String) monthMap.get("endDayOfMonth"));

				    for (j = startDayOfMonth;j <= endDayOfMonth; j++) {
				    	countPeriod = 1;
				    	String dayOfMonth = "";

				    	if (j<10) {
				    		dayOfMonth = "0" + j;
				    	} else {
				    		dayOfMonth = "" + j;
				    	}

			            %><td class="en11pxb"><%

			            if (scheduleDate.equals(yearMonth + "/" + dayOfMonth)) {
			            	title = eqpId + " " + scheduleDate + "<br>" + "�������� (��ʱ)<br>"
			            			+ (String)map.get("PERIOD_NAME") + " (" + (String)map.get("STANDARD_HOUR") + ")";

			            	while (k < pmScheduleList.size()-1) {
				        		map = (HashMap) pmScheduleList.get(k+1);
								eqpId = (String) map.get("EQUIPMENT_ID");

								if (tmpEqpId.equals(eqpId)) {
									k = k + 1;
									String tmpScheduleDate = scheduleDate;
									scheduleDate = (String) map.get("SCHEDULE_DATE");

									if (scheduleDate.equals(tmpScheduleDate)) {
										title = title + "<br>" + (String)map.get("PERIOD_NAME") + " (" + (String)map.get("STANDARD_HOUR") + ")";
										countPeriod++;
									} else {
										break;
									}
								} else {
									break;
								}
							}//end while

							out.print("<a title='" + title + "'>" + countPeriod + "</a>");
			            }

			            %></td><%
		            }
				}//end monthList
			}//end pmScheduleList
		}
		%>
		</tr>
      </table>
      </fieldset></td>
  </tr>
</table>

<%}%>

<script language="javascript">
	var obj=document.getElementById('equipmentType');
	obj.value='<%=equ%>'
	equipMentChange();
</script>