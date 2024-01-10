<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.mcs.webapp.util.ConstantsMcs"%>

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

String queryType = (String) request.getAttribute("queryType");

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
    	    <tr>
    		<td class="tabletitle" width="12%">
    		    <i18n:message key="mcs.type" />
    		    <i18n:message key="mcs.data_required_red_star" />
    		</td>

			<td class="tablelist" width="30%" colspan="3">
			    <input type="radio" id="type1" name="queryType" value="REPAIR" checked>维修
                <input type="radio" id="type2" name="queryType" value="WASH" <%if ("WASH".equals(queryType)) {%>checked<%}%>>清洗
			</td>
	    </tr>

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
		<li><a class="button-text" href="javascript:doSubmit('<ofbiz:url>/queryRepairWashEntry</ofbiz:url>')"><span>&nbsp;查询&nbsp;</span></a></li>
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
    	    <td class="en11pxb">No.</td>
    		<td class="en11pxb">物料号</td>
    		<td class="en11pxb">描述</td>
    		<td class="en11pxb">均价</td>

    		<%if ("REPAIR".equals(queryType)) {%>
        		<td class="en11pxb">已维修</td>
        		<td class="en11pxb">内部维修</td>
        		<td class="en11pxb">送外待维修</td>
        		<td class="en11pxb">送外维修中</td>
        	<%} else if ("WASH".equals(queryType)) {%>
        	    <td class="en11pxb">已清洗</td>
        		<td class="en11pxb">自主清洗</td>
        		<td class="en11pxb">送外待清洗</td>
        		<td class="en11pxb">送外清洗中</td>
        	<%}%>

    		<td class="en11pxb">当前使用</td>
    		<td class="en11pxb">期间报废</td>
    		<td class="en11pxb">合计</td>
    	</tr>

		<%
		int i = 1;
		if (materialStatusList != null && materialStatusList.size() > 0) {
			for(Iterator it1 = materialStatusList.iterator();it1.hasNext(); i++) {
				Map map1 = (Map) it1.next();
				int c5 = Integer.parseInt((String) map1.get("COLUMN5"));//使用中

				int c7 = Integer.parseInt((String) map1.get("COLUMN7"));//内部维修
				int c8 = Integer.parseInt((String) map1.get("COLUMN8"));//送外待维修
				int c9 = Integer.parseInt((String) map1.get("COLUMN9"));//送外维修中

				//期间报废
				int c11 = Integer.parseInt((String) map1.get("COLUMN11"));
				int c12 = Integer.parseInt((String) map1.get("COLUMN12"));
				int c13 = Integer.parseInt((String) map1.get("COLUMN13"));
				int c14 = Integer.parseInt((String) map1.get("COLUMN14"));
				int c15 = Integer.parseInt((String) map1.get("COLUMN15"));

				int c16 = Integer.parseInt((String) map1.get("COLUMN16"));//自主清洗
				int c17 = Integer.parseInt((String) map1.get("COLUMN17"));//送外待清洗
				int c18 = Integer.parseInt((String) map1.get("COLUMN18"));//送外清洗中
				int c19 = Integer.parseInt((String) map1.get("COLUMN19"));//已清洗
				int c20 = Integer.parseInt((String) map1.get("COLUMN20"));//已维修

			    int qtyRecycle = c20;   //已维修
			    int qtyFab = c7;        //内部维修
			    int qtyOpt = c8;        //送外待维修
			    int qtyLeader = c9;     //送外维修中
			    String oldStatusRecycle = ConstantsMcs.CABINET_RECYCLE_REPAIR;
			    String oldStatusFab = ConstantsMcs.FAB_REPAIR;
			    String oldStatusOpt = ConstantsMcs.VENDOR_REPAIR_OPT;
			    String oldStatusLeader = ConstantsMcs.VENDOR_REPAIR_LEADER;

			    if ("WASH".equals(queryType)) {
			        qtyRecycle = c19;    //已清洗
    			    qtyFab = c16;        //自主清洗
    			    qtyOpt = c17;        //送外待清洗
    			    qtyLeader = c18;     //送外清洗中
    			    oldStatusRecycle = ConstantsMcs.CABINET_RECYCLE_WASH;
    			    oldStatusFab = ConstantsMcs.FAB_WASH;
    			    oldStatusOpt = ConstantsMcs.VENDOR_WASH_OPT;
    			    oldStatusLeader = ConstantsMcs.VENDOR_WASH_LEADER;
			    }

			    int qtyEqp = c5;    //使用中
			    int qtyScrap = c11 + c12 + c13 + c14 + c15;    //已报废

			    String url1 = "/fabRepairEntry?mtrNum=" + map1.get("MTR_NUM") + "&oldStatus=";
		%>
    		<tr class="tablelist">
    		    <td><%=i%></td>
    			<td class="en11pxb"><%=map1.get("MTR_NUM")%></td>
    			<td class="en11pxb"><%=UtilFormatOut.checkNull((String) map1.get("MTR_DESC"))%></td>
    			<td class="en11pxb"><%=UtilFormatOut.checkNull((String) map1.get("MOVING_AVERAGE_PRICE"))%></td>

                <td>
                    <a href="#" onclick="turnto('<ofbiz:url><%=url1+oldStatusRecycle%></ofbiz:url>')" >
                        <%=qtyRecycle%>
                    </a>
                </td>
                <td>
                    <a href="#" onclick="turnto('<ofbiz:url><%=url1+oldStatusFab%></ofbiz:url>')" >
                        <%=qtyFab%>
                    </a>
                </td>
                <td>
                    <a href="#" onclick="turnto('<ofbiz:url><%=url1+oldStatusOpt%></ofbiz:url>')" >
                        <%=qtyOpt%>
                    </a>
                </td>
                <td>
                    <a href="#" onclick="turnto('<ofbiz:url><%=url1+oldStatusLeader%></ofbiz:url>')" >
                        <%=qtyLeader%>
                    </a>
                </td>

    	  	    <td>
    	  	        <a href="#" onclick="turnto('<ofbiz:url>/queryMaterialStatusDetail?mtrNum=<%=map1.get("MTR_NUM")%>&status=USING&startDate=2000-01-01</ofbiz:url>')" >
    	  	            <%=qtyEqp%>
    	  	        </a>
    	  	    </td>

    	  	    <td><%=qtyScrap%></td>

    	  	    <td><%=qtyRecycle+qtyFab+qtyOpt+qtyLeader+qtyEqp+qtyScrap%></td>
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
