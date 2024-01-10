<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<% 
	String partNo=UtilFormatOut.checkNull(request.getParameter("partNo"));
	String partName=UtilFormatOut.checkNull(request.getParameter("partName"));
%>
<base target="_self">
<!-- yui page script-->
<script language="javascript">
	//查询
	function recylePartsQuery(){
		partsDataQueryForm.submit();
	}
	
	function selectPartsNo(partsNo,partName){
		var result=new Array();
		result["partNo"]=partsNo;
		result["partName"]=partName;
		window.returnValue=result;
		window.close();
	}

</script>
<form action="<%=request.getContextPath()%>/control/queryPartsNoList" method="post" id="partsDataQueryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>物料信息查询参数</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2"> 
	     <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb">物料号:</td>
	       <td width="28%"><input class="input" type="text" name="partNo" id="partNo" value="<%=partNo%>" size="22" tabindex="1" />
	    	</td>
	    <td width="12%" class="en11pxb">物料名:</td>
	    <td width="28%"><input class="input" type="text" name="partName" id="partName" value="<%=partName%>" size="22" tabindex="2" />
	    </td>
	    <td width="20%" class="en11pxb" align="left">
	    	<table border="0" cellspacing="0" cellpadding="0">
			  <tr height="30">
			   	<td width="20">&nbsp;</td>
			    <td><ul class="button">
				<li><a class="button-text" href="#" onclick="javascript:recylePartsQuery();"><span>&nbsp;确定&nbsp;</span></a></li> 
				</ul></td>
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
      <legend>物料信息列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="30%" class="en11pxb">物料号</td>
          <td width="55%" class="en11pxb">物料名</td>
        </tr>
        <ofbiz:if name="partsNoList">
	        <ofbiz:iterator name="cust" property="partsNoList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11px"><a href="#" onclick="selectPartsNo('<ofbiz:inputvalue entityAttr="cust" field="partNo"/>','<ofbiz:entityfield attribute="cust" field="partName"/>')"><ofbiz:entityfield attribute="cust" field="partNo"/></a></td>
		          <td class="en11px"><ofbiz:entityfield attribute="cust" field="partName"/></td>
		        </tr>
	      </ofbiz:iterator>
      </ofbiz:if>
      </table>
      </fieldset></td>
  </tr>
</table>