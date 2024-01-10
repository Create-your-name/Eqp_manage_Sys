<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%
	int i=1;
	int k=0;
	Map paramMap = (Map)request.getAttribute("paramMap");
	List periodList = (List)request.getAttribute("periodList");
%>

<!-- yui page script-->
<script language="javascript">
	//����
	function save(){
		var flag = 0;
		if(Ext.get('jobNum').dom.value=="0"){
			alert("��Ϊ�˱������������");
			return
		} else {
			for(i=1;i<=Ext.get('jobNum').dom.value;i++) {
				if (Ext.get('jobSelect_'+i).dom.checked) {
					flag = flag + 1;
				}
			}

			if(flag == 0) {
				alert("�빴ѡ�������");
				return;
			} else if(flag > 3) {
				alert("����������ѡ3��");
				return;
			}
		}
		loading();
		pmForm.submit();
	}

	function returnPmForm(){
		var actionURL='<ofbiz:url>/createPmRecord</ofbiz:url>'
		document.location.href=actionURL;
	}

	function selectPeriod(){
		pmForm.action ='<%=request.getContextPath()%>/control/addPmFormEntry';
		pmForm.submit();
	}

</script>
<form action="<%=request.getContextPath()%>/control/submitPmForm" method="post" id="pmForm" onsubmit="return false;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�������������</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
          <td width="30%" bgcolor="#ACD5C9" class="en11pxb">��������</td>
          <td width="70%" class="en11pxb">
          	<select id="periodIndex" name="periodIndex" onchange="selectPeriod();">
		          <option value=""></option>
		          <ofbiz:if name="periodList">
			        <ofbiz:iterator name="defaultPeriod" property="periodList">
				    <option value='<ofbiz:inputvalue entityAttr="defaultPeriod" field="periodIndex"/>'><ofbiz:inputvalue entityAttr="defaultPeriod" field="periodName"/></option>
			      </ofbiz:iterator>
		      	</ofbiz:if>
    		</select>
          </td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td width="30%" bgcolor="#ACD5C9" class="en11pxb">ѡ�������</td>
          <td width="70%">
          	 <table width="100%" border="0" cellspacing="1" cellpadding="2">
          	 <ofbiz:if name="dealProgrammeList">
          	 	<ofbiz:iterator name="cust" property="dealProgrammeList">
          	 	<%
          	 		k=i++;
          	 	%>
          	 	<tr bgcolor="#DFE1EC">
					<td width="30%"  class="en11pxb">
					    <input type="checkbox" name="jobSelect_<%=k%>" id="jobSelect_<%=k%>" value="1">
					</td>

					<td width="70%"  class="en11pxb">
					    <input type="hidden" name="jobIndex_<%=k%>" value='<ofbiz:inputvalue entityAttr="cust" field="jobIndex"/>'>
					    <ofbiz:inputvalue entityAttr="cust" field="jobName"/>
					</td>
          	 	</tr>
          	 	</ofbiz:iterator>
          	 </ofbiz:if>
          	 </table>
    	  </td>
        </tr>
      </table>
      </fieldset></td>
      <input type="hidden" name="eqpId" id="eqpId" value='<%=UtilFormatOut.checkNull((String)paramMap.get("eqpId"))%>'>
      <input type="hidden" name="scheduleIndex" id="scheduleIndex" value='<%=UtilFormatOut.checkNull((String)paramMap.get("scheduleIndex"))%>'>
      <input type="hidden" name="scheduleDate" id="scheduleDate" value='<%=UtilFormatOut.checkNull((String)paramMap.get("scheduleDate"))%>'>
      <input type="hidden" name="jobNum" id="jobNum" value='<%=k%>'>
      <input type="hidden" name="formType" value='<%=UtilFormatOut.checkNull((String)paramMap.get("formType"))%>'>
  </tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
	<tr height="30">
	    <td><ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:save();" id="save"><span>&nbsp;ȷ��&nbsp;</span></a></li>
		</ul>
		<ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:returnPmForm();"><span>&nbsp;����&nbsp;</span></a></li>
		</ul>
		</td>
	</tr>
</table>
</form>
<script language="javascript">
	var period = Ext.get('periodIndex').dom;
	period.value='<%=UtilFormatOut.checkNull((String)paramMap.get("periodIndex"))%>';
	<ofbiz:if name="dealProgrammeList">
	</ofbiz:if>
	<ofbiz:unless name="dealProgrammeList">
		var obj = Ext.get('save').dom;
		obj.disabled = true;
	</ofbiz:unless>
</script>
