<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%
	String equipmentId=(String)request.getAttribute("equipmentId");
%>
<!--include yui css-->
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
var equipment = function() {
	var equipmentDs,equipmentCom;
	return {
		init : function() {
			this.createDataStore();
		    this.createCombox();
		    this.initLoad();
		},

		//设置数据源
		createDataStore : function() {
			equipmentDs = new Ext.data.Store({
		        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/querySectionEquipmentList</ofbiz:url>'}),
			    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
		    });
		},

		createCombox : function() {
		    //设置equipment
		    equipmentCom = new Ext.form.ComboBox({
			    store: equipmentDs,
			    displayField:'equipmentId',
			    valueField:'equipmentId',
			    hiddenName:'equipment_Id',
			    typeAhead: true,
			    mode: 'local',
			    width: 170,
			    triggerAction: 'all'
		    });
		    equipmentCom.applyTo('equipmentSelect');
		},

		initLoad : function() {
			equipmentDs.load({callback:function(){ equipmentCom.setValue("<%=UtilFormatOut.checkNull(request.getParameter("equipment_Id"))%>"); }});
		},

		returnEqpValue : function() {
			return equipmentCom.getValue();
		}
	}
}();

Ext.EventManager.onDocumentReady(equipment.init,equipment,true);

	//数据合法性校验
	function checkForm(){

		if(Ext.get('paramName').dom.value==""){
			return "参数名称 不能为空";
		}

		if(Ext.get('maxValue').dom.value==""){
			return "警示最大值 不能为空";
		}

		if(Ext.get('minValue').dom.value==""){
			return "警示最小值 不能为空";
		}

		return "";
	}

	//改变弹出框大小
	extDlg.dlgInit('500','400');

	//新增弹出页
	function addEquipmentParam(obj){
		Ext.get('seqIndex').dom.value="";
		Ext.get('equipmentId').dom.value=Ext.get('equipment_Id').dom.value;
		Ext.get('paramName').dom.value="";
		Ext.get('eqpStatus').dom.value="";
		Ext.get('maxValue').dom.value="";
		Ext.get('minValue').dom.value="";
		Ext.get('stdFlag').dom.value="";
		Ext.get('sort').dom.value="";

		extDlg.showAddDialog(obj);
	}

	//修改弹出页
	function editEquipmentParam(obj,seqIndex){
		Ext.get('seqIndex').dom.value=seqIndex;
		var url='<ofbiz:url>/queryEquipmentParamByIndex</ofbiz:url>?seqIndex='+seqIndex;
		extDlg.showEditDialog(obj,url);
	}

	//删除弹出页
	function delEquipmentParam(obj,seqIndex){
        Ext.MessageBox.confirm('删除确认', '您确信要删除此记录吗？',function result(value){
	        if(value=="yes"){
				var equipmentId=Ext.get('equipmentId').dom.value;
				var url='<ofbiz:url>/delEquipmentParam</ofbiz:url>?seqIndex='+seqIndex+"&equipment_Id=<%=equipmentId%>";
				document.location=url;
			}else{
				return;
			}
        });

	}

	//修改时远程调用成功后给页面赋值
	function commentSuccess(o){
	//alert(o.responseText);
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('equipmentId').dom.value=result.equipmentId;
			Ext.get('paramName').dom.value=result.paramName;
			Ext.get('eqpStatus').dom.value=result.eqpStatus;
			Ext.get('maxValue').dom.value=result.maxValue;
			Ext.get('minValue').dom.value=result.minValue;
			Ext.get('stdFlag').dom.value=result.stdFlag;
			Ext.get('sort').dom.value=result.sort;
		}
	}

	//查询
	function equipmentParamList() {
		if(equipment.returnEqpValue() == "") {
			Ext.MessageBox.alert('警告', '请选择设备!');
			return;
		}
		loading();
		document.eqpParamForm.submit();
	}

	function queryDcop() {
		var url = '<ofbiz:url>/equiList</ofbiz:url>?actionName=' + Ext.getDom('equipment_Id').value;
		window.open(url,"equiList",
			"top=130,left=240,width=685,height=180,title=,channelmode=0," +
			"directories=0,fullscreen=0,location=0,menubar=0,resizable=1," +
			"scrollbars=1,status=1,titlebar=0,toolbar=no");
	}
</script>

<form name="eqpParamForm" method="POST" action="<%=request.getContextPath()%>/control/equipmentParamList">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="98%"><fieldset><legend>查询机台参数信息</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
          <td width="15%" class="en11pxb">&nbsp;设备</td>
          <td width="25%"><input type="text" size="40" name="equipmentSelect" autocomplete="off"/></td>

          <td width="60%" class="en11pxb" align="left">
      		<table border="0" cellspacing="0" cellpadding="0">
		  	<tr height="30">
		   		<td width="20">&nbsp;</td>
		   	 	<td><ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:equipmentParamList();"><span>&nbsp;确定&nbsp;</span></a></li>
					</ul>
				</td>
		  	</tr>
			</table>
      	  </td>
        </tr>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>
