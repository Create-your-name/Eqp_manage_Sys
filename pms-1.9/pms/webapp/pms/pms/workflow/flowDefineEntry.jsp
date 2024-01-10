<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<% String eventType = request.getParameter("eventType");
   String eqpType = UtilFormatOut.checkNull(request.getParameter("equipmentType"));
   String pcStyle = UtilFormatOut.checkNull(request.getParameter("pcStyle")); %>

<!-- yui page script-->
<script language="javascript">
var flow = function() {
	var eqpTypeDS,pcStyleDS,eqpTypeCom,pcStyleCom,eventTypeCom;
	var pmPeriodDS,pcPeriodDS,pmPeriodCom,pcPeriodCom;
	var periodRecordDef;
	var pmPeriod  = '<%=UtilFormatOut.checkNull(request.getParameter("pmPeriod"))%>';
	var pcPeriod = '<%=UtilFormatOut.checkNull(request.getParameter("pcPeriod"))%>';
	return {
		init : function() {
			this.createDataStore();
		    this.createCombox();
		    this.initLoad();
		    this.changeEventType();
		},

		//��������Դ
		createDataStore : function() {
			periodRecordDef = Ext.data.Record.create([
			    {name: 'periodIndex'},
				{name: 'periodName'}
			]);

			eqpTypeDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentTypeByCommon</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentType'}]))
		    });

		    pcStyleDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryPcStyleByCommon</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'styleIndex'}, {name: 'name'}]))
		    });

		    pmPeriodDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryPMPeriodByEqpType</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, periodRecordDef)
		    });

		    pcPeriodDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryPCPeriodByPcStyle</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, periodRecordDef)
		    });
		},

		createCombox : function() {
			eventTypeCom = new Ext.form.ComboBox({
			        typeAhead: true,
			        triggerAction: 'all',
			        transform:'eventType',
			        width:170,
			        forceSelection:true
			 });
			 eventTypeCom.on('select', this.changeEventType);

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
		    eqpTypeCom.on('select', this.loadPmPeriod);

			//����pcStyle
			pcStyleCom = new Ext.form.ComboBox({
			    store: pcStyleDS,
			    displayField:'name',
			    valueField:'styleIndex',
			    hiddenName:'styleIndex',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all'
		    });
		    pcStyleCom.applyTo('pcStyleSelect');
		    pcStyleCom.on('select', this.loadPcPeriod);

		    //����pmPeriod
			pmPeriodCom = new Ext.form.ComboBox({
			    store: pmPeriodDS,
			    displayField:'periodName',
			    valueField:'periodIndex',
			    hiddenName:'pmPeriod',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all'
		    });
		    pmPeriodCom.applyTo('pmPeriodSelect');

		    //����pcPeriod
		    pcPeriodCom = new Ext.form.ComboBox({
			    store: pcPeriodDS,
			    displayField:'periodName',
			    valueField:'periodIndex',
			    hiddenName:'pcPeriod',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all'
		    });
		    pcPeriodCom.applyTo('pcPeriodSelect');
		},

		initLoad : function() {
			var eqpType = '<%=UtilFormatOut.checkNull(request.getParameter("equipmentType"))%>', pcStyle = '<%=UtilFormatOut.checkNull(request.getParameter("styleIndex"))%>', eventType = '<%=request.getParameter("eventType")==null?"PM":request.getParameter("eventType")%>';
			//eqpTypeDS.load();
			eventTypeCom.setValue(eventType);
	    	eqpTypeDS.load({callback:function(){ eqpTypeCom.setValue(eqpType); if(eqpType != ''){flow.loadPmPeriod();} }});
			pcStyleDS.load({callback:function(){ pcStyleCom.setValue(pcStyle); if(pcStyle != ''){flow.loadPcPeriod();} }});
		},

		loadPmPeriod : function() {
			var val = eqpTypeCom.getValue();//10003
	 		pmPeriodDS.load({params:{equipmentType:val},callback:function(){ pmPeriodCom.setValue(pmPeriod); pmPeriod=""; }});
		},

		loadPcPeriod : function() {
			var val = pcStyleCom.getValue();//
	 		pcPeriodDS.load({params:{pcStyle:val},callback:function(){ pcPeriodCom.setValue(pcPeriod); pcPeriod=""; }});
		},

		changeEventType : function() {
			var eventType = eventTypeCom.getValue();
			var eqpTypeArea = Ext.get('eqpTypeArea'); eqpTypeArea.enableDisplayMode();
			var pcStyleArea = Ext.get('pcStyleArea'); pcStyleArea.enableDisplayMode();
			var pmPeriodArea = Ext.get('pmPeriodArea'); pmPeriodArea.enableDisplayMode();
			var pcPeriodArea = Ext.get('pcPeriodArea'); pcPeriodArea.enableDisplayMode();
			if(eventType == 'PM') {
				eqpTypeArea.show(); pmPeriodArea.show();
				pcStyleArea.hide(); pcPeriodArea.hide();
			} else if(eventType == 'PC') {
				eqpTypeArea.hide(); pmPeriodArea.hide();
				pcStyleArea.show(); pcPeriodArea.show();
			} else if(eventType == 'TS') {
				eqpTypeArea.show(); pmPeriodArea.hide();
				pcStyleArea.hide(); pcPeriodArea.hide();
			}
		},

		returnValue: function() {
			var eventType = eventTypeCom.getValue();
			if(eventType == 'PM') {
				return pmPeriodCom.getValue();
			} else if(eventType == 'PC') {
				return pcPeriodCom.getValue();
			} else if(eventType == 'TS') {
				return eqpTypeCom.getValue();
			}
			return "";
		},

		returnEventName: function() {
			var eventType = eventTypeCom.getValue();
			if(eventType == 'PM') {
				return eqpTypeCom.getValue();
			} else if(eventType == 'PC') {
				return pcStyleCom.getValue();
			} else if(eventType == 'TS') {
				return eqpTypeCom.getValue();
			}
			return "";
		}
	}
}();

