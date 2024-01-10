<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>


<%
String startDate=UtilFormatOut.checkNull(request.getParameter("startDate"));
String endDate=UtilFormatOut.checkNull(request.getParameter("endDate"));

List materialStatusList = (List) request.getAttribute("materialStatusList");

String equipmentDept = UtilFormatOut.checkNull(request.getParameter("deptIndex"));
//nowdate = new Timestamp(System.currentTimeMillis());


Date date = new Date();
SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");

if ( UtilFormatOut.checkNull(request.getParameter("endDate"))=="" ) {
endDate=df.format(date);
}

Date d= new Date();
int n = -7;//or   1
d.setDate(d.getDate()+n);

SimpleDateFormat df2=new SimpleDateFormat("yyyy-MM-dd");
if ( UtilFormatOut.checkNull(request.getParameter("startDate"))=="" ) {
startDate=df2.format(d);
}

//�õ�����
String deptIndex;
if (UtilFormatOut.checkNull(request.getParameter("deptIndex"))=="") {
	deptIndex=(String )request.getAttribute("deptIndex");
	//out.print("123");
} else {
    //out.print("456");
	deptIndex=request.getParameter("deptIndex");
}

String mtrNum = UtilFormatOut.checkNull(request.getParameter("mtrNum"));
boolean isSog = false;
if (mtrNum.length() >= 9) {
	if (ConstantsMcs.SOG_PREFIX.equals(mtrNum.substring(0,9))) {
		isSog = true;
	}
}
%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function doSubmit(url) {
//	if(Ext.getDom('deptIndex').value=='') {
//		Ext.MessageBox.alert('����', '��ѡ����!');
//		return;
//	}
	//loading();
	document.MaterialForm.action = url;
	document.MaterialForm.submit();
}

function turnto(url) {
	document.MaterialForm.action = url;
	document.MaterialForm.submit();
}

function viewStatusHist(materialStatusIndex) {
    var url = "<ofbiz:url>/queryMaterialStatusHist</ofbiz:url>?materialStatusIndex=" + materialStatusIndex;
    var result = window.showModalDialog(url, window, "dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");
}

