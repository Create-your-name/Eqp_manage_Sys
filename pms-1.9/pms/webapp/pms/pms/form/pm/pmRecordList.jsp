<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%
	String eqpId=UtilFormatOut.checkNull(request.getParameter("eqpId"));
	String startDate=UtilFormatOut.checkNull(request.getParameter("startDate"));
	String endDate=UtilFormatOut.checkNull(request.getParameter("endDate"));
	List pmList = (List)request.getAttribute("pmRecordList");
%>
<!-- yui page script-->
<script language="javascript">
	//查询
	function query(){
		var eqpId=Ext.get('eqpId').dom.value;
		var startDate=Ext.get('startDate').dom.value;
		var endDate=Ext.get('endDate').dom.valu;
		if((eqpId==""||eqpId==undefined) && (startDate==""||startDate==undefined) &&(endDate==""||endDate==undefined)){
			Ext.MessageBox.alert('警告', '查询条件不能都为空!');
			return;
		}
		loading();
		pmForm.submit();
	}

	var eqpId;

	//初始化页面控件
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

	    eqpId = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'eqpId',
	        width:170,
	        forceSelection:true
	    });
	    initEqpState();
	 });

	 function initEqpState(){
		 eqpId.setValue('<%=eqpId%>');
		 Ext.get('startDate').dom.value='<%=startDate%>';
		 Ext.get('endDate').dom.value='<%=endDate%>';
	 }

	function addPatchPmForm(scheduleIndex,scheduleDate,periodIndex,eqpId){
		loading();
	 	Ext.get('scheduleIndex').dom.value=scheduleIndex;
	 	Ext.get('scheduleDate').dom.value=scheduleDate;
	 	Ext.get('periodIndex').dom.value=periodIndex;
	 	Ext.get('queryEqpId').dom.value=eqpId;
	 	pmFormAdd.submit();
	}

	//建立当天的保养表单
	function todayPmAdd(){
		var eqpId=Ext.get('eqpId').dom.value;
		if((eqpId==""||eqpId==undefined)){
			Ext.MessageBox.alert('警告', '请选择设备编号!');
			return;
		}

	 	var date = new Date();
	 	var scheduleDate=date.getYear()+"-"+(date.getMonth()+1)+"-"+date.getDate();
	 	Ext.get('queryEqpId').dom.value=Ext.get('eqpId').dom.value;
	 	Ext.get('scheduleDate').dom.value=scheduleDate;
		pmFormAdd.action='<%=request.getContextPath()%>/control/addPmFormEntry'
		pmFormAdd.submit();
	}
</script>
<form action="<%=request.getContextPath()%>/control/queryPmRecordEntry" method="post" id="pmForm" onsubmit="return false;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>建立保养表单</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
          <td width="20%" bgcolor="#ACD5C9" class="en11pxb">设备</td>
          <td width="80%" colspan="3"><select id="eqpId" name="eqpId">
		          <option value=""></option>
		          <ofbiz:if name="equipmentList">
			        <ofbiz:iterator name="equipment" property="equipmentList">
				    <option value='<ofbiz:inputvalue entityAttr="equipment" field="equipmentId"/>'><ofbiz:inputvalue entityAttr="equipment" field="equipmentId"/></option>
			      </ofbiz:iterator>
		      	</ofbiz:if>
    		</select>
    		</td>
        </tr>
        <tr bgcolor="#DFE1EC">
	        <td bgcolor="#ACD5C9" class="en11pxb">日期</td>
	        <td width="30%"><input type="text" ID="startDate" NAME="startDate" readonly size="26"></td>
	    	<td  width="5%" class="en11pxb">到:</td>
	    	<td width="45%"><input type="text" ID="endDate" NAME="endDate"  readonly size="26"></td>
        </tr>
        <tr bgcolor="#DFE1EC">
        	<td class="en11pxb" colspan="4" align="left">
	        	<table border="0" cellspacing="0" cellpadding="0">
			  		<tr height="30">
					    <td><ul class="button">
							<li><a class="button-text" href="#" onclick="javascript:todayPmAdd();"><span>&nbsp;建立当日保养表单&nbsp;</span></a></li>
						</ul>
					    <ul class="button">
							<li><a class="button-text" href="#" onclick="javascript:query();"><span>&nbsp;查询补填保养记录&nbsp;</span></a></li>
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
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>未建立的保养表单(补填)(设备状态非03或05)</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="20%" class="en11pxb">机台</td>
          <td width="20%" class="en11pxb">保养类型</td>
          <td width="25%" class="en11pxb">日期</td>
        </tr>
         <% if(pmList != null && pmList.size() > 0) {
       		for(Iterator it = pmList.iterator();it.hasNext();) {
					Map map = (Map)it.next();%>
	        <tr bgcolor="#DFE1EC">
		    <td class="en11px"><a href="#" onclick="addPatchPmForm('<%=map.get("SCHEDULEINDEX")%>','<%=map.get("SCHEDULEDATE")%>','<%=map.get("PERIODINDEX")%>','<%=map.get("EQUIPMENTID")%>')"><%=map.get("EQUIPMENTID")%></a></td>
		    <td class="en11px"><%=map.get("PERIODNAME")%></td>
		    <td class="en11px"><%=map.get("SCHEDULEDATE")%></td>
		    </tr>
	     <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>
<form action="<%=request.getContextPath()%>/control/addPatchPmFormEntry" method="post" id="pmFormAdd" onsubmit="return false;">
<input id="scheduleIndex" type="hidden" name="scheduleIndex" />
<input id="periodIndex" type="hidden" name="periodIndex"/>
<input id="queryEqpId" type="hidden" name="queryEqpId"/>
<input id="scheduleDate" type="hidden" name="scheduleDate"/>
</form>
