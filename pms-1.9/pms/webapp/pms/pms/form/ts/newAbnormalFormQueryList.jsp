<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="com.csmc.pms.webapp.util.Constants"%>
<%
	String startDate=UtilFormatOut.checkNull((String)request.getAttribute("startDate"));
	String endDate=UtilFormatOut.checkNull((String)request.getAttribute("endDate"));
	String eqpId=UtilFormatOut.checkNull((String)request.getAttribute("eqpId"));
	List abnormalList = (List)request.getAttribute("ABNORMALFORM_LIST");

	String maintDept = UtilFormatOut.checkNull((String) request.getAttribute("maintDept"));
%>

<!-- yui page script-->
<script language="javascript">

	//查询
	function query(){
	    var maintDept=Ext.get('maintDept').dom.value;
		var eqpId=Ext.get('eqpId').dom.value;
		var endDate=Ext.get('endDate').dom.valu;
		var startDate=Ext.get('startDate').dom.value;

		if (maintDept==""||maintDept==undefined) {
		    Ext.MessageBox.alert('警告', '请选择部门!');
			return;
		}

		if((startDate==""||startDate==undefined) &&(endDate==""||endDate==undefined) && eqpId==""){
			Ext.MessageBox.alert('警告', '请输入查询条件!');
			return;
		}
		loading();
		tsForm.submit();
	}

	//重置
	function reset(){
	    Ext.get('maintDept').dom.value='';
		Ext.get('eqpId').dom.value='';
		Ext.get('startDate').dom.value='';
		Ext.get('endDate').dom.value='';
	}

	//进入表单新建页面
	function intoAbnormalForm(index,eqpId){
		loading();
		var actionURL='<ofbiz:url>/newOverAbnormalFormView</ofbiz:url>?functionType=3&abnormalIndex='+index+'&eqpId='+eqpId;
		document.location.href=actionURL;
	}

	//初始化页面控件
	Ext.onReady(function(){
	     var startDate = new Ext.form.DateField({
	    	format: 'Y-m-d',
	        allowBlank:true
	    });

	 	var endDate = new Ext.form.DateField({
	 		format: 'Y-m-d',
	        allowBlank:true
	    });

	    startDate.applyTo('startDate');
	    endDate.applyTo('endDate');

	    var dept = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'maintDept',
	        width:170,
	        forceSelection:true
	    });
	    dept.on('select',loadEqpId);

	    var eqpIdDS = new Ext.data.Store({
	        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentByAnd</ofbiz:url>'}),
		    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
	    });

	    //设置eqpId
		var	eqpIdCom = new Ext.form.ComboBox({
		    store: eqpIdDS,
		    displayField:'equipmentId',
		    valueField:'equipmentId',
		    hiddenName:'eqpId',
		    typeAhead: true,
		    mode: 'local',
		    width: 170,
		    triggerAction: 'all'
	    });
	    eqpIdCom.applyTo('eqpIdSelect');

	    function loadEqpId() {
    		var val = dept.getValue();
    		if (val == '<%=maintDept%>') {
    		    var equipmentId  = '<%=UtilFormatOut.checkNull(request.getParameter("eqpId"))%>';
    		} else {
    		    var equipmentId  = '';
    		}

    		eqpIdDS.load({params:{maintDept:val},callback:function(){ eqpIdCom.setValue(equipmentId); }});
    	};

        // initial dept & epqid
    	dept.setValue("<%=maintDept%>");
    	eqpIdCom.setValue('<%=eqpId%>');
    	loadEqpId();

	    initDate();
	 });

	 function initDate(){
		 Ext.get('startDate').dom.value='<%=startDate%>';
		 Ext.get('endDate').dom.value='<%=endDate%>';
	 }
</script>
<form action="<%=request.getContextPath()%>/control/newQueryOverAbnormalFormByCondition" method="post" id="tsForm" onsubmit="return false;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>异常记录查询参数</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
	        <td bgcolor="#ACD5C9" class="en11pxb">表单创建日期</td>
	        <td width="30%"><input type="text" ID="startDate" NAME="startDate" readonly size="26"></td>

	    	<td  width="10%" bgcolor="#ACD5C9" class="en11pxb">到:</td>
	    	<td width="45%"><input type="text" ID="endDate" NAME="endDate" readonly size="26"></td>
        </tr>

        <tr bgcolor="#DFE1EC">
	        <td bgcolor="#ACD5C9" class="en11pxb">部门</td>
	        <td width="30%">
	            <select id="maintDept" name="maintDept">
         			<option value=''></option>
	          		<ofbiz:if name="deptList">
		        		<ofbiz:iterator name="cust" property="deptList">
			    			<option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>'>
			    			    <ofbiz:inputvalue entityAttr="cust" field="equipmentDept"/>
			    			</option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
    			</select>
    	    </td>

	    	<td  width="10%" bgcolor="#ACD5C9" class="en11pxb">EqpId</td>
	    	<td width="45%">
	    	    <input type="text" size="40" name="eqpIdSelect" autocomplete="off"/>
	    	</td>
        </tr>

        <tr bgcolor="#DFE1EC">
        	<td class="en11pxb" colspan="4" align="left">
	        	<table border="0" cellspacing="0" cellpadding="0">
			  		<tr height="30">
					    <td><ul class="button">
							<li><a class="button-text" href="#" onclick="javascript:query();"><span>&nbsp;查询&nbsp;</span></a></li>
						</ul>
						<ul class="button">
							<li><a class="button-text" href="#" onclick="javascript:reset();"><span>&nbsp;重置&nbsp;</span></a></li>
						</ul>
						</td>
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
      <legend>异常记录表单列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="10%" class="en11pxb">机台编号</td>
          <td width="20%" class="en11pxb">表单编号</td>
          <td width="8%" class="en11pxb">表单类型</td>
          <td width="10%" class="en11pxb">撰写人</td>
          <td width="8%" class="en11pxb">故障现象</td>
          <td width="8%" class="en11pxb">实际工时</td>
          <td width="15%" class="en11pxb">发生时间</td>
          <td width="14%" class="en11pxb">结束时间</td>
        </tr>
         <% if(abnormalList != null && abnormalList.size() > 0) {
       		for(Iterator it = abnormalList.iterator();it.hasNext();) {
					Map map = (Map)it.next();%>
	        <tr bgcolor="#DFE1EC">
		    <td class="en11px"><a href="#" onclick="intoAbnormalForm('<%=map.get("ABNORMAL_INDEX")%>','<%=map.get("EQUIPMENT_ID")%>')"><%=map.get("EQUIPMENT_ID")%></a></td>
		    <td class="en11px"><%=map.get("ABNORMAL_NAME")%></td>
		    <td class="en11px"><%if(Constants.FORM_TYPE_PATCH.equalsIgnoreCase((String)map.get("FORM_TYPE"))){%>补填<%}else{%>正常<%}%></td>
		    <td class="en11px"><%=map.get("CREATE_NAME")%></td>
		    <td class="en11px"><%=map.get("ABNORMAL_TEXT")%></td>
		    <td class="en11px"><%=map.get("MAN_HOUR")%></td>
		    <td class="en11px"><%=map.get("ABNORMAL_TIME")%></td>
		    <td class="en11px"><%=map.get("END_TIME")%></td>
		    </tr>
	     <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>
