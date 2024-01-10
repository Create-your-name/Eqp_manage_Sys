<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="java.util.*" %>
<% 
	String startDate=UtilFormatOut.checkNull((String)request.getAttribute("startDate"));
	String endDate=UtilFormatOut.checkNull((String)request.getAttribute("endDate"));
	List abnormalList = (List)request.getAttribute("ABNORMALFORM_LIST");
%>

<!-- yui page script-->
<script language="javascript">
	//查询
	function query(){
		var startDate=Ext.get('startDate').dom.value;
		var endDate=Ext.get('endDate').dom.valu;
		if((startDate==""||startDate==undefined) &&(endDate==""||endDate==undefined)){
			Ext.MessageBox.alert('警告', '请输入查询条件!');
			return;
		}
		loading();
		tsForm.submit();
	}
	
	//重置
	function reset(){
		 Ext.get('eqpId').dom.value='';
		 Ext.get('startDate').dom.value='';
		 Ext.get('endDate').dom.value='';
	}
	
	//进入表单新建页面
	function intoAbnormalForm(index,eqpId){
		loading();
		var actionURL='<ofbiz:url>/intoAbnormalFormManageEntry</ofbiz:url>?functionType=1&abnormalIndex='+index+'&eqpId='+eqpId;
		document.location.href=actionURL;	
	}
	
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
	    
	    initDate();
	 });
	 
	 function initDate(){
		 Ext.get('startDate').dom.value='<%=startDate%>';
		 Ext.get('endDate').dom.value='<%=endDate%>';
	 }
</script>
<form action="<%=request.getContextPath()%>/control/queryAbnormalFormEntry" method="post" id="tsForm" onsubmit="return false;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>异常记录查询参数</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC"> 
	        <td bgcolor="#ACD5C9" class="en11pxb">表单创建日期</td>
	        <td width="30%"><input type="text" ID="startDate" NAME="startDate" readonly size="26"></td>
	    	<td  width="5%" class="en11pxb">到:</td>
	    	<td width="45%"><input type="text" ID="endDate" NAME="endDate"  readonly size="26"></td>
        </tr>
        <tr bgcolor="#DFE1EC"> 
        	<td class="en11pxb" colspan="4" align="left">
	        	<table border="0" cellspacing="0" cellpadding="0">
			  		<tr height="30">
					    <td><ul class="button">
							<li><a class="button-text" href="#" onclick="javascript:query();"><span>&nbsp;查询&nbsp;</span></a></li> 
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
      <legend>异常记录表单列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="10%" class="en11pxb">机台编号</td>
          <td width="20%" class="en11pxb">表单编号</td>
          <td width="10%" class="en11pxb">表单类型</td>
          <td width="10%" class="en11pxb">撰写人</td>
          <td width="20%" class="en11pxb">异常发生时间</td>
          <td width="10%" class="en11pxb">表单状态</td>
          <td width="20%" class="en11pxb">表单最后修改时间</td>
        </tr>
         <% if(abnormalList != null && abnormalList.size() > 0) {  
       		for(Iterator it = abnormalList.iterator();it.hasNext();) { 
					Map map = (Map)it.next();
					%>
	        <tr bgcolor="#DFE1EC">
		    <td class="en11px"><a href="#" onclick="intoAbnormalForm('<%=map.get("ABNORMAL_INDEX")%>','<%=map.get("EQUIPMENT_ID")%>')"><%=map.get("EQUIPMENT_ID")%></a></td>
		    <td class="en11px"><%=map.get("ABNORMAL_NAME")%></td>
		    <td class="en11px"><%if(Constants.FORM_TYPE_PATCH.equalsIgnoreCase((String)map.get("FORM_TYPE"))){%>补填<%}else{%>正常<%}%></td>
		    <td class="en11px"><%=map.get("CREATE_NAME")%></td>
		    <td class="en11px"><%=map.get("ABNORMAL_TIME")%></td>
		    <td class="en11px"><%=map.get("STATUS")%></td>
		    <td class="en11px"><%=map.get("UPDATE_TIME")%></td>
		    </tr>
	     <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>
