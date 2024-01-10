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
if (endDate == "") {
	endDate = df.format(day);
}

day.setDate(day.getDate()-7);
if (startDate == "") {
	startDate = df.format(day);
}

//得到部门
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
		Ext.MessageBox.alert('警告', '请选择部门!');
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

    //给select控件(aa)应用样式
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
	      		<td width="12%" bgcolor="#ACD5C9" class="en11pxb">报废日期<i18n:message key="mcs.data_required_red_star" /></td>
	      		<td width="28%"><input type="text" ID="startDate" NAME="startDate" value="<%=startDate%>" readonly></td>
	   			<td width="9%" bgcolor="#ACD5C9" class="en11pxb">到:</td>
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
		<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryStoCabinetEntry</ofbiz:url>')"><span>&nbsp;查询&nbsp;</span></a></li>
	</ul></td>

    <td><ul class="button">
		<li><a class="button-text" href="javascript:document.MaterialForm.reset();"><span>&nbsp;重置&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<%--注意维护的时候是更具materialIndex(物料索引)进行关联字段的--%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset>
    <legend>物料使用状况列表</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
    	<tr class="tabletitle">
    	    <td class="en11pxb" rowspan="2">No.</td>
    		<td class="en11pxb" rowspan="2">物料号</td>
    		<td class="en11pxb" rowspan="2">描述</td>
    		<td class="en11pxb" rowspan="2">均价</td>
    		<td class="en11pxb" colspan="3">当前库存</td>
    		<td class="en11pxb" rowspan="2">当前使用</td>
    		<td class="en11pxb" rowspan="2">期间报废</td>
    	</tr>

    	<tr class="tabletitle">
    		<td class="en11pxb">新领库存</td>
    		<td class="en11pxb">放回暂存</td>
    		<td class="en11pxb">待维修清洗</td>
    	</tr>

		<%
		int i = 1;
		if (materialStatusList != null && materialStatusList.size() > 0) {
			for(Iterator it1 = materialStatusList.iterator();it1.hasNext(); i++) {
				Map map1 = (Map) it1.next();
				int c1 = Integer.parseInt((String) map1.get("COLUMN1"));//新领未暂存
				int c2 = Integer.parseInt((String) map1.get("COLUMN2"));//新进暂存区
				int c3 = Integer.parseInt((String) map1.get("COLUMN3"));//放回暂存区

				int c5 = Integer.parseInt((String) map1.get("COLUMN5"));//使用中

				int c7 = Integer.parseInt((String) map1.get("COLUMN7"));//内部维修
				int c8 = Integer.parseInt((String) map1.get("COLUMN8"));//送外待维修

				//期间报废
				int c11 = Integer.parseInt((String) map1.get("COLUMN11"));
				int c12 = Integer.parseInt((String) map1.get("COLUMN12"));
				int c13 = Integer.parseInt((String) map1.get("COLUMN13"));
				int c14 = Integer.parseInt((String) map1.get("COLUMN14"));
				int c15 = Integer.parseInt((String) map1.get("COLUMN15"));

				int c16 = Integer.parseInt((String) map1.get("COLUMN16"));//自主清洗
				int c17 = Integer.parseInt((String) map1.get("COLUMN17"));//送外待清洗
				int c19 = Integer.parseInt((String) map1.get("COLUMN19"));//已清洗
				int c20 = Integer.parseInt((String) map1.get("COLUMN20"));//已维修

				int qtyStoCabinet = c1 + c2 + c3;    //当前库存
			    int qtyEqp = c5;    //使用中
			    int qtyScrap = c11 + c12 + c13 + c14 + c15;    //已报废
		%>
    		<tr class="tablelist">
    		    <td><%=i%></td>
    			<td class="en11pxb"><%=map1.get("MTR_NUM")%></td>
    			<td class="en11pxb"><%=UtilFormatOut.checkNull((String) map1.get("MTR_DESC"))%></td>
    			<td class="en11pxb"><%=UtilFormatOut.checkNull((String) map1.get("MOVING_AVERAGE_PRICE"))%></td>
                <td><%=c1+c2%></td>
                <td><%=c3+c19+c20%></td>
                <td><%=c7+c8+c16+c17%></td>
    	  	    <td><%=qtyEqp%></td>
    	  	    <td><%=qtyScrap%></td>
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
	<font color="#FF0000" face="黑体" size="-1">
		Tips :

		<br>
		&nbsp;1. 放回暂存区 = 使用后放回 + 已清洗 + 已维修

		<br>
		&nbsp;2. 待维修清洗 = 内部维修 + 送外待维修 + 自主清洗 + 送外待清洗
	</font>
</p>