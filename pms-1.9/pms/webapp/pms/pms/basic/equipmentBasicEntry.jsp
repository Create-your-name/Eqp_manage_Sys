<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	//���ݺϷ���У��
	function checkForm(){
		var name = Ext.get('name').dom.value;
        var description = Ext.get('description').dom.value;
		if(name==""){
			return "���Ʋ���Ϊ��";
		}
		if(description==""){
			return "��������Ϊ��";
		}
		return "";
	}
	
	//�ı䵯�����Сwidth,height
	extDlg.dlgInit('500','300');
	
	//�������ܵ���ҳ
	function addEquipmentBasic(obj){
		Ext.get('seqIndex').dom.value="";
		Ext.get('name').dom.value="";
        Ext.get('description').dom.value="";
		extDlg.showAddDialog(obj);
	}
	
	//�޸Ĺ��ܵ���ҳ
	function editEquipmentBasic(obj,seqIndex){
		Ext.get('seqIndex').dom.value=seqIndex;
		var url='<ofbiz:url>/queryEquipmentBasicByIndex</ofbiz:url>?seqIndex='+seqIndex;
		extDlg.showEditDialog(obj,url);
	}
	
	//�޸�ʱԶ�̵��óɹ����ҳ�渳ֵ
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('name').dom.value=result.name;
			Ext.get('description').dom.value=result.description;
		}
	}
	
	//ɾ��
	function delEquipmentBasicByIndex(seqIndex){
        Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˼�¼��',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/delEquipmentBasicByIndex</ofbiz:url>?seqIndex='+seqIndex;
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
      <legend>�豸�ͺ��б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addEquipmentBasic(this);"/></td>
          <td width="33%" class="en11pxb">����</td>
          <td width="62%" class="en11pxb">����</td>
        </tr>
        <ofbiz:if name="equipmentBasicList">
	        <ofbiz:iterator name="cust" property="equipmentBasicList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delEquipmentBasicByIndex('<ofbiz:inputvalue entityAttr="cust" field="seqIndex"/>')"/></td>
		          <td width="33%" class="en11px"><a href="#" onclick="editEquipmentBasic(this,'<ofbiz:inputvalue entityAttr="cust" field="seqIndex"/>')"><ofbiz:entityfield attribute="cust" field="name"/></a></td>
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
    <div class="x-dlg-hd">�豸�ͺ��б�</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="�豸�ͺ��б�">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/equipmentBasic" method="post" id="pcStyleForm" onsubmit="return false;">
                	<input id="seqIndex" type="hidden" name="seqIndex" value="" />
                <p>
                <label for="name"><small>����</small></label>
                <input class="textinput" type="text" name="name" id="name" value="" size="22" tabindex="1" />
                </p>
                <p><label for="description"><small>����</small></label>
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