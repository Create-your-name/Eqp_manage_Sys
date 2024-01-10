<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.net.URLEncoder" %>
<% 
	String eqpModel=(String)request.getAttribute("eqpModel");
 
 
%>
<!--include yui css-->
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	//���ݺϷ���У��
	function checkForm(){
		  var reg = /^[+]{0,1}(\d+)$|^[+]{0,1}(\d+\.\d+)$/;//Js��֤�Ǹ���(0,����������С��) 
		  var keydesc = Ext.get('keydesc').dom.value;
	      var errorspec = Ext.get('errorspec').dom.value;
	      var ifalarm = Ext.get('ifalarm').dom.value;
	      var usenumber=Ext.get('usenumber').dom.value;
			if(keydesc==""){
				return "�ؼ��ֲ���Ϊ��";
			}
			if(ifalarm=="Y" && errorspec=="")
		    { 
				return "���������߲���Ϊ��";
			}			
			
			if(!IsNumeric(errorspec)){
				return "���������߱���Ϊ����";
			}
			if(!IsNumeric(usenumber)){
				return "ʹ����������Ϊ����";
			}
			if(reg.test(usenumber)==false){
        	return "ʹ����������Ϊ��ֵ";
        	}

		return "";
	}
	
	//�ı䵯�����С
	extDlg.dlgInit('500','550');
	
	//�ؼ�����delayά��ҳ��
	function editkeypartsdelay(obj,keypartsid,partsid,eqptype){
		var result=window.open("<%=request.getContextPath()%>/control/queryKeyPartsDelayList?keyPartsId="+keypartsid+"&partsId="+partsid+"&eqpType="+eqptype,"parts","top=130,left=240,width=650,height=350,title=,channelmode=0," +
             "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
             "scrollbars=1,status=1,titlebar=0,toolbar=0");
	}
	
	//��������ҳ
	 function addkeyparts(){
		 getPartsNo();
	}
	
	function getPartsNo(){
		debugger;
		var par=new Array(); 
        var result=window.open ("<%=request.getContextPath()%>/control/queryKeyParts?eqpModel=<%=eqpModel%>","parts","top=130,left=240,width=650,height=350,title=,channelmode=0," +
             "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
             "scrollbars=1,status=1,titlebar=0,toolbar=0");
	}
	//�޸ĵ���ҳ
	function editKeyPartsId(obj,mustchange,usenumber,keypartsid,keydesc,errorspec,warmspec,enable,sectionIndex){
		Ext.get('keypartsID').dom.value=keypartsid;
		Ext.get('keyDesc').dom.value=keydesc;
		Ext.get('errorspec').dom.value=errorspec;
		Ext.get('warmspec').dom.value=warmspec;
		Ext.get('section').dom.value=sectionIndex;
		if(usenumber!="null"){
			Ext.get('usenumber').dom.value=usenumber;
		}else{
			Ext.get('usenumber').dom.value=1;
		}
		if(mustchange!="null"){
			Ext.get('mustchange').dom.value=mustchange;
		}else{
			Ext.get('mustchange').dom.value="N";
		}
		Ext.get('enable').dom.value=enable;		
		var url='<ofbiz:url>/queryKeyPartsByIndex</ofbiz:url>?keypartsid='+keypartsid;
		extDlg.showEditDialog(obj,url);
	}
	//ɾ������ҳ
	function delDefaultPeriod(obj,periodIndex){
        Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˼�¼��',function result(value){
	        if(value=="yes"){
				var eqpModel=Ext.get('eqp_Model').dom.value;
				var url='<ofbiz:url>/deleteKeyParts</ofbiz:url>?keyPartsId='+periodIndex+'&eqpModel='+eqpModel;
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
			Ext.get('periodStyle').dom.value=result.periodStyle;
			
			periodDaysScope= result.periodDaysScope;
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
	function keyEqpPartsList() {
		 if(Ext.getDom('eqp_Model').value=='') {
		 	Ext.MessageBox.alert('����', '��ѡ��eqpModel��');
		 	return;
		 }
		  
		loading();
		querykeyPartsForm.submit();	
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
	Ext.lib.Ajax.formRequest('PeriodDefineForm',actionURL,{success: periodStyleSuccess, failure: periodStyleFailure});
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
	        transform:'eqp_Model',
	        width:170,
	        forceSelection:true
	    });
	});	
	
	
