<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*"%>
<%
		String date1 = "";
		String date2 = "";		
		if(pageContext.findAttribute("date1")!=null){
			date1 = request.getAttribute("date1").toString();
		}
		if(pageContext.findAttribute("date2")!=null){
			date2 = request.getAttribute("date2").toString();
		}
%>
<script language="javascript">

	function editDocument(abnormalDocIndex){
		var flag = "123";
		if (abnormalDocIndex==""){
			flag = "new"
		}
		loading();;
		document.queryNotEndDocument.action = '<ofbiz:url>/abnormalDocDefineSingle21</ofbiz:url>?improveDocIndex='+abnormalDocIndex+'&type='+document.queryImproveDocument.type.value+'&flag='+flag;
		document.queryNotEndDocument.submit();	
	}
	
	function reasonDocument(url){
		if (queryNotEndDocument.create_Time1.value == ""){
			Ext.MessageBox.alert("警告","开始日期 不能为空！");
			return;
		}
		if (queryNotEndDocument.create_Time2.value == ""){
			Ext.MessageBox.alert("警告","结束日期 不能为空！");
			return;
		}
		var strTemp = queryNotEndDocument.type.value;
		loading();
		document.queryNotEndDocument.action = url;
		document.queryNotEndDocument.submit();	
		//document.queryImproveDocument.type.value = strTemp;
	}
	
	function editJob(jobRelationIndex) {
       var url = "<ofbiz:url>/editFlowData</ofbiz:url>?jobRelationIndex=" + jobRelationIndex;
       window.open(url,"job","top=130,left=240,width=600,height=400,title=,channelmode=0," +
           "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
           "scrollbars=1,status=1,titlebar=0,toolbar=no");
   }

	Ext.onReady(function(){
	    var create_Time1 = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:true
	    });
	    var create_Time2 = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:true
	    });
	    //将控件与页面的INPUT框捆绑
	    create_Time1.applyTo('create_Time1');   
	    create_Time2.applyTo('create_Time2');  
	    
	 
	    var type = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'type',
	        width:100,
	        forceSelection:true
	    });
	    
	    <% String type = request.getParameter("type");%>
	    type.setValue("<%=type==null?"PM":type%>");
	});
</script>
<form action="<%=request.getContextPath()%>/control/queryNotEndDocument"  name="queryNotEndDocument" method="POST" id ="queryNotEndDocument" >
<!--copy area-->
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="98%"><fieldset><legend>未填数据查询</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
     	<tr bgcolor="#DFE1EC"> 
        <!-----部门的设备ID---->
          <td width="12%" bgcolor="#ACD5C9" class="en11pxb">开始日期</td>
          <td width="13%" class="en11px"><input type="text" ID="create_Time1" NAME="create_Time1" readonly value = <%=date1%>  ></td>
          <td width="14%" bgcolor="#ACD5C9" class="en11pxb">结束日期</td>
          <td width="17%" class="en11px"><input type="text" ID="create_Time2" NAME="create_Time2" readonly value = <%=date2%>></td>
        </tr>
        <tr bgcolor="#DFE1EC"> 
        <!-----部门的设备ID---->
          <td width="12%" bgcolor="#ACD5C9" class="en11pxb">表单类型</td>
          <td width="13%" class="en11pxb"><select id="type" name="type">
	    	  	<option value="PM">保养</option>
	     	 	<option value="TS">异常</option>
	     	 	<option value="PC">巡检</option>
     		 </select>
			</td>
          <td width="17%" class="en11pxb">&nbsp;</td>
          <td width="17%" class="en11pxb">&nbsp;</td>
        </tr>
      </table>
	 </fieldset> 
	</td>
  <tr> 
</table>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:reasonDocument('<%=request.getContextPath()%>/control/queryNotEndDocumentSearch');"><span>&nbsp;显示&nbsp;</span></a></li> 
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.queryNotEndDocument.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
    <td></td>
  </tr>
</table>
</form>

<br>
<ofbiz:if name="flag" value="OK">
<div id="queryImproveDocument1" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="queryImproveDocument1" style="visibility:'hidden';">
</ofbiz:unless>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
    <td width="98%"><fieldset><legend>未填数据查询</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td class="en11pxb">表单名</td>
      		<td class="en11pxb">设备名</td>
            <td class="en11pxb">周期</td>
            <td class="en11pxb">流程名</td>
            <td class="en11pxb">表单完成日期</td>
    	</tr>
    	
    	<%
    		String formIndex = "";
    		String EQUIPMENT_ID = "";
    		String PERIOD_NAME = "";
    		String JOB_NAME = "";
    		String SEQ_INDEX = "";
    		String END_TIME = "";
    		String formName = "";
        	List flowList = (List)request.getAttribute("DocumentList");
        	if(flowList != null && flowList.size() > 0) {
        	for (int i = 0 ; i <= flowList.size()-1 ; i++){
        		Map map = (Map)flowList.get(i);
        		formIndex = (String)(map.get("FORM_INDEX"));
        		formName = (String)(map.get("FORM_NAME"));
        		EQUIPMENT_ID = (String)(map.get("EQUIPMENT_ID"));
        		PERIOD_NAME = (String)(map.get("PERIOD_NAME"));
        		JOB_NAME = (String)(map.get("JOB_NAME"));
        		SEQ_INDEX = (String)(map.get("SEQ_INDEX"));
        		END_TIME = (String)(map.get("END_TIME"));
        		%>
        			<tr bgcolor="#DFE1EC">
        				<td class="en11px"><%=formName %></td>
        				<td class="en11px"><%=EQUIPMENT_ID %></td>
        				<td class="en11px"><%=PERIOD_NAME %></td>
        				<td class="en11px"><a href="javascript:editJob(<%=SEQ_INDEX%>);"><%=JOB_NAME %></a></td>
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