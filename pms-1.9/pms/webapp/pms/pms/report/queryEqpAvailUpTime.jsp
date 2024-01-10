<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Calendar" %>

<%
	String sDate = request.getParameter("startDate");

	if (null == sDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		sDate = formatter.format(Calendar.getInstance().getTime());
	}

%>
<script lanaguage="javascript">

	function doSubmit(url) {
		if(document.eqpForm.startDate.value=="") {
			alert("��ѡ��ʼ���ڣ�");
			return;
		}

		if(document.eqpForm.endDate.value=="") {
			alert("��ѡ��������ڣ�");
			return;
		}

		if(document.eqpForm.eqpIdSelected.options.length==0) {
		    var objFromSelect = document.eqpForm.eqpId;
		    for(var i=0;i<objFromSelect.options.length;i++) {
			    objFromSelect.options[i].selected = true;
			}

			doAddEqp();
		}

		for(var i=0;i<document.eqpForm.eqpIdSelected.options.length;i++) {
			document.eqpForm.eqpIdSelected.options[i].selected = true;
		}

		loading();

		document.eqpForm.action = url;
		document.eqpForm.submit();
	}

	function doAddEqp() {
		if(document.eqpForm.eqpId.options.length==0)
		{
			alert("��ѡ���豸���࣡");
			return;
		}

		moveOption(document.eqpForm.eqpId,document.eqpForm.eqpIdSelected)
	}

	function doRemoveEqp() {
		if(document.eqpForm.eqpIdSelected.options.length==0)
		{
			alert("��ѡ���豸��");
			return;
		}
	    moveOption(document.eqpForm.eqpIdSelected,document.eqpForm.eqpId)
	}

	function moveOption(objFromSelect,objToSelect) {
		/*var objOption = document.createElement("option");
		objOption.text = objFromSelect.options[objFromSelect.selectedIndex].text;
		objOption.value = objFromSelect.options[objFromSelect.selectedIndex].value;
		objToSelect.options.add(objOption);

		objFromSelect.options.remove(objFromSelect.selectedIndex);*/
		for(var i=0;i<objFromSelect.options.length;i++) {
			if (objFromSelect.options[i].selected) {
				var e = objFromSelect.options[i];
				objToSelect.options.add(new Option(e.text, e.value));
				objFromSelect.remove(i);
				i = i - 1;
			}
		}
	}

    function clearEqp(eqpSelectObj) {
	    for(var i=0;i<eqpSelectObj.options.length;i++) {
			eqpSelectObj.remove(i);
			i = i - 1;
		}
	}
	var oldEqpValue="";

	//�豸����ѡ��
	function eqpTypeChange(){
		var newEqpValue=Ext.get('equipmentType').dom.value;
		var actionURL='<ofbiz:url>/getEqpIdByType</ofbiz:url>?equipmentType='+newEqpValue;
		if(oldEqpValue!=newEqpValue){
				Ext.lib.Ajax.formRequest('eqpForm',actionURL,{success: commentSuccess, failure: commentFailure});
		}
		oldEqpValue=newEqpValue;
	}

	//Զ�̵��óɹ�
	function commentSuccess(o){
	    //clear all eqpid options
        clearEqp(document.getElementById("eqpIdSelected"));
        clearEqp(document.getElementById("eqpId"));

		var result = eval('(' + o.responseText + ')');
		if (result!=null & result!=""){
		    //�豸ID���ݳ�ʼ��
		    var equipId=document.getElementById("eqpId");
			var eqpArray=result.eqpIdArray;
			var eqpIdSize=result.eqpIdArray.length;
			equipId.length=0;
			for(var i=0;i<eqpIdSize;i++){
				equipId.options[equipId.length]=new Option(eqpArray[i],eqpArray[i]);
			}
		}
	}

	//Զ�̵���ʧ��
   var commentFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
   };

   //��ʼ��ҳ��ؼ�
	Ext.onReady(function(){
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

	    var eqpType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'equipmentType',
	        width:170,
	        forceSelection:true
	    });
	    eqpType.on('select',eqpTypeChange);

	    var period = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'period',
	        width:170,
	        forceSelection:true
	    });
	 });

</script>


