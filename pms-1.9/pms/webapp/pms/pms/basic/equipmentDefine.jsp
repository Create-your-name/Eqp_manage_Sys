<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.entity.GenericValue"%>
<%@ page import="org.ofbiz.base.util.UtilFormatOut"%>
<%@ page import="org.ofbiz.entity.GenericValue"%>

<%
	GenericValue equipment = (GenericValue) request.getAttribute("equipment");
	boolean addFlag = (equipment==null);
%>
<script language="javascript">
	function doSubmit(url)
	{
		if(Trim(Ext.getDom('equipmentId').value)=='') {
			Ext.MessageBox.alert('警告', '设备名不能为空!');
			return;
		}

		if(Trim(Ext.getDom('equipmentDesc').value)=='') {
			Ext.MessageBox.alert('警告', '设备描述不能为空!');
			return;
		}

		if(Trim(Ext.getDom('equipmentType').value)=='') {
			Ext.MessageBox.alert('警告', '设备大类不能为空!');
			return;
		}

		if(Trim(Ext.getDom('location').value)=='') {
			Ext.MessageBox.alert('警告', 'Location不能为空!');
			return;
		}

		if(Trim(Ext.getDom('equipmentGroup').value)=='') {
			Ext.MessageBox.alert('警告', 'EQ Group(Module)不能为空!');
			return;
		}

		if(Trim(Ext.getDom('section').value)=='') {
			Ext.MessageBox.alert('警告', '课别不能为空!');
			return;
		}

		if(Trim(Ext.getDom('maintDept').value)=='') {
			Ext.MessageBox.alert('警告', '维护部门不能为空!');
			return;
		}

		if(Trim(Ext.getDom('useDept').value)=='') {
			Ext.MessageBox.alert('警告', '使用部门不能为空!');
			return;
		}

		if(Trim(Ext.getDom('equipmentEngineer').value)=='') {
			Ext.MessageBox.alert('警告', '设备工程师不能为空!');
			return;
		}

		if(Trim(Ext.getDom('msa').value)=='Y'){
		    if(Trim(Ext.getDom('msaEmail').value)==''){
		        Ext.MessageBox.alert('警告', 'MSA维护责任人和部门邮件地址不能为空!');
		        return;
		    }
		    else{
		        var regex = /^(([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z])+)+([;.](([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z])+)+)*$/;
		        if(!regex.test(Trim(Ext.getDom('msaEmail').value))){
		            Ext.MessageBox.alert('警告','MSA维护责任人和部门邮件地址格式不正确!');
		            return;

		        }
		    }
		}

		if (Trim(Ext.getDom('equipmentType').value)=='菜单备份'){
		    if(Trim(Ext.getDom('recipeBackupEmail').value)==''){
		        Ext.MessageBox.alert('警告', '菜单备份责任人和部门邮件地址不能为空!');
		        return;
		    }
		    else{
		        var regex = /^(([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z])+)+([;.](([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z])+)+)*$/;
		        if(!regex.test(Trim(Ext.getDom('recipeBackupEmail').value))){
		            Ext.MessageBox.alert('警告','菜单备份责任人和部门邮件地址格式不正确!');
		            return;

		        }
		    }
		}

		Ext.MessageBox.confirm('保存或者删除确认', '修改或者删除会导致信息丢失!',function result(value){
    		if(value=="yes"){
        		loading();
        		document.eqpForm.action = url;
        		document.eqpForm.submit();
    		}else{
    			return;
    		}
	    });

	}

	function cancel(url) {
		loading();
		document.eqpForm.action = url;
		document.eqpForm.submit();
	}

	Ext.onReady(function(){
	    var equipmentType = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'equipmentType',
	        width:170,
	        forceSelection:true
	    });

	    equipmentType.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("equipmentType"))%>');

	   /* var ownDept = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'ownDept',
	        width:170,
	        forceSelection:true
	    });

	    ownDept.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("ownDept"))%>');*/

	    Ext.getDom('ownDept').value = '<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("ownDept"))%>';

	  /*  var maintDept = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'maintDept',
	        width:170,
	        forceSelection:true
	    });

	    maintDept.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("maintDept"))%>');*/

	    /*var useDept = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'useDept',
	        width:170,
	        forceSelection:true
	    });

	    useDept.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("useDept"))%>');*/

	    Ext.getDom('useDept').value = '<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("useDept"))%>';

	    var parentEqpid = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'parentEqpid',
	        width:170,
	        forceSelection:true
	    });

	    parentEqpid.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("parentEqpid"))%>');

	   /* var keyEqp = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'keyEqp',
	        width:170,
	        forceSelection:true
	    });

	    keyEqp.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("keyEqp"))%>');*/

	    Ext.getDom('keyEqp').value = '<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("keyEqp"))%>';

	   /* var adjustEqp = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'adjustEqp',
	        width:170,
	        forceSelection:true
	    });

	    adjustEqp.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("adjustEqp"))%>');*/

	    Ext.getDom('adjustEqp').value = '<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("adjustEqp"))%>';

	    /*var measureEqp = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'measureEqp',
	        width:170,
	        forceSelection:true
	    });



	   measureEqp.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("measureEqp"))%>');*/

		Ext.getDom('measureEqp').value = '<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("measureEqp"))%>';


	   /* var exhaust = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'exhaust',
	        width:170,
	        forceSelection:true
	    });

	    exhaust.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("exhaust"))%>');*/
	    Ext.getDom('exhaust').value = '<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("exhaust"))%>';

	    /*var meter = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'meter',
	        width:170,
	        forceSelection:true
	    });

	   meter.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("meter"))%>');*/
	   Ext.getDom('meter').value = '<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("meter"))%>';

	  /* var voltage = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'voltage',
	        width:170,
	        forceSelection:true
	    });

	    voltage.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("voltage"))%>');*/

	    Ext.getDom('voltage').value = '<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("voltage"))%>';

	    /*var power = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'power',
	        width:170,
	        forceSelection:true
	    });

	    power.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("power"))%>');*/
	    Ext.getDom('power').value = '<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("power"))%>';

		/*var section = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'section',
	        width:170,
	        forceSelection:true
	    });

	    section.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("section"))%>');*/
	    Ext.getDom('section').value = '<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("section"))%>';

	    Ext.getDom('msa').value = '<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("msa"))%>';

	    var location = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'location',
	        width:170,
	        forceSelection:true
	    });

	    location.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("location"))%>');

			var modelStore=new Ext.data.Store({
	        proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryModelByVendor</ofbiz:url>'}),
		    reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'model'}]))
	    });

			var vendorStore=new Ext.data.Store({
					proxy: new Ext.data.HttpProxy({url: '<ofbiz:url>/queryVendor</ofbiz:url>'}),
				reader: new Ext.data.JsonReader({}, Ext.data.Record.create([{name: 'vendor'}]))
			});

			var modelCombox =new Ext.form.ComboBox({
	    	store: modelStore,
				displayField:'model',
				valueField:'model',
				hiddenName:'model',
	    	typeAhead: true,
	    	mode: 'local',
				triggerAction: 'all',
				width:170
			});
	    modelCombox.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("model"))%>');
	    modelCombox.applyTo('model');

	    var vendorCombox =new Ext.form.ComboBox({
	    	store: vendorStore,
				displayField:'vendor',
				valueField:'vendor',
				hiddenName:'vendor',
	    	typeAhead: true,
				triggerAction: 'all',
				width:170
		  });
	    
	    vendorCombox.applyTo('vendor');
	    vendorCombox.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("vendor"))%>');
	    modelCombox.setValue('<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("model"))%>');
	    vendorCombox.on('select', loadModel);

			function loadModel() {
    		var  vendor= vendorCombox.getValue();
    		if(vendor != "") {
    			modelStore.load({params:{vendor:vendor},callback:function(){}});
    		}else{
    			modelCombox.reset(); modelStore.removeAll();
    		}
    	}
	});
