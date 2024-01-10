<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Calendar" %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%
		String startDate = "";
		String endDate = "";
		if(pageContext.findAttribute("startDate")!=null){
			startDate = request.getAttribute("startDate").toString();
		}
		if(pageContext.findAttribute("endDate")!=null){
			endDate = request.getAttribute("endDate").toString();
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		if ("".equals(endDate))
		{
			endDate = formatter.format(Calendar.getInstance().getTime());
		}
		if ("".equals(startDate))
		{
			//开始时间默认比结束时间早一周
		   	Date dd = formatter.parse(endDate);
		   	Calendar  calendar = Calendar.getInstance();
	   		calendar.setTime(dd);
	   		calendar.add(Calendar.DAY_OF_MONTH, -7);
	   		startDate = formatter.format(calendar.getTime());
		}
%>
<%
	String sDate = request.getParameter("startDate");

	if (null == sDate) {
		sDate = formatter.format(Calendar.getInstance().getTime());
	}

	String eqpType = UtilFormatOut.checkNull(request.getParameter("equipmentType"));
	String dept = UtilFormatOut.checkNull((String)request.getParameter("equipmentDept"));
	if ("".equals(dept))
	{
		dept = UtilFormatOut.checkNull((String)request.getAttribute("equipmentDept"));
	}

	String[] arrEqpId = request.getParameterValues("eqpIdSelected");

%>

<script language="javascript">

	function doSubmit(url) {
		if (document.eqpForm.startDate.value == ""){
			Ext.MessageBox.alert("警告","请选择开始日期！");
			return;
		}
		if (document.eqpForm.endDate.value == ""){
			Ext.MessageBox.alert("警告","请选择结束日期！");
			return;
		}
		if (Ext.get('equipmentDept').dom.value == ""){
			Ext.MessageBox.alert("警告","请选择部门！");
			return;
		}
		if (Ext.get('eqpIdSelected').dom.options.length == 0 && Ext.get('eqpId').dom.options.length == 0){
			Ext.MessageBox.alert("警告","请选择设备！");
			return;
		}

		for(var i=0;i<document.eqpForm.eqpIdSelected.options.length;i++) {
			document.eqpForm.eqpIdSelected.options[i].selected = true;
		}
		for(var i=0;i<document.eqpForm.eqpId.options.length;i++) {
			document.eqpForm.eqpId.options[i].selected = true;
		}

		loading();

		document.eqpForm.action = url;
		document.eqpForm.submit();
	}

	function doAddEqp() {
		if(document.eqpForm.eqpId.options.length==0)
		{
			//Ext.MessageBox.alert("警告","请选择设备大类！");
			return;
		}

		moveOption(document.eqpForm.eqpId,document.eqpForm.eqpIdSelected);
	}

	function doRemoveEqp() {
		if(document.eqpForm.eqpIdSelected.options.length==0)
		{
			Ext.MessageBox.alert("警告","请选择设备！");
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

		var oldPeriodIndex="";
		var oldEquipmentType="";
	// 根据设备大类，周期，项目，过滤得到有数据的设备ID
	function filterEqpTypeChange()
	{
		var newPeriodIndex = Ext.get('periodIndex').dom.value;
		var newEquipmentType = Ext.get('equipmentType').dom.value;
		var sDate = document.eqpForm.startDate.value;
		var eDate = document.eqpForm.endDate.value;

		var actionURL = '<ofbiz:url>/queryEqpIdByValidDataInChart</ofbiz:url>?periodIndex='+newPeriodIndex+'&equipmentType='+newEquipmentType+'&sDate='+sDate+'&eDate='+eDate;

		actionURL = encodeURI(actionURL);

		if(oldPeriodIndex != oldPeriodIndex || oldEquipmentType != newEquipmentType){
			Ext.lib.Ajax.formRequest('eqpForm',actionURL,{success: commentSuccess, failure: commentFailure});
		}

		oldPeriodIndex = newPeriodIndex;
		oldEquipmentType = newEquipmentType;
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

	function clearEqp(eqpSelectObj) {
	    for(var i=0;i<eqpSelectObj.options.length;i++) {
			eqpSelectObj.remove(i);
			i = i - 1;
		}
	}

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
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryPMPeriodByEqpTypeAndDateInChart</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, periodRecordDef)
		    });

		    flowActionItemDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryFlowActionItemByEqpType</ofbiz:url>'}),
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
			// 选择设备大类后，刷选掉没有数据的设备ID，2008.12.16，883609，黄海平
		    //eqpTypeCom.on('select', filterEqpTypeChange);

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
		    periodCom.on('select', filterEqpTypeChange);
		},

		initLoad : function() {
			var eqpType = '<%=eqpType%>';
			var dept = '<%=dept%>';
			deptDS.load({callback:function(){ deptCom.setValue(dept); if(dept != ''){eqpTypeChange();} }});
	    	eqpTypeDS.load({callback:function(){ eqpTypeCom.setValue(eqpType); if(eqpType != ''){eqpForm.loadPeriod(); filterEqpTypeChange();} }});
		},

		loadPeriod : function() {
			var val = eqpTypeCom.getValue(), periodIndex  = '<%=UtilFormatOut.checkNull(request.getParameter("periodIndex"))%>';
	 		// periodDS.load({params:{equipmentType:val},callback:function(){ periodCom.setValue(periodIndex); }});
	 		var sDate = document.eqpForm.startDate.value,eDate = document.eqpForm.endDate.value;
	 		periodDS.load({params:{equipmentType:val,sDate:sDate,eDate:eDate},callback:function(){ periodCom.setValue(periodIndex); }});
		},
		loadFlowActionItem : function() {
			var val = eqpTypeCom.getValue(), itemIndex  = '<%=UtilFormatOut.checkNull(request.getParameter("itemIndex"))%>';
	 		flowActionItemDS.load({params:{equipmentType:val},callback:function(){  }});
		}

	}
}();

