<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<% String eventType = request.getParameter("eventType");
   String eqpType = UtilFormatOut.checkNull(request.getParameter("equipmentType")); 
   String pcStyle = UtilFormatOut.checkNull(request.getParameter("pcStyle")); %>
   
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	Ext.onReady(function(){
	    var eqpType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'equipmentType',
	        width:170,
	        forceSelection:true
	    });
	    
	    var pcStyle = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'pcStyle',
	        width:170,
	        forceSelection:true
	    });
	    
	    var eventType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'eventType',
	        width:170,	
	        forceSelection:true
	    });
	    
	    eqpType.setValue("<%=eqpType%>");
	    pcStyle.setValue("<%=pcStyle%>");
	    //绑定select事件
	    eventType.on('select',eventTypeChange);
	    eqpType.on('select',changeEventName);
	    pcStyle.on('select',changeEventName);
	    
	    eventTypeChange();
	    
	    Ext.get('eqpType').dom.value = "<%=eqpType%>";
	});
	
	function changeEventName() {
		if(Ext.getDom('eventType').value=="PC") {
			Ext.getDom('eventName').value = Ext.getDom('pcStyle').value;
		} else {
			Ext.getDom('eventName').value = Ext.getDom('equipmentType').value;
		}
		//alert(Ext.getDom('eventName').value);
	}
	
	function eventTypeChange() {
		if('PC'==Ext.getDom('eventType').value) {
			Ext.getDom('pcContent').style.display="";
			Ext.getDom('eqpTypeContent').style.display="none";
		} else {
			Ext.getDom('eqpTypeContent').style.display="";
			Ext.getDom('pcContent').style.display="none";
		}
	}
	
	function queryActionList() {
		if(Ext.getDom('eventType').value=="PC") {
			if(Ext.getDom('pcStyle').value=="") {
				Ext.MessageBox.alert('警告', '请选择巡检类型!');
				return;
			}
		} else if(Ext.getDom('equipmentType').value==""){
			Ext.MessageBox.alert('警告', '请选择设备大类!');
			return;
		}
		doSubmit("<%=request.getContextPath()%>/control/queryFlowActionList");
	}
	
	function queryAction(actionIndex) {
		Ext.getDom('actionIndex').value = actionIndex;
		doSubmit("<ofbiz:url>/queryFlowAction</ofbiz:url>");
		//document.location="<ofbiz:url>/queryFlowAction</ofbiz:url>?actionIndex=" + actionIndex;
	}
	
	function addAction() {
		if(Ext.getDom('eventType').value=="PC") {
			if(Ext.getDom('pcStyle').value=="") {
				Ext.MessageBox.alert('警告', '请选择巡检类型!');
				return;
			}
		} else if(Ext.getDom('equipmentType').value==""){
			Ext.MessageBox.alert('警告', '请选择设备大类!');
			return;
		}
		doSubmit("<ofbiz:url>/queryFlowAction</ofbiz:url>");
		//document.location="<ofbiz:url>/queryFlowAction</ofbiz:url>?actionIndex=" + actionIndex;
	}
	
	function copyAction(obj,actionIndex,eqpType,actionName) {
		Ext.get('seqIndex').dom.value=actionIndex;
		Ext.get('equipmentType').dom.value=eqpType;
		Ext.get('actionName').dom.value="复制" + actionName;
		extDlg.showAddDialog(obj);
		//Ext.MessageBox.prompt('动作名', '请输入Copy后的动作名:', function(btn,text){
    	//if(btn == 'ok') {
      	//if(text=='') {
      	//	alert('动作名不能为空！');
      	//} else {
      	//	doSubmit("<ofbiz:url>/copyFlowAction</ofbiz:url>?actionIndex=" + actionIndex + "&actionName=" + text);
      		//document.location="<ofbiz:url>/copyFlowAction</ofbiz:url>?actionIndex=" + actionIndex + "&actionName=" + text;
      	//}
    	//}
    //});
	}
	
	//数据合法性校验
	function checkForm(){
        var description = Ext.get('actionName').dom.value;
		if(description==""){
			return "动作名不能为空";
		}
		return "";
	}
	
	function doSubmit(url) {
		loading();
		document.flowActionEntryForm.action=url;
		document.flowActionEntryForm.submit();
	}
	
	//修改时远程调用成功后给页面赋值
	function commentSuccess(o){
	//alert(o.responseText);
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('eqpType').dom.value=result.equipmentType;
			
			}
	}
</script>

