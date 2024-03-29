<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Calendar" %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>

<%
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
	String dept = UtilFormatOut.checkNull(request.getParameter("equipmentDept"));

%>
<script lanaguage="javascript">

	function doSubmit(url) {


        	//if(document.eqpForm.equipmentType.value=="") {
		//	alert("请选择设备大类！");
		//	return;
		//}

		//if(document.eqpForm.periodIndex.value=="") {
		//	alert("请选择PM周期！");
		//	return;
		//}
        	if(document.eqpForm.equipmentType.value=="" && document.eqpForm.periodIndex.value=="" && document.getElementById("deptSelect").value=="") {
			alert("请选择设备大类,PM周期或部门");
			return;	
		}
		for(var i=0;i<document.eqpForm.eqpIdSelected.options.length;i++) {
			document.eqpForm.eqpIdSelected.options[i].selected = true;
		}

		loading();

		document.eqpForm.action = url;
		document.eqpForm.submit();
	}

	function doAddEqp() {
		if(document.eqpForm.eqpId.options.length==0)
		{
			alert("请选择设备大类！");
			return;
		}

		moveOption(document.eqpForm.eqpId,document.eqpForm.eqpIdSelected);
	}

	function doRemoveEqp() {
		if(document.eqpForm.eqpIdSelected.options.length==0)
		{
			alert("请选择设备！");
			return;
		}
	    moveOption(document.eqpForm.eqpIdSelected,document.eqpForm.eqpId);
	}

	function moveOption(objFromSelect,objToSelect) {
		/*var objOption = document.createElement("option");
		objOption.text = objFromSelect.options[objFromSelect.selectedIndex].text;
		objOption.value = objFromSelect.options[objFromSelect.selectedIndex].value;
		objToSelect.options.add(objOption);

		objFromSelect.options.remove(objFromSelect.selectedIndex);*/
		for(var i=0;i<objFromSelect.options.length;i++) {
			if (objFromSelect.options[i].selected) {
				var e = objFromSelect.options[i];
				objToSelect.options.add(new Option(e.text, e.value));
				objFromSelect.remove(i);
				i = i - 1;
			}
		}
	}

	function clearEqp(eqpSelectObj) {
	    for(var i=0;i<eqpSelectObj.options.length;i++) {
			eqpSelectObj.remove(i);
			i = i - 1;
		}
	}

	var oldDeptValue="";
	var oldEqpValue="";

	//设备大类选择
	function eqpTypeChange(){
		var newDeptValue = Ext.get('equipmentDept').dom.value;
		var newEqpValue = Ext.get('equipmentType').dom.value;

		var actionURL = '<ofbiz:url>/queryEqpIdByEqpTypeByCommon</ofbiz:url>?equipmentType='+newEqpValue+'&maintDept='+newDeptValue;

		actionURL = encodeURI(actionURL);

		if(oldEqpValue != newEqpValue || oldDeptValue != newDeptValue){
			Ext.lib.Ajax.formRequest('eqpForm',actionURL,{success: commentSuccess, failure: commentFailure});
		}

		oldDeptValue = newDeptValue;
		oldEqpValue = newEqpValue;
	}

	//远程调用成功
	function commentSuccess(o){
        //clear all eqpid options
        clearEqp(document.eqpForm.eqpIdSelected);
        clearEqp(document.eqpForm.eqpId);

		var eqpArray = eval('(' + o.responseText + ')');
		var eqpArraySize = eqpArray.length;

		var equipId = document.getElementById("eqpId");
		equipId.length = 0;
		for(var i=0; i < eqpArraySize; i++){
			equipId.options[equipId.length] = new Option(eqpArray[i].equipmentId,eqpArray[i].equipmentId);
		}

	}

	//远程调用失败
	var commentFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
	};

</script>

<!-- yui page script-->
<script language="javascript">

var eqpForm = function() {

	var deptDS, deptCom;
	var eqpTypeDS, eqpTypeCom;
	var periodDS, periodCom;
	var periodRecordDef;

	return {
		init : function() {
			this.createDataStore();
		    this.createCombox();
		    this.initLoad();
		},

		//设置数据源
		createDataStore : function() {
			periodRecordDef = Ext.data.Record.create([
			    {name: 'periodIndex'},
				{name: 'periodName'}
			]);

			deptDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentDept</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentDept'}]))
		    });

			eqpTypeDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentTypeByCommon</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentType'}]))
		    });

		    periodDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryPMPeriodByEqpType</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, periodRecordDef)
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

		    //设置dept
			deptCom = new Ext.form.ComboBox({
			    store: deptDS,
			    displayField:'equipmentDept',
			    valueField:'equipmentDept',
			    hiddenName:'equipmentDept',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all'
		    });
		    deptCom.applyTo('deptSelect');
		    deptCom.on('select', eqpTypeChange);

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
			eqpTypeCom.on('select', this.loadPeriod);
			eqpTypeCom.on('select', eqpTypeChange);

		    //设置pm周期
			periodCom = new Ext.form.ComboBox({
			    store: periodDS,
			    displayField:'periodName',
			    valueField:'periodIndex',
			    hiddenName:'periodIndex',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all'
		    });
		    periodCom.applyTo('periodSelect');
		    
		  

		},

		initLoad : function() {
			var eqpType = '<%=eqpType%>';
			var dept = '<%=dept%>';
			deptDS.load({callback:function(){ deptCom.setValue(dept); if(dept != ''){eqpTypeChange();} }});
	    	eqpTypeDS.load({callback:function(){ eqpTypeCom.setValue(eqpType); if(eqpType != ''){eqpForm.loadPeriod();eqpTypeChange();} }});
		},

		loadPeriod : function() {
			var val = eqpTypeCom.getValue(), periodIndex  = '<%=UtilFormatOut.checkNull(request.getParameter("periodIndex"))%>';
	 		periodDS.load({params:{equipmentType:val},callback:function(){ periodCom.setValue(periodIndex); }});
		}

	}
}();

