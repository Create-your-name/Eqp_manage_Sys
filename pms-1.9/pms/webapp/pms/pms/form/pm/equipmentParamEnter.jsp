<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<!--include yui css-->
<%@ include file="../../yui-ext/ext-comdlg.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<% 
int i=1;
int k=1;
List unscheduleEqpParamList = (List)request.getAttribute("unscheduleEqpParamList");
%>
<!-- yui page script-->
<script language="javascript">
	var equipmentId = '<%=request.getAttribute("equipmentId")%>';
	//���ݺϷ���У��
	function checkForm(){
        var description = Ext.get('paramName').dom.value;
		if(description==""){
			return "�������Ʋ���Ϊ��";
		}
		return "";
	}
	
	//�ı䵯�����С
	extDlg.dlgInit('500','300');
	
	//�޸ĵ���ҳ
	function editEquipmentParam(obj,seqIndex){
		Ext.get('seqIndex').dom.value=seqIndex;
		var url='<ofbiz:url>/queryEquipmentParamValueByIndex</ofbiz:url>?seqIndex='+seqIndex;
		extDlg.showEditDialog(obj,url);
	}
	
	
	//�޸�ʱԶ�̵��óɹ����ҳ�渳ֵ
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('equipmentId').dom.value=result.equipmentId;
			Ext.get('paramName').dom.value=result.paramName;
			Ext.get('eqpStatus').dom.value=result.eqpStatus;
			Ext.get('maxValue').dom.value=result.maxValue;
			Ext.get('minValue').dom.value=result.minValue;
			Ext.get('stdFlag').dom.value=result.stdFlag;
			Ext.get('manualValue').dom.value=result.manualValue;
			Ext.get('guiValue').dom.value=result.guiValue;
			Ext.get('transBy').dom.value=result.transBy;
			Ext.get('_section').dom.value=Ext.get('section').dom.value;
			if (Ext.get('stdFlag').dom.value=='M')
			{
				Ext.get('guiValue').dom.disabled = true;
			}
			else if(Ext.get('stdFlag').dom.value=='A')
			{
				Ext.get('manualValue').dom.disabled = true;
			}			
		}
	}	

	//��ѯ
	function equipmentParamList() {
		//if(Ext.getDom('equipment_Id').value=='') {
			//Ext.MessageBox.alert('����', '��ѡ���豸!');
			//return;
		//}
		loading();
		document.eqpParamForm.submit();	
	}
	
	//�豸���������޸�
	function batchEdit(){
	<%if(unscheduleEqpParamList!=null&&unscheduleEqpParamList.size()>0){%>
		var max=Ext.get('maxIndex').dom.value
		for(var i=1;i<=max;i++){
			var obj=document.getElementById("manualValue_"+i);
			if(obj.value==''){
				//continue;
				// '�ֶ�����ֵ����Ϊ��,huanghp,883609,2008.10.30
				Ext.MessageBox.alert('����', '�ֶ�����ֵ����Ϊ�գ�����!');
				return false;
			}else{
				if(!IsNumeric(obj.value)){
					Ext.MessageBox.alert('����', '�ֶ�����ֵ����Ϊ���֣�����!');
					return false;
				}
			}
		}
		loading();
		Ext.get('_section_batch').dom.value=Ext.get('section').dom.value;
		document.batchEeditForm.submit();
		<%}else{%>
			Ext.MessageBox.alert('����', '�޿����޸ĵļ�¼������!');
			return false;
		<%}%>
	}
	
	var eqpParam = function() {
		var sectionCom;
		var sectionDS;
		var sectionRecordDef;
		return {
			init : function() {
				this.createDataStore();
			    this.createCombox();
			    this.initLoad();
			},
			
			//��������Դ
			createDataStore : function() {
				sectionRecordDef = Ext.data.Record.create([
				    {name: 'sectionIndex'}, 
					{name: 'section'}
				]);
	
				sectionDS = new Ext.data.Store({
			        proxy: new Ext.data.HttpProxy({url: '<%=request.getContextPath()%>/control/querySectionByDept'}),
				    reader: new Ext.data.JsonReader({}, sectionRecordDef)
			    });
			    
			    equipmentDS = new Ext.data.Store({
			        proxy: new Ext.data.HttpProxy({url: '<%=request.getContextPath()%>/control/queryEquipmentBySection'}),
				    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
			    });
			},
			
			createCombox : function() {
				 
				//���ÿα�
			    sectionCom = new Ext.form.ComboBox({
				    store: sectionDS,
				    displayField:'section',
				    valueField:'section',
				    typeAhead: true,
				    mode: 'local',
				    width: 170,
				    triggerAction: 'all'
			    });
			    sectionCom.applyTo('section');
			    sectionCom.on('select', this.loadEquipment);
	
				//�����豸ID
				eqpCom = new Ext.form.ComboBox({
				    store: equipmentDS,
				    displayField:'equipmentId',
				    valueField:'equipmentId',
				    typeAhead: true,
				    mode: 'local',
				    width: 170,
				    triggerAction: 'all'
			    });
			    eqpCom.applyTo('equipment_Id');
			}, 
				
			initLoad : function() {
				var section = '<%=request.getAttribute("section")%>';
				if (section=='null'){
					section = "";
				}
				sectionDS.load({callback:function(){if(section != "") { sectionCom.setValue(section); eqpParam.loadEquipment(); } }});
			},
			
			loadEquipment : function() {
				var section = sectionCom.getValue();
				
				if (equipmentId=='null'){
					equipmentId = "";
				}
				equipmentDS.load({params:{section:section},callback:function(){ eqpCom.setValue(equipmentId);equipmentId=""; }})
			}
		}
	}();
	
	Ext.EventManager.onDocumentReady(eqpParam.init,eqpParam,true);
	
