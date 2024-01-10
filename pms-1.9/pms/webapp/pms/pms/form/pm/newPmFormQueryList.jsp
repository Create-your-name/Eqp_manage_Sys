<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%
	String startDate=UtilFormatOut.checkNull((String)request.getAttribute("startDate"));
	String endDate=UtilFormatOut.checkNull((String)request.getAttribute("endDate"));
	String eqpId=UtilFormatOut.checkNull((String)request.getAttribute("eqpId"));
	List pmList = (List)request.getAttribute("PMFORM_LIST");

	String maintDept = UtilFormatOut.checkNull((String) request.getAttribute("maintDept"));
	
	String equipmentType = UtilFormatOut.checkNull(request.getParameter("equipmentType"));
	String periodIndex = UtilFormatOut.checkNull(request.getParameter("periodIndex"));

%>

<!-- yui page script-->
<script language="javascript">

	var dept;
	var eqpIdCom;
	var eType;
	var eqpIdCom2;
	var periodIndexCom;
	
	function selectDept()
	{
		Ext.get('ra1').dom.checked = 'true';
		Ext.get('ra2').dom.checked = '';
		Ext.get('div1').dom.style.display ='';
	 	Ext.get('div2').dom.style.display ='none';
	 	
    	eType.setValue("");
    	eqpIdCom2.setValue("");
    	periodIndexCom.setValue("");
	}
	
	function selectType()
	{
		Ext.get('ra1').dom.checked = '';
		Ext.get('ra2').dom.checked = 'true';
		Ext.get('div1').dom.style.display ='none';
	 	Ext.get('div2').dom.style.display ='';
	 	
	 	dept.setValue("");
    	eqpIdCom.setValue("");
	}
	


	//查询
	function query(){
	    var maintDept=Ext.get('maintDept').dom.value;
		var eqpId;
		if ((Ext.get('ra1').dom.checked+"") == "true")
		{
			eqpId=Ext.get('eqpId').dom.value;
		}
		else
		{
			eqpId=Ext.get('eqpId2').dom.value;
		}
		
		var equipmentType= Ext.get('equipmentType').dom.value;
		var periodIndex= Ext.get('periodIndex').dom.value;
		var endDate=Ext.get('endDate').dom.valu;
		var startDate=Ext.get('startDate').dom.value;

		if ((Ext.get('ra1').dom.checked+"") == "true")
		{
			if (maintDept==""||maintDept==undefined) 
			{
			    Ext.MessageBox.alert('警告', '请选择部门!');
				return;
			}
		}
		else
		{
			if (equipmentType==""||equipmentType==undefined) 
			{
			    Ext.MessageBox.alert('警告', '请选择设备大类!');
				return;
			}
		}



		if((startDate==""||startDate==undefined) && (endDate==""||endDate==undefined) && eqpId==""){
			Ext.MessageBox.alert('警告', '请输入查询条件!');
			return;
		}

		loading();
		pmForm.submit();
	}

	//重置
	function reset(){
	    Ext.get('maintDept').dom.value='';
		Ext.get('eqpId').dom.value='';
		Ext.get('equipmentType').dom.value='';
		Ext.get('eqpId2').dom.value='';
		Ext.get('periodIndex').dom.value='';
		Ext.get('startDate').dom.value='';
		Ext.get('endDate').dom.value='';
	}

	//进入表单新建页面
	function intoPmForm(index,eqpId){
		loading();
		var actionURL='<ofbiz:url>/newOverPmFormView</ofbiz:url>?functionType=3&pmIndex='+index+'&eqpId='+eqpId;
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

		//==============================================
		//设置equipmentTypeCom
		eType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'equipmentType',
	        width:170,
	        forceSelection:true
	    });
	    eType.on('select',loadPeriodIndex);
	    eType.on('select',loadEqpId2);

	    //设置periodIndexCom
	    var periodRecordDef = Ext.data.Record.create([
			{name: 'periodIndex'},
			{name: 'periodName'}
		]);
			
	    var periodDS = new Ext.data.Store({
		    proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryPMPeriodByEqpType</ofbiz:url>'}),
			reader: new Ext.data.JsonReader({}, periodRecordDef)
		});

		periodIndexCom = new Ext.form.ComboBox({
		    store: periodDS,
		    displayField:'periodName',
		    valueField:'periodIndex',
		    hiddenName:'periodIndex',
		    typeAhead: true,
		    mode: 'local',
		    width: 170,
		    triggerAction: 'all'
	    });
	    periodIndexCom.applyTo('periodSelect');

	    function loadPeriodIndex() {
    		var val = eType.getValue();
    		if (val == '<%=equipmentType%>') {
    		    var periodIndex  = '<%=UtilFormatOut.checkNull(request.getParameter("periodIndex"))%>';
    		} else {
    		    var periodIndex  = '';
    		}

    		periodDS.load({params:{equipmentType:val},callback:function(){ periodIndexCom.setValue(periodIndex); }});
    	};
    	
    	var eqpIdDS2 = new Ext.data.Store({
	        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryEquipmentByAnd</ofbiz:url>'}),
		    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'equipmentId'}]))
	    });

	    //设置eqpId2
			eqpIdCom2 = new Ext.form.ComboBox({
		    store: eqpIdDS2,
		    displayField:'equipmentId',
		    valueField:'equipmentId',
		    hiddenName:'eqpId2',
		    typeAhead: true,
		    mode: 'local',
		    width: 170,
		    triggerAction: 'all'
	    });
	    eqpIdCom2.applyTo('eqpIdSelect2');

	    function loadEqpId2() {
    		var val = eType.getValue();
    		if (val == '<%=equipmentType%>') {
    		    var equipmentId  = '<%=UtilFormatOut.checkNull(request.getParameter("eqpId"))%>';
    		} else {
    		    var equipmentId  = '';
    		}

    		eqpIdDS2.load({params:{equipmentType:val},callback:function(){ eqpIdCom2.setValue(equipmentId); }});
    	};
		//==============================================


	    dept = new Ext.form.ComboBox({
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
		eqpIdCom = new Ext.form.ComboBox({
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
    	
    	eType.setValue("<%=equipmentType%>");
    	eqpIdCom2.setValue('<%=eqpId%>');
    	periodIndexCom.setValue('<%=periodIndex%>');
    	loadPeriodIndex();
    	loadEqpId2();

	    initDate();
	    selectDept();
	 });

	 function initDate(){
	 
		 Ext.get('startDate').dom.value='<%=startDate%>';
		 Ext.get('endDate').dom.value='<%=endDate%>';
	 }

</script>

<form action="<%=request.getContextPath()%>/control/newQueryOverPmFormByCondition" method="post" id="pmForm" onsubmit="return false;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
  <td><fieldset>
      <legend>保养记录查询</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC">
	        <td colspan = "2" width="10%" bgcolor="#ACD5C9" class="en11pxb">表单创建日期</td>
	        <td width="20%"><input type="text" ID="startDate" NAME="startDate" readonly size="26"></td>

	    	<td  width="10%" bgcolor="#ACD5C9" class="en11pxb">到:</td>
	    	<td width="25%"><input type="text" ID="endDate" NAME="endDate"  readonly size="26"></td>
	    	<td colspan = "2" width="35%" bgcolor="#DFE1EC" class="en11pxb"> </td>
        </tr>
        
        <tr bgcolor="#DFE1EC">
        	<td width="2%" bgcolor="#ACD5C9" class="en11pxb"><input type="radio" name="ra1" id="ra1" value="0" onclick="selectDept()"/></td>
        	<td width="8%" bgcolor="#ACD5C9" class="en11pxb">部门</td>
        	<td colspan="5">
	        	<div name="div1" id="div1">
		        	<table>
			        	<tr>
					        <td width="20%">
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
				
					    	<td width="10%" bgcolor="#ACD5C9" class="en11pxb">EqpId</td>
					    	<td width="25%">
					    	    <input type="text" size="40" name="eqpIdSelect" autocomplete="off"/>
					    	</td>
					    	<td colspan = "2" width="35%" bgcolor="#DFE1EC" class="en11pxb"> </td>
				    	</tr>
			    	</table>
		    	</div>
	    	</td>
        </tr>
        <tr bgcolor="#DFE1EC">
        	<td width="2%" bgcolor="#ACD5C9" class="en11pxb"><input type="radio" name="ra2" id="ra2" value="0" onclick="selectType()"></td>
        	<td width="8%" bgcolor="#ACD5C9" class="en11pxb">设备大类</td>
        	<td colspan="5">
        		<div name="div2" id="div2">
		        	<table width="100%">
		        		<tr>
							<td width="30%">
					            <select id=equipmentType name="equipmentType">
				         			<option value=''></option>
					          		<ofbiz:if name="eqpTypeList">
						        		<ofbiz:iterator name="cust" property="eqpTypeList">
							    			<option value='<ofbiz:inputvalue entityAttr="cust" field="equipmentType"/>'>
							    			    <ofbiz:inputvalue entityAttr="cust" field="equipmentType"/>
							    			</option>
						      			</ofbiz:iterator>
					      			</ofbiz:if>
				    			</select>
				    	    </td>
				
					    	<td width="10%" bgcolor="#ACD5C9" class="en11pxb">EqpId</td>
					    	<td width="25%">
					    	    <input type="text" size="40" name="eqpIdSelect2" autocomplete="off"/>
					    	</td>
				
					    	<td width="10%" bgcolor="#ACD5C9" class="en11pxb">PM周期</td>
					    	<td width="25%">
					    	    <input type="text" size="40" name="periodSelect" autocomplete="off"/>
					    	</td>
				    	</tr>
			    	</table>
			    </div>	
	    	</td>
        </tr>

        <tr bgcolor="#DFE1EC">
        	<td class="en11pxb" colspan="7" align="left">
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
      <legend>保养记录表单列表</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#ACD5C9">
          <td width="10%" class="en11pxb">机台编号</td>
          <td width="20%" class="en11pxb">表单编号</td>
          <td width="10%" class="en11pxb">撰写人</td>
          <td width="10%" class="en11pxb">表单类型</td>
          <td width="10%" class="en11pxb">保养类别</td>
          <td width="10%" class="en11pxb">实际工时</td>
          <td width="15%" class="en11pxb">开始时间</td>
          <td width="15%" class="en11pxb">结束时间</td>
        </tr>
         <% if(pmList != null && pmList.size() > 0) {
       		for(Iterator it = pmList.iterator();it.hasNext();) {
					Map map = (Map)it.next();%>
	        <tr bgcolor="#DFE1EC">
		    <td class="en11px"><a href="#" onclick="intoPmForm('<%=map.get("PM_INDEX")%>','<%=map.get("EQUIPMENT_ID")%>')"><%=map.get("EQUIPMENT_ID")%></a></td>
		    <td class="en11px"><%=map.get("PM_NAME")%></td>
		    <td class="en11px"><%=map.get("ACCOUNT_NAME")%></td>
		    <td class="en11px"><%=map.get("FORM_TYPE")%></td>
		    <td class="en11px"><%=map.get("PERIOD_NAME")%></td>
		    <td class="en11px"><%=map.get("MAN_HOUR")%></td>
		    <td class="en11px"><%=map.get("START_TIME")%></td>
		    <td class="en11px"><%=map.get("END_TIME")%></td>
		    </tr>
	     <%
	  	}
	  } %>
      </table>
      </fieldset></td>
  </tr>
</table>
