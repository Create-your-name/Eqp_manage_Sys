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
	    displayStatus="未完成";
	}else{
	    displayStatus="完成";
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
	//false:处理程程序未处理完毕;处理完毕状态为：true
	var dealJobStatus=false;
    var eventSub;
    var event;
    var anormalReason;
 	var overTimeIndexObj;
   	//改变弹出框大小width,height
	extDlg.dlgInit('500','300');

	//新增功能弹出页
	function addParts(obj){
		getPartsNo();
	}

	//修改功能弹出页
	function editParts(obj,seqIndex){
		//Ext.MessageBox.confirm('表单暂存确认', '在修改parts前请您[暂存]表单,否则会导致表单信息丢失!',function result(value){
		//if(value=="yes"){
	        	Ext.get('seqIndex').dom.value=seqIndex;
				var url='<ofbiz:url>/partsUseInfoQueryEntry</ofbiz:url>?seqIndex='+seqIndex;
				extDlg.showEditDialog(obj,url);
		//	}else{
		//		return;
		//	}
	     //});
	}

	//修改时远程调用成功后给页面赋值
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


   //完成(如果存在处理程序，须执行完后，方可操作)
   function overFormRecord(){

  		var actionURL='<ofbiz:url>/pmFormJobStatusQueryEntry</ofbiz:url>?jobStr=<%=jobStr%>';
		Ext.lib.Ajax.formRequest('pmForm',actionURL,{success: commentQueryJobStatusSuccess, failure: commentFailure});
   }

	//查询JOB状态远程调用成功
	function commentQueryJobStatusSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			var status_job=result.jobStatus;
			//完成
			if(status_job=="1"){
				dealJobStatus=true;
			}else{
				dealJobStatus=false;
			}

			var errorMsg=checkPmForm('1');
	   		if(errorMsg!=""){
	   			Ext.MessageBox.alert('警告', errorMsg);
	   			return;
	   		}
	   		Ext.MessageBox.confirm('修改表单确认', '您确信要修改此保养表单吗？',function result(value){
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

	//删除物料信息
	function delPartsUse(seqIndex){
	 Ext.MessageBox.confirm('删除确认', '确信要删除此物料前，请您[暂存]表单,否则会导致表单信息丢失!',function result(value){
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

   //暂存
   function tempSaveForm(){
		var errorMsg=checkPmForm();
   		if(errorMsg!=""){
   			Ext.MessageBox.alert('警告', errorMsg);
   			return;
   		}
	   loading();
	   pmForm.submit();
	}


	//获取partNo
	function getPartsNo(obj){
		var par=new Array();
		//设备ID，formType值为PM时需要
		//PM周期,formType值为PM时需要
		/*var result=window.showModalDialog ("<%=request.getContextPath()%>/control/intoPmFormPartsQueryEntry?formType=PM&eqpId=<%=equipmentId%>&periodIndex=<%=periodIndex%>&eventIndex=<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>",par,"dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");*/

		// mcs interface: pm/ts using parts
		var result = window.showModalDialog("<%=request.getContextPath()%>/control/useMaterialByQtyEntry?eventType=PM&eqpId=<%=equipmentId%>&eventIndex=<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>", window, "dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");

		document.location.href="<%=request.getContextPath()%>/control/queryPmformByLeader?functionType=1&activate=parts&pmIndex=<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>&eqpId=<%=equipmentId%>";
	}

	//查询设备状态
	function queryEqpStatus(){
		var eqpId='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
		var actionURL='<ofbiz:url>/pmFormEqpStatusQueryEntry</ofbiz:url>?eqpId='+eqpId;
		//var actionURL='<ofbiz:url>/pmFormEqpStatusQueryEntry</ofbiz:url>?eqpId=MCS7280';
		Ext.lib.Ajax.formRequest('pmForm',actionURL,{success: commentQuerySuccess, failure: commentFailure});
	}

	//远程调用成功
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

	//修改设备状态
	function updateEqpStatus(){
		var eqpId='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
		var eqpState=Ext.get('eqpState').dom.value;
		if(eqpState==''){
			Ext.MessageBox.alert('警告', "请选择设备状态!");
   			return;
		}
		var actionURL='<ofbiz:url>/pmFormEqpStatusUpdateEntry</ofbiz:url>?eqpId='+eqpId+'&eqpState='+eqpState;
		Ext.lib.Ajax.formRequest('pmForm',actionURL,{success: commentUpdateSuccess, failure: commentFailure});
	}

	//远程调用成功
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

	//远程调用失败
   var commentFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
   };

   //pmForm数据校验
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
   			errorStr='保养开始时间不能为空!';
   			return errorStr;
   		}
   		if(returnTime==""){
   			errorStr='回线时间不能为空!';
   			return errorStr;
   		}

   		if(startTime.indexOf(":")==-1){
   			startTime=startTime+" 00:00:00";
   		}

   		//var hourElapsed=(returnTime.getTime()-startTime22534.getTime())/1000/3600;
   		//if(hourElapsed<0){
   			//errorStr='回线时间不能小于异常发生时间!';
   			//return errorStr;
   		//}

   		var startDate=Date.parseDate(startTime, "Y-m-d h:i:s");
   		var returnDate=Date.parseDate(returnTime, "Y-m-d h:i:s");

		var jobText=Ext.get('jobText').dom.value;

		//暂存时不需要校验
		if(flag=="1"){
 			if(dealJobStatus==false){
 				errorStr='必须把处理程序完成!';
 				return errorStr;
 			}
		}
		if(jobText==""){
				errorStr='请输入[处理程序]中的处理过程!';
				return errorStr;
		}
   		return errorStr;
   }

	//新建问题追踪
   function saveFollowJob(){
   		var url='<ofbiz:url>/intoAddQuFollow</ofbiz:url>?type=FORM&eventType=PM&eventIndex=<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>&eqpId=<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
		windowOpen(url,'新建问题追踪',685,400);
    }

   //数据合法性校验
  function checkForm(){
		var partNo = Ext.get('partNo').dom.value;
        var partName = Ext.get('partName').dom.value;
        var partCount=Ext.get('partCount').dom.value;
		if(partNo==""){
			return "物料号不能为空";
		}
		if(partName==""){
			return "物料名不能为空";
		}
		if(partCount==""){
			return "数量不能为空";
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

   //页面init时
   Ext.onReady(function(){
	    //注册事件下拉
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


   		//回线时间注册
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


	   //注册sheet
	   var tabs = new Ext.TabPanel('tabs');
       var pmInfo = tabs.addTab('pmInfo', "保养信息");
       var flow = tabs.addTab('flow', "处理程序");
       var parts = tabs.addTab('parts', "使用配件");
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
<fieldset><legend>保养记录表</legend>
<div id="tabs">
	<div id="pmInfo" class="tab-content">
	    <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td width="20%" bgcolor="#ACD5C9" class="en11pxb">EQPID</td>
	          <td width="30%" class="en11px"><ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/></td>
	          <td width="20%" bgcolor="#ACD5C9" class="en11pxb">部门</td>
	          <td width="30%" class="en11px"><%=(String)paramMap.get("accountDept")%></td>
	        </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">课别</td>
	          <td class="en11px"><%=(String)paramMap.get("accountSection")%></td>
	          <td bgcolor="#ACD5C9" class="en11pxb">撰写时间</td>
	          <td class="en11px"><ofbiz:inputvalue entityAttr="pmForm" field="createTime"/></td>
	        </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">撰写人</td>
	          <td class="en11px"><ofbiz:inputvalue entityAttr="pmForm" field="createUser"/></td>
	          <td class="en11pxb" bgcolor="#ACD5C9">文件编号</td>
	          <td class="en11px"><ofbiz:inputvalue entityAttr="pmForm" field="pmName"/></td>
	        </tr>

            <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">开始时间</td>
	          <td class="en11px"><input type="text" class="input" ID="startTime" NAME="startTime" value='<%=startTime%>' readonly></td>
	          <td bgcolor="#ACD5C9" class="en11pxb">结束时间</td>
	          <td class="en11px"><input type="text" class="input" ID="returnTime" NAME="returnTime" readonly size="26" value="<%=endTime%>"></td>
	          </tr>

          <tr bgcolor="#DFE1EC">
            <td class="en11pxb" bgcolor="#ACD5C9">保养超时/时间过短原因</td>
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
	      	<td class="en11pxb" bgcolor="#ACD5C9">超时备注</td>
	      	<td><div id="remark" style="display:none"><input type="text" ID="comment" NAME="comment" value='<ofbiz:inputvalue entityAttr="pmForm" field="overtimeComment"/>'></div></td>
          </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">PM类别</td>
	          <td class="en11px"><input type="text" ID="periodName" NAME="periodName" readonly size="20" class="input" value='<ofbiz:inputvalue entityAttr="defaultPeriod" field="periodName"/>'></td>
	          <td bgcolor="#ACD5C9" class="en11pxb">表单状态</td>
	          <td class="en11px"><input type="text" ID="formStatus" NAME="formStatus" readonly size="20" class="input" value='<ofbiz:inputvalue entityAttr="defaultPeriod" field="eqpStatus"/>'></td>
	        </tr>
          <%if("NORMAL".equals(formType)){%>
          <tr bgcolor="#DFE1EC">
            <td bgcolor="#ACD5C9"><ul class="button">
                      <li><a class="button-text" href="#" onclick="queryEqpStatus();"><span>&nbsp;查询目前设备状态&nbsp;</span></a></li>
              </ul></td>
            <td class="en11px" colspan="3">&nbsp;<span id='curEqpStatus'></span></td>

          </tr>
          <% }%>
        </table>
  </div>

  <div id="flow" class="tab-content">
   <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
   		<tr bgcolor="#DFE1EC">
   			<td width="14%" rowspan="4" bgcolor="#ACD5C9" class="en11pxb">保养表单</td>
   			<td colspan="2" rowspan="4" class="en11px">
   				<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
   					<% for(int i=0;i<jobList.size();i++) {
						Map job = (Map)jobList.get(i); %>
   					<tr bgcolor="#DFE1EC">
   						<td class="en11px" width="35%"><a href="#" onclick="runJob('<%=job.get("SEQ_INDEX")%>')"><%=job.get("JOB_NAME")%></a></td>
   						<td class="en11px" width="65%"><a href="#" onclick="editJob('<%=job.get("SEQ_INDEX")%>')">修改</a></td>
   					</tr>
   					<% } %>
   				</table>
   			</td>
		</tr>
   </table>
   <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
       <tr bgcolor="#DFE1EC">
         <td width="14%" rowspan="4" bgcolor="#ACD5C9" class="en11pxb">处理过程</td>
         <td colspan="2" rowspan="4" class="en11px"><label>
           <textarea name="jobText" id="jobText" cols="60" rows="10"><ofbiz:inputvalue entityAttr="pmForm" field="jobText"/></textarea>
         </label></td>
         <!--
           <td width="42%" class="en11px"><ul class="button">
                     <li><a class="button-text" href="#" onclick="enterJobText();"><span>&nbsp;载入处理程序&nbsp;</span></a></li>
             </ul></td>

           -->
       </tr>
     </table>
  </div>

  <div id="parts" class="tab-content">
   <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
      <tr bgcolor="#ACD5C9">
         <td width="2%"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addParts(this);"/></td>
         <td width="30%" class="en11pxb">Parts编号</td>
         <td width="30%" class="en11pxb">Parts品名规格</td>
         <td width="20%" class="en11pxb">Parts种类</td>
         <td width="18%" class="en11pxb">数量</td>
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
			<li><a class="button-text" id="overForm" href="#" onclick="overFormRecord();"><span>&nbsp;修改&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" id="followJob" href="#" onclick="saveFollowJob();"><span>&nbsp;建立问题追踪&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">修改Parts</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="修改Parts">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/updatePmPartsByLeader" method="post" id="partsForm" onsubmit="return false;">
                	<input id="activate" type="hidden" name="activate" value="parts" />
                	<input id="partType" type="hidden" name="partType" value="" />
                	<input id="seqIndex" type="hidden" name="seqIndex" value="" />
                	<input id="functionType" type="hidden" name="functionType" value='1'/>
                	<input id="eqpId" type="hidden" name="eqpId" value="<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>" />
                	<input id="pmIndex" type="hidden" name="pmIndex" value="<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>" />
                <p>
                <label for="name"><small>物料号</small></label>
                <input class="textinput" type="text" name="partNo" id="partNo" value="" size="22" tabindex="1" readonly="readonly"/>
                </p>
                <p><label for="description"><small>物料名</small></label>
                <input class="textinput" type="text" name="partName" id="partName" value="" size="22" tabindex="2" readonly="readonly"/>
                </p>
                <p><label for="description"><small>数量</small></label>
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

