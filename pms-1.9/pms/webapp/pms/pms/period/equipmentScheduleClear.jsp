<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<!--include yui css-->
<script language="javascript">
	function doSubmit(url)
	{
		var startDate = document.equipmentScheduleForm.startDate.value;
		var endDate = document.equipmentScheduleForm.endDate.value;
		if(startDate=="")
		{
			alert("开始时间不能为空");
			return;		
		}
		
		if(endDate=="")
		{
			alert("结束时间不能为空");
			return;
		}
		
		if(startDate>endDate) {
			alert("开始时间不能大于结束时间");
			return;
		}
		
		Ext.MessageBox.confirm('确认', '确定要删除该区间范围内的保养计划？',function result(value){
			if(value=="yes"){
				loading();
				document.equipmentScheduleForm.action = url;
				document.equipmentScheduleForm.submit();	
			} else {
				return;
			}
		});
		
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

	 });	
</script>
<form name="equipmentScheduleForm" method="POST" action="<%=request.getContextPath()%>/control/equipmentScheduleDefine">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <fieldset><legend>保养计划删除</legend>

      	<table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">
			<tr bgcolor='#DFE1EC' height="25">
				<td width="10%" class="en11pxb">删除区间</td>
				<td width="30%"><input type="text" ID="startDate" NAME="startDate"></td>
				<td width="8%">到:</td>
				<td width="37%"><input type="text" ID="endDate" NAME="endDate"></td>
			</tr>

			<tr bgcolor="#DFE1EC" height="25">
				<td class="en11pxb">部门</td>
				<td><input type="text" class="input" name="accountSection" value="<ofbiz:field attribute="accountSection"/>" ></td>
				<td></td>
		   	 	<td></td>
		    </tr>
		</table>
	  </fieldset>
	</td>
  <tr>
</table>
<table border="0" cellspacing="0" cellpadding="0">
<tr height="30"><td><ul class="button">
		<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/defaultPeriodClear')"><span>&nbsp;确定&nbsp;</span></a></li> 
		</ul>
	</td>
</tr>
</table>
</form>