<form name="eqpForm" method="POST">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <fieldset><legend>��ѯ��̨Available/Uptime Chart</legend>

      	<table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">
			<tr bgcolor='#DFE1EC'>
				<td width="10%" class="en11pxb">�豸����</td>
				<td align="left">
					<select name="equipmentType" id="equipmentType">
					  <option value=""></option>
			          <ofbiz:if name="eqpTypeList">
				        <ofbiz:iterator name="cust" property="eqpTypeList">
					    <option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentType"/>'>
					    	<ofbiz:inputvalue entityAttr="cust" field="equipmentType"/>
					    </option>
				      </ofbiz:iterator>
			      	</ofbiz:if>
		    		</select>
				</td>
				<td colspan="4"></td>
			</tr>

			<tr bgcolor="#DFE1EC">
				<td width="10%" class="en11pxb">����</td>
				<td>
					<select name="period" id="period">
						<option value="DAY">��</option>
						<option value="WEEK">��</option>
						<option value="MONTH">��</option>
			    	</select>
				</td>

				<td width="12%">��:</td>
				<td width="28%"><input type="text" ID="startDate" NAME="startDate" readonly></td>
				<td width="12%">��:</td>
				<td width="28%"><input type="text" ID="endDate" NAME="endDate" value="<%=sDate%>" readonly></td>
		    </tr>

		    <tr bgcolor='#DFE1EC'>
				<td width="10%" class="en11pxb">Chart</td>
				<td colspan="5">
					<input type='checkbox' name='showAvailableTime' value="1" checked>Available Time
					&nbsp;
					<input type='checkbox' name='showUpTime' value="1" checked>Up Time
				</td>
			</tr>

			<tr bgcolor='#DFE1EC'>
				<td width="10%" class="en11pxb">������</td>
				<td colspan="5">

					<input type="text" id="referLine" name="referLine" value="0">
				</td>

			</tr>
		</table>
	  </fieldset>
	</td>
  <tr>
</table>

<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0" id="table3">
  <tr>
    <td valign="top"> <fieldset> <legend>�豸�б�</legend>
        <table width="100%" border="0" cellspacing="1" cellpadding="0" id="table4">
          <tr bgcolor="#DFE1EC">

            <td width="33%" class="en11pxb"><table cellSpacing="1" cellPadding="0" width="95%" border="0">
              <tr align="middle" bgColor="#dfe1ec">
                <td class="title-en" align="middle" width="299"></td>
              </tr>
              <tr bgColor="#dfe1ec">
                <td align="middle" width="304">
                    <select name="eqpId" id="eqpId" class="select" multiple size=8 ondblclick="doAddEqp();">
		    		</select>
                </td>
              </tr>
            </table></td>

            <td width="35%" height="35" class="en11pxb" align="center">
            <table height="64" cellSpacing="1" cellPadding="0" width="95%" border="0" id="table14">
              <tr align="middle">
                <td width="140"><table cellSpacing="0" cellPadding="0" border="0" id="table15">
                    <tr>
                      <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                      <td background="<%=request.getContextPath()%>/images/button_bg.gif"><a class="button-text" href="javascript:doAddEqp();"> &gt;&gt;</a></td>
                      <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
                    </tr>
                  </table>
                    <table cellSpacing="0" cellPadding="0" border="0" id="table16">
                      <tr>
                        <td width="11"><img src="<%=request.getContextPath()%>/images/button_left.gif"></td>
                        <td background="<%=request.getContextPath()%>/images/button_bg.gif"><a class="button-text" href="javascript:doRemoveEqp();"> &lt;&lt;</a></td>
                        <td width="20"><img src="<%=request.getContextPath()%>/images/button_right.gif"></td>
                      </tr>
                  </table></td>
              </tr>
            </table>
			</td>

            <td width="32%" class="en11pxb">
            <table cellSpacing="1" cellPadding="0" width="224" border="0" id="table10">
              <tr align="middle" bgColor="#dfe1ec">
                <td class="title-en" align="middle" width="299"></td>
              </tr>
              <tr bgColor="#dfe1ec">
                <td align="middle" width="304">
                    <select name="eqpIdSelected" id="eqpIdSelected" class="select" multiple size=8 ondblclick="doRemoveEqp();">
                    </select>
                </td>
              </tr>
            </table>
            </td>

          </tr>

        </table>

      </fieldset>
    </td>
  </tr>
</table>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/showEqpAvailUpTimeChart')"><span>&nbsp;��ʾ&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:document.eqpForm.reset();"><span>&nbsp;����&nbsp;</span></a></li>
	</ul></td>
	<td width="20">&nbsp;</td>
  </tr>
</table>

</form>