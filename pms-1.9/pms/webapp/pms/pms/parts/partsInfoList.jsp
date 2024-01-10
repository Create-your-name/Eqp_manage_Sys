<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.*"%>


<%
	String mtrGrp=UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
	String category=UtilFormatOut.checkNull(request.getParameter("mtrGrp"));
	String partNo=UtilFormatOut.checkNull(request.getParameter("partNo"));
	String periodIndex=UtilFormatOut.checkNull(request.getParameter("periodIndex"));
	String flowIndex=UtilFormatOut.checkNull(request.getParameter("flowIndex"));
	String eqpType=UtilFormatOut.checkNull(request.getParameter("eqpType"));
	if(eqpType.equals("")){
	    eqpType = (String) request.getAttribute("eqpType");
	}
	int size=0;
	List list=(List)request.getAttribute("partsNoList");
	if(list!=null){
		size=list.size();
	}
%>

<base target="_self">
<!-- yui page script-->
<script language="javascript">
	//查询
	function partsQuery(){
	  	var mtrGrpValue=Ext.get('mtrGrp').dom.value;
	  	if(mtrGrpValue==''){
			Ext.MessageBox.alert('警告', "请选择物料组!");
   			return;
   		}
	  	var partNoValue=Ext.get('partNo').dom.value;
	  	if(partNoValue.length < 4){
			Ext.MessageBox.alert('警告', "至少输入4位料号长度查询!");
   			return;
   		}
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
		var checked=isChecked();
		if(checked){
			for(i=0;i<<%=size%>;i++){
		   	   var checkObj=document.getElementById("parts_"+i);
			   if (checkObj.checked==true){
			   		var inputObj=document.getElementById("partsNum_"+i);
			   		if(inputObj.value==""){
			   			alert("已选择的物料，必须输入默认数量!");
			   			return;
			   		}else if(isNaN(inputObj.value)){
			   			alert("默认数量必须为数字!");
			   			return;
			   		}
					var inputObj1=document.getElementById("templateCount_"+i);
			   		if(inputObj1.value==""){
			   			alert("已选择的物料，必须输入参考数量!");
			   			return;
			   		} else if(isNaN(inputObj.value)){
			   			alert("参考数量必须为数字!");
			   			return;
			   		} else {
			   		    //var reg = new RegExp("^([0-9]*)+(.[0-9]{1,2})?$");//验证数字保留两位小数
                        var reg = new RegExp("^([1-9]{1})+([0-9]*)?$");//验证正整数
                        if(!reg.test(inputObj.value)){
                            alert("物料数量必须为正整数!");
			   			    return;
                        }
			   		}

			   		var inputRemarkValue=document.getElementById("remark_"+i).value;
			   		if(inputRemarkValue.length>10){
			   			alert("备注长度不能超过10个字符!!");
			   			return;
			   		}
			   }
		 	 }
			loading();
			partsDataSaveForm.submit();
		}else{
			alert("没有选择物料，请确认!");
		}
	}

	//关闭查询页面，刷新母页面
	function closeWindow(){
	    window.close();
	    window.opener.document.location.href="<%=request.getContextPath()%>/control/queryPmPartsTemplateList?flow=<%=flowIndex%>&period=<%=periodIndex%>&equipmentType=<%=eqpType%>";
	}

	//选择物料
	function partsSelect(obj){
		var id=obj.id;
		var valueArray=id.split("_");
		if(obj.checked==true){
			document.getElementById("partsNum_"+valueArray[1]).readOnly=false;
			document.getElementById("partsNum_"+valueArray[1]).style.background="#FFFFFF";

		    document.getElementById("remark_"+valueArray[1]).readOnly=false;
			document.getElementById("remark_"+valueArray[1]).style.background="#FFFFFF";

			document.getElementById("templateCount_"+valueArray[1]).readOnly=false;
			document.getElementById("templateCount_"+valueArray[1]).style.background="#FFFFFF";
		}else{
			document.getElementById("partsNum_"+valueArray[1]).readOnly=true;
			document.getElementById("partsNum_"+valueArray[1]).style.background="#E9E9E9";

		    document.getElementById("remark_"+valueArray[1]).readOnly=true;
			document.getElementById("remark_"+valueArray[1]).style.background="#E9E9E9";

			document.getElementById("templateCount_"+valueArray[1]).readOnly=true;
			document.getElementById("templateCount_"+valueArray[1]).style.background="#E9E9E9";	
		}
	}

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

    /*Ext.onReady(function(){此方法在完成页面载入后才执行判断太慢了
		if("Y" == "<%=request.getAttribute("flag")%>") {
            window.close();
			window.opener.document.location.href="<%=request.getContextPath()%>/control/queryPmPartsTemplateList?flow=<%=flowIndex%>&period=<%=periodIndex%>&equipmentType=<%=eqpType%>";
		}
    });*/

    <%if ("Y".equals(request.getAttribute("flag"))) {%>
        closeWindow();//保存后关闭页面并刷新
    <%}%>
