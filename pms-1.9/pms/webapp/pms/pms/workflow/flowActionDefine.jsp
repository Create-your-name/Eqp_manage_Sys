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
	//存放动作状况删除的index
	var delStatusList = new Array();
	var dcop,tabs, enabled,empty,itemShowFlag,dcopFlag;
	//初始化界面标签
	Ext.onReady(function(){
		itemShowFlag = <%=addflag%>;
		dcopFlag = <%="DCOP".equals(actionType)%>;
		tabs = new Ext.TabPanel('tabs');
        var action = tabs.addTab('action', "动作");
        var item = tabs.addTab('item', "动作项目");
        //绑定action-tab事件
        action.on('activate', function(){
        	var td = Ext.Element.get(Ext.DomQuery.select('td',Ext.getDom('buttonArea'))[0]);
        	td.enableDisplayMode();
			td.show();
        });
        //绑定item-tab事件
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

	//改变弹出框大小width,height
	extDlg.dlgInit('500','500');

	//新增动作项目Temp弹出页
	function addActionItemTemp(obj){
		var inputList = Ext.DomQuery.select('*[class=textinput]',Ext.getDom('x-form'));
		Ext.each(inputList, function(item) {
			item.value = "";
		});
		Ext.getDom('itemType').disabled = false;
		Ext.getDom('itemOrder').value = '<%=itemOrder%>';
		extDlg.showAddDialog(obj);
	}

	//修改动作项目弹出页
	function editActionItemApply(obj,itemIndex) {
		var url='<ofbiz:url>/queryFlowActionItem</ofbiz:url>?itemIndex='+itemIndex;
		extDlg.showEditDialog(obj,url);
	}

	//删除动作项目
	function deleteActionItemApply(itemIndex, actionIndex) {
		Ext.MessageBox.confirm('删除确认', '您确信要删除此记录吗？',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/deleteFlowActionItemApply</ofbiz:url>?function=item&itemIndex='+itemIndex+'&actionIndex='+actionIndex;
				document.location=url;
			}else{
				return;
			}
        });
	}

	//修改动作项目Temp弹出页
	function editActionItemTemp(obj,flowActionItemTempIndex) {
		var url='<ofbiz:url>/queryFlowActionItemTemp</ofbiz:url>?flowActionItemTempIndex='+flowActionItemTempIndex;
		extDlg.showEditDialog(obj,url);
	}

	//删除动作项目Temp
	function deleteActionItemTemp(flowActionItemTempIndex, actionIndex) {
		Ext.MessageBox.confirm('删除确认', '您确信要删除此记录吗？',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/deleteFlowActionItemTemp</ofbiz:url>?function=item&flowActionItemTempIndex='+flowActionItemTempIndex+'&actionIndex='+actionIndex;
				document.location=url;
			}else{
				return;
			}
        });
	}

	//修改时远程调用成功后给页面赋值
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

	//校验Form
	function checkForm() {
		if(Trim(Ext.getDom('itemName').value).length == 0) {
			return "项目名称不能为空";
		}
		if(Trim(Ext.getDom('itemDescription').value).length == 0) {
			return "项目描述不能为空"
		}

		if(Ext.getDom('itemType').value == 2) {
			/*if(Ext.getDom('itemUpperSpec').value.length == 0) {
				return "数字类型，上限不能为空";
			}*/
			if(Ext.getDom('itemUpperSpec').value.length != 0 && !IsNumeric(Ext.getDom('itemUpperSpec').value)) {
				return "上限必须为数字";
			}
			/*if(Ext.getDom('itemLowerSpec').value.length == 0) {
				return "数字类型，下限不能为空";
			}*/
			if(Ext.getDom('itemLowerSpec').value.length != 0 && !IsNumeric(Ext.getDom('itemLowerSpec').value)) {
				return "下限必须为数字";
			}
			if(Ext.getDom('itemUpperSpec').value.length != 0 && Ext.getDom('itemLowerSpec').value.length != 0) {
				if(Ext.getDom('itemLowerSpec').value*1 > Ext.getDom('itemUpperSpec').value*1) {
					return "下限不能超过上限";
				}
			}
		} else if(Ext.getDom('itemType').value == 3) {
			if(Ext.getDom('itemOption').value.length == 0) {
				return "选项类型，选项不能为空"
			}
		}

		var checkColumnLengthMsg = checkColumnLength('itemName||256||项目名称;itemDescription||256||项目描述');
		if(checkColumnLengthMsg.length > 0 ) {
			return checkColumnLengthMsg;
		}

		return "";
	}

	//新增状况
	function addActionStatus(){
		//获得表中的行数
		var index = Ext.DomQuery.select('tr',Ext.getDom('statusArea')).length;
		var status1Index = Ext.getDom('statusIndex_1').value;
		//如果index超过一个则将statusName1清空
		if(index == 2 && status1Index == "") {
			Ext.getDom('statusName_1').value = "";
		}
		//获得序列号
		var orderNum = index;
		//新增行
		Ext.get('statusArea').createChild({
			tag:'tr', style:'background-color:#DFE1EC', children: [
				{tag: 'td', html:orderNum, cls:'en11pxb'},
				{tag: 'td'}
		]});
		//获得所有列
		var tdList = Ext.DomQuery.select('td',Ext.getDom('statusArea'))
		//得到最后一列加上text，img，hidden
		var newStatus = tdList[tdList.length-1];
		Ext.DomHelper.append(newStatus, {tag:'input',name:'statusName_'+orderNum, type:'text', size:'20', cls:'input'}, true);
		var img = Ext.DomHelper.append(newStatus, {tag:'img', src: '<%=request.getContextPath()%>/pms/images/minus.gif',style:'cursor:hand',name:'statusImg_'+orderNum}, true);
		Ext.DomHelper.append(newStatus, {tag:'input', type:'hidden', name:'statusIndex_'+orderNum, value:'', cls:'input'}, true);
		//加上事件
		Ext.EventManager.addListener(img, 'click', deleteActionStatus);
	}

	//删除状况
	function deleteActionStatus(e) {
		var obj,index;
		if(e.nodeType == 1) {
			obj = e;
		} else {
			obj = e.getTarget('img');
		}
		var trList = Ext.DomQuery.select('tr',Ext.getDom('statusArea'));
		if(trList.length == 2) {
			//Ext.MessageBox.alert('警告', '最后一个状况不能删除!');
			return;
		}
		//获得行索引
		index = obj.name.substr(obj.name.indexOf('_')+1);
		//得到行对象
		var row = Ext.DomQuery.select('tr',Ext.getDom('statusArea'))[index];
		//判断delete的时候statusIndex的值是否为空，放入集合
		var statusIndex = Ext.DomQuery.selectNode('*[name^=statusIndex]', row);
		if(statusIndex.value != "") {
			//如果statusIndex不为空，提示是否删除
			Ext.MessageBox.confirm('删除确认', '您确信要删除此状态吗？',function result(value){
				if(value=='no') return;
				if(value=='yes') {
					//后台校验actionStatus的状态
					Ext.lib.Ajax.request('GET','<ofbiz:url>/deleteFlowActionStatus?actionIndex=</ofbiz:url>'+Ext.getDom('actionIndex').value,
						{success : function(o) {
							var result = eval('(' + o.responseText + ')');
							if(result != null && result != "") {
								var flag = result.checkflag;
								if("error" == flag) {
									Ext.MessageBox.alert('警告', '该动作已经绑定流程！');
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
		//重命名
		var i = 0;
		var trList = Ext.DomQuery.select('tr',Ext.getDom('statusArea'));
		Ext.each(trList, function(tr) {
			//修改第二行的TD
			if(i > 0) {
				var td = Ext.DomQuery.selectNode('td',tr);
				td.innerHTML = i;
				var img = Ext.DomQuery.selectNode('img',tr);
				//切割'_'前面的字串
				img.name = img.name.substr(0,img.name.indexOf('_')+1) + i;
			}
			var inputList = Ext.DomQuery.select('input',tr);
			Ext.each(inputList, function(item) {
				item.name = item.name.substr(0,item.name.indexOf('_')+1) + i;
			});
			i++;
		});
		//TODO如果statusIndex为空，则直接改成下一步，否则保留name
		if(trList.length == 2) {
			Ext.getDom('statusName_1').value = "下一步";
		}
	}

	function saveAction(){
		if(Trim(Ext.getDom('actionName').value).length == 0) {
			Ext.MessageBox.alert('警告', '动作名不能为空!');
			return;
		}
		var regex = /^MSA.*$/;
        var isMsaDept = <%=isMsaDept%>;
        if(isMsaDept==true && !regex.test(Ext.getDom('actionName').value)){
            Ext.MessageBox.alert('警告', '动作名称必须以MSA开头命名!');
            return;
        }
		if(Trim(Ext.getDom('actionDescription').value).length == 0) {
			Ext.MessageBox.alert('警告', '动作描述不能为空!');
			return;
		}

		var checkColumnLengthMsg = checkColumnLength('actionName||256||动作名;actionDescription||256||动作描述');
		if(checkColumnLengthMsg.length > 0 ) {
			Ext.MessageBox.alert('警告', checkColumnLengthMsg);
			return;
		}
		//判断Status是否为空
		if(isStatusEmpty()) {
			Ext.MessageBox.alert('警告', "状况不能为空！");
			return;
		}
		//增加删除Status
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
		Ext.MessageBox.confirm('删除确认', '您确信要删除此动作吗？',function result(value){
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
			Ext.MessageBox.alert('警告', '请选择工艺课长审批!');
			return;
		}

		Ext.MessageBox.confirm('发送申请确认', '您确信要发送动作项目修改申请吗？',function result(value){
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

<fieldset><legend>动作设置</legend>
<!-- 动作Tabs -->
<%
    String eventType = request.getParameter("eventType");
	String eventName = request.getParameter("eventName");
%>
<div id="tabs">
	<div id="action" class="tab-content">
		<!--动作设置-->
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
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">动作名</td>
          		<td width="85%"><input name="actionName" type="text" class="input"<%if("DCOP".equals(actionType)) { %> readonly<%}%> value="<ofbiz:inputvalue entityAttr="flowAction" field="actionName" default="" tryEntityAttr="true"/>" size="50"/><img id="dcopPanel" src="<%=request.getContextPath()%>/images/icon_search.gif" width="15" height="16" border="0" onClick="queryDcop()" style="cursor:hand"></td>
         	</tr>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">动作描述</td>
          		<td width="85%"><input name="actionDescription" type="text" class="input" value="<ofbiz:inputvalue entityAttr="flowAction" field="actionDescription" default="" tryEntityAttr="true"/>" size="50"/></td>
         	</tr>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">是否DCOP</td>
          		<td width="85%"><select id="actionType" name="actionType">
		      	<option value="NORMAL"<% if("NORMAL".equals(actionType)) {%> selected<% } %>>否</option>
		      	<%if (Constants.CALL_TP_FLAG) {%>
		      	    <option value="DCOP"<% if("DCOP".equals(actionType)) {%> selected<% } %>>是</option>
		      	<%}%>
		      </select></td>
         	</tr>
         	<% String enabled = addflag?"1":flowAction.getString("enabled");%>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">是否使用</td>
          		<td width="85%"><select id="enabled" name="enabled">
		      	<option value="1"<% if("1".equals(enabled)) {%> selected<% } %>>是</option>
		      	<option value="0"<% if("0".equals(enabled)) {%> selected<% } %>>否</option>
		      </select></td>
         	</tr>
         	<% String empty = addflag?"0":flowAction.getString("empty");%>
         	<tr bgcolor="#DFE1EC">
        		<td width="15%" class="en11pxb" bgcolor="#ACD5C9">是否延时输入</td>
          		<td width="85%"><select id="empty" name="empty">
		      	<option value="1"<% if("1".equals(empty)) {%> selected<% } %>>是</option>
		      	<option value="0"<% if("0".equals(empty)) {%> selected<% } %>>否</option>
		      </select></td>
         	</tr>
		</table>
		<!--动作状况-->
		<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30" id="statusArea">
			<tr bgcolor="#ACD5C9">
        		<td width="2%"><img src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addActionStatus()"/></td>
          		<td width="98%" class="en11pxb">状况</td>
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
	          		<td width="98%"><input name="statusName_1" type="text" class="input" value="下一步" size="20"/><img name="statusImg_1" src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="deleteActionStatus(this)" /><input type="hidden" name="statusIndex_1" value=""></td>
	         	</tr>
         	</ofbiz:unless>
		</table>
		</form>
    </div>

    <div id="item" class="tab-content">
    	<!--Item信息-->
		<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
			<tr bgcolor="#ACD5C9">
        		<td width="5%" align="center">
        		    <img src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addActionItemTemp(this)"/>
        		</td>
          		<td width="5%" class="en11pxb">序号</td>
          		<td width="23%" class="en11pxb">项目名称</td>
          		<td width="22%" class="en11pxb">说明</td>
          		<td width="10%" class="en11pxb">样式</td>
          		<td width="8%" class="en11pxb">单位</td>
          		<td width="8%" class="en11pxb">上限</td>
          		<td width="8%" class="en11pxb">下限</td>
          		<td width="10%" class="en11pxb">预设</td>
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
		<!--Item Temp信息-->
		<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
			<tr bgcolor="#ACD5C9">
			    <td class="en11pxb"></td>
        		<td class="en11pxb">操作类型</td>
        		<td class="en11pxb">申请人</td>
        		<td class="en11pxb">申请时间</td>
        		<td class="en11pxb">状态</td>
          		<td class="en11pxb">序号</td>
          		<td class="en11pxb">项目名称</td>
          		<td class="en11pxb">说明</td>
          		<td class="en11pxb">样式</td>
          		<td class="en11pxb">单位</td>
          		<td class="en11pxb">上限</td>
          		<td class="en11pxb">下限</td>
          		<td class="en11pxb">预设</td>
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
      		            *工艺审批选择：
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
      		            <input type="button" name="btn" value="发送签核" style="cursor:hand" onclick="sendSubmit()">
                    </td>
      		    </tr>
      		<%}%>
		</table>
    </div>
</div>
</fieldset>

<!--按钮-->
<table border="0" cellspacing="0" cellpadding="0" id="buttonArea">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:saveAction();"><span>&nbsp;保存&nbsp;</span></a></li>
	</ul></td>
	<!--<td><ul class="button">
			<li><a class="button-text" href="javascript:queryActionList();"><span>&nbsp;冻结&nbsp;</span></a></li>
	</ul></td>-->
	<td><ul class="button">
			<li><a class="button-text" href="javascript:deleteAction()"><span>&nbsp;删除&nbsp;</span></a></li>
	</ul></td>
	<td><ul class="button">
			<li><a class="button-text" href="javascript:cancel()"><span>&nbsp;取消&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">动作项目</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="项目设置">
            <div class="inner-tab" id="x-form">
                <form action="<ofbiz:url>/saveFlowActionItemTemp?function=item</ofbiz:url>" method="post" id="actionItemForm" onsubmit="return false;">

                <input name="flowActionItemTempIndex" class="textinput" type="hidden" value="<%=UtilFormatOut.checkNull(request.getParameter("flowActionItemTempIndex"))%>" />
                <input name="itemIndex" class="textinput" type="hidden" value="<%=UtilFormatOut.checkNull(request.getParameter("itemIndex"))%>" />

                <input name="actionIndex" type="hidden" value="<ofbiz:inputvalue entityAttr="flowAction" field="actionIndex" default="" tryEntityAttr="false"/>" />
				<input name="eventType" type="hidden" value="<ofbiz:inputvalue entityAttr="flowAction" field="eventType" default="" tryEntityAttr="false"/>" />
				<input name="eventName" type="hidden" value="<ofbiz:inputvalue entityAttr="flowAction" field="eventName" default="" tryEntityAttr="false"/>" />

				<!--<input name="itemOrder" type="hidden" value="" />-->
                <p>
                <label for="name"><small>项目名称</small></label>
            	<input class="textinput" type="text" name="itemName" value="" size="22" tabindex="1" /></p>
            	<p>
                <label for="description"><small>项目描述</small></label>
                <input class="textinput" type="text" name="itemDescription" value="" size="22" tabindex="2" /></p>
                <p>
                <label for="itemType"><small>项目样式</small></label>
                <select name="itemType" class="select" tabindex="3" onChange="changeItemType(this.value)">
			      	<option value="<%=Constants.TEXT_ITEM%>">文字</option>
			      	<option value="<%=Constants.NUMBER_ITEM%>">数字</option>
			      	<option value="<%=Constants.OPTION_ITEM%>">选项</option>
			    </select></p>
			    <p>
			    <label for="itemOption"><small>选项<font color="red">（请用"|"隔开选项）</font></small></label>
                <input class="textinput" type="text" name="itemOption" value="" size="22" tabindex="4" /></p>
                <p>
                <label for="defaultValue"><small>预设值</small></label>
                <input class="textinput" type="text" name="defaultValue" value="" size="22" tabindex="5" /></p>
                <p>
                <label for="itemUnit"><small>单位</small></label>
                <input class="textinput" type="text" name="itemUnit" value="" size="22" tabindex="6" /></p>
                <p>
                <label for="itemUpperSpec"><small>上限</small></label>
                <input class="textinput" type="text" name="itemUpperSpec" value="" size="22" tabindex="7" /></p>
                <p>
                <label for="itemLowerSpec"><small>下限</small></label>
                <input class="textinput" type="text" name="itemLowerSpec" value="" size="22" tabindex="8" /></p>
                <p>
                <label for="itemOrder"><small>序号</small></label>
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