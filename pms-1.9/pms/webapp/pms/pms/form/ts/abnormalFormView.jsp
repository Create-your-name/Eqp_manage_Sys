<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import ="com.csmc.pms.webapp.util.*"%>
<%@ page import="java.util.*" %>

<script src="<%=request.getContextPath()%>/camera/WsUtil.js" type="text/javascript" charset="utf-8"></script>
<script src="<%=request.getContextPath()%>/camera/OcxUtil.js" type="text/javascript" charset="utf-8"></script>
<script src="<%=request.getContextPath()%>/camera/axCam_Ocx.js" type="text/javascript" charset="utf-8" for="axCam_Ocx"
	event="MessageCallback(type,str)"></script>
<script src="<%=request.getContextPath()%>/camera/axCam_Ocx2.js" type="text/javascript" charset="utf-8" for="axCam_Ocx2"
	event="MessageCallback(type,str)"></script>
<script src="<%=request.getContextPath()%>/camera/cbCam.js" type="text/javascript" charset="utf-8"></script>

<%
	int size=0;
	List keyPartsUseList=(List)request.getAttribute("keyPartsUseList");
	if(keyPartsUseList!=null){
		size=keyPartsUseList.size();
	}
	List guiPriv = (List)request.getSession().getAttribute(SessionNames.GUI_PRIV_LIST_KEY);
	GenericValue gv=(GenericValue)request.getAttribute("ABNORMAL");
	pageContext.setAttribute("abnormal",gv);
	String abnormalIndex = gv.getString("abnormalIndex");
	String userId = (String)request.getAttribute("userId");
	String section=(String)request.getAttribute("SECTION");
	String accountDept=(String)request.getAttribute("ACCOUNT_DEPT");
	String inputType=gv.getString("formType");
	String formLockStatus=gv.getString("lockStatus");
	if (formLockStatus == null){
		formLockStatus = "";
	}
	String formLockUser=gv.getString("lockUser");
	if (formLockUser == null){
		formLockUser = "";
	}
	int status=Integer.parseInt(gv.getString("status"));
	String displayStatus="";
	String jobText=UtilFormatOut.checkNull(gv.getString("jobText"));
	String jobRelationIndex=(String)request.getAttribute("JOB_RELATION_INDEX");
	if(StringUtils.isNotEmpty(jobText)){
		jobText=jobText.replaceAll("\r\n","<br>");
	}
	if(Constants.CREAT==status||Constants.START==status||Constants.HOLD==status){
		displayStatus="未完成";
	}else{
		displayStatus="完成";
	}
%>

<script language="javascript">
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

   //远程调用失败
   var commentFailure = function(o){
    	Ext.MessageBox.alert('Status', 'Unable to connect.');
   };

   //页面init时
   Ext.onReady(function(){
	// 初始化摄像头
	loadActiveX();

  	//注册sheet
	   var tabs = new Ext.TabPanel('tabs');
       var abnormal = tabs.addTab('abnormal', "异常信息");
       var flow = tabs.addTab('flow', "处理程序");
       var parts = tabs.addTab('parts', "使用配件");
	   var checkList = tabs.addTab('checkList', "check list");
       tabs.activate('abnormal');
   });

   //查看流程数据
    function viewJob(jobRelationIndex) {
        var url = "<ofbiz:url>/viewFlow</ofbiz:url>?jobRelationIndex=" + jobRelationIndex;
        window.open(url,"viewJob",
                "top=130,left=240,width=600,height=400,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=no");
    }

    //leader进入修改界面
    function editForm(index,eqpId){
		loading();
		var actionURL='<ofbiz:url>/intoAbnormalFormManageEntry</ofbiz:url>?functionType=1&abnormalIndex='+index+'&eqpId='+eqpId;
		document.location.href=actionURL;
	}

	//修改或查询问题跟踪
	function editFollowJob(status,index){
		var url='';
		if(status=='0'){
			//建立
			url='<ofbiz:url>/intoEditQuFollow</ofbiz:url>?followIndex='+index;
		}else if(status=='2'){
			//未结案
			url='<ofbiz:url>/intoAddItemQuFollow</ofbiz:url>?followIndex='+index;
		}else if(status=='1'){
			//结案
			url='<ofbiz:url>/overFollowJobShow</ofbiz:url>?followIndex='+index;
		}
		document.location.href=url;
	}
	//签核
	function flowLeadSubmit(submitType){
		Ext.getDom('submitType').value = submitType
		Ext.MessageBox.confirm('送签确认', '你确定要送签该信息？',function result(value){
		if(value=="yes"){
			var url='<ofbiz:url>/sendTsFormSubmit</ofbiz:url>';
			loading();
			document.abnorForm.action=url;
			document.abnorForm.submit();
		}else{
			return;
		}
		});
	}

	//文件管理页面
	function manageFile(){
		var url='<ofbiz:url>/fileUploadDefineEntry</ofbiz:url>?eventIndex=<%=abnormalIndex%>&eventType=TS&uploadItem=uploadIndex';
		windowOpen(url,'文件上传下载',685,400);
	}
