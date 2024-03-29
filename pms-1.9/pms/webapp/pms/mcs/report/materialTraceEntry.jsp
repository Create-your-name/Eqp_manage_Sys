<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

<%
List materialStatusList = (List) request.getAttribute("materialStatusList");

String startDate = UtilFormatOut.checkNull(request.getParameter("startDate"));
String endDate = UtilFormatOut.checkNull(request.getParameter("endDate"));

SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
Date today = new Date();

if (endDate.equals("")) {
	endDate=df.format(today);
}

Date d = new Date();
int n = -7;
d.setDate(d.getDate()+n);

if (startDate.equals("")) {
	startDate=df.format(d);
}

//得到部门
String deptIndex = UtilFormatOut.checkNull(request.getParameter("deptIndex"));
if (deptIndex.equals("")) {
	deptIndex = (String) request.getAttribute("deptIndex");
}
%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
function doSubmit(url) {
//	if(Ext.getDom('deptIndex').value=='') {
//		Ext.MessageBox.alert('警告', '请选部门!');
//		return;
//	}
	//loading();
	document.MaterialForm.action = url;
	document.MaterialForm.submit();
}

function viewStatusHist(materialStatusIndex) {
    var url = "<ofbiz:url>/queryMaterialStatusHist</ofbiz:url>?materialStatusIndex=" + materialStatusIndex;
    var result = window.showModalDialog(url, window, "dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");
}

<!-- ##################################### yui page script ################################ -->
Ext.onReady(function(){
    //格式化日期
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

    //给select控件(deptIndex)应用样式
    equipmentDept = new Ext.form.ComboBox({
        typeAhead: true,
        triggerAction: 'all',
        transform:'deptIndex',
        width:150,
    	emptyText: 'Select a equipmentDept...',
        forceSelection:true
    });
    equipmentDept.setValue("<%=deptIndex%>");

// 初始化物料组
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
    <td><fieldset><legend>查询条件</legend>
    	<table width="100%" border="0" cellspacing="1" cellpadding="2">
		    <tr bgcolor="#DFE1EC" height="30">
	      		 <td width="12%" bgcolor="#ACD5C9" class="en11pxb">领用日期:</td>
	      		 <td width="28%"><input type="text" ID="startDate" NAME="startDate" value="<%=startDate%>" readonly></td>
	   			 <td width="9%" bgcolor="#ACD5C9" class="en11pxb">到:</td>
	    		 <td width="28%"><input type="text" ID="endDate" NAME="endDate" value="<%=endDate%>"  readonly></td>
	    	</tr>

			<tr bgcolor="#DFE1EC" height="12">
				<td class="en11pxb" bgcolor="#ACD5C9" width="12%"><i18n:message key="mcs.dept" /></td>
				<td  colspan="3">
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
			<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/materialTraceEntry</ofbiz:url>')"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.MaterialForm.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>物料追踪列表</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr bgcolor="#ACD5C9" height="13">
    	    <td class="en11pxb" rowspan="2"><i18n:message key="mcs.index_alias_name" /></td>
    		<td class="en11pxb" rowspan="2" align="center"><i18n:message key="mcs.material_number" /></td>
    		<td class="en11pxb" rowspan="2" align="center"><i18n:message key="mcs.material_description" /></td>
    		<td class="en11pxb" rowspan="2" align="center">领用</td>
    		<td class="en11pxb" colspan="2" align="center">暂存</td>
    		<td class="en11pxb" colspan="2" align="center">恒温</td>
    		<td class="en11pxb" align="center" colspan="4">使用</td>
    		<td class="en11pxb" rowspan="2" align="center">用完</td>
    		<td class="en11pxb" rowspan="2" align="center">在线报废</td>
    	</tr>
    	<tr bgcolor="#ACD5C9" height="13">
        	<td class="en11pxb" align="center">时间</td>
        	<td class="en11pxb" align="center">操作者</td>
        	<td class="en11pxb" align="center">时间</td>
        	<td class="en11pxb" align="center">操作者</td>
            <td class="en11pxb" align="center">操作时间</td>
            <td class="en11pxb" align="center">操作者</td>
            <td class="en11pxb" align="center">设备</td>
            <td class="en11pxb" align="center">备注</td>
        </tr>

		<%
		if(materialStatusList != null && materialStatusList.size() > 0) {
			for(Iterator it1 = materialStatusList.iterator();it1.hasNext();) {
			    Map map1 = (Map) it1.next();
		%>
		        <tr class="tablelist">
    		        <td class="en11px">
    	        	    <a href="javascript:viewStatusHist('<%=map1.get("MATERIAL_STATUS_INDEX")%>')">
    	        	        <%=map1.get("ALIAS_NAME")%>
    	        	    </a>
    	        	</td>

        			<td class="en11px"><%=UtilFormatOut.checkNull((String) map1.get("MTR_NUM"))%></td>
        			<td class="en11px"><%=UtilFormatOut.checkNull((String) map1.get("MTR_DESC"))%></td>
        			<td class="en11px"><%=UtilFormatOut.checkNull((String) map1.get("DOC_TIME"))%></td>
        			<td class="en11px"><%=UtilFormatOut.checkNull((String) map1.get("CABINET_TIME"))%></td>
        			<td class="en11px"><%=UtilFormatOut.checkNull((String) map1.get("CABINET_TRANS_BY"))%></td>
        			<td class="en11px"><%=UtilFormatOut.checkNull((String) map1.get("UNFROZEN_TRANS_TIME"))%></td>
        			<td class="en11px"><%=UtilFormatOut.checkNull((String) map1.get("UNFROZEN_TRANS_BY"))%></td>
        			<td class="en11px"><%=UtilFormatOut.checkNull((String) map1.get("USING_TIME"))%></td>
        			<td class="en11px"><%=UtilFormatOut.checkNull((String) map1.get("USING_TRANS_BY"))%></td>
        			<td class="en11px"><%=UtilFormatOut.checkNull((String) map1.get("USING_OBJECT_ID"))%></td>
        			<td class="en11px"><%=UtilFormatOut.checkNull((String) map1.get("USING_NOTE"))%></td>
        			<td class="en11px"><%=UtilFormatOut.checkNull((String) map1.get("FINISH_TIME"))%></td>
        			<td class="en11px"><%=UtilFormatOut.checkNull((String) map1.get("SCRAP_TIME"))%></td>
    		    </tr>
		<%
		    }
		}
		%>

    </table>
	</fieldset>
	</td>
  <tr>
</table>

</form>