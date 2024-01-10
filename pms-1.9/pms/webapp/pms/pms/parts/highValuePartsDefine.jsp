<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<% 
	String dept=(String)request.getAttribute("dept");
%>
<!-- yui page script-->
<script language="javascript">
  Ext.onReady(function(){
      Ext.getDom('dept').value = '<%=dept%>';
  });

	//改变弹出框大小width,height
	extDlg.dlgInit('500','300');
	
	//新增功能弹出页
	function addHighValueParts(obj){
		Ext.get('deptSel').dom.value='<%=dept%>';
    Ext.get('mode').dom.value="insert";
    Ext.get('partNo').dom.value="";
    Ext.get('partName').dom.value="";
    Ext.get('averagePrice').dom.value="";
    Ext.get('partNo').dom.readOnly=false;
		extDlg.showAddDialog(obj);
	}
	
	//修改功能弹出页
	function editHighValuePartDefine(obj,partNo){
    Ext.get('mode').dom.value="update";
		var url='<ofbiz:url>/editHighValuePartDefine</ofbiz:url>?partNo='+partNo;
		extDlg.showEditDialog(obj,url);
	}
	
	//修改时远程调用成功后给页面赋值
	function commentSuccess(o){
    var result = eval('(' + o.responseText + ')');
		 if (result!=null && result!=""){
      Ext.get('partNo').dom.value=result.partNo;
      Ext.get('partNo').dom.readOnly=true;
			Ext.get('partName').dom.value=result.partName;
			Ext.get('averagePrice').dom.value=result.averagePrice;
			Ext.get('deptSel').dom.value=result.dept;
		}
	}
	
	//删除
	function delHighValueParts(partNo){
    Ext.MessageBox.confirm('删除确认', '您确信要删除此记录吗？',function result(value){
      if(value=="yes"){
        var url='<ofbiz:url>/delHighValuePartDefine</ofbiz:url>?partNo='+partNo;
        document.location=url;
      }else{
        return;
      }
    });
	}
	
	//查询
	function queryHighValueParts(){
		// if(Ext.get('eventCategory').dom.value==""){
		// 	Ext.MessageBox.alert('Status', '请选择事件分类!');
		// 	return;
		// }
		highValuePartsForm.submit();
  }

  //数据合法性校验
	function checkForm(){
		var partNo = Ext.get('partNo').dom.value;
		if(partNo==""){
			return "请填写物料号";
		}
		var partName = Ext.get('partName').dom.value;
		if(partName==""){
			return "请填写物料名";
		}
		var averagePrice = Ext.get('averagePrice').dom.value;
		if(averagePrice==""){
			return "请填写平均价格";
		}
    var reg = /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/;
    if(!reg.test(averagePrice)){
      return "请输入正确的金额!";
    }
		var dept = Ext.get('deptSel').dom.value;
		if(dept==""){
			return "请选择部门";
    }
		return "";
	}
  
</script>

<form action="<%=request.getContextPath()%>/control/highValuePartsList" method="post" id="highValuePartsForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>高价物料查询参数</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC"> 
          <td width="12%" class="en11pxb">部门:</td>
          <td width="28%">
          <select id="dept" name="dept">
						<option value="薄膜部">薄膜部</option>
            <option value="腐蚀部">腐蚀部</option>
						<option value="扩散部">扩散部</option>
            <option value="光刻部">光刻部</option>
					</select>
      </td>
      <td width="20%" class="en11pxb" align="left">
      	<table border="0" cellspacing="0" cellpadding="0">
		  <tr height="30">
		   	<td width="20">&nbsp;</td>
		    <td><ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:queryHighValueParts();"><span>&nbsp;确定&nbsp;</span></a></li> 
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

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>高价物料列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addHighValueParts(this);"/></td>
          <td width="15%" class="en11pxb">部门</td>
          <td width="30%" class="en11pxb">物料号</td>
          <td width="30%" class="en11pxb">物料名</td>
          <td width="20%" class="en11pxb">平均单价（RMB）</td>
        </tr>
        <ofbiz:if name="highValuePartsList">
	        <ofbiz:iterator name="cust" property="highValuePartsList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delHighValueParts('<ofbiz:inputvalue entityAttr="cust" field="partNo"/>')"/></td>
		          <td width="15%" class="en11px"><ofbiz:entityfield attribute="cust" field="dept"/></td>
              <td width="30%" class="en11px"><a href="#" onclick="editHighValuePartDefine(this,'<ofbiz:inputvalue entityAttr="cust" field="partNo"/>')"><ofbiz:entityfield attribute="cust" field="partNo"/></a></td>
		          <td width="30%" class="en11px"><ofbiz:entityfield attribute="cust" field="partName"/></td>
              <td width="20%" class="en11px"><ofbiz:entityfield attribute="cust" field="averagePrice"/></td>
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
    <div class="x-dlg-hd">新增高价物料</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="新增高价">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/manageHighValuePartDefine" method="post" id="highValuePartForm" name="highValuePartForm" onsubmit="return false;">
                <input id="mode" type="hidden" name="mode" value="" />
                <p>
                <label for="partNo"><small>物料号</small></label>
                <input class="textinput" type="text" name="partNo" id="partNo" value="" size="22" tabindex="1" />
                </p>
                <p><label for="partName"><small>物料名</small></label>
                <input class="textinput" type="text" name="partName" id="partName" value="" size="22" tabindex="2" />
                </p>
                <p><label for="averagePrice"><small>平均单价（RMB）</small></label>
                <input class="textinput" type="text" name="averagePrice" id="averagePrice" value="" size="22" tabindex="3" />
                </p>
                <p><label for=""><small>选择部门</small></label>
                <select id="deptSel" name="deptSel">
                  <option value="薄膜部">薄膜部</option>
                  <option value="腐蚀部">腐蚀部</option>
                  <option value="扩散部">扩散部</option>
                  <option value="光刻部">光刻部</option>
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