</script>

<form action="<%=request.getContextPath()%>/control/abnormalFormManageTempSaveEntry" method="post" id="abnorForm" name="abnorForm">
		<input name="submitObject" type="hidden" value="TSFORM" />
		<input name="submitObjectIndex" type="hidden" value="<ofbiz:inputvalue entityAttr="abnormal" field="abnormalIndex"/>" />
		<input name="submitType" type="hidden" value="" />
		<input name="submitObjectName" type="hidden" value="<ofbiz:inputvalue entityAttr="abnormal" field="abnormalName"/>" />

<fieldset><legend>异常记录表</legend>
<div id="tabs">
	<div id="abnormal" class="tab-content">
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
	          <td bgcolor="#ACD5C9" class="en11pxb">目前处理状况</td>
	          <td class="en11px" colspan="3"><%=displayStatus%></td>
	        </tr>
            <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">异常时间</td>
	          <td class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="abnormalTime"/></td>
	          <td bgcolor="#ACD5C9" class="en11pxb">(预计)回线时间</td>
	          <td class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="returnTime"/></td>
	          </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">成员</td>
	          <td colspan="3" class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="member"/></td>
	        </tr>
	        <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">故障现象</td>
	          <td colspan="3" class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="abnormalText"/></td>
	        </tr>
            <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">Rework</td>
	          <td colspan="3" class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="reworkNote"/></td>
	        </tr>
	      <tr bgcolor="#DFE1EC" height="25">
            <td class="en11pxb" bgcolor="#ACD5C9">事件分类</td>
            <td class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="eventCategory"/></td>
            <td class="en11pxb" bgcolor="#ACD5C9">事件细项</td>
            <td class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="subeventCategory"/></td>
          </tr>
          <tr bgcolor="#DFE1EC" height="25">
            <td class="en11pxb" bgcolor="#ACD5C9">ErrorCode</td>
            <td class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="errorCode"/></td>
            <td class="en11pxb" bgcolor="#ACD5C9">异常原因</td>
            <td class="en11px"><ofbiz:inputvalue entityAttr="abnormal" field="abnormalReason"/></td>
          </tr>
          <%if("NORMAL".equals(inputType)){%>
          <tr bgcolor="#DFE1EC">
            <td bgcolor="#ACD5C9"><ul class="button">
                      <li><a class="button-text" href="#" onclick="queryEqpStatus();"><span>&nbsp;查询目前设备状态&nbsp;</span></a></li>
              </ul></td>
            <td class="en11px" colspan="3">&nbsp;<span id='eqpStatus'></span></td>
            </tr>
            <%} %>
        </table>
  </div>

  <div id="flow" class="tab-content">
  	<% if(!"".equals(jobRelationIndex)){%>
  	<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
   		<tr bgcolor="#DFE1EC">
   			<td width="14%" rowspan="4" bgcolor="#ACD5C9" class="en11pxb">流程项目</td>
   			<td colspan="2" rowspan="4" class="en11px">
   				<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
   					<tr bgcolor="#DFE1EC">
   						<td class="en11px" width="35%"><a href="javascript:viewJob(<%=jobRelationIndex%>)">查看</a></td>
   					</tr>
   				</table>
   			</td>
		</tr>
   </table>
   <% }%>
   <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
       <tr bgcolor="#DFE1EC">
         <td width="14%" bgcolor="#ACD5C9" class="en11pxb">处理过程</td>
         <td class="en11px" height="100"><%=jobText%></td>
       </tr>
       <tr bgcolor="#DFE1EC">
         <td bgcolor="#ACD5C9" class="en11pxb">异常真正原因</td>
         <td class="en11px" height="100"><ofbiz:inputvalue entityAttr="abnormal" field="relAbnormalReason"/></td>
       </tr>
     </table>
  </div>

  <div id="parts" class="tab-content">
  <div style="overflow-x:hidden;overflow-y:scroll;width:100%;"> 
   <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
      <tr bgcolor="#ACD5C9"> 
         <td width="2%"></td>
         <td width="5%" class="en11pxb">物料组</td>
         <td width="10%" class="en11pxb">料号</td>
         <td width="20%" class="en11pxb">料名</td>
         <td width="10%" class="en11pxb">批次</td>
         <td width="5%" class="en11pxb">物料类别</td>
         <td width="5%" class="en11pxb">参考数量</td>
         <td width="5%" class="en11pxb">库存余量</td>
         <td width="5%" class="en11pxb">实际使用数量</td>
         <td width="5%" class="en11pxb">是否关键备件</td>
         <td width="20%" class="en11pxb">备注</td>
       </tr>
      </table>
      </div>
      <div style="overflow-x:hidden;overflow-y:scroll;width:100%;height:240px;">
     	<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30"  style="word-wrap:break-word; word-break:break-all;">      
       <% 
        List partsUseList=(List)request.getAttribute("PARTS_USE_LIST");
        if(partsUseList != null && partsUseList.size() > 0) {  
            for(Iterator it = partsUseList.iterator();it.hasNext();) { 
                    Map map = (Map)it.next();
        %>
           <tr bgcolor="#DFE1EC"> 
             <td width="2%"></td>
             <td width="5%" class="en11px"><%=map.get("MTR_GRP")%></td>
             <td width="10%" class="en11px"><%=map.get("PART_NO")%></td>
             <td width="20%" class="en11px"><%=map.get("PART_NAME")%></td>
             <td width="10%" class="en11px"><%=map.get("BATCH_NUM")%></td>
             <td width="5%" class="en11px"><%=map.get("PART_TYPE")%></td>
             <td width="5%" class="en11px"><%if(map.get("TEMPLATE_COUNT")!=null){%><%=map.get("TEMPLATE_COUNT")%><%}%></td>
             <td width="5%" class="en11px">
             	<%if(map.get("QTY")!=null){%><%=map.get("QTY")%><%}%>
             </td>
             <td width="5%" class="en11px"><%=map.get("PART_COUNT")%>
             </td>
             <td width="5%" class="en11px" align="center"><%=map.get("ISKEYPARTS")%></td> 
             <td width="20%" class="en11px"><%=UtilFormatOut.checkNull((String) map.get("REMARK"))%></td>
           </tr>
          <%
	        }
	      } 
	      %>
     </table>
	</div>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td> 
      <legend>此设备关键备件信息</legend>
	  <div style="overflow-x:hidden;overflow-y:scroll;width:100%;">
      <table width="100%" border="0" cellspacing="1" cellpadding="2" height="30"  style="word-wrap:break-word; word-break:break-all;">
        <tr bgcolor="#ACD5C9"> 
          <td width="4%" class="en11pxb">选择</td>
          <td width="6%" class="en11pxb">关键字</td>
          <td width="8%" class="en11pxb">物料号</td>
          <td width="8%" class="en11pxb">物料名</td>
          <td width="4%" class="en11pxb">使用数量</td>
          <td width="10%" class="en11pxb">类别</td>
          <td width="4%" class="en11pxb">寿命类型</td>
          <td width="4%" class="en11pxb">剩余寿命</td>
          <td width="10%" class="en11pxb">vendor</td>         
          <td width="8%" class="en11pxb">SeriesNo.</td> 
          <td width="8%" class="en11pxb">baseS/N</td> 
          <td width="4%" class="en11pxb">旧件已用寿命</td>
          <td width="4%" class="en11pxb">延期次数</td>
          <td width="10%" class="en11pxb">换下原因</td> 
          <td width="8%" class="en11pxb">备注</td>    
        </tr>
       </table>
       </div>
      <div style="overflow-x:hidden;overflow-y:scroll;width:100%;height:240px;">
     	<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30"  style="word-wrap:break-word; word-break:break-all;">
      <% 
         if(keyPartsUseList != null && keyPartsUseList.size() > 0) {  
            int k = 0;
            for(Iterator it = keyPartsUseList.iterator();it.hasNext();) { 
                Map map = (Map)it.next();
                String warnRst=map.get("warnRst").toString();
                String errorRst=map.get("errorRst").toString();  
								String mustchangeRemark=map.get("mustchangeRemark").toString();
								String isUsed=map.get("isUsed").toString();
                if("Y".equals(errorRst)){
        %> 
           <tr bgcolor="OrangeRed">
        <%}else if("Y".equals(warnRst)){%>   
           <tr bgcolor="PINK">
        <%}else if(map.get("lifeError")!=null){%>
        	<tr bgcolor="yellow">
        <%}else{%>
        	<tr bgcolor="#DFE1EC">
        <%}%>
             <td width="4%" class="en11px">
        <%
        	if("1".equals(mustchangeRemark) && "Y".equals(errorRst) && "Y".equals(map.get("MUSTCHANGE").toString())){
        %>
             <div id="check_<%=k%>" style="display:'none'">
        <%
        	}else{
        %>
        	<div id="check_<%=k%>" style="display:''">
        <% } %>
             <input style="width:95%" type="checkBox" id="parts_<%=k%>" name="parts_<%=k%>"  value='<%=map.get("MTR_NUM")%>' <% if (  !map.get("KEY_USE_ID").toString().equals("0")) {%>checked="1" <% } %> />
			 </div>
			 <input type="hidden" id="mustchangeRemark_<%=k%>" name="mustchangeRemark_<%=k%>" value='<%=map.get("mustchangeRemark")%>' />
			 <input type="hidden" id="isdelayed_<%=k%>" name="isdelayed_<%=k%>" value='<%=map.get("ISDELAYED")%>' />
			 <input type="hidden" id="createTime_<%=k%>" name="createTime_<%=k%>" value='<%=map.get("CREATE_TIME_NEW")%>' />
			 <input type="hidden" id="eqpModel_<%=k%>" name="eqpModel_<%=k%>" value='<%=map.get("EQP_MODEL")%>' />
			 <input type="hidden" id="errorSeriesNo_<%=k%>" name="errorSeriesNo_<%=k%>" value='<%=UtilFormatOut.checkNull((String)map.get("errorSeriesNo"))%>' />
			 <input type="hidden" id="isClean_<%=k%>" name="isClean_<%=k%>" value='<%=UtilFormatOut.checkNull((String)map.get("ISCLEAN"))%>' />	
			 <input type="hidden" id="keyPartsCleanId_<%=k%>" name="keyPartsCleanId_<%=k%>" value='<%=UtilFormatOut.checkNull((String)map.get("KEY_PARTS_CLEAN_ID"))%>' />		 
		<% 
			if("Y".equals(isUsed) || UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("DELAY")){  
			  if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("DELAY")){%> 
			 	<div id="delay_<%=k%>" style="display:''"><a href="#" style="font-size:8px;" onclick="keyPartsDelaySet(this,'<%=map.get("KEY_PARTS_ID") %>','<%=map.get("MTR_NUM") %>','<%=map.get("DELAYTIME")%>','<%=map.get("INIT_LIFE")%>','<%=map.get("KEY_USE_ID")%>','<%=map.get("KEY_USE_ID_USED")%>','<%=k%>','<%=map.get("ACTUL")%>')" >延期</a></div>
			 <%}else{%> 
			 	<div id="delay_<%=k%>" style="display:none" ><a href="#" style="font-size:8px;" onclick="keyPartsDelaySet(this,'<%=map.get("KEY_PARTS_ID") %>','<%=map.get("MTR_NUM") %>','<%=map.get("DELAYTIME")%>','<%=map.get("INIT_LIFE")%>','<%=map.get("KEY_USE_ID")%>','<%=map.get("KEY_USE_ID_USED")%>','<%=k%>','<%=map.get("ACTUL")%>')" >延期</a></div>
             <%}%>
        <% } %>
             <input id="keypartsid_<%=k%>" type="hidden" name="keypartsid_<%=k%>" value='<%=map.get("KEY_PARTS_ID")%>'/>
             <input id="partsuseid_<%=k%>" type="hidden" name="partsuseid_<%=k%>" value='<%=map.get("PARTS_USE_ID")%>'/>
             <input id="keyuseid_<%=k%>" type="hidden" name="keyuseid_<%=k%>" value='<%=map.get("KEY_USE_ID")%>'/>
             <input id="partsMtrGrp_<%=k%>" type="hidden" name="partsMtrGrp_<%=k%>" value='<%=map.get("MTR_GRP")%>' />
             <input id="keyUseIdUsed_<%=k%>" type="hidden" name="keyUseIdUsed_<%=k%>" value='<%=map.get("KEY_USE_ID_USED")%>' />
             <input id="kqty_<%=k%>" type="hidden" name="kqty_<%=k%>" value='<%=map.get("QTY")%>' /> 
             <input id="kusedNum_<%=k%>" type="hidden" name="kusedNum_<%=k%>" value='<%=map.get("USED_NUMBER")%>' /> 
             <input id="mustchange_<%=k%>" type="hidden" name="mustchange_<%=k%>" value='<%=map.get("MUSTCHANGE")%>' /> 
             <input id="errorRst_<%=k%>" type="hidden" name="errorRst_<%=k%>" value='<%=map.get("errorRst")%>' /> 
             <input id="lifeError_<%=k%>" type="hidden" name="lifeError_<%=k%>" value='<%=UtilFormatOut.checkNull((String)map.get("lifeError"))%>' /> 
             </td>
             <td width="6%" class="en11px">
             	<input id="keydesc_<%=k%>" type="hidden" name="keydesc_<%=k%>" value='<%=map.get("KEYDESC")%>' />
             	<%=map.get("KEYDESC")%></td>
             <td width="8%" class="en11px"><input id="mtrNum_<%=k%>" type="hidden" name="mtrNum_<%=k%>" value='<%=map.get("MTR_NUM")%>' /><%=map.get("MTR_NUM")%></td>
             <td width="8%" class="en11px">
                <input id="partsName_<%=k%>" type="hidden" name="partsName_<%=k%>" value='<%=map.get("MTR_DESC")%>' />
                <%=map.get("MTR_DESC")%>
             </td>
             <%
             if(map.get("USE_NUMBER")!=null){
             %>       
             <td width="4%" class="en11px"><input id="useNum_<%=k%>" name="useNum_<%=k%>" class="input" value='<%=map.get("USE_NUMBER")%>' maxlength="2" type="text" readonly="readonly" style="background:E9E9E9;width:95%"/></td>  
             <%
             }else{
             %>
             <td width="4%" class="en11px"><input id="useNum_<%=k%>" name="useNum_<%=k%>" class="input" value="1" maxlength="2" type="text" readonly="readonly" style="background:E9E9E9;width:95%"/></td>  
             <%
             }
             %>
             <td width="10%" class="en11px"><select style="width:95%" id="newparts_type<%=k%>" readonly="readonly" name="newparts_type<%=k%>">
      	        <option  <% if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("NEW")) { %>  selected <% }%> value="NEW">NEW</option>
      			<option <% if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("CLEAN")) { %>  selected <% }%>    value="CLEAN">CLEAN</option>
      		<%
      		if(!UtilFormatOut.checkNull((String)map.get("ISCLEAN")).equals("Y")){      			      		
      		%>
      		    <option <% if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("OVERHUAL")) { %>  selected <% }%>    value="OVERHUAL">OVERHUAL</option>
      			<option <% if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("OLD")) { %>  selected <% }%>    value="OLD">OLD</option>
      			<option <% if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("技改/DEMO")) { %>  selected <% }%>    value="技改/DEMO">技改/DEMO</option>
      		
      		<%
	      		if(UtilFormatOut.checkNull((String)map.get("MUSTCHANGE")).equals("Y")||isUsed.equals("N")){
	      			if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("DELAY")){
      		%> 
      			<option <% if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("DELAY")) { %>  selected <% }%>    value="DELAY">延期</option>  
      		<%		}
      			}else{%>
      			<option <% if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("DELAY")) { %>  selected <% }%>    value="DELAY">延期</option>      		
      		<% 	}
      		} %>	
      			</select></td>
             <td width="4%" class="en11px" id="lifetype_<%=k%>" name="lifetype_<%=k%>" align="center"><%=UtilFormatOut.checkNull((String)map.get("lifeType"))%></td>      		       		      		
             <td width="4%" class="en11px" id="remainLife_<%=k%>" name="remainLife_<%=k%>" ><% if(map.get("lifeError")!=null ){ %><%=UtilFormatOut.checkNull((String)map.get("lifeError"))%><% }else{ %><%=UtilFormatOut.checkNull((String)map.get("remainLife"))%><% } %></td>            
             <td width="10%" class="en11px">          		
							<select id="vendor_<%=k%>" name="vendor_<%=k%>"  readonly="readonly" style="background:E9E9E9;width:95%">
									<option value='<%=UtilFormatOut.checkNull((String)map.get("VENDOR"))%>' selected><%=UtilFormatOut.checkNull((String)map.get("VENDOR"))%></option>
							</select>          		
          	 </td>
           	<%
           		if(UtilFormatOut.checkNull((String)map.get("ISCLEAN")).equals("Y")){
           			List seriesNoList=(List)map.get("seriesNoList");
           	%>
           	 <td width="8%" class="en11px">
           	 	<select style="width:95%" id="seriesno_<%=k%>" name="seriesno_<%=k%>" readonly="readonly" style="background:E9E9E9;width:95%" >
           	 		<option value=""></option>
           	<%		if(seriesNoList!=null&&seriesNoList.size()>0){
           				for(int s=0;s<seriesNoList.size();s++){
           	%>	
           	 		<option <% if( UtilFormatOut.checkNull((String)seriesNoList.get(s)).equals( UtilFormatOut.checkNull((String)map.get("SERIES_NO")) ) ){ %>  selected <% }%> value='<%=UtilFormatOut.checkNull((String)seriesNoList.get(s))%>'><%=UtilFormatOut.checkNull((String)seriesNoList.get(s))%></option>           							
           	<%			}
           			}
           	%>
           	 	</select></td>
           	<%	}else{ %>           		
             <td width="8%" class="en11px"><input id="seriesno_<%=k%>" name="seriesno_<%=k%>" class="input" size=10 maxlength="20" type="text" readonly="readonly" style="background:E9E9E9;width:95%" value='<%=UtilFormatOut.checkNull((String)map.get("SERIES_NO"))%>'/></td>
           	<% 	} %>
             <td width="8%" class="en11px"><input id="baseno_<%=k%>" name="baseno_<%=k%>" class="input" size=10 maxlength="20" type="text" readonly="readonly" style="background:E9E9E9;width:95%" value='<%=UtilFormatOut.checkNull((String)map.get("BASE_SN"))%>'/></td>             
             <td width="4%" class="en11px"><input id="initlife_<%=k%>" name="initlife_<%=k%>" class="input" maxlength="20" type="text" readonly="readonly" style="background:E9E9E9;width:95%" value='<%=UtilFormatOut.checkNull((String)map.get("INIT_LIFE"))%>'/></td>
             <td width="4%" class="en11px"><input id="delaytime_<%=k%>" name="delaytime_<%=k%>" class="input" maxlength="20" type="text" readonly="readonly" style="background:E9E9E9;width:95%" value='<%=UtilFormatOut.checkNull((String)map.get("DELAYTIME"))%>'/></td>
             <td width="10%" class="en11px"><input id="offline_<%=k%>" name="offline_<%=k%>" class="input" maxlength="20" type="text" readonly="readonly" style="background:E9E9E9;width:95%" value='<%=UtilFormatOut.checkNull((String)map.get("OFF_LINE"))%>'/></td>
             <td width="8%" class="en11px"><input id="remark_<%=k%>" name="remark_<%=k%>" class="input" maxlength="40" type="text" readonly="readonly" style="background:E9E9E9;width:95%" value='<%=UtilFormatOut.checkNull((String)map.get("REMARK"))%>'/></td>           
           </tr>
          <%
          k++;
            }
          } 
          %>
      </table>
      </div>
    </td>
  </tr>
  </table>
  
  </div>

  <div id="checkList" class="tab-content">
	<table width="100%" border="0" cellspacing="1" cellpadding="2">
		<tr>
			<td>
				<div style="width:320px; height: 100px; background:#C7EDCC; border: 1px solid black;float:left">
					<div style="width: 100%;height: 100%;">
						<textarea id="TextArea1" cols="20" rows="2" style="width: 100%;height:100%;"></textarea>
					</div>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:Capture('<%=abnormalIndex%>')"><span>&nbsp;拍照&nbsp;</span></a></li>
					<li><a class="button-text" href="javascript:manageFile()"><span>&nbsp;上传&nbsp;</span></a></li>
				</ul>
			</td>
		</tr>
		<input id="uploadIndex" type="hidden" name="uploadIndex"/>
	</table>
  </div>
