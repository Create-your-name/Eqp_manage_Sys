<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.text.SimpleDateFormat"%>
<% 
	String eqpModel=(String)request.getAttribute("eqp_Model");
  	String eqpid=(String)request.getAttribute("equipmentId");
  	String msg=(String)request.getAttribute("_ERROR_MESSAGE_");
  	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	Calendar cDay = Calendar.getInstance();
	String  unTime = formatter.format(cDay.getTime());
%>
<!--include yui css-->
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	//��ʼ��ҳ��ؼ�
	Ext.onReady(function(){	 			
	    var deptType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'eqp_Model',
	        width:170,
	        forceSelection:true
	    });
	    deptType.on('select',equipModelChange);	    
	 });
	 
	 function equipModelChange(){
		debugger;
		var newEqpValue=Ext.get('eqp_Model').dom.value.replace("+", "aaa");
		var actionURL='<ofbiz:url>/getEqpIdByModel</ofbiz:url>?equipmodel='+newEqpValue;
		Ext.lib.Ajax.formRequest('querykeyPartsUnuseForm',actionURL,{success: commentSuccess_c, failure: commentFailure});
	}
	//Զ�̵��óɹ�
	function commentSuccess_c(o){
		 var result = eval('(' + o.responseText + ')');
	 
           //�豸ID���ݳ�ʼ��
			var equipId=document.getElementById("equipmentId");
			var eqpArray=result.eqpIdArray;
			var eqpIdSize=result.eqpIdArray.length;
			equipId.length=1;
			for(var i=0;i<eqpIdSize;i++){
				equipId.options[equipId.length]=new Option(eqpArray[i],eqpArray[i]);
			}
			var obj=document.getElementById('equipmentId');
			obj.value='<%=eqpid%>'
	}
	
	var commentFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
    }; 
    
    function keyEqpPartsList(){
    	querykeyPartsUnuseForm.submit();
    }
    
    function unUseKeyParts(obj,model,keyUseId,eqpId,keydesc,partsId,useNumber,partsType){
    	Ext.get('model').dom.value=model;
    	Ext.get('eqpId').dom.value=eqpId;
    	Ext.get('keydesc').dom.value=keydesc;
    	Ext.get('partsId').dom.value=partsId;
    	Ext.get('useNumber').dom.value=useNumber;
    	Ext.get('partsType').dom.value=partsType;
    	Ext.get('keyUseId').dom.value=keyUseId;
        var url='<ofbiz:url>/queryKeyPartsByIndex</ofbiz:url>';
        extDlg.showEditDialog(obj,url);
    }
    function commentSuccess(o){
    	var unTime = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:true
	    });
	    unTime.applyTo('unTime');
    }
    function checkForm(){
		var unTime=Ext.get('unTime').dom.value;
		var remark=Ext.get('remark').dom.value;
		var model=Ext.get('model').dom.value;
		var eqpId=Ext.get('eqpId').dom.value;
    	var actionUrl=document.unUse.action;
        document.unUse.action=actionUrl+"?eqp_Model="+model+"&equipmentId="+eqpId;  	
		if(unTime==""){
			return "�»�ʱ�䲻��Ϊ��";
		}
		if(remark==""){
			return "��ע����Ϊ��";
		}
		return "";
	}

</script>
<form name="querykeyPartsUnuseForm" method="POST" action="<%=request.getContextPath()%>/control/keyPartsUnuseList">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td><fieldset><legend>��ѯδ�»��ؼ�����</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC" height="30"> 
          <td class="en11pxb" width="12%">eqpModel</td>
          <td>          		
         		<select id="eqp_Model" name="eqp_Model">
          			<option value=''></option>
	          		<ofbiz:if name="equipmentTypeList">
		        		<ofbiz:iterator name="EquipmentModel" property="equipmentTypeList">
			    			<option value='<ofbiz:inputvalue entityAttr="EquipmentModel" field="model"/>'><ofbiz:inputvalue entityAttr="EquipmentModel" field="model"/></option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
    			</select>          		
          </td>
          <td class="en11pxb" width="12%">EQPID</td>
          <td width="38%">          		
         		<select id="equipmentId" name="equipmentId">
          			<option value=''></option>	          		
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
	
			<li><a class="button-text" href="#" onclick="javascript:keyEqpPartsList();"><span>&nbsp;ȷ��&nbsp;</span></a></li> 
			</ul>
		</td>
	</tr>
</table>
</form>

