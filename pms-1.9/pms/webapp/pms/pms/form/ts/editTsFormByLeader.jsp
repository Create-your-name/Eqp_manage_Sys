<%@page contentType='text/html; charset=gb2312'%>
<%@ page import="com.csmc.pms.webapp.util.CommonUtil"%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="java.util.*" %>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%
	String activate=(String)request.getAttribute("activate");
	GenericValue gv=(GenericValue)request.getAttribute("ABNORMAL");
	pageContext.setAttribute("abnormal",gv);
	String section=(String)request.getAttribute("SECTION");
	String accountDept=(String)request.getAttribute("ACCOUNT_DEPT");
	List eqpStatusList=(List)request.getAttribute("EQP_STATUS_LIST");
	List reasonList=(List)request.getAttribute("REASON_List");
	String jobRelationIndex=(String)request.getAttribute("JOB_RELATION_INDEX");
	String inputType=gv.getString("formType");
	int status=Integer.parseInt(gv.getString("status"));
	String displayStatus="";
	if(Constants.CREAT==status||Constants.START==status||Constants.HOLD==status){
		displayStatus="未完成";
	}else{
		displayStatus="完成";
	}
	String abnormalTime="";
	if(gv.getTimestamp("abnormalTime")!=null){
		abnormalTime=CommonUtil.toGuiDate(gv.getTimestamp("abnormalTime"),"yyyy-MM-dd HH:mm:ss");
	}
