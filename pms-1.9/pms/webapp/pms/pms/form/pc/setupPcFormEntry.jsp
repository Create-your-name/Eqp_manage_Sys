<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ include file="../../yui-ext/ext-comdlg.jsp"%>

<%
	List pcFormList = (List) request.getAttribute("pcFormList");
	String styleIndex = (String) request.getAttribute("styleIndex");

	Calendar now = new GregorianCalendar();
	now.setTime(new Date());
	String strNow = now.get(now.YEAR)+"/"+(now.get(now.MONTH)+1)+"/"+now.get(now.DATE);
 %>

<!--include yui css-->
<script language="javascript">
	function doSubmit()	{
	    if(document.pcStyleForm.styleIndex.value=="") {
			alert("请选择巡检style！");
			return;
		}

		loading();
		document.pcStyleForm.submit();
	}

	function showJob(jobIndex) {

        var url = "<%=request.getContextPath()%>/pms/workflow/flowShow.jsp?jobIndex=" + jobIndex;
        window.open(url,"job",
                "top=130,left=240,width=600,height=400,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=no");
    }


	//数据合法性校验
	function checkForm(){
		var note = Ext.get('note').dom.value;
		if(note==""){
			//return "备注不能为空";
		}

		return "";
	}

	//改变弹出框大小width,height
	extDlg.dlgInit('500','300');

	//新增功能弹出页
	function addPcForm(obj, styleIndex, periodIndex, periodName, scheduleIndex, scheduleDate){
		Ext.get('formStyleIndex').dom.value = styleIndex;
		Ext.get('periodIndex').dom.value = periodIndex;
		Ext.get('periodName').dom.value = periodName;
		Ext.get('scheduleIndex').dom.value = scheduleIndex;
		Ext.get('scheduleDate').dom.value = scheduleDate;

		Ext.get('note').dom.value="";
		extDlg.showAddDialog(obj);
	}

	Ext.onReady(function(){
	    var styleIndex = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'styleIndex',
	        width:170,
	        forceSelection:true
	    });
	});
</script>

<form name="pcStyleForm" method="post" action="<%=request.getContextPath()%>/control/setupPcFormEntry">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <fieldset><legend>建立/填写巡检表单</legend>

      	<table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">
			<tr bgcolor='#DFE1EC'>
				<td width="10%" class="en11pxb">巡检style</td>
				<td width="20%" align="left">
					<select name="styleIndex" id="styleIndex">
			          <ofbiz:if name="pcStyleList">
				        <ofbiz:iterator name="cust" property="pcStyleList">
					    <option value='<ofbiz:inputvalue entityAttr="cust" field="styleIndex"/>'>
					    	<ofbiz:inputvalue entityAttr="cust" field="name"/>
					    </option>
				      </ofbiz:iterator>
			      	</ofbiz:if>
		    		</select>
				</td>

		   	 	<td align="left">
		   	 		<ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:doSubmit();"><span>&nbsp;查询当日巡检&nbsp;</span></a></li>
					</ul>
				</td>
			</tr>
		</table>
	  </fieldset>
	</td>
  <tr>
</table>
</form>

