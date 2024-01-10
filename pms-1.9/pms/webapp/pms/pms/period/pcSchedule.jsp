<%@page contentType='text/html; charset=gb2312'%>
<%@ taglib uri='ofbizTags' prefix='ofbiz' %>
<!--include yui css-->
<script language="javascript">
	function pcScheduleDefine()
	{
		if(Ext.getDom('styleIndex').value=='') {
			Ext.MessageBox.alert('警告', '请选择巡检样式！');
			return;
		}
		loading();
		document.pcScheduleForm.submit();
	}
	
	Ext.onReady(function(){
	    var styleIndex = new Ext.form.ComboBox({
	        typeAhead: true,
	        triggerAction: 'all',
	        transform:'styleIndex',
	        width:220,
	        forceSelection:true
	    });
	});	
</script>
<form name="pcScheduleForm" method="POST" action="<%=request.getContextPath()%>/control/pcScheduleDefine">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td><fieldset><legend>巡检日期设定</legend>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr bgcolor="#DFE1EC" height="30"> 
          <td width="12%" class="en11pxb">&nbsp;巡检样式</td>
          <td>          		
         		<select id="styleIndex" name="styleIndex">
         			<option value=''></option>
	          		<ofbiz:if name="pcStyleList">
		        		<ofbiz:iterator name="pcStyle" property="pcStyleList">
			    			<option value='<ofbiz:inputvalue entityAttr="pcStyle" field="styleIndex"/>'><ofbiz:inputvalue entityAttr="pcStyle" field="name"/></option>
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
		<li><a class="button-text" href="#" onclick="javascript:pcScheduleDefine();"><span>&nbsp;确定&nbsp;</span></a></li> 
		</ul>
	</td>
	</tr>
</table>
</form>
