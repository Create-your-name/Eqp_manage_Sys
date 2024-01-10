<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.Map" %>
<%@ page  import="com.csmc.pms.webapp.util.CommonUtil"%>
<%

	Map paramMap = (Map)request.getAttribute("paramMap");
	String activate=(String)request.getAttribute("activate");
	GenericValue pmForm=(GenericValue)request.getAttribute("pmForm");
	String equipmentId = (String)pmForm.getString("equipmentId");
	String periodIndex = (String)pmForm.getString("periodIndex");
	GenericValue defaultPeriod = (GenericValue)request.getAttribute("defaultPeriod");
	List eqpStatusList=(List)request.getAttribute("eqpStatusList");
	String formType=pmForm.getString("formType");
	int status=Integer.parseInt(pmForm.getString("status"));
	List jobList = (List)request.getAttribute("jobList");
	String displayStatus="";
	if(Constants.CREAT==status||Constants.START==status||Constants.HOLD==status){
	    displayStatus="δ���";
	}else{
	    displayStatus="���";
	}
	String overtimeReasonIndex=UtilFormatOut.checkNull((String)pmForm.getString("overtimeReasonIndex"));
	String endTime="";
	if(pmForm.getTimestamp("endTime")!=null){
	    endTime=CommonUtil.toGuiDate(pmForm.getTimestamp("endTime"),"yyyy-MM-dd HH:mm:ss");
	}
	String startTime="";
	if(pmForm.getTimestamp("startTime")!=null){
	    startTime=CommonUtil.toGuiDate(pmForm.getTimestamp("startTime"),"yyyy-MM-dd HH:mm:ss");
	}

	String jobStr = "";
	for(int i=0;i<jobList.size();i++){
		Map job = (Map)jobList.get(i);
		if (i==0){
		    jobStr = job.get("SEQ_INDEX").toString();
		}else{
	    	jobStr = jobStr+":"+job.get("SEQ_INDEX");
		}
	}
