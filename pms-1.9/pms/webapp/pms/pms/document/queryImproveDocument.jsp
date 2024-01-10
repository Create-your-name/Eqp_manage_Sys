<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
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
		document.improveDocument.action = '<ofbiz:url>/abnormalDocDefineSingle21</ofbiz:url>?improveDocIndex='+abnormalDocIndex+'&type='+document.queryImproveDocument.type.value+'&flag='+flag;
		document.improveDocument.submit();	
	}
	
	function reasonDocument(url){
		/*if (improveDocument.create_Time1.value == ""){
			Ext.MessageBox.alert("queryImproveDocument","开始日期 不能为空！");
			return;
		}
		if (improveDocument.create_Time2.value == ""){
			Ext.MessageBox.alert("queryImproveDocument","结束日期 不能为空！");
			return;
		}*/
		var strTemp = improveDocument.type.value;
		loading();
		document.improveDocument.action = url;
		document.improveDocument.submit();	
		//document.queryImproveDocument.type.value = strTemp;
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
	    type.setValue("<%=type==null?"EQP":type%>");
	});
</script>
<form action="<%=request.getContextPath()%>/control/queryImproveDocument"  name="improveDocument" method="POST" id ="queryImproveDocument" >
<!--copy area-->
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="98%"><fieldset><legend>改善类报告书查询</legend>
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
          <td width="12%" bgcolor="#ACD5C9" class="en11pxb">改善报告书类型</td>
          <td width="13%" class="en11pxb"><select id="type" name="type">
	    	  	<option value="EQP">设备改善</option>
	     	 	<option value="PM">保养改善</option>
	     	 	<option value="SE">安全改善</option>
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
			<li><a class="button-text" href="#" onclick="javascript:reasonDocument('<%=request.getContextPath()%>/control/queryImproveDocumentEntry1');"><span>&nbsp;显示&nbsp;</span></a></li> 
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.queryImproveDocument.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
    <td></td>
  </tr>
</table>
</form>

<br>
<ofbiz:if name="flag" value="OK">
<div id="queryImproveDocument" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="queryImproveDocument" style="visibility:'hidden';">
</ofbiz:unless>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
   <td><fieldset>
      <legend>改善类报告</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td class="en11pxb">报告编号</td>
      		<td class="en11pxb">改善类型</td>
            <td class="en11pxb">撰写人</td>
            <td class="en11pxb">撰写时间</td>
    	</tr>
      <ofbiz:if name="DocumentList">
	        <ofbiz:iterator name="cust" property="DocumentList">
	        	<% GenericValue gv = (GenericValue)pageContext.findAttribute("cust"); 
	        		String status = gv.getString("type");
	        		String name = "";
	        		if (status.equals("EQP")){
						name = "设备改善";
					}else if (status.equals("PM")){
						name = "保养改善";
					}else if (status.equals("SE")){
						name = "安全改善";
					}
	        		%>
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb"><a href="#" onclick="editDocument('<ofbiz:inputvalue entityAttr="cust" field="impDocIndex"/>')"><ofbiz:inputvalue entityAttr="cust" field="docName"/></a></td>
		          <td class="en11pxb"><%=name%></td>
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