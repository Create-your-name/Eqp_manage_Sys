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
					<legend>��ѯ����</legend>
					<table width="100%" border="0" cellspacing="1" cellpadding="2">
						<tr bgcolor="#DFE1EC" height="30">
							<td class="en11pxb" bgcolor="#ACD5C9" width="12%">
								<i18n:message key="mcs.dept" />
							</td>
							<td>
								<select id="deptSelect" name="deptSelect">
										<option value='all'>ALL</option>
                                        <option value='10001'>����һ��</option>
                                        <option value='10002'>���̶���</option>
                                        <option value='10003'>�������첿</option>
                                        <option value='10004'>������֤��</option>
                                        <option value='10005'>�������ϲ�</option>
								</select>
							</td>
							<td class="en11pxb" bgcolor="#ACD5C9" width="12%">�·�
							</td>
							<td>
								<select id="queryMonth" name="queryMonth">
									<option value='all'></option>
									<option value='1'>һ��</option>
									<option value='2'>����</option>
									<option value='3'>����</option>
									<option value='4'>����</option>
									<option value='5'>����</option>
									<option value='6'>����</option>
									<option value='7'>����</option>
									<option value='8'>����</option>
									<option value='9'>����</option>
									<option value='10'>ʮ��</option>
									<option value='11'>ʮһ��</option>
									<option value='12'>ʮ����</option>
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
					<li><a class="button-text" href="javascript:query();"><span>&nbsp;��ѯ&nbsp;</span></a></li>
				</ul>
			</td>
		</tr>
	</table>

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>
				<fieldset>
					<legend>�ؼ����������ܿ�</legend>
					<table width="100%" border="0" cellspacing="1" cellpadding="2">
						<tr bgcolor="#ACD5C9">
							<td class="en11pxb">����</td>
							<td class="en11pxb">�豸</td>
							<td class="en11pxb">Model</td>
							<td class="en11pxb">���Ϻ�</td>
							<td class="en11pxb">��������</td>
							<td class="en11pxb">��ʼʹ��ʱ��</td>
							<td class="en11pxb">��������</td>
							<td class="en11pxb">�趨����</td>
							<td class="en11pxb">ʵ������</td>
							<td class="en11pxb">�Ƿ�ػ�</td>
							<td class="en11pxb">������</td>
							<td class="en11pxb">����δ����</td>
							<td class="en11pxb">�����ܿ�״̬</td>
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
							<td class="en11px">�����ܿ�������</td>
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
							<td class="en11px">�ܿ��쳣������</td>
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
							<td class="en11px">ͳ�ƣ�</td>
							<td class="en11px"><%=percent%></td>
						</tr>
					</table>
				</fieldset>
			</td>
		<tr>
	</table>
</form>