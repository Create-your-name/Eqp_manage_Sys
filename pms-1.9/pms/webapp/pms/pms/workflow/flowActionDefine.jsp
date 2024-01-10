<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*"%>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>
<%@ page import="com.csmc.pms.webapp.workflow.help.*"%>

<%@ include file="../yui-ext/ext-comdlg.jsp"%>

<%
try {
    GenericValue flowAction = (GenericValue)request.getAttribute("flowAction");
	String function = request.getParameter("function");
	List itemList = (List)request.getAttribute("itemList");

	int itemOrder = (itemList==null || itemList.size()==0)?1:0;
	if(itemOrder == 0) {
		GenericValue lastItem = (GenericValue)itemList.get(itemList.size()-1);
		itemOrder = lastItem.getInteger("itemOrder").intValue()+1;
	}

	boolean addflag = true;
	if(flowAction != null) {
		addflag = false;
		pageContext.setAttribute("flowAction", flowAction);
	}

	String actionType = addflag?"NORMAL":flowAction.getString("actionType");
	String isMsaDept = (String) request.getAttribute("isMsaDept");
	boolean isItemSavedOrRejected = false;
%>

<!-- yui page script-->
<script language="javascript">
	//��Ŷ���״��ɾ����index
	var delStatusList = new Array();
	var dcop,tabs, enabled,empty,itemShowFlag,dcopFlag;
	//��ʼ�������ǩ
	Ext.onReady(function(){
		itemShowFlag = <%=addflag%>;
		dcopFlag = <%="DCOP".equals(actionType)%>;
		tabs = new Ext.TabPanel('tabs');
        var action = tabs.addTab('action', "����");
        var item = tabs.addTab('item', "������Ŀ");
        //��action-tab�¼�
        action.on('activate', function(){
        	var td = Ext.Element.get(Ext.DomQuery.select('td',Ext.getDom('buttonArea'))[0]);
        	td.enableDisplayMode();
			td.show();
        });
        //��item-tab�¼�
        item.on('activate', function(){
        	var td = Ext.Element.get(Ext.DomQuery.select('td',Ext.getDom('buttonArea'))[0]);
        	td.enableDisplayMode();
			td.hide();
        });

        <% if("item".equals(function)) { %>
        tabs.activate('item');
        <% } else { %>
        tabs.activate('action');
    	<% } %>

        dcop = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'actionType',
	        width:80,
	        forceSelection:true
	    });
	    dcop.on('select', changeDcopPanel);
	    if(!itemShowFlag) dcop.disabled=true;

	    enabled = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'enabled',
	        width:80,
	        forceSelection:true
	    });

	    empty = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'empty',
	        width:80,
	        forceSelection:true
	    });

	    changeDcopPanel();
	    if(itemShowFlag || dcopFlag) tabs.disableTab('item');
	});

	function changeDcopPanel() {
	    var panel = Ext.get('dcopPanel');
        if('<%=Constants.ACTION_DCOP_TYPE%>' == dcop.getValue()) {
        	panel.enableDisplayMode(); panel.show();
        	tabs.disableTab('item');
        	empty.setValue('0');
        	empty.disabled = true;
		} else {
			panel.enableDisplayMode(); panel.hide();
			if(!itemShowFlag) tabs.enableTab('item');
			empty.disabled = false;
		}
	}

	//�ı䵯�����Сwidth,height
	extDlg.dlgInit('500','500');

	//����������ĿTemp����ҳ
	function addActionItemTemp(obj){
		var inputList = Ext.DomQuery.select('*[class=textinput]',Ext.getDom('x-form'));
		Ext.each(inputList, function(item) {
			item.value = "";
		});
		Ext.getDom('itemType').disabled = false;
		Ext.getDom('itemOrder').value = '<%=itemOrder%>';
		extDlg.showAddDialog(obj);
	}

	//�޸Ķ�����Ŀ����ҳ
	function editActionItemApply(obj,itemIndex) {
		var url='<ofbiz:url>/queryFlowActionItem</ofbiz:url>?itemIndex='+itemIndex;
		extDlg.showEditDialog(obj,url);
	}

	//ɾ��������Ŀ
	function deleteActionItemApply(itemIndex, actionIndex) {
		Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˼�¼��',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/deleteFlowActionItemApply</ofbiz:url>?function=item&itemIndex='+itemIndex+'&actionIndex='+actionIndex;
				document.location=url;
			}else{
				return;
			}
        });
	}

	//�޸Ķ�����ĿTemp����ҳ
	function editActionItemTemp(obj,flowActionItemTempIndex) {
		var url='<ofbiz:url>/queryFlowActionItemTemp</ofbiz:url>?flowActionItemTempIndex='+flowActionItemTempIndex;
		extDlg.showEditDialog(obj,url);
	}

	//ɾ��������ĿTemp
	function deleteActionItemTemp(flowActionItemTempIndex, actionIndex) {
		Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˼�¼��',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/deleteFlowActionItemTemp</ofbiz:url>?function=item&flowActionItemTempIndex='+flowActionItemTempIndex+'&actionIndex='+actionIndex;
				document.location=url;
			}else{
				return;
			}
        });
	}

	//�޸�ʱԶ�̵��óɹ����ҳ�渳ֵ
	function commentSuccess(o){
		var result = eval('(' + o.responseText + ')');

		if (result!=null & result!=""){
			var inputList = Ext.DomQuery.select('input,select',Ext.getDom('x-form'));
			Ext.each(inputList, function(item) {
				var jsonValue = eval('result.' + item.name);
				if(jsonValue != undefined) {
					item.value = jsonValue;
				}
			});
			var itemType = eval(result.itemType);
			changeItemType(itemType);
			Ext.getDom('itemType').disabled = true;
		}
	}

	//У��Form
	function checkForm() {
		if(Trim(Ext.getDom('itemName').value).length == 0) {
			return "��Ŀ���Ʋ���Ϊ��";
		}
		if(Trim(Ext.getDom('itemDescription').value).length == 0) {
			return "��Ŀ��������Ϊ��"
		}

		if(Ext.getDom('itemType').value == 2) {
			/*if(Ext.getDom('itemUpperSpec').value.length == 0) {
				return "�������ͣ����޲���Ϊ��";
			}*/
			if(Ext.getDom('itemUpperSpec').value.length != 0 && !IsNumeric(Ext.getDom('itemUpperSpec').value)) {
				return "���ޱ���Ϊ����";
			}
			/*if(Ext.getDom('itemLowerSpec').value.length == 0) {
				return "�������ͣ����޲���Ϊ��";
			}*/
			if(Ext.getDom('itemLowerSpec').value.length != 0 && !IsNumeric(Ext.getDom('itemLowerSpec').value)) {
				return "���ޱ���Ϊ����";
			}
			if(Ext.getDom('itemUpperSpec').value.length != 0 && Ext.getDom('itemLowerSpec').value.length != 0) {
				if(Ext.getDom('itemLowerSpec').value*1 > Ext.getDom('itemUpperSpec').value*1) {
					return "���޲��ܳ�������";
				}
			}
		} else if(Ext.getDom('itemType').value == 3) {
			if(Ext.getDom('itemOption').value.length == 0) {
				return "ѡ�����ͣ�ѡ���Ϊ��"
			}
		}

		var checkColumnLengthMsg = checkColumnLength('itemName||256||��Ŀ����;itemDescription||256||��Ŀ����');
		if(checkColumnLengthMsg.length > 0 ) {
			return checkColumnLengthMsg;
		}

		return "";
	}

	//����״��
	function addActionStatus(){
		//��ñ��е�����
		var index = Ext.DomQuery.select('tr',Ext.getDom('statusArea')).length;
		var status1Index = Ext.getDom('statusIndex_1').value;
		//���index����һ����statusName1���
		if(index == 2 && status1Index == "") {
			Ext.getDom('statusName_1').value = "";
		}
		//������к�
		var orderNum = index;
		//������
		Ext.get('statusArea').createChild({
			tag:'tr', style:'background-color:#DFE1EC', children: [
				{tag: 'td', html:orderNum, cls:'en11pxb'},
				{tag: 'td'}
		]});
		//���������
		var tdList = Ext.DomQuery.select('td',Ext.getDom('statusArea'))
		//�õ����һ�м���text��img��hidden
		var newStatus = tdList[tdList.length-1];
		Ext.DomHelper.append(newStatus, {tag:'input',name:'statusName_'+orderNum, type:'text', size:'20', cls:'input'}, true);
		var img = Ext.DomHelper.append(newStatus, {tag:'img', src: '<%=request.getContextPath()%>/pms/images/minus.gif',style:'cursor:hand',name:'statusImg_'+orderNum}, true);
		Ext.DomHelper.append(newStatus, {tag:'input', type:'hidden', name:'statusIndex_'+orderNum, value:'', cls:'input'}, true);
		//�����¼�
		Ext.EventManager.addListener(img, 'click', deleteActionStatus);
	}

	//ɾ��״��
	function deleteActionStatus(e) {
		var obj,index;
		if(e.nodeType == 1) {
			obj = e;
		} else {
			obj = e.getTarget('img');
		}
		var trList = Ext.DomQuery.select('tr',Ext.getDom('statusArea'));
		if(trList.length == 2) {
			//Ext.MessageBox.alert('����', '���һ��״������ɾ��!');
			return;
		}
		//���������
		index = obj.name.substr(obj.name.indexOf('_')+1);
		//�õ��ж���
		var row = Ext.DomQuery.select('tr',Ext.getDom('statusArea'))[index];
		//�ж�delete��ʱ��statusIndex��ֵ�Ƿ�Ϊ�գ����뼯��
		var statusIndex = Ext.DomQuery.selectNode('*[name^=statusIndex]', row);
		if(statusIndex.value != "") {
			//���statusIndex��Ϊ�գ���ʾ�Ƿ�ɾ��
			Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ����״̬��',function result(value){
				if(value=='no') return;
				if(value=='yes') {
					//��̨У��actionStatus��״̬
					Ext.lib.Ajax.request('GET','<ofbiz:url>/deleteFlowActionStatus?actionIndex=</ofbiz:url>'+Ext.getDom('actionIndex').value,
						{success : function(o) {
							var result = eval('(' + o.responseText + ')');
							if(result != null && result != "") {
								var flag = result.checkflag;
								if("error" == flag) {
									Ext.MessageBox.alert('����', '�ö����Ѿ������̣�');
								} else if("success" == flag){
									delStatusList.push(statusIndex.value);
									afterDeleteActionStatus(row);
								}
							}
						},
						failure : function() {Ext.MessageBox.alert('Status', 'Unable to connect.'); }});

				}
			});
		} else {
			afterDeleteActionStatus(row);
		}
	}

	function afterDeleteActionStatus(row) {
		Ext.Element.get(row).remove();
		//������
		var i = 0;
		var trList = Ext.DomQuery.select('tr',Ext.getDom('statusArea'));
		Ext.each(trList, function(tr) {
			//�޸ĵڶ��е�TD
			if(i > 0) {
				var td = Ext.DomQuery.selectNode('td',tr);
				td.innerHTML = i;
				var img = Ext.DomQuery.selectNode('img',tr);
				//�и�'_'ǰ����ִ�
				img.name = img.name.substr(0,img.name.indexOf('_')+1) + i;
			}
			var inputList = Ext.DomQuery.select('input',tr);
			Ext.each(inputList, function(item) {
				item.name = item.name.substr(0,item.name.indexOf('_')+1) + i;
			});
			i++;
		});
		//TODO���statusIndexΪ�գ���ֱ�Ӹĳ���һ����������name
		if(trList.length == 2) {
			Ext.getDom('statusName_1').value = "��һ��";
		}
	}

	function saveAction(){
		if(Trim(Ext.getDom('actionName').value).length == 0) {
			Ext.MessageBox.alert('����', '����������Ϊ��!');
			return;
		}
		var regex = /^MSA.*$/;
        var isMsaDept = <%=isMsaDept%>;
        if(isMsaDept==true && !regex.test(Ext.getDom('actionName').value)){
            Ext.MessageBox.alert('����', '�������Ʊ�����MSA��ͷ����!');
            return;
        }
		if(Trim(Ext.getDom('actionDescription').value).length == 0) {
			Ext.MessageBox.alert('����', '������������Ϊ��!');
			return;
		}

		var checkColumnLengthMsg = checkColumnLength('actionName||256||������;actionDescription||256||��������');
		if(checkColumnLengthMsg.length > 0 ) {
			Ext.MessageBox.alert('����', checkColumnLengthMsg);
			return;
		}
		//�ж�Status�Ƿ�Ϊ��
		if(isStatusEmpty()) {
			Ext.MessageBox.alert('����', "״������Ϊ�գ�");
			return;
		}
		//����ɾ��Status
		Ext.getDom('deleteStatus').value = delStatusList.join(',');
		doSumbit("<ofbiz:url>/saveFlowAction</ofbiz:url>");
	}

	function isStatusEmpty() {
		var length = Ext.DomQuery.select('tr',Ext.getDom('statusArea')).length;
		for(var i = 1 ; i < length; i++) {
			if(Ext.getDom('statusName_'+i) != null) {
			var statusVal = Ext.getDom('statusName_'+i).value;
			if(statusVal == "") return true;
		    }
		}
		return false;
	}

	function doSumbit(url) {
		loading();
		document.flowActionForm.action=url;
		document.flowActionForm.submit();
	}

	function queryDcop() {
		var url = "<ofbiz:url>/queryDcopList</ofbiz:url>?actionName=" + Ext.getDom('actionName').value;
		window.open(url,"dcop",
			"top=130,left=240,width=685,height=180,title=,channelmode=0," +
			"directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
			"scrollbars=1,status=1,titlebar=0,toolbar=no");
	}

	function cancel() {
		doSumbit('<ofbiz:url>/queryFlowActionList</ofbiz:url>');
	}

	function deleteAction() {
		Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˶�����',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/deleteFlowAction</ofbiz:url>?actionIndex='+Ext.getDom('actionIndex').value;
				document.location=url;
			}else{
				return;
			}
        });
	}

	function sendSubmit() {
		if(Trim(Ext.getDom('ownerProcess').value).length == 0) {
			Ext.MessageBox.alert('����', '��ѡ���տγ�����!');
			return;
		}

		Ext.MessageBox.confirm('��������ȷ��', '��ȷ��Ҫ���Ͷ�����Ŀ�޸�������',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/sendSubmitFlowActionItem</ofbiz:url>?function=item&actionIndex=' + Ext.getDom('actionIndex').value + '&ownerProcess=' + Ext.getDom('ownerProcess').value;
				document.location=url;
			}else{
				return;
			}
        });
	}

	function changeItemType(item) {
		if(item=='<%=Constants.TEXT_ITEM%>') {
			Ext.getDom('itemOption').disabled = true;
			Ext.getDom('itemUnit').disabled = true;
			Ext.getDom('itemUpperSpec').disabled = true;
			Ext.getDom('itemLowerSpec').disabled = true;
		} else if(item == '<%=Constants.NUMBER_ITEM%>') {
			Ext.getDom('itemOption').disabled = true;
			Ext.getDom('itemUnit').disabled = false;
			Ext.getDom('itemUpperSpec').disabled = false;
			Ext.getDom('itemLowerSpec').disabled = false;
		} else  if(item == '<%=Constants.OPTION_ITEM%>') {
			Ext.getDom('itemOption').disabled = false;
			Ext.getDom('itemUnit').disabled = true;
			Ext.getDom('itemUpperSpec').disabled = true;
			Ext.getDom('itemLowerSpec').disabled = true;
		}
	}
