<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Calendar" %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>

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

<script language="javascript">
    //Fab5 ��������
	function popUp(url1) {
	   props=window.open(url1, 'poppage', 'toolbars=0, scrollbars=1, location=0, statusbars=1, menubars=1, resizable=1, width=800, height=600, left = 240, top = 212');
	}

	function doSubmit(url) {
        if(document.abnormalChartEntry.equipmentType.value=="" && document.abnormalChartEntry.equipmentDept.value=="") {
			alert("��ѡ���豸������ţ�");
			return;
		}

		for(var i=0;i<document.abnormalChartEntry.eqpIdSelected.options.length;i++) {
			document.abnormalChartEntry.eqpIdSelected.options[i].selected = true;
		}

		loading();

		document.abnormalChartEntry.action = url;
		document.abnormalChartEntry.submit();
	}

	function doAddEqp() {
		if(document.abnormalChartEntry.eqpId.options.length==0)
		{
			alert("��ѡ���豸���࣡");
			return;
		}

		moveOption(document.abnormalChartEntry.eqpId,document.abnormalChartEntry.eqpIdSelected);
	}

	function doRemoveEqp() {
		if(document.abnormalChartEntry.eqpIdSelected.options.length==0)
		{
			alert("��ѡ���豸��");
			return;
		}
	    moveOption(document.abnormalChartEntry.eqpIdSelected,document.abnormalChartEntry.eqpId);
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

	//�豸����ѡ��
	function eqpTypeChange(){
		var newDeptValue = Ext.get('equipmentDept').dom.value;
		var newEqpValue = Ext.get('equipmentType').dom.value;

		var actionURL = '<ofbiz:url>/queryEqpIdByEqpTypeByCommon</ofbiz:url>?equipmentType='+newEqpValue+'&maintDept='+newDeptValue;

		actionURL = encodeURI(actionURL);

		if(oldEqpValue != newEqpValue || oldDeptValue != newDeptValue){
			Ext.lib.Ajax.formRequest('abnormalChartEntry',actionURL,{success: commentSuccess, failure: commentFailure});
		}

		oldDeptValue = newDeptValue;
		oldEqpValue = newEqpValue;
	}

	//Զ�̵��óɹ�
	function commentSuccess(o){
        //clear all eqpid options
        clearEqp(document.abnormalChartEntry.eqpIdSelected);
        clearEqp(document.abnormalChartEntry.eqpId);

		var eqpArray = eval('(' + o.responseText + ')');
		var eqpArraySize = eqpArray.length;

		var equipId = document.getElementById("eqpId");
		equipId.length = 0;
		for(var i=0; i < eqpArraySize; i++){
			equipId.options[equipId.length] = new Option(eqpArray[i].equipmentId,eqpArray[i].equipmentId);
		}
	}

	//Զ�̵���ʧ��
	var commentFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
	};
</script>

<!-- yui page script-->
<script language="javascript">

var abnormalChartEntry = function() {

	var deptDS, deptCom;
	var eqpTypeDS, eqpTypeCom;

	return {
		init : function() {
			this.createDataStore();
		    this.createCombox();
		    this.initLoad();
		},

		//��������Դ
		createDataStore : function() {

			deptDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentDept</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentDept'}]))
		    });

			eqpTypeDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentTypeByCommon</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentType'}]))
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

		    //����dept
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

			//�����豸ID
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
			eqpTypeCom.on('select', eqpTypeChange);
		},

		initLoad : function() {
			var eqpType = '<%=eqpType%>';
			var dept = '<%=dept%>';
			deptDS.load({callback:function(){ deptCom.setValue(dept); if(dept != ''){eqpTypeChange();} }});
	    	eqpTypeDS.load({callback:function(){ eqpTypeCom.setValue(eqpType); if(eqpType != ''){eqpTypeChange();} }});
		}

	}
}();

Ext.EventManager.onDocumentReady(abnormalChartEntry.init,abnormalChartEntry,true);