Ext.EventManager.onDocumentReady(flow.init,flow,true);

function queryFlowList() {
	var msg = checkCondition();
	if(msg.length > 0) {
		Ext.MessageBox.alert('����', msg);
		return;
	}
	Ext.getDom('eventObject').value = flow.returnValue();
	doSubmit("<ofbiz:url>/queryFlowList</ofbiz:url>");
}

function checkCondition() {
	var msg = "";
	if(Ext.getDom('eventType').value=="PC") {
		if(Ext.getDom('styleIndex').value=="") {
			msg = '��ѡ��Ѳ������!';
			return msg;
		}
		if(Ext.getDom('pcPeriod').value=="") {
			msg = '��ѡ��Ѳ������!';
			return msg;
		}
	} else if(Ext.getDom('equipmentType').value==""){
		msg = '��ѡ���豸����!';
		return msg;
	} else if(Ext.getDom('eventType').value=="PM") {
		if(Ext.getDom('pmPeriod').value=="") {
			msg = '��ѡ��������!';
			return msg;
		}
	}
	return msg;
}

function addFlow() {
	var msg = checkCondition();
	if(msg.length > 0) {
		Ext.MessageBox.alert('����', msg);
		return;
	}
	Ext.getDom('eventName').value = flow.returnEventName();
	Ext.getDom('eventObject').value = flow.returnValue();
	doSubmit("<ofbiz:url>/addFlow</ofbiz:url>");
}

function queryFlow(jobIndex) {
	Ext.getDom('jobIndex').value = jobIndex;
	Ext.getDom('eventName').value = flow.returnEventName();
	Ext.getDom('eventObject').value = flow.returnValue();
	doSubmit("<ofbiz:url>/queryFlow</ofbiz:url>");
}

function queryTempFlow(tempIndex) {
	Ext.getDom('tempIndex').value = tempIndex;
	Ext.getDom('eventName').value = flow.returnEventName();
	Ext.getDom('eventObject').value = flow.returnValue();
	doSubmit("<ofbiz:url>/queryFlow</ofbiz:url>");
}

function doSubmit(url) {
	loading();
	document.flowEntryForm.action=url;
	document.flowEntryForm.submit();
}

function showFlowDetail(jobIndex,isPrint,periodName) {
	document.location="<ofbiz:url>/viewFlowStructure</ofbiz:url>?jobIndex=" + jobIndex+"&isPrint="+isPrint+"&periodName="+periodName;
}

function queryHistFlow(jobIndex,periodName) {
	document.location="<ofbiz:url>/queryHistFlow</ofbiz:url>?jobIndex=" + jobIndex+"&periodName="+periodName;
}
</script>

