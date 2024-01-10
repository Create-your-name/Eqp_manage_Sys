<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>

<%
	String sDate = request.getParameter("startDate");

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	if (null == sDate) {
		sDate = formatter.format(Calendar.getInstance().getTime());
	}

	//开始时间默认比结束时间早一周
   	Date dd = formatter.parse(sDate);  
   	Calendar  calendar = Calendar.getInstance(); 
   	calendar.setTime(dd); 
   	calendar.add(Calendar.DAY_OF_MONTH, -7);   
   	String eDate = formatter.format(calendar.getTime()); 
	

	String eqpType = UtilFormatOut.checkNull(request.getParameter("equipmentType"));
	//String dept = UtilFormatOut.checkNull(request.getParameter("equipmentDept"));
	String dept = UtilFormatOut.checkNull((String)request.getAttribute("equipmentDept"));
%>

<script lanaguage="javascript">

	function doSubmit(url) {
		if(document.eqpForm.startDate.value=="") {
			alert("请选择开始日期！");
			return;
		}

		if(document.eqpForm.endDate.value=="") {
			alert("请选择结束日期！");
			return;
		}

		if(document.eqpForm.equipmentType.value=="") {
			alert("请选择设备大类！");
			return;
		}

		if(document.eqpForm.periodIndex.value=="") {
			alert("请选择PM周期！");
			return;
		}

		if(document.eqpForm.itemIndex.value=="") {
			alert("请选择项目！");
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
	
	var oldItemIndex="";
	// 根据设备大类，周期，项目，过滤得到有数据的设备ID
	function filterEqpTypeChange()
	{
		var periodIndex = Ext.get('periodIndex').dom.value;
		var newItemIndex = Ext.get('itemIndex').dom.value;
		var sDate = document.eqpForm.startDate.value;
		var eDate = document.eqpForm.endDate.value;
		
		var actionURL = '<ofbiz:url>/queryEqpIdByValidData</ofbiz:url>?periodIndex='+periodIndex+'&itemIndex='+newItemIndex+'&sDate='+sDate+'&eDate='+eDate+'&isMsa=1';

		actionURL = encodeURI(actionURL);

		if(oldItemIndex != newItemIndex){
			Ext.lib.Ajax.formRequest('eqpForm',actionURL,{success: commentSuccess, failure: commentFailure});
		}

		oldItemIndex = newItemIndex;
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
	var flowActionItemDS, flowActionItemCom;
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
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryPMPeriodByEqpTypeAndDate</ofbiz:url>?isMsa=1'}),
			    reader: new Ext.data.JsonReader({}, periodRecordDef)
		    });

		    flowActionItemDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryFlowActionItemByEqpTypeAndPeriod</ofbiz:url>?isMsa=1'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'itemName'}, {name: 'itemIndex'}]))
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
			// eqpTypeCom.on('select', this.loadFlowActionItem);
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
		    // 选择PM周期后，加上设备大类下所有设备，刷选掉没有数据的项目，2008.12.10，883609，黄海平
		    
		    periodCom.on('select', this.loadFlowActionItem);
		    
		    //设置项目
			flowActionItemCom = new Ext.form.ComboBox({
			    store: flowActionItemDS,
			    displayField:'itemName',
			    valueField:'itemIndex',
			    hiddenName:'itemIndex',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all'
		    });
		    flowActionItemCom.applyTo('flowActionItemSelect');
		    // 选择PM周期后，根据项目，刷选掉没有数据的设备ID，2008.12.11，883609，黄海平
		    
		    flowActionItemCom.on('select', filterEqpTypeChange);

		},

		initLoad : function() {
			var eqpType = '<%=eqpType%>';
			var dept = '<%=dept%>';
			deptDS.load({callback:function(){ deptCom.setValue(dept); if(dept != ''){eqpTypeChange();} }});
	    	eqpTypeDS.load({callback:function(){ eqpTypeCom.setValue(eqpType); if(eqpType != ''){eqpForm.loadPeriod(); /* eqpForm.loadFlowActionItem();*/  eqpTypeChange();} }});
		},

		loadPeriod : function() {
			var val = eqpTypeCom.getValue(), periodIndex  = '<%=UtilFormatOut.checkNull(request.getParameter("periodIndex"))%>';
	 		// periodDS.load({params:{equipmentType:val},callback:function(){ periodCom.setValue(periodIndex); }});
			var sDate = document.eqpForm.startDate.value,eDate = document.eqpForm.endDate.value;
	 		periodDS.load({params:{equipmentType:val,sDate:sDate,eDate:eDate},callback:function(){ periodCom.setValue(periodIndex); }});
		},

		loadFlowActionItem : function() {
			var val = eqpTypeCom.getValue(), itemIndex  = '<%=UtilFormatOut.checkNull(request.getParameter("itemIndex"))%>';
			var val1 = periodCom.getValue(),sDate = document.eqpForm.startDate.value,eDate = document.eqpForm.endDate.value;
	 		 flowActionItemDS.load({params:{equipmentType:val,periodIndex:val1,sDate:sDate,eDate:eDate},callback:function(){ flowActionItemCom.setValue(itemIndex); }});
	 	}
	}
}();

Ext.EventManager.onDocumentReady(eqpForm.init,eqpForm,true);

</script>

<form name="eqpForm" method="post">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <fieldset><legend>查询MSA机台保养纪录</legend>

      	<table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">
      		<tr bgcolor="#DFE1EC">
				<td width="15%" class="en11pxb" bgcolor="#ACD5C9"><font color="#FF0000">*</font>参数输入时间</td>
				<td width="28%" ><input type="text" ID="startDate" NAME="startDate" value="<%=eDate%>" readonly></td>
				<td width="15%" bgcolor="#ACD5C9"><font color="#FF0000">*</font>到:</td>
				<td width="28%"><input type="text" ID="endDate" NAME="endDate" value="<%=sDate%>" readonly></td>
		    </tr>

			<tr bgcolor='#DFE1EC'>
				<td class="en11pxb" bgcolor="#ACD5C9">部门</td>
				<td><input type="text" size="40" name="deptSelect" autocomplete="off"/></td>

				<td class="en11pxb" bgcolor="#ACD5C9"><font color="#FF0000">*</font>设备大类</td>
				<td><input type="text" size="40" name="eqpTypeSelect" autocomplete="off"/></td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td class="en11pxb" bgcolor="#ACD5C9"><font color="#FF0000">*</font>PM周期</td>
				<td><input type="text" size="40" name="periodSelect" autocomplete="off"/></td>

				<td class="en11pxb" bgcolor="#ACD5C9"><font color="#FF0000">*</font>项目</td>
				<td><input type="text" size="40" name="flowActionItemSelect" autocomplete="off"/></td>
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
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/showMsaPm')"><span>&nbsp;显示&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.eqpForm.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
	<td width="20">&nbsp;</td>
  </tr>
</table>

</form>