</form>

<br>
<ofbiz:if name="flag" value="OK">
<div id="equipmentParamList" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="equipmentParamList" style="visibility:'hidden';">
</ofbiz:unless>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>设定机台参数</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="4%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addEquipmentParam(this);"/></td>
          <td width="16%" class="en11pxb">参数名称</td>
          <td width="16%" class="en11pxb">MES设备状态码</td>
          <td width="16%" class="en11pxb">警示最大值</td>
          <td width="16%" class="en11pxb">警示最小值</td>
          <td width="16%" class="en11pxb">手动(M)/自动(A)</td>
          <td width="16%" class="en11pxb">顺序</td>
        </tr>

        <ofbiz:if name="unscheduleEqpParamList">
	        <ofbiz:iterator name="unscheduleEqpParam" property="unscheduleEqpParamList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="4%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delEquipmentParam(this,'<ofbiz:inputvalue entityAttr="unscheduleEqpParam" field="seqIndex"/>');"/></td>

		          <td width="16%" class="en11px">
		            <a href="#" onclick="editEquipmentParam(this,'<ofbiz:inputvalue entityAttr="unscheduleEqpParam" field="seqIndex"/>')">
		                <ofbiz:entityfield attribute="unscheduleEqpParam" field="paramName"/>
		            </a>
		          </td>

		          <td width="16%" class="en11px"><ofbiz:entityfield attribute="unscheduleEqpParam" field="eqpStatus"/></td>
		          <td width="16%" class="en11px"><ofbiz:entityfield attribute="unscheduleEqpParam" field="maxValue"/></td>
		          <td width="16%" class="en11px"><ofbiz:entityfield attribute="unscheduleEqpParam" field="minValue"/></td>
		          <td width="16%" class="en11px"><ofbiz:entityfield attribute="unscheduleEqpParam" field="stdFlag"/></td>
		          <td width="16%" class="en11px">
		          	<%
		          		GenericValue eqpParam = (GenericValue)pageContext.findAttribute("unscheduleEqpParam");
						if ("0".equals(eqpParam.getString("sort"))){
							out.println("顺序");
		          		}else{
		          			out.println("倒序");
		          		}
		          	%>
		          	<!--<ofbiz:entityfield attribute="unscheduleEqpParam" field="sort"/>-->
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
        <div id="post-tab" class="x-dlg-tab" title="设定机台参数">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/manageEquipmentParam" method="post" id="eq" onsubmit="return false;">
                <input id="seqIndex" type="hidden" name="seqIndex" value="" />
                <input id="equipmentId" type="hidden" name="equipmentId" value="" />
                <p>
                <label for="name"><small>参数名称</small></label>
                <input class="textinput" type="text" name="paramName" id="paramName" value="" size="22" tabindex="1" />
                </p>
                <p>
                <label for="name"><small>MES设备状态码</small></label>
                <!-- <input class="textinput" type="text" name="eqpStatus" id="eqpStatus" value="" size="22" tabindex="1" /> -->
                <select name="eqpStatus" id="eqpStatus" class="select">
					<ofbiz:if name="promisStatusList">
		        		<ofbiz:iterator name="promisStatus" property="promisStatusList">
			    			<option value='<ofbiz:inputvalue entityAttr="promisStatus" field="eqpStatus"/>'><ofbiz:inputvalue entityAttr="promisStatus" field="eqpStatus"/></option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
				</select>
                </p>
                <p>
                <label for="name"><small>警示最大值</small></label>
                <input class="textinput" type="text" name="maxValue" id="maxValue" value="" size="22" tabindex="1" />
                </p>
                <p>
                <label for="name"><small>警示最小值</small></label>
                <input class="textinput" type="text" name="minValue" id="minValue" value="" size="22" tabindex="1" />
                </p>
                <p>
                <label for="name"><small>手动(M)/自动(A)</small></label>
                <select name="stdFlag" id="stdFlag" class="select">
					<option value="A">自动</option>
					<option value="M">手动</option>
				</select>
                <!--<input class="textinput" type="text" name="stdFlag" id="stdFlag" value="" size="22" tabindex="1" />-->
                </p>
                <p>
                <label for="name"><small>顺序</small></label>
				<select name="sort" id="sort" class="select">
					<option value="0">顺序</option>
					<option value="1">倒序</option>
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