<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

<%
String startDate = UtilFormatOut.checkNull(request.getParameter("startDate"));
String endDate = UtilFormatOut.checkNull(request.getParameter("endDate"));
String deptIndex = UtilFormatOut.checkNull(request.getParameter("deptIndex"));

List HaojianLingList = (List) request.getAttribute("HaojianLingList");

if (deptIndex == "") {
	deptIndex = (String) request.getAttribute("deptIndex");
}

SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
Date d = new Date();
if (endDate == "") {
    endDate = df.format(d);
}

if (startDate == "") {
    d.setDate(d.getDate() - 30);
    startDate = df.format(d);
}
%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function doSubmit(url) {
	document.MaterialForm.action = url;
	document.MaterialForm.submit();
}

function turnto(url) {
    //window.navigate(url);
	document.MaterialForm.action = url;
	document.MaterialForm.submit();

}
function cancel(url) {
    window.navigate(url);
}
<!-- ##################################### yui page script ################################ -->
Ext.onReady(function(){
       //格式化日期
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


	//给select控件(aa)应用样式
	equipmentDept = new Ext.form.ComboBox({
       
		displayField:'deptIndex',
		valueField:'deptIndex',
		hiddenName:'deptIndex',
		typeAhead: true,
		mode: 'local',
		width: 150,
		emptyText: 'Select a deptIndex...',
		triggerAction: 'all',
		forceSelection:true
    });
	//equipmentDept.on('select',loadEquipment);
    equipmentDept.setValue("<%=deptIndex%>");

	equipmentDept = new Ext.form.ComboBox({
        
        transform:'deptIndex',
        
        forceSelection:true
    });
	//equipmentDept.on('select',loadEquipment);
    equipmentDept.setValue("<%=deptIndex%>");
});


</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form name="MaterialForm" method="post" >
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>查询条件</legend>
    	<table width="100%" border="0" cellspacing="1" cellpadding="2">
		    <tr bgcolor="#DFE1EC" height="30">
	     		<tr bgcolor="#DFE1EC">
	      		 <td width="12%" bgcolor="#ACD5C9" class="en11pxb">领用日期:</td>
	      		 <td width="28%"><input type="text" ID="startDate" NAME="startDate" value="<%=startDate%>" readonly></td>
	   			 <td width="9%" bgcolor="#ACD5C9" class="en11pxb">到:</td>
	    		 <td width="28%"><input type="text" ID="endDate" NAME="endDate" value="<%=endDate%>"  readonly></td>
	    	</tr>
			<tr bgcolor="#DFE1EC" height="12">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%">部门</td>
				<td nowrap="nowrap" >
					<select id="deptIndex" name="deptIndex">
						<option value=''></option>
						<ofbiz:if name="deptList">
							<ofbiz:iterator name="cust" property="deptList">
								<option value='<ofbiz:inputvalue entityAttr="cust" field="deptIndex"/>'>
									<ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>
								</option>

							</ofbiz:iterator>
						</ofbiz:if>
					</select>
				<td>
			</tr>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryHaojianLingMaterialPriceEntry</ofbiz:url>')"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.MaterialForm.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" onclick="javascript: history.back();" href="#"><span>&nbsp;返回&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<%--注意维护的时候是更具materialIndex(物料索引)进行关联字段的--%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>领料单详细信息</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9" height="13">
    	    <td class="en11pxb">部门</td>
			<td class="en11pxb">备件金额</td>
			<td class="en11pxb">耗件金额</td>
			<td class="en11pxb">总金额</td>
    	</tr>

		<%
		if(HaojianLingList != null && HaojianLingList.size() > 0) {
			for(Iterator it1 = HaojianLingList.iterator();it1.hasNext();) {
			    Map map1 = (Map) it1.next();
		%>
		       <tr class="tablelist">
    		        <td class="en11px"><%=map1.get("EQUIPMENT_DEPT")%></td>

    		        <td class="en11px">
        				<a href="#" onclick="turnto('<ofbiz:url>/queryStoReqEntry?deptIndex=<%=map1.get("DEPT_INDEX")%>&mtrGrp=20002P</ofbiz:url>')" >
        				    <%=map1.get("BEIJIAN")%>
        				</a>
        			</td>

        			<td class="en11px">
        				<a href="#" onclick="turnto('<ofbiz:url>/queryStoReqEntry?deptIndex=<%=map1.get("DEPT_INDEX")%>&mtrGrp=20002S</ofbiz:url>')" >
        				    <%=map1.get("HAOJIAN")%>
        				</a>
        			</td>

        			<td class="en11px">
        				<%=map1.get("SUM_PRICE")%>
        			</td>
    		   </tr>
		<%
		    }
		}
		%>

    </table>
	 </fieldset>
	</td>
  <tr>
</table>
</form>