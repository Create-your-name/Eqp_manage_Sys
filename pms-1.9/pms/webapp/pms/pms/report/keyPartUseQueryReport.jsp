<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<%
     
	List partList = (List)request.getAttribute("PART_LIST");
	String equ = UtilFormatOut.checkNull(request.getParameter("equipmentType"));
	
	String keydesc = (String)request.getAttribute("keydesc");
	String eqpmodel = (String)request.getAttribute("eqpmodel");
	String equId = request.getParameter("equipmentId");
	String eqpDept = (String)request.getAttribute("eqpDept");
	String deptIndex = (String)request.getAttribute("deptIndex");	
 	String section=(String)request.getAttribute("section");
	String equipmentId=(String)request.getAttribute("equipmentId");
	String isalarm=(String)request.getAttribute("isalarm");
	String isdelay=(String)request.getAttribute("isdelay");
	String isError=(String)request.getAttribute("isError");
	String isUsed=(String)request.getAttribute("isUsed");
	     	
	String sDate = UtilFormatOut.checkNull(request.getParameter("startDate"));
	String eDate = UtilFormatOut.checkNull(request.getParameter("endDate"));
	String flag = (String)request.getAttribute("flag");
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	Calendar cDay = Calendar.getInstance();
	 String  startDate = formatter.format(cDay.getTime());
	 
	
 %>
<script language='javascript' src='<%=request.getContextPath()%>/images/pupdate.js' type='text/javascript'></script>
<!-- yui page script-->
<script language="javascript">
	 
	function keyPartsDelayQuery(keyUseId){
		debugger;
        var result=window.open ("<%=request.getContextPath()%>/control/queryKeyPartsDelayReport?keyUseId="+keyUseId,"","top=130,left=240,width=650,height=350,title=,channelmode=0," +
             "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
             "scrollbars=1,status=1,titlebar=0,toolbar=0");
	}
	 
	function partsUserQuery(){
		debugger;
		var starttime=Ext.get("startDate").dom.value;
		var endtime=Ext.get("endDate").dom.value;
		if(starttime==""){
			Ext.MessageBox.alert('����', '��ʼ���ڲ���Ϊ�գ�');
			return;
		}
		if(endtime==""){
			Ext.MessageBox.alert('����', '�������ڲ���Ϊ�գ�');
			return;
		}
		loading();
		document.keypartsDataQueryForm.submit();
	}
	
	function checkForm(){
		var name = Ext.get('offtime').dom.value;
		if(name==""){
			return "�»�ʱ�䲻��Ϊ��";
		}
	 	return "";
	}
	
	//�豸����ѡ��
	function equipMentChange(){
		var newEqpValue=Ext.get('equipmentType').dom.value;
		var actionURL='<ofbiz:url>/getEqpIdAndPeriod</ofbiz:url>?equipmentType='+newEqpValue;
		Ext.lib.Ajax.formRequest('keypartsDataQueryForm',actionURL,{success: commentSuccess, failure: commentFailure});
	}
	
	//�豸modelѡ�� replace("+", "aaa")
	function equipModelChange(){
		debugger;
		var newEqpValue=Ext.get('eqp_Model').dom.value.replace("+", "aaa");
		var actionURL='<ofbiz:url>/getEqpIdByModel</ofbiz:url>?equipmodel='+newEqpValue;
		Ext.lib.Ajax.formRequest('keypartsDataQueryForm',actionURL,{success: commentSuccess, failure: commentFailure});
	}
	 
	//�ı䵯�����С
	extDlg.dlgInit('500','550');
	
	//�޸ĵ���ҳ
	function editKeyPartoffline(obj,keyUseId){
		Ext.get('keyUseId').dom.value=keyUseId;
		//alert(keyUseId);
		var url='<ofbiz:url>/editKeyPartofflineByIndex</ofbiz:url>?keyUseId='+keyUseId;
		extDlg.showEditDialog(obj,url);
		document.KeyPartofflineForm.all.item("offtime").disabled=false;
		document.KeyPartofflineForm.all.item("status").disabled=false;
 
	}
	 
	function partsUserQuery1(obj)
	{    
		var keyUseId= Ext.get('keyUseId').dom.value;
		//Ext.MessageBox.alert(keyUseId);
		debugger;
		var url='<ofbiz:url>/manageKeyPartoffline</ofbiz:url>?keyUseId='+keyUseId;
		//Ext.MessageBox.alert(url); 
		extDlg.showEditDialog(obj,url);
	}
	
	function editSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 alert(result);
		 if (result!=null & result!=""){
		 debugger;
		}
	}
	//Զ�̵���ʧ��
   var editFailure = function(o){
    	Ext.MessageBox.alert('����', 'Unable to connect.');
   };
	
	//Զ�̵��óɹ�
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			   debugger;
				Ext.get('keyUseId').dom.value=result.keyUseId;
				Ext.get('eqpId').dom.value=result.eqpId;
			}
           //�豸ID���ݳ�ʼ��
			var equipId=document.getElementById("equipmentId");
			var eqpArray=result.eqpIdArray;
			var eqpIdSize=result.eqpIdArray.length;
			equipId.length=1;
			for(var i=0;i<eqpIdSize;i++){
				equipId.options[equipId.length]=new Option(eqpArray[i],eqpArray[i]);
			}
			var keydesc=document.getElementById("keydesc");
			var keydescArray=result.keydescArray;
			var keydescSize=result.keydescArray.length;
			keydesc.length=1;
			for(var i=0;i<keydescSize;i++){
				keydesc.options[keydesc.length]=new Option(keydescArray[i],keydescArray[i]);
			}
		var obj=document.getElementById('keydesc');
		obj.value='<%=keydesc%>' 
		var obj=document.getElementById('equipmentId');
		obj.value='<%=equipmentId%>' 
		 
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
	 	
	 	var offtime = new Ext.form.DateField({
	 		format: 'Y-m-d',
	        allowBlank:true
	    });
	  
	    startDate.applyTo('startDate');
	    endDate.applyTo('endDate');
	    offtime.applyTo('offtime');
	    	   
	    
	    var deptType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'eqp_Dept',
	        width:170,
	        forceSelection:true
	    });
	    deptType.on('select',deptChange);
	    
	 });
	 function deptChange(){
		debugger;
		var newDeptValue=Ext.get('eqp_Dept').dom.value;
		//alert(newDeptValue);
		var actionURL='<ofbiz:url>/getSectionAndModelByDeptIndex</ofbiz:url>?deptIndex='+newDeptValue;
		Ext.lib.Ajax.formRequest('keypartsDataQueryForm',actionURL,{success: commentSuccess_d, failure: commentFailure});
	 }
	function commentSuccess_d(o){
		 var result = eval('(' + o.responseText + ')');
			var deptSection=document.getElementById("deptSection");
			var sectionArray=result.sectionArray;
			var sectionSize=result.sectionArray.length;
			deptSection.length=1;
			for(var i=0;i<sectionSize;i++){
				deptSection.options[deptSection.length]=new Option(sectionArray[i],sectionArray[i]);
			}
			var eqpModel=document.getElementById("eqp_Model");
			var modelArray=result.modelArray;
			var modelSize=result.modelArray.length;
			eqpModel.options.length=1;
			for(var i=0;i<modelSize;i++){
				eqpModel.options[eqpModel.length]=new Option(modelArray[i],modelArray[i]);
			} 
		var sectionobj=document.getElementById('deptSection');
		sectionobj.value='<%=section%>'
		var obj=document.getElementById('eqp_Model');
		obj.value='<%=eqpmodel%>'
		equipModelChange();
		 
	}
	
	 
