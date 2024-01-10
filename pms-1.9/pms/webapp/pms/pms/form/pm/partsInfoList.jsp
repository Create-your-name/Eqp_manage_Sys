<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map" %>


<%
    String partNo=UtilFormatOut.checkNull(request.getParameter("partNo"));
    String eqpId=UtilFormatOut.checkNull(request.getParameter("eqpId"));
    String periodIndex=UtilFormatOut.checkNull(request.getParameter("periodIndex"));
    String formType=request.getParameter("formType");
    String eventIndex=request.getParameter("eventIndex");
    String ifkey=request.getParameter("ifkey");
    String formreturn=request.getParameter("formreturn");

    List flowList=(List)request.getAttribute("flowList");
    int size=0;
    List list=(List)request.getAttribute("partsNoList");
    if(list!=null){
        size=list.size();
    }
%>

<base target="_self">
<!-- yui page script-->
<script language="javascript">
    /* var pregion;
    var partType; */
    //查询
    function partsQuery(){
        <ofbiz:if name="formType" value="PM">
            var eqpId=Ext.get('eqpId').dom.value;
            var periodIndex=Ext.get('periodIndex').dom.value;
            if(eqpId==''){
                Ext.MessageBox.alert('警告', "设备ID为空,请联系管理员!");
                return;
            }else if(periodIndex==''){
                Ext.MessageBox.alert('警告', "PM周期为空,请联系管理员!");
                return;
            }
        </ofbiz:if>

        /*var mtrGrpValue=Ext.get('mtrGrp').dom.value;
        if(mtrGrpValue==''){
            Ext.MessageBox.alert('警告', "请选择物料组!");
            return;
        }*/

        var mtrGrpValue=Ext.get('mtrGrp').dom.value;
        if(mtrGrpValue==''){
            Ext.MessageBox.alert('警告', "请选择物料组!");
            return;
        }
		var url=document.getElementById("partsDataQueryForm").action;
        document.getElementById("partsDataQueryForm").action=url+"?formreturn=<%=formreturn%>";

        loading();
        partsDataQueryForm.submit();
    }

    //判断
    function isChecked(){
     for(i=0;i<<%=size%>;i++){
       var checkObj=document.getElementById("parts_"+i);
           if (checkObj.checked==true){
            return true;
           }
      }
      return false;
    }

    //物料保存
    function partSave(){
        // alert(<%=size%>);
        // var checked=isChecked();

        // if(checked){
        //     for(i=0;i<<%=size%>;i++){
        //        var checkObj=document.getElementById("parts_"+i);
        //        if (checkObj.checked==true){
        //            alert(2);
        //             var inputObj=document.getElementById("partsNum_"+i);
        //             if(inputObj.value==""){
        //                 alert("已选择的物料，必须输入数量!");
        //                 return;
        //             }else if(isNaN(inputObj.value)){
        //                 alert("物料数量必须为数字!");
        //                 return;
        //             }
        //             var inputRemarkValue=document.getElementById("remark_"+i).value;
        //             if(inputRemarkValue.length>10){
        //                 alert("备注长度不能超过10个字符!!");
        //                 return;
        //             }
        //        }
        //      }
        //     //Ext.get('type').dom.value=Ext.get('mtrGrp').dom.value;
        //     loading();
        //     partsDataSaveForm.submit();
        // }else{
        //     alert("没有选择物料，请确认!");
        //     return;
        // }
        var checked=isChecked();
        debugger;
        if(checked){
            for(i=0;i<<%=size%>;i++){
               var checkObj=document.getElementById("parts_"+i);
               if (checkObj.checked==true ){
                    var inputObj=document.getElementById("useNum_"+i);
                    var qty=document.getElementById("qty_"+i).value;
                    var reg = /^[+]{0,1}(\d+)$|^[+]{0,1}(\d+\.\d+)$/;//Js验证非负数(0,正整数和正小数) 
                    if(inputObj.value==""){
                        alert("已选择的物料，必须输入数量!");
                        return;
                    }else if(isNaN(inputObj.value)){
                    	alert("物料数量必须为数字!");
                        return;
                    }else if(reg.test(inputObj.value)==false){
                        alert("物料数量不能为负值!");
                        return;
                    }

                    if(parseInt(inputObj.value)>parseInt(qty)){
                    	alert("使用数量不能大于库存余量!");
                        return;
                    }
                    var inputRemarkValue=document.getElementById("remark_"+i).value;
                    if(inputRemarkValue.length>50){
                        alert("备注长度不能超过50个字符!!");
                        return;
                    }
               }
             }
            Ext.get('type').dom.value=Ext.get('mtrGrp').dom.value;
            var url=document.getElementById("keyPartsDataSaveForm").action;
        	document.getElementById("keyPartsDataSaveForm").action=url+"?formreturn=<%=formreturn%>";
            loading();
            keyPartsDataSaveForm.submit();
            
        }else{
            alert("没有选择物料，请确认!");
            return;
        }
    }

    //选择物料
    function partsSelect(obj){
        debugger;
        var id=obj.id;
        var valueArray=id.split("_");
        if(obj.checked==true){
            document.getElementById("useNum_"+valueArray[1]).readOnly=false;  
            document.getElementById("useNum_"+valueArray[1]).style.background="#FFFFFF";
            document.getElementById("newparts_type"+valueArray[1]).readOnly=false;
            document.getElementById("newparts_type"+valueArray[1]).style.background="#FFFFFF";  
            document.getElementById("vendor_"+valueArray[1]).readOnly=false;
            document.getElementById("vendor_"+valueArray[1]).style.background="#FFFFFF";
            document.getElementById("seriesno_"+valueArray[1]).readOnly=false;
            document.getElementById("seriesno_"+valueArray[1]).style.background="#FFFFFF";          
            document.getElementById("baseno_"+valueArray[1]).readOnly=false;
            document.getElementById("baseno_"+valueArray[1]).style.background="#FFFFFF";
            document.getElementById("remark_"+valueArray[1]).readOnly=false;
            document.getElementById("remark_"+valueArray[1]).style.background="#FFFFFF";    
        }else{ 
            document.getElementById("useNum_"+valueArray[1]).readOnly=true;
            document.getElementById("useNum_"+valueArray[1]).style.background="#E9E9E9";  
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
        }
    }

    function partsSelect2(obj){
    	debugger;
        var id=obj.id;
        var valueArray=id.split("_");
        if(obj.checked==true){
            document.getElementById("useNum_"+valueArray[1]).readOnly=false;  
            document.getElementById("useNum_"+valueArray[1]).style.background="#FFFFFF";
            document.getElementById("remark_"+valueArray[1]).readOnly=false;
            document.getElementById("remark_"+valueArray[1]).style.background="#FFFFFF";   
            document.getElementById("partType_"+valueArray[1]).readOnly=false;
            document.getElementById("partType_"+valueArray[1]).style.background="#FFFFFF"; 
        }else{
            document.getElementById("useNum_"+valueArray[1]).readOnly=true;
            document.getElementById("useNum_"+valueArray[1]).style.background="#E9E9E9";   
            document.getElementById("remark_"+valueArray[1]).readOnly=true;
            document.getElementById("remark_"+valueArray[1]).style.background="#E9E9E9"; 
            document.getElementById("partType_"+valueArray[1]).readOnly=true;
            document.getElementById("partType_"+valueArray[1]).style.background="#E9E9E9";   
        }
    }
    
    // function checkIskey(){
    //     if(document.getElementById("ifkey").checked==true){
    //         document.getElementById("notKeyform").style.display="none";
    //             document.getElementById("isKeyform").style.display="block";
                
    //     }else{
    //         document.getElementById("notKeyform").style.display="block";
    //             document.getElementById("isKeyform").style.display="none";
            
    //     }
        
        
    // }

    //关闭查询页面，刷新母页面
	function closeWindow(){
	    window.close();
	    window.opener.document.location.href="<%=request.getContextPath()%>/control/pmRecordInfo?functionType=1&activate=parts&pmIndex=<%=eventIndex%>&eqpId=<%=eqpId%>";
	}

    <%if ("Y".equals(request.getAttribute("flag"))) {%>
        closeWindow();//保存后关闭页面并刷新
    <%}%>

    //物料类别选择
    function partTypeChange(){
        var partsTypeValue=partType.getValue();
        if(partsTypeValue=='OTHER'||partsTypeValue=='RECYCLE'){
            //显示选择级别下拉
            Ext.get('level').dom.style.display='';
        }else{
            //隐藏选择级别下拉
            Ext.get('level').dom.style.display='none';
        }
    } 

	Ext.onReady(function(){
        if("Y" == "<%=request.getAttribute("flag")%>") {
        	if("new"=="<%=formreturn%>"){  		
            	window.opener.document.location.href="<%=request.getContextPath()%>/control/pmRecordInfo?functionType=1&activate=parts&pmIndex=<%=eventIndex%>&eqpId=<%=eqpId%>";
        	}else if("edit"=="<%=formreturn%>"){
           		window.opener.document.location.href="<%=request.getContextPath()%>/control/queryPmformByLeader?functionType=1&activate=parts&pmIndex=<%=eventIndex%>&eqpId=<%=eqpId%>";        	
        	}
            window.close();
        }        
    });
