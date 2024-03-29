<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.*"%>

<%
	Map paramMap = (Map)request.getAttribute("paramMap");
	String activate=(String)request.getAttribute("activate");
	GenericValue pmForm=(GenericValue)request.getAttribute("pmForm");
	String pmIndex = (String)pmForm.getString("pmIndex");
	String equipmentId = (String)pmForm.getString("equipmentId");
	String periodIndex = (String)pmForm.getString("periodIndex");
	GenericValue defaultPeriod = (GenericValue)request.getAttribute("defaultPeriod");
	String periodName = defaultPeriod.getString("periodName");
	double dblStdHour = defaultPeriod.getDouble("standardHour").doubleValue();//标准工时
	boolean isFab5 = Constants.CALL_ASURA_FLAG;

	List eqpStatusList=(List)request.getAttribute("eqpStatusList");
	String formType = pmForm.getString("formType");
	int status = Integer.parseInt(pmForm.getString("status"));
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
	String flowIndex = "";
	for (int i=0; i<jobList.size(); i++) {
		Map job = (Map) jobList.get(i);
		if (i==0) {
		    jobStr = job.get("SEQ_INDEX").toString();
		    flowIndex = job.get("JOB_INDEX").toString();//mcs更换备耗件: 取第一个处理流程
		} else {
	    	jobStr = jobStr+":"+job.get("SEQ_INDEX");
		}
	}
	int psize=0;
	List partsUseList=(List)request.getAttribute("PARTS_USE_LIST");
	if(partsUseList!=null) {
		psize=partsUseList.size();
	}
	int size=0;
	List keyPartsUseList=(List) request.getAttribute("keyPartsUseList");
	if(keyPartsUseList!=null) {
		size=keyPartsUseList.size();
	}
%>

<%@ include file="../../yui-ext/ext-comdlg.jsp"%>
<%@ include file="../../yui-ext/ext-comdlg-ajaxsub.jsp"%>
<%@ include file="../../yui-ext/ext-comdlg-ajaxsub1.jsp"%>
<!-- <script src="<%=request.getContextPath()%>/camera/WsUtil.js" type="text/javascript" charset="utf-8"></script>
<script src="<%=request.getContextPath()%>/camera/OcxUtil.js" type="text/javascript" charset="utf-8"></script>
<script src="<%=request.getContextPath()%>/camera/axCam_Ocx.js" type="text/javascript" charset="utf-8" for="axCam_Ocx"
	event="MessageCallback(type,str)"></script>
<script src="<%=request.getContextPath()%>/camera/axCam_Ocx2.js" type="text/javascript" charset="utf-8" for="axCam_Ocx2"
	event="MessageCallback(type,str)"></script>
