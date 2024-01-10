<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>

<%
String startDate = UtilFormatOut.checkNull(request.getParameter("startDate"));
String endDate = UtilFormatOut.checkNull(request.getParameter("endDate"));

SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
Date day = new Date();
day.setDate(day.getDate()+30);
if (endDate == "") {
	endDate = df.format(day);
}

/*day.setDate(day.getDate()-7);
if (startDate == "") {
	startDate = df.format(day);
}*/

//�õ�����
String deptIndex = UtilFormatOut.checkNull(request.getParameter("deptIndex"));
if (deptIndex == "") {
	deptIndex = (String) request.getAttribute("deptIndex");
}

List materialStatusList = (List) request.getAttribute("materialStatusList");
%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function doSubmit(url) {
	if(Ext.getDom('deptIndex').value=='') {
		Ext.MessageBox.alert('����', '��ѡ����!');
		return;
	}
	loading();

	document.MaterialForm.action = url;
	document.MaterialForm.submit();
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

    // ��ʼ���豸�б�
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
		var val = equipmentDept.getValue();
		if (val == '<%=deptIndex%>') {
			var equipmentId  = '<%=UtilFormatOut.checkNull(request.getParameter("equipmentId"))%>';
		} else {
			var equipmentId  = '';
		}
		equipmentDS.load({params:{equipmentDept:val},callback:function(){ equipmentCom.setValue(equipmentId); }});
	};

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
	      		 <td width="12%" bgcolor="#ACD5C9" class="en11pxb">Ԥ�Ƹ�������</td>
	      		 <td width="28%"><input type="text" ID="startDate" NAME="startDate" value="<%=startDate%>"></td>
	   			 <td width="9%" bgcolor="#ACD5C9" class="en11pxb">��:</td>
	    		 <td width="28%"><input type="text" ID="endDate" NAME="endDate" value="<%=endDate%>"></td>
	    	</tr>

			<tr bgcolor="#DFE1EC" height="12">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.dept" /><i18n:message key="mcs.data_required_red_star" /></td>
				<td>
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
				</td>

				<td class="en11pxb" bgcolor="#ACD5C9" width="12%">�豸</td>
				<td>
				    <input type="text" size="20" name="equipmentId" autocomplete="off"/>
				</td>
			</tr>

			<tr bgcolor="#DFE1EC" height="30">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_group" /></td>
				<td><input type="text" size="20" name="mtrGrpSelect" autocomplete="off"/></td>
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_number" /></td>
				<td ><input type="text"  size="20" name="mtrNum"  value="<%=UtilFormatOut.checkNull(request.getParameter("mtrNum"))%>"/></td>
			</tr>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
		<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryUsePeriodEntry</ofbiz:url>')"><span>&nbsp;��ѯ&nbsp;</span></a></li>
	</ul></td>

    <td><ul class="button">
		<li><a class="button-text" href="javascript:document.MaterialForm.reset();"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset>
    <legend>ʹ�������б�</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr class="tabletitle">
    	    <td class="en11pxb">No.</td>
    	    <td class="en11pxb">�豸</td>
    		<td class="en11pxb">���Ϻ�</td>
    		<td class="en11pxb">����</td>
    		<td class="en11pxb">������</td>
    		<td class="en11pxb">ʹ����</td>
    		<td class="en11pxb">ʹ��ʱ��</td>
    		<td class="en11pxb">Ԥ�Ƹ�������</td>
    		<td class="en11pxb">��������(��)</td>
    		<td class="en11pxb">��ʹ��(��)</td>
    		<td class="en11pxb">ʣ��(��)</td>
    		<td class="en11pxb">����ʹ��</td>
    	</tr>

		<%
		int i = 1;
		if (materialStatusList != null && materialStatusList.size() > 0) {
			for(Iterator it = materialStatusList.iterator(); it.hasNext(); i++) {
				Map map = (Map) it.next();
    			String usePeriod = UtilFormatOut.checkNull((String) map.get("USE_PERIOD"));
    			String usableTimeLimit = UtilFormatOut.checkNull((String) map.get("USABLE_TIME_LIMIT"));
    			String materialStatusIndex = UtilFormatOut.checkNull((String) map.get("MATERIAL_STATUS_INDEX"));
    			String mrbDays = UtilFormatOut.checkNull((String) map.get("MRB_DAYS"));//����������
    			int expandUseDays = Integer.parseInt(usableTimeLimit) / 2;

    			double dusePeriod = 0;
    			if (!usePeriod.equals("")) {
    				dusePeriod = Double.parseDouble(usePeriod);
    			}

    			double dUsableTimeLimit = 0;
    			if (!usableTimeLimit.equals("")) {
    				dUsableTimeLimit = Double.parseDouble(usableTimeLimit);
    			}

                double dMrbDays = Double.parseDouble(mrbDays);
    			String alarmColor = "black";
    			if (dusePeriod+15 >= dUsableTimeLimit + dMrbDays) {
    			    alarmColor = "blue";//����15����������
    			}
    			if (dusePeriod >= dUsableTimeLimit + dMrbDays) {
    			    alarmColor = "red";//�ѳ���
    			}

		%>
    		<tr class="tablelist" style="color:<%=alarmColor%>">
    		    <td><%=i%></td>
    		    <td><%=UtilFormatOut.checkNull((String) map.get("USING_OBJECT_ID"))%></td>
    			<td><%=UtilFormatOut.checkNull((String) map.get("MTR_NUM"))%></td>
	            <td><%=UtilFormatOut.checkNull((String) map.get("MTR_DESC"))%></td>
	            <td><%=UtilFormatOut.checkNull((String) map.get("MTR_GRP"))%></td>
    			<td><%=UtilFormatOut.checkNull((String) map.get("ACCOUNT_NAME"))%></td>
    			<td><%=UtilFormatOut.checkNull((String) map.get("UPDATE_TIME"))%></td>
    			<td><%=UtilFormatOut.checkNull((String) map.get("FORCAST_CHANGE_DATE"))%></td>
    			<td><%=UtilFormatOut.checkNull((String) map.get("USABLE_TIME_LIMIT"))%></td>
                <td><%=UtilFormatOut.checkNull((String) map.get("USE_PERIOD"))%></td>
	        	<td><%=UtilFormatOut.checkNull((String) map.get("LEFT_DAYS"))%></td>
	        	<td>
	        	    <%if ("0".equals(mrbDays)) {%>
	        	        <ul class="button"><li><a class="button-text" href="<ofbiz:url>/expandUseDays?materialStatusIndex=<%=materialStatusIndex%>&expandUseDays=<%=expandUseDays%></ofbiz:url>">
	        	            <span>&nbsp;����<%=expandUseDays%>��&nbsp;</span>
	        	        </a></li></ul>
	        	    <%} else {%>
	        	        ������<%=mrbDays%>��
	        	    <%}%>
	        	</td>
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

<br>
<p>
	<font face="����" size="-1">
	    Tips:
	    <br>
	    1��<font color="red">��ɫ���ѳ��ڡ�</font><font color="blue">��ɫ��15���ڼ������ڡ�</font>
	    <br>
	    2���ڡ�������Ϣ�趨���˵� ά�������豸
		<a href="<ofbiz:url>/querySpecialUsableTimeEntry</ofbiz:url>">
		    ���������淶
		</a>
	</font>
</p>
<br>