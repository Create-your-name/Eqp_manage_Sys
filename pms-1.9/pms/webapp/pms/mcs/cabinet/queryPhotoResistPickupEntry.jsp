<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='i18n' prefix='i18n' %>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import ="com.csmc.pms.webapp.util.*"%>


<%@ include file="../../pms/yui-ext/ext-comdlg.jsp"%>

<!-- ##################################### submit script ################################ -->
<script language="javascript">
	//改变弹出框大小width,height
	extDlg.dlgInit('500','300');

	//修改功能弹出页
	function editPrSafeQty(obj, mtrNum) {
		Ext.get('mtrNum').dom.value = mtrNum;
		var url='<ofbiz:url>/getJsonPrSafeQty</ofbiz:url>?mtrNum='+mtrNum;
		extDlg.showEditDialog(obj,url);
	}

	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('mtrNum').dom.value=result.mtrNum;
			Ext.get('safeQty').dom.value=result.safeQty;
		}
	}

	//数据合法性校验
	function checkForm(){
		var mtrNum = Ext.get('mtrNum').dom.value;
		var safeQty = Ext.get('safeQty').dom.value;

		if(mtrNum==""){
			return "请选择物料号";
		}
		if(safeQty==""){
			return "安全库存不可以为空";
		}

		return "";
	}
</script>

<!-- ##################################### html content ################################ -->
<i18n:bundle baseName="mcs_resource" id="bundle"/>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><fieldset>
		<legend>光刻胶领用查询</legend>
			<table id="objTable1" width="100%" border="0" cellspacing="1" cellpadding="2">
				<tr class="tabletitle">
					<td width="10%" class="en11pxb"><i18n:message key="mcs.material_number" /></td>
					<td width="30%" class="en11pxb"><i18n:message key="mcs.material_description" /></td>
					<td class="en11pxb">建议领用（瓶）=安全-暂存</td>
					<td class="en11pxb">暂存数量（瓶）</td>
					<td class="en11pxb">安全库存（瓶）</td>
					<td class="en11pxb">最长恒温时间（天）</td>
				</tr>

				<%
                List list = (List) request.getAttribute("photoResistPickupList");
                if(list != null && list.size() > 0) {
            	    for(Iterator it = list.iterator();it.hasNext();) {
        				Map map = (Map) it.next();
        				String shelfLifeTime = UtilFormatOut.checkNull((String) map.get("SHELF_LIFE_TIME"));

        				double dShelfLifeTime = 0;
        				if (!shelfLifeTime.equals("")) {
        					dShelfLifeTime = Double.parseDouble(shelfLifeTime);
        				}
                %>
				<tr class="tablelist" <%if (dShelfLifeTime <= 10) {%>style="color:red"<%}%>>
					<td><%=UtilFormatOut.checkNull((String) map.get("MTR_NUM"))%></td>
	        	    <td><%=UtilFormatOut.checkNull((String) map.get("MTR_DESC"))%></td>
					<td><%=UtilFormatOut.checkNull((String) map.get("REQ_QTY"))%></td>
	        	    <td><%=UtilFormatOut.checkNull((String) map.get("CABINET_QTY"))%></td>
					<td class="en11px">
					    <a href="#" onclick="editPrSafeQty(this,'<%=UtilFormatOut.checkNull((String) map.get("MTR_NUM"))%>')">
					        <%=UtilFormatOut.checkNull((String) map.get("SAFE_QTY"))%>
					    </a>
					</td>
					<td><%=UtilFormatOut.checkNull((String) map.get("UNFROZEN_TIME"))%></td>
				</tr>
				<%
        	        }
        	    }
        	    %>
			</table>
		</fieldset></td>
	</tr>
</table>

<br>
<p>
	<font color="#FF0000" face="黑体" size="-1">
		Tips :
		<br>1、晚班根据建议领用申请数量，早班领用。
		<br>2、领用时间14:00以后，18：00以前。
		<br>3、安全库存一般默认前一天数量，特殊情况领用人（班长、作业长）可做适当修改。
		<br>4、暂存区如果存在距有效期小于10天的光刻胶，则整条记录为红色。
		<br>*5、Durimide 7510 Polyimide（料号20100061502）查看实物领用。
		<br>*6、每天领用胶量必须全部录入条码于暂存区，予保证建议领用数量的准确性。
	</font>
</p>

<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><fieldset>
		<legend>参考领用量</legend>
			<table width="100%" border="0" cellspacing="1" cellpadding="2">
				<tr class="tabletitle">
					<td width="10%" class="en11pxb" rowspan="2"><i18n:message key="mcs.material_number" /></td>
					<td width="30%" class="en11pxb" rowspan="2"><i18n:message key="mcs.material_description" /></td>
					<td class="en11pxb" colspan="2">500K</td>
					<td class="en11pxb" colspan="2">600K</td>
					<td class="en11pxb" colspan="2">700K</td>
					<td class="en11pxb" colspan="2">800K</td>
				</tr>

				<tr class="tabletitle">
					<td class="en11pxb">用量/天</td>
					<td class="en11pxb">库存量</td>
					<td class="en11pxb">用量/天</td>
					<td class="en11pxb">库存量</td>
					<td class="en11pxb">用量/天</td>
					<td class="en11pxb">库存量</td>
					<td class="en11pxb">用量/天</td>
					<td class="en11pxb">库存量</td>
				</tr>

				<ofbiz:if name="prRefList" size="0">
				<ofbiz:iterator name="cust" property="prRefList">
				<tr class="tablelist">
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="mtrNum" /></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="mtrDesc" /></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="refDaily500k" /></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="refStock500k" /></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="refDaily600k" /></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="refStock600k" /></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="refDaily700k" /></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="refStock700k" /></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="refDaily800k" /></td>
					<td class="en11px"><ofbiz:entityfield attribute="cust" field="refStock800k" /></td>
				</tr>
				</ofbiz:iterator>
				</ofbiz:if>
			</table>
		</fieldset></td>
	</tr>
</table>

<%
List guiPriv = (List) request.getSession().getAttribute(SessionNames.GUI_PRIV_LIST_KEY);
if (guiPriv.contains("PHOTORESIST_PICKUP_EDIT")) {
%>
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">修改 安全库存（瓶）</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="安全库存（瓶）">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/managePrSafeQty" method="post" id="manageForm" onsubmit="return false;">

                <p>
                <label for="name"><small><i18n:message key="mcs.material_number" /></small><big><i18n:message key="mcs.data_required_red_star" /></big></label>
				<input class="textinput" type="text" name="mtrNum" id="mtrNum" value="" size="22" tabindex="1" readonly />

                <label for="name"><small>安全库存</small><big><i18n:message key="mcs.data_required_red_star" /></big></label>
                <input class="textinput" type="text" name="safeQty" id="safeQty" value="" size="22" tabindex="2" />
                </p>
                </form>
            </div>
        </div>
    </div>
    <div class="x-dlg-ft">
        <div id="dlg-msg">
            <span id="post-error" class="posting-msg"><img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" />&nbsp;<span id="post-error-msg"></span></span>
        </div>
    </div>
</div>
<%}%>