<form method="post" name="flowEntryForm">
<input name="eventObject" type="hidden" value="" />
<input name="eventName" type="hidden" value="" />
<input name="jobIndex" type="hidden" value="" />
<input name="tempIndex" type="hidden" value="" />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>��������</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
			<tr bgcolor="#DFE1EC">
				<td width="15%" bgcolor="#ACD5C9" class="en11pxb">����</td>
		        <td width="85%">
					<select name="eventType">
						<option value="PM">�豸����</option>
						<option value="TS">�豸�쳣</option>
						<option value="PC">Ѳ�챣��</option>
					</select></td>
			</tr>
			<tr bgcolor="#DFE1EC" id="eqpTypeArea">
				<td class="en11pxb" bgcolor="#ACD5C9">�豸����</td>
				<td><input type="text" size="40" name="eqpTypeSelect" autocomplete="off"/></td>
			</tr>
			<tr bgcolor="#DFE1EC" id="pcStyleArea">
				<td class="en11pxb" bgcolor="#ACD5C9">Ѳ������</td>
				<td><input type="text" size="40" name="pcStyleSelect" autocomplete="off"/></td>
			</tr>
			<tr bgcolor="#DFE1EC" id="pmPeriodArea">
				<td class="en11pxb" bgcolor="#ACD5C9">(���õ�)��������</td>
				<td><input type="text" size="40" name="pmPeriodSelect" autocomplete="off"/></td>
			</tr>
			<tr bgcolor="#DFE1EC" id="pcPeriodArea">
				<td class="en11pxb" bgcolor="#ACD5C9">(���õ�)Ѳ������</td>
				<td><input type="text" size="40" name="pcPeriodSelect" autocomplete="off"/></td>
			</tr>
		</table>
      </fieldset></td>
  </tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:queryFlowList();"><span>&nbsp;��ѯ&nbsp;</span></a></li>
	</ul></td>
	<td><ul class="button">
			<li><a class="button-text" href="javascript:addFlow()"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>��������б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td class="en11pxb">��������</td>
      		<td class="en11pxb">��������</td>
      		<td class="en11pxb">��ѯ���°汾</td>
      		<td class="en11pxb">��ѯ��ʷ�汾</td>
    	</tr>

    	<ofbiz:if name="flowList">
		     <ofbiz:iterator name="cust" property="flowList">
		        <tr bgcolor="#DFE1EC">
		        	<td class="en11px"><a href="javascript:queryFlow(<ofbiz:inputvalue entityAttr="cust" field="jobIndex" default="" tryEntityAttr="false"/>)"><ofbiz:entityfield attribute="cust" field="jobName"/></a></td>
		        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="jobDescription"/></td>
		        	<td class="en11px"><img src="<%=request.getContextPath()%>/pms/images/copy_16.gif" style="cursor:hand" onclick="showFlowDetail(<ofbiz:inputvalue entityAttr="cust" field="jobIndex" default="" tryEntityAttr="false"/>,<%if ("PM".equals(request.getAttribute("eventType"))){%>'true','<%=request.getAttribute("periodName")%>'<%}else{%>'false',''<%}%>)" /></td>
		        	<%
							if ("PM".equals(request.getAttribute("eventType")))
							{
							%>
		        	<td class="en11px"><a href="javascript:queryHistFlow(<ofbiz:inputvalue entityAttr="cust" field="jobIndex" default="" tryEntityAttr="false"/>,'<%=request.getAttribute("periodName")%>')"><ofbiz:entityfield attribute="cust" field="jobName"/></a></td>
		        	<%
		        	}
		        	%>
		        </tr>
		   	</ofbiz:iterator>
      	</ofbiz:if>
      </table>
      </fieldset></td>
  </tr>
</table>

<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>��ǩ�����б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td class="en11pxb">��������</td>
      		<td class="en11pxb">��������</td>
      		<td class="en11pxb">��������</td>
      		<td class="en11pxb">������</td>
      		<td class="en11pxb">״̬(0�ݴ棬3�˻�)</td>
      		<td class="en11pxb">ʱ��</td>
    	</tr>
    	<ofbiz:if name="flowTempList">
		     <ofbiz:iterator name="cust" property="flowTempList">
		        <tr bgcolor="#DFE1EC">
		        	<td class="en11px"><a href="javascript:queryTempFlow(<ofbiz:inputvalue entityAttr="cust" field="tempIndex" default="" tryEntityAttr="false"/>)"><ofbiz:entityfield attribute="cust" field="jobName"/></a></td>
		        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="jobDescription"/></td>
		        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="submitType"/></td>
		        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="transBy"/></td>
		        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="status"/></td>
		        	<td class="en11px"><ofbiz:entityfield attribute="cust" field="updateTime"/></td>
		        </tr>
		   	</ofbiz:iterator>
      	</ofbiz:if>
      </table>
      </fieldset></td>
  </tr>
</table>
</form>