</script>
<form name="querykeyPartsForm" method="POST" action="<%=request.getContextPath()%>/control/keyEqpPartsList">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td><fieldset><legend>��ѯ�豸����</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC" height="30"> 
          <td class="en11pxb" width="10%">eqpModel</td>
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
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addkeyparts();"/></td>
          <td width="10%" class="en11pxb">�Ϻ�</td>
          <td width="5%" class="en11pxb">eqpModel</td>     
          <td width="20%" class="en11pxb">��������</td>
          <td width="5%" class="en11pxb">�ؼ���</td>
          <td width="5%" class="en11pxb">ʹ������</td>
          <td width="10%" class="en11pxb">��������</td>
          <td width="10%" class="en11pxb">Limitlife</td>
          <td width="10%" class="en11pxb">WARNINGlife</td> 
          <td width="5%" class="en11pxb">�Ƿ񱨾�</td>
          <td width="5%" class="en11pxb">�Ƿ�ػ�</td>
          <td width="5%" class="en11pxb">�Ƿ�����</td>
          <td width="10%" class="en11pxb">ά��ʱ��</td> 
          <td width="10%" class="en11pxb">֪ͨ��</td>
        </tr>     
         <% 
        List keyList=(List)request.getAttribute("keyList");
        if(keyList != null && keyList.size() > 0) {  
            int k = 0;
            for(Iterator it = keyList.iterator();it.hasNext();) { 
                Map map = (Map)it.next();
               
        %>
       <!--   <ofbiz:if name="keyEqpPartsList">
	        <ofbiz:iterator name="keylist" property="keyEqpPartsList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="center"><img  src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delDefaultPeriod(this,'<ofbiz:inputvalue entityAttr="keylist" field="keyPartsId"/>');"/></td>
		          <td width="10%" class="en11px"><ofbiz:entityfield attribute="keylist" field="partsId"/></td>
		          <td width="5%" class="en11px"><ofbiz:entityfield attribute="keylist" field="eqpType"/></td>
		          <td width="20%" class="en11px"><ofbiz:entityfield attribute="keylist" field="partsName"/></td>
		          <td width="10%" class="en11px"><ofbiz:entityfield attribute="keylist" field="keydesc"/></td>
		          <td width="10%" class="en11px"><ofbiz:entityfield attribute="keylist" field="limitType"/></td>		          
		          <td width="10%" class="en11px"><ofbiz:entityfield attribute="keylist" field="errorSpec"/></td>
		          <td width="10%" class="en11px"><ofbiz:entityfield attribute="keylist" field="warnSpec"/></td>
		          <td width="10%" class="en11px"><ofbiz:entityfield attribute="keylist" field="isAlarm"/></td>     
		          <td width="10%" class="en11px"><ofbiz:entityfield attribute="keylist" field="createTime"/></td>          
		        </tr>
	      </ofbiz:iterator>
      </ofbiz:if>-->
       <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="left"><img  src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delDefaultPeriod(this,'<%=map.get("KEY_PARTS_ID") %>');"/>
		        <%  String mustchange="";
		        	if(map.get("MUSTCHANGE")==null){}else{mustchange=(String)map.get("MUSTCHANGE");}
		        	if(!mustchange.equals("Y")){
		        		if(map.get("ISDELAY").toString().equals("Y")){
		        %>  	
		          	<img  src="<%=request.getContextPath()%>/pms/images/set.gif" style="cursor:hand" onclick="editkeypartsdelay(this,'<%=map.get("KEY_PARTS_ID") %>','<%=map.get("PARTS_ID") %>','<%=map.get("EQP_TYPE") %>')" />
		        <%}else{%>
		        	<img  src="<%=request.getContextPath()%>/pms/images/aset.jpg" style="cursor:hand" onclick="editkeypartsdelay(this,'<%=map.get("KEY_PARTS_ID") %>','<%=map.get("PARTS_ID") %>','<%=map.get("EQP_TYPE") %>')" />
		       	<%}
		       	}
		       	%>
		          </td>
		          <td width="10%" class="en11px"><a href="#" onclick="editKeyPartsId(this,'<%=map.get("MUSTCHANGE")%>','<%=map.get("USE_NUMBER")%>','<%=map.get("KEY_PARTS_ID") %>','<%=map.get("KEYDESC") %>','<%=UtilFormatOut.checkNull((String)map.get("ERROR_SPEC")) %>','<%=UtilFormatOut.checkNull((String)map.get("WARN_SPEC")) %>','<%=UtilFormatOut.checkNull((String)map.get("ENABLE")) %>','<%=map.get("SECTION_INDEX") %>')"/><%=map.get("PARTS_ID") %></td>
		          <td width="5%" class="en11px"><%=map.get("EQP_TYPE") %></td>
		          <td width="20%" class="en11px"><%=map.get("PARTS_NAME") %></td>
		          <td width="5%" class="en11px"><%=map.get("KEYDESC") %></td>
		          <%
		          if(map.get("USE_NUMBER")!=null){
		          %>
		          <td width="5%" class="en11px"><%=map.get("USE_NUMBER") %></td>
		          <%
		          }else{
		          %>
		         <td width="5%" class="en11px">1</td>
		          <%
		          }
		          %>
		          <td width="10%" class="en11px"><%=map.get("LIMIT_TYPE") %></td>		          
		          <td width="10%" class="en11px"><%=UtilFormatOut.checkNull((String)map.get("ERROR_SPEC")) %></td>
		          <td width="10%" class="en11px"><%=UtilFormatOut.checkNull((String)map.get("WARN_SPEC")) %></td>
		          <td width="5%" class="en11px"><%=map.get("IS_ALARM") %></td>
		          <%
		          if(map.get("MUSTCHANGE")!=null){
		          %>
		          <td width="5%" class="en11px"><%=map.get("MUSTCHANGE") %></td>
		          <%
		          }else{
		          %>
		          <td width="5%" class="en11px">N</td>
		          <%
		          }
		          %>
		          <td width="5%" class="en11px"><%=UtilFormatOut.checkNull((String)map.get("ENABLE")) %></td>  
		          <td width="10%" class="en11px"><%=UtilFormatOut.checkNull((String)map.get("UPDATETIME")) %></td>  
		          <td width="10%" class="en11px"><%=UtilFormatOut.checkNull((String)map.get("SECTION") )%></td>          
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
    <div class="x-dlg-hd">�ؼ���������</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="�ؼ������趨">
            <div class="inner-tab" id="x-form">
                <form name="KeyPartsForm" action="<%=request.getContextPath()%>/control/updateKeyParts" method="post" id="eq" onsubmit="return false;">
                <input id="keypartsID" type="hidden" name="keypartsID" value="" /> 
                <input id="eqpModel" type="hidden" name="eqpModel" value='<%=eqpModel%>' />   
                <p>
                <label for="name"><small>�ؼ���</small></label>
                <input class="textinput" type="text" name="keyDesc" id="keyDesc" value="" size="22" tabindex="1" style="background:E9E9E9" readonly="readonly"/>
                </p>
                <p>
                <label for="name"><small>ʹ������</small></label>
                <input class="textinput" type="text" name="usenumber" id="usenumber" value="" size="22" tabindex="1" />
                </p>
                <p>
                <label for="name"><small>����������</small></label>
                <input class="textinput" type="text" name="errorspec" id="errorspec" value="" size="22" tabindex="1" />
                </p>   
                <p>
                <label for="name"><small>Ԥ����</small></label>
                <input class="textinput" type="text" name="warmspec" id="warmspec" value="" size="22" tabindex="1" />
                </p>  
                <p>
                <label for="name"><small>�Ƿ񱨾�</small></label>
                <select id="ifalarm" name="ifalarm">
      	        <option value="Y">Y</option>
      		    <option value="N">N</option>
      			</select>
                </p>
                <p>
                <label for="name"><small>�Ƿ�ػ�</small></label>
                <select id="mustchange" name="mustchange">
      	        <option value="Y">Y</option>
      		    <option value="N">N</option>
      			</select>
                </p>
                <p>
                <label for="name"><small>�Ƿ�����</small></label>
                <select id="enable" name="enable">
      	        <option value="Y">Y</option>
      		    <option value="N">N</option>
      			</select>
                </p>
                <p>
                <label for="name"><small>֪ͨ��</small></label>
                <select id="section" name="section">
      	        	<ofbiz:if name="SectionList">
                    	<ofbiz:iterator name="cust" property="SectionList">                      
                        	<option value='<ofbiz:inputvalue entityAttr="cust" field="sectionIndex"/>'>
                            	<ofbiz:inputvalue entityAttr="cust" field="section"/>
                    		</option>
                    	</ofbiz:iterator>
                     </ofbiz:if>
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
		var obj=document.getElementById('eqp_Model');
		obj.value='<%=eqpModel%>'
	</ofbiz:if>
</script>