<script src="<%=request.getContextPath()%>/camera/cbCam.js" type="text/javascript" charset="utf-8"></script> -->
<script language="javascript">
	//false:处理程程序未处理完毕;处理完毕状态为：true
	var dealJobStatus=false;
    var eventSub;
    var event;
    var anormalReason;
 	var overTimeIndexObj;
	var tabs;
	var pmInfo;
	var flow;
	var parts;
	// var checkList;
   	//改变弹出框大小width,height
	extDlg.dlgInit('700','400');
	eytDlg.dlgInit('700','300');
    eytDlg1.dlgInit('700','300');

	//新增功能弹出页
	function addParts(obj){
		getPartsNo();
	}

	//选择part批次
	function selectBatch(obj, partNo, partsUseId, seqIndex) {
		var url='<ofbiz:url>/specifyPartBatch</ofbiz:url>?partNo='+partNo+'&partsUseId='+partsUseId+'&eventIndex='+seqIndex+'&eqpId=<%=equipmentId%>';
		var result=window.open (url,"parts","top=130,left=240,width=650,height=350,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=0");
	}

	//修改功能弹出页
	function editParts(obj,seqIndex,keyPartUseId,templateCount,eqty,partCount,partType){
		// Ext.MessageBox.confirm('表单暂存确认', '在修改parts前请您[暂存]表单,否则会导致表单信息丢失!',function result(value){
		// if(value=="yes"){
	    //     	Ext.get('seqIndex').dom.value=seqIndex;
		// 		var url='<ofbiz:url>/partsUseInfoQueryEntry</ofbiz:url>?seqIndex='+seqIndex;
		// 		extDlg.showEditDialog(obj,url);
		// 	}else{
		// 		return;
		// 	}
	    //  });
		debugger;
		Ext.get('seqIndex').dom.value=seqIndex;
		Ext.get('keyPartUseId').dom.value=keyPartUseId;
		// Ext.get('materialIndex').dom.value=materialIndex;
		if(templateCount=="null"){
		Ext.get('templateCount').dom.value="";
		}else{
		Ext.get('templateCount').dom.value=templateCount;
		}
		// if(partType!=null&&partType=="新品"){
		// 	Ext.get('eqty').dom.value=parseInt(eqty)+parseInt(partCount);
		// }else{
			Ext.get('eqty').dom.value=eqty;   
		// }
		var url='<ofbiz:url>/partsUseInfoQueryEntry</ofbiz:url>?seqIndex='+seqIndex;
		extDlg.showEditDialog(obj,url,templateCount);
	}

	//选择物料
    function partsSelect(obj,partsId,isUsed){
    	debugger;    	
        var id=obj.id;
        var valueArray=id.split("_");             
        
        var errorSeriesNo=document.getElementById("errorSeriesNo_"+valueArray[1]).value;
        var seriesNoObj=document.getElementById("seriesno_"+valueArray[1]);
        var isClean=document.getElementById("isClean_"+valueArray[1]).value;
        if(isClean=="Y"&&isUsed=="Y"&&errorSeriesNo!=null&&errorSeriesNo!=""){
			for(i=0;i<seriesNoObj.options.length;i++){
				if(seriesNoObj[i].value==errorSeriesNo){
					seriesNoObj.options.remove(i);
				}
			}
        }

        if(obj.checked==true){ 
            document.getElementById("remark_"+valueArray[1]).readOnly=false;
            document.getElementById("remark_"+valueArray[1]).style.background="#FFFFFF";            
            if(document.getElementById("newparts_type"+valueArray[1]).value=="DELAY"){
            	document.getElementById("newparts_type"+valueArray[1]).readOnly=true;
            	document.getElementById("newparts_type"+valueArray[1]).style.background="#E9E9E9";
            	document.getElementById("vendor_"+valueArray[1]).readOnly=true;
           	 	document.getElementById("vendor_"+valueArray[1]).style.background="#E9E9E9";
            	document.getElementById("seriesno_"+valueArray[1]).readOnly=true;
            	document.getElementById("seriesno_"+valueArray[1]).style.background="#E9E9E9";     
             	document.getElementById("baseno_"+valueArray[1]).readOnly=true;
            	document.getElementById("baseno_"+valueArray[1]).style.background="#E9E9E9";
            	document.getElementById("offline_"+valueArray[1]).readOnly=true;
           		document.getElementById("offline_"+valueArray[1]).style.background="#E9E9E9";            
            }else{
             document.getElementById("newparts_type"+valueArray[1]).readOnly=false;
             document.getElementById("newparts_type"+valueArray[1]).style.background="#FFFFFF";  
             document.getElementById("vendor_"+valueArray[1]).readOnly=false;
             document.getElementById("vendor_"+valueArray[1]).style.background="#FFFFFF";
             document.getElementById("seriesno_"+valueArray[1]).readOnly=false;
             document.getElementById("seriesno_"+valueArray[1]).style.background="#FFFFFF";          
             document.getElementById("baseno_"+valueArray[1]).readOnly=false;
             document.getElementById("baseno_"+valueArray[1]).style.background="#FFFFFF";
	            document.getElementById("offline_"+valueArray[1]).readOnly=false;
	            document.getElementById("offline_"+valueArray[1]).style.background="#FFFFFF";
	        }
            if(document.getElementById("newparts_type"+valueArray[1]).value=="OLD")
            	{
            	   document.getElementById("initlife_"+valueArray[1]).readOnly=false;
                   document.getElementById("initlife_"+valueArray[1]).style.background="#FFFFFF";  
            	}
        }else{   
             document.getElementById("newparts_type"+valueArray[1]).readOnly=true;
             document.getElementById("newparts_type"+valueArray[1]).style.background="#E9E9E9";
             document.getElementById("vendor_"+valueArray[1]).readOnly=true;
             document.getElementById("vendor_"+valueArray[1]).style.background="#E9E9E9";
             document.getElementById("seriesno_"+valueArray[1]).readOnly=true;
             document.getElementById("seriesno_"+valueArray[1]).style.background="#E9E9E9";     
             document.getElementById("baseno_"+valueArray[1]).readOnly=true;
             document.getElementById("baseno_"+valueArray[1]).style.background="#E9E9E9";
            document.getElementById("remark_"+valueArray[1]).readOnly=true;
            document.getElementById("remark_"+valueArray[1]).style.background="#E9E9E9";    
            document.getElementById("initlife_"+valueArray[1]).readOnly=true;
            document.getElementById("initlife_"+valueArray[1]).style.background="#E9E9E9";  
            document.getElementById("offline_"+valueArray[1]).readOnly=true;
            document.getElementById("offline_"+valueArray[1]).style.background="#E9E9E9";  
        }
        // vendersChange(partsId,valueArray[1]);
    }
    function vendersChange(partsId,index){
        var actionURL='<ofbiz:url>/getVendersByPartsId</ofbiz:url>?partsId='+partsId+'&indexId='+index;
		Ext.lib.Ajax.formRequest('pmForm',actionURL,{success: commentSuccess_v, failure: commentFailure});
    }
    
	function commentSuccess_v(o_v){
		 var result = eval('(' + o_v.responseText + ')');
		 	var indexIdArray=result.indexIdArray;
			var vendorsArray=result.vendorsArray;
			var indexId=indexIdArray[0];
			// var vendor=document.getElementById("vendor_"+indexId);
			var vendorSize=result.vendorsArray.length;
			vendor.length=1;
			for(var i=0;i<vendorSize;i++){
				vendor.options[vendor.length]=new Option(vendorsArray[i],vendorsArray[i]);
			}
	}
	function submitSuccess_y(o_y){
		var result = eval('(' + o_y.responseText + ')');
		var index=result.nameIndex;
		var eMsg=result.delayRst;		
		var partsId=Ext.get('partsId_1').dom.value;
		
		// document.getElementById("vendor_"+index).options[0]=new Option(result.vendor,result.vendor);
		// document.getElementById("vendor_"+index).options[0].selected = true;		
		
		vendersChange(partsId,index);
		//Ext.get('vendor_'+index).dom.value=result.vendor;
		Ext.get('seriesno_'+index).dom.value=result.seriesNo;
		Ext.get('baseno_'+index).dom.value=result.baseSn;
		Ext.get('initlife_'+index).dom.value=result.initLife;
		Ext.get('delaytime_'+index).dom.value=result.delaytime;
		Ext.get('isdelayed_'+index).dom.value="1";
		
		Ext.get('post-error-y').radioClass('active-msg');
        Ext.get('post-error-msg-y').update(eMsg);
		//alert(eMsg);
	}
	function submitFailure_y(o_y){
		var result = eval('(' + o_y.responseText + ')');
		var eMsg=result.delayRst;
		Ext.get('post-error-y').radioClass('active-msg');
        Ext.get('post-error-msg-y').update(eMsg);	
		//alert(eMsg);
	}
	function submitSuccess_y1(o_y1){
		var result = eval('(' + o_y1.responseText + ')');
		var eMsg=result.mustchangeCommRst;
		
		var mtrsize = Ext.DomQuery.select('tr',Ext.getDom('mInfo')).length;
		var mcheckArray=document.getElementsByName("mCheckBox");
		if(mcheckArray!=null || mcheckArray.length>0){
			var mkeyUsedIdkArray=new Array();
			for(i=0;i<mcheckArray.length;i++){
				if(mcheckArray[i].checked){
					var index=mcheckArray[i].value;
					var mkeyUsedId=Ext.get('mKeyUseId_'+index).dom.value;
					mkeyUsedIdkArray[i]=mkeyUsedId;
				}
			}		
		}
		
		for(j=0;j<<%=size%>;j++){
			var keyUseId=Ext.get('keyuseid_'+j).dom.value;
			var keyUseIdUsed=Ext.get('keyUseIdUsed_'+j).dom.value;
			var mustchange=Ext.get('mustchange_'+j).dom.value;
			var errorRst=Ext.get('errorRst_'+j).dom.value;
			var checkObj=document.getElementById("parts_"+j);
			var keyUsedIdIndexOf=mkeyUsedIdkArray.indexOf(keyUseId);
			var keyUseIdUsedIndexOf=mkeyUsedIdkArray.indexOf(keyUseIdUsed);
			var divobj=Ext.get('check_'+j).dom;
			if(mustchange=="Y" && errorRst=="Y"){
				if(parseInt(keyUsedIdIndexOf)<0 && parseInt(keyUseIdUsedIndexOf)<0){
					Ext.get('mustchangeRemark_'+j).dom.value="0";
					divobj.style.display="";
				}else{
					Ext.get('mustchangeRemark_'+j).dom.value="1";
					checkObj.checked=false;
					divobj.style.display="none";
				}
			}
		}		
		
		Ext.get('post-error-y1').radioClass('active-msg');
        Ext.get('post-error-msg-y1').update(eMsg);
		//alert(eMsg);
	}
	function submitFailure_y1(o_y1){
		var result = eval('(' + o_y.responseText + ')');
		var eMsg=result.mustchangeCommRst;
		Ext.get('post-error-y1').radioClass('active-msg');
        Ext.get('post-error-msg-y1').update(eMsg);	
		//alert(eMsg);
	}
	function checkForm_y(){
		var dItrsize=Ext.DomQuery.select('tr',Ext.getDom('dInfo')).length;
		var dLtrsize=Ext.DomQuery.select('tr',Ext.getDom('dLife')).length;
		if(dItrsize=="1"){
			return "没有延期评估项，不能延期";
		}else{
			for(i=1;i<dItrsize;i++){
				var delayResult=Ext.get('delayResult_'+i).dom.value;
				if(delayResult==""){
					return "RESULT不能为空";
				}
			}
		}       
		var reg =/^[0-9]*[1-9][0-9]*$/;//验证非零的正整数     
		var delayLife=Ext.get('delayLife_'+1).dom.value;
		var errorSpec=Ext.get('errorSpec_'+1).dom.value;
		var actul=Ext.get('actul_'+1).dom.value;
		//var cRst=parseInt(delayLife)-parseInt(errorSpec)-parseInt(actul);
		var cRst=parseInt(delayLife)-parseInt(errorSpec)
		if(delayLife==""){
			return "延期数值不能为空";
		}
		if(reg.test(delayLife)==false){
			return "延期数值必须为正整数";
		}
		if(parseInt(cRst)>parseInt(0)){
			return "延期数值不能大于原寿命"+errorSpec;
			//return "延期数值不能大于原寿命"+errorSpec+"+实际使用寿命";
		}
        return "";
    }
	function checkForm_y1(){
		var mtrsize = Ext.DomQuery.select('tr',Ext.getDom('mInfo')).length;
		var mcheckArray=document.getElementsByName("mCheckBox");
		if(mcheckArray!=null || mcheckArray.length>0){
			for(i=0;i<mcheckArray.length;i++){
				if(mcheckArray[i].checked){
					var index=mcheckArray[i].value;
					var reason=Ext.get('mReason_'+index).dom.value;
					var remark=Ext.get('mRemark_'+index).dom.value;
					if(reason==""){
						return "未换原因不能为空";
					}
					if(remark==""){
						return "备注不能为空";
					}
				}
			}
		}
		return "";
	}

	var commentFailure_y = function(o){
        Ext.MessageBox.alert('Status', 'Unable to connect.');
   	};

	//修改时远程调用成功后给页面赋值
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('partNo').dom.value=result.partNo;
			Ext.get('partName').dom.value=result.partName;
			Ext.get('partCount').dom.value=result.partCount;
			Ext.get('partType').dom.value=result.partType;
			if (!(typeof(result.remark) == "undefined"))
            {
                Ext.get('remark').dom.value=result.remark;  
            }else{
                Ext.get('remark').dom.value='';
            }
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

   	function chkNewPartsBatch() {
	   	var needConfirm = false;
		var checked = document.getElementById("checkbox").checked;
		var checked2 = document.getElementById("checkbox2").checked;
		if (<%=isFab5%> && !checked && !checked2) {
			Ext.MessageBox.alert('警告', "请勾选是否使用配件");
			tabs.activate('parts');
	   		return;
		}
		for(i=0;i<<%=psize%>;i++){
  	       	var batchNum = document.getElementById("batchNum_"+i).value;
			var partType = document.getElementById("partType_"+i).value;
  	        if(partType == "新品" && batchNum == "Unspecified"){
				needConfirm = true;
				break;
           	} 
       	}
		if (needConfirm) {
			Ext.MessageBox.confirm('新品备件未指定批次', '是否继续？', function result(value){
				if(value=="yes"){
					overFormRecord();
				}else {return;}
			});
		}
		else {
			overFormRecord();
		}
   	}

    //完成(如果存在处理程序，须执行完后，方可操作)
    function overFormRecord(){
		for(i=0;i<<%=size%>;i++){
			var mustchangeRemark=document.getElementById("mustchangeRemark_"+i).value;
			var checkObj=document.getElementById("parts_"+i);
			var errorRst=Ext.get('errorRst_'+i).dom.value;
			var mustchange=Ext.get('mustchange_'+i).dom.value;
			var lifeError=document.getElementById("lifeError_"+i).value;
			var equipId='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
			var reg =/^ACWS02|ACWS03|BCWS04/;
			if(reg.test(equipId)==false){
				if(lifeError!=""){
					alert(checkObj.value+"fdc收值或fdcrelation异常,请check");
					return;
				}
			}
			
			if(mustchange=="Y"){
			if(mustchangeRemark=="0" && checkObj.checked==false){
				alert("必换件："+checkObj.value+"未更换,必须完成：必换件超期使用说明");
				return;
			} 
			}else{	   	
				if(errorRst=="Y"){
					alert(checkObj.value+"已超期，必须延期或更换");
					return;
				}     
			}
			
		}
		var checked=isChecked();

		if (Ext.get('formType').dom.value == "NORMAL"){
			var actionURL='<ofbiz:url>/getSystemTimestamp</ofbiz:url>';
			Ext.lib.Ajax.formRequest('pmForm',actionURL,{success: getReturnTime, failure: commentFailure});
		} else {
			var actionURL='<ofbiz:url>/pmFormJobStatusQueryEntry</ofbiz:url>?jobStr=<%=jobStr%>';
			Ext.lib.Ajax.formRequest('pmForm',actionURL,{success: commentQueryJobStatusSuccess, failure: commentFailure});
		}
		
    }

    // 得到结束时间并校验
    function getReturnTime(o){
   		var result = eval('(' + o.responseText + ')');
		if (result!=null & result!=""){
			Ext.get('returnTime').dom.value = result.time;
		}

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
	   		Ext.MessageBox.confirm('结束表单确认', '您确信要完成此保养表单吗？',function result(value){
		        if(value=="yes"){
					var actionURL='<ofbiz:url>/pmFormManageOverEntry</ofbiz:url>';
					loading();
					pmForm.action=actionURL;
					pmForm.submit();
				}else{
					return;
				}
	        });
		}
	}

	//判断是否选择关键备件
    function isChecked(){
     for(i=0;i<<%=size%>;i++){
       var checkObj=document.getElementById("parts_"+i);
           if (checkObj.checked==true){
            return true;
           }
      }
      return false;
    }

	//删除物料信息
	function delPartsUse(seqIndex, keyPartsUseId){
	 Ext.MessageBox.confirm('删除确认', '确信要删除此物料前，请您[暂存]表单,否则会导致表单信息丢失!',function result(value){
	        if(value=="yes"){
				var eqpId='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>'
				var pmIndex='<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>'
				var actionURL='<ofbiz:url>/pmFormPartsDelEntry</ofbiz:url>?functionType=1&seqIndex='+seqIndex+'&keyuseid='+keyPartsUseId+"&pmIndex="+pmIndex;
				loading();
				document.location.href=actionURL;
			}else{
				return;
			}
        });
	}

    //暂存
    function tempSaveForm(){
	   	var checked=isChecked();    
		for(i=0;i<<%=size%>;i++){
			var checkObj=document.getElementById("parts_"+i);
			var inputObja=document.getElementById("vendor_"+i);
			var inputObjb=document.getElementById("seriesno_"+i);
			var inputObjc=document.getElementById("baseno_"+i);
			var inputObjd=document.getElementById("newparts_type"+i);
			var inputObje=document.getElementById("initlife_"+i);
			var inputRemarkValue=document.getElementById("remark_"+i).value;
			var mtrNum=document.getElementById("mtrNum_"+i).value;
			var useNum=document.getElementById("useNum_"+i).value;
			var kqty=document.getElementById("kqty_"+i).value;
			var kusedNum=document.getElementById("kusedNum_"+i).value;
			var isdelayed=document.getElementById("isdelayed_"+i).value;
			var mustchangeRemark=document.getElementById("mustchangeRemark_"+i).value;
			var lifeError=document.getElementById("lifeError_"+i).value;
			var equipId='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
			var reg =/^ACWS02|ACWS03|BCWS04/;
			if (checkObj.checked==true){
				if(isdelayed=="1"){
					if(inputObjd.value!="DELAY"){
						alert(checkObj.value+"已做了延期的,类别只能是延期");
						return;
					}
				}
				if(inputObjd.value!="DELAY"){
					if(inputObja.value==""||inputObjb.value==""||inputObjc.value==""){
						alert("已选择的物料，必须输入Vendor,Series No.,base S/N!");
						return;
					} 
					if(inputObjd.value=="OLD"  && (inputObje.value=="" || !IsNumeric(inputObje.value)))       
						{
						alert("OLD类型，必须输入已使用寿命");
						return;
						}
										
					if(inputRemarkValue.length>50){
						alert("备注长度不能超过50个字符!!");
						return;
					}
				}else{
					if(isdelayed=="0"){
						alert("类别为延期，必须做延期操作");
						return;	               		
					}
				}
			}else{
				if(isdelayed=="1"){
					alert(checkObj.value+"已做了延期的,必须勾选");
					return;
				}
			}
		}
		loading();
		pmForm.submit();
	}

	function keyPartsDelaySet(obj,keypartsid,partsid,delaytime,initLife,keyUseId,keyUseIdUsed,nameIndex,actul){
		var url="<%=request.getContextPath()%>/control/keypartsdelayinfo?nameIndex="+nameIndex+"&actul="+actul+"&keyPartsId="+keypartsid+"&keyUseId="+keyUseId+"&keyUseIdUsed="+keyUseIdUsed+"&eventIndex="+'<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>';    		        
        eytDlg.showEditDialog(obj,url);
	}

	//获取partNo
	function getPartsNo(obj){
		var par=new Array();
		//设备ID，formType值为PM时需要
		//PM周期,formType值为PM时需要
		//打开partsInfoList.jsp，显示parts_data基础表数据
		/*var result=window.open ("<%=request.getContextPath()%>/control/intoPmFormPartsQueryEntry?formType=PM&eqpId=<%=equipmentId%>&periodIndex=<%=periodIndex%>&eventIndex=<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>","parts","top=130,left=240,width=850,height=650,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=0");*/
		//var result=window.showModalDialog ("<%=request.getContextPath()%>/control/intoPmFormPartsQueryEntry?formType=PM&eqpId=<%=equipmentId%>&periodIndex=<%=periodIndex%>&eventIndex=<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>",par,"dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");

		// mcs更换备耗件接口: pm/ts using parts
		// var result = window.open ("<%=request.getContextPath()%>/control/useSparePartEntry?eventType=PM&eqpId=<%=equipmentId%>&period=<%=periodIndex%>&flow=<%=flowIndex%>&eventIndex=<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>", "parts","top=130,left=240,width=850,height=650,title=,channelmode=0," +
        //         "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
        //         "scrollbars=1,status=1,titlebar=0,toolbar=0");

		// 关键备件使用接口
		var result=window.open ("<%=request.getContextPath()%>/control/pmFormPartsQueryEntry?formreturn=new&formType=PM&eqpId=<%=equipmentId%>&periodIndex=<%=periodIndex%>&eventIndex=<ofbiz:inputvalue entityAttr='pmForm' field='pmIndex'/>","parts","top=130,left=240,width=650,height=350,title=,channelmode=0," +
                "directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
                "scrollbars=1,status=1,titlebar=0,toolbar=0");

        // mcs按数量使用接口
		//var result = window.showModalDialog("<%=request.getContextPath()%>/control/useMaterialByQtyEntry?eventType=PM&eqpId=<%=equipmentId%>&eventIndex=<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>", window, "dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");
		//document.location.href="<%=request.getContextPath()%>/control/pmRecordInfo?functionType=1&activate=parts&pmIndex=<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>&eqpId=<%=equipmentId%>";
	}

	//查询设备状态
	function queryEqpStatus(){
		var eqpId='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
		var actionURL='<ofbiz:url>/pmFormEqpStatusQueryEntry</ofbiz:url>?eqpId='+eqpId;
		//var actionURL='<ofbiz:url>/pmFormEqpStatusQueryEntry</ofbiz:url>?eqpId=MCS7280';
		Ext.lib.Ajax.formRequest('pmForm',actionURL,{success: commentQuerySuccess, failure: commentFailure});
	}

	//Fab1制造作业PM后QC, 制造作业PM返工后QC, PM返工
	function pmQcTime(promisStatus){
		var pmIndex='<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>';
		var eqpId='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
		var actionURL="<ofbiz:url>/pmQcTime</ofbiz:url>?pmIndex="+pmIndex+"&eqpId="+eqpId+"&promisStatus="+promisStatus;
		//alert(actionURL);
        Ext.lib.Ajax.formRequest('pmForm',actionURL,{success: commentUpdateSuccess, failure: commentFailure});
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
		var changeFlag = '<ofbiz:inputvalue entityAttr="pmForm" field="isChangeStatus"/>';
		if('N' == changeFlag) {
			Ext.MessageBox.alert('警告', "该周期不能修改设备状态!");
   			return;
		}
		var eqpId='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
		var eqpState=Ext.get('eqpState').dom.value;
		if(eqpState==''){
			Ext.MessageBox.alert('警告', "请选择设备状态!");
   			return;
		}
		var actionURL='<ofbiz:url>/pmFormEqpStatusUpdateEntry</ofbiz:url>?eqpId='+eqpId+'&eqpState='+eqpState;
		Ext.lib.Ajax.formRequest('pmForm',actionURL,{success: commentUpdateSuccess, failure: commentFailure});
	}

	//使用旧靶，开启MCS开关
	function useOldTarget(){
		var eqpId = '<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
		var actionURL="<ofbiz:url>/updateMcsPmChangeTarget</ofbiz:url>?eqpId="+eqpId;
        //alert(actionURL);
        Ext.lib.Ajax.formRequest('pmForm',actionURL,{success: commentUpdateSuccess, failure: commentFailure});
	}

	function delayCheck(obj,partsType,divid,initlifeid,checkboxid,vendorid,seriesnoid,basenoid,offlineid,remarkid){
		if(document.getElementById(checkboxid).checked==true){
			if(partsType=="DELAY"){				
				if(document.getElementById(divid)!=null){
				document.getElementById(divid).style.display="";				
				}				
             	// document.getElementById(vendorid).readOnly=true;
             	// document.getElementById(vendorid).style.background="#E9E9E9";
             	document.getElementById(seriesnoid).readOnly=true;
             	document.getElementById(seriesnoid).style.background="#E9E9E9";     
             	document.getElementById(basenoid).readOnly=true;
             	document.getElementById(basenoid).style.background="#E9E9E9";  
            	document.getElementById(initlifeid).readOnly=true;
            	document.getElementById(initlifeid).style.background="#E9E9E9";  
            	document.getElementById(offlineid).readOnly=true;
            	document.getElementById(offlineid).style.background="#E9E9E9"; 
            	document.getElementById(remarkid).readOnly=false;
            	document.getElementById(remarkid).style.background="#FFFFFF"; 
			}else{
				if(document.getElementById(divid)!=null){
				document.getElementById(divid).style.display="none";				
				}				
				// document.getElementById(vendorid).readOnly=false;
             	// document.getElementById(vendorid).style.background="#FFFFFF";
             	document.getElementById(seriesnoid).readOnly=false;
             	document.getElementById(seriesnoid).style.background="#FFFFFF";     
             	document.getElementById(basenoid).readOnly=false;
             	document.getElementById(basenoid).style.background="#FFFFFF";  
            	document.getElementById(initlifeid).readOnly=false;
            	document.getElementById(initlifeid).style.background="#FFFFFF";  
            	document.getElementById(offlineid).readOnly=false;
            	document.getElementById(offlineid).style.background="#FFFFFF"; 
            	document.getElementById(remarkid).readOnly=false;
            	document.getElementById(remarkid).style.background="#FFFFFF"; 
			}
			if(partsType=="OLD"){
				document.getElementById(initlifeid).readOnly=false;
		        document.getElementById(initlifeid).style.background="#FFFFFF";
			}else{
				document.getElementById(initlifeid).readOnly=true;
	            document.getElementById(initlifeid).style.background="#E9E9E9";  
			}
		}	
	}

	function getMustChangeErrorRemark(obj,eventIndex,eqpId){
        var url='<ofbiz:url>/getMustChangeErrorRemark</ofbiz:url>?eventIndex='+eventIndex+'&eqpId='+eqpId;
		eytDlg1.showEditDialog(obj,url);
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
   		var startTime=Ext.get('startTime').dom.value;
   		var returnTime=Ext.get('returnTime').dom.value;

   		var startDate=Date.parseDate(startTime, "Y-m-d H:i:s");
	   	var returnDate=Date.parseDate(returnTime, "Y-m-d H:i:s");

   		var errorStr="";
   		if(startTime==""){
   			errorStr='保养开始时间不能为空!';
   			return errorStr;
   		}

   		if (Ext.get('formType').dom.value=="PATCH"){
   			if(startDate >= returnDate) {
   				return '开始时间不能大于结束时间';
   			}
   		}

		var jobText=Ext.get('jobText').dom.value;

		//完成表单时校验，暂存时不需要校验
		if(flag=="1"){
		    if(returnTime==""){
       			errorStr='回线时间不能为空!';
       			return errorStr;
       		}

 			if(dealJobStatus==false){
 				errorStr='必须把处理程序完成!';
 				return errorStr;
 			}
		}

		if(jobText==""){
			errorStr='请输入[处理程序]中的处理过程!';
			return errorStr;
		}

   		//普通表单：校验PM结束时间是否超时或过短
   		if (Ext.get('formType').dom.value=="NORMAL"){
	   		var overTimeIndex=Ext.getDom('overTimeIndex').value;
	   		var workHour = (returnDate - startDate)/3600000;

	   		if (overTimeIndex == null || overTimeIndex == ""){
		   		if (workHour > <%=dblStdHour%>*1.1){
		   			errorStr="保养超时，请选择超时原因!";
		   			return errorStr;
		   		}else if (workHour < <%=dblStdHour%>*0.5){
		   			errorStr="保养过短，请选择过短原因!";
   	                return errorStr;
		   		}
	   		}

	   		var overTimeComment=Ext.getDom('comment').value;
	   		if (overTimeIndex=="0" && overTimeComment=="") {
	   		    errorStr="选择other时，必须填写超时备注!";
   	            return errorStr;
	   		}

	   		if (typeof(pmForm.holdEnabled) != "undefined") {
    		    /*if (pmForm.triggerStage.value == "") {
    		        errorStr = 'hold触发Stage不能为空!';
           			return errorStr;
    		    }*/

    		    if (pmForm.holdCodeReason.value == "") {
    		        errorStr = 'Hold码:Hold原因不能为空!';
           			return errorStr;
    		    }
    		}
   		}

   		return errorStr;
   }

	//新建问题追踪
    function saveFollowJob(){
   		var url='<ofbiz:url>/intoAddQuFollow</ofbiz:url>?type=FORM&eventType=PM&eventIndex=<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>&eqpId=<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>';
		windowOpen(url,'新建问题追踪',685,400);
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

    //数据合法性校验
    function checkForm(){
		var partNo = Ext.get('partNo').dom.value;
        var partName = Ext.get('partName').dom.value;
        var partCount=Ext.get('partCount').dom.value;
		var remark=Ext.get('remark').dom.value;
        var templateCount=Ext.get('templateCount').dom.value;
        var qty=Ext.get('eqty').dom.value;
        var pratType=Ext.get('partType').dom.value;
		var reg=/^\d+$/;
		if(partNo==""){
			return "物料号不能为空";
		}
		if(partName==""){
			return "物料名不能为空";
		}
		if(partCount==""){
			return "数量不能为空";
		}
		if(reg.test(partCount)==false){
        	return "使用数量必须为正整数";
        }
        if(remark.length>100){
            return "备注长度不能超过50个字符!";
        }
        if(eval(partCount)==0 && remark==""){
            return "请输入使用数字为0的原因";
        }
        if(pratType=="NEW"){
			if(parseInt(qty)<parseInt(partCount)){
				return "使用数量不能大于库存余量";
			}
        }
		return "";
	}

    function enterJobText(){
   		//Ext.get('jobText').dom.value=Ext.get('jobContent').dom.value;
    }

	function overTimeReason(){
    	if(overTimeIndexObj.getDisplayField(overTimeIndexObj.getValue())=="other"){
    		Ext.getDom('remark1').style.display='';
    	}
    	else{
    	    Ext.getDom('comment').value = "";
    		Ext.getDom('remark1').style.display='none';
    	}
	}

   //页面init时
   Ext.onReady(function(){
		// loadActiveX();
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
	   	/*if(overTimeIndexObj.getDisplayField(overTimeIndexObj.getValue())=="other"){
	   		Ext.getDom('remark').style.display='';
	   	}
	   	else{
	   		Ext.getDom('remark').style.display='none';
	   	}*/


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

	    //NORMAL：需要修改设备状态，不能修改异常时间
	    //PATCHL：不需要修改设备状态，可以修改异常时间
	    <%if("NORMAL".equalsIgnoreCase(formType)){%>
		     var eqpState = new Ext.form.ComboBox({
		        typeAhead: true,
		        triggerAction: 'all',
		        transform:'eqpState',
		        width:170,
		        forceSelection:true
		    });
	    <%}%>

	   //注册sheet
	   tabs = new Ext.TabPanel('tabs');
       pmInfo = tabs.addTab('pmInfo', "保养信息");
       flow = tabs.addTab('flow', "处理程序");
	   parts = tabs.addTab('parts', "使用配件");
	   tabs.activate('parts');
	//    checkList = tabs.addTab('checkList', "check list");
       <% if(("parts").equals(activate)){ %>
       		tabs.activate('parts');
       <%}else{%>
       		tabs.activate('pmInfo');
       <%}%>
   });

   function commentSuccess_y1(o_y1){
		var mtrsize = Ext.DomQuery.select('tr',Ext.getDom('mInfo')).length;
    	if(mtrsize>1){
		       	for(i=0;i<mtrsize-1;i++){
			       	var row = Ext.DomQuery.select('tr',Ext.getDom('mInfo'))[mtrsize-i-1];
			       	Ext.Element.get(row).remove();
		       	}
	    }
		if(o_y1.responseText!=null && o_y1.responseText!=""){
			var result = eval('(' + o_y1.responseText + ')'); 
			var mustchangeCommedArray=result.mustchangeCommedArray;
			var mustchangeCommreqArray=result.mustchangeCommreqArray;
			var tdIndex=0;
			for(j=0;j<mustchangeCommedArray.length;j++){
				var mustchangeCommed=mustchangeCommedArray[j];
				orderNum=tdIndex;				
				tdIndex++;
	       		Ext.get('mInfo').createChild({
	       			tag:'tr', style:'background-color:#DFE1EC', children: [
	       				{tag: 'td', html:'<input style="width:95%" type="checkBox" name="mCheckBox"  value="'+orderNum+'" checked />',cls:'en11pxb',align:'center'},
	       				{tag: 'td', html:'<input name="mKeydesc_'+orderNum+'" type="hidden", style="width:95%", cls="input" value="'+mustchangeCommed.keydesc+'" />'+mustchangeCommed.keydesc
						    +'<input name="mKeyPartsMustchangeCommId_'+orderNum+'" type="hidden", style="width:95%", cls="input" value="'+mustchangeCommed.keyPartsMustchangeCommId+'" />'
	       					+'<input name="mKeyPartsId_'+orderNum+'" type="hidden", style="width:95%", cls="input" value="'+mustchangeCommed.keyPartsId+'" />'
	       					+'<input name="mKeyUseId_'+orderNum+'" type="hidden", style="width:95%", cls="input" value="'+mustchangeCommed.keyUseId+'" />'
	       					,cls:'en11pxb',align:'center'},
	       				{tag: 'td', html:'<input name="mPartsId_'+orderNum+'" type="hidden", style="width:95%", cls="input" value="'+mustchangeCommed.partsId+'" />'+mustchangeCommed.partsId,cls:'en11pxb',align:'center'},
						{tag: 'td', html:'<input name="mPartsName_'+orderNum+'" type="hidden", style="width:95%", cls="input" value="'+mustchangeCommed.partsName+'" />'+mustchangeCommed.partsName,cls:'en11pxb',align:'center'},
						{tag: 'td', html:'<select name="mReason_'+orderNum+'", style="width:95%", readonly="readonly" >'
	       					+'<option value="'+mustchangeCommed.reason+'" selected >'+mustchangeCommed.reason+'</option><option value="无预算开PR" >无预算开PR</option><option value="转PO时间长" >转PO时间长</option><option value="因交货期长未到货" >因交货期长未到货</option><option value="其他" >其他</option></select>'
							,cls:'en11pxb',align:'center'},
						{tag: 'td', html:'<input name="mRemark_'+orderNum+'" type="text", style="width:95%", cls="input" value="'+mustchangeCommed.remark+'" />',cls:'en11pxb',align:'center'}
				]});			
			}
			for(k=0;k<mustchangeCommreqArray.length;k++){
				var mustchangeCommreq=mustchangeCommreqArray[k];
				orderNum=tdIndex;	
				tdIndex++;	
	       		Ext.get('mInfo').createChild({
	       			tag:'tr', style:'background-color:#DFE1EC', children: [
	       				{tag: 'td', html:'<input style="width:95%" type="checkBox" name="mCheckBox"  value="'+orderNum+'"  />',cls:'en11pxb',align:'center'},
	       				{tag: 'td', html:'<input name="mKeydesc_'+orderNum+'" type="hidden", style="width:95%", cls="input" value="'+mustchangeCommreq.keydesc+'" />'+mustchangeCommreq.keydesc
	       					+'<input name="mKeyPartsMustchangeCommId_'+orderNum+'" type="hidden", style="width:95%", cls="input" value="'+mustchangeCommreq.keyPartsMustchangeCommId+'" />'
	       					+'<input name="mKeyPartsId_'+orderNum+'" type="hidden", style="width:95%", cls="input" value="'+mustchangeCommreq.keyPartsId+'" />'
	       					+'<input name="mKeyUseId_'+orderNum+'" type="hidden", style="width:95%", cls="input" value="'+mustchangeCommreq.keyUseId+'" />'
	       					,cls:'en11pxb',align:'center'},
	       				{tag: 'td', html:'<input name="mPartsId_'+orderNum+'" type="hidden", style="width:95%", cls="input" value="'+mustchangeCommreq.partsId+'" />'+mustchangeCommreq.partsId,cls:'en11pxb',align:'center'},
						{tag: 'td', html:'<input name="mPartsName_'+orderNum+'" type="hidden", style="width:95%", cls="input" value="'+mustchangeCommreq.partsName+'" />'+mustchangeCommreq.partsName,cls:'en11pxb',align:'center'},
						{tag: 'td', html:'<select name="mReason_'+orderNum+'", style="width:95%", readonly="readonly" >'
	       					+'<option value="" ></option><option value="无预算开PR" >无预算开PR</option><option value="转PO时间长" >转PO时间长</option><option value="因交货期长未到货" >因交货期长未到货</option><option value="其他" >其他</option></select>'
	       					,cls:'en11pxb',align:'center'},
						{tag: 'td', html:'<input name="mRemark_'+orderNum+'" type="text", style="width:95%", cls="input" value="" />',cls:'en11pxb',align:'center'}
				]});
			}
			var trlength=Ext.DomQuery.select('tr',Ext.getDom('mInfo')).length-1;
			var url="<%=request.getContextPath()%>/control/mustchangeRemarkSaveEntry";
        	document.mustChangeForm.action=url+"?trlength="+trlength;
			
		}
	    
	}
	function submitSuccess_y1(o_y1){
		var result = eval('(' + o_y1.responseText + ')');
		var eMsg=result.mustchangeCommRst;
		
		var mtrsize = Ext.DomQuery.select('tr',Ext.getDom('mInfo')).length;
		var mcheckArray=document.getElementsByName("mCheckBox");
		if(mcheckArray!=null || mcheckArray.length>0){
			var mkeyUsedIdkArray=new Array();
			for(i=0;i<mcheckArray.length;i++){
				if(mcheckArray[i].checked){
					var index=mcheckArray[i].value;
					var mkeyUsedId=Ext.get('mKeyUseId_'+index).dom.value;
					mkeyUsedIdkArray[i]=mkeyUsedId;
				}
			}		
		}
		
		for(j=0;j<<%=size%>;j++){
			var keyUseId=Ext.get('keyuseid_'+j).dom.value;
			var keyUseIdUsed=Ext.get('keyUseIdUsed_'+j).dom.value;
			var mustchange=Ext.get('mustchange_'+j).dom.value;
			var errorRst=Ext.get('errorRst_'+j).dom.value;
			var checkObj=document.getElementById("parts_"+j);
			var keyUsedIdIndexOf=mkeyUsedIdkArray.indexOf(keyUseId);
			var keyUseIdUsedIndexOf=mkeyUsedIdkArray.indexOf(keyUseIdUsed);
			var divobj=Ext.get('check_'+j).dom;
			if(mustchange=="Y" && errorRst=="Y"){
				if(parseInt(keyUsedIdIndexOf)<0 && parseInt(keyUseIdUsedIndexOf)<0){
					Ext.get('mustchangeRemark_'+j).dom.value="0";
					divobj.style.display="";
				}else{
					Ext.get('mustchangeRemark_'+j).dom.value="1";
					checkObj.checked=false;
					divobj.style.display="none";
				}
			}
		}		
		
		Ext.get('post-error-y1').radioClass('active-msg');
        Ext.get('post-error-msg-y1').update(eMsg);
		//alert(eMsg);
	}
	function submitFailure_y1(o_y1){
		var result = eval('(' + o_y.responseText + ')');
		var eMsg=result.mustchangeCommRst;
		Ext.get('post-error-y1').radioClass('active-msg');
        Ext.get('post-error-msg-y1').update(eMsg);	
		//alert(eMsg);
	}
	function checkForm_y1(){
		var mtrsize = Ext.DomQuery.select('tr',Ext.getDom('mInfo')).length;
		var mcheckArray=document.getElementsByName("mCheckBox");
		if(mcheckArray!=null || mcheckArray.length>0){
			for(i=0;i<mcheckArray.length;i++){
				if(mcheckArray[i].checked){
					var index=mcheckArray[i].value;
					var reason=Ext.get('mReason_'+index).dom.value;
					var remark=Ext.get('mRemark_'+index).dom.value;
					if(reason==""){
						return "未换原因不能为空";
					}
					if(remark==""){
						return "备注不能为空";
					}
				}
			}
		}
		return "";
	}

	function commentSuccess_y(o_y){
    	//
    	var ptrsize = Ext.DomQuery.select('tr',Ext.getDom('pInfo')).length;
    	var dtrsize = Ext.DomQuery.select('tr',Ext.getDom('dInfo')).length;
    	var dltrsize = Ext.DomQuery.select('tr',Ext.getDom('dLife')).length;
    	var hsize = Ext.DomQuery.select('tr',Ext.getDom('hInfo')).length;
    	if(ptrsize>1){	       		
		       	for(i=0;i<ptrsize-1;i++){
		       	//alert(i);
			       	var row = Ext.DomQuery.select('tr',Ext.getDom('pInfo'))[ptrsize-i-1];
			       	Ext.Element.get(row).remove();
		       	}
	    }
	    if(dtrsize>1){
		       	for(i=0;i<dtrsize-1;i++){
		       	//alert(i);
			       	var row = Ext.DomQuery.select('tr',Ext.getDom('dInfo'))[dtrsize-i-1];
			       	Ext.Element.get(row).remove();
		       	}
	    }
	    if(dltrsize>1){
		       	for(i=0;i<dltrsize-1;i++){
		       	//alert(i);
			       	var row = Ext.DomQuery.select('tr',Ext.getDom('dLife'))[dltrsize-i-1];
			       	Ext.Element.get(row).remove();
		       	}
	    }
	    if(hsize>0){
		       	for(i=0;i<hsize;i++){
			       	var row = Ext.DomQuery.select('tr',Ext.getDom('hInfo'))[hsize-i-1];
			       	Ext.Element.get(row).remove();
		       	}
	    }	    
 	    	    		
       	var result = eval('(' + o_y.responseText + ')'); 
    	//parts信息
       	var keyPartsJson=result[0];
       	var keyPartsArrary=keyPartsJson.keyPartsJson;
       	//alert(keyPartsArrary.length);
       	for(k=0;k<keyPartsArrary.length;k++){
       		var partsInfo=keyPartsArrary[k];
       		var orderNum=k+1;
       		var delaytime=partsInfo.delaytime;
       		if(delaytime==undefined){
       			delaytime=0;
       		}
       		Ext.get('hInfo').createChild({
       			tag:'tr', style:'background-color:#DFE1EC', children: [
       				{tag: 'td', html:'<input name="nameIndex_'+orderNum+'" type="hidden", style="width:30%", cls="input" value="'+partsInfo.nameIndex+'" />',cls:'en11pxb',align:'center'},
       				{tag: 'td', html:'<input name="keyPartsId_'+orderNum+'" type="hidden", style="width:30%", cls="input" value="'+partsInfo.keyPartsId+'" />',cls:'en11pxb',align:'center'},
					{tag: 'td', html:'<input name="eventIndex_'+orderNum+'" type="hidden", style="width:30%", cls="input" value="'+partsInfo.eventIndex+'" />',cls:'en11pxb',align:'center'},
					{tag: 'td', html:'<input name="keyUseId_'+orderNum+'" type="hidden", style="width:30%", cls="input" value="'+partsInfo.keyUseId+'" />',cls:'en11pxb',align:'center'},
					{tag: 'td', html:'<input name="keyUseIdUsed_'+orderNum+'" type="hidden", style="width:30%", cls="input" value="'+partsInfo.keyUseIdUsed+'" />',cls:'en11pxb',align:'center'},			
					{tag: 'td', html:'<input name="errorSpec_'+orderNum+'" type="hidden", style="width:30%", cls="input" value="'+partsInfo.errorSpec+'" />',cls:'en11pxb',align:'center'}
			]});
       		//alert(partsInfo.partsId);
       		Ext.get('pInfo').createChild({
				tag:'tr', style:'background-color:#DFE1EC', children: [
					{tag: 'td', html:'<input name="partsId_'+orderNum+'" type="hidden", style="width:30%", cls="input" value="'+partsInfo.partsId+'" />'+partsInfo.partsId,cls:'en11pxb',align:'center'},
					{tag: 'td', html:'<input name="partsName_'+orderNum+'" type="hidden", style="width:90%", cls="input" value="'+partsInfo.partsName+'" />'+partsInfo.partsName,cls:'en11pxb'},
					{tag: 'td', html:'<input name="keydesc_'+orderNum+'" type="hidden", style="width:90%", cls="input" value="'+partsInfo.keydesc+'" />'+partsInfo.keydesc,cls:'en11pxb'},
					{tag: 'td', html:'<input name="actul_'+orderNum+'" type="hidden", style="width:90%", cls="input" value="'+partsInfo.actul+'" />'+partsInfo.actul,cls:'en11pxb'},					
					{tag: 'td', html:'<input name="delaytime_'+orderNum+'" type="hidden", style="width:90%", cls="input" value="'+delaytime+'" />'+delaytime,cls:'en11pxb'}
			]});
       		var delayLife=partsInfo.delayLife;
			//alert(delayLife);
	        if(delayLife==undefined ){
	        	delayLife="";
	        }
	       var limitType=partsInfo.limitType;
			Ext.get('dLife').createChild({
				tag:'tr', style:'background-color:#DFE1EC', children: [
					{tag: 'td', html:'<input name="limitType_'+orderNum+'" type="hidden", style="width:10%", cls="input" value="'+partsInfo.limitType+'" />'+partsInfo.limitType,cls:'en11pxb'},
					{tag: 'td', html:'<input name="delayLife_'+orderNum+'" type="text", style="width:10%", cls="input" value="'+delayLife+'" />',cls:'en11pxb'}
			]});						
        }
       	//延期评估信息         	       	      	
		var keyPartsDelayJson=result[1];
       	var keyPartsDelayArrary=keyPartsDelayJson.keyPartsDelayJson; 
       	  
        for(i=0;i<keyPartsDelayArrary.length;i++){
	        var delayInfo=keyPartsDelayArrary[i];
	        var orderNum=i+1;
	        var delayResult=delayInfo.delayResult;
			//alert(delayInfo);
	        if(delayResult== undefined ){
	        	delayResult="";
	        }
			
			Ext.get('dInfo').createChild({
				tag:'tr', style:'background-color:#DFE1EC', children: [
					{tag: 'td', html:'<input name="delayItem_'+orderNum+'" type="hidden", style="width:30%", cls="input" value="'+delayInfo.delayItem+'" />'+delayInfo.delayItem,cls:'en11pxb',align:'center'},
					{tag: 'td', html:'<input name="delaySpec_'+orderNum+'" type="hidden", style="width:90%", cls="input" value="'+delayInfo.delaySpec+'" />'+delayInfo.delaySpec,cls:'en11pxb'},
					{tag: 'td', html:'<input name="delayRule_'+orderNum+'" type="hidden", style="width:90%", cls="input" value="'+delayInfo.delayRule+'" />'+delayInfo.delayRule,cls:'en11pxb'},
					{tag: 'td', html:'<input name="delayResult_'+orderNum+'" type="text", style="width:90%", cls="input" value="'+delayResult+'" />',cls:'en11pxb'}
			]});						
        }
        var dsize=keyPartsDelayArrary.length;
        var psize=keyPartsArrary.length;
		var url="<%=request.getContextPath()%>/control/pmkeyPartsDelayInfoSaveEntry";
        document.delayForm.action=url+"?dsize="+dsize+"&psize="+psize;
    }
	//文件管理页面
	// function manageFile(){
	// 	var url='<ofbiz:url>/fileUploadDefineEntry</ofbiz:url>?eventIndex=<%=pmIndex%>&eventType=PM&uploadItem=uploadIndex';
	// 	windowOpen(url,'文件上传下载',685,400);
	// }
</script>

<form action="<%=request.getContextPath()%>/control/pmFormManageTempSaveEntry" method="post" id="pmForm">
<input type="hidden" name="pmIndex" id="pmIndex" value='<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>'>
<input type="hidden" name="formType" id="formType" value='<ofbiz:inputvalue entityAttr="pmForm" field="formType"/>'>
<input type="hidden" name="eqpId" id="eqpId" value='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>'>
<input type="hidden" name="jobContent" id="jobContent" value='<ofbiz:inputvalue entityAttr="pmForm" field="jobText"/>'>
<input type="hidden" name="functionType" id="functionType" value="1"/>
<input type="hidden" name="periodIndex" id="periodIndex" value='<ofbiz:inputvalue entityAttr="pmForm" field="periodIndex"/>'>

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
	          <td class="en11px"><input type="text" class="input" ID="returnTime" NAME="returnTime" readonly size="26"></td>
	        </tr>

          <tr bgcolor="#DFE1EC">
            <td class="en11pxb" bgcolor="#ACD5C9">保养超时/时间过短原因</td>
            <td>
            <select id="overTimeIndex" name="overTimeIndex" class="select" onclick="return overTimeReason();">
            	<option value=""></option>

		        <ofbiz:if name="overTimeList">
			       <ofbiz:iterator name="overTime" property="overTimeList">
				   <option value='<ofbiz:inputvalue entityAttr="overTime" field="reasonIndex"/>'><ofbiz:inputvalue entityAttr="overTime" field="reason"/></option>
			     	</ofbiz:iterator>
		      	</ofbiz:if>

		      	<option value="0">other</option>
	      	</select>
	      	</td>
	      	<td class="en11pxb" bgcolor="#ACD5C9">超时备注</td>
	      	<td><div id="remark1" style="display:none"><input type="text" ID="comment" NAME="comment" value='<ofbiz:inputvalue entityAttr="pmForm" field="overtimeComment"/>'></div></td>
          </tr>

	      <tr bgcolor="#DFE1EC" height="25">
	          <td class="en11pxb" bgcolor="#ACD5C9">PM类别</td>
	          <td class="en11px"><input type="text" ID="periodName" NAME="periodName" readonly size="20" class="input" value='<ofbiz:inputvalue entityAttr="defaultPeriod" field="periodName"/>'></td>
	          <td bgcolor="#ACD5C9" class="en11pxb">表单状态</td>
	          <td class="en11px"><input type="text" ID="formStatus" NAME="formStatus" readonly size="20" class="input" value='<ofbiz:inputvalue entityAttr="defaultPeriod" field="eqpStatus"/>'></td>
	      </tr>

          <%if ("NORMAL".equals(formType)) {%>
          <tr bgcolor="#DFE1EC">
            <td bgcolor="#ACD5C9"><ul class="button">
                      <li><a class="button-text" href="#" onclick="queryEqpStatus();"><span>&nbsp;查询目前设备状态&nbsp;</span></a></li>
              </ul></td>
            <td class="en11px">&nbsp;<span id='curEqpStatus'></span></td>
            <td class="en11pxb" bgcolor="#ACD5C9">更改设备状态</td>
            <td>
                <select id="eqpState" name="eqpState" class="select">
              		<option value=''></option>
    	          	<%
    	          	if(eqpStatusList != null && eqpStatusList.size() > 0) {
           				for(Iterator it = eqpStatusList.iterator();it.hasNext();) {
    					Map map = (Map)it.next();
    				%>
    					<option value='<%=map.get("EQP_STATUS")%>'><%=map.get("EQP_DESC")%></option>
    				<%
    				    }
    				}
    				%>
    	      	</select>
	      	    <table border="0" cellspacing="0" cellpadding="0">
				  <tr height="30">
				    <td><ul class="button">
							<li><a class="button-text" href="#" onclick="updateEqpStatus();"><span>&nbsp;确定&nbsp;</span></a></li>
					</ul></td>
				  </tr>
				</table>
			</td>
		  </tr>
		  
          <%if (Constants.CALL_TP_FLAG) {%>
          <tr bgcolor="#DFE1EC" height='30'>
            <td class="en11px" bgcolor="#ACD5C9" colspan="4">
                <ul class="button">
                    <li><a class="button-text" href="#" onclick="pmQcTime('04-POST');"><span>&nbsp;制造作业PM后QC&nbsp;</span></a></li>
                </ul>
                <ul class="button">
                    <li><a class="button-text" href="#" onclick="pmQcTime('04-RWPS');"><span>&nbsp;制造作业PM返工后QC&nbsp;</span></a></li>
                </ul>
                <ul class="button">
                    <li><a class="button-text" href="#" onclick="pmQcTime('04-REW');"><span>&nbsp;PM返工&nbsp;</span></a></li>
                </ul>
            </td>
          </tr>
          <%}
          }
          %>
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
				<ofbiz:inputvalue entityAttr="parsUse" field="partNo"/></td>
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
		<div style="overflow-x:hidden;overflow-y:scroll;width:100%;">
			<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30">
				<tr bgcolor="#ACD5C9">
					<td width="2%">
						<img src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addParts(this);" />
					</td>
					<td width="8%" class="en11pxb">处理流程</td>
					<td width="5%" class="en11pxb">物料组</td>
					<td width="10%" class="en11pxb">料号</td>
					<td width="20%" class="en11pxb">料名</td>
					<td width="10%" class="en11pxb">批次</td>
					<td width="5%" class="en11pxb">物料类别</td>
					<td width="5%" class="en11pxb">参考数量</td>
					<td width="5%" class="en11pxb">库存余量</td>
					<td width="5%" class="en11pxb">使用数量</td>
					<td width="5%" class="en11pxb">是否关键备件</td>
					<td width="20%" class="en11pxb">备注</td>
				</tr>
			</table>
		</div>
		<div style="overflow-x:hidden;overflow-y:scroll;width:100%;height:240px;">
			<table width="100%" border="0" cellspacing="1" cellpadding="2" height="30"  style="word-wrap:break-word; word-break:break-all;">
			<%         
			if(partsUseList != null && partsUseList.size() > 0) {  
				int k = 0;
				for(Iterator it = partsUseList.iterator();it.hasNext();) { 
					Map map = (Map)it.next();
					String partNo = (String) map.get("PART_NO");
					String isKeyPart = (String)map.get("ISKEYPARTS");
					String partType = (String)map.get("PART_TYPE");
			%>
				<tr bgcolor="#DFE1EC"> 
					<td width="2%"><img  src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delPartsUse('<%=map.get("SEQ_INDEX")%>','<%=map.get("KEY_USE_ID")%>');"/></td>
					<td width="8%" class="en11px"><%=map.get("JOB_NAME")%></td>
					<td width="5%" class="en11px"><%=map.get("MTR_GRP")%></td>
					<td width="10%" class="en11px"><input id="partNo_<%=k%>" type="hidden" name="partNo_<%=k%>" value='<%=UtilFormatOut.checkNull((String) map.get("PART_NO"))%>'/><%=map.get("PART_NO")%></td>
					<td width="20%" class="en11px"><%=map.get("PART_NAME")%></td>
					<td width="10%" class="en11px">
						<input id="reqIndex_<%=k%>" type="hidden" name="reqIndex_<%=k%>" value='<%=map.get("MATERIAL_STO_REQ_INDEX")%>'/>
						<input id="batchNum_<%=k%>" type="hidden" name="batchNum_<%=k%>" value='<%=map.get("BATCH_NUM")%>'/>
						<input id="partType_<%=k%>" type="hidden" name="partType_<%=k%>" value='<%=map.get("PART_TYPE")%>'/>
						<% if (partType.equals("新品")) { %>
							<a href="#" onclick="selectBatch(this,'<%=map.get("PART_NO")%>', '<%=map.get("SEQ_INDEX")%>', '<%=map.get("EVENT_INDEX")%>')"><%=map.get("BATCH_NUM")%></a>
						<% } %>
					</td>
					<td width="5%" class="en11px"><%=map.get("PART_TYPE")%></td>
					<td width="5%" class="en11px"><%if(map.get("TEMPLATE_COUNT")!=null){%><%=map.get("TEMPLATE_COUNT")%><%}%></td>
					<td width="5%" class="en11px">
						<input id="qty_<%=k%>" type="hidden" name="qty_<%=k%>" value='<%=UtilFormatOut.checkNull((String) map.get("STOCK_QTY"))%>'/>
						<%=UtilFormatOut.checkNull((String) map.get("STOCK_QTY"))%>
					</td>
					<td width="5%" class="en11px">
						<input id="partCount_<%=k%>" type="hidden" name="partCount_<%=k%>" value='<%=UtilFormatOut.checkNull((String) map.get("PART_COUNT"))%>'/>
						<% if (isKeyPart.equals("Y")) { %><%=map.get("PART_COUNT")%>
						<% } else { %>
							<a href="#" onclick="editParts(this,'<%=map.get("SEQ_INDEX")%>','<%=map.get("KEY_USE_ID")%>','<%=map.get("TEMPLATE_COUNT")%>','<%=map.get("STOCK_QTY")%>','<%=map.get("PART_COUNT")%>','<%=map.get("PART_TYPE")%>')"><%=map.get("PART_COUNT")%></a>
						<% }%>
					</td>
					<td width="5%" class="en11px" align="center"><%=map.get("ISKEYPARTS")%></td> 
					<td width="20%" class="en11px"><input id="premark_<%=k%>" type="hidden" name="premark_<%=k%>" value='<%=UtilFormatOut.checkNull((String) map.get("REMARK"))%>'/><%=UtilFormatOut.checkNull((String) map.get("REMARK"))%></td>
				</tr>
			<%
					k++;
				}
			} 
			%>
			</table>
		</div>
		<p></p> <p></p> 
		<input type="hidden" name="pmIndex" id="pmIndex" value='<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>'> 
		<input type="hidden" name="functionType" id="functionType" value="1"/>
		<input type="hidden" name="eventIndex" id="eventIndex" value='<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>'>   
		<input type="hidden" name="eqpId" id="eqpId" value='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>'>
		<input type="hidden" name="flowIndex" id="flowIndex" value='<%=flowIndex%>'>
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
						if (errorRst.equals("Y")) {
				%> 
				   	<tr bgcolor="OrangeRed">
				<%}else if(warnRst.equals("Y")){%>   
				   	<tr bgcolor="PINK">
				<%}else if(map.get("lifeError")!=null){%>
					<tr bgcolor="yellow">
				<%}else{%>
					<tr bgcolor="#DFE1EC">
				<%}%>
						<td width="4%" class="en11px">
				<%
					String mustchangeRemark=map.get("mustchangeRemark").toString();
					if(mustchangeRemark.equals("1") && errorRst.equals("Y") && UtilFormatOut.checkNull((String)map.get("MUSTCHANGE")).equals("Y")){
				%>
							<div id="check_<%=k%>" style="display:'none'">
				<%
					}else{
				%>
							<div id="check_<%=k%>" style="display:''">
				<% } %>
								<input style="width:95%" type="checkBox" id="parts_<%=k%>" name="parts_<%=k%>"  value='<%=map.get("MTR_NUM")%>' <% if (  !map.get("KEY_USE_ID").toString().equals("0")) {%>checked="1" <% } %>    onclick="partsSelect(this,'<%=map.get("MTR_NUM")%>','<%=map.get("isUsed")%>');"/>
							</div>
							<input type="hidden" id="mustchangeRemark_<%=k%>" name="mustchangeRemark_<%=k%>" value='<%=map.get("mustchangeRemark")%>' />
							<input type="hidden" id="isdelayed_<%=k%>" name="isdelayed_<%=k%>" value='<%=map.get("ISDELAYED")%>' />
							<input type="hidden" id="createTime_<%=k%>" name="createTime_<%=k%>" value='<%=map.get("CREATE_TIME_NEW")%>' />
							<input type="hidden" id="eqpModel_<%=k%>" name="eqpModel_<%=k%>" value='<%=map.get("EQP_MODEL")%>' />
							<input type="hidden" id="errorSeriesNo_<%=k%>" name="errorSeriesNo_<%=k%>" value='<%=UtilFormatOut.checkNull((String)map.get("errorSeriesNo"))%>' />
							<input type="hidden" id="isClean_<%=k%>" name="isClean_<%=k%>" value='<%=UtilFormatOut.checkNull((String)map.get("ISCLEAN"))%>' />	
							<input type="hidden" id="keyPartsCleanId_<%=k%>" name="keyPartsCleanId_<%=k%>" value='<%=UtilFormatOut.checkNull((String)map.get("KEY_PARTS_CLEAN_ID"))%>' />
				<%  String isUsed=map.get("isUsed").toString();
					if(isUsed.equals("Y")||UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("DELAY")){  %>
					 <% if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("DELAY")){%> 
						 	<div id="delay_<%=k%>" style="display:''"><a href="#" style="font-size:8px;" onclick="keyPartsDelaySet(this,'<%=map.get("KEY_PARTS_ID") %>','<%=map.get("MTR_NUM") %>','<%=map.get("DELAYTIME")%>','<%=map.get("INIT_LIFE")%>','<%=map.get("KEY_USE_ID")%>','<%=map.get("KEY_USE_ID_USED")%>','<%=k%>','<%=map.get("ACTUL")%>')" >延期</a></div>
					 <%}else{%> 
						 	<div id="delay_<%=k%>" style="display:none" ><a href="#" style="font-size:8px;" onclick="keyPartsDelaySet(this,'<%=map.get("KEY_PARTS_ID") %>','<%=map.get("MTR_NUM") %>','<%=map.get("DELAYTIME")%>','<%=map.get("INIT_LIFE")%>','<%=map.get("KEY_USE_ID")%>','<%=map.get("KEY_USE_ID_USED")%>','<%=k%>','<%=map.get("ACTUL")%>')" >延期</a></div>
					 <%}%>
				<% } %>
							<input id="keypartsid_<%=k%>" type="hidden" name="keypartsid_<%=k%>" value='<%=map.get("KEY_PARTS_ID")%>'/>
							<input id="partsuseid_<%=k%>" type="hidden" name="partsuseid_<%=k%>" value='<%=map.get("PARTS_USE_ID")%>'/>
							<input id="mcsid_<%=k%>" type="hidden" name="mcsid_<%=k%>" value='<%=map.get("MATERIAL_STO_REQ_INDEX")%>'/>
							<input id="keyuseid_<%=k%>" type="hidden" name="keyuseid_<%=k%>" value='<%=map.get("KEY_USE_ID")%>'/>
							<input id="keyPartsMtrGrp_<%=k%>" type="hidden" name="keyPartsMtrGrp_<%=k%>" value='<%=map.get("MTR_GRP")%>' />
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
					 <td width="10%" class="en11px"><select style="width:95%" id="newparts_type<%=k%>" readonly="readonly" name="newparts_type<%=k%>"  onchange="delayCheck(this,this.options[this.options.selectedIndex].value,'delay_<%=k%>','initlife_<%=k%>','parts_<%=k%>','vendor_<%=k%>','seriesno_<%=k%>','baseno_<%=k%>','offline_<%=k%>','remark_<%=k%>')">
					<%
					if(UtilFormatOut.checkNull((String)map.get("ISCLEAN")).equals("Y")){
					%>
						<option value=""></option>
						<option <% if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("CLEAN")) { %>  selected <% }%>    value="CLEAN">CLEAN</option>
					<%
					}else{
					%>
						<option <% if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("NEW")) { %>  selected <% }%> value="NEW">NEW</option>
						<option <% if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("OVERHUAL")) { %>  selected <% }%>    value="OVERHUAL">OVERHUAL</option>
						<option <% if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("OLD")) { %>  selected <% }%>    value="OLD">OLD</option>
						<option <% if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("DEMO")) { %>  selected <% }%>    value="DEMO">技改/DEMO</option>
					<%
						if(UtilFormatOut.checkNull((String)map.get("MUSTCHANGE")).equals("Y")||isUsed.equals("N")){
							if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("DELAY")){
					%> 
						
						<option <% if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("DELAY")) { %>  selected <% }%>    value="DELAY">延期</option>  
					<%		}
						}else{
					%>
						<option <% if(UtilFormatOut.checkNull((String)map.get("PARTS_TYPE")).equals("DELAY")) { %>  selected <% }%>    value="DELAY">延期2</option>      		
					<% 	}
					}
					%>
						<td width="4%" class="en11px" align="center"><%=UtilFormatOut.checkNull((String)map.get("lifeType"))%></td>      		 
						<td width="4%" class="en11px"><% if(map.get("lifeError")!=null ){ %><%=UtilFormatOut.checkNull((String)map.get("lifeError"))%><% }else{ %><%=UtilFormatOut.checkNull((String)map.get("remainLife"))%><% } %> </td>           
						<td width="10%" class="en11px">          		
							<select id="vendor_<%=k%>" name="vendor_<%=k%>"  readonly="readonly" style="background:E9E9E9;width:95%">
								<option value=""></option>
								<%		List vendorList=(List)map.get("vendorList");
										if(vendorList!=null&&vendorList.size()>0){
											for(int v=0;v<vendorList.size();v++){
								%>		
										<option <% if( UtilFormatOut.checkNull((String)vendorList.get(v)).equals( UtilFormatOut.checkNull((String)map.get("VENDOR")) ) ){ %>  selected <% }%> value='<%=UtilFormatOut.checkNull((String)vendorList.get(v))%>'><%=UtilFormatOut.checkNull((String)vendorList.get(v))%></option>
								<%			}
										}
								%>    			
							</select>          		
						</td>

						<%
							if(UtilFormatOut.checkNull((String)map.get("ISCLEAN")).equals("Y")){
								List seriesNoList=(List)map.get("seriesNoList");
						%>
						<td width="8%" class="en11px">
							<select style="width:95%" id="seriesno_<%=k%>" name="seriesno_<%=k%>" readonly="readonly" style="background:E9E9E9;width:95%" >
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
		<tr>
			<td><input type="checkBox" id="checkbox" name="checkbox"> 使用配件 <input type="checkBox" id="checkbox2" name="checkbox2"> 未使用配件</td>
		</tr>
		</table>
	</div>

	<!-- <div id="checkList" class="tab-content">
		<table width="100%" border="0" cellspacing="1" cellpadding="2">
			<tr>
				<form action="<%=request.getContextPath()%>/control/fileUpDefine" method="post" id="fileUploadForm" ENCTYPE="multipart/form-data">
					<tr>
						<td>
							<div style="width:320px; height: 100px; background:#C7EDCC; border: 1px solid black;float:left">
								<div style="width: 100%;height: 100%;">
									<textarea id="TextArea1" cols="20" rows="2" style="width: 100%;height:100%;"></textarea>
								</div>
							</div>
						</td>
					</tr>
				</form>
			</tr>
			<tr>
				<td>
					<ul class="button">
						<li><a class="button-text" href="#" onclick="javascript:Capture('<%=pmIndex%>')"><span>&nbsp;拍照&nbsp;</span></a></li>
						<li><a class="button-text" href="javascript:manageFile()"><span>&nbsp;上传&nbsp;</span></a></li>
					</ul>
				</td>
			</tr>
    		<input id="uploadIndex" type="hidden" name="uploadIndex"/>
		</table>
	</div> -->
</div>
</fieldset>

<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" id="tempSave" href="#" onclick="tempSaveForm();" ><span>&nbsp;暂存&nbsp;</span></a></li>
	</ul></td>

    <td><ul class="button">
			<li><a class="button-text" id="overForm" href="#" onclick="chkNewPartsBatch();"><span>&nbsp;完成&nbsp;</span></a></li>
	</ul></td>

    <td><ul class="button">
			<li><a class="button-text" id="followJob" href="#" onclick="saveFollowJob();"><span>&nbsp;建立问题追踪&nbsp;</span></a></li>
	</ul></td>
	<td><ul class="button">
			<li><a class="button-text" id="mustChangeRemark" href="#" onclick="getMustChangeErrorRemark(this,'<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>','<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>');"><span>&nbsp;必换件超期使用&nbsp;</span></a></li> 
	</ul></td>

	<%
	List guiPriv = (List) request.getSession().getAttribute(SessionNames.GUI_PRIV_LIST_KEY);
	if (periodName.indexOf(Constants.CHANGE_TARGET_PERIOD_PREFIX)>-1 && guiPriv.contains("LEADER_PMFORM_EDIT")) {
	%>
	<td><ul class="button">
			<li><a class="button-text" href="javascript:useOldTarget()"><span>&nbsp;使用旧靶&nbsp;</span></a></li>
	</ul></td>
	<% } %>

    <td><font color="#ff0000">● 注意：请在完成表单前,确认使用了配件！</font></td>
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

<%
//fab1普通保养表单，按保养周期设定显示hold设置
String holdCode = defaultPeriod.getString("holdCode");
if (Constants.CALL_TP_FLAG && Constants.FORM_TYPE_NORMAL.equals(formType) && holdCode != null) {
    String holdReason = defaultPeriod.getString("holdReason");
    long holdLotNum = defaultPeriod.getLong("holdLotNum").longValue();
    String immediateHold = defaultPeriod.getString("immediateHold");
	String triggerStage = UtilFormatOut.checkNull(defaultPeriod.getString("triggerStage"));
	String holdDesc = UtilFormatOut.checkNull(defaultPeriod.getString("holdDesc"));
%>

<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>GUI检查步骤自动hold（保养周期设定）</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="20%" class="en11pxb">Hold码:Hold原因</td>
          <td width="10%" class="en11pxb">Hold批次数量</td>
          <td width="20%" class="en11pxb">Y:当站立即Hold，N:最近检查步骤Hold</td>
		  <td width="30%" class="en11pxb">触发Stage(例如AL1-PCK-1A，留空则不限制stage)</td>
		  <td class="en11pxb">备注</td>
        </tr>

        <tr bgcolor="#DFE1EC">
            <td class="en11px">
                <input type="hidden" id="holdEnabled" name="holdEnabled" value="ON" />
                <input type="hidden" id="basicId" name="basicId" value="<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>" />
                <input type="hidden" id="isCycle" name="isCycle" value="Y" />

                <input type="text" class="input" id="holdCodeReason" name="holdCodeReason" value="<%=holdCode%>:<%=holdReason%>" readonly >
            </td>

            <td class="en11px">
                <input type="text" class="input" id="holdLotNum" name="holdLotNum" value="<%=holdLotNum%>" readonly >
            </td>

            <td class="en11px">
                <input type="text" class="input" id="immediateHold" name="immediateHold" value="<%=immediateHold%>" readonly >
            </td>

            <td class="en11px">
                <input type="text" class="input" id="triggerStage" name="triggerStage" value="<%=triggerStage%>" readonly >
			</td>
			<td class="en11px">
                <input type="text" class="input" id="holdDesc" name="holdDesc" value="<%=holdDesc%>" >
            </td>
        </tr>

      </table>
      </fieldset></td>
  </tr>
</table>
<%}%>
</form>

<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">修改Parts</div>
    <div class="x-dlg-bd">
		<div id="post-tab" class="x-dlg-tab" title="修改Parts">
			<div class="inner-tab" id="x-form">
				<form action="<%=request.getContextPath()%>/control/pmFormPartsUpdateEntry" method="post" id="partsForm" onsubmit="return false;">
					<input id="activate" type="hidden" name="activate" value="parts" />
					<!-- <input id="partType" type="hidden" name="partType" value="" /> -->
					<input id="seqIndex" type="hidden" name="seqIndex" value="" />
					<input id="keyPartUseId" type="hidden" name="keyPartUseId" value="" />
					<input id="materialIndex" type="hidden" name="materialIndex" value="" />
					<input id="functionType" type="hidden" name="functionType" value='1'/>
					<input id="eqpId" type="hidden" name="eqpId" value='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>' />
					<input id="pmIndex" type="hidden" name="pmIndex" value='<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>' />
					<p>
						<label for="name"><small>物料号</small></label>
						<input class="textinput" type="text" name="partNo" id="partNo" value="" size="22" tabindex="1" style="background:E9E9E9" readonly="readonly"/>
					</p>
					<p>
						<label for="description"><small>物料名</small></label>
						<input class="textinput" type="text" name="partName" id="partName" value="" size="22" tabindex="2" style="background:E9E9E9" readonly="readonly"/>
					</p>
					<p>
						<label for="description"><small>物料类别</small></label>         
						<select id="partType" name="partType" >
							<ofbiz:if name="partTypeList">
								<ofbiz:iterator name="cust" property="partTypeList" type="java.util.Map">
								<option value='<ofbiz:entityfield attribute="cust" field="ID"/>'><ofbiz:entityfield attribute="cust" field="PART_TYPE"/></option>
								</ofbiz:iterator>
							</ofbiz:if>
						</select>   
					</p>
					<p>
						<label for="description"><small>库存余量</small></label>
						<input class="textinput" type="text" name="eqty" id="eqty" value="" size="22" tabindex="3" style="background:E9E9E9" readonly="readonly" />
					</p>
					<p>
						<label for="description"><small>参考数量</small></label>
						<input class="textinput" type="text" name="templateCount" id="templateCount" value="" size="22" tabindex="3" style="background:E9E9E9" readonly="readonly" />
					</p>
					<p>
						<label for="description"><small>使用数量</small></label>
						<input class="textinput" type="text" name="partCount" id="partCount" value="" size="22" tabindex="3" />
					</p>
					<p>
						<label for="description"><small>备注</small></label>
						<input class="textinput" type="text" name="remark" id="remark" maxlength="50" value="" size="22" tabindex="4" />
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

<div id="y-dlg" style="visibility:hidden;">
	<div class="x-dlg-hd">延期评估</div>
	<div class="x-dlg-bd">
		<div id="post-tab-y" class="x-dlg-tab" title="延期评估">
			<div class="inner-tab" id="y-form">
				<form action="<%=request.getContextPath()%>/control/pmkeyPartsDelayInfoSaveEntry" method="post" name="delayForm" id="delayForm" onsubmit="return false;">
					<input id="activate" type="hidden" name="activate" value="parts" />	                   
					<input id="seqIndex" type="hidden" name="seqIndex" value="" />
					<input id="materialIndex" type="hidden" name="materialIndex" value="" />
					<input id="functionType" type="hidden" name="functionType" value='1'/>
					<input id="eqpId" type="hidden" name="eqpId" value='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>' />
					<input id="pmIndex" type="hidden" name="pmIndex" value='<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>' />
					<table id="hInfo"></table>
					<table id="pInfo">
						<tr bgcolor="#ACD5C9">
							<td width="20%" class="en11pxb">物料号</td>
							<td width="20%" class="en11pxb">物料名</td>     
							<td width="20%" class="en11pxb">关键字</td>
							<td width="20%" class="en11pxb">实用寿命</td>
							<td width="5%" class="en11pxb">延期次数</td>
						</tr>
					</table>
					<table id="dInfo">
						<tr bgcolor="#ACD5C9">
							<td width="5%" align="center" class="en11pxb">评估ITEM</td>
							<td width="30%" class="en11pxb">评估项目</td>
							<td width="30%" class="en11pxb">规范</td>
							<td width="30%" class="en11pxb">Result</td>
						</tr>
					</table>
					<table id="dLife">
						<tr bgcolor="#ACD5C9">            
							<td width="10%" class="en11pxb">寿命选项</td>     
							<td width="20%" class="en11pxb">延期数值</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
	</div>
	<div class="x-dlg-ft">
		<div id="dlg-msg-y">
			<span id="post-error-y" class="posting-msg"><img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" />&nbsp;<span id="post-error-msg-y"></span></span>
		</div>
	</div>
</div>

<div id="y1-dlg" style="visibility:hidden;">
	<div class="x-dlg-hd">必换件未换说明</div>
	<div class="x-dlg-bd">
		<div id="post-tab-y1" class="x-dlg-tab" title="必换件未换说明">
			<div class="inner-tab" id="y1-form">
				<form action="" method="post" name="mustChangeForm" id="mustChangeForm" onsubmit="return false;">              			
					<input type="hidden" id="mFormType" name="mFormType" value="PM" />
					<input type="hidden" id="mEventIndex" name="mEventIndex" value='<ofbiz:inputvalue entityAttr="pmForm" field="pmIndex"/>' />
					<input type="hidden" id="mEqpId" name="mEqpId" value='<ofbiz:inputvalue entityAttr="pmForm" field="equipmentId"/>' />
					<table id="mInfo">
						<tr bgcolor="#ACD5C9">
							<td width="5%" class="en11pxb">选择</td>
							<td width="15%" class="en11pxb">关键字</td>
							<td width="20%" class="en11pxb">物料号</td>
							<td width="20%" class="en11pxb">物料名</td>     
							<td width="20%" class="en11pxb">未换原因</td>
							<td width="20%" class="en11pxb">备注</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
	</div>
	<div class="x-dlg-ft">
		<div id="dlg-msg-y1">
			<span id="post-error-y1" class="posting-msg"><img src="<%=request.getContextPath()%>/pms/images/warning.gif" width="16" height="16" />&nbsp;<span id="post-error-msg-y1"></span></span>
		</div>
	</div>
</div>
<!-- <div id='ActiveXDivOne'>
</div> -->

<script language="javascript">
		var followJob=Ext.get('followJob').dom;
		var tempSave=Ext.get('tempSave').dom;
		var overForm=Ext.get('overForm').dom;
		<%
			boolean flag = false;
			flag = "1".equalsIgnoreCase(pmForm.getString("status")) && StringUtils.isEmpty(pmForm.getString("status"));
			if (flag){
		%>
				followJob.disabled = true;
				tempSave.disabled = true;
				overForm.disabled = true;
		<%
			}
		%>
		Ext.get('returnTime').dom.value='<%=endTime%>';
</script>