</script>

<form action="<%=request.getContextPath()%>/control/queryPartsNoTemplateList?flowIndex=<%=flowIndex%>&periodIndex=<%=periodIndex%>&eqpType=<%=eqpType%>" method="post" id="partsDataQueryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<input type="hidden" id="eqpType" name="eqpType" value="" />
<tr>
  <td><fieldset>
      <legend>物料信息查询参数</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
	     <tr bgcolor="#DFE1EC">
	       <td width="12%" class="en11pxb">选择物料:</td>
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
	       <td width="12%" class="en11pxb">物料号:</td>
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
					<li><a class="button-text" href="#" onclick="javascript:closeWindow();"><span>&nbsp;关闭&nbsp;</span></a></li>
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
<form action="<%=request.getContextPath()%>/control/partsSaveEntry?flowIndex=<%=flowIndex%>&periodIndex=<%=periodIndex%>&equipType=<%=eqpType%>&mtrGrp=<%=category%>" method="post" id="partsDataSaveForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>物料信息列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="10%" class="en11pxb">选择</td>
          <td width="20%" class="en11pxb">物料号</td>
          <td width="20%" class="en11pxb">物料名</td>
          <td width="10%" class="en11pxb">物料类别</td>
          <td width="10%" class="en11pxb">参考数量</td>
          <td width="10%" class="en11pxb">默认数量</td>
          <td width="20%" class="en11pxb">备注</td>
        </tr>
        <%
        List partsUseList=(List)request.getAttribute("partsNoList");
        if(partsUseList != null && partsUseList.size() > 0) {
            int k = 0;
            for(Iterator it = partsUseList.iterator();it.hasNext();) {
                Map map = (Map)it.next();
        %>
		<input id="partsName_<%=k%>" type="hidden" name="partsName_<%=k%>" value='<%=map.get("PART_NAME")%>' />
			<td class="en11px"><input type="checkBox" id="parts_<%=k%>" name="parts_<%=k%>" value='<%=map.get("PART_NO")%>' onclick="partsSelect(this);"></td>
			<td class="en11px"><%=map.get("PART_NO")%></td>
			<td class="en11px"><%=map.get("PART_NAME")%></td>
			<td class="en11px">
			<ofbiz:if name="partTypeList">
				<select name="partType_<%=k%>" id="partType_<%=k%>"/>
					<ofbiz:iterator name="cust" property="partTypeList" type="java.util.Map">
						<option value='<ofbiz:inputvalue entityAttr="cust" field="ID"/>'><ofbiz:inputvalue entityAttr="cust" field="PART_TYPE"/></option>
					</ofbiz:iterator>
				</select>
			</ofbiz:if>
			</td>
			<td class="en11px"><input id="templateCount_<%=k%>" name="templateCount_<%=k%>" class="input" type="text" readonly="readonly" style="background:E9E9E9"/></td>    
			<td class="en11px"><input id="partsNum_<%=k%>" name="partsNum_<%=k%>" class="input" type="text" readonly="readonly" style="background:E9E9E9"/></td>
			<td class="en11px"><input id="remark_<%=k%>" name="remark_<%=k%>" class="input" size="40" type="text" readonly="readonly" style="background:E9E9E9"/></td>
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
<script language="javascript">
	//查询后的页面的初始化
	Ext.get('mtrGrp').dom.value='<%=mtrGrp%>'
	Ext.get('partNo').dom.value='<%=partNo%>'
	Ext.get('eqpType').dom.value='<%=eqpType%>'
</script>