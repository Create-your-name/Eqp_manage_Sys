<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%
String equipId = "";
if (pageContext.findAttribute("equipmentId")!=null){
	equipId = pageContext.findAttribute("equipmentId").toString();
}
 %>
<script language="javascript">

	function editDocument(abnormalDocIndex,equipment){
		loading();;
		document.abnormalDocEntry.action = '<ofbiz:url>/abnormalDocDefineSingle</ofbiz:url>?abnormalDocIndex='+abnormalDocIndex+'&equipmentId='+document.abnormalDocEntry.equipment_Id.value;
		document.abnormalDocEntry.submit();	
		
	}
	
	function reasonDocument(url){
		if (abnormalDocEntry.equipmentId.value == ""){
			Ext.MessageBox.alert("警告","设备不能为空！");
			return;
		}
		var strTemp = abnormalDocEntry.equipmentId.value;
		loading();
		document.abnormalDocEntry.action = url;
		document.abnormalDocEntry.submit();	
		document.abnormalDocEntry.equipment_Id.value = strTemp;
	}

	function doSubmit(url) {
		if (abnormalDocEntry.equipmentId.value == ""){
			Ext.MessageBox.alert("警告","设备不能为空！");
			return;
		}
		
		Ext.MessageBox.confirm('删除新增', '您确信要新增此记录吗？',function result(value){
			if(value=="yes"){
				//request.setAttribute("EquipmnetID",abnormalDocEntry.equipmentId.value);
				loading();
				document.abnormalDocEntry.action = url;
				document.abnormalDocEntry.submit();	
			}
		});
	}
	
	function queryDcop() {
		var url = '<ofbiz:url>/equiList</ofbiz:url>?actionName=' + Ext.getDom('equipmentId').value;
		window.open(url,"equiList",
			"top=130,left=240,width=685,height=180,title=,channelmode=0," +
			"directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
			"scrollbars=1,status=1,titlebar=0,toolbar=no");
	}
</script>
<form onsubmit="javascript:reasonDocument('<%=request.getContextPath()%>/control/abnormalDocumentEntryQuery');" action="<%=request.getContextPath()%>/control/abnormalDocumentEntryQuery"  name="abnormalDocEntry" method="POST" id ="abnormalDocEntry" >
<!--copy area-->
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="0"><input type="hidden"  name="equipment_Id"  id="equipment_Id" class="input" value = <%=request.getAttribute("equipmentId") %>></td>
    <td width="98%"><fieldset><legend>设备</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC"> 
        <!-----部门的设备ID---->
          <td width="12%" class="en11pxb">&nbsp;设备</td>
          <td width="12%"><input id="equipmentId" type="text" class="input" name = "equipmentId" value = <%=equipId %>></td>
          <td width="76%"><a href="#"><img src="<%=request.getContextPath()%>/images/icon_search.gif" width="15" height="16" border="0" onClick="queryDcop()"></a></td>
        </tr>
      </table>
	 </fieldset> 
	</td>
  <tr> 
</table>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
   	<td><ul class="button">
			<li><a class="button-text" href="#"  onclick="javascript:reasonDocument('<%=request.getContextPath()%>/control/abnormalDocumentEntryQuery');"><span>&nbsp;查询&nbsp;</span></a></li> 
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.abnormalDocEntry.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/abnormalDocDefine')"><span>&nbsp;新增&nbsp;</span></a></li> 
	</ul></td>
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
      		<td class="en11pxb">文件编号</td>
      		
      		<td class="en11pxb">设备</td>
            <td class="en11pxb">撰写人</td>
            <td class="en11pxb">撰写时间</td>
            <td class="en11pxb">状态</td>
    	</tr>
      <ofbiz:if name="DocumentList">
	        <ofbiz:iterator name="cust" property="DocumentList">
	        	<% GenericValue gv = (GenericValue)pageContext.findAttribute("cust"); 
	        		String status = gv.getString("status");
	        		String name = "";
	        		if (status == "0"){
						name = "开始";
					}else if (status == "1"){
						name = "结束";
					}else{
						name = "暂存";
					}
	        		//if(status.equals("2")) name = "开始";
	        		%>
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb"><a href="#" onclick="editDocument('<ofbiz:inputvalue entityAttr="cust" field="abnormalDocIndex"/>')"><ofbiz:inputvalue entityAttr="cust" field="abnormalDocName"/></a></td>
		          
		          <td class="en11pxb"><ofbiz:inputvalue entityAttr="cust" field="equipmentId"/></td>
		          <td class="en11pxb"><ofbiz:inputvalue entityAttr="cust" field="owner"/></td>
   		          <td class="en11pxb"><ofbiz:inputvalue entityAttr="cust" field="createTime"/></td>
   		          <td class="en11pxb"><%=name%></td>
		        </tr>
	      </ofbiz:iterator>
      </ofbiz:if>
      </table>
      </fieldset></td>
  </tr>
</table>
</div>