</script>
<form action="<%=request.getContextPath()%>/control/queryKeyPartsUseByCondition" method="post" id="keypartsDataQueryForm" name="keypartsDataQueryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�ؼ�������ѯ����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2"> 
	     <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb">����:</td>
	       <td width="28%"><input type="text" ID="startDate" NAME="startDate" value="<%=sDate %>" readonly></td>
	    <td width="12%" class="en11pxb">��:</td>
	    <td width="28%"><input type="text" ID="endDate" NAME="endDate"  value="<%=eDate %>" readonly></td>
	    </tr>
	     <tr bgcolor="#DFE1EC"> 
	        <td width="12%" class="en11pxb">����:</td>
		    <td>          		
         		<select id="eqp_Dept" name="eqp_Dept">
          			<option value=''></option>
	          		<ofbiz:if name="equipDeptList">
		        		<ofbiz:iterator name="EquipmentDept" property="equipDeptList">
			    			<option value='<ofbiz:inputvalue entityAttr="EquipmentDept" field="deptIndex"/>'><ofbiz:inputvalue entityAttr="EquipmentDept" field="equipmentDept"/></option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
    			</select>          		
          </td>
          <td width="12%" class="en11pxb">�α�:</td>
	      <td width="28%"><select id="deptSection" name="deptSection">
	          <option value=""></option>
    		</select></td>
	    </tr>
	    </tr>
	     <tr bgcolor="#DFE1EC"> 
	        <td width="12%" class="en11pxb">eqpModel:</td>
		    <td>          		
         		<select id="eqp_Model" name="eqp_Model" onchange="equipModelChange()">
          			<option value=''></option>
	          		<ofbiz:if name="equipMoelList">
		        		<ofbiz:iterator name="EquipmentModel" property="equipMoelList">
			    			<option value='<ofbiz:inputvalue entityAttr="EquipmentModel" field="model"/>'><ofbiz:inputvalue entityAttr="EquipmentModel" field="model"/></option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
    			</select>          		
          </td>
	    <td width="12%" class="en11pxb">EqpId:</td>
	    <td width="28%"><select id="equipmentId" name="equipmentId">
	          <option value=""></option>
    		</select></td>
	    </tr>
	    <tr bgcolor="#DFE1EC"> 
		    <td width="12%" class="en11pxb">�ؼ���:</td>
		    <td width="28%" ><select id="keydesc" name="keydesc">
	          <option value=""></option>
    		</select></td>
		      <td width="12%" class="en11pxb">�豸����:</td>
	       <td width="28%"><select id="equipmentType" name="equipmentType">
	          <option value=""></option>
	          <ofbiz:if name="equipMentList">
		        <ofbiz:iterator name="cust" property="equipMentList">
			    <option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentType"/>'><ofbiz:inputvalue entityAttr="cust" field="equipmentType"/></option>
		      </ofbiz:iterator>
	      	</ofbiz:if>
    		</select></td>
		   </tr>
	 	<tr bgcolor="#DFE1EC">
		    <td width="12%" class="en11pxb">����:</td>
		    <td width="28%" >
			    <select id="isalarm" name="isalarm">
		          <option value=""></option>
		          <option value="Y">Y</option>          
	    		</select>
    		</td>
          <td width="12%" class="en11pxb">�Ƿ���</td>
	      <td width="28%">    
         	<select id="isError" name="isError">
	          <option value=""></option>
	          <option  <% if(UtilFormatOut.checkNull(isError).equals("Y")) { %>  selected <% }%> value="Y">Y</option>  
    		</select>       		
          </td>
		   </tr>
		   <tr bgcolor="#DFE1EC">
		    <td width="12%" class="en11pxb">����:</td>
	        <td width="28%"><select id="isdelay" name="isdelay">
	          <option value=""></option>
	          <option value="Y">Y</option>
		      <option value="N">N</option>	          
    		</select></td>
		    <td width="12%" class="en11pxb">�ϻ���</td>
	        <td width="28%">
		        <select id="isUsed" name="isUsed">
		          <option value=""></option>
		          <option  <% if(UtilFormatOut.checkNull(isUsed).equals("Y")) { %>  selected <% }%> value="Y">Y</option>  
		          <option  <% if(UtilFormatOut.checkNull(isUsed).equals("N")) { %>  selected <% }%> value="N">N</option>  
	    		</select>     
	        </td>    		
		   </tr>
	 
	    <tr bgcolor="#DFE1EC">
	   		<td width="20%" class="en11pxb" align="left" colspan="4">
		    	<table border="0" cellspacing="0" cellpadding="0">
				  <tr height="30" >
				   	<td width="0">&nbsp;</td>
				    <td><ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:partsUserQuery();"><span>&nbsp;��ѯ&nbsp;</span></a></li> 
					</ul><ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:reset();"><span>&nbsp;����&nbsp;</span></a></li> 
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

