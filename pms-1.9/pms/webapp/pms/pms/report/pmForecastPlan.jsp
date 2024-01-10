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

	//设备大类选择
	function equipMentChange(){
		var newEqpValue=Ext.get('equipmentType').dom.value;
		var actionURL='<ofbiz:url>/getEqpIdAndPeriod</ofbiz:url>?equipmentType='+newEqpValue;
		if(oldEqpValue!=newEqpValue){
				Ext.lib.Ajax.formRequest('pmForecastPlanForm',actionURL,{success: commentSuccess, failure: commentFailure});
		}
		oldEqpValue=newEqpValue;
	}

	//远程调用成功
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			//设备保养种类数据初始化
			var nameSize=result.priodNameArray.length;
			var nameArray=result.priodNameArray;
			var valueArray=result.priodValueArray;
			var pmObj=document.getElementById("period");
			pmObj.length=1;
			for(var i=0;i<nameSize;i++){
				pmObj.options[pmObj.length]=new Option(nameArray[i],valueArray[i]);
			}

			//设备ID数据初始化
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

   //初如化查询后的界面
   function initQuery(){
   		var eqpIdObj=document.getElementById('equipmentId');
   		eqpIdObj.value='<%=equId%>';

   		var periodObj=document.getElementById('period');
		periodObj.value='<%=period%>';

		document.getElementById('keyEqp').value = '<%=keyEqp%>';
		document.getElementById('adjustEqp').value = '<%=adjustEqp%>';
		document.getElementById('measureEqp').value = '<%=measureEqp%>';
   }

   //远程调用失败
   var commentFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
   };

	//页面重置
	function reset(){
		Ext.get('equipmentType').dom.value = "";
		Ext.get('equipmentId').dom.value = "";
        Ext.get('keyEqp').dom.value = "";
        Ext.get('adjustEqp').dom.value = "";
        Ext.get('measureEqp').dom.value = "";
	}

	//初始化页面控件
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
      <legend>PM预测报表(3 months)</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
	     <tr bgcolor="#DFE1EC">
	       <td width="12%" class="en11pxb">开始日期:</td>
	       <td width="28%">
	       	 <input type="text" id="startDate" name="startDate" value="<%=sDate%>" readonly>
	       </td>

	       <td width="15%" class="en11pxb">设备大类:</td>
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

	    	<td width="15%" class="en11pxb">设备保养种类:</td>
		    <td><select id="period" name="period">
		  		<option value=""></option>
		  	</select></td>
	    </tr>

	    <tr bgcolor="#DFE1EC">
			<td class="en11pxb">关键设备:</td>
			<td>
				<select name='keyEqp' id='keyEqp'>
					<option value='' ></option>
					<option value='Y'>Y</option>
					<option value='N'>N</option>
				</select>
			</td>

			<td class="en11pxb">计量设备:</td>
			<td>
				<select name='adjustEqp' id='adjustEqp'>
					<option value='' ></option>
					<option value='Y'>Y</option>
					<option value='N'>N</option>
				</select>
			</td>
	    </tr>

	    <tr bgcolor="#DFE1EC">
			<td class="en11pxb">校准设备:</td>
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
    					<li><a class="button-text" href="#" onclick="javascript:doSubmit('<%=request.getContextPath()%>/control/pmForecastPlanByPeriod');"><span>&nbsp;查询&nbsp;</span></a></li>
    					</ul>

    					<ul class="button">
    					<li><a class="button-text" href="#" onclick="javascript:doSubmit('<%=request.getContextPath()%>/control/pmForecastPlan');"><span>&nbsp;设备查询&nbsp;</span></a></li>
    					</ul>

    					<ul class="button">
    					<li><a class="button-text" href="#" onclick="javascript:reset();"><span>&nbsp;重置&nbsp;</span></a></li>
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
      <legend>PM计划列表</legend>
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
			            	title = eqpId + " " + scheduleDate + "<br>" + "保养周期 (工时)<br>"
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