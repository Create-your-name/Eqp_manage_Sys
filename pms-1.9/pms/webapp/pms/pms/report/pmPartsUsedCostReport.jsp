<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat"%>
<%
	List partList = (List)request.getAttribute("PART_LIST");
	String partNo=UtilFormatOut.checkNull(request.getParameter("partNo"));
	String pmType=UtilFormatOut.checkNull(request.getParameter("pmType"));
	String sMonth = UtilFormatOut.checkNull(request.getParameter("month"));
	String eqpType = UtilFormatOut.checkNull(request.getParameter("equipmentType"));
    String dept = UtilFormatOut.checkNull(request.getParameter("equipmentDept"));
 %>

<!-- yui page script-->
<script lanaguage="javascript">

    function doSubmit(url) {
        if(document.eqpForm.equipmentDept.value=="") {
            alert("请选择部门！");
            return;
        }
        if(document.eqpForm.month.value=="") {
            alert("请选择查询月份！");
            return;
        }

        for(var i=0;i<document.eqpForm.eqpIdSelected.options.length;i++) {
            document.eqpForm.eqpIdSelected.options[i].selected = true;
        }

        loading();

        document.eqpForm.action = url;
        document.eqpForm.submit();
    }

    function doAddEqp() {
        if(document.eqpForm.eqpId.options.length==0)
        {
            alert("请选择设备大类！");
            return;
        }

        moveOption(document.eqpForm.eqpId,document.eqpForm.eqpIdSelected);
    }

    function doRemoveEqp() {
        if(document.eqpForm.eqpIdSelected.options.length==0)
        {
            alert("请选择设备！");
            return;
        }
        moveOption(document.eqpForm.eqpIdSelected,document.eqpForm.eqpId);
    }

    function moveOption(objFromSelect,objToSelect) {
        for(var i=0;i<objFromSelect.options.length;i++) {
            if (objFromSelect.options[i].selected) {
                var e = objFromSelect.options[i];
                objToSelect.options.add(new Option(e.text, e.value));
                objFromSelect.remove(i);
                i = i - 1;
            }
        }
    }

    function clearEqp(eqpSelectObj) {
        for(var i=0;i<eqpSelectObj.options.length;i++) {
            eqpSelectObj.remove(i);
            i = i - 1;
        }
    }

    var oldDeptValue="";
    var oldEqpValue="";

    //部门选择或设备大类选择
    function eqpTypeChange(){
        var newDeptValue = Ext.get('equipmentDept').dom.value;
        var newEqpValue = Ext.get('equipmentType').dom.value;

        var actionURL = '<ofbiz:url>/queryEqpIdByEqpTypeByCommon</ofbiz:url>?equipmentType='+newEqpValue+'&maintDept='+newDeptValue;

        actionURL = encodeURI(actionURL);
        if(oldEqpValue != newEqpValue || oldDeptValue != newDeptValue){
            Ext.lib.Ajax.formRequest('eqpForm',actionURL,{success: commentSuccess, failure: commentFailure});
        }

        oldDeptValue = newDeptValue;
        oldEqpValue = newEqpValue;
    }

    //远程调用成功
    function commentSuccess(o){
        //clear all eqpid options
        clearEqp(document.eqpForm.eqpIdSelected);
        clearEqp(document.eqpForm.eqpId);

        var eqpArray = eval('(' + o.responseText + ')');
        var eqpArraySize = eqpArray.length;

        var equipId = document.getElementById("eqpId");
        equipId.length = 0;
        for(var i=0; i < eqpArraySize; i++){
            equipId.options[equipId.length] = new Option(eqpArray[i].equipmentId,eqpArray[i].equipmentId);
        }

    }

    //远程调用失败
    var commentFailure = function(o){
        Ext.MessageBox.alert('Status', 'Unable to connect.');
    };

</script>
<!-- yui page script-->
<script language="javascript">

