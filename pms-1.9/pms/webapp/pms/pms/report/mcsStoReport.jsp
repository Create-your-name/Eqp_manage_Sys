<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%
	List partList = (List)request.getAttribute("PART_LIST");
    String dept = UtilFormatOut.checkNull(request.getParameter("equipmentDept"));
	String partNo=UtilFormatOut.checkNull(request.getParameter("partNo"));
	String sMonth = UtilFormatOut.checkNull(request.getParameter("month"));
 %>

<!-- yui page script-->
<script language="javascript">
	//��ѯ
   function doSubmit(url){
	   if(document.partsDataQueryForm.month.value=="") {
           alert("��ѡ���ѯ�·ݣ�");
           return;
       }
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
    var monthDS,monthCom;

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

            monthDS = new Ext.data.Store({
                proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryMonthsList</ofbiz:url>'}),
                reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'month'}]))
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

            //���ò�ѯ�·�
            monthCom = new Ext.form.ComboBox({
                store: monthDS,
                displayField:'month',
                valueField:'month',
                hiddenName:'month',
                typeAhead: true,
                mode: 'local',
                width: 170,
                triggerAction: 'all'
            });
            monthCom.applyTo('monthSelect');
        },

        initLoad : function() {
            var dept = '<%=dept%>';
            var sMonth = '<%=sMonth%>';
            var partNo = '<%=partNo%>';
            deptDS.load({callback:function(){ deptCom.setValue(dept);}});
            monthDS.load({callback:function(){ monthCom.setValue(sMonth);}});
            document.partsDataQueryForm.partNo.value==partNo;
        }
    }
}();

Ext.EventManager.onDocumentReady(partsDataQueryForm.init,partsDataQueryForm,true);

</script>
<form method="post" name="partsDataQueryForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�ֿ߲߱��������ѯ����</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
	     <tr bgcolor="#DFE1EC">
	       <td width="12%" class="en11pxb" bgcolor="#ACD5C9">��ѯ�·�:</td>
	       <td width="28%">
	       <input type="text" size="40" name="monthSelect" autocomplete="off"/>
	       </td>
		    <td width="12%" class="en11pxb" bgcolor="#ACD5C9">����:</td>
		    <td width="28%">
		      <input type="text" size="40" name="deptSelect" autocomplete="off"/>
	       </td>
	    </tr>
	     <tr bgcolor="#DFE1EC">
	       <td width="12%" class="en11pxb" bgcolor="#ACD5C9">���Ϻ�:</td>
	       <td width="28%" colspan="3"><input class="input" type="text" name="partNo" id="partNo" /></td>
	    </tr>
      </table>
      </fieldset></td>
  </tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
    <li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/mcsPartsUseDiff')"><span>&nbsp;��ѯ&nbsp;</span></a></li>
    </ul></td>
    <td width="20">&nbsp;</td>
  </tr>
</table>
</form>
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>�ֿ߲߱��������ѯ�б�</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="15%" class="en11pxb">���Ϻ�</td>
          <td width="30%" class="en11pxb">��������</td>
          <td width="10%" class="en11pxb">������</td>
          <td width="10%" class="en11pxb">�������</td>
          <td width="20%" class="en11pxb">�����������</td>
          <td width="5%" class="en11pxb">����</td>
          <td width="10%" class="en11pxb">�������</td>
        </tr>
        <% if(partList != null && partList.size() > 0) {
       		for(Iterator it = partList.iterator();it.hasNext();) {
					Map map = (Map)it.next();
					int delay = Integer.valueOf((String)map.get("DELAY_DAYS"));
					String color = delay > 7 ? "red":"black" ;
					String plant = UtilFormatOut.checkNull((String)map.get("PLANT"));
					String mtrPlant = "";
					if("1200".equals(plant)){
					    mtrPlant = "��Ʒ";
					}
					else if("1201".equals(plant)){
                        mtrPlant = "ά��Ʒ";
                    }
	    %>
		        <tr bgcolor="#DFE1EC">
		          <td class="en11px"><%=map.get("MTR_NUM")%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("MTR_DESC"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("MTR_GRP"))%></td>
		          <td class="en11px"><%=mtrPlant%></td>
		          <td class="en11px" style="color: <%=color%>;"><%=UtilFormatOut.checkNull((String)map.get("DOC_TIME"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("VERPR"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("QTY"))%></td>
		        </tr>
	  <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>
