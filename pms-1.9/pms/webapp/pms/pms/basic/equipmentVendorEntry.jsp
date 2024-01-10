<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	//���ݺϷ���У��
	function checkForm(){
		var vendor = Ext.get('vendor').dom.value;
        var address = Ext.get('address').dom.value;
        var contactName = Ext.get('contactName').dom.value;
        var phoneNo = Ext.get('phoneNo').dom.value;
		if(vendor==""){
			return "���̲���Ϊ��";
		}
		if(address==""){
			return "סַ����Ϊ��";
		}
		if(contactName==""){
			return "�����˲���Ϊ��";
		}
		if(contactName==""){
			return "�绰����Ϊ��";
		}
		return "";
	}
	
	//�ı䵯�����Сwidth,height
	extDlg.dlgInit('500','300');
	
	//�������ܵ���ҳ
	function addEquipmentVendor(obj){
		Ext.get('seqIndex').dom.value="";
		Ext.get('vendor').dom.value="";
        Ext.get('address').dom.value="";
        Ext.get('contactName').dom.value="";
        Ext.get('phoneNo').dom.value="";
		extDlg.showAddDialog(obj);
	}
	
	//�޸Ĺ��ܵ���ҳ
	function editEquipmentVendor(obj,seqIndex){
		Ext.get('seqIndex').dom.value=seqIndex;
		var url='<ofbiz:url>/queryEquipmentVendorByIndex</ofbiz:url>?seqIndex='+seqIndex;
		extDlg.showEditDialog(obj,url);
	}
	
	//�޸�ʱԶ�̵��óɹ����ҳ�渳ֵ
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('vendor').dom.value=result.vendor;
			Ext.get('address').dom.value=result.address;
			Ext.get('contactName').dom.value=result.contactName;
			Ext.get('phoneNo').dom.value=result.phoneNo;
		}
	}
	
	//ɾ��
	function delEquipmentVendorByIndex(seqIndex){
        Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˼�¼��',function result(value){
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
      <legend>��ϴ���������б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addEquipmentVendor(this);"/></td>
          <td width="20%" class="en11pxb">����</td>
          <td width="45%" class="en11pxb">סַ</td>
          <td width="15%" class="en11pxb">������</td>
          <td width="15%" class="en11pxb">�绰</td>
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
    <div class="x-dlg-hd">��ϴ��������</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="��ϴ��������">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/equipmentVendor" method="post" id="pcStyleForm" onsubmit="return false;">
                	<input id="seqIndex" type="hidden" name="seqIndex" value="" />
                <p>
                <label for="name"><small>����</small></label>
                <input class="textinput" type="text" name="vendor" id="vendor" value="" size="22" tabindex="1" />
                </p>
                <p><label for="description"><small>סַ</small></label>
                <input class="textinput" type="text" name="address" id="address" value="" size="22" tabindex="2" />
                </p>
                <p><label for="description"><small>������</small></label>
                <input class="textinput" type="text" name="contactName" id="contactName" value="" size="22" tabindex="2" />
                </p>
                <p><label for="description"><small>�绰</small></label>
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