var eqpForm = function() {

    var deptDS, deptCom;
    var eqpTypeDS, eqpTypeCom;
    var periodDS, periodCom;
    var periodRecordDef;
    var monthDS,monthCom;

    return {
        init : function() {
            this.createDataStore();
            this.createCombox();
            this.initLoad();
        },

        //设置数据源
        createDataStore : function() {
            periodRecordDef = Ext.data.Record.create([
                {name: 'periodIndex'},
                {name: 'periodName'}
            ]);

            deptDS = new Ext.data.Store({
                proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentDept</ofbiz:url>'}),
                reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentDept'}]))
            });

            eqpTypeDS = new Ext.data.Store({
                proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentTypeByCommon</ofbiz:url>'}),
                reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentType'}]))
            });

            periodDS = new Ext.data.Store({
                proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryPMPeriodByEqpType</ofbiz:url>'}),
                reader: new Ext.data.JsonReader({}, periodRecordDef)
            });

            monthDS = new Ext.data.Store({
                proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryMonthsList</ofbiz:url>'}),
                reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'month'}]))
            });
        },

        createCombox : function() {
            //设置dept
            deptCom = new Ext.form.ComboBox({
                store: deptDS,
                displayField:'equipmentDept',
                valueField:'equipmentDept',
                hiddenName:'equipmentDept',
                typeAhead: true,
                mode: 'local',
                width: 170,
                triggerAction: 'all'
            });
            deptCom.applyTo('deptSelect');
            deptCom.on('select', eqpTypeChange);

            //设置设备大类
            eqpTypeCom = new Ext.form.ComboBox({
                store: eqpTypeDS,
                displayField:'equipmentType',
                valueField:'equipmentType',
                hiddenName:'equipmentType',
                typeAhead: true,
                mode: 'local',
                width: 170,
                triggerAction: 'all'
            });
            eqpTypeCom.applyTo('eqpTypeSelect');
            eqpTypeCom.on('select', this.loadPeriod);
            eqpTypeCom.on('select', eqpTypeChange);

            //设置查询月份
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

            //设置pm周期
            periodCom = new Ext.form.ComboBox({
                store: periodDS,
                displayField:'periodName',
                valueField:'periodIndex',
                hiddenName:'periodIndex',
                typeAhead: true,
                mode: 'local',
                width: 170,
                triggerAction: 'all'
            });
            periodCom.applyTo('periodSelect');

        },

        initLoad : function() {
            var eqpType = '<%=eqpType%>';
            var dept = '<%=dept%>';
            var sMonth = '<%=sMonth%>';
            var partNo = '<%=partNo%>';
            var pmType = '<%=pmType%>';
            if(partNo != ''){
                document.eqpForm.partNo.value=partNo;
            }
            deptDS.load({callback:function(){ deptCom.setValue(dept); if(dept != '' && eqpType == ''){eqpTypeChange();} }});
            monthDS.load({callback:function(){ monthCom.setValue(sMonth);}});
            eqpTypeDS.load({callback:function(){ eqpTypeCom.setValue(eqpType); if(eqpType != ''){eqpForm.loadPeriod(); eqpTypeChange();} }});

            var obj=document.getElementById("pmType");
            for(i=0;i<obj.length;i++){
               if(obj[i].value==pmType){
            	   obj[i].selected = true;
            	   return;
               }
            }
        },

        loadPeriod : function() {
            var val = eqpTypeCom.getValue(), periodIndex  = '<%=UtilFormatOut.checkNull(request.getParameter("periodIndex"))%>';
            periodDS.load({params:{equipmentType:val},callback:function(){ periodCom.setValue(periodIndex); }});
        }
    }
}();

Ext.EventManager.onDocumentReady(eqpForm.init,eqpForm,true);

</script>

<form action="<%=request.getContextPath()%>/control/mcsPartsUseDiff" method="post" id="eqpForm" name="eqpForm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>PM用料明细查询参数</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
	     <tr bgcolor="#DFE1EC">
		    <td width="12%" class="en11pxb" bgcolor="#ACD5C9">部门:</td>
		    <td width="28%" colspan="3" ><input type="text" size="40" name="deptSelect" autocomplete="off"/></td>
	    </tr>
	    <tr bgcolor="#DFE1EC">
           <td class="en11pxb" bgcolor="#ACD5C9">设备大类</td>
           <td colspan="3" ><input type="text" size="40" name="eqpTypeSelect" autocomplete="off"/></td>
        </tr>
	     <tr bgcolor="#DFE1EC">
	    <tr bgcolor="#DFE1EC">
           <td class="en11pxb" bgcolor="#ACD5C9">PM周期</td>
           <td colspan="3" ><input type="text" size="40" name="periodSelect" autocomplete="off"/></td>
        </tr>
	     <tr bgcolor="#DFE1EC">
	       <td width="12%" class="en11pxb" bgcolor="#ACD5C9">保养类别:</td>
            <td width="28%"  colspan="3">
              <select id="pmType" name="pmType">
              <option value="<%=Constants.PM%>">正常保养</option>
              <option value="<%=Constants.TS%>">异常保养</option>
              </select>
           </td>
	    </tr>
	     <tr bgcolor="#DFE1EC">
	       <td width="12%" class="en11pxb" bgcolor="#ACD5C9">查询月份:</td>
           <td width="28%" colspan="3">
           <input type="text" size="40" name="monthSelect" autocomplete="off"/>
           </td>
	    </tr>
      </table>
      </fieldset></td>
  </tr>