<form method="post" name="flowActionEntryForm">
<input name="eventName" type="hidden" value="<%=request.getParameter("eventName")%>" />
<input name="actionIndex" type="hidden" value="" />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>动作条件</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
         <tr bgcolor="#DFE1EC">
          <td width="15%" class="en11pxb" bgcolor="#ACD5C9">类型</td>
          <td width="85%"><select id="eventType" name="eventType">
		      	<option value="PM" <%=("PM".equals(eventType)?"selected":"")%>>设备保养</option>
		      	<option value="TS" <%=("TS".equals(eventType)?"selected":"")%>>设备异常</option>
		      	<option value="PC" <%=("PC".equals(eventType)?"selected":"")%>>巡检保养</option>
		      </select></td>
         </tr>
         <tr bgcolor="#DFE1EC" id="eqpTypeContent">
         	<td class="en11pxb" bgcolor="#ACD5C9">设备大类</td>
       	  	<td><select id="equipmentType" name="equipmentType">
          	  <option value=""></option>
          	  <ofbiz:if name="eqpTypeList">
          	    <ofbiz:iterator name="cust" property="eqpTypeList">
          	      <option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentType"/>'><ofbiz:inputvalue entityAttr="cust" field="equipmentType"/></option>
          	      </ofbiz:iterator>
          	    </ofbiz:if>
       	    </select></td>
         </tr>
         <tr bgcolor="#DFE1EC" id="pcContent">
           <td class="en11pxb" bgcolor="#ACD5C9">巡检类型</td>
           <td><select id="pcStyle" name="pcStyle">
          	  <option value=""></option>
          	  <ofbiz:if name="pcStyleList">
          	    <ofbiz:iterator name="cust" property="pcStyleList">
          	      <option value='<ofbiz:inputvalue entityAttr="cust" field="styleIndex"/>'><ofbiz:inputvalue entityAttr="cust" field="name"/></option>
          	      </ofbiz:iterator>
          	    </ofbiz:if>
       	    </select></td>
         </tr>
      </table>
      </fieldset></td>
  </tr>
</table>    
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:queryActionList();"><span>&nbsp;查询&nbsp;</span></a></li> 
	</ul></td>
	<td><ul class="button">
			<li><a class="button-text" href="javascript:addAction();"><span>&nbsp;新增&nbsp;</span></a></li> 
	</ul></td>
  </tr>
</table>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>动作列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
      	<tr bgcolor="#ACD5C9">
      		<td class="en11pxb">动作</td>
      		<td class="en11pxb">动作描述</td>
      		<td class="en11pxb">操作</td>
    	</tr>
      	 <ofbiz:if name="actionList">
	        <ofbiz:iterator name="cust" property="actionList">
		        <tr bgcolor="#DFE1EC">
		          <td width="38%" class="en11px"><a href="javascript:queryAction('<ofbiz:inputvalue entityAttr="cust" field="actionIndex"/>')"><ofbiz:entityfield attribute="cust" field="actionName"/></a></td>
		          <td width="32%" class="en11px"><ofbiz:entityfield attribute="cust" field="actionDescription"/></td>
		          <td width="30%" class="en11px"><img src="<%=request.getContextPath()%>/pms/images/copy.gif" style="cursor:hand" onclick="copyAction(this,'<ofbiz:inputvalue entityAttr="cust" field="actionIndex"/>','<ofbiz:inputvalue entityAttr="cust" field="eventName"/>','<ofbiz:entityfield attribute="cust" field="actionName"/>')" /></td>
		        </tr>
	      </ofbiz:iterator>
      	</ofbiz:if>
      </table>
      </fieldset></td>
  </tr>
</table>  
</form><!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">动作名</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="动作名">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/copyFlowAction" method="post" id="flowActionCopy" onsubmit="return false;">
                	<input id="styleIndex" type="hidden" name="styleIndex" value="" />
                <% if(!"PC".equals(eventType)) { %>
                <p><label for="description"><small>设备大类</small></label>
                <select name="eqpType" id="eqpType" class="select">
					<ofbiz:if name="eqpTypeList">
		        		<ofbiz:iterator name="equipmentType" property="eqpTypeList">
			    			<option value='<ofbiz:inputvalue entityAttr="equipmentType" field="equipmentType"/>'><ofbiz:inputvalue entityAttr="equipmentType" field="equipmentType"/></option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
				</select></p><br/>
				<% } %>
                <p>
                <input id="seqIndex" type="hidden" name="seqIndex" value="" />
                <input id="eqpType1" type="hidden" name="eqpType1" value="" />
                <label for="name"><small>请输入Copy后的动作名:</small></label>
                <input class="textinput" type="text" name="actionName" id="actionName" value="" size="22" tabindex="1" />
                </p>
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