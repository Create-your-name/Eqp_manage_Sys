<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>

<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="java.util.*"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<% 
	String equ=(String)request.getAttribute("equType");
	String reasonType=(String)request.getAttribute("reasonType");
	String equipmentType = (String) request.getAttribute("equipmentType");
	String holdReasonIndex = UtilFormatOut.checkNull((String) request.getAttribute("holdReasonIndex"));
%>

<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	//数据合法性校验
	function checkForm(){
        var description = Ext.get('reason').dom.value;
		if(description==""){
			return "原因不能为空";
		}
		return "";
	}
	
	//改变弹出框大小width,height
	extDlg.dlgInit('500','300');
	
	//新增功能弹出页
	function addReason(obj){
		Ext.get('reasonIndex').dom.value="";
		Ext.get('equipType').dom.value='<%=equ%>';
		Ext.get('reType').dom.value='<%=reasonType%>';
		Ext.get('reason').dom.value="";
		extDlg.showAddDialog(obj);
	}
	
	//修改功能弹出页
	function editReason(obj,reasonIndex){
		Ext.get('reasonIndex').dom.value=reasonIndex;
		Ext.get('equipType').dom.value='<%=equ%>';
		Ext.get('reType').dom.value='<%=reasonType%>';
		var url='<ofbiz:url>/queryReasonByIndex</ofbiz:url>?reasonIndex='+reasonIndex;
		extDlg.showEditDialog(obj,url);
	}
	
	//删除
	function delReason(reasonIndex){
        Ext.MessageBox.confirm('删除确认', '您确信要删除此记录吗？',function result(value){
	        if(value=="yes"){
				var equipType=Ext.get('equipmentType').dom.value;
				var reasonType=Ext.get('reasonType').dom.value;
				var url='<ofbiz:url>/delReasonByIndex</ofbiz:url>?reasonIndex='+reasonIndex+"&equipType=<%=equ%>&reType=<%=reasonType%>";
				document.location=url;
			}else{
				return;
			}
        });
	}
	
	//修改时远程调用成功后给页面赋值
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('reason').dom.value=result.reason;
		}
	}
	
	function setupHold(holdReasonIndex, holdFlag) {
		if(holdFlag == 1 && Ext.getDom('holdCodeReason').value == '') {
			Ext.MessageBox.alert('警告', '设置Hold码:Hold原因不能为空，如需取消设置请点删除Hold！');
			return;
		}

		loading();
		document.holdForm.action = '<ofbiz:url>/pmsReasonHold</ofbiz:url>?holdReasonIndex='+holdReasonIndex+'&holdFlag='+holdFlag+'&equipmentType=<%=equ%>'+'&reasonType=<%=reasonType%>';
		document.holdForm.submit();
	}
	
	
	//查询
	function reasonQuery(){
		if(Ext.get('equipmentType').dom.value==""){
			Ext.MessageBox.alert('警告', '请选择设备大类!');
			return;
		}
		epuipmentForm.submit();
	}
	
	Ext.onReady(function(){
	    var eqpType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'equipmentType',
	        width:170,
	        forceSelection:true
	    });
	    
	    var reasonType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'reasonType',
	        width:100,
	        forceSelection:true
	    });
	});
</script>

<form action="<%=request.getContextPath()%>/control/reasonList" method="post" id="epuipmentForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>选择设备大类</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC"> 
          <td width="12%" class="en11pxb">设备大类:</td>
          <td width="28%">
          	<select id="equipmentType" name="equipmentType">
	          <option value=""></option>
	          <ofbiz:if name="equipMentList">
		        <ofbiz:iterator name="cust" property="equipMentList">
			    <option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentType"/>'><ofbiz:inputvalue entityAttr="cust" field="equipmentType"/></option>
		      </ofbiz:iterator>
	      	</ofbiz:if>
    		</select>
      </td>
      <td width="15%" class="en11pxb">原因类型:</td>
      <td width="25%"><select id="reasonType" name="reasonType">
      	<option value="ABNORMAL">异常</option>
      	<option value="OVERTIME">超时</option>
      </select>
      </td>
      <td width="20%" class="en11pxb" align="left">
      	<table border="0" cellspacing="0" cellpadding="0">
		  <tr height="30">
		   	<td width="20">&nbsp;</td>
		    <td><ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:reasonQuery();"><span>&nbsp;确定&nbsp;</span></a></li> 
			</ul></td>
		  </tr>
		</table>
      </td>
        </tr>
      </table>
      </fieldset></td>
  </tr>
</table>
</form>
<br>
<ofbiz:if name="flag" value="ok">
<div id="pmsReasonList" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="pmsReasonList" style="visibility:'hidden';">
</ofbiz:unless>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>PMS原因（异常、超时）</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addReason(this);"/></td>
          <td width="50%" class="en11pxb">原因</td>
             <%if (Constants.CALL_TP_FLAG) {%>
              <td width="45%" class="en11pxb">GUI检查步骤自动Hold</td>
          <%}%>
        </tr>
        <ofbiz:if name="pmsReasonList">
	        <ofbiz:iterator name="cust" property="pmsReasonList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delReason('<ofbiz:inputvalue entityAttr="cust" field="reasonIndex"/>');"/></td>
		          <td width="50%" class="en11px"><a href="#" onclick="editReason(this,'<ofbiz:inputvalue entityAttr="cust" field="reasonIndex"/>')"><ofbiz:entityfield attribute="cust" field="reason"/></a></td>		          	

                  <%if (Constants.CALL_TP_FLAG) {%>
	       		      <td width="45%" class="en11px">
	       		          <a href='<ofbiz:url>/pmsReasonList?holdReasonIndex=<ofbiz:inputvalue entityAttr="cust" field="reasonIndex"/>&equipmentType=<%=equ%>&reasonType=<%=reasonType%></ofbiz:url>'>
	       		            设置<ofbiz:inputvalue entityAttr="cust" field="holdCode"/>
	       		          </a>
	       		      </td>
	       		  <%}%>
		        </tr>
	      </ofbiz:iterator>
      </ofbiz:if>
      </table>
      </fieldset></td>
  </tr>
