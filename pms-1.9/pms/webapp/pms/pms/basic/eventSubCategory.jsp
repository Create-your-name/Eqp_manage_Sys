<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<% 
	String eventIndex=(String)request.getAttribute("eventIndex");
%>
<!-- yui page script-->
<script language="javascript">
	//���ݺϷ���У��
	function checkForm(){
		var name = Ext.get('subCategory').dom.value;
        var description = Ext.get('description').dom.value;
		if(name==""){
			return "�¼�ϸ���Ϊ��";
		}
		if(description==""){
			return "�¼�ϸ����������Ϊ��";
		}
		return "";
	}
	
	//�ı䵯�����Сwidth,height
	extDlg.dlgInit('500','300');
	
	//�������ܵ���ҳ
	function addEventSub(obj){
		Ext.get('eventSubIndex').dom.value="";
		Ext.get('eventIndex').dom.value='<%=eventIndex%>';
		Ext.get('subCategory').dom.value="";
        Ext.get('description').dom.value="";
		extDlg.showAddDialog(obj);
	}
	
	//�޸Ĺ��ܵ���ҳ
	function editEventSub(obj,eventSubIndex){
		Ext.get('eventSubIndex').dom.value=eventSubIndex;
		Ext.get('eventIndex').dom.value='<%=eventIndex%>';
		var url='<ofbiz:url>/queryEventSubCategoryByIndex</ofbiz:url>?eventSubIndex='+eventSubIndex;
		extDlg.showEditDialog(obj,url);
	}
	
	//�޸�ʱԶ�̵��óɹ����ҳ�渳ֵ
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('subCategory').dom.value=result.subCategory;
			Ext.get('description').dom.value=result.desc;
		}
	}
	
	//ɾ��
	function delEventSubByIndex(eventSubIndex){
        Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˼�¼��',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/delEventSubCategoryByIndex</ofbiz:url>?eventSubIndex='+eventSubIndex+"&eventIndex=<%=eventIndex%>";
				document.location=url;
			}else{
				return;
			}
        });
	}
	
	//��ѯ
	function eventQuery(){
		if(Ext.get('eventCategory').dom.value==""){
			Ext.MessageBox.alert('Status', '��ѡ���¼�����!');
			return;
		}
		eventSubQueryForm.submit();
	}
	
	Ext.onReady(function(){
	    var eqpType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'eventCategory',
	        width:170,
	        forceSelection:true
	    });
	});
</script>
<form action="<%=request.getContextPath()%>/control/eventSubCategoryList" method="post" id="eventSubQueryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>ѡ���¼�����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC"> 
          <td width="12%" class="en11pxb">�¼�����:</td>
          <td width="28%">
          	<select id="eventCategory" name="eventCategory">
	          <option value=""></option>
	          <ofbiz:if name="eventCategoryList">
		        <ofbiz:iterator name="cust" property="eventCategoryList">
			    <option value='<ofbiz:inputvalue entityAttr="cust" field="eventIndex"/>'><ofbiz:inputvalue entityAttr="cust" field="category"/></option>
		      </ofbiz:iterator>
	      	</ofbiz:if>
    		</select>
      </td>
      <td width="20%" class="en11pxb" align="left">
      	<table border="0" cellspacing="0" cellpadding="0">
		  <tr height="30">
		   	<td width="20">&nbsp;</td>
		    <td><ul class="button">
					<li><a class="button-text" href="#" onclick="javascript:eventQuery();"><span>&nbsp;ȷ��&nbsp;</span></a></li> 
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
<ofbiz:if name="flag" value="ok">
<div id="reaList" style="visibility:'';">
</ofbiz:if>
<ofbiz:unless name="flag">
<div id="reaList" style="visibility:'hidden';">
</ofbiz:unless>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�¼�ϸ���б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addEventSub(this);"/></td>
          <td width="33%" class="en11pxb">�¼�ϸ��</td>
          <td width="62%" class="en11pxb">�¼�ϸ������</td>
        </tr>
        <ofbiz:if name="eventSubCategoryList">
	        <ofbiz:iterator name="cust" property="eventSubCategoryList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delEventSubByIndex('<ofbiz:inputvalue entityAttr="cust" field="eventSubIndex"/>')"/></td>
		          <td width="33%" class="en11px"><a href="#" onclick="editEventSub(this,'<ofbiz:inputvalue entityAttr="cust" field="eventSubIndex"/>')"><ofbiz:entityfield attribute="cust" field="subCategory"/></a></td>
		          <td width="62%" class="en11px"><ofbiz:entityfield attribute="cust" field="description"/></td>
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
    <div class="x-dlg-hd">�¼�ϸ��</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="�¼�ϸ��">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/eventSubCategoryDefine" method="post" id="eventSubForm" onsubmit="return false;">
                	<input id="eventSubIndex" type="hidden" name="eventSubIndex" value="" />
                	<input id="eventIndex" type="hidden" name="eventIndex" value="" />
                <p>
                <label for="name"><small>�¼�ϸ��</small></label>
                <input class="textinput" type="text" name="subCategory" id="subCategory" value="" size="22" tabindex="1" />
                </p>
                <p><label for="description"><small>�¼�ϸ������</small></label>
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
<script language="javascript">
	<ofbiz:if name="flag" value="ok">
		var obj=document.getElementById('eventCategory');
		obj.value='<%=eventIndex%>'
	</ofbiz:if>
</script>