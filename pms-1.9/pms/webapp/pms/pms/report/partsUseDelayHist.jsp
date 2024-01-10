<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.entity.GenericDelegator"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ taglib uri='i18n' prefix='i18n' %>

<% 
	String deptIndex = UtilFormatOut.checkNull((String)request.getAttribute("deptIndex"));
	String queryMonth = UtilFormatOut.checkNull((String)request.getAttribute("queryMonth"));
	String kpuNum = UtilFormatOut.checkNull((String)request.getAttribute("kpuNum"));
	String overdueNum = UtilFormatOut.checkNull((String)request.getAttribute("overdueNum"));
	String percent = UtilFormatOut.checkNull((String)request.getAttribute("percent"));
%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
	Ext.onReady(function(){
		var deptSelect = new Ext.form.ComboBox({
			typeAhead: true,
			triggerAction: 'all',
			transform:'deptSelect',
			width:150,
			emptyText: 'Select a cost center...',
			forceSelection:true
		});
		deptSelect.setValue("<%=deptIndex%>");

		var queryMonth = new Ext.form.ComboBox({
			typeAhead: true,
			triggerAction: 'all',
			transform:'queryMonth',
			width:150,
			emptyText: 'Select a cost center...',
			forceSelection:true
		});
		queryMonth.setValue("<%=queryMonth%>");
	});

	function query() {
		document.queryForm.submit();
	}
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle" />
<form name="queryForm" method="post" action="<%=request.getContextPath()%>/control/queryKeyPartUseOverdueHist">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>
				<fieldset>
					<legend>查询条件</legend>
					<table width="100%" border="0" cellspacing="1" cellpadding="2">
						<tr bgcolor="#DFE1EC" height="30">
							<td class="en11pxb" bgcolor="#ACD5C9" width="12%">
								<i18n:message key="mcs.dept" />
							</td>
							<td>
								<select id="deptSelect" name="deptSelect">
										<option value='all'>ALL</option>
                                        <option value='10001'>工程一部</option>
                                        <option value='10002'>工程二部</option>
                                        <option value='10003'>生产制造部</option>
                                        <option value='10004'>质量保证部</option>
                                        <option value='10005'>动力保障部</option>
								</select>
							</td>
							<td class="en11pxb" bgcolor="#ACD5C9" width="12%">月份
							</td>
							<td>
								<select id="queryMonth" name="queryMonth">
									<option value='all'></option>
									<option value='1'>一月</option>
									<option value='2'>二月</option>
									<option value='3'>三月</option>
									<option value='4'>四月</option>
									<option value='5'>五月</option>
									<option value='6'>六月</option>
									<option value='7'>七月</option>
									<option value='8'>八月</option>
									<option value='9'>九月</option>
									<option value='10'>十月</option>
									<option value='11'>十一月</option>
									<option value='12'>十二月</option>
								</select>
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		<tr>
	</table>

	<table border="0" cellspacing="0" cellpadding="0">
		<tr height="30">
			<td>
				<ul class="button">
					<li><a class="button-text" href="javascript:query();"><span>&nbsp;查询&nbsp;</span></a></li>
				</ul>
			</td>
		</tr>
	</table>

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>
				<fieldset>
					<legend>关键备件寿命管控</legend>
					<table width="100%" border="0" cellspacing="1" cellpadding="2">
						<tr bgcolor="#ACD5C9">
							<td class="en11pxb">部门</td>
							<td class="en11pxb">设备</td>
							<td class="en11pxb">Model</td>
							<td class="en11pxb">物料号</td>
							<td class="en11pxb">物料名称</td>
							<td class="en11pxb">开始使用时间</td>
							<td class="en11pxb">寿命类型</td>
							<td class="en11pxb">设定寿命</td>
							<td class="en11pxb">实际寿命</td>
							<td class="en11pxb">是否必换</td>
							<td class="en11pxb">超寿命</td>
							<td class="en11pxb">超期未评估</td>
							<td class="en11pxb">寿命管控状态</td>
						</tr>
						<ofbiz:if name="delayHistList">
							<ofbiz:iterator name="cust" property="delayHistList" type="java.util.Map">
								<tr bgcolor="#DFE1EC">
									<td class="en11px">
										<ofbiz:entityfield attribute="cust" field="DEPT" />
									</td>
									<td class="en11px">
										<ofbiz:entityfield attribute="cust" field="EQP_ID" />
									</td>
									<td class="en11px">
										<ofbiz:entityfield attribute="cust" field="EQP_MODEL" />
									</td>
									<td class="en11px">
										<ofbiz:entityfield attribute="cust" field="PART_ID" />
									</td>
									<td class="en11px">
										<ofbiz:entityfield attribute="cust" field="PART_NAME" />
									</td>
									<td class="en11px">
										<ofbiz:entityfield attribute="cust" field="START_USING_TIME" />
									</td>
									<td class="en11px">
										<ofbiz:entityfield attribute="cust" field="LIFE_TYPE" />
									</td>
									<td class="en11px">
										<ofbiz:entityfield attribute="cust" field="PARTS_LIFE" />
									</td>
									<td class="en11px">
										<ofbiz:entityfield attribute="cust" field="ACTUAL_LIFE" />
									</td>
									<td class="en11px">
										<ofbiz:entityfield attribute="cust" field="IS_MUST_CHANGE" />
									</td>
									<td class="en11px">
										<ofbiz:entityfield attribute="cust" field="IS_OVERDUE" />
									</td>
									<td class="en11px">
										<ofbiz:entityfield attribute="cust" field="IS_DELAY" />
									</td>
									<td class="en11px">
										<ofbiz:entityfield attribute="cust" field="LIFE_CONTROL" />
									</td>
							</ofbiz:iterator>
						</ofbiz:if>
						<tr bgcolor="#DFE1EC">
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
						</tr>
						<tr bgcolor="#DFE1EC">
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px">寿命管控总数：</td>
							<td class="en11px"><%=kpuNum%></td>
						</tr>
						<tr bgcolor="#DFE1EC">
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px">管控异常数量：</td>
							<td class="en11px"><%=overdueNum%></td>
						</tr>
						<tr bgcolor="#DFE1EC">
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px"></td>
							<td class="en11px">统计：</td>
							<td class="en11px"><%=percent%></td>
						</tr>
					</table>
				</fieldset>
			</td>
		<tr>
	</table>
</form>