</script>

<fieldset><legend>��������</legend>
<!-- ����Tabs -->
<%
    String eventType = request.getParameter("eventType");
	String eventName = request.getParameter("eventName");
%>
<div id="tabs">
	<div id="action" class="tab-content">
		<!--��������-->
		<form name="flowActionForm" method="post">
		<input name="actionIndex" type="hidden" value="<ofbiz:inputvalue entityAttr="flowAction" field="actionIndex" default="" tryEntityAttr="false"/>" />
		<input name="eventType" type="hidden" value="<ofbiz:inputvalue entityAttr="flowAction" field="eventType" default="<%=eventType%>" tryEntityAttr="false"/>" />
		<input name="eventName" type="hidden" value="<ofbiz:inputvalue entityAttr="flowAction" field="eventName" default="<%=eventName%>" tryEntityAttr="false"/>" />
		<input name="equipmentType" type="hidden" value="<%=request.getParameter("equipmentType")%>" />
		<input name="pcStyle" type="hidden" value="<%=request.getParameter("pcStyle")%>" />
		<input name="frozen" type="hidden" value="1" />
		<input name="deleteStatus" type="hidden" value="" />
		<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
			<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">������</td>
          		<td width="85%"><input name="actionName" type="text" class="input"<%if("DCOP".equals(actionType)) { %> readonly<%}%> value="<ofbiz:inputvalue entityAttr="flowAction" field="actionName" default="" tryEntityAttr="true"/>" size="50"/><img id="dcopPanel" src="<%=request.getContextPath()%>/images/icon_search.gif" width="15" height="16" border="0" onClick="queryDcop()" style="cursor:hand"></td>
         	</tr>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">��������</td>
          		<td width="85%"><input name="actionDescription" type="text" class="input" value="<ofbiz:inputvalue entityAttr="flowAction" field="actionDescription" default="" tryEntityAttr="true"/>" size="50"/></td>
         	</tr>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">�Ƿ�DCOP</td>
          		<td width="85%"><select id="actionType" name="actionType">
		      	<option value="NORMAL"<% if("NORMAL".equals(actionType)) {%> selected<% } %>>��</option>
		      	<%if (Constants.CALL_TP_FLAG) {%>
		      	    <option value="DCOP"<% if("DCOP".equals(actionType)) {%> selected<% } %>>��</option>
		      	<%}%>
		      </select></td>
         	</tr>
         	<% String enabled = addflag?"1":flowAction.getString("enabled");%>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">�Ƿ�ʹ��</td>
          		<td width="85%"><select id="enabled" name="enabled">
		      	<option value="1"<% if("1".equals(enabled)) {%> selected<% } %>>��</option>
		      	<option value="0"<% if("0".equals(enabled)) {%> selected<% } %>>��</option>
		      </select></td>
         	</tr>
         	<% String empty = addflag?"0":flowAction.getString("empty");%>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">�Ƿ���ʱ����</td>
          		<td width="85%"><select id="empty" name="empty">
		      	<option value="1"<% if("1".equals(empty)) {%> selected<% } %>>��</option>
		      	<option value="0"<% if("0".equals(empty)) {%> selected<% } %>>��</option>
		      </select></td>
         	</tr>
		</table>
		<!--����״��-->
		<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30" id="statusArea">
			<tr bgcolor="#ACD5C9">
        		<td width="2%"><img src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addActionStatus()"/></td>
          		<td width="98%" class="en11pxb">״��</td>
         	</tr>
         	<ofbiz:if name="statusList" size="0">
         		<ofbiz:iterator name="cust" property="statusList">
	         	<tr bgcolor="#DFE1EC">
	        		<td width="2%" class="en11pxb"><ofbiz:entityfield attribute="cust" field="statusOrder"/></td>
	          		<td width="98%"><input name="statusName_<ofbiz:entityfield attribute="cust" field="statusOrder"/>" type="text" class="input" value="<ofbiz:entityfield attribute="cust" field="statusName"/>" size="20"/><img name="statusImg_<ofbiz:entityfield attribute="cust" field="statusOrder"/>" src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="deleteActionStatus(this)"/><input type="hidden" name="statusIndex_<ofbiz:entityfield attribute="cust" field="statusOrder"/>" value="<ofbiz:inputvalue entityAttr="cust" field="statusIndex"/>"></td>
	         	</tr>
         		</ofbiz:iterator>
         	</ofbiz:if>
         	<ofbiz:unless name="statusList" size="0">
	         	<tr bgcolor="#DFE1EC">
	        		<td width="2%" class="en11pxb">1</td>
	          		<td width="98%"><input name="statusName_1" type="text" class="input" value="��һ��" size="20"/><img name="statusImg_1" src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="deleteActionStatus(this)" /><input type="hidden" name="statusIndex_1" value=""></td>
	         	</tr>
         	</ofbiz:unless>
		</table>
		</form>
    </div>

    <div id="item" class="tab-content">
    	<!--Item��Ϣ-->
		<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
			<tr bgcolor="#ACD5C9">
        		<td width="5%" align="center">
        		    <img src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addActionItemTemp(this)"/>
        		</td>
          		<td width="5%" class="en11pxb">���</td>
          		<td width="23%" class="en11pxb">��Ŀ����</td>
          		<td width="22%" class="en11pxb">˵��</td>
          		<td width="10%" class="en11pxb">��ʽ</td>
          		<td width="8%" class="en11pxb">��λ</td>
          		<td width="8%" class="en11pxb">����</td>
          		<td width="8%" class="en11pxb">����</td>
          		<td width="10%" class="en11pxb">Ԥ��</td>
         	</tr>
         	<ofbiz:if name="itemList">
		        <ofbiz:iterator name="cust" property="itemList">
		        	<% GenericValue attrObject = (GenericValue)pageContext.findAttribute("cust"); %>
			        <tr bgcolor="#DFE1EC">
			          <td align="center">
			            <img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="deleteActionItemApply('<ofbiz:inputvalue entityAttr="cust" field="itemIndex"/>', '<ofbiz:inputvalue entityAttr="cust" field="actionIndex"/>')" />
			          </td>

			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemOrder"/></td>

			          <td class="en11px">
			              <a href="#" onclick="editActionItemApply(this,'<ofbiz:inputvalue entityAttr="cust" field="itemIndex"/>')">
			                  <ofbiz:entityfield attribute="cust" field="itemName"/>
			              </a>
			          </td>

			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemDescription"/></td>

			          <td class="en11px">
			              <%=WorkflowHelper.getItemTypeText(attrObject.getInteger("itemType").intValue())%>
			              <ofbiz:entityfield attribute="cust" field="itemOption"/>
			          </td>
			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemUnit"/></td>
			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemUpperSpec"/></td>
			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemLowerSpec"/></td>
			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="defaultValue"/></td>
			        </tr>
		      	</ofbiz:iterator>
      		</ofbiz:if>
		</table>

		<br>
		<!--Item Temp��Ϣ-->
		<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
			<tr bgcolor="#ACD5C9">
			    <td class="en11pxb"></td>
        		<td class="en11pxb">��������</td>
        		<td class="en11pxb">������</td>
        		<td class="en11pxb">����ʱ��</td>
        		<td class="en11pxb">״̬</td>
          		<td class="en11pxb">���</td>
          		<td class="en11pxb">��Ŀ����</td>
          		<td class="en11pxb">˵��</td>
          		<td class="en11pxb">��ʽ</td>
          		<td class="en11pxb">��λ</td>
          		<td class="en11pxb">����</td>
          		<td class="en11pxb">����</td>
          		<td class="en11pxb">Ԥ��</td>
         	</tr>

         	<ofbiz:if name="itemTempList">
		        <ofbiz:iterator name="cust" property="itemTempList">
		        	<%
		        	GenericValue attrObject = (GenericValue)pageContext.findAttribute("cust");
		        	int itemTempStatus = attrObject.getInteger("status").intValue();
		        	if (itemTempStatus == Constants.SAVED_CODE || itemTempStatus == Constants.REJECTED_CODE) {
		        	    isItemSavedOrRejected = true;
		        	}
		        	%>

			        <tr bgcolor="#DFE1EC">
			          <td align="center">
			          <%if (attrObject.getInteger("status").intValue() != 2) {%>
			              <img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="deleteActionItemTemp('<ofbiz:inputvalue entityAttr="cust" field="flowActionItemTempIndex"/>', '<ofbiz:inputvalue entityAttr="cust" field="actionIndex"/>')" />
			          <%}%>
			          </td>

			          <td class="en11px">
			              <%=WorkflowHelper.getEvtText(attrObject.getString("evt"))%>
			          </td>

			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="transBy"/></td>

			          <td class="en11px">
			              <%=CommonUtil.toGuiDate(attrObject.getTimestamp("updateTime"), "yyyy-MM-dd HH:mm:ss")%>
			          </td>

			          <td class="en11px">
			              <%=WorkflowHelper.getStatusText(itemTempStatus)%>
			          </td>

			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemOrder"/></td>

			          <td class="en11px">
			              <a href="#" onclick="editActionItemTemp(this,'<ofbiz:inputvalue entityAttr="cust" field="flowActionItemTempIndex"/>')">
			                  <ofbiz:entityfield attribute="cust" field="itemName"/>
			              </a>
			          </td>

			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemDescription"/></td>

			          <td class="en11px">
			              <%=WorkflowHelper.getItemTypeText(attrObject.getInteger("itemType").intValue())%>
			              <ofbiz:entityfield attribute="cust" field="itemOption"/>
			          </td>

			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemUnit"/></td>
			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemUpperSpec"/></td>
			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="itemLowerSpec"/></td>
			          <td class="en11px"><ofbiz:entityfield attribute="cust" field="defaultValue"/></td>
			        </tr>
		      	</ofbiz:iterator>
      		</ofbiz:if>

      		<%if (isItemSavedOrRejected) {%>
      		    <tr bgcolor="#DFE1EC">
      		        <td class="en11px" colspan="13">
      		            *��������ѡ��
      		            <select name="ownerProcess" id="ownerProcess">
      		                <option value=""></option>
          		            <ofbiz:if name="ownerProcessList">
                    			<ofbiz:iterator name="cust" property="ownerProcessList" type="java.util.Map">
                    			    <option value="<ofbiz:entityfield attribute="cust" field="SECTION_LEADER"/>">
                    			        <ofbiz:entityfield attribute="cust" field="SECTION"/>
                    			        :
                    			        <ofbiz:entityfield attribute="cust" field="ACCOUNT_NAME"/>
                    			    </option>
                    		    </ofbiz:iterator>
                    	    </ofbiz:if>
        			    </select>
      		            <input type="button" name="btn" value="����ǩ��" style="cursor:hand" onclick="sendSubmit()">
                    </td>
      		    </tr>
      		<%}%>
		</table>
    </div>
