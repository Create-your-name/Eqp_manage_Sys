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
			alert("��ʼʱ�䲻��Ϊ��");
			return;		
		}
		
		if(endDate=="")
		{
			alert("����ʱ�䲻��Ϊ��");
			return;
		}
		
		if(startDate>endDate) {
			alert("��ʼʱ�䲻�ܴ��ڽ���ʱ��");
			return;
		}
		
		Ext.MessageBox.confirm('ȷ��', 'ȷ��Ҫɾ�������䷶Χ�ڵı����ƻ���',function result(value){
			if(value=="yes"){
				loading();
				document.equipmentScheduleForm.action = url;
				document.equipmentScheduleForm.submit();	
			} else {
				return;
			}
		});
		
	}
	
   //��ʼ��ҳ��ؼ�
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
      <fieldset><legend>�����ƻ�ɾ��</legend>

      	<table width="100%" border="0" cellspacing="1" cellpadding="2" class="en11pxb">
			<tr bgcolor='#DFE1EC' height="25">
				<td width="10%" class="en11pxb">ɾ������</td>
				<td width="30%"><input type="text" ID="startDate" NAME="startDate"></td>
				<td width="8%">��:</td>
				<td width="37%"><input type="text" ID="endDate" NAME="endDate"></td>
			</tr>

			<tr bgcolor="#DFE1EC" height="25">
				<td class="en11pxb">����</td>
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
		<li><a class="button-text" href="javascript:doSubmit('<%=request.getContextPath()%>/control/defaultPeriodClear')"><span>&nbsp;ȷ��&nbsp;</span></a></li> 
		</ul>
	</td>
</tr>
</table>
</form>
