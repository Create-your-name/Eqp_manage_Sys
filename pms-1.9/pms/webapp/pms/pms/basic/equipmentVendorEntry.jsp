<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	//数据合法性校验
	function checkForm(){
		var vendor = Ext.get('vendor').dom.value;
        var address = Ext.get('address').dom.value;
        var contactName = Ext.get('contactName').dom.value;
        var phoneNo = Ext.get('phoneNo').dom.value;
		if(vendor==""){
			return "厂商不能为空";
		}
		if(address==""){
			return "住址不能为空";
		}
		if(contactName==""){
			return "连络人不能为空";
		}
		if(contactName==""){
			return "电话不能为空";
		}
		return "";
	}
	
	//改变弹出框大小width,height
	extDlg.dlgInit('500','300');
	
	//新增功能弹出页
	function addEquipmentVendor(obj){
		Ext.get('seqIndex').dom.value="";
		Ext.get('vendor').dom.value="";
        Ext.get('address').dom.value="";
        Ext.get('contactName').dom.value="";
        Ext.get('phoneNo').dom.value="";
		extDlg.showAddDialog(obj);
	}
	
	//修改功能弹出页
	function editEquipmentVendor(obj,seqIndex){
		Ext.get('seqIndex').dom.value=seqIndex;
		var url='<ofbiz:url>/queryEquipmentVendorByIndex</ofbiz:url>?seqIndex='+seqIndex;
		extDlg.showEditDialog(obj,url);
	}
	
	//修改时远程调用成功后给页面赋值
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('vendor').dom.value=result.vendor;
			Ext.get('address').dom.value=result.address;
			Ext.get('contactName').dom.value=result.contactName;
			Ext.get('phoneNo').dom.value=result.phoneNo;
		}
	}
	
	//删除
	function delEquipmentVendorByIndex(seqIndex){
        Ext.MessageBox.confirm('删除确认', '您确信要删除此记录吗？',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/delEquipmentVendorByIndex</ofbiz:url>?seqIndex='+seqIndex;
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
      <legend>清洗备件厂商列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addEquipmentVendor(this);"/></td>
          <td width="20%" class="en11pxb">厂商</td>
          <td width="45%" class="en11pxb">住址</td>
          <td width="15%" class="en11pxb">连络人</td>
          <td width="15%" class="en11pxb">电话</td>
        </tr>
        <ofbiz:if name="equipmentVendorList">
	        <ofbiz:iterator name="cust" property="equipmentVendorList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delEquipmentVendorByIndex('<ofbiz:inputvalue entityAttr="cust" field="seqIndex"/>')"/></td>
		          <td class="en11px"><a href="#" onclick="editEquipmentVendor(this,'<ofbiz:inputvalue entityAttr="cust" field="seqIndex"/>')"><ofbiz:entityfield attribute="cust" field="vendor"/></a></td>
		          <td class="en11px"><ofbiz:entityfield attribute="cust" field="address"/></td>
		          <td class="en11px"><ofbiz:entityfield attribute="cust" field="contactName"/></td>
		          <td class="en11px"><ofbiz:entityfield attribute="cust" field="phoneNo"/></td>
		        </tr>
	      </ofbiz:iterator>
      </ofbiz:if>
      </table>
      </fieldset></td>
  </tr>
</table>
<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">清洗备件厂商</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="清洗备件厂商">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/equipmentVendor" method="post" id="pcStyleForm" onsubmit="return false;">
                	<input id="seqIndex" type="hidden" name="seqIndex" value="" />
                <p>
                <label for="name"><small>厂商</small></label>
                <input class="textinput" type="text" name="vendor" id="vendor" value="" size="22" tabindex="1" />
                </p>
                <p><label for="description"><small>住址</small></label>
                <input class="textinput" type="text" name="address" id="address" value="" size="22" tabindex="2" />
                </p>
                <p><label for="description"><small>连络人</small></label>
                <input class="textinput" type="text" name="contactName" id="contactName" value="" size="22" tabindex="2" />
                </p>
                <p><label for="description"><small>电话</small></label>
                <input class="textinput" type="text" name="phoneNo" id="phoneNo" value="" size="22" tabindex="2" />
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