%>
<%@ include file="../../yui-ext/ext-comdlg.jsp"%>
<script language="javascript">
	//false:处理程程序未处理完毕;处理完毕状态为：true
	var dealJobStatus=false;
    var eventSub;
    var event;
    var anormalReason;
    var tabs;
    var abnormal;
    var flow;
    var parts;
    var index='<ofbiz:inputvalue entityAttr="abnormal" field="subeventIndex"/>';
	var eventSubDef = Ext.data.Record.create([
	      {name: 'eventSubIndex'},
		  {name: 'subCategory'}
  	]);

  	var ds = new Ext.data.Store({
         proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/getEventSubList</ofbiz:url>'}),
         reader: new Ext.data.JsonReader({}, eventSubDef)
    });

   	//事件细项选择
 	function eventSubChange() {
		 var val = event.getValue();
		 ds.load({params:{eventIndex:val},callback:function(){eventSub.setValue(index);index="";}});
   	}

   	//改变弹出框大小width,height
	extDlg.dlgInit('500','300');

	//新增功能弹出页
	function addParts(obj){
		getPartsNo();
	}

	//删除物料信息
	function delPartsUse(seqIndex){
	 Ext.MessageBox.confirm('删除确认', '确信要删除此物料吗?',function result(value){
	        if(value=="yes"){
				var eqpId='<ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/>'
				var abnormalIndex='<ofbiz:inputvalue entityAttr="abnormal" field="abnormalIndex"/>'
				var actionURL='<ofbiz:url>/deleteTsPartsByLeader</ofbiz:url>?activate=parts&functionType=1&seqIndex='+seqIndex+'&eqpId='+eqpId+"&abnormalIndex="+abnormalIndex;
				loading();
				document.location.href=actionURL;
			}else{
				return;
			}
        });
	}

   //暂存
   function tempSaveForm(){
	  Ext.get('anormalReasonName').dom.value=anormalReason.getDisplayField(anormalReason.getValue());
	  Ext.get('eventName').dom.value=event.getDisplayField(event.getValue());
	  Ext.get('eventSubName').dom.value=event.getDisplayField(Ext.get('eventSubIndex').dom.value);
	  loading();
	  abnorForm.submit();
  }

   //完成(如果存在处理程序，须执行完后，方可操作)
   function overForm(){
   		if('<%=jobRelationIndex%>'!=''){
   			var actionURL='<ofbiz:url>/abnormalFormJobStatusQueryEntry</ofbiz:url>?relationIndex=<%=jobRelationIndex%>';
			Ext.lib.Ajax.formRequest('abnorForm',actionURL,{success: commentQueryJobStatusSuccess, failure: commentFailure});
   		}else{
   			var errorMsg=checkAbnormalForm("0");
	   		if(errorMsg!=""){
	   			Ext.MessageBox.alert('警告', errorMsg);
	   			return;
	   		}
	   		Ext.MessageBox.confirm('修改表单确认', '您确信要修改此异常表单吗？',function result(value){
		        if(value=="yes"){
		        	Ext.get('anormalReasonName').dom.value=anormalReason.getDisplayField(anormalReason.getValue());
		   			Ext.get('eventName').dom.value=event.getDisplayField(event.getValue());
		   			Ext.get('eventSubName').dom.value=Ext.get('eventSub').dom.value;
					var actionURL='<ofbiz:url>/editTsFormByLeader</ofbiz:url>';
					loading();
					abnorForm.action=actionURL;
					abnorForm.submit();
				}else{
					return;
				}
   		   });
   		}
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
			var errorMsg=checkAbnormalForm("0");
	   		if(errorMsg!=""){
	   			Ext.MessageBox.alert('警告', errorMsg);
	   			return;
	   		}
	   		Ext.MessageBox.confirm('修改表单确认', '您确信要修改此异常表单吗？',function result(value){
		        if(value=="yes"){
		        	Ext.get('anormalReasonName').dom.value=anormalReason.getDisplayField(anormalReason.getValue());
		   			Ext.get('eventName').dom.value=event.getDisplayField(event.getValue());
		   			Ext.get('eventSubName').dom.value=Ext.get('eventSub').dom.value;
					var actionURL='<ofbiz:url>/editTsFormByLeader</ofbiz:url>';
					loading();
					abnorForm.action=actionURL;
					abnorForm.submit();
				}else{
					return;
				}
	        });
		}
	}

   //新建问题追踪
   function saveFollowJob(){
   		var url='<ofbiz:url>/intoAddQuFollow</ofbiz:url>?type=FORM&eventType=TS&eventIndex=<ofbiz:inputvalue entityAttr="abnormal" field="abnormalIndex"/>&eqpId=<ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/>';
		windowOpen(url,'新建问题追踪',685,400);
    }

	//获取partNo
	function getPartsNo(){
		var par=new Array();
		//设备ID，formType值为PM时需要
		//PM周期,formType值为PM时需要
		/*var result=window.showModalDialog ("<%=request.getContextPath()%>/control/intoAbnormalFormPartsQueryEntry?formType=TS&eqpId=<ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/>&periodIndex=&eventIndex=<ofbiz:inputvalue entityAttr="abnormal" field="abnormalIndex"/>",par,"dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");*/

		// mcs interface: pm/ts using parts
		var result = window.showModalDialog("<%=request.getContextPath()%>/control/useMaterialByQtyEntry?eventType=TS&eqpId=<ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/>&eventIndex=<ofbiz:inputvalue entityAttr="abnormal" field="abnormalIndex"/>", window, "dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");

		 Ext.get('anormalReasonName').dom.value=anormalReason.getDisplayField(anormalReason.getValue());
		 Ext.get('eventName').dom.value=event.getDisplayField(event.getValue());
		 Ext.get('eventSubName').dom.value=Ext.get('eventSub').dom.value;
		 abnorForm.action="<%=request.getContextPath()%>/control/queryTsformByLeader?activate=parts&functionType=1&eqpId=<ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/>";
		 loading();
		 abnorForm.submit();
	}

	//修改功能弹出页
	function editParts(obj,seqIndex){
		//Ext.MessageBox.confirm('表单暂存确认', '在修改parts前请您[暂存]表单,否则会导致表单信息丢失!',function //result(value){
		//if(value=="yes"){
	        	Ext.get('seqIndex').dom.value=seqIndex;
				var url='<ofbiz:url>/partsUseInfoQueryEntry</ofbiz:url>?seqIndex='+seqIndex;
				extDlg.showEditDialog(obj,url);
			//}else{
			//	return;
			//}
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

	//查询设备状态
	function queryEqpStatus(){
		var eqpId='<ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/>';
		var actionURL='<ofbiz:url>/abnormalFormEqpStatusQueryEntry</ofbiz:url>?eqpId='+eqpId;
		Ext.lib.Ajax.formRequest('abnorForm',actionURL,{success: commentQuerySuccess, failure: commentFailure});
	}

	//远程调用成功
	function commentQuerySuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			var status=result.status;
			if("success"==status){
				var eqpStatus=result.eqpStatus;
				Ext.get('eqpStatus').dom.innerText=eqpStatus;
			}else if("error"==status){
				var message=result.message;
				setErrorMsg(message);
			}
		}
	}

	//修改设备状态
	function updateEqpStatus(){
		var eqpId='<ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/>';
		var eqpState=Ext.get('eqpState').dom.value;
		if(eqpState==''){
			Ext.MessageBox.alert('警告', "请选择设备状态!");
   			return;
		}
		var actionURL='<ofbiz:url>/abnormalFormEqpStatusUpdateEntry</ofbiz:url>?eqpId='+eqpId+'&eqpState='+eqpState+'&abnormalIndex=<ofbiz:inputvalue entityAttr="abnormal" field="abnormalIndex"/>';
		Ext.lib.Ajax.formRequest('abnorForm',actionURL,{success: commentEditSuccess, failure: commentFailure});
	}

	//远程调用成功
	function commentEditSuccess(o){
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

   //abnormalForm数据校验
   //0:完成操作
   //1:暂存操作
   function checkAbnormalForm(flag){
   		var abnormalTime=Ext.get('abnormalTime').dom.value;
   		var errorStr="";
   		if(abnormalTime==""){
   			errorStr='异常时间不能为空!';
   			tabs.activate('abnormal');
   			return errorStr;
   		}
   		var abnormalTime=Ext.get('abnormalTime').dom.value;
   		if(abnormalTime.indexOf(":")==-1){
   			abnormalTime=abnormalTime+" 00:00:00";
   		}
   		var abnormalDate=Date.parseDate(abnormalTime, "Y-m-d h:i:s");
   		var abnormalReasonDisplay=anormalReason.getDisplayField(anormalReason.getValue());
   		var jobText=Ext.get('jobText').dom.value;
   		var relAbnormalReason=Ext.get('relAbnormalReason').dom.value;
   		var abnormalText=Ext.get('abnormalText').dom.value;
   		var nowDate=new Date();
   		var hourElapsed=(nowDate.getTime()-abnormalDate.getTime())/1000/3600;
		//暂存时不需要校验
		if(flag=="0"){
			//回线时间大于2小时
   			if(hourElapsed>2){
   				if(abnormalText==""){
					errorStr='维修时间大于2小时，[异常信息]中的故障现象必填!';
					tabs.activate('abnormal');
					return errorStr;
				}
				/*if(abnormalReasonDisplay.toUpperCase()=="OTHER" && relAbnormalReason==""){
					errorStr='故障时间大于2小时，异常原因为other,[处理程序]中的异常真正原因必填!';
					tabs.activate('flow');
					return errorStr;
				}*/
				if('<%=jobRelationIndex%>'!='' && dealJobStatus==false){
					errorStr='有处理程序，必须载入!';
					tabs.activate('flow');
					return errorStr;
				}
				if(jobText==""){
					errorStr='请填写[处理程序]中的处理过程！';
					tabs.activate('flow');
					return errorStr;
				}
   			}else{
   				if(abnormalReasonDisplay.toUpperCase()=="OTHER"&&jobText==""){
					errorStr='故障时间2小时以内，异常原因为other,请填写[处理程序]中的处理过程！';
					tabs.activate('flow');
					return errorStr;
				}
   			}
		}
   		return errorStr;
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
		if(isNaN(partCount)){
			return "数量必须为数字";
		}
		return "";
	}

	//执行流程
	function runJob(jobRelationIndex) {
        var url = "<ofbiz:url>/runFlow</ofbiz:url>?jobRelationIndex=" + jobRelationIndex;
        window.open(url,"job",
                "top=130,left=240,width=600,height=400,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=no");
    }

    //查看流程数据
    function viewJob(jobRelationIndex) {
        var url = "<ofbiz:url>/viewFlow</ofbiz:url>?jobRelationIndex=" + jobRelationIndex;
        window.open(url,"viewJob",
                "top=130,left=240,width=600,height=400,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=no");
    }

   //页面init时
   Ext.onReady(function(){
	    //注册事件下拉
	    event= new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'event',
	        width:170,
	        forceSelection:true
	    });
	    event.on('select',eventSubChange);
		event.setValue('<ofbiz:inputvalue entityAttr="abnormal" field="eventIndex"/>')

	    //NORMAL：需要修改设备状态，不能修改异常时间
	    //PATCHL：不需要修改设备状态，可以修改异常时间
	     <%if(Constants.FORM_TYPE_NORMAL.equals(inputType)){%>
		     /*var eqpState = new Ext.form.ComboBox({
		        typeAhead: true,
		        triggerAction: 'all',
		        transform:'eqpState',
		        width:170,
		        forceSelection:true
		    });
		    Ext.get('abnormalTime').dom.value='<%=abnormalTime%>'*/
	    <%
	    	}else{
	    %>
	   	var abnormalTime = new Ext.form.DateTimeField({
		 	format: 'Y-m-d H:i:s',
		    allowBlank:true
		});
		abnormalTime.applyTo('abnormalTime');
		abnormalTime.setValue('<%=abnormalTime%>')
	    <%
	    }
	    %>

	   //注册异常原因
	   anormalReason = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'anormalReason',
	        width:170,
	        forceSelection:true
	    });
	   anormalReason.setValue('<ofbiz:inputvalue entityAttr="abnormal" field="abnormalReasonIndex"/>');

	   //注册事件细项
	   eventSub = new Ext.form.ComboBox({
	     store: ds,
	     displayField:'subCategory',
	     valueField:'eventSubIndex',
	     hiddenName:'eventSubIndex',
	     typeAhead: true,
	     width:170,
	     mode: 'local',
	     triggerAction: 'all'
	   });
  	   eventSub.applyTo('eventSub');


	   //注册sheet
	   tabs = new Ext.TabPanel('tabs');
       abnormal = tabs.addTab('abnormal', "异常信息");
       flow = tabs.addTab('flow', "处理程序");
       parts = tabs.addTab('parts', "使用配件");
       tabs.activate('abnormal');
       <% if(("parts").equals(activate)){%>
       		tabs.activate('parts');
       <%}else{%>
       		tabs.activate('abnormal');
       <%}%>
      eventSubChange();

   });