</script>


<form name="eqpForm"  method="POST">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td valign="top"> <fieldset> <legend><%=addFlag?"新增":"修改"%> 设备基本信息</legend>
      <table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">
        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">设备名<font color="red">*</font></td>
          <td width="29%"><input name="equipmentId" type="text" class="input" value='<%=addFlag?request.getParameter("eqpid"):UtilFormatOut.checkNull(equipment.getString("equipmentId"))%>' <%=addFlag?"":"readonly"%>></td>
          <td width="18%" bgcolor="#ACD5C9">设备描述<font color="red">*</font></td>
          <td width="36%"><input name="equipmentDesc" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("equipmentDesc"))%>'></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">设备大类<font color="red">*</font></td>
          <td width="29%">
          	<!--<input name="equipmentType" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("equipmentType"))%>'>-->
          	<select id="equipmentType" name="equipmentType" class="select-short">
          		<option value=''></option>
	          	<ofbiz:if name="equipmentTypeList">
		        	<ofbiz:iterator name="equipmentType" property="equipmentTypeList">
			    		<option value='<ofbiz:inputvalue entityAttr="equipmentType" field="equipmentType"/>'><ofbiz:inputvalue entityAttr="equipmentType" field="equipmentType"/></option>
		      		</ofbiz:iterator>
	      		</ofbiz:if>
    		</select>
          </td>
          <td width="18%" bgcolor="#ACD5C9">机台型号(MODEL)</td>
          <td width="36%">  <input name="model" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("model"))%>'></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">Location<font color="red">*</font></td>
          <td width="29%">
            <!--<input name="location" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("location"))%>'>-->
          <% List locationList = (List)request.getAttribute("locationList");%>
          <select id="location" name="location" class="select-short">
          		<option value=''></option>
          		<% if(locationList != null) {
          			for(Iterator locationIt = locationList.iterator(); locationIt.hasNext();) {
          			Map map = (Map)locationIt.next();%>
			    <option value='<%=map.get("LOCATION")%>'><%=map.get("LOCATION")%></option>
			    <% }
			    } %>
	      	</select></td>
          <td width="18%" bgcolor="#ACD5C9">EQ Group(Module)<font color="red">*</font></td>
          <td width="36%">
            <input name="equipmentGroup" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("equipmentGroup"))%>'></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">课别<font color="red">*</font></td>
          <td width="29%">
          	<% List sectionList = (List)request.getAttribute("sectionList");%>
            <!--<input name="section" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("section"))%>'>-->
          	<select name="section" class="select-short">
          		<option value=''></option>
          		<% if(sectionList != null) {
          			for(Iterator sectionIt = sectionList.iterator(); sectionIt.hasNext();) {
          			Map map = (Map)sectionIt.next();%>
			    <option value='<%=map.get("SECTION")%>'><%=map.get("SECTION")%></option>
			    <% }
			    } %>
	      	</select>
	      </td>
          <td width="18%" bgcolor="#ACD5C9">维护部门<font color="red">*</font></td>
          <td width="36%">
            <!--<input name="maintDept" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("maintDept"))%>'>-->
     		<%
     		String maintDept = (String)request.getAttribute("maintDept");
     		maintDept = addFlag?maintDept:UtilFormatOut.checkNull(equipment.getString("maintDept"));
     		%>
     		<%=maintDept%>
     		<input name="maintDept" type="hidden" value="<%=maintDept%>" />
          </td>
        </tr>

        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">使用部门<font color="red">*</font></td>
          <td width="29%">
            <!--<input name="useDept" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("useDept"))%>'>-->
           <select id="useDept" name="useDept" class="select-short">
          		<option value=''></option>
	          	<ofbiz:if name="equipmentDeptList">
		        	<ofbiz:iterator name="equipmentDept" property="equipmentDeptList">
			    		<option value='<ofbiz:inputvalue entityAttr="equipmentDept" field="equipmentDept"/>'><ofbiz:inputvalue entityAttr="equipmentDept" field="equipmentDept"/></option>
		      		</ofbiz:iterator>
	      		</ofbiz:if>
	      	</select>
          </td>
          <td width="18%" bgcolor="#ACD5C9">拥有部门<font color="red">*</font></td>
          <td width="36%">
            <!--<input name="ownDept" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("ownDept"))%>'>-->
           <select id="ownDept" name="ownDept" class="select-short">
          		<option value=''></option>
	          	<ofbiz:if name="equipmentDeptList">
		        	<ofbiz:iterator name="equipmentDept" property="equipmentDeptList">
			    		<option value='<ofbiz:inputvalue entityAttr="equipmentDept" field="equipmentDept"/>'><ofbiz:inputvalue entityAttr="equipmentDept" field="equipmentDept"/></option>
		      		</ofbiz:iterator>
	      		</ofbiz:if>
	      	</select>
          </td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">财产编号</td>
          <td width="29%">
            <input name="assetNo" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("assetNo"))%>'></td>
          <td width="18%" bgcolor="#ACD5C9">专案代码</td>
          <td width="36%">
            <input name="projectCode" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("projectCode"))%>'></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">厂商</td>
          <td width="29%">
            <input name="vendor" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("vendor"))%>'></td>
          <td width="18%" bgcolor="#ACD5C9">厂商机台编号</td>
          <td width="36%">
            <input name="vendorEqpNo" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("vendorEqpNo"))%>'></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">厂商工程师</td>
          <td width="29%">
            <input name="vendorEngineer" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("vendorEngineer"))%>'></td>
          <td width="18%" bgcolor="#ACD5C9">厂商电话</td>
          <td width="36%">
            <input name="vendorPhone" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("vendorPhone"))%>'></td>
        </tr>

        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">设备工程师<font color="red">*</font></td>
          <td width="29%">
            <!-- <input name="equipmentEngineer" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("equipmentEngineer"))%>'> -->
          	<% List engineerList = (List)request.getAttribute("engineerList");%>
          	<select id="equipmentEngineer" name="equipmentEngineer" class="select-short" >
          		<option value=''></option>
          		<%
          			if(engineerList != null)
          			{
          				String cur_engineer = null;
          				if (!addFlag)
          				{
          					cur_engineer = equipment.getString("equipmentEngineer");
          				}
          				for(int i = 0, size = engineerList.size(); i < size; i = i + 1)
          				{
          					Map map = (Map)engineerList.get(i);
          					if (((String)map.get("ACCOUNT_NO")).equalsIgnoreCase(cur_engineer))
          					{
          		%>
			    <option value='<%=map.get("ACCOUNT_NO")%>' selected=true><%=map.get("ACCOUNT_NO")%> <%=map.get("ACCOUNT_NAME")%></option>
			    <%
			    			}
			    			else
			    			{
			    %>
			    <option value='<%=map.get("ACCOUNT_NO")%>'><%=map.get("ACCOUNT_NO")%> <%=map.get("ACCOUNT_NAME")%></option>
			    <%
			    			}
			    		}
			    	}
			    %>
	      	</select>
	      </td>
          <td width="18%" bgcolor="#ACD5C9">设备BACKUP工程师<font color="red">*</font></td>
          <td width="36%">
            <!-- <input name="backupEngineer" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("backupEngineer"))%>'> -->
          	<select id="backupEngineer" name="backupEngineer" class="select-short">
          		<option value=''></option>
          		<%
          			if(engineerList != null)
          			{
          				String cur_engineer = null;
          				if (!addFlag)
          				{
          					cur_engineer = equipment.getString("backupEngineer");
          				}
          				for(int i = 0, size = engineerList.size(); i < size; i = i + 1)
          				{
          					Map map = (Map)engineerList.get(i);
          					if (((String)map.get("ACCOUNT_NO")).equalsIgnoreCase(cur_engineer))
          					{
          		%>
			    <option value='<%=map.get("ACCOUNT_NO")%>' selected=true><%=map.get("ACCOUNT_NO")%> <%=map.get("ACCOUNT_NAME")%></option>
			    <%
			    			}
			    			else
			    			{
			    %>
			    <option value='<%=map.get("ACCOUNT_NO")%>'><%=map.get("ACCOUNT_NO")%> <%=map.get("ACCOUNT_NAME")%></option>
			    <%
			    			}
			    		}
			    	}
			    %>
	      	</select>
          </td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">设备LEADER<font color="red">*</font></td>
          <td width="29%">
            <!-- <input name="equipmentLeader" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("equipmentLeader"))%>'> -->
          	<select id="equipmentLeader" name="equipmentLeader" class="select-short">
          		<option value=''></option>
          		<%
          			if(engineerList != null)
          			{
          				String cur_engineer = null;
          				if (!addFlag)
          				{
          					cur_engineer = equipment.getString("equipmentLeader");
          				}
          				for(int i = 0, size = engineerList.size(); i < size; i = i + 1)
          				{
          					Map map = (Map)engineerList.get(i);
          					if (((String)map.get("ACCOUNT_NO")).equalsIgnoreCase(cur_engineer))
          					{
          		%>
			    <option value='<%=map.get("ACCOUNT_NO")%>' selected=true><%=map.get("ACCOUNT_NO")%> <%=map.get("ACCOUNT_NAME")%></option>
			    <%
			    			}
			    			else
			    			{
			    %>
			    <option value='<%=map.get("ACCOUNT_NO")%>'><%=map.get("ACCOUNT_NO")%> <%=map.get("ACCOUNT_NAME")%></option>
			    <%
			    			}
			    		}
			    	}
			    %>
	      	</select>
          </td>
          <td width="18%" bgcolor="#ACD5C9">工艺工程师</td>
          <td width="36%">
            <!-- <input name="processEngineer" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("processEngineer"))%>'> -->
            <% List processEngineerList = (List)request.getAttribute("processEngineerList");%>
          	<select id="processEngineer" name="processEngineer" class="select-short">
          		<option value=''></option>
          		<%
          			if(processEngineerList != null)
          			{
          				String cur_engineer = null;
          				if (!addFlag)
          				{
          					cur_engineer = equipment.getString("processEngineer");
          				}
          				for(int i = 0, size = processEngineerList.size(); i < size; i = i + 1)
          				{
          					Map map = (Map)processEngineerList.get(i);
          					if (((String)map.get("ACCOUNT_NO")).equalsIgnoreCase(cur_engineer))
          					{
          		%>
			    <option value='<%=map.get("ACCOUNT_NO")%>' selected=true><%=map.get("ACCOUNT_NO")%> <%=map.get("ACCOUNT_NAME")%></option>
			    <%
			    			}
			    			else
			    			{
			    %>
			    <option value='<%=map.get("ACCOUNT_NO")%>'><%=map.get("ACCOUNT_NO")%> <%=map.get("ACCOUNT_NAME")%></option>
			    <%
			    			}
			    		}
			    	}
			    %>
	      	</select>
          </td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">LENGTH</td>
          <td width="29%">
            <input name="length" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("length"))%>'></td>
          <td width="18%" bgcolor="#ACD5C9">WIDTH</td>
          <td width="36%">
            <input name="width" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("width"))%>'></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">HEIGHT</td>
          <td width="29%">
            <input name="height" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("height"))%>'></td>
          <td width="18%" bgcolor="#ACD5C9">WEIGHT</td>
          <td width="36%">
            <input name="weight" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("weight"))%>'></td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">排风</td>
          <td width="29%">
            <!--<input name="exhaust" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("exhaust"))%>'>-->
           <select id="exhaust" name="exhaust" class="select-short">
          		<option value=''></option>
	          	<ofbiz:if name="exhaustList">
		        	<ofbiz:iterator name="exhaust" property="exhaustList">
			    		<option value='<ofbiz:inputvalue entityAttr="exhaust" field="name"/>'><ofbiz:inputvalue entityAttr="exhaust" field="name"/></option>
		      		</ofbiz:iterator>
	      		</ofbiz:if>
	      	</select>
          </td>
          <td width="18%" bgcolor="#ACD5C9">相位 (∮)</td>
          <td width="36%">
            <!--<input name="meter" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("meter"))%>'>-->
           <select id="meter" name="meter" class="select-short">
          		<option value=''></option>
	          	<ofbiz:if name="meterList">
		        	<ofbiz:iterator name="meter" property="meterList">
			    		<option value='<ofbiz:inputvalue entityAttr="meter" field="name"/>'><ofbiz:inputvalue entityAttr="meter" field="name"/></option>
		      		</ofbiz:iterator>
	      		</ofbiz:if>
	      	</select>
          </td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">电压 (V)</td>
          <td width="29%">
            <!--<input name="voltage" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("voltage"))%>'>-->
           <select id="voltage" name="voltage" class="select-short">
          		<option value=''></option>
	          	<ofbiz:if name="voltageList">
		        	<ofbiz:iterator name="voltage" property="voltageList">
			    		<option value='<ofbiz:inputvalue entityAttr="voltage" field="name"/>'><ofbiz:inputvalue entityAttr="voltage" field="name"/></option>
		      		</ofbiz:iterator>
	      		</ofbiz:if>
	      	</select>
          </td>
          <td width="18%" bgcolor="#ACD5C9">功率 (KW)</td>
          <td width="36%">
            <!--<input name="power" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("power"))%>'>-->
           <select id="power" name="power" class="select-short">
          		<option value=''></option>
	          	<ofbiz:if name="powerList">
		        	<ofbiz:iterator name="power" property="powerList">
			    		<option value='<ofbiz:inputvalue entityAttr="power" field="name"/>'><ofbiz:inputvalue entityAttr="power0" field="name"/></option>
		      		</ofbiz:iterator>
	      		</ofbiz:if>
	      	</select>
          </td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">母设备</td>
          <td width="29%">
            <!--<input name="parentEqpid" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("parentEqpid"))%>'>-->
           <select id="parentEqpid" name="parentEqpid" class="select">
          		<option value=''></option>
	          	<ofbiz:if name="equipmentList">
		        	<ofbiz:iterator name="parentEquipment" property="equipmentList">
			    		<option value='<ofbiz:inputvalue entityAttr="parentEquipment" field="equipmentId"/>'><ofbiz:inputvalue entityAttr="parentEquipment" field="equipmentId"/></option>
		      		</ofbiz:iterator>
	      		</ofbiz:if>
	      	</select>
          </td>
          <td width="18%" bgcolor="#ACD5C9">关键设备</td>
          <td width="36%">
            <!--<input name="keyEqp" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("keyEqp"))%>'>-->
            <select id="keyEqp" name="keyEqp" class="select-short">
          		<option value=''></option>
				<option value='Y'>Y</option>
				<option value='N'>N</option>
	      	</select>
          </td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">标准设备</td>
          <td width="29%">
            <!--<input name="adjustEqp" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("adjustEqp"))%>'>-->
            <select id="adjustEqp" name="adjustEqp" class="select-short">
          		<option value=''></option>
				<option value='Y'>Y</option>
				<option value='N'>N</option>
	      	</select>
          </td>
          <td width="18%" bgcolor="#ACD5C9">计量设备</td>
          <td width="36%">
            <!--<input name="measureEqp" type="text" class="input" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("measureEqp"))%>'>-->
            <select id="measureEqp" name="measureEqp" class="select-short">
          		<option value=''></option>
				<option value='Y'>Y</option>
				<option value='N'>N</option>
	      	</select>
          </td>
        </tr>
        <tr bgcolor="#DFE1EC">
          <td width="17%" bgcolor="#ACD5C9">备注</td>
          <td width="29%">
            <input name="note" type="text" class="input" size="60" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("note"))%>'>
          </td>
          <td width="18%" bgcolor="#ACD5C9">MSA</td>
          <td width="36%">
              <select id="msa" name="msa" class="select-short">
                  <option value=''></option>
                  <option value='Y'>Y</option>
                  <option value='N'>N</option>
              </select>
          </td>
        </tr>

        <tr bgcolor="#DFE1EC">
            <td width="17%" bgcolor="#ACD5C9">MSA维护 责任人和部门邮件地址</td>
            <td width="29%" colspan="3">
                <input name="msaEmail" type="text" class="input" size="100" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("msaEmail"))%>'>
            (;分隔邮件地址)
            </td>
        </tr>

         <tr bgcolor="#DFE1EC">
            <td width="17%" bgcolor="#ACD5C9">菜单备份 责任人和部门邮件地址</td>
            <td width="29%" colspan="3">
                <input name="recipeBackupEmail" type="text" class="input" size="100" value='<%=addFlag?"":UtilFormatOut.checkNull(equipment.getString("recipeBackupEmail"))%>'>
            (;分隔邮件地址)
            </td>
        </tr>
      </table>
    </fieldset>
  </tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/equipmentDefineSave')"><span>&nbsp;<%=addFlag?"新增":"修改"%>&nbsp;</span></a></li>
	</ul></td>
	<% if(!addFlag) { %>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/equipmentDefineDelete')"><span>&nbsp;删除&nbsp;</span></a></li>
	</ul></td>
	<% } %>
    <td><ul class="button">
			<li><a class="button-text" href="javascript:cancel('<%=request.getContextPath()%>/control/equipmentDefineEntry')"><span>&nbsp;取消&nbsp;</span></a></li>
	</ul></td>
  </tr>
</table>
</form>