<ofbiz:if name="flag" value="OK">
<div id="queryKeyPartsUseByCondition" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="queryKeyPartsUseByCondition" style="visibility:'hidden';">
</ofbiz:unless>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�ؼ�����ʹ�ü�¼�б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb">�豸Group</td>
          <td width="5%" class="en11pxb">EqpID</td>
          <td width="10%" class="en11pxb">�Ϻ�</td>
          <td width="15%" class="en11pxb">��������</td> 
          <td width="15%" class="en11pxb">�ؼ���</td>
          <td width="15%" class="en11pxb">���</td>
          <td width="15%" class="en11pxb">��������</td> 
          <td width="5%" class="en11pxb">Alarmlife</td>
          <td width="5%" class="en11pxb">Warminglife</td>
          <td width="5%" class="en11pxb">���ڴ���</td>
          <td width="5%" class="en11pxb">��������</td>
          <td width="5%" class="en11pxb">Actual</td> 
          <td width="10%" class="en11pxb">�ϻ�ʱ��</td>
          <td width="5%" class="en11pxb">Vendor</td>
          <td width="5%" class="en11pxb">SeriesNo.</td>
          <td width="5%" class="en11pxb">baseS/N</td>
          <td width="5%" class="en11pxb">�»�ʱ��</td>
          <td width="5%" class="en11pxb">Status</td>
          <td width="5%" class="en11pxb">������</td>
          <td width="10%" class="en11pxb">REMARK</td>      
        </tr>
       <% if(partList != null && partList.size() > 0) {  
       		for(Iterator it = partList.iterator();it.hasNext();) { 
					Map map = (Map)it.next();
					String partsType=(String)map.get("PARTS_TYPE");
					if(partsType!=null&&partsType.equals("DELAY")){
						partsType="����";
					}
					if( Float.parseFloat((String)map.get("ACTUL")) >  (Float.parseFloat((String)map.get("ERROR_SPEC"))+Float.parseFloat((String)map.get("DELAY_LIFE"))) ){%> 
						<tr bgcolor= "OrangeRed">  
					<% }else if( Float.parseFloat((String)map.get("ACTUL")) >=  (Float.parseFloat((String)map.get("WARN_SPEC"))+Float.parseFloat((String)map.get("DELAY_LIFE"))) ){%>
						 <tr bgcolor= "PINK"> 
					<% }else if(Integer.parseInt((String)map.get("DELAYTIME"))>0){%>
						 <tr bgcolor= "YELLOW"> 
					<%}else{%> 
						 <tr bgcolor= "#DFE1EC">  
					<% } %>
				  
			 
		          <td class="en11px"><%=map.get("EQUIPMENT_TYPE")%></td>
		          <td class="en11px"><%=map.get("EQP_ID")%></td>
		          <!--  <td class="en11px"><a href="#" onclick="editKeyPartoffline(this,'<%=map.get("KEY_USE_ID")%>')"><%=map.get("PART_NO")%></td>-->
		          <td class="en11px"><%=map.get("PARTS_ID")%></td>
		          <td class="en11px"><%=map.get("PARTS_NAME")%></td>		          
		          <td class="en11px"><%=map.get("KEYDESC")%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull(partsType)%></td>
		          <td class="en11px"><%=map.get("LIMIT_TYPE")%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("ERROR_SPEC"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("WARN_SPEC"))%></td>
		          <td class="en11px">
		          <%
		          String delaytime=(String)map.get("DELAYTIME");
		          if(delaytime.equals("0")){
		          %>
		          	<%=map.get("DELAYTIME")%>
		          <%}else{%>
		          	<a href="#" onclick="keyPartsDelayQuery('<%=map.get("KEY_USE_ID")%>');"><%=map.get("DELAYTIME")%></a>
		          <%}%>
		          </td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("DELAY_LIFE"))%></td>
		          <td class="en11px"><% if(map.get("lifeError")!=null ){ %><%=map.get("lifeError")%><% }else{ %><%=map.get("ACTUL")%><% } %></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("UPDATETIME"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("VENDOR"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("SERIES_NO"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("BASE_SN"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("CREATE_TIME"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("STATUS"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("TRANS_BY"))%></td>
		           <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("REMARK"))%></td>
		        </tr>
	  <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>

<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">�ؼ������»��趨</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="�»�����">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/manageKeyPartoffline" method="post" id="KeyPartofflineForm" onsubmit="return false;">
                	<input id="keyUseId"  type="hidden" name="keyUseId" value="" />
                	<input id="startDate"   type="hidden"  name="startDate" value="<%=sDate %>" />
                	<input id="endDate"    type="hidden" name="endDate" value="<%=eDate %>" />
                <p>
                <label for="name"><small>EqpId</small></label>
                <input class="textinput" type="text" name="eqpId" id="eqpId" value="" size="22" tabindex="1" /> 
                </p>
                <p> 
                <label for="name"><small>�»�ʱ��</small></label>
                 <table><tr><td>
                 <input type="text" ID="offtime" NAME="offtime" value="" readonly>
                 </td></tr></table> 
          	    </p>
          	     <p>
                <label for="name"><small>״̬</small></label>               
                <select name = "status" id="status" class="select" >
          			<option value='HOLD'>HOLD</option>
          			<option value='SCRAP'>SCRAP</option>
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
		var obj=document.getElementById('equipmentType');
		obj.value='<%=equ%>'
		equipModelChange();
   		var sDateObj=document.getElementById('startDate');
   		sDateObj.value='<%=sDate%>';
   		var eDateObj=document.getElementById('endDate');
   		eDateObj.value='<%=eDate%>';
   		var obj=document.getElementById('equipmentId');
		obj.value='<%=equipmentId%>'
		var deptobj=document.getElementById('eqp_Dept');
		deptobj.value='<%=deptIndex%>'
		deptChange();
		var isalarmobj=document.getElementById('isalarm');
		isalarmobj.value='<%=isalarm%>'
		var isdelayobj=document.getElementById('isdelay');
		isdelayobj.value='<%=isdelay%>'
</script>