<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<title>IT-PMS 关键备件寿命提醒</title>
</head>
<style type="text/css">
td {background-color:#3B5998; font-size:14px; font-family:"宋体,Times New Roman, Times, serif"; color:#FFFFFF}
.td-text{background-color:#E9FAFE;color:#000000}
.td-text-b{background-color:#E9FAFE;color:#000000;font-weight:bold}
.td-other{background-color:#993366;font-size:14px; font-family:"宋体,Times New Roman, Times, serif"; color:#FFFFFF}
.td-other-text{background-color:#ffff99;color:#000000}
.td-other-text-b{background-color:#ffff99;color:#000000;font-weight:bold}
</style>
<body>
<span style="font-size:14px; font-family:"宋体,Times New Roman, Times, serif";">Dear ALL:<BR>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 如下关键备件使用寿命已超预警线，请及时处理！</span><br>
<br>
<table width="700" border="0" cellpadding="1" cellspacing="2">
	<#if (keyPartsWarnList?size > 0)>
	<tr>	
	   <td colspan="10">关键备件预警列表</td>
	</tr>
	<tr>
	   <td class="td">设备</td>
	   <td class="td">关键备件</td>
	   <td class="td">料号</td>
	   <td class="td">名称</td>
	   <td class="td">是否必换</td>
	   <td class="td">寿命类型</td>
	   <td class="td">实际使用寿命</td>
	   <td class="td">预警寿命</td>
	   <td class="td">超期寿命</td>
	   <td class="td">已延期寿命</td>
	</tr>
	 <#list keyPartsWarnList as sto>
	 <tr>
	   <td class="td-text-b" width="70">${sto["EQP_ID"]}</td>
	   <td class="td-text">${sto["KEYDESC"]}</td>
	   <td class="td-text">${sto["PARTS_ID"]}</td>
	   <td class="td-text">${sto["PARTS_NAME"]}</td>
	   <td class="td-text">${sto["MUSTCHANGE"]}</td>
	   <td class="td-text">${sto["LIMIT_TYPE"]}</td>
	   <td class="td-text">${sto["ACTUL"]}</td>
	   <td class="td-text">${sto["WARN_SPEC"]}</td>
	   <td class="td-text">${sto["ERROR_SPEC"]}</td>
	   <td class="td-text">${sto["DELAY_LIFE"]}</td>
	 </tr>
	 </#list>
  	</#if> 
</table>
<BR>
<table width="700" border="0" cellpadding="1" cellspacing="2">
	<#if (keyPartsErrorList?size > 0)>
	<tr>
	   <td colspan="10">关键备件超期列表</td>
	</tr>
	<tr>
	   <td class="td">设备</td>
	   <td class="td">关键备件</td>
	   <td class="td">料号</td>
	   <td class="td">名称</td>
	   <td class="td">是否必换</td>
	   <td class="td">寿命类型</td>
	   <td class="td">实际使用寿命</td>
	   <td class="td">预警寿命</td>
	   <td class="td">超期寿命</td>
	   <td class="td">已延期寿命</td>
	</tr>
	 <#list keyPartsErrorList as sto>
	 <tr>
	   <td class="td-text-b" width="70">${sto["EQP_ID"]}</td>
	   <td class="td-text">${sto["KEYDESC"]}</td>
	   <td class="td-text">${sto["PARTS_ID"]}</td>
	   <td class="td-text">${sto["PARTS_NAME"]}</td>
	   <td class="td-text">${sto["MUSTCHANGE"]}</td>
	   <td class="td-text">${sto["LIMIT_TYPE"]}</td>
	   <td class="td-text">${sto["ACTUL"]}</td>
	   <td class="td-text">${sto["WARN_SPEC"]}</td>
	   <td class="td-text">${sto["ERROR_SPEC"]}</td>
	   <td class="td-text">${sto["DELAY_LIFE"]}</td>
	 </tr>
	 </#list>
  	</#if> 
</table>
<BR>
<span  style="font-size:14px; font-family:"宋体,Times New Roman, Times, serif";">以上信息如有问题请联系IT</span>
</body>
</html>