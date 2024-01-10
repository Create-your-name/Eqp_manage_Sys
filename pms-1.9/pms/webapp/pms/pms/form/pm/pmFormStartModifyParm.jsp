<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<script language="javascript">
	//确定
	function save(){
		var actionURL='<ofbiz:url>/upateEquipmentParamManualValue</ofbiz:url>';
		eqpParamForm.action=actionURL;
		eqpParamForm.submit();
	}
	
	//取消
	function cancle(){
		var actionURL='<ofbiz:url>/pmRecordInfo</ofbiz:url>';
		eqpParamForm.action=actionURL;
		eqpParamForm.submit();
	}
</script>
<form action="" method="post" id="eqpParamForm">
 <input id="pmIndex" type="hidden" name="pmIndex" value='<%=request.getParameter("pmIndex")%>' />
 <input id="eqpId" type="hidden" name="eqpId" value='<%=request.getParameter("eqpId")%>' />
 <input id="functionType" type="hidden" name="functionType" value='<%=request.getParameter("functionType")%>' />
 
<fieldset><legend>设备相关手动输入值</legend>
 <table width="100%" border="0" cellspacing="1" cellpadding="2">
  	<ofbiz:if name="UNSCHEDULE_EQP_PARAM">
	        <ofbiz:iterator name="cust" property="UNSCHEDULE_EQP_PARAM">
	        <tr bgcolor="#DFE1EC">
		    <td class="en11px" width="8%"><input type="checkBox" id="seqIndex" name="seqIndex" <ofbiz:inputvalue entityAttr="cust" field="seqIndex" fullattrs="true"/>></td>
		    <td class="en11px" width="92%"><ofbiz:entityfield attribute="cust" field="paramName"/>,手动输入值:<ofbiz:entityfield attribute="cust" field="manualValue"/></td>
	      	</tr>
	 </ofbiz:iterator>
  </ofbiz:if>
</table>
</fieldset>
</form>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="#" onclick="save();"><span>&nbsp;确定&nbsp;</span></a></li> 
	</ul>
	<ul class="button">
			<li><a class="button-text" href="#" onclick="cancle();"><span>&nbsp;取消&nbsp;</span></a></li> 
	</ul>
	</td>
  </tr>
</table>