</table>
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0" id="table3">
  <tr>
    <td valign="top"> <fieldset> <legend>设备列表</legend>
        <table width="100%" border="0" cellspacing="1" cellpadding="0" id="table4">
          <tr bgcolor="#DFE1EC">

            <td width="33%" class="en11pxb"><table cellSpacing="1" cellPadding="0" width="95%" border="0">
              <tr align="middle" bgColor="#dfe1ec">
                <td class="title-en" align="middle" width="299"></td>
              </tr>
              <tr bgColor="#dfe1ec">
                <td align="middle" width="304">
                    <select name="eqpId" id="eqpId" class="select" multiple size=8 ondblclick="doAddEqp();">
                    </select>
                </td>
              </tr>
            </table></td>

            <td width="35%" height="35" class="en11pxb" align="center">
            <table height="64" cellSpacing="1" cellPadding="0" width="95%" border="0" id="table14">
              <tr align="middle">
                <td width="140"><table cellSpacing="0" cellPadding="0" border="0" id="table15">
                    <tr>
                      <td width="11"><img src="/pms/images/button_left.gif"></td>
                      <td background="/pms/images/button_bg.gif"><a class="button-text" href="javascript:doAddEqp();"> &gt;&gt;</a></td>
                      <td width="20"><img src="/pms/images/button_right.gif"></td>
                    </tr>
                  </table>
                    <table cellSpacing="0" cellPadding="0" border="0" id="table16">
                      <tr>
                        <td width="11"><img src="/pms/images/button_left.gif"></td>
                        <td background="/pms/images/button_bg.gif"><a class="button-text" href="javascript:doRemoveEqp();"> &lt;&lt;</a></td>
                        <td width="20"><img src="/pms/images/button_right.gif"></td>
                      </tr>
                  </table></td>
              </tr>
            </table>
            </td>

            <td width="32%" class="en11pxb">
            <table cellSpacing="1" cellPadding="0" width="224" border="0" id="table10">
              <tr align="middle" bgColor="#dfe1ec">
                <td class="title-en" align="middle" width="299"></td>
              </tr>
              <tr bgColor="#dfe1ec">
                <td align="middle" width="304">
                    <select name="eqpIdSelected" id="eqpIdSelected" class="select" multiple size=8 ondblclick="doRemoveEqp();">
                    </select>
                </td>
              </tr>
            </table>
            </td>

          </tr>

        </table>

      </fieldset>
    </td>
  </tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
    <li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/pmPartsUsedCostDetailQuery')"><span>&nbsp;查询&nbsp;</span></a></li>
    </ul></td>
    <td width="20">&nbsp;</td>
  </tr>
</table>
</form>
<br>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>PM用料明细查询列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="5%" class="en11pxb">部门</td>
          <td width="5%" class="en11pxb">设备</td>
          <td width="15%" class="en11pxb">保养周期</td>
          <td width="15%" class="en11pxb">保养时间</td>
          <td width="8%" class="en11pxb">实际费用</td>
          <td width="7%" class="en11pxb">理论费用</td>
          <td width="10%" class="en11pxb">实际与理论差异</td>
        </tr>
       <% if(partList != null && partList.size() > 0) {
       		for(Iterator it = partList.iterator();it.hasNext();) {
					Map map = (Map)it.next();
					double diff = Double.valueOf((String)map.get("COST_DIFF"));
					String color = diff != 0 ? "red":"black" ;
	    %>
		        <tr bgcolor="#DFE1EC">
		          <td class="en11px"><%=map.get("MAINT_DEPT")%></td>
		          <td class="en11px"><%=map.get("EQUIPMENT_ID")%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("PM_NAME"))%></td>
		          <td class="en11px"><%=map.get("START_TIME")%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("USED_COST"))%></td>
		          <td class="en11px"><%=UtilFormatOut.checkNull((String)map.get("COST"))%></td>
		          <td class="en11px" style="color: <%=color%>;"><%=UtilFormatOut.checkNull((String)map.get("COST_DIFF"))%></td>
		        </tr>
	  <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>
<br>