<ofbiz:if name="flag" value="OK">
<div id="keyEqpPartsList" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="keyEqpPartsList" style="visibility:'hidden';">
</ofbiz:unless>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>ά���ؼ�����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td class="en11pxb"></td>
          <td class="en11pxb">����</td>
          <td class="en11pxb">eqpModel</td>
          <td class="en11pxb">eqpId</td>
          <td class="en11pxb">�ؼ���</td>
          <td class="en11pxb">���Ϻ�</td>
          <td class="en11pxb">������</td>
          <td class="en11pxb">����</td>
          <td class="en11pxb">���</td>
          <td class="en11pxb">��������</td>
          <td class="en11pxb">vendor</td>         
          <td class="en11pxb">SeriesNo</td> 
          <td class="en11pxb">baseS/N</td> 
          <td class="en11pxb">��������</td>
          <td class="en11pxb">���ڴ���</td>
          <td class="en11pxb">����ԭ��</td> 
          <td class="en11pxb">��ע</td>
        </tr>     
         <% 
        List keyPartsUnuseList=(List)request.getAttribute("keyPartsUnuseList");
        if(keyPartsUnuseList != null && keyPartsUnuseList.size() > 0) {  
            int k = 0;
            for(Iterator it = keyPartsUnuseList.iterator();it.hasNext();) { 
                Map map = (Map)it.next();
               
        %>       
       <tr bgcolor="#DFE1EC">
		          <td class="en11px"><input type="button" value="�»�" onclick="javascript:unUseKeyParts(this,'<%=map.get("MODEL") %>','<%=map.get("KEY_USE_ID") %>','<%=map.get("EQP_ID") %>','<%=map.get("KEYDESC") %>','<%=map.get("PARTS_ID") %>','<%=map.get("USE_NUMBER") %>','<%=map.get("PARTS_TYPE") %>');"></td>
		          <td class="en11px"><%=map.get("T_NAME") %></td>
		          <td class="en11px"><%=map.get("MODEL") %></td>
		          <td class="en11px"><%=map.get("EQP_ID") %></td>
		          <td class="en11px"><%=map.get("KEYDESC") %></td>
		          <td class="en11px"><%=map.get("PARTS_ID") %></td>
		          <td class="en11px"><%=map.get("PARTS_NAME") %></td>		          
		          <td class="en11px"><%=map.get("USE_NUMBER") %></td>
		          <td class="en11px"><%=map.get("PARTS_TYPE") %></td>		          
		          <td class="en11px"><%=map.get("LIMIT_TYPE") %></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("VENDOR"))%></td>
      			  <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("SERIES_NO"))%></td>
      			  <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("BASE_SN"))%></td>
      			  <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("INIT_LIFE"))%></td>
      			  <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("DELAYTIME"))%></td>
      			  <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("OFF_LINE"))%></td>
      			  <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("REMARK"))%></td>
		        </tr>
		         <%
          k++;
            }
          } 
          %>
      </table>
      </fieldset></td>
  </tr>
</table>
</div>

<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">�ؼ������»�</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="�ؼ������»�">
            <div class="inner-tab" id="x-form">
                <form name="unUse" action="<%=request.getContextPath()%>/control/unUseKeyParts" method="post" id="unUse" onsubmit="return false;">
                <input type="hidden" name="keyUseId" id="keyUseId" value="" />
                <input type="hidden" name="model" id="model" value="" />
                <p>
                <label for="description"><small>��̨</small></label>
                <input class="textinput" type="text" name="eqpId" id="eqpId" value="" size="22"  style="background:E9E9E9" readonly="readonly"/>
                </p>
                <p>
                <label for="description"><small>�ؼ���</small></label>
                <input class="textinput" type="text" name="keydesc" id="keydesc" value="" size="22"  style="background:E9E9E9" readonly="readonly"/>
                </p>
                <p><label for="description"><small>������</small></label>
                <input class="textinput" type="text" name="partsId" id="partsId" value="" size="22"  style="background:E9E9E9" readonly="readonly"/>
                </p>
                <p><label for="description"><small>���</small></label>
                <input class="textinput" type="text" name="partsType" id="partsType" value="" size="22" style="background:E9E9E9" readonly="readonly"/>
                </p>
                <p>
                <label for="name"><small>ʹ������</small></label>
                <input class="textinput" type="text" name="useNumber" id="useNumber" value="" size="22" style="background:E9E9E9" readonly="readonly"/>
                </p>
              	<p>
                <label for="name"><small>�»�ʱ��</small></label>
                <ul><input type="date" name="unTime" id="unTime" value="" /></ul>
                </p>
              	<p>
                <label for="name"><small>��ע</small></label>
                <input class="textinput" type="text" name="remark" id="remark" value="" size="22" />
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
		var obj=document.getElementById('eqp_Model');
		obj.value='<%=eqpModel%>'
		equipModelChange();
</script>