</script>

<form name="abnormalChartEntry" method="post">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <fieldset><legend>��ѯ�豸�쳣��ʱ</legend>

      	<table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">
      		<tr bgcolor="#DFE1EC">
				<td width="15%" class="en11pxb" bgcolor="#ACD5C9">����ʼʱ��</td>
				<td width="28%"><input type="text" id="startDate" name="startDate" value="<%=startDate%>" readonly></td>
				<td width="15%" bgcolor="#ACD5C9">��:</td>
				<td width="28%"><input type="text" id="endDate" name="endDate" value="<%=endDate%>" readonly></td>
		    </tr>

			<tr bgcolor='#DFE1EC'>
				<td class="en11pxb" bgcolor="#ACD5C9">�豸����</td>
				<td><input type="text" size="40" name="eqpTypeSelect"/></td>

				<td class="en11pxb" bgcolor="#ACD5C9">����</td>
				<td><input type="text" size="40" name="deptSelect" /></td>

			</tr>

		</table>
	  </fieldset>
	</td>
  <tr>
</table>

<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0" id="table3">
  <tr>
    <td valign="top"> <fieldset> <legend>�豸�б�</legend>
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
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/AbnormalTableShow')"><span>&nbsp;��ʾ&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.abnormalChartEntry.reset();"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>
	<td width="20">&nbsp;</td>
  </tr>
</table>
</form>

<!--
<%if (Constants.CALL_ASURA_FLAG) {%>
<br>
<br>
<fieldset><legend>PMS��¼��ѯ</legend>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="javascript:popUp('http://10.57.49.7/QC/PMQuery.aspx')"><span>&nbsp;PM������ѯ&nbsp;</span></a></li>
	</ul></td>
    <td width="20">&nbsp;</td>
    <td><ul class="button">
		<li><a class="button-text" href="javascript:popUp('http://10.57.49.7/QC/PMDelete.aspx')"><span>&nbsp;PM on Time ��ѯ&nbsp;</span></a></li>
	</ul></td>
    <td width="20">&nbsp;</td>
    <td><ul class="button">
		<li><a class="button-text" href="javascript:popUp('http://10.57.49.7/QC/PMAbnormal.aspx')"><span>&nbsp;PM In Time��ѯ&nbsp;</span></a></li>
	</ul></td>
    <td width="20">&nbsp;</td>
    <td><ul class="button">
		<li><a class="button-text" href="javascript:popUp('http://10.57.49.7/QC/PMChart.aspx')"><span>&nbsp;PMS����Chart��ʾ&nbsp;</span></a></li>
	</ul></td>
    <td>&nbsp;&nbsp;&nbsp;</td>
    <td><ul class="button">
		<li><a class="button-text" href="javascript:popUp('http://10.57.49.7/MESWeb/PartsLife.aspx')"><span>&nbsp;Parts������ѯ&nbsp;</span></a></li>
	</ul></td>
  </tr>

  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="javascript:popUp('http://f5-ser-eip.csmc.com.cn/dlflo/mes/pms_report/F5_Equipment_Checklist.asp')"><span>&nbsp;CheckList��ѯ&nbsp;</span></a></li>
	</ul></td>
	<td width="20">&nbsp;</td>
    <td><ul class="button">
		<li><a class="button-text" href="javascript:popUp('http://10.57.49.7/QC/PMItemQuery.aspx')"><span>&nbsp;PM �ƻ���ѯ&nbsp;</span></a></li>
	</ul></td>
	<td width="20">&nbsp;</td>
    <td><ul class="button">
		<li><a class="button-text" href="javascript:popUp('http://10.57.49.7/QC/PMCompQuery.aspx')"><span>&nbsp;PM����ѯ&nbsp;</span></a></li>
	</ul></td>
	<td width="20">&nbsp;</td>
  </tr>
</table>
</fieldset>
<%}%>
-->