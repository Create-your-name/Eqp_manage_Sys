<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>

<%
    String startDate = (String) request.getAttribute("startDate");
	String endDate = (String) request.getAttribute("endDate");
	String styleIndex = (String) request.getAttribute("styleIndex");

	List pcFormList = (List) request.getAttribute("pcFormList");
 %>

<!--include yui css-->
<script language="javascript">
	function doSubmit()	{
	    if(document.pcForm.styleIndex.value=="") {
			alert("请选择巡检style！");
			return;
		}

		loading();
		document.pcForm.submit();
	}

	/*function showJob(jobIndex) {

        var url = "<ofbiz:url>/showFlow</ofbiz:url>?jobIndex=" + jobIndex;
        window.open(url,"job",
                "top=130,left=240,width=600,height=400,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=no");
    }*/

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
	    endDate.applyTo('endDate');

	    var styleIndex = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'styleIndex',
	        width:170,
	        forceSelection:true
	    });
	});
</script>

<form name="pcForm" method="post" action="<%=request.getContextPath()%>/control/queryPcFormEntry">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <fieldset><legend>查询巡检记录</legend>

      	<table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">

      	    <tr bgcolor="#DFE1EC">
				<td width="15%" class="en11pxb" bgcolor="#ACD5C9">巡检开始时间</td>
				<td width="28%">
				    <input type="text" id="startDate" name="startDate" value="<%=startDate%>" readonly size="26">
				</td>
				<td width="15%" bgcolor="#ACD5C9">到:</td>
				<td width="28%">
				    <input type="text" id="endDate" name="endDate" value="<%=endDate%>" readonly size="26">
				</td>
		    </tr>

			<tr bgcolor='#DFE1EC'>
				<td width="10%" class="en11pxb" bgcolor="#ACD5C9">巡检style</td>
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

		   	 	<td align="left" colspan="2">
		   	 		<ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:doSubmit();"><span>&nbsp;查询&nbsp;</span></a></li>
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
          <td class="en11pxb">描述</td>
          <td class="en11pxb">巡检开始日期</td>
          <td class="en11pxb">巡检结束日期</td>
          <td class="en11pxb">表单编号</td>
          <td class="en11pxb">状态</td>
        </tr>

		<%
			if(pcFormList != null && pcFormList.size() > 0) {

	       		for(Iterator it = pcFormList.iterator();it.hasNext();) {
					Map map = (Map) it.next();

                    String periodName = UtilFormatOut.checkNull((String) map.get("PERIOD_NAME"));
					String periodDesc = UtilFormatOut.checkNull((String) map.get("PERIOD_DESC"));
					String startTime = UtilFormatOut.checkNull((String) map.get("START_TIME"));
					String endTime = UtilFormatOut.checkNull((String) map.get("END_TIME"));
					String pcName = UtilFormatOut.checkNull((String) map.get("PC_NAME"));
					String pcIndex = UtilFormatOut.checkNull((String) map.get("PC_INDEX"));
					String status = UtilFormatOut.checkNull((String) map.get("STATUS"));
					String jobRelationIndex = UtilFormatOut.checkNull((String) map.get("SEQ_INDEX"));

		%>
			        <tr bgcolor="#DFE1EC">
			            <td class="en11px"><%=periodName%></td>
			        	<td class="en11px"><%=periodDesc%></td>
			        	<td class="en11px"><%=startTime%></td>
			        	<td class="en11px"><%=endTime%></td>
			        	<td class="en11px">
			        	    <a href="javascript:document.location.href=encodeURI('inputPcFormEntry?periodName=<%=periodName%>&pcIndex=<%=pcIndex%>&jobRelationIndex=<%=jobRelationIndex%>&status=<%=status%>')">
			        	        <%=pcName%>
			        	    </a>
			        	 </td>
			        	 <td class="en11px"><%=status%></td>
			        </tr>
	  	<%

				}//end for

			}//end if
		%>

      </table>
      </fieldset></td>
  </tr>
</table>

<script language="javascript">

	var obj = document.getElementById('styleIndex');
	obj.value = '<%=styleIndex%>';

</script>