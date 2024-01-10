<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<%
    String maintDept = UtilFormatOut.checkNull((String) request.getAttribute("maintDept"));
%>

<!--include yui css-->
<script language="javascript">

	Ext.onReady(function(){

	    var dept = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'maintDept',
	        width:170,
	        forceSelection:true
	    });
	    dept.on('select',loadEqpId);

	    var eqpIdDS = new Ext.data.Store({
	        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentByAnd</ofbiz:url>'}),
		    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
	    });

	    //设置eqpId
		var	eqpIdCom = new Ext.form.ComboBox({
		    store: eqpIdDS,
		    displayField:'equipmentId',
		    valueField:'equipmentId',
		    hiddenName:'equipmentId',
		    typeAhead: true,
		    mode: 'local',
		    width: 170,
		    triggerAction: 'all'
	    });
	    eqpIdCom.applyTo('eqpIdSelect');

	    function loadEqpId() {
    		var val = dept.getValue();
    		if (val == '<%=maintDept%>') {
    		    var eqpId  = '<%=UtilFormatOut.checkNull(request.getParameter("equipmentId"))%>';
    		} else {
    		    var eqpId  = '';
    		}

    		eqpIdDS.load({params:{maintDept:val},callback:function(){ eqpIdCom.setValue(eqpId); }});
    	};

        // initial dept & epqid
    	dept.setValue("<%=maintDept%>");
    	loadEqpId();
	});

	function equipmentScheduleDefine() {
		if(Ext.getDom('equipmentId').value=='') {
			Ext.MessageBox.alert('警告', '请选择设备！');
			return;
		}
		loading();
		document.equipmentScheduleForm.submit();
	}

</script>

<form name="equipmentScheduleForm" method="POST" action="<%=request.getContextPath()%>/control/queryPmSchedule">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>机台PM日期查询</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC" height="30">
            <td width="10%" class="en11pxb">&nbsp;部&nbsp;门&nbsp;</td>
            <td>
         		<select id="maintDept" name="maintDept">
         			<option value=''></option>
	          		<ofbiz:if name="deptList">
		        		<ofbiz:iterator name="cust" property="deptList">
			    			<option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>'>
			    			    <ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>
			    			</option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
    			</select>
            </td>
        </tr>

        <tr bgcolor="#DFE1EC" height="30">
            <td width="10%" class="en11pxb">&nbsp;设&nbsp;备&nbsp;</td>
            <td><input type="text" size="40" name="eqpIdSelect" autocomplete="off"/></td>
        </tr>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
	<tr height="30">
	 	<td><ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:equipmentScheduleDefine();"><span>&nbsp;确定&nbsp;</span></a></li>
			</ul>
		</td>
	</tr>
</table>
</form>