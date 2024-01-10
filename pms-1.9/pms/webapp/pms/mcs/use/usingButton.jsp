<!-- 物料换上使用按钮 -->
<ofbiz:if name="mtrGrp">
<table border="0" cellspacing="0" cellpadding="0">
  <tr height="30">
    <td class="en11pxb">
        换上备注
        <input type="text" class="input" size="20" name="useNote" value="">
    </td>

    <td><ul class="button">
		<li><a class="button-text" href="javascript:changeMaterialStatus('<%=ConstantsMcs.USING%>', '<i18n:message key="mcs.status_using" />');">
		    <span>&nbsp;<i18n:message key="mcs.status_using" />&nbsp;</span>
		</a></li>
	</ul></td>
  </tr>
</table>
</ofbiz:if>

<br>
<p>
	<font color="#FF0000" face="黑体" size="-1">
		Tips :

		<br>
		&nbsp;1. 使用暂存区物料前，必须已维护可用设备。
		<a href="<ofbiz:url>/queryMaterialEntry?usingObjectId=<%=eqpId%></ofbiz:url>">
		    查询物料信息设定
		</a>

		<br>
		&nbsp;2. 暂存区物料列表，仅列出有效期内的物料。
		<a href="<ofbiz:url>/queryOverExpirationEntry</ofbiz:url>">
		    查询超有效期材料
		</a>
		，联系IQC修改有效期。
	</font>
</p>