</script>

<form action="<%=request.getContextPath()%>/control/queryPartsNoPMList" method="post" id="partsDataQueryForm">
<input id="eqpId" type="hidden" name="eqpId" value="<%=eqpId%>" />
<input id="formType" type="hidden" name="formType" value="<%=formType%>" />
<input id="periodIndex" type="hidden" name="periodIndex" value="<%=periodIndex%>" />
<input id="eventIndex" type="hidden" name="eventIndex" value="<%=eventIndex%>" />

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>物料信息查询参数 [<%=eqpId%>]</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
         <!-- <tr bgcolor="#DFE1EC">
           <td width="6%" class="en11pxb">处理流程:</td>
           <td width="28%">
               <select id="jobIndex" name="jobIndex">
                   <ofbiz:if name="flowList">
                       <ofbiz:iterator name="cust" property="flowList">
                           <option value='<ofbiz:inputvalue entityAttr="cust" field="jobIndex"/>'>
                               <ofbiz:inputvalue entityAttr="cust" field="jobName"/>
                           </option>
                       </ofbiz:iterator>
                   </ofbiz:if>
               </select>
           </td>
         </tr> -->

         <tr bgcolor="#DFE1EC">
           <td width="6%" class="en11pxb">选择物料:</td>
           <td width="28%">
                <select id="mtrGrp" name="mtrGrp">
                    <option value=""></option>
                    <option value="20002P">20002P(备件)</option>
                    <option value="20002S">20002S(易耗备件)</option>
                    <option value="20002T">20002T(无料号备件)</option>
                </select>
            </td>
         </tr>

         <tr bgcolor="#DFE1EC">
           <td width="6%" class="en11pxb">物料号:</td>
           <td width="28%"><input class="input" type="text" name="partNo" id="partNo" value="<%=partNo%>" size="22" tabindex="1" />
           </td>
         </tr>

          <tr bgcolor="#DFE1EC">
            <td width="20%" class="en11pxb" align="left" colspan="2">
                <table border="0" cellspacing="0" cellpadding="0">
                  <tr height="30">
                    <td width="20">&nbsp;</td>
                    <td><ul class="button">
                    <li><a class="button-text" href="#" onclick="javascript:partsQuery();"><span>&nbsp;查询&nbsp;</span></a></li>
                    </ul>
                    <ul class="button">
                    <li><a class="button-text" href="#" onclick="javascript:partSave();"><span>&nbsp;保存&nbsp;</span></a></li>
                    </ul>
                    <ul class="button">
                    <li><a class="button-text" href="#" onclick="javascript:window.close();"><span>&nbsp;关闭&nbsp;</span></a></li>
                    </ul>
                    </td>
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
<% if(ifkey!=null) { %>
<form action="<%=request.getContextPath()%>/control/pmFormPartsSaveEntry" method="post" id="partsDataSaveForm">
<input id="equipmentId" type="hidden" name="eqpId" value="<%=eqpId%>" />
<input id="formType" type="hidden" name="formType" value="<%=formType%>" />
<input id="periodIndex" type="hidden" name="periodIndex" value="<%=periodIndex%>" />
<input id="formIndex" type="hidden" name="eventIndex" value="<%=eventIndex%>" />
<input id="jobIndex" type="hidden" name="jobIndex" value="<%=request.getParameter("jobIndex")%>" />

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
        <legend>物料信息列表1</legend>
        <table width="100%" border="0" cellspacing="1" cellpadding="2">
            <tr bgcolor="#ACD5C9"> 
              <td class="en11pxb">选择</td>
              <td class="en11pxb">物料号</td>
              <td class="en11pxb">物料名</td>
              <td class="en11pxb">批次</td>
              <td class="en11pxb">物料类别</td>
              <td class="en11pxb">处理流程</td>
              <td class="en11pxb">使用数量</td>
              <td class="en11pxb">剩余量</td>
              <td class="en11pxb">单价</td>  
              <td class="en11pxb">NEW/OVERHUA</td>  
              <td class="en11pxb">vendor</td>         
              <td class="en11pxb">SeriesNo.</td> 
              <td class="en11pxb">baseS/N</td> 
              <td class="en11pxb">领料人</td> 
              <td class="en11pxb">领料时间</td> 
              <td class="en11pxb">备注</td>    
            </tr>
          <% 
            List partsUseList=(List)request.getAttribute("partsNoList");
            if(partsUseList != null && partsUseList.size() > 0) {  
                int k = 0;
                for(Iterator it = partsUseList.iterator();it.hasNext();) { 
                    Map map = (Map)it.next();
                   
            %>
            <tr bgcolor="#DFE1EC"> 
                <td class="en11px">
                <input type="checkBox" id="parts_<%=k%>" name="parts_<%=k%>" value='<%=map.get("MTR_NUM")%>' onclick="partsSelect(this);"/>
                <input id="keypartsid_<%=k%>" type="hidden" name="keypartsid_<%=k%>" value='<%=map.get("KEY_PARTS_ID")%>'/>
                <input id="partsMtrGrp_<%=k%>" type="hidden" name="partsMtrGrp_<%=k%>" value='<%=map.get("MTR_GRP")%>' />
                </td>
                <td class="en11px"><%=map.get("MTR_NUM")%></td>
                <td class="en11px">
                <input id="partsName_<%=k%>" type="hidden" name="partsName_<%=k%>" value='<%=map.get("MTR_DESC")%>' />
                <%=map.get("MTR_DESC")%>
                </td>
                <td class="en11px">
                <input id="batchnum_<%=k%>" type="hidden" name="batchnum_<%=k%>" value='<%=map.get("BATCH_NUM")%>' />
                <%=map.get("BATCH_NUM")%></td>
                <td class="en11px">
                <input id="partType_<%=k%>" type="hidden" name="partType_<%=k%>" value='<%=map.get("TYPE")%>' />
                <%=map.get("TYPE")%>
                </td>
                <td class="en11px">
                    <select id="flow_<%=k%>" name="flow_<%=k%>">
                        <ofbiz:if name="flowList">
                            <ofbiz:iterator name="cust" property="flowList">
                                <option value='<ofbiz:inputvalue entityAttr="cust" field="jobIndex"/>'>
                                    <ofbiz:inputvalue entityAttr="cust" field="jobName"/>
                                </option>
                            </ofbiz:iterator>
                        </ofbiz:if>
                    </select>
                </td>
                <td class="en11px"><input id="useNum_<%=k%>" name="useNum_<%=k%>" value="1" class="input" size=2 type="text" maxlength=2 readonly="readonly" style="background:E9E9E9"/></td>
                <td class="en11px"><%=map.get("QTY")%></td>
                <td class="en11px"><%=map.get("VERPR")%></td>
                <td class="en11px"><select id="newparts_type<%=k%>" name="newparts_type<%=k%>">
                    <option value="NEW">NEW</option>
                    <option value="OVERHUAL">OVERHUAL</option>
                    </select></td> 
                <td class="en11px"><input id="vendor_<%=k%>" name="vendor_<%=k%>" class="input" size=10 maxlength="20" type="text" readonly="readonly" style="background:E9E9E9"/></td>
                <td class="en11px"><input id="seriesno_<%=k%>" name="seriesno_<%=k%>" class="input" size=10 maxlength="20" type="text" readonly="readonly" style="background:E9E9E9"/></td>
                <td class="en11px"><input id="baseno_<%=k%>" name="baseno_<%=k%>" class="input" size=10 maxlength="20" type="text" readonly="readonly" style="background:E9E9E9"/></td>             
                <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("RECIPIENT"))%></td>
                <td class="en11px"><%=map.get("DOC_TIME")%></td>
                <td class="en11px"><input id="remark_<%=k%>" name="remark_<%=k%>" class="input" size=20 maxlength="40" type="text" readonly="readonly" style="background:E9E9E9"/></td>
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
</form>
<% }
else {
%>
<div>
<form action="<%=request.getContextPath()%>/control/pmFormPartsSaveEntry" method="post" id="keyPartsDataSaveForm">
<input id="equipmentId" type="hidden" name="eqpId" value="<%=eqpId%>" />
<input id="form" type="hidden" name="formType" value="<%=formType%>" />
<input id="type" type="hidden" name="type" value="<%=UtilFormatOut.checkNull(request.getParameter("mtrGrp"))%>" />
<input id="prodIndex" type="hidden" name="periodIndex" value="<%=periodIndex%>" />
<input id="formIndex" type="hidden" name="eventIndex" value="<%=eventIndex%>" />
<input id="jobIndex" type="hidden" name="jobIndex" value="<%=request.getParameter("jobIndex")%>" />

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>

  <td><fieldset>
      <legend>物料信息列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td    class="en11pxb">选择</td>
          <td  class="en11pxb">PLANT</td>
          <td    class="en11pxb">物料号</td>
          <td    class="en11pxb">物料名</td>
          <td    class="en11pxb">批次</td>
          <td   class="en11pxb">物料类别</td>
          <td   class="en11pxb">处理流程</td>
          <td   class="en11pxb">使用数量</td>
          <td   class="en11pxb">剩余量</td>
          <td   class="en11pxb">单价</td>
          <td   class="en11pxb">领用人</td>
          <td   class="en11pxb">领用时间</td>
          <td   class="en11pxb">备注</td>
        </tr>
      <% 
        List partsUseList=(List)request.getAttribute("partsNoList");
        if(partsUseList != null && partsUseList.size() > 0) {  
            int k = 0;
            for(Iterator it = partsUseList.iterator();it.hasNext();) { 
                Map map = (Map)it.next();
        %>
           <tr bgcolor="#DFE1EC"> 
             <td class="en11px">
             <input type="checkBox" id="parts_<%=k%>" name="parts_<%=k%>" value='<%=map.get("MTR_NUM")%>' onclick="partsSelect2(this);"/>
             <input id="mcsid_<%=k%>" type="hidden" name="mcsid_<%=k%>" value='<%=map.get("REQ_INDEX")%>' />
             </td>
             <td class="en11px"><%=map.get("PLANT")%></td>
             <td class="en11px"><%=map.get("MTR_NUM")%></td>           
             <td class="en11px">
                <input id="partsName_<%=k%>" type="hidden" name="partsName_<%=k%>" value='<%=map.get("MTR_DESC")%>' />
                <%=map.get("MTR_DESC")%>
             </td>
              <td class="en11px">
             <input id="batchnum_<%=k%>" type="hidden" name="batchnum_<%=k%>" value='<%=map.get("BATCH_NUM")%>' />
             <%=UtilFormatOut.checkNull((String)map.get("BATCH_NUM"))%></td>
             <td class="en11px">
                <select name="partType_<%=k%>" id="partType_<%=k%>"/>
					<ofbiz:iterator name="cust" property="partTypeList" type="java.util.Map">
						<option value='<ofbiz:inputvalue entityAttr="cust" field="ID"/>'><ofbiz:inputvalue entityAttr="cust" field="PART_TYPE"/></option>
					</ofbiz:iterator>
				</select>
             </td>
              <td class="en11px">
                   <select id="flow_<%=k%>" name="flow_<%=k%>">
                       <ofbiz:if name="flowList">
                           <ofbiz:iterator name="cust" property="flowList">
                               <option value='<ofbiz:inputvalue entityAttr="cust" field="jobIndex"/>'>
                                   <ofbiz:inputvalue entityAttr="cust" field="jobName"/>
                               </option>
                           </ofbiz:iterator>
                       </ofbiz:if>
                   </select>
              </td>
             <td class="en11px"><input id="useNum_<%=k%>" name="useNum_<%=k%>" value="1" class="input" size=2 type="text" maxlength=2 readonly="readonly" style="background:E9E9E9"/></td>
             <td class="en11px"><input id="qty_<%=k%>" type="hidden" name="qty_<%=k%>" value='<%=map.get("QTY")%>' /><%=map.get("QTY")%></td>
             <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("VERPR"))%></td>
             <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("RECIPIENT"))%></td>
             <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("DOC_TIME"))%></td>
             <td class="en11px"><input id="remark_<%=k%>" name="remark_<%=k%>" class="input" size=20 maxlength="40" type="text" readonly="readonly" style="background:E9E9E9"/></td>
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
</form>
</div>
<% } %>  
<script language="javascript">
    //查询后的页面的初始化
    Ext.get('partNo').dom.value='<%=UtilFormatOut.checkNull(request.getParameter("partNo"))%>'
    Ext.get('mtrGrp').dom.value='<%=UtilFormatOut.checkNull(request.getParameter("mtrGrp"))%>'

    // if('<%=ifkey%>'=='是'){
    //     document.getElementsByName("ifkey")[0].checked=true;
            
    // }else{
    // document.getElementsByName("ifkey")[0].checked=false;
        
    // }
</script>