Ext.EventManager.onDocumentReady(eqpForm.init,eqpForm,true);

    //进入保养表单查询页面
    function intoPmForm(index,eqpId){
    	loading();
    	var actionURL='<ofbiz:url>/overPmFormView</ofbiz:url>?functionType=3&pmIndex='+index+'&eqpId='+eqpId;
    	document.location.href=actionURL;
    }

    //修改或查询问题跟踪
	function editFollowJob(status,index){
		var url='';
		if(status=='0'){
			//建立
			url='<ofbiz:url>/intoEditQuFollow</ofbiz:url>?followIndex='+index;
		}else if(status=='2'){
			//未结案
			url='<ofbiz:url>/intoAddItemQuFollow</ofbiz:url>?followIndex='+index;
		}else if(status=='1'){
			//结案
			url='<ofbiz:url>/overFollowJobShow</ofbiz:url>?followIndex='+index;
		}
		document.location.href=url;
	}
</script>

<form name="eqpForm" method="post">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <fieldset><legend>保养表单超规范数据查询</legend>

      	<table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">
      		<tr bgcolor="#DFE1EC">
				<td width="15%" class="en11pxb" bgcolor="#ACD5C9"><font color="#FF0000">*</font>数据输入日期</td>
				<td width="28%"><input type="text" ID="startDate" NAME="startDate" value="<%=startDate%>" readonly></td>
				<td width="15%" bgcolor="#ACD5C9"><font color="#FF0000">*</font>到:</td>
				<td width="28%"><input type="text" ID="endDate" NAME="endDate" value="<%=endDate%>" readonly></td>
		    </tr>

			<tr bgcolor='#DFE1EC'>
				<td class="en11pxb" bgcolor="#ACD5C9"><font color="#FF0000">*</font>部门</td>
				<td><input type="text" size="40" name="deptSelect" autocomplete="off"/></td>

				<td class="en11pxb" bgcolor="#ACD5C9">设备大类</td>
				<td><input type="text" size="40" name="eqpTypeSelect" autocomplete="off"/></td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td class="en11pxb" bgcolor="#ACD5C9">PM周期</td>
				<td><input type="text" size="40" name="periodSelect" autocomplete="off"/></td>

				<td class="en11pxb" bgcolor="#ACD5C9"> </td>
				<td><input type="hidden" size="40" name="flowActionItemSelect" autocomplete="off"/></td>
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
              <tr align="center" bgColor="#dfe1ec">
                <td class="title-en" align="center" width="299"></td>
              </tr>
              <tr bgColor="#dfe1ec">
                <td align="center" width="304">
                    <select name="eqpId" id="eqpId" class="select" multiple size=8 ondblclick="doAddEqp();">
		    		</select>
                </td>
              </tr>
            </table></td>

            <td width="35%" height="35" class="en11pxb" align="center">
            <table height="64" cellSpacing="1" cellPadding="0" width="95%" border="0" id="table14">
              <tr align="center">
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
              <tr align="center" bgColor="#dfe1ec">
                <td class="title-en" align="center" width="299"></td>
              </tr>
              <tr bgColor="#dfe1ec">
                <td align="center" width="304">
                    <select name="eqpIdSelected" id="eqpIdSelected" class="select" multiple size=8 ondblclick="doRemoveEqp();">
                    <% if(arrEqpId != null && arrEqpId.length > 0) {
                    	for(int i = 0 ; i < arrEqpId.length ; i++) { %>
                    	<option><%=arrEqpId[i]%></option>
                    <% }
                    } %>
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
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/queryShowPm')"><span>&nbsp;显示&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.eqpForm.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
	<td width="20">&nbsp;</td>
  </tr>