%>
<%@ include file="../../yui-ext/ext-comdlg.jsp"%>
<script language="javascript">
	//false:����̳���δ�������;�������״̬Ϊ��true
	var dealJobStatus=false;
    var eventSub;
    var event;
    var anormalReason;
 	var overTimeIndexObj;
   	//�ı䵯�����Сwidth,height
	extDlg.dlgInit('500','300');

	//�������ܵ���ҳ
	function addParts(obj){
		getPartsNo();
	}

	//�޸Ĺ��ܵ���ҳ
	function editParts(obj,seqIndex){
		//Ext.MessageBox.confirm('���ݴ�ȷ��', '���޸�partsǰ����[�ݴ�]��,����ᵼ�±���Ϣ��ʧ!',function result(value){
		//if(value=="yes"){
	        	Ext.get('seqIndex').dom.value=seqIndex;
				var url='<ofbiz:url>/partsUseInfoQueryEntry</ofbiz:url>?seqIndex='+seqIndex;
				extDlg.showEditDialog(obj,url);
		//	}else{
		//		return;
		//	}
	     //});
	}

	//�޸�ʱԶ�̵��óɹ����ҳ�渳ֵ
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('partNo').dom.value=result.partNo;
			Ext.get('partName').dom.value=result.partName;
			Ext.get('partCount').dom.value=result.partCount;
		}
	}
   function runJob(jobRelationIndex) {
       var url = "<ofbiz:url>/runFlow</ofbiz:url>?jobRelationIndex=" + jobRelationIndex;
       window.open(url,"job","top=130,left=240,width=600,height=400,title=,channelmode=0," +
           "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
           "scrollbars=1,status=1,titlebar=0,toolbar=no");
   }

   function editJob(jobRelationIndex) {
       var url = "<ofbiz:url>/editFlowData</ofbiz:url>?jobRelationIndex=" + jobRelationIndex;
       window.open(url,"job","top=130,left=240,width=600,height=400,title=,channelmode=0," +
           "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
           "scrollbars=1,status=1,titlebar=0,toolbar=no");
   }


   //���(������ڴ��������ִ����󣬷��ɲ���)
   function overFormRecord(){

  		var actionURL='<ofbiz:url>/pmFormJobStatusQueryEntry</ofbiz:url>?jobStr=<%=jobStr%>';
		Ext.lib.Ajax.formRequest('pmForm',actionURL,{success: commentQueryJobStatusSuccess, failure: commentFailure});
   }

	//��ѯJOB״̬Զ�̵��óɹ�
	function commentQueryJobStatusSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			var status_job=result.jobStatus;
			//���
			if(status_job=="1"){
				dealJobStatus=true;
			}else{
				dealJobStatus=false;
			}

			var errorMsg=checkPmForm('1');
	   		if(errorMsg!=""){
	   			Ext.MessageBox.alert('����', errorMsg);
	   			return;
	   		}
	   		Ext.MessageBox.confirm('�޸ı�ȷ��', '��ȷ��Ҫ�޸Ĵ˱�������',function result(value){
		        if(value=="yes"){
					var actionURL='<ofbiz:url>/editPmFormByLeader</ofbiz:url>';
					loading();
					pmForm.action=actionURL;
					pmForm.submit();
				}else{
					return;
				}
	        });
		}
	}

	//ɾ��������Ϣ
	function delPartsUse(seqIndex){
	 Ext.MessageBox.confirm('ɾ��ȷ��', 'ȷ��Ҫɾ��������ǰ������[�ݴ�]��,����ᵼ�±���Ϣ��ʧ!',function result(value){
	        if(value=="yes"){
				var eqpId='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>'
				var pmIndex='<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>'
				var actionURL='<ofbiz:url>/deletePmPartsByLeader</ofbiz:url>?functionType=1&seqIndex='+seqIndex+'&eqpId='+eqpId+"&pmIndex="+pmIndex;
				loading();
				document.location.href=actionURL;
			}else{
				return;
			}
        });
	}

   //�ݴ�
   function tempSaveForm(){
		var errorMsg=checkPmForm();
   		if(errorMsg!=""){
   			Ext.MessageBox.alert('����', errorMsg);
   			return;
   		}
	   loading();
	   pmForm.submit();
	}


	//��ȡpartNo
	function getPartsNo(obj){
		var par=new Array();
		//�豸ID��formTypeֵΪPMʱ��Ҫ
		//PM����,formTypeֵΪPMʱ��Ҫ
		/*var result=window.showModalDialog ("<%=request.getContextPath()%>/control/intoPmFormPartsQueryEntry?formType=PM&eqpId=<%=equipmentId%>&periodIndex=<%=periodIndex%>&eventIndex=<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>",par,"dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");*/

		// mcs interface: pm/ts using parts
		var result = window.showModalDialog("<%=request.getContextPath()%>/control/useMaterialByQtyEntry?eventType=PM&eqpId=<%=equipmentId%>&eventIndex=<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>", window, "dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");

		document.location.href="<%=request.getContextPath()%>/control/queryPmformByLeader?functionType=1&activate=parts&pmIndex=<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>&eqpId=<%=equipmentId%>";
	}

	//��ѯ�豸״̬
	function queryEqpStatus(){
		var eqpId='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
		var actionURL='<ofbiz:url>/pmFormEqpStatusQueryEntry</ofbiz:url>?eqpId='+eqpId;
		//var actionURL='<ofbiz:url>/pmFormEqpStatusQueryEntry</ofbiz:url>?eqpId=MCS7280';
		Ext.lib.Ajax.formRequest('pmForm',actionURL,{success: commentQuerySuccess, failure: commentFailure});
	}

	//Զ�̵��óɹ�
	function commentQuerySuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			var status=result.status;
			if("success"==status){
				var eqpStatus=result.eqpStatus;
				Ext.get('curEqpStatus').dom.innerText=eqpStatus;
			}else if("error"==status){
				var message=result.message;
				setErrorMsg(message);
			}
		}
	}

	//�޸��豸״̬
	function updateEqpStatus(){
		var eqpId='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
		var eqpState=Ext.get('eqpState').dom.value;
		if(eqpState==''){
			Ext.MessageBox.alert('����', "��ѡ���豸״̬!");
   			return;
		}
		var actionURL='<ofbiz:url>/pmFormEqpStatusUpdateEntry</ofbiz:url>?eqpId='+eqpId+'&eqpState='+eqpState;
		Ext.lib.Ajax.formRequest('pmForm',actionURL,{success: commentUpdateSuccess, failure: commentFailure});
	}

	//Զ�̵��óɹ�
	function commentUpdateSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			var status=result.status;
			var message=result.message;
			if("success"==status){
				setSuccessMsg(message);
			}else if("error"==status){
				setErrorMsg(message);
			}
		}
	}

	//Զ�̵���ʧ��
   var commentFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
   };

   //pmForm����У��
   function checkPmForm(flag){
   		if (Ext.get('formType').dom.value=="NORMAL"){
   			var day = new Date();
   			//alert(day);
   			Ext.get('returnTime').dom.value=day.getYear()+"-"+(parseInt(day.getMonth())+1)+"-"+day.getDate()+" "+ day.getHours()+":"+day.getMinutes()+":"+day.getSeconds();
   		}

   		var startTime=Ext.get('startTime').dom.value;
   		var returnTime=Ext.get('returnTime').dom.value;
   		var errorStr="";
   		if(startTime==""){
   			errorStr='������ʼʱ�䲻��Ϊ��!';
   			return errorStr;
   		}
   		if(returnTime==""){
   			errorStr='����ʱ�䲻��Ϊ��!';
   			return errorStr;
   		}

   		if(startTime.indexOf(":")==-1){
   			startTime=startTime+" 00:00:00";
   		}

   		//var hourElapsed=(returnTime.getTime()-startTime22534.getTime())/1000/3600;
   		//if(hourElapsed<0){
   			//errorStr='����ʱ�䲻��С���쳣����ʱ��!';
   			//return errorStr;
   		//}

   		var startDate=Date.parseDate(startTime, "Y-m-d h:i:s");
   		var returnDate=Date.parseDate(returnTime, "Y-m-d h:i:s");

		var jobText=Ext.get('jobText').dom.value;

		//�ݴ�ʱ����ҪУ��
		if(flag=="1"){
 			if(dealJobStatus==false){
 				errorStr='����Ѵ���������!';
 				return errorStr;
 			}
		}
		if(jobText==""){
				errorStr='������[�������]�еĴ������!';
				return errorStr;
		}
   		return errorStr;
   }

	//�½�����׷��
   function saveFollowJob(){
   		var url='<ofbiz:url>/intoAddQuFollow</ofbiz:url>?type=FORM&eventType=PM&eventIndex=<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>&eqpId=<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
		windowOpen(url,'�½�����׷��',685,400);
    }

   //���ݺϷ���У��
  function checkForm(){
		var partNo = Ext.get('partNo').dom.value;
        var partName = Ext.get('partName').dom.value;
        var partCount=Ext.get('partCount').dom.value;
		if(partNo==""){
			return "���ϺŲ���Ϊ��";
		}
		if(partName==""){
			return "����������Ϊ��";
		}
		if(partCount==""){
			return "��������Ϊ��";
		}
		return "";
	}

    function enterJobText(){
   		//Ext.get('jobText').dom.value=Ext.get('jobContent').dom.value;
    }

	function overTimeReason(){
    	if(overTimeIndexObj.getDisplayField(overTimeIndexObj.getValue())=="other"){
    		Ext.get('remark').dom.style.display='';
    	}
    	else{
    		Ext.get('remark').dom.style.display='none';
    	}
	}

   //ҳ��initʱ
   Ext.onReady(function(){
	    //ע���¼�����
	    overTimeIndexObj = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'overTimeIndex',
	        width:170,
	        forceSelection:true
	    });

	    overTimeIndexObj.setValue('<%=overtimeReasonIndex%>');
	    overTimeIndexObj.on('select',overTimeReason);
	   	if(overTimeIndexObj.getDisplayField(overTimeIndexObj.getValue())=="other"){
	   		Ext.get('remark').dom.style.display='';
	   	}
	   	else{
	   		Ext.get('remark').dom.style.display='none';
	   	}


   		//����ʱ��ע��
   		if (Ext.get('formType').dom.value=="PATCH"){
			var returnTime = new Ext.form.DateTimeField({
	 			format: 'Y-m-d H:i:s',
	       		allowBlank:true
	   		});
	 		returnTime.applyTo('returnTime');

			var startTime = new Ext.form.DateTimeField({
	 			format: 'Y-m-d H:i:s',
	       		allowBlank:true
	   		});
	 		startTime.applyTo('startTime');
			//returnTime.setValue('<%=endTime%>');
   		}


	   //ע��sheet
	   var tabs = new Ext.TabPanel('tabs');
       var pmInfo = tabs.addTab('pmInfo', "������Ϣ");
       var flow = tabs.addTab('flow', "�������");
       var parts = tabs.addTab('parts', "ʹ�����");
       <% if(("parts").equals(activate)){%>
       		tabs.activate('parts');
       <%}else{%>
       		tabs.activate('pmInfo');
       <%}%>

   });
