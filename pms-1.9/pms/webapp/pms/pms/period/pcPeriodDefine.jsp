<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%
	String styleIndex =(String)request.getAttribute("styleIndex");
%>
<!--include yui css-->
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	//数据合法性校验
	function checkForm(){
        var periodName = Ext.get('periodName').dom.value;
        var defaultDays = Ext.get('defaultDays').dom.value;
        var standardHour = Ext.get('standardHour').dom.value;

		if(periodName==""){
			return "保养名称不能为空";
		}

		if(defaultDays==""){
			return "基本天数不能为空";
		}

		if(standardHour==""){
			return "标准工时不能为空";
		}

		if(!IsNumeric(defaultDays)){
			return "基本天数必须为数字";
		}

		if(!IsNumeric(standardHour)){
			return "标准工时必须为数字";
		}

		return "";
	}

	//改变弹出框大小
	extDlg.dlgInit('500','500');

	//新增弹出页
	function addPcPeriod(obj){
		Ext.get('periodIndex').dom.value="";
		Ext.get('styleIndex').dom.value=Ext.get('style_Index').dom.value;
		Ext.get('periodName').dom.value="";
		Ext.get('periodDesc').dom.value="";
		Ext.get('defaultDays').dom.value="";
		Ext.get('standardHour').dom.value="";
		Ext.get('enabled').dom.value="1";

		extDlg.showAddDialog(obj);
	}

	//修改弹出页
	function editPcPeriod(obj,periodIndex){
		Ext.get('periodIndex').dom.value=periodIndex;
		var url='<ofbiz:url>/queryPcPeriodByIndex</ofbiz:url>?periodIndex='+periodIndex;
		extDlg.showEditDialog(obj,url);
	}
	//删除弹出页
	function delPcPeriod(obj,periodIndex){
        Ext.MessageBox.confirm('删除确认', '您确信要删除此记录吗？',function result(value){
	        if(value=="yes"){
				var styleIndex=Ext.get('styleIndex').dom.value;
				var url='<ofbiz:url>/delPcPeriod</ofbiz:url>?periodIndex='+periodIndex+"&styleIndex=<%=styleIndex%>";
				document.location=url;
			}else{
				return;
			}
        });

	}

	//修改时远程调用成功后给页面赋值
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('styleIndex').dom.value=result.styleIndex;
			Ext.get('periodName').dom.value=result.periodName;
			Ext.get('periodDesc').dom.value=result.periodDesc;
			Ext.get('defaultDays').dom.value=result.defaultDays;
			Ext.get('standardHour').dom.value=result.standardHour;
			Ext.get('enabled').dom.value=result.enabled;
		}
	}

	//查询
	function pcPeriodList() {
		if(Ext.getDom('style_Index').value=='') {
			Ext.MessageBox.alert('警告', '请选择巡检样式！');
			return;
		}
		loading();
		document.pcPeriodForm.submit();
	}

	Ext.onReady(function(){
	    var style_Index = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'style_Index',
	        width:220,
	        forceSelection:true
	    });
	});

</script>
<form name="pcPeriodForm" method="POST" action="<%=request.getContextPath()%>/control/pcPeriodList">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="100%"><fieldset><legend>查询巡检周期</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC" height="30">
          <td width="12%" class="en11pxb">&nbsp;巡检样式</td>
          <td>
         		<select id="style_Index" name="style_Index">
          			<option value=''></option>
	          		<ofbiz:if name="pcStyleList">
		        		<ofbiz:iterator name="pcStyle" property="pcStyleList">
			    			<option value='<ofbiz:inputvalue entityAttr="pcStyle" field="styleIndex"/>'><ofbiz:inputvalue entityAttr="pcStyle" field="name"/></option>
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
		<li><a class="button-text" href="#" onclick="javascript:pcPeriodList();"><span>&nbsp;确定&nbsp;</span></a></li>
		</ul>
	</td>
	</tr>
</table>
</form>

<ofbiz:if name="flag" value="OK">
<div id="pcPeriodList" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="pcPeriodList" style="visibility:'hidden';">
</ofbiz:unless>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>查询巡检类型</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addPcPeriod(this);"/></td>
          <td width="14%" class="en11pxb">巡检周期</td>
          <td width="14%" class="en11pxb">说明</td>
          <td width="14%" class="en11pxb">基本天数(天)</td>
          <td width="14%" class="en11pxb">标准工时(小时)</td>
          <td width="14%" class="en11pxb">是否启用</td>
        </tr>
        <ofbiz:if name="pcPeriodList">
	        <ofbiz:iterator name="pcPeriod" property="pcPeriodList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="center">
		            <!--
		            <img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delPcPeriod(this,'<ofbiz:inputvalue entityAttr="pcPeriod" field="periodIndex"/>');"/>
		            -->
		          </td>

		          <td width="14%" class="en11px"><a href="#" onclick="editPcPeriod(this,'<ofbiz:inputvalue entityAttr="pcPeriod" field="periodIndex"/>')"><ofbiz:entityfield attribute="pcPeriod" field="periodName"/></td>
		          <td width="14%" class="en11px"><ofbiz:entityfield attribute="pcPeriod" field="periodDesc"/></td>
		          <td width="14%" class="en11px"><ofbiz:entityfield attribute="pcPeriod" field="defaultDays"/></td>
		          <td width="14%" class="en11px"><ofbiz:entityfield attribute="pcPeriod" field="standardHour"/></td>
	       		  <td width="11%" class="en11px">
	       		  <%
					GenericValue period = (GenericValue)pageContext.findAttribute("pcPeriod");
	       		  	if ("1".equals(period.getString("enabled"))){
	       		  		out.println("Y");
	       		  	}else{
	       		  		out.println("N");
	       		  	}
	       		  %>
	       		  </td>
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
    <div class="x-dlg-hd">设定机台参数</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="设备保养周期">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/managePcPeriod" method="post" id="eq" onsubmit="return false;">
                <input id="periodIndex" type="hidden" name="periodIndex" value="" />
                <input id="styleIndex" type="hidden" name="styleIndex" value="" />
                <p>
                <label for="name"><small>巡检周期</small></label>
                <input class="textinput" type="text" name="periodName" id="periodName" value="" size="22" tabindex="1" />
                </p>
                <p>
                <label for="name"><small>说明</small></label>
                <input class="textinput" type="text" name="periodDesc" id="periodDesc" value="" size="22" tabindex="1" />
                </p>
                <p>
                <label for="name"><small>基本天数(天)</small></label>
                <input class="textinput" type="text" name="defaultDays" id="defaultDays" value="" size="22" tabindex="1" />
                </p>
                <p>
                <label for="name"><small>标准工时(小时)</small></label>
                <input class="textinput" type="text" name="standardHour" id="standardHour" value="" size="22" tabindex="1" />
                </p>
                <p>
                <label for="name"><small>是否启用</small></label>
                <select name = "enabled" id="enabled" class="select" >
          			<option value='1'>Y</option>
          			<option value='0'>N</option>
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
		var obj=document.getElementById('style_Index');
		obj.value='<%=styleIndex%>'
	</ofbiz:if>
</script>