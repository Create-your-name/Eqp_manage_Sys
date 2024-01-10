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

	//�ı䵯�����Сwidth,height
	extDlg.dlgInit('500','300');
	
	//�������ܵ���ҳ
	function addHighValueParts(obj){
		Ext.get('deptSel').dom.value='<%=dept%>';
    Ext.get('mode').dom.value="insert";
    Ext.get('partNo').dom.value="";
    Ext.get('partName').dom.value="";
    Ext.get('averagePrice').dom.value="";
    Ext.get('partNo').dom.readOnly=false;
		extDlg.showAddDialog(obj);
	}
	
	//�޸Ĺ��ܵ���ҳ
	function editHighValuePartDefine(obj,partNo){
    Ext.get('mode').dom.value="update";
		var url='<ofbiz:url>/editHighValuePartDefine</ofbiz:url>?partNo='+partNo;
		extDlg.showEditDialog(obj,url);
	}
	
	//�޸�ʱԶ�̵��óɹ����ҳ�渳ֵ
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
	
	//ɾ��
	function delHighValueParts(partNo){
    Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˼�¼��',function result(value){
      if(value=="yes"){
        var url='<ofbiz:url>/delHighValuePartDefine</ofbiz:url>?partNo='+partNo;
        document.location=url;
      }else{
        return;
      }
    });
	}
	
	//��ѯ
	function queryHighValueParts(){
		// if(Ext.get('eventCategory').dom.value==""){
		// 	Ext.MessageBox.alert('Status', '��ѡ���¼�����!');
		// 	return;
		// }
		highValuePartsForm.submit();
  }

  //���ݺϷ���У��
	function checkForm(){
		var partNo = Ext.get('partNo').dom.value;
		if(partNo==""){
			return "����д���Ϻ�";
		}
		var partName = Ext.get('partName').dom.value;
		if(partName==""){
			return "����д������";
		}
		var averagePrice = Ext.get('averagePrice').dom.value;
		if(averagePrice==""){
			return "����дƽ���۸�";
		}
    var reg = /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/;
    if(!reg.test(averagePrice)){
      return "��������ȷ�Ľ��!";
    }
		var dept = Ext.get('deptSel').dom.value;
		if(dept==""){
			return "��ѡ����";
    }
		return "";
	}
  
</script>

<form action="<%=request.getContextPath()%>/control/highValuePartsList" method="post" id="highValuePartsForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�߼����ϲ�ѯ����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC"> 
          <td width="12%" class="en11pxb">����:</td>
          <td width="28%">
          <select id="dept" name="dept">
						<option value="��Ĥ��">��Ĥ��</option>
            <option value="��ʴ��">��ʴ��</option>
						<option value="��ɢ��">��ɢ��</option>
            <option value="��̲�">��̲�</option>
					</select>
      </td>
      <td width="20%" class="en11pxb" align="left">
      	<table border="0" cellspacing="0" cellpadding="0">
		  <tr height="30">
		   	<td width="20">&nbsp;</td>
		    <td><ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:queryHighValueParts();"><span>&nbsp;ȷ��&nbsp;</span></a></li> 
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
      <legend>�߼������б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addHighValueParts(this);"/></td>
          <td width="15%" class="en11pxb">����</td>
          <td width="30%" class="en11pxb">���Ϻ�</td>
          <td width="30%" class="en11pxb">������</td>
          <td width="20%" class="en11pxb">ƽ�����ۣ�RMB��</td>
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
    <div class="x-dlg-hd">�����߼�����</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="�����߼�">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/manageHighValuePartDefine" method="post" id="highValuePartForm" name="highValuePartForm" onsubmit="return false;">
                <input id="mode" type="hidden" name="mode" value="" />
                <p>
                <label for="partNo"><small>���Ϻ�</small></label>
                <input class="textinput" type="text" name="partNo" id="partNo" value="" size="22" tabindex="1" />
                </p>
                <p><label for="partName"><small>������</small></label>
                <input class="textinput" type="text" name="partName" id="partName" value="" size="22" tabindex="2" />
                </p>
                <p><label for="averagePrice"><small>ƽ�����ۣ�RMB��</small></label>
                <input class="textinput" type="text" name="averagePrice" id="averagePrice" value="" size="22" tabindex="3" />
                </p>
                <p><label for=""><small>ѡ����</small></label>
                <select id="deptSel" name="deptSel">
                  <option value="��Ĥ��">��Ĥ��</option>
                  <option value="��ʴ��">��ʴ��</option>
                  <option value="��ɢ��">��ɢ��</option>
                  <option value="��̲�">��̲�</option>
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