</script>

<form name="eqpParamForm" method="POST" action="<%=request.getContextPath()%>/control/equipmentParamValueList">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="100%"><fieldset><legend>��д��̨����</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC"> 
		  <td width="12%" class="en11pxb">&nbsp;����</td>
          <td width="12%">
			<input type="text" size="40" name="section" autocomplete="off"/>
          </td>                    
          <td width="12%" class="en11pxb">&nbsp;�豸</td>
          <td width="12%">
				<input type="text" size="40" name="equipment_Id" autocomplete="off"/>	 
          </td>  
          <td width="20%" class="en11pxb" align="left">
      		<table border="0" cellspacing="0" cellpadding="0">
		  	<tr height="30">
		   		<td width="20">&nbsp;</td>
		   	 	<td><ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:equipmentParamList();"><span>&nbsp;ȷ��&nbsp;</span></a></li> 
					</ul>
				</td>
		  	</tr>
			</table>
      	  </td>
        </tr>
      </table>
	 </fieldset> 
	</td>
  <tr> 
</table>
</form>

<br>
<ofbiz:if name="flag" value="OK">
<div id="equipmentParamList" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="equipmentParamList" style="visibility:'hidden';">
</ofbiz:unless>

<form name="batchEeditForm" method="POST" action="<%=request.getContextPath()%>/control/equipmentParamValueBatchEdit">
<input id="_section_batch" type="hidden" name="_section_batch" value="" />
<input id="equipment_Id" type="hidden" name="equipment_Id" value="<%=request.getAttribute("equipmentId")%>" /> 
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�趨��̨����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="11%" class="en11pxb">EQP ID</td>
          <td width="11%" class="en11pxb">��������</td>
          <td width="12%" class="en11pxb">MES�豸״̬��</td>
          <td width="11%" class="en11pxb">��ʾ���ֵ</td>
          <td width="11%" class="en11pxb">��ʾ��Сֵ</td>
          <td width="11%" class="en11pxb">�ֶ�(M)/�Զ�(A)</td> 
          <td width="11%" class="en11pxb">GUIֵ</td>
          <td width="11%" class="en11pxb">�ֶ�����ֵ</td>
          <td width="11%" class="en11pxb">����޸���</td>                                       
        </tr>
        <%if(unscheduleEqpParamList!=null&&unscheduleEqpParamList.size()>0){
        	for(Iterator it = unscheduleEqpParamList.iterator();it.hasNext();) { 
					Map map = (Map)it.next();
        %>
		        <tr bgcolor="#DFE1EC">
		         <% 
	        		k=i++;
	        	%>
		          <input id="seqIndex_<%=k%>" type="hidden" name="seqIndex_<%=k%>" value='<%=map.get("SEQ_INDEX")%>' />
		          <input id="equipmentId_<%=k%>" type="hidden" name="equipmentId_<%=k%>" value='<%=map.get("EQUIPMENT_ID")%>' />		        
		          <td width="11%" class="en11px"><%=map.get("EQUIPMENT_ID")%></td>
		          <td width="11%" class="en11px"><%=map.get("PARAM_NAME")%></td>
		          <td width="12%" class="en11px"><%=UtilFormatOut.checkNull((String)map.get("EQP_STATUS"))%></td>
		          <td width="11%" class="en11px"><%=UtilFormatOut.checkNull((String)map.get("MAX_VALUE"))%></td>
		          <td width="11%" class="en11px"><%=UtilFormatOut.checkNull((String)map.get("MIN_VALUE"))%></td>
		          <td width="11%" class="en11px"><%=UtilFormatOut.checkNull((String)map.get("STD_FLAG"))%></td>
		          <td width="11%" class="en11px"><%=UtilFormatOut.checkNull((String)map.get("GUI_VALUE"))%></td>
		          <td width="11%" class="en11px"><input type="text" ID="manualValue_<%=k%>" NAME="manualValue_<%=k%>" size="5" class="input" value='<%=UtilFormatOut.checkNull((String)map.get("MANUAL_VALUE"))%>'></td>
		          <td width="11%" class="en11px"><%=UtilFormatOut.checkNull((String)map.get("TRANS_BY"))%></td>		          		          		          		          
		        </tr>
	      <%
	      	}
	      }%>
	      <input id="maxIndex" type="hidden" name="maxIndex" value="<%=k%>"/>
      </table>
      </fieldset></td>
  </tr>
