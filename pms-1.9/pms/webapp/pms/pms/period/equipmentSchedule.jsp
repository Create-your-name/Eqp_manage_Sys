<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<!--include yui css-->
<script language="javascript">
	Ext.onReady(function(){
	    var equipment = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'equipmentId',
	        width:170,
	        forceSelection:true
	    });
	});

	function equipmentScheduleDefine()
	{
		if(Ext.getDom('equipmentId').value=='') {
			Ext.MessageBox.alert('警告', '请选择设备！');
			return;
		}
		loading();
		document.equipmentScheduleForm.submit();
	}
</script>
<form name="equipmentScheduleForm" method="POST" action="<%=request.getContextPath()%>/control/equipmentScheduleDefine">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><fieldset><legend>机台PM计划行事历设定</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC" height="30">
          <td width="10%" class="en11pxb">&nbsp;设&nbsp;备&nbsp;</td>
          <td>
         		<select id="equipmentId" name="equipmentId">
         			<option value=''></option>
	          		<ofbiz:if name="equipmentList">
		        		<ofbiz:iterator name="equipment" property="equipmentList">
			    			<option value='<ofbiz:inputvalue entityAttr="equipment" field="equipmentId"/>'><ofbiz:inputvalue entityAttr="equipment" field="equipmentId"/></option>
		      			</ofbiz:iterator>
	      			</ofbiz:if>
    			</select>
          </td>
        </tr>
      </table>
	 </fieldset>
	</td>
  <tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
	<tr height="30">
	 	<td><ul class="button">
			<li><a class="button-text" href="#" onclick="javascript:equipmentScheduleDefine();"><span>&nbsp;确定&nbsp;</span></a></li>
			</ul>
		</td>
	</tr>
</table>
</form>