</script>
<form action="<%=request.getContextPath()%>/control/queryPmformByLeader" method="post" id="pmForm">
<input type="hidden" name="pmIndex" id="pmIndex" value='<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>'>
<input type="hidden" name="formType" id="formType" value='<ofbiz:inputvalue entityAttr="pmForm" field="formType"/>'>
<input type="hidden" name="eqpId" id="eqpId" value='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>'>
<input type="hidden" name="jobContent" id="jobContent" value='<ofbiz:inputvalue entityAttr="pmForm" field="jobText"/>'>
<input type="hidden" name="functionType" id="functionType" value="1"/>
<fieldset><legend>������¼��</legend>
<div id="tabs">
	<div id="pmInfo" class="tab-content">
	    <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td width="20%" bgcolor="#ACD5C9" class="en11pxb">EQPID</td>
	          <td width="30%" class="en11px"><ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/></td>
	          <td width="20%" bgcolor="#ACD5C9" class="en11pxb">����</td>
	          <td width="30%" class="en11px"><%=(String)paramMap.get("accountDept")%></td>
	        </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">�α�</td>
	          <td class="en11px"><%=(String)paramMap.get("accountSection")%></td>
	          <td bgcolor="#ACD5C9" class="en11pxb">׫дʱ��</td>
	          <td class="en11px"><ofbiz:inputvalue entityAttr="pmForm" field="createTime"/></td>
	        </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">׫д��</td>
	          <td class="en11px"><ofbiz:inputvalue entityAttr="pmForm" field="createUser"/></td>
	          <td class="en11pxb" bgcolor="#ACD5C9">�ļ����</td>
	          <td class="en11px"><ofbiz:inputvalue entityAttr="pmForm" field="pmName"/></td>
	        </tr>

            <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">��ʼʱ��</td>
	          <td class="en11px"><input type="text" class="input" ID="startTime" NAME="startTime" value='<%=startTime%>' readonly></td>
	          <td bgcolor="#ACD5C9" class="en11pxb">����ʱ��</td>
	          <td class="en11px"><input type="text" class="input" ID="returnTime" NAME="returnTime" readonly size="26" value="<%=endTime%>"></td>
	          </tr>

          <tr bgcolor="#DFE1EC">
            <td class="en11pxb" bgcolor="#ACD5C9">������ʱ/ʱ�����ԭ��</td>
            <td>
            <select id="overTimeIndex" name="overTimeIndex" class="select" onclick="return overTimeReason();">
            	<option value=''></option>
		         <ofbiz:if name="overTimeList">
			       <ofbiz:iterator name="overTime" property="overTimeList">
				   <option value='<ofbiz:inputvalue entityAttr="overTime" field="reasonIndex"/>'><ofbiz:inputvalue entityAttr="overTime" field="reason"/></option>
			     	</ofbiz:iterator>
		      	 </ofbiz:if>
	      	</select>
	      	</td>
	      	<td class="en11pxb" bgcolor="#ACD5C9">��ʱ��ע</td>
	      	<td><div id="remark" style="display:none"><input type="text" ID="comment" NAME="comment" value='<ofbiz:inputvalue entityAttr="pmForm" field="overtimeComment"/>'></div></td>
          </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">PM���</td>
	          <td class="en11px"><input type="text" ID="periodName" NAME="periodName" readonly size="20" class="input" value='<ofbiz:inputvalue entityAttr="defaultPeriod" field="periodName"/>'></td>
	          <td bgcolor="#ACD5C9" class="en11pxb">��״̬</td>
	          <td class="en11px"><input type="text" ID="formStatus" NAME="formStatus" readonly size="20" class="input" value='<ofbiz:inputvalue entityAttr="defaultPeriod" field="eqpStatus"/>'></td>
	        </tr>
          <%if("NORMAL".equals(formType)){%>
          <tr bgcolor="#DFE1EC">
            <td bgcolor="#ACD5C9"><ul class="button">
                      <li><a class="button-text" href="#" onclick="queryEqpStatus();"><span>&nbsp;��ѯĿǰ�豸״̬&nbsp;</span></a></li>
              </ul></td>
            <td class="en11px" colspan="3">&nbsp;<span id='curEqpStatus'></span></td>

          </tr>
          <% }%>
        </table>
  </div>

  <div id="flow" class="tab-content">
   <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
   		<tr bgcolor="#DFE1EC">
   			<td width="14%" rowspan="4" bgcolor="#ACD5C9" class="en11pxb">������</td>
   			<td colspan="2" rowspan="4" class="en11px">
   				<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
   					<% for(int i=0;i<jobList.size();i++) {
						Map job = (Map)jobList.get(i); %>
   					<tr bgcolor="#DFE1EC">
   						<td class="en11px" width="35%"><a href="#" onclick="runJob('<%=job.get("SEQ_INDEX")%>')"><%=job.get("JOB_NAME")%></a></td>
   						<td class="en11px" width="65%"><a href="#" onclick="editJob('<%=job.get("SEQ_INDEX")%>')">�޸�</a></td>
   					</tr>
   					<% } %>
   				</table>
   			</td>
		</tr>
   </table>
   <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
       <tr bgcolor="#DFE1EC">
         <td width="14%" rowspan="4" bgcolor="#ACD5C9" class="en11pxb">�������</td>
         <td colspan="2" rowspan="4" class="en11px"><label>
           <textarea name="jobText" id="jobText" cols="60" rows="10"><ofbiz:inputvalue entityAttr="pmForm" field="jobText"/></textarea>
         </label></td>
         <!--
           <td width="42%" class="en11px"><ul class="button">
                     <li><a class="button-text" href="#" onclick="enterJobText();"><span>&nbsp;���봦�����&nbsp;</span></a></li>
             </ul></td>

           -->
       </tr>
     </table>
  </div>

  <div id="parts" class="tab-content">
   <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
      <tr bgcolor="#ACD5C9">
         <td width="2%"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addParts(this);"/></td>
         <td width="30%" class="en11pxb">Parts���</td>
         <td width="30%" class="en11pxb">PartsƷ�����</td>
         <td width="20%" class="en11pxb">Parts����</td>
         <td width="18%" class="en11pxb">����</td>
       </tr>
       <ofbiz:if name="PARTS_USE_LIST">
		  <ofbiz:iterator name="parsUse" property="PARTS_USE_LIST" type="java.util.Map">
	       <tr bgcolor="#DFE1EC">
	         <td><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delPartsUse('<ofbiz:inputvalue entityAttr="parsUse" field="seqIndex"/>')"/></td>

	         <td class="en11px"><ofbiz:entityfield attribute="parsUse" field="PART_NO"/></td>
	         <td class="en11px"><ofbiz:entityfield attribute="parsUse" field="PART_NAME"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="parsUse" field="PART_TYPE"/></td>
				<td class="en11px"><ofbiz:entityfield attribute="parsUse" field="PART_COUNT"/></td>
	       </tr>
	     </ofbiz:iterator>
	  </ofbiz:if>
     </table>
  </div>