</table>
<br>
<ofbiz:if name="flag" value="OK">
<div id="queryShowPmChart" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="queryShowPmChart" style="visibility:'hidden';">
</ofbiz:unless>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
   <td><fieldset><legend></legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td class="en11pxb">设备</td>
      		<td class="en11pxb">项目名</td>
      		<td class="en11pxb">项目值</td>
            <td class="en11pxb">项目上限</td>
            <td class="en11pxb">项目下限</td>
            <td class="en11pxb">输入人</td>
            <td class="en11pxb">输入时间</td>
    	</tr>

    	<%
        	List DocumentList = (List)request.getAttribute("DocumentList");
        	if ((DocumentList != null) && (DocumentList.size()!= 0)) {
	        	for (int i = 0 ; i <= DocumentList.size()-1 ; i++){
	        		Map map = (Map)DocumentList.get(i);
	        		String pmIndex = (String)(map.get("PM_INDEX"));
	        		String EQUIPMENT_ID = (String)(map.get("EQUIPMENT_ID"));
	        		String itemIndex = (String)(map.get("ITEM_NAME"));
	        		String itemValue = (String)(map.get("ITEM_VALUE"));
	        		String itemUpperSpec = (String)(map.get("ITEM_UPPER_SPEC"));
	        		String itemLowerSpec = (String)(map.get("ITEM_LOWER_SPEC"));
	        		String transBy = (String)(map.get("TRANS_BY"));
	        		String updateTime = (String)(map.get("UPDATE_TIME"));
    	%>
    	            <tr bgcolor="#DFE1EC">
						<td class="en11px">
						    <a href="#" onclick="intoPmForm('<%=pmIndex%>','<%=EQUIPMENT_ID%>')">
						        <%=EQUIPMENT_ID%>
						    </a>
						</td>
        				<td class="en11px"><%=itemIndex%></td>
        				<td class="en11px"><%=itemValue%></td>
        				<td class="en11px"><%=itemUpperSpec%></td>
        				<td class="en11px"><%=itemLowerSpec%></td>
        				<td class="en11px"><%=transBy%></td>
        				<td class="en11px"><%=updateTime%></td>
        			</tr>
    	<%      }
    	}
    	%>

      </table>
     </td>
  </tr>
</table>
</form>