</div>
</fieldset>

<!--��ť-->
<table border="0" cellspacing="0" cellpadding="0" id="buttonArea">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:saveAction();"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>
	<!--<td><ul class="button">
			<li><a class="button-text" href="javascript:queryActionList();"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>-->
	<td><ul class="button">
			<li><a class="button-text" href="javascript:deleteAction()"><span>&nbsp;ɾ��&nbsp;</span></a></li>
	</ul></td>
	<td><ul class="button">
			<li><a class="button-text" href="javascript:cancel()"><span>&nbsp;ȡ��&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">������Ŀ</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="��Ŀ����">
            <div class="inner-tab" id="x-form">
                <form action="<ofbiz:url>/saveFlowActionItemTemp?function=item</ofbiz:url>" method="post" id="actionItemForm" onsubmit="return false;">

                <input name="flowActionItemTempIndex" class="textinput" type="hidden" value="<%=UtilFormatOut.checkNull(request.getParameter("flowActionItemTempIndex"))%>" />
                <input name="itemIndex" class="textinput" type="hidden" value="<%=UtilFormatOut.checkNull(request.getParameter("itemIndex"))%>" />

                <input name="actionIndex" type="hidden" value="<ofbiz:inputvalue entityAttr="flowAction" field="actionIndex" default="" tryEntityAttr="false"/>" />
				<input name="eventType" type="hidden" value="<ofbiz:inputvalue entityAttr="flowAction" field="eventType" default="" tryEntityAttr="false"/>" />
				<input name="eventName" type="hidden" value="<ofbiz:inputvalue entityAttr="flowAction" field="eventName" default="" tryEntityAttr="false"/>" />

				<!--<input name="itemOrder" type="hidden" value="" />-->
                <p>
                <label for="name"><small>��Ŀ����</small></label>
            	<input class="textinput" type="text" name="itemName" value="" size="22" tabindex="1" /></p>
            	<p>
                <label for="description"><small>��Ŀ����</small></label>
                <input class="textinput" type="text" name="itemDescription" value="" size="22" tabindex="2" /></p>
                <p>
                <label for="itemType"><small>��Ŀ��ʽ</small></label>
                <select name="itemType" class="select" tabindex="3" onChange="changeItemType(this.value)">
			      	<option value="<%=Constants.TEXT_ITEM%>">����</option>
			      	<option value="<%=Constants.NUMBER_ITEM%>">����</option>
			      	<option value="<%=Constants.OPTION_ITEM%>">ѡ��</option>
			    </select></p>
			    <p>
			    <label for="itemOption"><small>ѡ��<font color="red">������"|"����ѡ�</font></small></label>
                <input class="textinput" type="text" name="itemOption" value="" size="22" tabindex="4" /></p>
                <p>
                <label for="defaultValue"><small>Ԥ��ֵ</small></label>
                <input class="textinput" type="text" name="defaultValue" value="" size="22" tabindex="5" /></p>
                <p>
                <label for="itemUnit"><small>��λ</small></label>
                <input class="textinput" type="text" name="itemUnit" value="" size="22" tabindex="6" /></p>
                <p>
                <label for="itemUpperSpec"><small>����</small></label>
                <input class="textinput" type="text" name="itemUpperSpec" value="" size="22" tabindex="7" /></p>
                <p>
                <label for="itemLowerSpec"><small>����</small></label>
                <input class="textinput" type="text" name="itemLowerSpec" value="" size="22" tabindex="8" /></p>
                <p>
                <label for="itemOrder"><small>���</small></label>
                <input class="textinput" type="text" name="itemOrder" value="" size="22" tabindex="9" /></p>
                </form>
            </div>
        </div>
    </div>
    <div class="x-dlg-ft">
        <div id="dlg-msg">
            <span id="post-error" class="posting-msg"><img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" />&nbsp;<span id="post-error-msg"></span></span>
        </div>
    </div>
</div>

<%
} catch(Exception e) {
    out.print(e.getMessage());
}
%>