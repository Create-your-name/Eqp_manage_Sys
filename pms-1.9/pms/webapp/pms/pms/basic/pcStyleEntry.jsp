<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	//数据合法性校验
	function checkForm(){
		var name = Ext.get('name').dom.value;
        var description = Ext.get('description').dom.value;
		if(name==""){
			return "巡检名不能为空";
		}
		else
		{
			var subName = name.substring(0,2);
			alert(subName);
			if (subName=="光刻" || subName=="腐蚀" || subName=="薄膜" || subName=="扩散" || subName=="质量" || subName=="工程" || subName=="其它")
			{}
			else
			{
				return "巡检名必须以部门的前2字开头，如“光刻……”，“质量……”";
			}
		}
		if(description==""){
			return "描述不能为空";
		}
		
		var checkColumnLengthMsg = checkColumnLength('name||50||巡检名;description||100||描述');
		if(checkColumnLengthMsg.length > 0 ) {
			return checkColumnLengthMsg;
		}
		
		return "";
	}
	
	//改变弹出框大小width,height
	extDlg.dlgInit('500','300');
	
	//新增功能弹出页
	function addPcStyle(obj){
		Ext.get('styleIndex').dom.value="";
		Ext.get('name').dom.value="";
        Ext.get('description').dom.value="";
		extDlg.showAddDialog(obj);
	}
	
	//修改功能弹出页
	function editPcStyle(obj,pcStyleIndex){
		Ext.get('styleIndex').dom.value=pcStyleIndex;
		var url='<ofbiz:url>/queryPcStyleByIndex</ofbiz:url>?styleIndex='+pcStyleIndex;
		extDlg.showEditDialog(obj,url);
	}
	
	//修改时远程调用成功后给页面赋值
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('name').dom.value=result.name;
			Ext.get('description').dom.value=result.desc;
		}
	}
	
	//删除
	function delStyleIndex(styleIndex){
        Ext.MessageBox.confirm('删除确认', '您确信要删除此记录吗？',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/delPcStyleByIndex</ofbiz:url>?styleIndex='+styleIndex;
				document.location=url;
			}else{
				return;
			}
        });
	}
</script>
<%
	response.getWriter().write("<font color=\"red\">" + "巡检名必须以部门的前2字开头，如“光刻……”，“质量……”，以便将未完成巡检归属到所在部门，未明确说明的归属到设施部。" + "</font>");
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>巡检样式列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addPcStyle(this);"/></td>
          <td width="33%" class="en11pxb">巡检名</td>
          <td width="62%" class="en11pxb">描述</td>
        </tr>
        <ofbiz:if name="pcStyleList">
	        <ofbiz:iterator name="cust" property="pcStyleList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delStyleIndex('<ofbiz:inputvalue entityAttr="cust" field="styleIndex"/>')"/></td>
		          <td width="33%" class="en11px"><a href="#" onclick="editPcStyle(this,'<ofbiz:inputvalue entityAttr="cust" field="styleIndex"/>')"><ofbiz:entityfield attribute="cust" field="name"/></a></td>
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
    <div class="x-dlg-hd">巡检样式</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="巡检样式">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/pcStyleDefine" method="post" id="pcStyleForm" onsubmit="return false;">
                	<input id="styleIndex" type="hidden" name="styleIndex" value="" />
                <p>
                <label for="name"><small>巡检名</small></label>
                <input class="textinput" type="text" name="name" id="name" value="" size="22" tabindex="1" />
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