<!-- ##################################### yui page script ################################ -->
Ext.onReady(function(){
   //��ʽ������
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

    //��select�ؼ�(aa)Ӧ����ʽ
	equipmentDept = new Ext.form.ComboBox({
        typeAhead: true,
        triggerAction: 'all',
        transform:'deptIndex',
        width:150,
		emptyText: 'Select a equipmentDept...',
        forceSelection:true
    });
	equipmentDept.on('select',loadEquipment);
    equipmentDept.setValue("<%=deptIndex%>");

    // ��ʼ��������
	var mtrGrpDS = new Ext.data.Store({
	    proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonCommonMtrGrp</ofbiz:url>'}),
	    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'mtrGrp'}, {name: 'mtrGrpDesc'}]))
	});

	var mtrGrpCom = new Ext.form.ComboBox({
	    store: mtrGrpDS,
	    displayField: 'mtrGrpDesc',
	    valueField: 'mtrGrp',
	    hiddenName: 'mtrGrp',
	    typeAhead: true,
	    mode: 'local',
	    width: 150,
	    triggerAction: 'all',
	    emptyText: 'Select a group...',
	    allowBlank: true
	});
	mtrGrpCom.applyTo('mtrGrpSelect');

	var mtrGrp = '<%=UtilFormatOut.checkNull(request.getParameter("mtrGrp"))%>';
	mtrGrpDS.load({callback:function(){ mtrGrpCom.setValue(mtrGrp); }});

    //��ʼ�豸�б�
	var equipmentDS = new Ext.data.Store({
		proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonEquipmentList</ofbiz:url>'}),
		reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
	});

	var equipmentCom = new Ext.form.ComboBox({
		store: equipmentDS,
		displayField:'equipmentId',
		valueField:'equipmentId',
		hiddenName:'equipmentId',
		typeAhead: true,
		mode: 'local',
		width: 150,
		emptyText: 'Select a eqpid...',
		triggerAction: 'all'
	});
	equipmentCom.applyTo('equipmentId');
    var equipmentId='';
	equipmentDS.load({callback:function(){ equipmentCom.setValue(equipmentId); }});

	loadEquipment();
	function loadEquipment() {
	    //alert("1");
		var val = equipmentDept.getValue();
		if (val == '<%=equipmentDept%>') {
			var equipmentId  = '<%=UtilFormatOut.checkNull(request.getParameter("equipmentId"))%>';
		} else {
			var equipmentId  = '';
		}
		//alert("2");
		equipmentDS.load({params:{equipmentDept:val},callback:function(){ equipmentCom.setValue(equipmentId); }});
		//alert("3");
	};
});
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form name="MaterialForm" method="post">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>��ѯ����</legend>
    	<table width="100%" border="0" cellspacing="1" cellpadding="2">
		    <tr bgcolor="#DFE1EC" height="30">
	     		<tr bgcolor="#DFE1EC">
	      		 <td width="12%" bgcolor="#ACD5C9" class="en11pxb">ʹ������:</td>
	      		 <td width="28%"><input type="text" ID="startDate" NAME="startDate" value="<%=startDate%>" readonly size="22"></td>
	   			 <td width="9%" bgcolor="#ACD5C9" class="en11pxb">��:</td>
	    		 <td width="28%"><input type="text" ID="endDate" NAME="endDate" value="<%=endDate%>"  readonly size="22"></td>
	    	</tr>
			<tr bgcolor="#DFE1EC" height="12">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.dept" /></td>
				<td nowrap="nowrap" >
					<select id="deptIndex" name="deptIndex">
						<option value=''></option>
						<ofbiz:if name="deptList">
							<ofbiz:iterator name="cust" property="deptList">
								<option value='<ofbiz:inputvalue entityAttr="cust" field="deptIndex"/>'>
									<ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>
								</option>

							</ofbiz:iterator>
						</ofbiz:if>
					</select>
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%">
					<i18n:message key="mcs.equipment" />
				</td>
				<td ><input type="text" size="20" name="equipmentId" autocomplete="off"/>
				</td>
			</tr>
			<tr bgcolor="#DFE1EC" height="30">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_group" /></td>
				<td><input type="text" size="20" name="mtrGrpSelect" autocomplete="off"/></td>
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_number" /></td>
				<td ><input type="text"  size="19" name="mtrNum"  value="<%=UtilFormatOut.checkNull(request.getParameter("mtrNum"))%>"/></td>
			</tr>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryUsingHistoryByEqptEntry</ofbiz:url>')"><span>&nbsp;��ѯ&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.MaterialForm.reset();"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<%--ע��ά����ʱ���Ǹ���materialIndex(��������)���й����ֶε�--%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset>
    <legend>��ʷʹ���б�</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9" height="13">
    	    <td class="en11pxb"><i18n:message key="mcs.index_alias_name" /></td>
        	<td class="en11pxb" align="center"><i18n:message key="mcs.equipment" /></td>
    		<td class="en11pxb" align="center"><i18n:message key="mcs.material_number" /></td>
    		<td class="en11pxb" align="center"><i18n:message key="mcs.material_description" /></td>
    		<td class="en11pxb" align="center"><i18n:message key="mcs.operator" /></td>
			<%if (isSog) {%>
			<td class="en11pxb" align="center">���¿�ʼʱ��</td>
			<%}%>
    		<td class="en11pxb" align="center">ʹ��ʱ��</td>
    		<td class="en11pxb" align="center"><i18n:message key="mcs.current_status" /></td>
    	</tr>

		<%
		if(materialStatusList != null && materialStatusList.size() > 0) {
			for(Iterator it1 = materialStatusList.iterator();it1.hasNext();) {
			    Map map1 = (Map)it1.next();
		%>
		        <tr class="tablelist">
    		        <td class="en11px">
    	        	    <a href="javascript:viewStatusHist('<%=map1.get("MATERIAL_STATUS_INDEX")%>')">
    	        	        <%=map1.get("ALIAS_NAME")%>
    	        	    </a>
    	        	</td>

        			<td class="en11px"><%=map1.get("USING_OBJECT_ID")%></td>
        			<td class="en11px"><%=map1.get("MTR_NUM")%></td>
        			<td class="en11px"><%=map1.get("MTR_DESC")%></td>
        			<td class="en11px"><%=map1.get("TRANS_BY")%></td>
					<%if (isSog) {%>
        			<td class="en11px"><%=map1.get("UNFROZEN_START_TIME")%></td>
					<%}%>
        			<td class="en11px"><%=map1.get("UPDATE_TIME")%></td>
        			<td class="en11px"><%=map1.get("DESCRIPTION")%></td>
    		    </tr>
		<%
		    }
		}
		%>

    </table>
    </fieldset></td>
  <tr>
</table>

</form>