</div>
</fieldset>
</form>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:history.back(-1)"><span>&nbsp;返回&nbsp;</span></a></li>
			<li><a class="button-text" href="javascript:manageFile()"><span>&nbsp;Check List&nbsp;</span></a></li>
	</ul></td>
	<%
	if (!formLockStatus.equals(Constants.SUBMITED)){
	if ("".equals(formLockStatus)){
	 %>
	<td><ul class="button">
		<li><a class="button-text" id="tempFlow" href="javascript:flowLeadSubmit('<%=Constants.SUBMIT_MODIFY%>');" ><span>&nbsp;申请修改&nbsp;</span></a></li> 
	</ul></td>
	<%
	}else if ("LOCKED".equals(formLockStatus)){
		if (userId.equals(formLockUser)){
	 %>
	 <td><ul class="button">
			<li><a class="button-text" href="javascript:editForm('<ofbiz:inputvalue entityAttr="abnormal" field="abnormalIndex"/>','<ofbiz:inputvalue entityAttr="abnormal" field="equipmentId"/>')"><span>&nbsp;修改&nbsp;</span></a></li> 
	</ul></td>
	<% }}
	}%>
	</tr>
</table>

<%
List followJobList = (List) request.getAttribute("FOLLOWJOB_LIST");
if(followJobList != null && followJobList.size() > 0) {
%>
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>问题追踪列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="20%" class="en11pxb">名称</td>
          <td width="25%" class="en11pxb">目的</td>
          <td width="10%" class="en11pxb">部门</td>
          <td width="10%" class="en11pxb">课别</td>
          <td width="8%" class="en11pxb">状态</td>
          <td width="7%" class="en11pxb">创建者</td>
          <td width="15%" class="en11pxb">创建时间</td>
        </tr>
        <%
   		for(Iterator it = followJobList.iterator();it.hasNext();) {
			Map map = (Map)it.next();
		%>
	        <tr bgcolor="#DFE1EC">
	          <td class="en11px">
	            <a href="#" onclick="editFollowJob('<%=map.get("STATUS")%>','<%=map.get("FOLLOW_INDEX")%>')">
	                <%=UtilFormatOut.checkNull((String)map.get("FOLLOW_NAME"))%>
	            </a>
	          </td>
	          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("PURPOSE"))%></td>
	          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("EQUIPMENT_DEPT"))%></td>
	          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("SECTION"))%></td>
	          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("CSTATUS"))%></td>
	          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("CREATOR"))%></td>
	          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("CREATE_TIME"))%></td>
	        </tr>
	  <%}%>
      </table>
      </fieldset></td>
  </tr>
</table>
<%}%>

<div id='ActiveXDivOne'>
</div>