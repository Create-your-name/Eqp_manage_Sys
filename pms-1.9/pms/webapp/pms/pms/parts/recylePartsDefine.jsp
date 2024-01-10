<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<% 
	String accountSection=(String)request.getAttribute("accountSection");
	String partStyle=(String)request.getAttribute("partStyle");
	String region=(String)request.getAttribute("region");
%>
<!-- yui page script-->
<script language="javascript">
	//数据合法性校验
	function checkForm(){
		var partNo = Ext.get('partNo').dom.value;
        var partName = Ext.get('partName').dom.value;
		if(partNo==""){
			return "物料号不能为空";
		}
		if(partName==""){
			return "物料名不能为空";
		}
		return "";
	}
	
	//改变弹出框大小width,height
	extDlg.dlgInit('500','300');
	
	//新增功能弹出页
	function addRecyleParts(obj){
		Ext.get('partStyle').dom.value='<%=partStyle%>';
		Ext.get('region').dom.value='<%=region%>'
		Ext.get('partNo').dom.value="";
        Ext.get('partName').dom.value="";
		extDlg.showAddDialog(obj);
	}
	
	//修改功能弹出页
	function editRecyleParts(obj,partsNo){
		Ext.get('partStyle').dom.value='<%=partStyle%>';
		Ext.get('region').dom.value='<%=region%>'
		var url='<ofbiz:url>/queryRecylePartsByPartNo</ofbiz:url>?functionType=1&partNo='+partsNo;
		extDlg.showEditDialog(obj,url);
	}
	
	//修改时远程调用成功后给页面赋值
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('partNo').dom.value=result.partNo;
			Ext.get('partName').dom.value=result.partName;
		}
	}
	
	//删除
	function delPartsByNo(partsNo){
        Ext.MessageBox.confirm('删除确认', '您确信要删除此记录吗？',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/delRecylePartsByPartsNo</ofbiz:url>?partNo='+partsNo;
				document.location=url;
			}else{
				return;
			}
        });
	}
	
	//查询
	function recylePartsQuery(){
		if(Ext.get('pStyle').dom.value==""){
			Ext.MessageBox.alert('Status', '请选择物料类型!');
			return;
		}
		if(Ext.get('pregion').dom.value==""){
			Ext.MessageBox.alert('Status', '请选择级别!');
			return;
		}
		recylePartsQueryForm.submit();
	}
	
	//获取partNo
	function getPartsNo(obj){
		var result=window.showModalDialog ("<%=request.getContextPath()%>/control/queryPartsNoList","","dialogWidth=650px;dialogHeight=650px;center=yes;resizable=yes;scroll=yes;status=yes;help=no");
		if (result!=undefined){
			Ext.get('partNo').dom.value=result["partNo"];
			Ext.get('partName').dom.value=result["partName"];
		}
	}
		
	Ext.onReady(function(){
	    var pStyle = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'pStyle',
	        width:170,
	        forceSelection:true
	    });
	    var pregion = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'pregion',
	        width:170,
	        forceSelection:true
	    });

	});
</script>
<form action="<%=request.getContextPath()%>/control/queryRecylePartsList" method="post" id="recylePartsQueryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>recyleParts选择</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2"> 
	     <tr bgcolor="#DFE1EC"> 
	       <td width="12%" class="en11pxb">物料类型:</td>
	       <td width="28%"><select id="pStyle" name="pStyle">
	        <option value="RECYCLE">RECYCLE</option>
	        <option value="OTHER">OTHER</option>
	 		</select>
	    </td>
	    <td width="12%" class="en11pxb">级别:</td>
	    <td width="28%"><select id="pregion" name="pregion">
	    	<option value="COMP">公司</option>
	    	<option value="<%=accountSection%>"><%=accountSection%></option>
	    	</select>
	    </td>
	    <td width="20%" class="en11pxb" align="left">
	    	<table border="0" cellspacing="0" cellpadding="0">
			  <tr height="30">
			   	<td width="20">&nbsp;</td>
			    <td><ul class="button">
				<li><a class="button-text" href="#" onclick="javascript:recylePartsQuery();"><span>&nbsp;确定&nbsp;</span></a></li> 
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
<ofbiz:if name="flag" value="ok">
<div id="reaList" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="reaList" style="visibility:'hidden';">
</ofbiz:unless>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>recyleParts列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addRecyleParts(this);"/></td>
          <td width="30%" class="en11pxb">物料号</td>
          <td width="55%" class="en11pxb">物料名</td>
        </tr>
        <ofbiz:if name="recyclePartsList">
	        <ofbiz:iterator name="cust" property="recyclePartsList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delPartsByNo('<ofbiz:inputvalue entityAttr="cust" field="partNo"/>')"/></td>
		          <td class="en11px"><a href="#" onclick="editRecyleParts(this,'<ofbiz:inputvalue entityAttr="cust" field="partNo"/>')"><ofbiz:entityfield attribute="cust" field="partNo"/></a></td>
		          <td class="en11px"><ofbiz:entityfield attribute="cust" field="partName"/></td>
		        </tr>
	      </ofbiz:iterator>
      </ofbiz:if>
      </table>
      </fieldset></td>
  </tr>
</table>
</div>

<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">recyleParts</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="recyleParts">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/recylePartsDefine?functionType=2" method="post" id="recylePartsForm" onsubmit="return false;">
                	<input id="partStyle" type="hidden" name="partStyle" value="" />
                	<input id="region" type="hidden" name="region" value="" />
                <p>
                <label for="name"><small>物料号</small><img src="../images/icon_search.gif" width="15" height="16" border="0" onclick="getPartsNo(this);" style="cursor:hand"></label>
                <input class="textinput" type="text" name="partNo" id="partNo" value="" size="22" tabindex="1" readonly="readonly"/>
                </p>
                <p><label for="description"><small>物料名</small></label>
                <input class="textinput" type="text" name="partName" id="partName" value="" size="22" tabindex="2" />
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
	<ofbiz:if name="flag" value="ok">
		var obj=document.getElementById('pStyle');
		obj.value='<%=partStyle%>'
		var obj=document.getElementById('pregion');
		obj.value='<%=region%>'
	</ofbiz:if>
</script>