</table>
</form>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="#" onclick="batchEdit();"><span>&nbsp;�޸�&nbsp;</span></a></li> 
	</ul></td>
  </tr>
</table>
</div>
<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">�趨��̨����</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="��д��̨����">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/manageEquipmentParamValue" method="post" id="eq" onsubmit="return false;">
                <input id="_section" type="hidden" name="_section" value="" />
                <input id="seqIndex" type="hidden" name="seqIndex" value="" />                
				<input id="eqpStatus" type="hidden" name="eqpStatus" value="" />                
				<input id="maxValue" type="hidden" name="maxValue" value="" />                
				<input id="minValue" type="hidden" name="minValue" value="" /> 
				<input id="stdFlag" type="hidden" name="stdFlag" value="" /> 				
				<input id="guiValue" type="hidden" name="guiValue" value="" /> 				
				<input id="transBy" type="hidden" name="transBy" value="" />
				<input id="equipment_Id" type="hidden" name="equipment_Id" value="<%=request.getAttribute("equipmentId")%>" /> 			
                <p>
                <label for="name"><small>EQP ID</small></label>
                <input class="textinput" type="text" name="equipmentId" id="equipmentId" value="" size="22" tabindex="1" readonly/>
                </p>                
                <p>
                <label for="name"><small>��������</small></label>
                <input class="textinput" type="text" name="paramName" id="paramName" value="" size="22" tabindex="1" readonly/>
                </p>
                <!--  
                <p>
                <label for="name"><small>MES�豸״̬��</small></label>
                <input class="textinput" type="text" name="eqpStatus" id="eqpStatus" value="" size="22" tabindex="1" readonly/>
                </p>
                <p>
                <label for="name"><small>��ʾ���ֵ</small></label>
                <input class="textinput" type="text" name="maxValue" id="maxValue" value="" size="22" tabindex="1" readonly/>
                </p>
                <p>
                <label for="name"><small>��ʾ��Сֵ</small></label>
                <input class="textinput" type="text" name="minValue" id="minValue" value="" size="22" tabindex="1" readonly/>
                </p>      
                <p>
                <label for="name"><small>�ֶ�(M)/�Զ�(A)</small></label>
                <input class="textinput" type="text" name="stdFlag" id="stdFlag" value="" size="22" tabindex="1" readonly/>
                </p>                               
                <p>
                <label for="name"><small>GUIֵ</small></label>
                <input class="textinput" type="text" name="guiValue" id="guiValue" value="" size="22" tabindex="1" />
                </p> 
                --> 
                <p>
                <label for="name"><small>�ֶ�����ֵ</small></label>
                <input class="textinput" type="text" name="manualValue" id="manualValue" value="" size="22" tabindex="1" />
                </p>
                <!--                  
                 <p>
                <label for="name"><small>����޸���</small></label>
                <input class="textinput" type="text" name="transBy" id="transBy" value="" size="22" tabindex="1" readonly/>
                </p> 
                 -->                                                                          
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