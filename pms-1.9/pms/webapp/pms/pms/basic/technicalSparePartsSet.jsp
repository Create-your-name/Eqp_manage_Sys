<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="java.util.List"%>
<% 
	String eqpType=(String)request.getAttribute("eqpType");
%>
<!--include yui css-->
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	//���ݺϷ���У��
	function checkForm(){
        var mtrDesc = Ext.get('mtrDesc').dom.value;
        var qty = Ext.get('qty').dom.value;

		if(mtrDesc==""){
			return "���ı���������Ϊ��";
		}
		
		if(qty==""){
			return "��������Ϊ��";
		}			
		
		if(!IsNumeric(qty)){
			return "��������Ϊ����";
		}

		
		return "";
	}
	
	//�ı䵯�����С
	extDlg.dlgInit('500','230');
	
	//��������ҳ
	 function addTechnicalSpareParts(obj){
		Ext.get('eqpType').dom.value=Ext.get('eqp_Type').dom.value;		
		var mtrNumTemp="2T"+Ext.get('eqp_Type').dom.value;	
		Ext.get('mtrNum').dom.value=mtrNumTemp;	
		document.TechnicalSparePartsForm.mtrNumTemp.value=mtrNumTemp;
		Ext.get('mtrDesc').dom.value="";
		Ext.get('qty').dom.value="999";
		extDlg.showAddDialog(obj);	
	}
	
	
	//�޸ĵ���ҳ
	function editTechnicalSpareParts(obj,materialStoReqIndex){
		Ext.get('eqpType').dom.value=Ext.get('eqp_Type').dom.value;
		Ext.get('materialStoReqIndex').dom.value=materialStoReqIndex;
		var url='<ofbiz:url>/queryTechnicalSparePartsByIndex</ofbiz:url>?materialStoReqIndex='+materialStoReqIndex;
		extDlg.showEditDialog(obj,url);
	}
	
	//ɾ������ҳ
	function delTechnicalSpareParts(obj,materialStoReqIndex){
        Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˼�¼��',function result(value){
	        if(value=="yes"){
				var eqpType=Ext.get('eqpType').dom.value;
				var url='<ofbiz:url>/deleteTechnicalSpareParts</ofbiz:url>?materialStoReqIndex='+materialStoReqIndex+"&eqpType=<%=eqpType%>";
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
			Ext.get('mtrNum').dom.value=result.mtrNum;
			Ext.get('mtrDesc').dom.value=result.mtrDesc;
			Ext.get('qty').dom.value=result.qty;
		}

		if(Ext.get('defaultDays').dom.value=="1")
		{
			Ext.get('timeRangeIndex').dom.disabled = false;
		}
		else
		{
			Ext.get('timeRangeIndex').dom.disabled = true;
		}						
	}	

	//��ѯ
	function technicalSparePartsList() {
		if(Ext.getDom('eqp_Type').value=='') {
			Ext.MessageBox.alert('����', '��ѡ���豸���࣡');
			return;
		}
		loading();
		document.technicalSpareParts.submit();	
	}
	
	function changeDefaultDays()
	{
		if(Ext.get('defaultDays').dom.value=="1")
		{
			Ext.get('timeRangeIndex').dom.disabled = false;
		}
		else
		{
			Ext.get('timeRangeIndex').dom.disabled = true;
		}
	}
	
	//ѡ���豸��������ʱ
	function onChangePeriodStyle(){
	var	periodStyle=Ext.get('periodStyle').dom.value;
	if(periodStyle==''){
	Ext.get('periodName').dom.value="";
	Ext.get('defaultDays').dom.value="";
	return ;
	}
	var actionURL='<ofbiz:url>/queryEquipmentCycDefault</ofbiz:url>?periodStyle='+periodStyle;
	Ext.lib.Ajax.formRequest('TechnicalSparePartsForm',actionURL,{success: periodStyleSuccess, failure: periodStyleFailure});
	
 }
 
 var periodDaysScope="";
	function periodStyleSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
		 Ext.get('periodName').dom.value=result.periodStyle;
		 periodDaysScope= result.periodDaysScope;
		 Ext.get('defaultDays').dom.value=result.periodDaysScope.split("-")[0];
		 changeDefaultDays();
		}
	}
	//Զ�̵���ʧ��
   var periodStyleFailure = function(o){
    	Ext.MessageBox.alert('����', 'Unable to connect.');
   };
	
	Ext.onReady(function(){
	    var eqpType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'eqp_Type',
	        width:170,
	        forceSelection:true
	    });
	});	
	
	
