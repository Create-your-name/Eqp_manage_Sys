<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*" %>
<%
	int i=1;
	String parentEqpId=request.getParameter("parentEqpId");
	String seqIndex=request.getParameter("seqIndex");
	List tsList = (List)request.getAttribute("TS_LIST");

	String queryEqpId=request.getParameter("queryEqpId");
    String startDate=request.getParameter("beginDate");
    String endDate=request.getParameter("eDate");
    String abnormalTime=request.getParameter("abnormalTime");
%>

<!-- yui page script-->
<script language="javascript">
	//����
	function save(){
		loading();
		tsForm.submit();
	}

	//����
	function returnTsForm(){
		retrunTsForm.submit();
	}

	//ѡ�����豸ʱ,���°𶨿ؼ���״̬;
	function changeBindCheck(obj){
		var id=obj.id;
		var valueArray=id.split("_");
		document.getElementById("childEqpIdBind_"+valueArray[1]).checked=false;
		if(obj.checked==true){
			document.getElementById("childEqpIdBind_"+valueArray[1]).disabled=false;
		}else{
			document.getElementById("childEqpIdBind_"+valueArray[1]).disabled=true;
		}

	}

	//��ʼ��ҳ��ؼ�
	Ext.onReady(function(){
	    var job = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'jobIndex',
	        width:170,
	        forceSelection:true
	    });
	 });
</script>
<form action="<%=request.getContextPath()%>/control/addTsFormEntry" method="post" id="tsForm" onsubmit="return false;">
<input id="seqIndex" type="hidden" name="seqIndex" value='<%=seqIndex%>' />
<input id="eqpId" type="hidden" name="eqpId" value='<%=parentEqpId%>' />
<input id="abnormalTime" type="hidden" name="abnormalTime" value='<%=abnormalTime%>' />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�������</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
          <td width="30%" bgcolor="#ACD5C9" class="en11pxb">ѡ�������</td>
          <td width="70%"><select id="jobIndex" name="jobIndex">
		          <option value=""></option>
		          <ofbiz:if name="DEALPRO_LIST">
			        <ofbiz:iterator name="cust" property="DEALPRO_LIST">
				    <option value='<ofbiz:inputvalue entityAttr="cust" field="jobIndex"/>'><ofbiz:inputvalue entityAttr="cust" field="jobName"/></option>
			      </ofbiz:iterator>
		      	</ofbiz:if>
    		</select>
    		</td>
        </tr>
      </table>
      </fieldset></td>
  </tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
	<tr height="30">
	    <td><ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:save();"><span>&nbsp;ȷ��&nbsp;</span></a></li>
		</ul>
		<ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:returnTsForm();"><span>&nbsp;����&nbsp;</span></a></li>
		</ul>
		</td>
	</tr>
</table>
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>���豸�б�</legend>
	 <table width="100%" border="0" cellspacing="1" cellpadding="2">
	      <tr bgcolor="#ACD5C9">
	        <td width="25%" class="en11pxb">ѡ��<font color="red">(һ����)</font></td>
	        <td width="25%" class="en11pxb">���豸</td>
	        <td width="25%" class="en11pxb">�豸״̬</td>
	        <td width="25%" class="en11pxb">�Ƿ��<font color="red">(һ�����)</font></td>
	      </tr>

	       <%if(tsList != null && tsList.size() > 0) {
       		for(Iterator it = tsList.iterator();it.hasNext();) {
					Map map = (Map)it.next();%>
	        <tr bgcolor="#DFE1EC">
	        <%
	        	int k=i++;
	        %>
	        <input id="childSeqIndex_<%=k%>" type="hidden" name="childSeqIndex_<%=k%>" value='<%=map.get("SEQINDEX")%>' />
		    <td class="en11px"><input type="checkBox" id="childEqpId_<%=k%>" name="childEqpId_<%=k%>" value='<%=map.get("EQUIPMENTID")%>' onclick="changeBindCheck(this);"></td>
		    <td class="en11px"><%=map.get("EQUIPMENTID")%></td>
		    <td class="en11px"><%=map.get("EQUIPMENT_STATUS")%></td>
		    <td class="en11px"><input type="checkBox" id="childEqpIdBind_<%=k%>" name="childEqpIdBind_<%=k%>" value='1' disabled></td>
	      	</tr>
	        <%
	  		}
	 	 } %>
	     </table>
      </fieldset></td>
  </tr>
</table>
</form>

<form action="<%=request.getContextPath()%>/control/queryTsRecordEntry" method="post" id="retrunTsForm" onsubmit="return false;">
<input id="startDate" type="hidden" name="startDate" value='<%=startDate%>' />
<input id="endDate" type="hidden" name="endDate" value='<%=endDate%>' />
<input id="eqpId" type="hidden" name="eqpId" value='<%=queryEqpId%>'/>
</form>