</table>
</div>

<%
//fab1保养周期显示hold设置
if (Constants.CALL_TP_FLAG && !"".equals(holdReasonIndex)) {

    long holdLotNum = 0;
    String immediateHold = "Y";
    String triggerStage = "";
	String holdCode = "";
	String holdDesc = "";

    GenericValue holdReason = (GenericValue) request.getAttribute("holdReason");
    holdCode = holdReason.getString("holdCode");
    if (holdCode != null) {
        holdLotNum = holdReason.getLong("holdLotNum").longValue();
        immediateHold = holdReason.getString("immediateHold");
        triggerStage = UtilFormatOut.checkNull(holdReason.getString("triggerStage"));
        holdDesc = UtilFormatOut.checkNull(holdReason.getString("holdDesc"));
    }
%>

<br>
<form name="holdForm" method="post" action="<%=request.getContextPath()%>/control/pmsReasonList">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>GUI检查步骤自动hold（保养周期设定：<%=holdReason.getString("reason")%>）</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
            <td width="20%" class="en11pxb">Hold码:Hold原因</td>
            <td width="10%" class="en11pxb">Hold批次数量</td>
            <td width="20%" class="en11pxb">是否当站立即Hold</td>
            <td width="30%" class="en11pxb">触发Stage(例如AL1-PCK-1A，留空则不限制stage)</td>
            <td class="en11pxb">备注</td>
        </tr>

        <tr bgcolor="#DFE1EC">
            <td class="en11px">
                <select id="holdCodeReason" name="holdCodeReason">
              		<option value=''>未设置</option>
    	          	<%
    	          	List holdCodeReasonList = (List) request.getAttribute("holdCodeReasonList");
    	          	if (holdCodeReasonList != null && holdCodeReasonList.size() > 0) {
           				for(Iterator it = holdCodeReasonList.iterator();it.hasNext();) {
    					Map map = (Map) it.next();
    					String holdCode1 = (String) map.get("HOLD_CODE");
    					String holdReason1 = (String) map.get("HOLD_CODE_DESC");
    				%>
    					<option value='<%=holdCode1%>:<%=holdReason1%>' <%if (holdCode1.equals(holdCode)) {%>selected<%}%>>
    					    <%=holdCode1%>:<%=holdReason1%>
    					</option>
    				<%
    				    }
    				}
    				%>
    	      	</select>
            </td>

            <td class="en11px">
                <select id="holdLotNum" name="holdLotNum">
    	          	<%
           			for(int i = 1; i <= 20; i++) {
    				%>
    					<option value='<%=i%>' <%if (i == holdLotNum) {%>selected<%}%>><%=i%></option>
    				<%
    				}
    				%>
				  </select>
            </td>

            <td class="en11px">
                <select id="immediateHold" name="immediateHold">
    	          	<option value='Y' <%if ("Y".equals(immediateHold)) {%>selected<%}%>>Y:当站立即Hold</option>
    	          	<option value='N' <%if ("N".equals(immediateHold)) {%>selected<%}%>>N:最近检查步骤Hold</option>
    	      	</select>
            </td>

            <td class="en11px"><input type="text" class="input" ID="triggerStage" NAME="triggerStage" value="<%=triggerStage%>"></td>
            <td class="en11px"><input type="text" class="input" ID="holdDesc" NAME="holdDesc" value="<%=holdDesc%>"></td>
        </tr>

      </table>
      </fieldset></td>
  </tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
	<tr height="30">
	 	<td>
	 	    <ul class="button">
			    <li><a class="button-text" href="#" onclick="javascript:setupHold(<%=holdReasonIndex%>, 1);"><span>&nbsp;设置Hold&nbsp;</span></a></li>
			</ul>
		</td>
		<td>
	 	    <ul class="button">
			    <li><a class="button-text" href="#" onclick="javascript:setupHold(<%=holdReasonIndex%>, 0);"><span>&nbsp;删除Hold&nbsp;</span></a></li>
			</ul>
		</td>
	</tr>
</table>
</form>
<%}%>

<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">PMS原因（异常、超时）</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="PMS原因（异常、超时）">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/reason" method="post" id="reasonForm" onsubmit="return false;">
                	<input id="reasonIndex" type="hidden" name="reasonIndex" value="" />
                	<input id="equipType" type="hidden" name="equipType" value="" />
                	<input id="test" type="hidden" name="test" value="test" />
                	<input id="reType" type="hidden" name="reType" value="" />
                <p>
                <label for="name"><small>原因</small></label>
                <input class="textinput" type="text" name="reason" id="reason" value="" size="22" tabindex="1" />
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
<script language="javascript">
	<ofbiz:if name="flag" value="ok">
		var obj=document.getElementById('equipmentType');
		obj.value='<%=equ%>'
		var obj=document.getElementById('reasonType');
		obj.value='<%=reasonType%>'
	</ofbiz:if>
</script>