</script>
<form name="technicalSpareParts" method="POST" action="<%=request.getContextPath()%>/control/technicalSparePartsList">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td><fieldset><legend>���Ϻű�������</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC" height="30"> 
          <td class="en11pxb" width="10%">�豸����</td>
          <td>          		
         		<select id="eqp_Type" name="eqp_Type">
          			<option value=''></option>
	          		<ofbiz:if name="equipmentTypeList">
		        		<ofbiz:iterator name="equipmentType" property="equipmentTypeList">
			    			<option value='<ofbiz:inputvalue entityAttr="equipmentType" field="equipmentType"/>'><ofbiz:inputvalue entityAttr="equipmentType" field="equipmentType"/></option>
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
			<li><a class="button-text" href="#" onclick="javascript:technicalSparePartsList();"><span>&nbsp;ȷ��&nbsp;</span></a></li> 
			</ul>
		</td>
	</tr>
</table>
</form>
<ofbiz:if name="flag" value="OK">
<div id="technicalSparePartsList" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="technicalSparePartsList" style="visibility:'hidden';">
</ofbiz:unless>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�趨��̨����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addTechnicalSpareParts(this);"/></td>
          <td width="15%" class="en11pxb">������</td>
          <td width="20%" class="en11pxb">��������</td>
          <!-- <td width="15%" class="en11pxb">����</td>
          <td width="15%" class="en11pxb">����</td>
          <td width="15%" class="en11pxb">����</td>     -->    
                                                        
        </tr>
        <ofbiz:if name="technicalSparePartsList">
	        <ofbiz:iterator name="McsMaterialStoReq" property="technicalSparePartsList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="center"><img  src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delTechnicalSpareParts(this,'<ofbiz:inputvalue entityAttr="McsMaterialStoReq" field="materialStoReqIndex"/>');"/></td>
		          <td width="15%" class="en11px"><a href="#" onclick="editTechnicalSpareParts(this,'<ofbiz:inputvalue entityAttr="McsMaterialStoReq" field="materialStoReqIndex"/>')"><ofbiz:entityfield attribute="McsMaterialStoReq" field="mtrNum"/></td>
		          <td width="20%" class="en11px"><ofbiz:entityfield attribute="McsMaterialStoReq" field="mtrDesc"/></td>
		         <!--  <td width="15%" class="en11px"><ofbiz:entityfield attribute="McsMaterialStoReq" field="plant"/></td>
		          <td width="15%" class="en11px"><ofbiz:entityfield attribute="McsMaterialStoReq" field="mtrGrp"/></td>
		          <td width="15%" class="en11px"><ofbiz:entityfield attribute="McsMaterialStoReq" field="qty"/></td>		          
		           -->
		        </tr>
	      </ofbiz:iterator>
      </ofbiz:if>
      </table>
      </fieldset></td>
  </tr>
</table>
</div>
<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">�趨���Ϻű�������</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="���Ϻű����趨">
            <div class="inner-tab" id="x-form">
                <form name="TechnicalSparePartsForm" action="<%=request.getContextPath()%>/control/manageTechnicalSpareParts" method="post" id="eq" onsubmit="return false;">
                <input id="materialStoReqIndex" type="hidden" name="materialStoReqIndex" value="" /> 
                <input id="mtrNumTemp" type="hidden" name="mtrNumTemp" />
                <input id="eqpType" type="hidden" name="eqpType" value="" />
                <input id="mtrNum" type="hidden" name="mtrNum" value="" />
                <p>
                <label for="name"><small>���Ϻű�������</small></label>
                <input class="textinput" type="text" name="mtrDesc" id="mtrDesc" value="" size="22" tabindex="1" />
                </p>
                <p>
                <label for="name"><small>����</small></label>
                <input class="textinput" type="text" name="qty" id="qty" value="" size="22" tabindex="1" />
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