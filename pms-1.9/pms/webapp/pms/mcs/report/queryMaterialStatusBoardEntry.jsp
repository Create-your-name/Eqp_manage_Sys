<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>


<%
String equipmentDept = UtilFormatOut.checkNull(request.getParameter("deptIndex"));
String startDate = UtilFormatOut.checkNull(request.getParameter("startDate"));
String endDate = UtilFormatOut.checkNull(request.getParameter("endDate"));
String queryDate = UtilFormatOut.checkNull(request.getParameter("queryDate"));

String nullvalue = null;
List statusList = (List) request.getAttribute("statusList");
List materialStatusList = (List) request.getAttribute("materialStatusList");

if (queryDate=="") {
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	Date day = new Date();
	if (endDate == "") {
		endDate = df.format(day);
	}

	day.setDate(day.getDate()-7);
	if (startDate == "") {
		startDate = df.format(day);
	}
}

//�õ�����
String deptIndex;
if (UtilFormatOut.checkNull(request.getParameter("deptIndex"))=="") {
	deptIndex=(String )request.getAttribute("deptIndex");
} else {
	deptIndex=request.getParameter("deptIndex");
}
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

function turnto(url) {
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
    //equipmentCom.applyTo('equipmentId');
    //var equipmentId='';
    //equipmentDS.load({callback:function(){ equipmentCom.setValue(equipmentId); }});

    //״̬���ݳ�ʼ��
    var statusDS = new Ext.data.Store({
        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getJsonStatusList</ofbiz:url>'}),
    	reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'description'}, {name: 'status'}]))
    });

    statusCom = new Ext.form.ComboBox({
        store: statusDS,
        displayField:'description',
        valueField:'status',
        hiddenName:'status',
        typeAhead: true,
        mode: 'local',
        width: 150,
    	emptyText: 'Select a status...',
        triggerAction: 'all'
    });
    //statusCom.applyTo('status');

	/*var status = '<%=UtilFormatOut.checkNull(request.getParameter("status"))%>';
	var queryDate='<%=queryDate%>';

	if (queryDate == "2") {
        statusDS.load({callback:function(){ statusCom.setValue("STO-OUT"); }});
	} else if (queryDate == "7") {
	    statusDS.load({callback:function(){ statusCom.setValue("CABINET-NEW"); }});
	} else {
		statusDS.load({callback:function(){ statusCom.setValue(status); }});
	}*/

	//loadEquipment();
	function loadEquipment() {
		var val = equipmentDept.getValue();
		if (val == '<%=equipmentDept%>') {
			var equipmentId  = '<%=UtilFormatOut.checkNull(request.getParameter("equipmentId"))%>';
		} else {
			var equipmentId  = '';
		}
		equipmentDS.load({params:{equipmentDept:val},callback:function(){ equipmentCom.setValue(equipmentId); }});
	};
});
</script>

<%--����ת���ķ���--%>
<%--
select t1.mtr_grp,
       sum(decode(t2.seq_num,10000, countnum, null)) "COLUMN0",
       sum(decode(t2.seq_num,10001, t1.countnum, null))"COLUMN1",
       sum(decode(t2.seq_num,10002, t1.countnum, null))"COLUMN2",
       sum(decode(t2.seq_num,10003, countnum, null)) "COLUMN3",
       sum(decode(t2.seq_num,10004, t1.countnum, null))"COLUMN4",
       sum(decode(t2.seq_num,10005, t1.countnum, null))"COLUMN5",
       sum(decode(t2.seq_num,10006, countnum, null)) "COLUMN6",
       sum(decode(t2.seq_num,10007, t1.countnum, null))"COLUMN7",
       sum(decode(t2.seq_num,10008, t1.countnum, null))"COLUMN8",
       sum(decode(t2.seq_num,10009, countnum, null)) "COLUMN9",
       sum(decode(t2.seq_num,10010, t1.countnum, null))"COLUMN10",
       sum(decode(t2.seq_num,10011, t1.countnum, null))"COLUMN11",
       sum(decode(t2.seq_num,10012, t1.countnum, null)) "COLUMN12"
  from (select t.mtr_grp, t.status, count(*) countnum
          from mcs_material_status t
         group by t.mtr_grp, t.status) t1
,(select  status,description,seq_num from mcs_material_status_code order by seq_num) t2
where  t1.status = t2.status
 group by mtr_grp