</script>
<form action="<%=request.getContextPath()%>/control/queryTsformByLeader" method="post" id="abnorForm">
<fieldset><legend>异常记录表单</legend>
<div id="tabs">
	<div id="abnormal" class="tab-content">
	<input id="functionType" type="hidden" name="functionType" value='1' />
	<input id="formType" type="hidden" name="formType" value='<%=inputType%>' />
	<input id="anormalReasonName" type="hidden" name="anormalReasonName"/>
	<input id="abnormalIndex" type="hidden" name="abnormalIndex" value='<ofbiz:inputvalue entityAttr="abnormal" field="abnormalIndex"/>'/>
	<input id="eqpId" type="hidden" name="eqpId" value='<ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/>'/>
	<input id="eventName" type="hidden" name="eventName" />
	<input id="eventSubName" type="hidden" name="eventSubName" />
	    <table width="100%" border="0" cellspacing="1" cellpadding="2">
	        <tr bgcolor="#DFE1EC" height="25">
	          <td width="20%" bgcolor="#ACD5C9" class="en11pxb">EQPID</td>
	          <td width="30%" class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/></td>
	          <td width="20%" bgcolor="#ACD5C9" class="en11pxb">部门</td>
	          <td width="30%" class="en11px"><%=accountDept%></td>
	        </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td bgcolor="#ACD5C9" class="en11pxb">课别</td>
	          <td class="en11px"><%=section%></td>
	          <td bgcolor="#ACD5C9" class="en11pxb">撰写时间</td>
	          <td class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="createTime"/></td>
	        </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">报告人</td>
	          <td class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="createName"/></td>
	          <td class="en11pxb" bgcolor="#ACD5C9">文件编号</td>
	          <td class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="abnormalName"/></td>
	        </tr>
            <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">异常时间</td>
	          <td class="en11px"><input type="text" ID="abnormalTime" NAME="abnormalTime" readonly size="20" class="input" value="<%=abnormalTime%>"></td>
	          <td bgcolor="#ACD5C9" class="en11pxb">目前处理状况</td>
	          <td class="en11px"><%=displayStatus%></td>
	          </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">成员</td>
	          <td colspan="3" class="en11px"><input type="text" size="70" name="member" id="member" class="input" value='<ofbiz:inputvalue entityAttr="abnormal" field="member"/>'></td>
	        </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">故障现象</td>
	          <td colspan="3" class="en11px"><input type="text" size="70"  name="abnormalText" id="abnormalText" class="input" value='<ofbiz:inputvalue entityAttr="abnormal" field="abnormalText"/>'></td>
	        </tr>
            <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">Rework</td>
	          <td colspan="3" class="en11px"><input type="text" size="70"  name="reworkNote" id="reworkNote" class="input" value='<ofbiz:inputvalue entityAttr="abnormal" field="reworkNote"/>'></td>
	        </tr>
	        <tr bgcolor="#DFE1EC" height="25">
            <td class="en11pxb" bgcolor="#ACD5C9">事件分类</td>
            <td><select id="event" name="event" class="select">
          		<option value=''></option>
	          	<ofbiz:if name="EVENT_CATEGORY_LIST">
		        	<ofbiz:iterator name="eventCategory" property="EVENT_CATEGORY_LIST">
			    		<option value='<ofbiz:inputvalue entityAttr="eventCategory" field="eventIndex"/>'><ofbiz:inputvalue entityAttr="eventCategory" field="category"/></option>
		      		</ofbiz:iterator>
	      		</ofbiz:if>
	      	</select></td>
            <td class="en11pxb" bgcolor="#ACD5C9">事件细项</td>
            <td><input type="text" size="40" name="eventSub" id="eventSub" autocomplete="off"/></td>
          </tr>
          <tr bgcolor="#DFE1EC">
            <td class="en11pxb" bgcolor="#ACD5C9">ErrorCode</td>
            <td><input type="text" name="errorCode" size="25"  id="errorCode" class="input" value='<ofbiz:inputvalue entityAttr="abnormal" field="errorCode"/>'></td>
            <td class="en11pxb" bgcolor="#ACD5C9">异常原因</td>
            <td><select id="anormalReason" name="anormalReason" class="select">
          		<option value='' selected>OTHER</option>
	          	 <% if(reasonList != null && reasonList.size() > 0) {
       				for(Iterator it = reasonList.iterator();it.hasNext();) {
					Map map = (Map)it.next();%>
					<option value='<%=map.get("REASON_INDEX")%>'><%=map.get("REASON")%></option>
				 <%
				  	}
				  } %>
	      	</select></td>
          </tr>
          <%if("NORMAL".equals(inputType)){%>
          <tr bgcolor="#DFE1EC">
            <td bgcolor="#ACD5C9"><ul class="button">
                      <li><a class="button-text" href="#" onclick="queryEqpStatus();"><span>&nbsp;查询目前设备状态&nbsp;</span></a></li>
              </ul></td>
            <td class="en11px" colspan="3">&nbsp;<span id='eqpStatus'></span></td>

          </tr>
          <% }%>
        </table>
  </div>

  <div id="flow" class="tab-content">
   <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
       <tr bgcolor="#DFE1EC">
         <td width="14%" rowspan="2" bgcolor="#ACD5C9" class="en11pxb">处理过程</td>
         <td colspan="2" rowspan="2" class="en11px"><label>
           <textarea name="jobText" id="jobText" cols="60" rows="10"><ofbiz:inputvalue entityAttr="abnormal" field="jobText"/></textarea>
         </label></td>
         <td width="42%" class="en11px">
         <% if(!"".equals(jobRelationIndex)){%>
         <ul class="button">
                     <li><a class="button-text" href="javascript:runJob(<%=jobRelationIndex%>)"><span>&nbsp;执行处理程序&nbsp;</span></a></li>
          </ul>
          <% }%>
          </td>
       </tr>
       <tr bgcolor="#DFE1EC">
         <td class="en11px">
         <% if(!"".equals(jobRelationIndex)){%>
         <ul class="button">
                     <li><a class="button-text" href="javascript:viewJob(<%=jobRelationIndex%>)"><span>&nbsp;查询流程项目&nbsp;</span></a></li>
         </ul>
         <% }%>
         </td>
       </tr>
       <tr bgcolor="#DFE1EC">
         <td bgcolor="#ACD5C9" class="en11pxb">异常真正原因</td>
         <td colspan="3" class="en11px"><textarea name="relAbnormalReason" id="relAbnormalReason" cols="60" rows="8"><ofbiz:inputvalue entityAttr="abnormal" field="relAbnormalReason"/></textarea></td>
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
		  <ofbiz:iterator name="parsUse" property="PARTS_USE_LIST">
	       <tr bgcolor="#DFE1EC">
	         <td><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delPartsUse('<ofbiz:inputvalue entityAttr="parsUse" field="seqIndex"/>')"/></td>

	         <td class="en11px">
	            <!--<a href="#" onclick="editParts(this,'<ofbiz:inputvalue entityAttr="parsUse" field="seqIndex"/>')">
	                <ofbiz:inputvalue entityAttr="parsUse" field="partNo"/>
	            </a>-->
	            <ofbiz:inputvalue entityAttr="parsUse" field="partNo"/>
	        </td>

	         <td class="en11px"><ofbiz:inputvalue entityAttr="parsUse" field="partName"/></td>
	         <td class="en11px"><ofbiz:inputvalue entityAttr="parsUse" field="partType"/></td>
	         <td class="en11px"><ofbiz:inputvalue entityAttr="parsUse" field="partCount"/></td>
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
			<li><a class="button-text" href="#" onclick="overForm();"><span>&nbsp;修改&nbsp;</span></a></li>
	</ul></td>
    <td><ul class="button">
			<li><a class="button-text" href="#" onclick="saveFollowJob();"><span>&nbsp;建立问题追踪&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>

<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">修改Parts</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="修改Parts">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/updateTsPartsByLeader" method="post" id="partsForm" onsubmit="return false;">
                	<input id="activate" type="hidden" name="activate" value="parts" />
                	<input id="partType" type="hidden" name="partType" value="" />
                	<input id="functionType" type="hidden" name="functionType" value='1'/>
                	<input id="seqIndex" type="hidden" name="seqIndex" value="" />
                	<input id="eqpId" type="hidden" name="eqpId" value="<ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/>" />
                	<input id="abnormalIndex" type="hidden" name="abnormalIndex" value="<ofbiz:inputvalue entityAttr="abnormal" field="abnormalIndex"/>" />
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