Ext.EventManager.onDocumentReady(eqpForm.init,eqpForm,true);

</script>

<form name="eqpForm" method="post">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <fieldset><legend>查询设备保养用时</legend>

      	<table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">
      		<tr bgcolor="#DFE1EC">
				<td width="15%" class="en11pxb" bgcolor="#ACD5C9">表单开始时间</td>
				<td width="28%"><input type="text" id="startDate" name="startDate" value="<%=startDate%>" readonly></td>
				<td width="15%" bgcolor="#ACD5C9">到:</td>
				<td width="28%"><input type="text" id="endDate" name="endDate" value="<%=endDate%>" readonly></td>
		    </tr>

			<tr bgcolor='#DFE1EC'>
				<td class="en11pxb" bgcolor="#ACD5C9">设备大类</td>
				<td><input type="text" size="40" name="eqpTypeSelect" autocomplete="off"/></td>

				<td class="en11pxb" bgcolor="#ACD5C9">PM周期</td>
				<td><input type="text" size="40" name="periodSelect" autocomplete="off"/></td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td class="en11pxb" bgcolor="#ACD5C9">部门</td>
				<td><input type="text" size="40" name="deptSelect" id="deptSelect" autocomplete="off"/></td>

				<td class="en11pxb" bgcolor="#ACD5C9">Target</td>
                <td> 
                    <select id="targetSelect" name="targetSelect" >
                        <option selected="selected" value=""></option>
                        <option value="<1">< 1</option>
                        <option value=">=1">>= 1</option>
                        <option value=">=2">>= 2</option>
                        <option value=">=4">>= 4</option>
                        <option value=">=8">>= 8</option>
                        <option value=">=12">>= 12</option>
                    </select>
                </td>
			</tr>

		</table>
	  </fieldset>
	</td>
  <tr>
</table>

<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0" id="table3">
  <tr>
    <td valign="top"> <fieldset> <legend>设备列表</legend>
        <table width="100%" border="0" cellspacing="1" cellpadding="0" id="table4">
          <tr bgcolor="#DFE1EC">

            <td width="33%" class="en11pxb"><table cellSpacing="1" cellPadding="0" width="95%" border="0">
              <tr align="middle" bgColor="#dfe1ec">
                <td class="title-en" align="middle" width="299"></td>
              </tr>
              <tr bgColor="#dfe1ec">
                <td align="middle" width="304">
                    <select name="eqpId" id="eqpId" class="select" multiple size=8 ondblclick="doAddEqp();">
		    		</select>
                </td>
              </tr>
            </table></td>

            <td width="35%" height="35" class="en11pxb" align="center">
            <table height="64" cellSpacing="1" cellPadding="0" width="95%" border="0" id="table14">
              <tr align="middle">
                <td width="140"><table cellSpacing="0" cellPadding="0" border="0" id="table15">
                    <tr>
                      <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                      <td background="<%=request.getContextPath()%>/images/button_bg.gif"><a class="button-text" href="javascript:doAddEqp();"> &gt;&gt;</a></td>
                      <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
                    </tr>
                  </table>
                    <table cellSpacing="0" cellPadding="0" border="0" id="table16">
                      <tr>
                        <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                        <td background="<%=request.getContextPath()%>/images/button_bg.gif"><a class="button-text" href="javascript:doRemoveEqp();"> &lt;&lt;</a></td>
                        <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
                      </tr>
                  </table></td>
              </tr>
            </table>
			</td>

            <td width="32%" class="en11pxb">
            <table cellSpacing="1" cellPadding="0" width="224" border="0" id="table10">
              <tr align="middle" bgColor="#dfe1ec">
                <td class="title-en" align="middle" width="299"></td>
              </tr>
              <tr bgColor="#dfe1ec">
                <td align="middle" width="304">
                    <select name="eqpIdSelected" id="eqpIdSelected" class="select" multiple size=8 ondblclick="doRemoveEqp();">
                    </select>
                </td>
              </tr>
            </table>
            </td>

          </tr>

        </table>

      </fieldset>
    </td>
  </tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/showPmFormChart')"><span>&nbsp;显示&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.eqpForm.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
	<td width="20">&nbsp;</td>
  </tr>
</table>

</form>
