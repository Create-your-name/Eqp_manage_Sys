<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>
<%@ page import="com.csmc.pms.webapp.common.helper.AccountHelper"%>
<%@ page import="org.ofbiz.entity.GenericDelegator"%>

<%  GenericDelegator delegator = CommonUtil.getPmsDelegator(request);
    String maintDept = AccountHelper.getUserInfo(request, delegator).getString("accountDept");
	List undoTsList = (List)request.getAttribute("undoTsList"); %>

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

		//��������Դ
		createDataStore : function() {
		    eqpIdDS = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEqpIdByEqpTypeByCommon?blank=1</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
		    });
		},

		createCombox : function() {
			//����eqpId
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

function intoAbnormalForm(index,eqpId){
	loading();
	var actionURL='<ofbiz:url>/intoAbnormalFormManageEntry</ofbiz:url>?functionType=1&abnormalIndex='+index+'&eqpId='+eqpId;
	document.location.href=actionURL;
}

function query() {
	doSubmit('<%=request.getContextPath()%>/control/queryUndoTsformlist');
}

function doSubmit(url) {
	loading();
	document.undoTsForm.action = url;
	document.undoTsForm.submit();
}
</script>

<form method="post" name="undoTsForm">
<input type="hidden" value="<%=maintDept%>" name="maintDept" />
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>��ѯδ��ɵĻ�̨�쳣</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC" height="30">
          <td width="10%" class="en11pxb">&nbsp;��&nbsp;��&nbsp;</td>
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
			<li><a class="button-text" href="javascript:query();"><span>&nbsp;ȷ��&nbsp;</span></a></li>
			</ul>
		</td>
	</tr>
</table>
</form>
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�쳣��¼���б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="13%" class="en11pxb">��̨���</td>
          <td width="13%" class="en11pxb">�����</td>
          <td width="13%" class="en11pxb">������</td>
          <td width="13%" class="en11pxb">�쳣ʱ��</td>
          <td width="12%" class="en11pxb">��״̬</td>
          <td width="12%" class="en11pxb">������ʱ��</td>
          <td width="15%" class="en11pxb">������޸�ʱ��</td>
        </tr>
         <% if(undoTsList != null && undoTsList.size() > 0) {
       		for(Iterator it = undoTsList.iterator();it.hasNext();) {
					Map map = (Map)it.next();
					String status = UtilFormatOut.checkNull((String)map.get("STATUS")); %>
	        <tr bgcolor="#DFE1EC">
		    <td class="en11px"><a href="javascript:intoAbnormalForm('<%=map.get("ABNORMAL_INDEX")%>','<%=map.get("EQUIPMENT_ID")%>')"><%=map.get("EQUIPMENT_ID")%></a></td>
		    <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("ABNORMAL_NAME"))%></td>
		    <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("CREATE_NAME"))%></td>
		    <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("ABNORMAL_TIME"))%></td>
		    <td class="en11px"><%="0".equals(status)?"������":("2".equals(status)?"������":("-1".equals(status)?"����δ��ʼ":""))%></td>
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