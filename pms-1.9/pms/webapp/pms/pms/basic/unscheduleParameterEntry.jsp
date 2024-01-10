<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	//数据合法性校验
	function checkForm(){
		var name = Ext.get('paramName').dom.value;
        var description = Ext.get('description').dom.value;
		if(name==""){
			return "参数名不能为空";
		}
		if(description==""){
			return "描述不能为空";
		}
		return "";
	}
	
	//改变弹出框大小width,height
	extDlg.dlgInit('500','300');
	
	//新增功能弹出页
	function addUnscheduleParameter(obj){
		Ext.get('paramIndex').dom.value="";
		Ext.get('paramName').dom.value="";
        Ext.get('description').dom.value="";
		extDlg.showAddDialog(obj);
	}
	
	//修改功能弹出页
	function editUnscheduleParameter(obj,paramIndex){
		Ext.get('paramIndex').dom.value=paramIndex;
		var url='<ofbiz:url>/queryUnscheduleParameterByIndex</ofbiz:url>?paramIndex='+paramIndex;
		extDlg.showEditDialog(obj,url);
	}
	
	//修改时远程调用成功后给页面赋值
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('paramName').dom.value=result.paramName;
			Ext.get('description').dom.value=result.description;
		}
	}
	
	//删除
	function delUnscheduleParameterByIndex(paramIndex){
        Ext.MessageBox.confirm('删除确认', '您确信要删除此记录吗？',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/delUnscheduleParameterByIndex</ofbiz:url>?paramIndex='+paramIndex;
				document.location=url;
			}else{
				return;
			}
        });
	}
</script>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>不定期保养参数列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addUnscheduleParameter(this);"/></td>
          <td width="33%" class="en11pxb">参数名</td>
          <td width="62%" class="en11pxb">描述</td>
        </tr>
        <ofbiz:if name="unscheduleParameterList">
	        <ofbiz:iterator name="cust" property="unscheduleParameterList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delUnscheduleParameterByIndex('<ofbiz:inputvalue entityAttr="cust" field="paramIndex"/>')"/></td>
		          <td width="33%" class="en11px"><a href="#" onclick="editUnscheduleParameter(this,'<ofbiz:inputvalue entityAttr="cust" field="paramIndex"/>')"><ofbiz:entityfield attribute="cust" field="paramName"/></a></td>
		          <td width="62%" class="en11px"><ofbiz:entityfield attribute="cust" field="description"/></td>
		        </tr>
	      </ofbiz:iterator>
      </ofbiz:if>
      </table>
      </fieldset></td>
  </tr>
</table>
<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">不定期保养参数</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="不定期保养参数">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/unscheduleParameter" method="post" id="pcStyleForm" onsubmit="return false;">
                	<input id="paramIndex" type="hidden" name="paramIndex" value="" />
                <p>
                <label for="name"><small>参数名</small></label>
                <input class="textinput" type="text" name="paramName" id="paramName" value="" size="22" tabindex="1" />
                </p>
                <p><label for="description"><small>描述</small></label>
                <input class="textinput" type="text" name="description" id="description" value="" size="22" tabindex="2" />
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