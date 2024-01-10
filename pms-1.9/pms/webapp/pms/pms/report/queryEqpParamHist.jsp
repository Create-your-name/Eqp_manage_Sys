<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<%
	List eqpParamList = (List) request.getAttribute("eqpParamList");

	String startDate = request.getParameter("startDate");
	String endDate = request.getParameter("endDate");

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	if (null == startDate) {
		Calendar cDay = Calendar.getInstance();
		cDay.add(Calendar.DATE,-1);
		startDate = formatter.format(cDay.getTime());
	}

	if (null == endDate) {
		endDate = formatter.format(Calendar.getInstance().getTime());
	}

	String eqpType = UtilFormatOut.checkNull(request.getParameter("equipmentType"));
	String eqpId = UtilFormatOut.checkNull(request.getParameter("eqpId"));
	String paramName = UtilFormatOut.checkNull(request.getParameter("paramName"));
%>

<!-- yui page script-->
<script language="javascript">

var eqpParam = function() {

	var eqpTypeDS,eqpTypeCom;
	var eqpIdDS,eqpIdCom;

	return {
		init : function() {
			this.createDataStore();
		    this.createCombox();
		    this.initLoad();
		},

		//设置数据源
		createDataStore : function() {

			eqpTypeDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentTypeByCommon</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentType'}]))
		    });

		    eqpIdDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEqpIdByEqpTypeByCommon</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
		    });

		    paramNameDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryParamNameByEqpId</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'paramName'}]))
		    });

		},

		createCombox : function() {

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

			//设置设备ID
		    eqpTypeCom = new Ext.form.ComboBox({
			    store: eqpTypeDS,
			    displayField:'equipmentType',
			    valueField:'equipmentType',
			    hiddenName:'equipmentType',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all'
		    });
		    eqpTypeCom.applyTo('eqpTypeSelect');
		    eqpTypeCom.on('select', this.loadEqpId);

			//设置eqpId
			eqpIdCom = new Ext.form.ComboBox({
			    store: eqpIdDS,
			    displayField:'equipmentId',
			    valueField:'equipmentId',
			    hiddenName:'eqpId',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all'
		    });
		    eqpIdCom.applyTo('eqpIdSelect');
		    eqpIdCom.on('select', this.loadParamName);

		    //设置paramName
			paramNameCom = new Ext.form.ComboBox({
			    store: paramNameDS,
			    displayField:'paramName',
			    valueField:'paramName',
			    hiddenName:'paramName',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all'
		    });
		    paramNameCom.applyTo('paramNameSelect');

		},

		initLoad : function() {
			var eqpType = '<%=eqpType%>';
	    	eqpTypeDS.load({callback:function(){ eqpTypeCom.setValue(eqpType); if(eqpType != ''){eqpParam.loadEqpId();} }});
		},

		loadEqpId : function() {
			var val = eqpTypeCom.getValue();
			if (val == '<%=eqpType%>') {
			    var eqpId  = '<%=UtilFormatOut.checkNull(request.getParameter("eqpId"))%>';
			} else {
			    var eqpId  = '';
			}

			eqpIdDS.load({params:{equipmentType:val},callback:function(){ eqpIdCom.setValue(eqpId); eqpParam.loadParamName(); }});
		},

		loadParamName : function() {
			var val = eqpIdCom.getValue(), paramName  = '<%=UtilFormatOut.checkNull(request.getParameter("paramName"))%>';
	 		paramNameDS.load({params:{eqpId:val},callback:function(){ paramNameCom.setValue(paramName); }});
		}

	}
}();

Ext.EventManager.onDocumentReady(eqpParam.init,eqpParam,true);

function doSubmit(url) {
    if(Ext.get('equipmentType').dom.value==""){
		Ext.MessageBox.alert('警告', '"设备大类不能为空"!');
		return;
	}

    document.paramForm.action = url;
	loading();
	document.paramForm.submit();
}

function showChart() {
    if(Ext.get('equipmentType').dom.value==""){
		Ext.MessageBox.alert('警告', '"设备大类不能为空"!');
		return;
	}

    var url = "<ofbiz:url>/showEqpParamChart</ofbiz:url>?startDate=" + Ext.get('startDate').dom.value
            + "&endDate=" + Ext.get('endDate').dom.value
			+ "&equipmentType=" + Ext.get('equipmentType').dom.value
			+ "&eqpId="+ Ext.get('eqpId').dom.value
			+ "&paramName=" + Ext.get('paramName').dom.value
    window.open(url,"chart",
            "top=130,left=240,width=700,height=500,title=,channelmode=0," +
            "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
            "scrollbars=1,status=1,titlebar=0,toolbar=no");
}

</script>

<form method="post" name="paramForm">

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <fieldset>
        <legend>Unschedule EQP Param 表</legend>
        <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
			<tr bgcolor="#DFE1EC">
		    	<td width="12%" class="en11pxb">日期:</td>
		   		<td width="28%"><input type="text" id="startDate" name="startDate" value="<%=startDate%>" readonly></td>
			    <td width="12%" class="en11pxb">到:</td>
			    <td width="28%"><input type="text" id="endDate" name="endDate" value="<%=endDate%>" readonly></td>
		    </tr>

			<tr bgcolor="#DFE1EC">
				<td class="en11pxb">设备大类</td>
				<td><input type="text" size="40" name="eqpTypeSelect" autocomplete="off"/></td>

				<td class="en11pxb">设备</td>
				<td><input type="text" size="40" name="eqpIdSelect" autocomplete="off"/></td>
			</tr>

			<tr bgcolor="#DFE1EC">
				<td class="en11pxb">机台参数</td>
				<td><input type="text" size="40" name="paramNameSelect" autocomplete="off"/></td>

				<td class="en11pxb"></td>
				<td></td>
			</tr>

		</table>
      </fieldset>
    </td>
  </tr>
</table>

</form>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryEqpParamHist</ofbiz:url>');"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>

	<td><ul class="button">
			<li><a class="button-text" href="javascript:document.paramForm.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>

	<td><ul class="button">
			<li><a class="button-text" href="javascript:showChart();"><span>&nbsp;查看机台参数图&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>机台参数资料</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td class="en11pxb">日期</td>
      		<td class="en11pxb">设备大类</td>
      		<td class="en11pxb">设备</td>
      		<td class="en11pxb">参数名称</td>
      		<td class="en11pxb">实际值</td>
      		<td class="en11pxb">警示最大值</td>
      		<td class="en11pxb">警示最小值</td>
      		<td class="en11pxb">最后修改者</td>
    	</tr>

    	<%
    	if(eqpParamList != null && eqpParamList.size() > 0) {
    		for(Iterator it = eqpParamList.iterator();it.hasNext();) {
				Map map = (Map)it.next();
    	%>
		        <tr bgcolor="#DFE1EC">
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("UPDATE_TIME"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("EQUIPMENT_TYPE"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("EQUIPMENT_ID"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("PARAM_NAME"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("VALUE"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("MAX_VALUE"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("MIN_VALUE"))%></td>
		        	<td class="en11px"><%=UtilFormatOut.checkNull((String) map.get("TRANS_BY"))%></td>
		        </tr>
		 <%
		 	}
		 }
		 %>

      </table>
      </fieldset></td>
  </tr>
</table>