<br />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>巡检表单</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td class="en11pxb">巡检周期</td>
          <td class="en11pxb">表单编号</td>
          <td class="en11pxb">使用流程</td>
          <td class="en11pxb">表单状况</td>
        </tr>

		<%
			if(pcFormList != null && pcFormList.size() > 0) {

				String periodName = "";
				String periodIndex = "";
				String pcNames = "";
				String jobIndex = "";
				String jobName = "";
				String status = "";
				String scheduleIndex = "";
				String scheduleDate = "";

	       		for(Iterator it = pcFormList.iterator();it.hasNext();) {
					Map map = (Map)it.next();

                    String tmpScheduleIndex = UtilFormatOut.checkNull((String) map.get("SCHEDULE_INDEX"));
					String tmpPeriodName = UtilFormatOut.checkNull((String) map.get("PERIOD_NAME"));
					String tmpPeriodIndex = UtilFormatOut.checkNull((String) map.get("PERIOD_INDEX"));
					String tmpStatus = UtilFormatOut.checkNull((String) map.get("STATUS"));
					String tmpJobIndex = UtilFormatOut.checkNull((String) map.get("JOB_INDEX"));
					String tmpJobName = UtilFormatOut.checkNull((String) map.get("JOB_NAME"));
					String tmpScheduledate = UtilFormatOut.checkNull((String) map.get("SCHEDULE_DATE"));

					String pcIndex = UtilFormatOut.checkNull((String) map.get("PC_INDEX"));
					String jobRelationIndex = UtilFormatOut.checkNull((String) map.get("SEQ_INDEX"));
					String pcName = UtilFormatOut.checkNull((String) map.get("PC_NAME"));
					String tmpPcName = "&nbsp;&nbsp;&nbsp;&nbsp;"
					        + "<a href='javascript:document.location.href=encodeURI(\"inputPcFormEntry?periodName=" + tmpPeriodName
					        + "&pcIndex=" + pcIndex + "&jobRelationIndex=" + jobRelationIndex
					        + "&status=" + tmpStatus + "\")'>"
					        + pcName + "</a>";

					if ("".equals(periodIndex)) {
						periodIndex = UtilFormatOut.checkNull((String) map.get("PERIOD_INDEX"));
						scheduleIndex = UtilFormatOut.checkNull((String) map.get("SCHEDULE_INDEX"));
					}

					if (!periodIndex.equals(tmpPeriodIndex) ) {
		%>
				        <tr bgcolor="#DFE1EC">
				        	<td class="en11px"><%=periodName%></td>

				        	<td class="en11px">
				        		<a href="#" onclick="addPcForm(this,'<%=styleIndex%>','<%=periodIndex%>','<%=periodName%>','<%=scheduleIndex%>','<%=scheduleDate%>')">
				        			建立巡检表单
				        		</a>
				        		<%=pcNames%>
				        	</td>

				        	<td class="en11px"><a href="javascript:showJob(<%=jobIndex%>)"><%=jobName%></a></td>
				        	<td class="en11px"><%=status%></td>
				        </tr>
	  	<%
	  					pcNames = tmpPcName;
	  					status = tmpStatus;
	  				} else {
						pcNames = pcNames + "<br>" + tmpPcName;
                        status = status + "<br>" + tmpStatus;
					}

					scheduleIndex = tmpScheduleIndex;
					periodName = tmpPeriodName;
					periodIndex = tmpPeriodIndex;
					jobIndex = tmpJobIndex;
					jobName = tmpJobName;
					scheduleDate = tmpScheduledate;
				}//end for
		%>
				<tr bgcolor="#DFE1EC">
					<td class="en11px"><%=periodName%></td>

					<td class="en11px">
						<a href="#" onclick="addPcForm(this,'<%=styleIndex%>','<%=periodIndex%>','<%=periodName%>','<%=scheduleIndex%>','<%=scheduleDate%>')">
							建立巡检表单
						</a>
						<br><%=pcNames%>
					</td>

					<td class="en11px"><a href="javascript:showJob(<%=jobIndex%>)"><%=jobName%></a></td>
					<td class="en11px"><%=status%></td>
				</tr>
		<%
			}//end if
		%>

      </table>
      </fieldset></td>
  </tr>
</table>

<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">巡检表单</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="巡检表单">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/setupPcForm" method="post" id="pcForm" onsubmit="return false;">
                	<input type="hidden" id="formStyleIndex" name="formStyleIndex" value="" />
                	<input type="hidden" id="periodIndex" name="periodIndex" value="" />
                	<input type="hidden" id="scheduleIndex" name="scheduleIndex" value="" />

				<p>
                	<label><small>巡检表单</small></label>
                	<input class="textinput" type="text" name="periodName" id="periodName" value="" size="22" tabindex="1" readonly />
                </p>

                <p>
                	<label><small>巡检计划日期</small></label>
                	<input class="textinput" type="text" name="scheduleDate" id="scheduleDate" value="<%=strNow%>" size="22" tabindex="2" readonly />
                </p>

                <p>
	                <label><small>备注</small></label>
                	<input class="textinput" type="text" name="note" id="note" value="" size="22" tabindex="3"/>
                </p>

                </form>
            </div>
        </div>
    </div>
    <div class="x-dlg-ft">
        <div id="dlg-msg">
            <span id="post-error" class="posting-msg"><img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" />&nbsp;<span id="post-error-msg"></span></span>
        </div>
    </div>
</div>

<script language="javascript">

	var obj=document.getElementById('styleIndex');
	obj.value = '<%=styleIndex%>';

</script>