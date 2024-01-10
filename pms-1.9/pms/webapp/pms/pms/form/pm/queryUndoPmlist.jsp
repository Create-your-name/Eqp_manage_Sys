<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>
<%@ page import="com.csmc.pms.webapp.common.helper.AccountHelper"%>
<%@ page import="org.ofbiz.entity.GenericDelegator"%>

<%  GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
    String maintDept = AccountHelper.getUserInfo(request, delegator).getString("accountDept");
	List undoPmList = (List)request.getAttribute("undoPmList"); %>

<!-- yui page script-->
<script language="javascript">
var eqpParam = function() {
//	var eqpIdDS,eqpIdCom;

	return {
		init : function() {
			this.createDataStore();
		    this.createCombox();
		    this.initLoad();
		},

		//设置数据源
		createDataStore : function() {
		    eqpIdDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEqpIdByEqpTypeByCommon?blank=1</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
		    });
		},

		createCombox : function() {
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
//		    eqpIdCom.on('select', this.loadParamName);

		},

		initLoad : function() {
			var maintDept = '<%=maintDept%>';
			var eqpId = '<%=UtilFormatOut.checkNull(request.getParameter("eqpId"))%>'
			eqpIdDS.load({params:{maintDept:maintDept},callback:function(){ eqpIdCom.setValue(eqpId); }});
		}
	}
}();

Ext.EventManager.onDocumentReady(eqpParam.init,eqpParam,true);

function pmRecordInfo(index,eqpId){
	loading();
	var actionURL='<ofbiz:url>/pmRecordInfo</ofbiz:url>?functionType=1&pmIndex='+index+'&eqpId='+eqpId;
	document.location.href=actionURL;
}

function query() {
	doSubmit('<ofbiz:url>/queryUndoPmformlist</ofbiz:url>');
}

function doSubmit(url) {
	loading();
	document.undoPmForm.action = url;
	document.undoPmForm.submit();
}
</script>

<form method="post" name="undoPmForm">
<input type="hidden" value="<%=maintDept%>" name="maintDept" />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>查询未完成的机台保养</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
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
			<li><a class="button-text" href="javascript:query();"><span>&nbsp;确定&nbsp;</span></a></li>
			</ul>
		</td>
	</tr>
</table>
</form>
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>保养记录表单列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="13%" class="en11pxb">机台编号</td>
          <td width="13%" class="en11pxb">表单编号</td>
          <td width="13%" class="en11pxb">撰写人</td>
          <td width="12%" class="en11pxb">表单状态</td>
          <td width="13%" class="en11pxb">表单类型</td>
          <td width="12%" class="en11pxb">保养类别</td>
          <td width="12%" class="en11pxb">表单建立时间</td>
          <td width="15%" class="en11pxb">表单最后修改时间</td>
        </tr>
         <% if(undoPmList != null && undoPmList.size() > 0) {
       		for(Iterator it = undoPmList.iterator();it.hasNext();) {
					Map map = (Map)it.next();
					String status = UtilFormatOut.checkNull((String)map.get("STATUS")); %>
	        <tr bgcolor="#DFE1EC">
		    <td class="en11px"><a href="javascript:pmRecordInfo('<%=map.get("PM_INDEX")%>','<%=map.get("EQUIPMENT_ID")%>')"><%=map.get("EQUIPMENT_ID")%></a></td>
		    <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("PM_NAME"))%></td>
		    <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("ACCOUNT_NAME"))%></td>
		    <td class="en11px"><%="0".equals(status)?"处理中":("2".equals(status)?"处理中":("-1".equals(status)?"建立未开始":""))%></td>
		    <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("FORM_TYPE"))%></td>
		    <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("PERIOD_NAME"))%></td>
		    <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("CREATE_TIME"))%></td>
		    <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("UPDATE_TIME"))%></td>
		    </tr>
	     <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>