<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<!-- yui page script-->
<script language="javascript">
	//���ݺϷ���У��
	function checkForm(){
		var name = Ext.get('category').dom.value;
        var description = Ext.get('description').dom.value;
		if(name==""){
			return "�¼����಻��Ϊ��";
		}
		if(description==""){
			return "�¼�������������Ϊ��";
		}
		return "";
	}
	
	//�ı䵯�����Сwidth,height
	extDlg.dlgInit('500','300');
	
	//�������ܵ���ҳ
	function addEventCategory(obj){
		Ext.get('eventIndex').dom.value="";
		Ext.get('category').dom.value="";
        Ext.get('description').dom.value="";
		extDlg.showAddDialog(obj);
	}
	
	//�޸Ĺ��ܵ���ҳ
	function editEventCategory(obj,eventIndex){
		Ext.get('eventIndex').dom.value=eventIndex;
		var url='<ofbiz:url>/queryEventCategoryByIndex</ofbiz:url>?eventIndex='+eventIndex;
		extDlg.showEditDialog(obj,url);
	}
	
	//�޸�ʱԶ�̵��óɹ����ҳ�渳ֵ
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('category').dom.value=result.category;
			Ext.get('description').dom.value=result.desc;
		}
	}
	
	//ɾ��
	function delEventCategory(eventIndex){
        Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˼�¼��',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/delEventCategoryByIndex</ofbiz:url>?eventIndex='+eventIndex;
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
      <legend>�¼������б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addEventCategory(this);"/></td>
          <td width="33%" class="en11pxb">�¼�����</td>
          <td width="62%" class="en11pxb">�¼���������</td>
        </tr>
        <ofbiz:if name="eventCategoryList">
	        <ofbiz:iterator name="cust" property="eventCategoryList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delEventCategory('<ofbiz:inputvalue entityAttr="cust" field="eventIndex"/>')"/></td>
		          <td width="33%" class="en11px"><a href="#" onclick="editEventCategory(this,'<ofbiz:inputvalue entityAttr="cust" field="eventIndex"/>')"><ofbiz:entityfield attribute="cust" field="category"/></a></td>
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
    <div class="x-dlg-hd">�¼�����</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="�¼�����">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/eventCategoryDefine" method="post" id="pcStyleForm" onsubmit="return false;">
                	<input id="eventIndex" type="hidden" name="eventIndex" value="" />
                <p>
                <label for="name"><small>�¼�����</small></label>
                <input class="textinput" type="text" name="category" id="category" value="" size="22" tabindex="1" />
                </p>
                <p><label for="description"><small>�¼���������</small></label>
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