--%>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>
<form name="MaterialForm" method="post">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>��ѯ����</legend>
    	<table width="100%" border="0" cellspacing="1" cellpadding="2">
		    <tr bgcolor="#DFE1EC" height="30">
	     		<tr bgcolor="#DFE1EC">
	      		 <td width="12%" bgcolor="#ACD5C9" class="en11pxb"><i18n:message key="mcs.doc_time" /><i18n:message key="mcs.data_required_red_star" /></td>
	      		 <td width="28%"><input type="text" ID="startDate" NAME="startDate" value="<%=startDate%>" readonly></td>
	   			 <td width="9%" bgcolor="#ACD5C9" class="en11pxb">��:</td>
	    		 <td width="28%"><input type="text" ID="endDate" NAME="endDate" value="<%=endDate%>"  readonly></td>
	    	</tr>

			<tr bgcolor="#DFE1EC" height="12">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.dept" /><i18n:message key="mcs.data_required_red_star" /></td>
				<td colspan="3">
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

				<!--<td class="en11pxb" bgcolor="#ACD5C9" width="12%">
					<i18n:message key="mcs.equipment" />
				</td>
				<td ><input type="text" size="20" name="equipmentId" autocomplete="off"/>
				</td>-->
			</tr>

			<tr bgcolor="#DFE1EC" height="30">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_group" /></td>
				<td><input type="text" size="20" name="mtrGrpSelect" autocomplete="off"/></td>
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.material_number" /></td>
				<td ><input type="text"  size="20" name="mtrNum"  value="<%=UtilFormatOut.checkNull(request.getParameter("mtrNum"))%>"/></td>
			</tr>

			<!--<tr bgcolor="#DFE1EC" height="30">
				<td class="en11pxb"  bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.current_status" /></td>
				<td colspan="3"><input type="text" size="20" name="status" autocomplete="off"/></td>
			</tr>-->
      </table>
	 </fieldset>
	</td>
  <tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryMaterialStatusBoardEntry</ofbiz:url>')"><span>&nbsp;��ѯ&nbsp;</span></a></li>
	</ul></td>

    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.MaterialForm.reset();"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>

    <!--<td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryMaterialStatusBoardEntry</ofbiz:url>?queryDate=2')"><span>&nbsp;2��δ�ݴ�&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryMaterialStatusBoardEntry</ofbiz:url>?queryDate=7')"><span>&nbsp;7��δʹ��&nbsp;</span></a></li>
	</ul></td>-->
  </tr>
</table>

<%--ע��ά����ʱ���Ǹ���materialIndex(��������)���й����ֶε�--%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset>
    <legend>����ʹ��״���б�</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9" height="13">
		<td class="en11pxb" align="center" width="10%" colspan="3">���Ϻ� \ ���� \ ����</td>
		<ofbiz:if name="statusList">
			<ofbiz:iterator name="cust" property="statusList">
				<td class="en11pxb"><ofbiz:inputvalue entityAttr="cust" field="description"/></td>
			</ofbiz:iterator>
		</ofbiz:if>
    	</tr>

		<%
		if(materialStatusList != null && materialStatusList.size() > 0) {
			for(Iterator it1 = materialStatusList.iterator();it1.hasNext();) {
				Map map1 = (Map)it1.next();
		%>

			<tr bgcolor="#DFE1EC">
			<td class="en11pxb"><%=map1.get("MTR_NUM")%></td>
			<td class="en11pxb">(<%=UtilFormatOut.checkNull((String) map1.get("MTR_DESC"))%>)</td>
			<td class="en11pxb"><%=UtilFormatOut.checkNull((String) map1.get("MOVING_AVERAGE_PRICE"))%></td>

			<%
			if (statusList != null && statusList.size() > 0) {
			    int i=0;
				for (Iterator it2 = statusList.iterator();it2.hasNext();) {
					Map map2 = (Map) it2.next();
					//2009-12-1 wanggq�����߼�,�������ϵ�״̬����Ϣ��Ҫ���ӵ����ϵ���ѯ
					if (i <= 1) {
						if ((String) map1.get("COLUMN"+i)==nullvalue) {%>
					  		<td class="en11px">&nbsp;</td>
						<%} else {%>
				  			<td class="en11px">
				  			    <A href="#" onclick="turnto('<ofbiz:url>/queryStoReqEntry?mtrNum=<%=map1.get("MTR_NUM")%>&status=<%=map2.get("status")%></ofbiz:url>')" ><%=map1.get("COLUMN"+i)%></A>
				  			</td>
		                <%
						}
					} else {
						if ((String) map1.get("COLUMN"+i)==nullvalue) { %>
					  		<td class="en11px">&nbsp;</td>
						<%} else {   %>

				  			<td class="en11px">
				  			    <A  href="#" onclick="turnto('<ofbiz:url>/queryMaterialStatusDetail?mtrNum=<%=map1.get("MTR_NUM")%>&status=<%=map2.get("status")%></ofbiz:url>')" ><%=map1.get("COLUMN"+i)%></A>
				  			</td>
		                <%
						    }
				        }
    				    i = i+1;
    			    }
	  	        }
	  	    }
		}
		%>
    </table>
    </fieldset></td>
  <tr>
</table>
</form>