<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="java.util.*"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>

<%
try {
	String eqpType = (String) request.getAttribute("eqpType");
    String isMsaDept = (String) request.getAttribute("isMsaDept");
    String holdPeriodIndex = UtilFormatOut.checkNull((String) request.getAttribute("holdPeriodIndex"));
%>

<!--include yui css-->
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	//���ݺϷ���У��
	function checkForm(){
        var periodName = Ext.get('periodName').dom.value;
        var defaultDays = Ext.get('defaultDays').dom.value;
        var standardHour = Ext.get('standardHour').dom.value;
        var warningDays = Ext.get('warningDays').dom.value;
        var timeRangeIndex = Ext.get('timeRangeIndex').dom.value;
        // ���� ��PROMIS�豸״̬�롱������PROMIS�豸״̬����У�飬huanghp,883609,2008.10.30
        var eqpStatus = Ext.get('eqpStatus').dom.value;
		var isUpdatePromis = Ext.get('isUpdatePromis').dom.value;

		if(periodName==""){
			return "�������Ʋ���Ϊ��";
		}
		var regex = /^MSA.*$/;
		var isMsaDept = <%=isMsaDept%>;
		if(isMsaDept==true && !regex.test(periodName)){
		    return "�����������Ʊ�����MSA��ͷ����";
		}

		if(defaultDays==""){
			return "������������Ϊ��";
		}

		if(standardHour==""){
			return "��׼��ʱ����Ϊ��";
		}

		if(warningDays==""){
			return "��ʾ��������Ϊ��";
		}

		if(!IsNumeric(defaultDays)){
			return "������������Ϊ����";
		}

		if(!IsNumeric(standardHour)){
			return "��׼��ʱ����Ϊ����";
		}

		if(!IsNumeric(warningDays)){
			return "��ʾ��������Ϊ����";
		}

		if((defaultDays == "1" || defaultDays == "2" || eqpStatus=="04-7D") && timeRangeIndex==""){
			return "�ձ�������ѡ����ʱ��";
		}

		// ���� ��PROMIS�豸״̬�롱������PROMIS�豸״̬����У�飬huanghp,883609,2008.10.30
		if(eqpStatus==""){
			return "MES�豸״̬�벻��Ϊ��";
		}
		if(isUpdatePromis==""){
			return "����MES�豸״̬����Ϊ��";
		}

		// ���� ��ʾ�����ͻ���������У�飬��������<=15,��ʾ����<=2;15<��������<=90,��ʾ����<=7;��������>90,��ʾ����<=30;huanghp,883609,2008.12.12
		// <10���ά��������ʮ��ά������+/-1�죻10-15���ά������ʮ�ա�˫�ܡ�����ά������+/-2�죻15-120���ά�������¶ȡ�˫�¡����ȡ�����ά������+/-7�죻>120�죨���ꡢ���ά������+/-30�졣20130318
		if(defaultDays<10 && warningDays>1)
		{
			return "��������<10,��ʾ��������<=1";
		}
		if(defaultDays>=10 && defaultDays<=15 && warningDays>2)
		{
			return "10<=��������<=15,��ʾ��������<=2";
		}
		if(defaultDays>15 && defaultDays<=120 && warningDays>7)
		{
			return "15<��������<=120,��ʾ��������<=7";
		}
		if(defaultDays>120 && warningDays>30)
		{
			return "��������>120,��ʾ��������<=30";
		}
		return "";
	}

	//�ı䵯�����С
	extDlg.dlgInit('500','550');

	//��������ҳ
	function addDefaultPeriod(obj){
		Ext.get('periodIndex').dom.value="";
		Ext.get('eqpType').dom.value=Ext.get('eqp_Type').dom.value;
		Ext.get('periodName').dom.value="";
		Ext.get('periodDesc').dom.value="";
		Ext.get('defaultDays').dom.value="";
		Ext.get('standardHour').dom.value="";
		Ext.get('eqpStatus').dom.value="";
		Ext.get('warningDays').dom.value="";
		Ext.get('isUpdatePromis').dom.value="";
		Ext.get('timeRangeIndex').dom.value="";
		Ext.get('enabled').dom.value="1";
		Ext.get('isContainSmallPm').dom.value="N";

		extDlg.showAddDialog(obj);
	}

	//�޸ĵ���ҳ
	function editDefaultPeriod(obj,periodIndex){
		Ext.get('periodIndex').dom.value=periodIndex;
		var url='<ofbiz:url>/queryDefaultPeriodByIndex</ofbiz:url>?periodIndex='+periodIndex;
		extDlg.showEditDialog(obj,url);
	}

	//ɾ������ҳ
	function delDefaultPeriod(obj,periodIndex){
        Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˼�¼��',function result(value){
	        if(value=="yes"){
				var eqpType=Ext.get('eqpType').dom.value;
				var url='<ofbiz:url>/delDefaultPeriod</ofbiz:url>?periodIndex='+periodIndex+"&eqpType=<%=eqpType%>";
				document.location=url;
			}else{
				return;
			}
        });
	}

	//�޸�ʱԶ�̵��óɹ����ҳ�渳ֵ
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('eqpType').dom.value=result.eqpType;
			Ext.get('periodName').dom.value=result.periodName;
			Ext.get('periodDesc').dom.value=result.periodDesc;
			Ext.get('defaultDays').dom.value=result.defaultDays;
			Ext.get('standardHour').dom.value=result.standardHour;
			Ext.get('eqpStatus').dom.value=result.eqpStatus;
			Ext.get('warningDays').dom.value=result.warningDays;
			Ext.get('isUpdatePromis').dom.value=result.isUpdatePromis;
			Ext.get('timeRangeIndex').dom.value=result.timeRangeIndex;
			Ext.get('enabled').dom.value=result.enabled;
			Ext.get('isContainSmallPm').dom.value=result.isContainSmallPm;
		}

		if(Ext.get('defaultDays').dom.value=="1" || Ext.get('defaultDays').dom.value=="2" || Ext.get('eqpStatus').dom.value=="04-7D")
		{
			Ext.get('timeRangeIndex').dom.disabled = false;
		}
		else
		{
			Ext.get('timeRangeIndex').dom.disabled = true;
		}
	}

	//��ѯ
	function defaultPeriodList() {
		if(Ext.getDom('eqp_Type').value=='') {
			Ext.MessageBox.alert('����', '��ѡ���豸���࣡');
			return;
		}
		loading();
		document.defaultPeriodForm.submit();
	}

	function changeDefaultDays()
	{
		if(Ext.get('defaultDays').dom.value=="1" || Ext.get('defaultDays').dom.value=="2" || Ext.get('eqpStatus').dom.value=="04-7D")
		{
			Ext.get('timeRangeIndex').dom.disabled = false;
		}
		else
		{
			Ext.get('timeRangeIndex').dom.disabled = true;
		}
	}

	Ext.onReady(function(){
	    var eqpType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'eqp_Type',
	        width:170,
	        forceSelection:true
	    });
	});

	function setupHold(holdPeriodIndex, holdFlag) {
		if(holdFlag == 1 && Ext.getDom('holdCodeReason').value == '') {
			Ext.MessageBox.alert('����', '����Hold��:Holdԭ����Ϊ�գ�����ȡ���������ɾ��Hold��');
			return;
		}

		loading();
		document.holdForm.action = '<ofbiz:url>/defaultPeriodHold</ofbiz:url>?holdPeriodIndex='+holdPeriodIndex+'&holdFlag='+holdFlag+'&eqp_Type=<%=eqpType%>';
		document.holdForm.submit();
	}