</div>
</fieldset>
</form>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" id="overForm" href="#" onclick="overFormRecord();"><span>&nbsp;�޸�&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" id="followJob" href="#" onclick="saveFollowJob();"><span>&nbsp;��������׷��&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">�޸�Parts</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="�޸�Parts">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/updatePmPartsByLeader" method="post" id="partsForm" onsubmit="return false;">
                	<input id="activate" type="hidden" name="activate" value="parts" />
                	<input id="partType" type="hidden" name="partType" value="" />
                	<input id="seqIndex" type="hidden" name="seqIndex" value="" />
                	<input id="functionType" type="hidden" name="functionType" value='1'/>
                	<input id="eqpId" type="hidden" name="eqpId" value="<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>" />
                	<input id="pmIndex" type="hidden" name="pmIndex" value="<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>" />
                <p>
                <label for="name"><small>���Ϻ�</small></label>
                <input class="textinput" type="text" name="partNo" id="partNo" value="" size="22" tabindex="1" readonly="readonly"/>
                </p>
                <p><label for="description"><small>������</small></label>
                <input class="textinput" type="text" name="partName" id="partName" value="" size="22" tabindex="2" readonly="readonly"/>
                </p>
                <p><label for="description"><small>����</small></label>
                <input class="textinput" type="text" name="partCount" id="partCount" value="" size="22" tabindex="3" />
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

