<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ include file="../yui-ext/ext-comdlg.jsp"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%
	String dept = UtilFormatOut.checkNull(request.getParameter("equipmentDept"));
    if("".equals(dept)){
        dept = UtilFormatOut.checkNull((String) request.getAttribute("equipmentDept"));
    }
%>
<head>
<base target="_self">
</head>
<!-- yui page script-->
<script language="javascript">
	//���ݺϷ���У��
	function checkForm(){
		var partNo = Ext.get('partNo').dom.value;
        var partName = Ext.get('partName').dom.value;
        var price = Ext.get('price').dom.value;
        var costCenter = Ext.get('costCenter').dom.value;
		if(partNo==""){
			alert("���ϺŲ���Ϊ��");
			return;
		}
		if(partName==""){
			alert("����������Ϊ��");
			return;
		}
		if(price==""){
			alert("ƽ�����۲���Ϊ��");
			return;
		}
		if(isNaN(price)){
            alert("ƽ������RMB����Ϊ����!");
            return;
        }
		if(costCenter==""){
			alert("��ѡ����");
			return;
		}
		return "";
	}

	//�ı䵯�����Сwidth,height
	extDlg.dlgInit('500','300');

	//�������ܵ���ҳ
	function addFilterParts(obj){
		Ext.get('partNo').dom.value="";
        Ext.get('partName').dom.value="";
		extDlg.showAddDialog(obj);
	}

	//ɾ��
	function delPartsByNo(partsNo){
        Ext.MessageBox.confirm('ɾ��ȷ��', '��ȷ��Ҫɾ���˼�¼��',function result(value){
	        if(value=="yes"){
				var url='<ofbiz:url>/delFilterPartsByPartsNo</ofbiz:url>?partNo='+partsNo;
				//document.location=url;
				document.partsDataQueryForm.action = url;
			    document.partsDataQueryForm.submit();
			}else{
				return;
			}
        });
	}
	//��ѯ
   function doSubmit(url){
       if(document.partsDataQueryForm.equipmentDept.value=="") {
           alert("��ѡ���Ŵ��࣡");
           return;
       }
       loading();

       document.partsDataQueryForm.action = url;
       document.partsDataQueryForm.submit();
   }
</script>
<!-- yui page script-->
<script language="javascript">

var partsDataQueryForm = function() {

    var deptDS, deptCom;

    return {
        init : function() {
            this.createDataStore();
            this.createCombox();
            this.initLoad();
        },

        //��������Դ
        createDataStore : function() {

            deptDS = new Ext.data.Store({
                proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentDept</ofbiz:url>'}),
                reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'deptIndex'},{name: 'equipmentDept'}]))
            });

        },

        createCombox : function() {
            //����dept
            deptCom = new Ext.form.ComboBox({
                store: deptDS,
                displayField:'equipmentDept',
                valueField:'deptIndex',
                hiddenName:'equipmentDept',
                typeAhead: true,
                mode: 'local',
                width: 170,
                triggerAction: 'all'
            });
            deptCom.applyTo('deptSelect');

        },

        initLoad : function() {
            var dept = '<%=dept%>';
            deptDS.load({callback:function(){ deptCom.setValue(dept);}});
        }
    }
}();

Ext.EventManager.onDocumentReady(partsDataQueryForm.init,partsDataQueryForm,true);

</script>
<form method="post" name="partsDataQueryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�ͼ����ϲ�ѯ����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
         <tr bgcolor="#DFE1EC">
            <td width="5%" class="en11pxb" bgcolor="#ACD5C9">����:</td>
            <td width="28%">
              <input type="text" size="40" name="deptSelect" autocomplete="off"/>
           </td>
        </tr>
      </table>
      </fieldset></td>
  </tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
    <li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/queryFilterParts')"><span>&nbsp;��ѯ&nbsp;</span></a></li>
    </ul></td>
    <td width="20">&nbsp;</td>
  </tr>
</table>
</form>
<br>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�ͼ������б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="5%" class="en11pxb" align="center"><img  src="<%=request.getContextPath()%>/pms/images/plus.gif" style="cursor:hand" onclick="addFilterParts(this);"/></td>
          <td width="10%" class="en11pxb">����</td>
          <td width="20%" class="en11pxb">���Ϻ�</td>
          <td width="55%" class="en11pxb">������</td>
          <td width="10%" class="en11pxb">ƽ������RMB</td>
        </tr>
        <ofbiz:if name="filterPartsList">
	        <ofbiz:iterator name="cust" property="filterPartsList">
		        <tr bgcolor="#DFE1EC">
		          <td class="en11pxb" width="5%" align="center"><img src="<%=request.getContextPath()%>/pms/images/minus.gif" style="cursor:hand" onclick="delPartsByNo('<ofbiz:inputvalue entityAttr="cust" field="partNo"/>')"/></td>
		          <td class="en11px"><ofbiz:entityfield attribute="cust" field="costCenter"/></td>
		          <td class="en11px"><ofbiz:entityfield attribute="cust" field="partNo"/></td>
		          <td class="en11px"><ofbiz:entityfield attribute="cust" field="partName"/></td>
		          <td class="en11px"><ofbiz:entityfield attribute="cust" field="price"/></td>
		        </tr>
	      </ofbiz:iterator>
      </ofbiz:if>
      </table>
      </fieldset></td>
  </tr>
</table>


<!-- add dialog -->
<div id="x-dlg" style="visibility:hidden;">
    <div class="x-dlg-hd">�����ͼ�����</div>
    <div class="x-dlg-bd">
        <div id="post-tab" class="x-dlg-tab" title="�ͼ�����">
            <div class="inner-tab" id="x-form">
                <form action="<%=request.getContextPath()%>/control/addFilterParts" method="post" id="filterPartsForm"  onsubmit="return false;">
                <p>
                <label for="name"><small>���Ϻ�</small></label>
                <input class="textinput" type="text" name="partNo" id="partNo" maxlength="20" value="" size="22" tabindex="1" />
                </p>
                <p><label for="description"><small>������</small></label>
                <input class="textinput" type="text" name="partName" id="partName" maxlength="50" value="" size="22" tabindex="2" />
                </p>
                <p><label for="description"><small>ƽ������RMB</small></label>
                <input class="textinput" type="text" name="price" id="price" maxlength="10" value="" size="22" tabindex="3" />
                </p>
                <p><label for="description"><small>ѡ����</small></label>
                    <select name='costCenter' style="width:170">
                      <option value=""></option>
                      <ofbiz:if name="eqpDeptList">
                        <ofbiz:iterator name="cust" property="eqpDeptList">
                        <option value='<ofbiz:inputvalue entityAttr="cust" field="deptIndex"/>'>
                            <ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>
                        </option>
                      </ofbiz:iterator>
                    </ofbiz:if>
                    </select>
                </p>
            </td>
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

</script>