</script>

<form name="defaultPeriodForm" method="post" action="<%=request.getContextPath()%>/control/defaultPeriodList">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>��ѯ��������</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC" height="30">
            <td class="en11pxb" width="10%">�豸����</td>
            <td>
         	    <select id="eqp_Type" name="eqp_Type">
          			<option value=''></option>
	          		<ofbiz:if name="equipmentTypeList">
		        		<ofbiz:iterator name="equipmentType" property="equipmentTypeList">
			    			<option value='<ofbiz:inputvalue entityAttr="equipmentType" field="equipmentType"/>'>
			    			    <ofbiz:inputvalue entityAttr="equipmentType" field="equipmentType"/>
			    			</option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
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
	 	<td><ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:defaultPeriodList();"><span>&nbsp;��ѯ&nbsp;</span></a></li>
			</ul>
		</td>
	</tr>
</table>
</form>

<ofbiz:if name="flag" value="OK">
<div id="defaultPeriodList" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="defaultPeriodList" style="visibility:'hidden';">
</ofbiz:unless>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�趨��������</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addDefaultPeriod(this);"/></td>
          <td width="14%" class="en11pxb">��������</td>
          <td width="10%" class="en11pxb">˵��</td>
          <td width="5%" class="en11pxb">��������</td>
          <td width="5%" class="en11pxb">��׼��ʱ</td>
          <td width="5%" class="en11pxb">��ʾ����</td>
          <td width="5%" class="en11pxb">MES�豸״̬��</td>
          <td width="5%" class="en11pxb">����MES�豸״̬</td>
          <td width="5%" class="en11pxb">����ʱ��</td>
          <td class="en11pxb">�Ƿ�����</td>
          <td class="en11pxb">���°���ά����Ŀ</td>

          <%if (Constants.CALL_TP_FLAG) {%>
              <td class="en11pxb">GUI��鲽���Զ�Hold</td>
          <%}%>
        </tr>
        <ofbiz:if name="defaultPeriodList">
	        <ofbiz:iterator name="defaultPeriod" property="defaultPeriodList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="center">
		            <img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delDefaultPeriod(this,'<ofbiz:inputvalue entityAttr="defaultPeriod" field="periodIndex"/>');"/>
		          </td>

		          <td width="14%" class="en11px">
		            <a href="#" onclick="editDefaultPeriod(this,'<ofbiz:inputvalue entityAttr="defaultPeriod" field="periodIndex"/>')">
		                <ofbiz:entityfield attribute="defaultPeriod" field="periodName"/>
		            </a>
		          </td>

		          <td width="10%" class="en11px"><ofbiz:entityfield attribute="defaultPeriod" field="periodDesc"/></td>
		          <td width="9%" class="en11px"><ofbiz:entityfield attribute="defaultPeriod" field="defaultDays"/></td>
		          <td width="9%" class="en11px"><ofbiz:entityfield attribute="defaultPeriod" field="standardHour"/></td>
		          <td width="9%" class="en11px"><ofbiz:entityfield attribute="defaultPeriod" field="warningDays"/></td>
		          <td width="11%" class="en11px"><ofbiz:entityfield attribute="defaultPeriod" field="eqpStatus"/></td>
		          <td width="11%" class="en11px"><ofbiz:entityfield attribute="defaultPeriod" field="isUpdatePromis"/></td>
	       		  <%
	       			GenericValue period = (GenericValue)pageContext.findAttribute("defaultPeriod");
					if (period.getString("timeRangeIndex")==null){
	       		  %>
	       		    <td width="11%" class="en11px"></td>
	       		  <%
	       		  	}else{
	       		  		List timeRangeList = (List)request.getAttribute("timeRangeList");
	       		  		for (int i=0;i<timeRangeList.size();i++)
	       		  		{
	       		  			GenericValue timeRange = (GenericValue)timeRangeList.get(i);
	       		  			if (timeRange.getString("rangeIndex").equals(period.getString("timeRangeIndex")))
	       		  			{
	       		  %>
	       		  			<td width="11%" class="en11px"><%=timeRange.getString("timeRange")%></td>
	       		  <%
	       		  			}
	       		  		}
	       		  	}
	       		  %>
	       		  <td class="en11px">
	       		  	<%
	       		  		if ("1".equals(period.getString("enabled"))){
	       		  			out.println("Y");
	       		  		}else{
	       		  			out.println("N");
	       		  		}
	       		  	%>
	       		  </td>

	       		  <td class="en11px"><ofbiz:entityfield attribute="defaultPeriod" field="isContainSmallPm"/></td>

                  <%if (Constants.CALL_TP_FLAG) {%>
	       		      <td class="en11px">
	       		          <a href='<ofbiz:url>/defaultPeriodList?holdPeriodIndex=<ofbiz:inputvalue entityAttr="defaultPeriod" field="periodIndex"/>&eqp_Type=<%=eqpType%></ofbiz:url>'>
	       		            ����<ofbiz:inputvalue entityAttr="defaultPeriod" field="holdCode"/>
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
//fab1����������ʾhold����
if (Constants.CALL_TP_FLAG && !"".equals(holdPeriodIndex)) {
    long holdLotNum = 0;
    String immediateHold = "Y";
    String triggerStage = "";
	String holdCode = "";
	String holdDesc = "";

    GenericValue holdPeriod = (GenericValue) request.getAttribute("holdPeriod");
    holdCode = holdPeriod.getString("holdCode");
    if (holdCode != null) {
        holdLotNum = holdPeriod.getLong("holdLotNum").longValue();
        immediateHold = holdPeriod.getString("immediateHold");
		triggerStage = UtilFormatOut.checkNull(holdPeriod.getString("triggerStage"));
		holdDesc = UtilFormatOut.checkNull(holdPeriod.getString("holdDesc"));
    }
%>

<br>
<form name="holdForm" method="post" action="<%=request.getContextPath()%>/control/defaultPeriodList">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>GUI��鲽���Զ�hold�����������趨��<%=holdPeriod.getString("periodName")%>��</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
            <td width="20%" class="en11pxb">Hold��:Holdԭ��</td>
            <td width="10%" class="en11pxb">Hold��������</td>
            <td width="20%" class="en11pxb">�Ƿ�վ����Hold</td>
			<td width="30%" class="en11pxb">����Stage(����AL1-PCK-1A������������stage)</td>
            <td class="en11pxb">��ע</td>
        </tr>

        <tr bgcolor="#DFE1EC">
            <td class="en11px">
                <select id="holdCodeReason" name="holdCodeReason">
              		<option value=''>δ����</option>
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
    	          	<option value='Y' <%if ("Y".equals(immediateHold)) {%>selected<%}%>>Y:��վ����Hold</option>
    	          	<option value='N' <%if ("N".equals(immediateHold)) {%>selected<%}%>>N:�����鲽��Hold</option>
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
			    <li><a class="button-text" href="#" onclick="javascript:setupHold(<%=holdPeriodIndex%>, 1);"><span>&nbsp;����Hold&nbsp;</span></a></li>
			</ul>
		</td>
		<td>
	 	    <ul class="button">
			    <li><a class="button-text" href="#" onclick="javascript:setupHold(<%=holdPeriodIndex%>, 0);"><span>&nbsp;ɾ��Hold&nbsp;</span></a></li>
			</ul>
		</td>
	</tr>
</table>
</form>
<%}%>

<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">�趨��������</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="�豸��������">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/manageDefaultPeriod" method="post" id="eq" onsubmit="return false;">
                <input id="periodIndex" type="hidden" name="periodIndex" value="" />
                <input id="eqpType" type="hidden" name="eqpType" value="" />
                <p>
                <label for="name"><small>��������</small></label>
                <input class="textinput" type="text" name="periodName" id="periodName" value="" size="22" tabindex="1" />
                </p>
                <p>
                <label for="name"><small>˵��</small></label>
                <input class="textinput" type="text" name="periodDesc" id="periodDesc" value="" size="22" tabindex="1" />
                </p>
                <p>
                <label for="name"><small>��������</small></label>
                <input class="textinput" type="text" name="defaultDays" id="defaultDays" value="" size="22" tabindex="1" onchange="return changeDefaultDays(this);" />
                </p>
                <p>
                <label for="name"><small>��׼��ʱ</small></label>
                <input class="textinput" type="text" name="standardHour" id="standardHour" value="" size="22" tabindex="1" />
                </p>
                <p>
                <label for="name"><small>��ʾ����</small></label>
                <input class="textinput" type="text" name="warningDays" id="warningDays" value="" size="22" tabindex="1" />
                </p>
                <p>
                <label for="name"><small>MES�豸״̬��</small></label>
          		<select id="eqpStatus" name="eqpStatus" class="select">
          			<option value=''></option>
	          		<ofbiz:if name="promisEqpStatusList">
		        		<ofbiz:iterator name="promisEqpStatus" property="promisEqpStatusList">
			    			<option value='<ofbiz:inputvalue entityAttr="promisEqpStatus" field="eqpStatus"/>'><ofbiz:inputvalue entityAttr="promisEqpStatus" field="eqpStatus"/></option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
    			</select>
                </p>
                <p>
                <label for="name"><small>����MES�豸״̬</small></label>
                <select name = "isUpdatePromis" id="isUpdatePromis" class="select">
                	<option value='Y'>Y</option>
                	<option value='N'>N</option>
       			</select>
                </p>
                <p>
                <label for="name"><small>�ձ�������ʱ��</small></label>
                <select name = "timeRangeIndex" id="timeRangeIndex" class="select" disabled >
          			<option value=''></option>
	          		<ofbiz:if name="timeRangeList">
		        		<ofbiz:iterator name="timeRange" property="timeRangeList">
			    			<option value='<ofbiz:inputvalue entityAttr="timeRange" field="rangeIndex"/>'><ofbiz:inputvalue entityAttr="timeRange" field="timeRange"/></option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
       			</select>
                </p>
                <p>
                <label for="name"><small>�Ƿ�����</small></label>
                <select name = "enabled" id="enabled" class="select" >
          			<option value='1'>Y</option>
          			<option value='0'>N</option>
       			</select>
                </p>
                <p>
                <label for="name"><small>�Ƿ����°���ά����Ŀ(Y�Զ�ɾ������������Ա����ƻ�: ���翪ʼ��ά��ʱ����ɾ�������¡��ܺ���ά���ƻ�)</small></label>
                <select name = "isContainSmallPm" id="isContainSmallPm" class="select" >
          			<option value='N'>N</option>
          			<option value='Y'>Y</option>
       			</select>
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
	<ofbiz:if name="flag" value="OK">
		var obj=document.getElementById('eqp_Type');
		obj.value='<%=eqpType%>'
	</ofbiz:if>
</script>

<%
} catch (Exception e) {
    out.println("Error:" + e);
}
%>