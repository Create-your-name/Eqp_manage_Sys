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
			return "Ѳ��������Ϊ��";
		}
		else
		{
			var subName = name.substring(0,2);
			alert(subName);
			if (subName=="���" || subName=="��ʴ" || subName=="��Ĥ" || subName=="��ɢ" || subName=="����" || subName=="����" || subName=="����")
			{}
			else
			{
				return "Ѳ���������Բ��ŵ�ǰ2�ֿ�ͷ���硰��̡�������������������";
			}
		}
		if(description==""){
			return "��������Ϊ��";
		}
		
		var checkColumnLengthMsg = checkColumnLength('name||50||Ѳ����;description||100||����');
		if(checkColumnLengthMsg.length > 0 ) {
			return checkColumnLengthMsg;
		}
		
		return "";
	}
	
	//�ı䵯�����Сwidth,height
	extDlg.dlgInit('500','300');
	
	//�������ܵ���ҳ
	function addPcStyle(obj){
		Ext.get('styleIndex').dom.value="";
		Ext.get('name').dom.value="";
        Ext.get('description').dom.value="";
		extDlg.showAddDialog(obj);
	}
	
	//�޸Ĺ��ܵ���ҳ
	function editPcStyle(obj,pcStyleIndex){
		Ext.get('styleIndex').dom.value=pcStyleIndex;
		var url='<ofbiz:url>/queryPcStyleByIndex</ofbiz:url>?styleIndex='+pcStyleIndex;
		extDlg.showEditDialog(obj,url);
	}
	
	//�޸�ʱԶ�̵��óɹ����ҳ�渳ֵ
	function commentSuccess(o){
		 var result = eval('(' + o.responseText + ')');
		 if (result!=null & result!=""){
			Ext.get('name').dom.value=result.name;
			Ext.get('description').dom.value=result.desc;
		}
	}
	
	//ɾ��
	function delStyleIndex(styleIndex){
        Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˼�¼��',function result(value){
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
	response.getWriter().write("<font color=\"red\">" + "Ѳ���������Բ��ŵ�ǰ2�ֿ�ͷ���硰��̡����������������������Ա㽫δ���Ѳ����������ڲ��ţ�δ��ȷ˵���Ĺ�������ʩ����" + "</font>");
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>Ѳ����ʽ�б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9"> 
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addPcStyle(this);"/></td>
          <td width="33%" class="en11pxb">Ѳ����</td>
          <td width="62%" class="en11pxb">����</td>
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
    <div class="x-dlg-hd">Ѳ����ʽ</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="Ѳ����ʽ">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/pcStyleDefine" method="post" id="pcStyleForm" onsubmit="return false;">
                	<input id="styleIndex" type="hidden" name="styleIndex" value="" />
                <p>
                <label for="name"><small>Ѳ����</small></label>
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