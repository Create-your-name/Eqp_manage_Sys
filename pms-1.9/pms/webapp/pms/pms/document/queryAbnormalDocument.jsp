<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%
String equipId = "";
if (pageContext.findAttribute("equipmentId")!=null){
	equipId = pageContext.findAttribute("equipmentId").toString();
}
String create_Time1 = "";
if (pageContext.findAttribute("create_Time1")!=null){
	create_Time1 = pageContext.findAttribute("create_Time1").toString();
}
String create_Time2 = "";
if (pageContext.findAttribute("create_Time2")!=null){
	create_Time2 = pageContext.findAttribute("create_Time2").toString();
}
%>
<script language="javascript">

	function editDocument(abnormalDocIndex,equipment){
		loading();;
		document.queryAbnormalDocument.action = '<ofbiz:url>/abnormalDocDefineSingle1</ofbiz:url>?abnormalDocIndex='+abnormalDocIndex+'&equipmentId='+document.queryAbnormalDocument.equipment_Id.value;
		document.queryAbnormalDocument.submit();	
	}
	
	function reasonDocument(url){
		/*if (queryAbnormalDocument.create_Time1.value == ""){
			Ext.MessageBox.alert("警告","Date From 不能为空！");
			return;
		}
		if (queryAbnormalDocument.create_Time2.value == ""){
			Ext.MessageBox.alert("警告","Date To 不能为空！");
			return;
		}*/
		var strTemp = queryAbnormalDocument.equipmentId.value;
		loading();
		document.queryAbnormalDocument.action = url;
		document.queryAbnormalDocument.submit();	
		document.queryAbnormalDocument.equipment_Id.value = strTemp;
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
	});
	
	function queryDcop() {
		var url = '<ofbiz:url>/equiList</ofbiz:url>?actionName=' + Ext.getDom('equipmentId').value;
		window.open(url,"equiList",
			"top=130,left=240,width=685,height=180,title=,channelmode=0," +
			"directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
			"scrollbars=1,status=1,titlebar=0,toolbar=no");
	}
</script>
<form action="<%=request.getContextPath()%>/control/queryAbnormalDocument"  name="queryAbnormalDocument" method="POST" id ="queryAbnormalDocument" >
<!--copy area-->
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="98%"><fieldset><legend>异常报告书查询条件</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
     	<tr bgcolor="#DFE1EC"> 
        <!-----部门的设备ID---->
          <td width="12%" class="en11pxb"><input type="hidden"  name="equipment_Id"  id="equipment_Id" class="input" value = <%=request.getAttribute("equipmentId") %>>发生时间</td>
          <td width="13%" class="en11px"><input type="text" ID="create_Time1" NAME="create_Time1" readonly value = <%=create_Time1 %>></td>
          <td width="14%" class="en11pxb">结束时间</td>
          <td width="17%" class="en11px"><input type="text" ID="create_Time2" NAME="create_Time2" readonly value = <%=create_Time2 %>></td>
        </tr>
        <tr bgcolor="#DFE1EC"> 
        <!-----部门的设备ID---->
          <td width="12%" class="en11pxb">设备</td>
          <td width="13%" ><input id="equipmentId" type="text" class="input" name = "equipmentId" value = <%=equipId %>><a href="#"><img src="../images/icon_search.gif" width="15" height="16" border="0" onClick="queryDcop()"></a></td>
          <td width="17%" class="en11pxb"></td>
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
			<li><a class="button-text" href="#" onclick="javascript:reasonDocument('<%=request.getContextPath()%>/control/abnormalDocumentEntryQuery1');"><span>&nbsp;显示&nbsp;</span></a></li> 
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.queryAbnormalDocument.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
    <td></td>
  </tr>
</table>
</form>

<br>
<ofbiz:if name="flag" value="OK">
<div id="queryAnnormalDocument" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="queryAnnormalDocument" style="visibility:'hidden';">
</ofbiz:unless>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
   <td><fieldset>
      <legend>异常报告书</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td class="en11pxb">报告书编号</td>
      		<td class="en11pxb">设备</td>
            <td class="en11pxb">撰写人</td>
            <td class="en11pxb">撰写时间</td>
            
    	</tr>
      <ofbiz:if name="DocumentList">
	        <ofbiz:iterator name="cust" property="DocumentList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb"><a href="#" onclick="editDocument('<ofbiz:inputvalue entityAttr="cust" field="abnormalDocIndex"/>')"><ofbiz:inputvalue entityAttr="cust" field="abnormalDocName"/></a></td>
		          <td class="en11pxb"><ofbiz:inputvalue entityAttr="cust" field="equipmentId"/></td>
		          <td class="en11pxb"><ofbiz:inputvalue entityAttr="cust" field="owner"/></td>
   		          <td class="en11pxb"><ofbiz:inputvalue entityAttr="cust" field="createTime"/></td>
   		          
		        </tr>
	      </ofbiz:iterator>
      </ofbiz:if>
      </table>
      </fieldset></td>
  </tr>
</table>
</div>